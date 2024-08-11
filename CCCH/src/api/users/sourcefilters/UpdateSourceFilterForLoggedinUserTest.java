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
import InvokerServer.Source4SPOGServer;
import InvokerServer.UserSpogServer;
import InvokerServer.GatewayServer.siteType;
import api.preparedata.InitialTestData;
import api.preparedata.InitialTestDataImpl;
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

public class UpdateSourceFilterForLoggedinUserTest extends base.prepare.Is4Org {

	private SPOGServer spogServer;
	private Source4SPOGServer source4spogServer;
	private UserSpogServer userSpogServer;
	private String csrAdmin;
	private String csrPwd;
//	private ExtentReports rep;
	private ExtentTest test;
	
	private String csr_readonly_email = "csr_readonly_zhaoguo@arcserve.com";
	private String csr_readonly_password = "Caworld_2017";
	private InitialTestDataImpl initialTestDataImpl;
	private InitialTestData itd;
	private String password = "Pa$$w0rd";

	private String siteID1;
	private String siteID2;
	private String uuid1 = UUID.randomUUID().toString();
	private String uuid2 = UUID.randomUUID().toString();

	private String  org_model_prefix=this.getClass().getSimpleName();
	private String initial_sourcefilter_ID_direct;

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
		source4spogServer = new Source4SPOGServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		this.csrAdmin = csrAdminUserName;
		this.csrPwd = csrAdminPassword;
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		String prefix = RandomStringUtils.randomAlphanumeric(8);

		String prefix_a = RandomStringUtils.randomAlphanumeric(8) + this.getClass().getSimpleName();		
		initialTestDataImpl = new InitialTestDataImpl(baseURI, port, csrAdminUserName, csrAdminPassword, prefix_a, password);
		itd = initialTestDataImpl.initialize();

		// create msp organization #1, and then 1 sub-organizations in it, create one
		// admin for the sub organization;
		
		/**
		 * 
		GatewayServer gatewayServer = new GatewayServer(baseURI, port);
		spogServer.userLogin(itd.getDirect_org_1_user_1_email(), password);
		String token = spogServer.getJWTToken();
		Response response = spogServer.createSite("siteName", siteType.cloud_direct.name(), itd.getDirect_org_1(), token, test);
		 */
		
		GatewayServer gatewayServer = new GatewayServer(baseURI, port);
		spogServer.userLogin(itd.getDirect_org_1_user_1_email(), password);
		String token = spogServer.getJWTToken();
		
		/**
		 * Create Site1
		 */
		Response response = spogServer.createSite("siteName", siteType.cloud_direct.name(), itd.getDirect_org_1(), token, test);
		Map<String, String> sitecreateResMap = new HashMap<>();
		sitecreateResMap = spogServer.checkCreateSite(response, SpogConstants.SUCCESS_POST, "siteName", siteType.cloud_direct.name(),
				itd.getDirect_org_1(), itd.getDirect_org_1_user_1(), "", test);
		String registrationBasecode = sitecreateResMap.get("registration_basecode");
		siteID1 = sitecreateResMap.get("site_id");
		
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
		
		String gatewayID = UUID.randomUUID().toString();
		String gatewayHostName = prefix + "hostName1";
		String siteVersion = "";
		test.log(LogStatus.INFO, "register a site");
		response = gatewayServer.RegisterSite(siteRegistrationKey, gatewayID, gatewayHostName, siteVersion, siteID1, test);
		String site_secret = gatewayServer.checkRegisterSite(response, ServerResponseCode.Succes_Login, siteID1,
				"siteName", siteType.cloud_direct.name(), itd.getDirect_org_1(), itd.getDirect_org_1_user_1(), true, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		
		/**
		 * Create Site 2
		 */
		
		response = spogServer.createSite("siteName", siteType.cloud_direct.name(), itd.getDirect_org_1(), token, test);
		sitecreateResMap = new HashMap<>();
		sitecreateResMap = spogServer.checkCreateSite(response, SpogConstants.SUCCESS_POST, "siteName", siteType.cloud_direct.name(),
				itd.getDirect_org_1(), itd.getDirect_org_1_user_1(), "", test);
		registrationBasecode = sitecreateResMap.get("registration_basecode");
		siteID2 = sitecreateResMap.get("site_id");
		
		siteRegistrationKey = "";
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
		gatewayID = UUID.randomUUID().toString();
		gatewayHostName = prefix + "hostName2";
		siteVersion = "";
		test.log(LogStatus.INFO, "register a site");
		response = gatewayServer.RegisterSite(siteRegistrationKey, gatewayID, gatewayHostName, siteVersion, siteID2, test);
		site_secret = gatewayServer.checkRegisterSite(response, ServerResponseCode.Succes_Login, siteID2,
				"siteName", siteType.cloud_direct.name(), itd.getDirect_org_1(), itd.getDirect_org_1_user_1(), true, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		
		spogServer.userLogin(itd.getDirect_org_1_user_1_email(), password);
		
		String sourceID1 = spogServer.createSourceWithCheck("source_name1", SourceType.machine, SourceProduct.udp, 
				itd.getDirect_org_1(), siteID1, ProtectionStatus.unprotect, ConnectionStatus.online, 
				OSMajor.windows.name(), "sql;exchange", "vm_name", null, 
				"agent_name", "os_name", "64" ,"1.0.0","2.0", "http://upgrade", UUID.randomUUID().toString(), 
				SpogConstants.SUCCESS_POST, "", test);
		
		String sourceID2 = spogServer.createSourceWithCheck("source_name2", SourceType.machine, SourceProduct.udp, 
				itd.getDirect_org_1(), siteID1, ProtectionStatus.unprotect, ConnectionStatus.online, 
				OSMajor.windows.name(), "emptyarray", "vm_name", null, 
				"agent_name", "os_name", "64" ,"1.0.0","2.0", "http://upgrade", UUID.randomUUID().toString(), 
				SpogConstants.SUCCESS_POST, "", test);
		
		String sourceID3 = spogServer.createSourceWithCheck("source_name3", SourceType.machine, SourceProduct.udp, 
				itd.getDirect_org_1(), siteID1, ProtectionStatus.unprotect, ConnectionStatus.online, 
				OSMajor.windows.name(), "sql;exchange", "vm_name", null, 
				"agent_name", "os_name", "64" ,"1.0.0","2.0", "http://upgrade", UUID.randomUUID().toString(), 
				SpogConstants.SUCCESS_POST, "", test);
		
		String sourceID4 = spogServer.createSourceWithCheck("source_name4", SourceType.machine, SourceProduct.udp, 
				itd.getDirect_org_1(), siteID1, ProtectionStatus.unprotect, ConnectionStatus.offline, 
				OSMajor.windows.name(), "sql;exchange", "vm_name", null, 
				"agent_name", "os_name", "64" ,"1.0.0","2.0", "http://upgrade", UUID.randomUUID().toString(), 
				SpogConstants.SUCCESS_POST, "", test);
		
		String sourceID5 = spogServer.createSourceWithCheck("source_name5", SourceType.machine, SourceProduct.udp, 
				itd.getDirect_org_1(), siteID1, ProtectionStatus.unprotect, ConnectionStatus.online, 
				OSMajor.linux.name(), "sql;exchange", "vm_name", null, 
				"agent_name", "os_name", "64" ,"1.0.0","2.0", "http://upgrade", UUID.randomUUID().toString(), 
				SpogConstants.SUCCESS_POST, "", test);
		
		String sourceID6 = spogServer.createSourceWithCheck("source_name6", SourceType.instant_vm, SourceProduct.udp, 
				itd.getDirect_org_1(), siteID1, ProtectionStatus.unprotect, ConnectionStatus.online, 
				OSMajor.windows.name(), "sql;exchange", "vm_name", null, 
				"agent_name", "os_name", "64" ,"1.0.0","2.0", "http://upgrade", UUID.randomUUID().toString(), 
				SpogConstants.SUCCESS_POST, "", test);
		
		String sourceID7 = spogServer.createSourceWithCheck("source_name7", SourceType.office_365, SourceProduct.udp, 
				itd.getDirect_org_1(), siteID1, ProtectionStatus.unprotect, ConnectionStatus.online, 
				OSMajor.windows.name(), "sql;exchange", "vm_name", null, 
				"agent_name", "os_name", "64" ,"1.0.0","2.0", "http://upgrade", UUID.randomUUID().toString(), 
				SpogConstants.SUCCESS_POST, "", test);
		
		String sourceID8 = spogServer.createSourceWithCheck("source_name8", SourceType.shared_folder, SourceProduct.udp, 
				itd.getDirect_org_1(), siteID2, ProtectionStatus.unprotect, ConnectionStatus.online, 
				OSMajor.unknown.name(), "sql;exchange", "vm_name", null, 
				"agent_name", "os_name", "64" ,"1.0.0","2.0", "http://upgrade", UUID.randomUUID().toString(), 
				SpogConstants.SUCCESS_POST, "", test);
		
		String sourceID9 = spogServer.createSourceWithCheck("source_name9", SourceType.virtual_standby, SourceProduct.udp, 
				itd.getDirect_org_1(), siteID2, ProtectionStatus.unprotect, ConnectionStatus.online, 
				OSMajor.windows.name(), "sql;exchange", "vm_name", null, 
				"agent_name", "os_name", "64" ,"1.0.0","2.0", "http://upgrade", UUID.randomUUID().toString(), 
				SpogConstants.SUCCESS_POST, "", test);
		
		String sourceID10 = spogServer.createSourceWithCheck("source_name10", SourceType.office_365, SourceProduct.udp, 
				itd.getDirect_org_1(), siteID2, ProtectionStatus.unprotect, ConnectionStatus.online, 
				OSMajor.unknown.name(), "sql;exchange", "vm_name", null, 
				"agent_name", "os_name", "64" ,"1.0.0","2.0", "http://upgrade", UUID.randomUUID().toString(), 
				SpogConstants.SUCCESS_POST, "", test);
		
		String sourceID11 = spogServer.createSourceWithCheck("source_name11", SourceType.shared_folder, SourceProduct.udp, 
				itd.getDirect_org_1(), siteID2, ProtectionStatus.unprotect, ConnectionStatus.online, 
				OSMajor.unknown.name(), "sql;exchange", "vm_name", null, 
				"agent_name", "os_name", "64" ,"1.0.0","2.0", "http://upgrade", UUID.randomUUID().toString(), 
				SpogConstants.SUCCESS_POST, "", test);
		
		spogServer.userLogin(itd.getDirect_org_1_user_1_email(), password);
		source4spogServer.setToken(spogServer.getJWTToken());
		initial_sourcefilter_ID_direct = source4spogServer.createSourcefilterForLoggedinUserWithCheck("initial_filtername", "protect", "online", null, "active", null, "windows", siteID1, "source_Name", "machine", "true", test);
	}

	@DataProvider(name = "filter_info_valid_params")
	public final Object[][] updateSourcefilterInfoValid() {
		return new Object[][] {		
			// different roles to create source filter for logged in user
			{"filter_name01", itd.getDirect_org_1_user_1_email(), "protect", "none", "online", "none", "none", "windows", siteID1, "source_Name","machine", "true"},
			{"filter_name02", itd.getMsp_org_1_user_1_email(), "protect", "none", "online", "none", "none", "windows", siteID1, "source_Name","machine", "true"},
			{"filter_name03", itd.getMsp_org_1_sub_1_user_1_email(), "protect", "none", "online", "none", "none", "windows", siteID1, "source_Name","machine", "true"},
			{"filter_name03_a", itd.getMsp_org_1_account_admin_1_email(), "protect", "none", "online", "none", "none", "windows", siteID1, "source_Name","machine", "true"},
			{"filter_name03_b", itd.getRoot_msp_org_1_account_admin_1_email(), "protect", "none", "online", "none", "none", "windows", siteID1, "source_Name","machine", "true"},
			{"filter_name03_c", itd.getRoot_msp_org_1_user_1_email(), "protect", "none", "online", "none", "none", "windows", siteID1, "source_Name","machine", "true"},
			{"filter_name03_d", itd.getRoot_msp_org_1_account_1_user_1_email(), "protect", "none", "online", "none", "none", "windows", siteID1, "source_Name","machine", "true"},
			{"filter_name03_e", itd.getRoot_msp_org_1_sub_msp_1_user_1_email(), "protect", "none", "online", "none", "none", "windows", siteID1, "source_Name","machine", "true"},
			{"filter_name03_f", itd.getRoot_msp_org_1_sub_msp_1_account_admin_1_email(), "protect", "none", "online", "none", "none", "windows", siteID1, "source_Name","machine", "true"},
			{"filter_name03_g", itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1_email(), "protect", "none", "online", "none", "none", "windows", siteID1, "source_Name","machine", "true"},
			
			// monitor user related
			{"filter_name04_a", itd.getRoot_msp_org_1_monitor_user_1_email(), "protect", "none", "online", "none", "none", "windows", siteID1, "source_Name","machine", "true"},
			{"filter_name04_b", itd.getRoot_msp_org_1_account_1_monitor_user_1_email(), "protect", "none", "online", "none", "none", "windows", siteID1, "source_Name","machine", "true"},
			{"filter_name04_b", itd.getRoot_msp_org_1_sub_msp_1_monitor_user_1_email(), "protect", "none", "online", "none", "none", "windows", siteID1, "source_Name","machine", "true"},
			{"filter_name04_a", itd.getMsp_org_1_monitor_user_1_email(), "protect", "none", "online", "none", "none", "windows", siteID1, "source_Name","machine", "true"},
			{"filter_name04_a", itd.getDirect_org_1_monitor_user_1_email(), "protect", "none", "online", "none", "none", "windows", siteID1, "source_Name","machine", "true"},

			// update source filter with different protection status
			{"filter_name04", itd.getDirect_org_1_user_1_email(), "protect,unprotect", "none", "online", "none", "none", "windows", siteID1, "source_Name","machine", "true"},
			{"filter_name05", itd.getDirect_org_1_user_1_email(), "emptyarray", "none", "online", "none", "none", "windows", siteID1, "source_Name","machine", "true"},
			{"filter_name06", itd.getDirect_org_1_user_1_email(), "none", "none", "online", "none", "none", "windows", siteID1, "source_Name","machine", "true"},
			{"filter_name07", itd.getDirect_org_1_user_1_email(), null, "none", "online", "none", "none", "windows", siteID1, "source_Name","machine", "true"},
			// create source filter with different connection status
			{"filter_name08", itd.getDirect_org_1_user_1_email(), "protect", "none", "online,offline", "none", "none", "windows", siteID1, "source_Name","machine", "true"},
			{"filter_name09", itd.getDirect_org_1_user_1_email(), "protect", "none", "emptyarray", "none", "none", "windows", siteID1, "source_Name","machine", "true"},
			{"filter_name10", itd.getDirect_org_1_user_1_email(), "protect", "none", "none", "none", "none", "windows", siteID1, "source_Name","machine", "true"},
			{"filter_name11", itd.getDirect_org_1_user_1_email(), "protect", "none", null, "none", "none", "windows", siteID1, "source_Name","machine", "true"},
			// create source filter with different backup status
			{"filter_name12", itd.getDirect_org_1_user_1_email(), "protect", "none", "online", "active,finished", "none", "windows", siteID1, "source_Name","machine", "true"},
			{"filter_name13", itd.getDirect_org_1_user_1_email(), "protect", "none", "online", "emptyarray", "none", "windows", siteID1, "source_Name","machine", "true"},
			{"filter_name14", itd.getDirect_org_1_user_1_email(), "protect", "none", "online", "none", "none", "windows", siteID1, "source_Name","machine", "true"},
			{"filter_name15", itd.getDirect_org_1_user_1_email(), "protect", "none", "online", null, "none", "windows", siteID1, "source_Name","machine", "true"},
			// create source filter with different operating system
			{"filter_name16", itd.getDirect_org_1_user_1_email(), "protect", "none", "online", "none", "none", "windows,linux", siteID1, "source_Name","machine", "true"},
			{"filter_name17", itd.getDirect_org_1_user_1_email(), "protect", "none", "online", "none", "none", "emptyarray", siteID1, "source_Name","machine", "true"},
			{"filter_name18", itd.getDirect_org_1_user_1_email(), "protect", "none", "online", "none", "none", "none", siteID1, "source_Name","machine", "true"},
			{"filter_name19", itd.getDirect_org_1_user_1_email(), "protect", "none", "online", "none", "none", null, siteID1, "source_Name","machine", "true"},
			// create source filter with different siteID
			{"filter_name20", itd.getDirect_org_1_user_1_email(), "protect", "none", "online", "none", "none", "windows", siteID1 + "," + siteID2, "source_Name","machine", "true"},
			{"filter_name21", itd.getDirect_org_1_user_1_email(), "protect", "none", "online", "none", "none", "windows", "emptyarray", "source_Name","machine", "true"},
			{"filter_name22", itd.getDirect_org_1_user_1_email(), "protect", "none", "online", "none", "none", "windows", "none", "source_Name","machine", "true"},
			{"filter_name23", itd.getDirect_org_1_user_1_email(), "protect", "none", "online", "none", "none", "windows", null, "source_Name","machine", "true"},
			// create source filter with different source name
			{"filter_name24", itd.getDirect_org_1_user_1_email(), "protect", "none", "online", "none", "none", "windows", siteID1, "none","machine", "true"},
			{"filter_name25", itd.getDirect_org_1_user_1_email(), "protect", "none", "online", "none", "none", "windows", siteID1, "","machine", "true"},
			{"filter_name26", itd.getDirect_org_1_user_1_email(), "protect", "none", "online", "none", "none", "windows", siteID1, null,"machine", "true"},
			// create source filter with different source type
//			{"filter_name27", itd.getDirect_org_1_user_1_email(), "protect", "none", "online", "none", "none", "windows", siteID1, "none", "none", "true"},
//			{"filter_name28", itd.getDirect_org_1_user_1_email(), "protect", "none", "online", "none", "none", "windows", siteID1, "none", null, "true"},
//			{"filter_name28_a", itd.getDirect_org_1_user_1_email(), "protect", "none", "online", "none", "none", "windows", siteID1, "none", "agentless_vm", "true"},
			// create source filter with different default values
//			{"filter_name29", itd.getDirect_org_1_user_1_email(), "protect", "none", "online", "none", "none", "windows", siteID1, "none", "machine", ""},
//			{"filter_name30", itd.getDirect_org_1_user_1_email(), "protect", "none", "online", "none", "none", "windows", siteID1, "none", "machine", "none"},
			// create source filter with different policyID and groupID
			{"filter_name31", itd.getDirect_org_1_user_1_email(), "protect", UUID.randomUUID().toString() + ","  + UUID.randomUUID().toString(), "online", "none", UUID.randomUUID().toString() + ","  + UUID.randomUUID().toString(), "windows", siteID1, "none", "machine", "true"},
			{"filter_name32", itd.getDirect_org_1_user_1_email(), "protect", "emptyarray", "online", "none", "emptyarray", "windows", siteID1, "none", "machine", "true"},
			{"filter_name33", itd.getDirect_org_1_user_1_email(), "protect", "none", "online", "none", "none", "windows", siteID1, "none", "machine", "true"},
			{"filter_name34", itd.getDirect_org_1_user_1_email(), "protect", null, "online", "none", null, "windows", siteID1, "none", "machine", "true"},
			// update source filter with different filter name;
			{"", itd.getDirect_org_1_user_1_email(), "protect", null, "online", "none", null, "windows", siteID1, "none", "machine", "true"},
//			{null, itd.getDirect_org_1_user_1_email(), "protect", null, "online", "none", null, "windows", siteID1, "none", "machine", "true"},
//			{"none", itd.getDirect_org_1_user_1_email(), "protect", null, "online", "none", null, "windows", siteID1, "none", "machine", "true"},
			// update source filter and changes isDefault
			{"filter_name35", itd.getDirect_org_1_user_1_email(), "protect", null, "online", "none", null, "windows", siteID1, "none", "machine", "false"},
			
		};
	}
	
	@Test(dataProvider = "filter_info_valid_params")
	public void updateSourceFilterValidTest(String filter_name, String userName, String protection_status, String protection_policy, 
			String connection_status, String backup_status, String source_group, String operating_system, String site_id, String source_name, String source_type, String is_default) {
		spogServer.userLogin(userName, password);
		source4spogServer.setToken(spogServer.getJWTToken());
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		String filterID = source4spogServer.createSourcefilterForLoggedinUserWithCheck(prefix + "filtername", "protect", "online", null, "active", null, "windows", siteID1, "source_Name", "machine", "true", test);
		source4spogServer.updateSourcefilterForLoggedinUserWithCheck(filterID, filter_name, protection_status, 
				connection_status, protection_policy, backup_status, source_group, operating_system, site_id, source_name, source_type, is_default, test);
	}
	
	@DataProvider(name = "filter_info_invalid_params")
	public final Object[][] updateSourcefilterInfoInvalid() {
		return new Object[][] {
			// update a source filter and use an existing name;
			{"initial_filtername", itd.getDirect_org_1_user_1_email(), "protect", "none", "online", "none", "none", "windows", siteID1, "source_Name","machine", "true", 400, "00A00001"},
			// invalid protection status
			{"filterName", itd.getDirect_org_1_user_1_email(), "protectionStatus", "none", "online", "none", "none", "windows", siteID1, "source_Name","machine", "true", 400, "40000006"},
			// invalid protection policy
			{"filterName", itd.getDirect_org_1_user_1_email(), "protect", "protectionPolicy", "online", "none", "none", "windows", siteID1, "source_Name","machine", "true", 400, "40000005"},
			// invalid connection status
			{"filterName", itd.getDirect_org_1_user_1_email(), "protect", "none", "connectionStatus", "none", "none", "windows", siteID1, "source_Name","machine", "true", 400, "40000006"},
			// invalid backup status
			{"filterName", itd.getDirect_org_1_user_1_email(), "protect", "none", "none", "backupStatus", "none", "windows", siteID1, "source_Name","machine", "true", 400, "40000006"},
			// invalid source group
			{"filterName", itd.getDirect_org_1_user_1_email(), "protect", "none", "none", "none", "sourceGroup", "windows", siteID1, "source_Name","machine", "true", 400, "40000005"},
			// invalid operation system
			{"filterName", itd.getDirect_org_1_user_1_email(), "protect", "none", "none", "none", "none", "operatingsystem", siteID1, "source_Name","machine", "true", 400, "40000006"},
			// invalid siteID
//			{"filterName", itd.getDirect_org_1_user_1_email(), "protect", "none", "none", "none", "none", "windows", "siteID", "source_Name","machine", "true", 400, "40000005"},
			// invalid source type
//			{"filterName", itd.getDirect_org_1_user_1_email(), "protect", "none", "none", "none", "none", "windows", siteID1, "source_Name", "sourceType", "true", 400, "40000006"},
			// invalid isDefault
//			{"filterName", itd.getDirect_org_1_user_1_email(), "protect", "none", "none", "none", "none", "windows", siteID1, "source_Name", "machine", "isDefault", 400, "00100001"},
		};}
	
	@Test(dataProvider = "filter_info_invalid_params")
	public void updateSourceFilterInvalidTest(String filter_name, String userName, String protection_status, String protection_policy, 
			String connection_status, String backup_status, String source_group, String operating_system, String site_id, String source_name, String source_type, String is_default, 
			int statusCode, String errorCode) {
		spogServer.userLogin(userName, password);
		source4spogServer.setToken(spogServer.getJWTToken());
		
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		String filterID = source4spogServer.createSourcefilterForLoggedinUserWithCheck(prefix + "filtername", "protect", "online", null, "active", null, "windows", siteID1, "source_Name", "machine", "true", test);
		source4spogServer.updateSourcefilterForLoggedinUserWithCodeCheck(filterID, filter_name, protection_status, 
				connection_status, protection_policy, backup_status, source_group, operating_system, site_id, source_name, source_type, is_default, statusCode, errorCode, test);
	}
	
	@DataProvider(name = "filter_info_notoken_params")
	public final Object[][] updateSourcefilterInfoNoToken() {
		return new Object[][] {
			{"filtername", itd.getDirect_org_1_user_1_email(), "protect", "none", "online", "none", "none", "windows", siteID1, "source_Name","machine", "true", 401, "00900006"},
			{"filtername", itd.getMsp_org_1_user_1_email(), "protect", "none", "online", "none", "none", "windows", siteID1, "source_Name","machine", "true", 401, "00900006"},
			{"filtername", itd.getMsp_org_1_sub_1_user_1_email(), "protect", "none", "online", "none", "none", "windows", siteID1, "source_Name","machine", "true", 401, "00900006"},
			{"filtername", itd.getRoot_msp_org_1_user_1_email(), "protect", "none", "online", "none", "none", "windows", siteID1, "source_Name","machine", "true", 401, "00900006"},
			{"filtername", itd.getRoot_msp_org_1_account_admin_1_email(), "protect", "none", "online", "none", "none", "windows", siteID1, "source_Name","machine", "true", 401, "00900006"},
			{"filtername", itd.getRoot_msp_org_1_account_1_user_1_email(), "protect", "none", "online", "none", "none", "windows", siteID1, "source_Name","machine", "true", 401, "00900006"},
			{"filtername", itd.getRoot_msp_org_1_sub_msp_1_account_admin_1_email(), "protect", "none", "online", "none", "none", "windows", siteID1, "source_Name","machine", "true", 401, "00900006"},
			{"filtername", itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1_email(), "protect", "none", "online", "none", "none", "windows", siteID1, "source_Name","machine", "true", 401, "00900006"},
			{"filtername", itd.getRoot_msp_org_1_sub_msp_1_user_1_email(), "protect", "none", "online", "none", "none", "windows", siteID1, "source_Name","machine", "true", 401, "00900006"},

		};}
	
	@Test(dataProvider = "filter_info_notoken_params")
	public void updateSourceFilterNoTokenTest(String filter_name, String userName, String protection_status, String protection_policy, 
			String connection_status, String backup_status, String source_group, String operating_system, String site_id, String source_name, String source_type, String is_default, 
			int statusCode, String errorCode) {
		spogServer.userLogin(userName, password);
		source4spogServer.setToken(spogServer.getJWTToken());
		String filterID = source4spogServer.createSourcefilterForLoggedinUserWithCheck(filter_name, protection_status, 
				connection_status, protection_policy, backup_status, source_group, operating_system, site_id, source_name, source_type, is_default, test);
		source4spogServer.setToken("");
		source4spogServer.updateSourcefilterForLoggedinUserWithCodeCheck(filterID, filter_name, protection_status, 
				connection_status, protection_policy, backup_status, source_group, operating_system, site_id, source_name, source_type, is_default, statusCode, errorCode, test);
	}
	
	@Test()
	public void csrReadonlyTest() {
		spogServer.userLogin(csr_readonly_email, csr_readonly_password);
		source4spogServer.setToken(spogServer.getJWTToken());
		//(filter_name, protection_status, connection_status, protection_policy, backup_status, source_group, operating_system, site_id, source_name, source_type, is_default, test);
		String filterID = source4spogServer.createSourcefilterForLoggedinUserWithCheck("filterName" + RandomStringUtils.randomAlphanumeric(8), "protect", "online", uuid1, "finished", 
				uuid1, "windows", uuid1, "sourceName1", "machine", "true", test);
		source4spogServer.updateSourcefilterForLoggedinUserWithCheck(filterID, "filterName_updated" + RandomStringUtils.randomAlphanumeric(8), "protect", "offline", uuid1, "finished", 
				uuid1, "windows", uuid1, "sourceName1", "machine", "true", test);
		
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
