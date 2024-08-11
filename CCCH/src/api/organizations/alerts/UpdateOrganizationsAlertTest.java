package api.organizations.alerts;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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
import Constants.LogSeverityType;
import Constants.LogSourceType;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import Constants.volume_type;
import InvokerServer.GatewayServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGAlertsServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGJobServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class UpdateOrganizationsAlertTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private SPOGDestinationServer spogDestinationServer;
	private Policy4SPOGServer policy4SpogServer;
	private SPOGAlertsServer spogAlertsServer;

	private ExtentTest test;
	private TestOrgInfo ti;

	String[] type = new String[12];
	private String job_Type = "restore,restore";
	private String job_Method = "full,incremental,resync,full,incremental,resync,full,incremental,resync,full,incremental,resync";
	public String JobSeverity = "critical,information,critical,warning,information,critical,warning,information,critical,warning,information,critical";
	private String test_log_Message_1 = "testLogMessage", test_log_Message_2 = "connect_node_failed_test_message";
	public String JobStatus = "failed,failed,failed,incomplete,idle,waiting,crash,license_failed,backupjob_proc_exit,skipped,stop,active,finished";

	private String Direct_cloud_id;
	private String msp_cloud_account_id;
	private String root_cloud_id;
	private GatewayServer gatewayServer;
	private ArrayList<String> allowed_actions = new ArrayList<>();

	@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runningMachine, String buildVersion) {

		spogServer = new SPOGServer(baseURI, port);
		policy4SpogServer = new Policy4SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogAlertsServer=new SPOGAlertsServer(baseURI, port);

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

		allowed_actions.add("viewlogs");
		allowed_actions.add("acknowledge");
	}

	// This information is related to the destination information of the
	// cloudDedupe volume
	@DataProvider(name = "updateOrganizationsAlerts")
	public final Object[][] updateOrganizationsAlerts() {
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

	@Test(dataProvider = "updateOrganizationsAlerts", dependsOnMethods = "deleteResources")
	public void updateOrganizationsAlert_valid(String userName, String password, String adminToken,
			String organization_name, String organization_id, String create_user_id, String site_id, Long start_time_ts,
			Long endTimeTS, String site_Token, String status, String job_seq, Double percent_complete,
			String protected_data_size, String raw_data_size, String sync_read_size, String ntfs_volume_size,
			String virtual_disk_provision_size, String dedupe_savings, String logSourceType[], String LogSeverity[],
			String log_message_id, String[] log_message_data, boolean isLogExist, long log_generate_time,
			String help_message_id, String orgType, String org_name) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Ramya.Nagepalli");
		String job_id = null;
		HashMap<String, Object> alert_details = new HashMap<String, Object>();
		ArrayList<String> alert_data = new ArrayList<String>();
		alert_details.put("empty", true);
		ArrayList<HashMap> actual = new ArrayList<HashMap>();
		HashMap<String, ArrayList<HashMap<String, Object>>> last_job = new HashMap<String, ArrayList<HashMap<String, Object>>>();

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
		String job_status[] = JobStatus.split(",");

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
		HashMap<String, Object> perform_ar_test = policy4SpogServer.createPerformARTestOption("1", "1", "1", "1", test);
		HashMap<String, Object> retention_policy = policy4SpogServer.createRetentionPolicyOption("2", "2", "2", "2",
				test);
		HashMap<String, Object> udp_replication_from_remote_DTO = policy4SpogServer
				.createUdpReplicationFromRemoteInfoDTO(perform_ar_test, retention_policy, test);
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

		test.log(LogStatus.INFO, "deleting all alerts");
		test.log(LogStatus.INFO, "get organization Alerts ");
		response = spogAlertsServer.getOrganizationAlerts(adminToken, organization_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, test);

		ArrayList<String> alert_id_1 = response.then().extract().path("data.alert_id");

		for (int j = 0; j < alert_id_1.size(); j++) {
			test.log(LogStatus.INFO, "delete organization Alerts ");
			spogAlertsServer.deleteOrganizationAlertsByAlertId(adminToken, organization_id, alert_id_1.get(j),
					SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		}

		spogAlertsServer.deleteOrganizationAlerts_Acknowledgeall(adminToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		for (int i = 0; i < 2; i++) {
			start_time_ts = start_time_ts + i * 10 + 1;
			endTimeTS = endTimeTS + i * 10 + 200;
			// post jobs
			job_id = gatewayServer.postJobWithCheck(start_time_ts, endTimeTS, organization_id, source_id, source_id,
					rps_id, destination_id, policy_id, job_type[0], job_method[i], status, site_Token, test);
			
			last_job = spogDestinationServer.composeLastJob(start_time_ts, endTimeTS, percent_complete, status,
					job_type[0], job_method[i]);

			spogAlertsServer.setToken(adminToken);

			alert_details.put("destination_name", destination_name);
			alert_details.put("policy_name", policy_name);
			alert_details.put("source_name", source_name);

			if (i == 0) {

				alert_data.add("information");
			} else {
				alert_data.add("warning");
			}

			test.log(LogStatus.INFO, "get the Creating organization Alerts ");
			response = spogAlertsServer.getOrganizationAlerts(adminToken, organization_id,
					SpogConstants.SUCCESS_GET_PUT_DELETE, test);

			alert_id_1 = response.then().extract().path("data.alert_id");

			if (!(alert_id_1.isEmpty())) {
				if (organization_name.contains("csrreadonly")) {
					test.log(LogStatus.INFO, "updating organization Alerts with csrreadonly token");
					response = spogAlertsServer.updateOrganizationAlerts(ti.csr_readonly_token, alert_id_1.get(0),
							organization_id, job_id, type[i], job_severity[i], alert_details, alert_data,
							SpogConstants.INSUFFICIENT_PERMISSIONS, test);

					test.log(LogStatus.INFO, "check the updated organization Alerts ");
					spogAlertsServer.checkAlertData(response, alert_id_1.get(0), organization_id, org_name, job_id,
							type[i], job_severity[i], alert_details, alert_data, allowed_actions,
							SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY, test);

				} else {
					test.log(LogStatus.INFO, "updating organization Alerts ");
					response = spogAlertsServer.updateOrganizationAlerts(adminToken, alert_id_1.get(0), organization_id,
							job_id, "recovery_errors", job_severity[i], alert_details, alert_data,
							SpogConstants.SUCCESS_GET_PUT_DELETE, test);

					test.log(LogStatus.INFO, "check the updated organization Alerts ");
					spogAlertsServer.checkAlertData(response, alert_id_1.get(0), organization_id, org_name, job_id,
							"recovery_errors", job_severity[i], alert_details, alert_data, allowed_actions,
							SpogConstants.SUCCESS_POST, SpogMessageCode.SUCCESS_POST, test);

				}

				alert_details.clear();
				test.log(LogStatus.INFO, "delete organization Alerts ");
				spogAlertsServer.deleteOrganizationAlertsByAlertId(adminToken, organization_id, alert_id_1.get(0),
						SpogConstants.SUCCESS_GET_PUT_DELETE, test);
			}
			test.log(LogStatus.INFO, "delete created sources");
			spogServer.setToken(adminToken);
			spogServer.deleteSourceByID(source_id, test);

		}
		test.log(LogStatus.INFO, "delete organization policy by id ");
		policy4SpogServer.deletePolicybyPolicyId(adminToken, policy_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);
	}

	@DataProvider(name = "updateOrganizationsAlerts_invalid")
	public final Object[][] updateOrganizationsAlerts_invalid() {
		return new Object[][] { { ti.direct_org1_user1_email, ti.common_password, ti.direct_org1_user1_token, "Direct",
				ti.direct_org1_id, ti.direct_org1_user1_id, Direct_cloud_id, System.currentTimeMillis(),
				System.currentTimeMillis(), ti.direct_org1_user1_token, "finished", "1", 30.20, "3", "02", "2", "3",
				"3", "3",
				new String[] { LogSourceType.spog.toString(), LogSourceType.udp.toString(),
						LogSourceType.cloud_direct.toString() },
				new String[] { LogSeverityType.information.name(), LogSeverityType.warning.name(),
						LogSeverityType.error.name() },
				test_log_Message_1, new String[] { "node", "10.57.63.2" }, true, System.currentTimeMillis(),
				"arcserve@spog", "direct", ti.direct_org1_name }, };
	}

	// get the user defined job columns using csr/valid token
	@Test(dataProvider = "updateOrganizationsAlerts_invalid")
	public void updateOrganizationsAlert_invalid_401(String userName, String password, String adminToken,
			String organization_name, String organization_id, String create_user_id, String site_id, Long start_time_ts,
			Long endTimeTS, String site_Token, String status, String job_seq, Double percent_complete,
			String protected_data_size, String raw_data_size, String sync_read_size, String ntfs_volume_size,
			String virtual_disk_provision_size, String dedupe_savings, String logSourceType[], String LogSeverity[],
			String log_message_id, String[] log_message_data, boolean isLogExist, long log_generate_time,
			String help_message_id, String orgType, String org_name) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());

		test.assignAuthor("Ramya.Nagepalli");
		spogAlertsServer.setToken(adminToken);
		String job_id = null, log_id = null;

		HashMap<String, Object> alert_details = new HashMap<String, Object>();
		ArrayList<String> alert_data = new ArrayList<String>();

		alert_details.put("empty", true);
		alert_data.add("information");

		test.log(LogStatus.INFO, " Creating organization Alerts ");
		String[] job_severity = JobSeverity.split(",");
		String alert_id = UUID.randomUUID().toString();

		int i = 0;

		test.log(LogStatus.INFO, "update organization Alerts with invalidtoken");

		Response response = spogAlertsServer.updateOrganizationAlerts("abc", alert_id, organization_id, job_id, type[i],
				job_severity[i], alert_details, alert_data, SpogConstants.NOT_LOGGED_IN, test);

		test.log(LogStatus.INFO, "check response of updated organization Alerts ");
		spogAlertsServer.checkAlertData(response, alert_id, organization_id, org_name, job_id, type[i], job_severity[i],
				alert_details, alert_data, allowed_actions, SpogConstants.NOT_LOGGED_IN,
				SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);

		test.log(LogStatus.INFO, "update organization Alerts with missed token");
		response = spogAlertsServer.updateOrganizationAlerts("", alert_id, organization_id, job_id, type[i],
				job_severity[i], alert_details, alert_data, SpogConstants.NOT_LOGGED_IN, test);

		test.log(LogStatus.INFO, "check response of updated organization Alerts ");
		spogAlertsServer.checkAlertData(response, alert_id, organization_id, org_name, job_id, type[i], job_severity[i],
				alert_details, alert_data, allowed_actions, SpogConstants.NOT_LOGGED_IN,
				SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);

	}

	@Test(dataProvider = "updateOrganizationsAlerts_invalid")
	public void updateOrganizationsAlert_invalid_400(String userName, String password, String adminToken,
			String organization_name, String organization_id, String create_user_id, String site_id, Long start_time_ts,
			Long endTimeTS, String site_Token, String status, String job_seq, Double percent_complete,
			String protected_data_size, String raw_data_size, String sync_read_size, String ntfs_volume_size,
			String virtual_disk_provision_size, String dedupe_savings, String logSourceType[], String LogSeverity[],
			String log_message_id, String[] log_message_data, boolean isLogExist, long log_generate_time,
			String help_message_id, String orgType, String org_name) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());

		test.assignAuthor("Ramya.Nagepalli");
		spogAlertsServer.setToken(adminToken);
		String job_id = null, log_id = null;

		HashMap<String, Object> alert_details = new HashMap<String, Object>();
		ArrayList<String> alert_data = new ArrayList<String>();

		test.log(LogStatus.INFO, " Creating organization Alerts ");
		String[] job_severity = JobSeverity.split(",");
		String alert_id = UUID.randomUUID().toString();

		int i = 0;

		test.log(LogStatus.INFO, "update organization Alerts with token, invalid org id");
		Response response = spogAlertsServer.updateOrganizationAlerts(adminToken, alert_id, "abc", job_id, type[i],
				job_severity[i], alert_details, alert_data, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);

		test.log(LogStatus.INFO, "check response of updated organization Alerts ");
		spogAlertsServer.checkAlertData(response, alert_id, "abc", org_name, job_id, type[i], job_severity[i],
				alert_details, alert_data, allowed_actions, SpogConstants.REQUIRED_INFO_NOT_EXIST,
				SpogMessageCode.ELEMENT_ORGANIZATIONID_IS_NOT_UUID, test);

		test.log(LogStatus.INFO, "update organization Alerts with token, invalid alert id");
		response = spogAlertsServer.updateOrganizationAlerts(adminToken, "abc", organization_id, job_id, type[i],
				job_severity[i], alert_details, alert_data, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);

		test.log(LogStatus.INFO, "check response of updated organization Alerts ");
		spogAlertsServer.checkAlertData(response, "abc", organization_id, org_name, job_id, type[i], job_severity[i],
				alert_details, alert_data, allowed_actions, SpogConstants.REQUIRED_INFO_NOT_EXIST,
				SpogMessageCode.ELEMENT_ORGANIZATIONID_IS_NOT_UUID, test);

	}

	@Test(dataProvider = "updateOrganizationsAlerts_invalid")
	public void updateOrganizationsAlert_invalid_404(String userName, String password, String adminToken,
			String organization_name, String organization_id, String create_user_id, String site_id, Long start_time_ts,
			Long endTimeTS, String site_Token, String status, String job_seq, Double percent_complete,
			String protected_data_size, String raw_data_size, String sync_read_size, String ntfs_volume_size,
			String virtual_disk_provision_size, String dedupe_savings, String logSourceType[], String LogSeverity[],
			String log_message_id, String[] log_message_data, boolean isLogExist, long log_generate_time,
			String help_message_id, String orgType, String org_name) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());

		test.assignAuthor("Ramya.Nagepalli");
		spogAlertsServer.setToken(adminToken);
		String job_id = null, log_id = null;

		HashMap<String, Object> alert_details = new HashMap<String, Object>();
		ArrayList<String> alert_data = new ArrayList<String>();

		alert_details.put("empty", true);
		alert_data.add("information");

		test.log(LogStatus.INFO, " Creating organization Alerts ");
		String[] job_severity = JobSeverity.split(",");
		String alert_id = UUID.randomUUID().toString();

		String org_id = UUID.randomUUID().toString();
		int i = 0;

		test.log(LogStatus.INFO, "update organization Alerts with token, random alert id");

		Response response = spogAlertsServer.updateOrganizationAlerts(adminToken, alert_id, org_id, job_id, type[i],
				job_severity[i], alert_details, alert_data, SpogConstants.RESOURCE_NOT_EXIST, test);

		test.log(LogStatus.INFO, "check response of updated organization Alerts ");
		spogAlertsServer.checkAlertData(response, alert_id, org_id, org_name, job_id, type[i], job_severity[i],
				alert_details, alert_data, allowed_actions, SpogConstants.RESOURCE_NOT_EXIST,
				SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED, test);

	}

	@DataProvider(name = "updateOrganizationsAlerts_403")
	public final Object[][] updateOrganizationsAlerts_403() {
		return new Object[][] {
				{ "direct-msp", ti.direct_org1_id, "direct", ti.direct_org1_name, ti.normal_msp_org1_user1_token },
				{ "direct-suborg", ti.direct_org1_id, "direct", ti.direct_org1_name,
						ti.normal_msp1_suborg1_user1_token },
				{ "suborg-direct", ti.normal_msp1_suborg1_id, "sub_org", ti.normal_msp1_suborg1_name,
						ti.direct_org1_user1_token },
				{ "suborg-msp", ti.normal_msp1_suborg1_id, "sub_org", ti.normal_msp1_suborg1_name,
						ti.normal_msp_org2_user1_token },
				{ "suborg-suborgb", ti.normal_msp1_suborg1_id, "sub_org", ti.normal_msp1_suborg1_name,
						ti.normal_msp1_suborg2_user1_token },

				// 3 tier cases
				{ "suborg-rootmsp", ti.normal_msp1_suborg1_id, "sub_org", ti.normal_msp1_suborg1_name,
						ti.root_msp_org2_user1_token },
				{ "rootsub-rootmsp", ti.root_msp1_suborg1_id, "rootsub", ti.root_msp1_suborg1_name,
						ti.root_msp_org2_user1_token },
				{ "rootsub-submsp_sub", ti.root_msp1_suborg1_id, "rootsub", ti.root_msp1_suborg1_name,
						ti.msp1_submsp1_suborg1_user1_token },
				{ "rootsub-submsp", ti.root_msp1_suborg1_id, "rootsub", ti.root_msp1_suborg1_name,
						ti.root_msp1_submsp1_user1_token },

				{ "rootmsp-submsp_sub", ti.root_msp_org1_id, "root_msp", ti.root_msp_org1_name,
						ti.msp1_submsp1_suborg1_user1_token },
				{ "rootmsp-rootsub", ti.root_msp_org1_id, "root_msp", ti.root_msp_org1_name,
						ti.root_msp1_suborg1_user1_token },
				{ "rootmsp-submsp", ti.root_msp_org1_id, "root_msp", ti.root_msp_org1_name,
						ti.root_msp1_submsp1_user1_token },

				{ "submsp-submsp_sub", ti.root_msp1_submsp_org1_id, "submsp", ti.root_msp1_submsp_org1_name,
						ti.msp1_submsp1_suborg1_user1_token },
				{ "submsp-rootsub", ti.root_msp1_submsp_org1_id, "submsp", ti.root_msp1_submsp_org1_name,
						ti.root_msp1_suborg1_user1_token },

				{ "submsp-rootmaa", ti.root_msp1_submsp_org1_id, "submsp", ti.root_msp1_submsp_org1_name,
						ti.root_msp_org1_msp_accountadmin1_token },
				// monitor cases
				{ "direct-monitor", ti.direct_org1_id, "direct", ti.direct_org1_name,
						ti.direct_org1_monitor_user1_token },
				{ "suborg-monitor", ti.normal_msp1_suborg1_id, "sub_org", ti.normal_msp1_suborg1_name,
						ti.normal_msp1_suborg1_monitor_user1_token },
				{ "rootsub-monitor", ti.root_msp1_suborg1_id, "rootsub", ti.root_msp1_suborg1_name,
						ti.root_msp1_suborg1_monitor_user1_token },
				{ "rootmsp-monitor", ti.root_msp_org1_id, "root_msp", ti.root_msp_org1_name,
						ti.root_msp_org1_monitor_user1_token },
				{ "normalmsp-monitor", ti.normal_msp_org1_id, "normal_msp", ti.normal_msp_org1_name,
						ti.normal_msp_org1_monitor_user1_token },
				{ "submsp-monitor", ti.root_msp1_submsp_org1_id, "submsp", ti.root_msp1_submsp_org1_name,
						ti.root_msp1_submsp1_monitor_user1_token },
				};
	}

	@Test(dataProvider = "updateOrganizationsAlerts_403")
	public void updateOrganizationsAlerts_403(String testcase, String organization_id, String orgType, String org_name,
			String otherOrgToken) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());

		test.assignAuthor("Ramya.Nagepalli");
		String job_id = null;

		HashMap<String, Object> alert_details = new HashMap<String, Object>();
		ArrayList<String> alert_data = new ArrayList<String>();

		String[] job_severity = JobSeverity.split(",");
		String alert_id = UUID.randomUUID().toString();

		Response response = null;

		int i = 0;

		test.log(LogStatus.INFO, "update organization Alerts with token, random org id");
		response = spogAlertsServer.updateOrganizationAlerts(otherOrgToken, alert_id, organization_id, job_id, type[i],
				job_severity[i], alert_details, alert_data, SpogConstants.INSUFFICIENT_PERMISSIONS, test);

		test.log(LogStatus.INFO, "check response of updated organization Alerts ");
		spogAlertsServer.checkAlertData(response, alert_id, organization_id, org_name, job_id, type[i], job_severity[i],
				alert_details, alert_data, allowed_actions, SpogConstants.INSUFFICIENT_PERMISSIONS,
				SpogMessageCode.RESOURCE_PERMISSION_DENY, test);

		String org_id = UUID.randomUUID().toString();

		test.log(LogStatus.INFO, "Creating organization Alerts with invalid org id");

		if(!(testcase.contains("monitor")))
		{
		test.log(LogStatus.INFO, "update organization Alerts with token, random org id");
		response = spogAlertsServer.updateOrganizationAlerts(otherOrgToken, alert_id, org_id, job_id, type[i],
				job_severity[i], alert_details, alert_data, SpogConstants.RESOURCE_NOT_EXIST, test);

		test.log(LogStatus.INFO, "check response of updated organization Alerts ");
		spogAlertsServer.checkAlertData(response, alert_id, org_id, org_name, job_id, type[i], job_severity[i],
				alert_details, alert_data, allowed_actions, SpogConstants.RESOURCE_NOT_EXIST,
				SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED, test);
		}

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
