package api.organizations;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class GetSpecifiedAccountForSpecifiedMSPTest extends base.prepare.Is4Org {

	private SPOGServer spogServer;
	private UserSpogServer userSpogServer;

	private ExtentTest test;
	private String common_password = "Mclaren@2020";
	private String csrAdminUserName;
	private String csrAdminPassword;

	private String csrReadOnlyUserName;
	private String csrReadOnlyPassword;
	private String csr_read_only_validToken;
	private String csr_read_only_user_id;

	private String prefix_direct = "spog_Ramya_direct";
	private String postfix_email = "@arcserve.com";
	private String direct_org_name = prefix_direct + "_org";
	private String direct_org_email = direct_org_name + postfix_email;
	private String direct_org_first_name = direct_org_name + "_first_name";
	private String direct_org_last_name = direct_org_name + "_last_name";
	private String direct_user_name = prefix_direct + "_admin";
	private String direct_user_name_email = prefix_direct + "_admin" + postfix_email;
	private String direct_user_first_name = direct_user_name + "_first_name";
	private String direct_user_last_name = direct_user_name + "_last_name";

	private String prefix_msp = "spog_Ramya_msp";
	private String msp_org_name = prefix_msp + "_org";
	private String msp_org_email = msp_org_name + postfix_email;
	private String msp_org_first_name = msp_org_name + "_first_name";
	private String msp_org_last_name = msp_org_name + "_last_name";
	private String msp_user_name = prefix_msp + "_admin";
	private String msp_user_name_email = prefix_msp + "_admin" + postfix_email;
	private String msp_user_first_name = msp_user_name + "_first_name";
	private String msp_user_last_name = msp_user_name + "_last_name";

	private String prefix_msp_account_admin = "spog_rakesh_msp_account";
	private String msp_account_admin_email = prefix_msp_account_admin + postfix_email;
	private String msp_account_admin_first_name = prefix_msp_account_admin + "_first_name";
	private String msp_account_admin_last_name = prefix_msp_account_admin + "_last_name";
	private String msp_account_admin_validToken;
	String msp_account_admin_id;
	String prefix = RandomStringUtils.randomAlphanumeric(8);

	// sub org related
	private String prefix_suborg = "spog_Ramya_suborg";
	private String sub_org_name = prefix_suborg + "_org";
	// used for test case count like passed,failed,remaining cases
	/*
	 * private SQLServerDb bqdb1; public int Nooftest;
	 * 
	 * long creationTime; String buildnumber=null; String buildVersion; String
	 * BQame=null; private testcasescount count1; private String
	 * running_Machine;
	 */

	int passedcases = 0;
	int failedcases = 0;
	int skippedcases = 0;

	HashMap<String, Object> source_status = null;

	HashMap<String, Object> backup_status = null;

	HashMap<String, Object> cloud_direct_usage = null;

	HashMap<String, Object> cloud_hybrid_usage = null;

	HashMap<String, Object> extra_Inputs = null;

	ArrayList<String> allowed_actions = new ArrayList<String>();

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
		userSpogServer = new UserSpogServer(baseURI, port);
		rep = ExtentManager.getInstance(this.getClass().getSimpleName(), logFolder);
		Nooftest = 0;
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		this.csrAdminUserName = adminUserName;
		this.csrAdminPassword = adminPassword;
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date = new java.util.Date();
		this.buildVersion = buildVersion + "_" + dateFormater.format(date);
		BQName = this.getClass().getSimpleName();
		String author = "Ramya.Nagepalli";
		runningMachine = runningMachine;
		BQName = this.getClass().getSimpleName();
		if (count1.isstarttimehit() == 0) {
			System.out.println("Into get GetSpecifiedAccountforSpecifiedMSP");
			creationTime = System.currentTimeMillis();
			count1.setcreationtime(creationTime);
			// creationTime = System.currentTimeMillis();
			try {
				bqdb1.updateTable(BQName, runningMachine, buildVersion, String.valueOf(Nooftest), "0", "0",
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
		source_status = spogServer.compose_source_status("0", "0", "0", "0", "0");

		backup_status = spogServer.compose_backup_status("0", "0", "0", "0");

		cloud_direct_usage = spogServer.compose_cloud_direct_usage("0", "0");

		cloud_hybrid_usage = spogServer.compose_cloud_hybrid_usage("0", "0");

		allowed_actions.add("setthreshold");
		allowed_actions.add("assignadmin");
		allowed_actions.add("viewaccount");
		allowed_actions.add("delete");

		// login with csr read only credentials
		spogServer.userLogin(csrReadOnlyUserName, csrReadOnlyPassword);
		csr_read_only_validToken = spogServer.getJWTToken();
		csr_read_only_user_id = spogServer.GetLoggedinUser_UserID();
	}

	@DataProvider(name = "suborganizationAndUserInfo")
	public final Object[][] getMSP_subOrganizationAndUserInfo() {
		return new Object[][] { { msp_org_name, SpogConstants.MSP_ORG, msp_org_email, common_password,
				msp_org_first_name, msp_org_last_name, msp_user_name_email, common_password, msp_user_first_name,
				msp_user_last_name, SpogConstants.MSP_ADMIN, sub_org_name } };
	}

	@DataProvider(name = "DirectorganizationAndUserInfo")
	public final Object[][] getDirectOrganizationAndUserInfo() {
		return new Object[][] { { direct_org_name, SpogConstants.DIRECT_ORG, direct_org_email, common_password,
				direct_org_first_name, direct_org_last_name, direct_user_name_email, common_password,
				direct_user_first_name, direct_user_last_name, SpogConstants.DIRECT_ADMIN, msp_org_name,
				SpogConstants.MSP_ORG, msp_org_email, common_password, msp_org_first_name, msp_org_last_name,
				msp_user_name_email, common_password, msp_user_first_name, msp_user_last_name, SpogConstants.MSP_ADMIN,
				sub_org_name } };
	}

	@DataProvider(name = "suborganization1AndUserInfo")
	public final Object[][] getMSP_subOrganization1AndUserInfo() {
		return new Object[][] { { msp_org_name, SpogConstants.MSP_ORG, msp_org_email, common_password,
				msp_org_first_name, msp_org_last_name, msp_user_name_email, common_password, msp_user_first_name,
				msp_user_last_name, SpogConstants.MSP_ADMIN, sub_org_name, msp_org_name, SpogConstants.MSP_ORG,
				msp_org_email, common_password, msp_org_first_name, msp_org_last_name, msp_user_name_email,
				common_password, msp_user_first_name, msp_user_last_name, SpogConstants.MSP_ADMIN } };

	}

	@Test(dataProvider = "suborganizationAndUserInfo")
	public void getsuborginfoById_mspuservalidtoken(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName, String organizationLastName,
			String userEmail, String userPassword, String FirstName, String LastName, String Role_Id,
			String suborgname) {
		test = rep.startTest("getsuborginfoById_mspuservalidtoken");
		test.assignAuthor("Ramya Nagepalli");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;
		FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + FirstName;
		LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + LastName;
		userEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;
		suborgname = spogServer.ReturnRandom(suborgname);
		test.log(LogStatus.INFO, "Login with csr admin user");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		String organization_id = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "The MSP organization id is " + organization_id);
		test.log(LogStatus.INFO,
				"Create a sub org under organization " + organizationName + " with org name as " + suborgname);
		String sub_org_Id = spogServer.createAccountWithCheck(organization_id, suborgname, organization_id, test);
		test.log(LogStatus.INFO, "logging in with the created user " + organizationEmail);
		spogServer.userLogin(organizationEmail, organizationPwd);
		String validToken = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Login with csr admin user");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		String user_id = spogServer.GetLoggedinUser_UserID();
		String user_name = spogServer.GetLoggedinUser_UserName();
		HashMap<String, Object> added_by = spogServer.compose_added_by(user_id, user_name, "xiang_csr@arcserve.com");

		HashMap<String, Object> extra_Inputs = spogServer.compose_extra_Inputs("", "success", source_status,
				backup_status, cloud_direct_usage, cloud_hybrid_usage, added_by, allowed_actions, false);

		test.log(LogStatus.INFO, "Getting the sub organization Info by using the msp admin user JWT" + validToken);
		spogServer.getsuborgaccountinfobyIdwithcheck(validToken, extra_Inputs, organization_id, sub_org_Id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, suborgname, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

		// Create msp_account_admin
		test.log(LogStatus.INFO, "Create a msp account admin under msp org");
		msp_account_admin_email = prefix + msp_account_admin_email;
		spogServer.setToken(validToken);
		msp_account_admin_id = spogServer.createUserAndCheck(msp_account_admin_email, common_password,
				msp_account_admin_first_name, msp_account_admin_last_name, SpogConstants.MSP_ACCOUNT_ADMIN,
				organization_id, test);
		spogServer.userLogin(msp_account_admin_email, common_password);
		this.msp_account_admin_validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "The token is :" + msp_account_admin_validToken);
		userSpogServer.assignMspAccountAdmins(organization_id, sub_org_Id, new String[] { msp_account_admin_id },
				validToken);

		allowed_actions.clear();

		allowed_actions.add("setthreshold");
		/* allowed_actions.add("assignadmin"); */
		allowed_actions.add("viewaccount");
		/* allowed_actions.add("delete"); */

		extra_Inputs = spogServer.compose_extra_Inputs("", "success", source_status, backup_status, cloud_direct_usage,
				cloud_hybrid_usage, added_by, allowed_actions, false);

		test.log(LogStatus.INFO,
				"Getting the sub organization Info by using the msp account admin JWT" + msp_account_admin_validToken);
		spogServer.getsuborgaccountinfobyIdwithcheck(msp_account_admin_validToken, extra_Inputs, organization_id,
				sub_org_Id, SpogConstants.SUCCESS_GET_PUT_DELETE, suborgname, SpogMessageCode.SUCCESS_GET_PUT_DEL,
				test);
		allowed_actions.clear();

		allowed_actions.add("setthreshold");
		allowed_actions.add("assignadmin");
		allowed_actions.add("viewaccount");
		allowed_actions.add("delete");

		// get specified accounts for specified msp using csrreadonly valid
		// token

		test.log(LogStatus.INFO,
				"Getting the sub organization Info by using the msp account admin JWT" + msp_account_admin_validToken);
		spogServer.getsuborgaccountinfobyIdwithcheck(csr_read_only_validToken, extra_Inputs, organization_id,
				sub_org_Id, SpogConstants.SUCCESS_GET_PUT_DELETE, suborgname, SpogMessageCode.SUCCESS_GET_PUT_DEL,
				test);

		test.log(LogStatus.INFO,
				"Performing the operations to delete the user and orginzation by loggin in as csr admin");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		spogServer.DeleteOrganizationWithCheck(sub_org_Id, test);
		spogServer.DeleteUserById(msp_account_admin_id, test);
		spogServer.DeleteOrganizationWithCheck(organization_id, test);

	}

	@Test(dataProvider = "suborganizationAndUserInfo")
	public void getsuborginfoById_mspuserinvalidtoken(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName, String organizationLastName,
			String userEmail, String userPassword, String FirstName, String LastName, String Role_Id,
			String suborgname) {
		test = rep.startTest("getsuborginfoById_mspuserinvalidtoken");
		test.assignAuthor("Ramya Nagepalli");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;
		FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + FirstName;
		LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + LastName;
		userEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;
		suborgname = spogServer.ReturnRandom(suborgname);
		test.log(LogStatus.INFO, "Login with csr admin user");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		String organization_id = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "The MSP organization id is " + organization_id);
		test.log(LogStatus.INFO,
				"Create a sub org under organization " + organizationName + " with org name as " + suborgname);
		String sub_org_Id = spogServer.createAccountWithCheck(organization_id, suborgname, organization_id, test);
		test.log(LogStatus.INFO, "logging in with the created user " + organizationEmail);
		spogServer.userLogin(organizationEmail, organizationPwd);
		String validToken = spogServer.getJWTToken() + "wrwr";
		test.log(LogStatus.INFO, "Getting the sub organization Info by using the msp admin user JWT" + validToken);

		test.log(LogStatus.INFO, "Login with csr admin user");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		String user_id = spogServer.GetLoggedinUser_UserID();
		String user_name = spogServer.GetLoggedinUser_UserName();
		HashMap<String, Object> added_by = spogServer.compose_added_by(user_id, user_name, "xiang_csr@arcserve.com");

		HashMap<String, Object> extra_Inputs = spogServer.compose_extra_Inputs("", "success", source_status,
				backup_status, cloud_direct_usage, cloud_hybrid_usage, added_by, allowed_actions, false);

		spogServer.getsuborgaccountinfobyIdwithcheck(validToken, extra_Inputs, organization_id, sub_org_Id,
				SpogConstants.NOT_LOGGED_IN, suborgname, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);
		test.log(LogStatus.INFO,
				"Performing the operations to delete the user and orginzation by loggin in as csr admin");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		spogServer.DeleteOrganizationWithCheck(sub_org_Id, test);
		spogServer.DeleteOrganizationWithCheck(organization_id, test);
	}

	@Test(dataProvider = "suborganizationAndUserInfo")
	public void getsuborginfoById_mspusermissingtoken(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName, String organizationLastName,
			String userEmail, String userPassword, String FirstName, String LastName, String Role_Id,
			String suborgname) {
		test = rep.startTest("getsuborginfoById_mspusermissingtoken");
		test.assignAuthor("Ramya Nagepalli");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;
		FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + FirstName;
		LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + LastName;
		userEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;
		suborgname = spogServer.ReturnRandom(suborgname);
		test.log(LogStatus.INFO, "Login with csr admin user");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		String organization_id = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "The MSP organization id is " + organization_id);
		test.log(LogStatus.INFO,
				"Create a sub org under organization " + organizationName + " with org name as " + suborgname);
		String sub_org_Id = spogServer.createAccountWithCheck(organization_id, suborgname, organization_id, test);
		test.log(LogStatus.INFO, "logging in with the created user " + organizationEmail);
		spogServer.userLogin(organizationEmail, organizationPwd);
		String validToken = "";
		test.log(LogStatus.INFO, "Getting the sub organization Info by using the msp admin user JWT" + validToken);

		test.log(LogStatus.INFO, "Login with csr admin user");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		String user_id = spogServer.GetLoggedinUser_UserID();
		String user_name = spogServer.GetLoggedinUser_UserName();
		HashMap<String, Object> added_by = spogServer.compose_added_by(user_id, user_name, "xiang_csr@arcserve.com");

		HashMap<String, Object> extra_Inputs = spogServer.compose_extra_Inputs("", "success", source_status,
				backup_status, cloud_direct_usage, cloud_hybrid_usage, added_by, allowed_actions, false);

		spogServer.getsuborgaccountinfobyIdwithcheck(validToken, extra_Inputs, organization_id, sub_org_Id,
				SpogConstants.NOT_LOGGED_IN, suborgname, SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);
		test.log(LogStatus.INFO,
				"Performing the operations to delete the user and orginzation by loggin in as csr admin");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		spogServer.DeleteOrganizationWithCheck(sub_org_Id, test);
		spogServer.DeleteOrganizationWithCheck(organization_id, test);
	}

	@Test(dataProvider = "suborganizationAndUserInfo")
	public void getsuborginfoById_csruservalidtoken(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName, String organizationLastName,
			String userEmail, String userPassword, String FirstName, String LastName, String Role_Id,
			String suborgname) {
		test = rep.startTest("getsuborginfoById_csruservalidtoken");
		test.assignAuthor("Ramya Nagepalli");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;
		FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + FirstName;
		LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + LastName;
		userEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;
		suborgname = spogServer.ReturnRandom(suborgname);
		test.log(LogStatus.INFO, "Login with csr admin user");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		String organization_id = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "The MSP organization id is " + organization_id);
		test.log(LogStatus.INFO,
				"Create a sub org under organization " + organizationName + " with org name as " + suborgname);
		String sub_org_Id = spogServer.createAccountWithCheck(organization_id, suborgname, organization_id, test);
		test.log(LogStatus.INFO, "logging in with the created user " + organizationEmail);
		// spogServer.userLogin(organizationEmail,organizationPwd);
		String validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "Getting the sub organization Info by using the csr admin user JWT" + validToken);

		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		String user_id = spogServer.GetLoggedinUser_UserID();
		String user_name = spogServer.GetLoggedinUser_UserName();
		HashMap<String, Object> added_by = spogServer.compose_added_by(user_id, user_name, "xiang_csr@arcserve.com");

		HashMap<String, Object> extra_Inputs = spogServer.compose_extra_Inputs("", "success", source_status,
				backup_status, cloud_direct_usage, cloud_hybrid_usage, added_by, allowed_actions, false);

		spogServer.getsuborgaccountinfobyIdwithcheck(validToken, extra_Inputs, organization_id, sub_org_Id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, suborgname, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

		// check for blocked status by using csr token
		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		spogServer.updateAccount_blocked(organization_id, sub_org_Id, "sub_org_1", "true");

		extra_Inputs = spogServer.compose_extra_Inputs("", "success", source_status, backup_status, cloud_direct_usage,
				cloud_hybrid_usage, added_by, allowed_actions, true);

		spogServer.getsuborgaccountinfobyIdwithcheck(validToken, extra_Inputs, organization_id, sub_org_Id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, "sub_org_1", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

		test.log(LogStatus.INFO,
				"Performing the operations to delete the user and orginzation by loggin in as csr admin");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		spogServer.DeleteOrganizationWithCheck(sub_org_Id, test);
		spogServer.DeleteOrganizationWithCheck(organization_id, test);
	}

	@Test(dataProvider = "DirectorganizationAndUserInfo")
	public void getsuborginfoById_directadmintoken(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName, String organizationLastName,
			String userEmail, String userPassword, String FirstName, String LastName, String Role_Id,
			String msporganizationName, String mspogranizationType, String msporganizationEmail,
			String msporganizationPwd, String msporganizationFirstName, String msporganizationLastName,
			String mspuserEmail, String mspuserPassword, String mspFirstName, String mspLastName, String mspRole_Id,
			String suborgname) {
		test = rep.startTest("getsuborginfoById_directadmintoken");
		test.assignAuthor("Ramya Nagepalli");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;
		FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + FirstName;
		LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + LastName;
		userEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;
		msporganizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + msporganizationName;
		msporganizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + msporganizationEmail;
		msporganizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + msporganizationFirstName;
		msporganizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + msporganizationLastName;
		mspuserEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + mspuserEmail;
		mspFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + mspFirstName;
		mspLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + mspLastName;
		suborgname = spogServer.ReturnRandom(suborgname);

		test.log(LogStatus.INFO, "Login with csr admin user");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);

		test.log(LogStatus.INFO, "Creating an organization of type " + organizationType);
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		String organization_id = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "The Direct organization id is " + organization_id);

		test.log(LogStatus.INFO, "Creating an organization of type " + mspogranizationType);
		prefix = RandomStringUtils.randomAlphanumeric(8);
		String organization_id1 = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name + org_model_prefix,
				mspogranizationType, msporganizationEmail, msporganizationPwd, msporganizationFirstName,
				msporganizationLastName, test);
		test.log(LogStatus.INFO, "The MSP organization id is " + organization_id1);

		test.log(LogStatus.INFO,
				"Create a sub org under organization " + msporganizationName + " with org name as " + suborgname);
		String sub_org_Id = spogServer.createAccountWithCheck(organization_id1, suborgname, organization_id1, test);

		test.log(LogStatus.INFO, "logging in with the created user of direct org " + organizationEmail);
		spogServer.userLogin(organizationEmail, organizationPwd);
		String validToken = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Login with csr admin user");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		String user_id = spogServer.GetLoggedinUser_UserID();
		String user_name = spogServer.GetLoggedinUser_UserName();
		HashMap<String, Object> added_by = spogServer.compose_added_by(user_id, user_name, "xiang_csr@arcserve.com");

		HashMap<String, Object> extra_Inputs = spogServer.compose_extra_Inputs("", "success", source_status,
				backup_status, cloud_direct_usage, cloud_hybrid_usage, added_by, allowed_actions, false);

		test.log(LogStatus.INFO, "Getting the sub organization Info by using the direct admin user JWT" + validToken);
		spogServer.getsuborgaccountinfobyIdwithcheck(validToken, extra_Inputs, organization_id, sub_org_Id,
				SpogConstants.RESOURCE_NOT_EXIST, suborgname, SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED, test);
		test.log(LogStatus.INFO,
				"Performing the operations to delete the user and organization by loggin in as csr admin");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		spogServer.DeleteOrganizationWithCheck(sub_org_Id, test);
		spogServer.DeleteOrganizationWithCheck(organization_id, test);
		spogServer.DeleteOrganizationWithCheck(organization_id1, test);
	}

	@Test(dataProvider = "suborganizationAndUserInfo")
	public void getsuborginfoById_deletedsuborg(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName, String organizationLastName,
			String userEmail, String userPassword, String FirstName, String LastName, String Role_Id,
			String suborgname) {
		test = rep.startTest("getsuborginfoById_deletedsuborg");
		test.assignAuthor("Ramya Nagepalli");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;
		FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + FirstName;
		LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + LastName;
		userEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;
		suborgname = spogServer.ReturnRandom(suborgname);
		test.log(LogStatus.INFO, "Login with csr admin user");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		String validToken = spogServer.getJWTToken();
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		String organization_id = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "The MSP organization id is " + organization_id);
		test.log(LogStatus.INFO,
				"Create a sub org under organization " + organizationName + " with org name as " + suborgname);
		String sub_org_Id = spogServer.createAccountWithCheck(organization_id, suborgname, organization_id, test);
		test.log(LogStatus.INFO,
				"Performing the operations to delete the user and organization by loggin in as csr admin");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		spogServer.DeleteOrganizationWithCheck(sub_org_Id, test);
		test.log(LogStatus.INFO, "Getting the sub organization Info by using the csradmin admin user JWT" + validToken);

		test.log(LogStatus.INFO, "Login with csr admin user");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		String user_id = spogServer.GetLoggedinUser_UserID();
		String user_name = spogServer.GetLoggedinUser_UserName();
		HashMap<String, Object> added_by = spogServer.compose_added_by(user_id, user_name, "xiang_csr@arcserve.com");

		HashMap<String, Object> extra_Inputs = spogServer.compose_extra_Inputs("", "success", source_status,
				backup_status, cloud_direct_usage, cloud_hybrid_usage, added_by, allowed_actions, false);

		spogServer.getsuborgaccountinfobyIdwithcheck(validToken, extra_Inputs, organization_id, sub_org_Id,
				SpogConstants.RESOURCE_NOT_EXIST, suborgname, SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED, test);

		// Create msp_account_admin
		test.log(LogStatus.INFO, "Create a msp account admin under msp org");
		msp_account_admin_email = prefix + msp_account_admin_email;
		spogServer.setToken(validToken);
		msp_account_admin_id = spogServer.createUserAndCheck(msp_account_admin_email, common_password,
				msp_account_admin_first_name, msp_account_admin_last_name, SpogConstants.MSP_ACCOUNT_ADMIN,
				organization_id, test);
		spogServer.userLogin(msp_account_admin_email, common_password);
		this.msp_account_admin_validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "The token is :" + msp_account_admin_validToken);
		userSpogServer.assignMspAccountAdmins(organization_id, sub_org_Id, new String[] { msp_account_admin_id },
				validToken);
		test.log(LogStatus.INFO,
				"Getting the sub organization Info by using the msp account admin JWT" + msp_account_admin_validToken);
		spogServer.getsuborgaccountinfobyIdwithcheck(msp_account_admin_validToken, extra_Inputs, organization_id,
				sub_org_Id, SpogConstants.RESOURCE_NOT_EXIST, suborgname,
				SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED, test);

		// deleting the created organizations and users
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		spogServer.DeleteUserById(msp_account_admin_id, test);
		spogServer.DeleteOrganizationWithCheck(organization_id, test);
	}

	@Test(dataProvider = "DirectorganizationAndUserInfo")
	public void getsuborginfoById_suborguser(String organizationName, String organizationType, String organizationEmail,
			String organizationPwd, String organizationFirstName, String organizationLastName, String userEmail,
			String userPassword, String FirstName, String LastName, String Role_Id, String msporganizationName,
			String mspogranizationType, String msporganizationEmail, String msporganizationPwd,
			String msporganizationFirstName, String msporganizationLastName, String mspuserEmail,
			String mspuserPassword, String mspFirstName, String mspLastName, String mspRole_Id, String suborgname) {
		test = rep.startTest("getsuborginfoById_suborguser");
		test.assignAuthor("Ramya Nagepalli");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;
		FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + FirstName;
		LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + LastName;
		userEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;
		msporganizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + msporganizationName;
		msporganizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + msporganizationEmail;
		msporganizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + msporganizationFirstName;
		msporganizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + msporganizationLastName;
		mspuserEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + mspuserEmail;
		mspFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + mspFirstName;
		mspLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + mspLastName;
		suborgname = spogServer.ReturnRandom(suborgname);

		test.log(LogStatus.INFO, "Login with csr admin user");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);

		test.log(LogStatus.INFO, "Creating an organization of type " + mspogranizationType);
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		String organization_id1 = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name + org_model_prefix,
				mspogranizationType, msporganizationEmail, msporganizationPwd, msporganizationFirstName,
				msporganizationLastName, test);
		test.log(LogStatus.INFO, "The MSP organization id is " + organization_id1);

		test.log(LogStatus.INFO,
				"Create a sub org under organization " + msporganizationName + " with org name as " + suborgname);
		String sub_org_Id = spogServer.createAccountWithCheck(organization_id1, suborgname, organization_id1, test);
		test.log(LogStatus.INFO, "The Suborg organization id is " + sub_org_Id);

		test.log(LogStatus.INFO, "Creating a direct user under suborganization");
		Response response = spogServer.createUser(userEmail, common_password, FirstName, LastName,
				SpogConstants.DIRECT_ADMIN, sub_org_Id, test);
		String user_id = spogServer.checkCreateUser(response, SpogConstants.SUCCESS_POST, userEmail, FirstName,
				LastName, SpogConstants.DIRECT_ADMIN, sub_org_Id, "", test);

		test.log(LogStatus.INFO, "Login with direct admin user of the sub organization" + user_id);
		spogServer.userLogin(userEmail, common_password);
		String validToken = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Login with csr admin user");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		user_id = spogServer.GetLoggedinUser_UserID();
		String user_name = spogServer.GetLoggedinUser_UserName();
		HashMap<String, Object> added_by = spogServer.compose_added_by(user_id, user_name, "xiang_csr@arcserve.com");

		HashMap<String, Object> extra_Inputs = spogServer.compose_extra_Inputs("", "success", source_status,
				backup_status, cloud_direct_usage, cloud_hybrid_usage, added_by, allowed_actions, false);

		test.log(LogStatus.INFO, "Getting the sub organization Info by using the direct admin user JWT" + validToken);
		spogServer.getsuborgaccountinfobyIdwithcheck(validToken, extra_Inputs, organization_id1, sub_org_Id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, suborgname, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		test.log(LogStatus.INFO,
				"Performing the operations to delete the user and organization by loggin in as csr admin");

		// deleting the created organizations and users
		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		spogServer.DeleteOrganizationWithCheck(sub_org_Id, test);
		spogServer.DeleteOrganizationWithCheck(organization_id1, test);
	}

	@Test(dataProvider = "suborganization1AndUserInfo")
	public void getsuborginfoById_othermspusertoken(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName, String organizationLastName,
			String userEmail, String userPassword, String FirstName, String LastName, String Role_Id, String suborgname,
			String organization1Name, String organization1Type, String organization1Email, String organization1Pwd,
			String organization1FirstName, String organization1LastName, String user1Email, String user1Password,
			String user1FirstName, String user1LastName, String user1Role_Id) {
		test = rep.startTest("getsuborginfoById_othermspusertoken");
		test.assignAuthor("Ramya Nagepalli");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;
		FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + FirstName;
		LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + LastName;
		userEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;
		organization1Name = RandomStringUtils.randomAlphanumeric(8) + "_" + organization1Name;
		organization1Email = RandomStringUtils.randomAlphanumeric(8) + "_" + organization1Email;
		organization1FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organization1FirstName;
		organization1LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organization1LastName;
		user1FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + user1LastName;
		user1LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + user1LastName;
		user1Email = RandomStringUtils.randomAlphanumeric(8) + "_" + user1Email;

		suborgname = spogServer.ReturnRandom(suborgname);
		test.log(LogStatus.INFO, "Login with csr admin user");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);

		String prefix = RandomStringUtils.randomAlphanumeric(8);
		String organization_id = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "The MSP_A organization id is " + organization_id);

		test.log(LogStatus.INFO,
				"Create a sub org under organization " + organizationName + " with org name as " + suborgname);
		String sub_org_Id = spogServer.createAccountWithCheck(organization_id, suborgname, organization_id, test);

		prefix = RandomStringUtils.randomAlphanumeric(8);
		String organization_id1 = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name + org_model_prefix,
				organization1Type, organization1Email, organization1Pwd, organization1FirstName, organization1LastName,
				test);
		test.log(LogStatus.INFO, "The MSP_B organization id is " + organization_id1);

		test.log(LogStatus.INFO, "Log in with MSP B msp_admin user " + organization1Email);
		spogServer.userLogin(organization1Email, organization1Pwd);
		String validToken = spogServer.getJWTToken();

		test.log(LogStatus.INFO,
				"Getting the sub organization of MSP_A Info by using the MSP_B admin user JWT" + validToken);
		spogServer.getsuborgaccountinfobyIdwithcheck(validToken, extra_Inputs, organization_id, sub_org_Id,
				SpogConstants.INSUFFICIENT_PERMISSIONS, suborgname, SpogMessageCode.RESOURCE_PERMISSION_DENY, test);
		test.log(LogStatus.INFO,
				"Performing the operations to delete the user and orginzation by loggin in as csr admin");

		// deleting the created organizations and users
		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		spogServer.DeleteOrganizationWithCheck(sub_org_Id, test);
		spogServer.DeleteOrganizationWithCheck(organization_id, test);
		spogServer.DeleteOrganizationWithCheck(organization_id1, test);
	}

	@Test(dataProvider = "suborganization1AndUserInfo")
	public void getsuborgAinfoById_parentid_MSPB_MSPBusertoken(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName, String organizationLastName,
			String userEmail, String userPassword, String FirstName, String LastName, String Role_Id, String suborgname,
			String organization1Name, String organization1Type, String organization1Email, String organization1Pwd,
			String organization1FirstName, String organization1LastName, String user1Email, String user1Password,
			String user1FirstName, String user1LastName, String user1Role_Id) {
		test = rep.startTest("getsuborgAinfoById_parentid_MSPB_MSPBusertoken");
		test.assignAuthor("Ramya Nagepalli");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;
		FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + FirstName;
		LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + LastName;
		userEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;
		organization1Name = RandomStringUtils.randomAlphanumeric(8) + "_" + organization1Name;
		organization1Email = RandomStringUtils.randomAlphanumeric(8) + "_" + organization1Email;
		organization1FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organization1FirstName;
		organization1LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organization1LastName;
		user1FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + user1LastName;
		user1LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + user1LastName;
		user1Email = RandomStringUtils.randomAlphanumeric(8) + "_" + user1Email;

		suborgname = spogServer.ReturnRandom(suborgname);
		String suborgnameB = spogServer.ReturnRandom(suborgname);
		test.log(LogStatus.INFO, "Login with csr admin user");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		String csr_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Create an MSPA org using the csr token");
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		String organization_id = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "The MSP_A organization id is " + organization_id);

		test.log(LogStatus.INFO, "Create a sub orgA under organization " + organizationName + " with org name as "
				+ suborgname + " usng csr token");
		String sub_org_Id = spogServer.createAccountWithCheck(organization_id, suborgname, organization_id, test);
		test.log(LogStatus.INFO, "The MSP_A sub organization id is " + sub_org_Id);

		test.log(LogStatus.INFO, "Create an MSPB org using the csr token");
		prefix = RandomStringUtils.randomAlphanumeric(8);
		String organization_id1 = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name + org_model_prefix,
				organization1Type, organization1Email, organization1Pwd, organization1FirstName, organization1LastName,
				test);
		test.log(LogStatus.INFO, "The MSP_B organization id is " + organization_id1);

		test.log(LogStatus.INFO, "Create a sub orgB under organization " + organization1Name + " with org name as "
				+ suborgnameB + " usng csr token");
		String sub_org_Id_B = spogServer.createAccountWithCheck(organization_id1, suborgnameB, organization_id1, test);
		test.log(LogStatus.INFO, "The MSP_B sub organization id is " + sub_org_Id_B);

		test.log(LogStatus.INFO, "Log in with MSP B msp_admin user " + organization1Email);
		spogServer.userLogin(organization1Email, organization1Pwd);
		String validToken = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Login with csr admin user");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		String user_id = spogServer.GetLoggedinUser_UserID();

		String user_name = spogServer.GetLoggedinUser_UserName();
		HashMap<String, Object> added_by = spogServer.compose_added_by(user_id, user_name, "xiang_csr@arcserve.com");

		HashMap<String, Object> extra_Inputs = spogServer.compose_extra_Inputs("", "success", source_status,
				backup_status, cloud_direct_usage, cloud_hybrid_usage, added_by, allowed_actions, false);

		test.log(LogStatus.INFO,
				"Getting the sub organization of MSP_A Info by MSPB parenid and using the MSP_B admin user JWT"
						+ validToken);
		spogServer.getsuborgaccountinfobyIdwithcheck(validToken, extra_Inputs, organization_id1, sub_org_Id,
				SpogConstants.RESOURCE_NOT_EXIST, suborgname, SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED, test);

		// deleting the created organizations and users
		test.log(LogStatus.INFO,
				"Performing the operations to delete the user and orginzation by loggin in as csr admin");

		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		spogServer.DeleteOrganizationWithCheck(sub_org_Id, test);
		spogServer.DeleteOrganizationWithCheck(sub_org_Id_B, test);
		spogServer.DeleteOrganizationWithCheck(organization_id, test);
		spogServer.DeleteOrganizationWithCheck(organization_id1, test);
	}

	@Test(dataProvider = "suborganization1AndUserInfo")
	public void getsuborgAinfoById_parentid_MSPB_suborgBusertoken(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName, String organizationLastName,
			String userEmail, String userPassword, String FirstName, String LastName, String Role_Id, String suborgname,
			String organization1Name, String organization1Type, String organization1Email, String organization1Pwd,
			String organization1FirstName, String organization1LastName, String user1Email, String user1Password,
			String user1FirstName, String user1LastName, String user1Role_Id) {
		test = rep.startTest("getsuborgAinfoById_parentid_MSPB_suborgBusertoken");
		test.assignAuthor("Ramya Nagepalli");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;
		FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + FirstName;
		LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + LastName;
		userEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;
		organization1Name = RandomStringUtils.randomAlphanumeric(8) + "_" + organization1Name;
		organization1Email = RandomStringUtils.randomAlphanumeric(8) + "_" + organization1Email;
		organization1FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organization1FirstName;
		organization1LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organization1LastName;
		user1FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + user1LastName;
		user1LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + user1LastName;
		user1Email = RandomStringUtils.randomAlphanumeric(8) + "_" + user1Email;

		suborgname = spogServer.ReturnRandom(suborgname);
		String suborgnameB = spogServer.ReturnRandom(suborgname);
		test.log(LogStatus.INFO, "Login with csr admin user");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		String csr_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Create an MSPA org using the csr token");
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		String organization_id = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "The MSP_A organization id is " + organization_id);

		test.log(LogStatus.INFO, "Log in with MSP A msp_admin user " + organizationEmail);
		spogServer.userLogin(organizationEmail, organizationPwd);

		test.log(LogStatus.INFO, "Create a sub orgA under organization " + organizationName + " with org name as "
				+ suborgname + " usng msp token");
		String sub_org_Id = spogServer.createAccountWithCheck(organization_id, suborgname, organization_id, test);
		test.log(LogStatus.INFO, "The MSP_A sub organization id is " + sub_org_Id);

		test.log(LogStatus.INFO, "Create a sub orgA under organization " + organizationName + " with org name as "
				+ suborgname + " usng msp token");
		String sub_org_Id_1 = spogServer.createAccountWithCheck(organization_id, suborgname + "1", organization_id,
				test);
		test.log(LogStatus.INFO, "The MSP_A sub organization id is " + sub_org_Id);

		test.log(LogStatus.INFO, "Login with csr admin user");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		csr_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Create an MSPB org using the csr token");
		prefix = RandomStringUtils.randomAlphanumeric(8);
		String organization_id1 = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name + org_model_prefix,
				organization1Type, organization1Email, organization1Pwd, organization1FirstName, organization1LastName,
				test);
		test.log(LogStatus.INFO, "The MSP_B organization id is " + organization_id1);

		test.log(LogStatus.INFO, "Create a sub orgB under organization " + organization1Name + " with org name as "
				+ suborgnameB + " usng csr token");
		String sub_org_Id_B = spogServer.createAccountWithCheck(organization_id1, suborgnameB, organization_id1, test);
		test.log(LogStatus.INFO, "The MSP_B sub organization id is " + sub_org_Id_B);

		test.log(LogStatus.INFO, "Log in with MSP B msp_admin user " + organization1Email);
		spogServer.userLogin(organization1Email, organization1Pwd);
		String validToken = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Create a direct user under the sub org B " + sub_org_Id_B);
		Response response = spogServer.createUser(userEmail, userPassword, FirstName, LastName,
				SpogConstants.DIRECT_ADMIN, sub_org_Id_B, test);
		String user_id_suborg = spogServer.checkCreateUser(response, SpogConstants.SUCCESS_POST, userEmail, FirstName,
				LastName, SpogConstants.DIRECT_ADMIN, sub_org_Id_B, "", test);
		spogServer.userLogin(userEmail, userPassword);
		String suborg_B_validToken = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Login with csr admin user");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		String user_id = spogServer.GetLoggedinUser_UserID();

		String user_name = spogServer.GetLoggedinUser_UserName();
		HashMap<String, Object> added_by = spogServer.compose_added_by(user_id, user_name, "xiang_csr@arcserve.com");

		HashMap<String, Object> extra_Inputs = spogServer.compose_extra_Inputs("", "success", source_status,
				backup_status, cloud_direct_usage, cloud_hybrid_usage, added_by, allowed_actions, false);

		test.log(LogStatus.INFO,
				"Getting the sub organization of MSP_A Info by MSPB parenid and using the suborg_B admin user JWT"
						+ suborg_B_validToken);
		spogServer.getsuborgaccountinfobyIdwithcheck(suborg_B_validToken, extra_Inputs, organization_id1, sub_org_Id,
				SpogConstants.RESOURCE_NOT_EXIST, suborgname, SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED, test);

		// Create msp_account_admin
		test.log(LogStatus.INFO, "Create a msp account admin under msp org");
		msp_account_admin_email = prefix + msp_account_admin_email;
		spogServer.userLogin(organizationEmail, organizationPwd);
		validToken = spogServer.getJWTToken();
		msp_account_admin_id = spogServer.createUserAndCheck(msp_account_admin_email, common_password,
				msp_account_admin_first_name, msp_account_admin_last_name, SpogConstants.MSP_ACCOUNT_ADMIN,
				organization_id, test);
		spogServer.userLogin(msp_account_admin_email, common_password);
		this.msp_account_admin_validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "The token is :" + msp_account_admin_validToken);
		userSpogServer.assignMspAccountAdmins(organization_id, sub_org_Id, new String[] { msp_account_admin_id },
				validToken);

		test.log(LogStatus.INFO,
				"Getting the sub organization of MSP_B Info by MSPA parenid and using the msp account admin JWT"
						+ msp_account_admin_validToken);
		spogServer.getsuborgaccountinfobyIdwithcheck(msp_account_admin_validToken, extra_Inputs, organization_id,
				sub_org_Id_1, SpogConstants.INSUFFICIENT_PERMISSIONS, suborgname + "1",
				SpogMessageCode.RESOURCE_PERMISSION_DENY, test);

		test.log(LogStatus.INFO,
				"Performing the operations to delete the user and orginzation by loggin in as csr admin");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		spogServer.DeleteOrganizationWithCheck(sub_org_Id, test);
		spogServer.DeleteOrganizationWithCheck(sub_org_Id_B, test);
		spogServer.DeleteOrganizationWithCheck(organization_id, test);
		spogServer.DeleteOrganizationWithCheck(organization_id1, test);
	}

	@Test(dataProvider = "suborganization1AndUserInfo")
	public void getsuborgAinfoById_parentid_csr_csrusertoken(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName, String organizationLastName,
			String userEmail, String userPassword, String FirstName, String LastName, String Role_Id, String suborgname,
			String organization1Name, String organization1Type, String organization1Email, String organization1Pwd,
			String organization1FirstName, String organization1LastName, String user1Email, String user1Password,
			String user1FirstName, String user1LastName, String user1Role_Id) {
		test = rep.startTest("getsuborgAinfoById_parentid_MSPB_suborgBusertoken");
		test.assignAuthor("Ramya Nagepalli");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;
		FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + FirstName;
		LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + LastName;
		userEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;
		organization1Name = RandomStringUtils.randomAlphanumeric(8) + "_" + organization1Name;
		organization1Email = RandomStringUtils.randomAlphanumeric(8) + "_" + organization1Email;
		organization1FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organization1FirstName;
		organization1LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organization1LastName;
		user1FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + user1LastName;
		user1LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + user1LastName;
		user1Email = RandomStringUtils.randomAlphanumeric(8) + "_" + user1Email;

		suborgname = spogServer.ReturnRandom(suborgname);
		String suborgnameB = spogServer.ReturnRandom(suborgname);
		test.log(LogStatus.INFO, "Login with csr admin user");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		String csr_org_id = spogServer.GetLoggedinUserOrganizationID();
		String csr_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Create an MSPA org using the csr token");
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		String organization_id = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "The MSP_A organization id is " + organization_id);

		spogServer.userLogin(organizationEmail, organizationPwd, test);
		String msp_org_id = spogServer.GetLoggedinUserOrganizationID();
		String msp_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Create a sub orgA under organization " + organizationName + " with org name as "
				+ suborgname + " usng msp token");
		String sub_org_Id = spogServer.createAccountWithCheck(msp_org_id, suborgname, msp_org_id, test);
		test.log(LogStatus.INFO, "The MSP_A sub organization id is " + sub_org_Id);

		test.log(LogStatus.INFO,
				"Getting the sub organization of MSP_A Info by csr parentid and using the csr token" + csr_token);
		spogServer.getsuborgaccountinfobyIdwithcheck(csr_token, extra_Inputs, csr_org_id, sub_org_Id,
				SpogConstants.RESOURCE_NOT_EXIST, suborgname, SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED, test);

		test.log(LogStatus.INFO,
				"Performing the operations to delete the user and orginzation by loggin in as csr admin");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		spogServer.DeleteOrganizationWithCheck(sub_org_Id, test);
		// spogServer.DeleteOrganizationWithCheck(sub_org_Id_B, test);
		spogServer.DeleteOrganizationWithCheck(organization_id, test);
		// spogServer.DeleteOrganizationWithCheck(organization_id1, test);
	}

	@Test(dataProvider = "suborganization1AndUserInfo")
	public void getsuborgAinfoById_parentid_MSPB_csrtoken(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName, String organizationLastName,
			String userEmail, String userPassword, String FirstName, String LastName, String Role_Id, String suborgname,
			String organization1Name, String organization1Type, String organization1Email, String organization1Pwd,
			String organization1FirstName, String organization1LastName, String user1Email, String user1Password,
			String user1FirstName, String user1LastName, String user1Role_Id) {
		test = rep.startTest("getsuborgAinfoById_parentid_MSPB_csrtoken");
		test.assignAuthor("Ramya Nagepalli");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;
		FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + FirstName;
		LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + LastName;
		userEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;
		organization1Name = RandomStringUtils.randomAlphanumeric(8) + "_" + organization1Name;
		organization1Email = RandomStringUtils.randomAlphanumeric(8) + "_" + organization1Email;
		organization1FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organization1FirstName;
		organization1LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organization1LastName;
		user1FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + user1LastName;
		user1LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + user1LastName;
		user1Email = RandomStringUtils.randomAlphanumeric(8) + "_" + user1Email;

		suborgname = spogServer.ReturnRandom(suborgname);
		String suborgnameB = spogServer.ReturnRandom(suborgname);
		test.log(LogStatus.INFO, "Login with csr admin user");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		String csr_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Create an MSPA org using the csr token");
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		String organization_id = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "The MSP_A organization id is " + organization_id);

		test.log(LogStatus.INFO, "Create a sub orgA under organization " + organizationName + " with org name as "
				+ suborgname + " usng csr token");
		String sub_org_Id = spogServer.createAccountWithCheck(organization_id, suborgname, organization_id, test);
		test.log(LogStatus.INFO, "The MSP_A sub organization id is " + sub_org_Id);

		test.log(LogStatus.INFO, "Create an MSPB org using the csr token");
		prefix = RandomStringUtils.randomAlphanumeric(8);
		String organization_id1 = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name + org_model_prefix,
				organization1Type, organization1Email, organization1Pwd, organization1FirstName, organization1LastName,
				test);
		test.log(LogStatus.INFO, "The MSP_B organization id is " + organization_id1);

		test.log(LogStatus.INFO, "Create a sub orgB under organization " + organization1Name + " with org name as "
				+ suborgnameB + " usng csr token");
		String sub_org_Id_B = spogServer.createAccountWithCheck(organization_id1, suborgnameB, organization_id1, test);
		test.log(LogStatus.INFO, "The MSP_B sub organization id is " + sub_org_Id_B);

		test.log(LogStatus.INFO, "Log in with MSP B msp_admin user " + organization1Email);
		spogServer.userLogin(organization1Email, organization1Pwd);
		String validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO,
				"Getting the sub organization of MSP_A Info by MSPB parenid and using the MSP_B admin user JWT"
						+ validToken);
		spogServer.getsuborgaccountinfobyIdwithcheck(csr_token, extra_Inputs, organization_id1, sub_org_Id,
				SpogConstants.RESOURCE_NOT_EXIST, suborgname, SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED, test);

		test.log(LogStatus.INFO,
				"Performing the operations to delete the user and orginzation by loggin in as csr admin");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		spogServer.DeleteOrganizationWithCheck(sub_org_Id, test);
		spogServer.DeleteOrganizationWithCheck(sub_org_Id_B, test);
		spogServer.DeleteOrganizationWithCheck(organization_id, test);
		spogServer.DeleteOrganizationWithCheck(organization_id1, test);
	}

	@Test(dataProvider = "suborganization1AndUserInfo")
	public void getsuborgA2infoById_parentid_MSPA_suborgA1usertoken(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName, String organizationLastName,
			String userEmail, String userPassword, String FirstName, String LastName, String Role_Id,
			String suborgnameA1, String organization1Name, String organization1Type, String organization1Email,
			String organization1Pwd, String organization1FirstName, String organization1LastName, String user1Email,
			String user1Password, String user1FirstName, String user1LastName, String user1Role_Id) {
		test = rep.startTest("getsuborgA2infoById_parentid_MSPA_suborgA1usertoken");
		test.assignAuthor("Ramya Nagepalli");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;
		FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + FirstName;
		LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + LastName;
		userEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;
		organization1Name = RandomStringUtils.randomAlphanumeric(8) + "_" + organization1Name;
		organization1Email = RandomStringUtils.randomAlphanumeric(8) + "_" + organization1Email;
		organization1FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organization1FirstName;
		organization1LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organization1LastName;
		user1FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + user1LastName;
		user1LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + user1LastName;
		user1Email = RandomStringUtils.randomAlphanumeric(8) + "_" + user1Email;

		suborgnameA1 = spogServer.ReturnRandom(suborgnameA1);
		String suborgnameA2 = spogServer.ReturnRandom(suborgnameA1);
		test.log(LogStatus.INFO, "Login with csr admin user");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		String csr_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Create an MSPA org using the csr token");
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		String organization_id = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "The MSP_A organization id is " + organization_id);

		test.log(LogStatus.INFO, "Create a sub orgA under organization " + organizationName + " with org name as "
				+ suborgnameA1 + " usng csr token");
		String sub_org_Id_A1 = spogServer.createAccountWithCheck(organization_id, suborgnameA1, organization_id, test);
		test.log(LogStatus.INFO, "The MSP_A sub organization id is " + sub_org_Id_A1);

		test.log(LogStatus.INFO, "Create a sub orgB under organization " + organization1Name + " with org name as "
				+ suborgnameA2 + " usng csr token");
		String sub_org_Id_A2 = spogServer.createAccountWithCheck(organization_id, suborgnameA2, organization_id, test);
		test.log(LogStatus.INFO, "The MSP_B sub organization id is " + sub_org_Id_A2);

		test.log(LogStatus.INFO, "Create a direct user under the sub org A1 " + sub_org_Id_A1);
		Response response = spogServer.createUser(userEmail, userPassword, FirstName, LastName,
				SpogConstants.DIRECT_ADMIN, sub_org_Id_A1, test);
		String user_id_suborg = spogServer.checkCreateUser(response, SpogConstants.SUCCESS_POST, userEmail, FirstName,
				LastName, SpogConstants.DIRECT_ADMIN, sub_org_Id_A1, "", test);
		spogServer.userLogin(userEmail, userPassword);
		String suborg_A1_validToken = spogServer.getJWTToken();

		test.log(LogStatus.INFO,
				"Getting the sub organization of Suborg_A2 Info by MSPA parenid and using the suborg_A1 admin user JWT"
						+ suborg_A1_validToken);
		spogServer.getsuborgaccountinfobyIdwithcheck(suborg_A1_validToken, extra_Inputs, organization_id, sub_org_Id_A2,
				SpogConstants.INSUFFICIENT_PERMISSIONS, suborgnameA2, SpogMessageCode.RESOURCE_PERMISSION_DENY, test);

		test.log(LogStatus.INFO,
				"Performing the operations to delete the user and orginzation by loggin in as csr admin");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		spogServer.DeleteOrganizationWithCheck(sub_org_Id_A1, test);
		spogServer.DeleteOrganizationWithCheck(sub_org_Id_A2, test);
		spogServer.DeleteOrganizationWithCheck(organization_id, test);

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
		// endTest(logger) : It ends the current test and prepares to create
		// HTML report
		rep.endTest(test);

	}

}
