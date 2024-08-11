package api.users.userfilters;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.Log4SPOGServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class CreateUserFilterForSpecificUserTest extends base.prepare.Is4Org {

	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private UserSpogServer userSpogServer;
	//public int Nooftest;
	//	private ExtentReports rep;
	private ExtentTest test;

	private TestOrgInfo ti;
	//private String runningMachine;
	//private String buildVersion;

	LocalDate date = LocalDate.now();  
	LocalDate yesterday = date.minusDays(1);  
	LocalDate tomorrow = yesterday.plusDays(2);


	@BeforeClass
	@Parameters({ "baseURI", "port","csrAdminUserName", "csrAdminPassword","csrReadOnlyAdminName","csrReadOnlyAdminPassword","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword,String csrReadOnlyAdminName,String csrReadOnlyAdminPassword, String logFolder,String runningMachine,String buildVersion) throws UnknownHostException {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		rep = ExtentManager.getInstance(getClass().getSimpleName(), logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Prasad.Deverakonda";

		Nooftest=0;
		ti = new TestOrgInfo(spogServer, test);	

		//Used for creating a build number with dateformat
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date=new java.util.Date();
		this.buildVersion=buildVersion+"_"+dateFormater.format(date);

		this.BQName=this.getClass().getSimpleName();
		this.runningMachine=runningMachine;
		System.out.println("The value of hit is "+count1.isstarttimehit());


		if( count1.isstarttimehit( ) == 0 ) 
		{
			System.out.println("into creation time");
			creationTime=System.currentTimeMillis();
			count1.setcreationtime(creationTime);

			// creationTime = System.currentTimeMillis();
			try
			{
				bqdb1.updateTable(BQName, this.runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0", String.valueOf(Nooftest), count1.getcreationtime(), "InProgress",author+" and Rest server is "+baseURI.split("//")[1]);
			} 
			catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@DataProvider(name = "create_user_filter_valid")
	public final Object[][] createUserFilterValidParams() {
		return new Object[][] {
			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
				"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcs",
					"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", ti.direct_org1_user1_email,
						"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
							"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "firstname" ,
								"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "lastname" ,
									"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },


			//Get User filters for the normal msp organization

			{ "Normal_msp", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null ,
										"false","verified",SpogConstants.MSP_ADMIN,"true",0 },
			{ "Normal_msp", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "firstname" ,
											"false","verified",SpogConstants.MSP_ADMIN,"true",0 },
			{ "Normal_msp", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "lastname" ,
												"false","verified",SpogConstants.MSP_ADMIN,"true",0 },
			{ "Normal_msp", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName",ti.normal_msp1_suborg1_user1_email,
													"false","verified",SpogConstants.MSP_ADMIN,"true",0 },
			{ "Normal_msp", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName","@arcserve.com",
														"false","verified",SpogConstants.MSP_ADMIN,"true",0 },	


			//Get User filters of Normal msp account admin 
			{ "Normal_msp_account_admin", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null ,
															"false","verified",SpogConstants.MSP_ACCOUNT_ADMIN,"true",0 },
			{ "Normal_msp_account_admin", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "firstname" ,
																"false","verified",SpogConstants.MSP_ACCOUNT_ADMIN,"true",0 },
			{ "Normal_msp_account_admin", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "lastname" ,
																	"false","verified",SpogConstants.MSP_ACCOUNT_ADMIN,"true",0 },
			{ "Normal_msp_account_admin", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName",ti.normal_msp_org1_msp_accountadmin1_email,
																		"false","verified",SpogConstants.MSP_ACCOUNT_ADMIN,"true",0 },
			{ "Normal_msp_account_admin", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName","@arcserve.com",
																			"false","verified",SpogConstants.MSP_ACCOUNT_ADMIN,"true",0 },	


			//Get User filters of normal sub msp Organization
			{ "NormalMsp_Suborg", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null ,
																				"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "NormalMsp_Suborg", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "firstname" ,
																					"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "NormalMsp_Suborg", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "lastname" ,
																						"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "NormalMsp_Suborg", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName",ti.normal_msp1_suborg1_user1_email,
																							"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "NormalMsp_Suborg", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName","@arcserve.com",
																								"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },	


			//Get User Filters for Root MSP organization
			{ "root_msp", ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null ,
																									"false","verified",SpogConstants.MSP_ADMIN,"true",0 },
			{ "root_msp", ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "firstname" ,
																										"false","verified",SpogConstants.MSP_ADMIN,"true",0 },
			{ "root_msp", ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "lastname" ,
																											"false","verified",SpogConstants.MSP_ADMIN,"true",0 },
			{ "root_msp", ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName",ti.root_msp_org1_user1_email,
																												"false","verified",SpogConstants.MSP_ADMIN,"true",0 },
			{ "root_msp", ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName","@arcserve.com",
																													"false","verified",SpogConstants.MSP_ADMIN,"true",0 },	

			//Get User Filters for Customer account of Root MSP Organization
			{ "Customer_account_Root_Msp", ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null ,
																														"false","verified",SpogConstants.MSP_ADMIN,"true",0 },
			{ "Customer_account_Root_Msp", ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "firstname" ,
																															"false","verified",SpogConstants.MSP_ADMIN,"true",0 },
			{ "Customer_account_Root_Msp", ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "lastname" ,
																																"false","verified",SpogConstants.MSP_ADMIN,"true",0 },
			{ "Customer_account_Root_Msp", ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName",ti.root_msp_org1_user1_email,
																																	"false","verified",SpogConstants.MSP_ADMIN,"true",0 },
			{ "Customer_account_Root_Msp", ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName","@arcserve.com",
																																		"false","verified",SpogConstants.MSP_ADMIN,"true",0 },	

			//Get User Filters for Sub MSP organization
			{ "sub_msp", ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null ,
																																			"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "sub_msp", ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "firstname" ,
																																				"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "sub_msp", ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "lastname" ,
																																					"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "sub_msp", ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName",ti.root_msp1_submsp1_user1_email,
																																						"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "sub_msp", ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName","@arcserve.com",
																																							"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },	
			//Get User filters for Customer account of Sub msp

			{ "Customeraccoutn_SUBMSP", ti.msp1_submsp1_sub_org1_id,ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null ,
																																								"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "sub_msp", ti.msp1_submsp1_sub_org1_id,ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "firstname" ,
																																									"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "sub_msp", ti.msp1_submsp1_sub_org1_id,ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "lastname" ,
																																										"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "sub_msp", ti.msp1_submsp1_sub_org1_id,ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName",ti.root_msp1_submsp1_user1_email,
																																											"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "sub_msp", ti.msp1_submsp1_sub_org1_id,ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName","@arcserve.com",
																																												"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },	


		};		

	}

	@Test(dataProvider = "create_user_filter_valid")
	public void CreateUserFilterForSpecifiedUserValid_200(String organizationType,
			String organization_id,
			String validToken,
			String user_id,
			String filter_name,
			String search_string,
			String user_is_blocked,
			String user_status, 
			String role_id, 
			String is_default,
			int count) 
	{
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);

		ArrayList<HashMap<String,Object>> expectedresponse = new ArrayList<>();
		HashMap<String,Object> expected_response = new HashMap<String,Object>();

		test.log(LogStatus.INFO, "List all the job filter Ids for a Logged in user whose is_default value is True");
		String additionalURL = spogServer.PrepareURL("", "", 1, 20, test);
		userSpogServer.setToken(validToken);


		test.log(LogStatus.INFO, "Create user filter in org of type "+organizationType);
		Response response = userSpogServer.createUserFilterForSpecificUser(user_id, filter_name, search_string, user_is_blocked, user_status, role_id, is_default, validToken, test);

		test.log(LogStatus.INFO, "Validate the response");
		String  filter_Id = userSpogServer.verifyUserFilters(response, filter_name, search_string, user_is_blocked, user_status, role_id, is_default, count, SpogConstants.SUCCESS_POST, null, test);

		test.log(LogStatus.INFO, "Compose the expected response");
		expected_response = composeExpectedUserFilter(filter_Id, filter_name, user_id, organization_id, search_string, user_is_blocked, user_status,role_id, is_default, 1);
		expectedresponse.add(expected_response);

		test.log(LogStatus.INFO, "Delete the user filter in org of type: "+organizationType);
		userSpogServer.deleteUserFilterByUserIdWithCheck(validToken, user_id, filter_Id, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);



	}

	@DataProvider(name = "Create_user_filter_401")
	public final Object[][] CreateUserFilter_401() {
		return new Object[][] {
			// different role id
			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
				"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "sub", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null ,
					"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },

		};
	}
	@Test(dataProvider = "Create_user_filter_401")
	public void CreateUserFilterForSpecifiedUserValid_401(String organizationType,
			String organization_id,
			String validToken,
			String user_id,
			String filter_name,
			String search_string,
			String user_is_blocked,
			String user_status, 
			String role_id, 
			String is_default,
			int count) 
	{
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		String filter_Id = null;
		ArrayList<HashMap<String,Object>> expectedresponse = new ArrayList<>();
		HashMap<String,Object> expected_response = new HashMap<String,Object>();


		userSpogServer.setToken(validToken);
		test.log(LogStatus.INFO, "Create user filter in org of type "+organizationType + " using missing token");
		Response response = userSpogServer.createUserFilterForSpecificUser(user_id, filter_name, search_string, user_is_blocked, user_status, role_id, is_default, "", test);

		test.log(LogStatus.INFO, "Validate the response");
		userSpogServer.verifyUserFilters(response, filter_name, search_string, user_is_blocked, user_status, role_id, is_default, count, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);

		test.log(LogStatus.INFO, "Create user filter in org of type "+organizationType + " using invalid token");
		response = userSpogServer.createUserFilterForSpecificUser(user_id, filter_name, search_string, user_is_blocked, user_status, role_id, is_default, validToken+"J", test);

		test.log(LogStatus.INFO, "Validate the response");
		userSpogServer.verifyUserFilters(response, filter_name, search_string, user_is_blocked, user_status, role_id, is_default, count, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT,test);		
	}

	@DataProvider(name = "createUserFilter_403")
	public final Object[][] createUserFilter_403() {
		return new Object[][] {
			//Direct Organization
			{ "Create Direct org user filters with another direct Org Token ",ti.direct_org2_user1_token,ti.direct_org1_user1_id},
			{ "Create Direct org user filters with Normal msp user Token ",ti.normal_msp_org2_user1_token,ti.direct_org1_user1_id},
			{ "Create Direct org user filters with Normal msp accout adim user Token ",ti.normal_msp_org2_msp_accountadmin1_token,ti.direct_org1_user1_id},
			{ "Create Direct org user filters with Normal msp's customer account user Token ",ti.normal_msp2_suborg1_user1_token,ti.direct_org1_user1_id},

			{ "Create Direct org user filters with Root msp user Token ",ti.root_msp_org1_user1_token,ti.direct_org1_user1_id},
			{ "Create Direct org user filters with  Root msp account adim user Token ",ti.root_msp_org1_msp_accountadmin1_token,ti.direct_org1_user1_id},
			{ "Create Direct org user filters with Customer accout of RootMsp user token",ti.root_msp1_suborg1_user1_token,ti.direct_org1_user1_id},

			{ "Create Direct org user filters with Sub msp user Token ",ti.direct_org2_user1_token,ti.direct_org1_user1_id},
			{ "Create Direct org user filters with Sub msp account adim user Token ",ti.direct_org2_user1_token,ti.direct_org1_user1_id},
			{ "Create Direct org user filters with Customer accout of Sub Msp user toekn",ti.direct_org2_user1_token,ti.direct_org1_user1_id},


			//Create User filters of normal  msp Organization

			{ "Create Normal msp's Customer account user filters with the Direct user token",ti.direct_org1_user1_token,ti.normal_msp1_suborg1_user1_id},
			{ "Create Normal msp's Customer account user filters with the normal user token", ti.normal_msp_org2_user1_token,ti.normal_msp1_suborg1_user1_id},
			{ "Create Normal msp's Customer account user filters with the normal msp account admin user token", ti.normal_msp_org2_msp_accountadmin1_token,ti.normal_msp1_suborg1_user1_id },
			{ "Create Normal msp's Customer account user filters with the customer account of another normal msp organzation", ti.normal_msp2_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id},
			{ "Create Normal msp's Customer account user filters with the root msp organzation", ti.root_msp1_submsp1_user1_token,ti.normal_msp1_suborg1_user1_id},
			{ "Create Normal msp's  Customer account user filters with the sub msp's account admin", ti.root_msp1_submsp1_account_admin_token,ti.normal_msp1_suborg1_user1_id},	
			{ "Create Normal msp's Customer account user filters with the sub msp's customer account",ti.msp1_submsp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id},	
			{ "Create Normal msp's Customer account user filters with the root msp organzation",ti.root_msp_org1_user1_token,ti.normal_msp1_suborg1_user1_id},	
			{ "Create Normal msp's Customer account user filters with the root msp  account adminorganzation", ti.root_msp_org1_msp_accountadmin1_token,ti.normal_msp1_suborg1_user1_id},	
			{ "Create Normal msp's Customer account user filters with the root msp's cutstomer account  organzation", ti.root_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id},

			//Create User filters of root   msp Organization
			{ "Create Root msp user filters with the Direct user token",ti.direct_org1_user1_token,ti.root_msp_org1_user1_id },
			{ "Create Root msp user filters with the normal user token",ti.normal_msp_org1_user1_token,ti.root_msp_org1_user1_id },
			{ "Create Root msp user filters with the normal msp account admin user token",ti.normal_msp_org1_msp_accountadmin1_token,ti.root_msp_org1_user1_id},
			{ "Create Root msp user filters with the customer account of another normal msp organzation",ti.normal_msp2_suborg1_user1_token,ti.root_msp_org1_user1_id},
			{ "Create Root msp user filters with the root msp organzation",ti.root_msp1_submsp1_user1_token,ti.root_msp_org1_user1_id},	
			{ "Create Root msp user filters with the sub msp's account admin", ti.root_msp1_submsp1_account_admin_token,ti.root_msp_org1_user1_id},	
			{ "Create Root msp user filters with the sub msp's customer account",ti.msp1_submsp1_suborg1_user1_token,ti.root_msp_org1_user1_id},	
			{ "Create Root msp user filters with the root msp organzation",ti.root_msp_org2_user1_token,ti.root_msp_org1_user1_id},	
			{ "Create Root msp user filters with the root msp  account adminorganzation", ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp_org1_user1_id },	
			{ "Create Root msp user filters with the root msp's cutstomer account  organzation",ti.root_msp1_suborg1_user1_token,ti.root_msp_org1_user1_id },	

			//Create User filters of normal   msp Organization
			{ "Create sub msp  user filters with the Direct user token",ti.direct_org1_user1_token,ti.root_msp1_submsp1_user1_id },
			{ "Create sub msp  user filters with the normal user token", ti.normal_msp_org1_user1_token,ti.root_msp1_submsp1_user1_id},
			{ "Create sub msp  user filters with the normal msp account admin user token",ti.normal_msp_org1_msp_accountadmin1_token,ti.root_msp1_submsp1_user1_id },
			{ "Create sub msp  user filters with the customer account of another normal msp organzation", ti.normal_msp2_suborg1_user1_token,ti.root_msp1_submsp1_user1_id },
			{ "Create sub msp  user filters with the root msp organzation",ti.root_msp2_submsp1_user1_token,ti.root_msp1_submsp1_user1_id },	
			{ "Create sub msp  user filters with the sub msp's account admin",ti.root_msp1_submsp1_account_admin_token,ti.root_msp1_submsp1_user1_id },	
			{ "Create sub msp  user filters with the sub msp's customer account",ti.msp1_submsp1_suborg1_user1_token,ti.root_msp1_submsp1_user1_id },	
			{ "Create sub msp  user filters with the root msp organzation",ti.root_msp_org2_user1_token,ti.root_msp1_submsp1_user1_id },	
			{ "Create sub msp  user filters with the root msp  account adminorganzation",ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp1_submsp1_user1_id },	
			{ "Create sub msp  user filters with the root msp's cutstomer account  organzation", ti.root_msp1_suborg1_user1_token,ti.root_msp1_submsp1_user1_id },	
		};
	}

	@Test(dataProvider = "createUserFilter_403")
	public void createUserFilterForSpecificUser_403(String testcase,
			String Othertoken,
			String user_id) 
	{
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_");
		String filter_Id = null;
		ArrayList<HashMap<String,Object>> expectedresponse = new ArrayList<>();
		HashMap<String,Object> expected_response = new HashMap<String,Object>();

		String filter_name=RandomStringUtils.randomAlphanumeric(4)+"filterName";
		String search_string="@arcserve.com";
		String user_is_blocked="false";
		String user_status="verified";
		String  role_id=null;
		String is_default="true";
		userSpogServer.setToken(Othertoken);
		test.log(LogStatus.INFO, "Create user filter in org of type ");
		Response response = userSpogServer.createUserFilterForSpecificUser(user_id, filter_name, search_string, user_is_blocked, user_status,  role_id, is_default, Othertoken, test);

		test.log(LogStatus.INFO, "Validate the response");
		String filter_id = userSpogServer.verifyUserFilters(response, filter_name, search_string, user_is_blocked, user_status, role_id, is_default, 0, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY, test);

		//		userSpogServer.deleteUserFilterByUserIdWithCheck(validToken, user_id, filter_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);


	}

	@Test(dataProvider = "Create_user_filter_401")
	public void createUserFilterForSpecificUser_404(String organizationType,
			String organization_id,
			String validToken,
			String user_id,
			String filter_name,
			String search_string,
			String user_is_blocked,
			String user_status, 
			String role_id, 
			String is_default,
			int count) 
	{
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		String filter_Id = null;
		ArrayList<HashMap<String,Object>> expectedresponse = new ArrayList<>();
		HashMap<String,Object> expected_response = new HashMap<String,Object>();


		userSpogServer.setToken(validToken);
		test.log(LogStatus.INFO, "Create user filter in org of type "+organizationType + " using user id that does not exist");
		Response response = userSpogServer.createUserFilterForSpecificUser(UUID.randomUUID().toString(), filter_name, search_string, user_is_blocked, user_status, role_id, is_default, validToken, test);

		test.log(LogStatus.INFO, "Validate the response");
		userSpogServer.verifyUserFilters(response, filter_name, search_string, user_is_blocked, user_status, role_id, is_default, count, SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.USER_ID_DOESNOT_EXIST, test);

		test.log(LogStatus.INFO, "Create user filter in org of type "+organizationType + " using invalid token");
		response = userSpogServer.createUserFilterForSpecificUser(user_id, filter_name, search_string, user_is_blocked, user_status, role_id, is_default, validToken+"J", test);

		test.log(LogStatus.INFO, "Validate the response");
		userSpogServer.verifyUserFilters(response, filter_name, search_string, user_is_blocked, user_status, role_id, is_default, count, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT,test);		
	}


	@DataProvider(name = "create_user_filter_400")
	public final Object[][] createUserFilter_400() {
		return new Object[][] {
			//Direct Org
			{ "direct_roleid", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null,
				null,null,SpogConstants.MSP_ACCOUNT_ADMIN,"true",0 },
			{ "direct_status", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcs",
					"false","verifie",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "direct_blocked", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", ti.direct_org1_user1_email,
						"yes","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "direct_searchString", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "a",
							"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },


			//Get User filters for the normal msp organization

			{ "Normal_msp_roleid", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null ,
								null,null,SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "Normal_msp_status", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "firstname" ,
									"false","verifie",SpogConstants.MSP_ADMIN,"true",0 },
			{ "Normal_msp_blocked", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "lastname" ,
										"fals","verified",SpogConstants.MSP_ADMIN,"true",0 },
			{ "Normal_msp_searchString", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName","aa",
											"false","verified",SpogConstants.MSP_ADMIN,"true",0 },


			//Get User filters of Normal msp account admin 
			{ "Normal_msp_account_admin_roleid", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null ,
												null,null,SpogConstants.MSP_ADMIN,"true",0 },
			{ "Normal_msp_account_admin_status", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "firstname" ,
													"false","verifie",SpogConstants.MSP_ACCOUNT_ADMIN,"true",0 },
			{ "Normal_msp_account_admin_blocked", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "lastname" ,
														"fal","verified",SpogConstants.MSP_ACCOUNT_ADMIN,"true",0 },
			{ "Normal_msp_account_admin_searchString", ti.normal_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName","a",
															"false","verified",SpogConstants.MSP_ACCOUNT_ADMIN,"true",0 },


			//Get User filters of normal sub msp Organization
			{ "NormalMsp_Suborg_roleid", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null ,
																"false","verified",SpogConstants.MSP_ACCOUNT_ADMIN,"true",0 },
			{ "NormalMsp_Suborg_status", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "firstname" ,
																	"false","verifie",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "NormalMsp_Suborg_blocked", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "lastname" ,
																		"fale","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "NormalMsp_Suborg_searchString", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName","te",
																			"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },


			//Get User Filters for Root MSP organization
			{ "root_msp_roleid", ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null ,
																				"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "root_msp_status", ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "firstname" ,
																					"false","verifie",SpogConstants.MSP_ADMIN,"true",0 },
			{ "root_msp_blocked", ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "lastname" ,
																						"fale","verified",SpogConstants.MSP_ADMIN,"true",0 },
			{ "root_msp_searchString", ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName","te",
																							"false","verified",SpogConstants.MSP_ADMIN,"true",0 },

			//Get User Filters for Customer account of Root MSP Organization
			{ "Customer_account_Root_Msp_roleid", ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null ,
																								"false","verified",SpogConstants.MSP_ADMIN,"true",0 },
			{ "Customer_account_Root_Msp_status", ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "firstname" ,
																									"false","verifie",SpogConstants.MSP_ADMIN,"true",0 },
			{ "Customer_account_Root_Msp_blocked", ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "lastname" ,
																										"fals","verified",SpogConstants.MSP_ADMIN,"true",0 },
			{ "Customer_account_Root_Msp_searchString", ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName","ti",
																											"false","verified",SpogConstants.MSP_ADMIN,"true",0 },

			//Get User Filters for Sub MSP organization
			{ "sub_msp_roleid", ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null ,
																												"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "sub_msp_status", ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "firstname" ,
																													"false","verifie",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "sub_msp_blocked", ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "lastname" ,
																														"fale","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "sub_msp_searchString", ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName","rt",
																															"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			//Get User filters for Customer account of Sub msp

			{ "Customeraccoutn_SUBMSP_roleid", ti.msp1_submsp1_sub_org1_id,ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null ,
																																"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "Customeraccoutn_SUBMSP_status", ti.msp1_submsp1_sub_org1_id,ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "firstname" ,
																																	"false","verifie",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "Customeraccoutn_SUBMSP_blocked", ti.msp1_submsp1_sub_org1_id,ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "lastname" ,
																																		"fals","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "Customeraccoutn_SUBMSP_searchString", ti.msp1_submsp1_sub_org1_id,ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName","qw",
																																			"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },

		};
	}
	@Test(dataProvider = "create_user_filter_400")
	public void createUserFilterForSpecificUser_400(String organizationType,
			String organization_id,
			String validToken,
			String user_id,
			String filter_name,
			String search_string,
			String user_is_blocked,
			String user_status, 
			String role_id, 
			String is_default,
			int count) 
	{
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		String filter_Id = null;
		ArrayList<HashMap<String,Object>> expectedresponse = new ArrayList<>();
		HashMap<String,Object> expected_response = new HashMap<String,Object>();


		userSpogServer.setToken(validToken);
		test.log(LogStatus.INFO, "Create user filter in org of type "+organizationType + " using invalid params or enums");
		Response response = userSpogServer.createUserFilterForSpecificUser(user_id, filter_name, search_string, user_is_blocked, user_status, role_id, is_default, validToken, test);

		test.log(LogStatus.INFO, "Validate the response");
		if(organizationType.contains("_blocked")) {
			userSpogServer.verifyUserFilters(response, filter_name, search_string, user_is_blocked, user_status, role_id, is_default, count, 
					SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.INVALID_PARAMETER,test);
		}
		else if(organizationType.contains("-email")||organizationType.contains("-searchString")) {

			userSpogServer.verifyUserFilters(response, filter_name, search_string, user_is_blocked, user_status, role_id, is_default, count, 
					SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.SEARCH_STRING_MIN_THREE,test);
		}else if(organizationType.contains("_status")) {

			userSpogServer.verifyUserFilters(response, filter_name, search_string, user_is_blocked, user_status, role_id, is_default, count, 
					SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_STATUS_NOT_VALID_ENUM,test);
		}
		/*else {
			userSpogServer.verifyUserFilters(response, filter_name, search_string, user_is_blocked, user_status, role_id, is_default, count, 
					SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_ROLE_ID_NOT_VALID_ENUM,test);
			test.log(LogStatus.INFO, "Create user filter in org of type "+organizationType + " using invalid user id ");
			response = userSpogServer.createUserFilterForSpecificUser("123", filter_name, search_string, user_is_blocked, user_status, role_id, is_default, validToken, test);

			test.log(LogStatus.INFO, "Validate the response");
			userSpogServer.verifyUserFilters(response, filter_name, search_string, user_is_blocked, user_status, role_id, is_default, count, 
					SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_ROLE_ID_NOT_VALID_ENUM, test);
		}

*/

	}

	@AfterMethod
	public void getResult(ITestResult result){
		if(result.getStatus() == ITestResult.FAILURE){
			count1.setfailedcount();
			//remaincases=Nooftest-passedcases-failedcases;
			test.log(LogStatus.FAIL, "Test Case Failed is "+result.getName()+" with parameters as "+Arrays.asList(result.getParameters()) );
			test.log(LogStatus.FAIL, result.getThrowable().getMessage());

		}else if(result.getStatus() == ITestResult.SKIP){
			test.log(LogStatus.SKIP, "Test Case Skipped is "+result.getName());
			count1.setskippedcount();
		}else if(result.getStatus()==ITestResult.SUCCESS){
			count1.setpassedcount();
			//remaincases=Nooftest-passedcases-failedcases;

		}
		// ending test
		//endTest(logger) : It ends the current test and prepares to create HTML report
		rep.endTest(test);
		//rep.flush();
	}
	/*	@AfterTest
	public void aftertest() {
		test.log(LogStatus.INFO, "The total test cases passed are "+count1.getpassedcount());
		test.log(LogStatus.INFO, "the total test cases failed are "+count1.getfailedcount());

		rep.flush();

	}
	@AfterClass
	public void updatebd() {
		test.log(LogStatus.INFO, "Performing the operations to delete the user and orginzation by logging in as csr admin");	
		test.log(LogStatus.INFO, "Login in as csr admin to delete the organization");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);



		// spogServer.DeleteUserById(user_id, test);
		spogServer.DeleteOrganizationWithCheck(direct_organization_id, test);
		spogServer.DeleteOrganizationWithCheck(sub_org_Id, test);
		spogServer.DeleteOrganizationWithCheck(sub_org_Id_1, test);
		spogServer.DeleteOrganizationWithCheck(msp_organization_id, test);
		spogServer.DeleteOrganizationWithCheck(msp_organization_id_b, test);
		try {
			if(count1.getfailedcount()>0) {
				Nooftest=count1.getpassedcount()+count1.getfailedcount()+count1.getskippedcount();
				bqdb1.updateTable(BQame, runningMachine, this.buildVersion, String.valueOf(Nooftest), Integer.toString(count1.getpassedcount()), Integer.toString(count1.getfailedcount()), String.valueOf(count1.getskippedcount()), count1.getcreationtime(), "Failed");
			}else {
				Nooftest=count1.getpassedcount()+count1.getfailedcount()+count1.getskippedcount();
				bqdb1.updateTable(BQame, runningMachine, this.buildVersion, String.valueOf(Nooftest), Integer.toString(count1.getpassedcount()), Integer.toString(count1.getfailedcount()), String.valueOf(count1.getskippedcount()), count1.getcreationtime(), "Passed");
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	 */

	/*****************************************************************************************************/

	public HashMap<String,Object> composeExpectedUserFilter(String filter_id,
			String filter_name,
			String user_id,
			String organization_id,
			String Search_string, 
			String user_is_blocked, 
			String user_status, 
			String role_id, 
			String is_defaul,
			int count) {
		HashMap<String,Object> expected_response = new HashMap<>();
		expected_response.put("filter_id", filter_id);
		expected_response.put("filter_name", filter_name);
		expected_response.put("user_id", user_id);
		expected_response.put("organization_id", organization_id);
		expected_response.put("Search_string",Search_string);
		expected_response.put("blocked",user_is_blocked);
		expected_response.put("status",user_status);
		expected_response.put("role_id",role_id);
		expected_response.put("is_defaul",is_defaul);
		expected_response.put("count",count);
		return expected_response;
	}


}
