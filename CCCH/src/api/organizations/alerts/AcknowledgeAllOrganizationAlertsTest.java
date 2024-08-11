package api.organizations.alerts;

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
import Constants.LogSeverityType;
import Constants.LogSourceType;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGAlertsServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class AcknowledgeAllOrganizationAlertsTest extends base.prepare.Is4Org {

	private SPOGServer spogServer;
	private SPOGAlertsServer spogAlertsServer;
	private SPOGDestinationServer spogDestinationServer;
	private Policy4SPOGServer policy4SpogServer;

	private ExtentTest test;
	private TestOrgInfo ti;

	String[] type = new String[12];
	private String job_Type = "restore,restore";
	private String job_Status = "finished";
	private String job_Method = "full,incremental,resync,full,incremental,resync,full,incremental,resync,full,incremental,resync";
	public String JobSeverity = "critical,information,critical,warning,information,critical,warning,information,critical,warning,information,critical";
	private String test_log_Message_1 = "testLogMessage", test_log_Message_2 = "connect_node_failed_test_message";

	private String org_model_prefix = this.getClass().getSimpleName();
	private String Direct_cloud_id;
	private String msp_cloud_account_id;
	private String root_cloud_id;
	private GatewayServer gatewayServer;

	@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runningMachine, String buildVersion) {

		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogAlertsServer = new SPOGAlertsServer(baseURI, port);
		policy4SpogServer = new Policy4SPOGServer(baseURI, port);

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

		// Alert Types
		type[0] = "backup_missed";
		type[1] = "backup_failed";
		type[2] = "policy_errors";
		type[3] = "policy_warnings";
		type[4] = "policy_success";
		type[5] = "backup_errors";
		type[6] = "backup_warnings";
		type[7] = "backup_success";
		type[8] = "recovery_failed";
		type[9] = "recovery_errors";
		type[10] = "recovery_warnings";
		type[11] = "recovery_success";

		// get cloud account for the direct organization
		Response response = spogServer.getCloudAccounts(ti.direct_org1_user1_token, "", test);
		Direct_cloud_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");

		// get cloud account for the msp organization
		response = spogServer.getCloudAccounts(ti.normal_msp_org1_user1_token, "", test);
		msp_cloud_account_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");

		// get cloud account for the root msp organization
		response = spogServer.getCloudAccounts(ti.root_msp_org1_user1_token, "", test);
		root_cloud_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");
	}

	// This information is related to the destination information of the
	// cloudDedupe volume
	@DataProvider(name = "AcknowledgeAllOrganizationAlerts_valid")
	public final Object[][] AcknowledgeAllOrganizationAlerts_valid() {
		return new Object[][] {
				{ ti.direct_org1_user1_email, ti.common_password, ti.direct_org1_user1_token, "Direct",
						ti.direct_org1_id, ti.direct_org1_user1_id, Direct_cloud_id, System.currentTimeMillis(),
						System.currentTimeMillis(), ti.direct_org1_user1_token, "failed", "1", 30.20, "3", "02", "2",
						"3", "3", "3",
						new String[] { LogSourceType.spog.toString(), LogSourceType.udp.toString(),
								LogSourceType.cloud_direct.toString() },
						new String[] { LogSeverityType.information.name(), LogSeverityType.warning.name(),
								LogSeverityType.error.name() },
						test_log_Message_1, new String[] { "node", "10.57.63.2" }, true, System.currentTimeMillis(),
						"arcserve@spog", "direct", ti.direct_org1_name },
				{ ti.normal_msp1_suborg1_user1_email, ti.common_password, ti.normal_msp1_suborg1_user1_token, "suborg",
						ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_id, msp_cloud_account_id,
						System.currentTimeMillis(), System.currentTimeMillis(), ti.normal_msp1_suborg1_user1_token,
						"failed", "5", 24.45, "5", "6", "5", "5", "7", "4",
						new String[] { LogSourceType.spog.toString(), LogSourceType.udp.toString(),
								LogSourceType.cloud_direct.toString() },
						new String[] { LogSeverityType.information.name(), LogSeverityType.warning.name(),
								LogSeverityType.error.name() },
						test_log_Message_1, new String[] { "node", "10.57.63.2" }, true, System.currentTimeMillis(),
						"arcserve@spog", "sub_org", ti.normal_msp1_suborg1_name },

				{ ti.root_msp1_suborg1_user1_email, ti.common_password, ti.root_msp1_suborg1_user1_token, "rootsub",
						ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_id, root_cloud_id,
						System.currentTimeMillis(), System.currentTimeMillis(), ti.root_msp1_suborg1_user1_token,
						"failed", "5", 24.45, "5", "6", "5", "5", "7", "4",
						new String[] { LogSourceType.spog.toString(), LogSourceType.udp.toString(),
								LogSourceType.cloud_direct.toString() },
						new String[] { LogSeverityType.information.name(), LogSeverityType.warning.name(),
								LogSeverityType.error.name() },
						test_log_Message_1, new String[] { "node", "10.57.63.2" }, true, System.currentTimeMillis(),
						"arcserve@spog", "sub_org", ti.root_msp1_suborg1_name },
				{ ti.msp1_submsp1_suborg1_user1_email, ti.common_password, ti.msp1_submsp1_suborg1_user1_token,
						"csrreadonly", ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_id, root_cloud_id,
						System.currentTimeMillis(), System.currentTimeMillis(), ti.msp1_submsp1_suborg1_user1_token,
						"failed", "5", 24.45, "5", "6", "5", "5", "7", "4",
						new String[] { LogSourceType.spog.toString(), LogSourceType.udp.toString(),
								LogSourceType.cloud_direct.toString() },
						new String[] { LogSeverityType.information.name(), LogSeverityType.warning.name(),
								LogSeverityType.error.name() },
						test_log_Message_1, new String[] { "node", "10.57.63.2" }, true, System.currentTimeMillis(),
						"arcserve@spog", "sub_org", ti.msp1_submsp1_sub_org1_name },

		};
	}

	@Test(dataProvider = "AcknowledgeAllOrganizationAlerts_valid", dependsOnMethods = "deleteResources")
	public void postData(String userName, String password, String adminToken, String organization_name,
			String organization_id, String create_user_id, String site_id, Long start_time_ts, Long endTimeTS,
			String site_Token, String status, String job_seq, Double percent_complete, String protected_data_size,
			String raw_data_size, String sync_read_size, String ntfs_volume_size, String virtual_disk_provision_size,
			String dedupe_savings, String logSourceType[], String LogSeverity[], String log_message_id,
			String[] log_message_data, boolean isLogExist, long log_generate_time, String help_message_id,
			String orgType, String org_name) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Ramya.Nagepalli");

		String destination_id = UUID.randomUUID().toString();
		spogServer.userLogin(userName, password);
		adminToken = spogServer.getJWTToken();
		String rps_id = UUID.randomUUID().toString();
		String policy_id = UUID.randomUUID().toString();
		String sourceName = spogServer.ReturnRandom("ramya_source");
		test.log(LogStatus.INFO, "create source");
		spogServer.setToken(adminToken);
		String job_type[] = job_Type.split(",");
		String job_method[] = job_Method.split(",");
		String job_severity[] = JobSeverity.split(",");

		String prefix = RandomStringUtils.randomAlphanumeric(8);
		spogServer.userLogin(userName, password);
		adminToken = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "create source");
		spogServer.setToken(adminToken);

		String schedule_id = UUID.randomUUID().toString();
		String task_id = UUID.randomUUID().toString();
		String throttle_id = UUID.randomUUID().toString();
		String source_name, source_id, destination_name;

		spogServer.setToken(adminToken);
		source_id = spogServer.createSourceWithCheck(sourceName, SourceType.machine, SourceProduct.cloud_direct,
				organization_id, site_id, ProtectionStatus.unprotect, ConnectionStatus.online, "windows", "SQL_SERVER",
				test);

		Response response = spogServer.getSourceById(adminToken, source_id, test);
		source_name = response.then().extract().path("data.source_name").toString();

		test.log(LogStatus.INFO, "get destination of logged in user");
		response = spogDestinationServer.getDestinations(adminToken, "organization_id=" + organization_id, test);
		destination_id = response.then().extract().path("data[" + 0 + "].destination_id");
		destination_name = response.then().extract().path("data[" + 0 + "].destination_name");

		// create schedule for policy
		test.log(LogStatus.INFO, "Create custom settings");
		HashMap<String, Object> customScheduleSettingDTO = policy4SpogServer.createCustomScheduleDTO("1522068700422",
				"full", "1", "true", "10", "minutes", test);

		HashMap<String, Object> scheduleSettingDTO = policy4SpogServer.createScheduleSettingDTO(null,
				customScheduleSettingDTO, null, null, test);

		test.log(LogStatus.INFO, "Create cloud direct schedule");

		HashMap<String, Object> cloudDirectScheduleDTO = policy4SpogServer.createCloudDirectScheduleDTO("0 6 * * *",
				test);

		test.log(LogStatus.INFO, "Create schedule settings");
		scheduleSettingDTO = policy4SpogServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null, null, null, test);

		ArrayList<HashMap<String, Object>> schedules = policy4SpogServer.createPolicyScheduleDTO(null, schedule_id,
				"1d", task_id, destination_id, scheduleSettingDTO, "06:00", "18:00", "cloud_direct_file_folder_backup",
				destination_name, test);

		test.log(LogStatus.INFO, "Exclude the folders from cloud direct file backup");

		ArrayList<HashMap<String, Object>> excludes = policy4SpogServer.createExcludeInfoDTO(null, "path", "c:\\tmp",
				test);
		HashMap<String, Object> cloudDirectLocalBackupDTO = policy4SpogServer.createCloudDirectLocalBackupDTO("d:\\tmp",
				"true", test);
		HashMap<String, Object> cloudDirectFileBackupTaskInfoDTO = policy4SpogServer
				.createCloudDirectFileBackupTaskInfoDTO("d:\\tmp", cloudDirectLocalBackupDTO, excludes, test);
		ArrayList<HashMap<String, Object>> destinations = policy4SpogServer.createPolicyTaskDTO(null, task_id,
				"cloud_direct_file_folder_backup", destination_id, "none", null, cloudDirectFileBackupTaskInfoDTO, null,
				test);
		ArrayList<HashMap<String, Object>> throttles = policy4SpogServer.createPolicyThrottleDTO(null, throttle_id,
				task_id, "network", "1200", "1", "06:00", "18:00", "cloud_direct_file_folder_backup", destination_id,
				destination_name, test);

		policy4SpogServer.setToken(adminToken);
		// create cloud direct policy
		String policy_name = spogServer.ReturnRandom("ramya");

		response = policy4SpogServer.createPolicy(policy_name, policy_name, "cloud_direct_baas", null, "true", null,
				destinations, schedules, throttles, policy_id, organization_id, test);
		policy_id = response.then().extract().path("data.policy_id").toString();

		for (int i = 0; i < 1; i++) {
			start_time_ts = start_time_ts + i * 10 + 1;
			endTimeTS = endTimeTS + i * 10 + 200;
			// post jobs
			gatewayServer.setToken(adminToken);
			gatewayServer.postJobWithCheck(start_time_ts, endTimeTS, organization_id, source_id, source_id, rps_id,
					destination_id, policy_id, job_type[0], job_method[i], status, site_Token, test);

		}

	}

	@DataProvider(name = "org_info1")
	public final Object[][] org_info1() {
		return new Object[][] {
				{ "direct", ti.direct_org1_id, ti.direct_org1_user1_token, SpogConstants.SUCCESS_GET_PUT_DELETE,
						SpogMessageCode.SUCCESS_GET_PUT_DEL },
				{ "nomal_msp", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token,
						SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL },
				{ "root_msp", ti.root_msp_org1_id, ti.root_msp_org1_user1_token, SpogConstants.SUCCESS_GET_PUT_DELETE,
						SpogMessageCode.SUCCESS_GET_PUT_DEL },
				{ "root_sub", ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_token,
						SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL },
				{ "submsp", ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_user1_token,
						SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL },
				{ "submsp_sub", ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_token,
						SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL },
				// csr read only cases
				{ "direct", ti.direct_org1_id, ti.csr_readonly_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "nomal_msp", ti.normal_msp_org1_id, ti.csr_readonly_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "root_msp", ti.root_msp_org1_id, ti.csr_readonly_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "root_sub", ti.root_msp1_suborg1_id, ti.csr_readonly_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "submsp", ti.root_msp1_submsp_org1_id, ti.csr_readonly_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "submsp_sub", ti.msp1_submsp1_sub_org1_id, ti.csr_readonly_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				// monitor role cases
				{ "direct_monitor", ti.direct_org1_id, ti.direct_org1_monitor_user1_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "nomal_msp_monitor", ti.normal_msp_org1_id, ti.normal_msp_org1_monitor_user1_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "root_msp_monitor", ti.root_msp_org1_id, ti.root_msp_org1_monitor_user1_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "root_sub_monitor", ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_monitor_user1_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "submsp_monitor", ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_monitor_user1_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "submsp_sub_monitor", ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_monitor_user1_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },

		};
	}

	@Test(dataProvider = "org_info1", dependsOnMethods = "postData")
	public void acknowledgeAllOrganizationAlerts_200(String org_type, String organization_id, String adminToken,
			int ExpectedStatusCode, SpogMessageCode expectederror) {

		test.log(LogStatus.INFO, "get the organization Alerts " + org_type);
		Response response = spogAlertsServer.getOrganizationAlerts(adminToken, organization_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		ArrayList<String> exp_alerts = response.then().extract().path("data.alert_id");

		test.log(LogStatus.INFO, "acknowledge all organization alerts");
		spogAlertsServer.deleteOrganizationAlerts_Acknowledgeall(adminToken, ExpectedStatusCode, expectederror, test);

		test.log(LogStatus.INFO, "get alerts after deleting all alerts for validation");
		response = spogAlertsServer.getOrganizationAlerts(adminToken, organization_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		exp_alerts = response.then().extract().path("data.alert_id");

		if (exp_alerts.isEmpty())
			test.log(LogStatus.PASS, "Successfully deleted all organization alerts");
	}

	@DataProvider(name = "Acknowledgeall_invalid")
	public final Object[][] Acknowledgeall_invalid() {
		return new Object[][] { { ti.direct_org1_user1_token }, };
	}

	// get the user defined job columns using csr/valid token
	@Test(dataProvider = "Acknowledgeall_invalid")
	public void deleteOrganizationAlerts_Acknowledgeall_invalid(String adminToken) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());

		test.assignAuthor("Ramya.Nagepalli");

		test.log(LogStatus.INFO, "delete organization Alerts with invalidtoken");
		spogAlertsServer.deleteOrganizationAlerts_Acknowledgeall("abc", SpogConstants.NOT_LOGGED_IN,
				SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);

		test.log(LogStatus.INFO, "delete organization Alerts with missed token");
		spogAlertsServer.deleteOrganizationAlerts_Acknowledgeall("", SpogConstants.NOT_LOGGED_IN,
				SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);

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

	/******************************************************************
	 * RandomFunction
	 ******************************************************************************/
	public int gen_random_index(String[] job_status) {
		Random generator = new Random();
		int randomindx = generator.nextInt(job_status.length);

		return randomindx;
	}

}
