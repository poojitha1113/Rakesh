package api.users.usercolumns;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

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
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class DeleteUserColumnsForLoggedInUserTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private UserSpogServer userSpogServer;

	private ExtentTest test;
	private TestOrgInfo ti;

	LocalDate date = LocalDate.now();
	LocalDate yesterday = date.minusDays(1);
	LocalDate tomorrow = yesterday.plusDays(2);

	ArrayList<String> columnIdList = new ArrayList<String>();
	ArrayList<HashMap<String, Object>> columnsHeadContent = new ArrayList<HashMap<String, Object>>();

	private String org_model_prefix = this.getClass().getSimpleName();

	@BeforeClass
	@Parameters({ "baseURI", "port", "csrAdminUserName", "csrAdminPassword", "csrReadOnlyAdminName",
			"csrReadOnlyAdminPassword", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword,
			String readOnlyUserName, String readOnlyPassword, String logFolder, String runningMachine,
			String buildVersion) {

		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		rep = ExtentManager.getInstance(this.getClass().getSimpleName(), logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Nagepalli.Ramya";

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

		userSpogServer.setToken(ti.direct_org1_user1_token);
		Response response = userSpogServer.getUsersColumns(SpogConstants.SUCCESS_GET_PUT_DELETE, test);

		columnsHeadContent = response.then().extract().path("data");
		int length = columnsHeadContent.size();
		for (int i = 0; i < length; i++) {
			HashMap<String, Object> HeadContent = columnsHeadContent.get(i);

			columnIdList.add((String) HeadContent.get("column_id"));
		}

	}

	@DataProvider(name = "deleteUsersColumnsForLoggedInUser")
	public Object[][] deleteUsersColumnsForLoggedInUser_200() {
		return new Object[][] {

				{ "csrreadonly", ti.csr_readonly_token, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "2", "3", "1", "4", "7", "5", "6", "4" }, 4 },

				{ "direct", ti.direct_org1_user1_token, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "2", "3", "1", "4", "7", "5", "6", "4" }, 4 },

				{ "direct-visibleTrue", ti.direct_org1_user1_token, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "true" },
						new String[] { "2", "3", "1", "4", "7", "5", "6", "4" }, 4 },
				{ "direct-visibleFalse", ti.direct_org1_user1_token, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "false" },
						new String[] { "2", "3", "1", "4", "7", "5", "6", "4" }, 4 },

				{ "msp", ti.normal_msp_org1_user1_token, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "3", "6", "2", "4", "1", "4", "5", "7" }, 7 },

				{ "suborg", ti.normal_msp1_suborg1_user1_token, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "3", "2", "1", "4", "5", "7", "6", "4" }, 1 },

				// Delete the existing user user columns and create the same
				// columns
				{ "direct-deleteCreateSame", ti.direct_org1_user1_token, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "2", "3", "1", "4", "7", "5", "6", "4" }, 4 },

				{ "direct-sameColId", ti.direct_org1_user1_token, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "2", "3", "1", "4", "7", "5", "6", "4" }, 2 },

				{ "direct-sameColIdOrderId", ti.direct_org1_user1_token, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "2", "2", "2", "2", "3", "3", "2" }, 2 },

				{ "direct-sameorderid", ti.direct_org1_user1_token, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "2", "3", "2", "2", "3", "3", "2", "4" }, 4 },

				// csr read only cases

				{ "csrreadonly-deleteCreateSame", ti.csr_readonly_token, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "2", "3", "1", "4", "7", "5", "6", "4" }, 4 },

				{ "csrreadonly-sameColId", ti.csr_readonly_token, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "2", "3", "1", "4", "7", "5", "6", "4" }, 2 },

				{ "csrreadonly-sameColIdOrderId", ti.csr_readonly_token, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "2", "2", "2", "2", "3", "3", "2" }, 2 },

				{ "csrreadonly-sameorderid", ti.csr_readonly_token, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "2", "3", "2", "2", "3", "3", "2", "4" }, 4 }, };
	}

	@Test(dataProvider = "deleteUsersColumnsForLoggedInUser", enabled = true)
	public void deleteUsersColumnsForLoggedInUserTest_200(String organizationType, String validToken, String[] sort,
			String[] filter, String[] visible, String[] order_id, int noofcolumnstocreate) {
		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		test.assignAuthor("Nagepalli.Ramya");
		userSpogServer.setToken(validToken);

		test.log(LogStatus.INFO, "Delete user columns by user id for organization : " + organizationType);
		userSpogServer.deleteUserColumnsForLoggedInUserWithCheck(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);

		ArrayList<HashMap<String, Object>> expectedColumns = new ArrayList<>();
		ArrayList<HashMap<String, Object>> expected_columns1 = new ArrayList<>();
		HashMap<String, Object> temp = new HashMap<>();

		for (int i = 0; i < noofcolumnstocreate; i++) {

			int index1 = gen_random_index(sort);
			int index2 = gen_random_index(filter);
			int index3 = gen_random_index(visible);

			if (organizationType.contains("sameColId")) {
				temp = userSpogServer.composeUser_Column(columnIdList.get(0), sort[index1], filter[index2],
						visible[index3], order_id[i]);
				if (i != 1) {
					expected_columns1.add(temp);
				}
			} else {
				temp = userSpogServer.composeUser_Column(columnIdList.get(i), sort[index1], filter[index2],
						visible[index3], order_id[i]);
			}
			// temp = userSpogServer.composeUser_Column(columnIdList.get(i),
			// sort[index1], filter[index2], visible[index3], order_id[i]);
			expectedColumns.add(temp);
		}

		test.log(LogStatus.INFO, "Create user columns for logged in user in organization : " + organizationType);
		Response response = userSpogServer.createUserColumnsForLoggedInUser(validToken, expectedColumns, test);

		test.log(LogStatus.INFO, "Compare user columns by user id for organization : " + organizationType);
		if (organizationType.contains("direct-sameColId") || organizationType.contains("csrreadonly-sameColId")) {
			userSpogServer.compareUserColumnsContent(response, expected_columns1, columnsHeadContent,
					SpogConstants.SUCCESS_POST, null, test);
		} else {
			userSpogServer.compareUserColumnsContent(response, expectedColumns, columnsHeadContent,
					SpogConstants.SUCCESS_POST, null, test);
		}

		test.log(LogStatus.INFO, "Delete user columns by user id for organization : " + organizationType);
		userSpogServer.deleteUserColumnsForLoggedInUserWithCheck(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);

		if ((organizationType == "direct-deleteCreateSame") || (organizationType == "csrreadonly-deleteCreateSame")) {
			test.log(LogStatus.INFO, "Create user columns by user id for organization : " + organizationType);
			response = userSpogServer.createUserColumnsForLoggedInUser(validToken, expectedColumns, test);

			test.log(LogStatus.INFO, "Compare user columns by user id for organization : " + organizationType);
			userSpogServer.compareUserColumnsContent(response, expectedColumns, columnsHeadContent,
					SpogConstants.SUCCESS_POST, null, test);

			test.log(LogStatus.INFO, "Delete user columns by user id for organization : " + organizationType);
			userSpogServer.deleteUserColumnsForLoggedInUserWithCheck(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE,
					null, test);

		}
	}

	@Test(dataProvider = "deleteUsersColumnsForLoggedInUser", enabled = true)
	public void deleteUsersColumnsForLoggedInUserTest_401(String organizationType, String validToken, String[] sort,
			String[] filter, String[] visible, String[] order_id, int noofcolumnstocreate) {
		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		test.assignAuthor("Nagepalli.Ramya");
		userSpogServer.setToken(validToken);

		ArrayList<HashMap<String, Object>> expectedColumns = new ArrayList<>();
		ArrayList<HashMap<String, Object>> expected_columns1 = new ArrayList<>();
		HashMap<String, Object> temp = new HashMap<>();

		for (int i = 0; i < noofcolumnstocreate; i++) {

			int index1 = gen_random_index(sort);
			int index2 = gen_random_index(filter);
			int index3 = gen_random_index(visible);

			if (organizationType.contains("sameColId")) {
				temp = userSpogServer.composeUser_Column(columnIdList.get(0), sort[index1], filter[index2],
						visible[index3], order_id[i]);
				if (i != 1) {
					expected_columns1.add(temp);
				}
			} else {
				temp = userSpogServer.composeUser_Column(columnIdList.get(i), sort[index1], filter[index2],
						visible[index3], order_id[i]);
			}
			// temp = userSpogServer.composeUser_Column(columnIdList.get(i),
			// sort[index1], filter[index2], visible[index3], order_id[i]);
			expectedColumns.add(temp);
		}

		test.log(LogStatus.INFO, "Create user columns for logged in user in organization : " + organizationType);
		Response response = userSpogServer.createUserColumnsForLoggedInUser(validToken, expectedColumns, test);

		test.log(LogStatus.INFO, "Compare user columns by user id for organization : " + organizationType);
		if (organizationType.contains("sameColId")) {
			userSpogServer.compareUserColumnsContent(response, expected_columns1, columnsHeadContent,
					SpogConstants.SUCCESS_POST, null, test);
		} else {
			userSpogServer.compareUserColumnsContent(response, expectedColumns, columnsHeadContent,
					SpogConstants.SUCCESS_POST, null, test);
		}

		test.log(LogStatus.INFO, "Delete user columns by user id for organization : " + organizationType);
		userSpogServer.deleteUserColumnsForLoggedInUserWithCheck("siteToken", SpogConstants.NOT_LOGGED_IN,
				SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);

		test.log(LogStatus.INFO, "Delete user columns by user id for organization : " + organizationType);
		userSpogServer.deleteUserColumnsForLoggedInUserWithCheck(validToken + "123", SpogConstants.NOT_LOGGED_IN,
				SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);

		test.log(LogStatus.INFO, "Delete user columns by user id for organization : " + organizationType);
		userSpogServer.deleteUserColumnsForLoggedInUserWithCheck("", SpogConstants.NOT_LOGGED_IN,
				SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);

		test.log(LogStatus.INFO, "Delete user columns by user id for organization : " + organizationType);
		userSpogServer.deleteUserColumnsForLoggedInUserWithCheck(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);

		if (organizationType.contains("deleteCreateSame")) {
			test.log(LogStatus.INFO, "Create user columns by user id for organization : " + organizationType);
			response = userSpogServer.createUserColumnsForLoggedInUser(validToken, expectedColumns, test);

			test.log(LogStatus.INFO, "Compare user columns by user id for organization : " + organizationType);
			userSpogServer.compareUserColumnsContent(response, expectedColumns, columnsHeadContent,
					SpogConstants.SUCCESS_POST, null, test);

			test.log(LogStatus.INFO, "Delete user columns by user id for organization : " + organizationType);
			userSpogServer.deleteUserColumnsForLoggedInUserWithCheck("siteToken", SpogConstants.NOT_LOGGED_IN,
					SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);

			test.log(LogStatus.INFO, "Delete user columns by user id for organization : " + organizationType);
			userSpogServer.deleteUserColumnsForLoggedInUserWithCheck(validToken + "123", SpogConstants.NOT_LOGGED_IN,
					SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);

			test.log(LogStatus.INFO, "Delete user columns by user id for organization : " + organizationType);
			userSpogServer.deleteUserColumnsForLoggedInUserWithCheck("", SpogConstants.NOT_LOGGED_IN,
					SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);

			test.log(LogStatus.INFO, "Delete user columns by user id for organization : " + organizationType);
			userSpogServer.deleteUserColumnsForLoggedInUserWithCheck(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE,
					null, test);

		}
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
