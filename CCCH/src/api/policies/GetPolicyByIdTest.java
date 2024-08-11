package api.policies;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.ConnectionStatus;
import Constants.DestinationStatus;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import Constants.volume_type;
import InvokerServer.GatewayServer;
import InvokerServer.Org4SPOGServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGHypervisorsServer;
import InvokerServer.SPOGJobServer;
import InvokerServer.SPOGServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class GetPolicyByIdTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private SPOGDestinationServer spogDestinationServer;
	private Policy4SPOGServer policy4SPOGServer;

	private ExtentTest test;
	private TestOrgInfo ti;

	String[] type = new String[12];
	private String job_Type = "backup,restore";
	private String job_Method = "full,incremental,resync,full,incremental,resync,full,incremental,resync,full,incremental,resync";
	public String JobSeverity = "warning,information,critical,warning,information,critical,warning,information,critical,warning,information,critical";
	private String test_log_Message_1 = "testLogMessage", test_log_Message_2 = "connect_node_failed_test_message";
	public String JobStatus = "failed,failed,failed,incomplete,idle,waiting,crash,license_failed,backupjob_proc_exit,skipped,stop,active,finished";

	private String Direct_cloud_id;
	private String msp_cloud_account_id;
	private String root_cloud_id;
	private String destination_id_direct;
	private String destination_id_suborg;
	private String destination_id_rootsub;
	private String destination_id_submsp_sub;

	@BeforeClass
	@Parameters({ "baseURI", "port", "csrAdminUserName", "csrAdminPassword", "csrReadOnlyAdminName",
			"csrReadOnlyAdminPassword", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword,
			String readOnlyUserName, String readOnlyPassword, String logFolder, String runningMachine,
			String buildVersion) {

		spogServer = new SPOGServer(baseURI, port);
		policy4SPOGServer = new Policy4SPOGServer(baseURI, port);

		rep = ExtentManager.getInstance(this.getClass().getSimpleName(), logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Ramya.Nagepalli";

		Nooftest = 0;
		// Used for creating a build number with dateformat
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date = new java.util.Date();
		this.buildVersion = buildVersion + "_" + dateFormater.format(date);

		BQName = this.getClass().getSimpleName();
		this.runningMachine = runningMachine;
		System.out.println("The value of hit is " + count1.isstarttimehit());
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);

		if (count1.isstarttimehit() == 0) {
			System.out.println("into creation time");
			creationTime = System.currentTimeMillis();
			count1.setcreationtime(creationTime);

			// creationTime = System.currentTimeMillis();
			try {
				bqdb1.updateTable(BQName, this.runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0",
						String.valueOf(Nooftest), count1.getcreationtime(), "InProgress",
						author + " and Rest server is " + baseURI.split("//")[1]);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		ti = new TestOrgInfo(spogServer, test);

		// get cloud account for the direct organization
		Response response = spogServer.getCloudAccounts(ti.direct_org1_user1_token, "", test);
		Direct_cloud_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");

		// get cloud account for the msp organization
		response = spogServer.getCloudAccounts(ti.normal_msp_org1_user1_token, "", test);
		msp_cloud_account_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");

		// get cloud account for the root msp organization
		response = spogServer.getCloudAccounts(ti.root_msp_org1_user1_token, "", test);
		root_cloud_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");

		// get destinations
		response = spogDestinationServer.getDestinations(ti.direct_org1_user1_token, "", test);
		destination_id_direct = response.then().extract().path("data[" + 0 + "].destination_id");

		response = spogDestinationServer.getDestinations(ti.normal_msp1_suborg1_user1_token, "", test);
		destination_id_suborg = response.then().extract().path("data[" + 0 + "].destination_id");

		response = spogDestinationServer.getDestinations(ti.root_msp1_suborg1_user1_token, "", test);
		destination_id_rootsub = response.then().extract().path("data[" + 0 + "].destination_id");

		response = spogDestinationServer.getDestinations(ti.msp1_submsp1_suborg1_user1_token, "", test);
		destination_id_submsp_sub = response.then().extract().path("data[" + 0 + "].destination_id");

	}

	@DataProvider(name = "getPolicyById")
	public final Object[][] getPolicyById() {
		return new Object[][] {
				{ "Direct-cloud_direct_file_folder_backup", ti.direct_org1_id, ti.direct_org1_user1_token,
						destination_id_direct, Direct_cloud_id, UUID.randomUUID().toString(), "cloud_direct_baas",
						"cloud_direct_file_folder_backup", Direct_cloud_id, "daily" },
				{ "Direct-cloud_direct_image_backup", ti.direct_org1_id, ti.direct_org1_user1_token,
						destination_id_direct, Direct_cloud_id, UUID.randomUUID().toString(), "cloud_direct_baas",
						"cloud_direct_image_backup", Direct_cloud_id, "daily" },
				{ "Direct-cloud_direct_vmware_backup", ti.direct_org1_id, ti.direct_org1_user1_token,
						destination_id_direct, Direct_cloud_id, UUID.randomUUID().toString(), "cloud_direct_hypervisor",
						"cloud_direct_vmware_backup", Direct_cloud_id, "daily" },

				{ "Direct-cloud_direct_file_folder_backup", ti.direct_org1_id, ti.direct_org1_user1_token,
						destination_id_direct, Direct_cloud_id, UUID.randomUUID().toString(), "cloud_direct_baas",
						"cloud_direct_file_folder_backup", Direct_cloud_id, "weekly" },
				{ "Direct-cloud_direct_image_backup", ti.direct_org1_id, ti.direct_org1_user1_token,
						destination_id_direct, Direct_cloud_id, UUID.randomUUID().toString(), "cloud_direct_baas",
						"cloud_direct_image_backup", Direct_cloud_id, "weekly" },

				{ "SUB_ORG-cloud_direct_image_backup", ti.normal_msp1_suborg1_id, ti.normal_msp_org1_user1_token,
						destination_id_suborg, msp_cloud_account_id, UUID.randomUUID().toString(), "cloud_direct_baas",
						"cloud_direct_image_backup", msp_cloud_account_id, "daily" },
				{ "SUB_ORG-cloud_direct_file_folder_backup", ti.normal_msp1_suborg1_id, ti.normal_msp_org1_user1_token,
						destination_id_suborg, msp_cloud_account_id, UUID.randomUUID().toString(), "cloud_direct_baas",
						"cloud_direct_file_folder_backup", msp_cloud_account_id, "daily" },
				{ "SUB_ORG-cloud_direct_vmware_backup", ti.normal_msp1_suborg1_id, ti.normal_msp_org1_user1_token,
						destination_id_suborg, msp_cloud_account_id, UUID.randomUUID().toString(),
						"cloud_direct_hypervisor", "cloud_direct_vmware_backup", msp_cloud_account_id, "daily" },

				{ "SUB_ORG-cloud_direct_image_backup", ti.normal_msp1_suborg1_id, ti.normal_msp_org1_user1_token,
						destination_id_suborg, msp_cloud_account_id, UUID.randomUUID().toString(), "cloud_direct_baas",
						"cloud_direct_image_backup", msp_cloud_account_id, "weekly" },
				{ "SUB_ORG-cloud_direct_file_folder_backup", ti.normal_msp1_suborg1_id, ti.normal_msp_org1_user1_token,
						destination_id_suborg, msp_cloud_account_id, UUID.randomUUID().toString(), "cloud_direct_baas",
						"cloud_direct_file_folder_backup", msp_cloud_account_id, "weekly" },

				// 3 tier cases
				{ "rootsub-cloud_direct_image_backup", ti.root_msp1_suborg1_id, ti.root_msp_org1_user1_token,
						destination_id_rootsub, root_cloud_id, UUID.randomUUID().toString(), "cloud_direct_baas",
						"cloud_direct_image_backup", root_cloud_id, "daily" },
				{ "rootsub-cloud_direct_file_folder_backup", ti.root_msp1_suborg1_id, ti.root_msp_org1_user1_token,
						destination_id_rootsub, root_cloud_id, UUID.randomUUID().toString(), "cloud_direct_baas",
						"cloud_direct_file_folder_backup", root_cloud_id, "daily" },
				{ "rootsub-cloud_direct_vmware_backup", ti.root_msp1_suborg1_id, ti.root_msp_org1_user1_token,
						destination_id_rootsub, root_cloud_id, UUID.randomUUID().toString(), "cloud_direct_hypervisor",
						"cloud_direct_vmware_backup", root_cloud_id, "daily" },
				{ "rootsub-cloud_direct_image_backup", ti.root_msp1_suborg1_id, ti.root_msp_org1_user1_token,
						destination_id_rootsub, root_cloud_id, UUID.randomUUID().toString(), "cloud_direct_baas",
						"cloud_direct_image_backup", root_cloud_id, "weekly" },
				{ "rootsub-cloud_direct_file_folder_backup", ti.root_msp1_suborg1_id, ti.root_msp_org1_user1_token,
						destination_id_rootsub, root_cloud_id, UUID.randomUUID().toString(), "cloud_direct_baas",
						"cloud_direct_file_folder_backup", root_cloud_id, "weekly" },

				{ "submsp_sub-cloud_direct_image_backup", ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_token,
						destination_id_submsp_sub, root_cloud_id, UUID.randomUUID().toString(), "cloud_direct_baas",
						"cloud_direct_image_backup", root_cloud_id, "daily" },
				{ "submsp_sub-cloud_direct_file_folder_backup", ti.msp1_submsp1_sub_org1_id,
						ti.msp1_submsp1_suborg1_user1_token, destination_id_submsp_sub, root_cloud_id,
						UUID.randomUUID().toString(), "cloud_direct_baas", "cloud_direct_file_folder_backup",
						root_cloud_id, "daily" },
				{ "submsp_sub-cloud_direct_vmware_backup", ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_token,
						destination_id_submsp_sub, root_cloud_id, UUID.randomUUID().toString(),
						"cloud_direct_hypervisor", "cloud_direct_vmware_backup", root_cloud_id, "daily" },
				{ "submsp_sub-cloud_direct_image_backup", ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_token,
						destination_id_submsp_sub, root_cloud_id, UUID.randomUUID().toString(), "cloud_direct_baas",
						"cloud_direct_image_backup", root_cloud_id, "weekly" },
				{ "submsp_sub-cloud_direct_file_folder_backup", ti.msp1_submsp1_sub_org1_id,
						ti.msp1_submsp1_suborg1_user1_token, destination_id_submsp_sub, root_cloud_id,
						UUID.randomUUID().toString(), "cloud_direct_baas", "cloud_direct_file_folder_backup",
						root_cloud_id, "weekly" }, };
	}

	@Test(dataProvider = "getPolicyById",dependsOnMethods="deleteResources")
	public void getpolicybypolicyid(String organizationType, String organization_id, String validToken,
			String destination_id, String cloud_id, String policy_id, String policyType, String taskType,
			String site_id, String schedule) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		String source_id = null;

		ArrayList<HashMap<String, Object>> schedules = new ArrayList<>();

		String schedule_id = spogServer.returnRandomUUID();

		String task_id = spogServer.returnRandomUUID();

		String throttle_id = spogServer.returnRandomUUID();

		String throttle_type = "network";

		String policy_name = spogServer.ReturnRandom("test");

		String policy_description = spogServer.ReturnRandom("description");

		String resource_name = spogServer.ReturnRandom("Ramya") + "_";

		test.log(LogStatus.INFO, "The token is :" + validToken);

		test.log(LogStatus.INFO, "post the destination");

		String destination_name = RandomStringUtils.randomAlphanumeric(4) + "Ramya-test34";

		policy4SPOGServer.setToken(validToken);

		test.log(LogStatus.INFO, "Create schedule settings");

		HashMap<String, Object> scheduleSettingDTO = new HashMap<String, Object>();

		HashMap<String, Object> customScheduleSettingDTO = policy4SPOGServer.createCustomScheduleDTO("1522068700422",
				"full", "1", "true", "10", "minutes", test);

		test.log(LogStatus.INFO, "Create schedules");

		test.log(LogStatus.INFO, "Create cloud direct schedule");

		HashMap<String, Object> cloudDirectScheduleDTO = new HashMap<>();
		if (schedule.equals("daily")) {
			cloudDirectScheduleDTO = policy4SPOGServer.createCloudDirectScheduleDTO("0 6 * * *", test);
			test.log(LogStatus.INFO, "Create schedule settings");
			scheduleSettingDTO = policy4SPOGServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null, null, null,
					null, test);
			schedules = policy4SPOGServer.createPolicyScheduleDTO(null, schedule_id, "1d", task_id, destination_id,
					scheduleSettingDTO, "06:00", "12:00", taskType, destination_name, test);
		} else {
			cloudDirectScheduleDTO = policy4SPOGServer.createCloudDirectScheduleDTO("0 6 * * 1 *", test);
			test.log(LogStatus.INFO, "Create schedule settings");
			scheduleSettingDTO = policy4SPOGServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null, null, null,
					null, test);
			schedules = policy4SPOGServer.createPolicyScheduleDTO(null, schedule_id, "1w", task_id, destination_id,
					scheduleSettingDTO, "06:00", "12:00", taskType, destination_name, test);
		}

		test.log(LogStatus.INFO, "Exclude the folders from cloud direct file backup");

		ArrayList<HashMap<String, Object>> excludes = policy4SPOGServer.createExcludeInfoDTO(null, "path", "c:\\tmp",
				test);

		HashMap<String, Object> cloudDirectLocalBackupDTO = policy4SPOGServer.createCloudDirectLocalBackupDTO("d:\\tmp",
				"true", test);

		HashMap<String, Object> cloudDirectFileBackupTaskInfoDTO = policy4SPOGServer
				.createCloudDirectFileBackupTaskInfoDTO("d:\\tmp", cloudDirectLocalBackupDTO, excludes, test);

		ArrayList<String> drivers = new ArrayList<>();

		drivers.add("C");

		ArrayList<HashMap<String, Object>> destinations = new ArrayList<HashMap<String, Object>>();

		HashMap<String, Object> perform_ar_test = policy4SPOGServer.createPerformARTestOption("true", "true", "true",
				"true", test);

		HashMap<String, Object> retention_policy = policy4SPOGServer.createRetentionPolicyOption("2", "2", "2", "2",
				test);

		HashMap<String, Object> udp_replication_from_remote_DTO = policy4SPOGServer
				.createUdpReplicationFromRemoteInfoDTO(perform_ar_test, retention_policy, test);

		HashMap<String, Object> cloudDirectimageBackupTaskInfoDTO = policy4SPOGServer
				.createCloudDirectImageBackupTaskInfoDTO(drivers, cloudDirectLocalBackupDTO, test);
		
		spogServer.setToken(validToken);

		if (taskType.equals("cloud_direct_hypervisor") || taskType.equals("cloud_direct_file_folder_backup")) {

			source_id = spogServer.createSourceWithCheck(resource_name, SourceType.machine, SourceProduct.cloud_direct,
					organization_id, cloud_id, ProtectionStatus.unprotect, ConnectionStatus.online, "windows",
					"SQLSERVER", test);

			test.log(LogStatus.INFO, "Create task type and link it to the destination ");
			destinations = policy4SPOGServer.createPolicyTaskDTO(null, task_id, taskType, destination_id, "none", null,
					cloudDirectFileBackupTaskInfoDTO, null, test);

		} else if (taskType.equals("cloud_direct_image_backup")) {

			test.log(LogStatus.INFO, "Adding sources of type " + SourceType.machine + " to org " + organizationType);
			source_id = spogServer.createSourceWithCheck(resource_name, SourceType.machine, SourceProduct.cloud_direct,
					organization_id, site_id, ProtectionStatus.unprotect, ConnectionStatus.online, "windows",
					"SQLSERVER", test);

			test.log(LogStatus.INFO, "Create task type and link it to the destination ");

			destinations = policy4SPOGServer.createPolicyTaskDTO(null, task_id, taskType, destination_id, "none",
					cloudDirectimageBackupTaskInfoDTO, null, null, test);

		} else if (taskType.equalsIgnoreCase("cloud_direct_vmware_backup")) {
			String destination_type = "cloud_direct_volume";
			destinations = policy4SPOGServer.createPolicyTaskDTO(destination_name, null, task_id, taskType,
					destination_id, null, null, null, null, null, null, null, destination_type, test);

		} else {
			HashMap<String, Object> cloud_hybrid_store = new HashMap<String, Object>();
			cloud_hybrid_store = spogDestinationServer.composeCloudHybridInfo(4, true, 64, true);
			test.log(LogStatus.INFO, "Creating a destination of type cloud_hybrid_store");
			Response response = spogDestinationServer.createDestination(UUID.randomUUID().toString(), validToken,
					cloud_id, organization_id, site_id, UUID.randomUUID().toString(), "20", "cloud_hybrid_store",
					"running", destination_name, cloud_hybrid_store, test);
			destination_id = response.then().extract().path("data.destination_id");

			destinations = policy4SPOGServer.createPolicyTaskDTO(null, task_id, taskType, destination_id, null, null,
					null, udp_replication_from_remote_DTO, test);

			scheduleSettingDTO = policy4SPOGServer.createScheduleSettingDTO(null, customScheduleSettingDTO, null, null,
					null, test);

			schedules = policy4SPOGServer.createPolicyScheduleDTO(null, spogServer.returnRandomUUID(), "custom",
					task_id, destination_id, scheduleSettingDTO, "06:00", "12:00", taskType, destination_name, test);

		}
		test.log(LogStatus.INFO, "Create network throttle ");

		ArrayList<HashMap<String, Object>> throttles = policy4SPOGServer.createPolicyThrottleDTO(null, throttle_id,
				task_id, throttle_type, "1200", "1", "06:00", "18:00", taskType, destination_id, destination_name,
				test);

		test.log(LogStatus.INFO, "Create a policy of type cloud_direct_baas");

		Response response = null;

		if (taskType.equals("cloud_direct_vmware_backup")) {
			String hypervisor_id = UUID.randomUUID().toString();
			response = policy4SPOGServer.createPolicy(policy_name, policy_description, policyType, "true", source_id,
					destinations, schedules, null, policy_id, organization_id, hypervisor_id, test);
		} else {
			response = policy4SPOGServer.createPolicy(policy_name, policy_description, policyType, null, "true",
					source_id, destinations, schedules, throttles, policy_id, organization_id, test);
		}

		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		policy4SPOGServer.checkPolicyDestinations(response, SpogConstants.SUCCESS_POST, destinations, test);
		policy4SPOGServer.checkPolicyCommon(response, SpogConstants.SUCCESS_POST, policy_name, policy_description,
				policyType, null, "true", "success", source_id, policy_id, organization_id, test);
		policy4SPOGServer.checkPolicySchedules(response, SpogConstants.SUCCESS_POST, schedules, test);

		test.log(LogStatus.INFO, "update the policy by modifying configuration in the schedule");
		if (!taskType.equals("cloud_direct_vmware_backup")) {

			if (schedule.equals("daily")) {
				cloudDirectScheduleDTO = policy4SPOGServer.createCloudDirectScheduleDTO("0 6 * * *", test);
				test.log(LogStatus.INFO, "Create schedule settings");
				scheduleSettingDTO = policy4SPOGServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null, null,
						null, null, test);
				schedules = policy4SPOGServer.createPolicyScheduleDTO(null, schedule_id, "1d", task_id, destination_id,
						scheduleSettingDTO, "06:00", "12:00", taskType, destination_name, test);
			} else {
				cloudDirectScheduleDTO = policy4SPOGServer.createCloudDirectScheduleDTO("0 6 * * 1 *", test);
				test.log(LogStatus.INFO, "Create schedule settings");
				scheduleSettingDTO = policy4SPOGServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null, null,
						null, null, test);
				schedules = policy4SPOGServer.createPolicyScheduleDTO(null, schedule_id, "1w", task_id, destination_id,
						scheduleSettingDTO, "06:00", "12:00", taskType, destination_name, test);
			}

			response = policy4SPOGServer.updatePolicy(policy_name, policy_description, policyType, null, "true",
					source_id, destinations, schedules, throttles, policy_id, organization_id, validToken, test);
		}

		test.log(LogStatus.INFO, "get the policy by policy Id");
		String Policy_id = response.then().extract().path("data.policy_id");
		test.log(LogStatus.INFO, "get policy by id with valid token");
		response = policy4SPOGServer.getPolicyById(validToken, Policy_id, policy_name, policy_description, policyType,
				null, "true", source_id, organization_id, destinations, throttles, schedules,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		test.log(LogStatus.INFO, "get policy by id with csr read only valid token");
		response = policy4SPOGServer.getPolicyById(ti.csr_readonly_token, Policy_id, policy_name, policy_description,
				policyType, null, "true", source_id, organization_id, destinations, throttles, schedules,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		policy4SPOGServer.checkPolicyCommon(response, SpogConstants.SUCCESS_GET_PUT_DELETE, policy_name,
				policy_description, policyType, null, "true", "success", source_id, policy_id, organization_id, test);

		test.log(LogStatus.INFO, "deleting the created sources in the organization with valid token");
		spogServer.deleteSourcesById(validToken, source_id, test);
	}

	@DataProvider(name = "getPolicyById_401")
	public final Object[][] getPolicyById_401() {
		return new Object[][] { { "Direct", ti.direct_org1_id, ti.direct_org1_user1_token, destination_id_direct,
				Direct_cloud_id, UUID.randomUUID().toString(), "cloud_direct_baas", "cloud_direct_file_folder_backup",
				Direct_cloud_id }, };
	}

	@Test(dataProvider = "getPolicyById_401")
	public void getjobsbyjobid_401(String organizationType, String organization_id, String validToken,
			String destination_id, String cloud_id, String policy_id, String policyType, String taskType,
			String site_id) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		test.log(LogStatus.INFO, "get the policy by policy Id");

		String Policy_id = UUID.randomUUID().toString();

		String Invalid_Token = "Abc";

		String Missing_Token = "";

		test.log(LogStatus.INFO, "get the policy details using policy id with invalid Token ");

		ArrayList<HashMap<String, Object>> destinations = null;
		ArrayList<HashMap<String, Object>> throttles = null;
		ArrayList<HashMap<String, Object>> schedules = null;
		Response response = policy4SPOGServer.getPolicyById(Invalid_Token, Policy_id, "", "", policyType, null, "true",
				UUID.randomUUID().toString(), organization_id, destinations, throttles, schedules,
				SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);

		test.log(LogStatus.INFO, "get the policy details using policy id with Missing Token ");

		response = policy4SPOGServer.getPolicyById(Missing_Token, Policy_id, "", "", policyType, null, "true",
				UUID.randomUUID().toString(), organization_id, destinations, throttles, schedules,
				SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);

	}

	@Test(dataProvider = "getPolicyById_401")
	public void getjobsbyjobid_400_Invalid_Random_PolicyId(String organizationType, String organization_id,
			String validToken, String destination_id, String cloud_id, String policy_id, String policyType,
			String taskType, String site_id) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		String source_id = null;

		ArrayList<HashMap<String, Object>> schedules = new ArrayList<>();

		String schedule_id = spogServer.returnRandomUUID();

		String task_id = spogServer.returnRandomUUID();

		String throttle_id = spogServer.returnRandomUUID();

		String throttle_type = "network";

		String policy_name = spogServer.ReturnRandom("test");

		String policy_description = spogServer.ReturnRandom("description");

		String resource_name = spogServer.ReturnRandom("Ramya") + "_";

		test.log(LogStatus.INFO, "The token is :" + validToken);

		test.log(LogStatus.INFO, "post the destination");

		String destination_name = RandomStringUtils.randomAlphanumeric(4) + "Ramya-test34";

		policy4SPOGServer.setToken(validToken);

		test.log(LogStatus.INFO, "get the policy by policy Id");

		String Policy_id = "Abc";

		test.log(LogStatus.INFO, "get the policy details with invalid Policy Id ");

		Policy_id = UUID.randomUUID().toString();

		test.log(LogStatus.INFO, "get the policy details using policy id as Random  ");

		ArrayList<HashMap<String, Object>> destinations = null;
		ArrayList<HashMap<String, Object>> throttles = null;
		Response response = policy4SPOGServer.getPolicyById(validToken, Policy_id, policy_name, policy_description,
				policyType, null, "true", source_id, organization_id, destinations, throttles, schedules,
				SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.POLICY_ID_NOT_FOUND, test);

	}

	@DataProvider(name = "getPolicyById1")
	public final Object[][] getPolicyById1() {
		return new Object[][] {
				{ "direct-msp", ti.direct_org1_id, ti.direct_org1_user1_token, ti.normal_msp_org1_user1_token,
						destination_id_direct, Direct_cloud_id, UUID.randomUUID().toString(), "cloud_direct_baas",
						"cloud_direct_image_backup", Direct_cloud_id },
				{ "direct-subOrg", ti.direct_org1_id, ti.direct_org1_user1_token, ti.normal_msp1_suborg1_user1_token,
						destination_id_direct, Direct_cloud_id, UUID.randomUUID().toString(), "cloud_direct_baas",
						"cloud_direct_image_backup", null },
				{ "direct-other_direct", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org2_user1_token,
						destination_id_direct, Direct_cloud_id, UUID.randomUUID().toString(), "cloud_direct_baas",
						"cloud_direct_image_backup", null }, };
	}

	@Test(dataProvider = "getPolicyById1")
	public void getpolicyBypolicyId_403_Insufficient_permissions(String organizationType, String organization_id,
			String validToken, String inValidToken, String destination_id, String cloud_id, String policy_id,
			String policyType, String taskType, String site_id) {
		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		String source_id = null;
		spogServer.setToken(validToken);

		ArrayList<HashMap<String, Object>> schedules = new ArrayList<>();

		String schedule_id = spogServer.returnRandomUUID();

		String task_id = spogServer.returnRandomUUID();

		String throttle_id = spogServer.returnRandomUUID();

		String throttle_type = "network";

		String policy_name = spogServer.ReturnRandom("test");

		String policy_description = spogServer.ReturnRandom("description");

		String resource_name = spogServer.ReturnRandom("Ramya") + "_";

		test.log(LogStatus.INFO, "The token is :" + validToken);

		test.log(LogStatus.INFO, "post the destination");

		String destination_name = RandomStringUtils.randomAlphanumeric(4) + "Ramya-test34";

		policy4SPOGServer.setToken(validToken);

		test.log(LogStatus.INFO, "Create schedule settings");

		HashMap<String, Object> scheduleSettingDTO = new HashMap<String, Object>();

		HashMap<String, Object> customScheduleSettingDTO = policy4SPOGServer.createCustomScheduleDTO("1522068700422",
				"full", "1", "true", "10", "minutes", test);

		test.log(LogStatus.INFO, "Create schedules");

		test.log(LogStatus.INFO, "Create cloud direct schedule");

		HashMap<String, Object> cloudDirectScheduleDTO = policy4SPOGServer.createCloudDirectScheduleDTO("0 6 * * *",
				test);

		test.log(LogStatus.INFO, "Create schedule settings");
		scheduleSettingDTO = policy4SPOGServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null, null, null, null,
				test);

		schedules = policy4SPOGServer.createPolicyScheduleDTO(null, schedule_id, "1d", task_id, destination_id,
				scheduleSettingDTO, "06:00", "12:00", taskType, destination_name, test);

		test.log(LogStatus.INFO, "Exclude the folders from cloud direct file backup");

		ArrayList<HashMap<String, Object>> excludes = policy4SPOGServer.createExcludeInfoDTO(null, "path", "c:\\tmp",
				test);

		HashMap<String, Object> cloudDirectLocalBackupDTO = policy4SPOGServer.createCloudDirectLocalBackupDTO("d:\\tmp",
				"true", test);

		HashMap<String, Object> cloudDirectFileBackupTaskInfoDTO = policy4SPOGServer
				.createCloudDirectFileBackupTaskInfoDTO("d:\\tmp", cloudDirectLocalBackupDTO, excludes, test);

		ArrayList<String> drivers = new ArrayList<>();

		drivers.add("C");

		ArrayList<HashMap<String, Object>> destinations = new ArrayList<HashMap<String, Object>>();

		HashMap<String, Object> perform_ar_test = policy4SPOGServer.createPerformARTestOption("true", "true", "true",
				"true", test);

		HashMap<String, Object> retention_policy = policy4SPOGServer.createRetentionPolicyOption("2", "2", "2", "2",
				test);

		HashMap<String, Object> udp_replication_from_remote_DTO = policy4SPOGServer
				.createUdpReplicationFromRemoteInfoDTO(perform_ar_test, retention_policy, test);

		HashMap<String, Object> cloudDirectimageBackupTaskInfoDTO = policy4SPOGServer
				.createCloudDirectImageBackupTaskInfoDTO(drivers, cloudDirectLocalBackupDTO, test);

		if (taskType.equals("cloud_direct_hypervisor") || taskType.equals("cloud_direct_file_folder_backup")) {

			test.log(LogStatus.INFO, "Adding sources of type " + SourceType.machine + " to org " + organizationType);
			source_id = spogServer.createSourceWithCheck(resource_name, SourceType.machine, SourceProduct.cloud_direct,
					organization_id, site_id, ProtectionStatus.unprotect, ConnectionStatus.online, "windows",
					"SQLSERVER", test);

			test.log(LogStatus.INFO, "Create task type and link it to the destination ");

			destinations = policy4SPOGServer.createPolicyTaskDTO(null, task_id, taskType, destination_id, "none", null,
					cloudDirectFileBackupTaskInfoDTO, null, test);

		} else if (taskType.equals("cloud_direct_image_backup")) {

			test.log(LogStatus.INFO, "Adding sources of type " + SourceType.machine + " to org " + organizationType);

			source_id = spogServer.createSourceWithCheck(resource_name, SourceType.machine, SourceProduct.cloud_direct,
					organization_id, site_id, ProtectionStatus.unprotect, ConnectionStatus.online, "windows",
					"SQLSERVER", test);

			test.log(LogStatus.INFO, "Create task type and link it to the destination ");

			destinations = policy4SPOGServer.createPolicyTaskDTO(null, task_id, taskType, destination_id, "none",
					cloudDirectimageBackupTaskInfoDTO, null, null, test);

		} else {
			HashMap<String, Object> cloud_hybrid_store = new HashMap<String, Object>();
			cloud_hybrid_store = spogDestinationServer.composeCloudHybridInfo(4, true, 64, true);
			test.log(LogStatus.INFO, "Creating a destination of type cloud_hybrid_store");
			Response response = spogDestinationServer.createDestination(UUID.randomUUID().toString(), validToken,
					cloud_id, organization_id, site_id, UUID.randomUUID().toString(), "20", "cloud_hybrid_store",
					"running", destination_name, cloud_hybrid_store, test);
			destination_id = response.then().extract().path("data.destination_id");

			destinations = policy4SPOGServer.createPolicyTaskDTO(null, task_id, taskType, destination_id, null, null,
					null, udp_replication_from_remote_DTO, test);

			scheduleSettingDTO = policy4SPOGServer.createScheduleSettingDTO(null, customScheduleSettingDTO, null, null,
					null, test);

			schedules = policy4SPOGServer.createPolicyScheduleDTO(null, spogServer.returnRandomUUID(), "custom",
					task_id, destination_id, scheduleSettingDTO, "06:00", "12:00", taskType, destination_name, test);

		}
		test.log(LogStatus.INFO, "Create network throttle ");

		ArrayList<HashMap<String, Object>> throttles = policy4SPOGServer.createPolicyThrottleDTO(null, throttle_id,
				task_id, throttle_type, "1200", "1", "06:00", "18:00", taskType, destination_id, destination_name,
				test);

		test.log(LogStatus.INFO, "Create a policy of type cloud_direct_baas");

		Response response = policy4SPOGServer.createPolicy(policy_name, policy_description, policyType, null, "true",
				source_id, destinations, schedules, throttles, policy_id, organization_id, test);

		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);

		// policy4SPOGServer.checkPolicyDestinations(response,
		// SpogConstants.SUCCESS_POST, destinations, test);

		policy4SPOGServer.checkPolicyCommon(response, SpogConstants.SUCCESS_POST, policy_name, policy_description,
				policyType, null, "true", "success", source_id, policy_id, organization_id, test);

		policy4SPOGServer.checkPolicySchedules(response, SpogConstants.SUCCESS_POST, schedules, test);

		test.log(LogStatus.INFO, "get the policy by policy Id with other organization token");

		String Policy_id = response.then().extract().path("data.policy_id");
		test.log(LogStatus.INFO, "get the policy by id in the organization with valid token");
		response = policy4SPOGServer.getPolicyById(inValidToken, Policy_id, policy_name, policy_description, policyType,
				null, "true", source_id, organization_id, destinations, throttles, schedules,
				SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY, test);

		test.log(LogStatus.INFO, "get the policy by id in the organization with csr read only valid token");
		response = policy4SPOGServer.getPolicyById(ti.csr_readonly_token, UUID.randomUUID().toString(), policy_name,
				policy_description, policyType, null, "true", source_id, organization_id, destinations, throttles,
				schedules, SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.UNABLE_TO_FIND_POLICY_ID, test);

		test.log(LogStatus.INFO, "deleteSourcesById with valid token");
		spogServer.deleteSourcesById(validToken, source_id, test);

	}
	

	@DataProvider(name = "org_info")
	public final Object[][] org_info() {
		return new Object[][] {

				{ ti.direct_org1_id, ti.direct_org1_user1_token },
				{ ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token },
				{ ti.root_msp_org1_id, ti.root_msp_org1_user1_token },
				{ ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_token },
				{ ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_user1_token },
				{ ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_token },

		};
	}

	@Test(dataProvider = "org_info")
	public void deleteResources(String org_id, String validToken) {

		policy4SPOGServer.setToken(validToken);
		Response response = policy4SPOGServer.getPolicies(null);
		ArrayList<String> policies = new ArrayList<>();
		policies = response.then().extract().path("data.policy_id");

		if (!policies.isEmpty()) {
			policies.stream().forEach(policy -> {
				policy4SPOGServer.deletePolicybyPolicyId(ti.csr_token, policy);
			});
		}

		spogServer.setToken(validToken);
		response = spogServer.getSources("", "", 1, 20, true, test);

		ArrayList<String> sources = new ArrayList<>();
		sources = response.then().extract().path("data.source_id");
		if (!sources.isEmpty()) {
			sources.stream().forEach(source -> {
				spogServer.deleteSourceByID(source, test);
			});
		}

	}

	@AfterMethod
	public void getResult(ITestResult result) {
		if (result.getStatus() == ITestResult.FAILURE) {
			count1.setfailedcount();
			test.log(LogStatus.FAIL, "Test Case Failed is " + result.getName() + " with parameters as "
					+ Arrays.asList(result.getParameters()));
			test.log(LogStatus.FAIL, result.getThrowable().getMessage());

		} else if (result.getStatus() == ITestResult.SKIP) {
			test.log(LogStatus.SKIP, "Test Case Skipped is " + result.getName());
			count1.setskippedcount();
		} else if (result.getStatus() == ITestResult.SUCCESS) {
			count1.setpassedcount();

		}
		rep.endTest(test);
		rep.flush();
	}

}
