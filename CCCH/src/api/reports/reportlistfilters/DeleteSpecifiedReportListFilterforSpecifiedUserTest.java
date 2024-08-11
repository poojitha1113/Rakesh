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

public class DeleteSpecifiedReportListFilterforSpecifiedUserTest extends base.prepare.Is4Org {
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

	@DataProvider(name = "Deletereportslistfilters", parallel = false)
	public final Object[][] Getreportslistfilters() {

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
				{ "csrreadonly", ti.csr_org_id, ti.csr_readonly_token, ti.csr_readonly_admin_user_id,
						spogServer.ReturnRandom("report"), "last_1_month,last_3_months", "all_sources", "quarterly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },

		};
	}

	@Test(dataProvider = "Deletereportslistfilters")
	public void DeleteSpecifiedReportFilter_200(String organizationType, String organization_ids, String validToken,
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
		spogServer.setToken(validToken);
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
		test.log(LogStatus.INFO, "Composing Filter List for Reports in org: " + organizationType);
		HashMap<String, Object> reportlistfilters = spogReportServer.composeReportListFilterInfo(ReportName,
				DateRangeTypes, type, source_group_ids, organization_ids, schedule_frequency, generated_ts_start,
				generated_ts_end, filter_name, is_default);

		Response respone = spogReportServer.createReportListFiltersForSpecifiedUserWithCheck(validToken, user_id,
				organization_ids, reportlistfilters, SpogConstants.SUCCESS_POST, null, test);

		String filter_id = respone.then().extract().path("data.filter_id");
		test.log(LogStatus.INFO, "The filter id is " + filter_id);

		spogServer.setToken(validToken);
		spogReportServer.deleteReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

	}

	@Test(dataProvider = "Deletereportslistfilters")
	public void DeleteSpecifiedReportFilter_401(String organizationType, String organization_ids, String validToken,
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
		/*
		 * if (!(organizationType.contains("csrreadonly"))) { for (int i = 0; i <
		 * noOfSourceGroupsToCreate; i++) { source_group_id =
		 * spogServer.createGroupWithCheck2(validToken, orgId, groupName + i,
		 * groupDescription, test); if (i > 0) { source_group_ids = source_group_ids +
		 * "," + source_group_id; } else { source_group_ids = source_group_id; } } }
		 */

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
		spogReportServer.deleteReportListFiltersForSpecifiedUserByFilterIdWithCheck("", user_id, filter_id,
				SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);

		// invalid token
		spogReportServer.deleteReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken + "junk", user_id,
				filter_id, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);
/*
		// Userid is not uuid
		spogReportServer.deleteReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, "uuid", filter_id,
				SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ERROR_MESSAGE_ELEMENT_NOT_UUID, test);

		spogReportServer.deleteReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id, "uuid",
				SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ERROR_MESSAGE_ELEMENT_NOT_UUID, test);
		// given random userid
		String random_user_id = UUID.randomUUID().toString();

		spogReportServer.deleteReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, random_user_id,
				filter_id, SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.USER_ID_DOESNOT_EXIST, test);

		// given random filter_id
		String random_filter_id = UUID.randomUUID().toString();

		spogReportServer.deleteReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id,
				random_filter_id, SpogConstants.RESOURCE_NOT_EXIST,
				SpogMessageCode.FILTER_NOT_FOUND_WITH_USER_ID, test);

		// deleted the filter
		spogReportServer.deleteReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		// Agian the deleted Filter
		spogReportServer.deleteReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id, filter_id,
				SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.FILTER_NOT_FOUND_WITH_USER_ID, test);*/

	}

	@DataProvider(name = "Deletereportslistfilters_403", parallel = false)
	public final Object[][] Deletereportslistfilters2() {

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

				// csrreadonly cases
				{ "csrreadonly", ti.csr_org_id, ti.csr_readonly_token, ti.direct_org1_user1_token,
						ti.csr_readonly_admin_user_id, spogServer.ReturnRandom("report"), "last_1_month,last_3_months",
						"all_sources", "quarterly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 3 },
				{ "csrreadonly", ti.csr_org_id, ti.csr_readonly_token, ti.normal_msp_org1_user1_token,
						ti.csr_readonly_admin_user_id, spogServer.ReturnRandom("report"), "last_1_month,last_3_months",
						"all_sources", "quarterly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 3 },
				{ "csrreadonly", ti.csr_org_id, ti.csr_readonly_token, ti.normal_msp1_suborg1_user1_token,
						ti.csr_readonly_admin_user_id, spogServer.ReturnRandom("report"), "last_1_month,last_3_months",
						"all_sources", "quarterly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 3 },

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

	@Test(dataProvider = "Deletereportslistfilters_403")
	public void DeleteSpecifiedReportFilter_403(String organizationType, String organization_ids, String validToken,
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

		spogServer.setToken(anothertoken);
		spogReportServer.deleteReportListFiltersForSpecifiedUserByFilterIdWithCheck(anothertoken, user_id, filter_id,
				SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY, test);

	}

	@DataProvider(name = "Deletereportslistfilters_404", parallel = false)
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
				{ "csrreadonly", ti.csr_org_id, ti.csr_readonly_token, ti.csr_readonly_admin_user_id,
						spogServer.ReturnRandom("report"), "last_7_days", "selected_source_groups", "monthly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },

		};
	}

	/*@Test(dataProvider = "Deletereportslistfilters_404")
	public void DeleteSpecifiedReportFilter_404(String organizationType, String organization_ids, String validToken,
			String user_id, String ReportName, String DateRangeTypes, String type, String schedule_frequency,
			long generated_ts_start, long generated_ts_end, String filter_name, boolean is_default, String groupName,
			String groupDescription, int noOfSourceGroupsToCreate) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		ArrayList<String> OrgIds = new ArrayList<>();

		spogServer.setToken(validToken);

		// orgId for creating source_groups
		String orgId = null;
		if (organizationType.contains("suborg")) {
			orgId = ti.normal_msp1_suborg1_id;
		} else {
			orgId = spogServer.GetLoggedinUserOrganizationID();
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

		test.log(LogStatus.INFO, "Delete report list filters with random user id for user : " + organizationType);
		spogReportServer.deleteReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken,
				UUID.randomUUID().toString(), filter_id, SpogConstants.RESOURCE_NOT_EXIST,
				SpogMessageCode.USER_ID_DOESNOT_EXIST, test);

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
