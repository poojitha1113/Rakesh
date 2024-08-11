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

public class GetSourcesTest  extends base.prepare.PrepareOrgInfo{
	
	@Parameters({ "pmfKey"})
	public GetSourcesTest(String pmfKey) {
		super(pmfKey);
		// TODO Auto-generated constructor stub
	}

	
	private SPOGServer spogServer;
	  private UserSpogServer userSpogServer;
	  private GatewayServer gatewayServer;
	  private SPOGDestinationServer spogDestinationServer;
	  Policy4SPOGServer policy4SPOGServer;
	  private Org4SPOGServer org4SpogServer;
	  private String csrAdminUserName;
	  private String csrAdminPassword;
	  private String csrReadOnlyAdminUserName;
	  private String csrReadOnlyAdminPassword;
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
	private String root_msp_direct_org_destination_id;

	  
	  @BeforeClass
	  @Parameters({ "baseURI", "port", "logFolder", "csrAdminUserName", "csrAdminPassword", "buildVersion", "csrReadOnlyAdminUserName","csrReadOnlyAdminPassword"})
	  public void beforeClass(String baseURI, String port, String logFolder, String adminUserName, String adminPassword ,  String buildVersion, String csrROAdminUserName, String csrROPwd ) throws UnknownHostException {
				
		
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
		  	 this.csrReadOnlyAdminUserName = csrROAdminUserName;
			  this.csrReadOnlyAdminPassword = csrROPwd;
		    test = rep.startTest("beforeClass");
		
		    prepareEnv();
		    prepare(baseURI, port, logFolder, this.csrAdminUserName,  this.csrAdminPassword, this.getClass().getSimpleName() );
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
	 
	
	  
	  @DataProvider(name = "sourceInfo")
	  public final Object[][] getSourceInfo() {
		  System.out.println("dataprovide");
	 	 return new Object[][]   {
	 		 			{final_direct_user_name_email,common_password, SourceType.machine,  SourceProduct.udp, direct_org_id, direct_site_id+";"+another_direct_site_id, 
	 		 				ProtectionStatus.unprotect, ConnectionStatus.online,OSMajor.windows.name(),"", direct_user_id, directGroupName_id, directGroupName,  SpogConstants.DIRECT_ORG,
	 		 				null, null, spogServer.ReturnRandom("shuo_vm"),direct_hypervisor_id,spogServer.ReturnRandom("shuo_agent"),"windows 2012", "64",
	 		 				"1.1.0", "2.0.0", "http://upgrade" },
	 		 			{final_msp_user_name_email, common_password, SourceType.machine,  SourceProduct.udp,  account_id, account_site_id+";"+ another_account_site_id, ProtectionStatus.unprotect, ConnectionStatus.offline,
			 					OSMajor.windows.name(),	"exchange",  msp_user_id, this.accountGroupName_id, this.accountGroupName, SpogConstants.MSP_SUB_ORG,  account_id, account_site_id+";"+ another_account_site_id,
			 				spogServer.ReturnRandom("shuo_vm"),null,spogServer.ReturnRandom("shuo_agent"),"windows 2008", "32",
			 				"1.1.0", "2.0.0", null},	 		 			
	 		 			{account_user_email, common_password, SourceType.shared_folder,  SourceProduct.udp,  account_id, account_site_id+";"+ another_account_site_id,
	 		 				ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(),	"sql;exchange", account_user_id,  null, null, SpogConstants.MSP_SUB_ORG,
	 		 				null, null, null, null, null, null, null, null,null, null},
	 		 				
	 		 	    {final_msp_account_admin_email, common_password, SourceType.shared_folder,  SourceProduct.udp,  account_id, account_site_id+";"+ another_account_site_id,
	 				ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(),	"sql;exchange", msp_account_admin_id,  null, null, SpogConstants.MSP_SUB_ORG,
	 				null, null, null, null, null, null, null, null,null, null},
	 		 	
	 		{this.final_root_msp_user_name_email,common_password, SourceType.machine,  SourceProduct.udp, root_msp_direct_org_id, root_msp_direct_org_site1_siteId, 
 				ProtectionStatus.unprotect, ConnectionStatus.online,OSMajor.windows.name(),"", root_msp_direct_org_user_id, null, null,  SpogConstants.DIRECT_ORG,
 				null, null, spogServer.ReturnRandom("shuo_vm"),null,spogServer.ReturnRandom("shuo_agent"),"windows 2012", "64",
 				"1.1.0", "2.0.0", "http://upgrade" },
	 		{this.final_root_msp_account_admin_user_name_email,common_password, SourceType.machine,  SourceProduct.udp, root_msp_direct_org_id, root_msp_direct_org_site1_siteId, 
 	 				ProtectionStatus.unprotect, ConnectionStatus.online,OSMajor.windows.name(),"", root_msp_direct_org_user_id, null, null,  SpogConstants.DIRECT_ORG,
 	 				null, null, spogServer.ReturnRandom("shuo_vm"),null,spogServer.ReturnRandom("shuo_agent"),"windows 2012", "64",
 	 				"1.1.0", "2.0.0", "http://upgrade" },
	 		{final_root_msp_direct_org_user_email,common_password, SourceType.machine,  SourceProduct.udp, root_msp_direct_org_id, root_msp_direct_org_site1_siteId, 
	 				ProtectionStatus.unprotect, ConnectionStatus.online,OSMajor.windows.name(),"", root_msp_direct_org_user_id, null, null,  SpogConstants.DIRECT_ORG,
	 				null, null, spogServer.ReturnRandom("shuo_vm"),null,spogServer.ReturnRandom("shuo_agent"),"windows 2012", "64",
	 				"1.1.0", "2.0.0", "http://upgrade" },
	 		{final_sub_msp1_user_name_email,common_password, SourceType.machine,  SourceProduct.udp, sub_msp1_account1_id, sub_msp1_account1_site1_siteId, 
		 				ProtectionStatus.unprotect, ConnectionStatus.online,OSMajor.windows.name(),"", root_msp_direct_org_user_id, null, null,  SpogConstants.DIRECT_ORG,
		 				null, null, spogServer.ReturnRandom("shuo_vm"),null,spogServer.ReturnRandom("shuo_agent"),"windows 2012", "64",
		 				"1.1.0", "2.0.0", "http://upgrade" },
	 		{final_sub_msp1_msp_account_user_name_email,common_password, SourceType.machine,  SourceProduct.udp, sub_msp1_account1_id, sub_msp1_account1_site1_siteId, 
			 				ProtectionStatus.unprotect, ConnectionStatus.online,OSMajor.windows.name(),"", root_msp_direct_org_user_id, null, null,  SpogConstants.DIRECT_ORG,
			 				null, null, spogServer.ReturnRandom("shuo_vm"),null,spogServer.ReturnRandom("shuo_agent"),"windows 2012", "64",
			 				"1.1.0", "2.0.0", "http://upgrade" },
	 		{final_sub_msp1_account1_user_email,common_password, SourceType.machine,  SourceProduct.udp, sub_msp1_account1_id, sub_msp1_account1_site1_siteId, 
				 				ProtectionStatus.unprotect, ConnectionStatus.online,OSMajor.windows.name(),"", root_msp_direct_org_user_id, null, null,  SpogConstants.DIRECT_ORG,
				 				null, null, spogServer.ReturnRandom("shuo_vm"),null,spogServer.ReturnRandom("shuo_agent"),"windows 2012", "64",
				 				"1.1.0", "2.0.0", "http://upgrade" },
	 		 	
	 	 };
	 	 	}

  /**
   * @testcase:2. direct admin could get all sources in its organization
   * 3. msp admin could get all sources in its organization 
   * 34. msp admin could get all sources in its child organization
   * 4. account admin could get all sources in its organization
   * 17. check the default page number is 1
   * 18. check the default page size is 20
   * 27. check agent info in response
   * 39. filter by is_deleted
   * 30. get source will include deleted source
   * 1. availabe actions should be showed

   * 
   * 30. MSP account admin could get sources for mastered account
   * 
   * root msp admin could get its direct org's source
   * root msp msp account admin could get its direct org's source
   * root msp direct org's admin could get its source
   * 
   * sub msp1 admin could get it account1's source
   * sub msp1 msp account admin could get it account1's source
   * account1 could get its source
   * 
   * 

   * @param userName
   * @param password
   * @param sourceType
   * @param sourceProduct
   * @param org_id
   * @param siteId
   * @param protectionStatus
   * @param connectionStatus
   * @param os_major
   * @param applications
   * @param create_user_id
   * @param group_id
   * @param group_name
   */
  @Test(dataProvider = "sourceInfo",enabled=true, priority = 0)
  public void getSources(String userName, String password, SourceType sourceType, SourceProduct sourceProduct, String org_id, String siteIds, 
		  ProtectionStatus protectionStatus,  ConnectionStatus connectionStatus, String os_major, String applications,  String create_user_id, 
		  String group_id, String group_name, String orgType, String account_id, String account_site_ids,String vm_name ,String hypervisor_id ,
		  String agent_name ,String os_name , String os_architecture ,String agent_current_version ,String agent_upgrade_version ,String agent_upgrade_link) {
	  
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		test.assignAuthor("shuo.zhang");
	  	spogServer.errorHandle.printInfoMessageInDebugFile("/****************getSources**************/");
	  
	  
		test.log(LogStatus.INFO,"admin login");
		spogServer.errorHandle.printInfoMessageInDebugFile("admin login");
	  	spogServer.userLogin(userName, password);
	  
	  	ArrayList<String> allSourcesNodes = new    ArrayList<String> ();
	  	ArrayList<HashMap<String, Object> > expectedSourceList= new ArrayList<HashMap<String, Object> >();
	  	String[] arrayOfSourceNodes= new String[3];
	    String[] siteIdArray = siteIds.split(";");
	  	for(int i=0; i<=2; i++){
	  		String soureceName = spogServer.ReturnRandom("shuo_source");	  	
			test.log(LogStatus.INFO,"create source ");
			spogServer.errorHandle.printInfoMessageInDebugFile("create source");
			int randomNum = ThreadLocalRandom.current().nextInt(0, siteIdArray.length);
			String siteId = siteIdArray[randomNum];		

		  	Response createSourceResponse = spogServer.createSource(soureceName, sourceType, sourceProduct, org_id, siteId,  protectionStatus, 
		  			connectionStatus, os_major, applications, vm_name, hypervisor_id, agent_name, os_name, os_architecture, agent_current_version, 
		  			agent_upgrade_version, agent_upgrade_link, test);
		 
		
			String[] policyIds = null;
			String[] groupIds = null;
			if(group_id!=null){
				groupIds = new String[]{group_id};
			}
			HashMap<String, Object> eachSourceInfo = getExpectedSourceMap(createSourceResponse, siteId, policyIds, null, groupIds, spogServer.getJWTToken(), false,null);
			expectedSourceList.add(eachSourceInfo);
			String source_id =  createSourceResponse.then().extract().path("data.source_id");
	    	allSourcesNodes.add(source_id);
			arrayOfSourceNodes[i] = source_id;	
					  		  	
	  	}
		  //add source to group
	  	if((group_id!=null) && (!group_id.equals(""))){
			test.log(LogStatus.INFO,"add source to group");
			spogServer.errorHandle.printInfoMessageInDebugFile("add source to group");
			spogServer.addSourcetoSourceGroupwithCheck(group_id, arrayOfSourceNodes, spogServer.getJWTToken(), SpogConstants.SUCCESS_POST, null, test);
	  	}
		
	  	
/*	  	if(orgType.equals(SpogConstants.MSP_ORG)){
	  		String[] accountSiteIdArray = account_site_ids.split(";");
	  	  	for(int i=0; i<2; i++){
		  		String soureceName = spogServer.ReturnRandom("shuo_source");	  	
				test.log(LogStatus.INFO,"create source in account and check");
				spogServer.errorHandle.printInfoMessageInDebugFile("create source in account and check");
				int randomNum = ThreadLocalRandom.current().nextInt(0, accountSiteIdArray.length);
				String siteId = accountSiteIdArray[randomNum];		
			  	Response createSourceResponse = spogServer.createSource(soureceName, sourceType, sourceProduct, account_id, siteId,  protectionStatus, 
			  			connectionStatus, os_major, applications, vm_name, hypervisor_id, agent_name, os_name, os_architecture, agent_current_version, 
			  			agent_upgrade_version, agent_upgrade_link, test);
			  	String[] policyIds = null;
				String[] groupIds = null;
				HashMap<String, Object> eachSourceInfo = this.getExpectedSourceMap(createSourceResponse, siteId, policyIds, null, groupIds, spogServer.getJWTToken(), false,null);
				expectedSourceList.add(eachSourceInfo);
				arrayOfSourceNodes[i] = createSourceResponse.then().extract().path("data.source_id");
				String source_id =  createSourceResponse.then().extract().path("data.source_id");
		    	allSourcesNodes.add(source_id);
		  	}
	  	}
	  	*/
	  	
	  	
		test.log(LogStatus.INFO,"get sources ");
		spogServer.errorHandle.printInfoMessageInDebugFile("get sources ");
	  	Response response = spogServer.getSources("is_deleted;=;false", null,-1, -1, true, test);
	  	
	  	Collections.reverse(expectedSourceList);	  	
		spogServer.checkGetSources(response, SpogConstants.SUCCESS_GET_PUT_DELETE, expectedSourceList, -1, -1, -1, null, test);
		
	
		test.log(LogStatus.INFO,"delete source");
		spogServer.errorHandle.printInfoMessageInDebugFile("delete source");
		for(int i=0; i< allSourcesNodes.size();i++){
			response = spogServer.deleteSourcesById(spogServer.getJWTToken(), allSourcesNodes.get(i), test);
			spogServer.deleteSourcesWithCheck(response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		}
		
		if(org_id.equals(direct_org_id)){
			response = spogServer.getSources("", null, -1, -1, true, test);

			ArrayList<String> availabeActionsArray = new ArrayList<String>();
			if(!sourceProduct.name().toString().equalsIgnoreCase("udp")){
				availabeActionsArray.add("remove");
			}
			availabeActionsArray.add("startrecovery");
			
			for(int i=0; i< expectedSourceList.size(); i++){
				expectedSourceList.get(i).put("is_deleted", true);
				expectedSourceList.get(i).put("available_actions", availabeActionsArray);
				expectedSourceList.get(i).put("connection_status", connectionStatus.toString());
				
			}
			spogServer.checkGetSources(response, SpogConstants.SUCCESS_GET_PUT_DELETE, expectedSourceList, -1, -1, -1, null, test);
		}
		
	
  }
  
  @DataProvider(name = "sourceInfo1")
  public final Object[][] getSourceInfo1() {
 	 return new Object[][]   {
 		 			
 		 			{"organization_id;=;direct"},
 		 			{"organization_id;in;msp_child|direct" },
 					
 		 			{"connection_status;=;" + ConnectionStatus.offline},
 		 			{"connection_status;in;" + ConnectionStatus.offline + "|" + ConnectionStatus.online},
 		 			{"site_id;=;"+ direct_site_id},
 		 			{"site_id;in;"+ direct_site_id + "|" + another_direct_site_id},
 		 			{"group_id;=;"+ directGroupName_id},
 		 			{"group_id;in;"+ directGroupName_id + "|" + accountGroupName_id},				
 		 			{"operating_system;=;linux"}, 
 		 			{"operating_system;in;linux|mac"},	
 		 			{"source_type;=;"+ SourceType.virtual_standby}, 
 		 			{"source_name;=;shuo"}	,		
 		//
 		 			/*		{"protection_status;=;" + ProtectionStatus.protect.name()},
		 			{"protection_status;in;" + ProtectionStatus.protect.name()+"|"+ ProtectionStatus.unprotect.name() },
		 			
 		 		    {"last_job;=;finished"},
 		 			{"last_job;in;finished|failed"}, 	
 		 			{"policy_id;=;"+ UUID.randomUUID().toString()},	
 		 			{"policy_id;in;"+ UUID.randomUUID().toString() + "|" + UUID.randomUUID().toString() }, 		
		 			*
		 			*/
		 			//	{"source_type;in;"+ SourceType.virtual_standby + "|" +  SourceType.machine  },
		 		  
 	 };
 	 	}
  
  /**
   * @author shuo.zhang
   * @test
   * 1. csr admin could get  sources in direct org
   * 37. csr admin could get  sources in msp org
   * 38. csr admin could get sources in account
   * 
   * 5. filter by orgnanization_id=X
   * 6.  filter by orgnanization_id In [X|Y]
   * 7. filter by protection_status=X
   * 8.  filter by protection_status in [X|Y]
   * 9. filter by connection_status=X
   * 10. filter by connection_status in [X|Y]
   * 11. filter by site_id=X
   * 12. filter by site_id in [X|Y]
   * 13. filter by group_id=X
   * 14. filter by group_id in [X|Y]
   * 15. filter by source name=X
   * 28. filter by os_major =X
   * 29. filter by os_major in X|Y
   * 33. filter by source_type = X
   * 34. filter by source_type in X|Y
   * 35. filter by source name, it should be case insensitive
   * 36. filter by source name, it should be wildcard search
   * 37. filter by last_backup_status=X
   * 38. filter by last_backup_status in X|Y
   * 26. check last job field in response
   * 1. Minimum length for all string searches will be 3 chars for source name 
   * Get sources filter by source name is treated as fuzzy search
   * 1. admin could get sources by filter with group id != [specified group id]
   * 
   * 31. filter by policy_id = X
   * 32. filter by policy_id in X|Y
   * @param filterStr
   */
  @Test(dataProvider = "sourceInfo1",enabled=false,  priority = 1)
  public void getSourcesWithFilter(String filterStr) {
	  
	  	test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
	  	test.assignAuthor("shuo.zhang");
	  	spogServer.errorHandle.printInfoMessageInDebugFile("/****************getSourcesWithFilter**************/");
	  
		test.log(LogStatus.INFO,"admin login");
		spogServer.errorHandle.printInfoMessageInDebugFile("csr admin login");
	  	spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
	  	String user_id = spogServer.GetLoggedinUser_UserID();
	  	
	  	test.log(LogStatus.INFO,"create source in direct org"); 	
		spogServer.errorHandle.printInfoMessageInDebugFile("create source in direct org");
		
	 
	  	String[] filterArray = filterStr.split(";");
	  	String filterName = filterArray[0];
	 	String filterOperator = filterArray[1];
	 	String filterValue = filterArray[2];
	 	String[] filterValuesArray = filterValue.split("\\|");
	    String[] directSiteIdArray = new String[]{direct_site_id, another_direct_site_id};
	    String[] arrayOfSourceNodes = new String[2];
	    ArrayList<String> allSourcesNodes = new    ArrayList<String> ();
	    String[] osArray = new String[]{"linux", "mac"};
	    String[] jobStatusArray = new String[]{"finished", "failed"};
		ArrayList<HashMap<String, Object>> expectedSourceList = new ArrayList<HashMap<String, Object>> ();
		ArrayList<HashMap<String, Object>> lastJobList =null;
		HashMap<String, Object> lastJobHashMap =null;
		for(int i=0; i<directSiteIdArray.length;i++){
	    	String soureceName = spogServer.ReturnRandom("shuo_source");
	    	Response createSourceResponse = null;
	    	String  siteId=directSiteIdArray[i];
	    	String  os_major = osArray[i];
	    	String source_id =null;
	    	String policy_id= null;
	    	String policyName=null;
	    	if(!filterName.equalsIgnoreCase("last_job")&& !filterName.equalsIgnoreCase("policy_id") && !filterName.equalsIgnoreCase("protection_status")){
	    		createSourceResponse = spogServer.createSource(soureceName,SourceType.values()[i], SourceProduct.cloud_direct, direct_org_id, siteId,  ProtectionStatus.unprotect, 
		    			ConnectionStatus.values()[i], os_major, null, null, null, null, null, null, null, null, null, test);
		    	source_id =  createSourceResponse.then().extract().path("data.source_id");
	    	}
	    	
	    	if(filterName.equalsIgnoreCase("last_job")|| filterName.equalsIgnoreCase("policy_id")||  filterName.equalsIgnoreCase("protection_status")){
	    		
	    		createSourceResponse = spogServer.createSource(soureceName,SourceType.machine, SourceProduct.cloud_direct, direct_org_id, siteId,  ProtectionStatus.unprotect, 
		    			ConnectionStatus.online, os_major, null, null, null, null, null, null, null, null, null, test);
		    	source_id =  createSourceResponse.then().extract().path("data.source_id");
	    		  String pmfKey="shuo";
	    		  if(filterName.equalsIgnoreCase("last_job")|| filterName.equalsIgnoreCase("protection_status")){
	    			  policy_id=spogServer.returnRandomUUID();
	    		  }else{
	    			  if(filterOperator.equals("=")){
	    				  if(i!=0){
	    					  policy_id=spogServer.returnRandomUUID();
	    				  }else{
	    					  policy_id = filterValuesArray[i];
	    				  }
	    			  }else{
	    				  policy_id = filterValuesArray[i];
	    			  }
	    			
	    		  }
	    		 
	    		  String task_id=spogServer.returnRandomUUID();	
	    		  HashMap<String, Object> cloudDirectScheduleDTO=policy4SPOGServer.createCloudDirectScheduleDTO("0 6 * * *", test);
		   		  HashMap<String, Object> scheduleSettingDTO=policy4SPOGServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null,test);
		   		  ArrayList<HashMap<String,Object>> schedules =policy4SPOGServer.createPolicyScheduleDTO(null,spogServer.returnRandomUUID(), 
		   				  "1d", task_id, destinationMap.get(siteId), scheduleSettingDTO, "06:00", "12:00", "cloud_direct_file_folder_backup" , "dest",test);
		     		  
		   		  
		   		  ArrayList<HashMap<String,Object>> excludes=policy4SPOGServer.createExcludeInfoDTO(null, "path", "c:\\tmp", test);
		   		  HashMap<String, Object> cloudDirectLocalBackupDTO=policy4SPOGServer.createCloudDirectLocalBackupDTO("d:\\tmp", "true" , test);
		   		  HashMap<String, Object> cloudDirectFileBackupTaskInfoDTO=policy4SPOGServer.createCloudDirectFileBackupTaskInfoDTO("d:\\tmp", cloudDirectLocalBackupDTO, excludes,test);
		   		  ArrayList<HashMap<String,Object>>  destinations= policy4SPOGServer.createPolicyTaskDTO(null, task_id, "cloud_direct_file_folder_backup", destinationMap.get(siteId), "none", null, cloudDirectFileBackupTaskInfoDTO, null, test);
		   		  ArrayList<HashMap<String,Object>> throttles =policy4SPOGServer.createPolicyThrottleDTO(null, spogServer.returnRandomUUID(), task_id, "network", "1200", "1", "06:00", "18:00", "cloud_direct_file_folder_backup" , destinationMap.get(siteId),"dest",test);

		   		  policy4SPOGServer.setToken(spogServer.getJWTToken());
		   		  //create cloud direct policy
		   		  policyName = spogServer.ReturnRandom(pmfKey);
		   		  Response response=policy4SPOGServer.createPolicy(policyName, spogServer.ReturnRandom(pmfKey), "cloud_direct_baas", null, true,  source_id, destinations, schedules, throttles, policy_id, direct_org_id, test);
		   		//  policy4SPOGServer.checkPolicyDestinations(response,SpogConstants.SUCCESS_POST,destinations,test);

	    			long start_time_ts = System.currentTimeMillis()/1000;
					String server_id = UUID.randomUUID().toString();			
					String rps_id = UUID.randomUUID().toString();
				//	String datastore_id = UUID.randomUUID().toString();
					long endTimeTS = System.currentTimeMillis()/1000;
					String post_job_id = null;
					if(filterName.equalsIgnoreCase("protection_status")){
						post_job_id = gatewayServer.postJobWithCheck(start_time_ts,endTimeTS, direct_org_id, server_id, source_id, rps_id, destinationMap.get(siteId), policy_id,
			    				"backup", "full", "finished", siteInfo.get(siteId), test);
						lastJobList = this.getLastJob(JobType4LatestJob.backup_full, null, "finished", start_time_ts, endTimeTS, 0.0,post_job_id);
					}else{
						post_job_id=gatewayServer.postJobWithCheck(start_time_ts,endTimeTS, direct_org_id, server_id, source_id, rps_id, destinationMap.get(siteId), policy_id,
			    				"backup", "full", jobStatusArray[i], siteInfo.get(siteId), test);
						lastJobList = this.getLastJob(JobType4LatestJob.backup_full, null, jobStatusArray[i], start_time_ts, endTimeTS, 0.0, post_job_id);
					}
		    		
					
				
	    
	    	}
	  	    	
	    	if((filterName.equalsIgnoreCase("organization_id") && filterValue.contains(SpogConstants.DIRECT_ORG))||
	    			(filterName.equalsIgnoreCase("protection_status")&& ArrayUtils.contains(filterValuesArray, ProtectionStatus.protect.name()) )||
	    			(filterName.equalsIgnoreCase("connection_status")&& ArrayUtils.contains(filterValuesArray, ConnectionStatus.values()[i].name()))||
	    			 filterName.equalsIgnoreCase("site_id") && filterValue.contains(siteId)||
	    			 filterName.equalsIgnoreCase("group_id") && filterValue.contains(directGroupName_id)||
	    			 filterName.equalsIgnoreCase("source_name") && soureceName.contains(filterValue)||
	    			 filterName.equalsIgnoreCase("operating_system") && filterValue.contains(os_major)||
	    			 filterName.equalsIgnoreCase("source_type") && filterValue.contains(SourceType.values()[i].name())||
	    			 filterName.equalsIgnoreCase("last_job") && filterValue.contains( jobStatusArray[i])||
	    			 filterName.equalsIgnoreCase("policy_id") && filterValue.contains(policy_id)
	    			)
	    	{
	    		String[] policyIds = null;
	    		String[] policyNames = null;
	    		if(filterName.equalsIgnoreCase("last_job")||filterName.equalsIgnoreCase("policy_id")||filterName.equalsIgnoreCase("protection_status")){
	    			policyIds= new String[1];
	    			policyIds[0]= policy_id;
	    			policyNames= new String[1];
	    			policyNames[0]=policyName;
	    		}
				
				String[] groupIds = new String[] {directGroupName_id};
				
				HashMap<String, Object> eachSourceInfo = getExpectedSourceMap(createSourceResponse, siteId, policyIds , policyNames, groupIds, spogServer.getJWTToken(), false, lastJobList);			
				expectedSourceList.add(eachSourceInfo);
 		  
			  	
	    	}
	    	
	    	arrayOfSourceNodes [i] =source_id;	
	    	allSourcesNodes.add(source_id);
	    }
	    spogServer.addSourcetoSourceGroupwithCheck(directGroupName_id, arrayOfSourceNodes, spogServer.getJWTToken(), SpogConstants.SUCCESS_POST, null, test);
	    
	    if(!filterName.equalsIgnoreCase("organization_id")&&!filterName.equalsIgnoreCase("site_id")){
	    	String soureceName = spogServer.ReturnRandom("liang_source");
	    	Response createSourceResponse = spogServer.createSource(soureceName, SourceType.machine, SourceProduct.udp, direct_org_id, directSiteIdArray[0],  null, 
		  			null, OSMajor.windows.name(), null,  test);	
	    	String source_id =  createSourceResponse.then().extract().path("data.source_id");
	    	allSourcesNodes.add(source_id);
			
	    	if(filterName.equalsIgnoreCase("protection_status")&& ArrayUtils.contains(filterValuesArray, ProtectionStatus.unprotect.name())){
	    		HashMap<String, Object> eachSourceInfo = getExpectedSourceMap(createSourceResponse, directSiteIdArray[0], null , null, null, spogServer.getJWTToken(), false, null);			
				expectedSourceList.add(eachSourceInfo);
	    	}
	    	
	    }
	        
/*	  	test.log(LogStatus.INFO,"create source in msp org");	 
		spogServer.errorHandle.printInfoMessageInDebugFile("create source in msp org");
	    String[] mspSiteIdArray =  new String[]{msp_site_id, another_msp_site_id};
	    for(int i=0; i<mspSiteIdArray.length;i++){
	    	String soureceName = spogServer.ReturnRandom("shuo_source");
	    	Response createSourceResponse = spogServer.createSource(soureceName, SourceType.machine, SourceProduct.udp, msp_org_id, mspSiteIdArray[i],  ProtectionStatus.protect, 
		  			ConnectionStatus.offline, "windows", null, null, null, null, null, null, null, null, null, test);
	    				
			if(filterName.equals("organization_id") && filterValue.contains(SpogConstants.MSP_ORG)||
					 filterName.equalsIgnoreCase("group_id") && filterValue.contains(mspGroupName_id)
					){
				test.log(LogStatus.INFO,"filter is organization_id");
				spogServer.errorHandle.printInfoMessageInDebugFile("filter is organization_id");
				
				String[] policyIds = null;
				String[] groupIds = new String[] {mspGroupName_id};
				HashMap<String, Object> eachSourceInfo = getExpectedSourceMap(createSourceResponse, mspSiteIdArray[i], policyIds , null,  groupIds, spogServer.getJWTToken(), false, null);			
				expectedSourceList.add(eachSourceInfo);
				
			}
			String source_id =  createSourceResponse.then().extract().path("data.source_id");
	    	allSourcesNodes.add(source_id);
			arrayOfSourceNodes [i] = source_id;	
	    }
	    spogServer.addSourcetoSourceGroupwithCheck(mspGroupName_id, arrayOfSourceNodes, spogServer.getJWTToken(), SpogConstants.SUCCESS_POST, null, test);*/
	   
	    test.log(LogStatus.INFO,"create source in msp sub org");	 
		spogServer.errorHandle.printInfoMessageInDebugFile("create source in msp sub org");
		 String[] accountSiteIdArray =  new String[]{this.account_site_id, this.another_account_site_id};
		    for(int i=0; i<accountSiteIdArray.length;i++){
		    	
		    	 String soureceName = spogServer.ReturnRandom("shuo_source");
		 	     Response createSourceResponse = spogServer.createSource(soureceName, SourceType.machine, SourceProduct.udp, account_id, accountSiteIdArray[i],  ProtectionStatus.unprotect, 
		 		  			ConnectionStatus.offline, "windows", null,  test);
  				
		 		if(filterName.equals("organization_id") && filterValue.contains(SpogConstants.MSP_SUB_ORG)||
						 filterName.equalsIgnoreCase("group_id") && filterValue.contains(accountGroupName_id)
						){
					test.log(LogStatus.INFO,"filter is organization_id");
					spogServer.errorHandle.printInfoMessageInDebugFile("filter is organization_id");
					
					String[] policyIds = null;
					String[] groupIds = new String[] {accountGroupName_id};
					HashMap<String, Object> eachSourceInfo = getExpectedSourceMap(createSourceResponse, accountSiteIdArray[i], policyIds , null,  groupIds, spogServer.getJWTToken(), false, null);			
					expectedSourceList.add(eachSourceInfo);
					
				}
				String source_id =  createSourceResponse.then().extract().path("data.source_id");
		    	allSourcesNodes.add(source_id);
				arrayOfSourceNodes [i] = source_id;	
				
	
		    }
		 
		    spogServer.addSourcetoSourceGroupwithCheck(accountGroupName_id, arrayOfSourceNodes, spogServer.getJWTToken(), SpogConstants.SUCCESS_POST, null, test);
		 
	   
	   
    
	
		//get filter str
    	
    	
    	
    	
		test.log(LogStatus.INFO,"get filter");	
		String newFilterStr = filterStr;
		spogServer.errorHandle.printInfoMessageInDebugFile("get filter");
		if(filterName.equalsIgnoreCase("organization_id")){
			if(filterOperator.equals("=")){
				if(filterValue.equals(SpogConstants.DIRECT_ORG)){
					  // newFilterStr = filterName + ";"+ filterOperator + ";" + direct_org_id;
					filterValue = direct_org_id;
				}
				
			}else{
				//in 
				newFilterStr = filterName + ";"+ filterOperator + ";";
				if(filterValue.contains(SpogConstants.DIRECT_ORG)&& filterValue.contains(SpogConstants.MSP_SUB_ORG)){
					  // newFilterStr+=  direct_org_id + "|" + msp_org_id ;
					filterValue = direct_org_id + "|" + account_id ;
				}
				
			}			
		}else if(filterName.equalsIgnoreCase("source_name")){
			//newFilterStr = filterName + ";"+ filterOperator + ";"+ filterValue.toUpperCase();
			filterValue = filterValue.toUpperCase() ;
			
		}/*else{
			if(filterOperator.equals("in")){
				newFilterStr = filterName + ";"+ filterOperator + ";["+ filterValue+ "]";

			}			
		}*/
		
		String sourceFilterStr= getSourceFilterStr(user_id, filterName, filterOperator, filterValue);
		sourceFilterStr+=",is_deleted;=;false";
		Collections.reverse(expectedSourceList);	
		if(!filterName.equalsIgnoreCase("organization_id")&&!filterName.equalsIgnoreCase("site_id")&&!filterName.equalsIgnoreCase("group_id")){
			spogServer.userLogin(final_direct_user_name_email, common_password);
		}else{
			spogServer.userLogin(this.csrReadOnlyAdminUserName, this.csrReadOnlyAdminPassword);
			   test.log(LogStatus.INFO,"get sources");	 
				spogServer.errorHandle.printInfoMessageInDebugFile("get sources");
			  	Response response = spogServer.getSources(sourceFilterStr, null, -1, -1, true, test);
			  	
			
			  	//check result
			  	test.log(LogStatus.INFO,"check get sources result");	 
				spogServer.errorHandle.printInfoMessageInDebugFile("check get sources result");
				spogServer.checkGetSources(response, SpogConstants.SUCCESS_GET_PUT_DELETE, expectedSourceList, -1, -1, -1, null, test);
				
			  	spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
		}
	
	 
	    test.log(LogStatus.INFO,"get sources");	 
		spogServer.errorHandle.printInfoMessageInDebugFile("get sources");
	  	Response response = spogServer.getSources(sourceFilterStr, null, -1, -1, true, test);

	  	//check result
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
  
  /*
   * 16. filter by multiple conditions
   * 20. set filter and paging together, should get the correct sources
   * 19. set page=X&Page_size=Y, should get the correct sources
   * 2. search string set less than 3 chars for source name, it should be ignored
   * @author shuo.zhang
   */
  @Test(enabled=false,  priority = 2)
  public void getSourcesWithMultiConditionsAndPaging() {
	  
	  //filter by direct_org_id and another_direct_site_id
	   //set page_size =5 page_number=2
	  
	  	test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
	  	test.assignAuthor("shuo.zhang");
	  	spogServer.errorHandle.printInfoMessageInDebugFile("/****************getSourcesWithMultiConditionsAndPaging**************/");
	  
		test.log(LogStatus.INFO,"admin login");
		spogServer.errorHandle.printInfoMessageInDebugFile("csr admin login");
	  	spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
	  	
	  	test.log(LogStatus.INFO,"create source in direct org"); 	
		spogServer.errorHandle.printInfoMessageInDebugFile("create source in direct org");
		
	    ArrayList<String> allSourcesNodes = new    ArrayList<String> ();
		ArrayList<HashMap<String, Object>> expectedSourceList = new ArrayList<HashMap<String, Object>> ();
		ArrayList<HashMap<String, Object>> expectedSourceList1 = new ArrayList<HashMap<String, Object>> ();
		ArrayList<Response> tempResponseList = new ArrayList<Response> ();
	    String[] directSiteIdArray = new String[]{direct_site_id, another_direct_site_id};

	    int page_number=2;
	    int page_size=2;
	    int total_size=0;
	  
		
		for(int i=0; i<10;i++){
	    	String soureceName = spogServer.ReturnRandom("shuo_source");
	    	Response createSourceResponse = null;
	    	String  siteId= directSiteIdArray[i%2];
	    	
	    	createSourceResponse = spogServer.createSource(soureceName, SourceType.machine, SourceProduct.udp, direct_org_id, siteId,  ProtectionStatus.protect, 
			  			ConnectionStatus.offline, "windows", null,  test);		
		    		
	    	String source_id =  createSourceResponse.then().extract().path("data.source_id");
	     	allSourcesNodes.add(source_id);
	    	tempResponseList.add(createSourceResponse);
	    }
		
		Collections.reverse(tempResponseList);
		for(int i=0; i<10; i++){
			String siteId = tempResponseList.get(i).jsonPath().get("data.site.site_id");
			if(siteId.equals(another_direct_site_id)){
				total_size++;
				if((total_size>page_number)&&(expectedSourceList.size()<page_size)){
					test.log(LogStatus.INFO,"filter is site_id");
					spogServer.errorHandle.printInfoMessageInDebugFile("filter is site_id");					
					String[] policyIds = null;
					String[] groupIds = null;			
					HashMap<String, Object> eachSourceInfo = getExpectedSourceMap(tempResponseList.get(i), siteId , policyIds ,null, groupIds, spogServer.getJWTToken(), false, null);			
					expectedSourceList.add(eachSourceInfo);

				}
			}
			
		
		}

	
	   

	  	test.log(LogStatus.INFO,"create source in account org");	 
		spogServer.errorHandle.printInfoMessageInDebugFile("create source in account org");
	    String[] mspSiteIdArray =  new String[]{this.account_site_id, this.another_account_site_id};
	    for(int i=0; i<mspSiteIdArray.length;i++){
	    	String soureceName = spogServer.ReturnRandom("shuo_source");
	    	Response createSourceResponse = spogServer.createSource(soureceName, SourceType.machine, SourceProduct.udp, account_id, mspSiteIdArray[i],  ProtectionStatus.protect, 
		  			ConnectionStatus.offline, "windows", null,  test);
	    	String source_id =  createSourceResponse.then().extract().path("data.source_id");
	     	allSourcesNodes.add(source_id);
		
	    }

	
	    test.log(LogStatus.INFO,"get sources");	 
		spogServer.errorHandle.printInfoMessageInDebugFile("get sources");
	  	String newFilterStr = "organization_id;=;"+ direct_org_id+",site_id;=;" + another_direct_site_id + ",is_deleted;=;false";
		Response response = spogServer.getSources(newFilterStr , null, page_number, page_size, true, test);
	  	
		
	  	//check result
	  	test.log(LogStatus.INFO,"check get sources result");	 
		spogServer.errorHandle.printInfoMessageInDebugFile("check get sources result");
		spogServer.checkGetSources(response, SpogConstants.SUCCESS_GET_PUT_DELETE, expectedSourceList, page_number, page_size, total_size, null, test);
		
		
		//2. search string set less than 3 chars for source name, it should be ignored
		newFilterStr = "organization_id;=;"+ direct_org_id+",site_id;=;" + another_direct_site_id + ",source_name;=;bb,is_deleted;=;false";
		response =  spogServer.getSources(newFilterStr , null, page_number, page_size, true, test);
	
		//check result
	  	test.log(LogStatus.INFO,"check get sources result");	 
		spogServer.errorHandle.printInfoMessageInDebugFile("check get sources result");
		spogServer.checkGetSources(response, SpogConstants.SUCCESS_GET_PUT_DELETE, expectedSourceList, page_number, page_size, total_size, null, test);
		
		
		
	    test.log(LogStatus.INFO,"get sources");	 
		spogServer.errorHandle.printInfoMessageInDebugFile("get sources");
		newFilterStr = "organization_id;=;"+ UUID.randomUUID().toString();
		response = spogServer.getSources(newFilterStr ,  null,-1, -1, true, test);
		
		
		  	//check result
		test.log(LogStatus.INFO,"check get sources result");	 
		spogServer.errorHandle.printInfoMessageInDebugFile("check get sources result");
		spogServer.checkGetSources(response, SpogConstants.SUCCESS_GET_PUT_DELETE,  new ArrayList<HashMap<String, Object>>(), -1, -1, 0, null, test);
	
		

		
		//delete source
		test.log(LogStatus.INFO,"delete source");
		spogServer.errorHandle.printInfoMessageInDebugFile("delete source");
	  	spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);

		for(int i=0; i< allSourcesNodes.size();i++){
			response = spogServer.deleteSourcesById(spogServer.getJWTToken(), allSourcesNodes.get(i), test);
			spogServer.deleteSourcesWithCheck(response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		}
		


	  	
}


  
  @DataProvider(name = "sourceInfo2")
  public final Object[][] getSourceInfo2() {
 	 return new Object[][]   {
			   
			   	 {final_msp_user_name_email, "organization_id;=;" + direct_org_id},
			   	 {account_user_email, "organization_id;=;"+   direct_org_id},
			     {final_direct_user_name_email, "organization_id;in;" + direct_org_id + "|" + account_id  },
 		 		 {final_msp_account_admin_email, "organization_id;=;"+   direct_org_id},	
 		 		
 		 		{final_msp_account_admin_email, "organization_id;=;"+   another_account_id},	
 		 		 {final_direct_user_name_email, "organization_id;=;"+ account_id},
 		 	 	
 		 		 {final_root_msp_user_name_email, "organization_id;=;" + sub_msp1_account1_id},
 		 		{final_root_msp_account_admin_user_name_email, "organization_id;=;" + sub_msp1_account1_id},
 		 		{final_root_msp_direct_org_user_email, "organization_id;=;" + sub_msp1_account1_id},
 		 		{final_sub_msp1_user_name_email, "organization_id;=;" + this.root_msp_direct_org_id},
 		 		{final_sub_msp1_user_name_email, "organization_id;=;" + this.sub_msp2_account1_id},
 		 		{final_sub_msp1_msp_account_user_name_email, "organization_id;=;" + this.root_msp_direct_org_id},
 		 		{final_sub_msp1_msp_account_user_name_email, "organization_id;=;" + this.sub_msp2_account1_id},
 		 		{final_sub_msp1_msp_account_user_name_email, "organization_id;=;" + this.sub_msp1_account2_id},
 		 		
 		 		{final_sub_msp1_account1_user_email, "organization_id;=;" + this.root_msp_direct_org_id},
 		 		{final_sub_msp1_account1_user_email, "organization_id;=;" + this.sub_msp2_account1_id},
 		 		{final_sub_msp1_account1_user_email, "organization_id;=;" + this.sub_msp1_account2_id},
 	 };
 	 	}
  /**
   * @author shuo.zhang
   *    23. direct admin could not get all sources in msp organization
   *    24. msp admin could not get all sources in direct organization
   *    25. account admin could not get all sources in direct organization
   *    22. when filter by orgnanization_id In [X|Y], if user doesn't have the right on orgnaization Y, it will report 403
   *    33. MSP account admin could not get sources for its direct org
   *    32. MSP account admin could not get sources for its own org
   *    31. MSP account admin could not get sources for not mastered account
   *    35. direct admin could not get all sources in account
   *    36. account admin could not get all sources in msp organization
   *    root msp admin can not get sub msp1 account1's source
   *    root msp msp account admin can not get sub msp1 account1's source
   *    root msp direct org's admin can not get sub msp1 account1's source
   *      sub msp1 admin can not get root msp direct org's source
   *       sub msp1 admin can not get sub msp2 account1's source
   *    sub msp1 msp account admin can not get root msp direct org's source
   *       sub msp1 msp account admin can not get sub msp2 account1's source
   *       sub msp1 msp account admin can not get sub msp1 account2's source
   *     sub msp1  account1 admin can not get root msp direct org's source
   *       sub msp1  account1 admin can not get sub msp2 account1's source
   *       sub msp1 account1 admin can not get sub msp1 account2's source
   *       
   *       
   * @param userName
   * @param filterStr
   */
	@Test(enabled=true, dataProvider="sourceInfo2",priority = 4)
	public void cannotGetSource(String userName, String filterStr){
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		test.assignAuthor("shuo.zhang");
	  	spogServer.errorHandle.printInfoMessageInDebugFile("/****************cannotGetSource**************/");
	  
		test.log(LogStatus.INFO,"admin login");
		spogServer.errorHandle.printInfoMessageInDebugFile("csr admin login");
	  	spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
	  	
	  	test.log(LogStatus.INFO,"create source in direct org"); 	
		spogServer.errorHandle.printInfoMessageInDebugFile("create source in direct org");
		

	    ArrayList<String> allSourcesNodes = new    ArrayList<String> ();
		
	    String[] directSiteIdArray = new String[]{direct_site_id, another_direct_site_id};
	    for(int i=0; i<directSiteIdArray.length;i++){
	    	String soureceName = spogServer.ReturnRandom("shuo_source");
	    	Response createSourceResponse = null;
	    	String  siteId=directSiteIdArray[i];
	    	
	    	createSourceResponse = spogServer.createSource(soureceName, SourceType.machine, SourceProduct.udp, direct_org_id, siteId,  ProtectionStatus.values()[i], 
			  			ConnectionStatus.values()[i], "windows", null,  test);		

	    	String source_id =  createSourceResponse.then().extract().path("data.source_id");
	     	allSourcesNodes.add(source_id);	    	
		
	    }
	    
	    test.log(LogStatus.INFO,"create source in account org"); 
	    String[] mspSiteIdArray = new String[]{this.account_site_id, this.another_account_site_id};
	    for(int i=0; i<mspSiteIdArray.length;i++){
	    	String soureceName = spogServer.ReturnRandom("shuo_source");
	    	Response createSourceResponse = null;
	    	String  siteId=mspSiteIdArray[i];
	    	
	    	createSourceResponse = spogServer.createSource(soureceName, SourceType.machine, SourceProduct.udp, account_id, siteId,  ProtectionStatus.values()[i], 
			  			ConnectionStatus.values()[i], "windows", null,  test);		

	    	String source_id =  createSourceResponse.then().extract().path("data.source_id");
	     	allSourcesNodes.add(source_id);		    	
		
	    }
	    

	    spogServer.userLogin(userName, common_password);
	    test.log(LogStatus.INFO,"get sources");	 
		spogServer.errorHandle.printInfoMessageInDebugFile("get sources");
		Response response = spogServer.getSources(filterStr , null, -1, -1, true, test);
		  	
		  	//check result
		test.log(LogStatus.INFO,"check get sources result");	 
		spogServer.errorHandle.printInfoMessageInDebugFile("check get sources result");
		spogServer.checkGetSources(response, SpogConstants.INSUFFICIENT_PERMISSIONS, null, -1, -1, -1, ErrorCode.RESOURCE_PERMISSION, test);
			
			
			//delete source
		test.log(LogStatus.INFO,"delete source");
		spogServer.errorHandle.printInfoMessageInDebugFile("delete source");
		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);

		for(int i=0; i< allSourcesNodes.size();i++){
			response = spogServer.deleteSourcesById(spogServer.getJWTToken(), allSourcesNodes.get(i), test);
			spogServer.deleteSourcesWithCheck(response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		}
			

	  
	}

	/**
	 * @author shuo.zhang
	 * 21. Can NOT call API if not logged in - 401when JWT is missing
	 */
	  @Test(enabled=false, priority = 2)	  
	  public void getSourceWithoutLogin(){
		  
		  	test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  	test.assignAuthor("shuo.zhang");
		  	spogServer.errorHandle.printInfoMessageInDebugFile("/****************createSourceWithoutLogin**************/");
		  
			test.log(LogStatus.INFO,"admin login");
			spogServer.errorHandle.printInfoMessageInDebugFile("admin login");
		  	spogServer.userLogin(csrAdminUserName, csrAdminPassword);

		    test.log(LogStatus.INFO,"get sources");	 
			spogServer.errorHandle.printInfoMessageInDebugFile("get sources");
			Response response = spogServer.getSources(null ,  null,-1, -1, false, test);
			spogServer.checkGetSources(response, SpogConstants.NOT_LOGGED_IN, null,  -1, -1, -1, ErrorCode.AUTHORIZATION_HEADER_BLANK, test);
		 
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
		//	 sourceInfo.put("protection_status", createSourceResponse.then().extract().path("data.protection_status"));
		 sourceInfo.put("protection_status", ProtectionStatus.unprotect.name());
			 
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
		  		
		  		policyMap.put("policy_type", "cloud_direct_baas");
		  		policyMap.put("policy_id", policyIds[i]);
		  		policyMap.put("policy_name", policyNames[i]);
		  		policyList.add(policyMap);
		  	}
	  	  sourceInfo.put("policy", policyList);
	  	 
	  	}
	
	  	//last_recovery_point_ts
	/*  	if(lastJobList==null){
	  		 lastJobList = new 	ArrayList<HashMap<String, Object> >();
	  		sourceInfo.put("last_recovery_point_ts", null );
	  	}else{
	  		if( createSourceResponse.then().extract().path("data.source_product").toString().equalsIgnoreCase(SourceProduct.udp.name())){
	  			sourceInfo.put("last_recovery_point_ts", lastJobList.get(0).get("start_time_ts") );
	  		}else{
	  			sourceInfo.put("last_recovery_point_ts", null );
	  		}
	  	
	  	}
	  		sourceInfo.put("last_job", lastJobList);
	  	*/
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
		
			availabeActionsArray.add("startrecovery");
			availabeActionsArray.add("delete");
		//	availabeActionsArray.add("cancelrepliationin");
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
		
			
	//		availabeActionsArray.add("remove");

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
	private String getSourceFilterStr(	String user_id, String filterName , String filterOperator, String filterValue){
		
		String filter_id = null;
		String filterStr=null;
		if(filterName.equalsIgnoreCase("organization_id")){
			
			return filterName+";"+filterOperator+";"+filterValue;
		}else if(filterName.equalsIgnoreCase("protection_status")){
			filter_id = spogServer.createFilterwithCheck(user_id, spogServer.returnRandomUUID(), filterValue.replace("|", ","), "none", "none", 
					"none", "none", "none", "none", "none", "none", "none", "false", test);

		}else if(filterName.equalsIgnoreCase("connection_status")){
			filter_id = spogServer.createFilterwithCheck(user_id, spogServer.returnRandomUUID(), "none", filterValue.replace("|", ","), "none", 
					"none", "none", "none", "none", "none", "none", "none", "false", test);

		}else if(filterName.equalsIgnoreCase("site_id")){
			filter_id = spogServer.createFilterwithCheck(user_id, spogServer.returnRandomUUID(), "none", "none", "none", 
					"none", "none", "none", "none",filterValue.replace("|", ",") , "none", "none", "false", test);

		}else if(filterName.equalsIgnoreCase("group_id")){
			filter_id = spogServer.createFilterwithCheck(user_id, spogServer.returnRandomUUID(), "none", "none", "none", 
					"none",  filterValue.replace("|", ","), "none", "none", "none" , "none", "none", "false", test);

		}else if(filterName.equalsIgnoreCase("operating_system")){
			filter_id = spogServer.createFilterwithCheck(user_id, spogServer.returnRandomUUID(), "none", "none", "none", 
					"none",  "none", filterValue.replace("|", ","), "none", "none" , "none", "none", "false", test);

		}
		else if(filterName.equalsIgnoreCase("source_type")){
			filter_id = spogServer.createFilterwithCheck(user_id, spogServer.returnRandomUUID(), "none", "none", "none", 
					"none",  "none", "none", "none", "none" , "none", filterValue.replace("|", ","), "false", test);

		}else if(filterName.equalsIgnoreCase("source_name")){
			filter_id = spogServer.createFilterwithCheck(user_id, spogServer.returnRandomUUID(), "none", "none", "none", 
					"none",  "none", "none", "none", "none" , filterValue, "none", "false", test);

		}else if(filterName.equalsIgnoreCase("last_job")){
			filter_id = spogServer.createFilterwithCheck(user_id, spogServer.returnRandomUUID(), "none", "none", "none", 
					filterValue.replace("|", ","),  "none", "none", "none", "none" , filterValue, "none", "false", test);

		}
		else if(filterName.equalsIgnoreCase("policy_id")){
			filter_id = spogServer.createFilterwithCheck(user_id, spogServer.returnRandomUUID(), "none", "none", filterValue.replace("|", ","), 
					"none",  "none", "none", "none", "none" , filterValue, "none", "false", test);

		}
		Response response = spogServer.getFilterByID(user_id, filter_id);
		Map<String, Object> dataMap = response.jsonPath().getMap("data");
		filterStr =  filterName + ";" + filterOperator + ";" ;
	//	if(filterName.equalsIgnoreCase("protection_status")||filterName.equalsIgnoreCase("connection_status")){
		if(filterName.equalsIgnoreCase("source_type")){
			filterStr+= dataMap.get("source_filter_type");
		}else if(filterName.equalsIgnoreCase("source_name")){
			filterStr+= dataMap.get("source_name");
		}else{
			ArrayList<String> protectStatusList = (ArrayList<String> ) dataMap.get(filterName);
			for(int i=0; i<protectStatusList.size();i++){
				if(i!=0){
					filterStr+= "|";
				}
				filterStr+=protectStatusList.get(i);
			}
		}
		
		spogServer.deletefilterspecifiedbyUserIdwithCheck(user_id, filter_id, spogServer.getJWTToken(), SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		return filterStr;
	}
	
	private ArrayList<HashMap<String, Object>> getLastJob(Object jobType, String jobSeverity, String jobStatus, long start_time_ts, long endTimeTS, double percent, String jobId){
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
		lastJobHashMap.put("job_id",jobId);
		lastJobHashMap.put("end_time_ts", endTimeTS);
		lastJobHashMap.put("percent_complete", percent);
		lastJobList.add(lastJobHashMap);
		return lastJobList;
	}

}

