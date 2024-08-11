package api.organization.orders;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.SpogConstants;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import InvokerServer.SPOGDestinationServer;
import bsh.org.objectweb.asm.Constants;
import dataPreparation.JsonPreparation;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import groovyjarjarantlr.PreservingFileWriter;
import io.restassured.response.Response;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.HashMap;

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

public class GetOrdersForLoggedInUserTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private UserSpogServer userSpogServer;
	private SPOGDestinationServer spogDestinationServer;
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
	  private String sharePassword = "Caworld_2018";
	  private String csr_email;
	  private String direct_email;
	  private String msp_email;
	  private String msp2_email;
	  private String msp_account_admin;
	  private String accountDirect_email;
	  private String csr_token;
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
	  private String account2_subMsp1_id;
	  private String account1_subMsp2_id;
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
		  rep = ExtentManager.getInstance("GetOrdersForLoggedInUserTest",logFolder);
		  test = rep.startTest("beforeClass");
		  spogServer = new SPOGServer(baseURI, port);
		  userSpogServer =new UserSpogServer(baseURI, port);
		  spogDestinationServer= new SPOGDestinationServer(baseURI, port);
		  jp = new JsonPreparation();
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
		  /**************************create root msp related****************************/
		  //create root msp
		  rootMsp_admin_email=spogServer.ReturnRandom("yuefen_rootmsp@spogqa.com");
		  rootMspOrg_id=spogServer.CreateOrganizationByEnrollWithCheck(spogServer.ReturnRandom("yuefen_rootmsp")+org_prefix, "msp", rootMsp_admin_email, sharePassword, "yuefen", "liu");
          spogServer.convertTo3Tier(rootMspOrg_id);
          //create sub msp
          HashMap<String, Object> requestInfo = jp.composeSubMspRqstInfo("subMSP1"+org_prefix, rootMspOrg_id, "yuefen", "liu", "submsp1_yuefen@spogqa.com", "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c");
          subMsp1_id = spogServer.createSubMspWithCheck(csr_token, rootMspOrg_id, requestInfo, SpogConstants.SUCCESS_POST, null, test);
          subMsp1_admin_email =spogServer.ReturnRandom("submsp_yuefen_update@spogqa.com");
		  spogServer.createUserAndCheck(subMsp1_admin_email, sharePassword, "yuefen", "liu", "msp_admin", subMsp1_id, test);
		  
		  requestInfo = jp.composeSubMspRqstInfo("subMSP2"+org_prefix, rootMspOrg_id, "yuefen", "liu", "submsp2_yuefen@spogqa.com", "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c");
          subMsp2_id = spogServer.createSubMspWithCheck(csr_token, rootMspOrg_id, requestInfo, SpogConstants.SUCCESS_POST, null, test);
          subMsp2_admin_email =spogServer.ReturnRandom("submsp_yuefen_update@spogqa.com");
		  spogServer.createUserAndCheck(subMsp2_admin_email, sharePassword, "yuefen", "liu", "msp_admin", subMsp2_id, test);
		  
		  //create account under sub msp
		  account1_subMsp1_id=spogServer.createAccountWithCheck(subMsp1_id,spogServer.ReturnRandom("accoun1_submsp1_yuefen_spogqa")+org_prefix, subMsp1_id, test);
		  account1_subMsp1_user_email=spogServer.ReturnRandom("account_submsp_yuefen_update@spogqa.com");
		  spogServer.createUserAndCheck(account1_subMsp1_user_email, sharePassword, "yuefen", "liu", "direct_admin", account1_subMsp1_id, test);
		  
		  account2_subMsp1_id=spogServer.createAccountWithCheck(subMsp1_id,spogServer.ReturnRandom("accoun1_submsp1_yuefen_spogqa")+org_prefix, subMsp1_id, test);
		  account2_subMsp1_user_email=spogServer.ReturnRandom("account_submsp_yuefen_update@spogqa.com");
		  spogServer.createUserAndCheck(account2_subMsp1_user_email, sharePassword, "yuefen", "liu", "direct_admin", account2_subMsp1_id, test);
		  
		  account1_subMsp2_id=spogServer.createAccountWithCheck(subMsp2_id,spogServer.ReturnRandom("accoun1_submsp2_yuefen_spogqa")+org_prefix, subMsp2_id, test);
		  //create account under root msp
		  account1_rootMsp_id=spogServer.createAccountWithCheck(rootMspOrg_id,spogServer.ReturnRandom("accoun1_rootmsp1_yuefen_spogqa")+org_prefix, rootMspOrg_id, test);
		  account1_rootMsp_user_email=spogServer.ReturnRandom("account_rootmsp_yuefen_update@spogqa.com");
		  spogServer.createUserAndCheck(account1_rootMsp_user_email, sharePassword, "yuefen", "liu", "direct_admin", account1_rootMsp_id, test);
		  /*******************************************************************************/
//		  account2_id = spogServer.createAccountWithCheck(mspOrg_id, spogServer.ReturnRandom("accoun2_msp1_yuefen_spogqa"), mspOrg_id, test);
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
        	  System.out.println("Into GetOrdersForLoggedInUserTest");
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
	  @DataProvider(name = "orderInfo10")
	  public final Object[][] getOrderInfo10() {
		  return new Object[][] { 
					  {this.rootMsp_admin_email, sharePassword},
					  {this.subMsp1_admin_email, sharePassword}, 
					  {this.account1_rootMsp_user_email, sharePassword},
					  {this.account1_subMsp1_user_email, sharePassword},		  
					  
		  };
		}
	  @Test(dataProvider = "orderInfo10")
	  public void rootMspRelatedGetOrder(String loginUser, String loginPassword) {
		  System.out.println("rootMspRelatedGetOrder");
		  test = rep.startTest("rootMspRelatedGetOrder");
		  test.assignAuthor("Liu Yuefen");
          
		  //login 
		  spogServer.userLogin(loginUser, loginPassword);
		  String token=spogServer.getJWTToken();
		  
		  //setToken for userspogserver
		  userSpogServer.setToken(token);
		  System.out.println("token"+token);
		  
		  //get orders
		  Response response =userSpogServer.getOrdersForLoggedInUser();
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		  
	  }
	  
	  @Test 
	  public void getOrderSuccessfulWithCheck() {
		  System.out.println("getOrderSuccessfulWithCheck");
		  test = rep.startTest("getOrderSuccessfulWithCheck");
		  test.assignAuthor("Liu Yuefen");
          
		  //login 
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword);
		  
		  //create org
		  String userName=spogServer.ReturnRandom("msp_yuefen@spogqa.com");
		  String orgId=spogServer.CreateOrganizationByEnrollWithCheck(spogServer.ReturnRandom("yuefen_msp_name")+org_prefix, "msp", userName, sharePassword, "yuefen", "liu");
		 
		  //create CD
		  //String cloud_direct_account_id=spogServer.createCloudAccountWithCheck(spogServer.ReturnRandom("yuefen_key"), "cloudAccountSecret", spogServer.ReturnRandom("yuefen_name"), "cloud_direct", orgId, "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8) , "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8),"SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8), "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
          //login
		  spogServer.userLogin(userName, sharePassword);
		  String token=spogServer.getJWTToken();
		  //setToken for userspogserver
		  userSpogServer.setToken(token);
		  System.out.println("token"+token);
		  //add order
		  int num =3;
		  String orderIds[] =new String[num];
		  String fulfillmentIds[] =new String[num];
		
		  for (int i=0;i<num;i++) {
			  orderIds[i]="SKUTESTDATA_1"+i+"_8_0_1_"+RandomStringUtils.randomNumeric(8);
			  fulfillmentIds[i]="SKUTESTDATA_1"+i+"_8_0_1_"+RandomStringUtils.randomNumeric(8);
			  userSpogServer.addOrderForLoggedInUserAndCheck(orderIds[i], fulfillmentIds[i], test);
		  }
		  //get orders
		  Response response =userSpogServer.getOrdersForLoggedInUser();
		  
		  ArrayList<HashMap<String, Object>> orderList = new ArrayList<>();
		  orderList =response.then().extract().path("data");
	
		  for(int i=0;i<orderList.size();i++) {
			  HashMap<String, Object> orderInfo = orderList.get(i);
			  if (orderInfo.get("order_id").equals(orderIds[i])) {

				  assertTrue("compare orders pass",true);
				  
			  }else {
				  assertTrue("compare orders fail",false);
			  }
				 
		  }
		  //recycle volume
//		  spogDestinationServer.recycleCloudVolumesAndDelOrg(orgId,test);
	  }
	  
	  @DataProvider(name = "orderInfo")
	  public final Object[][] getOrderInfo() {
		  return new Object[][] { 
					  {spogServer.ReturnRandom("yuefen_directorg_name"),"direct",spogServer.ReturnRandom("yuefen_direct@spogqa.com"),sharePassword,"yuefen","liu","SKUTESTDATA_30_8_0_1_"+RandomStringUtils.randomNumeric(8),"SKUTESTDATA_30_8_0_1_"+RandomStringUtils.randomNumeric(8)}, 
					  {spogServer.ReturnRandom("yuefen_msporg_name"),"msp",spogServer.ReturnRandom("yuefen_msp@spogqa.com"),sharePassword,"yuefen","liu","SKUTESTDATA_30_8_0_1_"+RandomStringUtils.randomNumeric(8),"SKUTESTDATA_30_8_0_1_"+RandomStringUtils.randomNumeric(8)}, 
			          }; 
		}
	  
	  @Test (dataProvider = "orderInfo")
	  public void getOrderSuccess(String organizationName, String organizationType, String organizationEmail, String organizationPwd, String organizationFirstName, String organizationLastName, String orderID, String fulfillmentID) {
		  System.out.println("getOrderSuccess");
		  test = rep.startTest("getOrderSuccess");
		  test.assignAuthor("Liu Yuefen");
          
		  //login 
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword);
		  //create org
		  String orgId=spogServer.CreateOrganizationByEnrollWithCheck(organizationName+org_prefix, organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName);
		  //create CD
		  //String cloud_direct_account_id=spogServer.createCloudAccountWithCheck(spogServer.ReturnRandom("yuefen_key"), "cloudAccountSecret", spogServer.ReturnRandom("yuefen_name"), "cloud_direct", orgId, "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8) , "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8),"SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8), "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
          //login
		  spogServer.userLogin(organizationEmail, organizationPwd);
		  String token =spogServer.getJWTToken();
		  //create order
		  userSpogServer.setToken(token);
		  userSpogServer.addOrderForLoggedInUserAndCheck(orderID, fulfillmentID, test);
		  //get order
		  Response response =userSpogServer.getOrdersForLoggedInUser();
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		  //recycle
//		  spogDestinationServer.recycleCloudVolumesAndDelOrg(orgId,test);
		  }
	  
	  @DataProvider(name = "orderInfo2")
	  public final Object[][] getOrderInfo2() {
		  return new Object[][] { 
					  {csr_token, SpogConstants.SUCCESS_GET_PUT_DELETE,""}, 
					  {direct_token,SpogConstants.SUCCESS_GET_PUT_DELETE,""}, 
					  {msp2_token,SpogConstants.SUCCESS_GET_PUT_DELETE,""}, 
					  {msp_account_admin_token,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101"}, 
					  {account_token,SpogConstants.SUCCESS_GET_PUT_DELETE,""}, 
		  }; 
		}
	  
	  @Test (dataProvider = "orderInfo2")
	  public void getOrderByDifferentUser(String token, int expectedStatusCode, String expectedErrorCode) {
		  System.out.println("getOrderByDifferentUser");
		  test = rep.startTest("getOrderByDifferentUser");
		  test.assignAuthor("Liu Yuefen");
		  
		  //get order
		  userSpogServer.setToken(token);
		  Response response =userSpogServer.getOrdersForLoggedInUser();
		  spogServer.checkResponseStatus(response, expectedStatusCode);
		  if(expectedStatusCode !=200) {
			  spogServer.checkErrorCode(response, expectedErrorCode);
		  }

		  }
		  
	  @Test
	  public void getOrderFail401(){
		  System.out.println("getOrderFail401");
		  test = rep.startTest("getOrderFail401");
		  test.assignAuthor("Liu Yuefen");
		  
		  userSpogServer.setToken("");
		  //get oder
		  Response response =userSpogServer.getOrdersForLoggedInUser();
		  spogServer.checkResponseStatus(response, SpogConstants.NOT_LOGGED_IN);
		  spogServer.checkErrorCode(response, "00900006");
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
