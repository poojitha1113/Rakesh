package api.destination;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
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

import Constants.DestinationStatus;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import Constants.volume_type;
import InvokerServer.GatewayServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGJobServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class UpdateDestinationUsageByIdTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private SPOGJobServer spogJobServer;
	private SPOGDestinationServer spogDestinationServer;
	private Policy4SPOGServer policy4SpogServer;

	private ExtentTest test;
	private TestOrgInfo ti;

	LocalDate date = LocalDate.now();
	LocalDate yesterday = date.minusDays(1);
	LocalDate tomorrow = yesterday.plusDays(2);

	private String direct_destination_id, sub_destination_id, Direct_cloud_id, msp_cloud_id, root_cloud_id;

	HashMap<String, Object> Retention = new HashMap<String, Object>();
	HashMap<String, Object> cloud_direct_volume = new HashMap<String, Object>();
	HashMap<String, Object> cloud_hybrid_store = new HashMap<String, Object>();
	HashMap<String, String> retention = new HashMap<String, String>();

	String[] type = new String[12];

	String[] datacenters = null;
	private String job_Type = "backup";
	private String job_Status = "finished";
	private String job_Method = "full,incremental,resync,full,incremental,resync,full,incremental,resync,full,incremental,resync";
	public String JobSeverity = "warning,information,critical,warning,information,critical,warning,information,critical,warning,information,critical";

	private String rootsub_destination_id;
	private String submsp_sub_destination_id;

	@BeforeClass
	@Parameters({ "baseURI", "port", "csrAdminUserName", "csrAdminPassword", "csrReadOnlyAdminName",
			"csrReadOnlyAdminPassword", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword,
			String readOnlyUserName, String readOnlyPassword, String logFolder, String runningMachine,
			String buildVersion) {

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

		// create a cloud account under direct
		Response response = spogServer.getCloudAccounts(ti.direct_org1_user1_token, "", test);
		Direct_cloud_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");

		// create a cloud account under MSP
		response = spogServer.getCloudAccounts(ti.normal_msp_org1_user1_token, "", test);
		msp_cloud_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");

		response = spogServer.getCloudAccounts(ti.root_msp_org1_user1_token, "", test);
		root_cloud_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");

		spogDestinationServer.setToken(ti.csr_token);
		datacenters = spogDestinationServer.getDestionationDatacenterID();

		// create destinations
		String destination_name = spogServer.ReturnRandom("destination");
		HashMap<String, String> retention = spogDestinationServer.composeRetention("0", "0", "0", "0", "0", "0");
		HashMap<String, Object> cloud_direct_volume = spogDestinationServer.composeCloudDirectInfo(Direct_cloud_id,
				destination_name, "7D", "7 Days", 26.0, 24.0, 50.0, volume_type.normal.toString(), "windows8",
				retention);

		// create destination of cloud direct

		spogDestinationServer.setToken(ti.direct_org1_user1_token);
		response = spogDestinationServer.getDestinations(ti.direct_org1_user1_token, "", test);
		direct_destination_id = response.then().extract().path("data[0].destination_id");

		spogDestinationServer.setToken(ti.normal_msp_org1_user1_token);
		response = spogDestinationServer.getDestinations(ti.normal_msp1_suborg1_user1_token, "", test);
		sub_destination_id = response.then().extract().path("data[0].destination_id");

		spogDestinationServer.setToken(ti.root_msp_org1_user1_token);
		response = spogDestinationServer.getDestinations(ti.root_msp1_suborg1_user1_token, "", test);
		rootsub_destination_id = response.then().extract().path("data[0].destination_id");

		spogDestinationServer.setToken(ti.root_msp1_submsp1_user1_token);
		response = spogDestinationServer.getDestinations(ti.root_msp1_submsp1_user1_token, "", test);
		submsp_sub_destination_id = response.then().extract().path("data[0].destination_id");

	}

	// post destination

	@DataProvider(name = "updateDestinationUsageById_valid")
	public final Object[][] updateDestinationUsageById_valid() {

		return new Object[][] {
				{ "Valid Scenario to Update Destination Usage For Direct Organization using csr read only user",
						"Direct", ti.csr_readonly_token, ti.direct_org1_id, direct_destination_id, Direct_cloud_id,
						Direct_cloud_id, ti.direct_org1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Valid Scenario to Update Destination Usage For Suborg Organization using csr read only user",
						"SubOrg", ti.csr_readonly_token, ti.normal_msp1_suborg1_id, sub_destination_id, msp_cloud_id,
						msp_cloud_id, ti.normal_msp_org1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },

				{ "Valid Scenario to Update Destination Usage For Direct Organization", "Direct",
						ti.direct_org1_user1_token, ti.direct_org1_id, direct_destination_id, Direct_cloud_id,
						Direct_cloud_id, ti.direct_org1_user1_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Valid Scenario to Update Destination Usage For Suborg Organization", "SubOrg",
						ti.normal_msp_org1_user1_token, ti.normal_msp1_suborg1_id, sub_destination_id, msp_cloud_id,
						msp_cloud_id, ti.normal_msp_org1_user1_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null },

				// 3 tier cses
				{ "Valid Scenario to Update Destination Usage For rootSuborg Organization", "root_sub",
						ti.root_msp_org1_user1_token, ti.root_msp1_suborg1_id, rootsub_destination_id, root_cloud_id,
						root_cloud_id, ti.root_msp_org1_user1_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null },

				{ "Valid Scenario to Update Destination Usage For submspSuborg Organization", "submsp_sub",
						ti.root_msp1_submsp1_user1_token, ti.msp1_submsp1_sub_org1_id, submsp_sub_destination_id,
						root_cloud_id, root_cloud_id, ti.msp1_submsp1_suborg1_user1_token,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },

				{ "InValid Scenario(InvalidToken) to Update Destination Usage For Direct Organization", "Direct",
						"Jnunk", ti.direct_org1_id, direct_destination_id, Direct_cloud_id, Direct_cloud_id,
						ti.direct_org1_user1_token, SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT },
				{ "InValid Scenario(MissingToken) to Update Destination Usage For Direct Organization", "Direct", "",
						ti.direct_org1_id, spogServer.returnRandomUUID().toString(), Direct_cloud_id, Direct_cloud_id,
						ti.direct_org1_user1_token, SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTHENTICATION_FAILED },
				{ "InValid Scenario(Org_id is randomUUID)", "Direct", ti.direct_org1_user1_token, ti.direct_org1_id,
						spogServer.returnRandomUUID().toString(), Direct_cloud_id, Direct_cloud_id,
						ti.direct_org1_user1_token, SpogConstants.RESOURCE_NOT_EXIST,
						SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED },
				{ "InValid Scenario(destination is randomUUID)", "Direct", ti.direct_org1_user1_token,
						ti.direct_org1_id, spogServer.returnRandomUUID().toString(), Direct_cloud_id, Direct_cloud_id,
						ti.direct_org1_user1_token, SpogConstants.RESOURCE_NOT_EXIST,
						SpogMessageCode.DESTINATION_NOT_FOUND },

				// 403
				{ "Insufficient Permissions(Direct-Directb)", "Direct", ti.normal_msp_org1_user1_token,
						ti.direct_org1_id, spogServer.returnRandomUUID().toString(), Direct_cloud_id, Direct_cloud_id,
						ti.direct_org1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Insufficient Permissions(Direct-MSP)", "Direct", ti.normal_msp_org1_user1_token, ti.direct_org1_id,
						spogServer.returnRandomUUID().toString(), Direct_cloud_id, Direct_cloud_id,
						ti.direct_org1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Insufficient Permissions(Direct-suborg)", "Direct", ti.normal_msp1_suborg1_user1_token,
						ti.direct_org1_id, spogServer.returnRandomUUID().toString(), Direct_cloud_id, Direct_cloud_id,
						ti.direct_org1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Insufficient Permissions(MSP-Directb)", "MSP", ti.direct_org1_user1_token, ti.normal_msp_org1_id,
						spogServer.returnRandomUUID().toString(), msp_cloud_id, msp_cloud_id,
						ti.normal_msp_org1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Insufficient Permissions(MSP-MSPb)", "MSP", ti.normal_msp_org2_user1_token, ti.normal_msp_org1_id,
						spogServer.returnRandomUUID().toString(), msp_cloud_id, msp_cloud_id,
						ti.normal_msp_org1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Insufficient Permissions(MSP-suborg)", "MSP", ti.normal_msp1_suborg2_user1_token,
						ti.normal_msp_org1_id, spogServer.returnRandomUUID().toString(), msp_cloud_id, msp_cloud_id,
						ti.normal_msp_org1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Insufficient Permissions(suborg-Directb)", "suborg", ti.direct_org1_user1_token,
						ti.normal_msp1_suborg1_id, spogServer.returnRandomUUID().toString(), msp_cloud_id, msp_cloud_id,
						ti.normal_msp_org1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Insufficient Permissions(suborg-MSPb)", "suborg", ti.normal_msp_org2_user1_token,
						ti.normal_msp1_suborg1_id, spogServer.returnRandomUUID().toString(), msp_cloud_id, msp_cloud_id,
						ti.normal_msp_org1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Insufficient Permissions(suborg-suborg)", "suborg", ti.normal_msp1_suborg2_user1_token,
						ti.normal_msp1_suborg1_id, spogServer.returnRandomUUID().toString(), msp_cloud_id, msp_cloud_id,
						ti.normal_msp_org1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },

				// 400
				{ "InValid Scenario(organization is not valid UUID) to Update Destination Usage For Direct Organization",
						"Direct", ti.direct_org1_user1_token, ti.direct_org1_id,
						spogServer.returnRandomUUID().toString(), Direct_cloud_id, Direct_cloud_id,
						ti.direct_org1_user1_token, SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_ORGANIZATIONID_IS_NOT_UUID },
				{ "InValid Scenario(destination is not valid UUID) to Update Destination Usage For Direct Organization",
						"Direct", ti.direct_org1_user1_token, ti.direct_org1_id,
						spogServer.returnRandomUUID().toString(), Direct_cloud_id, Direct_cloud_id,
						ti.direct_org1_user1_token, SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_DESTINATIONID_IS_NOT_UUID },
				{ "InValid Scenario(timestamp as null)", "Direct", ti.direct_org1_user1_token, ti.direct_org1_id,
						direct_destination_id, Direct_cloud_id, Direct_cloud_id, ti.direct_org1_user1_token,
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.TIME_STAMP_CANNOT_BLANK },
				{ "InValid Scenario(Destination as null)", "Direct", ti.direct_org1_user1_token, ti.direct_org1_id,
						spogServer.returnRandomUUID().toString(), Direct_cloud_id, Direct_cloud_id,
						ti.direct_org1_user1_token, SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.DESTINATION_CANNOT_BLANK },

				// 3 tier cases
				{ "Insufficient Permissions(rootmsp-rootsub)", "rootmsp", ti.root_msp1_suborg2_user1_token,
						ti.root_msp_org1_id, spogServer.returnRandomUUID().toString(), root_cloud_id, root_cloud_id,
						ti.root_msp_org1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Insufficient Permissions(rootmsp-submsp)", "rootmsp", ti.root_msp1_submsp1_user1_token,
						ti.root_msp_org1_id, spogServer.returnRandomUUID().toString(), root_cloud_id, root_cloud_id,
						ti.root_msp_org1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Insufficient Permissions(rootmsp-submsp_sub)", "rootmsp", ti.msp1_submsp1_suborg1_user1_token,
						ti.root_msp_org1_id, spogServer.returnRandomUUID().toString(), root_cloud_id, root_cloud_id,
						ti.root_msp_org1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Insufficient Permissions(rootsub-submsp)", "rootsub", ti.root_msp1_submsp1_user1_token,
						ti.root_msp1_suborg1_id, spogServer.returnRandomUUID().toString(), root_cloud_id, root_cloud_id,
						ti.root_msp1_suborg1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Insufficient Permissions(rootsub-submsp_sub)", "rootsub", ti.msp1_submsp1_suborg1_user1_token,
						ti.root_msp1_suborg1_id, spogServer.returnRandomUUID().toString(), root_cloud_id, root_cloud_id,
						ti.root_msp1_suborg1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },

				{ "Insufficient Permissions(submsp-rootsub)", "submsp", ti.root_msp1_suborg2_user1_token,
						ti.root_msp1_submsp_org1_id, spogServer.returnRandomUUID().toString(), root_cloud_id,
						root_cloud_id, ti.msp1_submsp1_suborg1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },

		};

	}

	@Test(dataProvider = "updateDestinationUsageById_valid")
	public void updateDestinationUsageById_valid(String testCase, String org_type, String invalidToken,
			String organization_id, String destination_id, String cloud_id, String site_id, String adminToken,
			int ExpextedStatusCode, SpogMessageCode expectedErrror) {

		test = ExtentManager.getNewTest("updateDestinationUsageById_valid for " + testCase);

		spogDestinationServer.setToken(adminToken);
		Response response = null;

		// update destination usage
		String primary_usage = "20.00", snapshot_usage = "20.00", timestamp = "434456";

		if (testCase.equals("InValid Scenario(Org_id is randomUUID)")) {
			response = spogDestinationServer.updateDestinationUsage(adminToken, UUID.randomUUID().toString(),
					destination_id, timestamp, primary_usage, snapshot_usage, SpogConstants.RESOURCE_NOT_EXIST,
					SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED, test);

		} else if (testCase.equals("InValid Scenario(destination is randomUUID)")) {
			response = spogDestinationServer.updateDestinationUsage(adminToken, organization_id,
					UUID.randomUUID().toString(), timestamp, primary_usage, snapshot_usage,
					SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.DESTINATION_NOT_FOUND, test);

		} else if (testCase.equals(
				"InValid Scenario(organization is not valid UUID) to Update Destination Usage For Direct Organization")) {
			response = spogDestinationServer.updateDestinationUsage(adminToken, "abc", destination_id, timestamp,
					primary_usage, snapshot_usage, SpogConstants.REQUIRED_INFO_NOT_EXIST,
					SpogMessageCode.ELEMENT_IS_NOT_UUID, test);

		} else if (testCase.equals(
				"InValid Scenario(destination is not valid UUID) to Update Destination Usage For Direct Organization")) {
			response = spogDestinationServer.updateDestinationUsage(adminToken, organization_id, "abc", timestamp,
					primary_usage, snapshot_usage, SpogConstants.REQUIRED_INFO_NOT_EXIST,
					SpogMessageCode.ELEMENT_IS_NOT_UUID, test);

		} else if (testCase.equals("InValid Scenario(timestamp as null)")) {
			response = spogDestinationServer.updateDestinationUsage(adminToken, organization_id, destination_id, null,
					primary_usage, snapshot_usage, SpogConstants.REQUIRED_INFO_NOT_EXIST,
					SpogMessageCode.TIME_STAMP_CANNOT_BLANK, test);

		} else if (testCase.equals("InValid Scenario(Destination as null)")) {
			response = spogDestinationServer.updateDestinationUsage(adminToken, organization_id, null, timestamp,
					primary_usage, snapshot_usage, SpogConstants.REQUIRED_INFO_NOT_EXIST,
					SpogMessageCode.DESTINATION_CANNOT_BLANK, test);

		} else {
			response = spogDestinationServer.updateDestinationUsage(invalidToken, organization_id, destination_id,
					timestamp, primary_usage, snapshot_usage, ExpextedStatusCode, expectedErrror, test);

		}

		/*
		 * spogDestinationServer.setToken(csr_token);
		 * spogDestinationServer.RecycleDirectVolume(destination_id, test);
		 */

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

	/******************************************************************
	 * RandomFunction
	 ******************************************************************************/
	public int gen_random_index(String[] job_status) {
		Random generator = new Random();
		int randomindx = generator.nextInt(job_status.length);

		return randomindx;
	}

}
