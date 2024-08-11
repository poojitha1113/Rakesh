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

public class UpdateReportListFiltersByFilterIDForLoggedInUser extends base.prepare.Is4Org {

	private SPOGServer spogServer;
	private SPOGReportServer spogReportServer;
	private UserSpogServer userSpogServer;
	private ExtentTest test;
	private String org_model_prefix = this.getClass().getSimpleName();
	String prefix = RandomStringUtils.randomAlphanumeric(4);

	ArrayList<HashMap<String, Object>> expecteddata = new ArrayList<>();
	private TestOrgInfo ti;

	@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runningMachine, String buildVersion) {

		spogServer = new SPOGServer(baseURI, port);
		spogReportServer = new SPOGReportServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		rep = ExtentManager.getInstance("UpdateReportListFiltersByFilterIDforLoggedInUser", logFolder);
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

				{ "direct", ti.direct_org1_id, ti.direct_org1_user1_token, spogServer.ReturnRandom("report"),
						"last_7_days", "selected_organizations", "weekly", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2,
						spogServer.ReturnRandom("report_u"), "", "", "", "filter1" },
				{ "msp", ti.root_msp_org1_id, ti.root_msp_org1_user1_token, spogServer.ReturnRandom("report"),
						"last_7_days", "selected_source_groups", "monthly", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), false,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3, "",
						"last_6_months,last_7_days", "all_sources", "", spogServer.ReturnRandom("filter_u") },
				{ "suborg", ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_token,
						spogServer.ReturnRandom("report"), "last_1_month,last_3_months", "all_sources", "quarterly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3, "",
						"", "selected_source_groups", "monthly",spogServer.ReturnRandom("filter_u")},
				{ "suborg_mspacc", ti.root_msp1_suborg1_id, ti.root_msp_org1_msp_accountadmin1_token,
						spogServer.ReturnRandom("report"), "last_1_month,last_3_months", "selected_organizations",
						"quarterly", -1, -1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3, "",
						"last_7_days", "all_organizations", "", spogServer.ReturnRandom("filter_u") },
				{ "submsp", ti.msp1_submsp1_sub_org1_id, ti.root_msp1_submsp1_user1_token,
						spogServer.ReturnRandom("report"), "last_7_days", "selected_source_groups", "monthly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						false, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3, "",
						"last_6_months,last_7_days", "all_sources", "", spogServer.ReturnRandom("filter_u") },
				{ "submsp_suborg", ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_token,
						spogServer.ReturnRandom("report"), "last_1_month,last_3_months", "all_sources", "quarterly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3, "",
						"", "selected_source_groups", "monthly",spogServer.ReturnRandom("filter_u")},
				{ "submsp_suborg_mspacc", ti.msp1_submsp1_sub_org1_id, ti.root_msp1_submsp1_account_admin_token,
						spogServer.ReturnRandom("report"), "last_1_month,last_3_months", "selected_organizations",
						"quarterly", -1, -1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3, "",
						"last_7_days", "all_organizations", "", spogServer.ReturnRandom("filter_u") },
				{ "csrreadonly", ti.csr_org_id, ti.csr_readonly_token, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_organizations", "weekly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2, spogServer.ReturnRandom("report_u"), "", "", "",
						spogServer.ReturnRandom("filter_u")},

		};
	}

	// Valid cases - 200 and invalid 403
	@Test(dataProvider = "createreportslistfilters")
	public void updateReportListFiltersByFilterIdforLoggedInUser_200(String organizationType, String organization_ids,
			String validToken, String ReportName, String DateRangeTypes, String type, String schedule_frequency,
			long generated_ts_start, long generated_ts_end, String filter_name, Boolean is_default, String groupName,
			String groupDescription, int noOfSourceGroupsToCreate, String ReportName_U, String DateRangeTypes_U,
			String type_U, String schedule_frequency_U, String filter_name_U) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);
		ArrayList<String> OrgIds = new ArrayList<>();

		
		test.assignAuthor("Kanamarlapudi, Chandra Kanth");

		spogServer.setToken(validToken);

		// orgId for creating source_groups
		String orgId = null;
		if (organizationType.contains("submsp_suborg")) {
			orgId = ti.msp1_submsp1_sub_org1_id;
		} else if (organizationType.contains("suborg")) {
			orgId = ti.root_msp1_suborg1_id;
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

		spogReportServer.setToken(validToken);

		test.log(LogStatus.INFO, "Composing Filter List for Reports in org: " + organizationType);
		HashMap<String, Object> reportlistfilters = spogReportServer.composeReportListFilterInfo(ReportName,
				DateRangeTypes, type, source_group_ids, organization_ids, schedule_frequency, generated_ts_start,
				generated_ts_end, filter_name, is_default);

		test.log(LogStatus.INFO, "Creating Filter List for Reports in org: " + organizationType);
		Response response = spogReportServer.createReportListFiltersForLoggedInUserWithCheck(validToken,
				reportlistfilters, SpogConstants.SUCCESS_POST, null, test);

		String filter_id = response.then().extract().path("data.filter_id");
		test.log(LogStatus.INFO, "The filter id is " + filter_id);

		test.log(LogStatus.INFO, "Update Filter List for Reports in org: " + organizationType);
		HashMap<String, Object> updateReportListFiltersInfo = spogReportServer.composeReportListFilterInfo(ReportName_U,
				DateRangeTypes_U, type_U, source_group_ids, organization_ids, schedule_frequency_U, generated_ts_start,
				generated_ts_end, filter_name_U, is_default);

		test.log(LogStatus.INFO, "Update Filter List for Reports in org: " + organizationType);
		spogReportServer.updateReportListFiltersForLoggedInUserByFilterIdWithCheck(validToken, filter_id,
				updateReportListFiltersInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		test.log(LogStatus.INFO, "Deleting Filter List for Reports in org: " + organizationType);
		spogReportServer.deleteReportListFiltersForLoggedInUserByFilterIdWithCheck(validToken, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

	}

	@DataProvider(name = "updateReportListFiltersInvalid")
	public Object[][] updateReportListFiltersInvalid() {
		return new Object[][] {
				// 400
				{ "Update report list filters in the direct organization with invalid filter id",
						ti.direct_org1_user1_token, "invalidfilterid", SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_IS_NOT_UUID },
				{ "Update report list filters in the msp organization with invalid filter id",
						ti.root_msp_org1_user1_token, "invalidfilterid", SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_IS_NOT_UUID },
				{ "Update report list filters in the sub organization with invalid filter id",
						ti.root_msp1_suborg1_user1_token, "invalidfilterid", SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_IS_NOT_UUID },
				{ "Update report list filters in the sub organization with invalid filter id and msp_account admin token",
						ti.root_msp_org1_msp_accountadmin1_token, "invalidfilterid",
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID },
				{ "Update report list filters in the sub msp organization with invalid filter id",
						ti.root_msp1_submsp1_user1_token, "invalidfilterid", SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_IS_NOT_UUID },
				{ "Update report list filters in the sub msp sub organization with invalid filter id",
						ti.msp1_submsp1_suborg1_user1_token, "invalidfilterid", SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_IS_NOT_UUID },
				{ "Update report list filters in the sub organization with invalid filter id and sub msp_account admin token",
						ti.root_msp1_submsp1_account_admin_token, "invalidfilterid",
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID },

				// 401
				{ "Update report list filters invalid token", "INVALIDTOKEN", UUID.randomUUID().toString(),
						SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT },
				{ "Update report list filters missing token", "", UUID.randomUUID().toString(),
						SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED },
				{ "Update report list filters null as token", null, UUID.randomUUID().toString(),
						SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED },

				// 404
				{ "Update report list filters in the direct organization with filter id that does not exist",
						ti.direct_org1_user1_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST,
						SpogMessageCode.FILTER_NOT_FOUND_WITH_USER_ID },
				{ "Update report list filters in the msp organization with filter id that does not exist",
						ti.root_msp_org1_user1_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST,
						SpogMessageCode.FILTER_NOT_FOUND_WITH_USER_ID },
				{ "Update report list filters in the sub organization with filter id that does not exist",
						ti.root_msp1_suborg1_user1_token, UUID.randomUUID().toString(),
						SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.FILTER_NOT_FOUND_WITH_USER_ID },
				{ "Update report list filters in the sub organization with filter id that does not exist and msp_account admin token",
						ti.root_msp_org1_msp_accountadmin1_token, UUID.randomUUID().toString(),
						SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.FILTER_NOT_FOUND_WITH_USER_ID },
				{ "Update report list filters in the sub msp organization with filter id that does not exist",
						ti.root_msp1_submsp1_user1_token, UUID.randomUUID().toString(),
						SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.FILTER_NOT_FOUND_WITH_USER_ID },
				{ "Update report list filters in the sub msp sub organization with filter id that does not exist",
						ti.msp1_submsp1_suborg1_user1_token, UUID.randomUUID().toString(),
						SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.FILTER_NOT_FOUND_WITH_USER_ID },
				{ "Update report list filters in the sub organization with filter id that does not exist and sub msp_account admin token",
						ti.root_msp1_submsp1_account_admin_token, UUID.randomUUID().toString(),
						SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.FILTER_NOT_FOUND_WITH_USER_ID },
				// csrreadonly cases
				{ "Update report list filters in the csr organization with invalid filter id", ti.csr_readonly_token,
						"invalidfilterid", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID },
				{ "Update report list filters in the csr organization with filter id that does not exist",
						ti.csr_readonly_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST,
						SpogMessageCode.FILTER_NOT_FOUND_WITH_USER_ID }, };
	}

	// Invalid cases - 400
	@Test(dataProvider = "updateReportListFiltersInvalid")
	public void updateReportListFiltersByFilterIdforLoggedInUserInvalid(String caseType, String token, String filter_id,
			int expectedStatusCode, SpogMessageCode expectedErrorMessage) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		//test = ExtentManager.getNewTest(caseType);
		test.assignAuthor("Kanamarlapudi, Chandra Kanth");
		HashMap<String, Object> expectedData = new HashMap<>();
		expectedData.put("filter_name", "filter_name1");
		expectedData.put("is_default", "false");
		test.log(LogStatus.INFO, caseType);
		spogReportServer.updateReportListFiltersForLoggedInUserByFilterIdWithCheck(token, filter_id, expectedData,
				expectedStatusCode, expectedErrorMessage, test);

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
