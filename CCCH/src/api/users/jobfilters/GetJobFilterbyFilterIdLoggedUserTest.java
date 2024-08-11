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
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGJobServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class GetJobFilterbyFilterIdLoggedUserTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private SPOGJobServer spogJobServer;

	private ExtentTest test;
	private TestOrgInfo ti;

	LocalDate date = LocalDate.now();
	LocalDate yesterday = date.minusDays(1);
	LocalDate tomorrow = yesterday.plusDays(2);
	private String Direct_cloud_id;
	private String msp_cloud_id;
	private String root_cloud_id;

	@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runningMachine, String buildVersion) {

		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogJobServer = new SPOGJobServer(baseURI, port);
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
				// different users

				{ "direct", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id, "finished",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup_full",
						System.currentTimeMillis(), 0, "filterName", "true", Direct_cloud_id,
						ti.direct_org1_user1_token, null, null },
				{ "suborg", ti.normal_msp_org2_id, ti.normal_msp_org2_user1_token, ti.normal_msp_org2_user1_id,
						"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup_full",
						System.currentTimeMillis(), 0, "filterName", "true", msp_cloud_id,
						ti.normal_msp_org2_user1_token, null, null },

				{ "root_sub", ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,
						"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup_full",
						System.currentTimeMillis(), 0, "filterName", "true", root_cloud_id,
						ti.root_msp1_suborg1_user1_token, null, null },
				{ "submsp_sub", ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_token,
						ti.msp1_submsp1_suborg1_user1_id, "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup_full", System.currentTimeMillis(), 0, "filterName",
						"true", root_cloud_id, ti.msp1_submsp1_suborg1_user1_token, null, null },

		};
	}

	@Test(dataProvider = "create_job_filter_valid")
	public void getJobFilterValid(String organizationType, String organization_id, String validToken, String user_id,
			String jobStatus, String policyID, String resourceID, String jobType, long startTimeTS, long endTimeTS,
			String filterName, String isDefault, String site_id, String siteToken, String filterStartTS,
			String filterEndTS) {
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

		spogServer.setToken(validToken);
		spogJobServer.setToken(validToken);
		String prefix = RandomStringUtils.randomAlphanumeric(4);

		test.log(LogStatus.INFO, "Create a filter with jobType as backup_full");
		count = 0;

		for (int i = 0; i < 2; i++) {
			prefix = RandomStringUtils.randomAlphanumeric(4);
			filterName = prefix + filterName;
			spogServer.setToken(validToken);
			filterArray[i] = spogJobServer.createJobFilterWithCheckEx(user_id, filter_jobStatus, policyID, resourceID,
					filter_jobType, "last_24_hours", filterEndTS, null, null, filterName, isDefault, test);

			test.log(LogStatus.INFO, "Compose the expected job filter");
			expected_response = spogJobServer.composeExpectedJobFilter(filterArray[i], filterName, user_id,
					organization_id, filter_jobStatus, filter_jobType, filter_policy, filter_source, filterStartTS,
					filterEndTS, null, null, isDefault, i);
			expectedresponse.add(expected_response);

			spogJobServer.setToken(validToken);
			test.log(LogStatus.INFO, "Get specified job filter for logged in user");
			spogJobServer.getspecifiedJobFilterForLoggedInUser(filterArray[i], validToken, expected_response,
					SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

			spogJobServer.getspecifiedJobFilterByUserIDwithCheck(user_id, filterArray[i], ti.csr_readonly_token,
					expected_response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

			test.log(LogStatus.INFO, "Delete the job filter by filter ID");
			spogJobServer.deleteJobFilterByFilterID(user_id, filterArray[i], validToken,
					SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		}

	}

	// getjobfilterbyfilterId_401

	@Test(dataProvider = "create_job_filter_valid")
	public void getJobFilterValid_401(String organizationType, String organization_id, String validToken,
			String user_id, String jobStatus, String policyID, String resourceID, String jobType, long startTimeTS,
			long endTimeTS, String filterName, String isDefault, String site_id, String siteToken, String filterStartTS,
			String filterEndTS)

	{
		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);
		ArrayList<HashMap<String, Object>> expectedresponse = new ArrayList<>();
		HashMap<String, Object> expected_response = new HashMap<String, Object>();
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		int count = 0;
		spogJobServer.setToken(validToken);
		String filter_Id = spogJobServer.createJobFilterWithCheckEx(user_id, jobStatus, policyID, resourceID, jobType,
				"last_24_hours", filterEndTS, null, null, prefix + filterName, isDefault, test);

		test.log(LogStatus.INFO, "Compose the expected job filter");
		expected_response = spogJobServer.composeExpectedJobFilter(filter_Id, prefix + filterName, user_id,
				organization_id, jobStatus, jobType, policyID, resourceID, "last_24_hours", filterEndTS, null, null,
				isDefault, count);
		expectedresponse.add(expected_response);

		test.log(LogStatus.INFO, "Get filter by id for logged in user by passing  invalid token");
		spogJobServer.getspecifiedJobFilterForLoggedInUser(filter_Id, validToken + "12", expected_response,
				SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);

		test.log(LogStatus.INFO, "Get filter by id for logged in user by passing  NULL token");
		spogJobServer.getspecifiedJobFilterForLoggedInUser(filter_Id, null, expected_response,
				SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);

		test.log(LogStatus.INFO, "Get filter by id for logged in user by empty header token");
		spogJobServer.getspecifiedJobFilterForLoggedInUser(filter_Id, "", expected_response,
				SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);

		test.log(LogStatus.INFO, "Delete the job filter by filter ID");
		spogJobServer.deleteJobFilterByFilterID(user_id, filter_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE,
				null, test);

	}

	// getjobfilterbyfilterId_404

	@Test(dataProvider = "create_job_filter_valid")
	public void getJobFilterValid_404(String organizationType, String organization_id, String validToken,
			String user_id, String jobStatus, String policyID, String resourceID, String jobType, long startTimeTS,
			long endTimeTS, String filterName, String isDefault, String site_id, String siteToken, String filterStartTS,
			String filterEndTS) {
		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);
		ArrayList<HashMap<String, Object>> expectedresponse = new ArrayList<>();
		HashMap<String, Object> expected_response = new HashMap<String, Object>();
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		int count = 0;
		spogJobServer.setToken(validToken);
		String filter_Id = spogJobServer.createJobFilterWithCheckEx(user_id, jobStatus, policyID, resourceID, jobType,
				"last_24_hours", filterEndTS, null, null, prefix + filterName, isDefault, test);

		test.log(LogStatus.INFO, "Compose the expected job filter");
		expected_response = spogJobServer.composeExpectedJobFilter(filter_Id, prefix + filterName, user_id,
				organization_id, jobStatus, jobType, policyID, resourceID, filterStartTS, filterEndTS, null, null,
				isDefault, count);
		expectedresponse.add(expected_response);

		test.log(LogStatus.INFO, "Get filter by id for logged in user");
		spogJobServer.getspecifiedJobFilterForLoggedInUser(UUID.randomUUID().toString(), validToken, expected_response,
				SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.FILTER_NOT_FOUND_WITH_USER_ID, test);

		test.log(LogStatus.INFO, "Delete the job filter by filter ID");
		spogJobServer.deleteJobFilterByFilterID(user_id, filter_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE,
				null, test);

		test.log(LogStatus.INFO, "Get filter by id for logged in user after deleting the filter_id");
		spogJobServer.getspecifiedJobFilterForLoggedInUser(filter_Id, validToken, expected_response,
				SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.FILTER_NOT_FOUND_WITH_USER_ID, test);

	}

	@DataProvider(name = "create_job_filter_valid_404")
	public final Object[][] createJobFilterValidParams1() {
		return new Object[][] {
				// different users
				{ "direct", "direct_msp", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id,
						ti.normal_msp_org1_user1_token, "active,finished", null, null, null, null, null, null, null,
						"filterName", "true" },
				{ "direct", "direct_mspsuborg", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id,
						ti.normal_msp1_suborg1_user1_token, "active,finished", null, null, null, null, null, null, null,
						"filterName", "true" },
				{ "direct", "direct_directb", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id,
						ti.direct_org2_user1_token, "active,finished", null, null, null, null, null, null, null,
						"filterName", "true" },
				{ "msp", "msp-direct", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token,
						ti.normal_msp_org1_user1_id, ti.direct_org1_user1_token, "active,finished", null, null, null,
						null, null, null, null, "filterName", "true" },
				{ "msp", "msp-mspsuborg", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token,
						ti.normal_msp_org1_user1_id, ti.normal_msp1_suborg1_user1_token, "active,finished", null, null,
						null, null, null, null, null, "filterName", "true" },
				{ "mspb", "msp-mspb", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token,
						ti.normal_msp_org1_user1_id, ti.normal_msp_org2_user1_token, "active,finished", null, null,
						null, null, null, null, null, "filterName", "true" },
				{ "suborg", "suborg-direct", ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token,
						ti.normal_msp1_suborg1_user1_id, ti.direct_org1_user1_token, "active,finished", null, null,
						null, null, null, null, null, "filterName", "true" },
				{ "suborg", "suborg-mspb", ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token,
						ti.normal_msp1_suborg1_user1_id, ti.normal_msp_org2_user1_token, "active,finished", null, null,
						null, null, null, null, null, "filterName", "true" },
				{ "suborg", "suborg-suborgb", ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token,
						ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg2_user1_token, "active,finished", null,
						null, null, null, null, null, null, "filterName", "true" },
				{ "csr", "csr-msp", ti.csr_org_id, ti.csr_token, ti.csr_admin_user_id, ti.normal_msp_org1_user1_token,
						"active,finished", null, null, null, null, null, null, null, "filterName", "true" },
				// csr read only cases
				{ "csrreadonly", "csr-direct", ti.csr_org_id, ti.csr_readonly_token, ti.csr_readonly_admin_user_id,
						ti.direct_org1_user1_token, "active,finished", null, null, null, null, null, null, null,
						"filterName", "true" },
				{ "csrreadonly", "csr-msp", ti.csr_org_id, ti.csr_readonly_token, ti.csr_readonly_admin_user_id,
						ti.normal_msp_org1_user1_token, "active,finished", null, null, null, null, null, null, null,
						"filterName", "true" },
				{ "csrreadonly", "csr-suborg", ti.csr_org_id, ti.csr_readonly_token, ti.csr_readonly_admin_user_id,
						ti.normal_msp1_suborg1_user1_token, "active,finished", null, null, null, null, null, null, null,
						"filterName", "true" }, };
	}

	@Test(dataProvider = "create_job_filter_valid_404")
	public void getJobFilterValid_404_mixedOrg(String organization, String organizationType, String organization_id,
			String validToken, String user_id, String otherToken, String jobStatus, String policyID, String resourceID,
			String jobType, String startTimeTSStart, String startTimeTSEnd, String endTimeTSStart, String endTimeTSEnd,
			String filterName, String isDefault) {
		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);
		spogJobServer.setToken(validToken);
		
		test.log(LogStatus.INFO, "Get filter by id for logged in user after deleting the filter_id");
		spogJobServer.getspecifiedJobFilterForLoggedInUser(UUID.randomUUID().toString(), validToken, new HashMap<>(),
				SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.FILTER_NOT_FOUND_WITH_USER_ID, test);

	}

	@Test(dataProvider = "create_job_filter_valid")
	public void getJobFilterValid_Multiplefilters(String organizationType, String organization_id, String validToken,
			String user_id, String jobStatus, String policyID, String resourceID, String jobType, long startTimeTS,
			long endTimeTS, String filterName, String isDefault, String site_id, String siteToken, String filterStartTS,
			String filterEndTS) {

		String filtersArray[] = new String[10];

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);
		ArrayList<HashMap<String, Object>> expectedresponse = new ArrayList<>();
		HashMap<String, Object> expected_response = new HashMap<String, Object>();
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		int count = 0;
		spogJobServer.setToken(validToken);
		for (int i = 0; i < 2; i++) {

			filtersArray[i] = spogJobServer.createJobFilterWithCheckEx(user_id, jobStatus, policyID, resourceID,
					jobType, "last_24_hours", filterEndTS, null, null, prefix + filterName + i, isDefault, test);

			test.log(LogStatus.INFO, "Compose the expected job filter");
			expected_response = spogJobServer.composeExpectedJobFilter(filtersArray[i], prefix + filterName + i,
					user_id, organization_id, jobStatus, jobType, policyID, resourceID, "last_24_hours", filterEndTS,
					null, null, isDefault, i);
			expectedresponse.add(expected_response);

			test.log(LogStatus.INFO, "List all the job filter Ids for Logged in user" + filtersArray[i]);

			test.log(LogStatus.INFO, "Get filter by id for logged in user");
			spogJobServer.getspecifiedJobFilterForLoggedInUser(filtersArray[i], validToken, expected_response,
					SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

			test.log(LogStatus.INFO, "Delete the job filter by filter ID");
			spogJobServer.deleteJobFilterByFilterID(user_id, filtersArray[i], validToken,
					SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		}

	}

	@DataProvider(name = "create_job_filter_valid_ex")
	public final Object[][] createJobFilterValidParams_ex() {
		return new Object[][] {

				{ "direct", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id, "finished",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup_full",
						System.currentTimeMillis(), 0, "filterName", "true", Direct_cloud_id,
						ti.direct_org1_user1_token, null, null, "origin" },
				{ "suborg", ti.normal_msp_org2_id, ti.normal_msp_org2_user1_token, ti.normal_msp_org2_user1_id,
						"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup_full",
						System.currentTimeMillis(), 0, "filterName", "true", msp_cloud_id,
						ti.normal_msp_org2_user1_token, null, null, "origin" },

				{ "root_sub", ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,
						"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup_full",
						System.currentTimeMillis(), 0, "filterName", "true", root_cloud_id,
						ti.root_msp1_suborg1_user1_token, null, null, "origin" },
				{ "submsp_sub", ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_token,
						ti.msp1_submsp1_suborg1_user1_id, "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup_full", System.currentTimeMillis(), 0, "filterName",
						"true", root_cloud_id, ti.msp1_submsp1_suborg1_user1_token, null, null, "origin" },

		};
	}

	@Test(dataProvider = "create_job_filter_valid_ex")
	public void getJobFilterValid_ex(String organizationType, String organization_id, String validToken, String user_id,
			String jobStatus, String policyID, String resourceID, String jobType, long startTimeTS, long endTimeTS,
			String filterName, String isDefault, String site_id, String siteToken, String filterStartTS,
			String filterEndTS, String view_type) {
		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);
		String filterArray[] = new String[10];
		int count = 0;
		String filter_jobStatus = jobStatus;
		String filter_jobType = jobType;
		String filter_source = resourceID;
		String filter_policy = policyID;
		String filter_Name;
		ArrayList<HashMap<String, Object>> expectedresponse = new ArrayList<>();
		HashMap<String, Object> expected_response = new HashMap<String, Object>();

		spogJobServer.setToken(validToken);
		spogServer.setToken(validToken);

		test.log(LogStatus.INFO, "Create a filter with jobType as backup_full");
		filter_Name = RandomStringUtils.random(4) + filterName;
		count = 2;

		spogServer.setToken(validToken);
		String group_id = UUID.randomUUID().toString();
		for (int i = 0; i < count; i++) {
			Response response = spogJobServer.createJobFilterWithCheckResponse(user_id, filter_jobStatus, policyID,
					resourceID, filter_jobType, "last_24_hours", filterEndTS, null, null, filter_Name, isDefault,
					group_id, organization_id, view_type, test);

			filterArray[i] = response.then().extract().path("data.filter_id").toString();

			test.log(LogStatus.INFO, "Compose the expected job filter");
			expected_response = spogJobServer.composeExpectedJobFilter(filterArray[i], filter_Name, user_id,
					organization_id, filter_jobStatus, filter_jobType, filter_policy, filter_source, filterStartTS,
					filterEndTS, null, null, isDefault, count, group_id, view_type);
			expectedresponse.add(expected_response);

			test.log(LogStatus.INFO, "Delete the job filter by filter ID");
			spogJobServer.deleteJobFilterByFilterID(user_id, filterArray[i], validToken,
					SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		}

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
