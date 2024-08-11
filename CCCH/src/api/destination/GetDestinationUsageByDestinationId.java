package api.destination;

import api.destination.GetDestinations;
import base.prepare.TestOrgInfo;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.volume_type;
import Base64.EncodingBase64;
import Constants.ConnectionStatus;
import Constants.JobStatus;
import Constants.OSMajor;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import InvokerServer.GatewayServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import bsh.org.objectweb.asm.Constants;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import groovyjarjarantlr.PreservingFileWriter;
import io.restassured.response.Response;
import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.net.InetAddress;
import java.text.SimpleDateFormat;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.codehaus.groovy.control.XStreamUtils;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import Constants.SpogMessageCode;
import InvokerServer.GatewayServer.siteType;
import Constants.DestinationStatus;
import Constants.DestinationType;

public class GetDestinationUsageByDestinationId extends base.prepare.Is4Org {

	public SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private SPOGDestinationServer spogDestinationServer;
	private ExtentTest test;
	private Response response;

	/*
	 * private ExtentReports rep; private SQLServerDb bqdb1; public int Nooftest;
	 * private long creationTime; private String BQName=null; private String
	 * runningMachine; private testcasescount count1; private String buildVersion;
	 */

	// For storing the retention,cloud_direct_vloume information
	HashMap<String, String> retention = new HashMap<String, String>();
	HashMap<String, Object> cloud_direct_volume = new HashMap<String, Object>();

	HashMap<String, Object> cloud_dedupe_volume = new HashMap<String, Object>();

	private String job_Type = "backup_incremental,backup_full,copy,merge";
	private String job_Status = "finished";
	private String job_Method = "full,incremental,resync";
	public String JobSeverity = "success,information";
	private String direct_cloud_id, msp_cloud_id;
	private String[] datacenters;

	private UserSpogServer userSpogServer;
	private String org_model_prefix = this.getClass().getSimpleName();
	private String direct_baas_destionation_ID;
	private String sub_orga_baas_destionation_ID;
	private String submsp_suborg_baas_destionation_ID;
	private TestOrgInfo ti;

	@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runningMachine, String buildVersion) {

		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogServer = new SPOGServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		rep = ExtentManager.getInstance(this.getClass().getSimpleName(), logFolder);
		Nooftest = 0;
		bqdb1 = new SQLServerDb();
		String author = "Kanamarlapudi, Chandra Kanth";
		count1 = new testcasescount();
		test = rep.startTest("beforeClass");
		test.assignAuthor("Kanamarlapudi, Chandra Kanth");
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

		response = spogServer.getCloudAccounts(ti.direct_org1_user1_token, "", test);
		direct_cloud_id = response.then().extract().path("data[0].cloud_account_id");

		response = spogServer.getCloudAccounts(ti.root_msp_org1_user1_token, "", test);
		msp_cloud_id = response.then().extract().path("data[0].cloud_account_id");

		spogDestinationServer.setToken(ti.csr_token);
		datacenters = spogDestinationServer.getDestionationDatacenterID();

		response = spogDestinationServer.getDestinations(ti.direct_org1_user1_token, null, test);
		direct_baas_destionation_ID = response.then().extract().path("data[0].destination_id");

		response = spogDestinationServer.getDestinations(ti.root_msp1_suborg1_user1_token, null, test);
		sub_orga_baas_destionation_ID = response.then().extract().path("data[0].destination_id");

		response = spogDestinationServer.getDestinations(ti.msp1_submsp1_suborg1_user1_token, null, test);
		submsp_suborg_baas_destionation_ID = response.then().extract().path("data[0].destination_id");

		// Composing a HashMap for the destinations Retentions of the cloud direct
		// volumes
		HashMap<String, Object> Retention = new HashMap<String, Object>();
		HashMap<String, String> retention = new HashMap<String, String>();
		retention = spogDestinationServer.composeRetention("0", "0", "7", "0", "0", "0");
		Retention.put("7D", retention);
		retention = spogDestinationServer.composeRetention("0", "0", "14", "0", "0", "0");
		Retention.put("14D", retention);
		retention = spogDestinationServer.composeRetention("0", "0", "31", "0", "0", "0");
		Retention.put("1M", retention);
		retention = spogDestinationServer.composeRetention("0", "0", "31", "0", "2", "0");
		Retention.put("2M", retention);
		retention = spogDestinationServer.composeRetention("0", "0", "31", "0", "3", "0");
		Retention.put("3M", retention);
		retention = spogDestinationServer.composeRetention("0", "0", "31", "0", "6", "0");
		Retention.put("6M", retention);
		retention = spogDestinationServer.composeRetention("0", "0", "31", "0", "12", "0");
		Retention.put("1Y", retention);
		retention = spogDestinationServer.composeRetention("0", "0", "31", "0", "12", "2");
		Retention.put("2Y", retention);
		retention = spogDestinationServer.composeRetention("0", "0", "31", "0", "12", "3");
		Retention.put("3Y", retention);
		retention = spogDestinationServer.composeRetention("0", "0", "31", "0", "12", "7");
		Retention.put("7Y", retention);
		retention = spogDestinationServer.composeRetention("0", "0", "31", "0", "12", "10");
		Retention.put("10Y", retention);
		retention = spogDestinationServer.composeRetention("0", "0", "31", "0", "12", "-1");
		Retention.put("forever", retention);
		retention = spogDestinationServer.composeRetention("0", "42", "7", "0", "0", "0");
		Retention.put("7Dhf", retention);
		retention = spogDestinationServer.composeRetention("0", "42", "14", "0", "0", "0");
		Retention.put("14Dhf", retention);
		retention = spogDestinationServer.composeRetention("0", "42", "31", "0", "0", "0");
		Retention.put("1Mhf", retention);
		spogDestinationServer.setRetention(Retention);
	}

	// This information is related to the destination information of the cloud
	// hybrid volume
	@DataProvider(name = "destinationinfocloudHybrid")
	public final Object[][] getDestination() {
		return new Object[][] {
				{ "Create Cloud Hybrid destination and get by direct org user token", ti.direct_org1_user1_token,
						ti.direct_org1_user1_token, ti.direct_org1_name, ti.direct_org1_id, direct_cloud_id,
						DestinationType.cloud_hybrid_store.toString(), "d:\\ds\\0", "d:\\ds\\1", "d:\\ds\\3",
						"d:\\ds\\2", 5, true, 512, "256", false, "", "300", "200", 0.5, 0.5,
						DestinationStatus.running.toString(), spogServer.ReturnRandom("destDedup1"),
						System.currentTimeMillis(), System.currentTimeMillis(), "finished", "2" },

				{ "Create Cloud Hybrid destination for sub org and get by msp user token", ti.root_msp_org1_user1_token,
						ti.root_msp_org1_user1_token, ti.root_msp1_suborg1_name, ti.root_msp1_suborg1_id, msp_cloud_id,
						DestinationType.cloud_hybrid_store.toString(), "e:\\dsp\\0", "f:\\ds\\1", "f:\\ds\\3",
						"f:\\ds\\2", 45, true, 128, "256", true, "456", "355", "400", 0.565, 0.465,
						DestinationStatus.running.toString(), spogServer.ReturnRandom("testDedup"),
						System.currentTimeMillis(), System.currentTimeMillis(), "finished", "10" },
				{ "Create Cloud Hybrid destination for sub org and get by sub org user token",
						ti.root_msp_org1_user1_token, ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_name,
						ti.root_msp1_suborg1_id, msp_cloud_id, DestinationType.cloud_hybrid_store.toString(),
						"e:\\dsp\\0", "f:\\ds\\1", "f:\\ds\\3", "f:\\ds\\2", 45, true, 128, "256", true, "456", "355",
						"400", 0.565, 0.465, DestinationStatus.running.toString(), spogServer.ReturnRandom("testDedup"),
						System.currentTimeMillis(), System.currentTimeMillis(), "finished", "10" },
				{ "Create Cloud Hybrid destination for sub org and get by msp account admin user token",
						ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_token,
						ti.root_msp1_suborg1_name, ti.root_msp1_suborg1_id, msp_cloud_id,
						DestinationType.cloud_hybrid_store.toString(), "e:\\dsp\\0", "f:\\ds\\1", "f:\\ds\\3",
						"f:\\ds\\2", 45, true, 128, "256", true, "456", "355", "400", 0.565, 0.465,
						DestinationStatus.running.toString(), spogServer.ReturnRandom("testDedup"),
						System.currentTimeMillis(), System.currentTimeMillis(), "finished", "10" },

				{ "Create Cloud Hybrid destination for sub org in sub msp and get by sub msp user token",
						ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_token,
						ti.msp1_submsp1_sub_org1_name, ti.msp1_submsp1_sub_org1_id, msp_cloud_id,
						DestinationType.cloud_hybrid_store.toString(), "e:\\dsp\\0", "f:\\ds\\1", "f:\\ds\\3",
						"f:\\ds\\2", 45, true, 128, "256", true, "456", "355", "400", 0.565, 0.465,
						DestinationStatus.running.toString(), spogServer.ReturnRandom("testDedup"),
						System.currentTimeMillis(), System.currentTimeMillis(), "finished", "10" },
				{ "Create Cloud Hybrid destination for sub org in sub msp and get by sub org user token",
						ti.root_msp1_submsp1_user1_token, ti.msp1_submsp1_suborg1_user1_token,
						ti.msp1_submsp1_sub_org1_name, ti.msp1_submsp1_sub_org1_id, msp_cloud_id,
						DestinationType.cloud_hybrid_store.toString(), "e:\\dsp\\0", "f:\\ds\\1", "f:\\ds\\3",
						"f:\\ds\\2", 45, true, 128, "256", true, "456", "355", "400", 0.565, 0.465,
						DestinationStatus.running.toString(), spogServer.ReturnRandom("testDedup"),
						System.currentTimeMillis(), System.currentTimeMillis(), "finished", "10" },
				{ "Create Cloud Hybrid destination for sub org in sub msp and get by sub msp account admin user token",
						ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp1_account_admin_token,
						ti.msp1_submsp1_sub_org1_name, ti.msp1_submsp1_sub_org1_id, msp_cloud_id,
						DestinationType.cloud_hybrid_store.toString(), "e:\\dsp\\0", "f:\\ds\\1", "f:\\ds\\3",
						"f:\\ds\\2", 45, true, 128, "256", true, "456", "355", "400", 0.565, 0.465,
						DestinationStatus.running.toString(), spogServer.ReturnRandom("testDedup"),
						System.currentTimeMillis(), System.currentTimeMillis(), "finished", "10" },

		};
	}

	@Test(dataProvider = "destinationinfocloudHybrid")
	public void getDestinationsByIdcloudHybrid(String testCase, String token, String tokenForGet,
			String organization_name, String organization_id, String site_id, String DestinationType,
			String data_store_folder, String data_destination, String index_destination, String hash_destination,
			int concurrent_active_node, boolean Is_deduplicated, int block_size, String hash_memory,
			boolean is_compressed, String encryption_password, String occupied_space, String stored_data,
			Double deduplication_rate, Double compression_rate, String destination_status, String destination_name,
			long start_time_ts, long endTimeTS, String status, String dedupe_savings) {

		HashMap<String, ArrayList<HashMap<String, Object>>> last_job = new HashMap<String, ArrayList<HashMap<String, Object>>>();
		test = ExtentManager.getNewTest(testCase);
		test.assignAuthor("Rakesh.Chalamala");
		String destination_id = null;

		test.log(LogStatus.INFO, "Creating a destination of type cloud_dedupe_volume");
		cloud_dedupe_volume = spogDestinationServer.composeCloudDedupeInfo(data_store_folder, data_destination,
				index_destination, hash_destination, concurrent_active_node, Is_deduplicated, block_size, hash_memory,
				is_compressed, encryption_password, occupied_space, stored_data, deduplication_rate, compression_rate);
		Response response = spogDestinationServer.createDestination(UUID.randomUUID().toString(), token, site_id,
				organization_id, site_id, datacenters[0], dedupe_savings, DestinationType, destination_status,
				destination_name, cloud_dedupe_volume, test);
		destination_id = response.then().extract().path("data.destination_id");

		String rps_id = UUID.randomUUID().toString();
		String policy_id = UUID.randomUUID().toString();
		String sourceName = spogServer.ReturnRandom("rak_source");

		test.log(LogStatus.INFO, "create source");
		spogServer.setToken(token);
		String job_type[] = job_Type.split(",");
		String job_method[] = job_Method.split(",");
		String job_severity[] = JobSeverity.split(",");

		test.log(LogStatus.INFO, "Create source");
		response = spogServer.createSource(sourceName, SourceType.machine, SourceProduct.cloud_direct, organization_id,
				site_id, ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(), "", test);
		String source_id = response.then().extract().path("data.source_id");
		for (int i = 0; i < 2; i++) {
			start_time_ts = start_time_ts + i * 10 + 1;
			endTimeTS = endTimeTS + i * 10 + 200;
			String job_id = gatewayServer.postJobWithCheck(start_time_ts, endTimeTS, organization_id, source_id,
					source_id, rps_id, destination_id, policy_id, job_type[i], job_method[i], status, token, test);

			last_job = spogDestinationServer.composeLastJob(start_time_ts, endTimeTS, 0.0, status, job_type[i],
					job_method[i]);
		}

		String additionalURL = spogServer.PrepareURL("", "", 0, 0, test);

		test.log(LogStatus.INFO, "Get destination usage for cloud_hybrid_store will fail with 400");
		response = spogDestinationServer.getDestinationUsageCount(token, destination_id, additionalURL, test);
		spogDestinationServer.checkgetDestinationUsage(response, destination_id, destination_name,
				SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.DESTINATION_TYPE_MUST_BE_CLOUD_DIRECT, test);

		test.log(LogStatus.INFO, "Get destination usage for cloud_hybrid_store will fail with 400 with csr token");
		response = spogDestinationServer.getDestinationUsageCount(ti.csr_token, destination_id, additionalURL, test);
		spogDestinationServer.checkgetDestinationUsage(response, destination_id, destination_name,
				SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.DESTINATION_TYPE_MUST_BE_CLOUD_DIRECT, test);

		test.log(LogStatus.INFO,
				"Get destination usage for cloud_hybrid_store will fail with 400 with csr readonly token");
		response = spogDestinationServer.getDestinationUsageCount(ti.csr_readonly_token, destination_id, additionalURL,
				test);
		spogDestinationServer.checkgetDestinationUsage(response, destination_id, destination_name,
				SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.DESTINATION_TYPE_MUST_BE_CLOUD_DIRECT, test);

		spogDestinationServer.deletedestinationbydestination_Id(destination_id, token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		spogServer.deleteSourceByID(source_id, test);
	}

	// This information is related to the valid JWTTokenForCloudDirectInformation
	@DataProvider(name = "destinationinfocloudDirect")
	public final Object[][] getDestinatonInfo() {
		return new Object[][] {
				{ "Create Cloud Direct destination and get by direct org user token", ti.direct_org1_user2_token,
						new String[] { ti.direct_org1_user2_token }, ti.direct_org1_name, ti.direct_org1_id,
						DestinationType.cloud_direct_volume.toString(), direct_cloud_id,
						DestinationStatus.running.toString(), spogServer.ReturnRandom("dest"),
						spogServer.ReturnRandom("vol"), "6M", "6 Months", "0", "0", "0", "0", "0", "0", 23.0, 23.0,
						46.0, volume_type.normal.toString(), "RAKCHA-PCW10", datacenters[0], System.currentTimeMillis(),
						System.currentTimeMillis(), JobStatus.finished.toString(), "10" },

				{ "Create Cloud Direct destination in sub org and get by MSP, sub org, Account admin user tokens",
						ti.root_msp_org1_user2_token,
						new String[] { ti.root_msp_org1_user2_token, ti.root_msp1_suborg1_user2_token,
								ti.root_msp_org1_msp_accountadmin1_token },
						ti.root_msp1_suborg1_name, ti.root_msp1_suborg1_id,
						DestinationType.cloud_direct_volume.toString(), msp_cloud_id,
						DestinationStatus.running.toString(), spogServer.ReturnRandom("dest"),
						spogServer.ReturnRandom("vol"), "6M", "6 Months", "0", "0", "0", "0", "0", "0", 23.0, 23.0,
						46.0, volume_type.normal.toString(), "RAKCHA-10", datacenters[0], System.currentTimeMillis(),
						System.currentTimeMillis(), JobStatus.finished.toString(), "6" },

				{ "Create Cloud Direct destination in sub msp's sub org and get by sub MSP, sub org, Account admin user tokens",
						ti.root_msp1_submsp1_user2_token,
						new String[] { ti.root_msp1_submsp1_user2_token, ti.msp1_submsp1_suborg1_user2_token,
								ti.root_msp1_submsp1_account_admin_token },
						ti.msp1_submsp1_sub_org1_name, ti.msp1_submsp1_sub_org1_id,
						DestinationType.cloud_direct_volume.toString(), msp_cloud_id,
						DestinationStatus.running.toString(), spogServer.ReturnRandom("dest"),
						spogServer.ReturnRandom("vol"), "6M", "6 Months", "0", "0", "0", "0", "0", "0", 23.0, 23.0,
						46.0, volume_type.normal.toString(), "RAKCHA-10", datacenters[0], System.currentTimeMillis(),
						System.currentTimeMillis(), JobStatus.finished.toString(), "8" },

		};
	}

	@Test(dataProvider = "destinationinfocloudDirect", enabled = true)
	public void getDestinationsByIdcloudDirect(String caseName, String token, String[] tokensForGet,
			String organization_name, String organization_id, String DestinationType, String site_id,
			String destination_status, String destination_name, String cloud_direct_volume_name, String retention_id,
			String retention_name, String age_hours_max, String age_four_hours_max, String age_days_max,
			String age_weeks_max, String age_months_max, String age_years_max, Double primary_usage,
			Double snapshot_usage, Double total_usage, String volume_type, String hostname, String datacenter_id,
			long start_time_ts, long endTimeTS, String status, String dedupe_savings) {

		HashMap<String, ArrayList<HashMap<String, Object>>> last_job = new HashMap<String, ArrayList<HashMap<String, Object>>>();

		// creating the test object for the log information
		test = ExtentManager.getNewTest(caseName);
		test.assignAuthor("Rakesh.Chalamala");
		spogDestinationServer.setToken(token);
		String destination_id = null;

		// creating a destination
		retention = spogDestinationServer.composeRetention(age_hours_max, age_four_hours_max, age_days_max,
				age_weeks_max, age_months_max, age_years_max);
		cloud_direct_volume = spogDestinationServer.composeCloudDirectInfo(site_id, cloud_direct_volume_name,
				retention_id, retention_name, primary_usage, snapshot_usage, total_usage, volume_type, hostname,
				retention);

		test.log(LogStatus.INFO, "Creating a destination of type cloud_direct_volume");
/*		Response response = spogDestinationServer.createDestination(token, organization_id, "", datacenter_id,
				DestinationType, destination_status, destination_name, cloud_direct_volume, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		destination_id = response.then().extract().path("data.destination_id");*/
		destination_id = spogDestinationServer.createCdDestination(token, organization_id, datacenter_id, DestinationType, destination_name, destination_status, volume_type, 
				retention_id, retention_name, retention, SpogConstants.SUCCESS_POST, test);

		String rps_id = UUID.randomUUID().toString();
		String policy_id = UUID.randomUUID().toString();
		String sourceName = spogServer.ReturnRandom("rak_source");

		test.log(LogStatus.INFO, "create source");
		spogServer.setToken(token);
		String job_type[] = job_Type.split(",");
		String job_method[] = job_Method.split(",");
		String job_severity[] = JobSeverity.split(",");
		response = spogServer.createSource(sourceName, SourceType.agentless_vm, SourceProduct.cloud_direct,
				organization_id, site_id, ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(),
				"", test);
		String source_id = response.then().extract().path("data.source_id");

		for (int i = 0; i < 2; i++) {
			start_time_ts = start_time_ts + i * 10 + 1;
			endTimeTS = endTimeTS + i * 10 + 200;
			String job_id = gatewayServer.postJobWithCheck(start_time_ts, endTimeTS, organization_id, source_id,
					source_id, rps_id, destination_id, policy_id, job_type[i], job_method[i], status, token, test);
			last_job = spogDestinationServer.composeLastJob(start_time_ts, endTimeTS, 0.0, status, job_type[i],
					job_method[i]);
		}

		String additionalURL = spogServer.PrepareURL("", "", 0, 0, test);
		for (String tokenForGet : tokensForGet) {
			response = spogDestinationServer.getDestinationUsageCount(tokenForGet, destination_id, additionalURL, test);
			spogDestinationServer.checkgetDestinationUsage(response, destination_id, destination_name,
					SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		}

		test.log(LogStatus.INFO, "Get destination usage with csr token");
		response = spogDestinationServer.getDestinationUsageCount(ti.csr_token, destination_id, additionalURL, test);
		spogDestinationServer.checkgetDestinationUsage(response, destination_id, destination_name,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		test.log(LogStatus.INFO, "Get destination usage with csr readonly user token");
		response = spogDestinationServer.getDestinationUsageCount(ti.csr_readonly_token, destination_id, additionalURL,
				test);
		spogDestinationServer.checkgetDestinationUsage(response, destination_id, destination_name,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		spogDestinationServer.recycleCDVolume(destination_id);
//		spogDestinationServer.deletedestinationbydestination_Id(destination_id, token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
//		spogServer.deleteSourceByID(source_id, test);
	}

	@DataProvider(name = "DestinationdataInvalid")
	public final Object[][] getDestinationUsageByIdInsufficientPermissions() {
		return new Object[][] {
				// 401 cases
				{ "Get destinations by id with invalid token", "invalidtoken", UUID.randomUUID().toString(),
						SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT },
				{ "Get destinations by id with missing token", "", UUID.randomUUID().toString(),
						SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED },

				// 400 cases
				{ "Get specified destination with invalid id & csr token", ti.csr_token, "invalid",
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_DESTINATIONID_NOT_UUID },
				{ "Get specified destination with invalid id & csr readonly user token", ti.csr_readonly_token,
						"invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_DESTINATIONID_NOT_UUID },
				{ "Get specified destination with invalid id in direct org", ti.direct_org1_user1_token, "invalid",
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_DESTINATIONID_NOT_UUID },
				{ "Get specified destination with invalid id in msp org", ti.root_msp_org1_user1_token, "invalid",
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_DESTINATIONID_NOT_UUID },
				{ "Get specified destination with invalid id in sub org", ti.root_msp1_suborg1_user1_token, "invalid",
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_DESTINATIONID_NOT_UUID },
				{ "Get specified destination with invalid id in sub org with msp_account_admin token",
						ti.root_msp_org1_msp_accountadmin1_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_DESTINATIONID_NOT_UUID },

				{ "Get specified destination with null & csr token", ti.csr_token, null,
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_DESTINATIONID_NOT_UUID },
				{ "Get specified destination with null & csr readonly user token", ti.csr_readonly_token, null,
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_DESTINATIONID_NOT_UUID },
				{ "Get specified destination with invalid id in direct org", ti.direct_org1_user1_token, null,
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_DESTINATIONID_NOT_UUID },
				{ "Get specified destination with invalid id in msp org", ti.root_msp_org1_user1_token, null,
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_DESTINATIONID_NOT_UUID },
				{ "Get specified destination with invalid id in sub org", ti.root_msp1_suborg1_user1_token, null,
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_DESTINATIONID_NOT_UUID },
				{ "Get specified destination with invalid id in sub org with msp_account_admin token",
						ti.root_msp_org1_msp_accountadmin1_token, null, SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_DESTINATIONID_NOT_UUID },

				// 403 cases
				{ "Get specified destination usage of direct org with msp_token", ti.root_msp_org1_user1_token,
						direct_baas_destionation_ID, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get specified destination usage of direct org with suborg token", ti.root_msp1_suborg1_user1_token,
						direct_baas_destionation_ID, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get specified destination usage of direct org with msp_account_admin token",
						ti.root_msp_org1_msp_accountadmin1_token, direct_baas_destionation_ID,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get specified destination usage of direct org with sub msp_token", ti.root_msp1_submsp1_user1_token,
						direct_baas_destionation_ID, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get specified destination usage of direct org with sub msp suborg token",
						ti.msp1_submsp1_suborg1_user1_token, direct_baas_destionation_ID,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get specified destination usage of direct org with sub msp_account_admin token",
						ti.root_msp1_submsp1_account_admin_token, direct_baas_destionation_ID,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get specified destination usage of sub org with direct user token", ti.direct_org1_user1_token,
						sub_orga_baas_destionation_ID, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get specified destination usage of sub org with msp org2 user token", ti.root_msp_org2_user1_token,
						sub_orga_baas_destionation_ID, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get specified destination usage of sub org with sub msp org user token",
						ti.root_msp1_submsp1_user1_token, sub_orga_baas_destionation_ID,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get specified destination usage of sub org with sub msp sub org user token",
						ti.msp1_submsp1_suborg1_user1_token, sub_orga_baas_destionation_ID,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get specified destination usage of sub org with sub msp org account admin user token",
						ti.root_msp1_submsp1_account_admin_token, sub_orga_baas_destionation_ID,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get specified destination usage of sub msp sub org with direct user token",
						ti.direct_org1_user1_token, submsp_suborg_baas_destionation_ID,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get specified destination usage of sub msp sub org with msp org2 user token",
						ti.root_msp_org2_user1_token, submsp_suborg_baas_destionation_ID,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get specified destination usage of sub msp sub org with sub msp org2 user token",
						ti.root_msp1_submsp2_user1_token, submsp_suborg_baas_destionation_ID,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get specified destination usage of sub msp sub org with sub org user token",
						ti.root_msp1_suborg1_user1_token, submsp_suborg_baas_destionation_ID,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get specified destination usage of sub msp sub org with root msp org account admin user token",
						ti.root_msp_org1_msp_accountadmin1_token, submsp_suborg_baas_destionation_ID,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },

				// 404 cases
				{ "Get specified destination with id that does not exist with csr token", ti.csr_token,
						UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST,
						SpogMessageCode.DESTINATION_NOT_FOUND },
				{ "Get specified destination with id that does not exist with csr readonly user token",
						ti.csr_readonly_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST,
						SpogMessageCode.DESTINATION_NOT_FOUND },
				{ "Get specified destination with id that does not exist in direct org", ti.direct_org1_user1_token,
						UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST,
						SpogMessageCode.DESTINATION_NOT_FOUND },
				{ "Get specified destination with id that does not exist in msp org", ti.root_msp_org1_user1_token,
						UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST,
						SpogMessageCode.DESTINATION_NOT_FOUND },
				{ "Get specified destination with id that does not exist in sub org", ti.root_msp1_suborg1_user1_token,
						UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST,
						SpogMessageCode.DESTINATION_NOT_FOUND },
				{ "Get specified destination with id that does not exist in sub org with msp_account_admin token",
						ti.root_msp_org1_msp_accountadmin1_token, UUID.randomUUID().toString(),
						SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.DESTINATION_NOT_FOUND },

		};
	}

	@Test(dataProvider = "DestinationdataInvalid")
	public void getDestinationUsageById_401_403_404(String caseType, String token, String destination_id,
			int expectedStatusCode, SpogMessageCode expectedErrorMessage) {

		String additionalURL = null;

		// creating the test object for the log information
		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Rakesh.Chalamala");

		// Get the Destination By ID
		test.log(LogStatus.INFO, caseType);
		additionalURL = spogServer.PrepareURL("", "", 0, 0, test);
		response = spogDestinationServer.getDestinationUsageCount(token, destination_id, additionalURL, test);
		spogDestinationServer.checkgetDestinationUsage(response, destination_id, "", expectedStatusCode,
				expectedErrorMessage, test);

	}

	// passing the information to the BQ
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
		// ending test
		// endTest(logger) : It ends the current test and prepares to create HTML report
		rep.endTest(test);
		// rep.flush();
	}
}