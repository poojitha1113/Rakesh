package api.users.userfilters;

import java.io.IOException;
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
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class DeleteUserFilterForSpecifiedUserTest extends base.prepare.Is4Org{

	private SPOGServer spogServer;
	private TestOrgInfo ti;
	private GatewayServer gatewayServer;
	private UserSpogServer userSpogServer;
	private SPOGDestinationServer spogDestinationServer;
	//public int Nooftest;
	private ExtentTest test;
	//used for test case count like passed,failed,remaining cases
	int passedcases=0;
	int failedcases=0;
	int skippedcases=0;
	String buildnumber=null;
	
    /*private ExtentReports rep;
    private SQLServerDb bqdb1;
    public int Nooftest;
    private long creationTime;
    private String BQName=null;
    private String runningMachine;
    private testcasescount count1;
    private String buildVersion;*/
	
	private String  org_model_prefix=this.getClass().getSimpleName();
	
	@BeforeClass
	@Parameters({ "baseURI", "port","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String logFolder,String runningMachine,String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		rep = ExtentManager.getInstance("DeleteUserFilterForSpecifiedUserTest", logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Kanamarlapudi, Chandra Kanth";
		
		Nooftest=0;
		//Used for creating a build number with dateformat
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

		ti = new TestOrgInfo(spogServer, test);
		
	}
	
	@DataProvider(name = "delete_user_filter_valid")
	public final Object[][] delete_user_filter_valid_200() {
		return new Object[][] {
			// different role id
				{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null,
						null, null,SpogConstants.DIRECT_ADMIN,"true",0 },
				{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null,
						null, null,SpogConstants.MSP_ADMIN,"true",0 },
				{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null,
						null, null,null,"true",0 },				
			//different email
				{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", ti.direct_org1_user1_email,
						null, null,null,"true",0 },
				{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", ti.root_msp_org1_user1_email,
						null, null,null,"true",0 },
				{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", ti.root_msp1_suborg1_user1_email,
						null, null,null,"true",0 },
				{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null,
						null, null,null,"true",0 },
			//different blocked value
				{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null,
						"true", null,null,"true",0 },
				{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null,
						"false", null,null,"true",0 },
				{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null,
						null, null,null,"true",0 },
			//different status
				{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null,
						null, "verified",null,"true",0 },
				{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null,
						null, "unverified",null,"true",0 },
				{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null,
						null, null,null,"true",0 },
			//different first name
				{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", RandomStringUtils.randomAlphanumeric(10)+"filterName",
						null, null,null,"true",0 },
				{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", RandomStringUtils.randomAlphanumeric(10)+"filterName",
						null, null,null,"true",0 },
				{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null,
						null, null,null,"true",0 },
			//different last name				
				{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", RandomStringUtils.randomAlphanumeric(10)+"filterName",
							null, null,null,"true",0 },
					{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", RandomStringUtils.randomAlphanumeric(10)+"filterName",
							null, null,null,"true",0 },
					{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null,
							null, null,null,"true",0 },
				
			//filter on firstname, status and blocked
				
				{ "msp", ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", RandomStringUtils.randomAlphanumeric(10)+"filterName",
						"true", "verified",null,"true",0 },
			//filter on last name and first name	
				{ "msp", ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", RandomStringUtils.randomAlphanumeric(10)+"filterName",
						null, null,null,"true",0 },
			//filter on role id, email	
				{ "msp", ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", ti.root_msp_org1_user1_email,
						null, null,SpogConstants.MSP_ADMIN,"true",0 },
			//filter on all
				{ "suborg", ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", RandomStringUtils.randomAlphanumeric(10)+"filterName",
						"true", "verified",SpogConstants.DIRECT_ADMIN,"true",0 },
				
				{ "csr", ti.csr_org_id,ti.csr_token,ti.csr_admin_user_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", RandomStringUtils.randomAlphanumeric(10)+"filterName",
							"true", "verified",SpogConstants.CSR_ADMIN,"true",0 },
				{ "csr_readonly", ti.csr_org_id,ti.csr_readonly_token,ti.csr_readonly_admin_user_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", RandomStringUtils.randomAlphanumeric(10)+"filterName",
								"true", "verified",SpogConstants.CSR_ADMIN,"true",0 },
		};
	}
	
	
	
	@Test(dataProvider = "delete_user_filter_valid",enabled=true)
	public void deleteUserFilterForSpecificUserValid_200(String organizationType,
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
		
		userSpogServer.setToken(validToken);
		test.log(LogStatus.INFO, "Create user filter in org of type "+organizationType);
		Response response = userSpogServer.createUserFilterForSpecificUser(user_id, filter_name, search_string, user_is_blocked, user_status, role_id, is_default, validToken, test);
		
		test.log(LogStatus.INFO, "Validate the response");
		filter_Id = userSpogServer.verifyUserFilters(response, filter_name, search_string, user_is_blocked, user_status, role_id, is_default, count, SpogConstants.SUCCESS_POST, null, test);
		
		test.log(LogStatus.INFO, "Delete the user filter in org of type: "+organizationType);
		userSpogServer.deleteUserFilterByUserIdWithCheck(validToken, user_id, filter_Id, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		
		if(organizationType=="suborg") {
			test.log(LogStatus.INFO, "Create user filter in org of type "+organizationType + " using msp token");
			response = userSpogServer.createUserFilterForSpecificUser(user_id, filter_name+1, search_string, user_is_blocked, user_status, role_id, is_default, ti.root_msp_org1_user1_token, test);
			test.log(LogStatus.INFO, "Validate the response");
			filter_Id = userSpogServer.verifyUserFilters(response, filter_name+1, search_string, user_is_blocked, user_status, role_id, is_default, count, SpogConstants.SUCCESS_POST, null, test);
			
			test.log(LogStatus.INFO, "Delete the user filter in org of type: "+organizationType);
			userSpogServer.deleteUserFilterByUserIdWithCheck(validToken, user_id, filter_Id, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			
			test.log(LogStatus.INFO, "Create user filter in org of type "+organizationType + " using msp_account_admin token");
			response = userSpogServer.createUserFilterForSpecificUser(user_id, filter_name+1, search_string, user_is_blocked, user_status, role_id, is_default, ti.root_msp_org1_msp_accountadmin1_token, test);
			test.log(LogStatus.INFO, "Validate the response");
			filter_Id = userSpogServer.verifyUserFilters(response, filter_name+1, search_string, user_is_blocked, user_status, role_id, is_default, count, SpogConstants.SUCCESS_POST, null, test);
			
			test.log(LogStatus.INFO, "Delete the user filter in org of type: "+organizationType);
			userSpogServer.deleteUserFilterByUserIdWithCheck(validToken, user_id, filter_Id, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		}
		
		userSpogServer.setToken(validToken);
		test.log(LogStatus.INFO, "Create user filter in org of type "+organizationType);
		response = userSpogServer.createUserFilterForSpecificUser(user_id, filter_name, search_string, user_is_blocked, user_status, role_id, is_default, validToken, test);
		
		test.log(LogStatus.INFO, "Validate the response");
		filter_Id = userSpogServer.verifyUserFilters(response, filter_name, search_string, user_is_blocked, user_status, role_id, is_default, count, SpogConstants.SUCCESS_POST, null, test);
		
		test.log(LogStatus.INFO, "Delete the user filter in org of type: "+organizationType);
		userSpogServer.deleteUserFilterByUserIdWithCheck(ti.csr_token, user_id, filter_Id, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
	}
	
	@DataProvider(name = "delete_user_filter_401")
	public final Object[][] deleteUserFilter_401() {
		return new Object[][] {
			// different role id
				{ "csr", ti.csr_org_id,ti.csr_token,ti.csr_admin_user_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null,
					null, null,SpogConstants.CSR_ADMIN,"true",0 },
				{ "csr_readonly", ti.csr_org_id,ti.csr_readonly_token,ti.csr_readonly_admin_user_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null,
					null, null,SpogConstants.CSR_ADMIN,"true",0 },			
				{ "direct", ti.direct_org1_id,ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null,
						null, null,SpogConstants.DIRECT_ADMIN,"true",0 },
				{ "msp", ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", RandomStringUtils.randomAlphanumeric(10)+"filterName",
							"true", "verified",null,"true",0 },
				{ "suborg", ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", RandomStringUtils.randomAlphanumeric(10)+"filterName",
							"true", "verified",SpogConstants.DIRECT_ADMIN,"true",0 },
				{ "suborg", ti.root_msp1_suborg1_id,ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", RandomStringUtils.randomAlphanumeric(10)+"filterName",
								"true", "verified",SpogConstants.DIRECT_ADMIN,"true",0 },
		};
	}
	

	@Test(dataProvider = "delete_user_filter_401",enabled=true)
	public void deleteUserFilterForSpecificUser_400(String organizationType,
													 String organization_id,
													 String validToken,
													 String user_id,
													 String filter_name,
													 String search_string,
													 String user_is_blocked,
													 String user_status,
													 String role_id, 
													 String is_default,
													 int count) {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		String filter_Id = null;
		
		userSpogServer.setToken(validToken);
		test.log(LogStatus.INFO, "Create user filter in org of type "+organizationType);
		Response response = userSpogServer.createUserFilterForSpecificUser(user_id, filter_name, search_string, user_is_blocked, user_status, role_id, is_default, validToken, test);
		
		test.log(LogStatus.INFO, "Validate the response");
		filter_Id = userSpogServer.verifyUserFilters(response, filter_name, search_string, user_is_blocked, user_status, role_id, is_default, count, SpogConstants.SUCCESS_POST, null, test);
		
		test.log(LogStatus.INFO, "Delete user filter in org of type "+organizationType+" with user id as null");
		userSpogServer.deleteUserFilterByUserIdWithCheck(validToken, null, filter_Id, SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_USERID_IS_NOT_UUID, test);
		
		test.log(LogStatus.INFO, "Delete user filter in org of type "+organizationType+" with filter id as null");
		userSpogServer.deleteUserFilterByUserIdWithCheck(validToken, user_id, null, SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_FILTERID_IS_NOT_UUID, test);
		
		test.log(LogStatus.INFO, "Delete the user filter in org of type: "+organizationType);
		userSpogServer.deleteUserFilterByUserIdWithCheck(validToken, user_id, filter_Id, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
	}
	
	@Test(dataProvider = "delete_user_filter_401",enabled=true)
	public void deleteUserFilterForSpecificUser_401(String organizationType,
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

		test.log(LogStatus.INFO, "Create user filter in org of type "+organizationType + " using missing token");
		userSpogServer.deleteUserFilterByUserIdWithCheck("", user_id, filter_Id, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);
		
		test.log(LogStatus.INFO, "Create user filter in org of type "+organizationType + " using invalid token");
		userSpogServer.deleteUserFilterByUserIdWithCheck(validToken+"j", user_id, filter_Id, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);				
	}
	
	@DataProvider(name = "delete_user_filter_403")
	public final Object[][] deleteUserFilter_403() {
		return new Object[][] {
			// different role id
				{ "direct-mspT", ti.direct_org1_id,ti.direct_org1_user1_token,ti.root_msp_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null,
						null, null,SpogConstants.DIRECT_ADMIN,"true",0 },
				{ "direct-suborgT", ti.direct_org1_id,ti.direct_org1_user1_token,ti.root_msp1_suborg1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null,
							null, null,SpogConstants.DIRECT_ADMIN,"true",0 },
				{ "direct-rootmspT", ti.direct_org1_id,ti.direct_org1_user1_token,ti.root_msp1_submsp1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null,
								null, null,SpogConstants.DIRECT_ADMIN,"true",0 },
				{ "direct-submspsuborgT", ti.direct_org1_id,ti.direct_org1_user1_token,ti.msp1_submsp1_suborg1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null,
									null, null,SpogConstants.DIRECT_ADMIN,"true",0 },
				
				{ "msp-diretT", ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.direct_org1_user1_token,ti.root_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", RandomStringUtils.randomAlphanumeric(10)+"filterName",
							"true", "verified",null,"true",0 },
				{ "msp-suborgT", ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_user1_token,ti.root_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", RandomStringUtils.randomAlphanumeric(10)+"filterName",
								"true", "verified",null,"true",0 },
				{ "msp-submspT", ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.root_msp1_submsp1_user1_token,ti.root_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", RandomStringUtils.randomAlphanumeric(10)+"filterName",
									"true", "verified",null,"true",0 },
				{ "msp-submspsuborgT", ti.root_msp_org1_id,ti.root_msp_org1_user1_token,ti.msp1_submsp1_suborg1_user1_token,ti.root_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", RandomStringUtils.randomAlphanumeric(10)+"filterName",
										"true", "verified",null,"true",0 },
				
				{ "submsp-diretT", ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,ti.direct_org1_user1_token,ti.root_msp1_submsp1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", RandomStringUtils.randomAlphanumeric(10)+"filterName",
											"true", "verified",null,"true",0 },
				{ "submsp-suborgT", ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,ti.root_msp1_suborg1_user1_token,ti.root_msp1_submsp1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", RandomStringUtils.randomAlphanumeric(10)+"filterName",
												"true", "verified",null,"true",0 },
				{ "submsp-submsp2T", ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp2_user1_token,ti.root_msp1_submsp1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", RandomStringUtils.randomAlphanumeric(10)+"filterName",
													"true", "verified",null,"true",0 },
				{ "submsp-submspsuborgT", ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,ti.root_msp1_suborg1_user1_token,ti.root_msp1_submsp1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", RandomStringUtils.randomAlphanumeric(10)+"filterName",
														"true", "verified",null,"true",0 },
								
				{ "suborg-directT", ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.direct_org1_user1_token,ti.root_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", RandomStringUtils.randomAlphanumeric(10)+"filterName",
							"true", "verified",SpogConstants.DIRECT_ADMIN,"true",0 },
				{ "suborg-suborgbT", ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg2_user1_token,ti.root_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", RandomStringUtils.randomAlphanumeric(10)+"filterName",
								"true", "verified",SpogConstants.DIRECT_ADMIN,"true",0 },
				{ "suborgb-mspAccAdminT", ti.root_msp1_suborg2_id,ti.root_msp1_suborg2_user1_token,ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp1_suborg2_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", RandomStringUtils.randomAlphanumeric(10)+"filterName",
									"true", "verified",SpogConstants.DIRECT_ADMIN,"true",0 },
				{ "suborg-submspT", ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.root_msp1_submsp1_user1_token,ti.root_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", RandomStringUtils.randomAlphanumeric(10)+"filterName",
										"true", "verified",SpogConstants.DIRECT_ADMIN,"true",0 },
				{ "suborg-submspsuborgT", ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,ti.msp1_submsp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", RandomStringUtils.randomAlphanumeric(10)+"filterName",
											"true", "verified",SpogConstants.DIRECT_ADMIN,"true",0 },
		};
	}
	@Test(dataProvider = "delete_user_filter_403",enabled=true)
	public void deleteUserFilterForSpecificUser_403(String organizationType,
													 String organization_id,
													 String validToken,
													 String otherOrgtoken,
													 String user_id,
													 String filter_name,
													 String search_string,
													 String user_is_blocked,
													 String user_status,
													 String role_id, 
													 String is_default,
													 int count) {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		String filter_Id = null;
		SpogMessageCode expectedmessagecode = null;
		userSpogServer.setToken(validToken);
		test.log(LogStatus.INFO, "Create user filter in org of type "+organizationType);
		Response response = userSpogServer.createUserFilterForSpecificUser(user_id, filter_name, search_string, user_is_blocked, user_status, role_id, is_default, validToken, test);
		
		test.log(LogStatus.INFO, "Validate the response");
		filter_Id = userSpogServer.verifyUserFilters(response, filter_name, search_string, user_is_blocked, user_status, role_id, is_default, count, SpogConstants.SUCCESS_POST, null, test);
		
		if(organizationType.contains("msp-")) {
			expectedmessagecode = SpogMessageCode.DIRECT_ADMIN_CANNOT_VIEW_MSP_CSR;
		}else {
			expectedmessagecode = SpogMessageCode.LOGIN_USER_ORG_NOT_SAME_AS_GET_USER;
		}
		expectedmessagecode = SpogMessageCode.RESOURCE_PERMISSION_DENY;
		test.log(LogStatus.INFO, "Create user filter in org of type "+organizationType + " using otherOrg token");
		userSpogServer.deleteUserFilterByUserIdWithCheck(otherOrgtoken, user_id, filter_Id, SpogConstants.INSUFFICIENT_PERMISSIONS, expectedmessagecode, test);
		
		test.log(LogStatus.INFO, "Delete the user filter in org of type: "+organizationType);
		userSpogServer.deleteUserFilterByUserIdWithCheck(validToken, user_id, filter_Id, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
	}
	
	@Test(dataProvider = "delete_user_filter_401",enabled=true)
	public void deleteUserFilterForSpecificUser_404(String organizationType,
													 String organization_id,
													 String validToken,
													 String user_id,
													 String filter_name,
													 String search_string,
													 String user_is_blocked,
													 String user_status,
													 String role_id, 
													 String is_default,
													 int count) {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		String filter_Id = null;
		
		userSpogServer.setToken(validToken);
		test.log(LogStatus.INFO, "Create user filter in org of type "+organizationType);
		Response response = userSpogServer.createUserFilterForSpecificUser(user_id, filter_name, search_string, user_is_blocked, user_status, role_id, is_default, validToken, test);
		
		test.log(LogStatus.INFO, "Validate the response");
		filter_Id = userSpogServer.verifyUserFilters(response, filter_name, search_string, user_is_blocked, user_status, role_id, is_default, count, SpogConstants.SUCCESS_POST, null, test);
		
			
		test.log(LogStatus.INFO, "Delete user filter in org of type "+organizationType+" with random user id ");
		userSpogServer.deleteUserFilterByUserIdWithCheck(validToken, UUID.randomUUID().toString(), filter_Id, SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.USER_ID_DOESNOT_EXIST, test);
		
		test.log(LogStatus.INFO, "Delete user filter in org of type "+organizationType+" with random filter id ");
		userSpogServer.deleteUserFilterByUserIdWithCheck(validToken, user_id, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.USER_FILTER_NOT_FOUND_WITH_USER_ID, test);
					
		test.log(LogStatus.INFO, "Delete the user filter in org of type: "+organizationType);
		userSpogServer.deleteUserFilterByUserIdWithCheck(validToken, user_id, filter_Id, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		//delete and again delete
		test.log(LogStatus.INFO, "Delete the user filter in org of type: "+organizationType);
		userSpogServer.deleteUserFilterByUserIdWithCheck(validToken, user_id, filter_Id, SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.USER_FILTER_NOT_FOUND_WITH_USER_ID, test);
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
