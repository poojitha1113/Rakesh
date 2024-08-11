package api.sources;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
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

import Constants.ConnectionStatus;
import Constants.ErrorCode;
import Constants.OSMajor;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import InvokerServer.GatewayServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import InvokerServer.Log4GatewayServer;
import InvokerServer.Org4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGHypervisorsServer;
import InvokerServer.GatewayServer.siteType;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import invoker.SiteTestHelper;
import io.restassured.response.Response;

public class CreateSourceTest  extends base.prepare.PrepareOrgInfo{
	
	@Parameters({ "pmfKey"})
	  public CreateSourceTest(String pmfKey) {
		super(pmfKey);
		// TODO Auto-generated constructor stub
	}


	private SPOGServer spogServer;
	  private UserSpogServer userSpogServer;
	  private GatewayServer gatewayServer;
	  private SPOGHypervisorsServer spogHypervisorsServer;
	  private SPOGDestinationServer spogDestinationServer;
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
	//  private String msp_site_id;
	  
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
		
	  private String prefix_csr = "spogqa_shuo_csr";
	  private String csr_user_name = prefix_csr + "_admin";
	  private String csr_user_name_email = prefix_csr + "_admin" + postfix_email;
	  private String csr_user_first_name = csr_user_name + "_first_name";
	  private String csr_user_last_name = csr_user_name + "_last_name";
	  
	  HashMap<String,String> siteTokenMap ;
	  HashMap<String,String> destinationMap ;
	  //this is for update portal, each testng class is taken as BQ set
/*	  private SQLServerDb bqdb1;
	  public int Nooftest;
	  private long creationTime;
	  private String BQName=null;
	  private String runningMachine;
	  private testcasescount count1;
	  private String buildVersion;
	  private ExtentReports rep;*/
	  
	  
	private String another_account_id;
	private String site_id_another_account;
	private String prefix_destination = "SPOG_QA_shuo";
	private String direct_user_cloud_account_id;
	private String account_user_cloud_account_id;
	private String msp_user_cloud_account_id;
	private Org4SPOGServer org4SpogServer;

	private String  org_model_prefix=this.getClass().getSimpleName();
	private String root_msp_direct_org_destination_id;
	 
	  @BeforeClass
	  @Parameters({ "baseURI", "port", "logFolder", "csrAdminUserName", "csrAdminPassword", "buildVersion", "csrReadOnlyAdminUserName","csrReadOnlyAdminPassword"})
	  public void beforeClass(String baseURI, String port, String logFolder, String adminUserName, String adminPassword ,  String buildVersion, String csrROAdminUserName, String csrROPwd ) throws UnknownHostException {
		  	spogServer = new SPOGServer(baseURI, port);
		  	userSpogServer = new UserSpogServer(baseURI, port);
		  	gatewayServer =new GatewayServer(baseURI,port);
			spogHypervisorsServer = new SPOGHypervisorsServer(baseURI, port);
			spogDestinationServer=new SPOGDestinationServer(baseURI,port);
			org4SpogServer = new Org4SPOGServer(baseURI, port);
		  	rep = ExtentManager.getInstance("CreateSourceTest",logFolder);
		  	this.csrAdminUserName = adminUserName;
		  	this.csrAdminPassword = adminPassword;
		  	this.csrReadOnlyAdminUserName = csrROAdminUserName;
			this.csrReadOnlyAdminPassword = csrROPwd;
			  
		    test = rep.startTest("beforeClass");
			spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
			
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
		    
			//*******************create direct org,user,site**********************/
			siteTokenMap = new HashMap<String,String>();
			destinationMap = new HashMap<String,String>();
			String prefix = RandomStringUtils.randomAlphanumeric(8);
			final_direct_user_name_email = prefix + direct_user_name_email;
			
			test.log(LogStatus.INFO,"create a direct org");
			direct_org_id= spogServer.CreateOrganizationByEnrollWithCheck(  prefix+direct_org_name+org_model_prefix, SpogConstants.DIRECT_ORG, final_direct_user_name_email, common_password, prefix + direct_user_first_name, prefix + direct_user_last_name);
		 	 Response response = userSpogServer.postUsersSearchResponse(final_direct_user_name_email, null, null, spogServer.getJWTToken());
	 		  if(response.statusCode()==SpogConstants.SUCCESS_GET_PUT_DELETE){
	 			 int total_size = response.then().extract().path("pagination.total_size");
			 	  if(total_size==1){
			 		 ArrayList<HashMap<String, Object>> dataList = response.then().extract().path("data");
			 		 direct_user_id = (String) dataList.get(0).get("user_id");
	
			 	  }
	 		  }
	 		  
		
		
			
			/*test.log(LogStatus.INFO,"create a admin under direct org");
			direct_user_id = spogServer.createUserAndCheck(final_direct_user_name_email, common_password, prefix + direct_user_first_name, prefix + direct_user_last_name, SpogConstants.DIRECT_ADMIN, direct_org_id, test);
			*/spogServer.userLogin(final_direct_user_name_email, common_password);
		  	
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
			
	/*		test.log(LogStatus.INFO, "creating cloudaccount for the user");
			direct_user_cloud_account_id=spogServer.createCloudAccountWithCheck(prefix+"cloudAccountKey", prefix+"cloudAccountSecret", prefix+"cloudAccountName", "cloud_direct", direct_org_id, "SKUTESTDATA_1_0_0_0_"+prefix, "SKUTESTDATA_1_0_0_0_"+prefix,"91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
		*/
			test.log(LogStatus.INFO,"create destination for direct org");
			spogDestinationServer.setToken(spogServer.getJWTToken());
			String[] datacenterIDs=spogDestinationServer.getDestionationDatacenterID();
		    int index=(int)Math.random()*datacenterIDs.length;
		    String datacenterID=datacenterIDs[index];			    
		    test.log(LogStatus.INFO,"get datacenterID:" +datacenterID);
		    
		    try {
				Thread.sleep(20000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    String destination_name= prefix_destination+RandomStringUtils.randomAlphanumeric(3);
			String direct_destination_id = spogDestinationServer.createDestinationWithCheck("", direct_org_id, direct_site_id, datacenterID,"cloud_direct_volume", "running", "1",
					"" , "normal", "zhash05-win8", "custom","custom", "0", "0", "7", "0",
    				"0", "0",  "", "", "", "", destination_name,test);
			destinationMap.put(this.direct_site_id, direct_destination_id);
			
			//************************create msp org,user,site*************************************
			spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
			prefix = RandomStringUtils.randomAlphanumeric(8);
			test.log(LogStatus.INFO,"create a msp org");
			//msp_org_id = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name+ org_model_prefix , SpogConstants.MSP_ORG, null, null, null, null, test);
			final_msp_user_name_email = prefix + msp_user_name_email;
			msp_org_id = spogServer.CreateOrganizationByEnrollWithCheck(prefix + msp_org_name+org_model_prefix, SpogConstants.MSP_ORG, final_msp_user_name_email, common_password, prefix + msp_user_first_name, prefix + msp_user_last_name);
			 response = userSpogServer.postUsersSearchResponse(final_msp_user_name_email, null, null, spogServer.getJWTToken());
	 		  if(response.statusCode()==SpogConstants.SUCCESS_GET_PUT_DELETE){
	 			 int total_size = response.then().extract().path("pagination.total_size");
			 	  if(total_size==1){
			 		 ArrayList<HashMap<String, Object>> dataList = response.then().extract().path("data");
			 		msp_user_id = (String) dataList.get(0).get("user_id");
	
			 	  }
	 		  }
		/*	test.log(LogStatus.INFO,"create a admin under msp org");
			msp_user_id = spogServer.createUserAndCheck(final_msp_user_name_email, common_password, prefix + msp_user_first_name, prefix + msp_user_last_name, SpogConstants.MSP_ADMIN, msp_org_id, test);
		*/
			
			test.log(LogStatus.INFO,"create a msp account admin under msp org");
			final_msp_account_admin_email = prefix + this.msp_account_admin_email;
			msp_account_admin_id = spogServer.createUserAndCheck(final_msp_account_admin_email, common_password, prefix + msp_account_admin_first_name, prefix + msp_account_admin_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, msp_org_id, test);
			
	 		spogServer.userLogin(final_msp_user_name_email, common_password);
			test.log(LogStatus.INFO,"Getting the JWTToken for the msp user");
			String msp_user_validToken = spogServer.getJWTToken();
			test.log(LogStatus.INFO,"The token is :"+ msp_user_validToken );
			/*	
			siteName= spogServer.getRandomSiteName("TestCreate");
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
			//msp_user_cloud_account_id=spogServer.createCloudAccountWithCheck(prefix+"cloudAccountKey", prefix+"cloudAccountSecret", prefix+"cloudAccountName", "cloud_direct", msp_org_id, "SKUTESTDATA_1_0_0_0_"+prefix, "SKUTESTDATA_1_0_0_0_"+prefix,"91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
			

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
			
			test.log(LogStatus.INFO,"create destination for msp org");
			spogServer.userLogin(final_msp_user_name_email, common_password);
			destination_name= prefix_destination+RandomStringUtils.randomAlphanumeric(3);	
			spogDestinationServer.setToken(spogServer.getJWTToken());
			String account_destination_id = spogDestinationServer.createDestinationWithCheck("", account_id, this.account_site_id, datacenterID,"cloud_direct_volume", "running", "1",
						msp_user_cloud_account_id , "normal", "zhash05-win8", "custom","custom", "0", "0", "7", "0",
	    				"0", "0",  "", "", "", "", destination_name,test);
			destinationMap.put(this.account_site_id, account_destination_id);
			
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
			siteTokenMap.put(site_id_another_account, gatewayServer.getJWTToken());
			
			prepare(baseURI, port, logFolder, this.csrAdminUserName,  this.csrAdminPassword, this.getClass().getSimpleName() );
			
			/*test.log(LogStatus.INFO,"create destination for root msp direct org");
			spogServer.userLogin(this.final_root_msp_user_name_email, common_password);
			destination_name= prefix_destination+RandomStringUtils.randomAlphanumeric(3);	
			spogDestinationServer.setToken(spogServer.getJWTToken());
			root_msp_direct_org_destination_id = spogDestinationServer.createDestinationWithCheck("", this.root_msp_direct_org_id, this.root_msp_direct_org_site1_siteId, datacenterID,"cloud_direct_volume", "running", "1",
						null , "normal", "zhash05-win8", "custom","custom", "0", "0", "7", "0",
	    				"0", "0",  "", "", "", "", destination_name,test);
			destinationMap.put(this.root_msp_direct_org_site1_siteId, root_msp_direct_org_destination_id);
			*/
		    

	  }
	  
	  

	  
/**
 * @author shuo.zhang
 * @test 1. direct admin could post source in its organization
 * 3.direct admin could post different sources in different sites
 * 4. Msp admin  could post source in its organization
 * 5. MSP admin could post source in its child organization
 * 7. MSP admin  could post different sources in different sites
 * 9. account admin  could post source in its organization
 * 11. account admin  could post different sources in different sites
 * 12. set organization_id is empty, it should use logged in user's organization id
 * 13. set create_user_id is empty, it should use logged in user's id
 * 8. csr admin could post source in direct organization
 * 18. set source_type as valid value
 * 20. set source_product as valid value
 * 22. set protection_status as valid value
 * 24. set connection_status as valid value
 * 26. set application as multiple applications
 * 28. csr admin could post source in msp organization
 * 29. csr admin could post source in msp sub organization
 * 1. user could create source with specified source id
 * 3. user could create source with source_id as ""
 * 
 * 1. add a cloud direct agent based node with os_major
 * 2. add a cloud direct agent based node with source_id
 * 3. add a cloud direct agent based node with site_token
 * 4. add a cloud direct agent based node with agent_current_version
 * 5. add a cloud direct agent based node with agent_name
 * 12. MSP site token can add cloud direct source
 * 
 * 6. add a cloud direct agent based node with agent_upgrade_version
 * 7. add a cloud direct agent based node with agent_upgrade_link
 * 8. add a cloud direct agent based node with os_name
 * 9. add a cloud direct agent based node with os_architecture
 * 10. add a cloud direct agent based node with source_product
 * 11. direct site token can add cloud direct source
 *
 * 13. account site token can add cloud direct source
 * 
 * add agent-less node
 * 1. add a cloud direct agent based node with site_token
 * 2. add a cloud direct agent based node with source_type: agentless_vm
 * 3. add a cloud direct agent based node with hypervisor id
 * 4. add a cloud direct agent based node with os_major
 * 5. direct site token could add a cloud direct agent based node
 * 6. add a cloud direct agent based node with vm_name 
 * 7. msp site token could add a cloud direct agent based node
 * 8. account site token could add a cloud direct agent based node
 * 
 * 
 * 30. MSP account admin could create source in mastered account
 *   * 34. MSP account admin login, not set org id, set site id as account's, create source successfully
 * 
 * root msp admin can create source in its direct org
 * root msp account admin can create source in its direct org
 * root msp direct org 's admin can create source in its org
 * sub msp1 admin can create source in account1
 * sub msp1 account admin can create source in account1
 * account1's admin can create source in account1
 * 
 * 

 * @param sourceType
 * @param sourceProduct
 * @param org_id
 * @param siteId
 * @param protectionStatus
 * @param connectionStatus
 * @param os_major
 * @param applications
 * @param create_user_id
 * @param agent_vm_name
 * @param agent_hypervisor_id
 * @param agent_name
 * @param agent_current_version
 * @param agent_upgrade_version
 * @param os_name
 * @param os_architecture
 * @param agent_upgrade_link
 */
	  
 @DataProvider(name = "sourceInfo")
 public final Object[][] getSourceInfo() {
	 return new Object[][]   {
					{final_direct_user_name_email,common_password, SourceType.machine,  SourceProduct.udp, direct_org_id, direct_site_id, ProtectionStatus.protect, ConnectionStatus.online,
							OSMajor.windows.name(),	"", "shuo_vm1", null, null, null, null,null,null, null, null}, 
		 				{final_direct_user_name_email,common_password, SourceType.office_365,  SourceProduct.udp, direct_org_id, another_direct_site_id, ProtectionStatus.unprotect, ConnectionStatus.online,
	 			 			OSMajor.windows.name(),	"", null, null, null, null, null,null,null, null , null},
		  	           
		  	           {final_msp_user_name_email, common_password, SourceType.shared_folder,  SourceProduct.udp,  account_id, account_site_id, ProtectionStatus.protect, ConnectionStatus.online,
	 			 					OSMajor.windows.name(),	"sql;exchange", null, null, "shuo_proxy_1", null, null,null,null, null , null },
					
		 				{account_user_email, common_password, SourceType.machine,  SourceProduct.udp,  account_id, account_site_id, ProtectionStatus.protect, ConnectionStatus.online,
	 			 							OSMajor.windows.name(),	"sql;exchange",  null, null, null, null, "64" ,null,null, null , null},
		 				{account_user_email, common_password, SourceType.machine,  SourceProduct.udp,  account_id, another_account_site_id, ProtectionStatus.protect, ConnectionStatus.online,
	 			 								OSMajor.windows.name(),	"",   null, null, null, null, null,"1.0.0" ,null, null, null},
		 				{account_user_email, common_password, SourceType.machine,  SourceProduct.udp,  "", another_account_site_id, ProtectionStatus.protect, ConnectionStatus.online,
	 			 									OSMajor.windows.name(),	"",   null, null, null, null, null,null, "1.0.1", null, null},
						{account_user_email, common_password, SourceType.machine,  SourceProduct.udp,  account_id, account_site_id, ProtectionStatus.protect, ConnectionStatus.online,
	 			 										OSMajor.windows.name(),	"sql;exchange",   "shuo_vm2", null, "shuo_agent1", "windows 2012", "64" ,"1.0.0","2.0", "http://upgrade", null},
		 				{csrAdminUserName, csrAdminPassword, SourceType.machine,  SourceProduct.udp, direct_org_id, direct_site_id, ProtectionStatus.protect, ConnectionStatus.online,
	 			 											OSMajor.windows.name(),	"",  null, null, null, null, null,null,null, null , null},
		 				
		 				{csrAdminUserName, csrAdminPassword, SourceType.machine,  SourceProduct.udp,  account_id, account_site_id, ProtectionStatus.protect, ConnectionStatus.online,
	 			 													OSMajor.windows.name(),	"sql;exchange",   null, null, null, null, null,null,null, null , null},
		 				{final_direct_user_name_email,common_password, SourceType.machine,  SourceProduct.udp, direct_org_id, direct_site_id, ProtectionStatus.protect, ConnectionStatus.online,
		 					OSMajor.windows.name(),	"", "shuo_vm1", null, null, null, null,null,null, null, UUID.randomUUID().toString()}, 
		 				{final_direct_user_name_email,common_password, SourceType.machine,  SourceProduct.udp, direct_org_id, direct_site_id, ProtectionStatus.protect, ConnectionStatus.online,
		 						OSMajor.windows.name(),	"", "shuo_vm1", null, null, null, null,null,null, null, ""}, 
		//below is for cloud account	
		 
		 	{final_direct_user_name_email,common_password, SourceType.machine,  SourceProduct.cloud_direct, direct_org_id, direct_site_id, ProtectionStatus.protect, ConnectionStatus.online,
				OSMajor.linux.name(),	"", "shuo_vm1", null, "cloud direct agent", null, "64" ,"1.0.0","1.1.1", "https://upgrade.com", UUID.randomUUID().toString()}, 
		 	{account_user_email, common_password, SourceType.agentless_vm,  SourceProduct.cloud_direct,  account_id, account_site_id, ProtectionStatus.protect, ConnectionStatus.online,
						OSMajor.mac.name(),	"sql;exchange",   "shuo_vm2",  UUID.randomUUID().toString(), "shuo_agent1", "windows 2012", "64" ,"1.0.0","2.0", "http://upgrade", UUID.randomUUID().toString()},
		 
		 {final_msp_user_name_email, common_password,SourceType.agentless_vm,  SourceProduct.cloud_direct,  account_id, account_site_id, ProtectionStatus.protect, ConnectionStatus.offline,
				OSMajor.windows.name(),	"", null,null, null, null, null,null ,null, null , UUID.randomUUID().toString()},
		 	{final_direct_user_name_email,common_password, SourceType.agentless_vm,  SourceProduct.cloud_direct, direct_org_id, direct_site_id, ProtectionStatus.protect, ConnectionStatus.online,
				OSMajor.linux.name(),	"",null,UUID.randomUUID().toString(), null, null, null,null ,null, null , UUID.randomUUID().toString()},  
			{account_user_email, common_password, SourceType.agentless_vm,  SourceProduct.cloud_direct,  account_id, account_site_id, ProtectionStatus.protect, ConnectionStatus.online,
					OSMajor.mac.name(),	"sql;exchange",   "shuo_vm2",  UUID.randomUUID().toString(), "shuo_agent1", "windows 2012", "64" ,"1.0.0","2.0", "http://upgrade", UUID.randomUUID().toString()},
	 	
		 {final_msp_account_admin_email,common_password, SourceType.machine,  SourceProduct.udp, account_id, account_site_id, ProtectionStatus.protect, ConnectionStatus.online,
				OSMajor.windows.name(),	"", "shuo_vm1", null, null, null, null,null,null, null, null},
		
		 {this.final_root_msp_user_name_email,common_password, SourceType.machine,  SourceProduct.udp, this.root_msp_direct_org_id, this.root_msp_direct_org_site1_siteId, ProtectionStatus.protect, ConnectionStatus.online,
				OSMajor.windows.name(),	"", "shuo_vm1", null, null, null, null,null,null, null, null}, 
		 {this.final_root_msp_account_admin_user_name_email,common_password, SourceType.machine,  SourceProduct.udp, this.root_msp_direct_org_id, this.root_msp_direct_org_site1_siteId, ProtectionStatus.protect, ConnectionStatus.online,
					OSMajor.windows.name(),	"", "shuo_vm1", null, null, null, null,null,null, null, null}, 
		 {this.final_root_msp_direct_org_user_email,common_password, SourceType.machine,  SourceProduct.udp, this.root_msp_direct_org_id, this.root_msp_direct_org_site1_siteId, ProtectionStatus.protect, ConnectionStatus.online,
						OSMajor.windows.name(),	"", "shuo_vm1", null, null, null, null,null,null, null, null}, 
		 {this.final_sub_msp1_user_name_email,common_password, SourceType.machine,  SourceProduct.udp, this.sub_msp1_account1_id, this.sub_msp1_account1_site1_siteId, ProtectionStatus.protect, ConnectionStatus.online,
						OSMajor.windows.name(),	"", "shuo_vm1", null, null, null, null,null,null, null, null}, 
		 {this.final_sub_msp1_msp_account_user_name_email,common_password, SourceType.machine,  SourceProduct.udp, this.sub_msp1_account1_id, this.sub_msp1_account1_site1_siteId, ProtectionStatus.protect, ConnectionStatus.online,
							OSMajor.windows.name(),	"", "shuo_vm1", null, null, null, null,null,null, null, null}, 
		 {this.final_sub_msp1_account1_user_email,common_password, SourceType.machine,  SourceProduct.udp, this.sub_msp1_account1_id, this.sub_msp1_account1_site1_siteId, ProtectionStatus.protect, ConnectionStatus.online,
								OSMajor.windows.name(),	"", "shuo_vm1", null, null, null, null,null,null, null, null}, 
	 };
	 	}
	  
  @Test(dataProvider = "sourceInfo", enabled=true)
  public void createSource(String userName, String password, SourceType sourceType, SourceProduct sourceProduct, String org_id, String siteId, ProtectionStatus protectionStatus, 
		  ConnectionStatus connectionStatus, String os_major, String applications,String vm_name ,String hypervisor_id ,String agent_name ,String os_name ,
			String os_architecture ,String agent_current_version ,String agent_upgrade_version ,String agent_upgrade_link, String source_id) {
	  
	  	test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
	  	test.assignAuthor("shuo.zhang");
	  	spogServer.errorHandle.printInfoMessageInDebugFile("/****************createSource*************");
	  
		test.log(LogStatus.INFO,"admin login");
		spogServer.errorHandle.printInfoMessageInDebugFile("admin login");
	    spogServer.userLogin(userName, password);
	 	String soureceName = spogServer.ReturnRandom("shuo_source");
	  	
	  	
		test.log(LogStatus.INFO,"create source and check");
		spogServer.errorHandle.printInfoMessageInDebugFile("create source and check");
		String destination_id_ret = null;
		ProtectionStatus protectState = ProtectionStatus.unprotect;
		if(sourceProduct.equals(SourceProduct.cloud_direct)){
			String userToken = spogServer.getJWTToken();
			spogServer.setToken(siteTokenMap.get(siteId));
			if(sourceType.equals(SourceType.agentless_vm) && (hypervisor_id!=null)){
				
				String prefix = RandomStringUtils.randomAlphanumeric(8);
		
				
				destination_id_ret=destinationMap.get(siteId);
				
				spogHypervisorsServer.setToken(siteTokenMap.get(siteId));
				hypervisor_id = spogHypervisorsServer.createHypervisorWithCheck("none", prefix + "shuo_hypervisor_name", "vmware", "cloud_direct", "none", siteId, 
						org_id, "false", String.valueOf(System.currentTimeMillis()), destination_id_ret,  "none", "0 0 * * *", "1d", null, null, null, null, null, 
								"none", "none", "emptyarray", "emptyarray", "emptyarray", "emptyarray", "none", "none", test);
			//	protectState =ProtectionStatus.partial_protect;
			}
			
			Response response = spogServer.createSource(soureceName, sourceType, sourceProduct, org_id, siteId, protectionStatus, connectionStatus,
					os_major, applications, vm_name, hypervisor_id, agent_name, os_name, os_architecture, agent_current_version, agent_upgrade_version,
					agent_upgrade_link, source_id, test);
			spogServer.setToken(userToken);
						
			String new_source_id = spogServer.checkCreateSource(response, soureceName, sourceType, sourceProduct, org_id, siteId, protectState, 
		  			connectionStatus, os_major, applications, vm_name, hypervisor_id, agent_name, os_name, os_architecture, agent_current_version,
		  			agent_upgrade_version, agent_upgrade_link, source_id, SpogConstants.SUCCESS_POST, "", test);
			if(sourceType.equals(SourceType.agentless_vm) && (hypervisor_id!=null)){
				spogHypervisorsServer.deleteHypervisorWithCheck(hypervisor_id, test);
				
			}
		
			
			
		}else{
			
			String new_source_id = spogServer.createSourceWithCheck(soureceName, sourceType, sourceProduct, org_id, siteId,  protectionStatus, 
		  			connectionStatus, os_major, applications, vm_name, hypervisor_id, agent_name, os_name, os_architecture, agent_current_version,
		  			agent_upgrade_version, agent_upgrade_link, source_id, SpogConstants.SUCCESS_POST, "", test);
		  	test.log(LogStatus.INFO,"delete source");
			spogServer.errorHandle.printInfoMessageInDebugFile("delete source");
			Response response = spogServer.deleteSourcesById(spogServer.getJWTToken(), new_source_id, test);
			spogServer.deleteSourcesWithCheck(response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		}

		

		
  }
  
  @DataProvider(name = "sourceInfo1")
  public final Object[][] getSourceInfo1() {
 	 return new Object[][]   {
 		 			
 		 				{final_msp_user_name_email, common_password,SourceType.instant_vm.name(),  SourceProduct.cloud_direct.name(),  direct_org_id, direct_site_id, ProtectionStatus.protect.name(), ConnectionStatus.offline.name(),
 		 						OSMajor.windows.name(),	"sql",  msp_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION, null},
 		 			
 		 				{final_direct_user_name_email,common_password, SourceType.machine.name(),  SourceProduct.udp.name(), direct_org_id, null, ProtectionStatus.protect.name(), ConnectionStatus.online.name(),
 		 								OSMajor.windows.name(),	"", direct_user_id,SpogConstants.SUCCESS_POST, null, null},
 		 				{final_direct_user_name_email,common_password, SourceType.machine.name(),  SourceProduct.udp.name(), direct_org_id, UUID.randomUUID().toString(), ProtectionStatus.protect.name(), ConnectionStatus.online.name(),
 		 									OSMajor.windows.name(),	"", direct_user_id,SpogConstants.SUCCESS_POST,null, null},
 		 				
 		 				{csrAdminUserName,csrAdminPassword, SourceType.machine.name(),  SourceProduct.udp.name(), UUID.randomUUID().toString(), direct_site_id, ProtectionStatus.protect.name(), ConnectionStatus.online.name(),
 		 										OSMajor.windows.name(),	"",  direct_user_id,SpogConstants.RESOURCE_NOT_EXIST, ErrorCode.CAN_NOT_FIND_ORG, null},
 		 				{final_direct_user_name_email,common_password, SourceType.machine.name(),  "test", direct_org_id, direct_site_id, ProtectionStatus.protect.name(), ConnectionStatus.online.name(),
 		 											OSMajor.windows.name(),	"", direct_user_id, SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.NOT_VALID_ENUM, null},
 		 			
 		 				{final_direct_user_name_email,common_password, SourceType.machine.name(),  SourceProduct.udp.name(), direct_org_id, direct_site_id, ProtectionStatus.protect.name(), "test",
 		 													OSMajor.windows.name(),	"", direct_user_id, SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.NOT_VALID_ENUM, null},
					
 		 														
 		
 		 				{final_direct_user_name_email,common_password, SourceType.machine.name(),  SourceProduct.udp.name(), direct_org_id, direct_site_id, ProtectionStatus.protect.name(), ConnectionStatus.online.name(),
 		 						OSMajor.windows.name(),	"", direct_user_id,SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.ELEMENT_NOT_UUID, "dddero"},
 		
 		{final_msp_account_admin_email, common_password,SourceType.instant_vm.name(),  SourceProduct.udp.name(),  another_account_id, site_id_another_account, ProtectionStatus.protect.name(), ConnectionStatus.offline.name(),
				OSMajor.windows.name(),	"sql",  msp_account_admin_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION, null},
 	
 		{final_msp_account_admin_email, common_password,SourceType.instant_vm.name(),  SourceProduct.udp.name(),  direct_org_id, direct_site_id, ProtectionStatus.protect.name(), ConnectionStatus.offline.name(),
						OSMajor.windows.name(),	"sql",  msp_account_admin_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION, null},
 		
 		{final_msp_account_admin_email, common_password,SourceType.instant_vm.name(),  SourceProduct.udp.name(),  "", account_site_id, ProtectionStatus.protect.name(), ConnectionStatus.offline.name(),
			OSMajor.windows.name(),	"sql", msp_account_admin_id, SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.SPECIFY_ORG_IN_PARAMETER, null},
 		{this.csrReadOnlyAdminUserName, this.csrReadOnlyAdminPassword,SourceType.instant_vm.name(),  SourceProduct.cloud_direct.name(),  direct_org_id, direct_site_id, ProtectionStatus.protect.name(), ConnectionStatus.offline.name(),
					OSMajor.windows.name(),	"sql",  null, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION, null},
 		{this.csrReadOnlyAdminUserName, this.csrReadOnlyAdminPassword,SourceType.instant_vm.name(),  SourceProduct.cloud_direct.name(),  account_id, account_site_id, ProtectionStatus.protect.name(), ConnectionStatus.offline.name(),
						OSMajor.windows.name(),	"sql",  null, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION, null},
 		


 		
 		 {this.final_root_msp_user_name_email,common_password, SourceType.machine.name(),  SourceProduct.udp.name(), this.sub_msp1_account1_id, this.sub_msp1_account1_site1_siteId, ProtectionStatus.protect.name(), ConnectionStatus.online.name(),
							OSMajor.windows.name(), "sql", null,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION, null}, 
 		 {this.final_root_msp_account_admin_user_name_email,common_password, SourceType.machine.name(),  SourceProduct.udp.name(), this.sub_msp1_account1_id, this.sub_msp1_account1_site1_siteId, ProtectionStatus.protect.name(), ConnectionStatus.online.name(),
								OSMajor.windows.name(), "sql", null,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION, null}, 		
 		 {this.final_root_msp_direct_org_user_email,common_password, SourceType.machine.name(),  SourceProduct.udp.name(), this.sub_msp1_account1_id, this.sub_msp1_account1_site1_siteId, ProtectionStatus.protect.name(), ConnectionStatus.online.name(),
									OSMajor.windows.name(), "sql", null,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION, null}, 		
 		 {this.final_sub_msp1_user_name_email,common_password, SourceType.machine.name(),  SourceProduct.udp.name(), this.sub_msp2_account1_id, this.sub_msp2_account1_site1_siteId, ProtectionStatus.protect.name(), ConnectionStatus.online.name(),
										OSMajor.windows.name(), "sql", null,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION, null}, 	
 		 {this.final_sub_msp1_user_name_email,common_password, SourceType.machine.name(),  SourceProduct.udp.name(), this.root_msp_direct_org_id, this.root_msp_direct_org_site1_siteId, ProtectionStatus.protect.name(), ConnectionStatus.online.name(),
											OSMajor.windows.name(), "sql", null,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION, null}, 	
 		 
 		 {this.final_sub_msp1_msp_account_user_name_email,common_password, SourceType.machine.name(),  SourceProduct.udp.name(), this.sub_msp2_account1_id, this.sub_msp2_account1_site1_siteId, ProtectionStatus.protect.name(), ConnectionStatus.online.name(),
												OSMajor.windows.name(), "sql", null,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION, null}, 	
		 {this.final_sub_msp1_msp_account_user_name_email,common_password, SourceType.machine.name(),  SourceProduct.udp.name(), this.root_msp_direct_org_id, this.root_msp_direct_org_site1_siteId, ProtectionStatus.protect.name(), ConnectionStatus.online.name(),
													OSMajor.windows.name(), "sql", null,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION, null}, 	
		 {this.final_sub_msp1_msp_account_user_name_email,common_password, SourceType.machine.name(),  SourceProduct.udp.name(), this.sub_msp1_account2_id, this.sub_msp1_account2_site1_siteId, ProtectionStatus.protect.name(), ConnectionStatus.online.name(),
														OSMajor.windows.name(), "sql", null,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION, null}, 	
		 
		 
 		 {this.final_sub_msp1_account1_user_email,common_password, SourceType.machine.name(),  SourceProduct.udp.name(), this.sub_msp2_account1_id, this.sub_msp2_account1_site1_siteId, ProtectionStatus.protect.name(), ConnectionStatus.online.name(),
												OSMajor.windows.name(), "sql", null,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION, null}, 	
		 {this.final_sub_msp1_account1_user_email,common_password, SourceType.machine.name(),  SourceProduct.udp.name(), this.root_msp_direct_org_id, this.root_msp_direct_org_site1_siteId, ProtectionStatus.protect.name(), ConnectionStatus.online.name(),
													OSMajor.windows.name(), "sql", null,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION, null}, 	
		 {this.final_sub_msp1_account1_user_email,common_password, SourceType.machine.name(),  SourceProduct.udp.name(), this.sub_msp1_account2_id, this.sub_msp1_account2_site1_siteId, ProtectionStatus.protect.name(), ConnectionStatus.online.name(),
														OSMajor.windows.name(), "sql", null,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION, null}, 	
		 
		 
 	 };
 	 	}
  /**
   * @testcase 
   * 2. direct admin could can't post source in other organization
   * 6. MSP admin could not post source in other organization
   * 10. account admin could not post source in other organization
   * 14. site_id is empty, post source will report error.
   * 15. set site_id as invalid, post source will report error
   * 16. set create_user_id as invalid, post source will report error
   * 17. set organization_id as invalid, post source will report error
   * 19. set source_type as invalid value, post source will be successful
   * 21. set source_product as invalid value, post source will be successful
   * 23. set protection_status as invalid value, post source will be successful
   * 25. set connection_status as invalid value, post source will be successful
   *39. create source set site_id as msp_site_id, org_id as direct org, check error code
   *3. user could not create source with invalid format source id
   *
   * 31. MSP account admin could not create source in other not mastered account
   * 32. MSP account admin could not create source in its own org
   * 33. MSP account admin could not create source in direct org

   * 35. MSP account admin login, not set org id, set site id as MSP's, failed to create source
   * root msp admin can not create source in sub msp1 account1
   * root msp msp account admin can not create source in sub msp1 account1
   * root msp direct org'admin can not create source in sub msp1 account11
   * sub msp1 admin can not create source in sub msp2 account11
   * sub msp1 admin can not create source in root msp direct org
   * sub msp1 msp account admin can not create source in sub msp2 account1
   * sub msp1 msp account admin can not create source in root msp direct org
   * sub msp1 msp account admin can not create source in root msp1 account2
   * 
   * sub msp1 account1 admin can not create source in sub msp2 account1
   * sub msp1 account1  admin can not create source in root msp direct org
   * sub msp1 account1  admin can not create source in root msp1 account2
   * 
   * 
   * 
   * @author shuo.zhang
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
   * @param expectedStatusCode
   * @param expectedErrorCode
   */
  @Test(dataProvider = "sourceInfo1", enabled=true)
  public void createSourceErrorHandlingTest(String userName, String password, String sourceType, String sourceProduct, String org_id, String siteId, String protectionStatus, 
		  String connectionStatus, String os_major, String applications,  String create_user_id, int expectedStatusCode, String expectedErrorCode, String specified_source_id) {
	  
	  	test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
	  	test.assignAuthor("shuo.zhang");
	  	spogServer.errorHandle.printInfoMessageInDebugFile("/****************createSourceErrorHandlingTest**************/");
	  
		test.log(LogStatus.INFO,"admin login");
		spogServer.errorHandle.printInfoMessageInDebugFile("admin login");
	  	spogServer.userLogin(userName, password);
	  	String plan_id = UUID.randomUUID().toString();
	  	String soureceName = spogServer.ReturnRandom("shuo_source");
	  	
		test.log(LogStatus.INFO,"create source and check");
		spogServer.errorHandle.printInfoMessageInDebugFile("create source and check");

		String source_id=null;
		if(specified_source_id==null){
			source_id= spogServer.createSourceWithCheck(soureceName, sourceType, sourceProduct, org_id, siteId,  protectionStatus, 
		  			connectionStatus, os_major, applications, create_user_id, expectedStatusCode,expectedErrorCode, true, test);
		}else{
			source_id=  spogServer.createSourceWithCheck(soureceName, SourceType.valueOf(sourceType), SourceProduct.valueOf(sourceProduct), org_id, siteId,
					 ProtectionStatus.valueOf(protectionStatus),  ConnectionStatus.valueOf(connectionStatus), os_major, applications, null, null, null, null,
					 null, null, null, null, specified_source_id, expectedStatusCode, expectedErrorCode, test);	
		}
	 	
	 
	  	if(source_id!=null){
	  		test.log(LogStatus.INFO,"delete source");
	  		spogServer.errorHandle.printInfoMessageInDebugFile("delete source");
			Response response = spogServer.deleteSourcesById(spogServer.getJWTToken(), source_id, test);
			spogServer.deleteSourcesWithCheck(response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
	  	}
		
  }
  
  /**
   * @author shuo.zhang
   * @testcase:
   * 27. Can NOT call API if not logged in - 401when JWT is missing
   * 40. can not create source with same name
   */
  @Test(enabled=true)
  
  public void createSourceWithoutLogin(){
	  
		test = rep.startTest("");
	  	test.assignAuthor("shuo.zhang");
	  	spogServer.errorHandle.printInfoMessageInDebugFile("/****************createSourceWithoutLogin**************/");
	  
		test.log(LogStatus.INFO,"admin login");
		spogServer.errorHandle.printInfoMessageInDebugFile("admin login");
	  	spogServer.userLogin(csrAdminUserName, csrAdminPassword);
	  	String plan_id = UUID.randomUUID().toString();
	  	String soureceName = spogServer.ReturnRandom("shuo_source");
	  	
		test.log(LogStatus.INFO,"create source and check");
		spogServer.errorHandle.printInfoMessageInDebugFile("create source and check");
		spogServer.createSourceWithCheck(soureceName, SourceType.machine.name(), SourceProduct.udp.name(), direct_org_id, direct_site_id,  ProtectionStatus.protect.name(), 
	  			ConnectionStatus.offline.name(), "", "", direct_user_id, SpogConstants.NOT_LOGGED_IN, ErrorCode.AUTHORIZATION_HEADER_BLANK, false, test);
	 
		String source_id = spogServer.createSourceWithCheck(soureceName, SourceType.machine.name(), SourceProduct.udp.name(), direct_org_id, direct_site_id,  ProtectionStatus.protect.name(), 
	  			ConnectionStatus.offline.name(), "", "", direct_user_id, SpogConstants.SUCCESS_POST, null, true, test);
		String source_id_1 =spogServer.createSourceWithCheck(soureceName, SourceType.machine.name(), SourceProduct.udp.name(), direct_org_id, direct_site_id,  ProtectionStatus.protect.name(), 
	  			ConnectionStatus.offline.name(), "", "", direct_user_id, SpogConstants.SUCCESS_POST, null, true, test);
		Response response = spogServer.deleteSourcesById(spogServer.getJWTToken(), source_id, test);
		spogServer.deleteSourcesWithCheck(response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		 response = spogServer.deleteSourcesById(spogServer.getJWTToken(), source_id_1, test);
		spogServer.deleteSourcesWithCheck(response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
  	
  }
  

  /**
   * 2. user could not create source with existing source id in the same organization
   * 4. user could not create source with source id which is existed in other organization
   */
  @Test( enabled=true)
  public void createSourceWithExistingSourceId() {
	  
	  	test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
	  	test.assignAuthor("shuo.zhang");
	  	spogServer.errorHandle.printInfoMessageInDebugFile("/****************createSourceWithExistingSourceId**************/");
	  
		test.log(LogStatus.INFO,"admin login");
		spogServer.errorHandle.printInfoMessageInDebugFile("admin login");
	    spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
	  	String soureceName = spogServer.ReturnRandom("shuo_source");
	  	
		test.log(LogStatus.INFO,"create source and check");
		spogServer.errorHandle.printInfoMessageInDebugFile("create source and check");
	
		String source_id = spogServer.createSourceWithCheck(soureceName, SourceType.machine, SourceProduct.udp, direct_org_id, direct_site_id,  null, 
	  			null, null, null, null, null, null, null, null, null, null, null, null, SpogConstants.SUCCESS_POST, "", test);
		test.log(LogStatus.INFO,"create source with existing source id and check");
		String new_source_id = spogServer.createSourceWithCheck(soureceName, SourceType.machine, SourceProduct.udp, direct_org_id, direct_site_id,  null, 
		  			null, null, null, null, null, null, null, null, null, null, null, source_id, SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.SOURCE_ID_EXIST, test);
		
	
	  	test.log(LogStatus.INFO,"delete source");
		spogServer.errorHandle.printInfoMessageInDebugFile("delete source");
		Response response = spogServer.deleteSourcesById(spogServer.getJWTToken(), source_id, test);
		spogServer.deleteSourcesWithCheck(response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		
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
