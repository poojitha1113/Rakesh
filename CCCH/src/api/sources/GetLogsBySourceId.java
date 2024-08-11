package api.sources;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.joda.time.LocalDate;
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
import Constants.LogSeverityType;
import Constants.LogSourceType;
import Constants.OSMajor;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import Constants.volume_type;
import InvokerServer.GatewayServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import InvokerServer.GatewayServer.siteType;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import invoker.SiteTestHelper;
import io.restassured.response.Response;
import InvokerServer.Log4SPOGServer;

public class GetLogsBySourceId extends base.prepare.Is4Org{

	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private Log4SPOGServer log4SPOGServer;
	private UserSpogServer userSpogServer;
	private String csrAdminUserName;
	private String csrAdminPassword;
	private String csr_readonly_email;
	private String csr_readonly_token;
	private String csr_readonly_user_id;
	private ExtentTest test;
	private SPOGDestinationServer spogDestinationServer;

	//this is for update portal, each testng class is taken as BQ set
    /*private ExtentReports rep;
    private SQLServerDb bqdb1;
    public int Nooftest;
    private long creationTime;
    private String BQName=null;
    private String runningMachine;
    private testcasescount count1;
    private String buildVersion;*/

	private String postfix_email = "@arcserve.com";
	private String common_password = "Mclaren@2020";

	private String prefix_direct = "SPOG_QA_RAKESH_BQ_DIRECT";
	private String direct_org_name = prefix_direct + "_org";
	private String direct_org_id=null;
	private String direct_user_name = prefix_direct + "_admin";
	private String direct_user_name_email = direct_user_name + postfix_email;
	private String direct_user_first_name = direct_user_name + "_first_name";
	private String direct_user_last_name = direct_user_name + "_last_name";
	private String direct_user_id =null;
	private String final_direct_user_name_email = null;
	private String direct_site_id;
	private String direct_site_token;

	private String prefix_msp = "SPOG_QA_RAKESH_BQ_MSP";
	private String msp_org_name = prefix_msp + "_org";
	private String msp_org_email = msp_org_name + postfix_email;
	private String msp_org_first_name = msp_org_name + "_first_name";
	private String msp_org_last_name = msp_org_name + "_last_name";
	private String msp_user_name = prefix_msp + "_admin";
	private String msp_user_name_email = prefix_msp + "_admin" + postfix_email;
	private String msp_user_first_name = msp_user_name + "_first_name";
	private String msp_user_last_name = msp_user_name + "_last_name";
	private String msp_org_id;
	private String final_msp_user_name_email;
	private String msp_user_id;
	private String msp_site_id;
	private String msp_site_token;

	private String account_id;
	private String account_user_email;
	private String account_user_id;
	private String account_site_id;
	private String another_account_site_id;
	private String account_site_token;
	private String another_msp_site_token;



	private String test_log_Message_1 = "testLogMessage";
	private String test_log_Message_2 = "connect_node_failed_test_message";
	private String test_log_Message_3 = "testLogMessageWithoutData";
	private	String job_Type = "backup,restore,copy,merge";/*conversion*vm_backup,vm_recovery,vm_catalog_fs,mount_recovery_point,office365_backup,cifs_backup,sharepoint_backup,vm_merge,catalog_fs,catalog_app,catalog_grt,file_copy_backup,file_copy_purge,file_copy_restore,file_copy_catalog_sync,file_copy_source_delete,file_copy_delete,catalog_fs_ondemand,vm_catalog_fs_ondemand,rps_replicate,rps_replicate_in_bound,rps_merge,rps_conversion,bmr,rps_data_seeding,rps_data_seeding_in,vm_recovery_hyperv,rps_purge_datastore,start_instant_vm,stop_instant_vm,assure_recovery,start_instant_vhd,stop_instant_vhd,archive_to_tape,linux_instant_vm";*/
	private	String job_Status= "finished";
	private	String job_Method = "full,incremental,resync";

	//For storing the retention,cloud_direct_vloume information 

	HashMap<String,String >retention=new HashMap<String,String>();
	HashMap<String,Object>cloud_direct_volume=new HashMap<String,Object>();

	HashMap<String,Object>cloud_dedupe_volume=new HashMap<String,Object>();
	
	
	ArrayList<String> servers=new ArrayList<String>();
	//Store the response for the  jobs_data
	//HashMap For direct Organization
	ArrayList<HashMap<String,Object>> allLogs=new ArrayList<HashMap<String,Object>>();
	HashMap<String,Object> Log_data=new HashMap<String,Object>();
	HashMap<String,Object> job_data=new HashMap<String,Object>();

	//HashMap for msp_Organization

	ArrayList<HashMap<String,Object>> allLogsMsp=new ArrayList<HashMap<String,Object>>();

	//HashMap for sub_organization

	ArrayList<HashMap<String,Object>> allLogssubOrg=new ArrayList<HashMap<String,Object>>();

	// CSR Logs
	ArrayList<HashMap<String,Object>> allLogsCsr=new ArrayList<HashMap<String,Object>>();

	private ArrayList<HashMap<String, Object>> allLogsMsp_suborg=new ArrayList<HashMap<String,Object>>();

	//csrAllLogs
	ArrayList<HashMap<String,Object>> allLogsUnderCsr=new ArrayList<HashMap<String,Object>>();

	//List of the source_id
	ArrayList<String>sources=new ArrayList<String>();
	private String direct_validToken=null;
	private String msp_user_validToken=null;

	public String JobSeverity= "success,warning,error,critical,information";

	//Sorting based on the create_ts 
	long current = ZonedDateTime.now().toInstant().toEpochMilli();
	long yesterday = ZonedDateTime.now().minusDays(1).toInstant().toEpochMilli();
	long tomorrow = ZonedDateTime.now().plusDays(1).toInstant().toEpochMilli();

	private String account_user_validToken, organization_email_msp, csr_Token, sourceName;
	private String direct_server_id,  direct_server_id_1, msp_server_id, msp_server_id_1, msp_sub_server_id;
	private ArrayList<String> sourcesNames=new ArrayList<String>();
	private String direct_cloud_account_id;
	private String msp_cloud_account_id;
	
	private String prefix_msp_account_admin = "spog_rakesh_msp_account";
	private String msp_account_admin_email = prefix_msp_account_admin+postfix_email;
	private String msp_account_admin_first_name = prefix_msp_account_admin+"_first_name";
	private String msp_account_admin_last_name = prefix_msp_account_admin+"_last_name";
	private String msp_account_admin_validToken;
	String msp_account_admin_id;

	private String  org_model_prefix=this.getClass().getSimpleName();
	
	@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "csrAdminUserName", "csrAdminPassword","csrReadOnlyAdminName","runningMachine", "buildVersion"})
	public void beforeClass(String baseURI, String port, String logFolder, String adminUserName, String adminPassword ,String csrReadOnlyUser,String runningMachine, String buildVersion) throws UnknownHostException {

		spogServer = new SPOGServer(baseURI, port);
		gatewayServer =new GatewayServer(baseURI,port);
		log4SPOGServer = new Log4SPOGServer(baseURI,port);
		spogDestinationServer=new SPOGDestinationServer(baseURI,port);
		userSpogServer = new UserSpogServer(baseURI, port);
		rep = ExtentManager.getInstance("GetLogsBySourceID",logFolder);
		this.csrAdminUserName = adminUserName;
		this.csrAdminPassword = adminPassword;
		this.csr_readonly_email = csrReadOnlyUser;
		//this is for update portal
		this.BQName = this.getClass().getSimpleName();
		String author = "Kanamarlapudi, Chandra Kanth";
		this.runningMachine=runningMachine;
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
				bqdb1.updateTable(BQName, runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0", String.valueOf(Nooftest), count1.getcreationtime(), "InProgress",author+" and Rest server is "+baseURI.split("//")[1]);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}  

		test = rep.startTest("beforeClass");
		
		test.log(LogStatus.INFO, "csr_readonly user login");
		spogServer.userLogin(csr_readonly_email, common_password);
		csr_readonly_token = spogServer.getJWTToken();
		csr_readonly_user_id = spogServer.GetLoggedinUser_UserID();
		
		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
		csr_Token=spogServer.getJWTToken();

		//create a direct org
		test.log(LogStatus.INFO,"create  a direct organization");
		String organization_email_direct_1=RandomStringUtils.randomAlphanumeric(8)+"1_spog_direct_Rakesh@arcserve.com";
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		direct_org_id = spogServer.CreateOrganizationWithCheck(prefix+direct_org_name +org_model_prefix, SpogConstants.DIRECT_ORG, organization_email_direct_1, common_password, spogServer.ReturnRandom("Rakesh_first_direct"),spogServer.ReturnRandom("Rakesh_last_direct"), test);
		final_direct_user_name_email = prefix + direct_user_name_email;
		direct_user_id = spogServer.createUserAndCheck(final_direct_user_name_email, common_password, prefix + direct_user_first_name, prefix + direct_user_last_name, SpogConstants.DIRECT_ADMIN, direct_org_id, test);
		spogServer.userLogin(final_direct_user_name_email, common_password);

		test.log(LogStatus.INFO,"Getting the JWTToken for the Logged in user");
		direct_validToken = spogServer.getJWTToken();
		String siteName= spogServer.getRandomSiteName("TestCreate");
		test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
		String sitetype=siteType.gateway.toString();
		test.log(LogStatus.INFO,"The siteType :"+sitetype);
		test.log(LogStatus.INFO,"Creating a site For Logged in user");
		direct_site_id = gatewayServer.createsite_register_login(direct_org_id, direct_validToken, direct_user_id, "rakesh", "1.0.0", spogServer, test);
		direct_site_token=gatewayServer.getJWTToken();
		test.log(LogStatus.INFO, "The site token is "+direct_site_token);
		
		sourceName = spogServer.ReturnRandom("rakesh_source");	

		servers.add(sourceName);
		test.log(LogStatus.INFO,"create source");
		direct_server_id = spogServer.createSourceWithCheck(sourceName, SourceType.machine, SourceProduct.udp,direct_org_id, direct_site_id, ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(), 	"sql;exchange",  "rakesh_vm2",  null, "rakesh_agent1", "windows 2012", "64" ,"1.0.0","2.0", "http://upgrade",  test);
		//direct_cloud_account_id=createCloudAccount( UUID.randomUUID().toString(),"cloudAccountSecret","CloudAccountData", "cloud_direct",direct_org_id , "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_","91a9b48e-6ac6-4c47-8202-614b5cdcfe0c");

		//.....................create msp org,user,site..........................................................................
		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
		prefix = RandomStringUtils.randomAlphanumeric(8);
		test.log(LogStatus.INFO,"create a msp org");

		test.log(LogStatus.INFO,"create  a  msp organization");
		organization_email_msp=RandomStringUtils.randomAlphanumeric(8)+"2_spog_msp_Rakesh@arcserve.com";
		msp_org_id = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name+org_model_prefix , SpogConstants.MSP_ORG, organization_email_msp, common_password,spogServer.ReturnRandom("Rakesh_first_msp"),spogServer.ReturnRandom("Rakesh_last_msp"), test);
		final_msp_user_name_email = prefix + msp_user_name_email;

		test.log(LogStatus.INFO,"create a admin under msp org");
		msp_user_id = spogServer.createUserAndCheck(final_msp_user_name_email, common_password, prefix + msp_user_first_name, prefix + msp_user_last_name, SpogConstants.MSP_ADMIN, msp_org_id, test);
		spogServer.userLogin(final_msp_user_name_email, common_password);

		test.log(LogStatus.INFO,"Getting the JWTToken for the msp user");
		msp_user_validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The token is :"+ msp_user_validToken );

		siteName= spogServer.getRandomSiteName("TestCreate");
		test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
		sitetype=siteType.gateway.toString();
		test.log(LogStatus.INFO,"The siteType :"+sitetype);


		/*test.log(LogStatus.INFO,"Creating a site For msp org");
		msp_site_id = gatewayServer.createsite_register_login(msp_org_id, msp_user_validToken, msp_user_id, "rakesh", "1.0.0", spogServer, test);
		msp_site_token=gatewayServer.getJWTToken();
		test.log(LogStatus.INFO, "The site token is "+ msp_site_token);*/
		
		/*sourceName = spogServer.ReturnRandom("rakesh_source");
		servers.add(sourceName);
		test.log(LogStatus.INFO,"create source");
		msp_server_id = spogServer.createSourceWithCheck(sourceName, SourceType.machine, SourceProduct.udp, msp_org_id,msp_site_id, ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(), 	"sql;exchange",  "rakesh_vm2",  UUID.randomUUID().toString(), "rakesh_agent1", "windows 2012", "64" ,"1.0.0","2.0", "http://upgrade",  test);*/

		
		//create a mspOrganization cloud_account_id
		//msp_cloud_account_id=createCloudAccount( UUID.randomUUID().toString(),"cloudAccountSecret","CloudAccountData", "cloud_direct",msp_org_id, "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_","99a9b48e-6ac6-4c47-8202-614b5cdcfe0c");


		//create account, account user and site
		test.log(LogStatus.INFO,"Creating a account For msp org");
		account_id = spogServer.createAccountWithCheck(msp_org_id, prefix + "SPOG_QA_RAKESH_BQ_SUB_ORG_A", "", test);
		prefix = RandomStringUtils.randomAlphanumeric(8);

		test.log(LogStatus.INFO,"Creating a account user For account org");
		account_user_email = prefix + msp_user_name_email;
		account_user_id = spogServer.createUserAndCheck(account_user_email, common_password, prefix + msp_user_first_name, prefix + msp_user_last_name, SpogConstants.DIRECT_ADMIN, account_id, test);
		spogServer.userLogin(account_user_email, common_password);


		test.log(LogStatus.INFO,"Getting the JWTToken for the account user");
		account_user_validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The token is :"+ account_user_validToken );

		siteName= spogServer.getRandomSiteName("TestCreate");
		test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
		sitetype=siteType.gateway.toString();
		test.log(LogStatus.INFO,"The siteType :"+sitetype);

		test.log(LogStatus.INFO,"Creating a site For account org");
		account_site_id = gatewayServer.createsite_register_login(account_id, account_user_validToken, account_user_id, "rakesh", "1.0.0", spogServer, test);
		account_site_token=gatewayServer.getJWTToken();
		test.log(LogStatus.INFO, "The site token is "+ account_site_token);
		
		sourceName = spogServer.ReturnRandom("rakesh_source");	 
		servers.add(sourceName);
		test.log(LogStatus.INFO,"create source");
		msp_sub_server_id = spogServer.createSourceWithCheck(sourceName, SourceType.machine, SourceProduct.udp,account_id,account_site_id, ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(), 	"sql;exchange",  "rakesh_vm2",  null, "rakesh_agent1", "windows 2012", "64" ,"1.0.0","2.0", "http://upgrade",  test);


		//Composing a HashMap for the destinations Retentions of the cloud direct volumes 
		HashMap<String,Object> Retention=new HashMap<String,Object>();
		HashMap<String,String>retention=new HashMap<String,String>();	
		retention=spogDestinationServer.composeRetention("0","0","7","0","0","0");
		Retention.put("7D",retention);
		retention=spogDestinationServer.composeRetention("0","0","14","0","0","0");
		Retention.put("14D",retention);
		retention=spogDestinationServer.composeRetention("0","0","31","0","0","0");
		Retention.put("1M",retention);
		retention=spogDestinationServer.composeRetention("0","0","31","0","2","0");
		Retention.put("2M",retention);
		retention=spogDestinationServer.composeRetention("0","0","31","0","3","0");
		Retention.put("3M",retention);
		retention=spogDestinationServer.composeRetention("0","0","31","0","6","0");
		Retention.put("6M",retention);
		retention=spogDestinationServer.composeRetention("0","0","31","0","12","0");
		Retention.put("1Y",retention);
		retention=spogDestinationServer.composeRetention("0","0","31","0","12","2");
		Retention.put("2Y",retention);
		retention=spogDestinationServer.composeRetention("0","0","31","0","12","3");
		Retention.put("3Y",retention);
		retention=spogDestinationServer.composeRetention("0","0","31","0","12","7");
		Retention.put("7Y",retention);
		retention=spogDestinationServer.composeRetention("0","0","31","0","12","10");
		Retention.put("10Y",retention);
		retention=spogDestinationServer.composeRetention("0","0","31","0","12","-1");
		Retention.put("forever",retention);
		retention=spogDestinationServer.composeRetention("0","42","7","0","0","0");
		Retention.put("7Dhf",retention);
		retention=spogDestinationServer.composeRetention("0","42","14","0","0","0");
		Retention.put("14Dhf",retention);
		retention=spogDestinationServer.composeRetention("0","42","31","0","0","0");
		Retention.put("1Mhf",retention);
		spogDestinationServer.setRetention(Retention);
		
		//Create msp_account_admin
		test.log(LogStatus.INFO, "Create a msp account admin under msp org");
		msp_account_admin_email = prefix+msp_account_admin_email;
		spogServer.setToken(msp_user_validToken);
		msp_account_admin_id = spogServer.createUserAndCheck(msp_account_admin_email, common_password, msp_account_admin_first_name, msp_account_admin_last_name, "msp_account_admin", msp_org_id, test);
		spogServer.userLogin(msp_account_admin_email, common_password);
		this.msp_account_admin_validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The token is :"+ msp_account_admin_validToken );
		userSpogServer.assignMspAccountAdmins(msp_org_id, account_id, new String[] {msp_account_admin_id}, msp_user_validToken);
	}
	
	@DataProvider(name = "log_data")
	public final Object[][] getLogDataParams() {
		return new Object[][] {
				{final_direct_user_name_email,direct_org_id,direct_site_id, direct_server_id, direct_site_token, new String[] {LogSourceType.spog.toString(),LogSourceType.udp.toString(),LogSourceType.cloud_direct.toString()},
				new String[] { LogSeverityType.information.name(),LogSeverityType.warning.name(),LogSeverityType.error.name()},test_log_Message_1,
				new String[] { "node", "10.57.63.2"}, true,System.currentTimeMillis(),"arcserve@spog","direct",servers.get(0),direct_cloud_account_id},
			    /*{ final_msp_user_name_email, msp_org_id,msp_site_id,  msp_server_id, msp_site_token, new String[] {LogSourceType.spog.toString(),LogSourceType.udp.toString(),LogSourceType.cloud_direct.toString()}, new String[] { LogSeverityType.information.toString(),LogSeverityType.warning.toString(),LogSeverityType.error.toString()},
				test_log_Message_2, new String[] { "node", "10.57.63.2","Agent","network" }, true , System.currentTimeMillis(),"Hello@spog","msp",servers.get(1),msp_cloud_account_id},				
				*/{ account_user_email, account_id, account_site_id,  msp_sub_server_id,account_site_token,new String[] {LogSourceType.spog.toString(),LogSourceType.udp.toString(),LogSourceType.cloud_direct.toString()}, new String[] { LogSeverityType.information.toString(),LogSeverityType.warning.toString(),LogSeverityType.error.toString()},
			    test_log_Message_1, new String[] { "node", "10.57.63.2"}, true ,  System.currentTimeMillis(),"Spog@UdpIntegration","suborg",servers.get(1),UUID.randomUUID().toString()}
			

		};
	}
	

	//This method is used to create a cloud_account
		public String createCloudAccount(String cloudAccountKey,String cloudAccountSecret, String cloudAccountName, String cloudAccountType, String organization_id,
				String orderID,String fulfillmentID,String datacenter_id){
			String cloud_account_id="";
			String prefix=RandomStringUtils.randomAlphanumeric(8);
			if (!(null == orderID) && !("" == orderID) && !(orderID.equalsIgnoreCase("none"))) {
				orderID = orderID + prefix;
			}

			if (!(null == fulfillmentID) && !("" == fulfillmentID) && !(fulfillmentID.equalsIgnoreCase("none"))) {
				fulfillmentID = fulfillmentID + prefix;
			}
			if(cloudAccountKey!=""&&cloudAccountSecret!=""){
				//cloudAccountKey=RandomStringUtils.randomAlphanumeric(8)+cloudAccountKey;
				cloudAccountSecret=RandomStringUtils.randomAlphanumeric(8)+cloudAccountSecret;
			}
			cloudAccountName=RandomStringUtils.randomAlphanumeric(8)+cloudAccountName;

			if (organization_id == null || organization_id == "" || organization_id.equalsIgnoreCase("none")) {
				cloud_account_id=spogServer.createCloudAccountWithCheck(cloudAccountKey, cloudAccountSecret, cloudAccountName,
						cloudAccountType, organization_id, orderID, fulfillmentID,datacenter_id, test);
			} else {
				cloud_account_id=spogServer.createCloudAccountWithCheck(cloudAccountKey, cloudAccountSecret, cloudAccountName,
						cloudAccountType, organization_id, orderID, fulfillmentID,datacenter_id, test);
			}
			return cloud_account_id;
		}
	     public  String createDestination(String Token,String organization_id,
			String DestinationType,String destination_status,String site_id,String destination_name,String cloud_direct_volume_name,String retention_id,String retention_name,String age_hours_max ,String age_four_hours_max,
			String age_days_max , String 	age_weeks_max, String age_months_max,String age_years_max , Double primary_usage,Double  snapshot_usage,Double total_usage,String volume_type,String hostname,String datacenter_id,String dedupe_savings,String cloud_account_id){

		//creating  a destination 
		test.log(LogStatus.INFO,"creating a destination of type :"+DestinationType);
		retention= spogDestinationServer.composeRetention(age_hours_max,age_four_hours_max,age_days_max ,age_weeks_max,age_months_max,age_years_max);
		cloud_direct_volume=spogDestinationServer.composeCloudDirectInfo(cloud_account_id,cloud_direct_volume_name,retention_id,retention_name,primary_usage,snapshot_usage,total_usage,volume_type,hostname,retention);	
		test.log(LogStatus.INFO,"Creating a destination of type cloud_direct_volume");
		Response response=spogDestinationServer.createDestination(UUID.randomUUID().toString(),Token,cloud_account_id,organization_id,site_id,datacenter_id, dedupe_savings,DestinationType,destination_status,destination_name,cloud_direct_volume,test);
		String destination_id=response.then().extract().path("data.destination_id");		
		return destination_id;

	}

	@Test(dataProvider = "log_data")	 
	public void PostLogsBySourceID(String email, String org_id,  String site_id, String server_id,String site_token, 
		String logSourceType[], String LogSeverity[], String log_message_id,  String[] log_message_data,boolean isLogExist, long log_generate_time,
		String help_message_id,String orgType,String generatedFrom,String cloud_account_id){		  
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		test.assignAuthor("Kanamarlapudi, Chandra Kanth");  
		spogServer.userLogin(email, common_password);	  
		String adminToken=spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The value of the Token is:"+adminToken);	
		String rps_id = UUID.randomUUID().toString();String policy_id= UUID.randomUUID().toString();String sourceName = spogServer.ReturnRandom("rakesh_source");	  	
		test.log(LogStatus.INFO,"create source");
		String source_id = spogServer.createSourceWithCheck(sourceName, SourceType.machine, SourceProduct.udp, org_id, site_id, ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(), "",  test);
		sources.add(source_id);
		test.log(LogStatus.INFO,"create job");
		//String destination_id=createDestination(adminToken,org_id,"cloud_direct_volume","creating",site_id,"dest451","dest5123","6M","6 Months","2","21","31","14","11","5",26.0,24.0,50.0,volume_type.normal.toString(),"KRISHLAP_1","91a9b48e-6ac6-4c47-8202-614b5cdcfe0c","10",cloud_account_id);		 
		String destination_id=UUID.randomUUID().toString();
		String job_type[]=job_Type.split(",");String job_method[]=job_Method.split(",");String job_severity[]=JobSeverity.split(",");
		String message="";
		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream("src\\LogMessage.properties"));
			message=prop.getProperty(log_message_id);
		}catch (IOException e) {
			e.printStackTrace();
		}
		for (int i=0,j=0;i<job_type.length;i++,j++){
			long start_time_ts = System.currentTimeMillis();  long endTimeTS = System.currentTimeMillis()+ 10000;
			log_generate_time= System.currentTimeMillis();
			//For jobs of type backup/full/incremental/Resync
			if(job_type[i].equals("backup")){
				for(int z=0;z<job_method.length;z++) {
					String job_id = gatewayServer.postJobWithCheck(start_time_ts,endTimeTS, org_id, server_id, source_id, rps_id,destination_id, policy_id,
							job_type[i], job_method[z], job_Status, site_token, test);	       		 
					//gatewayServer.postJobDataWithCheck(job_id,"1",job_severity[j],"30", "30", "10","10", "10","2",site_token, test);
					//GetThe jobs response
					Response response=spogServer.getJobsById(adminToken, job_id, test);
					job_data=spogServer.composeJobData(response,test);	
					for(int k=0;k<LogSeverity.length;k++){
						log_generate_time= log_generate_time+1;
						String log_id =null;		  
						if(isLogExist){
							test.log(LogStatus.INFO,"create log for the job");		  	
							 String log_data="";
							for(int p=0;p<log_message_data.length;p++) {
								log_data+=log_message_data[p]+",";
							}		
							log_id=gatewayServer.createLogwithCheck(log_generate_time,  UUID.randomUUID().toString(),job_id,org_id, org_id, source_id,  LogSeverity[k], logSourceType[k],log_message_id, log_data.substring(0,log_data.length()-1),help_message_id, site_token,test);
						}	
						test.log(LogStatus.INFO,"The value of the message is:"+log_id);
						//Storing the response for the getLogs			
						if(log_message_id.equals(test_log_Message_1)){
							message=message.replace("{0}",log_message_data[0]).replace("{1}",log_message_data[1]);
						}else if (log_message_id.equals(test_log_Message_2)){
							message=message.replace("{0}",log_message_data[0]).replace("{1}",log_message_data[1]).replace("{2}",log_message_data[2]).replace("{3}",log_message_data[3]);
						}	   									
						//Log_data=spogServer.ComposeLogs(log_generate_time,log_id,job_data,LogSeverity[k],logSourceType[k],site_id,org_id,message,test);				
						Log_data=spogServer.ComposeLogs(log_generate_time,log_id,job_data,LogSeverity[k],logSourceType[k],site_id,org_id,message,help_message_id,generatedFrom,test);
						if(orgType.equalsIgnoreCase("direct")){ 
							allLogs.add(Log_data);
						}else if(orgType.equalsIgnoreCase("msp")){			
							allLogsMsp.add(Log_data);
						}else if(orgType.equalsIgnoreCase("suborg")){
							allLogssubOrg.add(Log_data);
						}
					}
				} 

			}		
			//For another Kinds of jobs
			else{
				String job_id = gatewayServer.postJobWithCheck(start_time_ts,endTimeTS, org_id, server_id, source_id, rps_id,destination_id, policy_id,
						job_type[i], job_method[j], job_Status, site_token, test);	       		 
				//gatewayServer.postJobDataWithCheck(job_id,"1",job_severity[j],"30", "30", "10","10", "10","2",site_token, test);
				//GetThe jobs response
				Response response=spogServer.getJobsById(adminToken, job_id, test);
				job_data=spogServer.composeJobData(response,test);	
				for(int k=0;k<LogSeverity.length;k++){
					log_generate_time=log_generate_time+1;
					String log_id =null;		  
					if(isLogExist){
						test.log(LogStatus.INFO,"create log for the job");	
						 String log_data="";
						for(int p=0;p<log_message_data.length;p++) {
							log_data+=log_message_data[p]+",";
						}		
						log_id=gatewayServer.createLogwithCheck(log_generate_time, UUID.randomUUID().toString(), job_id,org_id, org_id, source_id,  LogSeverity[k], logSourceType[k],log_message_id, log_data.substring(0,log_data.length()-1),help_message_id, site_token,test);
					}	
					test.log(LogStatus.INFO,"The value of the message is:"+log_id);	
					//Storing the response for the getLogs			
					if(log_message_id.equals(test_log_Message_1)){
						message=message.replace("{0}",log_message_data[0]).replace("{1}",log_message_data[1]);
					}else if (log_message_id.equals(test_log_Message_2)){
						message=message.replace("{0}",log_message_data[0]).replace("{1}",log_message_data[1]).replace("{2}",log_message_data[2]).replace("{3}",log_message_data[3]);
					}	
					Log_data=spogServer.ComposeLogs(log_generate_time,log_id,job_data,LogSeverity[k],logSourceType[k],site_id,org_id,message,help_message_id,generatedFrom,test);		
					if(orgType.equalsIgnoreCase("direct")){ 
						allLogs.add(Log_data);
					}else if(orgType.equalsIgnoreCase("msp")){			
						allLogsMsp.add(Log_data);
					}else if(orgType.equalsIgnoreCase("suborg")){
						allLogssubOrg.add(Log_data);
					}
				}
				//for the job severity type
				if(j==2){
					j=0; 
				}

			}
		}
	}

	/**
	 * This API queries logs with source id and returns log list.
	 * Filter:log_severity_type, log_ts, job_type.<br>
	 * Sort:sort=log_severity_type, log_ts, job_type.<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;To sort in descending order, the sort field should start with hyphen.<br>
	 * Pagination: page=:page&page_size=:page_size<br>
	 * Example:?log_severity_type=:log_severity_type&log_ts=>X<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;&log_ts=<Y OR log_ts=[X,Y].<br>
	 * log_severity_type:information,warning,error.<br>
	 * job_type:backup,restore,copy,merge,conversion,vm_backup,vm_recovery,<br>
	 * &nbsp;&nbsp;vm_catalog_fs,mount_recovery_point,office365_backup,cifs_backup,sharepoint_backup,<br>
	 * &nbsp;&nbsp;linux_assure_recovery,vm_merge,catalog_fs,catalog_app,catalog_grt,file_copy_backup,file_copy_purge,<br>
	 * &nbsp;&nbsp;file_copy_restore,file_copy_catalog_sync,file_copy_source_delete,file_copy_delete,catalog_fs_ondemand,<br>
	 * &nbsp;&nbsp;vm_catalog_fs_ondemand,rps_replicate,rps_replicate_in_bound,rps_merge,rps_conversion,bmr,<br>
	 * &nbsp;&nbsp;rps_data_seeding,rps_data_seeding_in,vm_recovery_hyperv,rps_purge_datastore,start_instant_vm,<br>
	 * &nbsp;&nbsp;stop_instant_vm,assure_recovery,start_instant_vhd,stop_instant_vhd,archive_to_tape,linux_instant_vm.<br>
	 * @param sourceId
	 * @responseMessage 200 success

	 */

	//It is realated_to only pagination,sorting 	
	//Get all the Logs information by applying pagination sorting and Filtering 
	@DataProvider(name = "getLogsInfo")
	public final Object[][] getLogsInfo() {
		return new Object[][]   { 
				//csr readonly token
				/**/
				
				{direct_validToken,"direct",1,100,"","log_ts;asc",sources.get(0),allLogs},
				{csr_readonly_token,"direct",1,20,"","log_ts;desc",sources.get(0),allLogs},//csr_readonly
				{direct_validToken,"direct",1,101,"","log_ts;asc",sources.get(0),allLogs}, 
				{direct_validToken,"direct",1,21,"","log_ts;asc",sources.get(0),allLogs},
				{direct_validToken,"direct",1,19,"","log_ts;desc",sources.get(0),allLogs},
				{direct_validToken,"direct",1,99,"","log_ts;asc",sources.get(0),allLogs},				                   
				{msp_user_validToken,"msp",1,20,"","log_ts;desc",sources.get(1),allLogssubOrg},
				{msp_user_validToken,"msp",1,99,"","log_ts;asc",sources.get(1),allLogssubOrg},
				{msp_user_validToken,"msp",1,19,"job_type;in;backup_full|backup_incremental","log_ts;asc",sources.get(1),allLogssubOrg},
				{msp_user_validToken,"msp",1,21,"job_type;in;backup_full|backup_incremental","log_ts;asc",sources.get(1),allLogssubOrg},
				{msp_user_validToken,"msp",1,21,"log_severity_type;in;information|warning|error","log_ts;asc",sources.get(1),allLogssubOrg},
				{msp_user_validToken,"msp",1,21,"log_ts;>;"+yesterday,"log_ts;desc",sources.get(1),allLogssubOrg},
				{msp_user_validToken,"msp",1,21,"log_ts;<;"+tomorrow,"log_ts;asc",sources.get(1),allLogssubOrg},
				{msp_user_validToken,"msp",1,21,"log_ts;<;"+tomorrow,"log_ts;desc",sources.get(1),allLogssubOrg},
				{msp_user_validToken,"msp",1,21,"log_ts;>;"+yesterday+","+"log_ts;<;"+tomorrow,"log_ts;desc",sources.get(1),allLogssubOrg},
				{msp_user_validToken,"msp",0,0,"job_type;in;backup_full|backup_incremental"+","+"log_severity_type;in;information|warning","log_ts;asc",sources.get(1),allLogssubOrg},  
				{msp_user_validToken,"suborg",1,20,"","log_ts;asc",sources.get(1),allLogssubOrg},
				{account_user_validToken,"suborg",1,99,"","log_ts;asc",sources.get(1),allLogssubOrg},
				{account_user_validToken,"suborg",1,19,"job_type;=;backup_full","log_ts;desc",sources.get(1),allLogssubOrg},
				{account_user_validToken,"suborg",1,19,"job_type;in;backup_full|backup_incremental","log_ts;asc",sources.get(1),allLogssubOrg},
				{account_user_validToken,"suborg",1,21,"job_type;in;backup_full|backup_incremental","log_ts;desc",sources.get(1),allLogssubOrg},
				{account_user_validToken,"suborg",1,21,"log_severity_type;in;information|warning|error","log_ts;asc",sources.get(1),allLogssubOrg},
				{account_user_validToken,"suborg",1,21,"log_ts;>;"+yesterday,"log_ts;asc",sources.get(1),allLogssubOrg},
				{account_user_validToken,"suborg",1,21,"log_ts;<;"+tomorrow,"log_ts;desc",sources.get(1),allLogssubOrg},
				{account_user_validToken,"suborg",1,21,"log_ts;<;"+tomorrow,"log_ts;asc",sources.get(1),allLogssubOrg},
				
				{msp_account_admin_validToken,"suborg",1,99,"","log_ts;asc",sources.get(1),allLogssubOrg},
				{msp_account_admin_validToken,"suborg",1,19,"job_type;=;backup_full","log_ts;desc",sources.get(1),allLogssubOrg},
				{msp_account_admin_validToken,"suborg",1,19,"job_type;in;backup_full|backup_incremental","log_ts;asc",sources.get(1),allLogssubOrg},
				
				{direct_validToken,"direct",1,19,"job_type;=;backup_full","log_ts;asc",sources.get(0),allLogs},
				{direct_validToken,"direct",1,19,"job_type;in;backup_full|backup_incremental","log_ts;asc",sources.get(0),allLogs},
				{direct_validToken,"direct",1,21,"job_type;in;backup_full|backup_incremental","log_ts;asc",sources.get(0),allLogs},
				{direct_validToken,"direct",1,21,"log_severity_type;in;information|warning|error","log_ts;desc",sources.get(0),allLogs},
				{direct_validToken,"direct",1,21,"log_ts;>;"+yesterday,"log_ts;asc",sources.get(0),allLogs},
				{direct_validToken,"direct",1,21,"log_ts;>;"+yesterday+","+"log_ts;<;"+tomorrow,"log_ts;asc",sources.get(0),allLogs},			
				{direct_validToken,"direct",1,21,"log_ts;<;"+tomorrow,"log_ts;asc",sources.get(0),allLogs},	
			    {direct_validToken,"direct",1,21,"","log_severity_type;asc,log_ts;asc",sources.get(0),allLogs}	,		
				{direct_validToken,"direct",1,21,"","job_type;asc,log_ts;asc",sources.get(0),allLogs},
				{direct_validToken,"direct",1,21,"","job_type;desc,log_ts;desc",sources.get(0),allLogs},
				
				{csr_readonly_token,"direct",1,19,"job_type;=;backup_full","log_ts;asc",sources.get(0),allLogs},
				{csr_readonly_token,"direct",1,19,"job_type;in;backup_full|backup_incremental","log_ts;asc",sources.get(0),allLogs},
				{csr_readonly_token,"direct",1,21,"job_type;in;backup_full|backup_incremental","log_ts;asc",sources.get(0),allLogs},
				{csr_readonly_token,"direct",1,21,"log_severity_type;in;information|warning|error","log_ts;desc",sources.get(0),allLogs},
			    {csr_readonly_token,"direct",1,21,"","log_severity_type;asc,log_ts;asc",sources.get(0),allLogs}	,		
				{csr_readonly_token,"direct",1,21,"","job_type;asc,log_ts;asc",sources.get(0),allLogs},
				{csr_readonly_token,"direct",1,21,"","job_type;desc,log_ts;desc",sources.get(0),allLogs},
				{csr_readonly_token	,"suborg",1,100,"","log_ts;asc",sources.get(1),allLogssubOrg},
				{csr_readonly_token,"suborg",1,19,"job_type;=;backup_full","log_ts;desc",sources.get(1),allLogssubOrg},
				{csr_readonly_token,"suborg",1,19,"job_type;in;backup_full|backup_incremental","log_ts;asc",sources.get(1),allLogssubOrg},
				{csr_readonly_token,"suborg",1,21,"job_type;in;backup_full|backup_incremental","log_ts;desc",sources.get(1),allLogssubOrg},
				{csr_readonly_token,"suborg",1,21,"log_severity_type;in;information|warning|error","log_ts;asc",sources.get(1),allLogssubOrg},
				{csr_readonly_token,"suborg",1,21,"log_ts;>;"+yesterday,"log_ts;asc",sources.get(1),allLogssubOrg},
				{csr_readonly_token,"suborg",1,21,"log_ts;<;"+tomorrow,"log_ts;desc",sources.get(1),allLogssubOrg},
				{csr_readonly_token,"suborg",1,21,"log_ts;<;"+tomorrow,"log_ts;asc",sources.get(1),allLogssubOrg},
		};			
	}
	@Test(dependsOnMethods="PostLogsBySourceID",dataProvider="getLogsInfo")
	public void GetLogsForSourceId(String admin_Token,String OrgType,int page_number,int page_size,String filterStr,String SortStr,String source_id,ArrayList<HashMap<String,Object>> Logsinfo){
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()); 		
		test.assignAuthor("Kanamarlapudi, Chandra Kanth"); 	
		test.log(LogStatus.INFO,"The filtername is :"+filterStr);
		String[] filterArray=null;
		String[] filters=null;
		String filtersdata=null;
		String filterName = null,filterOperator = null, filterValue = null;
		ArrayList<HashMap<String,Object>> Logs_info = new ArrayList<>();
		//It is related to the Filter on the single value 
		if(filterStr!=""&&filterStr!=null&&!filterStr.contains(",")){
			filterArray = filterStr.split(";");
			filterName = filterArray[0];filterOperator = filterArray[1];filterValue = filterArray[2];
			Logs_info=spogServer.getLogsInfo(Logsinfo,filterName,filterOperator,filterValue);
			Logsinfo=Logs_info;
			if(SortStr!=""&&SortStr.contains("log_ts")&&!(filterStr.contains("log_ts"))){
				Logsinfo=spogServer.getLogsTimeSortInfo(SortStr, Logsinfo, test);	
			}
		}//It is related to the filtering on the multiple values 
		else if(filterStr!=""&& filterStr!=null&&filterStr.contains(",")) {
			filters = filterStr.split(",");	
			for(int i=0;i<filters.length;i++){
				filtersdata=filters[i];
				filterArray = filtersdata.split(";");
				filterName = filterArray[0];filterOperator = filterArray[1];filterValue = filterArray[2];		
				Logs_info=spogServer.getLogsInfo(Logsinfo,filterName,filterOperator,filterValue);
				Logsinfo=Logs_info;
				if(SortStr!=""&&SortStr.contains("log_ts")&&!(filterStr.contains("log_ts"))){
					Logsinfo=spogServer.getLogsTimeSortInfo(SortStr, Logsinfo, test);	
				}
			}		
		}			
		//For the sorting based on log_severity_type and job_type and default is (Log_ts)
		if(SortStr.contains("log_severity_type")||SortStr.contains("job_type")) {
			Logsinfo=spogServer.getLogsSortInfo(SortStr, Logsinfo, test);
		}

		test.log(LogStatus.INFO,"Preparing the URI for get logs by sourceId");
		String additionalURL=spogServer.PrepareURL(filterStr,SortStr,page_number, page_size, test);	
		Response response= log4SPOGServer.getSourceIdLogs(source_id,admin_Token,additionalURL,test);
		if(OrgType.equalsIgnoreCase("direct")){	   		
			log4SPOGServer.checkGetSourcesIdLogs(response,SpogConstants.SUCCESS_GET_PUT_DELETE,Logsinfo,page_number,page_size,filterStr,SortStr,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);	
		}else if(OrgType.equalsIgnoreCase("msp")){		
			log4SPOGServer.checkGetSourcesIdLogs(response,SpogConstants.SUCCESS_GET_PUT_DELETE,Logsinfo,page_number,page_size,filterStr,SortStr,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);	
		}else if(OrgType.equalsIgnoreCase("subOrg")) {
			log4SPOGServer.checkGetSourcesIdLogs(response,SpogConstants.SUCCESS_GET_PUT_DELETE,Logsinfo,page_number,page_size,filterStr,SortStr,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);	
		}
	}	
	/**
	 * This API queries get Logs by source_id,  and returns logs list.
	 * Filter:message, source_name, generated_from .<br>
	 * Sort:sort= log_ts<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp;To sort in descending order, the sort field should start with hyphen.<br>
	 * Pagination: page=:page& page_size=:page_size<br>
	 * Example:?generated_from=:generated_from & log_ts=>X<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp; ?message=:message & log_ts>=X<br>
	 * &nbsp;&nbsp;&nbsp;&nbsp; ?source_name=:source_name & log_ts <=X <br>  
	 * log_severity_type:information,warning,error.<br>
	 * job_type:backup,restore,copy,merge,conversion,vm_backup,vm_recovery,<br>
	 * */

	@DataProvider(name="getLogsFilterInfo")
	public final Object[][] getLogsInfoForFilters() {
		return new Object[][]   { {direct_validToken,"direct",1,23,"generated_from;=;"+servers.get(0)+","+"job_type;in;backup_full|backup_incremental",sources.get(0),"log_ts;desc",allLogs},
				{direct_validToken,"direct",2,3,"generated_from;=;"+servers.get(0),sources.get(0),"log_ts;asc",allLogs},  
				{direct_validToken,"direct",2,5,"generated_from;=;"+servers.get(0),sources.get(0),"log_ts;asc",allLogs},  
				{direct_validToken,"direct",1,23,"help_message_id;=;arcserve@spog",sources.get(0),"log_ts;asc",allLogs}, 
				{msp_user_validToken,"msp",1,23,"generated_from;=;"+servers.get(1)+","+"job_type;in;backup_full|backup_incremental",sources.get(1),"log_ts;desc",allLogssubOrg},
				{msp_user_validToken,"msp",3,2,"generated_from;=;"+servers.get(1)+","+"job_type;in;backup_full|backup_incremental",sources.get(1),"log_ts;desc",allLogssubOrg},
				{msp_user_validToken,"msp",1,23,"help_message_id;=;Hello@spog",sources.get(1),"log_ts;asc",allLogssubOrg},  
			    {account_user_validToken,"subOrg",2,4,"generated_from;=;"+servers.get(1)+","+"job_type;in;backup_full|backup_incremental",sources.get(1),"log_ts;asc",allLogssubOrg},
				{account_user_validToken,"subOrg",1,17,"generated_from;=;"+servers.get(1),sources.get(1),"log_ts;asc",allLogssubOrg},  
				{account_user_validToken,"subOrg",2,4,"generated_from;=;"+servers.get(1),sources.get(1),"log_ts;asc",allLogssubOrg},  
				{account_user_validToken,"subOrg",2,3,"help_message_id;=;Spog@UdpIntegration",sources.get(1),"log_ts;asc",allLogssubOrg},
				{msp_account_admin_validToken,"subOrg",1,17,"generated_from;=;"+servers.get(1),sources.get(1),"log_ts;asc",allLogssubOrg},  
				{msp_account_admin_validToken,"subOrg",2,4,"generated_from;=;"+servers.get(1),sources.get(1),"log_ts;asc",allLogssubOrg}, 
		};
	}	
	@Test(dependsOnMethods="PostLogsBySourceID",dataProvider="getLogsFilterInfo",enabled=true)
	public void GetLogsForSourceIdFilters(String admin_Token,String OrgType,int page_number,int page_size,String filterStr,String source_id,String SortStr,ArrayList<HashMap<String,Object>> Logsinfo){
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+OrgType+"_"+filterStr+"_"+page_number+"_"+page_size); 					
		test.assignAuthor("Kanamarlapudi, Chandra Kanth"); 	
		test.log(LogStatus.INFO,"The filtername is :"+filterStr);
		String[] filterArray=null;
		String[] filters=null;
		String filtersdata=null;
		String filterName = null,filterOperator = null, filterValue = null;
		ArrayList<HashMap<String,Object>> Logs_info = new ArrayList<>();
		//It is related to the Filter on the single value 
		if(filterStr!=""&&filterStr!=null&&!filterStr.contains(",")){
			filterArray = filterStr.split(";");
			filterName = filterArray[0];filterOperator = filterArray[1];filterValue = filterArray[2];
			Logs_info=spogServer.getLogsInfo(Logsinfo,filterName,filterOperator,filterValue);
			Logsinfo=Logs_info;
			
			if(SortStr!=""&&SortStr.contains("log_ts")&&!(filterStr.contains("log_ts"))){
				Logsinfo=spogServer.getLogsTimeSortInfo(SortStr, Logsinfo, test);	
			}
		}//It is related to the filtering on the multiple values 
		else if(filterStr!=""&& filterStr!=null&&filterStr.contains(",")) {
			filters = filterStr.split(",");	
			for(int i=0;i<filters.length;i++){
				filtersdata=filters[i];
				filterArray = filtersdata.split(";");
				filterName = filterArray[0];filterOperator = filterArray[1];filterValue = filterArray[2];		
				Logs_info=spogServer.getLogsInfo(Logsinfo,filterName,filterOperator,filterValue);
				Logsinfo=Logs_info;				
				if(SortStr!=""&&SortStr.contains("log_ts")&&!(filterStr.contains("log_ts"))){
					Logsinfo=spogServer.getLogsTimeSortInfo(SortStr, Logsinfo, test);	
				}
			}		
		}			
		//For the sorting based on log_severity_type and job_type and default is (Log_ts)
		if(SortStr.contains("log_severity_type")||SortStr.contains("job_type")) {
			Logsinfo=spogServer.getLogsSortInfo(SortStr, Logsinfo, test);
		}

		test.log(LogStatus.INFO,"Preparing the URI for get logs by sourceId");
		String additionalURL=spogServer.PrepareURL(filterStr,SortStr,page_number, page_size, test);	
		Response response= log4SPOGServer.getSourceIdLogs(source_id,admin_Token,additionalURL,test);
		if(OrgType.equalsIgnoreCase("direct")){	   		
			log4SPOGServer.checkGetSourcesIdLogs(response,SpogConstants.SUCCESS_GET_PUT_DELETE,Logsinfo,page_number,page_size,filterStr,SortStr,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);	
		}else if(OrgType.equalsIgnoreCase("msp")){		
			log4SPOGServer.checkGetSourcesIdLogs(response,SpogConstants.SUCCESS_GET_PUT_DELETE,Logsinfo,page_number,page_size,filterStr,SortStr,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);	
		}else if(OrgType.equalsIgnoreCase("subOrg")) {
			log4SPOGServer.checkGetSourcesIdLogs(response,SpogConstants.SUCCESS_GET_PUT_DELETE,Logsinfo,page_number,page_size,filterStr,SortStr,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);	
		}
	}
	//Invalid cases :GetLogs By using Invalid cases
    /**(source Id as input) 
	 * @responseMessage 401 user not login OR invalid/expire token/missing
	 * @responseMessage 403 insufficient permission in authorization
	 * @responseMessage 404 sourceId id does not exist
	 * */
	 @DataProvider(name = "getLogsInfo3")
	 public final Object[][] getLogsInfoByMissingToken() {
		 return new Object[][]   { {"","direct",1,3,"organization_id;=;direct","create_ts;desc",SpogMessageCode.COMMON_AUTHENTICATION_FAILED,SpogConstants.NOT_LOGGED_IN,sources.get(0),allLogs},	
				 {"","msp",2,3,"organization_id;=;msp","create_ts;asc",SpogMessageCode.COMMON_AUTHENTICATION_FAILED,SpogConstants.NOT_LOGGED_IN,sources.get(1),allLogsMsp},			               
				 {"","csr",1,3,"organization_id;in;msp|direct","create_ts;desc",SpogMessageCode.COMMON_AUTHENTICATION_FAILED,SpogConstants.NOT_LOGGED_IN,sources.get(0),allLogsCsr},	
				 {"","subOrg",2,2,"","create_ts;desc",SpogMessageCode.COMMON_AUTHENTICATION_FAILED,SpogConstants.NOT_LOGGED_IN,sources.get(1),allLogssubOrg},
				 {direct_validToken+"JUNK","direct",1,3,"organization_id;=;direct","create_ts;desc",SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT,SpogConstants.NOT_LOGGED_IN,sources.get(0),allLogs},	
				 {msp_user_validToken+"JUNK","msp",2,3,"organization_id;=;msp","create_ts;asc",SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT,SpogConstants.NOT_LOGGED_IN,sources.get(1),allLogsMsp},			               
				 {csr_Token+"JUNK","csr",1,3,"organization_id;in;msp|direct","create_ts;desc",SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT,SpogConstants.NOT_LOGGED_IN,sources.get(0),allLogsCsr},	
				 {account_user_validToken+"JUNK","subOrg",2,2,"","create_ts;desc",SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT,SpogConstants.NOT_LOGGED_IN,sources.get(1),allLogssubOrg},	           
				 {direct_validToken,"direct",1,3,"organization_id;=;direct","create_ts;desc",SpogMessageCode.RESOURCE_NOT_FOUND,SpogConstants.RESOURCE_NOT_EXIST,UUID.randomUUID().toString(),allLogs},	
				 {msp_user_validToken,"msp",2,3,"organization_id;=;msp","create_ts;asc",SpogMessageCode.RESOURCE_NOT_FOUND,SpogConstants.RESOURCE_NOT_EXIST,UUID.randomUUID().toString(),allLogsMsp},			               
				 {csr_Token,"csr",1,3,"organization_id;in;msp|direct","create_ts;desc",SpogMessageCode.RESOURCE_NOT_FOUND,SpogConstants.RESOURCE_NOT_EXIST,UUID.randomUUID().toString(),allLogsCsr},	
				 {account_user_validToken,"subOrg",2,2,"","create_ts;desc",SpogMessageCode.RESOURCE_NOT_FOUND,SpogConstants.RESOURCE_NOT_EXIST,UUID.randomUUID().toString(),allLogssubOrg},
				 {direct_validToken,"direct",1,3,"organization_id;=;direct","create_ts;desc",SpogMessageCode.RESOURCE_PERMISSION_DENY,SpogConstants.INSUFFICIENT_PERMISSIONS,sources.get(1),allLogs},	
				 {msp_user_validToken,"msp",2,3,"","create_ts;asc",SpogMessageCode.RESOURCE_PERMISSION_DENY,SpogConstants.INSUFFICIENT_PERMISSIONS,sources.get(0),allLogsCsr},	                               
				 {direct_validToken,"subOrg",2,2,"organization_id;=;msp_child","create_ts;desc",SpogMessageCode.RESOURCE_PERMISSION_DENY,SpogConstants.INSUFFICIENT_PERMISSIONS,sources.get(1),allLogsCsr},
				 {account_user_validToken,"msp",2,3,"","create_ts;asc",SpogMessageCode.RESOURCE_PERMISSION_DENY,SpogConstants.INSUFFICIENT_PERMISSIONS,sources.get(0),allLogsCsr},
				 {account_user_validToken,"subOrg",1,3,"organization_id;=;direct","create_ts;desc",SpogMessageCode.RESOURCE_PERMISSION_DENY,SpogConstants.INSUFFICIENT_PERMISSIONS,sources.get(0),allLogsCsr},
				 
				 {msp_account_admin_validToken,"subOrg",1,3,"organization_id;=;direct","create_ts;desc",SpogMessageCode.RESOURCE_PERMISSION_DENY,SpogConstants.INSUFFICIENT_PERMISSIONS,sources.get(0),allLogsCsr},
				 {msp_account_admin_validToken,"subOrg",1,3,"organization_id;=;direct","create_ts;desc",SpogMessageCode.RESOURCE_NOT_FOUND,SpogConstants.RESOURCE_NOT_EXIST,UUID.randomUUID().toString(),allLogsCsr}
		 };
	 }
	 @Test(dependsOnMethods="PostLogsBySourceID",dataProvider="getLogsInfo3",enabled=true)
	 public void GetLogsForInvalidCases(String admin_Token,String OrgType,int page_number,int page_size,String filterStr,String SortStr,SpogMessageCode errorInfo,int status,String source_id,ArrayList<HashMap<String,Object>> Logsinfo){
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+OrgType+"_"+filterStr+"_"+page_number+"_"+page_size); 				
		test.assignAuthor("Kanamarlapudi, Chandra Kanth");	
		 //preparing the URL and validating the Response For 
		 test.log(LogStatus.INFO,"Preparing the URI for get logs by sourceId");
		 String additionalURL=spogServer.PrepareURL(filterStr,SortStr,page_number, page_size, test);	
		 Response response= log4SPOGServer.getSourceIdLogs(source_id,admin_Token,additionalURL,test);
		 if(OrgType.equalsIgnoreCase("direct")){	   		
			 log4SPOGServer.checkGetSourcesIdLogs(response,status,Logsinfo,page_number,page_size,filterStr,SortStr,errorInfo,test);	
		 }else if(OrgType.equalsIgnoreCase("msp")){		
			 log4SPOGServer.checkGetSourcesIdLogs(response,status,Logsinfo,page_number,page_size,filterStr,SortStr,errorInfo,test);	
		 }else if(OrgType.equalsIgnoreCase("subOrg")) {
			 log4SPOGServer.checkGetSourcesIdLogs(response,status,Logsinfo,page_number,page_size,filterStr,SortStr,errorInfo,test);	
		 }else{
			 log4SPOGServer.checkGetSourcesIdLogs(response,status,Logsinfo,page_number,page_size,filterStr,SortStr,errorInfo,test);	
		 }
	 }
	 
	 @DataProvider(name="getLogsInvaliddata")
	 public Object[][] getLogsInvaliddata(){
		 return new Object[][] {
			 {"Get Logs by source_id where id is null & using csr token",
				 csr_Token, null, SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID},
			 {"Get Logs by source_id where id is null & using csr_readonly user token",
					 csr_readonly_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID},
			 {"Get Logs by source_id where id is null & using direct org user token",
						 direct_validToken, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID},
			 {"Get Logs by source_id where id is null & using msp org user token",
							 msp_user_validToken, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID},
			 {"Get Logs by source_id where id is null & using suborg user token",
								 account_user_validToken, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID},
			 {"Get Logs by source_id where id is null & using msp Account Admin token",
									 msp_account_admin_validToken, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID},

			 {"Get Logs by source_id where id is invalid & using csr token",
										 csr_Token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID},
			 {"Get Logs by source_id where id is invalid & using csr_readonly user token",
											 csr_readonly_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID},
			 {"Get Logs by source_id where id is invalid & using direct org user token",
												 direct_validToken, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID},
			 {"Get Logs by source_id where id is invalid & using msp org user token",
													 msp_user_validToken, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID},
			 {"Get Logs by source_id where id is invalid & using suborg user token",
														 account_user_validToken, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID},
			 {"Get Logs by source_id where id is invalid & using msp Account Admin token",
															 msp_account_admin_validToken, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID},

			 {"Get Logs by source_id where id is random & using csr token",
																 csr_Token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.RESOURCE_NOT_FOUND},
			 {"Get Logs by source_id where id is random & using csr_readonly user token",
																	 csr_readonly_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.RESOURCE_NOT_FOUND},
			 {"Get Logs by source_id where id is random & using direct org user token",
																		 direct_validToken, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.RESOURCE_NOT_FOUND},
			 {"Get Logs by source_id where id is random & using msp org user token",
																			 msp_user_validToken, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.RESOURCE_NOT_FOUND},
			 {"Get Logs by source_id where id is random & using suborg user token",
																				 account_user_validToken, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.RESOURCE_NOT_FOUND},
			 {"Get Logs by source_id where id is random & using msp Account Admin token",
																					 msp_account_admin_validToken, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.RESOURCE_NOT_FOUND},
		 };
	 }
	 
	 @Test(dataProvider = "getLogsInvaliddata")
	 public void getLogsBySourceIdInvalid(String caseType, String token, String source_id, int expectedStatuCode, SpogMessageCode expectedErrorMessage) {
		
		 test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()); 				
		 test.assignAuthor("Kanamarlapudi, Chandra Kanth");

		 String additionalURL=null;
		 
		 test.log(LogStatus.INFO, caseType);
		 Response response= log4SPOGServer.getSourceIdLogs(source_id,token,additionalURL,test);
		 log4SPOGServer.checkGetSourcesIdLogs(response,expectedStatuCode,null,1,20,null,null,expectedErrorMessage,test);
		 
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
