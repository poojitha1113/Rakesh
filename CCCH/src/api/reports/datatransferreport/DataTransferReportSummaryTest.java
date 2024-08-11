package api.reports.datatransferreport;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.UUID;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.ConnectionStatus;
import Constants.DestinationStatus;
import Constants.DestinationType;
import Constants.JobStatus;
import Constants.Job_method;
import Constants.LogSeverityType;
import Constants.OSMajor;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGHypervisorsServer;
import InvokerServer.SPOGReportServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import InvokerServer.GatewayServer.siteType;
import base.prepare.TestOrgInfo;
import InvokerServer.Policy4SPOGServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class DataTransferReportSummaryTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private SPOGReportServer spogReportServer;
	private SPOGDestinationServer spogDestinationServer;
	private GatewayServer gatewayServer;
	private ExtentTest test;

	private TestOrgInfo ti;
	private String Direct_cloud_id;
	private String msp_cloud_account_id;
	private String root_cloud_id;
	private ArrayList<HashMap<String, Object>> direct_response = new ArrayList<>();
	private ArrayList<String> direct_sources = new ArrayList<>();
	private ArrayList<String> direct_sourceGroups = new ArrayList<>();
	private String direct_report_id;
	private ArrayList<HashMap<String, Object>> suborg_response = new ArrayList<>();
	private ArrayList<String> suborg_sources = new ArrayList<>();
	private ArrayList<String> suborg_sourceGroups = new ArrayList<>();
	private String sub_report_id;
	private String direct_destinationid;
	private String suborg_destinationid;
	private String rootsub_destinationid;
	private String submsp_sub_destinationid;

	private ArrayList<HashMap<String, Object>> rootsub_response = new ArrayList<>();
	private ArrayList<String> rootsub_sources = new ArrayList<>();
	private ArrayList<String> rootsub_sourceGroups = new ArrayList<>();
	private String rootsub_report_id;

	private ArrayList<HashMap<String, Object>> submsp_sub_response = new ArrayList<>();
	private ArrayList<String> submsp_sub_sources = new ArrayList<>();
	private ArrayList<String> submsp_sub_sourceGroups = new ArrayList<>();
	private String submsp_sub_report_id;
	private Policy4SPOGServer policy4SpogServer;

	@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runningMachine, String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		spogReportServer = new SPOGReportServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		policy4SpogServer=new Policy4SPOGServer(baseURI, port);
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
		// get cloud account for the submsp_sub organization
		Response response = spogServer.getCloudAccounts(ti.direct_org1_user1_token, "", test);
		Direct_cloud_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");

		// get cloud account for the msp organization
		response = spogServer.getCloudAccounts(ti.normal_msp_org1_user1_token, "", test);
		msp_cloud_account_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");

		// get cloud account for the root msp organization
		response = spogServer.getCloudAccounts(ti.root_msp_org1_user1_token, "", test);
		root_cloud_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");

		response = spogDestinationServer.getDestinations(ti.direct_org1_user1_token, "", test);
		direct_destinationid = response.then().extract().path("data[" + 0 + "].destination_id");

		response = spogDestinationServer.getDestinations(ti.normal_msp_org1_user1_token,
				"organization_id=" + ti.normal_msp1_suborg1_id, test);
		suborg_destinationid = response.then().extract().path("data[" + 0 + "].destination_id");

		response = spogDestinationServer.getDestinations(ti.root_msp_org1_user1_token,
				"organization_id=" + ti.root_msp1_suborg1_id, test);
		rootsub_destinationid = response.then().extract().path("data[" + 0 + "].destination_id");

		response = spogDestinationServer.getDestinations(ti.root_msp1_submsp1_user1_token,
				"organization_id=" + ti.root_msp1_submsp_org1_id, test);
		submsp_sub_destinationid = response.then().extract().path("data[" + 0 + "].destination_id");
	}

	@DataProvider(name = "postSource")
	public Object[][] postSource() {
		return new Object[][] {
				/*
				 * // dataprovider to create the sources in all organizations { "direct",
				 * ti.direct_org1_user1_token, ti.direct_org1_id, direct_site_id,
				 * UUID.randomUUID().toString() }, { "suborg",
				 * ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_id,
				 * suborga_site_id, UUID.randomUUID().toString() },
				 */
				{ "direct", ti.direct_org1_user1_token, ti.direct_org1_id, Direct_cloud_id, direct_destinationid },
				{ "suborg", ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_id, msp_cloud_account_id,
						suborg_destinationid },
				{ "rootsub", ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_id, root_cloud_id,
						rootsub_destinationid },
				{ "submsp_sub", ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_sub_org1_id, root_cloud_id,
						submsp_sub_destinationid },

		};
	}

	@Test(dataProvider = "postSource",dependsOnMethods="deleteResources")
	public void postData(String orgType, String validToken, String organization_id, String site_id,
			String destination_id) {

		String source_id;
		String source_name = spogServer.ReturnRandom("source");
		long start_time_ts = System.currentTimeMillis();
		long end_time_ts = System.currentTimeMillis() + 10000;
		String rps_id = UUID.randomUUID().toString();
		String policy_id = UUID.randomUUID().toString();
		HashMap<String, Object> scheduleInfo = new HashMap<String, Object>();
		ArrayList<HashMap<String, Object>> expectedReportsInfo = new ArrayList<HashMap<String, Object>>();

		spogServer.setToken(validToken);
		test.log(LogStatus.INFO, "Creating source in the organization of type: " + orgType);
		source_id = spogServer.createSourceWithCheck(source_name, SourceType.machine, SourceProduct.cloud_direct,
				organization_id, site_id, ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(),
				"", test);

		gatewayServer.postJob(start_time_ts, end_time_ts, organization_id, source_id, source_id, rps_id, destination_id,
				policy_id, "backup", Job_method.full.toString(), JobStatus.finished.toString(), validToken, test);

		test.log(LogStatus.INFO, "create report schedule ");
		scheduleInfo = spogReportServer.composeReportScheduleInfo("report_name", "data_transfer", "last_7_days",
				System.currentTimeMillis(), System.currentTimeMillis(), "monthly", "", organization_id,
				"a@gmail.com,b@gmail.com", null, "all_sources");
		spogReportServer.createReportScheduleWithCheck(validToken, scheduleInfo, SpogConstants.SUCCESS_GET_PUT_DELETE,
				null, test);

		scheduleInfo.put("create_user", spogServer.getLoggedinUser_EmailId());
		expectedReportsInfo.add(scheduleInfo);

		Response response = spogReportServer.getReports(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		if (orgType.equals("direct")) {
			direct_report_id = response.then().extract().path("data.get(0).report_id");
		} else if (orgType.equals("suborg")) {
			sub_report_id = response.then().extract().path("data.get(0).report_id");
		} else if (orgType.equals("rootsub")) {
			rootsub_report_id = response.then().extract().path("data.get(0).report_id");
		} else if (orgType.equals("submsp_sub")) {
			submsp_sub_report_id = response.then().extract().path("data.get(0).report_id");
		}
	}

	@DataProvider(name = "DTSinput")
	public Object[][] DTSinput() {
		return new Object[][] {
				// csr_readonly cases
				{ "Get Data Transfer Report Summary in direct org & csr_readonly token", ti.csr_readonly_token,
						"organization_id=" + ti.direct_org1_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data Transfer Report Summary in direct org filter with report_id & csr_readonly token",
						ti.csr_readonly_token,
						"organization_id=" + ti.direct_org1_id + "&" + "report_id=" + direct_report_id,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data Transfer Report Summary in sub org & csr_readonly token", ti.csr_readonly_token,
						"organization_id=" + ti.normal_msp1_suborg1_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data Transfer Report Summary in sub org filter with report_id & csr_readonly token",
						ti.csr_readonly_token,
						"organization_id=" + ti.normal_msp1_suborg1_id + "&" + "report_id=" + sub_report_id,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },

				{ "Get Data Transfer Report Summary in direct org", ti.direct_org1_user1_token, "",
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data Transfer Report Summary in direct org filter with report_id", ti.direct_org1_user1_token,
						"report_id=" + direct_report_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data Transfer Report Summary in sub org filter with report_id",
						ti.normal_msp1_suborg1_user1_token, "report_id=" + sub_report_id,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data Transfer Report Summary in sub org", ti.normal_msp1_suborg1_user1_token, "",
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data Transfer Report Summary in sub org with msp token", ti.normal_msp_org1_user1_token, "",
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data Transfer Report Summary in sub org with msp_account_admin token",
						ti.normal_msp_org1_msp_accountadmin1_token, "", SpogConstants.SUCCESS_GET_PUT_DELETE, null },

				{ "Get Data Transfer Report Summary with invalid token", "invalid", "", SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT },
				{ "Get Data Transfer Report Summary with missing token", "", "", SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTHENTICATION_FAILED },
				// 3 tier cases

				{ "Get Data Transfer Report Summary in rootsub org filter with report_id",
						ti.root_msp1_suborg1_user1_token, "report_id=" + rootsub_report_id,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data Transfer Report Summary in rootsub org", ti.root_msp1_suborg1_user1_token, "",
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data Transfer Report Summary in rootsub org with msp token", ti.root_msp_org1_user1_token, "",
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data Transfer Report Summary in rootsub org with msp_account_admin token",
						ti.root_msp_org1_msp_accountadmin1_token, "", SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data Transfer Report Summary in rootsub org & csr_readonly token", ti.csr_readonly_token,
						"organization_id=" + ti.root_msp1_suborg1_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data Transfer Report Summary in rootsub org filter with report_id & csr_readonly token",
						ti.csr_readonly_token,
						"organization_id=" + ti.root_msp1_suborg1_id + "&" + "report_id=" + rootsub_report_id,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },

				{ "Get Data Transfer Report Summary in submsp_sub org filter with report_id",
						ti.root_msp1_submsp1_user1_token, "report_id=" + submsp_sub_report_id,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data Transfer Report Summary in submsp_sub org", ti.root_msp1_submsp1_user1_token, "",
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data Transfer Report Summary in submsp_sub org with msp token", ti.root_msp1_submsp1_user1_token,
						"", SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data Transfer Report Summary in submsp_sub org with msp_account_admin token",
						ti.root_msp1_submsp1_account_admin_token, "", SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data Transfer Report Summary in submsp_sub org & csr_readonly token", ti.csr_readonly_token,
						"organization_id=" + ti.root_msp1_submsp_org1_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Get Data Transfer Report Summary in submsp_sub org filter with report_id & csr_readonly token",
						ti.csr_readonly_token,
						"organization_id=" + ti.root_msp1_submsp_org1_id + "&" + "report_id=" + submsp_sub_report_id,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },

		};
	}

	@Test(dataProvider = "DTSinput", dependsOnMethods = "postData")
	public void getDataTransferReportSummaryValid(String caseType, String validToken, String additionalURL,
			int expectedStatusCode, SpogMessageCode expectedErrorMessage) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());

		test.log(LogStatus.INFO, caseType);
		spogReportServer.getDataTransferReportSummaryWithCheck(validToken, additionalURL, expectedStatusCode,
				expectedErrorMessage, test);

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
		response = spogReportServer.getReports(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		ArrayList<String> reports = new ArrayList<>();
		reports = response.then().extract().path("data.report_id");

		if (!reports.isEmpty())
			reports.stream().forEach(report -> {
				spogReportServer.deleteReportsByIdWithCheck(validToken, report, SpogConstants.SUCCESS_GET_PUT_DELETE,
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
