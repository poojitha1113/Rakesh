package api.jobs;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
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
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Base64.Base64Coder;
import Constants.ConnectionStatus;
import Constants.DestinationStatus;
import Constants.DestinationType;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGServer;
import InvokerServer.ServerResponseCode;
import InvokerServer.UserSpogServer;
import InvokerServer.GatewayServer.siteType;
import InvokerServer.SPOGDestinationServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;

public class GetJobsbyIdTest extends base.prepare.Is4Org {

	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private SPOGDestinationServer spogDestinationServer;
	private Policy4SPOGServer policy4SPOGServer;
	private UserSpogServer userSpogServer;
	//public int Nooftest;
	//private ExtentReports rep;
	private ExtentTest test;
	private String common_password = "Mclaren@2013";
	private String csrAdminUserName;
	private String csrAdminPassword;
	private String csr_token;
	private String csr_readonly_email;
	private String csr_readonly_token;
	
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
	private String direct_cloud_id;

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
	
	private String prefix_msp_account_admin = "spog_rakesh_msp_account";
	private String msp_account_admin_email = prefix_msp_account_admin+postfix_email;
	private String msp_account_admin_first_name = prefix_msp_account_admin+"_first_name";
	private String msp_account_admin_last_name = prefix_msp_account_admin+"_last_name";
	private String msp_account_admin_validToken;
	
	
	private String initial_sub_org_name_a = "spog_qa_sub_Prasad_a";
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
	
	String cloud_account_id;
	String cloud_account_token;
	String msp_cloud_account_id;
	String msp_cloud_account_token;
	String task_type;
	String policy_type;
	String policy_id;
	
	private String site_version="1.0.0";
	private String gateway_hostname="Prasad";
	private String org_model_prefix = this.getClass().getSimpleName();
	
	//used for test case count like passed,failed,remaining cases
	//private SQLServerDb bqdb1;
	//public int Nooftest;
	int passedcases=0;
	int failedcases=0;
	int skippedcases=0;
	//long creationTime;
	String buildnumber=null;
	//String BQame=null;
	//private testcasescount count1;
	
	
	String direct_organization_id;
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
	String msp_account_admin_id;
	
	String direct_site_id;
	String msp_site_id;
	String mspb_site_id;
	String suborga_site_id;
	String suborgb_site_id;
	
	//private String runningMachine;
	//private String buildVersion;
	
	
	
	
	@BeforeClass
	@Parameters({ "baseURI", "port","csrAdminUserName", "csrAdminPassword","csrReadOnlyUser","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword, String csrReadOnlyUser, String logFolder,String runningMachine,String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		policy4SPOGServer = new Policy4SPOGServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		rep = ExtentManager.getInstance("GetJobsbyIdTest", logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Prasad.Deverakonda";
		
		Nooftest=0;
		this.csrAdminUserName = adminUserName;
		this.csrAdminPassword = adminPassword;
		this.csr_readonly_email = csrReadOnlyUser;
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
		//login in as csr admin
		test.log(LogStatus.INFO, "Logging with csrAdmin");
		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
		csr_token = spogServer.getJWTToken();
		
		test.log(LogStatus.INFO, "Get the logged in user id ");
		csr_user_id = spogServer.GetLoggedinUser_UserID();
		test.log(LogStatus.INFO, "The csr user id is "+csr_user_id);
		
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		//Create a msp org
		test.log(LogStatus.INFO, "create a msp org");
		this.msp_org_email = prefix+this.msp_org_email;
		this.msp_organization_id = spogServer.CreateOrganizationByEnrollWithCheck(prefix+msp_org_name+org_model_prefix, SpogConstants.MSP_ORG, this.msp_org_email, common_password, prefix+msp_org_first_name, prefix+msp_org_last_name);
		this.msp_organization_id = spogServer.CreateOrganizationByEnrollWithCheck(prefix+msp_org_name+org_model_prefix, SpogConstants.MSP_ORG, this.msp_org_email, common_password, prefix+msp_org_first_name, prefix+msp_org_last_name);
		
		Response response = spogServer.getCloudAccounts(spogServer.getJWTToken(), "", test);
		msp_cloud_account_id = response.then().extract().path("data[0].cloud_account_id");
		
		test.log(LogStatus.INFO, "create another msp org");
		this.msp_org_email_b = prefix+this.msp_org_email_b;
		this.msp_organization_id_b = spogServer.CreateOrganizationWithCheck(prefix+msp_org_name_b+org_model_prefix, SpogConstants.MSP_ORG, this.msp_org_email_b, common_password, prefix+msp_org_first_name_b, prefix+msp_org_last_name_b);
		
		//Create a suborg under MSP org and a user for sub org
		spogServer.userLogin(this.msp_org_email, common_password);
		test.log(LogStatus.INFO,"Getting the JWTToken for the Logged in user");
		msp_user_validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The token is :"+ msp_user_validToken );
		
		test.log(LogStatus.INFO, "Get the logged in user id ");
		msp_user_id = spogServer.GetLoggedinUser_UserID();
		test.log(LogStatus.INFO, "The msp user id is "+msp_user_id);
		
		test.log(LogStatus.INFO, "Create a site/register/login to the site");
		msp_site_id = gatewayServer.createsite_register_login(msp_organization_id, msp_user_validToken, msp_user_id, "ts", "1.0.0", spogServer, test);
		msp_site_token = gatewayServer.getJWTToken();
		
		test.log(LogStatus.INFO, "Create a suborg A under msp org");
		sub_org_Id = spogServer.createAccountWithCheck(msp_organization_id, prefix+initial_sub_org_name_a,
				msp_organization_id);
		test.log(LogStatus.INFO, "Create a suborg B under msp org");
		sub_org_Id_1 = spogServer.createAccountWithCheck(msp_organization_id, prefix+initial_sub_org_name_b,
				msp_organization_id);
		
		test.log(LogStatus.INFO, "Create a direct user for sub org A");
		this.initial_sub_email_a = prefix + initial_sub_email_a;
		suborga_user_id = spogServer.createUserAndCheck(this.initial_sub_email_a, common_password,
				prefix + this.initial_sub_first_name_a, prefix + this.initial_sub_last_name_a,
				SpogConstants.DIRECT_ADMIN, sub_org_Id, test);
		
		test.log(LogStatus.INFO, "Create a direct user for sub org B");
		this.initial_sub_email_b = prefix + initial_sub_email_b;
		suborgb_user_id = spogServer.createUserAndCheck(this.initial_sub_email_b, common_password,
				prefix + this.initial_sub_first_name_b, prefix + this.initial_sub_last_name_b,
				SpogConstants.DIRECT_ADMIN, sub_org_Id_1, test);
		
		test.log(LogStatus.INFO, "Login in to sub org A");
		spogServer.userLogin(this.initial_sub_email_a, common_password);
		test.log(LogStatus.INFO,"Getting the JWTToken for the Logged in user");
		sub_orga_user_validToken = spogServer.getJWTToken();
		
		test.log(LogStatus.INFO, "Create a site/register/login to the site");
		suborga_site_id = gatewayServer.createsite_register_login(sub_org_Id, sub_orga_user_validToken, suborga_user_id, "ts", "1.0.0", spogServer, test);
		suborga_site_token = gatewayServer.getJWTToken();
		
		test.log(LogStatus.INFO, "Login in to sub org B");
		spogServer.userLogin(this.initial_sub_email_b, common_password);
		test.log(LogStatus.INFO,"Getting the JWTToken for the Logged in user");
		sub_orgb_user_validToken = spogServer.getJWTToken();
		
		test.log(LogStatus.INFO, "Create a site/register/login to the site");
		suborgb_site_id = gatewayServer.createsite_register_login(sub_org_Id_1, sub_orgb_user_validToken, suborgb_user_id, "ts", "1.0.0", spogServer, test);
		
		
		//login to mspb and create a site
		spogServer.userLogin(this.msp_org_email_b, common_password);
		test.log(LogStatus.INFO,"Getting the JWTToken for the Logged in user");
		mspb_user_validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The token is :"+ mspb_user_validToken );
		
		test.log(LogStatus.INFO, "Get the logged in user id ");
		mspb_user_id = spogServer.GetLoggedinUser_UserID();
		test.log(LogStatus.INFO, "The msp user id is "+mspb_user_id);
		
		//login in as csr admin
		test.log(LogStatus.INFO, "Logging with csrAdmin");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword,test);
		//csr_token = spogServer.getJWTToken();

		//Create a direct org
		
		this.direct_org_email = prefix+this.direct_org_email;
		this.direct_organization_id = spogServer.CreateOrganizationByEnrollWithCheck(prefix+direct_org_name+org_model_prefix, SpogConstants.DIRECT_ORG, this.direct_org_email, common_password, prefix+direct_org_first_name, prefix+direct_org_last_name);
		spogServer.userLogin(this.direct_org_email, common_password);
		test.log(LogStatus.INFO,"Getting the JWTToken for the Logged in user");
		direct_user_validToken = spogServer.getJWTToken();
		//direct_user_validToken=validToken;
		test.log(LogStatus.INFO,"The token is :"+ direct_user_validToken );
		
		response = spogServer.getCloudAccounts(direct_user_validToken, "", test);
		direct_cloud_id = response.then().extract().path("data[0].cloud_account_id");
		
		test.log(LogStatus.INFO, "Get the logged in user id ");
		direct_user_id = spogServer.GetLoggedinUser_UserID();
		test.log(LogStatus.INFO, "The direct org user id is "+direct_user_id);
		
		test.log(LogStatus.INFO, "Create a site/register/login to the site");
		direct_site_id = gatewayServer.createsite_register_login(direct_organization_id, direct_user_validToken, direct_user_id, "ts", "1.0.0", spogServer, test);
		direct_site_token = gatewayServer.getJWTToken();
		
		//Create msp_account_admin
		test.log(LogStatus.INFO, "Create a msp account admin under msp org");
		msp_account_admin_email = prefix+msp_account_admin_email;
		spogServer.setToken(msp_user_validToken);
		msp_account_admin_id = spogServer.createUserAndCheck(msp_account_admin_email, common_password, msp_account_admin_first_name, msp_account_admin_last_name, "msp_account_admin", msp_organization_id, test);
		spogServer.userLogin(msp_account_admin_email, common_password);
		this.msp_account_admin_validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The token is :"+ msp_account_admin_validToken );
		userSpogServer.assignMspAccountAdmins(msp_organization_id, sub_org_Id, new String[] {msp_account_admin_id}, msp_user_validToken);
				
		test.log(LogStatus.INFO, "csr_readonly user login");
		spogServer.userLogin(csr_readonly_email, common_password);
		csr_readonly_token = spogServer.getJWTToken();
	
	}
	
	
	
	@DataProvider(name = "createorgandsite")
	public final Object[][] createorgandsite() {
		return new Object[][] {
			{ direct_org_name, SpogConstants.DIRECT_ORG, direct_org_email, common_password,direct_org_first_name, direct_org_last_name, 
				direct_user_name_email, common_password,direct_user_first_name, direct_user_last_name, SpogConstants.DIRECT_ADMIN},
			
				};
	}
	
	@DataProvider(name = "postjobandjobdata")
	public final Object[][] postjobandjobdata() {
		return new Object[][] {
			{"direct",direct_organization_id,direct_user_validToken,direct_site_id,direct_site_token,UUID.randomUUID().toString(),policy_id,"backup", "full", "finished"},
			{"msp",msp_organization_id,msp_user_validToken,msp_site_id,msp_site_token,UUID.randomUUID().toString(),UUID.randomUUID().toString(),"backup", "full", "finished"},
			{"suborg",sub_org_Id,sub_orga_user_validToken,suborga_site_id,suborga_site_token,UUID.randomUUID().toString(),UUID.randomUUID().toString(),"backup", "full", "finished"},
			{"suborgusingmsp",sub_org_Id,msp_user_validToken,suborga_site_id,suborga_site_token,UUID.randomUUID().toString(),UUID.randomUUID().toString(),"backup", "full", "finished"},
			{"mspaccountadmin",sub_org_Id,msp_account_admin_validToken,suborga_site_id,suborga_site_token,UUID.randomUUID().toString(),UUID.randomUUID().toString(),"backup", "full", "finished"}
		};
	}
	
	@DataProvider(name = "postjobandjobdata1")
	public final Object[][] postjobandjobdata1() {
		return new Object[][] {

/*			{"direct",direct_organization_id,direct_user_validToken,direct_site_id,direct_site_token,UUID.randomUUID().toString(),policy_id,"backup", "full", "finished","cloud_direct_file_folder_backup","cloud_direct_baas"},
			{"msp",msp_organization_id,msp_user_validToken,msp_site_id,msp_site_token,UUID.randomUUID().toString(),policy_id,"backup", "full", "finished","cloud_direct_file_folder_backup","cloud_direct_baas"},
			{"suborg",sub_org_Id,sub_orga_user_validToken,suborga_site_id,suborga_site_token,UUID.randomUUID().toString(),policy_id,"backup", "full", "finished","cloud_direct_file_folder_backup","cloud_direct_baas"},
			{"suborgusingmsp",sub_org_Id,msp_user_validToken,suborga_site_id,suborga_site_token,UUID.randomUUID().toString(),policy_id,"backup", "full", "finished","cloud_direct_file_folder_backup","cloud_direct_baas"},
			{"mspaccountadmin",sub_org_Id,msp_account_admin_validToken,suborga_site_id,suborga_site_token,UUID.randomUUID().toString(),policy_id,"backup", "full", "finished","cloud_direct_file_folder_backup","cloud_direct_baas"}
*/
			{"direct",direct_organization_id,direct_user_validToken,direct_cloud_id,direct_site_id,direct_site_token,UUID.randomUUID().toString(),policy_id,"backup", "full", "finished","cloud_direct_file_folder_backup","cloud_direct_baas"},
//			{"msp",msp_organization_id,msp_user_validToken,msp_site_id,msp_site_token,UUID.randomUUID().toString(),policy_id,"backup", "full", "finished","cloud_direct_file_folder_backup","cloud_direct_baas"},
			{"suborg",sub_org_Id,sub_orga_user_validToken,msp_cloud_account_id,suborga_site_id,suborga_site_token,UUID.randomUUID().toString(),policy_id,"backup", "full", "finished","cloud_direct_file_folder_backup","cloud_direct_baas"},
			{"suborgusingmsp",sub_org_Id,msp_user_validToken,msp_cloud_account_id,suborga_site_id,suborga_site_token,UUID.randomUUID().toString(),policy_id,"backup", "full", "finished","cloud_direct_file_folder_backup","cloud_direct_baas"},
			{"mspaccountadmin",sub_org_Id,msp_account_admin_validToken,msp_cloud_account_id,suborga_site_id,suborga_site_token,UUID.randomUUID().toString(),policy_id,"backup", "full", "finished","cloud_direct_file_folder_backup","cloud_direct_baas"}

		};
	}
	
	
	@Test(dataProvider = "postjobandjobdata1")
	public void getjobsbyjobid(String organizationType,
									 String organization_id,
									 String validToken,
									 String cloud_account_id,
									 String site_id,
									 String site_token,
									 String rps_id,
									 String policy_id,
									 String jobType,
									 String jobMethod,
									 String jobStatus,
									 String task_type,
									 String policy_type
									 ) {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		//copied code
		
		long start_time_ts;
		long end_time_ts;
		String schedule_id=spogServer.returnRandomUUID();
		String task_id=spogServer.returnRandomUUID();
		String throttle_id=spogServer.returnRandomUUID();
		String throttle_type="network";
		String policy_name=spogServer.ReturnRandom("test");
		String policy_description=spogServer.ReturnRandom("description");
		String resource_name = spogServer.ReturnRandom("Prasad")+"_";
		String destination_name = spogServer.ReturnRandom("Prasad")+"_";
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		HashMap<String, Object> scheduleSettingDTO = new HashMap<>();
		ArrayList<HashMap<String,Object>>  destinations = new ArrayList<>();
		ArrayList<HashMap<String,Object>> schedules = new ArrayList<>();
		Response response;
		
		String source_id;
		String destination_id;
		spogDestinationServer.setToken(validToken);
		String[] datacenters = spogDestinationServer.getDestionationDatacenterID();
	
		spogServer.setToken(validToken);
		
		if(organizationType.contains("sub"))
		{
			spogDestinationServer.setToken(msp_user_validToken);
			
			source_id = spogServer.createSourceWithCheck(resource_name, SourceType.machine, SourceProduct.cloud_direct, organization_id, site_id,ProtectionStatus.unprotect,ConnectionStatus.online, "windows", "SQLSERVER", test);
				test.log(LogStatus.INFO, "post the destination");
				//For sub org clout account id is not given instead site_id is provided to create destination
				destination_id = spogDestinationServer.createDestinationWithCheck(UUID.randomUUID().toString(),organization_id, site_id, datacenters[0], DestinationType.cloud_direct_volume.toString(), 
						DestinationStatus.running.toString(), "20", null,"normal", RandomStringUtils.randomAlphanumeric(4)+"host-t", "7D", "7 Days",
						"0", "0", "0", "0", "0", "0", "0", "0", 
						"0", "0", destination_name, test);
		} else
		{
			spogDestinationServer.setToken(validToken);
			source_id = spogServer.createSourceWithCheck(resource_name, SourceType.machine, SourceProduct.cloud_direct, organization_id, site_id,ProtectionStatus.unprotect,ConnectionStatus.online, "windows", "SQLSERVER", test);
			test.log(LogStatus.INFO, "post the destination");
			//For sub org clout account id is not given instead site_id is provided to create destination
			destination_id = spogDestinationServer.createDestinationWithCheck(UUID.randomUUID().toString(),organization_id, site_id, datacenters[0], DestinationType.cloud_direct_volume.toString(), 
					DestinationStatus.running.toString(), "20", null,"normal", RandomStringUtils.randomAlphanumeric(4)+"host-t", "7D", "7 Days",
					"0", "0", "0", "0", "0", "0", "0", "0", 
					"0", "0", destination_name, test);
		}
			
 
		policy4SPOGServer.setToken(validToken);
/*		if(task_type.equalsIgnoreCase("udp_replication_from_remote")) {
			destination_name = prefix+"-test";
			destination_id = spogDestinationServer.createDestinationWithCheck("",organization_id, suborga_site_id, 
		    		"91a9b48e-6ac6-4c47-8202-614b5cdcfe0c","cloud_direct_volume", "running","9",
		    		"", "normal", "iran-win7", "2M", "2M", 
		    		"0","0", "31", "0", "2", "0", "5", "true", "1", "true" ,destination_name,		
				test);
			HashMap<String, Object> CustomScheduleDTO=policy4SPOGServer.createCustomScheduleDTO("1524469596000", "full","1","true","10","minutes",test);
			scheduleSettingDTO=policy4SPOGServer.createScheduleSettingDTO( null,CustomScheduleDTO,test);
			schedules =policy4SPOGServer.createPolicyScheduleDTO(null,spogServer.returnRandomUUID(), 
					  "custom", task_id, destination_id, scheduleSettingDTO, "06:00", "12:00", test);
			
		}else {
*/			test.log(LogStatus.INFO, "Create cloud direct schedule");
			
			HashMap<String, Object> cloudDirectScheduleDTO=policy4SPOGServer.createCloudDirectScheduleDTO("0 6 * * *", test);
			
			test.log(LogStatus.INFO, "Create schedule settings");
			scheduleSettingDTO=policy4SPOGServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null,test);
			schedules =policy4SPOGServer.createPolicyScheduleDTO(null,schedule_id, 
					  "1d", task_id, destination_id, scheduleSettingDTO, "06:00", "18:00", task_type ,destination_name,test);
		
		
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
		//task_type = "cloud_direct_file_folder_backup";
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
		
		response=policy4SPOGServer.createPolicy(policy_name, policy_description, "cloud_direct_baas", null, "true", source_id, destinations, schedules, throttles, policy_id, organization_id, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		policy_id = response.then().extract().path("data.policy_id");
		
		
		
		//original code
		String datastore_id;
		//resource_name = spogServer.ReturnRandom("prasad")+"_";
		ArrayList<HashMap<String, Object>> jobsandjobdata = new ArrayList<>();
		prefix = RandomStringUtils.randomAlphanumeric(8);
		spogServer.setToken(validToken);
		
		spogDestinationServer.setToken(validToken);
		
		test.log(LogStatus.INFO, "Post Jobs to site under org "+organizationType);
		start_time_ts = System.currentTimeMillis()/1000L;
		end_time_ts = System.currentTimeMillis()/1000L+1;
		long duration = end_time_ts - start_time_ts;
		response = gatewayServer.postJob(start_time_ts, end_time_ts,organization_id,source_id, source_id, rps_id, destination_id, 
				policy_id,jobType, jobMethod, jobStatus,site_token, test);
		
		String job_id = response.then().extract().path("data.job_id");
		test.log(LogStatus.INFO, "The job id is "+job_id);
		
		test.log(LogStatus.INFO, "Post Jobs data to site under org ");
		response = gatewayServer.postJobData(job_id, "1", "success", "100", "0", "0", "0", "0", "0", "0","0","none","none","none","none","none","none","none","none",site_token,test);
		
		test.log(LogStatus.INFO, "Store the jobs for validation purpose");
		HashMap<String, Object> temp = spogServer.composejobsinfo_check(job_id, site_id, source_id, source_id, resource_name, rps_id, organization_id,
				destination_id,policy_id,start_time_ts, end_time_ts,jobType+"_"+jobMethod, jobStatus,destination_name,
				jobType+"_"+jobMethod+" from "+ resource_name + " to "+destination_name,"success","100",duration,"1",DestinationType.cloud_direct_volume.toString());
		
		jobsandjobdata.add(temp);
		
		
		test.log(LogStatus.INFO, "Get Jobs by job Id using direct org user token");
		spogServer.getjobsbyjobIdwithcheck(validToken, job_id, resource_name, site_id,SpogConstants.SUCCESS_GET_PUT_DELETE, jobsandjobdata, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		
		test.log(LogStatus.INFO, "Get Jobs by job Id using csr token");
		spogServer.getjobsbyjobIdwithcheck(csr_token, job_id, resource_name, site_id,SpogConstants.SUCCESS_GET_PUT_DELETE, jobsandjobdata, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		
		spogServer.getjobsbyjobIdwithcheck(csr_readonly_token, job_id, resource_name, site_id,SpogConstants.SUCCESS_GET_PUT_DELETE, jobsandjobdata, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		
		test.log(LogStatus.INFO, "Delete the source");
		response = spogServer.deleteSourcesById(validToken, source_id, test);
		
		policy4SPOGServer.deletePolicybyPolicyId(validToken, policy_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
	}
	
	
	@Test(dataProvider = "postjobandjobdata")
	public void getjobsbyjobid_invalid_missing_JWT(String organizationType,
													String organization_id,
													String validToken,
													String site_id,
													String site_token,
													String rps_id,
													String policy_id,
													String jobType,
													String jobMethod,
													String jobStatus
													) {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		String datastore_id;
		long start_time_ts;
		long end_time_ts;
		String resource_name = spogServer.ReturnRandom("Prasad")+"_";
		ArrayList<HashMap<String, Object>> jobsandjobdata = new ArrayList<>();
		
		spogServer.setToken(validToken);
		test.log(LogStatus.INFO, "Adding sources of type "+SourceType.machine +" to org "+organizationType);
		
		String source_id = spogServer.createSourceWithCheck(resource_name, SourceType.machine, SourceProduct.udp, organization_id, site_id,ProtectionStatus.unprotect, ConnectionStatus.online, "windows", "SQLSERVER", test);
		
		test.log(LogStatus.INFO, "Create a destination of type cloud dedupe");
		spogDestinationServer.setToken(validToken);
		String[] datacenters = spogDestinationServer.getDestionationDatacenterID();
		
		
		test.log(LogStatus.INFO, "Post the destination");
		String destination_name = RandomStringUtils.randomAlphanumeric(4)+"Prasad-test34";
		datastore_id = UUID.randomUUID().toString();
		/*datastore_id = spogDestinationServer.createDestinationWithCheck(UUID.randomUUID().toString(),organization_id, site_id, datacenters[0], DestinationType.cloud_direct_volume.toString(), DestinationStatus.running.toString(), 
				"20", "1", "21", "normal", RandomStringUtils.randomAlphanumeric(4)+"host-t", "7D", 
				"0", "0", "0", "0", "0", "0", "0", "0", 
				"0", "0", "0", "0", "0", "0", "0", "0", 
				"0", "0", "0", "0", destination_name, test);*/
		
		test.log(LogStatus.INFO, "Post Jobs to site under direct org");
		start_time_ts = System.currentTimeMillis()/1000L;
		end_time_ts = System.currentTimeMillis()/1000L+1;
		long duration = end_time_ts - start_time_ts;
		Response response = gatewayServer.postJob(System.currentTimeMillis(), System.currentTimeMillis(),organization_id,source_id, source_id, rps_id, datastore_id, 
				policy_id,jobType, jobMethod, jobStatus,site_token, test);
		
		String job_id = response.then().extract().path("data.job_id");
		test.log(LogStatus.INFO, "The job id is "+job_id);
		
		test.log(LogStatus.INFO, "Post Jobs data to site under org "+organizationType);
		response = gatewayServer.postJobData(job_id, "1", "success", "100", "0", "0", "0", "0", "0", "0","0","none","none","none","none","none","none","none","none",site_token,test);
		
		test.log(LogStatus.INFO, "Store the jobs for validation purpose");
		HashMap<String, Object> temp = spogServer.composejobsinfo_check(job_id, site_id, source_id, source_id, resource_name, rps_id, organization_id,
				datastore_id,policy_id,start_time_ts, end_time_ts,jobType+"_"+jobMethod, jobStatus,destination_name,
				jobType+"_"+jobMethod+" from "+ resource_name + " to "+destination_name,"success","100",duration,"1",DestinationType.cloud_direct_volume.toString());
		
		jobsandjobdata.add(temp);
		
		
		test.log(LogStatus.INFO, "Get Jobs by job Id using invalid JWT");
		spogServer.getjobsbyjobIdwithcheck(validToken+"J", job_id, resource_name, site_id,SpogConstants.NOT_LOGGED_IN, jobsandjobdata, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);
		
		test.log(LogStatus.INFO, "Get Jobs by job Id using missing JWT");
		spogServer.getjobsbyjobIdwithcheck("", job_id, resource_name, site_id,SpogConstants.NOT_LOGGED_IN, jobsandjobdata, SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);
		
		
		
		test.log(LogStatus.INFO, "Delete the source");
		response = spogServer.deleteSourcesById(validToken, source_id, test);
		spogServer.deleteSourcesWithCheck(response, SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);		
		//spogDestinationServer.deletedestinationbydestination_Id(datastore_id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		
				
	

	}
	
	@Test(dataProvider = "postjobandjobdata")
	public void getjobsbyjobid_invalidJobId(String organizationType,
											String organization_id,			
											String validToken,
											String site_id,
											String site_token,
											String rps_id,
											String policy_id,
											String jobType,
											String jobMethod,
											String jobStatus
														) {

		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		String datastore_id;
		long start_time_ts;
		long end_time_ts;
		String resource_name = spogServer.ReturnRandom("prasad")+"_";
		ArrayList<HashMap<String, Object>> jobsandjobdata = new ArrayList<>();


		test.log(LogStatus.INFO, "Adding sources of type "+SourceType.machine +" to org "+organizationType);
		spogServer.setToken(validToken);
		String source_id = spogServer.createSourceWithCheck(resource_name, SourceType.machine, SourceProduct.udp, organization_id, site_id,ProtectionStatus.unprotect, ConnectionStatus.online, "windows", "SQLSERVER", test);

		test.log(LogStatus.INFO, "Create a destination of type cloud direct");
		spogDestinationServer.setToken(validToken);
		String[] datacenters = spogDestinationServer.getDestionationDatacenterID();


		test.log(LogStatus.INFO, "Post the destination");
		String destination_name = RandomStringUtils.randomAlphanumeric(4)+"prasad-test34";
		datastore_id = UUID.randomUUID().toString();
		/*datastore_id = spogDestinationServer.createDestinationWithCheck(UUID.randomUUID().toString(),organization_id, site_id, datacenters[0], DestinationType.cloud_direct_volume.toString(), DestinationStatus.running.toString(), 
				"20", "1", "21", "normal", RandomStringUtils.randomAlphanumeric(4)+"host-t", "7D", 
				"0", "0", "0", "0", "0", "0", "0", "0", 
				"0", "0", "0", "0", "0", "0", "0", "0", 
				"0", "0", "0", "0", destination_name, test);*/

		test.log(LogStatus.INFO, "Post Jobs to site under org "+organizationType);
		start_time_ts = System.currentTimeMillis()/1000L;
		end_time_ts = System.currentTimeMillis()/1000L+1;
		long duration = end_time_ts - start_time_ts;
		Response response = gatewayServer.postJob(System.currentTimeMillis(), System.currentTimeMillis(),organization_id,source_id, source_id, rps_id, datastore_id, 
				policy_id,jobType, jobMethod, jobStatus,site_token, test);

		String job_id = response.then().extract().path("data.job_id");
		test.log(LogStatus.INFO, "The job id is "+job_id);

		test.log(LogStatus.INFO, "Post Jobs data to site under org ");
		response = gatewayServer.postJobData(job_id, "1", "success", "100", "0", "0", "0", "0", "0", "0","0","none","none","none","none","none","none","none","none",site_token,test);

		test.log(LogStatus.INFO, "Store the jobs for validation purpose");
		HashMap<String, Object> temp = spogServer.composejobsinfo_check(job_id, site_id, source_id, source_id, resource_name, rps_id, organization_id,
				datastore_id,policy_id,start_time_ts, end_time_ts,jobType+"_"+jobMethod, jobStatus,destination_name,
				jobType+"_"+jobMethod+" from "+ resource_name + " to "+destination_name,"success","100",duration,"1",DestinationType.cloud_direct_volume.toString());

		jobsandjobdata.add(temp);


		test.log(LogStatus.INFO, "Get Jobs by job Id using invalid JWT");
		spogServer.getjobsbyjobIdwithcheck(validToken, UUID.randomUUID().toString(), resource_name, site_id,SpogConstants.RESOURCE_NOT_EXIST, jobsandjobdata, SpogMessageCode.JOB_NOT_FOUND, test);

		spogServer.getjobsbyjobIdwithcheck(csr_readonly_token, UUID.randomUUID().toString(), resource_name, site_id,SpogConstants.RESOURCE_NOT_EXIST, jobsandjobdata, SpogMessageCode.JOB_NOT_FOUND, test);


		test.log(LogStatus.INFO, "Delete the source");
		response = spogServer.deleteSourcesById(validToken, source_id, test);
		spogServer.deleteSourcesWithCheck(response, SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);		
		//spogDestinationServer.deletedestinationbydestination_Id(datastore_id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);




	}

	@DataProvider(name = "postjobandjobdata_403")
	public final Object[][] postjobandjobdata_403() {
		return new Object[][] {
			{"direct",direct_organization_id,direct_user_validToken,direct_site_id,direct_site_token,msp_user_validToken,UUID.randomUUID().toString(),UUID.randomUUID().toString(),"backup", "full", "finished"},
			{"direct",direct_organization_id,direct_user_validToken,direct_site_id,direct_site_token,sub_orga_user_validToken,UUID.randomUUID().toString(),UUID.randomUUID().toString(),"backup", "full", "finished"},
			{"msp",msp_organization_id,msp_user_validToken,msp_site_id,msp_site_token,direct_user_validToken,UUID.randomUUID().toString(),UUID.randomUUID().toString(),"backup", "full", "finished"},
			{"msp",msp_organization_id,msp_user_validToken,msp_site_id,msp_site_token,suborga_site_token,UUID.randomUUID().toString(),UUID.randomUUID().toString(),"backup", "full", "finished"},
			{"msp",msp_organization_id,msp_user_validToken,msp_site_id,msp_site_token,mspb_user_validToken,UUID.randomUUID().toString(),UUID.randomUUID().toString(),"backup", "full", "finished"},
			{"suborg",sub_org_Id,sub_orga_user_validToken,suborga_site_id,suborga_site_token,direct_user_validToken,UUID.randomUUID().toString(),UUID.randomUUID().toString(),"backup", "full", "finished"},
			{"suborg",sub_org_Id,sub_orga_user_validToken,suborga_site_id,suborga_site_token,mspb_user_validToken,UUID.randomUUID().toString(),UUID.randomUUID().toString(),"backup", "full", "finished"},
			{"suborg",sub_org_Id,sub_orga_user_validToken,suborga_site_id,suborga_site_token,sub_orgb_user_validToken,UUID.randomUUID().toString(),UUID.randomUUID().toString(),"backup", "full", "finished"},
			{"mspaccountadmin",sub_org_Id,msp_account_admin_validToken,suborga_site_id,suborga_site_token,sub_orgb_user_validToken,UUID.randomUUID().toString(),UUID.randomUUID().toString(),"backup", "full", "finished"},

			
		};
	}
	
/*	@DataProvider(name = "postjobandjobdata_403")
	public final Object[][] postjobandjobdata_403() {
		return new Object[][] {
			{"direct",direct_organization_id,direct_user_validToken,direct_site_id,direct_site_token,msp_user_validToken,UUID.randomUUID().toString(),UUID.randomUUID().toString(),"backup", "full", "finished"},
			{"direct",direct_organization_id,direct_user_validToken,direct_site_id,direct_site_token,sub_orga_user_validToken,UUID.randomUUID().toString(),UUID.randomUUID().toString(),"backup", "full", "finished"},
			{"msp",msp_organization_id,msp_user_validToken,msp_site_id,msp_site_token,direct_user_validToken,UUID.randomUUID().toString(),UUID.randomUUID().toString(),"backup", "full", "finished"},
			{"msp",msp_organization_id,msp_user_validToken,msp_site_id,msp_site_token,suborga_site_token,UUID.randomUUID().toString(),UUID.randomUUID().toString(),"backup", "full", "finished"},
			{"msp",msp_organization_id,msp_user_validToken,msp_site_id,msp_site_token,mspb_user_validToken,UUID.randomUUID().toString(),UUID.randomUUID().toString(),"backup", "full", "finished"},
			{"suborg",sub_org_Id,sub_orga_user_validToken,suborga_site_id,suborga_site_token,direct_user_validToken,UUID.randomUUID().toString(),UUID.randomUUID().toString(),"backup", "full", "finished"},
			{"suborg",sub_org_Id,sub_orga_user_validToken,suborga_site_id,suborga_site_token,mspb_user_validToken,UUID.randomUUID().toString(),UUID.randomUUID().toString(),"backup", "full", "finished"},
			{"suborg",sub_org_Id,sub_orga_user_validToken,suborga_site_id,suborga_site_token,sub_orgb_user_validToken,UUID.randomUUID().toString(),UUID.randomUUID().toString(),"backup", "full", "finished"},
			{"mspaccountadmin",sub_org_Id,msp_account_admin_validToken,suborga_site_id,suborga_site_token,sub_orgb_user_validToken,UUID.randomUUID().toString(),UUID.randomUUID().toString(),"backup", "full", "finished"},
			
		};
	}*/
	
	
	@Test(dataProvider = "postjobandjobdata_403")
	public void getjobsbyjobid_insufficientpermissions(String organizationType,
														String organization_id,
														String validToken,
														String site_id,
														String site_token,
														String otherorg_token,
														String rps_id,
														String policy_id,
														String jobType,
														String jobMethod,
														String jobStatus
														) {

		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		String datastore_id;
		long start_time_ts;
		long end_time_ts;
		String resource_name = spogServer.ReturnRandom("Prasad")+"_";
		ArrayList<HashMap<String, Object>> jobsandjobdata = new ArrayList<>();

		spogServer.setToken(validToken);
		test.log(LogStatus.INFO, "Adding sources of type "+SourceType.machine +" to org "+organizationType);

		String source_id = spogServer.createSourceWithCheck(resource_name, SourceType.machine, SourceProduct.udp, organization_id, site_id,ProtectionStatus.unprotect, ConnectionStatus.online, "windows", "SQLSERVER", test);

		test.log(LogStatus.INFO, "Create a destination of type cloud direct");
		spogDestinationServer.setToken(validToken);
		String[] datacenters = spogDestinationServer.getDestionationDatacenterID();


		test.log(LogStatus.INFO, "Post the destination");
		String destination_name = RandomStringUtils.randomAlphanumeric(4)+"Prasad-tes";
		datastore_id = UUID.randomUUID().toString();
		/*datastore_id = spogDestinationServer.createDestinationWithCheck(UUID.randomUUID().toString(),organization_id, site_id, datacenters[0], DestinationType.cloud_direct_volume.toString(), DestinationStatus.running.toString(), 
				"20", "1", "21", "normal", RandomStringUtils.randomAlphanumeric(4)+"host-t", "7D", 
				"0", "0", "0", "0", "0", "0", "0", "0", 
				"0", "0", "0", "0", "0", "0", "0", "0", 
				"0", "0", "0", "0", destination_name, test);*/

		test.log(LogStatus.INFO, "Post Jobs to site under direct org");
		start_time_ts = System.currentTimeMillis()/1000L;
		end_time_ts = System.currentTimeMillis()/1000L+1;
		long duration = end_time_ts - start_time_ts;
		Response response = gatewayServer.postJob(System.currentTimeMillis(), System.currentTimeMillis(),organization_id,source_id, source_id, rps_id, datastore_id, 
				policy_id,jobType, jobMethod, jobStatus,site_token, test);

		String job_id = response.then().extract().path("data.job_id");
		test.log(LogStatus.INFO, "The job id is "+job_id);

		test.log(LogStatus.INFO, "Post Jobs data to site under org ");
		response = gatewayServer.postJobData(job_id, "1", "success", "100", "0", "0", "0", "0", "0", "0","0","none","none","none","none","none","none","none","none",site_token,test);

		test.log(LogStatus.INFO, "Store the jobs for validation purpose");
		HashMap<String, Object> temp = spogServer.composejobsinfo_check(job_id, site_id, source_id, source_id, resource_name, rps_id, organization_id,
				datastore_id,policy_id,start_time_ts, end_time_ts,jobType+"_"+jobMethod, jobStatus,destination_name,
				jobType+"_"+jobMethod+" from "+ resource_name + " to "+destination_name,"success","100",duration,"1",DestinationType.cloud_direct_volume.toString());

		jobsandjobdata.add(temp);


		test.log(LogStatus.INFO, "Get Jobs by job Id using other org JWT");
		spogServer.getjobsbyjobIdwithcheck(otherorg_token, job_id, resource_name, site_id,SpogConstants.INSUFFICIENT_PERMISSIONS, jobsandjobdata, SpogMessageCode.RESOURCE_PERMISSION_DENY, test);

		

		test.log(LogStatus.INFO, "Delete the source");
		response = spogServer.deleteSourcesById(validToken, source_id, test);
		spogServer.deleteSourcesWithCheck(response, SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);		
		//spogDestinationServer.deletedestinationbydestination_Id(datastore_id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);


	}

	@DataProvider(name = "post_job_data_enhancement")
	public final Object[][] postJobDataEnhancementParams() {
		return new Object[][] { 
			//different job method
				{ "backup", "unknown", "finished" }, 
				{ "backup", "full", "finished" }, 
				{ "backup", "incremental", "finished" },
				{ "backup", "resync", "finished" },

				//different job status
				{ "backup", "full", "active"},
				{ "backup", "incremental", "finished"},
				{ "backup", "resync", "canceled"},
				{ "backup", "full", "failed"},
				{ "backup", "incremental", "incomplete" },
				{ "backup", "resync", "idle" },
				{ "backup", "full", "waiting"},
				{ "backup", "incremental", "crash"},
				{ "backup", "resync", "license_failed"},
				{ "backup", "full", "backupjob_proc_exit"},
				{ "backup", "incremental", "skipped"},
				{ "backup", "resync", "stop"},
				{ "backup", "full", "missed"},				
				
				//different job type
				{"restore", "", "finished" },
				{"copy", "", "finished" },
				{"merge", "", "finished" },
				{"conversion", "", "finished" },
				{"vm_backup", "full", "finished" },
				{"vm_recovery", "", "finished" },
				{"vm_catalog_fs", "", "finished" },
				{"vm_restore_file_to_original", "", "finished" },
				{"vmware_vapp_backup", "full", "finished" },
				{"vmware_vapp_recovery", "", "finished" },
				{ "mount_recovery_point", "", "finished" },
				{"assure_recovery", "", "finished" },
				{"vm_merge", "", "finished" },
				{"catalog_fs", "", "finished" },
				{"catalog_app", "", "finished" },
				{ "catalog_grt", "", "finished" },
				{ "file_copy_backup", "", "finished" },
				{"file_copy_purge", "", "finished" },
				{ "file_copy_restore", "", "finished" },
				{ "file_copy_catalog_sync", "", "finished" },
				{"file_copy_source_delete", "", "finished" },
				{ "file_copy_delete", "", "finished" },
				{ "catalog_fs_ondemand", "", "finished" },
				{ "vm_catalog_fs_ondemand", "", "finished" },
				{ "rps_replicate", "", "finished" },
				{ "rps_replicate_in_bound", "", "finished" },
				{"rps_merge", "", "finished" },
				{ "rps_conversion", "", "finished" },
				{"bmr", "", "finished" },
				{ "rps_data_seeding", "", "finished" },
				{"rps_data_seeding_in", "", "finished" },
				{ "vm_recovery_hyperv", "", "finished" },
				{ "rps_purge_datastore", "", "finished" },
				{ "start_instant_vm", "", "finished" },
				{ "stop_instant_vm", "", "finished" },
				{ "assure_recovery", "", "finished" },
				{ "start_instant_vhd", "", "finished" },
				{ "stop_instant_vhd", "", "finished" },
				{ "archive_to_tape", "", "finished" },
				{ "linux_instant_vm", "", "finished" },
				{ "auto_protection_discovery_vm", "", "finished" },
				{ "udp_console_activity_log", "", "finished" },
				{ "udp_console_agent_service_down", "", "finished" },
				{ "office365_backup", "full", "finished" },
				{ "cifs_backup", "full", "finished" },
				{ "sharepoint_backup", "incremental", "finished" },
				{ "data_store", "", "finished" },
				};
	}
	
	
	@DataProvider(name = "postjobandjobdata_all")
	public final Object[][] postjobandjobdata_all() {
		return new Object[][] {
			{"direct",direct_organization_id,direct_user_validToken,direct_site_id,direct_site_token,UUID.randomUUID().toString(),UUID.randomUUID().toString(),"backup", "", "finished","1","","100"},
			/*{"msp",msp_organization_id,msp_user_validToken,msp_site_id,msp_site_token,UUID.randomUUID().toString(),UUID.randomUUID().toString(),"backup", "", "finished","1","","100"},*/
			{"suborg",sub_org_Id,sub_orga_user_validToken,suborga_site_id,suborga_site_token,UUID.randomUUID().toString(),UUID.randomUUID().toString(),"backup", "", "finished","1","","100"},
			{"suborgusingmsp",sub_org_Id,msp_user_validToken,suborga_site_id,suborga_site_token,UUID.randomUUID().toString(),UUID.randomUUID().toString(),"backup", "", "finished","1","","100"},
		};
	}
	
	
	@Test(dataProvider = "postjobandjobdata_all")
	public void getjobsbyjobid_differenttypesofjobstatus(String organizationType,
														String organization_id,
														String validToken,
														String site_id,
														String site_token,
														String rps_id,
														String policy_id,
														String jobType,
														String jobMethod,
														String jobStatus,
														String jobseq,
														String logseverity,
														String percentage_complete
														) {

		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		String datastore_id;
		long start_time_ts;
		long end_time_ts;
		int index=0;
		int indx_severity=0;
		String resource_name = spogServer.ReturnRandom("Prasad")+"_";
		//String job_types[] = new String[] {"backup","rps_replicate","rps_replicate_in_bound","rps_merge"};
		String job_methods[] = new String[] { "full","incremental","resync"};
		String log_severity[] = new String[] { "success","error","warning"};
		String[] job_status = new String[] {/*"active",*/"finished","canceled","failed","incomplete","idle","waiting","crash","license_failed","backupjob_proc_exit",/*"skipped","stop",*/"missed"};
		Response response = null;
		

		spogServer.setToken(validToken);
		test.log(LogStatus.INFO, "Adding sources of type "+SourceType.machine +" to org "+organizationType);
		String source_id = spogServer.createSourceWithCheck(resource_name, SourceType.machine, SourceProduct.cloud_direct, organization_id, site_id,ProtectionStatus.unprotect, ConnectionStatus.online, "windows", "SQLSERVER", test);
		
		/*test.log(LogStatus.INFO, "Create a cloud account of type cloud direct");
		String cloud_account_id = spogServer.createCloudAccountWithCheck("test", "tt", "tata", "cloud_direct", organization_id, RandomStringUtils.randomAlphanumeric(8), RandomStringUtils.randomAlphanumeric(8), test);
		*/
		test.log(LogStatus.INFO, "Create a destination of type cloud direct");
		spogDestinationServer.setToken(validToken);
		String[] datacenters = spogDestinationServer.getDestionationDatacenterID();


		test.log(LogStatus.INFO, "Post the destination");
		String destination_name = RandomStringUtils.randomAlphanumeric(4)+"Prasad-tes";
		spogServer.setToken(validToken);
		
		if(organizationType.contains("sub"))
		{
			spogDestinationServer.setToken(msp_user_validToken);
			
			source_id = spogServer.createSourceWithCheck(resource_name, SourceType.machine, SourceProduct.cloud_direct, organization_id, site_id,ProtectionStatus.unprotect,ConnectionStatus.online, "windows", "SQLSERVER", test);
				test.log(LogStatus.INFO, "post the destination");
				//For sub org clout account id is not given instead site_id is provided to create destination
				datastore_id = spogDestinationServer.createDestinationWithCheck(UUID.randomUUID().toString(),organization_id, site_id, datacenters[0], DestinationType.cloud_direct_volume.toString(), 
						DestinationStatus.running.toString(), "20", null,"normal", RandomStringUtils.randomAlphanumeric(4)+"host-t", "7D", "7 Days",
						"0", "0", "0", "0", "0", "0", "0", "0", 
						"0", "0", destination_name, test);
		} else
		{
			spogDestinationServer.setToken(validToken);
			source_id = spogServer.createSourceWithCheck(resource_name, SourceType.machine, SourceProduct.cloud_direct, organization_id, site_id,ProtectionStatus.unprotect,ConnectionStatus.online, "windows", "SQLSERVER", test);
			test.log(LogStatus.INFO, "post the destination");
			//For sub org clout account id is not given instead site_id is provided to create destination
			datastore_id = spogDestinationServer.createDestinationWithCheck(UUID.randomUUID().toString(),organization_id, site_id, datacenters[0], DestinationType.cloud_direct_volume.toString(), 
					DestinationStatus.running.toString(), "20", null,"normal", RandomStringUtils.randomAlphanumeric(4)+"host-t", "7D", "7 Days",
					"0", "0", "0", "0", "0", "0", "0", "0", 
					"0", "0", destination_name, test);
		}
		test.log(LogStatus.INFO, "Post Jobs to site under direct org");
		
		
		
		for(int i=0;i<job_status.length;i++) {
			percentage_complete="100";
			ArrayList<HashMap<String, Object>> jobsandjobdata = new ArrayList<>();
			start_time_ts = System.currentTimeMillis()/1000L;
			end_time_ts = System.currentTimeMillis()/1000L+1;
			long duration = end_time_ts - start_time_ts;
			if(jobType=="backup") {
				
				index = gen_random_index(job_methods);
				jobMethod = job_methods[index];
				
			}
			indx_severity = gen_random_index(log_severity);
			logseverity = log_severity[indx_severity];
			
			if(job_status[i]=="active") {
				end_time_ts=0;
				percentage_complete="10";
			}
			if(job_status[i]=="idle"||job_status[i]=="waiting") {
				//duration =0;
				percentage_complete="0";
			}
			response = gatewayServer.postJob(start_time_ts, end_time_ts,organization_id,source_id, source_id, rps_id, datastore_id, 
					policy_id,jobType, jobMethod, job_status[i],site_token, test);
			
			String job_id = response.then().extract().path("data.job_id");
			test.log(LogStatus.INFO, "The job id is "+job_id);
			test.log(LogStatus.INFO, "Post Jobs data to site under org ");
			//response = gatewayServer.postJobData(job_id, Integer.toString(i+1) , logseverity, percentage_complete, "0", "0", "0", "0", "0", site_token,test);
			response = gatewayServer.postJobData(job_id, Integer.toString(i+1),logseverity, percentage_complete, "0", "0", "0", "0", "0", "0","0","none","none","none","none","none","none","none","none",site_token,test);
			test.log(LogStatus.INFO, "Store the jobs for validation purpose");
			
			if(jobMethod=="resync") jobMethod="verified";
			HashMap<String, Object> temp = spogServer.composejobsinfo_check(job_id, site_id, source_id, source_id, resource_name, rps_id, organization_id,
					datastore_id,policy_id,start_time_ts, end_time_ts,jobType+"_"+jobMethod, job_status[i],destination_name,
					jobType+"_"+jobMethod+" from "+ resource_name + " to "+destination_name,logseverity,percentage_complete,duration,Integer.toString(i+1),DestinationType.cloud_direct_volume.toString());

			jobsandjobdata.add(temp);


			test.log(LogStatus.INFO, "Get Jobs by job Id using other org JWT");
			spogServer.getjobsbyjobIdwithcheck(validToken, job_id, resource_name, site_id,SpogConstants.SUCCESS_GET_PUT_DELETE, jobsandjobdata, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			spogServer.getjobsbyjobIdwithcheck(csr_readonly_token, job_id, resource_name, site_id,SpogConstants.SUCCESS_GET_PUT_DELETE, jobsandjobdata, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			
		}
		

		test.log(LogStatus.INFO, "Delete the source");
		response = spogServer.deleteSourcesById(validToken, source_id, test);
		spogServer.deleteSourcesWithCheck(response, SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);		
		//spogDestinationServer.deletedestinationbydestination_Id(datastore_id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);


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
/*	@AfterTest
	public void aftertest() {
		test.log(LogStatus.INFO, "The total test cases passed are "+count1.getpassedcount());
		test.log(LogStatus.INFO, "the total test cases failed are "+count1.getfailedcount());

		rep.flush();

	}
	@AfterClass
	public void updatebd() {
		test.log(LogStatus.INFO, "Performing the operations to delete the user and orginzation by logging in as csr admin");	
		test.log(LogStatus.INFO, "Login in as csr admin to delete the organization");
		// spogServer.DeleteUserById(user_id, test);
		spogDestinationServer.setToken(csr_token);
		spogDestinationServer.recycleCloudVolumesAndDelOrg(direct_organization_id, test);
		spogDestinationServer.recycleCloudVolumesAndDelOrg(msp_organization_id, test);
		spogDestinationServer.recycleCloudVolumesAndDelOrg(sub_org_Id, test);
		spogDestinationServer.recycleCloudVolumesAndDelOrg(sub_org_Id_1, test);
		spogDestinationServer.recycleCloudVolumesAndDelOrg(msp_organization_id_b, test);
		try {
			if(count1.getfailedcount()>0) {
				Nooftest=count1.getpassedcount()+count1.getfailedcount()+count1.getskippedcount();
				bqdb1.updateTable(BQame, runningMachine, this.buildVersion, String.valueOf(Nooftest), Integer.toString(count1.getpassedcount()), Integer.toString(count1.getfailedcount()), String.valueOf(count1.getskippedcount()), count1.getcreationtime(), "Failed");
			}else {
				Nooftest=count1.getpassedcount()+count1.getfailedcount()+count1.getskippedcount();
				bqdb1.updateTable(BQame, runningMachine, this.buildVersion, String.valueOf(Nooftest), Integer.toString(count1.getpassedcount()), Integer.toString(count1.getfailedcount()), String.valueOf(count1.getskippedcount()), count1.getcreationtime(), "Passed");
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	*/
	
	/******************************************************************RandomFunction******************************************************************************/
	public int gen_random_index(String[] job_status) {
		Random generator = new Random();
		int randomindx = generator.nextInt(job_status.length);
		
		return randomindx;
	}
}
