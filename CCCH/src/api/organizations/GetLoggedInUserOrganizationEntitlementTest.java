package api.organizations;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

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

import Constants.ErrorCode;
import Constants.SpogConstants;
import InvokerServer.Org4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;
import ui.others.helper.EmailHelper;
import ui.spog.server.SPOGUIServer;

public class GetLoggedInUserOrganizationEntitlementTest extends base.prepare.PrepareOrgInfo{
	
	@Parameters({ "pmfKey"})
	public GetLoggedInUserOrganizationEntitlementTest(String pmfKey) {
		super(pmfKey);
		// TODO Auto-generated constructor stub
	}

	private SPOGServer spogServer;
	  private Org4SPOGServer org4SpogServer;
	  private UserSpogServer user4SpogServer;
	  private SPOGDestinationServer spogDestinationServer;
	  private String csrAdminUserName;
	  private String csrAdminPassword;
	  private ExtentTest test;
	  
	/*  private ExtentReports rep;	
	  private SQLServerDb bqdb1;
	  public int Nooftest;
	  private long creationTime;
	  private String BQName=null;
	  private String runningMachine;
	  private testcasescount count1;
	  private String buildVersion;*/
	  private EmailHelper emailHelper;
	  private SPOGUIServer spogUIServer;
	  private String browserType;
	  private String maxWaitTimeSec;
	  
	  private String postfix_email = "@arcserve.com";
	  private String common_password = "Welcome*02";
	  
	  private String direct_org_id;
	  private String final_direct_user_name_email = null;

	  private String msp_org_id=null;
	  private String final_msp_user_name_email=null;	  
	 
	  private String account_id;
	  private String account_user_email;

	  
	  private String prefix_msp = "spogqa_shuo_msp";
	  private String msp_org_name = prefix_msp + "_org";
	  private String msp_org_email = msp_org_name + postfix_email;
	  private String msp_org_first_name = msp_org_name + "_first_name";
	  private String msp_org_last_name = msp_org_name + "_last_name";
	  private String msp_user_name = prefix_msp + "_admin";
	  private String msp_user_name_email = prefix_msp + "_admin" + postfix_email;
	  private String msp_user_first_name = msp_user_name + "_first_name";
	  private String msp_user_last_name = msp_user_name + "_last_name";
	  
	  
	  private String prefix_msp_account = "spogqa_shuo_msp_account";
	  private String msp_account_admin_name = prefix_msp_account + "_admin";
	  private String msp_account_admin_email =msp_account_admin_name + postfix_email;
	  private String msp_account_admin_first_name = msp_account_admin_name + "_first_name";
	  private String msp_account_admin_last_name = msp_account_admin_name + "_last_name";
	  
	  private String direct_user_id;
	  private String msp_user_id;
	  private String account_user_id;
	  private String another_msp_org_id;
	  private String final_msp_account_admin_email;
	  private String msp_account_admin_id;
	  private String  org_model_prefix=this.getClass().getSimpleName();
	  
	  @BeforeClass
	  @Parameters({ "baseURI", "port", "browserType", "maxWaitTimeSec", "logFolder", "csrAdminUserName", "csrAdminPassword", "buildVersion"})
	  public void beforeClass(String baseURI, String port,String browserType, String maxWaitTimeSec,
			  String logFolder, String adminUserName, String adminPassword ,  String buildVersion) throws UnknownHostException {
		
		
	 	  spogServer = new SPOGServer(baseURI, port);
	 	  org4SpogServer = new Org4SPOGServer(baseURI, port);
	 	  user4SpogServer = new UserSpogServer(baseURI, port);
	 	  spogDestinationServer = new SPOGDestinationServer(baseURI, port);
	 	  this.browserType = browserType;
	 	  this.maxWaitTimeSec = maxWaitTimeSec;
	 	
	 	 
		  rep = ExtentManager.getInstance("CreateUserTest",logFolder);
		  this.csrAdminUserName = adminUserName;
		  this.csrAdminPassword = adminPassword;
		  test = rep.startTest("beforeClass");
		  this.BQName = this.getClass().getSimpleName();
		      String author = "Shuo.Zhang";
		      this.runningMachine =  InetAddress.getLocalHost().getHostName();
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
		      
		   
		        spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
	
				//************************create msp org,user,*************************************
				spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
				String prefix = RandomStringUtils.randomAlphanumeric(8);
				test.log(LogStatus.INFO,"create a msp org");
			//	msp_org_id = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name+org_model_prefix , SpogConstants.MSP_ORG, null, null, null, null, test);
				final_msp_user_name_email = prefix + msp_user_name_email;
				/*
				test.log(LogStatus.INFO,"create a msp admin under msp org");
				msp_user_id = spogServer.createUserAndCheck(final_msp_user_name_email, common_password, prefix + msp_user_first_name, prefix + msp_user_last_name, SpogConstants.MSP_ADMIN, msp_org_id, test);
			*/	msp_org_id = spogServer.CreateOrganizationByEnrollWithCheck(prefix + msp_org_name+org_model_prefix, SpogConstants.MSP_ORG, final_msp_user_name_email, common_password, prefix + msp_user_first_name, prefix + msp_user_last_name);
			 	Response response = user4SpogServer.postUsersSearchResponse(final_msp_user_name_email, null, null, spogServer.getJWTToken());
			 	if(response.statusCode()==SpogConstants.SUCCESS_GET_PUT_DELETE){
	 			 int total_size = response.then().extract().path("pagination.total_size");
			 	  if(total_size==1){
			 		 ArrayList<HashMap<String, Object>> dataList = response.then().extract().path("data");
			 		msp_user_id = (String) dataList.get(0).get("user_id");
	
			 	  }
			 	}
			 	response = org4SpogServer.postOrgFreeTrialCloudHybrid(msp_org_id, spogServer.getJWTToken());
				org4SpogServer.checkPostOrgFreeTrialCloudHybrid(response, SpogConstants.SUCCESS_POST, null);
		 	 
				test.log(LogStatus.INFO,"create a msp account admin under msp org");
				final_msp_account_admin_email = prefix + this.msp_account_admin_email;
				msp_account_admin_id = spogServer.createUserAndCheck(final_msp_account_admin_email, common_password, prefix + msp_account_admin_first_name, prefix + msp_account_admin_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, msp_org_id, test);
			

				spogServer.userLogin(final_msp_user_name_email, common_password);
			  	

				//create account, account user and site
				test.log(LogStatus.INFO,"Creating a account For msp org");
				account_id = spogServer.createAccountWithCheck(msp_org_id, "sub_" + prefix + msp_org_name, "", test);
				prefix = RandomStringUtils.randomAlphanumeric(8);
			
				test.log(LogStatus.INFO,"Creating a account user For account org");
				account_user_email = prefix + msp_user_name_email;
				account_user_id = spogServer.createUserAndCheck(account_user_email, common_password, prefix + msp_user_first_name, prefix + msp_user_last_name, SpogConstants.DIRECT_ADMIN, account_id, test);
				
			
				spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
				test.log(LogStatus.INFO,"assign account to msp account admin");
				String[] mspAccountAdmins = new String []{msp_account_admin_id};
				user4SpogServer.assignMspAccountAdmins(msp_org_id, account_id, mspAccountAdmins , spogServer.getJWTToken());
				
				prepareByEnroll(baseURI, port, logFolder, this.csrAdminUserName,  this.csrAdminPassword, this.getClass().getSimpleName() );
	  }
	  
	  @DataProvider(name = "organizationAndUserInfo1")
		public final Object[][] getOrganizationAndUserInfo1() {
			return new Object[][] {
				{this.final_msp_account_admin_email, this.common_password,  SpogConstants.SUCCESS_GET_PUT_DELETE,null,"trial", "LICENSE"},
				{this.account_user_email, this.common_password,  SpogConstants.SUCCESS_GET_PUT_DELETE,null,"trial", "LICENSE"},
				{this.final_msp_user_name_email, this.common_password,  SpogConstants.SUCCESS_GET_PUT_DELETE,null,"trial", "LICENSE"},
			
				{this.final_root_msp_user_name_email, this.common_password, SpogConstants.SUCCESS_GET_PUT_DELETE,null, "active", "subscription"},
				{this.final_root_msp_account_admin_user_name_email, this.common_password, SpogConstants.SUCCESS_GET_PUT_DELETE,null, "active", "subscription"},
				{this.final_root_msp_direct_org_user_email, this.common_password, SpogConstants.SUCCESS_GET_PUT_DELETE,null, "active", "subscription"},
				{this.final_sub_msp1_msp_account_user_name_email, this.common_password, SpogConstants.SUCCESS_GET_PUT_DELETE,null, "active", "subscription"},
				{this.final_sub_msp1_account1_user_email, this.common_password, SpogConstants.SUCCESS_GET_PUT_DELETE,null, "active", "subscription"},
				{this.final_sub_msp1_user_name_email, this.common_password, SpogConstants.SUCCESS_GET_PUT_DELETE,null, "active", "subscription"},
			};
		}
	  @Test(dataProvider = "organizationAndUserInfo1", enabled=true)
	  /**
	   * 3. msp account admin couldnot get entitlement for its org
	   * 4. account admin could not get entitlement for its org
	   * 7. check response for trial license
	   * @param email
	   * @param password
	   * @param statusCode
	   * @param errorCode
	   */
	  public void getLoggedInUserOrgEntitlement(String email, String password, int statusCode, String errorCode, String CDExpectedState,String cd_billing_type){
		  
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("shuo.zhang");
		  spogServer.userLogin(email,password );
		  String orgType="";
		  String orgId = spogServer.GetLoggedinUserOrganizationID();
		  
		  spogServer.userLogin(this.csrAdminUserName,this.csrAdminPassword );
		  orgType= spogServer.GetOrganizationTypebyID(orgId);
		
	
		  test.log(LogStatus.INFO,"admin login");
		  spogServer.userLogin(email,password );
		  test.log(LogStatus.INFO,"get entitlement");
		  Response response = org4SpogServer.getLoggedInUserOrganizationEntitlement(spogServer.getJWTToken());
		  boolean parentRootMSP= cd_billing_type.equalsIgnoreCase("subscription")?true:false;
		  HashMap<String, Object> cloudDirectInfo = org4SpogServer.getCloudDirectInfo(CDExpectedState, 0, 0, 0, 0,0, orgType,cd_billing_type, parentRootMSP);
		  HashMap<String, Object> cloudHybridInfo = org4SpogServer.getCloudHybridInfo("trial", 0, 0, 0, 0,0, orgType);
		  test.log(LogStatus.INFO,"check entitlement");
		  org4SpogServer.checkGetOrganizationEntitlement(response, cloudDirectInfo ,cloudHybridInfo,  statusCode, errorCode);
	  }
  
  
	  @DataProvider(name = "organizationAndUserInfo2")
		public final Object[][] getOrganizationAndUserInfo2() {
			return new Object[][] { 
				{"arcserveautotest1@hotmail.com", "Storage!07", SpogConstants.DIRECT_ORG,"5", "8", "1","1","1", "0","0","0"},
				{"arcserveautotest1@hotmail.com", "Storage!07", SpogConstants.MSP_ORG,"5", "8", "1","1","1", "0","0","0"},
			};
		}
	  @Test(dataProvider = "organizationAndUserInfo2", enabled=true)
	  /*
	   * 1. direct admin could get entiltements for its org
	   * 2. msp admin could get entitlement for its org
	   * 8. check response when cloud direct license is active
	   * 9. check response when cloud hybrid license is active
	   * 10. create a order to direct org, check the response
	   * 11. add multiple orders to direct org, check the response
	   * 12. create a order to msp org, check the response
	   * 13. create multiple orders to msp org, check the response
	   */
	  public void getLoggedInUserOrgEntitlementWhenAddOrder(String email, String password, String orgType, String cloud_direct_baas, String cloud_direct_draas,
			  String cloud_direct_ad, String cloud_direct_ip, String cloud_hybrid_baas, String cloud_hybrid_draas, String cloud_hybrid_ad, String cloud_hybrid_ip){
		  
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("shuo.zhang");
		  test.log(LogStatus.INFO,"destroy org if it exsits");

		  spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		  Response response = user4SpogServer.postUsersSearchResponse(email, null, null, spogServer.getJWTToken());
 		  if(response.statusCode()==SpogConstants.SUCCESS_GET_PUT_DELETE){
 			 int total_size = response.then().extract().path("pagination.total_size");
		 	  if(total_size==1){
		 		 ArrayList<HashMap<String, Object>> dataList = response.then().extract().path("data");
		 		 HashMap<String ,Object> orgInfo = 	(HashMap<String, Object>) dataList.get(0).get("organization");
		 		org4SpogServer.setToken(spogServer.getJWTToken());
		 		 org4SpogServer.destroyOrganization(orgInfo.get("organization_id").toString() , test);
		 	  }
 		  }
 		  
 		 test.log(LogStatus.INFO,"create organization and user");
 		 String[] datacenterIDs=spogDestinationServer.getDestionationDatacenterID(); 		  
	 	 String datacenter_id=datacenterIDs[0];
	 	 String orgName = "shuo_"+ RandomStringUtils.randomAlphanumeric(6)+org_model_prefix;
	 	 org4SpogServer.setToken(spogServer.getJWTToken());
	 	/* String org_id= org4SpogServer.enrollOrganizationsWithCheck("shuo", "zhang", email, "", orgName,
	 			 orgType, datacenter_id, SpogConstants.SUCCESS_POST, null, test);
	 	
	 	 test.log(LogStatus.INFO,"activate user");
		 emailHelper = new EmailHelper(browserType, Integer.valueOf(maxWaitTimeSec));

		//	 emailHelper.loginMailboxAndOpenActivateAccountPage(email, password);
		 emailHelper.loginMailboxAndOpenActivateAccountPage(email, password, "shuo", "zhang", orgName, email);
		 spogUIServer = new SPOGUIServer(browserType, Integer.valueOf(maxWaitTimeSec));
		 spogUIServer.createAccount(password);
		 emailHelper.destroy();*/
	 	 
		 
		 String org_id=  spogServer.CreateOrganizationByEnrollWithCheck( orgName, orgType, email, password, "shuo", "zhang");
		 response = org4SpogServer.postOrgFreeTrialCloudHybrid(org_id, spogServer.getJWTToken());
	 	 org4SpogServer.checkPostOrgFreeTrialCloudHybrid(response, SpogConstants.SUCCESS_POST, null);
	 		
			
		 
		 test.log(LogStatus.INFO,"add order");
		 
		    //login
		 spogServer.userLogin(email, password);
		 String token =spogServer.getJWTToken();
		 user4SpogServer.setToken(token);
		 String cloudHybridLicenseStatus ="trial";
		 for(int i=1; i<3; i++){
			 String orderID = "SKUTESTDATA_"+ cloud_direct_baas + "_" +cloud_direct_draas+ "_" + cloud_direct_ad + "_"+  cloud_direct_ip;
			 if(cloud_hybrid_baas!=null){
				 orderID += "_" + cloud_hybrid_baas + "_"+ cloud_hybrid_draas+ "_"+ cloud_hybrid_ad + "_"+ cloud_hybrid_ip;;
				 cloudHybridLicenseStatus="active";
			 }
			 orderID+= "_"+ RandomStringUtils.randomNumeric(8);
			 String fulfillmentID = orderID;
			 user4SpogServer.addOrderByOrgIdAndCheck(org_id, orderID , fulfillmentID , test);
			 
			  test.log(LogStatus.INFO,"get entitlement");

			  response = org4SpogServer.getLoggedInUserOrganizationEntitlement(spogServer.getJWTToken());
			  int cdDrVcpu=Math.max(1, Integer.valueOf(cloud_direct_draas)/4)*i;
			  if(cdDrVcpu<2){
				  cdDrVcpu=2;
			  }
			  HashMap<String, Object> cloudDirectInfo = org4SpogServer.getCloudDirectInfo("active",Integer.valueOf(cloud_direct_baas)*i,Integer.valueOf(cloud_direct_draas)*i,
					  Integer.valueOf(cloud_direct_ad)*i,Integer.valueOf(cloud_direct_ip)*i,  cdDrVcpu, orgType,"LICENSE", true);	
			  
			  HashMap<String, Object> cloudHybridInfo = null;
	
			  if(cloud_hybrid_baas!=null){
				  int chDrVcpu=0;
				  if(!cloud_hybrid_draas.equalsIgnoreCase("0")){
					  chDrVcpu= Math.max(1, Integer.valueOf(cloud_hybrid_draas)/4)*i;
				  }		
					cloudHybridInfo = org4SpogServer.getCloudHybridInfo(cloudHybridLicenseStatus, Integer.valueOf(cloud_hybrid_baas)*i,
							  Integer.valueOf(cloud_hybrid_draas)*i, Integer.valueOf(cloud_hybrid_ad)*i,Integer.valueOf(cloud_hybrid_ip)*i, chDrVcpu, orgType);
				  }else{
					 cloudHybridInfo = org4SpogServer.getCloudHybridInfo(cloudHybridLicenseStatus, 0,0,0,0,0, orgType);
				  }
			
		
			  test.log(LogStatus.INFO,"check entitlement");
			  org4SpogServer.checkGetOrganizationEntitlement(response, cloudDirectInfo ,cloudHybridInfo,  SpogConstants.SUCCESS_GET_PUT_DELETE, null);
			  
		 }
		 spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		 org4SpogServer.setToken(spogServer.getJWTToken());
		 test.log(LogStatus.INFO,"destroy org");
		 org4SpogServer.destroyOrganization(org_id , test);
	 	 
	  }
	  
	  @Test(enabled=true)
	  /**
	   * 5. call api without login, authentification fail
	   * 6. call api with invalid token, authentification fail
	   */
	  public void getSpecifiedOrgEntitlementWithoutLogin(){
		 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("shuo.zhang");
		  test.log(LogStatus.INFO,"without login call api");
		  Response response = org4SpogServer.getLoggedInUserOrganizationEntitlementWithoutLogin();
		  org4SpogServer.checkGetOrganizationEntitlement(response, null ,null,  SpogConstants.NOT_LOGGED_IN, ErrorCode.AUTHORIZATION_HEADER_BLANK);
		  test.log(LogStatus.INFO,"call api with random token");
		  response = org4SpogServer.getLoggedInUserOrganizationEntitlement(UUID.randomUUID().toString());
		  org4SpogServer.checkGetOrganizationEntitlement(response, null ,null,  SpogConstants.NOT_LOGGED_IN, ErrorCode.AUTHORIZATION_HEADER_BLANK);
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
