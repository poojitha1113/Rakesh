package api.jobs;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
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
import org.testng.asserts.SoftAssert;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.ConnectionStatus;
import Constants.DestinationStatus;
import Constants.DestinationType;
import Constants.JobStatus;
import Constants.LogSeverityType;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;

public class GetJobsTest extends base.prepare.Is4Org {
	
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private SPOGDestinationServer spogDestinationServer;
	private UserSpogServer userSpogServer;
	//public int Nooftest;
	private ExtentTest test;
	private String common_password = "Mclaren@2020";
	private String csrAdminUserName;
	private String csrAdminPassword;
	private String csr_token;
	private String csr_readonly_email;
	private String csr_readonly_token;
	private String csr_readonly_user_id;
	
	private String prefix_direct = "spog_rakesh_direct";
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
	
	private String prefix_direct_1 = "spog_rakesh_direct_1";
	private String postfix_email_1 = "@arcserve.com";
	private String direct_org_name_1 = prefix_direct_1 + "_org";
	private String direct_org_email_1 = direct_org_name_1 + postfix_email_1;
	private String direct_org_first_name_1 = direct_org_name_1 + "_first_name";
	private String direct_org_last_name_1 = direct_org_name_1 + "_last_name";
	private String direct_user_name_1 = prefix_direct_1 + "_admin";

	private String prefix_msp = "spog_rakesh_msp";
	private String msp_org_name = prefix_msp + "_org";
	private String msp_org_email = msp_org_name + postfix_email;
	private String msp_org_first_name = msp_org_name + "_first_name";
	private String msp_org_last_name = msp_org_name + "_last_name";
	private String msp_user_name = prefix_msp + "_admin";
	private String msp_user_name_email = prefix_msp + "_admin" + postfix_email;
	private String msp_user_first_name = msp_user_name + "_first_name";
	private String msp_user_last_name = msp_user_name + "_last_name";
	private String msp_user_validToken;
	
	private String prefix_msp_b = "spog_rakesh_msp_b";
	private String msp_org_name_b = prefix_msp_b + "_org";
	private String msp_org_email_b = msp_org_name_b + postfix_email;
	private String msp_org_first_name_b = msp_org_name_b + "_first_name";
	private String msp_org_last_name_b = msp_org_name_b + "_last_name";
	
	
	private String initial_sub_org_name_a = "spog_qa_sub_rakesh_a";
	private String initial_sub_email_a = "spog_qa_sub_rakesh_a@arcserve.com";
	private String initial_sub_first_name_a = "spog_qa_sub_rakesh_a";
	private String initial_sub_last_name_a = "spog_qa_sub_chalamala_a";
	private String sub_org_user_validToken;
	
	
	private String initial_sub_org_name_b = "spog_qa_sub_rakesh_b";
	private String initial_sub_email_b = "spog_qa_sub_rakesh_b@arcserve.com";
	private String initial_sub_first_name_b = "spog_qa_sub_rakesh_b";
	private String initial_sub_last_name_b = "spog_qa_sub_chalamala_b";
	
	private String prefix_msp_account_admin = "spog_rakesh_msp_account";
	private String msp_account_admin_email = prefix_msp_account_admin+postfix_email;
	private String msp_account_admin_email_b = prefix_msp_account_admin+postfix_email;
	private String msp_account_admin_first_name = prefix_msp_account_admin+"_first_name";
	private String msp_account_admin_last_name = prefix_msp_account_admin+"_last_name";
	private String msp_account_admin_validToken;
	String msp_account_admin_id;
	
	private String site_version="1.0.0";
	private String gateway_hostname="rakesh";
	//used for test case count like passed,failed,remaining cases
	int passedcases=0;
	int failedcases=0;
	int skippedcases=0;
	String buildnumber=null;
	
    /*private ExtentReports rep;
    private SQLServerDb bqdb1;
    public int Nooftest;
    private long creationTime;
    private String BQName=null;
    private String runningMachine;
    private testcasescount count1;
    private String buildVersion;*/
	
	String direct_organization_id;
	String direct_organization_id_1;
	String msp_organization_id;
	String msp_organization_id_b;
	String sub_org_Id;
	String sub_org_Id_1;
	
	private String[] direct_sites;
	private String[] msp_sites;
	private String[] suborg_sites;
	
	private String[] direct_sources = null;
	private String[] msp_sources = null;
	private String[] suborg_sources = null;
	
	private String[] direct_rps;
	private String[] msp_rps;
	private String[] suborg_rps;
	
	private String[] direct_serverid;
	private String[] msp_serverid;
	private String[] suborg_serverid;
	
	private String[] direct_destinationid;
	private String[] msp_destinationid;
	private String[] suborg_destinationid;
	private String[] suborg_policyid;
	
	ArrayList<HashMap<String,Object>> direct_response = new ArrayList<>();
	ArrayList<HashMap<String,Object>> msp_response = new ArrayList<>();
	ArrayList<HashMap<String,Object>> suborg_response = new ArrayList<>();
	ArrayList<HashMap<String,Object>> msp_suborg_response = new ArrayList<>();
	
	private String  org_model_prefix=this.getClass().getSimpleName();
		
	@BeforeClass
	@Parameters({ "baseURI", "port","csrAdminUserName", "csrAdminPassword","csrReadOnlyAdminName","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword,String csrReadOnlyUser, String logFolder,String runningMachine,String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		rep = ExtentManager.getInstance(this.getClass().getSimpleName(), logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Kanamarlapudi, Chandra Kanth";
		
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
		
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		//Create a msp org
		test.log(LogStatus.INFO, "create a msp org");
		this.msp_org_email = prefix+this.msp_org_email;
		msp_organization_id = spogServer.CreateOrganizationWithCheck(prefix+msp_org_name+org_model_prefix, SpogConstants.MSP_ORG, this.msp_org_email, common_password, prefix+msp_org_first_name, prefix+msp_org_last_name,test);
		
		test.log(LogStatus.INFO, "create another msp org");
		this.msp_org_email_b = prefix+this.msp_org_email_b;
		msp_organization_id_b = spogServer.CreateOrganizationWithCheck(prefix+msp_org_name_b+org_model_prefix, SpogConstants.MSP_ORG, this.msp_org_email_b, common_password, prefix+msp_org_first_name_b, prefix+msp_org_last_name_b,test);
		
		//Create a suborg under MSP org and a user for sub org
		spogServer.userLogin(this.msp_org_email, common_password);
		msp_user_validToken = spogServer.getJWTToken();
		sub_org_Id = spogServer.createAccountWithCheck(msp_organization_id, prefix+initial_sub_org_name_a,
				msp_organization_id);
		sub_org_Id_1 = spogServer.createAccountWithCheck(msp_organization_id, prefix+initial_sub_org_name_b,
				msp_organization_id);
		
		this.initial_sub_email_a = prefix + initial_sub_email_a;
		spogServer.createUserAndCheck(this.initial_sub_email_a, common_password,
				prefix + this.initial_sub_first_name_a, prefix + this.initial_sub_last_name_a,
				SpogConstants.DIRECT_ADMIN, sub_org_Id, test);
		
		this.initial_sub_email_b = prefix + initial_sub_email_b;
		spogServer.createUserAndCheck(this.initial_sub_email_b, common_password,
				prefix + this.initial_sub_first_name_b, prefix + this.initial_sub_last_name_b,
				SpogConstants.DIRECT_ADMIN, sub_org_Id_1, test);
		
		//Create msp_account_admin
		test.log(LogStatus.INFO, "Create a msp account admin under msp org");
		msp_account_admin_email = prefix+msp_account_admin_email;
		spogServer.setToken(msp_user_validToken);
		msp_account_admin_id = spogServer.createUserAndCheck(msp_account_admin_email, common_password, msp_account_admin_first_name, msp_account_admin_last_name, "msp_account_admin", msp_organization_id, test);
		spogServer.userLogin(msp_account_admin_email, common_password);
		this.msp_account_admin_validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The token is :"+ msp_account_admin_validToken );
		userSpogServer.assignMspAccountAdmins(msp_organization_id, sub_org_Id, new String[] {msp_account_admin_id}, msp_user_validToken);
		
		//login in as csr admin
		test.log(LogStatus.INFO, "Logging with csrAdmin");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword,test);
		csr_token = spogServer.getJWTToken();
		
		//Create a direct org
		
		this.direct_org_email = prefix+this.direct_org_email;
		this.direct_org_email_1 = prefix+this.direct_org_email_1;
		direct_organization_id = spogServer.CreateOrganizationWithCheck(prefix+direct_org_name+org_model_prefix, SpogConstants.DIRECT_ORG, this.direct_org_email, common_password, prefix+direct_org_first_name, prefix+direct_org_last_name,test);
		direct_organization_id_1 = spogServer.CreateOrganizationWithCheck(prefix+direct_org_name_1+org_model_prefix, SpogConstants.DIRECT_ORG, this.direct_org_email_1, common_password, prefix+direct_org_first_name_1, prefix+direct_org_last_name_1,test);
				
		test.log(LogStatus.INFO, "csr_readonly user login");
		spogServer.userLogin(csr_readonly_email, common_password);
		csr_readonly_token = spogServer.getJWTToken();
		csr_readonly_user_id = spogServer.GetLoggedinUser_UserID();
	}

	
	@DataProvider(name = "postjobandjobdata",parallel=false)
	public final Object[][] postjobandjobdata() {
		return new Object[][] {
			{"direct",direct_org_email,common_password,direct_organization_id,SourceType.office_365,SourceProduct.udp,ProtectionStatus.unprotect,ConnectionStatus.online,UUID.randomUUID().toString(),
				UUID.randomUUID().toString(),UUID.randomUUID().toString(),"testJobMessage",new String[] { "node", "agent"},3,2,new String[] {"backup"}, "full", 
				new String[] {"finished",JobStatus.canceled.toString()},new String[] {LogSeverityType.information.toString(),LogSeverityType.warning.toString(),LogSeverityType.error.toString()}},
			/*{"msp",msp_org_email,common_password,msp_organization_id,SourceType.office_365,SourceProduct.udp,ProtectionStatus.unprotect,ConnectionStatus.online,UUID.randomUUID().toString(),
				UUID.randomUUID().toString(),UUID.randomUUID().toString(),"testJobMessage",new String[] { "node", "agent"},2,2,new String[] {"backup"}, "full", 
				new String[] {"finished",JobStatus.canceled.toString()},new String[] {LogSeverityType.information.toString(),LogSeverityType.warning.toString(),LogSeverityType.error.toString()}},
			*/{"suborg",initial_sub_email_a,common_password,sub_org_Id,SourceType.office_365,SourceProduct.udp,ProtectionStatus.unprotect,ConnectionStatus.online,UUID.randomUUID().toString(),
				UUID.randomUUID().toString(),UUID.randomUUID().toString(),"testJobMessage",new String[] { "node", "agent"},2,2,new String[] {"backup"}, "full",
				new String[] {"finished",JobStatus.canceled.toString()},new String[] {LogSeverityType.information.toString(),LogSeverityType.warning.toString(),LogSeverityType.error.toString()}},
			{"msp_suborg",msp_org_email,common_password,sub_org_Id,SourceType.office_365,SourceProduct.udp,ProtectionStatus.unprotect,ConnectionStatus.online,UUID.randomUUID().toString(),
				UUID.randomUUID().toString(),UUID.randomUUID().toString(),"testJobMessage",new String[] { "node", "agent"},3,2,new String[] {"backup"}, "full", 
				new String[] {"finished",JobStatus.canceled.toString()},new String[] {LogSeverityType.information.toString(),LogSeverityType.warning.toString(),LogSeverityType.error.toString()}}
		};
	}
	
	@Test(dataProvider = "postjobandjobdata",groups= {"sanity"},threadPoolSize = 2)
	public void getjobs(String organizationType,
										 String userEmail,
										 String password,
										 String organization_id,
										 SourceType source_type,
										 SourceProduct source_product,
										 ProtectionStatus protectionstatus,
										 ConnectionStatus conn_status,
										 String rps_id1,
										 String datastore_id1,
										 String policy_id1,
										 String messageId ,
										 String[] messagedata,
										 int noofjobs,
										 int noofsites,
										 String[] jobType,
										 String jobMethod,
										 String[] jobStatus,
										 String[] logSeverity
										) {
		long start_time_ts;
		long end_time_ts;
		long duration;
		
		int time_increment = 0;
		
		Response response;
		String additionalURL;
		
		String server_id;
		String resource_id;
		String rps_id;
		String datastore_id;
		String policy_id;
		
		String[] siteids = new String[noofsites];
		String[] sources = new String[noofjobs*noofsites];
		String[] sourcenames = new String[noofjobs*noofsites];
		String[] rpsids = new String[noofsites];
		String[] serverids = new String[noofsites];
		String[] policyids = new String[noofsites];
		String[] datastoreids = new String[noofsites];
		
		long current = ZonedDateTime.now().toInstant().toEpochMilli()/1000L;
		long yesterday = ZonedDateTime.now().minusDays(1).toInstant().toEpochMilli()/1000L;
		long tomorrow = ZonedDateTime.now().plusDays(1).toInstant().toEpochMilli()/1000L;
		
		long two_months = ZonedDateTime.now().minusMonths(2).toInstant().toEpochMilli();
		ArrayList<HashMap<String,Object>> expected_response = new ArrayList<>();
		HashMap<String,Object> temp = new HashMap<>() ;
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		String resource_name ;
		String destination_name;
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		
		test.log(LogStatus.INFO,"Login with the user "+userEmail);
		spogServer.userLogin(userEmail,password);
		
		test.log(LogStatus.INFO,"Getting the JWTToken for the Logged in user");
		String validToken = spogServer.getJWTToken();
		//direct_user_validToken=validToken;
		test.log(LogStatus.INFO,"The token is :"+ validToken );
		
		test.log(LogStatus.INFO, "Get the logged in user id ");
		String user_id = spogServer.GetLoggedinUser_UserID();
		test.log(LogStatus.INFO, "The logged in user id is "+user_id);
		int sourcecount=0;
		String cloud_account_id = null;
		
		if(!organizationType.equals("msp_suborg")) {
			for(int j=0;j<noofsites;j++) {
				
				test.log(LogStatus.INFO, "Create a site/register/login to the site");
				String site_id = gatewayServer.createsite_register_login(organization_id, validToken, user_id, "ts", "1.0.0", spogServer, test);
				String site_token = gatewayServer.getJWTToken();
				siteids[j]=site_id;
				rps_id = UUID.randomUUID().toString();
				rpsids[j] = rps_id;
				policy_id = UUID.randomUUID().toString();
				policyids[j] = policy_id;
				datastore_id = UUID.randomUUID().toString();
				datastoreids[j] = datastore_id;
				test.log(LogStatus.INFO, "Adding sources of type "+source_type +" to "+organizationType+" org");
				server_id = spogServer.createSourceWithCheck(spogServer.ReturnRandom("machinek")+"_site", SourceType.machine, source_product, organization_id, site_id,protectionstatus, conn_status, "windows", "SQLSERVER", test);
				serverids[j] = server_id;
				
				test.log(LogStatus.INFO, "Create a destination of type cloud direct volume");
				spogDestinationServer.setToken(validToken);
				String[] datacenters = spogDestinationServer.getDestionationDatacenterID();
				
				if(organizationType.equals("direct")||organizationType.equals("msp")) {
				test.log(LogStatus.INFO, "Create a cloud account of type cloud direct");
				//cloud_account_id = spogServer.createCloudAccountWithCheck("test", "tt", "tata", "cloud_direct", organization_id, "SKUTESTDATA_1_0_0_0_"+prefix, "SKUTESTDATA_1_0_0_0_"+prefix, datacenters[0],test);
				cloud_account_id = UUID.randomUUID().toString();
				}
				
				test.log(LogStatus.INFO, "Post the destination");
				destination_name = RandomStringUtils.randomAlphanumeric(4)+"rakesh-test34";
				if (organizationType.contains("suborg")) {
					
//					spogServer.userLogin(msp_org_email, common_password);
//					response = spogDestinationServer.getDestinations(validToken, "", test);
//					datastore_id = response.then().extract().path("data[0].destination_id");
					spogDestinationServer.setToken(msp_user_validToken);
					datastore_id = spogDestinationServer.createDestinationWithCheck(UUID.randomUUID().toString(),organization_id, site_id, datacenters[1], DestinationType.cloud_direct_volume.toString(), DestinationStatus.running.toString(), 
							"20","","normal",RandomStringUtils.randomAlphanumeric(4)+"host-t", "2M", "2M", 
				    		"0","0", "31", "0", "2", "0", "5", "true", "1", "true",destination_name, test);
					spogServer.userLogin(userEmail,password);
				}else {
//					response = spogDestinationServer.getDestinations(validToken, "", test);
//					datastore_id = response.then().extract().path("data[0].destination_id");
					
					datastore_id = spogDestinationServer.createDestinationWithCheck(UUID.randomUUID().toString(),organization_id, site_id, datacenters[1], DestinationType.cloud_direct_volume.toString(), DestinationStatus.running.toString(), 
							"20","","normal",RandomStringUtils.randomAlphanumeric(4)+"host-t", "2M", "2M", 
				    		"0","0", "31", "0", "2", "0", "5", "true", "1", "true",destination_name, test);
				}
				datastoreids[j] = datastore_id;
				
				for(int i=0;i<noofjobs;i++) {
					time_increment = time_increment + 1;
					resource_name= spogServer.ReturnRandom("office365")+i;
					sourcenames[sourcecount] = resource_name;
					test.log(LogStatus.INFO, "Adding sources of type "+source_type +" to "+organizationType+" org");
					String source_id = spogServer.createSourceWithCheck(resource_name, source_type, source_product, organization_id, site_id,protectionstatus, conn_status, "windows", "SQLSERVER", test);
					sources[sourcecount]=source_id;
					
					test.log(LogStatus.INFO, "Post Jobs of type " +jobType[0] +" and job method is " +jobMethod+" where status is "+jobStatus[0] +" to site to "+organizationType+ " org");
					start_time_ts = System.currentTimeMillis()/1000L+time_increment;
					//start_time_ts =  ((start_time_ts/1000) % 60);
					end_time_ts = System.currentTimeMillis()/1000L+2+time_increment;
					duration = end_time_ts-start_time_ts;
					
					String job_id = gatewayServer.postJobWithCheck(start_time_ts, end_time_ts, organization_id, server_id, source_id, rps_id, datastore_id, policy_id, jobType[0], jobMethod, jobStatus[0], site_token, test);
					
					
					/*response = gatewayServer.postJob(start_time_ts, end_time_ts,organization_id,server_id, source_id, rps_id, datastore_id, 
													 policy_id,jobType[0], jobMethod, jobStatus[0],site_token, test);
					String job_id = response.then().extract().path("data.job_id");*/
					test.log(LogStatus.INFO, "The job id is "+job_id);
					
					test.log(LogStatus.INFO, "Post Jobs data to site under org "+organizationType);
					//response = gatewayServer.postJobData(job_id, "1", logSeverity[0], "100", "100", "100", "0", "100", "0", site_token,test);
					response = gatewayServer.postJobData(job_id, "1", logSeverity[0], "100", "100", "100", "0", "100", "0", "0","0","none","none","none","none","none","none","none","none",site_token,test);
					test.log(LogStatus.INFO, "Store the jobs for validation purpose");
					temp = spogServer.composejobsinfo_check(job_id, site_id, server_id, source_id, resource_name, rps_id, organization_id,
							datastore_id,policy_id,start_time_ts, end_time_ts,jobType[0]+"_"+jobMethod,jobStatus[0],destination_name,
							jobType[0]+"_"+jobMethod+" from "+ resource_name + " to "+destination_name,logSeverity[0],"100",duration,"1",DestinationType.cloud_direct_volume.toString());
					expected_response.add(temp);
					
					test.log(LogStatus.INFO, "Post Jobs of type " +jobType[0] +" and job method is " +jobMethod+" where status is "+jobStatus[1] +" to site to "+organizationType+ " org");
					
					start_time_ts = start_time_ts+1;
					end_time_ts = end_time_ts+2;
					duration = end_time_ts-start_time_ts;
					job_id = gatewayServer.postJobWithCheck(start_time_ts, end_time_ts,organization_id,server_id, source_id, rps_id, datastore_id, 
													 policy_id,jobType[0], "incremental",jobStatus[1] ,site_token, test);
//					job_id = response.then().extract().path("data.job_id");
					test.log(LogStatus.INFO, "The job id is "+job_id);
					
					test.log(LogStatus.INFO, "Post Jobs data to site under org "+organizationType);
					//response = gatewayServer.postJobData(job_id, "1", logSeverity[1], "100", "0", "0", "0", "0", "0", site_token,test);
					response = gatewayServer.postJobData(job_id, "1", logSeverity[1], "100", "0", "0", "0", "0", "0", "0","0","none","none","none","none","none","none","none","none",site_token,test);
					test.log(LogStatus.INFO, "Store the jobs for validation purpose");
					temp = spogServer.composejobsinfo_check(job_id, site_id, server_id, source_id, resource_name, rps_id, organization_id,
							datastore_id,policy_id,start_time_ts, end_time_ts,jobType[0]+"_"+"incremental", jobStatus[1],destination_name,
							jobType[0]+"_"+"incremental"+" from "+ resource_name + " to "+destination_name,logSeverity[1],"100",duration,"1",DestinationType.cloud_direct_volume.toString());
					expected_response.add(temp);
					
					
					test.log(LogStatus.INFO, "Post Jobs of type " +jobType[0] +" and job method is " +jobMethod+" where status is "+jobStatus[1] +" to site to "+organizationType+ " org");
					
					start_time_ts = start_time_ts+1;
					end_time_ts = end_time_ts+2;
					duration = end_time_ts-start_time_ts;
					job_id = gatewayServer.postJobWithCheck(start_time_ts, end_time_ts,organization_id,server_id, source_id, rps_id, datastore_id, 
													 policy_id,jobType[0], "resync",jobStatus[1] ,site_token, test);
//					job_id = response.then().extract().path("data.job_id");
					test.log(LogStatus.INFO, "The job id is "+job_id);
					
					test.log(LogStatus.INFO, "Post Jobs data to site under org "+organizationType);
					//response = gatewayServer.postJobData(job_id, "1", logSeverity[2], "100", "0", "0", "0", "0", "0", site_token,test);
					response = gatewayServer.postJobData(job_id, "1", logSeverity[2], "100", "0", "0", "0", "0", "0", "0","0","none","none","none","none","none","none","none","none",site_token,test);
					
					test.log(LogStatus.INFO, "Store the jobs for validation purpose");
					temp = spogServer.composejobsinfo_check(job_id, site_id, server_id, source_id, resource_name, rps_id, organization_id,datastore_id,policy_id,start_time_ts, 
							end_time_ts,jobType[0]+"_"+"verified", jobStatus[1],destination_name,jobType[0]+"_"+"verified"+" from "+ resource_name + " to "+destination_name,
							logSeverity[2],"100",duration,"1",DestinationType.cloud_direct_volume.toString());
					expected_response.add(temp);
					sourcecount++;
				}
				
			}
		}
		
		if(organizationType.equals("direct")) {
			direct_sites = siteids;
			direct_response = expected_response;
			direct_sources = sources;
			direct_serverid = serverids;
			direct_rps = rpsids;
		}
		if(organizationType.equals("msp")) {
			msp_sites = siteids;
			msp_response = expected_response;
			msp_sources = sources;
			msp_serverid = serverids;
			msp_rps = rpsids;
		}
		if(organizationType.equals("suborg")) {
			suborg_sites = siteids;
			suborg_response = expected_response;
			suborg_sources = sources;
			suborg_serverid = serverids;
			suborg_rps = rpsids;
			suborg_destinationid = datastoreids;
			suborg_policyid = policyids;
		}
		if(organizationType.equals("msp_suborg")) {
			msp_suborg_response.addAll(msp_response);
			msp_suborg_response.addAll(suborg_response);
			expected_response.addAll(msp_suborg_response);
			
		}
		
		//Add a wait time of 20seconds, or else the get jobs will fail
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(!organizationType.equals("msp_suborg")) {
			test.log(LogStatus.INFO, "Get all the jobs in the org: "+organizationType);
			response = spogServer.getJobs(validToken, "", test);
			test.log(LogStatus.INFO, "Validate the reponse of get jobs");
			spogServer.checkGetJobs(response, 1, 30, "", "", expected_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,  test);
		}
		
		
		if(organizationType.equals("msp_suborg")) {
			test.log(LogStatus.INFO, "Get the jobs by organization id in ascending order: "+organization_id +"where page is:2 and page_size is: 2 using msp token");
			additionalURL = spogServer.PrepareURL("organization_id;=;"+organization_id, "start_time_ts;asc", 2, 2, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, 2, 2, "create_ts;asc","organization_id,="+organization_id,suborg_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			//csr
			test.log(LogStatus.INFO, "Get the jobs by organization id in ascending order: "+organization_id +"where page is:2 and page_size is: 2 using csr token");
			additionalURL = spogServer.PrepareURL("organization_id;=;"+organization_id, "start_time_ts;asc", 2, 2, test);
			response = spogServer.getJobs(csr_token, additionalURL, test);
			spogServer.checkGetJobs(response, 2, 2, "create_ts;asc","organization_id,="+organization_id,suborg_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			//csr_readonly
			test.log(LogStatus.INFO, "Get the jobs by organization id in ascending order: "+organization_id +"where page is:2 and page_size is: 2 using csr_readonly token");
			additionalURL = spogServer.PrepareURL("organization_id;=;"+organization_id, "start_time_ts;asc", 2, 2, test);
			response = spogServer.getJobs(csr_readonly_token, additionalURL, test);
			spogServer.checkGetJobs(response, 2, 2, "create_ts;asc","organization_id,="+organization_id,suborg_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			test.log(LogStatus.INFO, "Get the jobs by organization id in descending order: "+organization_id +"where page is:-1 and page_size is: 2 using msp token");
			additionalURL = spogServer.PrepareURL("organization_id;=;"+organization_id, "start_time_ts;desc", -1, 2, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, -1, 2, "create_ts;desc","organization_id,="+organization_id,suborg_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			test.log(LogStatus.INFO, "Get the jobs in all sites in descending order: "+suborg_sites[0] +"where page is:3 and page_size is: 1 using msp token");
			additionalURL = spogServer.PrepareURL("site_id;in;"+suborg_sites[0]+"|"+suborg_sites[1], "start_time_ts;desc", 3, 1, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, 3, 1, "create_ts;desc","site_id;in;"+suborg_sites[0]+"|"+suborg_sites[1],suborg_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			//csr
			test.log(LogStatus.INFO, "Get the jobs in all sites in descending order: "+suborg_sites[0] +"where page is:3 and page_size is: 1 using csr token");
			additionalURL = spogServer.PrepareURL("site_id;in;"+suborg_sites[0]+"|"+suborg_sites[1], "start_time_ts;desc", 3, 1, test);
			response = spogServer.getJobs(csr_token, additionalURL, test);
			spogServer.checkGetJobs(response, 3, 1, "create_ts;desc","site_id;in;"+suborg_sites[0]+"|"+suborg_sites[1],suborg_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			//csr_readonly
			test.log(LogStatus.INFO, "Get the jobs in all sites in descending order: "+suborg_sites[0] +"where page is:3 and page_size is: 1 using csr_readonly token");
			additionalURL = spogServer.PrepareURL("site_id;in;"+suborg_sites[0]+"|"+suborg_sites[1], "start_time_ts;desc", 3, 1, test);
			response = spogServer.getJobs(csr_readonly_token, additionalURL, test);
			spogServer.checkGetJobs(response, 3, 1, "create_ts;desc","site_id;in;"+suborg_sites[0]+"|"+suborg_sites[1],suborg_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			test.log(LogStatus.INFO, "Get the jobs by site id in descinding order: "+suborg_sites[0] +"where page is:3 and page_size is: 1 using msp token");
			additionalURL = spogServer.PrepareURL("site_id;=;"+suborg_sites[0], "start_time_ts;desc", 3, 1, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, 3, 1, "create_ts;desc","site_id;=;"+suborg_sites[0],suborg_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			//csr
			test.log(LogStatus.INFO, "Get the jobs by site id in descinding order: "+suborg_sites[0] +"where page is:3 and page_size is: 1 using csr token");
			additionalURL = spogServer.PrepareURL("site_id;=;"+suborg_sites[0], "start_time_ts;desc", 3, 1, test);
			response = spogServer.getJobs(csr_token, additionalURL, test);
			spogServer.checkGetJobs(response, 3, 1, "create_ts;desc","site_id;=;"+suborg_sites[0],suborg_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			//csr_readonly
			test.log(LogStatus.INFO, "Get the jobs by site id in descinding order: "+suborg_sites[0] +"where page is:3 and page_size is: 1 using csr_readonly token");
			additionalURL = spogServer.PrepareURL("site_id;=;"+suborg_sites[0], "start_time_ts;desc", 3, 1, test);
			response = spogServer.getJobs(csr_readonly_token, additionalURL, test);
			spogServer.checkGetJobs(response, 3, 1, "create_ts;desc","site_id;=;"+suborg_sites[0],suborg_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			test.log(LogStatus.INFO, "Get all the jobs by rps id in descending order: "+suborg_rps[0] +"where page is:1 and page_size is: 20 using msp token");
			additionalURL = spogServer.PrepareURL("rps_id;=;"+suborg_rps[0], "start_time_ts;desc", 1, 20, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts;desc","rps_id;=;"+suborg_rps[0],suborg_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			//csr
			test.log(LogStatus.INFO, "Get all the jobs by rps id in descending order: "+suborg_rps[0] +"where page is:1 and page_size is: 20 using csr token");
			additionalURL = spogServer.PrepareURL("rps_id;=;"+suborg_rps[0], "start_time_ts;desc", 1, 20, test);
			response = spogServer.getJobs(csr_token, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts;desc","rps_id;=;"+suborg_rps[0],suborg_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			//csr readonly
			test.log(LogStatus.INFO, "Get all the jobs by rps id in descending order: "+suborg_rps[0] +"where page is:1 and page_size is: 20 using csr_readonly token");
			additionalURL = spogServer.PrepareURL("rps_id;=;"+suborg_rps[0], "start_time_ts;desc", 1, 20, test);
			response = spogServer.getJobs(csr_readonly_token, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts;desc","rps_id;=;"+suborg_rps[0],suborg_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			test.log(LogStatus.INFO, "Get all the jobs by all rps id in descending order: "+suborg_rps[0] +suborg_rps[1] +"where page is:1 and page_size is: 30 using msp token");
			additionalURL = spogServer.PrepareURL("rps_id;in;"+suborg_rps[0]+"|"+suborg_rps[1], "start_time_ts;desc", 1, 30, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 30, "create_ts;desc","rps_id;in;"+suborg_rps[0]+"|"+suborg_rps[1],suborg_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			test.log(LogStatus.INFO, "Get all the jobs by all rps id in descending order: "+suborg_rps[0] +suborg_rps[1] +"where page is:1 and page_size is: 20 using csr token");
			additionalURL = spogServer.PrepareURL("rps_id;in;"+suborg_rps[0]+"|"+suborg_rps[1], "start_time_ts;desc", 1, 30, test);
			response = spogServer.getJobs(csr_token, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 30, "create_ts;desc","rps_id;in;"+suborg_rps[0]+"|"+suborg_rps[1],suborg_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			
			test.log(LogStatus.INFO, "Get all the jobs by server id in descending order: "+suborg_serverid[0] +"where page is:1 and page_size is: 20 using msp token");
			additionalURL = spogServer.PrepareURL("server_id;=;"+suborg_serverid[0],"start_time_ts;desc", 1, 20, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts;desc","server_id;=;"+suborg_serverid[0],suborg_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			//csr
			test.log(LogStatus.INFO, "Get all the jobs by server id in descending order: "+suborg_serverid[0] +"where page is:1 and page_size is: 20 using csr token");
			additionalURL = spogServer.PrepareURL("server_id;=;"+suborg_serverid[0],"start_time_ts;desc", 1, 20, test);
			response = spogServer.getJobs(csr_token, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts;desc","server_id;=;"+suborg_serverid[0],suborg_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			//csr_readonly
			test.log(LogStatus.INFO, "Get all the jobs by server id in descending order: "+suborg_serverid[0] +"where page is:1 and page_size is: 20 using csr_readonly token");
			additionalURL = spogServer.PrepareURL("server_id;=;"+suborg_serverid[0],"start_time_ts;desc", 1, 20, test);
			response = spogServer.getJobs(csr_readonly_token, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts;desc","server_id;=;"+suborg_serverid[0],suborg_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			/*test.log(LogStatus.INFO, "Get all the jobs by all server id in ascending order: "+suborg_serverid[0] +suborg_serverid[1]+"where page is:1 and page_size is: 20 using msp token");
			additionalURL = spogServer.PrepareURL("server_id;in;"+suborg_serverid[0]+"|"+suborg_serverid[1]+",start_time_ts;<;"+ System.currentTimeMillis(),"", 1, 30, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 30, "create_ts","server_id;in;"+suborg_serverid[0]+"|"+suborg_serverid[1],suborg_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			test.log(LogStatus.INFO, "Get all the jobs by all server id in ascending order: "+suborg_serverid[0] +suborg_serverid[1]+"where page is:1 and page_size is: 20 using csr token");
			additionalURL = spogServer.PrepareURL("server_id;in;"+suborg_serverid[0]+"|"+suborg_serverid[1]+",start_time_ts;<;"+ System.currentTimeMillis(),"", 1, 30, test);
			response = spogServer.getJobs(csr_token, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 30, "create_ts","server_id;in;"+suborg_serverid[0]+"|"+suborg_serverid[1],suborg_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			test.log(LogStatus.INFO, "Get all the jobs by destination id: "+suborg_destinationid[0] +"where page is:1 and page_size is: 20");
			additionalURL = spogServer.PrepareURL("destination_id;=;"+suborg_destinationid[0]+",start_time_ts;<;"+ System.currentTimeMillis(),"", 1, 20, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts","destination_id;=;"+suborg_destinationid[0],suborg_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			test.log(LogStatus.INFO, "Get all the jobs by destination id: "+suborg_destinationid[0] +suborg_destinationid[1] +"where page is:1 and page_size is: 20");
			additionalURL = spogServer.PrepareURL("destination_id;in;"+suborg_destinationid[0]+"|"+suborg_destinationid[1]+",start_time_ts;<;"+ System.currentTimeMillis(),"", 1, 30, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 30, "create_ts","destination_id;in;"+suborg_destinationid[0]+suborg_destinationid[1],suborg_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			test.log(LogStatus.INFO, "Get all the jobs by policy id: "+suborg_policyid[0] +"where page is:1 and page_size is: 20");
			additionalURL = spogServer.PrepareURL("policy_id;=;"+suborg_policyid[0]+",start_time_ts;<;"+ System.currentTimeMillis(),"", 1, 20, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts","policy_id;=;"+suborg_policyid[0],suborg_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);*/
			
			test.log(LogStatus.INFO, "Get all the jobs by policy_id id: "+suborg_policyid[0] +" and "+suborg_policyid[1] +"where page is:1 and page_size is: 20");
			additionalURL = spogServer.PrepareURL("policy_id;in;"+suborg_policyid[0]+"|"+suborg_policyid[1]+",start_time_ts;>;"+ current,"", 1, 30, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 30, "create_ts","policy_id;in;"+suborg_policyid[0]+suborg_policyid[1],suborg_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			
			//Get all the jobs under msp and suborg
			test.log(LogStatus.INFO, "Get the jobs by organization id(msp and suborg) in ascending order: where page is:2 and page_size is: 2 using msp token");
			additionalURL = spogServer.PrepareURL("organization_id;in;"+msp_organization_id+"|"+organization_id, "start_time_ts;asc", 2, 2, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, 2, 2, "create_ts;asc","organization_id,in"+msp_organization_id+"|"+organization_id,expected_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			//csr
			test.log(LogStatus.INFO, "Get the jobs by organization id(msp and suborg) in ascending order: where page is:2 and page_size is: 2 using csr token");
			additionalURL = spogServer.PrepareURL("organization_id;in;"+msp_organization_id+"|"+organization_id, "start_time_ts;asc", 2, 2, test);
			response = spogServer.getJobs(csr_token, additionalURL, test);
			spogServer.checkGetJobs(response, 2, 2, "create_ts;asc","organization_id,in"+msp_organization_id+"|"+organization_id,expected_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			//csr_readonly
			test.log(LogStatus.INFO, "Get the jobs by organization id(msp and suborg) in ascending order: where page is:2 and page_size is: 2 using csr_readonly token");
			additionalURL = spogServer.PrepareURL("organization_id;in;"+msp_organization_id+"|"+organization_id, "start_time_ts;asc", 2, 2, test);
			response = spogServer.getJobs(csr_readonly_token, additionalURL, test);
			spogServer.checkGetJobs(response, 2, 2, "create_ts;asc","organization_id,in"+msp_organization_id+"|"+organization_id,expected_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			test.log(LogStatus.INFO, "Get the jobs by organization id in descending order: "+msp_organization_id +"where page is:-1 and page_size is: 2 using msp token");
			additionalURL = spogServer.PrepareURL("organization_id;in;"+msp_organization_id+"|"+organization_id, "start_time_ts;desc", -1, 2, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, -1, 2, "create_ts;desc","organization_id,in;"+msp_organization_id+"|"+organization_id,expected_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			
			
			
		}else {
			
			test.log(LogStatus.INFO, "Get different jobs filtered by job_type: "+jobType[0]+"_increment" + jobType[0]+"_full" );
			additionalURL = spogServer.PrepareURL("job_type;in;"+jobType[0]+"_full"+"|"+jobType[0]+"_incremental", "", 1, 20, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts","job_type;in;"+jobType[0]+"_full"+"|"+jobType[0]+"_incremental", expected_response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			
			test.log(LogStatus.INFO, "Get different jobs filtered by job_type: "+jobType[0]+"_increment" + jobType[0]+"_full and job_status: " +jobStatus[1]);
			additionalURL = spogServer.PrepareURL("job_type;in;"+jobType[0]+"_full"+"|"+jobType[0]+"_incremental,job_status;in;"+jobStatus[1], "", 1, 20, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts","job_type;in;"+jobType[0]+"_full"+"|"+jobType[0]+"_incremental,job_status;in;"+jobStatus[1], expected_response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			
			test.log(LogStatus.INFO, "Get different jobs filtered by job_type: "+jobType[0]+"_increment" + jobType[0]+"_full and source_id: " +sources[0]+ " and " +sources[1]);
			additionalURL = spogServer.PrepareURL("job_type;in;"+jobType[0]+"_full"+"|"+jobType[0]+"_incremental,source_id;in;"+sources[0]+"|"+sources[1], "", 1, 20, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts","job_type;in;"+jobType[0]+"_full"+"|"+jobType[0]+"_incremental,source_id;in;"+sources[0]+"|"+sources[1], expected_response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			
			test.log(LogStatus.INFO, "Get different jobs filtered by job_type: "+jobType[0]+"_increment" + jobType[0]+"_full and source_id: " +sources[0]+ " and " +sources[1]);
			additionalURL = spogServer.PrepareURL("job_type;in;"+jobType[0]+"_full"+"|"+jobType[0]+"_incremental,job_status;in;"+jobStatus[1]+",source_id;in;"+sources[0]+"|"+sources[1], "", 1, 20, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts","job_type;in;"+jobType[0]+"_full"+"|"+jobType[0]+"_incremental,job_status;in;"+jobStatus[1]+",source_id;in;"+sources[0]+"|"+sources[1], expected_response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			
			test.log(LogStatus.INFO, "Get all the jobs filtered by job_type: "+jobType[0]+"_increment" + "and policyid: "+policyids[0] );
			additionalURL = spogServer.PrepareURL("job_type;=;"+jobType[0]+"_incremental,policy_id;=;"+policyids[0], "", 1, 20, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts","job_type;=;"+jobType[0]+"_incremental,policy_id;=;"+policyids[0], expected_response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			
			test.log(LogStatus.INFO, "Get all the jobs filtered by job_type: "+jobType[0]+"_increment" + "and sourceid: "+sources[0] );
			additionalURL = spogServer.PrepareURL("job_type;=;"+jobType[0]+"_incremental,source_id;=;"+sources[0], "", 1, 20, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts","job_type;=;"+jobType[0]+"_incremental,source_id;=;"+sources[0], expected_response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			
			test.log(LogStatus.INFO, "Get all the jobs filtered by job_status: "+jobStatus[1] + "and sourceid: "+sources[1] );
			additionalURL = spogServer.PrepareURL("job_status;=;"+jobStatus[1]+",source_id;=;"+sources[1], "", 1, 20, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts","job_status;=;"+jobStatus[1]+",source_id;=;"+sources[1], expected_response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			
			test.log(LogStatus.INFO, "Get all the jobs filtered by job_status: "+jobStatus[1] + "and policy_id: "+policyids[0] );
			additionalURL = spogServer.PrepareURL("job_status;=;"+jobStatus[1]+",policy_id;=;"+policyids[0], "", 1, 20, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts","job_status;=;"+jobStatus[1]+",policy_id;=;"+policyids[0], expected_response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			
			test.log(LogStatus.INFO, "Get all the jobs filtered by source_id: "+sources[0] + "and policy_id: "+policyids[0] );
			additionalURL = spogServer.PrepareURL("source_id;=;"+sources[0]+",policy_id;=;"+policyids[0], "", 1, 20, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts","source_id;=;"+sources[0]+",policy_id;=;"+policyids[0], expected_response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			
			test.log(LogStatus.INFO, "Get all the jobs filtered by source_id: "+sources[0] + "and policy_id: "+policyids[0] );
			additionalURL = spogServer.PrepareURL("source_id;=;"+sources[0]+",policy_id;=;"+policyids[0], "", 1, 20, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts","source_id;=;"+sources[0]+",policy_id;=;"+policyids[0], expected_response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			
			test.log(LogStatus.INFO, "Get all the jobs filtered by source name: "+sourcenames[0]  );
			additionalURL = spogServer.PrepareURL("source_name;=;"+sourcenames[0], "", 1, 20, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts","source_name;=;"+sourcenames[0], expected_response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			
			test.log(LogStatus.INFO, "Get all the jobs filtered by source name that does not exist ");
			additionalURL = spogServer.PrepareURL("source_name;=;123e", "", 1, 20, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts","source_name;=;123e", expected_response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			
			
			test.log(LogStatus.INFO, "Get all the jobs when search by source name that contains office365");
			additionalURL = spogServer.PrepareURL("source_name;=;office365", "", 1, 20, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 30, "create_ts","source_name;=;office365", expected_response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			
			test.log(LogStatus.INFO, "Get all the jobs when search by source name that contains off");
			additionalURL = spogServer.PrepareURL("source_name;=;off", "", 1, 20, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 30, "create_ts","source_name;=;off", expected_response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			
			if(organizationType.equals("suborg")) {
				test.log(LogStatus.INFO, "Get different jobs filtered by job_type: "+jobType[0]+"_increment" + jobType[0]+"_full" );
				additionalURL = spogServer.PrepareURL("job_type;in;"+jobType[0]+"_full"+"|"+jobType[0]+"_incremental", "", 1, 20, test);
				response = spogServer.getJobs(msp_account_admin_validToken, additionalURL, test);
				spogServer.checkGetJobs(response, 1, 20, "create_ts","job_type;in;"+jobType[0]+"_full"+"|"+jobType[0]+"_incremental", expected_response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
				
				test.log(LogStatus.INFO, "Get different jobs filtered by job_type: "+jobType[0]+"_increment" + jobType[0]+"_full and job_status: " +jobStatus[1]);
				additionalURL = spogServer.PrepareURL("job_type;in;"+jobType[0]+"_full"+"|"+jobType[0]+"_incremental,job_status;in;"+jobStatus[1], "", 1, 20, test);
				response = spogServer.getJobs(msp_account_admin_validToken, additionalURL, test);
				spogServer.checkGetJobs(response, 1, 20, "create_ts","job_type;in;"+jobType[0]+"_full"+"|"+jobType[0]+"_incremental,job_status;in;"+jobStatus[1], expected_response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
				
				test.log(LogStatus.INFO, "Get different jobs filtered by job_type: "+jobType[0]+"_increment" + jobType[0]+"_full and source_id: " +sources[0]+ " and " +sources[1]);
				additionalURL = spogServer.PrepareURL("job_type;in;"+jobType[0]+"_full"+"|"+jobType[0]+"_incremental,source_id;in;"+sources[0]+"|"+sources[1], "", 1, 20, test);
				response = spogServer.getJobs(msp_account_admin_validToken, additionalURL, test);
				spogServer.checkGetJobs(response, 1, 20, "create_ts","job_type;in;"+jobType[0]+"_full"+"|"+jobType[0]+"_incremental,source_id;in;"+sources[0]+"|"+sources[1], expected_response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
				
				test.log(LogStatus.INFO, "Get different jobs filtered by job_type: "+jobType[0]+"_increment" + jobType[0]+"_full and source_id: " +sources[0]+ " and " +sources[1]);
				additionalURL = spogServer.PrepareURL("job_type;in;"+jobType[0]+"_full"+"|"+jobType[0]+"_incremental,job_status;in;"+jobStatus[1]+",source_id;in;"+sources[0]+"|"+sources[1], "", 1, 20, test);
				response = spogServer.getJobs(msp_account_admin_validToken, additionalURL, test);
				spogServer.checkGetJobs(response, 1, 20, "create_ts","job_type;in;"+jobType[0]+"_full"+"|"+jobType[0]+"_incremental,job_status;in;"+jobStatus[1]+",source_id;in;"+sources[0]+"|"+sources[1], expected_response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
				
				test.log(LogStatus.INFO, "Get all the jobs filtered by job_type: "+jobType[0]+"_increment" + "and policyid: "+policyids[0] );
				additionalURL = spogServer.PrepareURL("job_type;=;"+jobType[0]+"_incremental,policy_id;=;"+policyids[0], "", 1, 20, test);
				response = spogServer.getJobs(msp_account_admin_validToken, additionalURL, test);
				spogServer.checkGetJobs(response, 1, 20, "create_ts","job_type;=;"+jobType[0]+"_incremental,policy_id;=;"+policyids[0], expected_response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
				
				test.log(LogStatus.INFO, "Get all the jobs filtered by job_type: "+jobType[0]+"_increment" + "and sourceid: "+sources[0] );
				additionalURL = spogServer.PrepareURL("job_type;=;"+jobType[0]+"_incremental,source_id;=;"+sources[0], "", 1, 20, test);
				response = spogServer.getJobs(msp_account_admin_validToken, additionalURL, test);
				spogServer.checkGetJobs(response, 1, 20, "create_ts","job_type;=;"+jobType[0]+"_incremental,source_id;=;"+sources[0], expected_response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
				
				test.log(LogStatus.INFO, "Get all the jobs filtered by job_status: "+jobStatus[1] + "and sourceid: "+sources[1] );
				additionalURL = spogServer.PrepareURL("job_status;=;"+jobStatus[1]+",source_id;=;"+sources[1], "", 1, 20, test);
				response = spogServer.getJobs(msp_account_admin_validToken, additionalURL, test);
				spogServer.checkGetJobs(response, 1, 20, "create_ts","job_status;=;"+jobStatus[1]+",source_id;=;"+sources[1], expected_response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
				
				test.log(LogStatus.INFO, "Get all the jobs filtered by job_status: "+jobStatus[1] + "and policy_id: "+policyids[0] );
				additionalURL = spogServer.PrepareURL("job_status;=;"+jobStatus[1]+",policy_id;=;"+policyids[0], "", 1, 20, test);
				response = spogServer.getJobs(msp_account_admin_validToken, additionalURL, test);
				spogServer.checkGetJobs(response, 1, 20, "create_ts","job_status;=;"+jobStatus[1]+",policy_id;=;"+policyids[0], expected_response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
				
				test.log(LogStatus.INFO, "Get all the jobs filtered by source_id: "+sources[0] + "and policy_id: "+policyids[0] );
				additionalURL = spogServer.PrepareURL("source_id;=;"+sources[0]+",policy_id;=;"+policyids[0], "", 1, 20, test);
				response = spogServer.getJobs(msp_account_admin_validToken, additionalURL, test);
				spogServer.checkGetJobs(response, 1, 20, "create_ts","source_id;=;"+sources[0]+",policy_id;=;"+policyids[0], expected_response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
				
				test.log(LogStatus.INFO, "Get all the jobs filtered by source_id: "+sources[0] + "and policy_id: "+policyids[0] );
				additionalURL = spogServer.PrepareURL("source_id;=;"+sources[0]+",policy_id;=;"+policyids[0], "", 1, 20, test);
				response = spogServer.getJobs(msp_account_admin_validToken, additionalURL, test);
				spogServer.checkGetJobs(response, 1, 20, "create_ts","source_id;=;"+sources[0]+",policy_id;=;"+policyids[0], expected_response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
				
				test.log(LogStatus.INFO, "Get all the jobs when search by source name that contains office365");
				additionalURL = spogServer.PrepareURL("source_name;=;office365", "", 1, 20, test);
				response = spogServer.getJobs(msp_account_admin_validToken, additionalURL, test);
				spogServer.checkGetJobs(response, 1, 30, "create_ts","source_name;=;office365", expected_response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
				
				test.log(LogStatus.INFO, "Get all the jobs when search by source name that contains off");
				additionalURL = spogServer.PrepareURL("source_name;=;off", "", 1, 20, test);
				response = spogServer.getJobs(msp_account_admin_validToken, additionalURL, test);
				spogServer.checkGetJobs(response, 1, 30, "create_ts","source_name;=;off", expected_response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
				
				
			}
			
			test.log(LogStatus.INFO, "Get all the jobs filtered by source name: "+sourcenames[0] + " and " +sourcenames[1]  );
			additionalURL = spogServer.PrepareURL("source_name;in;"+sourcenames[0]+"|"+sourcenames[1], "", 1, 20, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts","source_name;in;"+sourcenames[0]+"|"+sourcenames[1], expected_response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			
			test.log(LogStatus.INFO, "Get the jobs by organization id in ascending order: "+organization_id +"where page is:2 and page_size is: 2");
			additionalURL = spogServer.PrepareURL("organization_id;=;"+organization_id, "start_time_ts;asc", 2, 2, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, 2, 2, "create_ts;asc","organization_id,="+organization_id,expected_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			test.log(LogStatus.INFO, "Get the jobs by organization id in descending order: "+organization_id +"where page is:-1 and page_size is: 2");
			additionalURL = spogServer.PrepareURL("organization_id;=;"+organization_id, "start_time_ts;desc", -1, 2, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, -1, 2, "create_ts;desc","organization_id,="+organization_id,expected_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			//csr
			test.log(LogStatus.INFO, "Get the jobs by organization id in descending order: "+organization_id +"where page is:-1 and page_size is: 2 using csr token");
			additionalURL = spogServer.PrepareURL("organization_id;=;"+organization_id, "start_time_ts;desc", -1, 2, test);
			response = spogServer.getJobs(csr_token, additionalURL, test);
			spogServer.checkGetJobs(response, -1, 2, "create_ts;desc","organization_id,="+organization_id,expected_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			//csr_readonly
			test.log(LogStatus.INFO, "Get the jobs by organization id in descending order: "+organization_id +"where page is:-1 and page_size is: 2 using csr_readonly token");
			additionalURL = spogServer.PrepareURL("organization_id;=;"+organization_id, "start_time_ts;desc", -1, 2, test);
			response = spogServer.getJobs(csr_readonly_token, additionalURL, test);
			spogServer.checkGetJobs(response, -1, 2, "create_ts;desc","organization_id,="+organization_id,expected_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			test.log(LogStatus.INFO, "Get the jobs in all sites in descending order: "+siteids[0] +siteids[1] +"where page is:3 and page_size is: 1");
			additionalURL = spogServer.PrepareURL("site_id;in;"+siteids[0]+"|"+siteids[1], "start_time_ts;desc", 3, 1, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, 3, 1, "create_ts;desc","site_id;in;"+siteids[0]+"|"+siteids[1],expected_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			test.log(LogStatus.INFO, "Get the jobs in all sites in descending order: "+siteids[0] +siteids[1] +"where page is:1 and page_size is: 20");
			additionalURL = spogServer.PrepareURL("site_id;in;"+siteids[0]+"|"+siteids[1], "start_time_ts;desc", 1, 20, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts;desc","site_id;in;"+siteids[0]+"|"+siteids[1],expected_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			//csr
			test.log(LogStatus.INFO, "Get the jobs in all sites in descending order: "+siteids[0] +siteids[1] +"where page is:3 and page_size is: 1 using csr token");
			additionalURL = spogServer.PrepareURL("site_id;in;"+siteids[0]+"|"+siteids[1], "start_time_ts;desc", 3, 1, test);
			response = spogServer.getJobs(csr_token, additionalURL, test);
			spogServer.checkGetJobs(response, 3, 1, "create_ts;desc","site_id;in;"+siteids[0]+"|"+siteids[1],expected_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			//csr_readonly
			test.log(LogStatus.INFO, "Get the jobs in all sites in descending order: "+siteids[0] +siteids[1] +"where page is:3 and page_size is: 1 using csr_readonly token");
			additionalURL = spogServer.PrepareURL("site_id;in;"+siteids[0]+"|"+siteids[1], "start_time_ts;desc", 3, 1, test);
			response = spogServer.getJobs(csr_readonly_token, additionalURL, test);
			spogServer.checkGetJobs(response, 3, 1, "create_ts;desc","site_id;in;"+siteids[0]+"|"+siteids[1],expected_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			test.log(LogStatus.INFO, "Get the jobs by site id in descinding order: "+siteids[0] +"where page is:3 and page_size is: 1");
			additionalURL = spogServer.PrepareURL("site_id;=;"+siteids[0], "start_time_ts;desc", 3, 1, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, 3, 1, "create_ts;desc","site_id;=;"+siteids[0],expected_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			test.log(LogStatus.INFO, "Get all the jobs by rps id in descending order: "+rpsids[0] +"where page is:1 and page_size is: 20");
			additionalURL = spogServer.PrepareURL("rps_id;=;"+rpsids[0], "start_time_ts;desc", 1, 20, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts;desc","rps_id;=;"+rpsids[0],expected_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			test.log(LogStatus.INFO, "Get all the jobs by all rps id in descending order: "+rpsids[0] +rpsids[1] +"where page is:1 and page_size is: 20");
			additionalURL = spogServer.PrepareURL("rps_id;in;"+rpsids[0]+"|"+rpsids[1], "start_time_ts;desc", 1, 20, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts;desc","rps_id;in;"+rpsids[0]+"|"+rpsids[1],expected_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			//csr
			test.log(LogStatus.INFO, "Get all the jobs by all rps id in descending order: "+rpsids[0] +rpsids[1] +"where page is:1 and page_size is: 20 using csr token");
			additionalURL = spogServer.PrepareURL("rps_id;in;"+rpsids[0]+"|"+rpsids[1], "start_time_ts;desc", 1, 20, test);
			response = spogServer.getJobs(csr_token, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts;desc","rps_id;in;"+rpsids[0]+"|"+rpsids[1],expected_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			//csr_readonly
			test.log(LogStatus.INFO, "Get all the jobs by all rps id in descending order: "+rpsids[0] +rpsids[1] +"where page is:1 and page_size is: 20 using csr_readonly token");
			additionalURL = spogServer.PrepareURL("rps_id;in;"+rpsids[0]+"|"+rpsids[1], "start_time_ts;desc", 1, 20, test);
			response = spogServer.getJobs(csr_readonly_token, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts;desc","rps_id;in;"+rpsids[0]+"|"+rpsids[1],expected_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);			
			
			test.log(LogStatus.INFO, "Get all the jobs by server id in descending order: "+serverids[0] +"where page is:1 and page_size is: 20");
			additionalURL = spogServer.PrepareURL("server_id;=;"+serverids[0],"start_time_ts;desc", 1, 20, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts;desc","server_id;=;"+serverids[0],expected_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			test.log(LogStatus.INFO, "Get all the jobs by all server id : "+serverids[0] +" "+serverids[1]+"where page is:1 and page_size is: 20");
			additionalURL = spogServer.PrepareURL("server_id;in;"+serverids[0]+"|"+serverids[1]+",start_time_ts;>;"+current,"", 1, 20, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts","server_id;in;"+serverids[0]+"|"+serverids[1],expected_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			//csr
			test.log(LogStatus.INFO, "Get all the jobs by all server id : "+serverids[0] +serverids[1]+"where page is:1 and page_size is: 20 using csr token");
			additionalURL = spogServer.PrepareURL("server_id;in;"+serverids[0]+"|"+serverids[1]+",start_time_ts;>;"+current,"", 1, 20, test);
			response = spogServer.getJobs(csr_token, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts","server_id;in;"+serverids[0]+"|"+serverids[1],expected_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			//csr_readonly
			test.log(LogStatus.INFO, "Get all the jobs by all server id : "+serverids[0] +serverids[1]+"where page is:1 and page_size is: 20 using csr_readonly token");
			additionalURL = spogServer.PrepareURL("server_id;in;"+serverids[0]+"|"+serverids[1]+",start_time_ts;>;"+current,"", 1, 20, test);
			response = spogServer.getJobs(csr_readonly_token, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts","server_id;in;"+serverids[0]+"|"+serverids[1],expected_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			/*test.log(LogStatus.INFO, "Get all the jobs by all server id by timestamp>yesterday: "+serverids[0] +serverids[1]+"where page is:1 and page_size is: 20");
			additionalURL = spogServer.PrepareURL("server_id;in;"+serverids[0]+"|"+serverids[1]+",start_time_ts;>;"+current,"", 1, 20, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts","server_id;in;"+serverids[0]+"|"+serverids[1],expected_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			*/
			
			test.log(LogStatus.INFO, "Get all the jobs by destination id: "+datastoreids[0] +"where page is:1 and page_size is: 20");
			additionalURL = spogServer.PrepareURL("destination_id;=;"+datastoreids[0]+",start_time_ts;>;"+current,"", 1, 20, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts","destination_id;=;"+datastoreids[0],expected_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			test.log(LogStatus.INFO, "Get all the jobs by destination id: "+datastoreids[0] +" and "+datastoreids[1] +"where page is:1 and page_size is: 20");
			additionalURL = spogServer.PrepareURL("destination_id;in;"+datastoreids[0]+"|"+datastoreids[1]+",start_time_ts;>;"+current,"", 1, 20, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts","destination_id;in;"+datastoreids[0]+datastoreids[1],expected_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			test.log(LogStatus.INFO, "Get all the jobs by destination id: "+datastoreids[0] +"where page is:1 and page_size is: 20 using csr token");
			additionalURL = spogServer.PrepareURL("destination_id;=;"+datastoreids[0]+",start_time_ts;>;"+current,"", 1, 20, test);
			response = spogServer.getJobs(csr_token, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts","destination_id;=;"+datastoreids[0],expected_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			//csr_readonly
			test.log(LogStatus.INFO, "Get all the jobs by destination id: "+datastoreids[0] +"where page is:1 and page_size is: 20 using csr_readonly token");
			additionalURL = spogServer.PrepareURL("destination_id;=;"+datastoreids[0]+",start_time_ts;>;"+current,"", 1, 20, test);
			response = spogServer.getJobs(csr_readonly_token, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts","destination_id;=;"+datastoreids[0],expected_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			test.log(LogStatus.INFO, "Get all the jobs by destination id: "+datastoreids[0] + " and "+datastoreids[1] +"where page is:1 and page_size is: 20 using csr token");
			additionalURL = spogServer.PrepareURL("destination_id;in;"+datastoreids[0]+"|"+datastoreids[1]+",start_time_ts;>;"+current,"", 1, 20, test);
			response = spogServer.getJobs(csr_token, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts","destination_id;in;"+datastoreids[0]+datastoreids[1],expected_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			//csr_readonly
			test.log(LogStatus.INFO, "Get all the jobs by destination id: "+datastoreids[0] + " and "+datastoreids[1] +"where page is:1 and page_size is: 20 using csr_readonly token");
			additionalURL = spogServer.PrepareURL("destination_id;in;"+datastoreids[0]+"|"+datastoreids[1]+",start_time_ts;>;"+current,"", 1, 20, test);
			response = spogServer.getJobs(csr_readonly_token, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts","destination_id;in;"+datastoreids[0]+datastoreids[1],expected_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			test.log(LogStatus.INFO, "Get all the jobs by destination id: "+datastoreids[0]+" and " +datastoreids[1] +"where page is:1 and page_size is: 20 using csr token");
			additionalURL = spogServer.PrepareURL("destination_id;in;"+datastoreids[0]+"|"+datastoreids[1]+",start_time_ts;>;"+current+",start_time_ts;<;"+ System.currentTimeMillis()/1000L,"", 1, 20, test);
			response = spogServer.getJobs(csr_token, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts","destination_id;in;"+datastoreids[0]+datastoreids[1],expected_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			//csr_readonly
			test.log(LogStatus.INFO, "Get all the jobs by destination id: "+datastoreids[0]+" and " +datastoreids[1] +"where page is:1 and page_size is: 20 using csr_readonly token");
			additionalURL = spogServer.PrepareURL("destination_id;in;"+datastoreids[0]+"|"+datastoreids[1]+",start_time_ts;>;"+current+",start_time_ts;<;"+ System.currentTimeMillis()/1000L,"", 1, 20, test);
			response = spogServer.getJobs(csr_readonly_token, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts","destination_id;in;"+datastoreids[0]+datastoreids[1],expected_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			test.log(LogStatus.INFO, "Get all the jobs by policy id: "+policyids[0] +" where page is:1 and page_size is: 20 using csr token");
			additionalURL = spogServer.PrepareURL("policy_id;=;"+policyids[0]+",start_time_ts;>;"+current,"", 1, 20, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts","policy_id;=;"+policyids[0],expected_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			test.log(LogStatus.INFO, "Get all the jobs by policy id: "+policyids[0]+" " +policyids[1] +"where page is:1 and page_size is: 20 using csr token");
			additionalURL = spogServer.PrepareURL("policy_id;in;"+policyids[0]+"|"+policyids[1]+",start_time_ts;>;"+current,"", 1, 20, test);
			response = spogServer.getJobs(csr_token, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts","policy_id;in;"+policyids[0]+"|"+policyids[1],expected_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			//csr_readonly
			test.log(LogStatus.INFO, "Get all the jobs by policy id: "+policyids[0]+" " +policyids[1] +"where page is:1 and page_size is: 20 using csr_readonly token");
			additionalURL = spogServer.PrepareURL("policy_id;in;"+policyids[0]+"|"+policyids[1]+",start_time_ts;>;"+current,"", 1, 20, test);
			response = spogServer.getJobs(csr_readonly_token, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts","policy_id;in;"+policyids[0]+"|"+policyids[1],expected_response,SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			
			test.log(LogStatus.INFO, "Get all the jobs by source_id: "+sources[0] +"where page is 1 and page_size is:20");
			additionalURL = spogServer.PrepareURL("source_id;in;"+sources[0]+",start_time_ts;>;"+current,"", 1, 20, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts", "source_id;=;"+sources[0], expected_response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			
			test.log(LogStatus.INFO, "Get all the jobs filtered by job_type: "+jobStatus[1]);
			additionalURL = spogServer.PrepareURL("job_status;=;"+jobStatus[1]+",start_time_ts;>;"+current, "", 1, 20, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts", "job_status;=;"+jobStatus[1], expected_response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			
			test.log(LogStatus.INFO, "Get all the jobs filtered by job_status: "+jobStatus[1]);
			additionalURL = spogServer.PrepareURL("job_status;=;"+jobStatus[1]+",start_time_ts;<;"+ System.currentTimeMillis()/1000L, "", 1, 20, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts", "job_status;=;"+jobStatus[1], expected_response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			
			test.log(LogStatus.INFO, "Get all the jobs filtered by job_status: "+jobStatus[0]);
			additionalURL = spogServer.PrepareURL("job_status;=;"+jobStatus[0]+",start_time_ts;>;"+current, "", 1, 20, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts", "job_status;=;"+jobStatus[0], expected_response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			
			test.log(LogStatus.INFO, "Get all the jobs filtered by job_type: "+jobType[0]+"_incremental");
			additionalURL = spogServer.PrepareURL("job_type;=;"+jobType[0]+"_incremental"+",start_time_ts;>;"+current, "", 1, 20, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts", "job_type;=;"+jobType[0]+"_incremental", expected_response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			
			test.log(LogStatus.INFO, "Get all the jobs filtered by job_type: "+jobType[0]+"_full");
			additionalURL = spogServer.PrepareURL("job_type;=;"+jobType[0]+"_full"+",start_time_ts;>;"+ current, "", 1, 20, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts", "job_type;=;"+jobType[0]+"_full", expected_response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			
			
			test.log(LogStatus.INFO, "Get all the jobs filtered by job_type: "+jobType[0]+"_verified" + "and job_status: "+jobStatus[0] );
			additionalURL = spogServer.PrepareURL("job_type;=;"+jobType[0]+"_verified,job_status;=;"+jobStatus[1], "", 1, 20, test);
			response = spogServer.getJobs(validToken, additionalURL, test);
			spogServer.checkGetJobs(response, 1, 20, "create_ts","job_type;=;"+jobType[0]+"_verified", expected_response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			
			
		}
	}
	
	@DataProvider(name = "postjobandjobdata_insufficientpermissions",parallel=false)
	public final Object[][] postjobandjobdata_insufficientpermissions() {
		return new Object[][] {
			{"direct",direct_org_email,common_password,direct_organization_id,msp_org_email,common_password},
			{"msp",msp_org_email,common_password,msp_organization_id,direct_org_email,common_password},
			{"msp",msp_org_email,common_password,msp_organization_id,initial_sub_email_a,common_password},
			{"msp",msp_org_email,common_password,msp_organization_id,msp_org_email_b,common_password},
			{"suborg",initial_sub_email_a,common_password,sub_org_Id,direct_org_email,common_password},
			{"suborg",initial_sub_email_a,common_password,sub_org_Id,initial_sub_email_b,common_password},
			
			
		};
	}
	@Test(dataProvider = "postjobandjobdata_insufficientpermissions")
	public void getjobs_insufficient_permissions(String organizationType,
										 	String userEmail,
										 	String password,
										 	String organization_id,
										 	String mspuserEmail,
										 	String msppassword) {
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		String additionalURL;
		Response response;
		
		test.log(LogStatus.INFO,"Login with the user "+mspuserEmail);
		spogServer.userLogin(mspuserEmail,msppassword);

		test.log(LogStatus.INFO,"Getting the JWTToken for the Logged in user");
		String validToken = spogServer.getJWTToken();
		//direct_user_validToken=validToken;
		test.log(LogStatus.INFO,"The token is :"+ validToken );

		test.log(LogStatus.INFO, "Get the jobs in "+organizationType+" org by organization id in ascending order: "+organization_id +"where page is:2 and page_size is: 2 using msp token(invalid permissions)");
		additionalURL = spogServer.PrepareURL("organization_id;=;"+organization_id, "create_ts;asc", 2, 2, test);
		response = spogServer.getJobs(validToken, additionalURL, test);
		spogServer.checkGetJobs(response, 2, 2, "create_ts;asc","organization_id,="+organization_id,suborg_response,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY,test);
		
		if(organizationType.equals("suborg")) {
			test.log(LogStatus.INFO, "Get the jobs in "+organizationType+" org by organization id in ascending order: "+sub_org_Id_1 +"where page is:2 and page_size is: 2 using msp token(invalid permissions)");
			additionalURL = spogServer.PrepareURL("organization_id;=;"+sub_org_Id_1, "create_ts;asc", 2, 2, test);
			response = spogServer.getJobs(msp_account_admin_validToken , additionalURL, test);
			spogServer.checkGetJobs(response, 2, 2, "create_ts;asc","organization_id,="+sub_org_Id_1,suborg_response,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY,test);
		}

		
		
		
	}
	
	@DataProvider(name = "postjobandjobdata_invalid_ids",parallel=false)
	public final Object[][] postjobandjobdata_invalid_ids() {
		return new Object[][] {
			{"direct",direct_org_email,common_password,UUID.randomUUID().toString()},
			{"msp",msp_org_email,common_password,UUID.randomUUID().toString()},
			{"suborg",initial_sub_email_a,common_password,UUID.randomUUID().toString()},
			{"csr",csrAdminUserName,csrAdminPassword,UUID.randomUUID().toString()},
			{"csr_readonly",csr_readonly_email,common_password,UUID.randomUUID().toString()}
		};
	}
	@Test(dataProvider = "postjobandjobdata_invalid_ids")
	public void getjobs_invalid_ids(String organizationType,
										 	String userEmail,
										 	String password,
										 	String organization_id
										 	) {
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		String additionalURL;
		Response response;
		
		test.log(LogStatus.INFO,"Login with the user "+userEmail);
		spogServer.userLogin(userEmail,password);

		test.log(LogStatus.INFO,"Getting the JWTToken for the Logged in user");
		String validToken = spogServer.getJWTToken();
		//direct_user_validToken=validToken;
		test.log(LogStatus.INFO,"The token is :"+ validToken );

		test.log(LogStatus.INFO, "Get the jobs in "+organizationType+" org by invalid organization id in ascending order: "+organization_id +"where page is:2 and page_size is: 2 ");
		additionalURL = spogServer.PrepareURL("organization_id;=;"+organization_id, "start_time_ts;asc", 2, 2, test);
		response = spogServer.getJobs(validToken, additionalURL, test);
		spogServer.checkGetJobs(response, 2, 2, "create_ts;asc","organization_id,="+organization_id,suborg_response,SpogConstants.SUCCESS_GET_PUT_DELETE,null,test);

		
		
		
	}
	
	@DataProvider(name = "postjobandjobdata_invalid_missing_token",parallel=false)
	public final Object[][] postjobandjobdata_invalid_missing_token() {
		return new Object[][] {
			{"direct",direct_org_email,common_password,direct_organization_id},
			{"msp",msp_org_email,common_password,msp_organization_id},
			{"suborg",initial_sub_email_a,common_password,sub_org_Id},
			{"csr",csrAdminUserName,csrAdminPassword,direct_organization_id},
			{"csr_readonly",csr_readonly_email,common_password,direct_organization_id}
		};
	}
	@Test(dataProvider = "postjobandjobdata_invalid_missing_token")
	public void getjobs_invalid_missing_token(String organizationType,
										 	String userEmail,
										 	String password,
										 	String organization_id
										 	) {
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		String additionalURL;
		Response response;
		
		test.log(LogStatus.INFO,"Login with the user "+userEmail);
		spogServer.userLogin(userEmail,password);

		test.log(LogStatus.INFO,"Getting the JWTToken for the Logged in user");
		String validToken = spogServer.getJWTToken();
		//direct_user_validToken=validToken;
		test.log(LogStatus.INFO,"The token is :"+ validToken );

		test.log(LogStatus.INFO, "Get the jobs in "+organizationType+" org by invalid JWT token");
		additionalURL = spogServer.PrepareURL("organization_id;=;"+organization_id, "start_time_ts;asc", 2, 2, test);
		response = spogServer.getJobs(validToken+"J", additionalURL, test);
		spogServer.checkGetJobs(response, 2, 2, "create_ts;asc","organization_id,="+organization_id,suborg_response,SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT,test);
		
		if(organizationType.equals("csr")) {
			test.log(LogStatus.INFO, "Get the jobs in "+organizationType+" org by missing JWT token");
			additionalURL = spogServer.PrepareURL("organization_id;=;"+organization_id, "start_time_ts;asc", 2, 2, test);
			response = spogServer.getJobs("", additionalURL, test);
			spogServer.checkGetJobs(response, 2, 2, "create_ts;asc","organization_id,="+organization_id,suborg_response,SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTHENTICATION_FAILED,test);
		}
		

		
		
		
	}
	
	@DataProvider(name = "postjobandjobdata_sort",parallel=false)
	public final Object[][] postjobandjobdata_sort() {
		return new Object[][] {
			{"direct",direct_org_email_1,common_password,direct_organization_id_1,SourceType.office_365,SourceProduct.udp,ProtectionStatus.unprotect,ConnectionStatus.online,UUID.randomUUID().toString(),
				UUID.randomUUID().toString(),UUID.randomUUID().toString(),2,1,new String[] {"backup"}, new String[] {"full","incremental","resync"}, 
				new String[] {"finished","canceled","failed","incomplete","idle","waiting","crash","license_failed","backupjob_proc_exit","skipped","stop","missed"},
				new String[] {"success","error","warning"}},
			/*{"msp",msp_org_email,common_password,msp_organization_id,SourceType.office_365,SourceProduct.udp,ProtectionStatus.unprotect,ConnectionStatus.online,UUID.randomUUID().toString(),
				UUID.randomUUID().toString(),UUID.randomUUID().toString(),"testJobMessage",new String[] { "node", "agent"},2,2,new String[] {"backup"}, "full", 
				new String[] {"finished",JobStatus.canceled.toString()},new String[] {LogSeverityType.information.toString(),LogSeverityType.warning.toString(),LogSeverityType.error.toString()}},*/
			{"suborg",initial_sub_email_a,common_password,sub_org_Id,SourceType.office_365,SourceProduct.udp,ProtectionStatus.unprotect,ConnectionStatus.online,UUID.randomUUID().toString(),
					UUID.randomUUID().toString(),UUID.randomUUID().toString(),2,1,new String[] {"backup"}, new String[] {"full","incremental","resync"}, 
					new String[] {"finished","canceled","failed","incomplete","idle","waiting","crash","license_failed","backupjob_proc_exit","skipped","stop","missed"},
					new String[] {"success","error","warning"}},
			{"msp_suborg",msp_org_email,common_password,sub_org_Id,SourceType.office_365,SourceProduct.udp,ProtectionStatus.unprotect,ConnectionStatus.online,UUID.randomUUID().toString(),
						UUID.randomUUID().toString(),UUID.randomUUID().toString(),2,1,new String[] {"backup"}, new String[] {"full","incremental","resync"}, 
						new String[] {"finished","canceled","failed","incomplete","idle","waiting","crash","license_failed","backupjob_proc_exit","skipped","stop","missed"},
						new String[] {"success","error","warning"}}
					};
	}

	
	@Test(dataProvider = "postjobandjobdata_sort")
	public void getjobs_sort(String organizationType,
										 String userEmail,
										 String password,
										 String organization_id,
										 SourceType source_type,
										 SourceProduct source_product,
										 ProtectionStatus protectionstatus,
										 ConnectionStatus conn_status,
										 String rps_id1,
										 String datastore_id1,
										 String policy_id1,
										 int noofjobs,
										 int noofsites,
										 String[] jobType,
										 String[] jobMethod,
										 String[] jobStatus,
										 String[] logSeverity
										) {
		long start_time_ts;
		long end_time_ts;
		long duration;
		String percentage_complete = "100";
		Response response;
		String additionalURL;
		String job_methods = null;
		String log_severity;
		int index=0;
		int indx_severity=0;
		
		String server_id;
		String resource_id;
		String rps_id;
		String datastore_id;
		String policy_id;
		String resource_name = spogServer.ReturnRandom("machinek")+"_site";
		
		String[] siteids = new String[noofsites];
		String[] sources = new String[noofjobs*noofsites];
		String[] rpsids = new String[noofsites];
		String[] serverids = new String[noofsites];
		String[] policyids = new String[noofsites];
		String[] datastoreids = new String[noofsites];
		ArrayList<HashMap<String, Object>> jobsandjobdata = new ArrayList<>();
		//ArrayList<HashMap<String,Object>> expected_response = new ArrayList<>();
		HashMap<String,Object> temp = new HashMap<>() ;
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		
		String destination_name;
		String site_token=null;
		
		test.log(LogStatus.INFO,"Login with the user "+userEmail);
		spogServer.userLogin(userEmail,password);
		
		test.log(LogStatus.INFO,"Getting the JWTToken for the Logged in user");
		String validToken = spogServer.getJWTToken();
		//direct_user_validToken=validToken;
		test.log(LogStatus.INFO,"The token is :"+ validToken );
		
		test.log(LogStatus.INFO, "Get the logged in user id ");
		String user_id = spogServer.GetLoggedinUser_UserID();
		test.log(LogStatus.INFO, "The logged in user id is "+user_id);
		int sourcecount=0;
		
		for(int j=0;j<noofsites;j++) {

			test.log(LogStatus.INFO, "Create a site/register/login to the site");
			String site_id = gatewayServer.createsite_register_login(organization_id, validToken, user_id, "ts", "1.0.0", spogServer, test);
			site_token = gatewayServer.getJWTToken();
			siteids[j]=site_id;
			rps_id = UUID.randomUUID().toString();
			rpsids[j] = rps_id;
			policy_id = UUID.randomUUID().toString();
			policyids[j] = policy_id;
			datastore_id = UUID.randomUUID().toString();
			datastoreids[j] = datastore_id;
			test.log(LogStatus.INFO, "Adding sources of type "+source_type +" to "+organizationType+" org");
			server_id = spogServer.createSourceWithCheck(resource_name, SourceType.machine, source_product, organization_id, site_id,protectionstatus, conn_status, "windows", "SQLSERVER", test);
			serverids[j] = server_id;

			test.log(LogStatus.INFO, "Create a destination of type cloud direct volume");
			spogDestinationServer.setToken(validToken);
			String[] datacenters = spogDestinationServer.getDestionationDatacenterID();

			test.log(LogStatus.INFO, "Post the destination");
			destination_name = RandomStringUtils.randomAlphanumeric(4)+"rakesh-test34";
			if (organizationType.contains("suborg")) {
				
//				spogServer.userLogin(msp_org_email, common_password);
//				response = spogDestinationServer.getDestinations(validToken, "", test);
//				datastore_id = response.then().extract().path("data[0].destination_id");
				spogDestinationServer.setToken(msp_user_validToken);
				datastore_id = spogDestinationServer.createDestinationWithCheck(UUID.randomUUID().toString(),organization_id, site_id, datacenters[1], DestinationType.cloud_direct_volume.toString(), DestinationStatus.running.toString(), 
						"20","","normal",RandomStringUtils.randomAlphanumeric(4)+"host-t", "2M", "2M", 
			    		"0","0", "31", "0", "2", "0", "5", "true", "1", "true",destination_name, test);
				spogServer.userLogin(userEmail,password);
			}else {
//				response = spogDestinationServer.getDestinations(validToken, "", test);
//				datastore_id = response.then().extract().path("data[0].destination_id");
				
				datastore_id = spogDestinationServer.createDestinationWithCheck(UUID.randomUUID().toString(),organization_id, site_id, datacenters[1], DestinationType.cloud_direct_volume.toString(), DestinationStatus.running.toString(), 
						"20","","normal",RandomStringUtils.randomAlphanumeric(4)+"host-t", "2M", "2M", 
			    		"0","0", "31", "0", "2", "0", "5", "true", "1", "true",destination_name, test);
			}
			
			datastoreids[j] = datastore_id;
			start_time_ts = System.currentTimeMillis()/1000L;
			end_time_ts = System.currentTimeMillis()/1000L+100;
			for(int i=0;i<jobStatus.length;i++) {

				percentage_complete="100";
				
				start_time_ts = start_time_ts + 1;
				end_time_ts = end_time_ts + 2;
				duration = end_time_ts - start_time_ts;
				if(jobType[0]=="backup") {

					index = gen_random_index(jobMethod);
					job_methods = jobMethod[index];

				}
				indx_severity = gen_random_index(logSeverity);
				log_severity = logSeverity[indx_severity];

				if(jobStatus[i]=="active") {
					end_time_ts=0;
					percentage_complete="10";
				}
				if(jobStatus[i]=="idle"||jobStatus[i]=="waiting") {
					//duration =0;
					percentage_complete="0";
				}
				String job_id = gatewayServer.postJobWithCheck(start_time_ts, end_time_ts,organization_id,server_id, server_id, rps_id, datastore_id, 
						policy_id,jobType[0], job_methods, jobStatus[i],site_token, test);

//				String job_id = response.then().extract().path("data.job_id");
				test.log(LogStatus.INFO, "The job id is "+job_id);
				test.log(LogStatus.INFO, "Post Jobs data to site under org ");
				response = gatewayServer.postJobData(job_id, Integer.toString(i+1), log_severity, percentage_complete, "0", "0", "0", "0", "0", "0","0","none","none","none","none","none","none","none","none",site_token,test);
				if(job_methods=="resync") job_methods="verified";
				
				test.log(LogStatus.INFO, "Store the jobs for validation purpose");
				HashMap<String, Object> temp1 = spogServer.composejobsinfo_check(job_id, site_id, server_id, server_id, resource_name, rps_id, organization_id,
						datastore_id,policy_id,start_time_ts, end_time_ts,jobType[0]+"_"+job_methods, jobStatus[i],destination_name,
						jobType[0]+"_"+job_methods+" from "+ resource_name + " to "+destination_name,log_severity,percentage_complete,duration,Integer.toString(i+1),DestinationType.cloud_direct_volume.toString());

				jobsandjobdata.add(temp1);
			}

		}		
		test.log(LogStatus.INFO, "Get different jobs sorted by job_status: " );
		additionalURL = spogServer.PrepareURL("", "job_status;desc", 1, 20, test);
		response = spogServer.getJobs(validToken, additionalURL, test);
		spogServer.checkGetJobs(response, 1, 20, "job_status,desc","", jobsandjobdata, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		
		test.log(LogStatus.INFO, "Get different jobs sorted by job_status: " );
		additionalURL = spogServer.PrepareURL("", "job_status;asc", 1, 20, test);
		response = spogServer.getJobs(validToken, additionalURL, test);
		spogServer.checkGetJobs(response, 1, 20, "job_status,asc","", jobsandjobdata, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		
		test.log(LogStatus.INFO, "Get different jobs sorted by job_type: " );
		additionalURL = spogServer.PrepareURL("", "job_type;asc", 1, 20, test);
		response = spogServer.getJobs(validToken, additionalURL, test);
		spogServer.checkGetJobs(response, 1, 20, "job_type,asc","", jobsandjobdata, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		
		// post one month old jobs 
		/*response = gatewayServer.postJob(ZonedDateTime.now().minusMonths(1).toInstant().toEpochMilli(), ZonedDateTime.now().minusMonths(1).toInstant().toEpochMilli(),organization_id,serverids[0], serverids[0], 
				UUID.randomUUID().toString(), UUID.randomUUID().toString(),UUID.randomUUID().toString(),jobType[0], jobMethod[0], jobStatus[0],site_token, test);

		String job_id = response.then().extract().path("data.job_id");
		test.log(LogStatus.INFO, "The job id is "+job_id);
		test.log(LogStatus.INFO, "Post Jobs data to site under org ");
		response = gatewayServer.postJobData(job_id, "20" , logSeverity[0], percentage_complete, "0", "0", "0", "0", "0", site_token,test);*/
		
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
	/******************************************************************RandomFunction******************************************************************************/
	public int gen_random_index(String[] job_status) {
		Random generator = new Random();
		int randomindx = generator.nextInt(job_status.length);
		
		return randomindx;
	}

}
