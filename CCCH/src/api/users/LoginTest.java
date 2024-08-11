package api.users;

import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.ErrorCode;
import Constants.SpogConstants;
import InvokerServer.Org4SPOGServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import InvokerServer.GatewayServer.siteType;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import invoker.SiteTestHelper;
import io.restassured.response.Response;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;

public class LoginTest extends base.prepare.PrepareOrgInfo{

	 @Parameters({ "pmfKey"})
  public LoginTest(String pmfKey) {
		super(pmfKey);
		// TODO Auto-generated constructor stub
	}



private SPOGServer spogServer;
  private UserSpogServer userSpogServer;
  private Org4SPOGServer org4SpogServer;
  private String csrAdminUserName;
  private String csrAdminPassword;
  private String csrReadOnlyAdminUserName;
  private String csrReadOnlyAdminPassword;
  private ExtentTest test;
  //this is for update portal, each testng class is taken as BQ set
/*  private SQLServerDb bqdb1;
  public int Nooftest;
  private long creationTime;
  private String BQName=null;
  private String runningMachine;
  private testcasescount count1;
  private String buildVersion;
  private ExtentReports rep;*/

  private String postfix_email = "@arcserve.com";
  private String common_password = "Welcome*02";
  
  private String prefix_direct = "spogqa_shuo_direct";
  private String direct_org_name = prefix_direct + "_org";
  private String direct_org_id=null;
  private String direct_user_name = prefix_direct + "_admin";
  private String direct_user_name_email = direct_user_name + postfix_email;
  private String direct_user_first_name = direct_user_name + "_first_name";
  private String direct_user_last_name = direct_user_name + "_last_name";
  private String final_direct_user_name_email = null;

  
  
  private String prefix_msp = "spogqa_shuo_msp";
  private String msp_org_name = prefix_msp + "_org";
  private String msp_user_name = prefix_msp + "_admin";
  private String msp_user_name_email = prefix_msp + "_admin" + postfix_email;
  private String msp_user_first_name = msp_user_name + "_first_name";
  private String msp_user_last_name = msp_user_name + "_last_name";
  private String msp_org_id=null;
  private String final_msp_user_name_email=null;	  

  private String prefix_msp_account = "spogqa_shuo_msp_account";
  private String msp_account_admin_name = prefix_msp_account + "_admin";
  private String msp_account_admin_email =msp_account_admin_name + postfix_email;
  private String msp_account_admin_first_name = msp_account_admin_name + "_first_name";
  private String msp_account_admin_last_name = msp_account_admin_name + "_last_name";
  private String msp_account_admin_id;
  private String final_msp_account_admin_email;

  
  
  private String account_id;
  private String account_user_email;
	private String direct_user_id;
	private String msp_user_id;
	private String account_user_id;
	private String  org_model_prefix=this.getClass().getSimpleName();

	
  @BeforeClass
  @Parameters({ "baseURI", "port","logFolder","buildVersion" ,"csrAdminUserName", "csrAdminPassword", "csrReadOnlyAdminUserName","csrReadOnlyAdminPassword"})
  public void beforeClass(String baseURI, String port, String logFolder,  String buildVersion,  String adminUserName, String adminPassword, String csrROAdminUserName, String csrROPwd ) throws UnknownHostException {
	  spogServer = new SPOGServer(baseURI, port);
	  userSpogServer = new UserSpogServer(baseURI, port);
	  org4SpogServer = new Org4SPOGServer(baseURI, port);
	  this.csrAdminUserName = adminUserName;
	  this.csrAdminPassword = adminPassword;
	  this.csrReadOnlyAdminUserName = csrROAdminUserName;
	  this.csrReadOnlyAdminPassword = csrROPwd;
	  rep = ExtentManager.getInstance("LoginTest", logFolder);
	  test = rep.startTest("beforeClass");
  
	  	//this is for update portal
	  	 this.BQName = this.getClass().getSimpleName();
	        String author = "Shuo.Zhang";
	        this.runningMachine =  InetAddress.getLocalHost().getHostName();
	        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
	        java.util.Date date=new java.util.Date();
	        this.buildVersion=buildVersion+"_"+dateFormater.format(date);
	        Nooftest=0;
	        bqdb1 = new SQLServerDb();
	        count1 = new testcasescount();
	        if(count1.isstarttimehit()==0) {
	            System.out.println("Into get loggedInUserById");
	            creationTime=System.currentTimeMillis();
	            count1.setcreationtime(creationTime);
	            //creationTime = System.currentTimeMillis();
	            try {
                    bqdb1.updateTable(this.BQName, runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0", String.valueOf(Nooftest), count1.getcreationtime(), "InProgress",author+" and Rest server is "+baseURI.split("//")[1]);
	            } catch (ClientProtocolException e) {
	                  // TODO Auto-generated catch block
	                  e.printStackTrace();
	            } catch (IOException e) {
	                  // TODO Auto-generated catch block
	                  e.printStackTrace();
	            }
	        }  
	        
	        prepareEnv();
	    	prepare(baseURI, port, logFolder, this.csrAdminUserName,  this.csrAdminPassword, this.getClass().getSimpleName() );
  }
  

  private void prepareEnv(){
	
	  	spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
		//*******************create direct org,user,site**********************/
		String prefix = RandomStringUtils.randomAlphanumeric(8)+org_model_prefix;
		test.log(LogStatus.INFO,"create a direct org");
		direct_org_id = spogServer.CreateOrganizationWithCheck(prefix+direct_org_name , SpogConstants.DIRECT_ORG, null, null, null, null, test);
		final_direct_user_name_email = prefix + direct_user_name_email;
		
		test.log(LogStatus.INFO,"create a admin under direct org");
		direct_user_id = spogServer.createUserAndCheck(final_direct_user_name_email, common_password, prefix + direct_user_first_name, prefix + direct_user_last_name, SpogConstants.DIRECT_ADMIN, direct_org_id, test);
		spogServer.userLogin(final_direct_user_name_email, common_password);
	  	

		//************************create msp org,user,site*************************************
		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
		String csrToken = spogServer.getJWTToken();
		/*test.log(LogStatus.INFO,"Creating csr readonly user ");
		csr_read_only_mail= "shuo_csr_read_only@arcserve.com";
		spogServer.createUserAndCheck(csr_read_only_mail, common_password, prefix + "_first_name_csr", prefix + "_last_name_csr", SpogConstants.CSR_READ_ONLY_ADMIN, "11111111-2222-3333-1111-222222222222", test);
		*/
		
		prefix =RandomStringUtils.randomAlphanumeric(8)+org_model_prefix;
		test.log(LogStatus.INFO,"create a msp org");
		msp_org_id = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name , SpogConstants.MSP_ORG, null, null, null, null, test);
		final_msp_user_name_email = prefix + msp_user_name_email;
		
		test.log(LogStatus.INFO,"create a admin under msp org");
		msp_user_id = spogServer.createUserAndCheck(final_msp_user_name_email, common_password, prefix + msp_user_first_name, prefix + msp_user_last_name, SpogConstants.MSP_ADMIN, msp_org_id, test);
		spogServer.userLogin(final_msp_user_name_email, common_password);
	  	
		test.log(LogStatus.INFO,"create a msp account admin under msp org");
		final_msp_account_admin_email = prefix + this.msp_account_admin_email;
		msp_account_admin_id = spogServer.createUserAndCheck(final_msp_account_admin_email, common_password, prefix + msp_account_admin_first_name, prefix + msp_account_admin_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, msp_org_id, test);
		
		
		//create account, account user and site
		test.log(LogStatus.INFO,"Creating a account For msp org");
		account_id = spogServer.createAccountWithCheck(msp_org_id, "sub_" + prefix + msp_org_name, "", test);
		prefix = RandomStringUtils.randomAlphanumeric(8);
	
		test.log(LogStatus.INFO,"Creating a account user For account org");
		account_user_email = prefix + msp_user_name_email;
		account_user_id = spogServer.createUserAndCheck(account_user_email, common_password, prefix + msp_user_first_name, prefix + msp_user_last_name, SpogConstants.DIRECT_ADMIN, account_id, test);
		spogServer.userLogin(account_user_email, common_password);

	 


  }
  
  
  /**
   * 6. csr admin can login
   * @author shuo.zhang
   * @param userName
   * @param password
   */


  @DataProvider(name = "userInfo")
	public final Object[][] getUserInfo() {
		return new Object[][] { 	{csrAdminUserName, csrAdminPassword},
									{csrReadOnlyAdminUserName, csrReadOnlyAdminPassword},
									{final_root_msp_user_name_email, common_password},
									{final_root_msp_account_admin_user_name_email, common_password},
									{final_sub_msp1_user_name_email, common_password},
									{final_sub_msp1_msp_account_user_name_email, common_password},
									{final_root_msp_account_admin_user_name_email, common_password},
									{final_sub_msp1_account2_user_email, common_password},
									
			
		};
	}		  
  @Test(dataProvider = "userInfo", enabled=true)
  public void loginWithCorrectInfo(String userName, String password) {
	 // http://xiang-gitlab:9080/api/users/login
	  test = rep.startTest("loginWithCorrectInfo");
	  test.assignAuthor("shuo.zhang");
	  spogServer.errorHandle.printInfoMessageInDebugFile("/****************loginWithCorrectInfo**************/");
	  Response response = spogServer.login(userName, password, test);
	  spogServer.checkLogin(response, SpogConstants.SUCCESS_LOGIN, "", test);
  }
 
  /**
   * @testcase 2. Verify it can't login with invalid username and password.
   *  @author shuo.zhang
   * @param userName
   */
  @Test(enabled=true)
  @Parameters({ "csrAdminUserName" })
  public void loginWithInvalidInfo(String userName) {
	  test = rep.startTest("loginWithInvalidInfo");
	  test.assignAuthor("shuo.zhang");
	  spogServer.errorHandle.printInfoMessageInDebugFile("/****************loginWithInvalidInfo**************/");
	  Response response = spogServer.login(userName, "9ol,", test);
	  spogServer.checkLogin(response, SpogConstants.NOT_LOGGED_IN, ErrorCode.PASSWORD_INCORRECT,test);
  }
 
  /**
   * @testcase:3. Verify it can't login with one not-existing username and password.
   *  @author shuo.zhang
   */
  @Test(enabled=true)
  public void loginWithNonExistingInfo() {
	  test = rep.startTest("loginWithNonExistingInfo");
	  test.assignAuthor("shuo.zhang");
	  spogServer.errorHandle.printInfoMessageInDebugFile("/****************loginWithNonExistingInfo**************/");
	  Response response = spogServer.login("baby", "9ol,", test);
	  spogServer.checkLogin(response, SpogConstants.NOT_LOGGED_IN, ErrorCode.USER_NOT_EXISTS ,test );
  }
 
  //
  /**
   * @testcase:5. User upper case/lower case or mixed cases are all allowed to login.
   *  @author shuo.zhang
   * @param userName
   * @param password
   */
  @Test(enabled=true)
  @Parameters({ "csrAdminUserName", "csrAdminPassword" })
  public void loginWithDiffentCase(String userName, String password) {
	 // http://xiang-gitlab:9080/api/users/login
	  test = rep.startTest("loginWithDiffentCase");
	  test.assignAuthor("shuo.zhang");
	  spogServer.errorHandle.printInfoMessageInDebugFile("/****************loginWithDiffentCase**************/");
	  Response response = spogServer.login(userName.toUpperCase(), password, test);
	  spogServer.checkLogin(response, SpogConstants.SUCCESS_LOGIN, "", test);
	  
	  response = spogServer.login(userName.toLowerCase(), password, test);
	  spogServer.checkLogin(response, SpogConstants.SUCCESS_LOGIN, "", test);
  }
  
  
  @DataProvider(name = "organizationAndUserInfo1")
	public final Object[][] getOrganizationAndUserInfo1() {
		return new Object[][] { 	{direct_org_id, prefix_direct,null,direct_user_first_name, direct_user_last_name,  SpogConstants.DIRECT_ADMIN, 
			"unverified", SpogConstants.NOT_LOGGED_IN, ErrorCode.AUTHORIZATION_HEADER_BLANK},
									{direct_org_id, prefix_direct,common_password,direct_user_first_name, direct_user_last_name,  SpogConstants.DIRECT_ADMIN, 
				"verified", SpogConstants.SUCCESS_LOGIN, null},
									{direct_org_id, prefix_direct,common_password,direct_user_first_name, direct_user_last_name,  SpogConstants.DIRECT_ADMIN, 
				"reset", SpogConstants.NOT_LOGGED_IN, ErrorCode.USER_IS_UNVERIFIED},
			
		};
	}		  
  
  /**
   * 1. block user login when status is unverified
   * 2. user could login when status is verified
   * 3. block user login when blocked is true
   * 4. user could login when blocked is false
   * 5. block user login when status is reset
   * 
   * @param organization_id
   * @param userPrefix
   * @param userPassword
   * @param userFirstName
   * @param userLastName
   * @param role_id
   * @param expectStatusCode
   * @param expectedErrorCode
   */
  @Test(dataProvider = "organizationAndUserInfo1", enabled=true)
  public void blockUserLoginWithInvalidStatus(  String organization_id, String userPrefix,  String userPassword, String userFirstName, String userLastName, String role_id, 
		 String status, int expectStatusCode, String expectedErrorCode ) {
	
	  test = rep.startTest("blockUserLoginWithInvalidStatus");
	  test.assignAuthor("shuo.zhang");
	  spogServer.errorHandle.printInfoMessageInDebugFile("/****************blockUserLoginWithInvalidStatus**************/");
	  spogServer.userLogin(csrAdminUserName, csrAdminPassword,test);
	  
	  test.log(LogStatus.INFO,"create user");
	  String adminEmail = userPrefix + "_" + RandomStringUtils.randomAlphanumeric(8)+ postfix_email;	
	  
	  Response response =  spogServer.createUser(adminEmail, userPassword,  userFirstName, userLastName, role_id, organization_id, test);
	
	  String user_id = null;
	  if(status.equalsIgnoreCase("reset")){
		   user_id = spogServer.checkCreateUser(response, SpogConstants.SUCCESS_POST, adminEmail, userFirstName, userLastName, role_id, organization_id,
				  null, "verified", expectedErrorCode, test);
		   userSpogServer.resetPasswordWithCheck(user_id, spogServer.getJWTToken(), SpogConstants.SUCCESS_GET_PUT_DELETE, null, test)	;	
		   
		 							
		  
	  }else{
		   user_id = spogServer.checkCreateUser(response, SpogConstants.SUCCESS_POST, adminEmail, userFirstName, userLastName, role_id, organization_id,
				  null, status, expectedErrorCode, test);
	  }
	  
	  test.log(LogStatus.INFO,"check user login");
	  response = spogServer.login(adminEmail, userPassword, test);
	  spogServer.checkLogin(response, expectStatusCode,expectedErrorCode, test);
	  
	  spogServer.userLogin(csrAdminUserName, csrAdminPassword,test);
	  spogServer.updateUserByIdForBlockedStatus(user_id, "true", spogServer.getJWTToken(), test);
	  
	  if(userPassword!=null){
		  test.log(LogStatus.INFO,"check user login");
		  response = spogServer.login(adminEmail, userPassword, test);
		  spogServer.checkLogin(response, SpogConstants.NOT_LOGGED_IN, ErrorCode.USER_IS_BLOCKED, test);
	  }

	  
	
  }
  
  @DataProvider(name = "organizationAndUserInfo2")
	public final Object[][] getOrganizationAndUserInfo2() {
		return new Object[][] { 	{this.final_direct_user_name_email, common_password, direct_org_id},
			{this.final_msp_user_name_email, common_password, msp_org_id},
			{this.account_user_email, common_password, account_id},
			{this.final_msp_account_admin_email, common_password, msp_org_id},
			
		};
	}		  
  
  
  @Test(dataProvider = "organizationAndUserInfo2", enabled=true)
  /**
   * 1. direct admin can not login the blocked organization
   * 2. msp admin can not login the blocked organization
   * 3. msp account admin can not login the blocked organization
   * 4. account admin can not login the blocked organization
   * 
   * @param userName
   * @param password
   * @param org_id
   */
  public void userCannotLoginBlockedOrg(String userName, String password, String org_id){
	  
	  test = rep.startTest("userCannotLoginBlockedOrg");
	  test.assignAuthor("shuo.zhang");
	  
	  spogServer.userLogin(csrAdminUserName, csrAdminPassword,test);
	  test.log(LogStatus.INFO,"update org with block status");
	  spogServer.updateOrganizationInfoByID(org_id, spogServer.getOrganizationNameByID(org_id), "true", test);
	  
	  Response response  =spogServer.login(userName, password, test);
	  spogServer.checkLogin(response,  SpogConstants.NOT_LOGGED_IN, ErrorCode.ORG_IS_BLCOKED);
	  
  }

  @AfterMethod
 public void getResult(ITestResult result){
    if(result.getStatus() == ITestResult.FAILURE){
        count1.setfailedcount();            
        test.log(LogStatus.FAIL, "Test Case Failed is "+result.getName()+" with parameters as "+Arrays.asList(result.getParameters()) );
        test.log(LogStatus.FAIL, result.getThrowable().getMessage());
    }else if(result.getStatus() == ITestResult.SKIP){
        count1.setskippedcount();
        test.log(LogStatus.SKIP, "Test Case Skipped is "+result.getName());
    }else if(result.getStatus()==ITestResult.SUCCESS){
        count1.setpassedcount();
    }
    // ending test
    //endTest(logger) : It ends the current test and prepares to create HTML report
    rep.endTest(test);    
}



	  private void deleteEnv(){

			spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
			org4SpogServer.setToken(spogServer.getJWTToken());
			 test.log(LogStatus.INFO,"destroy org");
		 	 org4SpogServer.destroyOrganization(direct_org_id , test);

			 test.log(LogStatus.INFO,"destroy org");
		 	 org4SpogServer.destroyOrganization(msp_org_id , test);
			
	  }

}
