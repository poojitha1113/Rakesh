package api;

import org.testng.annotations.Test;

import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.SPOGServer;
import InvokerServer.GatewayServer.siteType;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import invoker.SiteTestHelper;
import io.restassured.response.Response;

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
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class getUserSessionTest {
	  private SPOGServer spogServer;
	  private GatewayServer gatewayServer;
	  private String csrAdminUserName;
	  private String csrAdminPassword;
	  private ExtentReports rep;
	  private ExtentTest test;
	  
	  private String create_ts="";
	  private String last_login_ts="";
	  private String blocked="false";
	  private String image_url="";	 
	  private String organization_id="";	  
	  private String postfix_email = "@arcserve.com";
	  private String common_password = "Welcome*02";
	  private String csr_admin_org_ID="";
	  private String phone_number="null";
	  
	  private String prefix_direct = "spogqa_yaner01_direct";
	  private String direct_org_name = prefix_direct + "_org";
	  private String final_direct_org_name=null;
	  private String direct_org_id=null;
	  private String direct_user_name = prefix_direct + "_admin";
	  private String direct_user_name_email = direct_user_name + postfix_email;
	  private String direct_user_first_name = direct_user_name + "_first_name";
	  private String direct_user_last_name = direct_user_name + "_last_name";
	  private String direct_user_id =null;
	  private String final_direct_user_name_email = null;
	  private String direct_site_id;

	  private String final_direct_user_first_name;
	  private String final_direct_user_last_name;
  

	  
	  private String prefix_msp = "spogqa_yaner01_msp";
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
	  private String final_msp_org_name=null;
	  private String final_msp_user_name_email=null;
	  private String final_msp_user_first_name;
	  private String final_msp_user_last_name;
	  private String msp_site_id;
	  
	private String account_user_first_name=null;
	private String account_user_last_name=null;
	private String account_name=null;
	  
	  private String account_id;
	  private String account_user_email;
	  private String account_user_id;
	  private String account_site_id;
	  
	  private String another_direct_site_id;
	  private String another_msp_site_id;
	  private String another_account_site_id;
		
	  private String prefix_csr = "spogqa_yaner01_csr";
	  private String csr_user_name = prefix_csr + "_admin";
	  private String csr_user_name_email = prefix_csr + "_admin" + postfix_email;
	  private String csr_user_first_name = csr_user_name + "_first_name";
	  private String csr_user_last_name = csr_user_name + "_last_name";
	  
	  //this is for update portal, each testng class is taken as BQ set
	  private SQLServerDb bqdb1;
	  public int Nooftest;
	  private long creationTime;
	  private String BQName=null;
	  private String runningMachine;
	  private testcasescount count1;
	  private String buildVersion;	 
	  @BeforeClass
	  @Parameters({ "baseURI", "port", "logFolder", "csrAdminUserName", "csrAdminPassword", "buildVersion"})
	  public void beforeClass(String baseURI, String port, String logFolder, String adminUserName, String adminPassword ,  String buildVersion) throws UnknownHostException {
		
		  	spogServer = new SPOGServer(baseURI, port);
		  	gatewayServer =new GatewayServer(baseURI,port);
		  	rep = ExtentManager.getInstance("GetUserSession",logFolder);
		  	this.csrAdminUserName = adminUserName;
		  	this.csrAdminPassword = adminPassword;

		    test = rep.startTest("beforeClass");
			spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
			//*******************create direct org,user,site**********************/
			String prefix = RandomStringUtils.randomAlphanumeric(8);
			test.log(LogStatus.INFO,"create a direct org");
			final_direct_org_name=prefix+direct_org_name;
			direct_org_id = spogServer.CreateOrganizationWithCheck(prefix+direct_org_name , SpogConstants.DIRECT_ORG, null, common_password, null, null, test);
			final_direct_user_name_email = prefix + direct_user_name_email;
			this.final_direct_user_first_name = prefix + direct_user_first_name;
			this.final_direct_user_last_name = prefix + direct_user_last_name;
			
			test.log(LogStatus.INFO,"create a admin under direct org");
			direct_user_id = spogServer.createUserAndCheck(final_direct_user_name_email, common_password, prefix + direct_user_first_name, prefix + direct_user_last_name, SpogConstants.DIRECT_ADMIN, direct_org_id, test);
			spogServer.userLogin(final_direct_user_name_email, common_password);
		  	
			test.log(LogStatus.INFO,"Getting the JWTToken for the direct user");
			String direct_user_validToken = spogServer.getJWTToken();
			test.log(LogStatus.INFO,"The token is :"+ direct_user_validToken );
			
			String siteName= spogServer.getRandomSiteName("TestCreate");
			test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
			String sitetype=siteType.gateway.toString();
			test.log(LogStatus.INFO,"The siteType :"+sitetype);
			
			test.log(LogStatus.INFO,"Creating a site For direct org");
			direct_site_id = gatewayServer.createsite_register_login(direct_org_id, direct_user_validToken, direct_user_id, "yaner01", "1.0.0", spogServer, test);
			String direct_site_token=gatewayServer.getJWTToken();
			test.log(LogStatus.INFO, "The site token is "+direct_site_token);
			
			siteName= spogServer.getRandomSiteName("TestCreate");
			test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
			sitetype=siteType.gateway.toString();
			test.log(LogStatus.INFO,"The siteType :"+sitetype);
			
			test.log(LogStatus.INFO,"Creating another site For direct org");
			another_direct_site_id = gatewayServer.createsite_register_login(direct_org_id, direct_user_validToken, direct_user_id, "yaner011", "1.0.0", spogServer, test);
			String another_direct_site_token=gatewayServer.getJWTToken();
			test.log(LogStatus.INFO, "The site token is "+another_direct_site_token);

			//************************create msp org,user,site*************************************
			spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
			prefix = RandomStringUtils.randomAlphanumeric(8);
			test.log(LogStatus.INFO,"create a msp org");
			msp_org_id = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name , SpogConstants.MSP_ORG, null, common_password, null, null, test);
			final_msp_user_name_email = prefix + msp_user_name_email;
			final_msp_org_name=prefix + msp_org_name;
			
			test.log(LogStatus.INFO,"create a admin under msp org");
			msp_user_id = spogServer.createUserAndCheck(final_msp_user_name_email, common_password, prefix + msp_user_first_name, prefix + msp_user_last_name, SpogConstants.MSP_ADMIN, msp_org_id, test);
			spogServer.userLogin(final_msp_user_name_email, common_password);
			final_msp_user_first_name=prefix + msp_user_first_name;
			final_msp_user_last_name=prefix + msp_user_last_name;
		  	
			test.log(LogStatus.INFO,"Getting the JWTToken for the msp user");
			String msp_user_validToken = spogServer.getJWTToken();
			test.log(LogStatus.INFO,"The token is :"+ msp_user_validToken );
			
			siteName= spogServer.getRandomSiteName("TestCreate");
			test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
			sitetype=siteType.gateway.toString();
			test.log(LogStatus.INFO,"The siteType :"+sitetype);
			
			test.log(LogStatus.INFO,"Creating a site For msp org");
			msp_site_id = gatewayServer.createsite_register_login(msp_org_id, msp_user_validToken, msp_user_id, "yaner01", "1.0.0", spogServer, test);
			String msp_site_token=gatewayServer.getJWTToken();
			test.log(LogStatus.INFO, "The site token is "+ msp_site_token);
			
			siteName= spogServer.getRandomSiteName("TestCreate");
			test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
			sitetype=siteType.gateway.toString();
			test.log(LogStatus.INFO,"The siteType :"+sitetype);
			
			test.log(LogStatus.INFO,"Creating another site For msp org");
			another_msp_site_id = gatewayServer.createsite_register_login(msp_org_id, msp_user_validToken, msp_user_id, "yaner011", "1.0.0", spogServer, test);
			String another_msp_site_token=gatewayServer.getJWTToken();
			test.log(LogStatus.INFO, "The site token is "+ another_msp_site_token);
			
			

			//create account, account user and site
			test.log(LogStatus.INFO,"Creating a account For msp org");
			account_id = spogServer.createAccountWithCheck(msp_org_id, "sub_" + prefix + msp_org_name, "", test);
			account_name="sub_" + prefix + msp_org_name;
			prefix = RandomStringUtils.randomAlphanumeric(8);
			
			
			test.log(LogStatus.INFO,"Creating a account user For account org");
			account_user_email = prefix + msp_user_name_email;
			account_user_id = spogServer.createUserAndCheck(account_user_email, common_password, prefix + msp_user_first_name, prefix + msp_user_last_name, SpogConstants.DIRECT_ADMIN, account_id, test);
			spogServer.userLogin(account_user_email, common_password);
			account_user_first_name=prefix + msp_user_first_name;
			account_user_last_name=prefix + msp_user_last_name;
			
			test.log(LogStatus.INFO,"Getting the JWTToken for the account user");
			String account_user_validToken = spogServer.getJWTToken();
			test.log(LogStatus.INFO,"The token is :"+ account_user_validToken );
			
			siteName= spogServer.getRandomSiteName("TestCreate");
			test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
			sitetype=siteType.gateway.toString();
			test.log(LogStatus.INFO,"The siteType :"+sitetype);
			
			test.log(LogStatus.INFO,"Creating a site For account org");
			account_site_id = gatewayServer.createsite_register_login(account_id, account_user_validToken, account_user_id, "yaner01", "1.0.0", spogServer, test);
			String account_site_token=gatewayServer.getJWTToken();
			test.log(LogStatus.INFO, "The site token is "+ account_site_token);
			
			siteName= spogServer.getRandomSiteName("TestCreate");
			test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
			sitetype=siteType.gateway.toString();
			test.log(LogStatus.INFO,"The siteType :"+sitetype);
			
			test.log(LogStatus.INFO,"Creating another site For account org");
			another_account_site_id = gatewayServer.createsite_register_login(account_id, account_user_validToken, account_user_id, "yaner011", "1.0.0", spogServer, test);
			String another_account_site_token=gatewayServer.getJWTToken();
			test.log(LogStatus.INFO, "The site token is "+ another_account_site_token);
			
			
		  	//this is for update portal
		  	this.BQName = this.getClass().getSimpleName();
		    String author = "Eric.Yang";
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
	  
	  /**
	   * @author Eric.Yang
	   * @testcase:
	   * 1. csrAdmin Li Xiang login and  get user session with valid token
	   * 2. get user session with invalid token
	   * 3. get user session with miss token
	   * 4. direct admin login and  get user session with valid token
	   * 5. MSP admin login and  get user session with valid token
	   * 7. account admin login and  get user session with valid token
	   * 
	   */
	  
	@Test
	@Parameters({  "csrAdminUserName", "csrAdminPassword" })
	public void getLoggedUserCsrAdminWithValidToken(String AdminUserName, String AdminPassword) {
		test=rep.startTest("getUserSessionCsrAdminWithValidToken");
		test.assignAuthor("Eric Yang");
		this.csrAdminUserName=AdminUserName;
		this.csrAdminPassword=AdminPassword;
		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
		test.log(LogStatus.INFO,"Getting the token of the csr_admin");
		String validToken=spogServer.getJWTToken();
		test.log(LogStatus.INFO, "Getting the logged in user for csr admin");
		String userID=spogServer.GetLoggedinUser_UserID();
		Response user_response=spogServer.getUserByID(userID, test);

		create_ts=user_response.then().extract().path("data." + "create_ts").toString().toLowerCase();
		last_login_ts=user_response.then().extract().path("data." + "last_login_ts").toString().toLowerCase();
		blocked=user_response.then().extract().path("data." + "blocked").toString().toLowerCase();
		image_url=user_response.then().extract().path("data." + "image_url").toString().toLowerCase();
		organization_id=user_response.then().extract().path("data." + "organization_id").toString().toLowerCase();
		this.csr_admin_org_ID=organization_id;
		Response response =spogServer.getUserSessionWithCheck(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
/**
 *   			       String adminusername,String first_name,String last_name,String phone_number,String role_id,String organization_id,String user_id,String image_url,String create_ts,String last_login_ts,String blocked
  			       ,String organization_name, String organization_type, String parent_id, ExtentTest test )
		*/
	
		spogServer.checkUserSession(response,SpogConstants.SUCCESS_GET_PUT_DELETE, csrAdminUserName,"Xiang","Li","+1 405 517 1234","csr_admin",organization_id
				,userID,image_url,create_ts,last_login_ts,blocked
				,"xiang_csr","csr", "00000000-0000-0000-0000-000000000000","","", test );
		
		}
	@Test
	@Parameters({  "csrAdminUserName", "csrAdminPassword" })
	public void getLoggedUserCsrAdminWithInValidToken(String AdminUserName, String AdminPassword) {
		test=rep.startTest("getUserSessionCsrAdminWithInValidToken");
		test.assignAuthor("Eric Yang");
		this.csrAdminUserName=AdminUserName;
		this.csrAdminPassword=AdminPassword;
		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
		test.log(LogStatus.INFO,"Getting the token of the csr_admin");
		String validToken=spogServer.getJWTToken();
		String invalidToken="eyJhbGciOiJIU";
		test.log(LogStatus.INFO, "Getting the logged in user for csr admin");
		String userID=spogServer.GetLoggedinUser_UserID();
		Response user_response=spogServer.getUserByID(userID, test);

		create_ts=user_response.then().extract().path("data." + "create_ts").toString().toLowerCase();
		last_login_ts=user_response.then().extract().path("data." + "last_login_ts").toString().toLowerCase();
		blocked=user_response.then().extract().path("data." + "blocked").toString().toLowerCase();
		image_url=user_response.then().extract().path("data." + "image_url").toString().toLowerCase();
		organization_id=user_response.then().extract().path("data." + "organization_id").toString().toLowerCase();
		this.csr_admin_org_ID=organization_id;
		Response response =spogServer.getUserSessionWithCheck(invalidToken, SpogConstants.NOT_LOGGED_IN, test);
		spogServer.checkUserSession(response,401, csrAdminUserName,"Xiang","Li","+1 405 517 1234","csr_admin",organization_id
				,userID,image_url,create_ts,last_login_ts,blocked
				,"xiang_csr","csr", "00000000-0000-0000-0000-000000000000","Invalid JWT token: ","00900006", test );
		
		}	
	@Test
	@Parameters({  "csrAdminUserName", "csrAdminPassword" })
	public void getLoggedUserCsrAdminWithMissToken(String AdminUserName, String AdminPassword) {
		test=rep.startTest("getUserSessionCsrAdminWithValidToken");
		test.assignAuthor("Eric Yang");
		this.csrAdminUserName=AdminUserName;
		this.csrAdminPassword=AdminPassword;
		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
		test.log(LogStatus.INFO,"Getting the token of the csr_admin");
		String validToken=spogServer.getJWTToken();
		String invalidToken=validToken+"f";
		test.log(LogStatus.INFO, "Getting the logged in user for csr admin");
		String userID=spogServer.GetLoggedinUser_UserID();
		Response user_response=spogServer.getUserByID(userID, test);

		create_ts=user_response.then().extract().path("data." + "create_ts").toString().toLowerCase();
		last_login_ts=user_response.then().extract().path("data." + "last_login_ts").toString().toLowerCase();
		blocked=user_response.then().extract().path("data." + "blocked").toString().toLowerCase();
		image_url=user_response.then().extract().path("data." + "image_url").toString().toLowerCase();
		organization_id=user_response.then().extract().path("data." + "organization_id").toString().toLowerCase();
		this.csr_admin_org_ID=organization_id;
		Response response =spogServer.getUserSessionWithCheck("", SpogConstants.NOT_LOGGED_IN, test);
		spogServer.checkUserSession(response,401, csrAdminUserName,"Xiang","Li","+1 405 517 1234","csr_admin",organization_id
				,userID,image_url,create_ts,last_login_ts,blocked
				,"xiang_csr","csr", "00000000-0000-0000-0000-000000000000","Authorization header cannot be blank!","00900006", test );
		
		}	

	@Test
	@Parameters({  "csrAdminUserName", "csrAdminPassword" })
	public void getUserSession_DirectAdmin_WithValidToken(String AdminUserName, String AdminPassword) {
		test=rep.startTest("getUserSession_DirectAdmin_WithValidToken");
		test.assignAuthor("Eric Yang");
		this.csrAdminUserName=AdminUserName;
		this.csrAdminPassword=AdminPassword;
		spogServer.userLogin(this.final_direct_user_name_email, this.common_password);
		test.log(LogStatus.INFO,"Getting the token of the direct_admin");
		String validToken=spogServer.getJWTToken();
		test.log(LogStatus.INFO, "Getting the logged in user for direct admin");
		//String userID=spogServer.GetLoggedinUser_UserID();
		//userID=direct_user_id;
		Response user_response=spogServer.getUserByID(direct_user_id, test);
		//spogServer.GetOrganizationParentID(response)
		String ParentID=this.csr_admin_org_ID;
		

		create_ts=user_response.then().extract().path("data." + "create_ts").toString().toLowerCase();
		last_login_ts=user_response.then().extract().path("data." + "last_login_ts").toString().toLowerCase();
		blocked=user_response.then().extract().path("data." + "blocked").toString().toLowerCase();
		image_url=user_response.then().extract().path("data." + "image_url").toString().toLowerCase();
		organization_id=user_response.then().extract().path("data." + "organization_id").toString().toLowerCase();
		
		Response response =spogServer.getUserSessionWithCheck(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		spogServer.checkUserSession(response,SpogConstants.SUCCESS_GET_PUT_DELETE, this.final_direct_user_name_email,this.final_direct_user_first_name,this.final_direct_user_last_name,phone_number,"direct_admin",organization_id
				,direct_user_id,image_url,create_ts,last_login_ts,blocked
				,final_direct_org_name,"direct", ParentID,"","", test );
	}	
	
	@Test
	@Parameters({  "csrAdminUserName", "csrAdminPassword" })
	public void getUserSession_MSPAdmin_WithValidToken(String AdminUserName, String AdminPassword) {
		test=rep.startTest("getUserSession_MSPAdmin_WithValidToken");
		test.assignAuthor("Eric Yang");
		this.csrAdminUserName=AdminUserName;
		this.csrAdminPassword=AdminPassword;
		spogServer.userLogin(this.final_msp_user_name_email, this.common_password);
		test.log(LogStatus.INFO,"Getting the token of the MSP_admin");
		String validToken=spogServer.getJWTToken();
		test.log(LogStatus.INFO, "Getting the logged in user for direct admin");
		//String userID=spogServer.GetLoggedinUser_UserID();
		//userID=direct_user_id;
		Response user_response=spogServer.getUserByID(msp_user_id, test);
		//spogServer.GetOrganizationParentID(response)
		String ParentID=this.csr_admin_org_ID;
		

		create_ts=user_response.then().extract().path("data." + "create_ts").toString().toLowerCase();
		last_login_ts=user_response.then().extract().path("data." + "last_login_ts").toString().toLowerCase();
		blocked=user_response.then().extract().path("data." + "blocked").toString().toLowerCase();
		image_url=user_response.then().extract().path("data." + "image_url").toString().toLowerCase();
		organization_id=user_response.then().extract().path("data." + "organization_id").toString().toLowerCase();
		
		Response response =spogServer.getUserSessionWithCheck(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		spogServer.checkUserSession(response,SpogConstants.SUCCESS_GET_PUT_DELETE, this.final_msp_user_name_email,this.final_msp_user_first_name,this.final_msp_user_last_name,phone_number,"MSP_admin",organization_id
				,msp_user_id,image_url,create_ts,last_login_ts,blocked
				,final_msp_org_name,"MSP", ParentID,"","", test );
	}	

	@Test
	@Parameters({  "csrAdminUserName", "csrAdminPassword" })
	public void getUserSession_account_user_WithValidToken(String AdminUserName, String AdminPassword) {
		test=rep.startTest("getUserSession_acount_user_WithValidToken");
		test.assignAuthor("Eric Yang");
		this.csrAdminUserName=AdminUserName;
		this.csrAdminPassword=AdminPassword;
		spogServer.userLogin(this.account_user_email, this.common_password);
		test.log(LogStatus.INFO,"Getting the token of the account_user");
		String validToken=spogServer.getJWTToken();
		test.log(LogStatus.INFO, "Getting the logged in user for account user");
		//String userID=spogServer.GetLoggedinUser_UserID();
		//userID=direct_user_id;
		Response user_response=spogServer.getUserByID(account_user_id, test);
		String ParentID=this.msp_org_id;
		

		create_ts=user_response.then().extract().path("data." + "create_ts").toString().toLowerCase();
		last_login_ts=user_response.then().extract().path("data." + "last_login_ts").toString().toLowerCase();
		blocked=user_response.then().extract().path("data." + "blocked").toString().toLowerCase();
		image_url=user_response.then().extract().path("data." + "image_url").toString().toLowerCase();
		organization_id=user_response.then().extract().path("data." + "organization_id").toString().toLowerCase();
		
		Response response =spogServer.getUserSessionWithCheck(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		spogServer.checkUserSession(response,SpogConstants.SUCCESS_GET_PUT_DELETE, this.account_user_email,this.account_user_first_name,this.account_user_first_name,phone_number,"direct_admin",this.account_id
				,account_user_id,image_url,create_ts,last_login_ts,blocked
				,account_name,"msp_child", ParentID, "","",test );
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
			String csr_user_validToken = spogServer.getJWTToken();
			test.log(LogStatus.INFO,"delete direct site");
			
			spogServer.errorHandle.printInfoMessageInDebugFile("delete direct site");
			SiteTestHelper.deleteSite(direct_site_id, csr_user_validToken);
			spogServer.errorHandle.printInfoMessageInDebugFile("delete another direct site");
			SiteTestHelper.deleteSite(another_direct_site_id, csr_user_validToken);
					
			test.log(LogStatus.INFO,"delete direct org");
			spogServer.errorHandle.printInfoMessageInDebugFile("delete direct organization");
			spogServer.DeleteOrganizationWithCheck(direct_org_id, test);
			
			spogServer.errorHandle.printInfoMessageInDebugFile("delete account site");
			SiteTestHelper.deleteSite(account_site_id, csr_user_validToken);
			spogServer.errorHandle.printInfoMessageInDebugFile("delete another account site");
			SiteTestHelper.deleteSite(another_account_site_id, csr_user_validToken);
			
			spogServer.errorHandle.printInfoMessageInDebugFile("delete msp site");
			SiteTestHelper.deleteSite(msp_site_id, csr_user_validToken);
			spogServer.errorHandle.printInfoMessageInDebugFile("delete another msp site");
			SiteTestHelper.deleteSite(another_msp_site_id, csr_user_validToken);
			
			
			test.log(LogStatus.INFO,"delete msp org");
			spogServer.errorHandle.printInfoMessageInDebugFile("delete msp organization");
			spogServer.DeleteOrganizationWithCheck(msp_org_id, test);
			

			
		    test.log(LogStatus.INFO, "The total test cases passed are "+count1.getpassedcount());
		    test.log(LogStatus.INFO, "the total test cases failed are "+count1.getfailedcount());
		    rep.flush();
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
	
}
