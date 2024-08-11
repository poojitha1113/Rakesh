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
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class GetUserFiltersForSpecificUserTest extends base.prepare.Is4Org{
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private UserSpogServer userSpogServer;
	//public int Nooftest;
	//private ExtentReports rep;
	private ExtentTest test;
	private TestOrgInfo ti;

	private String site_version="1.0.0";
	private String gateway_hostname="Malleswari";
	//used for test case count like passed,failed,remaining cases
	//private testcasescount count1;
	int passedcases=0;
	int failedcases=0;
	int skippedcases=0;


	LocalDate date = LocalDate.now();  
	LocalDate yesterday = date.minusDays(1);  
	LocalDate tomorrow = yesterday.plusDays(2);


	@BeforeClass
	@Parameters({ "baseURI", "port","csrAdminUserName", "csrAdminPassword","csrReadOnlyAdminName","csrReadOnlyAdminPassword","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword,String csrReadOnlyAdminName,String csrReadOnlyAdminPassword, String logFolder,String runningMachine,String buildVersion) throws UnknownHostException {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		rep = ExtentManager.getInstance("GetUserFiltersForSpecificUserwithTest", logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Malleswari";

		Nooftest=0;
		//Used for creating a build number with dateformat
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date=new java.util.Date();
		this.buildVersion=buildVersion+"_"+dateFormater.format(date);

		BQName=this.getClass().getSimpleName();
		this.runningMachine=runningMachine;
		System.out.println("The value of hit is "+count1.isstarttimehit());
		ti = new TestOrgInfo(spogServer, test);	

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


	@DataProvider(name = "Get_user_filter_valid")
	public final Object[][] getUserFilterValidParams() {
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

	@Test(dataProvider = "Get_user_filter_valid")
	public void GetUserFilterForSpecifiedUserValid_200(String organizationType,
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

		test.log(LogStatus.INFO, "Get the user filter for specified user");
		userSpogServer.getUserFiltersForSpecificUserwithCheck(user_id,"",validToken,expectedresponse,SpogConstants.SUCCESS_GET_PUT_DELETE,null,test);


		//created filter with the userstaus and user_verified 

		test.log(LogStatus.INFO, "Delete the user filter in org of type: "+organizationType);
		userSpogServer.deleteUserFilterByUserIdWithCheck(validToken, user_id, filter_Id, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);


	}




	@DataProvider(name = "Get_user_filter_401")
	public final Object[][] GetUserFilter_401() {
		return new Object[][] {
			// different role id
			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
				"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "sub", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null ,
					"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },

		};
	}
	@Test(dataProvider = "Get_user_filter_401")
	public void GetUserFilterForSpecifiedUserValid_401(String organizationType,
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

		//Missed the token 
		test.log(LogStatus.INFO, "Get the user filter for specified user");
		userSpogServer.getUserFiltersForSpecificUserwithCheck(user_id,"","",expectedresponse,SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);

		//given invalid token 
		test.log(LogStatus.INFO, "Get the user filter for specified user");
		userSpogServer.getUserFiltersForSpecificUserwithCheck(user_id,"",validToken+"123",expectedresponse,SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT,test);

	}

	@DataProvider(name = "Get_user_filter_403")
	public final Object[][] GetUserFilter_403() {
		return new Object[][] {
			//Direct Organization
			{ "Get Direct org user filters with another direct Org Token ",ti.direct_org2_user1_token,ti.direct_org1_user1_id},
			{ "Get Direct org user filters with Normal msp user Token ",ti.normal_msp_org2_user1_token,ti.direct_org1_user1_id},
			{ "Get Direct org user filters with Normal msp accout adim user Token ",ti.normal_msp_org2_msp_accountadmin1_token,ti.direct_org1_user1_id},
			{ "Get Direct org user filters with Normal msp's customer account user Token ",ti.normal_msp2_suborg1_user1_token,ti.direct_org1_user1_id},

			{ "Get Direct org user filters with Root msp user Token ",ti.root_msp_org1_user1_token,ti.direct_org1_user1_id},
			{ "Get Direct org user filters with  Root msp account adim user Token ",ti.root_msp_org1_msp_accountadmin1_token,ti.direct_org1_user1_id},
			{ "Get Direct org user filters with Customer accout of RootMsp user token",ti.root_msp1_suborg1_user1_token,ti.direct_org1_user1_id},

			{ "Get Direct org user filters with Sub msp user Token ",ti.direct_org2_user1_token,ti.direct_org1_user1_id},
			{ "Get Direct org user filters with Sub msp account adim user Token ",ti.direct_org2_user1_token,ti.direct_org1_user1_id},
			{ "Get Direct org user filters with Customer accout of Sub Msp user toekn",ti.direct_org2_user1_token,ti.direct_org1_user1_id},


			//Get User filters of normal  msp Organization

			{ "Get Normal msp's Customer account user filters with the Direct user token",ti.direct_org1_user1_token,ti.normal_msp1_suborg1_user1_id},
			{ "Get Normal msp's Customer account user filters with the normal user token", ti.normal_msp_org2_user1_token,ti.normal_msp1_suborg1_user1_id},
			{ "Get Normal msp's Customer account user filters with the normal msp account admin user token", ti.normal_msp_org2_msp_accountadmin1_token,ti.normal_msp1_suborg1_user1_id },
			{ "Get Normal msp's Customer account user filters with the customer account of another normal msp organzation", ti.normal_msp2_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id},
			{ "Get Normal msp's Customer account user filters with the root msp organzation", ti.root_msp1_submsp1_user1_token,ti.normal_msp1_suborg1_user1_id},
			{ "Get Normal msp's  Customer account user filters with the sub msp's account admin", ti.root_msp1_submsp1_account_admin_token,ti.normal_msp1_suborg1_user1_id},	
			{ "Get Normal msp's Customer account user filters with the sub msp's customer account",ti.msp1_submsp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id},	
			{ "Get Normal msp's Customer account user filters with the root msp organzation",ti.root_msp_org1_user1_token,ti.normal_msp1_suborg1_user1_id},	
			{ "Get Normal msp's Customer account user filters with the root msp  account adminorganzation", ti.root_msp_org1_msp_accountadmin1_token,ti.normal_msp1_suborg1_user1_id},	
			{ "Get Normal msp's Customer account user filters with the root msp's cutstomer account  organzation", ti.root_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id},

			//Get User filters of root   msp Organization
			{ "Get Root msp user filters with the Direct user token",ti.direct_org1_user1_token,ti.root_msp_org1_user1_id },
			{ "Get Root msp user filters with the normal user token",ti.normal_msp_org1_user1_token,ti.root_msp_org1_user1_id },
			{ "Get Root msp user filters with the normal msp account admin user token",ti.normal_msp_org1_msp_accountadmin1_token,ti.root_msp_org1_user1_id},
			{ "Get Root msp user filters with the customer account of another normal msp organzation",ti.normal_msp2_suborg1_user1_token,ti.root_msp_org1_user1_id},
			{ "Get Root msp user filters with the root msp organzation",ti.root_msp1_submsp1_user1_token,ti.root_msp_org1_user1_id},	
			{ "Get Root msp user filters with the sub msp's account admin", ti.root_msp1_submsp1_account_admin_token,ti.root_msp_org1_user1_id},	
			{ "Get Root msp user filters with the sub msp's customer account",ti.msp1_submsp1_suborg1_user1_token,ti.root_msp_org1_user1_id},	
			{ "Get Root msp user filters with the root msp organzation",ti.root_msp_org2_user1_token,ti.root_msp_org1_user1_id},	
			{ "Get Root msp user filters with the root msp  account adminorganzation", ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp_org1_user1_id },	
			{ "Get Root msp user filters with the root msp's cutstomer account  organzation",ti.root_msp1_suborg1_user1_token,ti.root_msp_org1_user1_id },	

			//Get User filters of normal   msp Organization
			{ "Get sub msp  user filters with the Direct user token",ti.direct_org1_user1_token,ti.root_msp1_submsp1_user1_id },
			{ "Get sub msp  user filters with the normal user token", ti.normal_msp_org1_user1_token,ti.root_msp1_submsp1_user1_id},
			{ "Get sub msp  user filters with the normal msp account admin user token",ti.normal_msp_org1_msp_accountadmin1_token,ti.root_msp1_submsp1_user1_id },
			{ "Get sub msp  user filters with the customer account of another normal msp organzation", ti.normal_msp2_suborg1_user1_token,ti.root_msp1_submsp1_user1_id },
			{ "Get sub msp  user filters with the root msp organzation",ti.root_msp2_submsp1_user1_token,ti.root_msp1_submsp1_user1_id },	
			{ "Get sub msp  user filters with the sub msp's account admin",ti.root_msp1_submsp1_account_admin_token,ti.root_msp1_submsp1_user1_id },	
			{ "Get sub msp  user filters with the sub msp's customer account",ti.msp1_submsp1_suborg1_user1_token,ti.root_msp1_submsp1_user1_id },	
			{ "Get sub msp  user filters with the root msp organzation",ti.root_msp_org2_user1_token,ti.root_msp1_submsp1_user1_id },	
			{ "Get sub msp  user filters with the root msp  account adminorganzation",ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp1_submsp1_user1_id },	
			{ "Get sub msp  user filters with the root msp's cutstomer account  organzation", ti.root_msp1_suborg1_user1_token,ti.root_msp1_submsp1_user1_id },	

		};
	}


	@Test(dataProvider = "Get_user_filter_403")
	public void GetUserFilterForSpecifiedUserValid_403(String testcase,
			String Othertoken,
			String user_id
			)
	{
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+testcase);
		userSpogServer.getUserFiltersForSpecificUserwithCheck(user_id,"",Othertoken,new ArrayList<>(),SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY, test);

	}

	@DataProvider(name = "Get_user_filter_valid_deleteduser")
	public final Object[][] getUserFilterValidParamsdeleteduser() {
		return new Object[][] {
			// different role id
			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
				"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "submsp", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null ,
					"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "Normal_msp", ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,ti.normal_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null ,
						"false","verified",SpogConstants.MSP_ADMIN,"true",0 },
			{ "root_msp", ti.root_msp_org2_id,ti.root_msp_org2_user1_token,ti.root_msp_org2_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null ,
							"false","verified",SpogConstants.MSP_ADMIN,"true",0 },


		};

	}
	@Test(dataProvider = "Get_user_filter_valid_deleteduser")
	public void GetUserFilterForSpecifiedUserValid_404(String organizationType,
			String organization_id,
			String validToken,
			String user_id,
			String filter_name,
			String Search_string,
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
		test.log(LogStatus.INFO, "Create user filter in org of type "+organizationType);
		Response response = userSpogServer.createUserFilterForLoggedInUser(filter_name, Search_string, user_is_blocked, user_status, role_id, is_default, test);

		test.log(LogStatus.INFO, "Validate the response");
		filter_Id = userSpogServer.verifyUserFilters(response, filter_name, Search_string, user_is_blocked, user_status, role_id, is_default, count, SpogConstants.SUCCESS_POST, null, test);

		test.log(LogStatus.INFO, "Compose the expected response");
		expected_response = composeExpectedUserFilter(filter_Id, filter_name, user_id, organization_id, Search_string, user_is_blocked, user_status, role_id, is_default, 1);
		expectedresponse.add(expected_response);

		String RandomUeser_id=UUID.randomUUID().toString();
		test.log(LogStatus.INFO, "Get the user filter for specified user");
		userSpogServer.getUserFiltersForSpecificUserwithCheck(RandomUeser_id,"",ti.csr_token,expectedresponse,SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.USER_ID_DOESNOT_EXIST, test);

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




	/****************************************************************Generic Function***************************************************************************/
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