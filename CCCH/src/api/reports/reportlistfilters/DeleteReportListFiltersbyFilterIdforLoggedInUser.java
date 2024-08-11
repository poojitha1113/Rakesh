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

public class DeleteReportListFiltersbyFilterIdforLoggedInUser extends base.prepare.Is4Org {
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
	public final Object[][] Deletereportslistfilters() {

		return new Object[][] {

				{ "Delete report list filters for direct organization", ti.direct_org1_id, ti.direct_org1_user1_token,
						spogServer.ReturnRandom("report"), "last_7_days", "selected_organizations", "weekly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2 },
				{ "Delete report list filters for msp organization", ti.normal_msp_org1_id,
						ti.normal_msp_org1_user1_token, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_source_groups", "monthly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 3 },
				{ "Delete report list filters for sub organization",
						ti.normal_msp1_suborg1_id + "," + ti.normal_msp_org1_id, ti.normal_msp1_suborg1_user1_token,
						spogServer.ReturnRandom("report"), "last_1_month,last_3_months", "all_sources", "quarterly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				{ "Delete report list filters for sub organization with msp_account_admin token",
						ti.normal_msp1_suborg1_id/* +","+ti.normal_msp_org1_id */,
						ti.normal_msp_org1_msp_accountadmin1_token, spogServer.ReturnRandom("report"),
						"last_1_month,last_3_months", "all_sources", "quarterly", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				// csr read only cases

				{ "Delete report list filters for csr organization using csrreadonly valid token", ti.csr_org_id,
						ti.csr_readonly_token, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_organizations", "weekly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2 },

				// 3 tier cases
				{ "Delete report list filters for root msp organization", ti.root_msp_org1_id,
						ti.root_msp_org1_user1_token, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_source_groups", "monthly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 3 },
				{ "Delete report list filters for root sub organization",
						ti.root_msp1_suborg1_id + "," + ti.root_msp_org1_id, ti.root_msp1_suborg1_user1_token,
						spogServer.ReturnRandom("report"), "last_1_month,last_3_months", "all_sources", "quarterly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				{ "Delete report list filters for root sub organization with msp_account_admin token",
						ti.root_msp1_suborg1_id/* +","+ti.root_msp_org1_id */, ti.root_msp_org1_msp_accountadmin1_token,
						spogServer.ReturnRandom("report"), "last_1_month,last_3_months", "all_sources", "quarterly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },

				{ "Delete report list filters for submsp organization", ti.root_msp1_submsp_org1_id,
						ti.root_msp1_submsp1_user1_token, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_source_groups", "monthly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 3 },
				{ "Delete report list filters for submsp_sub organization",
						ti.msp1_submsp1_sub_org1_id + "," + ti.root_msp1_submsp_org1_id,
						ti.root_msp1_submsp1_user1_token, spogServer.ReturnRandom("report"),
						"last_1_month,last_3_months", "all_sources", "quarterly", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				{ "Delete report list filters for root sub organization with msp_account_admin token",
						ti.root_msp1_submsp_org1_id/* +","+ti.root_msp_org1_id */,
						ti.root_msp1_submsp1_account_admin_token, spogServer.ReturnRandom("report"),
						"last_1_month,last_3_months", "all_sources", "quarterly", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 }, };
	}

	// Valid cases - 201

	@Test(dataProvider = "Deletereportslistfilters")
	public void deleteReportListFilters_201(String caseType, String organization_ids, String validToken,
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
		/*if (caseType.contains("sub")) {
			orgId = ti.normal_msp1_suborg1_id; // hard coding the organization id for case msp_account_admin
		} else {
			orgId = spogServer.GetLoggedinUserOrganizationID();
		}*/
		String[] org_ids = organization_ids.split(",");

		if (org_ids.length != 1) {
			orgId = org_ids[0];
		} else {
			orgId = org_ids.toString();
		}

		String source_group_ids = null, source_group_id = null;
		/*if (!(caseType.contains("csrreadonly"))) {
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

		test.log(LogStatus.INFO, "Create filter list for reports");
		Response response = spogReportServer.createReportListFiltersForLoggedInUserWithCheck(validToken,
				reportlistfilters, SpogConstants.SUCCESS_POST, null, test);

		String filter_id = response.then().extract().path("data.filter_id");
		test.log(LogStatus.INFO, "The filter id is " + filter_id);

		test.log(LogStatus.INFO, caseType);
		spogReportServer.deleteReportListFiltersForLoggedInUserByFilterIdWithCheck(validToken, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

	}

	@DataProvider(name = "DeletereportslistfiltersInvalid")
	public Object[][] DeletereportslistfiltersInvalid() {
		return new Object[][] {
				{ "Delete report list filters with invalid filter id for direct organization",
						ti.direct_org1_user1_token, "INVALIDFILTERID", SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_IS_NOT_UUID },
				{ "Delete report list filters with invalid filter id for msp organization",
						ti.normal_msp_org1_user1_token, "INVALIDFILTERID", SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_IS_NOT_UUID },
				{ "Delete report list filters with invalid filter id for suborg organization",
						ti.normal_msp1_suborg2_user1_token, "INVALIDFILTERID", SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_IS_NOT_UUID },
				{ "Delete report list filters with invalid filter id for suborg organization with msp_account_admin token",
						ti.normal_msp_org1_msp_accountadmin1_token, "INVALIDFILTERID",
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID },

				{ "Delete report list filters with invalid token", "INVAIDTOKEN", UUID.randomUUID().toString(),
						SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT },
				{ "Delete report list filters with missing token", "", UUID.randomUUID().toString(),
						SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED },

				{ "Delete report list filters with filter id that doesnot exist for direct organization",
						ti.direct_org1_user1_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST,
						SpogMessageCode.FILTER_NOT_FOUND_WITH_USER_ID },
				{ "Delete report list filters with filter id that doesnot exist for msp organization",
						ti.normal_msp_org1_user1_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST,
						SpogMessageCode.FILTER_NOT_FOUND_WITH_USER_ID },
				{ "Delete report list filters with filter id that doesnot exist for suborg organization",
						ti.normal_msp1_suborg2_user1_token, UUID.randomUUID().toString(),
						SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.FILTER_NOT_FOUND_WITH_USER_ID },
				{ "Delete report list filters with filter id that doesnot exist for suborg organization with msp_account_admin token",
						ti.normal_msp_org1_msp_accountadmin1_token, UUID.randomUUID().toString(),
						SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.FILTER_NOT_FOUND_WITH_USER_ID },
				// csrreadonly cases
				{ "Delete report list filters with filter id that doesnot exist for csr organization",
						ti.csr_readonly_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST,
						SpogMessageCode.FILTER_NOT_FOUND_WITH_USER_ID },
				{ "Delete report list filters with invalid filter id for csr organization with ti.csr_readonly_token token",
						ti.csr_readonly_token, "INVALIDFILTERID", SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_IS_NOT_UUID }, };
	}
	// Invalid case - 401

	@Test(dataProvider = "DeletereportslistfiltersInvalid")
	public void deleteReportListFiltersbyFilterIDforLoggedInUser_400_401_404(String caseType, String token,
			String filter_id, int expectedStatusCode, SpogMessageCode expectedErrorMessage) {

		test.log(LogStatus.INFO, caseType);
		spogReportServer.deleteReportListFiltersForLoggedInUserByFilterIdWithCheck(token, filter_id, expectedStatusCode,
				expectedErrorMessage, test);

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
