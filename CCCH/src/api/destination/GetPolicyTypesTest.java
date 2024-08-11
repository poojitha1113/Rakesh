package api.destination;

import invoker.SiteTestHelper;
import io.restassured.response.Response;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.testng.ITestResult;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.AfterSuite;

import Constants.SpogConstants;
import InvokerServer.GatewayServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import InvokerServer.GatewayServer.siteType;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class GetPolicyTypesTest {
	  private SPOGServer spogServer;	
	  private SPOGDestinationServer spogDestinationServer;
	  private String csrAdminUserName;
	  private String csrAdminPassword;
	  private ExtentReports rep;
	  private ExtentTest test;
	  
	  private String postfix_email = "@arcserve.com";
	  private String common_password = "Welcome*02";
	  
	  private String prefix_direct = "spogqa_leiyu_direct";
	  private String direct_org_name = prefix_direct + "_org";
	  private String direct_org_id=null;
	  private String direct_user_name = prefix_direct + "_admin";
	  private String direct_user_name_email = direct_user_name + postfix_email;
	  private String direct_user_first_name = direct_user_name + "_first_name";
	  private String direct_user_last_name = direct_user_name + "_last_name";
	  private String direct_user_id=null;
	  private String final_direct_user_name_email = null;

	  
	  
	  private String prefix_msp = "spogqa_leiyu_msp";
	  private String msp_org_name = prefix_msp + "_org";
	  private String msp_org_email = msp_org_name + postfix_email;
	  private String msp_org_first_name = msp_org_name + "_first_name";
	  private String msp_org_last_name = msp_org_name + "_last_name";
	  private String msp_user_name = prefix_msp + "_admin";
	  private String msp_user_name_email = prefix_msp + "_admin" + postfix_email;
	  private String msp_user_first_name = msp_user_name + "_first_name";
	  private String msp_user_last_name = msp_user_name + "_last_name";
	  private String msp_user_id =null;
	  private String msp_org_id=null;
	  private String final_msp_user_name_email=null;	  
	  private String msp_site_id;
	  
	  
	  private String account_id;
	  private String account_user_email;
	  private String account_user_id;

		
	  
	  //this is for update portal, each testng class is taken as BQ set
	  private SQLServerDb bqdb1;
	  public int Nooftest;
	  private long creationTime;
	  private String BQName=null;
	  private String runningMachine;
	  private testcasescount count1;
	  private String buildVersion;
	
  @DataProvider(name="accountInfo")
  public Object[][] userAccount(){
		  return new Object[][]{	
				  {csrAdminUserName,csrAdminPassword},
				  {final_direct_user_name_email,common_password},
				  {final_msp_user_name_email,common_password},
				  {account_user_email,common_password}
		  };
  }

  /**
   * get /policies/backuptypes test
   * @testcase
   * 0 csr admin could get /policies/types
   * 1 direct admin could get /policies/types
	 2 MSP admin could get /policies/types
	 3 account admin could get /policies/types
   * @author leiyu.wang
   * @param userName
   * @param password
 * @throws JSONException 
   */
  @Test(dataProvider="accountInfo",priority=1)
  public void getBackuptypes(String userName,String password) throws JSONException {
	  	test = rep.startTest("get policies/backuptypes");
	  	test.assignAuthor("leiyu.wang");
	  	spogServer.errorHandle.printInfoMessageInDebugFile("/****************getBackuptypes**************/");
	  	spogServer.userLogin(userName, password);
	  	spogDestinationServer.setToken(spogServer.getJWTToken());
	  	
		test.log(LogStatus.INFO,"get Backuptypes and check");
		spogServer.errorHandle.printInfoMessageInDebugFile("get Backuptypes and check");	
		spogDestinationServer.getPolicyTypes(SpogConstants.SUCCESS_GET_PUT_DELETE, test);	 	 
  }
  

  @BeforeClass
  @Parameters({ "baseURI", "port", "logFolder", "csrAdminUserName", "csrAdminPassword", "buildVersion"})
  public void beforeClass(String baseURI, String port, String logFolder, String adminUserName, String adminPassword ,  String buildVersion) throws UnknownHostException {
	
	  	spogServer = new SPOGServer(baseURI, port);
	  	spogDestinationServer=new SPOGDestinationServer(baseURI, port);
	  	rep = ExtentManager.getInstance("GetPoliciesBackuptypes",logFolder);
	  	this.csrAdminUserName = adminUserName;
	  	this.csrAdminPassword = adminPassword;

	    test = rep.startTest("beforeClass");
		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
		//*******************create direct org,user**********************/
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		test.log(LogStatus.INFO,"create a direct org");
		direct_org_id = spogServer.CreateOrganizationWithCheck(prefix+direct_org_name , SpogConstants.DIRECT_ORG, null, common_password, null, null, test);
		final_direct_user_name_email = prefix + direct_user_name_email;
		
		test.log(LogStatus.INFO,"create a admin under direct org");
		direct_user_id = spogServer.createUserAndCheck(final_direct_user_name_email, common_password, prefix + direct_user_first_name, prefix + direct_user_last_name, SpogConstants.DIRECT_ADMIN, direct_org_id, test);
		spogServer.userLogin(final_direct_user_name_email, common_password);
		

		//************************create msp org,user*************************************
		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
		prefix = RandomStringUtils.randomAlphanumeric(8);
		test.log(LogStatus.INFO,"create a msp org");
		msp_org_id = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name , SpogConstants.MSP_ORG, null, common_password, null, null, test);
		final_msp_user_name_email = prefix + msp_user_name_email;
		
		test.log(LogStatus.INFO,"create a admin under msp org");
		msp_user_id = spogServer.createUserAndCheck(final_msp_user_name_email, common_password, prefix + msp_user_first_name, prefix + msp_user_last_name, SpogConstants.MSP_ADMIN, msp_org_id, test);
		spogServer.userLogin(final_msp_user_name_email, common_password);
	  	
		
		//create account, account user and site
		test.log(LogStatus.INFO,"Creating a account For msp org");
		account_id = spogServer.createAccountWithCheck(msp_org_id, "sub_" + prefix + msp_org_name, "", test);
		prefix = RandomStringUtils.randomAlphanumeric(8);
	
		test.log(LogStatus.INFO,"Creating a account user For account org");
		account_user_email = prefix + msp_user_name_email;
		account_user_id = spogServer.createUserAndCheck(account_user_email, common_password, prefix + msp_user_first_name, prefix + msp_user_last_name, SpogConstants.DIRECT_ADMIN, account_id, test);
		spogServer.userLogin(account_user_email, common_password);
	  	

		
	  	//this is for update portal
	  	this.BQName = this.getClass().getSimpleName();
	    String author = "leiyu.wang";
	    this.runningMachine =  InetAddress.getLocalHost().getHostName();
	    SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
	    java.util.Date date=new java.util.Date();
	    this.buildVersion=buildVersion+"_"+dateFormater.format(date);
	    Nooftest=0;
	    bqdb1 = new SQLServerDb();
	    count1 = new testcasescount();
	    if(count1.isstarttimehit()==0) {      
	            creationTime=System.currentTimeMillis();
	            count1.setcreationtime(creationTime);
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

  @AfterClass
	public void aftertest() {
		
		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
				
		test.log(LogStatus.INFO,"delete direct org");
		spogServer.errorHandle.printInfoMessageInDebugFile("delete direct organization");
		spogServer.DeleteOrganizationWithCheck(direct_org_id, test);
		
		test.log(LogStatus.INFO,"delete msp org");
		spogServer.errorHandle.printInfoMessageInDebugFile("delete msp organization");
		spogServer.DeleteOrganizationWithCheck(msp_org_id, test);
		

		
	    test.log(LogStatus.INFO, "The total test cases passed are "+count1.getpassedcount());
	    test.log(LogStatus.INFO, "the total test cases failed are "+count1.getfailedcount());
	    rep.flush();
	}


  @BeforeTest
  public void beforeTest() {
  }

  @AfterTest
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

  @BeforeSuite
  public void beforeSuite() {
  }

  @AfterSuite
  public void afterSuite() {
  }

}
