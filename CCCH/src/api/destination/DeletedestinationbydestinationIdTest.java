package api.destination;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

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
import Constants.DestinationType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGJobServer;
import InvokerServer.SPOGReportServer;
import InvokerServer.SPOGServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class DeletedestinationbydestinationIdTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private SPOGJobServer spogJobServer;
	private SPOGDestinationServer spogDestinationServer;
	private SPOGReportServer spogreportServer;
	private Policy4SPOGServer policy4SPOGServer;

	private ExtentTest test;
	private TestOrgInfo ti;

	String[] type = new String[12];
	private String job_Type = "backup";
	private String job_Status = "finished";
	private String job_Method = "full,incremental,resync,full,incremental,resync,full,incremental,resync,full,incremental,resync";
	public String JobSeverity = "warning,information,critical,warning,information,critical,warning,information,critical,warning,information,critical";
	public String JobStatus = "canceled,failed,finished,active,canceled,idle,waiting,crash,license_failed,backupjob_proc_exit,skipped,stop,active,finished";

	private String Direct_cloud_id;
	private String msp_cloud_account_id;
	private String root_cloud_id;

	ArrayList<String> available_actions = new ArrayList<String>();
	ArrayList<String> available_actions1 = new ArrayList<String>();
	private ArrayList<HashMap> source_group = new ArrayList<HashMap>();
	private String[] datacenters = null;

	@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runningMachine, String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		policy4SPOGServer = new Policy4SPOGServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		rep = ExtentManager.getInstance(this.getClass().getSimpleName(), logFolder);
		Nooftest = 0;
		bqdb1 = new SQLServerDb();
		String author = "Ramya.Nagepalli";
		count1 = new testcasescount();
		test = rep.startTest("beforeClass");
		test.assignAuthor("Ramya");
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

	@DataProvider(name = "postdestination_cloudvolume", parallel = false)
	public final Object[][] postdestination_cloudvolume() {
		return new Object[][] {
				{ "direct", ti.direct_org1_user1_token, ti.direct_org1_user1_id, Direct_cloud_id, ti.direct_org1_id,
						DestinationType.cloud_direct_volume.toString(), DestinationStatus.running.toString(), 20, 2, 22,
						"normal", "host-abc", "7D", "0", "0", "0", "0", "0", "0",
						RandomStringUtils.randomAlphanumeric(4) + "Ramya-test34" },
				{ "suborgmsptoken", ti.normal_msp_org1_user1_token, ti.normal_msp1_suborg1_user1_id,
						msp_cloud_account_id, ti.normal_msp1_suborg1_id, DestinationType.cloud_direct_volume.toString(),
						DestinationStatus.running.toString(), 20, 2, 22, "normal", "host-abc", "7D", "0", "0", "0", "0",
						"0", "0", RandomStringUtils.randomAlphanumeric(4) + "Ramya-test34" }

		};
	}

	@Test(dataProvider = "postdestination_cloudvolume")
	public void deletedestinationbydestination_Id_clouddirectvolume(String organizationType, String validToken,
			String user_id, String site_id, String organization_id, String destination_type, String destination_status,
			double primary_usage, double snapshot_usage, double total_usage, String volume_type, String hostname,
			String retention_id, String age_hours_max, String age_four_hours_max, String age_days_max,
			String age_weeks_max, String age_months_max, String age_years_max, String destination_name) {
		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		test.log(LogStatus.INFO, "get a destination of type " + destination_type);
		Response response = spogDestinationServer.getDestinations(validToken, "organization_id=" + organization_id,
				test);
		String destination_Id = response.then().extract().path("data[0].destination_id");
		test.log(LogStatus.INFO, "Delete destination by destination id");
		spogDestinationServer.deletedestinationbydestination_Id(destination_Id, validToken + "abc",
				SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);

		test.log(LogStatus.INFO, "After trying with invalid token");

		test.log(LogStatus.INFO, "Delete destination by destination id");
		spogDestinationServer.deletedestinationbydestination_Id(destination_Id, "", SpogConstants.NOT_LOGGED_IN,
				SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);

		test.log(LogStatus.INFO, "After trying with missed token");

		test.log(LogStatus.INFO, "Delete destination by destination id");
		spogDestinationServer.deletedestinationbydestination_Id(UUID.randomUUID().toString(), validToken,
				SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.DESTINATION_NOT_FOUND, test);

		test.log(LogStatus.INFO, "After trying with Invalid destinationID");

		HashMap<String, String> params = null;
		policy4SPOGServer.setToken(validToken);
		response = policy4SPOGServer.getPolicies(params);
		ArrayList<String> policies = response.then().extract().path("data.policy_id");

		for (int i = 0; i < policies.size(); i++)
			policy4SPOGServer.deletePolicybyPolicyId(validToken, policies.get(i));

		test.log(LogStatus.INFO, "Delete destination by destination id");
		/*
		 * spogDestinationServer.deletedestinationbydestination_Id(destination_Id,
		 * validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		 */

	}

	@DataProvider(name = "postdestination_cloud_hybrid_store", parallel = false)
	public final Object[][] postdestination_cloud_hybrid_store() {
		return new Object[][] {
				{ "direct", ti.direct_org1_user1_token, ti.direct_org1_user1_id, Direct_cloud_id, ti.direct_org1_id,
						DestinationType.cloud_hybrid_store.toString(), DestinationStatus.running.toString(), "F:\\abc",
						"F:\\abc\\data", "F:\\abc\\index", "F:\\abc\\hash", 3, true, 64, "1024", true, "Mclaren@2010",
						"0", "0", "0.5", "0.5", "Ramya-test1" },
				{ "msp", ti.normal_msp_org1_user1_token, ti.normal_msp1_suborg1_user1_id, msp_cloud_account_id,
						ti.normal_msp1_suborg1_id, DestinationType.cloud_hybrid_store.toString(),
						DestinationStatus.running.toString(), "F:\\abc", "F:\\abc\\data", "F:\\abc\\index",
						"F:\\abc\\hash", 3, true, 64, "1024", true, "Mclaren@2010", "0", "0", "0.5", "0.5",
						"Ramya-test1" }

		};
	}

	@Test(dataProvider = "postdestination_cloud_hybrid_store")
	public void deletedestinationbydestination_Id_cloud_hybrid_store(String organizationType, String validToken,
			String user_id, String site_id, String organization_id, String destination_type, String destination_status,
			String data_store_folder, String data_destination, String index_destination, String hash_destination,
			int concurrent_active_node, boolean is_deduplicated, int block_size, String hash_memory,
			boolean is_compressed, String encryption_password, String occupied_space, String store_data,
			String deduplication_rate, String compression_rate, String destination_name) {
		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);
		spogDestinationServer.setToken(validToken);

		test.log(LogStatus.INFO, "Create a destination of type " + destination_type);
		String destination_Id = spogDestinationServer.createHybridDestination(validToken, organization_id,
				datacenters[0], DestinationType.cloud_hybrid_store.toString(), destination_name, "running", test);

		test.log(LogStatus.INFO, "get destination and verify the contextual actions");
		Response response = spogDestinationServer.getDestinationById(validToken, destination_Id, test);
		ArrayList<String> act_data = response.then().extract().path("data.available_actions");
		if (act_data.contains("delete")) {
			assertTrue(true, "Delete action is available for CH destination");
		} else {
			assertFalse(true, "Delete action is not available for CH destination");
		}

		test.log(LogStatus.INFO, "delete policies associated with CH destination");

		policy4SPOGServer.setToken(validToken);
		response = policy4SPOGServer.getPolicies(null);
		ArrayList<String> policies = response.then().extract().path("data.policy_id");
		
		policies.forEach(policy->{
			policy4SPOGServer.deletePolicybyPolicyId(validToken, policy);
		});

		test.log(LogStatus.INFO, "Delete destination by destination id");
		spogDestinationServer.deletedestinationbydestination_Id(destination_Id, validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

	}

	@DataProvider(name = "getdestinationfilterbyfilterId_insufficientpermissions", parallel = false)
	public final Object[][] getdestinationfilterbyfilterId_403() {
		return new Object[][] {
				{ "direct_msp", "direct", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id,
						Direct_cloud_id, ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,
						DestinationType.cloud_direct_volume.toString(), DestinationStatus.running.toString(), 20, 2, 22,
						"normal", "host-abc", "7D", "0", "0", "0", "0", "0", "0",
						RandomStringUtils.randomAlphanumeric(4) + "Ramya-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },
				{ "direct_suborg", "direct", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id,
						Direct_cloud_id, ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id,
						DestinationType.cloud_direct_volume.toString(), DestinationStatus.running.toString(), 20, 2, 22,
						"normal", "host-abc", "7D", "0", "0", "0", "0", "0", "0",
						RandomStringUtils.randomAlphanumeric(4) + "Ramya-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },
				{ "suborg_direct", "suborg", ti.normal_msp1_suborg1_id, ti.normal_msp_org1_user1_token,
						ti.normal_msp1_suborg1_user1_id, msp_cloud_account_id, ti.direct_org1_user1_token,
						ti.direct_org1_user1_id, DestinationType.cloud_direct_volume.toString(),
						DestinationStatus.running.toString(), 20, 2, 22, "normal", "host-abc", "1M", "0", "0", "0", "0",
						"0", "0", RandomStringUtils.randomAlphanumeric(4) + "Ramya-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },
				{ "suborg_suborgb", "suborg", ti.normal_msp1_suborg1_id, ti.normal_msp_org1_user1_token,
						ti.normal_msp1_suborg1_user1_id, msp_cloud_account_id, ti.direct_org1_user1_token,
						ti.direct_org1_user1_id, DestinationType.cloud_direct_volume.toString(),
						DestinationStatus.running.toString(), 20, 2, 22, "normal", "host-abc", "1M", "0", "0", "0", "0",
						"0", "0", RandomStringUtils.randomAlphanumeric(4) + "Ramya-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },
				{ "suborg_mspb", "suborg", ti.normal_msp1_suborg1_id, ti.normal_msp_org1_user1_token,
						ti.normal_msp1_suborg1_user1_id, msp_cloud_account_id, ti.normal_msp_org2_user1_token,
						ti.normal_msp_org2_user1_id, DestinationType.cloud_direct_volume.toString(),
						DestinationStatus.running.toString(), 20, 2, 22, "normal", "host-abc", "1M", "0", "0", "0", "0",
						"0", "0", RandomStringUtils.randomAlphanumeric(4) + "Ramya-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },

				// 3 tier cases
				{ "direct_rootmsp", "direct", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id,
						Direct_cloud_id, ti.root_msp_org1_user1_token, ti.root_msp_org1_user1_id,
						DestinationType.cloud_direct_volume.toString(), DestinationStatus.running.toString(), 20, 2, 22,
						"normal", "host-abc", "7D", "0", "0", "0", "0", "0", "0",
						RandomStringUtils.randomAlphanumeric(4) + "Ramya-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },
				{ "direct_rootsuborg", "direct", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id,
						Direct_cloud_id, ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id,
						DestinationType.cloud_direct_volume.toString(), DestinationStatus.running.toString(), 20, 2, 22,
						"normal", "host-abc", "7D", "0", "0", "0", "0", "0", "0",
						RandomStringUtils.randomAlphanumeric(4) + "Ramya-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },
				{ "rootsuborg_direct", "suborg", ti.root_msp1_suborg1_id, ti.root_msp_org1_user1_token,
						ti.root_msp1_suborg1_user1_id, root_cloud_id, ti.direct_org1_user1_token,
						ti.direct_org1_user1_id, DestinationType.cloud_direct_volume.toString(),
						DestinationStatus.running.toString(), 20, 2, 22, "normal", "host-abc", "1M", "0", "0", "0", "0",
						"0", "0", RandomStringUtils.randomAlphanumeric(4) + "Ramya-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },
				{ "rootsuborg_rootsuborgb", "suborg", ti.root_msp1_suborg1_id, ti.root_msp_org1_user1_token,
						ti.root_msp1_suborg1_user1_id, root_cloud_id, ti.direct_org1_user1_token,
						ti.direct_org1_user1_id, DestinationType.cloud_direct_volume.toString(),
						DestinationStatus.running.toString(), 20, 2, 22, "normal", "host-abc", "1M", "0", "0", "0", "0",
						"0", "0", RandomStringUtils.randomAlphanumeric(4) + "Ramya-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },
				{ "rootsuborg_rootmspb", "suborg", ti.root_msp1_suborg1_id, ti.root_msp_org1_user1_token,
						ti.root_msp1_suborg1_user1_id, root_cloud_id, ti.root_msp_org2_user1_token,
						ti.root_msp_org2_user1_id, DestinationType.cloud_direct_volume.toString(),
						DestinationStatus.running.toString(), 20, 2, 22, "normal", "host-abc", "1M", "0", "0", "0", "0",
						"0", "0", RandomStringUtils.randomAlphanumeric(4) + "Ramya-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },

				{ "rootsuborg-submsp", "submsp", ti.root_msp1_suborg1_id, ti.root_msp_org1_user1_token,
						ti.root_msp1_suborg1_user1_id, root_cloud_id, ti.root_msp1_submsp1_user1_token,
						ti.root_msp1_submsp1_user1_id, DestinationType.cloud_direct_volume.toString(),
						DestinationStatus.running.toString(), 20, 2, 22, "normal", "host-abc", "1M", "0", "0", "0", "0",
						"0", "0", RandomStringUtils.randomAlphanumeric(4) + "Ramya-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },
				{ "rootsuborg-submsp_sub", "submsp_sub", ti.root_msp1_suborg1_id, ti.root_msp_org1_user1_token,
						ti.root_msp1_suborg1_user1_id, root_cloud_id, ti.msp1_submsp1_suborg1_user1_token,
						ti.msp1_submsp1_suborg1_user1_id, DestinationType.cloud_direct_volume.toString(),
						DestinationStatus.running.toString(), 20, 2, 22, "normal", "host-abc", "1M", "0", "0", "0", "0",
						"0", "0", RandomStringUtils.randomAlphanumeric(4) + "Ramya-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },
				{ "submsp-submsp_sub", "submsp", ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_user1_token,
						ti.root_msp1_submsp1_user1_id, root_cloud_id, ti.msp1_submsp1_suborg1_user1_token,
						ti.msp1_submsp1_suborg1_user1_id, DestinationType.cloud_direct_volume.toString(),
						DestinationStatus.running.toString(), 20, 2, 22, "normal", "host-abc", "1M", "0", "0", "0", "0",
						"0", "0", RandomStringUtils.randomAlphanumeric(4) + "Ramya-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },
				{ "submsp-rootsub", "submsp", ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_user1_token,
						ti.root_msp1_submsp1_user1_id, root_cloud_id, ti.root_msp1_suborg1_user1_token,
						ti.root_msp1_suborg1_user1_id, DestinationType.cloud_direct_volume.toString(),
						DestinationStatus.running.toString(), 20, 2, 22, "normal", "host-abc", "1M", "0", "0", "0", "0",
						"0", "0", RandomStringUtils.randomAlphanumeric(4) + "Ramya-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },

				{ "submsp-rootmaa", "submsp", ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_user1_token,
						ti.root_msp1_submsp1_user1_id, root_cloud_id, ti.root_msp_org1_msp_accountadmin1_token,
						ti.root_msp_org1_msp_accountadmin1_id, DestinationType.cloud_direct_volume.toString(),
						DestinationStatus.running.toString(), 20, 2, 22, "normal", "host-abc", "1M", "0", "0", "0", "0",
						"0", "0", RandomStringUtils.randomAlphanumeric(4) + "Ramya-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },
				{ "rootsuborg-submsp_sub", "submsp_sub", ti.root_msp1_suborg1_id, ti.root_msp_org1_user1_token,
						ti.root_msp1_suborg1_user1_id, root_cloud_id, ti.root_msp_org1_msp_accountadmin1_token,
						ti.root_msp_org1_msp_accountadmin1_id, DestinationType.cloud_direct_volume.toString(),
						DestinationStatus.running.toString(), 20, 2, 22, "normal", "host-abc", "1M", "0", "0", "0", "0",
						"0", "0", RandomStringUtils.randomAlphanumeric(4) + "Ramya-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },

		};
	}

	@Test(dataProvider = "getdestinationfilterbyfilterId_insufficientpermissions")
	public void deletedestinationbydestination_Id_403_otherorgtoken(String testcase, String organizationType,
			String organization_id, String validToken, String user_id, String site_id, String otherorgtoken,
			String otherorguserid, String destination_type, String destination_status, double primary_usage,
			double snapshot_usage, double total_usage, String volume_type, String hostname, String retention_id,
			String age_hours_max, String age_four_hours_max, String age_days_max, String age_weeks_max,
			String age_months_max, String age_years_max, String destination_name, String filter_destination_name,
			String filter_destination_type, String filter_policy_id) {
		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);
		String destination_Id = UUID.randomUUID().toString();

		ArrayList<String> destinations = new ArrayList<>();
		Response response = spogDestinationServer.getDestinations(validToken, "", test);

		destinations = response.then().extract().path("data.destination_id");
		destination_Id = destinations.get(0);

		test.log(LogStatus.INFO, "Delete the destination with other org token");
		spogDestinationServer.deletedestinationbydestination_Id(destination_Id, otherorgtoken,
				SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY, test);

		// delete destiantion with csr read only token
		test.log(LogStatus.INFO, "Delete the destination with other org token");
		spogDestinationServer.deletedestinationbydestination_Id(destination_Id, ti.csr_readonly_token,
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
