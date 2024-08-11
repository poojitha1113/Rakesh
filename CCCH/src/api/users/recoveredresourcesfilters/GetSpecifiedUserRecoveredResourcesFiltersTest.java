package api.users.recoveredresourcesfilters;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
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

import Constants.OSMajor;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.SPOGJobServer;
import InvokerServer.SPOGRecoveredResourceServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class GetSpecifiedUserRecoveredResourcesFiltersTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private SPOGJobServer spogJobServer;
	private SPOGRecoveredResourceServer spogRecoveredResourceServer;

	private ExtentTest test;

	private TestOrgInfo ti;

	private String common_password = "Mclaren@2020";
	private String csrAdminUserName;
	private String csrAdminPassword;
	private String csr_token;

	private String csrReadOnlyUserName;
	private String csrReadOnlyPassword;
	private String csr_read_only_validToken;
	private String csr_read_only_user_id;

	private String prefix_direct = "SPOG_QA_RAMYA_BQ_direct";
	private String postfix_email = "@arcserve.com";
	private String direct_org_name = prefix_direct + "_org";
	private String direct_org_email = direct_org_name + postfix_email;
	private String direct_org_first_name = direct_org_name + "_first_name";
	private String direct_org_last_name = direct_org_name + "_last_name";
	private String direct_user_name = prefix_direct + "_admin";
	private String direct_user_name_email = prefix_direct + "_admin" + postfix_email;
	private String direct_user_first_name = direct_user_name + "_first_name";
	private String direct_user_last_name = direct_user_name + "_last_name";
	private String direct_user_validToken;

	private String prefix_direct_b = "SPOG_QA_RAMYA_BQ_direct_b";
	private String postfix_email_b = "@arcserve.com";
	private String direct_org_name_b = prefix_direct_b + "_org";
	private String direct_org_email_b = direct_org_name_b + postfix_email_b;
	private String direct_org_first_name_b = direct_org_name_b + "_first_name";
	private String direct_org_last_name_b = direct_org_name_b + "_last_name";
	private String direct_user_name_b = prefix_direct_b + "_admin";
	private String direct_user_name_email_b = prefix_direct_b + "_admin" + postfix_email_b;
	private String direct_user_first_name_b = direct_user_name_b + "_first_name";
	private String direct_user_last_name_b = direct_user_name_b + "_last_name";
	private String direct_user_validToken_b;

	private String prefix_direct_c = "SPOG_QA_RAMYA_BQ_direct_c";
	private String postfix_email_c = "@arcserve.com";
	private String direct_org_name_c = prefix_direct_c + "_org";
	private String direct_org_email_c = direct_org_name_c + postfix_email_c;
	private String direct_org_first_name_c = direct_org_name_c + "_first_name";
	private String direct_org_last_name_c = direct_org_name_c + "_last_name";
	private String direct_user_name_c = prefix_direct_c + "_admin";
	private String direct_user_name_email_c = prefix_direct_c + "_admin" + postfix_email_c;
	private String direct_user_first_name_c = direct_user_name_c + "_first_name";
	private String direct_user_last_name_c = direct_user_name_c + "_last_name";
	private String direct_user_validToken_c;

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

	private String prefix_msp_b = "SPOG_QA_RAMYA_BQ_msp_b";
	private String msp_org_name_b = prefix_msp_b + "_org";
	private String msp_org_email_b = msp_org_name_b + postfix_email;
	private String msp_org_first_name_b = msp_org_name_b + "_first_name";
	private String msp_org_last_name_b = msp_org_name_b + "_last_name";
	private String mspb_user_validToken;

	private String initial_sub_org_name_a = "SPOG_QA_RAMYA_BQ_sub_a";
	private String initial_sub_email_a = "spog_qa_sub_ramya_a@arcserve.com";
	private String initial_sub_first_name_a = "spog_qa_sub_ramya_a";
	private String initial_sub_last_name_a = "spog_qa_sub_nagepalli_a";
	private String sub_orga_user_validToken;

	private String initial_sub_org_name_b = "SPOG_QA_RAMYA_BQ_sub_b";
	private String initial_sub_email_b = "spog_qa_sub_ramya_b@arcserve.com";
	private String initial_sub_first_name_b = "spog_qa_sub_ramya_b";
	private String initial_sub_last_name_b = "spog_qa_sub_nagepalli_b";
	private String sub_orgb_user_validToken;

	private String initial_sub_org_name_c = "SPOG_QA_RAMYA_BQ_sub_c";
	private String initial_sub_email_c = "spog_qa_sub_ramya_c@arcserve.com";
	private String initial_sub_first_name_c = "spog_qa_sub_ramya_c";
	private String initial_sub_last_name_c = "spog_qa_sub_nagepalli_c";
	private String sub_orgc_user_validToken;

	private String prefix_msp_account_admin = "SPOG_QA_RAMYA_BQ_msp_account";
	private String msp_account_admin_email = prefix_msp_account_admin + postfix_email;
	private String msp_account_admin_first_name = prefix_msp_account_admin + "_first_name";
	private String msp_account_admin_last_name = prefix_msp_account_admin + "_last_name";
	private String msp_account_admin_validToken;
	String msp_account_admin_id;
	String msp_admin_name;
	String msp_account_admin_user_id;
	private UserSpogServer userSpogServer;

	private String site_version = "1.0.0";
	private String gateway_hostname = "Ramya";
	// used for test case count like passed,failed,remaining cases
	/*
	 * private SQLServerDb bqdb1; public int Nooftest;
	 */
	int passedcases = 0;
	int failedcases = 0;
	int skippedcases = 0;
	/*
	 * long creationTime; String buildnumber=null; String BQame=null;
	 */
	// private testcasescount count1;

	String direct_organization_id;
	String direct_organization_id_b;
	String direct_organization_id_c;
	String msp_organization_id;
	String msp_organization_id_b;
	String sub_org_Id;
	String sub_org_Id_1;
	String sub_org_Id_2;

	String csr_user_id;
	String direct_user_id;
	String direct_user_id_b;
	String direct_user_id_c;
	String msp_user_id;
	String mspb_user_id;
	String suborga_user_id;
	String suborgb_user_id;
	String suborgc_user_id;

	String direct_site_id;
	String direct_site_id_b;
	String direct_site_id_c;

	String suborga_site_id;
	String suborgb_site_id;
	String suborgc_site_id;

	/*
	 * private String runningMachine; private String buildVersion;
	 */
	ArrayList<String> State = new ArrayList<String>();
	ArrayList<String> recoveredResourceType = new ArrayList<String>();

	LocalDate date = LocalDate.now();
	LocalDate yesterday = date.minusDays(1);
	LocalDate tomorrow = yesterday.plusDays(2);

	ArrayList<String> columnIdList = new ArrayList<String>();
	ArrayList<HashMap<String, Object>> columnsHeadContent = new ArrayList<HashMap<String, Object>>();

	ArrayList<HashMap<String, Object>> filtersHeadContent = new ArrayList<HashMap<String, Object>>();
	ArrayList<String> filter_id = new ArrayList<String>();

	String csr_org_id, filterId;

	private String org_model_prefix = this.getClass().getSimpleName();

	@BeforeClass
	@Parameters({ "baseURI", "port", "csrAdminUserName", "csrAdminPassword", "csrReadOnlyAdminName",
			"csrReadOnlyAdminPassword", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword,
			String readOnlyUserName, String readOnlyPassword, String logFolder, String runningMachine,
			String buildVersion) {

		this.csrReadOnlyUserName = readOnlyUserName;
		this.csrReadOnlyPassword = readOnlyPassword;

		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogJobServer = new SPOGJobServer(baseURI, port);
		spogRecoveredResourceServer = new SPOGRecoveredResourceServer(baseURI, port);
		rep = ExtentManager.getInstance(this.getClass().getSimpleName(), logFolder);
		userSpogServer = new UserSpogServer(baseURI, port);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Ramya.Nagepalli";

		Nooftest = 0;
		this.csrAdminUserName = adminUserName;
		this.csrAdminPassword = adminPassword;
		// Used for creating a build number with dateformat
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date = new java.util.Date();
		this.buildVersion = buildVersion + "_" + dateFormater.format(date);

		BQName = this.getClass().getSimpleName();
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
		// login in as csr admin
		test.log(LogStatus.INFO, "Logging with csrAdmin");
		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
		csr_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Get the logged in user id ");
		csr_user_id = spogServer.GetLoggedinUser_UserID();
		test.log(LogStatus.INFO, "The csr user id is " + csr_user_id);
		csr_org_id = spogServer.GetLoggedinUserOrganizationID();

		String prefix = RandomStringUtils.randomAlphanumeric(8);
		// Create a msp org
		test.log(LogStatus.INFO, "create a msp org");
		this.msp_org_email = prefix + this.msp_org_email;
		msp_organization_id = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name + org_model_prefix,
				SpogConstants.MSP_ORG, this.msp_org_email, common_password, prefix + msp_org_first_name,
				prefix + msp_org_last_name, test);

		test.log(LogStatus.INFO, "create another msp org");
		this.msp_org_email_b = prefix + this.msp_org_email_b;
		msp_organization_id_b = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name_b + org_model_prefix,
				SpogConstants.MSP_ORG, this.msp_org_email_b, common_password, prefix + msp_org_first_name_b,
				prefix + msp_org_last_name_b, test);

		// Create a suborg under MSP org and a user for sub org
		spogServer.userLogin(this.msp_org_email, common_password);
		test.log(LogStatus.INFO, "Getting the JWTToken for the Logged in user");
		msp_user_validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "The token is :" + msp_user_validToken);

		test.log(LogStatus.INFO, "Get the logged in user id ");
		msp_user_id = spogServer.GetLoggedinUser_UserID();
		test.log(LogStatus.INFO, "The msp user id is " + msp_user_id);

		test.log(LogStatus.INFO, "Create a suborg A under msp org");
		sub_org_Id = spogServer.createAccountWithCheck(msp_organization_id, prefix + initial_sub_org_name_a,
				msp_organization_id);
		test.log(LogStatus.INFO, "Create a suborg B under msp org");
		sub_org_Id_1 = spogServer.createAccountWithCheck(msp_organization_id, prefix + initial_sub_org_name_b,
				msp_organization_id);
		test.log(LogStatus.INFO, "Create a suborg C under msp org");
		sub_org_Id_2 = spogServer.createAccountWithCheck(msp_organization_id, prefix + initial_sub_org_name_c,
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

		test.log(LogStatus.INFO, "Create a direct user for sub org C");
		this.initial_sub_email_c = prefix + initial_sub_email_c;
		suborgc_user_id = spogServer.createUserAndCheck(this.initial_sub_email_c, common_password,
				prefix + this.initial_sub_first_name_c, prefix + this.initial_sub_last_name_c,
				SpogConstants.DIRECT_ADMIN, sub_org_Id_2, test);

		test.log(LogStatus.INFO, "Login in to sub org A");
		spogServer.userLogin(this.initial_sub_email_a, common_password);
		test.log(LogStatus.INFO, "Getting the JWTToken for the Logged in user");
		sub_orga_user_validToken = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Create a site/register/login to the site");
		suborga_site_id = gatewayServer.createsite_register_login(sub_org_Id, sub_orga_user_validToken, suborga_user_id,
				"ts", "1.0.0", spogServer, test);

		test.log(LogStatus.INFO, "Login in to sub org B");
		spogServer.userLogin(this.initial_sub_email_b, common_password);
		test.log(LogStatus.INFO, "Getting the JWTToken for the Logged in user");
		sub_orgb_user_validToken = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Create a site/register/login to the site");
		suborgb_site_id = gatewayServer.createsite_register_login(sub_org_Id_1, sub_orgb_user_validToken,
				suborgb_user_id, "ts", "1.0.0", spogServer, test);

		test.log(LogStatus.INFO, "Login in to sub org C");
		spogServer.userLogin(this.initial_sub_email_c, common_password);
		test.log(LogStatus.INFO, "Getting the JWTToken for the Logged in user");
		sub_orgc_user_validToken = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Create a site/register/login to the site");
		suborgb_site_id = gatewayServer.createsite_register_login(sub_org_Id_2, sub_orgc_user_validToken,
				suborgc_user_id, "ts", "1.0.0", spogServer, test);

		// login to mspb and create a site
		spogServer.userLogin(this.msp_org_email_b, common_password);
		test.log(LogStatus.INFO, "Getting the JWTToken for the Logged in user");
		mspb_user_validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "The token is :" + mspb_user_validToken);

		test.log(LogStatus.INFO, "Get the logged in user id ");
		mspb_user_id = spogServer.GetLoggedinUser_UserID();
		test.log(LogStatus.INFO, "The msp user id is " + mspb_user_id);

		// login in as csr admin
		test.log(LogStatus.INFO, "Logging with csrAdmin");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		csr_token = spogServer.getJWTToken();

		// Create a direct org

		this.direct_org_email = prefix + this.direct_org_email;
		direct_organization_id = spogServer.CreateOrganizationWithCheck(prefix + direct_org_name + org_model_prefix,
				SpogConstants.DIRECT_ORG, this.direct_org_email, common_password, prefix + direct_org_first_name,
				prefix + direct_org_last_name, test);
		spogServer.userLogin(this.direct_org_email, common_password);
		test.log(LogStatus.INFO, "Getting the JWTToken for the Logged in user");
		direct_user_validToken = spogServer.getJWTToken();
		// direct_user_validToken=validToken;
		test.log(LogStatus.INFO, "The token is :" + direct_user_validToken);

		test.log(LogStatus.INFO, "Get the logged in user id ");
		direct_user_id = spogServer.GetLoggedinUser_UserID();
		test.log(LogStatus.INFO, "The direct org user id is " + direct_user_id);

		test.log(LogStatus.INFO, "Create a site/register/login to the site");
		direct_site_id = gatewayServer.createsite_register_login(direct_organization_id, direct_user_validToken,
				direct_user_id, "ts", "1.0.0", spogServer, test);
		spogServer.setToken(direct_user_validToken);

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

		// place all elements in state
		// all,provisioning,started,stopped,provisioning_failed,deprovisioning,deprovisioned,starting,stopping,restarting
		State.add("all");
		State.add("provisioning");
		State.add("started");
		State.add("stopped");
		State.add("provisioning_failed");
		State.add("deprovisioning");
		State.add("deprovisioned");
		State.add("starting");
		State.add("stopping");
		State.add("restarting");

		// place all elements in recovered resources types
		// all,clouddirect_draas,ivm,vsb

		recoveredResourceType.add("all");
		recoveredResourceType.add("clouddirect_draas");
		recoveredResourceType.add("ivm");
		recoveredResourceType.add("vsb");

		// login with csr read only credentials
		spogServer.userLogin(csrReadOnlyUserName, csrReadOnlyPassword);
		csr_read_only_validToken = spogServer.getJWTToken();

		csr_read_only_user_id = spogServer.GetLoggedinUser_UserID();

		ti = new TestOrgInfo(spogServer, test);
		spogServer.setToken(ti.csr_token);

	}

	@DataProvider(name = "get_RecoveredResources_filter_valid")
	public final Object[][] getRecoveredResourcesFilterValidParams() {

		return new Object[][] {

				{ "csr-readonly", ti.csr_readonly_token, ti.csr_readonly_admin_user_id, ti.csr_org_id,
						UUID.randomUUID().toString(), State.get(0), OSMajor.windows.name(),
						recoveredResourceType.get(0), spogServer.ReturnRandom("filter"), "true" },

				{ "DIRECT", ti.direct_org1_user1_token, ti.direct_org1_user1_id, ti.direct_org1_id,
						UUID.randomUUID().toString(), State.get(0), OSMajor.windows.name(),
						recoveredResourceType.get(0), spogServer.ReturnRandom("filter"), "true" },
				{ "MSP", ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id, ti.normal_msp_org1_id,
						UUID.randomUUID().toString(), State.get(1), OSMajor.windows.name(),
						recoveredResourceType.get(1), spogServer.ReturnRandom("filter"), "false" },
				{ "SUBORG", ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id,
						ti.normal_msp1_suborg1_id, UUID.randomUUID().toString(), State.get(3), OSMajor.windows.name(),
						recoveredResourceType.get(2), spogServer.ReturnRandom("filter"), "true" },

				// 3 tier cases
				{ "Root MSP", ti.root_msp_org1_user1_token, ti.root_msp_org1_user1_id, ti.root_msp_org1_id,
						UUID.randomUUID().toString(), State.get(1), OSMajor.windows.name(),
						recoveredResourceType.get(1), spogServer.ReturnRandom("filter"), "false" },
				{ "sub MSP", ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,
						ti.root_msp1_submsp_org1_id, UUID.randomUUID().toString(), State.get(1), OSMajor.windows.name(),
						recoveredResourceType.get(1), spogServer.ReturnRandom("filter"), "false" },
				{ "sub_org-sub_MSP", ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,
						ti.msp1_submsp1_sub_org1_id, UUID.randomUUID().toString(), State.get(1), OSMajor.windows.name(),
						recoveredResourceType.get(1), spogServer.ReturnRandom("filter"), "false" },

		};

	}

	// get the user defined job columns using csr/valid token
	@Test(dataProvider = "get_RecoveredResources_filter_valid")
	public void getRecoveredResourcesFilterValid_200(String organizationType, String validToken, String user_id,
			String org_id, String policy_id, String state, String OSmajor, String RecoveredResourceType,
			String filter_name, String is_default

	) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		test.assignAuthor("");

		spogRecoveredResourceServer.setToken(validToken);

		ArrayList<HashMap<String, Object>> compose_filters = new ArrayList<HashMap<String, Object>>();

		test.log(LogStatus.INFO, "create specified user RecoveredResources Filter in org:  " + organizationType
				+ "and compare with default filters");

		HashMap<String, Object> compose_filter_1 = new HashMap<String, Object>();

		ArrayList<String> policy_1 = new ArrayList<String>();
		policy_1.add(policy_id);

		ArrayList<String> State_1 = new ArrayList<String>();
		State_1.add(State.get(3));

		ArrayList<String> Osmajor_1 = new ArrayList<String>();
		Osmajor_1.add(OSmajor);

		ArrayList<String> coveredResourceType_1 = new ArrayList<String>();
		coveredResourceType_1.add(recoveredResourceType.get(0));

		compose_filter_1.put("policy_id", policy_1);
		compose_filter_1.put("state", State_1);
		compose_filter_1.put("os_major", Osmajor_1);
		compose_filter_1.put("recovered_resource_type", coveredResourceType_1);
		compose_filter_1.put("filter_name", filter_name + "abc");
		compose_filter_1.put("is_default", is_default);

		compose_filters.add(compose_filter_1);

		Response response = spogRecoveredResourceServer.createSpecifiedUserRecoveredResourcesFilters(user_id,
				validToken, policy_id, State.get(3), OSmajor, recoveredResourceType.get(0), filter_name + "abc",
				is_default, test);

		String filter_id = response.then().extract().path("data.filter_id").toString();

		spogRecoveredResourceServer.checkRecoveredResourceFilters(response, filter_id, policy_id, State.get(3), OSmajor,
				recoveredResourceType.get(0), filter_name + "abc", is_default, org_id, user_id,
				SpogConstants.SUCCESS_POST, SpogMessageCode.SUCCESS_POST, test, filtersHeadContent);

		test.log(LogStatus.INFO, "get the created filters in the organization");
		response = spogRecoveredResourceServer.getSpecifiedUserRecoveredResourcesFilters(user_id, validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, test);

		test.log(LogStatus.INFO, "check Recovered Resource Filters");
		spogRecoveredResourceServer.checkRecoveredResourceFilters(response, filter_id, policy_id, State.get(3), OSmajor,
				recoveredResourceType.get(0), filter_name + "abc", is_default, org_id, user_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test, compose_filters);

		test.log(LogStatus.INFO, "delete the created filters in the organization");
		response = spogRecoveredResourceServer.deleteSpecifiedUserRecoveredResourcesFiltersByFilterId(user_id,
				filter_id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, test);

	}

	@DataProvider(name = "get_RecoveredResources_filter_invalid")
	public final Object[][] getRecoveredResourcesFilterInValidParams() {

		return new Object[][] {

				{ "csr-readonly", ti.csr_readonly_token, ti.csr_readonly_admin_user_id, ti.csr_org_id,
						UUID.randomUUID().toString(), State.get(0), OSMajor.windows.name(),
						recoveredResourceType.get(0), spogServer.ReturnRandom("filter"), "true" },

				{ "DIRECT", ti.direct_org1_user1_token, ti.direct_org1_user1_id, ti.direct_org1_id,
						UUID.randomUUID().toString(), State.get(0), OSMajor.windows.name(),
						recoveredResourceType.get(0), spogServer.ReturnRandom("filter"), "true" },
				{ "MSP", ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id, ti.normal_msp_org1_id,
						UUID.randomUUID().toString(), State.get(1), OSMajor.windows.name(),
						recoveredResourceType.get(1), spogServer.ReturnRandom("filter"), "false" },
				{ "SUBORG", ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id,
						ti.normal_msp1_suborg1_id, UUID.randomUUID().toString(), State.get(3), OSMajor.windows.name(),
						recoveredResourceType.get(2), spogServer.ReturnRandom("filter"), "true" },

				// 3 tier cases
				{ "Root MSP", ti.root_msp_org1_user1_token, ti.root_msp_org1_user1_id, ti.root_msp_org1_id,
						UUID.randomUUID().toString(), State.get(1), OSMajor.windows.name(),
						recoveredResourceType.get(1), spogServer.ReturnRandom("filter"), "false" },
				{ "sub MSP", ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,
						ti.root_msp1_submsp_org1_id, UUID.randomUUID().toString(), State.get(1), OSMajor.windows.name(),
						recoveredResourceType.get(1), spogServer.ReturnRandom("filter"), "false" },
				{ "sub_org-sub_MSP", ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,
						ti.msp1_submsp1_sub_org1_id, UUID.randomUUID().toString(), State.get(1), OSMajor.windows.name(),
						recoveredResourceType.get(1), spogServer.ReturnRandom("filter"), "false" },

		};

	}

	// get the user defined job columns using csr/valid token
	@Test(dataProvider = "get_RecoveredResources_filter_invalid")
	public void getRecoveredResourcesFilterValid_401(String organizationType, String validToken, String user_id,
			String org_id, String policy_id, String state, String OSmajor, String recoveredResourceType,
			String filter_name, String is_default) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		test.assignAuthor("");

		spogRecoveredResourceServer.setToken(validToken);

		test.log(LogStatus.INFO, "create specified user RecoveredResources Filter in org:  " + organizationType);

		test.log(LogStatus.INFO, "created filters in the organization with valid token");

		Response response = spogRecoveredResourceServer.createSpecifiedUserRecoveredResourcesFilters(user_id,
				validToken, policy_id, state, OSmajor, recoveredResourceType, filter_name, is_default, test);

		String filter_id = response.then().extract().path("data.filter_id").toString();

		spogRecoveredResourceServer.checkRecoveredResourceFilters(response, filter_id, policy_id, state, OSmajor,
				recoveredResourceType, filter_name, is_default, org_id, user_id, SpogConstants.SUCCESS_POST,
				SpogMessageCode.SUCCESS_POST, test, filtersHeadContent);

		String MissingToken = "";

		test.log(LogStatus.INFO, "get filters in the organization with missing token");

		response = spogRecoveredResourceServer.getSpecifiedUserRecoveredResourcesFilters(user_id, MissingToken,
				SpogConstants.NOT_LOGGED_IN, test);

		spogRecoveredResourceServer.checkRecoveredResourceFilters(response, filter_id, policy_id, state, OSmajor,
				recoveredResourceType, filter_name, is_default, org_id, user_id, SpogConstants.NOT_LOGGED_IN,
				SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test, filtersHeadContent);

		String inValidToken = "abc";

		test.log(LogStatus.INFO, "get filters in the organization with invalid token");

		response = spogRecoveredResourceServer.getSpecifiedUserRecoveredResourcesFilters(user_id, inValidToken,
				SpogConstants.NOT_LOGGED_IN, test);

		spogRecoveredResourceServer.checkRecoveredResourceFilters(response, filter_id, policy_id, state, OSmajor,
				recoveredResourceType, filter_name, is_default, org_id, user_id, SpogConstants.NOT_LOGGED_IN,
				SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test, filtersHeadContent);

		test.log(LogStatus.INFO, "Delete the created filters in the organization");

		response = spogRecoveredResourceServer.deleteSpecifiedUserRecoveredResourcesFiltersByFilterId(user_id,
				filter_id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, test);

	}

	@DataProvider(name = "get_RecoveredResources_filter_invalid_403")
	public final Object[][] getRecoveredResourcesFilterInValidParams_403() {

		return new Object[][] { { "direct-msp", ti.direct_org1_user1_token, ti.direct_org1_user1_id, ti.direct_org1_id,
				UUID.randomUUID().toString(), State.get(0), OSMajor.windows.name(), recoveredResourceType.get(0),
				spogServer.ReturnRandom("filter"), "true", ti.normal_msp_org1_user1_token },
				{ "direct-suborg", ti.direct_org1_user1_token, ti.direct_org1_user1_id, ti.direct_org1_id,
						UUID.randomUUID().toString(), State.get(0), OSMajor.windows.name(),
						recoveredResourceType.get(0), spogServer.ReturnRandom("filter"), "true",
						ti.msp1_submsp1_suborg1_user1_token },
				{ "MSP-direct", ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id, ti.normal_msp_org1_id,
						UUID.randomUUID().toString(), State.get(1), OSMajor.windows.name(),
						recoveredResourceType.get(1), spogServer.ReturnRandom("filter"), "false",
						ti.direct_org1_user1_token },
				{ "MSP-mspb", ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id, ti.normal_msp_org1_id,
						UUID.randomUUID().toString(), State.get(1), OSMajor.windows.name(),
						recoveredResourceType.get(1), spogServer.ReturnRandom("filter"), "false",
						ti.normal_msp_org2_user1_token },
				{ "MSP-suborg", ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id, ti.normal_msp_org1_id,
						UUID.randomUUID().toString(), State.get(1), OSMajor.windows.name(),
						recoveredResourceType.get(1), spogServer.ReturnRandom("filter"), "false",
						ti.msp1_submsp1_suborg1_user1_token },
				{ "SUBORG-direct", ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id,
						ti.normal_msp1_suborg1_user1_id, UUID.randomUUID().toString(), State.get(3),
						OSMajor.windows.name(), recoveredResourceType.get(2), spogServer.ReturnRandom("filter"), "true",
						ti.direct_org1_user1_token },
				{ "SUBORG-msp", ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id,
						ti.normal_msp1_suborg1_user1_id, UUID.randomUUID().toString(), State.get(3),
						OSMajor.windows.name(), recoveredResourceType.get(2), spogServer.ReturnRandom("filter"), "true",
						ti.normal_msp_org2_user1_token },
				{ "SUBORG-suborgb", ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id,
						ti.normal_msp1_suborg1_user1_id, UUID.randomUUID().toString(), State.get(3),
						OSMajor.windows.name(), recoveredResourceType.get(2), spogServer.ReturnRandom("filter"), "true",
						ti.root_msp1_suborg1_user1_token },
				{ "SUBORG_MSP", ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id,
						ti.normal_msp1_suborg1_user1_id, UUID.randomUUID().toString(), State.get(3),
						OSMajor.windows.name(), recoveredResourceType.get(2), spogServer.ReturnRandom("filter"), "true",
						ti.root_msp1_suborg1_user1_token },
				{ "csr-direct", ti.csr_token, ti.csr_admin_user_id, ti.csr_org_id, UUID.randomUUID().toString(),
						State.get(0), OSMajor.windows.name(), recoveredResourceType.get(0),
						spogServer.ReturnRandom("filter"), "true", ti.direct_org1_user1_token },
				{ "csr-msp", ti.csr_token, ti.csr_admin_user_id, ti.csr_org_id, UUID.randomUUID().toString(),
						State.get(0), OSMajor.windows.name(), recoveredResourceType.get(0),
						spogServer.ReturnRandom("filter"), "true", ti.root_msp_org1_user1_token },
				{ "csr-suborg", ti.csr_token, ti.csr_admin_user_id, ti.csr_org_id, UUID.randomUUID().toString(),
						State.get(0), OSMajor.windows.name(), recoveredResourceType.get(0),
						spogServer.ReturnRandom("filter"), "true", ti.root_msp1_suborg1_user1_token },
				{ "csr-mspAdminAccount", ti.csr_token, ti.csr_admin_user_id, ti.csr_org_id,
						UUID.randomUUID().toString(), State.get(0), OSMajor.windows.name(),
						recoveredResourceType.get(0), spogServer.ReturnRandom("filter"), "true",
						ti.normal_msp_org1_msp_accountadmin1_token },

				{ "csr-read-only-direct", ti.csr_readonly_token, ti.csr_readonly_admin_user_id, ti.csr_org_id,
						UUID.randomUUID().toString(), State.get(0), OSMajor.windows.name(),
						recoveredResourceType.get(0), spogServer.ReturnRandom("filter"), "true",
						ti.direct_org1_user1_token },
				{ "csr-read-only-msp", ti.csr_readonly_token, ti.csr_readonly_admin_user_id, ti.csr_org_id,
						UUID.randomUUID().toString(), State.get(0), OSMajor.windows.name(),
						recoveredResourceType.get(0), spogServer.ReturnRandom("filter"), "true",
						ti.normal_msp_org1_user1_token },
				{ "csr-read-only-suborg", ti.csr_readonly_token, ti.csr_readonly_admin_user_id, ti.csr_org_id,
						UUID.randomUUID().toString(), State.get(0), OSMajor.windows.name(),
						recoveredResourceType.get(0), spogServer.ReturnRandom("filter"), "true",
						ti.normal_msp1_suborg1_user1_token },
				{ "csr-read-only-mspAdminAccount", ti.csr_readonly_token, ti.csr_readonly_admin_user_id, ti.csr_org_id,
						UUID.randomUUID().toString(), State.get(0), OSMajor.windows.name(),
						recoveredResourceType.get(0), spogServer.ReturnRandom("filter"), "true",
						ti.normal_msp_org1_msp_accountadmin1_token },

				// 3 tier architecture cases
				{ "INSUFFICIENT_PERMISSIONS of root msp-suborgb", ti.root_msp_org1_user1_token,
						ti.root_msp_org1_user1_id, ti.root_msp_org1_id, UUID.randomUUID().toString(), State.get(0),
						OSMajor.windows.name(), recoveredResourceType.get(0), spogServer.ReturnRandom("filter"), "true",
						ti.root_msp1_suborg1_user1_token },

				{ "INSUFFICIENT_PERMISSIONS of submsp1-root_suborgb", ti.root_msp1_submsp1_user1_token,
						ti.root_msp1_submsp1_user1_id, ti.root_msp1_submsp_org1_id, UUID.randomUUID().toString(),
						State.get(0), OSMajor.windows.name(), recoveredResourceType.get(0),
						spogServer.ReturnRandom("filter"), "true", ti.root_msp1_suborg1_user1_token },

				{ "INSUFFICIENT_PERMISSIONS of root_suborgb-submsp1", ti.root_msp1_suborg1_user1_token,
						ti.root_msp1_suborg1_user1_id, ti.root_msp1_suborg1_id, UUID.randomUUID().toString(),
						State.get(0), OSMajor.windows.name(), recoveredResourceType.get(0),
						spogServer.ReturnRandom("filter"), "true", ti.root_msp1_submsp1_user1_token },

				{ "INSUFFICIENT_PERMISSIONS of submsp1-submsp1_suborg", ti.root_msp1_submsp1_user1_token,
						ti.root_msp1_submsp1_user1_id, ti.root_msp1_submsp_org1_id, UUID.randomUUID().toString(),
						State.get(0), OSMajor.windows.name(), recoveredResourceType.get(0),
						spogServer.ReturnRandom("filter"), "true", ti.msp1_submsp1_suborg1_user1_token },

				{ "INSUFFICIENT_PERMISSIONS of root_msp-submsp1", ti.root_msp_org1_user1_token,
						ti.root_msp_org1_user1_id, ti.root_msp_org1_id, UUID.randomUUID().toString(), State.get(0),
						OSMajor.windows.name(), recoveredResourceType.get(0), spogServer.ReturnRandom("filter"), "true",
						ti.root_msp1_submsp1_user1_token },

				{ "INSUFFICIENT_PERMISSIONS of root_msp-submsp1_suborg", ti.root_msp_org1_user1_token,
						ti.root_msp_org1_user1_id, ti.root_msp_org1_id, UUID.randomUUID().toString(), State.get(0),
						OSMajor.windows.name(), recoveredResourceType.get(0), spogServer.ReturnRandom("filter"), "true",
						ti.msp1_submsp1_suborg1_user1_token }, };

	}

	// get the user defined job columns using csr/valid token
	@Test(dataProvider = "get_RecoveredResources_filter_invalid_403")
	public void getRecoveredResourcesFilterValid_403(String organizationType, String validToken, String user_id,
			String org_id, String policy_id, String state, String OSmajor, String recoveredResourceType,
			String filter_name, String is_default, String otherorgToken) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		test.assignAuthor("");
		spogRecoveredResourceServer.setToken(validToken);
		/*
		 * test.log(LogStatus.INFO,
		 * "create specified user RecoveredResources Filter in org:  " +
		 * organizationType); Response response =
		 * spogRecoveredResourceServer.createSpecifiedUserRecoveredResourcesFilters(
		 * user_id, validToken, policy_id, state, OSmajor, recoveredResourceType,
		 * filter_name, is_default, test);
		 * 
		 * String filter_id =
		 * response.then().extract().path("data.filter_id").toString();
		 * 
		 * spogRecoveredResourceServer.checkRecoveredResourceFilters(response,
		 * filter_id, policy_id, state, OSmajor, recoveredResourceType, filter_name,
		 * is_default, org_id, user_id, SpogConstants.SUCCESS_POST,
		 * SpogMessageCode.SUCCESS_POST, test, filtersHeadContent);
		 */

		String filter_id = UUID.randomUUID().toString();
		test.log(LogStatus.INFO, "get filters in the organization with other org token");
		Response response = spogRecoveredResourceServer.getSpecifiedUserRecoveredResourcesFilters(user_id,
				otherorgToken, SpogConstants.INSUFFICIENT_PERMISSIONS, test);

		spogRecoveredResourceServer.checkRecoveredResourceFilters(response, filter_id, policy_id, state, OSmajor,
				recoveredResourceType, filter_name, is_default, org_id, user_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
				SpogMessageCode.RESOURCE_PERMISSION_DENY, test, filtersHeadContent);
		/*
		 * test.log(LogStatus.INFO, "Delete the created filters in the organization");
		 * 
		 * response = spogRecoveredResourceServer.
		 * deleteSpecifiedUserRecoveredResourcesFiltersByFilterId(user_id, filter_id,
		 * validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		 */

	}

	// get the user defined job columns using csr/valid token
	@Test(dataProvider = "get_RecoveredResources_filter_invalid")
	public void getRecoveredResourcesFilterValid_404(String organizationType, String validToken, String user_id,
			String org_id, String policy_id, String state, String OSmajor, String recoveredResourceType,
			String filter_name, String is_default) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		test.assignAuthor("");

		spogRecoveredResourceServer.setToken(validToken);

		test.log(LogStatus.INFO, "create specified user RecoveredResources Filter in org:  " + organizationType);
		test.log(LogStatus.INFO, "created filters in the organization");

		Response response = spogRecoveredResourceServer.createSpecifiedUserRecoveredResourcesFilters(user_id,
				validToken, policy_id, state, OSmajor, recoveredResourceType, filter_name, is_default, test);

		String filter_id = response.then().extract().path("data.filter_id").toString();

		spogRecoveredResourceServer.checkRecoveredResourceFilters(response, filter_id, policy_id, state, OSmajor,
				recoveredResourceType, filter_name, is_default, org_id, user_id, SpogConstants.SUCCESS_POST,
				SpogMessageCode.SUCCESS_POST, test, filtersHeadContent);

		String random_user_id = UUID.randomUUID().toString();

		test.log(LogStatus.INFO, "get filters in the organization with random user id");

		response = spogRecoveredResourceServer.getSpecifiedUserRecoveredResourcesFilters(random_user_id, validToken,
				SpogConstants.RESOURCE_NOT_EXIST, test);

		spogRecoveredResourceServer.checkRecoveredResourceFilters(response, filter_id, policy_id, state, OSmajor,
				recoveredResourceType, filter_name, is_default, org_id, user_id, SpogConstants.RESOURCE_NOT_EXIST,
				SpogMessageCode.USER_ID_DOESNOT_EXIST, test, filtersHeadContent);

		test.log(LogStatus.INFO, "Delete the created filters in the organization");

		response = spogRecoveredResourceServer.deleteSpecifiedUserRecoveredResourcesFiltersByFilterId(user_id,
				filter_id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, test);

	}

	// get the user defined job columns using csr/valid token
	@Test(dataProvider = "get_RecoveredResources_filter_invalid")
	public void getRecoveredResourcesFilterValid_400(String organizationType, String validToken, String user_id,
			String org_id, String policy_id, String state, String OSmajor, String recoveredResourceType,
			String filter_name, String is_default) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		test.assignAuthor("");

		spogRecoveredResourceServer.setToken(validToken);

		test.log(LogStatus.INFO, "create specified user RecoveredResources Filter in org:  " + organizationType);

		test.log(LogStatus.INFO, "created filters in the organization");

		Response response = spogRecoveredResourceServer.createSpecifiedUserRecoveredResourcesFilters(user_id,
				validToken, policy_id, state, OSmajor, recoveredResourceType, filter_name, is_default, test);

		String filter_id = response.then().extract().path("data.filter_id").toString();

		spogRecoveredResourceServer.checkRecoveredResourceFilters(response, filter_id, policy_id, state, OSmajor,
				recoveredResourceType, filter_name, is_default, org_id, user_id, SpogConstants.SUCCESS_POST,
				SpogMessageCode.SUCCESS_POST, test, filtersHeadContent);

		String invalid_user_id = "abc";

		test.log(LogStatus.INFO, "get filters in the organization with invalid user_id");

		response = spogRecoveredResourceServer.getSpecifiedUserRecoveredResourcesFilters(invalid_user_id, validToken,
				SpogConstants.REQUIRED_INFO_NOT_EXIST, test);

		spogRecoveredResourceServer.checkRecoveredResourceFilters(response, filter_id, policy_id, state, OSmajor,
				recoveredResourceType, filter_name, is_default, org_id, user_id, SpogConstants.REQUIRED_INFO_NOT_EXIST,
				SpogMessageCode.ELEMENT_USERID_IS_NOT_UUID, test, filtersHeadContent);

		test.log(LogStatus.INFO, "Delete the created filters in the organization");

		response = spogRecoveredResourceServer.deleteSpecifiedUserRecoveredResourcesFiltersByFilterId(user_id,
				filter_id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, test);

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
		rep.endTest(test);
		rep.flush();
	}

	/******************************************************************
	 * RandomFunction
	 ******************************************************************************/
	public int gen_random_index(String[] job_status) {
		Random generator = new Random();
		int randomindx = generator.nextInt(job_status.length);

		return randomindx;
	}

}
