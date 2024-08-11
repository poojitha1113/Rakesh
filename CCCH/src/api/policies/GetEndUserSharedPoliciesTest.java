package api.policies;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.DestinationStatus;
import Constants.PolicyType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import Constants.volume_type;
import InvokerServer.GatewayServer;
import InvokerServer.Org4SPOGServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGCloudRPSServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGHypervisorsServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class GetEndUserSharedPoliciesTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private SPOGDestinationServer spogDestinationServer;
	private Policy4SPOGServer policy4SPOGServer;
	private SPOGHypervisorsServer spogHypervisorsServer;
	private Org4SPOGServer org4SpogServer;

	private ExtentTest test;
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
	private String direct_site_token;

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

	private String initial_sub_org_name_c = "SPOG_QA_RAMYA_BQ_sub_c";
	private String initial_sub_email_c = "spog_qa_sub_Ramya_c@arcserve.com";
	private String initial_sub_first_name_c = "spog_qa_sub_Ramya_c";
	private String initial_sub_last_name_c = "spog_qa_sub_Nagepalli_c";
	private String sub_orgc_user_validToken;

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

	private String site_version = "1.0.0";
	private String gateway_hostname = "Ramya";

	int passedcases = 0;
	int failedcases = 0;
	int skippedcases = 0;

	String direct_organization_id;
	String direct_organization_id_b;
	String msp_organization_id;
	String msp_organization_id_b;
	String sub_org_Id;
	String sub_org_Id_1;
	String sub_org_Id_2;

	String csr_user_id;
	String direct_user_id;
	String msp_user_id;
	String mspb_user_id;
	String suborga_user_id;
	String suborgb_user_id;
	String suborgc_user_id;

	String direct_site_id, direct_site_id_b, direct_user_id_b;

	String suborga_site_id;
	String suborgb_site_id;
	String suborgc_site_id, suborgc_site_token;
	String direct_token;
	String MSPvalidToken, mspOrg_id, msp_cloud_account_id, cloudAccountSecret;

	HashMap<String, Object> cloud_direct_volume = new HashMap<String, Object>();
	HashMap<String, String> retention = new HashMap<String, String>();
	private String destination_id = null, sub_orga_user_validToken_1, suborga_site_id_1, suborga_site_token_1, MSP_org,
			cloudAccountSecret_msp, destination_id_msp, destination_id_suborg;
	private String cloudAccountSecret_direct, destination_id_direct, direct_cloud_account_id;

	private ArrayList<String> direct_source_ids, sub_source_ids = new ArrayList<String>();
	private ArrayList<HashMap> direct_sources;
	private ArrayList<HashMap> sub_sources;

	private String org_model_prefix = this.getClass().getSimpleName();
	private String cloud_rps_name;
	private String cloud_rps_port;
	private String cloud_rps_protocol;
	private String cloud_rps_password;
	private String cloud_rps_username;
	private String csr_site_id;
	private String csr_site_name;
	private String rps_server_name, rps_server_id;
	private SPOGCloudRPSServer spogcloudRPSServer;

	String datacenters[] = new String[5];

	ArrayList<HashMap> direct_policies = new ArrayList<HashMap>();
	ArrayList<HashMap> sub_policies = new ArrayList<HashMap>();

	private String prefix_msp_account_admin = "SPOG_QA_RAMYA_BQ_msp_account";
	private String msp_account_admin_email = prefix_msp_account_admin + postfix_email;
	private String msp_account_admin_first_name = prefix_msp_account_admin + "_first_name";
	private String msp_account_admin_last_name = prefix_msp_account_admin + "_last_name";
	private String msp_account_admin_validToken;
	String msp_admin_name;
	String msp_account_admin_user_id;
	String msp_account_admin_id;
	private UserSpogServer userSpogServer;

	@BeforeClass
	@Parameters({ "baseURI", "port", "csrAdminUserName", "csrAdminPassword", "csrReadOnlyUserName",
			"csrReadOnlyPassword", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword,
			String readOnlyUserName, String readOnlyPassword, String logFolder, String runningMachine,
			String buildVersion) {

		this.csrReadOnlyUserName = readOnlyUserName;
		this.csrReadOnlyPassword = readOnlyPassword;

		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		policy4SPOGServer = new Policy4SPOGServer(baseURI, port);
		spogHypervisorsServer = new SPOGHypervisorsServer(baseURI, port);
		org4SpogServer = new Org4SPOGServer(baseURI, port);
		spogcloudRPSServer = new SPOGCloudRPSServer(baseURI, port);
		userSpogServer=new UserSpogServer(baseURI, port);
		
		rep = ExtentManager.getInstance(this.getClass().getSimpleName(), logFolder);
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

		String prefix = RandomStringUtils.randomAlphanumeric(8);

		// Create a msp org
		test.log(LogStatus.INFO, "create a msp org");
		this.msp_org_email = prefix + this.msp_org_email;

		spogServer.setToken(csr_token);
		msp_organization_id = spogServer.CreateOrganizationByEnrollWithCheck(prefix + msp_org_name + org_model_prefix,
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

		test.log(LogStatus.INFO, "get Cloud Account for MSP org");
		Response response = spogServer.getCloudAccounts(msp_user_validToken, "", test);
		msp_cloud_account_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");

		// create a sub org under msp
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

		test.log(LogStatus.INFO, "Create a site/register/login to the site");
		suborga_site_id = gatewayServer.createsite_register_login(sub_org_Id, sub_orga_user_validToken, suborga_user_id,
				"ts", "1.0.0", spogServer, test);
		suborga_site_token = gatewayServer.getJWTToken();

		// compose destination for sub org
		spogDestinationServer.setToken(msp_user_validToken);
		retention = spogDestinationServer.composeRetention("0", "0", "0", "0", "0", "0");
		cloud_direct_volume = spogDestinationServer.composeCloudDirectInfo(msp_cloud_account_id,
				"dest_cloud_direct_msp", "7D", "7 Days", 26.0, 24.0, 50.0, volume_type.normal.toString(), "windows8",
				retention);
		
		spogDestinationServer.setToken(csr_token);
		test.log(LogStatus.INFO, "Get the datacenter id");
		datacenters = spogDestinationServer.getDestionationDatacenterID();

		// create destination for suborg
		response = spogDestinationServer.createDestination(UUID.randomUUID().toString(), msp_user_validToken,
				msp_cloud_account_id, sub_org_Id, suborga_site_id,  datacenters[0], "20",
				"cloud_direct_volume", DestinationStatus.running.toString(), "destination_suborg_volume",
				cloud_direct_volume, test);
		destination_id_suborg = response.then().extract().path("data.destination_id");

		test.log(LogStatus.INFO, "Login in to sub org B");
		spogServer.userLogin(this.initial_sub_email_b, common_password);
		test.log(LogStatus.INFO, "Getting the JWTToken for the Logged in user");
		sub_orgb_user_validToken = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Create a site/register/login to the site");
		suborgb_site_id = gatewayServer.createsite_register_login(sub_org_Id_1, sub_orgb_user_validToken,
				suborgb_user_id, "ts", "1.0.0", spogServer, test);

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
		// csr_token = spogServer.getJWTToken();

		// Create a direct org

		this.direct_org_email = prefix + this.direct_org_email;
		direct_organization_id = spogServer.CreateOrganizationByEnrollWithCheck(
				prefix + direct_org_name + org_model_prefix, SpogConstants.DIRECT_ORG, this.direct_org_email,
				common_password, prefix + direct_org_first_name, prefix + direct_org_last_name, test);

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
		direct_site_token = gatewayServer.getJWTToken();

		// create a cloud account under direct
		response = spogServer.getCloudAccounts(direct_user_validToken, "", test);
		direct_cloud_account_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");

		spogDestinationServer.setToken(direct_user_validToken);
		retention = spogDestinationServer.composeRetention("0", "0", "0", "0", "0", "0");
		cloud_direct_volume = spogDestinationServer.composeCloudDirectInfo(direct_cloud_account_id, "dest_cloud_direct",
				"7D", "7 Days", 26.0, 24.0, 50.0, volume_type.normal.toString(), "windows8", retention);

		// create destination for direct
		response = spogDestinationServer.createDestination(UUID.randomUUID().toString(), direct_user_validToken,
				direct_cloud_account_id, direct_organization_id, direct_site_id, datacenters[0],
				"20", "cloud_direct_volume", DestinationStatus.running.toString(), "destination_direct_volume",
				cloud_direct_volume, test);
		destination_id_direct = response.then().extract().path("data.destination_id");

		// login in as csr admin
		test.log(LogStatus.INFO, "Logging with csrAdmin");
		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
		csr_token = spogServer.getJWTToken();

		// Create another direct B org
		this.direct_org_email_b = prefix + this.direct_org_email_b;
		direct_organization_id_b = spogServer.CreateOrganizationWithCheck(prefix + direct_org_name_b + org_model_prefix,
				SpogConstants.DIRECT_ORG, this.direct_org_email_b, common_password, prefix + direct_org_first_name_b,
				prefix + direct_org_last_name_b, test);
		spogServer.userLogin(this.direct_org_email_b, common_password);
		test.log(LogStatus.INFO, "Getting the JWTToken for the Logged in user");
		direct_user_validToken_b = spogServer.getJWTToken();
		// direct_user_validToken=validToken;
		test.log(LogStatus.INFO, "The token is :" + direct_user_validToken_b);

		test.log(LogStatus.INFO, "Get the logged in user id ");
		direct_user_id_b = spogServer.GetLoggedinUser_UserID();
		test.log(LogStatus.INFO, "The direct org user id is " + direct_user_id_b);

		test.log(LogStatus.INFO, "Create a site/register/login to the site");
		direct_site_id_b = gatewayServer.createsite_register_login(direct_organization_id_b, direct_user_validToken_b,
				direct_user_id_b, "ts", "1.0.0", spogServer, test);

		spogServer.setToken(direct_user_validToken_b);

		// login with csr read only credentials
		spogServer.userLogin(csrReadOnlyUserName, csrReadOnlyPassword);
		csr_read_only_validToken = spogServer.getJWTToken();
		csr_read_only_user_id = spogServer.GetLoggedinUser_UserID();
		
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


		// Activate Cloud Hybrid trail for organization
		test.log(LogStatus.INFO, "Activate Cloud Hybrid trail for direct organization");
		spogServer.postCloudhybridInFreeTrial(csr_token, direct_organization_id, "direct", false, false);

		test.log(LogStatus.INFO, "Activate Cloud Hybrid trail for msp organization");
		spogServer.postCloudhybridInFreeTrial(csr_token, msp_organization_id, "msp", false, false);

		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream("src\\CloudRPS.properties"));

			cloud_rps_name = prop.getProperty("cloud_rps_name");
			cloud_rps_port = prop.getProperty("cloud_rps_port");
			cloud_rps_protocol = prop.getProperty("cloud_rps_protocol");
			cloud_rps_username = prop.getProperty("cloud_rps_username");
			cloud_rps_password = prop.getProperty("cloud_rps_password");
			csr_site_id = prop.getProperty("csr_site_id");
			csr_site_name = prop.getProperty("csr_site_name");

		} catch (IOException e) {
			e.printStackTrace();
			test.log(LogStatus.INFO, "Failed to load RPS properties file");
		}

		response = spogServer.getLoggedInUser(csr_token, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		csr_user_id = response.then().extract().path("data.user_id");
		String csr_organization_id = response.then().extract().path("data.organization_id");
		String csr_organizationEmail = response.then().extract().path("data.email");
		

		// create cloud rps with csr token
		spogcloudRPSServer.setToken(csr_token);
		test.log(LogStatus.INFO, " create cloud rps with csr token");
		response = spogcloudRPSServer.createCloudRPS(cloud_rps_name, cloud_rps_port, cloud_rps_protocol,
				cloud_rps_username, cloud_rps_password, csr_organization_id, csr_site_id, datacenters[0], csr_token);
		rps_server_name = response.then().extract().path("data.server_name");
		rps_server_id = response.then().extract().path("data.server_id");

	}

	@DataProvider(name = "datastore_info")
	public final Object[][] datastore_info() {
		return new Object[][] {
				{ "Direct", direct_organization_id, spogServer.ReturnRandom("destination") + "1", "", datacenters[0],
						"0.001" },
				{ "sub", sub_org_Id, spogServer.ReturnRandom("destination") + "1", "", datacenters[0], "0.001" },

				{ "Direct", direct_organization_id, spogServer.ReturnRandom("destination") + "2", "", datacenters[0],
						"0.001" },
				{ "sub", sub_org_Id, spogServer.ReturnRandom("destination") + "2", "", datacenters[0], "0.001" },

		};
	}

	@Test(dataProvider = "datastore_info")
	public void createCloudHybridPolicies(String org_type, String organization_id, String destination_name,
			String volume, String datacenter_id, String capacity) {
		test.log(LogStatus.INFO, "create CH policy for org :" + org_type);

		HashMap<String, Object> hybridStoreInfo = spogcloudRPSServer.composeCloudHybridStoreInfo(csr_token,
				destination_name, rps_server_id, organization_id, volume, datacenter_id, capacity);

		HashMap<String, String> expected_data = new HashMap<String, String>();

		expected_data.put("policy_name", destination_name + " policy");
		expected_data.put("policy_type", PolicyType.cloud_hybrid_replication.toString());
		expected_data.put("policy_status", "success");

		if (org_type.equalsIgnoreCase("sub")) {
			sub_policies.add(expected_data);

		} else {
			direct_policies.add(expected_data);

		}
		spogcloudRPSServer.createCloudHybridStoreWithCheck(csr_token, hybridStoreInfo, SpogConstants.SUCCESS_POST,
				SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
	}

	@DataProvider(name = "org_info")
	public final Object[][] org_info() {
		return new Object[][] {
				{ "valid - get direct org shared policies", "Direct", direct_organization_id, direct_user_validToken,
						SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, direct_policies },
				{ "valid - get sub org shared policies", "Sub", sub_org_Id, sub_orga_user_validToken,
						SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, sub_policies },
				
				{ "invalid CSR - get direct org shared policies", "Direct", direct_organization_id, csr_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.UNABLE_TO_GET_POLICY_FOR_NON_DIRECT_USER, null },
			
				{ "invalid CSR Read Only- get sub org shared policies", "Sub", sub_org_Id, csr_read_only_validToken,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.UNABLE_TO_GET_POLICY_FOR_NON_DIRECT_USER, null },
				
				{ "invalid msp- get sub org shared policies", "Sub", sub_org_Id, msp_user_validToken,
							SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.UNABLE_TO_GET_POLICY_FOR_NON_DIRECT_USER, null },
				
				{ "invalid msp account admin - get sub org shared policies", "Sub", sub_org_Id, msp_account_admin_validToken,
								SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.UNABLE_TO_GET_POLICY_FOR_NON_DIRECT_USER, null },

				{ "invalid token- get direct org shared policies", "Direct", direct_organization_id, "abc",
						SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, null },
				{ "invalid token- get sub org shared policies", "Sub", sub_org_Id, "", SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTHENTICATION_FAILED, null },

		};
	}

	@Test(dataProvider = "org_info", dependsOnMethods = "createCloudHybridPolicies")
	public void getSharedPoliciesofLoggedInUser(String testcase, String org_type, String organization_id,
			String validtoken, int ErrorCode, SpogMessageCode errormessage, ArrayList<HashMap> expected_data) {
		test.log(LogStatus.INFO, "get shared policies of org type : " + org_type);
		policy4SPOGServer.setToken(validtoken);

		policy4SPOGServer.getSharedPoliciesofLoggedInUserWithCheck(validtoken, ErrorCode, errormessage, expected_data,
				test);
	}

	@AfterMethod
	public void getResult(ITestResult result) {
		if (result.getStatus() == ITestResult.FAILURE) {
			count1.setfailedcount();

			test.log(LogStatus.FAIL, "Test Case Failed is " + result.getName() + " with parameters as "
					+ Arrays.asList(result.getParameters()));
			test.log(LogStatus.FAIL, result.getThrowable().getMessage());

		} else if (result.getStatus() == ITestResult.SKIP) {
			test.log(LogStatus.SKIP, "Test Case Skipped is " + result.getName());
			count1.setskippedcount();
		} else if (result.getStatus() == ITestResult.SUCCESS) {
			count1.setpassedcount();

		}
		rep.endTest(test);
		rep.flush();
	}

	@AfterClass
	public void updatebd() {

		ArrayList<String> policies = new ArrayList<String>();

		test.log(LogStatus.INFO, "get the policies of r=each org and delete the policies");
		policy4SPOGServer.setToken(direct_user_validToken);
		Response response = policy4SPOGServer.getPolicies(null);
		policies = response.then().extract().path("data.policy_id");

		if (!policies.isEmpty()) {
			policies.stream().forEach(policy -> {
				policy4SPOGServer.deletePolicybyPolicyId(csr_token, policy);
			});
		}

		policy4SPOGServer.setToken(sub_orga_user_validToken);
		response = policy4SPOGServer.getPolicies(null);
		policies = response.then().extract().path("data.policy_id");

		if (!policies.isEmpty()) {
			policies.stream().forEach(policy -> {
				policy4SPOGServer.deletePolicybyPolicyId(csr_token, policy);
			});
		}

		test.log(LogStatus.INFO, "Get the cloud RPS datastores and delete");
		response = spogcloudRPSServer.getCloudHybridStoresOnSpecifiedRPS(csr_token, rps_server_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		ArrayList<HashMap<String, Object>> datastores = response.then().extract().path("data");
		if (!datastores.isEmpty()) {
			datastores.stream().forEach(datastore -> {
				spogcloudRPSServer.destroyCloudHybridStoreById(csr_token, datastore.get("destination_id").toString(),
						SpogConstants.SUCCESS_GET_PUT_DELETE, test);
				// deleteCloudRPSDataStore(datastore.get("datastore_id").toString(), csr_token,
				// SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
				try {
					Thread.sleep(2500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			});
		}
		test.log(LogStatus.INFO, "Delete  the RPS");
		spogcloudRPSServer.deleteCloudRPS(rps_server_id, csr_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

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
		String orgHasString = this.getClass().getSimpleName();
		System.out.println(orgHasString);
		System.out.println("in father afterclass");
		System.out.println("class in father is:" + orgHasString);
		System.out.println("in father after class");
		destroyOrg(orgHasString);
	}

}
