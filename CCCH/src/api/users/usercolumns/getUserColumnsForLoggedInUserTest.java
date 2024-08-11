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
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class getUserColumnsForLoggedInUserTest extends base.prepare.Is4Org {

	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private UserSpogServer userSpogServer;
	private ExtentTest test;
	// used for test case count like passed,failed,remaining cases
	int passedcases = 0;
	int failedcases = 0;
	int skippedcases = 0;
	ArrayList<String> columnIdList = new ArrayList<String>();
	ArrayList<HashMap<String, Object>> columnsHeadContent = new ArrayList<HashMap<String, Object>>();
	private TestOrgInfo ti;

	@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runningMachine, String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		rep = ExtentManager.getInstance("getUserColumnsForLoggedInUserTest", logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Ramesh.Pendurthy";

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

		userSpogServer.setToken(ti.direct_org1_user1_token);
		Response response = userSpogServer.getUsersColumns(SpogConstants.SUCCESS_GET_PUT_DELETE, test);

		columnsHeadContent = response.then().extract().path("data");
		int length = columnsHeadContent.size();
		for (int i = 0; i < length; i++) {
			HashMap<String, Object> HeadContent = columnsHeadContent.get(i);
			columnIdList.add((String) HeadContent.get("column_id"));
		}
	}

	@DataProvider
	public Object[][] getUserColumnsForLoggedInUser_200() {
		return new Object[][] {

				{ "csr", ti.csr_token, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "2", "3", "1", "4", "7", "5", "6", "8" }, 4 },
				{ "csr_readonly", ti.csr_readonly_token, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "2", "3", "1", "4", "7", "5", "6", "8" }, 4 },

				{ "direct", ti.direct_org1_user1_token, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "2", "3", "1", "4", "7", "5", "6", "8" }, 4 },

				{ "direct-visibleTrue", ti.direct_org1_user1_token, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "true" },
						new String[] { "2", "3", "1", "4", "7", "5", "6", "8" }, 4 },
				{ "direct-visibleFalse", ti.direct_org1_user1_token, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "false" },
						new String[] { "2", "3", "1", "4", "7", "5", "6", "8" }, 4 },

				{ "msp", ti.root_msp_org1_user1_token, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "3", "6", "2", "5", "1", "4", "5", "7" }, 7 },
				{ "mspAA", ti.root_msp_org1_user1_token, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "3", "3", "3", "3", "3", "3", "3", "3" }, 7 },
				{ "suborg", ti.root_msp1_suborg1_user1_token, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "3", "2", "1", "4", "5", "7", "6", "8" }, 1 },
				{ "submsp", ti.root_msp_org1_user1_token, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "3", "6", "2", "5", "1", "4", "5", "7" }, 7 },
				{ "submspAA", ti.root_msp_org1_user1_token, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "3", "3", "3", "3", "3", "3", "3", "3" }, 7 },
				{ "sub_mspsuborg", ti.root_msp1_suborg1_user1_token, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "3", "2", "1", "4", "5", "7", "6", "8" }, 1 }, };
	}

	@Test(dataProvider = "getUserColumnsForLoggedInUser_200", enabled = true)
	public void getUserColumnsForLoggedInUser_200(String organizationType, String validToken, String[] sort,
			String[] filter, String[] visible, String[] order_id, int noofcolumnstocreate) {
		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		test.assignAuthor("Ramesh.Pendurthy");
		userSpogServer.setToken(validToken);

		ArrayList<HashMap<String, Object>> expectedColumns = new ArrayList<>();
		HashMap<String, Object> temp = new HashMap<>();

		for (int i = 0; i < noofcolumnstocreate; i++) {

			int index1 = gen_random_index(sort);
			int index2 = gen_random_index(filter);
			int index3 = gen_random_index(visible);

			temp = userSpogServer.composeUser_Column(columnIdList.get(i), sort[index1], filter[index2], visible[index3],
					order_id[i]);
			expectedColumns.add(temp);
		}

		userSpogServer.deleteUserColumnsForLoggedInUserWithCheck(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);
		test.log(LogStatus.INFO, "Create user columns by user id for organization : " + organizationType);
		Response response = userSpogServer.createUserColumnsForLoggedInUser(validToken, expectedColumns, test);

		test.log(LogStatus.INFO, "Compare user columns by user id for organization : " + organizationType);
		userSpogServer.compareUserColumnsContent(response, expectedColumns, columnsHeadContent,
				SpogConstants.SUCCESS_POST, null, test);

		test.log(LogStatus.INFO, "Get user columns for logged in user: " + organizationType);
		userSpogServer.getUserColumnsForLoggedinUser(validToken, expectedColumns, columnsHeadContent,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		test.log(LogStatus.INFO, "Delete user columns by user id for organization : " + organizationType);
		userSpogServer.deleteUserColumnsForLoggedInUserWithCheck(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);

	}

	@DataProvider(name = "getUserColumnsForLoggedInUser_401")
	public final Object[][] getUserColumnsForLoggedInUser_401() {
		return new Object[][] {
				{ "Get user columns for logged in user with invalid user token", "invalid", SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT },
				{ "Get user columns for logged in user with missing token", "", SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTHENTICATION_FAILED },
				{ "Get user columns for logged in user with null as token", null, SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTHENTICATION_FAILED }, };
	}

	@Test(dataProvider = "getUserColumnsForLoggedInUser_401", enabled = true)
	public void getUsersColumnsByUserIdTest_401(String testCase, String token, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage) {

		test = ExtentManager.getNewTest(testCase);
		userSpogServer.getUserColumnsForLoggedinUser(token, null, columnsHeadContent, expectedStatusCode,
				expectedErrorMessage, test);
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
		// ending test
		// endTest(logger) : It ends the current test and prepares to create HTML report
		rep.endTest(test);
		// rep.flush();
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
