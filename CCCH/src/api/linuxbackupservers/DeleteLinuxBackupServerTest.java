package api.linuxbackupservers;

import org.testng.annotations.Test;

import com.google.inject.PrivateBinder;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.sun.mail.handlers.message_rfc822;

import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.Linux4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGJobServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import bsh.org.objectweb.asm.Constants;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.StringUtils;
import genericutil.User;
import genericutil.testcasescount;
import groovyjarjarantlr.PreservingFileWriter;
import invoker.Linux4SPOGInvoker;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.codehaus.groovy.ast.expr.PrefixExpression;

import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import Constants.SpogConstants;

import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import Constants.SourceType;

public class DeleteLinuxBackupServerTest extends base.prepare.Is4Org{

	private SPOGServer spogServer;
	private UserSpogServer userSpogServer;
	private Linux4SPOGServer linux4spogServer;
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
	private String initial_msp_user_ID_added = "";

	private String initial_msp_account_email = "spog_qa_msp_account_zhaoguo@arcserve.com";
	private String initial_msp_account_email_full = "";
	private String initial_msp_account_first_name = "spog_qa_msp_ma";
	private String initial_msp_account_last_name = "spog_qa_msp_zhaoguo";
	private String initial_msp_account_userID = "";

	private String initial_direct_org_name = "spog_qa_direct_zhaoguo";
	private String initial_direct_email = "spog_qa_direct_zhaoguo@arcserve.com";
	private String initial_direct_email_full = "";
	private String initial_direct_first_name = "spog_qa_direct_ma";
	private String initial_direct_last_name = "spog_qa_direct_zhaoguo2";
	
	private String initial_direct_email_added = "spog_qa_direct_zhaoguo_added@arcserve.com";
	private String initial_direct_email_full_added = "";
	private String initial_direct_first_name_added = "spog_qa_direct_ma";
	private String initial_direct_last_name_added = "spog_qa_direct_zhaoguo2";
	private String initial_direct_user_ID_added = "";

	private String initial_sub_org_name_a = "spog_qa_sub_zhaoguo_a";
	private String initial_sub_email_a = "spog_qa_sub_zhaoguo_a@arcserve.com";
	private String initial_sub_email_full_a = "";
	private String initial_sub_first_name_a = "spog_qa_sub_ma_a";
	private String initial_sub_last_name_a = "spog_qa_sub_zhaoguo_a";
	private String initial_sub_user_ID_a = "";
	
	private String initial_sub_email_a_added = "spog_qa_sub_zhaoguo_a_added@arcserve.com";
	private String initial_sub_email_full_a_added = "";
	private String initial_sub_first_name_a_added = "spog_qa_sub_ma_a";
	private String initial_sub_last_name_a_added = "spog_qa_sub_zhaoguo_a";
	private String initial_sub_user_ID_a_added = "";
	
	private String initial_sub_org_name_b = "spog_qa_sub_zhaoguo_b";
	private String initial_sub_email_b = "spog_qa_sub_zhaoguo_b@arcserve.com";
	private String initial_sub_email_full_b = "";
	private String initial_sub_first_name_b = "spog_qa_sub_ma_b";
	private String initial_sub_last_name_b = "spog_qa_sub_zhaoguo_b";
	private String initial_sub_user_ID_b = "";
	
	private String initial_sub_email_b_added = "spog_qa_sub_zhaoguo_b_added@arcserve.com";
	private String initial_sub_email_full_b_added = "";
	private String initial_sub_first_name_b_added = "spog_qa_sub_ma_b";
	private String initial_sub_last_name_b_added = "spog_qa_sub_zhaoguo_b";
	private String initial_sub_user_ID_b_added = "";

	private String initial_msp_orgID;
	private String initial_direct_orgID;
	private String initial_sub_orgID_a;
	private String initial_sub_orgID_b;

	private String password = "Pa$$w0rd";
	private String longString = StringUtils.repeatString("abcdefghijklmnopqrstuvwxyz", 10);

//	private SQLServerDb bqdb1;
//	public int Nooftest;
//	private long creationTime;
//	private String BQName = null;
//	private String runningMachine;
//	private testcasescount count1;
//	private String buildVersion;
	private String uuid1 = UUID.randomUUID().toString();
	private String uuid2 = UUID.randomUUID().toString();
	private String  org_model_prefix=this.getClass().getSimpleName();
	
	private String datacenter_id = "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c";
	private String csr_organization_id = "11111111-2222-3333-1111-222222222222";
	private String siteID = "fa48b4d5-638d-4b8e-8c04-a6cd1631797e";
	
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
		linux4spogServer = new Linux4SPOGServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
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
		this.initial_msp_user_ID_added = spogServer.createUserAndCheck(initial_msp_email_full_added, password, prefix+ initial_msp_first_name_added, 
				prefix+ initial_msp_last_name_added, "msp_admin", initial_msp_orgID, test);

		spogServer.userLogin(initial_msp_email_full, password);
		this.initial_sub_orgID_a = spogServer.createAccountWithCheck(initial_msp_orgID, initial_sub_org_name_a,	initial_msp_orgID);
		this.initial_sub_orgID_b = spogServer.createAccountWithCheck(initial_msp_orgID, initial_sub_org_name_b,	initial_msp_orgID);
		this.initial_sub_email_full_a = prefix + initial_sub_email_a;
		this.initial_sub_user_ID_a = spogServer.createUserAndCheck(prefix + this.initial_sub_email_a, password,
				prefix + this.initial_sub_first_name_a, prefix + this.initial_sub_last_name_a,
				SpogConstants.DIRECT_ADMIN, initial_sub_orgID_a, test);
		
		this.initial_sub_email_full_a_added = prefix + this.initial_sub_email_a_added;
		this.initial_sub_user_ID_a_added = spogServer.createUserAndCheck(prefix + this.initial_sub_email_a_added, password,
				prefix + this.initial_sub_first_name_a_added, prefix + this.initial_sub_last_name_a_added,
				SpogConstants.DIRECT_ADMIN, initial_sub_orgID_a, test);
		
		this.initial_sub_email_full_b = prefix + initial_sub_email_b;
		this.initial_sub_user_ID_b = spogServer.createUserAndCheck(prefix + this.initial_sub_email_b, password,
				prefix + this.initial_sub_first_name_b, prefix + this.initial_sub_last_name_b,
				SpogConstants.DIRECT_ADMIN, initial_sub_orgID_b, test);
		
		this.initial_sub_email_full_b_added = prefix + this.initial_sub_email_b_added;
		this.initial_sub_user_ID_b_added = spogServer.createUserAndCheck(prefix + this.initial_sub_email_b_added, password,
				prefix + this.initial_sub_first_name_b_added, prefix + this.initial_sub_last_name_b_added,
				SpogConstants.DIRECT_ADMIN, initial_sub_orgID_b, test);

		this.initial_msp_account_email_full = prefix + initial_msp_account_email;
		this.initial_msp_account_userID = spogServer.createUserAndCheck(initial_msp_account_email_full, password, initial_msp_account_first_name, 
				initial_msp_account_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, initial_msp_orgID, test);
		String[] userIds = new String[] {initial_msp_account_userID}; 
		userSpogServer.assignMspAccountAdmins(initial_msp_orgID, initial_sub_orgID_a, userIds, spogServer.getJWTToken());

		// create 1 direct organizations;
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		this.initial_direct_email_full = prefix + this.initial_direct_email;
		this.initial_direct_orgID = spogServer.CreateOrganizationWithCheck(prefix + initial_direct_org_name + org_model_prefix,
				SpogConstants.DIRECT_ORG, prefix + initial_direct_email, password, prefix + initial_direct_first_name,
				prefix + initial_direct_last_name);
		
		this.initial_direct_email_full_added = prefix + this.initial_direct_email_added;
		this.initial_direct_user_ID_added = spogServer.createUserAndCheck(prefix + this.initial_direct_email_added, password,
				prefix + this.initial_direct_first_name_added, prefix + this.initial_direct_last_name_added,
				SpogConstants.DIRECT_ADMIN, initial_direct_orgID, test);
	}
	
	@DataProvider(name = "update_linux_backup_server_valid")
	public final Object[][] updateHypervisorfilterValidParams() {
		return new Object[][] {
			// different sever_id
			{"none", "server_name01", "https", "8014", "root", "cnbjrdqa1@", initial_direct_orgID, UUID.randomUUID().toString(), "normal", "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c"},
			{UUID.randomUUID().toString(), "server_name01", "https",  "8014", "root", "cnbjrdqa1@", initial_direct_orgID, UUID.randomUUID().toString(), "normal", "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c"},
		};
	}
//	zhaoguo: covered in other bq set.
//	@Test(dataProvider = "update_linux_backup_server_valid")
	public void deleteLinuxServerValid(String server_id, String server_name, String server_protocol, String server_port, String server_username, String server_password, String organization_id, 
			String site_id, String status, String datacenter_id) {
		spogServer.userLogin(csrAdmin, csrPwd);
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		linux4spogServer.setToken(spogServer.getJWTToken());
		server_name = prefix + server_name;
		String server_id_response = linux4spogServer.createLinuxBackupServer(server_id, server_name, server_protocol, server_port, server_username, server_password, organization_id, site_id, status, datacenter_id, test);
		
		linux4spogServer.deleteLinuxBackupServer(server_id_response, test);
	}
	
	@DataProvider(name = "update_linux_backup_server_invalid")
	public final Object[][] updateHypervisorfilterinValidParams() {
		return new Object[][] {
			{initial_direct_email_full, password, "none", "10.57.10.12", "https", "8014", "root", "cnbjrdqa1@", csr_organization_id, siteID, "normal", datacenter_id, 403, "00100101"},
			{initial_msp_email_full, password, "none", "10.57.10.12", "https", "8014", "root", "cnbjrdqa1@", csr_organization_id, siteID, "normal", datacenter_id, 403, "00100101"},
			{initial_sub_email_full_a, password, "none", "10.57.10.12", "https", "8014", "root", "cnbjrdqa1@", csr_organization_id, siteID, "normal", datacenter_id, 403, "00100101"},
			{csr_readonly_email, csr_readonly_password, "none", "10.57.10.12", "https", "8014", "root", "cnbjrdqa1@", csr_organization_id, siteID, "normal", datacenter_id, 403, "00100101"},
		};
	}
	
	@Test(dataProvider = "update_linux_backup_server_invalid")
	public void updateLinuxServerInvalid(String username, String password, String server_id, String server_name, String server_protocol, String server_port, String server_username, String server_password, String organization_id, 
			String site_id, String status, String datacenter_id, int status_code, String error_code) {
		spogServer.userLogin(csrAdmin, csrPwd);
		linux4spogServer.setToken(spogServer.getJWTToken());
		String server_id_response = linux4spogServer.createLinuxBackupServer(server_id, server_name, server_protocol, server_port, server_username, server_password, organization_id, site_id, status, datacenter_id, test);
		spogServer.userLogin(username, password);
		linux4spogServer.setToken(spogServer.getJWTToken());
		linux4spogServer.deleteLinuxBackupServerWithErrorCheck(server_id_response, status_code, error_code, test);
		spogServer.userLogin(csrAdmin, csrPwd);
		linux4spogServer.setToken(spogServer.getJWTToken());
		linux4spogServer.deleteLinuxBackupServer(server_id_response, test);
	}
	
	@DataProvider(name = "delete_linux_backup_server_notoken")
	public final Object[][] updateHypervisorfilternotokenParams() {
		return new Object[][] {
			{csrAdmin, csrPwd, "none", "10.57.10.12", "https", "8014", "root", "cnbjrdqa1@", csr_organization_id, siteID, "normal", datacenter_id, 401, "00900006"},
		};}
	
	@Test(dataProvider = "delete_linux_backup_server_notoken")
	public void deleteLinuxServerNotoken(String username, String password, String server_id, String server_name, String server_protocol, String server_port, String server_username, String server_password, String organization_id, 
			String site_id, String status, String datacenter_id, int status_code, String error_code) {
		spogServer.userLogin(csrAdmin, csrPwd);
		linux4spogServer.setToken(spogServer.getJWTToken());
		String server_id_response = linux4spogServer.createLinuxBackupServer(server_id, server_name, server_protocol, server_port, server_username, server_password, organization_id, site_id, status, datacenter_id, test);
		spogServer.userLogin(username, password);
		linux4spogServer.setToken("");
		linux4spogServer.deleteLinuxBackupServerWithErrorCheck(server_id_response, status_code, error_code, test);
		spogServer.userLogin(csrAdmin, csrPwd);
		linux4spogServer.setToken(spogServer.getJWTToken());
		linux4spogServer.deleteLinuxBackupServer(server_id_response, test);
		
	}
	
	@DataProvider(name = "update_linux_backup_server_invalidID")
	public final Object[][] updateHypervisorfilterInvalidIDParams() {
		return new Object[][] {
			{"none", "server_name01", "https", "root", "cnbjrdqa1@", initial_direct_orgID, UUID.randomUUID().toString(), "normal", "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c"},
		};}
	
	@Test(dataProvider = "update_linux_backup_server_invalidID")
	public void updateLinuxServerinvalidID(String server_id, String server_name, String server_protocol, String server_username, String server_password, String organization_id, 
			String site_id, String status, String datacenter_id) {
		spogServer.userLogin(csrAdmin, csrPwd);
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		linux4spogServer.setToken(spogServer.getJWTToken());
		server_name = prefix + server_name;
		// String server_id_response = linux4spogServer.createLinuxBackupServer(server_id, server_name, server_protocol, server_username, server_password, organization_id, site_id, status, datacenter_id, test);
		linux4spogServer.deleteLinuxBackupServerWithErrorCheck(UUID.randomUUID().toString(), 404, "01400002", test);
		linux4spogServer.deleteLinuxBackupServerWithErrorCheck("serverID", 400, "40000005", test);
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
