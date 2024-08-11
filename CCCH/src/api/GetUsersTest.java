package api;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
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

import Constants.ErrorCode;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.Org4SPOGServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import dataPreparation.JsonPreparation;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;

public class GetUsersTest  extends base.prepare.Is4Org{

	private SPOGServer spogServer;
	private JsonPreparation jp;
	private TestOrgInfo ti;
	private ExtentTest test;
	protected ExtentReports rep;

	int passedcases = 0;
	int failedcases = 0;
	int skippedcases = 0;


	String csr_token, csr_org_id;
	String prefix = RandomStringUtils.randomAlphabetic(4);
	String userEmail =prefix+"eswari.sykam100@gmail.com";
	@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runningMachine,
			String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		jp = new JsonPreparation();
		rep = ExtentManager.getInstance(getClass().getName(), logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Sykam, Naga Malleswari";
		ti = new TestOrgInfo(spogServer, test);
		Nooftest = 0;
		// Used for creating a build number with dateformat
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date = new java.util.Date();
		this.buildVersion = buildVersion + "_" + dateFormater.format(date);

		BQName = this.getClass().getSimpleName();
		this.runningMachine = runningMachine;
		System.out.println("The value of hit is " + count1.isstarttimehit());
		
		ti = new TestOrgInfo(spogServer, test);
		

		/*if (count1.isstarttimehit() == 0) {
			System.out.println("into creation time");
			creationTime = System.currentTimeMillis();
			count1.setcreationtime(creationTime);

			try {
				bqdb1.updateTable(BQName, this.runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0",
						String.valueOf(Nooftest), count1.getcreationtime(), "InProgress",
						author + " and Rest server is " + baseURI.split("//")[1]);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/
		
	}



	@DataProvider(name = "create_Users_Info")
	public final Object[][] createlogcoloumnsValidParams() {

		return new Object[][] {
			// Alert Email Recipient columns
			{ "Create users for direct organization", ti.direct_org1_user1_token,ti.direct_org1_id,"FirstName","LastName",userEmail,spogServer.GetLoggedinUser_RoleID(),"en","Mclaren@2016",2},
			{ "Create users for msp organization", ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,"FirstName","LastName",userEmail,spogServer.GetLoggedinUser_RoleID(),"en","Mclaren@2016",2},
			{ "Create users for msp account admin organization", ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,"FirstName","LastName",userEmail,SpogConstants.MSP_ACCOUNT_ADMIN,"en","Mclaren@2016",2},
			{ "Create users for Customer account of msp ", ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_id,"FirstName","LastName",userEmail,spogServer.GetLoggedinUser_RoleID(),"en","Mclaren@2016",2},

		};
	}

	@Test(dataProvider = "create_Users_Info", enabled = true)
	public void createColumnsForLoggedInUser_valid(String testCase,
			String token,
			String ogranization_id,
			String firstName,
			String lastName,
			String email,
			String role_id,
			String preference_language,
			String password,
			int noofusers
			) {

		test = ExtentManager.getNewTest(testCase);
		test.assignAuthor("Sykam, Naga Malleswari");

		ArrayList<HashMap<String,Object>>expected_usersInfo=createUsersInfo(token,firstName,lastName, email,role_id,ogranization_id,preference_language,password,noofusers);
		test.log(LogStatus.INFO, "Get all users and validate the users");
		spogServer.getUsersForOrganizationWithCheck(token, ogranization_id,role_id, expected_usersInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		test.log(LogStatus.INFO, "Delete users of logged in user");
		String user_id=spogServer.GetLoggedinUser_UserID();
		deleteUsers(token, user_id, ogranization_id);
	}


	@DataProvider(name = "insufficientTestcases_Info")
	public final Object[][] getUserf_InsufficientInfo() {

		return new Object[][] {
			// Alert Email Recipient columns
			{ "Get direct organization users with the another direct user token ", ti.direct_org1_user1_token,ti.direct_org2_user1_token,ti.direct_org1_id,"FirstName","LastName",userEmail,spogServer.GetLoggedinUser_RoleID(),null,"Mclaren@2016",2},
			{ "Get direct organization users with the normal msp user token ", ti.direct_org1_user1_token,ti.normal_msp_org1_user1_token,ti.direct_org1_id,"FirstName","LastName",userEmail,spogServer.GetLoggedinUser_RoleID(),null,"Mclaren@2016",2},
			{ "Get direct organization users with the normal msp account admin user token ", ti.direct_org1_user1_token,ti.normal_msp_org1_msp_accountadmin1_token,ti.direct_org1_id,"FirstName","LastName",userEmail,spogServer.GetLoggedinUser_RoleID(),null,"Mclaren@2016",2},
			{ "Get direct organization users with the customer accoutn of normal msp user token ", ti.direct_org1_user1_token,ti.normal_msp1_suborg1_user1_token,ti.direct_org1_id,"FirstName","LastName",userEmail,spogServer.GetLoggedinUser_RoleID(),null,"Mclaren@2016",2},
			{ "Get direct organization users with the root msp user token ", ti.direct_org1_user1_token,ti.root_msp_org1_user1_token,ti.direct_org1_id,"FirstName","LastName",userEmail,spogServer.GetLoggedinUser_RoleID(),null,"Mclaren@2016",2},
			{ "Get direct organization users with the root msp account admin user token ", ti.direct_org1_user1_token,ti.root_msp_org1_msp_accountadmin1_token,ti.direct_org1_id,"FirstName","LastName",userEmail,spogServer.GetLoggedinUser_RoleID(),null,"Mclaren@2016",2},
			{ "Get direct organization users with the customer account of root msp", ti.direct_org1_user1_token,ti.root_msp1_suborg1_user1_token,ti.direct_org1_id,"FirstName","LastName",userEmail,spogServer.GetLoggedinUser_RoleID(),null,"Mclaren@2016",2},
			{ "Get direct organization users with the submsp user token", ti.direct_org1_user1_token,ti.root_msp1_submsp1_user1_token,ti.direct_org1_id,"FirstName","LastName",userEmail,spogServer.GetLoggedinUser_RoleID(),null,"Mclaren@2016",2},
			{ "Get direct organization users with the submsp account admin user token", ti.direct_org1_user1_token,ti.root_msp1_submsp1_account_admin_token,ti.direct_org1_id,"FirstName","LastName",userEmail,spogServer.GetLoggedinUser_RoleID(),null,"Mclaren@2016",2},
			{ "Get direct organization users with the customer account of submsp", ti.direct_org1_user1_token,ti.msp1_submsp1_suborg2_user1_token,ti.direct_org1_id,"FirstName","LastName",userEmail,spogServer.GetLoggedinUser_RoleID(),null,"Mclaren@2016",2},

			{ "Get Normal msp organization users with the another direct user token ", ti.normal_msp_org1_user1_token,ti.direct_org2_user1_token,ti.normal_msp_org1_id,"FirstName","LastName",userEmail,spogServer.GetLoggedinUser_RoleID(),null,"Mclaren@2016",2},
			{ "Get Normal msp organization users with the normal msp user token ", ti.normal_msp_org1_user1_token,ti.normal_msp_org2_user1_token,ti.normal_msp_org1_id,"FirstName","LastName",userEmail,spogServer.GetLoggedinUser_RoleID(),null,"Mclaren@2016",2},
			{ "Get Normal msp organization users with the normal msp account admin user token ", ti.normal_msp_org1_user1_token,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_id,"FirstName","LastName",userEmail,spogServer.GetLoggedinUser_RoleID(),null,"Mclaren@2016",2},
			{ "Get Normal msp organization users with the customer accoutn of normal msp user token ", ti.normal_msp_org1_user1_token,ti.normal_msp1_suborg1_user1_token,ti.normal_msp_org1_id,"FirstName","LastName",userEmail,spogServer.GetLoggedinUser_RoleID(),null,"Mclaren@2016",2},
			{ "Get Normal msp organization users with the root msp user token ", ti.normal_msp_org1_user1_token,ti.root_msp_org1_user1_token,ti.normal_msp_org1_id,"FirstName","LastName",userEmail,spogServer.GetLoggedinUser_RoleID(),null,"Mclaren@2016",2},
			{ "Get Normal msp organization users with the root msp account admin user token ", ti.normal_msp_org1_user1_token,ti.root_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_id,"FirstName","LastName",userEmail,spogServer.GetLoggedinUser_RoleID(),null,"Mclaren@2016",2},
			{ "Get Normal msp organization users with the customer account of root msp", ti.normal_msp_org1_user1_token,ti.root_msp1_suborg1_user1_token,ti.normal_msp_org1_id,"FirstName","LastName",userEmail,spogServer.GetLoggedinUser_RoleID(),null,"Mclaren@2016",2},
			{ "Get Normal msp organization users with the submsp user token", ti.normal_msp_org1_user1_token,ti.root_msp1_submsp1_user1_token,ti.normal_msp_org1_id,"FirstName","LastName",userEmail,spogServer.GetLoggedinUser_RoleID(),null,"Mclaren@2016",2},
			{ "Get Normal msp organization users with the submsp account admin user token", ti.normal_msp_org1_user1_token,ti.root_msp1_submsp1_account_admin_token,ti.normal_msp_org1_id,"FirstName","LastName",userEmail,spogServer.GetLoggedinUser_RoleID(),null,"Mclaren@2016",2},
			{ "Get Normal msp organization users with the customer account of submsp", ti.normal_msp_org1_user1_token,ti.msp1_submsp1_suborg2_user1_token,ti.normal_msp_org1_id,"FirstName","LastName",userEmail,spogServer.GetLoggedinUser_RoleID(),null,"Mclaren@2016",2},


			{ "Get Normal msp account admin organization users with the another direct user token ", ti.normal_msp_org1_msp_accountadmin1_token,ti.direct_org2_user1_token,ti.normal_msp_org1_id,"FirstName","LastName",userEmail,spogServer.GetLoggedinUser_RoleID(),null,"Mclaren@2016",2},
			{ "Get Normal msp account admin  organization users with the normal msp user token ", ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org2_user1_token,ti.normal_msp_org1_id,"FirstName","LastName",userEmail,spogServer.GetLoggedinUser_RoleID(),null,"Mclaren@2016",2},
			{ "Get Normal msp account admin  organization users with the normal msp account admin user token ", ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org2_msp_accountadmin1_token,ti.normal_msp_org1_id,"FirstName","LastName",userEmail,spogServer.GetLoggedinUser_RoleID(),null,"Mclaren@2016",2},
			{ "Get Normal msp account admin  organization users with the customer accoutn of normal msp user token ", ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp1_suborg1_user1_token,ti.normal_msp_org1_id,"FirstName","LastName",userEmail,spogServer.GetLoggedinUser_RoleID(),null,"Mclaren@2016",2},
			{ "Get Normal msp account admin  organization users with the root msp user token ", ti.normal_msp_org1_msp_accountadmin1_token,ti.root_msp_org1_user1_token,ti.normal_msp_org1_id,"FirstName","LastName",userEmail,spogServer.GetLoggedinUser_RoleID(),null,"Mclaren@2016",2},
			{ "Get Normal msp account admin  organization users with the root msp account admin user token ", ti.normal_msp_org1_msp_accountadmin1_token,ti.root_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_id,"FirstName","LastName",userEmail,spogServer.GetLoggedinUser_RoleID(),null,"Mclaren@2016",2},
			{ "Get Normal msp account admin  organization users with the customer account of root msp", ti.normal_msp_org1_msp_accountadmin1_token,ti.root_msp1_suborg1_user1_token,ti.normal_msp_org1_id,"FirstName","LastName",userEmail,spogServer.GetLoggedinUser_RoleID(),null,"Mclaren@2016",2},
			{ "Get Normal msp account admin  organization users with the submsp user token", ti.normal_msp_org1_msp_accountadmin1_token,ti.root_msp1_submsp1_user1_token,ti.normal_msp_org1_id,"FirstName","LastName",userEmail,spogServer.GetLoggedinUser_RoleID(),null,"Mclaren@2016",2},
			{ "Get Normal msp account admin  organization users with the submsp account admin user token", ti.normal_msp_org1_msp_accountadmin1_token,ti.root_msp1_submsp1_account_admin_token,ti.normal_msp_org1_id,"FirstName","LastName",userEmail,spogServer.GetLoggedinUser_RoleID(),null,"Mclaren@2016",2},
			{ "Get Normal msp account admin  organization users with the customer account of submsp", ti.normal_msp_org1_msp_accountadmin1_token,ti.msp1_submsp1_suborg2_user1_token,ti.normal_msp_org1_id,"FirstName","LastName",userEmail,spogServer.GetLoggedinUser_RoleID(),null,"Mclaren@2016",2},

			{ "Get customer account of normal msp users with the another direct user token ", ti.normal_msp1_suborg1_user1_token,ti.direct_org2_user1_token,ti.normal_msp1_suborg1_id,"FirstName","LastName",userEmail,spogServer.GetLoggedinUser_RoleID(),null,"Mclaren@2016",2},
			{ "Get customer account of normal msp users with the normal msp user token ", ti.normal_msp1_suborg1_user1_token,ti.normal_msp_org2_user1_token,ti.normal_msp1_suborg1_id,"FirstName","LastName",userEmail,spogServer.GetLoggedinUser_RoleID(),null,"Mclaren@2016",2},
			{ "Get customer account of normal msp users with the normal msp account admin user token ", ti.normal_msp1_suborg1_user1_token,ti.normal_msp_org2_msp_accountadmin1_token,ti.normal_msp1_suborg1_id,"FirstName","LastName",userEmail,spogServer.GetLoggedinUser_RoleID(),null,"Mclaren@2016",2},
			{ "Get customer account of normal msp users with the customer accoutn of normal msp user token ", ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_id,"FirstName","LastName",userEmail,spogServer.GetLoggedinUser_RoleID(),null,"Mclaren@2016",2},
			{ "Get customer account of normal msp users with the root msp user token ", ti.normal_msp1_suborg1_user1_token,ti.root_msp_org1_user1_token,ti.normal_msp1_suborg1_id,"FirstName","LastName",userEmail,spogServer.GetLoggedinUser_RoleID(),null,"Mclaren@2016",2},
			{ "Get customer account of normal msp users with the root msp account admin user token ", ti.normal_msp1_suborg1_user1_token,ti.root_msp_org1_msp_accountadmin1_token,ti.normal_msp1_suborg1_id,"FirstName","LastName",userEmail,spogServer.GetLoggedinUser_RoleID(),null,"Mclaren@2016",2},
			{ "Get customer account of normal msp users with the customer account of root msp", ti.normal_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_id,"FirstName","LastName",userEmail,spogServer.GetLoggedinUser_RoleID(),null,"Mclaren@2016",2},
			{ "Get customer account of normal msp users with the submsp user token", ti.normal_msp1_suborg1_user1_token,ti.root_msp1_submsp1_user1_token,ti.normal_msp1_suborg1_id,"FirstName","LastName",userEmail,spogServer.GetLoggedinUser_RoleID(),null,"Mclaren@2016",2},
			{ "Get customer account of normal msp users with the submsp account admin user token", ti.normal_msp1_suborg1_user1_token,ti.root_msp1_submsp1_account_admin_token,ti.normal_msp1_suborg1_id,"FirstName","LastName",userEmail,spogServer.GetLoggedinUser_RoleID(),null,"Mclaren@2016",2},
			{ "Get customer account of normal msp users with the customer account of submsp", ti.normal_msp1_suborg1_user1_token,ti.msp1_submsp1_suborg2_user1_token,ti.normal_msp1_suborg1_id,"FirstName","LastName",userEmail,spogServer.GetLoggedinUser_RoleID(),null,"Mclaren@2016",2},

		};
	}

	@Test(dataProvider = "insufficientTestcases_Info", enabled = false)
	public void getuserInfowithInvalidToken(String testCase,
			String token,
			String otherOrgToken,
			String ogranization_id,
			String firstName,
			String lastName,
			String email,
			String role_id,
			String preference_language,
			String password,
			int noofusers
			) {

		test = ExtentManager.getNewTest(testCase);
		test.assignAuthor("Sykam, NagaMalleswari");

		ArrayList<HashMap<String,Object>>expected_usersInfo=createUsersInfo(token,firstName,lastName, email,role_id,ogranization_id,preference_language,password,noofusers);
		test.log(LogStatus.INFO, "Get all users and validate the users");
		spogServer.getUsersForOrganizationWithCheck(otherOrgToken, ogranization_id,role_id, expected_usersInfo, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY, test);
	}


	@Test(dataProvider = "create_Users_Info", enabled = false)
	public void getusers_InvalidTestCases(String testCase,
			String token,
			String ogranization_id,
			String firstName,
			String lastName,
			String email,
			String role_id,
			String preference_language,
			String password,
			int noofusers
			) {

		test = ExtentManager.getNewTest(testCase);
		test.assignAuthor("Sykam, NagaMalleswari");

		ArrayList<HashMap<String,Object>>expected_usersInfo=createUsersInfo(token,firstName,lastName, email,role_id,ogranization_id,preference_language,password,noofusers);
		test.log(LogStatus.INFO, "Get all users and validate the users");

		spogServer.getUsersForOrganizationWithCheck("", ogranization_id,role_id, expected_usersInfo, SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);
		spogServer.getUsersForOrganizationWithCheck("Invalid", ogranization_id,role_id, expected_usersInfo, SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);

		ogranization_id =UUID.randomUUID().toString();
		spogServer.getUsersForOrganizationWithCheck(token, ogranization_id,role_id, expected_usersInfo, SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);

		test.log(LogStatus.INFO, "Delete users of logged in user");
		String user_id=spogServer.GetLoggedinUser_UserID();
		deleteUsers(token, user_id, ogranization_id);
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
	 *Compose UserInfo
	 ******************************************************************************/


	public ArrayList<HashMap<String,Object>> createUsersInfo(String token,String firstName,String lastName,String email,String role_id,String  ogranization_id,String preference_language,String password,int noofusers) {

		ArrayList<HashMap<String, Object>> expected_usersInfo = new ArrayList<>();
		HashMap<String, Object> temp;

		if(!(noofusers==0)) {
			for(int i=0;i<noofusers;i++) {
				temp=jp.composeUserInfo(firstName, lastName, email+i, role_id, ogranization_id, preference_language);
				expected_usersInfo.add(temp);
				spogServer.setToken(token);
				String user_id = spogServer.getUserIdInfo(firstName, lastName, email+i, null, role_id, ogranization_id, preference_language, password,SpogConstants.SUCCESS_POST,null, test);
				temp.put("user_id", user_id);

			}
		}
		return expected_usersInfo;
	}


	public void deleteUsers(String token,String expuser_id,String org_id) {
		Response response=spogServer.getUsersInfo(token, org_id);
		ArrayList<HashMap<String,Object>>allusers=	response.then().extract().path("data");

		for(int i =0;i<allusers.size();i++) {
			String actUser_id=allusers.get(i).get("user_id").toString();
			if(!(actUser_id==expuser_id)) {
				spogServer.DeleteUserById(actUser_id);
			}
		}	
	}
}