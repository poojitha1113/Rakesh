package api.organizations.alerts;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGAlertsServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class GenerateOrganizationCapacityAlertTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private SPOGAlertsServer spogAlertsServer;
	private Policy4SPOGServer policy4SpogServer;

	private ExtentTest test;
	private TestOrgInfo ti;

	String[] type = new String[12];
	private String job_Type = "restore,restore";
	private String job_Method = "full,incremental,resync,full,incremental,resync,full,incremental,resync,full,incremental,resync";
	public String JobSeverity = "critical,information,critical,warning,information,critical,warning,information,critical,warning,information,critical";
	private String test_log_Message_1 = "testLogMessage", test_log_Message_2 = "connect_node_failed_test_message";
	public String JobStatus = "failed,failed,failed,incomplete,idle,waiting,crash,license_failed,backupjob_proc_exit,skipped,stop,active,finished";

	private String Direct_cloud_id;
	private String msp_cloud_account_id;
	private String root_cloud_id;
	private GatewayServer gatewayServer;

	@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runningMachine, String buildVersion) {

		spogServer = new SPOGServer(baseURI, port);
		spogAlertsServer = new SPOGAlertsServer(baseURI, port);
		policy4SpogServer = new Policy4SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);

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

		// Alert Types
		type[0] = "backup_missed";
		type[1] = "backup_failed";
		type[2] = "policy_errors";
		type[3] = "policy_warnings";
		type[4] = "policy_success";
		type[5] = "backup_errors";
		type[6] = "backup_warnings";
		type[7] = "backup_success";
		type[8] = "recovery_failed";
		type[9] = "recovery_errors";
		type[10] = "recovery_warnings";
		type[11] = "recovery_success";

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
	@DataProvider(name = "generateOrganizationCapacityAlert")
	public final Object[][] generateOrganizationCapacityAlert() {
		return new Object[][] { { "Direct", ti.csr_token, ti.direct_org1_id },
				{ "msp", ti.csr_token, ti.normal_msp_org1_id }, { "suborg", ti.csr_token, ti.normal_msp1_suborg1_id },
				{ "rootmsp", ti.csr_token, ti.root_msp_org1_id }, { "rootsub", ti.csr_token, ti.root_msp1_suborg1_id },
				{ "submsp", ti.csr_token, ti.root_msp1_submsp_org1_id },
				{ "submsp_sub", ti.csr_token, ti.msp1_submsp1_sub_org1_id }, };
	}

	@Test(dataProvider = "generateOrganizationCapacityAlert", dependsOnMethods = "deleteResources")
	public void generateOrganizationCapacityAlert_valid(String OrganizationType, String Token, String Organization_id) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + OrganizationType);

		test.log(LogStatus.INFO, "Generate Capacity alert for Organization with csr token");
		spogAlertsServer.generateOrganizationCapacityAlert(Token, Organization_id, SpogConstants.SUCCESS_GET_PUT_DELETE,
				SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

	}

	@DataProvider(name = "generateOrganizationCapacityAlert_invalid")
	public final Object[][] generateOrganizationCapacityAlert_invalid() {
		return new Object[][] {
				{ "Invalid token(missed)", "Direct", "", ti.direct_org1_id, SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTHENTICATION_FAILED },
				{ "Invalid token(any string)", "Direct", "abc", ti.direct_org1_id, SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT },
				{ "direct organization token other than csr", "Direct", ti.direct_org1_user1_token, ti.direct_org1_id,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.FORBIDDEN_TO_VISIT_THE_RESOURCE },
				{ "msp organization token other than csr", "MSP", ti.normal_msp_org1_user1_token, ti.normal_msp_org1_id,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.FORBIDDEN_TO_VISIT_THE_RESOURCE },
				{ "suborg organization token other than csr", "SUBORG", ti.normal_msp1_suborg1_user1_token,
						ti.normal_msp1_suborg1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.FORBIDDEN_TO_VISIT_THE_RESOURCE },
				{ "Insufficient permissions csrreadonly-Direct", "Direct-csrreadonly", ti.direct_org1_user1_token,
						ti.csr_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.FORBIDDEN_TO_VISIT_THE_RESOURCE },
				{ "Insufficient permissions csrreadonly-msp", "MSP-csrreadonly", ti.normal_msp_org1_user1_token,
						ti.csr_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.FORBIDDEN_TO_VISIT_THE_RESOURCE },
				{ "Insufficient permissions csrreadonly-suborg", "SUBORG-csrreadonly",
						ti.normal_msp1_suborg1_user1_token, ti.csr_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.FORBIDDEN_TO_VISIT_THE_RESOURCE },
				{ "Insufficient permissions Direct-csrreadonly", "Direct-csrreadonly", ti.csr_readonly_token,
						ti.direct_org1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Insufficient permissions MSP-csrreadonly", "MSP-csrreadonly", ti.csr_readonly_token,
						ti.normal_msp_org1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Insufficient permissions SUBORG-csrreadonly", "SUBORG-csrreadonly", ti.csr_readonly_token,
						ti.normal_msp1_suborg1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },

				// 3 tier cases
				{ "root msp organization token other than csr", "MSP", ti.root_msp_org1_user1_token, ti.csr_org_id,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.FORBIDDEN_TO_VISIT_THE_RESOURCE },
				{ "root suborg organization token other than csr", "SUBORG", ti.root_msp1_suborg1_user1_token,
						ti.csr_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.FORBIDDEN_TO_VISIT_THE_RESOURCE },
				{ "submsp suborg organization token other than csr", "SUBORG", ti.msp1_submsp1_suborg1_user1_token,
						ti.csr_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.FORBIDDEN_TO_VISIT_THE_RESOURCE },
				{ "submsp organization token other than csr", "SUBORG", ti.root_msp1_submsp1_user1_token, ti.csr_org_id,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.FORBIDDEN_TO_VISIT_THE_RESOURCE },
				// monitor cases
				{ "Insufficient permissions Direct-monitor", "Direct-monitor", ti.direct_org1_monitor_user1_token,
						ti.direct_org1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Insufficient permissions MSP-monitor", "MSP-monitor", ti.normal_msp_org1_monitor_user1_token,
						ti.normal_msp_org1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Insufficient permissions SUBORG-monitor", "SUBORG-monitor",
						ti.normal_msp1_suborg1_monitor_user1_token, ti.normal_msp1_suborg1_id,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Insufficient permissions root MSP-monitor", "MSP-monitor", ti.root_msp_org1_monitor_user1_token,
						ti.root_msp_org1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Insufficient permissions root SUBORG-monitor", "SUBORG-monitor",
						ti.root_msp1_suborg1_monitor_user1_token, ti.root_msp1_suborg1_id,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Insufficient permissions root submsp-monitor", "submsp-monitor",
						ti.root_msp1_submsp1_monitor_user1_token, ti.root_msp1_submsp_org1_id,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Insufficient permissions root submsp_sub-monitor", "submsp_sub-monitor",
						ti.msp1_submsp1_suborg1_monitor_user1_token, ti.msp1_submsp1_sub_org1_id,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },

		};
	}

	@Test(dataProvider = "generateOrganizationCapacityAlert_invalid", dependsOnMethods = "deleteResources")
	public void generateOrganizationCapacityAlert_invalid(String testcase, String organizationType, String token,
			String Organization_id, int expectedStatusCode, SpogMessageCode expectedErrorMessage) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + organizationType);
		test.log(LogStatus.INFO, "Generate Capacity alert for Organization with invalid scenario" + testcase);
		spogAlertsServer.generateOrganizationCapacityAlert(token, Organization_id, expectedStatusCode,
				expectedErrorMessage, test);
	}

	@DataProvider(name = "org_info")
	public final Object[][] org_info() {
		return new Object[][] { { ti.direct_org1_id, ti.direct_org1_user1_token },
				{ ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token },
				{ ti.root_msp_org1_id, ti.root_msp_org1_user1_token },
				{ ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_token },
				{ ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_user1_token },
				{ ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_token }, };
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
	public void afterMethodTest() {
		rep.endTest(test);
		rep.flush();
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
