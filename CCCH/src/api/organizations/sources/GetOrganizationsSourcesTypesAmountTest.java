package api.organizations.sources;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

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

import Constants.ConnectionStatus;
import Constants.ErrorCode;
import Constants.OSMajor;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import InvokerServer.GatewayServer;
import InvokerServer.Org4SPOGServer;
import InvokerServer.SPOGServer;
import InvokerServer.Source4SPOGServer;
import InvokerServer.UserSpogServer;
import InvokerServer.GatewayServer.siteType;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import invoker.SiteTestHelper;
import io.restassured.response.Response;

public class GetOrganizationsSourcesTypesAmountTest  extends base.prepare.PrepareOrgInfo{
	
	@Parameters({ "pmfKey"})
	  public GetOrganizationsSourcesTypesAmountTest(String pmfKey) {
		super(pmfKey);
		// TODO Auto-generated constructor stub
	}



	private SPOGServer spogServer;
	  private UserSpogServer userSpogServer;
	  private Source4SPOGServer source4SpogServer;
	  private GatewayServer gatewayServer;
	  private String csrAdminUserName;
	  private String csrAdminPassword;
	  private String csrReadOnlyAdminUserName;
	  private String csrReadOnlyAdminPassword;
	  private ExtentTest test;

	  private String postfix_email = "@arcserve.com";
	  private String common_password = "Welcome*02";
	  
	  private String prefix_direct = "spogqa_shuo_direct";
	  private String direct_org_name = prefix_direct + "_org";
	  private String direct_user_name = prefix_direct + "_admin";
	  private String direct_user_name_email  =direct_user_name + postfix_email;
	  private String direct_user_first_name = direct_user_name + "_first_name";
	  private String direct_user_last_name = direct_user_name + "_last_name";
	  private String direct_org_id;
	  private String direct_user_id;
	  private String final_direct_user_name_email;
	  private String direct_site_id;
	  private String another_direct_site_id;

	  private String prefix_msp = "spogqa_shuo_msp";
	  private String msp_org_name = prefix_msp + "_org";
	  private String msp_user_name = prefix_msp + "_admin";
	  private String msp_user_name_email = prefix_msp + "_admin" + postfix_email;
	  private String msp_user_first_name = msp_user_name + "_first_name";
	  private String msp_user_last_name = msp_user_name + "_last_name";
	  private String msp_user_id;
	  private String final_msp_account_admin_email;
	 // private String msp_site_id;
	//  private String another_msp_site_id;
	  private String msp_org_id=null;
	  private String final_msp_user_name_email=null;	
	  
	  private String prefix_msp_account = "spogqa_shuo_msp_account";
	  private String msp_account_admin_name = prefix_msp_account + "_admin";
	  private String msp_account_admin_email =msp_account_admin_name + postfix_email;
	  private String msp_account_admin_first_name = msp_account_admin_name + "_first_name";
	  private String msp_account_admin_last_name = msp_account_admin_name + "_last_name";
	  private String msp_account_admin_id;
	  
	  private String prefix_account = "spogqa_shuo_account";
	  private String account_user_name = prefix_account + "_admin";
	  private String account_user_name_email = prefix_account + "_admin" + postfix_email;
	  private String account_user_first_name = account_user_name + "_first_name";
	  private String account_user_last_name = account_user_name + "_last_name";
	  private String account_id;
	  private String account_user_email;
	  private String account_user_id;
	  private String account_site_id;
	  private String another_account_site_id;
		private String another_account_id;
	  
	  private HashMap<String, String> siteTokenMap;
	  private ArrayList<String> sourceIdArray;
	  
	  //this is for update portal, each testng class is taken as BQ set
	 /* private SQLServerDb bqdb1;
	  public int Nooftest;
	  private long creationTime;
	  private String BQName=null;
	  private String runningMachine;
	  private testcasescount count1;
	  private String buildVersion;
	  private ExtentReports rep;
	  */
	private List<HashMap<String, Object>> expectedDirectOrgResult;
	private List<HashMap<String, Object>> expectedMspOrgResult;
	private List<HashMap<String, Object>> expectedAccountResult;
	private Org4SPOGServer org4SpogServer;
	private String  org_model_prefix=this.getClass().getSimpleName();
	private List<HashMap<String, Object>> expectedRootMSPDirectOrgResult;
	private List<HashMap<String, Object>> expectedSubMSP1Account1Result;

	

	
	

	  @BeforeClass
	  @Parameters({ "baseURI", "port", "logFolder", "csrAdminUserName", "csrAdminPassword", "buildVersion", "csrReadOnlyAdminUserName","csrReadOnlyAdminPassword"})
	  public void beforeClass(String baseURI, String port, String logFolder, String adminUserName, String adminPassword ,  String buildVersion, String csrROAdminUserName, String csrROPwd ) throws UnknownHostException {
		
		 	  spogServer = new SPOGServer(baseURI, port);
		 	  userSpogServer = new UserSpogServer(baseURI, port);
		 	  source4SpogServer = new Source4SPOGServer(baseURI, port);
		 		org4SpogServer = new Org4SPOGServer(baseURI, port);
		 	  gatewayServer =new GatewayServer(baseURI,port);
			  rep = ExtentManager.getInstance("GetOrganizationsSourcesTypesAmountTest",logFolder);
			  this.csrAdminUserName = adminUserName;
			  this.csrAdminPassword = adminPassword;
			  this.csrReadOnlyAdminUserName = csrROAdminUserName;
			  this.csrReadOnlyAdminPassword = csrROPwd;
			  test = rep.startTest("beforeClass");
			  spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
			
			  siteTokenMap = new HashMap<String,String>();
			  sourceIdArray= new  ArrayList<String>();
			//*******************create direct org,user,**********************/
			
			String prefix = RandomStringUtils.randomAlphanumeric(8);
			test.log(LogStatus.INFO,"create a direct org");
			direct_org_id = spogServer.CreateOrganizationWithCheck(prefix+direct_org_name +org_model_prefix, SpogConstants.DIRECT_ORG, null, null, null, null, test);
			final_direct_user_name_email = prefix + direct_user_name_email;
			
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
			direct_site_id = gatewayServer.createsite_register_login(direct_org_id, direct_user_validToken, direct_user_id, "shuo", "1.0.0", spogServer, test);
			String direct_site_token=gatewayServer.getJWTToken();
			test.log(LogStatus.INFO, "The site token is "+direct_site_token);
			siteTokenMap.put(direct_site_id, direct_site_token);
			
			siteName= spogServer.getRandomSiteName("TestCreate");
			test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
			sitetype=siteType.gateway.toString();
			test.log(LogStatus.INFO,"The siteType :"+sitetype);
			
			test.log(LogStatus.INFO,"Creating another site For direct org");
			another_direct_site_id = gatewayServer.createsite_register_login(direct_org_id, direct_user_validToken, direct_user_id, "shuo1", "1.0.0", spogServer, test);
			String another_direct_site_token=gatewayServer.getJWTToken();
			test.log(LogStatus.INFO, "The site token is "+another_direct_site_token);
			siteTokenMap.put(another_direct_site_id, another_direct_site_token);
			
			List<HashMap<String, Object>> expectedResult1 = prepareSources4Org(final_direct_user_name_email, common_password, direct_site_id, direct_org_id);
			List<HashMap<String, Object>> expectedResult2 = prepareSources4Org(final_direct_user_name_email, common_password, another_direct_site_id, direct_org_id);
			expectedDirectOrgResult = getTotalAmount4Org(expectedResult1, expectedResult2);
			//************************create msp org,user,*************************************
			spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
			prefix = RandomStringUtils.randomAlphanumeric(8);
			test.log(LogStatus.INFO,"create a msp org");
			msp_org_id = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name+org_model_prefix , SpogConstants.MSP_ORG, null, null, null, null, test);
			final_msp_user_name_email = prefix + msp_user_name_email;
			
			test.log(LogStatus.INFO,"create a msp admin under msp org");
			msp_user_id = spogServer.createUserAndCheck(final_msp_user_name_email, common_password, prefix + msp_user_first_name, prefix + msp_user_last_name, SpogConstants.MSP_ADMIN, msp_org_id, test);
			
			test.log(LogStatus.INFO,"create a msp account admin under msp org");
			final_msp_account_admin_email = prefix + this.msp_account_admin_email;
			msp_account_admin_id = spogServer.createUserAndCheck(final_msp_account_admin_email, common_password, prefix + msp_account_admin_first_name, prefix + msp_account_admin_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, msp_org_id, test);
			
			spogServer.userLogin(final_msp_user_name_email, common_password);
			test.log(LogStatus.INFO,"Getting the JWTToken for the msp user");
			String msp_user_validToken = spogServer.getJWTToken();
			test.log(LogStatus.INFO,"The token is :"+ msp_user_validToken );
			
			/*siteName= spogServer.getRandomSiteName("TestCreate");
			test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
			sitetype=siteType.gateway.toString();
			test.log(LogStatus.INFO,"The siteType :"+sitetype);
			
			test.log(LogStatus.INFO,"Creating a site For msp org");
			msp_site_id = gatewayServer.createsite_register_login(msp_org_id, msp_user_validToken, msp_user_id, "shuo", "1.0.0", spogServer, test);
			String msp_site_token=gatewayServer.getJWTToken();
			test.log(LogStatus.INFO, "The site token is "+ msp_site_token);
			siteTokenMap.put(msp_site_id, msp_site_token);
			
			siteName= spogServer.getRandomSiteName("TestCreate");
			test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
			sitetype=siteType.gateway.toString();
			test.log(LogStatus.INFO,"The siteType :"+sitetype);
			
			test.log(LogStatus.INFO,"Creating another site For msp org");
			another_msp_site_id = gatewayServer.createsite_register_login(msp_org_id, msp_user_validToken, msp_user_id, "shuo1", "1.0.0", spogServer, test);
			String another_msp_site_token=gatewayServer.getJWTToken();
			test.log(LogStatus.INFO, "The site token is "+ another_msp_site_token);
			siteTokenMap.put(another_msp_site_id, another_msp_site_token);*/
		/*	
			 expectedResult1 = prepareSources4Org(final_msp_user_name_email, common_password, msp_site_id, msp_org_id);
			 expectedResult2 = prepareSources4Org(final_msp_user_name_email, common_password, another_msp_site_id, msp_org_id);
		     expectedMspOrgResult = getTotalAmount4Org(expectedResult1, expectedResult2);
			*/

			//create account, account user and site
			test.log(LogStatus.INFO,"Creating a account For msp org");
			account_id = spogServer.createAccountWithCheck(msp_org_id, "sub_" + prefix + msp_org_name, "", test);
			prefix = RandomStringUtils.randomAlphanumeric(8);
		
			test.log(LogStatus.INFO,"Creating a account user For account org");
			account_user_email = prefix + account_user_name_email;
			account_user_id = spogServer.createUserAndCheck(account_user_email, common_password, prefix + account_user_first_name, prefix + account_user_last_name, SpogConstants.DIRECT_ADMIN, account_id, test);
			spogServer.userLogin(account_user_email, common_password);
			
			test.log(LogStatus.INFO,"Getting the JWTToken for the account user");
			String account_user_validToken = spogServer.getJWTToken();
			test.log(LogStatus.INFO,"The token is :"+ account_user_validToken );
			
			siteName= spogServer.getRandomSiteName("TestCreate");
			test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
			sitetype=siteType.gateway.toString();
			test.log(LogStatus.INFO,"The siteType :"+sitetype);
			
			test.log(LogStatus.INFO,"Creating a site For account org");
			account_site_id = gatewayServer.createsite_register_login(account_id, account_user_validToken, account_user_id, "shuo", "1.0.0", spogServer, test);
			String account_site_token=gatewayServer.getJWTToken();
			test.log(LogStatus.INFO, "The site token is "+ account_site_token);
			siteTokenMap.put(account_site_id, account_site_token);
				
			siteName= spogServer.getRandomSiteName("TestCreate");
			test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
			sitetype=siteType.gateway.toString();
			test.log(LogStatus.INFO,"The siteType :"+sitetype);
			
			test.log(LogStatus.INFO,"Creating another site For account org");
			another_account_site_id = gatewayServer.createsite_register_login(account_id, account_user_validToken, account_user_id, "shuo1", "1.0.0", spogServer, test);
			String another_account_site_token=gatewayServer.getJWTToken();
			test.log(LogStatus.INFO, "The site token is "+ another_account_site_token);
			siteTokenMap.put(another_account_site_id, another_account_site_token);
			
			expectedResult1 =prepareSources4Org(account_user_email, common_password, account_site_id, account_id);
			expectedResult2 =prepareSources4Org(account_user_email, common_password, another_account_site_id, account_id);
			expectedAccountResult = getTotalAmount4Org(expectedResult1, expectedResult2);
			
		  	
			spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
			test.log(LogStatus.INFO,"assign account to msp account admin");
			String[] mspAccountAdmins = new String []{msp_account_admin_id};
			userSpogServer.assignMspAccountAdmins(msp_org_id, account_id, mspAccountAdmins , spogServer.getJWTToken()); 
			
			//create account, account user and site
			test.log(LogStatus.INFO,"Creating another account For msp org");
			prefix = RandomStringUtils.randomAlphanumeric(8);
			another_account_id = spogServer.createAccountWithCheck(msp_org_id, "sub_" + prefix + msp_org_name, "", test);
		
			 prepare(baseURI, port, logFolder, this.csrAdminUserName,  this.csrAdminPassword, this.getClass().getSimpleName() );
			 
			 siteTokenMap.put(root_msp_direct_org_site1_siteId, root_msp_direct_org_site1_token);
			 siteTokenMap.put(root_msp_direct_org_site2_siteId, root_msp_direct_org_site2_token);
			 expectedResult1= prepareSources4Org(this.final_root_msp_direct_org_user_email, common_password, this.root_msp_direct_org_site1_siteId, this.root_msp_direct_org_id);
			 expectedResult2=prepareSources4Org(this.final_root_msp_direct_org_user_email, common_password, this.root_msp_direct_org_site2_siteId, this.root_msp_direct_org_id);
			 expectedRootMSPDirectOrgResult = getTotalAmount4Org(expectedResult1, expectedResult2);
			 
			 siteTokenMap.put(sub_msp1_account1_site1_siteId, sub_msp1_account1_site1_token);
			 siteTokenMap.put(sub_msp1_account1_site2_siteId, sub_msp1_account1_site2_token);
			 expectedResult1= prepareSources4Org(this.final_sub_msp1_account1_user_email, common_password, this.sub_msp1_account1_site1_siteId, this.sub_msp1_account1_id);
			 expectedResult2=prepareSources4Org(this.final_sub_msp1_account1_user_email, common_password, this.sub_msp1_account1_site2_siteId, this.sub_msp1_account1_id);
			 expectedSubMSP1Account1Result = getTotalAmount4Org(expectedResult1, expectedResult2);
			 
			 
			 
			 
			 
			 
			 
			
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
		    
	  }
 
	@DataProvider(name = "orgInfo1")
	  public final Object[][] getOrgInfo1() {
	 	 return new Object[][]   {
	 		 {final_direct_user_name_email,common_password, direct_org_id, expectedDirectOrgResult,  SpogConstants.SUCCESS_GET_PUT_DELETE, null},
	 	
	 		 {account_user_email, common_password,account_id, expectedAccountResult,  SpogConstants.SUCCESS_GET_PUT_DELETE, null},
	 		 {final_msp_account_admin_email,common_password, account_id, expectedAccountResult,  SpogConstants.SUCCESS_GET_PUT_DELETE, null},

	 		 {final_msp_user_name_email,common_password, account_id, expectedAccountResult,  SpogConstants.SUCCESS_GET_PUT_DELETE, null},
	 		{csrAdminUserName, csrAdminPassword, direct_org_id, expectedDirectOrgResult,  SpogConstants.SUCCESS_GET_PUT_DELETE, null},
	 	
	 		{csrAdminUserName, csrAdminPassword, account_id, expectedAccountResult,  SpogConstants.SUCCESS_GET_PUT_DELETE, null},
	 		
	 		 {final_direct_user_name_email, common_password,msp_org_id, null,  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
	 		 {final_direct_user_name_email, common_password,account_id, null,  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
	 		{final_msp_user_name_email, common_password,direct_org_id, null,  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
	 		 {account_user_email, common_password,msp_org_id, null,  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
	 		 {account_user_email, common_password,direct_org_id, null,  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
	 		{final_msp_account_admin_email, common_password,direct_org_id, null,  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
	 		
	 		{final_msp_account_admin_email, common_password,another_account_id, null,  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
	 		
	 		 {final_direct_user_name_email,common_password, null, expectedDirectOrgResult,  SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.ELEMENT_NOT_UUID},
	 		 {final_direct_user_name_email,common_password, "", expectedDirectOrgResult,  SpogConstants.RESOURCE_NOT_EXIST, ErrorCode.RESOURCE_NOT_FOUND},
	 		 {final_direct_user_name_email,common_password, UUID.randomUUID().toString(), expectedDirectOrgResult,  SpogConstants.RESOURCE_NOT_EXIST, ErrorCode.CAN_NOT_FIND_ORG},
	 		 {this.csrReadOnlyAdminUserName,this.csrReadOnlyAdminPassword, direct_org_id, expectedDirectOrgResult,  SpogConstants.SUCCESS_GET_PUT_DELETE, null},
	 		 {this.csrReadOnlyAdminUserName,this.csrReadOnlyAdminPassword, account_id, expectedAccountResult,  SpogConstants.SUCCESS_GET_PUT_DELETE, null},
	 		 
	 		 {this.final_root_msp_user_name_email,common_password, this.root_msp_direct_org_id, expectedRootMSPDirectOrgResult,  SpogConstants.SUCCESS_GET_PUT_DELETE, null},	 		
	 		 {this.final_root_msp_user_name_email,common_password, this.sub_msp1_account1_id,   expectedSubMSP1Account1Result,  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
	 		 {this.final_root_msp_account_admin_user_name_email,common_password, this.root_msp_direct_org_id, expectedRootMSPDirectOrgResult,  SpogConstants.SUCCESS_GET_PUT_DELETE, null},	 		
	 		 {this.final_root_msp_account_admin_user_name_email,common_password, this.sub_msp1_account1_id,   expectedSubMSP1Account1Result,  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
	 		 {this.final_sub_msp1_user_name_email,common_password, this.sub_msp1_account1_id, expectedSubMSP1Account1Result,  SpogConstants.SUCCESS_GET_PUT_DELETE, null},	 		
	 		 {this.final_sub_msp1_user_name_email,common_password, this.root_msp_direct_org_id,   null,  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
	 		 {this.final_sub_msp1_user_name_email,common_password, this.sub_msp2_account1_id,   null,  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
	 		 {this.final_sub_msp1_msp_account_user_name_email,common_password, this.sub_msp1_account1_id, expectedSubMSP1Account1Result,  SpogConstants.SUCCESS_GET_PUT_DELETE, null},	 		
	 		 {this.final_sub_msp1_msp_account_user_name_email,common_password, this.root_msp_direct_org_id,   null,  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
	 		 {this.final_sub_msp1_msp_account_user_name_email,common_password, this.sub_msp2_account1_id,   null,  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
	 		 {this.final_sub_msp1_msp_account_user_name_email,common_password, this.sub_msp1_account2_id,   null,  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
	 		 {this.final_sub_msp1_account1_user_email,common_password, this.sub_msp1_account1_id, expectedSubMSP1Account1Result,  SpogConstants.SUCCESS_GET_PUT_DELETE, null},	 		
	 		 {this.final_sub_msp1_account1_user_email,common_password, this.root_msp_direct_org_id,   null,  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
	 		 {this.final_sub_msp1_account1_user_email,common_password, this.sub_msp2_account1_id,   null,  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
	 		 {this.final_sub_msp1_account1_user_email,common_password, this.sub_msp1_account2_id,   null,  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},	 		 
	 		 {this.final_root_msp_direct_org_user_email,common_password, this.root_msp_direct_org_id, expectedRootMSPDirectOrgResult,  SpogConstants.SUCCESS_GET_PUT_DELETE, null},	 		
	 		 {this.final_root_msp_direct_org_user_email,common_password, this.sub_msp2_account1_id,   null,  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
	 		
	 	 
	 	 };
	 	}
   @Test(dataProvider = "orgInfo1", enabled=true)
   /**
    * 1. direct admin could get amount of source type of its organization
    * 2. msp admin could get amount of  source type of its organization
    * 3. sub org admin could get amount of  source type of its organization
    * 4. MSP account admin could get amount of  source type of mastered organization
    * 5. msp admin could get amount of  source type of sub organization
    * 6. csr admin could get amount of source type of direct organization
    * 7. csr admin could get amount of source type of msp organization
    * 8 csr admin could get amount of source type of msp sub organization
    * 
    * 9. direct admin could not get amount of source type of msp organization, report 403
    * 10. direct admin could not get amount of source type of msp sub organization, report 403
    * 11. msp admin could not get amount of source type of direct organization, report 403
    * 12. sub org admin could not get amount of source type of its parent organization, report 403
    * 13. sub org admin could not get amount of source type of direct organization, report 403
    * 14. MSP account admin could not get amount of source type of direct organization, report 403
    * 15. MSP account admin could not get amount of source type of msp organization, report 403
    * 16. MSP account admin could not get amount of source type of other sub organization, report 403
    * 
    * 17. set id as null in url, report 400
    * 18. set id as "" in url, report 404
    * 19. set id as random string in url, report 401
    * 
    * root msp admin could get amount for its direct org
    * root msp admin could not get amount for sub msp1 account
    * root msp msp account admin could get amount for its direct org
    * root msp msp account  admin could not get amount for sub msp1 account   
    * sub  msp1 admin could get amount for its direct org
    * sub  msp1 admin could not get amount for root msp's direct org
    * sub  msp1 admin could not get amount for sub msp2 's direct org
     * sub msp1 msp account admin could get amount for its direct org
    * sub  msp1 msp account admin could not get amount for root msp's direct org
    * sub  msp1 msp account admin could not get amount for sub msp1 's account2
    * sub  msp1 msp account admin could not get amount for sub msp2 's direct org
    * 
    * sub msp1 account admin could get amount for its org
    * sub msp1 account admin could not get amount for root msp's direct org
    * sub msp1 account admin could not get amount for sub msp1 account2
    * sub msp1 account admin could not get amount for sub msp1 account1
      root msp direct org's admin could get amount for its org
    * root msp direct org's admin could not get amount for sub msp2 account1

    * 
    *    * @param userName
    * @param orgId
    * @param expectedOrgResult
    */
   public void getSourcesTypeAmountByOrgId(String userName, String password, String orgId, List<HashMap<String, Object>> expectedOrgResult,
		   int expectedStatusCode, String expectedErrorCode) {
	   
	   spogServer.userLogin(userName, password);
	   source4SpogServer.setToken(spogServer.getJWTToken());
	   Response response = source4SpogServer.getSourcesTypesAmountByOrgId(orgId, spogServer.getJWTToken());
	   source4SpogServer.checkGetSourcesTypesAmountByOrgId(response, expectedStatusCode, expectedErrorCode, expectedOrgResult);
	   
   }
   
   @Test
   /**
    * 20. Can NOT call API if not logged in - 401when JWT is missing
    * 21. Can NOT call API with invalid token
    */
   public void getSourcesTypeAmountByOrgIdWithInvalidToken(){
	   
	   
	   Response response = source4SpogServer.getSourcesTypesAmountByOrgIdWithoutLogin(direct_org_id);
	   source4SpogServer.checkGetSourcesTypesAmountByOrgId(response, SpogConstants.NOT_LOGGED_IN, ErrorCode.AUTHORIZATION_HEADER_BLANK, null);
	   //test with invalid token
	   response = source4SpogServer.getSourcesTypesAmountByOrgId(direct_org_id, UUID.randomUUID().toString());
	   source4SpogServer.checkGetSourcesTypesAmountByOrgId(response, SpogConstants.NOT_LOGGED_IN, ErrorCode.AUTHORIZATION_HEADER_BLANK, null);
   }
  
   private void createSource(String userName, String password, SourceProduct sourceProduct, String siteId, SourceType sourceType, String org_id){
	   		
	  		spogServer.userLogin(userName, password);
		 	String soureceName = spogServer.ReturnRandom("shuo_source");
 	
			test.log(LogStatus.INFO,"create source and check");
			spogServer.errorHandle.printInfoMessageInDebugFile("create source and check");
		
			if(sourceProduct.equals(SourceProduct.cloud_direct)){
				String userToken = spogServer.getJWTToken();
				spogServer.setToken(siteTokenMap.get(siteId));
								
				String new_source_id = spogServer.createSourceWithCheck(soureceName, sourceType, sourceProduct, org_id, siteId,  ProtectionStatus.unprotect, ConnectionStatus.online, 
						OSMajor.windows.name(), null, test);
				
				spogServer.setToken(userToken);
				sourceIdArray.add(new_source_id);				
			}else{
				String new_source_id = spogServer.createSourceWithCheck(soureceName, sourceType, sourceProduct, org_id, siteId,  ProtectionStatus.unprotect, ConnectionStatus.online, 
						OSMajor.windows.name(), null, test);
				sourceIdArray.add(new_source_id);
			}
  }
   
   
   private List<	HashMap<String, Object> >  prepareSources4Org(String userName, String password, String siteId, String org_id){
	   
	   SourceType[] sourceTypeArray = SourceType.values();
	   SourceProduct[] sourceProductArray = SourceProduct.values();
	   List<	HashMap<String, Object> > expectedResult = new ArrayList<	HashMap<String, Object> > ();
	   for(int i=0; i<sourceTypeArray.length; i++){
		   HashMap<String, Object> eachItem = new HashMap<String, Object>();
		   eachItem.put("source_type", sourceTypeArray[i]);
		   int amount =0;
		   for(int j=0; j< sourceProductArray.length; j++){
			   if(sourceProductArray[j]== SourceProduct.cloud_direct){
				   if(sourceTypeArray[i].equals(SourceType.agentless_vm) || sourceTypeArray[i].equals(SourceType.machine) ){
					   createSource(userName, password, sourceProductArray[j], siteId, sourceTypeArray[i], org_id );
					   amount++;
				   }
			   }else{
				   createSource(userName, password, sourceProductArray[j], siteId, sourceTypeArray[i], org_id );
				   amount++;
			   }
			  
		   }
		   eachItem.put("amount", amount);
		   expectedResult.add(eachItem);
	   }
	   return expectedResult;
	   
   }
   
   private List<HashMap<String, Object>>  getTotalAmount4Org( List<	HashMap<String, Object> > expectedResult1,  List<HashMap<String, Object>> expectedResult2){
	   
	   List<	HashMap<String, Object> > expectedOrgResult = new ArrayList<	HashMap<String, Object> >();
	   SourceType[] sourceTypeArray = SourceType.values();
	   for(int i=0; i<sourceTypeArray.length; i++){
		   String key = expectedResult1.get(i).get("source_type").toString();
		   int amount1 = (int)expectedResult1.get(i).get("amount");
		   int amount2 = (int)expectedResult2.get(i).get("amount");
		   HashMap<String, Object> eachItem = new HashMap<String, Object> ();
		   eachItem.put("source_type", key);
		   eachItem.put("amount", amount1+amount2);
		   expectedOrgResult.add(eachItem);
	   }
	   
	   return expectedOrgResult;
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
