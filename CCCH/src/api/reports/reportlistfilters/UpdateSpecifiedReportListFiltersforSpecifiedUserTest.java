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

public class UpdateSpecifiedReportListFiltersforSpecifiedUserTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private SPOGReportServer spogReportServer;
	private ExtentTest test;
	private TestOrgInfo ti;
	private ArrayList<String> filter_ids = new ArrayList<>();

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

	@DataProvider(name = "updateSpecifiedreportfilter", parallel = false)
	public final Object[][] updatereportslistfilters() {

		return new Object[][] {

				{ "direct", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id,
						spogServer.ReturnRandom("report"), "last_7_days", "selected_organizations", "daily",
						System.currentTimeMillis(), System.currentTimeMillis() + 2, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						spogServer.ReturnRandom("report"), "last_7_days", "selected_organizations", "daily",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName_direct"),
						spogServer.ReturnRandom("groupDescription_direct") },
				{ "msp", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,
						spogServer.ReturnRandom("report"), "last_7_days", "selected_source_groups", "monthly",
						System.currentTimeMillis(), System.currentTimeMillis() + 2, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3,
						spogServer.ReturnRandom("report"), "last_7_days", "selected_organizations", "weekly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName_direct"),
						spogServer.ReturnRandom("groupDescription_direct") },
				{ "suborg", ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token,
						ti.normal_msp1_suborg1_user1_id, spogServer.ReturnRandom("report"),
						"last_1_month,last_3_months", "all_sources", "quarterly", System.currentTimeMillis(),
						System.currentTimeMillis() + 2, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3,
						spogServer.ReturnRandom("report"), "last_7_days", "selected_organizations", "weekly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName_sub"),
						spogServer.ReturnRandom("groupDescription_sub") },
				{ "suborg", ti.normal_msp1_suborg1_id, ti.normal_msp_org1_user1_token, ti.normal_msp1_suborg1_user1_id,
						spogServer.ReturnRandom("report"), "last_1_month,last_3_months", "all_sources", "quarterly",
						System.currentTimeMillis(), System.currentTimeMillis() + 2, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3,
						spogServer.ReturnRandom("report"), "last_7_days", "selected_organizations", "weekly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName_sub"),
						spogServer.ReturnRandom("groupDescription_sub") },
				{ "suborg_mspuser_token", ti.normal_msp1_suborg1_id, ti.normal_msp_org1_msp_accountadmin1_token,
						ti.normal_msp1_suborg1_user1_id, spogServer.ReturnRandom("report"),
						"last_1_month,last_3_months", "all_sources", "quarterly", System.currentTimeMillis(),
						System.currentTimeMillis() + 2, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3,
						spogServer.ReturnRandom("report"), "last_7_days", "selected_organizations", "weekly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName_sub_msp_account_admin"),
						spogServer.ReturnRandom("groupDescription_suborg_with_msptokwkn") },
				// csrreadonly cases
				{ "csrreadonly", ti.csr_org_id, ti.csr_readonly_token, ti.csr_readonly_admin_user_id,
						spogServer.ReturnRandom("report"), "last_7_days", "selected_organizations", "daily",
						System.currentTimeMillis(), System.currentTimeMillis() + 2, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						spogServer.ReturnRandom("report"), "last_7_days", "selected_organizations", "daily",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName_csrreadonly"),
						spogServer.ReturnRandom("groupDescription_csrreadonly") },

		};
	}

	@Test(dataProvider = "updateSpecifiedreportfilter")
	public void updateSpecifiedReportFilter_200(String organizationType, String organization_ids, String validToken,
			String user_id, String ReportName, String DateRangeTypes, String type, String schedule_frequency,
			long generated_ts_start, long generated_ts_end, String filter_name, boolean is_default, String groupName,
			String groupDescription, int noOfSourceGroupsToCreate, String updateReportName, String updateDateRangeTypes,
			String updatetype, String updateschedule_frequency, long updategenerated_ts_start,
			long updategenerated_ts_end, String updatefilter_name, boolean updateis_default, String updategroupName,
			String updategroupDescription) {

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
		if (!(organizationType.contains("csrreadonly"))) {
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
		spogReportServer.setToken(validToken);
		test.log(LogStatus.INFO, "Composing Filter List for Reports in org: " + organizationType);
		HashMap<String, Object> reportlistfilters = spogReportServer.composeReportListFilterInfo(ReportName,
				DateRangeTypes, type, source_group_ids, organization_ids, schedule_frequency, generated_ts_start,
				generated_ts_end, filter_name, is_default);

		Response respone = spogReportServer.createReportListFiltersForSpecifiedUserWithCheck(validToken, user_id,
				organization_ids, reportlistfilters, SpogConstants.SUCCESS_POST, null, test);

		String filter_id = respone.then().extract().path("data.filter_id");
		test.log(LogStatus.INFO, "The filter id is " + filter_id);

		spogServer.setToken(validToken);
		// created Reported filetr on all fileds
		test.log(LogStatus.INFO, "Composing Filter List for Reports in org: " + organizationType);
		HashMap<String, Object> Updatereportlistfilters = spogReportServer.composeReportListFilterInfo(updateReportName,
				updateDateRangeTypes, updatetype, source_group_ids, organization_ids, updateschedule_frequency,
				updategenerated_ts_start, updategenerated_ts_end, updatefilter_name, updateis_default);

		// verify the report filters for specified user
		spogReportServer.updateReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id,
				organization_ids, filter_id, Updatereportlistfilters, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		// updated filter with the Report name is empty
		test.log(LogStatus.INFO, "Composing Filter List for Reports in org: " + organizationType);
		HashMap<String, Object> Updatereportlistfilters_1 = spogReportServer.composeReportListFilterInfo("",
				updateDateRangeTypes, updatetype, source_group_ids, organization_ids, updateschedule_frequency,
				updategenerated_ts_start, updategenerated_ts_end, updatefilter_name, updateis_default);

		// verify the report filters for specified user
		spogReportServer.updateReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id,
				organization_ids, filter_id, Updatereportlistfilters_1, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);

		// updated filter with the updateDateRangeTypes is empty
		test.log(LogStatus.INFO, "Composing Filter List for Reports in org: " + organizationType);
		HashMap<String, Object> Updatereportlistfilters_2 = spogReportServer.composeReportListFilterInfo(
				updateReportName, "", updatetype, source_group_ids, organization_ids, updateschedule_frequency,
				updategenerated_ts_start, updategenerated_ts_end, updatefilter_name, updateis_default);

		// verify the report filters for specified user
		spogReportServer.updateReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id,
				organization_ids, filter_id, Updatereportlistfilters_2, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);

		// updated filter with the updateschedule_frequency is empty
		test.log(LogStatus.INFO, "Composing Filter List for Reports in org: " + organizationType);
		HashMap<String, Object> Updatereportlistfilters_3 = spogReportServer.composeReportListFilterInfo(
				updateReportName, updateDateRangeTypes, updatetype, source_group_ids, organization_ids, "",
				updategenerated_ts_start, updategenerated_ts_end, updatefilter_name, updateis_default);

		// verify the report filters for specified user
		spogReportServer.updateReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id,
				organization_ids, filter_id, Updatereportlistfilters_3, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);

		// updated filter with the source_group_ids is empty
		test.log(LogStatus.INFO, "Composing Filter List for Reports in org: " + organizationType);
		HashMap<String, Object> Updatereportlistfilters_4 = spogReportServer.composeReportListFilterInfo(
				updateReportName, updateDateRangeTypes, updatetype, "", organization_ids, updateschedule_frequency,
				updategenerated_ts_start, updategenerated_ts_end, updatefilter_name, updateis_default);

		// verify the report filters for specified user
		spogReportServer.updateReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id,
				organization_ids, filter_id, Updatereportlistfilters_4, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);

		// updated filter with the organization_ids is empty
		test.log(LogStatus.INFO, "Composing Filter List for Reports in org: " + organizationType);
		HashMap<String, Object> Updatereportlistfilters_5 = spogReportServer.composeReportListFilterInfo(
				updateReportName, updateDateRangeTypes, updatetype, source_group_ids, "", updateschedule_frequency,
				updategenerated_ts_start, updategenerated_ts_end, updatefilter_name, updateis_default);

		// verify the report filters for specified user
		spogReportServer.updateReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id,
				organization_ids, filter_id, Updatereportlistfilters_5, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);

		// updated filter with the generated_ts is empty
		test.log(LogStatus.INFO, "Composing Filter List for Reports in org: " + organizationType);
		HashMap<String, Object> Updatereportlistfilters_6 = spogReportServer.composeReportListFilterInfo(
				updateReportName, updateDateRangeTypes, updatetype, source_group_ids, organization_ids,
				updateschedule_frequency, updategenerated_ts_start, updategenerated_ts_end, updatefilter_name,
				updateis_default);

		// verify the report filters for specified user
		spogReportServer.updateReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id,
				organization_ids, filter_id, Updatereportlistfilters_6, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);

		// updated filter with the generated_ts is empty
		test.log(LogStatus.INFO, "Composing Filter List for Reports in org: " + organizationType);
		HashMap<String, Object> Updatereportlistfilters_7 = spogReportServer.composeReportListFilterInfo(
				updateReportName, updateDateRangeTypes, updatetype, source_group_ids, organization_ids,
				updateschedule_frequency, updategenerated_ts_start, updategenerated_ts_end, updatefilter_name,
				updateis_default);

		// verify the report filters for specified user
		spogReportServer.updateReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id,
				organization_ids, filter_id, Updatereportlistfilters_7, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);

		// created Reported filter on Datarangetype is "last_1_month"
		test.log(LogStatus.INFO, "Composing Filter List for Reports in org: " + organizationType);
		HashMap<String, Object> Updatereportlistfilters_8 = spogReportServer.composeReportListFilterInfo(
				updateReportName, "last_1_month", updatetype, source_group_ids, organization_ids,
				updateschedule_frequency, updategenerated_ts_start, updategenerated_ts_end, updatefilter_name,
				updateis_default);

		// verify the report filters for specified user
		spogReportServer.updateReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id,
				organization_ids, filter_id, Updatereportlistfilters_8, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);

		// created Reported filter on Datarangetype is "last_3_months"
		test.log(LogStatus.INFO, "Composing Filter List for Reports in org: " + organizationType);
		HashMap<String, Object> Updatereportlistfilters_9 = spogReportServer.composeReportListFilterInfo(
				updateReportName, "last_3_months", updatetype, source_group_ids, organization_ids,
				updateschedule_frequency, updategenerated_ts_start, updategenerated_ts_end, updatefilter_name,
				updateis_default);

		// verify the report filters for specified user
		spogReportServer.updateReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id,
				organization_ids, filter_id, Updatereportlistfilters_9, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);

		// created Reported filter on Datarangetype is "last_6_months"
		test.log(LogStatus.INFO, "Composing Filter List for Reports in org: " + organizationType);
		HashMap<String, Object> Updatereportlistfilters_10 = spogReportServer.composeReportListFilterInfo(
				updateReportName, "last_6_months", updatetype, source_group_ids, organization_ids,
				updateschedule_frequency, updategenerated_ts_start, updategenerated_ts_end, updatefilter_name,
				updateis_default);

		// verify the report filters for specified user
		spogReportServer.updateReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id,
				organization_ids, filter_id, Updatereportlistfilters_10, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);

		// created Reported filter on Datarangetype is "last_1_year"
		test.log(LogStatus.INFO, "Composing Filter List for Reports in org: " + organizationType);
		HashMap<String, Object> Updatereportlistfilters_11 = spogReportServer.composeReportListFilterInfo(
				updateReportName, "last_1_year", updatetype, source_group_ids, organization_ids,
				updateschedule_frequency, updategenerated_ts_start, updategenerated_ts_end, updatefilter_name,
				updateis_default);

		// verify the report filters for specified user
		spogReportServer.updateReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id,
				organization_ids, filter_id, Updatereportlistfilters_11, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);

		// created Reported filter on Datarangetype is "last_1_year" and Report
		// typr is "all_sources"
		test.log(LogStatus.INFO, "Composing Filter List for Reports in org: " + organizationType);
		HashMap<String, Object> Updatereportlistfilters_12 = spogReportServer.composeReportListFilterInfo(
				updateReportName, "last_1_year", "all_sources", source_group_ids, organization_ids,
				updateschedule_frequency, updategenerated_ts_start, updategenerated_ts_end, updatefilter_name,
				updateis_default);

		// verify the report filters for specified user
		spogReportServer.updateReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id,
				organization_ids, filter_id, Updatereportlistfilters_12, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);

		// created Reported filter on Datarangetype is "last_1_year" and Report
		// typr is "all_organizations"
		test.log(LogStatus.INFO, "Composing Filter List for Reports in org: " + organizationType);
		HashMap<String, Object> Updatereportlistfilters_13 = spogReportServer.composeReportListFilterInfo(
				updateReportName, "last_1_year", "all_organizations", source_group_ids, organization_ids,
				updateschedule_frequency, updategenerated_ts_start, updategenerated_ts_end, updatefilter_name,
				updateis_default);

		// verify the report filters for specified user
		spogReportServer.updateReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id,
				organization_ids, filter_id, Updatereportlistfilters_13, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);

		// created Reported filter on Datarangetype is "last_1_year" and Report
		// typr is "selected_source_groups "
		test.log(LogStatus.INFO, "Composing Filter List for Reports in org: " + organizationType);
		HashMap<String, Object> Updatereportlistfilters_14 = spogReportServer.composeReportListFilterInfo(
				updateReportName, "last_1_year", "selected_source_groups", source_group_ids, organization_ids,
				updateschedule_frequency, updategenerated_ts_start, updategenerated_ts_end, updatefilter_name,
				updateis_default);

		// verify the report filters for specified user
		spogReportServer.updateReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id,
				organization_ids, filter_id, Updatereportlistfilters_14, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);

		// created Reported filter on Datarangetype is "last_1_year" and Report
		// typr is "selected_source_groups "
		test.log(LogStatus.INFO, "Composing Filter List for Reports in org: " + organizationType);
		HashMap<String, Object> Updatereportlistfilters_15 = spogReportServer.composeReportListFilterInfo(
				updateReportName, "last_1_year", "selected_organizations", source_group_ids, organization_ids,
				updateschedule_frequency, updategenerated_ts_start, updategenerated_ts_end, updatefilter_name,
				updateis_default);

		// verify the report filters for specified user
		spogReportServer.updateReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id,
				organization_ids, filter_id, Updatereportlistfilters_15, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);

	}

	@Test(dataProvider = "updateSpecifiedreportfilter")
	public void updateReportListFilters_invalidScenarios(String organizationType, String organization_ids,
			String validToken, String user_id, String ReportName, String DateRangeTypes, String type,
			String schedule_frequency, long generated_ts_start, long generated_ts_end, String filter_name,
			boolean is_default, String groupName, String groupDescription, int noOfSourceGroupsToCreate,
			String updateReportName, String updateDateRangeTypes, String updatetype, String updateschedule_frequency,
			long updategenerated_ts_start, long updategenerated_ts_end, String updatefilter_name,
			boolean updateis_default, String updategroupName, String updategroupDescription) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		ArrayList<String> OrgIds = new ArrayList<>();

		test.assignAuthor("");

		spogServer.setToken(validToken);

		// orgId for creating source_groups
		String orgId = null;
		if (organizationType.contains("suborg")) {
			orgId = ti.normal_msp1_suborg1_id;
		} else if (organizationType.contains("csrreadonly")) {
			orgId = ti.csr_org_id;
		} else {
			orgId = spogServer.GetLoggedinUserOrganizationID();
		}

		String source_group_ids = null, source_group_id = null;
	/*	if (!(organizationType.contains("csrreadonly"))) {
			for (int i = 0; i < noOfSourceGroupsToCreate; i++) {
				source_group_id = spogServer.createGroupWithCheck2(validToken, orgId, groupName + i, groupDescription,
						test);
				if (i > 0) {
					source_group_ids = source_group_ids + "," + source_group_id;
				} else {
					source_group_ids = source_group_id;
				}
			}
		}*/
		spogServer.setToken(validToken);
		test.log(LogStatus.INFO, "Composing Filter List for Reports in org: " + organizationType);
		HashMap<String, Object> reportlistfilters = spogReportServer.composeReportListFilterInfo(ReportName,
				DateRangeTypes, type, source_group_ids, organization_ids, schedule_frequency, generated_ts_start,
				generated_ts_end, filter_name, is_default);

		Response respone = spogReportServer.createReportListFiltersForSpecifiedUserWithCheck(validToken, user_id,
				organization_ids, reportlistfilters, SpogConstants.SUCCESS_POST, null, test);

		String filter_id = respone.then().extract().path("data.filter_id");
		test.log(LogStatus.INFO, "The filter id is " + filter_id);

		spogServer.setToken(validToken);
		// created Reported filetr on all fileds
		test.log(LogStatus.INFO, "Composing Filter List for Reports in org: " + organizationType);
		HashMap<String, Object> Updatereportlistfilters = spogReportServer.composeReportListFilterInfo(updateReportName,
				updateDateRangeTypes, updatetype, source_group_ids, organization_ids, updateschedule_frequency,
				updategenerated_ts_start, updategenerated_ts_end, updatefilter_name, updateis_default);

		// update the report filetr with the missed token
		spogReportServer.updateReportListFiltersForSpecifiedUserByFilterIdWithCheck("", user_id, organization_ids,
				filter_id, Updatereportlistfilters, SpogConstants.NOT_LOGGED_IN,
				SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);

		// update the filter with the invalid token
		spogReportServer.updateReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken + "junk", user_id,
				organization_ids, filter_id, Updatereportlistfilters, SpogConstants.NOT_LOGGED_IN,
				SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);

		/*// update the report filetr with the missed token
		spogReportServer.updateReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, "uuid",
				organization_ids, filter_id, Updatereportlistfilters, SpogConstants.REQUIRED_INFO_NOT_EXIST,
				SpogMessageCode.ERROR_MESSAGE_ELEMENT_NOT_UUID, test);

		String random_user_id = UUID.randomUUID().toString();

		spogReportServer.updateReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, random_user_id,
				organization_ids, filter_id, Updatereportlistfilters, SpogConstants.RESOURCE_NOT_EXIST,
				SpogMessageCode.USER_ID_DOESNOT_EXIST, test);*/

		spogReportServer.deleteReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id, filter_id, SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		spogServer.setToken(ti.csr_token);
		ti.csr_org_id = spogServer.GetLoggedinUserOrganizationID();

		spogServer.setToken(ti.csr_readonly_token);
		ti.csr_readonly_admin_user_id = spogServer.GetLoggedinUser_UserID();
	}

	@DataProvider(name = "updatereportslistfilters_403", parallel = false)
	public final Object[][] createreportslistfilters2() {

		return new Object[][] {

				{ "direct- msp", ti.direct_org1_id, ti.direct_org1_user1_token, ti.normal_msp_org1_user1_token,
						ti.direct_org1_user1_id, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_organizations", "weekly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2 },
				{ "direct-sub", ti.direct_org1_id, ti.direct_org1_user1_token, ti.normal_msp1_suborg1_user1_token,
						ti.direct_org1_user1_id, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_organizations", "weekly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2 },
				{ "direct-direct2", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org2_user1_token,
						ti.direct_org1_user1_id, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_organizations", "weekly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2 },
				{ "msp-msp2", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token, ti.normal_msp_org2_user1_token,
						ti.normal_msp_org1_user1_id, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_source_groups", "monthly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 3 },
				{ "msp-direct", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token, ti.direct_org1_user1_token,
						ti.normal_msp_org1_user1_id, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_source_groups", "monthly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 3 },
				{ "msp-sub", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token, ti.normal_msp1_suborg1_user1_token,
						ti.normal_msp_org1_user1_id, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_source_groups", "monthly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 3 },
				{ "suborg-sub2", ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token,
						ti.normal_msp1_suborg2_user1_token, ti.normal_msp1_suborg1_user1_id,
						spogServer.ReturnRandom("report"), "last_1_month,last_3_months", "all_sources", "quarterly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				{ "suborg-direct", ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token,
						ti.direct_org1_user1_token, ti.normal_msp1_suborg1_user1_id, spogServer.ReturnRandom("report"),
						"last_1_month,last_3_months", "all_sources", "quarterly", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				{ "suborg-msp2", ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token,
						ti.normal_msp_org2_user1_token, ti.normal_msp1_suborg1_user1_id,
						spogServer.ReturnRandom("report"), "last_1_month,last_3_months", "all_sources", "quarterly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },

				// csrreadonly cases
				{ "csrreadonly-direct", ti.csr_org_id, ti.csr_readonly_token, ti.direct_org1_user1_token,
						ti.csr_readonly_admin_user_id, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_organizations", "weekly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2 },
				{ "csrreadonly-msp", ti.csr_org_id, ti.csr_readonly_token, ti.normal_msp_org1_user1_token,
						ti.csr_readonly_admin_user_id, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_organizations", "weekly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2 },
				{ "csrreadonly-sub", ti.csr_org_id, ti.csr_readonly_token, ti.normal_msp1_suborg1_user1_token,
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
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 }, };
	}

	@Test(dataProvider = "updatereportslistfilters_403")
	public void updateSpecifiedReportFilter_403(String organizationType, String organization_ids, String validToken,
			String anothertoken, String user_id, String ReportName, String DateRangeTypes, String type,
			String schedule_frequency, long generated_ts_start, long generated_ts_end, String filter_name,
			boolean is_default, String groupName, String groupDescription, int noOfSourceGroupsToCreate) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		// orgId for creating source_groups
		String orgId = null;
		String[] org_ids = organization_ids.split(",");

		if (org_ids.length != 1) {
			orgId = org_ids[0];
		} else {
			orgId = org_ids.toString();
		}

		String source_group_ids = null, source_group_id = null;
		
		spogServer.setToken(validToken);
		test.log(LogStatus.INFO, "Composing Filter List for Reports in org: " + organizationType);
		HashMap<String, Object> reportlistfilters = spogReportServer.composeReportListFilterInfo(ReportName,
				DateRangeTypes, type, source_group_ids, organization_ids, schedule_frequency, generated_ts_start,
				generated_ts_end, filter_name, is_default);

		Response respone = spogReportServer.createReportListFiltersForSpecifiedUserWithCheck(validToken, user_id,
				organization_ids, reportlistfilters, SpogConstants.SUCCESS_POST, null, test);

		String filter_id = respone.then().extract().path("data.filter_id");
		test.log(LogStatus.INFO, "The filter id is " + filter_id);

		spogReportServer.updateReportListFiltersForSpecifiedUserByFilterIdWithCheck(anothertoken, user_id,
				organization_ids, filter_id, reportlistfilters, SpogConstants.INSUFFICIENT_PERMISSIONS,
				SpogMessageCode.RESOURCE_PERMISSION_DENY, test);

	}

	@DataProvider(name = "updateereportslistfilters_404", parallel = false)
	public final Object[][] updatereportslistfilters3() {

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
				{ "rootsub", ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,
						spogServer.ReturnRandom("report"), "last_1_month,last_3_months", "all_sources", "quarterly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				{ "rootmsp", ti.root_msp_org1_id, ti.root_msp_org1_user1_token, ti.root_msp_org1_user1_id,
						spogServer.ReturnRandom("report"), "last_7_days", "selected_source_groups", "monthly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				{ "submsp", ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_user1_token,
						ti.root_msp1_submsp1_user1_id, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_source_groups", "monthly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 3 },
				{ "submsp_sub", ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_token,
						ti.msp1_submsp1_suborg1_user1_id, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_source_groups", "monthly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 3 },
				// csrreadonly
				{ "csrreadonly", ti.csr_org_id, ti.csr_readonly_token, ti.csr_readonly_admin_user_id,
						spogServer.ReturnRandom("report"), "last_7_days", "selected_source_groups", "monthly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },

		};
	}
/*
	@Test(dataProvider = "updateereportslistfilters_404")
	public void updateSpecifiedReportFilter_404(String organizationType, String organization_ids, String validToken,
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

		String source_group_ids = null, source_group_id = null;
		

		test.assignAuthor("");
		spogServer.setToken(validToken);
		test.log(LogStatus.INFO, "Composing Filter List for Reports in org: " + organizationType);
		HashMap<String, Object> reportlistfilters = spogReportServer.composeReportListFilterInfo(ReportName,
				DateRangeTypes, type, source_group_ids, organization_ids, schedule_frequency, generated_ts_start,
				generated_ts_end, filter_name, is_default);

		Response respone = spogReportServer.createReportListFiltersForSpecifiedUserWithCheck(validToken, user_id,
				organization_ids, reportlistfilters, SpogConstants.SUCCESS_POST, null, test);

		String filter_id = respone.then().extract().path("data.filter_id");
		test.log(LogStatus.INFO, "The filter id is " + filter_id);

		test.log(LogStatus.INFO,
				"Update report list filter for specified user with specified filter id using random id as the user_id");
		spogReportServer.updateReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken,
				UUID.randomUUID().toString(), organization_ids, filter_id, reportlistfilters,
				SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.USER_ID_DOESNOT_EXIST, test);

		spogReportServer.deleteReportListFiltersForLoggedInUserByFilterIdWithCheck(validToken, filter_id, SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
	}*/

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
