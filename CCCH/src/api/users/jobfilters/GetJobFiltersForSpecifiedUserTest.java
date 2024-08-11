package api.users.jobfilters;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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

import Constants.ConnectionStatus;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.SPOGJobServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class GetJobFiltersForSpecifiedUserTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private SPOGJobServer spogJobServer;
	private UserSpogServer userSpogServer;

	private ExtentTest test;
	private TestOrgInfo ti;

	String rps_id = UUID.randomUUID().toString();
	String destination_id = UUID.randomUUID().toString();
	String Direct_cloud_id;
	String msp_cloud_id;
	private String root_cloud_id;

	@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runningMachine, String buildVersion) {

		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogJobServer = new SPOGJobServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		rep = ExtentManager.getInstance(this.getClass().getSimpleName(), logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Nagepalli.Ramya";

		Nooftest = 0;
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
		ti = new TestOrgInfo(spogServer, test);

		spogServer.setToken(ti.direct_org1_user1_token);
		Response response = spogServer.getCloudAccounts(ti.direct_org1_user1_token, "", test);
		Direct_cloud_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");

		response = spogServer.getCloudAccounts(ti.normal_msp_org2_user1_token, "", test);
		msp_cloud_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");

		response = spogServer.getCloudAccounts(ti.root_msp_org1_user1_token, "", test);
		root_cloud_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");
	}

	@DataProvider(name = "create_job_filter_valid")
	public final Object[][] createJobFilterValidParams() {
		return new Object[][] {
				{ "direct-csrreadonly", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id,
						"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup_full",
						System.currentTimeMillis() / 1000, 0, "filterName1", "false", Direct_cloud_id,
						ti.direct_org1_user1_token, 1, null, null, "origin" },
				{ "suborg-msp-csrreadonly", ti.normal_msp1_suborg2_id, ti.normal_msp_org1_user1_token,
						ti.normal_msp1_suborg2_user1_id, "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup_full", System.currentTimeMillis() / 1000, 0,
						"filterName2", "false", msp_cloud_id, ti.normal_msp1_suborg2_user1_token, 1, null, null,
						"origin" },
				/*{ "suborg-csrreadonly", ti.normal_msp1_suborg1_id, ti.normal_msp_org1_msp_accountadmin1_token,
						ti.normal_msp1_suborg1_user1_id, "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup_full", System.currentTimeMillis() / 1000, 0,
						"filterName3", "false", msp_cloud_id, ti.normal_msp1_suborg1_user1_token, 1, null, null,
						"origin" },*/

				{ "rootsub-msp-csrreadonly", ti.root_msp1_suborg1_id, ti.root_msp_org1_user1_token,
						ti.root_msp1_suborg1_user1_id, "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup_full", System.currentTimeMillis() / 1000, 0,
						"filterName4", "false", root_cloud_id, ti.root_msp1_suborg1_user1_token, 1, null, null,
						"origin" },
				{ "rootsub-csrreadonly", ti.root_msp1_suborg1_id, ti.root_msp_org1_msp_accountadmin1_token,
						ti.root_msp1_suborg1_user1_id, "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup_full", System.currentTimeMillis() / 1000, 0,
						"filterName5", "false", root_cloud_id, ti.root_msp1_suborg1_user1_token, 1, null, null,
						"origin" },

		};
	}

	@Test(dataProvider = "create_job_filter_valid")
	public void getJobFilterValid_200(String organizationType, String organization_id, String validToken,
			String user_id, String jobStatus, String policyID, String resourceID, String jobType, long startTimeTS,
			long endTimeTS, String filterName, String isDefault, String site_id, String siteToken, int filterCount,
			String filterStartTS, String filterEndTS, String view_type) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);
		String filterArray[] = new String[10];
		int count = 0;
		String filter_jobStatus = jobStatus;
		String filter_jobType = jobType;
		String filter_source = resourceID;
		String filter_policy = policyID;
		ArrayList<HashMap<String, Object>> expectedresponse = new ArrayList<>();
		HashMap<String, Object> expected_response = new HashMap<String, Object>();

		test.log(LogStatus.INFO, "Create a filter with jobType as backup_full");
		String prefix = spogServer.ReturnRandom("filter");
		String group_id = UUID.randomUUID().toString();
		spogJobServer.setToken(validToken);
		for (int i = 0; i < 2; i++) {

			prefix = spogServer.ReturnRandom("filter");
			filterName = prefix + filterName;
			
			Response response = spogJobServer.createJobFilterWithCheckResponse(user_id, filter_jobStatus, policyID,
					resourceID, filter_jobType, "last_24_hours", filterEndTS, null, null, filterName, isDefault,
					group_id, organization_id, view_type, test);

			filterArray[i] = response.then().extract().path("data.filter_id").toString();

			test.log(LogStatus.INFO, "Compose the expected job filter");
			expected_response = spogJobServer.composeExpectedJobFilter(filterArray[i], filterName, user_id,
					organization_id, filter_jobStatus, filter_jobType, filter_policy, filter_source, filterStartTS,
					filterEndTS, null, null, isDefault, i, group_id, view_type);
			expectedresponse.add(expected_response);
		}

		test.log(LogStatus.INFO, "Get job filters for a specified user id");
		spogJobServer.getJobFiltersForUserwithCheck(user_id, validToken, expectedresponse,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		test.log(LogStatus.INFO, "Get job filters for logged in user");
		spogJobServer.getJobFiltersForUserwithCheck(user_id, validToken, expectedresponse,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		test.log(LogStatus.INFO, "List all the job filter Ids for a specified in user whose is_default value is True");
		String additionalURL = spogServer.PrepareURL("is_default;=;true", "", 1, 20, test);

		spogJobServer.getJobFiltersForUserwithCheck_isdefault(user_id, validToken, additionalURL, expectedresponse,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		test.log(LogStatus.INFO, "List all the job filter Ids for a specified in user whose is_default value is False");

		additionalURL = spogServer.PrepareURL("is_default;=;false", "", 1, 20, test);
		spogJobServer.getJobFiltersForUserwithCheck_isdefault(user_id, validToken, additionalURL, expectedresponse,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		test.log(LogStatus.INFO,
				"List all the job filter Ids for a specified in user whose is_default value is False using csr token");
		spogJobServer.getJobFiltersForUserwithCheck_isdefault(user_id, ti.csr_token, additionalURL, expectedresponse,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		// applying filtering on filter_name
		additionalURL = spogServer.PrepareURL("filter_name;=;" + filterName, "", 1, 20, test);
		spogJobServer.getJobFiltersForUserwithCheck_isdefault(user_id, validToken, additionalURL, expectedresponse,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		// applying filtering on is_default true and filter_name
		additionalURL = spogServer.PrepareURL("is_default;=;false,filter_name;=;" + filterName, "", 1, 20, test);
		spogJobServer.getJobFiltersForUserwithCheck_isdefault(user_id, validToken, additionalURL, expectedresponse,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		if (organizationType.contains("suborg")) {
			test.log(LogStatus.INFO, "Get the job filters for a sub org user using msp token");
			spogJobServer.getJobFiltersForUserwithCheck_isdefault(user_id, ti.normal_msp_org1_user1_token,
					additionalURL, expectedresponse, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		}

		if (organizationType.contains("csrreadonly")) {
			additionalURL = spogServer.PrepareURL("is_default;=;false,filter_name;=;" + filterName, "", 1, 20, test);
			test.log(LogStatus.INFO, "Get the job filters for a org user using csr read only valid token");
			spogJobServer.getJobFiltersForUserwithCheck_isdefault(user_id, ti.csr_readonly_token, additionalURL,
					expectedresponse, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		}

		test.log(LogStatus.INFO, "Delete the job filter by filter ID");

		for (int i = 0; i < filterArray.length; i++) {
			if (filterArray[i] != null)
				spogJobServer.deleteJobFilterByFilterID(user_id, filterArray[i], validToken,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		}

	}

	// when no filters exist for the user try to get the filters for specific
	// user

	@Test(dataProvider = "create_job_filter_valid")
	public void getJobFilterValid_200_WithoutFilters(String organizationType, String organization_id, String validToken,
			String user_id, String jobStatus, String policyID, String resourceID, String jobType, long startTimeTS,
			long endTimeTS, String filterName, String isDefault, String site_id, String siteToken, int filterCount,
			String filterStartTS, String filterEndTS, String view_type) {
		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		test.log(LogStatus.INFO, "Get job filters for a specified user id when there are no Filters ");
		spogJobServer.getJobFiltersForUserwithCheck(user_id, validToken, new ArrayList<>(),
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

	}

	@DataProvider(name = "create_job_filter_401")
	public final Object[][] createJobFilterValidParams_401() {
		return new Object[][] {

				{ "direct", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id, "active,finished",
						null, null, null, "2018-01-01 12:00:00", "2018-01-02 12:00:00", null, null, "filterName",
						"true" },
				{ "msp", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id, "finished",
						null, null, null, "2018-01-01 12:00:00", "2018-01-02 12:00:00", null, null, "filterName",
						"true" },
				{ "suborg", ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token,
						ti.normal_msp1_suborg1_user1_id, "finished", null, null, null, "2018-01-01 12:00:00",
						"2018-01-02 12:00:00", null, null, "filterName", "true" },
				{ "csr", ti.csr_org_id, ti.csr_token, ti.csr_admin_user_id, "active,finished", null, null, null,
						"2018-01-01 12:00:00", "2018-01-02 12:00:00", null, null, "filterName", "true" }

		};
	}

	@Test(dataProvider = "create_job_filter_401")
	public void getJobFilterValid_401(String organizationType, String organization_id, String validToken,
			String user_id, String jobStatus, String policyID, String resourceID, String jobType,
			String startTimeTSStart, String startTimeTSEnd, String endTimeTSStart, String endTimeTSEnd,
			String filterName, String isDefault) {
		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		test.log(LogStatus.INFO, "Get job filters for a specified user id  with other invalid token");
		spogJobServer.getJobFiltersForUserwithCheck(user_id, validToken + "123", new ArrayList<>(),
				SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);

		test.log(LogStatus.INFO, "Get job filters for a specified user id  with null token");
		spogJobServer.getJobFiltersForUserwithCheck(user_id, null, new ArrayList<>(), SpogConstants.NOT_LOGGED_IN,
				SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);

		test.log(LogStatus.INFO, "Get job filters for a specified user id  with null token");
		spogJobServer.getJobFiltersForUserwithCheck(user_id, "", new ArrayList<>(), SpogConstants.NOT_LOGGED_IN,
				SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);

	}

	// Get the filters for the specified user, by passing [random user_id &
	// deleted user_id]

	@DataProvider(name = "create_job_filter_valid_404_Userid")
	public final Object[][] createJobFilterValidParams_404_Userid() {
		return new Object[][] {
				// different users
				{ "direct", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id, "active,finished",
						null, null, null, "2018-01-01 12:00:00", "2018-01-02 12:00:00", null, null, "filterName",
						"true" },
				{ "direct", ti.direct_org2_id, ti.csr_token, ti.direct_org2_user1_id, "active,finished", null, null,
						null, "2018-01-01 12:00:00", "2018-01-02 12:00:00", null, null, "filterName", "true" },
				/*
				 * { "suborg",ti.normal_msp1_suborg2_id,ti.csr_token,ti.
				 * normal_msp1_suborg2_user1_id, "finished",null, null, null,
				 * "2018-01-01 12:00:00", "2018-01-02 12:00:00", null, null, "filterName",
				 * "true" },
				 */{ "suborgmsptoken", ti.normal_msp1_suborg2_id, ti.normal_msp_org1_user1_token,
						ti.normal_msp1_suborg2_user1_id, "finished", null, null, null, "2018-01-01 12:00:00",
						"2018-01-02 12:00:00", null, null, "filterName", "true" },
				{ "suborg", ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token,
						ti.normal_msp1_suborg1_user1_id, "finished", null, null, null, "2018-01-01 12:00:00",
						"2018-01-02 12:00:00", null, null, "filterName", "true" },
				{ "msp", ti.normal_msp_org2_user1_token, ti.csr_token, ti.normal_msp_org2_user1_id, "finished", null,
						null, null, "2018-01-01 12:00:00", "2018-01-02 12:00:00", null, null, "filterName", "true" },
				{ "msp", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id, "finished",
						null, null, null, "2018-01-01 12:00:00", "2018-01-02 12:00:00", null, null, "filterName",
						"true" },
				{ "suborg", ti.normal_msp1_suborg2_id, ti.normal_msp_org1_msp_accountadmin1_token,
						ti.normal_msp1_suborg2_user1_id, "finished", null, null, null, "2018-01-01 12:00:00",
						"2018-01-02 12:00:00", null, null, "filterName", "true" },
				{ "csrreadonly-sub", ti.csr_org_id, ti.csr_readonly_token, ti.normal_msp1_suborg2_user1_id, "finished",
						null, null, null, "2018-01-01 12:00:00", "2018-01-02 12:00:00", null, null, "filterName",
						"true" },
				{ "csrreadonly-direct", ti.csr_org_id, ti.csr_readonly_token, ti.direct_org1_user1_id, "finished", null,
						null, null, "2018-01-01 12:00:00", "2018-01-02 12:00:00", null, null, "filterName", "true" },
				{ "csrreadonly-msp", ti.csr_org_id, ti.csr_readonly_token, ti.normal_msp_org1_user1_id, "finished",
						null, null, null, "2018-01-01 12:00:00", "2018-01-02 12:00:00", null, null, "filterName",
						"true" },

		};
	}

	@Test(dataProvider = "create_job_filter_valid_404_Userid")
	public void getJobFilterValid_404_Userid(String organizationType, String organization_id, String validToken,
			String user_id, String jobStatus, String policyID, String resourceID, String jobType,
			String startTimeTSStart, String startTimeTSEnd, String endTimeTSStart, String endTimeTSEnd,
			String filterName, String isDefault) {
		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		test.log(LogStatus.INFO, "Get job filters for a specified user id by passing random UserID");
		spogJobServer.getJobFiltersForUserwithCheck(UUID.randomUUID().toString(), validToken, new ArrayList<>(),
				SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.USER_ID_DOESNOT_EXIST, test);

	}

	@DataProvider(name = "create_job_filter_403")
	public final Object[][] createJobFilterValidParams_403() {
		return new Object[][] {
				// different users

				{ "direct-suborg-mspb", "direct_mspsuborg", ti.direct_org1_id, ti.direct_org1_user1_token,
						ti.direct_org1_user1_id, ti.normal_msp1_suborg1_user1_token, "active",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup_full",
						System.currentTimeMillis(), 0, "filterName", "true", Direct_cloud_id,
						ti.direct_org1_user1_token },
				{ "direct-suborg-mspb", "direct_directb", ti.direct_org1_id, ti.direct_org1_user1_token,
						ti.direct_org1_user1_id, ti.direct_org2_user1_token, "active", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup_full", System.currentTimeMillis(), 0, "filterName",
						"true", Direct_cloud_id, ti.direct_org1_user1_token },

				{ "direct-suborg-mspb", "suborg-direct", ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token,
						ti.normal_msp1_suborg1_user1_id, ti.direct_org1_user1_token, "active",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup_full",
						System.currentTimeMillis(), 0, "filterName", "true", msp_cloud_id,
						ti.normal_msp1_suborg1_user1_token },
				{ "direct-suborg-mspb", "suborg-mspb", ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token,
						ti.normal_msp1_suborg1_user1_id, ti.normal_msp_org2_user1_token, "active",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup_full",
						System.currentTimeMillis(), 0, "filterName", "true", msp_cloud_id,
						ti.normal_msp1_suborg1_user1_token },
				{ "direct-suborg-mspb", "suborg-suborgb", ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token,
						ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg2_user1_token, "active",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup_full",
						System.currentTimeMillis(), 0, "filterName", "true", msp_cloud_id,
						ti.normal_msp1_suborg1_user1_token },

				{ "direct-suborg-mspb", "suborg-direct", ti.normal_msp1_suborg1_id,
						ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp1_suborg1_user1_id,
						ti.direct_org1_user1_token, "active", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup_full", System.currentTimeMillis(), 0, "filterName",
						"true", msp_cloud_id, ti.normal_msp1_suborg1_user1_token },
				// csr read only cases
				{ "csrreadonly-direct", "csrreadonly-direct", ti.csr_org_id, ti.csr_readonly_token,
						ti.csr_readonly_admin_user_id, ti.direct_org1_user1_token, "active",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup_full",
						System.currentTimeMillis(), 0, "filterName", "true", Direct_cloud_id,
						ti.direct_org1_user1_token },
				{ "csrreadonly-suborg", "csrreadonly-suborg", ti.csr_org_id, ti.csr_readonly_token,
						ti.csr_readonly_admin_user_id, ti.normal_msp1_suborg1_user1_token, "active",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup_full",
						System.currentTimeMillis(), 0, "filterName", "true", msp_cloud_id,
						ti.normal_msp1_suborg1_user1_token },

				// 3 tier cases
				{ "rootsuborg-submsp", "root_suborg", ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_token,
						ti.root_msp1_suborg1_user1_id, ti.root_msp1_submsp1_user1_token, "active",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup_full",
						System.currentTimeMillis(), 0, "filterName", "true", root_cloud_id,
						ti.root_msp1_suborg1_user1_token },

				{ "rootsuborg-submsp_sub", "root_suborg", ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_token,
						ti.root_msp1_suborg1_user1_id, ti.msp1_submsp1_suborg1_user1_token, "active",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup_full",
						System.currentTimeMillis(), 0, "filterName", "true", root_cloud_id,
						ti.root_msp1_suborg1_user1_token },

				{ "submsp_sub-rootmsp", "submsp_sub", ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_token,
						ti.msp1_submsp1_suborg1_user1_id, ti.root_msp_org1_user1_token, "active",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup_full",
						System.currentTimeMillis(), 0, "filterName", "true", root_cloud_id,
						ti.root_msp_org1_user1_token },

				{ "rootmsp-submsp", "rootmsp", ti.root_msp_org1_id, ti.root_msp_org1_user1_token,
						ti.root_msp_org1_user1_id, ti.root_msp1_submsp1_user1_token, "active",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup_full",
						System.currentTimeMillis(), 0, "filterName", "true", root_cloud_id,
						ti.root_msp_org1_user1_token },
				{ "rootmsp-submsp_sub", "rootmsp", ti.root_msp_org1_id, ti.root_msp_org1_user1_token,
						ti.root_msp_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token, "active",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup_full",
						System.currentTimeMillis(), 0, "filterName", "true", root_cloud_id,
						ti.root_msp_org1_user1_token },
				{ "rootmsp-root_sub", "rootmsp", ti.root_msp_org1_id, ti.root_msp_org1_user1_token,
						ti.root_msp_org1_user1_id, ti.root_msp1_suborg1_user1_token, "active",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup_full",
						System.currentTimeMillis(), 0, "filterName", "true", root_cloud_id,
						ti.root_msp_org1_user1_token }, };
	}

	@Test(dataProvider = "create_job_filter_403")
	public void getJobFilterValid_403(String organization, String organizationType, String organization_id,
			String validToken, String user_id, String otherToken, String jobStatus, String policyID, String resourceID,
			String jobType, long startTime, long endTime, String filterName, String isDefault, String site_id,
			String siteToken)

	{
		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		test.log(LogStatus.INFO, "Get job filters for a specified user id  with other invalid token");
		spogJobServer.getJobFiltersForUserwithCheck(user_id, otherToken, new ArrayList<>(),
				SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY, test);

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

}
