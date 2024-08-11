package api.organization.orders;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import java.text.SimpleDateFormat;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;

import org.testng.annotations.AfterMethod;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.SPOGServer;
import InvokerServer.Org4SPOGServer;
import InvokerServer.UserSpogServer;
import dataPreparation.JsonPreparation;
import InvokerServer.SPOGDestinationServer;
import genericutil.ExtentManager;
import io.restassured.response.Response;
import genericutil.SQLServerDb;
import genericutil.testcasescount;

public class CreateOrderForLoggedInUserTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private UserSpogServer userSpogServer;
	private SPOGDestinationServer spogDestinationServer;
	private Org4SPOGServer org4SpogServer;
	private JsonPreparation jp;
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
	  private String csrReadOnlyUser="liuyu05@arcserve.com";
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
	  //root msp related
	  private String rootMspOrg_id;
	  private String rootMsp_admin_email;
	  private String rootMsp_account_admin_email;
	  private String rootMsp_directOrg1_id;
	  private String rootMsp_directOrg2_id;
	  private String rootMsp_directOrg1_user_email;
	  private String rootMsp_directOrg2_user_email;
	  private String subMsp1_id;
	  private String subMsp2_id;
	  private String subMsp1_admin_email;
	  private String subMsp1_account_admin_email;
	  private String subMsp2_admin_email;
	  private String subMsp2_account_admin_email;
	  private String account1_subMsp1_id;
	  private String account2_subMsp2_id;
	  private String account1_subMsp1_user_email;
	  private String account2_subMsp1_user_email;
	  private String account1_rootMsp_id;
	  private String account2_rootMsp_id;
	  private String account1_rootMsp_user_email;
	  private String account2_rootMsp_user_email;
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
		  rep = ExtentManager.getInstance("CreateOrderForLoggedInUserTest",logFolder);
		  test = rep.startTest("beforeClass");
		  spogServer = new SPOGServer(baseURI, port);
		  userSpogServer =new UserSpogServer(baseURI, port);
		  spogDestinationServer= new SPOGDestinationServer(baseURI, port);
		  org4SpogServer = new Org4SPOGServer(baseURI, port);
		  jp = new JsonPreparation();
		  spogServer.userLogin(userName, password);  
		  
		  this.csr_token = spogServer.getJWTToken();
		  this.csrGlobalLoginUser = userName;
		  this.csrGlobalLoginPassword = password;
		  csrOrg_id = spogServer.GetLoggedinUserOrganizationID();
//		  directOrg_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_update_direct1_yuefen"),"direct",spogServer.ReturnRandom("spogqa_direct1_update@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("direct_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
//		  directOrg2_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_update_direct2_yuefen"),"direct",spogServer.ReturnRandom("spogqa_direct2_update@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("direct_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  mspOrg_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_update_yuefen_msp1")+org_prefix,"msp",spogServer.ReturnRandom("spogqa_update_msp1@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("direct_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
//		  mspOrg2_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_update_yuefen_msp2"),"msp",spogServer.ReturnRandom("spogqa_update_msp2@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("direct_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  account_id = spogServer.createAccountWithCheck(mspOrg_id,spogServer.ReturnRandom("accoun1_msp1_yuefen_spogqa")+org_prefix, mspOrg_id, test);
//		  account2_id = spogServer.createAccountWithCheck(mspOrg_id, spogServer.ReturnRandom("accoun2_msp1_yuefen_spogqa"), mspOrg_id, test);
//		  
		  //create root msp related
		  rootMsp_admin_email=spogServer.ReturnRandom("yuefen_rootmsp@spogqa.com");
		  rootMspOrg_id=spogServer.CreateOrganizationByEnrollWithCheck(spogServer.ReturnRandom("yuefen_rootmsp")+org_prefix, "msp", rootMsp_admin_email, sharePassword, "yuefen", "liu");
          spogServer.convertTo3Tier(rootMspOrg_id);
          //create sub msp
          HashMap<String, Object> requestInfo = jp.composeSubMspRqstInfo("subMSP1"+org_prefix, rootMspOrg_id, "yuefen", "liu", "submsp1_yuefen@spogqa.com", "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c");
          subMsp1_id = spogServer.createSubMspWithCheck(csr_token, rootMspOrg_id, requestInfo, SpogConstants.SUCCESS_POST, null, test);
          subMsp1_admin_email =spogServer.ReturnRandom("submsp_yuefen_update@spogqa.com");
		  spogServer.createUserAndCheck(subMsp1_admin_email, sharePassword, "yuefen", "liu", "msp_admin", subMsp1_id, test);
		  //create account under sub msp
		  account1_subMsp1_id=spogServer.createAccountWithCheck(subMsp1_id,spogServer.ReturnRandom("accoun1_submsp1_yuefen_spogqa")+org_prefix, subMsp1_id, test);
		  account1_subMsp1_user_email=spogServer.ReturnRandom("account_submsp_yuefen_update@spogqa.com");
		  spogServer.createUserAndCheck(account1_subMsp1_user_email, sharePassword, "yuefen", "liu", "direct_admin", account1_subMsp1_id, test);
		  
		  //create account under root msp
		  account1_rootMsp_id=spogServer.createAccountWithCheck(rootMspOrg_id,spogServer.ReturnRandom("accoun1_rootmsp1_yuefen_spogqa")+org_prefix, rootMspOrg_id, test);
		  account1_rootMsp_user_email=spogServer.ReturnRandom("account_rootmsp_yuefen_update@spogqa.com");
		  spogServer.createUserAndCheck(account1_rootMsp_user_email, sharePassword, "yuefen", "liu", "direct_admin", account1_rootMsp_id, test);
		  
		  //create csr user
//		  this.csr_email = spogServer.ReturnRandom("direct_yuefen_update@spogqa.com");
//		  csr_user_id =spogServer.createUserAndCheck(csr_email, sharePassword, "yuefen", "liu", "csr_admin", csrOrg_id, test);
//		  //create direct user
//		  System.out.println("direct user");
//		  this.direct_email = spogServer.ReturnRandom("direct_yuefen_update@spogqa.com");
//		  direct_user_id = spogServer.createUserAndCheck(direct_email, sharePassword, "yuefen", "liu", "direct_admin", directOrg_id, test);
//		  spogServer.userLogin(direct_email, sharePassword);
//		  this.direct_token = spogServer.getJWTToken();
//		  //create msp1 user
//		  System.out.println("msp1 user");
//		  spogServer.setToken(csr_token);
//		  this.msp_email = spogServer.ReturnRandom("msp1_yuefen_update@spogqa.com");
//		  msp_user_id =spogServer.createUserAndCheck(msp_email, sharePassword, "yuefen", "liu", "msp_admin", mspOrg_id, test);
//		  spogServer.userLogin(msp_email, sharePassword);
//		  this.msp_token = spogServer.getJWTToken();
//		  // create msp2 user
//		  System.out.println("msp2 user");
//		  spogServer.setToken(csr_token);
//		  this.msp2_email = spogServer.ReturnRandom("msp2_yuefen_update@spogqa.com");
//		  spogServer.createUserAndCheck(msp2_email, sharePassword, "yuefen", "liu", "msp_admin", mspOrg2_id, test);
//		  spogServer.userLogin(msp2_email, sharePassword);
//		  this.msp2_token = spogServer.getJWTToken();
//		  //create msp account admin
//		  System.out.println("msp account admin user");
//		  spogServer.setToken(csr_token);
//		  this.msp_account_admin = spogServer.ReturnRandom("msp_account_admin_yuefen@spogqa.com");
//		  spogServer.createUserAndCheck(msp_account_admin, sharePassword, "yuefen", "liu", "msp_account_admin", mspOrg_id, test);
//		  spogServer.userLogin(msp_account_admin, sharePassword);
//		  this.msp_account_admin_token = spogServer.getJWTToken();
		  //create account user
		  System.out.println("account user");
		  spogServer.setToken(csr_token);
		  this.accountDirect_email = spogServer.ReturnRandom("account1_msp1_yuefen_update@spogqa.com");
		  account_user_id=spogServer.createUserAndCheck(accountDirect_email, sharePassword, "yuefen", "liu", "direct_admin", account_id, test);
		  spogServer.userLogin(accountDirect_email, sharePassword);
		  this.account_token = spogServer.getJWTToken();
		  
		  spogServer.userLogin(csrReadOnlyUser, sharePassword);
		  csr_readonly_token = spogServer.getJWTToken();
		  
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
        	  System.out.println("Into CreateOrderForLoggedInUserTest");
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
          setEnv(baseURI,  port,  userName, password);
	  }
	  
	  //root msp related, root msp only accept subscription, not accept order license.
	  @DataProvider(name = "orderInfo10")
	  public final Object[][] getOrderInfo10() {
		  return new Object[][] { 
					  {this.rootMsp_admin_email, sharePassword, "SKUTESTDATA_10_8_0_1_"+RandomStringUtils.randomNumeric(8),"SKUTESTDATA_10_8_0_1_"+RandomStringUtils.randomNumeric(8),SpogConstants.REQUIRED_INFO_NOT_EXIST,"00300033"},
					  {this.subMsp1_admin_email, sharePassword, "SKUTESTDATA_10_8_0_1_"+RandomStringUtils.randomNumeric(8),"SKUTESTDATA_10_8_0_1_"+RandomStringUtils.randomNumeric(8),SpogConstants.NOT_ALLOWED_ON_RESOURCE,"00300021"}, 
					  {this.account1_rootMsp_user_email, sharePassword,"SKUTESTDATA_10_8_0_1_"+RandomStringUtils.randomNumeric(8),"SKUTESTDATA_10_8_0_1_"+RandomStringUtils.randomNumeric(8),SpogConstants.NOT_ALLOWED_ON_RESOURCE,"00300021"},
					  {this.account1_subMsp1_user_email, sharePassword,"SKUTESTDATA_10_8_0_1_"+RandomStringUtils.randomNumeric(8),"SKUTESTDATA_10_8_0_1_"+RandomStringUtils.randomNumeric(8),SpogConstants.NOT_ALLOWED_ON_RESOURCE,"00300021"},
					  {this.accountDirect_email, sharePassword,"SKUTESTDATA_10_8_0_1_"+RandomStringUtils.randomNumeric(8),"SKUTESTDATA_10_8_0_1_"+RandomStringUtils.randomNumeric(8),SpogConstants.NOT_ALLOWED_ON_RESOURCE,"00300021"},
					  
		  };
		}
	  @Test(dataProvider = "orderInfo10")
	  public void addOrderToSubMspSuccessAndCheck(String loginUser, String loginPassword, String orderID, String fulfillmentID, int expectedStatusCode, String expectedErrorCode ) {
		  System.out.println("addOrderToSubMspSuccessAndCheck");
		  test = rep.startTest("addOrderToSubMspSuccessAndCheck");
		  test.assignAuthor("Liu Yuefen");
		  
          //login
		  spogServer.userLogin(loginUser, loginPassword);
		  String token =spogServer.getJWTToken();
		  //create order
		  userSpogServer.setToken(token);
		  System.out.println("starting to add order:");
		  Response response=userSpogServer.addOrderForLoggedInUser(orderID,fulfillmentID, test);
		  spogServer.checkResponseStatus(response, expectedStatusCode);
		  spogServer.checkErrorCode(response, expectedErrorCode);
		  }
	  

	  @Test (dataProvider = "orderInfo")
	  public void addOrderSuccessAndCheck(String organizationName, String organizationType, String organizationEmail, String organizationPwd, String organizationFirstName, String organizationLastName, String orderID, String fulfillmentID) {
		  System.out.println("addOrderSuccessAndCheck");
		  test = rep.startTest("addOrderSuccessAndCheck");
		  test.assignAuthor("Liu Yuefen");
          
		  //login 
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword);
		  //enroll
		  spogServer.CreateOrganizationByEnrollWithCheck(organizationName+org_prefix, organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName);
//		  try {
//				Thread.sleep(5000);
//			
//			  } catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			  } 
		  //create org
//		  String orgId=spogServer.CreateOrganizationWithCheck(organizationName+org_prefix, organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName);
//		  //create CD
//		  String cloud_direct_account_id=spogServer.createCloudAccountWithCheck(spogServer.ReturnRandom("yuefen_key"), "cloudAccountSecret", spogServer.ReturnRandom("yuefen_name"), "cloud_direct", orgId, "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8) , "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8),"SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8), "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
          //login
		  spogServer.userLogin(organizationEmail, organizationPwd);
		  String token =spogServer.getJWTToken();
		  //create order
		  userSpogServer.setToken(token);
//		  Response response =userSpogServer.addOrderForLoggedInUser(orderID, fulfillmentID, test);
//		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);  
		  userSpogServer.addOrderForLoggedInUserAndCheck(orderID, fulfillmentID, test);
		  //recycle
//		  spogDestinationServer.recycleCloudVolumesAndDelOrg(orgId,test);
		  }
	  
	  @DataProvider(name = "orderInfo2")
	  public final Object[][] getOrderInfo2() {
		  return new Object[][] { 
					  {csr_token,"SKUTESTDATA_10_8_0_1_"+RandomStringUtils.randomNumeric(8),"SKUTESTDATA_10_8_0_1_"+RandomStringUtils.randomNumeric(8),SpogConstants.NOT_ALLOWED_ON_RESOURCE,"00300021"},
					  {account_token,"SKUTESTDATA_10_8_0_1_"+RandomStringUtils.randomNumeric(8),"SKUTESTDATA_10_8_0_1_"+RandomStringUtils.randomNumeric(8),SpogConstants.NOT_ALLOWED_ON_RESOURCE,"00300021"}, 
					  {csr_readonly_token,"SKUTESTDATA_10_8_0_1_"+RandomStringUtils.randomNumeric(8),"SKUTESTDATA_10_8_0_1_"+RandomStringUtils.randomNumeric(8),SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101"}
		  };
		}
	  //change error code from 00200036 to 00300021
	  @Test (dataProvider = "orderInfo2")
	  public void addOrderFail(String token, String orderID, String fulfillmentID, int expectedStatusCode, String expectedErrorCode) {
		  System.out.println("addOrderFail");
		  test = rep.startTest("addOrderFail");
		  test.assignAuthor("Liu Yuefen");
          
		  userSpogServer.setToken(token);
		  
		  //create order
		  Response response =userSpogServer.addOrderForLoggedInUser(orderID, fulfillmentID, test);
		  spogServer.checkResponseStatus(response, expectedStatusCode);  
		  spogServer.checkErrorCode(response, expectedErrorCode);
		  }
	  
	  @DataProvider(name = "orderInfo3")
	  public final Object[][] getOrderInfo3() {
		  return new Object[][] { 
					  {spogServer.ReturnRandom("yuefen_directorg_name"),"direct",spogServer.ReturnRandom("yuefen_direct@spogqa.com"),sharePassword,"yuefen","liu","","",SpogConstants.REQUIRED_INFO_NOT_EXIST,"40000001"}, 
					  {spogServer.ReturnRandom("yuefen_directorg_name"),"direct",spogServer.ReturnRandom("yuefen_direct@spogqa.com"),sharePassword,"yuefen","liu","abc","abc",SpogConstants.REQUIRED_INFO_NOT_EXIST,"8501"}, 
					  {spogServer.ReturnRandom("yuefen_msporg_name"),"msp",spogServer.ReturnRandom("yuefen_msp@spogqa.com"),sharePassword,"yuefen","liu","","",SpogConstants.REQUIRED_INFO_NOT_EXIST,"40000001"}, 
			          }; 
		}
	  
	  @Test (dataProvider = "orderInfo3")
	  public void addOrderFail2(String organizationName, String organizationType, String organizationEmail, String organizationPwd, String organizationFirstName, 
			  String organizationLastName, String orderID, String fulfillmentID, int expectedStatusCode, String expectedErrorCode) {
		  System.out.println("addOrderFail2");
		  test = rep.startTest("addOrderFail2");
		  test.assignAuthor("Liu Yuefen");
          
		  //login 
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword);
		  //enroll
		  String orgId=spogServer.CreateOrganizationByEnrollWithCheck(organizationName+org_prefix, organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName);
//		  //create org
//		  String orgId=spogServer.CreateOrganizationWithCheck(organizationName+org_prefix, organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName);
//		  //create CD
//		  String cloud_direct_account_id=spogServer.createCloudAccountWithCheck(spogServer.ReturnRandom("yuefen_key"), "cloudAccountSecret", spogServer.ReturnRandom("yuefen_name"), "cloud_direct", orgId, "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8) , "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8),"SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8), "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
          //login
		  spogServer.userLogin(organizationEmail, organizationPwd);
		  String token =spogServer.getJWTToken();
		  //create order
		  userSpogServer.setToken(token);
		  Response response =userSpogServer.addOrderForLoggedInUser(orderID, fulfillmentID, test);
          spogServer.checkResponseStatus(response, expectedStatusCode);
          spogServer.checkErrorCode(response, expectedErrorCode);
          
		  //recycle
//		  spogDestinationServer.recycleCloudVolumesAndDelOrg(orgId,test);
		  }

	  
	  @Test 
	  public void addOrderUsingMspAccountAdmin() {
		  System.out.println("addOrderUsingMspAccountAdmin");
		  test = rep.startTest("addOrderUsingMspAccountAdmin");
		  test.assignAuthor("Liu Yuefen");
          
		  //login 
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword);
		  //create org
		  String orgId=spogServer.CreateOrganizationByEnrollWithCheck(spogServer.ReturnRandom("msp_name")+org_prefix, "msp", spogServer.ReturnRandom("msp_yuefen@spogqa.com"), sharePassword, "yuefen", "liu");
		  //create msp account admin
		  String username =spogServer.ReturnRandom("msp_account_admin_yuefen@spogqa.com");
		  String user_id =spogServer.createUserAndCheck(username, sharePassword, "yuefen", "liu", "msp_account_admin", orgId, test);
		  
//		  //create CD
//		  String cloud_direct_account_id=spogServer.createCloudAccountWithCheck(spogServer.ReturnRandom("yuefen_key"), "cloudAccountSecret", spogServer.ReturnRandom("yuefen_name"), "cloud_direct", orgId, "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8) , "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8),"SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8), "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
          //login
		  spogServer.userLogin(username, sharePassword);
		  String token =spogServer.getJWTToken();
		  //create order
		  userSpogServer.setToken(token);
		  Response response =userSpogServer.addOrderForLoggedInUser("SKUTESTDATA_10_8_0_1_20121212121"+RandomStringUtils.randomNumeric(8), "SKUTESTDATA_10_8_0_1_20121212121"+RandomStringUtils.randomNumeric(8), test);
          spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS);
          spogServer.checkErrorCode(response, "");
          
		  //recycle
//		  spogDestinationServer.recycleCloudVolumesAndDelOrg(orgId,test);
		  }
		  
	  @Test 
	  public void addDuplicateOrders() {
		  System.out.println("addDuplicateOrders");
		  test = rep.startTest("addDuplicateOrders");
		  test.assignAuthor("Liu Yuefen");
          
		  //login 
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword);
		  
		  //create org
		  String userName=spogServer.ReturnRandom("msp_yuefen@spogqa.com");
		  String orgId=spogServer.CreateOrganizationByEnrollWithCheck(spogServer.ReturnRandom("msp_name")+org_prefix, "msp", userName, sharePassword, "yuefen", "liu");
		 
//		  //create CD
//		  String cloud_direct_account_id=spogServer.createCloudAccountWithCheck(spogServer.ReturnRandom("yuefen_key"), "cloudAccountSecret", spogServer.ReturnRandom("yuefen_name"), "cloud_direct", orgId, "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8) , "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8),"SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8), "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
          //login
		  spogServer.userLogin(userName, sharePassword);
		  String token=spogServer.getJWTToken();
		  //add order
		  userSpogServer.setToken(token);
		  String orderId="SKUTESTDATA_10_8_0_1_"+RandomStringUtils.randomNumeric(8);
		  String fulfillmentId ="SKUTESTDATA_10_8_0_1_"+RandomStringUtils.randomNumeric(8);
		  Response response =userSpogServer.addOrderForLoggedInUser(orderId, fulfillmentId, test);
          spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);
          //add the duplicate orders
          response =userSpogServer.addOrderForLoggedInUser(orderId, fulfillmentId, test);
          spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST);
          spogServer.checkErrorCode(response, "00300022");
          
		  //recycle
//		  spogDestinationServer.recycleCloudVolumesAndDelOrg(orgId,test);
		  }
	  
	  @Test 
	  public void addMultipleOrdersForOneOrg() {
		  System.out.println("addMultipleOrdersForOneOrg");
		  test = rep.startTest("addMultipleOrdersForOneOrg");
		  test.assignAuthor("Liu Yuefen");
          
		  //login 
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword);
		  
		  //create org
		  String userName=spogServer.ReturnRandom("msp_yuefen@spogqa.com");
		  String orgId=spogServer.CreateOrganizationByEnrollWithCheck(spogServer.ReturnRandom("msp_name")+org_prefix, "msp", userName, sharePassword, "yuefen", "liu");
		 
//		  //create CD
//		  String cloud_direct_account_id=spogServer.createCloudAccountWithCheck(spogServer.ReturnRandom("yuefen_key"), "cloudAccountSecret", spogServer.ReturnRandom("yuefen_name"), "cloud_direct", orgId, "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8) , "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8),"SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8), "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
          //login
		  spogServer.userLogin(userName, sharePassword);
		  String token=spogServer.getJWTToken();
		  //setToken for userspogserver
		  userSpogServer.setToken(token);
		  //add order
		  int num =3;
		  String orderIds[] =new String[num];
		  String fulfillmentIds[] =new String[num];
		
		  for (int i=0;i<num;i++) {
			  orderIds[i]="SKUTESTDATA_1"+i+"_8_0_1_"+RandomStringUtils.randomNumeric(8);
			  fulfillmentIds[i]="SKUTESTDATA_1"+i+"_8_0_1_"+RandomStringUtils.randomNumeric(8);
			  userSpogServer.addOrderForLoggedInUserAndCheck(orderIds[i], fulfillmentIds[i], test);
		  }
		  
		  //recycle
//		  spogDestinationServer.recycleCloudVolumesAndDelOrg(orgId,test);
		  } 
	  @Test 
	  public void addOdersFail401() {
		  System.out.println("addOdersFail401");
		  test = rep.startTest("addOdersFail401");
		  test.assignAuthor("Liu Yuefen");
   
		  //add order
		  userSpogServer.setToken("");
		  String orderId="SKUTESTDATA_10_8_0_1_"+RandomStringUtils.randomNumeric(8);
		  String fulfillmentId ="SKUTESTDATA_10_8_0_1_"+RandomStringUtils.randomNumeric(8);
		  Response response =userSpogServer.addOrderForLoggedInUser(orderId, fulfillmentId, test);
          spogServer.checkResponseStatus(response, SpogConstants.NOT_LOGGED_IN);
          spogServer.checkErrorCode(response, "00900006");
		  }
	  
	  /***************** Add orders with no start/end time should fail - Sprint 34 ******************************/
	  @DataProvider(name="addOrderFailTestCases")
	  public Object[][] addOrderFailTestCases(){
		  return new Object[][] {
			  {"Add order that does not have start/end time to direct org", "direct", "473814", "65096729", SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.PROBLEM_WITH_ORDER_CONTACT_SUPPORT},
			  {"Add order that does not have start/end time to MSP org", "msp", "473814", "65096729", SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.PROBLEM_WITH_ORDER_CONTACT_SUPPORT},
		  };
	  }
	  @Test(dataProvider="addOrderFailTestCases")
	  public void addOrderFailTest( String caseType, 
			  						String orgType,
			  						String orderID,
			  						String fulfillmentID,
			  						int expectedStatusCode,
			  						SpogMessageCode expectedErrorMessage ) {
		  
		  System.out.println(caseType);
		  test = rep.startTest(caseType);
		  test.assignAuthor("Rakesh.Chalamala");
          
		  //login 
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword);
		  
		  //create org
		  String userName=spogServer.ReturnRandom(spogServer.ReturnRandom("user")+"@spogqa.com");
		  spogServer.CreateOrganizationByEnrollWithCheck(spogServer.ReturnRandom("org_name")+org_prefix, orgType, userName, sharePassword, "Rakesh", "Chalamala");
		
		  spogServer.userLogin(userName, sharePassword);
		  userSpogServer.setToken(spogServer.getJWTToken());
		  userSpogServer.addOrderForLoggedInUserWithCheck(orderID, fulfillmentID, expectedStatusCode, expectedErrorMessage, test);
		  		  
	  }
	  /***************************************** end **************************************************/
	  
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
	  
		  
/*
 * order format:
SKUTESTDATA_A_B_C_D_E
A: TBs of BaaS Storage
B: GB of DRaaS RAM (if this is nonzero, then DRaaS will be enabled for this cloud direct account). This has to be an even number
C: (0|1) Add an AD Server? 
D: Number of extra IP address
E: A unique string

SKUTESTDATA_10_8_0_1_20121212121
10 TB of BaaS
8 GB RAM
No AD server
1 extra IP
 */
}