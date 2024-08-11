package api.policies;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
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
import InvokerServer.SPOGReportServer;
import InvokerServer.SPOGServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class DeployPolicyByPolicyIdTest extends base.prepare.Is4Org {

	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private SPOGJobServer spogJobServer;
	private SPOGDestinationServer spogDestinationServer;
	private SPOGReportServer spogreportServer;
	private Policy4SPOGServer policy4SpogServer;

	private ExtentTest test;
	private TestOrgInfo ti;

	String[] type = new String[12];
	private String job_Type = "backup";
	private String job_Status = "finished";
	private String job_Method = "full,incremental,resync,full,incremental,resync,full,incremental,resync,full,incremental,resync";
	public String JobSeverity = "warning,information,critical,warning,information,critical,warning,information,critical,warning,information,critical";
	public String JobStatus = "canceled,failed,finished,active,canceled,idle,waiting,crash,license_failed,backupjob_proc_exit,skipped,stop,active,finished";

	private String Direct_cloud_id;
	private String msp_cloud_account_id;
	private String root_cloud_id;
	private String destination_id_direct;
	private String destination_id_suborg;
	private String destination_id_rootsub;
	private String destination_id_submsp_sub;

	@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runningMachine, String buildVersion) {

		spogServer = new SPOGServer(baseURI, port);
		spogDestinationServer=new SPOGDestinationServer(baseURI, port);
		policy4SpogServer=new Policy4SPOGServer(baseURI, port);
		rep = ExtentManager.getInstance(this.getClass().getSimpleName(), logFolder);
		Nooftest = 0;
		bqdb1 = new SQLServerDb();
		String author = "Ramya.Nagepalli";
		count1 = new testcasescount();
		test = rep.startTest("beforeClass");
		test.assignAuthor("Ramya");
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date = new java.util.Date();
		this.buildVersion = buildVersion + "_" + dateFormater.format(date);
		this.BQName = this.getClass().getSimpleName();
		this.runningMachine = runningMachine;
		System.out.println("The value of hit is " + count1.isstarttimehit());
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

	/**
	 * Added cases only for policy_type="cloud_direct_baas", for positive scenarios
	 * 
	 * "cloud_direct_draas" is not supported to create policy through API
	 * "cloud_direct_hypervisor" is not supported through API to get sources, since
	 * not adding real hypervisor to the org "cloud_hybrid_replication" is not
	 * supported policy type to get the sources, added only negative scenario
	 * 
	 * @return
	 */

	@DataProvider(name = "policy_info")
	public final Object[][] policy_info() {
		return new Object[][] {
				{ "Direct-cloud_direct_file_folder_backup", ti.direct_org1_id, ti.direct_org1_user1_token,
						destination_id_direct, Direct_cloud_id, UUID.randomUUID().toString(), "cloud_direct_baas",
						"cloud_direct_file_folder_backup", null },
				{ "Direct-cloud_direct_image_backup", ti.direct_org1_id, ti.direct_org1_user1_token,
						destination_id_direct, Direct_cloud_id, UUID.randomUUID().toString(), "cloud_direct_baas",
						"cloud_direct_image_backup", null },
				{ "SUB_ORG-cloud_direct_image_backup", ti.normal_msp1_suborg1_id, ti.normal_msp_org1_user1_token,
						destination_id_suborg, msp_cloud_account_id, UUID.randomUUID().toString(), "cloud_direct_baas",
						"cloud_direct_image_backup", msp_cloud_account_id },
				{ "SUB_ORG-cloud_direct_file_folder_backup", ti.normal_msp1_suborg1_id, ti.normal_msp_org1_user1_token,
						destination_id_suborg, msp_cloud_account_id, UUID.randomUUID().toString(), "cloud_direct_baas",
						"cloud_direct_file_folder_backup", msp_cloud_account_id },
				{ "Root_sub-cloud_direct_image_backup", ti.root_msp1_suborg1_id, ti.root_msp_org1_user1_token,
						destination_id_rootsub, root_cloud_id, UUID.randomUUID().toString(), "cloud_direct_baas",
						"cloud_direct_image_backup", root_cloud_id },
				{ "Root_sub-cloud_direct_file_folder_backup", ti.root_msp1_suborg1_id, ti.root_msp_org1_user1_token,
						destination_id_rootsub, root_cloud_id, UUID.randomUUID().toString(), "cloud_direct_baas",
						"cloud_direct_file_folder_backup", root_cloud_id },

				{ "submsp_sub-cloud_direct_image_backup", ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_token,
						destination_id_submsp_sub, root_cloud_id, UUID.randomUUID().toString(), "cloud_direct_baas",
						"cloud_direct_image_backup", root_cloud_id },
				{ "submsp_sub-cloud_direct_file_folder_backup", ti.msp1_submsp1_sub_org1_id,
						ti.msp1_submsp1_suborg1_user1_token, destination_id_submsp_sub, root_cloud_id,
						UUID.randomUUID().toString(), "cloud_direct_baas", "cloud_direct_file_folder_backup",
						root_cloud_id },

		};
	}

	@Test(dataProvider = "policy_info", dependsOnMethods = "deleteResources")
	public void PostDeployPolicyById(String organizationType, String organization_id, String validToken,
			String dataStore_id, String cloud_id, String policy_id, String policyType, String taskType,
			String site_id) {

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

		policy4SpogServer.setToken(validToken);
		spogServer.setToken(validToken);

		test.log(LogStatus.INFO, "Create schedule settings");

		HashMap<String, Object> scheduleSettingDTO = new HashMap<String, Object>();

		HashMap<String, Object> customScheduleSettingDTO = policy4SpogServer.createCustomScheduleDTO("1522068700422",
				"full", "1", "true", "10", "minutes", test);

		test.log(LogStatus.INFO, "Create schedules");

		test.log(LogStatus.INFO, "Create cloud direct schedule");

		HashMap<String, Object> cloudDirectScheduleDTO = policy4SpogServer.createCloudDirectScheduleDTO("0 6 * * *",
				test);

		test.log(LogStatus.INFO, "Create schedule settings");
		scheduleSettingDTO = policy4SpogServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null, null, null, null,
				test);

		schedules = policy4SpogServer.createPolicyScheduleDTO(null, schedule_id, "1d", task_id, dataStore_id,
				scheduleSettingDTO, "06:00", "12:00", taskType, destination_name, test);

		test.log(LogStatus.INFO, "Exclude the folders from cloud direct file backup");

		ArrayList<HashMap<String, Object>> excludes = policy4SpogServer.createExcludeInfoDTO(null, "path", "c:\\tmp",
				test);

		HashMap<String, Object> cloudDirectLocalBackupDTO = policy4SpogServer.createCloudDirectLocalBackupDTO("d:\\tmp",
				"true", test);

		HashMap<String, Object> cloudDirectFileBackupTaskInfoDTO = policy4SpogServer
				.createCloudDirectFileBackupTaskInfoDTO("d:\\tmp", cloudDirectLocalBackupDTO, excludes, test);

		ArrayList<String> drivers = new ArrayList<>();

		drivers.add("C");

		ArrayList<HashMap<String, Object>> destinations = new ArrayList<HashMap<String, Object>>();

		HashMap<String, Object> perform_ar_test = policy4SpogServer.createPerformARTestOption("true", "true", "true",
				"true", test);

		HashMap<String, Object> retention_policy = policy4SpogServer.createRetentionPolicyOption("2", "2", "2", "2",
				test);

		HashMap<String, Object> udp_replication_from_remote_DTO = policy4SpogServer
				.createUdpReplicationFromRemoteInfoDTO(perform_ar_test, retention_policy, test);

		HashMap<String, Object> cloudDirectimageBackupTaskInfoDTO = policy4SpogServer
				.createCloudDirectImageBackupTaskInfoDTO(drivers, cloudDirectLocalBackupDTO, test);

		if (taskType == "cloud_direct_hypervisor" || taskType == "cloud_direct_file_folder_backup") {

			test.log(LogStatus.INFO, "Adding sources of type " + SourceType.machine + " to org " + organizationType);

			source_id = spogServer.createSourceWithCheck(resource_name, SourceType.machine, SourceProduct.cloud_direct,
					organization_id, site_id, ProtectionStatus.unprotect, ConnectionStatus.online, "windows",
					"SQLSERVER", test);

			String[] arrayofsourcenodes = new String[1];
			arrayofsourcenodes[0] = source_id;
			// CREATED SOURCE TO THE GROUP
			String group_name = "ramya";
			test.log(LogStatus.INFO, "Create a source group");
			/*String group_id = spogServer.createGroupWithCheck(organization_id, group_name, "add sources", test);
			spogServer.setToken(validToken);
			// add sourcs to the sourcegroup
			test.log(LogStatus.INFO, "Add the sources to the sourcegroup " + group_id);
			spogServer.addSourcetoSourceGroupwithCheck(group_id, arrayofsourcenodes, validToken,
					SpogConstants.SUCCESS_POST, SpogMessageCode.SUCCESS_POST, test);*/

			test.log(LogStatus.INFO, "Create task type and link it to the destination ");

			destinations = policy4SpogServer.createPolicyTaskDTO(null, task_id, taskType, dataStore_id, "none", null,
					cloudDirectFileBackupTaskInfoDTO, null, test);

		} else if (taskType == "cloud_direct_image_backup") {

			test.log(LogStatus.INFO, "Adding sources of type " + SourceType.machine + " to org " + organizationType);

			source_id = spogServer.createSourceWithCheck(resource_name, SourceType.machine, SourceProduct.cloud_direct,
					organization_id, site_id, ProtectionStatus.unprotect, ConnectionStatus.online, "windows",
					"SQLSERVER", test);

			test.log(LogStatus.INFO, "Create task type and link it to the destination ");

			destinations = policy4SpogServer.createPolicyTaskDTO(null, task_id, taskType, dataStore_id, "none",
					cloudDirectimageBackupTaskInfoDTO, null, null, test);

		} else if (taskType.equalsIgnoreCase("cloud_direct_vmware_backup")) {

			test.log(LogStatus.INFO, "Create task type and link it to the destination ");

			String destination_type = "cloud_direct_volume";
			destinations = policy4SpogServer.createPolicyTaskDTO(destination_name, null, task_id, taskType,
					dataStore_id, null, null, null, null, null, null, null, destination_type, test);

			cloudDirectScheduleDTO = policy4SpogServer.createCloudDirectScheduleDTO("30 15 * * *", test);

			test.log(LogStatus.INFO, "Create schedule settings");
			scheduleSettingDTO = policy4SpogServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null, null, null,
					null, test);

			schedules = policy4SpogServer.createPolicyScheduleDTO(null, schedule_id, "1d", task_id, dataStore_id,
					scheduleSettingDTO, null, null, taskType, destination_name, test);

		} else {
			HashMap<String, Object> cloud_hybrid_store = new HashMap<String, Object>();
			cloud_hybrid_store = spogDestinationServer.composeCloudHybridInfo(4, true, 64, true);
			test.log(LogStatus.INFO, "Creating a destination of type cloud_hybrid_store");
			Response response = spogDestinationServer.createDestination(UUID.randomUUID().toString(), validToken,
					cloud_id, organization_id, site_id, UUID.randomUUID().toString(), "20", "cloud_hybrid_store",
					"running", destination_name, cloud_hybrid_store, test);
			String destination_id = response.then().extract().path("data.destination_id");

			dataStore_id = destination_id;

			destinations = policy4SpogServer.createPolicyTaskDTO(null, task_id, taskType, dataStore_id, null, null,
					null, udp_replication_from_remote_DTO, test);

			scheduleSettingDTO = policy4SpogServer.createScheduleSettingDTO(null, customScheduleSettingDTO, null, null,
					null, test);

			schedules = policy4SpogServer.createPolicyScheduleDTO(null, spogServer.returnRandomUUID(), "custom",
					task_id, dataStore_id, scheduleSettingDTO, "06:00", "12:00", taskType, destination_name, test);

		}
		test.log(LogStatus.INFO, "Create network throttle ");

		ArrayList<HashMap<String, Object>> throttles = policy4SpogServer.createPolicyThrottleDTO(null, throttle_id,
				task_id, throttle_type, "1200", "1", "06:00", "18:00", taskType, dataStore_id, destination_name, test);

		test.log(LogStatus.INFO, "Create a policy of type cloud_direct_baas");

		Response response = null;

		if (taskType.equals("cloud_direct_vmware_backup")) {
			String hypervisor_id = UUID.randomUUID().toString();
			response = policy4SpogServer.createPolicy(policy_name, policy_description, policyType, "true", source_id,
					destinations, schedules, null, policy_id, organization_id, hypervisor_id, test);
		} else {
			response = policy4SpogServer.createPolicy(policy_name, policy_description, policyType, null, "true",
					source_id, destinations, schedules, throttles, policy_id, organization_id, test);
		}

		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		test.log(LogStatus.INFO, "get policy by id");
		Response policy_response = policy4SpogServer.getPolicyById(validToken, policy_id, test);
		HashMap<String, Object> act_response = policy_response.then().extract().path("data");

		test.log(LogStatus.INFO, "get sources of policy_type :" + policyType);
		response = policy4SpogServer.postDeployPolicyById(validToken, policy_id, SpogConstants.SUCCESS_GET_PUT_DELETE,
				null, act_response, test);

	}

	@DataProvider(name = "policy_info_invalid")
	public final Object[][] policy_info_invalid() {
		return new Object[][] {
				{ "Invalid scenario - missing token", "direct", ti.direct_org1_id, "cloud_direct_baas", "",
						SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED,
						UUID.randomUUID().toString() },
				{ "Invalid scenario - invalid token", "direct", ti.direct_org1_id, "cloud_direct_baas", "abc",
						SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT,
						UUID.randomUUID().toString() },

				{ "Invalid Scenario - random UUID as policy_id", "sub", ti.normal_msp1_suborg1_id, "cloud_direct_baas",
						ti.normal_msp1_suborg1_user1_token, SpogConstants.RESOURCE_NOT_EXIST,
						SpogMessageCode.POLICY_ID_NOT_FOUND, UUID.randomUUID().toString() },
				{ "Invalid Scenario - invalid UUID as policy_id", "sub", ti.normal_msp1_suborg1_id, "cloud_direct_baas",
						ti.normal_msp1_suborg1_user1_token, SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_POLICYID_IS_NOT_UUID, "abcd" },

		};
	}

	@Test(dataProvider = "policy_info_invalid", dependsOnMethods = "PostDeployPolicyById")
	public void PostDeployPolicyById_invalid(String Testcase, String org_type, String org_id, String policy_type,
			String validToken, int ExpectedStatusCode, SpogMessageCode ErrorMessage, String policy_id) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + org_type);
		test.log(LogStatus.INFO, "deploy policy with" + Testcase);

		policy4SpogServer.postDeployPolicyById(validToken, policy_id, ExpectedStatusCode, ErrorMessage, null, test);
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

		policy4SpogServer.setToken(validToken);
		Response response = policy4SpogServer.getPolicies(null);
		ArrayList<String> policies = new ArrayList<>();
		policies = response.then().extract().path("data.policy_id");

		if (!policies.isEmpty()) {
			policies.stream().forEach(policy -> {
				policy4SpogServer.deletePolicybyPolicyId(ti.csr_token, policy);
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

	/**
	 * gen_random_index - method used to generate random input
	 * 
	 * @author Ramya.Nagepalli
	 * @param total_Widgets_msp2
	 * @return
	 */

	public int gen_random_index(ArrayList<String> total_Widgets_msp2) {
		Random generator = new Random();
		int randomindx = generator.nextInt(total_Widgets_msp2.size());

		return randomindx;
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
