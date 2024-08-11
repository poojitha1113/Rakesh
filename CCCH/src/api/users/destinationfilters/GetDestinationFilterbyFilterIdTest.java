package api.users.destinationfilters;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;

public class GetDestinationFilterbyFilterIdTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private SPOGDestinationServer spogDestinationServer;
	private UserSpogServer userSpogServer;
	private TestOrgInfo ti;
	private ExtentTest test;
	

	private String site_version = "1.0.0";
	private String gateway_hostname = "ramesh";
	private String org_model_prefix = this.getClass().getSimpleName();

	int passedcases = 0;
	int failedcases = 0;
	int skippedcases = 0;

	String buildnumber = null;

	

	private String runningMachine;
	private String buildVersion;

	LocalDate date = LocalDate.now();
	LocalDate yesterday = date.minusDays(1);
	LocalDate tomorrow = yesterday.plusDays(2);

	@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port,String logFolder, String runningMachine,
			String buildVersion) {

		
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		rep = ExtentManager.getInstance("GetDestinationFilterbyFilterIdTest", logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Ramesh.Pendurthy";

		Nooftest = 0;
		
		// Used for creating a build number with dateformat
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
			ti = new TestOrgInfo(spogServer, test);
		}
	}
		

	@DataProvider(name = "getdestinationfilter", parallel = false)
	public final Object[][] getdestinationfilter() {
		return new Object[][] {
				{ "direct", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id,
						DestinationType.cloud_direct_volume.toString(), DestinationStatus.creating.toString(), "20.1",
						"2", "22", "normal", "host-abc", "7D", "0", "0", "0", "0", "0", "0",
						RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },
				{ "direct", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id,
						DestinationType.cloud_direct_volume.toString(), DestinationStatus.creating.toString(), "20.1",
						"2", "22", "normal", "host-abc", "7D", "0", "0", "0", "0", "0", "0",
						RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34",
						RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34", "none",
						UUID.randomUUID().toString() },
				{ "direct", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id,
						DestinationType.cloud_direct_volume.toString(), DestinationStatus.creating.toString(), "20.1",
						"2", "22", "normal", "host-abc", "7D", "0", "0", "0", "0", "0", "0",
						RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34",
						RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34",
						DestinationType.cloud_direct_volume.toString(), "none" },
				{ "msp", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id, 
						DestinationType.cloud_direct_volume.toString(), DestinationStatus.creating.toString(), "20.1",
						"2", "22", "normal", "host-abc", "14D", "0", "0", "0", "0", "0", "0",
						RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },
				{ "suborg", ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id,
						DestinationType.cloud_direct_volume.toString(), DestinationStatus.creating.toString(), "20.1",
						"2", "22", "normal", "host-abc", "1M", "0", "0", "0", "0", "0", "0",
						RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },
				{ "msp", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,
						DestinationType.cloud_direct_volume.toString(), DestinationStatus.creating.toString(), "20.1",
						"2", "22", "normal", "host-abc", "14D", "0", "0", "0", "0", "0", "0",
						RandomStringUtils.randomAlphanumeric(4) + "test",
						RandomStringUtils.randomAlphanumeric(4) + "test", "none", "none" },
				{ "suborg", ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id,
						DestinationType.cloud_direct_volume.toString(), DestinationStatus.creating.toString(), "20.1",
						"2", "22", "normal", "host-abc", "1M", "0", "0", "0", "0", "0", "0",
						RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34",
						RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34", "none", "none" },
				{ "suborgusingmsptoken", ti.normal_msp1_suborg1_id, ti.normal_msp_org1_user1_token, ti.normal_msp1_suborg1_user1_id,
						DestinationType.cloud_direct_volume.toString(), DestinationStatus.creating.toString(), "20.1",
						"2", "22", "normal", "host-abc", "1M", "0", "0", "0", "0", "0", "0",
						RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34",
						RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34", "none", "none" },
				{ "suborg-mspAccAdminT", ti.normal_msp1_suborg1_id, ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp1_suborg1_user1_id,
						DestinationType.cloud_direct_volume.toString(), DestinationStatus.creating.toString(), "20.1",
						"2", "22", "normal", "host-abc", "1M", "0", "0", "0", "0", "0", "0",
						RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34",
						RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34", "none", "none" },
				// csr read only cases
				{ "csrreadonly", ti.csr_org_id,ti.csr_readonly_token,ti.csr_readonly_admin_user_id,
						DestinationType.cloud_direct_volume.toString(), DestinationStatus.running.toString(), "20.1",
						"2", "22", "normal", "host-abc", "14D", "0", "0", "0", "0", "0", "0",
						RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34",
						RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34", "none", "none" }, };
	}

	@Test(dataProvider = "getdestinationfilter")
	public void getdestinationbyfilterId(String organizationType, String organization_id, String validToken,
			String user_id, String destination_type, String destination_status, String primary_usage,
			String snapshot_usage, String total_usage, String volume_type, String hostname, String retention_id,
			String age_hours_max, String age_four_hours_max, String age_days_max, String age_weeks_max,
			String age_months_max, String age_years_max, String destination_name, String filter_destination_name,
			String filter_destination_type, String filter_policy_id) {
		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);
		String prefix = RandomStringUtils.randomAlphanumeric(4);
		HashMap<String, Object> expected_response = new HashMap<>();

		spogDestinationServer.setToken(validToken);
		String[] datacenters = spogDestinationServer.getDestionationDatacenterID();

		/*
		 * test.log(LogStatus.INFO, "Post the destination"); String
		 * destination_Id =
		 * spogDestinationServer.createDestinationWithCheck(organization_id,
		 * site_id, datacenters[0], destination_type, destination_status,
		 * primary_usage, snapshot_usage, total_usage, volume_type, hostname,
		 * retention_id, age_hours_max, age_four_hours_max, age_days_max,
		 * age_weeks_max, age_months_max, age_years_max, "0", "0", "0", "0",
		 * "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", destination_name,
		 * test);
		 * 
		 * test.log(LogStatus.INFO, "Post the destination"); String
		 * destination_Id2 =
		 * spogDestinationServer.createDestinationWithCheck(organization_id,
		 * site_id, datacenters[0], destination_type, destination_status,
		 * primary_usage, snapshot_usage, total_usage, volume_type, hostname,
		 * retention_id, age_hours_max, age_four_hours_max, age_days_max,
		 * age_weeks_max, age_months_max, age_years_max, "0", "0", "0", "0",
		 * "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", prefix+"test",
		 * test);
		 */

		test.log(LogStatus.INFO, "Create the destination filter for destination type");
		String filter_Id = spogDestinationServer.createDestinationFilterWithCheck(user_id, prefix + "filter",
				filter_destination_name, filter_policy_id, filter_destination_type, "true", test);

		test.log(LogStatus.INFO, "Compose destination filter info");
		expected_response = composefilterinfo(filter_Id, organization_id, user_id, prefix + "filter",
				filter_destination_name, filter_policy_id, filter_destination_type);

		test.log(LogStatus.INFO, "get destination filter by filter id using token of " + organizationType);
		spogDestinationServer.getdestinationfilterbyfilterId(user_id, filter_Id, validToken, expected_response,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		test.log(LogStatus.INFO, "get destination filter by filter id using token of csr");
		spogDestinationServer.getdestinationfilterbyfilterId(user_id, filter_Id, ti.csr_token, expected_response,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		test.log(LogStatus.INFO, "delete destination filter by filter id");
		spogDestinationServer.deletedestinationfilterbyfilterId(user_id, filter_Id, validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		/*
		 * test.log(LogStatus.INFO, "Delete the destination");
		 * spogDestinationServer.deletedestinationbydestination_Id(
		 * destination_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE,
		 * null, test);
		 */

	}

	@DataProvider(name = "getdestinationfilter_401", parallel = false)
	public final Object[][] getdestinationfilter_401() {
		return new Object[][] {
				{ "direct", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id,
						DestinationType.cloud_direct_volume.toString(), DestinationStatus.creating.toString(), "20.1",
						"2", "22", "normal", "host-abc", "7D", "0", "0", "0", "0", "0", "0",
						RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },
				{ "msp", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,
						DestinationType.cloud_direct_volume.toString(), DestinationStatus.creating.toString(), "20.1",
						"2", "22", "normal", "host-abc", "14D", "0", "0", "0", "0", "0", "0",
						RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },
				{ "suborg", ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id,
						DestinationType.cloud_direct_volume.toString(), DestinationStatus.creating.toString(), "20.1",
						"2", "22", "normal", "host-abc", "1M", "0", "0", "0", "0", "0", "0",
						RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },

		};
	}

	@Test(dataProvider = "getdestinationfilter_401")
	public void getdestinationbyfilterId_401(String organizationType, String organization_id, String validToken,
			String user_id, String destination_type, String destination_status, String primary_usage,
			String snapshot_usage, String total_usage, String volume_type, String hostname, String retention_id,
			String age_hours_max, String age_four_hours_max, String age_days_max, String age_weeks_max,
			String age_months_max, String age_years_max, String destination_name, String filter_destination_name,
			String filter_destination_type, String filter_policy_id) {
		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);
		String prefix = RandomStringUtils.randomAlphanumeric(4);
		HashMap<String, Object> expected_response = new HashMap<>();

		spogDestinationServer.setToken(validToken);
		String[] datacenters = spogDestinationServer.getDestionationDatacenterID();

		/*
		 * test.log(LogStatus.INFO, "Post the destination"); String
		 * destination_Id =
		 * spogDestinationServer.createDestinationWithCheck(organization_id,
		 * site_id, datacenters[0], destination_type, destination_status,
		 * primary_usage, snapshot_usage, total_usage, volume_type, hostname,
		 * retention_id, age_hours_max, age_four_hours_max, age_days_max,
		 * age_weeks_max, age_months_max, age_years_max, "0", "0", "0", "0",
		 * "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", destination_name,
		 * test);
		 */

		test.log(LogStatus.INFO, "Create the destination filter for destination type");
		String filter_Id = spogDestinationServer.createDestinationFilterWithCheck(user_id, prefix + "filter",
				filter_destination_name, filter_policy_id, filter_destination_type, "true", test);

		test.log(LogStatus.INFO, "Compose destination filter info");
		expected_response = composefilterinfo(filter_Id, organization_id, user_id, prefix + "filter",
				filter_destination_name, filter_policy_id, filter_destination_type);

		test.log(LogStatus.INFO, "get destination filter by filter id using invalid token of " + organizationType);
		spogDestinationServer.getdestinationfilterbyfilterId(user_id, filter_Id, validToken + "J", expected_response,
				SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);

		test.log(LogStatus.INFO, "get destination filter by filter id using missing token of " + organizationType);
		spogDestinationServer.getdestinationfilterbyfilterId(user_id, filter_Id, "", expected_response,
				SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);

		test.log(LogStatus.INFO, "delete destination filter by filter id");
		spogDestinationServer.deletedestinationfilterbyfilterId(user_id, filter_Id, validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		/*
		 * test.log(LogStatus.INFO, "Delete the destination");
		 * spogDestinationServer.deletedestinationbydestination_Id(
		 * destination_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE,
		 * null, test);
		 */

	}

	@DataProvider(name = "getdestinationfilterbyfilterId_doesnotexist", parallel = false)
	public final Object[][] getdestinationfilterbyfilterId_doesnotexist() {
		return new Object[][] {
				{ "direct", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id,
						DestinationType.cloud_direct_volume.toString(), DestinationStatus.creating.toString(), "20.1",
						"2", "22", "normal", "host-abc", "7D", "0", "0", "0", "0", "0", "0",
						RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },
				{ "msp", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,
						DestinationType.cloud_direct_volume.toString(), DestinationStatus.creating.toString(), "20.1",
						"2", "22", "normal", "host-abc", "14D", "0", "0", "0", "0", "0", "0",
						RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },
				{ "suborg", ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id,
						DestinationType.cloud_direct_volume.toString(), DestinationStatus.creating.toString(), "20.1",
						"2", "22", "normal", "host-abc", "1M", "0", "0", "0", "0", "0", "0",
						RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },
				{ "suborgusingmsptoken", ti.normal_msp1_suborg1_id, ti.normal_msp_org1_user1_token, ti.normal_msp1_suborg1_user1_id,
						DestinationType.cloud_direct_volume.toString(), DestinationStatus.creating.toString(), "20.1",
						"2", "22", "normal", "host-abc", "1M", "0", "0", "0", "0", "0", "0",
						RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34",
						RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34", "none", "none" },
				// csr read only cases
				{ "csrreadonly", ti.csr_org_id, ti.csr_readonly_token ,ti.csr_readonly_admin_user_id ,
						DestinationType.cloud_direct_volume.toString(), DestinationStatus.running.toString(), "20.1",
						"2", "22", "normal", "host-abc", "7D", "0", "0", "0", "0", "0", "0",
						RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },

		};
	}

	@Test(dataProvider = "getdestinationfilterbyfilterId_doesnotexist")
	public void getdestinationfilterbyfilterId_doesnotexist(String organizationType, String organization_id,
			String validToken, String user_id, String destination_type, String destination_status, String primary_usage,
			String snapshot_usage, String total_usage, String volume_type, String hostname, String retention_id,
			String age_hours_max, String age_four_hours_max, String age_days_max, String age_weeks_max,
			String age_months_max, String age_years_max, String destination_name, String filter_destination_name,
			String filter_destination_type, String filter_policy_id) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);
		String prefix = RandomStringUtils.randomAlphanumeric(4);
		HashMap<String, Object> expected_response = new HashMap<>();

		spogDestinationServer.setToken(validToken);
		String[] datacenters = spogDestinationServer.getDestionationDatacenterID();

		/*
		 * test.log(LogStatus.INFO, "Post the destination"); String
		 * destination_Id =
		 * spogDestinationServer.createDestinationWithCheck(organization_id,
		 * site_id, datacenters[0], destination_type, destination_status,
		 * primary_usage, snapshot_usage, total_usage, volume_type, hostname,
		 * retention_id, age_hours_max, age_four_hours_max, age_days_max,
		 * age_weeks_max, age_months_max, age_years_max, "0", "0", "0", "0",
		 * "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", destination_name,
		 * test);
		 */
		test.log(LogStatus.INFO, "Create the destination filter for destination type");
		String filter_Id = spogDestinationServer.createDestinationFilterWithCheck(user_id, prefix + "filter",
				filter_destination_name, filter_policy_id, filter_destination_type, "true", test);

		test.log(LogStatus.INFO, "Compose destination filter info");
		expected_response = composefilterinfo(filter_Id, organization_id, user_id, prefix + "filter",
				filter_destination_name, filter_policy_id, filter_destination_type);

		test.log(LogStatus.INFO, "get destination filter by filter id where the filter does not exist using token of "
				+ organizationType);
		spogDestinationServer.getdestinationfilterbyfilterId(user_id, UUID.randomUUID().toString(), validToken,
				expected_response, SpogConstants.RESOURCE_NOT_EXIST,
				SpogMessageCode.DESTINATION_FILTER_NOT_FOUND_WITH_USER_ID, test);

		test.log(LogStatus.INFO, "get destination filter by filter id where the userid does not exist using token of "
				+ organizationType);
		spogDestinationServer.getdestinationfilterbyfilterId(UUID.randomUUID().toString(), filter_Id, validToken,
				expected_response, SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.USER_ID_DOESNOT_EXIST, test);

		test.log(LogStatus.INFO,
				"get destination filter by filter id where the filter does not exist using token of csr");
		spogDestinationServer.getdestinationfilterbyfilterId(user_id, UUID.randomUUID().toString(), ti.csr_token,
				expected_response, SpogConstants.RESOURCE_NOT_EXIST,
				SpogMessageCode.DESTINATION_FILTER_NOT_FOUND_WITH_USER_ID, test);

		test.log(LogStatus.INFO, "delete destination filter by filter id");
		spogDestinationServer.deletedestinationfilterbyfilterId(user_id, filter_Id, validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		/*
		 * test.log(LogStatus.INFO, "Delete the destination");
		 * spogDestinationServer.deletedestinationbydestination_Id(
		 * destination_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE,
		 * null, test);
		 */

	}

	@DataProvider(name = "getdestinationfilterbyfilterId_insufficientpermissions", parallel = false)
	public final Object[][] getdestinationfilterbyfilterId_403() {
		return new Object[][] {
				{ "direct_msp", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id, ti.normal_msp_org1_user1_token,
						ti.normal_msp_org1_user1_id,DestinationType.cloud_direct_volume.toString(),	DestinationStatus.creating.toString(),
						"20.1", "2", "22", "normal", "host-abc", "7D", "0", "0","0", "0", "0", "0",RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34", "none", 
						DestinationType.cloud_direct_volume.toString(),"none" }, 
				{ "direct_suborg", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id,
							ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id, DestinationType.cloud_direct_volume.toString(),
						DestinationStatus.creating.toString(), "20.1", "2", "22", "normal", "host-abc", "7D", "0", "0",
						"0", "0", "0", "0", RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },
				{ "msp_direct", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id, ti.direct_org1_user1_token,
						ti.direct_org1_user1_id,DestinationType.cloud_direct_volume.toString(),
						DestinationStatus.creating.toString(), "20.1", "2", "22", "normal", "host-abc", "14D", "0", "0",
						"0", "0", "0", "0", RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },
				{ "msp_suborg", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id, ti.normal_msp1_suborg1_user1_token,
						ti.normal_msp1_suborg1_user1_id,DestinationType.cloud_direct_volume.toString(),
						DestinationStatus.creating.toString(), "20.1", "2", "22", "normal", "host-abc", "14D", "0", "0",
						"0", "0", "0", "0", RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },
				{ "msp_mspb", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id, ti.normal_msp_org2_user1_token,
						ti.normal_msp_org2_user1_id,DestinationType.cloud_direct_volume.toString(), DestinationStatus.creating.toString(), "20.1",
						"2", "22", "normal", "host-abc", "14D", "0", "0", "0", "0", "0", "0",
						RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },
				{ "suborg_direct", ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id,
						ti.direct_org1_user1_token, ti.direct_org1_user1_id, DestinationType.cloud_direct_volume.toString(),
						DestinationStatus.creating.toString(), "20.1", "2", "22", "normal", "host-abc", "1M", "0", "0",
						"0", "0", "0", "0", RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },
				{ "suborg_suborgb", ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg2_user1_token,
						ti.normal_msp1_suborg2_user1_id, DestinationType.cloud_direct_volume.toString(),
						DestinationStatus.creating.toString(), "20.1", "2", "22", "normal", "host-abc", "1M", "0", "0",
						"0", "0", "0", "0", RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },
				{ "suborg_mspb", ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id,ti.normal_msp_org2_user1_token,
						ti.normal_msp_org2_user1_id, DestinationType.cloud_direct_volume.toString(),
						DestinationStatus.creating.toString(), "20.1", "2", "22", "normal", "host-abc", "1M", "0", "0",
						"0", "0", "0", "0", RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },
				{ "suborgb_mspAccAdminT", ti.normal_msp1_suborg2_id, ti.normal_msp1_suborg2_user1_token, ti.normal_msp1_suborg2_user1_id,
						ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp1_suborg1_user1_id, DestinationType.cloud_direct_volume.toString(),
						DestinationStatus.creating.toString(), "20.1", "2", "22", "normal", "host-abc", "1M", "0", "0",
						"0", "0", "0", "0", RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },
				// csr read only cases
				{ "csrreadonly_msp", ti.csr_org_id, ti.csr_readonly_token,ti.csr_readonly_admin_user_id, ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,
						DestinationType.cloud_direct_volume.toString(),
						DestinationStatus.running.toString(), "20.1", "2", "22", "normal", "host-abc", "7D", "0", "0",
						"0", "0", "0", "0", RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },
				{ "csrreadonly_direct",ti.csr_org_id, ti.csr_readonly_token,ti.csr_readonly_admin_user_id,
							ti.direct_org1_user1_token, ti.direct_org1_user1_id, DestinationType.cloud_direct_volume.toString(),
						DestinationStatus.running.toString(), "20.1", "2", "22", "normal", "host-abc", "7D", "0", "0",
						"0", "0", "0", "0", RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },
				{ "csrreadonly_suborg", ti.csr_org_id, ti.csr_readonly_token,ti.csr_readonly_admin_user_id,
							ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id, DestinationType.cloud_direct_volume.toString(),
						DestinationStatus.running.toString(), "20.1", "2", "22", "normal", "host-abc", "7D", "0", "0",
						"0", "0", "0", "0", RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" }, };
	}

	@Test(dataProvider = "getdestinationfilterbyfilterId_insufficientpermissions")
	public void getdestinationfilterbyfilterId_403_otherogtoken(String organizationType, String organization_id,
			String validToken, String user_id, String otherorgtoken, String otherorguserid, String destination_type,
			String destination_status, String primary_usage, String snapshot_usage, String total_usage,
			String volume_type, String hostname, String retention_id, String age_hours_max, String age_four_hours_max,
			String age_days_max, String age_weeks_max, String age_months_max, String age_years_max,
			String destination_name, String filter_destination_name, String filter_destination_type,
			String filter_policy_id) {
		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);
		String prefix = RandomStringUtils.randomAlphanumeric(4);
		HashMap<String, Object> expected_response = new HashMap<>();

		SpogMessageCode expected = null;
		spogDestinationServer.setToken(validToken);
		String[] datacenters = spogDestinationServer.getDestionationDatacenterID();

		/*
		 * test.log(LogStatus.INFO, "Post the destination"); String
		 * destination_Id =
		 * spogDestinationServer.createDestinationWithCheck(organization_id,
		 * site_id, datacenters[0], destination_type, destination_status,
		 * primary_usage, snapshot_usage, total_usage, volume_type, hostname,
		 * retention_id, age_hours_max, age_four_hours_max, age_days_max,
		 * age_weeks_max, age_months_max, age_years_max, "0", "0", "0", "0",
		 * "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", destination_name,
		 * test);
		 */
		test.log(LogStatus.INFO, "Create the destination filter for destination type");
		String filter_Id = spogDestinationServer.createDestinationFilterWithCheck(user_id, prefix + "filter",
				filter_destination_name, filter_policy_id, filter_destination_type, "true", test);

		test.log(LogStatus.INFO, "Compose destination filter info");
		expected_response = composefilterinfo(filter_Id, organization_id, user_id, prefix + "filter",
				filter_destination_name, filter_policy_id, filter_destination_type);

		if (organizationType == "msp_direct" || organizationType == "msp_suborg") {
			expected = SpogMessageCode.DIRECT_ADMIN_CANNOT_VIEW_MSP_CSR;
		} else {
			expected = SpogMessageCode.LOGIN_USER_ORG_NOT_SAME_AS_GET_USER;
		}

		expected = SpogMessageCode.RESOURCE_PERMISSION_DENY;

		test.log(LogStatus.INFO,
				"get destination filter by filter id where the filter Id and the user id are from organization "
						+ organizationType + " and token from other org");
		spogDestinationServer.getdestinationfilterbyfilterId(user_id, filter_Id, otherorgtoken, expected_response,
				SpogConstants.INSUFFICIENT_PERMISSIONS, expected, test);

		/*
		 * test.log(LogStatus.INFO,
		 * "get destination filter by filter id where the filter Id and token are from organization "
		 * +organizationType + " and user id from other org ");
		 * spogDestinationServer.getdestinationfilterbyfilterId(otherorguserid,
		 * filter_Id, validToken, expected_response,
		 * SpogConstants.INSUFFICIENT_PERMISSIONS,
		 * SpogMessageCode.DIRECT_ADMIN_CANNOT_VIEW_MSP_CSR, test);
		 */
		test.log(LogStatus.INFO, "delete destination filter by filter id");
		spogDestinationServer.deletedestinationfilterbyfilterId(user_id, filter_Id, validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		/*
		 * test.log(LogStatus.INFO, "Delete the destination");
		 * spogDestinationServer.deletedestinationbydestination_Id(
		 * destination_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE,
		 * null, test);
		 */

	}

	@DataProvider(name = "getdestinationfilterbyfilterId_403_404", parallel = false)
	public final Object[][] getdestinationfilterbyfilterId_403_404() {
		return new Object[][] {
				{ "direct_msp", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id, ti.normal_msp_org1_user1_token,
					ti.normal_msp_org1_user1_id, DestinationType.cloud_direct_volume.toString(),
						DestinationStatus.creating.toString(), "20.1", "2", "22", "normal", "host-abc", "7D", "0", "0",
						"0", "0", "0", "0", RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },
				{ "direct_suborg", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id,
							ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id, DestinationType.cloud_direct_volume.toString(),
						DestinationStatus.creating.toString(), "20.1", "2", "22", "normal", "host-abc", "7D", "0", "0",
						"0", "0", "0", "0", RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },
				{ "msp_direct", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id, ti.direct_org1_user1_token,
						ti.direct_org1_user1_id, DestinationType.cloud_direct_volume.toString(),
						DestinationStatus.creating.toString(), "20.1", "2", "22", "normal", "host-abc", "14D", "0", "0",
						"0", "0", "0", "0", RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },
				{ "msp_suborg", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id, ti.normal_msp1_suborg1_user1_token,
						ti.normal_msp1_suborg1_user1_id, DestinationType.cloud_direct_volume.toString(),
						DestinationStatus.creating.toString(), "20.1", "2", "22", "normal", "host-abc", "14D", "0", "0",
						"0", "0", "0", "0", RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },
				{ "msp_mspb", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id, ti.normal_msp_org2_user1_token,
						ti.normal_msp_org2_user1_id,
						DestinationType.cloud_direct_volume.toString(), DestinationStatus.creating.toString(), "20.1",
						"2", "22", "normal", "host-abc", "14D", "0", "0", "0", "0", "0", "0",
						RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },
				{ "suborg_direct", ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id,
						ti.direct_org1_user1_token, ti.direct_org1_user1_id, DestinationType.cloud_direct_volume.toString(),
						DestinationStatus.creating.toString(), "20.1", "2", "22", "normal", "host-abc", "1M", "0", "0",
						"0", "0", "0", "0", RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },
				{ "suborg_suborgb",ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id, ti.normal_msp1_suborg2_user1_token,
							ti.normal_msp1_suborg2_user1_id, DestinationType.cloud_direct_volume.toString(),
						DestinationStatus.creating.toString(), "20.1", "2", "22", "normal", "host-abc", "1M", "0", "0",
						"0", "0", "0", "0", RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },
				{ "suborg_mspb", ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id,ti.normal_msp_org2_user1_token,
						ti.normal_msp_org2_user1_id, DestinationType.cloud_direct_volume.toString(),
						DestinationStatus.creating.toString(), "20.1", "2", "22", "normal", "host-abc", "1M", "0", "0",
						"0", "0", "0", "0", RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },
				{ "direct_msp_csr", ti.direct_org1_id, ti.csr_token, ti.direct_org1_user1_id, ti.csr_token, ti.normal_msp_org1_user1_id,
						DestinationType.cloud_direct_volume.toString(), DestinationStatus.creating.toString(), "20.1",
						"2", "22", "normal", "host-abc", "1M", "0", "0", "0", "0", "0", "0",
						RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },
				{ "direct_csr", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id, ti.direct_org1_user1_token,
						ti.csr_admin_user_id, DestinationType.cloud_direct_volume.toString(),
						DestinationStatus.creating.toString(), "20.1", "2", "22", "normal", "host-abc", "1M", "0", "0",
						"0", "0", "0", "0", RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },
				{ "msp_csr", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_token, ti.csr_admin_user_id,
						DestinationType.cloud_direct_volume.toString(), DestinationStatus.creating.toString(), "20.1",
						"2", "22", "normal", "host-abc", "1M", "0", "0", "0", "0", "0", "0",
						RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },
				// csr read only cases
				{ "direct_csrreadonly", ti.direct_org1_id, ti.direct_org1_user1_token, ti.direct_org1_user1_id,
						ti.direct_org1_user1_token, ti.csr_readonly_admin_user_id, DestinationType.cloud_direct_volume.toString(),
						DestinationStatus.creating.toString(), "20.1", "2", "22", "normal", "host-abc", "1M", "0", "0",
						"0", "0", "0", "0", RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },
				{ "msp_csrreadonly", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id, ti.normal_msp_org1_user1_token,
						ti.csr_readonly_admin_user_id, DestinationType.cloud_direct_volume.toString(),
						DestinationStatus.creating.toString(), "20.1", "2", "22", "normal", "host-abc", "1M", "0", "0",
						"0", "0", "0", "0", RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" },
				{ "suborg_csrreadonly",  ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id, ti.normal_msp_org1_user1_token,
						ti.csr_readonly_admin_user_id, DestinationType.cloud_direct_volume.toString(),
						DestinationStatus.creating.toString(), "20.1", "2", "22", "normal", "host-abc", "1M", "0", "0",
						"0", "0", "0", "0", RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34", "none",
						DestinationType.cloud_direct_volume.toString(), "none" }, };
	}

	@Test(dataProvider = "getdestinationfilterbyfilterId_403_404")
	public void getdestinationfilterbyfilterId_403_otheroguserid(String organizationType, String organization_id,
			String validToken, String user_id, String otherorgtoken, String otherorguserid, String destination_type,
			String destination_status, String primary_usage, String snapshot_usage, String total_usage,
			String volume_type, String hostname, String retention_id, String age_hours_max, String age_four_hours_max,
			String age_days_max, String age_weeks_max, String age_months_max, String age_years_max,
			String destination_name, String filter_destination_name, String filter_destination_type,
			String filter_policy_id) {
		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);
		String prefix = RandomStringUtils.randomAlphanumeric(4);
		HashMap<String, Object> expected_response = new HashMap<>();
		SpogMessageCode expected = null;
		int expectedstatuscode = SpogConstants.INSUFFICIENT_PERMISSIONS;

		spogDestinationServer.setToken(validToken);
		String[] datacenters = spogDestinationServer.getDestionationDatacenterID();

		/*
		 * test.log(LogStatus.INFO, "Post the destination"); String
		 * destination_Id =
		 * spogDestinationServer.createDestinationWithCheck(organization_id,
		 * site_id, datacenters[0], destination_type, destination_status,
		 * primary_usage, snapshot_usage, total_usage, volume_type, hostname,
		 * retention_id, age_hours_max, age_four_hours_max, age_days_max,
		 * age_weeks_max, age_months_max, age_years_max, "0", "0", "0", "0",
		 * "0", "0", "0", "0", "0", "0", "0", "0", "0", "0", destination_name,
		 * test);
		 */
		test.log(LogStatus.INFO, "Create the destination filter for destination type");
		String filter_Id = spogDestinationServer.createDestinationFilterWithCheck(user_id, prefix + "filter",
				filter_destination_name, filter_policy_id, filter_destination_type, "true", test);

		test.log(LogStatus.INFO, "Compose destination filter info");
		expected_response = composefilterinfo(filter_Id, organization_id, user_id, prefix + "filter",
				filter_destination_name, filter_policy_id, filter_destination_type);
		expected = SpogMessageCode.RESOURCE_PERMISSION_DENY;
		if (organizationType == "msp_suborg" || organizationType == "direct_msp_csr") {
			expected = SpogMessageCode.DESTINATION_FILTER_NOT_FOUND_WITH_USER_ID;
			expectedstatuscode = SpogConstants.RESOURCE_NOT_EXIST;
		} /*
			 * else if(organizationType=="direct_msp"||organizationType==
			 * "suborg_mspb"||organizationType=="direct_csr") { expected =
			 * SpogMessageCode.DIRECT_ADMIN_CANNOT_VIEW_MSP_CSR; }else
			 * if(organizationType=="msp_csr") { expected =
			 * SpogMessageCode.MSP_ADMIN_CANNOT_VIEW_CSR; }else { expected =
			 * SpogMessageCode.LOGIN_USER_ORG_NOT_SAME_AS_GET_USER;
			 * 
			 * }
			 */

		test.log(LogStatus.INFO,
				"get destination filter by filter id where the filter Id and token are from organization "
						+ organizationType + " and user id from other org " + organizationType);
		spogDestinationServer.getdestinationfilterbyfilterId(otherorguserid, filter_Id, validToken, expected_response,
				expectedstatuscode, expected, test);

		test.log(LogStatus.INFO, "delete destination filter by filter id");
		spogDestinationServer.deletedestinationfilterbyfilterId(user_id, filter_Id, validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		/*
		 * test.log(LogStatus.INFO, "Delete the destination");
		 * spogDestinationServer.deletedestinationbydestination_Id(
		 * destination_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE,
		 * null, test);
		 */

	}

	public HashMap<String, Object> composefilterinfo(String filter_Id, String organization_Id, String user_Id,
			String filter_name, String destination_name, String policy_id, String destination_type) {
		HashMap<String, Object> expected_response = new HashMap<>();
		expected_response.put("filter_id", filter_Id);
		expected_response.put("organization_id", organization_Id);
		expected_response.put("user_id", user_Id);
		expected_response.put("filter_name", filter_name);
		expected_response.put("policy_id", policy_id);
		expected_response.put("destination_name", destination_name);
		expected_response.put("destination_type", destination_type);
		return expected_response;
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
		// ending test
		// endTest(logger) : It ends the current test and prepares to create
		// HTML report
		rep.endTest(test);
		// rep.flush();
	}
	/*
	 * @AfterTest public void aftertest() { test.log(LogStatus.INFO,
	 * "The total test cases passed are "+count1.getpassedcount());
	 * test.log(LogStatus.INFO, "the total test cases failed are "
	 * +count1.getfailedcount());
	 * 
	 * rep.flush();
	 * 
	 * }
	 * 
	 * @AfterClass public void updatebd() { test.log(LogStatus.INFO,
	 * "Performing the operations to delete the user and orginzation by logging in as csr admin"
	 * ); test.log(LogStatus.INFO,
	 * "Login in as csr admin to delete the organization");
	 * spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
	 * 
	 * 
	 * 
	 * // spogServer.DeleteUserById(user_id, test);
	 * spogServer.DeleteOrganizationWithCheck(direct_organization_id, test);
	 * spogServer.DeleteOrganizationWithCheck(sub_org_Id, test);
	 * spogServer.DeleteOrganizationWithCheck(sub_org_Id_1, test);
	 * spogServer.DeleteOrganizationWithCheck(msp_organization_id, test);
	 * spogServer.DeleteOrganizationWithCheck(msp_organization_id_b, test); try
	 * { if(count1.getfailedcount()>0) {
	 * Nooftest=count1.getpassedcount()+count1.getfailedcount()+count1.
	 * getskippedcount(); bqdb1.updateTable(BQame, runningMachine,
	 * this.buildVersion, String.valueOf(Nooftest),
	 * Integer.toString(count1.getpassedcount()),
	 * Integer.toString(count1.getfailedcount()),
	 * String.valueOf(count1.getskippedcount()), count1.getcreationtime(),
	 * "Failed"); }else {
	 * Nooftest=count1.getpassedcount()+count1.getfailedcount()+count1.
	 * getskippedcount(); bqdb1.updateTable(BQame, runningMachine,
	 * this.buildVersion, String.valueOf(Nooftest),
	 * Integer.toString(count1.getpassedcount()),
	 * Integer.toString(count1.getfailedcount()),
	 * String.valueOf(count1.getskippedcount()), count1.getcreationtime(),
	 * "Passed"); } } catch (ClientProtocolException e) { // TODO Auto-generated
	 * catch block e.printStackTrace(); } catch (IOException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); }
	 * 
	 * }
	 */
}
