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
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.SpogConstants;
import Constants.UIConstants;
import InvokerServer.Org4SPOGServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;
import ui.spog.server.UserAccountsHelper;

public class EditUser4UITest extends base.prepare.Is4Org {
	 private SPOGServer spogServer;
	  private UserSpogServer userSpogServer;
	  private UserAccountsHelper userAccountsHelper;
	  private String csrAdminUserName;
	  private String csrAdminPassword;
	  private ExtentTest test;
	
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

	  
	  private String prefix_msp_account = "spogqa_Ramya_msp_account";
	  private String msp_account_admin_name = prefix_msp_account + "_admin";
	  private String msp_account_admin_email =msp_account_admin_name + postfix_email;
	  private String msp_account_admin_first_name = msp_account_admin_name + "_first_name";
	  private String msp_account_admin_last_name = msp_account_admin_name + "_last_name";
	  private String msp_account_admin_id;
	  private String final_msp_account_admin_email;
	  
	  private String prefix_account  = "spogqa_Ramya_account";
	  private String account_user_name = prefix_account + "_admin";
	  private String account_user_name_email = prefix_account + "_admin" + postfix_email;
	  private String account_user_first_name = account_user_name + "_first_name";
	  private String account_user_last_name = account_user_name + "_last_name";
	  
	  
	  private String account_id;
	  private String account_user_email;
	  private String direct_user_id;
	  private String msp_user_id;
	  private String account_user_id;
	private String another_account_id;
	private String another_account_user_email;
	private String another_account_user_id;
	private Org4SPOGServer org4SpogServer;
	private String  org_model_prefix=this.getClass().getSimpleName();
	
	  @BeforeClass
	  @Parameters({ "baseURI", "port",  "browserType", "maxWaitTimeSec",  "logFolder", "csrAdminUserName", "csrAdminPassword", "buildVersion", "uiBaseURI"})
	  public void beforeClass(String baseURI, String port, String browserType, String maxWaitTimeSec,
			  String logFolder, String adminUserName, String adminPassword ,  String buildVersion, String uiBaseURI) throws UnknownHostException {
		
		  
		  userAccountsHelper = new UserAccountsHelper(browserType, Integer.valueOf(maxWaitTimeSec));
		  org4SpogServer = new Org4SPOGServer(baseURI, port);
		  spogServer = new SPOGServer(baseURI, port);
		  userSpogServer = new UserSpogServer(baseURI, port);
		  this.url = uiBaseURI;
		  this.csrAdminUserName = adminUserName;
		  this.csrAdminPassword = adminPassword;
		  rep = ExtentManager.getInstance(this.getClass().getSimpleName(),logFolder);
		  test = rep.startTest(this.getClass().getSimpleName());
		
			
		  prepareEnv();
		  userAccountsHelper.openUrl(url);
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
	  @DataProvider(name = "organizationAndUserInfo3")
	 	public final Object[][] getOrganizationAndUserInfo3() {
	 		return new Object[][] { 	
	 			{this.final_direct_user_name_email, common_password, "direct", SpogConstants.DIRECT_ADMIN, direct_org_id, null,
	 				RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(4)},
	 			{this.final_msp_user_name_email, common_password, "msp", SpogConstants.MSP_ADMIN, msp_org_id, null,
	 				RandomStringUtils.randomAlphanumeric(2), RandomStringUtils.randomAlphanumeric(4)},
	 			{this.account_user_email, common_password, "account", SpogConstants.DIRECT_ADMIN, account_id, null,
	 				RandomStringUtils.randomAlphanumeric(128), RandomStringUtils.randomAlphanumeric(128)},
	 		
	 		};
	 	}
	  @Test(dataProvider = "organizationAndUserInfo3", enabled=true)
	  /**
	   * verify if we can edit user details like firstname
	   * Verify if we can edit lastname
	   * Verify if we can edit email
	   * 1. direct admin in direct org could edit direct admin
	   * 2. msp admin could edit msp admin
	   * 4. account admin could edit account admin
	   * 6. set firstname's length is 128, information could be saved
	   * 9. set lastname's length is 128, could save successfully
	   * @param email
	   * @param password
	   * @param userPrefix
	   * @param role_id
	   * @param org_id
	   * @param uiRole
	   */
	  
	  public void editUserInfo(String email, String password, String userPrefix, String role_id, String org_id, String uiRole, String newFirstName, String newLastName){
		  
		  spogServer.userLogin(email, password);
		  String userEmail = userPrefix + "_" + RandomStringUtils.randomAlphanumeric(8)+ postfix_email;	
		  String userFirstName = userPrefix + "_" +  RandomStringUtils.randomAlphanumeric(8);
		  String userLastName = userPrefix + "_" +  RandomStringUtils.randomAlphanumeric(8);
		  spogServer.createUserAndCheck(userEmail, common_password, userFirstName, userLastName, role_id, org_id, test);
		  		  
		  userAccountsHelper.login_Spog(email, password);
		  userAccountsHelper.editUserDetail(userFirstName + " " + userLastName , newFirstName, newLastName,  userEmail);
		  userAccountsHelper.logout();
	  }
	  
	  @DataProvider(name = "organizationAndUserInfo1")
	 	public final Object[][] getOrganizationAndUserInfo1() {
	 		return new Object[][] { 	
	 			{"", null},
	 			{null,""},
	 			{RandomStringUtils.randomAlphanumeric(129), RandomStringUtils.randomAlphanumeric(12)},
	 			{RandomStringUtils.randomAlphanumeric(19), RandomStringUtils.randomAlphanumeric(129)},
	 		};
	 	}
	  
	  @Test(dataProvider = "organizationAndUserInfo1", enabled=true)
	  /**
	   * 5. set firstname is empty, click save should report error
	   * 8. set lastname is empty, click save should report error
	   * 7. set firstname is more than 128, click save should report error
	   * 10. set lastname is more than 128, click save should report error
	   * @param modifiedfirstName
	   * @param modifiedLastName
	   */
	  public void editUserInfoErrorHandling(String modifiedfirstName, String modifiedLastName){
		  
		  spogServer.userLogin(final_direct_user_name_email, common_password);
		  Response response = spogServer.getLoggedInUser(spogServer.getJWTToken(), SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  String firstName = response.then().extract().path("data.first_name");
		  String lastName = response.then().extract().path("data.last_name");
		  
		  userAccountsHelper.login_Spog(final_direct_user_name_email, common_password);
		  userAccountsHelper.editUserDetailsErrorHandling(firstName + " " + lastName, modifiedfirstName, modifiedLastName);
		  userAccountsHelper.logout();
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
		  	
		  	
			test.log(LogStatus.INFO,"create a msp account admin under msp org");
			final_msp_account_admin_email = prefix + this.msp_account_admin_email;
			msp_account_admin_id = spogServer.createUserAndCheck(final_msp_account_admin_email, common_password, prefix + msp_account_admin_first_name, prefix + msp_account_admin_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, msp_org_id, test);
				
			
			//create account, account user and site
			test.log(LogStatus.INFO,"Creating a account For msp org");
			account_id = spogServer.createAccountWithCheck(msp_org_id, "sub_" + prefix + msp_org_name, "", test);
			prefix = RandomStringUtils.randomAlphanumeric(8);
		
			test.log(LogStatus.INFO,"Creating a account user For account org");
			account_user_email = prefix + account_user_name_email;
			account_user_id = spogServer.createUserAndCheck(account_user_email, common_password, prefix + account_user_first_name, prefix + account_user_last_name, SpogConstants.DIRECT_ADMIN, account_id, test);
			
			//create account, account user 
			test.log(LogStatus.INFO,"Creating another account For msp org");
			prefix = RandomStringUtils.randomAlphanumeric(8);
			another_account_id = spogServer.createAccountWithCheck(msp_org_id, "sub_" + prefix + msp_org_name, "", test);
		  	
			test.log(LogStatus.INFO,"Creating a account admin For another account org");
			another_account_user_email = prefix + account_user_name_email;
			another_account_user_id = spogServer.createUserAndCheck(another_account_user_email, common_password, prefix + account_user_first_name, prefix + account_user_last_name, SpogConstants.DIRECT_ADMIN, another_account_id, test);
			

			
			spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
			test.log(LogStatus.INFO,"assign account to msp account admin");
			String[] mspAccountAdmins = new String []{msp_account_admin_id};
			userSpogServer.assignMspAccountAdmins(msp_org_id, account_id, mspAccountAdmins , spogServer.getJWTToken()); 
			
			
	}
		@AfterClass
		public void subUpdatebd() {
			 /* try {
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
			  String orgHasString=this.getClass().getSimpleName();
			  System.out.println(orgHasString);
			  System.out.println("in father afterclass");
			  System.out.println("class in father is:"+orgHasString);
			  System.out.println("in father after class");
			  destroyOrg(orgHasString);*/
			  this.updatebd();
			  userAccountsHelper.destroy();
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
