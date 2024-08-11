package api.jobs;

import org.testng.annotations.Test;

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
import bsh.org.objectweb.asm.Constants;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import groovyjarjarantlr.PreservingFileWriter;
import io.restassured.response.Response;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;

import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.interfaces.RSAMultiPrimePrivateCrtKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import Constants.SpogConstants;

import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;

public class PostJobTest extends base.prepare.Is4Org{
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private String csrAdmin;
	private String csrPwd;
//	private ExtentReports rep;
	private ExtentTest test;
	
	private String csr_readonly_email = "csr_readonly_zhaoguo@arcserve.com";
	private String csr_readonly_password = "Caworld_2017";

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
		
	}

	@DataProvider(name = "post_job_data")
	public final Object[][] postJobDataParams() {
		return new Object[][] { 
				// different siteType
				{ initial_direct_email_full, password, initial_direct_orgID, RandomStringUtils.randomAlphanumeric(8),
						"gateway", UUID.randomUUID().toString(), RandomStringUtils.randomAlphanumeric(8), "",
						System.currentTimeMillis(), System.currentTimeMillis(), initial_direct_orgID,
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup", "incremental", "finished" },
				{ initial_direct_email_full, password, initial_direct_orgID, RandomStringUtils.randomAlphanumeric(8),
						"gateway", UUID.randomUUID().toString(), RandomStringUtils.randomAlphanumeric(8), "",
						System.currentTimeMillis(), System.currentTimeMillis(), initial_direct_orgID,
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup", "incremental", "finished" },

				// different end_time_ts
				{ initial_direct_email_full, password, initial_direct_orgID, RandomStringUtils.randomAlphanumeric(8),
						"gateway", UUID.randomUUID().toString(), RandomStringUtils.randomAlphanumeric(8), "",
						System.currentTimeMillis(), 0, initial_direct_orgID, UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(),
						"backup", "incremental", "finished" },

				// different organization_id format
				{ initial_direct_email_full, password, initial_direct_orgID, RandomStringUtils.randomAlphanumeric(8),
						"gateway", UUID.randomUUID().toString(), RandomStringUtils.randomAlphanumeric(8), "",
						System.currentTimeMillis(), System.currentTimeMillis(), "none", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(),
						"backup", "incremental", "finished" },
				{ initial_direct_email_full, password, initial_direct_orgID, RandomStringUtils.randomAlphanumeric(8),
						"gateway", UUID.randomUUID().toString(), RandomStringUtils.randomAlphanumeric(8), "",
						System.currentTimeMillis(), System.currentTimeMillis(), null, UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(),
						"backup", "incremental", "finished" },
				{ initial_direct_email_full, password, initial_direct_orgID, RandomStringUtils.randomAlphanumeric(8),
						"gateway", UUID.randomUUID().toString(), RandomStringUtils.randomAlphanumeric(8), "",
						System.currentTimeMillis(), System.currentTimeMillis(), "", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(),
						"backup", "incremental", "finished" },

				// different server_id format
				{ initial_direct_email_full, password, initial_direct_orgID, RandomStringUtils.randomAlphanumeric(8),
						"gateway", UUID.randomUUID().toString(), RandomStringUtils.randomAlphanumeric(8), "",
						System.currentTimeMillis(), System.currentTimeMillis(), initial_direct_orgID, "none",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(),
						"backup", "incremental", "finished" },
				{ initial_direct_email_full, password, initial_direct_orgID, RandomStringUtils.randomAlphanumeric(8),
						"gateway", UUID.randomUUID().toString(), RandomStringUtils.randomAlphanumeric(8), "",
						System.currentTimeMillis(), System.currentTimeMillis(), initial_direct_orgID, null,
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(),
						"backup", "incremental", "finished" },
				{ initial_direct_email_full, password, initial_direct_orgID, RandomStringUtils.randomAlphanumeric(8),
						"gateway", UUID.randomUUID().toString(), RandomStringUtils.randomAlphanumeric(8), "",
						System.currentTimeMillis(), System.currentTimeMillis(), initial_direct_orgID, "",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(),
						"backup", "incremental", "finished" },

				// different rps_id format
				{ initial_direct_email_full, password, initial_direct_orgID, RandomStringUtils.randomAlphanumeric(8),
						"gateway", UUID.randomUUID().toString(), RandomStringUtils.randomAlphanumeric(8), "",
						System.currentTimeMillis(), System.currentTimeMillis(), initial_direct_orgID,
						UUID.randomUUID().toString(), "none", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup", "incremental", "finished" },
				{ initial_direct_email_full, password, initial_direct_orgID, RandomStringUtils.randomAlphanumeric(8),
						"gateway", UUID.randomUUID().toString(), RandomStringUtils.randomAlphanumeric(8), "",
						System.currentTimeMillis(), System.currentTimeMillis(), initial_direct_orgID,
						UUID.randomUUID().toString(), null, UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup", "incremental", "finished"},
				{ initial_direct_email_full, password, initial_direct_orgID, RandomStringUtils.randomAlphanumeric(8),
						"gateway", UUID.randomUUID().toString(), RandomStringUtils.randomAlphanumeric(8), "",
						System.currentTimeMillis(), System.currentTimeMillis(), initial_direct_orgID,
						UUID.randomUUID().toString(), "", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "incremental", "finished" },

				// different destination_id format
				{ initial_direct_email_full, password, initial_direct_orgID, RandomStringUtils.randomAlphanumeric(8),
						"gateway", UUID.randomUUID().toString(), RandomStringUtils.randomAlphanumeric(8), "",
						System.currentTimeMillis(), System.currentTimeMillis(), initial_direct_orgID,
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "none",
						UUID.randomUUID().toString(), "backup", "incremental", "finished" },
				{ initial_direct_email_full, password, initial_direct_orgID, RandomStringUtils.randomAlphanumeric(8),
						"gateway", UUID.randomUUID().toString(), RandomStringUtils.randomAlphanumeric(8), "",
						System.currentTimeMillis(), System.currentTimeMillis(), initial_direct_orgID,
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), null,
						UUID.randomUUID().toString(), "backup", "incremental", "finished"},
				{ initial_direct_email_full, password, initial_direct_orgID, RandomStringUtils.randomAlphanumeric(8),
						"gateway", UUID.randomUUID().toString(), RandomStringUtils.randomAlphanumeric(8), "",
						System.currentTimeMillis(), System.currentTimeMillis(), initial_direct_orgID,
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "", UUID.randomUUID().toString(), "backup", "incremental", "finished" },

				// different policy_id format
				{ initial_direct_email_full, password, initial_direct_orgID, RandomStringUtils.randomAlphanumeric(8),
						"gateway", UUID.randomUUID().toString(), RandomStringUtils.randomAlphanumeric(8), "",
						System.currentTimeMillis(), System.currentTimeMillis(), initial_direct_orgID,
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(),
						"none", "backup", "incremental", "finished" },
				{ initial_direct_email_full, password, initial_direct_orgID, RandomStringUtils.randomAlphanumeric(8),
						"gateway", UUID.randomUUID().toString(), RandomStringUtils.randomAlphanumeric(8), "",
						System.currentTimeMillis(), System.currentTimeMillis(), initial_direct_orgID,
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(),
						null, "backup", "incremental", "finished" },
				{ initial_direct_email_full, password, initial_direct_orgID, RandomStringUtils.randomAlphanumeric(8),
						"gateway", UUID.randomUUID().toString(), RandomStringUtils.randomAlphanumeric(8), "",
						System.currentTimeMillis(), System.currentTimeMillis(), initial_direct_orgID,
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), UUID.randomUUID().toString(),
						"", "backup", "incremental", "finished" },
		};
	}
	
//	@Test (dataProvider = "post_job_data")
	public void testPostJob(String userName, String password, String orgID, String siteName, String siteType, String gatewayID, String gatewayHostname, String siteVesion,
			long startTimeTS, long endTimeTS, String organizationID, String serverID, String rpsID, String destinationID, String policyID, String jobType, String jobMethod, String jobStatus) {
		spogServer.userLogin(userName, password);
		String spogToken = spogServer.getJWTToken();
		String userID = spogServer.GetLoggedinUser_UserID();
		
		Response response = spogServer.createSite(siteName, siteType, orgID, spogToken, test);
		
		Map<String,String > sitecreateResMap = new HashMap<>();
		sitecreateResMap=spogServer.checkCreateSite(response, SpogConstants.SUCCESS_POST,siteName, siteType,
				orgID,userID,"",test);
		
		String registrationBasecode = sitecreateResMap.get("registration_basecode");
		String siteID = sitecreateResMap.get("site_id");
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
		
		response = gatewayServer.RegisterSite(siteRegistrationKey,gatewayID,gatewayHostname,siteVesion,siteID,test);
		String site_secret = gatewayServer.checkRegisterSite(response, ServerResponseCode.Succes_Login, siteID, siteName, siteType, orgID, userID, true,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
		response = gatewayServer.LoginSite(siteID, site_secret, gatewayID, test);
		String token = response.then().extract().path("data.token");
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		String resourceID = spogServer.createSourceWithCheck(prefix + "sourceName", SourceType.machine, SourceProduct.udp,
				orgID, siteID, ProtectionStatus.protect, ConnectionStatus.online, "windows", "SQL_SERVER", test);
		
		gatewayServer.postJobWithCheck(startTimeTS, endTimeTS, organizationID, serverID, resourceID, rpsID, destinationID, policyID, jobType, jobMethod, jobStatus, token, test);
	}
	
	@DataProvider(name = "post_job_data_enhancement")
	public final Object[][] postJobDataEnhancementParams() {
		return new Object[][] { 
			//different job method
				{ "backup", "unknown", "finished" }, 
				{ "backup", "full", "finished" }, 
				{ "backup", "incremental", "finished" },
				{ "backup", "resync", "finished" },
				{ "backup_full", "none", "finished" },
				{ "backup_incremental", "none", "finished" },
				{ "backup_verified", "none", "finished" },
				{ "backup_full", "", "finished" },
				{ "backup_incremental", "", "finished" },
				{ "backup_verified", "", "finished" },
				{ "backup_full", "full", "finished" },
				{ "backup_incremental", "full", "finished" },
				{ "backup_verified", "full", "finished" },

				//different job status
//				{ "backup", "incremental", "active"},
				{ "backup", "incremental", "finished"},
//				{ "backup", "incremental", "canceled"},
//				{ "backup", "incremental", "failed"},
//				{ "backup", "incremental", "incomplete" },
				{ "backup", "incremental", "idle" },
				{ "backup", "incremental", "waiting"},
//				{ "backup", "incremental", "crash"},
//				{ "backup", "incremental", "license_failed"},
//				{ "backup", "incremental", "backupjob_proc_exit"},
//				{ "backup", "incremental", "skipped"},
				{ "backup", "incremental", "stop"},
				{ "backup", "incremental", "missed"},				
				
				//different job type
				{"restore", "", "finished" },
				{"copy", "", "finished" },
				{"merge", "", "finished" },
				{"conversion", "", "finished" },
				{"vm_backup", "", "finished" },
				{"vm_recovery", "", "finished" },
				{"vm_catalog_fs", "", "finished" },
				{"vm_restore_file_to_original", "", "finished" },
				{"vmware_vapp_backup", "", "finished" },
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
				{ "office365_backup", "", "finished" },
				{ "cifs_backup", "", "finished" },
				{ "sharepoint_backup", "", "finished" },
				{ "data_store", "", "finished" },
				};
	} 
	
	@Test (dataProvider = "post_job_data_enhancement")
	public void testPostJobEnhancement(String jobType, String jobMethod, String jobStatus) {
		spogServer.userLogin(initial_direct_email_full_added, password);
	
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		String resourceID = spogServer.createSourceWithCheck(prefix + "sourceName", SourceType.machine, SourceProduct.cloud_direct,
				initial_direct_orgID, siteID, ProtectionStatus.unprotect, ConnectionStatus.online, "windows", "SQL_SERVER", test);
		
		long startTimeTS = System.currentTimeMillis();
		long endTimeTS = System.currentTimeMillis();
		String serverID = UUID.randomUUID().toString();
		String rpsID = UUID.randomUUID().toString();
		String destinationID = UUID.randomUUID().toString();
		String policyID = UUID.randomUUID().toString();
		String jobId = UUID.randomUUID().toString();
		Response response = gatewayServer.postJobWithCheck(jobId, startTimeTS, endTimeTS, initial_direct_orgID, serverID, resourceID, rpsID, destinationID, policyID, jobType, jobMethod, jobStatus, siteToken, test);
		
	}
	
	@DataProvider(name = "post_job_with_jobid_enhancement")
	public final Object[][] postJobWithJobIDParams() {
		return new Object[][] { 
			//different job method
				{ "backup", "unknown", "finished", UUID.randomUUID().toString() },
				{ "backup", "unknown", "finished", null }, 
				{ "backup", "unknown", "finished", "none" }, 
				{ "backup", "unknown", "finished", "" }, 
		};}
	
//	@Test (dataProvider = "post_job_with_jobid_enhancement")
	public void testPostJobWithID(String jobType, String jobMethod, String jobStatus, String jobID) {
		spogServer.userLogin(initial_direct_email_full_added, password);
	
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		String resourceID = spogServer.createSourceWithCheck(prefix + "sourceName", SourceType.machine, SourceProduct.udp,
				initial_direct_orgID, siteID, ProtectionStatus.protect, ConnectionStatus.online, "windows", "SQL_SERVER", test);
		
		long startTimeTS = System.currentTimeMillis();
		long endTimeTS = System.currentTimeMillis();
		String serverID = UUID.randomUUID().toString();
		String rpsID = UUID.randomUUID().toString();
		String destinationID = UUID.randomUUID().toString();
		String policyID = UUID.randomUUID().toString();
		gatewayServer.postJobWithCheck(jobID, startTimeTS, endTimeTS, initial_direct_orgID, serverID, resourceID, rpsID, destinationID, policyID, jobType, jobMethod, jobStatus, siteToken, test);
	}
	
	@DataProvider(name = "post_job_with_jobid_duplicate")
	public final Object[][] postJobWithDuplicateJobIDParams() {
		return new Object[][] { 
			//different job method
				{ "backup", "unknown", "finished", UUID.randomUUID().toString(), 400, "00600006" },
				 
		};}
	
//	@Test (dataProvider = "post_job_with_jobid_duplicate")
	public void testPostJobWithDuplicateID(String jobType, String jobMethod, String jobStatus, String jobID, int statusCode, String errorCode) {
		spogServer.userLogin(initial_direct_email_full_added, password);
	
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		String resourceID = spogServer.createSourceWithCheck(prefix + "sourceName", SourceType.machine, SourceProduct.udp,
				initial_direct_orgID, siteID, ProtectionStatus.protect, ConnectionStatus.online, "windows", "SQL_SERVER", test);
		
		long startTimeTS = System.currentTimeMillis();
		long endTimeTS = System.currentTimeMillis();
		String serverID = UUID.randomUUID().toString();
		String rpsID = UUID.randomUUID().toString();
		String destinationID = UUID.randomUUID().toString();
		String policyID = UUID.randomUUID().toString();
		gatewayServer.postJobWithCheck(jobID, startTimeTS, endTimeTS, initial_direct_orgID, serverID, resourceID, rpsID, destinationID, policyID, jobType, jobMethod, jobStatus, siteToken, test);
		gatewayServer.postJobWithCodeCheck(jobID, startTimeTS, endTimeTS, initial_direct_orgID, serverID, 
				resourceID, rpsID, destinationID, policyID, jobType, jobMethod, jobStatus, siteToken, statusCode, errorCode, test);
	}
	
	@DataProvider(name = "post_job_with_invalid_jobid")
	public final Object[][] postJobWithInvalidJobIDParams() {
		return new Object[][] { 
			//different job method
				{ "backup", "unknown", "finished", "jobID", 400, "40000005" },
		};}
	
//	@Test (dataProvider = "post_job_with_invalid_jobid")
	public void testPostJobWithInvalidID(String jobType, String jobMethod, String jobStatus, String jobID, int statusCode, String errorCode) {
		spogServer.userLogin(initial_direct_email_full_added, password);
	
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		String resourceID = spogServer.createSourceWithCheck(prefix + "sourceName", SourceType.machine, SourceProduct.udp,
				initial_direct_orgID, siteID, ProtectionStatus.protect, ConnectionStatus.online, "windows", "SQL_SERVER", test);
		
		long startTimeTS = System.currentTimeMillis();
		long endTimeTS = System.currentTimeMillis();
		String serverID = UUID.randomUUID().toString();
		String rpsID = UUID.randomUUID().toString();
		String destinationID = UUID.randomUUID().toString();
		String policyID = UUID.randomUUID().toString();
		gatewayServer.postJobWithCodeCheck(jobID, startTimeTS, endTimeTS, initial_direct_orgID, serverID, 
				resourceID, rpsID, destinationID, policyID, jobType, jobMethod, jobStatus, siteToken, statusCode, errorCode, test);
	}
	
	@DataProvider(name = "post_job_data_403")
	public final Object[][] postJobData403Params() {
		return new Object[][] {
				{ initial_direct_email_full, password, initial_direct_orgID, initial_msp_orgID, 403, "00100101" },
				{ initial_direct_email_full, password, initial_direct_orgID, initial_sub_orgID_a, 403, "00100101" },
				{ initial_direct_email_full, password, initial_direct_orgID, UUID.randomUUID().toString(), 403, "00100101" },
				
				{ initial_msp_email_full, password, initial_msp_orgID, initial_direct_orgID, 403, "00100101" },
				//confirmed with Li Neo, it is changed in 2013/03/13, msp admin can post a job to its sub org 
//				{ initial_msp_email_full, password, initial_msp_orgID, initial_sub_orgID_a, 403, "00100101" },
				{ initial_msp_email_full, password, initial_msp_orgID, UUID.randomUUID().toString(), 403, "00100101" },
				
				{ initial_sub_email_full_a, password, initial_sub_orgID_a, initial_msp_orgID, 403, "00100101" },
				{ initial_sub_email_full_a, password, initial_sub_orgID_a, initial_direct_orgID, 403, "00100101" },
				{ initial_sub_email_full_a, password, initial_sub_orgID_a, UUID.randomUUID().toString(), 403, "00100101" },
		};}
	
//	@Test (dataProvider = "post_job_data_403")
	public void testPostJobWithError(String userName, String password, String orgID, String organizationID, int statusCode, String expectedErrorCode) {
		spogServer.userLogin(userName, password);
		String spogToken = spogServer.getJWTToken();
		String userID = spogServer.GetLoggedinUser_UserID();
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		String siteName = prefix + "siteName";
		String siteType = "gateway";
		Response response = spogServer.createSite(siteName, siteType, orgID, spogToken, test);
		
		Map<String,String > sitecreateResMap = new HashMap<>();
		sitecreateResMap=spogServer.checkCreateSite(response, SpogConstants.SUCCESS_POST, siteName, siteType,
				orgID,userID,"",test);
		
		String registrationBasecode = sitecreateResMap.get("registration_basecode");
		String siteID = sitecreateResMap.get("site_id");
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
		String token = response.then().extract().path("data.token");
		
		long startTimeTS = System.currentTimeMillis();
		long endTimeTS = System.currentTimeMillis();
		String serverID = UUID.randomUUID().toString();
		String resourceID = spogServer.createSourceWithCheck(prefix + "sourceName", SourceType.machine, SourceProduct.udp,
				orgID, siteID, ProtectionStatus.protect, ConnectionStatus.online, "windows", "SQL_SERVER", test);
		String rpsID = UUID.randomUUID().toString();
		String destinationID = UUID.randomUUID().toString();
		String policyID = UUID.randomUUID().toString();
		String jobType = "backup";
		String jobMethod = "incremental";
		String jobStatus = "finished";
		gatewayServer.postJobWithCodeCheck(startTimeTS, endTimeTS, organizationID, serverID, resourceID, rpsID, destinationID, policyID, jobType, jobMethod, jobStatus, token, statusCode, expectedErrorCode, test);
		
	}
	

	
	@DataProvider(name = "post_job_data_401")
	public final Object[][] postJobData401Params() {
		return new Object[][] {
			{ initial_direct_email_full, password, initial_direct_orgID, initial_direct_orgID, 401, "00900006" },
			{ initial_msp_email_full, password, initial_msp_orgID, initial_msp_orgID, 401, "00900006" },
			{ initial_sub_email_full_a, password, initial_sub_orgID_a, initial_sub_orgID_a, 401, "00900006" },

		};}
	
//	@Test (dataProvider = "post_job_data_401")
	public void testPostJobWithnoToken(String userName, String password, String orgID, String organizationID, int statusCode, String expectedErrorCode) {
		spogServer.userLogin(userName, password);
		String spogToken = spogServer.getJWTToken();
		String userID = spogServer.GetLoggedinUser_UserID();
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		String siteName = prefix + "siteName";
		String siteType = "gateway";
		Response response = spogServer.createSite(siteName, siteType, orgID, spogToken, test);
		
		Map<String,String > sitecreateResMap = new HashMap<>();
		sitecreateResMap=spogServer.checkCreateSite(response, SpogConstants.SUCCESS_POST, siteName, siteType,
				orgID,userID,"",test);
		
		String registrationBasecode = sitecreateResMap.get("registration_basecode");
		String siteID = sitecreateResMap.get("site_id");
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
		String token = response.then().extract().path("data.token");
		token = "";
		
		long startTimeTS = System.currentTimeMillis();
		long endTimeTS = System.currentTimeMillis();
		String serverID = UUID.randomUUID().toString();
		String resourceID = spogServer.createSourceWithCheck(prefix + "sourceName", SourceType.machine, SourceProduct.udp,
				orgID, siteID, ProtectionStatus.protect, ConnectionStatus.online, "windows", "SQL_SERVER", test);
		String rpsID = UUID.randomUUID().toString();
		String destinationID = UUID.randomUUID().toString();
		String policyID = UUID.randomUUID().toString();
		String jobType = "backup";
		String jobMethod = "incremental";
		String jobStatus = "finished";
		
		gatewayServer.postJobWithCodeCheck(startTimeTS, endTimeTS, organizationID, serverID, resourceID, rpsID, destinationID, policyID, jobType, jobMethod, jobStatus, token, statusCode, expectedErrorCode, test);
		
	}
	
	@DataProvider(name = "post_job_data_400")
	public final Object[][] postJobData400Params() {
		return new Object[][] {
				// start_time_ts to 0
				{ initial_direct_email_full, password, initial_direct_orgID, RandomStringUtils.randomAlphanumeric(8),
						"gateway", UUID.randomUUID().toString(), RandomStringUtils.randomAlphanumeric(8), "", 0,
						System.currentTimeMillis(), initial_direct_orgID, UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "", UUID.randomUUID().toString(), "backup", "incremental",
						"finished", 400, "40000001" },
				// job type to empty;
				{ initial_direct_email_full, password, initial_direct_orgID, RandomStringUtils.randomAlphanumeric(8),
						"gateway", UUID.randomUUID().toString(), RandomStringUtils.randomAlphanumeric(8), "",
						System.currentTimeMillis(), System.currentTimeMillis(), initial_direct_orgID,
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "", UUID.randomUUID().toString(),
						"", "incremental", "finished", 400, "40000001" },
				// job type not set;
				{ initial_direct_email_full, password, initial_direct_orgID, RandomStringUtils.randomAlphanumeric(8),
							"gateway", UUID.randomUUID().toString(), RandomStringUtils.randomAlphanumeric(8), "",
							System.currentTimeMillis(), System.currentTimeMillis(), initial_direct_orgID,
							UUID.randomUUID().toString(), UUID.randomUUID().toString(), "", UUID.randomUUID().toString(),
							"none", "incremental", "finished", 400, "40000001" },
				// job type to null;
				{ initial_direct_email_full, password, initial_direct_orgID, RandomStringUtils.randomAlphanumeric(8),
							"gateway", UUID.randomUUID().toString(), RandomStringUtils.randomAlphanumeric(8), "",
							System.currentTimeMillis(), System.currentTimeMillis(), initial_direct_orgID,
							UUID.randomUUID().toString(), UUID.randomUUID().toString(), "", UUID.randomUUID().toString(),
							null, "incremental", "finished", 400, "40000001" },
				// job type to invalid value;
				{ initial_direct_email_full, password, initial_direct_orgID, RandomStringUtils.randomAlphanumeric(8),
						"gateway", UUID.randomUUID().toString(), RandomStringUtils.randomAlphanumeric(8), "",
						System.currentTimeMillis(), System.currentTimeMillis(), initial_direct_orgID,
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "", UUID.randomUUID().toString(),
						"jobType", "incremental", "finished", 400, "40000006" },
				// job method to invalid value;
				{ initial_direct_email_full, password, initial_direct_orgID, RandomStringUtils.randomAlphanumeric(8),
						"gateway", UUID.randomUUID().toString(), RandomStringUtils.randomAlphanumeric(8), "",
						System.currentTimeMillis(), System.currentTimeMillis(), initial_direct_orgID,
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "", UUID.randomUUID().toString(),
						"backup", "jobMethod", "finished", 400, "40000006" },
				// job status to empty
				{ initial_direct_email_full, password, initial_direct_orgID, RandomStringUtils.randomAlphanumeric(8),
						"gateway", UUID.randomUUID().toString(), RandomStringUtils.randomAlphanumeric(8), "",
						System.currentTimeMillis(), System.currentTimeMillis(), initial_direct_orgID,
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "", UUID.randomUUID().toString(),
						"backup", "incremental", "", 400, "40000001" },
				// job status not set
				{ initial_direct_email_full, password, initial_direct_orgID, RandomStringUtils.randomAlphanumeric(8),
						"gateway", UUID.randomUUID().toString(), RandomStringUtils.randomAlphanumeric(8), "",
						System.currentTimeMillis(), System.currentTimeMillis(), initial_direct_orgID,
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "", UUID.randomUUID().toString(),
						"backup", "incremental", "none", 400, "40000001" },
				// job status to null
				{ initial_direct_email_full, password, initial_direct_orgID, RandomStringUtils.randomAlphanumeric(8),
						"gateway", UUID.randomUUID().toString(), RandomStringUtils.randomAlphanumeric(8), "",
						System.currentTimeMillis(), System.currentTimeMillis(), initial_direct_orgID,
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "", UUID.randomUUID().toString(),
						"backup", "incremental", null, 400, "40000001" },
				//job status to invalid value
				{ initial_direct_email_full, password, initial_direct_orgID, RandomStringUtils.randomAlphanumeric(8),
						"gateway", UUID.randomUUID().toString(), RandomStringUtils.randomAlphanumeric(8), "",
						System.currentTimeMillis(), System.currentTimeMillis(), initial_direct_orgID,
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "", UUID.randomUUID().toString(),
						"backup", "incremental", "jobStatus", 400, "40000006" },
	};}
	
//	@Test (dataProvider = "post_job_data_400")
	public void testPostJobWithError400(String userName, String password, String orgID, String siteName, String siteType, String gatewayID, String gatewayHostname, String siteVesion,
			long startTimeTS, long endTimeTS, String organizationID, String serverID, String rpsID, String destinationID, String policyID, String jobType, String jobMethod, String jobStatus, int statusCode, String expectedErrorCode) {
		spogServer.userLogin(userName, password);
		String spogToken = spogServer.getJWTToken();
		String userID = spogServer.GetLoggedinUser_UserID();
		
		Response response = spogServer.createSite(siteName, siteType, orgID, spogToken, test);
		
		Map<String,String > sitecreateResMap = new HashMap<>();
		sitecreateResMap=spogServer.checkCreateSite(response, SpogConstants.SUCCESS_POST,siteName, siteType,
				orgID,userID,"",test);
		
		String registrationBasecode = sitecreateResMap.get("registration_basecode");
		String siteID = sitecreateResMap.get("site_id");
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
		
		response = gatewayServer.RegisterSite(siteRegistrationKey,gatewayID,gatewayHostname,siteVesion,siteID,test);
		String site_secret = gatewayServer.checkRegisterSite(response, ServerResponseCode.Succes_Login, siteID, siteName, siteType, orgID, userID, true,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
		response = gatewayServer.LoginSite(siteID, site_secret, gatewayID, test);
		String token = response.then().extract().path("data.token");
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		String resourceID = spogServer.createSourceWithCheck(prefix + "sourceName", SourceType.machine, SourceProduct.udp,
				orgID, siteID, ProtectionStatus.protect, ConnectionStatus.online, "windows", "SQL_SERVER", test);
		
		gatewayServer.postJobWithCodeCheck(startTimeTS, endTimeTS, organizationID, serverID, resourceID, rpsID, destinationID, policyID, jobType, jobMethod, jobStatus, token, statusCode, expectedErrorCode, test);
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
