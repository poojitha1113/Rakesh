package api.jobs;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Base64.Base64Coder;
import Constants.ConnectionStatus;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.SPOGServer;
import InvokerServer.ServerResponseCode;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.mozilla.javascript.ObjArray;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

public class PostJobDataTest extends base.prepare.Is4Org{
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private String csrAdmin;
	private String csrPwd;
//	private ExtentReports rep;
	private ExtentTest test;
	private String initial_msp_org_name = "spog_qa_msp_zhaoguo";
	private String initial_msp_email = "spog_qa_msp_zhaoguo@arcserve.com";
	private String initial_msp_email_full = "";
	private String initial_msp_first_name = "spog_qa_msp_ma";
	private String initial_msp_last_name = "spog_qa_msp_zhaoguo";
	
	private String initial_msp_email_added = "spog_qa_msp_zhaoguo_added@arcserve.com";
	private String initial_msp_email_full_added = "";
	private String initial_msp_first_name_added = "spog_qa_msp_ma";
	private String initial_msp_last_name_added = "spog_qa_msp_zhaoguo";


	private String initial_direct_org_name = "spog_qa_direct_zhaoguo";
	private String initial_direct_email = "spog_qa_direct_zhaoguo@arcserve.com";
	private String initial_direct_email_full = "";
	private String initial_direct_first_name = "spog_qa_direct_ma";
	private String initial_direct_last_name = "spog_qa_direct_zhaoguo2";
	
	private String initial_direct_email_added = "spog_qa_direct_zhaoguo_added@arcserve.com";
	private String initial_direct_email_full_added = "";
	private String initial_direct_first_name_added = "spog_qa_direct_ma";
	private String initial_direct_last_name_added = "spog_qa_direct_zhaoguo2";

	private String initial_sub_org_name_a = "spog_qa_sub_zhaoguo_a";
	private String initial_sub_email_a = "spog_qa_sub_zhaoguo_a@arcserve.com";
	private String initial_sub_email_full_a = "";
	private String initial_sub_first_name_a = "spog_qa_sub_ma_a";
	private String initial_sub_last_name_a = "spog_qa_sub_zhaoguo_a";
	
	private String initial_sub_email_a_added = "spog_qa_sub_zhaoguo_a_added@arcserve.com";
	private String initial_sub_email_full_a_added = "";
	private String initial_sub_first_name_a_added = "spog_qa_sub_ma_a";
	private String initial_sub_last_name_a_added = "spog_qa_sub_zhaoguo_a";

	private String initial_msp_orgID;
	private String initial_direct_orgID;
	private String initial_sub_orgID_a;

	private String password = "Pa$$w0rd";

//	private SQLServerDb bqdb1;
//	public int Nooftest;
//	private long creationTime;
//	private String BQName = null;
//	private String runningMachine;
//	private testcasescount count1;
//	private String buildVersion;
	private String uuid1 = UUID.randomUUID().toString();
	private String uuid2 = UUID.randomUUID().toString();
	private String siteToken;
	private String siteID;
	private String initial_resourceID;
	private String initial_udpResourceID;
	private String  org_model_prefix=this.getClass().getSimpleName();
	
	@BeforeClass
	@Parameters({ "baseURI", "port", "csrAdminUserName", "csrAdminPassword", "logFolder", "runningMachine",
			"buildVersion" })
	public void beforeClass(String baseURI, String port, String csrAdminUserName, String csrAdminPassword,
			String logFolder, String runningMachine, String buildVersion) {
		rep = ExtentManager.getInstance("GetSpecificFilterFromSpecificUsersTest", logFolder);
		test = rep.startTest("initializing data...");

		this.BQName = this.getClass().getSimpleName();
		String author = "Zhaoguo.Ma";
		this.runningMachine = runningMachine;
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date = new java.util.Date();
		this.buildVersion = buildVersion + "_" + dateFormater.format(date);
		Nooftest = 0;
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		if (count1.isstarttimehit() == 0) {
			System.out.println("Into get loggedInUserById");
			creationTime = System.currentTimeMillis();
			count1.setcreationtime(creationTime);
			// creationTime = System.currentTimeMillis();
			try {
				bqdb1.updateTable(BQName, runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0",
						String.valueOf(Nooftest), count1.getcreationtime(), "InProgress", author + " and Rest server is "+baseURI.split("//")[1]);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		this.csrAdmin = csrAdminUserName;
		this.csrPwd = csrAdminPassword;
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		String prefix = RandomStringUtils.randomAlphanumeric(8);

		// create msp organization #1, and then 1 sub-organizations in it, create one
		// admin for the sub organization;
		this.initial_msp_email_full = prefix + this.initial_msp_email;
		this.initial_msp_orgID = spogServer.CreateOrganizationWithCheck(prefix + initial_msp_org_name + org_model_prefix,
				SpogConstants.MSP_ORG, prefix + initial_msp_email, password, prefix + initial_msp_first_name,
				prefix + initial_msp_last_name);
		
		this.initial_msp_email_full_added = prefix + this.initial_msp_email_added;
		spogServer.createUserAndCheck(initial_msp_email_full_added, password, prefix+ initial_msp_first_name_added, 
				prefix+ initial_msp_last_name, "msp_admin", initial_msp_orgID, test);

		this.initial_sub_orgID_a = spogServer.createAccountWithCheck(initial_msp_orgID, initial_sub_org_name_a,
				initial_msp_orgID);

		spogServer.userLogin(initial_msp_email_full, password);
		this.initial_sub_email_full_a = prefix + initial_sub_email_a;
		spogServer.createUserAndCheck(prefix + this.initial_sub_email_a, password,
				prefix + this.initial_sub_first_name_a, prefix + this.initial_sub_last_name_a,
				SpogConstants.DIRECT_ADMIN, initial_sub_orgID_a, test);
		
		this.initial_sub_email_full_a_added = prefix + this.initial_sub_email_a_added;
		spogServer.createUserAndCheck(prefix + this.initial_sub_email_a_added, password,
				prefix + this.initial_sub_first_name_a_added, prefix + this.initial_sub_last_name_a_added,
				SpogConstants.DIRECT_ADMIN, initial_sub_orgID_a, test);

		// create 1 direct organizations;
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		this.initial_direct_email_full = prefix + this.initial_direct_email;
		this.initial_direct_orgID = spogServer.CreateOrganizationWithCheck(prefix + initial_direct_org_name + org_model_prefix,
				SpogConstants.DIRECT_ORG, prefix + initial_direct_email, password, prefix + initial_direct_first_name,
				prefix + initial_direct_last_name);
		
		this.initial_direct_email_full_added = prefix + this.initial_direct_email_added;
		spogServer.createUserAndCheck(prefix + this.initial_direct_email_added, password,
				prefix + this.initial_direct_first_name_added, prefix + this.initial_direct_last_name_added,
				SpogConstants.DIRECT_ADMIN, initial_direct_orgID, test);
		
		// create a site
		spogServer.userLogin(initial_direct_email_full_added, password);
		String spogToken = spogServer.getJWTToken();
		String userID = spogServer.GetLoggedinUser_UserID();
		String siteName = RandomStringUtils.randomAlphanumeric(8);
		String siteType = "gateway";
		String orgID = initial_direct_orgID;
		Response response = spogServer.createSite(siteName, siteType, orgID, spogToken, test);
		
		Map<String,String > sitecreateResMap = new HashMap<>();
		sitecreateResMap=spogServer.checkCreateSite(response, SpogConstants.SUCCESS_POST,siteName, siteType,
				orgID,userID,"",test);
		
		String registrationBasecode = sitecreateResMap.get("registration_basecode");
		siteID = sitecreateResMap.get("site_id");
		String siteRegistrationKey = "";
		try {
			String decoded = URLDecoder.decode( registrationBasecode.trim(), "UTF-8" );
			Base64Coder base64 = new Base64Coder();
			String decrypted = base64.decode( decoded );
			String[] parts    = decrypted.split( "\\n", -2 );
			siteRegistrationKey = parts[1];
		} catch(UnsupportedEncodingException e ){
			test.log(LogStatus.FAIL,"The value of the error Message :"+e.getMessage());
		}
		
		String gatewayID = UUID.randomUUID().toString();
		String gatewayHostname = RandomStringUtils.randomAlphanumeric(8);
		String siteVersion = "";
		response = gatewayServer.RegisterSite(siteRegistrationKey,gatewayID,gatewayHostname,siteVersion,siteID,test);
		String site_secret = gatewayServer.checkRegisterSite(response, ServerResponseCode.Succes_Login, siteID, siteName, siteType, orgID, userID, true,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
		response = gatewayServer.LoginSite(siteID, site_secret, gatewayID, test);
		this.siteToken = response.then().extract().path("data.token");
		
		this.initial_resourceID = spogServer.createSourceWithCheck(prefix + "sourceName", SourceType.machine, SourceProduct.cloud_direct,
				orgID, siteID, ProtectionStatus.unprotect, ConnectionStatus.online, "windows", "SQL_SERVER", test);
		this.initial_udpResourceID = spogServer.createSourceWithCheck(prefix + "udpSourceName", SourceType.machine, SourceProduct.udp,
				orgID, siteID, ProtectionStatus.unprotect, ConnectionStatus.online, "windows", "SQL_SERVER", test);
	}

	@DataProvider(name = "post_job_data_valid_params")
	public final Object[][] postJobDataValidParams(){
		return new Object[][] {
			{initial_direct_orgID, initial_resourceID, "backup", "incremental", "finished", "20", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),
				"300", "1", "2", "information", "finished", "13000", "14000", "15000", "16000", "400", "none", "none", "none"}, 
			{initial_direct_orgID, initial_resourceID, "backup", "incremental", "finished", "none", "none", "none", "none", "none", "none", "none", "none", "none", "none", "none", "none", "none", "none", "none", "none"}, 
			{initial_direct_orgID, initial_resourceID, "backup", "incremental", "finished", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "", ""}, 
			{initial_direct_orgID, initial_resourceID, "backup", "incremental", "finished", null, null, null,	null, null, null, null, null, null, null, null, null, null, null, null, null}, 
			{initial_direct_orgID, initial_resourceID, "backup", "none", "finished", "20", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),
				"300", "1", "2", "information", "finished", "13000", "14000", "15000", "16000", "400", "none", "none", "none"},
			{initial_direct_orgID, initial_resourceID, "restore", "none", "finished", "20", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),
				"300", "1", "2", "information", "finished", "13000", "14000", "15000", "16000", "400", initial_resourceID, "restore_data_location", "restore_data_path"},
			{initial_direct_orgID, initial_resourceID, "restore", "none", "finished", "20", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),
				"300", "1", "2", "error", "finished", "13000", "14000", "15000", "16000", "400", initial_resourceID, "restore_data_location", "restore_data_path"},
			{initial_direct_orgID, initial_resourceID, "restore", "none", "finished", "20", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),
				"300", "1", "2", "warning", "finished", "13000", "14000", "15000", "16000", "400", initial_resourceID, "restore_data_location", "restore_data_path"},
			{initial_direct_orgID, initial_resourceID, "restore", "none", "finished", "20", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),
				"300", "1", "2", "warning", "canceled", "13000", "14000", "15000", "16000", "400", initial_resourceID, "restore_data_location", "restore_data_path"},
		};
	}
	
	@Test (dataProvider = "post_job_data_valid_params")
	public void postJobDataTest(String orgniazationID, String resourceID, String jobType, String jobMethod, String jobStatus, 
			String percentComplete, String startTimeTS, String endTimeTS, String elapsedTime, String errorCount, String warningCount, String severity,
			String jobDataJobStatus, String processedBytesProcessed, String processedBytesChanged, String processedFiles, 
			String transferredUncompressedBytes, String backupThroughput, String restoreDataSource, String restoreDataLocation, String restoreDataPath){
		String job_id = UUID.randomUUID().toString();
				gatewayServer.postJobWithCheck(job_id, System.currentTimeMillis()/1000, System.currentTimeMillis()/1000, orgniazationID, UUID.randomUUID().toString(), resourceID,
				UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), jobType, jobMethod, jobStatus, this.siteToken, test);
		gatewayServer.postJobDataForCDWithCheck(job_id, percentComplete, startTimeTS, endTimeTS, elapsedTime, errorCount, warningCount,
				severity, jobDataJobStatus, processedBytesProcessed, processedBytesChanged, processedFiles, transferredUncompressedBytes, backupThroughput, 
				restoreDataSource, restoreDataLocation, restoreDataPath, siteToken, test);
	}
	
	@DataProvider(name = "post_job_data_twice")
	public final Object[][] postJobDataTwiceParams() {
		return new Object[][] {
			{initial_direct_orgID, initial_resourceID, "backup", "none", "finished", "20", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),
				"300", "1", "2", "information", "finished", "13000", "14000", "15000", "16000", "400", "none", "none", "none", 400, "00600005"},
			};}
	
//	@Test(dataProvider = "post_job_data_twice")
	public void postJobDataTwiceTest(String orgniazationID, String resourceID, String jobType, String jobMethod, String jobStatus, 
			String percentComplete, String startTimeTS, String endTimeTS, String elapsedTime, String errorCount, String warningCount, String severity,
			String jobDataJobStatus, String processedBytesProcessed, String processedBytesChanged, String processedFiles, 
			String transferredUncompressedBytes, String backupThroughput, String restoreDataSource, String restoreDataLocation, String restoreDataPath, int statusCode, String Errorcode) {
		String job_id = UUID.randomUUID().toString(); 
				gatewayServer.postJobWithCheck(job_id, System.currentTimeMillis(), System.currentTimeMillis(), orgniazationID, UUID.randomUUID().toString(), resourceID,
				UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), jobType, jobMethod, jobStatus, this.siteToken, test);
		gatewayServer.postJobDataForCDWithCheck(job_id, percentComplete, startTimeTS, endTimeTS, elapsedTime, errorCount, warningCount,
				severity, jobDataJobStatus, processedBytesProcessed, processedBytesChanged, processedFiles, transferredUncompressedBytes, backupThroughput, 
				restoreDataSource, restoreDataLocation, restoreDataPath, siteToken, test);
		gatewayServer.postJobDataForCDWithErrorCheck(job_id, percentComplete, startTimeTS, endTimeTS, elapsedTime, errorCount, warningCount,
				severity, jobDataJobStatus, processedBytesProcessed, processedBytesChanged, processedFiles, transferredUncompressedBytes, backupThroughput, 
				restoreDataSource, restoreDataLocation, restoreDataPath, siteToken, statusCode, Errorcode, test);
	}
	
	@DataProvider(name = "post_job_data_no_token")
	public final Object[][] postJobDataWithoutToken() {
		return new Object[][] {
			{initial_direct_orgID, initial_resourceID, "backup", "none", "finished", "20", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),
				"300", "1", "2", "information", "finished", "13000", "14000", "15000", "16000", "400", "none", "none", "none", 401, "00900006"},

			};}
	
	@Test(dataProvider = "post_job_data_no_token")
	public void postJobDataWithoutTokenTest(String orgniazationID, String resourceID, String jobType, String jobMethod, String jobStatus, 
			String percentComplete, String startTimeTS, String endTimeTS, String elapsedTime, String errorCount, String warningCount, String severity,
			String jobDataJobStatus, String processedBytesProcessed, String processedBytesChanged, String processedFiles, 
			String transferredUncompressedBytes, String backupThroughput, String restoreDataSource, String restoreDataLocation, String restoreDataPath, int statusCode, String Errorcode)  {
		String job_id = UUID.randomUUID().toString();
		gatewayServer.postJobWithCheck(job_id, System.currentTimeMillis(), System.currentTimeMillis(), orgniazationID, UUID.randomUUID().toString(), resourceID,
				UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), jobType, jobMethod, jobStatus, this.siteToken, test);
		
		gatewayServer.postJobDataForCDWithErrorCheck(job_id, percentComplete, startTimeTS, endTimeTS, elapsedTime, errorCount, warningCount,
				severity, jobDataJobStatus, processedBytesProcessed, processedBytesChanged, processedFiles, transferredUncompressedBytes, backupThroughput, 
				restoreDataSource, restoreDataLocation, restoreDataPath, "", statusCode, Errorcode, test);
	}
	
	@DataProvider(name = "post_job_data_invalid_jobID")
	public final Object[][] postJobDataInvalidJobIDParams() {
		return new Object[][] {
			{initial_direct_orgID, initial_resourceID, "backup", "none", "finished", "20", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),
				"300", "1", "2", "information", "finished", "13000", "14000", "15000", "16000", "400", "none", "none", "none"},

			};}
	@Test (dataProvider = "post_job_data_invalid_jobID")
	public void postJobDataInvalidJobID(String orgniazationID, String resourceID, String jobType, String jobMethod, String jobStatus, 
			String percentComplete, String startTimeTS, String endTimeTS, String elapsedTime, String errorCount, String warningCount, String severity,
			String jobDataJobStatus, String processedBytesProcessed, String processedBytesChanged, String processedFiles, 
			String transferredUncompressedBytes, String backupThroughput, String restoreDataSource, String restoreDataLocation, String restoreDataPath) {
		
//		gatewayServer.postJobDataForCDWithErrorCheck(UUID.randomUUID().toString(), percentComplete, startTimeTS, endTimeTS, elapsedTime, errorCount, warningCount,
//				severity, jobDataJobStatus, processedBytesProcessed, processedBytesChanged, processedFiles, transferredUncompressedBytes, backupThroughput, 
//				restoreDataSource, restoreDataLocation, restoreDataPath, siteToken, 404, "00600003", test);
		gatewayServer.postJobDataForCDWithErrorCheck("invalid_uuid", percentComplete, startTimeTS, endTimeTS, elapsedTime, errorCount, warningCount,
				severity, jobDataJobStatus, processedBytesProcessed, processedBytesChanged, processedFiles, transferredUncompressedBytes, backupThroughput, 
				restoreDataSource, restoreDataLocation, restoreDataPath, siteToken, 400, "40000005", test);
	}
	
	@DataProvider(name = "post_replication_in_job_data")
	public final Object[][] postReplicationJobDataParams() {
		return new Object[][] {
			{initial_direct_orgID, initial_udpResourceID, "rps_replicate_in_bound", "none", "active", "phase", "20", "start_time_ts", "elapsed_time", "estimated_time_remaining", 
				"protection", "compression_level", "target_recovery_point_server", "target_data_store_name", "current_session", "target_cloud_hybrid_store", 
				"start_session", "end_session", "saved_bandwidth_percentage", "source_recovery_point_server", 
				"source_data_store_name", "network_throttle", "pysical_throughput", "logical_throughput", "job_seq"},
			{initial_direct_orgID, initial_udpResourceID, "rps_replicate_in_bound", "none", "active", "none", "none", "none", "none", "none", 
				"none", "none", "none", "none", "none", "none", "none", "none", "none", "none",	"none", "none", "none", "none", "none"},
			{initial_direct_orgID, initial_udpResourceID, "rps_replicate_in_bound", "none", "active", null, null, null, null, null, null, null, null, null, null, null, null, 
					null, null, null, null, null, null, null, null},
			{initial_direct_orgID, initial_udpResourceID, "rps_replicate_in_bound", "none", "active", "", "", "", "", "", "", "", "", "", "", "", "", "", "", "",	"", "", "", "", ""},
			};}

//	@Test(dataProvider = "post_replication_in_job_data")
	public void postReplicationJobDataTest(String orgniazationID, String resourceID, String jobType, String jobMethod, String jobStatus, 
			String phase, String percent_complete, String start_time_ts, String elapsed_time,
			String estimated_time_remaining, String protection, String compression_level, String target_recovery_point_server, String target_data_store_name, 
			String current_session, String target_cloud_hybrid_store, String start_session, String end_session, String saved_bandwidth_percentage, 
			String source_recovery_point_server, String source_data_store_name, String network_throttle, String pysical_throughput, String logical_throughput, String job_seq)  {
		
		String job_id = gatewayServer.postJobWithCheck(System.currentTimeMillis(), System.currentTimeMillis(), orgniazationID, UUID.randomUUID().toString(), resourceID,
				UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(), jobType, jobMethod, jobStatus, this.siteToken, test);
		
		gatewayServer.postReplicationJobDataWithCheck(job_id, phase, percent_complete, start_time_ts, elapsed_time, estimated_time_remaining, protection, 
				compression_level, target_recovery_point_server, target_data_store_name, current_session, target_cloud_hybrid_store, 
				start_session, end_session, saved_bandwidth_percentage, source_recovery_point_server, 
				source_data_store_name, network_throttle, pysical_throughput, logical_throughput, job_seq, this.siteToken, test);
				
	}
	
	@AfterMethod
	public void afterMethodTest() {
		rep.endTest(test);
		rep.flush();
	}

	@AfterMethod
	public void getResult(ITestResult result) {
		if (result.getStatus() == ITestResult.FAILURE) {
			count1.setfailedcount();
			test.log(LogStatus.FAIL, "Test Case Failed is " + result.getName() + " with parameters as "
					+ Arrays.asList(result.getParameters()));
			test.log(LogStatus.FAIL, result.getThrowable().getMessage());
		} else if (result.getStatus() == ITestResult.SKIP) {
			count1.setskippedcount();
			test.log(LogStatus.SKIP, "Test Case Skipped is " + result.getName());
		} else if (result.getStatus() == ITestResult.SUCCESS) {
			count1.setpassedcount();
		}
		// ending test
		// endTest(logger) : It ends the current test and prepares to create HTML report
		rep.endTest(test);
	}
}
