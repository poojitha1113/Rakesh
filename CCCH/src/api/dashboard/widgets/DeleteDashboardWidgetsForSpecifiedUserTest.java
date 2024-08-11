package api.dashboard.widgets;

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
import InvokerServer.SPOGJobServer;
import InvokerServer.SPOGReportServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class DeleteDashboardWidgetsForSpecifiedUserTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private SPOGReportServer spogReportServer;
	private ExtentTest test;

	private TestOrgInfo ti;
	ArrayList<HashMap<String, Object>> defaultWidgets = new ArrayList<HashMap<String, Object>>();
	ArrayList<String> widgetsId = new ArrayList<String>();

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

		// get system default widgets
		Response response = spogReportServer.getSystemDashboardWidgets(ti.normal_msp_org1_user1_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		defaultWidgets = response.then().extract().path("data");

		for (int i = 0; i < defaultWidgets.size(); i++) {
			widgetsId.add(defaultWidgets.get(i).get("dashboard_widget_id").toString());
		}
	}

	@DataProvider(name = "deleteDashboardWidgetsForSpecifiedUser_valid")
	public final Object[][] deleteDashboardWidgetsForSpecifiedInUser_valid() {
		return new Object[][] {
				{ "csrreadonly", ti.csr_readonly_token, ti.csr_readonly_admin_user_id,
						new String[] { "9", "7", "5", "2", "3", "1", "4", "6", "8" },
						new String[] { "true", "false", "none", "true", "false", "true", "false", "none", "true" },
						new String[] { "true", "false", "none", "true", "false", "false", "none", "true", "false" } },
				{ "Direct", ti.direct_org1_user1_token, ti.direct_org1_user1_id,
						new String[] { "9", "7", "5", "2", "3", "1", "4", "6", "8" },
						new String[] { "true", "false", "none", "true", "false", "true", "false", "none", "true" },
						new String[] { "true", "false", "none", "true", "false", "false", "none", "true", "false" } },
				{ "MSP", ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,
						new String[] { "9", "7", "5", "2", "3", "1", "4", "6", "8" },
						new String[] { "true", "false", "none", "true", "false", "false", "none", "true", "false" },
						new String[] { "true", "false", "none", "true", "false", "false", "none", "true", "false" } },
				{ "suborg", ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id,
						new String[] { "9", "7", "5", "2", "3", "1", "4", "6", "8" },
						new String[] { "true", "false", "none", "true", "false", "true", "false", "none", "true" },
						new String[] { "true", "false", "none", "true", "false", "false", "none", "true", "false" } },
				{ "MSPAccount", ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp1_suborg1_user1_id,
						new String[] { "9", "7", "5", "2", "3", "1", "4", "6", "8" },
						new String[] { "true", "false", "none", "true", "false", "false", "none", "true", "false" },
						new String[] { "true", "false", "none", "true", "false", "false", "none", "true", "false" } },
				{ "MSP", ti.normal_msp_org1_user1_token, ti.normal_msp1_suborg1_user1_id,
						new String[] { "9", "7", "5", "2", "3", "1", "4", "6", "8" },
						new String[] { "true", "false", "none", "true", "false", "false", "none", "true", "false" },
						new String[] { "true", "false", "none", "true", "false", "false", "none", "true", "false" } },
				{ "csrreadonly", ti.csr_readonly_token, ti.csr_readonly_admin_user_id,
						new String[] { "9", "7", "5", "2", "3", "1", "4", "6", "8" },
						new String[] { "true", "false", "none", "true", "false", "true", "false", "none", "true" },
						new String[] { "true", "false", "none", "true", "false", "false", "none", "true", "false" } },
				{ "Direct", ti.direct_org1_user1_token, ti.direct_org1_user1_id,
						new String[] { "9", "7", "5", "2", "3", "1", "4", "6", "8" },
						new String[] { "true", "false", "none", "true", "false", "true", "false", "none", "true" },
						new String[] { "true", "false", "none", "true", "false", "false", "none", "true", "false" } },
				{ "MSP", ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,
						new String[] { "9", "7", "5", "2", "3", "1", "4", "6", "8" },
						new String[] { "true", "false", "none", "true", "false", "false", "none", "true", "false" },
						new String[] { "true", "false", "none", "true", "false", "false", "none", "true", "false" } },
				{ "suborg", ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id,
						new String[] { "9", "7", "5", "2", "3", "1", "4", "6", "8" },
						new String[] { "true", "false", "none", "true", "false", "true", "false", "none", "true" },
						new String[] { "true", "false", "none", "true", "false", "false", "none", "true", "false" } },
				{ "MSPAccount", ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_msp_accountadmin1_id,
						new String[] { "9", "7", "5", "2", "3", "1", "4", "6", "8" },
						new String[] { "true", "false", "none", "true", "false", "false", "none", "true", "false" },
						new String[] { "true", "false", "none", "true", "false", "false", "none", "true", "false" } },

				// 3 tier cases
				{ "RootMSP", ti.root_msp_org1_user1_token, ti.root_msp_org1_user1_id,
						new String[] { "9", "7", "5", "2", "3", "1", "4", "6", "8" },
						new String[] { "true", "false", "none", "true", "false", "false", "none", "true", "false" },
						new String[] { "true", "false", "none", "true", "false", "false", "none", "true", "false" } },
				{ "RootSub", ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,
						new String[] { "9", "7", "5", "2", "3", "1", "4", "6", "8" },
						new String[] { "true", "false", "none", "true", "false", "false", "none", "true", "false" },
						new String[] { "true", "false", "none", "true", "false", "false", "none", "true", "false" } },
				{ "submsp_sub", ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,
						new String[] { "9", "7", "5", "2", "3", "1", "4", "6", "8" },
						new String[] { "true", "false", "none", "true", "false", "false", "none", "true", "false" },
						new String[] { "true", "false", "none", "true", "false", "false", "none", "true", "false" } },
				{ "RootMSPAccountAdmin", ti.root_msp1_submsp1_account_admin_token,
						ti.root_msp1_submsp1_account_admin_1_id,
						new String[] { "9", "7", "5", "2", "3", "1", "4", "6", "8" },
						new String[] { "true", "false", "none", "true", "false", "false", "none", "true", "false" },
						new String[] { "true", "false", "none", "true", "false", "false", "none", "true", "false" } },
				{ "SubMSP", ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,
						new String[] { "9", "7", "5", "2", "3", "1", "4", "6", "8" },
						new String[] { "true", "false", "none", "true", "false", "false", "none", "true", "false" },
						new String[] { "true", "false", "none", "true", "false", "false", "none", "true",
								"false" } }, };
	}

	@Test(dataProvider = "deleteDashboardWidgetsForSpecifiedUser_valid")
	public void deleteDashboardWidgetsForSpecifiedUser(String OrganizationType, String ValidToken, String user_id,
			String[] order_id, String[] visibility, String[] isExpandable) {
		test.log(LogStatus.INFO, "deleteDashboardWidgetsForSpecifiedUser of the organization type" + OrganizationType);

		int userinput = 1;
		userinput = gen_random_index(order_id);

		if (userinput == 0)
			userinput = 1;

		ArrayList<HashMap<String, Object>> expected_widgets = new ArrayList<>();
		HashMap<String, Object> temp = new HashMap<>();

		spogReportServer.deleteDashboardWidgetsForSpecifiedUser(ValidToken, user_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

		test.log(LogStatus.INFO, "composing the postDashboardWidgetsForSpecifiedUser in org: " + OrganizationType);
		for (int j = 0; j < userinput; j++) {
			temp = spogReportServer.composeDashboardWidgets(widgetsId.get(j), order_id[j], visibility[j],
					isExpandable[j]);
			expected_widgets.add(temp);
		}

		test.log(LogStatus.INFO, "post dashboard widgets for the logged in user with valid token");
		Response response = spogReportServer.postDashboardWidgetsForSpecifiedUser(expected_widgets, user_id, ValidToken,
				test);

		for (int j = 0; j < userinput; j++) {
			expected_widgets.get(j).put("dashboard_widget_id", widgetsId.get(j));
		}

		test.log(LogStatus.INFO, "validate actual dashboard widgets with expected");
		spogReportServer.validateDashboardWidgets(response, expected_widgets, defaultWidgets,
				SpogConstants.SUCCESS_POST, SpogMessageCode.SUCCESS_POST, test);

		test.log(LogStatus.INFO, "delete the create dashboard widgets of the logged in user with valid token");
		spogReportServer.deleteDashboardWidgetsForSpecifiedUser(ValidToken, user_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

	}

	@DataProvider(name = "deleteDashboardWidgetsForSpecifiedUser_invalid")
	public final Object[][] deleteDashboardWidgetsForSpecifiedUser_invalid() {
		return new Object[][] {
				{ "deleteDashboardWidgetsForSpecifiedUser with invalid token", "1234", ti.direct_org1_user1_id,
						SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT },
				{ "deleteDashboardWidgetsForSpecifiedUser with missed token", "", ti.direct_org1_user1_id,
						SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED } };

	}

	@Test(dataProvider = "deleteDashboardWidgetsForSpecifiedUser_invalid")
	public void deleteDashboardWidgetsForSpecifiedUser_401(String Testcase, String token, String user_id,
			int expectedStatusCode, SpogMessageCode expectedErrorMessage) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());

		test.assignAuthor("Ramya.Nagepalli");

		test.log(LogStatus.INFO, "invalid Scenario:" + Testcase);
		spogReportServer.deleteDashboardWidgetsForSpecifiedUser(token, user_id, expectedStatusCode,
				expectedErrorMessage, test);

	}

	@DataProvider(name = "deleteDashboardWidgetsForSpecifiedUser_403")
	public final Object[][] deleteDashboardWidgetsForSpecifiedUser_403() {
		return new Object[][] {

				{ "INSUFFICIENT_PERMISSIONS of Direct_msp", ti.direct_org1_user1_token, ti.direct_org1_user1_id,
						ti.normal_msp_org1_user1_token, "Direct", SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "INSUFFICIENT_PERMISSIONS of Direct_direct_b", ti.direct_org1_user1_token, ti.direct_org1_user1_id,
						ti.direct_org2_user1_token, "Direct", SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "INSUFFICIENT_PERMISSIONS of Direct_sub-org", ti.direct_org1_user1_token, ti.direct_org1_user1_id,
						ti.normal_msp1_suborg1_user1_token, "Direct", SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "INSUFFICIENT_PERMISSIONS of msp-mspb", ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,
						ti.normal_msp_org2_user1_token, "MSP", SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "INSUFFICIENT_PERMISSIONS of msp-direct", ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,
						ti.direct_org2_user1_token, "MSP", SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "INSUFFICIENT_PERMISSIONS of msp-suborg", ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,
						ti.normal_msp1_suborg1_user1_token, "MSP", SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "INSUFFICIENT_PERMISSIONS of suborg-direct", ti.normal_msp1_suborg1_user1_token,
						ti.normal_msp1_suborg1_user1_id, ti.direct_org2_user1_token, "SUB_ORG",
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "INSUFFICIENT_PERMISSIONS of suborg-mspb", ti.normal_msp1_suborg1_user1_token,
						ti.normal_msp1_suborg1_user1_id, ti.normal_msp_org2_user1_token, "SUB_ORG",
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "INSUFFICIENT_PERMISSIONS of suborg-suborgb", ti.normal_msp1_suborg1_user1_token,
						ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg2_user1_token, "SUB_ORG",
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "INSUFFICIENT_PERMISSIONS of mspaccount-suborgb", ti.normal_msp_org1_msp_accountadmin1_token,
						ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg2_user1_token, "SUB_ORG",
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "user_id is random UUID", ti.normal_msp1_suborg1_user1_token, UUID.randomUUID().toString(),
						ti.normal_msp1_suborg1_user1_token, "SUB_ORG", SpogConstants.RESOURCE_NOT_EXIST,
						SpogMessageCode.USER_ID_DOESNOT_EXIST },
				{ "user_id is invalid", ti.normal_msp_org1_msp_accountadmin1_token, "12345",
						ti.normal_msp_org1_msp_accountadmin1_token, "SUB_ORG", SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_USERID_IS_NOT_UUID },
				/*
				 * { "order_id is greater than max", ti.normal_msp1_suborg1_user1_token,
				 * ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg1_user1_token,
				 * "SUB_ORG", SpogConstants.REQUIRED_INFO_NOT_EXIST,
				 * SpogMessageCode.ORDER_ID_SHOULD_BE_LESS_THAN_MAX_WIDGETS }, {
				 * "order_id is less than min", ti.normal_msp_org1_msp_accountadmin1_token,
				 * ti.normal_msp1_suborg1_user1_id, ti.normal_msp_org1_msp_accountadmin1_token,
				 * "SUB_ORG", SpogConstants.REQUIRED_INFO_NOT_EXIST,
				 * SpogMessageCode.ORDER_ID_ATLEAST_1 },
				 */

				// csr read only cases
				{ "INSUFFICIENT_PERMISSIONS of Direct_csrreadonly", ti.csr_readonly_token,
						ti.csr_readonly_admin_user_id, ti.direct_org1_user1_token, "csrreadonly",
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "INSUFFICIENT_PERMISSIONS of MSP_csrreadonly", ti.csr_readonly_token, ti.csr_readonly_admin_user_id,
						ti.normal_msp_org1_user1_token, "csrreadonly", SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "INSUFFICIENT_PERMISSIONS of suborg_csrreadonly", ti.csr_readonly_token,
						ti.csr_readonly_admin_user_id, ti.normal_msp1_suborg1_user1_token, "csrreadonly",
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "user_id is random UUID", ti.csr_readonly_token, UUID.randomUUID().toString(), ti.csr_readonly_token,
						"csrreadonly", SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.USER_ID_DOESNOT_EXIST },
				{ "user_id is invalid", ti.csr_readonly_token, "12345", ti.csr_readonly_token, "csrreadonly",
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_USERID_IS_NOT_UUID },
				/*
				 * { "order_id is greater than max", ti.csr_readonly_token,
				 * ti.csr_readonly_admin_user_id, ti.csr_readonly_token, "csrreadonly",
				 * SpogConstants.REQUIRED_INFO_NOT_EXIST,
				 * SpogMessageCode.ORDER_ID_SHOULD_BE_LESS_THAN_MAX_WIDGETS }, {
				 * "order_id is less than min", ti.csr_readonly_token,
				 * ti.csr_readonly_admin_user_id, ti.csr_readonly_token, "csrreadonly",
				 * SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ORDER_ID_ATLEAST_1 },
				 */
				// 3 tier cases
				{ "INSUFFICIENT_PERMISSIONS of rootmsp-rootmspb", ti.root_msp_org1_user1_token,
						ti.root_msp_org1_user1_id, ti.root_msp_org2_user1_token, "MSP",
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "INSUFFICIENT_PERMISSIONS of rootmsp-direct", ti.root_msp_org1_user1_token, ti.root_msp_org1_user1_id,
						ti.direct_org2_user1_token, "MSP", SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "INSUFFICIENT_PERMISSIONS of rootmsp-suborg", ti.root_msp_org1_user1_token, ti.root_msp_org1_user1_id,
						ti.root_msp1_suborg1_user1_token, "MSP", SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },

				{ "INSUFFICIENT_PERMISSIONS of rootmsp-rootsub", ti.root_msp_org1_user1_token,
						ti.root_msp_org1_user1_id, ti.root_msp2_suborg1_user1_token, "MSP",
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "INSUFFICIENT_PERMISSIONS of rootmsp-submsp", ti.root_msp_org1_user1_token, ti.root_msp_org1_user1_id,
						ti.root_msp1_submsp1_user1_token, "MSP", SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "INSUFFICIENT_PERMISSIONS of rootmsp-submsp_sub", ti.root_msp_org1_user1_token,
						ti.root_msp_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token, "MSP",
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },

				{ "INSUFFICIENT_PERMISSIONS of submsp-submsp_sub", ti.root_msp1_submsp1_user1_token,
						ti.root_msp1_submsp1_user1_id, ti.msp1_submsp1_suborg1_user1_token, "MSP",
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "INSUFFICIENT_PERMISSIONS of submsp-rootsub", ti.root_msp1_submsp1_user1_token,
						ti.root_msp1_submsp1_user1_id, ti.root_msp1_suborg1_user1_token, "MSP",
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },

				{ "INSUFFICIENT_PERMISSIONS of rootsub-submsp", ti.root_msp1_suborg1_user1_token,
						ti.root_msp1_suborg1_user1_id, ti.root_msp1_submsp1_user1_token, "MSP",
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "INSUFFICIENT_PERMISSIONS of rootsub-submsp_sub", ti.root_msp1_suborg1_user1_token,
						ti.root_msp1_suborg1_user1_id, ti.msp1_submsp1_suborg1_user1_token, "MSP",
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				// monitor case
				{ "INSUFFICIENT_PERMISSIONS of direct monitor", ti.direct_org1_monitor_user1_token,
						ti.direct_org1_monitor_user1_id, ti.direct_org1_monitor_user1_token, "direct",
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },

		};
	}

	@Test(dataProvider = "deleteDashboardWidgetsForSpecifiedUser_403")
	public void deleteDashboardWidgetsForSpecifiedUser_invalid(String invalidTestCase, String adminToken,
			String user_id, String InValidToken, String organization_type, int ExpectedStatusCode,
			SpogMessageCode Errormessage) {

		test.log(LogStatus.INFO, "invalid Scenario for deleteDashboardWidgetsForSpecifiedUser" + invalidTestCase);

		ArrayList<HashMap<String, Object>> expected_widgets = new ArrayList<HashMap<String, Object>>();

		HashMap<String, Object> temp = new HashMap<>();

		test.log(LogStatus.INFO, "composing the postDashboardWidgetsForSpecifiedUser in org: " + organization_type);

		temp = spogReportServer.composeDashboardWidgets(widgetsId.get(1), "1", "true", "true");
		expected_widgets.add(temp);

		test.log(LogStatus.INFO, "post the expected DashboardWidgets For the SpecifiedUser ");
		spogReportServer.postDashboardWidgetsForSpecifiedUser(expected_widgets, user_id, adminToken, test);

		test.log(LogStatus.INFO,
				"delete dashboard widgets for specified user with invalid scenario :" + invalidTestCase);

		if (invalidTestCase.equals("user_id is random UUID")) {
			spogReportServer.deleteDashboardWidgetsForSpecifiedUser(InValidToken, UUID.randomUUID().toString(),
					ExpectedStatusCode, Errormessage, test);
		} else if (invalidTestCase.equals("user_id is invalid")) {
			spogReportServer.deleteDashboardWidgetsForSpecifiedUser(InValidToken, "1236545", ExpectedStatusCode,
					Errormessage, test);
		} else {
			spogReportServer.deleteDashboardWidgetsForSpecifiedUser(InValidToken, user_id, ExpectedStatusCode,
					Errormessage, test);

		}
		test.log(LogStatus.INFO, "delete dashboard widgets for specified user");
		spogReportServer.deleteDashboardWidgetsForSpecifiedUser(adminToken, user_id,
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

	// generate random number
	public int gen_random_index(String[] job_status) {
		Random generator = new Random();
		int randomindx = generator.nextInt(job_status.length);

		return randomindx;
	}

}
