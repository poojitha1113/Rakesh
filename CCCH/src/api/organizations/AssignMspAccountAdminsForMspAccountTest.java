package api.organizations;
import java.io.IOException;
import java.util.Arrays;
import java.text.SimpleDateFormat;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.SpogConstants;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import io.restassured.response.Response;

import genericutil.SQLServerDb;
import genericutil.testcasescount;

public class AssignMspAccountAdminsForMspAccountTest extends base.prepare.PrepareOrgInfo {
	@Parameters({ "pmfKey"})
	  public AssignMspAccountAdminsForMspAccountTest(String pmfKey) {
			super(pmfKey);
			// TODO Auto-generated constructor stub
		}
	private SPOGServer spogServer;
	private UserSpogServer userSpogServer;
	  private String csrOrg_id;
	  private String directOrg_id;
	  private String directOrg2_id;
	  private String mspOrg_id;
	  private String mspOrg2_id;
	  private String account_id;
	  private String account2_id;
//	  private ExtentReports rep;
	  private ExtentTest test;
	  private Response response;
	  private String csrGlobalLoginUser;
	  private String csrGlobalLoginPassword;
	  private String csrReadOnlyUser = "liuyu05@arcserve.com";
	  private String sharePassword = "Caworld_2018";
	  private String csr_email;
	  private String direct_email;
	  private String msp_email;
	  private String msp2_email;
	  private String msp_account_admin;
	  private String accountDirect_email;
	  private String csr_token;
	  private String csr_readonly_token;
	  private String msp_token;
	  private String msp2_token;
	  private String msp_account_admin_token;
	  private String direct_token;
	  private String account_token;
	  private String csr_user_id;
	  private String direct_user_id;
	  private String msp_user_id;
	  private String account_user_id;
	  private String  org_prefix=this.getClass().getSimpleName();
	  //this is for update portal, each testng class is taken as BQ set
//	  private SQLServerDb bqdb1;
//	  public int Nooftest;
//	  private long creationTime;
//	  private String BQName=null;
//	  private String runningMachine;
//	  private testcasescount count1;
//	  private String buildVersion;
	  //end
	  
	  @BeforeClass
	  @Parameters({ "baseURI", "port" , "csrAdminUserName", "csrAdminPassword","logFolder","runningMachine", "buildVersion"})
	  public void beforeClass(String baseURI, String port, String userName, String password, String logFolder, String runningMachine, String buildVersion) {
		  rep = ExtentManager.getInstance("AssignMspAccountAdminsForMspAccountTest",logFolder);
		  test = rep.startTest("beforeClass");
		  spogServer = new SPOGServer(baseURI, port);
		  userSpogServer =new UserSpogServer(baseURI, port);
		  
		  spogServer.userLogin(this.csrReadOnlyUser, sharePassword);
		  this.csr_readonly_token = spogServer.getJWTToken();
		  
		  spogServer.userLogin(userName, password);  
		  this.csr_token = spogServer.getJWTToken();
		  
		  this.csrGlobalLoginUser = userName;
		  this.csrGlobalLoginPassword = password;
		  csrOrg_id = spogServer.GetLoggedinUserOrganizationID();
		  directOrg_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_update_direct1_yuefen")+org_prefix,"direct",spogServer.ReturnRandom("spogqa_direct1_update@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("direct_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
//		  directOrg2_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_update_direct2_yuefen"),"direct",spogServer.ReturnRandom("spogqa_direct2_update@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("direct_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  mspOrg_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_update_yuefen_msp1")+org_prefix,"msp",spogServer.ReturnRandom("spogqa_update_msp1@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("direct_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  mspOrg2_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_update_yuefen_msp2")+org_prefix,"msp",spogServer.ReturnRandom("spogqa_update_msp2@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("direct_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  account_id = spogServer.createAccountWithCheck(mspOrg_id,spogServer.ReturnRandom("accoun1_msp1_yuefen_spogqa")+org_prefix, mspOrg_id, test);
		  account2_id = spogServer.createAccountWithCheck(mspOrg_id, spogServer.ReturnRandom("accoun2_msp1_yuefen_spogqa")+org_prefix, mspOrg_id, test);
//		  //create csr user
//		  this.csr_email = spogServer.ReturnRandom("direct_yuefen_update@spogqa.com");
//		  csr_user_id =spogServer.createUserAndCheck(csr_email, sharePassword, "yuefen", "liu", "csr_admin", csrOrg_id, test);
		  //create direct user
		  System.out.println("direct user");
		  this.direct_email = spogServer.ReturnRandom("direct_yuefen_update@spogqa.com");
		  direct_user_id = spogServer.createUserAndCheck(direct_email, sharePassword, "yuefen", "liu", "direct_admin", directOrg_id, test);
		  spogServer.userLogin(direct_email, sharePassword);
		  this.direct_token = spogServer.getJWTToken();
		  //create msp1 user
		  System.out.println("msp1 user");
		  spogServer.setToken(csr_token);
		  this.msp_email = spogServer.ReturnRandom("msp1_yuefen_update@spogqa.com");
		  msp_user_id =spogServer.createUserAndCheck(msp_email, sharePassword, "yuefen", "liu", "msp_admin", mspOrg_id, test);
		  spogServer.userLogin(msp_email, sharePassword);
		  this.msp_token = spogServer.getJWTToken();
		  // create msp2 user
		  System.out.println("msp2 user");
		  spogServer.setToken(csr_token);
		  this.msp2_email = spogServer.ReturnRandom("msp2_yuefen_update@spogqa.com");
		  spogServer.createUserAndCheck(msp2_email, sharePassword, "yuefen", "liu", "msp_admin", mspOrg2_id, test);
		  spogServer.userLogin(msp2_email, sharePassword);
		  this.msp2_token = spogServer.getJWTToken();
		  //create msp account admin
		  System.out.println("msp account admin user");
		  spogServer.setToken(csr_token);
		  this.msp_account_admin = spogServer.ReturnRandom("msp_account_admin_yuefen@spogqa.com");
		  spogServer.createUserAndCheck(msp_account_admin, sharePassword, "yuefen", "liu", "msp_account_admin", mspOrg_id, test);
		  spogServer.userLogin(msp_account_admin, sharePassword);
		  this.msp_account_admin_token = spogServer.getJWTToken();
		  //create account user
		  System.out.println("account user");
		  spogServer.setToken(csr_token);
		  this.accountDirect_email = spogServer.ReturnRandom("account1_msp1_yuefen_update@spogqa.com");
		  account_user_id=spogServer.createUserAndCheck(accountDirect_email, sharePassword, "yuefen", "liu", "direct_admin", account_id, test);
		  spogServer.userLogin(accountDirect_email, sharePassword);
		  this.account_token = spogServer.getJWTToken();
		  
		  //this is for update portal
          this.BQName = this.getClass().getSimpleName();
          String author = "Yuefen.liu";
          this.runningMachine = runningMachine;
          SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
          java.util.Date date=new java.util.Date();
          this.buildVersion=buildVersion+"_"+dateFormater.format(date);
          Nooftest=0;
          bqdb1 = new SQLServerDb();
          count1 = new testcasescount();
          if(count1.isstarttimehit()==0) {
        	  System.out.println("Into AssignMspAccountAdminsForMspAccountTest");
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
      //end
          prepare(baseURI, port, logFolder, userName, password, this.getClass().getSimpleName() );
          setEnv(baseURI,  port,  userName, password);
	  }
	
	  @DataProvider(name = "UserInfo100")
	  public final Object[][] getUserInfo100() {
		  return new Object[][] { 
			          {this.direct_email, sharePassword, this.root_msp_org_id, this.root_msp_direct_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101"},
			          {this.direct_email, sharePassword, this.sub_msp1_org_id, this.sub_msp1_account1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101"},
			          {this.msp_email, sharePassword, this.root_msp_org_id, this.root_msp_direct_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101"},
			          {this.msp_email, sharePassword, this.sub_msp1_org_id, this.sub_msp1_account1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101"},
					  
			          {this.final_root_msp_user_name_email, this.common_password, this.mspOrg_id, this.account_id, SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101"}, 
			          {this.final_sub_msp1_user_name_email, this.common_password, this.mspOrg_id, this.account_id, SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101"}, 
			          }; 
		}
	  @Test (dataProvider = "UserInfo100")
	  //Error Code:00100101 Message:"There is not permission to manage the resource for current user!" 
	  public void assignMspAdminsRootMspRelated(String loginUser, String loginPassword, String parentOrgId, String childOrgId, int expectedStatusCode, String expectedErrorCode) {
		  System.out.println("assignMspAdminsRootMspRelated");
		  test = rep.startTest("assignMspAdminsRootMspRelated");
		  test.assignAuthor("Liu Yuefen");
          
		  spogServer.setToken(csr_token);
		  
		  int num =2;
		  String[] userIds = new String[num];
		  for (int i=0;i<num;i++) {
			  String email = spogServer.ReturnRandom("yuefen_mspAccountAdmin_")+i+"@spogqa.com";
			  userIds[i] = spogServer.createUserAndCheck(email, sharePassword, "yuefen", "liu", "msp_account_admin", parentOrgId, test);
			  
		  }
		  //login
		  spogServer.userLogin(loginUser, loginPassword);
		  String token = spogServer.getJWTToken();
		  Response response = userSpogServer.assignMspAccountAdmins(parentOrgId, childOrgId, userIds, token);
		  spogServer.checkResponseStatus(response, expectedStatusCode, test);
		  spogServer.checkErrorCode(response, expectedErrorCode);
		  
		  
		  spogServer.setToken(csr_token);
		  for (int i=0;i<num;i++) {
			  spogServer.DeleteUserById(userIds[i], test);
			  
		  }
          
	  }
	  
	  @Test 
	  public void csrReadOnlyUserAssignMspAdminsFail() {
		  System.out.println("csrReadOnlyUserAssignMspAdminsFail");
		  test = rep.startTest("csrReadOnlyUserAssignMspAdminsFail");
		  test.assignAuthor("Liu Yuefen");
          
		  spogServer.setToken(csr_token);
		  System.out.println("token:"+csr_token);
		  
		  int num =2;
		  String[] userIds = new String[num];
		  for (int i=0;i<num;i++) {
			  String email = spogServer.ReturnRandom("yuefen_mspAccountAdmin_")+i+"@spogqa.com";
			  userIds[i] = spogServer.createUserAndCheck(email, sharePassword, "yuefen", "liu", "msp_account_admin", mspOrg_id, test);
		  }
		  
		  //change to csr readonly token

		  Response response = userSpogServer.assignMspAccountAdmins(mspOrg_id, account_id, userIds, csr_readonly_token);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS);
		  spogServer.checkErrorCode(response,"00100101");
		  
		  //delete
		  spogServer.setToken(csr_token);
		  for (int i=0;i<num;i++) {
			  spogServer.DeleteUserById(userIds[i], test);
			  
		  } 
	  }
	  
	  @DataProvider(name = "UserInfo")
	  public final Object[][] getUserInfo() {
		  return new Object[][] { 
					  {this.csrGlobalLoginUser, this.csrGlobalLoginPassword}, 
					  {this.msp_email,this.sharePassword}
			          }; 
		}
	  @Test (dataProvider = "UserInfo")
	  public void assignMspAdminsSuccessful(String userName, String password) {
		  System.out.println("assignMspAdminsSuccessful");
		  test = rep.startTest("assignMspAdminsSuccessful");
		  test.assignAuthor("Liu Yuefen");
          
		  spogServer.userLogin(userName, password);
		  String token = spogServer.getJWTToken();
				  
		  int num =2;
		  String[] userIds = new String[num];
		  for (int i=0;i<num;i++) {
			  String email = spogServer.ReturnRandom("yuefen_mspAccountAdmin_")+i+"@spogqa.com";
			  userIds[i] = spogServer.createUserAndCheck(email, sharePassword, "yuefen", "liu", "msp_account_admin", mspOrg_id, test);
		  }

		  Response response = userSpogServer.assignMspAccountAdmins(mspOrg_id, account_id, userIds, token);
		  userSpogServer.assignMspAccountAdminsWithCheck(response, userIds, token);
		  
		  //delete
		  for (int i=0;i<num;i++) {
			  spogServer.DeleteUserById(userIds[i], test);
			  
		  } 
	  }
	  
	  @DataProvider(name = "UserInfo1")
	  public final Object[][] getUserInfo1() {
		  return new Object[][] { 
			          {direct_token, SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101"},
					  {msp_account_admin_token, SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101"}, 
					  {msp2_token, SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101"}, 
					  {account_token, SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101"}, 
			          }; 
		}
	  @Test (dataProvider = "UserInfo1")
	  //Error Code:00100101 Message:"There is not permission to manage the resource for current user!" 
	  public void assignMspAdminsFail(String token, int expectedStatusCode, String expectedErrorCode) {
		  System.out.println("assignMspAdminsFail");
		  test = rep.startTest("assignMspAdminsFail");
		  test.assignAuthor("Liu Yuefen");
          
		  spogServer.setToken(csr_token);
		  
		  int num =2;
		  String[] userIds = new String[num];
		  for (int i=0;i<num;i++) {
			  String email = spogServer.ReturnRandom("yuefen_mspAccountAdmin_")+i+"@spogqa.com";
			  userIds[i] = spogServer.createUserAndCheck(email, sharePassword, "yuefen", "liu", "msp_account_admin", mspOrg_id, test);
			  
		  }
		  
		  Response response = userSpogServer.assignMspAccountAdmins(mspOrg_id, account_id, userIds, token);
		  spogServer.checkResponseStatus(response, expectedStatusCode, test);
		  spogServer.checkErrorCode(response, expectedErrorCode);
		  
		  
		  for (int i=0;i<num;i++) {
			  spogServer.DeleteUserById(userIds[i], test);
			  
		  }
          
	  }
	  
	  
	  @Test 
	  //Error Code:00200001 Message:"Unauthorized: The user doesn't exist or the status is not active."  
	  public void assignMspAdminsFail2() {
		  System.out.println("assignMspAdminsFail2");
		  test = rep.startTest("assignMspAdminsFail2");
		  test.assignAuthor("Liu Yuefen");
          
		  spogServer.setToken(csr_token);
		  
		  int num =2;
		  String[] userIds = new String[num];
		  for (int i=0;i<num;i++) {
			  String email = spogServer.ReturnRandom("yuefen_mspAccountAdmin_")+i+"@spogqa.com";
			  userIds[i] = spogServer.createUserAndCheck(email, sharePassword, "yuefen", "liu", "msp_account_admin", mspOrg_id, test);
			  spogServer.DeleteUserById(userIds[i], test);
			  
		  }
		  
		  Response response = userSpogServer.assignMspAccountAdmins(mspOrg_id, account_id, userIds, csr_token);
		  spogServer.checkResponseStatus(response, SpogConstants.RESOURCE_NOT_EXIST, test);
		  spogServer.checkErrorCode(response, "00200007");   
	  }
	  
	  @DataProvider(name = "UserInfo2")
	  public final Object[][] getUserInfo2() {
		  return new Object[][] { 
			          {spogServer.returnRandomUUID(), spogServer.returnRandomUUID(), SpogConstants.RESOURCE_NOT_EXIST,"0030000A"},
			          {"", "", SpogConstants.RESOURCE_NOT_EXIST,"00900001"},
					  {mspOrg2_id, account_id, SpogConstants.RESOURCE_NOT_EXIST, "0030000A"}, 
					  {directOrg_id, account_id, SpogConstants.RESOURCE_NOT_EXIST, "0030000A"},
					  {csrOrg_id, account_id, SpogConstants.RESOURCE_NOT_EXIST, "0030000A"},
					  {account_id, account_id, SpogConstants.RESOURCE_NOT_EXIST, "0030000A"},
					  {mspOrg_id, mspOrg_id, SpogConstants.RESOURCE_NOT_EXIST, "0030000A"},
					  {mspOrg_id, directOrg_id, SpogConstants.RESOURCE_NOT_EXIST, "0030000A"},
			          }; 
		}
	  @Test(dataProvider = "UserInfo2")
	  //Error Code:0030000A Message:"The organization [{0}] is not found or has been removed."  
	  public void assignMspAdminsFail3(String parentOrg, String childOrg, int expectedStatusCode, String expectedErrorCode) {
		  System.out.println("assignMspAdminsFail3");
		  test = rep.startTest("assignMspAdminsFail3");
		  test.assignAuthor("Liu Yuefen");
          
		  spogServer.setToken(csr_token);
		  
		  int num =2;
		  String[] userIds = new String[num];
		  for (int i=0;i<num;i++) {
			  String email = spogServer.ReturnRandom("yuefen_mspAccountAdmin_")+i+"@spogqa.com";
			  userIds[i] = spogServer.createUserAndCheck(email, sharePassword, "yuefen", "liu", "msp_account_admin", mspOrg_id, test);
			  
		  }
		  
		  Response response = userSpogServer.assignMspAccountAdmins(parentOrg, childOrg, userIds, csr_token);
		  spogServer.checkResponseStatus(response, expectedStatusCode);
		  spogServer.checkErrorCode(response, expectedErrorCode);   
		  
		  		  for (int i=0;i<num;i++) {
			  spogServer.DeleteUserById(userIds[i], test);
			  
		  }
		  
	  }
	  
	  @Test 
	  //Error Code:"00300017" Message:"MSP account admin [{0}] does not belong to [{1}] MSP organization." 
	  //assign msp2 msp account admins to msp1 sub org
	  public void assignMspAdminsFail4() {
		  System.out.println("assignMspAdminsFail4");
		  test = rep.startTest("assignMspAdminsFail4");
		  test.assignAuthor("Liu Yuefen");
          
		  spogServer.setToken(csr_token);
		  
		  int num =2;
		  String[] userIds = new String[num];
		  for (int i=0;i<num;i++) {
			  String email = spogServer.ReturnRandom("yuefen_mspAccountAdmin_")+i+"@spogqa.com";
			  userIds[i] = spogServer.createUserAndCheck(email, sharePassword, "yuefen", "liu", "msp_account_admin", mspOrg2_id, test);
			  
		  }
		
		  Response response = userSpogServer.assignMspAccountAdmins(mspOrg_id, account_id, userIds, csr_token);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
		  spogServer.checkErrorCode(response, "00300017");   
		  
  		  for (int i=0;i<num;i++) {
  			  spogServer.DeleteUserById(userIds[i], test);
  		  }	    
	  }
	  
	  @DataProvider(name = "UserInfo3")
	  public final Object[][] getUserInfo3() {
		  return new Object[][] { 
			          {csr_user_id, SpogConstants.REQUIRED_INFO_NOT_EXIST,"00100007"},
			          {direct_user_id, SpogConstants.REQUIRED_INFO_NOT_EXIST,"00300011"},
			          {msp_user_id, SpogConstants.REQUIRED_INFO_NOT_EXIST,"00300011"},
			          {account_user_id, SpogConstants.REQUIRED_INFO_NOT_EXIST,"00300011"}
			          }; 
		}
	  @Test(dataProvider = "UserInfo3")
	  //Error Code:00300011 Message:"Invalid parameter combination for assigning MSP account admin to msp_child organization [{0}]." 
	  public void assignMspAdminsFail5(String userId,  int expectedStatusCode, String expectedErrorCode) {
		  System.out.println("assignMspAdminsFail5");
		  test = rep.startTest("assignMspAdminsFail5");
		  test.assignAuthor("Liu Yuefen");
          
		  spogServer.setToken(csr_token);
		  
		  String[] userIds = new String[1];
          userIds[0] = userId;
		  
		  Response response = userSpogServer.assignMspAccountAdmins(mspOrg_id, account_id, userIds, csr_token);
		  spogServer.checkResponseStatus(response, expectedStatusCode);
		  spogServer.checkErrorCode(response, expectedErrorCode);   
	  }
			  
	  @Test 
	  //Error Code:00900005 Message:"This is a bad request." 
	  public void assignMspAdminsFail6() {
		  System.out.println("assignMspAdminsFail6");
		  test = rep.startTest("assignMspAdminsFail6");
		  test.assignAuthor("Liu Yuefen");
          
		  spogServer.setToken(csr_token);
		  
		  int num =2;
		  String[] userIds = new String[num];
		  
		  String email = spogServer.ReturnRandom("yuefen_mspAccountAdmin@spogqa.com");
		  userIds[0] = spogServer.createUserAndCheck(email, sharePassword, "yuefen", "liu", "msp_account_admin", mspOrg_id, test);
		  userIds[1] = userIds[0];
			 
		  Response response = userSpogServer.assignMspAccountAdmins(mspOrg_id, account_id, userIds, csr_token);
		  spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
		  spogServer.checkErrorCode(response, "00300014");   
		  
  		  spogServer.DeleteUserById(userIds[0], test);
  		     
	  }
	  
	  @Test 
	  //"code": "00300012","message": "MSP account admin [] already exist for [] msp_child Organization.",
	  public void assignMspAdminsTwice() {
		  System.out.println("assignMspAdminsTwice");
		  test = rep.startTest("assignMspAdminsTwice");
		  test.assignAuthor("Liu Yuefen");
          
		  spogServer.setToken(csr_token);
		  
		  String[] userIds = new String[1];
		  
		  String email = spogServer.ReturnRandom("yuefen_mspAccountAdmin_1@spogqa.com");
		  userIds[0]= spogServer.createUserAndCheck(email, sharePassword, "yuefen", "liu", "msp_account_admin", mspOrg_id, test);

		  Response response = userSpogServer.assignMspAccountAdmins(mspOrg_id, account_id, userIds, csr_token);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		  
		  response = userSpogServer.assignMspAccountAdmins(mspOrg_id, account_id, userIds, csr_token);
		  spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST);
		  spogServer.checkErrorCode(response, "00300012");
	
		  //delete
		  spogServer.DeleteUserById(userIds[0], test);
	  }
	  @Test 
	  //assing the same msp account admin to multiple sub org.
	  public void assignMspAdminsToMultipleSubOrg() {
		  System.out.println("assignMspAdminsToMultipleSubOrg");
		  test = rep.startTest("assignMspAdminsToMultipleSubOrg");
		  test.assignAuthor("Liu Yuefen");
          
		  spogServer.setToken(csr_token);
		  
		  String[] userIds = new String[1];
		  
		  String email = spogServer.ReturnRandom("yuefen_mspAccountAdmin_1@spogqa.com");
		  userIds[0]= spogServer.createUserAndCheck(email, sharePassword, "yuefen", "liu", "msp_account_admin", mspOrg_id, test);

		  Response response = userSpogServer.assignMspAccountAdmins(mspOrg_id, account_id, userIds, csr_token);
		  userSpogServer.assignMspAccountAdminsWithCheck(response, userIds, csr_token);
		  
		  response = userSpogServer.assignMspAccountAdmins(mspOrg_id, account2_id, userIds, csr_token);
		  userSpogServer.assignMspAccountAdminsWithCheck(response, userIds, csr_token);
	
		  //delete
		  spogServer.DeleteUserById(userIds[0], test);
	  }
	  
	  @Test 
	  public void assignBlockedMspAdmins() {
		  System.out.println("assignBlockedMspAdmins");
		  test = rep.startTest("assignBlockedMspAdmins");
		  test.assignAuthor("Liu Yuefen");
          
		  spogServer.setToken(csr_token);
		  //create
		  String[] userIds = new String[1];
		  String email = spogServer.ReturnRandom("yuefen_mspAccountAdmin_1@spogqa.com");
		  String user_id = spogServer.createUserAndCheck(email, sharePassword, "yuefen", "liu", "msp_account_admin", mspOrg_id, test);
		  userIds[0] = user_id;
		  //update status to blocked
		  response = spogServer.updateUserByIdForBlockedStatus(user_id, "true", csr_token, test);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  //assign
		  Response response = userSpogServer.assignMspAccountAdmins(mspOrg_id, account_id, userIds, csr_token);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS);
		  
		  //delete
		  spogServer.DeleteUserById(userIds[0], test);

	  }
	  
	  @DataProvider(name = "UserInfo4")
	  public final Object[][] getUserInfo4() {
		  return new Object[][] { 
			          {null},
			          {spogServer.returnRandomUUID()},
			          {""},
			          }; 
		}
	  @Test (dataProvider = "UserInfo4")
	  //invoke api without login
	  public void assignMspAdminsFail401(String token) {
		  System.out.println("assignMspAdminsFail401");
		  test = rep.startTest("assignMspAdminsFail401");
		  test.assignAuthor("Liu Yuefen");
          
		  spogServer.setToken(csr_token);
		  
		  int num =1;
		  String[] userIds = new String[num];
		  
		  String email = spogServer.ReturnRandom("yuefen_mspAccountAdmin@spogqa.com");
		  userIds[0] = spogServer.createUserAndCheck(email, sharePassword, "yuefen", "liu", "msp_account_admin", mspOrg_id, test);
		  
		  Response response = userSpogServer.assignMspAccountAdmins(mspOrg_id, account_id, userIds, token);
		  spogServer.checkResponseStatus(response, SpogConstants.NOT_LOGGED_IN, test);
		  spogServer.checkErrorCode(response, "00900006"); 
		  //delete user
		  spogServer.setToken(csr_token);
  		  spogServer.DeleteUserById(userIds[0], test);     
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
  
}
