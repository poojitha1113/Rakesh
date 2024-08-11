package api.dashboard;

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

import Constants.DestinationStatus;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import Constants.volume_type;
import InvokerServer.GatewayServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGJobServer;
import InvokerServer.SPOGReportServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class GetDashboardPolicySummaryTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private SPOGJobServer spogJobServer;
	private SPOGDestinationServer spogDestinationServer;
	private SPOGReportServer spogreportServer;
	private Policy4SPOGServer policy4SpogServer;

	private ExtentTest test;
	private TestOrgInfo ti;

	String[] type = new String[12];
	private String job_Type = "restore";
	private String job_Status = "finished";
	private String job_Method = "full,incremental,resync,full,incremental,resync,full,incremental,resync,full,incremental,resync";
	public String JobSeverity = "warning,information,critical,warning,information,critical,warning,information,critical,warning,information,critical";
	private String Direct_cloud_id;
	private String msp_cloud_account_id;
	private String root_cloud_id;

	@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runningMachine, String buildVersion) {

		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogJobServer = new SPOGJobServer(baseURI, port);
		spogreportServer = new SPOGReportServer(baseURI, port);
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
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);

		policy4SpogServer = new Policy4SPOGServer(baseURI, port);
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

		// get cloud account for the direct organization
		Response response = spogServer.getCloudAccounts(ti.direct_org1_user1_token, "", test);
		Direct_cloud_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");

		// get cloud account for the msp organization
		response = spogServer.getCloudAccounts(ti.normal_msp_org1_user1_token, "", test);
		msp_cloud_account_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");

		// get cloud account for the root msp organization
		response = spogServer.getCloudAccounts(ti.root_msp_org1_user1_token, "", test);
		root_cloud_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");

	}

	// This information is related to the destination information of the
	// cloudDedupe volume

	@DataProvider(name = "getDashboardPolicySummary")
	public final Object[][] getDashboardPolicySummary() {
		return new Object[][] {
				{ "Direct", ti.direct_org1_user1_token, ti.direct_org1_id, ti.direct_org1_user1_id, Direct_cloud_id,
						Direct_cloud_id, System.currentTimeMillis() / 1000, System.currentTimeMillis() / 1000,
						ti.direct_org1_user1_token, "1", 30.20, "3", "02", "2", "3", "3", "3" },
				{ "sub", ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_id,
						msp_cloud_account_id, msp_cloud_account_id, System.currentTimeMillis() / 1000,
						System.currentTimeMillis() / 1000, ti.normal_msp1_suborg1_user1_token, "1", 30.20, "3", "02", "2",
						"3", "3", "3" },
				{ "rootsub", ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_id,
						root_cloud_id, root_cloud_id, System.currentTimeMillis() / 1000,
						System.currentTimeMillis() / 1000, ti.root_msp1_suborg1_user1_token, "1", 30.20, "3", "02", "2",
						"3", "3", "3" },
				{ "submsp_sub", ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_sub_org1_id,
						ti.msp1_submsp1_suborg1_user1_id, root_cloud_id, root_cloud_id, System.currentTimeMillis() / 1000,
						System.currentTimeMillis() / 1000, ti.msp1_submsp1_suborg1_user1_token, "1", 30.20, "3", "02", "2",
						"3", "3", "3" },

		};
	}

	@Test(dataProvider = "getDashboardPolicySummary",dependsOnMethods="deleteResources")
	public void getDashboardPolicySummaryOfLoggedinuserTest(String organization_type, String adminToken,
			String organization_id, String create_user_id, String site_id, String cloud_id, Long start_time_ts,
			Long endTimeTS, String site_Token, String job_seq, Double percent_complete, String protected_data_size,
			String raw_data_size, String sync_read_size, String ntfs_volume_size, String virtual_disk_provision_size,
			String dedupe_savings) {

		String job_id = null, log_id = null, source_id = null;
		HashMap<String, Object> expectedPolicyDetails = new HashMap<String, Object>();
		int count = 0, success = 0, failure = 0, deploying = 0, total = 0;

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());

		test.assignAuthor("Ramya.Nagepalli");

		String sourceName = spogServer.ReturnRandom("SPOG_ARCSEVRE");
		test.log(LogStatus.INFO, "create source");
		String destination_id = null;

		spogServer.setToken(adminToken);
		String protectionStatus = "unprotect,unprotect,unprotect,unprotect,unprotect";
		String connectionStatus = "online,offline,online,offline,online";
		String osmajor = "windows,linux,mac,unknown,windows";
		String SourceType = "machine,machine,machine,machine,machine";
		String SourceProduct = "cloud_direct,cloud_direct,cloud_direct,cloud_direct,cloud_direct";

		String[] protection = protectionStatus.split(",");
		String[] connection = connectionStatus.split(",");
		String[] Osmajor = osmajor.split(",");
		String[] sourceType = SourceType.split(",");
		String[] sourceProduct = SourceProduct.split(",");

		ArrayList<String> Policies = new ArrayList<String>();
		ArrayList<String> Policies_status = new ArrayList<String>();

		for (int i = 0; i < 2; i++) {

			String prefix = RandomStringUtils.randomAlphanumeric(8);
			String policy_id = UUID.randomUUID().toString(), policy_status = null;
			String destination_name = prefix + "cloud_direct_hybrid" + i;
			String schedule_id = UUID.randomUUID().toString();
			String task_id = UUID.randomUUID().toString();
			String throttle_id = UUID.randomUUID().toString();
			/* String destination_id=UUID.randomUUID().toString(); */
			String rps_id = UUID.randomUUID().toString();

			Response response = spogDestinationServer.getDestinations(adminToken, "organization_id=" + organization_id,
					test);
			destination_id = response.then().extract().path("data[" + 0 + "].destination_id");

			spogServer.setToken(adminToken);
			source_id = spogServer.createSourceWithCheck(sourceName + i, sourceType[i], sourceProduct[i],
					organization_id, cloud_id, protection[i], connection[i], Osmajor[i], "SQL_SERVER", create_user_id,
					SpogConstants.SUCCESS_POST, null, true, test);

			test.log(LogStatus.INFO, "Create custom settings");
			HashMap<String, Object> customScheduleSettingDTO = policy4SpogServer
					.createCustomScheduleDTO("1522068700422", "full", "1", "true", "10", "minutes", test);

			HashMap<String, Object> scheduleSettingDTO = policy4SpogServer.createScheduleSettingDTO(null,
					customScheduleSettingDTO, null, null, test);

			test.log(LogStatus.INFO, "Create cloud direct schedule");

			HashMap<String, Object> cloudDirectScheduleDTO = policy4SpogServer.createCloudDirectScheduleDTO("0 6 * * *",
					test);

			test.log(LogStatus.INFO, "Create schedule settings");
			scheduleSettingDTO = policy4SpogServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null, null, null,
					test);

			ArrayList<HashMap<String, Object>> schedules = policy4SpogServer.createPolicyScheduleDTO(null, schedule_id,
					"1d", task_id, destination_id, scheduleSettingDTO, "06:00", "12:00",
					"cloud_direct_file_folder_backup", destination_name, test);

			test.log(LogStatus.INFO, "Exclude the folders from cloud direct file backup");

			ArrayList<HashMap<String, Object>> excludes = policy4SpogServer.createExcludeInfoDTO(null, "path",
					"c:\\tmp", test);

			HashMap<String, Object> cloudDirectLocalBackupDTO = policy4SpogServer
					.createCloudDirectLocalBackupDTO("d:\\tmp", "true", test);

			HashMap<String, Object> cloudDirectFileBackupTaskInfoDTO = policy4SpogServer
					.createCloudDirectFileBackupTaskInfoDTO("d:\\tmp", cloudDirectLocalBackupDTO, excludes, test);
			
			ArrayList<HashMap<String, Object>> destinations = policy4SpogServer.createPolicyTaskDTO(null, task_id,
					"cloud_direct_file_folder_backup", destination_id, "none", null, cloudDirectFileBackupTaskInfoDTO,
					null, test);
			ArrayList<HashMap<String, Object>> throttles = policy4SpogServer.createPolicyThrottleDTO(null, throttle_id,
					task_id, "network", "1200", "1", "06:00", "18:00", "cloud_direct_file_folder_backup",
					destination_id, destination_name, test);

			// create cloud direct policy
			policy4SpogServer.setToken(adminToken);
			String policy_name = spogServer.ReturnRandom("ramya");
			response = policy4SpogServer.createPolicy(prefix + policy_name + i, policy_name + i, "cloud_direct_baas",
					null, "true", null, destinations, schedules, throttles, policy_id, organization_id, test);
			policy_id = response.then().extract().path("data.policy_id").toString();
			
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			response = policy4SpogServer.getPolicyById(adminToken, policy_id, test);

			policy_id = response.then().extract().path("data.policy_id").toString();
			policy_status = response.then().extract().path("data.policy_status").toString();

			Policies.add(policy_id);
			Policies_status.add(policy_status);

			if (policy_status.equals("failure") || policy_status.equals("deploying")) {
				failure++;
				count = failure;
			} else if (policy_status.equals("success")) {
				success++;
				count = success;
			}

			total++;

		}
		expectedPolicyDetails.put("total", total );
		expectedPolicyDetails.put("failure", failure);
		expectedPolicyDetails.put("deploying", deploying);
		expectedPolicyDetails.put("success", success );

		spogreportServer.setToken(adminToken);

		String additionalUrl = "";
		test.log(LogStatus.INFO,
				"getDashboardPolicySummaryDetails for the organization" + organization_type + " with valid token");
		Response response = spogreportServer.getDashboardPolicySummaryDetails(adminToken, additionalUrl, test);

		test.log(LogStatus.INFO, "checkTopSources for the organization" + organization_type);
		spogreportServer.checkDashboardPolicySummaryDetails(response, organization_id, additionalUrl,
				expectedPolicyDetails, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		additionalUrl = spogServer.PrepareURL("organization_id;=;" + organization_id, "", 1, 20, test);
		test.log(LogStatus.INFO, "getDashboardPolicySummaryDetails for the organization" + organization_type
				+ " with valid token filter with valid organization id");

		response = spogreportServer.getDashboardPolicySummaryDetails(adminToken, additionalUrl, test);
		spogreportServer.checkDashboardPolicySummaryDetails(response, organization_id, additionalUrl,
				expectedPolicyDetails, SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

		additionalUrl = spogServer.PrepareURL("organization_id;=;" + organization_id, "", 1, 20, test);
		test.log(LogStatus.INFO, "getDashboardPolicySummaryDetails for the organization" + organization_type
				+ " with csr read only token filter with valid organization id");

		response = spogreportServer.getDashboardPolicySummaryDetails(ti.csr_readonly_token, additionalUrl, test);
		spogreportServer.checkDashboardPolicySummaryDetails(response, organization_id, additionalUrl,
				expectedPolicyDetails, SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

		total = 0;
		failure = 0;
		deploying = 0;
		success = 0;

		test.log(LogStatus.INFO, "delete created sources");
		spogServer.setToken(adminToken);
		spogServer.deleteSourceByID(source_id, test);

		for (int i = 0; i < Policies.size(); i++) {
			test.log(LogStatus.INFO,
					"Delete policy by id for the organization" + organization_type + " with valid token");
			policy4SpogServer.deletePolicybyPolicyId(adminToken, Policies.get(i), SpogConstants.SUCCESS_GET_PUT_DELETE,
					null, test);
		}

	}

	@DataProvider(name = "getDashboardPolicySummary_invalid")
	public final Object[][] getDashboardPolicySummary_invalid() {
		return new Object[][] {
				{ "Invalid Authorization with invalid token", "Direct", "abc", ti.direct_org1_id, "",
						SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT },
				{ "Invalid Authorization with Missing token", "Direct", "", ti.direct_org1_id, "",
						SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED },
				{ "InSufficient permissions Direct-Msp Organization", "Direct", ti.direct_org1_user1_token,
						ti.direct_org1_id,
						spogServer.PrepareURL("organization_id;=;" + ti.normal_msp_org1_id, "", 1, 20, test),
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "InSufficient permissions Direct-Suborg Organization", "Direct", ti.direct_org1_user1_token,
						ti.normal_msp1_suborg1_id,
						spogServer.PrepareURL("organization_id;=;" + ti.normal_msp1_suborg1_id, "", 1, 20, test),
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "InSufficient permissions suborg-Suborgb Organization", "suborg", ti.normal_msp1_suborg2_user1_token,
						ti.normal_msp1_suborg1_id,
						spogServer.PrepareURL("organization_id;=;" + ti.normal_msp1_suborg1_id, "", 1, 20, test),
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				/*	{ "InSufficient permissions suborg-Msp Organization", "suborg", ti.normal_msp_org2_user1_token,
						ti.root_msp1_suborg2_id,
						spogServer.PrepareURL("organization_id;=;" + ti.normal_msp_org2_user1_token, "", 1, 20, test),
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY  },
				{ "get policy SummaryTest_invalid for the organization with CSR token, filter with any other(UUID) organization_id",
						"Direct", ti.csr_token, UUID.randomUUID().toString(),
						spogServer.PrepareURL("organization_id;=;" + UUID.randomUUID().toString(), "", 1, 20, test),
						SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL },*/

				// csrreadonly cases
				{ "InSufficient permissions Direct-csrreadonly Organization", "csrreadonly", ti.direct_org1_user1_token,
						ti.direct_org1_id, spogServer.PrepareURL("organization_id;=;" + ti.csr_org_id, "", 1, 20, test),
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "InSufficient permissions msp-csrreadonly Organization", "csrreadonly",
						ti.normal_msp_org1_user1_token, ti.normal_msp_org1_id,
						spogServer.PrepareURL("organization_id;=;" + ti.csr_org_id, "", 1, 20, test),
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "InSufficient permissions suborg-csrreadonly Organization", "csrreadonly",
						ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_id,
						spogServer.PrepareURL("organization_id;=;" + ti.csr_org_id, "", 1, 20, test),
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },

				// 3 tier cases
				{ "InSufficient permissions suborg-rootmsp", "sub_org", ti.root_msp_org2_user1_token,
						ti.normal_msp1_suborg1_id,
						spogServer.PrepareURL("organization_id;=;" + ti.normal_msp1_suborg1_id, "", 1, 20, test),
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "InSufficient permissions rootsub-rootmsp", "rootsub", ti.root_msp_org2_user1_token,
						ti.root_msp1_suborg1_id,
						spogServer.PrepareURL("organization_id;=;" + ti.normal_msp1_suborg1_id, "", 1, 20, test),
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "InSufficient permissions rootsub-submsp_sub", "rootsub", ti.msp1_submsp1_suborg1_user1_token,
						ti.root_msp1_suborg1_id,
						spogServer.PrepareURL("organization_id;=;" + ti.normal_msp1_suborg1_id, "", 1, 20, test),
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "InSufficient permissions rootsub-submsp", "rootsub", ti.root_msp1_submsp1_user1_token,
						ti.root_msp1_suborg1_id,
						spogServer.PrepareURL("organization_id;=;" + ti.normal_msp1_suborg1_id, "", 1, 20, test),
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "InSufficient permissions rootmsp-submsp_sub", "root_msp", ti.msp1_submsp1_suborg1_user1_token,
						ti.root_msp_org1_id,
						spogServer.PrepareURL("organization_id;=;" + ti.root_msp_org1_id, "", 1, 20, test),
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "InSufficient permissions rootmsp-rootsub", "root_msp", ti.root_msp1_suborg1_user1_token,
						ti.root_msp_org1_id,
						spogServer.PrepareURL("organization_id;=;" + ti.root_msp_org1_id, "", 1, 20, test),
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "InSufficient permissions rootmsp-submsp", "root_msp", ti.root_msp1_submsp1_user1_token,
						ti.root_msp_org1_id,
						spogServer.PrepareURL("organization_id;=;" + ti.root_msp_org1_id, "", 1, 20, test),
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },

				{ "InSufficient permissions submsp-submsp_sub", "submsp", ti.msp1_submsp1_suborg1_user1_token,
						ti.root_msp1_submsp_org1_id,
						spogServer.PrepareURL("organization_id;=;" + ti.root_msp1_submsp_org1_id, "", 1, 20, test),
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },

				{ "InSufficient permissions submsp-rootsub", "submsp", ti.root_msp1_suborg1_user1_token,
						ti.root_msp1_submsp_org1_id,
						spogServer.PrepareURL("organization_id;=;" + ti.root_msp1_submsp_org1_id, "", 1, 20, test),
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },

				{ "InSufficient permissions submsp-rootmaa", "submsp", ti.root_msp_org1_msp_accountadmin1_token,
						ti.root_msp1_submsp_org1_id,
						spogServer.PrepareURL("organization_id;=;" + ti.root_msp1_submsp_org1_id, "", 1, 20, test),
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },

		};
	}

	@Test(dataProvider = "getDashboardPolicySummary_invalid",dependsOnMethods="deleteResources")
	public void getDashboardPolicySummaryOfLoggedinuser_invalid(String testcase, String organization_type,
			String adminToken, String organization_id, String additionalUrl, int spogCode,
			SpogMessageCode Error_Message) {

		test = ExtentManager
				.getNewTest(this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName()
						+ testcase + "for Organization" + organization_type);

		test.assignAuthor("Ramya.Nagepalli");
		test.log(LogStatus.INFO, "getDashboardPolicySummaryDetails for the organization" + organization_type
				+ " with invalid case" + testcase);
		Response response = spogreportServer.getDashboardPolicySummaryDetails(adminToken, additionalUrl, test);
		spogreportServer.checkDashboardPolicySummaryDetails(response, organization_id, additionalUrl,
				new HashMap<String, Object>(), spogCode, Error_Message, test);

	}

	private HashMap<String, Object> composeTopPolicies(String policy_name, String stack, int value) {

		HashMap<String, Object> exp = new HashMap<String, Object>();
		exp.put("name", policy_name);
		exp.put("stack", stack);
		exp.put("value", value);

		return exp;
	}

	@DataProvider(name = "org_info")
	public final Object[][] org_info() {
		return new Object[][] {

				{ ti.direct_org1_id, ti.direct_org1_user1_token },
				{ ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token },
				{ ti.root_msp_org1_id, ti.root_msp_org1_user1_token },
				{ ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_token },
				{ ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_user1_token },
				{ ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_token },

		};
	}

	@Test(dataProvider = "org_info")
	public void deleteResources(String org_id, String validToken) {

		policy4SpogServer.setToken(validToken);
		Response response = policy4SpogServer.getPolicies(null);
		ArrayList<String> policies = new ArrayList<>();
		policies = response.then().extract().path("data.policy_id");

		if (!policies.isEmpty()) {
			policies.stream().forEach(policy -> {
				policy4SpogServer.deletePolicybyPolicyId(ti.csr_token, policy);
			});
		}

		spogServer.setToken(validToken);
		response = spogServer.getSources("", "", 1, 20, true, test);

		ArrayList<String> sources = new ArrayList<>();
		sources = response.then().extract().path("data.source_id");
		if (!sources.isEmpty()) {
			sources.stream().forEach(source -> {
				spogServer.deleteSourceByID(source, test);
			});
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
