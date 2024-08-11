package CI;

import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

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
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.Log4GatewayServer;
import InvokerServer.SPOGServer;
import InvokerServer.ServerResponseCode;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import groovy.lang.TracingInterceptor;
import invoker.SiteTestHelper;
import invoker.SiteTestHelper.siteType;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.FilterableRequestSpecification;

public class CISmokeTest {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private Log4GatewayServer log4GatewayServer;
	private String csrAdmin;
	private String csrPwd;
	private ExtentReports rep;
	private ExtentTest test;
	private testcasescount count1;

	private String initial_msp_org_name = "spog_qa_msp_ci";
	private String initial_msp_org_name_d = "spog_qa_msp_ci_d";
	private String initial_msp_org_name_u = "spog_qa_msp_ci_u";
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
		this.initial_msp_orgID = spogServer.CreateOrganizationWithCheck(prefix + initial_msp_org_name,
				SpogConstants.MSP_ORG, this.initial_msp_email_full, password, prefix + initial_msp_first_name,
				prefix + initial_msp_last_name);
		test.log(LogStatus.INFO, "Create msp org");
		this.initial_msp_orgID_d = spogServer.CreateOrganizationWithCheck(prefix + initial_msp_org_name_d,
				SpogConstants.MSP_ORG, prefix + initial_msp_email_d, password, prefix + initial_msp_first_name,
				prefix + initial_msp_last_name);

		test.log(LogStatus.INFO, "Create msp org for deleted later");
		this.initial_msp_email_full_added = prefix + this.initial_msp_email_added;
		spogServer.createUserAndCheck(initial_msp_email_full_added, password, prefix + initial_msp_first_name_added,
				prefix + initial_msp_last_name, "msp_admin", initial_msp_orgID, test);
		test.log(LogStatus.INFO, "Create msp admin user");
		this.initial_sub_orgID_a = spogServer.createAccountWithCheck(initial_msp_orgID, initial_sub_org_name_a,
				initial_msp_orgID);
		test.log(LogStatus.INFO, "Create msp account org");
		spogServer.userLogin(initial_msp_email_full, password);
		this.initial_sub_email_full_a = prefix + initial_sub_email_a;
		spogServer.createUserAndCheck(prefix + this.initial_sub_email_a, password,
				prefix + this.initial_sub_first_name_a, prefix + this.initial_sub_last_name_a,
				SpogConstants.DIRECT_ADMIN, initial_sub_orgID_a, test);

		this.initial_sub_email_full_a_added = prefix + this.initial_sub_email_a_added;
		spogServer.createUserAndCheck(prefix + this.initial_sub_email_a_added, password,
				prefix + this.initial_sub_first_name_a_added, prefix + this.initial_sub_last_name_a_added,
				SpogConstants.DIRECT_ADMIN, initial_sub_orgID_a, test);
		this.initial_sub_email_full_a_deleted = prefix + this.initial_sub_email_a_deleted;
		spogServer.createUserAndCheck(prefix + this.initial_sub_email_a_deleted, password,
				prefix + this.initial_sub_first_name_a_deleted, prefix + this.initial_sub_last_name_a_deleted,
				SpogConstants.DIRECT_ADMIN, initial_sub_orgID_a, test);
		test.log(LogStatus.INFO, "Create msp account users");
		// create 1 direct organizations;
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		this.initial_direct_email_full = prefix + this.initial_direct_email;
		this.initial_direct_orgID = spogServer.CreateOrganizationWithCheck(prefix + initial_direct_org_name,
				SpogConstants.DIRECT_ORG, prefix + initial_direct_email, password, prefix + initial_direct_first_name,
				prefix + initial_direct_last_name);

		this.initial_direct_email_full_added = prefix + this.initial_direct_email_added;
		spogServer.createUserAndCheck(prefix + this.initial_direct_email_added, password,
				prefix + this.initial_direct_first_name_added, prefix + this.initial_direct_last_name_added,
				SpogConstants.DIRECT_ADMIN, initial_direct_orgID, test);
		test.log(LogStatus.INFO, "Create direct org and admin user");
	}

	@Test
	public void OrganizationAndUserRelatedTest() {
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		// create msp org and one msp admin user for get and update
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		String defaultUserEmail = prefix + initial_msp_email_u;
		String defaultUserFirstName = prefix + initial_msp_first_name;
		String defaultUserLastName = prefix + initial_msp_last_name;
		String msp_new_org_name = prefix + initial_msp_org_name_u + "_modify";
		ArrayList<ResponseBody> userInfo = new ArrayList<ResponseBody>();
		String initial_msp_orgID_u = spogServer.CreateOrganizationWithCheck(prefix + initial_msp_org_name_u,
				SpogConstants.MSP_ORG, null, null, null, null);
		// create user
		Response user_response = spogServer.createUser(defaultUserEmail, password, defaultUserFirstName,
				defaultUserLastName, SpogConstants.MSP_ADMIN, initial_msp_orgID_u, test);
		String msp_user_u_id = spogServer.checkCreateUser(user_response, SpogConstants.SUCCESS_POST, defaultUserEmail,
				defaultUserFirstName, defaultUserLastName, SpogConstants.MSP_ADMIN, initial_msp_orgID_u, "", test);
		test.log(LogStatus.INFO, "create msp admin user");
		// login user
		spogServer.userLogin(defaultUserEmail, password);
		test.log(LogStatus.INFO, "login as msp admin user");
		String validToken = spogServer.getJWTToken();

		// get all the users in the logged in user's organization
		userInfo.add(user_response.body());
		String filterStr = null;
		String sortStr = null;
		int pageNumber = -1;
		int pageSize = -1;
		Response response = spogServer.getAllUsersInLoggedOrganization(filterStr, sortStr, pageNumber, pageSize, test);
		spogServer.checkGetAllUsersInLoggedOrganization(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userInfo,
				pageNumber, pageSize, userInfo.size(), null, test);
		test.log(LogStatus.INFO, "get all the users in the logged in organization");

		// get accounts for specified msp
		ArrayList<HashMap> ExpectedAccounts = new ArrayList<HashMap>();
		ArrayList<ResponseBody> Accounts = new ArrayList<ResponseBody>();
		String organization_name = spogServer.ReturnRandom("spogqa_account");
		Response create_account_response = spogServer.createAccountWithCheck2(initial_msp_orgID_u, organization_name,
				initial_msp_orgID_u, test);
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("organization_id", create_account_response.jsonPath().getUUID("data.organization_id").toString());
		map.put("organization_name", create_account_response.jsonPath().getString("data.organization_name"));
		map.put("organization_type", create_account_response.jsonPath().getString("data.organization_type"));
		map.put("parent_id", create_account_response.jsonPath().getUUID("data.parent_id").toString());
		map.put("create_ts", create_account_response.jsonPath().getString(("data.create-ts")));
		ExpectedAccounts.add(map);
		Accounts.add(response.getBody());
		test.log(LogStatus.INFO, "get all the Accounts for the Specified Msp:" + ExpectedAccounts.size());
		String additonalURL = spogServer.PrepareURL(filterStr, sortStr, pageNumber, pageSize, test);
		Response all_account_response = spogServer.getAllaccountsForSpecifiedMsp(validToken, initial_msp_orgID_u,
				additonalURL, test);
		test.log(LogStatus.INFO, "validing the response generated for get all accounts for specifed msp");

		// get all users in one organization
		ArrayList<HashMap> expectedUsers = new ArrayList<HashMap>();
		HashMap<String, String> m = new HashMap();
		m.put("user_id", msp_user_u_id);
		m.put("role_id", SpogConstants.MSP_ADMIN);
		m.put("organization_id", initial_msp_orgID_u);
		m.put("email", defaultUserEmail.toLowerCase());
		m.put("first_name", defaultUserFirstName);
		m.put("last_name", defaultUserLastName);
		expectedUsers.add(m);
		spogServer.CompareUsersByOrganizationID(initial_msp_orgID_u, expectedUsers, test);
		test.log(LogStatus.INFO, "get organization by id and get all users from the organization");

		// get user by id
		Response logged_in_user_response = spogServer.getLoggedUserInfoByID(validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, msp_user_u_id, test);
		spogServer.checkLoggedInUser(logged_in_user_response, SpogConstants.SUCCESS_GET_PUT_DELETE, "",
				defaultUserEmail, msp_user_u_id, initial_msp_orgID_u, test);
		test.log(LogStatus.INFO, "get user by id");

		// get the logged in user
		Response login_user_response = spogServer.getLoggedInUser(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE,
				test);
		spogServer.checkLoggedInUser(login_user_response, SpogConstants.SUCCESS_GET_PUT_DELETE,
				SpogMessageCode.SUCCESS_GET_PUT_DEL, defaultUserEmail, test);
		test.log(LogStatus.INFO, "get the logged in user of the msp organization");

		// update logged in user's organization
		spogServer.UpdateLoggedInOrganization(msp_new_org_name, test);
		test.log(LogStatus.INFO, "update organization by logged in user");

		// get organization by id
		String default_parent_id = "00000000-0000-0000-0000-000000000000";
		spogServer.GetOrganizationInfobyIDWithCheck(initial_msp_orgID_u, msp_new_org_name, SpogConstants.MSP_ORG,
				default_parent_id, test);
		test.log(LogStatus.INFO, "get organization information by id");

		// get logged in user's organization
		spogServer.GetLoggedInUserOrganizationInfoWithCheck(msp_new_org_name, prefix + initial_msp_org_name_u,
				SpogConstants.MSP_ORG, initial_msp_orgID_u, test);
		test.log(LogStatus.INFO, "get organization information by logged in user");

		// update an organization by id
		spogServer.updateOrganizationInfoByID(initial_msp_orgID_u, msp_new_org_name + "d", test);
		test.log(LogStatus.INFO, "update organization by id");
		spogServer.GetLoggedInUserOrganizationInfoWithCheck(msp_new_org_name + "d", prefix + initial_msp_org_name_u,
				SpogConstants.MSP_ORG, initial_msp_orgID_u, test);

		// change password for logged in user
		String newPassword = spogServer.ReturnRandom("abcABC123");
		spogServer.changePasswordForLoggedInUserWithExpectedStatusCode(password, newPassword,
				SpogConstants.SUCCESS_LOGIN, test);
		spogServer.userLogin(defaultUserEmail, newPassword, SpogConstants.SUCCESS_LOGIN, test);
		test.log(LogStatus.INFO, "change password for logged in user");

		// change password for specified user
		String newPassword1 = spogServer.ReturnRandom("abcABC123");
		spogServer.changePasswordForSpecifiedUserWithExpectedStatusCode(msp_user_u_id, newPassword, newPassword1,
				SpogConstants.SUCCESS_LOGIN, test);
		spogServer.userLogin(defaultUserEmail, newPassword1, SpogConstants.SUCCESS_LOGIN, test);
		test.log(LogStatus.INFO, "change password for specified user");

		// update the logged in user
		String newPassword2 = spogServer.ReturnRandom("spogqaYYY111");
		String newFirstName2 = spogServer.ReturnRandom("spogqa_ci");
		String newLastName2 = spogServer.ReturnRandom("spogqa_ci");
		Response response1 = spogServer.UpdateLoggedInUser("", newPassword2, newFirstName2, newLastName2, "", "");
		spogServer.checkResponseStatus(response1, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		test.log(LogStatus.INFO, "updated logged in user");

		// update user by id
		newPassword2 = spogServer.ReturnRandom("spogqaYYY111");
		Response response2 = spogServer.updateUserById(msp_user_u_id, spogServer.ReturnRandom("spogqa_ci"),
				newPassword2, spogServer.ReturnRandom("spogqa_ci"), spogServer.ReturnRandom("spogqa_ci_update"), "", "",
				test);
		spogServer.checkResponseStatus(response2, SpogConstants.SUCCESS_GET_PUT_DELETE);
		test.log(LogStatus.INFO, "updated user by id");

		// create an account for msp
		String account_id;
		account_id = spogServer.createAccountWithCheck(initial_msp_orgID_u, spogServer.ReturnRandom("spogqa_ci"), "",
				test);
		test.log(LogStatus.INFO, "create an account for msp");

		// update an account for msp
		String suborgname = spogServer.ReturnRandom("spogqa_ci_account");
		spogServer.updateAccountWithCheck(initial_msp_orgID_u, account_id, suborgname, test);
		test.log(LogStatus.INFO, "update an account for msp");

		// get specified account for specified msp
		//spogServer.getsuborgaccountinfobyIdwithcheck(validToken, initial_msp_orgID_u, account_id,
	//			SpogConstants.SUCCESS_GET_PUT_DELETE, suborgname, SpogMessageCode.INVALID_LOG_ID, test);
		test.log(LogStatus.INFO,
				"Performing the operations to delete the user and organization by loggin in as csr admin");

		// delete specified account for specified msp
		spogServer.deleteMSPAccountWithCheck(initial_msp_orgID_u, account_id);
		test.log(LogStatus.INFO, "delete specified account for specified msp");
		spogServer.userLogin(csrAdmin, csrPwd);

		// delete a user by id
		spogServer.DeleteUserById(msp_user_u_id, test);
		test.log(LogStatus.INFO, "delete a user by id");

		// delete organization by id
		spogServer.DeleteOrganizationWithCheck(initial_msp_orgID_u, test);
		test.log(LogStatus.INFO, "delete organization by id");

		// get list of user roles (GET roles)
		test.log(LogStatus.INFO, "get list of user roles");
		response = spogServer.getUserroles("", "", -1, -1, test);
		ArrayList<Map<String, Object>> userRoles = response.then().extract().path("data");
		// currently the csr admin can get 3 user roles, it may need to change as we
		// introduce more user roles later
//		assertEquals(userRoles.size(), 4);

		// create cloud account
//		spogServer.userLogin(this.initial_direct_email_full, password);
//		String spogToken = spogServer.getJWTToken();
//		String userID = spogServer.GetLoggedinUser_UserID();
//		String cloudAccountKey = spogServer.returnRandomUUID();
//		String cloudAccountSecret = spogServer.ReturnRandom("directorg_directaccount_secret");
//		String cloudAccountName = spogServer.ReturnRandom("directorg_directaccount_name");
//		String cloudAccountType = "aws_s3";
//		String cloud_account_status = "success";
//		String orderid=spogServer.ReturnRandom("SKUTESTDATA_1_0_0_0_");
//		String fullfiledid=spogServer.ReturnRandom("SKUTESTDATA_1_0_0_0_");
//		String cloud_account_id = spogServer.createCloudAccountWithCheck(cloudAccountKey, cloudAccountSecret,
//				cloudAccountName, cloudAccountType, this.initial_direct_orgID,fullfiledid,orderid, test);
//		test.log(LogStatus.INFO, "create cloud account");
//		ArrayList<HashMap<String, Object>> cloudDatasDirect = new ArrayList<HashMap<String, Object>>();
//		HashMap<String, Object> cloudData = new HashMap<String, Object>();
//		cloudData = spogServer.composeCloudData(cloud_account_id, cloudAccountKey, initial_direct_orgID, userID,
//				cloudAccountName, cloud_account_status, cloudAccountType);
//		cloudDatasDirect.add(cloudData);

		// get the cloud account for the specified id
//		test.log(LogStatus.INFO, "Get the cloud accounts for the specified id");
//		Response cloud_response = spogServer.getCloudAccountById(spogToken, cloud_account_id, test);
//		test.log(LogStatus.INFO, "validating the response for the getCloudAccountsBySpecifiedId");
//		spogServer.checkGetCloudAccountById(cloud_response, SpogConstants.SUCCESS_GET_PUT_DELETE, 
//			cloud_account_id,initial_direct_orgID, cloudAccountKey, userID, cloudAccountName, cloudAccountType,
//			null,test);

		// get all cloud accounts
//		int page_number = 1;
//		int page_size = 20;
//		String cloud_filterStr = "organization_id;=;direct";
//		String[] filterArray = null;
//		String filterName = null, filterOperator = null;
//		filterArray = cloud_filterStr.split(";");
//		filterName = filterArray[0];
//		filterOperator = filterArray[1];
//		String newFilterStr = filterName + ";" + filterOperator + ";" + initial_direct_orgID;
//		test.log(LogStatus.INFO, "get filter");
//		test.log(LogStatus.INFO, "Preapring the URL for the Get cloud accounts:");
//		String additionalURL = spogServer.PrepareURL(newFilterStr, "", page_number, page_size, test);
//		cloud_response = spogServer.getCloudAccounts(spogToken, additionalURL, test);
//		spogServer.checkGetCloudAccounts(cloud_response, SpogConstants.SUCCESS_GET_PUT_DELETE, cloudDatasDirect,
//				page_number, page_size, newFilterStr, "", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

		// update cloud account
//		String newName = cloudAccountName + "_new";
//		Response update_account_response = spogServer.updateCloudAccount(cloud_account_id, "", "", newName, "", "",
//				test);
//		spogServer.checkResponseStatus(update_account_response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);

		// login account
//		Response login_account_response = spogServer.cloudDirectAccountLogin(cloudAccountKey, cloudAccountSecret, test);
//		spogServer.checkResponseStatus(login_account_response, SpogConstants.SUCCESS_LOGIN);

		// delete account
//		spogServer.deleteCloudAccountWithExpectedStatusCode(cloud_account_id, SpogConstants.SUCCESS_GET_PUT_DELETE,
//				test);

	}

	/**
	 * @author Zhaoguo.Ma
	 * @throws InterruptedException 
	 */
	@Test
	public void auditTrialTest() throws InterruptedException {
		spogServer.userLogin(csrAdmin, csrPwd);

		// define params for create organization
		String prefix = RandomStringUtils.randomAlphanumeric(8).toLowerCase();
		String orgName = prefix + "orgName";
		String orgType = "direct";
		String email = prefix + "email@arcserve.com";
		String firstName = prefix + "firstName";
		String lastName = prefix + "lastName";

		// define params for update organization
		String orgName_updated = prefix + "orgName_updated";

		test.log(LogStatus.INFO, "create an organization");
		String organizationID = spogServer.CreateOrganizationWithCheck(orgName, orgType, email, password, firstName,
				lastName, test);
		spogServer.userLogin(email, password);

		// define params for getAuditTrial
		String userToken = spogServer.getJWTToken();
		String userID = spogServer.GetLoggedinUser_UserID();

		// update login user to trigger the audittrail, the code_id is 5
		spogServer.UpdateLoggedInUser(email, password, firstName, lastName, SpogConstants.DIRECT_ADMIN, organizationID);

		// Audit API - (GET users/:id/audittrail)
		Response response = spogServer.getAuditDetailsForUsers(userToken, userID, "", test);
		ArrayList<Map<String, Object>> auditTrails = response.then().extract().path("data");
		//assertEquals(auditTrails.get(auditTrails.size() - 1).get("code_id"), 5);

		// update login organization to trigger the audittrail, the code_id is 108
		spogServer.UpdateLoggedInOrganization(orgName_updated, test);
		Thread.sleep(2000);
		// Audit API - (GET organizations/:id/audittrail)
		response = spogServer.getaudittrailbyorgId(userToken, organizationID, "", test);
		auditTrails = response.then().extract().path("data");
		//assertEquals(auditTrails.get(auditTrails.size() - 1).get("code_id"), 108);
	}

	/**
	 * @author Zhaoguo.Ma
	 */
	@Test
	public void sourceFilterRelatedTest() {
		spogServer.userLogin(this.initial_msp_email_full, password);
		String spogToken = spogServer.getJWTToken();
		String userID = spogServer.GetLoggedinUser_UserID();

		// define the params for create source filter
		String filterName = "filterName";
		String protectionStatus = "protect,unprotect";
		String connectionStatus = "offline,online";
		String protectionPolicy = UUID.randomUUID().toString();
		String backupStatus = "active,finished";
		String sourceGroup = UUID.randomUUID().toString();
		String operatingSystem = "linux,windows";
		String applications = "All,EXCHANGE,SQL_SERVER";
		String sourceType = "all";
		String isDefault = "true";

		// define the params for another source filter
		String filterName_added = "filterName_added";
		String isDefault_added = "false";
		String filterName_updated = "filterName_updated";

		// test create a source filter (POST users/:id/sourcefilters)
		test.log(LogStatus.INFO, "create source filter");
		String filterID = spogServer.createFilterwithCheck(userID, filterName, protectionStatus, connectionStatus,
				protectionPolicy, backupStatus, sourceGroup, operatingSystem, applications, sourceType, isDefault,
				test);

		// create another source filter for (update/get/delete)
		test.log(LogStatus.INFO, "create another source filter");
		String filterID_added = spogServer.createFilterwithCheck(userID, filterName_added, protectionStatus,
				connectionStatus, protectionPolicy, backupStatus, sourceGroup, operatingSystem, applications,
				sourceType, isDefault_added, test);

		// update source filter by ID (PUT users/:id/sourcefilters/:id1)
		test.log(LogStatus.INFO, "update source filter by ID");
		spogServer.updateFilterwithCheck(userID, filterID_added, filterName_updated, protectionStatus, connectionStatus,
				protectionPolicy, backupStatus, sourceGroup, operatingSystem, applications, sourceType, sourceType,
				isDefault_added, test);

		// get source filter by ID (Get users/:id/sourcefilters/:id1)
		test.log(LogStatus.INFO, "get source filter by ID");
		Response response = spogServer.getFilterByID(userID, filterID_added);
		response.then().body("data.filter_name", equalTo(filterName_updated));

		// delete source filter by ID (DELETE users/:id/sourcefilters/:id1)
		test.log(LogStatus.INFO, "delete source filter by ID");
		spogServer.deletefilterspecifiedbyUserIdwithCheck(userID, filterID_added, spogToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		// get source filters for specified user (GET users/:id/sourcefilters)
		test.log(LogStatus.INFO, "get source filters");
		response = spogServer.getFiltersByUserID(userID);
		ArrayList<Map<String, Object>> filters = new ArrayList<Map<String, Object>>();
		filters = response.then().extract().path("data");
		assertEquals(filters.size(), 1);
		assertEquals(filters.get(0).get("filter_id"), filterID);
		assertEquals(filters.get(0).get("filter_name"), filterName);
	}

	/**
	 * @author Zhaoguo.Ma
	 */
	@Test
	public void sourceAndSourceGroupTest() {
		// login use sub organization admin;
		spogServer.userLogin(initial_sub_email_full_a, password);
		String userToken = spogServer.getJWTToken();
		String prefix = RandomStringUtils.randomAlphanumeric(8);

		// define the params for createSource
		String userID = spogServer.GetLoggedinUser_UserID();
		String sourceName = spogServer.ReturnRandom("ci_source");
		String sourceType = "machine";
		String sourceProduct = "udp";
		String organizationID = initial_sub_orgID_a;
		String siteID = gatewayServer.createsite_register_login(initial_sub_orgID_a, userToken, userID, prefix + "ci",
				"1.0.0", spogServer, test);
		String protectionStatus = "protect";
		String connectionStatus = "online";
		String osMajor = "windows";
		String applications = "SQL_SERVER";

		// define the params for create another source (used for put/delete)
		String sourceName_added = "sourceName_added";
		String sourceType_added = "shared_folder";

		// define the params for update source
		String sourceName_updated = "sourceName_updated";

		// define the params for createSourceGroup
		String sourceGroupName = "sourceGroup";

		// define the params for create another source group (used for put/delete)
		String sourceGroupName_added = "sourceGroup_added";

		// define the params for update source group
		String sourceGroupName_updated = "sourceGroup_updated";

		// test create a source (POST sources)
		test.log(LogStatus.INFO, "create a source");
		String sourceID = spogServer.createSourceWithCheck(sourceName, SourceType.machine, SourceProduct.udp,
				organizationID, siteID, ProtectionStatus.protect, ConnectionStatus.online, osMajor, applications, test);
		// String sourceID = spogServer.createSourceWithCheck(sourceName, sourceType,
		// sourceProduct, organizationID,
		// siteID, protectionStatus, connectionStatus, osMajor, applications, userID,
		// SpogConstants.SUCCESS_POST,
		// null, true, test);

		// create another source for put/delete
		test.log(LogStatus.INFO, "create another source for put/delete");
		String sourceID_added = spogServer.createSourceWithCheck(sourceName_added, SourceType.machine,
				SourceProduct.udp, organizationID, siteID, ProtectionStatus.protect, ConnectionStatus.online, osMajor,
				applications, test);
		// String sourceID_added = spogServer.createSourceWithCheck(sourceName_added,
		// sourceType_added, sourceProduct,
		// organizationID, siteID, protectionStatus, connectionStatus, osMajor,
		// applications, userID,
		// SpogConstants.SUCCESS_POST, null, true, test);

		// update a source (PUT sources/:id)
		test.log(LogStatus.INFO, "update a source");
		spogServer.updateSourcebysourceId(sourceID_added, sourceName_updated, SourceType.shared_folder,
				SourceProduct.udp, organizationID, siteID, "", ProtectionStatus.protect, ConnectionStatus.online,
				osMajor, userToken, test);

		// get source by ID (GET sources/:id)
		test.log(LogStatus.INFO, "get source by ID");
		Response response = spogServer.getSourceById(userToken, sourceID_added, test);
		response.then().body("data.source_name", equalTo(sourceName_updated));

		// get sources by sourceType (GET sources/types)
		test.log(LogStatus.INFO, "get source by Type");
		response = spogServer.getsourcesbytypes("", userToken, test);
		ArrayList<Map<String, Object>> sourcesByType = response.then().extract().path("data");
		for (Map<String, Object> sourceByType : sourcesByType) {
			if (sourceByType.get("source_type").toString().equalsIgnoreCase(sourceType)
					|| sourceByType.get("source_type").toString().equalsIgnoreCase(sourceType_added)) {
				assertEquals(sourceByType.get("amount"), 1);
			} else {
				assertEquals(sourceByType.get("amount"), 0);
			}
		}

		// delete source by ID (DELETE sources/:id)
		test.log(LogStatus.INFO, "delete source by ID");
		spogServer.deleteSourcesById(userToken, sourceID_added, test);

		// get sources (Get sources)
		test.log(LogStatus.INFO, "get sources");
		response = spogServer.getSources("", null,-1, -1, true, test);
		ArrayList<Map<String, Object>> sources = response.then().extract().path("data");
		assertEquals(sources.size(), 2);
		for (Map<String, Object> source : sources) {
			if (source.get("source_id").toString().equalsIgnoreCase(sourceID_added)) {
				assertEquals(source.get("is_deleted"), true);
			}
		}

		// create source group (POST sources/groups)
		test.log(LogStatus.INFO, "create source group");
		String sourceGroupID = spogServer.createGroupWithCheck(organizationID, sourceGroupName, "", test);

		// create another source group for modify/delete
		test.log(LogStatus.INFO, "create another source group");
		String sourceGroupID_added = spogServer.createGroupWithCheck(organizationID, sourceGroupName_added, "", test);

		// update source group by ID (PUT sources/groups/:id)
		test.log(LogStatus.INFO, "update source group by id");
		spogServer.updateSourceGroup(sourceGroupID_added, sourceGroupName_updated, "");

		// get source group by ID (GET sources/groups/:id)
		test.log(LogStatus.INFO, "get source group by id");
		response = spogServer.getGroupById(userToken, sourceGroupID_added, test);
		response.then().body("data.group_name", equalTo(sourceGroupName_updated));

		// delete source group by ID (DELETE sources/groups/:id)
		test.log(LogStatus.INFO, "delete source group by id");
		spogServer.deleteGroup(sourceGroupID_added, test);

		// get source groups (GET sources/groups)
		test.log(LogStatus.INFO, "get source groups");
		response = spogServer.getSourceGroups("", "", "", "", "");
		ArrayList<HashMap<String, Object>> sourceGroups = response.then().extract().path("data");
		assertEquals(sourceGroups.size(), 1);

		// add a source to a group (POST sources/groups/:id/members)
		test.log(LogStatus.INFO, "add a source  to a group");
		String[] sourceNodes = new String[] { sourceID };
		spogServer.addSourcetoSourceGroupwithCheck(sourceGroupID, sourceNodes, userToken, SpogConstants.SUCCESS_POST,
				null, test);

		// get sources list for a specified group (GET sources/groups/:id/members)
		test.log(LogStatus.INFO, "get sources list for a specified group");
		response = spogServer.getSourceListFromOneGroup(sourceGroupID, -1, -1, test);
		ArrayList<Map<String, Object>> sourceList = response.then().extract().path("data");
		assertEquals(sourceList.size(), 1);
		assertEquals(sourceList.get(0).get("source_id"), sourceID);
		assertEquals(sourceList.get(0).get("source_name"), sourceName);

		// remove a source from a group (DELETE sources/groups/:id/members/:id)
		test.log(LogStatus.INFO, "remove a source from a group");
		spogServer.deleteSourcefromSourceGroupwithCheck(sourceGroupID, sourceID, userToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		// get sources list for a specified group (GET sources/groups/:id/members)
		test.log(LogStatus.INFO, "get sources list for a specified group");
		response = spogServer.getSourceListFromOneGroup(sourceGroupID, -1, -1, test);
		sourceList = response.then().extract().path("data");
		assertEquals(sourceList.size(), 0);

	}

	/**
	 * @author Zhaoguo.Ma
	 */
	//@Test
	public void jobRelatedTest() {
		// login use direct org;
		spogServer.userLogin(this.initial_direct_email_full, password);
		String spogToken = spogServer.getJWTToken();
		String userID = spogServer.GetLoggedinUser_UserID();

		String prefix = RandomStringUtils.randomAlphanumeric(4);
		// define the params for createSite - site will be used for jobs;
		String siteName = prefix + "siteName";
		String siteType = "gateway";
		String organizationID = initial_direct_orgID;

		// define the params for createSite - site will used for operations
		// (update/get/delete);
		String siteName_added = prefix + "siteName_added";
		String siteName_updated = prefix + "siteName_updated";

		// define the params for registerSite;
		String gatewayID = UUID.randomUUID().toString();
		String gatewayHostName = prefix + "hostName";
		String siteVersion = "";

		// define the params for postJobs;
		long startTimeTS = System.currentTimeMillis();
		long endTimeTS = System.currentTimeMillis();
		String jobType = "backup";
		String jobMethod= "incremental"; 
		String jobStatus = "finished";
		
		String serverID = UUID.randomUUID().toString();
		// String resourceID = UUID.randomUUID().toString();
		String rpsID = UUID.randomUUID().toString();
		String destinationID = UUID.randomUUID().toString();
		String policyID = UUID.randomUUID().toString();
		
		// define the params for postJobData;
		String jobSeq = "1000";
		String severity = "information";
		String percentComplete="20";
		String protectedDataSize = "12000";
		String rawDataSize = "13000";
		String syncReadSize = "14000";
		String ntfsVolumeSize = "15000";
		String virtualDiskProvisionSize = "16000";

		// define the params for putJobData;
		String virtualDiskProvisionSize_updated = "17000";

		// test create a site (POST sites/link)
		test.log(LogStatus.INFO, "create a site");
		Response response = spogServer.createSite(siteName, siteType, organizationID, spogToken, test);
		Map<String, String> sitecreateResMap = new HashMap<>();
		sitecreateResMap = spogServer.checkCreateSite(response, SpogConstants.SUCCESS_POST, siteName, siteType,
				organizationID, userID, "", test);
		String registrationBasecode = sitecreateResMap.get("registration_basecode");
		String siteID = sitecreateResMap.get("site_id");

		// define the params to create a resource;
		String sourceName = "sourceName";
		// String sourceType = "machine";
		// String sourceProduct = "udp";
		// String protectionStatus = "protect";
		// String connectionStatus = "online";
		String osMajor = "windows";
		String applications = "SQL_SERVER";
		String resourceID = spogServer.createSourceWithCheck(sourceName, SourceType.machine, SourceProduct.cloud_direct,
				organizationID, siteID, ProtectionStatus.protect, ConnectionStatus.online, osMajor, applications, test);
		// String resourceID1 = spogServer.createSourceWithCheck(sourceName, sourceType,
		// sourceProduct, organizationID,
		// siteID, protectionStatus, connectionStatus, osMajor, applications, userID,
		// SpogConstants.SUCCESS_POST,
		// null, true, test);

		// create another site for update/delete
		test.log(LogStatus.INFO, "create another site");
		response = spogServer.createSite(siteName_added, siteType, organizationID, spogToken, test);
		Map<String, String> sitecreateResMap_added = new HashMap<>();
		sitecreateResMap_added = spogServer.checkCreateSite(response, SpogConstants.SUCCESS_POST, siteName_added,
				siteType, organizationID, userID, "", test);
		String siteID_added = sitecreateResMap_added.get("site_id");

		// test update site by ID
		test.log(LogStatus.INFO, "update site by id");
		response = SiteTestHelper.updateSiteById(siteID_added, siteName_updated, spogToken);
		response.then().body("data.site_name", equalTo(siteName_updated));

		// test get site by id (GET sites/:id)
		test.log(LogStatus.INFO, "get a site by ID");
		response = SiteTestHelper.getSite(siteID_added, spogToken);
		response.then().body("data.site_id", equalTo(siteID_added));
		response.then().body("data.site_name", equalTo(siteName_updated));
		response.then().body("data.site_type", equalTo(siteType));
		response.then().body("data.organization_id", equalTo(organizationID));

		// test delete site by id (DELETE sites/:id)
		test.log(LogStatus.INFO, "delete a site by ID");
		response = SiteTestHelper.deleteSite(siteID_added, spogToken);

		// test get sites (GET sites)
		//test.log(LogStatus.INFO, "get sites");
		//response = spogServer.getSites();
		//ArrayList<Map<String, Object>> sites = new ArrayList<Map<String, Object>>();
		//sites = response.then().extract().path("data");
		//assertEquals(sites.size(), 1);
		//assertEquals(sites.get(0).get("site_id"), siteID);
		//assertEquals(sites.get(0).get("site_name"), siteName);
		//assertEquals(sites.get(0).get("site_type"), siteType);
		//assertEquals(sites.get(0).get("organization_id"), organizationID);

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

		// test get site configuration (GET sites/:id/configuration)
		test.log(LogStatus.INFO, "get site configuration");
		response = gatewayServer.getSiteConfiguration(siteID, token, test);
		gatewayServer.checkSiteConfiguration(response, siteID, ServerResponseCode.Success_Get,
				SpogMessageCode.AUDIT_RESOURCE_NOT_FOUND, test);

		// test post job (POST jobs)
		test.log(LogStatus.INFO, "post job");
		String jobID = gatewayServer.postJobWithCheck(startTimeTS, endTimeTS, organizationID, serverID, resourceID,
				rpsID, destinationID, policyID, jobType, "none", jobStatus, token, test);

		// test post job data (POST jobs/:id/data)
		test.log(LogStatus.INFO, "post job data");
		gatewayServer.postJobDataWithCheck(jobID, "none", severity, "20", 
				"none", "none", "none", "none", "none", "10", "20", "none", "none",			
				"none", "none", "none", "none", "none", "none", token, test);

		// get job by ID (GET jobs/:id)
		test.log(LogStatus.INFO, "get job by id");
		response = spogServer.getJobsById(spogToken, jobID, test);
		response.then().body("data.job_id", equalTo(jobID)).body("data.site_id", equalTo(siteID))
				.body("data.server_id", equalTo(serverID)).body("data.source_id", equalTo(resourceID))
				.body("data.organization_id", equalTo(organizationID));

		// get job data by ID (GET jobs/:id/data)
		test.log(LogStatus.INFO, "get job data by id");
		response = spogServer.getJobDataByID(spogToken, jobID, test);
		response.then().body("data.severity", equalTo(severity));

		// get jobs (GET jobs)
		test.log(LogStatus.INFO, "get jobs");
		response = spogServer.getJobs(spogToken, "", test);
		ArrayList<Map<String, Object>> jobs = response.then().extract().path("data");
		assertEquals(jobs.size(), 1);
		assertEquals(jobs.get(0).get("job_id"), jobID);

		// get jobs by organization id (GET organizations/:id/jobs)
		test.log(LogStatus.INFO, "get jobs by organization ID");
		response = spogServer.getOrganizationJobs(organizationID, null);
		jobs = response.then().extract().path("data");
		assertEquals(jobs.size(), 1);
		assertEquals(jobs.get(0).get("job_id"), jobID);

		// get jobs by source id (GET sources/:id/jobs)
		test.log(LogStatus.INFO, "get jobs by source id");
		response = spogServer.getJobsBySourceID(resourceID, "", "", -1, -1, true, test);
		jobs = response.then().extract().path("data");
		assertEquals(jobs.size(), 1);
		assertEquals(jobs.get(0).get("job_id"), jobID);

		// put job (PUT jobs/:id)
		test.log(LogStatus.INFO, "put job");
		gatewayServer.updateJobWithCheck(jobID, startTimeTS, endTimeTS, organizationID, serverID, resourceID, rpsID, destinationID, policyID, jobType, jobMethod, jobStatus, token, test);
//		gatewayServer.updateJobWithCheck(jobID, System.currentTimeMillis(), serverID, resourceID, rpsID, datastoreID,
//				siteID, organizationID, messageID, policyID, messageData, token, test);

		// put job data (PUT jobs/:id/data)
		test.log(LogStatus.INFO, "put job data");
		//gatewayServer.updateJobDataWithCheck(jobID, jobSeq, severity, percentComplete, protectedDataSize, rawDataSize, syncReadSize, ntfsVolumeSize, virtualDiskProvisionSize_updated, token, test);
//		gatewayServer.updateJobDataWithCheck(jobID, jobID, jobSeq, jobType, jobMethod, jobStatus, endTimeTS,
//				protectedDataSize, rawDataSize, syncReadSize, ntfsVolumeSize, virtualDiskProvisionSize_updated, token,
//				test);

	}

	/**
	 * @author Jing.Shan
	 */
//	@Test
	public void logRelatedTest() {
		// login use direct org;
		spogServer.userLogin(this.initial_direct_email_full, password);
		String spogToken = spogServer.getJWTToken();
		String userID = spogServer.GetLoggedinUser_UserID();

		String prefix = RandomStringUtils.randomAlphanumeric(4);
		// define the params for createSite - site will be used for jobs;
		String siteName = prefix + "siteName";
		String siteType = "gateway";
		String organizationID = initial_direct_orgID;

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
		String resource_name = spogServer.ReturnRandom("ci") + "_";
		String resourceID = spogServer.createSourceWithCheck(resource_name, SourceType.machine, SourceProduct.cloud_direct,
				organizationID, siteID, ProtectionStatus.protect, ConnectionStatus.online, "windows", "SQLSERVER",
				test);
		// String jobID = gatewayServer.postJobWithCheck(startTimeTS, endTimeTS,
		// serverID, resourceID, rpsID, datastoreID, siteID,
		// organizationID, messageID, policyID, messageData, token, test);

		String jobID = gatewayServer.postJobWithCheck(startTimeTS, endTimeTS, organizationID, serverID, resourceID,
				rpsID, destinationID, policyID, jobType, "none", jobStatus, token, test);

		// test post job data (POST jobs/:id/data)
		test.log(LogStatus.INFO, "post job data");
		// gatewayServer.postJobDataWithCheck(jobID, jobID, jobSeq, jobType, jobMethod,
		// jobStatus, endTimeTS,
		// protectedDataSize, rawDataSize, syncReadSize, ntfsVolumeSize,
		// virtualDiskProvisionSize, token, test);
		gatewayServer.postJobDataWithCheck(jobID, "none", "information", "20", 
				"none", "none", "none", "none", "none", "10", "20", "none", "none",			
				"none", "none", "none", "none", "none", "none", token, test);

		response = spogServer.getJobsById(spogToken, jobID, test);
		HashMap<String, Object> job_data = spogServer.composeJobData(response, test);

		// post log
		String message_date = "spog,QA" ;
		String SeverityType = "information";
		String logSourceType = "spog";
		String message_id = "testLogMessage";
		String sourceName = "sourceNameLog";
		String sourceType = "machine";
		String sourceProduct = "udp";
		String protectionStatus = "protect";
		String connectionStatus = "online";
		String osMajor = "windows";
		String applications = "SQL_SERVER";
		String sourceID = spogServer.createSourceWithCheck(sourceName, 	SourceType.machine, SourceProduct.udp,
				organizationID, siteID, ProtectionStatus.protect, ConnectionStatus.online, osMajor, applications, test);
		String log_id = gatewayServer.createLogwithCheck(startTimeTS,jobID, organizationID, organizationID, sourceID,SeverityType,
				logSourceType, message_id, message_date, token, test);
		test.log(LogStatus.INFO, "Create log successfully with log id is:" + log_id);

		// get logs by id
		String message = "";
		spogServer.userLogin(this.initial_direct_email_full, password);
//		response = spogServer.getLogsById(log_id, spogToken, test);
//		spogServer.checkGetLogsById(response, SpogConstants.SUCCESS_GET_PUT_DELETE, startTimeTS, log_id, job_data,
//				SeverityType, logSourceType, siteID, organizationID, message, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

		// get all logs
		response = spogServer.getJobsById(spogToken, jobID, test);
		job_data = spogServer.composeJobData(response, test);

		HashMap<String, Object> Log_data = new HashMap<String, Object>();
		ArrayList<HashMap<String, Object>> allLogs = new ArrayList<HashMap<String, Object>>();
		Log_data = spogServer.ComposeLogs(startTimeTS, log_id, job_data, SeverityType, logSourceType, siteID,
				organizationID, message, test);
		allLogs.add(Log_data);

		int page_number = 1;
		int page_size = 40;
		String filterStr = "organization_id;in;direct";
		String SortStr = "create_ts;asc";

		String[] filterArray = null;
		String filterName = null, filterOperator = null, filterValue = null;
		if (filterStr != "" && filterStr != null) {
			filterArray = filterStr.split(";");
			filterName = filterArray[0];
			filterOperator = filterArray[1];
			filterValue = filterArray[2];
		}

		test.log(LogStatus.INFO, "get filter");
		String newFilterStr = filterStr;
		newFilterStr = filterName + ";" + filterOperator + ";";
		newFilterStr += organizationID;

		String additionalURL = spogServer.PrepareURL(newFilterStr, SortStr, page_number, page_size, test);
		response = spogServer.getLogs(spogToken, additionalURL, test);
		// need open
		//spogServer.checkGetLogs(response, SpogConstants.SUCCESS_GET_PUT_DELETE, allLogs, page_number, page_size,
		//		newFilterStr, SortStr, SpogMessageCode.SUCCESS_GET_PUT_DEL, "", test);

		// get log by organization id
		additionalURL = spogServer.PrepareURL("", "create_ts;desc", page_number, page_size, test);
		// need open
		//response = spogServer.getLogsByOrganizationId(spogToken, organizationID, additionalURL, test);
		//spogServer.checkGetLogs(response, SpogConstants.SUCCESS_GET_PUT_DELETE, allLogs, page_number, page_size,
		//		filterStr, SortStr, SpogMessageCode.SUCCESS_GET_PUT_DEL, "", test);

		// get job log by job id
		ArrayList<HashMap<String, Object>> expected_response = new ArrayList<>();
		HashMap<String, Object> temp = new HashMap<>();
		temp = spogServer.composejob_log(log_id, SeverityType, logSourceType, siteID, organizationID, messageData, jobID,
				"Backup - Incremental", resourceID, resource_name, rpsID, "");
		expected_response.add(temp);

		// get jobs/:id/logs
		test.log(LogStatus.INFO, "Get job logs by job id");
		spogServer.getjoblogbyjobIdwithcheck(spogToken, jobID, expected_response, SpogConstants.SUCCESS_GET_PUT_DELETE,
				SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		// create another job for update log
		long start_time_ts = System.currentTimeMillis();
		String server_id = UUID.randomUUID().toString();
		String rps_id = UUID.randomUUID().toString();
		String datastore_id = UUID.randomUUID().toString();
		String policy_id = UUID.randomUUID().toString();
		String job_message_id = "testJobMessage";
		String[] job_message_Data = new String[] { "node", "agent" };
		String[] log_message_data = new String[] { "spog", "QA" };
		String log_severity_type = "warning";
		String log_source_type = "cloud_direct";
		String update_log_message_id = "connect_node_failed_test_message";
		String[] update_log_message_data = new String[] { "node", "10.57.63.2", "Agent", "network" };

		String job_id = gatewayServer.postJobWithCheck(startTimeTS, endTimeTS, organizationID, serverID, resourceID,
				rpsID, destinationID, policyID, jobType, "none", jobStatus, token, test);
		test.log(LogStatus.INFO, "create job");

		// update log
		log4GatewayServer.updateLogWithCheck(startTimeTS,log_id, job_id, organizationID, organizationID, resourceID,log_severity_type,
				log_source_type, update_log_message_id, update_log_message_data, true, token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		test.log(LogStatus.INFO, "update log");

		// delete logs/:id
		test.log(LogStatus.INFO, "Delete the logs by log Id");
		spogServer.deletelogbylogId(spogToken, log_id, SpogConstants.SUCCESS_GET_PUT_DELETE,
				SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

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
		rep.endTest(test);
	}

	@AfterTest
	public void aftertest() {
		test.log(LogStatus.INFO, "The total test cases passed are " + count1.getpassedcount());
		test.log(LogStatus.INFO, "the total test cases failed are " + count1.getfailedcount());
		rep.flush();
	}
}