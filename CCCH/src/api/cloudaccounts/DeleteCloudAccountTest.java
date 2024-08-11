package api.cloudaccounts;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.SpogConstants;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import bsh.org.objectweb.asm.Constants;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import groovyjarjarantlr.PreservingFileWriter;
import io.restassured.response.Response;

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
public class DeleteCloudAccountTest {
	private SPOGServer spogServer;
	private UserSpogServer userSpogServer;
	  private String csrOrg_id;
	  private String directOrg_id;
	  private String directOrg2_id;
	  private String mspOrg_id;
	  private String mspOrg2_id;
	  private String account_id;
	  private String account2_id;
	  private String csr_email;
	  private String msp_account_admin_email;
	  private String msp_email;
	  private String direct_email;
	  private String accountDirect_email;
	  private String msp_account_admin_id;
	  private String sharePassword = "Caworld_2018";
	  private ExtentReports rep;
	  private ExtentTest test;
	  private Response response;
	  private String csrGlobalLoginUser;
	  private String csrGlobalLoginPassword;
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
		  //end 
		  spogServer = new SPOGServer(baseURI, port);
		  userSpogServer = new UserSpogServer(baseURI, port);
		  rep = ExtentManager.getInstance("DeleteCloudAccountTest",logFolder);
		  test = rep.startTest("beforeClass");
		  spogServer.userLogin(userName, password);
		  String token=spogServer.getJWTToken();
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
		  this.csr_email= spogServer.ReturnRandom("csr_yuefen@spog.com");
		  spogServer.createUserAndCheck(csr_email, sharePassword, "yuefen", "liu", "csr_admin", csrOrg_id, test);
		  
		  this.direct_email = spogServer.ReturnRandom("direct_admin_yuefen@spog.com");
		  spogServer.createUserAndCheck(direct_email, sharePassword, "yuefen", "liu", "direct_admin", directOrg_id, test);
		  
		  this.msp_email= spogServer.ReturnRandom("msp_admin_yuefen@spog.com");
		  spogServer.createUserAndCheck(msp_email, sharePassword, "yuefen", "liu", "msp_admin", mspOrg_id, test);
		  
		  this.msp_account_admin_email = spogServer.ReturnRandom("msp_account_admin_yuefen@spog.com");
		  msp_account_admin_id = spogServer.createUserAndCheck(msp_account_admin_email, sharePassword, "yuefen", "liu", "msp_account_admin", mspOrg_id, test);
		  
		  this.accountDirect_email= spogServer.ReturnRandom("account_admin_yuefen@spog.com");
		  spogServer.createUserAndCheck(accountDirect_email, sharePassword, "yuefen", "liu", "direct_admin", account_id, test);
	  }
	  
//	  @Test
//	  public void mspAccountAdminDeleteS3Success() {
//		  System.out.println("mspAccountAdminDeleteS3Success");
//		  test = rep.startTest("mspAccountAdminDeleteS3Success");
//		  test.assignAuthor("Liu Yuefen");
//		  
//		  //login
//		  spogServer.userLogin(this.csrGlobalLoginUser, this.csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
//		  String token= spogServer.getJWTToken();
//		  
//		  //create org
//		  String organizationID = spogServer.createAccountWithCheck(mspOrg_id,spogServer.ReturnRandom("spogqa_accoun1_msp1_yuefen_group"), mspOrg_id, test);
//		  userSpogServer.assignMspAccountAdmins(mspOrg_id, organizationID, msp_account_admin_id, token);
//		  
//		  //login with msp account admin
//		  spogServer.userLogin(this.msp_account_admin_email, sharePassword, SpogConstants.SUCCESS_LOGIN, test);
//		  
//		  //create cloud account
//		  test.log(LogStatus.INFO, "using csr admin create cloud accounts");
//		  String cloud_account_id = spogServer.createCloudAccountWithCheck(spogServer.ReturnRandom("spogqa_account_s3accountkey"), spogServer.ReturnRandom("account_s3account0_secret"), spogServer.ReturnRandom("account_s3account_name"), "aws_s3", organizationID, null, null, test);
//		  
//		  //delete cloud account
//		  test.log(LogStatus.INFO, "using csr admin delete cloud accounts");
//		  spogServer.deleteCloudAccountWithExpectedStatusCode(cloud_account_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
//		  
//		  //delete org
//		  spogServer.setToken(token);
//		  spogServer.deleteMSPAccountWithCheck(mspOrg_id, organizationID);
//	  }
	  
	  @DataProvider(name = "cloudAccountInfo9")
	  public final Object[][] getCloudAccountInfo9() {
		  return new Object[][] { 
			  {spogServer.ReturnRandom("spogqa0_direct_s3accountkey"),spogServer.ReturnRandom("direct_s3account0_secret"),spogServer.ReturnRandom("direct_s3account0_name"),"aws_s3","direct", null, null},
			  {spogServer.ReturnRandom("spogqa0_msp_s3accountkey"),spogServer.ReturnRandom("msp_s3account0_secret"),spogServer.ReturnRandom("msp_s3account0_name"),"aws_s3","msp", null, null},
			  {spogServer.ReturnRandom("spogqa_account_s3accountkey"),spogServer.ReturnRandom("account_s3account0_secret"),spogServer.ReturnRandom("account_s3account_name"),"aws_s3","child", null, null},
			  {spogServer.ReturnRandom("spogqa0_direct_s3accountkey"),spogServer.ReturnRandom("direct_s3account0_secret"),spogServer.ReturnRandom("direct_s3account0_name"),"cloud_direct","direct", "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8),"SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8)},
			  {spogServer.ReturnRandom("spogqa0_msp_s3accountkey"),spogServer.ReturnRandom("msp_s3account0_secret"),spogServer.ReturnRandom("msp_s3account0_name"),"cloud_direct","msp", "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8),"SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8)},
		  };
		}
	  @Test(dataProvider = "cloudAccountInfo9")
	  public void mspAccountAdminDeleteCloudAccountFail(String cloudAccountKey, String cloudAccountSecret, String cloudAccountName, String cloudAccountType, 
			  String orgType, String orderID, String fulfillmentID) {
		  System.out.println("mspAccountAdminDeleteCloudAccountFail");
		  test = rep.startTest("mspAccountAdminDeleteCloudAccountFail");
		  test.assignAuthor("Liu Yuefen");
		  //login with msp account admin
		  spogServer.userLogin(this.csrGlobalLoginUser, this.csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  String token= spogServer.getJWTToken();
		  
		  //create org
		  String organizationID = null;
		  if (orgType == "child") {
			  organizationID = spogServer.createAccountWithCheck(mspOrg_id,spogServer.ReturnRandom("spogqa_accoun1_msp1_yuefen_group"), mspOrg_id, test);
		  }else {
			  organizationID = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_orgname_yuefen"),orgType,spogServer.ReturnRandom("yuefen_org_email@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("org_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  }
		  
		  //create cloud account
		  test.log(LogStatus.INFO, "using csr admin create cloud accounts");
		  String cloud_account_id = spogServer.createCloudAccountWithCheck(spogServer.ReturnRandom("spogqa_account_s3accountkey"), spogServer.ReturnRandom("account_s3account0_secret"), spogServer.ReturnRandom("account_s3account_name"), "aws_s3", organizationID, null, null, test);
		  
		  //login msp account admin
		  spogServer.userLogin(this.msp_account_admin_email, sharePassword);
		  //delete cloud account
		  test.log(LogStatus.INFO, "using csr admin delete cloud accounts");
		  spogServer.deleteCloudAccountWithExpectedStatusCode(cloud_account_id, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
		  
		  //delete org
		  spogServer.setToken(token);
		  spogServer.deleteMSPAccountWithCheck(mspOrg_id, organizationID);
	  }
	  /*
	  //used by csr user
	  @DataProvider(name = "cloudAccountInfo")
	  public final Object[][] getCloudAccountInfo() {
		  return new Object[][] { 
			  //{spogServer.ReturnRandom("spogqa_csr_directaccountkey"),spogServer.ReturnRandom("csr_directaccount_secret"),spogServer.ReturnRandom("csr_directaccount_name"),"cloud_direct",csrOrg_id}, 
			  //{spogServer.ReturnRandom("spogqa0_csr_s3accountkey"),spogServer.ReturnRandom("csr_s3account0_secret"),spogServer.ReturnRandom("csr_s3account0_name"),"aws_s3",csrOrg_id},
			  {spogServer.ReturnRandom("spogqa0_direct_directaccountkey"),spogServer.ReturnRandom("direct_directaccount0_secret"),spogServer.ReturnRandom("direct_directaccount0_name"),"cloud_direct","direct","SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8),"SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8)}, 
			  {spogServer.ReturnRandom("spogqa0_direct_s3accountkey"),spogServer.ReturnRandom("direct_s3account0_secret"),spogServer.ReturnRandom("direct_s3account0_name"),"aws_s3","direct", null, null},
			  {spogServer.ReturnRandom("spogqa0_msp_directaccountkey"),spogServer.ReturnRandom("msp_directaccount0_secret"),spogServer.ReturnRandom("msp_directaccount0_name"),"cloud_direct","msp","SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8),"SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8)}, 
			  {spogServer.ReturnRandom("spogqa0_msp_s3accountkey"),spogServer.ReturnRandom("msp_s3account0_secret"),spogServer.ReturnRandom("msp_s3account0_name"),"aws_s3","msp", null, null},
			  //{spogServer.ReturnRandom("spogqa_account_directaccountkey"),spogServer.ReturnRandom("account_directaccount_secret"),spogServer.ReturnRandom("account_directaccount_name"),"cloud_direct",account_id}, 
			  {spogServer.ReturnRandom("spogqa_account_s3accountkey"),spogServer.ReturnRandom("account_s3account0_secret"),spogServer.ReturnRandom("account_s3account_name"),"aws_s3","child", null, null},
	          };
		}

	  @Test(dataProvider = "cloudAccountInfo")
	  //csr user can delete any cloud account
	  public void csrUserDeleteCloudAccount(String cloudAccountKey, String cloudAccountSecret, String cloudAccountName, String cloudAccountType, 
			  String orgType, String orderID, String fulfillmentID){	 
		  System.out.println("csrUserDeleteCloudAccount");
		  test = rep.startTest("csrUserDeleteCloudAccount");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login with csr user
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  //create org
		  String organizationID = null;
		  if (orgType == "child") {
			  organizationID = spogServer.createAccountWithCheck(mspOrg_id,spogServer.ReturnRandom("spogqa_accoun1_msp1_yuefen_group"), mspOrg_id, test);
		  }else {
			  organizationID = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_orgname_yuefen"),orgType,spogServer.ReturnRandom("yuefen_org_email@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("org_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  }
		  //create cloud account
		  test.log(LogStatus.INFO, "using csr admin create cloud accounts");
		  String cloud_account_id = spogServer.createCloudAccountWithCheck(cloudAccountKey, cloudAccountSecret, cloudAccountName, cloudAccountType, organizationID, orderID, fulfillmentID, test);
		  
		  //delete cloud account
		  test.log(LogStatus.INFO, "using csr admin delete cloud accounts");
		  spogServer.deleteCloudAccountWithExpectedStatusCode(cloud_account_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  
		  //delete org
	      if (orgType == "child") {
			  spogServer.deleteMSPAccountWithCheck(mspOrg_id, organizationID);
		  }else {	
			  spogServer.DeleteOrganizationWithCheck(organizationID, test);  
		  }
	  }
	  
	//used by direct user
	  @DataProvider(name = "cloudAccountInfo1")
	  public final Object[][] getCloudAccountInfo1() {
		  return new Object[][] { 
			  {spogServer.ReturnRandom("spogqa1_direct_directaccountkey"),spogServer.ReturnRandom("direct_directaccount1_secret"),spogServer.ReturnRandom("direct_directaccount1_name"),"cloud_direct", "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8),"SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8)}, 
			  {spogServer.ReturnRandom("spogqa1_direct_s3accountkey"),spogServer.ReturnRandom("direct_s3account1_secret"),spogServer.ReturnRandom("direct_s3account1_name"),"aws_s3", null, null}
			  };
		}
	  
	  @Test(dataProvider = "cloudAccountInfo1")
	  public void directUserDeleteCloudAccountSuccess(String cloudAccountKey, String cloudAccountSecret, String cloudAccountName, String cloudAccountType, 
			   String orderID, String fulfillmentID){	 
		  System.out.println("directUserDeleteCloudAccountSuccess");
		  test = rep.startTest("directUserDeleteCloudAccountSuccess");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login with csr user
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  //create direct org
		  String email = spogServer.ReturnRandom("yuefen_org_email@spogqa.com");
		  String password = spogServer.ReturnRandom("aaQQdsf11");
		  String organizationID = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_orgname_yuefen"),"direct",email,password,spogServer.ReturnRandom("org_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  
		  //create cloud account
		  test.log(LogStatus.INFO, "using csr admin create cloud accounts");
		  String cloud_account_id = spogServer.createCloudAccountWithCheck(cloudAccountKey, cloudAccountSecret, cloudAccountName, cloudAccountType, organizationID, orderID, fulfillmentID, test);
		  
		  // login with direct user
		  spogServer.userLogin(email, password, test);
		  
		  //delete cloud account
		  test.log(LogStatus.INFO, "using direct admin to delete cloud accounts");
		  spogServer.deleteCloudAccountWithExpectedStatusCode(cloud_account_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  
		  //login with csr user
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  //delete org
		  spogServer.DeleteOrganizationWithCheck(organizationID, test);
	  }
	  
	  @DataProvider(name = "cloudAccountInfo2")
	  public final Object[][] getCloudAccountInfo2() {
		  return new Object[][] { 
			  //{spogServer.ReturnRandom("spogqa_csr_directaccountkey"),spogServer.ReturnRandom("csr_directaccount_secret"),spogServer.ReturnRandom("csr_directaccount_name"),"cloud_direct",csrOrg_id},
			  //{spogServer.ReturnRandom("spogqa2_csr_s3accountkey"),spogServer.ReturnRandom("csr_s3account2_secret"),spogServer.ReturnRandom("csr_s3account2_name"),"aws_s3",csrOrg_id},
			  {spogServer.ReturnRandom("spogqa2_direct2_directaccountkey"),spogServer.ReturnRandom("direct2_directaccount2_secret"),spogServer.ReturnRandom("direct2_directaccount2_name"),"cloud_direct","direct", "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8),"SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8)},
			  {spogServer.ReturnRandom("spogqa2_direct2_s3accountkey"),spogServer.ReturnRandom("direct2_s3account2_secret"),spogServer.ReturnRandom("direct2_s3account2_name"),"aws_s3","direct", null, null},
			  {spogServer.ReturnRandom("spogqa2_msp_directaccountkey"),spogServer.ReturnRandom("msp_directaccount2_secret"),spogServer.ReturnRandom("msp_directaccount2_name"),"cloud_direct","msp", "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8),"SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8)}, 
			  {spogServer.ReturnRandom("spogqa2_msp_s3accountkey"),spogServer.ReturnRandom("msp_s3account2_secret"),spogServer.ReturnRandom("msp_s3account2_name"),"aws_s3","msp", null, null},
			  //{spogServer.ReturnRandom("spogqa_account_directaccountkey"),spogServer.ReturnRandom("account_directaccount_secret"),spogServer.ReturnRandom("account_directaccount_name"),"cloud_direct",account_id}, 
			  {spogServer.ReturnRandom("spogqa2_account_s3accountkey"),spogServer.ReturnRandom("account_s3account2_secret"),spogServer.ReturnRandom("account_s3account2_name"),"aws_s3","child", null, null},
	          };
		}
	  
	  @Test(dataProvider = "cloudAccountInfo2")
	  public void directUserDeleteCloudAccountFail(String cloudAccountKey, String cloudAccountSecret, String cloudAccountName, String cloudAccountType, 
			  String orgType, String orderID, String fulfillmentID){	 
		  System.out.println("directUserDeleteCloudAccount");
		  test = rep.startTest("directUserDeleteCloudAccount");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login with csr user
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  //create org
		  String organizationID = null;
		  if (orgType == "child") {
			  organizationID = spogServer.createAccountWithCheck(mspOrg_id,spogServer.ReturnRandom("spogqa_accoun1_msp1_yuefen_group"), mspOrg_id, test);
		  }else {
			  organizationID = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_orgname_yuefen"),orgType,spogServer.ReturnRandom("yuefen_org_email@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("org_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  }
		  
		  //create cloud account
		  test.log(LogStatus.INFO, "using csr admin create cloud accounts");
		  String cloud_account_id = spogServer.createCloudAccountWithCheck(cloudAccountKey, cloudAccountSecret, cloudAccountName, cloudAccountType, organizationID, orderID, fulfillmentID, test);
		  
		  // login with direct user
		  spogServer.userLogin(direct_email, sharePassword, test);
		  
		  //delete cloud account
		  test.log(LogStatus.INFO, "using direct admin to delete cloud accounts");
		  Response response = spogServer.deleteCloudAccount(cloud_account_id, test);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
		  spogServer.checkErrorCode(response, "00B00005");
		  
		  //login with csr user and delete account
		  test.log(LogStatus.INFO, "using csr admin to delete cloud accounts");
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  spogServer.deleteCloudAccountWithExpectedStatusCode(cloud_account_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  
		  //delete org
	      if (orgType == "child") {
			  spogServer.deleteMSPAccountWithCheck(mspOrg_id, organizationID);
		  }else {
			  spogServer.DeleteOrganizationWithCheck(organizationID, test);  
		  }
	  }
	  
	//used by msp user
	  @DataProvider(name = "cloudAccountInfo3")
	  public final Object[][] getCloudAccountInfo3() {
		  return new Object[][] { 
			  {spogServer.ReturnRandom("spogqa3_msp_directaccountkey"),spogServer.ReturnRandom("msp_directaccount3_secret"),spogServer.ReturnRandom("msp_directaccount3_name"),"cloud_direct","msp","SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8),"SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8)}, 
			  {spogServer.ReturnRandom("spogqa3_msp_s3accountkey"),spogServer.ReturnRandom("msp_s3account3_secret"),spogServer.ReturnRandom("msp_s3account3_name"),"aws_s3","msp", null, null},
			  //{spogServer.ReturnRandom("spogqa_account_directaccountkey"),spogServer.ReturnRandom("account_directaccount_secret"),spogServer.ReturnRandom("account_directaccount_name"),"cloud_direct",account_id}, 
			  {spogServer.ReturnRandom("spogqa3_account_s3accountkey"),spogServer.ReturnRandom("account_s3account3_secret"),spogServer.ReturnRandom("account_s3account3_name"),"aws_s3","child", null, null}
			  };
		}
	  
	  @Test(dataProvider = "cloudAccountInfo3")
	  public void mspUserDeleteCloudAccountSuccess(String cloudAccountKey, String cloudAccountSecret, String cloudAccountName, String cloudAccountType, 
			  String orgType, String orderID, String fulfillmentID){	 
		  System.out.println("mspUserDeleteCloudAccountSuccess");
		  test = rep.startTest("mspUserDeleteCloudAccountSuccess");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login with csr user
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  //create org
		  String organizationID = null;
		  String email = spogServer.ReturnRandom("yuefen_org_email@spogqa.com");
		  String password = spogServer.ReturnRandom("aaQQdsf11");
		  if (orgType == "msp") {
			  organizationID = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_orgname_yuefen"),orgType, email, password,spogServer.ReturnRandom("org_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
			  
			  String cloud_account_id = spogServer.createCloudAccountWithCheck(cloudAccountKey, cloudAccountSecret, cloudAccountName, cloudAccountType, organizationID, orderID, fulfillmentID, test);
			  
			  spogServer.userLogin(email, password, test);
			  
			  spogServer.deleteCloudAccountWithExpectedStatusCode(cloud_account_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test); 
			  
			  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
			  
			  spogServer.DeleteOrganizationWithCheck(organizationID, test); 
		  }else {
			  organizationID = spogServer.createAccountWithCheck(mspOrg_id,spogServer.ReturnRandom("spogqa_accoun1_msp1_yuefen_group"), mspOrg_id, test);
			  
			  String cloud_account_id = spogServer.createCloudAccountWithCheck(cloudAccountKey, cloudAccountSecret, cloudAccountName, cloudAccountType, organizationID, orderID, fulfillmentID, test);
			  
			  spogServer.userLogin(msp_email, sharePassword, test);
			  
			  spogServer.deleteCloudAccountWithExpectedStatusCode(cloud_account_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);  
			  
			  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
			  
			  spogServer.deleteMSPAccountWithCheck(mspOrg_id, organizationID);
		  }
	  }
	  
	  @DataProvider(name = "cloudAccountInfo4")
	  public final Object[][] getCloudAccountInfo4() {
		  return new Object[][] { 
			  //{spogServer.ReturnRandom("spogqa_csr_directaccountkey"),spogServer.ReturnRandom("csr_directaccount_secret"),spogServer.ReturnRandom("csr_directaccount_name"),"cloud_direct",csrOrg_id},
			  //{spogServer.ReturnRandom("spogqa4_csr_s3accountkey"),spogServer.ReturnRandom("csr_s3account4_secret"),spogServer.ReturnRandom("csr_s3account4_name"),"aws_s3",csrOrg_id},
			  {spogServer.ReturnRandom("spogqa4_direct2_directaccountkey"),spogServer.ReturnRandom("direct2_directaccount4_secret"),spogServer.ReturnRandom("direct2_directaccount4_name"),"cloud_direct","direct", "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8),"SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8)},
			  {spogServer.ReturnRandom("spogqa4_direct2_s3accountkey"),spogServer.ReturnRandom("direct2_s3account4_secret"),spogServer.ReturnRandom("direct2_s3account4_name"),"aws_s3","direct", null, null},
			  {spogServer.ReturnRandom("spogqa4_msp2_directaccountkey"),spogServer.ReturnRandom("msp2_directaccount4_secret"),spogServer.ReturnRandom("msp2_directaccount4_name"),"cloud_direct","msp","SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8),"SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8)}, 
			  {spogServer.ReturnRandom("spogqa4_msp2_s3accountkey"),spogServer.ReturnRandom("msp2_s3account4_secret"),spogServer.ReturnRandom("msp2_s3account4_name"),"aws_s3","msp",null, null},
			  //{spogServer.ReturnRandom("spogqa_account2_directaccountkey"),spogServer.ReturnRandom("account2_directaccount_secret"),spogServer.ReturnRandom("account2_directaccount_name"),"cloud_direct",account2_id}, 
			  {spogServer.ReturnRandom("spogqa4_account2_s3accountkey"),spogServer.ReturnRandom("account2_s3account4_secret"),spogServer.ReturnRandom("account2_s3account4_name"),"aws_s3","child",null,null},
	          };
		}
	  
	  @Test(dataProvider = "cloudAccountInfo4")
	  public void mspUserDeleteCloudAccountFail(String cloudAccountKey, String cloudAccountSecret, String cloudAccountName, String cloudAccountType, 
			  String orgType, String orderID, String fulfillmentID){	 
		  System.out.println("mspUserDeleteCloudAccountFail");
		  test = rep.startTest("mspUserDeleteCloudAccountFail");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login with csr user
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  //create org
		  String organizationID = null;
		 
		  if (orgType == "child") {
			  organizationID = spogServer.createAccountWithCheck(mspOrg2_id,spogServer.ReturnRandom("spogqa_accoun1_msp2_yuefen_group"), mspOrg2_id, test);
		  }else {
			  organizationID = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_orgname_yuefen"),orgType,spogServer.ReturnRandom("yuefen_org_email@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("org_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  }
		  
		  //create cloud account
		  test.log(LogStatus.INFO, "using csr admin create cloud accounts");
		  String cloud_account_id = spogServer.createCloudAccountWithCheck(cloudAccountKey, cloudAccountSecret, cloudAccountName, cloudAccountType, organizationID, orderID, fulfillmentID, test);
		  
		  // login with msp user
		  test.log(LogStatus.INFO, "using msp admin to login");
		  spogServer.userLogin(msp_email, sharePassword, test);
		  
		  //delete cloud account
		  test.log(LogStatus.INFO, "using msp admin to delete cloud accounts");
		  Response response = spogServer.deleteCloudAccount(cloud_account_id, test);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
		  spogServer.checkErrorCode(response, "00B00005");
		  
		  //login with csr user and delete success
		  test.log(LogStatus.INFO, "using csr admin login and delete cloud accounts");
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  response = spogServer.deleteCloudAccount(cloud_account_id, test);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  
		  //delete org
	      if (orgType == "child") {
			  spogServer.deleteMSPAccountWithCheck(mspOrg2_id, organizationID);
		  }else {
			  spogServer.DeleteOrganizationWithCheck(organizationID, test);  
		  }
	  }
	  
	//used by account user
	  @DataProvider(name = "cloudAccountInfo5")
	  public final Object[][] getCloudAccountInfo5() {
		  return new Object[][] { 
			  //{spogServer.ReturnRandom("spogqa_account_directaccountkey"),spogServer.ReturnRandom("account_directaccount_secret"),spogServer.ReturnRandom("account_directaccount_name"),"cloud_direct",account_id}, 
			  {spogServer.ReturnRandom("spogqa5_account_s3accountkey"),spogServer.ReturnRandom("account_s3account5_secret"),spogServer.ReturnRandom("account_s3account5_name"),"aws_s3", null, null}
			  };
		}
	  
	  @Test(dataProvider = "cloudAccountInfo5")
	  public void accountUserDeleteCloudAccountSuccess(String cloudAccountKey, String cloudAccountSecret, String cloudAccountName, String cloudAccountType, 
			  String orderID, String fulfillmentID){	 
		  System.out.println("accountUserDeleteCloudAccountSuccess");
		  test = rep.startTest("accountUserDeleteCloudAccountSuccess");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login with csr user
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  //create org, user
		  String organizationID = spogServer.createAccountWithCheck(mspOrg2_id,spogServer.ReturnRandom("spogqa_accoun1_msp2_yuefen_group"), mspOrg2_id, test);
		  String email = spogServer.ReturnRandom("spogqa_account_msp2@spogqa.com");
		  spogServer.createUser(email, sharePassword, "yuefen", "liu", "direct_admin", organizationID, test);
		  
		  //create cloud account
		  test.log(LogStatus.INFO, "using csr admin create cloud accounts");
		  String cloud_account_id = spogServer.createCloudAccountWithCheck(cloudAccountKey, cloudAccountSecret, cloudAccountName, cloudAccountType, organizationID, orderID, fulfillmentID, test);
		  
		  // login with account user
		  spogServer.userLogin(email, sharePassword, test);
		  
		  //delete cloud account
		  test.log(LogStatus.INFO, "using account direct admin create cloud accounts");
		  spogServer.deleteCloudAccountWithExpectedStatusCode(cloud_account_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  
		  //delete org
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  spogServer.deleteMSPAccountWithCheck(mspOrg2_id, organizationID);
	  }
	  
	  @DataProvider(name = "cloudAccountInfo6")
	  public final Object[][] getCloudAccountInfo6() {
		  return new Object[][] { 
			  //{spogServer.ReturnRandom("spogqa_csr_directaccountkey"),spogServer.ReturnRandom("csr_directaccount_secret"),spogServer.ReturnRandom("csr_directaccount_name"),"cloud_direct",csrOrg_id},
			  //{spogServer.ReturnRandom("spogqa6_csr_s3accountkey"),spogServer.ReturnRandom("csr_s3account6_secret"),spogServer.ReturnRandom("csr_s3account_name"),"aws_s3",csrOrg_id},
			  {spogServer.ReturnRandom("spogqa6_direct2_directaccountkey"),spogServer.ReturnRandom("direct2_directaccount6_secret"),spogServer.ReturnRandom("direct2_directaccount_name"),"cloud_direct","direct","SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8),"SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8)},
			  {spogServer.ReturnRandom("spogqa6_direct2_s3accountkey"),spogServer.ReturnRandom("direct2_s3account6_secret"),spogServer.ReturnRandom("direct2_s3account_name"),"aws_s3","direct",null, null},
			  {spogServer.ReturnRandom("spogqa6_msp_directaccountkey"),spogServer.ReturnRandom("msp_directaccount6_secret"),spogServer.ReturnRandom("msp_directaccount_name"),"cloud_direct","msp","SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8),"SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8)}, 
			  {spogServer.ReturnRandom("spogqa6_msp_s3accountkey"),spogServer.ReturnRandom("msp_s3account6_secret"),spogServer.ReturnRandom("msp_s3account_name"),"aws_s3","msp",null, null},
			  //{spogServer.ReturnRandom("spogqa6_msp2_directaccountkey"),spogServer.ReturnRandom("msp2_directaccount6_secret"),spogServer.ReturnRandom("msp2_directaccount_name"),"cloud_direct",mspOrg2_id}, 
			  //{spogServer.ReturnRandom("spogqa6_msp2_s3accountkey"),spogServer.ReturnRandom("msp2_s3account6_secret"),spogServer.ReturnRandom("msp2_s3account_name"),"aws_s3",mspOrg2_id},
			  //{spogServer.ReturnRandom("spogqa_account2_directaccountkey"),spogServer.ReturnRandom("account2_directaccount_secret"),spogServer.ReturnRandom("account2_directaccount_name"),"cloud_direct",account2_id}, 
			  {spogServer.ReturnRandom("spogqa_account2_s3accountkey"),spogServer.ReturnRandom("account2_s3account_secret"),spogServer.ReturnRandom("account2_s3account_name"),"aws_s3","child",null,null},
	          };
		}
	  
	  @Test(dataProvider = "cloudAccountInfo6")
	  public void accountUserDeleteCloudAccountFail(String cloudAccountKey, String cloudAccountSecret, String cloudAccountName, String cloudAccountType, 
			  String orgType, String orderID, String fulfillmentID){	 
		  System.out.println("accountUserDeleteCloudAccountFail");
		  test = rep.startTest("accountUserDeleteCloudAccountFail");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login with csr user
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  //create org
		  String organizationID = null;
		  if (orgType == "child") {
			  organizationID = spogServer.createAccountWithCheck(mspOrg_id,spogServer.ReturnRandom("spogqa_accoun1_msp1_yuefen_group"), mspOrg_id, test);
		  }else {
			  organizationID = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_orgname_yuefen"),orgType,spogServer.ReturnRandom("yuefen_org_email@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("org_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  }
		  
		  //create cloud account
		  test.log(LogStatus.INFO, "using csr admin create cloud accounts");
		  String cloud_account_id = spogServer.createCloudAccountWithCheck(cloudAccountKey, cloudAccountSecret, cloudAccountName, cloudAccountType, organizationID, orderID, fulfillmentID, test);
		  
		  // login with account user
		  spogServer.userLogin(accountDirect_email, sharePassword, test);
		  
		  //delete cloud account
		  test.log(LogStatus.INFO, "using account direct admin to delete cloud accounts");
		  Response response = spogServer.deleteCloudAccount(cloud_account_id, test);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
		  spogServer.checkErrorCode(response, "00B00005");
		  
		  //login with csr user and delete account
		  test.log(LogStatus.INFO, "using csr admin to delete cloud accounts");
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  
	      if (orgType == "child") {
			  spogServer.deleteMSPAccountWithCheck(mspOrg_id, organizationID);
		  }else {
			  spogServer.DeleteOrganizationWithCheck(organizationID, test);  
		  }
	  }
      
	  //create one cloud account for normal check
	  @DataProvider(name = "cloudAccountInfo7")
	  public final Object[][] getCloudAccountInfo7() {
		  return new Object[][] { 
			  {spogServer.ReturnRandom("spogqa7_direct7_directaccountkey"),spogServer.ReturnRandom("direct7_directaccount_secret"),spogServer.ReturnRandom("direct7_directaccount_name"),"aws_s3",directOrg_id,null,null}
	          };
		}
	  
	  @Test(dataProvider = "cloudAccountInfo7")
	  public void DeleteCloudAccountNormalCheck(String cloudAccountKey, String cloudAccountSecret, String cloudAccountName, String cloudAccountType,
			  String organizationID, String orderID, String fulfillmentID){	 
		  System.out.println("DeleteCloudAccountNormalCheck");
		  test = rep.startTest("DeleteCloudAccountNormalCheck");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login with csr user
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  //create cloud account
		  String cloud_account_id = spogServer.createCloudAccountWithCheck(cloudAccountKey, cloudAccountSecret, cloudAccountName, cloudAccountType, organizationID, orderID, fulfillmentID, test);
		  
		  //delete cloud account id=null
		 spogServer.deleteCloudAccountFailWithCheck(null, SpogConstants.REQUIRED_INFO_NOT_EXIST, "40000005", test);
		 
		 //delete cloud account id=""
		 spogServer.deleteCloudAccountFailWithCheck("", SpogConstants.NOT_ALLOWED_ON_RESOURCE, "00900002",test);
		 
		 //delete cloud account id ="89b65035-75e1-4543-8e01-dbede65e2d65"
		 spogServer.deleteCloudAccountFailWithCheck("89b65035-75e1-4543-8e01-dbede65e2d65", SpogConstants.RESOURCE_NOT_EXIST, "00B00002", test);
		 
		 //delete cloud account twice
		 spogServer.deleteCloudAccountWithExpectedStatusCode(cloud_account_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		 spogServer.deleteCloudAccountFailWithCheck("cloud_account_id", SpogConstants.REQUIRED_INFO_NOT_EXIST, "40000005", test);
	  }
	  
	  //create one cloud account for normal check
	  @DataProvider(name = "cloudAccountInfo8")
	  public final Object[][] getCloudAccountInfo8() {
		  return new Object[][] { 
			  {spogServer.ReturnRandom("spogqa8_direct8_directaccountkey"),spogServer.ReturnRandom("direct8_directaccount_secret"),spogServer.ReturnRandom("direct8_directaccount_name"),"aws_s3",directOrg_id, null,null}
	          };
		}
	  
	  @Test(dataProvider = "cloudAccountInfo8")
	  public void DeleteCloudAccountFail401(String cloudAccountKey, String cloudAccountSecret, String cloudAccountName, String cloudAccountType, 
			  String organizationID, String orderID, String fulfillmentID){	 
		  System.out.println("DeleteCloudAccountFail401");
		  test = rep.startTest("DeleteCloudAccountFail401");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login with csr user
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  //create cloud account
		  String cloud_account_id = spogServer.createCloudAccountWithCheck(cloudAccountKey, cloudAccountSecret, cloudAccountName, cloudAccountType, organizationID, orderID, fulfillmentID, test);
		  
         //reset token
		  spogServer.setToken("");
		 
		 //delete cloud account without login
		 spogServer.deleteCloudAccountFailWithCheck(cloud_account_id, SpogConstants.NOT_LOGGED_IN,  "00900006", test);
		 
		 //login and delete
		 spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		 spogServer.deleteCloudAccountWithExpectedStatusCode(cloud_account_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	  }
	  */
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
