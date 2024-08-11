package api.organizations;

import org.testng.annotations.Test;

import Constants.ConnectionStatus;
import Constants.OSMajor;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.SPOGServer;
import InvokerServer.Org4SPOGServer;
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
import java.util.HashMap;
import java.util.UUID;

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

public class GetOrganizationsIdRecoveredresourcesTypesTest extends base.prepare.PrepareOrgInfo {
	  @Parameters({ "pmfKey"})
	public GetOrganizationsIdRecoveredresourcesTypesTest(String pmfKey) {
		super(pmfKey);
		// TODO Auto-generated constructor stub
	}
	private String  org_model_prefix=this.getClass().getSimpleName();
	  private SPOGServer spogServer;
	  private Org4SPOGServer org4SOGServer;
	  private GatewayServer gatewayServer;
	  private String csrAdminUserName;
	  private String csrAdminPassword;
	
	 // private ExtentReports rep;
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
	  
	  private String final_msp_account_admin_email;	  
	  
	  HashMap<String,String> siteTokenMap ;
	  
	  private int direct_org_instant_vm_num=1;
	  private int direct_org_vsb_num=2;
	  private int msp_instant_vm_num=0;//3
	  private int msp_vsb_num=0;//3
	  private int msp_account_instant_vm_num=5;
	  private int msp_account_vsb_num=6;
	  
	  private int rootmsp_direct_org_instant_vm_num=1;
	  private int rootmsp_direct_org_vsb_num=2;
	  private int sub_msp1__instant_vm_num=3;
	  private int sub_msp1__vsb_num=4;
	  private int sub_msp1_account1_instant_vm_num=5;
	  private  int sub_msp1_account1_vsb_num=6;
	  private int sub_msp1_account2_instant_vm_num=3;
	  private int sub_msp1_account2_vsb_num=4;
	  private int sub_msp2_account1_instant_vm_num=5;
	  private  int sub_msp2_account1_vsb_num=6;
	  private int sub_msp2_account2_instant_vm_num=3;
	  private int sub_msp2_account2_vsb_num=4;	  
	  
	  
	  
	  //this is for update portal, each testng class is taken as BQ set
	 // private SQLServerDb bqdb1;
	 // public int Nooftest;
	 // private long creationTime;
	 // private String BQName=null;
	 // private String runningMachine;
	 // private testcasescount count1;
	 // private String buildVersion;	
	  @BeforeClass
	  @Parameters({ "baseURI", "port","logFolder","buildVersion" ,"csrAdminUserName", "csrAdminPassword"})
	  public void beforeClass(String baseURI, String port, String logFolder,  String buildVersion,  String adminUserName, String adminPassword) throws UnknownHostException {
			
		  	spogServer = new SPOGServer(baseURI, port);
		  	gatewayServer =new GatewayServer(baseURI,port);
		  	rep = ExtentManager.getInstance("GetOrganizationsIdRecoveredresourcesTypesTest",logFolder);
		  	this.csrAdminUserName = adminUserName;
		  	this.csrAdminPassword = adminPassword;
		  
		  	org4SOGServer= new Org4SPOGServer(baseURI, port);
		  	
		  	
		    test = rep.startTest("beforeClass");
			spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
			//*******************create direct org,user,site**********************/
			String prefix = RandomStringUtils.randomAlphanumeric(8);
			test.log(LogStatus.INFO,"create a direct org");
			final_direct_org_name=prefix+direct_org_name;
			direct_org_id = spogServer.CreateOrganizationWithCheck(prefix+direct_org_name+org_model_prefix, SpogConstants.DIRECT_ORG, null, common_password, null, null, test);
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
			
			String userToken = spogServer.getJWTToken();
			//spogServer.setToken(siteTokenMap.get(siteId));
/**
String source_name; SourceType source_type; SourceProduct source_product;
		String organization_id; String site_id; ProtectionStatus protection_status;
		ConnectionStatus connection_status; String os_major; String applications; String vm_name;
		String hypervisor_id; String agent_name; String os_name; String os_architecture;
		String agent_current_version; String agent_upgrade_version; String agent_upgrade_link; String source_id;
		ExtentTest test;
		**/
			/**
		{final_msp_user_name_email, common_password,SourceType.instant_vm,  SourceProduct.udp,  msp_org_id, msp_site_id, ProtectionStatus.protect, ConnectionStatus.offline,
				OSMajor.windows.name(),	"exchange", null, UUID.randomUUID().toString(), null, null, null,null,null, null , null},
     {final_msp_user_name_email, common_password, SourceType.shared_folder,  SourceProduct.udp,  account_id, account_site_id, ProtectionStatus.protect, ConnectionStatus.online,
					OSMajor.windows.name(),	"sql;exchange", null, null, "shuo_proxy_1", null, null,null,null, null , null },
		{final_msp_user_name_email, common_password, SourceType.virtual_standby,  SourceProduct.udp,  msp_org_id, another_msp_site_id, ProtectionStatus.protect, ConnectionStatus.offline,
						OSMajor.windows.name(),	"sql",  null, null, null, "windows 2008 r2", null,null,null, null, null },
			
			Response response = spogServer.createSource(source_name, source_type, source_product, organization_id, site_id, protection_status, connection_status,
					os_major, applications, vm_name, hypervisor_id, agent_name, os_name, os_architecture, agent_current_version, agent_upgrade_version,
					agent_upgrade_link, source_id, test);
			
			
			  private int direct_org_instant_vm_num=1;
			  private int direct_org_vsb_num=2;
			  private int msp__instant_vm_num=3;
			  private int msp__vsb_num=4;
			  private int msp_account_instant_vm_num=5;
			  private int msp_account_vsb_num=6;
			**/
			//int direct_org_instant_vm_num=5; add direct_org source
			for(int i=1;i<=direct_org_instant_vm_num;i++){
			Response response = spogServer.createSource("source_yaner01_1_int"+i, SourceType.instant_vm, SourceProduct.udp, direct_org_id, direct_site_id, ProtectionStatus.protect, ConnectionStatus.online,
					OSMajor.windows.name(), "sql;exchange", null, null, "yaner01_proxy_1", "", "", "", "",
					"", "", test);}			
			for(int i=1;i<=direct_org_vsb_num;i++){
			Response response = spogServer.createSource("source_yaner01_1_vsb"+i, SourceType.virtual_standby, SourceProduct.udp, direct_org_id, direct_site_id, ProtectionStatus.protect, ConnectionStatus.online,
					OSMajor.windows.name(), "sql;exchange", null, null, "yaner01_proxy_1", "", "", "", "",
					"", "", test);}
			
			test.log(LogStatus.INFO,"Creating another site For direct org");
			another_direct_site_id = gatewayServer.createsite_register_login(direct_org_id, direct_user_validToken, direct_user_id, "yaner011", "1.0.0", spogServer, test);
			String another_direct_site_token=gatewayServer.getJWTToken();
			test.log(LogStatus.INFO, "The site token is "+another_direct_site_token);

			//************************create msp org,user,site*************************************
			spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
			prefix = RandomStringUtils.randomAlphanumeric(8);
			test.log(LogStatus.INFO,"create a msp org");
			msp_org_id = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name+org_model_prefix, SpogConstants.MSP_ORG, null, common_password, null, null, test);
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
			
//			siteName= spogServer.getRandomSiteName("TestCreate");
//			test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
//			sitetype=siteType.gateway.toString();
//			test.log(LogStatus.INFO,"The siteType :"+sitetype);
			
//			test.log(LogStatus.INFO,"Creating a site For msp org");
//			msp_site_id = gatewayServer.createsite_register_login(msp_org_id, msp_user_validToken, msp_user_id, "yaner01", "1.0.0", spogServer, test);
//			String msp_site_token=gatewayServer.getJWTToken();
//			test.log(LogStatus.INFO, "The site token is "+ msp_site_token);
			
//			siteName= spogServer.getRandomSiteName("TestCreate");
//			test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
//			sitetype=siteType.gateway.toString();
//			test.log(LogStatus.INFO,"The siteType :"+sitetype);
			
//			test.log(LogStatus.INFO,"Creating another site For msp org");
//			another_msp_site_id = gatewayServer.createsite_register_login(msp_org_id, msp_user_validToken, msp_user_id, "yaner011", "1.0.0", spogServer, test);
//			String another_msp_site_token=gatewayServer.getJWTToken();
//			test.log(LogStatus.INFO, "The site token is "+ another_msp_site_token);
			
			//int direct_org_instant_vm_num=5; add MSP source
//			for(int i=1;i<=msp_instant_vm_num;i++){
//			Response response = spogServer.createSource("MSP_source_yaner01_1_int"+i, SourceType.instant_vm, SourceProduct.udp, msp_org_id, msp_site_id, ProtectionStatus.protect, ConnectionStatus.online,
//					OSMajor.windows.name(), "sql;exchange", null, null, "yaner01_proxy_1", "", "", "", "",
//					"", "", test);}			
//			for(int i=1;i<=msp_vsb_num;i++){
//			Response response = spogServer.createSource("MSP_source_yaner01_1_vsb"+i, SourceType.virtual_standby, SourceProduct.udp, msp_org_id, msp_site_id, ProtectionStatus.protect, ConnectionStatus.online,
//					OSMajor.windows.name(), "sql;exchange", null, null, "yaner01_proxy_1", "", "", "", "",
//					"", "", test);}
			

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

			//int direct_org_instant_vm_num=5; add MSP account source
			for(int i=1;i<=msp_account_instant_vm_num;i++){
			Response response = spogServer.createSource("MSP_account_source_yaner01_1_int"+i, SourceType.instant_vm, SourceProduct.udp, account_id, account_site_id, ProtectionStatus.protect, ConnectionStatus.online,
					OSMajor.windows.name(), "sql;exchange", null, null, "yaner01_proxy_1", "", "", "", "",
					"", "", test);}			
			for(int i=1;i<=msp_account_vsb_num;i++){
			Response response = spogServer.createSource("MSP_account_source_yaner01_1_vsb"+i, SourceType.virtual_standby, SourceProduct.udp, account_id, account_site_id, ProtectionStatus.protect, ConnectionStatus.online,
					OSMajor.windows.name(), "sql;exchange", null, null, "yaner01_proxy_1", "", "", "", "",
					"", "", test);}
			
			
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
			
			// parepare root MSP 2 tires orgs' VSB and IVM
			
			  
			  /**
			//int rootmsp_direct_org_instant_vm_num=1; add direct_org source
			for(int i=1;i<=this.rootmsp_direct_org_instant_vm_num;i++){
			Response response = spogServer.createSource("source_yaner01_1_int"+i, SourceType.instant_vm, SourceProduct.udp, this.root_msp_direct_org_id, this.root_msp_direct_org_site1_siteId, ProtectionStatus.protect, ConnectionStatus.online,
					OSMajor.windows.name(), "sql;exchange", null, null, "yaner01_proxy_1", "", "", "", "",
					"", "", test);}			
			for(int i=1;i<=this.direct_org_vsb_num;i++){
			Response response = spogServer.createSource("source_yaner01_1_vsb"+i, SourceType.virtual_standby, SourceProduct.udp, this.root_msp_direct_org_id, this.root_msp_direct_org_site1_siteId, ProtectionStatus.protect, ConnectionStatus.online,
					OSMajor.windows.name(), "sql;exchange", null, null, "yaner01_proxy_1", "", "", "", "",
					"", "", test);}


			//int rootmsp_direct_org_instant_vm_num=1; add direct_org source
			for(int i=1;i<=this.sub_msp1_account1_instant_vm_num;i++){
			Response response = spogServer.createSource("source_yaner01_1_int"+i, SourceType.instant_vm, SourceProduct.udp, this.sub_msp1_account1_id, this.sub_msp1_account1_site1_siteId, ProtectionStatus.protect, ConnectionStatus.online,
					OSMajor.windows.name(), "sql;exchange", null, null, "yaner01_proxy_1", "", "", "", "",
					"", "", test);}			
			for(int i=1;i<=this.sub_msp1__vsb_num;i++){
			Response response = spogServer.createSource("source_yaner01_1_vsb"+i, SourceType.virtual_standby, SourceProduct.udp, this.sub_msp1_account1_id, this.sub_msp1_account1_site1_siteId, ProtectionStatus.protect, ConnectionStatus.online,
					OSMajor.windows.name(), "sql;exchange", null, null, "yaner01_proxy_1", "", "", "", "",
					"", "", test);}			
			*/
			//test.log(LogStatus.INFO,"Creating another site For direct org");
			//another_direct_site_id = gatewayServer.createsite_register_login(direct_org_id, direct_user_validToken, direct_user_id, "yaner011", "1.0.0", spogServer, test);
			//String another_direct_site_token=gatewayServer.getJWTToken();
			//test.log(LogStatus.INFO, "The site token is "+another_direct_site_token);
	  }
	  
	  /**
	   * @author Eric.Yang
	   * @testcase: new cases about    root MSP 2tiers 
	   */		  
	  // 1. root msp account admin could get amount for source type virtual_standby and instant_vm by specified its direct orgnization id	  
			@Test
			@Parameters({  "csrAdminUserName", "csrAdminPassword" })
			public void root_msp_account_adminGetAmountForSourceTypeVSBandInstantVMbydirect_OrgId(String AdminUserName, String AdminPassword) {
				test=rep.startTest("root_msp_account_adminGetAmountForSourceTypeVSBandInstantVMbydirect_OrgId");
				test.assignAuthor("Eric Yang");
				this.csrAdminUserName=AdminUserName;
				this.csrAdminPassword=AdminPassword;
				spogServer.userLogin(this.final_root_msp_account_admin_user_name_email, this.common_password);
				test.log(LogStatus.INFO,"Getting the token of final_msp_account_admin_email");
				String validToken=spogServer.getJWTToken();
				//test.log(LogStatus.INFO, "Getting the logged in user for direct_user");
				//String userID=spogServer.GetLoggedinUser_UserID();
				test.log(LogStatus.INFO, "final_root_msp_user_name_email get amount for source type VSB and instant_vm by orgnization id");
						org4SOGServer.getOrganizationsIdRecoversourceTypesWithCheck(validToken, this.root_msp_direct_org_id, SpogConstants.SUCCESS_GET_PUT_DELETE,0,0, test);
				}
			  // 1. sub msp account admin could get amount for source type virtual_standby and instant_vm by specified its direct orgnization id	  
					@Test
					@Parameters({  "csrAdminUserName", "csrAdminPassword" })
					public void sub_msp_account_adminGetAmountForSourceTypeVSBandInstantVMbydirect_OrgId(String AdminUserName, String AdminPassword) {
						test=rep.startTest("sub_msp_account_adminGetAmountForSourceTypeVSBandInstantVMbydirect_OrgId");
						test.assignAuthor("Eric Yang");
						this.csrAdminUserName=AdminUserName;
						this.csrAdminPassword=AdminPassword;
						spogServer.userLogin(this.final_sub_msp1_msp_account_user_name_email, this.common_password);
						test.log(LogStatus.INFO,"Getting the token of final_msp_account_admin_email");
						String validToken=spogServer.getJWTToken();
						//test.log(LogStatus.INFO, "Getting the logged in user for direct_user");
						//String userID=spogServer.GetLoggedinUser_UserID();
						test.log(LogStatus.INFO, "final_root_msp_user_name_email get amount for source type VSB and instant_vm by orgnization id");
								org4SOGServer.getOrganizationsIdRecoversourceTypesWithCheck(validToken, this.sub_msp1_account1_id, SpogConstants.SUCCESS_GET_PUT_DELETE,0,0, test);
						}			
			
	  
			  // 2. root msp account admin could NOT get amount for source type virtual_standby and instant_vm by specified its sub MSP's direct orgnization id	  
					@Test
					@Parameters({  "csrAdminUserName", "csrAdminPassword" })
					public void root_msp_account_adminNOTGetAmountForSourceTypeVSBandInstantVMbysub_msp1_direct_OrgId(String AdminUserName, String AdminPassword) {
						test=rep.startTest("root_msp_account_adminNOTGetAmountForSourceTypeVSBandInstantVMbysub_msp1_direct_OrgId");
						test.assignAuthor("Eric Yang");
						this.csrAdminUserName=AdminUserName;
						this.csrAdminPassword=AdminPassword;
						spogServer.userLogin(this.final_root_msp_account_admin_user_name_email, this.common_password);
						test.log(LogStatus.INFO,"Getting the token of final_msp_account_admin_email");
						String validToken=spogServer.getJWTToken();
						//test.log(LogStatus.INFO, "Getting the logged in user for direct_user");
						//String userID=spogServer.GetLoggedinUser_UserID();
						test.log(LogStatus.INFO, "final_root_msp_user_name_email get amount for source type VSB and instant_vm by orgnization id");
								org4SOGServer.getOrganizationsIdRecoversourceTypesWithCheck(validToken, this.sub_msp1_account1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,this.sub_msp1_account1_instant_vm_num,this.sub_msp1_account1_vsb_num, test);
						}			
					  // 2. root msp account admin could NOT get amount for source type virtual_standby and instant_vm by specified its sub MSP1's orgnization id	  
							@Test
							@Parameters({  "csrAdminUserName", "csrAdminPassword" })
							public void root_msp_account_adminNOTGetAmountForSourceTypeVSBandInstantVMbysub_msp1_OrgId(String AdminUserName, String AdminPassword) {
								test=rep.startTest("root_msp_account_adminNOTGetAmountForSourceTypeVSBandInstantVMbysub_msp1_OrgId");
								test.assignAuthor("Eric Yang");
								this.csrAdminUserName=AdminUserName;
								this.csrAdminPassword=AdminPassword;
								spogServer.userLogin(this.final_root_msp_account_admin_user_name_email, this.common_password);
								test.log(LogStatus.INFO,"Getting the token of final_msp_account_admin_email");
								String validToken=spogServer.getJWTToken();
								//test.log(LogStatus.INFO, "Getting the logged in user for direct_user");
								//String userID=spogServer.GetLoggedinUser_UserID();
								test.log(LogStatus.INFO, "final_root_msp_user_name_email get amount for source type VSB and instant_vm by orgnization id");
										org4SOGServer.getOrganizationsIdRecoversourceTypesWithCheck(validToken, this.sub_msp1_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS,this.sub_msp1__instant_vm_num,this.sub_msp1__vsb_num, test);
								}
							
							  //  sub MSP1 direct1 account admin could NOT get amount for source type virtual_standby and instant_vm by specified its sub msp1 direct2 orgnization id	  
							@Test
							@Parameters({  "csrAdminUserName", "csrAdminPassword" })
							public void root_msp1_account1_adminNOTGetAmountForSourceTypeVSBandInstantVMbysub_msp1_direct2_OrgId(String AdminUserName, String AdminPassword) {
								test=rep.startTest("root_msp1_account1_adminNOTGetAmountForSourceTypeVSBandInstantVMbysub_msp1_direct2_OrgId");
								test.assignAuthor("Eric Yang");
								this.csrAdminUserName=AdminUserName;
								this.csrAdminPassword=AdminPassword;
								spogServer.userLogin(this.final_sub_msp1_account1_user_email, this.common_password);
								test.log(LogStatus.INFO,"Getting the token of final_msp_account_admin_email");
								String validToken=spogServer.getJWTToken();
								//test.log(LogStatus.INFO, "Getting the logged in user for direct_user");
								//String userID=spogServer.GetLoggedinUser_UserID();
								test.log(LogStatus.INFO, "final_root_msp_user_name_email get amount for source type VSB and instant_vm by orgnization id");
										org4SOGServer.getOrganizationsIdRecoversourceTypesWithCheck(validToken, this.sub_msp1_account2_id, SpogConstants.INSUFFICIENT_PERMISSIONS,this.sub_msp1_account2_instant_vm_num,this.sub_msp1_account2_vsb_num, test);
								}
							
							  //  sub MSP1 direct2 account  could NOT get amount for source type virtual_standby and instant_vm by specified its sub msp2 direct1 orgnization id	  
							@Test
							@Parameters({  "csrAdminUserName", "csrAdminPassword" })
							public void root_msp1_account2_adminNOTGetAmountForSourceTypeVSBandInstantVMbysub_msp2_direct1_OrgId(String AdminUserName, String AdminPassword) {
								test=rep.startTest("root_msp1_account2_adminNOTGetAmountForSourceTypeVSBandInstantVMbysub_msp2_direct1_OrgId");
								test.assignAuthor("Eric Yang");
								this.csrAdminUserName=AdminUserName;
								this.csrAdminPassword=AdminPassword;
								spogServer.userLogin(this.final_sub_msp1_account2_user_email, this.common_password);
								test.log(LogStatus.INFO,"Getting the token of final_msp_account_admin_email");
								String validToken=spogServer.getJWTToken();
								//test.log(LogStatus.INFO, "Getting the logged in user for direct_user");
								//String userID=spogServer.GetLoggedinUser_UserID();
								test.log(LogStatus.INFO, "final_root_msp_user_name_email get amount for source type VSB and instant_vm by orgnization id");
										org4SOGServer.getOrganizationsIdRecoversourceTypesWithCheck(validToken, this.sub_msp2_account1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,this.sub_msp1_account1_instant_vm_num,this.sub_msp2_account1_vsb_num, test);
								}							
	 ///////////////////////////////// /////////////////////////////// /////////////////////////////// /////////////////////////////// 
	  
	  /**
	   * @author Eric.Yang
	   * @testcase:
	   * 1. direct admin could get amount for source type virtual_standby and instant_vm by specified orgnization id
2.MSP admin could get amount for source type virtual_standby and instant_vm by specified orgnization id
3.MSP admin could get amount for source type virtual_standby and instant_vm by specified sub-organization id
4.csr admin could get amount for source type virtual_standby and instant_vm by specified direct organization id
5.csr admin could get amount for source type virtual_standby and instant_vm by specified msp organization id
6.csr admin could get amount for source type virtual_standby and instant_vm by specified MSP sub-organization id
7.account user could get amount for source type virtual_standby and instant_vm by specified orgnization id
7_1.MSP account admin could get amount for source type virtual_standby and instant_vm by specified orgnization id
8.direct admin could not get amount for source type virtual_standby and instant_vm by msp organization id
9.direct admin could not get amount for source type virtual_standby and instant_vm by msp sub organization id, report 403
10.msp admin could not get amount for source type virtual_standby and instant_vm by direct organization id, report 403
11.sub org admin could not get amount for source type virtual_standby and instant_vm by its parent organization it, report 403
12.sub org admin could not get amount for source type virtual_standby and instant_vm by direct organization id, report 403
13.MSP account admin could not get amount for source type virtual_standby and instant_vm by direct organization id, report 403
14.MSP account admin could not get amount for source type virtual_standby and instant_vm by msp organization id, report 403
15.MSP account admin could not get amount for source type virtual_standby and instant_vm by other sub organization id, report 403
16.could not get amount for source type virtual_standby and instant_vm without token, report401
17.could not get amount for source type virtual_standby and instant_vm with invalid token
	   * 
	   * 
	   * 
	   */	  

	  // 1. direct admin could get amount for source type virtual_standby and instant_vm by specified orgnization id	  
		@Test
		@Parameters({  "csrAdminUserName", "csrAdminPassword" })
		public void directAdminGetAmountForSourceTypeVSBandInstantVMbyOrgId(String AdminUserName, String AdminPassword) {
			test=rep.startTest("directAdminGetAmountForSourceTypeVSBandInstantVMbyOrgId");
			test.assignAuthor("Eric Yang");
			this.csrAdminUserName=AdminUserName;
			this.csrAdminPassword=AdminPassword;
			spogServer.userLogin(this.final_direct_user_name_email, this.common_password);
			test.log(LogStatus.INFO,"Getting the token of the direct_user");
			String validToken=spogServer.getJWTToken();
			//test.log(LogStatus.INFO, "Getting the logged in user for direct_user");
			//String userID=spogServer.GetLoggedinUser_UserID();
			test.log(LogStatus.INFO, "direct admin get amount for source type VSB and instant_vm by orgnization id");
					org4SOGServer.getOrganizationsIdRecoversourceTypesWithCheck(validToken, direct_org_id, SpogConstants.SUCCESS_GET_PUT_DELETE,direct_org_instant_vm_num,direct_org_vsb_num, test);
			}
		//2.MSP admin could get amount for source type virtual_standby and instant_vm by specified orgnization id
		@Test
		@Parameters({  "csrAdminUserName", "csrAdminPassword" })
		
		public void MSPAdminGetAmountForSourceTypeVSBandInstantVMbyOrgId(String AdminUserName, String AdminPassword) {
			test=rep.startTest("MSPAdminGetAmountForSourceTypeVSBandInstantVMbyOrgId");
			test.assignAuthor("Eric Yang");
			this.csrAdminUserName=AdminUserName;
			this.csrAdminPassword=AdminPassword;
			spogServer.userLogin(this.final_msp_user_name_email, this.common_password);
			test.log(LogStatus.INFO,"Getting the token of the MSP admin");
			String validToken=spogServer.getJWTToken();
			test.log(LogStatus.INFO, "MSP admin get amount for source type VSB and instant_vm by orgnization id");
					org4SOGServer.getOrganizationsIdRecoversourceTypesWithCheck(validToken, msp_org_id, SpogConstants.SUCCESS_GET_PUT_DELETE,msp_instant_vm_num,msp_vsb_num, test);
			}		
		//3.MSP admin could get amount for source type virtual_standby and instant_vm by specified sub-organization id		
		@Test
		@Parameters({  "csrAdminUserName", "csrAdminPassword" })
		public void MSPAdminGetAmountForSourceTypeVSBandInstantVMbySubOrgId(String AdminUserName, String AdminPassword) {
			test=rep.startTest("MSPAdminGetAmountForSourceTypeVSBandInstantVMbySubOrgId");
			test.assignAuthor("Eric Yang");
			this.csrAdminUserName=AdminUserName;
			this.csrAdminPassword=AdminPassword;
			spogServer.userLogin(this.final_msp_user_name_email, this.common_password);
			test.log(LogStatus.INFO,"Getting the token of the MSP admin");
			String validToken=spogServer.getJWTToken();
			test.log(LogStatus.INFO, "MSP admin get amount for source type VSB and instant_vm by sub orgnization id");
					org4SOGServer.getOrganizationsIdRecoversourceTypesWithCheck(validToken, account_id, SpogConstants.SUCCESS_GET_PUT_DELETE,msp_account_instant_vm_num,msp_account_vsb_num, test);
			}	
		//4.csr admin could get amount for source type virtual_standby and instant_vm by specified direct organization id
		@Test
		@Parameters({  "csrAdminUserName", "csrAdminPassword" })
		public void CSRAdminGetAmountForSourceTypeVSBandInstantVMbyDirectOrgId(String AdminUserName, String AdminPassword) {
			test=rep.startTest("CSRAdminGetAmountForSourceTypeVSBandInstantVMbyDirectOrgId");
			test.assignAuthor("Eric Yang");
			this.csrAdminUserName=AdminUserName;
			this.csrAdminPassword=AdminPassword;
			spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
			test.log(LogStatus.INFO,"Getting the token of the CSR admin");
			String validToken=spogServer.getJWTToken();
			test.log(LogStatus.INFO, "CSR admin get amount for source type VSB and instant_vm by direct orgnization id");
					org4SOGServer.getOrganizationsIdRecoversourceTypesWithCheck(validToken, direct_org_id, SpogConstants.SUCCESS_GET_PUT_DELETE,direct_org_instant_vm_num,direct_org_vsb_num, test);
			}			
		//5.csr admin could get amount for source type virtual_standby and instant_vm by specified msp organization id
		@Test
		@Parameters({  "csrAdminUserName", "csrAdminPassword" })
		public void CSRAdminGetAmountForSourceTypeVSBandInstantVMbyMSPOrgId(String AdminUserName, String AdminPassword) {
			test=rep.startTest("CSRAdminGetAmountForSourceTypeVSBandInstantVMbyMSPOrgId");
			test.assignAuthor("Eric Yang");
			this.csrAdminUserName=AdminUserName;
			this.csrAdminPassword=AdminPassword;
			spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
			test.log(LogStatus.INFO,"Getting the token of the CSR admin");
			String validToken=spogServer.getJWTToken();
			test.log(LogStatus.INFO, "CSR admin get amount for source type VSB and instant_vm by MSPt orgnization id");
					org4SOGServer.getOrganizationsIdRecoversourceTypesWithCheck(validToken, msp_org_id, SpogConstants.SUCCESS_GET_PUT_DELETE,msp_instant_vm_num,msp_vsb_num, test);
			}			
		//6.csr admin could get amount for source type virtual_standby and instant_vm by specified sub-organization id
		@Test
		@Parameters({  "csrAdminUserName", "csrAdminPassword" })
		public void CSRAdminGetAmountForSourceTypeVSBandInstantVMbyMSPSubOrgId(String AdminUserName, String AdminPassword) {
			test=rep.startTest("CSRAdminGetAmountForSourceTypeVSBandInstantVMbyMSPSubOrgId");
			test.assignAuthor("Eric Yang");
			this.csrAdminUserName=AdminUserName;
			this.csrAdminPassword=AdminPassword;
			spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
			test.log(LogStatus.INFO,"Getting the token of the CSR admin");
			String validToken=spogServer.getJWTToken();
			test.log(LogStatus.INFO, "CSR admin get amount for source type VSB and instant_vm by MSP sub orgnization id");
					org4SOGServer.getOrganizationsIdRecoversourceTypesWithCheck(validToken, account_id, SpogConstants.SUCCESS_GET_PUT_DELETE,msp_account_instant_vm_num,msp_account_vsb_num, test);
			}		
		//7.account user could get amount for source type virtual_standby and instant_vm by specified account id
		@Test
		@Parameters({  "csrAdminUserName", "csrAdminPassword" })
		public void accountUserGetAmountForSourceTypeVSBandInstantVMbyAccountId(String AdminUserName, String AdminPassword) {
			test=rep.startTest("accountAdminGetAmountForSourceTypeVSBandInstantVMbyAccountId");
			test.assignAuthor("Eric Yang");
			this.csrAdminUserName=AdminUserName;
			this.csrAdminPassword=AdminPassword;
			spogServer.userLogin(account_user_email, common_password);
			test.log(LogStatus.INFO,"Getting the token of the MSP account user");
			String validToken=spogServer.getJWTToken();
			test.log(LogStatus.INFO, "account user can get amount for source type VSB and instant_vm by MSP sub orgnization id");
					org4SOGServer.getOrganizationsIdRecoversourceTypesWithCheck(validToken, account_id, SpogConstants.SUCCESS_GET_PUT_DELETE,msp_account_instant_vm_num,msp_account_vsb_num, test);
			}
		//7_1.MSP account admin could get amount for source type virtual_standby and instant_vm by specified account id
		//8.direct admin could not get amount for source type virtual_standby and instant_vm by msp organization id
		@Test
		@Parameters({  "csrAdminUserName", "csrAdminPassword" })
		public void directAdminNotGetAmountForSourceTypeVSBandInstantVMbyMSPOrgId(String AdminUserName, String AdminPassword) {
			test=rep.startTest("directAdminNotGetAmountForSourceTypeVSBandInstantVMbyMSPOrgId");
			test.assignAuthor("Eric Yang");
			this.csrAdminUserName=AdminUserName;
			this.csrAdminPassword=AdminPassword;
			spogServer.userLogin(final_direct_user_name_email, common_password);
			test.log(LogStatus.INFO,"Getting the token of the direct admin");
			String validToken=spogServer.getJWTToken();
			test.log(LogStatus.INFO, "direct admin could not get amount for source type VSB and instant_vm by MSP orgnization id");
					org4SOGServer.getOrganizationsIdRecoversourceTypesWithCheck(validToken, msp_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS,msp_instant_vm_num,msp_vsb_num, test);
			}		
		//9.direct admin could not get amount for source type virtual_standby and instant_vm by msp sub organization id, report 403
		@Test
		@Parameters({  "csrAdminUserName", "csrAdminPassword" })
		public void directAdminNotGetAmountForSourceTypeVSBandInstantVMbyMSPSubOrgId(String AdminUserName, String AdminPassword) {
			test=rep.startTest("directAdminNotGetAmountForSourceTypeVSBandInstantVMbyMSPSubOrgId");
			test.assignAuthor("Eric Yang");
			this.csrAdminUserName=AdminUserName;
			this.csrAdminPassword=AdminPassword;
			spogServer.userLogin(final_direct_user_name_email, common_password);
			test.log(LogStatus.INFO,"Getting the token of the direct admin");
			String validToken=spogServer.getJWTToken();
			test.log(LogStatus.INFO, "direct admin could not get amount for source type VSB and instant_vm by MSP sub orgnization id");
					org4SOGServer.getOrganizationsIdRecoversourceTypesWithCheck(validToken, account_id, SpogConstants.INSUFFICIENT_PERMISSIONS,msp_account_instant_vm_num,msp_account_vsb_num, test);
			}				
		//10.msp admin could not get amount for source type virtual_standby and instant_vm by direct organization id, report 403
		@Test
		@Parameters({  "csrAdminUserName", "csrAdminPassword" })
		public void MSPAdminNotGetAmountForSourceTypeVSBandInstantVMbyDirectOrgId(String AdminUserName, String AdminPassword) {
			test=rep.startTest("MSPAdminNotGetAmountForSourceTypeVSBandInstantVMbyDirectOrgId");
			test.assignAuthor("Eric Yang");
			this.csrAdminUserName=AdminUserName;
			this.csrAdminPassword=AdminPassword;
			spogServer.userLogin(final_msp_user_name_email, common_password);
			test.log(LogStatus.INFO,"Getting the token of the direct admin");
			String validToken=spogServer.getJWTToken();
			test.log(LogStatus.INFO, "direct admin could not get amount for source type VSB and instant_vm by MSP sub orgnization id");
					org4SOGServer.getOrganizationsIdRecoversourceTypesWithCheck(validToken, direct_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS,direct_org_instant_vm_num,direct_org_vsb_num, test);
			}			
		//11.sub org admin could not get amount for source type virtual_standby and instant_vm by its parent organization it, report 403
		@Test
		@Parameters({  "csrAdminUserName", "csrAdminPassword" })
		public void SubOrgAdminNotGetAmountForSourceTypeVSBandInstantVMbyParentOrgId(String AdminUserName, String AdminPassword) {
			test=rep.startTest("MSPAdminNotGetAmountForSourceTypeVSBandInstantVMbyDirectOrgId");
			test.assignAuthor("Eric Yang");
			this.csrAdminUserName=AdminUserName;
			this.csrAdminPassword=AdminPassword;
			spogServer.userLogin(account_user_email, common_password);
			test.log(LogStatus.INFO,"Getting the token of the direct admin");
			String validToken=spogServer.getJWTToken();
			test.log(LogStatus.INFO, "Sub org admin could not get amount for source type VSB and instant_vm by Parent orgnization id");
					org4SOGServer.getOrganizationsIdRecoversourceTypesWithCheck(validToken, msp_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS,msp_instant_vm_num,msp_vsb_num, test);
			}
		//12.sub org admin could not get amount for source type virtual_standby and instant_vm by direct organization id, report 403
		@Test
		@Parameters({  "csrAdminUserName", "csrAdminPassword" })
		public void SubOrgAdminNotGetAmountForSourceTypeVSBandInstantVMbyDirectOrgId(String AdminUserName, String AdminPassword) {
			test=rep.startTest("MSPAdminNotGetAmountForSourceTypeVSBandInstantVMbyDirectOrgId");
			test.assignAuthor("Eric Yang");
			this.csrAdminUserName=AdminUserName;
			this.csrAdminPassword=AdminPassword;
			spogServer.userLogin(account_user_email, common_password);
			test.log(LogStatus.INFO,"Getting the token of the direct admin");
			String validToken=spogServer.getJWTToken();
			test.log(LogStatus.INFO, "Sub org admin could not get amount for source type VSB and instant_vm by Direct orgnization id");
					org4SOGServer.getOrganizationsIdRecoversourceTypesWithCheck(validToken, direct_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS,direct_org_instant_vm_num,direct_org_vsb_num, test);
			}		
		//13.MSP account admin could not get amount for source type virtual_standby and instant_vm by direct organization id, report 403
		//14.MSP account admin could not get amount for source type virtual_standby and instant_vm by msp organization id, report 403
		//15.MSP account admin could not get amount for source type virtual_standby and instant_vm by other sub organization id, report 403

		//16.could not get amount for source type virtual_standby and instant_vm without token, report401
		@Test
		@Parameters({  "csrAdminUserName", "csrAdminPassword" })
		public void AdminNotGetAmountForSourceTypeVSBandInstantVMWithoutToken(String AdminUserName, String AdminPassword) {
			test=rep.startTest("AdminNotGetAmountForSourceTypeVSBandInstantVMWithoutToken");
			test.assignAuthor("Eric Yang");
			this.csrAdminUserName=AdminUserName;
			this.csrAdminPassword=AdminPassword;
			spogServer.userLogin(final_direct_user_name_email, common_password);
			test.log(LogStatus.INFO,"Getting the token of the direct admin");
			String validToken=spogServer.getJWTToken();
			test.log(LogStatus.INFO, "could not get amount for source type virtual_standby and instant_vm without token, report401");
					org4SOGServer.getOrganizationsIdRecoversourceTypesWithCheck("", direct_org_id, SpogConstants.NOT_LOGGED_IN,direct_org_instant_vm_num,direct_org_vsb_num, test);
			}		
		//17.could not get amount for source type virtual_standby and instant_vm with invalid token
		@Test
		@Parameters({  "csrAdminUserName", "csrAdminPassword" })
		public void AdminNotGetAmountForSourceTypeVSBandInstantVMWithInvalidToken(String AdminUserName, String AdminPassword) {
			test=rep.startTest("AdminNotGetAmountForSourceTypeVSBandInstantVMWithInvalidToken");
			test.assignAuthor("Eric Yang");
			this.csrAdminUserName=AdminUserName;
			this.csrAdminPassword=AdminPassword;
			spogServer.userLogin(final_direct_user_name_email, common_password);
			test.log(LogStatus.INFO,"Getting the token of the direct admin");
			String validToken=spogServer.getJWTToken()+"1";
			test.log(LogStatus.INFO, "could not get amount for source type virtual_standby and instant_vm with invalid token");
					org4SOGServer.getOrganizationsIdRecoversourceTypesWithCheck(validToken, direct_org_id, SpogConstants.NOT_LOGGED_IN,direct_org_instant_vm_num,direct_org_vsb_num, test);
			}
		
		//18. csr read only user could get  amount for source type virtual_standby and instant_vm with direct org
		@Test
		@Parameters({  "csrReadOnlyAdminUserName", "csrReadOnlyAdminPassword" })
		public void CSRReadOnlyAdminGetAmountForSourceTypeVSBandInstantVMbyDirectOrgId(String AdminUserName, String AdminPassword) {
			test=rep.startTest("CSRReadOnlyAdminGetAmountForSourceTypeVSBandInstantVMbyDirectOrgId");
			test.assignAuthor("Eric Yang");
		
			spogServer.userLogin(AdminUserName,AdminPassword);
			test.log(LogStatus.INFO,"Getting the token of the CSR readonly admin");
			String validToken=spogServer.getJWTToken();
			test.log(LogStatus.INFO, "CSR admin get amount for source type VSB and instant_vm by direct orgnization id");
					org4SOGServer.getOrganizationsIdRecoversourceTypesWithCheck(validToken, direct_org_id, SpogConstants.SUCCESS_GET_PUT_DELETE,direct_org_instant_vm_num,direct_org_vsb_num, test);
			}			
		//19. csr read only user could get  amount for source type virtual_standby and instant_vm with msp org
		@Test
		@Parameters({  "csrReadOnlyAdminUserName", "csrReadOnlyAdminPassword" })
		public void CSRREadOnlyAdminGetAmountForSourceTypeVSBandInstantVMbyMSPOrgId(String AdminUserName, String AdminPassword) {
			test=rep.startTest("CSRREadOnlyAdminGetAmountForSourceTypeVSBandInstantVMbyMSPOrgId");
			test.assignAuthor("Eric Yang");
		
			spogServer.userLogin(AdminUserName,AdminPassword);
			test.log(LogStatus.INFO,"Getting the token of the CSR admin");
			String validToken=spogServer.getJWTToken();
			test.log(LogStatus.INFO, "CSR admin get amount for source type VSB and instant_vm by MSPt orgnization id");
					org4SOGServer.getOrganizationsIdRecoversourceTypesWithCheck(validToken, msp_org_id, SpogConstants.SUCCESS_GET_PUT_DELETE,msp_instant_vm_num,msp_vsb_num, test);
			}
		
		//20.csr read only admin could get amount for source type virtual_standby and instant_vm by specified sub-organization id
		@Test
		@Parameters({  "csrReadOnlyAdminUserName", "csrReadOnlyAdminPassword" })
		public void CSRReadOnlyAdminGetAmountForSourceTypeVSBandInstantVMbyMSPSubOrgId(String AdminUserName, String AdminPassword) {
			test=rep.startTest("CSRReadOnlyAdminGetAmountForSourceTypeVSBandInstantVMbyMSPSubOrgId");
			test.assignAuthor("Eric Yang");
			spogServer.userLogin(AdminUserName,AdminPassword);
			test.log(LogStatus.INFO,"Getting the token of the CSR admin");
			String validToken=spogServer.getJWTToken();
			test.log(LogStatus.INFO, "CSR admin get amount for source type VSB and instant_vm by MSP sub orgnization id");
			org4SOGServer.getOrganizationsIdRecoversourceTypesWithCheck(validToken, account_id, SpogConstants.SUCCESS_GET_PUT_DELETE,msp_account_instant_vm_num,msp_account_vsb_num, test);
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
