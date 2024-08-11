package api.reports.reportlistfilters;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

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

public class CreateReportListFiltersForLoggedInUserTest extends base.prepare.Is4Org {

	private SPOGServer spogServer;
	private SPOGReportServer spogReportServer;
	private UserSpogServer userSpogServer;
	private ExtentTest test;
	private TestOrgInfo ti;

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

	@DataProvider(name = "createreportlistfilters", parallel = false)
	public final Object[][] createreportlistfilters() {

		return new Object[][] {

				{ "Create report list filters for direct organization with all filters", ti.direct_org1_id,
						ti.direct_org1_user1_token, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_organizations", "weekly", System.currentTimeMillis(),
						System.currentTimeMillis() + 1000, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2 },
				{ "Create report list filters for direct organization without type filter", ti.direct_org1_id,
						ti.direct_org1_user1_token, spogServer.ReturnRandom("report"), "last_7_days", "", "weekly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1000,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 4 },
				{ "Create report list filters for msp organization", ti.normal_msp_org1_id,
						ti.normal_msp_org1_user1_token, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_source_groups", "monthly", System.currentTimeMillis(),
						System.currentTimeMillis() + 1000, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				{ "Create report list filters for msp organization without DateRangeType filter", ti.normal_msp_org1_id,
						ti.normal_msp_org1_user1_token, spogServer.ReturnRandom("report"), "", "selected_organizations",
						"monthly", System.currentTimeMillis(), System.currentTimeMillis() + 1000,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2 },
				{ "Create report list filters for msp organization with is_default false", ti.normal_msp_org1_id,
						ti.normal_msp_org1_user1_token, spogServer.ReturnRandom("report"), "last_1_year",
						"all_organizations", "quarterly", System.currentTimeMillis(), System.currentTimeMillis() + 1000,
						spogServer.ReturnRandom("filter"), false, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2 },
				{ "Create report list filters for sub organization without schedule frequency filter",
						ti.normal_msp1_suborg1_id + "," + ti.normal_msp_org1_id, ti.normal_msp1_suborg1_user1_token,
						spogServer.ReturnRandom("report"), "last_1_month,last_6_months", "all_sources", "",
						System.currentTimeMillis(), System.currentTimeMillis() + 1000,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 3 },
				{ "Create report list filters for sub organization without type filter", ti.normal_msp1_suborg1_id,
						ti.normal_msp1_suborg1_user1_token, spogServer.ReturnRandom("report"), "today,last_7_days", "",
						"daily", System.currentTimeMillis(), System.currentTimeMillis() + 1000,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 3 },
				{ "Create report list filters for sub organization with multiple Daterangetypes filters",
						ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token,
						spogServer.ReturnRandom("report"), "last_1_month,last_3_months", "all_sources", "quarterly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1000,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 3 },
				{ "Create report list filters for sub organization with msp_account_admin token with multiple Daterangetypes filters",
						ti.normal_msp1_suborg1_id, ti.normal_msp_org1_msp_accountadmin1_token,
						spogServer.ReturnRandom("report"), "last_1_month,last_3_months", "all_sources", "quarterly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1000,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 3 },
				// csr read only cases
				{ "Create report list filters for csrreadonly organization with csrreadonly token with multiple Daterangetypes filters",
						ti.csr_org_id, ti.csr_readonly_token, spogServer.ReturnRandom("report"),
						"last_1_month,last_3_months", "all_sources", "quarterly", System.currentTimeMillis(),
						System.currentTimeMillis() + 1000, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				{ "Create report list filters for csrreadonly organization without schedule frequency filter",
						ti.csr_org_id + "," + ti.csr_org_id, ti.csr_readonly_token, spogServer.ReturnRandom("report"),
						"last_1_month,last_6_months", "all_sources", "", System.currentTimeMillis(),
						System.currentTimeMillis() + 1000, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				{ "Create report list filters for csrreadonly organization with is_default false", ti.csr_org_id,
						ti.csr_readonly_token, spogServer.ReturnRandom("report"), "last_1_year", "all_organizations",
						"quarterly", System.currentTimeMillis(), System.currentTimeMillis() + 1000,
						spogServer.ReturnRandom("filter"), false, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2 },
				{ "Create report list filters for csrreadonly organization", ti.csr_org_id, ti.csr_readonly_token,
						spogServer.ReturnRandom("report"), "last_7_days", "selected_source_groups", "monthly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1000,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 3 },
				{ "Create report list filters for csrreadonly organization without DateRangeType filter", ti.csr_org_id,
						ti.csr_readonly_token, spogServer.ReturnRandom("report"), "", "selected_organizations",
						"monthly", System.currentTimeMillis(), System.currentTimeMillis() + 1000,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2 },

				// 3 tier cases

				{ "Create report list filters for root msp organization", ti.root_msp_org1_id,
						ti.root_msp_org1_user1_token, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_source_groups", "monthly", System.currentTimeMillis(),
						System.currentTimeMillis() + 1000, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				{ "Create report list filters for root msp organization without DateRangeType filter",
						ti.root_msp_org1_id, ti.root_msp_org1_user1_token, spogServer.ReturnRandom("report"), "",
						"selected_organizations", "monthly", System.currentTimeMillis(),
						System.currentTimeMillis() + 1000, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2 },
				{ "Create report list filters for root msp organization with is_default false", ti.root_msp_org1_id,
						ti.root_msp_org1_user1_token, spogServer.ReturnRandom("report"), "last_1_year",
						"all_organizations", "quarterly", System.currentTimeMillis(), System.currentTimeMillis() + 1000,
						spogServer.ReturnRandom("filter"), false, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2 },
				/*{ "Create report list filters for root sub organization without schedule frequency filter",
						ti.root_msp1_suborg1_id + "," + ti.root_msp_org1_id, ti.root_msp1_suborg1_user1_token,
						spogServer.ReturnRandom("report"), "last_1_month,last_6_months", "all_sources", "",
						System.currentTimeMillis(), System.currentTimeMillis() + 1000,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 3 },
				{ "Create report list filters for root sub organization without type filter", ti.root_msp1_suborg1_id,
						ti.root_msp1_suborg1_user1_token, spogServer.ReturnRandom("report"), "today,last_7_days", "",
						"daily", System.currentTimeMillis(), System.currentTimeMillis() + 1000,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 3 },
				{ "Create report list filters for root sub organization with multiple Daterangetypes filters",
						ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_token, spogServer.ReturnRandom("report"),
						"last_1_month,last_3_months", "all_sources", "quarterly", System.currentTimeMillis(),
						System.currentTimeMillis() + 1000, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },*/
				/*{ "Create report list filters for root sub organization with msp_account_admin token with multiple Daterangetypes filters",
						ti.root_msp1_suborg1_id, ti.root_msp_org1_msp_accountadmin1_token,
						spogServer.ReturnRandom("report"), "last_1_month,last_3_months", "all_sources", "quarterly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1000,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 3 },
				{ "Create report list filters for submsp sub organization with submsp msp_account_admin token with multiple Daterangetypes filters",
						ti.msp1_submsp1_sub_org1_id, ti.root_msp1_submsp1_account_admin_token,
						spogServer.ReturnRandom("report"), "last_1_month,last_3_months", "all_sources", "quarterly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1000,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 3 },
				{ "Create report list filters for submsp organization without DateRangeType filter",
						ti.msp1_submsp1_sub_org1_id, ti.root_msp1_submsp1_user1_token,
						spogServer.ReturnRandom("report"), "", "selected_organizations", "monthly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1000,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2 },*/
				{ "Create report list filters for root msp organization with is_default false",
						ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_token,
						spogServer.ReturnRandom("report"), "last_1_year", "all_organizations", "quarterly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1000,
						spogServer.ReturnRandom("filter"), false, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2 },

		};
	}

	// Valid cases - 201
	@Test(dataProvider = "createreportlistfilters")
	public void createReportListFilters_201(String caseType, String organization_ids, String validToken,
			String ReportName, String DateRangeTypes, String type, String schedule_frequency, long generated_ts_start,
			long generated_ts_end, String filter_name, Boolean is_default, String groupName, String groupDescription,
			int noOfSourceGroupsToCreate) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		ArrayList<String> OrgIds = new ArrayList<>();

		test.assignAuthor("Kanamarlapudi, Chandra Kanth");

		spogServer.setToken(validToken);

		// orgId for creating source_groups
		String orgId = null;
		if (caseType.contains("sub")) {
			orgId = ti.normal_msp1_suborg1_id;

		} else {
			orgId = spogServer.GetLoggedinUserOrganizationID();
		}

		String source_group_ids = null, source_group_id = null;
	/*	if (!(caseType.contains("csrreadonly"))) {
			for (int i = 0; i < noOfSourceGroupsToCreate; i++) {
				source_group_id = spogServer.createGroupWithCheck(orgId, groupName + i, groupDescription, test);
				if (i > 0) {
					source_group_ids = source_group_ids + "," + source_group_id;
				} else {
					source_group_ids = source_group_id;
				}
			}
		}*/

		spogReportServer.setToken(validToken);

		test.log(LogStatus.INFO, "Composing Filter List for Reports");
		HashMap<String, Object> reportlistfilters = spogReportServer.composeReportListFilterInfo(ReportName,
				DateRangeTypes, type, source_group_ids, organization_ids, schedule_frequency, generated_ts_start,
				generated_ts_end, filter_name, is_default);

		test.log(LogStatus.INFO, caseType);
		Response response = spogReportServer.createReportListFiltersForLoggedInUserWithCheck(validToken,
				reportlistfilters, SpogConstants.SUCCESS_POST, null, test);

		String filter_id = response.then().extract().path("data.filter_id");
		test.log(LogStatus.INFO, "The filter id is " + filter_id);

		test.log(LogStatus.INFO, "Deleting Filter List for Reports");
		spogReportServer.deleteReportListFiltersForLoggedInUserByFilterIdWithCheck(validToken, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

	}

	@DataProvider(name = "createReportListFiltersInvalid")
	public Object[][] createReportListFiltersInvalid() {

		return new Object[][] {
				{ "Create report list filters with invalid token", "INVALID TOKEN", SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT },
				{ "Create report list filters with missing token", "", SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTHENTICATION_FAILED }, };
	}

	// Invalid cases - 401

	@Test(dataProvider = "createReportListFiltersInvalid")
	public void createReportListFilters_401(String caseType, String token, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage) {

		HashMap<String, Object> reportlistfilters = new HashMap<>();

		test.log(LogStatus.INFO, caseType);
		spogReportServer.createReportListFiltersForLoggedInUserWithCheck(token, reportlistfilters, expectedStatusCode,
				expectedErrorMessage, test);

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
