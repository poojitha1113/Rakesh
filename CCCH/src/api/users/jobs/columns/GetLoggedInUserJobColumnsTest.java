
package api.users.jobs.columns;

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

import Constants.JobColumnConstants;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.SPOGJobServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class GetLoggedInUserJobColumnsTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private SPOGJobServer spogJobServer;

	private ExtentTest test;
	private TestOrgInfo ti;

	ArrayList<String> columnIdList = new ArrayList<String>();
	ArrayList<String> MSPColumnIdList = new ArrayList<String>();

	ArrayList<HashMap<String, Object>> columnsHeadContent = new ArrayList<HashMap<String, Object>>();
	ArrayList<HashMap<String, Object>> MSPColumnsHeadContent = new ArrayList<HashMap<String, Object>>();


	@BeforeClass
	@Parameters({ "baseURI", "port","logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runningMachine,
			String buildVersion) {

		spogServer = new SPOGServer(baseURI, port);
		spogJobServer = new SPOGJobServer(baseURI, port);
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
		Response response = spogServer.getJobsColumns(test);

		columnsHeadContent = response.then().extract().path("data");
		int length = columnsHeadContent.size();
		for (int i = 0; i < length; i++) {
			HashMap<String, Object> HeadContent = columnsHeadContent.get(i);

			columnIdList.add((String) HeadContent.get("column_id"));
		}
		
		spogServer.setToken(ti.normal_msp_org1_user1_token);
		response = spogServer.getJobsColumns(test);
		MSPColumnsHeadContent = response.then().extract().path("data");
		length = MSPColumnsHeadContent.size();
		for (int i = 0; i < length; i++) {
			HashMap<String, Object> HeadContent = MSPColumnsHeadContent.get(i);
			MSPColumnIdList.add((String) HeadContent.get("column_id"));
		}

	}

	@DataProvider(name = "get_job_Column_valid")
	public final Object[][] getJobColumnValidParams() {

		return new Object[][] {
				{ "csrreadonly", ti.csr_readonly_token, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "4", "1", "3", "2", }, 4 },
				{ "direct-csr", ti.csr_token, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "4", "1", "3", "2", }, 4 },
				{ "msp-csr", ti.csr_token, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "7", "8", "9", "1", "2", "3", "4", "5", "6" }, 5 },
				{ "suborg-csr", ti.csr_token, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9" }, 6 },
				{ "suborgmsp-csr", ti.csr_token, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "9", "3", "4", "5", "6", "1", "2", "7", "8" }, 3 },
				{ "direct", ti.direct_org1_user1_token, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "4", "1", "3", "2", }, 2 },
				{ "msp", ti.normal_msp_org2_user1_token, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "5", "6", "7", "8", "9", "1", "2", "3", "4" }, 4 },
				{ "suborg", ti.normal_msp1_suborg2_user1_token, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "7", "8", "9", "1", "2", "3", "4", "5", "6" }, 5 },
				{ "suborgmsptoken", ti.normal_msp_org1_user1_token, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "5", "6", "7", "8", "9", "1", "2", "3", "4" }, 7 },
				// 3 tier cases
				{ "rootmsp", ti.root_msp_org2_user1_token, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "5", "6", "7", "8", "9", "1", "2", "3", "4" }, 4 },
				{ "rootsuborg", ti.root_msp1_suborg2_user1_token, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "7", "8", "9", "1", "2", "3", "4", "5", "6" }, 5 },
				{ "rootsub-root", ti.root_msp_org1_user1_token, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "5", "6", "7", "8", "9", "1", "2", "3", "4" }, 7 },
				{ "submsp", ti.root_msp1_submsp1_user1_token, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "7", "8", "9", "1", "2", "3", "4", "5", "6" }, 5 },
				{ "submsp_sub", ti.msp1_submsp1_suborg1_user1_token, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "7", "8", "9", "1", "2", "3", "4", "5", "6" }, 5 },
				{ "submsp_sub-submsp", ti.root_msp1_submsp1_user1_token, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "7", "8", "9", "1", "2", "3", "4", "5", "6" }, 5 },

		};

	}

	// get the user defined job columns using csr/valid token
	@Test(dataProvider = "get_job_Column_valid")
	public void getJobColumnsForLoggedInUser_200(String organizationType, String validToken, String[] sort,
			String[] filter, String[] visible, String[] orderId, int userInput) {
		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		test.assignAuthor("");

		spogJobServer.setToken(validToken);

		test.log(LogStatus.INFO, "get job columns in org: " + organizationType + "without creating");

		if (organizationType.equalsIgnoreCase("msp")) {
			columnsHeadContent = MSPColumnsHeadContent;
			columnIdList = MSPColumnIdList;
		}

		Response response2 = spogJobServer.getJobColumnsForLoggedInUser(validToken, test);

		spogJobServer.compareJobColumnsContent(response2, columnsHeadContent, columnsHeadContent,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		test.log(LogStatus.INFO, "composing the job columns in org: " + organizationType);

		ArrayList<HashMap<String, Object>> expected_columns = new ArrayList<>();

		HashMap<String, Object> temp = new HashMap<>();

		for (int i = 0; i < userInput; i++) {
			int index1 = gen_random_index(sort);

			int index2 = gen_random_index(filter);

			int index3 = gen_random_index(visible);

			temp = spogJobServer.composejob_Column(columnIdList.get(i), sort[index1], filter[index2], visible[index3],
					orderId[i]);

			expected_columns.add(temp);
		}

		test.log(LogStatus.INFO, "Create the job columns in org: " + organizationType);

		Response response = spogJobServer.createJobColumnsForLoggedInUser(validToken, expected_columns, test);

		test.log(LogStatus.INFO, "Comparing the job columns in org: with the Response");

		spogJobServer.compareJobColumnsContent(response, expected_columns, columnsHeadContent,
				SpogConstants.SUCCESS_POST, null, test);

		test.log(LogStatus.INFO, "get the created job columns in org: " + organizationType + " with the valid Token");

		Response response1 = spogJobServer.getJobColumnsForLoggedInUser(validToken, test);

		spogJobServer.compareJobColumnsContent(response1, expected_columns, columnsHeadContent,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		test.log(LogStatus.INFO,
				"deleting the created job columns in org: " + organizationType + " with the valid Token");

		spogJobServer.deleteJobColumnsForLoggedInUser(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

	}

	@DataProvider(name = "get_job_Column_Invalid")
	public final Object[][] getJobColumnInValidParams() {

		return new Object[][] {
				{ "csrreadonly", ti.csr_readonly_token, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "4", "7", "3", "2", }, 4 },
				{ "Direct", ti.direct_org1_user1_token, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "4", "7", "3", "2", }, 4 },
				{ "suborgundermsp", ti.normal_msp_org1_msp_accountadmin1_token,
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "4", "7", "3", "2", }, 4 }

		};

	}

	// get the user defined job columns using Invalid token, missed

	@Test(dataProvider = "get_job_Column_Invalid")
	public void getJobColumnsForLoggedInUser_401(String organizationType, String validToken, String[] sort,
			String[] filter, String[] visible, String[] orderId, int userInput) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		test.assignAuthor("");

		spogJobServer.setToken(validToken);

		ArrayList<HashMap<String, Object>> expected_columns = new ArrayList<>();

		HashMap<String, Object> temp = new HashMap<>();

		test.log(LogStatus.INFO, "composing the job columns in org: " + organizationType);

		for (int i = 0; i < userInput; i++) {
			int index1 = gen_random_index(sort);

			int index2 = gen_random_index(filter);

			int index3 = gen_random_index(visible);

			temp = spogJobServer.composejob_Column(columnIdList.get(i), sort[index1], filter[index2], visible[index3],
					orderId[i]);

			expected_columns.add(temp);
		}

		test.log(LogStatus.INFO, "Create the job columns in org: " + organizationType);

		Response response = spogJobServer.createJobColumnsForLoggedInUser(validToken, expected_columns, test);

		test.log(LogStatus.INFO, "Comparing the job columns in org: with the Response" + organizationType);

		spogJobServer.compareJobColumnsContent(response, expected_columns, columnsHeadContent,
				SpogConstants.SUCCESS_POST, null, test);

		String Not_valid_Token = validToken + "Abc";

		test.log(LogStatus.INFO, "get the job columns in org: with the InValid Token");

		Response response1 = spogJobServer.getJobColumnsForLoggedInUser(Not_valid_Token, test);

		spogJobServer.compareJobColumnsContent(response1, expected_columns, columnsHeadContent,
				SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);

		String MissingToken = "";

		test.log(LogStatus.INFO, "get the job columns in org: with the MissingToken ");

		Response response2 = spogJobServer.getJobColumnsForLoggedInUser(MissingToken, test);

		spogJobServer.compareJobColumnsContent(response2, expected_columns, columnsHeadContent,
				SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);

		test.log(LogStatus.INFO,
				"deleting the created job columns in org: " + organizationType + " with the valid Token");

		spogJobServer.deleteJobColumnsForLoggedInUser(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

	}

	@DataProvider(name = "get_job_Column_msp_admin_account")
	public final Object[][] get_job_Column_msp_admin_account() {

		return new Object[][] { { "MSP ADMIN Account", ti.normal_msp_org1_msp_accountadmin1_token,
				new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
				new String[] { "true", "false", "none" }, new String[] { "4", "7", "3", "2", }, 4 }

		};

	}

	@Test(dataProvider = "get_job_Column_msp_admin_account")
	public void getJobColumnsForLoggedInUser_msp_admin_account(String organizationType, String validToken,
			String[] sort, String[] filter, String[] visible, String[] orderId, int userInput) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		test.assignAuthor("");

		spogJobServer.setToken(validToken);

		ArrayList<HashMap<String, Object>> expected_columns = new ArrayList<>();

		if (organizationType.equalsIgnoreCase("msp")) {
			columnsHeadContent = MSPColumnsHeadContent;
			columnIdList = MSPColumnIdList;
		}

		HashMap<String, Object> temp = new HashMap<>();

		test.log(LogStatus.INFO, "composing the job columns in org: " + organizationType);

		for (int i = 0; i < userInput; i++) {
			int index1 = gen_random_index(sort);

			int index2 = gen_random_index(filter);

			int index3 = gen_random_index(visible);

			temp = spogJobServer.composejob_Column(columnIdList.get(i), sort[index1], filter[index2], visible[index3],
					orderId[i]);

			expected_columns.add(temp);
		}

		test.log(LogStatus.INFO, "Create the job columns in org: " + organizationType);

		Response response = spogJobServer.createJobColumnsForLoggedInUser(validToken, expected_columns, test);

		test.log(LogStatus.INFO, "Comparing the job columns in org: with the Response" + organizationType);

		spogJobServer.compareJobColumnsContent(response, expected_columns, columnsHeadContent,
				SpogConstants.SUCCESS_POST, null, test);

		test.log(LogStatus.INFO, "get the job columns from suborg managed by msp admin with the valid msp admin Token");

		response = spogJobServer.getJobColumnsForLoggedInUser(validToken, test);
		spogJobServer.compareJobColumnsContent(response, expected_columns, columnsHeadContent,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		test.log(LogStatus.INFO,
				"deleting the created job columns in org: " + organizationType + " with the valid Token");
		spogJobServer.deleteJobColumnsForLoggedInUser(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

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
		// endTest(logger) : It ends the current test and prepares to create
		// HTML report
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
