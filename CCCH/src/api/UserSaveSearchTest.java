package api;

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
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.DestinationType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.Log4SPOGServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGJobServer;
import InvokerServer.SPOGRecoveredResourceServer;
import InvokerServer.SPOGReportServer;
import InvokerServer.SPOGServer;
import InvokerServer.Source4SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class UserSaveSearchTest extends base.prepare.Is4Org {

	private SPOGServer spogServer;
	private SPOGReportServer spogReportServer;
	private SPOGJobServer spogJobServer;
	private UserSpogServer userSpogServer;
	private Policy4SPOGServer policySpogServer;
	private SPOGRecoveredResourceServer spogRecoveredResourceServer;
	private Source4SPOGServer source4SpogServer;
	private SPOGDestinationServer spogDestinationServer;
	private Log4SPOGServer log4SPOGServer;

	private ExtentTest test;
	private TestOrgInfo ti;

	String datacenters[] = null;

	@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runningMachine, String buildVersion) {

		spogServer = new SPOGServer(baseURI, port);
		spogJobServer = new SPOGJobServer(baseURI, port);
		spogReportServer = new SPOGReportServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		log4SPOGServer = new Log4SPOGServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		spogRecoveredResourceServer = new SPOGRecoveredResourceServer(baseURI, port);
		source4SpogServer = new Source4SPOGServer(baseURI, port);
		policySpogServer = new Policy4SPOGServer(baseURI, port);

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

	@DataProvider(name = "userSaveSearchTest_valid")
	public final Object[][] userSaveSearchTest_valid() {
		return new Object[][] { { "Direct", "direct", ti.direct_org1_id, ti.csr_token, ti.csr_admin_user_id },
				{ "MSP", "msp", ti.normal_msp_org1_id, ti.csr_token, ti.csr_admin_user_id },
				{ "SUBORG", "msp_child", ti.normal_msp1_suborg1_id, ti.csr_token, ti.csr_admin_user_id },
				{ "MSP-SUBORG", "msp_child", ti.normal_msp1_suborg1_id, ti.normal_msp_org1_user1_token,
						ti.normal_msp_org1_user1_id },
				{ "MSPAccountAdmin-SUBORG", "msp_child", ti.normal_msp1_suborg1_id,
						ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id },

				// 3 tier cases

				{ "root_msp", "msp", ti.root_msp_org1_id, ti.csr_token, ti.csr_admin_user_id },
				{ "root_sub", "msp_child", ti.root_msp1_suborg1_id, ti.csr_token, ti.csr_admin_user_id },
				/*
				 * { "rootsub-rootmsp", "msp_child", ti.root_msp1_suborg1_id,
				 * ti.root_msp_org1_user1_token, ti.root_msp1_suborg1_user1_id }, {
				 * "submsp-root", "msp", ti.root_msp1_submsp_org1_id,
				 * ti.root_msp_org1_user1_token, ti.root_msp1_submsp1_user1_id }, {
				 * "submsp_sub-submsp", "msp", ti.msp1_submsp1_sub_org1_id,
				 * ti.root_msp1_submsp1_user1_token, ti.msp1_submsp1_suborg1_user1_id }, {
				 * "submsp_sub-submsp_maa", "msp", ti.msp1_submsp1_sub_org1_id,
				 * ti.root_msp1_submsp1_account_admin_token, ti.msp1_submsp1_suborg1_user1_id },
				 * { "rootsub-rootmsp_maa", "msp_child", ti.root_msp1_suborg1_id,
				 * ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp1_suborg1_user1_id },
				 */ };

	}

	@Test(dataProvider = "userSaveSearchTest_valid")
	public void ReportFilter_userSaveSearch_200(String orgtype, String view_type, String Org_id, String validToken,
			String user_id) {
		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Ramya Nagepalli");

		Response response = null;

		HashMap<String, Object> reportfilterInfo = new HashMap<String, Object>();

		String source_name = spogServer.ReturnRandom("source_name");
		String destination_name = spogServer.ReturnRandom("destination_name");
		String policy_id = UUID.randomUUID().toString();
		String destination_id = UUID.randomUUID().toString();
		String organization_ids = Org_id;
		String DateRangeType = "last_24_hours";
		long start_ts_dr = System.currentTimeMillis();
		long end_ts_dr = System.currentTimeMillis() + 360000;
		String report_type = "backup_jobs";
		String destination_type = DestinationType.cloud_direct_volume.toString();
		String job_type = "backup";
		String retention_id = "7D";
		String backup_schedule = "Tue,Wed,Mon";
		String filter_name = spogServer.ReturnRandom("filter_name");
		boolean is_default = true;
		String report_filter_type = "all";

		// report filters - for LoggedInUser
		test.log(LogStatus.INFO, "Composing Report filters");
		reportfilterInfo = spogReportServer.composeReportFilterInfo(organization_ids, DateRangeType, start_ts_dr,
				end_ts_dr, "daily", filter_name, false, report_filter_type);

		test.log(LogStatus.INFO, "create report filters for logged in user");
		response = spogReportServer.createReportFilterForSpecifiedUserWithCheck(null, user_id, Org_id, validToken,
				reportfilterInfo, SpogConstants.SUCCESS_POST, SpogMessageCode.SUCCESS_POST, test);

		// validate the view type for the created filters
		test.log(LogStatus.INFO, "validate the view type for the created filters");
		spogServer.validateViewType(response, view_type, test);

		String filter_id = response.then().extract().path("data.filter_id").toString();

		test.log(LogStatus.INFO, "get report filters for logged in user");

		response = spogReportServer.getReportFiltersForLoggedInUserByFilterIdWithCheck("organization_id=" + Org_id,
				validToken, filter_id, reportfilterInfo, SpogConstants.SUCCESS_GET_PUT_DELETE,
				SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

		// validate the view type for the created filters
		test.log(LogStatus.INFO, "validate the view type for the created filters");
		spogServer.validateViewType(response, view_type, test);

		response = spogReportServer.updateReportFiltersForLoggedInUser("organization_id=" + Org_id, validToken,
				filter_id, reportfilterInfo, reportfilterInfo, SpogConstants.SUCCESS_GET_PUT_DELETE,
				SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

		// validate the view type for the created filters
		test.log(LogStatus.INFO, "validate the view type for the created filters");
		spogServer.validateViewType(response, view_type, test);

		test.log(LogStatus.INFO, "delte report filters for logged in user");
		spogReportServer.deleteReportFiltersForLoggedInUser(validToken, filter_id, SpogConstants.SUCCESS_GET_PUT_DELETE,
				SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

	}

	@DataProvider(name = "createreportlistfilters", parallel = false)
	public final Object[][] createreportlistfilters() {

		return new Object[][] {

				{ "Create report list filters for direct organization with all filters", ti.direct_org1_id,
						ti.csr_token, spogServer.ReturnRandom("report"), "last_7_days", "selected_organizations",
						"weekly", System.currentTimeMillis(), System.currentTimeMillis() + 1000,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, "direct", ti.csr_admin_user_id },

				/*
				 * { "Create report list filters for msp organization", ti.normal_msp_org1_id,
				 * ti.csr_token, spogServer.ReturnRandom("report"), "last_7_days",
				 * "selected_source_groups", "monthly", System.currentTimeMillis(),
				 * System.currentTimeMillis() + 1000, spogServer.ReturnRandom("filter"), true,
				 * spogServer.ReturnRandom("groupName"),
				 * spogServer.ReturnRandom("groupDescription"), 3, "msp", ti.csr_admin_user_id
				 * },
				 * 
				 * { "Create report list filters for sub organization without type filter",
				 * ti.normal_msp1_suborg1_id, ti.csr_token, spogServer.ReturnRandom("report"),
				 * "today,last_7_days", "", "daily", System.currentTimeMillis(),
				 * System.currentTimeMillis() + 1000, spogServer.ReturnRandom("filter"), true,
				 * spogServer.ReturnRandom("groupName"),
				 * spogServer.ReturnRandom("groupDescription"), 3, "msp_child",
				 * ti.csr_admin_user_id }, {
				 * "Create report list filters for sub organization without type filter",
				 * ti.normal_msp1_suborg1_id, ti.normal_msp_org1_user1_token,
				 * spogServer.ReturnRandom("report"), "today,last_7_days", "", "daily",
				 * System.currentTimeMillis(), System.currentTimeMillis() + 1000,
				 * spogServer.ReturnRandom("filter"), true,
				 * spogServer.ReturnRandom("groupName"),
				 * spogServer.ReturnRandom("groupDescription"), 3, "msp_child",
				 * ti.normal_msp_org1_user1_id }, {
				 * "Create report list filters for sub organization without type filter",
				 * ti.normal_msp1_suborg1_id, ti.normal_msp_org1_msp_accountadmin1_token,
				 * spogServer.ReturnRandom("report"), "today,last_7_days", "", "daily",
				 * System.currentTimeMillis(), System.currentTimeMillis() + 1000,
				 * spogServer.ReturnRandom("filter"), true,
				 * spogServer.ReturnRandom("groupName"),
				 * spogServer.ReturnRandom("groupDescription"), 3, "msp_child",
				 * ti.normal_msp_org1_msp_accountadmin1_id },
				 */

		};
	}

	// Valid cases - 201
	@Test(dataProvider = "createreportlistfilters")
	public void SavedSearch_ReportListFilters(String caseType, String organization_ids, String validToken,
			String ReportName, String DateRangeTypes, String type, String schedule_frequency, long generated_ts_start,
			long generated_ts_end, String filter_name, Boolean is_default, String groupName, String groupDescription,
			int noOfSourceGroupsToCreate, String view_type, String user_id) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		ArrayList<String> OrgIds = new ArrayList<>();

		spogServer.setToken(validToken);

		// orgId for creating source_groups
		String orgId = null;
		if (caseType.contains("sub")) {
			orgId = ti.normal_msp1_suborg1_id;

		} else if (caseType.contains("direct")) {
			orgId = ti.direct_org1_id;
		} else {
			orgId = ti.normal_msp_org1_id;
		}

		spogReportServer.setToken(validToken);

		test.log(LogStatus.INFO, "Composing Filter List for Reports");
		HashMap<String, Object> reportlistfilters = spogReportServer.composeReportFilterInfo(orgId, DateRangeTypes,
				generated_ts_start, generated_ts_end, schedule_frequency, filter_name, is_default, "all");

		test.log(LogStatus.INFO, caseType);
		Response response = spogReportServer.createReportListFiltersForLoggedInUserWithCheck("organization_id=" + orgId,
				validToken, reportlistfilters, SpogConstants.SUCCESS_POST, SpogMessageCode.SUCCESS_POST, test);

		// validate the view type for the created filters
		test.log(LogStatus.INFO, "validate the view type for the created filters");
		spogServer.validateViewType(response, view_type, test);

		String filter_id = response.then().extract().path("data.filter_id");
		test.log(LogStatus.INFO, "The filter id is " + filter_id);

		// get report list filters
		response = spogReportServer.getReportListFiltersForLoggedInUserByFilterIdWithCheck("organization_id=" + orgId,
				validToken, filter_id, SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

		// validate the view type for the created filters
		test.log(LogStatus.INFO, "validate the view type for the created filters");
		spogServer.validateViewType(response, view_type, test);

		// update report list filters
		response = spogReportServer.updateReportListFiltersForLoggedInUserByFilterIdWithCheck(
				"organization_id=" + orgId, validToken, filter_id, reportlistfilters,
				SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

		// validate the view type for the created filters
		test.log(LogStatus.INFO, "validate the view type for the created filters");
		spogServer.validateViewType(response, view_type, test);

		test.log(LogStatus.INFO, "Deleting Filter List for Reports");
		spogReportServer.deleteReportListFiltersForLoggedInUserByFilterIdWithCheck(validToken, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

	}

	@DataProvider(name = "create_destination_filter_for_loggedin_user_valid")
	public final Object[][] createDestinationFilterValidParams() {
		return new Object[][] {

				// different policy id format
				{ ti.csr_token, ti.direct_org1_id, ti.csr_admin_user_id, "filterName0", "destinationName",
						UUID.randomUUID().toString(), "share_folder", "true", "direct" },
				{ ti.csr_token, ti.normal_msp_org1_id, ti.csr_admin_user_id, "filterName1", "destinationName",
						UUID.randomUUID().toString() + "," + UUID.randomUUID().toString(), "share_folder", "true",
						"msp" },
				{ ti.csr_token, ti.normal_msp1_suborg1_id, ti.csr_admin_user_id, "filterName2", "destinationName",
						"emptyarray", "share_folder", "true", "msp_child" },
				{ ti.normal_msp_org1_user1_token, ti.normal_msp1_suborg1_id, ti.normal_msp_org1_user1_id, "filterName3",
						"destinationName", null, "share_folder", "true", "msp_child" },
				{ ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp1_suborg1_id,
						ti.normal_msp_org1_msp_accountadmin1_id, "filterName4", "destinationName", "none",
						"share_folder", "true", "msp_child" },

		};
	}

	@Test(dataProvider = "create_destination_filter_for_loggedin_user_valid")
	public void DestinationFilterValid(String validToken, String org_id, String user_id, String filterName,
			String destinationName, String policyID, String destinationType, String isDefault, String view_type) {

		String prefix = RandomStringUtils.randomAlphanumeric(8);
		filterName = prefix + filterName;
		spogDestinationServer.setToken(validToken);
		spogServer.setToken(validToken);
		if (null != policyID) {
			String[] splitted_policyID = policyID.split(",");
			Arrays.sort(splitted_policyID);
			policyID = String.join(",", splitted_policyID);
		}
		Response response = null;
		test.log(LogStatus.INFO, "createDestinationFilterForLoggedinUser");
		response = spogDestinationServer.createDestinationFilter(org_id, user_id, filterName, destinationName, policyID,
				destinationType, "false", test);
		String filter_id = response.then().extract().path("data.filter_id").toString();

		// validate the view type for the created filters
		test.log(LogStatus.INFO, "validate the view type for the created filters");
		spogServer.validateViewType(response, view_type, test);

		// update
		test.log(LogStatus.INFO, "updateDestinationFilterForLoggedInUserWithCheck");
		response = spogDestinationServer.updateDestinationFilterForLoggedInUserWithCheck(filter_id, filterName,
				destinationName, policyID, destinationType, isDefault, test, false);

		// validate the view type for the created filters
		test.log(LogStatus.INFO, "validate the view type for the created filters");
		spogServer.validateViewType(response, view_type, test);

		// get
		test.log(LogStatus.INFO, "getspecifiedDestinationFilterForLoggedInUserwithCheck");
		response = spogDestinationServer.getspecifiedDestinationFilterForLoggedInUserwithCheck(
				"organization_id=" + org_id, filter_id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE,
				SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

		// validate the view type for the created filters
		test.log(LogStatus.INFO, "validate the view type for the created filters");
		// spogServer.validateViewType(response, view_type, test);

		test.log(LogStatus.INFO, "deletedestinationfilterbyfilterId");
		spogDestinationServer.deletedestinationfilterbyfilterId(user_id, filter_id, validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
	}
	// job filters

	@DataProvider(name = "SavedSearch_job_filter_for_loggedin_user_valid")
	public final Object[][] createJobFilterValidParams() {
		return new Object[][] {
				// different users
				{ ti.csr_token, ti.direct_org1_id, "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()),
						String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true", "direct",
						ti.csr_admin_user_id },
				{ ti.csr_token, ti.normal_msp_org1_id, "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()),
						String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true", "msp",
						ti.csr_admin_user_id },
				{ ti.csr_token, ti.normal_msp1_suborg1_id, "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()),
						String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true", "msp_child",
						ti.csr_admin_user_id },
				{ ti.normal_msp_org1_user1_token, ti.normal_msp1_suborg1_id, "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()),
						String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true", "msp_child",
						ti.normal_msp_org1_user1_id },
				{ ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp1_suborg1_id, "finished",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom",
						String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),
						"sourceName", "filterName", "true", "msp_child", ti.normal_msp_org1_msp_accountadmin1_id }, };
	}

	@Test(dataProvider = "SavedSearch_job_filter_for_loggedin_user_valid")
	public void SavedSearch_JobFilter_Valid(String validtoken, String org_id, String jobStatus, String policyID,
			String resourceID, String jobType, String startTimeType, String startTimeTSStart, String startTimeTSEnd,
			String sourceName, String filterName, String isDefault, String view_type, String user_id) {
		spogServer.setToken(validtoken);

		String prefix = RandomStringUtils.randomAlphanumeric(8);
		filterName = prefix + filterName;
		if (null != policyID) {
			String[] splitted_policyID = policyID.split(",");
			Arrays.sort(splitted_policyID);
			policyID = String.join(",", splitted_policyID);
		}

		if (null != resourceID) {
			String[] splitted_resourceID = resourceID.split(",");
			Arrays.sort(splitted_resourceID);
			resourceID = String.join(",", splitted_resourceID);
		}
		spogJobServer.setToken(spogServer.getJWTToken());
		Response response = spogJobServer.createJobFilterForSpecifiedUserWithCheckEx_savedsearch(
				"organization_id=" + org_id, user_id, org_id, jobStatus, policyID, jobType, startTimeType,
				startTimeTSStart, startTimeTSEnd, "all", filterName, isDefault, test);

		String filter_id = response.then().extract().path("data.filter_id").toString();

		// validate the view type for the created filters
		test.log(LogStatus.INFO, "validate the view type for the created filters");
		spogServer.validateViewType(response, view_type, test);

		// get job filter for loggedin user
		test.log(LogStatus.INFO, "getJobFilterForLoggedinUser by filter_id");
		response = spogJobServer.getJobFilterForLoggedinUserByID(filter_id, test);

		// validate the view type for the created filters
		test.log(LogStatus.INFO, "validate the view type for the created filters");
		spogServer.validateViewType(response, view_type, test);

		test.log(LogStatus.INFO, "update JobFilterForLoggedinUser by filter_id");
		response = spogJobServer.updateJobFilterWithCheck("organization_id=" + org_id, user_id, filter_id, jobStatus,
				policyID, resourceID, jobType, startTimeTSStart, startTimeTSEnd, filterName, isDefault, test);

		// validate the view type for the created filters
		test.log(LogStatus.INFO, "validate the view type for the created filters");
		spogServer.validateViewType(response, view_type, test);

		// delete the created job filters for logged in user by filter_id
		test.log(LogStatus.INFO, "delete the created job filters for logged in user by filter_id");
		spogJobServer.deleteJobFilterforloggedInUser(filter_id, validtoken, SpogConstants.SUCCESS_GET_PUT_DELETE,
				SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
	}
	// log filters

	@DataProvider(name = "loginUserToCreateDifferentDefaultFlag")
	public final Object[][] getLoginUserToCreateDifferentDefaultFlag() {
		return new Object[][] { /*
								 * { "csr", ti.direct_org1_id, ti.csr_token, "false", "direct",
								 * ti.csr_admin_user_id }, { "csr", ti.normal_msp_org1_id, ti.csr_token,
								 * "false", "msp", ti.csr_admin_user_id }, { "csr", ti.normal_msp1_suborg1_id,
								 * ti.csr_token, "false", "msp_child", ti.csr_admin_user_id }, { "msp_child",
								 * ti.normal_msp1_suborg1_id, ti.normal_msp_org1_user1_token, "false",
								 * "msp_child", ti.normal_msp_org1_user1_id }, { "msp_child",
								 * ti.normal_msp1_suborg1_id, ti.normal_msp_org1_msp_accountadmin1_token,
								 * "false", "msp_child", ti.normal_msp_org1_msp_accountadmin1_id },
								 */ };
	}

	@Test(dataProvider = "loginUserToCreateDifferentDefaultFlag")
	public void Logfilter_save_search(String loginUserType, String organization_id, String validtoken,
			String default_flag, String view_type, String user_id) {
		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Ramya,Nagepalli");

		String filter_name = spogServer.returnRandomUUID();

		log4SPOGServer.setToken(spogServer.getJWTToken());
		test.log(LogStatus.INFO, "Login as " + loginUserType + " admin");
		Response response = log4SPOGServer.createLogFilterForLoggedInUserwithCheck_savedsearch(organization_id,
				filter_name, null, null, "none", "information", default_flag, test);

		String filter_id = response.then().extract().path("data.filter_id").toString();
		test.log(LogStatus.INFO, loginUserType + " admin create log filter with default flag is " + default_flag);

		// validate the view type for the created filters
		test.log(LogStatus.INFO, "validate the view type for the created filters");
		spogServer.validateViewType(response, view_type, test);

		// update log filters for logged in user
		response = log4SPOGServer.updateLogFilterForLoggedInUserwithCheck("", user_id, filter_id, organization_id,
				filter_name, null, null, "none", "information", default_flag, test);

		// validate the view type for the created filters
		test.log(LogStatus.INFO, "validate the view type for the created filters");
		spogServer.validateViewType(response, view_type, test);

		// get log filters for logged in user
		response = log4SPOGServer.getLogFiltersForLoggedInUser("", test);

		// validate the view type for the created filters
		test.log(LogStatus.INFO, "validate the view type for the created filters");
		// spogServer.validateViewType(response, "["+view_type+"]", test);

		// Delete the created filter
		/*
		 * log4SPOGServer.deleteLogFilterforloggedinUser(filter_id, validtoken,
		 * SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL,
		 * test);
		 */
	}

	// policy filters

	@DataProvider(name = "valid_params")
	public final Object[][] createPolicyFilterValidParams() {
		return new Object[][] {
				{ ti.csr_token, spogServer.ReturnRandom("filter_name1"), "policy_name",
						UUID.randomUUID().toString() + "," + UUID.randomUUID().toString(), "active,finished",
						"deploying,failure", "false", "direct", ti.direct_org1_id, ti.csr_admin_user_id },
				{ ti.csr_token, spogServer.ReturnRandom("filter_name2"), "policy_name", UUID.randomUUID().toString(),
						"finished", "failure", "false", "msp", ti.normal_msp_org1_id, ti.csr_admin_user_id },
				{ ti.csr_token,spogServer.ReturnRandom("filter_name3"), "policy_name", UUID.randomUUID().toString(), "finished", "failure",
						"false", "msp_child", ti.normal_msp1_suborg1_id, ti.csr_admin_user_id },
				{ ti.normal_msp_org1_user1_token, spogServer.ReturnRandom("filter_name3"), "policy_name",
						UUID.randomUUID().toString(), "finished", "failure", "false", "msp_child",
						ti.normal_msp1_suborg1_id, ti.normal_msp_org1_user1_id },
				{ ti.normal_msp_org1_msp_accountadmin1_token, spogServer.ReturnRandom("filter_name4"), "policy_name",
						UUID.randomUUID().toString(), "finished", "failure", "false", "msp_child",
						ti.normal_msp1_suborg1_id, ti.normal_msp_org1_msp_accountadmin1_id },

		};
	}

	@Test(dataProvider = "valid_params")
	public void PolicyFilter_savesearch(String valid_token, String filter_name, String policy_name, String group_id,
			String last_backup_status, String status, String is_default, String view_type, String org_id,
			String user_id) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());

		spogServer.setToken(valid_token);
		userSpogServer.setToken(spogServer.getJWTToken());

		String prefix = RandomStringUtils.randomAlphanumeric(8);
		filter_name = prefix + filter_name;

		Response response = null;
		test.log(LogStatus.INFO, "createPolicyFilterForSpecifiedUserWithCheck");

		response = userSpogServer.createPolicyFilterForSpecificUserWithCheck_savesearch(org_id, user_id, filter_name,
				status, is_default, "all", test);

		String filter_id = response.then().extract().path("data.filter_id").toString();

		// validate the view type for the created filters
		test.log(LogStatus.INFO, "validate the view type for the created filters");
		spogServer.validateViewType(response, view_type, test);

		test.log(LogStatus.INFO, "updatePolicyFilterForSpecificUserWithCheck");
		response = userSpogServer.updatePolicyFilterForSpecificUserWithCheck_audit(user_id, filter_id, filter_name,
				policy_name, group_id, last_backup_status, status, is_default, test);

		// validate the view type for the created filters
		test.log(LogStatus.INFO, "validate the view type for the created filters");
		spogServer.validateViewType(response, view_type, test);

		test.log(LogStatus.INFO, "getPolicyFilterForSpecificUserWithCheck");
		response = userSpogServer.getSpecificPolicyFilterForLoggedinUser(filter_id, test);

		// validate the view type for the created filters
		test.log(LogStatus.INFO, "validate the view type for the created filters");
		spogServer.validateViewType(response, view_type, test);

		test.log(LogStatus.INFO, "deletePolicyFilterForSpecificUserWithCheck");
		userSpogServer.deleteSpecificPolicyFilterForLoggedinUser(filter_id, test);

	}

	@Test(dataProvider = "valid_params")
	public void SourceFilter_savesearch(String valid_token, String filter_name, String policy_name, String group_id,
			String last_backup_status, String status, String is_default, String view_type, String org_id,
			String user_id) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());

		spogServer.setToken(valid_token);
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		Response response = null;

		String protection_status = "protect";
		String connection_status = "online";
		String protection_policy = UUID.randomUUID().toString();
		String backup_status = last_backup_status;
		;
		String source_group = group_id;
		String operating_system = "windows";
		String applications = "sql";
		String site_id = UUID.randomUUID().toString();
		String source_name = prefix + "source";
		String source_type = "all";

		test.log(LogStatus.INFO, "createSourceFilter_savesearch");
		response = spogServer.createSourceFilter_savesearch(org_id, user_id, filter_name, protection_status,
				connection_status, protection_policy, backup_status, source_group, operating_system, applications,
				site_id, source_name, source_type, is_default, test);

		String filter_id = response.then().extract().path("data.filter_id").toString();

		// validate the view type for the created filters
		test.log(LogStatus.INFO, "validate the view type for the created filters");
		spogServer.validateViewType(response, view_type, test);

		test.log(LogStatus.INFO, "updateSourceFilter_savesearch");
		response = spogServer.updateSourceFilter_savesearch(user_id, filter_id, filter_name, protection_status,
				connection_status, protection_policy, backup_status, source_group, operating_system, applications,
				source_type, "all", is_default, test);

		// validate the view type for the created filters
		test.log(LogStatus.INFO, "validate the view type for the created filters");
		spogServer.validateViewType(response, view_type, test);

		test.log(LogStatus.INFO, "get SourceFilter_savesearch");
		spogServer.getFilterByID(user_id, filter_id);

		// validate the view type for the created filters
		test.log(LogStatus.INFO, "validate the view type for the created filters");
		spogServer.validateViewType(response, view_type, test);

		test.log(LogStatus.INFO, "delete SourceFilter_savesearch with filter_id");
		spogServer.deletefilterspecifiedbyUserIdwithCheck(user_id, filter_id, valid_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
	}

	@DataProvider(name = "create_user_filter_valid")
	public final Object[][] createUserFilterValidParams() {
		return new Object[][] {

				// csr readonly token
				{ "csr", ti.direct_org1_id, ti.csr_token, ti.csr_admin_user_id,
						RandomStringUtils.randomAlphanumeric(4) + "filterName", null, null, "verified", "false", 0,
						"direct", SpogConstants.DIRECT_ADMIN },
				{ "csr", ti.normal_msp_org1_id, ti.csr_token, ti.csr_admin_user_id,
						RandomStringUtils.randomAlphanumeric(4) + "filterName", null, null, "verified", "false", 0,
						"msp", SpogConstants.MSP_ADMIN },
				{ "csr", ti.normal_msp1_suborg1_id, ti.csr_token, ti.csr_admin_user_id,
						RandomStringUtils.randomAlphanumeric(4) + "filterName", null, null, "verified", "false", 0,
						"msp_child", SpogConstants.DIRECT_ADMIN },
				{ "msp", ti.normal_msp1_suborg1_id, ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,
						RandomStringUtils.randomAlphanumeric(4) + "filterName", null, null, "verified", "false", 0,
						"msp_child", SpogConstants.DIRECT_ADMIN },
				{ "msp_account_admin", ti.normal_msp1_suborg1_id, ti.normal_msp_org1_msp_accountadmin1_token,
						ti.normal_msp_org1_msp_accountadmin1_id, RandomStringUtils.randomAlphanumeric(4) + "filterName",
						null, null, "verified", "false", 0, "msp_child", SpogConstants.DIRECT_ADMIN }, };
	}

	@Test(dataProvider = "create_user_filter_valid")
	public void UserFilter_savesearch(String organizationType, String organization_id, String validToken,
			String user_id, String filter_name, String search_string, String user_is_blocked, String user_status,
			String is_default, int count, String view_type, String role_id) {
		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);
		String filter_id = null;

		Response response = null;

		userSpogServer.setToken(validToken);

		test.log(LogStatus.INFO, "createUserFilter_savesearch");

		String user_filter_type = "all";
		response = userSpogServer.createUserFilterForSpecificUser_savesearch(user_id, filter_name, search_string,
				user_is_blocked, user_status, role_id, is_default, validToken, organization_id, user_filter_type, test);
		filter_id = response.then().extract().path("data.filter_id").toString();

		// validate the view type for the created filters
		test.log(LogStatus.INFO, "validate the view type for the created filters");
		spogServer.validateViewType(response, view_type, test);

		test.log(LogStatus.INFO, "update user Filter savesearch");
		response = userSpogServer.updateUserFilterForLoggedInUser(filter_id, filter_name, search_string,
				user_is_blocked, user_status, role_id, is_default, test);

		// validate the view type for the created filters
		test.log(LogStatus.INFO, "validate the view type for the created filters");
		spogServer.validateViewType(response, view_type, test);

		test.log(LogStatus.INFO, "get user Filter savesearch");
		response = userSpogServer.getUserFilterForLoggedInUser(filter_id, validToken, test);

		// validate the view type for the created filters
		test.log(LogStatus.INFO, "validate the view type for the created filters");
		spogServer.validateViewType(response, view_type, test);

		test.log(LogStatus.INFO, "delete user Filter savesearch");
		userSpogServer.deleteUserFilterForLoggedInUserWithCheck(validToken, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
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

}
