package api.policies;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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

import Constants.AuditCodeConstants;
import Constants.ConnectionStatus;
import Constants.DestinationStatus;
import Constants.DestinationType;
import Constants.ErrorCode;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGCloudRPSServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGHypervisorsServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import invoker.SiteTestHelper.siteType;
import io.restassured.response.Response;

public class UpdatePolicyTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private SPOGDestinationServer spogDestinationServer;
	private Policy4SPOGServer policy4SPOGServer;
	private SPOGCloudRPSServer spogcloudRPSServer;
	private SPOGHypervisorsServer spogHypervisorsServer;
	private UserSpogServer userSpogServer;
	//public int Nooftest;
	//private ExtentReports rep;
	private ExtentTest test;
	private String common_password = "Mclaren@2013";
	private String csrAdminUserName;
	private String csrAdminPassword;
	private String csr_token;
	
	private String prefix_direct = "spog_Prasad_direct";
	private String postfix_email = "@arcserve.com";
	private String direct_org_name = prefix_direct + "_org";
	private String direct_org_email = direct_org_name + postfix_email;
	private String direct_org_first_name = direct_org_name + "_first_name";
	private String direct_org_last_name = direct_org_name + "_last_name";
	private String direct_user_name = prefix_direct + "_admin";
	private String direct_user_name_email = prefix_direct + "_admin" + postfix_email;
	private String direct_user_first_name = direct_user_name + "_first_name";
	private String direct_user_last_name = direct_user_name + "_last_name";
	private String direct_user_validToken;
	private String direct_site_token;
	

	private String prefix_msp = "spog_Prasad_msp";
	private String msp_org_name = prefix_msp + "_org";
	private String msp_org_email = msp_org_name + postfix_email;
	private String msp_org_first_name = msp_org_name + "_first_name";
	private String msp_org_last_name = msp_org_name + "_last_name";
	private String msp_user_name = prefix_msp + "_admin";
	private String msp_user_name_email = prefix_msp + "_admin" + postfix_email;
	private String msp_user_first_name = msp_user_name + "_first_name";
	private String msp_user_last_name = msp_user_name + "_last_name";
	private String msp_user_validToken;
	private String msp_site_token;
	
	private String prefix_msp_b = "spog_Prasad_msp_b";
	private String msp_org_name_b = prefix_msp_b + "_org";
	private String msp_org_email_b = msp_org_name_b + postfix_email;
	private String msp_org_first_name_b = msp_org_name_b + "_first_name";
	private String msp_org_last_name_b = msp_org_name_b + "_last_name";
	private String mspb_user_validToken;
	
	private String prefix_msp_account_admin = "spog_Prasad_msp_account";
	private String msp_account_admin_email = prefix_msp_account_admin+postfix_email;
	private String msp_account_admin_first_name = prefix_msp_account_admin+"_first_name";
	private String msp_account_admin_last_name = prefix_msp_account_admin+"_last_name";
	private String msp_account_admin_validToken;
	
	
	private String initial_sub_org_name_a = "spog_qa_Prasad_bq_do_not_delete_sub_a";
	private String initial_sub_email_a = "spog_qa_sub_Prasad_a@arcserve.com";
	private String initial_sub_first_name_a = "spog_qa_sub_Prasad_a";
	private String initial_sub_last_name_a = "spog_qa_sub_deverakonda_a";
	private String sub_orga_user_validToken;
	private String suborga_site_token;
	
	private String initial_sub_org_name_b = "spog_qa_sub_Prasad_b";
	private String initial_sub_email_b = "spog_qa_sub_Prasad_b@arcserve.com";
	private String initial_sub_first_name_b = "spog_qa_sub_Prasad_b";
	private String initial_sub_last_name_b = "spog_qa_sub_deverakonda_b";
	private String sub_orgb_user_validToken;
	
	private String site_version="1.0.0";
	private String gateway_hostname="Prasad";
	//used for test case count like passed,failed,remaining cases
	//private SQLServerDb bqdb1;
	//public int Nooftest;
	int passedcases=0;
	int failedcases=0;
	int skippedcases=0;
	//long creationTime;
	String buildnumber=null;
	String BQame=null;
	//private testcasescount count1;
	
	
	String direct_organization_id="76572f7e-931e-4438-a5f2-3144eca1e6d6";
	String msp_organization_id;
	String msp_organization_id_b;
	String sub_org_Id;
	String sub_org_Id_1;
	
	String csr_user_id;
	String direct_user_id;
	String msp_user_id;
	String mspb_user_id;
	String suborga_user_id;
	String suborgb_user_id;
	String user_id_msp_account_admin;
	
	String direct_site_id;
	String msp_site_id;
	String mspb_site_id;
	String suborga_site_id;
	String suborgb_site_id;
	
	String task_Id = null;
	String schedule_Id = null;
	String throttle_Id = null;
	String policy_Id = null;
	String validToken;
	String datastore_id = null;
	String policy_id = null;
	String destination_id = null;
	String rps_server_id = null;
	String rps_server_name = "10.60.17.102";
	
	String cloud_account_id;
	String cloud_account_token;
	String msp_cloud_account_id;
	String msp_cloud_account_token;
	
//	private String runningMachine;
//	private String buildVersion;
	
	private String  org_model_prefix=this.getClass().getSimpleName();
		
	@BeforeClass
	@Parameters({ "baseURI", "port","csrAdminUserName", "csrAdminPassword","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword, String logFolder,String runningMachine,String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		policy4SPOGServer= new Policy4SPOGServer(baseURI, port);
		spogHypervisorsServer = new SPOGHypervisorsServer(baseURI,port);
		spogcloudRPSServer = new SPOGCloudRPSServer(baseURI,port);
		userSpogServer = new UserSpogServer(baseURI,port);
		rep = ExtentManager.getInstance("UpdatePolicyTest", logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Prasad.Deverakonda";
		
		Nooftest=0;
		this.csrAdminUserName = adminUserName;
		this.csrAdminPassword = adminPassword;
		//Used for creating a build number with dateformat
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date=new java.util.Date();
		this.buildVersion=buildVersion+"_"+dateFormater.format(date);
		
		this.BQName=this.getClass().getSimpleName();
		this.runningMachine=runningMachine;
		System.out.println("The value of hit is "+count1.isstarttimehit());
		
		
		if( count1.isstarttimehit( ) == 0 ) 
		{
			System.out.println("into creation time");
			creationTime=System.currentTimeMillis();
			count1.setcreationtime(creationTime);
			
			// creationTime = System.currentTimeMillis();
			    try
			    {
				    bqdb1.updateTable(BQName, this.runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0", String.valueOf(Nooftest), count1.getcreationtime(), "InProgress",author+" and Rest server is "+baseURI.split("//")[1]);
			    } 
			    catch (ClientProtocolException e) {
				    // TODO Auto-generated catch block
				       e.printStackTrace();
			    } 
			    catch (IOException e)
			    {
				    // TODO Auto-generated catch block
				        e.printStackTrace();
			    }
		}

		
		
//***************************************DIRECT****************************************************		
		//login in as csr admin
		
				test.log(LogStatus.INFO, "Logging with csrAdmin");
				spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
				csr_token = spogServer.getJWTToken();
		
				test.log(LogStatus.INFO, "Get the logged in user id ");
				csr_user_id = spogServer.GetLoggedinUser_UserID();
				test.log(LogStatus.INFO, "The csr user id is "+csr_user_id);
		
				String prefix = RandomStringUtils.randomAlphanumeric(8);
		
		//Create a direct org
				test.log(LogStatus.INFO, "create a direct org");
				this.direct_org_email = prefix+this.direct_org_email;
				this.direct_organization_id = spogServer.CreateOrganizationByEnrollWithCheck(prefix+direct_org_name+org_model_prefix, SpogConstants.DIRECT_ORG, this.direct_org_email, common_password, prefix+direct_org_first_name, prefix+direct_org_last_name);
				

				
				spogServer.userLogin(this.direct_org_email, common_password);
				test.log(LogStatus.INFO,"Getting the JWTToken for the Logged in user");
				direct_user_validToken = spogServer.getJWTToken();
				//direct_user_validToken=validToken;
				
				Response response=spogServer.getCloudAccounts(direct_user_validToken, "", test);
				cloud_account_id=response.then().extract().path("data["+0+"].cloud_account_id");
				
				
				test.log(LogStatus.INFO,"The token is :"+ direct_user_validToken );
		
				test.log(LogStatus.INFO, "Get the logged in user id ");
				direct_user_id = spogServer.GetLoggedinUser_UserID();
				test.log(LogStatus.INFO, "The direct org user id is "+direct_user_id);
	 
		//Create a site and login and get the site get the token
				
				test.log(LogStatus.INFO, "Create a site/register/login to the site");
		//		direct_site_id = gatewayServer.createsite_register_login(direct_organization_id, direct_user_validToken, direct_user_id, "ts", "1.0.0", spogServer, test);
		
		//		cloud_account_token = gatewayServer.getJWTToken();
				
		//		String direct_site_secret = response.then().extract().path("data.site_secret");
		//Create Cloud Account
				
				/*test.log(LogStatus.INFO, "Get the datacenter id");
				spogDestinationServer.setToken(direct_user_validToken);
				String[] datacenters = spogDestinationServer.getDestionationDatacenterID();
				spogServer.setToken(direct_user_validToken);
			*/	//Response response = spogServer.getCloudAccounts(msp_site_token, "", test);
				//response.then().extract().path("data.cloud_account_id");
//				cloud_account_id = spogServer.createCloudAccountWithCheck("test", "tt", "tata", "cloud_direct", direct_organization_id, "SKUTESTDATA_1_0_0_0_"+prefix, "SKUTESTDATA_1_0_0_0_"+prefix, datacenters[0],test);
				
//				response = gatewayServer.LoginSite(cloud_account_id, direct_site_secret, test);
//				cloud_account_token = response.then().extract().path("data.token");
									
//***************************************MSP****************************************************	
				
				//Setting csr toekn to MSP
				prefix = RandomStringUtils.randomAlphanumeric(8);
				spogServer.setToken(csr_token);
				
		//Create a MSP org
				
				test.log(LogStatus.INFO, "create a msp org");
				this.msp_org_email = prefix+this.msp_org_email;
				this.msp_organization_id = spogServer.CreateOrganizationByEnrollWithCheck(prefix+msp_org_name+org_model_prefix, SpogConstants.MSP_ORG, this.msp_org_email, common_password, prefix+msp_org_first_name, prefix+msp_org_last_name);
				spogServer.userLogin(this.msp_org_email, common_password);
				test.log(LogStatus.INFO,"Getting the JWTToken for the Logged in user");
				msp_user_validToken = spogServer.getJWTToken();
				//msp_user_validToken=validToken;
				test.log(LogStatus.INFO,"The token is :"+ msp_user_validToken );
		
				test.log(LogStatus.INFO, "Get the logged in user id ");
				msp_user_id = spogServer.GetLoggedinUser_UserID();
				test.log(LogStatus.INFO, "The MSP org user id is "+msp_user_id);
	 
				response=spogServer.getCloudAccounts(msp_user_validToken, "", test);
				msp_cloud_account_id=response.then().extract().path("data["+0+"].cloud_account_id");
		//Create a site for MSP Org and get the token
				
				test.log(LogStatus.INFO, "Create a site/login to the site");
				//msp_site_id = gatewayServer.createsite_register_login(msp_organization_id, msp_user_validToken, msp_user_id, "ts", "1.0.0", spogServer, test);
				//msp_site_token = gatewayServer.getJWTToken();
				
		//Create Cloud Account
				
				/*test.log(LogStatus.INFO, "Get the datacenter id");
				spogDestinationServer.setToken(msp_user_validToken);
				datacenters = spogDestinationServer.getDestionationDatacenterID();
				spogServer.setToken(msp_user_validToken);
			*/	//Response response = spogServer.getCloudAccounts(msp_site_token, "", test);
				//response.then().extract().path("data.cloud_account_id");
//				msp_cloud_account_id = spogServer.createCloudAccountWithCheck("test", "tt", "tata", "cloud_direct", msp_organization_id, "SKUTESTDATA_1_0_0_0_"+prefix, "SKUTESTDATA_1_0_0_0_"+prefix, datacenters[0],test);
				
//				Response response = gatewayServer.LoginSite(msp_cloud_account_id, "tt", test);
//				msp_cloud_account_token = response.then().extract().path("data.token");

//***************************************SUB ORG****************************************************				
				
		

				//Create a suborg under MSP org and a user for sub org
				prefix = RandomStringUtils.randomAlphanumeric(8);

				test.log(LogStatus.INFO, "Create a suborg A under msp org");
				sub_org_Id = spogServer.createAccountWithCheck(msp_organization_id, prefix+initial_sub_org_name_a,
						msp_organization_id);
				


				test.log(LogStatus.INFO, "Create a direct user for sub org A");
				this.initial_sub_email_a = prefix + initial_sub_email_a;
				suborga_user_id = spogServer.createUserAndCheck(this.initial_sub_email_a, common_password,
						prefix + this.initial_sub_first_name_a, prefix + this.initial_sub_last_name_a,
						SpogConstants.DIRECT_ADMIN, sub_org_Id, test);
				
				test.log(LogStatus.INFO, "Login in to sub org A");
				spogServer.userLogin(this.initial_sub_email_a, common_password);
				test.log(LogStatus.INFO,"Getting the JWTToken for the Logged in user");
				sub_orga_user_validToken = spogServer.getJWTToken();
				
				test.log(LogStatus.INFO, "Create a site/register/login to the site");
				//msp_site_id = gatewayServer.createsite_register_login(msp_organization_id, msp_user_validToken, msp_user_id, "ts", "1.0.0", spogServer, test);
				response = spogServer.createSite(spogServer.ReturnRandom("TestSubOrg"), "cloud_direct", sub_org_Id, sub_orga_user_validToken, test);
				suborga_site_id = response.then().extract().path("data.site_id");

	}
	

	
	@DataProvider(name = "updatepolicybyId")
	public final Object[][] updatepolicybyId() {
		return new Object[][] {

		
			
			//BAAS
			
			{"Policy Update for Direct Organization for BAAS",direct_organization_id, this.direct_user_validToken, cloud_account_id,"cloud_direct_file_folder_backup","cloud_direct_baas"},
			{"Policy Update for sub Organization for BAAS",sub_org_Id, this.msp_user_validToken,suborga_site_id,"cloud_direct_file_folder_backup","cloud_direct_baas"},
			
			{"Policy Update for Direct Organization for BAAS (Image)",direct_organization_id, this.direct_user_validToken, cloud_account_id,"cloud_direct_image_backup","cloud_direct_baas"},
			{"Policy Update for sub Organization for BAAS (Image)",sub_org_Id, this.msp_user_validToken,suborga_site_id,"cloud_direct_image_backup","cloud_direct_baas"},
/*			
			//DRAAS
			
			{"Policy Update for Direct Organization for DRAAS",direct_organization_id,this.direct_user_validToken, cloud_account_id,"cloud_direct_image_backup","cloud_direct_draas"},
			{"Policy Update for Sub Organization for DRAAS",sub_org_Id, this.msp_user_validToken,suborga_site_id,"cloud_direct_image_backup","cloud_direct_draas"},
			
			
			//CH
			
			{"Policy Update for Direct Organization for UDP",direct_organization_id, direct_user_validToken,cloud_account_id, "udp_replication_from_remote","cloud_hybrid_replication"},
			{"Policy Update for sub Organization for UDP",sub_org_Id, this.msp_user_validToken, suborga_site_id, "udp_replication_from_remote","cloud_hybrid_replication"},
			
*/
		};
	}
	
	@Test(dataProvider = "updatepolicybyId")
	public void updatepolicybypolicyid(String organizationType,
									 String organization_id,
									 String validToken,
							
									 String site_id,
									 String task_type,
									 String policy_type
									 ) {
	
	/*@Test(dataProvider = "updatepolicybyId")
	public void updatepolicybypolicyid(String organizationType,
									 String organization_id,
									 String validToken,
									 String cloud_account_id,
									 String cloud_account_Token,
									 String site_id,
									 String task_type,
									 String policy_type
									 ) {*/
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		
		long start_time_ts;
		long end_time_ts;
		
/*		{"Assign datastore to direct org",csr_organization_id,csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,2048,false,"C:\\"+spogServer.ReturnRandom("dest")+"\\data","administrator",common_password,
			"C:\\"+spogServer.ReturnRandom("dest")+"\\index","administrator",common_password, "C:\\"+spogServer.ReturnRandom("dest")+"\\hash","administrator",common_password,4,
			"C:\\"+spogServer.ReturnRandom("dest"),"administrator",common_password,"standard","abc",true,"abc@arcserve.com",
			direct_organization_id, direct_user_validToken, direct_site_id,datacenters[0],"cloud_hybrid_store","4", "true", "4", "true" ,spogServer.ReturnRandom("destname")},
*/		
		
			int block_size = 4;
		   int hash_memory = 2048;
		   long capacity = 2048;
		   boolean hash_on_ssd = false;
		   String data_destination_path = "E:\\"+spogServer.ReturnRandom("dest")+"\\data";
		   String data_destination_username = "administrator";
		   String data_destination_password = common_password;
		   String index_destination_path = "E:\\"+spogServer.ReturnRandom("dest")+"\\index";;
		   String index_destination_username = "administrator";
		   String index_destination_password = common_password;
		   String hash_destination_path = "E:\\"+spogServer.ReturnRandom("dest")+"\\hash";;
		   String hash_destination_username = "administrator";
		   String hash_destination_password = common_password;
			String concurrent_active_nodes = "4";
			String datastore_path = "E:\\"+spogServer.ReturnRandom("dest");
			String datastore_username = "administrator";
			String datastore_password = common_password;
			String compression_type = "standard";
			boolean send_email = true;
			String encryption_password = "abc";
			String to_email = "abc@arcserve.com";
			String encryption_enabled = "true";
			String dedupe_enabled = "true";
			String datastore_name = spogServer.ReturnRandom("Prasad_destname");
			String destination_type = DestinationType.cloud_hybrid_store.toString();
			String compression_enabled = "true";
			String to_replace = null;
		String schedule_id=spogServer.returnRandomUUID();
		String task_id=spogServer.returnRandomUUID();
		String throttle_id=spogServer.returnRandomUUID();
		String throttle_type="network";
		String policy_name=spogServer.ReturnRandom("test");
		String policy_description=spogServer.ReturnRandom("description");
		//String policy_id = spogServer.returnRandomUUID();
		String resource_name = spogServer.ReturnRandom("Prasad")+"_";
		String destination_name = spogServer.ReturnRandom("Prasad")+"_";
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		HashMap<String, Object> scheduleSettingDTO = new HashMap<>();
		ArrayList<HashMap<String,Object>>  destinations = new ArrayList<>();
		ArrayList<HashMap<String,Object>> schedules = new ArrayList<>();
		
		spogServer.setToken(validToken);
		String source_id = null;
		
		
		if((!task_type.equalsIgnoreCase("udp_replication_from_remote"))) {

			if(organizationType.contains("sub"))
			{
				spogDestinationServer.setToken(validToken);
				String[] datacenters = spogDestinationServer.getDestionationDatacenterID();
				source_id = spogServer.createSourceWithCheck(resource_name, SourceType.machine, SourceProduct.cloud_direct, organization_id, site_id,ProtectionStatus.unprotect,ConnectionStatus.online, "windows", "SQLSERVER", test);
				test.log(LogStatus.INFO, "post the destination");
				//For sub org clout account id is not given instead site_id is provided to create destination
				destination_id = spogDestinationServer.createDestinationWithCheck(UUID.randomUUID().toString(),organization_id, site_id, datacenters[0], DestinationType.cloud_direct_volume.toString(), 
						DestinationStatus.running.toString(), "20", site_id,"normal", RandomStringUtils.randomAlphanumeric(4)+"host-t", "7D", "7 Days",
						"0", "0", "0", "0", "0", "0", "0", "0", 
						"0", "0", destination_name, test);
			} else
			{
				spogDestinationServer.setToken(validToken);
				String[] datacenters = spogDestinationServer.getDestionationDatacenterID();
				source_id = spogServer.createSourceWithCheck(resource_name, SourceType.machine, SourceProduct.cloud_direct, organization_id, site_id,ProtectionStatus.unprotect,ConnectionStatus.online, "windows", "SQLSERVER", test);
				test.log(LogStatus.INFO, "post the destination");
				//For sub org clout account id is not given instead site_id is provided to create destination
				destination_id = spogDestinationServer.createDestinationWithCheck(UUID.randomUUID().toString(),organization_id, site_id, datacenters[0], DestinationType.cloud_direct_volume.toString(), 
						DestinationStatus.running.toString(), "20", site_id,"normal", RandomStringUtils.randomAlphanumeric(4)+"host-t", "7D", "7 Days",
						"0", "0", "0", "0", "0", "0", "0", "0", 
						"0", "0", destination_name, test);
			}
		}


		policy4SPOGServer.setToken(validToken);
		if(task_type.equalsIgnoreCase("udp_replication_from_remote")) {

			//*************************************************************Changed by Prasad *************************************************************			


			spogDestinationServer.setToken(validToken);
			source_id = spogServer.createSourceWithCheck(resource_name, SourceType.machine, SourceProduct.udp, organization_id, site_id, ProtectionStatus.unprotect,ConnectionStatus.online, "windows", "SQLSERVER", test);


			test.log(LogStatus.INFO, "Logging with csrAdmin");
			spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
			csr_token = spogServer.getJWTToken();
			String csr_organizationId = spogServer.GetLoggedinUserOrganizationID();
			//String csr_organizationName = spogServer.getOrganizationNameByID(csr_organizationId);
			String csr_organizationName = spogServer.GetLoggedinUserOrganizationNameByID(csr_organizationId);
			
			//create Site 
			
			//String csr_site_id = gatewayServer.createsite_register_login(csr_organizationId, csr_token, csr_user_id, "10.60.17.86", site_version, spogServer, test);
/*			Response response = spogServer.createSite("PrasadGatewaySite", siteType.gateway.toString(), csr_organizationId, csr_token, test);
			Map<String, String> SiteInfo= spogServer.checkCreateSite(response, SpogConstants.SUCCESS_POST, "PrasadGatewaySite", siteType.gateway.toString(), csr_organizationId, csr_user_id, null, test);
			String csr_site_id = SiteInfo.get("site_id");
			//copy and paste the registration code in gateway server
			String site_reg_code = SiteInfo.get("registration_basecode");*/
			
			String csr_site_name = "PrasadGatewaySite";
			String csr_site_id = "4cd70dec-db92-409b-9b65-1e3bb4e22cb2";
			spogDestinationServer.setToken(csr_token);
			//Rakesh Site ID
			//String csr_site_id = "e6f3de07-d1ae-413f-89f4-0fd296352644";
			String[] datacenters = spogDestinationServer.getDestionationDatacenterID();
			String[] datacenter_name = spogDestinationServer.getDestionationDatacenterName();
			Response response = spogcloudRPSServer.createCloudRPS(rps_server_name, "8014", "https", "administrator", "Mclaren@2020", csr_organizationId, csr_site_id, datacenters[0], csr_token);


			HashMap<String,Object> composedInfo = new HashMap<>();
			//composedInfo =  spogcloudRPSServer.composeCloudRPSInfo("10.60.17.102", "8014", "https", "administrator", "Mclaren@2020", csr_organizationId, csr_site_id, datacenters[0], spogServer.getLoggedInUser(csr_token, SpogConstants.SUCCESS_LOGIN, test));
			String user_id = spogServer.GetLoggedinUser_UserID();
			String user_name = spogServer.GetLoggedinUser_UserName();

			//composedInfo =  spogcloudRPSServer.composeCloudRPSInfo("10.60.17.102", "8014", "https", "administrator", "Mclaren@2020", csr_organizationId, csr_site_id, datacenters[0], spogServer.getLoggedInUser(csr_token, SpogConstants.SUCCESS_LOGIN, test));
			composedInfo = spogcloudRPSServer.composeCloudRPSInfo("10.60.17.102", "8014", "https", "administrator", "Mclaren@2020", csr_organizationId, csr_organizationName, csr_site_id, csr_site_name, datacenters[0], datacenter_name[0], user_id,user_name);
			
			test.log(LogStatus.INFO, "Perform the response validation");
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
			rps_server_id = spogcloudRPSServer.verifyCloudRPS(response, composedInfo, test);

			SpogMessageCode expectederrorMessage = null;  
			test.log(LogStatus.INFO, "Start the  cloud RPS in organization "+organizationType);

			test.log(LogStatus.INFO, "Compose the hashmap for deduplication info "+organizationType);
			HashMap<String,Object> deduplicationInfo = spogcloudRPSServer.composededuplicationInfo(block_size, hash_memory,hash_on_ssd, data_destination_path, data_destination_username, data_destination_password, 
					index_destination_path, index_destination_username, index_destination_password, hash_destination_path, hash_destination_username, hash_destination_password);

			test.log(LogStatus.INFO, "Compose the hashmap for datastoreproperties info "+organizationType);

			HashMap<String,Object> datastorepropertiesInfo = spogcloudRPSServer.composedatastorepropertiesInfo(Integer.parseInt(concurrent_active_nodes), capacity, datastore_path, datastore_username, datastore_password, compression_type, encryption_password, deduplicationInfo, Boolean.valueOf(send_email), to_email);

			test.log(LogStatus.INFO, "Create the cloud rps datastore "+organizationType);

			response = spogcloudRPSServer.createCloudRPSDataStore(datastore_name, Boolean.valueOf(dedupe_enabled), Boolean.valueOf(encryption_enabled), Boolean.valueOf(compression_enabled), rps_server_id, datastorepropertiesInfo, csr_token);

			test.log(LogStatus.INFO, "Perform the response validation");

			datastore_id = spogcloudRPSServer.validateCloudRPSDataStoreResponse(response, datastore_name, Boolean.valueOf(dedupe_enabled), Boolean.valueOf(encryption_enabled), Boolean.valueOf(compression_enabled), rps_server_id, rps_server_name, datastorepropertiesInfo, SpogConstants.SUCCESS_POST, null, "", test);

			spogDestinationServer.setToken(validToken);
			destination_id = spogDestinationServer.createDestinationWithCheck(null,organization_id, csr_site_id, 
					datacenters[0],destination_type, "running","0",
					"", "normal", "iran-win7", "2M", "2M", 
					"0","0", "31", "0", "2", "0", concurrent_active_nodes, dedupe_enabled, "4", compression_enabled ,destination_name,test);

			test.log(LogStatus.INFO, "Assign the rps datastore to the destination");
			response = spogDestinationServer.assigndatastore(destination_id, datastore_id, csr_token, test);
			//Compose hashmap for cloudhybrid
			HashMap<String, Object> expectedresponse_cloudhybrid = new HashMap<>();
			
			expectedresponse_cloudhybrid.put("concurrent_active_node",concurrent_active_nodes);
			expectedresponse_cloudhybrid.put("is_deduplicated",Boolean.valueOf(dedupe_enabled));
			expectedresponse_cloudhybrid.put("block_size",block_size);
			expectedresponse_cloudhybrid.put("is_compressed",Boolean.valueOf(compression_enabled));
			expectedresponse_cloudhybrid.put("compression",compression_type);
			//compose hasmap for destination
			HashMap<String, Object> expectedresponse = new HashMap<>();
			expectedresponse.put("destination_id", destination_id);
			expectedresponse.put("destination_name",destination_name);
			expectedresponse.put("destination_type",destination_type);
			expectedresponse.put("datastore_id",datastore_id);
			expectedresponse.put("organization_id", organization_id);
			//expectedresponse.put("site_id",csr_site_id);
			expectedresponse.put("cloud_hybrid_store",expectedresponse_cloudhybrid);
			
			spogDestinationServer.checkassigneddatastore(response, expectedresponse, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			//response = spogDestinationServer.assigndatastore(destination_id_1, datastore_id, validToken, test);		
			//spogDestinationServer.unassigndatastore(destination_id, validToken, test);
			//spogDestinationServer.unassigndatastore(destination_id_1, validToken, test);
		
			
			
			//use CSR Token
			/*Create RPS
			Create Datastore
			AssignDatastore to Destination
			Delete RPS
			Delete Datastore
			*/
//**********************************************************************Changes End**************************************************			
			

			HashMap<String, Object> CustomScheduleDTO=policy4SPOGServer.createCustomScheduleDTO("1524469596000", "full","1","true","10","minutes",test);
			scheduleSettingDTO=policy4SPOGServer.createScheduleSettingDTO( null,CustomScheduleDTO,test);
			schedules =policy4SPOGServer.createPolicyScheduleDTO(null,spogServer.returnRandomUUID(), 
					  "custom", task_id, destination_id, scheduleSettingDTO, "06:00", "12:00", test);
			
		}else {
			test.log(LogStatus.INFO, "Create cloud direct schedule");
			
			HashMap<String, Object> cloudDirectScheduleDTO=policy4SPOGServer.createCloudDirectScheduleDTO("0 6 * * *", test);
			
			test.log(LogStatus.INFO, "Create schedule settings");
			scheduleSettingDTO=policy4SPOGServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null,test);
			schedules =policy4SPOGServer.createPolicyScheduleDTO(null,schedule_id, 
					  "1d", task_id, destination_id, scheduleSettingDTO, "06:00", "12:00", task_type ,destination_name,test);
		}
		
		test.log(LogStatus.INFO, "Exclude the folders from cloud direct file backup");  
		
		ArrayList<HashMap<String,Object>> excludes=policy4SPOGServer.createExcludeInfoDTO(null, "path", "c:\\tmp", test);
		HashMap<String, Object> cloudDirectLocalBackupDTO=policy4SPOGServer.createCloudDirectLocalBackupDTO("d:\\tmp", "true", test);
		HashMap<String, Object> cloudDirectFileBackupTaskInfoDTO=policy4SPOGServer.createCloudDirectFileBackupTaskInfoDTO("d:\\tmp", cloudDirectLocalBackupDTO, excludes,test);
		
		ArrayList<String> drivers = new ArrayList<>();
		drivers.add("C");
		
		HashMap<String, Object> cloudDirectimageBackupTaskInfoDTO = policy4SPOGServer.createCloudDirectImageBackupTaskInfoDTO(drivers, cloudDirectLocalBackupDTO, test);
		
		HashMap<String, Object> PerformARTestOption=policy4SPOGServer.createPerformARTestOption("true", "true", "true", "true", test);
		HashMap<String, Object> RetentionPolicyOption =policy4SPOGServer.createRetentionPolicyOption ("2", "2", "2", "2", test);
		HashMap<String, Object> udp_replication_from_remote =policy4SPOGServer.createUdpReplicationFromRemoteInfoDTO(PerformARTestOption, RetentionPolicyOption, test);
			  
		test.log(LogStatus.INFO, "Create task type and link it to the destination ");
		if(task_type == "cloud_direct_file_folder_backup") {
			destinations= policy4SPOGServer.createPolicyTaskDTO(null, task_id, task_type, destination_id, "none", null, cloudDirectFileBackupTaskInfoDTO, null, test);
		}else if(task_type == "cloud_direct_image_backup") {
			destinations= policy4SPOGServer.createPolicyTaskDTO(null, task_id, task_type, destination_id, "none", cloudDirectimageBackupTaskInfoDTO, null, null, test);
		}else if(task_type.equalsIgnoreCase("udp_replication_from_remote")) {
			 
			 destinations= policy4SPOGServer.createPolicyTaskDTO(null, task_id, task_type, destination_id, "none", null, null, udp_replication_from_remote, test);
		}
		

		test.log(LogStatus.INFO, "Create network throttle ");
		
		ArrayList<HashMap<String,Object>> throttles =policy4SPOGServer.createPolicyThrottleDTO(null, throttle_id, task_id, throttle_type, "1200", "1", "06:00", "18:00", task_type,destination_id,destination_name,test);
		
		test.log(LogStatus.INFO, "Create a policy of type backup_recovery and task of type "+task_type);
		
		//spogServer.postCloudhybridInFreeTrial(spogServer.getJWTToken(), organization_id,organizationType,true,false);
		
		Response response=policy4SPOGServer.createPolicy(policy_name, policy_description, policy_type, null, "true", source_id, destinations, schedules, throttles, policy_id, organization_id, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		policy_Id = response.then().extract().path("data.policy_id");
		//sleep for 20 seconds
		sleepTime(20000);
		response = policy4SPOGServer.getPolicyById(validToken, policy_Id, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		
		//response.then().body("data.policy_status", equalTo("failure"));
		response.then().body("data.policy_status", not("deploying"));
		test.log(LogStatus.INFO, "Update the policy by policy name");
		policy_name = spogServer.ReturnRandom("test1");
		
		response = policy4SPOGServer.updatePolicy(policy_name, policy_description, policy_type, null, "true", source_id, destinations, schedules, throttles, policy_Id, organization_id, validToken,test);
		policy4SPOGServer.checkPolicyDestinations(response,SpogConstants.SUCCESS_GET_PUT_DELETE,destinations,test);
		policy4SPOGServer.checkPolicyCommon(response, SpogConstants.SUCCESS_GET_PUT_DELETE, policy_name, policy_description, policy_type, null, "true", "success", source_id, policy_Id, organization_id, test);
		policy4SPOGServer.checkPolicySchedules(response,SpogConstants.SUCCESS_GET_PUT_DELETE,schedules,test);
		
		//sleep for 20 seconds
		sleepTime(20000);
	}	
	
	@AfterMethod
	public void getResult(ITestResult result){
		if(result.getStatus() == ITestResult.FAILURE){
			count1.setfailedcount();
			//remaincases=Nooftest-passedcases-failedcases;
			test.log(LogStatus.FAIL, "Test Case Failed is "+result.getName()+" with parameters as "+Arrays.asList(result.getParameters()) );
			test.log(LogStatus.FAIL, result.getThrowable().getMessage());

		}else if(result.getStatus() == ITestResult.SKIP){
			test.log(LogStatus.SKIP, "Test Case Skipped is "+result.getName());
			count1.setskippedcount();
		}else if(result.getStatus()==ITestResult.SUCCESS){
			count1.setpassedcount();
			//remaincases=Nooftest-passedcases-failedcases;

		}
		// ending test
		//endTest(logger) : It ends the current test and prepares to create HTML report
		rep.endTest(test);
		//rep.flush();
	}

	@AfterClass
	public void updatebd() {
		
		//spogcloudRPSServer.deleteCloudRPS(rps_server_id, csr_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		test.log(LogStatus.INFO, "Delete sources by id");
		//spogServer.deleteSourceByID(source_id, test);
		test.log(LogStatus.INFO, "Delete the policy by policy id");
		policy4SPOGServer.deletePolicybyPolicyId(csr_token, policy_Id, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		test.log(LogStatus.INFO, "Delete the cloud rps datastore");
//		spogDestinationServer.unassigndatastore(destination_id, csr_token, test);
		//spogDestinationServer.deletedestinationbydestination_Id(destination_id, csr_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		spogDestinationServer.recycleCloudVolumesAndDelOrg(direct_organization_id, test);
		spogDestinationServer.recycleCloudVolumesAndDelOrg(msp_organization_id, test);
		spogDestinationServer.recycleCloudVolumesAndDelOrg(sub_org_Id, test);
//		spogcloudRPSServer.deleteCloudRPSDataStore(datastore_id, csr_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
//		spogcloudRPSServer.deleteCloudRPS(rps_server_id, csr_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		
		test.log(LogStatus.INFO, "Performing the operations to delete the user and orginzation by logging in as csr admin");	
		test.log(LogStatus.INFO, "Login in as csr admin to delete the organization");
		spogServer.setToken(csr_token);
		
		test.log(LogStatus.INFO, "Delete the site");
	//	Response response = spogServer.deleteSite(cloud_account_id, csr_token);
	//	spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		
		test.log(LogStatus.INFO, "Delete the organizations");
		spogDestinationServer.setToken(csr_token);
		

//		spogDestinationServer.recycleCloudVolumesAndDelOrg(direct_organization_id, test);
//		spogDestinationServer.recycleCloudVolumesAndDelOrg(msp_organization_id, test);
//		spogDestinationServer.recycleCloudVolumesAndDelOrg(sub_org_Id, test);
		
//		spogServer.DeleteOrganizationWithCheck(direct_organization_id, test);
		
//		spogServer.DeleteOrganizationWithCheck(sub_org_Id, test);
//		spogServer.DeleteOrganizationWithCheck(sub_org_Id_1, test);
//		spogServer.DeleteOrganizationWithCheck(msp_organization_id, test);
//		spogServer.DeleteOrganizationWithCheck(msp_organization_id_b, test);
		try {
			if(count1.getfailedcount()>0) {
				Nooftest=count1.getpassedcount()+count1.getfailedcount()+count1.getskippedcount();
				bqdb1.updateTable(BQName, runningMachine, this.buildVersion, String.valueOf(Nooftest), Integer.toString(count1.getpassedcount()), Integer.toString(count1.getfailedcount()), String.valueOf(count1.getskippedcount()), count1.getcreationtime(), "Failed");
			}else {
				Nooftest=count1.getpassedcount()+count1.getfailedcount()+count1.getskippedcount();
				bqdb1.updateTable(BQName, runningMachine, this.buildVersion, String.valueOf(Nooftest), Integer.toString(count1.getpassedcount()), Integer.toString(count1.getfailedcount()), String.valueOf(count1.getskippedcount()), count1.getcreationtime(), "Passed");
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void sleepTime(int n) {

		try {
			Thread.sleep(n);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	

}
