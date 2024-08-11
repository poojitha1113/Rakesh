package api.reports.reportlistfilters;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang.RandomStringUtils;
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
import InvokerServer.GatewayServer;
import InvokerServer.SPOGReportServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class CreateReportListFiltersforSpecifiedUserTest extends base.prepare.Is4Org {

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

	@DataProvider(name = "createreportslistfilters", parallel = false)
	public final Object[][] createreportslistfilters() {

		return new Object[][] {

				{ "direct", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id,
						spogServer.ReturnRandom("report"), "last_7_days", "selected_organizations", "weekly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						ti.direct_org1_id },
				{ "msp", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,
						spogServer.ReturnRandom("report"), "last_7_days", "selected_source_groups", "monthly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3,
						ti.normal_msp_org1_id },
				{ "suborg", ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token,
						ti.normal_msp1_suborg1_user1_id, spogServer.ReturnRandom("report"),
						"last_1_month,last_3_months", "all_sources", "quarterly", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3,
						ti.normal_msp1_suborg1_id },
				{ "suborg_msp_account_admin_token", ti.normal_msp1_suborg1_id,
						ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp1_suborg1_user1_id,
						spogServer.ReturnRandom("report"), "last_1_month,last_3_months", "all_sources", "quarterly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3,
						ti.normal_msp1_suborg1_id },
				{ "suborg_msptoken", ti.normal_msp1_suborg1_id, ti.normal_msp_org1_user1_token,
						ti.normal_msp1_suborg1_user1_id, spogServer.ReturnRandom("report"),
						"last_1_month,last_3_months", "all_sources", "quarterly", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3,
						ti.normal_msp1_suborg1_id },
				{ "csrreadonly", ti.csr_org_id, ti.csr_readonly_token, ti.csr_readonly_admin_user_id,
						spogServer.ReturnRandom("report"), "last_1_month,last_3_months", "all_sources", "quarterly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3,
						ti.csr_org_id },

		};
	}

	@Test(dataProvider = "createreportslistfilters")
	public void createReportListFilters_201(String organizationType, String organization_ids, String validToken,
			String user_id, String ReportName, String DateRangeTypes, String type, String schedule_frequency,
			long generated_ts_start, long generated_ts_end, String filter_name, boolean is_default, String groupName,
			String groupDescription, int noOfSourceGroupsToCreate, String orgId) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		ArrayList<String> OrgIds = new ArrayList<>();

		test.assignAuthor("");

		spogServer.setToken(validToken);
		String source_group_ids = null, source_group_id = null;
		if (!(organizationType.equalsIgnoreCase("csrreadonly"))) {
			for (int i = 0; i < noOfSourceGroupsToCreate; i++) {
				source_group_id = spogServer.createGroupWithCheck2(validToken, orgId, groupName + i, groupDescription,
						test);
				if (i > 0) {
					source_group_ids = source_group_ids + "," + source_group_id;
				} else {
					source_group_ids = source_group_id;
				}

			}
		}

		test.assignAuthor("");

		spogServer.setToken(validToken);
		test.log(LogStatus.INFO, "Composing Filter List for Reports in org: " + organizationType);
		HashMap<String, Object> reportlistfilters = spogReportServer.composeReportListFilterInfo(ReportName,
				DateRangeTypes, type, source_group_ids, organization_ids, schedule_frequency, generated_ts_start,
				generated_ts_end, filter_name, is_default);

		Response response = spogReportServer.createReportListFiltersForSpecifiedUserWithCheck(validToken, user_id,
				orgId, reportlistfilters, SpogConstants.SUCCESS_POST, null, test);

		String filter_id = response.then().extract().path("data.filter_id");
		test.log(LogStatus.INFO, "The filter id is " + filter_id);

		// deleted the filter
		spogReportServer.deleteReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		spogServer.setToken(validToken);
		// created Reported filetr on all fileds
		test.log(LogStatus.INFO, "Composing Filter List for Reports in org: " + organizationType);
		HashMap<String, Object> reportlistfilters_1 = spogReportServer.composeReportListFilterInfo(ReportName,
				DateRangeTypes, type, source_group_ids, organization_ids, schedule_frequency, generated_ts_start,
				generated_ts_end, filter_name, is_default);

		// verify the report filters for specified user
		response = spogReportServer.createReportListFiltersForSpecifiedUserWithCheck(validToken, user_id, orgId,
				reportlistfilters_1, SpogConstants.SUCCESS_POST, null, test);

		filter_id = response.then().extract().path("data.filter_id");
		test.log(LogStatus.INFO, "The filter id is " + filter_id);

		// deleted the filter
		spogReportServer.deleteReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		// Create filter with the Report name is empty
		test.log(LogStatus.INFO, "Composing Filter List for Reports in org: " + organizationType);
		HashMap<String, Object> reportlistfilters_2 = spogReportServer.composeReportListFilterInfo("", DateRangeTypes,
				type, source_group_ids, organization_ids, schedule_frequency, generated_ts_start, generated_ts_end,
				filter_name, is_default);

		// verify the report filters for specified user
		response = spogReportServer.createReportListFiltersForSpecifiedUserWithCheck(validToken, user_id, orgId,
				reportlistfilters_2, SpogConstants.SUCCESS_POST, null, test);

		filter_id = response.then().extract().path("data.filter_id");
		test.log(LogStatus.INFO, "The filter id is " + filter_id);

		// deleted the filter
		spogReportServer.deleteReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		// Create filter with the updateDateRangeTypes is empty
		test.log(LogStatus.INFO, "Composing Filter List for Reports in org: " + organizationType);
		HashMap<String, Object> reportlistfilters_3 = spogReportServer.composeReportListFilterInfo(ReportName, "", type,
				source_group_ids, organization_ids, schedule_frequency, generated_ts_start, generated_ts_end,
				filter_name, is_default);

		// verify the report filters for specified user
		response = spogReportServer.createReportListFiltersForSpecifiedUserWithCheck(validToken, user_id, orgId,
				reportlistfilters_3, SpogConstants.SUCCESS_POST, null, test);

		filter_id = response.then().extract().path("data.filter_id");
		test.log(LogStatus.INFO, "The filter id is " + filter_id);

		// deleted the filter
		spogReportServer.deleteReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		// Create filter with the schedule_frequency is empty
		test.log(LogStatus.INFO, "Composing Filter List for Reports in org: " + organizationType);
		HashMap<String, Object> reportlistfilters_4 = spogReportServer.composeReportListFilterInfo(ReportName,
				DateRangeTypes, type, source_group_ids, organization_ids, "", generated_ts_start, generated_ts_end,
				filter_name, is_default);

		// verify the report filters for specified user
		response = spogReportServer.createReportListFiltersForSpecifiedUserWithCheck(validToken, user_id, orgId,
				reportlistfilters_4, SpogConstants.SUCCESS_POST, null, test);

		filter_id = response.then().extract().path("data.filter_id");
		test.log(LogStatus.INFO, "The filter id is " + filter_id);

		// deleted the filter
		spogReportServer.deleteReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		// create filter with the source_group_ids is empty
		test.log(LogStatus.INFO, "Composing Filter List for Reports in org: " + organizationType);
		HashMap<String, Object> reportlistfilters_5 = spogReportServer.composeReportListFilterInfo(ReportName,
				DateRangeTypes, type, "", organization_ids, schedule_frequency, generated_ts_start, generated_ts_end,
				filter_name, is_default);

		// verify the report filters for specified user
		response = spogReportServer.createReportListFiltersForSpecifiedUserWithCheck(validToken, user_id, orgId,
				reportlistfilters_5, SpogConstants.SUCCESS_POST, null, test);

		filter_id = response.then().extract().path("data.filter_id");
		test.log(LogStatus.INFO, "The filter id is " + filter_id);

		// deleted the filter
		spogReportServer.deleteReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		// create filter with the organization_ids is empty
		test.log(LogStatus.INFO, "Composing Filter List for Reports in org: " + organizationType);
		HashMap<String, Object> reportlistfilters_6 = spogReportServer.composeReportListFilterInfo(ReportName,
				DateRangeTypes, type, source_group_ids, "", schedule_frequency, generated_ts_start, generated_ts_end,
				filter_name, is_default);

		// verify the report filters for specified user
		response = spogReportServer.createReportListFiltersForSpecifiedUserWithCheck(validToken, user_id, orgId,
				reportlistfilters_6, SpogConstants.SUCCESS_POST, null, test);

		filter_id = response.then().extract().path("data.filter_id");
		test.log(LogStatus.INFO, "The filter id is " + filter_id);

		// deleted the filter
		spogReportServer.deleteReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		// create filter with the generated_ts is empty
		test.log(LogStatus.INFO, "Composing Filter List for Reports in org: " + organizationType);
		HashMap<String, Object> reportlistfilters_7 = spogReportServer.composeReportListFilterInfo(ReportName,
				DateRangeTypes, type, source_group_ids, organization_ids, schedule_frequency, generated_ts_start,
				generated_ts_end, filter_name, is_default);

		// verify the report filters for specified user
		response = spogReportServer.createReportListFiltersForSpecifiedUserWithCheck(validToken, user_id, orgId,
				reportlistfilters_7, SpogConstants.SUCCESS_POST, null, test);

		filter_id = response.then().extract().path("data.filter_id");
		test.log(LogStatus.INFO, "The filter id is " + filter_id);

		// deleted the filter
		spogReportServer.deleteReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		// create filter with the generated_ts is empty
		test.log(LogStatus.INFO, "Composing Filter List for Reports in org: " + organizationType);
		HashMap<String, Object> reportlistfilters_8 = spogReportServer.composeReportListFilterInfo(ReportName,
				DateRangeTypes, type, source_group_ids, organization_ids, schedule_frequency, generated_ts_start,
				generated_ts_end, filter_name, is_default);

		// verify the report filters for specified user
		response = spogReportServer.createReportListFiltersForSpecifiedUserWithCheck(validToken, user_id, orgId,
				reportlistfilters_8, SpogConstants.SUCCESS_POST, null, test);

		filter_id = response.then().extract().path("data.filter_id");
		test.log(LogStatus.INFO, "The filter id is " + filter_id);

		// deleted the filter
		spogReportServer.deleteReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		// created Reported filter on Datarangetype is "last_1_month"
		test.log(LogStatus.INFO, "Composing Filter List for Reports in org: " + organizationType);
		HashMap<String, Object> reportlistfilters_9 = spogReportServer.composeReportListFilterInfo(ReportName,
				"last_1_month", type, source_group_ids, organization_ids, schedule_frequency, generated_ts_start,
				generated_ts_end, filter_name, is_default);

		// verify the report filters for specified user
		response = spogReportServer.createReportListFiltersForSpecifiedUserWithCheck(validToken, user_id, orgId,
				reportlistfilters_9, SpogConstants.SUCCESS_POST, null, test);

		filter_id = response.then().extract().path("data.filter_id");
		test.log(LogStatus.INFO, "The filter id is " + filter_id);

		// deleted the filter
		spogReportServer.deleteReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		// created Reported filter on Datarangetype is "last_3_months"
		test.log(LogStatus.INFO, "Composing Filter List for Reports in org: " + organizationType);
		HashMap<String, Object> reportlistfilters_10 = spogReportServer.composeReportListFilterInfo(ReportName,
				"last_3_months", type, source_group_ids, organization_ids, schedule_frequency, generated_ts_start,
				generated_ts_end, filter_name, is_default);

		// verify the report filters for specified user
		response = spogReportServer.createReportListFiltersForSpecifiedUserWithCheck(validToken, user_id, orgId,
				reportlistfilters_10, SpogConstants.SUCCESS_POST, null, test);

		filter_id = response.then().extract().path("data.filter_id");
		test.log(LogStatus.INFO, "The filter id is " + filter_id);

		// deleted the filter
		spogReportServer.deleteReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		// created Reported filter on Datarangetype is "last_6_months"
		test.log(LogStatus.INFO, "Composing Filter List for Reports in org: " + organizationType);
		HashMap<String, Object> reportlistfilters_11 = spogReportServer.composeReportListFilterInfo(ReportName,
				"last_6_months", type, source_group_ids, organization_ids, schedule_frequency, generated_ts_start,
				generated_ts_end, filter_name, is_default);

		// verify the report filters for specified user
		response = spogReportServer.createReportListFiltersForSpecifiedUserWithCheck(validToken, user_id, orgId,
				reportlistfilters_11, SpogConstants.SUCCESS_POST, null, test);

		filter_id = response.then().extract().path("data.filter_id");
		test.log(LogStatus.INFO, "The filter id is " + filter_id);

		// deleted the filter
		spogReportServer.deleteReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		// created Reported filter on Datarangetype is "last_1_year"
		test.log(LogStatus.INFO, "Composing Filter List for Reports in org: " + organizationType);
		HashMap<String, Object> reportlistfilters_12 = spogReportServer.composeReportListFilterInfo(ReportName,
				"last_1_year", type, source_group_ids, organization_ids, schedule_frequency, generated_ts_start,
				generated_ts_end, filter_name, is_default);

		// verify the report filters for specified user
		response = spogReportServer.createReportListFiltersForSpecifiedUserWithCheck(validToken, user_id, orgId,
				reportlistfilters_12, SpogConstants.SUCCESS_POST, null, test);

		filter_id = response.then().extract().path("data.filter_id");
		test.log(LogStatus.INFO, "The filter id is " + filter_id);

		// deleted the filter
		spogReportServer.deleteReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		// created Reported filter on Datarangetype is "last_1_year" and Report
		// typr is "all_sources"
		test.log(LogStatus.INFO, "Composing Filter List for Reports in org: " + organizationType);
		HashMap<String, Object> reportlistfilters_13 = spogReportServer.composeReportListFilterInfo(ReportName,
				"last_1_year", "all_sources", source_group_ids, organization_ids, schedule_frequency,
				generated_ts_start, generated_ts_end, filter_name, is_default);

		// verify the report filters for specified user
		response = spogReportServer.createReportListFiltersForSpecifiedUserWithCheck(validToken, user_id, orgId,
				reportlistfilters_13, SpogConstants.SUCCESS_POST, null, test);

		filter_id = response.then().extract().path("data.filter_id");
		test.log(LogStatus.INFO, "The filter id is " + filter_id);

		// deleted the filter
		spogReportServer.deleteReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		// created Reported filter on Datarangetype is "last_1_year" and Report
		// typr is "all_organizations"
		test.log(LogStatus.INFO, "Composing Filter List for Reports in org: " + organizationType);
		HashMap<String, Object> reportlistfilters_14 = spogReportServer.composeReportListFilterInfo(ReportName,
				"last_1_year", "all_organizations", source_group_ids, organization_ids, schedule_frequency,
				generated_ts_start, generated_ts_end, filter_name, is_default);

		// verify the report filters for specified user
		response = spogReportServer.createReportListFiltersForSpecifiedUserWithCheck(validToken, user_id, orgId,
				reportlistfilters_14, SpogConstants.SUCCESS_POST, null, test);

		filter_id = response.then().extract().path("data.filter_id");
		test.log(LogStatus.INFO, "The filter id is " + filter_id);

		// deleted the filter
		spogReportServer.deleteReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		// created Reported filter on Datarangetype is "last_1_year" and Report
		// typr is "selected_source_groups "
		test.log(LogStatus.INFO, "Composing Filter List for Reports in org: " + organizationType);
		HashMap<String, Object> reportlistfilters_15 = spogReportServer.composeReportListFilterInfo(ReportName,
				"last_1_year", "selected_source_groups", source_group_ids, organization_ids, schedule_frequency,
				generated_ts_start, generated_ts_end, filter_name, is_default);

		// verify the report filters for specified user
		response = spogReportServer.createReportListFiltersForSpecifiedUserWithCheck(validToken, user_id, orgId,
				reportlistfilters_15, SpogConstants.SUCCESS_POST, null, test);

		filter_id = response.then().extract().path("data.filter_id");
		test.log(LogStatus.INFO, "The filter id is " + filter_id);

		// deleted the filter
		spogReportServer.deleteReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

	}

	@Test(dataProvider = "createreportslistfilters")
	public void createReportListFilters_401(String organizationType, String organization_ids, String validToken,
			String user_id, String ReportName, String DateRangeTypes, String type, String schedule_frequency,
			long generated_ts_start, long generated_ts_end, String filter_name, boolean is_default, String groupName,
			String groupDescription, int noOfSourceGroupsToCreate, String orgaId) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		ArrayList<String> OrgIds = new ArrayList<>();

		test.assignAuthor("");

		spogServer.setToken(validToken);

		// orgId for creating source_groups
		String orgId = null;
		if (organizationType.contains("suborg")) {
			orgId = ti.normal_msp1_suborg1_id;
		} else {
			orgId = spogServer.GetLoggedinUserOrganizationID();
		}

		String source_group_ids = null, source_group_id = null;
		if (!(organizationType.equals("csrreadonly"))) {
			for (int i = 0; i < noOfSourceGroupsToCreate; i++) {
				source_group_id = spogServer.createGroupWithCheck2(validToken, orgId, groupName + i, groupDescription,
						test);
				if (i > 0) {
					source_group_ids = source_group_ids + "," + source_group_id;
				} else {
					source_group_ids = source_group_id;
				}

			}
		}

		spogServer.setToken(validToken);

		test.log(LogStatus.INFO, "Composing Filter List for Reports in org: " + organizationType);
		HashMap<String, Object> reportlistfilters = spogReportServer.composeReportListFilterInfo(ReportName,
				DateRangeTypes, type, source_group_ids, organization_ids, schedule_frequency, generated_ts_start,
				generated_ts_end, filter_name, is_default);

		spogReportServer.createReportListFiltersForSpecifiedUserWithCheck("", user_id, organization_ids,
				reportlistfilters, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);

		spogReportServer.createReportListFiltersForSpecifiedUserWithCheck(validToken + "junk", user_id,
				organization_ids, reportlistfilters, SpogConstants.NOT_LOGGED_IN,
				SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);

		spogReportServer.createReportListFiltersForSpecifiedUserWithCheck(validToken, "uuid", organization_ids,
				reportlistfilters, SpogConstants.REQUIRED_INFO_NOT_EXIST,
				SpogMessageCode.ERROR_MESSAGE_ELEMENT_NOT_UUID, test);

		String random_user_id = UUID.randomUUID().toString();

		spogReportServer.createReportListFiltersForSpecifiedUserWithCheck(validToken, random_user_id, organization_ids,
				reportlistfilters, SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.USER_ID_DOESNOT_EXIST, test);

		test.log(LogStatus.INFO, "Composing Filter List for Reports in org: " + organizationType);
		HashMap<String, Object> reportlistfilters_1 = spogReportServer.composeReportListFilterInfo("", DateRangeTypes,
				type, source_group_ids, organization_ids, schedule_frequency, generated_ts_start, generated_ts_end,
				filter_name, is_default);

		spogReportServer.createReportListFiltersForSpecifiedUserWithCheck(validToken, user_id, organization_ids,
				reportlistfilters_1, SpogConstants.SUCCESS_POST, null, test);

	}

	@DataProvider(name = "createreportslistfilters_403", parallel = false)
	public final Object[][] createreportslistfilters2() {

		return new Object[][] {

				{ "direct", ti.direct_org1_id, ti.direct_org1_user1_token, ti.normal_msp_org1_user1_token,
						ti.direct_org1_user1_id, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_organizations", "weekly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2 },
				{ "direct", ti.direct_org1_id, ti.direct_org1_user1_token, ti.normal_msp1_suborg1_user1_token,
						ti.direct_org1_user1_id, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_organizations", "weekly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2 },
				{ "direct", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org2_user1_token,
						ti.direct_org1_user1_id, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_organizations", "weekly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2 },
				{ "msp", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token, ti.normal_msp_org2_user1_token,
						ti.normal_msp_org1_user1_id, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_source_groups", "monthly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 3 },
				{ "msp", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token, ti.direct_org1_user1_token,
						ti.normal_msp_org1_user1_id, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_source_groups", "monthly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 3 },
				{ "msp", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token, ti.normal_msp1_suborg1_user1_token,
						ti.normal_msp_org1_user1_id, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_source_groups", "monthly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 3 },
				{ "suborg", ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token,
						ti.normal_msp1_suborg2_user1_token, ti.normal_msp1_suborg1_user1_id,
						spogServer.ReturnRandom("report"), "last_1_month,last_3_months", "all_sources", "quarterly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				{ "suborg", ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token, ti.direct_org1_user1_token,
						ti.normal_msp1_suborg1_user1_id, spogServer.ReturnRandom("report"),
						"last_1_month,last_3_months", "all_sources", "quarterly", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				{ "suborg", ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token,
						ti.normal_msp_org2_user1_token, ti.normal_msp1_suborg1_user1_id,
						spogServer.ReturnRandom("report"), "last_1_month,last_3_months", "all_sources", "quarterly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				// csrreadonly
				{ "csrreadonly", ti.csr_org_id, ti.csr_readonly_token, ti.direct_org1_user1_token,
						ti.csr_readonly_admin_user_id, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_organizations", "weekly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2 },
				{ "csrreadonly", ti.csr_org_id, ti.csr_readonly_token, ti.normal_msp_org1_user1_token,
						ti.csr_readonly_admin_user_id, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_organizations", "weekly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2 },
				{ "csrreadonly", ti.csr_org_id, ti.csr_readonly_token, ti.normal_msp1_suborg1_user1_token,
						ti.csr_readonly_admin_user_id, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_organizations", "weekly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2 },
				
				// 3 tier cases
				{ "rootmsp-msp2", ti.root_msp_org1_id, ti.root_msp_org1_user1_token, ti.root_msp_org2_user1_token,
						ti.root_msp_org1_user1_id, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_source_groups", "monthly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 3 },
				{ "rootmsp-direct", ti.root_msp_org1_id, ti.root_msp_org1_user1_token, ti.direct_org1_user1_token,
						ti.root_msp_org1_user1_id, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_source_groups", "monthly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 3 },
				{ "rootmsp-suborg", ti.root_msp_org1_id, ti.root_msp_org1_user1_token, ti.root_msp1_suborg1_user1_token,
						ti.root_msp_org1_user1_id, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_source_groups", "monthly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 3 },
				{ "rootsuborg-suborg2", ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_token,
						ti.root_msp1_suborg2_user1_token, ti.root_msp1_suborg1_user1_id,
						spogServer.ReturnRandom("report"), "last_1_month,last_3_months", "all_sources", "quarterly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				{ "rootsuborg-direct", ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_token,
						ti.direct_org1_user1_token, ti.root_msp1_suborg1_user1_id, spogServer.ReturnRandom("report"),
						"last_1_month,last_3_months", "all_sources", "quarterly", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				{ "rootsuborg-msp2", ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_token,
						ti.root_msp_org2_user1_token, ti.root_msp1_suborg1_user1_id, spogServer.ReturnRandom("report"),
						"last_1_month,last_3_months", "all_sources", "quarterly", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				{ "submsp-msp2", ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_user1_token,
						ti.root_msp_org2_user1_token, ti.root_msp1_submsp1_user1_id, spogServer.ReturnRandom("report"),
						"last_7_days", "selected_source_groups", "monthly", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				{ "submsp-direct", ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_user1_token,
						ti.direct_org1_user1_token, ti.root_msp1_submsp1_user1_id, spogServer.ReturnRandom("report"),
						"last_7_days", "selected_source_groups", "monthly", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				{ "submsp-root_sub", ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_user1_token,
						ti.root_msp1_suborg1_user1_token, ti.root_msp1_submsp1_user1_id,
						spogServer.ReturnRandom("report"), "last_7_days", "selected_source_groups", "monthly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				{ "submsp_sub-rootsub", ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_token,
						ti.root_msp1_suborg2_user1_token, ti.msp1_submsp1_suborg1_user1_id,
						spogServer.ReturnRandom("report"), "last_1_month,last_3_months", "all_sources", "quarterly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				{ "submsp_sub-direct", ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_token,
						ti.direct_org1_user1_token, ti.msp1_submsp1_suborg1_user1_id, spogServer.ReturnRandom("report"),
						"last_1_month,last_3_months", "all_sources", "quarterly", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				{ "submsp_sub-msp2", ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_token,
						ti.root_msp_org2_user1_token, ti.msp1_submsp1_suborg1_user1_id,
						spogServer.ReturnRandom("report"), "last_1_month,last_3_months", "all_sources", "quarterly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 }, 

		};
	}

	@Test(dataProvider = "createreportslistfilters_403")
	public void createReportListFilters_403(String organizationType, String organization_ids, String validToken,
			String anothertoken, String user_id, String ReportName, String DateRangeTypes, String type,
			String schedule_frequency, long generated_ts_start, long generated_ts_end, String filter_name,
			boolean is_default, String groupName, String groupDescription, int noOfSourceGroupsToCreate) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		String orgId = null;
		if (organizationType.contains("suborg")) {
			orgId = ti.normal_msp1_suborg1_id;
		} else {
			spogServer.setToken(validToken);
			orgId = spogServer.GetLoggedinUserOrganizationID();
		}

		String source_group_ids = null;
		
		spogServer.setToken(validToken);
		test.log(LogStatus.INFO, "Composing Filter List for Reports in org: " + organizationType);
		HashMap<String, Object> reportlistfilters = spogReportServer.composeReportListFilterInfo(ReportName,
				DateRangeTypes, type, source_group_ids, organization_ids, schedule_frequency, generated_ts_start,
				generated_ts_end, filter_name, is_default);

		spogReportServer.createReportListFiltersForSpecifiedUserWithCheck(anothertoken, user_id, organization_ids,
				reportlistfilters, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY,
				test);

	}

	@DataProvider(name = "createreportslistfilters_404", parallel = false)
	public final Object[][] createreportslistfilters3() {

		return new Object[][] {

				{ "direct", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id,
						spogServer.ReturnRandom("report"), "last_7_days", "selected_organizations", "weekly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2 },
				{ "suborg", ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token,
						ti.normal_msp1_suborg1_user1_id, spogServer.ReturnRandom("report"),
						"last_1_month,last_3_months", "all_sources", "quarterly", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				{ "msp", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,
						spogServer.ReturnRandom("report"), "last_7_days", "selected_source_groups", "monthly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				// csrreadonly
				{ "csrreadonly", ti.csr_org_id, ti.csr_readonly_token, ti.csr_readonly_admin_user_id,
						spogServer.ReturnRandom("report"), "last_7_days", "selected_source_groups", "monthly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },

		};
	}

	@Test(dataProvider = "createreportslistfilters_404")
	public void createReportListFilters_404(String organizationType, String organization_ids, String validToken,
			String user_id, String ReportName, String DateRangeTypes, String type, String schedule_frequency,
			long generated_ts_start, long generated_ts_end, String filter_name, boolean is_default, String groupName,
			String groupDescription, int noOfSourceGroupsToCreate) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		ArrayList<String> OrgIds = new ArrayList<>();

		test.assignAuthor("");

		spogServer.setToken(validToken);

		// orgId for creating source_groups
		String orgId = null;
		if (organizationType.contains("suborg")) {
			orgId = ti.normal_msp1_suborg1_id;
		} else {
			orgId = spogServer.GetLoggedinUserOrganizationID();
		}

		test.assignAuthor("");

		spogServer.setToken(validToken);
		test.log(LogStatus.INFO, "Composing Filter List for Reports in org: " + organizationType);
		String source_group_ids = null;
		HashMap<String, Object> reportlistfilters = spogReportServer.composeReportListFilterInfo(ReportName,
				DateRangeTypes, type, source_group_ids, organization_ids, schedule_frequency, generated_ts_start,
				generated_ts_end, filter_name, is_default);

		spogReportServer.createReportListFiltersForSpecifiedUserWithCheck(ti.csr_token, UUID.randomUUID().toString(),
				organization_ids, reportlistfilters, SpogConstants.RESOURCE_NOT_EXIST,
				SpogMessageCode.USER_ID_DOESNOT_EXIST, test);

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

	/******************************************************************
	 * RandomFunction
	 ******************************************************************************/
	public int gen_random_index(String[] job_status) {
		Random generator = new Random();
		int randomindx = generator.nextInt(job_status.length);

		return randomindx;
	}

}
