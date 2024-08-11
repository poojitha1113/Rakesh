package api.capacityusage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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

import Constants.DestinationStatus;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import Constants.volume_type;
import InvokerServer.GatewayServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGJobServer;
import InvokerServer.SPOGReportServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class GetCapacityUsageReportsDetails extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private SPOGJobServer spogJobServer;
	private SPOGDestinationServer spogDestinationServer;
	private SPOGReportServer spogreportServer;
	private Policy4SPOGServer policy4SPOGServer;

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

	ArrayList<String> available_actions = new ArrayList<String>();
	ArrayList<String> available_actions1 = new ArrayList<String>();
	private ArrayList<HashMap> source_group = new ArrayList<HashMap>();
	private String[] datacenters;
	private Policy4SPOGServer policy4SpogServer;

	@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runningMachine, String buildVersion) {

		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogJobServer = new SPOGJobServer(baseURI, port);
		spogreportServer = new SPOGReportServer(baseURI, port);
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

		// get cloud account for the direct organization
		Response response = spogServer.getCloudAccounts(ti.direct_org1_user1_token, "", test);
		Direct_cloud_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");

		// get cloud account for the msp organization
		response = spogServer.getCloudAccounts(ti.normal_msp_org1_user1_token, "", test);
		msp_cloud_account_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");

		// get cloud account for the root msp organization
		response = spogServer.getCloudAccounts(ti.root_msp_org1_user1_token, "", test);
		root_cloud_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");

		spogDestinationServer.setToken(ti.csr_token);
		datacenters = spogDestinationServer.getDestionationDatacenterID();
	}

	@DataProvider(name = "getCapacityUsageReportsDetails_valid")
	public final Object[][] getCapacityUsageReportsDetails_valid() {
		return new Object[][] {
				{ "Direct", ti.direct_org1_user1_token, ti.direct_org1_id, ti.direct_org1_user1_id, Direct_cloud_id,
						Direct_cloud_id, System.currentTimeMillis(), System.currentTimeMillis() + 1000,
						ti.direct_org1_user1_token, "1", 100.00, "300", "200", "200", "1000", "300", "300",
						ti.direct_org1_name },
				{ "sub", ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_id,
						msp_cloud_account_id, msp_cloud_account_id, System.currentTimeMillis(),
						System.currentTimeMillis(), ti.normal_msp1_suborg1_user1_token, "1", 100.00, "300", "200",
						"200", "1000", "300", "300", ti.normal_msp1_suborg1_name },
				{ "rootsub", ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_id,
						root_cloud_id, root_cloud_id, System.currentTimeMillis(), System.currentTimeMillis(),
						ti.root_msp1_suborg1_user1_token, "1", 100.00, "300", "200", "200", "1000", "300", "300",
						ti.root_msp1_suborg1_name },
				{ "submsp_sub", ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_sub_org1_id,
						ti.msp1_submsp1_suborg1_user1_id, root_cloud_id, root_cloud_id, System.currentTimeMillis(),
						System.currentTimeMillis(), ti.msp1_submsp1_suborg1_user1_token, "1", 100.00, "300", "200",
						"200", "1000", "300", "300", ti.msp1_submsp1_sub_org1_name },

		};
	}

	@Test(dataProvider = "getCapacityUsageReportsDetails_valid", dependsOnMethods = "deleteResources")
	public void getCapacityUsageReportsDetails_valid(String organization_type, String adminToken,
			String organization_id, String create_user_id, String site_id, String cloud_id, Long start_time_ts,
			Long endTimeTS, String site_Token, String job_seq, Double percent_complete, String protected_data_size,
			String raw_data_size, String sync_read_size, String ntfs_volume_size, String virtual_disk_provision_size,
			String dedupe_savings, String organization_name) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + organization_type + "Organization");

		String job_id = null, source_id = null, destination_id = null;
		ArrayList<HashMap<String, Object>> expectedDetails = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> temp = new HashMap<String, Object>();
		HashMap<String, Object> destination = new HashMap<String, Object>();
		HashMap<String, Object> organization = new HashMap<String, Object>();
		HashMap<String, Object> retention = new HashMap<String, Object>();

		HashMap<String, ArrayList<HashMap<String, Object>>> last_job = new HashMap<String, ArrayList<HashMap<String, Object>>>();

		test.assignAuthor("Ramya.Nagepalli");

		String sourceName = spogServer.ReturnRandom("SPOG_ARCSEVRE");
		test.log(LogStatus.INFO, "create source");

		spogServer.setToken(site_Token);
		String protectionStatus = "protect,unprotect,protect,unprotect,protect";
		String connectionStatus = "online,offline,online,offline,online";
		String osmajor = "windows,linux,mac,unknown,windows";
		String SourceType = "machine,machine,machine,machine,machine";
		String SourceProduct = "cloud_direct,cloud_direct,cloud_direct,cloud_direct,cloud_direct";

		String[] protection = protectionStatus.split(",");
		String[] connection = connectionStatus.split(",");
		String[] Osmajor = osmajor.split(",");
		String[] sourceType = SourceType.split(",");
		String[] sourceProduct = SourceProduct.split(",");

		ArrayList<String> Policies = new ArrayList<String>();
		test.log(LogStatus.INFO, "get a destination of type ");
		Response response = spogDestinationServer.getDestinations(adminToken, "organization_id=" + organization_id,
				test);
		destination_id = response.then().extract().path("data[0].destination_id");
		String destination_name = response.then().extract().path("data[0].destination_name");
		for (int i = 0; i < 1; i++) {

			String policy_id = UUID.randomUUID().toString();
			/* destination_name="cloud_direct_hybrid"+i; */
			String schedule_id = UUID.randomUUID().toString();
			String task_id = UUID.randomUUID().toString();
			String throttle_id = UUID.randomUUID().toString();

			String rps_id = UUID.randomUUID().toString();

			spogServer.setToken(adminToken);

			source_id = spogServer.createSourceWithCheck(sourceName + i, sourceType[i], sourceProduct[i],
					organization_id, cloud_id, protection[i], connection[i], Osmajor[i], "SQL_SERVER", create_user_id,
					SpogConstants.SUCCESS_POST, null, true, test);

			destination.put("destination_id", destination_id);
			destination.put("destination_name", destination_name);
			destination.put("destination_type", "cloud_direct_volume");

			String prefix = RandomStringUtils.randomAlphanumeric(8);

			test.log(LogStatus.INFO, "Create custom settings");
			HashMap<String, Object> customScheduleSettingDTO = policy4SpogServer
					.createCustomScheduleDTO("1522068700422", "full", "1", "true", "10", "minutes", test);

			HashMap<String, Object> scheduleSettingDTO = policy4SpogServer.createScheduleSettingDTO(null,
					customScheduleSettingDTO, null, null, test);

			test.log(LogStatus.INFO, "Create cloud direct schedule");

			HashMap<String, Object> cloudDirectScheduleDTO = policy4SpogServer.createCloudDirectScheduleDTO("0 6 * * *",
					test);

			test.log(LogStatus.INFO, "Create schedule settings");
			scheduleSettingDTO = policy4SpogServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null, null, null,
					test);

			ArrayList<HashMap<String, Object>> schedules = policy4SpogServer.createPolicyScheduleDTO(null, schedule_id,
					"1d", task_id, destination_id, scheduleSettingDTO, "06:00", "12:00",
					"cloud_direct_file_folder_backup", destination_name, test);

			test.log(LogStatus.INFO, "Exclude the folders from cloud direct file backup");

			ArrayList<HashMap<String, Object>> excludes = policy4SpogServer.createExcludeInfoDTO(null, "path",
					"c:\\tmp", test);

			HashMap<String, Object> cloudDirectLocalBackupDTO = policy4SpogServer
					.createCloudDirectLocalBackupDTO("d:\\tmp", "true", test);

			HashMap<String, Object> cloudDirectFileBackupTaskInfoDTO = policy4SpogServer
					.createCloudDirectFileBackupTaskInfoDTO("d:\\tmp", cloudDirectLocalBackupDTO, excludes, test);
			HashMap<String, Object> perform_ar_test = policy4SpogServer.createPerformARTestOption("1", "1", "1", "1",
					test);

			HashMap<String, Object> retention_policy = policy4SpogServer.createRetentionPolicyOption("2", "2", "2", "2",
					test);

			HashMap<String, Object> udp_replication_from_remote_DTO = policy4SpogServer
					.createUdpReplicationFromRemoteInfoDTO(perform_ar_test, retention_policy, test);

			ArrayList<HashMap<String, Object>> destinations = policy4SpogServer.createPolicyTaskDTO(null, task_id,
					"cloud_direct_file_folder_backup", destination_id, "none", null, cloudDirectFileBackupTaskInfoDTO,
					null, test);
			ArrayList<HashMap<String, Object>> throttles = policy4SpogServer.createPolicyThrottleDTO(null, throttle_id,
					task_id, "network", "1200", "1", "06:00", "18:00", "cloud_direct_file_folder_backup",
					destination_id, destination_name, test);

			// create cloud direct policy
			policy4SpogServer.setToken(adminToken);
			String policy_name = spogServer.ReturnRandom("ramya");
			response = policy4SpogServer.createPolicy(prefix + policy_name + i, policy_name, "cloud_direct_baas", null,
					"true", source_id, destinations, schedules, throttles, policy_id, organization_id, test);
			policy_id = response.then().extract().path("data.policy_id").toString();

			Policies.add(policy_id);

		}
		spogServer.setToken(adminToken);

		organization.put("organization_id", organization_id);
		if (organization_type.contains("sub"))
			organization.put("organization_name", organization_name);
		else
			organization.put("organization_name", "AUTO_" + organization_name);

		retention.put("retention_id", "7D");
		retention.put("retention_name", "7 Days");

		temp.put("destination", destination);
		temp.put("organization", organization);
		temp.put("retention", retention);

		temp.put("processed_bytes", "400.0");
		temp.put("dedupe_savings", "0.0");
		temp.put("create_ts", "0");
		temp.put("storage_usage", "0");

		expectedDetails.add(temp);
		spogreportServer.setToken(adminToken);

		String primary_usage = "200.00", snapshot_usage = "200.00";
		Long timestamp = System.currentTimeMillis();

		// update destination usage
		spogDestinationServer.updateDestinationUsage(adminToken, organization_id, destination_id, timestamp.toString(),
				primary_usage, snapshot_usage, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		String additionalUrl = "";

		test.log(LogStatus.INFO,
				"getCapacityUsageReportsDetails for the organization" + organization_type + " with valid token");
		response = spogreportServer.getCapacityUsageReportsDetails(adminToken, additionalUrl, test);

		spogreportServer.checkCapacityUsageDetails(response, additionalUrl, expectedDetails,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		test.log(LogStatus.INFO, "getCapacityUsageReportsDetails for the organization" + organization_type
				+ " with ti.csr_readonly_token");
		additionalUrl = "organization_id=" + organization_id;
		response = spogreportServer.getCapacityUsageReportsDetails(ti.csr_readonly_token, additionalUrl, test);

		spogreportServer.checkCapacityUsageDetails(response, additionalUrl, expectedDetails,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		// get capacity usage details filter with destination name
		additionalUrl = "destination_name=" + destination_name;

		test.log(LogStatus.INFO, "getCapacityUsageReportsDetails for the organization" + organization_type
				+ " filter with destination name");
		response = spogreportServer.getCapacityUsageReportsDetails(adminToken, additionalUrl, test);

		spogreportServer.checkCapacityUsageDetails(response, additionalUrl, expectedDetails,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		// get capacity usage details filter with destination name
		additionalUrl = "destination_name=name";

		test.log(LogStatus.INFO, "getCapacityUsageReportsDetails for the organization" + organization_type
				+ " filter with destination name");
		response = spogreportServer.getCapacityUsageReportsDetails(adminToken, additionalUrl, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		
		test.log(LogStatus.INFO, "get reports");
		response = spogreportServer.getReports(adminToken, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		ArrayList<String> reports = new ArrayList<>();
		reports = response.then().extract().path("data.report_id");

		if (!reports.isEmpty())
			reports.stream().forEach(report -> {
				spogreportServer.deleteReportsByIdWithCheck(adminToken, report, SpogConstants.SUCCESS_GET_PUT_DELETE,
						null, test);
			});

		test.log(LogStatus.INFO, "create report schedule ");
		HashMap<String, Object> scheduleInfo = new HashMap<String, Object>();
		scheduleInfo = spogreportServer.composeReportScheduleInfo("report_name", "capacity_usage", "last_7_days",
				System.currentTimeMillis(), System.currentTimeMillis(), "monthly", "", organization_id,
				"a@gmail.com,b@gmail.com", null, "all_sources");
		spogreportServer.createReportScheduleWithCheck_audit(adminToken, scheduleInfo,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		ArrayList<HashMap<String, Object>> expectedReportsInfo = new ArrayList<HashMap<String, Object>>();
		expectedReportsInfo.add(scheduleInfo);

		response = spogreportServer.getReports(adminToken, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		String report_id = response.then().extract().path("data.get(0).report_id");

		// get capacity usage details filter with report_id
		additionalUrl = "report_id=" + report_id;

		test.log(LogStatus.INFO, "getCapacityUsageReportsDetails for the organization" + organization_type
				+ " filter with destination name");
		response = spogreportServer.getCapacityUsageReportsDetails(adminToken, additionalUrl, test);

		spogreportServer.checkCapacityUsageDetails(response, additionalUrl, expectedDetails,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		// get capacity usage details filter with random report_id
		additionalUrl = "report_id=" + UUID.randomUUID().toString();

		test.log(LogStatus.INFO, "getCapacityUsageReportsDetails for the organization" + organization_type
				+ " filter with destination name");
		response = spogreportServer.getCapacityUsageReportsDetails(adminToken, additionalUrl, test);

		spogreportServer.checkCapacityUsageDetails(response, additionalUrl, expectedDetails,
				SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.UNABLE_TO_FIND_REPORT, test);

		// get capacity usage details filter with null report_id
		additionalUrl = "report_id=" + null;

		test.log(LogStatus.INFO, "getCapacityUsageReportsDetails for the organization" + organization_type
				+ " filter with destination name");
		response = spogreportServer.getCapacityUsageReportsDetails(adminToken, additionalUrl, test);

		spogreportServer.checkCapacityUsageDetails(response, additionalUrl, expectedDetails,
				SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.NON_EXIST_CLOUD_ID, test);

		spogreportServer.deleteReportsByIdWithCheck(adminToken, report_id, SpogConstants.SUCCESS_GET_PUT_DELETE,
				null, test);
		
		// create report of type backup
		test.log(LogStatus.INFO, "create report of type backup");
		scheduleInfo.clear();
		expectedReportsInfo.clear();
		scheduleInfo = spogreportServer.composeReportScheduleInfo("report_name", "backup_jobs", "last_7_days",
				System.currentTimeMillis(), System.currentTimeMillis(), "monthly", "", organization_id,
				"a@gmail.com,b@gmail.com", null, "all_sources");
		spogreportServer.createReportScheduleWithCheck_audit(adminToken, scheduleInfo,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		expectedReportsInfo.add(scheduleInfo);
		response = spogreportServer.getReports(adminToken, SpogConstants.SUCCESS_GET_PUT_DELETE,  test);
		report_id = response.then().extract().path("data.get(0).report_id");

		additionalUrl = "report_id=" + report_id;

		test.log(LogStatus.INFO, "getCapacityUsageReportsDetails for the organization" + organization_type
				+ " filter with backup report_id");
		response = spogreportServer.getCapacityUsageReportsDetails(adminToken, additionalUrl, test);
		spogreportServer.checkCapacityUsageDetails(response, additionalUrl, expectedDetails,
				SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.REPORT_NOT_FOUND_OR_REMOVED, test);

		for (int i = 0; i < Policies.size(); i++) {
			test.log(LogStatus.INFO,
					"Delete policy by id for the organization" + organization_type + " with valid token");
			policy4SpogServer.deletePolicybyPolicyId(adminToken, Policies.get(i), SpogConstants.SUCCESS_GET_PUT_DELETE,
					null, test);
		}
		// delete created sources
		spogServer.setToken(ti.csr_token);
		spogServer.deleteSourceByID(source_id, test);

		test.log(LogStatus.INFO, "get reports");
		response = spogreportServer.getReports(adminToken, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		reports.clear();
		reports = response.then().extract().path("data.report_id");

		if (!reports.isEmpty())
			reports.stream().forEach(report -> {
				spogreportServer.deleteReportsByIdWithCheck(adminToken, report, SpogConstants.SUCCESS_GET_PUT_DELETE,
						null, test);
			});

	}

	@DataProvider(name = "getCapacityUsageReportsDetails_invalid")
	public final Object[][] getCapacityUsageReportsDetails_invalid() {
		return new Object[][] {
				{ "Invalid Authorization with invalid token", "Direct", "abc", ti.direct_org1_id, "",
						SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT },
				{ "Invalid Authorization with Missing token", "Direct", "", ti.direct_org1_id, "",
						SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED },
				{ "InSufficient permissions Direct-Msp Organization", "Direct", ti.direct_org1_user1_token,
						ti.direct_org1_id,
						spogServer.PrepareURL("organization_id;=;" + ti.normal_msp_org1_id, "", 1, 20, test),
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "InSufficient permissions Direct-Suborg Organization", "Direct", ti.direct_org1_user1_token,
						ti.direct_org1_id,
						spogServer.PrepareURL("organization_id;=;" + ti.normal_msp1_suborg1_id, "", 1, 20, test),
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "InSufficient permissions suborg-Suborgb Organization", "suborg", ti.normal_msp1_suborg2_user1_token,
						ti.normal_msp1_suborg1_id,
						spogServer.PrepareURL("organization_id;=;" + ti.normal_msp1_suborg1_id, "", 1, 20, test),
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "InSufficient permissions csreadonly-Direct Organization", "csreadonly", ti.direct_org1_user1_token,
						ti.csr_org_id, spogServer.PrepareURL("organization_id;=;" + ti.csr_org_id, "", 1, 20, test),
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "InSufficient permissions csreadonly-msp Organization", "csreadonly", ti.normal_msp_org1_user1_token,
						ti.csr_org_id, spogServer.PrepareURL("organization_id;=;" + ti.csr_org_id, "", 1, 20, test),
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "InSufficient permissions csreadonly-Suborg Organization", "csreadonly",
						ti.normal_msp1_suborg2_user1_token, ti.csr_org_id,
						spogServer.PrepareURL("organization_id;=;" + ti.csr_org_id, "", 1, 20, test),
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "getCapacityUsageReportsDetails for the organization with valid token, filter with invalid organization_id",
						"Direct", ti.direct_org1_user1_token, "12345",
						spogServer.PrepareURL("organization_id;=;" + 123, "", 1, 20, test),
						SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL },
				/*{ "getCapacityUsageReportsDetails for the organization with CSR token, filter with any other(UUID) organization_id",
						"Direct", ti.csr_token, ti.direct_org1_id,
						spogServer.PrepareURL("organization_id;=;" + UUID.randomUUID().toString(), "", 1, 20, test),
						SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL },*/

		};
	}

	@Test(dataProvider = "getCapacityUsageReportsDetails_invalid")
	public void getCapacityUsageReportsDetails_invalid(String testcase, String organization_type, String adminToken,
			String organization_id, String additionalUrl, int spogCode, SpogMessageCode Error_Message) {
		test = ExtentManager
				.getNewTest(this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName()
						+ testcase + "for Organization" + organization_type);

		ArrayList<HashMap<String, Object>> expectedDetails = new ArrayList<HashMap<String, Object>>();

		test.log(LogStatus.INFO,
				"getCapacityUsageReportsDetails for the organization" + organization_type + " " + testcase);

		if (testcase.equals(
				"getCapacityUsageReportsDetails for the organization with CSR token, filter with any other(UUID) organization_id")) {
			organization_id = UUID.randomUUID().toString();
			additionalUrl = spogServer.PrepareURL("organization_id;=;" + organization_id, "", 1, 20, test);
		}

		spogreportServer.setToken(adminToken);
		Response response = spogreportServer.getCapacityUsageReportsDetails(adminToken, additionalUrl, test);
		spogreportServer.checkCapacityUsageDetails(response, additionalUrl, expectedDetails, spogCode, Error_Message,
				test);

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
		test.log(LogStatus.INFO, "get reports");
		response = spogreportServer.getReports(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		ArrayList<String> reports = new ArrayList<>();
		reports = response.then().extract().path("data.report_id");

		if (!reports.isEmpty())
			reports.stream().forEach(report -> {
				spogreportServer.deleteReportsByIdWithCheck(validToken, report, SpogConstants.SUCCESS_GET_PUT_DELETE,
						null, test);
			});
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
