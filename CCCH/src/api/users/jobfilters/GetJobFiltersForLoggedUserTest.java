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

public class GetJobFiltersForLoggedUserTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private SPOGJobServer spogJobServer;
	private UserSpogServer userSpogServer;
	private SPOGDestinationServer spogDestinationServer;

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
		userSpogServer = new UserSpogServer(baseURI, port);
		rep = ExtentManager.getInstance("GetJobFiltersForLoggedUserTest", logFolder);
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
				{ "direct", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id, "finished",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup_full",
						System.currentTimeMillis(), 0, "filterName1", "false", Direct_cloud_id,
						ti.direct_org1_user1_token, null, null, "origin" },

				{ "suborg", ti.normal_msp_org2_id, ti.normal_msp_org2_user1_token, ti.normal_msp_org2_user1_id,
						"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup_full",
						System.currentTimeMillis(), 0, "filterName2", "false", msp_cloud_id,
						ti.normal_msp_org2_user1_token, null, null, "origin" },

				{ "root_sub", ti.root_msp_org1_id, ti.root_msp_org1_user1_token, ti.root_msp_org1_user1_id, "finished",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup_full",
						System.currentTimeMillis(), 0, "filterName3", "false", root_cloud_id,
						ti.root_msp_org1_user1_token, null, null, "origin" },

				{ "submsp_sub", ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_token,
						ti.msp1_submsp1_suborg1_user1_id, "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup_full", System.currentTimeMillis(), 0, "filterName4",
						"false", root_cloud_id, ti.msp1_submsp1_suborg1_user1_token, null, null, "origin" },

		};
	}

	@Test(dataProvider = "create_job_filter_valid")
	public void getJobFilterValid_200(String organizationType, String organization_id, String validToken,
			String user_id, String jobStatus, String policyID, String resourceID, String jobType, long startTimeTS,
			long endTimeTS, String filterName, String isDefault, String site_id, String siteToken, String filterStartTS,
			String filterEndTS, String view_type) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);
		String filterArray[] = new String[10];
		String filtername[] = new String[10];
		int count = 0;
		String filter_jobStatus = jobStatus;
		String filter_source = resourceID;
		String filter_policy = policyID;
		ArrayList<HashMap<String, Object>> expectedresponse = new ArrayList<>();
		HashMap<String, Object> expected_response = new HashMap<String, Object>();
		spogServer.setToken(validToken);
		spogJobServer.setToken(validToken);
		String prefix = RandomStringUtils.randomAlphanumeric(4);

		// create group for the node and assign the source to the group
		String group_id = spogServer.createGroupWithCheck(organization_id, prefix + "group_name", prefix + "group_name",
				test);
		for (int i = 0; i < 2; i++) {
			spogServer.setToken(validToken);
			prefix = RandomStringUtils.randomAlphanumeric(4);
			filterName = prefix + filterName;
			Response response = spogJobServer.createJobFilterWithCheckResponse(user_id, filter_jobStatus, policyID,
					resourceID, jobType, "last_24_hours", filterEndTS, null, null, filterName, isDefault, group_id,
					organization_id, view_type, test);
			filterArray[i] = response.then().extract().path("data.filter_id").toString();

			test.log(LogStatus.INFO, "Compose the expected job filter");
			expected_response = spogJobServer.composeExpectedJobFilter(filterArray[i], filterName, user_id,
					organization_id, filter_jobStatus, jobType, filter_policy, filter_source, filterStartTS,
					filterEndTS, null, null, isDefault, i, group_id, view_type);
			expectedresponse.add(expected_response);
		}

		test.log(LogStatus.INFO, "Get job filters for logged in user");
		spogJobServer.getJobFiltersForLoggedInUser(expectedresponse, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		test.log(LogStatus.INFO, "List all the job filter Ids for a Logged in user whose is_default value is True");
		String additionalURL = spogServer.PrepareURL("is_default;=;true", "", 1, 20, test);

		spogJobServer.getJobFiltersForLoggedInUser_isdefault(additionalURL, expectedresponse,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
/*
		test.log(LogStatus.INFO, "List all the job filter Ids for a Logged in user whose is_default value is False");

		additionalURL = spogServer.PrepareURL("is_default;=;false", "", 1, 20, test);

		spogJobServer.getJobFiltersForLoggedInUser_isdefault(additionalURL, expectedresponse,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
*/
		// applying filtering on filter_name
		additionalURL = spogServer.PrepareURL("filter_name;=;" + filterName, "", 1, 20, test);
		spogJobServer.getJobFiltersForLoggedInUser_isdefault(additionalURL, expectedresponse,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		// applying filtering on is_default true and filter_name
		additionalURL = spogServer.PrepareURL("is_default;=;false,filter_name;=;" + filtername[2], "", 1, 20, test);
		spogJobServer.getJobFiltersForLoggedInUser_isdefault(additionalURL, expectedresponse,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		// get job filter for logged in user using csr read only token
		spogJobServer.setToken(ti.csr_readonly_token);

		additionalURL = "organization=" + organization_id;
		test.log(LogStatus.INFO, "get job filter for logged in user using csr read only token");
		spogJobServer.getJobFiltersForLoggedInUser_isdefault(additionalURL, expectedresponse,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		test.log(LogStatus.INFO, "Delete the job filter by filter ID");

		for (int i = 0; i < filterArray.length; i++) {
			if (filterArray[i] != null)
				spogJobServer.deleteJobFilterByFilterID(user_id, filterArray[i], validToken,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		}

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
