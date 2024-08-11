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
import InvokerServer.SPOGJobServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class UpdateLoggedInUserJobColumnsTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private SPOGJobServer spogJobServer;

	private ExtentTest test;
	private TestOrgInfo ti;

	ArrayList<String> columnIdList = new ArrayList<String>();
	ArrayList<String> MSPColumnIdList = new ArrayList<String>();

	ArrayList<HashMap<String, Object>> columnsHeadContent = new ArrayList<HashMap<String, Object>>();
	ArrayList<HashMap<String, Object>> MSPColumnsHeadContent = new ArrayList<HashMap<String, Object>>();

	@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runningMachine, String buildVersion) {

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

	@DataProvider(name = "update_Job_Column_Valid")
	public final Object[][] updateJobColumnValidParams() {

		return new Object[][] {
				{ "CSR", ti.csr_token, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "4", "7", "3", "2", }, 4 },
				{ "CSR-readonly", ti.csr_readonly_token, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "4", "7", "3", "2", }, 4 },
				{ "DIRECT", ti.direct_org1_user1_token, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "9", "1", "8", "5", }, 3 },
				{ "MSP", ti.normal_msp_org2_user1_token, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9" }, 5 },
				{ "SUBORG", ti.normal_msp1_suborg1_user1_token, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "3", "6", "1", "4", "5", "2", "9", "8", "7" }, 7 },
				{ "SUBORG-MSP", ti.normal_msp_org1_user1_token, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "7", "2", "9", "4", "6", "5", "1", "8", "3" }, 9 },
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
	@Test(dataProvider = "update_Job_Column_Valid")
	public void updateJobColumnsForLoggedInUser_200(String organizationType, String validToken, String[] sort,
			String[] filter, String[] visible, String[] orderId, int userInput) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		test.assignAuthor("");

		spogJobServer.setToken(validToken);

		if (organizationType.equalsIgnoreCase("msp")) {
			columnsHeadContent = MSPColumnsHeadContent;
			columnIdList = MSPColumnIdList;
		}

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

		test.log(LogStatus.INFO, "Update the job columns in org: " + organizationType);

		Response response1 = spogJobServer.updateJobColumnsForLoggedInUser(validToken, expected_columns, test);

		test.log(LogStatus.INFO, "Comparing the job columns in org: with the Response");

		spogJobServer.compareJobColumnsContent(response1, expected_columns, columnsHeadContent,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		test.log(LogStatus.INFO,
				"deleting the created job columns in org: " + organizationType + " with the valid Token");

		spogJobServer.deleteJobColumnsForLoggedInUser(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

	}

	@DataProvider(name = "update_job_Column_Invalid")
	public final Object[][] updateJobColumnInValidParams() {

		return new Object[][] {
				{ "Direct", ti.direct_org1_user1_token, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "4", "7", "3", "2", "5", "1", "6" }, 4 },
				{ "suborg_mspadminaccount", ti.normal_msp_org1_msp_accountadmin1_token,
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "4", "7", "3", "2", "5", "1", "6" },
						4 },
				{ "csrreadonly", ti.csr_readonly_token, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "4", "7", "3", "2", "5", "1", "6" }, 4 }, };

	}

	// update the user defined job columns using Invalid token,missing token
	@Test(dataProvider = "update_job_Column_Invalid")
	public void updateJobColumnsForLoggedInUser_401(String organizationType, String validToken, String[] sort,
			String[] filter, String[] visible, String[] orderId, int userInput

	) {

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

		Response response1 = spogJobServer.createJobColumnsForLoggedInUser(validToken, expected_columns, test);

		test.log(LogStatus.INFO, "Comparing the job columns in organization with Response ");

		spogJobServer.compareJobColumnsContent(response1, expected_columns, columnsHeadContent,
				SpogConstants.SUCCESS_POST, null, test);

		String Not_valid_Token = validToken + "Abc";

		test.log(LogStatus.INFO, "update the job columns in organization with Invalid_Token ");

		Response response = spogJobServer.updateJobColumnsForLoggedInUser(Not_valid_Token, expected_columns, test);

		test.log(LogStatus.INFO, "Comparing the job columns in organization with Response ");

		spogJobServer.compareJobColumnsContent(response, expected_columns, columnsHeadContent,
				SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);

		String MissingToken = "";

		test.log(LogStatus.INFO, "update the job columns in organization with MissingToken ");

		Response response2 = spogJobServer.updateJobColumnsForLoggedInUser(MissingToken, expected_columns, test);

		test.log(LogStatus.INFO, "Comparing the job columns in organization with Response ");

		spogJobServer.compareJobColumnsContent(response2, expected_columns, columnsHeadContent,
				SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);

		test.log(LogStatus.INFO,
				"deleting the created job columns in org: " + organizationType + " with the valid Token");

		spogJobServer.deleteJobColumnsForLoggedInUser(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

	}

	// update does not exist column id,Element is not a uuid
	@Test(dataProvider = "update_job_Column_Invalid")
	public void updateJobColumnsForLoggedInUser_400_Invalid_columnId(String organizationType, String validToken,
			String[] sort, String[] filter, String[] visible, String[] orderId, int userInput) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		test.assignAuthor("");

		spogJobServer.setToken(validToken);

		ArrayList<HashMap<String, Object>> expected_columns = new ArrayList<>();

		HashMap<String, Object> temp = new HashMap<>();

		test.log(LogStatus.INFO, "composing the job columns in org: " + organizationType + "to update job columns");

		for (int i = 0; i < 1; i++) {
			int index1 = gen_random_index(sort);

			int index2 = gen_random_index(filter);

			int index3 = gen_random_index(visible);

			String does_not_exist_column_id = "abc";

			temp = spogJobServer.composejob_Column(does_not_exist_column_id, sort[index1], filter[index2],
					visible[index3], orderId[i]);

			expected_columns.add(temp);
		}

		test.log(LogStatus.INFO,
				"update the job columns in org: " + organizationType + "with does_not_exist_column_id");

		Response response = spogJobServer.updateJobColumnsForLoggedInUser(validToken, expected_columns, test);

		test.log(LogStatus.INFO, "Comparing the job columns in organization with Response ");

		spogJobServer.compareJobColumnsContent(response, expected_columns, columnsHeadContent,
				SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_COLUMNID_NOT_UUID, test);

		test.log(LogStatus.INFO,
				"deleting the created job columns in org: " + organizationType + " with the valid Token");

		spogJobServer.deleteJobColumnsForLoggedInUser(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

	}

	// create the user defined job columns with same column_Id(00D00004)
	@Test(dataProvider = "update_job_Column_Invalid")
	public void updateJobColumnsForLoggedInUser_200_AlreadyExist_columnId(String organizationType, String validToken,
			String[] sort, String[] filter, String[] visible, String[] orderId, int userInput) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		test.assignAuthor("");

		spogJobServer.setToken(validToken);

		if (organizationType.equalsIgnoreCase("msp")) {
			columnsHeadContent = MSPColumnsHeadContent;
			columnIdList = MSPColumnIdList;
		}
		// Update job columns

		ArrayList<HashMap<String, Object>> expected_columns = new ArrayList<>();

		HashMap<String, Object> temp = new HashMap<>();

		ArrayList<HashMap<String, Object>> expected_col = new ArrayList<>();

		HashMap<String, Object> temp1 = new HashMap<>();

		test.log(LogStatus.INFO, "composing the job columns in org: " + organizationType);

		for (int i = 0; i < userInput; i++) {
			int index1 = gen_random_index(sort);

			int index2 = gen_random_index(filter);

			int index3 = gen_random_index(visible);

			temp = spogJobServer.composejob_Column(columnIdList.get(1), sort[index1], filter[index2], visible[index3],
					orderId[i]);

			if (i == 0) {
				temp1 = spogJobServer.composejob_Column(columnIdList.get(1), sort[index1], filter[index2],
						visible[index3], orderId[i]);

				expected_col.add(temp1);
			}

			expected_columns.add(temp);
		}

		test.log(LogStatus.INFO,
				"updating the job columns in org: " + organizationType + "with Already existing column id");

		Response response1 = spogJobServer.updateJobColumnsForLoggedInUser(validToken, expected_columns, test);

		test.log(LogStatus.INFO, "Comparing the job columns in organization with Response ");

		spogJobServer.compareJobColumnsContent(response1, expected_col, columnsHeadContent,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		test.log(LogStatus.INFO,
				"deleting the created job columns in org: " + organizationType + " with the valid Token");

		spogJobServer.deleteJobColumnsForLoggedInUser(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
	}

	// update the user defined job columns with non exist column_Id(00D00003)
	@Test(dataProvider = "update_job_Column_Invalid")
	public void updateJobColumnsForLoggedInUser_404_NonExist_columnId(String organizationType, String validToken,
			String[] sort, String[] filter, String[] visible, String[] orderId, int userInput) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		test.assignAuthor("");

		spogJobServer.setToken(validToken);

		// update job columns

		ArrayList<HashMap<String, Object>> expected_columns = new ArrayList<>();

		HashMap<String, Object> temp = new HashMap<>();

		test.log(LogStatus.INFO, "composing the job columns in org: " + organizationType);

		for (int i = 0; i < 1; i++) {
			int index1 = gen_random_index(sort);

			int index2 = gen_random_index(filter);

			int index3 = gen_random_index(visible);

			String Random_column_id = UUID.randomUUID().toString();

			temp = spogJobServer.composejob_Column(Random_column_id, sort[index1], filter[index2], visible[index3],
					orderId[i]);

			expected_columns.add(temp);
		}

		test.log(LogStatus.INFO, "Update the job columns in organization with random column id ");

		Response response1 = spogJobServer.updateJobColumnsForLoggedInUser(validToken, expected_columns, test);

		test.log(LogStatus.INFO, "Comparing the job columns in organization with Response ");

		spogJobServer.compareJobColumnsContent(response1, expected_columns, columnsHeadContent,
				SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.COLUMN_ID_DOESNOT_EXIST, test);

		test.log(LogStatus.INFO,
				"deleting the created job columns in org: " + organizationType + " with the valid Token");

		spogJobServer.deleteJobColumnsForLoggedInUser(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

	}

/*	// update the user defined job columns with same order_Id, same
	// column_id(00D00001)
	@Test(dataProvider = "update_job_Column_Invalid")
	public void updateJobColumnsForLoggedInUser_200_AlreadyExist__ColumnId_orderId(String organizationType,
			String validToken, String[] sort, String[] filter, String[] visible, String[] orderId, int userInput) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		test.assignAuthor("");

		spogJobServer.setToken(validToken);

		// update job columns

		ArrayList<HashMap<String, Object>> expected_columns = new ArrayList<>();

		HashMap<String, Object> temp = new HashMap<>();

		if (organizationType.equalsIgnoreCase("msp")) {
			columnsHeadContent = MSPColumnsHeadContent;
			columnIdList = MSPColumnIdList;
		}

		test.log(LogStatus.INFO, "Composing the job columns in organization  ");

		for (int i = 0; i < userInput; i++) {
			int index1 = gen_random_index(sort);

			int index2 = gen_random_index(filter);

			int index3 = gen_random_index(visible);

			temp = spogJobServer.composejob_Column(columnIdList.get(1), sort[index1], filter[index2], visible[index3],
					orderId[1]);

			expected_columns.add(temp);
		}

		test.log(LogStatus.INFO, "Update the job columns in organization with_AlreadyExist_ColumnId_OrderId ");

		Response response = spogJobServer.updateJobColumnsForLoggedInUser(validToken, expected_columns, test);

		test.log(LogStatus.INFO, "Comparing the job columns in organization with Response ");

		spogJobServer.compareJobColumnsContent(response, expected_columns, columnsHeadContent,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		test.log(LogStatus.INFO,
				"deleting the created job columns in org: " + organizationType + " with the valid Token");

		spogJobServer.deleteJobColumnsForLoggedInUser(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

	}*/

	// update the user defined job columns with same order_Id(00D00005)
	@Test(dataProvider = "update_job_Column_Invalid")
	public void updateJobColumnsForLoggedInUser_200_AlreadyExist_orderId(String organizationType, String validToken,
			String[] sort, String[] filter, String[] visible, String[] orderId, int userInput) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		test.assignAuthor("");

		spogJobServer.setToken(validToken);

		if (organizationType.equalsIgnoreCase("msp")) {
			columnsHeadContent = MSPColumnsHeadContent;
			columnIdList = MSPColumnIdList;
		}

		// update job columns

		ArrayList<HashMap<String, Object>> expected_columns = new ArrayList<>();

		HashMap<String, Object> temp = new HashMap<>();

		ArrayList<HashMap<String, Object>> expected_col = new ArrayList<>();

		HashMap<String, Object> temp1 = new HashMap<>();

		test.log(LogStatus.INFO, "composing the job columns in org: " + organizationType);

		for (int i = 0; i < userInput; i++) {
			int index1 = gen_random_index(sort);

			int index2 = gen_random_index(filter);

			int index3 = gen_random_index(visible);

			if (i == 0) {
				temp1 = spogJobServer.composejob_Column(columnIdList.get(i), sort[index1], filter[index2],
						visible[index3], orderId[1]);

				expected_col.add(temp1);

			}

			temp = spogJobServer.composejob_Column(columnIdList.get(i), sort[index1], filter[index2], visible[index3],
					orderId[1]);

			expected_columns.add(temp);
		}
		test.log(LogStatus.INFO, "Update the job columns in org: " + organizationType + "AlreadyExist_orderId");

		Response response = spogJobServer.updateJobColumnsForLoggedInUser(validToken, expected_columns, test);

		test.log(LogStatus.INFO, "Comparing the job columns in organization with Response ");

		spogJobServer.compareJobColumnsContent(response, expected_columns, columnsHeadContent,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		test.log(LogStatus.INFO,
				"deleting the created job columns in org: " + organizationType + " with the valid Token");

		spogJobServer.deleteJobColumnsForLoggedInUser(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

	}

	// update the user defined job columns with in bound, out bound
	// order_Id(0,-ve)
	@Test(dataProvider = "update_job_Column_Invalid")
	public void updateJobColumnsForLoggedInUser_400_orderId_bounds(String organizationType, String validToken,
			String[] sort, String[] filter, String[] visible, String[] orderId, int userInput) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		test.assignAuthor("");

		spogJobServer.setToken(validToken);

		// update job columns

		ArrayList<HashMap<String, Object>> expected_columns = new ArrayList<>();

		HashMap<String, Object> temp = new HashMap<>();

		test.log(LogStatus.INFO, "Composing the job columns in organization  ");

		String[] Order = { "0", "-1" };

		for (int i = 0; i < userInput; i++) {
			int index1 = gen_random_index(sort);

			int index2 = gen_random_index(filter);

			int index3 = gen_random_index(visible);

			int index4 = gen_random_index(Order);

			temp = spogJobServer.composejob_Column(columnIdList.get(i), sort[index1], filter[index2], visible[index3],
					Order[index4]);

			expected_columns.add(temp);
		}
		test.log(LogStatus.INFO, "update the job columns in org: " + organizationType + "ORDER_ID lessthan 1");

		Response response = spogJobServer.updateJobColumnsForLoggedInUser(validToken, expected_columns, test);

		test.log(LogStatus.INFO, "Comparing the job columns in organization with Response ");

		spogJobServer.compareJobColumnsContent(response, expected_columns, columnsHeadContent,
				SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ORDER_ID_ATLEAST_1, test);

		test.log(LogStatus.INFO,
				"deleting the created job columns in org: " + organizationType + " with the valid Token");

		spogJobServer.deleteJobColumnsForLoggedInUser(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

	}

	// update the user defined job columns with in bound, out bound
	// order_Id(null)
	@Test(dataProvider = "update_job_Column_Invalid")
	public void updateJobColumnsForLoggedInUser_200_orderId_null(String organizationType, String validToken,
			String[] sort, String[] filter, String[] visible, String[] orderId, int userInput) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		test.assignAuthor("");

		spogJobServer.setToken(validToken);

		if (organizationType.equalsIgnoreCase("msp")) {
			columnsHeadContent = MSPColumnsHeadContent;
			columnIdList = MSPColumnIdList;
		}

		// update job columns

		ArrayList<HashMap<String, Object>> expected_columns = new ArrayList<>();

		HashMap<String, Object> temp = new HashMap<>();

		test.log(LogStatus.INFO, "composing the job columns in org: " + organizationType);

		String[] Order = { "0", "11", "", "-1" };

		for (int i = 0; i < 1; i++) {
			int index1 = gen_random_index(sort);

			int index2 = gen_random_index(filter);

			int index3 = gen_random_index(visible);

			temp = spogJobServer.composejob_Column(columnIdList.get(i), sort[index1], filter[index2], visible[index3],
					Order[2]);

			expected_columns.add(temp);
		}

		test.log(LogStatus.INFO, "update the job columns in org: " + organizationType + " with ORDER_ID  null");

		Response response2 = spogJobServer.updateJobColumnsForLoggedInUser(validToken, expected_columns, test);

		test.log(LogStatus.INFO, "Comparing the job columns in organization with Response ");

		spogJobServer.compareJobColumnsContent(response2, expected_columns, columnsHeadContent,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		test.log(LogStatus.INFO,
				"deleting the created job columns in org: " + organizationType + " with the valid Token");

		spogJobServer.deleteJobColumnsForLoggedInUser(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
	}

	// update the user defined job columns with in bound, out bound
	// order_Id(>Max)

	@Test(dataProvider = "update_job_Column_Invalid")
	public void updateJobColumnsForLoggedInUser_400_orderId_greaterthan_Max(String organizationType, String validToken,
			String[] sort, String[] filter, String[] visible, String[] orderId, int userInput) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		test.assignAuthor("");

		spogJobServer.setToken(validToken);

		// update job columns

		ArrayList<HashMap<String, Object>> expected_columns = new ArrayList<>();

		HashMap<String, Object> temp = new HashMap<>();

		test.log(LogStatus.INFO, "composing the job columns in org: " + organizationType);

		String[] Order = { "0", "11", "", "-1" };

		for (int i = 0; i < userInput; i++) {
			int index1 = gen_random_index(sort);

			int index2 = gen_random_index(filter);

			int index3 = gen_random_index(visible);

			temp = spogJobServer.composejob_Column(columnIdList.get(i), sort[index1], filter[index2], visible[index3],
					Order[1]);

			expected_columns.add(temp);
		}

		test.log(LogStatus.INFO,
				"update the job columns in org: " + organizationType + " with ORDER_ID greater than max");

		Response response = spogJobServer.updateJobColumnsForLoggedInUser(validToken, expected_columns, test);

		test.log(LogStatus.INFO, "Comparing the job columns in organization with Response ");

		spogJobServer.compareJobColumnsContent(response, expected_columns, columnsHeadContent,
				SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ORDER_ID_LESSTHAN_MAX, test);

		test.log(LogStatus.INFO,
				"deleting the created job columns in org: " + organizationType + " with the valid Token");

		spogJobServer.deleteJobColumnsForLoggedInUser(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

	}

	@DataProvider(name = "update_job_Column_msp_admin_account")
	public final Object[][] update_job_Column_msp_admin_account() {

		return new Object[][] { { "suborg_mspadminaccount", ti.normal_msp_org1_msp_accountadmin1_token,
				new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
				new String[] { "true", "false", "none" }, new String[] { "4", "7", "3", "2", "5", "1", "6" }, 4 }

		};

	}

	@Test(dataProvider = "update_job_Column_msp_admin_account")
	public void updateJobColumnsForLoggedInUser_msp_admin_account(String organizationType, String validToken,
			String[] sort, String[] filter, String[] visible, String[] orderId, int userInput) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		test.assignAuthor("");

		spogJobServer.setToken(validToken);

		// update job columns

		ArrayList<HashMap<String, Object>> expected_columns = new ArrayList<>();

		HashMap<String, Object> temp = new HashMap<>();

		test.log(LogStatus.INFO, "composing the job columns in org: " + organizationType);

		String[] Order = { "0", "11", "", "-1" };

		for (int i = 0; i < userInput; i++) {
			int index1 = gen_random_index(sort);

			int index2 = gen_random_index(filter);

			int index3 = gen_random_index(visible);

			temp = spogJobServer.composejob_Column(columnIdList.get(i), sort[index1], filter[index2], visible[index3],
					"1");

			expected_columns.add(temp);
		}

		test.log(LogStatus.INFO, "update the job columns in org: " + organizationType + " with msp_admin_account");

		Response response = spogJobServer.updateJobColumnsForLoggedInUser(validToken, expected_columns, test);

		test.log(LogStatus.INFO, "Comparing the job columns in organization with Response ");

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
