package CI;

import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.awt.image.SinglePixelPackedSampleModel;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Base64.Base64Coder;
import Constants.ConnectionStatus;
import Constants.LogSeverityType;
import Constants.LogSourceType;
import Constants.OSMajor;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.Log4GatewayServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import InvokerServer.ServerResponseCode;
import genericutil.ErrorHandler;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import groovy.lang.TracingInterceptor;
import invoker.SiteTestHelper;
import invoker.SiteTestHelper.siteType;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.FilterableRequestSpecification;

public class prepareUIData {
	private SPOGServer spogServer;
	public static ErrorHandler     errorHandle = ErrorHandler.getErrorHandler();
	private GatewayServer gatewayServer;
	private SPOGDestinationServer spogDestinationServer;
	private Log4GatewayServer log4GatewayServer;
	private String csrAdmin;
	private String csrPwd;
	private ExtentReports rep;
	private ExtentTest test;
	private testcasescount count1;

	private String initial_msp_org_name = "spog_qa_msp_ci";
	private String initial_msp_org_name_d = "spog_qa_msp_ci_d";
	private String initial_msp_org_name_u = "spog_qa_msp_ui";
	private String initial_msp_email = "spog_qa_msp_ci@arcserve.com";
	private String initial_msp_email_full = "";
	private String initial_msp_first_name = "spog_qa_msp_ma";
	private String initial_msp_last_name = "spog_qa_msp_ci";

	private String initial_msp_email_added = "spog_qa_msp_ci_added@arcserve.com";
	private String initial_msp_email_full_added = "";
	private String initial_msp_first_name_added = "spog_qa_msp_ma";
	private String initial_msp_last_name_added = "spog_qa_msp_ci";

	private String initial_msp_email_d = "spog_qa_msp_ci_deleted@arcserve.com";
	private String initial_msp_email_full_d = "";
	private String initial_msp_first_name_d = "spog_qa_msp_ma";
	private String initial_msp_last_name_d = "spog_qa_msp_ci";

	private String initial_msp_email_u = "spog_qa_msp_ci_updated@arcserve.com";

	private String initial_direct_org_name = "spog_qa_direct_ci";
	private String initial_direct_email = "spog_qa_direct_ci@arcserve.com";
	private String initial_direct_email_full = "";
	private String initial_direct_first_name = "spog_qa_direct_ma";
	private String initial_direct_last_name = "spog_qa_direct_ci2";

	private String initial_direct_email_added = "spog_qa_direct_ci_added@arcserve.com";
	private String initial_direct_email_full_added = "";
	private String initial_direct_first_name_added = "spog_qa_direct_ma";
	private String initial_direct_last_name_added = "spog_qa_direct_ci2";

	private String initial_sub_org_name_a = "spog_qa_sub_ci_a";
	private String initial_sub_email_a = "spog_qa_sub_ci_a@arcserve.com";
	private String initial_sub_email_full_a = "";
	private String initial_sub_first_name_a = "spog_qa_sub_ma_a";
	private String initial_sub_last_name_a = "spog_qa_sub_ci_a";

	private String initial_sub_email_a_added = "spog_qa_sub_ci_a_added@arcserve.com";
	private String initial_sub_email_full_a_added = "";
	private String initial_sub_first_name_a_added = "spog_qa_sub_ma_a";
	private String initial_sub_last_name_a_added = "spog_qa_sub_ci_a";

	private String initial_sub_email_a_deleted = "spog_qa_sub_ci_a_deleted@arcserve.com";
	private String initial_sub_email_full_a_deleted = "";
	private String initial_sub_first_name_a_deleted = "spog_qa_sub_ma_d";
	private String initial_sub_last_name_a_deleted = "spog_qa_sub_ci_d";

	private String initial_msp_orgID;
	private String initial_msp_orgID_d;
	private String initial_direct_orgID;
	private String initial_sub_orgID_a;

	private String password = "Pa$$w0rd";

	@BeforeSuite
	@Parameters({ "baseURI", "port", "csrAdminUserName", "csrAdminPassword" })
	public void beforeClass(String baseURI, String port, String csrAdminUserName, String csrAdminPassword) {
		spogServer = new SPOGServer(baseURI, port);
		//spogServer.checkSwagDocIsActive("http://qaspog2.zetta.net", 8080, SpogConstants.SUCCESS_GET_PUT_DELETE);
		// spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("test"),
		// SpogConstants.CSR_ORG, csrAdminUserName, csrAdminPassword, "", "");
	}

	@BeforeClass
	@Parameters({ "baseURI", "port", "csrAdminUserName", "csrAdminPassword", "logFolder", "runningMachine",
			"buildVersion" })
	public void beforeClass(String baseURI, String port, String csrAdminUserName, String csrAdminPassword,
			String logFolder, String runningMachine, String buildVersion) {

		rep = ExtentManager.getInstance("GetSpecificFilterFromSpecificUsersTest", logFolder);
		test = rep.startTest("initializing data...");

		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		log4GatewayServer = new Log4GatewayServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		this.csrAdmin = csrAdminUserName;
		this.csrPwd = csrAdminPassword;
		count1 = new testcasescount();
		//spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spog_ci"), SpogConstants.CSR_ORG, csrAdminUserName, csrAdminPassword, "ci", "ci");
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		// create msp organization #1, and then 1 sub-organizations in it, create one
		// admin for the sub organization;
		this.initial_msp_email_full = prefix + this.initial_msp_email;
		spogServer.userLogin(csrAdminUserName, csrAdminPassword,test);	
		
	}

	//@Test
	public void createMSPRelated() {
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		String prefix= "udp_zhangshuo_";
		String initial_msp_email = "spog_qa_msp_ui@arcserve.com";
		String initial_msp_account_org_name = "spog_qa_msp_account_ui";
		String initial_msp_first_name = "spog_qa_msp";
		String initial_msp_last_name = "spog_qa_msp_ui";
		String defaultMSPUserEmail = prefix + initial_msp_email;
		String defaultMSPUserFirstName = prefix + initial_msp_first_name;
		String defaultMSPUserLastName = prefix + initial_msp_last_name;
		String initial_msp_account_first_name = "spog_qa_msp_account";
		String initial_msp_account_last_name = "spog_qa_msp_account_ui";
		String initial_msp_account_email = "spog_qa_msp_account_ui@arcserve.com";
		String defaultMSPAccountUserEmail = prefix + initial_msp_account_email;
		String defaultMSPAccountUserFirstName = prefix + initial_msp_account_first_name;
		String defaultMSPAccountUserLastName = prefix + initial_msp_account_last_name;
		String initial_msp_orgID = spogServer.CreateOrganizationWithCheck(prefix + initial_msp_org_name,
				SpogConstants.MSP_ORG, null, null, null, null);
		test.log(LogStatus.INFO, "create MSP org");
		// create user
		Response user_response = spogServer.createUser(defaultMSPUserEmail, password, defaultMSPUserFirstName,
				defaultMSPUserLastName, SpogConstants.MSP_ADMIN, initial_msp_orgID, test);
		String msp_user_id = spogServer.checkCreateUser(user_response, SpogConstants.SUCCESS_POST, defaultMSPUserEmail,
				defaultMSPUserFirstName, defaultMSPUserLastName, SpogConstants.MSP_ADMIN, initial_msp_orgID, "", test);
		
		test.log(LogStatus.INFO, "create MSP admin user");
		String account_id = spogServer.createAccountWithCheck(initial_msp_orgID, initial_msp_account_org_name, "",
				test);
		test.log(LogStatus.INFO, "create an account for msp");
		user_response = spogServer.createUser(defaultMSPAccountUserEmail, password, defaultMSPAccountUserFirstName,
				defaultMSPAccountUserLastName, SpogConstants.DIRECT_ADMIN, account_id, test);
		String msp_account_user_id = spogServer.checkCreateUser(user_response, SpogConstants.SUCCESS_POST, defaultMSPAccountUserEmail,
				defaultMSPAccountUserFirstName, defaultMSPAccountUserLastName, SpogConstants.DIRECT_ADMIN, account_id, "", test);
		test.log(LogStatus.INFO, "create account admin user");
		// login user
		spogServer.userLogin(defaultMSPUserEmail, password);
		test.log(LogStatus.INFO, "login as msp admin user");
		String userToken = spogServer.getJWTToken();
		
		// define the params for createSource
		String spogToken = spogServer.getJWTToken();
		String userID = spogServer.GetLoggedinUser_UserID();

		// define the params for createSite - site will be used for jobs;
		String siteName = prefix + "siteName";
		String siteType = "gateway";
		String organizationID = initial_msp_orgID;

		// define the params for registerSite;
		String gatewayID = UUID.randomUUID().toString();
		String gatewayHostName = prefix + "hostName";
		String siteVersion = "";

		// define the params for postJobs;
		long startTimeTS = System.currentTimeMillis();
		String jobType = "backup";
		String jobMethod = "incremental";
		String jobStatus = "finished";
		long endTimeTS = System.currentTimeMillis();
		String serverID = UUID.randomUUID().toString();
		String rpsID = UUID.randomUUID().toString();
		String destinationID = UUID.randomUUID().toString();
		String messageID = "testJobMessage";
		String policyID = UUID.randomUUID().toString();
		String[] messageData = new String[] { "node", "agent" };

		// define the params for postJobData;
		String jobSeq = "1000";
		String severity = "information";
		String percentComplete = "20";
		String protectedDataSize = "12000";
		String rawDataSize = "13000";
		String syncReadSize = "14000";
		String ntfsVolumeSize = "15000";
		String virtualDiskProvisionSize = "16000";

		// test create a site (POST sites/link)
		test.log(LogStatus.INFO, "create a site");
		Response response = spogServer.createSite(siteName, siteType, organizationID, spogToken, test);
		Map<String, String> sitecreateResMap = new HashMap<>();
		sitecreateResMap = spogServer.checkCreateSite(response, SpogConstants.SUCCESS_POST, siteName, siteType,
				organizationID, userID, "", test);
		String registrationBasecode = sitecreateResMap.get("registration_basecode");
		String siteID = sitecreateResMap.get("site_id");

		String siteRegistrationKey = "";
		try {
			String decoded = URLDecoder.decode(registrationBasecode.trim(), "UTF-8");
			Base64Coder base64 = new Base64Coder();
			String decrypted = base64.decode(decoded);
			String[] parts = decrypted.split("\\n", -2);
			siteRegistrationKey = parts[1];
		} catch (UnsupportedEncodingException e) {
			test.log(LogStatus.FAIL, "The value of the error Message :" + e.getMessage());
		}

		// test register site (POST sites/:/id/register)
		test.log(LogStatus.INFO, "register a site");
		response = gatewayServer.RegisterSite(siteRegistrationKey, gatewayID, gatewayHostName, siteVersion, siteID,
				test);
		String site_secret = gatewayServer.checkRegisterSite(response, ServerResponseCode.Succes_Login, siteID,
				siteName, siteType, organizationID, userID, true, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

		// test login site (POST sites/:id/login)
		test.log(LogStatus.INFO, "login a site");
		response = gatewayServer.LoginSite(siteID, site_secret, gatewayID, test);
		String token = response.then().extract().path("data.token");

		// test post job (POST jobs)
		test.log(LogStatus.INFO, "post job");
		String resource_name = spogServer.ReturnRandom(prefix);
		String resourceID = spogServer.createSourceWithCheck(resource_name, SourceType.machine, SourceProduct.udp,
				organizationID, siteID, ProtectionStatus.protect, ConnectionStatus.online, "windows", "SQLSERVER",
				test);
		String jobID = gatewayServer.postJobWithCheck(startTimeTS, endTimeTS, organizationID, serverID, resourceID,
				rpsID, destinationID, policyID, jobType, jobMethod, jobStatus, token, test);

		// test post job data (POST jobs/:id/data)
		test.log(LogStatus.INFO, "post job data");
		gatewayServer.postJobDataWithCheck(jobID, jobSeq, severity, percentComplete, protectedDataSize, rawDataSize,
				syncReadSize, ntfsVolumeSize, virtualDiskProvisionSize, token, test);

		response = spogServer.getJobsById(spogToken, jobID, test);
		HashMap<String, Object> job_data = spogServer.composeJobData(response, test);

		// post log
		String message_date = "spog,QA" ;
		String SeverityType = "information";
		String logSourceType = "spog";
		String message_id = "testLogMessage";
		String log_id = gatewayServer.createLogwithCheck(startTimeTS,jobID, organizationID, organizationID, resourceID,SeverityType,
				logSourceType, message_id, message_date, token, test);
		test.log(LogStatus.INFO, "Create log successfully with log id is:" + log_id);
		
		spogServer.createCloudAccountWithCheck("cloudAccountKey", "cloudAccountSecret", "cloudAccountName", "cloud_direct", organizationID, 
				"SKUTESTDATA_1_0_0_0_"+prefix, "SKUTESTDATA_1_0_0_0_"+prefix, "SKUTESTDATA_1_0_0_0_"+prefix, "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test); 
		
	}
	
	@DataProvider(name = "createDirect")
	  public final Object[][] getDirectInfo() {
			return new Object[][] { {"udp_Evgenii.Tcarev_","Evgenii.Tcarev"}};
	  }
	@Test(dataProvider = "createDirect")
	public void createDirectRelated(String prefix, String qaName) {
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		//String resourceID_3 = spogServer.createSourceWithCheck(spogServer.ReturnRandom(prefix), SourceType.agentless_vm, SourceProduct.udp,
		//		"53761b6b-0b68-4a76-aeca-ad834f532d4c", "d191226b-c87c-4bb7-8283-10f3b7417159", ProtectionStatus.protect, ConnectionStatus.online, "windows", "sql",
		//		test);
		//String resourceID_4 = spogServer.createSourceWithCheck(spogServer.ReturnRandom(prefix), SourceType.instant_vm, SourceProduct.udp,
		//		"53761b6b-0b68-4a76-aeca-ad834f532d4c", "d191226b-c87c-4bb7-8283-10f3b7417159", ProtectionStatus.protect, ConnectionStatus.online, "windows", "sql",
		//		test);
		//String resourceID_5 = spogServer.createSourceWithCheck(spogServer.ReturnRandom(prefix), SourceType.office_365, SourceProduct.udp,
		//		"53761b6b-0b68-4a76-aeca-ad834f532d4c", "d191226b-c87c-4bb7-8283-10f3b7417159", ProtectionStatus.protect, ConnectionStatus.online, "windows", "sql",
		//		test);
		//String resourceID_6 = spogServer.createSourceWithCheck(spogServer.ReturnRandom(prefix), SourceType.shared_folder, SourceProduct.udp,
		//		"53761b6b-0b68-4a76-aeca-ad834f532d4c", "d191226b-c87c-4bb7-8283-10f3b7417159", ProtectionStatus.protect, ConnectionStatus.online, "windows", "sql",
		//		test);
		//String resourceID_7 = spogServer.createSourceWithCheck(spogServer.ReturnRandom(prefix), SourceType.virtual_standby, SourceProduct.udp,
		//		"53761b6b-0b68-4a76-aeca-ad834f532d4c", "d191226b-c87c-4bb7-8283-10f3b7417159", ProtectionStatus.protect, ConnectionStatus.online, "windows", "sql",
		//		test);
		
		String initial_direct_org_name = "spog_qa_direct_ui";
		String initial_direct_email = "spog_qa_direct_ui@arcserve.com";
		String initial_direct_first_name = "spog_qa_direct_ui";
		String initial_direct_last_name = "spog_qa_direct_ui";
	    String defaultDirectUserEmail = prefix + initial_direct_email;
		String defaultDirectUserFirstName = prefix + initial_direct_first_name;
		String defaultDirectUserLastName = prefix + initial_direct_last_name;
		String initial_direct_orgID = spogServer.CreateOrganizationWithCheck(prefix + initial_direct_org_name,
			SpogConstants.DIRECT_ORG, defaultDirectUserEmail, password, defaultDirectUserFirstName,
				defaultDirectUserLastName);
		test.log(LogStatus.INFO, "create MSP org");
		// create user
		test.log(LogStatus.INFO, "create direct admin user");
		// login user
		spogServer.userLogin(defaultDirectUserEmail, password);
		test.log(LogStatus.INFO, "login as direct admin user");
		String cloud_prefix = spogServer.ReturnRandom(prefix);
		String cloud_account_id=spogServer.createCloudAccountWithCheck("cloudAccountKey", "cloudAccountSecret", cloud_prefix+"cloudAccountName", "cloud_direct", initial_direct_orgID, 
				"SKUTESTDATA_1_0_0_0_"+cloud_prefix, "SKUTESTDATA_1_0_0_0_"+cloud_prefix, "SKUTESTDATA_1_0_0_0_"+cloud_prefix, "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
		String organizationID = initial_direct_orgID;
		//organizationID ="304c7b83-8ae8-49e1-88be-1b8c9ba9e366";
		// define the params for createSource
		//String cloud_account_id="bd349b9a-15a3-4133-b059-30b894b609f0";
		String spogToken = spogServer.getJWTToken();
		String userID = spogServer.GetLoggedinUser_UserID();
		String destinationID_1=UUID.randomUUID().toString();
		String destinationID_2=UUID.randomUUID().toString();
		//spogServer.userLogin(this.csrAdmin, this.csrPwd);
		String csrspogToken = spogServer.getJWTToken();
		String siteID=cloud_account_id;
		spogDestinationServer.setToken(csrspogToken);
		
//		spogDestinationServer.createDestination(destinationID_1, organizationID, cloud_account_id,
//				"91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", "cloud_dedupe_store", "running","9", "", "normal", qaName+"_1", "2M", "2M", 
//						"0","0", "31", "0", "2", "0","E:\\destination", "E:\\data", "E:\\index", "E:\\hash", "5", "true", "1", "512", "true", "123", "120", "300", "10", "20", "ddd", test); 
//		spogDestinationServer.createDestination(destinationID_2, organizationID, cloud_account_id,
//				"91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", "cloud_dedupe_store", "running","9", "", "normal", qaName+"_2", "2M", "2M", 
//						"0","0", "31", "0", "2", "0","E:\\destination", "E:\\data", "E:\\index", "E:\\hash", "5", "true", "1", "512", "true", "123", "120", "300", "10", "20", "ddd", test); 
//		
		String resource_name_1 = spogServer.ReturnRandom(prefix);
		String resource_name_2 = spogServer.ReturnRandom(prefix);
		//String resourceID_1 = spogServer.createSourceWithCheck(resource_name_1, SourceType.machine, SourceProduct.udp,
		//		organizationID, siteID, ProtectionStatus.protect, ConnectionStatus.online, "windows", "SQLSERVER",
		//		test);
		String resourceID_1 = spogServer.createSourceWithCheck(resource_name_1, SourceType.machine, SourceProduct.udp,
				organizationID, siteID, ProtectionStatus.protect, ConnectionStatus.online, "windows", "sql",
				test);
		String resourceID_2 = spogServer.createSourceWithCheck(resource_name_2, SourceType.machine, SourceProduct.udp,
				organizationID, siteID, ProtectionStatus.protect, ConnectionStatus.online, "windows", "exchange",
				test);
		//String resourceID_1 = spogServer.createSourceWithCheck(resource_name_1,SourceType.machine,  SourceProduct.udp,  organizationID, siteID, ProtectionStatus.protect, ConnectionStatus.online,
			//	OSMajor.windows.name(),	"exchange",   "test_vm1",  UUID.randomUUID().toString(), qaName+"_agent1", "windows 2012", "64" ,"1.0.0","2.0", "http://upgrade", null);
		//String resource_name_2 = spogServer.ReturnRandom(prefix);
		//String resourceID_2 = spogServer.createSourceWithCheck(resource_name_2,SourceType.machine,  SourceProduct.udp,  organizationID, siteID, ProtectionStatus.protect, ConnectionStatus.online,
		//	OSMajor.windows.name(),	"sql;exchange",   "test_vm2",  UUID.randomUUID().toString(), qaName+"_agent2", "windows 2012", "64" ,"1.0.0","2.0", "http://upgrade", null);
		
		// define the params for postJobs;
		long startTimeTS = System.currentTimeMillis();
		String jobType = "backup";
		String jobMethod = "incremental";
		String jobStatus = "finished";
		long endTimeTS = System.currentTimeMillis();
		String serverID = UUID.randomUUID().toString();
		String rpsID = UUID.randomUUID().toString();
		String policyID = UUID.randomUUID().toString();
		
		// define the params for postJobData;
		String jobSeq = "1000";
		String severity = "information";
		String percentComplete = "20";
		String protectedDataSize = "12000";
		String rawDataSize = "13000";
		String syncReadSize = "14000";
		String ntfsVolumeSize = "15000";
		String virtualDiskProvisionSize = "16000";

		
		// test login site (POST sites/:id/login)
		test.log(LogStatus.INFO, "login a site");
		
		Response response = gatewayServer.LoginSite(cloud_account_id, "cloudAccountSecret", cloud_account_id, test);
		String token = response.then().extract().path("data.token");

		// test post job (POST jobs)
		test.log(LogStatus.INFO, "post job");
		String jobID_1 = gatewayServer.postJobWithCheck(startTimeTS, endTimeTS, organizationID, resourceID_1, resourceID_1,
				resourceID_1, destinationID_1, policyID, jobType, jobMethod, jobStatus, token, test);
		
		String jobID_2 = gatewayServer.postJobWithCheck(startTimeTS, endTimeTS, organizationID, resourceID_2, resourceID_2,
				resourceID_2, destinationID_2, policyID, jobType, jobMethod, jobStatus, token, test);

		// test post job data (POST jobs/:id/data)
		test.log(LogStatus.INFO, "post job data");
		gatewayServer.postJobDataWithCheck(jobID_1, jobSeq, severity, percentComplete, protectedDataSize, rawDataSize,
				syncReadSize, ntfsVolumeSize, virtualDiskProvisionSize, token, test);
		gatewayServer.postJobDataWithCheck(jobID_2, jobSeq, severity, percentComplete, protectedDataSize, rawDataSize,
				syncReadSize, ntfsVolumeSize, virtualDiskProvisionSize, token, test);


		// post log
		String message_date = "spog,QA" ;
		String SeverityType = "information";
		String logSourceType = "spog";
		String message_id = "testLogMessage";
		String log_id_1 = gatewayServer.createLogwithCheck(startTimeTS,jobID_1, organizationID, organizationID, resourceID_1,SeverityType,
				logSourceType, message_id, message_date, token, test);
		String log_id_2 = gatewayServer.createLogwithCheck(startTimeTS,jobID_2, organizationID, organizationID, resourceID_2,SeverityType,
				logSourceType, message_id, message_date, token, test);
		test.log(LogStatus.INFO, "Create log successfully with log id is:" + log_id_1);
		
		errorHandle.printDebugMessageInDebugFile("******************direct information start for "+qaName+"*******************");
		errorHandle.printDebugMessageInDebugFile("org type is direct");
		errorHandle.printDebugMessageInDebugFile("org id:"+organizationID);
		errorHandle.printDebugMessageInDebugFile("direct admin email:"+defaultDirectUserEmail);
		errorHandle.printDebugMessageInDebugFile("direct admin password:"+password);
		errorHandle.printDebugMessageInDebugFile("direct admin user id:"+userID);
		errorHandle.printDebugMessageInDebugFile("direct admin user token:"+spogToken);
		errorHandle.printDebugMessageInDebugFile("cloud direct account id:"+cloud_account_id);
		errorHandle.printDebugMessageInDebugFile("site id:"+cloud_account_id);
		errorHandle.printDebugMessageInDebugFile("cloud direct account token:"+token);
		errorHandle.printDebugMessageInDebugFile("cloud direct account secret:cloudAccountSecret");
		errorHandle.printDebugMessageInDebugFile("source name 1:"+resource_name_1);
		errorHandle.printDebugMessageInDebugFile("source id 1:"+resourceID_1);
		errorHandle.printDebugMessageInDebugFile("source name 2:"+resource_name_2);
		errorHandle.printDebugMessageInDebugFile("source id 2:"+resourceID_2);
		errorHandle.printDebugMessageInDebugFile("destination id 1:"+destinationID_1);
		errorHandle.printDebugMessageInDebugFile("destination id 2:"+destinationID_2);
		errorHandle.printDebugMessageInDebugFile("job id 1:"+jobID_1);
		errorHandle.printDebugMessageInDebugFile("job id 2:"+jobID_2);
		errorHandle.printDebugMessageInDebugFile("log id 1:"+log_id_1);
		errorHandle.printDebugMessageInDebugFile("log id 2:"+log_id_2);
		errorHandle.printDebugMessageInDebugFile("******************direct information end for "+qaName+"*******************");
	}
	
	@Test(dataProvider = "createDirect")
	public void createMSPRelated(String prefix, String qaName) {
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		
		String initial_msp_org_name = "spog_qa_msp";
		String initial_msp_email = "spog_qa_msp@arcserve.com";
		String initial_msp_first_name = "spog_qa_msp";
		String initial_msp_last_name = "spog_qa_msp";
		String initial_msp_account_org_name = "spog_qa_msp_account";
	    String defaultMSPUserEmail = prefix + initial_msp_email;
		String defaultMSPUserFirstName = prefix + initial_msp_first_name;
		String defaultMSPUserLastName = prefix + initial_msp_last_name;
		String initial_msp_account_first_name = "spog_qa_msp_account";
		String initial_msp_account_last_name = "spog_qa_msp_account";
		String initial_msp_account_email = "spog_qa_msp_account@arcserve.com";
		String defaultMSPAccountUserEmail = prefix + initial_msp_account_email;
		String defaultMSPAccountUserFirstName = prefix + initial_msp_account_first_name;
		String defaultMSPAccountUserLastName = prefix + initial_msp_account_last_name;
		String initial_msp_orgID = spogServer.CreateOrganizationWithCheck(prefix + initial_msp_org_name,
			SpogConstants.MSP_ORG, defaultMSPUserEmail, password, defaultMSPUserFirstName,
			defaultMSPUserLastName);
		test.log(LogStatus.INFO, "create MSP org");
		// create user
		test.log(LogStatus.INFO, "create direct admin user");
		// login user
		spogServer.userLogin(defaultMSPUserEmail, password);
		test.log(LogStatus.INFO, "login as direct admin user");
		String organizationID = initial_msp_orgID;
		String account_id = spogServer.createAccountWithCheck(initial_msp_orgID, initial_msp_account_org_name, "",
				test);
		test.log(LogStatus.INFO, "create an account for msp");
		Response user_response = spogServer.createUser(defaultMSPAccountUserEmail, password, defaultMSPAccountUserFirstName,
				defaultMSPAccountUserLastName, SpogConstants.DIRECT_ADMIN, account_id, test);
		String msp_account_user_id = spogServer.checkCreateUser(user_response, SpogConstants.SUCCESS_POST, defaultMSPAccountUserEmail,
				defaultMSPAccountUserFirstName, defaultMSPAccountUserLastName, SpogConstants.DIRECT_ADMIN, account_id, "", test);
		test.log(LogStatus.INFO, "create account admin user");
		String cloud_prefix = spogServer.ReturnRandom(prefix);
		String cloud_account_id=spogServer.createCloudAccountWithCheck("cloudAccountKey", "cloudAccountSecret", cloud_prefix+"cloudAccountName", "cloud_direct", initial_direct_orgID, 
				"SKUTESTDATA_1_0_0_0_"+cloud_prefix, "SKUTESTDATA_1_0_0_0_"+cloud_prefix, "SKUTESTDATA_1_0_0_0_"+cloud_prefix, "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
		
		//organizationID ="304c7b83-8ae8-49e1-88be-1b8c9ba9e366";
		// define the params for createSource
		//String cloud_account_id="bd349b9a-15a3-4133-b059-30b894b609f0";
		String spogToken = spogServer.getJWTToken();
		String userID = spogServer.GetLoggedinUser_UserID();
		String destinationID_1=UUID.randomUUID().toString();
		String destinationID_2=UUID.randomUUID().toString();
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		String csrspogToken = spogServer.getJWTToken();
		spogDestinationServer.setToken(csrspogToken);
//		spogDestinationServer.createDestination(destinationID_1, organizationID, cloud_account_id,
//				"91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", "cloud_dedupe_store", "running","9", "", "normal", qaName+"_1", "2M", "2M", 
//						"0","0", "31", "0", "2", "0","E:\\destination", "E:\\data", "E:\\index", "E:\\hash", "5", "true", "1", "512", "true", "123", "120", "300", "10", "20", "ddd", test); 
//		spogDestinationServer.createDestination(destinationID_2, organizationID, cloud_account_id,
//				"91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", "cloud_dedupe_store", "running","9", "", "normal", qaName+"_2", "2M", "2M", 
//						"0","0", "31", "0", "2", "0","E:\\destination", "E:\\data", "E:\\index", "E:\\hash", "5", "true", "1", "512", "true", "123", "120", "300", "10", "20", "ddd", test); 
//		
		// define the params for postJobs;
		long startTimeTS = System.currentTimeMillis();
		String jobType = "backup";
		String jobMethod = "incremental";
		String jobStatus = "finished";
		long endTimeTS = System.currentTimeMillis();
		String serverID = UUID.randomUUID().toString();
		String rpsID = UUID.randomUUID().toString();
		String policyID = UUID.randomUUID().toString();
		
		// define the params for postJobData;
		String jobSeq = "1000";
		String severity = "information";
		String percentComplete = "20";
		String protectedDataSize = "12000";
		String rawDataSize = "13000";
		String syncReadSize = "14000";
		String ntfsVolumeSize = "15000";
		String virtualDiskProvisionSize = "16000";

		
		// test login site (POST sites/:id/login)
		test.log(LogStatus.INFO, "login a site");
		String siteID=cloud_account_id;
		Response response = gatewayServer.LoginSite(cloud_account_id, "cloudAccountSecret", cloud_account_id, test);
		String token = response.then().extract().path("data.token");

		// test post job (POST jobs)
		test.log(LogStatus.INFO, "post job");
		String resource_name_1 = spogServer.ReturnRandom(prefix);
		//String resourceID_1 = spogServer.createSourceWithCheck(resource_name_1, SourceType.machine, SourceProduct.udp,
		//		organizationID, siteID, ProtectionStatus.protect, ConnectionStatus.online, "windows", "SQLSERVER",
		//		test);
		//String resourceID_1 = spogServer.createSourceWithCheck(resource_name_1,SourceType.machine,  SourceProduct.udp,  organizationID, siteID, ProtectionStatus.protect, ConnectionStatus.online,
		//		OSMajor.windows.name(),	"exchange",   "test_vm1",  UUID.randomUUID().toString(), qaName+"_agent1", "windows 2012", "64" ,"1.0.0","2.0", "http://upgrade", null);
		String resource_name_2 = spogServer.ReturnRandom(prefix);
		//String resourceID_2 = spogServer.createSourceWithCheck(resource_name_2,SourceType.machine,  SourceProduct.udp,  organizationID, siteID, ProtectionStatus.protect, ConnectionStatus.online,
		//	OSMajor.windows.name(),	"sql;exchange",   "test_vm2",  UUID.randomUUID().toString(), qaName+"_agent2", "windows 2012", "64" ,"1.0.0","2.0", "http://upgrade", null);
		
		String resourceID_1 = spogServer.createSourceWithCheck(resource_name_1, SourceType.machine, SourceProduct.udp,
				organizationID, siteID, ProtectionStatus.protect, ConnectionStatus.online, "windows", "sql",
				test);
		String resourceID_2 = spogServer.createSourceWithCheck(resource_name_2, SourceType.machine, SourceProduct.udp,
				organizationID, siteID, ProtectionStatus.protect, ConnectionStatus.online, "windows", "exchange",
				test);
		String jobID_1 = gatewayServer.postJobWithCheck(startTimeTS, endTimeTS, organizationID, resourceID_1, resourceID_1,
				resourceID_1, destinationID_1, policyID, jobType, jobMethod, jobStatus, token, test);
		
		String jobID_2 = gatewayServer.postJobWithCheck(startTimeTS, endTimeTS, organizationID, resourceID_2, resourceID_2,
				resourceID_2, destinationID_2, policyID, jobType, jobMethod, jobStatus, token, test);

		// test post job data (POST jobs/:id/data)
		test.log(LogStatus.INFO, "post job data");
		gatewayServer.postJobDataWithCheck(jobID_1, jobSeq, severity, percentComplete, protectedDataSize, rawDataSize,
				syncReadSize, ntfsVolumeSize, virtualDiskProvisionSize, token, test);
		gatewayServer.postJobDataWithCheck(jobID_2, jobSeq, severity, percentComplete, protectedDataSize, rawDataSize,
				syncReadSize, ntfsVolumeSize, virtualDiskProvisionSize, token, test);


		// post log
		String message_date = "spog,QA" ;
		String SeverityType = "information";
		String logSourceType = "spog";
		String message_id = "testLogMessage";
		
		String log_id_1 = gatewayServer.createLogwithCheck(startTimeTS,jobID_1, organizationID, organizationID, resourceID_1,SeverityType,
				logSourceType, message_id, message_date, token, test);
		String log_id_2 = gatewayServer.createLogwithCheck(startTimeTS,jobID_2, organizationID, organizationID, resourceID_2,SeverityType,
				logSourceType, message_id, message_date, token, test);
		test.log(LogStatus.INFO, "Create log successfully with log id is:" + log_id_1);
		errorHandle.printDebugMessageInDebugFile("******************MSP information start for "+qaName+"*******************");
		errorHandle.printDebugMessageInDebugFile("org type is msp");
		errorHandle.printDebugMessageInDebugFile("org id:"+organizationID);
		errorHandle.printDebugMessageInDebugFile("msp account id:"+account_id);
		errorHandle.printDebugMessageInDebugFile("MSP admin email:"+defaultMSPUserEmail);
		errorHandle.printDebugMessageInDebugFile("MSP admin password:"+password);
		errorHandle.printDebugMessageInDebugFile("MSP admin id:"+userID);
		errorHandle.printDebugMessageInDebugFile("MSP admin user token:"+spogToken);
		errorHandle.printDebugMessageInDebugFile("MSP account admin email:"+defaultMSPAccountUserEmail);
		errorHandle.printDebugMessageInDebugFile("MSP account admin password:"+password);
		errorHandle.printDebugMessageInDebugFile("cloud direct account id:"+cloud_account_id);
		errorHandle.printDebugMessageInDebugFile("site id"+cloud_account_id);
		errorHandle.printDebugMessageInDebugFile("cloud direct account token:"+token);
		errorHandle.printDebugMessageInDebugFile("cloud direct account secret:cloudAccountSecret");
		errorHandle.printDebugMessageInDebugFile("source name 1:"+resource_name_1);
		errorHandle.printDebugMessageInDebugFile("source id 1:"+resourceID_1);
		errorHandle.printDebugMessageInDebugFile("source name 2:"+resource_name_2);
		errorHandle.printDebugMessageInDebugFile("source id 2:"+resourceID_2);
		errorHandle.printDebugMessageInDebugFile("destination id 1:"+destinationID_1);
		errorHandle.printDebugMessageInDebugFile("destination id 2:"+destinationID_2);
		errorHandle.printDebugMessageInDebugFile("job id 1:"+jobID_1);
		errorHandle.printDebugMessageInDebugFile("job id 2:"+jobID_2);
		errorHandle.printDebugMessageInDebugFile("log id 1:"+log_id_1);
		errorHandle.printDebugMessageInDebugFile("log id 2:"+log_id_2);
		errorHandle.printDebugMessageInDebugFile("******************MSP information end for "+qaName+"*******************");
	}
}
