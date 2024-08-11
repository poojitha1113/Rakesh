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

public class GetSpecifiedUserFilterForLoggedInUserTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private UserSpogServer userSpogServer;
	//public int Nooftest;
	//private ExtentReports rep;
	private ExtentTest test;
	private String common_password = "Mclaren@2013";
	private TestOrgInfo ti;

	LocalDate date = LocalDate.now();  
	LocalDate yesterday = date.minusDays(1);  
	LocalDate tomorrow = yesterday.plusDays(2);


	@BeforeClass
	@Parameters({ "baseURI", "port","csrAdminUserName", "csrAdminPassword","csrReadOnlyAdminName","csrReadOnlyAdminPassword","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword,String csrReadOnlyAdminName,String csrReadOnlyAdminPassword, String logFolder,String runningMachine,String buildVersion) throws UnknownHostException {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		rep = ExtentManager.getInstance("GetUserFilterForLoggedInUserTest", logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Sykam.malleswari";

		Nooftest=0;
		ti = new TestOrgInfo(spogServer, test);	//Used for creating a build number with dateformat
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date=new java.util.Date();
		this.buildVersion=buildVersion+"_"+dateFormater.format(date);

		BQName=this.getClass().getSimpleName();
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

			//Get User filters of normal sub msp Organization
			{ "submsp", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null ,
										"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "submsp", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "firstname" ,
											"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "submsp", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "lastname" ,
												"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "submsp", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName",ti.normal_msp1_suborg1_user1_email,
													"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "submsp", ti.normal_msp1_suborg1_id,ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName","@arcserve.com",
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

		};

	}

	@Test(dataProvider = "Get_user_filter_valid")
	public void GetUserFilterForLoggedInUserValid_200(String organizationType,
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

		userSpogServer.setToken(validToken);
		test.log(LogStatus.INFO, "Create user filter in org of type "+organizationType);
		Response response=userSpogServer.createUserFilterForLoggedInUser(filter_name, search_string, user_is_blocked, user_status, role_id, is_default, test);

		String filter_Id =userSpogServer.verifyUserFilters(response, filter_name, search_string, user_is_blocked, user_status, role_id, is_default, count, SpogConstants.SUCCESS_POST, null, test);

		test.log(LogStatus.INFO, "Get the user filter for specified user");
		response=userSpogServer.getUserFilterForLoggedInUser(filter_Id, validToken,test);

		userSpogServer.verifyUserFilters(response, filter_name, search_string, user_is_blocked, user_status, role_id, is_default, 1, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);


		//created filter with the userstaus and user_verified 

		String role_id_1=null;

		userSpogServer.setToken(validToken);
		test.log(LogStatus.INFO, "Create user filter in org of type "+organizationType);
		Response response1 = userSpogServer.createUserFilterForLoggedInUser(filter_name+"1", search_string, user_is_blocked, user_status,role_id_1, is_default, test);

		test.log(LogStatus.INFO, "Validate the response");
		String filter_Id_1 = userSpogServer.verifyUserFilters(response1, filter_name+"1", search_string, user_is_blocked, user_status, role_id_1, is_default, count, SpogConstants.SUCCESS_POST, null, test);

		test.log(LogStatus.INFO, "Get the user filter for specified user");
		response1=userSpogServer.getUserFilterForLoggedInUser(filter_Id_1, validToken,test);

		userSpogServer.verifyUserFilters(response1, filter_name+"1", search_string, user_is_blocked, user_status, role_id_1, is_default, 1, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);



		//created filter with the role_id and user_is_blocked 

		String user_status_1=null;

		userSpogServer.setToken(validToken);
		test.log(LogStatus.INFO, "Create user filter in org of type "+organizationType);
		Response response2 = userSpogServer.createUserFilterForLoggedInUser(filter_name+"2", search_string, user_is_blocked, user_status_1,role_id, is_default, test);

		test.log(LogStatus.INFO, "Validate the response");
		String filter_Id_2 = userSpogServer.verifyUserFilters(response2, filter_name+"2", search_string, user_is_blocked, user_status_1, role_id, is_default, count, SpogConstants.SUCCESS_POST, null, test);


		test.log(LogStatus.INFO, "Get the user filter for specified user");
		response2=userSpogServer.getUserFilterForLoggedInUser(filter_Id_2, validToken,test);

		userSpogServer.verifyUserFilters(response2,filter_name+"2", search_string, user_is_blocked, user_status_1, role_id, is_default, 1, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		//created filter with the role_id and user_status 

		String user_is_blocked_1=null;

		userSpogServer.setToken(validToken);
		test.log(LogStatus.INFO, "Create user filter in org of type "+organizationType);
		Response response3 = userSpogServer.createUserFilterForLoggedInUser(filter_name+"3", search_string, user_is_blocked_1, user_status,role_id, is_default, test);

		test.log(LogStatus.INFO, "Validate the response");
		String filter_Id_3 = userSpogServer.verifyUserFilters(response3, filter_name+"3", search_string, user_is_blocked_1, user_status, role_id, is_default, count, SpogConstants.SUCCESS_POST, null, test);

		test.log(LogStatus.INFO, "Get the user filter for specified user");
		response3=userSpogServer.getUserFilterForLoggedInUser(filter_Id_3, validToken,test);

		userSpogServer.verifyUserFilters(response3,filter_name+"3", search_string, user_is_blocked_1, user_status, role_id, is_default, 1, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);


		//created filter with  user_status 

		String user_is_blocked_2=null;
		String role_id_2=null;

		userSpogServer.setToken(validToken);
		test.log(LogStatus.INFO, "Create user filter in org of type "+organizationType);
		Response response4 = userSpogServer.createUserFilterForLoggedInUser(filter_name+"4", search_string, user_is_blocked_2, user_status,role_id_2, is_default, test);

		test.log(LogStatus.INFO, "Validate the response");
		String filter_Id_4 = userSpogServer.verifyUserFilters(response4, filter_name+"4", search_string, user_is_blocked_2, user_status, role_id_2, is_default, count, SpogConstants.SUCCESS_POST, null, test);

		test.log(LogStatus.INFO, "Get the user filter for specified user");
		response4=userSpogServer.getUserFilterForLoggedInUser(filter_Id_4, validToken,test);

		userSpogServer.verifyUserFilters(response4, filter_name+"4", search_string, user_is_blocked_2, user_status, role_id_2, is_default, 1, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);


		//created filter with  user_is_blocked  

		String user_status_3=null;
		String role_id_3=null;

		userSpogServer.setToken(validToken);
		test.log(LogStatus.INFO, "Create user filter in org of type "+organizationType);
		Response response5 = userSpogServer.createUserFilterForLoggedInUser(filter_name+"5", search_string, user_is_blocked, user_status_3,role_id_3, is_default, test);

		test.log(LogStatus.INFO, "Validate the response");
		String filter_Id_5 = userSpogServer.verifyUserFilters(response5, filter_name+"5", search_string, user_is_blocked, user_status_3, role_id_3, is_default, count, SpogConstants.SUCCESS_POST, null, test);

		test.log(LogStatus.INFO, "Get the user filter for specified user");
		response5=userSpogServer.getUserFilterForLoggedInUser(filter_Id_5, validToken,test);

		userSpogServer.verifyUserFilters(response5, filter_name+"5", search_string, user_is_blocked, user_status_3, role_id_3, is_default, 1, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);


		//created filter with role_id

		String user_status_4=null;
		String user_is_blocked_4=null;

		userSpogServer.setToken(validToken);
		test.log(LogStatus.INFO, "Create user filter in org of type "+organizationType);
		Response response6 = userSpogServer.createUserFilterForLoggedInUser(filter_name+"6", search_string, user_is_blocked_4, user_status_4,role_id, is_default, test);

		test.log(LogStatus.INFO, "Validate the response");
		String filter_Id_6 = userSpogServer.verifyUserFilters(response6, filter_name+"6", search_string, user_is_blocked_4, user_status_4, role_id, is_default, count, SpogConstants.SUCCESS_POST, null, test);

		test.log(LogStatus.INFO, "Get the user filter for specified user");
		response6=userSpogServer.getUserFilterForLoggedInUser(filter_Id_6, validToken,test);

		userSpogServer.verifyUserFilters(response6, filter_name+"6", search_string, user_is_blocked_4, user_status_4, role_id, is_default, 1, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		//created filter with invalid search_string 

		userSpogServer.setToken(validToken);
		test.log(LogStatus.INFO, "Create user filter in org of type "+organizationType);
		Response response7 = userSpogServer.createUserFilterForLoggedInUser(filter_name+"7", search_string+"junk", user_is_blocked_1, user_status,role_id, is_default, test);

		test.log(LogStatus.INFO, "Validate the response");
		String filter_Id_7 = userSpogServer.verifyUserFilters(response7, filter_name+"7", search_string+"junk", user_is_blocked_1, user_status, role_id, is_default, count, SpogConstants.SUCCESS_POST, null, test);

		test.log(LogStatus.INFO, "Get the user filter for specified user");
		response7=userSpogServer.getUserFilterForLoggedInUser(filter_Id_7, validToken,test);

		userSpogServer.verifyUserFilters(response7, filter_name+"7", search_string+"junk", user_is_blocked, user_status, role_id, is_default, 0, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);


		test.log(LogStatus.INFO, "Delete the user filter in org of type: "+organizationType);
		userSpogServer.deleteUserFilterForLoggedInUserWithCheck(validToken, filter_Id, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		test.log(LogStatus.INFO, "Delete the user filter in org of type: "+organizationType);
		userSpogServer.deleteUserFilterForLoggedInUserWithCheck(validToken, filter_Id_1, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		test.log(LogStatus.INFO, "Delete the user filter in org of type: "+organizationType);
		userSpogServer.deleteUserFilterForLoggedInUserWithCheck(validToken, filter_Id_2, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		test.log(LogStatus.INFO, "Delete the user filter in org of type: "+organizationType);
		userSpogServer.deleteUserFilterForLoggedInUserWithCheck(validToken, filter_Id_3, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		test.log(LogStatus.INFO, "Delete the user filter in org of type: "+organizationType);
		userSpogServer.deleteUserFilterForLoggedInUserWithCheck(validToken, filter_Id_4, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		test.log(LogStatus.INFO, "Delete the user filter in org of type: "+organizationType);
		userSpogServer.deleteUserFilterForLoggedInUserWithCheck(validToken, filter_Id_5, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		test.log(LogStatus.INFO, "Delete the user filter in org of type: "+organizationType);
		userSpogServer.deleteUserFilterForLoggedInUserWithCheck(validToken, filter_Id_6, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		test.log(LogStatus.INFO, "Delete the user filter in org of type: "+organizationType);
		userSpogServer.deleteUserFilterForLoggedInUserWithCheck(validToken, filter_Id_7, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);


	}

	@DataProvider(name = "create_user_filter_401")
	public final Object[][] createUserFilter_401() {
		return new Object[][] {
			// different role id
			{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
				"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },


		};
	}
	@Test(dataProvider = "create_user_filter_401")
	public void GetUserFilterForLoggedInUser_401(String organizationType,
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
		Response response = userSpogServer.createUserFilterForLoggedInUser(filter_name, search_string, user_is_blocked, user_status,role_id, is_default,test);

		filter_Id=userSpogServer.verifyUserFilters(response, filter_name, search_string, user_is_blocked, user_status, role_id, is_default, count, SpogConstants.SUCCESS_POST, null, test);

		test.log(LogStatus.INFO, "Get the user filter for specified user");
		Response Response1=userSpogServer.getUserFilterForLoggedInUser(filter_Id, validToken+"123",test);

		userSpogServer.verifyUserFilters(Response1, filter_name, search_string, user_is_blocked, user_status, role_id, is_default, 1, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);

		test.log(LogStatus.INFO, "Get the user filter for specified user");
		Response1=userSpogServer.getUserFilterForLoggedInUser(filter_Id, "",test);

		userSpogServer.verifyUserFilters(Response1, filter_name, search_string, user_is_blocked, user_status, role_id, is_default, 1, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED,test);

	}



	@DataProvider(name = "get_user_filter_404")
	public final Object[][] getUserFilter_404() {
		return new Object[][] {
			// different role id

			{ "Get Direct org user filters with another direct Org Token ",ti.direct_org1_user1_token,ti.direct_org2_user1_token,ti.direct_org1_user1_id,RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
				"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },

			//Get User filters of normal  msp Organization

			{ "Get Customer account user filters with the Direct user token",ti.normal_msp1_suborg1_user1_token,ti.direct_org1_user1_token,ti.normal_msp1_suborg1_user1_id,RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
					"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "Get Customer account user filters with the normal user token", ti.normal_msp1_suborg1_user1_token,ti.normal_msp_org2_user1_token,ti.normal_msp1_suborg1_user1_id,RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
						"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "Get Customer account user filters with the normal msp account admin user token", ti.normal_msp1_suborg1_user1_token,ti.normal_msp_org2_msp_accountadmin1_token,ti.normal_msp1_suborg1_user1_id ,RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
							"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "Get Customer account user filters with the customer account of another normal msp organzation",ti.normal_msp1_suborg1_user1_token, ti.normal_msp2_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id,RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
								"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "Get Customer account user filters with the root msp organzation",ti.normal_msp1_suborg1_user1_token, ti.root_msp1_submsp1_user1_token,ti.normal_msp1_suborg1_user1_id,RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
									"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "Get Customer account user filters with the sub msp's account admin", ti.normal_msp1_suborg1_user1_token,ti.root_msp1_submsp1_account_admin_token,ti.normal_msp1_suborg1_user1_id,RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
										"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },	
			{ "Get Customer account user filters with the sub msp's customer account",ti.normal_msp1_suborg1_user1_token,ti.msp1_submsp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id,RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
											"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },	
			{ "Get Customer account user filters with the root msp organzation",ti.normal_msp1_suborg1_user1_token,ti.root_msp_org1_user1_token,ti.normal_msp1_suborg1_user1_id,RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
												"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },	
			{ "Get Customer account user filters with the root msp  account adminorganzation", ti.normal_msp1_suborg1_user1_token,ti.root_msp_org1_msp_accountadmin1_token,ti.normal_msp1_suborg1_user1_id,RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
													"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },	
			{ "Get Customer account user filters with the root msp's cutstomer account  organzation",ti.normal_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_user1_id,RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
														"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },

			//Get User filters of root   msp Organization
			{ "Get Root msp account user filters with the Direct user token",ti.root_msp_org1_user1_token,ti.direct_org1_user1_token,ti.root_msp_org1_user1_id ,RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
															"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "Get Root msp account user filters with the normal user token",ti.root_msp_org1_user1_token,ti.normal_msp_org1_user1_token,ti.root_msp_org1_user1_id ,RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "Get Root msp account user filters with the normal msp account admin user token",ti.root_msp_org1_user1_token,ti.normal_msp_org1_msp_accountadmin1_token,ti.root_msp_org1_user1_id,RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																	"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "Get Root msp account user filters with the customer account of another normal msp organzation",ti.root_msp_org1_user1_token,ti.normal_msp2_suborg1_user1_token,ti.root_msp_org1_user1_id,RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																		"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "Get Root msp account user filters with the root msp organzation",ti.root_msp_org1_user1_token,ti.root_msp1_submsp1_user1_token,ti.root_msp_org1_user1_id,RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																			"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },	
			{ "Get Root msp account user filters with the sub msp's account admin",ti.root_msp_org1_user1_token, ti.root_msp1_submsp1_account_admin_token,ti.root_msp_org1_user1_id,RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																				"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },	
			{ "Get Root msp account user filters with the sub msp's customer account",ti.root_msp_org1_user1_token,ti.msp1_submsp1_suborg1_user1_token,ti.root_msp_org1_user1_id,RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																					"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },	
			{ "Get Root msp account user filters with the root msp organzation",ti.root_msp_org1_user1_token,ti.root_msp_org2_user1_token,ti.root_msp_org1_user1_id,RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																						"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "Get Root msp account user filters with the root msp  account adminorganzation",ti.root_msp_org1_user1_token, ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp_org1_user1_id ,RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																							"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },	
			{ "Get Root msp account user filters with the root msp's cutstomer account  organzation",ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_user1_token,ti.root_msp_org1_user1_id,RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																								"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },

			//Get User filters of normal   msp Organization
			{ "Get sub msp account user filters with the Direct user token",ti.root_msp1_submsp1_user1_token,ti.direct_org1_user1_token,ti.root_msp1_submsp1_user1_id ,RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																									"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "Get sub msp account user filters with the normal user token",ti.root_msp1_submsp1_user1_token, ti.normal_msp_org1_user1_token,ti.root_msp1_submsp1_user1_id,RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																										"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "Get sub msp account user filters with the normal msp account admin user token",ti.root_msp1_submsp1_user1_token,ti.normal_msp_org1_msp_accountadmin1_token,ti.root_msp1_submsp1_user1_id ,RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																											"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "Get sub msp account user filters with the customer account of another normal msp organzation", ti.root_msp1_submsp1_user1_token,ti.normal_msp2_suborg1_user1_token,ti.root_msp1_submsp1_user1_id ,RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																												"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },
			{ "Get sub msp account user filters with the root msp organzation",ti.root_msp1_submsp1_user1_token,ti.root_msp2_submsp1_user1_token,ti.root_msp1_submsp1_user1_id ,RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																													"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },	
			{ "Get sub msp account user filters with the sub msp's account admin",ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp1_account_admin_token,ti.root_msp1_submsp1_user1_id ,RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																														"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },	
			{ "Get sub msp account user filters with the sub msp's customer account",ti.root_msp1_submsp1_user1_token,ti.msp1_submsp1_suborg1_user1_token,ti.root_msp1_submsp1_user1_id ,RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																															"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },	
			{ "Get sub msp account user filters with the root msp organzation",ti.root_msp1_submsp1_user1_token,ti.root_msp_org2_user1_token,ti.root_msp1_submsp1_user1_id ,RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																																"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },	
			{ "Get sub msp account user filters with the root msp  account adminorganzation",ti.root_msp1_submsp1_user1_token,ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp1_submsp1_user1_id ,RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																																	"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },	
			{ "Get sub msp account user filters with the root msp's cutstomer account  organzation",ti.root_msp1_submsp1_user1_token, ti.root_msp1_suborg1_user1_token,ti.root_msp1_submsp1_user1_id ,RandomStringUtils.randomAlphanumeric(4)+"filterName", "@arcserve.com",
																																		"false","verified",SpogConstants.DIRECT_ADMIN,"true",0 },	


		};
	}

	@Test(dataProvider = "get_user_filter_404")
	public void GetUserFilterForLoggedInUser_404(String testcase,
			String validToken,
			String Othertoken,
			String user_id,
			String filter_name,
			String search_string,
			String user_is_blocked,
			String user_status, 
			String role_id, 
			String is_default,
			int count) 
	{
		
		String randomFilterId= UUID.randomUUID().toString();
		userSpogServer.setToken(Othertoken);
		userSpogServer.setToken(validToken);
		test.log(LogStatus.INFO, "Create user filter in org of type ");
		Response response = userSpogServer.createUserFilterForLoggedInUser(filter_name, search_string, user_is_blocked, user_status, role_id, is_default,test);

		String filter_Id=userSpogServer.verifyUserFilters(response, filter_name, search_string, user_is_blocked, user_status, role_id, is_default, count, SpogConstants.SUCCESS_POST, null, test);

		String random_filter_id=UUID.randomUUID().toString();
		test.log(LogStatus.INFO, "Get the user filter for specified user");

		response=userSpogServer.getUserFilterForLoggedInUser(random_filter_id, validToken,test);

		userSpogServer.verifyUserFilters(response, filter_name, search_string, user_is_blocked, user_status, role_id, is_default, 0, SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.USER_FILTER_NOT_FOUND_WITH_USER_ID, test);

		userSpogServer.setToken(Othertoken);
		Response response_1=userSpogServer.getUserFilterForLoggedInUser(filter_Id, Othertoken,test);

		userSpogServer.verifyUserFilters(response_1, filter_name, search_string, user_is_blocked, user_status, role_id, is_default, 0, SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.USER_FILTER_NOT_FOUND_WITH_USER_ID, test);

		 response=userSpogServer.getUserFilterForLoggedInUser(randomFilterId, Othertoken,test);

		userSpogServer.verifyUserFilters(response, filter_name, "@arcserve.com", "false", "verified", SpogConstants.DIRECT_ADMIN, "true", 0, SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.USER_FILTER_NOT_FOUND_WITH_USER_ID, test);
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





}