package api.policies;

import static invoker.SiteTestHelper.createSubOrgnaization;
import static invoker.SiteTestHelper.createUser;
import static invoker.SiteTestHelper.getRandomOrganizationName;
import static invoker.SiteTestHelper.getTestPassword;
import static org.hamcrest.Matchers.containsString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import Constants.ConnectionStatus;
import Constants.OSMajor;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import Constants.TaskType;
import InvokerServer.ServerResponseCode;
import dataPreparation.JsonPreparation;
import genericutil.ExtentManager;
import io.restassured.response.Response;

public class GetPoliciesEnhancementForRplcatRemoteRpsTest extends base.prepare.TestApiBase {

	private String token;
	// private ExtentReports extent;
	private String TestDataPrefix = getClass().getSimpleName();

	// private SQLServerDb bqdb1;
	// public int Nooftest;
	// private long creationTime;
	// private String BQName = null;
	// private String runningMachine;
	// private testcasescount count1;
	// private String buildVersion;

	private String mspOrgId;
	private String mspAdmin;
	private String mspPassword;
	private String groupId_A_Msp;
	private String groupId_B_Msp;

	private String accountOrgAId;
	private String childOrgA_Admin;
	private String childOrgA_Password;
	private String groupId_AccountA;

	private String accountOrgBId;
	private String childOrgB_Admin;
	private String childOrgB_Password;
	private String groupId_AccountB;

	private String directOrgId;
	private String directAdmin;
	private String directPassword;
	private String groupId_Direct;
	private String groupBId_Direct;

	// private SPOGServer spogServer;
	// private ExtentTest test;
	// private ExtentReports rep;
	// private String csrAdminUserName;
	// private String csrAdminPassword;

	private String password = getTestPassword();
	// private String baseUri;
	// private String port;
	// private SPOGDestinationServer spogDestinationServer;
	// private Policy4SPOGServer policy4SPOGServer;
	// private GatewayServer gatewayServer;
	private String msp_cloud_account_id;
	private String msp_site_jwt_token;
	private String msp_destination_id;
	private String msp_source_id;
	private ArrayList<String> testDestinations = new ArrayList<>();

	@Override
	public void setAuthor() {

		author = "Kanamarlapudi, Chandra Kanth";

	}

	// @BeforeTest
	// @Parameters({"baseURI", "port", "csrAdminUserName", "csrAdminPassword",
	// "logFolder",
	// "buildVersion"})
	//
	//
	// public void setSpogServerConnection(String baseURI, String port, String
	// username, String
	// password,
	// String logFolder, String buildVersion) throws UnknownHostException {
	//
	// baseUri = baseURI;
	// this.port = port;
	//
	// configSpogServerConnection(baseURI, port);
	// token = loginSpogServer(username, password);
	//
	// this.csrAdminUserName = username;
	// this.csrAdminPassword = password;
	//
	// spogServer = new SPOGServer(baseURI, port);
	// spogDestinationServer = new SPOGDestinationServer(baseURI, port);
	// gatewayServer = new GatewayServer(baseURI, port);
	// policy4SPOGServer = new Policy4SPOGServer(baseURI, port);
	//
	// spogServer.userLogin(username, password);
	//
	// rep = ExtentManager.getInstance(this.getClass().getSimpleName(),
	// logFolder);
	// test = rep.startTest("initializing data...");
	//
	// // this is for update portal
	// this.BQName = this.getClass().getSimpleName();
	// this.runningMachine = java.net.InetAddress.getLocalHost().getHostName();
	// SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
	// java.util.Date date = new java.util.Date();
	// this.buildVersion = buildVersion + "_" + dateFormater.format(date);
	// String author = "yin.li";
	// Nooftest = 0;
	// bqdb1 = new SQLServerDb();
	// count1 = new testcasescount();
	// if (count1.isstarttimehit() == 0) {
	// creationTime = System.currentTimeMillis();
	// count1.setcreationtime(creationTime);
	// // creationTime = System.currentTimeMillis();
	// try {
	// bqdb1.updateTable(BQName, runningMachine, this.buildVersion,
	// String.valueOf(Nooftest), "0",
	// "0", String.valueOf(Nooftest), count1.getcreationtime(), "InProgress",
	// author + " and Rest server is " + baseUri.split("//")[1]);
	// } catch (ClientProtocolException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	// }

	public static Map<String, String> composeRandomUserMap2(String prefix, String roleId, String organizationId) {

		JsonPreparation jp = new JsonPreparation();

		String username_email = getRandomUserName2(prefix) + "@" + RandomStringUtils.randomAlphanumeric(8) + ".com";
		String password = getTestPassword();
		String first_name = RandomStringUtils.randomAlphanumeric(4);
		String last_name = RandomStringUtils.randomAlphanumeric(4);
		String role_id = roleId;
		String organization_id = organizationId;

		Map<String, String> userInfo = jp.getUserInfo(username_email, password, first_name, last_name, role_id,
				organization_id);

		return userInfo;
	}

	public static String getRandomUserName2(String prefix) {

		return prefix + "_User_" + RandomStringUtils.randomAlphanumeric(3);
	}

	@BeforeClass
	public void prepareJobData() {

		test.assignAuthor("Kanamarlapudi, Chandra Kanth");
		test = rep.startTest("Prepare Job Data");

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Kanamarlapudi, Chandra Kanth");

		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		token = spogServer.getJWTToken();
		String csrToken = this.token;

		// create msp org
		Map<String, String> userInfoMap = composeRandomUserMap2(TestDataPrefix, SpogConstants.MSP_ADMIN, null);

		mspOrgId = spogServer.CreateOrganizationByEnrollWithCheck(getRandomOrganizationName(TestDataPrefix),
				SpogConstants.MSP_ORG, userInfoMap.get("email"), getTestPassword(),
				RandomStringUtils.randomAlphanumeric(4), RandomStringUtils.randomAlphanumeric(4));

		mspAdmin = userInfoMap.get("email");
		mspPassword = getTestPassword();
		// String mspToken = loginSpogServer(mspAdmin, mspPassword);
		spogServer.userLogin(mspAdmin, mspPassword);
		String mspToken = spogServer.getJWTToken();

		// String msp_policy_id = createPolicy();

		// createPolicy(2);

		// create child org A admin
		accountOrgAId = createSubOrgnaization(getRandomOrganizationName(TestDataPrefix), mspOrgId, mspToken);

		userInfoMap = composeRandomUserMap2(TestDataPrefix, SpogConstants.DIRECT_ADMIN, accountOrgAId);
		createUser(userInfoMap, mspToken);
		childOrgA_Admin = userInfoMap.get("email");
		childOrgA_Password = getTestPassword();
		spogServer.userLogin(childOrgA_Admin, childOrgA_Password);

		// create child org B admin
		accountOrgBId = createSubOrgnaization(getRandomOrganizationName(TestDataPrefix), mspOrgId, mspToken);

		userInfoMap = composeRandomUserMap2(TestDataPrefix, SpogConstants.DIRECT_ADMIN, accountOrgBId);
		createUser(userInfoMap, mspToken);
		childOrgB_Admin = userInfoMap.get("email");
		childOrgB_Password = getTestPassword();
		spogServer.userLogin(childOrgB_Admin, childOrgB_Password);

		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		// create msp org
		userInfoMap = composeRandomUserMap2(TestDataPrefix, SpogConstants.MSP_ADMIN, null);
		directOrgId = spogServer.CreateOrganizationByEnrollWithCheck(getRandomOrganizationName(TestPrefix),
				SpogConstants.DIRECT_ORG, userInfoMap.get("email"), getTestPassword(),
				RandomStringUtils.randomAlphanumeric(4), RandomStringUtils.randomAlphanumeric(4));

		directAdmin = userInfoMap.get("email");
		directPassword = getTestPassword();
		// String mspToken = loginSpogServer(mspAdmin, mspPassword);
		spogServer.userLogin(directAdmin, directPassword);
		createPolicy(3);

	}

	private String createPolicy() {

		String cloud_account_id = createCloudAccount();

		String policyId = createPolicy(cloud_account_id);

		return policyId;
	}

	private void createPolicy(int size) {

		String cloud_account_id = spogServer.getCloudAccounts(spogServer.getJWTToken(), "", test).then().extract()
				.path("data[0].cloud_account_id");

		for (int i = 0; i < size; i++) {
			createPolicy(cloud_account_id);

		}

	}

	private String createPolicy(String cloud_account_id) {

		String orgId = spogServer.GetLoggedinUserOrganizationID();
		String datacentId = getDatacenterId(0);
		// String destination_id =
		// spogDestinationServer.createDestinationWithCheck("",
		// orgId,cloud_account_id,
		// datacentId, "cloud_direct_volume", "running", "1", cloud_account_id,
		// "normal", RandomStringUtils.randomAlphabetic(5), "custom", "custom",
		// "0", "0", "7", "0",
		// "0", "0", "", "", "", "", "", "", "", "", "", "", "", "", "", "",
		// RandomStringUtils.randomAlphabetic(8), test);
		String destination_id = spogDestinationServer.createDestinationWithCheck("", orgId, cloud_account_id,
				datacentId, "cloud_direct_volume", "running", "1", cloud_account_id, "normal",
				RandomStringUtils.randomAlphabetic(5), "custom", "custom", "0", "0", "7", "0", "0", "0", "", "", "", "",
				RandomStringUtils.randomAlphabetic(8), test);
		testDestinations.add(destination_id);

		String source_id = spogServer.createSourceWithCheck(RandomStringUtils.random(8), SourceType.machine,
				SourceProduct.cloud_direct, orgId, cloud_account_id, ProtectionStatus.unprotect,
				ConnectionStatus.online, OSMajor.windows.name(), "sql;exchange", RandomStringUtils.random(8), null,
				RandomStringUtils.random(8), "windows 2012", "64", "1.0.0", "2.0", "http://upgrade", test);

		Response response = createPolicy(destination_id, source_id);
		String policyId = response.then().extract().path("data.policy_id");

		return policyId;
	}

	private String createCloudAccount() {

		String token = spogServer.getJWTToken();
		String orgId = spogServer.GetLoggedinUserOrganizationID();
		String userId = spogServer.GetLoggedinUser_UserID();

		String site_id = gatewayServer.createsite_register_login(orgId, token, userId, RandomStringUtils.random(8),
				"1.0.0", spogServer, test);

		// String msp_site_token = gatewayServer.getJWTToken();

		String cloud_account_id = spogServer.createCloudAccount(UUID.randomUUID().toString(), "cloudAccountSecret",
				"CloudAccountData", "cloud_direct", orgId, "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_",
				getDatacenterId(0), spogServer, test);

		return cloud_account_id;
	}

	public Response createPolicy(String destinationId, String sourceId) {

		String policy_id = spogServer.returnRandomUUID();
		String schedule_id = spogServer.returnRandomUUID();
		String task_id = spogServer.returnRandomUUID();
		String throttle_id = spogServer.returnRandomUUID();
		String throttle_type = "network";
		String task_type = TaskType.udp_replication_from_remote.toString(); // shared
																			// policy
		String policy_name = TestDataPrefix + RandomStringUtils.randomAlphabetic(5);
		String policy_description = spogServer.ReturnRandom("description");
		String policy_type = "cloud_direct_baas";

		String organization_id = spogServer.GetLoggedinUserOrganizationID();

		// HashMap<String, Object> cloudDirectScheduleDTO =
		// policy4SPOGServer.createCloudDirectScheduleDTO("0 0 * * *", test);
		// HashMap<String, Object> scheduleSettingDTO =
		// policy4SPOGServer.createScheduleSettingDTO(cloudDirectScheduleDTO,
		// null, test);
		// ArrayList<HashMap<String, Object>> schedules =
		// policy4SPOGServer.createPolicyScheduleDTO(null,
		// schedule_id, "1d", task_id, destinationId, scheduleSettingDTO,
		// "06:00", "12:00", test);
		// ArrayList<HashMap<String, Object>> excludes =
		// policy4SPOGServer.createExcludeInfoDTO(null, "path", "c:\\tmp",
		// test);
		// HashMap<String, Object> cloudDirectLocalBackupDTO =
		// policy4SPOGServer.createCloudDirectLocalBackupDTO("d:\\tmp", "true",
		// excludes, test);
		// HashMap<String, Object> cloudDirectFileBackupTaskInfoDTO =
		// policy4SPOGServer
		// .createCloudDirectFileBackupTaskInfoDTO("d:\\tmp",
		// cloudDirectLocalBackupDTO, test);
		// ArrayList<HashMap<String, Object>> destinations =
		// policy4SPOGServer.createPolicyTaskDTO(null, task_id, task_type,
		// destinationId, "none", null,
		// cloudDirectFileBackupTaskInfoDTO, null, test);
		// ArrayList<HashMap<String, Object>> throttles =
		// policy4SPOGServer.createPolicyThrottleDTO(null,
		// throttle_id, task_id, throttle_type, "1200", "1", "06:00", "18:00",
		// test);

		HashMap<String, Object> cloudDirectScheduleDTO = policy4SPOGServer.createCloudDirectScheduleDTO("0 6 * * *",
				test);
		HashMap<String, Object> scheduleSettingDTO = policy4SPOGServer.createScheduleSettingDTO(cloudDirectScheduleDTO,
				null, test);
		ArrayList<HashMap<String, Object>> schedules = policy4SPOGServer.createPolicyScheduleDTO(null,
				spogServer.returnRandomUUID(), "1d", task_id, destinationId, scheduleSettingDTO, "06:00", "12:00",
				"cloud_direct_file_folder_backup", "dest", test);
		ArrayList<HashMap<String, Object>> excludes = policy4SPOGServer.createExcludeInfoDTO(null, "path", "c:\\tmp",
				test);
		HashMap<String, Object> cloudDirectLocalBackupDTO = policy4SPOGServer.createCloudDirectLocalBackupDTO("d:\\tmp",
				"true", test);
		HashMap<String, Object> cloudDirectFileBackupTaskInfoDTO = policy4SPOGServer
				.createCloudDirectFileBackupTaskInfoDTO("d:\\tmp", cloudDirectLocalBackupDTO, excludes, test);
		ArrayList<HashMap<String, Object>> destinations = policy4SPOGServer.createPolicyTaskDTO(null, task_id,
				"cloud_direct_file_folder_backup", destinationId, "none", null, cloudDirectFileBackupTaskInfoDTO, null,
				test);
		ArrayList<HashMap<String, Object>> throttles = policy4SPOGServer.createPolicyThrottleDTO(null,
				spogServer.returnRandomUUID(), task_id, "network", "1200", "1", "06:00", "18:00",
				"cloud_direct_file_folder_backup", destinationId, "dest", test);

		policy4SPOGServer.setToken(spogServer.getJWTToken());

		// Response response =
		// policy4SPOGServer.createPolicy(policy_name, policy_description,
		// policy_type, null, "true",
		// "success", destinations, schedules, throttles, policy_id,
		// organization_id, test);

		Response response = policy4SPOGServer.createPolicy(policy_name, policy_description, policy_type, null, "true",
				sourceId, destinations, schedules, throttles, policy_id, organization_id, test);

		return response;
	}

	public void generateSourceFilterForloggedUser(String logUser, String logPsw, int count) {

		spogServer.userLogin(logUser, logPsw);
		String userId = spogServer.GetLoggedinUser_UserID();

		for (int i = 0; i < count; i++) {
			String filter_name = UUID.randomUUID().toString();
			String protection_status = "protect,unprotect";
			String connection_status = "online,offline";
			String protection_policy = UUID.randomUUID().toString();
			String backup_status = "finished";
			String source_group = UUID.randomUUID().toString();
			String operating_system = "windows";
			String applications = "SQL_SERVER";
			String site_id = UUID.randomUUID().toString();
			String source_name = UUID.randomUUID().toString();
			String source_type = "machine";
			String is_default = "false";

			String sourceFilterId = spogServer.createFilterwithCheck(userId, filter_name, protection_status,
					connection_status, protection_policy, backup_status, source_group, operating_system, applications,
					site_id, source_name, source_type, is_default, test);
		}
	}

	public String getRandDatacenterId() {

		spogDestinationServer.setToken(spogServer.getJWTToken());
		String[] datacenterIDs = spogDestinationServer.getDestionationDatacenterID();
		int index = (int) Math.random() * datacenterIDs.length;
		String datacenterID = datacenterIDs[index];
		return datacenterID;
	}

	public String getDatacenterId(int index) {

		spogDestinationServer.setToken(spogServer.getJWTToken());
		String[] datacenterIDs = spogDestinationServer.getDestionationDatacenterID();
		String datacenterID = datacenterIDs[index];
		return datacenterID;
	}

	public void generateSourceFilterForloggedUser(String logUser, String logPsw, String source_type) {

		spogServer.userLogin(logUser, logPsw);
		String userId = spogServer.GetLoggedinUser_UserID();

		String filter_name = UUID.randomUUID().toString();
		String protection_status = "protect,unprotect";
		String connection_status = "online,offline";
		String protection_policy = UUID.randomUUID().toString();
		String backup_status = "finished";
		String source_group = UUID.randomUUID().toString();
		String operating_system = "windows";
		String applications = "SQL_SERVER";
		String site_id = UUID.randomUUID().toString();
		String source_name = UUID.randomUUID().toString();
		String is_default = "false";

		String sourceFilterId = spogServer.createFilterwithCheck(userId, filter_name, protection_status,
				connection_status, protection_policy, backup_status, source_group, operating_system, applications,
				site_id, source_name, source_type, is_default, test);
	}

	@DataProvider(name = "get_policies_pass")
	public final Object[][] getPolicies() {

		return new Object[][] { { mspAdmin }, { directAdmin } };
	}

	@Test(dataProvider = "get_policies_pass")
	public void Given_LoggedInUser_When_GetPolicies_Should_Success(String loginUsr) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Kanamarlapudi, Chandra Kanth");

		spogServer.userLogin(loginUsr, password);

		Response response = policy4SPOGServer.getPolicies(null);
		response.then().statusCode(ServerResponseCode.Success_Get);
	}

	/*
	 * @Test() public void
	 * Given_LoggedInUser_When_GetReplicatePolicies_Should_Success1() {
	 * 
	 * test = ExtentManager.getNewTest( this.getClass().getName() + "." +
	 * Thread.currentThread().getStackTrace()[1].getMethodName());
	 * test.assignAuthor("yin.li");
	 * 
	 * String direct_admin_email = "bettyliu2009@gmail.com"; String
	 * direct_admin_password = "Caworld_2018";
	 * 
	 * spogServer.userLogin(direct_admin_email, direct_admin_password);
	 * 
	 * String replicatePolicyId = createReplicatePolicy();
	 * 
	 * Response response = policy4SPOGServer.getPolicies(null);
	 * response.then().statusCode(ServerResponseCode.Success_Get);
	 * response.then().assertThat().body(containsString(replicatePolicyId));
	 * 
	 * policy4SPOGServer.deletePolicybyPolicyId(spogServer.getJWTToken(),
	 * replicatePolicyId, 200, null, test); }
	 */

	@Test()
	public void Given_NotLoggedInUser_When_GetPolicies_Should_Failed() {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Kanamarlapudi, Chandra Kanth");

		policy4SPOGServer.setToken("");

		Response response = policy4SPOGServer.getPolicies(null);
		response.then().statusCode(ServerResponseCode.Not_login);
	}

	public HashMap<String, String> composeFilter(String status, String policyName, String groupId,
			String lastBackupStatus, String sortStr) {

		HashMap<String, String> params = new HashMap<String, String>();

		if (StringUtils.isNotEmpty(status)) {
			params.put("policy_status", status);
		}

		if (StringUtils.isNotEmpty(lastBackupStatus)) {
			params.put("last_job", lastBackupStatus);
		}

		if (StringUtils.isNotEmpty(policyName)) {
			params.put("policy_name", policyName);
		}

		if (StringUtils.isNotEmpty(groupId)) {
			params.put("source_group", groupId);
		}

		if (StringUtils.isNotEmpty(sortStr)) {
			params.put("sort", sortStr);
		}

		return params;
	}

	@DataProvider(name = "loginUserToCreatePolicy")
	public final Object[][] loginUserToCreatePolicy() {
		return new Object[][] { { "direct" }, { "account" }, { "msp" }, { "csr" } };
	}

	@Test(dataProvider = "loginUserToCreatePolicy")
	public void Given_LoggedInUser_When_GetReplicatePolicies_Should_Success(String loginUserType) {
		/*
		 * test = ExtentManager.getNewTest( this.getClass().getName() + "." +
		 * Thread.currentThread().getStackTrace()[1].getMethodName());
		 * test.assignAuthor("Shan, Jing");
		 */
		String policy_id = spogServer.returnRandomUUID();
		String schedule_id = spogServer.returnRandomUUID();
		String task_id = spogServer.returnRandomUUID();
		String replicateToTask_id = spogServer.returnRandomUUID();
		String throttle_id = spogServer.returnRandomUUID();
		String destination_store_id = null;
		String organization_id = null;
		String task_type = "udp_replication_from_remote";
		String to_task_type = "udp_replication_to_remote";
		String policy_type = "cloud_hybrid_replication";
		String policy_name = spogServer.ReturnRandom("test");
		String policy_description = spogServer.ReturnRandom("description");
		String destination_name = "shanjing-cloud-hybridDS4";

		String direct_org_id = "6be5f4f7-50df-4c2e-88c6-5384bddb873b";
		String direct_admin_email = "bettyliu2009@gmail.com";
		String direct_admin_password = "Caworld_2018";
		String direct_admin_user_id = "97f4972b-53f5-4065-98e6-9b9cf6823b54";
		String direct_hybrid_store_name = "jing-hy-direct";
		String direct_hybrid_store_id = "1a50a7a4-28fb-4339-8298-0b698347398f";
		String direct_org_name = "yuefen-directorg-betty";

		String account_org_id = "146bce60-21c0-4352-9638-0e17cb728cc0";
		String account_admin_email = "cc_rw_yuefen@arcserve.com";
		String account_admin_password = "Caworld_2017";
		String account_admin_user_id = "b364ea12-0234-473a-9423-e378e0805367";
		String account_hybrid_store_name = "jing-hy-suborg";
		String account_hybrid_store_id = "eb1e7640-6aa0-40e8-bd1f-a26ea961dfe6";
		String account_org_name = "cc_rw_yuefen";

		String msp_org_id = "5e52e8f6-891c-41fa-8bd0-df2ca7263eb1";
		String msp_admin_email = "cc_rw_msp@arcserve.com";
		String msp_admin_password = "Caworld_2017";
		String msp_admin_user_id = "57287088-8b40-4c73-bf67-b4fcf3d81287";
		String msp_org_name = "Arcserve SPOG QA";

		if (loginUserType.equalsIgnoreCase("csr")) {
			spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
			organization_id = direct_org_id;
			destination_store_id = direct_hybrid_store_id;
		} else if (loginUserType.equalsIgnoreCase("direct")) {
			destination_name = "liuyu05-cloud-hybridDS3";
			spogServer.userLogin(direct_admin_email, direct_admin_password);
			organization_id = direct_org_id;
			destination_store_id = direct_hybrid_store_id;
		} else if (loginUserType.equalsIgnoreCase("msp")) {
			spogServer.userLogin(msp_admin_email, msp_admin_password, test);
			organization_id = account_org_id;
			destination_store_id = account_hybrid_store_id;
		} else if (loginUserType.equalsIgnoreCase("account")) {
			spogServer.userLogin(account_admin_email, account_admin_password, test);
			organization_id = account_org_id;
			destination_store_id = account_hybrid_store_id;
		}

		String remote_console_name = "ec2-18-234-157-152.compute-1.amazonaws.com";
		String user_name = "administrator";
		String password = "cnbjrdqa1!";
		String port = "8015";
		String protocol = "https";

		String remote_plan_global_uuid = "f2a4953f-4b30-4301-bcaa-221e2be76c8b";
		String remote_plan_uuid = "f2a4953f-4b30-4301-bcaa-221e2be76c8b";
		String remote_plan_name = "yin-plan";
		String remote_plan_rpspolicy_uuid = "310516df-02d2-4444-9c70-3500aaa3002e";
		String remote_plan_rpspolicy_name = "shaji02-plan";

		/*
		 * destination_name = "liuyu05-cloud-hybridDS3";
		 * spogServer.userLogin(direct_admin_email, direct_admin_password);
		 * organization_id = direct_org_id; destination_store_id =
		 * direct_hybrid_store_id;
		 */

		HashMap<String, Object> CustomScheduleDTO = policy4SPOGServer.createCustomScheduleDTO("1524469596000", "full",
				"1", "true", "10", "minutes", test);
		HashMap<String, Object> scheduleSettingDTO = policy4SPOGServer.createScheduleSettingDTO(null, CustomScheduleDTO,
				test);
		ArrayList<HashMap<String, Object>> schedules = policy4SPOGServer.createPolicyScheduleDTO(null,
				spogServer.returnRandomUUID(), "custom", task_id, destination_store_id, scheduleSettingDTO, "06:00",
				"12:00", task_type, destination_name, test);
		HashMap<String, Object> performARTestOption = policy4SPOGServer.createPerformARTestOption("true", "true",
				"true", "true", test);
		// HashMap<String, Object>
		// retention_policy=policy4SPOGServer.createRetentionPolicyOption("6","7","","",
		// test);
		HashMap<String, Object> udpReplicationFromRemoteInfoDTO = policy4SPOGServer
				.createUdpReplicationFromRemoteInfoDTO(performARTestOption, null, test);
		HashMap<String, Object> udpReplicationToRemoteInfoDTO = policy4SPOGServer.createUdpReplicationToRemoteInfoDTO(
				remote_console_name, user_name, password, port, protocol, remote_plan_global_uuid, remote_plan_uuid,
				remote_plan_name, remote_plan_rpspolicy_uuid, remote_plan_rpspolicy_name, "10", "3", test);
		String ranm_de_id = spogServer.returnRandomUUID();
		// ArrayList<HashMap<String,Object>> destinations=
		// policy4SPOGServer.createPolicyTaskDTO(null, task_id, task_type,
		// destination_normal_id, "none", null, null,
		// udpReplicationFromRemoteInfoDTO, test);
		ArrayList<HashMap<String, Object>> destinations = policy4SPOGServer.createPolicyTaskDTO(null, task_id,
				task_type, destination_store_id, "none", null, null, udpReplicationFromRemoteInfoDTO, test);
		destinations = policy4SPOGServer.createPolicyTaskDTO(destinations, replicateToTask_id, to_task_type,
				destination_store_id, task_id, null, null, null, udpReplicationToRemoteInfoDTO, test);
		// spogServer.userLogin(this.csrAdmin, this.csrPwd);
		String user_token = spogServer.getJWTToken();
		policy4SPOGServer.setToken(user_token);
		ArrayList<HashMap<String, Object>> throttles = policy4SPOGServer.createPolicyThrottleDTO(null, throttle_id,
				task_id, "network", "1200", "1", "06:00", "18:00", task_type, destination_store_id, destination_name,
				test);

		policy4SPOGServer.setToken(user_token);
		// Response response=policy4SPOGServer.createPolicy(policy_name,
		// policy_description, policy_type, null, "true", "successful",
		// source_id_1, destinations, schedules, throttle, policy_id,
		// organization_id, test);
		Response response = policy4SPOGServer.createPolicy(policy_name, policy_description, policy_type, null, "true",
				null, destinations, schedules, throttles, policy_id, organization_id, test);
		waitPolicyStatus(user_token, policy_id);
		/*
		 * policy4SPOGServer.checkPolicyThrottles(response,
		 * SpogConstants.SUCCESS_POST, throttles, test);
		 * policy4SPOGServer.checkPolicySchedules(response,
		 * SpogConstants.SUCCESS_POST, schedules, test);
		 * policy4SPOGServer.checkReplicatePolicyDestination(response,
		 * SpogConstants.SUCCESS_POST, destinations, performARTestOption, null,
		 * test); policy4SPOGServer.checkPolicyCommon(response,
		 * SpogConstants.SUCCESS_POST, policy_name, policy_description,
		 * policy_type, null, "true", "success", null, policy_id,
		 * organization_id, test);
		 * policy4SPOGServer.deletePolicybyPolicyId(user_token, policy_id, 200,
		 * null, test);
		 */
		response = policy4SPOGServer.getPolicies(null);
		response.then().statusCode(ServerResponseCode.Success_Get);
		response.then().assertThat().body(containsString(policy_id));

		policy4SPOGServer.deletePolicybyPolicyId(spogServer.getJWTToken(), policy_id, 200, null, test);
	}

	public void waitPolicyStatus(String user_token, String policy_id) {
		Response response = policy4SPOGServer.getPolicyById(user_token, policy_id, test);
		try {
			Thread.sleep(5000);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String check_policyStatus = response.then().extract().path("data.policy_status");
		for (int loop = 1; loop < 10; loop++) {
			if (check_policyStatus.equalsIgnoreCase("deploying")) {
				try {
					Thread.sleep(5000);

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				response = policy4SPOGServer.getPolicyById(user_token, policy_id, test);
				check_policyStatus = response.then().extract().path("data.policy_status");
				if (!check_policyStatus.equalsIgnoreCase("deploying")) {
					break;
				}
			} else if (check_policyStatus.equalsIgnoreCase("success")) {
				break;
			}
		}

	}
	//
	// @AfterMethod
	// public void getResult(ITestResult result) {
	//
	// if (result.getStatus() == ITestResult.FAILURE) {
	// count1.setfailedcount();
	// test.log(LogStatus.FAIL, "Test Case Failed is " + result.getName() + "
	// with parameters as "
	// + Arrays.asList(result.getParameters()));
	// test.log(LogStatus.FAIL, result.getThrowable().getMessage());
	// } else if (result.getStatus() == ITestResult.SKIP) {
	// count1.setskippedcount();
	// test.log(LogStatus.SKIP, "Test Case Skipped is " + result.getName());
	// } else if (result.getStatus() == ITestResult.SUCCESS) {
	// count1.setpassedcount();
	// }
	// // ending test
	// // endTest(logger) : It ends the current test and prepares to create HTML
	// report
	// rep.endTest(test);
	// }
	//
	//
	// @AfterTest
	// public void aftertest() {
	//
	// test.log(LogStatus.INFO, "The total test cases passed are " +
	// count1.getpassedcount());
	// test.log(LogStatus.INFO, "the total test cases failed are " +
	// count1.getfailedcount());
	// rep.flush();
	// }
	//
	//
	// @AfterClass
	// public void updatebd() {
	//
	// for (String destinationId : testDestinations) {
	// spogDestinationServer.RecycleDirectVolume(destinationId, test);
	// }
	//
	// try {
	// if (count1.getfailedcount() > 0) {
	// Nooftest = count1.getpassedcount() + count1.getfailedcount() +
	// count1.getskippedcount();
	// bqdb1.updateTable(this.BQName, this.runningMachine, this.buildVersion,
	// String.valueOf(Nooftest), Integer.toString(count1.getpassedcount()),
	// Integer.toString(count1.getfailedcount()),
	// String.valueOf(count1.getskippedcount()),
	// count1.getcreationtime(), "Failed");
	// } else {
	// Nooftest = count1.getpassedcount() + count1.getfailedcount() +
	// count1.getskippedcount();
	// bqdb1.updateTable(this.BQName, this.runningMachine, this.buildVersion,
	// String.valueOf(Nooftest), Integer.toString(count1.getpassedcount()),
	// Integer.toString(count1.getfailedcount()),
	// String.valueOf(count1.getskippedcount()),
	// count1.getcreationtime(), "Passed");
	// }
	// } catch (ClientProtocolException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IOException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }

}
