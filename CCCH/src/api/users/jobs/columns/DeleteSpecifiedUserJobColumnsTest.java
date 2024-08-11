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

public class DeleteSpecifiedUserJobColumnsTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private SPOGJobServer spogJobServer;

	private ExtentTest test;
	private TestOrgInfo ti;

	ArrayList<String> columnIdList = new ArrayList<String>();
	ArrayList<HashMap<String, Object>> columnsHeadContent = new ArrayList<HashMap<String, Object>>();
	ArrayList<String> MSPColumnIdList = new ArrayList<String>();
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

	@DataProvider(name = "delete_job_Column_valid")
	public final Object[][] deleteJobColumnValidParams() {

		return new Object[][] {
				{ "direct-csr", ti.csr_token, ti.direct_org1_user1_id, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "4", "1", "3", "2", }, 4 },
				{ "msp-csr", ti.csr_token, ti.normal_msp_org1_user1_id, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "7", "8", "9", "1", "2", "3", "4", "5", "6" }, 5 },
				{ "suborg-csr", ti.csr_token, ti.normal_msp1_suborg1_user1_id, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9" }, 6 },
				{ "suborgmsp-csr", ti.csr_token, ti.normal_msp1_suborg1_user1_id,
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" },
						new String[] { "9", "3", "4", "5", "6", "1", "2", "7", "8" }, 3 },
				{ "direct", ti.direct_org1_user1_token, ti.direct_org1_user1_id,
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "4", "1", "3", "2", }, 2 },
				{ "msp", ti.normal_msp_org2_user1_token, ti.normal_msp_org2_user1_id,
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" },
						new String[] { "5", "6", "7", "8", "9", "1", "2", "3", "4" }, 4 },
				{ "suborg", ti.normal_msp1_suborg2_user1_token, ti.normal_msp1_suborg2_user1_id,
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" },
						new String[] { "7", "8", "9", "1", "2", "3", "4", "5", "6" }, 5 },
				{ "suborgmsptoken", ti.normal_msp_org1_user1_token, ti.normal_msp1_suborg2_user1_id,
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" },
						new String[] { "5", "6", "7", "8", "9", "1", "2", "3", "4" }, 7 },
				// csr read only cases
				{ "csrreadonly", ti.csr_readonly_token, ti.csr_readonly_admin_user_id,
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "4", "1", "3", "2", }, 4 },
				// 3 tier cases
				{ "rootmsp", ti.root_msp_org2_user1_token, ti.root_msp_org2_user1_id,
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" },
						new String[] { "5", "6", "7", "8", "9", "1", "2", "3", "4" }, 4 },
				{ "rootsuborg", ti.root_msp1_suborg2_user1_token, ti.root_msp1_suborg2_user1_id,
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" },
						new String[] { "7", "8", "9", "1", "2", "3", "4", "5", "6" }, 5 },
				{ "rootsub-root", ti.root_msp_org1_user1_token, ti.root_msp1_suborg2_user1_id,
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" },
						new String[] { "5", "6", "7", "8", "9", "1", "2", "3", "4" }, 7 },
				{ "submsp", ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" },
						new String[] { "7", "8", "9", "1", "2", "3", "4", "5", "6" }, 5 },
				{ "submsp_sub", ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" },
						new String[] { "7", "8", "9", "1", "2", "3", "4", "5", "6" }, 5 },

				{ "submsp_sub-submsp", ti.root_msp1_submsp1_user1_token, ti.msp1_submsp1_suborg1_user1_id,
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" },
						new String[] { "7", "8", "9", "1", "2", "3", "4", "5", "6" }, 5 }, };

	}

	// Delete job columns with valid Jwt token

	@Test(dataProvider = "delete_job_Column_valid")
	public void DeleteJobColumnsForSpecifiedUser_200(String organizationType, String validToken, String user_Id,
			String[] sort, String[] filter, String[] visible, String[] orderId, int userInput) {

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

		test.log(LogStatus.INFO, "Creating the job columns in org: " + organizationType);

		Response response = spogJobServer.createJobColumnsForSpecifiedUser(user_Id, validToken, expected_columns, test);

		test.log(LogStatus.INFO, "Comparing the job columns in organization with Response ");

		spogJobServer.compareJobColumnsContent(response, expected_columns, columnsHeadContent,
				SpogConstants.SUCCESS_POST, null, test);

		test.log(LogStatus.INFO, "deleting the created job columns in org: " + organizationType);

		spogJobServer.deleteJobcolumnsforspecifeduser(user_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);

	}

	@DataProvider(name = "delete_job_Column_Invalid")
	public final Object[][] deleteJobColumnInValidParams() {

		return new Object[][] {
				{ "MSP", ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "4", "7", "3", "2", }, 4 },
				{ "suborgundermsp", ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp1_suborg1_user1_id,
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "4", "7", "3", "2", }, 4 },
				{ "csrreadonly", ti.csr_readonly_token, ti.csr_readonly_admin_user_id,
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "4", "7", "3", "2", }, 4 }

		};

	}

	// Delete job columns with Invalid Jwt token
	@Test(dataProvider = "delete_job_Column_Invalid")
	public void DeleteJobColumnsForSpecifiedUser_401(String organizationType, String validToken, String user_Id,
			String[] sort, String[] filter, String[] visible, String[] orderId, int userInput) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		test.assignAuthor("");

		spogJobServer.setToken(validToken);

		ArrayList<HashMap<String, Object>> expected_columns = new ArrayList<>();

		HashMap<String, Object> temp = new HashMap<>();

		test.log(LogStatus.INFO, "composing the job columns in org: " + organizationType);

		for (int i = 0; i < 2; i++) {
			int index1 = gen_random_index(sort);

			int index2 = gen_random_index(filter);

			int index3 = gen_random_index(visible);

			temp = spogJobServer.composejob_Column(columnIdList.get(i + 3), sort[index1], filter[index2],
					visible[index3], orderId[i]);

			expected_columns.add(temp);
		}

		test.log(LogStatus.INFO, "Creating the job columns in org: " + organizationType);

		Response response = spogJobServer.createJobColumnsForSpecifiedUser(user_Id, validToken, expected_columns, test);

		test.log(LogStatus.INFO, "Comparing the job columns in organization with Response ");

		spogJobServer.compareJobColumnsContent(response, expected_columns, columnsHeadContent,
				SpogConstants.SUCCESS_POST, null, test);

		String Not_valid_Token = validToken + "Abc";

		test.log(LogStatus.INFO, "deleting the job columns in org: with the InValid Token");

		spogJobServer.deleteJobcolumnsforspecifeduser(user_Id, Not_valid_Token, SpogConstants.NOT_LOGGED_IN,
				SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);

		String MissingToken = "";

		test.log(LogStatus.INFO, "deleting the job columns in org: with the MissingToken ");

		spogJobServer.deleteJobcolumnsforspecifeduser(user_Id, MissingToken, SpogConstants.NOT_LOGGED_IN,
				SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);

		test.log(LogStatus.INFO,
				"deleting the created job columns in org: " + organizationType + " with the valid Token");

		spogJobServer.deleteJobcolumnsforspecifeduser(user_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);

	}

	// Delete job columns with Jwt token, random userid
	@Test(dataProvider = "delete_job_Column_Invalid")
	public void DeleteJobColumnsForSpecifiedUser_404(String organizationType, String validToken, String user_Id,
			String[] sort, String[] filter, String[] visible, String[] orderId, int userInput) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		test.assignAuthor("");

		spogJobServer.setToken(validToken);

		test.log(LogStatus.INFO, "deleting job columns in org: " + organizationType + "without creating");

		spogJobServer.deleteJobcolumnsforspecifeduser(user_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);

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

		Response response = spogJobServer.createJobColumnsForSpecifiedUser(user_Id, validToken, expected_columns, test);

		test.log(LogStatus.INFO, "Comparing the job columns in org: with the Response");

		spogJobServer.compareJobColumnsContent(response, expected_columns, columnsHeadContent,
				SpogConstants.SUCCESS_POST, null, test);

		String Random_user_id = UUID.randomUUID().toString();

		test.log(LogStatus.INFO, "deleting the job columns in organization with the random user_id");

		spogJobServer.deleteJobcolumnsforspecifeduser(Random_user_id, validToken, SpogConstants.RESOURCE_NOT_EXIST,
				SpogMessageCode.USER_ID_DOESNOT_EXIST, test);

		test.log(LogStatus.INFO,
				"deleting the created job columns in org: " + organizationType + " with the valid Token");

		spogJobServer.deleteJobcolumnsforspecifeduser(user_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);

	}

	// Delete job columns with Jwt token, Invalid userid

	@Test(dataProvider = "delete_job_Column_Invalid")
	public void DeleteJobColumnsForSpecifiedUser_400(String organizationType, String validToken, String user_Id,
			String[] sort, String[] filter, String[] visible, String[] orderId, int userInput) {

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

		Response response = spogJobServer.createJobColumnsForSpecifiedUser(user_Id, validToken, expected_columns, test);

		test.log(LogStatus.INFO, "Comparing the job columns in org: with the Response");

		spogJobServer.compareJobColumnsContent(response, expected_columns, columnsHeadContent,
				SpogConstants.SUCCESS_POST, null, test);

		String does_not_exist_user_id = "abc";

		test.log(LogStatus.INFO, "deleting the job columns in organization with the non existing user_id");

		spogJobServer.deleteJobcolumnsforspecifeduser(does_not_exist_user_id, validToken,
				SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_USER_ID_NOT_UUID, test);

		test.log(LogStatus.INFO,
				"deleting the created job columns in org: " + organizationType + " with the valid Token");

		spogJobServer.deleteJobcolumnsforspecifeduser(user_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);

	}

	@DataProvider(name = "deletejobColumn_403_insufficeintpermission", parallel = false)
	public final Object[][] deletejobColumn_403_insufficeintpermission() {
		return new Object[][] {
				{ "direct-msp", ti.direct_org1_user1_token, ti.direct_org1_user1_id, ti.normal_msp_org2_user1_token,
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "5", "1", "3", "2", }, 3,
						ti.direct_org1_user1_id },
				{ "direct-suborg", ti.direct_org1_user1_token, ti.direct_org1_user1_id,
						ti.normal_msp1_suborg2_user1_token, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "4", "1", "5", "3", }, 4, ti.direct_org1_user1_id },
				{ "msp-direct", ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id, ti.direct_org1_user1_token,
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "1", "5", "3", "4", "2", "6", "7", "9", "8" },
						2, ti.normal_msp_org1_user1_id },
				{ "msp-mspb", ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,
						ti.normal_msp_org2_user1_token, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "7", "2", "9", "4", "6", "5", "1", "8", "3" }, 5, ti.normal_msp_org1_user1_id },
				{ "msp-suborg", ti.normal_msp_org2_user1_token, ti.normal_msp_org1_user1_id,
						ti.normal_msp1_suborg2_user1_token, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "1", "5", "3", "4", "2", "6", "7", "9", "8" }, 8, ti.normal_msp_org1_user1_id },
				{ "suborg-direct", ti.normal_msp1_suborg2_user1_token, ti.normal_msp1_suborg2_user1_id,
						ti.direct_org1_user1_token, new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9" },
						1, ti.normal_msp1_suborg2_user1_id },
				{ "suborg-msp", ti.normal_msp1_suborg2_user1_token, ti.normal_msp1_suborg2_user1_id,
						ti.normal_msp_org2_user1_token, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "7", "2", "9", "4", "6", "5", "1", "8", "3" }, 3,
						ti.normal_msp1_suborg2_user1_id },
				{ "suborg-suborgb", ti.normal_msp1_suborg2_user1_token, ti.normal_msp1_suborg2_user1_id,
						ti.normal_msp1_suborg1_user1_token, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "3", "6", "1", "4", "5", "2", "9", "8", "7" }, 7,
						ti.normal_msp1_suborg2_user1_id },
				{ "csr-direct", ti.direct_org1_user1_token, ti.csr_admin_user_id, ti.direct_org1_user1_token,
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "4", "1", "5", "3", }, 4,
						ti.direct_org1_user1_id },
				{ "csr-msp", ti.normal_msp_org2_user1_token, ti.csr_admin_user_id, ti.normal_msp_org2_user1_token,
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "1", "5", "3", "4", "2", "6", "7", "9", "8" },
						2, ti.normal_msp_org1_user1_id },
				{ "csr-suborg", ti.normal_msp1_suborg2_user1_token, ti.csr_admin_user_id,
						ti.normal_msp1_suborg2_user1_token, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "1", "5", "3", "4", "2", "6", "7", "9", "8" }, 8,
						ti.normal_msp1_suborg2_user1_id },
				{ "csr-suborgmsp", ti.normal_msp_org2_user1_token, ti.csr_admin_user_id, ti.normal_msp_org2_user1_token,
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9" },
						1, ti.normal_msp_org1_user1_id },

				// csr read only cases
				{ "csrreadonly-direct", ti.direct_org1_user1_token, ti.direct_org1_user1_id, ti.csr_readonly_token,
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "4", "1", "5", "3", }, 4,
						ti.csr_readonly_admin_user_id },
				{ "csrreadonly-msp", ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id, ti.csr_readonly_token,
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "1", "5", "3", "4", "2", "6", "7", "9", "8" },
						2, ti.csr_readonly_admin_user_id },
				{ "csrreadonly-suborg", ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id,
						ti.csr_readonly_token, new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "1", "5", "3", "4", "2", "6", "7", "9", "8" },
						8, ti.csr_readonly_admin_user_id },
				{ "csrreadonly-suborgmspaccount", ti.normal_msp_org1_msp_accountadmin1_token,
						ti.normal_msp_org1_msp_accountadmin1_id, ti.csr_readonly_token,
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9" },
						1, ti.csr_readonly_admin_user_id },

				// 3 tier cases
				{ "rootmsp-direct", ti.root_msp_org1_user1_token, ti.root_msp_org1_user1_id, ti.direct_org1_user1_token,
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "1", "5", "3", "4", "2", "6", "7", "9", "8" },
						2, ti.root_msp_org1_user1_id },
				{ "rootmsp-rootmspb", ti.root_msp_org1_user1_token, ti.root_msp_org1_user1_id,
						ti.root_msp_org2_user1_token, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "7", "2", "9", "4", "6", "5", "1", "8", "3" }, 5, ti.root_msp_org1_user1_id },
				{ "rootmsp-submsp", ti.root_msp_org1_user1_token, ti.root_msp_org1_user1_id,
						ti.root_msp1_submsp1_user1_token, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "7", "2", "9", "4", "6", "5", "1", "8", "3" }, 5, ti.root_msp_org1_user1_id },
				{ "rootmsp-submsp_sub", ti.root_msp_org1_user1_token, ti.root_msp_org1_user1_id,
						ti.msp1_submsp1_suborg1_user1_token, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "7", "2", "9", "4", "6", "5", "1", "8", "3" }, 5, ti.root_msp_org1_user1_id },
				{ "rootmsp-rootsuborg", ti.root_msp_org2_user1_token, ti.root_msp_org1_user1_id,
						ti.root_msp1_suborg2_user1_token, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "1", "5", "3", "4", "2", "6", "7", "9", "8" }, 8, ti.root_msp_org1_user1_id },
				{ "rootsuborg-direct", ti.root_msp1_suborg2_user1_token, ti.root_msp1_suborg2_user1_id,
						ti.direct_org1_user1_token, new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9" },
						1, ti.root_msp1_suborg2_user1_id },
				{ "rootsuborg-rootmsp", ti.root_msp1_suborg2_user1_token, ti.root_msp1_suborg2_user1_id,
						ti.root_msp_org2_user1_token, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "7", "2", "9", "4", "6", "5", "1", "8", "3" }, 3,
						ti.root_msp1_suborg2_user1_id },
				{ "rootsuborg-rootsuborgb", ti.root_msp1_suborg2_user1_token, ti.root_msp1_suborg2_user1_id,
						ti.root_msp1_suborg1_user1_token, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "3", "6", "1", "4", "5", "2", "9", "8", "7" }, 7,
						ti.root_msp1_suborg2_user1_id },
				{ "submsp-submsp_sub", ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,
						ti.msp1_submsp1_suborg1_user1_token, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "7", "2", "9", "4", "6", "5", "1", "8", "3" }, 5,
						ti.root_msp1_submsp1_user1_id },
				{ "submsp-rootsub", ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,
						ti.root_msp1_suborg1_user1_token, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "7", "2", "9", "4", "6", "5", "1", "8", "3" }, 5,
						ti.root_msp1_submsp1_user1_id },
				{ "submsp-rootmaa", ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,
						ti.root_msp1_submsp1_account_admin_token, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "7", "2", "9", "4", "6", "5", "1", "8", "3" }, 5,
						ti.root_msp1_submsp1_user1_id },

				{ "submsp_sub-root", ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,
						ti.root_msp_org1_user1_token, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "7", "2", "9", "4", "6", "5", "1", "8", "3" }, 5,
						ti.msp1_submsp1_suborg1_user1_id },
				{ "submsp_sub-rootsub", ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,
						ti.root_msp1_suborg1_user1_token, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "7", "2", "9", "4", "6", "5", "1", "8", "3" }, 5,
						ti.msp1_submsp1_suborg1_user1_id },
				{ "rootmaa-submsp", ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_id,
						ti.root_msp1_submsp1_user1_token, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "7", "2", "9", "4", "6", "5", "1", "8", "3" }, 5,
						ti.root_msp_org1_msp_accountadmin1_id },
				{ "rootmaa-submsp_sub", ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_id,
						ti.msp1_submsp1_suborg1_user1_token, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "7", "2", "9", "4", "6", "5", "1", "8", "3" }, 5,
						ti.root_msp_org1_msp_accountadmin1_id },
				{ "submspmaa-submsp", ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp1_account_admin_1_id,
						ti.root_msp2_submsp2_user1_token, new String[] { "true", "false" },
						new String[] { "true", "false" }, new String[] { "true", "false" },
						new String[] { "7", "2", "9", "4", "6", "5", "1", "8", "3" }, 5,
						ti.root_msp1_submsp1_account_admin_1_id },

		};
	}

	// Delete job columns with other org Jwt token
	@Test(dataProvider = "deletejobColumn_403_insufficeintpermission")
	public void DeleteJobColumnsForSpecifiedUser_403_otherorgToken(String organizationType, String validToken,
			String other_user_Id, String otherorgToken, String[] sort, String[] filter, String[] visible,
			String[] orderId, int userInput, String valid_user_Id) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		test.assignAuthor("Ramya.Nagepalli");

		spogJobServer.setToken(validToken);

		int expectedstatuscode = SpogConstants.INSUFFICIENT_PERMISSIONS;

		SpogMessageCode expected = SpogMessageCode.RESOURCE_PERMISSION_DENY;

		test.log(LogStatus.INFO,
				"deleting the job columns in org: " + organizationType + " with the other organization Token");

		spogJobServer.deleteJobcolumnsforspecifeduser(other_user_Id, otherorgToken, expectedstatuscode, expected, test);

	}

	@DataProvider(name = "delete_job_Column_msp_admin_account")
	public final Object[][] delete_job_Column_msp_admin_account() {

		return new Object[][] {
				{ "suborg_mspadmin", ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id,
						new String[] { "true", "false", "none" }, new String[] { "true", "false", "none" },
						new String[] { "true", "false", "none" }, new String[] { "4", "7", "3", "2", }, 4 }

		};
	}

	@Test(dataProvider = "delete_job_Column_msp_admin_account")
	public void DeleteJobColumnsForSpecifiedUser_msp_admin_account(String organizationType, String validToken,
			String user_Id, String[] sort, String[] filter, String[] visible, String[] orderId, int userInput) {

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

		Response response = spogJobServer.createJobColumnsForSpecifiedUser(user_Id, validToken, expected_columns, test);

		test.log(LogStatus.INFO, "Comparing the job columns in org: with the Response");

		spogJobServer.compareJobColumnsContent(response, expected_columns, columnsHeadContent,
				SpogConstants.SUCCESS_POST, null, test);

		test.log(LogStatus.INFO,
				"deleting the job columns in organization with the ti.normal_msp_org1_msp_accountadmin1_token");

		spogJobServer.deleteJobcolumnsforspecifeduser(user_Id, ti.normal_msp_org1_msp_accountadmin1_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
	}

	@Test(dataProvider = "delete_job_Column_msp_admin_account")
	public void DeleteJobColumnsForSpecifiedUser_msp_admin_account_invalid(String organizationType, String validToken,
			String user_Id, String[] sort, String[] filter, String[] visible, String[] orderId, int userInput) {

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

		Response response = spogJobServer.createJobColumnsForSpecifiedUser(user_Id, validToken, expected_columns, test);

		test.log(LogStatus.INFO, "Comparing the job columns in org: with the Response");

		spogJobServer.compareJobColumnsContent(response, expected_columns, columnsHeadContent,
				SpogConstants.SUCCESS_POST, null, test);

		test.log(LogStatus.INFO,
				"deleting the job columns in organization with the other ti.normal_msp_org1_msp_accountadmin1_token");

		spogJobServer.deleteJobcolumnsforspecifeduser(user_Id, ti.normal_msp_org1_msp_accountadmin2_token,
				SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY, test);

		test.log(LogStatus.INFO,
				"deleting the job columns in organization with the ti.normal_msp_org1_msp_accountadmin1_token");

		spogJobServer.deleteJobcolumnsforspecifeduser(user_Id, ti.normal_msp_org1_msp_accountadmin1_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
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
