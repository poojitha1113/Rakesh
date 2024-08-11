package api.reports.reportlistfilters;

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

import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.SPOGReportServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class GetReportListFiltersforLoggedInUser extends base.prepare.Is4Org {

	private SPOGServer spogServer;
	private SPOGReportServer spogReportServer;
	private UserSpogServer userSpogServer;
	private ExtentTest test;
	private TestOrgInfo ti;
	private ArrayList<String> filter_ids = new ArrayList<>();
	private ArrayList<HashMap<String, Object>> ExpData = new ArrayList<HashMap<String, Object>>();

	@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runningMachine, String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		spogReportServer = new SPOGReportServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		rep = ExtentManager.getInstance(this.getClass().getSimpleName(), logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Kanamarlapudi, Chandra Kanth";

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
	}

	@DataProvider(name = "createreportslistfilters", parallel = false)
	public final Object[][] createreportslistfilters() {

		return new Object[][] {
				// csr_readonly
				{ "Create report list filters for an organization with all filters using csr_readonly token",
						ti.csr_org_id, ti.csr_readonly_token, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_organizations", "weekly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2 },
				{ "Create report list filters for an organization without type filter with csr_readonly token",
						ti.csr_org_id, ti.csr_readonly_token, spogServer.ReturnRandom("report"), "last_7_days", "",
						"weekly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 4 },
				{ "Create report list filters for an organization without DateRangeType filter using csr_readonly token",
						ti.csr_org_id, ti.csr_readonly_token, spogServer.ReturnRandom("report"), "",
						"selected_organizations", "monthly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2 },
				{ "Create report list filters for an organization without Generated_time filter using csr_readonly token",
						ti.csr_org_id, ti.csr_readonly_token, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_source_groups", "monthly", -1, -1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 4 },
				{ "Create report list filters for an organization with is_default false using csr_readonly token",
						ti.csr_org_id, ti.csr_readonly_token, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_source_groups", "monthly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), false, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 4 },
				{ "Create report list filters for an organization without schedule frequency filter using csr_readonly token",
						ti.csr_org_id, ti.csr_readonly_token, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_source_groups", "", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 4 },
				{ "Create report list filters for an organization with multiple Daterangetypes filters using csr_readonly token",
						ti.csr_org_id, ti.csr_readonly_token, spogServer.ReturnRandom("report"),
						"last_1_month,last_3_months", "all_sources", "daily", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 4 },

			/*	{ "Create report list filters for direct organization with all filters", ti.direct_org1_id,
						ti.direct_org1_user1_token, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_organizations", "weekly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2 },
				{ "Create report list filters for direct organization without type filter", ti.direct_org1_id,
						ti.direct_org1_user1_token, spogServer.ReturnRandom("report"), "last_7_days", "", "weekly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 4 },
				{ "Create report list filters for msp organization", ti.normal_msp_org1_id,
						ti.normal_msp_org1_user1_token, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_source_groups", "monthly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 3 },
				{ "Create report list filters for msp organization without DateRangeType filter", ti.normal_msp_org1_id,
						ti.normal_msp_org1_user1_token, spogServer.ReturnRandom("report"), "", "selected_organizations",
						"monthly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2 },
				{ "Create report list filters for msp organization without Generated_time filter",
						ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token, spogServer.ReturnRandom("report"),
						"last_7_days", "selected_source_groups", "monthly", -1, -1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 4 },
				{ "Create report list filters for msp organization with is_default false", ti.normal_msp_org1_id,
						ti.normal_msp_org1_user1_token, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_source_groups", "monthly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), false, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 4 },
				{ "Create report list filters for sub organization without schedule frequency filter",
						ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token,
						spogServer.ReturnRandom("report"), "last_7_days", "selected_source_groups", "",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 4 },
				{ "Create report list filters for sub organization without type filter", ti.normal_msp1_suborg1_id,
						ti.normal_msp1_suborg1_user1_token, spogServer.ReturnRandom("report"), "last_7_days", "",
						"daily", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 4 },
				{ "Create report list filters for sub organization with multiple Daterangetypes filters",
						ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token,
						spogServer.ReturnRandom("report"), "last_1_month,last_3_months", "all_sources", "daily",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 4 },
				{ "Create report list filters for sub organization with multiple Daterangetypes filters with msp_account_admin token",
						ti.normal_msp1_suborg1_id, ti.normal_msp_org1_msp_accountadmin1_token,
						spogServer.ReturnRandom("report"), "last_1_month,last_3_months", "all_sources", "daily",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 4 },
				{ "Create report list filters for sub organization without schedule frequency filter with msp_account_admin token",
						ti.normal_msp1_suborg1_id, ti.normal_msp_org1_msp_accountadmin1_token,
						spogServer.ReturnRandom("report"), "last_7_days", "selected_source_groups", "",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 4 },
*/
				// 3 tier cases
				/*{ "Create report list filters for msp organization", ti.root_msp_org1_id, ti.root_msp_org1_user1_token,
						spogServer.ReturnRandom("report"), "last_7_days", "selected_source_groups", "monthly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				{ "Create report list filters for msp organization without DateRangeType filter", ti.root_msp_org1_id,
						ti.root_msp_org1_user1_token, spogServer.ReturnRandom("report"), "", "selected_organizations",
						"monthly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2 },
				{ "Create report list filters for msp organization without Generated_time filter", ti.root_msp_org1_id,
						ti.root_msp_org1_user1_token, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_source_groups", "monthly", -1, -1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 4 },
				{ "Create report list filters for msp organization with is_default false", ti.root_msp_org1_id,
						ti.root_msp_org1_user1_token, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_source_groups", "monthly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), false, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 4 },
				{ "Create report list filters for sub organization without schedule frequency filter",
						ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_token, spogServer.ReturnRandom("report"),
						"last_7_days", "selected_source_groups", "", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 4 },
				{ "Create report list filters for sub organization without type filter", ti.root_msp1_suborg1_id,
						ti.root_msp1_suborg1_user1_token, spogServer.ReturnRandom("report"), "last_7_days", "", "daily",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 4 },
				{ "Create report list filters for sub organization with multiple Daterangetypes filters",
						ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_token, spogServer.ReturnRandom("report"),
						"last_1_month,last_3_months", "all_sources", "daily", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 4 },
				{ "Create report list filters for sub organization with multiple Daterangetypes filters with msp_account_admin token",
						ti.root_msp1_suborg1_id, ti.root_msp_org1_msp_accountadmin1_token,
						spogServer.ReturnRandom("report"), "last_1_month,last_3_months", "all_sources", "daily",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 4 },
				{ "Create report list filters for sub organization without schedule frequency filter with msp_account_admin token",
						ti.root_msp1_suborg1_id, ti.root_msp_org1_msp_accountadmin1_token,
						spogServer.ReturnRandom("report"), "last_7_days", "selected_source_groups", "",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 4 },*/

		};
	}

	// Valid cases - 201

	@Test(dataProvider = "createreportslistfilters", dependsOnMethods = { "deleteReportListFilters" })
	public void createReportListFilters_201(String caseType, String organization_ids, String validToken,
			String ReportName, String DateRangeTypes, String type, String schedule_frequency, long generated_ts_start,
			long generated_ts_end, String filter_name, Boolean is_default, String groupName, String groupDescription,
			int noOfSourceGroupsToCreate) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());

		spogServer.setToken(validToken);

		// orgId for creating source_groups
		String orgId = null;
		if (caseType.contains("sub")) {
			orgId = ti.normal_msp1_suborg1_id; // hard coding the organization id for case msp_account_admin
		} else {
			orgId = spogServer.GetLoggedinUserOrganizationID();
		}

		spogReportServer.setToken(validToken);

		test.log(LogStatus.INFO, "Composing Filter List for Reports");
		String source_group_ids = null;
		HashMap<String, Object> reportlistfilters = spogReportServer.composeReportListFilterInfo(ReportName,
				DateRangeTypes, type, source_group_ids, organization_ids, schedule_frequency, generated_ts_start,
				generated_ts_end, filter_name, is_default);

		test.log(LogStatus.INFO, caseType);
		Response response = spogReportServer.createReportListFiltersForLoggedInUserWithCheck(validToken,
				reportlistfilters, SpogConstants.SUCCESS_POST, null, test);

		String filter_id = response.then().extract().path("data.filter_id");
		test.log(LogStatus.INFO, "The filter id is " + filter_id);

		filter_ids.add(filter_id);
		ExpData.add(reportlistfilters);

		ArrayList<HashMap<String, Object>> expectedData = new ArrayList<>();

		expectedData = ExpData;

		test.log(LogStatus.INFO, "Get Filter List for Reports in org: ");
		spogReportServer.getReportListFiltersForLoggedInUserWithCheck(validToken, expectedData, "",
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		spogReportServer.deleteReportListFiltersForLoggedInUserByFilterIdWithCheck(validToken, filter_id, SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
	}

	@DataProvider(name = "getreportlist", parallel = false)
	public final Object[][] getreportlist() {

		return new Object[][] {

				{ "csr", ti.csr_token, "" }, { "csr", ti.csr_token, "is_default;=;false" },
				{ "csr", ti.csr_token, "is_default;=;true" },

				{ "csr_readonly", ti.csr_readonly_token, "" },
				{ "csr_readonly", ti.csr_readonly_token, "is_default;=;false" },
				{ "csr_readonly", ti.csr_readonly_token, "is_default;=;true" },

				{ "direct", ti.direct_org1_user1_token, "is_default;=;false" },
				{ "msporg", ti.normal_msp_org1_user1_token, "is_default;=;false" },
				{ "suborg", ti.normal_msp1_suborg1_user1_token, "is_default;=;false" },
				{ "suborg-mspAccAdminToken", ti.normal_msp_org1_msp_accountadmin1_token, "is_default;=;false", },
				{ "direct", ti.direct_org1_user1_token, "is_default;=;true" },
				{ "msporg", ti.normal_msp_org1_user1_token, "is_default;=;true" },
				{ "suborg", ti.normal_msp1_suborg1_user1_token, "is_default;=;true" },
				{ "suborg-mspAccAdminToken", ti.normal_msp_org1_msp_accountadmin1_token, "is_default;=;true", },
				{ "direct", ti.direct_org1_user1_token, "" }, { "msporg", ti.normal_msp_org1_user1_token, "" },
				{ "suborg", ti.normal_msp1_suborg1_user1_token, "" },
				{ "suborg-mspAccAdminToken", ti.normal_msp_org1_msp_accountadmin1_token, "" }, };
	}

/*	// Valid Cases - 200

	@Test(dataProvider = "getreportlist", dependsOnMethods = { "createReportListFilters_201" })
	public void getReportListFiltersforLoggedInUser_200(String organizationType, String validToken, String filterStr) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);
		ArrayList<HashMap<String, Object>> expectedData = new ArrayList<>();

		expectedData = ExpData;

		test.log(LogStatus.INFO, "Get Filter List for Reports in org: " + organizationType);
		spogReportServer.getReportListFiltersForLoggedInUserWithCheck(validToken, expectedData, filterStr,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
	}*/

	@DataProvider(name = "getReportListFiltersforLoggedInUser_401")
	public Object[][] getReportListFiltersforLoggedInUser_401() {
		return new Object[][] {

				// 401 cases - invalid / missing token
				{ "Get report list filters by filter id for loggedin user with invlaid token",
						UUID.randomUUID().toString(), "invalidtoken", SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT },
				{ "Get report list filters by filter id for loggedin user with missing token",
						UUID.randomUUID().toString(), "", SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTHENTICATION_FAILED },

		};
	}

	// Invalid cases - 401
	@Test(dataProvider = "getReportListFiltersforLoggedInUser_401")
	public void getReportListFiltersforLoggedInUser_401(String caseType, String filter_id, String token,
			int expectedStatusCode, SpogMessageCode expectedErrorMessage) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());

		test.log(LogStatus.INFO, caseType);
		spogReportServer.getReportListFiltersForLoggedInUserWithCheck(token, null, "", expectedStatusCode,
				expectedErrorMessage, test);

	}

	@Test
	public void deleteReportListFilters() {
		Response response = spogReportServer.getReportListFiltersForLoggedInUser(ti.csr_readonly_token, null,
				SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		ArrayList<String> filters = response.then().extract().path("data.filter_id");

		if (!filters.isEmpty()) {
			filters.stream().forEach(filter -> {
				spogReportServer.deleteReportListFiltersForLoggedInUserByFilterIdWithCheck(ti.csr_readonly_token,
						filter, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
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
