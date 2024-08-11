package ui.user;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.ErrorCode;
import Constants.SpogConstants;
import InvokerServer.Org4SPOGServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import ui.spog.server.SPOGUIServer;


public class UserLogin4UITest extends base.prepare.Is4Org{

 private SPOGServer spogServer;
  private UserSpogServer userSpogServer;
  private SPOGUIServer spogUIServer;
  private String csrAdminUserName;
  private String csrAdminPassword;
  private ExtentTest test;
  
  /*private ExtentReports rep;
  private SQLServerDb bqdb1;
  public int Nooftest;
  private long creationTime;
  private String BQName=null;
  private String runningMachine;
  private testcasescount count1;
  private String buildVersion;*/
  private String url;
  
  private String postfix_email = "@arcserve.com";
  private String common_password = "Welcome*02";
  
  private String prefix_direct = "spogqa_Ramya_direct";
  private String direct_org_name = prefix_direct + "_org";
  private String direct_org_id=null;
  private String direct_user_name = prefix_direct + "_admin";
  private String direct_user_name_email = direct_user_name + postfix_email;
  private String direct_user_first_name = direct_user_name + "_first_name";
  private String direct_user_last_name = direct_user_name + "_last_name";
  private String final_direct_user_name_email = null;

  
  
  private String prefix_msp = "spogqa_Ramya_msp";
  private String msp_org_name = prefix_msp + "_org";
  private String msp_user_name = prefix_msp + "_admin";
  private String msp_user_name_email = prefix_msp + "_admin" + postfix_email;
  private String msp_user_first_name = msp_user_name + "_first_name";
  private String msp_user_last_name = msp_user_name + "_last_name";
  private String msp_org_id=null;
  private String final_msp_user_name_email=null;	  

  
  
  private String account_id;
  private String account_user_email;
  private String direct_user_id;
  private String msp_user_id;
  private String account_user_id;
private Org4SPOGServer org4SpogServer;
private String  org_model_prefix=this.getClass().getSimpleName();


  @BeforeClass
  @Parameters({ "baseURI", "port",   "logFolder", "csrAdminUserName", "csrAdminPassword", "buildVersion", "uiBaseURI"})
  public void beforeClass(String baseURI, String port, 
		  String logFolder, String adminUserName, String adminPassword ,  String buildVersion, String uiBaseURI) throws UnknownHostException {
	
	  org4SpogServer = new Org4SPOGServer(baseURI, port);
	  spogServer = new SPOGServer(baseURI, port);
	  userSpogServer = new UserSpogServer(baseURI, port);
	  this.url = uiBaseURI;
	  this.csrAdminUserName = adminUserName;
	  this.csrAdminPassword = adminPassword;
	  rep = ExtentManager.getInstance(this.getClass().getSimpleName(),logFolder);
	  test = rep.startTest(this.getClass().getSimpleName());
	  prepareEnv();
	  this.BQName = this.getClass().getSimpleName();
      String author = "Ramya.Nagepalli";
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

  }
  
  @BeforeMethod
  @Parameters({  "browserType", "maxWaitTimeSec"})
  public void beforeMethod(String browserType, String maxWaitTimeSec){
	  
	  spogUIServer = new SPOGUIServer(browserType, Integer.valueOf(maxWaitTimeSec));
	  spogUIServer.openUrl(url);
  }
  
  
  
  @DataProvider(name = "organizationAndUserInfo1")
 	public final Object[][] getOrganizationAndUserInfo1() {
 		return new Object[][] { 	{this.final_direct_user_name_email, common_password},
 			{this.final_msp_user_name_email,   common_password},
 			{" " + account_user_email+ " ", common_password},

 		};
 	}
  @Test(dataProvider = "organizationAndUserInfo1", enabled=true)
  /**
   * Login with valid mail ID and valid password
   * Check if uppercase letters are accepted for Username
   * Check if uppercase letters are accepted for Password
   * Left and right trimming should be done for Password field
   * Left and right trimming should be done for Username field
   */
  public void loginSuccessfully(String userName, String password) {

	  spogUIServer.login_Spog(userName, password);
	
  }
  
  @DataProvider(name = "organizationAndUserInfo2")
 	public final Object[][] getOrganizationAndUserInfo2() {
 		return new Object[][] { 	{"hayy_"+ final_direct_user_name_email, common_password, "The user [hayy_"+ final_direct_user_name_email+"] is not found."},
 			{ final_direct_user_name_email, "", "Username or Password not provided"},			
 			
 		};
 	}
  @Test(dataProvider = "organizationAndUserInfo2", enabled=true)
  /**
   * Login using Invalid email ID and valid password
   * Login with blank password
   * 
   * @param userName
   * @param password
   * @param expectedErrorMsg
   */
  public void loginFail(String userName, String password, String expectedErrorMsg){
	
	  spogUIServer.login_Spog_with_error(userName, password, expectedErrorMsg);
  }
  
  @Test
  /**
   * Login without username and password
   */
  public void loginWithNoInfo(){
	  spogUIServer.checkLoginBtnIsDisabled();
  }
  
  
  @AfterMethod
  public void afterMethod(){
	  spogUIServer.destroy();
	
  }
  private void prepareEnv(){
		
	  	spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
		//*******************create direct org,user,site**********************/
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		test.log(LogStatus.INFO,"create a direct org");
		direct_org_id = spogServer.CreateOrganizationWithCheck(prefix+direct_org_name+org_model_prefix , SpogConstants.DIRECT_ORG, null, null, null, null, test);
		final_direct_user_name_email = prefix + direct_user_name_email;
		
		test.log(LogStatus.INFO,"create a admin under direct org");
		direct_user_id = spogServer.createUserAndCheck(final_direct_user_name_email, common_password, prefix + direct_user_first_name, prefix + direct_user_last_name, SpogConstants.DIRECT_ADMIN, direct_org_id, test);
		spogServer.userLogin(final_direct_user_name_email, common_password);
	  	

		//************************create msp org,user,site*************************************
		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
		prefix = RandomStringUtils.randomAlphanumeric(8);
		test.log(LogStatus.INFO,"create a msp org");
		msp_org_id = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name+org_model_prefix , SpogConstants.MSP_ORG, null, null, null, null, test);
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

		
		
}

  private void deleteEnv(){

		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
				
		spogServer.errorHandle.printInfoMessageInDebugFile("delete direct organization");
		 org4SpogServer.destroyOrganization(direct_org_id , test);
		
		test.log(LogStatus.INFO,"delete msp org");
		spogServer.errorHandle.printInfoMessageInDebugFile("delete msp organization");
		 org4SpogServer.destroyOrganization(msp_org_id , test);
		
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
    rep.endTest(test);    
}
}
