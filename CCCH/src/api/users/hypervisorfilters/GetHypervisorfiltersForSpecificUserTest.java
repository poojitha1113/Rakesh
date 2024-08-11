package api.users.hypervisorfilters;

import org.testng.annotations.Test;

import com.google.inject.PrivateBinder;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.sun.mail.handlers.message_rfc822;

import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGJobServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import bsh.org.objectweb.asm.Constants;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.User;
import genericutil.testcasescount;
import groovyjarjarantlr.PreservingFileWriter;
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
import Constants.SourceType;

public class GetHypervisorfiltersForSpecificUserTest {


	private SPOGServer spogServer;
	private UserSpogServer userSpogServer;
	private String csrAdmin;
	private String csrPwd;
	private ExtentReports rep;
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

	private SQLServerDb bqdb1;
	public int Nooftest;
	private long creationTime;
	private String BQName = null;
	private String runningMachine;
	private testcasescount count1;
	private String buildVersion;
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
		
		userSpogServer = new UserSpogServer(baseURI, port);
		this.csrAdmin = csrAdminUserName;
		this.csrPwd = csrAdminPassword;
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		String prefix = RandomStringUtils.randomAlphanumeric(8);

		// create msp organization #1, and then 1 sub-organizations in it, create one
		// admin for the sub organization;
		this.initial_msp_email_full = prefix + this.initial_msp_email;
		this.initial_msp_orgID = spogServer.CreateOrganizationWithCheck(prefix + initial_msp_org_name,
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
		this.initial_direct_orgID = spogServer.CreateOrganizationWithCheck(prefix + initial_direct_org_name,
				SpogConstants.DIRECT_ORG, prefix + initial_direct_email, password, prefix + initial_direct_first_name,
				prefix + initial_direct_last_name);
		
		this.initial_direct_email_full_added = prefix + this.initial_direct_email_added;
		this.initial_direct_user_ID_added = spogServer.createUserAndCheck(prefix + this.initial_direct_email_added, password,
				prefix + this.initial_direct_first_name_added, prefix + this.initial_direct_last_name_added,
				SpogConstants.DIRECT_ADMIN, initial_direct_orgID, test);
		
		
		// create initial hypervisorfilters
		spogServer.userLogin(initial_direct_email_full_added, password);
		userSpogServer.setToken(spogServer.getJWTToken());
		userSpogServer.createHypervisorFilterForLoggedinUser("filterName01", "status", "udp", "xen", "hypervisorName", "true", test);
		userSpogServer.createHypervisorFilterForLoggedinUser("filterName02", "status", "udp", "xen,vmware", "hypervisorName", "false", test);
		userSpogServer.createHypervisorFilterForLoggedinUser("filterName03", "status", "udp", "hyperv", "hypervisorName", "false", test);
		userSpogServer.createHypervisorFilterForLoggedinUser("filterName04", "status", "udp", "hyperv,xen", "hypervisorName", "false", test);
		userSpogServer.createHypervisorFilterForLoggedinUser("filterName05", "status", "udp", "kvm", "hypervisorName", "false", test);
		userSpogServer.createHypervisorFilterForLoggedinUser("filterName06", "status", "udp", "kvm,hyperv", "hypervisorName", "false", test);
		userSpogServer.createHypervisorFilterForLoggedinUser("filterName07", "status", "udp", "vmware,hyperv", "hypervisorName", "false", test);
		userSpogServer.createHypervisorFilterForLoggedinUser("filterName08", "status", "udp", "vmware,hyperv,kvm", "hypervisorName", "false", test);
		
		spogServer.userLogin(initial_msp_email_full_added, password);
		userSpogServer.setToken(spogServer.getJWTToken());
		userSpogServer.createHypervisorFilterForLoggedinUser("filterName01", "status", "udp", "xen", "hypervisorName", "true", test);
		userSpogServer.createHypervisorFilterForLoggedinUser("filterName02", "status", "udp", "xen,vmware", "hypervisorName", "false", test);
		userSpogServer.createHypervisorFilterForLoggedinUser("filterName03", "status", "udp", "hyperv", "hypervisorName", "false", test);
		userSpogServer.createHypervisorFilterForLoggedinUser("filterName04", "status", "udp", "hyperv,xen", "hypervisorName", "false", test);
		userSpogServer.createHypervisorFilterForLoggedinUser("filterName05", "status", "udp", "kvm", "hypervisorName", "false", test);
		userSpogServer.createHypervisorFilterForLoggedinUser("filterName06", "status", "udp", "kvm,hyperv", "hypervisorName", "false", test);
		userSpogServer.createHypervisorFilterForLoggedinUser("filterName07", "status", "udp", "vmware,hyperv", "hypervisorName", "false", test);
		userSpogServer.createHypervisorFilterForLoggedinUser("filterName08", "status", "udp", "vmware,hyperv,kvm", "hypervisorName", "false", test);
		
		spogServer.userLogin(initial_msp_account_email_full, password);
		userSpogServer.setToken(spogServer.getJWTToken());
		userSpogServer.createHypervisorFilterForLoggedinUser("filterName01", "status", "udp", "xen", "hypervisorName", "true", test);
		userSpogServer.createHypervisorFilterForLoggedinUser("filterName02", "status", "udp", "xen,vmware", "hypervisorName", "false", test);
		userSpogServer.createHypervisorFilterForLoggedinUser("filterName03", "status", "udp", "hyperv", "hypervisorName", "false", test);
		userSpogServer.createHypervisorFilterForLoggedinUser("filterName04", "status", "udp", "hyperv,xen", "hypervisorName", "false", test);
		userSpogServer.createHypervisorFilterForLoggedinUser("filterName05", "status", "udp", "kvm", "hypervisorName", "false", test);
		userSpogServer.createHypervisorFilterForLoggedinUser("filterName06", "status", "udp", "kvm,hyperv", "hypervisorName", "false", test);
		userSpogServer.createHypervisorFilterForLoggedinUser("filterName07", "status", "udp", "vmware,hyperv", "hypervisorName", "false", test);
		userSpogServer.createHypervisorFilterForLoggedinUser("filterName08", "status", "udp", "vmware,hyperv,kvm", "hypervisorName", "false", test);
		
		spogServer.userLogin(initial_sub_email_full_a_added, password);
		userSpogServer.setToken(spogServer.getJWTToken());
		userSpogServer.createHypervisorFilterForLoggedinUser("filterName01", "status", "udp", "xen", "hypervisorName", "true", test);
		userSpogServer.createHypervisorFilterForLoggedinUser("filterName02", "status", "udp", "xen,vmware", "hypervisorName", "false", test);
		userSpogServer.createHypervisorFilterForLoggedinUser("filterName03", "status", "udp", "hyperv", "hypervisorName", "false", test);
		userSpogServer.createHypervisorFilterForLoggedinUser("filterName04", "status", "udp", "hyperv,xen", "hypervisorName", "false", test);
		userSpogServer.createHypervisorFilterForLoggedinUser("filterName05", "status", "udp", "kvm", "hypervisorName", "false", test);
		userSpogServer.createHypervisorFilterForLoggedinUser("filterName06", "status", "udp", "kvm,hyperv", "hypervisorName", "false", test);
		userSpogServer.createHypervisorFilterForLoggedinUser("filterName07", "status", "udp", "vmware,hyperv", "hypervisorName", "false", test);
		userSpogServer.createHypervisorFilterForLoggedinUser("filterName08", "status", "udp", "vmware,hyperv,kvm", "hypervisorName", "false", test);
	}
	
	@DataProvider(name = "get_hypervisorfilters_valid")
	public final Object[][] getHypervisorfilterValidParams() {
		return new Object[][] {
			// get filter with different is_default
			{initial_direct_email_full_added, initial_direct_user_ID_added, 8},
			{initial_msp_email_full_added, initial_msp_user_ID_added, 8},
			{initial_sub_email_full_a_added, initial_sub_user_ID_a_added, 8},
			
			// get filter for other users in same org
			{initial_direct_email_full, initial_direct_user_ID_added, 8},
			{initial_msp_email_full, initial_msp_user_ID_added, 8},
			{initial_sub_email_full_a, initial_sub_user_ID_a_added, 8},
			
			// msp admin get filter in sub org
			{initial_msp_email_full, initial_sub_user_ID_a_added, 8},
			
			// msp account admin
			{initial_msp_email_full, initial_msp_account_userID, 8},
			{initial_msp_account_email_full, initial_sub_user_ID_a_added, 8},
		};
	}
	
	@Test(dataProvider = "get_hypervisorfilters_valid")
	public void getHypervisorfilterValid(String username, String userID, int expectedNumber) {
		spogServer.userLogin(username, password);
		userSpogServer.setToken(spogServer.getJWTToken());
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		
		Response response = userSpogServer.getHypervisorFiltersForSpecificUser(userID, test);
		
		response.then().body("pagination.total_size", equalTo(expectedNumber));
		ArrayList<Map<String, Object>> hypervisorFilters = response.then().extract().path("data");
		assertEquals(expectedNumber, hypervisorFilters.size());
	}
	
	@DataProvider(name = "get_hypervisorfilters_invalid")
	public final Object[][] getHypervisorfilterInvalidParams() {
		return new Object[][] {
			
			{initial_direct_email_full_added, initial_msp_user_ID_added, 403, "00100101"},
			{initial_direct_email_full_added, initial_sub_user_ID_a_added, 403, "00100101"},
			{initial_msp_email_full_added, initial_direct_user_ID_added, 403, "00100101"},
			{initial_sub_email_full_a_added, initial_msp_user_ID_added, 403, "00100101"},
			{initial_sub_email_full_a_added, initial_direct_user_ID_added, 403, "00100101"},
			
			// msp account admin
			{initial_msp_account_email_full, initial_msp_user_ID_added, 403, "00100101"},
			{initial_msp_account_email_full, initial_direct_user_ID_added, 403, "00100101"},
			{initial_msp_account_email_full, initial_sub_user_ID_b_added, 403, "00100101"},
			{initial_sub_email_full_a_added, initial_msp_account_userID, 403, "00100101"},
			{initial_direct_email_full_added, initial_msp_account_userID, 403, "00100101"},
			
			{initial_direct_email_full, UUID.randomUUID().toString(), 404, "00200007"},
			{initial_msp_email_full, UUID.randomUUID().toString(), 404, "00200007"},
			{initial_sub_email_full_a, UUID.randomUUID().toString(), 404, "00200007"},
			
			{initial_direct_email_full, "invaliUUID", 400, "40000005"},
			{initial_msp_email_full, "invaliUUID", 400, "40000005"},
			{initial_sub_email_full_a, "invaliUUID", 400, "40000005"},
			
		};
	}
	
	@Test(dataProvider = "get_hypervisorfilters_invalid")
	public void getHypervisorfilterInvalid(String username, String userID, int statusCode, String expectedErrorCode) {
		spogServer.userLogin(username, password);
		userSpogServer.setToken(spogServer.getJWTToken());
	
		userSpogServer.getHypervisorFiltersForSpecificUserWithCodeCheck(userID, statusCode, expectedErrorCode,test);
	}
	
	@DataProvider(name = "get_hypervisorfilters_no_token")
	public final Object[][] getHypervisorfilterNoTokenParams() {
		return new Object[][] {
			
			{initial_direct_email_full_added, initial_msp_user_ID_added, 401, "00900006"},
			{initial_direct_email_full_added, initial_sub_user_ID_a_added, 401, "00900006"},
			{initial_msp_email_full_added, initial_direct_user_ID_added, 401, "00900006"},
			
		};
	}
	
	@Test(dataProvider = "get_hypervisorfilters_no_token")
	public void getHypervisorfilterNoToken(String username, String userID, int statusCode, String expectedErrorCode) {
		spogServer.userLogin(username, password);
		userSpogServer.setToken("");
	
		userSpogServer.getHypervisorFiltersForSpecificUserWithCodeCheck(userID, statusCode, expectedErrorCode,test);
	}
	
	
	@DataProvider(name = "get_hypervisorfilters_pagination")
	public final Object[][] getHypervisorfiltersPaginationParams(){
		return new Object[][] {
			{"filter_name;=;filterName", "create_ts;desc", 1, 2, new String[] {"filterName08", "filterName07"}},
			{"filter_name;=;filterName", "create_ts;asc", 2, 2, new String[] {"filterName03", "filterName04"}},
			{"filter_name;=;filterName05", "create_ts;asc", 1, 2, new String[] {"filterName05"}},
			{"is_default;=;true", "create_ts;asc", 1, 2, new String[] {"filterName01"}},
		};
	}
	
	@Test(dataProvider = "get_hypervisorfilters_pagination")
	public void getHypervisorfiltersPagination(String filterStr, String sortStr, int pageNumber, int pageSize, String[] expectedFilterNames) {
		
		
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		String username = RandomStringUtils.randomAlphanumeric(8) + "spogqa@arcserve.com";
		
		
		spogServer.CreateOrganizationWithCheck("organizationName", SpogConstants.DIRECT_ORG, username, password, "organizationFirstName", "organizationLastName");
		spogServer.userLogin(username, password);
		String userID = spogServer.GetLoggedinUser_UserID();
		
		userSpogServer.setToken(spogServer.getJWTToken());
		userSpogServer.createHypervisorFilterForLoggedinUser("filterName01", "status", "udp", "xen", "hypervisorName", "true", test);
		userSpogServer.createHypervisorFilterForLoggedinUser("filterName02", "status", "udp", "xen,vmware", "hypervisorName", "false", test);
		userSpogServer.createHypervisorFilterForLoggedinUser("filterName03", "status", "udp", "hyperv", "hypervisorName", "false", test);
		userSpogServer.createHypervisorFilterForLoggedinUser("filterName04", "status", "udp", "hyperv,xen", "hypervisorName", "false", test);
		userSpogServer.createHypervisorFilterForLoggedinUser("filterName05", "status", "udp", "kvm", "hypervisorName", "false", test);
		userSpogServer.createHypervisorFilterForLoggedinUser("filterName06", "status", "udp", "kvm,hyperv", "hypervisorName", "false", test);
		userSpogServer.createHypervisorFilterForLoggedinUser("filterName07", "status", "udp", "vmware,hyperv", "hypervisorName", "false", test);
		userSpogServer.createHypervisorFilterForLoggedinUser("filterName08", "status", "udp", "vmware,hyperv,kvm", "hypervisorName", "false", test);
		
		Response response = userSpogServer.getHypervisorFiltersForSpecificUser(userID, filterStr, sortStr, pageNumber, pageSize, test);
		ArrayList<String> actualFilterNames = response.then().extract().path("data.filter_name");
		int size = expectedFilterNames.length;
		assertEquals(size, actualFilterNames.size());
		
		for (int i = 0; i < size; i++) {
			assertEquals(expectedFilterNames[i], actualFilterNames.get(i));
		}
		
	}
	
	@AfterMethod
	public void afterMethodTest() {
		rep.endTest(test);
		rep.flush();
	}

	@AfterClass
	public void deleteOrgs() {
		
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

	@AfterClass
	public void aftertest() {
		test.log(LogStatus.INFO, "The total test cases passed are " + count1.getpassedcount());
		test.log(LogStatus.INFO, "the total test cases failed are " + count1.getfailedcount());
		rep.flush();
	}

	@AfterClass
	public void updatebd() {
		try {
			if (count1.getfailedcount() > 0) {
				Nooftest = count1.getpassedcount() + count1.getfailedcount() + count1.getskippedcount();
				bqdb1.updateTable(this.BQName, this.runningMachine, this.buildVersion, String.valueOf(Nooftest),
						Integer.toString(count1.getpassedcount()), Integer.toString(count1.getfailedcount()),
						String.valueOf(count1.getskippedcount()), count1.getcreationtime(), "Failed");
			} else {
				Nooftest = count1.getpassedcount() + count1.getfailedcount() + count1.getskippedcount();
				bqdb1.updateTable(this.BQName, this.runningMachine, this.buildVersion, String.valueOf(Nooftest),
						Integer.toString(count1.getpassedcount()), Integer.toString(count1.getfailedcount()),
						String.valueOf(count1.getskippedcount()), count1.getcreationtime(), "Passed");
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
