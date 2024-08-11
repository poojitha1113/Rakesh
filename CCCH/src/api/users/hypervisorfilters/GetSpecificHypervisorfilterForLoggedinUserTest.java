package api.users.hypervisorfilters;

import org.testng.annotations.Test;

import com.google.inject.PrivateBinder;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.sun.mail.handlers.message_rfc822;

import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGHypervisorsServer;
import InvokerServer.SPOGJobServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import api.organizations.CreateAccountTest;
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
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;

public class GetSpecificHypervisorfilterForLoggedinUserTest {


	private SPOGServer spogServer;
	private UserSpogServer userSpogServer;
	private Policy4SPOGServer policy4SPOGServer;
	private SPOGDestinationServer spogDestinationServer;
	private SPOGHypervisorsServer spogHypervisorsServer;
	private GatewayServer gatewayServer; 

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
		  
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		spogHypervisorsServer = new SPOGHypervisorsServer(baseURI, port);  
		gatewayServer = new GatewayServer(baseURI, port);
		
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
		
		
		// create initial hypervisorfilter
		spogServer.userLogin(initial_direct_email_full_added, password);
		userSpogServer.setToken(spogServer.getJWTToken());
		userSpogServer.createHypervisorFilterForLoggedinUser("filterName_initial", "status", "udp", "xen", "hypervisorName", "true", test);
	}
	
	@DataProvider(name = "get_hypervisorfilter_valid")
	public final Object[][] getHypervisorfilterValidParams() {
		return new Object[][] {
			// get filter with different is_default
			{initial_direct_email_full_added, "filterName01", "status", "udp", "xen", "hypervisorName", "true"},
			{initial_direct_email_full_added, "filterName01", "status", "udp", "xen", "hypervisorName", "false"},
			{initial_direct_email_full_added, "filterName01", "status", "udp", "xen", "hypervisorName", "none"},
			{initial_direct_email_full_added, "filterName01", "status", "udp", "xen", "hypervisorName", null},
			
			// get filter with different hypervisorName
			{initial_direct_email_full_added, "filterName02", "status", "udp", "xen", "", "true"},
			{initial_direct_email_full_added, "filterName02", "status", "udp", "xen", "none", "true"},
			{initial_direct_email_full_added, "filterName02", "status", "udp", "xen", null, "true"},
			
			// get filter with different hypervisorType
			{initial_direct_email_full_added, "filterName08", "status", "udp", "xen,vmware", "hypervisorName", "true"},
			{initial_direct_email_full_added, "filterName08", "status", "udp", "none", "hypervisorName", "true"},
			{initial_direct_email_full_added, "filterName08", "status", "udp", "emptyarray", "hypervisorName", "true"},
			{initial_direct_email_full_added, "filterName08", "status", "udp", null, "hypervisorName", "true"},
			
			// TODO: create filter with different status
			
			// TODO: create filter with different hypervisor_product
			
			{initial_direct_email_full, "filterName03", "status", "udp", "xen", "hypervisorName", "true"},
			{initial_msp_email_full, "filterName03", "status", "udp", "xen", "hypervisorName", "true"},
			{initial_sub_email_full_a, "filterName03", "status", "udp", "xen", "hypervisorName", "true"},
			{initial_msp_account_email_full, "filterName03", "status", "udp", "xen", "hypervisorName", "true"},
			
		};
	}
	
	@Test(dataProvider = "get_hypervisorfilter_valid")
	public void getHypervisorfilterValid(String username, String filterName, String status, String hypervisorProduct, 
			String hypervisorType, String hypervisorName, String isDefault) {
		spogServer.userLogin(username, password);
		String userID = spogServer.GetLoggedinUser_UserID();
		userSpogServer.setToken(spogServer.getJWTToken());
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		filterName = prefix + filterName;
		String filterID = userSpogServer.createHypervisorFilterForLoggedinUser(filterName, status, hypervisorProduct, hypervisorType, hypervisorName, isDefault, test);
		Response response = userSpogServer.getHypervisorFilterForLoggedinUser(filterID, test);
		userSpogServer.verifyHypervisorfilter(response, filterName, status, hypervisorProduct, hypervisorType, hypervisorName, isDefault);
		response.then().body("data.user_id", equalTo(userID));
	}
	
	@DataProvider(name = "get_hypervisorfilter_no_token")
	public final Object[][] getHypervisorfilternNoTokenParams() {
		return new Object[][] {
			{initial_direct_email_full_added, initial_msp_email_full_added, "filterName01", "status", "udp", "xen", "hypervisorName", "true", 401, "00900006"},
			{initial_direct_email_full_added, initial_sub_email_full_a_added, "filterName01", "status", "udp", "xen", "hypervisorName", "true", 401, "00900006"},
			{initial_msp_email_full_added, initial_direct_email_full_added, "filterName01", "status", "udp", "xen", "hypervisorName", "true", 401, "00900006"}
		};}
	
	@Test(dataProvider = "get_hypervisorfilter_no_token")
	public void getHypervisorfilterNoToken(String username, String usernameGet, String filterName, String status, String hypervisorProduct, 
			String hypervisorType, String hypervisorName, String isDefault, int statusCode, String expectedErrorCode) {
		spogServer.userLogin(username, password);
		userSpogServer.setToken(spogServer.getJWTToken());
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		filterName = prefix + filterName;
		String filterID = userSpogServer.createHypervisorFilterForLoggedinUser(filterName, status, hypervisorProduct, hypervisorType, hypervisorName, isDefault, test);
		
		spogServer.userLogin(usernameGet, password);
		userSpogServer.setToken("");
		userSpogServer.getHypervisorFilterForLoggedinUserWithCodeCheck(filterID, statusCode, expectedErrorCode, test);
	}
	
	@DataProvider(name = "get_hypervisorfilter_non_existing")
	public final Object[][] getHypervisorfilternNonExistingParams() {
		return new Object[][] {
			{initial_direct_email_full_added, "filterID", 400, "40000005"},
			{initial_direct_email_full_added, UUID.randomUUID().toString(), 404, "00A00502"},
		};}
	
	@Test(dataProvider = "get_hypervisorfilter_non_existing")
	public void getHypervisorfilterNoExisting(String username, String filterID, int statusCode, String expectedErrorCode) {
		spogServer.userLogin(username, password);
		userSpogServer.setToken(spogServer.getJWTToken());
		userSpogServer.getHypervisorFilterForLoggedinUserWithCodeCheck(filterID, statusCode, expectedErrorCode, test);
	}
	
	@DataProvider(name = "get_hypervisorfilter_is_default")
	public final Object[][] getHypervisorfilternIsDefaultParams() {
		return new Object[][] {
			{initial_direct_email_full_added},
			{initial_msp_email_full_added},
			{initial_sub_email_full_a_added},
		};}
	
	@Test(dataProvider = "get_hypervisorfilter_is_default")
	public void getHypervisorfilterIsDefaultTest(String username) {
		spogServer.userLogin(username, password);
		userSpogServer.setToken(spogServer.getJWTToken());
		
		String filterID1 = userSpogServer.createHypervisorFilterForLoggedinUser("filterName_isDefault01", "status", "udp", "xen", "hypervisorName", "true", test);
		String filterID2 = userSpogServer.createHypervisorFilterForLoggedinUser("filterName_isDefault02", "status", "udp", "xen", "hypervisorName", "true", test);
		Response response = userSpogServer.getHypervisorFilterForLoggedinUser(filterID1, test);
		response.then().body("data.is_default", equalTo(false));
		response = userSpogServer.getHypervisorFilterForLoggedinUser(filterID2, test);
		response.then().body("data.is_default", equalTo(true));
		
		String filterID3 = userSpogServer.createHypervisorFilterForLoggedinUser("filterName_isDefault03", "status", "udp", "xen", "hypervisorName", "false", test);
		userSpogServer.updateHypervisorFilterForLoggedinUser(filterID3, "filterName_isDefault03", "status", "udp", "xen", "hypervisorName", "true", test);
		response = userSpogServer.getHypervisorFilterForLoggedinUser(filterID2, test);
		response.then().body("data.is_default", equalTo(false));
		response = userSpogServer.getHypervisorFilterForLoggedinUser(filterID3, test);
		response.then().body("data.is_default", equalTo(true));
	}
	
	@Test
	public void getHypervisorCountTest() {
		
		  String username = "vfhn8rwomail@arcserve.com";
		  String password = "Pa$$w0rd";
		  
		  String prefix = RandomStringUtils.randomAlphanumeric(8);
		  String task_id = UUID.randomUUID().toString();
		  String policy_id1 = UUID.randomUUID().toString();
		  
		  ExtentTest test = new ExtentTest("testname", "description");
		  spogServer.userLogin(csrAdmin, csrPwd);
		  String user_token=spogServer.getJWTToken();
		  
		  String directOrgId1 = "a40fbcda-a684-4347-938f-d27e6efece18";
		  String destination_id_ret1 = "e9639f91-61ce-452b-84c0-0a0880c59865";
		  String cloud_direct_account_id = "dcef5f30-7217-4d00-a14a-bc82ff168c9d";
		  String siteID = "dcef5f30-7217-4d00-a14a-bc82ff168c9d";
		  String cloudAccountSecret= "cloudAccountSecret";	
//		  String pmfKey="mabzh02";
//		  String pmfKey_prefix=spogServer.ReturnRandom(pmfKey);
//		  String policy_id=spogServer.returnRandomUUID();
//		  String task_id=spogServer.returnRandomUUID();
		  
//		  String directOrgNameForPrepare= prefix + "spogqa_direct_org_";
//		  String directOrgEmailForPrepare1= prefix +"@arcserve.com";
//		  String orgPwdForPrepare= "Caworld_2017";
//		  ExtentTest test = new ExtentTest("testname", "description");
//		  spogServer.userLogin(csrAdmin, csrPwd);
//		  String user_token=spogServer.getJWTToken();
//		  //create cloud account and create destination
//		  String directOrgId1 = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(directOrgNameForPrepare), SpogConstants.DIRECT_ORG, directOrgEmailForPrepare1, orgPwdForPrepare, "dd", "dd");
//		  
//		  spogServer.userLogin(directOrgEmailForPrepare1, orgPwdForPrepare);
//		  
//		  String cloud_direct_account_id = spogServer.createCloudAccountWithCheck("cloudAccountKey", "cloudAccountSecret", prefix + "cloudAccountName", "cloud_direct", directOrgId1, 
//					"SKUTESTDATA_1_0_0_0_"+prefix, "SKUTESTDATA_1_0_0_0_"+prefix, "SKUTESTDATA_1_0_0_0_"+ prefix , "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
//	      spogDestinationServer.setToken(user_token);
//	      String destination_id_ret1 = spogDestinationServer.createDestinationWithCheck(  "",directOrgId1, cloud_direct_account_id,"91a9b48e-6ac6-4c47-8202-614b5cdcfe0c","cloud_direct_volume", "running", "1",
//						  cloud_direct_account_id, "normal", prefix, "2M", "2M", "0", "0", "31", "0", "2", "0", "E:\\destination",
//							"E:\\data", "E:\\index", "E:\\hash", "5", "true", "1", "512", "true", "123", "120", "300", "10", "20",
//							"dest", test); 
		  Response response = gatewayServer.LoginSite(siteID, cloudAccountSecret, test);
			String token = response.then().extract().path("data.token");
			spogHypervisorsServer.setToken(token);
			
			String hypervisorID = spogHypervisorsServer.createHypervisorWithCheck("", prefix + "countTesthypervisorname", "vmware", "cloud_direct", "none", siteID, directOrgId1, "false", String.valueOf(System.currentTimeMillis()), 
					destination_id_ret1, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
					"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", test);
		
			spogServer.userLogin(username, password);
			userSpogServer.setToken(spogServer.getJWTToken());
		  String filter_id = userSpogServer.createHypervisorFilterForLoggedinUser("filtername" + prefix, "emptyarray", "emptyarray", "emptyarray", "countTesthypervisorname", "none", test);
		  response = userSpogServer.getHypervisorFilterForLoggedinUser(filter_id, test);
		  response.then().body("data.count", equalTo(1));
		  
		  userSpogServer.updateHypervisorFilterForLoggedinUser(filter_id, "filtername" + prefix, "emptyarray", "emptyarray", "emptyarray", "updated_countTesthypervisorname", "none", test);
		  response = userSpogServer.getHypervisorFilterForLoggedinUser(filter_id, test);
		  response.then().body("data.count", equalTo(0));
		  
		  spogHypervisorsServer.deleteHypervisorWithCheck(hypervisorID, test);
		  userSpogServer.deleteHypervisorFilterForLoggedinUser(filter_id, test);
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
