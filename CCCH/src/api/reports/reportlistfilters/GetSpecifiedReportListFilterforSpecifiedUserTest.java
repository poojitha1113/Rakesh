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
import InvokerServer.GatewayServer;
import InvokerServer.SPOGReportServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class GetSpecifiedReportListFilterforSpecifiedUserTest extends base.prepare.Is4Org {
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

	@DataProvider(name = "Getreportslistfilters", parallel = false)
	public final Object[][] Getreportslistfilters() {

		return new Object[][] {
				{ "csr", ti.csr_org_id, ti.csr_token, ti.csr_admin_user_id, spogServer.ReturnRandom("report"),
						"last_7_days", "selected_organizations", "weekly", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2 },
				{ "csr_readonly", ti.csr_org_id, ti.csr_readonly_token, ti.csr_readonly_admin_user_id,
						spogServer.ReturnRandom("report"), "last_7_days", "selected_organizations", "weekly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2 },

				{ "direct", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id,
						spogServer.ReturnRandom("report"), "last_7_days", "selected_organizations", "weekly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2 },
				{ "msp", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,
						spogServer.ReturnRandom("report"), "last_7_days", "selected_source_groups", "monthly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				{ "suborg", ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token,
						ti.normal_msp1_suborg1_user1_id, spogServer.ReturnRandom("report"),
						"last_1_month,last_3_months", "all_sources", "quarterly", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },

		};
	}

	@Test(dataProvider = "Getreportslistfilters")
	public void GetReportListFilters_201(String organizationType, String organization_ids, String validToken,
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
	/*	if (!organizationType.contains("csr")) {
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

		test.log(LogStatus.INFO, "Get reportlist filters by filter id for specified user ");
		spogReportServer.getReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id, organization_ids,
				filter_id, reportlistfilters, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

	}

	@DataProvider(name = "Getreportslistfilters_401", parallel = false)
	public final Object[][] Getreportslistfilters4() {

		return new Object[][] {

				{ "direct", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id,
						spogServer.ReturnRandom("report"), "last_7_days", "selected_organizations", "weekly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2 },
				{ "msp", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,
						spogServer.ReturnRandom("report"), "last_7_days", "selected_source_groups", "monthly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				{ "suborg", ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token,
						ti.normal_msp1_suborg1_user1_id, spogServer.ReturnRandom("report"),
						"last_1_month,last_3_months", "all_sources", "quarterly", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				// 3 tier cases
				{ "rootmsp", ti.root_msp_org1_id, ti.root_msp_org1_user1_token, ti.root_msp_org1_user1_id,
						spogServer.ReturnRandom("report"), "last_7_days", "selected_source_groups", "monthly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				{ "rootsub", ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,
						spogServer.ReturnRandom("report"), "last_1_month,last_3_months", "all_sources", "quarterly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				{ "submsp", ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_user1_token,
						ti.root_msp1_submsp1_user1_id, spogServer.ReturnRandom("report"), "last_1_month,last_3_months",
						"all_sources", "quarterly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 3 },
				{ "submsp_sub", ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_token,
						ti.msp1_submsp1_suborg1_user1_id, spogServer.ReturnRandom("report"),
						"last_1_month,last_3_months", "all_sources", "quarterly", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },

		};
	}

	@Test(dataProvider = "Getreportslistfilters_401")
	public void GetReportListFilters_401(String organizationType, String organization_ids, String validToken,
			String user_id, String ReportName, String DateRangeTypes, String type, String schedule_frequency,
			long generated_ts_start, long generated_ts_end, String filter_name, boolean is_default, String groupName,
			String groupDescription, int noOfSourceGroupsToCreate) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		ArrayList<String> OrgIds = new ArrayList<>();

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

		String source_group_ids = null, source_group_id = null;
		/*for (int i = 0; i < noOfSourceGroupsToCreate; i++) {
			source_group_id = spogServer.createGroupWithCheck2(validToken, orgId, groupName + i, groupDescription,
					test);
			if (i > 0) {
				source_group_ids = source_group_ids + "," + source_group_id;
			} else {
				source_group_ids = source_group_id;
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

		// missed token
		spogReportServer.getReportListFiltersForSpecifiedUserByFilterIdWithCheck("", user_id, organization_ids,
				filter_id, reportlistfilters, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED,
				test);

		// invalid token
		spogReportServer.getReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken + "junk", user_id,
				organization_ids, filter_id, reportlistfilters, SpogConstants.NOT_LOGGED_IN,
				SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);

	/*	// Userid is not uuid
		test.log(LogStatus.INFO, "Get report list filters by filter id for specified user when user id is invalid");
		spogReportServer.getReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, "uuid", organization_ids,
				filter_id, reportlistfilters, SpogConstants.REQUIRED_INFO_NOT_EXIST,
				SpogMessageCode.ERROR_MESSAGE_ELEMENT_NOT_UUID, test);

		test.log(LogStatus.INFO,
				"Get report list filters by filter id for specified user when user id is invalid using csr token");
		spogReportServer.getReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, "uuid", organization_ids,
				filter_id, reportlistfilters, SpogConstants.REQUIRED_INFO_NOT_EXIST,
				SpogMessageCode.ERROR_MESSAGE_ELEMENT_NOT_UUID, test);

		test.log(LogStatus.INFO,
				"Get report list filters by filter id for specified user when user id is invalid using csr_readonly token");
		spogReportServer.getReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, "uuid", organization_ids,
				filter_id, reportlistfilters, SpogConstants.REQUIRED_INFO_NOT_EXIST,
				SpogMessageCode.ERROR_MESSAGE_ELEMENT_NOT_UUID, test);

		// Filterid is not uuid
		test.log(LogStatus.INFO, "Get report list filters by filter id for specified user when filter id is invalid");
		spogReportServer.getReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id, organization_ids,
				"filterid", reportlistfilters, SpogConstants.REQUIRED_INFO_NOT_EXIST,
				SpogMessageCode.ERROR_MESSAGE_ELEMENT_NOT_UUID, test);

		test.log(LogStatus.INFO,
				"Get report list filters by filter id for specified user when filter id is invalid using csr token");
		spogReportServer.getReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id, organization_ids,
				"filterid", reportlistfilters, SpogConstants.REQUIRED_INFO_NOT_EXIST,
				SpogMessageCode.ERROR_MESSAGE_ELEMENT_NOT_UUID, test);

		test.log(LogStatus.INFO,
				"Get report list filters by filter id for specified user when filter id is invalid using csr_readonly token");
		spogReportServer.getReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id, organization_ids,
				"filterid", reportlistfilters, SpogConstants.REQUIRED_INFO_NOT_EXIST,
				SpogMessageCode.ERROR_MESSAGE_ELEMENT_NOT_UUID, test);

		// given random userid
		String random_user_id = UUID.randomUUID().toString();
		test.log(LogStatus.INFO,
				"Get report list filters by filter id for specified user when user id that does not exit is passed");
		spogReportServer.getReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, random_user_id,
				organization_ids, filter_id, reportlistfilters, SpogConstants.RESOURCE_NOT_EXIST,
				SpogMessageCode.USER_ID_DOESNOT_EXIST, test);

		test.log(LogStatus.INFO,
				"Get report list filters by filter id for specified user when user id that does not exit is passed using csr token");
		spogReportServer.getReportListFiltersForSpecifiedUserByFilterIdWithCheck(ti.csr_token, random_user_id,
				organization_ids, filter_id, reportlistfilters, SpogConstants.RESOURCE_NOT_EXIST,
				SpogMessageCode.USER_ID_DOESNOT_EXIST, test);

		test.log(LogStatus.INFO,
				"Get report list filters by filter id for specified user when user id that does not exit is passed using csr_readonly token");
		spogReportServer.getReportListFiltersForSpecifiedUserByFilterIdWithCheck(ti.csr_readonly_token, random_user_id,
				organization_ids, filter_id, reportlistfilters, SpogConstants.RESOURCE_NOT_EXIST,
				SpogMessageCode.USER_ID_DOESNOT_EXIST, test);

		// given random filter id
		String random_filter_id = UUID.randomUUID().toString();
		test.log(LogStatus.INFO,
				"Get report list filters by filter id for specified user when filter id that does not exit is passed");
		spogReportServer.getReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id, organization_ids,
				random_filter_id, reportlistfilters, SpogConstants.RESOURCE_NOT_EXIST,
				SpogMessageCode.FILTER_NOT_FOUND_WITH_USER_ID, test);

		test.log(LogStatus.INFO,
				"Get report list filters by filter id for specified user when filter  id that does not exit is passed using csr token");
		spogReportServer.getReportListFiltersForSpecifiedUserByFilterIdWithCheck(ti.csr_token, user_id,
				organization_ids, random_filter_id, reportlistfilters, SpogConstants.RESOURCE_NOT_EXIST,
				SpogMessageCode.FILTER_NOT_FOUND_WITH_USER_ID, test);

		test.log(LogStatus.INFO,
				"Get report list filters by filter id for specified user when filter  id that does not exit is passed using csr_readonly token");
		spogReportServer.getReportListFiltersForSpecifiedUserByFilterIdWithCheck(ti.csr_readonly_token, user_id,
				organization_ids, random_filter_id, reportlistfilters, SpogConstants.RESOURCE_NOT_EXIST,
				SpogMessageCode.FILTER_NOT_FOUND_WITH_USER_ID, test);*/

		// deleted the filter
		spogReportServer.deleteReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		// get the deleted Filter
		spogReportServer.getReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id, organization_ids,
				filter_id, reportlistfilters, SpogConstants.RESOURCE_NOT_EXIST,
				SpogMessageCode.FILTER_NOT_FOUND_WITH_USER_ID, test);

	}

	@DataProvider(name = "Getreportslistfilters_403", parallel = false)
	public final Object[][] Getreportslistfilters2() {

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

				// 3 tier cases
				{ "rootmsp- msp2", ti.root_msp_org1_id, ti.root_msp_org1_user1_token, ti.root_msp_org2_user1_token,
						ti.root_msp_org1_user1_id, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_source_groups", "monthly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 3 },
				{ "rootmsp-direct", ti.root_msp_org1_id, ti.root_msp_org1_user1_token, ti.direct_org1_user1_token,
						ti.root_msp_org1_user1_id, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_source_groups", "monthly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 3 },
				{ "rootmsp-sub", ti.root_msp_org1_id, ti.root_msp_org1_user1_token, ti.root_msp1_suborg1_user1_token,
						ti.root_msp_org1_user1_id, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_source_groups", "monthly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 3 },
				{ "rootsuborg-sub2", ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_token,
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

				{ "submsp-rootsuborg", ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_user1_token,
						ti.root_msp1_suborg2_user1_token, ti.root_msp1_submsp1_user1_id,
						spogServer.ReturnRandom("report"), "last_1_month,last_3_months", "all_sources", "quarterly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				{ "submsp-direct", ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_user1_token,
						ti.direct_org1_user1_token, ti.root_msp1_submsp1_user1_id, spogServer.ReturnRandom("report"),
						"last_1_month,last_3_months", "all_sources", "quarterly", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				{ "submsp-rootmsp", ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_user1_token,
						ti.root_msp_org2_user1_token, ti.root_msp1_submsp1_user1_id, spogServer.ReturnRandom("report"),
						"last_1_month,last_3_months", "all_sources", "quarterly", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				{ "submsp-submsp_sub", ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_user1_token,
						ti.msp1_submsp1_suborg1_user1_token, ti.root_msp1_submsp1_user1_id,
						spogServer.ReturnRandom("report"), "last_1_month,last_3_months", "all_sources", "quarterly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				{ "submsp-msp_maa", ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_user1_token,
						ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp1_submsp1_user1_id,
						spogServer.ReturnRandom("report"), "last_1_month,last_3_months", "all_sources", "quarterly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },

				{ "submsp_sub-rootsuborg", ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_token,
						ti.root_msp1_suborg2_user1_token, ti.msp1_submsp1_suborg1_user1_id,
						spogServer.ReturnRandom("report"), "last_1_month,last_3_months", "all_sources", "quarterly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				{ "submsp_sub-direct", ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_token,
						ti.direct_org1_user1_token, ti.msp1_submsp1_suborg1_user1_id, spogServer.ReturnRandom("report"),
						"last_1_month,last_3_months", "all_sources", "quarterly", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				{ "submsp_sub-rootmsp", ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_token,
						ti.root_msp_org2_user1_token, ti.msp1_submsp1_suborg1_user1_id,
						spogServer.ReturnRandom("report"), "last_1_month,last_3_months", "all_sources", "quarterly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				{ "submsp_sub-submsp_sub2", ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_token,
						ti.msp2_submsp1_suborg2_user1_token, ti.msp1_submsp1_suborg1_user1_id,
						spogServer.ReturnRandom("report"), "last_1_month,last_3_months", "all_sources", "quarterly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				{ "submsp_sub-msp_maa", ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_token,
						ti.root_msp_org1_msp_accountadmin1_token, ti.msp1_submsp1_suborg1_user1_id,
						spogServer.ReturnRandom("report"), "last_1_month,last_3_months", "all_sources", "quarterly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },

		};
	}

	@Test(dataProvider = "Getreportslistfilters_403")
	public void GetReportListFilters_403(String organizationType, String organization_ids, String validToken,
			String anothertoken, String user_id, String ReportName, String DateRangeTypes, String type,
			String schedule_frequency, long generated_ts_start, long generated_ts_end, String filter_name,
			boolean is_default, String groupName, String groupDescription, int noOfSourceGroupsToCreate) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
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

		String source_group_ids = null, source_group_id = null;
		/*for (int i = 0; i < noOfSourceGroupsToCreate; i++) {
			source_group_id = spogServer.createGroupWithCheck2(validToken, orgId, groupName + i, groupDescription,
					test);
			if (i > 0) {
				source_group_ids = source_group_ids + "," + source_group_id;
			} else {
				source_group_ids = source_group_id;
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

		spogServer.setToken(anothertoken);
		spogReportServer.getReportListFiltersForSpecifiedUserByFilterIdWithCheck(anothertoken, user_id,
				organization_ids, filter_id, reportlistfilters, SpogConstants.INSUFFICIENT_PERMISSIONS,
				SpogMessageCode.RESOURCE_PERMISSION_DENY, test);

	}

	@DataProvider(name = "Getreportslistfilters_404", parallel = false)
	public final Object[][] Getreportslistfilters3() {

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

				{ "root_sub", ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,
						spogServer.ReturnRandom("report"), "last_1_month,last_3_months", "all_sources", "quarterly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				{ "rootmsp", ti.root_msp_org1_id, ti.root_msp_org1_user1_token, ti.root_msp_org1_user1_id,
						spogServer.ReturnRandom("report"), "last_7_days", "selected_source_groups", "monthly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				{ "submsp_sub", ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_token,
						ti.msp1_submsp1_suborg1_user1_id, spogServer.ReturnRandom("report"),
						"last_1_month,last_3_months", "all_sources", "quarterly", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				{ "submsp", ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_user1_token,
						ti.root_msp1_submsp1_user1_id, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_source_groups", "monthly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 3 },

		};
	}

	@Test(dataProvider = "Getreportslistfilters_404")
	public void GetReportListFilters_404(String organizationType, String organization_ids, String validToken,
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

		String source_group_ids = null, source_group_id = null;
		/*for (int i = 0; i < noOfSourceGroupsToCreate; i++) {
			source_group_id = spogServer.createGroupWithCheck2(validToken, orgId, groupName + i, groupDescription,
					test);
			if (i > 0) {
				source_group_ids = source_group_ids + "," + source_group_id;
			} else {
				source_group_ids = source_group_id;
			}

		}*/

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

		/*
		 * spogServer.setToken(ti.csr_token); test.log(LogStatus.INFO,
		 * "deleted userid"); spogServer.CheckDeleteUserByIdStatus(user_id,
		 * SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		 * 
		 * test.log(LogStatus.INFO,
		 * "Get report list filters by filter id for specified user when user id is random using csr token"
		 * );
		 * spogReportServer.getReportListFiltersForSpecifiedUserByFilterIdWithCheck(ti.
		 * csr_token, user_id, organization_ids, filter_id, reportlistfilters,
		 * SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.USER_ID_DOESNOT_EXIST,
		 * test);
		 * 
		 * test.log(LogStatus.INFO,
		 * "Get report list filters by filter id for specified user when user id is random using csr_readonly token"
		 * );
		 * spogReportServer.getReportListFiltersForSpecifiedUserByFilterIdWithCheck(ti.
		 * csr_readonly_token, user_id, organization_ids, filter_id, reportlistfilters,
		 * SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.USER_ID_DOESNOT_EXIST,
		 * test);
		 */

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
