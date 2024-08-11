package api.backupjobreports.columns;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

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
import InvokerServer.GatewayServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGJobServer;
import InvokerServer.SPOGReportServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class GetBackUpJobReportsColumnsforLoggedInUserTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private SPOGJobServer spogJobServer;
	private SPOGReportServer spogReportServer;

	private ExtentTest test;
	private TestOrgInfo ti;

	LocalDate date = LocalDate.now();
	LocalDate yesterday = date.minusDays(1);
	LocalDate tomorrow = yesterday.plusDays(2);

	ArrayList<String> columnIdList = new ArrayList<String>();
	ArrayList<String> MSPColumnIdList = new ArrayList<String>();

	ArrayList<HashMap<String, Object>> columnsHeadContent = new ArrayList<HashMap<String, Object>>();
	ArrayList<HashMap<String, Object>> MSPColumnsHeadContent = new ArrayList<HashMap<String, Object>>();

	@BeforeClass
	@Parameters({ "baseURI", "port", "csrAdminUserName", "csrAdminPassword", "csrReadOnlyAdminName",
			"csrReadOnlyAdminPassword", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword,
			String readOnlyUserName, String readOnlyPassword, String logFolder, String runningMachine,
			String buildVersion) {

		spogServer = new SPOGServer(baseURI, port);
		spogJobServer = new SPOGJobServer(baseURI, port);
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
		spogServer.setToken(ti.csr_token);

		Response response = spogReportServer.getSystemBackUpJobReportsColumns(ti.direct_org1_user1_token, "direct",
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		columnsHeadContent = response.then().extract().path("data");
		int length = columnsHeadContent.size();
		for (int i = 0; i < length; i++) {
			HashMap<String, Object> HeadContent = columnsHeadContent.get(i);

			columnIdList.add((String) HeadContent.get("column_id"));

		}

		response = spogReportServer.getSystemBackUpJobReportsColumns(ti.normal_msp_org1_user1_token, "msp",
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		MSPColumnsHeadContent = response.then().extract().path("data");
		length = MSPColumnsHeadContent.size();
		for (int i = 0; i < length; i++) {
			HashMap<String, Object> HeadContent = MSPColumnsHeadContent.get(i);

			MSPColumnIdList.add((String) HeadContent.get("column_id"));

		}
	}

	// This information is related to the destination information of the
	// cloudDedupe volume
	@DataProvider(name = "getBackUpJobReportsColumnsforLoggedInUser_valid")
	public final Object[][] getBackUpJobReportsColumnsforLoggedInUser_valid() {
		return new Object[][] {
				{ ti.csr_readonly_token, "csrreadonly", ti.csr_org_id,
						new String[] { "false", "false", "true", "false", "none" },
						new String[] { "2", "7", "4", "6", "5", "1", "8", "3", "9" }, ti.csr_readonly_token },
				{ ti.direct_org1_user1_token, "Direct", ti.direct_org1_id,
						new String[] { "false", "false", "true", "false", "none" },
						new String[] { "3", "2", "9", "5", "3", "4", "7", "2", "6" }, ti.direct_org1_user1_token },
				{ ti.normal_msp_org1_user1_token, "MSP", ti.normal_msp_org1_id,
						new String[] { "true", "false", "true", "false", "none" },
						new String[] { "6", "2", "7", "4", "6", "5", "1", "8", "3" }, ti.normal_msp_org1_user1_token },
				{ ti.normal_msp1_suborg1_user1_token, "SUB_ORG", ti.normal_msp1_suborg1_id,
						new String[] { "true", "false", "true", "false", "none" },
						new String[] { "7", "2", "9", "4", "6", "5", "1", "8", "3" },
						ti.normal_msp1_suborg1_user1_token },
				/*
				 * { ti.normal_msp_org1_msp_accountadmin1_token, "SUB_ORG",
				 * ti.normal_msp1_suborg1_id, new String[] { "true", "false", "true", "false",
				 * "none" }, new String[] { "3", "2", "9", "4", "6", "5", "1", "8", "3" } },
				 */
				// 3 tier cases
				{ ti.root_msp_org1_user1_token, "ROOT_MSP", ti.root_msp_org1_id,
						new String[] { "true", "false", "true", "false", "none" },
						new String[] { "6", "2", "7", "4", "6", "5", "1", "8", "3" }, ti.root_msp_org1_user1_token },
				{ ti.root_msp1_submsp1_user1_token, "SUB_MSP", ti.root_msp1_submsp_org1_id,
						new String[] { "true", "false", "true", "false", "none" },
						new String[] { "5", "8", "9", "4", "6", "5", "1", "8", "3" },
						ti.root_msp1_submsp1_user1_token },
				{ ti.msp1_submsp1_suborg2_user1_token, "submsp_SUB_ORG", ti.msp1_submsp1_sub_org2_id,
						new String[] { "true", "false", "true", "false", "none" },
						new String[] { "3", "2", "9", "4", "6", "5", "1", "8", "3" },
						ti.msp1_submsp1_suborg2_user1_token },

				// monitor cases
				{ ti.root_msp_org1_user1_token, "ROOT_MSP_monitor", ti.root_msp_org1_id,
						new String[] { "true", "false", "true", "false", "none" },
						new String[] { "6", "2", "7", "4", "6", "5", "1", "8", "3" },
						ti.root_msp_org1_monitor_user1_token },

				{ ti.normal_msp_org1_user1_token, "MSP_monitor", ti.normal_msp_org1_id,
						new String[] { "true", "false", "true", "false", "none" },
						new String[] { "6", "2", "7", "4", "6", "5", "1", "8", "3" },
						ti.normal_msp_org1_monitor_user1_token },
				{ ti.direct_org1_user1_token, "direct_monitor", ti.direct_org1_id,
						new String[] { "true", "false", "true", "false", "none" },
						new String[] { "6", "2", "7", "4", "6", "5", "1", "8", "3" },
						ti.direct_org1_monitor_user1_token },

				{ ti.root_msp1_submsp1_user1_token, "SUB_MSP_monitor", ti.root_msp1_submsp_org1_id,
						new String[] { "true", "false", "true", "false", "none" },
						new String[] { "5", "8", "9", "4", "6", "5", "1", "8", "3" },
						ti.root_msp1_submsp1_monitor_user1_token },
				{ ti.msp1_submsp1_suborg2_user1_token, "submsp_SUB_ORG_monitor", ti.msp1_submsp1_sub_org2_id,
						new String[] { "true", "false", "true", "false", "none" },
						new String[] { "3", "2", "9", "4", "6", "5", "1", "8", "3" },
						ti.msp1_submsp1_suborg2_monitor_user1_token },

		};
	}

	@Test(dataProvider = "getBackUpJobReportsColumnsforLoggedInUser_valid")
	public void getBackUpJobReportsColumnsforLoggedInUser_valid(String adminToken, String organization_type,
			String organization_id, String[] visible, String[] order_id, String monitor_token) {

		test = ExtentManager.getNewTest(organization_type + "Organization getBackUpJobReportsColumnsforLoggedInUser");
		ArrayList<String> columnIdList = null;
		ArrayList<HashMap<String, Object>> columnsHeadContent=null;
		spogJobServer.setToken(adminToken);

		ArrayList<HashMap<String, Object>> expected_columns = new ArrayList<>();

		HashMap<String, Object> temp = new HashMap<>();


		if (organization_type.contains("msp")) {
			columnIdList = MSPColumnIdList;
			columnsHeadContent = MSPColumnsHeadContent;
		}else {
			columnIdList = this.columnIdList;
			columnsHeadContent = this.columnsHeadContent;
		}

		test.log(LogStatus.INFO,
				"composing the createBackUpJobReportsColumnsforLoggedInUser in org: " + organization_type);

		for (int j = 0; j < 2; j++) {
			temp = spogReportServer.composeBackUpJobReportsColumns(columnIdList.get(j), visible[j], order_id[j]);

			expected_columns.add(temp);
		}

		test.log(LogStatus.INFO,
				"Create the createBackUpJobReportsColumnsforLoggedInUser in org: " + organization_type);
		Response response = spogReportServer.createBackUpJobReportsColumnsforLoggedInUser(adminToken, expected_columns,
				SpogConstants.SUCCESS_POST, test);

		test.log(LogStatus.INFO, "check the backup job reports columns");
		spogJobServer.compareJobColumnsContent(response, expected_columns, columnsHeadContent,
				SpogConstants.SUCCESS_POST, null, test);

		if (organization_type.contains("monitor")) {
			test.log(LogStatus.INFO, "get the backup job reports columns with valid token");
			response = spogReportServer.getBackUpJobReportsColumnsForLoggedInUser(monitor_token,
					SpogConstants.SUCCESS_GET_PUT_DELETE, test);

			test.log(LogStatus.INFO, "check the backup job reports columns");
			spogJobServer.compareJobColumnsContent(response, columnsHeadContent, columnsHeadContent,
					SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		} else {
			test.log(LogStatus.INFO, "get the backup job reports columns with valid token");
			response = spogReportServer.getBackUpJobReportsColumnsForLoggedInUser(adminToken,
					SpogConstants.SUCCESS_GET_PUT_DELETE, test);

			test.log(LogStatus.INFO, "check the backup job reports columns");
			spogJobServer.compareJobColumnsContent(response, expected_columns, columnsHeadContent,
					SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		}

		test.log(LogStatus.INFO, "delete the backup job reports columns");
		spogReportServer.deleteBackUpJobReportsColumnsForLoggedInUser(adminToken, SpogConstants.SUCCESS_GET_PUT_DELETE,
				null, test);

	}

	@DataProvider(name = "getBackUpJobReportsColumnsforLoggedInUser_invalid")
	public final Object[][] getBackUpJobReportsColumnsforLoggedInUser_invalid() {
		return new Object[][] {
				{ "Invalid Authorization with junk token", "Junk", "Direct", ti.direct_org1_id,
						new String[] { "true", "false", "none" },
						new String[] { "1", "8", "9", "5", "3", "4", "7", "2", "6" }, SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, ti.direct_org1_user1_token },
				{ "Invalid Authorization with missing token", "", "MSP", ti.normal_msp_org1_user1_token,
						new String[] { "true", "false", "none" },
						new String[] { "7", "2", "9", "4", "6", "5", "1", "8", "3" }, SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTHENTICATION_FAILED, ti.normal_msp_org1_user1_token }, };
	}

	@Test(dataProvider = "getBackUpJobReportsColumnsforLoggedInUser_invalid")
	public void getBackUpJobReportsColumnsforLoggedInUser_invalid(String invalidTestCase, String InValidToken,
			String organization_type, String organization_id, String[] visible, String[] order_id,
			int ExpectedStatusCode, SpogMessageCode Errormessage, String adminToken) {

		test = ExtentManager.getNewTest(
				organization_type + "Organization getBackUpJobReportsColumnsforLoggedInUser" + invalidTestCase);

		spogJobServer.setToken(adminToken);

		ArrayList<HashMap<String, Object>> expected_columns = new ArrayList<>();

		HashMap<String, Object> temp = new HashMap<>();

		test.log(LogStatus.INFO,
				"composing the createBackUpJobReportsColumnsforLoggedInUser in org: " + organization_type);

		for (int j = 0; j < 2; j++) {
			temp = spogReportServer.composeBackUpJobReportsColumns(columnIdList.get(j), visible[j], order_id[j]);

			expected_columns.add(temp);
		}

		test.log(LogStatus.INFO,
				"Create the createBackUpJobReportsColumnsforLoggedInUser in org: " + organization_type);
		Response response = spogReportServer.createBackUpJobReportsColumnsforLoggedInUser(adminToken, expected_columns,
				SpogConstants.SUCCESS_POST, test);

		test.log(LogStatus.INFO, "check the backup job reports columns");
		spogJobServer.compareJobColumnsContent(response, expected_columns, columnsHeadContent,
				SpogConstants.SUCCESS_POST, SpogMessageCode.SUCCESS_POST, test);

		test.log(LogStatus.INFO, "get the backup job reports columns with invalid token");
		response = spogReportServer.getBackUpJobReportsColumnsForLoggedInUser(InValidToken, ExpectedStatusCode, test);

		test.log(LogStatus.INFO, "check the backup job reports columns");
		spogJobServer.compareJobColumnsContent(response, expected_columns, columnsHeadContent, ExpectedStatusCode,
				Errormessage, test);

		test.log(LogStatus.INFO, "delete the backup job reports columns");
		spogReportServer.deleteBackUpJobReportsColumnsForLoggedInUser(adminToken, SpogConstants.SUCCESS_GET_PUT_DELETE,
				null, test);

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
