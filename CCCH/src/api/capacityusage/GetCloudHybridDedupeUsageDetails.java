package api.capacityusage;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import Constants.DestinationType;
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

public class GetCloudHybridDedupeUsageDetails extends base.prepare.Is4Org {
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

	// This information is related to the destination information of the
	// cloudDedupe volume

	@DataProvider(name = "getCloudHybridDedupeUsageDetails_valid")
	public final Object[][] getCloudHybridDedupeUsageDetails_valid() {
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

	@Test(dataProvider = "getCloudHybridDedupeUsageDetails_valid", dependsOnMethods = "deleteResources")
	public void getCloudHybridDedupeUsageDetails_valid(String organization_type, String adminToken,
			String organization_id, String create_user_id, String site_id, String cloud_id, Long start_time_ts,
			Long endTimeTS, String site_Token, String job_seq, Double percent_complete, String protected_data_size,
			String raw_data_size, String sync_read_size, String ntfs_volume_size, String virtual_disk_provision_size,
			String dedupe_savings, String organization_name) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + organization_type + "Organization");

		HashMap<String, Object> temp = new HashMap<String, Object>();

		HashMap<String, ArrayList<HashMap<String, Object>>> last_job = new HashMap<String, ArrayList<HashMap<String, Object>>>();

		test.assignAuthor("Ramya.Nagepalli");

		String sourceName = spogServer.ReturnRandom("SPOG_ARCSEVRE");
		test.log(LogStatus.INFO, "create source");

		spogServer.setToken(site_Token);
		String prefix = RandomStringUtils.randomAlphanumeric(8);

		// create cloud hybrid destination
		String hybrid_destination_name = "";
		String destination_store_id = "";

		hybrid_destination_name = prefix + "suborg_hybrid";
		destination_store_id = spogDestinationServer.createHybridDestination(ti.csr_token, organization_id,
				"91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", DestinationType.cloud_hybrid_store.toString(),
				hybrid_destination_name, "running", test);

		// update destination usage

		String primary_usage = "200.00", snapshot_usage = "200.00";
		Long timestamp = System.currentTimeMillis();

		// update destination usage
		spogDestinationServer.updateDestinationUsage(ti.csr_token, organization_id, destination_store_id,
				timestamp.toString(), primary_usage, snapshot_usage, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		spogServer.setToken(adminToken);
		Date current_date = new Date();

		int exp_day = current_date.getDate();
		int exp_month = current_date.getMonth();
		exp_month = exp_month + 1;
		int exp_year = 2018;

		temp.put("source_data_value", "400.0");
		temp.put("source_data_date", new SimpleDateFormat("yyyy,M,d").format(new Date()));

		temp.put("dedupe_savings_value", "0.0");

		spogreportServer.setToken(adminToken);

		String additionalUrl = "";

		test.log(LogStatus.INFO,
				"getCloudHybridDedupeUsageDetails for the organization" + organization_type + " with valid token");
		Response response = spogreportServer.getCloudHybridDedupeUsageDetails(adminToken, additionalUrl, test);

		spogreportServer.checkCloudHybridDedupeUsageDetails(response, additionalUrl, temp,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		additionalUrl = "organization_id=" + organization_id;

		test.log(LogStatus.INFO, "getCloudHybridDedupeUsageDetails for the organization" + organization_type
				+ " with ti.csr_readonly_token");
		response = spogreportServer.getCloudHybridDedupeUsageDetails(ti.csr_readonly_token, additionalUrl, test);
		spogreportServer.checkCloudHybridDedupeUsageDetails(response, additionalUrl, temp,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		// get capacity usage details filter with destination name
		additionalUrl = "destination_name=" + hybrid_destination_name;

		test.log(LogStatus.INFO, "getCloudHybridDedupeUsageDetails for the organization" + organization_type
				+ " filter with destination name");
		response = spogreportServer.getCloudHybridDedupeUsageDetails(adminToken, additionalUrl, test);

		spogreportServer.checkCloudHybridDedupeUsageDetails(response, additionalUrl, temp,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		// get capacity usage details filter with other/random destination name
		additionalUrl = "destination_name=name";

		test.log(LogStatus.INFO, "getCloudHybridDedupeUsageDetails for the organization" + organization_type
				+ " filter with destination name");
		response = spogreportServer.getCloudHybridDedupeUsageDetails(adminToken, additionalUrl, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);

		/*
		 * spogreportServer.checkCloudHybridDedupeUsageDetails(response,
		 * additionalUrl,temp,SpogConstants.SUCCESS_GET_PUT_DELETE,null,test);
		 */

		// create backup jobs report for the organization.
		test.log(LogStatus.INFO, "create report schedule ");
		HashMap<String, Object> scheduleInfo = new HashMap<String, Object>();
		scheduleInfo = spogreportServer.composeReportScheduleInfo("report_name", "backup_jobs", "last_7_days",
				System.currentTimeMillis(), System.currentTimeMillis(), "monthly", "", organization_id,
				spogServer.getLoggedinUser_EmailId() + ",a@gmail.com,b@gmail.com", null, "all_sources");
		spogreportServer.createReportScheduleWithCheck_audit(adminToken, scheduleInfo,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		ArrayList<HashMap<String, Object>> expectedReportsInfo = new ArrayList<HashMap<String, Object>>();
		expectedReportsInfo.add(scheduleInfo);

		response = spogreportServer.getReports(adminToken, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		String report_id = response.then().extract().path("data.get(0).report_id");

		// get Cloud Hybrid Capacity Usage Details filter with report_id
		additionalUrl = "report_id;=;" + report_id;

		test.log(LogStatus.INFO, "getCloudHybridDedupeUsageDetails for the organization" + organization_type
				+ " filter with destination name");
		response = spogreportServer.getCloudHybridDedupeUsageDetails(adminToken, additionalUrl, test);

		spogreportServer.checkCloudHybridDedupeUsageDetails(response, additionalUrl, temp,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		// get capacity usage details filter with random report_id
		additionalUrl = "report_id=" + UUID.randomUUID().toString();

		test.log(LogStatus.INFO, "getCloudHybridDedupeUsageDetails for the organization" + organization_type
				+ " filter with destination name");
		response = spogreportServer.getCloudHybridDedupeUsageDetails(adminToken, additionalUrl, test);

		spogreportServer.checkCloudHybridDedupeUsageDetails(response, additionalUrl, temp,
				SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.UNABLE_TO_FIND_REPORT, test);
		
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
		response = spogreportServer.getReports(adminToken, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		report_id = response.then().extract().path("data.get(0).report_id");

		additionalUrl = "report_id=" + report_id;

		test.log(LogStatus.INFO, "getCloudHybridDedupeUsageDetails for the organization" + organization_type
				+ " filter with backup report_id");
		response = spogreportServer.getCloudHybridDedupeUsageDetails(adminToken, additionalUrl, test);
		spogreportServer.checkCloudHybridDedupeUsageDetails(response, additionalUrl, temp,
				SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.REPORT_NOT_FOUND_OR_REMOVED, test);

		// getCloudHybridDedupeUsageDetails filter with null report_id
		additionalUrl = "report_id=" + null;

		test.log(LogStatus.INFO, "getCloudHybridDedupeUsageDetails for the organization" + organization_type
				+ " filter with backup report_id");
		response = spogreportServer.getCloudHybridDedupeUsageDetails(adminToken, additionalUrl, test);

		spogreportServer.checkCloudHybridDedupeUsageDetails(response, additionalUrl, temp,
				SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.NON_EXIST_CLOUD_ID, test);

	}

	@DataProvider(name = "getCloudHybridDedupeUsageDetails_invalid")
	public final Object[][] getCloudHybridDedupeUsageDetails_invalid() {
		return new Object[][] {
				{ "Invalid Authorization with invalid token", "Direct", "abc", ti.direct_org1_id, "",
						SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT },
				{ "Invalid Authorization with Missing token", "Direct", "", ti.direct_org1_id, "",
						SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED },

		};
	}

	@Test(dataProvider = "getCloudHybridDedupeUsageDetails_invalid")
	public void getCloudHybridDedupeUsageDetails_invalid(String testcase, String organization_type, String adminToken,
			String organization_id, String additionalUrl, int spogCode, SpogMessageCode Error_Message) {
		test = ExtentManager
				.getNewTest(this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName()
						+ testcase + "for Organization" + organization_type);

		HashMap<String, Object> expectedDetails = new HashMap<String, Object>();

		test.log(LogStatus.INFO,
				"getCloudHybridDedupeUsageDetails for the organization" + organization_type + " " + testcase);

		if (testcase.equals(
				"getCloudHybridDedupeUsageDetails for the organization with CSR token, filter with any other(UUID) organization_id")) {
			organization_id = UUID.randomUUID().toString();
			additionalUrl = spogServer.PrepareURL("organization_id;=;" + organization_id, "", 1, 20, test);
		}

		Response response = spogreportServer.getCloudHybridDedupeUsageDetails(adminToken, additionalUrl, test);

		spogreportServer.checkCloudHybridDedupeUsageDetails(response, additionalUrl, expectedDetails, spogCode,
				Error_Message, test);

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
