package api.audittrail;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Pattern;

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

import Base64.Base64Coder;
import Constants.AuditCodeConstants;
import Constants.ConnectionStatus;
import Constants.DestinationStatus;
import Constants.DestinationType;
import Constants.JobType4LatestJob;
import Constants.LogSeverityType;
import Constants.OSMajor;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.GatewayServer.siteType;
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
import invoker.SiteTestHelper;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;

public class GetAuditTrailbyOrgIdTest extends base.prepare.Is4Org {

	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private SPOGDestinationServer spogDestinationServer;
	private SPOGJobServer spogJobServer;
	private UserSpogServer userSpogServer;
	private Log4SPOGServer log4SPOGServer;
	private Source4SPOGServer spogSourceServer;
	private Log4GatewayServer log4GatewayServer;
	private SPOGRecoveredResourceServer spogRecoveredResourceServer;
	private Policy4SPOGServer policy4SpogServer;
	private SPOGReportServer spogReportServer;
	private Linux4SPOGServer linux4spogServer;
	private SPOGCloudRPSServer spogcloudRPSServer;
	private SiteTestHelper siteTestHelper;
	// private ExtentReports rep;
	private ExtentTest test;
	private String common_password = "Mclaren@2010";
	private String csrAdminUserName;
	private String csrAdminPassword;

	private String csrReadOnlyUserName;
	private String csrReadOnlyPassword;
	private String csr_read_only_validToken;
	private String csr_read_only_user_id;

	private String prefix_direct = "spogqa_ramya_direct";
	private String postfix_email = "@arcserve.com";
	private String direct_org_name = prefix_direct + "_org";
	private String direct_org_email = direct_org_name + postfix_email;
	private String direct_org_first_name = direct_org_name + "_first_name";
	private String direct_org_last_name = direct_org_name + "_last_name";
	private String direct_user_name = prefix_direct + "_admin";
	private String direct_user_name_email = prefix_direct + "_admin" + postfix_email;
	private String direct_user_first_name = direct_user_name + "_first_name";
	private String direct_user_last_name = direct_user_name + "_last_name";
	private String mspOrgId;
	private String mspSubOrgId;
	private String prefix_msp = "spogqa_ramya_msp";
	private String msp_org_name = prefix_msp + "_org";
	private String msp_org_email = msp_org_name + postfix_email;
	private String msp_org_first_name = msp_org_name + "_first_name";
	private String msp_org_last_name = msp_org_name + "_last_name";
	private String msp_user_name = prefix_msp + "_admin";
	private String msp_user_name_email = prefix_msp + "_admin" + postfix_email;
	private String msp_user_first_name = msp_user_name + "_first_name";
	private String msp_user_last_name = msp_user_name + "_last_name";
	private ArrayList<HashMap> Users_Events = new ArrayList<HashMap>();
	private String msp_org_id = null;
	// used for test cases count like passed,failed,remaining cases
	/*
	 * private SQLServerDb bqdb1; public int Nooftest;
	 * 
	 * long creationTime;
	 */
	int passedcases = 0;
	int failedcases = 0;
	int skippedcases = 0;

	/*
	 * String BQame=null; private testcasescount count1; private String
	 * runningMachine,cloudAccountSecret; private String buildVersion;
	 */
	LocalDate date = LocalDate.now();
	LocalDate yesterday = date.minusDays(1);
	LocalDate tomorrow = yesterday.plusDays(2);

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

	private String org_model_prefix = "Auto"+this.getClass().getSimpleName();

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
		spogSourceServer = new Source4SPOGServer(baseURI, port);
		log4GatewayServer = new Log4GatewayServer(baseURI, port);
		spogRecoveredResourceServer = new SPOGRecoveredResourceServer(baseURI, port);
		policy4SpogServer = new Policy4SPOGServer(baseURI, port);
		spogcloudRPSServer = new SPOGCloudRPSServer(baseURI, port);
		linux4spogServer = new Linux4SPOGServer(baseURI, port);
		rep = ExtentManager.getInstance(this.getClass().getSimpleName(), logFolder);
		spogReportServer = new SPOGReportServer(baseURI, port);
		String author = "Ramya.Nagepalli";
		Nooftest = 0;
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
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
				bqdb1.updateTable(BQName, runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0",
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
		// login with csr read only credentials
		spogServer.userLogin(csrReadOnlyUserName, csrReadOnlyPassword);
		csr_read_only_validToken = spogServer.getJWTToken();
		csr_read_only_user_id = spogServer.GetLoggedinUser_UserID();

	}

	@DataProvider(name = "organizationAndUserInfo")
	public final Object[][] getOrganizationAndUserInfo() {
		return new Object[][] { { direct_org_name, SpogConstants.DIRECT_ORG, direct_org_email, common_password,
				direct_org_first_name, direct_org_last_name, direct_user_name_email, common_password,
				direct_user_first_name, direct_user_last_name, SpogConstants.DIRECT_ADMIN } };
	}

	@DataProvider(name = "msporganizationAndUserInfo")
	public final Object[][] getmspOrganizationAndUserInfo() {
		return new Object[][] { { msp_org_name, SpogConstants.MSP_ORG, msp_org_email, common_password,
				msp_org_first_name, msp_org_last_name, msp_user_name_email, common_password, msp_user_first_name,
				msp_user_last_name, SpogConstants.MSP_ADMIN } };
	}

	@Test(dataProvider = "msporganizationAndUserInfo")
	public void getaudittraildataofusers_sites_orgs_bymsporgId_InvalidOrgId(String organizationName,
			String organizationType, String organizationEmail, String organizationPwd, String organizationFirstName,
			String organizationLastName, String userEmail, String userPassword, String FirstName, String LastName,
			String Role_Id) {
		String gateway_hostname = "Ramya";
		String site_version = "1.0.0";
		ArrayList<HashMap> User_Events = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_Site = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_Org = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_User = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_byCodeid = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_Sites_Org = new ArrayList<HashMap>();
		ArrayList<ResponseBody> userresponse = new ArrayList<ResponseBody>();
		HashMap<String, Object> user_events_code = new HashMap<String, Object>();
		Response response1 = null;
		test = rep.startTest("getaudittraildataofusers_sites_orgs_bymsporgId_InvalidOrgId");
		test.assignAuthor("Ramya.Nagepalli");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;
		FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + FirstName;
		LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + LastName;
		userEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;

		test.log(LogStatus.INFO, "Login in as CSR Admin user");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		String csr_token = spogServer.getJWTToken();
		String csr_user_id = spogServer.GetLoggedinUser_UserID();
		test.log(LogStatus.INFO, "The csr admin user id is " + csr_user_id);
		test.log(LogStatus.INFO, "Create a organization of type " + organizationType);
		String organization_id = spogServer.CreateOrganizationWithCheck(organizationName + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "The organization id is " + organization_id);
		spogServer.userLogin(organizationEmail, organizationPwd);
		String validToken = spogServer.getJWTToken();
		String user_id = spogServer.GetLoggedinUser_UserID();
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.LOGIN_USER, organization_id, user_id,
				user_id, response1, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_User.add(user_events_code);

		test.log(LogStatus.INFO, "Prepare the URL");
		String additionalUrl = spogServer.PrepareURL("", "create_ts;desc", 1, 20, test);
		String invalid_orgId = UUID.randomUUID().toString();
		test.log(LogStatus.INFO, "Get the audit trail by Invalid orgId " + invalid_orgId);
		test.log(LogStatus.INFO,
				"Get the audit trail by direct org ID with descending order and current page is 1 and pagesize 20, csr token");
		Response response = spogServer.getaudittrailbyorgId(validToken, invalid_orgId, additionalUrl, test);

		spogServer.checkResponseStatus(response, SpogConstants.RESOURCE_NOT_EXIST);
		spogServer.checkaudittraildata(response, SpogConstants.RESOURCE_NOT_EXIST, User_Events, 1, 20, "", "",
				SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED, test);

		test.log(LogStatus.INFO,
				"Performing the operations to delete the user and orginzation by logging in as csr admin");
		test.log(LogStatus.INFO, "Login in as csr admin to delete the organization");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		// Deleting the msp organization
		test.log(LogStatus.INFO, "Login in as csr admin to delete the organization");
		spogServer.userLogin("ramya_csr@arcserve.com", "Mclaren@2020");
		csr_token = spogServer.getJWTToken();
		spogDestinationServer.recycleCloudVolumesAndDestroyOrg(csr_token, organization_id);
	}

	@Test(dataProvider = "msporganizationAndUserInfo")
	public void getaudittraildataofusers_sites_orgs_bymsporgId_OtherOrgId(String organizationName,
			String organizationType, String organizationEmail, String organizationPwd, String organizationFirstName,
			String organizationLastName, String userEmail, String userPassword, String FirstName, String LastName,
			String Role_Id) {
		String gateway_hostname = "Ramya";
		String site_version = "1.0.0";
		ArrayList<HashMap> User_Events = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_Site = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_Org = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_User = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_byCodeid = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_Sites_Org = new ArrayList<HashMap>();
		ArrayList<ResponseBody> userresponse = new ArrayList<ResponseBody>();
		HashMap<String, Object> user_events_code = new HashMap<String, Object>();
		Response response1 = null;
		test = rep.startTest("getaudittraildataofusers_sites_orgs_bymsporgId_OtherOrgId");
		test.assignAuthor("Ramya.Nagepalli");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;
		FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + FirstName;
		LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + LastName;
		userEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;

		test.log(LogStatus.INFO, "Login in as CSR Admin user");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		String csr_token = spogServer.getJWTToken();
		String csr_user_id = spogServer.GetLoggedinUser_UserID();
		test.log(LogStatus.INFO, "The csr admin user id is " + csr_user_id);
		test.log(LogStatus.INFO, "Create a organization of type " + organizationType);
		String organization_id = spogServer.CreateOrganizationWithCheck(organizationName + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "The organization id is " + organization_id);

		test.log(LogStatus.INFO, "Create another org");
		String organization_id1 = spogServer.CreateOrganizationWithCheck(organizationName + "_101" + org_model_prefix,
				organizationType, "2" + organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		spogServer.userLogin(organizationEmail, organizationPwd);
		String validToken = spogServer.getJWTToken();
		String user_id = spogServer.GetLoggedinUser_UserID();
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.LOGIN_USER, organization_id, user_id,
				user_id, response1, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_User.add(user_events_code);

		test.log(LogStatus.INFO, "Prepare the URL");
		String additionalUrl = spogServer.PrepareURL("", "create_ts;desc", 1, 20, test);
		test.log(LogStatus.INFO, "Get the audit trail by other orgId " + organization_id1);
		test.log(LogStatus.INFO,
				"Get the audit trail by direct org ID with descending order and current page is 1 and pagesize 20, csr token");
		Response response = spogServer.getaudittrailbyorgId(validToken, organization_id1, additionalUrl, test);

		spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS);
		spogServer.checkaudittraildata(response, SpogConstants.INSUFFICIENT_PERMISSIONS, User_Events, 1, 20, "", "",
				SpogMessageCode.RESOURCE_PERMISSION_DENY, test);

		test.log(LogStatus.INFO,
				"Performing the operations to delete the user and orginzation by logging in as csr admin");
		test.log(LogStatus.INFO, "Login in as csr admin to delete the organization");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		// Deleting the msp organization
		test.log(LogStatus.INFO, "Login in as csr admin to delete the organization");
		csr_token=spogServer.getJWTToken();
				
		spogDestinationServer.recycleCloudVolumesAndDestroyOrg(csr_token, organization_id);
		spogDestinationServer.recycleCloudVolumesAndDestroyOrg(csr_token, organization_id1);
	}

	@Test(dataProvider = "msporganizationAndUserInfo")
	public void getaudittraildataofusers_sites_orgs_bymsporgId(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName, String organizationLastName,
			String userEmail, String userPassword, String FirstName, String LastName, String Role_Id) {
		String gateway_hostname = "Ramya";
		String site_version = "1.0.0";
		String source_id = null;
		String destination_id = null;
		String sourcegroup_id = null;
		String site_token = null;
		ArrayList<HashMap> User_Events_Destinations = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_Sources = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_Site = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_Org = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_User = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_byCodeid = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_Sites_Org = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_Group = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_AddsourcetoGroup = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_SourceFilter = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_DestinationFilter = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_JobFilter = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_LogFilter = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_UserFilter = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_HypervisorFilter = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_RecoveryResourceFilters = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_PolicyFilters = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_UserColumns = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_JobColumns = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_LogColumns = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_SourceColumns = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_DestinationColumns = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_HypervisorColumns = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_RecoveryResourceColumns = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_PolicyColumns = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_jobs = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_jobs_data = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_logs = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_logs_data = new ArrayList<HashMap>();

		ArrayList<ResponseBody> userresponse = new ArrayList<ResponseBody>();
		ArrayList<HashMap<String, Object>> expectedColumns = new ArrayList<>();
		HashMap<String, Object> temp = new HashMap<>();
		HashMap<String, Object> user_events_code = new HashMap<String, Object>();

		Response response1 = null;

		ArrayList<HashMap> User_Events_policies = new ArrayList<HashMap>();
		// Related to policies
		HashMap<String, Object> scheduleSettingDTO = new HashMap<>();
		ArrayList<HashMap<String, Object>> schedules = new ArrayList<>();
		String schedule_id = spogServer.returnRandomUUID();
		String task_id = spogServer.returnRandomUUID();
		String throttle_id = spogServer.returnRandomUUID();
		String throttle_type = "network";
		String policy_name = spogServer.ReturnRandom("test");
		String policy_description = spogServer.ReturnRandom("description");
		String task_type = null;
		String destination_name = null;
		test = rep.startTest("getaudittraildataofusers_sites_orgs_bymsporgId");
		test.assignAuthor("Ramya.Nagepalli");

		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;
		FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + FirstName;
		LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + LastName;
		userEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;

		test.log(LogStatus.INFO, "Login in as CSR Admin user");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		String csr_token = spogServer.getJWTToken();
		String csr_user_id = spogServer.GetLoggedinUser_UserID();
		test.log(LogStatus.INFO, "The csr admin user id is " + csr_user_id);
		test.log(LogStatus.INFO, "Create a organization of type " + organizationType);
		String organization_id = spogServer.CreateOrganizationWithCheck(organizationName + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "The organization id is " + organization_id);
		msp_org_id = organization_id;
		spogServer.userLogin(organizationEmail, organizationPwd);
		String validToken = spogServer.getJWTToken();
		String user_id = spogServer.GetLoggedinUser_UserID();
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.LOGIN_USER, organization_id, user_id,
				user_id, response1, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_User.add(user_events_code);

		test.log(LogStatus.INFO, "The org userid is " + user_id);
		test.log(LogStatus.INFO, "The token is :" + validToken);

		test.log(LogStatus.INFO, "Create source filter for user");
		// spogServer.createFilterwithCheck(user_id, "test", "emptyarray",
		// "online", UUID.randomUUID().toString(), "finished", "emptyarray",
		// "windows", "emptyarray", SourceType.machine.name(), "true",test);
		String filter_name = "filter_name";
		String source_name = "source_name";
		Response response = spogServer.createFilter(user_id, filter_name, "emptyarray", "online",
				UUID.randomUUID().toString(), "finished", "none", "windows", "emptyarray", "", source_name,
				SourceType.machine.name(), "true", test);
		String filter_id = response.then().extract().path("data.filter_id");
		filter_name = response.then().extract().path("data.filter_name");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.create_source_filter, organization_id,
				user_id, filter_id, response, organizationEmail, "user", filter_name, "source_filter");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_SourceFilter.add(user_events_code);

		test.log(LogStatus.INFO, "update source filter for user");
		response = spogServer.updateFilter(user_id, filter_id, filter_name, "emptyarray", "online",
				UUID.randomUUID().toString(), "finished", "none", "windows", "emptyarray", "", source_name,
				SourceType.machine.name(), SourceType.machine.name(), "true", test);
		filter_name = response.then().extract().path("data.filter_name");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.modify_source_filter, organization_id,
				user_id, filter_id, response, organizationEmail, "user", filter_name, "source_filter");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_SourceFilter.add(user_events_code);

		test.log(LogStatus.INFO, "delete source filter for user");
		spogServer.deletefilterspecifiedbyUserIdwithCheck(user_id, filter_id, validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.delete_source_filter, organization_id,
				user_id, filter_id, response1, organizationEmail, "user", filter_name, "source_filter");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_SourceFilter.add(user_events_code);

		test.log(LogStatus.INFO, "Create destination filter for user");
		filter_name = "filter_d";
		spogDestinationServer.setToken(validToken);
		response = spogDestinationServer.createDestinationFilter(user_id, filter_name, destination_name, "none",
				DestinationType.cloud_direct_volume.toString(), "true", test);
		filter_id = response.then().extract().path("data.filter_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.create_destination_filter, organization_id,
				user_id, filter_id, response, organizationEmail, "user", filter_name, "destination_filter");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_DestinationFilter.add(user_events_code);

		test.log(LogStatus.INFO, "update destination filter for user");
		filter_name = "filter_d";
		response = spogDestinationServer.updateDestinationFilterWithCheck(user_id, filter_id, filter_name, "none",
				"none", DestinationType.cloud_direct_volume.toString(), "true", test, false);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.modify_destination_filter, organization_id,
				user_id, filter_id, response, organizationEmail, "user", filter_name, "destination_filter");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_DestinationFilter.add(user_events_code);

		test.log(LogStatus.INFO, "delete destination filter for user");
		spogDestinationServer.deletedestinationfilterbyfilterId(user_id, filter_id, validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.delete_destination_filter, organization_id,
				user_id, filter_id, response1, organizationEmail, "user", filter_name, "destination_filter");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_DestinationFilter.add(user_events_code);

		test.log(LogStatus.INFO, "Create the job filter in org " + organizationType);
		spogJobServer.setToken(validToken);
		filter_name = "J-filterName";
		response = spogJobServer.createJobFilterWithCheckResponse(user_id, "finished", UUID.randomUUID().toString(),
				source_id, "backup_full", null, null, null, null, filter_name, "true", test);
		filter_id = response.then().extract().path("data.filter_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_JOB_FILTER, organization_id, user_id,
				filter_id, response, organizationEmail, "user", filter_name, "job_filter");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_JobFilter.add(user_events_code);

		test.log(LogStatus.INFO, "update the job filter in org " + organizationType);
		filter_name = "J-filterName";
		response = spogJobServer.updateJobFilterWithCheck(user_id, filter_id, "finished", UUID.randomUUID().toString(),
				source_id, "backup_full", null, null, filter_name, "true", test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_JOB_FILTER, organization_id, user_id,
				filter_id, response, organizationEmail, "user", filter_name, "job_filter");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_JobFilter.add(user_events_code);

		test.log(LogStatus.INFO, "delete the job filter in org " + organizationType);
		spogJobServer.deleteJobFilterforloggedInUser(filter_id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_JOB_FILTER, organization_id, user_id,
				filter_id, response1, organizationEmail, "user", filter_name, "job_filter");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_JobFilter.add(user_events_code);

		log4SPOGServer.setToken(validToken);
		test.log(LogStatus.INFO, "Create the log filter in org " + organizationType);
		log4SPOGServer.setToken(validToken);
		filter_name = "filter_name_l";
		response = log4SPOGServer.createLogFilter(user_id, organization_id, filter_name, "last_24_hours", null, null,
				LogSeverityType.warning.toString(), "false", "none", "none", null, "sourcename", "none", test);
		filter_id = response.then().extract().path("data.filter_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_LOG_FILTER, organization_id, user_id,
				filter_id, response, organizationEmail, "user", filter_name, "log_filter");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_LogFilter.add(user_events_code);

		test.log(LogStatus.INFO, "update the log filter in org " + organizationType);
		response = log4SPOGServer.updateLogFilter(user_id, filter_id, organization_id, filter_name, "last_24_hours",
				null, "backup_full", LogSeverityType.warning.toString(), "false", "none", "none", "none", "sourcename",
				"none", test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_LOG_FILTER, organization_id, user_id,
				filter_id, response, organizationEmail, "user", filter_name, "log_filter");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_LogFilter.add(user_events_code);

		test.log(LogStatus.INFO, "delete the log filter in org " + organizationType);
		log4SPOGServer.deleteLogFilterforloggedinUser(filter_id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_LOG_FILTER, organization_id, user_id,
				filter_id, response1, organizationEmail, "user", filter_name, "log_filter");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_LogFilter.add(user_events_code);

		userSpogServer.setToken(validToken);

		test.log(LogStatus.INFO, "Create user filter in org of type " + organizationType);
		filter_name = "ufilter";
		response = userSpogServer.createUserFilterForSpecificUser(user_id, filter_name, "abcd", null, null,
				SpogConstants.MSP_ADMIN, "true", validToken, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		filter_id = response.then().extract().path("data.filter_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_USER_FILTER, organization_id,
				user_id, filter_id, response, organizationEmail, "user", filter_name, "user_filter");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_UserFilter.add(user_events_code);

		test.log(LogStatus.INFO, "update user filter in org of type " + organizationType);
		filter_name = "ufilter";
		response = userSpogServer.updateUserFilterForSpecificUser(user_id, filter_id, filter_name, null, null, null,
				SpogConstants.MSP_ADMIN, "true", validToken, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		filter_id = response.then().extract().path("data.filter_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_USER_FILTER, organization_id,
				user_id, filter_id, response, organizationEmail, "user", filter_name, "user_filter");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_UserFilter.add(user_events_code);

		test.log(LogStatus.INFO, "delete user filter in org of type " + organizationType);
		userSpogServer.deleteUserFilterByUserIdWithCheck(validToken, user_id, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_USER_FILTER, organization_id,
				user_id, filter_id, response1, organizationEmail, "user", filter_name, "user_filter");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_UserFilter.add(user_events_code);

		test.log(LogStatus.INFO, "Create policy filter in org of type " + organizationType);
		filter_name = "PPfilterName";
		response = userSpogServer.createPolicyFilterForLoggedinUser(filter_name, policy_name,
				UUID.randomUUID().toString(), "finished", "failure", "true", test);
		filter_id = response.then().extract().path("data.filter_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_POLICY_FILTER, organization_id,
				user_id, filter_id, response, organizationEmail, "user", filter_name, "policy_filter");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_PolicyFilters.add(user_events_code);

		test.log(LogStatus.INFO, "update policy filter in org of type " + organizationType);
		filter_name = "PPfilterName";
		response = userSpogServer.updatePolicyFilterForLoggedinUserWithCheck(filter_id, filter_name, policy_name,
				UUID.randomUUID().toString(), "finished", "failure", "true", test);
		filter_id = response.then().extract().path("data.filter_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_POLICY_FILTER, organization_id,
				user_id, filter_id, response, organizationEmail, "user", filter_name, "policy_filter");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_PolicyFilters.add(user_events_code);

		test.log(LogStatus.INFO, "delete policy filter in org of type " + organizationType);
		userSpogServer.deleteSpecificPolicyFilterForLoggedinUser(filter_id, test);
		filter_id = response.then().extract().path("data.filter_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_POLICY_FILTER, organization_id,
				user_id, filter_id, response1, organizationEmail, "user", filter_name, "policy_filter");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_PolicyFilters.add(user_events_code);

		test.log(LogStatus.INFO, "Create recoveredresource filter in org of type " + organizationType);
		String policy_id = UUID.randomUUID().toString();
		filter_name = "recfailure";
		response = spogRecoveredResourceServer.createLoggedInUserRecoveredResourcesFilters(validToken, policy_id, "",
				OSMajor.windows.name(), "", filter_name, "true", "", test);
		filter_id = response.then().extract().path("data.filter_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_RECOVEREDRESOURCE_FILTER,
				organization_id, user_id, filter_id, response, organizationEmail, "user", filter_name,
				"recovered_resource_filter");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_RecoveryResourceFilters.add(user_events_code);

		test.log(LogStatus.INFO, "Update recoveredresource filter in org of type " + organizationType);
		filter_name = "recfailure";
		response = spogRecoveredResourceServer.updateLoggedInUserRecoveredResourcesFilters(validToken, filter_id,
				policy_id, "starting", "", "", filter_name, "true", SpogConstants.SUCCESS_GET_PUT_DELETE, "", test);
		filter_id = response.then().extract().path("data.filter_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_RECOVEREDRESOURCE_FILTER,
				organization_id, user_id, filter_id, response, organizationEmail, "user", filter_name,
				"recovered_resource_filter");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_RecoveryResourceFilters.add(user_events_code);

		test.log(LogStatus.INFO, "Delete recoveredresource filter in org of type " + organizationType);
		spogRecoveredResourceServer.deleteLoggedInUserRecoveredResourcesFilters(validToken, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		filter_id = response.then().extract().path("data.filter_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_RECOVEREDRESOURCE_FILTER,
				organization_id, user_id, filter_id, response1, organizationEmail, "user", filter_name,
				"recovered_resource_filter");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_RecoveryResourceFilters.add(user_events_code);

		// create report list filters for specified user
		test.log(LogStatus.INFO, "create report list filters for specified user");
		spogServer.setToken(validToken);
		spogReportServer.setToken(validToken);
		filter_name = spogServer.ReturnRandom("report");
		HashMap<String, Object> expectedfilter = spogReportServer.composeReportListFilterInfo(filter_name,
				"last_7_days", "all_sources", "", organization_id, "weekly", System.currentTimeMillis(),
				System.currentTimeMillis(), filter_name, true);
		response = spogReportServer.createReportListFiltersForLoggedInUserWithCheck(validToken, expectedfilter,
				SpogConstants.SUCCESS_POST, SpogMessageCode.SUCCESS_POST, test);
		filter_id = response.then().extract().path("data.filter_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_REPORT_LIST_FILTER, organization_id,
				user_id, filter_id, response, organizationEmail, "user", filter_name, "report_list_filter");
		User_Events.add(user_events_code);

		// update report list filters for specified user
		test.log(LogStatus.INFO, "update report list filters for specified user");
		/*
		 * response = spogReportServer.
		 * updateReportListFiltersForLoggedInUserByFilterIdWithCheck(validToken,
		 * user_id, filter_id, expectedfilter, expectedfilter,
		 * SpogConstants.SUCCESS_GET_PUT_DELETE,
		 * SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		 */
		response = spogReportServer.updateReportListFiltersForLoggedInUserByFilterIdWithCheck(validToken, filter_id,
				expectedfilter, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_REPORT_LIST_FILTER, organization_id,
				user_id, filter_id, response, organizationEmail, "user", filter_name, "report_list_filter");
		User_Events.add(user_events_code);

		// delete report list filters for specified user
		test.log(LogStatus.INFO, "delete report list filters for specified user");
		spogReportServer.deleteReportListFiltersForLoggedInUserByFilterIdWithCheck(validToken, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_REPORT_LIST_FILTER, organization_id,
				user_id, filter_id, response1, organizationEmail, "user", filter_name, "report_list_filter");
		User_Events.add(user_events_code);

		test.log(LogStatus.INFO, "Create user columns for logged in user in organization : " + organizationType);
		temp = userSpogServer.composeUser_Column("50169479-cf16-412a-9a6d-3bf19a111a0b", "true", "true", "true", "1");
		expectedColumns.add(temp);
		response = userSpogServer.createUserColumnsForLoggedInUser(validToken, expectedColumns, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_USER_COLUMN, organization_id,
				user_id, user_id, response, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_UserColumns.add(user_events_code);

		test.log(LogStatus.INFO, "update user columns for logged in user in organization : " + organizationType);
		response = userSpogServer.updateUserColumnsForLoggedInUser(validToken, expectedColumns, expectedColumns,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_USER_COLUMN, organization_id,
				user_id, user_id, response, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_UserColumns.add(user_events_code);

		test.log(LogStatus.INFO, "delete user columns for logged in user in organization : " + organizationType);
		userSpogServer.deleteUserColumnsforSpecifiedUserwithCheck(user_id, validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_USER_COLUMN, organization_id,
				user_id, user_id, response1, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_UserColumns.add(user_events_code);

		test.log(LogStatus.INFO, "Create destination columns for logged in user in organization : " + organizationType);
		temp = userSpogServer.composeUser_Column("a6e15988-2919-4fd2-9b7d-00d2eddffde8", "false", "true", "true", "1");
		expectedColumns = new ArrayList<>();
		expectedColumns.add(temp);
		response = spogDestinationServer.createDestinationColumnsForSpecifiedUser(user_id, validToken, expectedColumns,
				test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_USER_DESTINATION_COLUMN,
				organization_id, user_id, user_id, response, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_DestinationColumns.add(user_events_code);

		test.log(LogStatus.INFO, "update destination columns for logged in user in organization : " + organizationType);
		response = spogDestinationServer.updateDestinationColumnsForLoggedinUser(validToken, expectedColumns,
				expectedColumns, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_USER_DESTINATION_COLUMN,
				organization_id, user_id, user_id, response, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_DestinationColumns.add(user_events_code);

		test.log(LogStatus.INFO, "delete destination columns for logged in user in organization : " + organizationType);
		spogDestinationServer.deleteDestinatinColumnsforLoggedInUserwithCheck(validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_USER_DESTINATION_COLUMN,
				organization_id, user_id, user_id, response1, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_DestinationColumns.add(user_events_code);

		test.log(LogStatus.INFO, "Create job columns for logged in user in organization : " + organizationType);
		temp = spogJobServer.composejob_Column("c6a4be7e-bf1c-4e88-a278-9c34f5f69117", "false", "true", "true", "1");
		expectedColumns = new ArrayList<>();
		expectedColumns.add(temp);
		response = spogJobServer.createJobColumnsForSpecifiedUser(user_id, validToken, expectedColumns, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_USER_JOB_COLUMN, organization_id,
				user_id, user_id, response, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_JobColumns.add(user_events_code);

		test.log(LogStatus.INFO, "update job columns for logged in user in organization : " + organizationType);
		response = spogJobServer.updateJobColumnsForLoggedInUser(validToken, expectedColumns, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_USER_JOB_COLUMN, organization_id,
				user_id, user_id, response, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_JobColumns.add(user_events_code);

		test.log(LogStatus.INFO, "delete job columns for logged in user in organization : " + organizationType);
		spogJobServer.deleteJobColumnsForLoggedInUser(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_USER_JOB_COLUMN, organization_id,
				user_id, user_id, response1, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_JobColumns.add(user_events_code);

		test.log(LogStatus.INFO, "Create log columns for logged in user in organization : " + organizationType);
		temp = log4SPOGServer.composelog_Column("55d3dbb0-83e7-45ae-879a-8f3a522d66c5", "true", "true", "true", "1");
		expectedColumns = new ArrayList<>();
		expectedColumns.add(temp);
		response = log4SPOGServer.createLogColumnsForLoggedInUser(validToken, expectedColumns, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_USER_LOG_COLUMN, organization_id,
				user_id, user_id, response, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_LogColumns.add(user_events_code);

		test.log(LogStatus.INFO, "update log columns for logged in user in organization : " + organizationType);
		response = log4SPOGServer.updateLogColumnsForloggedinUser(validToken, expectedColumns, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_USER_LOG_COLUMN, organization_id,
				user_id, user_id, response, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_LogColumns.add(user_events_code);

		test.log(LogStatus.INFO, "delete log columns for logged in user in organization : " + organizationType);
		log4SPOGServer.deleteLogColumnsforLoggedInUserwithCheck(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_USER_LOG_COLUMN, organization_id,
				user_id, user_id, response1, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_LogColumns.add(user_events_code);

		test.log(LogStatus.INFO, "Create source columns for logged in user in organization : " + organizationType);
		temp = log4SPOGServer.composelog_Column("07871b87-6ec5-44ae-a1bb-17abfb75a9f9", "false", "true", "true", "1");
		expectedColumns = new ArrayList<>();
		expectedColumns.add(temp);
		response = spogSourceServer.createUsersSourcesColumns(user_id, expectedColumns, validToken);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_USER_SOURCE_COLUMN, organization_id,
				user_id, user_id, response, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_SourceColumns.add(user_events_code);

		test.log(LogStatus.INFO, "update source columns for logged in user in organization : " + organizationType);
		response = spogSourceServer.updateUsersSourcesColumns(user_id, expectedColumns, validToken);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_USER_SOURCE_COLUMN, organization_id,
				user_id, user_id, response, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_SourceColumns.add(user_events_code);

		test.log(LogStatus.INFO, "delete source columns for logged in user in organization : " + organizationType);
		spogSourceServer.deleteUsersSourcesColumns(user_id, validToken);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_USER_SOURCE_COLUMN, organization_id,
				user_id, user_id, response1, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_SourceColumns.add(user_events_code);

		test.log(LogStatus.INFO, "Create hypervisor columns for logged in user in organization : " + organizationType);
		temp = log4SPOGServer.composelog_Column("31dfe327-b9fe-432a-a119-24b584a85263", "true", "true", "true", "1");
		expectedColumns = new ArrayList<>();
		expectedColumns.add(temp);
		response = spogSourceServer.createHypervisorColumnsForLoggedInUser(expectedColumns, validToken);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_USER_HYPERVISOR_COLUMN,
				organization_id, user_id, user_id, response, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_HypervisorColumns.add(user_events_code);

		test.log(LogStatus.INFO, "update hypervisor columns for logged in user in organization : " + organizationType);
		response = spogSourceServer.updateHypervisorColumnsForLoggedInUser(expectedColumns, validToken);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_USER_HYPERVISOR_COLUMN,
				organization_id, user_id, user_id, response, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_HypervisorColumns.add(user_events_code);

		test.log(LogStatus.INFO, "delete hypervisor columns for logged in user in organization : " + organizationType);
		spogSourceServer.deleteHypervisorColumnsForLoggedInUser(validToken);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_USER_HYPERVISOR_COLUMN,
				organization_id, user_id, user_id, response1, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_HypervisorColumns.add(user_events_code);

		test.log(LogStatus.INFO,
				"Create recovered resource columns for logged in user in organization: " + organizationType);
		temp = spogRecoveredResourceServer.composeRecoveredResource_Column("84129da7-d794-4cc5-bfe1-17daf6b911bb",
				"true", "false", "true", "1");
		expectedColumns = new ArrayList<>();
		expectedColumns.add(temp);
		response = spogRecoveredResourceServer.createRecoveredResourceColumnsForLoggedInUser(validToken,
				expectedColumns, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_USER_RECOVEREDRESOURCE_COLUMN,
				organization_id, user_id, user_id, response, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_RecoveryResourceColumns.add(user_events_code);

		test.log(LogStatus.INFO,
				"update recovered resource columns for logged in user in organization: " + organizationType);
		temp = spogRecoveredResourceServer.composeRecoveredResource_Column("84129da7-d794-4cc5-bfe1-17daf6b911bb",
				"true", "false", "true", "1");
		expectedColumns = new ArrayList<>();
		expectedColumns.add(temp);
		response = spogRecoveredResourceServer.updateRecoveredResourceColumnsForLoggedinUser(validToken,
				expectedColumns, expectedColumns, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_USER_RECOVEREDRESOURCE_COLUMN,
				organization_id, user_id, user_id, response, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_RecoveryResourceColumns.add(user_events_code);

		test.log(LogStatus.INFO,
				"delete recovered resource columns for logged in user in organization: " + organizationType);
		spogRecoveredResourceServer.deleteRecoveredResourceColumnsforLoggedInUserwithCheck(validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_USER_RECOVEREDRESOURCE_COLUMN,
				organization_id, user_id, user_id, response1, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_RecoveryResourceColumns.add(user_events_code);

		// create user policy columns
		test.log(LogStatus.INFO, "create user policy columns for logged in user in organization: " + organizationType);
		policy4SpogServer.setToken(validToken);
		ArrayList<HashMap<String, Object>> expected_columns_1 = new ArrayList<HashMap<String, Object>>();
		expected_columns_1 = policy4SpogServer.getPolicyColumnArrayList("Protected Sources;false;2", validToken, test);
		response = policy4SpogServer.createLoggedInUserPolicyColumns(expected_columns_1, validToken);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_USER_POLICY_COLUMN, organization_id,
				user_id, user_id, response, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_PolicyColumns.add(user_events_code);

		test.log(LogStatus.INFO, "update user policy columns for logged in user in organization: " + organizationType);
		policy4SpogServer.setToken(validToken);
		response = policy4SpogServer.updateLoggedInUserPolicyColumns(expected_columns_1, validToken);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_USER_POLICY_COLUMN, organization_id,
				user_id, user_id, response, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_PolicyColumns.add(user_events_code);

		test.log(LogStatus.INFO, "delete user policy columns for logged in user in organization: " + organizationType);
		policy4SpogServer.setToken(validToken);
		response = policy4SpogServer.deleteLoggedInUserPolicyColumns(validToken);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_USER_POLICY_COLUMN, organization_id,
				user_id, user_id, response1, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_PolicyColumns.add(user_events_code);

		// create report filter
		test.log(LogStatus.INFO, "create  report filter");
		HashMap<String, Object> reportfilterInfo = null;
		test.log(LogStatus.INFO, "compose report filter");
		filter_name = "filter_name";
		reportfilterInfo = spogReportServer.composeReportFilterInfo(source_name, destination_name, policy_id,
				destination_id, "", "", "today", System.currentTimeMillis(), System.currentTimeMillis(), "backup_jobs",
				"", "backup", "", "", filter_name, true, "backup_jobs");
		response = spogReportServer.createReportFiltersForLoggedInUser_audit(validToken, reportfilterInfo,
				SpogConstants.SUCCESS_POST, null, test);
		filter_id = response.then().extract().path("data.filter_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_REPORT_FILTER, organization_id,
				user_id, filter_id, response, organizationEmail, "user", filter_name, "report_filter");
		User_Events.add(user_events_code);

		// update report filter
		test.log(LogStatus.INFO, "update  report filter");
		reportfilterInfo = spogReportServer.composeReportFilterInfo(source_name, destination_name, policy_id,
				destination_id, "", "", "today", System.currentTimeMillis(), System.currentTimeMillis(), "backup_jobs",
				"", "backup", "", "", filter_name, true, "backup_jobs");
		response = spogReportServer.updateReportFiltersForLoggedInUser(validToken, filter_id, reportfilterInfo,
				reportfilterInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_REPORT_FILTER, organization_id,
				user_id, filter_id, response, organizationEmail, "user", filter_name, "report_filter");
		User_Events.add(user_events_code);

		// delete report filter
		test.log(LogStatus.INFO, "delete report filter");
		response = spogReportServer.deleteReportFiltersForSpecifiedUser(validToken, user_id, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_REPORT_FILTER, organization_id,
				user_id, filter_id, response1, organizationEmail, "user", filter_name, "report_filter");
		User_Events.add(user_events_code);

		// create backup job report columns
		test.log(LogStatus.INFO, "create backup job report columns");
		ArrayList<HashMap<String, Object>> expected_columns_2 = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> temp2 = new HashMap<>();
		for (int j = 0; j < 1; j++) {
			temp2 = spogReportServer.composeBackUpJobReportsColumns("b9004719-1897-4f09-abd2-7a9fb6edb768", "true",
					"3");

			expected_columns_2.add(temp2);
		}

		response = spogReportServer.createBackUpJobReportsColumnsforLoggedInUser(validToken, expected_columns_2,
				SpogConstants.SUCCESS_POST, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_USER_BACKUPJOBREPORT_COLUMN,
				organization_id, user_id, user_id, response, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);

		expected_columns_2.clear();

		// update backup job report columns
		test.log(LogStatus.INFO, "update backup job report columns");
		for (int j = 0; j < 1; j++) {
			temp2 = spogReportServer.composeBackUpJobReportsColumns("b9004719-1897-4f09-abd2-7a9fb6edb768", "true",
					"4");

			expected_columns_2.add(temp2);
		}

		response = spogReportServer.updateBackUpJobReportsColumnsForLoggedInUser(validToken, expected_columns_2,
				SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_USER_BACKUPJOBREPORT_COLUMN,
				organization_id, user_id, user_id, response, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);

		// delete BackUpJobReportsColumnsForSpecifiedUser
		test.log(LogStatus.INFO, "delete BackUpJobReportsColumnsForSpecifiedUser ");
		spogReportServer.deleteBackUpJobReportsColumnsForSpecifiedUser(validToken, user_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_USER_BACKUPJOBREPORT_COLUMN,
				organization_id, user_id, user_id, response1, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);

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
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_USER_RESTOREJOBREPORT_COLUMN,
				organization_id, user_id, user_id, response, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);

		expected_columns_2.clear();

		// update restore job report columns
		test.log(LogStatus.INFO, "update restore job report columns");
		for (int j = 0; j < 1; j++) {
			temp2 = spogReportServer.composeRestoreJobReports_Column("07871b87-6ec5-44ae-a1bb-17abfb75a9f9", "true",
					"false", "true", "4");

			expected_columns_2.add(temp2);
		}

		response = spogReportServer.updateRestoreJobReportsColumnsForLoggedinUser(validToken, expected_columns_2,
				expected_columns_2, SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_USER_RESTOREJOBREPORT_COLUMN,
				organization_id, user_id, user_id, response, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);

		// delete RestoreJobReportsColumnsForSpecifiedUser
		test.log(LogStatus.INFO, "delete RestoreJobReportsColumnsForSpecifiedUser ");
		spogReportServer.deleteRestoreJobReportsColumnsforLoggedInUserwithCheck(validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_USER_RESTOREJOBREPORT_COLUMN,
				organization_id, user_id, user_id, response1, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);

		test.log(LogStatus.INFO, "The expected data is " + User_Events.toString());

		// modify the organization by Id--- This is a bug

		// modify the loggedIn user organization
		test.log(LogStatus.INFO, "modify the logged In user organization");
		spogServer.setToken(validToken);
		organizationName = "spog_udp_qa_auto_ramya";
		response = spogServer.UpdateLoggedInOrganization(organizationName, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_LOGIN_USER_ORGANIZATION,
				organization_id, user_id, organization_id, response, organizationEmail, "user", organizationName,
				"organization");
		User_Events.add(user_events_code);
		User_Events_Org.add(user_events_code);
		User_Events_Sites_Org.add(user_events_code);

		// All events of the user

		test.log(LogStatus.INFO, "Create the user");
		response = spogServer.createUser(userEmail, userPassword, FirstName, LastName, Role_Id, organization_id, test);
		String user_id1 = spogServer.checkCreateUser(response, SpogConstants.SUCCESS_POST, userEmail, FirstName,
				LastName, Role_Id, organization_id, "", test);
		// spogServer.userLogin(userEmail, userPassword,test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_USER, organization_id, user_id,
				user_id1, response, userEmail, "user", userEmail, "user");
		User_Events.add(user_events_code);

		test.log(LogStatus.INFO, "Modify the user");
		response = spogServer.updateUserById(user_id1, userEmail, "", FirstName + "modi", LastName, Role_Id,
				organization_id, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_USER, organization_id, user_id,
				user_id1, response, userEmail, "user", userEmail, "user");
		User_Events.add(user_events_code);
		User_Events_User.add(user_events_code);

		/*
		 * test.log(LogStatus.INFO,
		 * "Modify the logged in user password by user id"); String
		 * changepassword = "Mclaren@2016"; response =
		 * spogServer.changePasswordForSpecifiedUser(user_id, organizationPwd,
		 * changepassword); spogServer.checkResponseStatus(response,
		 * SpogConstants.SUCCESS_GET_PUT_DELETE, test); user_events_code =
		 * spogServer.storetheuserevents(AuditCodeConstants.
		 * MODIFY_USER_PASSWORD, organization_id, user_id, user_id, response,
		 * userEmail, "user", userEmail, "user");
		 * User_Events.add(user_events_code);
		 * User_Events_User.add(user_events_code);
		 * 
		 * test.log(LogStatus.INFO, "Modify the login user"); response =
		 * spogServer.UpdateLoggedInUser(organizationEmail, "Mclaren@2016",
		 * organizationFirstName, organizationLastName + "mod", Role_Id,
		 * organization_id); user_events_code =
		 * spogServer.storetheuserevents(AuditCodeConstants.MODIFY_LOGIN_USER,
		 * organization_id, user_id, user_id, response, organizationEmail,
		 * "user", organizationEmail, "user");
		 * User_Events.add(user_events_code);
		 * User_Events_User.add(user_events_code);
		 */

		test.log(LogStatus.INFO, "Delete the user");
		spogServer.DeleteUserById(user_id1, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_USER, organization_id, user_id,
				user_id1, response1, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_User.add(user_events_code);

		test.log(LogStatus.INFO, "Modify login user password");
		spogServer.setToken(validToken);
		response = spogServer.changePasswordForLoggedInUser(organizationPwd, "Mclaren@2016");
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_LOGIN_USER_PASSWORD, organization_id,
				user_id, user_id, response, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_User.add(user_events_code);
		User_Events_byCodeid.add(user_events_code);

		for (int i = 0; i < 2; i++) {
			if (i == 1) {
				validToken = csr_read_only_validToken;
			}
			ArrayList<HashMap> Expectedresponse = new ArrayList<HashMap>();

			test.log(LogStatus.INFO, "Prepare the URL");
			String filter = "code_id;in;4001|4002";
			String additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO, "compose audit trails for validation ");
			Expectedresponse = compose_expected_response(filter, User_Events);
			test.log(LogStatus.INFO,
					"Get the audit trail by direct org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, Expectedresponse, 1, 20, "",
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			filter = "code_id;in;1028|1029|1030";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO, "compose audit trails for validation ");
			Expectedresponse = compose_expected_response(filter, User_Events);
			test.log(LogStatus.INFO,
					"Get the audit trail by direct org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, Expectedresponse, 1, 20, "",
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			filter = "code_id;in;2025|2026|2027";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO, "compose audit trails for validation ");
			Expectedresponse = compose_expected_response(filter, User_Events);
			test.log(LogStatus.INFO,
					"Get the audit trail by direct org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, Expectedresponse, 1, 20,
			// "",
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			filter = "code_id;in;2028|2029|2030";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO, "compose audit trails for validation ");
			Expectedresponse = compose_expected_response(filter, User_Events);
			test.log(LogStatus.INFO,
					"Get the audit trail by direct org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, Expectedresponse, 1, 20,
			// "",
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// get Audit trails for list reports
			ArrayList<HashMap> expectedResponse = new ArrayList<HashMap>();
			test.log(LogStatus.INFO, "Prepare the URL");
			filter = "code_id;in;1025|1026|1027";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by direct org ID with ascending order and current page is 1 and pagesize 20");
			expectedResponse = compose_expected_response(filter, User_Events);
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, expectedResponse, 1, 20, "",
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;7", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_byCodeid, 1, 20,
					"", "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;701|702|703", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_Group, 1, 20,
					"code_id;in;701|702|703", "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;801|802", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_AddsourcetoGroup,
					1, 20, "code_id;in;801|802", "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;1001|1002|1003", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_SourceFilter, 1,
					20, "code_id;in;1001|1002|1003", "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;1004|1005|1006", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE,
					User_Events_DestinationFilter, 1, 20, "code_id;in;1004|1005|1006", "create_ts;asc",
					SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;1007|1008|1009", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_JobFilter, 1, 20,
					"code_id;in;1007", "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;1010|1011|1012", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_LogFilter, 1, 20,
					"code_id;in;1010|1011|1012", "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;1013|1014|1015", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_UserFilter, 1,
					20, "code_id;in;1013|1014|1015", "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;1019|1020|1021", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE,
					User_Events_RecoveryResourceFilters, 1, 20, "code_id;in;1019|1020|1021", "create_ts;asc",
					SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;1022|1023|1024", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_PolicyFilters, 1,
					20, "code_id;in;1022|1023|1024", "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;2001|2002|2003", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_SourceColumns,
			// 1,
			// 20, "code_id;in;2001|2002|2003", "create_ts;asc",
			// SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;2007|2008|2009", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE,
			// User_Events_HypervisorColumns,
			// 1, 20, "code_id;in;2007|2008|2009", "create_ts;asc",
			// SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			// additionalUrl =
			// spogServer.PrepareURL("code_id;in;2010|2011|2012",
			// "create_ts;asc", 1, 20, test);
			additionalUrl = spogServer.PrepareURL("code_id;in;2010|2011|2012", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_UserColumns, 1,
			// 20,
			// "code_id;in;2010|2011|2012", "create_ts;asc",
			// SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;2004|2005|2006", "create_ts;asc", 1, 20, test);
			// additionalUrl = spogServer.PrepareURL("code_id;in;2004",
			// "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE,
			// User_Events_DestinationColumns,
			// 1, 20, "code_id;in;2004|2005|2006", "create_ts;asc",
			// SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;2013|2014|2015", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_JobColumns, 1,
			// 20,
			// "code_id;in;2013|2014|2015", "create_ts;asc",
			// SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;2016|2017|2018", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_LogColumns, 1,
			// 20,
			// "code_id;in;2016|2017|2018", "create_ts;asc",
			// SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;2019|2020|2021", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE,
			// User_Events_RecoveryResourceColumns, 1, 20,
			// "code_id;in;2019|2020|2021", "create_ts;asc",
			// SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;2022|2023|2024", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_PolicyColumns,
			// 1,
			// 20, "code_id;in;2022|2023|2024", "create_ts;asc",
			// SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;4006|4007", "create_ts;asc", 1, 20, test);
			// additionalUrl = spogServer.PrepareURL("code_id;in;4007",
			// "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_jobs, 1, 20,
					"code_id;in;4006|4007", "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;4008|4009", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_jobs_data, 1, 20,
					"code_id;in;4008|4009", "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;4001|4002|4003", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_logs, 1, 20,
					"code_id;in;4001|4002|4003", "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;501|502|503", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_Sources, 1, 20,
					"code_id;in;501|502|503", "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;601|602|603", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_Destinations, 1,
					20, "code_id;in;601|603", "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;401|402|403|404", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_Site, 1, 20,
					"code_id;in;401|402|403|404", "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");

			additionalUrl = spogServer.PrepareURL("code_id;in;401|402|403|404,create_ts;<;" + tomorrow, "", 1, 20,
					test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			// additionalUrl="filter=code_id:[401|402|403],code_id:[102|108]&sort=create_ts&page=1&page_size=20"
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_Sites_Org, 1, 20,
					"code_id;in;401|402|403|404,create_ts;<;" + tomorrow, "create_ts;asc",
					SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;401|402|403|404,create_ts;<;" + yesterday, "", 1, 20,
					test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			// additionalUrl="filter=code_id:[401|402|403],code_id:[102|108]&sort=create_ts&page=1&page_size=20"
			test.log(LogStatus.INFO, "Get the audit trail by timestamp less than the current day should result in 404");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			// spogServer.checkaudittraildata(response,
			// SpogConstants.RESOURCE_NOT_EXIST, User_Events_Sites_Org, 1, 20,
			// "",
			// "", SpogMessageCode.AUDIT_RESOURCE_NOT_FOUND, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("", "create_ts;asc", 1, 4, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 10");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events, 1, 4,
					"create_ts;asc", "", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("", "create_ts;asc", 2, 4, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 2 and pagesize 4");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events, 2, 4, "",
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

		}

		test.log(LogStatus.INFO,
				"Performing the operations to delete the user and orginzation by logging in as csr admin");
		test.log(LogStatus.INFO, "Login in as csr admin to delete the organization");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		csr_token = spogServer.getJWTToken();
		spogDestinationServer.recycleCloudVolumesAndDestroyOrg(csr_token, organization_id);

	}

	@DataProvider(name = "suborganizationAndUserInfo")
	public final Object[][] getsubOrganizationAndUserInfo() {
		return new Object[][] { { msp_org_name, SpogConstants.MSP_ORG, msp_org_email, common_password,
				msp_org_first_name, msp_org_last_name, msp_user_name_email, common_password, msp_user_first_name,
				msp_user_last_name, SpogConstants.MSP_ADMIN } };
	}

	@Test(dataProvider = "suborganizationAndUserInfo")
	public void getaudittraildataofusers_byInvalidsubOrgId(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName, String organizationLastName,
			String userEmail, String userPassword, String FirstName, String LastName, String Role_Id) {
		ArrayList<HashMap> User_Events = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_Suborg = new ArrayList<HashMap>();
		ArrayList<ResponseBody> userresponse = new ArrayList<ResponseBody>();
		HashMap<String, Object> user_events_code = new HashMap<String, Object>();
		Response response1 = null;
		String additionalUrl = null;
		String gateway_hostname = "Ramya";
		String site_version = "1.0.0";
		test = rep.startTest("getaudittraildataofusers_byInvalidsubOrgId");
		test.assignAuthor("Ramya.Nagepalli");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;
		FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + FirstName;
		LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + LastName;
		userEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;

		test.log(LogStatus.INFO, "Login in as CSR Admin user");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		String csr_token = spogServer.getJWTToken();

		String csr_user_id = spogServer.GetLoggedinUser_UserID();
		test.log(LogStatus.INFO, "The csr admin user id is " + csr_user_id);

		test.log(LogStatus.INFO, "Create a organization of type " + organizationType);
		String organization_id = spogServer.CreateOrganizationWithCheck(organizationName + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "The organization id is " + organization_id);

		test.log(LogStatus.INFO, "Login in as msp user " + organizationEmail);
		spogServer.userLogin(organizationEmail, organizationPwd, test);
		String msp_token = spogServer.getJWTToken();
		String msp_user = spogServer.GetLoggedinUser_UserID();
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.LOGIN_USER, organization_id, msp_user,
				msp_user, response1, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);

		test.log(LogStatus.INFO, "Create a sub org under MSP");
		String organization_name = "spog_udp_qa_auto_ramya_msp_child";
		Response response = spogServer.createAccount(organization_id, organization_name, organization_id);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		String sub_org_Id = spogServer.getOrganizationID(response);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_SUB_ORGANIZATION, organization_id,
				msp_user, sub_org_Id, response, organizationEmail, "user", organization_name, "organization");
		User_Events.add(user_events_code);

		// create user
		test.log(LogStatus.INFO, "Create a direct user under the sub org " + sub_org_Id);
		response = spogServer.createUser(userEmail, userPassword, FirstName, LastName, SpogConstants.DIRECT_ADMIN,
				sub_org_Id, test);
		String user_id = spogServer.checkCreateUser(response, SpogConstants.SUCCESS_POST, userEmail, FirstName,
				LastName, SpogConstants.DIRECT_ADMIN, sub_org_Id, "", test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_USER, organization_id, msp_user,
				user_id, response, organizationEmail, "user", userEmail, "user");
		User_Events.add(user_events_code);

		test.log(LogStatus.INFO, "Login with the direct user of sub org");
		spogServer.userLogin(userEmail, userPassword, test);
		String direct_token = spogServer.getJWTToken();
		String direct_user = spogServer.GetLoggedinUser_UserID();
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.LOGIN_USER, sub_org_Id, direct_user,
				direct_user, response1, userEmail, "user", userEmail, "user");
		User_Events_Suborg.add(user_events_code);

		test.log(LogStatus.INFO, "Prepare the URL with no filter, no sort and pageno 1 and pagesize 20");
		additionalUrl = spogServer.PrepareURL("", "", 1, 20, test);
		String invalidsuborgId = UUID.randomUUID().toString();
		test.log(LogStatus.INFO, "Get the audit trail by Invalid sub orgId " + invalidsuborgId);
		response = spogServer.getaudittrailbyorgId(direct_token, invalidsuborgId, additionalUrl, test);
		test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
		spogServer.checkResponseStatus(response, SpogConstants.RESOURCE_NOT_EXIST);
		spogServer.checkaudittraildata(response, SpogConstants.RESOURCE_NOT_EXIST, User_Events, 1, 20, "", "",
				SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED, test);

		test.log(LogStatus.INFO,
				"Performing the operations to delete the user and orginzation by logging in as csr admin");
		test.log(LogStatus.INFO, "Login in as csr admin to delete the organization");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		test.log(LogStatus.INFO, "Login in as csr admin to delete the organization");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		csr_token = spogServer.getJWTToken();
		spogDestinationServer.recycleCloudVolumesAndDestroyOrg(csr_token, organization_id);
	}

	@Test(dataProvider = "suborganizationAndUserInfo")
	public void getaudittraildataofusers_bysubOrgId_MissingJWT(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName, String organizationLastName,
			String userEmail, String userPassword, String FirstName, String LastName, String Role_Id) {
		ArrayList<HashMap> User_Events = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_Suborg = new ArrayList<HashMap>();
		ArrayList<ResponseBody> userresponse = new ArrayList<ResponseBody>();
		HashMap<String, Object> user_events_code = new HashMap<String, Object>();
		Response response1 = null;
		String additionalUrl = null;
		String gateway_hostname = "Ramya";
		String site_version = "1.0.0";
		test = rep.startTest("getaudittraildataofusers_bysubOrgId_MissingJWT");
		test.assignAuthor("Ramya.Nagepalli");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;
		FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + FirstName;
		LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + LastName;
		userEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;

		test.log(LogStatus.INFO, "Login in as CSR Admin user");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		String csr_token = spogServer.getJWTToken();

		String csr_user_id = spogServer.GetLoggedinUser_UserID();
		test.log(LogStatus.INFO, "The csr admin user id is " + csr_user_id);

		test.log(LogStatus.INFO, "Create a organization of type " + organizationType);
		String organization_id = spogServer.CreateOrganizationWithCheck(organizationName + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "The organization id is " + organization_id);

		test.log(LogStatus.INFO, "Login in as msp user " + organizationEmail);
		spogServer.userLogin(organizationEmail, organizationPwd, test);
		String msp_token = spogServer.getJWTToken();
		String msp_user = spogServer.GetLoggedinUser_UserID();
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.LOGIN_USER, organization_id, msp_user,
				msp_user, response1, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);

		test.log(LogStatus.INFO, "Create a sub org under MSP");
		String organization_name = "spog_udp_qa_auto_ramya_msp_child";
		Response response = spogServer.createAccount(organization_id, organization_name, organization_id);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		String sub_org_Id = spogServer.getOrganizationID(response);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_SUB_ORGANIZATION, organization_id,
				msp_user, sub_org_Id, response, organizationEmail, "user", organization_name, "organization");
		User_Events.add(user_events_code);

		// create user
		test.log(LogStatus.INFO, "Create a direct user under the sub org " + sub_org_Id);
		response = spogServer.createUser(userEmail, userPassword, FirstName, LastName, SpogConstants.DIRECT_ADMIN,
				sub_org_Id, test);
		String user_id = spogServer.checkCreateUser(response, SpogConstants.SUCCESS_POST, userEmail, FirstName,
				LastName, SpogConstants.DIRECT_ADMIN, sub_org_Id, "", test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_USER, organization_id, msp_user,
				user_id, response, organizationEmail, "user", userEmail, "user");
		User_Events.add(user_events_code);

		test.log(LogStatus.INFO, "Login with the direct user of sub org");
		spogServer.userLogin(userEmail, userPassword, test);
		String direct_token = spogServer.getJWTToken();
		String direct_user = spogServer.GetLoggedinUser_UserID();
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.LOGIN_USER, sub_org_Id, direct_user,
				direct_user, response1, userEmail, "user", userEmail, "user");
		User_Events_Suborg.add(user_events_code);

		test.log(LogStatus.INFO, "Prepare the URL with no filter, no sort and pageno 1 and pagesize 20");
		additionalUrl = spogServer.PrepareURL("", "", 1, 20, test);
		test.log(LogStatus.INFO, "Get the audit trail by sub orgId " + sub_org_Id);
		response = spogServer.getaudittrailbyorgId("", sub_org_Id, additionalUrl, test);
		test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
		spogServer.checkResponseStatus(response, SpogConstants.NOT_LOGGED_IN);
		spogServer.checkaudittraildata(response, SpogConstants.NOT_LOGGED_IN, User_Events, 1, 20, "", "",
				SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);

		test.log(LogStatus.INFO,
				"Performing the operations to delete the user and orginzation by logging in as csr admin");
		test.log(LogStatus.INFO, "Login in as csr admin to delete the organization");
		test.log(LogStatus.INFO, "Login in as csr admin to delete the organization");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		csr_token = spogServer.getJWTToken();
		spogDestinationServer.recycleCloudVolumesAndDestroyOrg(csr_token, organization_id);
	}

	@Test(dataProvider = "suborganizationAndUserInfo")
	public void getaudittraildataofusers_bysubOrgId_InvalidJWT(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName, String organizationLastName,
			String userEmail, String userPassword, String FirstName, String LastName, String Role_Id) {
		ArrayList<HashMap> User_Events = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_Suborg = new ArrayList<HashMap>();
		ArrayList<ResponseBody> userresponse = new ArrayList<ResponseBody>();
		HashMap<String, Object> user_events_code = new HashMap<String, Object>();
		Response response1 = null;
		String additionalUrl = null;
		String gateway_hostname = "Ramya";
		String site_version = "1.0.0";
		test = rep.startTest("getaudittraildataofusers_bysubOrgId_InvalidJWT");
		test.assignAuthor("Ramya.Nagepalli");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;
		FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + FirstName;
		LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + LastName;
		userEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;

		test.log(LogStatus.INFO, "Login in as CSR Admin user");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		String csr_token = spogServer.getJWTToken();

		String csr_user_id = spogServer.GetLoggedinUser_UserID();
		test.log(LogStatus.INFO, "The csr admin user id is " + csr_user_id);

		test.log(LogStatus.INFO, "Create a organization of type " + organizationType);
		String organization_id = spogServer.CreateOrganizationWithCheck(organizationName + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "The organization id is " + organization_id);

		test.log(LogStatus.INFO, "Login in as msp user " + organizationEmail);
		spogServer.userLogin(organizationEmail, organizationPwd, test);
		String msp_token = spogServer.getJWTToken();
		String msp_user = spogServer.GetLoggedinUser_UserID();
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.LOGIN_USER, organization_id, msp_user,
				msp_user, response1, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);

		test.log(LogStatus.INFO, "Create a sub org under MSP");
		String organization_name = "spog_udp_qa_auto_ramya_msp_child";
		Response response = spogServer.createAccount(organization_id, organization_name, organization_id);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		String sub_org_Id = spogServer.getOrganizationID(response);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_SUB_ORGANIZATION, organization_id,
				msp_user, sub_org_Id, response, organizationEmail, "user", organization_name, "organization");
		User_Events.add(user_events_code);

		// create user
		test.log(LogStatus.INFO, "Create a direct user under the sub org " + sub_org_Id);
		response = spogServer.createUser(userEmail, userPassword, FirstName, LastName, SpogConstants.DIRECT_ADMIN,
				sub_org_Id, test);
		String user_id = spogServer.checkCreateUser(response, SpogConstants.SUCCESS_POST, userEmail, FirstName,
				LastName, SpogConstants.DIRECT_ADMIN, sub_org_Id, "", test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_USER, organization_id, msp_user,
				user_id, response, organizationEmail, "user", userEmail, "user");
		User_Events.add(user_events_code);

		test.log(LogStatus.INFO, "Login with the direct user of sub org");
		spogServer.userLogin(userEmail, userPassword, test);
		String direct_token = spogServer.getJWTToken();
		String direct_user = spogServer.GetLoggedinUser_UserID();
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.LOGIN_USER, sub_org_Id, direct_user,
				direct_user, response1, organizationEmail, "user", organizationEmail, "user");
		User_Events_Suborg.add(user_events_code);

		test.log(LogStatus.INFO, "Prepare the URL with no filter, no sort and pageno 1 and pagesize 20");
		additionalUrl = spogServer.PrepareURL("", "", 1, 20, test);
		test.log(LogStatus.INFO, "Get the audit trail by sub orgId using invalid JWT token" + sub_org_Id);
		response = spogServer.getaudittrailbyorgId(direct_token + "J", sub_org_Id, additionalUrl, test);
		test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
		spogServer.checkResponseStatus(response, SpogConstants.NOT_LOGGED_IN);
		spogServer.checkaudittraildata(response, SpogConstants.NOT_LOGGED_IN, User_Events, 1, 20, "", "",
				SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);

		test.log(LogStatus.INFO, "Login in as csr admin to delete the organization");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		csr_token = spogServer.getJWTToken();
		spogDestinationServer.recycleCloudVolumesAndDestroyOrg(csr_token, organization_id);
	}

	@Test(dataProvider = "suborganizationAndUserInfo")
	public void getaudittraildataofusers_bysuborg_otherOrgId(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName, String organizationLastName,
			String userEmail, String userPassword, String FirstName, String LastName, String Role_Id) {
		ArrayList<HashMap> User_Events = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_Suborg = new ArrayList<HashMap>();
		ArrayList<ResponseBody> userresponse = new ArrayList<ResponseBody>();
		HashMap<String, Object> user_events_code = new HashMap<String, Object>();
		Response response1 = null;
		String additionalUrl = null;
		String gateway_hostname = "Ramya";
		String site_version = "1.0.0";
		test = rep.startTest("getaudittraildataofusers_bysuborg_otherOrgId");

		String prefix_msp_account_admin = "spog_udp_qa_auto_ramya_msp_account";
		String msp_account_admin_email = prefix_msp_account_admin + postfix_email;
		String msp_account_admin_first_name = prefix_msp_account_admin + "_first_name";
		String msp_account_admin_last_name = prefix_msp_account_admin + "_last_name";
		String msp_account_admin_validToken;
		String prefix = RandomStringUtils.randomAlphanumeric(8);

		test.assignAuthor("Ramya.Nagepalli");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;
		FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + FirstName;
		LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + LastName;
		userEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;

		test.log(LogStatus.INFO, "Login in as CSR Admin user");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		String csr_token = spogServer.getJWTToken();

		String csr_user_id = spogServer.GetLoggedinUser_UserID();
		test.log(LogStatus.INFO, "The csr admin user id is " + csr_user_id);

		test.log(LogStatus.INFO, "Create a organization of type " + organizationType);
		String organization_id = spogServer.CreateOrganizationWithCheck(organizationName + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "The organization id is " + organization_id);

		String organization_id1 = spogServer.CreateOrganizationWithCheck(organizationName + "_100" + org_model_prefix,
				organizationType, "1" + organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);

		test.log(LogStatus.INFO, "Login in as msp user " + organizationEmail);
		spogServer.userLogin(organizationEmail, organizationPwd, test);
		String msp_token = spogServer.getJWTToken();
		String msp_user = spogServer.GetLoggedinUser_UserID();
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.LOGIN_USER, organization_id, msp_user,
				msp_user, response1, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);

		test.log(LogStatus.INFO, "Create a sub org under MSP");
		String organization_name = "spog_udp_qa_auto_ramya_msp_child";
		Response response = spogServer.createAccount(organization_id, organization_name, organization_id);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		String sub_org_Id = spogServer.getOrganizationID(response);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_SUB_ORGANIZATION, organization_id,
				msp_user, sub_org_Id, response, organizationEmail, "user", organization_name, "organization");
		User_Events.add(user_events_code);

		test.log(LogStatus.INFO, "Create a second sub org under MSP");
		organization_name = spogServer.ReturnRandom("spogqa_account");
		response = spogServer.createAccount(organization_id, organization_name, organization_id);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		String sub_org_Id_1 = spogServer.getOrganizationID(response);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_SUB_ORGANIZATION, organization_id,
				msp_user, sub_org_Id_1, response, organizationEmail, "user", organization_name, "organization");
		User_Events.add(user_events_code);

		// create user
		test.log(LogStatus.INFO, "Create a direct user under the sub org " + sub_org_Id);
		response = spogServer.createUser(userEmail, userPassword, FirstName, LastName, SpogConstants.DIRECT_ADMIN,
				sub_org_Id, test);
		String user_id = spogServer.checkCreateUser(response, SpogConstants.SUCCESS_POST, userEmail, FirstName,
				LastName, SpogConstants.DIRECT_ADMIN, sub_org_Id, "", test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_USER, organization_id, msp_user,
				user_id, response, organizationEmail, "user", userEmail, "user");
		User_Events.add(user_events_code);

		test.log(LogStatus.INFO, "Create a msp account admin under msp org");
		msp_account_admin_email = prefix + msp_account_admin_email;
		spogServer.setToken(msp_token);
		response = spogServer.createUser(msp_account_admin_email, common_password, msp_account_admin_first_name,
				msp_account_admin_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, organization_id, test);
		String user_id_msp_account_admin = spogServer.checkCreateUser(response, SpogConstants.SUCCESS_POST,
				msp_account_admin_email, msp_account_admin_first_name, msp_account_admin_last_name,
				SpogConstants.MSP_ACCOUNT_ADMIN, organization_id, "", test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_USER, organization_id, msp_user,
				user_id, response, organizationEmail, "user", msp_account_admin_email, "user");
		User_Events.add(user_events_code);

		spogServer.userLogin(msp_account_admin_email, common_password);
		msp_account_admin_validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "The token is :" + msp_account_admin_validToken);
		// String direct_user = spogServer.GetLoggedinUser_UserID();
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.LOGIN_USER, organization_id,
				user_id_msp_account_admin, user_id_msp_account_admin, response1, msp_account_admin_email, "user",
				msp_account_admin_email, "user");
		User_Events.add(user_events_code);

		response = userSpogServer.assignMspAccountAdmins(organization_id, sub_org_Id,
				new String[] { user_id_msp_account_admin }, msp_token);

		test.log(LogStatus.INFO, "Login with the direct user of sub org");
		spogServer.userLogin(userEmail, userPassword, test);
		String direct_token = spogServer.getJWTToken();
		String direct_user = spogServer.GetLoggedinUser_UserID();
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.LOGIN_USER, sub_org_Id, direct_user,
				direct_user, response1, userEmail, "user", userEmail, "user");
		User_Events_Suborg.add(user_events_code);

		test.log(LogStatus.INFO, "Prepare the URL with no filter, no sort and pageno 1 and pagesize 20");
		additionalUrl = spogServer.PrepareURL("", "", 1, 20, test);
		test.log(LogStatus.INFO, "Get the audit trail by other orgId using valid JWT token" + organization_id1);
		response = spogServer.getaudittrailbyorgId(direct_token, organization_id1, additionalUrl, test);
		test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
		spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS);
		spogServer.checkaudittraildata(response, SpogConstants.INSUFFICIENT_PERMISSIONS, User_Events, 1, 20, "", "",
				SpogMessageCode.RESOURCE_PERMISSION_DENY, test);

		test.log(LogStatus.INFO, "Prepare the URL with no filter, no sort and pageno 1 and pagesize 20");
		additionalUrl = spogServer.PrepareURL("", "", 1, 20, test);
		test.log(LogStatus.INFO,
				"Get the audit trail for second sub org which is not added to msp account admin and using valid JWT token of msp account admin"
						+ organization_id1);
		response = spogServer.getaudittrailbyorgId(msp_account_admin_validToken, sub_org_Id_1, additionalUrl, test);
		test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
		spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS);
		spogServer.checkaudittraildata(response, SpogConstants.INSUFFICIENT_PERMISSIONS, User_Events, 1, 20, "", "",
				SpogMessageCode.RESOURCE_PERMISSION_DENY, test);

		test.log(LogStatus.INFO, "Prepare the URL with no filter, no sort and pageno 1 and pagesize 20");
		additionalUrl = spogServer.PrepareURL("", "", 1, 20, test);
		test.log(LogStatus.INFO,
				"Get the audit trail for org that does not exist and using valid JWT token of msp account admin"
						+ organization_id1);
		response = spogServer.getaudittrailbyorgId(msp_account_admin_validToken, UUID.randomUUID().toString(),
				additionalUrl, test);
		test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
		spogServer.checkResponseStatus(response, SpogConstants.RESOURCE_NOT_EXIST);
		spogServer.checkaudittraildata(response, SpogConstants.RESOURCE_NOT_EXIST, User_Events, 1, 20, "", "",
				SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED, test);

		test.log(LogStatus.INFO, "Login in as csr admin to delete the organization");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		csr_token = spogServer.getJWTToken();
		spogDestinationServer.recycleCloudVolumesAndDestroyOrg(csr_token, organization_id);
	}

	@Test(dataProvider = "suborganizationAndUserInfo")
	public void getaudittraildataofusers_sites_orgs_bysuborgId(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName, String organizationLastName,
			String userEmail, String userPassword, String FirstName, String LastName, String Role_Id) {
		test = rep.startTest("getaudittraildataofusers_sites_orgs_bysuborgId");
		test.assignAuthor("Ramya.Nagepalli");

		String prefix_msp_account_admin = "spog_udp_qa_auto_ramya_msp_account";
		String msp_account_admin_email = prefix_msp_account_admin + postfix_email;
		String msp_account_admin_first_name = prefix_msp_account_admin + "_first_name";
		String msp_account_admin_last_name = prefix_msp_account_admin + "_last_name";
		String msp_account_admin_validToken;
		String prefix = RandomStringUtils.randomAlphanumeric(8);

		ArrayList<HashMap> User_Events_Destinations = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_Sources = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_Suborg = new ArrayList<HashMap>();
		ArrayList<ResponseBody> userresponse = new ArrayList<ResponseBody>();
		HashMap<String, Object> user_events_code = new HashMap<String, Object>();

		ArrayList<HashMap> User_Events_Group = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_AddsourcetoGroup = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_SourceFilter = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_DestinationFilter = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_JobFilter = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_LogFilter = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_UserFilter = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_HypervisorFilter = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_RecoveryResourceFilters = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_PolicyFilters = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_UserColumns = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_JobColumns = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_LogColumns = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_SourceColumns = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_DestinationColumns = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_HypervisorColumns = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_RecoveryResourceColumns = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_PolicyColumns = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_jobs = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_jobs_data = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_logs = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_logs_data = new ArrayList<HashMap>();
		Response response1 = null;
		ArrayList<HashMap<String, Object>> expectedColumns = new ArrayList<>();
		HashMap<String, Object> temp = new HashMap<>();

		ArrayList<HashMap> User_Events_policies = new ArrayList<HashMap>();
		// Related to policies
		HashMap<String, Object> scheduleSettingDTO = new HashMap<>();
		ArrayList<HashMap<String, Object>> schedules = new ArrayList<>();
		String schedule_id = spogServer.returnRandomUUID();
		String task_id = spogServer.returnRandomUUID();
		String throttle_id = spogServer.returnRandomUUID();
		String throttle_type = "network";
		String policy_name = spogServer.ReturnRandom("test");
		String policy_description = spogServer.ReturnRandom("description");
		String task_type = null;
		String destination_name = null;

		String additionalUrl = null;
		String gateway_hostname = "Ramya";
		String site_version = "1.0.0";
		String source_id = null;
		String destination_id = null;
		String sourcegroup_id = null;
		String site_token = null;
		String validToken = null;

		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;
		FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + FirstName;
		LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + LastName;
		userEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;

		test.log(LogStatus.INFO, "Login in as CSR Admin user");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		String csr_token = spogServer.getJWTToken();

		String csr_user_id = spogServer.GetLoggedinUser_UserID();
		test.log(LogStatus.INFO, "The csr admin user id is " + csr_user_id);

		test.log(LogStatus.INFO, "Create a organization of type " + organizationType);
		String organization_id = spogServer.CreateOrganizationByEnrollWithCheck(organizationName + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "The organization id is " + organization_id);

		test.log(LogStatus.INFO, "Login in as msp user " + organizationEmail);
		spogServer.userLogin(organizationEmail, organizationPwd, test);
		String msp_token = spogServer.getJWTToken();
		String msp_user = spogServer.GetLoggedinUser_UserID();
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.LOGIN_USER, organization_id, msp_user,
				msp_user, response1, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);

		test.log(LogStatus.INFO, "Create a sub org under MSP");
		String organization_name = "spog_udp_qa_auto_ramya_msp_child";
		Response response = spogServer.createAccount(organization_id, organization_name, organization_id);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		String sub_org_Id = spogServer.getOrganizationID(response);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_SUB_ORGANIZATION, organization_id,
				msp_user, sub_org_Id, response, organizationEmail, "user", organization_name, "organization");
		User_Events.add(user_events_code);

		// create user
		test.log(LogStatus.INFO, "Create a direct user under the sub org " + sub_org_Id);
		response = spogServer.createUser(userEmail, userPassword, FirstName, LastName, SpogConstants.DIRECT_ADMIN,
				sub_org_Id, test);
		String user_id = spogServer.checkCreateUser(response, SpogConstants.SUCCESS_POST, userEmail, FirstName,
				LastName, SpogConstants.DIRECT_ADMIN, sub_org_Id, "", test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_USER, organization_id, msp_user,
				user_id, response, organizationEmail, "user", userEmail, "user");
		User_Events.add(user_events_code);

		// Create msp_account_admin
		test.log(LogStatus.INFO, "Create a msp account admin under msp org");
		msp_account_admin_email = prefix + msp_account_admin_email;
		spogServer.setToken(msp_token);
		response = spogServer.createUser(msp_account_admin_email, common_password, msp_account_admin_first_name,
				msp_account_admin_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, organization_id, test);
		String user_id_msp_account_admin = spogServer.checkCreateUser(response, SpogConstants.SUCCESS_POST,
				msp_account_admin_email, msp_account_admin_first_name, msp_account_admin_last_name,
				SpogConstants.MSP_ACCOUNT_ADMIN, organization_id, "", test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_USER, organization_id, msp_user,
				user_id, response, organizationEmail, "user", msp_account_admin_email, "user");
		User_Events.add(user_events_code);

		spogServer.userLogin(msp_account_admin_email, common_password);
		msp_account_admin_validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "The token is :" + msp_account_admin_validToken);
		// String direct_user = spogServer.GetLoggedinUser_UserID();
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.LOGIN_USER, organization_id,
				user_id_msp_account_admin, user_id_msp_account_admin, response1, msp_account_admin_email, "user",
				msp_account_admin_email, "user");
		User_Events.add(user_events_code);

		response = userSpogServer.assignMspAccountAdmins(organization_id, sub_org_Id,
				new String[] { user_id_msp_account_admin }, msp_token);

		test.log(LogStatus.INFO, "Login with the direct user of sub org");
		spogServer.userLogin(userEmail, userPassword, test);
		String direct_token = spogServer.getJWTToken();
		validToken = direct_token;
		String direct_user = spogServer.GetLoggedinUser_UserID();
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.LOGIN_USER, sub_org_Id, direct_user,
				direct_user, response1, userEmail, "user", userEmail, "user");
		User_Events_Suborg.add(user_events_code);

		test.log(LogStatus.INFO, "Create a site");
		String site_registration_key = null;
		String siteName = spogServer.getRandomSiteName("TestCreate");
		test.log(LogStatus.INFO, "Generated a Random SiteName " + siteName);
		String sitetype = siteType.gateway.toString();
		test.log(LogStatus.INFO, "The siteType :" + sitetype);

		test.log(LogStatus.INFO, "Creating a site For Logged in user");
		response = spogServer.createSite(siteName, sitetype, sub_org_Id, direct_token, test);
		Map<String, String> sitecreateResMap = new HashMap<>();
		test.log(LogStatus.INFO, "Check the created site");
		sitecreateResMap = spogServer.checkCreateSite(response, SpogConstants.SUCCESS_POST, siteName, sitetype,
				sub_org_Id, direct_user, "", test);
		String registration_basecode = sitecreateResMap.get("registration_basecode");
		String site_id = sitecreateResMap.get("site_id");
		test.log(LogStatus.INFO, "The site id is " + site_id);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_SITE, sub_org_Id, direct_user,
				site_id, response, organizationEmail, "user", siteName, "site");
		User_Events_Suborg.add(user_events_code);
		// User_Events_Site.add(user_events_code);
		// User_Events_Sites_Org.add(user_events_code);

		try {

			String decoded = URLDecoder.decode(registration_basecode.trim(), "UTF-8");
			Base64Coder base64 = new Base64Coder();
			String decrypted = base64.decode(decoded);
			String[] parts = decrypted.split("\\n", -2);
			site_registration_key = parts[1];
			// test.log(LogStatus.INFO, "Decoded base64code is : "+parts[1]);

		} catch (UnsupportedEncodingException e) {
			test.log(LogStatus.FAIL, "The value of the error Message :" + e.getMessage());
		}
		test.log(LogStatus.INFO, "After decoding the site_registration_key is: " + site_registration_key);
		String gateway_id = UUID.randomUUID().toString();
		test.log(LogStatus.INFO, "Randomly generated gateway_id is: " + gateway_id);
		gateway_hostname = RandomStringUtils.randomAlphanumeric(4) + "_" + gateway_hostname;
		test.log(LogStatus.INFO, "Randomly generated gateway_hostname is: " + gateway_hostname);
		boolean is_registered = true;

		test.log(LogStatus.INFO, "Registering the gateway to site");
		response = gatewayServer.RegisterSite(site_registration_key, gateway_id, gateway_hostname, site_version,
				site_id, test);
		String site_secret = gatewayServer.checkRegisterSite(response, ServerResponseCode.Succes_Login, site_id,
				siteName, sitetype, sub_org_Id, direct_user, is_registered, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		test.log(LogStatus.INFO, "The secret key is :" + site_secret);

		test.log(LogStatus.INFO, "Login to Site");
		response = gatewayServer.LoginSite(site_id, site_secret, gateway_id, test);
		test.log(LogStatus.INFO, "validate the login site response ");
		gatewayServer.checksiteLogin(response, ServerResponseCode.Succes_Login, "", test);
		site_token = gatewayServer.getJWTToken();
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.LOGIN_SITE, sub_org_Id, site_id, site_id,
				response, siteName, "site", siteName, "site");
		User_Events_Suborg.add(user_events_code);

		// create source
		test.log(LogStatus.INFO, "Adding sources of type " + SourceType.machine + " to " + organizationType + " org");
		String source_name = "test";
		response = spogServer.createSource(source_name, SourceType.machine, SourceProduct.cloud_direct, sub_org_Id,
				site_id, ProtectionStatus.protect, ConnectionStatus.online, "windows", "SQLSERVER", test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		source_id = response.then().extract().path("data.source_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_SOURCE, sub_org_Id, direct_user,
				source_id, response, userEmail, "user", source_name, "source");
		User_Events_Suborg.add(user_events_code);
		User_Events_Sources.add(user_events_code);

		/*
		 * test.log(LogStatus.INFO, "Adding sources of type "
		 * +SourceType.instant_vm +" to "+organizationType+" org"); String
		 * source_name = spogServer.ReturnRandom("machinek")+"_site"; response =
		 * spogServer.createSource(source_name, SourceType.machine,
		 * SourceProduct.cloud_direct, sub_org_Id,
		 * site_id,ProtectionStatus.protect, ConnectionStatus.online, "windows",
		 * "SQLSERVER", test); spogServer.checkResponseStatus(response,
		 * SpogConstants.SUCCESS_POST, test); source_id =
		 * response.then().extract().path("data.source_id");
		 * user_events_code=spogServer.storetheuserevents(AuditCodeConstants.
		 * CREATE_SOURCE, sub_org_Id, direct_user, source_id,
		 * response,siteName,"site",source_name,"source");
		 * User_Events_Suborg.add(user_events_code);
		 * User_Events_Sources.add(user_events_code);
		 */

		// Add a destination of type cloud direct
		spogDestinationServer.setToken(validToken);
		String[] datacenters = spogDestinationServer.getDestionationDatacenterID();

		// get Cloud Account
		test.log(LogStatus.INFO, "get Cloud Account for organization");
		String cloud_account_id = null;
		spogServer.setToken(validToken);
		response = spogServer.getCloudAccounts(validToken, "", test);
		cloud_account_id = response.then().extract().path("data[0].cloud_account_id");

		/*
		 * test.log(LogStatus.INFO,"creating a cloud_Account");
		 * prefix=spogServer.ReturnRandom("ramya"); //create an Organization
		 * cloud_account_id String
		 * cloud_account_id=createCloudAccount(UUID.randomUUID().toString(),
		 * "cloudAccountSecret","CloudAccountData",
		 * "cloud_direct",organization_id , prefix+"SKUTESTDATA_1_0_0_0_",
		 * prefix+"SKUTESTDATA_1_0_0_0_","91a9b48e-6ac6-4c47-8202-614b5cdcfe0c",
		 * spogServer,test); cloud_account_id =
		 * response.then().extract().path("data.cloud_account_id");
		 * test.log(LogStatus.INFO,"The value of the cloud_Account_id:"
		 * +cloud_account_id);
		 * user_events_code=spogServer.storetheuserevents(AuditCodeConstants.
		 * create_cloud_account, organization_id, user_id, cloud_account_id,
		 * response,userEmail, "user", "CloudAccountData", "cloud_account");
		 * User_Events.add(user_events_code);
		 */

		// create destination cloud_direct
		spogDestinationServer.setToken(msp_token);
		destination_name = spogServer.ReturnRandom("Ramya-test1") + "_site";
		test.log(LogStatus.INFO, "Create a destination of type cloud_direct_volume in org " + organizationType);
		response = spogDestinationServer.createDestination(UUID.randomUUID().toString(), sub_org_Id, cloud_account_id,
				datacenters[0], DestinationType.cloud_direct_volume.toString(), DestinationStatus.running.toString(),
				"20", cloud_account_id, "normal", RandomStringUtils.randomAlphanumeric(4) + "host-t", "7D", "7 Days",
				"0", "0", "0", "0", "0", "0", "0", "0", "0", "0", destination_name, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		destination_id = response.then().extract().path("data.destination_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.create_destination, sub_org_Id, user_id,
				destination_id, response, organizationEmail, "user", destination_name, "destination");
		User_Events.add(user_events_code);
		User_Events_Destinations.add(user_events_code);

		test.log(LogStatus.INFO, "Create a policy of task_type as ");
		/*
		 * test.log(LogStatus.INFO, "Create cloud direct schedule"); task_type =
		 * "cloud_direct_file_folder_backup"; HashMap<String, Object>
		 * cloudDirectScheduleDTO=policy4SpogServer.
		 * createCloudDirectScheduleDTO("0 6 * * *", test);
		 * 
		 * test.log(LogStatus.INFO, "Create schedule settings");
		 * scheduleSettingDTO=policy4SpogServer.createScheduleSettingDTO(
		 * cloudDirectScheduleDTO, null,test); schedules
		 * =policy4SpogServer.createPolicyScheduleDTO(null,schedule_id, "1d",
		 * task_id, destination_id, scheduleSettingDTO, "06:00", "18:00",
		 * task_type ,destination_name,test);
		 * 
		 * test.log(LogStatus.INFO,
		 * "Exclude the folders from cloud direct file backup");
		 * 
		 * ArrayList<HashMap<String,Object>>
		 * excludes=policy4SpogServer.createExcludeInfoDTO(null, "path",
		 * "c:\\tmp", test); HashMap<String, Object>
		 * cloudDirectLocalBackupDTO=policy4SpogServer.
		 * createCloudDirectLocalBackupDTO("c:\\tmp", "true", test);
		 * HashMap<String, Object>
		 * cloudDirectFileBackupTaskInfoDTO=policy4SpogServer.
		 * createCloudDirectFileBackupTaskInfoDTO("c:\\tmp",
		 * cloudDirectLocalBackupDTO, excludes,test);
		 * 
		 * ArrayList<String> drivers = new ArrayList<>(); drivers.add("C");
		 * 
		 * HashMap<String, Object> cloudDirectimageBackupTaskInfoDTO =
		 * policy4SpogServer.createCloudDirectImageBackupTaskInfoDTO(drivers,
		 * cloudDirectLocalBackupDTO, test);
		 * 
		 * test.log(LogStatus.INFO,
		 * "Create task type and link it to the destination ");
		 * ArrayList<HashMap<String,Object>> destinations =
		 * policy4SpogServer.createPolicyTaskDTO(null, task_id, task_type,
		 * destination_id, "none", null, cloudDirectFileBackupTaskInfoDTO, null,
		 * test);
		 * 
		 * test.log(LogStatus.INFO, "Create network throttle ");
		 * 
		 * ArrayList<HashMap<String,Object>> throttles
		 * =policy4SpogServer.createPolicyThrottleDTO(null, throttle_id,
		 * task_id, throttle_type, "1200", "1", "06:00", "18:00",
		 * task_type,destination_id,destination_name,test);
		 * 
		 * test.log(LogStatus.INFO,
		 * "Create a policy of type backup_recovery and task of type "
		 * +task_type);
		 * 
		 * 
		 * spogDestinationServer.recycleCloudVolumesAndDelOrg(
		 * "f1168979-04b8-4474-aff1-95fc8ad5b2f6", test);
		 * policy4SpogServer.setToken(msp_token);
		 * response=policy4SpogServer.createPolicy(policy_name,
		 * policy_description, "cloud_direct_baas", null, "true", source_id,
		 * destinations, schedules, throttles, "", sub_org_Id, test);
		 * spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST,
		 * test); String policy_id =
		 * response.then().extract().path("data.policy_id");
		 */
		test.log(LogStatus.INFO, "create policy for sub organization");
		task_id = UUID.randomUUID().toString();

		throttle_id = UUID.randomUUID().toString();
		schedule_id = UUID.randomUUID().toString();
		throttle_type = "network";
		String taskType = "cloud_direct_file_folder_backup";
		ArrayList<String> drivers = new ArrayList<>();

		drivers.add("C");

		policy4SpogServer.setToken(validToken);
		test.log(LogStatus.INFO, "Create cloud direct schedule");

		HashMap<String, Object> cloudDirectScheduleDTO = policy4SpogServer.createCloudDirectScheduleDTO("0 6 * * *",
				test);

		test.log(LogStatus.INFO, "Create custom settings");
		HashMap<String, Object> customScheduleSettingDTO = policy4SpogServer.createCustomScheduleDTO("1522068700422",
				"custom", "1", "true", "10", "minutes", test);

		scheduleSettingDTO = policy4SpogServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null, null, null, test);

		schedules = policy4SpogServer.createPolicyScheduleDTO(null, schedule_id, "1d", task_id, destination_id,
				scheduleSettingDTO, "06:00", "18:00", taskType, destination_name, test);
		ArrayList<HashMap<String, Object>> excludes = policy4SpogServer.createExcludeInfoDTO(null, "path", "c:\\tmp",
				test);

		HashMap<String, Object> perform_ar_test = policy4SpogServer.createPerformARTestOption("true", "true", "true",
				"true", test);

		HashMap<String, Object> retention_policy = policy4SpogServer.createRetentionPolicyOption("2", "2", "2", "2",
				test);

		HashMap<String, Object> udp_replication_from_remote_DTO = policy4SpogServer
				.createUdpReplicationFromRemoteInfoDTO(perform_ar_test, retention_policy, test);

		HashMap<String, Object> cloudDirectLocalBackupDTO = policy4SpogServer.createCloudDirectLocalBackupDTO("c:\\tmp",
				"true", test);

		HashMap<String, Object> cloudDirectFileBackupTaskInfoDTO = policy4SpogServer
				.createCloudDirectFileBackupTaskInfoDTO("c:\\tmp", cloudDirectLocalBackupDTO, excludes, test);
		HashMap<String, Object> cloudDirectimageBackupTaskInfoDTO = policy4SpogServer
				.createCloudDirectImageBackupTaskInfoDTO(drivers, cloudDirectLocalBackupDTO, test);
		ArrayList<HashMap<String, Object>> destinations = policy4SpogServer.createPolicyTaskDTO(null, task_id, taskType,
				destination_id, "none", null, cloudDirectFileBackupTaskInfoDTO, null, test);
		ArrayList<HashMap<String, Object>> throttles = policy4SpogServer.createPolicyThrottleDTO(null, throttle_id,
				task_id, throttle_type, "1200", "1", "06:00", "18:00", taskType, destination_id, destination_name,
				test);

		policy4SpogServer.setToken(msp_token);
		// create cloud direct policy
		policy_name = spogServer.ReturnRandom("ramya");
		response = policy4SpogServer.createPolicy(policy_name, policy_name, "cloud_direct_baas", null, "true",
				source_id, destinations, schedules, throttles, UUID.randomUUID().toString(), sub_org_Id, test);
		String policy_id = response.then().extract().path("data.policy_id").toString();
		// policy4SpogServer.checkPolicyDestinations(response,
		// SpogConstants.SUCCESS_POST, destinations, test);

		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_POLICY, sub_org_Id, user_id,
				policy_id, response, organizationEmail, "user", policy_name, "policy");
		User_Events.add(user_events_code);
		User_Events_policies.add(user_events_code);

		// spogDestinationServer.create
		/*
		 * response = spogDestinationServer.createDestination(sub_org_Id,
		 * site_id, datacenters[1],
		 * DestinationType.cloud_dedupe_store.toString(),
		 * DestinationStatus.creating.toString(), "0", "0", "0", "0", "0", "0",
		 * "0", "0", "0", "0", "0", "0", "F:\\abc", "F:\\abc\\data",
		 * "F:\\abc\\index", "F:\\abc\\hash",
		 * "3","true","64","1024","true","Mclaren@2010","0","0","0.5","0.5",
		 * spogServer.ReturnRandom("Ramya-test1")+"_site", test);
		 * spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST,
		 * test); destination_id =
		 * response.then().extract().path("data.destination_id");
		 * user_events_code=spogServer.storetheuserevents(AuditCodeConstants.
		 * create_destination, sub_org_Id, direct_user, destination_id,
		 * response,userEmail, "user", destination_name, "destination");
		 * User_Events_Suborg.add(user_events_code);
		 * User_Events_Destinations.add(user_events_code);
		 */

		// update destination
		/*
		 * test.log(LogStatus.INFO, "Compose the retention values");
		 * HashMap<String,String> retentionInfo =
		 * spogDestinationServer.composeRetention("0", "0", "0", "0", "0", "0");
		 * 
		 * test.log(LogStatus.INFO, "Compose the cloud volume with dummy values"
		 * ); HashMap<String,Object> cloudvolInfo =
		 * spogDestinationServer.composeCloudDirectInfo("0","0", "0", "0", "0",
		 * "0", retentionInfo);
		 * 
		 * 
		 * test.log(LogStatus.INFO, "Compose the cloud dedupe volume ");
		 * HashMap<String,Object> clouddedupevolInfo =
		 * spogDestinationServer.composeCloudDedupeInfo("F:\\abc",
		 * "F:\\abc\\data", "F:\\abc\\index", "F:\\abc\\hash", "4", "true",
		 * "64", "1024", "true", "Mclaren@2010", "2", "2", "0.8","0.3");
		 * 
		 * response =
		 * spogDestinationServer.updatedestinationinfobydestinationId(
		 * destination_id, direct_token, sub_org_Id, site_id, datacenters[1],
		 * DestinationType.cloud_dedupe_store.toString(),
		 * DestinationStatus.running.toString(),spogServer.ReturnRandom(
		 * "Ramya-test1")+"_site",cloudvolInfo, clouddedupevolInfo);
		 * spogServer.checkResponseStatus(response,
		 * SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		 * user_events_code=spogServer.storetheuserevents(AuditCodeConstants.
		 * update_destination, sub_org_Id, direct_user, destination_id,
		 * response); User_Events_Suborg.add(user_events_code);
		 * User_Events_Destinations.add(user_events_code);
		 */

		test.log(LogStatus.INFO, "Create a source group");
		String groupName = "audit-source-group";
		response = spogServer.createGroup(sub_org_Id, groupName, "audit purpose");
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		sourcegroup_id = response.then().extract().path("data.group_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.create_source_group, sub_org_Id, user_id,
				sourcegroup_id, response, organizationEmail, "user", groupName, "source_group");
		// User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_Group.add(user_events_code);

		test.log(LogStatus.INFO, "Modify source group");
		groupName = "audit-mod-sgrp";
		response = spogServer.updateSourceGroup(sourcegroup_id, groupName, "modify source group");
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.modify_source_group, sub_org_Id, user_id,
				sourcegroup_id, response, organizationEmail, "user", groupName, "source_group");
		// User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_Group.add(user_events_code);

		test.log(LogStatus.INFO, "Add source to source group");
		response = spogServer.addSourcetoSourceGroupwithCheck(sourcegroup_id, new String[] { source_id }, validToken,
				SpogConstants.SUCCESS_POST, null, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.add_source_to_group, sub_org_Id, user_id,
				sourcegroup_id, response, organizationEmail, "user", groupName, "source_group");
		// User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_AddsourcetoGroup.add(user_events_code);

		test.log(LogStatus.INFO, "Delete source from source group");
		spogServer.deleteSourcefromSourceGroupwithCheck(sourcegroup_id, source_id, validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.delete_source_from_group, sub_org_Id,
				user_id, sourcegroup_id, response1, organizationEmail, "user", groupName, "source_group");
		// User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_AddsourcetoGroup.add(user_events_code);

		test.log(LogStatus.INFO, "Delete source group");
		spogServer.deleteGroup(sourcegroup_id, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.delete_source_group, sub_org_Id, user_id,
				sourcegroup_id, response1, organizationEmail, "user", groupName, "source_group");
		// User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_Group.add(user_events_code);

		test.log(LogStatus.INFO, "Create source filter for user");
		// spogServer.createFilterwithCheck(user_id, "test", "emptyarray",
		// "online", UUID.randomUUID().toString(), "finished", "emptyarray",
		// "windows", "emptyarray", SourceType.machine.name(), "true",test);
		String filter_name = "filter_s";
		response = spogServer.createFilter(user_id, filter_name, "emptyarray", "online", UUID.randomUUID().toString(),
				"finished", "none", "windows", "emptyarray", site_id, source_name, SourceType.machine.name(), "true",
				test);
		String filter_id = response.then().extract().path("data.filter_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.create_source_filter, sub_org_Id, user_id,
				filter_id, response, organizationEmail, "user", filter_name, "source_filter");
		// User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_SourceFilter.add(user_events_code);

		test.log(LogStatus.INFO, "update source filter for user");
		filter_name = "filter_s";
		response = spogServer.updateFilter(user_id, filter_id, filter_name, "emptyarray", "online",
				UUID.randomUUID().toString(), "finished", "none", "windows", "emptyarray", site_id, source_name,
				SourceType.machine.name(), SourceType.machine.name(), "true", test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.modify_source_filter, sub_org_Id, user_id,
				filter_id, response, organizationEmail, "user", filter_name, "source_filter");
		// User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_SourceFilter.add(user_events_code);

		test.log(LogStatus.INFO, "delete source filter for user");
		spogServer.deletefilterspecifiedbyUserIdwithCheck(user_id, filter_id, validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.delete_source_filter, sub_org_Id, user_id,
				filter_id, response1, organizationEmail, "user", filter_name, "source_filter");
		// User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_SourceFilter.add(user_events_code);

		test.log(LogStatus.INFO, "Create destination filter for user");
		filter_name = "filter_d";
		response = spogDestinationServer.createDestinationFilterForLoggedinUser(filter_name, destination_name, "none",
				DestinationType.cloud_direct_volume.toString(), "true", test);
		filter_id = response.then().extract().path("data.filter_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.create_destination_filter, sub_org_Id,
				user_id, filter_id, response, organizationEmail, "user", filter_name, "destination_filter");
		// User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_DestinationFilter.add(user_events_code);

		test.log(LogStatus.INFO, "update destination filter for user");
		filter_name = "filter_d";
		response = spogDestinationServer.updateDestinationFilterForLoggedInUserWithCheck(filter_id, filter_name,
				destination_name, "none", DestinationType.cloud_direct_volume.toString(), "true", test, false);
		filter_id = response.then().extract().path("data.filter_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.modify_destination_filter, sub_org_Id,
				user_id, filter_id, response, organizationEmail, "user", filter_name, "destination_filter");
		// User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_DestinationFilter.add(user_events_code);

		/*
		 * test.log(LogStatus.INFO, "delete destination filter for user");
		 * spogDestinationServer.deletedestinationfilterbyfilterId(user_id,
		 * filter_id,msp_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
		 * test);deletedestinationfilterforLoggedInuser( filter_id, validToken,
		 * SpogConstants.SUCCESS_GET_PUT_DELETE, null, test); user_events_code =
		 * spogServer.storetheuserevents(AuditCodeConstants.
		 * delete_destination_filter, sub_org_Id, user_id, filter_id, response1,
		 * organizationEmail, "user", filter_name, "destination_filter");
		 * //User_Events.add(user_events_code);
		 * User_Events_Suborg.add(user_events_code);
		 * User_Events_DestinationFilter.add(user_events_code);
		 * 
		 */ test.log(LogStatus.INFO, "Create the job filter in org " + organizationType);
		spogJobServer.setToken(validToken);
		filter_name = "J-filterName";
		response = spogJobServer.createJobFilterForLoggedinUser("finished", UUID.randomUUID().toString(), source_id,
				"backup_full", null, null, filter_name, "true", test);
		filter_id = response.then().extract().path("data.filter_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_JOB_FILTER, sub_org_Id, user_id,
				filter_id, response, organizationEmail, "user", filter_name, "job_filter");
		// User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_JobFilter.add(user_events_code);

		test.log(LogStatus.INFO, "update the job filter in org " + organizationType);
		filter_name = "J-filterName";
		response = spogJobServer.updateJobFilterForLoggedinUserWithCheck(filter_id, "finished",
				UUID.randomUUID().toString(), source_id, "backup_full", null, null, filter_name, "true", test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_JOB_FILTER, sub_org_Id, user_id,
				filter_id, response, organizationEmail, "user", filter_name, "job_filter");
		// User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_JobFilter.add(user_events_code);

		test.log(LogStatus.INFO, "delete the job filter in org " + organizationType);
		spogJobServer.deleteJobFilterforloggedInUser(filter_id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_JOB_FILTER, sub_org_Id, user_id,
				filter_id, response1, organizationEmail, "user", filter_name, "job_filter");
		// User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_JobFilter.add(user_events_code);

		log4SPOGServer.setToken(validToken);
		test.log(LogStatus.INFO, "Create the log filter in org " + organizationType);
		log4SPOGServer.setToken(validToken);
		filter_name = "filter_l";
		response = log4SPOGServer.createLogFilter(user_id, sub_org_Id, filter_name, "last_24_hours", null, null,
				LogSeverityType.warning.toString(), "false", "none", "none", null, source_name, "none", test);
		filter_id = response.then().extract().path("data.filter_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_LOG_FILTER, sub_org_Id, user_id,
				filter_id, response, organizationEmail, "user", filter_name, "log_filter");
		// User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_LogFilter.add(user_events_code);

		test.log(LogStatus.INFO, "update the log filter in org " + organizationType);
		filter_name = "filter_l";
		response = log4SPOGServer.updateLogFilter(user_id, filter_id, sub_org_Id, filter_name, "last_24_hours", null,
				"backup_full", LogSeverityType.warning.toString(), "false", "none", "none", "none", source_name, "none",
				test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_LOG_FILTER, sub_org_Id, user_id,
				filter_id, response, organizationEmail, "user", filter_name, "log_filter");
		// User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_LogFilter.add(user_events_code);

		test.log(LogStatus.INFO, "delete the log filter in org " + organizationType);
		log4SPOGServer.deleteLogFilterforloggedinUser(filter_id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_LOG_FILTER, sub_org_Id, user_id,
				filter_id, response1, organizationEmail, "user", filter_name, "log_filter");
		// User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_LogFilter.add(user_events_code);

		userSpogServer.setToken(validToken);

		test.log(LogStatus.INFO, "Create user filter in org of type " + organizationType);
		filter_name = "ufilter";
		response = userSpogServer.createUserFilterForLoggedInUser(filter_name, "abcde", null, null,
				SpogConstants.MSP_ADMIN, "true", test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		filter_id = response.then().extract().path("data.filter_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_USER_FILTER, sub_org_Id, user_id,
				filter_id, response, organizationEmail, "user", filter_name, "user_filter");
		// User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_UserFilter.add(user_events_code);

		test.log(LogStatus.INFO, "update user filter in org of type " + organizationType);
		filter_name = "ufilter";
		response = userSpogServer.updateUserFilterForLoggedInUser(filter_id, filter_name, "adadf", null, null,
				SpogConstants.MSP_ADMIN, "true", test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		filter_id = response.then().extract().path("data.filter_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_USER_FILTER, sub_org_Id, user_id,
				filter_id, response, organizationEmail, "user", filter_name, "user_filter");
		// User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_UserFilter.add(user_events_code);

		test.log(LogStatus.INFO, "delete user filter in org of type " + organizationType);
		userSpogServer.deleteUserFilterByUserIdWithCheck(validToken, user_id, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_USER_FILTER, sub_org_Id, user_id,
				filter_id, response1, organizationEmail, "user", filter_name, "user_filter");
		// User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_UserFilter.add(user_events_code);

		test.log(LogStatus.INFO, "Create policy filter in org of type " + organizationType);
		filter_name = "PPfilterName";
		policy_name = "policyhna";
		response = userSpogServer.createPolicyFilterForLoggedinUser(filter_name, policy_name,
				UUID.randomUUID().toString(), "finished", "failure", "true", test);
		filter_id = response.then().extract().path("data.filter_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_POLICY_FILTER, sub_org_Id, user_id,
				filter_id, response, organizationEmail, "user", filter_name, "policy_filter");
		User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_PolicyFilters.add(user_events_code);

		test.log(LogStatus.INFO, "update policy filter in org of type " + organizationType);
		filter_name = "PPfilterName";
		response = userSpogServer.updatePolicyFilterForLoggedinUserWithCheck(filter_id, filter_name, policy_name,
				UUID.randomUUID().toString(), "finished", "failure", "true", test);
		filter_id = response.then().extract().path("data.filter_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_POLICY_FILTER, sub_org_Id, user_id,
				filter_id, response, organizationEmail, "user", filter_name, "policy_filter");
		User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_PolicyFilters.add(user_events_code);

		test.log(LogStatus.INFO, "delete policy filter in org of type " + organizationType);
		userSpogServer.deleteSpecificPolicyFilterForLoggedinUser(filter_id, test);
		filter_id = response.then().extract().path("data.filter_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_POLICY_FILTER, sub_org_Id, user_id,
				filter_id, response1, organizationEmail, "user", filter_name, "policy_filter");
		User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_PolicyFilters.add(user_events_code);

		test.log(LogStatus.INFO, "Create recoveredresource filter in org of type " + organizationType);
		// String policy_id = UUID.randomUUID().toString();
		filter_name = "recfailure";
		response = spogRecoveredResourceServer.createLoggedInUserRecoveredResourcesFilters(validToken, policy_id, "",
				OSMajor.windows.name(), "", filter_name, "true", "", test);
		filter_id = response.then().extract().path("data.filter_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_RECOVEREDRESOURCE_FILTER, sub_org_Id,
				user_id, filter_id, response, organizationEmail, "user", filter_name, "recovered_resource_filter");
		User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_RecoveryResourceFilters.add(user_events_code);

		test.log(LogStatus.INFO, "Update recoveredresource filter in org of type " + organizationType);
		filter_name = "recfailure";
		response = spogRecoveredResourceServer.updateLoggedInUserRecoveredResourcesFilters(validToken, filter_id,
				policy_id, "starting", "", "", filter_name, "true", SpogConstants.SUCCESS_GET_PUT_DELETE, "", test);
		filter_id = response.then().extract().path("data.filter_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_RECOVEREDRESOURCE_FILTER, sub_org_Id,
				user_id, filter_id, response, organizationEmail, "user", filter_name, "recovered_resource_filter");
		User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_RecoveryResourceFilters.add(user_events_code);

		test.log(LogStatus.INFO, "Delete recoveredresource filter in org of type " + organizationType);
		spogRecoveredResourceServer.deleteLoggedInUserRecoveredResourcesFilters(validToken, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		filter_id = response.then().extract().path("data.filter_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_RECOVEREDRESOURCE_FILTER, sub_org_Id,
				user_id, filter_id, response1, organizationEmail, "user", filter_name, "recovered_resource_filter");
		User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_RecoveryResourceFilters.add(user_events_code);

		// create report list filters for specified user
		test.log(LogStatus.INFO, "create report list filters for specified user");
		spogServer.setToken(validToken);
		spogReportServer.setToken(validToken);
		filter_name = spogServer.ReturnRandom("report");
		HashMap<String, Object> expectedfilter = spogReportServer.composeReportListFilterInfo(filter_name,
				"last_7_days", "all_sources", "", organization_id, "weekly", System.currentTimeMillis(),
				System.currentTimeMillis(), filter_name, true);
		response = spogReportServer.createReportListFiltersForLoggedInUserWithCheck(validToken, expectedfilter,
				SpogConstants.SUCCESS_POST, SpogMessageCode.SUCCESS_POST, test);
		filter_id = response.then().extract().path("data.filter_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_REPORT_LIST_FILTER, sub_org_Id,
				user_id, filter_id, response, organizationEmail, "user", filter_name, "report_list_filter");
		User_Events.add(user_events_code);

		// update report list filters for specified user
		test.log(LogStatus.INFO, "update report list filters for specified user");
		response = spogReportServer.updateReportListFiltersForLoggedInUserByFilterIdWithCheck(validToken, filter_id,
				expectedfilter, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		/*
		 * response = spogReportServer.
		 * updateReportListFiltersForLoggedInUserByFilterIdWithCheck(validToken,
		 * user_id, filter_id, expectedfilter, expectedfilter,
		 * SpogConstants.SUCCESS_GET_PUT_DELETE,
		 * SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		 */
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_REPORT_LIST_FILTER, sub_org_Id,
				user_id, filter_id, response, organizationEmail, "user", filter_name, "report_list_filter");
		User_Events.add(user_events_code);

		// delete report list filters for specified user
		test.log(LogStatus.INFO, "delete report list filters for specified user");
		spogReportServer.deleteReportListFiltersForLoggedInUserByFilterIdWithCheck(validToken, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_REPORT_LIST_FILTER, sub_org_Id,
				user_id, filter_id, response1, organizationEmail, "user", filter_name, "report_list_filter");
		User_Events.add(user_events_code);

		test.log(LogStatus.INFO, "Create user columns for logged in user in organization : " + organizationType);
		temp = userSpogServer.composeUser_Column("50169479-cf16-412a-9a6d-3bf19a111a0b", "true", "true", "true", "1");
		expectedColumns.add(temp);
		response = userSpogServer.createUserColumnsForLoggedInUser(validToken, expectedColumns, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_USER_COLUMN, sub_org_Id, user_id,
				user_id, response, userEmail, "user", userEmail, "user");
		// User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_UserColumns.add(user_events_code);

		test.log(LogStatus.INFO, "update user columns for logged in user in organization : " + organizationType);
		response = userSpogServer.updateUserColumnsForLoggedInUser(validToken, expectedColumns, expectedColumns,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_USER_COLUMN, sub_org_Id, user_id,
				user_id, response, userEmail, "user", userEmail, "user");
		// User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_UserColumns.add(user_events_code);

		test.log(LogStatus.INFO, "delete user columns for logged in user in organization : " + organizationType);
		userSpogServer.deleteUserColumnsforSpecifiedUserwithCheck(user_id, validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_USER_COLUMN, sub_org_Id, user_id,
				user_id, response1, userEmail, "user", userEmail, "user");
		// User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_UserColumns.add(user_events_code);

		test.log(LogStatus.INFO, "Create destination columns for logged in user in organization : " + organizationType);
		temp = userSpogServer.composeUser_Column("a6e15988-2919-4fd2-9b7d-00d2eddffde8", "false", "true", "true", "1");
		expectedColumns = new ArrayList<>();
		expectedColumns.add(temp);
		response = spogDestinationServer.createDestinationColumnsForLoggedInUser(validToken, expectedColumns, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_USER_DESTINATION_COLUMN, sub_org_Id,
				user_id, user_id, response, userEmail, "user", userEmail, "user");
		// User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_DestinationColumns.add(user_events_code);

		test.log(LogStatus.INFO, "update destination columns for logged in user in organization : " + organizationType);
		response = spogDestinationServer.updateDestinationColumnsForLoggedinUser(validToken, expectedColumns,
				expectedColumns, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_USER_DESTINATION_COLUMN, sub_org_Id,
				user_id, user_id, response, userEmail, "user", userEmail, "user");
		// User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_DestinationColumns.add(user_events_code);

		test.log(LogStatus.INFO, "delete destination columns for logged in user in organization : " + organizationType);
		spogDestinationServer.deleteDestinatinColumnsforLoggedInUserwithCheck(validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_USER_DESTINATION_COLUMN, sub_org_Id,
				user_id, user_id, response1, userEmail, "user", userEmail, "user");
		// User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_DestinationColumns.add(user_events_code);

		test.log(LogStatus.INFO, "Create job columns for logged in user in organization : " + organizationType);
		temp = spogJobServer.composejob_Column("c6a4be7e-bf1c-4e88-a278-9c34f5f69117", "false", "true", "true", "1");
		expectedColumns = new ArrayList<>();
		expectedColumns.add(temp);
		response = spogJobServer.createJobColumnsForLoggedInUser(validToken, expectedColumns, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_USER_JOB_COLUMN, sub_org_Id, user_id,
				user_id, response, userEmail, "user", userEmail, "user");
		// User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_JobColumns.add(user_events_code);

		/* organizationEmail=userEmail; */

		test.log(LogStatus.INFO, "update job columns for logged in user in organization : " + organizationType);
		response = spogJobServer.updateJobColumnsForLoggedInUser(validToken, expectedColumns, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_USER_JOB_COLUMN, sub_org_Id, user_id,
				user_id, response, userEmail, "user", userEmail, "user");
		// User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_JobColumns.add(user_events_code);

		test.log(LogStatus.INFO, "delete job columns for logged in user in organization : " + organizationType);
		spogJobServer.deleteJobColumnsForLoggedInUser(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_USER_JOB_COLUMN, sub_org_Id, user_id,
				user_id, response1, userEmail, "user", userEmail, "user");
		// User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_JobColumns.add(user_events_code);

		test.log(LogStatus.INFO, "Create log columns for logged in user in organization : " + organizationType);
		temp = log4SPOGServer.composelog_Column("55d3dbb0-83e7-45ae-879a-8f3a522d66c5", "true", "true", "true", "1");
		expectedColumns = new ArrayList<>();
		expectedColumns.add(temp);
		response = log4SPOGServer.createLogColumnsForLoggedInUser(validToken, expectedColumns, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_USER_LOG_COLUMN, sub_org_Id, user_id,
				user_id, response, userEmail, "user", userEmail, "user");
		// User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_LogColumns.add(user_events_code);

		test.log(LogStatus.INFO, "update log columns for logged in user in organization : " + organizationType);
		response = log4SPOGServer.updateLogColumnsForloggedinUser(validToken, expectedColumns, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_USER_LOG_COLUMN, sub_org_Id, user_id,
				user_id, response, userEmail, "user", userEmail, "user");
		// User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_LogColumns.add(user_events_code);

		test.log(LogStatus.INFO, "delete log columns for logged in user in organization : " + organizationType);
		log4SPOGServer.deleteLogColumnsforLoggedInUserwithCheck(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_USER_LOG_COLUMN, sub_org_Id, user_id,
				user_id, response1, userEmail, "user", userEmail, "user");
		// User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_LogColumns.add(user_events_code);

		test.log(LogStatus.INFO, "Create source columns for logged in user in organization : " + organizationType);
		temp = log4SPOGServer.composelog_Column("07871b87-6ec5-44ae-a1bb-17abfb75a9f9", "false", "true", "true", "1");
		expectedColumns = new ArrayList<>();
		expectedColumns.add(temp);
		response = spogSourceServer.createUsersSourcesColumns(user_id, expectedColumns, validToken);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_USER_SOURCE_COLUMN, sub_org_Id,
				user_id, user_id, response, userEmail, "user", userEmail, "user");
		// User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_SourceColumns.add(user_events_code);

		test.log(LogStatus.INFO, "update source columns for logged in user in organization : " + organizationType);
		response = spogSourceServer.updateUsersSourcesColumns(user_id, expectedColumns, validToken);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_USER_SOURCE_COLUMN, sub_org_Id,
				user_id, user_id, response, userEmail, "user", userEmail, "user");
		// User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_SourceColumns.add(user_events_code);

		test.log(LogStatus.INFO, "delete source columns for logged in user in organization : " + organizationType);
		spogSourceServer.deleteUsersSourcesColumns(user_id, validToken);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_USER_SOURCE_COLUMN, sub_org_Id,
				user_id, user_id, response1, userEmail, "user", userEmail, "user");
		// User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_SourceColumns.add(user_events_code);

		test.log(LogStatus.INFO, "Create hypervisor columns for logged in user in organization : " + organizationType);
		temp = log4SPOGServer.composelog_Column("31dfe327-b9fe-432a-a119-24b584a85263", "true", "true", "true", "1");
		expectedColumns = new ArrayList<>();
		expectedColumns.add(temp);
		response = spogSourceServer.createHypervisorColumnsForLoggedInUser(expectedColumns, validToken);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_USER_HYPERVISOR_COLUMN, sub_org_Id,
				user_id, user_id, response, userEmail, "user", userEmail, "user");
		// User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_HypervisorColumns.add(user_events_code);

		test.log(LogStatus.INFO, "update hypervisor columns for logged in user in organization : " + organizationType);
		response = spogSourceServer.updateHypervisorColumnsForLoggedInUser(expectedColumns, validToken);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_USER_HYPERVISOR_COLUMN, sub_org_Id,
				user_id, user_id, response, userEmail, "user", userEmail, "user");
		// User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_HypervisorColumns.add(user_events_code);

		test.log(LogStatus.INFO, "delete hypervisor columns for logged in user in organization : " + organizationType);
		spogSourceServer.deleteHypervisorColumnsForLoggedInUser(validToken);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_USER_HYPERVISOR_COLUMN, sub_org_Id,
				user_id, user_id, response1, userEmail, "user", userEmail, "user");
		// User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_HypervisorColumns.add(user_events_code);

		test.log(LogStatus.INFO,
				"Create recovered resource columns for logged in user in organization: " + organizationType);
		temp = spogRecoveredResourceServer.composeRecoveredResource_Column("84129da7-d794-4cc5-bfe1-17daf6b911bb",
				"true", "false", "true", "1");
		expectedColumns = new ArrayList<>();
		expectedColumns.add(temp);
		response = spogRecoveredResourceServer.createRecoveredResourceColumnsForLoggedInUser(validToken,
				expectedColumns, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_USER_RECOVEREDRESOURCE_COLUMN,
				sub_org_Id, user_id, user_id, response, userEmail, "user", userEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_RecoveryResourceColumns.add(user_events_code);

		test.log(LogStatus.INFO,
				"update recovered resource columns for logged in user in organization: " + organizationType);
		temp = spogRecoveredResourceServer.composeRecoveredResource_Column("84129da7-d794-4cc5-bfe1-17daf6b911bb",
				"true", "false", "true", "1");
		expectedColumns = new ArrayList<>();
		expectedColumns.add(temp);
		response = spogRecoveredResourceServer.updateRecoveredResourceColumnsForLoggedinUser(validToken,
				expectedColumns, expectedColumns, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_USER_RECOVEREDRESOURCE_COLUMN,
				sub_org_Id, user_id, user_id, response, userEmail, "user", userEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_RecoveryResourceColumns.add(user_events_code);

		test.log(LogStatus.INFO,
				"delete recovered resource columns for logged in user in organization: " + organizationType);
		spogRecoveredResourceServer.deleteRecoveredResourceColumnsforLoggedInUserwithCheck(validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_USER_RECOVEREDRESOURCE_COLUMN,
				sub_org_Id, user_id, user_id, response1, userEmail, "user", userEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_RecoveryResourceColumns.add(user_events_code);

		// create user policy columns
		test.log(LogStatus.INFO, "create user policy columns for logged in user in organization: " + organizationType);
		policy4SpogServer.setToken(validToken);
		ArrayList<HashMap<String, Object>> expected_columns_1 = new ArrayList<HashMap<String, Object>>();
		expected_columns_1 = policy4SpogServer.getPolicyColumnArrayList("Protected Sources;false;2", validToken, test);
		response = policy4SpogServer.createLoggedInUserPolicyColumns(expected_columns_1, validToken);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_USER_POLICY_COLUMN, sub_org_Id,
				user_id, user_id, response, userEmail, "user", userEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_PolicyColumns.add(user_events_code);

		test.log(LogStatus.INFO, "update user policy columns for logged in user in organization: " + organizationType);
		policy4SpogServer.setToken(validToken);
		response = policy4SpogServer.updateLoggedInUserPolicyColumns(expected_columns_1, validToken);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_USER_POLICY_COLUMN, sub_org_Id,
				user_id, user_id, response, userEmail, "user", userEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_PolicyColumns.add(user_events_code);

		test.log(LogStatus.INFO, "delete user policy columns for logged in user in organization: " + organizationType);
		policy4SpogServer.setToken(validToken);
		response = policy4SpogServer.deleteLoggedInUserPolicyColumns(validToken);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_USER_POLICY_COLUMN, sub_org_Id,
				user_id, user_id, response1, userEmail, "user", userEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Suborg.add(user_events_code);
		User_Events_PolicyColumns.add(user_events_code);
		/*
		 * long start_time_ts = System.currentTimeMillis(); policy_id =
		 * UUID.randomUUID().toString(); test.log(LogStatus.INFO,
		 * "Create job for logged in user in organization : " +
		 * organizationType); String job_name=""; response =
		 * gatewayServer.postJob(start_time_ts,
		 * System.currentTimeMillis()+10,sub_org_Id,source_id, source_id, null,
		 * null, policy_id,"backup", "full", "active",site_token, test);
		 * spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST,
		 * test); String job_id = response.then().extract().path("data.job_id");
		 * user_events_code =
		 * spogServer.storetheuserevents(AuditCodeConstants.CREATE_JOB,
		 * organization_id, site_id, job_id, response, site_id, "site",
		 * job_name, "job"); //User_Events.add(user_events_code);
		 * User_Events_Suborg.add(user_events_code);
		 * User_Events_jobs.add(user_events_code);
		 * 
		 * test.log(LogStatus.INFO,
		 * "update job  for logged in user in organization : " +
		 * organizationType); response =
		 * gatewayServer.updateJob(job_id,start_time_ts,
		 * System.currentTimeMillis(), sub_org_Id, source_id, source_id, null,
		 * null, policy_id, "backup", "full", "finished",site_token, test);
		 * spogServer.checkResponseStatus(response,
		 * SpogConstants.SUCCESS_GET_PUT_DELETE, test); user_events_code =
		 * spogServer.storetheuserevents(AuditCodeConstants.MODIFY_JOB,
		 * sub_org_Id, site_id, job_id, response);
		 * //User_Events.add(user_events_code);
		 * User_Events_Suborg.add(user_events_code);
		 * User_Events_jobs.add(user_events_code);
		 * 
		 * 
		 * test.log(LogStatus.INFO,
		 * "create job data for logged in user in organization : " +
		 * organizationType); response = gatewayServer.postJobData(job_id, "1",
		 * "success", "100", "0", "0", "0", "0", "0",
		 * "0","0","none","none","none","none","none","none","none","none",
		 * site_token,test); spogServer.checkResponseStatus(response,
		 * SpogConstants.SUCCESS_GET_PUT_DELETE, test); user_events_code =
		 * spogServer.storetheuserevents(AuditCodeConstants.CREATE_JOB_DATA,
		 * sub_org_Id, site_id, job_id, response);
		 * //User_Events.add(user_events_code);
		 * User_Events_Suborg.add(user_events_code);
		 * User_Events_jobs_data.add(user_events_code);
		 * 
		 * test.log(LogStatus.INFO,
		 * "update job data for logged in user in organization : " +
		 * organizationType); response =
		 * gatewayServer.updateJobData(job_id,job_id,"1", "success", "full",
		 * "finished",String.valueOf(start_time_ts) , "0", "0", "0", "0",
		 * "0",site_token, test); spogServer.checkResponseStatus(response,
		 * SpogConstants.SUCCESS_GET_PUT_DELETE, test); user_events_code =
		 * spogServer.storetheuserevents(AuditCodeConstants.MODIFY_JOB_DATA,
		 * sub_org_Id, site_id, job_id, response);
		 * //User_Events.add(user_events_code);
		 * User_Events_Suborg.add(user_events_code);
		 * User_Events_jobs_data.add(user_events_code);
		 * 
		 * 
		 * test.log(LogStatus.INFO, "Create log"); response =
		 * gatewayServer.createLog(System.currentTimeMillis(),job_id,
		 * sub_org_Id, sub_org_Id,source_id,"information", "udp",
		 * "testLogMessage", "SPOQ,QA", "test",site_token, test); String log_id
		 * = response.then().extract().path("data.log_id"); user_events_code =
		 * spogServer.storetheuserevents(AuditCodeConstants.CREATE_LOG,
		 * sub_org_Id, site_id, log_id, response);
		 * //User_Events.add(user_events_code);
		 * User_Events_Suborg.add(user_events_code);
		 * User_Events_logs.add(user_events_code);
		 * 
		 * test.log(LogStatus.INFO, "update log"); response =
		 * log4GatewayServer.updateLog(System.currentTimeMillis(),log_id,
		 * job_id, sub_org_Id, sub_org_Id, source_id,"information", "udp",
		 * "testLogMessage", new String[] {"SPOQ","QA"},
		 * JobType4LatestJob.backup_full.toString(),source_name,source_name,
		 * "test",true, site_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
		 * test); user_events_code =
		 * spogServer.storetheuserevents(AuditCodeConstants.MODIFY_LOG,
		 * sub_org_Id, site_id, log_id, response);
		 * //User_Events.add(user_events_code);
		 * User_Events_Suborg.add(user_events_code);
		 * User_Events_logs.add(user_events_code);
		 * 
		 * test.log(LogStatus.INFO, "Delete the logs by log Id");
		 * spogServer.deletelogbylogId(validToken, log_id,
		 * SpogConstants.SUCCESS_GET_PUT_DELETE,
		 * SpogMessageCode.SUCCESS_GET_PUT_DEL, test); user_events_code =
		 * spogServer.storetheuserevents(AuditCodeConstants.DELETE_LOG,
		 * sub_org_Id, direct_user, log_id, response);
		 * //User_Events.add(user_events_code);
		 * User_Events_Suborg.add(user_events_code);
		 * User_Events_logs.add(user_events_code);
		 * 
		 */

		long start_time_ts = System.currentTimeMillis();
		// policy_id = UUID.randomUUID().toString();
		test.log(LogStatus.INFO, "Create job for logged in user in organization : " + organizationType);
		String job_name = "";
		String rps_id = UUID.randomUUID().toString();
		String job_id = UUID.randomUUID().toString();
		response = gatewayServer.postJob(start_time_ts, System.currentTimeMillis() + 10, sub_org_Id, source_id,
				source_id, rps_id, destination_id, policy_id, "backup", "full", "finished", site_token, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_JOB, sub_org_Id, site_id, job_id,
				response, site_id, "site", job_name, "job");
		User_Events.add(user_events_code);

		User_Events_jobs.add(user_events_code);

		test.log(LogStatus.INFO, "update job  for logged in user in organization : " + organizationType);
		response = gatewayServer.updateJob(job_id, start_time_ts, System.currentTimeMillis(), sub_org_Id, source_id,
				source_id, null, null, policy_id, "backup", "full", "finished", site_token, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_JOB, sub_org_Id, site_id, job_id,
				response, site_id, "site", job_name, "job");
		User_Events.add(user_events_code);

		User_Events_jobs.add(user_events_code);

		test.log(LogStatus.INFO, "create job data for logged in user in organization : " + organizationType);
		response = gatewayServer.postJobData(job_id, "1", "success", "100", "0", "0", "0", "0", "0", "0", "0", "none",
				"none", "none", "none", "none", "none", "none", "none", site_token, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_JOB_DATA, sub_org_Id, site_id,
				site_id, response, site_id, "site", job_name, "job");
		User_Events.add(user_events_code);

		User_Events_jobs_data.add(user_events_code);

		test.log(LogStatus.INFO, "update job data for logged in user in organization : " + organizationType);
		response = gatewayServer.updateJobData(job_id, job_id, "1", "success", "full", "finished",
				String.valueOf(start_time_ts), "0", "0", "0", "0", "0", site_token, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_JOB_DATA, sub_org_Id, site_id,
				job_id, response, site_id, "site", job_name, "job");
		User_Events.add(user_events_code);

		User_Events_jobs_data.add(user_events_code);

		test.log(LogStatus.INFO, "Create log");
		String log_id = UUID.randomUUID().toString();
		response = gatewayServer.createLog(System.currentTimeMillis(), job_id, sub_org_Id, sub_org_Id, source_id,
				"information", "udp", "testLogMessage", "SPOQ,QA", "test", site_token, log_id, test);
		
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_LOG, sub_org_Id, site_id, log_id,
				response, site_id, "site", job_name, "log");
		User_Events.add(user_events_code);

		User_Events_logs.add(user_events_code);

		test.log(LogStatus.INFO, "update log");
		response = log4GatewayServer.updateLog(System.currentTimeMillis(), log_id, job_id, sub_org_Id, sub_org_Id,
				source_id, "information", "udp", "testLogMessage", new String[] { "SPOQ", "QA" },
				JobType4LatestJob.backup_full.toString(), source_name, source_name, "test", true, site_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_LOG, sub_org_Id, site_id, log_id,
				response, site_id, "site", job_name, "log");
		User_Events.add(user_events_code);

		User_Events_logs.add(user_events_code);

		test.log(LogStatus.INFO, "Delete the logs by log Id");
		spogServer.deletelogbylogId(validToken, log_id, SpogConstants.SUCCESS_GET_PUT_DELETE,
				SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_LOG, sub_org_Id, user_id, log_id,
				response, site_id, "site", job_name, "log");
		User_Events.add(user_events_code);

		User_Events_logs.add(user_events_code);
		// create report filter
		test.log(LogStatus.INFO, "create  report filter");
		HashMap<String, Object> reportfilterInfo = null;
		test.log(LogStatus.INFO, "compose report filter");
		filter_name = "filter_name";
		reportfilterInfo = spogReportServer.composeReportFilterInfo(source_name, destination_name, policy_id,
				destination_id, "", "", "today", System.currentTimeMillis(), System.currentTimeMillis(), "backup_jobs",
				"", "backup", "", "", filter_name, true, "backup_jobs");
		response = spogReportServer.createReportFiltersForLoggedInUser_audit(validToken, reportfilterInfo,
				SpogConstants.SUCCESS_POST, null, test);
		filter_id = response.then().extract().path("data.filter_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_REPORT_FILTER, sub_org_Id, user_id,
				filter_id, response, userEmail, "user", filter_name, "report_filter");
		User_Events.add(user_events_code);

		// update report filter
		test.log(LogStatus.INFO, "update  report filter");
		filter_name = "filter_name";
		reportfilterInfo = spogReportServer.composeReportFilterInfo(source_name, destination_name, policy_id,
				destination_id, "", "", "today", System.currentTimeMillis(), System.currentTimeMillis(), "backup_jobs",
				"", "backup", "", "", filter_name, true, "backup_jobs");
		response = spogReportServer.updateReportFiltersForLoggedInUser(validToken, filter_id, reportfilterInfo,
				reportfilterInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_REPORT_FILTER, sub_org_Id, user_id,
				filter_id, response, userEmail, "user", filter_name, "report_filter");
		User_Events.add(user_events_code);

		// delete report filter
		test.log(LogStatus.INFO, "delete report filter");
		response = spogReportServer.deleteReportFiltersForSpecifiedUser(validToken, user_id, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_REPORT_FILTER, sub_org_Id, user_id,
				filter_id, response1, userEmail, "user", filter_name, "report_filter");
		User_Events.add(user_events_code);

		// create backup job report columns
		test.log(LogStatus.INFO, "create backup job report columns");
		ArrayList<HashMap<String, Object>> expected_columns_2 = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> temp2 = new HashMap<>();
		for (int j = 0; j < 1; j++) {
			temp2 = spogReportServer.composeBackUpJobReportsColumns("b9004719-1897-4f09-abd2-7a9fb6edb768", "true",
					"3");

			expected_columns_2.add(temp2);
		}

		response = spogReportServer.createBackUpJobReportsColumnsforLoggedInUser(validToken, expected_columns_2,
				SpogConstants.SUCCESS_POST, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_USER_BACKUPJOBREPORT_COLUMN,
				sub_org_Id, user_id, user_id, response, userEmail, "user", userEmail, "user");
		User_Events.add(user_events_code);

		expected_columns_2.clear();

		// update backup job report columns
		test.log(LogStatus.INFO, "update backup job report columns");
		for (int j = 0; j < 1; j++) {
			temp2 = spogReportServer.composeBackUpJobReportsColumns("b9004719-1897-4f09-abd2-7a9fb6edb768", "true",
					"4");

			expected_columns_2.add(temp2);
		}

		response = spogReportServer.updateBackUpJobReportsColumnsForLoggedInUser(validToken, expected_columns_2,
				SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_USER_BACKUPJOBREPORT_COLUMN,
				sub_org_Id, user_id, user_id, response, userEmail, "user", userEmail, "user");
		User_Events.add(user_events_code);

		// delete BackUpJobReportsColumnsForSpecifiedUser
		test.log(LogStatus.INFO, "delete BackUpJobReportsColumnsForSpecifiedUser ");
		spogReportServer.deleteBackUpJobReportsColumnsForSpecifiedUser(validToken, user_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_USER_BACKUPJOBREPORT_COLUMN,
				sub_org_Id, user_id, user_id, response1, userEmail, "user", userEmail, "user");
		User_Events.add(user_events_code);

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
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_USER_RESTOREJOBREPORT_COLUMN,
				sub_org_Id, user_id, user_id, response, userEmail, "user", userEmail, "user");
		User_Events.add(user_events_code);

		expected_columns_2.clear();

		// update restore job report columns
		test.log(LogStatus.INFO, "update restore job report columns");
		for (int j = 0; j < 1; j++) {
			temp2 = spogReportServer.composeRestoreJobReports_Column("07871b87-6ec5-44ae-a1bb-17abfb75a9f9", "true",
					"false", "true", "4");

			expected_columns_2.add(temp2);
		}

		response = spogReportServer.updateRestoreJobReportsColumnsForLoggedinUser(validToken, expected_columns_2,
				expected_columns_2, SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_USER_RESTOREJOBREPORT_COLUMN,
				sub_org_Id, user_id, user_id, response, userEmail, "user", userEmail, "user");
		User_Events.add(user_events_code);

		// delete RestoreJobReportsColumnsForSpecifiedUser
		test.log(LogStatus.INFO, "delete RestoreJobReportsColumnsForSpecifiedUser ");
		spogReportServer.deleteRestoreJobReportsColumnsforLoggedInUserwithCheck(validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_USER_RESTOREJOBREPORT_COLUMN,
				sub_org_Id, user_id, user_id, response1, userEmail, "user", userEmail, "user");
		User_Events.add(user_events_code);

		test.log(LogStatus.INFO, "update the sitename");
		response = spogServer.updateSiteById(site_id, "test", direct_token);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_SITE, sub_org_Id, direct_user,
				site_id, response, userEmail, "user", "test", "site");
		User_Events_Suborg.add(user_events_code);
		// User_Events_Site.add(user_events_code);
		// User_Events_Sites_Org.add(user_events_code);

		test.log(LogStatus.INFO, "Delete the site " + site_id);
		response = spogServer.deleteSite(site_id, direct_token);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_SITE, sub_org_Id, direct_user,
				site_id, response1, userEmail, "user", "test", "site");
		User_Events_Suborg.add(user_events_code);
		// User_Events_Site.add(user_events_code);
		// User_Events_Sites_Org.add(user_events_code);

		// modify the loggedIn user organization
		spogServer.setToken(validToken);
		test.log(LogStatus.INFO, "Modify the logged in sub org name");
		organizationName = "spog_udp_qa_auto_ramya";
		response = spogServer.UpdateLoggedInOrganization(organizationName, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_LOGIN_USER_ORGANIZATION, sub_org_Id,
				direct_user, sub_org_Id, response, userEmail, "user", organizationName, "organization");
		User_Events_Suborg.add(user_events_code);
		// User_Events_Org.add(user_events_code);
		// User_Events_Sites_Org.add(user_events_code);

		test.log(LogStatus.INFO, "Modify the sub org user name");
		response = spogServer.updateUserById(direct_user, userEmail, "", FirstName + "modi", LastName,
				SpogConstants.DIRECT_ADMIN, sub_org_Id, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_USER, sub_org_Id, direct_user,
				direct_user, response, userEmail, "user", userEmail, "user");
		User_Events_Suborg.add(user_events_code);

		test.log(LogStatus.INFO, "Modify the logged in user password by user id");
		String changepassword = "Mclaren@2016";
		spogServer.setToken(validToken);
		response = spogServer.changePasswordForSpecifiedUser(direct_user, userPassword, changepassword);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_USER_PASSWORD, sub_org_Id,
				direct_user, direct_user, response, userEmail, "user", userEmail, "user");
		User_Events_Suborg.add(user_events_code);

		test.log(LogStatus.INFO, "Modify the login user");
		response = spogServer.UpdateLoggedInUser(userEmail, changepassword, FirstName + "_1", LastName + "_1",
				SpogConstants.DIRECT_ADMIN, sub_org_Id);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_LOGIN_USER, sub_org_Id, direct_user,
				direct_user, response, userEmail, "user", userEmail, "user");
		User_Events_Suborg.add(user_events_code);

		/*
		 * test.log(LogStatus.INFO, "Modify login user password");
		 * spogServer.setToken(validToken); response =
		 * spogServer.changePasswordForLoggedInUser(changepassword,
		 * userPassword); spogServer.checkResponseStatus(response,
		 * SpogConstants.SUCCESS_GET_PUT_DELETE, test); user_events_code =
		 * spogServer.storetheuserevents(AuditCodeConstants.
		 * MODIFY_LOGIN_USER_PASSWORD, sub_org_Id, direct_user, direct_user,
		 * response, userEmail, "user", userEmail, "user");
		 * User_Events_Suborg.add(user_events_code);
		 */

		// update a source
		test.log(LogStatus.INFO, "update the existing source with check");
		response = spogServer.updateSourcebysourceId(source_id, "test", SourceType.machine, SourceProduct.cloud_direct,
				sub_org_Id, site_id, "", ProtectionStatus.unprotect, ConnectionStatus.online, "windows", direct_token,
				test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		source_id = response.then().extract().path("data.source_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_SOURCE, sub_org_Id, direct_user,
				source_id, response, userEmail, "user", source_name, "source");
		User_Events_Suborg.add(user_events_code);
		User_Events_Sources.add(user_events_code);

		// delete policy
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*
		 * // Update the policies test.log(LogStatus.INFO,
		 * "Update the policy by policy name"); policy_name =
		 * spogServer.ReturnRandom("test1"); response =
		 * policy4SpogServer.updatePolicy(policy_name, policy_description,
		 * "cloud_direct_baas", null, "true", source_id, destinations,
		 * schedules, throttles, policy_id, sub_org_Id, validToken, test);
		 * spogServer.checkResponseStatus(response,
		 * SpogConstants.SUCCESS_GET_PUT_DELETE, test); user_events_code =
		 * spogServer.storetheuserevents(AuditCodeConstants.MODIFY_POLICY,
		 * sub_org_Id, user_id, policy_id, response, userEmail, "user",
		 * policy_name, "policy"); User_Events.add(user_events_code);
		 * User_Events_policies.add(user_events_code);
		 */

		// Update the policies
		test.log(LogStatus.INFO, "Update the policy by policy name");
		policy_name = spogServer.ReturnRandom("test1");
		response = policy4SpogServer.updatePolicy(policy_name, policy_description, "cloud_direct_baas", null, "true",
				source_id, destinations, schedules, throttles, policy_id, sub_org_Id, validToken, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_POLICY, sub_org_Id, user_id,
				policy_id, response, organizationEmail, "user", policy_name, "policy");
		User_Events.add(user_events_code);
		User_Events_policies.add(user_events_code);

		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Delete a source
		test.log(LogStatus.INFO, "Delete the source");
		response = spogServer.deleteSourcesById(spogServer.getJWTToken(), source_id, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_SOURCE, sub_org_Id, direct_user,
				source_id, response, userEmail, "user", source_name, "source");
		User_Events_Suborg.add(user_events_code);
		User_Events_Sources.add(user_events_code);

		test.log(LogStatus.INFO, "Delete the policy by policy id");
		policy4SpogServer.deletePolicybyPolicyId(validToken, policy_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_POLICY, sub_org_Id, user_id,
				policy_id, response1, userEmail, "user", policy_name, "policy");
		User_Events.add(user_events_code);
		User_Events_policies.add(user_events_code);

		for (int i = 0; i < 2; i++) {
			if (i == 1) {
				validToken = csr_read_only_validToken;
			}
			ArrayList<HashMap> Expectedresponse = new ArrayList<HashMap>();

			test.log(LogStatus.INFO, "Prepare the URL");
			String filter = "code_id;in;4001|4002";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + sub_org_Id);
			test.log(LogStatus.INFO, "compose audit trails for validation ");
			Expectedresponse = compose_expected_response(filter, User_Events);
			test.log(LogStatus.INFO,
					"Get the audit trail by direct org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, sub_org_Id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, Expectedresponse, 1, 20, "",
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			filter = "code_id;in;1028|1029|1030";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + sub_org_Id);
			test.log(LogStatus.INFO, "compose audit trails for validation ");
			Expectedresponse = compose_expected_response(filter, User_Events);
			test.log(LogStatus.INFO,
					"Get the audit trail by direct org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, sub_org_Id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, Expectedresponse, 1, 20,
			// "",
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			filter = "code_id;in;2025|2026|2027";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + sub_org_Id);
			test.log(LogStatus.INFO, "compose audit trails for validation ");
			Expectedresponse = compose_expected_response(filter, User_Events);
			test.log(LogStatus.INFO,
					"Get the audit trail by direct org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, sub_org_Id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, Expectedresponse, 1, 20,
			// "",
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			filter = "code_id;in;2028|2029|2030";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + sub_org_Id);
			test.log(LogStatus.INFO, "compose audit trails for validation ");
			Expectedresponse = compose_expected_response(filter, User_Events);
			test.log(LogStatus.INFO,
					"Get the audit trail by direct org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, sub_org_Id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, Expectedresponse, 1, 20,
			// "",
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// get Audit trails for list reports
			ArrayList<HashMap> expectedResponse = new ArrayList<HashMap>();
			test.log(LogStatus.INFO, "Prepare the URL");
			filter = "code_id;in;1025|1026|1027";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + sub_org_Id);
			test.log(LogStatus.INFO,
					"Get the audit trail by direct org ID with ascending order and current page is 1 and pagesize 20");
			expectedResponse = compose_expected_response(filter, User_Events);
			response = spogServer.getaudittrailbyorgId(validToken, sub_org_Id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, expectedResponse, 1, 20, "",
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;701|702|703", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + sub_org_Id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, sub_org_Id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_Group, 1, 20,
					"code_id;in;701|702|703", "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;801|802", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + sub_org_Id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, sub_org_Id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_AddsourcetoGroup,
					1, 20, "code_id;in;801|802", "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;1001|1002|1003", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + sub_org_Id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, sub_org_Id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_SourceFilter, 1,
					20, "code_id;in;1001|1002|1003", "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;1004|1005|1006", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + sub_org_Id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, sub_org_Id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE,
					User_Events_DestinationFilter, 1, 20, "code_id;in;1004|1005|1006", "create_ts;asc",
					SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;1007|1008|1009", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + sub_org_Id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, sub_org_Id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_JobFilter, 1, 20,
					"code_id;in;1007", "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;1010|1011|1012", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + sub_org_Id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, sub_org_Id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_LogFilter, 1, 20,
					"code_id;in;1010|1011|1012", "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;1013|1014|1015", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + sub_org_Id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, sub_org_Id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_UserFilter, 1,
					20, "code_id;in;1013|1014|1015", "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;1019|1020|1021", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + sub_org_Id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, sub_org_Id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE,
					User_Events_RecoveryResourceFilters, 1, 20, "code_id;in;1019|1020|1021", "create_ts;asc",
					SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;1022|1023|1024", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + sub_org_Id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, sub_org_Id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_PolicyFilters, 1,
					20, "code_id;in;1022|1023|1024", "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;2001|2002|2003", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + sub_org_Id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, sub_org_Id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_SourceColumns,
			// 1,
			// 20, "code_id;in;2001|2002|2003", "create_ts;asc",
			// SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;2007|2008|2009", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + sub_org_Id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, sub_org_Id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE,
			// User_Events_HypervisorColumns,
			// 1, 20, "code_id;in;2007|2008|2009", "create_ts;asc",
			// SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			// additionalUrl =
			// spogServer.PrepareURL("code_id;in;2010|2011|2012",
			// "create_ts;asc", 1, 20, test);
			additionalUrl = spogServer.PrepareURL("code_id;in;2010|2011|2012", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + sub_org_Id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, sub_org_Id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_UserColumns, 1,
			// 20,
			// "code_id;in;2010|2011|2012", "create_ts;asc",
			// SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;2004|2005|2006", "create_ts;asc", 1, 20, test);
			// additionalUrl = spogServer.PrepareURL("code_id;in;2004",
			// "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + sub_org_Id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, sub_org_Id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE,
			// User_Events_DestinationColumns,
			// 1, 20, "code_id;in;2004|2005|2006", "create_ts;asc",
			// SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;2013|2014|2015", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + sub_org_Id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, sub_org_Id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_JobColumns, 1,
			// 20,
			// "code_id;in;2013|2014|2015", "create_ts;asc",
			// SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;2016|2017|2018", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + sub_org_Id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, sub_org_Id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_LogColumns, 1,
			// 20,
			// "code_id;in;2016|2017|2018", "create_ts;asc",
			// SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;2019|2020|2021", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + sub_org_Id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, sub_org_Id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE,
			// User_Events_RecoveryResourceColumns, 1, 20,
			// "code_id;in;2019|2020|2021", "create_ts;asc",
			// SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;2022|2023|2024", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + sub_org_Id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, sub_org_Id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_PolicyColumns,
			// 1,
			// 20, "code_id;in;2022|2023|2024", "create_ts;asc",
			// SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			ArrayList<HashMap> expected_Response = new ArrayList<HashMap>();
			additionalUrl = spogServer.PrepareURL("code_id;in;4006|4007", "create_ts;asc", 1, 20, test);
			// additionalUrl = spogServer.PrepareURL("code_id;in;4007",
			// "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + sub_org_Id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			expected_Response = compose_expected_response("code_id;in;4006|4007", User_Events_jobs);
			response = spogServer.getaudittrailbyorgId(validToken, sub_org_Id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_jobs, 1, 20,
					"code_id;in;4006|4007", "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;4008|4009", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + sub_org_Id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, sub_org_Id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_jobs_data, 1, 20,
					"code_id;in;4008|4009", "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;4001|4002|4003", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + sub_org_Id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, sub_org_Id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_logs, 1, 20,
					"code_id;in;4001|4002|4003", "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;501|502|503", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + sub_org_Id);
			test.log(LogStatus.INFO,
					"Get the audit trail by sub org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, sub_org_Id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_Sources, 1, 20,
			// "code_id;in;501|502|503", "create_ts;asc",
			// SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;601|602|603", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + sub_org_Id);
			test.log(LogStatus.INFO,
					"Get the audit trail by direct org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, sub_org_Id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_Destinations,
			// 1,
			// 20, "code_id;in;601|603", "create_ts;asc",
			// SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;4001|4002|4003", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + sub_org_Id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20 using msp account admin token");
			response = spogServer.getaudittrailbyorgId(msp_account_admin_validToken, sub_org_Id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_logs, 1, 20,
					"code_id;in;4001|4002|4003", "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Login in as msp user to delete the sub org user and org" + organizationEmail);
			spogServer.userLogin(organizationEmail, organizationPwd, test);
			// String token = spogServer.getJWTToken();
			user_events_code = spogServer.storetheuserevents(AuditCodeConstants.LOGIN_USER, organization_id, msp_user,
					msp_user, response1, organizationEmail, "user", organizationEmail, "user");
			User_Events.add(user_events_code);

			test.log(LogStatus.INFO, "Delete the user");
			spogServer.DeleteUserById(direct_user, test);
			user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_USER, organization_id, msp_user,
					direct_user, response1, organizationEmail, "user", organizationEmail, "user");
			User_Events.add(user_events_code);

			test.log(LogStatus.INFO, "Prepare the URL with no filter, no sort and pageno 1 and pagesize 20");
			additionalUrl = spogServer.PrepareURL("", "", 1, 4, test);
			test.log(LogStatus.INFO, "Get the audit trail by sub orgId using the MSP token " + sub_org_Id);
			response = spogServer.getaudittrailbyorgId(msp_token, sub_org_Id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_Suborg, 1, 4,
			// "",
			// "", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;501|502|503", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + sub_org_Id);
			test.log(LogStatus.INFO,
					"Get the audit trail by sub org ID with ascending order and current page is 1 and pagesize 20 using msp token");
			response = spogServer.getaudittrailbyorgId(msp_token, sub_org_Id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_Sources, 1, 20,
					"code_id;in;501|502|503", "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;601|602|603", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + sub_org_Id);
			test.log(LogStatus.INFO,
					"Get the audit trail by direct org ID with ascending order and current page is 1 and pagesize 20 using msp token");
			response = spogServer.getaudittrailbyorgId(msp_token, sub_org_Id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_Destinations, 1,
					20, "code_id;in;601|603", "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// modify the sub organization by Id
			test.log(LogStatus.INFO, "Modify the sub org name by sub org Id");
			response = spogServer.updateAccount(organization_id, sub_org_Id, "spog_udp_qa_auto_ramya");
			// response=spogServer.updateOrganizationInfoByID(sub_org_Id,
			// "spog_udp_qa_auto_ramya",
			// test);
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
			user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_SUB_ORGANIZATION,
					organization_id, msp_user, sub_org_Id, response, organizationEmail, "user", "spog_udp_qa_auto_ramya",
					"organization");
			// User_Events_Suborg.add(user_events_code);
			// User_Events_Org.add(user_events_code);
			// User_Events_Sites_Org.add(user_events_code);
			test.log(LogStatus.INFO, "The expected data is " + User_Events.toString());

			test.log(LogStatus.INFO, "Prepare the URL with no filter, no sort and pageno 0 and pagesize 2");
			additionalUrl = spogServer.PrepareURL("", "", 0, 2, test);
			test.log(LogStatus.INFO, "Get the audit trail by msp orgId using csr token " + organization_id);
			response = spogServer.getaudittrailbyorgId(csr_token, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			/*
			 * spogServer.checkaudittraildata(response,
			 * SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events, 0, 2, "", "",
			 * SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			 */

			test.log(LogStatus.INFO, "Prepare the URL with no filter, no sort and pageno 1 and pagesize 20");
			additionalUrl = spogServer.PrepareURL("", "", 1, 3, test);
			test.log(LogStatus.INFO, "Get the audit trail by msp orgId " + organization_id);
			response = spogServer.getaudittrailbyorgId(msp_token, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events, 1, 3, "", "",
			// SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

		}
		test.log(LogStatus.INFO, "Login in as csr admin to delete the organization");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		csr_token = spogServer.getJWTToken();
		spogDestinationServer.recycleCloudVolumesAndDestroyOrg(csr_token, organization_id);

	}

	@Test(dataProvider = "organizationAndUserInfo")
	public void getaudittraildataofsites_byorgId_MissingToken(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName, String organizationLastName,
			String userEmail, String userPassword, String FirstName, String LastName, String Role_Id) {
		ArrayList<HashMap> User_Events = new ArrayList<HashMap>();
		HashMap<String, Object> user_events_code = new HashMap<String, Object>();
		Response response1 = null;
		test = rep.startTest("getaudittraildataofsites_byorgId_MissingToken");
		test.assignAuthor("Ramya.Nagepalli");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;
		FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + FirstName;
		LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + LastName;
		userEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;

		test.log(LogStatus.INFO, "Login in as CSR Admin user");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		String csr_token = spogServer.getJWTToken();
		String csr_user_id = spogServer.GetLoggedinUser_UserID();
		test.log(LogStatus.INFO, "The csr admin user id is " + csr_user_id);
		test.log(LogStatus.INFO, "Create a organization of type " + organizationType);
		String organization_id = spogServer.CreateOrganizationWithCheck(organizationName + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "The organization id is " + organization_id);
		spogServer.userLogin(organizationEmail, organizationPwd);
		String validToken = spogServer.getJWTToken();
		String user_id = spogServer.GetLoggedinUser_UserID();
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.LOGIN_USER, organization_id, user_id,
				user_id, response1, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);

		test.log(LogStatus.INFO, "Prepare the URL");
		String additionalUrl = spogServer.PrepareURL("", "", 1, 20, test);
		String org_ID = UUID.randomUUID().toString();
		test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
		// additionalUrl="filter=code_id:[401|402|403],code_id:[102|108]&sort=create_ts&page=1&page_size=20"
		test.log(LogStatus.INFO, "Get the audit trail by timestamp less than the current day should result in 404");
		Response response = spogServer.getaudittrailbyorgId("", organization_id, additionalUrl, test);
		test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
		spogServer.checkResponseStatus(response, SpogConstants.NOT_LOGGED_IN);
		spogServer.checkaudittraildata(response, SpogConstants.NOT_LOGGED_IN, User_Events, 1, 20, "", "",
				SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);
		test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
		test.log(LogStatus.INFO,
				"Performing the operations to delete the user and orginzation by logging in as csr admin");
		test.log(LogStatus.INFO, "Login in as csr admin to delete the organization");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		csr_token = spogServer.getJWTToken();
		spogDestinationServer.recycleCloudVolumesAndDestroyOrg(csr_token, organization_id);

	}

	@Test(dataProvider = "organizationAndUserInfo")
	public void getaudittraildataofsites_byorgId_InvalidToken(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName, String organizationLastName,
			String userEmail, String userPassword, String FirstName, String LastName, String Role_Id) {
		ArrayList<HashMap> User_Events = new ArrayList<HashMap>();
		HashMap<String, Object> user_events_code = new HashMap<String, Object>();
		Response response1 = null;
		test = rep.startTest("getaudittraildataofsites_byorgId_InvalidToken");
		test.assignAuthor("Ramya.Nagepalli");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;
		FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + FirstName;
		LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + LastName;
		userEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;

		test.log(LogStatus.INFO, "Login in as CSR Admin user");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		String csr_token = spogServer.getJWTToken();
		String csr_user_id = spogServer.GetLoggedinUser_UserID();
		test.log(LogStatus.INFO, "The csr admin user id is " + csr_user_id);
		test.log(LogStatus.INFO, "Create a organization of type " + organizationType);
		String organization_id = spogServer.CreateOrganizationWithCheck(organizationName + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "The organization id is " + organization_id);
		spogServer.userLogin(organizationEmail, organizationPwd);
		String validToken = spogServer.getJWTToken();
		String user_id = spogServer.GetLoggedinUser_UserID();
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.LOGIN_USER, organization_id, user_id,
				user_id, response1, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);

		test.log(LogStatus.INFO, "Prepare the URL");
		String additionalUrl = spogServer.PrepareURL("", "", 1, 20, test);
		String org_ID = UUID.randomUUID().toString();
		test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
		// additionalUrl="filter=code_id:[401|402|403],code_id:[102|108]&sort=create_ts&page=1&page_size=20"
		test.log(LogStatus.INFO, "Get the audit trail by timestamp less than the current day should result in 404");
		Response response = spogServer.getaudittrailbyorgId(validToken + "J", organization_id, additionalUrl, test);
		test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
		spogServer.checkResponseStatus(response, SpogConstants.NOT_LOGGED_IN);
		spogServer.checkaudittraildata(response, SpogConstants.NOT_LOGGED_IN, User_Events, 1, 20, "", "",
				SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);
		test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

		test.log(LogStatus.INFO, "Login in as csr admin to delete the organization");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		csr_token = spogServer.getJWTToken();
		spogDestinationServer.recycleCloudVolumesAndDestroyOrg(csr_token, organization_id);

	}

	@Test(dataProvider = "organizationAndUserInfo")
	public void getaudittraildataofsites_byInvalidorgId(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName, String organizationLastName,
			String userEmail, String userPassword, String FirstName, String LastName, String Role_Id) {
		ArrayList<HashMap> User_Events = new ArrayList<HashMap>();
		HashMap<String, Object> user_events_code = new HashMap<String, Object>();
		Response response1 = null;
		test = rep.startTest("getaudittraildataofusers_byinvalidorgId");
		test.assignAuthor("Ramya.Nagepalli");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;
		FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + FirstName;
		LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + LastName;
		userEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;

		test.log(LogStatus.INFO, "Login in as CSR Admin user");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		String csr_token = spogServer.getJWTToken();
		String csr_user_id = spogServer.GetLoggedinUser_UserID();
		test.log(LogStatus.INFO, "The csr admin user id is " + csr_user_id);
		test.log(LogStatus.INFO, "Create a organization of type " + organizationType);
		String organization_id = spogServer.CreateOrganizationWithCheck(organizationName + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "The organization id is " + organization_id);
		spogServer.userLogin(organizationEmail, organizationPwd);
		String validToken = spogServer.getJWTToken();
		String user_id = spogServer.GetLoggedinUser_UserID();
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.LOGIN_USER, organization_id, user_id,
				user_id, response1, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);

		test.log(LogStatus.INFO, "Prepare the URL");
		String additionalUrl = spogServer.PrepareURL("", "", 1, 20, test);
		String org_ID = UUID.randomUUID().toString();
		test.log(LogStatus.INFO, "Get the audit trail by Invalid orgId " + org_ID);
		// additionalUrl="filter=code_id:[401|402|403],code_id:[102|108]&sort=create_ts&page=1&page_size=20"
		test.log(LogStatus.INFO, "Get the audit trail by timestamp less than the current day should result in 404");
		Response response = spogServer.getaudittrailbyorgId(validToken, org_ID, additionalUrl, test);
		test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
		spogServer.checkResponseStatus(response, SpogConstants.RESOURCE_NOT_EXIST);
		spogServer.checkaudittraildata(response, SpogConstants.RESOURCE_NOT_EXIST, User_Events, 1, 20, "", "",
				SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED, test);
		test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
		test.log(LogStatus.INFO, "Login in as csr admin to delete the organization");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		csr_token = spogServer.getJWTToken();
		spogDestinationServer.recycleCloudVolumesAndDestroyOrg(csr_token, organization_id);

	}

	@Test(dataProvider = "organizationAndUserInfo")
	public void getaudittraildataof_byotherorgId(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName, String organizationLastName,
			String userEmail, String userPassword, String FirstName, String LastName, String Role_Id) {
		ArrayList<HashMap> User_Events = new ArrayList<HashMap>();
		HashMap<String, Object> user_events_code = new HashMap<String, Object>();
		Response response1 = null;
		test = rep.startTest("getaudittraildataofusers_byotherorgId");
		test.assignAuthor("Ramya.Nagepalli");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;
		FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + FirstName;
		LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + LastName;
		userEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;

		test.log(LogStatus.INFO, "Login in as CSR Admin user");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		String csr_token = spogServer.getJWTToken();
		String csr_user_id = spogServer.GetLoggedinUser_UserID();
		test.log(LogStatus.INFO, "The csr admin user id is " + csr_user_id);
		test.log(LogStatus.INFO, "Create a organization of type " + organizationType);
		String organization_id = spogServer.CreateOrganizationWithCheck(organizationName + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "The organization id is " + organization_id);

		test.log(LogStatus.INFO, "Create a MSP organization");
		String msp_organization_id = spogServer.CreateOrganizationWithCheck("1" + organizationName + org_model_prefix,
				SpogConstants.MSP_ORG, "1" + organizationEmail, organizationPwd, organizationFirstName,
				organizationLastName, test);

		spogServer.userLogin(organizationEmail, organizationPwd);
		String validToken = spogServer.getJWTToken();
		String user_id = spogServer.GetLoggedinUser_UserID();
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.LOGIN_USER, organization_id, user_id,
				user_id, response1, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);

		test.log(LogStatus.INFO, "Prepare the URL");
		String additionalUrl = spogServer.PrepareURL("", "", 1, 20, test);
		// String org_ID=UUID.randomUUID().toString();
		test.log(LogStatus.INFO, "Get the audit trail by other orgId " + msp_organization_id);
		// additionalUrl="filter=code_id:[401|402|403],code_id:[102|108]&sort=create_ts&page=1&page_size=20"
		test.log(LogStatus.INFO, "Get the audit trail by timestamp less than the current day should result in 404");
		Response response = spogServer.getaudittrailbyorgId(validToken, msp_organization_id, additionalUrl, test);
		test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
		spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS);
		spogServer.checkaudittraildata(response, SpogConstants.INSUFFICIENT_PERMISSIONS, User_Events, 1, 20, "", "",
				SpogMessageCode.RESOURCE_PERMISSION_DENY, test);
		test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
		test.log(LogStatus.INFO, "Login in as csr admin to delete the organization");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		csr_token = spogServer.getJWTToken();
		spogDestinationServer.recycleCloudVolumesAndDestroyOrg(csr_token, organization_id);

	}

	@Test(dataProvider = "organizationAndUserInfo")
	public void getaudittraildataof_byCSRadmin(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName, String organizationLastName,
			String userEmail, String userPassword, String FirstName, String LastName, String Role_Id) {
		ArrayList<HashMap> User_Events = new ArrayList<HashMap>();
		HashMap<String, Object> user_events_code = new HashMap<String, Object>();
		Response response1 = null;
		test = rep.startTest("getaudittraildataofusers_byotherorgId");
		test.assignAuthor("Ramya.Nagepalli");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;
		FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + FirstName;
		LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + LastName;
		userEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;

		test.log(LogStatus.INFO, "Login in as CSR Admin user");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		String csr_token = spogServer.getJWTToken();
		String csr_user_id = spogServer.GetLoggedinUser_UserID();
		test.log(LogStatus.INFO, "The csr admin user id is " + csr_user_id);
		test.log(LogStatus.INFO, "Create a organization of type " + organizationType);
		String organization_id = spogServer.CreateOrganizationWithCheck(organizationName + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "The organization id is " + organization_id);

		test.log(LogStatus.INFO, "Create a MSP organization");
		String msp_organization_id = spogServer.CreateOrganizationWithCheck("1" + organizationName + org_model_prefix,
				SpogConstants.MSP_ORG, "1" + organizationEmail, organizationPwd, organizationFirstName,
				organizationLastName, test);

		spogServer.userLogin(organizationEmail, organizationPwd);
		String validToken = spogServer.getJWTToken();
		String user_id = spogServer.GetLoggedinUser_UserID();
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.LOGIN_USER, organization_id, user_id,
				user_id, response1, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);

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

		Response response = spogServer.getLoggedInUser(csr_token, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		csr_user_id = response.then().extract().path("data.user_id");
		String csr_organization_id = response.then().extract().path("data.organization_id");
		String csr_organizationEmail = response.then().extract().path("data.email");

		// create cloud rps with csr token
		spogcloudRPSServer.setToken(csr_token);
		test.log(LogStatus.INFO, " create cloud rps with csr token");
		response = spogcloudRPSServer.createCloudRPS(cloud_rps_name, cloud_rps_port, cloud_rps_protocol,
				cloud_rps_username, cloud_rps_password, organization_id, csr_site_id,
				"91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", csr_token);
		String rps_server_name = response.then().extract().path("data.server_name");
		String rps_server_id = response.then().extract().path("data.server_id");
		user_events_code = spogServer.composeJsonMap(csr_user_id, csr_organization_id,
				AuditCodeConstants.CREATE_RECOVERY_POINT_SERVER, response, rps_server_id, csr_organizationEmail, "user",
				rps_server_name, "recovery_point_server");
		User_Events.add(user_events_code);

		// modify cloud rps
		test.log(LogStatus.INFO, "modify created cloud rps with csr token");
		response = spogcloudRPSServer.updateCloudRPS(rps_server_id, rps_server_name, cloud_rps_port, cloud_rps_protocol,
				cloud_rps_username, cloud_rps_password, csr_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		user_events_code = spogServer.composeJsonMap(csr_user_id, csr_organization_id,
				AuditCodeConstants.MODIFY_RECOVERY_POINT_SERVER, response, rps_server_id, csr_organizationEmail, "user",
				rps_server_name, "recovery_point_server");
		User_Events.add(user_events_code);

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
				datastorepropertiesInfo, csr_token);
		String datastore_id = response.then().extract().path("data.datastore_id");
		user_events_code = spogServer.composeJsonMap(csr_user_id, csr_organization_id,
				AuditCodeConstants.CREATE_DATA_STORE, response, datastore_id, csr_organizationEmail, "user",
				datastore_name, "datastore");
		User_Events.add(user_events_code);

		// modify datastore for cloud rps

		test.log(LogStatus.INFO, " modify datastore for cloud rps");
		response = spogcloudRPSServer.updateCloudRPSDataStore(datastore_name, true, true, true, datastore_id,
				datastorepropertiesInfo, csr_token);
		user_events_code = spogServer.composeJsonMap(csr_user_id, csr_organization_id,
				AuditCodeConstants.MODIFY_DATA_STORE, response, datastore_id, csr_organizationEmail, "user",
				datastore_name, "datastore");
		User_Events.add(user_events_code);

		// STOP datastore
		test.log(LogStatus.INFO, " stop datastore service for cloud rps");
		response = spogcloudRPSServer.postDatastoreStopWithCheck(csr_token, datastore_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		user_events_code = spogServer.composeJsonMap(csr_user_id, csr_organization_id,
				AuditCodeConstants.STOP_DATA_STORE, response1, datastore_id, csr_organizationEmail, "user",
				datastore_name, "datastore");
		User_Events.add(user_events_code);

		// start datastore
		test.log(LogStatus.INFO, " start datastore service for cloud rps");
		response = spogcloudRPSServer.postDatastoreStartWithCheck(csr_token, datastore_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		user_events_code = spogServer.composeJsonMap(csr_user_id, csr_organization_id,
				AuditCodeConstants.START_DATA_STORE, response1, datastore_id, csr_organizationEmail, "user",
				datastore_name, "datastore");
		User_Events.add(user_events_code);

		// delete datastore
		test.log(LogStatus.INFO, " delete datastore for cloud rps");
		spogcloudRPSServer.deleteCloudRPSDataStore(datastore_id, csr_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);
		user_events_code = spogServer.composeJsonMap(csr_user_id, csr_organization_id,
				AuditCodeConstants.DELETE_DATA_STORE, response1, datastore_id, csr_organizationEmail, "user",
				datastore_name, "datastore");
		User_Events.add(user_events_code);

		// delete cloud Rps
		test.log(LogStatus.INFO, " delete cloud rps");

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		spogcloudRPSServer.deleteCloudRPS(rps_server_id, csr_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		user_events_code = spogServer.composeJsonMap(csr_user_id, csr_organization_id,
				AuditCodeConstants.DELETE_RECOVERY_POINT_SERVER, response1, rps_server_id, csr_organizationEmail,
				"user", rps_server_name, "recovery_point_server");
		User_Events.add(user_events_code);

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
		linux4spogServer.setToken(csr_token);
		response = linux4spogServer.createLinuxBackupServer_audit(rps_server_id, linux_rps_name, linux_rps_protocol,
				linux_rps_port, linux_rps_username, linux_rps_password, csr_organization_id, csr_site_id, "normal",
				"91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
		String linux_server_id = response.then().extract().path("data.server_id");
		user_events_code = spogServer.composeJsonMap(csr_user_id, csr_organization_id,
				AuditCodeConstants.CREATE_LINUX_BACKUP_SERVER, response, linux_server_id, csr_organizationEmail, "user",
				linux_rps_name, "linux_backup_server");
		User_Events.add(user_events_code);

		// modify LINUX BACKUP SERVER
		test.log(LogStatus.INFO, "modify LINUX BACKUP SERVER");
		response = linux4spogServer.updateLinuxBackupServer_audit(linux_server_id, linux_server_id, linux_rps_name,
				linux_rps_protocol, linux_rps_port, linux_rps_username, linux_rps_password, csr_organization_id,
				csr_site_id, "normal", "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
		user_events_code = spogServer.composeJsonMap(csr_user_id, csr_organization_id,
				AuditCodeConstants.MODIFY_LINUX_BACKUP_SERVER, response, linux_server_id, csr_organizationEmail, "user",
				linux_rps_name, "linux_backup_server");
		User_Events.add(user_events_code);

		// Delete linux backup server
		test.log(LogStatus.INFO, "Delete LINUX BACKUP SERVER");
		linux4spogServer.deleteLinuxBackupServer(linux_server_id, test);
		user_events_code = spogServer.composeJsonMap(csr_user_id, csr_organization_id,
				AuditCodeConstants.DELETE_LINUX_BACKUP_SERVER, response1, linux_server_id, csr_organizationEmail,
				"user", linux_rps_name, "linux_backup_server");
		User_Events.add(user_events_code);

		// create Organization with CSR token

		String prefix=spogServer.ReturnRandom("auto_ramya");
		spogServer.setToken(csr_token);
		response = spogServer.CreateOrganizationByEnrollWithCheck_audit(prefix+organizationName + org_model_prefix,
				organizationType, prefix+organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		organization_id = response.then().extract().path("data[0].organization.organization_id");
		user_events_code = spogServer.composeJsonMap(csr_user_id, csr_organization_id,
				AuditCodeConstants.CREATE_ORGANIZATION, response, organization_id, "ramya_csr@arcserve.com", "user",
				prefix+organizationName + org_model_prefix, "organization");
		User_Events.add(user_events_code);
		 

		for (int i = 0; i < 2; i++) {
			if (i == 1) {
				csr_token = csr_read_only_validToken;
			}
			test.log(LogStatus.INFO, "providing the valid token of the csr user to get the details");
			String filter = "code_id=5001&server_id=e4ade573-e835-44d6-8032-237900fdc03c&page=1&page_size=1&sort=-create_ts";
			/*
			 * additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1,
			 * 20, test);
			 */
			String filter1 = "code_id;=;5001";
			String additionalUrl = spogServer.PrepareURL(filter1, "create_ts;asc", 1, 20, test);
			ArrayList<HashMap> userresponse = compose_expected_response(filter1, User_Events);
			response = spogServer.getaudittrailbyorgId(csr_token, csr_organization_id, filter, test);
			System.out.println("The value of the response is :" + response.getBody().asString());
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 1, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			test.log(LogStatus.INFO, "providing the valid token of the csr user to get the details");
			filter = "code_id=5002&server_id=e4ade573-e835-44d6-8032-237900fdc03c&page=1&page_size=1&sort=-create_ts";
			/*
			 * additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1,
			 * 20, test);
			 */
			filter1 = "code_id;=;5002";
			additionalUrl = spogServer.PrepareURL(filter1, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter1, User_Events);
			response = spogServer.getaudittrailbyorgId(csr_token, csr_organization_id, filter, test);
			System.out.println("The value of the response is :" + response.getBody().asString());
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 1, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			test.log(LogStatus.INFO, "providing the valid token of the csr user to get the details");
			filter = "code_id=5003&server_id=e4ade573-e835-44d6-8032-237900fdc03c&page=1&page_size=1&sort=-create_ts";
			/*
			 * additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1,
			 * 20, test);
			 */
			filter1 = "code_id;=;5003";
			additionalUrl = spogServer.PrepareURL(filter1, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter1, User_Events);
			response = spogServer.getaudittrailbyorgId(csr_token, csr_organization_id, filter, test);
			System.out.println("The value of the response is :" + response.getBody().asString());
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			/*
			 * spogServer.checkaudittraildata1(response,
			 * SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 1, filter,
			 * "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			 */
			test.log(LogStatus.INFO, "providing the valid token of the csr user to get the details");
			filter = "code_id=7001&datastore_id=" + datastore_id + "&page=1&page_size=1&sort=-create_ts";
			/*
			 * additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1,
			 * 20, test);
			 */
			filter1 = "code_id;=;7001";
			additionalUrl = spogServer.PrepareURL(filter1, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter1, User_Events);
			response = spogServer.getaudittrailbyorgId(csr_token, csr_organization_id, filter, test);
			System.out.println("The value of the response is :" + response.getBody().asString());
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 1, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			test.log(LogStatus.INFO, "providing the valid token of the csr user to get the details");
			filter = "code_id=7002&datastore_id=" + datastore_id + "&page=1&page_size=1&sort=-create_ts";
			/*
			 * additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1,
			 * 20, test);
			 */
			filter1 = "code_id;=;7002";
			additionalUrl = spogServer.PrepareURL(filter1, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter1, User_Events);
			response = spogServer.getaudittrailbyorgId(csr_token, csr_organization_id, filter, test);
			System.out.println("The value of the response is :" + response.getBody().asString());
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 1, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			test.log(LogStatus.INFO, "providing the valid token of the csr user to get the details");
			filter = "code_id=7003&datastore_id=" + datastore_id + "&page=1&page_size=1&sort=-create_ts";
			/*
			 * additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1,
			 * 20, test);
			 */
			filter1 = "code_id;=;7003";
			additionalUrl = spogServer.PrepareURL(filter1, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter1, User_Events);
			response = spogServer.getaudittrailbyorgId(csr_token, csr_organization_id, filter, test);
			System.out.println("The value of the response is :" + response.getBody().asString());
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 1, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			test.log(LogStatus.INFO, "providing the valid token of the csr user to get the details");
			filter = "code_id=7005&datastore_id=" + datastore_id + "&page=1&page_size=1&sort=-create_ts";
			/*
			 * additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1,
			 * 20, test);
			 */
			filter1 = "code_id;=;7005";
			additionalUrl = spogServer.PrepareURL(filter1, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter1, User_Events);
			response = spogServer.getaudittrailbyorgId(csr_token, csr_organization_id, filter, test);
			System.out.println("The value of the response is :" + response.getBody().asString());
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 1, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			test.log(LogStatus.INFO, "providing the valid token of the csr user to get the details");
			filter = "code_id=7006&datastore_id=" + datastore_id + "&page=1&page_size=1&sort=-create_ts";
			/*
			 * additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1,
			 * 20, test);
			 */
			filter1 = "code_id;=;7006";
			additionalUrl = spogServer.PrepareURL(filter1, "create_ts;asc", 1, 20, test);
			userresponse = compose_expected_response(filter1, User_Events);
			response = spogServer.getaudittrailbyorgId(csr_token, csr_organization_id, filter, test);
			System.out.println("The value of the response is :" + response.getBody().asString());
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 1, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			test.log(LogStatus.INFO, "providing the valid token of the csr user to get the details");
			filter = "code_id=6001&datastore_id=" + datastore_id + "&page=1&page_size=1&sort=-create_ts";
			filter1 = "code_id;=;6001";
			userresponse = compose_expected_response(filter1, User_Events);
			response = spogServer.getaudittrailbyorgId(csr_token, csr_organization_id, filter, test);
			System.out.println("The value of the response is :" + response.getBody().asString());
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 1, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			test.log(LogStatus.INFO, "providing the valid token of the csr user to get the details");
			filter = "code_id=6002&datastore_id=" + datastore_id + "&page=1&page_size=1&sort=-create_ts";
			filter1 = "code_id;=;6002";
			additionalUrl = spogServer.PrepareURL(filter1, "create_ts;asc", 1, 1, test);
			userresponse = compose_expected_response(filter1, User_Events);
			response = spogServer.getaudittrailbyorgId(csr_token, csr_organization_id, filter, test);
			System.out.println("The value of the response is :" + response.getBody().asString());
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 1, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			test.log(LogStatus.INFO, "providing the valid token of the csr user to get the details");
			filter = "code_id=6003&datastore_id=" + datastore_id + "&page=1&page_size=1&sort=-create_ts";
			filter1 = "code_id;=;6003";
			userresponse = compose_expected_response(filter1, User_Events);
			response = spogServer.getaudittrailbyorgId(csr_token, csr_organization_id, filter, test);
			System.out.println("The value of the response is :" + response.getBody().asString());
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 1, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("", "", 1, 20, test);
			// String org_ID=UUID.randomUUID().toString();
			test.log(LogStatus.INFO, "Get the audit trail by other orgId " + msp_organization_id);
			// additionalUrl="filter=code_id:[401|402|403],code_id:[102|108]&sort=create_ts&page=1&page_size=20"
			test.log(LogStatus.INFO, "Get the audit trail by timestamp less than the current day should result in 404");
			response = spogServer.getaudittrailbyorgId(validToken, msp_organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS);
			spogServer.checkaudittraildata(response, SpogConstants.INSUFFICIENT_PERMISSIONS, userresponse, 1, 20, "",
					"", SpogMessageCode.RESOURCE_PERMISSION_DENY, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			test.log(LogStatus.INFO,
					"Performing the operations to delete the user and orginzation by logging in as csr admin");
			test.log(LogStatus.INFO, "Login in as csr admin to delete the organization");
			spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);

			test.log(LogStatus.INFO, "providing the valid token of the csr user to get the details");
			filter = "code_id=101&sort=-create_ts&page=1&page_size=1&sort=-create_ts";
			filter1 ="code_id;=;101";

			userresponse = compose_expected_response(filter1, User_Events);
			response = spogServer.getaudittrailbyorgId(csr_token, csr_organization_id, filter, test);
			System.out.println("The value of the response is :" + response.getBody().asString());
			test.log(LogStatus.INFO, "Validating the response for respective audit tracking of a user");
			/*spogServer.checkaudittraildata1(response, SpogConstants.SUCCESS_GET_PUT_DELETE, userresponse, 1, 1, filter,
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);*/
		}


	}

	@Test(dataProvider = "organizationAndUserInfo")
	public void getaudittraildataofusers_sites_orgs_bydirectorgId(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName, String organizationLastName,
			String userEmail, String userPassword, String FirstName, String LastName, String Role_Id) {
		test = rep.startTest("getaudittraildataofusers_sites_orgs_bydirectorgId");
		test.assignAuthor("Ramya.Nagepalli");

		String gateway_hostname = "Ramya";
		String site_version = "1.0.0";
		String source_id = null;
		String destination_id = null;
		String sourcegroup_id = null;
		String site_token = null;
		ArrayList<HashMap> User_Events = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_Site = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_Sources = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_Cloudaccount = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_Destinations = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_source_Destinations = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_Org = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_User = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_byCodeid = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_Sites_Org = new ArrayList<HashMap>();
		ArrayList<ResponseBody> userresponse = new ArrayList<ResponseBody>();
		HashMap<String, Object> user_events_code = new HashMap<String, Object>();
		ArrayList<HashMap> User_Events_Group = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_AddsourcetoGroup = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_SourceFilter = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_DestinationFilter = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_JobFilter = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_LogFilter = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_UserFilter = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_HypervisorFilter = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_RecoveryResourceFilters = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_PolicyFilters = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_UserColumns = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_JobColumns = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_LogColumns = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_SourceColumns = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_DestinationColumns = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_HypervisorColumns = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_RecoveryResourceColumns = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_PolicyColumns = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_policies = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_jobs = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_jobs_data = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_logs = new ArrayList<HashMap>();
		ArrayList<HashMap> User_Events_logs_data = new ArrayList<HashMap>();
		// Related to policies
		HashMap<String, Object> scheduleSettingDTO = new HashMap<>();
		ArrayList<HashMap<String, Object>> schedules = new ArrayList<>();
		String schedule_id = spogServer.returnRandomUUID();
		String task_id = spogServer.returnRandomUUID();
		String throttle_id = spogServer.returnRandomUUID();
		String throttle_type = "network";
		String policy_name = spogServer.ReturnRandom("test");
		String policy_description = spogServer.ReturnRandom("description");
		String task_type = null;
		String destination_name = null;

		Response response1 = null;
		ArrayList<HashMap<String, Object>> expectedColumns = new ArrayList<>();
		HashMap<String, Object> temp = new HashMap<>();

		spogServer.login(csrAdminUserName, csrAdminPassword);
		String csr_Token = spogServer.getJWTToken();

		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;
		FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + FirstName;
		LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + LastName;
		userEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;

		// create Organization Enroll
		test.log(LogStatus.INFO, "Login in as CSR Admin user");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		String csr_token = spogServer.getJWTToken();
		String csr_user_id = spogServer.GetLoggedinUser_UserID();
		String organization_id = spogServer.CreateOrganizationByEnrollWithCheck(organizationName + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);

		test.log(LogStatus.INFO, "The organization id is " + organization_id);

		spogServer.userLogin(organizationEmail, organizationPwd);
		String validToken = spogServer.getJWTToken();
		String user_id = spogServer.GetLoggedinUser_UserID();
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.LOGIN_USER, organization_id, user_id,
				user_id, response1, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_User.add(user_events_code);
		test.log(LogStatus.INFO, "The org userid is " + user_id);
		test.log(LogStatus.INFO, "The token is :" + validToken);
		String site_registration_key = null;
		String siteName = spogServer.getRandomSiteName("TestCreate");
		test.log(LogStatus.INFO, "Generated a Random SiteName " + siteName);
		String sitetype = siteType.gateway.toString();
		test.log(LogStatus.INFO, "The siteType :" + sitetype);
		test.log(LogStatus.INFO, "Creating a site For Logged in user");
		Response response = spogServer.createSite(siteName, sitetype, organization_id, validToken, test);
		Map<String, String> sitecreateResMap = new HashMap<>();
		test.log(LogStatus.INFO, "Check the created site");
		sitecreateResMap = spogServer.checkCreateSite(response, SpogConstants.SUCCESS_POST, siteName, sitetype,
				organization_id, user_id, "", test);
		String registration_basecode = sitecreateResMap.get("registration_basecode");
		String site_id = sitecreateResMap.get("site_id");
		test.log(LogStatus.INFO, "The site id is " + site_id);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_SITE, organization_id, user_id,
				site_id, response, organizationEmail, "user", siteName, "site");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_Sites_Org.add(user_events_code);

		try {

			String decoded = URLDecoder.decode(registration_basecode.trim(), "UTF-8");
			Base64Coder base64 = new Base64Coder();
			String decrypted = base64.decode(decoded);
			String[] parts = decrypted.split("\\n", -2);
			site_registration_key = parts[1];
			// test.log(LogStatus.INFO, "Decoded base64code is : "+parts[1]);

		} catch (UnsupportedEncodingException e) {
			test.log(LogStatus.FAIL, "The value of the error Message :" + e.getMessage());
		}
		test.log(LogStatus.INFO, "After decoding the site_registration_key is: " + site_registration_key);
		String gateway_id = UUID.randomUUID().toString();
		test.log(LogStatus.INFO, "Randomly generated gateway_id is: " + gateway_id);
		gateway_hostname = RandomStringUtils.randomAlphanumeric(4) + "_" + gateway_hostname;
		test.log(LogStatus.INFO, "Randomly generated gateway_hostname is: " + gateway_hostname);
		boolean is_registered = true;
		test.log(LogStatus.INFO, "Registering the gateway to site");
		response = gatewayServer.RegisterSite(site_registration_key, gateway_id, gateway_hostname, site_version,
				site_id, test);
		String site_secret = gatewayServer.checkRegisterSite(response, ServerResponseCode.Succes_Login, site_id,
				siteName, sitetype, organization_id, user_id, is_registered, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		test.log(LogStatus.INFO, "The secret key is :" + site_secret);

		response = gatewayServer.LoginSite(site_id, site_secret, gateway_id, test);
		test.log(LogStatus.INFO, "validate the login site response ");
		gatewayServer.checksiteLogin(response, ServerResponseCode.Succes_Login, "", test);
		site_token = gatewayServer.getJWTToken();
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.LOGIN_SITE, organization_id, site_id,
				site_id, response, siteName, "site", siteName, "site");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_Sites_Org.add(user_events_code);

		test.log(LogStatus.INFO, "update the sitename");
		response = spogServer.updateSiteById(site_id, "test", validToken);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_SITE, organization_id, user_id,
				site_id, response, organizationEmail, "user", "test", "site");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_Sites_Org.add(user_events_code);
		/*
		 * //Add a source test.log(LogStatus.INFO, "Adding sources of type "
		 * +SourceType.machine +" to "+organizationType+" org"); String
		 * source_name=spogServer.ReturnRandom("machinek")+"_site";
		 * spogServer.setToken(validToken); response =
		 * spogServer.createSource(source_name, SourceType.machine,
		 * SourceProduct.cloud_direct, organization_id,
		 * site_id,ProtectionStatus.protect, ConnectionStatus.online, "windows",
		 * "SQLSERVER", test); spogServer.checkResponseStatus(response,
		 * SpogConstants.SUCCESS_POST, test); source_id =
		 * response.then().extract().path("data.source_id");
		 * user_events_code=spogServer.storetheuserevents(AuditCodeConstants.
		 * CREATE_SOURCE, organization_id, user_id, source_id,
		 * response,organizationEmail, "user",source_name,"source");
		 * User_Events.add(user_events_code);
		 * User_Events_Sources.add(user_events_code); //update a source
		 */

		// Add a source
		test.log(LogStatus.INFO, "Adding sources of type " + SourceType.machine + " to " + organizationType + " org");
		String source_name = "test";
		response = spogServer.createSource(source_name, SourceType.machine, SourceProduct.cloud_direct, organization_id,
				site_id, ProtectionStatus.protect, ConnectionStatus.online, "windows", "SQLSERVER", test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		source_id = response.then().extract().path("data.source_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_SOURCE, organization_id, user_id,
				source_id, response, organizationEmail, "user", source_name, "source");
		User_Events.add(user_events_code);
		User_Events_Sources.add(user_events_code);

		// Add a destination of type cloud direct
		spogDestinationServer.setToken(validToken);
		String[] datacenters = spogDestinationServer.getDestionationDatacenterID();

		// get Cloud Account
		test.log(LogStatus.INFO, "get Cloud Account for organization");
		String cloud_account_id = null;
		spogServer.setToken(validToken);
		response = spogServer.getCloudAccounts(validToken, "", test);
		cloud_account_id = response.then().extract().path("data[0].cloud_account_id");

		/*
		 * test.log(LogStatus.INFO,"creating a cloud_Account"); String
		 * prefix=spogServer.ReturnRandom("ramya"); //create an Organization
		 * cloud_account_id String
		 * cloud_account_id=createCloudAccount(UUID.randomUUID().toString(),
		 * "cloudAccountSecret","CloudAccountData",
		 * "cloud_direct",organization_id , prefix+"SKUTESTDATA_1_0_0_0_",
		 * prefix+"SKUTESTDATA_1_0_0_0_","91a9b48e-6ac6-4c47-8202-614b5cdcfe0c",
		 * spogServer,test); cloud_account_id =
		 * response.then().extract().path("data.cloud_account_id");
		 * test.log(LogStatus.INFO,"The value of the cloud_Account_id:"
		 * +cloud_account_id);
		 * user_events_code=spogServer.storetheuserevents(AuditCodeConstants.
		 * create_cloud_account, organization_id, user_id, cloud_account_id,
		 * response,userEmail, "user", "CloudAccountData", "cloud_account");
		 * User_Events.add(user_events_code);
		 * User_Events_Cloudaccount.add(user_events_code);
		 */

		// create destination cloud_direct
		spogDestinationServer.setToken(validToken);
		destination_name = spogServer.ReturnRandom("Ramya-test1") + "_site";
		test.log(LogStatus.INFO, "Create a destination of type cloud_direct_volume in org " + organizationType);
		response = spogDestinationServer.createDestination(UUID.randomUUID().toString(), organization_id,
				cloud_account_id, datacenters[0], DestinationType.cloud_direct_volume.toString(),
				DestinationStatus.running.toString(), "20", cloud_account_id, "normal",
				RandomStringUtils.randomAlphanumeric(4) + "host-t", "7D", "7 Days", "0", "0", "0", "0", "0", "0", "0",
				"0", "0", "0", destination_name, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		destination_id = response.then().extract().path("data.destination_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.create_destination, organization_id,
				user_id, destination_id, response, organizationEmail, "user", destination_name, "destination");
		User_Events.add(user_events_code);
		User_Events_Destinations.add(user_events_code);

		test.log(LogStatus.INFO, "Create a policy of task_type as ");
		test.log(LogStatus.INFO, "Create cloud direct schedule");
		task_type = "cloud_direct_file_folder_backup";
		HashMap<String, Object> cloudDirectScheduleDTO = policy4SpogServer.createCloudDirectScheduleDTO("0 6 * * *",
				test);

		test.log(LogStatus.INFO, "Create schedule settings");
		scheduleSettingDTO = policy4SpogServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null, test);
		schedules = policy4SpogServer.createPolicyScheduleDTO(null, schedule_id, "1d", task_id, destination_id,
				scheduleSettingDTO, "06:00", "12:00", task_type, destination_name, test);

		test.log(LogStatus.INFO, "Exclude the folders from cloud direct file backup");

		ArrayList<HashMap<String, Object>> excludes = policy4SpogServer.createExcludeInfoDTO(null, "path", "c:\\tmp",
				test);
		HashMap<String, Object> cloudDirectLocalBackupDTO = policy4SpogServer.createCloudDirectLocalBackupDTO("d:\\tmp",
				"true", test);
		HashMap<String, Object> cloudDirectFileBackupTaskInfoDTO = policy4SpogServer
				.createCloudDirectFileBackupTaskInfoDTO("d:\\tmp", cloudDirectLocalBackupDTO, excludes, test);

		ArrayList<String> drivers = new ArrayList<>();
		drivers.add("C");

		HashMap<String, Object> cloudDirectimageBackupTaskInfoDTO = policy4SpogServer
				.createCloudDirectImageBackupTaskInfoDTO(drivers, cloudDirectLocalBackupDTO, test);

		test.log(LogStatus.INFO, "Create task type and link it to the destination ");
		ArrayList<HashMap<String, Object>> destinations = policy4SpogServer.createPolicyTaskDTO(null, task_id,
				task_type, destination_id, "none", null, cloudDirectFileBackupTaskInfoDTO, null, test);

		test.log(LogStatus.INFO, "Create network throttle ");

		ArrayList<HashMap<String, Object>> throttles = policy4SpogServer.createPolicyThrottleDTO(null, throttle_id,
				task_id, throttle_type, "1200", "1", "06:00", "18:00", task_type, destination_id, destination_name,
				test);

		test.log(LogStatus.INFO, "Create a policy of type backup_recovery and task of type " + task_type);

		policy4SpogServer.setToken(validToken);
		response = policy4SpogServer.createPolicy(policy_name, policy_description, "cloud_direct_baas", null, "true",
				null, destinations, schedules, throttles, "", organization_id, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		String policy_id = response.then().extract().path("data.policy_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_POLICY, organization_id, user_id,
				policy_id, response, organizationEmail, "user", policy_name, "policy");
		User_Events.add(user_events_code);
		User_Events_policies.add(user_events_code);
		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Update the policies
		test.log(LogStatus.INFO, "Update the policy by policy name");
		policy_name = spogServer.ReturnRandom("test1");
		response = policy4SpogServer.updatePolicy(policy_name, policy_description, "cloud_direct_baas", null, "true",
				source_id, destinations, schedules, throttles, policy_id, organization_id, validToken, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_POLICY, organization_id, user_id,
				policy_id, response, organizationEmail, "user", policy_name, "policy");
		User_Events.add(user_events_code);
		User_Events_policies.add(user_events_code);

		// update the destination

		/*
		 * test.log(LogStatus.INFO, "Compose the retention values");
		 * HashMap<String,String> retentionInfo =
		 * spogDestinationServer.composeRetention("0", "0", "0", "0", "0", "0");
		 * 
		 * test.log(LogStatus.INFO, "Compose the cloud volume with dummy values"
		 * ); HashMap<String,Object> cloudvolInfo =
		 * spogDestinationServer.composeCloudDirectInfo("0","0", "0", "0", "0",
		 * "0", retentionInfo);
		 * 
		 * 
		 * test.log(LogStatus.INFO, "Compose the cloud dedupe volume ");
		 * HashMap<String,Object> clouddedupevolInfo =
		 * spogDestinationServer.composeCloudDedupeInfo("F:\\abc",
		 * "F:\\abc\\data", "F:\\abc\\index", "F:\\abc\\hash", "4", "true",
		 * "64", "1024", "true", "Mclaren@2010", "2", "2", "0.8","0.3");
		 * 
		 * response =
		 * spogDestinationServer.updatedestinationinfobydestinationId(
		 * destination_id, validToken, organization_id, site_id, datacenters[1],
		 * DestinationType.cloud_dedupe_store.toString(),
		 * DestinationStatus.running.toString(),spogServer.ReturnRandom(
		 * "Ramya-test1")+"_site",cloudvolInfo, clouddedupevolInfo);
		 * spogServer.checkResponseStatus(response,
		 * SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		 * user_events_code=spogServer.storetheuserevents(AuditCodeConstants.
		 * update_destination, organization_id, user_id, destination_id,
		 * response); User_Events.add(user_events_code);
		 * User_Events_Destinations.add(user_events_code);
		 */

		// Delete the destination
		/*
		 * test.log(LogStatus.INFO, "Delete destination by destination id");
		 * spogDestinationServer.deletedestinationbydestination_Id(
		 * destination_id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE,
		 * null, test);
		 * user_events_code=spogServer.storetheuserevents(AuditCodeConstants.
		 * delete_destination, organization_id, user_id, destination_id,
		 * response,userEmail, "user", destination_name, "destination");
		 * User_Events.add(user_events_code);
		 * User_Events_Destinations.add(user_events_code);
		 */

		test.log(LogStatus.INFO, "Create a source group");
		String groupName = "audit-source-group";
		response = spogServer.createGroup(organization_id, groupName, "audit purpose");
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		sourcegroup_id = response.then().extract().path("data.group_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.create_source_group, organization_id,
				user_id, sourcegroup_id, response, organizationEmail, "user", groupName, "source_group");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_Group.add(user_events_code);

		test.log(LogStatus.INFO, "Modify source group");
		groupName = "audit-source-group";
		response = spogServer.updateSourceGroup(sourcegroup_id, groupName, "modify source group");
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.modify_source_group, organization_id,
				user_id, sourcegroup_id, response, organizationEmail, "user", groupName, "source_group");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_Group.add(user_events_code);

		test.log(LogStatus.INFO, "Add source to source group");
		response = spogServer.addSourcetoSourceGroupwithCheck(sourcegroup_id, new String[] { source_id }, validToken,
				SpogConstants.SUCCESS_POST, null, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.add_source_to_group, organization_id,
				user_id, sourcegroup_id, response, organizationEmail, "user", groupName, "source_group");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_AddsourcetoGroup.add(user_events_code);

		test.log(LogStatus.INFO, "Delete source from source group");
		spogServer.deleteSourcefromSourceGroupwithCheck(sourcegroup_id, source_id, validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.delete_source_from_group, organization_id,
				user_id, sourcegroup_id, response1, organizationEmail, "user", groupName, "source_group");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_AddsourcetoGroup.add(user_events_code);

		test.log(LogStatus.INFO, "Delete source group");
		spogServer.deleteGroup(sourcegroup_id, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.delete_source_group, organization_id,
				user_id, sourcegroup_id, response1, organizationEmail, "user", groupName, "source_group");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_Group.add(user_events_code);

		test.log(LogStatus.INFO, "Create source filter for user");
		// spogServer.createFilterwithCheck(user_id, "test", "emptyarray",
		// "online", UUID.randomUUID().toString(), "finished", "emptyarray",
		// "windows", "emptyarray", SourceType.machine.name(), "true",test);
		String filter_name = "filter_s";
		response = spogServer.createFilter(user_id, filter_name, "emptyarray", "online", UUID.randomUUID().toString(),
				"finished", "none", "windows", "emptyarray", site_id, source_name, SourceType.machine.name(), "true",
				test);
		String filter_id = response.then().extract().path("data.filter_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.create_source_filter, organization_id,
				user_id, filter_id, response, organizationEmail, "user", filter_name, "source_filter");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_SourceFilter.add(user_events_code);

		test.log(LogStatus.INFO, "update source filter for user");
		filter_name = "filter_s";
		response = spogServer.updateFilter(user_id, filter_id, filter_name, "emptyarray", "online",
				UUID.randomUUID().toString(), "finished", "none", "windows", "emptyarray", site_id, source_name,
				SourceType.machine.name(), SourceType.machine.name(), "true", test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.modify_source_filter, organization_id,
				user_id, filter_id, response, organizationEmail, "user", filter_name, "source_filter");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_SourceFilter.add(user_events_code);

		test.log(LogStatus.INFO, "delete source filter for user");
		spogServer.deletefilterspecifiedbyUserIdwithCheck(user_id, filter_id, validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.delete_source_filter, organization_id,
				user_id, filter_id, response1, organizationEmail, "user", filter_name, "source_filter");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_SourceFilter.add(user_events_code);

		test.log(LogStatus.INFO, "Create destination filter for user");
		filter_name = "filter_d";
		response = spogDestinationServer.createDestinationFilter(user_id, filter_name, "filterdestname", "none",
				DestinationType.cloud_direct_volume.toString(), "true", test);
		filter_id = response.then().extract().path("data.filter_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.create_destination_filter, organization_id,
				user_id, filter_id, response, organizationEmail, "user", filter_name, "destination_filter");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_DestinationFilter.add(user_events_code);

		test.log(LogStatus.INFO, "update destination filter for user");
		/* filter_name="filter_dm"; */
		response = spogDestinationServer.updateDestinationFilterWithCheck(user_id, filter_id, filter_name, "none",
				"none", DestinationType.cloud_direct_volume.toString(), "true", test, false);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.modify_destination_filter, organization_id,
				user_id, filter_id, response, organizationEmail, "user", filter_name, "destination_filter");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_DestinationFilter.add(user_events_code);

		test.log(LogStatus.INFO, "delete destination filter for user");
		spogDestinationServer.deletedestinationfilterbyfilterId(user_id, filter_id, validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.delete_destination_filter, organization_id,
				user_id, filter_id, response1, organizationEmail, "user", filter_name, "destination_filter");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_DestinationFilter.add(user_events_code);

		test.log(LogStatus.INFO, "Create the job filter in org " + organizationType);
		spogJobServer.setToken(validToken);
		filter_name = "J-filterName";
		response = spogJobServer.createJobFilterWithCheckResponse(user_id, "finished", UUID.randomUUID().toString(),
				source_id, "backup_full", null, null, null, null, filter_name, "true", test);
		filter_id = response.then().extract().path("data.filter_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_JOB_FILTER, organization_id, user_id,
				filter_id, response, organizationEmail, "user", filter_name, "job_filter");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_JobFilter.add(user_events_code);

		test.log(LogStatus.INFO, "update the job filter in org " + organizationType);
		response = spogJobServer.updateJobFilterWithCheck(user_id, filter_id, "finished", UUID.randomUUID().toString(),
				source_id, "backup_full", null, null, filter_name, "true", test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_JOB_FILTER, organization_id, user_id,
				filter_id, response, organizationEmail, "user", filter_name, "job_filter");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_JobFilter.add(user_events_code);

		test.log(LogStatus.INFO, "delete the job filter in org " + organizationType);
		spogJobServer.deleteJobFilterforloggedInUser(filter_id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_JOB_FILTER, organization_id, user_id,
				filter_id, response1, organizationEmail, "user", filter_name, "job_filter");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_JobFilter.add(user_events_code);

		log4SPOGServer.setToken(validToken);
		test.log(LogStatus.INFO, "Create the log filter in org " + organizationType);
		log4SPOGServer.setToken(validToken);
		filter_name = "filter_l";
		response = log4SPOGServer.createLogFilter(user_id, organization_id, filter_name, "last_24_hours", null, null,
				LogSeverityType.warning.toString(), "false", "none", "none", null, "sourcename", "none", test);
		filter_id = response.then().extract().path("data.filter_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_LOG_FILTER, organization_id, user_id,
				filter_id, response, organizationEmail, "user", filter_name, "log_filter");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_LogFilter.add(user_events_code);

		test.log(LogStatus.INFO, "update the log filter in org " + organizationType);
		/* filter_name="filter_lm"; */
		response = log4SPOGServer.updateLogFilter(user_id, filter_id, organization_id, filter_name, "last_24_hours",
				null, "backup_full", LogSeverityType.warning.toString(), "false", "none", "none", "none", "sourcename",
				"none", test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_LOG_FILTER, organization_id, user_id,
				filter_id, response, organizationEmail, "user", filter_name, "log_filter");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_LogFilter.add(user_events_code);

		test.log(LogStatus.INFO, "delete the log filter in org " + organizationType);
		log4SPOGServer.deleteLogFilterforloggedinUser(filter_id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_LOG_FILTER, organization_id, user_id,
				filter_id, response1, organizationEmail, "user", filter_name, "log_filter");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_LogFilter.add(user_events_code);

		userSpogServer.setToken(validToken);

		test.log(LogStatus.INFO, "Create user filter in org of type " + organizationType);
		filter_name = "ufilter";
		response = userSpogServer.createUserFilterForSpecificUser(user_id, filter_name, "adfadf", null, null,
				SpogConstants.MSP_ADMIN, "true", validToken, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		filter_id = response.then().extract().path("data.filter_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_USER_FILTER, organization_id,
				user_id, filter_id, response, organizationEmail, "user", filter_name, "user_filter");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_UserFilter.add(user_events_code);

		test.log(LogStatus.INFO, "update user filter in org of type " + organizationType);
		response = userSpogServer.updateUserFilterForSpecificUser(user_id, filter_id, filter_name, "adfddf", null, null,
				SpogConstants.MSP_ADMIN, "true", validToken, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		filter_id = response.then().extract().path("data.filter_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_USER_FILTER, organization_id,
				user_id, filter_id, response, organizationEmail, "user", filter_name, "user_filter");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_UserFilter.add(user_events_code);

		test.log(LogStatus.INFO, "delete user filter in org of type " + organizationType);
		userSpogServer.deleteUserFilterByUserIdWithCheck(validToken, user_id, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_USER_FILTER, organization_id,
				user_id, filter_id, response1, organizationEmail, "user", filter_name, "user_filter");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_UserFilter.add(user_events_code);

		/*
		 * test.log(LogStatus.INFO, "Create hypervisor filter in org of type "
		 * +organizationType); //filter_id =
		 * userSpogServer.createHypervisorFilterForSpecificUser(user_id,
		 * "HfilterName", "status", "udp", "xen", "hypervisorName",
		 * "true",test); response =
		 * userSpogServer.createHypervisorFilterForLoggedinUser_audit(
		 * "HfilterName", "status", "udp", "xen", "hypervisorName",
		 * "true",test); filter_id =
		 * response.then().extract().path("data.filter_id"); user_events_code =
		 * spogServer.storetheuserevents(AuditCodeConstants.
		 * CREATE_HYPERVISOR_FILTER, organization_id, user_id, filter_id,
		 * response); User_Events.add(user_events_code);
		 * User_Events_Site.add(user_events_code);
		 * User_Events_HypervisorFilter.add(user_events_code);
		 * 
		 * test.log(LogStatus.INFO, "Update hypervisor filter in org of type "
		 * +organizationType);
		 * //userSpogServer.updateHypervisorFilterForSpecificUser(user_id,
		 * filter_id, "HfilterName", "status", "udp", "xen", "hypervisorName",
		 * "true",test); response =
		 * userSpogServer.updateHypervisorFilterForLoggedinUser(filter_id,
		 * "HfilterName", "status", "udp", "xen", "hypervisorName",
		 * "true",test); user_events_code =
		 * spogServer.storetheuserevents(AuditCodeConstants.
		 * MODIFY_HYPERVISOR_FILTER, organization_id, user_id, filter_id,
		 * response); User_Events.add(user_events_code);
		 * User_Events_Site.add(user_events_code);
		 * User_Events_HypervisorFilter.add(user_events_code);
		 * 
		 * test.log(LogStatus.INFO, "delete hypervisor filter in org of type "
		 * +organizationType);
		 * userSpogServer.deleteHypervisorFilterForSpecificUser(user_id,
		 * filter_id, test); user_events_code =
		 * spogServer.storetheuserevents(AuditCodeConstants.
		 * DELETE_HYPERVISOR_FILTER, organization_id, user_id, filter_id,
		 * response1); User_Events.add(user_events_code);
		 * User_Events_Site.add(user_events_code);
		 * User_Events_HypervisorFilter.add(user_events_code);
		 */

		test.log(LogStatus.INFO, "Create policy filter in org of type " + organizationType);
		filter_name = "PPfilterName";
		response = userSpogServer.createPolicyFilterForLoggedinUser(filter_name, policy_name,
				UUID.randomUUID().toString(), "finished", "failure", "true", test);
		filter_id = response.then().extract().path("data.filter_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_POLICY_FILTER, organization_id,
				user_id, filter_id, response, organizationEmail, "user", filter_name, "policy_filter");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_PolicyFilters.add(user_events_code);

		test.log(LogStatus.INFO, "update policy filter in org of type " + organizationType);
		/* filter_name="PPfilterName_m"; */
		response = userSpogServer.updatePolicyFilterForLoggedinUserWithCheck(filter_id, filter_name, policy_name,
				UUID.randomUUID().toString(), "finished", "failure", "true", test);
		filter_id = response.then().extract().path("data.filter_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_POLICY_FILTER, organization_id,
				user_id, filter_id, response, organizationEmail, "user", filter_name, "policy_filter");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_PolicyFilters.add(user_events_code);

		test.log(LogStatus.INFO, "delete policy filter in org of type " + organizationType);
		userSpogServer.deleteSpecificPolicyFilterForLoggedinUser(filter_id, test);
		filter_id = response.then().extract().path("data.filter_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_POLICY_FILTER, organization_id,
				user_id, filter_id, response1, organizationEmail, "user", filter_name, "policy_filter");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_PolicyFilters.add(user_events_code);

		// String policy_id=UUID.randomUUID().toString();
		test.log(LogStatus.INFO, "Create recoveredresource filter in org of type " + organizationType);
		filter_name = "recfailure";
		response = spogRecoveredResourceServer.createLoggedInUserRecoveredResourcesFilters(validToken, policy_id, "",
				OSMajor.windows.name(), "", filter_name, "true", "", test);
		filter_id = response.then().extract().path("data.filter_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_RECOVEREDRESOURCE_FILTER,
				organization_id, user_id, filter_id, response, organizationEmail, "user", filter_name,
				"recovered_resource_filter");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_RecoveryResourceFilters.add(user_events_code);

		test.log(LogStatus.INFO, "Update recoveredresource filter in org of type " + organizationType);
		filter_name = "recfailure";
		response = spogRecoveredResourceServer.updateLoggedInUserRecoveredResourcesFilters(validToken, filter_id,
				policy_id, "starting", "", "", filter_name, "true", SpogConstants.SUCCESS_GET_PUT_DELETE, "", test);
		filter_id = response.then().extract().path("data.filter_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_RECOVEREDRESOURCE_FILTER,
				organization_id, user_id, filter_id, response, organizationEmail, "user", filter_name,
				"recovered_resource_filter");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_RecoveryResourceFilters.add(user_events_code);

		test.log(LogStatus.INFO, "Delete recoveredresource filter in org of type " + organizationType);
		spogRecoveredResourceServer.deleteLoggedInUserRecoveredResourcesFilters(validToken, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		filter_id = response.then().extract().path("data.filter_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_RECOVEREDRESOURCE_FILTER,
				organization_id, user_id, filter_id, response1, organizationEmail, "user", filter_name,
				"recovered_resource_filter");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_RecoveryResourceFilters.add(user_events_code);

		// create report list filters for specified user
		test.log(LogStatus.INFO, "create report list filters for specified user");
		spogServer.setToken(validToken);

		spogReportServer.setToken(validToken);
		filter_name = spogServer.ReturnRandom("report");
		HashMap<String, Object> expectedfilter = spogReportServer.composeReportListFilterInfo(filter_name,
				"last_7_days", "all_sources", "", organization_id, "weekly", System.currentTimeMillis(),
				System.currentTimeMillis(), filter_name, true);
		response = spogReportServer.createReportListFiltersForLoggedInUserWithCheck(validToken, expectedfilter,
				SpogConstants.SUCCESS_POST, SpogMessageCode.SUCCESS_POST, test);
		filter_id = response.then().extract().path("data.filter_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_REPORT_LIST_FILTER, organization_id,
				user_id, filter_id, response, userEmail, "user", filter_name, "report_list_filter");
		User_Events.add(user_events_code);

		// update report list filters for specified user
		test.log(LogStatus.INFO, "update report list filters for specified user");
		test.log(LogStatus.INFO, "Update Filter List for Reports in org: " + organizationType);
		response = spogReportServer.updateReportListFiltersForLoggedInUserByFilterIdWithCheck(validToken, filter_id,
				expectedfilter, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		/*
		 * response = spogReportServer.
		 * updateReportListFiltersForLoggedInUserByFilterIdWithCheck(validToken,
		 * user_id, filter_id, expectedfilter, expectedfilter,
		 * SpogConstants.SUCCESS_GET_PUT_DELETE,
		 * SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		 */
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_REPORT_LIST_FILTER, organization_id,
				user_id, filter_id, response, userEmail, "user", filter_name, "report_list_filter");
		User_Events.add(user_events_code);

		// delete report list filters for specified user
		test.log(LogStatus.INFO, "delete report list filters for specified user");
		spogReportServer.deleteReportListFiltersForLoggedInUserByFilterIdWithCheck(validToken, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_REPORT_LIST_FILTER, organization_id,
				user_id, filter_id, response1, userEmail, "user", filter_name, "report_list_filter");
		User_Events.add(user_events_code);

		test.log(LogStatus.INFO, "Create user columns for logged in user in organization : " + organizationType);
		temp = userSpogServer.composeUser_Column("50169479-cf16-412a-9a6d-3bf19a111a0b", "true", "true", "true", "1");
		expectedColumns.add(temp);
		response = userSpogServer.createUserColumnsForLoggedInUser(validToken, expectedColumns, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_USER_COLUMN, organization_id,
				user_id, user_id, response, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_UserColumns.add(user_events_code);

		test.log(LogStatus.INFO, "update user columns for logged in user in organization : " + organizationType);
		response = userSpogServer.updateUserColumnsForLoggedInUser(validToken, expectedColumns, expectedColumns,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_USER_COLUMN, organization_id,
				user_id, user_id, response, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_UserColumns.add(user_events_code);

		test.log(LogStatus.INFO, "delete user columns for logged in user in organization : " + organizationType);
		userSpogServer.deleteUserColumnsforSpecifiedUserwithCheck(user_id, validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_USER_COLUMN, organization_id,
				user_id, user_id, response1, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_UserColumns.add(user_events_code);

		test.log(LogStatus.INFO, "Create destination columns for logged in user in organization : " + organizationType);
		temp = userSpogServer.composeUser_Column("a6e15988-2919-4fd2-9b7d-00d2eddffde8", "false", "true", "true", "1");
		expectedColumns = new ArrayList<>();
		expectedColumns.add(temp);
		response = spogDestinationServer.createDestinationColumnsForSpecifiedUser(user_id, validToken, expectedColumns,
				test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_USER_DESTINATION_COLUMN,
				organization_id, user_id, user_id, response, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_DestinationColumns.add(user_events_code);

		test.log(LogStatus.INFO, "update destination columns for logged in user in organization : " + organizationType);
		response = spogDestinationServer.updateDestinationColumnsForLoggedinUser(validToken, expectedColumns,
				expectedColumns, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_USER_DESTINATION_COLUMN,
				organization_id, user_id, user_id, response, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_DestinationColumns.add(user_events_code);

		test.log(LogStatus.INFO, "delete destination columns for logged in user in organization : " + organizationType);
		spogDestinationServer.deleteDestinatinColumnsforLoggedInUserwithCheck(validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_USER_DESTINATION_COLUMN,
				organization_id, user_id, user_id, response1, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_DestinationColumns.add(user_events_code);

		test.log(LogStatus.INFO, "Create job columns for logged in user in organization : " + organizationType);
		temp = spogJobServer.composejob_Column("c6a4be7e-bf1c-4e88-a278-9c34f5f69117", "false", "true", "true", "1");
		expectedColumns = new ArrayList<>();
		expectedColumns.add(temp);
		response = spogJobServer.createJobColumnsForSpecifiedUser(user_id, validToken, expectedColumns, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_USER_JOB_COLUMN, organization_id,
				user_id, user_id, response, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_JobColumns.add(user_events_code);

		test.log(LogStatus.INFO, "update job columns for logged in user in organization : " + organizationType);
		response = spogJobServer.updateJobColumnsForLoggedInUser(validToken, expectedColumns, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_USER_JOB_COLUMN, organization_id,
				user_id, user_id, response, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_JobColumns.add(user_events_code);

		test.log(LogStatus.INFO, "delete job columns for logged in user in organization : " + organizationType);
		spogJobServer.deleteJobColumnsForLoggedInUser(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_USER_JOB_COLUMN, organization_id,
				user_id, user_id, response1, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_JobColumns.add(user_events_code);

		test.log(LogStatus.INFO, "Create log columns for logged in user in organization : " + organizationType);
		temp = log4SPOGServer.composelog_Column("55d3dbb0-83e7-45ae-879a-8f3a522d66c5", "true", "true", "true", "1");
		expectedColumns = new ArrayList<>();
		expectedColumns.add(temp);
		response = log4SPOGServer.createLogColumnsForLoggedInUser(validToken, expectedColumns, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_USER_LOG_COLUMN, organization_id,
				user_id, user_id, response, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_LogColumns.add(user_events_code);

		test.log(LogStatus.INFO, "update log columns for logged in user in organization : " + organizationType);
		response = log4SPOGServer.updateLogColumnsForloggedinUser(validToken, expectedColumns, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_USER_LOG_COLUMN, organization_id,
				user_id, user_id, response, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_LogColumns.add(user_events_code);

		test.log(LogStatus.INFO, "delete log columns for logged in user in organization : " + organizationType);
		log4SPOGServer.deleteLogColumnsforLoggedInUserwithCheck(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_USER_LOG_COLUMN, organization_id,
				user_id, user_id, response1, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_LogColumns.add(user_events_code);

		test.log(LogStatus.INFO, "Create source columns for logged in user in organization : " + organizationType);
		temp = log4SPOGServer.composelog_Column("07871b87-6ec5-44ae-a1bb-17abfb75a9f9", "false", "true", "true", "1");
		expectedColumns = new ArrayList<>();
		expectedColumns.add(temp);
		response = spogSourceServer.createUsersSourcesColumns(user_id, expectedColumns, validToken);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_USER_SOURCE_COLUMN, organization_id,
				user_id, user_id, response, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_SourceColumns.add(user_events_code);

		test.log(LogStatus.INFO, "update source columns for logged in user in organization : " + organizationType);
		response = spogSourceServer.updateUsersSourcesColumns(user_id, expectedColumns, validToken);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_USER_SOURCE_COLUMN, organization_id,
				user_id, user_id, response, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_SourceColumns.add(user_events_code);

		test.log(LogStatus.INFO, "delete source columns for logged in user in organization : " + organizationType);
		spogSourceServer.deleteUsersSourcesColumns(user_id, validToken);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_USER_SOURCE_COLUMN, organization_id,
				user_id, user_id, response1, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_SourceColumns.add(user_events_code);

		test.log(LogStatus.INFO, "Create hypervisor columns for logged in user in organization : " + organizationType);
		temp = log4SPOGServer.composelog_Column("31dfe327-b9fe-432a-a119-24b584a85263", "true", "true", "true", "1");
		expectedColumns = new ArrayList<>();
		expectedColumns.add(temp);
		response = spogSourceServer.createHypervisorColumnsForLoggedInUser(expectedColumns, validToken);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_USER_HYPERVISOR_COLUMN,
				organization_id, user_id, user_id, response, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_HypervisorColumns.add(user_events_code);

		test.log(LogStatus.INFO, "update hypervisor columns for logged in user in organization : " + organizationType);
		response = spogSourceServer.updateHypervisorColumnsForLoggedInUser(expectedColumns, validToken);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_USER_HYPERVISOR_COLUMN,
				organization_id, user_id, user_id, response, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_HypervisorColumns.add(user_events_code);

		test.log(LogStatus.INFO, "delete hypervisor columns for logged in user in organization : " + organizationType);
		spogSourceServer.deleteHypervisorColumnsForLoggedInUser(validToken);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_USER_HYPERVISOR_COLUMN,
				organization_id, user_id, user_id, response1, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_HypervisorColumns.add(user_events_code);

		test.log(LogStatus.INFO,
				"Create recovered resource columns for logged in user in organization: " + organizationType);
		temp = spogRecoveredResourceServer.composeRecoveredResource_Column("84129da7-d794-4cc5-bfe1-17daf6b911bb",
				"true", "false", "true", "1");
		expectedColumns = new ArrayList<>();
		expectedColumns.add(temp);
		response = spogRecoveredResourceServer.createRecoveredResourceColumnsForLoggedInUser(validToken,
				expectedColumns, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_USER_RECOVEREDRESOURCE_COLUMN,
				organization_id, user_id, user_id, response, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_RecoveryResourceColumns.add(user_events_code);

		test.log(LogStatus.INFO,
				"update recovered resource columns for logged in user in organization: " + organizationType);
		temp = spogRecoveredResourceServer.composeRecoveredResource_Column("84129da7-d794-4cc5-bfe1-17daf6b911bb",
				"true", "false", "true", "1");
		expectedColumns = new ArrayList<>();
		expectedColumns.add(temp);
		response = spogRecoveredResourceServer.updateRecoveredResourceColumnsForLoggedinUser(validToken,
				expectedColumns, expectedColumns, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_USER_RECOVEREDRESOURCE_COLUMN,
				organization_id, user_id, user_id, response, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_RecoveryResourceColumns.add(user_events_code);

		test.log(LogStatus.INFO,
				"delete recovered resource columns for logged in user in organization: " + organizationType);
		spogRecoveredResourceServer.deleteRecoveredResourceColumnsforLoggedInUserwithCheck(validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_USER_RECOVEREDRESOURCE_COLUMN,
				organization_id, user_id, user_id, response1, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_RecoveryResourceColumns.add(user_events_code);

		// create user policy columns
		test.log(LogStatus.INFO, "create user policy columns for logged in user in organization: " + organizationType);
		policy4SpogServer.setToken(validToken);
		ArrayList<HashMap<String, Object>> expected_columns_1 = new ArrayList<HashMap<String, Object>>();
		expected_columns_1 = policy4SpogServer.getPolicyColumnArrayList("Protected Sources;false;2", validToken, test);
		response = policy4SpogServer.createLoggedInUserPolicyColumns(expected_columns_1, validToken);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_USER_POLICY_COLUMN, organization_id,
				user_id, user_id, response, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_PolicyColumns.add(user_events_code);

		test.log(LogStatus.INFO, "update user policy columns for logged in user in organization: " + organizationType);
		policy4SpogServer.setToken(validToken);
		response = policy4SpogServer.updateLoggedInUserPolicyColumns(expected_columns_1, validToken);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_USER_POLICY_COLUMN, organization_id,
				user_id, user_id, response, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_PolicyColumns.add(user_events_code);

		test.log(LogStatus.INFO, "delete user policy columns for logged in user in organization: " + organizationType);
		policy4SpogServer.setToken(validToken);
		response = policy4SpogServer.deleteLoggedInUserPolicyColumns(validToken);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_USER_POLICY_COLUMN, organization_id,
				user_id, user_id, response1, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_PolicyColumns.add(user_events_code);

		long start_time_ts = System.currentTimeMillis();
		// policy_id = UUID.randomUUID().toString();
		test.log(LogStatus.INFO, "Create job for logged in user in organization : " + organizationType);
		String job_name = "";
		String rps_id = UUID.randomUUID().toString();
		String job_id= UUID.randomUUID().toString();
		response = gatewayServer.postJob(start_time_ts, System.currentTimeMillis() + 10, organization_id, source_id,
				source_id, rps_id, destination_id, policy_id, "backup", "full", "finished", site_token, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		/*job_id = response.then().extract().path("data.job_id");*/
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_JOB, organization_id, site_id,
				job_id, response, site_id, "site", job_name, "job");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_jobs.add(user_events_code);

		test.log(LogStatus.INFO, "update job  for logged in user in organization : " + organizationType);
		response = gatewayServer.updateJob(job_id, start_time_ts, System.currentTimeMillis(), organization_id,
				source_id, source_id, null, null, policy_id, "backup", "full", "finished", site_token, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_JOB, organization_id, site_id,
				job_id, response, site_id, "site", job_name, "job");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_jobs.add(user_events_code);

		test.log(LogStatus.INFO, "create job data for logged in user in organization : " + organizationType);
		response = gatewayServer.postJobData(job_id, "1", "success", "100", "0", "0", "0", "0", "0", "0", "0", "none",
				"none", "none", "none", "none", "none", "none", "none", site_token, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_JOB_DATA, organization_id, site_id,
				site_id, response, site_id, "site", job_name, "job");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_jobs_data.add(user_events_code);

		test.log(LogStatus.INFO, "update job data for logged in user in organization : " + organizationType);
		response = gatewayServer.updateJobData(job_id, job_id, "1", "success", "full", "finished",
				String.valueOf(start_time_ts), "0", "0", "0", "0", "0", site_token, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_JOB_DATA, organization_id, site_id,
				job_id, response, site_id, "site", job_name, "job");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_jobs_data.add(user_events_code);

		test.log(LogStatus.INFO, "Create log");
		String log_id=UUID.randomUUID().toString();
		response = gatewayServer.createLog(System.currentTimeMillis(), log_id,job_id, organization_id, organization_id,
				source_id, "information", "udp", "testLogMessage", "SPOQ,QA", "test", site_token, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_LOG, organization_id, site_id,
				log_id, response, site_id, "site", job_name, "log");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_logs.add(user_events_code);

		test.log(LogStatus.INFO, "update log");
		response = log4GatewayServer.updateLog(System.currentTimeMillis(), log_id, job_id, organization_id,
				organization_id, source_id, "information", "udp", "testLogMessage", new String[] { "SPOQ", "QA" },
				JobType4LatestJob.backup_full.toString(), source_name, source_name, "test", true, site_token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_LOG, organization_id, site_id,
				log_id, response, site_id, "site", job_name, "log");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_logs.add(user_events_code);

		test.log(LogStatus.INFO, "Delete the logs by log Id");
		spogServer.deletelogbylogId(validToken, log_id, SpogConstants.SUCCESS_GET_PUT_DELETE,
				SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_LOG, organization_id, user_id,
				log_id, response, site_id, "site", job_name, "log");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_logs.add(user_events_code);

		// create report filter
		test.log(LogStatus.INFO, "create  report filter");
		HashMap<String, Object> reportfilterInfo = null;
		test.log(LogStatus.INFO, "compose report filter");
		filter_name = "reportFilter";
		reportfilterInfo = spogReportServer.composeReportFilterInfo(source_name, destination_name, policy_id,
				destination_id, "", "", "today", System.currentTimeMillis(), System.currentTimeMillis(), "backup_jobs",
				"", "backup", "", "", filter_name, true, "backup_jobs");
		response = spogReportServer.createReportFiltersForLoggedInUser_audit(validToken, reportfilterInfo,
				SpogConstants.SUCCESS_POST, null, test);
		filter_id = response.then().extract().path("data.filter_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_REPORT_FILTER, organization_id,
				user_id, filter_id, response, organizationEmail, "user", filter_name, "report_filter");
		User_Events.add(user_events_code);

		// update report filter
		test.log(LogStatus.INFO, "update  report filter");
		reportfilterInfo = spogReportServer.composeReportFilterInfo(source_name, destination_name, policy_id,
				destination_id, "", "", "today", System.currentTimeMillis(), System.currentTimeMillis(), "backup_jobs",
				"", "backup", "", "", filter_name, true, "backup_jobs");
		response = spogReportServer.updateReportFiltersForLoggedInUser(validToken, filter_id, reportfilterInfo,
				reportfilterInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_REPORT_FILTER, organization_id,
				user_id, filter_id, response, organizationEmail, "user", filter_name, "report_filter");
		User_Events.add(user_events_code);

		// delete report filter
		test.log(LogStatus.INFO, "delete report filter");
		response = spogReportServer.deleteReportFiltersForSpecifiedUser(validToken, user_id, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_REPORT_FILTER, organization_id,
				user_id, filter_id, response1, organizationEmail, "user", filter_name, "report_filter");
		User_Events.add(user_events_code);

		// create backup job report columns
		test.log(LogStatus.INFO, "create backup job report columns");
		ArrayList<HashMap<String, Object>> expected_columns_2 = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> temp2 = new HashMap<>();
		for (int j = 0; j < 1; j++) {
			temp2 = spogReportServer.composeBackUpJobReportsColumns("b9004719-1897-4f09-abd2-7a9fb6edb768", "true",
					"3");

			expected_columns_2.add(temp2);
		}

		response = spogReportServer.createBackUpJobReportsColumnsforLoggedInUser(validToken, expected_columns_2,
				SpogConstants.SUCCESS_POST, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_USER_BACKUPJOBREPORT_COLUMN,
				organization_id, user_id, user_id, response, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);

		expected_columns_2.clear();

		// update backup job report columns
		test.log(LogStatus.INFO, "update backup job report columns");
		for (int j = 0; j < 1; j++) {
			temp2 = spogReportServer.composeBackUpJobReportsColumns("b9004719-1897-4f09-abd2-7a9fb6edb768", "true",
					"4");

			expected_columns_2.add(temp2);
		}

		response = spogReportServer.updateBackUpJobReportsColumnsForLoggedInUser(validToken, expected_columns_2,
				SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_USER_BACKUPJOBREPORT_COLUMN,
				organization_id, user_id, user_id, response, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);

		// delete BackUpJobReportsColumnsForSpecifiedUser
		test.log(LogStatus.INFO, "delete BackUpJobReportsColumnsForSpecifiedUser ");
		spogReportServer.deleteBackUpJobReportsColumnsForSpecifiedUser(validToken, user_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_USER_BACKUPJOBREPORT_COLUMN,
				organization_id, user_id, user_id, response1, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);

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
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_USER_RESTOREJOBREPORT_COLUMN,
				organization_id, user_id, user_id, response, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);

		expected_columns_2.clear();

		// update restore job report columns
		test.log(LogStatus.INFO, "update restore job report columns");
		for (int j = 0; j < 1; j++) {
			temp2 = spogReportServer.composeRestoreJobReports_Column("07871b87-6ec5-44ae-a1bb-17abfb75a9f9", "true",
					"false", "true", "4");

			expected_columns_2.add(temp2);
		}

		response = spogReportServer.updateRestoreJobReportsColumnsForLoggedinUser(validToken, expected_columns_2,
				expected_columns_2, SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_USER_RESTOREJOBREPORT_COLUMN,
				organization_id, user_id, user_id, response, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);

		// delete RestoreJobReportsColumnsForSpecifiedUser
		test.log(LogStatus.INFO, "delete RestoreJobReportsColumnsForSpecifiedUser ");
		spogReportServer.deleteRestoreJobReportsColumnsforLoggedInUserwithCheck(validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_USER_RESTOREJOBREPORT_COLUMN,
				organization_id, user_id, user_id, response1, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);

		// delete the site
		test.log(LogStatus.INFO, "Delete the site " + site_id);
		response = spogServer.deleteSite(site_id, validToken);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_SITE, organization_id, user_id,
				site_id, response1, organizationEmail, "user", "test", "site");
		User_Events.add(user_events_code);
		User_Events_Site.add(user_events_code);
		User_Events_Sites_Org.add(user_events_code);

		test.log(LogStatus.INFO, "The expected data is " + User_Events.toString());

		// modify the organization by Id
		spogServer.setToken(validToken);
		response = spogServer.updateOrganizationInfoByID(organization_id, "spog_udp_qa_auto_ramya", test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_ORGANIZATION, organization_id,
				user_id, organization_id, response, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_Org.add(user_events_code);
		User_Events_Sites_Org.add(user_events_code);

		// modify the loggedIn user organization
		organizationName = "spog_udp_qa_auto_ramya";
		response = spogServer.UpdateLoggedInOrganization(organizationName, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_LOGIN_USER_ORGANIZATION,
				organization_id, user_id, organization_id, response, organizationEmail, "user", organizationName,
				"organization");
		User_Events.add(user_events_code);
		User_Events_Org.add(user_events_code);
		User_Events_Sites_Org.add(user_events_code);

		// All events of the user

		userEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;

		test.log(LogStatus.INFO, "Create the user");
		response = spogServer.createUser(userEmail, userPassword, FirstName, LastName, Role_Id, organization_id, test);
		String user_id1 = spogServer.checkCreateUser(response, SpogConstants.SUCCESS_POST, userEmail, FirstName,
				LastName, Role_Id, organization_id, "", test);
		// spogServer.userLogin(userEmail, userPassword,test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.CREATE_USER, organization_id, user_id,
				user_id1, response, organizationEmail, "user", userEmail, "user");
		User_Events.add(user_events_code);

		test.log(LogStatus.INFO, "Modify the user");
		response = spogServer.updateUserById(user_id1, userEmail, "", FirstName + "modi", LastName, Role_Id,
				organization_id, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_USER, organization_id, user_id,
				user_id1, response, userEmail, "user", userEmail, "user");
		User_Events.add(user_events_code);
		User_Events_User.add(user_events_code);
		/*
		 * test.log(LogStatus.INFO,
		 * "Modify the logged in user password by user id"); String
		 * changepassword = "Mclaren@2016"; response =
		 * spogServer.changePasswordForSpecifiedUser(user_id1, organizationPwd,
		 * changepassword); spogServer.checkResponseStatus(response,
		 * SpogConstants.SUCCESS_GET_PUT_DELETE, test); user_events_code =
		 * spogServer.storetheuserevents(AuditCodeConstants.
		 * MODIFY_USER_PASSWORD, organization_id, user_id1, user_id1, response,
		 * userEmail, "user", userEmail, "user");
		 * User_Events.add(user_events_code);
		 * User_Events_User.add(user_events_code);
		 * 
		 * test.log(LogStatus.INFO, "Modify the login user"); response =
		 * spogServer.UpdateLoggedInUser(organizationEmail, "Mclaren@2016",
		 * organizationFirstName, organizationLastName + "mod", Role_Id,
		 * organization_id); user_events_code =
		 * spogServer.storetheuserevents(AuditCodeConstants.MODIFY_LOGIN_USER,
		 * organization_id, user_id1, user_id1, response, organizationEmail,
		 * "user", organizationEmail, "user");
		 * User_Events.add(user_events_code);
		 * User_Events_User.add(user_events_code);
		 */

		test.log(LogStatus.INFO, "Delete the user");
		spogServer.DeleteUserById(user_id1, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_USER, organization_id, user_id,
				user_id1, response1, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_User.add(user_events_code);

		test.log(LogStatus.INFO, "Modify login user password");
		spogServer.setToken(validToken);
		response = spogServer.changePasswordForLoggedInUser(organizationPwd, "Mclaren@2016");
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_LOGIN_USER_PASSWORD, organization_id,
				user_id, user_id, response, organizationEmail, "user", organizationEmail, "user");
		User_Events.add(user_events_code);
		User_Events_User.add(user_events_code);
		User_Events_byCodeid.add(user_events_code);

		test.log(LogStatus.INFO, "update the existing source with check");
		source_name = "test";
		response = spogServer.updateSourcebysourceId(source_id, "test", SourceType.machine, SourceProduct.cloud_direct,
				organization_id, site_id, "", ProtectionStatus.unprotect, ConnectionStatus.online, "windows",
				validToken, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		source_id = response.then().extract().path("data.source_id");
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.MODIFY_SOURCE, organization_id, user_id,
				source_id, response, organizationEmail, "user", source_name, "source");
		User_Events.add(user_events_code);
		User_Events_Sources.add(user_events_code);

		// Delete a source
		test.log(LogStatus.INFO, "Delete the source");
		spogServer.setToken(csr_token);
		response = spogServer.deleteSourcesById(csr_token, source_id, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_SOURCE, organization_id, user_id,
				source_id, response, organizationEmail, "user", source_name, "source");
		User_Events.add(user_events_code);
		User_Events_Sources.add(user_events_code);

		// delete policy
		test.log(LogStatus.INFO, "Delete the policy by policy id");
		policy4SpogServer.deletePolicybyPolicyId(validToken, policy_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);
		user_events_code = spogServer.storetheuserevents(AuditCodeConstants.DELETE_POLICY, organization_id, user_id,
				policy_id, response1, organizationEmail, "user", policy_name, "policy");
		User_Events.add(user_events_code);
		User_Events_policies.add(user_events_code);

		for (int i = 0; i < 2; i++) {
			if (i == 1) {
				validToken = csr_read_only_validToken;
			}
			spogServer.setToken(validToken);
			ArrayList<HashMap> Expectedresponse = new ArrayList<HashMap>();
			test.log(LogStatus.INFO, "Prepare the URL");
			String filter = "code_id;in;4001|4002";
			String additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO, "compose audit trails for validation ");
			Expectedresponse = compose_expected_response(filter, User_Events_policies);
			test.log(LogStatus.INFO,
					"Get the audit trail by direct org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, Expectedresponse, 1, 20, "",
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			filter = "code_id;in;1028|1029|1030";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO, "compose audit trails for validation ");
			Expectedresponse = compose_expected_response(filter, User_Events);
			test.log(LogStatus.INFO,
					"Get the audit trail by direct org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, Expectedresponse, 1, 20, "",
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			filter = "code_id;in;2025|2026|2027";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO, "compose audit trails for validation ");
			Expectedresponse = compose_expected_response(filter, User_Events);
			test.log(LogStatus.INFO,
					"Get the audit trail by direct org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, Expectedresponse, 1, 20,
			// "",
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			filter = "code_id;in;2028|2029|2030";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO, "compose audit trails for validation ");
			Expectedresponse = compose_expected_response(filter, User_Events);
			test.log(LogStatus.INFO,
					"Get the audit trail by direct org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, Expectedresponse, 1, 20,
			// "",
			// "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			// get Audit trails for list reports
			ArrayList<HashMap> expectedResponse = new ArrayList<HashMap>();
			test.log(LogStatus.INFO, "Prepare the URL");
			filter = "code_id;in;1025|1026|1027";
			additionalUrl = spogServer.PrepareURL(filter, "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by direct org ID with ascending order and current page is 1 and pagesize 20");
			expectedResponse = compose_expected_response(filter, User_Events);
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, expectedResponse, 1, 20, "",
					"create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;7", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by direct org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_byCodeid, 1, 20,
					"", "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;701|702|703", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_Group, 1, 20,
					"code_id;in;701|702|703", "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;801|802", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_AddsourcetoGroup,
					1, 20, "code_id;in;801|802", "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;1001|1002|1003", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_SourceFilter, 1,
					20, "code_id;in;1001|1002|1003", "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;1004|1005|1006", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE,
					User_Events_DestinationFilter, 1, 20, "code_id;in;1004|1005|1006", "create_ts;asc",
					SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;1007|1008|1009", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_JobFilter, 1, 20,
					"code_id;in;1007", "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;1010|1011|1012", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_LogFilter, 1, 20,
					"code_id;in;1010|1011|1012", "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;1013|1014|1015", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_UserFilter, 1,
					20, "code_id;in;1013|1014|1015", "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;1019|1020|1021", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE,
					User_Events_RecoveryResourceFilters, 1, 20, "code_id;in;1019|1020|1021", "create_ts;asc",
					SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;1022|1023|1024", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_PolicyFilters, 1,
					20, "code_id;in;1022|1023|1024", "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;2001|2002|2003", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_SourceColumns,
			// 1,
			// 20, "code_id;in;2001|2002|2003", "create_ts;asc",
			// SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;2007|2008|2009", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE,
			// User_Events_HypervisorColumns,
			// 1, 20, "code_id;in;2007|2008|2009", "create_ts;asc",
			// SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			// additionalUrl =
			// spogServer.PrepareURL("code_id;in;2010|2011|2012",
			// "create_ts;asc", 1, 20, test);
			additionalUrl = spogServer.PrepareURL("code_id;in;2010|2011|2012", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_UserColumns, 1,
			// 20,
			// "code_id;in;2010|2011|2012", "create_ts;asc",
			// SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;2004|2005|2006", "create_ts;asc", 1, 20, test);
			// additionalUrl = spogServer.PrepareURL("code_id;in;2004",
			// "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE,
			// User_Events_DestinationColumns,
			// 1, 20, "code_id;in;2004|2005|2006", "create_ts;asc",
			// SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;2013|2014|2015", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_JobColumns, 1,
			// 20,
			// "code_id;in;2013|2014|2015", "create_ts;asc",
			// SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;2016|2017|2018", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_LogColumns, 1,
			// 20,
			// "code_id;in;2016|2017|2018", "create_ts;asc",
			// SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;2019|2020|2021", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE,
			// User_Events_RecoveryResourceColumns, 1, 20,
			// "code_id;in;2019|2020|2021", "create_ts;asc",
			// SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;2022|2023|2024", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			// spogServer.checkaudittraildata(response,
			// SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_PolicyColumns,
			// 1,
			// 20, "code_id;in;2022|2023|2024", "create_ts;asc",
			// SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;4006|4007", "create_ts;asc", 1, 20, test);
			// additionalUrl = spogServer.PrepareURL("code_id;in;4007",
			// "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			expectedResponse = compose_expected_response("code_id;in;4006|4007", User_Events_jobs);
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, expectedResponse, 1, 20,
					"code_id;in;4006|4007", "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;4008|4009", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_jobs_data, 1, 20,
					"code_id;in;4008|4009", "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;4001|4002|4003", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by msp org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_logs, 1, 20,
					"code_id;in;4001|4002|4003", "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;501|502|503", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by direct org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_Sources, 1, 20,
					"code_id;in;501|502|503", "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;601|602|603", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by direct org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_Destinations, 1,
					20, "code_id;in;601|602|603", "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;401|402|403|404", "create_ts;asc", 1, 20, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by direct org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_Site, 1, 20,
					"code_id;in;401|402|403", "create_ts;asc", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;401|402|403|404,create_ts;<;" + tomorrow, "", 1, 20,
					test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			// additionalUrl="filter=code_id:[401|402|403],code_id:[102|108]&sort=create_ts&page=1&page_size=20"
			test.log(LogStatus.INFO,
					"Get the audit trail by direct org ID with ascending order and current page is 1 and pagesize 20");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events_Sites_Org, 1, 20,
					"code_id;in;401|402|403,create_ts;<;" + tomorrow, "create_ts;asc",
					SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("code_id;in;401|402|403|404,create_ts;<;" + yesterday, "", 1, 20,
					test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			// additionalUrl="filter=code_id:[401|402|403],code_id:[102|108]&sort=create_ts&page=1&page_size=20"
			test.log(LogStatus.INFO, "Get the audit trail by timestamp less than the current day should result in 404");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			// spogServer.checkaudittraildata(response,
			// SpogConstants.RESOURCE_NOT_EXIST, User_Events_Sites_Org, 1, 20,
			// "",
			// "", SpogMessageCode.AUDIT_RESOURCE_NOT_FOUND, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

			test.log(LogStatus.INFO, "Prepare the URL");
			additionalUrl = spogServer.PrepareURL("", "create_ts;asc", 1, 10, test);
			test.log(LogStatus.INFO, "Get the audit trail by orgId " + organization_id);
			test.log(LogStatus.INFO,
					"Get the audit trail by direct org ID with ascending order and current page is 1 and pagesize 10");
			response = spogServer.getaudittrailbyorgId(validToken, organization_id, additionalUrl, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
			spogServer.checkaudittraildata(response, SpogConstants.SUCCESS_GET_PUT_DELETE, User_Events, 1, 10,
					"create_ts;asc", "", SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			test.log(LogStatus.INFO, "Response of audit trail is " + response.then().extract().asString());

		}
		test.log(LogStatus.INFO, "Login in as csr admin to delete the organization");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		csr_token = spogServer.getJWTToken();
		spogDestinationServer.recycleCloudVolumesAndDestroyOrg(csr_token, organization_id);

	}

	// to filter expected details from composed events
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

}
