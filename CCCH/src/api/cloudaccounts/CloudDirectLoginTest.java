package api.cloudaccounts;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import InvokerServer.SPOGServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;


import Constants.SpogConstants;

import org.testng.annotations.Test;

public class CloudDirectLoginTest {
	  private SPOGServer spogServer;
	  private String csrOrg_id;
	  private String directOrg_id;
	  private String directOrg2_id;
	  private String mspOrg_id;
	  private String mspOrg2_id;
	  private String account_id;
	  private String account2_id;
	  private String msp_email;
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
			System.out.println("CloudDirectLoginTest");
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
		  rep = ExtentManager.getInstance("CloudDirectLoginTest",logFolder);
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
		  
		  this.accountDirect_email= spogServer.ReturnRandom("msp_admin_yuefen@spog.com");
		  spogServer.createUserAndCheck(accountDirect_email, sharePassword, "yuefen", "liu", "direct_admin", account_id, test);
	  }
	  
	  @DataProvider(name = "cloudAccountInfo")
	  public final Object[][] getCloudAccountInfo() {
		  return new Object[][] { 
			  {spogServer.ReturnRandom("spogqa_csr_directaccountkey"),spogServer.ReturnRandom("csr_directaccount_secret"),spogServer.ReturnRandom("csr_directaccount_name"),"cloud_direct",csrOrg_id}, 
			  {spogServer.ReturnRandom("spogqa_direct_directaccountkey"),spogServer.ReturnRandom("direct_directaccount_secret"),spogServer.ReturnRandom("direct_directaccount_name"),"cloud_direct",directOrg_id}, 
			  {spogServer.ReturnRandom("spogqa_msp_directaccountkey"),spogServer.ReturnRandom("msp_directaccount_secret"),spogServer.ReturnRandom("msp_directaccount_name"),"cloud_direct",mspOrg_id}, 
			  {spogServer.ReturnRandom("spogqa_account_directaccountkey"),spogServer.ReturnRandom("account_directaccount_secret"),spogServer.ReturnRandom("account_directaccount_name"),"cloud_direct",account_id}
	          };
		}
	  @DataProvider(name = "cloudAccountInfo2")
	  public final Object[][] getCloudAccountInfo2() {
		  return new Object[][] { 
			  {spogServer.ReturnRandom("spogqa_csr_s3accountkey"),spogServer.ReturnRandom("csr_s3account_secret"),spogServer.ReturnRandom("csr_s3account_name"),"aws_s3",csrOrg_id},
			  {spogServer.ReturnRandom("spogqa_direct_s3accountkey"),spogServer.ReturnRandom("direct_s3account_secret"),spogServer.ReturnRandom("direct_s3account_name"),"aws_s3",directOrg_id},
			  {spogServer.ReturnRandom("spogqa_msp_s3accountkey"),spogServer.ReturnRandom("msp_s3account_secret"),spogServer.ReturnRandom("msp_s3account_name"),"aws_s3",mspOrg_id},
			  {spogServer.ReturnRandom("spogqa_account_s3accountkey"),spogServer.ReturnRandom("account_s3account_secret"),spogServer.ReturnRandom("account_s3account_name"),"aws_s3",account_id}
	          };
		}
	  @DataProvider(name = "cloudAccountInfo3")
	  public final Object[][] getCloudAccountInfo3() {
		  return new Object[][] { 
			  {spogServer.ReturnRandom("spogqa_directorg_directaccountkey"),spogServer.ReturnRandom("directorg_directaccount_secret"),spogServer.ReturnRandom("directorg_directaccount_name"),"cloud_direct",directOrg_id}
	          };
		}
	  
	  @Test(dataProvider = "cloudAccountInfo")
	  public void LoginCloudDirectSuccess(String cloudAccountKey, String cloudAccountSecret, String cloudAccountName, String cloudAccountType, String organizationID){	 
		  System.out.println("loginCloudDirectSuccess");
		  test = rep.startTest("loginCloudDirectSuccess");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login with csr user
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  String Secret = cloudAccountSecret;
		  //create cloud account
		 spogServer.createCloudAccountWithCheck(cloudAccountKey, cloudAccountSecret, cloudAccountName, cloudAccountType, organizationID, test);
		  
		 System.out.println("cloudAccountKey:"+cloudAccountKey);
		 System.out.println("Secret:"+Secret);
		 //login account
		 Response response = spogServer.cloudDirectAccountLogin(cloudAccountKey, Secret, test);
		 spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_LOGIN);                                       
	  }
	  
	  
	  @Test(dataProvider = "cloudAccountInfo2")
	  public void loginCloudDirectFail(String cloudAccountKey, String cloudAccountSecret, String cloudAccountName, String cloudAccountType, String organizationID){	 
		  System.out.println("loginCloudDirectFail");
		  test = rep.startTest("loginCloudDirectFail");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login with csr user
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  String Secret = cloudAccountSecret;
		  //create cloud account
		 spogServer.createCloudAccountWithCheck(cloudAccountKey, cloudAccountSecret, cloudAccountName, cloudAccountType, organizationID, test);
		  
		 System.out.println("cloudAccountKey:"+cloudAccountKey);
		 System.out.println("Secret:"+Secret);
		  //login account
		 Response response = spogServer.cloudDirectAccountLogin(cloudAccountKey, Secret, test);
		 spogServer.checkResponseStatus(response, SpogConstants.NOT_LOGGED_IN);
		 spogServer.checkErrorCode(response, "00B00006");
	                                       
	  }
	  
	  @Test(dataProvider = "cloudAccountInfo3")
	  public void loginCloudDirectNormalCheck(String cloudAccountKey, String cloudAccountSecret, String cloudAccountName, String cloudAccountType, String organizationID){	 
		  System.out.println("loginCloudDirectNormalCheck");
		  test = rep.startTest("loginCloudDirectNormalCheck");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login with csr user
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  //create cloud account
		  String Secret = cloudAccountSecret;
		  String cloudAccount_id = spogServer.createCloudAccountWithCheck(cloudAccountKey, cloudAccountSecret, cloudAccountName, cloudAccountType, organizationID, test);
			 
		  //login direct account key = invalid one;
		  spogServer.cloudDirectAccountLoginFail("abc", Secret, SpogConstants.NOT_LOGGED_IN, "00B00006", test);
		  
		  //login direct account secret = invalid one;
		  spogServer.cloudDirectAccountLoginFail(cloudAccountKey, "abc", SpogConstants.NOT_LOGGED_IN, "00B00007", test);
		  
		  
		  //delete the account
		  spogServer.deleteCloudAccountWithExpectedStatusCode(cloudAccount_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);   
		  //login with deleted account's info
		  spogServer.cloudDirectAccountLoginFail(cloudAccountKey, Secret, SpogConstants.NOT_LOGGED_IN, "00B00006", test);
		
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
