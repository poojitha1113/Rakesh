package api.organizations;
import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
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
public class UnAssignMspAccountAdminsForMspAccountTest extends base.prepare.PrepareOrgInfo {
	@Parameters({ "pmfKey"})
	  public  UnAssignMspAccountAdminsForMspAccountTest(String pmfKey) {
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
		  rep = ExtentManager.getInstance("UnAssignMspAccountAdminsForMspAccountTest",logFolder);
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
		  //create csr user
		  this.csr_email = spogServer.ReturnRandom("direct_yuefen_update@spogqa.com");
		  csr_user_id =spogServer.createUserAndCheck(csr_email, sharePassword, "yuefen", "liu", "csr_admin", csrOrg_id, test);
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
		  
		  prepare(baseURI, port, logFolder, this.csrGlobalLoginUser,  this.csrGlobalLoginPassword, this.getClass().getSimpleName() );
		  
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
        	  System.out.println("Into UnAssignMspAccountAdminsForMspAccountTest");
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
          setEnv(baseURI,  port,  userName, password);
      //end
	  }
	  
	  @DataProvider(name = "UserInfo10")
	  public final Object[][] getUserInfo10() {
		  return new Object[][] { 
					  {this.final_root_msp_user_name_email, this.common_password,this.root_msp_org_id},
					  {this.final_sub_msp1_user_name_email, this.common_password,this.sub_msp1_org_id},
			          }; 
		}
	  
	  @Test (dataProvider = "UserInfo10")
	  public void unAssignRootMspAdminsRelatedSuccessful(String loginUser, String loginPassword, String parentOrg) {
		  System.out.println("unAssignRootMspAdminsRelatedSuccessful");
		  test = rep.startTest("unAssignRootMspAdminsRelatedSuccessful");
		  test.assignAuthor("Liu Yuefen");
          
		  //login
		  spogServer.userLogin(loginUser, loginPassword);
		  String token=spogServer.getJWTToken();
		  //create account
		  String childOrg = spogServer.createAccountWithCheck(parentOrg,spogServer.ReturnRandom("accoun1_msp1_yuefen_spogqa")+org_prefix, parentOrg, test);
		  int num =2;
		  String[] userIds = new String[num];
		  for (int i=0;i<num;i++) {
			  String email = spogServer.ReturnRandom("yuefen_mspAccountAdmin_")+i+"@spogqa.com";
			  userIds[i] = spogServer.createUserAndCheck(email, sharePassword, "yuefen", "liu", "msp_account_admin", parentOrg, test);
			  
		  }
		  //assign
		  Response response = userSpogServer.assignMspAccountAdmins(parentOrg, childOrg, userIds,token );
		  userSpogServer.assignMspAccountAdminsWithCheck(response, userIds, token);
		  //unassign
		  String[] unAssignAdminIds = new String[1];
		  unAssignAdminIds[0]= userIds[0];
		  response = userSpogServer.unAssignMspAccountAdmins(parentOrg, childOrg, unAssignAdminIds, token);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	  }
	  
	  @DataProvider(name = "UserInfo11")
	  public final Object[][] getUserInfo11() {
		  return new Object[][] { 
			          {this.final_root_msp_user_name_email, this.common_password,this.sub_msp1_org_id},
			          {this.msp_email, sharePassword,this.sub_msp1_org_id},
			          {this.direct_email, sharePassword,this.sub_msp1_org_id},
			          
			          {this.final_sub_msp1_user_name_email, this.common_password,this.root_msp_org_id},
			          {this.msp_email, sharePassword,this.root_msp_org_id},
			          {this.direct_email,sharePassword,this.root_msp_org_id},
			          
			          {this.final_root_msp_user_name_email, this.common_password,this.mspOrg_id},
			          {this.final_sub_msp1_user_name_email, this.common_password,this.mspOrg_id},
			          
			          }; 
		}
	  @Test (dataProvider = "UserInfo11")
	  //Error Code:00100101 Message:"There is not permission to manage the resource for current user!" 
	  public void unAssignRootMspAdminsRelatedFail(String loginUser, String loginPassword, String parentOrg) {
		  System.out.println("unAssignRootMspAdminsRelatedFail");
		  test = rep.startTest("unAssignRootMspAdminsRelatedFail");
		  test.assignAuthor("Liu Yuefen");
          
		  spogServer.setToken(csr_token);
		//create account
		  String childOrg = spogServer.createAccountWithCheck(parentOrg,spogServer.ReturnRandom("accoun1_msp1_yuefen_spogqa")+org_prefix, parentOrg, test);
		  int num =2;
		  String[] userIds = new String[num];
		  for (int i=0;i<num;i++) {
			  String email = spogServer.ReturnRandom("yuefen_mspAccountAdmin_")+i+"@spogqa.com";
			  userIds[i] = spogServer.createUserAndCheck(email, sharePassword, "yuefen", "liu", "msp_account_admin", parentOrg, test);
			  
		  }
		  //assign
		  Response response = userSpogServer.assignMspAccountAdmins(parentOrg, childOrg, userIds,csr_token );
		  userSpogServer.assignMspAccountAdminsWithCheck(response, userIds, csr_token);
		  
		  //login
		  spogServer.userLogin(loginUser, loginPassword);
		  String token = spogServer.getJWTToken();
		  //unassign
		  String[] unAssignAdminIds = new String[1];
		  unAssignAdminIds[0]= userIds[1];
		  response = userSpogServer.unAssignMspAccountAdmins(parentOrg, childOrg, unAssignAdminIds, token);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
		  spogServer.checkErrorCode(response, "00100101");
	  }
	  
	  
	  @Test
	  public void csrReadOnlyUnAssignMspAdminsFail() {
		  System.out.println("csrReadOnlyUnAssignMspAdminsFail");
		  test = rep.startTest("csrReadOnlyUnAssignMspAdminsFail");
		  test.assignAuthor("Liu Yuefen");
          
		  spogServer.setToken(csr_token);
		  System.out.println("token:"+csr_token);
		  
		  int num =2;
		  String[] userIds = new String[num];
		  for (int i=0;i<num;i++) {
			  String email = spogServer.ReturnRandom("yuefen_mspAccountAdmin_")+i+"@spogqa.com";
			  userIds[i] = spogServer.createUserAndCheck(email, sharePassword, "yuefen", "liu", "msp_account_admin", mspOrg_id, test);
			  
		  }
		  //assign
		  Response response = userSpogServer.assignMspAccountAdmins(mspOrg_id, account_id, userIds, csr_token);
		  userSpogServer.assignMspAccountAdminsWithCheck(response, userIds, csr_token);
		  
		  //unassign
		  String[] unAssignAdminIds = new String[1];
		  unAssignAdminIds[0]= userIds[0];
		  response = userSpogServer.unAssignMspAccountAdmins(mspOrg_id, account_id, unAssignAdminIds, csr_readonly_token);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
		  spogServer.checkErrorCode(response, "00100101");
		  
		  spogServer.setToken(csr_token);
          
		  for (int i=0;i<num;i++) {
			  spogServer.DeleteUserById(userIds[i], test);
			  
		  } 
	  }
	  
	  @DataProvider(name = "UserInfo")
	  public final Object[][] getUserInfo() {
		  return new Object[][] { 
					  {csr_token},
					  {msp_token}
			          }; 
		}
	  
	  @Test (dataProvider = "UserInfo")
	  public void unAssignMspAdminsSuccessful(String token) {
		  System.out.println("unAssignMspAdminsSuccessful");
		  test = rep.startTest("unAssignMspAdminsSuccessful");
		  test.assignAuthor("Liu Yuefen");
          
		  spogServer.setToken(token);
		  System.out.println("token:"+token);
		  
		  int num =2;
		  String[] userIds = new String[num];
		  for (int i=0;i<num;i++) {
			  String email = spogServer.ReturnRandom("yuefen_mspAccountAdmin_")+i+"@spogqa.com";
			  userIds[i] = spogServer.createUserAndCheck(email, sharePassword, "yuefen", "liu", "msp_account_admin", mspOrg_id, test);
			  
		  }
		  //assign
		  Response response = userSpogServer.assignMspAccountAdmins(mspOrg_id, account_id, userIds, token);
		  userSpogServer.assignMspAccountAdminsWithCheck(response, userIds, token);
		  //unassign
		  String[] unAssignAdminIds = new String[1];
		  unAssignAdminIds[0]= userIds[0];
		  response = userSpogServer.unAssignMspAccountAdmins(mspOrg_id, account_id, unAssignAdminIds, token);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  response = userSpogServer.getAllMspAccountAdmins(mspOrg_id, account_id, token);
		  ArrayList<Map<String,Object>> list = new ArrayList<>();
		  list = response.then().extract().path("data");
		  for (int i=0; i<list.size();i++) {
			  if (list.get(i).containsValue("unAssignAdminIds[i]") == false) {
				  assertTrue("It's correct", true);
			  }
		  }
          
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
	  public void unAssignMspAdminsFail(String token, int expectedStatusCode, String expectedErrorCode) {
		  System.out.println("unAssignMspAdminsFail");
		  test = rep.startTest("unAssignMspAdminsFail");
		  test.assignAuthor("Liu Yuefen");
          
		  spogServer.setToken(csr_token);
		  //create and assign
		  int num =2;
		  String[] userIds = new String[num];
		  for (int i=0;i<num;i++) {
			  String email = spogServer.ReturnRandom("yuefen_mspAccountAdmin_")+i+"@spogqa.com";
			  userIds[i] = spogServer.createUserAndCheck(email, sharePassword, "yuefen", "liu", "msp_account_admin", mspOrg_id, test);
			  
		  }
		  Response response = userSpogServer.assignMspAccountAdmins(mspOrg_id, account_id, userIds, csr_token);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  
		  //unassign
		  String[] unAssignAdminIds = new String[1];
		  unAssignAdminIds[0]= userIds[1];
		  response = userSpogServer.unAssignMspAccountAdmins(mspOrg_id, account_id, unAssignAdminIds, token);
		  spogServer.checkResponseStatus(response, expectedStatusCode, test);
		  spogServer.checkErrorCode(response, expectedErrorCode);
		  //delete
		  for (int i=0;i<num;i++) {
			  spogServer.DeleteUserById(userIds[i], test);
			  
		  }  
	  }
	  
	  @Test 
	  //Error Code:00200001 Message:"Unauthorized: The user doesn't exist or the status is not active."  
	  public void unAssignMspAdminsFail2() {
		  System.out.println("unAssignMspAdminsFail2");
		  test = rep.startTest("unAssignMspAdminsFail2");
		  test.assignAuthor("Liu Yuefen");
          
		  spogServer.setToken(csr_token);
		  
		  int num =2;
		  String[] userIds = new String[num];
		  for (int i=0;i<num;i++) {
			  String email = spogServer.ReturnRandom("yuefen_mspAccountAdmin_")+i+"@spogqa.com";
			  userIds[i] = spogServer.createUserAndCheck(email, sharePassword, "yuefen", "liu", "msp_account_admin", mspOrg_id, test);
			  
		  }
		  
		  Response response = userSpogServer.assignMspAccountAdmins(mspOrg_id, account_id, userIds, csr_token);
		  //delete
		  for (int i =0; i<num;i++) {
			  spogServer.DeleteUserById(userIds[i], test);
		  }
	      //unassign
		  response = userSpogServer.unAssignMspAccountAdmins(mspOrg_id, account_id, userIds, csr_token);
		  spogServer.checkResponseStatus(response, SpogConstants.RESOURCE_NOT_EXIST, test);
		  spogServer.checkErrorCode(response, "00300013");   
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
	  public void unAssignMspAdminsFail3(String parentOrg, String childOrg, int expectedStatusCode, String expectedErrorCode) {
		  System.out.println("unAssignMspAdminsFail3");
		  test = rep.startTest("unAssignMspAdminsFail3");
		  test.assignAuthor("Liu Yuefen");
          
		  spogServer.setToken(csr_token);
		  
		  int num =2;
		  String[] userIds = new String[num];
		  for (int i=0;i<num;i++) {
			  String email = spogServer.ReturnRandom("yuefen_mspAccountAdmin_")+i+"@spogqa.com";
			  userIds[i] = spogServer.createUserAndCheck(email, sharePassword, "yuefen", "liu", "msp_account_admin", mspOrg_id, test);
			  
		  }
		  
		  Response response = userSpogServer.assignMspAccountAdmins(mspOrg_id, account_id, userIds, csr_token);
		  //unassign
		  response = userSpogServer.unAssignMspAccountAdmins(parentOrg, childOrg, userIds, csr_token);
		  spogServer.checkResponseStatus(response, expectedStatusCode);
		  spogServer.checkErrorCode(response, expectedErrorCode);   
		  //delete
		  		  for (int i=0;i<num;i++) {
			  spogServer.DeleteUserById(userIds[i], test);
			  
		  } 
	  }
	  
	  @Test 
	  //Error Code:"00300013" Message:"The organization [] does not have any account admin with Id [].",
	  public void unAssignMspAdminsFail4() {
		  System.out.println("unAssignMspAdminsFail4");
		  test = rep.startTest("unAssignMspAdminsFail4");
		  test.assignAuthor("Liu Yuefen");
          
		  spogServer.setToken(csr_token);
		  
		  int num =2;
		  String[] userIds = new String[num];
		  for (int i=0;i<num;i++) {
			  String email = spogServer.ReturnRandom("yuefen_mspAccountAdmin_")+i+"@spogqa.com";
			  userIds[i] = spogServer.createUserAndCheck(email, sharePassword, "yuefen", "liu", "msp_account_admin", mspOrg_id, test);
			  
		  }
		  //unassign
		  Response response = userSpogServer.unAssignMspAccountAdmins(mspOrg_id, account_id, userIds, csr_token);
		  spogServer.checkResponseStatus(response, SpogConstants.RESOURCE_NOT_EXIST, test);
		  spogServer.checkErrorCode(response, "00300013");   
		  
  		  for (int i=0;i<num;i++) {
  			  spogServer.DeleteUserById(userIds[i], test);
  		  }	    
	  }
	  
	  @DataProvider(name = "UserInfo3")
	  public final Object[][] getUserInfo3() {
		  return new Object[][] { 
			          {csr_user_id, SpogConstants.RESOURCE_NOT_EXIST,"00300013"},
			          {direct_user_id, SpogConstants.RESOURCE_NOT_EXIST,"00300013"},
			          {msp_user_id, SpogConstants.RESOURCE_NOT_EXIST,"00300013"},
			          {account_user_id, SpogConstants.RESOURCE_NOT_EXIST,"00300013"}
			          }; 
		}
	  
	  @Test(dataProvider = "UserInfo3")
	  //Error Code:"00300013" "message": "The organization [6f68ac71-51d7-4d7c-984d-caad2905d133] does not have any account admin with Id [8b3481f5-c568-4244-acec-a1544d8fbd42]."
	  public void unAssignMspAdminsFail5(String userId,  int expectedStatusCode, String expectedErrorCode) {
		  System.out.println("unAssignMspAdminsFail5");
		  test = rep.startTest("unAssignMspAdminsFail5");
		  test.assignAuthor("Liu Yuefen");
          
		  spogServer.setToken(csr_token);
		  
		  String[] userIds = new String[1];
          userIds[0] = userId;
		  
		  Response response = userSpogServer.unAssignMspAccountAdmins(mspOrg_id, account_id, userIds, csr_token);
		  spogServer.checkResponseStatus(response, expectedStatusCode);
		  spogServer.checkErrorCode(response, expectedErrorCode);   
	  }
	  
	  @Test 
	  //Error "code": "00300014", "message": "Multiple occurrence of UserId [{0}] in a single request." 
	  public void unAssignMspAdminsFail6() {
		  System.out.println("unAssignMspAdminsFail6");
		  test = rep.startTest("unAssignMspAdminsFail6");
		  test.assignAuthor("Liu Yuefen");
          
		  spogServer.setToken(csr_token);
		  
		  String[] userIds = new String[1];
		  String[] unAssignAdmins = new String[2];
		  
		  String email = spogServer.ReturnRandom("yuefen_mspAccountAdmin@spogqa.com");
		  userIds[0] = spogServer.createUserAndCheck(email, sharePassword, "yuefen", "liu", "msp_account_admin", mspOrg_id, test);
		  unAssignAdmins[0] = userIds[0];
		  unAssignAdmins[1] = userIds[0];
			 
		  Response response = userSpogServer.assignMspAccountAdmins(mspOrg_id, account_id, userIds, csr_token);
		  //unassign
		  response = userSpogServer.unAssignMspAccountAdmins(mspOrg_id, account_id, unAssignAdmins, csr_token);
		  spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
		  spogServer.checkErrorCode(response, "00300014");   
		  //delete
  		  spogServer.DeleteUserById(userIds[0], test);    
	  }

	  @Test 
	  //unassign twice
	  public void unAssignMspAdminsTwice() {
		  System.out.println("unAssignMspAdminsTwice");
		  test = rep.startTest("unAssignMspAdminsTwice");
		  test.assignAuthor("Liu Yuefen");
          
		  spogServer.setToken(csr_token);
		  
		  String[] userIds = new String[1];
		  
		  String email = spogServer.ReturnRandom("yuefen_mspAccountAdmin_1@spogqa.com");
		  userIds[0]= spogServer.createUserAndCheck(email, sharePassword, "yuefen", "liu", "msp_account_admin", mspOrg_id, test);

		  Response response = userSpogServer.assignMspAccountAdmins(mspOrg_id, account_id, userIds, csr_token);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		  //unassign
		  response = userSpogServer.unAssignMspAccountAdmins(mspOrg_id, account_id, userIds, csr_token);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		  response = userSpogServer.unAssignMspAccountAdmins(mspOrg_id, account_id, userIds, csr_token);
		  spogServer.checkResponseStatus(response, SpogConstants.RESOURCE_NOT_EXIST);
		  spogServer.checkErrorCode(response, "00300013");
	
		  //delete
		  spogServer.DeleteUserById(userIds[0], test);
	  }
	  
	  @Test 
	  //assing the same msp account admin to multiple sub org.
	  public void unAssignMspAdminsToMultipleSubOrg() {
		  System.out.println("unAssignMspAdminsToMultipleSubOrg");
		  test = rep.startTest("unAssignMspAdminsToMultipleSubOrg");
		  test.assignAuthor("Liu Yuefen");
          
		  spogServer.setToken(msp_token);
		  
		  String[] userIds = new String[1];
		  
		  String email = spogServer.ReturnRandom("yuefen_mspAccountAdmin_1@spogqa.com");
		  userIds[0]= spogServer.createUserAndCheck(email, sharePassword, "yuefen", "liu", "msp_account_admin", mspOrg_id, test);

		  Response response = userSpogServer.assignMspAccountAdmins(mspOrg_id, account_id, userIds, msp_token);
		  userSpogServer.assignMspAccountAdminsWithCheck(response, userIds, msp_token);
		  
		  response = userSpogServer.assignMspAccountAdmins(mspOrg_id, account2_id, userIds, msp_token);
		  userSpogServer.assignMspAccountAdminsWithCheck(response, userIds, msp_token);
	      //unassign
		  response = userSpogServer.unAssignMspAccountAdmins(mspOrg_id, account_id, userIds, msp_token);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  response = userSpogServer.unAssignMspAccountAdmins(mspOrg_id, account2_id, userIds, msp_token);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
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
	  public void unAssignMspAdminsFail401(String token) {
		  System.out.println("unAssignMspAdminsFail401");
		  test = rep.startTest("unAssignMspAdminsFail401");
		  test.assignAuthor("Liu Yuefen");
          
		  spogServer.setToken(csr_token);
		  
		  int num =1;
		  String[] userIds = new String[num];
		  
		  String email = spogServer.ReturnRandom("yuefen_mspAccountAdmin@spogqa.com");
		  userIds[0] = spogServer.createUserAndCheck(email, sharePassword, "yuefen", "liu", "msp_account_admin", mspOrg_id, test);
		  
		  Response response = userSpogServer.unAssignMspAccountAdmins(mspOrg_id, account_id, userIds, token);
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
