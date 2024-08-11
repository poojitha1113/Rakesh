package api.users.destinationfilters;

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
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class GetDestinationFiltersForLoggedInUserTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private SPOGDestinationServer spogDestinationServer;
	private UserSpogServer userSpogServer;
	private TestOrgInfo ti;
	
	private ExtentTest test;
	

	private String org_model_prefix = this.getClass().getSimpleName();
	private String site_version = "1.0.0";
	private String gateway_hostname = "ramesh";
	// used for test case count like passed,failed,remaining cases
	// private SQLServerDb bqdb1;
	// public int Nooftest;
	int passedcases = 0;
	int failedcases = 0;
	int skippedcases = 0;
	// long creationTime;
	String buildnumber = null;
	// String BQame=null;
	// private testcasescount count1;

	LocalDate date = LocalDate.now();
	LocalDate yesterday = date.minusDays(1);
	LocalDate tomorrow = yesterday.plusDays(2);

	@BeforeClass
	@Parameters({ "baseURI", "port",  "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port,String logFolder, String runningMachine,
			String buildVersion) {


		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		rep = ExtentManager.getInstance("GetDestinationFiltersForLoggedInUserTest", logFolder);
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
						RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34",
						DestinationType.cloud_direct_volume.toString(), UUID.randomUUID().toString() },
				{ "msp", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id,
						DestinationType.cloud_direct_volume.toString(), DestinationStatus.running.toString(), "20.1",
						"2", "22", "normal", "host-abc", "7D", "0", "0", "0", "0", "0", "0",
						RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34",
						DestinationType.cloud_direct_volume.toString(), UUID.randomUUID().toString() },
				{ "suborg", ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id,
						DestinationType.cloud_direct_volume.toString(), DestinationStatus.creating.toString(), "20.1",
						"2", "22", "normal", "host-abc", "7D", "0", "0", "0", "0", "0", "0",
						RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34",
						DestinationType.cloud_direct_volume.toString(), UUID.randomUUID().toString() },
				{ "csrreadonly", ti.csr_org_id,ti.csr_readonly_token,ti.csr_readonly_admin_user_id,
						DestinationType.cloud_direct_volume.toString(), DestinationStatus.running.toString(), "20.1",
						"2", "22", "normal", "host-abc", "7D", "0", "0", "0", "0", "0", "0",
						RandomStringUtils.randomAlphanumeric(4) + "ramesh-test34",
						DestinationType.cloud_direct_volume.toString(), UUID.randomUUID().toString() },

				
		};
	}

	@Test(dataProvider = "getdestinationfilter")
	public void getdestinationfiltersforloggedinuser(String organizationType, String organization_id, String validToken,
			String user_id, String destination_type, String destination_status, String primary_usage,
			String snapshot_usage, String total_usage, String volume_type, String hostname, String retention_id,
			String age_hours_max, String age_four_hours_max, String age_days_max, String age_weeks_max,
			String age_months_max, String age_years_max, String destination_name, String filter_destination_type,
			String filter_policy_id) {
		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);
		String filtersArray[] = new String[10];
		String prefix = RandomStringUtils.randomAlphanumeric(4);
		HashMap<String, Object> expected_response = new HashMap<>();
		ArrayList<HashMap<String, Object>> expectedresponse = new ArrayList<HashMap<String, Object>>();

		spogDestinationServer.setToken(validToken);
		String[] datacenters = spogDestinationServer.getDestionationDatacenterID();

		if (organizationType.equalsIgnoreCase("csrreadonly")) {
			HashMap<String, String> temp = new HashMap<String, String>();
			Response response = spogDestinationServer.getDestinationFilterByUserId(ti.csr_readonly_admin_user_id, temp);
			ArrayList<String> filterList = new ArrayList<String>();
			filterList = response.then().extract().path("data.filter_id");
			for (int i = 0; i < filterList.size(); i++) {
				spogDestinationServer.deletedestinationfilterbyfilterId(ti.csr_readonly_admin_user_id, filterList.get(i),
						ti.csr_readonly_token, SpogConstants.SUCCESS_GET_PUT_DELETE,
						SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			}
		}
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

		test.log(LogStatus.INFO, "Create the destination filter on destination name and destination type");
		String filter_Name = RandomStringUtils.randomAlphanumeric(4) + "filter";
		filtersArray[0] = spogDestinationServer.createDestinationFilterWithCheck(user_id, filter_Name, destination_name,
				"none", filter_destination_type, "true", test);
		expected_response = composefilterinfo(filtersArray[0], organization_id, user_id, filter_Name, destination_name,
				"none", filter_destination_type, 1);
		expectedresponse.add(expected_response);

		test.log(LogStatus.INFO, "Create the destination filter on policy and destination type");
		filter_Name = RandomStringUtils.randomAlphanumeric(4) + "filter";
		filtersArray[1] = spogDestinationServer.createDestinationFilterWithCheck(user_id, filter_Name, "none",
				filter_policy_id, filter_destination_type, "true", test);
		expected_response = composefilterinfo(filtersArray[1], organization_id, user_id, filter_Name, "none",
				filter_policy_id, filter_destination_type, 1);
		expectedresponse.add(expected_response);

		test.log(LogStatus.INFO, "Create the destination filter on all");
		filter_Name = RandomStringUtils.randomAlphanumeric(4) + "filter";
		filtersArray[2] = spogDestinationServer.createDestinationFilterWithCheck(user_id, filter_Name, destination_name,
				filter_policy_id, filter_destination_type, "true", test);
		expected_response = composefilterinfo(filtersArray[2], organization_id, user_id, filter_Name, destination_name,
				filter_policy_id, filter_destination_type, 1);
		expectedresponse.add(expected_response);

		test.log(LogStatus.INFO, "get destination filter for logged in user of org " + organizationType);
		spogDestinationServer.getDestinationFiltersForLoggedInUserwithCheck("", validToken, expectedresponse,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		/*
		 * test.log(LogStatus.INFO,
		 * "get destination filter for logged in user whose filter_name is "
		 * +filter_Name); String additionalURL =
		 * spogServer.PrepareURL("filter_name;=;"+filter_Name, "", 1, 20, test);
		 * spogDestinationServer.getDestinationFiltersForLoggedInUserwithCheck(
		 * additionalURL, validToken, expectedresponse,
		 * SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		 */
		test.log(LogStatus.INFO, "get destination filter for logged in user whose filter_name is " + filter_Name
				+ " and is_default is set as true");
		String additionalURL = spogServer.PrepareURL(
				"is_default;=;true,destination_type;=;" + DestinationType.cloud_direct_volume.toString(), "", 1, 20,
				test);
		spogDestinationServer.getDestinationFiltersForLoggedInUserwithCheck(additionalURL, validToken, expectedresponse,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		test.log(LogStatus.INFO, "get destination filter for logged in user whose filter_name is " + filter_Name
				+ " and is_default is set as true");
		additionalURL = spogServer.PrepareURL(
				"is_default;=;false,destination_type;=;" + DestinationType.cloud_direct_volume.toString(), "", 1, 20,
				test);
		spogDestinationServer.getDestinationFiltersForLoggedInUserwithCheck(additionalURL, validToken, expectedresponse,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		/*
		 * test.log(LogStatus.INFO,
		 * "get destination filter for logged in user whose filter_name is "
		 * +filter_Name +" and is_default is set as true"); additionalURL =
		 * spogServer.PrepareURL("is_default;=;false", "", 1, 20, test);
		 * spogDestinationServer.getDestinationFiltersForLoggedInUserwithCheck(
		 * additionalURL, validToken, expectedresponse,
		 * SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		 */

		for (int i = 0; i < 3; i++) {
			test.log(LogStatus.INFO, "delete destination filter by filter id");
			spogDestinationServer.deletedestinationfilterbyfilterId(user_id, filtersArray[i], validToken,
					SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		}

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
				{ "msp",ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id, 
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
	public void getdestinationfiltersforloggedinuser_401(String organizationType, String organization_id,
			String validToken, String user_id, String destination_type, String destination_status,
			String primary_usage, String snapshot_usage, String total_usage, String volume_type, String hostname,
			String retention_id, String age_hours_max, String age_four_hours_max, String age_days_max,
			String age_weeks_max, String age_months_max, String age_years_max, String destination_name,
			String filter_destination_name, String filter_destination_type, String filter_policy_id) {
		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);
		String prefix = RandomStringUtils.randomAlphanumeric(4);
		HashMap<String, Object> expected_response = new HashMap<>();
		ArrayList<HashMap<String, Object>> expectedresponse = new ArrayList<HashMap<String, Object>>();
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
		
		

        spogDestinationServer.setToken(ti.csr_token);
        datacenters = spogDestinationServer.getDestionationDatacenterID();	

		test.log(LogStatus.INFO, "Create the destination filter for destination type");
		String filter_Id = spogDestinationServer.createDestinationFilterWithCheck(user_id, prefix + "filter",
				filter_destination_name, filter_policy_id, filter_destination_type, "true", test);

		test.log(LogStatus.INFO, "Compose destination filter info");
		expected_response = composefilterinfo(filter_Id, organization_id, user_id, prefix + "filter",
				filter_destination_name, filter_policy_id, filter_destination_type, 1);
		expectedresponse.add(expected_response);

		test.log(LogStatus.INFO, "get destination filters using invalid token of " + organizationType);
		spogDestinationServer.getDestinationFiltersForLoggedInUserwithCheck("", validToken + "J", expectedresponse,
				SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);

		test.log(LogStatus.INFO, "get destination filters using missing token of " + organizationType);
		spogDestinationServer.getDestinationFiltersForLoggedInUserwithCheck("", "", expectedresponse,
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

	@Test(dataProvider = "getdestinationfilter")
	public void getdestinationfilters_200_nofilters(String organizationType, String organization_id, String validToken,
			String user_id, String destination_type, String destination_status, String primary_usage,
			String snapshot_usage, String total_usage, String volume_type, String hostname, String retention_id,
			String age_hours_max, String age_four_hours_max, String age_days_max, String age_weeks_max,
			String age_months_max, String age_years_max, String destination_name, String filter_destination_type,
			String filter_policy_id) {
		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);
		String prefix = RandomStringUtils.randomAlphanumeric(4);
		HashMap<String, Object> expected_response = new HashMap<>();
		ArrayList<HashMap<String, Object>> expectedresponse = new ArrayList<HashMap<String, Object>>();

		SpogMessageCode expected = null;
		spogDestinationServer.setToken(validToken);
		String[] datacenters = spogDestinationServer.getDestionationDatacenterID();

		if (organizationType.equalsIgnoreCase("csrreadonly")) {
			HashMap<String, String> temp = new HashMap<String, String>();
			Response response = spogDestinationServer.getDestinationFilterByUserId(ti.csr_readonly_admin_user_id, temp);
			ArrayList<String> filterList = new ArrayList<String>();
			filterList = response.then().extract().path("data.filter_id");
			for (int i = 0; i < filterList.size(); i++) {
				spogDestinationServer.deletedestinationfilterbyfilterId(ti.csr_readonly_admin_user_id, filterList.get(i),
						ti.csr_readonly_token, SpogConstants.SUCCESS_GET_PUT_DELETE,
						SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			}
		}
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

		test.log(LogStatus.INFO, "get destination filters and the token are from different organization");
		spogDestinationServer.getDestinationFiltersForLoggedInUserwithCheck("", validToken, expectedresponse,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		/*
		 * test.log(LogStatus.INFO, "Delete the destination");
		 * spogDestinationServer.deletedestinationbydestination_Id(
		 * destination_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE,
		 * null, test);
		 */

	}

	@Test(dataProvider = "getdestinationfilter")
	public void getdestinationfilters_200_withdefault_false(String organizationType, String organization_id,
			String validToken, String user_id, String destination_type, String destination_status, String primary_usage,
			String snapshot_usage, String total_usage, String volume_type, String hostname, String retention_id,
			String age_hours_max, String age_four_hours_max, String age_days_max, String age_weeks_max,
			String age_months_max, String age_years_max, String destination_name, String filter_destination_type,
			String filter_policy_id) {
		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);
		String prefix = RandomStringUtils.randomAlphanumeric(4);
		HashMap<String, Object> expected_response = new HashMap<>();
		ArrayList<HashMap<String, Object>> expectedresponse = new ArrayList<HashMap<String, Object>>();
		ArrayList<HashMap<String, Object>> expectedresponse1 = new ArrayList<HashMap<String, Object>>();

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
		test.log(LogStatus.INFO, "Create the destination filter on destination name");
		String filter_Name = RandomStringUtils.randomAlphanumeric(4) + "filter";
		String filter_Id = spogDestinationServer.createDestinationFilterWithCheck(user_id, filter_Name,
				destination_name, "none", "none", "false", test);
		expected_response = composefilterinfo(filter_Id, organization_id, user_id, filter_Name, destination_name,
				"none", "none", 1);
		expectedresponse.add(expected_response);

		test.log(LogStatus.INFO, "get destination filters with is_default as true");
		String additionalURL = spogServer.PrepareURL("is_default;=;true", "", 1, 20, test);
		spogDestinationServer.getDestinationFiltersForLoggedInUserwithCheck(additionalURL, validToken,
				expectedresponse1, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		/*
		 * test.log(LogStatus.INFO, "Delete the destination");
		 * spogDestinationServer.deletedestinationbydestination_Id(
		 * destination_Id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE,
		 * null, test);
		 */
		test.log(LogStatus.INFO, "delete destination filter by filter id");
		spogDestinationServer.deletedestinationfilterbyfilterId(user_id, filter_Id, validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

	}

	public HashMap<String, Object> composefilterinfo(String filter_Id, String organization_Id, String user_Id,
			String filter_name, String destination_name, String policy_id, String destination_type, int count) {
		HashMap<String, Object> expected_response = new HashMap<>();
		expected_response.put("filter_id", filter_Id);
		expected_response.put("organization_id", organization_Id);
		expected_response.put("user_id", user_Id);
		expected_response.put("filter_name", filter_name);
		expected_response.put("policy_id", policy_id);
		expected_response.put("destination_name", destination_name);
		expected_response.put("destination_type", destination_type);
		expected_response.put("count", count);
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
	 * getskippedcount(); bqdb1.updateTable(BQName, runningMachine,
	 * this.buildVersion, String.valueOf(Nooftest),
	 * Integer.toString(count1.getpassedcount()),
	 * Integer.toString(count1.getfailedcount()),
	 * String.valueOf(count1.getskippedcount()), count1.getcreationtime(),
	 * "Failed"); }else {
	 * Nooftest=count1.getpassedcount()+count1.getfailedcount()+count1.
	 * getskippedcount(); bqdb1.updateTable(BQName, runningMachine,
	 * this.buildVersion, String.valueOf(Nooftest),
	 * Integer.toString(count1.getpassedcount()),
	 * Integer.toString(count1.getfailedcount()),
	 * String.valueOf(count1.getskippedcount()), count1.getcreationtime(),
	 * "Passed"); } } catch (ClientProtocolException e) { // TODO Auto-generated
	 * catch block e.printStackTrace(); } catch (IOException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); }
	 * 
	 * }
	 * 
	 * 
	 */}
