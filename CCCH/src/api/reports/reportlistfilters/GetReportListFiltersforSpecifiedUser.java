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

public class GetReportListFiltersforSpecifiedUser extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private SPOGReportServer spogReportServer;
	private ExtentTest test;
	private TestOrgInfo ti;
	private ArrayList<String> filter_ids = new ArrayList<>();
	private ArrayList<HashMap<String, Object>> directExpData = new ArrayList<HashMap<String, Object>>();
	private ArrayList<HashMap<String, Object>> csrROExpData = new ArrayList<HashMap<String, Object>>();
	private ArrayList<HashMap<String, Object>> csrExpData = new ArrayList<HashMap<String, Object>>();
	private ArrayList<HashMap<String, Object>> mspExpData = new ArrayList<HashMap<String, Object>>();
	private ArrayList<HashMap<String, Object>> suborgExpData = new ArrayList<HashMap<String, Object>>();
	private ArrayList<HashMap<String, Object>> sub_mspAccAdm_expData = new ArrayList<HashMap<String, Object>>();

	@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runningMachine, String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		spogReportServer = new SPOGReportServer(baseURI, port);
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
	}

	/*@Test
	public void deleteReportListFilters() {
		Response response = spogReportServer.getReportListFiltersForLoggedInUser(ti.csr_readonly_token, null,
				SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		ArrayList<HashMap<String, Object>> filters = response.then().extract().path("data");

		for (int i = 0; i < filters.size(); i++) {
			filter_ids.add(filters.get(i).get("filter_id").toString());
		}

		for (int i = 0; i < filter_ids.size(); i++) {
			spogReportServer.deleteReportListFiltersForLoggedInUserByFilterIdWithCheck(ti.csr_readonly_token,
					filter_ids.get(i), SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		}
	}
*/
	@DataProvider(name = "createreportslistfilters", parallel = false)
	public final Object[][] createreportslistfilters() {

		return new Object[][] {

				{ "Create report list filters for an organization with all filters with csr_readonly token",
						ti.csr_org_id, ti.csr_readonly_admin_user_id, ti.csr_readonly_token,
						spogServer.ReturnRandom("report"), "last_7_days", "selected_organizations", "weekly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2 },
				{ "Create report list filters for an organization without type filter with csr_readonly token",
						ti.csr_org_id, ti.csr_readonly_admin_user_id, ti.csr_readonly_token,
						spogServer.ReturnRandom("report"), "last_7_days", "", "weekly", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 4 },
				{ "Create report list filters for an organization without DateRangeType filter with csr_readonly token",
						ti.csr_org_id, ti.csr_readonly_admin_user_id, ti.csr_readonly_token,
						spogServer.ReturnRandom("report"), "", "selected_organizations", "monthly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2 },
				{ "Create report list filters for an organization without Generated_time filter with csr_readonly token",
						ti.csr_org_id, ti.csr_readonly_admin_user_id, ti.csr_readonly_token,
						spogServer.ReturnRandom("report"), "last_7_days", "selected_source_groups", "monthly", -1, -1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 4 },
				{ "Create report list filters for an organization with is_default false with csr_readonly token",
						ti.csr_org_id, ti.csr_readonly_admin_user_id, ti.csr_readonly_token,
						spogServer.ReturnRandom("report"), "last_7_days", "selected_source_groups", "monthly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						false, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 4 },
				{ "Create report list filters for an organization without schedule frequency filter with csr_readonly token",
						ti.csr_org_id, ti.csr_readonly_admin_user_id, ti.csr_readonly_token,
						spogServer.ReturnRandom("report"), "last_7_days", "selected_source_groups", "",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 4 },
				{ "Create report list filters for an organization with multiple Daterangetypes filters with csr_readonly token",
						ti.csr_org_id, ti.csr_readonly_admin_user_id, ti.csr_readonly_token,
						spogServer.ReturnRandom("report"), "last_1_month,last_3_months", "all_sources", "daily",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 4 },

				{ "Create report list filters for direct organization with all filters", ti.direct_org1_id,
						ti.direct_org1_user1_id, ti.direct_org1_user1_token, spogServer.ReturnRandom("report"),
						"last_7_days", "selected_organizations", "weekly", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2 },
				{ "Create report list filters for direct organization without type filter", ti.direct_org1_id,
						ti.direct_org1_user1_id, ti.direct_org1_user1_token, spogServer.ReturnRandom("report"),
						"last_7_days", "", "weekly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 4 },
				{ "Create report list filters for msp organization", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_id,
						ti.normal_msp_org1_user1_token, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_source_groups", "monthly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 3 },
				{ "Create report list filters for msp organization without DateRangeType filter", ti.normal_msp_org1_id,
						ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_token, spogServer.ReturnRandom("report"),
						"", "selected_organizations", "monthly", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2 },
				{ "Create report list filters for msp organization without Generated_time filter",
						ti.normal_msp_org1_id, ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_token,
						spogServer.ReturnRandom("report"), "last_7_days", "selected_source_groups", "monthly", -1, -1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 4 },
				{ "Create report list filters for msp organization with is_default false", ti.normal_msp_org1_id,
						ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_token, spogServer.ReturnRandom("report"),
						"last_7_days", "selected_source_groups", "monthly", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), false,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 4 },
				{ "Create report list filters for sub organization without schedule frequency filter",
						ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg1_user1_token,
						spogServer.ReturnRandom("report"), "last_7_days", "selected_source_groups", "",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 4 },
				{ "Create report list filters for sub organization without type filter", ti.normal_msp1_suborg1_id,
						ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg1_user1_token,
						spogServer.ReturnRandom("report"), "last_7_days", "", "daily", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 4 },
				{ "Create report list filters for sub organization with multiple Daterangetypes filters",
						ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg1_user1_token,
						spogServer.ReturnRandom("report"), "last_1_month,last_3_months", "all_sources", "daily",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 4 },
				{ "Create report list filters for sub organization with multiple Daterangetypes filters with msp_account_admin token",
						ti.normal_msp1_suborg1_id, ti.normal_msp_org1_msp_accountadmin1_id,
						ti.normal_msp_org1_msp_accountadmin1_token, spogServer.ReturnRandom("report"),
						"last_1_month,last_3_months", "all_sources", "daily", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 4 },
				{ "Create report list filters for sub organization without schedule frequency filter with msp_account_admin token",
						ti.normal_msp1_suborg1_id, ti.normal_msp_org1_msp_accountadmin1_id,
						ti.normal_msp_org1_msp_accountadmin1_token, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_source_groups", "", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 4 },

				// 3 tier cases
				{ "Create report list filters for root msp organization", ti.root_msp_org1_id,
						ti.root_msp_org1_user1_id, ti.root_msp_org1_user1_token, spogServer.ReturnRandom("report"),
						"last_7_days", "selected_source_groups", "monthly", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				{ "Create report list filters for root msp organization without DateRangeType filter",
						ti.root_msp_org1_id, ti.root_msp_org1_user1_id, ti.root_msp_org1_user1_token,
						spogServer.ReturnRandom("report"), "", "selected_organizations", "monthly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2 },
				{ "Create report list filters for root msp organization without Generated_time filter",
						ti.root_msp_org1_id, ti.root_msp_org1_user1_id, ti.root_msp_org1_user1_token,
						spogServer.ReturnRandom("report"), "last_7_days", "selected_source_groups", "monthly", -1, -1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 4 },
				{ "Create report list filters for root msp organization with is_default false", ti.root_msp_org1_id,
						ti.root_msp_org1_user1_id, ti.root_msp_org1_user1_token, spogServer.ReturnRandom("report"),
						"last_7_days", "selected_source_groups", "monthly", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), false,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 4 },
				{ "Create report list filters for root sub organization without schedule frequency filter",
						ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_id, ti.root_msp1_suborg1_user1_token,
						spogServer.ReturnRandom("report"), "last_7_days", "selected_source_groups", "",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 4 },
				{ "Create report list filters for root sub organization without type filter", ti.root_msp1_suborg1_id,
						ti.root_msp1_suborg1_user1_id, ti.root_msp1_suborg1_user1_token,
						spogServer.ReturnRandom("report"), "last_7_days", "", "daily", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 4 },
				{ "Create report list filters for root sub organization with multiple Daterangetypes filters",
						ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_id, ti.root_msp1_suborg1_user1_token,
						spogServer.ReturnRandom("report"), "last_1_month,last_3_months", "all_sources", "daily",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 4 },
				{ "Create report list filters for root sub organization with multiple Daterangetypes filters with msp_account_admin token",
						ti.root_msp1_suborg1_id, ti.root_msp_org1_msp_accountadmin1_id,
						ti.root_msp_org1_msp_accountadmin1_token, spogServer.ReturnRandom("report"),
						"last_1_month,last_3_months", "all_sources", "daily", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 4 },
				{ "Create report list filters for root sub organization without schedule frequency filter with msp_account_admin token",
						ti.root_msp1_suborg1_id, ti.root_msp_org1_msp_accountadmin1_id,
						ti.root_msp_org1_msp_accountadmin1_token, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_source_groups", "", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 4 },

				{ "Create report list filters for submsp_sub organization without type filter",
						ti.msp1_submsp1_sub_org1_id, ti.root_msp1_submsp1_user1_id, ti.root_msp1_submsp1_user1_token,
						spogServer.ReturnRandom("report"), "last_7_days", "", "daily", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 4 },
				{ "Create report list filters for submsp_sub organization with multiple Daterangetypes filters",
						ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_id,
						ti.msp1_submsp1_suborg1_user1_token, spogServer.ReturnRandom("report"),
						"last_1_month,last_3_months", "all_sources", "daily", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 4 },
				{ "Create report list filters for submsp_sub organization with multiple Daterangetypes filters with msp_account_admin token",
						ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_id,
						ti.root_msp1_submsp1_account_admin_token, spogServer.ReturnRandom("report"),
						"last_1_month,last_3_months", "all_sources", "daily", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 4 },
				{ "Create report list filters for submsp_sub organization without schedule frequency filter with msp_account_admin token",
						ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_id,
						ti.root_msp1_submsp1_account_admin_token, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_source_groups", "", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 4 },

		};
	}

	// Valid cases - 201

	@Test(dataProvider = "createreportslistfilters"/*, dependsOnMethods = { "deleteReportListFilters" }*/)
	public void createReportListFilters_201(String caseType, String organization_ids, String user_id, String validToken,
			String ReportName, String DateRangeTypes, String type, String schedule_frequency, long generated_ts_start,
			long generated_ts_end, String filter_name, Boolean is_default, String groupName, String groupDescription,
			int noOfSourceGroupsToCreate) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		ArrayList<String> OrgIds = new ArrayList<>();

		test.assignAuthor("");

		spogServer.setToken(validToken);
		
		

		// orgId for creating source_groups
		String orgId = null;
		/*
		 * if (caseType.contains("sub")) { orgId = ti.normal_msp1_suborg1_id; // hard
		 * coding the organization id for case msp_account_admin } else { orgId =
		 * spogServer.GetLoggedinUserOrganizationID(); }
		 */
		String[] org_ids = organization_ids.split(",");

		if (org_ids.length != 1) {
			orgId = org_ids[0];
		} else {
			orgId = org_ids.toString();
		}
			
		orgId=spogServer.GetOrganizationIDforUser(user_id);

		String source_group_ids = null, source_group_id = null;
		/*
		 * if (!caseType.contains("csr")) { for (int i = 0; i <
		 * noOfSourceGroupsToCreate; i++) { source_group_id =
		 * spogServer.createGroupWithCheck(orgId, groupName + i, groupDescription,
		 * test); if (i > 0) { source_group_ids = source_group_ids + "," +
		 * source_group_id; } else { source_group_ids = source_group_id; } } }
		 */

		spogReportServer.setToken(validToken);

		test.log(LogStatus.INFO, "Composing Filter List for Reports");
		HashMap<String, Object> reportlistfilters = spogReportServer.composeReportListFilterInfo(ReportName,
				DateRangeTypes, type, source_group_ids, organization_ids, schedule_frequency, generated_ts_start,
				generated_ts_end, filter_name, is_default);

		test.log(LogStatus.INFO, caseType);
		Response response = spogReportServer.createReportListFiltersForSpecifiedUserWithCheck(validToken, user_id,
				orgId, reportlistfilters, SpogConstants.SUCCESS_POST, null, test);

		String filter_id = response.then().extract().path("data.filter_id");
		test.log(LogStatus.INFO, "The filter id is " + filter_id);

		reportlistfilters = new HashMap<>();
		reportlistfilters = response.then().extract().path("data");

		filter_ids.add(filter_id);

		// Add the created filters to the expectedData arraylist for each organizations
		// separately.
		if (caseType.contains("direct")) {
			directExpData.add(reportlistfilters);
		} else if (caseType.contains("csr_readonly")) {
			csrROExpData.add(reportlistfilters);
		} else if (caseType.contains("csr")) {
			csrExpData.add(reportlistfilters);
		} else if (caseType.contains("msp organization")) {
			mspExpData.add(reportlistfilters);
		} else if (!caseType.contains("msp_account_admin")) {
			suborgExpData.add(reportlistfilters);
		} else if (caseType.contains("msp_account_admin")) {
			sub_mspAccAdm_expData.add(reportlistfilters);
		}

	}

	@DataProvider(name = "getreportslistfilters", parallel = false)
	public final Object[][] getreportslistfilters() {

		return new Object[][] {
				/*
				 * {"csr",csr_user_id,csr_token,""},
				 * {"csr",csr_user_id,csr_token,"is_default;=;false"},
				 * {"csr",csr_user_id,csr_token,"is_default;=;false"},
				 */
				{ "csr_readonly", ti.csr_readonly_admin_user_id, ti.csr_readonly_token, "none" },
				/*{ "csr_readonly", ti.csr_readonly_admin_user_id, ti.csr_readonly_token, "is_default;=;false" },
				{ "csr_readonly", ti.csr_readonly_admin_user_id, ti.csr_readonly_token, "is_default;=;false" },

				{ "direct", ti.direct_org1_user1_id, ti.direct_org1_user1_token, "is_default;=;false" },
				{ "msporg", ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_token, "is_default;=;false" },
				{ "suborg", ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg1_user1_token, "is_default;=;false" },
				{ "suborg-mspAccAdminToken", ti.normal_msp_org1_msp_accountadmin1_id,
						ti.normal_msp_org1_msp_accountadmin1_token, "is_default;=;false", },
				{ "direct", ti.direct_org1_user1_id, ti.direct_org1_user1_token, "is_default;=;true" },
				{ "msporg", ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_token, "is_default;=;true" },
				{ "suborg", ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg1_user1_token, "is_default;=;true" },
				{ "suborg-mspAccAdminToken", ti.normal_msp_org1_msp_accountadmin1_id,
						ti.normal_msp_org1_msp_accountadmin1_token, "is_default;=;true", },*/
				{ "direct", ti.direct_org1_user1_id, ti.direct_org1_user1_token, "none" },
				{ "msporg", ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_token,"none"  },
				{ "suborg", ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg1_user1_token,"none" },
				{ "suborg-mspAccAdminToken", ti.normal_msp_org1_msp_accountadmin1_id,
						ti.normal_msp_org1_msp_accountadmin1_token, "none" }, };
	}

	// Valid cases - 200

	@Test(dataProvider = "getreportslistfilters")
	public void getReportListFiltersforSpecifiedUser_200(String organizationType, String user_id, String validToken,
			String filterStr) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);
		ArrayList<HashMap<String, Object>> expectedData = new ArrayList<>();

		if (organizationType.contains("direct")) {
			expectedData = directExpData;
		} else if (organizationType.contains("csr_readonly")) {
			expectedData = csrROExpData;
		} else if (organizationType.contains("csr")) {
			expectedData = csrExpData;
		} else if (organizationType.contains("msporg")) {
			expectedData = mspExpData;
		} else if ((!organizationType.contains("mspAccAdminToken"))) {
			expectedData = suborgExpData;
		} else if (organizationType.contains("mspAccAdminToken")) {
			expectedData = sub_mspAccAdm_expData;
		}

		test.log(LogStatus.INFO, "Get Filter List for Reports in org: " + organizationType);
		spogReportServer.getReportListFiltersForSpecifiedUserWithCheck(validToken, user_id, expectedData, filterStr,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

	}

	@DataProvider(name = "getReportListFiltersforSpecifiedUserInvalid")
	public Object[][] getReportListFiltersforSpecifiedUserInvalid() {
		return new Object[][] {
				// 400 cases - invalid filter_id
				{ "Get report list filters by filter id for specified user with invlaid filter id for direct organization",
						"invaliduserid", ti.direct_org1_user1_token, SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_IS_NOT_UUID },
				{ "Get report list filters by filter id for specified user with invlaid filter id for msp organization",
						"invaliduserid", ti.normal_msp_org1_user1_token, SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_IS_NOT_UUID },
				{ "Get report list filters by filter id for specified user with invlaid filter id for sub organization",
						"invaliduserid", ti.normal_msp1_suborg1_user1_token, SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_IS_NOT_UUID },
				{ "Get report list filters by filter id for specified user with invlaid filter id for sub organization with msp_account_admin token",
						"invaliduserid", ti.normal_msp_org1_msp_accountadmin1_token,
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID },

				// 401 cases - invalid / missing token
				{ "Get report list filters by filter id for specified user with invlaid token",
						UUID.randomUUID().toString(), "invalidtoken", SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT },
				{ "Get report list filters by filter id for specified user with missing token",
						UUID.randomUUID().toString(), "", SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTHENTICATION_FAILED },

				// 404 cases - filter_id that does not exist
				{ "Get report list filters by filter id for specified user with user id that does not exist for direct organization",
						UUID.randomUUID().toString(), ti.direct_org1_user1_token, SpogConstants.RESOURCE_NOT_EXIST,
						SpogMessageCode.USER_ID_DOESNOT_EXIST },
				{ "Get report list filters by filter id for specified user with user id that does not exist for msp organization",
						UUID.randomUUID().toString(), ti.normal_msp_org1_user1_token, SpogConstants.RESOURCE_NOT_EXIST,
						SpogMessageCode.USER_ID_DOESNOT_EXIST },
				{ "Get report list filters by filter id for specified user with user id that does not exist for sub organization",
						UUID.randomUUID().toString(), ti.normal_msp1_suborg1_user1_token,
						SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.USER_ID_DOESNOT_EXIST },
				{ "Get report list filters by filter id for specified user with user id that does not exist for sub organization with msp_account_admin token",
						UUID.randomUUID().toString(), ti.normal_msp_org1_msp_accountadmin1_token,
						SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.USER_ID_DOESNOT_EXIST }, };
	}

	// Invalid case - 401

	@Test(dataProvider = "getReportListFiltersforSpecifiedUserInvalid")
	public void getReportListFiltersforSpecifiedUser_400_401_404(String caseType, String user_id, String token,
			int expectedStatusCode, SpogMessageCode expectedErrorMessage) {

		String filterStr = null;
		ArrayList<HashMap<String, Object>> expData = new ArrayList<>();
		test.log(LogStatus.INFO, caseType);
		spogReportServer.getReportListFiltersForSpecifiedUserWithCheck(token, user_id, expData, filterStr,
				expectedStatusCode, expectedErrorMessage, test);

	}

	// Invalid case - 403(Insufficient permissions)

	@DataProvider(name = "getreportslistfilters_403", parallel = false)
	public final Object[][] getreportslistfilters_403() {

		return new Object[][] { { "direct-msp", ti.direct_org1_user1_id, ti.normal_msp_org1_user1_token },
				{ "direct-suborg", ti.direct_org1_user1_id, ti.normal_msp1_suborg1_user1_token },
				{ "direct-msp_maa", ti.direct_org1_user1_id, ti.normal_msp_org1_msp_accountadmin1_token },
				{ "msp-direct", ti.normal_msp_org1_user1_id, ti.direct_org1_user1_token },
				{ "msp-suborg", ti.normal_msp_org1_user1_id, ti.normal_msp1_suborg1_user1_token },
				{ "msp-msp_maa", ti.normal_msp_org1_user1_id, ti.normal_msp_org1_msp_accountadmin1_token },
				{ "suborg-direct", ti.normal_msp1_suborg1_user1_id, ti.direct_org1_user1_token },
				{ "suborg-suborg2", ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg2_user1_token },
				{ "msp_maa-direct", ti.normal_msp_org1_msp_accountadmin1_id, ti.direct_org1_user1_token },
				{ "rootmsp-direct", ti.root_msp_org1_user1_id, ti.direct_org1_user1_token },
				{ "rootmsp-suborg", ti.root_msp_org1_user1_id, ti.root_msp1_suborg1_user1_token },
				{ "rootmsp-msp_maa", ti.root_msp_org1_user1_id, ti.root_msp_org1_msp_accountadmin1_token },
				{ "rootsuborg-direct", ti.root_msp1_suborg1_user1_id, ti.direct_org1_user1_token },
				{ "rootsuborg-suborg2", ti.root_msp1_suborg1_user1_id, ti.root_msp1_suborg2_user1_token },
				{ "rootmspmaa-direct", ti.root_msp_org1_msp_accountadmin1_id, ti.direct_org1_user1_token },
				{ "submsp_sub-direct", ti.root_msp1_submsp1_user1_id, ti.direct_org1_user1_token },
				{ "submsp_sub-rootsuborg", ti.root_msp1_submsp1_user1_id, ti.root_msp1_suborg2_user1_token },
				{ "submsp-submsp_sub", ti.root_msp1_submsp1_user1_id, ti.msp1_submsp1_suborg1_user1_token },
				{ "submsp_sub-submsp_suborg2", ti.msp1_submsp1_suborg1_user1_id,
						ti.msp2_submsp1_suborg2_user1_token }, };
	}

	@Test(dataProvider = "getreportslistfilters_403")
	public void getReportListFiltersforSpecifiedUser_403(String organizationType, String user_id, String token) {

		ArrayList<HashMap<String, Object>> expData = new ArrayList<>();
		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		test.log(LogStatus.INFO, "Get Filter List for Reports in org: " + organizationType);
		spogReportServer.getReportListFiltersForSpecifiedUserWithCheck(token, user_id, expData, null,
				SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY, test);

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
