package api.organizations;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
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

import Constants.DestinationType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class LimitCDDestinationsTest extends base.prepare.Is4Org {

	private SPOGServer spogServer;
	private UserSpogServer userSpogServer;
	private SPOGDestinationServer spogDestinationServer;
	private ExtentTest test;
	private TestOrgInfo ti;

	String[] type = new String[12];
	private String job_Type = "backup,restore";
	private String job_Method = "full,incremental,resync,full,incremental,resync,full,incremental,resync,full,incremental,resync";
	public String JobSeverity = "warning,information,critical,warning,information,critical,warning,information,critical,warning,information,critical";
	private String test_log_Message_1 = "testLogMessage", test_log_Message_2 = "connect_node_failed_test_message";
	public String JobStatus = "failed,failed,failed,incomplete,idle,waiting,crash,license_failed,backupjob_proc_exit,skipped,stop,active,finished";

	private String Direct_cloud_id;
	private String msp_cloud_account_id;
	private String root_cloud_id;

	String[] destinations;
	String[] datacenters;

	private String org_model_prefix = this.getClass().getSimpleName();

	@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runningMachine, String buildVersion) {

		spogServer = new SPOGServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);

		rep = ExtentManager.getInstance("LimitCDDestinationsTest", logFolder);
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

		spogDestinationServer.setToken(ti.csr_token);
		datacenters = spogDestinationServer.getDestionationDatacenterID();

	}

	@DataProvider(name = "UpdateOrganizationCDcount")
	public final Object[][] UpdateOrganizationCDcount() {
		return new Object[][] {
				{ "Update direct org details - limit CD volumes", "direct", ti.direct_org1_name, ti.csr_token,
						ti.direct_org1_id, new String[] { "6", "7", "8", "10" }, SpogConstants.SUCCESS_GET_PUT_DELETE,
						SpogMessageCode.SUCCESS_GET_PUT_DEL, 14, 30, 21, 45 },
				{ "Update sub org details - limit CD volumes", "suborg", ti.normal_msp1_suborg1_name, ti.csr_token,
						ti.normal_msp1_suborg1_id, new String[] { "6", "7", "8", "10" },
						SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, 15, 20, 67, 75 },
				{ "Update root sub org details - limit CD volumes", "root_sub", ti.root_msp1_suborg1_name, ti.csr_token,
						ti.root_msp1_suborg1_id, new String[] { "6", "7", "8", "10" },
						SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, 15, 20, 67, 75 },
				{ "Update submsp_sub org details - limit CD volumes", "submsp_sub", ti.msp1_submsp1_sub_org1_name,
						ti.csr_token, ti.msp1_submsp1_sub_org1_id, new String[] { "6", "7", "8", "10" },
						SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, 15, 20, 67, 75 },

				// 400
				{ "invalid scenario - limit CD volumes", "direct", ti.direct_org1_name, ti.csr_token, ti.direct_org1_id,
						new String[] { "-1", "0" }, SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_VALUE_LESSTHAN_MINIMUM_VALUE, 10, 10, 10, 10 },
				{ "invalid scenario - limit CD volumes", "direct", ti.direct_org1_name, ti.csr_token, ti.direct_org1_id,
						new String[] { "0", "-1" }, SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_VALUE_LESSTHAN_MINIMUM_VALUE, 10, 15, 10, 10 },

				// 403
				{ "invalid scenario - limit CD volumes-403-direct", "direct", ti.direct_org1_name,
						ti.direct_org1_user1_token, ti.direct_org1_id, new String[] { "7", "0" },
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.ONLY_CSR_PERFORM_MODIFICATIONS, 20, 25,
						10, 10 },

				{ "invalid scenario - limit CD volumes-403-suborg", "suborg", ti.normal_msp1_suborg1_name,
						ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_id, new String[] { "9", "0" },
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.ONLY_CSR_PERFORM_MODIFICATIONS, 20, 15,
						10, 10 },
				{ "invalid scenario - limit CD volumes-403-msp", "msp", ti.normal_msp_org1_name,
						ti.normal_msp_org1_user1_token, ti.normal_msp_org1_id, new String[] { "11", "0" },
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.ONLY_CSR_PERFORM_MODIFICATIONS, 15, 25,
						10, 10 },

				{ "invalid scenario - limit CD volumes-403-msp", "rootmsp", ti.root_msp_org1_name,
						ti.root_msp_org1_user1_token, ti.msp1_submsp1_sub_org1_id, new String[] { "11", "0" },
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY, 15, 25,
						10, 10 },

		};
	}

	@Test(dataProvider = "UpdateOrganizationCDcount")
	public void LimitCDDestinations_valid(String Testcase, String OrgType, String Orgname, String validToken,
			String Organization_id, String[] noOfVolumes, int ExpectedStatusCode, SpogMessageCode ExpectedErrorMessage,
			int clouddirect_trial_length, int clouddirect_deletion_queue_length, int cloudhybrid_trial_length,
			int cloudhybrid_deletion_queue_length) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());

		test.log(LogStatus.INFO, Testcase);
		Response response = null;
		Boolean blocked = false;
		if (Testcase.contains("invalid")) {
			test.log(LogStatus.INFO, "Update destinations limit for the organization");
			spogServer.setToken(validToken);
			response = spogServer.UpdateOrganizationCDcount(validToken, Organization_id,
					Integer.parseInt(noOfVolumes[0]), clouddirect_trial_length, clouddirect_deletion_queue_length,
					cloudhybrid_trial_length, cloudhybrid_deletion_queue_length, ExpectedStatusCode,
					ExpectedErrorMessage, test);
		} else {
			int volume = gen_random_index(noOfVolumes);
			int limit = Integer.parseInt(noOfVolumes[volume]);

			test.log(LogStatus.INFO, "check default volumes for the organization :" + OrgType);
			CreateDestinationsToCheckLimit(OrgType, Organization_id, 5);
			spogServer.setToken(validToken);
			if (OrgType.contains("sub")) {
				response = spogServer.UpdateOrganizationCDcount(validToken, Organization_id, limit, 0, 0, 0, 0,
						ExpectedStatusCode, ExpectedErrorMessage, test);
			} else {
				response = spogServer.UpdateOrganizationCDcount(validToken, Organization_id, limit,
						clouddirect_trial_length, clouddirect_deletion_queue_length, cloudhybrid_trial_length,
						cloudhybrid_deletion_queue_length, ExpectedStatusCode, ExpectedErrorMessage, test);
			}

			test.log(LogStatus.INFO, "get destinations limit for the organization");
			response = spogServer.getSpecifiedOrgProperties(validToken, Organization_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);

			String act_count = response.then().extract().path("data.clouddirect_volume_count").toString();
			String act_clouddirect_trial_length = response.then().extract().path("data.clouddirect_trial_length")
					.toString();
			String act_clouddirect_deletion_queue_length = response.then().extract()
					.path("data.clouddirect_deletion_queue_length").toString();

			String act_cloudhybrid_trial_length = response.then().extract().path("data.cloudhybrid_trial_length")
					.toString();
			String act_cloudhybrid_deletion_queue_length = response.then().extract()
					.path("data.cloudhybrid_deletion_queue_length").toString();

			test.log(LogStatus.INFO, "validate the updated count with the return count from response");
			spogServer.assertResponseItem(Integer.toString(limit), act_count);

			if (!OrgType.contains("sub")) {
				test.log(LogStatus.INFO, "validate the act_clouddirect_trial_length from response");
				spogServer.assertResponseItem(Integer.toString(clouddirect_trial_length), act_clouddirect_trial_length);

				test.log(LogStatus.INFO, "validate the act_clouddirect_deletion_queue_length from response");
				spogServer.assertResponseItem(Integer.toString(clouddirect_deletion_queue_length),
						act_clouddirect_deletion_queue_length);

				test.log(LogStatus.INFO, "validate the act_cloudhybrid_trial_length from response");
				spogServer.assertResponseItem(Integer.toString(cloudhybrid_trial_length), act_cloudhybrid_trial_length);

				test.log(LogStatus.INFO, "validate the act_cloudhybrid_deletion_queue_length from response");
				spogServer.assertResponseItem(Integer.toString(cloudhybrid_deletion_queue_length),
						act_cloudhybrid_deletion_queue_length);
			}
			limit = limit - 4;
			test.log(LogStatus.INFO, "Create destinations upto updated limit" + limit);
			CreateDestinationsToCheckLimit(OrgType, Organization_id, limit);
		}

	}

	private void CreateDestinationsToCheckLimit(String orgType, String organization_id, int j) {

		for (int i = 0; i < j; i++) {
			String cloud_account_id = null;
			if (orgType.equalsIgnoreCase("direct")) {
				cloud_account_id = Direct_cloud_id;
				spogDestinationServer.setToken(ti.direct_org1_user1_token);

			} else if (orgType.contains("root")) {
				cloud_account_id = root_cloud_id;
				spogDestinationServer.setToken(ti.root_msp_org1_user1_token);

			} else if (orgType.contains("submsp_sub")) {
				cloud_account_id = root_cloud_id;
				spogDestinationServer.setToken(ti.root_msp1_submsp1_user1_token);

			} else {
				cloud_account_id = msp_cloud_account_id;
				spogDestinationServer.setToken(ti.normal_msp_org1_user1_token);
			}
			String prefix = spogServer.ReturnRandom("dest");
			test.log(LogStatus.INFO, "create cloud hybrid destination to check the limit specific to CD");
			spogDestinationServer.createHybridDestination(ti.csr_token, organization_id, datacenters[0],
					DestinationType.cloud_hybrid_store.toString(), "hybrid_volume" + prefix, "running", test);

			if (i < (j - 1)) {
				String dest=UUID.randomUUID().toString();
				spogDestinationServer.createDestinationWithCheckErrorCase(dest, organization_id,
						cloud_account_id, datacenters[0], DestinationType.cloud_direct_volume.toString(), "running",
						"20", cloud_account_id, "normal", "hostname", "7D", "retention", "0", "0", "0", "0", "0", "0",
						"4", "true", "4096", "true", "destination_volume" + prefix + 1, SpogConstants.SUCCESS_POST,
						SpogMessageCode.SUCCESS_POST.toString(), test);
			} else {
				spogDestinationServer.createDestinationWithCheckErrorCase(UUID.randomUUID().toString(), organization_id,
						cloud_account_id, datacenters[0], DestinationType.cloud_direct_volume.toString(), "running",
						"20", cloud_account_id, "normal", "hostname", "7D", "retention", "0", "0", "0", "0", "0", "0",
						"4", "true", "4096", "true", "destination_volume" + prefix,
						SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.REACHED_LIMIT_TO_CREATE_DESTINATION.getCodeString(), test);
			}
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

	// generate random number
	public int gen_random_index(String[] job_status) {
		Random generator = new Random();
		int randomindx = generator.nextInt(job_status.length);

		return randomindx;
	}
}
