package api.users.usercolumns;

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

public class getUserColumnsByUserIdTest extends base.prepare.Is4Org {

	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private UserSpogServer userSpogServer;
	private ExtentTest test;
	private String org_model_prefix = this.getClass().getSimpleName();
	// used for test case count like passed,failed,remaining cases
	ArrayList<String> columnIdList = new ArrayList<String>();
	ArrayList<HashMap<String, Object>> columnsHeadContent = new ArrayList<HashMap<String, Object>>();
	private TestOrgInfo ti;

	@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runningMachine, String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		rep = ExtentManager.getInstance("getUserColumnsByUserIdTest", logFolder);
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
	public Object[][] getUsersColumnsByUserId_200() {
		return new Object[][] {
				{ "csr", ti.csr_token, ti.csr_admin_user_id, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "2", "3", "1", "4", "7", "5", "6", "8" }, 4 },
				{ "csr_readonly", ti.csr_readonly_token, ti.csr_readonly_admin_user_id,
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" },
						new String[] { "2", "3", "1", "4", "7", "5", "6", "8" }, 4 },

				{ "direct", ti.direct_org1_user1_token, ti.direct_org1_user1_id,
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" },
						new String[] { "2", "3", "1", "4", "7", "5", "6", "8" }, 4 },
				{ "direct-csrT", ti.csr_token, ti.direct_org1_user1_id, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "3", "2", "8", "1", "4", "5", "6", "7" }, 6 },

				{ "direct-visibleTrue", ti.direct_org1_user1_token, ti.direct_org1_user1_id,
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "true" }, new String[] { "2", "3", "1", "4", "7", "5", "6", "8" }, 4 },
				{ "direct-visibleFalse", ti.direct_org1_user1_token, ti.direct_org1_user1_id,
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "false" }, new String[] { "2", "3", "1", "4", "7", "5", "6", "8" }, 4 },

				{ "msp", ti.root_msp_org1_user1_token, ti.root_msp_org1_user1_id, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "3", "6", "2", "8", "1", "4", "5", "7" }, 7 },
				{ "msp-csrT", ti.csr_token, ti.root_msp_org1_user1_id, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "3", "6", "2", "8", "1", "4", "5", "7" }, 7 },

				{ "suborg", ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "3", "2", "1", "4", "5", "7", "6", "8" }, 1 },
				{ "suborg-mspT", ti.root_msp_org1_user1_token, ti.root_msp1_suborg1_user1_id,
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "3", "2", "1", "4", "5", "7", "6", "8" }, 1 },
				{ "suborg-mspAAT", ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp1_suborg1_user1_id,
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "3", "2", "1", "4", "5", "7", "6", "8" }, 1 },
				{ "suborg-csrT", ti.csr_token, ti.root_msp1_suborg1_user1_id, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "3", "2", "1", "4", "5", "7", "6", "8" }, 1 },

				{ "submsp", ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "3", "6", "2", "8", "1", "4", "5", "7" }, 7 },
				{ "submsp-csrT", ti.csr_token, ti.root_msp1_submsp1_user1_id, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "3", "6", "2", "8", "1", "4", "5", "7" }, 7 },

				{ "submsp_suborg", ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "3", "2", "1", "4", "5", "7", "6", "8" }, 1 },
				{ "submsp_suborg-submspT", ti.root_msp1_submsp1_user1_token, ti.msp1_submsp1_suborg1_user1_id,
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "3", "2", "1", "4", "5", "7", "6", "8" }, 1 },
				{ "submsp_suborg-submspAAT", ti.root_msp1_submsp1_account_admin_token, ti.msp1_submsp1_suborg1_user1_id,
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "3", "2", "1", "4", "5", "7", "6", "8" }, 1 },
				{ "submsp_suborg-csrT", ti.csr_token, ti.msp1_submsp1_suborg1_user1_id,
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "3", "2", "1", "4", "5", "7", "6", "8" },
						1 } };

	}

	@Test(dataProvider = "getUsersColumnsByUserId_200", enabled = true)
	public void getUserColumnsByUserId_200(String organizationType, String validToken, String user_id, String[] sort,
			String[] filter, String[] visible, String[] order_id, int noofcolumnstocreate) {
		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		ArrayList<HashMap<String, Object>> expectedColumns = new ArrayList<>();
		HashMap<String, Object> temp = new HashMap<>();

		for (int i = 0; i < noofcolumnstocreate; i++) {

			int index1 = gen_random_index(sort);
			int index2 = gen_random_index(filter);
			int index3 = gen_random_index(visible);

			temp = userSpogServer.composeUser_Column(columnIdList.get(i), sort[index1], filter[index2], visible[index3],
					order_id[i]);
		}

		expectedColumns.add(temp);

		test.log(LogStatus.INFO, "Create user columns by user id for organization : " + organizationType);
		Response response = userSpogServer.createUserColumnsByUserId(validToken, user_id, expectedColumns, test);

		test.log(LogStatus.INFO, "Get user columns by user id: " + organizationType);
		userSpogServer.getUserColumnsByUserId(validToken, user_id, expectedColumns, columnsHeadContent,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		test.log(LogStatus.INFO, "Delete user columns by user id for organization : " + organizationType);
		userSpogServer.deleteUserColumnsforSpecifiedUserwithCheck(user_id, validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

	}

	@DataProvider(name = "getUserColumnsByUserId_403")
	public final Object[][] getUserColumnsByUserId_403() {

		return new Object[][] {
				// different users
				{ "direct-mspT", ti.direct_org1_user1_token, ti.root_msp_org1_user1_token, ti.direct_org1_user1_id,
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" },
						new String[] { "2", "3", "1", "4", "7", "5", "6", "8" }, 4 },
				{ "direct-suborgT", ti.direct_org1_user1_token, ti.root_msp1_suborg1_user1_token,
						ti.direct_org1_user1_id, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "2", "3", "1", "4", "7", "5", "6", "8" }, 6 },
				{ "direct-submspT", ti.direct_org1_user1_token, ti.root_msp1_submsp1_user1_token,
						ti.direct_org1_user1_id, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "2", "3", "1", "4", "7", "5", "6", "8" }, 4 },
				{ "direct-submspsuborgT", ti.direct_org1_user1_token, ti.msp1_submsp1_suborg1_user1_token,
						ti.direct_org1_user1_id, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "2", "3", "1", "4", "7", "5", "6", "8" }, 6 },

				// Get user columns by user id(msp org) and token of direct org should return
				// 403
				{ "msp-directT", ti.root_msp_org1_user1_token, ti.direct_org1_user1_token, ti.root_msp_org1_user1_id,
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "3", "6", "2", "7", "1", "4", "5", "7" }, 4 },
				// Get user columns by user id(msp org) and token of sub org should return 403
				{ "msp-suborgT", ti.root_msp_org1_user1_token, ti.root_msp1_suborg1_user1_token,
						ti.root_msp_org1_user1_id, new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "3", "6", "2", "8", "1", "4", "5", "7" }, 6 },
				// Get user columns by user id(msp org) and token of MSPb org should return 403
				{ "msp-mspbT", ti.root_msp_org1_user1_token, ti.root_msp_org2_user1_token, ti.root_msp_org1_user1_id,
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "3", "6", "2", "8", "1", "4", "5", "7" }, 5 },

				{ "submsp-directT", ti.root_msp1_submsp1_user1_token, ti.direct_org1_user1_token,
						ti.root_msp1_submsp1_user1_id, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "3", "6", "2", "7", "1", "4", "5", "7" }, 4 },
				// Get user columns by user id(msp org) and token of sub org should return 403
				{ "submsp-suborgT", ti.root_msp1_submsp1_user1_token, ti.root_msp1_suborg1_user1_token,
						ti.root_msp1_submsp1_user1_id, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "3", "6", "2", "8", "1", "4", "5", "7" }, 6 },

				// Get user columns by user id(msp org) and token of MSPb org should return 403
				{ "submsp-mspbT", ti.root_msp1_submsp1_user1_token, ti.root_msp_org2_user1_token,
						ti.root_msp1_submsp1_user1_id, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "3", "6", "2", "8", "1", "4", "5", "7" }, 5 },

				// Get user columns by user id(sub org) and token of direct org should return
				// 403
				{ "suborg-directT", ti.root_msp1_suborg1_user1_token, ti.direct_org1_user1_token,
						ti.root_msp1_suborg1_user1_id, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "1", "2", "3", "4", "5", "6", "7", "3" }, 4 },

				// Get user columns by user id(sub org) and token of suborgB should return 403
				{ "suborg-suborgbT", ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg2_user1_token,
						ti.root_msp1_suborg1_user1_id, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "1", "2", "3", "4", "5", "6", "7", "8" }, 3 },

				{ "csr-dirctT", ti.csr_token, ti.direct_org1_user1_token, ti.csr_admin_user_id,
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "4", "2", "3", "1", "5", "6", "8", "7" }, 6 },

				{ "csr-mspT", ti.csr_token, ti.root_msp_org1_user1_token, ti.csr_admin_user_id,
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "1", "2", "3", "7", "5", "4", "7", "6" }, 4 },

				{ "csr-suborgT", ti.csr_token, ti.root_msp1_suborg1_user1_token, ti.csr_admin_user_id,
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "1", "7", "5", "4", "3", "6", "2", "8" }, 3 },

		};

	}

	@Test(dataProvider = "getUserColumnsByUserId_403", enabled = true)
	public void getUserColumnsByUserIdTest_403(String organizationType, String validToken, String otherUserValidToken,
			String user_id, String[] sort, String[] filter, String[] visible, String[] order_id,
			int noofcolumnstocreate) {
		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		test.assignAuthor("Ramesh.Pendurthy");
		SpogMessageCode expected = null;
		userSpogServer.setToken(validToken);

		ArrayList<HashMap<String, Object>> expectedColumns = new ArrayList<>();
		HashMap<String, Object> temp = new HashMap<>();

		for (int i = 0; i < noofcolumnstocreate; i++) {

			int index1 = gen_random_index(sort);
			int index2 = gen_random_index(filter);
			int index3 = gen_random_index(visible);

			temp = userSpogServer.composeUser_Column(columnIdList.get(i), sort[index1], filter[index2], visible[index3],
					order_id[i]);
		}

		expectedColumns.add(temp);

		test.log(LogStatus.INFO, "Create user columns by user id for organization : " + organizationType);
		Response response = userSpogServer.createUserColumnsByUserId(validToken, user_id, expectedColumns, test);

		test.log(LogStatus.INFO, "Get user columns by user id: " + organizationType);
		userSpogServer.getUserColumnsByUserId(otherUserValidToken, user_id, expectedColumns, columnsHeadContent,
				SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY, test);

		test.log(LogStatus.INFO, "Delete user columns by user id for organization : " + organizationType);
		userSpogServer.deleteUserColumnsforSpecifiedUserwithCheck(user_id, ti.csr_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

	}

	@DataProvider(name = "getUserColumnsByUserIdInvalid")
	public final Object[][] getUserColumnsByUserIdInvalid() {
		return new Object[][] {
				// 400
				{ "Get user columns by invalid user id with direct org user token", ti.direct_org1_user1_token,
						"invaid_id", SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_USERID_IS_NOT_UUID },
				{ "Get user columns by invalid user id with msp org user token", ti.root_msp_org1_user1_token,
						"invaid_id", SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_USERID_IS_NOT_UUID },
				{ "Get user columns by invalid user id with sub org user token", ti.root_msp1_suborg1_user1_token,
						"invaid_id", SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_USERID_IS_NOT_UUID },
				{ "Get user columns by invalid user id with msp account admin user token",
						ti.root_msp1_submsp1_account_admin_token, "invaid_id", SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_USERID_IS_NOT_UUID },
				{ "Get user columns by invalid user id with submsp org user token", ti.root_msp1_submsp1_user1_token,
						"invaid_id", SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_USERID_IS_NOT_UUID },
				{ "Get user columns by invalid user id with sub msp sub org user token",
						ti.msp1_submsp1_suborg1_user1_token, "invaid_id", SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_USERID_IS_NOT_UUID },
				{ "Get user columns by invalid user id with sub msp account admin user token",
						ti.root_msp1_submsp1_account_admin_token, "invaid_id", SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_USERID_IS_NOT_UUID },

				// 401
				{ "Get user columns by user id with invalid token", "invalid", ti.direct_org1_user1_id,
						SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT },
				{ "Get user columns by user id with missing token", "", ti.direct_org1_user1_id,
						SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED },
				{ "Get user columns by user id with null as token", null, ti.direct_org1_user1_id,
						SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED },

				// 404
				{ "Get user columns by user id that does not exit with direct org user token",
						ti.direct_org1_user1_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST,
						SpogMessageCode.USER_ID_DOESNOT_EXIST },
				{ "Get user columns by user id that does not exit with msp org user token",
						ti.root_msp_org1_user1_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST,
						SpogMessageCode.USER_ID_DOESNOT_EXIST },
				{ "Get user columns by user id that does not exit with sub org user token",
						ti.root_msp1_suborg1_user1_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST,
						SpogMessageCode.USER_ID_DOESNOT_EXIST },
				{ "Get user columns by user id that does not exit with msp account admin user token",
						ti.root_msp1_submsp1_account_admin_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST,
						SpogMessageCode.USER_ID_DOESNOT_EXIST },
				{ "Get user columns by user id that does not exit with submsp org user token",
						ti.root_msp1_submsp1_user1_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST,
						SpogMessageCode.USER_ID_DOESNOT_EXIST },
				{ "Get user columns by user id that does not exit with sub msp sub org user token",
						ti.msp1_submsp1_suborg1_user1_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST,
						SpogMessageCode.USER_ID_DOESNOT_EXIST },
				{ "Get user columns by user id that does not exit with sub msp account admin user token",
						ti.root_msp1_submsp1_account_admin_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST,
						SpogMessageCode.USER_ID_DOESNOT_EXIST }, };

	}

	@Test(dataProvider = "getUserColumnsByUserIdInvalid", enabled = true)
	public void getUserColumnsByUserId_401(String testCase, String token, String user_id, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage) {

		test = ExtentManager.getNewTest(testCase);
		userSpogServer.getUserColumnsByUserId(token, user_id, null, columnsHeadContent, expectedStatusCode,
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
