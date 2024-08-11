package api.destination;

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
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGHypervisorsServer;
import InvokerServer.SPOGServer;
import base.prepare.TestOrgInfo;
import dataPreparation.JsonPreparation;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class GetDestinationsFilterByPolicyId extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private SPOGDestinationServer spogDestinationServer;
	private Policy4SPOGServer policy4SPOGServer;
	private SPOGHypervisorsServer spogHypervisorsServer;

	private ExtentTest test;
	private TestOrgInfo ti;

	ArrayList<HashMap<String, Object>> direct_Destination_information = new ArrayList<HashMap<String, Object>>();

	private ArrayList<HashMap<String, Object>> msp_Destination_information = new ArrayList<HashMap<String, Object>>();
	private ArrayList<HashMap<String, Object>> csr_Destination_information = new ArrayList<HashMap<String, Object>>();
	private ArrayList<HashMap<String, Object>> sub_Destination_information = new ArrayList<HashMap<String, Object>>();
	private ArrayList<HashMap<String, Object>> sub_mspDestination_information = new ArrayList<HashMap<String, Object>>();

	HashMap<String, Object> cloud_info_direct = new HashMap<String, Object>();
	HashMap<String, Object> cloud_info_dedupe = new HashMap<String, Object>();

	private JsonPreparation jp;

	String destination_type = "cloud_direct_volume";
	ArrayList<String> available_actions = new ArrayList<String>();
	ArrayList<HashMap<String, Object>> last_job = new ArrayList<HashMap<String, Object>>();
	ArrayList<String> schedule_options = new ArrayList<String>();
	HashMap<String, Object> cloud_volume = new HashMap<String, Object>();
	HashMap<String, Object> cloud_volume1 = new HashMap<String, Object>();
	HashMap<String, Object> cloud_volume2 = new HashMap<String, Object>();
	// HashMap<String,Object> retention= new HashMap<String,Object>();

	HashMap<String, Object> DestinationInfo = new HashMap<String, Object>();
	HashMap<String, Object> cloud_direct_volume = new HashMap<String, Object>();
	HashMap<String, String> retention = new HashMap<String, String>();

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
		gatewayServer = new GatewayServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		policy4SPOGServer = new Policy4SPOGServer(baseURI, port);
		spogHypervisorsServer = new SPOGHypervisorsServer(baseURI, port);
		rep = ExtentManager.getInstance(this.getClass().getSimpleName(), logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Ramya.Nagepalli";

		jp = new JsonPreparation();
		Nooftest = 0;
		// Used for creating a build number with dateformat
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date = new java.util.Date();
		this.buildVersion = buildVersion + "_" + dateFormater.format(date);

		BQName = this.getClass().getSimpleName();
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

		// composing available_actions
		available_actions.add("edit");
		available_actions.add("view_recovery_point");
		available_actions.add("delete");
		/*
		 * available_actions.add("start"); available_actions.add("rps_jumpstart");
		 */

		// composing retention
		retention.put("retention_id", "7D");
		retention.put("age_hours_max", "0");
		retention.put("age_four_hours_max", "0");
		retention.put("age_days_max", "7");
		retention.put("age_weeks_max", "0");
		retention.put("age_months_max", "0");
		retention.put("age_months_max", "0");

		// composing cloud volume
		cloud_volume.put("total_usage", 483520.0);
		cloud_volume.put("volume_type", "normal");
		cloud_volume.put("hostname", "windows8");
		cloud_volume.put("retention_id", "7D");
		cloud_volume.put("retention_name", "7 Days");
		cloud_volume.put("retention", retention);
		cloud_volume.put("schedule_options", schedule_options);

		// composing cloud volume for msp
		cloud_volume1.put("volume_type", "normal");
		cloud_volume1.put("hostname", "windows8");
		cloud_volume1.put("retention_id", "7D");
		cloud_volume1.put("retention_name", "7 Days");
		cloud_volume1.put("retention", retention);
		cloud_volume1.put("schedule_options", schedule_options);

		// composing cloud volume for sub org
		cloud_volume2.put("volume_type", "normal");
		cloud_volume2.put("hostname", "windows8");
		cloud_volume2.put("retention_id", "7D");
		cloud_volume2.put("retention_name", "7 Days");
		cloud_volume2.put("retention", retention);
		cloud_volume2.put("schedule_options", schedule_options);

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

	@DataProvider(name = "getDestinationsFilterByPolicyId")
	public final Object[][] getDestinationsFilterByPolicyId() {
		return new Object[][] {
				{ "Direct", ti.direct_org1_id, ti.direct_org1_user1_token, destination_id_direct, Direct_cloud_id,
						UUID.randomUUID().toString(), "cloud_direct_baas", "cloud_direct_file_folder_backup", null },
				// {"MSP",msp_organization_id,ti.normal_msp_org1_user1_token,destination_id_msp,msp_cloud_account_id,UUID.randomUUID().toString(),"cloud_direct_baas","cloud_direct_file_folder_backup",null},
				{ "SUB_ORG-cloud_direct_image_backup", ti.normal_msp1_suborg1_id, ti.normal_msp_org1_user1_token,
						destination_id_suborg, msp_cloud_account_id, UUID.randomUUID().toString(), "cloud_direct_baas",
						"cloud_direct_file_folder_backup", msp_cloud_account_id },
				{ "root_sub-cloud_direct_image_backup", ti.root_msp1_suborg1_id, ti.root_msp_org1_user1_token,
						destination_id_rootsub, root_cloud_id, UUID.randomUUID().toString(), "cloud_direct_baas",
						"cloud_direct_file_folder_backup", root_cloud_id },
				{ "submsp_sub-cloud_direct_image_backup", ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_token,
						destination_id_submsp_sub, root_cloud_id, UUID.randomUUID().toString(), "cloud_direct_baas",
						"cloud_direct_file_folder_backup", root_cloud_id },

		};
	}

	@Test(dataProvider = "getDestinationsFilterByPolicyId")
	public void getDestinationsByPolicyId(String organizationType, String organization_id, String validToken,
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

		schedules = policy4SPOGServer.createPolicyScheduleDTO(null, schedule_id, "1d", task_id, dataStore_id,
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

		if (taskType == "cloud_direct_hypervisor" || taskType == "cloud_direct_file_folder_backup") {

			test.log(LogStatus.INFO, "Adding sources of type " + SourceType.machine + " to org " + organizationType);
			// String source_id =
			// "3f2c5c57-738b-4a9c-ad61-a1ea599d945e,131dec05-7846-4459-acc9-004fd54634e5";

			spogServer.setToken(validToken);
			source_id = spogServer.createSourceWithCheck(resource_name, SourceType.machine, SourceProduct.cloud_direct,
					organization_id, site_id, ProtectionStatus.unprotect, ConnectionStatus.online, "windows", "SQLSERVER",
					test);

			String[] arrayofsourcenodes = new String[1];
			arrayofsourcenodes[0] = source_id;
			// CREATED SOURCE TO THE GROUP
			String group_name = "ramya";
			test.log(LogStatus.INFO, "Create a source group");
			String group_id = spogServer.createGroupWithCheck(organization_id, group_name, "add sources", test);
			spogServer.setToken(validToken);
			// add sourcs to the sourcegroup
			test.log(LogStatus.INFO, "Add the sources to the sourcegroup " + group_id);
			spogServer.addSourcetoSourceGroupwithCheck(group_id, arrayofsourcenodes, validToken,
					SpogConstants.SUCCESS_POST, SpogMessageCode.SUCCESS_POST, test);

			//
			test.log(LogStatus.INFO, "Create task type and link it to the destination ");

			destinations = policy4SPOGServer.createPolicyTaskDTO(null, task_id, taskType, dataStore_id, "none", null,
					cloudDirectFileBackupTaskInfoDTO, null, test);

		} else if (taskType == "cloud_direct_image_backup") {

			test.log(LogStatus.INFO, "Adding sources of type " + SourceType.machine + " to org " + organizationType);

			spogServer.setToken(validToken);
			source_id = spogServer.createSourceWithCheck(resource_name, SourceType.machine, SourceProduct.cloud_direct,
					organization_id, site_id, ProtectionStatus.unprotect, ConnectionStatus.online, "windows", "SQLSERVER",
					test);

			test.log(LogStatus.INFO, "Create task type and link it to the destination ");

			destinations = policy4SPOGServer.createPolicyTaskDTO(null, task_id, taskType, dataStore_id, "none",
					cloudDirectimageBackupTaskInfoDTO, null, null, test);

		} else {
			HashMap<String, Object> cloud_hybrid_store = new HashMap<String, Object>();
			cloud_hybrid_store = spogDestinationServer.composeCloudHybridInfo(4, true, 64, true);
			test.log(LogStatus.INFO, "Creating a destination of type cloud_hybrid_store");
			Response response = spogDestinationServer.createDestination(UUID.randomUUID().toString(), validToken,
					cloud_id, organization_id, site_id, UUID.randomUUID().toString(), "20", "cloud_hybrid_store",
					"running", destination_name, cloud_hybrid_store, test);
			String destination_id = response.then().extract().path("data.destination_id");

			dataStore_id = destination_id;

			destinations = policy4SPOGServer.createPolicyTaskDTO(null, task_id, taskType, dataStore_id, null, null,
					null, udp_replication_from_remote_DTO, test);

			scheduleSettingDTO = policy4SPOGServer.createScheduleSettingDTO(null, customScheduleSettingDTO, null, null,
					null, test);

			schedules = policy4SPOGServer.createPolicyScheduleDTO(null, spogServer.returnRandomUUID(), "custom",
					task_id, dataStore_id, scheduleSettingDTO, "06:00", "12:00", taskType, destination_name, test);

		}
		test.log(LogStatus.INFO, "Create network throttle ");

		ArrayList<HashMap<String, Object>> throttles = policy4SPOGServer.createPolicyThrottleDTO(null, throttle_id,
				task_id, throttle_type, "1200", "1", "06:00", "18:00", taskType, dataStore_id, destination_name, test);

		test.log(LogStatus.INFO, "Create a policy of type cloud_direct_baas");

		Response response = policy4SPOGServer.createPolicy(policy_name, policy_description, policyType, null, "true",
				null, destinations, schedules, throttles, policy_id, organization_id, test);

		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);

		// policy4SPOGServer.checkPolicyDestinations(response,
		// SpogConstants.SUCCESS_POST, destinations, test);

		policy4SPOGServer.checkPolicySchedules(response, SpogConstants.SUCCESS_POST, schedules, test);

		test.log(LogStatus.INFO, "get the policy by policy Id");

		String Policy_id = response.then().extract().path("data.policy_id");

		String FilterStr = "policy_id;=;" + Policy_id;

		String additionalURL = spogServer.PrepareURL(FilterStr, "", 1, 1, test);

		ArrayList<HashMap<String, Object>> Destination_information = new ArrayList<HashMap<String, Object>>();

		spogServer.setToken(validToken);
		String Org_name = spogServer.getLoggedInOrganizationName();

		DestinationInfo.put("destination_id", dataStore_id);
		DestinationInfo.put("cloud_account_id", cloud_id);
		DestinationInfo.put("site_id", Direct_cloud_id);
		DestinationInfo.put("organization_id", organization_id);
		DestinationInfo.put("organization_name", Org_name);
		DestinationInfo.put("datacenter_location", "Zetta Test");
		DestinationInfo.put("datacenter_region", "US");
		DestinationInfo.put("dedupe_savings", "20");
		DestinationInfo.put("destination_type", "cloud_direct_volume");
		DestinationInfo.put("destination_status", "running");
		DestinationInfo.put("destination_name", "destination_direct_volume");
		DestinationInfo.put("available_actions", available_actions);
		if (destination_type.equals("cloud_direct_volume")) {
			DestinationInfo.put("cloud_direct_volume", cloud_volume);
		} else if (destination_type.equals("cloud_hybrid_store")) {
			DestinationInfo.put("cloud_hybrid_store", cloud_volume);
		} else {
			DestinationInfo.put("share_folder", cloud_volume);
		}
		DestinationInfo.put("last_job", last_job);

		response = spogDestinationServer.getDestinations(validToken, additionalURL, test);

		Destination_information.add(DestinationInfo);

		spogDestinationServer.checkGetDestinations(response, SpogConstants.SUCCESS_GET_PUT_DELETE,
				Destination_information, 1, 1, FilterStr, "", null, test);

		test.log(LogStatus.INFO, "deleting the created sources in the organization with valid token");

		spogServer.deleteSourcesById(validToken, source_id, test);

		policy4SPOGServer.deletePolicybyPolicyId(validToken, Policy_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);
	}

	@Test(dataProvider = "getDestinationsFilterByPolicyId")
	public void getdestinationsbypolicyid_400_Invalid_Random_PolicyId(String organizationType, String organization_id,
			String validToken, String dataStore_id, String cloud_id, String policy_id, String policyType,
			String taskType, String site_id) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		String Policy_id = "Abc";

		ArrayList<HashMap<String, Object>> destinations = new ArrayList<HashMap<String, Object>>();

		String FilterStr = "policy_id;=;" + Policy_id;

		String additionalURL = spogServer.getFilterByPolicyIdUrl(FilterStr);

		test.log(LogStatus.INFO, "get the policy details with invalid Policy Id ");

		Response response = spogDestinationServer.getDestinations(validToken, additionalURL, test);

		// spogDestinationServer.checkGetDestinations(response,SpogConstants.INTERNAL_SERVER_ERROR,destinations,1,1,
		// FilterStr,null ,SpogMessageCode.ELEMENT_IS_NOT_UUID,test);

		Policy_id = UUID.randomUUID().toString();

		FilterStr = "policy_id;=;" + Policy_id;

		additionalURL = spogServer.getFilterByPolicyIdUrl(FilterStr);

		test.log(LogStatus.INFO, "get destinations filter by policy id as random ");

		response = spogDestinationServer.getDestinations(validToken, additionalURL, test);

		spogDestinationServer.checkGetDestinations(response, SpogConstants.SUCCESS_GET_PUT_DELETE, destinations, 1, 1,
				FilterStr, null, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

	}

	@AfterMethod
	public void getResult(ITestResult result) {
		if (result.getStatus() == ITestResult.FAILURE) {
			count1.setfailedcount();
			// remaincases=Nooftest-passedcases-failedcases;
			test.log(LogStatus.FAIL, "Test Case Failed is " + result.getName() + " with parameters as "
					+ Arrays.asList(result.getParameters()));
			test.log(LogStatus.FAIL, result.getThrowable().getMessage());

		} else if (result.getStatus() == ITestResult.SKIP) {
			test.log(LogStatus.SKIP, "Test Case Skipped is " + result.getName());
			count1.setskippedcount();
		} else if (result.getStatus() == ITestResult.SUCCESS) {
			count1.setpassedcount();
			// remaincases=Nooftest-passedcases-failedcases;

		}
		rep.endTest(test);
		rep.flush();
	}

}
