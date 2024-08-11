package api.cloudaccounts;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.SpogConstants;
import InvokerServer.SPOGServer;
import bsh.org.objectweb.asm.Constants;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import groovyjarjarantlr.PreservingFileWriter;
import io.restassured.response.Response;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;
import java.util.Arrays;

import java.io.IOException;
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
import org.testng.annotations.Test;

public class AddOrderToCloudAccountTest {
	  private SPOGServer spogServer;
	  private String csrOrg_id;
	  private String directOrg_id;
	  private String directOrg2_id;
	  private String mspOrg_id;
	  private String mspOrg2_id;
	  private String account_id;
	  private String account2_id;
	  private String msp_email;
	  private String msp_account_admin_email;
	  private String direct_email;
	  private String accountDirect_email;
	  private String sharePassword = "Caworld_2018";
	  private ExtentReports rep;
	  private ExtentTest test;
	  private Response response;
	  private String csrGlobalLoginUser;
	  private String csrGlobalLoginPassword;
	  private String directOrgCloudAccountID;
	  private String mspOrgcloudAccountID;
	  private String directS3AccountID;
	  private String mspS3AccountID;
	  private String csrAdminToken;
	  private String directAdminToken;
	  private String mspAdminToken;
	  private String mspAccountAdminToken;
	  private String accountDirectAdminToken;
	//this is for update portal, each testng class is taken as BQ set
	  private SQLServerDb bqdb1;
	  public int Nooftest;
	  private long creationTime;
	  private String BQName=null;
	  private String runningMachine;
	  private testcasescount count1;
	  private String buildVersion;
	  //end 
	  
	  @BeforeClass
	  
	  @Parameters({ "baseURI", "port" , "csrAdminUserName", "csrAdminPassword","logFolder","runningMachine", "buildVersion"})
	  public void beforeClass(String baseURI, String port, String userName, String password, String logFolder, String runningMachine, String buildVersion) {
		
		//this is for update portal
		  this.BQName = this.getClass().getSimpleName();
		  String author = "yuefen.liu";
		  this.runningMachine = runningMachine;
		  SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		  java.util.Date date=new java.util.Date();
		  this.buildVersion=buildVersion+"_"+dateFormater.format(date);
		  Nooftest=0;
		  bqdb1 = new SQLServerDb();
		  count1 = new testcasescount();
		  if(count1.isstarttimehit()==0) {
			System.out.println("AddOrderToCloudAccountTest");
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
		  spogServer = new SPOGServer(baseURI, port);
		  rep = ExtentManager.getInstance("AddOrderToCloudAccountTest",logFolder);
		  test = rep.startTest("beforeClass");
		  spogServer.userLogin(userName, password);
		  this.csrGlobalLoginUser = userName;
		  this.csrGlobalLoginPassword = password;
		  //create org
		  csrOrg_id = spogServer.GetLoggedinUserOrganizationID();
		  System.out.println("csr org id:"+csrOrg_id);
		  directOrg_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_group_direct1_yuefen"),"direct",spogServer.ReturnRandom("yuefen_direct1_group@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("direct_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  directOrg2_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_group_direct2_yuefen"),"direct",spogServer.ReturnRandom("yuefen_direct2_group@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("direct2_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  mspOrg_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_group_yuefen_msp1"),"msp",spogServer.ReturnRandom("yuefen_msp1_group@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("msp_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  mspOrg2_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_group_yuefen_msp2"),"msp",spogServer.ReturnRandom("yuefen_msp2_group@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("msp2_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  account_id = spogServer.createAccountWithCheck(mspOrg_id,spogServer.ReturnRandom("spogqa_accoun1_msp1_yuefen_group"), mspOrg_id, test);
		  account2_id = spogServer.createAccountWithCheck(mspOrg2_id, spogServer.ReturnRandom("spogqa_accoun2_msp2_yuefen_spogqa"), mspOrg2_id, test);
		  
		  //create user
		  this.direct_email = spogServer.ReturnRandom("direct_admin_yuefen@spog.com");
		  spogServer.createUserAndCheck(direct_email, sharePassword, "yuefen", "liu", "direct_admin", directOrg_id, test);
		  
		  this.msp_email= spogServer.ReturnRandom("msp_admin_yuefen@spog.com");
		  spogServer.createUserAndCheck(msp_email, sharePassword, "yuefen", "liu", "msp_admin", mspOrg_id, test);
		  
		  this.msp_account_admin_email=spogServer.ReturnRandom("msp_admin_yuefen@spog.com");
		  spogServer.createUserAndCheck(msp_account_admin_email, sharePassword, "yuefen", "liu", "msp_account_admin", mspOrg_id, test);
		  
		  this.accountDirect_email= spogServer.ReturnRandom("msp_admin_yuefen@spog.com");
		  spogServer.createUserAndCheck(accountDirect_email, sharePassword, "yuefen", "liu", "direct_admin", account_id, test);
		  
		  //create cloud direct
		  String directOrderID = "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8);
		  String directFulfillmentID = "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8);
		  this.directOrgCloudAccountID = spogServer.createCloudAccountWithCheck(null, null, spogServer.ReturnRandom("direct_cloudAccount_order"), "cloud_direct", directOrg_id, directOrderID, directFulfillmentID, test);
		  String mspOrderID = "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8);
		  String mspFulfillmentID = "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8);
		  this.mspOrgcloudAccountID = spogServer.createCloudAccountWithCheck(null, null, spogServer.ReturnRandom("msp_cloudAccount_order"), "cloud_direct", mspOrg_id, mspOrderID, mspFulfillmentID, test);
		  
		  //create s3 account
		  
		  this.directS3AccountID = spogServer.createCloudAccountWithCheck(spogServer.ReturnRandom("direct_s3_key"), spogServer.ReturnRandom("direct_s3_secret"), spogServer.ReturnRandom("direct_s3_name"), "aws_s3", directOrg_id, null, null, test);
		  this.mspS3AccountID = spogServer.createCloudAccountWithCheck(spogServer.ReturnRandom("msp_s3_key"), spogServer.ReturnRandom("msp_s3_secret"), spogServer.ReturnRandom("msp_s3_name"), "aws_s3", mspOrg_id, null, null, test);
		  //Token
		  Response response = spogServer.login(userName, password);
		  csrAdminToken = response.then().extract().path("data.token");
		  
		  response = spogServer.login(direct_email, sharePassword);
		  directAdminToken = response.then().extract().path("data.token");
		  
		  response = spogServer.login(msp_email, sharePassword);
		  mspAdminToken = response.then().extract().path("data.token");
		  
		  response = spogServer.login(msp_account_admin_email, sharePassword);
		  mspAccountAdminToken = response.then().extract().path("data.token");
		  
		  response = spogServer.login(accountDirect_email, sharePassword);
		  accountDirectAdminToken = response.then().extract().path("data.token");
	  }
	  
	  @DataProvider(name = "orderInfo")
	  public final Object[][] getOrderInfo() {
		  return new Object[][] { 
			  {csrAdminToken,directOrgCloudAccountID, "SKUTESTDATA_1_0_0_0_","SKUTESTDATA_1_0_0_0_", directOrg_id},
			  {csrAdminToken,directOrgCloudAccountID, "SKUTESTDATA_1_0_0_0_","abc", directOrg_id},
			  {csrAdminToken, mspOrgcloudAccountID, "SKUTESTDATA_1_0_0_0_","SKUTESTDATA_1_0_0_0_", mspOrg_id},
			  
			  {directAdminToken,directOrgCloudAccountID, "SKUTESTDATA_1_0_0_0_","SKUTESTDATA_1_0_0_0_", directOrg_id},
			  {directAdminToken,directOrgCloudAccountID, "SKUTESTDATA_1_0_0_0_","SKUTESTDATA_1_0_0_0_", ""},
			  
			  {mspAdminToken, mspOrgcloudAccountID, "SKUTESTDATA_1_0_0_0_","SKUTESTDATA_1_0_0_0_", mspOrg_id},
			  {mspAdminToken, mspOrgcloudAccountID, "SKUTESTDATA_1_0_0_0_","SKUTESTDATA_1_0_0_0_", ""}
	          };
		}
	  @Test(dataProvider = "orderInfo")
	  public void AddOrderToCloudDirectSuccess( String token, String cloudAccountID, String orderID, String fulfillmentID, String organizationID){	 
		  System.out.println("AddOrderToCloudDirectSuccess");
		  test = rep.startTest("AddOrderToCloudDirectSuccess");
		  test.assignAuthor("Liu Yuefen");
		  
		  String postfix = RandomStringUtils.randomNumeric(8);
		  orderID = orderID + postfix;
		  fulfillmentID = fulfillmentID + postfix;
		  
		  //set token
		  spogServer.setToken(token);
		  
		  //add order
		  spogServer.addOrderTocloudDirectOrderWithCheck(cloudAccountID, orderID, fulfillmentID, organizationID, SpogConstants.SUCCESS_POST, "", test);
	  }
	  
	  @DataProvider(name = "orderInfo2")
	  public final Object[][] getOrderInfo2() {
		  return new Object[][] { 
			  {accountDirectAdminToken,directOrgCloudAccountID, "SKUTESTDATA_1_0_0_0_","SKUTESTDATA_1_0_0_0_", directOrg_id},
			  {accountDirectAdminToken, mspOrgcloudAccountID, "SKUTESTDATA_1_0_0_0_","SKUTESTDATA_1_0_0_0_", mspOrg_id},
			  
			  {mspAdminToken,directOrgCloudAccountID, "SKUTESTDATA_1_0_0_0_","SKUTESTDATA_1_0_0_0_", directOrg_id},
			  {mspAdminToken, directS3AccountID, "SKUTESTDATA_1_0_0_0_","SKUTESTDATA_1_0_0_0_", directOrg_id},
			  
			  {directAdminToken, mspOrgcloudAccountID, "SKUTESTDATA_1_0_0_0_","SKUTESTDATA_1_0_0_0_", mspOrg_id},
			  
			  {mspAccountAdminToken, directOrgCloudAccountID, "SKUTESTDATA_1_0_0_0_","SKUTESTDATA_1_0_0_0_", directOrg_id},
			  {mspAccountAdminToken, mspOrgcloudAccountID, "SKUTESTDATA_1_0_0_0_","SKUTESTDATA_1_0_0_0_", mspOrg_id},
	          };
		}
	  @Test(dataProvider = "orderInfo2")
	  public void AddOrderToCloudDirectPermissionFail( String token, String cloudAccountID, String orderID, String fulfillmentID, String organizationID){	 
		  System.out.println("AddOrderToCloudDirectPermissionFail");
		  test = rep.startTest("AddOrderToCloudDirectPermissionFail");
		  test.assignAuthor("Liu Yuefen");
		  
		  String postfix = RandomStringUtils.randomNumeric(8);
		  orderID = orderID + postfix;
		  fulfillmentID = fulfillmentID + postfix;
		  
		  //set token
		  spogServer.setToken(token);
		  
		  //add order
		  spogServer.addOrderTocloudDirectOrderWithCheck(cloudAccountID, orderID, fulfillmentID, organizationID, SpogConstants.INSUFFICIENT_PERMISSIONS, "00100101", test);
	  }
	  
	  @DataProvider(name = "orderInfo3")
	  public final Object[][] getOrderInfo3() {
		  return new Object[][] { 
			  {directAdminToken, directS3AccountID, "SKUTESTDATA_1_0_0_0_","SKUTESTDATA_1_0_0_0_", directOrg_id},
			  {mspAdminToken, mspS3AccountID, "SKUTESTDATA_1_0_0_0_","SKUTESTDATA_1_0_0_0_", mspOrg_id}
	          };
		}
	  @Test(dataProvider = "orderInfo3")
	  public void AddOrderToS3Fail( String token, String cloudAccountID, String orderID, String fulfillmentID, String organizationID){	 
		  System.out.println("AddOrderToS3Fail");
		  test = rep.startTest("AddOrderToS3Fail");
		  test.assignAuthor("Liu Yuefen");
		  
		  String postfix = RandomStringUtils.randomNumeric(8);
		  orderID = orderID + postfix;
		  fulfillmentID = fulfillmentID + postfix;
		  
		  //set token
		  spogServer.setToken(token);
		  
		  //add order
		  spogServer.addOrderTocloudDirectOrderWithCheck(cloudAccountID, orderID, fulfillmentID, organizationID, SpogConstants.REQUIRED_INFO_NOT_EXIST, "00B0000D", test);
	  }
	  
	  @DataProvider(name = "orderInfo4")
	  public final Object[][] getOrderInfo4() {
		  return new Object[][] { 
			  {directAdminToken,directOrgCloudAccountID, "SKUTESTDATA_1_0_0_0_","SKUTESTDATA_1_0_0_0_", ""},
			  {mspAdminToken, mspOrgcloudAccountID, "SKUTESTDATA_1_0_0_0_","SKUTESTDATA_1_0_0_0_", ""}
	          };
		}
	  @Test(dataProvider = "orderInfo4")
	  public void AddDuplicateOrderToCD( String token, String cloudAccountID, String orderID, String fulfillmentID, String organizationID){	 
		  System.out.println("AddDuplicateOrderToCD");
		  test = rep.startTest("AddDuplicateOrderToCD");
		  test.assignAuthor("Liu Yuefen");
		  
		  String postfix = RandomStringUtils.randomNumeric(8);
		  orderID = orderID + postfix;
		  fulfillmentID = fulfillmentID + postfix;
		  
		  //set token
		  spogServer.setToken(token);
		  
		  //add order
		  System.out.println("add the order first time");
		  Response response = spogServer.addOrderTocloudDirectOrder(cloudAccountID, orderID, fulfillmentID, organizationID, test);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		  System.out.println("add the order second time");
		  response = spogServer.addOrderTocloudDirectOrder(cloudAccountID, orderID, fulfillmentID, organizationID, test);
		  spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
		  //spogServer.checkErrorCode(response, expectedErrorCode);
	  }
	  
	  @DataProvider(name = "orderInfo5")
	  public final Object[][] getOrderInfo5() {
		  return new Object[][] { 
			  {directAdminToken,null, "SKUTESTDATA_1_0_0_0_","SKUTESTDATA_1_0_0_0_", directOrg_id},
			  {mspAdminToken, null, "SKUTESTDATA_1_0_0_0_","SKUTESTDATA_1_0_0_0_", mspOrg_id}
	          };
		}

	  @Test(dataProvider = "orderInfo5")
	  public void AddOrderToCloudDirectNormalCheck( String token, String cloudAccountID, String orderID, String fulfillmentID, String organizationID){	 
		  System.out.println("AddOrderToCloudDirectNormalCheck");
		  test = rep.startTest("AddOrderToCloudDirectNormalCheck");
		  test.assignAuthor("Liu Yuefen");
		  
		  String postfix = RandomStringUtils.randomNumeric(8);
		  orderID = orderID + postfix;
		  fulfillmentID = fulfillmentID + postfix;
		  
		  //set token
		  spogServer.setToken(token);
		  
		  //add order
		  System.out.println("case: cloudaccountID =null");
		  spogServer.addOrderTocloudDirectOrderWithCheck(cloudAccountID, orderID, fulfillmentID, organizationID, SpogConstants.REQUIRED_INFO_NOT_EXIST, "40000005", test);
	  }
	  
	  @DataProvider(name = "orderInfo6")
	  public final Object[][] getOrderInfo6() {
		  return new Object[][] { 
			  {directAdminToken,"", "SKUTESTDATA_1_0_0_0_","SKUTESTDATA_1_0_0_0_", directOrg_id},
			  {mspAdminToken, "", "SKUTESTDATA_1_0_0_0_","SKUTESTDATA_1_0_0_0_", mspOrg_id}
	          };
		}

	  @Test(dataProvider = "orderInfo6")
	  public void AddOrderToCloudDirectNormalCheck2( String token, String cloudAccountID, String orderID, String fulfillmentID, String organizationID){	 
		  System.out.println("AddOrderToCloudDirectNormalCheck2");
		  test = rep.startTest("AddOrderToCloudDirectNormalCheck2");
		  test.assignAuthor("Liu Yuefen");
		  
		  String postfix = RandomStringUtils.randomNumeric(8);
		  orderID = orderID + postfix;
		  fulfillmentID = fulfillmentID + postfix;
		  
		  //set token
		  spogServer.setToken(token);
		  
		  //add order
		  System.out.println("cloudAccountID = blank");
		  spogServer.addOrderTocloudDirectOrderWithCheck(cloudAccountID, orderID, fulfillmentID, organizationID, SpogConstants.NOT_ALLOWED_ON_RESOURCE, "00900002", test);
	  }
	  
	  
	  @DataProvider(name = "orderInfo8")
	  public final Object[][] getOrderInfo8() {
		  return new Object[][] { 
			  {directAdminToken, directOrgCloudAccountID, null,"SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8), ""},
			  {mspAdminToken, mspOrgcloudAccountID, "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8),null, ""},
			  {directAdminToken, directOrgCloudAccountID, "","SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8), ""},
			  {mspAdminToken, mspOrgcloudAccountID, "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8), "", ""}
	          };
	  }
	  @Test(dataProvider = "orderInfo8")
	  public void AddOrderToCloudDirectNormalCheck3(String token, String cloudAccountID, String orderID, String fulfillmentID, String organizationID){	 
		  System.out.println("AddOrderToCloudDirectNormalCheck3");
		  test = rep.startTest("AddOrderToCloudDirectNormalCheck3");
		  test.assignAuthor("Liu Yuefen");
		 
		  //set token
		  spogServer.setToken(token);
		  
		  //add order
		  System.out.println("orderID or fulfillmentID = null/blank");
		  spogServer.addOrderTocloudDirectOrderWithCheck(cloudAccountID, orderID, fulfillmentID, organizationID, SpogConstants.REQUIRED_INFO_NOT_EXIST, "40000001", test);
	  }
	    
	  @Test
	  public void AddOrderToCloudDirectNormalCheck5(){	 
		  System.out.println("AddOrderToCloudDirectNormalCheck5");
		  test = rep.startTest("AddOrderToCloudDirectNormalCheck5");
		  test.assignAuthor("Liu Yuefen");
		 
		  //set token
		  spogServer.setToken(directAdminToken);
		  
		  //add order
		  System.out.println("organizationID = blank and token using direct admin");
		  spogServer.addOrderTocloudDirectOrderWithCheck(directOrgCloudAccountID,"SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8), "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8), "", SpogConstants.SUCCESS_POST, "", test);
	  }
	  @Test
	  public void AddOrderToCloudDirectNormalCheck6(){	 
		  System.out.println("AddOrderToCloudDirectNormalCheck6");
		  test = rep.startTest("AddOrderToCloudDirectNormalCheck6");
		  test.assignAuthor("Liu Yuefen");
		 
		  //set token
		  spogServer.setToken(directAdminToken);
		  
		  //add order
		  System.out.println("organizationID = blank and token using csr admin");
		  spogServer.addOrderTocloudDirectOrderWithCheck(directOrgCloudAccountID,"SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8), "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8), "", SpogConstants.SUCCESS_POST, "", test);
	  }
	  
	  @Test
	  public void AddOrderToCloudDirectNormalCheck7(){	 
		  System.out.println("AddOrderToCloudDirectNormalCheck7");
		  test = rep.startTest("AddOrderToCloudDirectNormalCheck7");
		  test.assignAuthor("Liu Yuefen");
		 
		  //set token
		  spogServer.setToken(directAdminToken);
		  
		  //add order
		  System.out.println("cloudAccountID = 00000000-0000-0000-0000-000000000000");
		  spogServer.addOrderTocloudDirectOrderWithCheck("00000000-0000-0000-0000-000000000000","SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8), "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8), "", SpogConstants.RESOURCE_NOT_EXIST, "00900001", test);
	  }
	  
	  @Test
	  public void AddOrderToDeletedCloudDirect(){
	  		  System.out.println("AddOrderToCloudDirectNormalCheck8");
		  test = rep.startTest("AddOrderToCloudDirectNormalCheck8");
		  test.assignAuthor("Liu Yuefen");
		  
	      //set token
		  spogServer.setToken(csrAdminToken);
		  
		  //create cloud account
		  String cloudDirectID2 = spogServer.createCloudAccountWithCheck(null, null, spogServer.ReturnRandom("CD_name"), "cloud_direct", directOrg2_id, "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8), "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8), test);
		  
		  //delete cloud account
		  spogServer.deleteCloudAccountWithExpectedStatusCode(cloudDirectID2, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  
		  //add order
		  System.out.println("orgID = 00900001");
		  Response response = spogServer.addOrderTocloudDirectOrder(cloudDirectID2, "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8), "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8), "", test);
		  spogServer.checkResponseStatus(response, SpogConstants.RESOURCE_NOT_EXIST, test);
		  spogServer.checkErrorCode(response, "00900001");
	  }
	  
	  @Test
	  public void AddOrderToCloudDirectNormalCheck8(){	 
		  System.out.println("AddOrderToCloudDirectNormalCheck8");
		  test = rep.startTest("AddOrderToCloudDirectNormalCheck8");
		  test.assignAuthor("Liu Yuefen");
		 
		  //set token
		  spogServer.setToken(directAdminToken);
		  
		  //add order
		  System.out.println("orgID = 00900001");
		  spogServer.addOrderTocloudDirectOrderWithCheck(directOrgCloudAccountID,"SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8), "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8), "abc", SpogConstants.REQUIRED_INFO_NOT_EXIST, "40000005", test);
	  }

	  @Test
	  public void AddOrderToCloudDirectNormalCheck9(){	 
		  System.out.println("AddOrderToCloudDirectNormalCheck9");
		  test = rep.startTest("AddOrderToCloudDirectNormalCheck9");
		  test.assignAuthor("Liu Yuefen");
		 
		  //set token
		  spogServer.setToken(directAdminToken);
		  
		  //add order

		  System.out.println("orderID = abc");
		  spogServer.addOrderTocloudDirectOrderWithCheck(directOrgCloudAccountID, "abc", "SKUTESTDATA_1_0_0_0_"+ RandomStringUtils.randomNumeric(8), "", SpogConstants.REQUIRED_INFO_NOT_EXIST, "00100001", test);
	  }
	  
	  @Test
	  public void getOrdersFail401(){	 
		  System.out.println("getOrdersFail401");
		  test = rep.startTest("getOrdersFail401");
		  test.assignAuthor("Liu Yuefen");
		  
		  //set token
		  spogServer.setToken("");
		  
		  //Add order
		  Response response = spogServer.addOrderTocloudDirectOrder(directOrgCloudAccountID, "SKUTESTDATA_1_0_0_0_"+ RandomStringUtils.randomNumeric(8), "SKUTESTDATA_1_0_0_0_"+ RandomStringUtils.randomNumeric(8), "", test);
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
	  
	  @AfterTest
	  public void aftertest() {
		  test.log(LogStatus.INFO, "The total test cases passed are "+count1.getpassedcount());
		  test.log(LogStatus.INFO, "the total test cases failed are "+count1.getfailedcount());
		  rep.flush();
	  }
	  
	  @AfterClass
	  public void updatebd() {
		try {
			if(count1.getfailedcount()>0) {
				Nooftest=count1.getpassedcount()+count1.getfailedcount()+count1.getskippedcount();
				bqdb1.updateTable(this.BQName, this.runningMachine, this.buildVersion, String.valueOf(Nooftest), Integer.toString(count1.getpassedcount()), Integer.toString(count1.getfailedcount()), String.valueOf(count1.getskippedcount()), count1.getcreationtime(), "Failed");
			}else {
				Nooftest=count1.getpassedcount()+count1.getfailedcount()+count1.getskippedcount();
				bqdb1.updateTable(this.BQName, this.runningMachine, this.buildVersion, String.valueOf(Nooftest), Integer.toString(count1.getpassedcount()), Integer.toString(count1.getfailedcount()), String.valueOf(count1.getskippedcount()), count1.getcreationtime(), "Passed");
			}
		}catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
}
}
