package api.backupjobreports.columns;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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

public class PostBackUpJobReportsColumnsforLoggedInUserTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private SPOGJobServer spogJobServer;
	private SPOGReportServer spogReportServer;
	private SPOGDestinationServer spogDestinationServer;
	private Policy4SPOGServer policy4SpogServer;

	private ExtentTest test;
	private TestOrgInfo ti;

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
		gatewayServer = new GatewayServer(baseURI, port);
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
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);

		policy4SpogServer = new Policy4SPOGServer(baseURI, port);
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

		Response response = spogReportServer.getSystemBackUpJobReportsColumns(ti.csr_token, "csr",
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
	@DataProvider(name = "postBackUpJobReportsColumnsforLoggedInUser_valid")
	public final Object[][] postBackUpJobReportsColumnsforLoggedInUser_valid() {
		return new Object[][] {
				{ "CSR-readOnly", ti.csr_readonly_token, ti.csr_org_id,
						new String[] { "false", "false", "true", "false", "none" },
						new String[] { "1", "3", "9", "5", "2", "4", "7", "2", "6" } },
				{ "Direct", ti.direct_org1_user1_token, ti.direct_org1_id,
						new String[] { "false", "false", "true", "false", "none" },
						new String[] { "3", "2", "9", "5", "3", "4", "7", "2", "6" } },
				{ "MSP", ti.normal_msp_org1_user1_token, ti.normal_msp_org1_id,
						new String[] { "true", "false", "true", "false", "none" },
						new String[] { "6", "2", "7", "4", "6", "5", "1", "8", "3" } },
				{ "SUB_ORG", ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_id,
						new String[] { "true", "false", "true", "false", "none" },
						new String[] { "5", "8", "9", "4", "6", "5", "1", "8", "3" } },
				{ "SUB_ORG", ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp1_suborg1_id,
						new String[] { "true", "false", "true", "false", "none" },
						new String[] { "3", "2", "9", "4", "6", "5", "1", "8", "3" } },

				// 3 tier cases
				{ "ROOT_MSP", ti.root_msp_org1_user1_token, ti.root_msp_org1_id,
						new String[] { "true", "false", "true", "false", "none" },
						new String[] { "6", "2", "7", "4", "6", "5", "1", "8", "3" } },
				{ "SUB_MSP", ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp_org1_id,
						new String[] { "true", "false", "true", "false", "none" },
						new String[] { "5", "8", "9", "4", "6", "5", "1", "8", "3" } },
				{ "submsp_SUB_ORG", ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_sub_org1_id,
						new String[] { "true", "false", "true", "false", "none" },
						new String[] { "3", "2", "9", "4", "6", "5", "1", "8", "3" } },

		};
	}

	@Test(dataProvider = "postBackUpJobReportsColumnsforLoggedInUser_valid")
	public void postBackUpJobReportsColumnsforLoggedInUser_valid(String organization_type, String adminToken,
			String organization_id, String[] visible, String[] order_id) {

		test = ExtentManager.getNewTest(organization_type + "Organization postBackUpJobReportsColumnsforLoggedInUser");

		spogJobServer.setToken(adminToken);

		ArrayList<HashMap<String, Object>> expected_columns = new ArrayList<>();

		HashMap<String, Object> temp = new HashMap<>();

		if (organization_type.equalsIgnoreCase("msp")) {
			columnIdList = MSPColumnIdList;
			columnsHeadContent = MSPColumnsHeadContent;
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

		test.log(LogStatus.INFO,
				"delete the createBackUpJobReportsColumnsforLoggedInUser in org: " + organization_type);
		spogReportServer.deleteBackUpJobReportsColumnsForLoggedInUser(adminToken, SpogConstants.SUCCESS_GET_PUT_DELETE,
				null, test);

	}

	@DataProvider(name = "createBackUpJobReportsColumnsforLoggedInUser_invalid")
	public final Object[][] createBackUpJobReportsColumnsforLoggedInUser_invalid() {
		return new Object[][] { { "Invalid Authorization with junk token", "Junk", "Direct", ti.direct_org1_id,
				new String[] { "true", "false", "none" }, new String[] { "1", "8", "9", "5", "3", "4", "7", "2", "6" },
				SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT },
				{ "Invalid Authorization with missing token", "", "MSP", ti.normal_msp_org1_id,
						new String[] { "true", "false", "none" },
						new String[] { "7", "2", "9", "4", "6", "5", "1", "8", "3" }, SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTHENTICATION_FAILED },

				{ "column_id_does_not_exist", ti.direct_org1_user1_token, "Direct", ti.direct_org1_id,
						new String[] { "true", "false", "none" },
						new String[] { "1", "8", "9", "5", "3", "4", "7", "2", "6" }, SpogConstants.RESOURCE_NOT_EXIST,
						SpogMessageCode.COLUMN_ID_DOESNOT_EXIST },
				{ "order_id_greaterthan_default_column_size", ti.direct_org1_user1_token, "Direct", ti.direct_org1_id,
						new String[] { "true", "false", "none" },
						new String[] { "1", "8", "9", "5", "3", "4", "7", "2", "6" },
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ORDER_ID_SHOULD_BE_LESS_THAN_MAX_COUNT },
				{ "order_id_lessthan_1", ti.normal_msp1_suborg1_user1_token, "SUB_ORG", ti.normal_msp1_suborg1_id,
						new String[] { "true", "false", "none" },
						new String[] { "1", "8", "9", "5", "3", "4", "7", "2", "6" },
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ORDER_ID_ATLEAST_1 },
				{ "order_id_already_exist", ti.normal_msp1_suborg2_user1_token, "SUB_ORG", ti.normal_msp1_suborg2_id,
						new String[] { "true", "false", "none" },
						new String[] { "1", "8", "9", "5", "3", "4", "7", "2", "6" }, SpogConstants.SUCCESS_POST,
						SpogMessageCode.SUCCESS_POST },

			/*	{ "column_id_already_exist", ti.normal_msp2_suborg1_user1_token, "SUB_ORG", ti.normal_msp2_suborg1_id,
						new String[] { "true", "false", "none" },
						new String[] { "7", "2", "8", "3", "4", "6", "7", "2", "6" }, SpogConstants.SUCCESS_POST,
						SpogMessageCode.SUCCESS_POST },*/

				{ "column_id_does_not_exist", ti.csr_readonly_token, "csr-readonly", ti.csr_org_id,
						new String[] { "true", "false", "none" },
						new String[] { "1", "8", "9", "5", "3", "4", "7", "2", "6" }, SpogConstants.RESOURCE_NOT_EXIST,
						SpogMessageCode.COLUMN_ID_DOESNOT_EXIST },
				{ "order_id_greaterthan_default_column_size", ti.csr_readonly_token, "csr-readonly", ti.csr_org_id,
						new String[] { "true", "false", "none" },
						new String[] { "1", "8", "9", "5", "3", "4", "7", "2", "6" },
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ORDER_ID_SHOULD_BE_LESS_THAN_MAX_COUNT },
				{ "order_id_lessthan_1", ti.csr_readonly_token, "csr-readonly", ti.csr_org_id,
						new String[] { "true", "false", "none" },
						new String[] { "1", "8", "9", "5", "3", "4", "7", "2", "6" },
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ORDER_ID_ATLEAST_1 },
				{ "order_id_already_exist", ti.csr_readonly_token, "csr-readonly", ti.csr_org_id,
						new String[] { "true", "false", "none" },
						new String[] { "1", "8", "9", "5", "3", "4", "7", "2", "6" }, SpogConstants.SUCCESS_POST,
						SpogMessageCode.SUCCESS_POST },
				/*{ "column_id_already_exist", ti.csr_readonly_token, "csr-readonly", ti.csr_org_id,
						new String[] { "true", "false", "none" },
						new String[] { "7", "2", "8", "3", "4", "6", "7", "2", "6" }, SpogConstants.SUCCESS_POST,
						SpogMessageCode.SUCCESS_POST }, */};
	}

	@Test(dataProvider = "createBackUpJobReportsColumnsforLoggedInUser_invalid")
	public void createBackUpJobReportsColumnsforLoggedInUser_invalid(String invalidTestCase, String adminToken,
			String organization_type, String organization_id, String[] visible, String[] order_id,
			int ExpectedStatusCode, SpogMessageCode Errormessage) {

		test = ExtentManager.getNewTest(
				organization_type + "Organization postBackUpJobReportsColumnsforLoggedInUser" + invalidTestCase);

		spogJobServer.setToken(adminToken);

		ArrayList<HashMap<String, Object>> expected_columns = new ArrayList<>();

		HashMap<String, Object> temp = new HashMap<>();

		test.log(LogStatus.INFO,
				"composing the createBackUpJobReportsColumnsforLoggedInUser in org: " + organization_type);

		for (int j = 3; j < 5; j++) {
			int index1 = gen_random_index(visible);

			if (invalidTestCase == "column_id_does_not_exist") {
				temp = spogReportServer.composeBackUpJobReportsColumns(UUID.randomUUID().toString(), visible[index1],
						order_id[j]);

				j = 5;
			} else if (invalidTestCase == "column_id_already_exist") {
				temp = spogReportServer.composeBackUpJobReportsColumns(columnIdList.get(1), visible[index1],
						order_id[j]);
			} else if (invalidTestCase == "order_id_already_exist") {
				temp = spogReportServer.composeBackUpJobReportsColumns(columnIdList.get(j), visible[index1], "1");
			} else if (invalidTestCase == "order_id_greaterthan_default_column_size") {
				temp = spogReportServer.composeBackUpJobReportsColumns(columnIdList.get(j), visible[index1], "20");
			} else if (invalidTestCase == "order_id_lessthan_1") {
				temp = spogReportServer.composeBackUpJobReportsColumns(columnIdList.get(j), visible[index1], "0");
			} else if (invalidTestCase == "order_id_null") {
				temp = spogReportServer.composeBackUpJobReportsColumns(columnIdList.get(j), visible[index1], " ");
			} else {
				temp = spogReportServer.composeBackUpJobReportsColumns(columnIdList.get(j), visible[index1],
						order_id[j]);
			}

			expected_columns.add(temp);
		}

		test.log(LogStatus.INFO,
				"Create the createBackUpJobReportsColumnsforLoggedInUser in org: " + organization_type);
		Response response = spogReportServer.createBackUpJobReportsColumnsforLoggedInUser(adminToken, expected_columns,
				ExpectedStatusCode, test);

		test.log(LogStatus.INFO, "check the backup job reports columns");
		spogJobServer.compareJobColumnsContent(response, expected_columns, columnsHeadContent, ExpectedStatusCode,
				Errormessage, test);

		if (invalidTestCase == "Invalid Authorization with junk token") {
			test.log(LogStatus.INFO,
					"delete the createBackUpJobReportsColumnsforLoggedInUser in org: " + organization_type);
			spogReportServer.deleteBackUpJobReportsColumnsForLoggedInUser(ti.direct_org1_user1_token,
					SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		} else if (invalidTestCase == "Invalid Authorization with missing token") {
			test.log(LogStatus.INFO,
					"delete the createBackUpJobReportsColumnsforLoggedInUser in org: " + organization_type);
			spogReportServer.deleteBackUpJobReportsColumnsForLoggedInUser(ti.normal_msp1_suborg1_user1_token,
					SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		} else {
			test.log(LogStatus.INFO,
					"delete the createBackUpJobReportsColumnsforLoggedInUser in org: " + organization_type);
			spogReportServer.deleteBackUpJobReportsColumnsForLoggedInUser(adminToken,
					SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		}

	}

	/******************************************************************
	 * RandomFunction
	 ******************************************************************************/
	public int gen_random_index(String[] job_status) {
		Random generator = new Random();
		int randomindx = generator.nextInt(job_status.length);

		return randomindx;
	}

	@AfterMethod
	public void afterMethodTest() {
		rep.endTest(test);
		rep.flush();
	}

	@AfterMethod
	public void getResult(ITestResult result) {
		if (result.getStatus() == ITestResult.FAILURE) {
			count1.setfailedcount();
			test.log(LogStatus.FAIL, "Test Case Failed is " + result.getName() + " with parameters as "
					+ Arrays.asList(result.getParameters()));
			test.log(LogStatus.FAIL, result.getThrowable().getMessage());
		} else if (result.getStatus() == ITestResult.SKIP) {
			count1.setskippedcount();
			test.log(LogStatus.SKIP, "Test Case Skipped is " + result.getName());
		} else if (result.getStatus() == ITestResult.SUCCESS) {
			count1.setpassedcount();
		}
		rep.endTest(test);
	}

}
