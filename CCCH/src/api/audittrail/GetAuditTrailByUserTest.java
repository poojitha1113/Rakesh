package api.audittrail;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.joda.time.LocalDate;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Base64.EncodingBase64;
import Constants.AuditCodeConstants;
import Constants.ConnectionStatus;
import Constants.DestinationStatus;
import Constants.DestinationType;
import Constants.LogSeverityType;
import Constants.OSMajor;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.Linux4SPOGServer;
import InvokerServer.Log4GatewayServer;
import InvokerServer.Log4SPOGServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGCloudRPSServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGJobServer;
import InvokerServer.SPOGRecoveredResourceServer;
import InvokerServer.SPOGReportServer;
import InvokerServer.SPOGServer;
import InvokerServer.ServerResponseCode;
import InvokerServer.Source4SPOGServer;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class GetAuditTrailByUserTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private Linux4SPOGServer linux4spogServer;
	private SPOGCloudRPSServer spogcloudRPSServer;
	private GatewayServer gatewayServer;
	private SPOGDestinationServer spogDestinationServer;
	private SPOGRecoveredResourceServer spogRecoveredResourceServer;
	private SPOGJobServer spogJobServer;
	private UserSpogServer userSpogServer;
	private Log4SPOGServer log4SPOGServer;
	private Source4SPOGServer spogSourceServer;
	private Log4GatewayServer log4GatewayServer;
	private Policy4SPOGServer policy4SpogServer;
	private SPOGReportServer spogReportServer;

	private String csrReadOnlyUserName;
	private String csrReadOnlyPassword;
	private String csr_read_only_validToken;
	private String csr_read_only_user_id;

	private ExtentTest test;
	private String common_password = "Mclaren@2020";
	private String csrAdminUserName;
	private String csrAdminPassword;
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
	private String prefix_msp = "spog_RamyaNagepalli_msp";
	private String msp_org_name = prefix_msp + "_org";
	private String msp_org_email = msp_org_name + postfix_email;
	private String msp_org_first_name = msp_org_name + "_first_name";
	private String msp_org_last_name = msp_org_name + "_last_name";
	private String msp_user_name = prefix_msp + "_admin";
	private String msp_user_name_email = prefix_msp + "_admin" + postfix_email;
	private String msp_user_first_name = msp_user_name + "_first_name";
	private String msp_user_last_name = msp_user_name + "_last_name";
	private String organizationName1 = null;
	private String organizationEmail1 = null;
	private String site_version = "1.0.0";
	private String gateway_hostname = "Ramya";
	private String prefix_msp_account_admin = "spog_ramya_msp_account";
	private String msp_account_admin_email = prefix_msp_account_admin + postfix_email;
	private String msp_account_admin_first_name = prefix_msp_account_admin + "_first_name";
	private String msp_account_admin_last_name = prefix_msp_account_admin + "_last_name";
	private String msp_account_admin_validToken;
	String msp_account_admin_id;
	String msp_admin_name;
	String msp_account_admin_user_id;

	String cloud_rps_name = null;
	String cloud_rps_port = null;
	String cloud_rps_protocol = null;
	String cloud_rps_username = null;
	String cloud_rps_password = null;
	String csr_site_id = null;
	String csr_site_name = null;

	String linux_rps_name = null;
	String linux_rps_port = null;
	String linux_rps_protocol = null;
	String linux_rps_username = null;
	String linux_rps_password = null;

	HashMap<String, Object> cloud_dedupe_volume = new HashMap<String, Object>();
	ArrayList<HashMap<String, Object>> filtersHeadContent = new ArrayList<HashMap<String, Object>>();

	private String organizationFirstName1 = null;
	private String organizationLastName1 = null;
	/*
	 * private SQLServerDb bqdb1; public int Nooftest;
	 * 
	 * long creationTime; String buildnumber=null; String BQName=null; private
	 * testcasescount count1;
	 */

	int passedcases = 0;
	int failedcases = 0;
	int skippedcases = 0;

	int noofAccounts = 4;
	int noofaccounts = 2;
	private String filterStr = "";
	private String sortStr = "";
	int curr_page = 1;
	int page_size = 20;
	// private String runningMachine;
	private String msp_orgID;
	private String additionalURL;
	private EncodingBase64 base64;
	// private String buildVersion=null;
	// This function is
	LocalDate date = LocalDate.now();
	LocalDate yesterday = date.minusDays(1);
	LocalDate tomorrow = date.plusDays(1);
	String csr_token;
	ArrayList<String> State = new ArrayList<String>();
	ArrayList<String> recoveredResourceType = new ArrayList<String>();

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
		gatewayServer = new GatewayServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		spogJobServer = new SPOGJobServer(baseURI, port);
		log4SPOGServer = new Log4SPOGServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		policy4SpogServer = new Policy4SPOGServer(baseURI, port);
		spogSourceServer = new Source4SPOGServer(baseURI, port);
		log4GatewayServer = new Log4GatewayServer(baseURI, port);
		spogReportServer = new SPOGReportServer(baseURI, port);
		spogRecoveredResourceServer = new SPOGRecoveredResourceServer(baseURI, port);
		spogcloudRPSServer = new SPOGCloudRPSServer(baseURI, port);
		linux4spogServer = new Linux4SPOGServer(baseURI, port);
		rep = ExtentManager.getInstance("GetAuditTrailByUserTest", logFolder);
		this.csrAdminUserName = adminUserName;
		this.csrAdminPassword = adminPassword;
		Nooftest = 0;
		base64 = new EncodingBase64();
		bqdb1 = new SQLServerDb();
		String author = "Ramya.Nagepalli";
		count1 = new testcasescount();
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

	}

	/**
	 * AuditTracking for users
	 * 
	 * @return
	 */

	// Data providers For direct/msp Organization
	@DataProvider(name = "organizationAndUserInfo4")
	public final Object[][] getOrganizationAndUserInfo_msp_direct() {
		return new Object[][] { { msp_org_name, SpogConstants.MSP_ORG, msp_org_email, common_password,
				msp_org_first_name, msp_org_last_name, msp_user_name_email, common_password, msp_user_first_name,
				msp_user_last_name, SpogConstants.MSP_ADMIN, direct_org_name, SpogConstants.DIRECT_ORG,
				direct_org_email, common_password, direct_org_first_name, direct_org_last_name } };
	}

	// Data providers For direct Organization
	@DataProvider(name = "organizationAndUserInfo1")
	public final Object[][] getOrganizationAndUserInfoDirect() {
		return new Object[][] { { direct_org_name, SpogConstants.DIRECT_ORG, direct_org_email, common_password,
				direct_org_first_name, direct_org_last_name, direct_user_name_email, common_password,
				direct_user_first_name, direct_user_last_name, SpogConstants.DIRECT_ADMIN } };
	}

	// Data providers For direct Organization
	@DataProvider(name = "organizationAndUserInfo7")
	public final Object[][] getOrganizationAndUserInfoDirectInfo() {
		return new Object[][] { { direct_org_name, SpogConstants.DIRECT_ORG, direct_org_email, common_password,
				direct_org_first_name, direct_org_last_name, direct_user_name_email, common_password,
				direct_user_first_name, direct_user_last_name, SpogConstants.DIRECT_ADMIN, SourceType.machine,
				SourceProduct.cloud_direct, ProtectionStatus.protect, ConnectionStatus.online, OSMajor.mac.name(),
				"sql;", "Ramya_56vm2", UUID.randomUUID().toString(), "Ramya_1", "windows 2016", "128", "1.0.1", "2.1",
				"http://install", "cloud_hybrid_store", "f:\\dsp\\0", "f:\\ds\\1", "f:\\ds\\3", "f:\\ds\\2", 53, true,
				64, "256", true, "123", "3000", "2000", 0.5589, 0.4568, DestinationStatus.running.toString(),
				"destDeveloper" } };
	}

	@DataProvider(name = "getOrganizationMsp")
	public final Object[][] getOrganizationMsp() {
		return new Object[][] { { msp_org_name, SpogConstants.MSP_ORG, msp_org_email, common_password,
				msp_org_first_name, msp_org_last_name, msp_user_name_email, common_password, msp_user_first_name,
				msp_user_last_name, SpogConstants.MSP_ADMIN } };
	}

	// Data providers For Msp Organization
	@DataProvider(name = "organizationAndUserInfo")
	public final Object[][] getOrganizationAndUserInfo() {
		return new Object[][] { { msp_org_name, SpogConstants.MSP_ORG, msp_org_email, common_password,
				msp_org_first_name, msp_org_last_name, msp_user_name_email, common_password, msp_user_first_name,
				msp_user_last_name, SpogConstants.MSP_ADMIN, SourceType.machine, SourceProduct.cloud_direct,
				ProtectionStatus.protect, ConnectionStatus.online, OSMajor.windows.name(), "sql;exchange", "Ramya_vm2",
				UUID.randomUUID().toString(), "Ramya_agent1", "windows 2012", "64", "1.0.0", "2.0", "http://upgrade",
				"cloud_hybrid_store", "e:\\dsp\\0", "e:\\ds\\1", "e:\\ds\\3", "e:\\ds\\2", 53, true, 64, "256", true,
				"123", "3000", "2000", 0.5589, 0.4568, DestinationStatus.running.toString(), "destDedupder" } };
	}

	public Response createCloudAccountValid(String organizationID, String cloudAccountKey, String cloudAccountSecret,
			String cloudAccountName, String cloudAccountType, String orderID, String fulfillmentID) {
		String cloud_account_id = null;
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		if (!(null == orderID) && !("" == orderID) && !(orderID.equalsIgnoreCase("none"))) {
			orderID = orderID + prefix;
		}

		if (!(null == fulfillmentID) && !("" == fulfillmentID) && !(fulfillmentID.equalsIgnoreCase("none"))) {
			fulfillmentID = fulfillmentID + prefix;
		}
		if (cloudAccountKey != "" && cloudAccountSecret != "") {
			cloudAccountKey = RandomStringUtils.randomAlphanumeric(8) + cloudAccountKey;
			// cloudAccountSecret=RandomStringUtils.randomAlphanumeric(8)+cloudAccountSecret;
		}
		cloudAccountName = RandomStringUtils.randomAlphanumeric(8) + cloudAccountName;
		Response response = null;
		if (organizationID == null || organizationID == "" || organizationID.equalsIgnoreCase("none")) {
			response = spogServer.createCloudAccountWithCheck1(cloudAccountKey, cloudAccountSecret, cloudAccountName,
					cloudAccountType, organizationID, orderID, fulfillmentID, test);
		} else {
			response = spogServer.createCloudAccountWithCheck1(cloudAccountKey, cloudAccountSecret, cloudAccountName,
					cloudAccountType, organizationID, orderID, fulfillmentID, test);
		}
		return response;
	}

	@Test(dataProvider = "getOrganizationMsp")
	public void getAuditTrailForMspwithinvalid_id(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName, String organizationLastName,
			String userEmail, String userPassword, String FirstName, String LastName, String Role_Id) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Ramya Nagepalli");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;
		FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + FirstName;
		LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + LastName;
		userEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;
		ArrayList<HashMap> auditdetails = new ArrayList<HashMap>();
		HashMap<String, Object> auditEvents = new HashMap<String, Object>();
		Response response3 = null;

		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		String organization_id = spogServer.CreateOrganizationWithCheck(organizationName + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "The organization id is " + organization_id);
		// login with msp organization
		test.log(LogStatus.INFO, "Logging with msp organization user");
		spogServer.userLogin(organizationEmail, common_password);
		String validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "Get the logged in user ");
		Response response = spogServer.getLoggedInUser(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		spogServer.checkLoggedInUser(response, SpogConstants.SUCCESS_GET_PUT_DELETE,
				SpogMessageCode.SUCCESS_GET_PUT_DEL, organizationEmail, test);
		String user_id = response.then().extract().path("data.user_id");
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.LOGIN_USER, response,
				user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);

		// create sub-Organization event
		test.log(LogStatus.INFO, "creating the sub Organization under the msp organization");

		String organization_name = "spog_Ramya_msp_child";
		Response response2 = spogServer.createAccountWithCheck2(organization_id, organization_name, organization_id,
				test);
		String account_org_id = response2.then().extract().path("data.organization_id");
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.CREATE_SUB_ORGANIZATION,
				response2, account_org_id, organizationEmail, "user", organization_name, "organization");
		auditdetails.add(auditEvents);

		test.log(LogStatus.INFO, "Preparing the url :");
		String additionalUrl = spogServer.PrepareURL("", "create_ts;asc", 1, 20, test);
		String invalid_user_id = UUID.randomUUID().toString();

		test.log(LogStatus.INFO, "providing the invalid userId to get the details");
		response = spogServer.getAuditDetailsForUsers(validToken, invalid_user_id, additionalURL, test);
		System.out.println("The value of the response is :" + response.getBody().asString());
		test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
		spogServer.checkaudittraildata1(response, SpogConstants.RESOURCE_NOT_EXIST, auditdetails, 1, 20, "",
				"create_ts;asc", SpogMessageCode.USER_ID_DOESNOT_EXIST, test);

		// Deleting the msp organization
		test.log(LogStatus.INFO, "Login in as csr admin to delete the organization");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		spogServer.DeleteOrganizationWithCheck(organization_id, test);

	}

	@Test(dataProvider = "organizationAndUserInfo")
	public void getAuditTrailForMspUser(String organizationName, String organizationType, String organizationEmail,
			String organizationPwd, String organizationFirstName, String organizationLastName, String userEmail,
			String userPassword, String FirstName, String LastName, String Role_Id, SourceType sourceType,
			SourceProduct sourceProduct, ProtectionStatus protectionStatus, ConnectionStatus connectionStatus,
			String os_major, String applications, String vm_name, String hypervisor_id, String agent_name,
			String os_name, String os_architecture, String agent_current_version, String agent_upgrade_version,
			String agent_upgrade_link, String DestinationTyp, String data_store_folder, String data_destination,
			String index_destination, String hash_destination, int concurrent_active_node, boolean Is_deduplicated,
			int block_size, String hash_memory, boolean is_compressed, String encryption_password,
			String occupied_space, String stored_data, Double deduplication_rate, Double compression_rate,
			String destination_status, String destination_name) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Ramya Nagepalli");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;
		FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + FirstName;
		LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + LastName;
		userEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;
		ArrayList<HashMap> auditdetails = new ArrayList<HashMap>();
		HashMap<String, Object> auditEvents = new HashMap<String, Object>();
		ArrayList<HashMap> usercodes = new ArrayList<HashMap>();
		ArrayList<HashMap> codedetails = new ArrayList<HashMap>();
		ArrayList<HashMap> orgcodes = new ArrayList<HashMap>();
		ArrayList<HashMap> userresponse = new ArrayList<HashMap>();
		ArrayList<HashMap<String, Object>> expectedColumns = new ArrayList<>();
		Response response3 = null;

		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		String organization_id = spogServer.CreateOrganizationWithCheck(organizationName + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "The organization id is " + organization_id);
		// login with msp organization
		test.log(LogStatus.INFO, "Logging with msp organization user");
		spogServer.userLogin(organizationEmail, common_password);
		String validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "Get the logged in user ");
		Response response = spogServer.getLoggedInUser(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		spogServer.checkLoggedInUser(response, SpogConstants.SUCCESS_GET_PUT_DELETE,
				SpogMessageCode.SUCCESS_GET_PUT_DEL, organizationEmail, test);
		String user_id = response.then().extract().path("data.user_id");
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.LOGIN_USER, response,
				user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);
		// usercodes
		usercodes.add(auditEvents);

		// create sub-Organization event
		test.log(LogStatus.INFO, "creating the sub Organization under the msp organization");

		String organization_name = "spog_Ramya_msp_child";
		response = spogServer.createAccountWithCheck2(organization_id, organization_name, organization_id, test);
		String account_org_id = response.then().extract().path("data.organization_id");
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.CREATE_SUB_ORGANIZATION,
				response, account_org_id, organizationEmail, "user", organization_name, "organization");
		auditdetails.add(auditEvents);
		// codeDetails HashMap
		codedetails.add(auditEvents);
		// For OrgCodes
		orgcodes.add(auditEvents);

		// update Logged In organization
		test.log(LogStatus.INFO, "Updatating the Logged in user organization:");

		String newName1 = "spog_Ramya_msp";
		spogServer.userLogin("xiang_csr@arcserve.com", "Caworld_2017");
		csr_token = spogServer.getJWTToken();
		spogServer.setToken(csr_token);
		Response response4 = spogServer.UpdateLoggedInOrganization(newName1, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id,
				AuditCodeConstants.MODIFY_LOGIN_USER_ORGANIZATION, response4, organization_id, organizationEmail,
				"user", newName1, "organization");
		auditdetails.add(auditEvents);
		// For OrgCodes
		orgcodes.add(auditEvents);

		// modify sub organization
		test.log(LogStatus.INFO, "Moidifying the created sub organization");
		String accountName = "spog_Ramya_msp_child";
		Response response5 = spogServer.updateAccountWithCheck2(organization_id, account_org_id, accountName, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.MODIFY_SUB_ORGANIZATION,
				response5, account_org_id, organizationEmail, "user", accountName, "organization");
		auditdetails.add(auditEvents);
		// For OrgCodes
		orgcodes.add(auditEvents);

		// delete sub-organization:
		test.log(LogStatus.INFO, "Deleting the subOrganization");
		spogServer.deleteMSPAccountWithCheck(organization_id, account_org_id);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.DELETE_SUB_ORGANIZATION,
				response, account_org_id, organizationEmail, "user", accountName, "organization");
		auditdetails.add(auditEvents);

		// For OrgCodes
		orgcodes.add(auditEvents);
		// create user under msp organization
		test.log(LogStatus.INFO, "creating a msp user under the msp organization");
		Response response1 = spogServer.createUser(userEmail, userPassword, FirstName, LastName, Role_Id,
				organization_id, test);
		String user_id1 = spogServer.checkCreateUser(response1, SpogConstants.SUCCESS_POST, userEmail, FirstName,
				LastName, Role_Id, organization_id, "", test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.CREATE_USER, response1,
				user_id1, organizationEmail, "user", userEmail, "user");
		auditdetails.add(auditEvents);
		// userCodes
		usercodes.add(auditEvents);

		// modifying user
		test.log(LogStatus.INFO, "Modify the user");
		response = spogServer.updateUserById(user_id1, userEmail, userPassword, FirstName + "modi", LastName, Role_Id,
				organization_id, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.MODIFY_USER, response,
				user_id1, organizationEmail, "user", userEmail, "user");
		auditdetails.add(auditEvents);
		// userCodes
		usercodes.add(auditEvents);

		// modify-The login user change password
		test.log(LogStatus.INFO, "Modify login user password");
		String newPassword = "Mclaren@2013";

		spogServer.setToken(validToken);
		response = spogServer.changePasswordForLoggedInUser(organizationPwd, newPassword);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.MODIFY_LOGIN_USER_PASSWORD,
				response, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);
		// userCodes
		usercodes.add(auditEvents);

		// Modify the Logged in user by id
		String changepassword = "Mclaren@2016";
		test.log(LogStatus.INFO, "Modify the logged in user password by user id");
		response = spogServer.changePasswordForSpecifiedUser(user_id, newPassword, changepassword);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.MODIFY_USER_PASSWORD,
				response, user_id, userEmail, "user", userEmail, "user");
		auditdetails.add(auditEvents);
		// userCodes
		usercodes.add(auditEvents);

		// modify the login user
		test.log(LogStatus.INFO, "Modify the login user");
		response = spogServer.UpdateLoggedInUser(organizationEmail, changepassword, organizationFirstName,
				organizationLastName + "mod", Role_Id, organization_id);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.MODIFY_LOGIN_USER,
				response, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);
		// userCodes
		usercodes.add(auditEvents);

		// delete the user.
		test.log(LogStatus.INFO, "Delete the user");
		spogServer.DeleteUserById(user_id1, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.DELETE_USER, response,
				user_id1, organizationEmail, "user", userEmail, "user");
		auditdetails.add(auditEvents);
		// userCodes
		usercodes.add(auditEvents);

		test.log(LogStatus.INFO, "Create the log filter in org " + organizationType);
		log4SPOGServer.setToken(validToken);
		String sourceName = "sourceName";
		String filter_name = "filter_l";
		response = log4SPOGServer.createLogFilter(user_id, organization_id, filter_name, "last_24_hours", null, null,
				LogSeverityType.warning.toString(), "false", "none", "none", null, sourceName, "none", test);
		String filter_id = response.then().extract().path("data.filter_id");
		auditEvents = spogServer.storetheuserevents(AuditCodeConstants.CREATE_LOG_FILTER, organization_id, user_id,
				filter_id, response, organizationEmail, "user", filter_name, "log_filter");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		test.log(LogStatus.INFO, "update the log filter in org " + organizationType);
		/* filter_name="filter_lm"; */
		response = log4SPOGServer.updateLogFilter(user_id, filter_id, organization_id, filter_name, "last_24_hours",
				null, "backup_full", LogSeverityType.warning.toString(), "false", "none", "none", "none", "sourcename",
				"none", test);
		auditEvents = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_LOG_FILTER, organization_id, user_id,
				filter_id, response, organizationEmail, "user", filter_name, "log_filter");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		test.log(LogStatus.INFO, "delete the log filter in org " + organizationType);
		log4SPOGServer.deleteLogFilterforloggedinUser(filter_id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);
		auditEvents = spogServer.storetheuserevents(AuditCodeConstants.DELETE_LOG_FILTER, organization_id, user_id,
				filter_id, response1, organizationEmail, "user", filter_name, "log_filter");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		test.log(LogStatus.INFO, "Create user columns for specified user in organization : " + organizationType);
		HashMap<String, Object> temp = userSpogServer.composeUser_Column("50169479-cf16-412a-9a6d-3bf19a111a0b", "true",
				"true", "true", "1");
		expectedColumns.add(temp);
		response = userSpogServer.createUserColumnsByUserId(validToken, user_id, expectedColumns, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.CREATE_USER_COLUMN,
				response, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		test.log(LogStatus.INFO, "update user columns for specified user in organization : " + organizationType);
		response = userSpogServer.updateUserColumnsByUserId(validToken, user_id, expectedColumns, expectedColumns,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.MODIFY_USER_COLUMN,
				response, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		test.log(LogStatus.INFO, "delete user columns for logged in user in organization : " + organizationType);
		userSpogServer.deleteUserColumnsForLoggedInUserWithCheck(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.DELETE_USER_COLUMN,
				response3, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		test.log(LogStatus.INFO, "Create destination columns for logged in user in organization : " + organizationType);
		temp = userSpogServer.composeUser_Column("a6e15988-2919-4fd2-9b7d-00d2eddffde8", "false", "true", "true", "1");
		expectedColumns = new ArrayList<>();
		expectedColumns.add(temp);
		response = spogDestinationServer.createDestinationColumnsForLoggedInUser(validToken, expectedColumns, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id,
				AuditCodeConstants.CREATE_USER_DESTINATION_COLUMN, response, user_id, organizationEmail, "user",
				organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		test.log(LogStatus.INFO, "update destination columns for Specified user in organization : " + organizationType);
		response = spogDestinationServer.updateDestinationColumnsByUserId(user_id, validToken, expectedColumns,
				expectedColumns, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id,
				AuditCodeConstants.MODIFY_USER_DESTINATION_COLUMN, response, user_id, organizationEmail, "user",
				organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		test.log(LogStatus.INFO, "delete destination columns for Specified user in organization : " + organizationType);
		spogDestinationServer.deleteDestinationColumnsforSpecifiedUserwithCheck(user_id, validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id,
				AuditCodeConstants.DELETE_USER_DESTINATION_COLUMN, response3, user_id, organizationEmail, "user",
				organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		test.log(LogStatus.INFO, "Create job columns for Logged in user in organization : " + organizationType);
		temp = spogJobServer.composejob_Column("c6a4be7e-bf1c-4e88-a278-9c34f5f69117", "false", "true", "true", "1");
		expectedColumns = new ArrayList<>();
		expectedColumns.add(temp);
		response = spogJobServer.createJobColumnsForLoggedInUser(validToken, expectedColumns, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.CREATE_USER_JOB_COLUMN,
				response, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		test.log(LogStatus.INFO, "update job columns for Specified user in organization : " + organizationType);
		response = spogJobServer.updateJobColumnsForSpecifiedUser(user_id, validToken, expectedColumns, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.MODIFY_USER_JOB_COLUMN,
				response, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		test.log(LogStatus.INFO, "delete job columns for Specified user in organization : " + organizationType);
		spogJobServer.deleteJobcolumnsforspecifeduser(user_id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.DELETE_USER_JOB_COLUMN,
				response3, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		test.log(LogStatus.INFO, "Create log columns for Specified user in organization : " + organizationType);
		temp = log4SPOGServer.composelog_Column("55d3dbb0-83e7-45ae-879a-8f3a522d66c5", "true", "true", "true", "1");
		expectedColumns = new ArrayList<>();
		expectedColumns.add(temp);
		response = log4SPOGServer.createLogColumnsForSpecifiedUser(user_id, validToken, expectedColumns, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.CREATE_USER_LOG_COLUMN,
				response, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		test.log(LogStatus.INFO, "update log columns for Specified user in organization : " + organizationType);
		response = log4SPOGServer.updateLogColumnsForSpecifiedUser(user_id, validToken, expectedColumns, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.MODIFY_USER_LOG_COLUMN,
				response, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		test.log(LogStatus.INFO, "delete log columns for Specified user in organization : " + organizationType);
		log4SPOGServer.deleteLogColumnsforSpecifiedUserwithCheck(user_id, validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.DELETE_USER_LOG_COLUMN,
				response3, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		test.log(LogStatus.INFO, "Create source columns for logged in user in organization : " + organizationType);
		temp = log4SPOGServer.composelog_Column("07871b87-6ec5-44ae-a1bb-17abfb75a9f9", "false", "true", "true", "1");
		expectedColumns = new ArrayList<>();
		expectedColumns.add(temp);
		response = spogSourceServer.createLoggedInUserSourcesColumns(expectedColumns, validToken);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.CREATE_USER_SOURCE_COLUMN,
				response, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		test.log(LogStatus.INFO, "update source columns for logged in user in organization : " + organizationType);
		response = spogSourceServer.updateLoggedInUserSourcesColumns(expectedColumns, validToken);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.MODIFY_USER_SOURCE_COLUMN,
				response, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);
		usercodes.add(auditEvents);

		test.log(LogStatus.INFO, "delete source columns for logged in user in organization : " + organizationType);
		spogSourceServer.deleteLoggedInUserSourcesColumns(validToken);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.DELETE_USER_SOURCE_COLUMN,
				response3, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);
		usercodes.add(auditEvents);

		// user column already exists
		test.log(LogStatus.INFO, "Create hypervisor columns for Specified user in organization : " + organizationType);
		String column_id = "31dfe327-b9fe-432a-a119-24b584a85263";
		temp = log4SPOGServer.composelog_Column(column_id, "true", "true", "true", "1");
		expectedColumns = new ArrayList<>();
		expectedColumns.add(temp);
		response = spogSourceServer.createHypervisorColumnsForSpecifiedUser(user_id, expectedColumns, validToken);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id,
				AuditCodeConstants.CREATE_USER_HYPERVISOR_COLUMN, response, user_id, organizationEmail, "user",
				organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);
		usercodes.add(auditEvents);

		test.log(LogStatus.INFO, "update hypervisor columns for Specified user in organization : " + organizationType);
		response = spogSourceServer.updateHypervisorColumnsForSpecifiedUser(user_id, expectedColumns, validToken);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id,
				AuditCodeConstants.MODIFY_USER_HYPERVISOR_COLUMN, response, user_id, organizationEmail, "user",
				organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);
		usercodes.add(auditEvents);

		test.log(LogStatus.INFO, "delete hypervisor columns for Specified user in organization : " + organizationType);
		spogSourceServer.deleteHypervisorColumnsForSpecifiedUser(user_id, validToken);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id,
				AuditCodeConstants.DELETE_USER_HYPERVISOR_COLUMN, response3, user_id, organizationEmail, "user",
				organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);
		usercodes.add(auditEvents);

		// creating recovered resourcefilters
		test.log(LogStatus.INFO, "creating recovered resourcefilters");
		filter_name = spogServer.ReturnRandom("ramya");
		String policy_id = UUID.randomUUID().toString();
		response = spogRecoveredResourceServer.createSpecifiedUserRecoveredResourcesFilters(user_id, validToken,
				policy_id, State.get(1), os_major, recoveredResourceType.get(1), filter_name, "true", "", test);
		filter_id = response.then().extract().path("data.filter_id");
		test.log(LogStatus.INFO, "validating the response for creating recovered resourcefilters");
		spogRecoveredResourceServer.checkRecoveredResourceFilters(response, filter_id, policy_id, State.get(1),
				os_major, recoveredResourceType.get(1), filter_name, "true", organization_id, user_id,
				SpogConstants.SUCCESS_POST, SpogMessageCode.SUCCESS_POST, test, filtersHeadContent);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id,
				AuditCodeConstants.CREATE_RECOVEREDRESOURCE_FILTER, response, filter_id, organizationEmail, "user",
				filter_name, "recovered_resource_filter");
		auditdetails.add(auditEvents);

		// update recovered resourcefilters
		test.log(LogStatus.INFO, "update recovered resourcefilters");
		response = spogRecoveredResourceServer.updateSpecifiedUserRecoveredResourcesFilters(user_id, validToken,
				filter_id, policy_id, State.get(1), os_major, recoveredResourceType.get(1), filter_name, "true",
				SpogConstants.SUCCESS_GET_PUT_DELETE, "", test);
		test.log(LogStatus.INFO, "validating the response for get recovered resourcefilters");
		spogRecoveredResourceServer.checkRecoveredResourceFilters(response, filter_id, policy_id, State.get(1),
				os_major, recoveredResourceType.get(1), filter_name, "true", organization_id, user_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test, filtersHeadContent
				);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id,
				AuditCodeConstants.MODIFY_RECOVEREDRESOURCE_FILTER, response, filter_id, organizationEmail, "user",
				filter_name, "recovered_resource_filter");
		auditdetails.add(auditEvents);

		// delete recovered resourcefilters
		test.log(LogStatus.INFO, "delete recovered resourcefilters");
		response = spogRecoveredResourceServer.deleteSpecifiedUserRecoveredResourcesFiltersByFilterId(user_id,
				filter_id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		test.log(LogStatus.INFO, "validating the response for delete recovered resourcefilters");
		auditEvents = spogServer.composeJsonMap(user_id, organization_id,
				AuditCodeConstants.DELETE_RECOVEREDRESOURCE_FILTER, response, filter_id, organizationEmail, "user",
				filter_name, "recovered_resource_filter");
		auditdetails.add(auditEvents);

		// creating recovered resource columns
		test.log(LogStatus.INFO, "creating recovered resource columns");
		ArrayList<HashMap<String, Object>> expected_columns = new ArrayList<HashMap<String, Object>>();
		ArrayList<HashMap<String, Object>> defaultcolumnresponse = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> temp1 = new HashMap<String, Object>();
		temp1 = spogRecoveredResourceServer.composeRecoveredResource_Column("84129da7-d794-4cc5-bfe1-17daf6b911bb",
				"false", "false", "true", "1");
		expected_columns.add(temp1);
		response = spogRecoveredResourceServer.createRecoveredResourceColumnsForSpecifiedUser(user_id, validToken,
				expected_columns, test);
		test.log(LogStatus.INFO, "validating the response for creating recovered resource columns");
		auditEvents = spogServer.composeJsonMap(user_id, organization_id,
				AuditCodeConstants.CREATE_USER_RECOVEREDRESOURCE_COLUMN, response, user_id, organizationEmail, "user",
				organizationEmail, "user");
		auditdetails.add(auditEvents);

		// update recovered resource columns
		test.log(LogStatus.INFO, "update recovered resource columns");
		response = spogRecoveredResourceServer.updateRecoveredResourceColumnsByUserId(user_id, validToken,
				expected_columns, defaultcolumnresponse, SpogConstants.SUCCESS_GET_PUT_DELETE,
				SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		test.log(LogStatus.INFO, "validating the response for updating recovered resource columns");
		auditEvents = spogServer.composeJsonMap(user_id, organization_id,
				AuditCodeConstants.MODIFY_USER_RECOVEREDRESOURCE_COLUMN, response, user_id, organizationEmail, "user",
				organizationEmail, "user");
		auditdetails.add(auditEvents);

		// delete recovered resource columns
		test.log(LogStatus.INFO, "delete recovered resource columns");
		spogRecoveredResourceServer.deleteRecoveredResourceColumnsforSpecifiedUserwithCheck(user_id, validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		test.log(LogStatus.INFO, "validating the response for deleting recovered resource columns");
		auditEvents = spogServer.composeJsonMap(user_id, organization_id,
				AuditCodeConstants.DELETE_USER_RECOVEREDRESOURCE_COLUMN, response3, user_id, organizationEmail, "user",
				organizationEmail, "user");
		auditdetails.add(auditEvents);

		// create user policy filter
		test.log(LogStatus.INFO, "create user policy filter");
		userSpogServer.setToken(validToken);
		filter_name = "Finished succesfully";
		response = userSpogServer.createPolicyFilterForSpecificUserWithCheck_audit(user_id, filter_name, "policy_name",
				UUID.randomUUID().toString(), "finished", "failure", "true", test);
		filter_id = response.then().extract().path("data.filter_id");
		test.log(LogStatus.INFO, "validating the response for create user policy filter");
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.CREATE_POLICY_FILTER,
				response, filter_id, organizationEmail, "user", filter_name, "policy_filter");
		auditdetails.add(auditEvents);

		// update user policy filter
		test.log(LogStatus.INFO, "update user policy filter");
		userSpogServer.setToken(validToken);
		filter_name = "Finished succesfully";
		response = userSpogServer.updatePolicyFilterForSpecificUserWithCheck_audit(user_id, filter_id, filter_name,
				"policy_name", UUID.randomUUID().toString(), "finished", "failure", "true", test);
		test.log(LogStatus.INFO, "validating the response for create user policy filter");
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.MODIFY_POLICY_FILTER,
				response, filter_id, organizationEmail, "user", filter_name, "policy_filter");
		auditdetails.add(auditEvents);

		// update user policy filter
		test.log(LogStatus.INFO, "delete user policy filter");
		userSpogServer.setToken(validToken);
		userSpogServer.deleteSpecificPolicyFilterForSpecificUser(user_id, filter_id, test);
		test.log(LogStatus.INFO, "validating the response for create user policy filter");
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.DELETE_POLICY_FILTER,
				response3, filter_id, organizationEmail, "user", filter_name, "policy_filter");
		auditdetails.add(auditEvents);

		// create user policy columns
		test.log(LogStatus.INFO, "create user policy columns");
		policy4SpogServer.setToken(validToken);
		ArrayList<HashMap<String, Object>> expected_columns_1 = new ArrayList<HashMap<String, Object>>();
		expected_columns_1 = policy4SpogServer.getPolicyColumnArrayList("Name;true;1", validToken, test);
		response = policy4SpogServer.createUsersPolicyColumns(user_id, expected_columns_1, validToken);
		test.log(LogStatus.INFO, "validating the response for create user policy columns");
		policy4SpogServer.checkUsersPolicyColumns(response, expected_columns_1, validToken, SpogConstants.SUCCESS_POST,
				null);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.CREATE_USER_POLICY_COLUMN,
				response, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);

		// update user policy columns
		test.log(LogStatus.INFO, "update user policy columns");
		response = policy4SpogServer.updateUsersPolicyColumns(user_id, expected_columns_1, validToken);
		test.log(LogStatus.INFO, "validating the response for create user policy columns");
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.MODIFY_USER_POLICY_COLUMN,
				response, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);

		// delete user policy columns
		test.log(LogStatus.INFO, "delete user policy columns");
		policy4SpogServer.setToken(validToken);
		policy4SpogServer.deleteUsersPolicyColumnsWithCheck(user_id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE,
				null);
		test.log(LogStatus.INFO, "validating the response for create user policy columns");
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.DELETE_USER_POLICY_COLUMN,
				response3, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);

		// create report list filters for specified user
		test.log(LogStatus.INFO, "create report list filters for specified user");
		spogServer.setToken(validToken);
		// orgId for creating source_groups
		String orgId = null;
		String groupName = spogServer.ReturnRandom("ramya");
		orgId = spogServer.GetLoggedinUserOrganizationID();
		String source_group_ids = null, source_group_id = null;
		for (int i = 0; i < 1; i++) {
			source_group_id = spogServer.createGroupWithCheck2(validToken, orgId, groupName + i, groupName, test);
			if (i > 0) {
				source_group_ids = source_group_ids + "," + source_group_id;
			} else {
				source_group_ids = source_group_id;
			}
		}
		spogReportServer.setToken(validToken);
		filter_name = spogServer.ReturnRandom("filter");
		HashMap<String, Object> expectedfilter = spogReportServer.composeReportListFilterInfo(filter_name,
				"last_7_days", "all_sources", "", organization_id, "weekly", System.currentTimeMillis(),
				System.currentTimeMillis(), filter_name, true);
		response = spogReportServer.createReportListFiltersForSpecifiedUserWithCheck(validToken, user_id,
				organization_id, expectedfilter, SpogConstants.SUCCESS_POST, SpogMessageCode.SUCCESS_POST, test);
		filter_id = response.then().extract().path("data.filter_id");
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.CREATE_REPORT_LIST_FILTER,
				response, filter_id, organizationEmail, "user", filter_name, "report_list_filter");
		auditdetails.add(auditEvents);

		// update report list filters for specified user
		test.log(LogStatus.INFO, "update report list filters for specified user");
		/* filter_name= spogServer.ReturnRandom("filter"); */
		response = spogReportServer.updateReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id,
				organization_id, filter_id, expectedfilter, SpogConstants.SUCCESS_GET_PUT_DELETE,
				SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.MODIFY_REPORT_LIST_FILTER,
				response, filter_id, organizationEmail, "user", filter_name, "report_list_filter");
		auditdetails.add(auditEvents);

		// delete report list filters for specified user
		test.log(LogStatus.INFO, "delete report list filters for specified user");
		spogReportServer.deleteReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.DELETE_REPORT_LIST_FILTER,
				response3, filter_id, organizationEmail, "user", filter_name, "report_list_filter");
		auditdetails.add(auditEvents);

		// create report filter
		test.log(LogStatus.INFO, "create  report filter");
		HashMap<String, Object> reportfilterInfo = null;
		test.log(LogStatus.INFO, "compose report filter");
		filter_name = spogServer.ReturnRandom("filter");
		reportfilterInfo = spogReportServer.composeReportFilterInfo(sourceName, destination_name, policy_id, "",
				source_group_id, "", "today", System.currentTimeMillis(), System.currentTimeMillis(), "backup_jobs", "",
				"backup", "", "", filter_name, true, "backup_jobs");
		response = spogReportServer.createReportFilterForSpecifiedUserWithCheck_audit(user_id, organization_id,
				validToken, reportfilterInfo, SpogConstants.SUCCESS_POST, null, test);
		filter_id = response.then().extract().path("data.filter_id");
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.CREATE_REPORT_FILTER,
				response, filter_id, organizationEmail, "user", filter_name, "report_filter");
		auditdetails.add(auditEvents);

		// update report filter
		test.log(LogStatus.INFO, "update  report filter");
		reportfilterInfo = spogReportServer.composeReportFilterInfo(sourceName, destination_name, policy_id, "",
				source_group_id, "", "today", System.currentTimeMillis(), System.currentTimeMillis(), "backup_jobs", "",
				"backup", "", "", filter_name, true, "backup_jobs");
		response = spogReportServer.updateReportFilterForSpecifiedUserWithCheck(user_id, filter_id, organization_id,
				validToken, reportfilterInfo, reportfilterInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.MODIFY_REPORT_FILTER,
				response, filter_id, organizationEmail, "user", filter_name, "report_filter");
		auditdetails.add(auditEvents);

		// delete report filter
		test.log(LogStatus.INFO, "delete report filter");
		response = spogReportServer.deleteReportFiltersForSpecifiedUser(validToken, user_id, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.DELETE_REPORT_FILTER,
				response3, filter_id, organizationEmail, "user", filter_name, "report_filter");
		auditdetails.add(auditEvents);

		// create backup job report columns
		test.log(LogStatus.INFO, "create backup job report columns");
		ArrayList<HashMap<String, Object>> expected_columns_2 = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> temp2 = new HashMap<>();
		for (int j = 0; j < 1; j++) {
			temp2 = spogReportServer.composeBackUpJobReportsColumns("b9004719-1897-4f09-abd2-7a9fb6edb768", "true",
					"3");

			expected_columns_2.add(temp2);
		}

		response = spogReportServer.createBackUpJobReportsColumnsForSpecifiedUser(validToken, user_id,
				expected_columns_2, SpogConstants.SUCCESS_POST, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id,
				AuditCodeConstants.CREATE_USER_BACKUPJOBREPORT_COLUMN, response, user_id, organizationEmail, "user",
				organizationEmail, "user");
		auditdetails.add(auditEvents);
		expected_columns_2.clear();

		// update backup job report columns
		test.log(LogStatus.INFO, "update backup job report columns");
		for (int j = 0; j < 1; j++) {
			temp2 = spogReportServer.composeBackUpJobReportsColumns("b9004719-1897-4f09-abd2-7a9fb6edb768", "true",
					"4");

			expected_columns_2.add(temp2);
		}

		response = spogReportServer.updateBackUpJobReportsColumnsForSpecifiedUser(validToken, user_id,
				expected_columns_2, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id,
				AuditCodeConstants.MODIFY_USER_BACKUPJOBREPORT_COLUMN, response, user_id, organizationEmail, "user",
				organizationEmail, "user");
		auditdetails.add(auditEvents);

		// delete BackUpJobReportsColumnsForSpecifiedUser
		test.log(LogStatus.INFO, "delete BackUpJobReportsColumnsForSpecifiedUser ");
		spogReportServer.deleteBackUpJobReportsColumnsForSpecifiedUser(validToken, user_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id,
				AuditCodeConstants.DELETE_USER_BACKUPJOBREPORT_COLUMN, response3, user_id, organizationEmail, "user",
				organizationEmail, "user");
		auditdetails.add(auditEvents);

		// create restore job report columns
		test.log(LogStatus.INFO, "create restore job report columns");
		spogReportServer.setToken(validToken);
		expected_columns_2.clear();
		for (int j = 0; j < 1; j++) {
			temp2 = spogReportServer.composeBackUpJobReportsColumns("07871b87-6ec5-44ae-a1bb-17abfb75a9f9", "true",
					"3");

			expected_columns_2.add(temp2);
		}

		response = spogReportServer.createRestoreJobReportsColumnsForSpecifiedUser(user_id, validToken,
				expected_columns_2, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id,
				AuditCodeConstants.CREATE_USER_RESTOREJOBREPORT_COLUMN, response, user_id, organizationEmail, "user",
				organizationEmail, "user");
		auditdetails.add(auditEvents);
		expected_columns_2.clear();

		// update restore job report columns
		test.log(LogStatus.INFO, "update restore job report columns");
		for (int j = 0; j < 1; j++) {
			temp2 = spogReportServer.composeRestoreJobReports_Column("07871b87-6ec5-44ae-a1bb-17abfb75a9f9", "true",
					"false", "true", "4");

			expected_columns_2.add(temp2);
		}

		response = spogReportServer.updateRestoreJobReportsColumnsByUserId(user_id, validToken, expected_columns_2,
				expected_columns_2, SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id,
				AuditCodeConstants.MODIFY_USER_RESTOREJOBREPORT_COLUMN, response, user_id, organizationEmail, "user",
				organizationEmail, "user");
		auditdetails.add(auditEvents);

		// delete RestoreJobReportsColumnsForSpecifiedUser
		test.log(LogStatus.INFO, "delete RestoreJobReportsColumnsForSpecifiedUser ");
		spogReportServer.deleteRestoreJobReportsColumnsforSpecifiedUserwithCheck(user_id, validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id,
				AuditCodeConstants.DELETE_USER_RESTOREJOBREPORT_COLUMN, response3, user_id, organizationEmail, "user",
				organizationEmail, "user");
		auditdetails.add(auditEvents);

		// create report schedule
		test.log(LogStatus.INFO, "create report schedule ");
		HashMap<String, Object> scheduleInfo = new HashMap<String, Object>();
		scheduleInfo = spogReportServer.composeReportScheduleInfo("report_name", "backup_jobs", "last_7_days",
				System.currentTimeMillis(), System.currentTimeMillis(), "daily", "", organization_id,
				"a@gmail.com,b@gmail.com", "0 0 19 ? * *", "all_sources");
		response = spogReportServer.createReportScheduleWithCheck_audit(validToken, scheduleInfo,
				SpogConstants.SUCCESS_POST, null, test);
		String schedule_id = response.then().extract().path("data.schedule_id");
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.CREATE_REPORT_SCHEDULE,
				response, user_id, organizationEmail, "user", schedule_id, "report_schedule");
		auditdetails.add(auditEvents);

		// modify report schedule
		test.log(LogStatus.INFO, "modify report schedule ");
		scheduleInfo.clear();
		scheduleInfo = spogReportServer.composeReportScheduleInfo("report_name", "backup_jobs", "last_7_days",
				System.currentTimeMillis(), System.currentTimeMillis(), "daily", "", organization_id,
				"a@gmail.com,b@gmail.com", "0 0 19 ? * *", "all_sources");
		response = spogReportServer.updateReportScheduleByIdWithCheck(schedule_id, validToken, scheduleInfo,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.MODIFY_REPORT_SCHEDULE,
				response, user_id, organizationEmail, "user", schedule_id, "report_schedule");
		auditdetails.add(auditEvents);

		// delete report schedule
		test.log(LogStatus.INFO, "modify report schedule ");
		spogReportServer.deleteReportScheduleByIdWithCheck(schedule_id, validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.DELETE_REPORT_SCHEDULE,
				response3, user_id, organizationEmail, "user", schedule_id, "report_schedule");
		auditdetails.add(auditEvents);

		for (int i = 0; i < 2; i++) {
			if (i == 1) {
				validToken = csr_read_only_validToken;
			}
			// check backupJobReportsColumnsForSpecifiedUser
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			String filter = "code_id;=;2025|2026|2027";
			String additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,filter
			// ,
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			// spogServer.checkaudittraildata1(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,
			// filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// check report schedule
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;4014|4015|4016";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,filter
			// ,
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			// spogServer.checkaudittraildata1(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,
			// filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// check RestoreJobReportsColumnsForSpecifiedUser
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;2028|2029|2030";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,filter
			// ,
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// check report filters
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;1028|1029|1030";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,filter
			// ,
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			/*
			 * //delete the site test.log(LogStatus.INFO, "Delete the site "
			 * +site_id); response = spogServer.deleteSite(site_id, validToken);
			 * auditEvents=spogServer.composeJsonMap(user_id,organization_id,
			 * AuditCodeConstants.DELETE_SITE,response3,site_id,
			 * organizationEmail, "user", newname, "site");
			 * auditdetails.add(auditEvents); codedetails.add(auditEvents);
			 * //For userCodes usercodes.add(auditEvents);
			 */

			// check list report filters
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;1025|1026|1027";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,filter
			// ,
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// Filtering based on code_id and ascending order sorting
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			additionalUrl = spogServer.PrepareURL("code_id;=;105", "create_ts;asc", 1, 20, test);
			filter = "code_id;=;105";
			userresponse = compose_expected_response(filter, codedetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,
					"code_id", "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// sorting based on Descending order
			test.log(LogStatus.INFO, "Preparing the URL for sorting based on descending order ");
			additionalUrl = spogServer.PrepareURL("", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + user_id);
			Response response6 = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			// spogServer.checkaudittraildata1(response6,SpogConstants.SUCCESS_GET_PUT_DELETE,auditdetails
			// , 1, 20, "",
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// Filtering based on code_id and ascending order sorting
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;1019|1020|1021";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,filter
			// ,
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// Filtering based on code_id and ascending order sorting
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;2019|2020|2021";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,filter
			// ,
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// Filtering based on code_id and ascending order sorting
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;1022|1023|1024";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,filter
			// ,
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// Filtering based on code_id and ascending order sorting
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;2022|2023|2024";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,filter
			// ,
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// Filtering based on code_id and ascending order sorting
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;1004|1005|1006";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,filter
			// ,
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// sorting based on Descending order
			test.log(LogStatus.INFO, "Preparing the URL for sorting based on descending order ");
			filter = "code_id;=;1001|1002|1003";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20,filter,
			// "create_ts;desc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// Sorting based on ascending order

			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;1007|1008|1009";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// sorting and pagination Ascending order
			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;1010|1011|1012";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			// sorting and pagination Ascending order
			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;1013|1014|1015";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// sorting and pagination Descending order
			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;1016|1017|1018";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// sorting and pagination Descending order
			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;2001|2002|2003";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// sorting and pagination Descending order
			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;2004|2005|2006";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;2007|2008|2009";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// sorting and pagination Ascending order
			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;2010|2011|2012";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// sorting and pagination Ascending order
			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;2013|2014|2015";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// sorting and pagination Descending order
			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;2016|2017|2018";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// Array of code id and filtering the url on usercodes

			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;4006|4007";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;4008|4009";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			// Sorting based on ascending order
			test.log(LogStatus.INFO, "Preparing the URL for Filtering  ");
			filter = "code_id;in;1|2|3";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by userid " + user_id);
			userresponse = compose_expected_response(filter, usercodes);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			test.log(LogStatus.INFO, "Preparing the URL for Filtering  ");
			filter = "code_id;in;4|5";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by userid " + user_id);
			userresponse = compose_expected_response(filter, usercodes);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			test.log(LogStatus.INFO, "Preparing the URL for Filtering  ");
			filter = "code_id;in;6|7";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;desc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by userid " + user_id);
			userresponse = compose_expected_response(filter, usercodes);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;desc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			// Array of codes filtering based on descending order For
			// organizations
			test.log(LogStatus.INFO, "Preparing the URL for Filtering  ");
			filter = "code_id;in;108";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;desc", 1, 20, test);
			userresponse = compose_expected_response(filter, orgcodes);
			test.log(LogStatus.INFO, "Get the audit trail by userid " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;desc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			// Array of codes filtering based on descending order For
			// organizations
			test.log(LogStatus.INFO, "Preparing the URL for Filtering  ");
			filter = "code_id;in;105";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, orgcodes);
			test.log(LogStatus.INFO, "Get the audit trail by userid " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			// Array of codes filtering based on descending order For
			// organizations
			test.log(LogStatus.INFO, "Preparing the URL for Filtering  ");
			filter = "code_id;in;107";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;desc", 1, 20, test);
			userresponse = compose_expected_response(filter, orgcodes);
			test.log(LogStatus.INFO, "Get the audit trail by userid " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;desc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			// Array of codes filtering based on descending order For
			// organizations
			test.log(LogStatus.INFO, "Preparing the URL for Filtering  ");
			filter = "code_id;in;106";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, orgcodes);
			test.log(LogStatus.INFO, "Get the audit trail by userid " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

		}

		// Deleting the msp organization
		test.log(LogStatus.INFO, "Login in as csr admin to delete the organization");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		spogServer.DeleteOrganizationWithCheck(organization_id, test);

	}

	@Test(dataProvider = "organizationAndUserInfo")
	public void getAuditTrailForMspUser_valid(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName, String organizationLastName,
			String userEmail, String userPassword, String FirstName, String LastName, String Role_Id,
			SourceType sourceType, SourceProduct sourceProduct, ProtectionStatus protectionStatus,
			ConnectionStatus connectionStatus, String os_major, String applications, String vm_name,
			String hypervisor_id, String agent_name, String os_name, String os_architecture,
			String agent_current_version, String agent_upgrade_version, String agent_upgrade_link,
			String destinationtype, String data_store_folder, String data_destination, String index_destination,
			String hash_destination, int concurrent_active_node, boolean Is_deduplicated, int block_size,
			String hash_memory, boolean is_compressed, String encryption_password, String occupied_space,
			String stored_data, Double deduplication_rate, Double compression_rate, String destination_status,
			String destination_name) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Ramya Nagepalli");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;
		FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + FirstName;
		LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + LastName;
		userEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;
		ArrayList<HashMap> auditdetails = new ArrayList<HashMap>();
		HashMap<String, Object> auditEvents = new HashMap<String, Object>();
		ArrayList<HashMap> usercodes = new ArrayList<HashMap>();
		ArrayList<HashMap> userresponse = new ArrayList<HashMap>();
		ArrayList<HashMap> codedetails = new ArrayList<HashMap>();
		ArrayList<HashMap> orgcodes = new ArrayList<HashMap>();
		ArrayList<HashMap<String, Object>> expectedColumns = new ArrayList<>();
		HashMap<String, Object> temp = new HashMap<>();
		Response response3 = null;
		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		String organization_id = spogServer.CreateOrganizationWithCheck(organizationName + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "The organization id is " + organization_id);
		// login with msp organization
		test.log(LogStatus.INFO, "Logging with msp organization user");
		spogServer.userLogin(organizationEmail, common_password);
		String validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "Get the logged in user ");
		Response response = spogServer.getLoggedInUser(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		spogServer.checkLoggedInUser(response, SpogConstants.SUCCESS_GET_PUT_DELETE,
				SpogMessageCode.SUCCESS_GET_PUT_DEL, organizationEmail, test);
		String user_id = response.then().extract().path("data.user_id");
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.LOGIN_USER, response,
				user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);
		// usercodes
		usercodes.add(auditEvents);

		// create sub-Organization event
		test.log(LogStatus.INFO, "creating the sub Organization under the msp organization");

		String organization_name = "spog_Ramya_msp_child";
		response = spogServer.createAccountWithCheck2(organization_id, organization_name, organization_id, test);
		String account_org_id = response.then().extract().path("data.organization_id");
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.CREATE_SUB_ORGANIZATION,
				response, account_org_id, organizationEmail, "user", organization_name, "organization");
		auditdetails.add(auditEvents);
		// codeDetails HashMap
		codedetails.add(auditEvents);
		// For OrgCodes
		orgcodes.add(auditEvents);

		// update Logged In organization
		test.log(LogStatus.INFO, "Updatating the Logged in user organization:");
		spogServer.userLogin("xiang_csr@arcserve.com", "Caworld_2017");
		csr_token = spogServer.getJWTToken();
		spogServer.setToken(csr_token);
		spogServer.setToken(csr_token);
		String newName1 = "spog_Ramya_msp";
		response = spogServer.UpdateLoggedInOrganization(newName1, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id,
				AuditCodeConstants.MODIFY_LOGIN_USER_ORGANIZATION, response, organization_id, organizationEmail, "user",
				newName1, "organization");
		auditdetails.add(auditEvents);
		// For OrgCodes
		orgcodes.add(auditEvents);

		// modify sub organization
		test.log(LogStatus.INFO, "Moidifying the created sub organization");
		String accountName = "spog_Ramya_msp_child";
		response = spogServer.updateAccountWithCheck2(organization_id, account_org_id, accountName, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.MODIFY_SUB_ORGANIZATION,
				response, account_org_id, organizationEmail, "user", accountName, "organization");
		auditdetails.add(auditEvents);
		// For OrgCodes
		orgcodes.add(auditEvents);

		// delete sub-organization:
		test.log(LogStatus.INFO, "Deleting the subOrganization");
		spogServer.deleteMSPAccountWithCheck(organization_id, account_org_id);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.DELETE_SUB_ORGANIZATION,
				response3, account_org_id, organizationEmail, "user", accountName, "organization");
		auditdetails.add(auditEvents);

		// For OrgCodes
		orgcodes.add(auditEvents);

		// create user under msp organization
		test.log(LogStatus.INFO, "creating a msp user under the msp organization");
		Response response1 = spogServer.createUser(userEmail, userPassword, FirstName, LastName, Role_Id,
				organization_id, test);
		String user_id1 = spogServer.checkCreateUser(response1, SpogConstants.SUCCESS_POST, userEmail, FirstName,
				LastName, Role_Id, organization_id, "", test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.CREATE_USER, response1,
				user_id1, organizationEmail, "user", userEmail, "user");
		auditdetails.add(auditEvents);
		// userCodes
		usercodes.add(auditEvents);

		// modifying user
		test.log(LogStatus.INFO, "Modify the user");
		response = spogServer.updateUserById(user_id1, userEmail, userPassword, FirstName + "modi", LastName, Role_Id,
				organization_id, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.MODIFY_USER, response,
				user_id1, organizationEmail, "user", userEmail, "user");
		auditdetails.add(auditEvents);
		// userCodes
		usercodes.add(auditEvents);

		// modify-The login user change password
		test.log(LogStatus.INFO, "Modify login user password");
		String newPassword = "Mclaren@2013";
		spogServer.setToken(validToken);
		response = spogServer.changePasswordForLoggedInUser(organizationPwd, newPassword);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.MODIFY_LOGIN_USER_PASSWORD,
				response, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);
		usercodes.add(auditEvents);

		// Modify the Logged in user by id
		String changepassword = "Mclaren@2016";
		spogServer.setToken(validToken);
		test.log(LogStatus.INFO, "Modify the logged in user password by user id");
		response = spogServer.changePasswordForSpecifiedUser(user_id, newPassword, changepassword);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.MODIFY_USER_PASSWORD,
				response, user_id, userEmail, "user", userEmail, "user");
		auditdetails.add(auditEvents);
		// userCodes
		usercodes.add(auditEvents);

		// modify the login user
		test.log(LogStatus.INFO, "Modify the login user");
		response = spogServer.UpdateLoggedInUser(organizationEmail, changepassword, organizationFirstName,
				organizationLastName + "mod", Role_Id, organization_id);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.MODIFY_LOGIN_USER,
				response, user_id, userEmail, "user", userEmail, "user");
		auditdetails.add(auditEvents);
		// userCodes
		usercodes.add(auditEvents);

		// delete the user.
		test.log(LogStatus.INFO, "Delete the user");
		spogServer.DeleteUserById(user_id1, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.DELETE_USER, response,
				user_id1, organizationEmail, "user", userEmail, "user");

		auditdetails.add(auditEvents);
		// userCodes
		usercodes.add(auditEvents);

		for (int i = 0; i < 2; i++) {
			if (i == 1) {
				validToken = csr_read_only_validToken;
			}
			// create policy Audits
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			String filter = "code_id;=;4010";
			String additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,filter
			// ,
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// create policy Audits
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;4011|4012";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,filter
			// ,
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// Filtering based on code_id and ascending order sorting
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;1019|1020|1021";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,filter
			// ,
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// Filtering based on code_id and ascending order sorting
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;2019|2020|2021";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,filter
			// ,
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// Filtering based on code_id and ascending order sorting
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;1022|1023|1024";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,filter
			// ,
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// Filtering based on code_id and ascending order sorting
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;2022|2023|2024";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,filter
			// ,
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// Filtering based on code_id and ascending order sorting
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;1004|1005|1006";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,filter
			// ,
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// sorting based on Descending order
			test.log(LogStatus.INFO, "Preparing the URL for sorting based on descending order ");
			filter = "code_id;=;1001|1002|1003";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20,filter,
			// "create_ts;desc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// Sorting based on ascending order

			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;1007|1008|1009";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// sorting and pagination Ascending order
			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;1010|1011|1012";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			// sorting and pagination Ascending order
			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;1013|1014|1015";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// sorting and pagination Descending order
			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;1016|1017|1018";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// sorting and pagination Descending order
			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;2001|2002|2003";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// sorting and pagination Descending order
			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;2004|2005|2006";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;2007|2008|2009";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// sorting and pagination Ascending order
			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;2010|2011|2012";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// sorting and pagination Ascending order
			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;2013|2014|2015";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// sorting and pagination Descending order
			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;2016|2017|2018";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// Array of code id and filtering the url on usercodes

			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;4006|4007";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;4008|4009";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

		}
		// Deleting the msp organization
		test.log(LogStatus.INFO, "Login in as csr admin to delete the organization");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);

		spogServer.DeleteOrganizationWithCheck(organization_id, test);

	}

	// to filter the expected responses from composed hash
	@SuppressWarnings({ "null", "unchecked", "rawtypes" })
	private ArrayList<HashMap> compose_expected_response(String filter, ArrayList<HashMap> auditdet) {
		// TODO Auto-generated method stub
		ArrayList<HashMap> expected = new ArrayList<HashMap>();

		@SuppressWarnings("unused")
		HashMap<String, Object> temp = new HashMap<String, Object>();

		String[] filterstr = filter.split(";");

		String filt = filterstr[2];
		String[] code_id = filt.split(Pattern.quote("|"));

		for (int j = 0; j < code_id.length; j++) {
			for (int i = 0; i < auditdet.size(); i++) {
				String code_id1 = (String) auditdet.get(i).get("code_id");
				test.log(LogStatus.INFO, "the value of code_id" + code_id1);
				if (code_id[j].equals(code_id1)) {
					expected.add(auditdet.get(i));
				} else {
					continue;
				}

			}
		}

		return expected;
	}

	private ArrayList<HashMap> compose(ArrayList<HashMap> auditdetails, Object log) {
		// TODO Auto-generated method stub
		return null;
	}

	@DataProvider(name = "organizationAndUserInfo2")
	public final Object[][] getOrganization_childAndUserInfo() {
		return new Object[][] { { msp_org_name, SpogConstants.MSP_ORG, msp_org_email, common_password,
				msp_org_first_name, msp_org_last_name, direct_user_name_email, common_password, direct_user_first_name,
				direct_user_last_name, SpogConstants.DIRECT_ADMIN } };
	}

	@Test(dataProvider = "organizationAndUserInfo2")
	public void getAuditTrailForSubOrgUser_1(String organizationName, String organizationType, String organizationEmail,
			String organizationPwd, String organizationFirstName, String organizationLastName, String userEmail,
			String userPassword, String FirstName, String LastName, String Role_Id) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Ramya Nagepalli");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		String msporganizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;
		FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + FirstName;
		LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + LastName;
		userEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;
		ArrayList<HashMap> auditdetails = new ArrayList<HashMap>();
		HashMap<String, Object> auditEvents = new HashMap<String, Object>();
		ArrayList<HashMap> auditdetailsForSuborg = new ArrayList<HashMap>();
		HashMap<String, Object> auditEventsForSuborg = new HashMap<String, Object>();
		ArrayList<HashMap> usercodes = new ArrayList<HashMap>();
		ArrayList<HashMap> userresponse = new ArrayList<HashMap>();
		ArrayList<HashMap> codedetails = new ArrayList<HashMap>();
		ArrayList<HashMap> orgcodes = new ArrayList<HashMap>();
		ArrayList<HashMap<String, Object>> expectedColumns = new ArrayList<>();
		HashMap<String, Object> temp = new HashMap<>();
		Response response3 = null;

		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		String organization_id = spogServer.CreateOrganizationByEnrollWithCheck(organizationName + org_model_prefix,
				organizationType, msporganizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "The organization id is " + organization_id);
		// login with msp organization
		test.log(LogStatus.INFO, "Logging with msp organization user");
		spogServer.userLogin(msporganizationEmail, common_password);
		String validToken = spogServer.getJWTToken();
		Response response = spogServer.getCloudAccounts(validToken, "", test);
		String cloud_account_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");

		test.log(LogStatus.INFO, "Get the logged in user ");
		response = spogServer.getLoggedInUser(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		spogServer.checkLoggedInUser(response, SpogConstants.SUCCESS_GET_PUT_DELETE,
				SpogMessageCode.SUCCESS_GET_PUT_DEL, msporganizationEmail, test);
		String msp_user_id = response.then().extract().path("data.user_id");
		auditEvents = spogServer.composeJsonMap(msp_user_id, organization_id, AuditCodeConstants.LOGIN_USER, response,
				msp_user_id, msporganizationEmail, "user", msporganizationEmail, "user");
		auditdetails.add(auditEvents);

		// create sub-Organization event
		test.log(LogStatus.INFO, "creating the sub Organization under the msp organization");
		String organization_name = "spog_Ramya_msp_child";

		response = spogServer.createAccountWithCheck2(organization_id, organization_name, organization_id, test);

		String account_org_id = response.then().extract().path("data.organization_id");
		auditEvents = spogServer.composeJsonMap(msp_user_id, organization_id,
				AuditCodeConstants.CREATE_SUB_ORGANIZATION, response, account_org_id, msporganizationEmail, "user",
				organization_name, "organization");
		auditdetails.add(auditEvents);

		// create a user under sub org
		spogServer.setToken(validToken);
		spogServer.userLogin("xiang_csr@arcserve.com", "Caworld_2017");
		csr_token = spogServer.getJWTToken();
		spogServer.setToken(csr_token);

		test.log(LogStatus.INFO, "creating a user under the sub Organization");
		String prefix = spogServer.ReturnRandom("spogqa_account");
		organization_name = spogServer.ReturnRandom("spogqa_account");
		String user_id = spogServer.createUserAndCheck(organizationEmail, organizationPwd, organizationFirstName,
				organizationLastName, SpogConstants.DIRECT_ADMIN, account_org_id, test);
		test.log(LogStatus.INFO, "Logging with sub organization user");

		spogServer.userLogin(organizationEmail, organizationPwd);
		String sub_user_token = spogServer.getJWTToken();

		// update Logged In organization
		test.log(LogStatus.INFO, "Updatating the Logged in user organization:");
		spogServer.setToken(csr_token);
		String newName1 = "spog_Ramya_sub";
		response = spogServer.UpdateLoggedInOrganization(newName1, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id,
				AuditCodeConstants.MODIFY_LOGIN_USER_ORGANIZATION, response, organization_id, organizationEmail, "user",
				newName1, "organization");
		auditdetails.add(auditEvents);

		// modify sub organization
		test.log(LogStatus.INFO, "Moidifying the created sub organization");
		String accountName = "spog_Ramya_msp_child";
		spogServer.setToken(validToken);
		response = spogServer.updateAccountWithCheck2(organization_id, account_org_id, accountName, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.MODIFY_SUB_ORGANIZATION,
				response, account_org_id, organizationEmail, "user", accountName, "organization");
		auditdetails.add(auditEvents);

		// create a direct user under the sub organization
		test.log(LogStatus.INFO, "creating a direct user under the msp organization");
		response = spogServer.createUser(userEmail, userPassword, FirstName, LastName, Role_Id, account_org_id, test);
		String user_id1 = spogServer.checkCreateUser(response, SpogConstants.SUCCESS_POST, userEmail, FirstName,
				LastName, Role_Id, account_org_id, "", test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.CREATE_USER, response,
				user_id1, organizationEmail, "user", userEmail, "user");
		auditdetails.add(auditEvents);

		// Login with the subOrg direct user
		test.log(LogStatus.INFO, "Logging with the sub organization user");
		spogServer.userLogin(userEmail, common_password);
		String userToken = spogServer.getJWTToken();
		auditEventsForSuborg = spogServer.composeJsonMap(user_id1, account_org_id, AuditCodeConstants.LOGIN_USER,
				response, user_id1, organizationEmail, "user", organizationEmail, "user");
		auditdetailsForSuborg.add(auditEventsForSuborg);

		// creating a site for direct_admin user
		test.log(LogStatus.INFO, "create a site in spog for mentioned details");
		String siteName = "msp_admin_user_site";
		String siteType = GatewayServer.siteType.gateway.toString();
		response = spogServer.createSite(siteName, siteType, account_org_id, userToken, test);
		Map<String, String> sitecreateResMap = new HashMap<>();
		// validating the response for creation of the site
		sitecreateResMap = spogServer.checkCreateSite(response, SpogConstants.SUCCESS_POST, siteName, siteType,
				account_org_id, user_id1, "", test);
		String registration_basecode = sitecreateResMap.get("registration_basecode");
		test.log(LogStatus.INFO, "The encoded message is:" + registration_basecode);
		String site_id = sitecreateResMap.get("site_id");
		test.log(LogStatus.INFO, "The generated site_id is :" + site_id);
		auditEventsForSuborg = spogServer.composeJsonMap(user_id1, account_org_id, AuditCodeConstants.CREATE_SITE,
				response, site_id, organizationEmail, "user", siteName, "site");
		auditdetailsForSuborg.add(auditEventsForSuborg);

		// Register a site for msp user organization_id
		String site_registration_key = null;
		// decoding the encoded base64 key
		site_registration_key = base64.DecodeSiteRegistrationKey(registration_basecode);
		test.log(LogStatus.INFO, "After decoding the site_registration_key : " + site_registration_key);
		String gateway_id = UUID.randomUUID().toString();
		test.log(LogStatus.INFO, "After decoding the gateway_id : " + gateway_id);
		gateway_hostname = RandomStringUtils.randomAlphanumeric(4) + "_" + gateway_hostname;
		boolean is_registered = true;
		// Registering a site using the site Registration_key
		response = gatewayServer.RegisterSite(site_registration_key, gateway_id, gateway_hostname, site_version,
				site_id, test);
		String site_secret = gatewayServer.checkRegisterSite(response, ServerResponseCode.Succes_Login, site_id,
				siteName, siteType, account_org_id, user_id1, is_registered, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

		// This is used to update a site by sitename
		String newname = "spog_test";
		test.log(LogStatus.INFO, "update the sitename");
		response = spogServer.updateSiteById(site_id, newname, userToken);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		auditEventsForSuborg = spogServer.composeJsonMap(user_id1, account_org_id, AuditCodeConstants.MODIFY_SITE,
				response, site_id, organizationEmail, "user", newname, "site");
		auditdetailsForSuborg.add(auditEventsForSuborg);

		// Login with msp organization to delete the sub organization
		test.log(LogStatus.INFO, "Logging with msp organization user");
		spogServer.userLogin(msporganizationEmail, common_password);
		validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "The value of the token:" + validToken);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.LOGIN_USER, response,
				user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);

		// audit tracking for the msp_child_org_direct_user
		test.log(LogStatus.INFO, "Getting the details of audit tracking for the msp org  users ");
		String filter = "code_id;=;402";
		String additonalURL = spogServer.PrepareURL(filter, "", 1, 2, test);
		userresponse = compose_expected_response(filter, auditdetailsForSuborg);
		Response response6 = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalURL, test);
		test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a msp_child user");
		// spogServer.checkaudittraildata1(response6,SpogConstants.SUCCESS_GET_PUT_DELETE,auditdetailsForSuborg
		// , curr_page, page_size, filter,
		// sortStr,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

		test.log(LogStatus.INFO, "The value of size:" + auditdetails.size());
		test.log(LogStatus.INFO, "Preparing the URL to invoke the API");
		additonalURL = spogServer.PrepareURL(filterStr, sortStr, curr_page, page_size, test);
		test.log(LogStatus.INFO, "Getting the details of audit tracking for the msp org  users ");
		response6 = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalURL, test);
		test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a msp user");
		// spogServer.checkaudittraildata1(response6,SpogConstants.SUCCESS_GET_PUT_DELETE,auditdetails
		// , curr_page, page_size, filterStr,
		// sortStr,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

		spogServer.setToken(sub_user_token);

		// create a source
		String sourceName = spogServer.ReturnRandom("Ramya_source");

		test.log(LogStatus.INFO, "create source and check");
		spogServer.errorHandle.printInfoMessageInDebugFile("create source and check");

		response = spogServer.createSource(sourceName, SourceType.machine, SourceProduct.cloud_direct, account_org_id,
				site_id, ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(), "sql;exchange",
				"Ramya_vm3", UUID.randomUUID().toString(), "Ramya_agent1", "windows 2012", "64", "1.0.0", "2.0",
				"http://upgrade", test);
		String source_id = response.then().extract().path("data.source_id");
		String source_name = response.then().extract().path("data.source_name");
		// Get the source by id
		test.log(LogStatus.INFO, "Get the source by related id ");

		spogServer.errorHandle.printInfoMessageInDebugFile("get source and check");
		// response=spogServer.getSourceById(validToken,source_id, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id, AuditCodeConstants.CREATE_SOURCE, response,
				source_id, newname, "site", sourceName, "source");
		auditdetails.add(auditEvents);

		// update a source
		test.log(LogStatus.INFO, "update the existing source with check");
		sourceName = "source_name_1";
		response = spogServer.updateSourcebysourceId(source_id, sourceName, SourceType.machine,
				SourceProduct.cloud_direct, account_org_id, site_id, "", ProtectionStatus.unprotect,
				ConnectionStatus.online, "windows", sub_user_token, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		source_id = response.then().extract().path("data.source_id");
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id, AuditCodeConstants.MODIFY_SOURCE, response,
				source_id, organizationEmail, "user", sourceName, "source");
		auditdetails.add(auditEvents);

		test.log(LogStatus.INFO, "Create a source group");
		String groupName = "audit-source-group";
		response = spogServer.createGroup(account_org_id, groupName, "audit purpose");
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		String sourcegroup_id = response.then().extract().path("data.group_id");
		auditEvents = spogServer.storetheuserevents(AuditCodeConstants.create_source_group, account_org_id, user_id,
				sourcegroup_id, response, organizationEmail, "user", groupName, "source_group");
		auditdetails.add(auditEvents);

		test.log(LogStatus.INFO, "Modify source group");
		groupName = "audit-mod-sgrp";
		response = spogServer.updateSourceGroup(sourcegroup_id, groupName, "modify source group");
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		auditEvents = spogServer.storetheuserevents(AuditCodeConstants.modify_source_group, account_org_id, user_id,
				sourcegroup_id, response, organizationEmail, "user", groupName, "source_group");
		auditdetails.add(auditEvents);

		test.log(LogStatus.INFO, "Add source to source group");
		response = spogServer.addSourcetoSourceGroupwithCheck(sourcegroup_id, new String[] { source_id },
				sub_user_token, SpogConstants.SUCCESS_POST, null, test);
		auditEvents = spogServer.storetheuserevents(AuditCodeConstants.add_source_to_group, account_org_id, user_id,
				sourcegroup_id, response, organizationEmail, "user", groupName, "source_group");
		auditdetails.add(auditEvents);

		test.log(LogStatus.INFO, "Delete source from source group");
		spogServer.deleteSourcefromSourceGroupwithCheck(sourcegroup_id, source_id, sub_user_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		auditEvents = spogServer.storetheuserevents(AuditCodeConstants.delete_source_from_group, organization_id,
				user_id, sourcegroup_id, response3, organizationEmail, "user", groupName, "source_group");
		auditdetails.add(auditEvents);

		test.log(LogStatus.INFO, "Delete source group");
		spogServer.deleteGroup(sourcegroup_id, test);
		auditEvents = spogServer.storetheuserevents(AuditCodeConstants.delete_source_group, account_org_id, user_id,
				sourcegroup_id, response3, organizationEmail, "user", groupName, "source_group");
		auditdetails.add(auditEvents);

		// create destination for sub org with msp token.

		String destination_name = "destDedupder";
		// create destination cloud_direct
		spogDestinationServer.setToken(validToken);
		String destination_id = UUID.randomUUID().toString();
		response = spogDestinationServer.createDestination(destination_id, account_org_id, cloud_account_id,
				"91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", DestinationType.cloud_direct_volume.toString(),
				DestinationStatus.running.toString(), "20", cloud_account_id, "normal",
				RandomStringUtils.randomAlphanumeric(4) + "host-t", "7D", "7 Days", "0", "0", "0", "0", "0", "0", "0",
				"0", "0", "0", destination_name, test);
		destination_id = response.then().extract().path("data.destination_id");
		auditEvents = spogServer.storetheuserevents(AuditCodeConstants.create_destination, account_org_id, user_id,
				destination_id, response, organizationEmail, "user", destination_name, "destination");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		test.log(LogStatus.INFO, "Create source filter for user");
		String filter_name = "s_filter";
		response = spogServer.createFilter(user_id, filter_name, "emptyarray", "online", UUID.randomUUID().toString(),
				"finished", "none", "windows", "emptyarray", site_id, source_name, SourceType.machine.name(), "true",
				test);
		String filter_id = response.then().extract().path("data.filter_id");
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id, AuditCodeConstants.create_source_filter,
				response, filter_id, organizationEmail, "user", filter_name, "source_filter");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		test.log(LogStatus.INFO, "update source filter for user");
		/* filter_name="s_filter_m"; */
		response = spogServer.updateFilter(user_id, filter_id, "te", "emptyarray", "online",
				UUID.randomUUID().toString(), "finished", "none", "windows", "emptyarray", site_id, source_name,
				SourceType.machine.name(), SourceType.machine.name(), "true", test);
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id, AuditCodeConstants.modify_source_filter,
				response, filter_id, organizationEmail, "user", filter_name, "source_filter");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		test.log(LogStatus.INFO, "delete source filter for user");
		spogServer.deletefilterspecifiedbyUserIdwithCheck(user_id, filter_id, validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id, AuditCodeConstants.delete_source_filter,
				response3, filter_id, organizationEmail, "user", filter_name, "source_filter");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		test.log(LogStatus.INFO, "Create destination filter for user");
		spogDestinationServer.setToken(sub_user_token);
		filter_name = "filter";
		response = spogDestinationServer.createDestinationFilter(user_id, "filter", "filterdestname", "none",
				DestinationType.cloud_direct_volume.toString(), "true", test);
		filter_id = response.then().extract().path("data.filter_id");
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id, AuditCodeConstants.create_destination_filter,
				response, filter_id, organizationEmail, "user", filter_name, "destination_filter");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		test.log(LogStatus.INFO, "update destination filter for user");
		response = spogDestinationServer.updateDestinationFilterWithCheck(user_id, filter_id, "filter", "none", "none",
				DestinationType.cloud_direct_volume.toString(), "true", test, false);
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id, AuditCodeConstants.modify_destination_filter,
				response, filter_id, organizationEmail, "user", filter_name, "destination_filter");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		test.log(LogStatus.INFO, "delete destination filter for user");
		spogDestinationServer.deletedestinationfilterbyfilterId(user_id, filter_id, validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id, AuditCodeConstants.delete_destination_filter,
				response3, filter_id, organizationEmail, "user", filter_name, "destination_filter");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		test.log(LogStatus.INFO, "Create the job filter in org " + organizationType);
		spogJobServer.setToken(sub_user_token);
		filter_name = "filter_name";
		response = spogJobServer.createJobFilterWithCheckResponse(user_id, "finished", UUID.randomUUID().toString(),
				source_id, "backup_full", null, null, null, null, "filter_name", "true", test);
		filter_id = response.then().extract().path("data.filter_id");
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id, AuditCodeConstants.CREATE_JOB_FILTER, response,
				filter_id, organizationEmail, "user", filter_name, "job_filter");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		test.log(LogStatus.INFO, "update the job filter in org " + organizationType);
		response = spogJobServer.updateJobFilterWithCheck(user_id, filter_id, "finished", UUID.randomUUID().toString(),
				source_id, "backup_full", null, null, "filter_name", "true", test);
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id, AuditCodeConstants.MODIFY_JOB_FILTER, response,
				filter_id, organizationEmail, "user", filter_name, "job_filter");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		test.log(LogStatus.INFO, "delete the job filter in org " + organizationType);
		spogJobServer.deleteJobFilterByFilterID(user_id, filter_id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE,
				null, test);
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id, AuditCodeConstants.DELETE_JOB_FILTER,
				response3, filter_id, organizationEmail, "user", filter_name, "job_filter");
		codedetails.add(auditEvents);

		test.log(LogStatus.INFO, "Create user filter in org of type " + organizationType);
		filter_name = "ufilter";
		response = userSpogServer.createUserFilterForSpecificUser(user_id, "ufilter", null, null, null,
				SpogConstants.MSP_ADMIN, "true", validToken, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		filter_id = response.then().extract().path("data.filter_id");
		auditEvents = spogServer.storetheuserevents(AuditCodeConstants.CREATE_USER_FILTER, account_org_id, user_id,
				filter_id, response, organizationEmail, "user", filter_name, "user_filter");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		test.log(LogStatus.INFO, "update user filter in org of type " + organizationType);
		response = userSpogServer.updateUserFilterForSpecificUser(user_id, filter_id, "ufilter", null, null, null,
				SpogConstants.MSP_ADMIN, "true", validToken, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		filter_id = response.then().extract().path("data.filter_id");
		auditEvents = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_USER_FILTER, account_org_id, user_id,
				filter_id, response, organizationEmail, "user", filter_name, "user_filter");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		test.log(LogStatus.INFO, "delete user filter in org of type " + organizationType);
		userSpogServer.setToken(sub_user_token);
		userSpogServer.deleteUserFilterByUserIdWithCheck(validToken, user_id, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		auditEvents = spogServer.storetheuserevents(AuditCodeConstants.DELETE_USER_FILTER, account_org_id, user_id,
				filter_id, response3, organizationEmail, "user", filter_name, "user_filter");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		test.log(LogStatus.INFO, "Create the log filter in org " + organizationType);
		log4SPOGServer.setToken(sub_user_token);
		filter_name = "filter_name";
		response = log4SPOGServer.createLogFilter(user_id, account_org_id, "filter_name", "last_24_hours", null, null,
				LogSeverityType.warning.toString(), "false", "none", "none", null, sourceName, "none", test);
		filter_id = response.then().extract().path("data.filter_id");
		auditEvents = spogServer.storetheuserevents(AuditCodeConstants.CREATE_LOG_FILTER, account_org_id, user_id,
				filter_id, response, organizationEmail, "user", filter_name, "log_filter");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		test.log(LogStatus.INFO, "update the log filter in org " + organizationType);
		response = log4SPOGServer.updateLogFilter(user_id, filter_id, account_org_id, "filter_name", "last_24_hours",
				null, "backup_full", LogSeverityType.warning.toString(), "false", "none", "none", "none", "sourcename",
				"none", test);
		auditEvents = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_LOG_FILTER, account_org_id, user_id,
				filter_id, response, organizationEmail, "user", filter_name, "log_filter");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		test.log(LogStatus.INFO, "delete the log filter in org " + organizationType);
		log4SPOGServer.deleteLogFilterByFilterID(user_id, filter_id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE,
				null, test);
		auditEvents = spogServer.storetheuserevents(AuditCodeConstants.DELETE_LOG_FILTER, account_org_id, user_id,
				filter_id, response3, organizationEmail, "user", filter_name, "log_filter");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		// ramya
		test.log(LogStatus.INFO, "Create user columns for specified user in organization : " + organizationType);
		temp = userSpogServer.composeUser_Column("50169479-cf16-412a-9a6d-3bf19a111a0b", "true", "true", "true", "1");
		expectedColumns.add(temp);
		response = userSpogServer.createUserColumnsByUserId(sub_user_token, user_id, expectedColumns, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id, AuditCodeConstants.CREATE_USER_COLUMN,
				response, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		// ramya
		test.log(LogStatus.INFO, "update user columns for specified user in organization : " + organizationType);
		response = userSpogServer.updateUserColumnsByUserId(sub_user_token, user_id, expectedColumns, expectedColumns,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id, AuditCodeConstants.MODIFY_USER_COLUMN,
				response, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		// ramya
		test.log(LogStatus.INFO, "delete user columns for logged in user in organization : " + organizationType);
		userSpogServer.deleteUserColumnsForLoggedInUserWithCheck(sub_user_token, SpogConstants.SUCCESS_GET_PUT_DELETE,
				null, test);
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id, AuditCodeConstants.DELETE_USER_COLUMN,
				response3, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		// ramya
		test.log(LogStatus.INFO, "Create destination columns for logged in user in organization : " + organizationType);
		temp = userSpogServer.composeUser_Column("a6e15988-2919-4fd2-9b7d-00d2eddffde8", "false", "true", "true", "1");
		expectedColumns = new ArrayList<>();
		expectedColumns.add(temp);
		response = spogDestinationServer.createDestinationColumnsForLoggedInUser(sub_user_token, expectedColumns, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id,
				AuditCodeConstants.CREATE_USER_DESTINATION_COLUMN, response, user_id, organizationEmail, "user",
				organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		// ramya
		test.log(LogStatus.INFO, "update destination columns for Specified user in organization : " + organizationType);
		response = spogDestinationServer.updateDestinationColumnsByUserId(user_id, sub_user_token, expectedColumns,
				expectedColumns, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id,
				AuditCodeConstants.MODIFY_USER_DESTINATION_COLUMN, response, user_id, organizationEmail, "user",
				organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		// ramya
		test.log(LogStatus.INFO, "delete destination columns for Specified user in organization : " + organizationType);
		spogDestinationServer.deleteDestinationColumnsforSpecifiedUserwithCheck(user_id, sub_user_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id,
				AuditCodeConstants.DELETE_USER_DESTINATION_COLUMN, response3, user_id, organizationEmail, "user",
				organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		// ramya
		test.log(LogStatus.INFO, "Create job columns for Logged in user in organization : " + organizationType);
		temp = spogJobServer.composejob_Column("c6a4be7e-bf1c-4e88-a278-9c34f5f69117", "false", "true", "true", "1");
		expectedColumns = new ArrayList<>();
		expectedColumns.add(temp);
		response = spogJobServer.createJobColumnsForSpecifiedUser(user_id, sub_user_token, expectedColumns, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id, AuditCodeConstants.CREATE_USER_JOB_COLUMN,
				response, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		// ramya
		test.log(LogStatus.INFO, "update job columns for Specified user in organization : " + organizationType);
		response = spogJobServer.updateJobColumnsForSpecifiedUser(user_id, sub_user_token, expectedColumns, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id, AuditCodeConstants.MODIFY_USER_JOB_COLUMN,
				response, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		// ramya
		test.log(LogStatus.INFO, "delete job columns for Specified user in organization : " + organizationType);
		spogJobServer.deleteJobcolumnsforspecifeduser(user_id, sub_user_token, SpogConstants.SUCCESS_GET_PUT_DELETE,
				null, test);
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id, AuditCodeConstants.DELETE_USER_JOB_COLUMN,
				response3, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		// ramya
		test.log(LogStatus.INFO, "Create log columns for Specified user in organization : " + organizationType);
		temp = log4SPOGServer.composelog_Column("55d3dbb0-83e7-45ae-879a-8f3a522d66c5", "true", "true", "true", "1");
		expectedColumns = new ArrayList<>();
		expectedColumns.add(temp);
		response = log4SPOGServer.createLogColumnsForSpecifiedUser(user_id, sub_user_token, expectedColumns, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id, AuditCodeConstants.CREATE_USER_LOG_COLUMN,
				response, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		// ramya
		test.log(LogStatus.INFO, "update log columns for Specified user in organization : " + organizationType);
		response = log4SPOGServer.updateLogColumnsForSpecifiedUser(user_id, sub_user_token, expectedColumns, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id, AuditCodeConstants.MODIFY_USER_LOG_COLUMN,
				response, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		// ramya
		test.log(LogStatus.INFO, "delete log columns for Specified user in organization : " + organizationType);
		log4SPOGServer.deleteLogColumnsforSpecifiedUserwithCheck(user_id, sub_user_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id, AuditCodeConstants.DELETE_USER_LOG_COLUMN,
				response3, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		// ramya
		test.log(LogStatus.INFO, "Create source columns for logged in user in organization : " + organizationType);
		temp = log4SPOGServer.composelog_Column("07871b87-6ec5-44ae-a1bb-17abfb75a9f9", "false", "true", "true", "1");
		expectedColumns = new ArrayList<>();
		expectedColumns.add(temp);
		response = spogSourceServer.createUsersSourcesColumns(user_id, expectedColumns, sub_user_token);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id, AuditCodeConstants.CREATE_USER_SOURCE_COLUMN,
				response, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		// Ramya
		test.log(LogStatus.INFO, "update source columns for logged in user in organization : " + organizationType);
		response = spogSourceServer.updateUsersSourcesColumns(user_id, expectedColumns, sub_user_token);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id, AuditCodeConstants.MODIFY_USER_SOURCE_COLUMN,
				response, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		// Ramya
		test.log(LogStatus.INFO, "delete source columns for logged in user in organization : " + organizationType);
		spogSourceServer.deleteLoggedInUserSourcesColumns(sub_user_token);
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id, AuditCodeConstants.DELETE_USER_SOURCE_COLUMN,
				response3, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		// Ramya-user column already exists
		test.log(LogStatus.INFO, "Create hypervisor columns for Specified user in organization : " + organizationType);
		String column_id = "31dfe327-b9fe-432a-a119-24b584a85263";
		temp = log4SPOGServer.composelog_Column(column_id, "true", "true", "true", "1");
		expectedColumns = new ArrayList<>();
		expectedColumns.add(temp);
		response = spogSourceServer.createHypervisorColumnsForSpecifiedUser(user_id, expectedColumns, validToken);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id,
				AuditCodeConstants.CREATE_USER_HYPERVISOR_COLUMN, response, user_id, organizationEmail, "user",
				organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		// Ramya
		test.log(LogStatus.INFO, "update hypervisor columns for Specified user in organization : " + organizationType);
		response = spogSourceServer.updateHypervisorColumnsForSpecifiedUser(user_id, expectedColumns, validToken);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id,
				AuditCodeConstants.MODIFY_USER_HYPERVISOR_COLUMN, response, user_id, organizationEmail, "user",
				organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		// Ramya
		test.log(LogStatus.INFO, "delete hypervisor columns for Specified user in organization : " + organizationType);
		spogSourceServer.deleteHypervisorColumnsForSpecifiedUser(user_id, validToken);
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id,
				AuditCodeConstants.DELETE_USER_HYPERVISOR_COLUMN, response3, user_id, organizationEmail, "user",
				organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		// create policy for direct
		test.log(LogStatus.INFO, "create policy for direct");
		String task_id = UUID.randomUUID().toString();

		String throttle_id = UUID.randomUUID().toString();
		String schedule_id = UUID.randomUUID().toString();
		String throttle_type = "network";
		String taskType = "cloud_direct_file_folder_backup";
		ArrayList<String> drivers = new ArrayList<>();
		// destination_id="792ef2fb-ced1-41fa-a93e-67881e9f058b";
		drivers.add("C");

		policy4SpogServer.setToken(sub_user_token);
		test.log(LogStatus.INFO, "Create cloud direct schedule");

		HashMap<String, Object> cloudDirectScheduleDTO = policy4SpogServer.createCloudDirectScheduleDTO("0 6 * * *",
				test);

		test.log(LogStatus.INFO, "Create custom settings");
		HashMap<String, Object> customScheduleSettingDTO = policy4SpogServer.createCustomScheduleDTO("1522068700422",
				"custom", "1", "true", "10", "minutes", test);

		HashMap<String, Object> scheduleSettingDTO = policy4SpogServer.createScheduleSettingDTO(cloudDirectScheduleDTO,
				null, null, null, test);

		ArrayList<HashMap<String, Object>> schedules = policy4SpogServer.createPolicyScheduleDTO(null, schedule_id,
				"1d", task_id, destination_id, scheduleSettingDTO, "06:00", "12:00", taskType, destination_name, test);
		ArrayList<HashMap<String, Object>> excludes = policy4SpogServer.createExcludeInfoDTO(null, "path", "c:\\tmp",
				test);

		HashMap<String, Object> perform_ar_test = policy4SpogServer.createPerformARTestOption("true", "true", "true",
				"true", test);

		HashMap<String, Object> retention_policy = policy4SpogServer.createRetentionPolicyOption("2", "2", "2", "2",
				test);

		HashMap<String, Object> udp_replication_from_remote_DTO = policy4SpogServer
				.createUdpReplicationFromRemoteInfoDTO(perform_ar_test, retention_policy, test);

		HashMap<String, Object> cloudDirectLocalBackupDTO = policy4SpogServer.createCloudDirectLocalBackupDTO("d:\\tmp",
				"true", test);

		HashMap<String, Object> cloudDirectFileBackupTaskInfoDTO = policy4SpogServer
				.createCloudDirectFileBackupTaskInfoDTO("d:\\tmp", cloudDirectLocalBackupDTO, excludes, test);
		HashMap<String, Object> cloudDirectimageBackupTaskInfoDTO = policy4SpogServer
				.createCloudDirectImageBackupTaskInfoDTO(drivers, cloudDirectLocalBackupDTO, test);
		ArrayList<HashMap<String, Object>> destinations = policy4SpogServer.createPolicyTaskDTO(null, task_id, taskType,
				destination_id, "none", null, cloudDirectFileBackupTaskInfoDTO, null, test);
		ArrayList<HashMap<String, Object>> throttles = policy4SpogServer.createPolicyThrottleDTO(null, throttle_id,
				task_id, throttle_type, "1200", "1", "06:00", "18:00", taskType, destination_id, destination_name,
				test);

		policy4SpogServer.setToken(sub_user_token);
		// create cloud direct policy
		String policy_name = spogServer.ReturnRandom("ramya");
		response = policy4SpogServer.createPolicy(policy_name, policy_name, "cloud_direct_baas", null, "true",
				source_id, destinations, schedules, throttles, UUID.randomUUID().toString(), account_org_id, test);
		String policy_id = response.then().extract().path("data.policy_id").toString();
		policy4SpogServer.checkPolicyDestinations(response, SpogConstants.SUCCESS_POST, destinations, test);
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id, AuditCodeConstants.CREATE_POLICY, response,
				policy_id, organizationEmail, "user", policy_name, "policy");
		auditdetails.add(auditEvents);

		// update policy
		throttles = policy4SpogServer.createPolicyThrottleDTO(null, throttle_id, task_id, throttle_type, "600", "1",
				"06:00", "12:00", taskType, destination_id, destination_name, test);
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		response = policy4SpogServer.updatePolicy(policy_name, policy_name, "cloud_direct_baas", null, "true",
				source_id, destinations, schedules, throttles, policy_id, account_org_id, validToken, test);
		policy4SpogServer.checkPolicyDestinations(response, SpogConstants.SUCCESS_GET_PUT_DELETE, destinations, test);
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id, AuditCodeConstants.MODIFY_POLICY, response,
				policy_id, organizationEmail, "user", policy_name, "policy");
		auditdetails.add(auditEvents);

		// creating recovered resourcefilters
		test.log(LogStatus.INFO, "creating recovered resourcefilters");
		/* policy_id = UUID.randomUUID().toString(); */
		filter_name = spogServer.ReturnRandom("ramya");
		spogRecoveredResourceServer.setToken(sub_user_token);
		// HashMap<String,Object>
		// compose_filter=spogRecoveredResourceServer.composeExpectedRecoveredResourcesFilter(policy_id,State.get(1),os_major,recoveredResourceType.get(1),filter_name,"true");
		response = spogRecoveredResourceServer.createSpecifiedUserRecoveredResourcesFilters(user_id, sub_user_token,
				policy_id, State.get(1), OSMajor.mac.name(), recoveredResourceType.get(1), filter_name, "true", "",
				test);
		filter_id = response.then().extract().path("data.filter_id");
		test.log(LogStatus.INFO, "validating the response for creating recovered resourcefilters");
		// spogRecoveredResourceServer.checkRecoveredResourceFilters(response,
		// filter_id,
		// policy_id,State.get(1),OSMajor.mac.name(),recoveredResourceType.get(1),filter_name,"true",
		// organization_id, user_id, SpogConstants.SUCCESS_POST,
		// SpogMessageCode.SUCCESS_POST, test, filtersHeadContent);
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id,
				AuditCodeConstants.CREATE_RECOVEREDRESOURCE_FILTER, response, filter_id, organizationEmail, "user",
				filter_name, "recovered_resource_filter");
		auditdetails.add(auditEvents);

		// update recovered resourcefilters
		test.log(LogStatus.INFO, "update recovered resourcefilters");
		response = spogRecoveredResourceServer.updateSpecifiedUserRecoveredResourcesFilters(user_id, sub_user_token,
				filter_id, policy_id, State.get(1), OSMajor.mac.name(), recoveredResourceType.get(1), filter_name,
				"true", SpogConstants.SUCCESS_GET_PUT_DELETE, "", test);
		test.log(LogStatus.INFO, "validating the response for get recovered resourcefilters");
		// spogRecoveredResourceServer.checkRecoveredResourceFilters(response,
		// filter_id,
		// policy_id,State.get(1),OSMajor.mac.name(),recoveredResourceType.get(1),filter_name,"true",
		// organization_id, user_id, SpogConstants.SUCCESS_GET_PUT_DELETE,
		// SpogMessageCode.SUCCESS_GET_PUT_DEL, test, filtersHeadContent);
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id,
				AuditCodeConstants.MODIFY_RECOVEREDRESOURCE_FILTER, response, filter_id, organizationEmail, "user",
				filter_name, "recovered_resource_filter");
		auditdetails.add(auditEvents);

		// delete recovered resourcefilters
		test.log(LogStatus.INFO, "delete recovered resourcefilters");
		response = spogRecoveredResourceServer.deleteSpecifiedUserRecoveredResourcesFiltersByFilterId(user_id,
				filter_id, sub_user_token, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		test.log(LogStatus.INFO, "validating the response for delete recovered resourcefilters");
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id,
				AuditCodeConstants.DELETE_RECOVEREDRESOURCE_FILTER, response, filter_id, organizationEmail, "user",
				filter_name, "recovered_resource_filter");
		auditdetails.add(auditEvents);

		// creating recovered resource columns
		test.log(LogStatus.INFO, "creating recovered resource columns");
		ArrayList<HashMap<String, Object>> expected_columns = new ArrayList<HashMap<String, Object>>();
		ArrayList<HashMap<String, Object>> defaultcolumnresponse = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> temp1 = new HashMap<String, Object>();
		temp1 = spogRecoveredResourceServer.composeRecoveredResource_Column("84129da7-d794-4cc5-bfe1-17daf6b911bb",
				"false", "false", "true", "1");
		expected_columns.add(temp1);
		response = spogRecoveredResourceServer.createRecoveredResourceColumnsForSpecifiedUser(user_id, sub_user_token,
				expected_columns, test);
		test.log(LogStatus.INFO, "validating the response for creating recovered resource columns");
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id,
				AuditCodeConstants.CREATE_USER_RECOVEREDRESOURCE_COLUMN, response, user_id, organizationEmail, "user",
				organizationEmail, "user");
		auditdetails.add(auditEvents);

		// update recovered resource columns
		test.log(LogStatus.INFO, "update recovered resource columns");
		response = spogRecoveredResourceServer.updateRecoveredResourceColumnsByUserId(user_id, sub_user_token,
				expected_columns, defaultcolumnresponse, SpogConstants.SUCCESS_GET_PUT_DELETE,
				SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		test.log(LogStatus.INFO, "validating the response for updating recovered resource columns");
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id,
				AuditCodeConstants.MODIFY_USER_RECOVEREDRESOURCE_COLUMN, response, user_id, organizationEmail, "user",
				organizationEmail, "user");
		auditdetails.add(auditEvents);

		// delete recovered resource columns
		test.log(LogStatus.INFO, "delete recovered resource columns");
		spogRecoveredResourceServer.deleteRecoveredResourceColumnsforSpecifiedUserwithCheck(user_id, sub_user_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		test.log(LogStatus.INFO, "validating the response for deleting recovered resource columns");
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id,
				AuditCodeConstants.DELETE_USER_RECOVEREDRESOURCE_COLUMN, response3, user_id, organizationEmail, "user",
				organizationEmail, "user");
		auditdetails.add(auditEvents);

		// create user policy filter
		test.log(LogStatus.INFO, "create user policy filter");
		userSpogServer.setToken(sub_user_token);
		filter_name = "Finished succesfully";
		response = userSpogServer.createPolicyFilterForSpecificUserWithCheck_audit(user_id, "Finished succesfully",
				"policy_name", UUID.randomUUID().toString(), "finished", "failure", "true", test);
		filter_id = response.then().extract().path("data.filter_id");
		test.log(LogStatus.INFO, "validating the response for create user policy filter");
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id, AuditCodeConstants.CREATE_POLICY_FILTER,
				response, filter_id, organizationEmail, "user", filter_name, "policy_filter");
		auditdetails.add(auditEvents);

		// update user policy filter
		test.log(LogStatus.INFO, "update user policy filter");
		userSpogServer.setToken(sub_user_token);
		response = userSpogServer.updatePolicyFilterForSpecificUserWithCheck_audit(user_id, filter_id,
				"Finished succesfully", "policy_name", UUID.randomUUID().toString(), "finished", "failure", "true",
				test);
		filter_id = response.then().extract().path("data.filter_id");
		test.log(LogStatus.INFO, "validating the response for create user policy filter");
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id, AuditCodeConstants.MODIFY_POLICY_FILTER,
				response, filter_id, organizationEmail, "user", filter_name, "policy_filter");
		auditdetails.add(auditEvents);

		// update user policy filter
		test.log(LogStatus.INFO, "delete user policy filter");
		userSpogServer.setToken(sub_user_token);
		userSpogServer.deleteSpecificPolicyFilterForSpecificUser(user_id, filter_id, test);
		test.log(LogStatus.INFO, "validating the response for create user policy filter");
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id, AuditCodeConstants.DELETE_POLICY_FILTER,
				response3, filter_id, organizationEmail, "user", filter_name, "policy_filter");
		auditdetails.add(auditEvents);

		// create user policy columns
		test.log(LogStatus.INFO, "create user policy columns");
		policy4SpogServer.setToken(sub_user_token);
		ArrayList<HashMap<String, Object>> expected_columns_1 = new ArrayList<HashMap<String, Object>>();
		expected_columns_1 = policy4SpogServer.getPolicyColumnArrayList("Name;true;1", sub_user_token, test);
		response = policy4SpogServer.createUsersPolicyColumns(user_id, expected_columns_1, sub_user_token);
		test.log(LogStatus.INFO, "validating the response for create user policy columns");
		policy4SpogServer.checkUsersPolicyColumns(response, expected_columns_1, sub_user_token,
				SpogConstants.SUCCESS_POST, null);
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id, AuditCodeConstants.CREATE_USER_POLICY_COLUMN,
				response, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);

		// update user policy columns
		test.log(LogStatus.INFO, "update user policy columns");
		response = policy4SpogServer.updateUsersPolicyColumns(user_id, expected_columns_1, sub_user_token);
		test.log(LogStatus.INFO, "validating the response for create user policy columns");
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id, AuditCodeConstants.MODIFY_USER_POLICY_COLUMN,
				response, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);

		// delete user policy columns
		test.log(LogStatus.INFO, "delete user policy columns");
		policy4SpogServer.setToken(sub_user_token);
		policy4SpogServer.deleteUsersPolicyColumnsWithCheck(user_id, sub_user_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null);
		test.log(LogStatus.INFO, "validating the response for create user policy columns");
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id, AuditCodeConstants.DELETE_USER_POLICY_COLUMN,
				response3, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);

		// create report list filters for specified user
		test.log(LogStatus.INFO, "create report list filters for specified user");
		spogServer.setToken(sub_user_token);
		groupName = spogServer.ReturnRandom("ramya");
		// orgId for creating source_groups

		String source_group_ids_1 = null, source_group_id_1 = null;
		for (int j = 0; j < 2; j++) {
			source_group_id_1 = spogServer.createGroupWithCheck2(sub_user_token, account_org_id, groupName + j,
					groupName, test);
			if (j > 0) {
				source_group_ids_1 = source_group_ids_1 + "," + source_group_id_1;
			} else {
				source_group_ids_1 = source_group_id_1;
			}
		}
		spogReportServer.setToken(sub_user_token);
		filter_name = spogServer.ReturnRandom("filter");
		HashMap<String, Object> expectedfilter = spogReportServer.composeReportListFilterInfo(filter_name,
				"last_7_days", "all_sources", "", organization_id, "weekly", System.currentTimeMillis(),
				System.currentTimeMillis(), filter_name, true);
		response = spogReportServer.createReportListFiltersForSpecifiedUserWithCheck(sub_user_token, user_id,
				organization_id, expectedfilter, SpogConstants.SUCCESS_POST, SpogMessageCode.SUCCESS_POST, test);
		filter_id = response.then().extract().path("data.filter_id");
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id, AuditCodeConstants.CREATE_REPORT_LIST_FILTER,
				response, filter_id, organizationEmail, "user", filter_name, "report_list_filter");
		auditdetails.add(auditEvents);

		// update report list filters for specified user
		test.log(LogStatus.INFO, "update report list filters for specified user");
		response = spogReportServer.updateReportListFiltersForSpecifiedUserByFilterIdWithCheck(sub_user_token, user_id,
				organization_id, filter_id, expectedfilter, SpogConstants.SUCCESS_GET_PUT_DELETE,
				SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id, AuditCodeConstants.MODIFY_REPORT_LIST_FILTER,
				response, filter_id, organizationEmail, "user", filter_name, "report_list_filter");
		auditdetails.add(auditEvents);

		// delete report list filters for specified user
		test.log(LogStatus.INFO, "delete report list filters for specified user");
		spogReportServer.deleteReportListFiltersForSpecifiedUserByFilterIdWithCheck(sub_user_token, user_id, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id, AuditCodeConstants.DELETE_REPORT_LIST_FILTER,
				response3, filter_id, organizationEmail, "user", filter_name, "report_list_filter");
		auditdetails.add(auditEvents);

		// create report list filters for specified user
		test.log(LogStatus.INFO, "create report list filters for specified user");
		spogServer.setToken(validToken);
		// orgId for creating source_groups
		String orgId = null;
		groupName = spogServer.ReturnRandom("ramya");
		orgId = spogServer.GetLoggedinUserOrganizationID();
		String source_group_ids = null, source_group_id = null;
		for (int j = 0; j < 1; j++) {
			source_group_id = spogServer.createGroupWithCheck2(validToken, orgId, groupName + j, groupName, test);
			if (j > 0) {
				source_group_ids = source_group_ids + "," + source_group_id;
			} else {
				source_group_ids = source_group_id;
			}
		}

		// create report filter
		test.log(LogStatus.INFO, "create  report filter");
		HashMap<String, Object> reportfilterInfo = null;
		test.log(LogStatus.INFO, "compose report filter");
		filter_name = "filter_name";
		reportfilterInfo = spogReportServer.composeReportFilterInfo(sourceName, destination_name, policy_id,
				destination_id, source_group_id, "", "today", System.currentTimeMillis(), System.currentTimeMillis(),
				"backup_jobs", "", "backup", "", "", filter_name, true, "backup_jobs");
		response = spogReportServer.createReportFilterForSpecifiedUserWithCheck_audit(user_id, account_org_id,
				sub_user_token, reportfilterInfo, SpogConstants.SUCCESS_POST, null, test);
		filter_id = response.then().extract().path("data.filter_id");
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id, AuditCodeConstants.CREATE_REPORT_FILTER,
				response, filter_id, organizationEmail, "user", filter_name, "report_filter");
		auditdetails.add(auditEvents);

		// update report filter
		reportfilterInfo.clear();
		test.log(LogStatus.INFO, "update  report filter");
		reportfilterInfo = spogReportServer.composeReportFilterInfo(sourceName, destination_name, policy_id,
				destination_id, "", "", "today", System.currentTimeMillis(), System.currentTimeMillis(), "backup_jobs",
				"", "backup", "", "", filter_name, true, "backup_jobs");
		response = spogReportServer.updateReportFilterForSpecifiedUserWithCheck(user_id, filter_id, account_org_id,
				sub_user_token, reportfilterInfo, reportfilterInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id, AuditCodeConstants.MODIFY_REPORT_FILTER,
				response, filter_id, organizationEmail, "user", filter_name, "report_filter");
		auditdetails.add(auditEvents);

		// delete report filter
		test.log(LogStatus.INFO, "delete report filter");
		response = spogReportServer.deleteReportFiltersForSpecifiedUser(sub_user_token, user_id, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id, AuditCodeConstants.DELETE_REPORT_FILTER,
				response3, filter_id, organizationEmail, "user", filter_name, "report_filter");
		auditdetails.add(auditEvents);

		// create backup job report columns
		test.log(LogStatus.INFO, "create backup job report columns");
		ArrayList<HashMap<String, Object>> expected_columns_2 = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> temp2 = new HashMap<>();
		for (int j = 0; j < 1; j++) {
			temp2 = spogReportServer.composeBackUpJobReportsColumns("b9004719-1897-4f09-abd2-7a9fb6edb768", "true",
					"3");

			expected_columns_2.add(temp2);
		}

		response = spogReportServer.createBackUpJobReportsColumnsForSpecifiedUser(sub_user_token, user_id,
				expected_columns_2, SpogConstants.SUCCESS_POST, test);
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id,
				AuditCodeConstants.CREATE_USER_BACKUPJOBREPORT_COLUMN, response, user_id, organizationEmail, "user",
				organizationEmail, "user");
		auditdetails.add(auditEvents);
		expected_columns_2.clear();

		// update backup job report columns
		test.log(LogStatus.INFO, "update backup job report columns");
		for (int j = 0; j < 1; j++) {
			temp2 = spogReportServer.composeBackUpJobReportsColumns("b9004719-1897-4f09-abd2-7a9fb6edb768", "true",
					"4");

			expected_columns_2.add(temp2);
		}

		response = spogReportServer.updateBackUpJobReportsColumnsForSpecifiedUser(sub_user_token, user_id,
				expected_columns_2, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id,
				AuditCodeConstants.MODIFY_USER_BACKUPJOBREPORT_COLUMN, response, user_id, organizationEmail, "user",
				organizationEmail, "user");
		auditdetails.add(auditEvents);

		// delete BackUpJobReportsColumnsForSpecifiedUser
		test.log(LogStatus.INFO, "delete BackUpJobReportsColumnsForSpecifiedUser ");
		spogReportServer.deleteBackUpJobReportsColumnsForSpecifiedUser(sub_user_token, user_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id,
				AuditCodeConstants.DELETE_USER_BACKUPJOBREPORT_COLUMN, response3, user_id, organizationEmail, "user",
				organizationEmail, "user");
		auditdetails.add(auditEvents);

		// create restore job report columns
		test.log(LogStatus.INFO, "create restore job report columns");
		spogReportServer.setToken(sub_user_token);
		expected_columns_2.clear();
		for (int j = 0; j < 1; j++) {
			temp2 = spogReportServer.composeBackUpJobReportsColumns("07871b87-6ec5-44ae-a1bb-17abfb75a9f9", "true",
					"3");

			expected_columns_2.add(temp2);
		}

		response = spogReportServer.createRestoreJobReportsColumnsForSpecifiedUser(user_id, sub_user_token,
				expected_columns_2, test);
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id,
				AuditCodeConstants.CREATE_USER_RESTOREJOBREPORT_COLUMN, response, user_id, organizationEmail, "user",
				organizationEmail, "user");
		auditdetails.add(auditEvents);
		expected_columns_2.clear();

		// update restore job report columns
		test.log(LogStatus.INFO, "update restore job report columns");
		for (int j = 0; j < 1; j++) {
			temp2 = spogReportServer.composeRestoreJobReports_Column("07871b87-6ec5-44ae-a1bb-17abfb75a9f9", "true",
					"false", "true", "4");

			expected_columns_2.add(temp2);
		}

		response = spogReportServer.updateRestoreJobReportsColumnsByUserId(user_id, sub_user_token, expected_columns_2,
				expected_columns_2, SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id,
				AuditCodeConstants.MODIFY_USER_RESTOREJOBREPORT_COLUMN, response, user_id, organizationEmail, "user",
				organizationEmail, "user");
		auditdetails.add(auditEvents);

		// delete RestoreJobReportsColumnsForSpecifiedUser
		test.log(LogStatus.INFO, "delete RestoreJobReportsColumnsForSpecifiedUser ");
		spogReportServer.deleteRestoreJobReportsColumnsforSpecifiedUserwithCheck(user_id, sub_user_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id,
				AuditCodeConstants.DELETE_USER_RESTOREJOBREPORT_COLUMN, response3, user_id, organizationEmail, "user",
				organizationEmail, "user");
		auditdetails.add(auditEvents);

		// update a source
		test.log(LogStatus.INFO, "update the existing source with check");
		sourceName = "source_name_1";
		response = spogServer.updateSourcebysourceId(source_id, sourceName, SourceType.machine,
				SourceProduct.cloud_direct, account_org_id, site_id, "", ProtectionStatus.unprotect,
				ConnectionStatus.online, "windows", sub_user_token, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		source_id = response.then().extract().path("data.source_id");
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id, AuditCodeConstants.MODIFY_SOURCE, response,
				source_id, organizationEmail, "user", sourceName, "source");
		auditdetails.add(auditEvents);

		test.log(LogStatus.INFO, "Create a source group");
		groupName = "audit-source-group";
		response = spogServer.createGroup(account_org_id, groupName, "audit purpose");
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		sourcegroup_id = response.then().extract().path("data.group_id");
		auditEvents = spogServer.storetheuserevents(AuditCodeConstants.create_source_group, account_org_id, user_id,
				sourcegroup_id, response, organizationEmail, "user", groupName, "source_group");
		auditdetails.add(auditEvents);

		test.log(LogStatus.INFO, "Modify source group");
		groupName = "audit-mod-sgrp";
		response = spogServer.updateSourceGroup(sourcegroup_id, groupName, "modify source group");
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		auditEvents = spogServer.storetheuserevents(AuditCodeConstants.modify_source_group, account_org_id, user_id,
				sourcegroup_id, response, organizationEmail, "user", groupName, "source_group");
		auditdetails.add(auditEvents);

		test.log(LogStatus.INFO, "Add source to source group");
		response = spogServer.addSourcetoSourceGroupwithCheck(sourcegroup_id, new String[] { source_id },
				sub_user_token, SpogConstants.SUCCESS_POST, null, test);
		auditEvents = spogServer.storetheuserevents(AuditCodeConstants.add_source_to_group, account_org_id, user_id,
				sourcegroup_id, response, organizationEmail, "user", groupName, "source_group");
		auditdetails.add(auditEvents);

		test.log(LogStatus.INFO, "Delete source from source group");
		spogServer.deleteSourcefromSourceGroupwithCheck(sourcegroup_id, source_id, sub_user_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		auditEvents = spogServer.storetheuserevents(AuditCodeConstants.delete_source_from_group, account_org_id,
				user_id, sourcegroup_id, response3, organizationEmail, "user", groupName, "source_group");
		auditdetails.add(auditEvents);

		test.log(LogStatus.INFO, "Delete source group");
		spogServer.deleteGroup(sourcegroup_id, test);
		auditEvents = spogServer.storetheuserevents(AuditCodeConstants.delete_source_group, account_org_id, user_id,
				sourcegroup_id, response3, organizationEmail, "user", groupName, "source_group");
		auditdetails.add(auditEvents);

		// delete policy by id
		policy4SpogServer.deletePolicybyPolicyId(sub_user_token, policy_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id, AuditCodeConstants.DELETE_POLICY, response3,
				policy_id, organizationEmail, "user", policy_name, "policy");
		auditdetails.add(auditEvents);

		// create report schedule
		test.log(LogStatus.INFO, "create report schedule ");
		HashMap<String, Object> scheduleInfo = new HashMap<String, Object>();
		scheduleInfo = spogReportServer.composeReportScheduleInfo("report_name", "backup_jobs", "last_7_days",
				System.currentTimeMillis(), System.currentTimeMillis(), "daily", "", organization_id,
				"a@gmail.com,b@gmail.com", " 0 0 19 ? * *", "all_sources");
		response = spogReportServer.createReportScheduleWithCheck_audit(validToken, scheduleInfo,
				SpogConstants.SUCCESS_POST, null, test);
		schedule_id = response.then().extract().path("data.schedule_id");
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id, AuditCodeConstants.CREATE_REPORT_SCHEDULE,
				response, user_id, organizationEmail, "user", schedule_id, "report_schedule");
		auditdetails.add(auditEvents);

		// modify report schedule
		test.log(LogStatus.INFO, "modify report schedule ");
		response = spogReportServer.updateReportScheduleByIdWithCheck(schedule_id, validToken, scheduleInfo,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id, AuditCodeConstants.MODIFY_REPORT_SCHEDULE,
				response, user_id, organizationEmail, "user", schedule_id, "report_schedule");
		auditdetails.add(auditEvents);

		// Deleting the source by id
		test.log(LogStatus.INFO, "delete source by id");
		response = spogServer.deleteSourcesById(validToken, source_id, test);
		test.log(LogStatus.INFO, "validating the response for the delete sourcses by id");
		spogServer.deleteSourcesWithCheck(response, SpogConstants.SUCCESS_GET_PUT_DELETE,
				SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id, AuditCodeConstants.DELETE_SOURCE, response,
				source_id, organizationEmail, "user", sourceName, "source");
		auditdetails.add(auditEvents);

		// delete the site
		test.log(LogStatus.INFO, "Delete the site " + site_id);
		response = spogServer.deleteSite(site_id, validToken);
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id, AuditCodeConstants.DELETE_SITE, response3,
				site_id, organizationEmail, "user", newname, "site");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);
		// delete report schedule
		test.log(LogStatus.INFO, "modify report schedule ");
		spogReportServer.deleteReportScheduleByIdWithCheck(schedule_id, validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		auditEvents = spogServer.composeJsonMap(user_id, account_org_id, AuditCodeConstants.DELETE_REPORT_SCHEDULE,
				response3, user_id, organizationEmail, "user", schedule_id, "report_schedule");

		destination_name = "destDedupder_test";
		// create destination cloud_direct
		spogDestinationServer.setToken(validToken);
		response = spogDestinationServer.createDestination(UUID.randomUUID().toString(), account_org_id,
				cloud_account_id, "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c",
				DestinationType.cloud_direct_volume.toString(), DestinationStatus.running.toString(), "20",
				cloud_account_id, "normal", RandomStringUtils.randomAlphanumeric(4) + "host-t", "7D", "7 Days", "0",
				"0", "0", "0", "0", "0", "0", "0", "0", "0", destination_name, test);
		destination_id = response.then().extract().path("data.destination_id");

		auditdetails.add(auditEvents);

		for (int i = 0; i < 2; i++) {
			if (i == 1) {
				validToken = csr_read_only_validToken;
			}

			// audit tracking for the msp_child_org_direct_user
			test.log(LogStatus.INFO, "Getting the details of audit tracking for the msp org  users ");
			filter = "code_id;=;402";
			additonalURL = spogServer.PrepareURL(filter, "", 1, 2, test);
			userresponse = compose_expected_response(filter, auditdetailsForSuborg);
			response6 = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalURL, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a msp_child user");
			// spogServer.checkaudittraildata1(response6,SpogConstants.SUCCESS_GET_PUT_DELETE,auditdetailsForSuborg
			// , curr_page, page_size, filter,
			// sortStr,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			test.log(LogStatus.INFO, "The value of size:" + auditdetails.size());
			test.log(LogStatus.INFO, "Preparing the URL to invoke the API");
			additonalURL = spogServer.PrepareURL(filterStr, sortStr, curr_page, page_size, test);
			test.log(LogStatus.INFO, "Getting the details of audit tracking for the msp org  users ");
			response6 = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalURL, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a msp user");
			// spogServer.checkaudittraildata1(response6,SpogConstants.SUCCESS_GET_PUT_DELETE,auditdetails
			// , curr_page, page_size, filterStr,
			// sortStr,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// create policy Audits
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;4010";
			String additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,filter
			// ,
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// check policy Audits
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;4014|4015|4016";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,filter
			// ,
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			// spogServer.checkaudittraildata1(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,
			// filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// check policy Audits
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;4011";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,filter
			// ,
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// check destination Audits
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;601|602";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,filter
			// ,
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;603";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,filter
			// ,
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// check policy Audits
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;4012";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,filter
			// ,
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// check backupJobReportsColumnsForSpecifiedUser
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;2025|2026|2027";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,filter
			// ,
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			// spogServer.checkaudittraildata1(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,
			// filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Preparing the URL for Filtering  ");
			filter = "code_id;in;1|2|3";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by userid " + user_id);
			userresponse = compose_expected_response(filter, usercodes);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			test.log(LogStatus.INFO, "Preparing the URL for Filtering  ");
			filter = "code_id;in;5";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by userid " + user_id);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			// check RestoreJobReportsColumnsForSpecifiedUser
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;2028|2029|2030";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,filter
			// ,
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// check report filters
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;1028|1029|1030";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,filter
			// ,
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// check list report filters
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;1025|1026|1027";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,filter
			// ,
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// Filtering based on code_id and ascending order sorting
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;1019|1020|1021";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,filter
			// ,
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// Filtering based on code_id and ascending order sorting
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;2019|2020|2021";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,filter
			// ,
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// Filtering based on code_id and ascending order sorting
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;1022|1023|1024";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,filter
			// ,
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// Filtering based on code_id and ascending order sorting
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;2022|2023|2024";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,filter
			// ,
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// Filtering based on code_id and ascending order sorting
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;1004|1005|1006";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,filter
			// ,
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// sorting based on Descending order
			test.log(LogStatus.INFO, "Preparing the URL for sorting based on descending order ");
			filter = "code_id;=;1001|1002|1003";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20,filter,
			// "create_ts;desc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// Sorting based on ascending order

			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;1007|1008|1009";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// sorting and pagination Ascending order
			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;1010|1011|1012";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			// sorting and pagination Ascending order
			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;1013|1014|1015";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// sorting and pagination Descending order
			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;1016|1017|1018";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// sorting and pagination Descending order
			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;2001|2002|2003";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(sub_user_token, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// sorting and pagination Descending order
			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;2004|2005|2006";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(sub_user_token, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;2007|2008|2009";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(sub_user_token, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// sorting and pagination Ascending order
			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;2010|2011|2012";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(sub_user_token, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// sorting and pagination Ascending order
			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;2013|2014|2015";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(sub_user_token, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// sorting and pagination Descending order
			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;2016|2017|2018";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(sub_user_token, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// Array of code id and filtering the url on usercodes

			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;4006|4007";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(sub_user_token, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;4008|4009";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(sub_user_token, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			// csr read only cases

			// check backupJobReportsColumnsForSpecifiedUser
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;2025|2026|2027";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(csr_read_only_validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,filter
			// ,
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			// spogServer.checkaudittraildata1(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,
			// filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Preparing the URL for Filtering  ");
			filter = "code_id;in;1|2|3";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by userid " + user_id);
			userresponse = compose_expected_response(filter, usercodes);
			response = spogServer.getAuditDetailsForUsers(csr_read_only_validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			test.log(LogStatus.INFO, "Preparing the URL for Filtering  ");
			filter = "code_id;in;5";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by userid " + user_id);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(csr_read_only_validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			// check RestoreJobReportsColumnsForSpecifiedUser
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;2028|2029|2030";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(csr_read_only_validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,filter
			// ,
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// check report filters
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;1028|1029|1030";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(csr_read_only_validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,filter
			// ,
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// check list report filters
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;1025|1026|1027";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(csr_read_only_validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,filter
			// ,
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// Filtering based on code_id and ascending order sorting
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;1019|1020|1021";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(csr_read_only_validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,filter
			// ,
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// Filtering based on code_id and ascending order sorting
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;2019|2020|2021";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(csr_read_only_validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,filter
			// ,
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// Filtering based on code_id and ascending order sorting
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;1022|1023|1024";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(csr_read_only_validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,filter
			// ,
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// Filtering based on code_id and ascending order sorting
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;2022|2023|2024";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(csr_read_only_validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,filter
			// ,
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// Filtering based on code_id and ascending order sorting
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;1004|1005|1006";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(csr_read_only_validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,filter
			// ,
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// sorting based on Descending order
			test.log(LogStatus.INFO, "Preparing the URL for sorting based on descending order ");
			filter = "code_id;=;1001|1002|1003";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + user_id);
			response = spogServer.getAuditDetailsForUsers(csr_read_only_validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20,filter,
			// "create_ts;desc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// Sorting based on ascending order

			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;1007|1008|1009";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(csr_read_only_validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// sorting and pagination Ascending order
			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;1010|1011|1012";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(csr_read_only_validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			// sorting and pagination Ascending order
			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;1013|1014|1015";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(csr_read_only_validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// sorting and pagination Descending order
			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;1016|1017|1018";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(csr_read_only_validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// sorting and pagination Descending order
			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;2001|2002|2003";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(csr_read_only_validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// sorting and pagination Descending order
			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;2004|2005|2006";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(csr_read_only_validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;2007|2008|2009";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(csr_read_only_validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// sorting and pagination Ascending order
			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;2010|2011|2012";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(csr_read_only_validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// sorting and pagination Ascending order
			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;2013|2014|2015";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(csr_read_only_validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// sorting and pagination Descending order
			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;2016|2017|2018";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(csr_read_only_validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// Array of code id and filtering the url on usercodes

			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;4006|4007";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(csr_read_only_validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;4008|4009";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(csr_read_only_validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		}
		// delete sub-organization:
		test.log(LogStatus.INFO, "Login in as csr admin to delete the organization");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);

		test.log(LogStatus.INFO, "Deleting the subOrganization");
		spogServer.deleteMSPAccountWithCheck(organization_id, account_org_id);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.DELETE_SUB_ORGANIZATION,
				response, account_org_id, organizationEmail, "user", accountName, "organization");
		auditdetails.add(auditEvents);

		// Deleting the msp organization
		test.log(LogStatus.INFO, "Login in as csr admin to delete the organization");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);

		spogServer.DeleteOrganizationWithCheck(organization_id, test);

	}

	@Test(dataProvider = "organizationAndUserInfo7")
	public void getAuditTrailForDirect(String organizationName, String organizationType, String organizationEmail,
			String organizationPwd, String organizationFirstName, String organizationLastName, String userEmail,
			String userPassword, String FirstName, String LastName, String Role_Id, SourceType sourceType,
			SourceProduct sourceProduct, ProtectionStatus protectionStatus, ConnectionStatus connectionStatus,
			String os_major, String applications, String vm_name, String hypervisor_id, String agent_name,
			String os_name, String os_architecture, String agent_current_version, String agent_upgrade_version,
			String agent_upgrade_link, String DestinationTyp, String data_store_folder, String data_destination,
			String index_destination, String hash_destination, int concurrent_active_node, boolean Is_deduplicated,
			int block_size, String hash_memory, boolean is_compressed, String encryption_password,
			String occupied_space, String stored_data, Double deduplication_rate, Double compression_rate,
			String destination_status, String destination_name) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Ramya Nagepalli");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;
		FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + FirstName;
		LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + LastName;
		userEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;
		ArrayList<HashMap> auditdetails = new ArrayList<HashMap>();
		HashMap<String, Object> auditEvents = new HashMap<String, Object>();
		ArrayList<HashMap> usercodes = new ArrayList<HashMap>();
		ArrayList<HashMap> sitecodes = new ArrayList<HashMap>();
		ArrayList<HashMap> userresponse = new ArrayList<HashMap>();
		ArrayList<HashMap> codedetails = new ArrayList<HashMap>();
		ArrayList<HashMap> orgcodes = new ArrayList<HashMap>();
		ArrayList<HashMap<String, Object>> expectedColumns = new ArrayList<>();
		HashMap<String, Object> temp = new HashMap<>();
		Response response3 = null;
		// organizationEmail=userEmail;

		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		csr_token = spogServer.getJWTToken();
		String organization_id = spogServer.CreateOrganizationByEnrollWithCheck(organizationName + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "The organization id is " + organization_id);
		// login with direct organization
		test.log(LogStatus.INFO, "Logging with direct organization user");
		spogServer.userLogin(organizationEmail, common_password);
		String validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "Get the logged in user ");
		Response response = spogServer.getLoggedInUser(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		spogServer.checkLoggedInUser(response, SpogConstants.SUCCESS_GET_PUT_DELETE,
				SpogMessageCode.SUCCESS_GET_PUT_DEL, organizationEmail, test);
		String user_id = response.then().extract().path("data.user_id");
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.LOGIN_USER, response,
				user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);
		usercodes.add(auditEvents);

		// create user under direct organization
		test.log(LogStatus.INFO, "creating a direct user under the msp organization");

		Response response1 = spogServer.createUser(userEmail, userPassword, FirstName, LastName, Role_Id,
				organization_id, test);
		String user_id1 = spogServer.checkCreateUser(response1, SpogConstants.SUCCESS_POST, userEmail, FirstName,
				LastName, Role_Id, organization_id, "", test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.CREATE_USER, response1,
				user_id1, organizationEmail, "user", userEmail, "user");
		auditdetails.add(auditEvents);
		usercodes.add(auditEvents);

		// modifying user
		test.log(LogStatus.INFO, "Modify the user");
		response = spogServer.updateUserById(user_id1, userEmail, userPassword, FirstName + "modi", LastName, Role_Id,
				organization_id, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.MODIFY_USER, response,
				user_id1, organizationEmail, "user", userEmail, "user");
		auditdetails.add(auditEvents);
		usercodes.add(auditEvents);

		// modify-The login user change password
		test.log(LogStatus.INFO, "Modify login user password");
		String newPassword = "Mclaren@2013";
		spogServer.setToken(validToken);
		response = spogServer.changePasswordForLoggedInUser(organizationPwd, newPassword);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.MODIFY_LOGIN_USER_PASSWORD,
				response, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);

		usercodes.add(auditEvents);

		// Modify the Logged in user by id
		String changepassword = "Mclaren@2016";
		test.log(LogStatus.INFO, "Modify the logged in user password by user id");
		response = spogServer.changePasswordForSpecifiedUser(user_id, newPassword, changepassword);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.MODIFY_USER_PASSWORD,
				response, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);
		usercodes.add(auditEvents);

		// modify the login user
		test.log(LogStatus.INFO, "Modify the login user");
		response = spogServer.UpdateLoggedInUser(organizationEmail, changepassword, organizationFirstName,
				organizationLastName + "mod", Role_Id, organization_id);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.MODIFY_LOGIN_USER,
				response, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);
		usercodes.add(auditEvents);

		// creating a site for direct_admin user
		test.log(LogStatus.INFO, "create a site in spog for mentioned details");
		String siteName = "msp_admin_user_site";
		String siteType = GatewayServer.siteType.gateway.toString();
		response = spogServer.createSite(siteName, siteType, organization_id, validToken, test);
		Map<String, String> sitecreateResMap = new HashMap<>();
		// validating the response for creation of the site
		sitecreateResMap = spogServer.checkCreateSite(response, SpogConstants.SUCCESS_POST, siteName, siteType,
				organization_id, user_id, "", test);
		String registration_basecode = sitecreateResMap.get("registration_basecode");
		test.log(LogStatus.INFO, "The encoded message is:" + registration_basecode);
		String site_id = sitecreateResMap.get("site_id");
		test.log(LogStatus.INFO, "The generated site_id is :" + site_id);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.CREATE_SITE, response,
				site_id, organizationEmail, "user", siteName, "site");
		auditdetails.add(auditEvents);
		sitecodes.add(auditEvents);

		// Register a site for direct user organization_id
		String site_registration_key = null;
		// decoding the encoded base64 key
		site_registration_key = base64.DecodeSiteRegistrationKey(registration_basecode);
		test.log(LogStatus.INFO, "After decoding the site_registration_key : " + site_registration_key);

		String gateway_id = UUID.randomUUID().toString();
		test.log(LogStatus.INFO, "After decoding the gateway_id : " + gateway_id);

		gateway_hostname = RandomStringUtils.randomAlphanumeric(4) + "_" + gateway_hostname;
		boolean is_registered = true;
		// Registering a site using the site Registration_key
		response = gatewayServer.RegisterSite(site_registration_key, gateway_id, gateway_hostname, site_version,
				site_id, test);
		String site_secret = gatewayServer.checkRegisterSite(response, ServerResponseCode.Succes_Login, site_id,
				siteName, siteType, organization_id, user_id, is_registered, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

		// This is used to update a site by sitename
		String newname = "spog_test";
		test.log(LogStatus.INFO, "update the sitename");
		response = spogServer.updateSiteById(site_id, newname, validToken);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.MODIFY_SITE, response,
				site_id, organizationEmail, "user", newname, "site");
		auditdetails.add(auditEvents);
		sitecodes.add(auditEvents);

		// this method is used to delete a site

		// create a source
		String sourceName = spogServer.ReturnRandom("Ramya_source");

		test.log(LogStatus.INFO, "create source and check");
		spogServer.errorHandle.printInfoMessageInDebugFile("create source and check");

		response = spogServer.createSource(sourceName, sourceType, sourceProduct, organization_id, site_id,
				protectionStatus, connectionStatus, os_major, applications, vm_name, hypervisor_id, agent_name, os_name,
				os_architecture, agent_current_version, agent_upgrade_version, agent_upgrade_link, test);
		String source_id = response.then().extract().path("data.source_id");
		// Get the source by id
		test.log(LogStatus.INFO, "Get the source by related id ");

		spogServer.errorHandle.printInfoMessageInDebugFile("get source and check");
		// response=spogServer.getSourceById(validToken,source_id, test);
		// spogServer.checkResponseStatus(response,SpogConstants.SUCCESS_GET_PUT_DELETE,test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.CREATE_SOURCE, response,
				source_id, newname, "site", sourceName, "source");
		auditdetails.add(auditEvents);

		/*
		 * //create a destinaition test.log(LogStatus.INFO,
		 * "creating a destination of type cloud_dedupe");
		 * cloud_dedupe_volume=spogDestinationServer.composeCloudDedupeInfo(
		 * data_store_folder
		 * ,data_destination,index_destination,hash_destination,
		 * concurrent_active_node, Is_deduplicated,block_size, hash_memory,
		 * is_compressed
		 * ,encryption_password,occupied_space,stored_data,deduplication_rate,
		 * compression_rate); test.log(LogStatus.INFO,
		 * "Creating a destination of type cloud_dedupe_volume");
		 * response=spogDestinationServer.createDestination(validToken,
		 * organization_id,site_id,"",DestinationTyp,destination_status,
		 * destination_name,cloud_dedupe_volume,test); String
		 * destination_id=response.then().extract().path("data.destination_id");
		 * auditEvents=spogServer.composeJsonMap(user_id,organization_id,
		 * AuditCodeConstants.create_destination,response,destination_id,
		 * userEmail, "user", destination_name, "destination");
		 * auditdetails.add(auditEvents);
		 * 
		 * test.log(LogStatus.INFO,"Deleting a destinaiton by id ");
		 * spogDestinationServer.deletedestinationbydestination_Id(
		 * destination_id,validToken,SpogConstants.SUCCESS_GET_PUT_DELETE,
		 * SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		 * auditEvents=spogServer.composeJsonMap(user_id,organization_id,
		 * AuditCodeConstants.delete_destination,response,destination_id,
		 * userEmail, "user", destination_name, "destination");
		 * auditdetails.add(auditEvents);
		 * 
		 */

		// delete the user.
		test.log(LogStatus.INFO, "Delete the user");
		spogServer.DeleteUserById(user_id1, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.DELETE_USER, response,
				user_id1, organizationEmail, "user", userEmail, "user");
		auditdetails.add(auditEvents);
		usercodes.add(auditEvents);

		// preparing the url for descending or
		test.log(LogStatus.INFO, "Preparing the URL for sorting based on descending order ");
		String additionalUrl = spogServer.PrepareURL("", "create_ts;asc", 1, 20, test);
		test.log(LogStatus.INFO, "Get the audit trail by orgId " + user_id);
		Response response6 = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
		test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
		// spogServer.checkaudittraildata1(response6,SpogConstants.SUCCESS_GET_PUT_DELETE,auditdetails
		// , 1, 20, "",
		// "create_ts;desc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
		test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

		// Sorting based on ascending order

		test.log(LogStatus.INFO, "Preparing the URL for sorting ");
		additionalUrl = spogServer.PrepareURL("", "create_ts;asc", 1, 20, test);
		test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
		response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
		test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
		// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,auditdetails
		// , 1, 20, "",
		// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

		// sorting and pagination Ascending order

		test.log(LogStatus.INFO, "Preparing the URL for sorting ");
		additionalUrl = spogServer.PrepareURL("", "create_ts;asc", 1, 4, test);
		test.log(LogStatus.INFO, "Get the audit trail by userId" + user_id);
		response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
		test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
		// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,auditdetails
		// , 1, 4, "",
		// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

		// sorting and pagination Ascending order
		test.log(LogStatus.INFO, "Preparing the URL for sorting ");
		additionalUrl = spogServer.PrepareURL("", "create_ts;asc", 2, 4, test);
		test.log(LogStatus.INFO, "Get the audit trail by userID " + user_id);
		response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
		test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
		// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,auditdetails
		// , 2, 4, "",
		// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

		// sorting and pagination Descending order

		test.log(LogStatus.INFO, "Preparing the URL for sorting ");
		additionalUrl = spogServer.PrepareURL("", "create_ts;desc", 1, 4, test);
		test.log(LogStatus.INFO, "Get the audit trail by userId" + user_id);
		response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
		test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
		// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,auditdetails
		// , 1, 4, "",
		// "create_ts;desc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

		// sorting and pagination Descending order
		test.log(LogStatus.INFO, "Preparing the URL for sorting ");
		additionalUrl = spogServer.PrepareURL("", "create_ts;desc", 2, 4, test);
		test.log(LogStatus.INFO, "Get the audit trail by userid " + user_id);
		response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
		test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
		// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,auditdetails
		// , 2, 4, "",
		// "create_ts;desc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

		// Array of code id and filtering the url on usercodes

		test.log(LogStatus.INFO, "Preparing the URL for Filtering  ");
		additionalUrl = spogServer.PrepareURL("code_id;in;1|2|3|4|5|6|7", "create_ts;asc", 1, 20, test);
		test.log(LogStatus.INFO, "Get the audit trail by userid " + user_id);
		response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
		test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
		// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,usercodes
		// , 1, 20, "code_id;in;1|2|3|4|5|6|7",
		// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

		// Array of codes filtering based on descending order
		test.log(LogStatus.INFO, "Preparing the URL for Filtering  ");
		additionalUrl = spogServer.PrepareURL("code_id;in;1|2|3|4|5|6|7", "create_ts;desc", 1, 20, test);
		test.log(LogStatus.INFO, "Get the audit trail by userid " + user_id);
		response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
		test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
		// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,usercodes
		// , 1, 20, "code_id;in;1|2|3|4|5|6|7",
		// "create_ts;desc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

		// array of codes Filtering based on creation of the time stamp < some
		// date value

		test.log(LogStatus.INFO, "Prepare the URL for the code id ");
		additionalUrl = spogServer.PrepareURL("code_id;in;1|2|3|4|5|6|7,create_ts;<;" + tomorrow, "create_ts;asc", 1,
				20, test);
		test.log(LogStatus.INFO, "Get the audit trail by userid  " + user_id);
		response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
		test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
		// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,usercodes
		// , 1, 20,
		// "code_id;in;1|2|3|4|5|6|7,create_ts;<;"+tomorrow,"create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

		// preapring the filering under the sites
		test.log(LogStatus.INFO, "Preparing the URL for Filtering  on sites ");
		String filter = "code_id;in;401|402|403";
		additionalUrl = spogServer.PrepareURL("code_id;in;401|402|403", "create_ts;asc", 1, 20, test);
		test.log(LogStatus.INFO, "Get the audit trail by userid " + user_id);
		userresponse = compose_expected_response(filter, sitecodes);
		response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
		test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
		// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
		// , 1, 20, "code_id;in;401|402|403",
		// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

		// Filtering based on codes and create_rs

		test.log(LogStatus.INFO, "Preparing the URL for Filtering under site and create_ts  ");
		additionalUrl = spogServer.PrepareURL("code_id;in;401|402|403,create_ts;>;" + yesterday, "create_ts;asc", 1, 10,
				test);
		test.log(LogStatus.INFO, "Get the audit trail by userid " + user_id);
		response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
		test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
		// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,sitecodes
		// , 1, 10, "code_id;in;401|402|403,create_ts;>;"+yesterday,
		// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

		// Filtering based on codes and create_rs

		test.log(LogStatus.INFO, "Preparing the URL for Filtering under site and create_ts  ");
		additionalUrl = spogServer.PrepareURL("code_id;in;401|402|403,create_ts;<;" + tomorrow, "create_ts;asc", 1, 10,
				test);
		test.log(LogStatus.INFO, "Get the audit trail by userid " + user_id);
		response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
		test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
		// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,sitecodes
		// , 1, 10, "code_id;in;401|402|403,create_ts;<;"+tomorrow,
		// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

		// code id and time stamp
		test.log(LogStatus.INFO, "Prepare the URL for the code id ");
		additionalUrl = spogServer.PrepareURL("code_id;in;1|2|3|4|5|6|7,create_ts;>;" + yesterday, "create_ts;asc", 1,
				20, test);
		test.log(LogStatus.INFO, "Get the audit trail by userid  " + user_id);
		response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
		test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
		// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,usercodes
		// , 1, 20,
		// preparing the url for descending or
		test.log(LogStatus.INFO, "Preparing the URL for sorting based on descending order ");
		additionalUrl = spogServer.PrepareURL("", "create_ts;asc", 1, 20, test);
		test.log(LogStatus.INFO, "Get the audit trail by orgId " + user_id);
		response6 = spogServer.getAuditDetailsForUsers(csr_read_only_validToken, user_id, additionalUrl, test);
		test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
		// spogServer.checkaudittraildata1(response6,SpogConstants.SUCCESS_GET_PUT_DELETE,auditdetails
		// , 1, 20, "",
		// "create_ts;desc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
		test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

		// Sorting based on ascending order

		test.log(LogStatus.INFO, "Preparing the URL for sorting ");
		additionalUrl = spogServer.PrepareURL("", "create_ts;asc", 1, 20, test);
		test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
		response = spogServer.getAuditDetailsForUsers(csr_read_only_validToken, user_id, additionalUrl, test);
		test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
		// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,auditdetails
		// , 1, 20, "",
		// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

		// sorting and pagination Ascending order

		test.log(LogStatus.INFO, "Preparing the URL for sorting ");
		additionalUrl = spogServer.PrepareURL("", "create_ts;asc", 1, 4, test);
		test.log(LogStatus.INFO, "Get the audit trail by userId" + user_id);
		response = spogServer.getAuditDetailsForUsers(csr_read_only_validToken, user_id, additionalUrl, test);
		test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
		// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,auditdetails
		// , 1, 4, "",
		// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

		// sorting and pagination Ascending order
		test.log(LogStatus.INFO, "Preparing the URL for sorting ");
		additionalUrl = spogServer.PrepareURL("", "create_ts;asc", 2, 4, test);
		test.log(LogStatus.INFO, "Get the audit trail by userID " + user_id);
		response = spogServer.getAuditDetailsForUsers(csr_read_only_validToken, user_id, additionalUrl, test);
		test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
		// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,auditdetails
		// , 2, 4, "",
		// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

		// sorting and pagination Descending order

		test.log(LogStatus.INFO, "Preparing the URL for sorting ");
		additionalUrl = spogServer.PrepareURL("", "create_ts;desc", 1, 4, test);
		test.log(LogStatus.INFO, "Get the audit trail by userId" + user_id);
		response = spogServer.getAuditDetailsForUsers(csr_read_only_validToken, user_id, additionalUrl, test);
		test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
		// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,auditdetails
		// , 1, 4, "",
		// "create_ts;desc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

		// sorting and pagination Descending order
		test.log(LogStatus.INFO, "Preparing the URL for sorting ");
		additionalUrl = spogServer.PrepareURL("", "create_ts;desc", 2, 4, test);
		test.log(LogStatus.INFO, "Get the audit trail by userid " + user_id);
		response = spogServer.getAuditDetailsForUsers(csr_read_only_validToken, user_id, additionalUrl, test);
		test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
		// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,auditdetails
		// , 2, 4, "",
		// "create_ts;desc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

		// Array of code id and filtering the url on usercodes

		test.log(LogStatus.INFO, "Preparing the URL for Filtering  ");
		additionalUrl = spogServer.PrepareURL("code_id;in;1|2|3|4|5|6|7", "create_ts;asc", 1, 20, test);
		test.log(LogStatus.INFO, "Get the audit trail by userid " + user_id);
		response = spogServer.getAuditDetailsForUsers(csr_read_only_validToken, user_id, additionalUrl, test);
		test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
		// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,usercodes
		// , 1, 20, "code_id;in;1|2|3|4|5|6|7",
		// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

		// Array of codes filtering based on descending order
		test.log(LogStatus.INFO, "Preparing the URL for Filtering  ");
		additionalUrl = spogServer.PrepareURL("code_id;in;1|2|3|4|5|6|7", "create_ts;desc", 1, 20, test);
		test.log(LogStatus.INFO, "Get the audit trail by userid " + user_id);
		response = spogServer.getAuditDetailsForUsers(csr_read_only_validToken, user_id, additionalUrl, test);
		test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
		// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,usercodes
		// , 1, 20, "code_id;in;1|2|3|4|5|6|7",
		// "create_ts;desc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

		// array of codes Filtering based on creation of the time stamp < some
		// date value

		test.log(LogStatus.INFO, "Prepare the URL for the code id ");
		additionalUrl = spogServer.PrepareURL("code_id;in;1|2|3|4|5|6|7,create_ts;<;" + tomorrow, "create_ts;asc", 1,
				20, test);
		test.log(LogStatus.INFO, "Get the audit trail by userid  " + user_id);
		response = spogServer.getAuditDetailsForUsers(csr_read_only_validToken, user_id, additionalUrl, test);
		test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
		// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,usercodes
		// , 1, 20,
		// "code_id;in;1|2|3|4|5|6|7,create_ts;<;"+tomorrow,"create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

		// preapring the filering under the sites
		test.log(LogStatus.INFO, "Preparing the URL for Filtering  on sites ");
		filter = "code_id;in;401|402|403";
		additionalUrl = spogServer.PrepareURL("code_id;in;401|402|403", "create_ts;asc", 1, 20, test);
		test.log(LogStatus.INFO, "Get the audit trail by userid " + user_id);
		userresponse = compose_expected_response(filter, sitecodes);
		response = spogServer.getAuditDetailsForUsers(csr_read_only_validToken, user_id, additionalUrl, test);
		test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
		// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
		// , 1, 20, "code_id;in;401|402|403",
		// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

		// Filtering based on codes and create_rs

		test.log(LogStatus.INFO, "Preparing the URL for Filtering under site and create_ts  ");
		additionalUrl = spogServer.PrepareURL("code_id;in;401|402|403,create_ts;>;" + yesterday, "create_ts;asc", 1, 10,
				test);
		test.log(LogStatus.INFO, "Get the audit trail by userid " + user_id);
		response = spogServer.getAuditDetailsForUsers(csr_read_only_validToken, user_id, additionalUrl, test);
		test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
		// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,sitecodes
		// , 1, 10, "code_id;in;401|402|403,create_ts;>;"+yesterday,
		// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

		// Filtering based on codes and create_rs

		test.log(LogStatus.INFO, "Preparing the URL for Filtering under site and create_ts  ");
		additionalUrl = spogServer.PrepareURL("code_id;in;401|402|403,create_ts;<;" + tomorrow, "create_ts;asc", 1, 10,
				test);
		test.log(LogStatus.INFO, "Get the audit trail by userid " + user_id);
		response = spogServer.getAuditDetailsForUsers(csr_read_only_validToken, user_id, additionalUrl, test);
		test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
		// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,sitecodes
		// , 1, 10, "code_id;in;401|402|403,create_ts;<;"+tomorrow,
		// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

		// code id and time stamp
		test.log(LogStatus.INFO, "Prepare the URL for the code id ");
		additionalUrl = spogServer.PrepareURL("code_id;in;1|2|3|4|5|6|7,create_ts;>;" + yesterday, "create_ts;asc", 1,
				20, test);
		test.log(LogStatus.INFO, "Get the audit trail by userid  " + user_id);
		response = spogServer.getAuditDetailsForUsers(csr_read_only_validToken, user_id, additionalUrl, test);
		test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
		// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,usercodes

		response = spogServer.getCloudAccounts(validToken, "", test);
		String cloud_account_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");
		// create destination cloud_direct
		spogDestinationServer.setToken(validToken);
		String destination_id = UUID.randomUUID().toString();
		response = spogDestinationServer.createDestination(destination_id, organization_id, cloud_account_id,
				"91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", DestinationType.cloud_direct_volume.toString(),
				DestinationStatus.running.toString(), "20", cloud_account_id, "normal",
				RandomStringUtils.randomAlphanumeric(4) + "host-t", "7D", "7 Days", "0", "0", "0", "0", "0", "0", "0",
				"0", "0", "0", destination_name, test);
		destination_id = response.then().extract().path("data.destination_id");
		auditEvents = spogServer.storetheuserevents(AuditCodeConstants.create_destination, organization_id, user_id,
				destination_id, response, organizationEmail, "user", destination_name, "destination");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		test.log(LogStatus.INFO, "Create source filter for user");
		String filter_name = "filter_s";
		response = spogServer.createFilter(user_id, filter_name, "emptyarray", "online", UUID.randomUUID().toString(),
				"finished", "none", "windows", "emptyarray", site_id, sourceName, SourceType.machine.name(), "true",
				test);
		String filter_id = response.then().extract().path("data.filter_id");
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.create_source_filter,
				response, filter_id, organizationEmail, "user", filter_name, "source_filter");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		test.log(LogStatus.INFO, "update source filter for user");
		/* filter_name="filter_sm"; */
		response = spogServer.updateFilter(user_id, filter_id, filter_name, "emptyarray", "online",
				UUID.randomUUID().toString(), "finished", "none", "windows", "emptyarray", site_id, sourceName,
				SourceType.machine.name(), SourceType.machine.name(), "true", test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.modify_source_filter,
				response, filter_id, organizationEmail, "user", filter_name, "source_filter");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		test.log(LogStatus.INFO, "delete source filter for user");
		spogServer.deletefilterspecifiedbyUserIdwithCheck(user_id, filter_id, validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.delete_source_filter,
				response3, filter_id, organizationEmail, "user", filter_name, "source_filter");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		test.log(LogStatus.INFO, "Create destination filter for user");
		spogDestinationServer.setToken(validToken);
		filter_name = "filter_dest";
		response = spogDestinationServer.createDestinationFilter(user_id, filter_name, destination_name, "none",
				DestinationType.cloud_direct_volume.toString(), "true", test);
		filter_id = response.then().extract().path("data.filter_id");
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.create_destination_filter,
				response, filter_id, organizationEmail, "user", filter_name, "destination_filter");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		test.log(LogStatus.INFO, "update destination filter for user");
		/* filter_name="filter_dest_m"; */
		response = spogDestinationServer.updateDestinationFilterWithCheck(user_id, filter_id, filter_name, "none",
				"none", DestinationType.cloud_direct_volume.toString(), "true", test, false);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.modify_destination_filter,
				response, filter_id, organizationEmail, "user", filter_name, "destination_filter");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		test.log(LogStatus.INFO, "delete destination filter for user");
		spogDestinationServer.deletedestinationfilterbyfilterId(user_id, filter_id, validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.delete_destination_filter,
				response3, filter_id, organizationEmail, "user", filter_name, "destination_filter");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		test.log(LogStatus.INFO, "Create the job filter in org " + organizationType);
		spogJobServer.setToken(validToken);
		filter_name = "filter_name";
		response = spogJobServer.createJobFilterWithCheckResponse(user_id, "finished", UUID.randomUUID().toString(),
				source_id, "backup_full", null, null, null, null, "filter_name", "true", test);
		filter_id = response.then().extract().path("data.filter_id");
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.CREATE_JOB_FILTER,
				response, filter_id, organizationEmail, "user", filter_name, "job_filter");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		test.log(LogStatus.INFO, "update the job filter in org " + organizationType);
		response = spogJobServer.updateJobFilterWithCheck(user_id, filter_id, "finished", UUID.randomUUID().toString(),
				source_id, "backup_full", null, null, "filter_name", "true", test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.MODIFY_JOB_FILTER,
				response, filter_id, organizationEmail, "user", filter_name, "job_filter");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		test.log(LogStatus.INFO, "delete the job filter in org " + organizationType);
		spogJobServer.deleteJobFilterforloggedInUser(filter_id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.DELETE_JOB_FILTER,
				response3, filter_id, organizationEmail, "user", filter_name, "job_filter");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		log4SPOGServer.setToken(validToken);

		userSpogServer.setToken(validToken);

		test.log(LogStatus.INFO, "Create user filter in org of type " + organizationType);
		filter_name = "ufilter";
		response = userSpogServer.createUserFilterForSpecificUser(user_id, filter_name, null, null, null,
				SpogConstants.MSP_ADMIN, "true", validToken, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		filter_id = response.then().extract().path("data.filter_id");
		auditEvents = spogServer.storetheuserevents(AuditCodeConstants.CREATE_USER_FILTER, organization_id, user_id,
				filter_id, response, organizationEmail, "user", filter_name, "user_filter");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		test.log(LogStatus.INFO, "update user filter in org of type " + organizationType);
		/* filter_name="ufilter_m"; */
		response = userSpogServer.updateUserFilterForSpecificUser(user_id, filter_id, filter_name, null, null, null,
				SpogConstants.MSP_ADMIN, "true", validToken, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		filter_id = response.then().extract().path("data.filter_id");
		auditEvents = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_USER_FILTER, organization_id, user_id,
				filter_id, response, organizationEmail, "user", filter_name, "user_filter");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		test.log(LogStatus.INFO, "delete user filter in org of type " + organizationType);
		userSpogServer.deleteUserFilterByUserIdWithCheck(validToken, user_id, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		auditEvents = spogServer.storetheuserevents(AuditCodeConstants.DELETE_USER_FILTER, organization_id, user_id,
				filter_id, response3, organizationEmail, "user", filter_name, "user_filter");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		test.log(LogStatus.INFO, "Create the log filter in org " + organizationType);
		log4SPOGServer.setToken(validToken);
		filter_name = "filter_name";
		response = log4SPOGServer.createLogFilter(user_id, organization_id, filter_name, "last_24_hours", null, null,
				LogSeverityType.warning.toString(), "false", "none", "none", null, sourceName, "none", test);
		filter_id = response.then().extract().path("data.filter_id");
		auditEvents = spogServer.storetheuserevents(AuditCodeConstants.CREATE_LOG_FILTER, organization_id, user_id,
				filter_id, response, organizationEmail, "user", filter_name, "log_filter");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		test.log(LogStatus.INFO, "update the log filter in org " + organizationType);
		response = log4SPOGServer.updateLogFilter(user_id, filter_id, organization_id, filter_name, "last_24_hours",
				null, "backup_full", LogSeverityType.warning.toString(), "false", "none", "none", "none", sourceName,
				"none", test);
		auditEvents = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_LOG_FILTER, organization_id, user_id,
				filter_id, response, organizationEmail, "user", filter_name, "log_filter");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		test.log(LogStatus.INFO, "delete the log filter in org " + organizationType);
		log4SPOGServer.deleteLogFilterforloggedinUser(filter_id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);
		auditEvents = spogServer.storetheuserevents(AuditCodeConstants.DELETE_LOG_FILTER, organization_id, user_id,
				filter_id, response1, organizationEmail, "user", filter_name, "log_filter");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		// ramya
		test.log(LogStatus.INFO, "Create user columns for specified user in organization : " + organizationType);
		temp = userSpogServer.composeUser_Column("50169479-cf16-412a-9a6d-3bf19a111a0b", "true", "true", "true", "1");
		expectedColumns.add(temp);
		response = userSpogServer.createUserColumnsByUserId(validToken, user_id, expectedColumns, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.CREATE_USER_COLUMN,
				response, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		// ramya
		test.log(LogStatus.INFO, "update user columns for specified user in organization : " + organizationType);
		response = userSpogServer.updateUserColumnsByUserId(validToken, user_id, expectedColumns, expectedColumns,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.MODIFY_USER_COLUMN,
				response, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);
		usercodes.add(auditEvents);

		test.log(LogStatus.INFO, "delete user columns for logged in user in organization : " + organizationType);
		userSpogServer.deleteUserColumnsForLoggedInUserWithCheck(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.DELETE_USER_COLUMN,
				response3, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);
		usercodes.add(auditEvents);

		test.log(LogStatus.INFO, "Create destination columns for logged in user in organization : " + organizationType);
		temp = userSpogServer.composeUser_Column("a6e15988-2919-4fd2-9b7d-00d2eddffde8", "false", "true", "true", "1");
		expectedColumns = new ArrayList<>();
		expectedColumns.add(temp);
		response = spogDestinationServer.createDestinationColumnsForLoggedInUser(validToken, expectedColumns, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id,
				AuditCodeConstants.CREATE_USER_DESTINATION_COLUMN, response, user_id, organizationEmail, "user",
				organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);
		usercodes.add(auditEvents);

		test.log(LogStatus.INFO, "update destination columns for Specified user in organization : " + organizationType);
		response = spogDestinationServer.updateDestinationColumnsByUserId(user_id, validToken, expectedColumns,
				expectedColumns, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id,
				AuditCodeConstants.MODIFY_USER_DESTINATION_COLUMN, response, user_id, organizationEmail, "user",
				organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);
		usercodes.add(auditEvents);

		test.log(LogStatus.INFO, "delete destination columns for Specified user in organization : " + organizationType);
		spogDestinationServer.deleteDestinationColumnsforSpecifiedUserwithCheck(user_id, validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id,
				AuditCodeConstants.DELETE_USER_DESTINATION_COLUMN, response3, user_id, organizationEmail, "user",
				organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);
		usercodes.add(auditEvents);

		test.log(LogStatus.INFO, "Create job columns for Logged in user in organization : " + organizationType);
		temp = spogJobServer.composejob_Column("c6a4be7e-bf1c-4e88-a278-9c34f5f69117", "false", "true", "true", "1");
		expectedColumns = new ArrayList<>();
		expectedColumns.add(temp);
		response = spogJobServer.createJobColumnsForLoggedInUser(validToken, expectedColumns, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.CREATE_USER_JOB_COLUMN,
				response, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		// ramya
		test.log(LogStatus.INFO, "update job columns for Specified user in organization : " + organizationType);
		response = spogJobServer.updateJobColumnsForSpecifiedUser(user_id, validToken, expectedColumns, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.MODIFY_USER_JOB_COLUMN,
				response, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		// ramya
		test.log(LogStatus.INFO, "delete job columns for Specified user in organization : " + organizationType);
		spogJobServer.deleteJobcolumnsforspecifeduser(user_id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.DELETE_USER_JOB_COLUMN,
				response3, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		// ramya
		test.log(LogStatus.INFO, "Create log columns for Specified user in organization : " + organizationType);
		temp = log4SPOGServer.composelog_Column("55d3dbb0-83e7-45ae-879a-8f3a522d66c5", "true", "true", "true", "1");
		expectedColumns = new ArrayList<>();
		expectedColumns.add(temp);
		response = log4SPOGServer.createLogColumnsForSpecifiedUser(user_id, validToken, expectedColumns, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.CREATE_USER_LOG_COLUMN,
				response, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		// ramya
		test.log(LogStatus.INFO, "update log columns for Specified user in organization : " + organizationType);
		response = log4SPOGServer.updateLogColumnsForSpecifiedUser(user_id, validToken, expectedColumns, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.MODIFY_USER_LOG_COLUMN,
				response, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		// ramya
		test.log(LogStatus.INFO, "delete log columns for Specified user in organization : " + organizationType);
		log4SPOGServer.deleteLogColumnsforSpecifiedUserwithCheck(user_id, validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.DELETE_USER_LOG_COLUMN,
				response3, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		// ramya
		test.log(LogStatus.INFO, "Create source columns for logged in user in organization : " + organizationType);
		temp = log4SPOGServer.composelog_Column("07871b87-6ec5-44ae-a1bb-17abfb75a9f9", "false", "true", "true", "1");
		expectedColumns = new ArrayList<>();
		expectedColumns.add(temp);
		response = spogSourceServer.createLoggedInUserSourcesColumns(expectedColumns, validToken);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.CREATE_USER_SOURCE_COLUMN,
				response, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		// Ramya
		test.log(LogStatus.INFO, "update source columns for logged in user in organization : " + organizationType);
		response = spogSourceServer.updateLoggedInUserSourcesColumns(expectedColumns, validToken);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.MODIFY_USER_SOURCE_COLUMN,
				response, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		// Ramya
		test.log(LogStatus.INFO, "delete source columns for logged in user in organization : " + organizationType);
		spogSourceServer.deleteLoggedInUserSourcesColumns(validToken);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.DELETE_USER_SOURCE_COLUMN,
				response3, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		// Ramya-user column already exists
		test.log(LogStatus.INFO, "Create hypervisor columns for Specified user in organization : " + organizationType);
		String column_id = "31dfe327-b9fe-432a-a119-24b584a85263";
		temp = log4SPOGServer.composelog_Column(column_id, "true", "true", "true", "1");
		expectedColumns = new ArrayList<>();
		expectedColumns.add(temp);
		response = spogSourceServer.createHypervisorColumnsForSpecifiedUser(user_id, expectedColumns, validToken);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id,
				AuditCodeConstants.CREATE_USER_HYPERVISOR_COLUMN, response, user_id, organizationEmail, "user",
				organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		// Ramya
		test.log(LogStatus.INFO, "update hypervisor columns for Specified user in organization : " + organizationType);
		response = spogSourceServer.updateHypervisorColumnsForSpecifiedUser(user_id, expectedColumns, validToken);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id,
				AuditCodeConstants.MODIFY_USER_HYPERVISOR_COLUMN, response, user_id, organizationEmail, "user",
				organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		// Ramya
		test.log(LogStatus.INFO, "delete hypervisor columns for Specified user in organization : " + organizationType);
		spogSourceServer.deleteHypervisorColumnsForSpecifiedUser(user_id, validToken);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id,
				AuditCodeConstants.DELETE_USER_HYPERVISOR_COLUMN, response3, user_id, organizationEmail, "user",
				organizationEmail, "user");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		// login SITE
		response = gatewayServer.LoginSite(site_id, site_secret, gateway_id, test);
		// response =
		// gatewayServer.LoginSite("9561d3f8-03f2-4723-b226-e438d344a4e8",
		// "o58kZ7PyCw40p8qdc65V9R0qsPjGGYCR","a87b4b0f-4552-4b9f-992b-7da5e62fc620",
		// test);
		test.log(LogStatus.INFO, "validate the login site response ");
		gatewayServer.checksiteLogin(response, ServerResponseCode.Succes_Login, "", test);
		String site_token = gatewayServer.getJWTToken();
		auditEvents = spogServer.composeJsonMap(site_id, organization_id, AuditCodeConstants.LOGIN_SITE, response,
				site_id, newname, "site", newname, "site");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		// create policy for direct
		test.log(LogStatus.INFO, "create policy for direct");
		String task_id = UUID.randomUUID().toString();

		String throttle_id = UUID.randomUUID().toString();
		String schedule_id = UUID.randomUUID().toString();
		String throttle_type = "network";
		String taskType = "cloud_direct_file_folder_backup";
		ArrayList<String> drivers = new ArrayList<>();
		// destination_id="792ef2fb-ced1-41fa-a93e-67881e9f058b";
		drivers.add("C");

		policy4SpogServer.setToken(validToken);
		test.log(LogStatus.INFO, "Create cloud direct schedule");

		HashMap<String, Object> cloudDirectScheduleDTO = policy4SpogServer.createCloudDirectScheduleDTO("0 6 * * *",
				test);

		test.log(LogStatus.INFO, "Create custom settings");
		HashMap<String, Object> customScheduleSettingDTO = policy4SpogServer.createCustomScheduleDTO("1522068700422",
				"custom", "1", "true", "10", "minutes", test);

		HashMap<String, Object> scheduleSettingDTO = policy4SpogServer.createScheduleSettingDTO(cloudDirectScheduleDTO,
				null, null, null, test);

		ArrayList<HashMap<String, Object>> schedules = policy4SpogServer.createPolicyScheduleDTO(null, schedule_id,
				"1d", task_id, destination_id, scheduleSettingDTO, "06:00", "12:00", taskType, destination_name, test);
		ArrayList<HashMap<String, Object>> excludes = policy4SpogServer.createExcludeInfoDTO(null, "path", "c:\\tmp",
				test);

		HashMap<String, Object> perform_ar_test = policy4SpogServer.createPerformARTestOption("true", "true", "true",
				"true", test);

		HashMap<String, Object> retention_policy = policy4SpogServer.createRetentionPolicyOption("2", "2", "2", "2",
				test);

		HashMap<String, Object> udp_replication_from_remote_DTO = policy4SpogServer
				.createUdpReplicationFromRemoteInfoDTO(perform_ar_test, retention_policy, test);

		HashMap<String, Object> cloudDirectLocalBackupDTO = policy4SpogServer.createCloudDirectLocalBackupDTO("d:\\tmp",
				"true", test);

		HashMap<String, Object> cloudDirectFileBackupTaskInfoDTO = policy4SpogServer
				.createCloudDirectFileBackupTaskInfoDTO("d:\\tmp", cloudDirectLocalBackupDTO, excludes, test);
		HashMap<String, Object> cloudDirectimageBackupTaskInfoDTO = policy4SpogServer
				.createCloudDirectImageBackupTaskInfoDTO(drivers, cloudDirectLocalBackupDTO, test);
		ArrayList<HashMap<String, Object>> destinations = policy4SpogServer.createPolicyTaskDTO(null, task_id, taskType,
				destination_id, "none", null, cloudDirectFileBackupTaskInfoDTO, null, test);
		ArrayList<HashMap<String, Object>> throttles = policy4SpogServer.createPolicyThrottleDTO(null, throttle_id,
				task_id, throttle_type, "1200", "1", "06:00", "18:00", taskType, destination_id, destination_name,
				test);

		policy4SpogServer.setToken(validToken);
		// create cloud direct policy
		String policy_name = spogServer.ReturnRandom("ramya");
		response = policy4SpogServer.createPolicy(policy_name, policy_name, "cloud_direct_baas", null, "true",
				source_id, destinations, schedules, throttles, UUID.randomUUID().toString(), organization_id, test);
		String policy_id = response.then().extract().path("data.policy_id").toString();
		policy4SpogServer.checkPolicyDestinations(response, SpogConstants.SUCCESS_POST, destinations, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.CREATE_POLICY, response,
				policy_id, organizationEmail, "user", policy_name, "policy");
		auditdetails.add(auditEvents);

		/*
		 * //post job long start_time_ts = System.currentTimeMillis();
		 * test.log(LogStatus.INFO,
		 * "Create job for logged in user in organization : " +
		 * organizationType); response = gatewayServer.postJob(start_time_ts,
		 * System.currentTimeMillis()+10,organization_id,source_id, source_id,
		 * destination_id, destination_id, policy_id,"backup", "full",
		 * "failed",site_token, test); spogServer.checkResponseStatus(response,
		 * SpogConstants.SUCCESS_POST, test); String job_id =
		 * response.then().extract().path("data.job_id"); auditEvents =
		 * spogServer.composeJsonMap(user_id,organization_id,AuditCodeConstants.
		 * CREATE_JOB, response,user_id ); auditdetails.add(auditEvents);
		 * codedetails.add(auditEvents); //For userCodes
		 * usercodes.add(auditEvents);
		 * 
		 * 
		 * test.log(LogStatus.INFO,
		 * "update job  for logged in user in organization : " +
		 * organizationType); response =
		 * gatewayServer.updateJob(job_id,start_time_ts,
		 * System.currentTimeMillis(), organization_id, source_id, source_id,
		 * destination_id, destination_id, policy_id, "backup", "full",
		 * "finished",site_token, test);
		 * spogServer.checkResponseStatus(response,
		 * SpogConstants.SUCCESS_GET_PUT_DELETE, test); auditEvents =
		 * spogServer.composeJsonMap(user_id,organization_id,AuditCodeConstants.
		 * MODIFY_JOB, response,job_id ); auditdetails.add(auditEvents);
		 * 
		 * test.log(LogStatus.INFO,
		 * "create job data for logged in user in organization : " +
		 * organizationType); response = gatewayServer.postJobData(job_id, "1",
		 * "success", "100", "0", "0", "0", "0", "0",
		 * "0","0","none","none","none","none","none","none","none","none",
		 * site_token,test); spogServer.checkResponseStatus(response,
		 * SpogConstants.SUCCESS_GET_PUT_DELETE, test); auditEvents =
		 * spogServer.composeJsonMap(user_id,organization_id,AuditCodeConstants.
		 * CREATE_JOB_DATA, response,job_id ); auditdetails.add(auditEvents);
		 * codedetails.add(auditEvents); //For userCodes
		 * usercodes.add(auditEvents);
		 * 
		 * 
		 * //Ramya - update test.log(LogStatus.INFO,
		 * "update job data for logged in user in organization : " +
		 * organizationType); response =
		 * gatewayServer.updateJobData(job_id,job_id,"1", "success", "full",
		 * "finished",String.valueOf(start_time_ts) , "0", "0", "0", "0",
		 * "0",site_token, test); spogServer.checkResponseStatus(response,
		 * SpogConstants.SUCCESS_GET_PUT_DELETE, test); auditEvents =
		 * spogServer.composeJsonMap(user_id,organization_id,AuditCodeConstants.
		 * MODIFY_JOB_DATA, response,job_id ); auditdetails.add(auditEvents);
		 * codedetails.add(auditEvents); //For userCodes
		 * usercodes.add(auditEvents);
		 * 
		 * 
		 * test.log(LogStatus.INFO, "Create log"); response =
		 * gatewayServer.createLog(System.currentTimeMillis(),job_id,
		 * organization_id, organization_id,source_id,"information", "udp",
		 * "testLogMessage", "SPOQ,QA", "test",site_token, test); String log_id
		 * = response.then().extract().path("data.log_id"); auditEvents =
		 * spogServer.composeJsonMap(user_id,organization_id,AuditCodeConstants.
		 * CREATE_LOG, response,log_id ); auditdetails.add(auditEvents);
		 * codedetails.add(auditEvents); //For userCodes
		 * usercodes.add(auditEvents);
		 * 
		 * test.log(LogStatus.INFO, "update log"); response =
		 * log4GatewayServer.updateLog(System.currentTimeMillis(),log_id,
		 * job_id, organization_id, organization_id, source_id,"information",
		 * "udp", "testLogMessage", new String[] {"SPOQ","QA"},
		 * JobType4LatestJob.backup_full.toString(),sourceName,sourceName,"test"
		 * ,true, site_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		 * auditEvents =
		 * spogServer.composeJsonMap(user_id,organization_id,AuditCodeConstants.
		 * MODIFY_LOG, response,log_id ); auditdetails.add(auditEvents);
		 * codedetails.add(auditEvents); //For userCodes
		 * usercodes.add(auditEvents);
		 * 
		 * test.log(LogStatus.INFO, "Delete the logs by log Id");
		 * spogServer.deletelogbylogId(validToken, log_id,
		 * SpogConstants.SUCCESS_GET_PUT_DELETE,
		 * SpogMessageCode.SUCCESS_GET_PUT_DEL, test); auditEvents =
		 * spogServer.composeJsonMap(user_id,organization_id,AuditCodeConstants.
		 * DELETE_LOG, response,log_id ); auditdetails.add(auditEvents);
		 * codedetails.add(auditEvents); //For userCodes
		 * usercodes.add(auditEvents);
		 */
		test.log(LogStatus.INFO, "update the sitename");
		response = spogServer.updateSiteById(site_id, "test", validToken);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.MODIFY_SITE, response,
				site_id, organizationEmail, "user", "test", "site");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		// creating recovered resourcefilters
		test.log(LogStatus.INFO, "creating recovered resourcefilters");
		filter_name = spogServer.ReturnRandom("ramya");

		// HashMap<String,Object>
		// compose_filter=spogRecoveredResourceServer.composeExpectedRecoveredResourcesFilter(policy_id,State.get(1),os_major,recoveredResourceType.get(1),filter_name,"true");
		response = spogRecoveredResourceServer.createSpecifiedUserRecoveredResourcesFilters(user_id, validToken,
				policy_id, State.get(1), os_major, recoveredResourceType.get(1), filter_name, "true", "", test);
		filter_id = response.then().extract().path("data.filter_id");
		test.log(LogStatus.INFO, "validating the response for creating recovered resourcefilters");
		spogRecoveredResourceServer.checkRecoveredResourceFilters(response, filter_id, policy_id, State.get(1),
				os_major, recoveredResourceType.get(1), filter_name, "true", organization_id, user_id,
				SpogConstants.SUCCESS_POST, SpogMessageCode.SUCCESS_POST, test, filtersHeadContent);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id,
				AuditCodeConstants.CREATE_RECOVEREDRESOURCE_FILTER, response, filter_id, organizationEmail, "user",
				filter_name, "recovered_resource_filter");
		auditdetails.add(auditEvents);

		// update recovered resourcefilters
		test.log(LogStatus.INFO, "update recovered resourcefilters");
		response = spogRecoveredResourceServer.updateSpecifiedUserRecoveredResourcesFilters(user_id, validToken,
				filter_id, policy_id, State.get(1), os_major, recoveredResourceType.get(1), filter_name, "true",
				SpogConstants.SUCCESS_GET_PUT_DELETE, "", test);
		test.log(LogStatus.INFO, "validating the response for get recovered resourcefilters");
		spogRecoveredResourceServer.checkRecoveredResourceFilters(response, filter_id, policy_id, State.get(1),
				os_major, recoveredResourceType.get(1), filter_name, "true", organization_id, user_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test, filtersHeadContent
				);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id,
				AuditCodeConstants.MODIFY_RECOVEREDRESOURCE_FILTER, response, filter_id, organizationEmail, "user",
				filter_name, "recovered_resource_filter");
		auditdetails.add(auditEvents);

		// delete recovered resourcefilters
		test.log(LogStatus.INFO, "delete recovered resourcefilters");
		response = spogRecoveredResourceServer.deleteSpecifiedUserRecoveredResourcesFiltersByFilterId(user_id,
				filter_id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		test.log(LogStatus.INFO, "validating the response for delete recovered resourcefilters");
		auditEvents = spogServer.composeJsonMap(user_id, organization_id,
				AuditCodeConstants.DELETE_RECOVEREDRESOURCE_FILTER, response, filter_id, organizationEmail, "user",
				filter_name, "recovered_resource_filter");
		auditdetails.add(auditEvents);

		// creating recovered resource columns
		test.log(LogStatus.INFO, "creating recovered resource columns");
		ArrayList<HashMap<String, Object>> expected_columns = new ArrayList<HashMap<String, Object>>();
		ArrayList<HashMap<String, Object>> defaultcolumnresponse = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> temp1 = new HashMap<String, Object>();
		temp1 = spogRecoveredResourceServer.composeRecoveredResource_Column("84129da7-d794-4cc5-bfe1-17daf6b911bb",
				"false", "false", "true", "1");
		expected_columns.add(temp1);
		response = spogRecoveredResourceServer.createRecoveredResourceColumnsForSpecifiedUser(user_id, validToken,
				expected_columns, test);
		test.log(LogStatus.INFO, "validating the response for creating recovered resource columns");
		auditEvents = spogServer.composeJsonMap(user_id, organization_id,
				AuditCodeConstants.CREATE_USER_RECOVEREDRESOURCE_COLUMN, response, user_id, organizationEmail, "user",
				organizationEmail, "user");
		auditdetails.add(auditEvents);

		// update recovered resource columns
		test.log(LogStatus.INFO, "update recovered resource columns");
		response = spogRecoveredResourceServer.updateRecoveredResourceColumnsByUserId(user_id, validToken,
				expected_columns, defaultcolumnresponse, SpogConstants.SUCCESS_GET_PUT_DELETE,
				SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		test.log(LogStatus.INFO, "validating the response for updating recovered resource columns");
		auditEvents = spogServer.composeJsonMap(user_id, organization_id,
				AuditCodeConstants.MODIFY_USER_RECOVEREDRESOURCE_COLUMN, response, user_id, organizationEmail, "user",
				organizationEmail, "user");
		auditdetails.add(auditEvents);

		// delete recovered resource columns
		test.log(LogStatus.INFO, "delete recovered resource columns");
		spogRecoveredResourceServer.deleteRecoveredResourceColumnsforSpecifiedUserwithCheck(user_id, validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		test.log(LogStatus.INFO, "validating the response for deleting recovered resource columns");
		auditEvents = spogServer.composeJsonMap(user_id, organization_id,
				AuditCodeConstants.DELETE_USER_RECOVEREDRESOURCE_COLUMN, response3, user_id, organizationEmail, "user",
				organizationEmail, "user");
		auditdetails.add(auditEvents);

		// create user policy filter
		test.log(LogStatus.INFO, "create user policy filter");
		userSpogServer.setToken(validToken);
		filter_name = "Finished succesfully";
		response = userSpogServer.createPolicyFilterForSpecificUserWithCheck_audit(user_id, filter_name, policy_name,
				UUID.randomUUID().toString(), "finished", "failure", "true", test);
		test.log(LogStatus.INFO, "validating the response for create user policy filter");
		filter_id = response.then().extract().path("data.filter_id");
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.CREATE_POLICY_FILTER,
				response, filter_id, organizationEmail, "user", filter_name, "policy_filter");
		auditdetails.add(auditEvents);

		// update user policy filter
		test.log(LogStatus.INFO, "update user policy filter");
		userSpogServer.setToken(validToken);
		/* filter_name="Finished succesfully"; */
		response = userSpogServer.updatePolicyFilterForSpecificUserWithCheck_audit(user_id, filter_id, filter_name,
				"policy_name", UUID.randomUUID().toString(), "finished", "failure", "true", test);
		test.log(LogStatus.INFO, "validating the response for create user policy filter");
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.MODIFY_POLICY_FILTER,
				response, filter_id, organizationEmail, "user", filter_name, "policy_filter");
		auditdetails.add(auditEvents);

		// update user policy filter
		test.log(LogStatus.INFO, "delete user policy filter");
		userSpogServer.setToken(validToken);
		userSpogServer.deleteSpecificPolicyFilterForSpecificUser(user_id, filter_id, test);
		test.log(LogStatus.INFO, "validating the response for create user policy filter");
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.DELETE_POLICY_FILTER,
				response3, filter_id, organizationEmail, "user", filter_name, "policy_filter");
		auditdetails.add(auditEvents);

		// create user policy columns
		test.log(LogStatus.INFO, "create user policy columns");
		policy4SpogServer.setToken(validToken);
		ArrayList<HashMap<String, Object>> expected_columns_1 = new ArrayList<HashMap<String, Object>>();
		expected_columns_1 = policy4SpogServer.getPolicyColumnArrayList("Name;true;1", validToken, test);
		response = policy4SpogServer.createUsersPolicyColumns(user_id, expected_columns_1, validToken);
		test.log(LogStatus.INFO, "validating the response for create user policy columns");
		policy4SpogServer.checkUsersPolicyColumns(response, expected_columns_1, validToken, SpogConstants.SUCCESS_POST,
				null);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.CREATE_USER_POLICY_COLUMN,
				response, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);

		// update user policy columns
		test.log(LogStatus.INFO, "update user policy columns");
		response = policy4SpogServer.updateUsersPolicyColumns(user_id, expected_columns_1, validToken);
		test.log(LogStatus.INFO, "validating the response for create user policy columns");
		policy4SpogServer.checkUsersPolicyColumns(response, expected_columns_1, validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.MODIFY_USER_POLICY_COLUMN,
				response, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);

		// delete user policy columns
		test.log(LogStatus.INFO, "delete user policy columns");
		policy4SpogServer.setToken(validToken);
		policy4SpogServer.deleteUsersPolicyColumnsWithCheck(user_id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE,
				null);
		test.log(LogStatus.INFO, "validating the response for create user policy columns");
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.DELETE_USER_POLICY_COLUMN,
				response3, user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);

		// create report list filters for specified user
		test.log(LogStatus.INFO, "create report list filters for specified user");
		spogServer.setToken(validToken);
		// orgId for creating source_groups
		String orgId = null;
		String groupName = spogServer.ReturnRandom("ramya");
		orgId = spogServer.GetLoggedinUserOrganizationID();
		String source_group_ids = null, source_group_id = null;
		for (int i = 0; i < 1; i++) {
			source_group_id = spogServer.createGroupWithCheck2(validToken, orgId, groupName + i, groupName, test);
			if (i > 0) {
				source_group_ids = source_group_ids + "," + source_group_id;
			} else {
				source_group_ids = source_group_id;
			}
		}
		spogReportServer.setToken(validToken);
		filter_name = spogServer.ReturnRandom("filter");
		HashMap<String, Object> expectedfilter = spogReportServer.composeReportListFilterInfo(filter_name,
				"last_7_days", "all_sources", "", organization_id, "weekly", System.currentTimeMillis(),
				System.currentTimeMillis(), filter_name, true);
		response = spogReportServer.createReportListFiltersForSpecifiedUserWithCheck(validToken, user_id,
				organization_id, expectedfilter, SpogConstants.SUCCESS_POST, SpogMessageCode.SUCCESS_POST, test);
		filter_id = response.then().extract().path("data.filter_id");
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.CREATE_REPORT_LIST_FILTER,
				response, filter_id, organizationEmail, "user", filter_name, "report_list_filter");
		auditdetails.add(auditEvents);

		// update report list filters for specified user
		test.log(LogStatus.INFO, "update report list filters for specified user");
		// filter_name=spogServer.ReturnRandom("filter");
		response = spogReportServer.updateReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id,
				organization_id, filter_id, expectedfilter, SpogConstants.SUCCESS_GET_PUT_DELETE,
				SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.MODIFY_REPORT_LIST_FILTER,
				response, filter_id, organizationEmail, "user", filter_name, "report_list_filter");
		auditdetails.add(auditEvents);

		// delete report list filters for specified user
		test.log(LogStatus.INFO, "delete report list filters for specified user");
		spogReportServer.deleteReportListFiltersForSpecifiedUserByFilterIdWithCheck(validToken, user_id, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.DELETE_REPORT_LIST_FILTER,
				response3, filter_id, organizationEmail, "user", filter_name, "report_list_filter");
		auditdetails.add(auditEvents);

		// create report filter
		test.log(LogStatus.INFO, "create  report filter");
		HashMap<String, Object> reportfilterInfo = null;
		test.log(LogStatus.INFO, "compose report filter");
		filter_name = spogServer.ReturnRandom("filter");
		reportfilterInfo = spogReportServer.composeReportFilterInfo(sourceName, destination_name, policy_id,
				destination_id, source_group_id, "", "today", System.currentTimeMillis(), System.currentTimeMillis(),
				"backup_jobs", "", "backup", "", "", filter_name, true, "backup_jobs");
		/*
		 * composeReportListFilterInfo("report_name","today","backup_jobs",
		 * source_group_id, organization_id,
		 * "",String.valueOf(System.currentTimeMillis()),String.valueOf(System.
		 * currentTimeMillis()), filter_name, true, "backup_jobs");
		 */ response = spogReportServer.createReportFilterForSpecifiedUserWithCheck_audit(user_id, organization_id,
				validToken, reportfilterInfo, SpogConstants.SUCCESS_POST, null, test);
		filter_id = response.then().extract().path("data.filter_id");
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.CREATE_REPORT_FILTER,
				response, filter_id, organizationEmail, "user", filter_name, "report_filter");
		auditdetails.add(auditEvents);

		// update report filter
		test.log(LogStatus.INFO, "update  report filter");
		/* filter_name=spogServer.ReturnRandom("filter"); */
		reportfilterInfo = spogReportServer.composeReportFilterInfo(sourceName, destination_name, policy_id,
				destination_id, source_group_id, "", "today", System.currentTimeMillis(), System.currentTimeMillis(),
				"backup_jobs", "", "backup", "", "", filter_name, true, "backup_jobs");
		response = spogReportServer.updateReportFilterForSpecifiedUserWithCheck(user_id, filter_id, organization_id,
				validToken, reportfilterInfo, reportfilterInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.MODIFY_REPORT_FILTER,
				response, filter_id, organizationEmail, "user", filter_name, "report_filter");
		auditdetails.add(auditEvents);

		// delete report filter
		test.log(LogStatus.INFO, "delete report filter");
		response = spogReportServer.deleteReportFiltersForSpecifiedUser(validToken, user_id, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.DELETE_REPORT_FILTER,
				response3, filter_id, organizationEmail, "user", filter_name, "report_filter");
		auditdetails.add(auditEvents);

		// create backup job report columns
		test.log(LogStatus.INFO, "create backup job report columns");
		ArrayList<HashMap<String, Object>> expected_columns_2 = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> temp2 = new HashMap<>();
		for (int j = 0; j < 1; j++) {
			temp2 = spogReportServer.composeBackUpJobReportsColumns("b9004719-1897-4f09-abd2-7a9fb6edb768", "true",
					"3");

			expected_columns_2.add(temp2);
		}

		response = spogReportServer.createBackUpJobReportsColumnsForSpecifiedUser(validToken, user_id,
				expected_columns_2, SpogConstants.SUCCESS_POST, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id,
				AuditCodeConstants.CREATE_USER_BACKUPJOBREPORT_COLUMN, response, user_id, organizationEmail, "user",
				organizationEmail, "user");
		auditdetails.add(auditEvents);
		expected_columns_2.clear();

		// update backup job report columns
		test.log(LogStatus.INFO, "update backup job report columns");
		for (int j = 0; j < 1; j++) {
			temp2 = spogReportServer.composeBackUpJobReportsColumns("b9004719-1897-4f09-abd2-7a9fb6edb768", "true",
					"4");

			expected_columns_2.add(temp2);
		}

		response = spogReportServer.updateBackUpJobReportsColumnsForSpecifiedUser(validToken, user_id,
				expected_columns_2, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id,
				AuditCodeConstants.MODIFY_USER_BACKUPJOBREPORT_COLUMN, response, user_id, organizationEmail, "user",
				organizationEmail, "user");
		auditdetails.add(auditEvents);

		// delete BackUpJobReportsColumnsForSpecifiedUser
		test.log(LogStatus.INFO, "delete BackUpJobReportsColumnsForSpecifiedUser ");
		spogReportServer.deleteBackUpJobReportsColumnsForSpecifiedUser(validToken, user_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id,
				AuditCodeConstants.DELETE_USER_BACKUPJOBREPORT_COLUMN, response3, user_id, organizationEmail, "user",
				organizationEmail, "user");
		auditdetails.add(auditEvents);

		// create restore job report columns
		test.log(LogStatus.INFO, "create restore job report columns");
		spogReportServer.setToken(validToken);
		expected_columns_2.clear();
		for (int j = 0; j < 1; j++) {
			temp2 = spogReportServer.composeBackUpJobReportsColumns("07871b87-6ec5-44ae-a1bb-17abfb75a9f9", "true",
					"3");

			expected_columns_2.add(temp2);
		}

		response = spogReportServer.createRestoreJobReportsColumnsForSpecifiedUser(user_id, validToken,
				expected_columns_2, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id,
				AuditCodeConstants.CREATE_USER_RESTOREJOBREPORT_COLUMN, response, user_id, organizationEmail, "user",
				organizationEmail, "user");
		auditdetails.add(auditEvents);
		expected_columns_2.clear();

		// update restore job report columns
		test.log(LogStatus.INFO, "update restore job report columns");
		for (int j = 0; j < 1; j++) {
			temp2 = spogReportServer.composeRestoreJobReports_Column("07871b87-6ec5-44ae-a1bb-17abfb75a9f9", "true",
					"false", "true", "4");

			expected_columns_2.add(temp2);
		}

		response = spogReportServer.updateRestoreJobReportsColumnsByUserId(user_id, validToken, expected_columns_2,
				expected_columns_2, SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id,
				AuditCodeConstants.MODIFY_USER_RESTOREJOBREPORT_COLUMN, response, user_id, organizationEmail, "user",
				organizationEmail, "user");
		auditdetails.add(auditEvents);

		// delete RestoreJobReportsColumnsForSpecifiedUser
		test.log(LogStatus.INFO, "delete RestoreJobReportsColumnsForSpecifiedUser ");
		spogReportServer.deleteRestoreJobReportsColumnsforSpecifiedUserwithCheck(user_id, validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id,
				AuditCodeConstants.DELETE_USER_RESTOREJOBREPORT_COLUMN, response3, user_id, organizationEmail, "user",
				organizationEmail, "user");
		auditdetails.add(auditEvents);

		// create report schedule
		test.log(LogStatus.INFO, "create report schedule ");
		HashMap<String, Object> scheduleInfo = new HashMap<String, Object>();
		scheduleInfo = spogReportServer.composeReportScheduleInfo("report_name", "backup_jobs", "last_7_days",
				System.currentTimeMillis(), System.currentTimeMillis(), "daily", "", organization_id,
				"a@gmail.com,b@gmail.com", " 0 0 19 ? * *", "all_sources");
		response = spogReportServer.createReportScheduleWithCheck_audit(validToken, scheduleInfo,
				SpogConstants.SUCCESS_POST, null, test);
		schedule_id = response.then().extract().path("data.schedule_id");
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.CREATE_REPORT_SCHEDULE,
				response, user_id, organizationEmail, "user", schedule_id, "report_schedule");
		auditdetails.add(auditEvents);

		// modify report schedule
		test.log(LogStatus.INFO, "modify report schedule ");
		response = spogReportServer.updateReportScheduleByIdWithCheck(schedule_id, validToken, scheduleInfo,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.MODIFY_REPORT_SCHEDULE,
				response, user_id, organizationEmail, "user", schedule_id, "report_schedule");
		auditdetails.add(auditEvents);

		// delete report schedule
		test.log(LogStatus.INFO, "modify report schedule ");
		spogReportServer.deleteReportScheduleByIdWithCheck(schedule_id, validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.DELETE_REPORT_SCHEDULE,
				response3, user_id, organizationEmail, "user", schedule_id, "report_schedule");
		auditdetails.add(auditEvents);

		// update a source
		test.log(LogStatus.INFO, "update the existing source with check");
		sourceName = "source_name_1";
		response = spogServer.updateSourcebysourceId(source_id, sourceName, SourceType.machine,
				SourceProduct.cloud_direct, organization_id, site_id, "", ProtectionStatus.unprotect,
				ConnectionStatus.online, "windows", validToken, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		source_id = response.then().extract().path("data.source_id");
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.MODIFY_SOURCE, response,
				source_id, organizationEmail, "user", sourceName, "source");
		auditdetails.add(auditEvents);

		test.log(LogStatus.INFO, "Create a source group");
		groupName = "audit-source-group";
		response = spogServer.createGroup(organization_id, groupName, "audit purpose");
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		String sourcegroup_id = response.then().extract().path("data.group_id");
		auditEvents = spogServer.storetheuserevents(AuditCodeConstants.create_source_group, organization_id, user_id,
				sourcegroup_id, response, organizationEmail, "user", groupName, "source_group");
		auditdetails.add(auditEvents);

		test.log(LogStatus.INFO, "Modify source group");
		groupName = "audit-mod-sgrp";
		response = spogServer.updateSourceGroup(sourcegroup_id, groupName, "modify source group");
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		auditEvents = spogServer.storetheuserevents(AuditCodeConstants.modify_source_group, organization_id, user_id,
				sourcegroup_id, response, organizationEmail, "user", groupName, "source_group");
		auditdetails.add(auditEvents);

		test.log(LogStatus.INFO, "Add source to source group");
		response = spogServer.addSourcetoSourceGroupwithCheck(sourcegroup_id, new String[] { source_id }, validToken,
				SpogConstants.SUCCESS_POST, null, test);
		auditEvents = spogServer.storetheuserevents(AuditCodeConstants.add_source_to_group, organization_id, user_id,
				sourcegroup_id, response, organizationEmail, "user", groupName, "source_group");
		auditdetails.add(auditEvents);

		test.log(LogStatus.INFO, "Delete source from source group");
		spogServer.deleteSourcefromSourceGroupwithCheck(sourcegroup_id, source_id, validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		auditEvents = spogServer.storetheuserevents(AuditCodeConstants.delete_source_from_group, organization_id,
				user_id, sourcegroup_id, response1, organizationEmail, "user", groupName, "source_group");
		auditdetails.add(auditEvents);

		test.log(LogStatus.INFO, "Delete source group");
		spogServer.deleteGroup(sourcegroup_id, test);
		auditEvents = spogServer.storetheuserevents(AuditCodeConstants.delete_source_group, organization_id, user_id,
				sourcegroup_id, response1, organizationEmail, "user", groupName, "source_group");
		auditdetails.add(auditEvents);

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		throttles = policy4SpogServer.createPolicyThrottleDTO(null, throttle_id, task_id, throttle_type, "1200", "1",
				"06:00", "18:00", taskType, destination_id, destination_name, test);
		response = policy4SpogServer.updatePolicy(policy_name, policy_name, "cloud_direct_baas", null, "true",
				source_id, destinations, schedules, throttles, policy_id, organization_id, validToken, test);
		policy4SpogServer.checkPolicyDestinations(response, SpogConstants.SUCCESS_GET_PUT_DELETE, destinations, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.MODIFY_POLICY, response,
				policy_id, organizationEmail, "user", policy_name, "policy");
		auditdetails.add(auditEvents);

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// delete policy by id
		policy4SpogServer.deletePolicybyPolicyId(validToken, policy_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.DELETE_POLICY, response3,
				policy_id, organizationEmail, "user", policy_name, "policy");
		auditdetails.add(auditEvents);

		// delete the site
		test.log(LogStatus.INFO, "Delete the site " + site_id);
		response = spogServer.deleteSite(site_id, validToken);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.DELETE_SITE, response3,
				site_id, organizationEmail, "user", newname, "site");
		auditdetails.add(auditEvents);
		codedetails.add(auditEvents);

		usercodes.add(auditEvents);

		// Deleting the source by id
		test.log(LogStatus.INFO, "delete source by id");
		response = spogServer.deleteSourcesById(validToken, source_id, test);
		test.log(LogStatus.INFO, "validating the response for the delete sourcses by id");
		spogServer.deleteSourcesWithCheck(response, SpogConstants.SUCCESS_GET_PUT_DELETE,
				SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.DELETE_SOURCE, response,
				source_id, organizationEmail, "user", sourceName, "source");
		auditdetails.add(auditEvents);

		/*
		 * // for delete destination
		 * 
		 * destination_name = "destDedupder_test"; // create destination
		 * cloud_direct spogDestinationServer.setToken(validToken); response =
		 * spogDestinationServer.createDestination(UUID.randomUUID().toString(),
		 * organization_id, cloud_account_id,
		 * "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c",
		 * DestinationType.cloud_direct_volume.toString(),
		 * DestinationStatus.running.toString(), "20", cloud_account_id,
		 * "normal", RandomStringUtils.randomAlphanumeric(4) + "host-t", "7D",
		 * "7 Days", "0", "0", "0", "0", "0", "0", "0", "0", "0", "0",
		 * destination_name, test); destination_id =
		 * response.then().extract().path("data.destination_id");
		 */

		for (int i = 0; i < 2; i++) {
			if (i == 1) {
				validToken = csr_read_only_validToken;
			}
			// check destination Audits
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;601";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;603";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// check report schedule
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;4014";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// check report schedule
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;4015|4016";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// check backupJobReportsColumnsForSpecifiedUser
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;2025|2026|2027";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Preparing the URL for Filtering  ");
			filter = "code_id;in;1|2|3";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by userid " + user_id);
			userresponse = compose_expected_response(filter, usercodes);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			test.log(LogStatus.INFO, "Preparing the URL for Filtering  ");
			filter = "code_id;in;4|5";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by userid " + user_id);
			userresponse = compose_expected_response(filter, usercodes);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			test.log(LogStatus.INFO, "Preparing the URL for Filtering  ");
			filter = "code_id;in;6|7";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;desc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by userid " + user_id);
			userresponse = compose_expected_response(filter, usercodes);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;desc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			// Array of codes filtering based on descending order For
			// organizations
			test.log(LogStatus.INFO, "Preparing the URL for Filtering  ");
			filter = "code_id;in;108";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;desc", 1, 20, test);
			userresponse = compose_expected_response(filter, orgcodes);
			test.log(LogStatus.INFO, "Get the audit trail by userid " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;desc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			// Array of codes filtering based on descending order For
			// organizations
			test.log(LogStatus.INFO, "Preparing the URL for Filtering  ");
			filter = "code_id;in;105";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, orgcodes);
			test.log(LogStatus.INFO, "Get the audit trail by userid " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			// Array of codes filtering based on descending order For
			// organizations
			test.log(LogStatus.INFO, "Preparing the URL for Filtering  ");
			filter = "code_id;in;107";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;desc", 1, 20, test);
			userresponse = compose_expected_response(filter, orgcodes);
			test.log(LogStatus.INFO, "Get the audit trail by userid " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;desc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			// Array of codes filtering based on descending order For
			// organizations
			test.log(LogStatus.INFO, "Preparing the URL for Filtering  ");
			filter = "code_id;in;106";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, orgcodes);
			test.log(LogStatus.INFO, "Get the audit trail by userid " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			// check RestoreJobReportsColumnsForSpecifiedUser
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;2028|2029|2030";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// check report filters
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;1028|1029|1030";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// check list report filters
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;1025|1026|1027";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// create policy Audits
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;4010";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,filter
			// ,
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// create policy Audits
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;4011|4012";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// Filtering based on code_id and ascending order sorting
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;1019|1020|1021";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,filter
			// ,
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// Filtering based on code_id and ascending order sorting
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;2019|2020|2021";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// Filtering based on code_id and ascending order sorting
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;1022|1023|1024";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20,filter
			// ,
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// Filtering based on code_id and ascending order sorting
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;2022|2023|2024";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// Filtering based on code_id and ascending order sorting
			test.log(LogStatus.INFO, "Preparing the URL based on code_id");
			filter = "code_id;=;1004|1005|1006";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// sorting based on Descending order
			test.log(LogStatus.INFO, "Preparing the URL for sorting based on descending order ");
			filter = "code_id;=;1001|1002|1003";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20,filter,
			// "create_ts;desc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// Sorting based on ascending order

			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;1007|1008|1009";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			// sorting and pagination Ascending order
			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;1010|1011|1012";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			// sorting and pagination Ascending order
			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;1013|1014|1015";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// sorting and pagination Descending order
			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;1016|1017|1018";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// sorting and pagination Descending order
			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;2001|2002|2003";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// sorting and pagination Descending order
			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;2004|2005|2006";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;2007|2008|2009";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// sorting and pagination Ascending order
			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;2010|2011|2012";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// sorting and pagination Ascending order
			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;2013|2014|2015";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// sorting and pagination Descending order
			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;2016|2017|2018";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 20, filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			// Array of code id and filtering the url on usercodes

			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;4006|4007";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			test.log(LogStatus.INFO, "Preparing the URL for sorting ");
			filter = "code_id;=;4008|4009";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter, auditdetails);
			test.log(LogStatus.INFO, "Get the audit trail by userId " + user_id);
			response = spogServer.getAuditDetailsForUsers(validToken, user_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

		}

		// Deleting the organization and recycle the cloud direct volume
		spogDestinationServer.setToken(csr_token);
		spogDestinationServer.recycleCloudVolumesAndDelOrg(organization_id, test);
	}

	@Test(dataProvider = "organizationAndUserInfo1")
	public void getAuditTrailForDirectwithinvalid_id(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName, String organizationLastName,
			String userEmail, String userPassword, String FirstName, String LastName, String Role_Id) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Ramya Nagepalli");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;
		FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + FirstName;
		LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + LastName;
		userEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;
		ArrayList<HashMap> auditdetails = new ArrayList<HashMap>();
		HashMap<String, Object> auditEvents = new HashMap<String, Object>();

		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		String organization_id = spogServer.CreateOrganizationWithCheck(organizationName + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "The organization id is " + organization_id);
		// login with direct organization
		test.log(LogStatus.INFO, "Logging with direct organization user");
		spogServer.userLogin(organizationEmail, common_password);
		String validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "Get the logged in user ");
		Response response = spogServer.getLoggedInUser(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		spogServer.checkLoggedInUser(response, SpogConstants.SUCCESS_GET_PUT_DELETE,
				SpogMessageCode.SUCCESS_GET_PUT_DEL, organizationEmail, test);
		String user_id = response.then().extract().path("data.user_id");
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.LOGIN_USER, response,
				user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);

		// create a user under direct organization
		test.log(LogStatus.INFO, "creating a direct user under the msp organization");

		Response response1 = spogServer.createUser(userEmail, userPassword, FirstName, LastName, Role_Id,
				organization_id, test);
		String user_id1 = spogServer.checkCreateUser(response1, SpogConstants.SUCCESS_POST, userEmail, FirstName,
				LastName, Role_Id, organization_id, "", test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.CREATE_USER, response1,
				user_id1, organizationEmail, "user", userEmail, "user");
		auditdetails.add(auditEvents);

		test.log(LogStatus.INFO, "Preparing the url for the direct organization :");
		String additionalUrl = spogServer.PrepareURL("", "create_ts;asc", 1, 20, test);
		String invalid_user_id = UUID.randomUUID().toString();

		test.log(LogStatus.INFO, "providing the invalid userId to get the details");
		response = spogServer.getAuditDetailsForUsers(validToken, invalid_user_id, additionalURL, test);
		System.out.println("The value of the response is :" + response.getBody().asString());
		test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
		spogServer.checkaudittraildata1(response, SpogConstants.RESOURCE_NOT_EXIST, auditdetails, 1, 20, "",
				"create_ts;asc", SpogMessageCode.USER_ID_DOESNOT_EXIST, test);

		// Deleting the direct organization
		test.log(LogStatus.INFO, "Login in as csr admin to delete the organization");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		spogServer.DeleteOrganizationWithCheck(organization_id, test);

	}

	// Direct user audit Tracking with invalid JWTToken

	@Test(dataProvider = "organizationAndUserInfo1")
	public void getAuditTrailForDirectwithinvalid_JWTtoken(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName, String organizationLastName,
			String userEmail, String userPassword, String FirstName, String LastName, String Role_Id) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Ramya Nagepalli");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;
		FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + FirstName;
		LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + LastName;
		userEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;
		ArrayList<HashMap> auditdetails = new ArrayList<HashMap>();
		HashMap<String, Object> auditEvents = new HashMap<String, Object>();

		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		String organization_id = spogServer.CreateOrganizationWithCheck(organizationName + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "The organization id is " + organization_id);
		// login with direct organization
		test.log(LogStatus.INFO, "Logging with direct organization user");
		spogServer.userLogin(organizationEmail, common_password);
		String validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "Get the logged in user ");
		Response response = spogServer.getLoggedInUser(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		spogServer.checkLoggedInUser(response, SpogConstants.SUCCESS_GET_PUT_DELETE,
				SpogMessageCode.SUCCESS_GET_PUT_DEL, organizationEmail, test);
		String user_id = response.then().extract().path("data.user_id");
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.LOGIN_USER, response,
				user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);

		// create a user under direct organization
		test.log(LogStatus.INFO, "creating a direct user under the msp organization");

		Response response1 = spogServer.createUser(userEmail, userPassword, FirstName, LastName, Role_Id,
				organization_id, test);
		String user_id1 = spogServer.checkCreateUser(response1, SpogConstants.SUCCESS_POST, userEmail, FirstName,
				LastName, Role_Id, organization_id, "", test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.CREATE_USER, response1,
				user_id1, organizationEmail, "user", userEmail, "user");
		auditdetails.add(auditEvents);

		test.log(LogStatus.INFO, "Preparing the url for the direct organization :");
		String additionalUrl = spogServer.PrepareURL("", "create_ts;asc", 1, 20, test);
		// Invalid Token:
		String invalidToken = validToken + "Junk";

		test.log(LogStatus.INFO, "providing the invalid userId to get the details");
		response = spogServer.getAuditDetailsForUsers(invalidToken, user_id, additionalURL, test);
		System.out.println("The value of the response is :" + response.getBody().asString());
		test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
		spogServer.checkaudittraildata1(response, SpogConstants.NOT_LOGGED_IN, auditdetails, 1, 20, "", "create_ts;asc",
				SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);

		// Deleting the direct organization
		test.log(LogStatus.INFO, "Login in as csr admin to delete the organization");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		String csr_token = spogServer.getJWTToken();
		spogServer.DeleteOrganizationWithCheck(organization_id, test);

		spogDestinationServer.setToken(csr_token);
		/*
		 * spogDestinationServer.recycleCloudVolumesAndDelOrg(
		 * "f4fd373c-2c53-429a-b00b-45338421c901", test);
		 * spogDestinationServer.recycleCloudVolumesAndDelOrg(
		 * "9fc38c85-e2ac-4f25-9c0f-009c98e5c0d8", test);
		 */

	}
	// Audit Tracking For Msp user by proving invalid JWT Token

	@Test(dataProvider = "getOrganizationMsp")
	public void getAuditTrailForMspwithinvalid_JWTtoken(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName, String organizationLastName,
			String userEmail, String userPassword, String FirstName, String LastName, String Role_Id) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Ramya Nagepalli");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;
		FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + FirstName;
		LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + LastName;
		userEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;
		ArrayList<HashMap> auditdetails = new ArrayList<HashMap>();
		HashMap<String, Object> auditEvents = new HashMap<String, Object>();

		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		String organization_id = spogServer.CreateOrganizationWithCheck(organizationName + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "The organization id is " + organization_id);
		// login with direct organization
		test.log(LogStatus.INFO, "Logging with msp organization user");
		spogServer.userLogin(organizationEmail, common_password);
		String validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "Get the logged in user ");
		Response response = spogServer.getLoggedInUser(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		spogServer.checkLoggedInUser(response, SpogConstants.SUCCESS_GET_PUT_DELETE,
				SpogMessageCode.SUCCESS_GET_PUT_DEL, organizationEmail, test);
		String user_id = response.then().extract().path("data.user_id");
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.LOGIN_USER, response,
				user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);

		// create a user under msp organization
		test.log(LogStatus.INFO, "creating a msp user under the msp organization");

		Response response1 = spogServer.createUser(userEmail, userPassword, FirstName, LastName, Role_Id,
				organization_id, test);
		String user_id1 = spogServer.checkCreateUser(response1, SpogConstants.SUCCESS_POST, userEmail, FirstName,
				LastName, Role_Id, organization_id, "", test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.CREATE_USER, response1,
				user_id1, organizationEmail, "user", userEmail, "user");
		auditdetails.add(auditEvents);

		test.log(LogStatus.INFO, "Preparing the url for the direct organization :");
		String additionalUrl = spogServer.PrepareURL("", "create_ts;asc", 1, 20, test);
		// Invalid Token:
		String invalidToken = validToken + "Junk";

		test.log(LogStatus.INFO, "providing the invalid userId to get the details");
		response = spogServer.getAuditDetailsForUsers(invalidToken, user_id, additionalURL, test);
		System.out.println("The value of the response is :" + response.getBody().asString());
		test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
		spogServer.checkaudittraildata1(response, SpogConstants.NOT_LOGGED_IN, auditdetails, 1, 20, "", "create_ts;asc",
				SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);

		// Deleting the direct organization
		test.log(LogStatus.INFO, "Login in as csr admin to delete the msp organization");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		spogServer.DeleteOrganizationWithCheck(organization_id, test);

	}

	// invalid csr admin JWT token
	@Test(dataProvider = "getOrganizationMsp")
	public void getAuditTrailForMspwithcsrInvalid_JWTtoken(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName, String organizationLastName,
			String userEmail, String userPassword, String FirstName, String LastName, String Role_Id) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Ramya Nagepalli");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;
		FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + FirstName;
		LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + LastName;
		userEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;
		ArrayList<HashMap> auditdetails = new ArrayList<HashMap>();
		HashMap<String, Object> auditEvents = new HashMap<String, Object>();

		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		test.log(LogStatus.INFO, "Getting the jwt token for csr admin user ");
		String csrToken = spogServer.getJWTToken();
		String organization_id = spogServer.CreateOrganizationWithCheck(organizationName + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "The organization id is " + organization_id);
		// login with direct organization
		test.log(LogStatus.INFO, "Logging with msp organization user");
		spogServer.userLogin(organizationEmail, common_password);
		String validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "Get the logged in user ");
		Response response = spogServer.getLoggedInUser(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		spogServer.checkLoggedInUser(response, SpogConstants.SUCCESS_GET_PUT_DELETE,
				SpogMessageCode.SUCCESS_GET_PUT_DEL, organizationEmail, test);
		String user_id = response.then().extract().path("data.user_id");
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.LOGIN_USER, response,
				user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);

		// create a user under msp organization
		test.log(LogStatus.INFO, "creating a msp user under the msp organization");

		Response response1 = spogServer.createUser(userEmail, userPassword, FirstName, LastName, Role_Id,
				organization_id, test);
		String user_id1 = spogServer.checkCreateUser(response1, SpogConstants.SUCCESS_POST, userEmail, FirstName,
				LastName, Role_Id, organization_id, "", test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.CREATE_USER, response1,
				user_id1, organizationEmail, "user", userEmail, "user");
		auditdetails.add(auditEvents);

		test.log(LogStatus.INFO, "Preparing the url for the direct organization :");
		String additionalUrl = spogServer.PrepareURL("", "create_ts;asc", 1, 20, test);
		// Invalid Token:
		String invalidToken = csrToken + "Junk";

		test.log(LogStatus.INFO, "providing the invalid csr Token  to get the details");
		response = spogServer.getAuditDetailsForUsers(invalidToken, user_id, additionalURL, test);
		System.out.println("The value of the response is :" + response.getBody().asString());
		test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
		spogServer.checkaudittraildata1(response, SpogConstants.NOT_LOGGED_IN, auditdetails, 1, 20, "", "create_ts;asc",
				SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);

		// Deleting the direct organization
		test.log(LogStatus.INFO, "Login in as csr admin to delete the msp organization");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		spogServer.DeleteOrganizationWithCheck(organization_id, test);

	}

	// Audit Tracking For msp user by proving valid JWT Token of csr_admin

	@Test(dataProvider = "getOrganizationMsp")
	public void getAuditTrailForMspwithcsrAdminToken(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName, String organizationLastName,
			String userEmail, String userPassword, String FirstName, String LastName, String Role_Id) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Ramya Nagepalli");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;
		FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + FirstName;
		LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + LastName;
		userEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;
		ArrayList<HashMap> auditdetails = new ArrayList<HashMap>();
		HashMap<String, Object> auditEvents = new HashMap<String, Object>();

		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		test.log(LogStatus.INFO, "Getting the jwtToken of  csr user ");
		String csrToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "Get the logged in user ");

		String organization_id = spogServer.CreateOrganizationWithCheck(organizationName + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "The organization id is " + organization_id);

		// login with direct organization
		test.log(LogStatus.INFO, "Logging with msp organization user");
		spogServer.userLogin(organizationEmail, common_password);
		String validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "Get the logged in user ");
		Response response = spogServer.getLoggedInUser(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		spogServer.checkLoggedInUser(response, SpogConstants.SUCCESS_GET_PUT_DELETE,
				SpogMessageCode.SUCCESS_GET_PUT_DEL, organizationEmail, test);
		String user_id = response.then().extract().path("data.user_id");
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.LOGIN_USER, response,
				user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);

		// create a user under msp organization
		test.log(LogStatus.INFO, "creating a msp user under the msp organization");
		Response response1 = spogServer.createUser(userEmail, userPassword, FirstName, LastName, Role_Id,
				organization_id, test);
		String user_id1 = spogServer.checkCreateUser(response1, SpogConstants.SUCCESS_POST, userEmail, FirstName,
				LastName, Role_Id, organization_id, "", test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.CREATE_USER, response1,
				user_id1, organizationEmail, "user", userEmail, "user");
		auditdetails.add(auditEvents);

		// preapring the url for the get audit trail
		test.log(LogStatus.INFO, "Preparing the url for the msp organization :");
		String additionalUrl = spogServer.PrepareURL("code_id;=;1", "create_ts;asc", 1, 20, test);
		String filter = "code_id;=;1";
		ArrayList<HashMap> userresponse = compose_expected_response(filter, auditdetails);
		test.log(LogStatus.INFO, "providing the valid token of csr admin to get the details");
		response = spogServer.getAuditDetailsForUsers(csrToken, user_id, additionalUrl, test);
		System.out.println("The value of the response is :" + response.getBody().asString());
		test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
		spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 20, "",
				"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

		// Deleting the direct organization
		test.log(LogStatus.INFO, "Login in as csr admin to delete the msp organization");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		spogServer.DeleteOrganizationWithCheck(organization_id, test);

	}

	// Audit Tracking For direct user by proving valid JWT Token of csr_admin

	@Test(dataProvider = "organizationAndUserInfo1")
	public void getAuditTrailForDirectwithcsrAdminToken(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName, String organizationLastName,
			String userEmail, String userPassword, String FirstName, String LastName, String Role_Id) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Ramya Nagepalli");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;
		FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + FirstName;
		LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + LastName;
		userEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;
		ArrayList<HashMap> auditdetails = new ArrayList<HashMap>();
		HashMap<String, Object> auditEvents = new HashMap<String, Object>();

		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		test.log(LogStatus.INFO, "Getting the jwtToken of  csr user ");
		String csrToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "Get the logged in user ");

		Response response = spogServer.getLoggedInUser(csrToken, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		String csr_user_id = response.then().extract().path("data.user_id");
		String csr_organization_id = response.then().extract().path("data.organization_id");
		String csr_organizationEmail = response.then().extract().path("data.email");

		response = spogServer.getLoggedInUser(csrToken, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		String user_id = response.then().extract().path("data.user_id");
		String organization_id = response.then().extract().path("data.organization_id");
		response = spogServer.CreateOrganizationByEnrollWithCheck_audit(organizationName + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		organization_id = response.then().extract().path("data[0].organization.organization_id");
		auditEvents = spogServer.composeJsonMap(csr_user_id, csr_organization_id,
				AuditCodeConstants.CREATE_ORGANIZATION, response, organization_id, csr_organizationEmail, "user",
				organizationName + org_model_prefix, "organization");
		auditdetails.add(auditEvents);

		// login with direct organization
		test.log(LogStatus.INFO, "Logging with direct organization user");
		spogServer.userLogin(organizationEmail, common_password);
		String validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "Get the logged in user ");
		response = spogServer.getLoggedInUser(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		spogServer.checkLoggedInUser(response, SpogConstants.SUCCESS_GET_PUT_DELETE,
				SpogMessageCode.SUCCESS_GET_PUT_DEL, organizationEmail, test);
		user_id = response.then().extract().path("data.user_id");
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.LOGIN_USER, response,
				user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);

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

		// create cloud rps with csr token
		spogcloudRPSServer.setToken(csrToken);
		test.log(LogStatus.INFO, " create cloud rps with csr token");
		response = spogcloudRPSServer.createCloudRPS(cloud_rps_name, cloud_rps_port, cloud_rps_protocol,
				cloud_rps_username, cloud_rps_password, organization_id, csr_site_id,
				"91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", csrToken);
		String rps_server_name = response.then().extract().path("data.server_name");
		String rps_server_id = response.then().extract().path("data.server_id");
		auditEvents = spogServer.composeJsonMap(csr_user_id, csr_organization_id,
				AuditCodeConstants.CREATE_RECOVERY_POINT_SERVER, response, rps_server_id, csr_organizationEmail, "user",
				rps_server_name, "recovery_point_server");
		auditdetails.add(auditEvents);

		// modify cloud rps
		test.log(LogStatus.INFO, "modify created cloud rps with csr token");
		response = spogcloudRPSServer.updateCloudRPS(rps_server_id, rps_server_name, cloud_rps_port, cloud_rps_protocol,
				cloud_rps_username, cloud_rps_password, csrToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		auditEvents = spogServer.composeJsonMap(csr_user_id, csr_organization_id,
				AuditCodeConstants.MODIFY_RECOVERY_POINT_SERVER, response, "e367d776-48a8-4cf6-af37-2e9c0d815716",
				csr_organizationEmail, "user", rps_server_name, "recovery_point_server");
		auditdetails.add(auditEvents);

		// create datastore for cloud rps
		test.log(LogStatus.INFO, "Compose the hashmap for deduplication info ");
		HashMap<String, Object> deduplicationInfo = spogcloudRPSServer.composededuplicationInfo(4, 1024, false,
				spogServer.ReturnRandom("C:\\destination\\data"), "administrator", common_password,
				spogServer.ReturnRandom("C:\\destination\\index"), "administrator", common_password,
				spogServer.ReturnRandom("C:\\destination\\hash"), "administrator", common_password);

		test.log(LogStatus.INFO, "Compose the hashmap for datastoreproperties info " + organizationType);
		HashMap<String, Object> datastorepropertiesInfo = spogcloudRPSServer.composedatastorepropertiesInfo(4, 102400,
				spogServer.ReturnRandom("C:\\destination1"), "administrator", common_password, "standard", "abcfdfa",
				deduplicationInfo, true, "abc@arcserve.com");

		String datastore_name = spogServer.ReturnRandom("datastore_name");
		test.log(LogStatus.INFO, "Create the cloud rps datastore " + organizationType);
		response = spogcloudRPSServer.createCloudRPSDataStore(datastore_name, true, true, true, rps_server_id,
				datastorepropertiesInfo, csrToken);
		String datastore_id = response.then().extract().path("data.datastore_id");
		auditEvents = spogServer.composeJsonMap(csr_user_id, csr_organization_id, AuditCodeConstants.CREATE_DATA_STORE,
				response, datastore_id, csr_organizationEmail, "user", datastore_name, "datastore");
		auditdetails.add(auditEvents);

		// modify datastore for cloud rps
		test.log(LogStatus.INFO, " modify datastore for cloud rps");
		response = spogcloudRPSServer.updateCloudRPSDataStore(datastore_name, true, true, true, datastore_id,
				datastorepropertiesInfo, csrToken);
		auditEvents = spogServer.composeJsonMap(csr_user_id, csr_organization_id, AuditCodeConstants.MODIFY_DATA_STORE,
				response, datastore_id, csr_organizationEmail, "user", datastore_name, "datastore");
		auditdetails.add(auditEvents);

		// STOP datastore
		test.log(LogStatus.INFO, " stop datastore service for cloud rps");
		Response response1 = spogcloudRPSServer.postDatastoreStopWithCheck(csrToken, datastore_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		auditEvents = spogServer.composeJsonMap(csr_user_id, csr_organization_id, AuditCodeConstants.STOP_DATA_STORE,
				response1, datastore_id, csr_organizationEmail, "user", datastore_name, "datastore");
		auditdetails.add(auditEvents);

		// start datastore
		test.log(LogStatus.INFO, " start datastore service for cloud rps");
		response1 = spogcloudRPSServer.postDatastoreStartWithCheck(csrToken, datastore_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		auditEvents = spogServer.composeJsonMap(csr_user_id, csr_organization_id, AuditCodeConstants.START_DATA_STORE,
				response1, datastore_id, csr_organizationEmail, "user", datastore_name, "datastore");
		auditdetails.add(auditEvents);

		// delete datastore
		test.log(LogStatus.INFO, " delete datastore for cloud rps");
		spogcloudRPSServer.deleteCloudRPSDataStore(datastore_id, csrToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);
		auditEvents = spogServer.composeJsonMap(csr_user_id, csr_organization_id, AuditCodeConstants.DELETE_DATA_STORE,
				response1, datastore_id, csr_organizationEmail, "user", datastore_name, "datastore");
		auditdetails.add(auditEvents);

		// delete cloud Rps
		test.log(LogStatus.INFO, " delete cloud rps");
		spogcloudRPSServer.deleteCloudRPS(rps_server_id, csrToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		auditEvents = spogServer.composeJsonMap(csr_user_id, csr_organization_id,
				AuditCodeConstants.DELETE_RECOVERY_POINT_SERVER, response1, rps_server_id, csr_organizationEmail,
				"user", rps_server_name, "recovery_point_server");
		auditdetails.add(auditEvents);

		Properties prop1 = new Properties();
		try {
			prop1.load(new FileInputStream("src\\linuxRPS.properties"));

			linux_rps_name = prop1.getProperty("linux_rps_name");
			linux_rps_port = prop1.getProperty("linux_rps_port");
			linux_rps_protocol = prop1.getProperty("linux_rps_protocol");
			linux_rps_username = prop1.getProperty("linux_rps_username");
			linux_rps_password = prop1.getProperty("linux_rps_password");
			csr_site_id = prop1.getProperty("csr_site_id");
			csr_site_name = prop1.getProperty("csr_site_name");

		} catch (IOException e) {
			e.printStackTrace();
			test.log(LogStatus.INFO, "Failed to load RPS properties file");
		}

		// CREATE LINUX BACKUP SERVER
		test.log(LogStatus.INFO, "CREATE LINUX BACKUP SERVER");
		linux4spogServer.setToken(csrToken);
		response = linux4spogServer.createLinuxBackupServer_audit(rps_server_id, linux_rps_name, linux_rps_protocol,
				linux_rps_port, linux_rps_username, linux_rps_password, csr_organization_id, csr_site_id, "normal",
				"91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
		String linux_server_id = response.then().extract().path("data.server_id");
		auditEvents = spogServer.composeJsonMap(csr_user_id, csr_organization_id,
				AuditCodeConstants.CREATE_LINUX_BACKUP_SERVER, response, linux_server_id, csr_organizationEmail, "user",
				linux_rps_name, "linux_backup_server");
		auditdetails.add(auditEvents);

		// modify LINUX BACKUP SERVER
		test.log(LogStatus.INFO, "modify LINUX BACKUP SERVER");
		response = linux4spogServer.updateLinuxBackupServer_audit(linux_server_id, linux_server_id, linux_rps_name,
				linux_rps_protocol, linux_rps_port, linux_rps_username, linux_rps_password, csr_organization_id,
				csr_site_id, "normal", "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
		auditEvents = spogServer.composeJsonMap(csr_user_id, csr_organization_id,
				AuditCodeConstants.MODIFY_LINUX_BACKUP_SERVER, response, linux_server_id, csr_organizationEmail, "user",
				linux_rps_name, "linux_backup_server");
		auditdetails.add(auditEvents);

		// Delete linux backup server
		test.log(LogStatus.INFO, "Delete LINUX BACKUP SERVER");
		linux4spogServer.deleteLinuxBackupServer(linux_server_id, test);
		auditEvents = spogServer.composeJsonMap(csr_user_id, csr_organization_id,
				AuditCodeConstants.DELETE_LINUX_BACKUP_SERVER, response1, linux_server_id, csr_organizationEmail,
				"user", linux_rps_name, "linux_backup_server");
		auditdetails.add(auditEvents);

		for (int i = 0; i < 2; i++) {
			if (i == 1) {
				csrToken = csr_read_only_validToken;
			}

			test.log(LogStatus.INFO, "Preparing the url for the direct organization :");
			String additionalUrl = spogServer.PrepareURL("", "create_ts;asc", 1, 20, test);

			test.log(LogStatus.INFO, "providing the valid token of the csr user to get the details");
			String filter = "code_id;=;4";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			ArrayList<HashMap> userresponse = compose_expected_response(filter, auditdetails);
			response = spogServer.getAuditDetailsForUsers(csrToken, user_id, additionalUrl, test);
			System.out.println("The value of the response is :" + response.getBody().asString());
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");

			test.log(LogStatus.INFO, "providing the valid token of the csr user to get the details");
			filter = "code_id=5001&server_id=e367d776-48a8-4cf6-af37-2e9c0d815716&page=1&page_size=1";
			String filter1 = "code_id;=;5001";
			userresponse = compose_expected_response(filter1, auditdetails);
			response = spogServer.getAuditDetailsForUsers(csrToken, csr_user_id, filter, test);
			System.out.println("The value of the response is :" + response.getBody().asString());
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 1, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			test.log(LogStatus.INFO, "providing the valid token of the csr user to get the details");
			filter = "code_id=5002&server_id=e367d776-48a8-4cf6-af37-2e9c0d815716&page=1&page_size=1";
			filter1 = "code_id;=;5002";
			userresponse = compose_expected_response(filter1, auditdetails);
			response = spogServer.getAuditDetailsForUsers(csrToken, csr_user_id, filter, test);
			System.out.println("The value of the response is :" + response.getBody().asString());
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 1,filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);

			test.log(LogStatus.INFO, "providing the valid token of the csr user to get the details");
			filter = "code_id=5003&server_id=e367d776-48a8-4cf6-af37-2e9c0d815716&page=1&page_size=1";
			filter1 = "code_id;=;5003";
			userresponse = compose_expected_response(filter1, auditdetails);
			response = spogServer.getAuditDetailsForUsers(csrToken, csr_user_id, filter, test);
			System.out.println("The value of the response is :" + response.getBody().asString());
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			/*
			 * spogServer.checkaudittraildata1(response,
			 * SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 1, filter,
			 * "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			 */

			test.log(LogStatus.INFO, "providing the valid token of the csr user to get the details");
			filter = "code_id=7001&datastore_id=" + datastore_id + "&page=1&page_size=1";
			filter1 = "code_id;=;7001";
			userresponse = compose_expected_response(filter1, auditdetails);
			response = spogServer.getAuditDetailsForUsers(csrToken, csr_user_id, filter, test);
			System.out.println("The value of the response is :" + response.getBody().asString());
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 1, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			test.log(LogStatus.INFO, "providing the valid token of the csr user to get the details");
			filter = "code_id=7002&datastore_id=" + datastore_id + "&page=1&page_size=1";
			filter1 = "code_id;=;7002";
			userresponse = compose_expected_response(filter1, auditdetails);
			response = spogServer.getAuditDetailsForUsers(csrToken, csr_user_id, filter, test);
			System.out.println("The value of the response is :" + response.getBody().asString());
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 1, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			test.log(LogStatus.INFO, "providing the valid token of the csr user to get the details");
			filter = "code_id=7003&datastore_id=" + datastore_id + "&page=1&page_size=1";
			filter1 = "code_id;=;7003";
			userresponse = compose_expected_response(filter1, auditdetails);
			response = spogServer.getAuditDetailsForUsers(csrToken, csr_user_id, filter, test);
			System.out.println("The value of the response is :" + response.getBody().asString());
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 1, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			test.log(LogStatus.INFO, "providing the valid token of the csr user to get the details");
			filter = "code_id=7006&datastore_id=" + datastore_id + "&page=1&page_size=1";
			filter1 = "code_id;=;7006";
			userresponse = compose_expected_response(filter1, auditdetails);
			response = spogServer.getAuditDetailsForUsers(csrToken, csr_user_id, filter, test);
			System.out.println("The value of the response is :" + response.getBody().asString());
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 1, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			test.log(LogStatus.INFO, "providing the valid token of the csr user to get the details");
			filter = "code_id=7005&datastore_id=" + datastore_id + "&page=1&page_size=1";
			filter1 = "code_id;=;7005";
			userresponse = compose_expected_response(filter1, auditdetails);
			response = spogServer.getAuditDetailsForUsers(csrToken, csr_user_id, filter, test);
			System.out.println("The value of the response is :" + response.getBody().asString());
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 1, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			test.log(LogStatus.INFO, "providing the valid token of the csr user to get the details");
			filter = "code_id=6001&datastore_id=" + datastore_id + "&page=1&page_size=1";

			filter1 = "code_id;=;6001";
			userresponse = compose_expected_response(filter1, auditdetails);
			response = spogServer.getAuditDetailsForUsers(csrToken, csr_user_id, filter, test);
			System.out.println("The value of the response is :" + response.getBody().asString());
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 1, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			test.log(LogStatus.INFO, "providing the valid token of the csr user to get the details");
			filter = "code_id=6002&datastore_id=" + datastore_id + "&page=1&page_size=1";

			filter1 = "code_id;=;6002";
			userresponse = compose_expected_response(filter1, auditdetails);
			response = spogServer.getAuditDetailsForUsers(csrToken, csr_user_id, filter, test);
			System.out.println("The value of the response is :" + response.getBody().asString());
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 1, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			test.log(LogStatus.INFO, "providing the valid token of the csr user to get the details");
			filter = "code_id=6003&datastore_id=" + datastore_id + "&page=1&page_size=1";

			filter1 = "code_id;=;6003";
			additionalUrl = spogServer.PrepareURL(filter1, "create_ts;asc", 1, 1, test);
			userresponse = compose_expected_response(filter1, auditdetails);
			response = spogServer.getAuditDetailsForUsers(csrToken, csr_user_id, filter, test);
			System.out.println("The value of the response is :" + response.getBody().asString());
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 1, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			test.log(LogStatus.INFO, "The organization id is " + organization_id);

			test.log(LogStatus.INFO, "providing the valid token of the csr user to get the details");
			filter = "code_id=101&sort=-create_ts";
			filter1 = "code_id;=;101";
			userresponse = compose_expected_response(filter1, auditdetails);
			response = spogServer.getAuditDetailsForUsers(csrToken, csr_user_id, filter, test);
			System.out.println("The value of the response is :" + response.getBody().asString());
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			// spogServer.checkaudittraildata1(response,SpogConstants.SUCCESS_GET_PUT_DELETE,userresponse
			// , 1, 1,filter,
			// "create_ts;asc",SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
		}

		// Deleting the direct organization
		test.log(LogStatus.INFO, "Login in as csr admin to delete the direct organization");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		spogServer.DeleteOrganizationWithCheck(organization_id, test);

	}

	// Audit Tracking For direct user by missing jwt Token of msp_admin

	@Test(dataProvider = "getOrganizationMsp")
	public void getAuditTrailForMsp_missingToken(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName, String organizationLastName,
			String userEmail, String userPassword, String FirstName, String LastName, String Role_Id) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Ramya Nagepalli");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;
		FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + FirstName;
		LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + LastName;
		userEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;
		ArrayList<HashMap> auditdetails = new ArrayList<HashMap>();
		HashMap<String, Object> auditEvents = new HashMap<String, Object>();

		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		test.log(LogStatus.INFO, "Getting the jwtToken of  csr user ");
		String csrToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "Get the logged in user ");

		String organization_id = spogServer.CreateOrganizationByEnrollWithCheck(organizationName + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "The organization id is " + organization_id);

		// login with direct organization
		test.log(LogStatus.INFO, "Logging with msp organization user");
		spogServer.userLogin(organizationEmail, common_password);
		String validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "Get the logged in user ");
		Response response = spogServer.getLoggedInUser(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		spogServer.checkLoggedInUser(response, SpogConstants.SUCCESS_GET_PUT_DELETE,
				SpogMessageCode.SUCCESS_GET_PUT_DEL, organizationEmail, test);
		String user_id = response.then().extract().path("data.user_id");
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.LOGIN_USER, response,
				user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);

		// create a user under msp organization
		test.log(LogStatus.INFO, "creating a msp user under the msp organization");

		Response response1 = spogServer.createUser(userEmail, userPassword, FirstName, LastName, Role_Id,
				organization_id, test);
		String user_id1 = spogServer.checkCreateUser(response1, SpogConstants.SUCCESS_POST, userEmail, FirstName,
				LastName, Role_Id, organization_id, "", test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.CREATE_USER, response1,
				user_id1, organizationEmail, "user", userEmail, "user");
		auditdetails.add(auditEvents);

		test.log(LogStatus.INFO, "Preparing the url for the msp organization :");
		String additionalUrl = spogServer.PrepareURL("", "create_ts;asc", 1, 20, test);

		test.log(LogStatus.INFO, "providing the missing token");
		response = spogServer.getAuditDetailsForUsers("", user_id, additionalURL, test);
		System.out.println("The value of the response is :" + response.getBody().asString());
		test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
		spogServer.checkaudittraildata1(response, SpogConstants.NOT_LOGGED_IN, auditdetails, 1, 20, "", "create_ts;asc",
				SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);

		// Deleting the direct organization
		test.log(LogStatus.INFO, "Login in as csr admin to delete the msp organization");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		spogServer.DeleteOrganizationWithCheck(organization_id, test);

	}

	// Audit Tracking For direct user by missing jwt Token of csr_admin

	@Test(dataProvider = "organizationAndUserInfo1")
	public void getAuditTrailForDirect_mssingToken(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName, String organizationLastName,
			String userEmail, String userPassword, String FirstName, String LastName, String Role_Id) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Ramya Nagepalli");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;
		FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + FirstName;
		LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + LastName;
		userEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;
		ArrayList<HashMap> auditdetails = new ArrayList<HashMap>();
		HashMap<String, Object> auditEvents = new HashMap<String, Object>();

		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		test.log(LogStatus.INFO, "Getting the jwtToken of  csr user ");
		String csrToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "Get the logged in user ");

		String organization_id = spogServer.CreateOrganizationWithCheck(organizationName + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "The organization id is " + organization_id);

		// login with direct organization
		test.log(LogStatus.INFO, "Logging with direct organization user");
		spogServer.userLogin(organizationEmail, common_password);
		String validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "Get the logged in user ");
		Response response = spogServer.getLoggedInUser(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		spogServer.checkLoggedInUser(response, SpogConstants.SUCCESS_GET_PUT_DELETE,
				SpogMessageCode.SUCCESS_GET_PUT_DEL, organizationEmail, test);
		String user_id = response.then().extract().path("data.user_id");
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.LOGIN_USER, response,
				user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);

		// create a user under msp organization
		test.log(LogStatus.INFO, "creating a direct user under the direct organization");

		Response response1 = spogServer.createUser(userEmail, userPassword, FirstName, LastName, Role_Id,
				organization_id, test);
		String user_id1 = spogServer.checkCreateUser(response1, SpogConstants.SUCCESS_POST, userEmail, FirstName,
				LastName, Role_Id, organization_id, "", test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.CREATE_USER, response1,
				user_id1, organizationEmail, "user", userEmail, "user");
		auditdetails.add(auditEvents);

		test.log(LogStatus.INFO, "Preparing the url for the direct organization :");
		String additionalUrl = spogServer.PrepareURL("", "create_ts;asc", 1, 20, test);

		test.log(LogStatus.INFO, "providing the missing token ");
		response = spogServer.getAuditDetailsForUsers("", user_id, additionalURL, test);
		System.out.println("The value of the response is :" + response.getBody().asString());
		test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
		spogServer.checkaudittraildata1(response, SpogConstants.NOT_LOGGED_IN, auditdetails, 1, 20, "", "create_ts;asc",
				SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);

		// Deleting the direct organization
		test.log(LogStatus.INFO, "Login in as csr admin to delete the direct organization");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		spogServer.DeleteOrganizationWithCheck(organization_id, test);

	}
	//
	// Audit Tracking For msp_admin user by proving direct userid

	@Test(dataProvider = "organizationAndUserInfo4")
	public void getAuditTrailForMsp_withDirectUserId(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName, String organizationLastName,
			String userEmail, String userPassword, String FirstName, String LastName, String Role_Id,
			String organizationName1, String organizationType1, String organizationEmail1, String organizationPwd1,
			String organizationFirstName1, String organizationLastName1) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Ramya Nagepalli");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;
		FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + FirstName;
		LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + LastName;
		userEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;
		ArrayList<HashMap> auditdetails = new ArrayList<HashMap>();
		HashMap<String, Object> auditEvents = new HashMap<String, Object>();

		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		test.log(LogStatus.INFO, "Getting the jwtToken of  csr user ");
		String csrToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "Get the logged in user ");

		// create a msp organization
		String organization_id = spogServer.CreateOrganizationWithCheck(organizationName + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "The organization id is " + organization_id);

		// login with msp organization
		test.log(LogStatus.INFO, "Logging with msp organization user");
		spogServer.userLogin(organizationEmail, common_password);
		String validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "Get the logged in user ");
		Response response = spogServer.getLoggedInUser(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		spogServer.checkLoggedInUser(response, SpogConstants.SUCCESS_GET_PUT_DELETE,
				SpogMessageCode.SUCCESS_GET_PUT_DEL, organizationEmail, test);
		String user_id = response.then().extract().path("data.user_id");
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.LOGIN_USER, response,
				user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);

		// create a user under msp organization
		test.log(LogStatus.INFO, "creating a msp user under the msp organization");

		Response response1 = spogServer.createUser(userEmail, userPassword, FirstName, LastName, Role_Id,
				organization_id, test);
		String user_id1 = spogServer.checkCreateUser(response1, SpogConstants.SUCCESS_POST, userEmail, FirstName,
				LastName, Role_Id, organization_id, "", test);
		auditEvents = spogServer.composeJsonMap(user_id, organization_id, AuditCodeConstants.CREATE_USER, response1,
				user_id1, organizationEmail, "user", userEmail, "user");
		auditdetails.add(auditEvents);

		// Login with csr admin
		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		test.log(LogStatus.INFO, "Getting the jwtToken of  csr user ");

		// create a direct organization
		organizationEmail1 = RandomStringUtils.randomAlphanumeric(4) + "_" + organizationEmail1;
		String direct_organization_id = spogServer.CreateOrganizationWithCheck(organizationName1 + org_model_prefix,
				organizationType1, organizationEmail1, organizationPwd, organizationFirstName1, organizationLastName1,
				test);
		test.log(LogStatus.INFO, "The organization id is " + direct_organization_id);

		// login with direct organization
		test.log(LogStatus.INFO, "Logging with msp organization user");
		spogServer.userLogin(organizationEmail1, common_password);
		String directToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "The value of direct Token: " + directToken);

		test.log(LogStatus.INFO, "Get the logged in user ");
		response = spogServer.getLoggedInUser(directToken, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		spogServer.checkLoggedInUser(response, SpogConstants.SUCCESS_GET_PUT_DELETE,
				SpogMessageCode.SUCCESS_GET_PUT_DEL, organizationEmail1, test);
		String direct_user_id = response.then().extract().path("data.user_id");
		auditEvents = spogServer.composeJsonMap(direct_user_id, direct_organization_id, AuditCodeConstants.LOGIN_USER,
				response, direct_user_id, organizationEmail, "user", organizationEmail, "user");
		auditdetails.add(auditEvents);

		test.log(LogStatus.INFO, "Preparing the url for the msp organization :");
		String additionalUrl = spogServer.PrepareURL("", "create_ts;asc", 1, 20, test);

		test.log(LogStatus.INFO, "providing the other org id ");
		response = spogServer.getAuditDetailsForUsers(validToken, direct_user_id, additionalURL, test);
		System.out.println("The value of the response is :" + response.getBody().asString());
		test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
		// spogServer.checkaudittraildata1(response,SpogConstants.INSUFFICIENT_PERMISSIONS,auditdetails
		// , 1, 20,"",
		// "create_ts;asc",SpogMessageCode.LOGIN_USER_ORG_NOT_SAME_AS_GET_USER,test);

		// Deleting the direct organization
		test.log(LogStatus.INFO, "Login in as csr admin to delete the msp organization");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		spogServer.DeleteOrganizationWithCheck(organization_id, test);

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
