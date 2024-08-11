package api.users;

// API function is changed , msp account admin get API return 0, msp admin return the value which does not include its sub org users
//API function is changed again, MSP admin can see its sub-org (account) users amount now , 
// if user want to get amount not including sub-org, user need add ?orgid=<orgid>  user2018-7-26
import org.testng.annotations.Test;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.SPOGUserrolesServer;
import InvokerServer.UserSpogServer;
import InvokerServer.GatewayServer;
import InvokerServer.Org4SPOGServer;
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

public class GetUsersAmountTest extends base.prepare.PrepareOrgInfo{
	  @Parameters({ "pmfKey"})
	public GetUsersAmountTest(String pmfKey) {
		super(pmfKey);
		// TODO Auto-generated constructor stub
	}

	private String  org_model_prefix=this.getClass().getSimpleName();
	  private SPOGServer spogServer;
	  private UserSpogServer userSpogServer;// use for assign account user to account admin
	  //private SPOGUserrolesServer spogUserrolesServer;
	  private GatewayServer gatewayServer;
	  private String csrAdminUserName;
	  private String csrAdminPassword;
	  //private ExtentReports rep;
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
	  
	  private String prefix_direct = "SPOG_QA_yaner01_BQ_direct";
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


	  
	  private String prefix_msp = "SPOG_QA_yaner01_BQ_msp";
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
		
	  private String prefix_csr = "SPOG_QA_yaner01_BQ_csr";
	  private String csr_user_name = prefix_csr + "_admin";
	  private String csr_user_name_email = prefix_csr + "_admin" + postfix_email;
	  private String csr_user_first_name = csr_user_name + "_first_name";
	  private String csr_user_last_name = csr_user_name + "_last_name";

	  private String msp_account_admin_name = prefix_msp + "_account_admin";
	  private String msp_account_admin_name_email = prefix_msp + "_account_admin" + postfix_email;
	  private String msp_account_admin_first_name = msp_account_admin_name + "_first_name";
	  private String msp_account_admin_last_name = msp_account_admin_name + "_last_name";
	  private String msp_account_admin_id =null;	  
	  private String final_msp_account_admin_email=null;	  
	  
	  //this is for update portal, each testng class is taken as BQ set
	 // private SQLServerDb bqdb1;
	 // public int Nooftest;
	 // private long creationTime;
	  //private String BQName=null;
	  //private String runningMachine;
	 // private testcasescount count1;
	 // private String buildVersion;
		
	  @BeforeClass
	  @Parameters({ "baseURI", "port", "logFolder", "csrAdminUserName", "csrAdminPassword", "buildVersion"})
	  public void beforeClass(String baseURI, String port, String logFolder, String adminUserName, String adminPassword ,  String buildVersion) throws UnknownHostException {
		
		  	spogServer = new SPOGServer(baseURI, port);
		  	userSpogServer =new UserSpogServer(baseURI, port);
		  	//spogUserrolesServer= new SPOGUserrolesServer(baseURI, port);
		  	gatewayServer =new GatewayServer(baseURI,port);
		  	rep = ExtentManager.getInstance("GetUsersAmountTest",logFolder);
		  	this.csrAdminUserName = adminUserName;
		  	this.csrAdminPassword = adminPassword;

		    test = rep.startTest("beforeClass");
			spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
			//*******************create direct org,user,site**********************/
			String prefix = RandomStringUtils.randomAlphanumeric(8);
			test.log(LogStatus.INFO,"create a direct org");
			final_direct_org_name=prefix+direct_org_name;
			direct_org_id = spogServer.CreateOrganizationWithCheck(prefix+direct_org_name+org_model_prefix , SpogConstants.DIRECT_ORG, null, common_password, null, null, test);
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
			msp_org_id = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name+org_model_prefix , SpogConstants.MSP_ORG, null, common_password, null, null, test);
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
			
//			test.log(LogStatus.INFO,"Creating a site For msp org");
//			msp_site_id = gatewayServer.createsite_register_login(msp_org_id, msp_user_validToken, msp_user_id, "yaner01", "1.0.0", spogServer, test);
//			String msp_site_token=gatewayServer.getJWTToken();
//			test.log(LogStatus.INFO, "The site token is "+ msp_site_token);
			
			siteName= spogServer.getRandomSiteName("TestCreate");
			test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
			sitetype=siteType.gateway.toString();
			test.log(LogStatus.INFO,"The siteType :"+sitetype);
			
//			test.log(LogStatus.INFO,"Creating another site For msp org");
//			another_msp_site_id = gatewayServer.createsite_register_login(msp_org_id, msp_user_validToken, msp_user_id, "yaner011", "1.0.0", spogServer, test);
//			String another_msp_site_token=gatewayServer.getJWTToken();
//			test.log(LogStatus.INFO, "The site token is "+ another_msp_site_token);
			
			//create MSP account admin   #####################
			test.log(LogStatus.INFO,"Creating a MSP account admin For account org");
			final_msp_account_admin_email = prefix + "account_admin"+msp_user_name_email;
			//msp_account_admin_id = spogServer.createUserAndCheck(final_msp_account_admin_email, common_password, prefix + msp_user_first_name, prefix + msp_user_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, account_id, test);
			this.msp_account_admin_id=  spogServer.createUserAndCheck(final_msp_account_admin_email, this.common_password, "Eric", "Yang", "msp_account_admin", msp_org_id, test);
			spogServer.userLogin(final_msp_account_admin_email, this.common_password);
			msp_account_admin_first_name=prefix + msp_user_first_name;
			msp_account_admin_last_name=prefix + msp_user_last_name;
			
			test.log(LogStatus.INFO,"Getting the JWTToken for the account admin");
			String account_admin_validToken = spogServer.getJWTToken();
			test.log(LogStatus.INFO,"The token is :"+ account_admin_validToken );			
			//########################################
			  //create msp account admin
			  System.out.println("msp account admin user");
		
			
			//login as MSP admin again
				spogServer.userLogin(final_msp_user_name_email, common_password);
				String token=spogServer.getJWTToken();// use for assign account to account admin

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
			String another_account_user_id = spogServer.createUserAndCheck("2"+account_user_email, common_password, prefix + msp_user_first_name, prefix + msp_user_last_name, SpogConstants.DIRECT_ADMIN, account_id, test);
			
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
			
			// assign account user to msp account admin
			
			Response response = userSpogServer.assignMspAccountAdmins(msp_org_id, account_id, this.msp_account_admin_id, token);
			
			
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
		    
			prepare(baseURI, port, logFolder, this.csrAdminUserName,  this.csrAdminPassword, this.getClass().getSimpleName() );
	  }	
	  /**
	   * @author Eric.Yang
	   * @testcase:
GET users/amount
1. csr admin GET users/amount get all users number in its org and sub org
2. direct admin GET users/amount get all users number in its org and sub org
3. msp admin GET users/amount get all users number in its org and sub org
4. msp account admin GET users/amount get all users number in its org and sub org
5. msp account user GET users/amount get all users number in its org and sub org
6. add or delete users, direct admin GET users/amount get all users new number in its org and sub org
7. add or delete users, msp admin GET users/amount get all users new number in its org and sub org
8. add or delete users, msp account admin GET users/amount get all users new number in its org and sub org
9. add or delete users, msp account user GET users/amount get all users new number in its org and sub org
10. count not GET users/amount without token, report401
11. could not GET users/amount with invalid token
* 
*/
	  
	  //
	  /**
	   * @author Eric.Yang
	   * @testcase:
	   * root MSP 2 tier cases
	   * 	public String prefix_root_msp ;
	public String root_msp_org_name;
	public String root_msp_org_id;
	public String root_msp_user_id;
	public String root_msp_user_name_email;
	public String final_root_msp_user_name_email;
	public String final_root_msp_direct_org_user_email,final_root_msp_direct_org1_user_email;
	public String root_msp_direct_org_user_id,root_msp_direct_org1_user_id;
	public String final_root_msp_account_admin_user_name_email;
	public String root_msp_account_admin_user_id;	
	public String final_root_msp_user_name_email;
		public String final_root_msp_account_admin_user_name_email;
GET users/amount
1. root msp  GET users/amount get all users number only in its org and its direct org,
2. sub msp1  GET users/amount get all users number only in its org and its direct1 and direct2 org,
3. sub msp2  GET users/amount get all users number only in its org and its direct1 and direct2 org,
4. direct1  GET users/amount get all users number only in its org ,
*/	  
		@Test
		@Parameters({  "csrAdminUserName", "csrAdminPassword" })
		public void rootMSP_userGetUsersAmountWithValidToken(String AdminUserName, String AdminPassword) {
			test=rep.startTest("rootMSP_userGetUsersAmountWithValidToken");
			test.assignAuthor("Eric Yang");
			//this.rootMSPUserName=AdminUserName;
			//this.rootMSPPassword=AdminPassword;
			spogServer.userLogin(this.final_root_msp_user_name_email, this.common_password);
			test.log(LogStatus.INFO,"Getting the token of the msp account_admin");
			String validToken=spogServer.getJWTToken();
			int expected_num=4;
			test.log(LogStatus.INFO, "msp user get amount for users amount");
			userSpogServer.getUsersAmountWithCheck(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, expected_num, test);
			}	  
		@Test
		@Parameters({  "csrAdminUserName", "csrAdminPassword" })
		public void rootMSP_account_adminGetUsersAmountWithValidToken(String AdminUserName, String AdminPassword) {
			test=rep.startTest("rootMSP_account_adminGetUsersAmountWithValidToken");
			test.assignAuthor("Eric Yang");
			//this.rootMSPUserName=AdminUserName;
			//this.rootMSPPassword=AdminPassword;
			spogServer.userLogin(this.final_root_msp_account_admin_user_name_email, this.common_password);
			test.log(LogStatus.INFO,"Getting the token of the direct admin");
			String validToken=spogServer.getJWTToken();
			int expected_num=0;
			test.log(LogStatus.INFO, "direct admin get amount for users amount");
			userSpogServer.getUsersAmountWithCheck(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, expected_num, test);
			}		  
		@Test
		@Parameters({  "csrAdminUserName", "csrAdminPassword" })
		public void rootMSP_direct_org1_userGetUsersAmountWithValidToken(String AdminUserName, String AdminPassword) {
			test=rep.startTest("rootMSP_direct_org1_userGetUsersAmountWithValidToken");
			test.assignAuthor("Eric Yang");
			//this.rootMSPUserName=AdminUserName;
			//this.rootMSPPassword=AdminPassword;
			spogServer.userLogin(this.final_root_msp_direct_org1_user_email, this.common_password);
			test.log(LogStatus.INFO,"Getting the token of root_msp_direct_org1_user");
			String validToken=spogServer.getJWTToken();
			int expected_num=1;
			test.log(LogStatus.INFO, "root_msp_direct_org1_user get amount for users amount");
			userSpogServer.getUsersAmountWithCheck(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, expected_num, test);
			}
		@Test
		@Parameters({  "csrAdminUserName", "csrAdminPassword" })
		public void sub_msp1__userGetUsersAmountWithValidToken(String AdminUserName, String AdminPassword) {
			test=rep.startTest("sub_msp1__userGetUsersAmountWithValidToken");
			test.assignAuthor("Eric Yang");
			//this.rootMSPUserName=AdminUserName;
			//this.rootMSPPassword=AdminPassword;
			spogServer.userLogin(this.final_sub_msp1_user_name_email, this.common_password);
			test.log(LogStatus.INFO,"Getting the token of final_sub_msp1_user_name_email");
			String validToken=spogServer.getJWTToken();
			int expected_num=5;
			test.log(LogStatus.INFO, "final_sub_msp1_user get amount for users amount");
			userSpogServer.getUsersAmountWithCheck(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, expected_num, test);
			}
		@Test
		@Parameters({  "csrAdminUserName", "csrAdminPassword" })
		public void sub_msp1_account_userGetUsersAmountWithValidToken(String AdminUserName, String AdminPassword) {
			test=rep.startTest("sub_msp1_account_userGetUsersAmountWithValidToken");
			test.assignAuthor("Eric Yang");
			//this.rootMSPUserName=AdminUserName;
			//this.rootMSPPassword=AdminPassword;
			spogServer.userLogin(this.final_sub_msp1_msp_account_user_name_email, this.common_password);
			test.log(LogStatus.INFO,"Getting the token of final_sub_msp1_msp_account_user");
			String validToken=spogServer.getJWTToken();
			int expected_num=0;
			test.log(LogStatus.INFO, "final_sub_msp1_user get amount for users amount");
			userSpogServer.getUsersAmountWithCheck(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, expected_num, test);
			}		
		@Test
		@Parameters({  "csrAdminUserName", "csrAdminPassword" })
		public void sub_msp1_account1_userGetUsersAmountWithValidToken(String AdminUserName, String AdminPassword) {
			test=rep.startTest("sub_msp1_account1_userGetUsersAmountWithValidToken");
			test.assignAuthor("Eric Yang");
			//this.rootMSPUserName=AdminUserName;
			//this.rootMSPPassword=AdminPassword;
			spogServer.userLogin(this.final_sub_msp1_account1_user_email, this.common_password);
			test.log(LogStatus.INFO,"Getting the token of final_sub_msp1_msp_account1_user");
			String validToken=spogServer.getJWTToken();
			int expected_num=1;
			test.log(LogStatus.INFO, "final_sub_msp1_user get amount for users amount");
			userSpogServer.getUsersAmountWithCheck(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, expected_num, test);
			}		
		
		
		
		
		
		
	  ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	  	  
	  //2. direct admin GET users/amount get all users number in its org and sub org
		@Test
		@Parameters({  "csrAdminUserName", "csrAdminPassword" })
		public void directAdminGetUsersAmountWithValidToken(String AdminUserName, String AdminPassword) {
			test=rep.startTest("directAdminGetUsersAmountWithValidToken");
			test.assignAuthor("Eric Yang");
			this.csrAdminUserName=AdminUserName;
			this.csrAdminPassword=AdminPassword;
			spogServer.userLogin(this.final_direct_user_name_email, this.common_password);
			test.log(LogStatus.INFO,"Getting the token of the direct admin");
			String validToken=spogServer.getJWTToken();
			int expected_num=1;
			test.log(LogStatus.INFO, "direct admin get amount for users amount");
			userSpogServer.getUsersAmountWithCheck(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, expected_num, test);
			}
		//3. msp admin GET users/amount get all users number in its org and sub org
		//expected changes, MSP admin can see its sub-org (account) users amount now , 2018-7-26
		@Test
		@Parameters({  "csrAdminUserName", "csrAdminPassword" })
		public void mspAdminGetUsersAmountWithValidToken(String AdminUserName, String AdminPassword) {
			test=rep.startTest("mspAdminGetUsersAmountWithValidToken");
			test.assignAuthor("Eric Yang");
			this.csrAdminUserName=AdminUserName;
			this.csrAdminPassword=AdminPassword;
			spogServer.userLogin(this.final_msp_user_name_email, this.common_password);
			test.log(LogStatus.INFO,"Getting the token of the msp admin");
			String validToken=spogServer.getJWTToken();
			int expected_num=4;
			test.log(LogStatus.INFO, "msp admin get amount for users amount");
			
			userSpogServer.getUsersAmountWithCheck(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, expected_num, test);
			}		
		//4. msp account admin GET users/amount get all users number in its org and sub org
		@Test
		@Parameters({  "csrAdminUserName", "csrAdminPassword" })
		public void mspAccountAdminGetUsersAmountWithValidToken(String AdminUserName, String AdminPassword) {
			test=rep.startTest("mspAccountAdminGetUsersAmountWithValidToken");
			test.assignAuthor("Eric Yang");
			this.csrAdminUserName=AdminUserName;
			this.csrAdminPassword=AdminPassword;
			spogServer.userLogin(this.final_msp_account_admin_email, this.common_password);
			test.log(LogStatus.INFO,"Getting the token of the msp account admin");
			String validToken=spogServer.getJWTToken();
			int expected_num= 0;
			test.log(LogStatus.INFO, "msp account admin get amount for users amount");
			userSpogServer.getUsersAmountWithCheck(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, expected_num, test);
			}		
		//5. msp account user GET users/amount get all users number in its org and sub org
		@Test
		@Parameters({  "csrAdminUserName", "csrAdminPassword" })
		public void mspAccountUserGetUsersAmountWithValidToken(String AdminUserName, String AdminPassword) {
			test=rep.startTest("mspAccountUserGetUsersAmountWithValidToken");
			test.assignAuthor("Eric Yang");
			this.csrAdminUserName=AdminUserName;
			this.csrAdminPassword=AdminPassword;
			spogServer.userLogin(this.account_user_email, this.common_password);
			test.log(LogStatus.INFO,"Getting the token of the direct admin");
			String validToken=spogServer.getJWTToken();
			int expected_num=2;
			test.log(LogStatus.INFO, "direct admin get amount for users amount");
			userSpogServer.getUsersAmountWithCheck(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, expected_num, test);
			}		
		//6. add or delete users, direct admin GET users/amount get all users new number in its org and sub org
		@Test
		@Parameters({  "csrAdminUserName", "csrAdminPassword" })
		public void directAdminGetUsersAmountWithValidTokenAfteraddOrDeleteUser(String AdminUserName, String AdminPassword) {
			test=rep.startTest("directAdminGetUsersAmountWithValidTokenAfteraddOrDeleteUser");
			test.assignAuthor("Eric Yang");
			this.csrAdminUserName=AdminUserName;
			this.csrAdminPassword=AdminPassword;
			spogServer.userLogin(this.final_direct_user_name_email, this.common_password);
			test.log(LogStatus.INFO,"Getting the token of the direct admin");
			String validToken=spogServer.getJWTToken();
			int expected_num=1;
			//add user in direct org
			test.log(LogStatus.INFO,"create a admin under direct org");
			String another_direct_user_id = spogServer.createUserAndCheck("2"+final_direct_user_name_email, common_password, direct_user_first_name,  direct_user_last_name, SpogConstants.DIRECT_ADMIN, direct_org_id, test);
			spogServer.userLogin("2"+final_direct_user_name_email, common_password);
			spogServer.userLogin(this.final_direct_user_name_email, this.common_password);
			test.log(LogStatus.INFO, "direct admin get amount for users amount");
			userSpogServer.getUsersAmountWithCheck(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, expected_num+1, test);
			//delete user in direct org
			spogServer.CheckDeleteUserByIdStatus(another_direct_user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
			test.log(LogStatus.INFO, "direct admin get amount for users amount");
			userSpogServer.getUsersAmountWithCheck(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, expected_num, test);
			}		
		//7. add or delete users, msp admin GET users/amount get all users new number in its org and sub org		
		@Test
		@Parameters({  "csrAdminUserName", "csrAdminPassword" })
		public void mspAdminGetUsersAmountWithValidTokenAfterAddOrDeleteUser(String AdminUserName, String AdminPassword) {
			test=rep.startTest("mspAdminGetUsersAmountWithValidTokenAfterAddOrDeleteUser");
			test.assignAuthor("Eric Yang");
			this.csrAdminUserName=AdminUserName;
			this.csrAdminPassword=AdminPassword;
			spogServer.userLogin(this.final_msp_user_name_email, this.common_password);
			test.log(LogStatus.INFO,"Getting the token of the msp admin");
			String validToken=spogServer.getJWTToken();
			int expected_num=4;
			test.log(LogStatus.INFO, "msp admin get amount for users amount");
			//add msp admin
			test.log(LogStatus.INFO,"create a admin under msp org");
			String another_msp_user_id = spogServer.createUserAndCheck("2"+final_msp_user_name_email, common_password,  msp_user_first_name, msp_user_last_name, SpogConstants.MSP_ADMIN, msp_org_id, test);
			spogServer.userLogin("2"+final_msp_user_name_email, common_password);
			spogServer.userLogin(this.final_msp_user_name_email, this.common_password);			
			userSpogServer.getUsersAmountWithCheck(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, expected_num+1, test);
			//delete user in direct org
			spogServer.CheckDeleteUserByIdStatus(another_msp_user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
			test.log(LogStatus.INFO, "msp admin get amount for users amount");
			userSpogServer.getUsersAmountWithCheck(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, expected_num, test);
			
			}			
		//8. add or delete users, msp account admin GET users/amount get all users new number in its org and sub org
		@Test
		@Parameters({  "csrAdminUserName", "csrAdminPassword" })
		public void mspAccountAdminGetUsersAmountWithValidTokenAfterAddOrDeleteUser(String AdminUserName, String AdminPassword) {
			test=rep.startTest("mspAccountAdminGetUsersAmountWithValidTokenAfterAddOrDeleteUser");
			test.assignAuthor("Eric Yang");
			this.csrAdminUserName=AdminUserName;
			this.csrAdminPassword=AdminPassword;
			spogServer.userLogin(this.final_msp_account_admin_email, this.common_password);
			test.log(LogStatus.INFO,"Getting the token of the msp account admin");
			String validToken=spogServer.getJWTToken();
			int expected_num=0;// original 3
			//add user
			spogServer.userLogin(this.final_msp_user_name_email, this.common_password);
			String another3_account_user_id = spogServer.createUserAndCheck("3"+account_user_email, common_password, msp_user_first_name, msp_user_last_name, SpogConstants.DIRECT_ADMIN, account_id, test);
			spogServer.userLogin(account_user_email, common_password);
			spogServer.userLogin(this.final_msp_user_name_email, this.common_password);
			test.log(LogStatus.INFO, "msp account admin get amount for users amount");
			userSpogServer.getUsersAmountWithCheck(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, expected_num, test);
			
			//delete user
			spogServer.CheckDeleteUserByIdStatus(another3_account_user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
			userSpogServer.getUsersAmountWithCheck(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, expected_num, test);
			
			}		
		//9. add or delete users, msp account user GET users/amount get all users new number in its org and sub org
		@Test
		@Parameters({  "csrAdminUserName", "csrAdminPassword" })
		public void mspAccountUserGetUsersAmountWithValidTokenAfterAddOrDeleteUser(String AdminUserName, String AdminPassword) {
			test=rep.startTest("mspAccountUserGetUsersAmountWithValidTokenAfterAddOrDeleteUser");
			test.assignAuthor("Eric Yang");
			this.csrAdminUserName=AdminUserName;
			this.csrAdminPassword=AdminPassword;
			spogServer.userLogin(this.account_user_email, this.common_password);
			test.log(LogStatus.INFO,"Getting the token of the direct admin");
			String validToken=spogServer.getJWTToken();
			int expected_num=2;
			spogServer.userLogin(this.final_msp_user_name_email, this.common_password);
			//add user
			String another4_account_user_id = spogServer.createUserAndCheck("4"+account_user_email, common_password, msp_user_first_name, msp_user_last_name, SpogConstants.DIRECT_ADMIN, account_id, test);
			test.log(LogStatus.INFO, "direct admin get amount for users amount");
			userSpogServer.getUsersAmountWithCheck(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, expected_num+1, test);
			//delete user
			spogServer.CheckDeleteUserByIdStatus(another4_account_user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
			userSpogServer.getUsersAmountWithCheck(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, expected_num, test);
			}
		//10. count not GET users/amount without token, report401
		@Test
		@Parameters({  "csrAdminUserName", "csrAdminPassword" })
		public void MSPAccountAdminGetUsersAmountWithMissToken(String AdminUserName, String AdminPassword) {
			test=rep.startTest("MSPAccountAdminGetUsersAmountWithMissToken");
			test.assignAuthor("Eric Yang");
			this.csrAdminUserName=AdminUserName;
			this.csrAdminPassword=AdminPassword;
			spogServer.userLogin(this.final_msp_account_admin_email, this.common_password);
			test.log(LogStatus.INFO,"Getting the token of the MSP account admin");
			String validToken=spogServer.getJWTToken();
			int expected_num=14;
			test.log(LogStatus.INFO, "MSP account admin get amount for users ammount");
			userSpogServer.getUsersAmountWithCheck("", SpogConstants.NOT_LOGGED_IN, expected_num, test);
			}		
//11. could not GET users/amount with invalid token
		@Test
		@Parameters({  "csrAdminUserName", "csrAdminPassword" })
		public void MSPAccountAdminGetUsersAmountWithInvalidToken(String AdminUserName, String AdminPassword) {
			test=rep.startTest("MSPAccountAdminGetUsersAmountWithInvalidToken");
			test.assignAuthor("Eric Yang");
			this.csrAdminUserName=AdminUserName;
			this.csrAdminPassword=AdminPassword;
			spogServer.userLogin(this.final_msp_account_admin_email, this.common_password);
			test.log(LogStatus.INFO,"Getting the token of the MSP account admin");
			String validToken=spogServer.getJWTToken()+"invalid";
			int expected_num=14;
			test.log(LogStatus.INFO, "MSP account admin get amount for users ammount");
			userSpogServer.getUsersAmountWithCheck(validToken, SpogConstants.NOT_LOGGED_IN, expected_num, test);
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
