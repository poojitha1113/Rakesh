package api.sources;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang3.ArrayUtils;
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
import Constants.JobStatus;
import Constants.JobType4LatestJob;
import Constants.OSMajor;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.Org4SPOGServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGHypervisorsServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import InvokerServer.GatewayServer.siteType;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import invoker.SiteTestHelper;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;

public class GetSourcesSortTest  extends base.prepare.Is4Org{
	
	
	private SPOGServer spogServer;
	  private UserSpogServer userSpogServer;
	  private GatewayServer gatewayServer;
	  private SPOGDestinationServer spogDestinationServer;
	  Policy4SPOGServer policy4SPOGServer;
	  private Org4SPOGServer org4SpogServer;
	  private String csrAdminUserName;
	  private String csrAdminPassword;
	
	  private ExtentTest test;
	  
	  private String postfix_email = "@arcserve.com";
	  private String common_password = "Welcome*02";
	  
	  private String prefix_direct = "spogqa_shuo_direct";
	  private String direct_org_name = prefix_direct + "_org";
	  private String direct_org_id=null;
	  private String direct_user_name = prefix_direct + "_admin";
	  private String direct_user_name_email = direct_user_name + postfix_email;
	  private String direct_user_first_name = direct_user_name + "_first_name";
	  private String direct_user_last_name = direct_user_name + "_last_name";
	  private String direct_user_id =null;
	  private String final_direct_user_name_email = null;
	  private String direct_site_id;
	  
	  
	  private String prefix_msp = "spogqa_shuo_msp";
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
	  
	  private String prefix_msp_account = "spogqa_shuo_msp_account";
	  private String msp_account_admin_name = prefix_msp_account + "_admin";
	  private String msp_account_admin_email =msp_account_admin_name + postfix_email;
	  private String msp_account_admin_first_name = msp_account_admin_name + "_first_name";
	  private String msp_account_admin_last_name = msp_account_admin_name + "_last_name";
	  private String msp_account_admin_id;
	  private String final_msp_account_admin_email;
	  
	  private String account_id;
	  private String account_user_email;
	  private String account_user_id;
	  private String account_site_id;
	  private String another_direct_site_id;
	  private String another_msp_site_id;
	  private String another_account_site_id;
	  private String another_account_id;
	  private String site_id_another_account;
		private String accountGroupName;
		private String accountGroupName_id;
		
	  private String prefix_csr = "spogqa_shuo_csr";
	  private String csr_user_name = prefix_csr + "_admin";
	  private String csr_user_name_email = prefix_csr + "_admin" + postfix_email;
	  private String csr_user_first_name = csr_user_name + "_first_name";
	  private String csr_user_last_name = csr_user_name + "_last_name";
	  
	  //this is for update portal, each testng class is taken as BQ set
/*	  private SQLServerDb bqdb1;
	  public int Nooftest;
	  private long creationTime;
	  private String BQName=null;
	  private String runningMachine;
	  private testcasescount count1;
	  private String buildVersion;
	  private ExtentReports rep;
	  */
	  
	  private String directGroupName_id;
	  private String directGroupName;
	  private String mspGroupName;
	  private String mspGroupName_id;
	  private	String job_Type = "backup";
	  private	String job_Status= "finished";
	  private	String job_Method = "full";
	  private HashMap<String,String> siteInfo = new HashMap<String, String>();
	private String direct_destination_id;
	private HashMap<String, String> destinationMap;
	private String direct_another_destination_id;
	private SPOGHypervisorsServer spogHypervisorsServer;
	private String direct_hypervisor_id;
	private String  org_model_prefix=this.getClass().getSimpleName();

	  
	  @BeforeClass
	  @Parameters({ "baseURI", "port", "logFolder", "csrAdminUserName", "csrAdminPassword", "buildVersion"})
	  public void beforeClass(String baseURI, String port, String logFolder, String adminUserName, String adminPassword ,  String buildVersion) throws UnknownHostException {
		  
			
		
		  	spogServer = new SPOGServer(baseURI, port);
			userSpogServer = new UserSpogServer(baseURI, port);
			org4SpogServer = new Org4SPOGServer(baseURI, port);
			spogHypervisorsServer = new SPOGHypervisorsServer(baseURI, port);
			spogDestinationServer = new SPOGDestinationServer(baseURI, port);
			policy4SPOGServer =new Policy4SPOGServer(baseURI, port);
		  	gatewayServer =new GatewayServer(baseURI,port);
		  	rep = ExtentManager.getInstance("GetSourcesTest",logFolder);
		  	this.csrAdminUserName = adminUserName;
		  	this.csrAdminPassword = adminPassword;
		
		    test = rep.startTest("beforeClass");
		
		    prepareEnv();

		  	//this is for update portal
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


	  private void prepareEnv(){
		

		  	spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
			//*******************create direct org,user,site**********************/
			String prefix = RandomStringUtils.randomAlphanumeric(8);
		/*	test.log(LogStatus.INFO,"create a direct org");
			direct_org_id = spogServer.CreateOrganizationWithCheck(prefix+direct_org_name , SpogConstants.DIRECT_ORG, null, null, null, null, test);
			final_direct_user_name_email = prefix + direct_user_name_email;
			
			test.log(LogStatus.INFO,"create a admin under direct org");
			direct_user_id = spogServer.createUserAndCheck(final_direct_user_name_email, common_password, prefix + direct_user_first_name, prefix + direct_user_last_name, SpogConstants.DIRECT_ADMIN, direct_org_id, test);
			spogServer.userLogin(final_direct_user_name_email, common_password);
		  	
		*/
			destinationMap = new HashMap<String,String>();
			 test.log(LogStatus.INFO,"create direct organization and user");
			 org4SpogServer.setToken(spogServer.getJWTToken());
			 spogDestinationServer.setToken(spogServer.getJWTToken());
		 	 String[] datacenterIDs=spogDestinationServer.getDestionationDatacenterID(); 		  		 
		 	 final_direct_user_name_email = prefix + direct_user_name_email;
			
		 	 
		 	 direct_org_id= spogServer.CreateOrganizationByEnrollWithCheck(  prefix+direct_org_name+org_model_prefix, SpogConstants.DIRECT_ORG, final_direct_user_name_email, common_password, prefix + direct_user_first_name, prefix + direct_user_last_name);
		 	 Response response = userSpogServer.postUsersSearchResponse(final_direct_user_name_email, null, null, spogServer.getJWTToken());
	 		  if(response.statusCode()==SpogConstants.SUCCESS_GET_PUT_DELETE){
	 			 int total_size = response.then().extract().path("pagination.total_size");
			 	  if(total_size==1){
			 		 ArrayList<HashMap<String, Object>> dataList = response.then().extract().path("data");
			 		 direct_user_id = (String) dataList.get(0).get("user_id");
	
			 	  }
	 		  }
	 		
		 	 
		 	 /*	 Response response = org4SpogServer.enrollOrganizations(prefix + direct_user_first_name, prefix + direct_user_last_name, final_direct_user_name_email, null,  prefix+direct_org_name, SpogConstants.DIRECT_ORG,datacenterIDs[0],test);
		   	 direct_org_id= org4SpogServer.checkEnrollOrganizations(response, prefix + direct_user_first_name, prefix + direct_user_last_name, final_direct_user_name_email, null, prefix+direct_org_name,  SpogConstants.DIRECT_ORG, datacenterIDs[0], SpogConstants.SUCCESS_POST, null,test);
		   	 direct_user_id = org4SpogServer.getUserIdFromEnrollOrganizationsResponse(response, test);
		   	 spogServer.updateUserById(direct_user_id, final_direct_user_name_email, common_password, prefix + direct_user_first_name, prefix + direct_user_last_name, SpogConstants.DIRECT_ADMIN, direct_org_id, test);
		   	*/
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
			 siteInfo.put( direct_site_id, direct_site_token);
			
			 siteName= spogServer.getRandomSiteName("TestCreate");
			 test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
			 sitetype=siteType.gateway.toString();
			 test.log(LogStatus.INFO,"The siteType :"+sitetype);
			
			 test.log(LogStatus.INFO,"Creating another site For direct org");
			 another_direct_site_id = gatewayServer.createsite_register_login(direct_org_id, direct_user_validToken, direct_user_id, "shuo1", "1.0.0", spogServer, test);
			 String another_direct_site_token=gatewayServer.getJWTToken();
			 test.log(LogStatus.INFO, "The site token is "+another_direct_site_token);
			 siteInfo.put( another_direct_site_id, another_direct_site_token);
			
		     directGroupName = spogServer.ReturnRandom("directGroupName_shuo");
			 directGroupName_id = spogServer.createGroupWithCheck(direct_org_id, directGroupName, "", test);
			 
			 test.log(LogStatus.INFO,"Creating destination for the site");
			 String datacenterID=datacenterIDs[0];			    		
			 String destination_name= "spog_direct_destination_get_sources_"+RandomStringUtils.randomAlphanumeric(3);
			 direct_destination_id = spogDestinationServer.createDestinationWithCheck("", direct_org_id, direct_site_id, datacenterID,"cloud_direct_volume", "running", "1",
						"" , "normal", "zhash05-win8", "custom","custom", "0", "0", "7", "0",
	    				"0", "0",  "", "", "", "", destination_name,test);
			 destinationMap.put(this.direct_site_id, direct_destination_id);
			 
			 destination_name= "spog_direct_destination__get_sources_"+RandomStringUtils.randomAlphanumeric(3);
			 direct_another_destination_id = spogDestinationServer.createDestinationWithCheck("", direct_org_id, another_direct_site_id, datacenterID,"cloud_direct_volume", "running", "1",
						"" , "normal", "zhash05-win8", "custom","custom", "0", "0", "7", "0",
	    				"0", "0",  "", "", "", "", destination_name,test);
			 destinationMap.put(this.another_direct_site_id, direct_another_destination_id);
			 
			 spogHypervisorsServer.setToken(direct_site_token);
			 direct_hypervisor_id = spogHypervisorsServer.createHypervisorWithCheck("none", prefix + "shuo_hypervisor_name", "vmware", "cloud_direct", "none", direct_site_id, 
						direct_org_id, "false", String.valueOf(System.currentTimeMillis()), direct_destination_id,  "none", "0 0 * * *", "1d", null, null, null, null, null, 
								"none", "none", "emptyarray", "emptyarray", "emptyarray", "emptyarray", "none", "none", test);
			 
			 
			//************************create msp org,user,site*************************************
			spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
			prefix = RandomStringUtils.randomAlphanumeric(8);
			test.log(LogStatus.INFO,"create a msp org and user");
			final_msp_user_name_email = prefix + msp_user_name_email;
			
			/* response = org4SpogServer.enrollOrganizations(prefix + msp_user_first_name, prefix + msp_user_last_name, final_msp_user_name_email, null,  prefix+msp_org_name, SpogConstants.MSP_ORG,datacenterIDs[0],test);
			 msp_org_id= org4SpogServer.checkEnrollOrganizations(response, prefix + msp_user_first_name, prefix + msp_user_last_name, final_msp_user_name_email, null,  prefix+msp_org_name, SpogConstants.MSP_ORG,datacenterIDs[0], SpogConstants.SUCCESS_POST, null,test);
			 msp_user_id = org4SpogServer.getUserIdFromEnrollOrganizationsResponse(response, test);
		   	 spogServer.updateUserById(msp_user_id, final_msp_user_name_email, common_password, prefix + msp_user_first_name, prefix + msp_user_last_name, SpogConstants.MSP_ADMIN, msp_org_id, test);
		   	*/
			msp_org_id = spogServer.CreateOrganizationByEnrollWithCheck(prefix + msp_org_name+org_model_prefix, SpogConstants.MSP_ORG, final_msp_user_name_email, common_password, prefix + msp_user_first_name, prefix + msp_user_last_name);
			 response = userSpogServer.postUsersSearchResponse(final_msp_user_name_email, null, null, spogServer.getJWTToken());
	 		  if(response.statusCode()==SpogConstants.SUCCESS_GET_PUT_DELETE){
	 			 int total_size = response.then().extract().path("pagination.total_size");
			 	  if(total_size==1){
			 		 ArrayList<HashMap<String, Object>> dataList = response.then().extract().path("data");
			 		msp_user_id = (String) dataList.get(0).get("user_id");
	
			 	  }
	 		  }
		/*	msp_org_id = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name , SpogConstants.MSP_ORG, null, null, null, null, test);
			final_msp_user_name_email = prefix + msp_user_name_email;
			
			test.log(LogStatus.INFO,"create a admin under msp org");
			msp_user_id = spogServer.createUserAndCheck(final_msp_user_name_email, common_password, prefix + msp_user_first_name, prefix + msp_user_last_name, SpogConstants.MSP_ADMIN, msp_org_id, test);
			*/
			
			spogServer.userLogin(final_msp_user_name_email, common_password);
		  	
			test.log(LogStatus.INFO,"create a msp account admin under msp org");
			final_msp_account_admin_email = prefix + this.msp_account_admin_email;
			msp_account_admin_id = spogServer.createUserAndCheck(final_msp_account_admin_email, common_password, prefix + msp_account_admin_first_name, prefix + msp_account_admin_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, msp_org_id, test);
			
		  	
	/*		test.log(LogStatus.INFO,"Getting the JWTToken for the msp user");
			String msp_user_validToken = spogServer.getJWTToken();
			test.log(LogStatus.INFO,"The token is :"+ msp_user_validToken );
			
			siteName= spogServer.getRandomSiteName("TestCreate");
			test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
			sitetype=siteType.gateway.toString();
			test.log(LogStatus.INFO,"The siteType :"+sitetype);
			
			test.log(LogStatus.INFO,"Creating a site For msp org");
			msp_site_id = gatewayServer.createsite_register_login(msp_org_id, msp_user_validToken, msp_user_id, "shuo", "1.0.0", spogServer, test);
			String msp_site_token=gatewayServer.getJWTToken();
			test.log(LogStatus.INFO, "The site token is "+ msp_site_token);
			siteInfo.put( msp_site_id, msp_site_token);
			
			siteName= spogServer.getRandomSiteName("TestCreate");
			test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
			sitetype=siteType.gateway.toString();
			test.log(LogStatus.INFO,"The siteType :"+sitetype);
			
			test.log(LogStatus.INFO,"Creating another site For msp org");
			another_msp_site_id = gatewayServer.createsite_register_login(msp_org_id, msp_user_validToken, msp_user_id, "shuo1", "1.0.0", spogServer, test);
			String another_msp_site_token=gatewayServer.getJWTToken();
			test.log(LogStatus.INFO, "The site token is "+ another_msp_site_token);
			siteInfo.put( another_msp_site_id, another_msp_site_token);*/
			
		

			//create account, account user and site
			test.log(LogStatus.INFO,"Creating a account For msp org");
			account_id = spogServer.createAccountWithCheck(msp_org_id, "sub_" + prefix + msp_org_name, "", test);
			prefix = RandomStringUtils.randomAlphanumeric(8);
		
			test.log(LogStatus.INFO,"Creating a account user For account org");
			account_user_email = prefix + msp_user_name_email;
			account_user_id = spogServer.createUserAndCheck(account_user_email, common_password, prefix + msp_user_first_name, prefix + msp_user_last_name, SpogConstants.DIRECT_ADMIN, account_id, test);
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
			siteInfo.put( account_site_id, account_site_token);
			
			siteName= spogServer.getRandomSiteName("TestCreate");
			test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
			sitetype=siteType.gateway.toString();
			test.log(LogStatus.INFO,"The siteType :"+sitetype);
			
			test.log(LogStatus.INFO,"Creating another site For account org");
			another_account_site_id = gatewayServer.createsite_register_login(account_id, account_user_validToken, account_user_id, "shuo1", "1.0.0", spogServer, test);
			String another_account_site_token=gatewayServer.getJWTToken();
			test.log(LogStatus.INFO, "The site token is "+ another_account_site_token);
			siteInfo.put( another_account_site_id, another_account_site_token);
			
			accountGroupName = spogServer.ReturnRandom("accountGroupName_shuo");
			accountGroupName_id = spogServer.createGroupWithCheck(account_id, accountGroupName, "", test);
			
			spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
			test.log(LogStatus.INFO,"assign account to msp account admin");
			String[] mspAccountAdmins = new String []{msp_account_admin_id};
			userSpogServer.assignMspAccountAdmins(msp_org_id, account_id, mspAccountAdmins , spogServer.getJWTToken()); 
			
			//create account, account user 
			test.log(LogStatus.INFO,"Creating another account For msp org");
			prefix = RandomStringUtils.randomAlphanumeric(8);
			another_account_id = spogServer.createAccountWithCheck(msp_org_id, "sub_" + prefix + msp_org_name, "", test);
			
			siteName= spogServer.getRandomSiteName("TestCreate");
			test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
			sitetype=siteType.gateway.toString();
			test.log(LogStatus.INFO,"The siteType :"+sitetype);
			
			test.log(LogStatus.INFO,"Creating a site For another account org");
			site_id_another_account = gatewayServer.createsite_register_login(another_account_id, spogServer.getJWTToken(), spogServer.GetLoggedinUser_UserID(), "shuo", "1.0.0", spogServer, test);
	
			
	  }
	 
	  private void deleteEnv(){

			spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
			String csr_user_validToken = spogServer.getJWTToken();
			test.log(LogStatus.INFO,"delete direct site");
			
			Collection<String> values = destinationMap.values();
			Iterator<String> valueIter = values.iterator();
			while(valueIter.hasNext()){
				spogDestinationServer.RecycleDirectVolume(valueIter.next(), test);
				
			}
			
			spogServer.errorHandle.printInfoMessageInDebugFile("delete direct site");
			SiteTestHelper.deleteSite(direct_site_id, csr_user_validToken);
			spogServer.errorHandle.printInfoMessageInDebugFile("delete another direct site");
			SiteTestHelper.deleteSite(another_direct_site_id, csr_user_validToken);
					
			test.log(LogStatus.INFO,"destroy direct org");
			org4SpogServer.setToken(csr_user_validToken);
			org4SpogServer.destroyOrganization(direct_org_id, test);
			/*spogServer.errorHandle.printInfoMessageInDebugFile("delete direct organization");
			spogServer.DeleteOrganizationWithCheck(direct_org_id, test);*/
			
			spogServer.errorHandle.printInfoMessageInDebugFile("delete account site");
			SiteTestHelper.deleteSite(account_site_id, csr_user_validToken);
			spogServer.errorHandle.printInfoMessageInDebugFile("delete another account site");
			SiteTestHelper.deleteSite(another_account_site_id, csr_user_validToken);
			
			spogServer.errorHandle.printInfoMessageInDebugFile("delete another account's site");
			SiteTestHelper.deleteSite(site_id_another_account, csr_user_validToken);
			
			
			spogServer.errorHandle.printInfoMessageInDebugFile("delete msp site");
			SiteTestHelper.deleteSite(msp_site_id, csr_user_validToken);
			spogServer.errorHandle.printInfoMessageInDebugFile("delete another msp site");
			SiteTestHelper.deleteSite(another_msp_site_id, csr_user_validToken);
			
			
			test.log(LogStatus.INFO,"delete msp org");
			spogServer.errorHandle.printInfoMessageInDebugFile("destroy msp organization");
			//spogServer.DeleteOrganizationWithCheck(msp_org_id, test);
			org4SpogServer.destroyOrganization(msp_org_id, test);
	  }

	  
	  
	  
	  @DataProvider(name = "sourceInfo3")
	  public final Object[][] getSourceInfo3() {
	 	 return new Object[][]   {
				  	  {"protection_status;asc"},
				  	 {"protection_status;desc"},
				  	 {"last_job_status;asc"},
	 		 		{"last_job_status;desc"},
	 		 				{"last_job_type;asc"},
	 		 			{"last_job_type;desc"},
	 		 			{"end_time_ts;desc"},
	 		 			{"end_time_ts;asc"}
	 	 };
	 	 	}
	  /**
	   * 
	   * all test cases will be deleted
	   * 1. Get sources sort by protection_status asc
	   * 5. Get sources sort by protection_status desc

	   * 3. Get sources sort by last_job_status asc
	   * 6. Get sources sort by last_job_status desc
	   * 
	   * 2. Get sources sort by last_job_type asc
	   * 7. Get sources sort by last_job_type desc
	   * 4. Get sources sort by end_time_ts asc
	   * 8. Get sources sort by end_time_ts desc
	   * @param sortStr
	   */
	  @Test(dataProvider = "sourceInfo3",enabled=false,  priority = 3)
	  public void getSourcesWithSort(String sortStr ) {
		  
			test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
			test.assignAuthor("shuo.zhang");
		  	spogServer.errorHandle.printInfoMessageInDebugFile("/****************getSourcesWithSort**************/");
		  
		  
			test.log(LogStatus.INFO,"admin login");
			spogServer.errorHandle.printInfoMessageInDebugFile("direct admin login");
		  	spogServer.userLogin(final_direct_user_name_email, common_password);

		  	ArrayList<String> allSourcesNodes = new    ArrayList<String> ();
		  	ArrayList<HashMap<String, Object> > expectedSourceList= new ArrayList<HashMap<String, Object> >();
		  	String[] sortArray = sortStr.split(";");
		    
		  	if(sortArray[0].equalsIgnoreCase("protection_status")){
		    	
				
				for(int i=0; i<2; i++){
					String soureceName = spogServer.ReturnRandom("shuo_source");	
					test.log(LogStatus.INFO,"create source ");
					spogServer.errorHandle.printInfoMessageInDebugFile("create source");
					Response createSourceResponse = spogServer.createSource(soureceName, SourceType.machine, SourceProduct.cloud_direct, direct_org_id, direct_site_id,  ProtectionStatus.values()[i], 
				  			ConnectionStatus.offline, OSMajor.windows.name(), null, null, null, null, null, null, null, null, null, test);
					/**/
					String source_id =  createSourceResponse.then().extract().path("data.source_id");
			    	allSourcesNodes.add(source_id);
					if(i==0){
						  String pmfKey="shuo";
			    		  String policy_id = spogServer.returnRandomUUID();
			    		  String task_id=spogServer.returnRandomUUID();	
			    		  HashMap<String, Object> cloudDirectScheduleDTO=policy4SPOGServer.createCloudDirectScheduleDTO("0 6 * * *", test);
				   		  HashMap<String, Object> scheduleSettingDTO=policy4SPOGServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null,test);
				   		  ArrayList<HashMap<String,Object>> schedules =policy4SPOGServer.createPolicyScheduleDTO(null,spogServer.returnRandomUUID(), 
				   				  "1d", task_id, destinationMap.get(direct_site_id), scheduleSettingDTO, "06:00", "12:00", "cloud_direct_file_folder_backup" , "dest",test);
				     		  
				   		  
				   		  ArrayList<HashMap<String,Object>> excludes=policy4SPOGServer.createExcludeInfoDTO(null, "path", "c:\\tmp", test);
				   		  HashMap<String, Object> cloudDirectLocalBackupDTO=policy4SPOGServer.createCloudDirectLocalBackupDTO("d:\\tmp", "true" , test);
				   		  HashMap<String, Object> cloudDirectFileBackupTaskInfoDTO=policy4SPOGServer.createCloudDirectFileBackupTaskInfoDTO("d:\\tmp", cloudDirectLocalBackupDTO, excludes,test);
				   		  ArrayList<HashMap<String,Object>>  destinations= policy4SPOGServer.createPolicyTaskDTO(null, task_id, "cloud_direct_file_folder_backup", destinationMap.get(direct_site_id), "none", null, cloudDirectFileBackupTaskInfoDTO, null, test);
				   		  ArrayList<HashMap<String,Object>> throttles =policy4SPOGServer.createPolicyThrottleDTO(null, spogServer.returnRandomUUID(), task_id, "network", "1200", "1", "06:00", "18:00", "cloud_direct_file_folder_backup" , destinationMap.get(direct_site_id),"dest",test);

				   		  policy4SPOGServer.setToken(spogServer.getJWTToken());
				   		  //create cloud direct policy
				   		  String policyName = spogServer.ReturnRandom(pmfKey);
				   		  Response response=policy4SPOGServer.createPolicy(policyName, spogServer.ReturnRandom(pmfKey), "cloud_direct_baas", null, true,  source_id, destinations, schedules, throttles, policy_id, direct_org_id, test);
				   		  policy4SPOGServer.checkPolicyDestinations(response,SpogConstants.SUCCESS_POST,destinations,test);

			    			long start_time_ts = System.currentTimeMillis()/1000;
							String server_id = UUID.randomUUID().toString();			
							String rps_id = UUID.randomUUID().toString();
						//	String datastore_id = UUID.randomUUID().toString();
							long endTimeTS = System.currentTimeMillis()/1000;
						
				
				    		String post_job_id = gatewayServer.postJobWithCheck(start_time_ts,endTimeTS, direct_org_id, server_id, source_id, rps_id, destinationMap.get(direct_site_id), policy_id,
				    				"backup", "full","finished", siteInfo.get(direct_site_id), test);
				    		String[] policyIds = new String[1];
			    			policyIds[0]= policy_id;
			    			String[] policyNames = new String[1];
			    			policyNames[0]=policyName;
			    			
			    		ArrayList<HashMap<String, Object>> lastJobList = getLastJob(JobType4LatestJob.backup_full, null,"finished", start_time_ts, endTimeTS, 0.0);
						
						HashMap<String, Object> eachSourceInfo = getExpectedSourceMap(createSourceResponse, direct_site_id, policyIds,policyNames, null, spogServer.getJWTToken(), false,lastJobList);
						expectedSourceList.add(eachSourceInfo);
					}else{
						HashMap<String, Object> eachSourceInfo = getExpectedSourceMap(createSourceResponse, direct_site_id, null,null, null, spogServer.getJWTToken(), false,null);
						expectedSourceList.add(eachSourceInfo);
					}
					
				}
				
				if(sortArray[1].equalsIgnoreCase("desc")){
					Collections.reverse(expectedSourceList);
				}
	
		  	}else if(sortArray[0].equalsIgnoreCase("last_job_status")){
		  		
		  		JobStatus[] jobStatusArray = JobStatus.values();
		  		String[] strJobStatusArray = new String[jobStatusArray.length];
		  		for(int i=0; i<jobStatusArray.length; i++){
		  			strJobStatusArray[i] = jobStatusArray[i].name();
		  		}
		  		
		  		Arrays.sort(strJobStatusArray);
		  		for(int i=0; i<jobStatusArray.length; i++){
		  			String soureceName = spogServer.ReturnRandom("shuo_source");	
					test.log(LogStatus.INFO,"create source ");
					spogServer.errorHandle.printInfoMessageInDebugFile("create source");
				/*	Response createSourceResponse = spogServer.createSource(soureceName, SourceType.machine, SourceProduct.udp, direct_org_id, direct_site_id,  ProtectionStatus.protect, 
				  			ConnectionStatus.offline, OSMajor.windows.name(), null, null, null, null, null, null, null, null, null, test);
					
					long start_time_ts = System.currentTimeMillis()/1000;
					String server_id = UUID.randomUUID().toString();			
					String rps_id = UUID.randomUUID().toString();
					String datastore_id = UUID.randomUUID().toString();	
					String policy_id= UUID.randomUUID().toString();
					long endTimeTS = System.currentTimeMillis()/1000;
					String source_id =  createSourceResponse.then().extract().path("data.source_id");
					
		    		String post_job_id = gatewayServer.postJobWithCheck(start_time_ts,endTimeTS, direct_org_id, server_id, source_id, rps_id, datastore_id, policy_id,
		    				job_Type, job_Method,strJobStatusArray[i], siteInfo.get(direct_site_id), test);

		    		
		    		
		    		*/
					Response createSourceResponse = spogServer.createSource(soureceName,SourceType.machine, SourceProduct.cloud_direct, direct_org_id, direct_site_id,  ProtectionStatus.protect, 
			    			ConnectionStatus.offline, OSMajor.windows.name(), null, null, null, null, null, null, null, null, null, test);
					String source_id =  createSourceResponse.then().extract().path("data.source_id");
		    		  String pmfKey="shuo";
		    		  String policy_id = spogServer.returnRandomUUID();
		    		  String task_id=spogServer.returnRandomUUID();	
		    		  HashMap<String, Object> cloudDirectScheduleDTO=policy4SPOGServer.createCloudDirectScheduleDTO("0 6 * * *", test);
			   		  HashMap<String, Object> scheduleSettingDTO=policy4SPOGServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null,test);
			   		  ArrayList<HashMap<String,Object>> schedules =policy4SPOGServer.createPolicyScheduleDTO(null,spogServer.returnRandomUUID(), 
			   				  "1d", task_id, destinationMap.get(direct_site_id), scheduleSettingDTO, "06:00", "12:00", "cloud_direct_file_folder_backup" , "dest",test);
			     		  
			   		  
			   		  ArrayList<HashMap<String,Object>> excludes=policy4SPOGServer.createExcludeInfoDTO(null, "path", "c:\\tmp", test);
			   		  HashMap<String, Object> cloudDirectLocalBackupDTO=policy4SPOGServer.createCloudDirectLocalBackupDTO("d:\\tmp", "true" , test);
			   		  HashMap<String, Object> cloudDirectFileBackupTaskInfoDTO=policy4SPOGServer.createCloudDirectFileBackupTaskInfoDTO("d:\\tmp", cloudDirectLocalBackupDTO, excludes,test);
			   		  ArrayList<HashMap<String,Object>>  destinations= policy4SPOGServer.createPolicyTaskDTO(null, task_id, "cloud_direct_file_folder_backup", destinationMap.get(direct_site_id), "none", null, cloudDirectFileBackupTaskInfoDTO, null, test);
			   		  ArrayList<HashMap<String,Object>> throttles =policy4SPOGServer.createPolicyThrottleDTO(null, spogServer.returnRandomUUID(), task_id, "network", "1200", "1", "06:00", "18:00", "cloud_direct_file_folder_backup" , destinationMap.get(direct_site_id),"dest",test);

			   		  policy4SPOGServer.setToken(spogServer.getJWTToken());
			   		  //create cloud direct policy
			   		  String policyName = spogServer.ReturnRandom(pmfKey);
			   		  Response response=policy4SPOGServer.createPolicy(policyName, spogServer.ReturnRandom(pmfKey), "cloud_direct_baas", null, true,  source_id, destinations, schedules, throttles, policy_id, direct_org_id, test);
			   		  policy4SPOGServer.checkPolicyDestinations(response,SpogConstants.SUCCESS_POST,destinations,test);

		    			long start_time_ts = System.currentTimeMillis()/1000;
						String server_id = UUID.randomUUID().toString();			
						String rps_id = UUID.randomUUID().toString();
					//	String datastore_id = UUID.randomUUID().toString();
						long endTimeTS = System.currentTimeMillis()/1000;
					
			
			    		String post_job_id = gatewayServer.postJobWithCheck(start_time_ts,endTimeTS, direct_org_id, server_id, source_id, rps_id, destinationMap.get(direct_site_id), policy_id,
			    				"backup", "full",strJobStatusArray[i], siteInfo.get(direct_site_id), test);
			    		String[] policyIds = new String[1];
		    			policyIds[0]= policy_id;
		    			String[] policyNames = new String[1];
		    			policyNames[0]=policyName;
		    			
		    		ArrayList<HashMap<String, Object>> lastJobList = getLastJob(JobType4LatestJob.backup_full, null, strJobStatusArray[i], start_time_ts, endTimeTS, 0.0);
					
					HashMap<String, Object> eachSourceInfo = getExpectedSourceMap(createSourceResponse, direct_site_id, policyIds,policyNames, null, spogServer.getJWTToken(), false,lastJobList);
					expectedSourceList.add(eachSourceInfo);
					
			    	allSourcesNodes.add(source_id);
			    	
		  		}
		  		if(sortArray[1].equalsIgnoreCase("desc")){
					Collections.reverse(expectedSourceList);
				}
		  		
		  	}else if(sortArray[0].equalsIgnoreCase("last_job_type")){
		  		
		  		JobType4LatestJob[] jobTypeArray = JobType4LatestJob.values();
		  		String[] strJobTypeArray = new String[jobTypeArray.length];
		  		for(int i=0; i<strJobTypeArray.length; i++){
		  			strJobTypeArray[i] = jobTypeArray[i].name();
		  		}
		  		Arrays.sort(strJobTypeArray);
		  		
		  		for(int i=0; i<strJobTypeArray.length; i++){
		  			String soureceName = spogServer.ReturnRandom("shuo_source");	
					test.log(LogStatus.INFO,"create source ");
					spogServer.errorHandle.printInfoMessageInDebugFile("create source");
					Response createSourceResponse = spogServer.createSource(soureceName, SourceType.machine, SourceProduct.udp, direct_org_id, direct_site_id,  ProtectionStatus.protect, 
				  			ConnectionStatus.offline, OSMajor.windows.name(), null, null, null, null, null, null, null, null, null, test);
					
					long start_time_ts = System.currentTimeMillis()/1000;
					String server_id = UUID.randomUUID().toString();			
					String rps_id = UUID.randomUUID().toString();
					String datastore_id = UUID.randomUUID().toString();	
					String policy_id= UUID.randomUUID().toString();
					long endTimeTS = System.currentTimeMillis()/1000;
					String source_id =  createSourceResponse.then().extract().path("data.source_id");
					String post_job_id = null;
					if(strJobTypeArray[i].contains("backup")){
						post_job_id = gatewayServer.postJobWithCheck(start_time_ts,endTimeTS, direct_org_id, server_id, source_id, rps_id, datastore_id, policy_id,
			    				strJobTypeArray[i], null, job_Status, siteInfo.get(direct_site_id), test);
					}else if(strJobTypeArray[i].contains("replication")){
						post_job_id = gatewayServer.postJobWithCheck(start_time_ts,endTimeTS, direct_org_id, server_id, source_id, rps_id, datastore_id, policy_id,
								"rps_replicate", "", job_Status, siteInfo.get(direct_site_id), test);
					}else {
						post_job_id = gatewayServer.postJobWithCheck(start_time_ts,endTimeTS, direct_org_id, server_id, source_id, rps_id, datastore_id, policy_id,
								strJobTypeArray[i], "", job_Status, siteInfo.get(direct_site_id), test);
					}
		    		
		    		

				/*	String patten = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
					TimeZone utcZone = TimeZone.getTimeZone("UTC");
			
					SimpleDateFormat dateFormate = new SimpleDateFormat(patten);
					dateFormate.setTimeZone(utcZone);
					ArrayList<HashMap<String, Object>> lastJobList = new ArrayList<HashMap<String, Object>>();
					HashMap<String, Object> lastJobHashMap = new  HashMap<String, Object>();
					lastJobHashMap.put("type", 	strJobTypeArray[i]);
					lastJobHashMap.put("severity", null);
					lastJobHashMap.put("status", job_Status);
			//		lastJobHashMap.put("start_time_ts",dateFormate.format(start_time_ts));
			//		lastJobHashMap.put("end_time_ts", dateFormate.format(endTimeTS));
					lastJobHashMap.put("start_time_ts",start_time_ts);
					lastJobHashMap.put("end_time_ts", endTimeTS);
					lastJobHashMap.put("percent_complete", 0.0);
					lastJobList.add(lastJobHashMap);*/
					
					ArrayList<HashMap<String, Object>> lastJobList = getLastJob(strJobTypeArray[i], null, job_Status, start_time_ts, endTimeTS, 0.0);
					
					HashMap<String, Object> eachSourceInfo = getExpectedSourceMap(createSourceResponse, direct_site_id, null, null,null, spogServer.getJWTToken(), false,lastJobList);
					expectedSourceList.add(eachSourceInfo);
					
			    	allSourcesNodes.add(source_id);
		  		}
		  		if(sortArray[1].equalsIgnoreCase("desc")){
					Collections.reverse(expectedSourceList);
				}
		  		
		  	}else if(sortArray[0].equalsIgnoreCase("end_time_ts")){
		  		
		  				  		
		  		for(int i=0; i<3; i++){
		  			String soureceName = spogServer.ReturnRandom("shuo_source");	
					test.log(LogStatus.INFO,"create source ");
					spogServer.errorHandle.printInfoMessageInDebugFile("create source");
					Response createSourceResponse = spogServer.createSource(soureceName, SourceType.machine, SourceProduct.udp, direct_org_id, direct_site_id,  ProtectionStatus.protect, 
				  			ConnectionStatus.offline, OSMajor.windows.name(), null, null, null, null, null, null, null, null, null, test);
					
					long start_time_ts = System.currentTimeMillis()/1000;
					String server_id = UUID.randomUUID().toString();			
					String rps_id = UUID.randomUUID().toString();
					String datastore_id = UUID.randomUUID().toString();	
					String policy_id= UUID.randomUUID().toString();
					long endTimeTS = (System.currentTimeMillis()+10000)/1000;
					String source_id =  createSourceResponse.then().extract().path("data.source_id");
					String post_job_id = null;
				
					post_job_id = gatewayServer.postJobWithCheck(start_time_ts,endTimeTS, direct_org_id, server_id, source_id, rps_id, datastore_id, policy_id,
			    				job_Type, job_Method, job_Status, siteInfo.get(direct_site_id), test);
					
				/*	String patten = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
					TimeZone utcZone = TimeZone.getTimeZone("UTC");
			
					SimpleDateFormat dateFormate = new SimpleDateFormat(patten);
					dateFormate.setTimeZone(utcZone);
					ArrayList<HashMap<String, Object>> lastJobList = new ArrayList<HashMap<String, Object>>();
					HashMap<String, Object> lastJobHashMap = new  HashMap<String, Object>();
					lastJobHashMap.put("type", 	JobType4LatestJob.full_backup);
					lastJobHashMap.put("severity", null);
					lastJobHashMap.put("status", job_Status);
					lastJobHashMap.put("start_time_ts",dateFormate.format(start_time_ts));
					lastJobHashMap.put("end_time_ts", dateFormate.format(endTimeTS));
					lastJobHashMap.put("percent_complete", 0.0);
					lastJobList.add(lastJobHashMap);*/
					
					ArrayList<HashMap<String, Object>> lastJobList = getLastJob(JobType4LatestJob.backup_full, null, job_Status, start_time_ts, endTimeTS, 0.0);
					
					HashMap<String, Object> eachSourceInfo = getExpectedSourceMap(createSourceResponse, direct_site_id, null, null,null, spogServer.getJWTToken(), false,lastJobList);
					expectedSourceList.add(eachSourceInfo);
					
			    	allSourcesNodes.add(source_id);
		  		}
		  		if(sortArray[1].equalsIgnoreCase("desc")){
					Collections.reverse(expectedSourceList);
				}
		  		
		  	}
		  	try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			test.log(LogStatus.INFO,"get sources ");
			spogServer.errorHandle.printInfoMessageInDebugFile("get sources ");
		  	Response response = spogServer.getSources("is_deleted;=;false", sortStr, -1, -1, true, test);
		  	
			spogServer.checkGetSources(response, SpogConstants.SUCCESS_GET_PUT_DELETE, expectedSourceList, -1, -1, -1, null, test);
					
			test.log(LogStatus.INFO,"delete source");
			spogServer.errorHandle.printInfoMessageInDebugFile("delete source");
			for(int i=0; i< allSourcesNodes.size();i++){
				response = spogServer.deleteSourcesById(spogServer.getJWTToken(), allSourcesNodes.get(i), test);
				spogServer.deleteSourcesWithCheck(response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			}
			
		
	  }
	  
	  @DataProvider(name = "sourceInfo4")
	  public final Object[][] getSourceInfo4() {
	 	 return new Object[][]   {
				 	 {this.final_direct_user_name_email, common_password, direct_org_id, new String[]{direct_site_id, another_direct_site_id}, directGroupName_id, directGroupName_id},
				  {this.final_direct_user_name_email, common_password, direct_org_id, new String[]{this.direct_site_id, this.another_direct_site_id}, this.directGroupName_id, UUID.randomUUID().toString()},
	 	  	 {this.final_direct_user_name_email, common_password, direct_org_id, new String[]{this.direct_site_id, this.another_direct_site_id}, this.directGroupName_id, "\"\""},
	 	 };
	 	 	}
	  @Test(dataProvider = "sourceInfo4",enabled=true,  priority = 5)
	  /**
	   * 1. admin could get correct sources by filter with group id != [specified group id] when some sources are in single group
	   * 4. admin could get correct sources by filter with group id != [specified group id] when some sources are in multiple groups
	   * 5. admin could get correct sources by filter with group id != [specified group id] when some sources are not in any group
	   * 3. set group id as random uuid, should returns all sources
	   * 2. set group id as "", should report error
	   * @param email
	   * @param password
	   * @param org_id
	   * @param site_id_array
	   * @param group_id
	   */
	  public void getSourcesNotInSpecifiedGroup(String email, String password, String org_id, String[] site_id_array, String group_id, String filter_Group_id) {
		  
		  	test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  	test.assignAuthor("shuo.zhang");
		  	spogServer.errorHandle.printInfoMessageInDebugFile("/****************getSourcesNotInSpecifiedGroup**************/");
		  
			test.log(LogStatus.INFO,"admin login");
			
		  	spogServer.userLogin(email, common_password);
		
		    String groupName1 = spogServer.ReturnRandom("groupName_shuo");
			String group_id1 = spogServer.createGroupWithCheck(org_id, groupName1, "", test);
		
		    ArrayList<String> allSourcesNodes = new    ArrayList<String> ();
		    ArrayList<HashMap<String, Object>> expectedSourceList = new ArrayList<HashMap<String, Object>> ();
		    String[] filterGroupSourceNodes = new String[2];
		    String[] otherGroupSourceNodes = new String[2];
			for(int i=0; i <4; i++ ){
				String soureceName = spogServer.ReturnRandom("shuo_source");	
				String site_id = site_id_array[i/2];
				Response createSourceResponse = spogServer.createSource(soureceName, SourceType.machine, SourceProduct.udp, org_id, site_id ,  ProtectionStatus.unprotect, 
			  			ConnectionStatus.offline, "", "", "", "", "", "", "", "", "","",test);
				String source_id =  createSourceResponse.then().extract().path("data.source_id");
				allSourcesNodes.add(source_id);
				String[] groupIds=null;
				boolean contains=true;
				if( i==0){
					filterGroupSourceNodes[0]=source_id;
					groupIds= new String[] {group_id};
				}else if (i==1){
					otherGroupSourceNodes[0]=source_id;
					groupIds= new String[] {group_id1};
				}else if(i==2){
					filterGroupSourceNodes[1]=source_id;
					otherGroupSourceNodes[1]=source_id;
					groupIds= new String[] {group_id, group_id1};
				}
				if(!filter_Group_id.equals("\"\"")){
					if(groupIds!=null){
						for(int j=0; j<groupIds.length; j++){
							if(groupIds[j].equalsIgnoreCase(filter_Group_id)){
								contains = false;
								break;
							}
						}
					}
				
				}else{
					if(groupIds==null) contains=false;
					
				}
				if( contains){
					HashMap<String, Object> eachSourceInfo = getExpectedSourceMap(createSourceResponse, site_id, null , null,groupIds, spogServer.getJWTToken(), false, null);			
					expectedSourceList.add(eachSourceInfo);
				}
				/*
				if(groupIds!=null){
					for(int j=0; j<groupIds.length; j++){
						if(groupIds[j].equalsIgnoreCase(filter_Group_id)){
							contains = false;
							break;
						}
					}
				}
				if(groupIds==null || contains){
					HashMap<String, Object> eachSourceInfo = getExpectedSourceMap(createSourceResponse, site_id, null , null,groupIds, spogServer.getJWTToken(), false, null);			
					expectedSourceList.add(eachSourceInfo);
				}*/
				
				
 		  
			}

			spogServer.addSourcetoSourceGroupwithCheck(group_id, filterGroupSourceNodes, spogServer.getJWTToken(), SpogConstants.SUCCESS_POST, null, test);
			spogServer.addSourcetoSourceGroupwithCheck(group_id1, otherGroupSourceNodes, spogServer.getJWTToken(), SpogConstants.SUCCESS_POST, null, test);
		  	
			test.log(LogStatus.INFO,"get sources");	 
			spogServer.errorHandle.printInfoMessageInDebugFile("get sources");
			Response response = spogServer.getSources("group_id;!=;"+ filter_Group_id + ",is_deleted;=;false", null, -1, -1, true, test);
			  	
			Collections.reverse(expectedSourceList);	
		/*	if((filter_Group_id!=null) && !filter_Group_id.equalsIgnoreCase("\"\"")){
				//check result
			  	test.log(LogStatus.INFO,"check get sources result");	 
				spogServer.errorHandle.printInfoMessageInDebugFile("check get sources result");
				spogServer.checkGetSources(response, SpogConstants.SUCCESS_GET_PUT_DELETE, expectedSourceList, -1, -1, -1, null, test);
				
			}else{
				test.log(LogStatus.INFO,"check get sources result");	 
				spogServer.errorHandle.printInfoMessageInDebugFile("check get sources result");
				spogServer.checkGetSources(response, SpogConstants.SUCCESS_GET_PUT_DELETE, new ArrayList<HashMap<String, Object>> (), -1, -1, -1, null, test);
			}*/
			test.log(LogStatus.INFO,"check get sources result");	 
			spogServer.errorHandle.printInfoMessageInDebugFile("check get sources result");
			spogServer.checkGetSources(response, SpogConstants.SUCCESS_GET_PUT_DELETE, expectedSourceList, -1, -1, -1, null, test);
				
				//delete source
				test.log(LogStatus.INFO,"delete source");
				spogServer.errorHandle.printInfoMessageInDebugFile("delete source");
			  	spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);

				for(int i=0; i< allSourcesNodes.size();i++){
					response = spogServer.deleteSourcesById(spogServer.getJWTToken(), allSourcesNodes.get(i), test);
					spogServer.deleteSourcesWithCheck(response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
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



		public HashMap<String, Object> getExpectedSourceMap(Response createSourceResponse, String siteId, String[] policyIds,String[] policyNames,
				String[] groupIds, String token, boolean is_deleted, 	ArrayList<HashMap<String, Object>> lastJobList){
			 
			test.log(LogStatus.INFO,"collect source information");
			spogServer.errorHandle.printInfoMessageInDebugFile("collect source information");
			
			HashMap<String, Object> sourceInfo = new 	HashMap<String,Object>();
			sourceInfo.put("source_id", createSourceResponse.then().extract().path("data.source_id"));
			sourceInfo.put("source_name", createSourceResponse.then().extract().path("data.source_name"));
			sourceInfo.put("vm_name", createSourceResponse.then().extract().path("data.vm_name"));
			sourceInfo.put("source_type", createSourceResponse.then().extract().path("data.source_type"));
			sourceInfo.put("source_product", createSourceResponse.then().extract().path("data.source_product"));
			String org_id =createSourceResponse.then().extract().path("data.organization_id");
			sourceInfo.put("organization_id", org_id);
			sourceInfo.put("organization_name", spogServer.getOrganizationNameByID(org_id));
			
		/*	if((lastJobList!=null) && (lastJobList.get(0).get("status").toString().equalsIgnoreCase(JobStatus.finished.name())||
					lastJobList.get(0).get("status").toString().equalsIgnoreCase(JobStatus.canceled.name())||
							lastJobList.get(0).get("status").toString().equalsIgnoreCase(JobStatus.failed.name()))){
				if((policyIds==null) || (policyIds.equals(""))){
					 sourceInfo.put("protection_status", ProtectionStatus.unprotect.name());
				}else{
					//need modify the code
					 sourceInfo.put("protection_status", createSourceResponse.then().extract().path("data.protection_status")); 
				}
			}else{
				 sourceInfo.put("protection_status", createSourceResponse.then().extract().path("data.protection_status"));
			}*/
			if(lastJobList!=null) {
				if((policyIds==null) || (policyIds.equals(""))){
					 sourceInfo.put("protection_status", ProtectionStatus.unprotect.name());
				}else{
					if(lastJobList.get(0).get("status").toString().equalsIgnoreCase(JobStatus.finished.name())){
						sourceInfo.put("protection_status", ProtectionStatus.protect.name());
					}else{
						 sourceInfo.put("protection_status", ProtectionStatus.unprotect.name());
					}
					 
				}
			}else{
				 sourceInfo.put("protection_status", createSourceResponse.then().extract().path("data.protection_status"));
				// sourceInfo.put("protection_status", ProtectionStatus.unprotect.name());
				 
			}
			
			sourceInfo.put("connection_status", createSourceResponse.then().extract().path("data.connection_status"));
		  	
		  	//site info
			HashMap<String, String> siteMap = new HashMap<String, String>();
		  	Response siteResponse = SiteTestHelper.getSite(siteId,spogServer.getJWTToken() );
		  	siteMap.put("site_id", siteId);
		  	siteMap.put("site_name", siteResponse.then().extract().path("data.site_name"));
		  	sourceInfo.put("site", siteMap);
		  	//policy 
		  	if((policyIds==null) || (policyIds.equals(""))){
		  		sourceInfo.put("policy", null);
		  	}else{
		  		ArrayList<HashMap<String, String>> policyList = new 	ArrayList<HashMap<String, String> >();
		  	  	for(int i=0; i < policyIds.length; i++){
			  		HashMap<String, String> policyMap = new HashMap<String, String>() ;
					policyMap.put("policy_type", null);
			  		policyMap.put("policy_id", policyIds[i]);
			  		policyMap.put("policy_name", policyNames[i]);
			  		policyList.add(policyMap);
			  	}
		  	  sourceInfo.put("policy", policyList);
		  	 
		  	}
		
		  	//last_recovery_point_ts
		  
		  	//last_job

			if(lastJobList==null){
		  		sourceInfo.put("last_job", lastJobList);
				sourceInfo.put("last_recovery_point_ts", null );
		  	}else{

				sourceInfo.put("last_job", lastJobList);
		  		if( createSourceResponse.then().extract().path("data.source_product").toString().equalsIgnoreCase(SourceProduct.udp.name())){
		  			sourceInfo.put("last_recovery_point_ts", lastJobList.get(0).get("start_time_ts") );
		  		}else{
		  			sourceInfo.put("last_recovery_point_ts", null );
		  		}
		  	}
		  	
			//available_actions
			ArrayList<String> availabeActionsArray = new ArrayList<String>();
			if(createSourceResponse.then().extract().path("data.source_product").toString().equalsIgnoreCase("udp")){
				availabeActionsArray.add("delete");
				availabeActionsArray.add("startrecovery");
				availabeActionsArray.add("cancelrepliationin");
			//	availabeActionsArray.add("remove");
			}else{
				if((policyIds!=null) && (!policyIds.equals(""))){
					availabeActionsArray.add("startbackup");
					availabeActionsArray.add("cancelbackup");
				}
				availabeActionsArray.add("startrecovery");
				availabeActionsArray.add("cancelrecovery");
				availabeActionsArray.add("delete");
			//	availabeActionsArray.add("upgradeagent");
				if(createSourceResponse.then().extract().path("data.source_type").equals(SourceType.machine.name())){
					availabeActionsArray.add("assignpolicy");
				}
				if((policyIds!=null) && !policyIds.equals("")){
					availabeActionsArray.add("unassignpolicy");
				}
			
				
			//	availabeActionsArray.add("remove");

			}
			

			sourceInfo.put("available_actions", availabeActionsArray);
		  	//group info
			ArrayList<HashMap<String, String>> groupList = new 	ArrayList<HashMap<String, String> >();
			if((groupIds==null) || (groupIds.equals(""))){
		  		sourceInfo.put("source_group", groupList);
		  	}else{
		  	
		  	  	for(int i=0; i <=groupIds.length-1; i++){
			  		HashMap<String, String> groupMap = new HashMap<String, String>() ;
			  		groupMap.put("group_id", groupIds[i]);
			  		String groupName = spogServer.getGroupById(token, groupIds[i], test).then().extract().path("data.group_name");
			  		groupMap.put("group_name", groupName);
			  		groupList.add(groupMap);
			  	}
		  	  sourceInfo.put("source_group", groupList);
		  	}
			
			//assured_recovery_job
			sourceInfo.put("assured_recovery_job", null);
			//agent
			HashMap<String, String> agentInfo = new HashMap<String, String>();
			agentInfo.put("agent_name", createSourceResponse.then().extract().path("data.agent.agent_name"));
			agentInfo.put("agent_current_version", createSourceResponse.then().extract().path("data.agent.agent_current_version"));
			agentInfo.put("agent_upgrade_version", createSourceResponse.then().extract().path("data.agent.agent_upgrade_version"));
			agentInfo.put("agent_upgrade_link", createSourceResponse.then().extract().path("data.agent.agent_upgrade_link"));
			sourceInfo.put("agent", agentInfo);
			//hypervisor
			
			if(createSourceResponse.then().extract().path("data.hypervisor")==null){
				sourceInfo.put("hypervisor", null);
			}else{
				HashMap<String, String> hypervisorInfo = new HashMap<String, String>();
				hypervisorInfo.put("hypervisor_id",  createSourceResponse.then().extract().path("data.hypervisor.hypervisor_id"));
				hypervisorInfo.put("hypervisor_name", createSourceResponse.then().extract().path("data.hypervisor.hypervisor_name"));
				sourceInfo.put("hypervisor", hypervisorInfo);
			}

			
			//ptc_status
			sourceInfo.put("ptc_status", null);
			//operating_system
			HashMap<String, String> operatingSystemInfo = new HashMap<String, String>();
			operatingSystemInfo.put("os_major", createSourceResponse.then().extract().path("data.operating_system.os_major"));
			operatingSystemInfo.put("os_name", createSourceResponse.then().extract().path("data.operating_system.os_name"));
			operatingSystemInfo.put("os_architecture", createSourceResponse.then().extract().path("data.operating_system.os_architecture"));
			sourceInfo.put("operating_system", operatingSystemInfo);
			//create_ts
			sourceInfo.put("create_ts", null);
			//modify_ts
			sourceInfo.put("modify_ts", null);
			//is_deleted
			sourceInfo.put("is_deleted", is_deleted);
			//multiple_policy_support
			sourceInfo.put("multiple_policy_support", false);
			sourceInfo.put("applications", createSourceResponse.then().extract().path("data.applications"));
			sourceInfo.put("create_user_id", createSourceResponse.then().extract().path("data.create_user_id"));
			//hidden
			sourceInfo.put("is_hidden", false);
			sourceInfo.put("have_configuration", false);
			sourceInfo.put("enable_draas", false);
		  	return sourceInfo;
	 }
		
		private ArrayList<HashMap<String, Object>> getLastJob(Object jobType, String jobSeverity, String jobStatus, long start_time_ts, long endTimeTS, double percent){
			/*	String patten = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
				TimeZone utcZone = TimeZone.getTimeZone("UTC");

				SimpleDateFormat dateFormate = new SimpleDateFormat(patten);
				dateFormate.setTimeZone(utcZone);*/
				ArrayList<HashMap<String, Object>> lastJobList = new ArrayList<HashMap<String, Object>>();
				HashMap<String, Object> lastJobHashMap = new  HashMap<String, Object>();
				lastJobHashMap.put("type", jobType);
				lastJobHashMap.put("severity", jobSeverity);
				lastJobHashMap.put("status", jobStatus);
				lastJobHashMap.put("start_time_ts",start_time_ts);
				lastJobHashMap.put("end_time_ts", endTimeTS);
				lastJobHashMap.put("percent_complete", percent);
				lastJobList.add(lastJobHashMap);
				return lastJobList;
			}



}
