package api.users.userfilters;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
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
import invoker.SPOGDestinationInvoker;
import io.restassured.response.Response;

public class DeleteUserFilterForLoggedInUserTest extends base.prepare.Is4Org {


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
	
	LocalDate date = LocalDate.now();  
	LocalDate yesterday = date.minusDays(1);  
	LocalDate tomorrow = yesterday.plusDays(2);
	
	private String  org_model_prefix=this.getClass().getSimpleName();
	
	@BeforeClass
	@Parameters({ "baseURI", "port","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String logFolder,String runningMachine,String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		rep = ExtentManager.getInstance("DeleteUserFilterForLoggedInUserTest", logFolder);
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
				{ "direct", ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null,
						null, null,SpogConstants.DIRECT_ADMIN,"true",0 },
				{ "direct", ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null,
						null, null,SpogConstants.MSP_ADMIN,"true",0 },
				{ "direct", ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null,
						null, null,null,"true",0 },
				
			//different email
				{ "direct", ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", ti.direct_org1_user1_email,
						null, null,null,"true",0 },
				{ "direct", ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", ti.root_msp_org1_user1_email,
						null, null,null,"true",0 },
				{ "direct", ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", ti.root_msp1_suborg1_user1_email,
						null, null,null,"true",0 },
				{ "direct", ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null,
						null, null,null,"true",0 },
			//different blocked value
				{ "direct", ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null,
						"true", null,null,"true",0 },
				{ "direct", ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null,
						"false", null,null,"true",0 },
				{ "direct", ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null,
						null, null,null,"true",0 },
			//different status
				{ "direct", ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null,
						null, "verified",null,"true",0 },
				{ "direct", ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null,
						null, "unverified",null,"true",0 },
				{ "direct", ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null,
						null, null,null,"true",0 },
			//different first name
				{ "direct", ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", RandomStringUtils.randomAlphanumeric(10)+"filterName",
						null, null,null,"true",0 },
				{ "direct", ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", RandomStringUtils.randomAlphanumeric(10)+"filterName",
						null, null,null,"true",0 },
				{ "direct", ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", null,
						null, null,null,"true",0 },
			//different last name
				
				{ "direct", ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", RandomStringUtils.randomAlphanumeric(10)+"filterName",
						null, null,null,"true",0 },
				{ "direct", ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", RandomStringUtils.randomAlphanumeric(10)+"filterName",
						null, null,null,"true",0 },
				{ "direct", ti.direct_org1_user1_token,ti.direct_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", RandomStringUtils.randomAlphanumeric(10)+"filterName",
						null, null,null,"true",0 },
				
			//filter on firstname, status and blocked
				
				{ "msp", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", RandomStringUtils.randomAlphanumeric(10)+"filterName",
						"true", "verified",null,"true",0 },
			//filter on last name and first name	
				/*{ "msp", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", RandomStringUtils.randomAlphanumeric(10)+"filterName",
						null, null,RandomStringUtils.randomAlphanumeric(10)+"filterName",null,null,"true",0 },*/
			//filter on role id, email	
				{ "msp", ti.root_msp_org1_user1_token,ti.root_msp_org1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", ti.root_msp_org1_user1_email,
						null, null,SpogConstants.MSP_ADMIN,"true",0 },
			//filter on all
				{ "suborg", ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_user1_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", RandomStringUtils.randomAlphanumeric(10)+"filterName",
						"true", "verified",SpogConstants.DIRECT_ADMIN,"true",0 },
				
				{ "csr", ti.csr_token,ti.csr_admin_user_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", RandomStringUtils.randomAlphanumeric(10)+"filterName",
							"true", "verified",SpogConstants.CSR_ADMIN,"true",0 },
				{ "csr_readonly", ti.csr_readonly_token,ti.csr_readonly_admin_user_id, RandomStringUtils.randomAlphanumeric(4)+"filterName", RandomStringUtils.randomAlphanumeric(10)+"filterName",
								"true", "verified",SpogConstants.CSR_ADMIN,"true",0 },
		};
	}
	
	
	
	@Test(dataProvider = "delete_user_filter_valid",enabled=true)
	public void deleteUserFilterForLoggedInUserValid_200(String organizationType,
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
		test.assignAuthor("Kanamarlapudi, Chandra Kanth");
		String filter_Id = null;
		
		userSpogServer.setToken(validToken);
		test.log(LogStatus.INFO, "Create user filter in org of type "+organizationType);
		Response response = userSpogServer.createUserFilterForLoggedInUser(filter_name, search_string, user_is_blocked, user_status, role_id, is_default, test);
				
		test.log(LogStatus.INFO, "Validate the response");
		filter_Id = userSpogServer.verifyUserFilters(response, filter_name, search_string, user_is_blocked, user_status, role_id, is_default, count, SpogConstants.SUCCESS_POST, null, test);
		
		userSpogServer.getUserFilterForLoggedInUser(filter_Id, validToken, test);
		
		test.log(LogStatus.INFO, "Delete the user filter in org of type: "+organizationType);
		userSpogServer.deleteUserFilterForLoggedInUserWithCheck(validToken, filter_Id, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		
		if(organizationType=="suborg") {
			userSpogServer.setToken(ti.root_msp_org1_msp_accountadmin1_token);
			test.log(LogStatus.INFO, "Create user filter in org of type "+organizationType + " using msp_account_admin token");
			response = userSpogServer.createUserFilterForLoggedInUser(filter_name, search_string, user_is_blocked, user_status, role_id, is_default, test);
			test.log(LogStatus.INFO, "Validate the response");
			filter_Id = userSpogServer.verifyUserFilters(response, filter_name, search_string, user_is_blocked, user_status, role_id, is_default, count, SpogConstants.SUCCESS_POST, null, test);
			
			test.log(LogStatus.INFO, "Delete the user filter in org of type: "+organizationType);
			userSpogServer.deleteUserFilterForLoggedInUserWithCheck(ti.root_msp_org1_msp_accountadmin1_token, filter_Id, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		}
	}
	
	@DataProvider(name = "delete_user_filter_401")
	public final Object[][] deleteUserFilter_401() {
		return new Object[][] {
			// different role id
				{ "csr", ti.csr_token, RandomStringUtils.randomAlphanumeric(4)+"filterName", null,
					null, null,SpogConstants.CSR_ADMIN,"true",0 },
				{ "csr_readonly", ti.csr_readonly_token, RandomStringUtils.randomAlphanumeric(4)+"filterName", null,
					null, null,SpogConstants.CSR_ADMIN,"true",0 },			
				{ "direct", ti.direct_org1_user1_token, RandomStringUtils.randomAlphanumeric(4)+"filterName", null,
						null, null,SpogConstants.DIRECT_ADMIN,"true",0 },
				{ "msp", ti.root_msp_org1_user1_token, RandomStringUtils.randomAlphanumeric(4)+"filterName", null,
							"true", "verified",null,"true",0 },
				{ "suborg", ti.root_msp1_suborg1_user1_token, RandomStringUtils.randomAlphanumeric(4)+"filterName", RandomStringUtils.randomAlphanumeric(10)+"filterName",
							"true", "verified",SpogConstants.DIRECT_ADMIN,"true",0 },
				{ "suborg", ti.root_msp_org1_msp_accountadmin1_token, RandomStringUtils.randomAlphanumeric(4)+"filterName", RandomStringUtils.randomAlphanumeric(10)+"filterName",
								"true", "verified",SpogConstants.DIRECT_ADMIN,"true",0 },
		};
	}
	

	@Test(dataProvider = "delete_user_filter_401",enabled=true)
	public void deleteUserFilterForLoggedInUser_400(String organizationType,
													 String validToken,
													 String filter_name,
													 String search_string,
													 String user_is_blocked,
													 String user_status,
													 String role_id, 
													 String is_default,
													 int count) {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread().getStackTrace()[1].getMethodName()+"_"+organizationType);
		test.assignAuthor("Kanamarlapudi, Chandra Kanth");
		String filter_Id = null;
		
		userSpogServer.setToken(validToken);
		test.log(LogStatus.INFO, "Create user filter in org of type "+organizationType);
		Response response = userSpogServer.createUserFilterForLoggedInUser(filter_name, search_string, user_is_blocked, user_status, role_id, is_default, test);
		
		test.log(LogStatus.INFO, "Validate the response");
		filter_Id = userSpogServer.verifyUserFilters(response, filter_name, search_string, user_is_blocked, user_status, role_id, is_default, count, SpogConstants.SUCCESS_POST, null, test);
		
		test.log(LogStatus.INFO, "Delete user filter in org of type "+organizationType+" with filter id as null");
		userSpogServer.deleteUserFilterForLoggedInUserWithCheck(validToken, null, SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_FILTERID_IS_NOT_UUID, test);
		
		test.log(LogStatus.INFO, "Delete the user filter in org of type: "+organizationType);
		userSpogServer.deleteUserFilterForLoggedInUserWithCheck(validToken, filter_Id, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
	}
	
	@Test(dataProvider = "delete_user_filter_401",enabled=true)
	public void deleteUserFilterForLoggedInUser_401(String organizationType,
													 String validToken,
													 String filter_name,
													 String search_string,
													 String user_is_blocked,
													 String user_status, 
													 String role_id, 
													 String is_default,
													 int count) 
	{
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		test.assignAuthor("Kanamarlapudi, Chandra Kanth");
		String filter_Id = null;

		test.log(LogStatus.INFO, "Create user filter in org of type "+organizationType + " using missing token");
		userSpogServer.deleteUserFilterForLoggedInUserWithCheck("", filter_Id, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);
		
		test.log(LogStatus.INFO, "Create user filter in org of type "+organizationType + " using invalid token");
		userSpogServer.deleteUserFilterForLoggedInUserWithCheck(validToken+"j", filter_Id, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);		
	}
	
	@Test(dataProvider = "delete_user_filter_401",enabled=true)
	public void deleteUserFilterForLoggedInUser_404(String organizationType,
													 String validToken,
													 String filter_name,
													 String search_string,
													 String user_is_blocked,
													 String user_status,
													 String role_id, 
													 String is_default,
													 int count) {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		test.assignAuthor("Kanamarlapudi, Chandra Kanth");
		
		String filter_Id = null;
		
		userSpogServer.setToken(validToken);
		
		test.log(LogStatus.INFO, "Create user filter in org of type "+organizationType);
		Response response = userSpogServer.createUserFilterForLoggedInUser(filter_name, search_string, user_is_blocked, user_status, role_id, is_default, test);
				
		test.log(LogStatus.INFO, "Validate the response");
		filter_Id = userSpogServer.verifyUserFilters(response, filter_name, search_string, user_is_blocked, user_status, role_id, is_default, count, SpogConstants.SUCCESS_POST, null, test);
		
		test.log(LogStatus.INFO, "Delete user filter in org of type "+organizationType+" with random filter id ");
		userSpogServer.deleteUserFilterForLoggedInUserWithCheck(validToken, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.FILTER_NOT_FOUND_WITH_USER_ID, test);
					
		test.log(LogStatus.INFO, "Delete the user filter in org of type: "+organizationType);
		userSpogServer.deleteUserFilterForLoggedInUserWithCheck(validToken, filter_Id, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
//		delete and again delete
		test.log(LogStatus.INFO, "Delete the user filter in org of type: "+organizationType);
		userSpogServer.deleteUserFilterForLoggedInUserWithCheck(validToken, filter_Id, SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.FILTER_NOT_FOUND_WITH_USER_ID, test);	
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
