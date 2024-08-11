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

public class GetReportListFiltersbyFilterIDforLoggedInUser extends base.prepare.Is4Org {
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


	@DataProvider(name = "createreportslistfilters", parallel = false)
	public final Object[][] createreportslistfilters() {

		return new Object[][] {
				{ "csr", ti.csr_org_id, ti.csr_token, spogServer.ReturnRandom("report"), "last_7_days",
						"selected_organizations", "weekly", System.currentTimeMillis(), System.currentTimeMillis() + 1,
						spogServer.ReturnRandom("filter"), true, spogServer.ReturnRandom("groupName"),
						spogServer.ReturnRandom("groupDescription"), 2 },
				{ "csr_readonly", ti.csr_org_id, ti.csr_readonly_token, spogServer.ReturnRandom("report"),
						"last_7_days", "selected_organizations", "weekly", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2 },

				{ "direct", ti.direct_org1_id, ti.direct_org1_user1_token, spogServer.ReturnRandom("report"),
						"last_7_days", "selected_organizations", "weekly", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 2 },
				{ "msp", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token, spogServer.ReturnRandom("report"),
						"last_7_days", "selected_source_groups", "monthly", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				{ "suborg", ti.normal_msp1_suborg1_id + "," + ti.normal_msp_org1_id, ti.normal_msp1_suborg1_user1_token,
						spogServer.ReturnRandom("report"), "last_1_month,last_3_months", "all_sources", "quarterly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				{ "suborg", ti.normal_msp1_suborg1_id + "," + ti.normal_msp_org1_id,
						ti.normal_msp_org1_msp_accountadmin1_token, spogServer.ReturnRandom("report"),
						"last_1_month,last_3_months", "all_sources", "quarterly", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				// 3 tier cases
				{ "rootmsp", ti.root_msp_org1_id, ti.root_msp_org1_user1_token, spogServer.ReturnRandom("report"),
						"last_7_days", "selected_source_groups", "monthly", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				{ "rootsuborg", ti.root_msp1_suborg1_id + "," + ti.root_msp_org1_id, ti.root_msp1_suborg1_user1_token,
						spogServer.ReturnRandom("report"), "last_1_month,last_3_months", "all_sources", "quarterly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				{ "rootsuborg", ti.root_msp1_suborg1_id + "," + ti.root_msp_org1_id,
						ti.root_msp_org1_msp_accountadmin1_token, spogServer.ReturnRandom("report"),
						"last_1_month,last_3_months", "all_sources", "quarterly", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },

				{ "submsp", ti.root_msp1_submsp_org1_id, ti.root_msp_org1_user1_token,
						spogServer.ReturnRandom("report"), "last_7_days", "selected_source_groups", "monthly",
						System.currentTimeMillis(), System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"),
						true, spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				{ "submsp_sub", ti.root_msp1_submsp_org1_id + "," + ti.msp1_submsp1_sub_org1_id,
						ti.root_msp1_submsp1_user1_token, spogServer.ReturnRandom("report"),
						"last_1_month,last_3_months", "all_sources", "quarterly", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },
				{ "submsp_sub", ti.root_msp1_submsp_org1_id + "," + ti.msp1_submsp1_sub_org1_id,
						ti.root_msp1_submsp1_account_admin_token, spogServer.ReturnRandom("report"),
						"last_1_month,last_3_months", "all_sources", "quarterly", System.currentTimeMillis(),
						System.currentTimeMillis() + 1, spogServer.ReturnRandom("filter"), true,
						spogServer.ReturnRandom("groupName"), spogServer.ReturnRandom("groupDescription"), 3 },

		};
	}

	// Valid cases - 200

	@Test(dataProvider = "createreportslistfilters")
	public void getReportListFiltersbyFilterIDforLoggedInUser_200(String organizationType, String organization_ids,
			String validToken, String ReportName, String DateRangeTypes, String type, String schedule_frequency,
			long generated_ts_start, long generated_ts_end, String filter_name, Boolean is_default, String groupName,
			String groupDescription, int noOfSourceGroupsToCreate) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);
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
		if (!organizationType.contains("csr")) {
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

		test.log(LogStatus.INFO, "Get Filter List for Reports in org: " + organizationType);
		spogReportServer.getReportListFiltersForLoggedInUserByFilterIdWithCheck(validToken, filter_id,
				reportlistfilters, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		test.log(LogStatus.INFO, "Deleting Filter List for Reports in org: " + organizationType);
		spogReportServer.deleteReportListFiltersForLoggedInUserByFilterIdWithCheck(validToken, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

	}

	@DataProvider(name = "getReportListFiltersbyFilterIDforLoggedInUserInvalid")
	public Object[][] getReportListFiltersbyFilterIDforLoggedInUserInvalid() {
		return new Object[][] {
				// 400 cases - invalid filter_id
				{ "Get report list filters by filter id for loggedin user with invlaid filter id for an organization with csr token",
						"invalidfilterid", ti.csr_token, SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_IS_NOT_UUID },
				{ "Get report list filters by filter id for loggedin user with invlaid filter id for an organization with csr readonly token",
						"invalidfilterid", ti.csr_readonly_token, SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_IS_NOT_UUID },
				{ "Get report list filters by filter id for loggedin user with invlaid filter id for direct organization",
						"invalidfilterid", ti.direct_org1_user1_token, SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_IS_NOT_UUID },
				{ "Get report list filters by filter id for loggedin user with invlaid filter id for msp organization",
						"invalidfilterid", ti.normal_msp_org1_user1_token, SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_IS_NOT_UUID },
				{ "Get report list filters by filter id for loggedin user with invlaid filter id for sub organization",
						"invalidfilter", ti.normal_msp1_suborg1_user1_token, SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_IS_NOT_UUID },
				{ "Get report list filters by filter id for loggedin user with invlaid filter id for sub organization with msp_account_admin token",
						"invalidfilterid", ti.normal_msp_org1_msp_accountadmin1_token,
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID },

				// 401 cases - invalid / missing token
				{ "Get report list filters by filter id for loggedin user with invlaid token",
						UUID.randomUUID().toString(), "invalidtoken", SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT },
				{ "Get report list filters by filter id for loggedin user with missing token",
						UUID.randomUUID().toString(), "", SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTHENTICATION_FAILED },

				// 404 cases - filter_id that does not exist
				{ "Get report list filters by filter id for loggedin user with filter id that does not exist for an organization with csr token",
						UUID.randomUUID().toString(), ti.csr_token, SpogConstants.RESOURCE_NOT_EXIST,
						SpogMessageCode.FILTER_NOT_FOUND_WITH_USER_ID },
				{ "Get report list filters by filter id for loggedin user with filter id that does not exist for an organization with csr readonly token",
						UUID.randomUUID().toString(), ti.csr_readonly_token, SpogConstants.RESOURCE_NOT_EXIST,
						SpogMessageCode.FILTER_NOT_FOUND_WITH_USER_ID },
				{ "Get report list filters by filter id for loggedin user with filter id that does not exist for direct organization",
						UUID.randomUUID().toString(), ti.direct_org1_user1_token, SpogConstants.RESOURCE_NOT_EXIST,
						SpogMessageCode.FILTER_NOT_FOUND_WITH_USER_ID },
				{ "Get report list filters by filter id for loggedin user with filter id that does not exist for msp organization",
						UUID.randomUUID().toString(), ti.normal_msp_org1_user1_token, SpogConstants.RESOURCE_NOT_EXIST,
						SpogMessageCode.FILTER_NOT_FOUND_WITH_USER_ID },
				{ "Get report list filters by filter id for loggedin user with filter id that does not exist for sub organization",
						UUID.randomUUID().toString(), ti.normal_msp1_suborg1_user1_token,
						SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.FILTER_NOT_FOUND_WITH_USER_ID },
				{ "Get report list filters by filter id for loggedin user with filter id that does not exist for sub organization with msp_account_admin token",
						UUID.randomUUID().toString(), ti.normal_msp_org1_msp_accountadmin1_token,
						SpogConstants.RESOURCE_NOT_EXIST,
						SpogMessageCode.FILTER_NOT_FOUND_WITH_USER_ID }, };
	}

	// Invalid case - 400,401,404
	@Test(dataProvider = "getReportListFiltersbyFilterIDforLoggedInUserInvalid")
	public void getReportListFiltersbyFilterIDforLoggedInUserInvalid(String caseType, String filter_id, String token,
			int expectedStatusCode, SpogMessageCode expectedErrorMessage) {

		test.log(LogStatus.INFO, caseType);
		spogReportServer.getReportListFiltersForLoggedInUserByFilterIdWithCheck(token, filter_id, new HashMap<>(),
				expectedStatusCode, expectedErrorMessage, test);
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