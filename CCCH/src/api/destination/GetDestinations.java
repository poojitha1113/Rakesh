package api.destination;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.joda.time.LocalDate;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.ConnectionStatus;
import Constants.DestinationStatus;
import Constants.OSMajor;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import Constants.volume_type;
import InvokerServer.GatewayServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import dataPreparation.JsonPreparation;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class GetDestinations extends base.prepare.Is4Org {

	public SPOGServer spogServer;
	private GetDestinations destination;
	private SPOGDestinationServer spogDestinationServer;
	private Policy4SPOGServer policy4SpogServer;
	private GatewayServer gatewayServer;
	private JsonPreparation jp;

	private ExtentTest test;
	private TestOrgInfo ti;

	// For storing the retention,cloud_direct_vloume information

	HashMap<String, String> retention = new HashMap<String, String>();
	HashMap<String, Object> cloud_direct_volume = new HashMap<String, Object>();

	HashMap<String, Object> cloud_hybrid_store = new HashMap<String, Object>();

	// Related to the
	// direct_destination/msp_destination/csr_destination/sub_org_destination
	ArrayList<HashMap<String, Object>> direct_Destination_information = new ArrayList<HashMap<String, Object>>();
	private ArrayList<HashMap<String, Object>> msp_Destination_information = new ArrayList<HashMap<String, Object>>();

	private ArrayList<HashMap<String, Object>> csr_Destination_information = new ArrayList<HashMap<String, Object>>();
	private ArrayList<HashMap<String, Object>> sub_Destination_information = new ArrayList<HashMap<String, Object>>();
	private ArrayList<HashMap<String, Object>> sub_mspDestination_information = new ArrayList<HashMap<String, Object>>();

	HashMap<String, Object> cloud_info_direct = new HashMap<String, Object>();
	HashMap<String, Object> cloud_info_dedupe = new HashMap<String, Object>();

	private UserSpogServer userSpogServer;
	ArrayList<String> available_actions = new ArrayList<String>();

	// Sorting based on the create_ts feild
	LocalDate date = LocalDate.now();
	LocalDate yesterday = date.minusDays(1);
	LocalDate tomorrow = date.plusDays(1);
	boolean flag = true;
	private String job_Type = "backup,backup,copy,merge";
	private String job_Status = "finished";
	private String job_Method = "full,incremental,resync";
	public String JobSeverity = "success,information";

	private int total_size;
	private String direct_cloud_account_id, msp_cloud_account_id, root_cloud_id;

	private String org_model_prefix = this.getClass().getSimpleName();
	private Response response;

	@BeforeClass
	@Parameters({ "baseURI", "port", "csrAdminUserName", "csrAdminPassword", "csrReadOnlyAdminName",
		"csrReadOnlyAdminPassword", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword,
			String readOnlyUserName, String readOnlyPassword, String logFolder, String runningMachine,
			String buildVersion) throws UnknownHostException {

		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		destination = new GetDestinations();
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		policy4SpogServer = new Policy4SPOGServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		jp = new JsonPreparation();
		rep = ExtentManager.getInstance("GetDestinations", logFolder);
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
			creationTime = System.currentTimeMillis();
			count1.setcreationtime(creationTime);
			try {
				bqdb1.updateTable(this.BQName, runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0",
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

		// Composing a HashMap for the destinations Retentions of the cloud
		// direct volumes
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

		ti = new TestOrgInfo(spogServer, test);

		// compose available actions
		available_actions.add("edit");
		available_actions.add("view_recovery_point");
		available_actions.add("delete");
		/*
		 * available_actions.add("start"); available_actions.add("rps_jumpstart");
		 */

		// get direct cloud account
		Response response = spogServer.getCloudAccounts(ti.direct_org1_user1_token, "", test);
		direct_cloud_account_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");

		// get MSP cloud account
		response = spogServer.getCloudAccounts(ti.normal_msp_org1_user1_token, "", test);
		msp_cloud_account_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");

		response = spogServer.getCloudAccounts(ti.root_msp_org1_user1_token, "", test);
		root_cloud_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");

	}

	// This information is related to the destination information of the
	// cloudDedupe volume
	@DataProvider(name = "destinationinfocloudDedupe")
	public final Object[][] getDestination() {
		return new Object[][] {
				{ ti.direct_org1_user1_email, ti.common_password, ti.direct_org1_user1_token, ti.direct_org1_name,
						ti.direct_org1_id, ti.direct_org1_user1_id, direct_cloud_account_id, "cloud_hybrid_store",
						"d:\\ds\\0", "d:\\ds\\1", "d:\\ds\\3", "d:\\ds\\2", 5, true, 512, "256", false, "", "300",
						"200", 0.5, 0.5, DestinationStatus.creating.toString(), "destDedup_dest", "direct",
						direct_cloud_account_id, System.currentTimeMillis() / 1000, System.currentTimeMillis() / 1000,
						ti.direct_org1_user1_token, "finished", "1", 30.20, "3", "02", "2", "3", "3", "3" },
				{ ti.normal_msp1_suborg1_user1_email, ti.common_password, ti.normal_msp_org1_user1_token,
						ti.normal_msp1_suborg1_name, ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_id,
						msp_cloud_account_id, "cloud_hybrid_store", "e:\\dsp\\0", "f:\\ds\\1", "f:\\ds\\3", "f:\\ds\\2",
						45, true, 128, "256", true, "456", "355", "400", 0.565, 0.465,
						DestinationStatus.running.toString(), "account_test_deduper", "suborg",
						UUID.randomUUID().toString(), System.currentTimeMillis() / 1000,
						System.currentTimeMillis() / 1000, ti.normal_msp_org1_user1_token, "finished", "5", 24.45, "5",
						"6", "5", "5", "7", "4" },
				// 3 tier cases
				{ ti.root_msp1_suborg1_user1_email, ti.common_password, ti.root_msp_org1_user1_token,
						ti.root_msp1_suborg1_name, ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_id,
						root_cloud_id, "cloud_hybrid_store", "e:\\dsp\\0", "f:\\ds\\1", "f:\\ds\\3", "f:\\ds\\2", 45,
						true, 128, "256", true, "456", "355", "400", 0.565, 0.465, DestinationStatus.running.toString(),
						"account_test_deduper", "rootsub", UUID.randomUUID().toString(),
						System.currentTimeMillis() / 1000, System.currentTimeMillis() / 1000,
						ti.root_msp_org1_user1_token, "finished", "5", 24.45, "5", "6", "5", "5", "7", "4" },
				{ ti.msp1_submsp1_suborg1_user1_email, ti.common_password, ti.root_msp1_submsp1_user1_token,
						ti.msp1_submsp1_sub_org1_name, ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_id,
						root_cloud_id, "cloud_hybrid_store", "e:\\dsp\\0", "f:\\ds\\1", "f:\\ds\\3", "f:\\ds\\2", 45,
						true, 128, "256", true, "456", "355", "400", 0.565, 0.465, DestinationStatus.running.toString(),
						"account_test_deduper", "submsp_sub", UUID.randomUUID().toString(),
						System.currentTimeMillis() / 1000, System.currentTimeMillis() / 1000,
						ti.root_msp1_submsp1_user1_token, "finished", "5", 24.45, "5", "6", "5", "5", "7", "4" }, };
	}

	@Test(dataProvider = "destinationinfocloudDedupe")
	public void PostDestinationsByIdcloudHybrid(String userName, String password, String adminToken,
			String organization_name, String organization_id, String create_user_id, String site_id,
			String DestinationType, String data_store_folder, String data_destination, String index_destination,
			String hash_destination, int concurrent_active_node, boolean Is_deduplicated, int block_size,
			String hash_memory, boolean is_compressed, String encryption_password, String occupied_space,
			String stored_data, Double deduplication_rate, Double compression_rate, String destination_status,
			String destination_name, String orgType, String cloud_account_id, Long start_time_ts, Long endTimeTS,
			String site_Token, String status, String job_seq, Double percent_complete, String protected_data_size,
			String raw_data_size, String sync_read_size, String ntfs_volume_size, String virtual_disk_provision_size,
			String dedupe_savings) {

		HashMap<String, ArrayList<HashMap<String, Object>>> last_job = new HashMap<String, ArrayList<HashMap<String, Object>>>();
		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Ramya.Nagepalli");

		test.log(LogStatus.INFO, "Creating a destination of type cloud_hybrid_store");
		String destination_id = spogDestinationServer.createHybridDestination(adminToken, organization_id,
				"91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", DestinationType, destination_name, "starting", test);

		test.log(LogStatus.INFO, "The value of the destination_id:" + destination_id);
		total_size++;
		test.log(LogStatus.INFO, "The value of the destination_id:" + destination_id);
		String rps_id = UUID.randomUUID().toString();
		String policy_id = UUID.randomUUID().toString();
		String sourceName = spogServer.ReturnRandom("Ramya");
		test.log(LogStatus.INFO, "create source");
		spogServer.setToken(adminToken);
		String job_type[] = job_Type.split(",");
		String job_method[] = job_Method.split(",");
		String job_severity[] = JobSeverity.split(",");
		String source_id = spogServer.createSourceWithCheck(sourceName, SourceType.machine, SourceProduct.udp,
				organization_id, site_id, ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(),
				"", test);
		for (int i = 0; i < 2; i++) {
			start_time_ts = start_time_ts + i * 10 + 1;
			endTimeTS = endTimeTS + i * 10 + 200;
			String job_id = gatewayServer.postJobWithCheck(start_time_ts, endTimeTS, organization_id, source_id,
					source_id, rps_id, destination_id, policy_id, job_type[i], job_method[i], status, site_Token, test);
			/*gatewayServer.postJobDataWithCheck(job_id, job_seq, job_severity[i], percent_complete, protected_data_size,
					raw_data_size, sync_read_size, ntfs_volume_size, virtual_disk_provision_size, site_Token, test);
			last_job = spogDestinationServer.composeLastJob(start_time_ts, endTimeTS, percent_complete, status,
					job_type[i], job_method[i]);*/
		}
		// Composing the HashMap For cloud_dedupe dataStore
		HashMap<String, Object> cloud_info_dedupe = jp.getDestinationInfo();
		cloud_info_dedupe.put("destination_id", destination_id);
		cloud_info_dedupe.put("organization_id", organization_id);
		cloud_info_dedupe.put("destination_type", DestinationType);
		cloud_info_dedupe.put("destination_id", destination_id);
		cloud_info_dedupe.put("organization_name", organization_name);
		cloud_info_dedupe.put("last_job", spogServer.getLastJob(last_job));
		if (orgType.equalsIgnoreCase("direct")) {
			direct_Destination_information.add(cloud_info_dedupe);
			csr_Destination_information.add(cloud_info_dedupe);
		} else if (orgType.equalsIgnoreCase("msp")) {
			msp_Destination_information.add(cloud_info_dedupe);
			csr_Destination_information.add(cloud_info_dedupe);
			sub_mspDestination_information.add(cloud_info_dedupe);
		} else if (orgType.equalsIgnoreCase("suborg")) {
			sub_Destination_information.add(cloud_info_dedupe);
			sub_mspDestination_information.add(cloud_info_dedupe);
			// csr_Destination_information.add(cloud_info_dedupe);
		}
	}

	// This information is related to the valid
	// JWTTokenForCloudDirectInformation
	@DataProvider(name = "destinationinfocloudDirect1")
	public final Object[][] getDestinatonInfo1() {
		return new Object[][] {
				{ ti.direct_org1_user1_email, ti.common_password, ti.direct_org1_user1_token, ti.direct_org1_name,
						ti.direct_org1_user1_id, "cloudAccountKey", "cloudAccountSecret", "CloudAccountData",
						"cloud_direct", ti.direct_org1_id, "cloud_direct_volume", DestinationStatus.running.toString(),
						direct_cloud_account_id, "direct_destination", "destination_direct_volume", "6M", "6 Months",
						"0", "0", "0", "0", "0", "0", 23.0, 23.0, 46.0, volume_type.normal.toString(), "BHAGHA-PCW10",
						"91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", direct_cloud_account_id, "direct",
						System.currentTimeMillis() / 1000, System.currentTimeMillis() / 1000,
						ti.direct_org1_user1_token, "finished", "1", 0.0, "3000", "100", "100", "100", "200", "10" },
				{ ti.normal_msp1_suborg1_user1_email, ti.common_password, ti.normal_msp_org1_user1_token,
						ti.normal_msp1_suborg1_name, "cloudAccountKey", "cloudAccountSecret",
						ti.normal_msp1_suborg1_user1_id, "CloudAccountData", "cloud_direct", ti.normal_msp1_suborg1_id,
						"cloud_direct_volume", DestinationStatus.running.toString(), msp_cloud_account_id, "dest951",
						"account_destination", "forever", "Forever", "0", "0", "0", "0", "0", "0", 26.9, 24.8, 51.1,
						volume_type.normal.toString(), "KRISHLAP_1", "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c",
						UUID.randomUUID().toString(), "suborg", System.currentTimeMillis() / 1000,
						System.currentTimeMillis() / 1000, ti.normal_msp1_suborg1_user1_token, "finished", "5", 24.26,
						"435", "456", "55", "345", "567", "19" },

				// 3 tier cases

				{ ti.root_msp1_suborg1_user1_email, ti.common_password, ti.root_msp_org1_user1_token,
						ti.root_msp1_suborg1_name, "cloudAccountKey", "cloudAccountSecret",
						ti.root_msp1_suborg1_user1_id, "CloudAccountData", "cloud_direct", ti.root_msp1_suborg1_id,
						"cloud_direct_volume", DestinationStatus.running.toString(), root_cloud_id, "dest951",
						"account_destination", "forever", "Forever", "0", "0", "0", "0", "0", "0", 26.9, 24.8, 51.1,
						volume_type.normal.toString(), "KRISHLAP_1", "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c",
						UUID.randomUUID().toString(), "rootsub", System.currentTimeMillis() / 1000,
						System.currentTimeMillis() / 1000, ti.root_msp1_suborg1_user1_token, "finished", "5", 24.26,
						"435", "456", "55", "345", "567", "19" },

				{ ti.msp1_submsp1_suborg1_user1_email, ti.common_password, ti.root_msp1_submsp1_user1_token,
						ti.msp1_submsp1_sub_org1_name, "cloudAccountKey", "cloudAccountSecret",
						ti.msp1_submsp1_suborg1_user1_id, "CloudAccountData", "cloud_direct",
						ti.msp1_submsp1_sub_org1_id, "cloud_direct_volume", DestinationStatus.running.toString(),
						root_cloud_id, "dest951", "account_destination", "forever", "Forever", "0", "0", "0", "0", "0",
						"0", 26.9, 24.8, 51.1, volume_type.normal.toString(), "KRISHLAP_1",
						"91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", UUID.randomUUID().toString(), "submsp_sub",
						System.currentTimeMillis() / 1000, System.currentTimeMillis() / 1000,
						ti.root_msp1_submsp1_user1_token, "finished", "5", 24.26, "435", "456", "55", "345", "567",
						"19" },

		};
	}

	@Test(dataProvider = "destinationinfocloudDirect1")
	public String PostDestinationsByIdcloudDirect(String userName, String password, String adminToken,
			String organization_name, String create_user_id, String cloudAccountKey, String cloudAccountSecret,
			String cloudAccountName, String cloudAccountType, String organization_id, String DestinationType,
			String destination_status, String site_id, String destination_name, String cloud_direct_volume_name,
			String retention_id, String retention_name, String age_hours_max, String age_four_hours_max,
			String age_days_max, String age_weeks_max, String age_months_max, String age_years_max,
			Double primary_usage, Double snapshot_usage, Double total_usage, String volume_type, String hostname,
			String datacenter_id, String cloud_account_id, String orgType, Long start_time_ts, Long endTimeTS,
			String site_Token, String status, String job_seq, Double percent_complete, String protected_data_size,
			String raw_data_size, String sync_read_size, String ntfs_volume_size, String virtual_disk_provision_size,
			String dedupe_savings) {

		HashMap<String, ArrayList<HashMap<String, Object>>> last_job = new HashMap<String, ArrayList<HashMap<String, Object>>>();
		// creating the test object for the log information
		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Ramya.Nagepalli");

		if (flag) {
			/*
			 * spogDestinationServer.recycleCloudVolumesAndDelOrg(
			 * "b2b0b531-5abe-427d-b7cf-e7dfe9876952", test);
			 * spogDestinationServer.recycleCloudVolumesAndDelOrg(
			 * "d1e2142c-dd26-41e5-b2e1-42fb278b34a3", test);
			 */ // Store the data for csr_logs before validation
			response = spogDestinationServer.getDestinations(ti.csr_token, "", test);
			flag = false;
		}
		// creating a destination
		test.log(LogStatus.INFO, "creating a destination of type :" + DestinationType);
		retention = spogDestinationServer.composeRetention(age_hours_max, age_four_hours_max, age_days_max,
				age_weeks_max, age_months_max, age_years_max);
		cloud_direct_volume = spogDestinationServer.composeCloudDirectInfo(cloud_account_id, cloud_direct_volume_name,
				retention_id, retention_name, primary_usage, snapshot_usage, total_usage, volume_type, hostname,
				retention);
		test.log(LogStatus.INFO, "Creating a destination of type cloud_direct_volume");
		Response response = spogDestinationServer.getDestinations(adminToken, "organization_id=" + organization_id,
				test);/*
						 * createDestination(UUID.randomUUID().toString(), adminToken, cloud_account_id,
						 * organization_id, site_id, datacenter_id, dedupe_savings, DestinationType,
						 * destination_status, destination_name, cloud_direct_volume, test);
						 */

		String destination_id = response.then().extract().path("data.destination_id");
		String policy_id = UUID.randomUUID().toString();

		// Creating source
		String protectionStatus = "protect, unprotect,protect, unprotect";
		String connectionStatus = "online, offline,online, offline";
		String osmajor = "windows,linux,mac,unknown";

		String[] protection = protectionStatus.split(",");
		String[] connection = connectionStatus.split(",");
		String[] os = osmajor.split(",");

		String source_id = null;
		String source_name = null;

		String schedule_id = UUID.randomUUID().toString();
		String task_id = UUID.randomUUID().toString();
		String throttle_id = UUID.randomUUID().toString();
		// create schedule for policy
		test.log(LogStatus.INFO, "Create custom settings");
		HashMap<String, Object> customScheduleSettingDTO = policy4SpogServer.createCustomScheduleDTO("1522068700422",
				"full", "1", "true", "10", "minutes", test);

		HashMap<String, Object> scheduleSettingDTO = policy4SpogServer.createScheduleSettingDTO(null,
				customScheduleSettingDTO, null, null, test);

		test.log(LogStatus.INFO, "Create cloud direct schedule");

		HashMap<String, Object> cloudDirectScheduleDTO = policy4SpogServer.createCloudDirectScheduleDTO("0 6 * * *",
				test);

		test.log(LogStatus.INFO, "Create schedule settings");
		scheduleSettingDTO = policy4SpogServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null, null, null, test);

		ArrayList<HashMap<String, Object>> schedules = policy4SpogServer.createPolicyScheduleDTO(null, schedule_id,
				"1d", task_id, destination_id, scheduleSettingDTO, "06:00", "12:00", "cloud_direct_file_folder_backup",
				destination_name, test);

		test.log(LogStatus.INFO, "Exclude the folders from cloud direct file backup");

		ArrayList<HashMap<String, Object>> excludes = policy4SpogServer.createExcludeInfoDTO(null, "path", "c:\\tmp",
				test);

		HashMap<String, Object> cloudDirectLocalBackupDTO = policy4SpogServer.createCloudDirectLocalBackupDTO("c:\\tmp",
				"true", test);

		HashMap<String, Object> cloudDirectFileBackupTaskInfoDTO = policy4SpogServer
				.createCloudDirectFileBackupTaskInfoDTO("d:\\tmp", cloudDirectLocalBackupDTO, excludes, test);
		HashMap<String, Object> perform_ar_test = policy4SpogServer.createPerformARTestOption("1", "1", "1", "1", test);

		HashMap<String, Object> retention_policy = policy4SpogServer.createRetentionPolicyOption("2", "2", "2", "2",
				test);

		HashMap<String, Object> udp_replication_from_remote_DTO = policy4SpogServer
				.createUdpReplicationFromRemoteInfoDTO(perform_ar_test, retention_policy, test);
		/*
		 * ArrayList<HashMap<String,Object>> schedules
		 * =policy4SpogServer.createPolicyScheduleDTO(null,schedule_id, "custom",
		 * task_id, destination_hybrid_id, scheduleSettingDTO, "06:00",
		 * "12:00","cloud_direct_file_folder_backup" ,destination_name,test);
		 */
		ArrayList<HashMap<String, Object>> destinations = policy4SpogServer.createPolicyTaskDTO(null, task_id,
				"cloud_direct_file_folder_backup", destination_id, "none", null, cloudDirectFileBackupTaskInfoDTO, null,
				test);
		ArrayList<HashMap<String, Object>> throttles = policy4SpogServer.createPolicyThrottleDTO(null, throttle_id,
				task_id, "network", "1200", "1", "06:00", "18:00", "cloud_direct_file_folder_backup", destination_id,
				destination_name, test);

		policy4SpogServer.setToken(adminToken);
		// create cloud direct policy
		String policy_name = spogServer.ReturnRandom("ramya");
		response = policy4SpogServer.createPolicy(policy_name, policy_name, "cloud_direct_baas", null, "true",
				source_id, destinations, schedules, throttles, UUID.randomUUID().toString(), organization_id, test);

		policy_id = response.then().extract().path("data.policy_id").toString();

		test.log(LogStatus.INFO, "The value of the destination_id:" + destination_id);
		String rps_id = UUID.randomUUID().toString();

		String sourceName = spogServer.ReturnRandom("Ramya");
		test.log(LogStatus.INFO, "create source");
		spogServer.setToken(adminToken);
		String job_type[] = job_Type.split(",");
		String job_method[] = job_Method.split(",");
		String job_severity[] = JobSeverity.split(",");
		spogServer.setToken(ti.direct_org1_user1_token);
		source_id = spogServer.createSourceWithCheck(sourceName, SourceType.machine, SourceProduct.cloud_direct,
				organization_id, cloud_account_id, ProtectionStatus.protect, ConnectionStatus.online,
				OSMajor.windows.name(), "", test);
		for (int i = 0; i < 2; i++) {
			start_time_ts = start_time_ts + i * 10 + 1;
			endTimeTS = endTimeTS + i * 10 + 200;
			String job_id = gatewayServer.postJobWithCheck(start_time_ts, endTimeTS, organization_id, source_id,
					source_id, rps_id, destination_id, policy_id, job_type[i], job_method[i], status, site_Token, test);
			gatewayServer.postJobDataWithCheck(job_id, job_seq, job_severity[i], percent_complete, protected_data_size,
					raw_data_size, sync_read_size, ntfs_volume_size, virtual_disk_provision_size, site_Token, test);
			// compose the last_job info
			last_job = spogDestinationServer.composeLastJob(start_time_ts, endTimeTS, percent_complete, status,
					job_type[i], job_method[i]);
		}
		// Composing the HashMap For cloud_direct_volume dataStore
		HashMap<String, Object> cloud_info_direct = jp.getDestinationInfo();
		cloud_info_direct.put("destination_id", destination_id);
		cloud_info_direct.put("organization_id", organization_id);
		cloud_info_direct.put("destination_type", DestinationType);
		cloud_info_direct.put("organization_name", organization_name);
		cloud_info_direct.put("last_job", spogServer.getLastJob(last_job));

		total_size++;
		if (orgType.equalsIgnoreCase("direct")) {
			direct_Destination_information.add(cloud_info_direct);
			csr_Destination_information.add(cloud_info_direct);
		} else if (orgType.equalsIgnoreCase("msp")) {
			msp_Destination_information.add(cloud_info_direct);
			sub_mspDestination_information.add(cloud_info_direct);
			csr_Destination_information.add(cloud_info_direct);
		} else if (orgType.equalsIgnoreCase("suborg")) {
			sub_Destination_information.add(cloud_info_direct);
			sub_mspDestination_information.add(cloud_info_direct);
			// csr_Destination_information.add(cloud_info_direct);
		}
		policy4SpogServer.deletePolicybyPolicyId(adminToken, policy_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null,
				test);
		return policy_id;
	}

	// get destinations minimum length 3

	@DataProvider(name = "getDestinationsSearchInfo")
	public final Object[][] getDestinationsSearchFilters() {
		return new Object[][] {
				{ ti.direct_org1_user1_token, "direct", 1, 7, "destination_name;=;dest;", "log_ts;desc",
						direct_Destination_information },
				{ ti.normal_msp_org1_user1_token, "msp", 1, 7, "destination_name;=;de;", "log_ts;desc",
						msp_Destination_information },
				{ ti.normal_msp_org1_user1_token, "suborg", 1, 7, "destination_name;=;dess;", "log_ts;desc",
						sub_mspDestination_information },
				{ ti.normal_msp_org1_msp_accountadmin1_token, "suborg", 1, 7, "destination_name;=;dess;", "log_ts;desc",
						sub_mspDestination_information }, };
	}

	@Test(dependsOnMethods = "PostDestinationsByIdcloudHybrid", dataProvider = "getDestinationsSearchInfo")
	public void GetDestinationsSearchDestinationName(String admin_Token, String OrgType, int page_number, int page_size,
			String filterStr, String SortStr, ArrayList<HashMap<String, Object>> destinationInfo) {
		test = ExtentManager
				.getNewTest(this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName()
						+ "_" + OrgType + "_" + filterStr + "_" + page_number + "_" + page_size);
		test.assignAuthor("Ramya.Nagepalli");
		String[] filterArray = null;
		String filterName = null, filterOperator = null, filterValue = null;
		if (filterStr != "" && filterStr != null) {
			filterArray = filterStr.split(";");
			filterName = filterArray[0];
			filterOperator = filterArray[1];
			filterValue = filterArray[2];
			destinationInfo = spogServer.fuzzySearchString(destinationInfo, filterName, filterOperator, filterValue,
					test, "destinations");
		}
		String additionalURL = spogServer.PrepareURL(filterStr, SortStr, page_number, page_size, test);
		Response response = spogDestinationServer.getDestinations(admin_Token, additionalURL, test);
		if (OrgType.equalsIgnoreCase("direct")) {
			spogDestinationServer.checkGetDestinations(response, available_actions,
					SpogConstants.SUCCESS_GET_PUT_DELETE, destinationInfo, page_number, page_size, filterStr, SortStr,
					SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

		} else if (OrgType.equalsIgnoreCase("msp")) {
			spogDestinationServer.checkGetDestinations(response, available_actions,
					SpogConstants.SUCCESS_GET_PUT_DELETE, destinationInfo, page_number, page_size, filterStr, SortStr,
					SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		} else if (OrgType.equalsIgnoreCase("subOrg")) {
			spogDestinationServer.checkGetDestinations(response, available_actions,
					SpogConstants.SUCCESS_GET_PUT_DELETE, destinationInfo, page_number, page_size, filterStr, SortStr,
					SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		}
	}

	// Get the destinations for valid cases
	@DataProvider(name = "getDestinaitonInfo1")
	public final Object[][] getDestinationInfo() {
		return new Object[][] {
				{ ti.direct_org1_user1_token, "direct", 1, 4, "", "create_ts;desc", direct_Destination_information },
				{ ti.direct_org1_user1_token, "direct", 1, 3, "", "", direct_Destination_information },
				// {ti.normal_msp_org1_user1_token,"msp",1,2,"","create_ts;asc",msp_Destination_information},
				{ ti.normal_msp_org1_msp_accountadmin1_token, "suborg", 1, 3, "", "", sub_mspDestination_information },
				{ ti.normal_msp1_suborg1_user1_token, "suborg", 1, 3, "", "create_ts;desc",
						sub_Destination_information },
				{ ti.normal_msp_org1_user1_token, "suborg", 1, 3, "", "", sub_Destination_information },
				/*
				 * {ti.csr_token,"csr",1,20,"","",csr_Destination_information},
				 * {ti.csr_token,"csr",1,20,"","create_ts;desc", csr_Destination_information}
				 */ };
	}

	@Test(dependsOnMethods = "PostDestinationsByIdcloudHybrid", dataProvider = "getDestinaitonInfo1")
	public void GetDestinationsForValidCasesUsingSortPagination(String admin_Token, String OrgType, int page_number,
			int page_size, String filterStr, String SortStr, ArrayList<HashMap<String, Object>> Destinationinfo) {

		// creating the test object for the log information
		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Ramya.Nagepalli");
		test.log(LogStatus.INFO, "Call the rest API for get Destinations");
		String additionalURL = spogServer.PrepareURL(filterStr, SortStr, page_number, page_size, test);
		Response response = spogDestinationServer.getDestinations(admin_Token, additionalURL, test);
		if (OrgType.equalsIgnoreCase("direct")) {
			test.log(LogStatus.INFO, "validating the information for the direct organization");
			spogDestinationServer.checkGetDestinations(response, available_actions,
					SpogConstants.SUCCESS_GET_PUT_DELETE, Destinationinfo, page_number, page_size, filterStr, SortStr,
					SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		} else if (OrgType.equalsIgnoreCase("msp")) {
			test.log(LogStatus.INFO, "validating the information for the msp and sub organization");
			spogDestinationServer.checkGetDestinations(response, available_actions,
					SpogConstants.SUCCESS_GET_PUT_DELETE, Destinationinfo, page_number, page_size, filterStr, SortStr,
					SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		} else if (OrgType.equalsIgnoreCase("suborg")) {
			test.log(LogStatus.INFO, "validating the information for the sub organization");
			spogDestinationServer.checkGetDestinations(response, available_actions,
					SpogConstants.SUCCESS_GET_PUT_DELETE, Destinationinfo, page_number, page_size, filterStr, SortStr,
					SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		} else if (OrgType.equalsIgnoreCase("csr")) {
			test.log(LogStatus.INFO,
					"validating the information for the direct msp and sub organization(csr organization)");
			int new_size = response.then().extract().path("pagination.total_size");
			if (new_size == total_size) {
				test.log(LogStatus.INFO, "The total_size has been matched successfully");
			} else {
				test.log(LogStatus.INFO, "The total_size has not matched successfully");
			}
		}

	}

	// Get the destinations for valid cases using Filtering
	@DataProvider(name = "getDestinaitonInfo5")
	public final Object[][] getDestinationInfoForFiltering() {
		return new Object[][] {
				{ ti.direct_org1_user1_token, "direct", 1, 3, "destination_type;=;cloud_direct_volume",
						"create_ts;desc", direct_Destination_information },
				{ ti.direct_org1_user1_token, "direct", 1, 3, "destination_type;=;cloud_hybrid_store", "create_ts;desc",
						direct_Destination_information },
				{ ti.csr_readonly_token, "csrreadonly-direct", 1, 3, "destination_type;=;cloud_hybrid_store",
						"create_ts;desc", direct_Destination_information },
				/*
				 * {ti.normal_msp_org1_user1_token,"msp",2,1,
				 * "destination_type;=;cloud_direct_volume","create_ts;desc",
				 * sub_mspDestination_information}, {ti.normal_msp_org1_user1_token,"msp",1,3,
				 * "destination_type;=;cloud_hybrid_store","create_ts;asc",
				 * sub_mspDestination_information},
				 */ };
	}

	@Test(dependsOnMethods = "PostDestinationsByIdcloudHybrid", dataProvider = "getDestinaitonInfo5")
	public void GetDestinationsForValidCasesUsingSortPaginationFiltering(String admin_Token, String OrgType,
			int page_number, int page_size, String filterStr, String SortStr,
			ArrayList<HashMap<String, Object>> Destinationinfo) {
		// creating the test object for the log information
		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Ramya.Nagepalli");
		String[] filterArray = null;
		String filterName = null, filterOperator = null, filterValue = null;
		if (filterStr != "" && filterStr != null && !(filterStr.contains("&"))) {
			filterArray = filterStr.split(";");
			filterName = filterArray[0];
			filterOperator = filterArray[1];
			filterValue = filterArray[2];
		}
		String newFilterStr = filterStr;
		if (filterStr != "" && filterStr != null) {
			ArrayList<HashMap<String, Object>> Destination_info = new ArrayList<>();
			if (filterName.equalsIgnoreCase("organization_id")) {
				if (filterOperator.equals("=")) {
					if (filterValue.equals(SpogConstants.DIRECT_ORG)) {
						newFilterStr = filterName + ";" + filterOperator + ";" + ti.direct_org1_id;
					} else if (filterValue.equals(SpogConstants.MSP_ORG)) {
						newFilterStr = filterName + ";" + filterOperator + ";" + ti.normal_msp_org1_id;
					} else {
						newFilterStr = filterName + ";" + filterOperator + ";" + ti.normal_msp1_suborg1_id;
					}
				} else {
					// in
					newFilterStr = filterName + ";" + filterOperator + ";";
					if (filterValue.contains(SpogConstants.DIRECT_ORG) && filterValue.contains(SpogConstants.MSP_ORG)
							&& filterValue.contains(SpogConstants.MSP_SUB_ORG)) {
						newFilterStr += ti.direct_org1_id + "|" + ti.normal_msp_org1_id + "|"
								+ ti.normal_msp1_suborg1_id;
					} else if (filterValue.contains(SpogConstants.DIRECT_ORG)
							&& filterValue.contains(SpogConstants.MSP_ORG)) {
						newFilterStr += ti.direct_org1_id + "|" + ti.normal_msp_org1_id;
					} else {
						newFilterStr += ti.normal_msp_org1_id + "|" + ti.normal_msp1_suborg1_id;
					}
				}
			} else if (filterOperator.equals("=")
					&& (filterName.contains("destination_type") || filterName.contains("destination_status"))) {
				// FilerOperator based on the Logs info
				for (int k = 0; k < Destinationinfo.size(); k++) {
					if (filterStr.contains("destination_type")) {
						if (Destinationinfo.get(k).get(filterName).equals(filterValue)) {
							Destination_info.add(Destinationinfo.get(k));
						}
					} else if (filterStr.contains("destination_status")) {

						if (Destinationinfo.get(k).get(filterName).equals(filterValue)) {
							Destination_info.add(Destinationinfo.get(k));
						}
					}
				}
				Destinationinfo = Destination_info;
			} else if (filterOperator.equals("in")
					&& (filterName.contains("destination_type") || filterName.contains("destination_status"))) {
				// Filter based on the array of logType
				String newFilterValue = filterValue.replace("|", ",");
				for (int k = 0; k < Destinationinfo.size(); k++) {
					String filterValues[] = newFilterValue.split(",");
					for (int i = 0; i < filterValues.length; i++) {
						if (filterStr.contains("destination_type")) {
							if (Destinationinfo.get(k).get(filterName).equals(filterValue)) {
								Destination_info.add(Destinationinfo.get(k));
							}
						} else if (filterStr.contains("destination_status")) {
							if (Destinationinfo.get(k).get(filterName).equals(filterValue)) {
								Destination_info.add(Destinationinfo.get(k));
							}
						}
					}
				}
				Destinationinfo = Destination_info;
			}
		}
		test.log(LogStatus.INFO, "Call the rest API for get Destinations");
		String additionalURL = spogServer.PrepareURL(newFilterStr, SortStr, page_number, page_size, test);
		Response response = spogDestinationServer.getDestinations(admin_Token, additionalURL, test);
		if (OrgType.equalsIgnoreCase("direct")) {
			test.log(LogStatus.INFO, "validating the information for the direct organization");
			spogDestinationServer.checkGetDestinations(response, available_actions,
					SpogConstants.SUCCESS_GET_PUT_DELETE, Destinationinfo, page_number, page_size, newFilterStr,
					SortStr, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		} else if (OrgType.equalsIgnoreCase("msp")) {
			test.log(LogStatus.INFO, "validating the information for the msp and sub organization");
			spogDestinationServer.checkGetDestinations(response, available_actions,
					SpogConstants.SUCCESS_GET_PUT_DELETE, Destinationinfo, page_number, page_size, newFilterStr,
					SortStr, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		} else if (OrgType.equalsIgnoreCase("suborg")) {
			test.log(LogStatus.INFO, "validating the information for the sub organization");
			spogDestinationServer.checkGetDestinations(response, available_actions,
					SpogConstants.SUCCESS_GET_PUT_DELETE, Destinationinfo, page_number, page_size, newFilterStr,
					SortStr, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		} else {
			test.log(LogStatus.INFO,
					"validating the information for the direct msp and sub organization(csr organization)");
			spogDestinationServer.checkGetDestinations(response, available_actions,
					SpogConstants.SUCCESS_GET_PUT_DELETE, Destinationinfo, page_number, page_size, newFilterStr,
					SortStr, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

		}
	}

	@DataProvider(name = "getDestinaitonInfo6")
	public final Object[][] DetinationsFiltering() {
		return new Object[][] {
				{ ti.csr_token, "csr", 1, 8, "destination_type;=;cloud_direct_volume&organization_id;in;direct|msp",
						"create_ts;asc", csr_Destination_information },
				{ ti.csr_token, "csr", 1, 3, "destination_type;=;cloud_hybrid_store&organization_id;in;direct|msp",
						"create_ts;desc", csr_Destination_information } };
	}

	@DataProvider(name = "getDestinaitonInfo2")
	public final Object[][] getDestinationMissingToken() {
		return new Object[][] {
				{ "", "direct", 1, 3, "", "create_ts;desc", direct_Destination_information, SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTHENTICATION_FAILED },
				{ "", "msp", 1, 3, "", "create_ts;desc", msp_Destination_information, SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTHENTICATION_FAILED },
				{ "", "suborg", 1, 3, "", "create_ts;desc", sub_Destination_information, SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTHENTICATION_FAILED },
				{ "", "csr", 1, 20, "", "", csr_Destination_information, SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTHENTICATION_FAILED },
				{ ti.direct_org1_user1_token + "junk", "direct", 1, 3, "", "create_ts;desc",
						direct_Destination_information, SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT },
				{ ti.normal_msp_org1_user1_token + "junk", "msp", 1, 3, "", "create_ts;desc",
						msp_Destination_information, SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT },
				// {ti.direct_org1_user1_token+"junk","suborg",1,3,"","create_ts;desc",sub_Destination_information,SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT},
				{ ti.csr_token + "junk", "csr", 1, 20, "", "", csr_Destination_information, SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT }, };
	}

	@Test(dependsOnMethods = "PostDestinationsByIdcloudHybrid", dataProvider = "getDestinaitonInfo2")
	public void GetDestinationsForMissingToken(String admin_Token, String OrgType, int page_number, int page_size,
			String filterStr, String SortStr, ArrayList<HashMap<String, Object>> Destinationinfo, int code,
			SpogMessageCode errorInfo) {

		// creating the test object for the log information
		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Ramya.Nagepalli");

		test.log(LogStatus.INFO, "Call the rest API for get Destinations");
		String additionalURL = spogServer.PrepareURL(filterStr, SortStr, page_number, page_size, test);
		Response response = spogDestinationServer.getDestinations(admin_Token, additionalURL, test);
		if (OrgType.equalsIgnoreCase("direct")) {
			test.log(LogStatus.INFO, "validating the information for the direct organization");
			spogDestinationServer.checkGetDestinations(response, available_actions, code, Destinationinfo, page_number,
					page_size, filterStr, SortStr, errorInfo, test);
		} else if (OrgType.equalsIgnoreCase("msp")) {
			test.log(LogStatus.INFO, "validating the information for the msp and sub organization");
			spogDestinationServer.checkGetDestinations(response, available_actions, code, Destinationinfo, page_number,
					page_size, filterStr, SortStr, errorInfo, test);
		} else if (OrgType.equalsIgnoreCase("suborg")) {
			test.log(LogStatus.INFO, "validating the information for the sub organization");
			spogDestinationServer.checkGetDestinations(response, available_actions, code, Destinationinfo, page_number,
					page_size, filterStr, SortStr, errorInfo, test);
		} else if (OrgType.equalsIgnoreCase("csr")) {
			test.log(LogStatus.INFO,
					"validating the information for the direct msp and sub organization(csr organization)");
			spogDestinationServer.checkGetDestinations(response, available_actions, code, Destinationinfo, page_number,
					page_size, filterStr, SortStr, errorInfo, test);
		}
	}

	// Invalid cases :GetLogs By using insufficientPermissions
	@DataProvider(name = "getDestinationInfo4")
	public final Object[][] getDestinationinfoByInsufficientPermissions() {
		return new Object[][] {
				{ ti.normal_msp_org1_user1_token, "direct", 1, 3, "organization_id;=;direct", "create_ts;desc",
						direct_Destination_information },
				{ ti.direct_org1_user1_token, "msp", 2, 3, "organization_id;=;msp", "create_ts;asc",
						msp_Destination_information },
				{ ti.direct_org1_user1_token, "subOrg", 2, 2, "organization_id;=;msp_child", "create_ts;desc",
						sub_Destination_information },
				{ ti.root_msp1_submsp1_account_admin_token, "subOrg", 2, 2, "organization_id;=;direct",
						"create_ts;desc", direct_Destination_information },
				// {ti.direct_org1_user1_token_2,"msp",2,3,"organization_id;=;msp","create_ts;asc",msp_Destination_information},
				// {ti.direct_org1_user1_token_2,"direct",1,3,"organization_id;=;direct","create_ts;desc",direct_Destination_information}
		};
	}

	@Test(dependsOnMethods = "PostDestinationsByIdcloudHybrid", dataProvider = "getDestinationInfo4")
	public void GetDestinationsforInsufficentPermission(String admin_Token, String OrgType, int page_number,
			int page_size, String filterStr, String SortStr, ArrayList<HashMap<String, Object>> Destinationinfo) {

		// creating the test object for the log information
		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Ramya.Nagepalli");
		String[] filterArray = null;
		String filterName = null, filterOperator = null, filterValue = null;
		if (filterStr != "" && filterStr != null) {
			filterArray = filterStr.split(";");
			filterName = filterArray[0];
			filterOperator = filterArray[1];
			filterValue = filterArray[2];
		}
		// preparing the URL and validating the Response For
		test.log(LogStatus.INFO, "get filter");
		String newFilterStr = filterStr;
		if (filterName.equalsIgnoreCase("organization_id")) {
			if (filterOperator.equals("=")) {
				if (filterValue.equals(SpogConstants.DIRECT_ORG)) {
					newFilterStr = filterName + ";" + filterOperator + ";" + ti.direct_org1_id;
				} else {
					newFilterStr = filterName + ";" + filterOperator + ";" + ti.normal_msp_org1_id;
				}
			} else {
				// in
				newFilterStr = filterName + ";" + filterOperator + ";";
				if (filterValue.contains(SpogConstants.DIRECT_ORG) && filterValue.contains(SpogConstants.MSP_ORG)
						&& filterValue.contains(SpogConstants.MSP_SUB_ORG)) {
					newFilterStr += ti.direct_org1_id + "|" + ti.normal_msp_org1_id + "|" + ti.normal_msp1_suborg1_id;
				} else {
					newFilterStr += ti.direct_org1_id + "|" + ti.normal_msp_org1_id;
				}
			}
		}
		test.log(LogStatus.INFO, "Call the rest API for get Destinations");
		String additionalURL = spogServer.PrepareURL(newFilterStr, SortStr, page_number, page_size, test);
		Response response = spogDestinationServer.getDestinations(admin_Token, additionalURL, test);
		if (OrgType.equalsIgnoreCase("direct")) {
			test.log(LogStatus.INFO, "validating the information for the direct organization");
			spogDestinationServer.checkGetDestinations(response, available_actions,
					SpogConstants.INSUFFICIENT_PERMISSIONS, Destinationinfo, page_number, page_size, newFilterStr,
					SortStr, SpogMessageCode.RESOURCE_PERMISSION_DENY, test);
		} else if (OrgType.equalsIgnoreCase("msp")) {
			test.log(LogStatus.INFO, "validating the information for the msp and sub organization");
			spogDestinationServer.checkGetDestinations(response, available_actions,
					SpogConstants.INSUFFICIENT_PERMISSIONS, Destinationinfo, page_number, page_size, newFilterStr,
					SortStr, SpogMessageCode.RESOURCE_PERMISSION_DENY, test);
		} else if (OrgType.equalsIgnoreCase("suborg")) {
			test.log(LogStatus.INFO, "validating the information for the sub organization");
			spogDestinationServer.checkGetDestinations(response, available_actions,
					SpogConstants.INSUFFICIENT_PERMISSIONS, Destinationinfo, page_number, page_size, newFilterStr,
					SortStr, SpogMessageCode.RESOURCE_PERMISSION_DENY, test);
		} else if (OrgType.equalsIgnoreCase("csr")) {
			test.log(LogStatus.INFO,
					"validating the information for the direct msp and sub organization(csr organization)");
			spogDestinationServer.checkGetDestinations(response, available_actions,
					SpogConstants.INSUFFICIENT_PERMISSIONS, Destinationinfo, page_number, page_size, newFilterStr,
					SortStr, SpogMessageCode.RESOURCE_PERMISSION_DENY, test);
		}
	}

	@AfterClass
	public void deleteCHDestinations() {

		test.log(LogStatus.INFO, "Delete CH destinations for Direct");
		Response response = spogDestinationServer.getDestinations(ti.direct_org1_user1_token, "", test);
		ArrayList<HashMap<String, Object>> destinations = new ArrayList<HashMap<String, Object>>();
		destinations = response.then().extract().path("data.destination_id");
		if (!destinations.isEmpty()) {
			destinations.stream().forEach(destination -> {
				spogDestinationServer.deletedestinationbydestination_Id(destination.toString(),
						ti.csr_token, test);
			});
		}

		test.log(LogStatus.INFO, "Delete CH destinations for normal sub org");
		response = spogDestinationServer.getDestinations(ti.normal_msp1_suborg1_user1_token, "", test);
		destinations = response.then().extract().path("data.destination_id");
		if (!destinations.isEmpty()) {
			destinations.stream().forEach(destination -> {
				spogDestinationServer.deletedestinationbydestination_Id(destination.toString(),
						ti.csr_token, test);
			});
		}

		test.log(LogStatus.INFO, "Delete CH destinations for root sub org");
		response = spogDestinationServer.getDestinations(ti.root_msp1_suborg1_user1_token, "", test);
		destinations = response.then().extract().path("data.destination_id");
		if (!destinations.isEmpty()) {
			destinations.stream().forEach(destination -> {
				spogDestinationServer.deletedestinationbydestination_Id(destination.toString(),
						ti.csr_token, test);
			});
		}
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
		rep.endTest(test);
		rep.flush();
	}

}
