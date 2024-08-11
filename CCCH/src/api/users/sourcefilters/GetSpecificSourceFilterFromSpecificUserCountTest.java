package api.users.sourcefilters;

import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.sun.mail.handlers.message_rfc822;

import Base64.Base64Coder;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.SPOGServer;
import InvokerServer.ServerResponseCode;
import InvokerServer.GatewayServer.siteType;
import bsh.org.objectweb.asm.Constants;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import groovyjarjarantlr.PreservingFileWriter;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;

import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import Constants.SpogConstants;

import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;

import Constants.ConnectionStatus;
import Constants.OSMajor;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;

public class GetSpecificSourceFilterFromSpecificUserCountTest extends base.prepare.Is4Org {

	private SPOGServer spogServer;
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
	
	private String siteID;
	private String password = "Pa$$w0rd";

	private String  org_model_prefix=this.getClass().getSimpleName();
//	private SQLServerDb bqdb1;
//	public int Nooftest;
//	private long creationTime;
//	private String BQName = null;
//	private String runningMachine;
//	private testcasescount count1;
//	private String buildVersion;
	private String uuid1 = UUID.randomUUID().toString();
	private String uuid2 = UUID.randomUUID().toString();

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
		String initial_direct_user_ID_added = spogServer.createUserAndCheck(prefix + this.initial_direct_email_added, password,
				prefix + this.initial_direct_first_name_added, prefix + this.initial_direct_last_name_added,
				SpogConstants.DIRECT_ADMIN, initial_direct_orgID, test);
		
		GatewayServer gatewayServer = new GatewayServer(baseURI, port);
		spogServer.userLogin(initial_direct_email_full_added, password);
		String token = spogServer.getJWTToken();
		Response response = spogServer.createSite("siteName", siteType.cloud_direct.name(), initial_direct_orgID, token, test);
		Map<String, String> sitecreateResMap = new HashMap<>();
		sitecreateResMap = spogServer.checkCreateSite(response, SpogConstants.SUCCESS_POST, "siteName", siteType.cloud_direct.name(),
				initial_direct_orgID, initial_direct_user_ID_added, "", test);
		String registrationBasecode = sitecreateResMap.get("registration_basecode");
		siteID = sitecreateResMap.get("site_id");
		
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
		String gatewayID = UUID.randomUUID().toString();
		String gatewayHostName = prefix + "hostName";
		String siteVersion = "";
		test.log(LogStatus.INFO, "register a site");
		response = gatewayServer.RegisterSite(siteRegistrationKey, gatewayID, gatewayHostName, siteVersion, siteID,
				test);
		String site_secret = gatewayServer.checkRegisterSite(response, ServerResponseCode.Succes_Login, siteID,
				"siteName", siteType.cloud_direct.name(), initial_direct_orgID, initial_direct_user_ID_added, true, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		
		String sourceID1 = spogServer.createSourceWithCheck("source_name1", SourceType.machine, SourceProduct.udp, 
				initial_direct_orgID, siteID, ProtectionStatus.protect, ConnectionStatus.online, 
				OSMajor.windows.name(), "sql;exchange", "vm_name", null, 
				"agent_name", "os_name", "64" ,"1.0.0","2.0", "http://upgrade", UUID.randomUUID().toString(), 
				SpogConstants.SUCCESS_POST, "", test);
		
		String sourceID2 = spogServer.createSourceWithCheck("source_name2", SourceType.machine, SourceProduct.cloud_direct, 
				initial_direct_orgID, siteID, ProtectionStatus.protect, ConnectionStatus.online, 
				OSMajor.windows.name(), "emptyarray", "vm_name", null, 
				"agent_name", "os_name", "64" ,"1.0.0","2.0", "http://upgrade", UUID.randomUUID().toString(), 
				SpogConstants.SUCCESS_POST, "", test);
		
		String sourceID3 = spogServer.createSourceWithCheck("source_name3", SourceType.machine, SourceProduct.udp, 
				initial_direct_orgID, siteID, ProtectionStatus.unprotect, ConnectionStatus.online, 
				OSMajor.windows.name(), "sql;exchange", "vm_name", null, 
				"agent_name", "os_name", "64" ,"1.0.0","2.0", "http://upgrade", UUID.randomUUID().toString(), 
				SpogConstants.SUCCESS_POST, "", test);
		
		String sourceID4 = spogServer.createSourceWithCheck("source_name4", SourceType.machine, SourceProduct.udp, 
				initial_direct_orgID, siteID, ProtectionStatus.protect, ConnectionStatus.offline, 
				OSMajor.windows.name(), "sql;exchange", "vm_name", null, 
				"agent_name", "os_name", "64" ,"1.0.0","2.0", "http://upgrade", UUID.randomUUID().toString(), 
				SpogConstants.SUCCESS_POST, "", test);
		
		String sourceID5 = spogServer.createSourceWithCheck("source_name5", SourceType.machine, SourceProduct.udp, 
				initial_direct_orgID, siteID, ProtectionStatus.protect, ConnectionStatus.online, 
				OSMajor.linux.name(), "sql;exchange", "vm_name", null, 
				"agent_name", "os_name", "64" ,"1.0.0","2.0", "http://upgrade", UUID.randomUUID().toString(), 
				SpogConstants.SUCCESS_POST, "", test);
		
		String sourceID6 = spogServer.createSourceWithCheck("source_name6", SourceType.instant_vm, SourceProduct.udp, 
				initial_direct_orgID, siteID, ProtectionStatus.protect, ConnectionStatus.online, 
				OSMajor.windows.name(), "sql;exchange", "vm_name", null, 
				"agent_name", "os_name", "64" ,"1.0.0","2.0", "http://upgrade", UUID.randomUUID().toString(), 
				SpogConstants.SUCCESS_POST, "", test);
		
		String sourceID7 = spogServer.createSourceWithCheck("source_name7", SourceType.office_365, SourceProduct.udp, 
				initial_direct_orgID, siteID, ProtectionStatus.protect, ConnectionStatus.online, 
				OSMajor.windows.name(), "sql;exchange", "vm_name", null, 
				"agent_name", "os_name", "64" ,"1.0.0","2.0", "http://upgrade", UUID.randomUUID().toString(), 
				SpogConstants.SUCCESS_POST, "", test);
		
		String sourceID8 = spogServer.createSourceWithCheck("source_name8", SourceType.shared_folder, SourceProduct.udp, 
				initial_direct_orgID, siteID, ProtectionStatus.protect, ConnectionStatus.online, 
				OSMajor.unknown.name(), "sql;exchange", "vm_name", null, 
				"agent_name", "os_name", "64" ,"1.0.0","2.0", "http://upgrade", UUID.randomUUID().toString(), 
				SpogConstants.SUCCESS_POST, "", test);
		
		String sourceID9 = spogServer.createSourceWithCheck("source_name9", SourceType.virtual_standby, SourceProduct.udp, 
				initial_direct_orgID, siteID, ProtectionStatus.unprotect, ConnectionStatus.online, 
				OSMajor.windows.name(), "sql;exchange", "vm_name", null, 
				"agent_name", "os_name", "64" ,"1.0.0","2.0", "http://upgrade", UUID.randomUUID().toString(), 
				SpogConstants.SUCCESS_POST, "", test);
		
		String sourceID10 = spogServer.createSourceWithCheck("source_name10", SourceType.office_365, SourceProduct.udp, 
				initial_direct_orgID, siteID, ProtectionStatus.unprotect, ConnectionStatus.online, 
				OSMajor.unknown.name(), "sql;exchange", "vm_name", null, 
				"agent_name", "os_name", "64" ,"1.0.0","2.0", "http://upgrade", UUID.randomUUID().toString(), 
				SpogConstants.SUCCESS_POST, "", test);
		
		String sourceID11 = spogServer.createSourceWithCheck("source_name11", SourceType.shared_folder, SourceProduct.udp, 
				initial_direct_orgID, siteID, ProtectionStatus.protect, ConnectionStatus.online, 
				OSMajor.unknown.name(), "sql;exchange", "vm_name", null, 
				"agent_name", "os_name", "64" ,"1.0.0","2.0", "http://upgrade", UUID.randomUUID().toString(), 
				SpogConstants.SUCCESS_POST, "", test);
		
	}
	
	public void compareFilter(String filter_id, String filter_name, String user_id, String organization_id, String protection_status,
						String connection_status, String protection_policy, String backup_status, 
						String source_group, String operating_system, String applications, String site_id, String source_name, String source_type, Boolean is_default, Response response) {
		
		String actual_filter_id = response.then().extract().path("data.filter_id");
	    
	    response.then().body("data.filter_name", equalTo(filter_name))
	    .body("data.organization_id",equalTo(organization_id))
	    .body("data.is_default", equalTo(is_default)); 
	    
	    
//	    String sourceType = response.then().extract().path("data.source_type");
//	    if (null == source_type || source_type == "" || source_type.equalsIgnoreCase("none")) {
//	    	assertEquals(sourceType, "all");
//	    } else {
//	    	assertEquals(sourceType, source_type);
//	    }
	    
	    ArrayList<String> response_protection_status =
	        response.then().extract().path("data.protection_status");
	    spogServer.assertFilterItem(protection_status, response_protection_status);

	    ArrayList<String> response_connection_status =
	        response.then().extract().path("data.connection_status");
	    spogServer.assertFilterItem(connection_status, response_connection_status);

	    ArrayList<String> response_protection_policy =
	        response.then().extract().path("data.policy_id");
	    spogServer.assertFilterItem(protection_policy, response_protection_policy);

	    ArrayList<String> response_backup_status = response.then().extract().path("data.last_backup_status");
	    spogServer.assertFilterItem(backup_status, response_backup_status);

	    ArrayList<String> response_source_group = response.then().extract().path("data.group_id");
	    spogServer.assertFilterItem(source_group, response_source_group);
	    
	    ArrayList<String> response_operating_system =
	        response.then().extract().path("data.os_major");	    
	    spogServer.assertFilterItem(operating_system, response_operating_system);
	   
	    ArrayList<String> response_site_id = response.then().extract().path("data.site_id");
	    spogServer.assertFilterItem(site_id, response_site_id);
	}

	@DataProvider(name = "basicfilterInfo")
	public final Object[][] getbasicfilterInfo() {
		return new Object[][] {
			{"filter_name1", initial_direct_email_full, initial_direct_orgID, "protect", "none", "online", "none", "none", "windows", "SQL", siteID, "source_Name","machine", "true", 2},
			{"filter_name2", initial_direct_email_full, initial_direct_orgID, "unprotect", "none", "online", "none", "none", "windows", "SQL", siteID, "source_Name","machine", "true", 1},
			{"filter_name3", initial_direct_email_full, initial_direct_orgID, "protect", "none", "offline", "none", "none", "windows", "SQL", siteID, "source_Name","machine", "true", 1},
			{"filter_name4", initial_direct_email_full, initial_direct_orgID, "protect", "none", "offline", "none", "none", "windows", "SQL", siteID, "source_Name","machine", "true", 1},
			{"filter_name5", initial_direct_email_full, initial_direct_orgID, "none", "none", "online", "none", "none", "windows", "SQL", siteID, "source_Name","machine", "true", 3},
			{"filter_name6", initial_direct_email_full, initial_direct_orgID, "none", "none", "none", "none", "none", "windows", "SQL", siteID, "source_Name","machine", "true", 4},
			{"filter_name7", initial_direct_email_full, initial_direct_orgID, "none", "none", "none", "none", "none", "linux", "SQL", siteID, "source_Name","machine", "true", 1},
			{"filter_name8", initial_direct_email_full, initial_direct_orgID, "none", "none", "none", "none", "none", "unknown", "SQL", siteID, "source_Name","office_365", "true", 1},
			{"filter_name9", initial_direct_email_full, initial_direct_orgID, "none", "none", "none", "none", "none", "none", "SQL", siteID, "source_Name","none", "true", 11},
			{"filter_name10", initial_direct_email_full, initial_direct_orgID, "none", "none", "none", "none", "none", "none", "SQL", siteID, "none","none", "true", 11},
			{"filter_name11", initial_direct_email_full, initial_direct_orgID, "none", "none", "none", "none", "none", "none", "SQL", siteID, "source_Name1","none", "true", 3},
			{"filter_name12", initial_direct_email_full, initial_direct_orgID, "none", "none", "none", "none", "none", "none", "SQL", siteID, "sourceName","none", "true", 0},
		};
	}
	
	@Test(dataProvider = "basicfilterInfo")
	public void validfilterTest(String filter_name, String userName, String organization_id, String protection_status, String protection_policy, 
			String connection_status, String backup_status, String source_group, String operating_system, String applications, String site_id, String source_name, String source_type, 
			String is_default, int expectedCount) {
		
		spogServer.userLogin(userName, password);
		String user_id = spogServer.GetLoggedinUser_UserID();
		
		
		String filter_id = spogServer.createFilterwithCheck(user_id, filter_name, protection_status, 
				connection_status, protection_policy, backup_status, source_group, operating_system, applications, site_id, source_name, source_type, is_default, test);
		
		Response response = spogServer.getFilterByID(user_id, filter_id);
		Boolean is_default_value = false;
		if(is_default==null) {
			is_default_value = false;
		} else if(is_default.equalsIgnoreCase("true")) {
			is_default_value = true;
		}
		compareFilter(filter_id, filter_name, user_id, organization_id, protection_status, connection_status,
				protection_policy, backup_status, source_group, operating_system, applications, site_id, source_name, source_type, is_default_value, response);
		int count = response.then().extract().path("data.count");
		assertEquals(count, expectedCount);
		
	}
	
	@DataProvider(name = "csr_readonly_params")
	public final Object[][] getFilters_csr_readonly() {
		return new Object[][] {
			{this.initial_msp_email_full, password},
			{this.initial_direct_email_full, password},
			{this.initial_sub_email_full_a, password},
			{this.csr_readonly_email, csr_readonly_password}
		};}
	
	@Test(dataProvider = "csr_readonly_params")
	public void csrReadonlyTest(String username, String password) {
		spogServer.userLogin(username, password);
		String user_id = spogServer.GetLoggedinUser_UserID();
		String uuid1 = UUID.randomUUID().toString();
		String uuid2 = UUID.randomUUID().toString();
		
		String filter_id = spogServer.createFilterwithCheck(user_id,  "filterName" + RandomStringUtils.randomAlphanumeric(8), "protect", "online", uuid1, "finished", 
				uuid1, "windows", "SQL_SERVER", uuid1, "sourceName1", "machine", "true", test);
		spogServer.userLogin(csr_readonly_email, csr_readonly_password);
		Response response = spogServer.getFilterByID(user_id, filter_id);
		
		spogServer.checkResponseStatus(response, 200);
		
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
