package api.sources.groups;

import invoker.SiteTestHelper;
import io.restassured.response.Response;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
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


import Constants.SpogConstants;
import InvokerServer.GatewayServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import InvokerServer.GatewayServer.siteType;
import api.preparedata.InitialTestData;
import api.preparedata.InitialTestDataImpl;
import InvokerServer.Source4SPOGServer;
import InvokerServer.UserSpogServer;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class GetSourceGroupAmount extends base.prepare.Is4Org{
		private String  org_model_prefix=this.getClass().getSimpleName();
		

	  private SPOGServer spogServer;
	  private SPOGDestinationServer spogDestinationServer;
	  private GatewayServer gatewayServer;
	  private Source4SPOGServer source4SPOGServer;
	  private UserSpogServer userSpogServer;
	  private String csrAdminUserName;
	  private String csrAdminPassword;
//	  private ExtentReports rep;
	  private ExtentTest test;
	  
	  private String csr_token=null;
	  private String postfix_email = "@arcserve.com";
	  private String common_password = "Welcome*02";
	  
		private String csr_readonly_email = "csr_readonly_zhaoguo@arcserve.com";
		  private String csr_readonly_password = "Caworld_2017";
		  
			private InitialTestDataImpl initialTestDataImpl;
			private InitialTestData itd;
			private String password = "Pa$$w0rd";

	  
	  private String prefix_direct = "SPOG_QA_Leiyu_BQ_direct";
	  private String direct_org_name = prefix_direct + "_org";
	  private String direct_org_id=null;
	  private String direct_user_name = prefix_direct + "_admin";
	  private String direct_user_name_email = direct_user_name + postfix_email;
	  private String direct_user_first_name = direct_user_name + "_first_name";
	  private String direct_user_last_name = direct_user_name + "_last_name";
	  private String direct_user_id =null;
	  private String final_direct_user_name_email = null;
	  private String direct_site_id;
	  private String direct_user_cloud_account_id;
	  
	  private String prefix_msp = "SPOG_QA_Leiyu_BQ__msp";
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

	  
	  private String prefix_msp_account="SPOG_QA_Leiyu_BQ_msp_account_admin";
	  private String msp_account_admin_name = prefix_msp_account + "_admin";
	  private String msp_account_admin_email =msp_account_admin_name + postfix_email;
	  private String msp_account_admin_first_name = msp_account_admin_name + "_first_name";
	  private String msp_account_admin_last_name = msp_account_admin_name + "_last_name";
	  private String msp_account_admin_id;
	  private String final_msp_account_admin_email;
	  
	  private String account_id;
	  private String account_user_email;
	  private String account_user_id;
		
	  private String prefix_csr = "SPOG_QA_Leiyu_BQ_csr";
	  private String csr_user_name = prefix_csr + "_admin";

	  
	  
	  
	  //this is for update portal, each testng class is taken as BQ set
//	  private SQLServerDb bqdb1;
//	  public int Nooftest;
//	  private long creationTime;
//	  private String BQName=null;
//	  private String runningMachine;
//	  private testcasescount count1;
//	  private String buildVersion;
	  

	  /**
	   * Test cases:
	 MSP account admin could get source group amount
	 direct admin could get source group amount
	 MSP admin could get source group amount
	 account admin could get source group amount
	   * @param username
	   * @param password
	   */
	  @Test(dataProvider = "GetPolicyAmountParameter")
	  public void GetSourceGroupAmountType(String username, String password, int count){
	 	 	test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
	 	  	test.assignAuthor("leiyu.wang");
	 	  
	 		test.log(LogStatus.INFO,username+" admin user login");
	 		spogServer.userLogin(username, password);		
	 		source4SPOGServer.setToken(spogServer.getJWTToken());
	 		
	 		if(username.equals(final_msp_account_admin_email))
	 			userSpogServer.assignMspAccountAdmins(msp_org_id,account_id, msp_account_admin_id, csr_token);
	 		
	 		source4SPOGServer.getSourceGroupAmountWithCheck(count, test);
	  }

	  @DataProvider(name="GetPolicyAmountParameter")
	  public Object[][] GetPolicyAmountParameter(){
	 	 return new Object[][]{	 			 
	 			 {final_direct_user_name_email, common_password,2},			 
	 			 {final_msp_user_name_email, common_password,4},	
	 			 {account_user_email, common_password,1},			
	 			 {final_msp_account_admin_email,common_password,1}, 
	 			{itd.getRoot_msp_org_1_user_1_email(), password, 4}, 
	 			{itd.getRoot_msp_org_1_sub_msp_1_user_1_email(), password, 4}, 
	 			{itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1_email(), password, 1}, 
	 			{itd.getRoot_msp_org_1_account_1_user_1_email(), password, 1}, 
	 			{itd.getRoot_msp_org_1_sub_msp_1_account_admin_1_email(), password, 1},
	 			{itd.getRoot_msp_org_1_account_admin_1_email(), password, 1},

	 	 };
	  }

	 /**
	  * csr admin could get policy amount
	  * @author leiyu.wang
	  */
	  @Test(enabled=true)
	  public void GetSourceGroupAmountFail(){
	 	 	test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
	 	  	test.assignAuthor("leiyu.wang");
	 	  
	 		test.log(LogStatus.INFO,csrAdminUserName + " admin user login");
	 		spogServer.userLogin(csrAdminUserName, csrAdminPassword);		

	 		source4SPOGServer.setToken(spogServer.getJWTToken());		
	 		source4SPOGServer.getSourceGroupAmountFail(200, test);
	 		
	 		spogServer.userLogin(csr_readonly_email, csr_readonly_password);		

	 		source4SPOGServer.setToken(spogServer.getJWTToken());		
	 		source4SPOGServer.getSourceGroupAmountFail(200, test);
	 		
	  }
	   
	  //could not get policy amount without token, report401
	  @Test(enabled=true)
	  public void GetSourceGroupAmountwithoutToken(){
	 	 	test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
	 	  	test.assignAuthor("leiyu.wang");
	 	  
	 		test.log(LogStatus.INFO,"admin login");					
	 		source4SPOGServer.setToken("");
	 		source4SPOGServer.getSourceGroupAmountFail(401, test);
	  } 
	  
	  //could not get policy amount with invalid token
	  @Test(enabled=true)
	  public void GetSourceGroupAmountWithInvalidToken(){
	 	 	test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
	 	  	test.assignAuthor("leiyu.wang");
	 	  
	 		test.log(LogStatus.INFO,csrAdminUserName+ " admin login");
	 		
	 		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
	 		source4SPOGServer.setToken("*****************************");
	 		source4SPOGServer.getSourceGroupAmountFail(401, test);	
	  }
	  
  @BeforeClass
  @Parameters({ "baseURI", "port", "logFolder", "csrAdminUserName", "csrAdminPassword", "buildVersion"})
  public void beforeClass(String baseURI, String port, String logFolder, String adminUserName, String adminPassword ,  String buildVersion) throws UnknownHostException {
	
	  	spogServer = new SPOGServer(baseURI, port);
	  	spogDestinationServer=new SPOGDestinationServer(baseURI,port);
	  	gatewayServer =new GatewayServer(baseURI,port);
	  	source4SPOGServer= new Source4SPOGServer(baseURI, port);
	  	userSpogServer=new UserSpogServer(baseURI, port);
	  	rep = ExtentManager.getInstance("GetSourceGroupAmountTest",logFolder);
	  	this.csrAdminUserName = adminUserName;
	  	this.csrAdminPassword = adminPassword;

	    test = rep.startTest("beforeClass");
	    spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
		csr_token=spogServer.getJWTToken();
		//*******************create direct org,user,site**********************/
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		test.log(LogStatus.INFO,"create a direct org");
		direct_org_id = spogServer.CreateOrganizationWithCheck(prefix+direct_org_name + org_model_prefix , SpogConstants.DIRECT_ORG, null, common_password, null, null, test);
		final_direct_user_name_email = prefix + direct_user_name_email;
				
		test.log(LogStatus.INFO,"create a admin under direct org");
		direct_user_id = spogServer.createUserAndCheck(final_direct_user_name_email, common_password, prefix + direct_user_first_name, prefix + direct_user_last_name, SpogConstants.DIRECT_ADMIN, direct_org_id, test);
		spogServer.userLogin(final_direct_user_name_email, common_password);
	  	
		//create group for direct org
		createSourceOrg(direct_org_id,"Direct_Org_Group","Direct_Org_Group",2, test);
		
	
		
		//************************create msp org,user,site*************************************
		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
		prefix = RandomStringUtils.randomAlphanumeric(8);
		test.log(LogStatus.INFO,"create a msp org");
		msp_org_id = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name + org_model_prefix , SpogConstants.MSP_ORG, null, common_password, null, null, test);
		final_msp_user_name_email = prefix + msp_user_name_email;
		
		test.log(LogStatus.INFO,"create a admin under msp org");
		msp_user_id = spogServer.createUserAndCheck(final_msp_user_name_email, common_password, prefix + msp_user_first_name, prefix + msp_user_last_name, SpogConstants.MSP_ADMIN, msp_org_id, test);
		spogServer.userLogin(final_msp_user_name_email, common_password);
		
		//create group for msp_org_id
		createSourceOrg(msp_org_id,"msp_org_Group","msp_org_Group",3, test);		

		
		//create MSP account admin
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
	  	
		//create group for account_org
		createSourceOrg(account_id,"account_org_Group","account_org_Group",1,test);	
	
		String prefix_a = RandomStringUtils.randomAlphanumeric(8) + this.getClass().getSimpleName();		
		initialTestDataImpl = new InitialTestDataImpl(baseURI, port, csrAdminUserName, csrAdminPassword, prefix_a, password);
		itd = initialTestDataImpl.initialize();
		
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
  
  public void createSourceOrg(String orgId,String groupName,String groupDes, int count, ExtentTest test){
	  for(int i=0;i<count;i++){
		  test.log(LogStatus.INFO, "create "+groupName+ count);
		  String prefix = RandomStringUtils.randomAlphanumeric(8);
		  Response response = spogServer.createGroup(orgId, prefix+groupName, prefix+groupDes);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST,test);	  
	  }
	  
  }
  
	@AfterMethod
	public void afterMethodTest() {
		rep.endTest(test);
		rep.flush();
	}
  
	@AfterMethod
	public void getResult(ITestResult result) {
		if (result.getStatus() == ITestResult.FAILURE) {
			count1.setfailedcount();
			test.log(LogStatus.FAIL, "Test Case Failed is " + result.getName() + " with parameters as "
					+ Arrays.asList(result.getParameters()));
			test.log(LogStatus.FAIL, result.getThrowable().getMessage());
		} else if (result.getStatus() == ITestResult.SKIP) {
			count1.setskippedcount();
			test.log(LogStatus.SKIP, "Test Case Skipped is " + result.getName());
		} else if (result.getStatus() == ITestResult.SUCCESS) {
			count1.setpassedcount();
		}
		// ending test
		// endTest(logger) : It ends the current test and prepares to create HTML report
		rep.endTest(test);
	}
}

