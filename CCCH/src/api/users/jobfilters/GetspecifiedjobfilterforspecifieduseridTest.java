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

public class GetspecifiedjobfilterforspecifieduseridTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private SPOGJobServer spogJobServer;

	private ExtentTest test;
	private TestOrgInfo ti;

	String rps_id = UUID.randomUUID().toString();
	String destination_id = UUID.randomUUID().toString();
	String Direct_cloud_id;
	String Direct_cloud_id_2;
	String msp_cloud_id;
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
		String author = "Ramya.Nagepalli";

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

		response = spogServer.getCloudAccounts(ti.direct_org2_user1_token, "", test);
		Direct_cloud_id_2 = response.then().extract().path("data[" + 0 + "].cloud_account_id");

		response = spogServer.getCloudAccounts(ti.normal_msp_org2_user1_token, "", test);
		msp_cloud_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");

		response = spogServer.getCloudAccounts(ti.root_msp_org1_user1_token, "", test);
		root_cloud_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");

	}

	@DataProvider(name = "create_job_filter_valid")
	public final Object[][] createJobFilterValidParams() {
		return new Object[][] {
				{ "direct", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id, "active",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup_full",
						System.currentTimeMillis() / 1000, 0, "filterName", "true", Direct_cloud_id,
						ti.direct_org1_user1_token, null, null, "origin" },
				{ "direct", ti.direct_org2_id, ti.csr_token, ti.direct_org2_user1_id, "active",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup_full",
						System.currentTimeMillis() / 1000, 0, "filterName", "true", Direct_cloud_id_2,
						ti.direct_org2_user1_token, null, null, "origin" },
				{ "suborgcsrtoken", ti.normal_msp1_suborg2_id, ti.csr_token, ti.normal_msp1_suborg2_user1_id, "active",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup_full",
						System.currentTimeMillis() / 1000, 0, "filterName", "true", msp_cloud_id,
						ti.normal_msp1_suborg2_user1_token, null, null, "origin" },
				{ "suborgmsptoken", ti.normal_msp1_suborg1_id, ti.normal_msp_org1_user1_token,
						ti.normal_msp1_suborg1_user1_id, "active", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup_full", System.currentTimeMillis() / 1000, 0,
						"filterName1", "true", msp_cloud_id, ti.normal_msp1_suborg1_user1_token, null, null, "origin" },
				{ "suborg_msp_account", ti.normal_msp1_suborg1_id, ti.normal_msp_org1_msp_accountadmin1_token,
						ti.normal_msp1_suborg1_user1_id, "active", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup_full", System.currentTimeMillis() / 1000, 0,
						"filterName2", "true", msp_cloud_id, ti.normal_msp1_suborg1_user1_token, null, null, "origin" },
				{ "suborg", ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token,
						ti.normal_msp1_suborg1_user1_id, "active", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup_full", System.currentTimeMillis() / 1000, 0,
						"filterName3", "true", msp_cloud_id, ti.normal_msp1_suborg1_user1_token, null, null, "origin" },

		};
	}

	@Test(dataProvider = "create_job_filter_valid")
	public void getJobFilterValid_200(String organizationType, String organization_id, String validToken,
			String user_id, String jobStatus, String policyID, String resourceID, String jobType, long startTimeTS,
			long endTimeTS, String filterName, String isDefault, String site_id, String siteToken, String filterStartTS,
			String filterEndTS, String view_type)

	{
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

		spogServer.setToken(validToken);
		spogJobServer.setToken(validToken);
		count = 0;
		String group_id = UUID.randomUUID().toString();

		for (int i = 0; i < 2; i++) {

			filter_Name = RandomStringUtils.random(4) + filterName;

			Response response = spogJobServer.createJobFilterWithCheckResponse(user_id, filter_jobStatus, policyID,
					resourceID, filter_jobType, "last_24_hours", filterEndTS, null, null, filter_Name, isDefault,
					group_id, organization_id, view_type, test);

			filterArray[i] = response.then().extract().path("data.filter_id").toString();

			test.log(LogStatus.INFO, "Compose the expected job filter");
			expected_response = spogJobServer.composeExpectedJobFilter(filterArray[i], filter_Name, user_id,
					organization_id, filter_jobStatus, filter_jobType, filter_policy, filter_source, filterStartTS,
					filterEndTS, null, null, isDefault, i, group_id, view_type);
			expectedresponse.add(expected_response);

			spogJobServer.setToken(validToken);
			test.log(LogStatus.INFO, "Get specified job filter for specified user");
			spogJobServer.getspecifiedJobFilterByUserIDwithCheck(user_id, filterArray[i], validToken, expected_response,
					SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

			// get specified user job filter by user id with csr read only valid
			// token
			test.log(LogStatus.INFO, "get specified user job filter by user id with csr read only valid token");
			spogJobServer.getspecifiedJobFilterByUserIDwithCheck(user_id, filterArray[i], ti.csr_readonly_token,
					expected_response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

			test.log(LogStatus.INFO, "Delete the job filter by filter ID");
			spogJobServer.deleteJobFilterByFilterID(user_id, filterArray[i], validToken,
					SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		}
	}

	// Get job specified filter for the specific user from multiple filters

	@Test(dataProvider = "create_job_filter_valid")
	public void getJobFilterValid_200_multipleFilters(String organizationType, String organization_id,
			String validToken, String user_id, String jobStatus, String policyID, String resourceID, String jobType,
			long startTimeTS, long endTimeTS, String filterName, String isDefault, String site_id, String siteToken,
			String filterStartTS, String filterEndTS, String view_type) {

		String filtersArray[] = new String[10];
		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);
		ArrayList<HashMap<String, Object>> expectedresponse = new ArrayList<>();
		HashMap<String, Object> expected_response = new HashMap<String, Object>();
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		spogJobServer.setToken(validToken);
		int count = 0;

		for (int i = 0; i < 2; i++) {

			filtersArray[i] = spogJobServer.createJobFilterWithCheckEx(user_id, jobStatus, policyID, resourceID,
					jobType, "last_24_hours", filterEndTS, null, null, prefix + filterName + i, isDefault, test);

			test.log(LogStatus.INFO, "Compose the expected job filter");
			expected_response = spogJobServer.composeExpectedJobFilter(filtersArray[i], prefix + filterName + i,
					user_id, organization_id, jobStatus, jobType, policyID, resourceID, filterStartTS, filterEndTS,
					null, null, isDefault, i);
			expectedresponse.add(expected_response);

			test.log(LogStatus.INFO, "List all the job filter Ids for a specified user id" + filtersArray[i]);

			test.log(LogStatus.INFO, "Get specified job filter for specified user");
			spogJobServer.getspecifiedJobFilterByUserIDwithCheck(user_id, filtersArray[i], validToken,
					expected_response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

			// get specified job filter by user id with csr read only valid
			// token

			test.log(LogStatus.INFO, "get specified job filter by user id with csr read only valid token");
			spogJobServer.getspecifiedJobFilterByUserIDwithCheck(user_id, filtersArray[i], ti.csr_readonly_token,
					expected_response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

			test.log(LogStatus.INFO, "Delete the job filter by filter ID");
			spogJobServer.deleteJobFilterByFilterID(user_id, filtersArray[i], validToken,
					SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		}

	}

	@DataProvider(name = "create_job_filter_401")
	public final Object[][] createJobFilterValidParams_401() {
		return new Object[][] {

				{ "direct", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id, "active,finished",
						null, null, null, System.currentTimeMillis(), System.currentTimeMillis(), null, null,
						"filterName", "true" },
				{ "msp", ti.normal_msp_org1_id, ti.normal_msp_org2_user1_token, ti.normal_msp_org1_user1_id, "finished",
						null, null, null, System.currentTimeMillis(), System.currentTimeMillis(), null, null,
						"filterName", "true" },
				{ "suborg", ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token,
						ti.normal_msp1_suborg1_user1_id, "finished", null, null, null, System.currentTimeMillis(),
						System.currentTimeMillis(), null, null, "filterName", "true" },

		};
	}

	// Get job specified filter for the specific user with invalid token, null &
	// empty token

	@Test(dataProvider = "create_job_filter_401")
	public void getJobFilterValid_401(String organizationType, String organization_id, String validToken,
			String user_id, String jobStatus, String policyID, String resourceID, String jobType, long startTime,
			long endTime, String endTimeTSStart, String endTimeTSEnd, String filterName, String isDefault) {
		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		test.log(LogStatus.INFO, "Get specified job filter for specified user with other invalid token");
		String filter_Id = UUID.randomUUID().toString();
		spogJobServer.getspecifiedJobFilterByUserIDwithCheck(user_id, filter_Id, validToken + "123", new HashMap<>(),
				SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);

		test.log(LogStatus.INFO, "Get specified job filter for specified user with other null token");
		spogJobServer.getspecifiedJobFilterByUserIDwithCheck(user_id, filter_Id, null, new HashMap<>(),
				SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);

		test.log(LogStatus.INFO, "Get specified job filter for specified user with other user missed token");
		spogJobServer.getspecifiedJobFilterByUserIDwithCheck(user_id, filter_Id, "", new HashMap<>(),
				SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);

	}

	// Get job specified filter for the specific user with invalid token, null &
	// empty token

	@DataProvider(name = "create_job_filter_403")
	public final Object[][] createJobFilterByID_403() {
		return new Object[][] {
				{ "direct", "direct_msp", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id,
						ti.normal_msp_org2_user1_token, ti.normal_msp_org2_user1_id, "active,finished", null, null,
						null, System.currentTimeMillis(), System.currentTimeMillis(), null, null, "filterName",
						"true" },
				{ "direct", "direct_mspsuborg", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id,
						ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id, "active,finished", null,
						null, null, System.currentTimeMillis(), System.currentTimeMillis(), null, null, "filterName",
						"true" },
				{ "direct", "direct_directb", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id,
						ti.direct_org2_user1_token, ti.direct_org2_user1_id, "active,finished", null, null, null,
						System.currentTimeMillis(), System.currentTimeMillis(), null, null, "filterName", "true" },
				{ "msp", "msp-direct", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token,
						ti.normal_msp_org1_user1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id,
						"active,finished", null, null, null, System.currentTimeMillis(), System.currentTimeMillis(),
						null, null, "filterName", "true" },
				{ "msp", "msp-mspsuborg", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token,
						ti.normal_msp_org1_user1_id, ti.normal_msp1_suborg1_user1_token,
						ti.normal_msp1_suborg1_user1_id, "active,finished", null, null, null,
						System.currentTimeMillis(), System.currentTimeMillis(), null, null, "filterName", "true" },
				{ "mspb", "msp-mspb", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token,
						ti.normal_msp_org1_user1_id, ti.normal_msp_org2_user1_token, ti.normal_msp_org2_user1_id,
						"active,finished", null, null, null, System.currentTimeMillis(), System.currentTimeMillis(),
						null, null, "filterName", "true" },
				{ "suborg", "suborg-direct", ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token,
						ti.normal_msp1_suborg1_user1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id,
						"active,finished", null, null, null, System.currentTimeMillis(), System.currentTimeMillis(),
						null, null, "filterName", "true" },
				{ "suborg", "suborg-mspb", ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token,
						ti.normal_msp1_suborg1_user1_id, ti.normal_msp_org2_user1_token, ti.normal_msp_org2_user1_id,
						"active,finished", null, null, null, System.currentTimeMillis(), System.currentTimeMillis(),
						null, null, "filterName", "true" },
				{ "suborg", "suborg-suborgb", ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token,
						ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg2_user1_token,
						ti.normal_msp1_suborg2_user1_id, "active,finished", null, null, null,
						System.currentTimeMillis(), System.currentTimeMillis(), null, null, "filterName", "true" },
				{ "csr", "csr-msp", ti.csr_org_id, ti.csr_token, ti.csr_admin_user_id, ti.normal_msp_org2_user1_token,
						ti.normal_msp_org2_user1_id, "active,finished", null, null, null, System.currentTimeMillis(),
						System.currentTimeMillis(), null, null, "filterName", "true" },
				{ "suborg", "suborg-direct", ti.normal_msp1_suborg1_id, ti.normal_msp_org1_msp_accountadmin1_token,
						ti.normal_msp1_suborg1_user1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id,
						"active,finished", null, null, null, System.currentTimeMillis(), System.currentTimeMillis(),
						null, null, "filterName", "true" },

				// csr read only cases
				{ "csrreadonly", "csr-msp", ti.csr_org_id, ti.csr_readonly_token, ti.csr_readonly_admin_user_id,
						ti.normal_msp_org2_user1_token, ti.normal_msp_org2_user1_id, "active,finished", null, null,
						null, System.currentTimeMillis(), System.currentTimeMillis(), null, null, "filterName",
						"true" },
				{ "csrreadonly", "csr-direct", ti.csr_org_id, ti.csr_readonly_token, ti.csr_readonly_admin_user_id,
						ti.direct_org1_user1_token, ti.direct_org1_user1_id, "active,finished", null, null, null,
						System.currentTimeMillis(), System.currentTimeMillis(), null, null, "filterName", "true" },
				{ "csrreadonly", "csr-suborg", ti.csr_org_id, ti.csr_readonly_token, ti.csr_readonly_admin_user_id,
						ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id, "active,finished", null,
						null, null, System.currentTimeMillis(), System.currentTimeMillis(), null, null, "filterName",
						"true" },

				// 3 tier cases

				{ "rootsuborg", "rootsuborg-submsp", ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_token,
						ti.root_msp1_suborg1_user1_id, ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,
						"active,finished", null, null, null, System.currentTimeMillis(), System.currentTimeMillis(),
						null, null, "filterName", "true" },

				{ "rootsuborg", "rootsuborg-submsp_suborg", ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_token,
						ti.root_msp1_suborg1_user1_id, ti.msp1_submsp1_suborg1_user1_token,
						ti.msp1_submsp1_suborg1_user1_id, "active,finished", null, null, null,
						System.currentTimeMillis(), System.currentTimeMillis(), null, null, "filterName", "true" },

				{ "rootmsp", "rootmsp-submsp", ti.root_msp_org1_id, ti.root_msp_org1_user1_token,
						ti.root_msp_org1_user1_id, ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,
						"active,finished", null, null, null, System.currentTimeMillis(), System.currentTimeMillis(),
						null, null, "filterName", "true" },

				{ "rootmsp", "rootmsp-submsp_sub", ti.root_msp_org1_id, ti.root_msp_org1_user1_token,
						ti.root_msp_org1_user1_id, ti.msp1_submsp1_suborg1_user1_token,
						ti.msp1_submsp1_suborg1_user1_id, "active,finished", null, null, null,
						System.currentTimeMillis(), System.currentTimeMillis(), null, null, "filterName", "true" },

				{ "rootmsp", "rootmsp-suborg", ti.root_msp_org1_id, ti.root_msp_org1_user1_token,
						ti.root_msp_org1_user1_id, ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,
						"active,finished", null, null, null, System.currentTimeMillis(), System.currentTimeMillis(),
						null, null, "filterName", "true" },

				{ "rootsuborg", "submsp-rootsuborg", ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_user1_token,
						ti.root_msp1_submsp1_user1_id, ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,
						"active,finished", null, null, null, System.currentTimeMillis(), System.currentTimeMillis(),
						null, null, "filterName", "true" },

		};
	}


	@DataProvider(name = "create_job_filter_404")
	public final Object[][] createJobFilterByID_404() {
		return new Object[][] {
				{ "direct_directb", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id,
						ti.direct_org2_user1_token, ti.direct_org2_user1_id, "active,finished", null, null, null,
						System.currentTimeMillis(), System.currentTimeMillis(), null, null, "filterName", "true" },
				{ "msp-mspborg", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,
						ti.normal_msp_org2_user1_token, ti.normal_msp_org2_user1_id, "active,finished", null, null,
						null, System.currentTimeMillis(), System.currentTimeMillis(), null, null, "filterName",
						"true" },
				{ "suborg-mspb", ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token,
						ti.normal_msp1_suborg1_user1_id, ti.normal_msp_org2_user1_token, ti.normal_msp_org2_user1_id,
						"active,finished", null, null, null, System.currentTimeMillis(), System.currentTimeMillis(),
						null, null, "filterName", "true" },
				{ "msp-mspsuborg_b", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,
						ti.normal_msp1_suborg2_user1_token, ti.normal_msp1_suborg2_user1_id, "active,finished", null,
						null, null, System.currentTimeMillis(), System.currentTimeMillis(), null, null, "filterName",
						"true" },
				{ "suborg-suborg_b", ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token,
						ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg2_user1_token,
						ti.normal_msp1_suborg2_user1_id, "active,finished", null, null, null,
						System.currentTimeMillis(), System.currentTimeMillis(), null, null, "filterName", "true" },
				{ "direct_msporg", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id,
						ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id, "active,finished", null, null,
						null, System.currentTimeMillis(), System.currentTimeMillis(), null, null, "filterName",
						"true" },
				{ "direct_mspsuborg", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id,
						ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id, "active,finished", null,
						null, null, System.currentTimeMillis(), System.currentTimeMillis(), null, null, "filterName",
						"true" },
				{ "csrreadonly-direct", ti.csr_org_id, ti.csr_readonly_token, ti.csr_readonly_admin_user_id,
						ti.direct_org1_user1_token, ti.direct_org1_user1_id, "active,finished", null, null, null,
						System.currentTimeMillis(), System.currentTimeMillis(), null, null, "filterName", "true" },
				{ "csrreadonly-msp", ti.csr_org_id, ti.csr_readonly_token, ti.csr_readonly_admin_user_id,
						ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id, "active,finished", null, null,
						null, System.currentTimeMillis(), System.currentTimeMillis(), null, null, "filterName",
						"true" },
				{ "csrreadonly-suborg", ti.csr_org_id, ti.csr_readonly_token, ti.csr_readonly_admin_user_id,
						ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id, "active,finished", null,
						null, null, System.currentTimeMillis(), System.currentTimeMillis(), null, null, "filterName",
						"true" },

		};
	}

	// Get job specified filter for the specific user with other organization
	// User id

	@Test(dataProvider = "create_job_filter_404")
	public void getJobFilterByID_404(String organizationType, String organization_id, String validToken, String user_id,
			String otherToken, String otherUserID, String jobStatus, String policyID, String resourceID, String jobType,
			long startTime, long endTime, String endTimeTSStart, String endTimeTSEnd, String filterName,
			String isDefault) {
		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);
		ArrayList<HashMap<String, Object>> expectedresponse = new ArrayList<>();
		HashMap<String, Object> expected_response = new HashMap<String, Object>();
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		int count = 0;

		spogJobServer.setToken(validToken);
		String filter_Id = UUID.randomUUID().toString();

		// trying to get job filter of other organization
		test.log(LogStatus.INFO, "Get specified job filter for specified user of other organization");
		spogJobServer.getspecifiedJobFilterByUserIDwithCheck(user_id, filter_Id, otherToken, expected_response,
				SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.FILTER_NOT_FOUND_WITH_USER_ID, test);


	}

	// getjobfilterbyfilterId_404 [random user_id,random filter_id,deleted
	// filter_id & deleted user_id]

	@DataProvider(name = "create_job_filter_valid_404_id")
	public final Object[][] createJobFilterValidParams_404_id() {
		return new Object[][] {
				// different users
				{ "direct", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id, "active,finished",
						null, null, null, System.currentTimeMillis(), System.currentTimeMillis(), null, null,
						"filterName", "true" },
				{ "direct", ti.direct_org2_id, ti.csr_token, ti.direct_org2_user1_id, "active,finished", null, null,
						null, System.currentTimeMillis(), System.currentTimeMillis(), null, null, "filterName",
						"true" },
				{ "suborg", ti.normal_msp1_suborg2_id, ti.csr_token, ti.normal_msp1_suborg2_user1_id, "finished", null,
						null, null, System.currentTimeMillis(), System.currentTimeMillis(), null, null, "filterName",
						"true" },
				{ "suborgmsptoken", ti.normal_msp1_suborg1_id, ti.normal_msp_org1_user1_token,
						ti.normal_msp1_suborg1_user1_id, "finished", null, null, null, System.currentTimeMillis(),
						System.currentTimeMillis(), null, null, "filterName", "true" },
				{ "msp", ti.normal_msp_org2_id, ti.csr_token, ti.normal_msp_org2_user1_id, "finished", null, null, null,
						System.currentTimeMillis(), System.currentTimeMillis(), null, null, "filterName", "true" },
				{ "msp", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id, "finished",
						null, null, null, System.currentTimeMillis(), System.currentTimeMillis(), null, null,
						"filterName", "true" },
				{ "suborg", ti.normal_msp1_suborg1_id, ti.normal_msp_org1_msp_accountadmin1_token,
						ti.normal_msp1_suborg1_user1_id, "finished", null, null, null, System.currentTimeMillis(),
						System.currentTimeMillis(), null, null, "filterName", "true" },

		};
	}

	@Test(dataProvider = "create_job_filter_valid_404_id")
	public void getJobFilterValid_404_id(String organizationType, String organization_id, String validToken,
			String user_id, String jobStatus, String policyID, String resourceID, String jobType, long startTime,
			long endTime, String endTimeTSStart, String endTimeTSEnd, String filterName, String isDefault) {
		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);
		ArrayList<HashMap<String, Object>> expectedresponse = new ArrayList<>();
		HashMap<String, Object> expected_response = new HashMap<String, Object>();
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		int count = 0;

		spogJobServer.setToken(validToken);
		String filter_Id = spogJobServer.createJobFilterWithCheckEx(user_id, jobStatus, policyID, resourceID, jobType,
				"last_24_hours", null, null, null, prefix + filterName, isDefault, test);

		// Get specified job filter for specified user by passing random job
		// filter id

		test.log(LogStatus.INFO, "Get specified job filter for specified user by passing random job filterid");
		spogJobServer.getspecifiedJobFilterByUserIDwithCheck(user_id, UUID.randomUUID().toString(), validToken,
				expected_response, SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.FILTER_NOT_FOUND_WITH_USER_ID,
				test);
	
		// Delete the job filter by filter ID
		test.log(LogStatus.INFO, "Delete the job filter by filter ID");
		spogJobServer.deleteJobFilterByFilterID(user_id, filter_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE,
				null, test);

		// Get specified job filter for specified user by passing deleted job
		// filter id
		test.log(LogStatus.INFO, "Get specified job filter for specified user by passing deleted job filterid");
		spogJobServer.getspecifiedJobFilterByUserIDwithCheck(user_id, filter_Id, validToken, expected_response,
				SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.FILTER_NOT_FOUND_WITH_USER_ID, test);

	
	}
	
	@Test(dataProvider = "create_job_filter_403")
	public void getJobFilterByID_403(String organization, String organizationType, String organization_id,
			String validToken, String user_id, String otherToken,String otherUserId, String jobStatus, String policyID, String resourceID,
			String jobType, long startTime, long endTime, String endTimeTSStart, String endTimeTSEnd, String filterName,
			String isDefault) {
		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		int count = 0;

		spogJobServer.setToken(validToken);
		String filter_Id = spogJobServer.createJobFilterWithCheckEx(user_id, jobStatus, policyID, resourceID, jobType,
				"last_24_hours", null, null, null, prefix + filterName, isDefault, test);

		test.log(LogStatus.INFO, "Get specified job filter for specified user with other user token");
		spogJobServer.getspecifiedJobFilterByUserIDwithCheck(otherUserId, filter_Id, otherToken, new HashMap<>(),
				SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY, test);
		
		test.log(LogStatus.INFO, "Delete the job filter by filter ID");
		spogJobServer.deleteJobFilterByFilterID(user_id, filter_Id, validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

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
