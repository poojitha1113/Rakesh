package api.organizations;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;

public class GetAllAccountsForSpecifiedMsp extends base.prepare.Is4Org {

	private SPOGServer spogServer;
	private GatewayServer gatewayServer;

	private ExtentTest test;
	private String common_password = "Mclaren@2020";
	private String csrAdminUserName;
	private String csrAdminPassword;

	private String csrReadOnlyUserName;
	private String csrReadOnlyPassword;
	private String csr_read_only_validToken;
	private String csr_read_only_user_id;

	private String postfix_email = "@arcserve.com";
	private String prefix_direct = "spogqa_Ramya_direct";
	private String direct_org_name = prefix_direct + "_org";
	private String direct_org_email = direct_org_name + postfix_email;
	private String direct_org_first_name = direct_org_name + "_first_name";
	private String direct_org_last_name = direct_org_name + "_last_name";
	private String direct_user_name = prefix_direct + "_admin";
	private String direct_user_name_email = prefix_direct + "_admin" + postfix_email;
	private String direct_user_first_name = direct_user_name + "_first_name";
	private String direct_user_last_name = direct_user_name + "_last_name";

	private String organizationName1 = null;
	private String organizationEmail1 = null;

	private String organizationFirstName1 = null;
	private String organizationLastName1 = null;
	/*
	 * private SQLServerDb bqdb1; public int Nooftest; int passedcases=0; int
	 * failedcases=0; int skippedcases=0; long creationTime; String
	 * buildnumber=null; String BQName=null; private testcasescount count1;
	 */

	int passedcases = 0;
	int failedcases = 0;
	int skippedcases = 0;

	int noofAccounts = 2;
	int noofaccounts = 15;
	private String filterStr = "";
	private String sortStr = "";
	int curr_page = 1;
	int page_size = 20;
	// private String runningMachine;
	private String msp_orgID;
	private String additionalURL;
	// private String buildVersion;

	private UserSpogServer userSpogServer;

	private String prefix_msp = "SPOG_QA_RAMYA_BQ_msp";
	private String msp_org_name = prefix_msp + "_org";
	private String msp_org_email = msp_org_name + postfix_email;
	private String msp_org_first_name = msp_org_name + "_first_name";
	private String msp_org_last_name = msp_org_name + "_last_name";
	private String msp_user_name = prefix_msp + "_admin";
	private String msp_user_name_email = prefix_msp + "_admin" + postfix_email;
	private String msp_user_first_name = msp_user_name + "_first_name";
	private String msp_user_last_name = msp_user_name + "_last_name";
	private String msp_user_validToken;
	private String msp_site_token;

	private String prefix_msp_b = "SPOG_QA_RAMYA_BQ_msp_b";
	private String msp_org_name_b = prefix_msp_b + "_org";
	private String msp_org_email_b = msp_org_name_b + postfix_email;
	private String msp_org_first_name_b = msp_org_name_b + "_first_name";
	private String msp_org_last_name_b = msp_org_name_b + "_last_name";
	private String mspb_user_validToken;

	private String initial_sub_org_name_a = "SPOG_QA_RAMYA_BQ_sub_a";
	private String initial_sub_email_a = "spog_qa_sub_Ramya_a@arcserve.com";
	private String initial_sub_first_name_a = "spog_qa_sub_Ramya_a";
	private String initial_sub_last_name_a = "spog_qa_sub_Nagepalli_a";
	private String sub_orga_user_validToken;
	private String suborga_site_token;

	private String initial_sub_org_name_b = "SPOG_QA_RAMYA_BQ_sub_b";
	private String initial_sub_email_b = "spog_qa_sub_Ramya_b@arcserve.com";
	private String initial_sub_first_name_b = "spog_qa_sub_Ramya_b";
	private String initial_sub_last_name_b = "spog_qa_sub_Nagepalli_b";
	private String sub_orgb_user_validToken;

	private String prefix_msp_account_admin = "SPOG_QA_RAMYA_BQ_msp_account";
	private String msp_account_admin_email = prefix_msp_account_admin + postfix_email;
	private String msp_account_admin_first_name = prefix_msp_account_admin + "_first_name";
	private String msp_account_admin_last_name = prefix_msp_account_admin + "_last_name";
	private String msp_account_admin_validToken;
	String msp_admin_name;
	String msp_account_admin_user_id;
	String msp_account_admin_id;

	String direct_organization_id;
	String msp_organization_id;
	String msp_organization_id_b;
	String sub_org_Id;
	String sub_org_Id_1;

	String csr_user_id;
	String direct_user_id;
	String msp_user_id;
	String mspb_user_id;
	String suborga_user_id;
	String suborgb_user_id;

	String direct_site_id;
	String msp_site_id;
	String mspb_site_id;
	String suborga_site_id;
	String suborgb_site_id;
	String csr_token;

	private String prefix_msp_account_admin_1 = "SPOG_QA_RAMYA_BQ_msp_account_1";
	private String msp_account_admin_email_1 = prefix_msp_account_admin_1 + postfix_email;
	private String msp_account_admin_first_name_1 = prefix_msp_account_admin_1 + "_first_name";
	private String msp_account_admin_last_name_1 = prefix_msp_account_admin_1 + "_last_name";
	private String msp_account_admin_validToken_1;
	String msp_admin_name_1;
	String msp_account_admin_user_id_1;
	String msp_account_admin_id_1;

	private String org_model_prefix = this.getClass().getSimpleName();

	@BeforeClass
	@Parameters({ "baseURI", "port", "csrAdminUserName", "csrAdminPassword", "csrReadOnlyUserName",
			"csrReadOnlyPassword", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword,
			String readOnlyUserName, String readOnlyPassword, String logFolder, String runningMachine,
			String buildVersion) {

		this.csrReadOnlyUserName = readOnlyUserName;
		this.csrReadOnlyPassword = readOnlyPassword;

		spogServer = new SPOGServer(baseURI, port);

		Nooftest = 0;
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Nagepalli,Ramya";
		rep = ExtentManager.getInstance(this.getClass().getSimpleName(), logFolder);
		test = rep.startTest("Setup");
		this.csrAdminUserName = adminUserName;
		this.csrAdminPassword = adminPassword;
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date = new java.util.Date();
		this.buildVersion = buildVersion + "_" + dateFormater.format(date);
		this.BQName = this.getClass().getSimpleName();
		this.runningMachine = runningMachine;
		System.out.println("The value of hit is " + count1.isstarttimehit());
		if (count1.isstarttimehit() == 0) {
			System.out.println("into creation time");
			creationTime = System.currentTimeMillis();
			count1.setcreationtime(creationTime);
			// creationTime = System.currentTimeMillis();
			try {
				bqdb1.updateTable(BQName, this.runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0",
						String.valueOf(Nooftest), count1.getcreationtime(), "InProgress",
						author + " and Rest server is " + baseURI.split("//")[1]);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		userSpogServer = new UserSpogServer(baseURI, port);

		// login in as csr admin

		test.log(LogStatus.INFO, "Logging with csrAdmin");
		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
		csr_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Get the logged in user id ");
		csr_user_id = spogServer.GetLoggedinUser_UserID();
		test.log(LogStatus.INFO, "The csr user id is " + csr_user_id);

		String prefix = RandomStringUtils.randomAlphanumeric(8);

		// Create a msp org
		test.log(LogStatus.INFO, "create a msp org");
		this.msp_org_email = prefix + this.msp_org_email;
		msp_organization_id = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name + org_model_prefix,
				SpogConstants.MSP_ORG, this.msp_org_email, common_password, prefix + msp_org_first_name,
				prefix + msp_org_last_name, test);

		// Create a suborg under MSP org and a user for sub org
		spogServer.userLogin(this.msp_org_email, common_password);
		test.log(LogStatus.INFO, "Getting the JWTToken for the Logged in user");
		msp_user_validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "The token is :" + msp_user_validToken);

		test.log(LogStatus.INFO, "Get the logged in user id ");
		msp_user_id = spogServer.GetLoggedinUser_UserID();
		test.log(LogStatus.INFO, "The msp user id is " + msp_user_id);

		/*
		 * test.log(LogStatus.INFO, "Create a site/register/login to the site");
		 * msp_site_id =
		 * gatewayServer.createsite_register_login(msp_organization_id,
		 * msp_user_validToken, msp_user_id, "ts", "1.0.0", spogServer, test);
		 * msp_site_token = gatewayServer.getJWTToken();
		 */
		test.log(LogStatus.INFO, "Create a suborg A under msp org");
		sub_org_Id = spogServer.createAccountWithCheck(msp_organization_id, prefix + initial_sub_org_name_a,
				msp_organization_id);
		test.log(LogStatus.INFO, "Create a suborg B under msp org");
		sub_org_Id_1 = spogServer.createAccountWithCheck(msp_organization_id, prefix + initial_sub_org_name_b,
				msp_organization_id);

		test.log(LogStatus.INFO, "Create a direct user for sub org A");
		this.initial_sub_email_a = prefix + initial_sub_email_a;
		suborga_user_id = spogServer.createUserAndCheck(this.initial_sub_email_a, common_password,
				prefix + this.initial_sub_first_name_a, prefix + this.initial_sub_last_name_a,
				SpogConstants.DIRECT_ADMIN, sub_org_Id, test);

		test.log(LogStatus.INFO, "Create a direct user for sub org B");
		this.initial_sub_email_b = prefix + initial_sub_email_b;
		suborgb_user_id = spogServer.createUserAndCheck(this.initial_sub_email_b, common_password,
				prefix + this.initial_sub_first_name_b, prefix + this.initial_sub_last_name_b,
				SpogConstants.DIRECT_ADMIN, sub_org_Id_1, test);

		test.log(LogStatus.INFO, "Login in to sub org A");
		spogServer.userLogin(this.initial_sub_email_a, common_password);
		test.log(LogStatus.INFO, "Getting the JWTToken for the Logged in user");
		sub_orga_user_validToken = spogServer.getJWTToken();

		/*
		 * test.log(LogStatus.INFO, "Create a site/register/login to the site");
		 * suborga_site_id = gatewayServer.createsite_register_login(sub_org_Id,
		 * sub_orga_user_validToken, suborga_user_id, "ts", "1.0.0", spogServer,
		 * test); suborga_site_token = gatewayServer.getJWTToken();
		 */
		test.log(LogStatus.INFO, "Login in to sub org B");
		spogServer.userLogin(this.initial_sub_email_b, common_password);
		test.log(LogStatus.INFO, "Getting the JWTToken for the Logged in user");
		sub_orgb_user_validToken = spogServer.getJWTToken();

		/*
		 * test.log(LogStatus.INFO, "Create a site/register/login to the site");
		 * suborgb_site_id =
		 * gatewayServer.createsite_register_login(sub_org_Id_1,
		 * sub_orgb_user_validToken, suborgb_user_id, "ts", "1.0.0", spogServer,
		 * test);
		 */

		// Create msp_account_admin
		test.log(LogStatus.INFO, "Create a msp account admin under msp org");

		prefix = RandomStringUtils.randomAlphanumeric(8);

		msp_account_admin_email = prefix + msp_account_admin_email;
		spogServer.setToken(msp_user_validToken);

		msp_account_admin_id = spogServer.createUserAndCheck(msp_account_admin_email, common_password,
				msp_account_admin_first_name, msp_account_admin_last_name, "msp_account_admin", msp_organization_id,
				test);
		spogServer.userLogin(msp_account_admin_email, common_password);

		this.msp_account_admin_validToken = spogServer.getJWTToken();

		msp_account_admin_user_id = spogServer.GetLoggedinUser_UserID();

		test.log(LogStatus.INFO, "The token is :" + msp_account_admin_validToken);

		test.log(LogStatus.INFO,
				"Assign the msp account admin to specified sub organization using msp user valid token ");
		userSpogServer.assignMspAccountAdmins(msp_organization_id, sub_org_Id, new String[] { msp_account_admin_id },
				msp_user_validToken);

		// create another msp admin account

		test.log(LogStatus.INFO, "Create a msp account admin under msp org");

		prefix = RandomStringUtils.randomAlphanumeric(8);

		msp_account_admin_email_1 = prefix + msp_account_admin_email_1;
		spogServer.setToken(msp_user_validToken);

		msp_account_admin_id_1 = spogServer.createUserAndCheck(msp_account_admin_email_1, common_password,
				msp_account_admin_first_name_1, msp_account_admin_last_name_1, "msp_account_admin", msp_organization_id,
				test);
		spogServer.userLogin(msp_account_admin_email_1, common_password);

		this.msp_account_admin_validToken_1 = spogServer.getJWTToken();

		msp_account_admin_user_id_1 = spogServer.GetLoggedinUser_UserID();

		test.log(LogStatus.INFO, "The token is :" + msp_account_admin_validToken_1);

		// assign another msp admin to another sub org

		test.log(LogStatus.INFO,
				"Assign the another msp account admin to another specified sub organization using msp user valid token ");
		userSpogServer.assignMspAccountAdmins(msp_organization_id, sub_org_Id_1,
				new String[] { msp_account_admin_id_1 }, msp_user_validToken);

		msp_admin_name = msp_account_admin_first_name + " " + msp_account_admin_last_name;
		msp_admin_name_1 = msp_account_admin_first_name_1 + " " + msp_account_admin_last_name_1;

		// login with csr read only credentials
		spogServer.userLogin(csrReadOnlyUserName, csrReadOnlyPassword);
		csr_read_only_validToken = spogServer.getJWTToken();
		csr_read_only_user_id = spogServer.GetLoggedinUser_UserID();

	}

	// Data provider to create multiple accounts under the msp_child
	// organization
	// TODO Auto-generated method stub

	@DataProvider(name = "organizationAndUserInfo")
	public final Object[][] getOrganizationAndUserInfo() {
		return new Object[][] { { msp_org_name, SpogConstants.MSP_ORG, msp_org_email, common_password,
				msp_org_first_name, msp_org_last_name } };
	}

	@Test(dataProvider = "organizationAndUserInfo")
	public void getAllAccounts_msp(String organizationName, String organizationType, String organizationEmail,
			String organizationPwd, String organizationFirstName, String organizationLastName) {

		test = rep.startTest("getAllAccounts_msp");
		test.assignAuthor("Ramya Nagepalli");
		// Generating different input parameters
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + msp_org_name;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;
		test.log(LogStatus.INFO, "Login with csr admin user");
		// Logging with the csr_admin To generate Token
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		test.log(LogStatus.INFO, "Getting the token of the CSR admin ");
		String csr_token = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "The Token is " + csr_token);
		// creating organization with msp_admin user
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		String mspOrgId = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "Login with msp user");
		// logging with the msp_admin user to get the JWT token
		spogServer.userLogin(organizationEmail, organizationPwd, test);
		test.log(LogStatus.INFO, "Getting the token of the msp admin ");
		String msp_token = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "The Token is " + msp_token);
		spogServer.setToken(msp_token);
		String user_id = spogServer.GetLoggedinUser_UserID();
		String org_id = null;
		String user_name = spogServer.GetLoggedinUser_UserName();
		// creating array of msp_sub organizations

		ArrayList<ResponseBody> Accounts = new ArrayList<ResponseBody>();
		boolean flag = true;
		int i = 1;
		while (flag) {
			test.log(LogStatus.INFO, "Creating an Sub Organization for the Msp Organization");
			String organization_name = spogServer.ReturnRandom("spogqa_account");
			Response response = spogServer.createAccountWithCheck2(mspOrgId, organization_name, mspOrgId, test);
			org_id = response.then().extract().path("data.organization_id");
			Accounts.add(response.getBody());

			if (1 == i) {
				flag = false;
			}
			i++;
		}

		HashMap<String, Object> source_status = spogServer.compose_source_status("0", "0", "0", "0", "0");

		HashMap<String, Object> backup_status = spogServer.compose_backup_status("0", "0", "0", "0");

		HashMap<String, Object> cloud_direct_usage = spogServer.compose_cloud_direct_usage("0", "0");

		HashMap<String, Object> cloud_hybrid_usage = spogServer.compose_cloud_hybrid_usage("0", "0");

		HashMap<String, Object> added_by = spogServer.compose_added_by(user_id, user_name, organizationEmail);

		ArrayList<String> allowed_actions = new ArrayList<String>();

		allowed_actions.add("setthreshold");
		allowed_actions.add("assignadmin");
		allowed_actions.add("viewaccount");
		allowed_actions.add("delete");

		HashMap<String, Object> extra_Inputs = spogServer.compose_extra_Inputs("", "success", source_status,
				backup_status, cloud_direct_usage, cloud_hybrid_usage, added_by, allowed_actions, false);

		// Preparing the additionalUrl
		String additionalURL = spogServer.PrepareURL(filterStr, sortStr, curr_page, page_size, test);
		Response response = spogServer.getAllaccountsForSpecifiedMsp(msp_token, mspOrgId, additionalURL, test);
		// System.out.println("The value of the generated response is
		// :"+response.getBody().asString());
		test.log(LogStatus.INFO, "validing the response generated for get all accounts for specifed msp");
		// spogServer.CheckallAccountsForSpecifiedMsp(response,SpogConstants.SUCCESS_GET_PUT_DELETE,Accounts,curr_page,page_size,test);
		spogServer.CheckallAccountsForSpecifiedMsp(response, SpogConstants.SUCCESS_GET_PUT_DELETE, Accounts,
				extra_Inputs, curr_page, page_size, filterStr, sortStr, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

		// get all accounts of msp with csr readonly valid token
		response = spogServer.getAllaccountsForSpecifiedMsp(csr_read_only_validToken, mspOrgId, additionalURL, test);
		test.log(LogStatus.INFO, "validing the response generated for get all accounts for specifed msp");
		spogServer.CheckallAccountsForSpecifiedMsp(response, SpogConstants.SUCCESS_GET_PUT_DELETE, Accounts,
				extra_Inputs, curr_page, page_size, filterStr, sortStr, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

		// check for blocked status by using csr token
		spogServer.setToken(csr_token);
		spogServer.updateAccount_blocked(mspOrgId, org_id, "sub_org_1", "true");

		additionalURL = spogServer.PrepareURL("organization_name;=;sub_org_1", sortStr, curr_page, page_size, test);
		response = spogServer.getAllaccountsForSpecifiedMsp(msp_token, mspOrgId, additionalURL, test);
		// System.out.println("The value of the generated response is
		// :"+response.getBody().asString());
		test.log(LogStatus.INFO, "validing the response generated for get all accounts for specifed msp");
		extra_Inputs = spogServer.compose_extra_Inputs("", "success", source_status, backup_status, cloud_direct_usage,
				cloud_hybrid_usage, added_by, allowed_actions, true);
		// spogServer.CheckallAccountsForSpecifiedMsp(response,SpogConstants.SUCCESS_GET_PUT_DELETE,Accounts,curr_page,page_size,test);
		spogServer.CheckallAccountsForSpecifiedMsp(response, SpogConstants.SUCCESS_GET_PUT_DELETE, Accounts,
				extra_Inputs, curr_page, page_size, filterStr, sortStr, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

	}

	@DataProvider(name = "sortByParams")
	public final Object[][] sortParams() {
		return new Object[][] { { "", "create_ts;asc", 1, 4 }, { "", "create_ts;asc", 1, 20 },
				{ "", "create_ts;asc", 1, 1000000001 }, { "", "create_ts;desc", 2, 1 }, { "", "create_ts;desc", 2, 1 },
				{ "", "create_ts;desc", 1, 20 }, { "", "create_ts;asc", 2, 1 }, { "", "create_ts;asc", 4, 4 } };
	}

	@Test(dataProvider = "sortByParams")
	public void getAllAccounts_msp_usingFilerting_sorting(String filterStr, String sortStr, int curr_page,
			int page_size) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Ramya Nagepalli");
		String organizationPwd = common_password;
		// Generating different input parameters
		organizationName1 = RandomStringUtils.randomAlphanumeric(8) + "_" + msp_org_name;
		organizationEmail1 = RandomStringUtils.randomAlphanumeric(8) + "_" + msp_org_name + postfix_email;
		organizationFirstName1 = RandomStringUtils.randomAlphanumeric(8) + "_" + msp_user_first_name;
		organizationLastName1 = RandomStringUtils.randomAlphanumeric(8) + "_" + msp_user_last_name;
		test.log(LogStatus.INFO, "Login with csr admin user");
		// Logging with the csr_admin To generate Token
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		test.log(LogStatus.INFO, "Getting the token of the CSR admin ");
		String csr_token = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "The Token is " + csr_token);
		// creating organization with msp_admin user
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		String mspOrgId = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name + org_model_prefix,
				SpogConstants.MSP_ORG, organizationEmail1, organizationPwd, organizationFirstName1,
				organizationLastName1, test);
		test.log(LogStatus.INFO, "Login with msp user");
		// logging with the msp_admin user to get the JWT token
		spogServer.userLogin(organizationEmail1, organizationPwd, test);
		test.log(LogStatus.INFO, "Getting the token of the msp admin ");
		String msp_token = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "The Token is " + msp_token);
		spogServer.setToken(msp_token);
		msp_token = spogServer.getJWTToken();
		String user_id = spogServer.GetLoggedinUser_UserID();

		String user_name = spogServer.GetLoggedinUser_UserName();
		// creating array of msp_sub organizations

		ArrayList<ResponseBody> Accounts = new ArrayList<ResponseBody>();
		boolean flag = true;
		int i = 1;
		while (flag) {
			test.log(LogStatus.INFO, "Creating an Sub Organization for the Msp Organization");
			String organization_name = spogServer.ReturnRandom("spogqa_account");
			Response response = spogServer.createAccountWithCheck2(mspOrgId, organization_name, mspOrgId, test);

			Accounts.add(response.getBody());

			if (noofAccounts == i) {
				flag = false;
			}
			i++;
		}

		HashMap<String, Object> source_status = spogServer.compose_source_status("0", "0", "0", "0", "0");

		HashMap<String, Object> backup_status = spogServer.compose_backup_status("0", "0", "0", "0");

		HashMap<String, Object> cloud_direct_usage = spogServer.compose_cloud_direct_usage("0", "0");

		HashMap<String, Object> cloud_hybrid_usage = spogServer.compose_cloud_hybrid_usage("0", "0");

		HashMap<String, Object> added_by = spogServer.compose_added_by(user_id, user_name, organizationEmail1);

		ArrayList<String> allowed_actions = new ArrayList<String>();

		allowed_actions.add("setthreshold");
		allowed_actions.add("assignadmin");
		allowed_actions.add("viewaccount");
		allowed_actions.add("delete");

		HashMap<String, Object> extra_Inputs = spogServer.compose_extra_Inputs("", "success", source_status,
				backup_status, cloud_direct_usage, cloud_hybrid_usage, added_by, allowed_actions, false);

		test.log(LogStatus.INFO, "get all the Accounts for the Specified Msp");
		String additionalURL = spogServer.PrepareURL(filterStr, sortStr, curr_page, page_size, test);
		// additionalURL="sort=create_ts&page=2&page_size=3";
		Response response = spogServer.getAllaccountsForSpecifiedMsp(msp_token, mspOrgId, additionalURL, test);

		ArrayList<String> msp_account_org_id = response.then().extract().path("data.organization_id");

		if (!(msp_account_org_id.isEmpty())) {

			test.log(LogStatus.INFO, "validating the response generated for get all accounts for specified MSP");

			spogServer.CheckallAccountsForSpecifiedMsp(response, SpogConstants.SUCCESS_GET_PUT_DELETE, Accounts,
					extra_Inputs, curr_page, page_size, filterStr, sortStr, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

		}

	}

	// Get allAccounts msp_admin user for invalid JWT Token
	@Test(dataProvider = "organizationAndUserInfo")
	public void getAllAccounts_msp_InvalidJwtToken(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName,
			String organizationLastName) {
		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Ramya Nagepalli");
		// Generating different input parameters
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + msp_org_name;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;

		// Logging with the csr_admin To generate Token
		test.log(LogStatus.INFO, "Login with csr admin user");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		test.log(LogStatus.INFO, "Getting the token of the CSR admin ");
		String csr_token = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "The Token is " + csr_token);

		String prefix = RandomStringUtils.randomAlphanumeric(8);
		// creating organization with msp_admin user
		String mspOrgId = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "Login with msp user");

		// logging with the msp_admin user to get the JWT token
		spogServer.userLogin(organizationEmail, organizationPwd, test);
		test.log(LogStatus.INFO, "Getting the token of the msp admin ");
		String msp_token = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "The Token is " + msp_token);
		spogServer.setToken(msp_token);
		String user_id = spogServer.GetLoggedinUser_UserID();

		String user_name = spogServer.GetLoggedinUser_UserName();
		// creating array of msp_sub organizations

		ArrayList<ResponseBody> Accounts = new ArrayList<ResponseBody>();
		boolean flag = true;
		int i = 1;
		while (flag) {
			test.log(LogStatus.INFO, "Creating an Sub Organization for the Msp Organization");
			String organization_name = spogServer.ReturnRandom("spogqa_account");
			Response response = spogServer.createAccountWithCheck2(mspOrgId, organization_name, mspOrgId, test);

			Accounts.add(response.getBody());

			if (noofAccounts == i) {
				flag = false;
			}
			i++;
		}

		HashMap<String, Object> source_status = spogServer.compose_source_status("0", "0", "0", "0", "0");

		HashMap<String, Object> backup_status = spogServer.compose_backup_status("0", "0", "0", "0");

		HashMap<String, Object> cloud_direct_usage = spogServer.compose_cloud_direct_usage("0", "0");

		HashMap<String, Object> cloud_hybrid_usage = spogServer.compose_cloud_hybrid_usage("0", "0");

		HashMap<String, Object> added_by = spogServer.compose_added_by(user_id, user_name, organizationEmail);

		ArrayList<String> allowed_actions = new ArrayList<String>();

		allowed_actions.add("setthreshold");
		allowed_actions.add("assignadmin");
		allowed_actions.add("viewaccount");
		allowed_actions.add("delete");

		HashMap<String, Object> extra_Inputs = spogServer.compose_extra_Inputs("", "success", source_status,
				backup_status, cloud_direct_usage, cloud_hybrid_usage, added_by, allowed_actions, false);

		String invalidToken = msp_token + "Junk";
		test.log(LogStatus.INFO, "The invalid JWT token:" + invalidToken);
		// Preparing the additionalUrl
		String additionalURL = spogServer.PrepareURL(filterStr, sortStr, curr_page, page_size, test);
		test.log(LogStatus.INFO, "The response should fail when provided a invalid JWT token");
		Response response = spogServer.getAllaccountsForSpecifiedMsp(invalidToken, mspOrgId, additionalURL, test);
		test.log(LogStatus.INFO, "The value of the generated response is:" + response.getBody().asString());
		// validating the response for getAllAccountsForSpecified
		test.log(LogStatus.INFO, "validing the response generated for get all accounts for specifed msp");
		spogServer.checkResponseStatus(response, SpogConstants.NOT_LOGGED_IN, test);
		spogServer.CheckallAccountsForSpecifiedMsp(response, SpogConstants.NOT_LOGGED_IN, Accounts, extra_Inputs,
				curr_page, page_size, filterStr, sortStr, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);

		// Deleting the msp organization
		test.log(LogStatus.INFO, "Login in as csr admin to delete the organization");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		spogServer.DeleteOrganizationWithCheck(mspOrgId, test);

	}

	// Get allAccountsMsp for missing JWT Token
	@Test(dataProvider = "organizationAndUserInfo")
	public void getAllAccounts_msp_MissingJwtToken(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName,
			String organizationLastName) {
		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Ramya Nagepalli");
		// Generating different input parameters
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + msp_org_name;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;

		// Logging with the csr_admin To generate Token
		test.log(LogStatus.INFO, "Login with csr admin user");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		test.log(LogStatus.INFO, "Getting the token of the CSR admin ");
		String csr_token = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "The Token is " + csr_token);

		String prefix = RandomStringUtils.randomAlphanumeric(8);
		// creating organization with msp_admin user
		String mspOrgId = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "Login with msp user");

		// logging with the msp_admin user to get the JWT token
		spogServer.userLogin(organizationEmail, organizationPwd, test);
		test.log(LogStatus.INFO, "Getting the token of the msp admin ");
		String msp_token = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "The Token is " + msp_token);
		spogServer.setToken(msp_token);
		String user_id = spogServer.GetLoggedinUser_UserID();

		String user_name = spogServer.GetLoggedinUser_UserName();
		// creating array of msp_sub organizations

		ArrayList<ResponseBody> Accounts = new ArrayList<ResponseBody>();
		boolean flag = true;
		int i = 1;
		while (flag) {
			test.log(LogStatus.INFO, "Creating an Sub Organization for the Msp Organization");
			String organization_name = spogServer.ReturnRandom("spogqa_account");
			Response response = spogServer.createAccountWithCheck2(mspOrgId, organization_name, mspOrgId, test);

			Accounts.add(response.getBody());

			if (noofAccounts == i) {
				flag = false;
			}
			i++;
		}

		HashMap<String, Object> source_status = spogServer.compose_source_status("0", "0", "0", "0", "0");

		HashMap<String, Object> backup_status = spogServer.compose_backup_status("0", "0", "0", "0");

		HashMap<String, Object> cloud_direct_usage = spogServer.compose_cloud_direct_usage("0", "0");

		HashMap<String, Object> cloud_hybrid_usage = spogServer.compose_cloud_hybrid_usage("0", "0");

		HashMap<String, Object> added_by = spogServer.compose_added_by(user_id, user_name, organizationEmail);

		ArrayList<String> allowed_actions = new ArrayList<String>();

		allowed_actions.add("setthreshold");
		allowed_actions.add("assignadmin");
		allowed_actions.add("viewaccount");
		allowed_actions.add("delete");

		HashMap<String, Object> extra_Inputs = spogServer.compose_extra_Inputs("", "success", source_status,
				backup_status, cloud_direct_usage, cloud_hybrid_usage, added_by, allowed_actions, false);

		// Preparing the additionalUrl
		String additionalURL = spogServer.PrepareURL(filterStr, sortStr, curr_page, page_size, test);
		test.log(LogStatus.INFO, "The response should fail when provided a missing JWT token");
		Response response = spogServer.getAllaccountsForSpecifiedMsp("", mspOrgId, additionalURL, test);
		test.log(LogStatus.INFO, "The value of the generated response is:" + response.getBody().asString());
		// validating the response for getAllAccountsForSpecified
		test.log(LogStatus.INFO, "validing the response generated for get all accounts for specifed msp");
		spogServer.checkResponseStatus(response, SpogConstants.NOT_LOGGED_IN, test);
		spogServer.CheckallAccountsForSpecifiedMsp(response, SpogConstants.NOT_LOGGED_IN, Accounts, extra_Inputs,
				curr_page, page_size, filterStr, sortStr, SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);

		// Deleting the msp organization
		test.log(LogStatus.INFO, "Login in as csr admin to delete the organization");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		spogServer.DeleteOrganizationWithCheck(mspOrgId, test);
	}

	// Get all Accounts For specifiedMsp when provided invalid orgId

	@Test(dataProvider = "organizationAndUserInfo")
	public void getAllAccounts_msp_InvalidId(String organizationName, String organizationType, String organizationEmail,
			String organizationPwd, String organizationFirstName, String organizationLastName) {
		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Ramya Nagepalli");
		// Generating different input parameters
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + msp_org_name;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;

		// Logging with the csr_admin To generate Token
		test.log(LogStatus.INFO, "Login with csr admin user");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		test.log(LogStatus.INFO, "Getting the token of the CSR admin ");
		String csr_token = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "The Token is " + csr_token);

		String prefix = RandomStringUtils.randomAlphanumeric(8);
		// creating organization with msp_admin user
		String mspOrgId = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "Login with msp user");

		// logging with the msp_admin user to get the JWT token
		spogServer.userLogin(organizationEmail, organizationPwd, test);
		test.log(LogStatus.INFO, "Getting the token of the msp admin ");
		String msp_token = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "The Token is " + msp_token);
		spogServer.setToken(msp_token);
		String user_id = spogServer.GetLoggedinUser_UserID();

		String user_name = spogServer.GetLoggedinUser_UserName();
		// creating array of msp_sub organizations

		ArrayList<ResponseBody> Accounts = new ArrayList<ResponseBody>();
		boolean flag = true;
		int i = 1;
		while (flag) {
			test.log(LogStatus.INFO, "Creating an Sub Organization for the Msp Organization");
			String organization_name = spogServer.ReturnRandom("spogqa_account");
			Response response = spogServer.createAccountWithCheck2(mspOrgId, organization_name, mspOrgId, test);

			Accounts.add(response.getBody());

			if (noofAccounts == i) {
				flag = false;
			}
			i++;
		}

		HashMap<String, Object> source_status = spogServer.compose_source_status("0", "0", "0", "0", "0");

		HashMap<String, Object> backup_status = spogServer.compose_backup_status("0", "0", "0", "0");

		HashMap<String, Object> cloud_direct_usage = spogServer.compose_cloud_direct_usage("0", "0");

		HashMap<String, Object> cloud_hybrid_usage = spogServer.compose_cloud_hybrid_usage("0", "0");

		HashMap<String, Object> added_by = spogServer.compose_added_by(user_id, user_name, organizationEmail);

		ArrayList<String> allowed_actions = new ArrayList<String>();

		allowed_actions.add("setthreshold");
		allowed_actions.add("assignadmin");
		allowed_actions.add("viewaccount");
		allowed_actions.add("delete");

		HashMap<String, Object> extra_Inputs = spogServer.compose_extra_Inputs("", "success", source_status,
				backup_status, cloud_direct_usage, cloud_hybrid_usage, added_by, allowed_actions, false);

		String invalidId = UUID.randomUUID().toString();
		test.log(LogStatus.INFO, "Invalid id :" + invalidId);
		// Preparing the additionalUrl
		String additionalURL = spogServer.PrepareURL(filterStr, sortStr, curr_page, page_size, test);
		test.log(LogStatus.INFO, "The response should fail when provided invalid orgid ");
		Response response = spogServer.getAllaccountsForSpecifiedMsp(msp_token, invalidId, additionalURL, test);
		test.log(LogStatus.INFO, "The value of the generated response is:" + response.getBody().asString());
		// validating the response for getAllAccountsForSpecified
		test.log(LogStatus.INFO, "validing the response generated for get all accounts for specifed msp");
		spogServer.checkResponseStatus(response, SpogConstants.RESOURCE_NOT_EXIST, test);
		spogServer.CheckallAccountsForSpecifiedMsp(response, SpogConstants.RESOURCE_NOT_EXIST, Accounts, extra_Inputs,
				curr_page, page_size, filterStr, sortStr, SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED, test);

		// Deleting the msp organization
		test.log(LogStatus.INFO, "Login in as csr admin to delete the organization");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		spogServer.DeleteOrganizationWithCheck(mspOrgId, test);

	}
	// Get all Accounts When provided csr_admin JWT Token

	@Test(dataProvider = "organizationAndUserInfo")
	public void getAllAccounts_csrJwtToken(String organizationName, String organizationType, String organizationEmail,
			String organizationPwd, String organizationFirstName, String organizationLastName) {
		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Ramya Nagepalli");
		// Generating different input parameters
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + msp_org_name;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;

		// Logging with the csr_admin To generate Token
		test.log(LogStatus.INFO, "Login with csr admin user");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		test.log(LogStatus.INFO, "Getting the token of the CSR admin ");
		String csr_token = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "The Token is " + csr_token);

		String prefix = RandomStringUtils.randomAlphanumeric(8);
		// creating organization with msp_admin user
		String mspOrgId = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "Login with msp user");

		// logging with the msp_admin user to get the JWT token
		spogServer.userLogin(organizationEmail, organizationPwd, test);
		test.log(LogStatus.INFO, "Getting the token of the msp admin ");
		String msp_token = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "The Token is " + msp_token);
		spogServer.setToken(msp_token);
		String user_id = spogServer.GetLoggedinUser_UserID();

		String user_name = spogServer.GetLoggedinUser_UserName();
		// creating array of msp_sub organizations

		ArrayList<ResponseBody> Accounts = new ArrayList<ResponseBody>();
		boolean flag = true;
		int i = 1;
		while (flag) {
			test.log(LogStatus.INFO, "Creating an Sub Organization for the Msp Organization");
			String organization_name = spogServer.ReturnRandom("spogqa_account");
			Response response = spogServer.createAccountWithCheck2(mspOrgId, organization_name, mspOrgId, test);

			Accounts.add(response.getBody());

			if (noofAccounts == i) {
				flag = false;
			}
			i++;
		}

		HashMap<String, Object> source_status = spogServer.compose_source_status("0", "0", "0", "0", "0");

		HashMap<String, Object> backup_status = spogServer.compose_backup_status("0", "0", "0", "0");

		HashMap<String, Object> cloud_direct_usage = spogServer.compose_cloud_direct_usage("0", "0");

		HashMap<String, Object> cloud_hybrid_usage = spogServer.compose_cloud_hybrid_usage("0", "0");

		HashMap<String, Object> added_by = spogServer.compose_added_by(user_id, user_name, organizationEmail);

		ArrayList<String> allowed_actions = new ArrayList<String>();

		allowed_actions.add("setthreshold");
		allowed_actions.add("assignadmin");
		allowed_actions.add("viewaccount");
		allowed_actions.add("delete");

		HashMap<String, Object> extra_Inputs = spogServer.compose_extra_Inputs("", "success", source_status,
				backup_status, cloud_direct_usage, cloud_hybrid_usage, added_by, allowed_actions, false);

		// Preparing the additionalUrl
		String additionalURL = spogServer.PrepareURL(filterStr, sortStr, curr_page, page_size, test);
		test.log(LogStatus.INFO, "The response should pass when provided csrToken ");
		Response response = spogServer.getAllaccountsForSpecifiedMsp(csr_token, mspOrgId, additionalURL, test);
		test.log(LogStatus.INFO, "The value of the generated response is:" + response.getBody().asString());
		// validating the response for getAllAccountsForSpecified with csr_admin
		// jwt token
		test.log(LogStatus.INFO, "validing the response generated for get all accounts for specifed msp");
		spogServer.CheckallAccountsForSpecifiedMsp(response, SpogConstants.SUCCESS_GET_PUT_DELETE, Accounts,
				extra_Inputs, curr_page, page_size, filterStr, sortStr, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

		// Deleting the msp organization
		test.log(LogStatus.INFO, "Login in as csr admin to delete the organization");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		spogServer.DeleteOrganizationWithCheck(mspOrgId, test);
	}

	// Data provider for msp,direct organization
	@DataProvider(name = "organizationAndUserInfo2")
	public final Object[][] getOrganizationAndUserInfo2() {
		return new Object[][] { { msp_org_name, SpogConstants.MSP_ORG, msp_org_email, common_password,
				msp_org_first_name, msp_org_last_name, direct_org_name, SpogConstants.DIRECT_ORG, direct_org_email,
				common_password, direct_org_first_name, direct_org_last_name } };
	}

	// Get all Accounts When provided direct admin joken

	@Test(dataProvider = "organizationAndUserInfo2")
	public void getAllAccounts_DirectJwtToken(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName, String organizationLastName,
			String organizationName1, String organizationType1, String organizationEmail1, String organizationPwd1,
			String organizationFirstName1, String organizationLastName1) {
		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Ramya Nagepalli");
		// Generating different input parameters
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + msp_org_name;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;

		// Logging with the csr_admin To generate Token
		test.log(LogStatus.INFO, "Login with csr admin user");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		test.log(LogStatus.INFO, "Getting the token of the CSR admin ");
		String csr_token = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "The Token is " + csr_token);

		String prefix = RandomStringUtils.randomAlphanumeric(8);
		// creating organization with msp_admin user
		String mspOrgId = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "Login with msp user");

		// create a direct organization
		organizationEmail1 = RandomStringUtils.randomAlphanumeric(4) + "_" + organizationEmail1;
		prefix = RandomStringUtils.randomAlphanumeric(8);
		String direct_organization_id = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name + org_model_prefix,
				organizationType1, organizationEmail1, organizationPwd, organizationFirstName1, organizationLastName1,
				test);
		test.log(LogStatus.INFO, "The organization id is " + direct_organization_id);

		// login with direct organization
		test.log(LogStatus.INFO, "Logging with msp organization user");
		spogServer.userLogin(organizationEmail1, common_password);
		String directToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "The value of direct Token: " + directToken);

		// logging with the msp_admin user to get the JWT token
		spogServer.userLogin(organizationEmail, organizationPwd, test);
		test.log(LogStatus.INFO, "Getting the token of the msp admin ");
		String msp_token = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "The Token is " + msp_token);
		spogServer.setToken(msp_token);
		String user_id = spogServer.GetLoggedinUser_UserID();

		String user_name = spogServer.GetLoggedinUser_UserName();
		// creating array of msp_sub organizations

		ArrayList<ResponseBody> Accounts = new ArrayList<ResponseBody>();
		boolean flag = true;
		int i = 1;
		while (flag) {
			test.log(LogStatus.INFO, "Creating an Sub Organization for the Msp Organization");
			String organization_name = spogServer.ReturnRandom("spogqa_account");
			Response response = spogServer.createAccountWithCheck2(mspOrgId, organization_name, mspOrgId, test);

			Accounts.add(response.getBody());

			if (noofAccounts == i) {
				flag = false;
			}
			i++;
		}

		HashMap<String, Object> source_status = spogServer.compose_source_status("0", "0", "0", "0", "0");

		HashMap<String, Object> backup_status = spogServer.compose_backup_status("0", "0", "0", "0");

		HashMap<String, Object> cloud_direct_usage = spogServer.compose_cloud_direct_usage("0", "0");

		HashMap<String, Object> cloud_hybrid_usage = spogServer.compose_cloud_hybrid_usage("0", "0");

		HashMap<String, Object> added_by = spogServer.compose_added_by(user_id, user_name, organizationEmail);

		ArrayList<String> allowed_actions = new ArrayList<String>();

		allowed_actions.add("setthreshold");
		allowed_actions.add("assignadmin");
		allowed_actions.add("viewaccount");
		allowed_actions.add("delete");

		HashMap<String, Object> extra_Inputs = spogServer.compose_extra_Inputs("", "success", source_status,
				backup_status, cloud_direct_usage, cloud_hybrid_usage, added_by, allowed_actions, false);

		// Preparing the additionalUrl
		String additionalURL = spogServer.PrepareURL(filterStr, sortStr, curr_page, page_size, test);
		test.log(LogStatus.INFO, "The response should fail  when provided DirectToken ");
		Response response = spogServer.getAllaccountsForSpecifiedMsp(directToken, mspOrgId, additionalURL, test);
		test.log(LogStatus.INFO, "The value of the generated response is:" + response.getBody().asString());

		// validating the response for getAllAccountsForSpecified with
		// direct_admin jwtToken
		test.log(LogStatus.INFO, "validing the response generated for get all accounts for specifed msp");
		spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
		spogServer.CheckallAccountsForSpecifiedMsp(response, SpogConstants.INSUFFICIENT_PERMISSIONS, Accounts,
				extra_Inputs, curr_page, page_size, filterStr, sortStr, SpogMessageCode.RESOURCE_PERMISSION_DENY, test);

		// Deleting the msp organization
		test.log(LogStatus.INFO, "Login in as csr admin to delete the organization");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		spogServer.DeleteOrganizationWithCheck(mspOrgId, test);

	}

	// Data provider for filtering based on the organization name
	@DataProvider(name = "Filter")
	public final Object[][] Filter() {
		return new Object[][] { { "", "create_ts;asc", 1, 20, }, { "", "create_ts;desc", 1, 20 } };
	}

	@DataProvider()
	@Test(dataProvider = "Filter")
	public void getAllAccounts_msp_usingFilerting_sorting1(String filterStr, String sortStr, int curr_page,
			int page_size) {
		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Ramya Nagepalli");
		String organizationPwd = common_password;
		// Generating different input parameters
		organizationName1 = RandomStringUtils.randomAlphanumeric(8) + "_" + msp_org_name;
		organizationEmail1 = RandomStringUtils.randomAlphanumeric(8) + "_" + msp_org_name + postfix_email;
		organizationFirstName1 = RandomStringUtils.randomAlphanumeric(8) + "_" + msp_user_first_name;
		organizationLastName1 = RandomStringUtils.randomAlphanumeric(8) + "_" + msp_user_last_name;
		test.log(LogStatus.INFO, "Login with csr admin user");
		// Logging with the csr_admin To generate Token
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		test.log(LogStatus.INFO, "Getting the token of the CSR admin ");
		String csr_token = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "The Token is " + csr_token);
		// creating organization with msp_admin user
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		String mspOrgId = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name + org_model_prefix,
				SpogConstants.MSP_ORG, organizationEmail1, organizationPwd, organizationFirstName1,
				organizationLastName1, test);
		test.log(LogStatus.INFO, "Login with msp user");
		ArrayList<String> organizations = new ArrayList<String>();
		test.log(LogStatus.INFO, "Creating an Sub Organization for the Msp Organization");

		// logging with the msp_admin user to get the JWT token
		spogServer.userLogin(organizationEmail1, organizationPwd, test);
		test.log(LogStatus.INFO, "Getting the token of the msp admin ");
		String msp_token = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "The Token is " + msp_token);
		spogServer.setToken(msp_token);
		String user_id = spogServer.GetLoggedinUser_UserID();

		String user_name = spogServer.GetLoggedinUser_UserName();
		// creating array of msp_sub organizations

		ArrayList<ResponseBody> Accounts = new ArrayList<ResponseBody>();
		boolean flag = true;
		int i = 1;
		while (flag) {
			test.log(LogStatus.INFO, "Creating an Sub Organization for the Msp Organization");
			String organization_name = spogServer.ReturnRandom("spogqa_account");

			organizations.add(organization_name);
			Response response = spogServer.createAccountWithCheck2(mspOrgId, organization_name, mspOrgId, test);

			Accounts.add(response.getBody());

			if (noofAccounts == i) {
				flag = false;
			}
			i++;
		}

		HashMap<String, Object> source_status = spogServer.compose_source_status("0", "0", "0", "0", "0");

		HashMap<String, Object> backup_status = spogServer.compose_backup_status("0", "0", "0", "0");

		HashMap<String, Object> cloud_direct_usage = spogServer.compose_cloud_direct_usage("0", "0");

		HashMap<String, Object> cloud_hybrid_usage = spogServer.compose_cloud_hybrid_usage("0", "0");

		HashMap<String, Object> added_by = spogServer.compose_added_by(user_id, user_name, organizationEmail1);

		ArrayList<String> allowed_actions = new ArrayList<String>();

		allowed_actions.add("setthreshold");
		allowed_actions.add("assignadmin");
		allowed_actions.add("viewaccount");
		allowed_actions.add("delete");

		HashMap<String, Object> extra_Inputs = spogServer.compose_extra_Inputs("", "success", source_status,
				backup_status, cloud_direct_usage, cloud_hybrid_usage, added_by, allowed_actions, false);

		test.log(LogStatus.INFO, "get all the Accounts for the Specified Msp");
		String name = organizations.get(0);
		// Fetching all the accounts for specified MSP
		filterStr = "organization_name;=;" + name;
		String additionalURL = spogServer.PrepareURL(filterStr, sortStr, curr_page, page_size, test);
		Response response = spogServer.getAllaccountsForSpecifiedMsp(msp_token, mspOrgId, additionalURL, test);
		test.log(LogStatus.INFO, "validing the response generated for get all accounts for specifed msp");
		spogServer.CheckallAccountsForSpecifiedMsp(response, SpogConstants.SUCCESS_GET_PUT_DELETE, Accounts,
				extra_Inputs, curr_page, page_size, filterStr, sortStr, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
	}

	@DataProvider(name = "Filter_account_name")
	public final Object[][] Filter_account_name() {
		return new Object[][] { { "account_name;=;", "create_ts;asc", 1, 2, },
				{ "account_name;=;", "create_ts;desc", 1, 2 } };
	}

	@DataProvider()
	@Test(dataProvider = "Filter_account_name")
	public void getAllAccounts_msp_usingFilerting_by_account_name(String filterStr, String sortStr, int curr_page,
			int page_size) {
		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Ramya Nagepalli");
		String organizationPwd = common_password;
		// Generating different input parameters
		organizationName1 = RandomStringUtils.randomAlphanumeric(8) + "_" + msp_org_name;
		organizationEmail1 = RandomStringUtils.randomAlphanumeric(8) + "_" + msp_org_name + postfix_email;
		organizationFirstName1 = RandomStringUtils.randomAlphanumeric(8) + "_" + msp_user_first_name;
		organizationLastName1 = RandomStringUtils.randomAlphanumeric(8) + "_" + msp_user_last_name;
		test.log(LogStatus.INFO, "Login with csr admin user");
		// Logging with the csr_admin To generate Token
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		test.log(LogStatus.INFO, "Getting the token of the CSR admin ");
		String csr_token = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "The Token is " + csr_token);
		// creating organization with msp_admin user
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		String mspOrgId = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name + org_model_prefix,
				SpogConstants.MSP_ORG, organizationEmail1, organizationPwd, organizationFirstName1,
				organizationLastName1, test);
		test.log(LogStatus.INFO, "Login with msp user");
		ArrayList<String> organizations = new ArrayList<String>();
		test.log(LogStatus.INFO, "Creating an Sub Organization for the Msp Organization");

		// logging with the msp_admin user to get the JWT token
		spogServer.userLogin(organizationEmail1, organizationPwd, test);
		test.log(LogStatus.INFO, "Getting the token of the msp admin ");
		String msp_token = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "The Token is " + msp_token);
		spogServer.setToken(msp_token);
		String user_id = spogServer.GetLoggedinUser_UserID();

		String user_name = spogServer.GetLoggedinUser_UserName();
		// creating array of msp_sub organizations

		ArrayList<ResponseBody> Accounts = new ArrayList<ResponseBody>();
		boolean flag = true;
		int i = 1;
		while (flag) {
			test.log(LogStatus.INFO, "Creating an Sub Organization for the Msp Organization");
			String organization_name = spogServer.ReturnRandom("spogqa_account");

			organizations.add(organization_name);
			Response response = spogServer.createAccountWithCheck2(mspOrgId, organization_name, mspOrgId, test);

			Accounts.add(response.getBody());

			if (noofAccounts == i) {
				flag = false;
			}
			i++;
		}

		HashMap<String, Object> source_status = spogServer.compose_source_status("0", "0", "0", "0", "0");

		HashMap<String, Object> backup_status = spogServer.compose_backup_status("0", "0", "0", "0");

		HashMap<String, Object> cloud_direct_usage = spogServer.compose_cloud_direct_usage("0", "0");

		HashMap<String, Object> cloud_hybrid_usage = spogServer.compose_cloud_hybrid_usage("0", "0");

		HashMap<String, Object> added_by = spogServer.compose_added_by(user_id, user_name, organizationEmail1);

		ArrayList<String> allowed_actions = new ArrayList<String>();

		allowed_actions.add("setthreshold");
		allowed_actions.add("assignadmin");
		allowed_actions.add("viewaccount");
		allowed_actions.add("delete");

		HashMap<String, Object> extra_Inputs = spogServer.compose_extra_Inputs("", "success", source_status,
				backup_status, cloud_direct_usage, cloud_hybrid_usage, added_by, allowed_actions, false);

		test.log(LogStatus.INFO, "get all the Accounts for the Specified Msp");
		String name = msp_admin_name;
		// Fetching all the accounts for specified MSP
		filterStr = filterStr + name;
		String additionalURL = spogServer.PrepareURL(filterStr, sortStr, curr_page, page_size, test);
		Response response = spogServer.getAllaccountsForSpecifiedMsp(msp_token, mspOrgId, additionalURL, test);
		test.log(LogStatus.INFO, "validing the response generated for get all accounts for specifed msp");
		spogServer.CheckallAccountsForSpecifiedMsp(response, SpogConstants.SUCCESS_GET_PUT_DELETE, Accounts,
				extra_Inputs, curr_page, page_size, filterStr, sortStr, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

	}

	@Test(dataProvider = "Filter_account_name")
	public void getAllAccounts_msp_usingFilerting_by_account_name_invalid(String filterStr, String sortStr,
			int curr_page, int page_size) {
		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Ramya Nagepalli");
		String organizationPwd = common_password;
		// Generating different input parameters
		organizationName1 = RandomStringUtils.randomAlphanumeric(8) + "_" + msp_org_name;
		organizationEmail1 = RandomStringUtils.randomAlphanumeric(8) + "_" + msp_org_name + postfix_email;
		organizationFirstName1 = RandomStringUtils.randomAlphanumeric(8) + "_" + msp_user_first_name;
		organizationLastName1 = RandomStringUtils.randomAlphanumeric(8) + "_" + msp_user_last_name;
		test.log(LogStatus.INFO, "Login with csr admin user");
		// Logging with the csr_admin To generate Token
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		test.log(LogStatus.INFO, "Getting the token of the CSR admin ");
		String csr_token = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "The Token is " + csr_token);
		// creating organization with msp_admin user
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		String mspOrgId = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name + org_model_prefix,
				SpogConstants.MSP_ORG, organizationEmail1, organizationPwd, organizationFirstName1,
				organizationLastName1, test);
		test.log(LogStatus.INFO, "Login with msp user");
		ArrayList<String> organizations = new ArrayList<String>();
		test.log(LogStatus.INFO, "Creating an Sub Organization for the Msp Organization");

		// logging with the msp_admin user to get the JWT token
		spogServer.userLogin(organizationEmail1, organizationPwd, test);
		test.log(LogStatus.INFO, "Getting the token of the msp admin ");
		String msp_token = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "The Token is " + msp_token);
		spogServer.setToken(msp_token);
		String user_id = spogServer.GetLoggedinUser_UserID();

		String user_name = spogServer.GetLoggedinUser_UserName();
		// creating array of msp_sub organizations

		ArrayList<ResponseBody> Accounts = new ArrayList<ResponseBody>();
		boolean flag = true;
		int i = 1;
		while (flag) {
			test.log(LogStatus.INFO, "Creating an Sub Organization for the Msp Organization");
			String organization_name = spogServer.ReturnRandom("spogqa_account");

			organizations.add(organization_name);
			Response response = spogServer.createAccountWithCheck2(mspOrgId, organization_name, mspOrgId, test);

			Accounts.add(response.getBody());

			if (noofAccounts == i) {
				flag = false;
			}
			i++;
		}

		HashMap<String, Object> source_status = spogServer.compose_source_status("0", "0", "0", "0", "0");

		HashMap<String, Object> backup_status = spogServer.compose_backup_status("0", "0", "0", "0");

		HashMap<String, Object> cloud_direct_usage = spogServer.compose_cloud_direct_usage("0", "0");

		HashMap<String, Object> cloud_hybrid_usage = spogServer.compose_cloud_hybrid_usage("0", "0");

		HashMap<String, Object> added_by = spogServer.compose_added_by(user_id, user_name, organizationEmail1);

		ArrayList<String> allowed_actions = new ArrayList<String>();

		allowed_actions.add("setthreshold");
		allowed_actions.add("assignadmin");
		allowed_actions.add("viewaccount");
		allowed_actions.add("delete");

		HashMap<String, Object> extra_Inputs = spogServer.compose_extra_Inputs("", "success", source_status,
				backup_status, cloud_direct_usage, cloud_hybrid_usage, added_by, allowed_actions, false);

		test.log(LogStatus.INFO, "get all the Accounts for the Specified Msp");
		String name = msp_admin_name_1;
		// Fetching all the accounts for specified MSP
		filterStr = filterStr + name;
		String additionalURL = spogServer.PrepareURL(filterStr, sortStr, curr_page, page_size, test);
		Response response = spogServer.getAllaccountsForSpecifiedMsp(msp_token, mspOrgId, additionalURL, test);
		test.log(LogStatus.INFO, "validing the response generated for get all accounts for specifed msp");
		spogServer.CheckallAccountsForSpecifiedMsp(response, SpogConstants.SUCCESS_GET_PUT_DELETE, Accounts,
				extra_Inputs, curr_page, page_size, filterStr, sortStr, null, test);

	}

	@AfterMethod
	public void getResult(ITestResult result) {
		if (result.getStatus() == ITestResult.FAILURE) {
			count1.setfailedcount();
			// remaincases=Nooftest-passedcases-failedcases;
			test.log(LogStatus.FAIL, "Test Case Failed is " + result.getName() + " with parameters as "
					+ Arrays.asList(result.getParameters()));
			test.log(LogStatus.FAIL, result.getThrowable().getMessage());

		} else if (result.getStatus() == ITestResult.SKIP) {
			test.log(LogStatus.SKIP, "Test Case Skipped is " + result.getName());
			count1.setskippedcount();
		} else if (result.getStatus() == ITestResult.SUCCESS) {
			count1.setpassedcount();
			// remaincases=Nooftest-passedcases-failedcases;

		}
		// ending test
		// endTest(logger) : It ends the current test and prepares to create
		// HTML report
		rep.endTest(test);
		// rep.flush();
	}

}
