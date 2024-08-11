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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
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
public class UpdateCloudAccountByIDTest {
	  private SPOGServer spogServer;
	  private UserSpogServer userSpogServer;
	  private String csrOrg_id;
	  private String directOrg_id;
	  private String directOrg2_id;
	  private String mspOrg_id;
	  private String mspOrg2_id;
	  private String account_id;
	  private String account2_id;
	  private String msp_email;
	  private String msp_account_admin_email;
	  private String msp_account_admin_id;
	  private String direct_email;
	  private String accountDirect_email;
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
			System.out.println("UpdateCloudAccountByIDTest");
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
		  rep = ExtentManager.getInstance("UpdateCloudAccountByIDTest",logFolder);
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
		  
		  this.msp_account_admin_email = spogServer.ReturnRandom("msp_account_admin_yuefen@spog.com");
		  msp_account_admin_id = spogServer.createUserAndCheck(msp_account_admin_email, sharePassword, "yuefen", "liu", "msp_account_admin", mspOrg_id, test);
		  
		  this.accountDirect_email= spogServer.ReturnRandom("msp_admin_yuefen@spog.com");
		  spogServer.createUserAndCheck(accountDirect_email, sharePassword, "yuefen", "liu", "direct_admin", account_id, test);
	  }
	  
	  @Test
	  public void mspAccountAdminUpdateS3Success() {
		  System.out.println("mspAccountAdminUpdateS3Success");
		  test = rep.startTest("mspAccountAdminUpdateS3Success");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login
		  spogServer.userLogin(this.csrGlobalLoginUser, this.csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  String token= spogServer.getJWTToken();
		  
		  //create org
		  String organizationID = spogServer.createAccountWithCheck(mspOrg_id,spogServer.ReturnRandom("spogqa_accoun1_msp1_yuefen_group"), mspOrg_id, test);
		  userSpogServer.assignMspAccountAdmins(mspOrg_id, organizationID, msp_account_admin_id, token);
		  
		  //login with msp account admin
		  spogServer.userLogin(this.msp_account_admin_email, sharePassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  //create cloud account
		  test.log(LogStatus.INFO, "using csr admin create cloud accounts");
		  String cloud_account_id = spogServer.createCloudAccountWithCheck(spogServer.ReturnRandom("spogqa_account_s3accountkey"), spogServer.ReturnRandom("account_s3account0_secret"), spogServer.ReturnRandom("account_s3account_name"), "aws_s3", organizationID, null, null, test);
		  
		  //update cloud account
		  test.log(LogStatus.INFO, "using csr admin delete cloud accounts");
		  String newCloudAccountName=spogServer.ReturnRandom("new_s3_name");
		  Response response=spogServer.updateCloudAccount(cloud_account_id, "", "", newCloudAccountName, "", "","","", test);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		  //delete org
		  spogServer.setToken(token);
		  spogServer.deleteMSPAccountWithCheck(mspOrg_id, organizationID);
	  }
	  
	  @DataProvider(name = "cloudAccountInfo10")
	  public final Object[][] getCloudAccountInfo10() {
		  return new Object[][] { 
			  {spogServer.ReturnRandom("spogqa0_direct_s3accountkey"),spogServer.ReturnRandom("direct_s3account0_secret"),spogServer.ReturnRandom("direct_s3account0_name"),"aws_s3","direct", null, null},
//			  {spogServer.ReturnRandom("spogqa0_msp_s3accountkey"),spogServer.ReturnRandom("msp_s3account0_secret"),spogServer.ReturnRandom("msp_s3account0_name"),"aws_s3","msp", null, null},
//			  {spogServer.ReturnRandom("spogqa_account_s3accountkey"),spogServer.ReturnRandom("account_s3account0_secret"),spogServer.ReturnRandom("account_s3account_name"),"aws_s3","child", null, null},
//			  {spogServer.ReturnRandom("spogqa0_direct_s3accountkey"),spogServer.ReturnRandom("direct_s3account0_secret"),spogServer.ReturnRandom("direct_s3account0_name"),"cloud_direct","direct", "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8),"SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8)},
//			  {spogServer.ReturnRandom("spogqa0_msp_s3accountkey"),spogServer.ReturnRandom("msp_s3account0_secret"),spogServer.ReturnRandom("msp_s3account0_name"),"cloud_direct","msp", "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8),"SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8)},
		  };
		}
	  @Test(dataProvider = "cloudAccountInfo10")
	  public void mspAccountAdminUpdateCloudAccountFail(String cloudAccountKey, String cloudAccountSecret, String cloudAccountName, String cloudAccountType, 
			  String orgType, String orderID, String fulfillmentID) {
		  System.out.println("mspAccountAdminUpdateCloudAccountFail");
		  test = rep.startTest("mspAccountAdminUpdateCloudAccountFail");
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
		  //update cloud account
		  test.log(LogStatus.INFO, "using csr admin delete cloud accounts");
		  String newCloudAccountName = spogServer.ReturnRandom("new_name");
		  Response response =spogServer.updateCloudAccount(cloud_account_id, "", "", newCloudAccountName, "", "", "", "", test);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS);
		  spogServer.checkErrorCode(response, "");
		  
		  //delete org
		  spogServer.setToken(token);
		  spogServer.deleteMSPAccountWithCheck(mspOrg_id, organizationID);
	  }
	  
	  @DataProvider(name = "cloudAccountInfo")
	  public final Object[][] getCloudAccountInfo() {
		  return new Object[][] { 
			  //{spogServer.ReturnRandom("spogqa_csr_directaccountkey"),spogServer.ReturnRandom("csr_directaccount_secret"),spogServer.ReturnRandom("csr_directaccount_name"),"cloud_direct",csrOrg_id}, 
			  //{spogServer.ReturnRandom("spogqa_csr_s3accountkey"),spogServer.ReturnRandom("csr_s3account_secret"),spogServer.ReturnRandom("csr_s3account_name"),"aws_s3",csrOrg_id,null, null},
			  {spogServer.ReturnRandom("spogqa_direct_directaccountkey"),spogServer.ReturnRandom("direct_directaccount_secret"),spogServer.ReturnRandom("direct_directaccount_name"),"cloud_direct","direct","SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8),"SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8)}, 
			  {spogServer.ReturnRandom("spogqa_direct_s3accountkey"),spogServer.ReturnRandom("direct_s3account_secret"),spogServer.ReturnRandom("direct_s3account_name"),"aws_s3", "direct", null, null},
			  {spogServer.ReturnRandom("spogqa_msp_directaccountkey"),spogServer.ReturnRandom("msp_directaccount_secret"),spogServer.ReturnRandom("msp_directaccount_name"),"cloud_direct", "msp", "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8), "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8)}, 
			  {spogServer.ReturnRandom("spogqa_msp_s3accountkey"),spogServer.ReturnRandom("msp_s3account_secret"),spogServer.ReturnRandom("msp_s3account_name"),"aws_s3", "msp", null, null},
			  //{spogServer.ReturnRandom("spogqa_account_directaccountkey"),spogServer.ReturnRandom("account_directaccount_secret"),spogServer.ReturnRandom("account_directaccount_name"),"cloud_direct",account_id}, 
			  {spogServer.ReturnRandom("spogqa_account_s3accountkey"),spogServer.ReturnRandom("account_s3account_secret"),spogServer.ReturnRandom("account_s3account_name"),"aws_s3", "child", null, null},
	          };
		}
	  
	  @Test(dataProvider = "cloudAccountInfo")
	  //csr user can update any cloud account
	  public void csrUserUpdateCloudAccount(String cloudAccountKey, String cloudAccountSecret, String cloudAccountName, String cloudAccountType, String orgType,
			  String orderID, String fulfillmentID){	 
		  System.out.println("csrUserUpdateCloudAccount");
		  test = rep.startTest("csrUserUpdateCloudAccount");
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
		  
		  //update cloud account
		  test.log(LogStatus.INFO, "using csr admin update cloud accounts");
		  String newName = cloudAccountName+"_new";
		  Response response = spogServer.updateCloudAccount(cloud_account_id, "", "", newName, "", "", test);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  response.then().body("data.cloud_account_name", equalTo(newName));
		  
		  //delete account
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
			  {spogServer.ReturnRandom("spogqa_direct_directaccountkey"),spogServer.ReturnRandom("direct_directaccount_secret"),spogServer.ReturnRandom("direct_directaccount_name"),"cloud_direct","direct","SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8), "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8)}, 
			  {spogServer.ReturnRandom("spogqa_direct_s3accountkey"),spogServer.ReturnRandom("direct_s3account_secret"),spogServer.ReturnRandom("direct_s3account_name"),"aws_s3",  "direct",null, null}
			  };
		}
	  
	  @Test(dataProvider = "cloudAccountInfo1")
	
	  public void directUserUpdateCloudAccountSuccess(String cloudAccountKey, String cloudAccountSecret, String cloudAccountName, String cloudAccountType, 
			  String orgType, String orderID, String fulfillmentID){	 
		  System.out.println("directUserUpdateCloudAccountSuccess");
		  test = rep.startTest("directUserUpdateCloudAccountSuccess");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login with csr user
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  //create direct org
		  String email = spogServer.ReturnRandom("yuefen_org_email@spogqa.com");
		  String password = spogServer.ReturnRandom("aaQQdsf11");
		  String organizationID = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_orgname_yuefen"),orgType,email,password,spogServer.ReturnRandom("org_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  
		  //create cloud account
		  String cloud_account_id = spogServer.createCloudAccountWithCheck(cloudAccountKey, cloudAccountSecret, cloudAccountName, cloudAccountType, organizationID, orderID, fulfillmentID, test);

		  
		  //login with direct user
		  spogServer.userLogin(email, password, SpogConstants.SUCCESS_LOGIN, test);
		  
		  //update cloud account
		  test.log(LogStatus.INFO, "using direct admin update cloud accounts");
		  String newName = cloudAccountName+"_new";
		  Response response = spogServer.updateCloudAccount(cloud_account_id, "", "", newName, "aws_s3", "", test);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  response.then().body("data.cloud_account_name", equalTo(newName));
		  
		  //delete account
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
			  //{spogServer.ReturnRandom("spogqa_csr_s3accountkey"),spogServer.ReturnRandom("csr_s3account_secret"),spogServer.ReturnRandom("csr_s3account_name"),"aws_s3", csrOrg_id, null, null},
			  {spogServer.ReturnRandom("spogqa_direct2_directaccountkey"),spogServer.ReturnRandom("direct2_directaccount_secret"),spogServer.ReturnRandom("direct2_directaccount_name"),"cloud_direct",  "direct","SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8), "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8)},
			  {spogServer.ReturnRandom("spogqa_direct2_s3accountkey"),spogServer.ReturnRandom("direct2_s3account_secret"),spogServer.ReturnRandom("direct2_s3account_name"),"aws_s3","direct", null, null},
			  {spogServer.ReturnRandom("spogqa_msp_directaccountkey"),spogServer.ReturnRandom("msp_directaccount_secret"),spogServer.ReturnRandom("msp_directaccount_name"),"cloud_direct", "msp", "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8), "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8)}, 
			  {spogServer.ReturnRandom("spogqa_msp_s3accountkey"),spogServer.ReturnRandom("msp_s3account_secret"),spogServer.ReturnRandom("msp_s3account_name"),"aws_s3",  "msp", null, null},
			  //{spogServer.ReturnRandom("spogqa_account_directaccountkey"),spogServer.ReturnRandom("account_directaccount_secret"),spogServer.ReturnRandom("account_directaccount_name"),"cloud_direct",account_id}, 
			  {spogServer.ReturnRandom("spogqa_account_s3accountkey"),spogServer.ReturnRandom("account_s3account_secret"),spogServer.ReturnRandom("account_s3account_name"),"aws_s3",  "child", null, null}
	          };
		}
	  
	  @Test(dataProvider = "cloudAccountInfo2")
	 
	  public void directUserUpdateCloudAccountFail(String cloudAccountKey, String cloudAccountSecret, String cloudAccountName, String cloudAccountType, 
			  String orgType, String orderID, String fulfillmentID){	 
		  System.out.println("directUserUpdateCloudAccountFail");
		  test = rep.startTest("directUserUpdateCloudAccountFail");
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
		  System.out.println("organizationID:"+organizationID);
		  String cloud_account_id = spogServer.createCloudAccountWithCheck(cloudAccountKey, cloudAccountSecret, cloudAccountName, cloudAccountType, organizationID, orderID, fulfillmentID, test);
		  
		  //login with direct user
		  spogServer.userLogin(direct_email, sharePassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  //update cloud account
		  test.log(LogStatus.INFO, "using direct admin update cloud accounts");
		  String newName = cloudAccountName+"_new";
		  Response response = spogServer.updateCloudAccount(cloud_account_id, "", "", newName, "", "", test);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
		  spogServer.checkErrorCode(response, "00B00003");
		  
		  //login csr user
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  //delete account
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
			  {spogServer.ReturnRandom("spogqa_msp_directaccountkey"),spogServer.ReturnRandom("msp_directaccount_secret"),spogServer.ReturnRandom("msp_directaccount_name"),"cloud_direct", "msp", "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8), "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8)}, 
			  {spogServer.ReturnRandom("spogqa_msp_s3accountkey"),spogServer.ReturnRandom("msp_s3account_secret"),spogServer.ReturnRandom("msp_s3account_name"),"aws_s3", "msp", null, null},
			  //{spogServer.ReturnRandom("spogqa_account_directaccountkey"),spogServer.ReturnRandom("account_directaccount_secret"),spogServer.ReturnRandom("account_directaccount_name"),"cloud_direct",account_id}, 
			  {spogServer.ReturnRandom("spogqa_account_s3accountkey"),spogServer.ReturnRandom("account_s3account_secret"),spogServer.ReturnRandom("account_s3account_name"),"aws_s3", "child", null, null}
			  };
		}
	  @Test(dataProvider = "cloudAccountInfo3")

	  public void mspUserUpdateCloudAccountSuccess(String cloudAccountKey, String cloudAccountSecret, String cloudAccountName, String cloudAccountType, 
			  String orgType, String orderID, String fulfillmentID){	 
		  System.out.println("mspUserUpdateCloudAccountSuccess");
		  test = rep.startTest("mspUserUpdateCloudAccountSuccess");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login with csr user
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  //create org
		  String organizationID = null;
		  String email = spogServer.ReturnRandom("yuefen_org_email@spogqa.com");
		  String password = spogServer.ReturnRandom("aaQQdsf11");
		  if (orgType == "msp") {
			  organizationID = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_orgname_yuefen"),orgType, email, password,spogServer.ReturnRandom("org_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
			  
		  }else {
			  organizationID = spogServer.createAccountWithCheck(mspOrg_id,spogServer.ReturnRandom("spogqa_accoun1_msp1_yuefen_group"), mspOrg_id, test);
		  }
		  
		  //create cloud account
		  test.log(LogStatus.INFO, "using csr admin create cloud accounts");
		  String cloud_account_id = spogServer.createCloudAccountWithCheck(cloudAccountKey, cloudAccountSecret, cloudAccountName, cloudAccountType, organizationID, orderID, fulfillmentID, test);
		  
		  //login with msp user, update and delete
		  String newName = cloudAccountName+"_new";
		  
		  if (orgType == "msp") {
			  spogServer.userLogin(email, password, SpogConstants.SUCCESS_LOGIN, test);
			  
			  Response response = spogServer.updateCloudAccount(cloud_account_id, "", "", newName, "", "", test);
			  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
			  response.then().body("data.cloud_account_name", equalTo(newName));
			  
			  spogServer.deleteCloudAccountWithExpectedStatusCode(cloud_account_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
			  
			  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
			  spogServer.DeleteOrganizationWithCheck(organizationID, test);  
			  
		  }else {
			  spogServer.userLogin(msp_email, sharePassword, SpogConstants.SUCCESS_LOGIN, test);
			  
			  Response response = spogServer.updateCloudAccount(cloud_account_id, "", "", newName, "", "", test);
			  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
			  response.then().body("data.cloud_account_name", equalTo(newName));
			  
			  spogServer.deleteCloudAccountWithExpectedStatusCode(cloud_account_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
			  
			  spogServer.deleteMSPAccountWithCheck(mspOrg_id, organizationID);
		  }
	  }
	  
	  @DataProvider(name = "cloudAccountInfo4")
	  public final Object[][] getCloudAccountInfo4() {
		  return new Object[][] { 
			  //{spogServer.ReturnRandom("spogqa_csr_directaccountkey"),spogServer.ReturnRandom("csr_directaccount_secret"),spogServer.ReturnRandom("csr_directaccount_name"),"cloud_direct",csrOrg_id},
			  //{spogServer.ReturnRandom("spogqa_csr_s3accountkey"),spogServer.ReturnRandom("csr_s3account_secret"),spogServer.ReturnRandom("csr_s3account_name"),"aws_s3", null, null, csrOrg_id},
			  {spogServer.ReturnRandom("spogqa_direct2_directaccountkey"),spogServer.ReturnRandom("direct2_directaccount_secret"),spogServer.ReturnRandom("direct2_directaccount_name"),"cloud_direct","direct","SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8), "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8)},
			  {spogServer.ReturnRandom("spogqa_direct2_s3accountkey"),spogServer.ReturnRandom("direct2_s3account_secret"),spogServer.ReturnRandom("direct2_s3account_name"),"aws_s3","direct", null, null},
			  {spogServer.ReturnRandom("spogqa_msp2_directaccountkey"),spogServer.ReturnRandom("msp2_directaccount_secret"),spogServer.ReturnRandom("msp2_directaccount_name"),"cloud_direct","msp", "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8), "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8)}, 
			  {spogServer.ReturnRandom("spogqa_msp2_s3accountkey"),spogServer.ReturnRandom("msp2_s3account_secret"),spogServer.ReturnRandom("msp2_s3account_name"),"aws_s3",  "msp", null, null},
			  //{spogServer.ReturnRandom("spogqa_account2_directaccountkey"),spogServer.ReturnRandom("account2_directaccount_secret"),spogServer.ReturnRandom("account2_directaccount_name"),"cloud_direct",account2_id}, 
			  {spogServer.ReturnRandom("spogqa_account2_s3accountkey"),spogServer.ReturnRandom("account2_s3account_secret"),spogServer.ReturnRandom("account2_s3account_name"),"aws_s3","child", null, null},
	          };
		}
	  
	  @Test(dataProvider = "cloudAccountInfo4")
	  public void mspUserUpdateCloudAccountFail(String cloudAccountKey, String cloudAccountSecret, String cloudAccountName, String cloudAccountType, 
			  String orgType, String orderID, String fulfillmentID){	 
		  System.out.println("mspUserUpdateCloudAccountFail");
		  test = rep.startTest("mspUserUpdateCloudAccountFail");
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
		  
		  //login with msp user
		  spogServer.userLogin(msp_email, sharePassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  //update cloud account
		  test.log(LogStatus.INFO, "using msp admin update cloud accounts");
		  String newName = cloudAccountName+"_new";
		  Response response = spogServer.updateCloudAccount(cloud_account_id, "", "", newName, "", "", test);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
		  spogServer.checkErrorCode(response, "00B00003");
		  
		  //login with csr user
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  //delete account
		  spogServer.deleteCloudAccountWithExpectedStatusCode(cloud_account_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  
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
			  {spogServer.ReturnRandom("spogqa_account_s3accountkey"),spogServer.ReturnRandom("account_s3account_secret"),spogServer.ReturnRandom("account_s3account_name"),"aws_s3", null, null}
			  };
		}
	  
	  @Test(dataProvider = "cloudAccountInfo5")

	  public void accountUserUpdateCloudAccountSuccess(String cloudAccountKey, String cloudAccountSecret, String cloudAccountName, String cloudAccountType, 
			   String orderID, String fulfillmentID){	 
		  System.out.println("accountUserUpdateCloudAccountSuccess");
		  test = rep.startTest("accountUserUpdateCloudAccountSuccess");
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
		  
		  //login with account user
		  spogServer.userLogin(email, sharePassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  //update cloud account
		  test.log(LogStatus.INFO, "using account direct admin update cloud accounts");
		  String newName = cloudAccountName+"_new";
		  Response response = spogServer.updateCloudAccount(cloud_account_id, "", "", newName, "", "", test);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  response.then().body("data.cloud_account_name", equalTo(newName));
		  
		  //delete account
		  spogServer.deleteCloudAccountWithExpectedStatusCode(cloud_account_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  
		  //delete org
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  spogServer.deleteMSPAccountWithCheck(mspOrg2_id, organizationID);
	  }
	  
	  
	  @DataProvider(name = "cloudAccountInfo6")
	  public final Object[][] getCloudAccountInfo6() {
		  return new Object[][] { 
			  //{spogServer.ReturnRandom("spogqa_csr_directaccountkey"),spogServer.ReturnRandom("csr_directaccount_secret"),spogServer.ReturnRandom("csr_directaccount_name"),"cloud_direct",csrOrg_id},
			  //{spogServer.ReturnRandom("spogqa_csr_s3accountkey"),spogServer.ReturnRandom("csr_s3account_secret"),spogServer.ReturnRandom("csr_s3account_name"),"aws_s3",null, null, csrOrg_id},
			  {spogServer.ReturnRandom("spogqa_direct2_directaccountkey"),spogServer.ReturnRandom("direct2_directaccount_secret"),spogServer.ReturnRandom("direct2_directaccount_name"),"cloud_direct","direct", "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8),"SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8)},
			  {spogServer.ReturnRandom("spogqa_direct2_s3accountkey"),spogServer.ReturnRandom("direct2_s3account_secret"),spogServer.ReturnRandom("direct2_s3account_name"),"aws_s3","direct", null, null},
			  {spogServer.ReturnRandom("spogqa_msp_directaccountkey"),spogServer.ReturnRandom("msp_directaccount_secret"),spogServer.ReturnRandom("msp_directaccount_name"),"cloud_direct","msp","SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8),"SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8)}, 
			  {spogServer.ReturnRandom("spogqa_msp_s3accountkey"),spogServer.ReturnRandom("msp_s3account_secret"),spogServer.ReturnRandom("msp_s3account_name"),"aws_s3","msp", null, null},
			  //{spogServer.ReturnRandom("spogqa_msp2_directaccountkey"),spogServer.ReturnRandom("msp2_directaccount_secret"),spogServer.ReturnRandom("msp2_directaccount_name"),"cloud_direct",mspOrg2_id, "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8),"SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8)}, 
			  //{spogServer.ReturnRandom("spogqa_msp2_s3accountkey"),spogServer.ReturnRandom("msp2_s3account_secret"),spogServer.ReturnRandom("msp2_s3account_name"),"aws_s3", mspOrg2_id, null, null},
			  //{spogServer.ReturnRandom("spogqa_account2_directaccountkey"),spogServer.ReturnRandom("account2_directaccount_secret"),spogServer.ReturnRandom("account2_directaccount_name"),"cloud_direct",account2_id}, 
			  {spogServer.ReturnRandom("spogqa_account2_s3accountkey"),spogServer.ReturnRandom("account2_s3account_secret"),spogServer.ReturnRandom("account2_s3account_name"),"aws_s3","child", null, null},
	          };
		}
	  
	  @Test(dataProvider = "cloudAccountInfo6")
	  public void accountUserUpdateCloudAccountFail(String cloudAccountKey, String cloudAccountSecret, String cloudAccountName, String cloudAccountType, 
			  String orgType, String orderID, String fulfillmentID){	 
		  System.out.println("accountUserUpdateCloudAccountFail");
		  test = rep.startTest("accountUserUpdateCloudAccountFail");
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
		  
		  //login with account user
		  spogServer.userLogin(accountDirect_email, sharePassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  //update cloud account
		  test.log(LogStatus.INFO, "using account direct admin update cloud accounts");
		  String newName = cloudAccountName+"_new";
		  Response response = spogServer.updateCloudAccount(cloud_account_id, "", "", newName, "", "", test);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
		  spogServer.checkErrorCode(response, "00B00003");
		  
		  //login with csr user
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  //delete account
		  spogServer.deleteCloudAccountWithExpectedStatusCode(cloud_account_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  
		  //delete org
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
			  {spogServer.ReturnRandom("spogqa_direct7_s3accountkey"),spogServer.ReturnRandom("direct7_s3account_secret"),spogServer.ReturnRandom("direct7_s3account_name"),"aws_s3",  null, null}
	          };
		}
		
	  @Test(dataProvider = "cloudAccountInfo7")
	  public void UpdateS3CloudAccountNormalCheck(String cloudAccountKey, String cloudAccountSecret, String cloudAccountName, String cloudAccountType, 
			   String orderID, String fulfillmentID){	 
		  System.out.println("UpdateS3CloudAccountNormalCheck");
		  test = rep.startTest("UpdateS3CloudAccountNormalCheck");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login with csr user
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  //create org
		  String organizationID = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_orgname_yuefen"), "direct",spogServer.ReturnRandom("yuefen_org_email@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("org_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  
		  //create cloud account
		  String cloudAccountId = spogServer.createCloudAccountWithCheck(cloudAccountKey, cloudAccountSecret, cloudAccountName, cloudAccountType, organizationID, orderID, fulfillmentID, test);
		  
		  //update cloud account key
		  System.out.println("update key");
		  String newKey = spogServer.ReturnRandom("new_accountkey_yuefen_spogqa");
		  response = spogServer.updateCloudAccount(cloudAccountId, newKey, "", "", "", "", test);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  response.then().body("data.cloud_account_key", equalTo(newKey));
		  
		  //update cloud account secret
		  System.out.println("update secret");
		  String newSecret = spogServer.ReturnRandom("new_accountSecret_yuefen_spogqa");
		  response = spogServer.updateCloudAccount(cloudAccountId, "", newSecret, "", "", "", test);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		 
		  //update cloud account name
		  System.out.println("update name");
		  String newName = spogServer.ReturnRandom("new_accountName_yuefen_spogqa");
		  response = spogServer.updateCloudAccount(cloudAccountId, "", "", newName, "", "", test);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  response.then().body("data.cloud_account_name", equalTo(newName));
		 
		  //update cloud account type
		  System.out.println("update type - can't update to cloud direct");
		  response = spogServer.updateCloudAccount(cloudAccountId, "", "", "", "cloud_direct", "", test);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  response.then().body("data.cloud_account_type", equalTo("aws_s3"));
		  
		  //update cloud account type to invalid one
		  System.out.println("update type to invalid one, will not update");
		  String newType = "cloud_new";
		  response = spogServer.updateCloudAccount(cloudAccountId, "", "", "", newType, "", test);
		  spogServer.checkResponseStatus(response,SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  
		  //update cloud account orgid
		  System.out.println("update orgid, will not update");
		  response = spogServer.updateCloudAccount(cloudAccountId, "", "", "", "", account_id, test);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  response.then().body("data.organization_id", equalTo(organizationID));
		  
		  //create another cloud account
		  System.out.println("create the second s3 and update to duplicate name fail");
		  String CloudAccountId2 = spogServer.createCloudAccountWithCheck(spogServer.ReturnRandom("account2_key_yuefen_spogqa"), spogServer.ReturnRandom("account2_secret_yuefen_spogqa"), spogServer.ReturnRandom("account2_name_yuefen_spogqa"), "aws_s3", organizationID, null, null, test);
		  //Try to update account name to duplicate in the same org 
		  response = spogServer.updateCloudAccount(CloudAccountId2, "", "", newName, "", "", test);
		  spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
		  spogServer.checkErrorCode(response, "00B00001");
		  
		  //Try to update account name to duplicate in the same org and with different type
		  System.out.println("create the third account -direct and update to duplicate name fail");
		  String CloudAccountId3 = spogServer.createCloudAccountWithCheck(spogServer.ReturnRandom("account3_key_yuefen_spogqa"), spogServer.ReturnRandom("account3_secret_yuefen_spogqa"), spogServer.ReturnRandom("account3_name_yuefen_spogqa"), "cloud_direct", organizationID,"SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8),"SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8), test);
		  response = spogServer.updateCloudAccount(CloudAccountId3, "", "", newName, "", "", test);
		  spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
		  spogServer.checkErrorCode(response, "00B00001");
		  
		  //There are duplicate accounts in the different org
		  System.out.println("create the fourth s3 in different org and update to duplicate name success");
		  String CloudAccountId4 = spogServer.createCloudAccountWithCheck(spogServer.ReturnRandom("account4_key_yuefen_spogqa"), spogServer.ReturnRandom("account4_secret_yuefen_spogqa"), spogServer.ReturnRandom("account4_name_yuefen_spogqa"), "aws_s3", directOrg2_id, null, null, test);
		  response = spogServer.updateCloudAccount(CloudAccountId4, "", "", newName, "", "", test);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);

		  //update cloud account  = null
		  System.out.println("update account with all parameters null");
		  response = spogServer.updateCloudAccount(CloudAccountId2, null, null, null, null, null, test);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  
		  //update cloud account id = null
		  System.out.println("update account id = null");
		  response = spogServer.updateCloudAccount(null, "", "", "", "", "", test);
		  spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
		  spogServer.checkErrorCode(response, "40000005");
		  
		  //update cloud account id = "00000000-0000-0000-0000-000000000000"
		  System.out.println("update account id = 00000000-0000-0000-0000-000000000000");
		  String account_ID = "00000000-0000-0000-0000-000000000000";
		  response = spogServer.updateCloudAccount(account_ID, "","","","","", test);
		  spogServer.checkResponseStatus(response, SpogConstants.RESOURCE_NOT_EXIST, test);
		  spogServer.checkErrorCode(response, "00B00002");
		  
		  //delete cloud accounts
		  spogServer.deleteCloudAccountWithExpectedStatusCode(cloudAccountId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  spogServer.deleteCloudAccountWithExpectedStatusCode(CloudAccountId2, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  spogServer.deleteCloudAccountWithExpectedStatusCode(CloudAccountId3, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  spogServer.deleteCloudAccountWithExpectedStatusCode(CloudAccountId4, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  
		  //delete org
		  spogServer.DeleteOrganizationWithCheck(organizationID, test);  
		  
	  }
	  
	  @DataProvider(name = "cloudAccountInfo9")
	  public final Object[][] getCloudAccountInfo9() {
		  return new Object[][] { 
			  {spogServer.ReturnRandom("direct9_CD_key"),spogServer.ReturnRandom("direct9_CD_secret"),spogServer.ReturnRandom("direct9_CD_name"),"cloud_direct","SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8), "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8)}
	          };
		}
		
	  @Test(dataProvider = "cloudAccountInfo9")
	  public void UpdateCloudDirectAccountNormalCheck(String cloudAccountKey, String cloudAccountSecret, String cloudAccountName, String cloudAccountType, 
			  String orderID, String fulfillmentID){	 
		  System.out.println("UpdateCloudDirectAccountNormalCheck");
		  test = rep.startTest("UpdateCloudDirectAccountNormalCheck");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login with csr user
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  //create org
		  String organizationID = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_orgname_yuefen"), "direct",spogServer.ReturnRandom("yuefen_org_email@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("org_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  
		  //create cloud account
		  String cloudAccountId = spogServer.createCloudAccountWithCheck(cloudAccountKey, cloudAccountSecret, cloudAccountName, cloudAccountType, organizationID, orderID, fulfillmentID, test);
		  
		  //update order ID
		  System.out.println("update orderID");
		  String newOrderID = "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8);
		  System.out.println("newOrderID:"+newOrderID);
		  Response response = spogServer.updateCloudAccount(cloudAccountId, "", "", "", "", "", newOrderID, "", test);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  //get order to check order id was not updated
		  response = spogServer.getCloudDirectOrder(cloudAccountId, test);
		  List<String> orderIDs = response.body().jsonPath().getList("data.orders.order_summary.order_id");
		  for (int i = 0; i < orderIDs.size(); i++) {
			  if (orderIDs.get(i).contains(newOrderID)) {
				  System.out.println("order id was updated, not expected!");
			  }else {
				  System.out.println("order id was not updated, expected!");
			  }
			  
		  }
		  
		  //update fulfillmentID
		  System.out.println("update fulfillmentID");
		  String newFulfillmentID = "SKUTESTDATA_1_0_0_0_"+RandomStringUtils.randomNumeric(8);
		  System.out.println("newOrderID:"+newFulfillmentID);
		  response = spogServer.updateCloudAccount(cloudAccountId, "", "", "", "", "", "", newFulfillmentID, test);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  //get order to check order id was not updated
		  response = spogServer.getCloudDirectOrder(cloudAccountId, test);
		  List<String> fulfillmentIDs = response.body().jsonPath().getList("data.orders.order_summary.fulfillment_id");
		  for (int i = 0; i < fulfillmentIDs.size(); i++) {
			  if (fulfillmentIDs.get(i).contains(newFulfillmentID)) {
				  System.out.println("fulfillment id was updated, not expected!");
			  }else {
				  System.out.println("fulfillment id was not updated, expected!");
			  }
			  
		  }
		  //update cloud account key
		  System.out.println("update key");
		  String newKey = spogServer.ReturnRandom("new_accountkey_yuefen_spogqa");
		  response = spogServer.updateCloudAccount(cloudAccountId, newKey, "", "", "", "", test);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  response.then().body("data.cloud_account_key", equalTo(cloudAccountKey));
		  
		  //update cloud account secret
		  System.out.println("update secret");
		  String newSecret = spogServer.ReturnRandom("new_accountSecret_yuefen_spogqa");
		  response = spogServer.updateCloudAccount(cloudAccountId, "", newSecret, "", "", "", test);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		 
		  //update cloud account name
		  System.out.println("update name");
		  String newName = spogServer.ReturnRandom("new_accountName_yuefen_spogqa");
		  response = spogServer.updateCloudAccount(cloudAccountId, "", "", newName, "", "", test);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  response.then().body("data.cloud_account_name", equalTo(newName));
		 
		  //update cloud account type
		  System.out.println("update type to s3");
		  response = spogServer.updateCloudAccount(cloudAccountId, "", "", "", "aws_s3", "", test);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  response.then().body("data.cloud_account_type", equalTo("cloud_direct"));
		  
		  //update cloud account type to invalid one
		  System.out.println("update type to invalid");
		  String newType = "cloud_new";
		  response = spogServer.updateCloudAccount(cloudAccountId, "", "", "", newType, "", test);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  
		  //update cloud account orgid
		  System.out.println("update orgid");
		  response = spogServer.updateCloudAccount(cloudAccountId, "", "", "", "", account_id, test);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  response.then().body("data.organization_id", equalTo(organizationID));

		  //update cloud account  = null
		  response = spogServer.updateCloudAccount(cloudAccountId, null, null, null, null, null, test);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  
		  //update cloud account id = null
		  response = spogServer.updateCloudAccount(null, "", "", "", "", "", test);
		  spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
		  spogServer.checkErrorCode(response, "40000005");
		  
		  //update cloud account id = "00000000-0000-0000-0000-000000000000"
		  String account_ID = "00000000-0000-0000-0000-000000000000";
		  response = spogServer.updateCloudAccount(account_ID, "","","","","", test);
		  spogServer.checkResponseStatus(response, SpogConstants.RESOURCE_NOT_EXIST, test);
		  spogServer.checkErrorCode(response, "00B00002");
		  
		  //delete cloud accounts
		  spogServer.deleteCloudAccountWithExpectedStatusCode(cloudAccountId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  
	  }
	  
	  @DataProvider(name = "cloudAccountInfo8")
	  public final Object[][] getCloudAccountInfo8() {
		  return new Object[][] { 
			  {spogServer.ReturnRandom("spogqa_direct8_s3accountkey"),spogServer.ReturnRandom("direct8_s3account_secret"),spogServer.ReturnRandom("direct8_s3account_name"),"aws_s3",directOrg_id, null, null}
	          };
		}
	  
	  @Test(dataProvider = "cloudAccountInfo8")
	  public void UpdateCloudAccountFail401(String cloudAccountKey, String cloudAccountSecret, String cloudAccountName, String cloudAccountType, 
	  String organizationID, String orderID, String fulfillmentID){	 
		  System.out.println("UpdateCloudAccountFail401");
		  test = rep.startTest("UpdateCloudAccountFail401");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login with csr user
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  //create cloud account
		  String cloudAccountId = spogServer.createCloudAccountWithCheck(cloudAccountKey, cloudAccountSecret, cloudAccountName, cloudAccountType, organizationID, orderID, fulfillmentID, test);
		  
		  //set token = ""
		  spogServer.setToken("");
		  String newName = cloudAccountName + "_new";
		  spogServer.updateCloudAccountWithCheck(cloudAccountId, "", "", newName, "", "", SpogConstants.NOT_LOGGED_IN, "00900006", test);
		  
		  //delete cloud account
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  spogServer.deleteCloudAccountWithExpectedStatusCode(cloudAccountId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
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
