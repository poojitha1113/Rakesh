
package ui.protect;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.ConnectionStatus;
import Constants.DestinationStatus;
import Constants.DestinationType;
import Constants.ProtectionStatus;
import Constants.SpogConstants;
import InvokerServer.Org4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;
import ui.spog.server.DestinationHelper;
import ui.spog.server.SPOGUIServer;

public class DestinationsPageTest extends base.prepare.Is4Org {

	private SPOGServer spogServer;
	private UserSpogServer userSpogServer;
	private SPOGDestinationServer spogDestinationServer;
	private Org4SPOGServer org4SpogServer;

	private DestinationHelper destinationHelper;
	private SPOGUIServer spogUIServer;

	private String csrAdminUserName;
	private String csrAdminPassword;
	private String csrReadOnlyAdminUserName;
	private String csrReadOnlyAdminPassword;
	private ExtentTest test;

	private String common_password = "Mclaren@2020";

	private String prefix_direct = "spog_UI_ramya_direct";
	private String postfix_email = "@arcserve.com";
	private String direct_org_name = prefix_direct + "_org";
	private String direct_org_email = direct_org_name + postfix_email;
	private String direct_org_first_name = direct_org_name + "_first_name";
	private String direct_org_last_name = direct_org_name + "_last_name";
	private String direct_user_name = prefix_direct + "_admin";
	private String direct_user_name_email = prefix_direct + "_admin" + postfix_email;
	private String direct_user_first_name = direct_user_name + "_first_name";
	private String direct_user_last_name = direct_user_name + "_last_name";
	private String direct_user_validToken, direct_organization_id;

	private String prefix_msp = "spogqa_shuo_msp";
	private String msp_org_name = prefix_msp + "_org";
	private String msp_user_name = prefix_msp + "_admin";
	private String msp_user_name_email = prefix_msp + "_admin" + postfix_email;
	private String msp_user_first_name = msp_user_name + "_first_name";
	private String msp_user_last_name = msp_user_name + "_last_name";
	private String msp_org_id = null;
	private String final_msp_user_name_email = null;

	private String prefix_msp_account = "spogqa_shuo_msp_account";
	private String msp_account_admin_name = prefix_msp_account + "_admin";
	private String msp_account_admin_email = msp_account_admin_name + postfix_email;
	private String msp_account_admin_first_name = msp_account_admin_name + "_first_name";
	private String msp_account_admin_last_name = msp_account_admin_name + "_last_name";
	private String msp_account_admin_id;
	private String final_msp_account_admin_email;

	private String account_id;
	private String account_user_email;
	private String direct_user_id;
	private String msp_user_id;
	private String account_user_id;
	private String datacenters[] = { "Zetta Test", "Zetta Stage", "London,UK" };
	private String saveSearch[] = { "saveSearch1", "saveSearch2", "saveSearch3" };
	private String HybridDestinations[] = { "destination_name", "destination_name2", "destination_name3" };
	private String modSaveSearch[] = { "mod_saveSearch1", "mod_saveSearch2", "mod_saveSearch3" };
	private ArrayList<String> destination_Columns = new ArrayList<>();

	private String url;
	private String Volume_name[] = { "dest_1", "dest_2", "dest_3" };
	private String datacenter_id[] = null;

	String[] Retention = { "7 Days ", "14 Days ", "1 Month", "2 Months ", "3 Months ", "6 Months ", "1 Year ",
			"2 Years ", "3 Years ", "7 Years", "10 Years ", "Forever " };

	ArrayList<String> contextual_elements = null;

	private String org_model_prefix = this.getClass().getSimpleName();

	private String direct_org_id;

	@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "csrAdminUserName", "csrAdminPassword", "browserType",
			"maxWaitTimeSec", "buildVersion", "uiBaseURI" })
	public void beforeClass(String baseURI, String port, String logFolder, String adminUserName, String adminPassword,
			String browserType, String maxWaitTimeSec, String buildVersion, String uiBaseURI)
			throws UnknownHostException {

		this.url = uiBaseURI;
		spogServer = new SPOGServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		org4SpogServer = new Org4SPOGServer(baseURI, port);
		this.csrAdminUserName = adminUserName;
		this.csrAdminPassword = adminPassword;

		spogUIServer = new SPOGUIServer(browserType, Integer.valueOf(maxWaitTimeSec));
		spogUIServer.openUrl(url);

		destinationHelper = new DestinationHelper(browserType, Integer.valueOf(maxWaitTimeSec));
		destinationHelper.openUrl(url);

		rep = ExtentManager.getInstance("DestinationPageTest", logFolder);
		test = rep.startTest("beforeClass");

		// this is for update portal
		this.BQName = this.getClass().getSimpleName();
		String author = "Ramya.Nagepalli";
		this.runningMachine = InetAddress.getLocalHost().getHostName();
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date = new java.util.Date();
		this.buildVersion = buildVersion + "_" + dateFormater.format(date);
		Nooftest = 0;
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		if (count1.isstarttimehit() == 0) {
			System.out.println("Into get loggedInUserById");
			creationTime = System.currentTimeMillis();
			count1.setcreationtime(creationTime);
			// creationTime = System.currentTimeMillis();
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

		prepareEnv();

		test.log(LogStatus.INFO, "login to the created Organization :");
		spogUIServer.login_Spog(direct_org_email, common_password);
		/*
		 * { "retention", "type", "protected_data", "source_count", "protection_policy",
		 * "latest_job", "location", "datacenter_region" };
		 */

		destination_Columns.add("Retention");
		destination_Columns.add("Type");
		destination_Columns.add("Protected Data");
		destination_Columns.add("Source Count");
		destination_Columns.add("Protection Policy");
		destination_Columns.add("Latest Job");
		destination_Columns.add("Location");
		destination_Columns.add("Data Center Region");

	}

	private void prepareEnv() {

		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
		// *******************create direct org,user,site**********************/
		String prefix = RandomStringUtils.randomAlphanumeric(8) + org_model_prefix;
		test.log(LogStatus.INFO, "create a direct org with enroll");

		// Create a direct org with enroll

		this.direct_org_email = prefix + this.direct_org_email;
		direct_org_name = prefix + direct_org_name;
		direct_organization_id = spogServer.CreateOrganizationByEnrollWithCheck(direct_org_name + org_model_prefix,
				SpogConstants.DIRECT_ORG, this.direct_org_email, common_password, prefix + direct_org_first_name,
				prefix + direct_org_last_name, test);
		spogServer.userLogin(this.direct_org_email, common_password);
		test.log(LogStatus.INFO, "Getting the JWTToken for the Logged in user");
		direct_user_validToken = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "The token is :" + direct_user_validToken);

		test.log(LogStatus.INFO, "Get the logged in user id ");
		direct_user_id = spogServer.GetLoggedinUser_UserID();
		test.log(LogStatus.INFO, "The direct org user id is " + direct_user_id);

		test.log(LogStatus.INFO, "get destination datacenter_id's using csrtoken ");
		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
		spogDestinationServer.setToken(spogServer.getJWTToken());

		datacenter_id = spogDestinationServer.getDestionationDatacenterID();

		test.log(LogStatus.INFO, "Activate Cloud Hybrid trail License for the Direct organization");
		spogServer.postCloudhybridInFreeTrial(spogServer.getJWTToken(), direct_organization_id, "direct", false, false);

		contextual_elements = new ArrayList<>();

		contextual_elements.add("Edit");
		contextual_elements.add("View Recovery Points");
	
		Response response=spogDestinationServer.getDestinations(spogServer.getJWTToken(), "organization_id="+direct_organization_id, test);		
		
		Volume_name[0]=response.then().extract().path("data["+0+"].destination_name").toString();

	}

	@DataProvider(name = "CloudVolumeInfo1")
	public final Object[][] CloudVolumeInfo1() {
		return new Object[][] {
				{ "Valid Scenario to Create Cloud Direct Volume for the enrolled org", "Direct", direct_org_email,
						common_password, Volume_name[1], datacenters[0], Retention[1], "comment", "", "" },
				{ "Invalid Scenario - Volume name is empty", "Direct", direct_org_email, common_password, "",
						datacenters[0], Retention[1], "comment", "", "" },
				{ "Invalid Scenario - Datacenter is given empty", "Direct", direct_org_email, common_password,
						"volume1", "", Retention[1], "comment", "", "" },
				{ "Invalid Scenario - Retention is given empty", "Direct", direct_org_email, common_password, "volume1",
						datacenters[0], "", "comment", "", "" },
				{ "Invalid Scenario - Volume name is given more than 256 characters", "Direct", direct_org_email,
						common_password,
						"13232546849dddLKJHGFDAasfhdaaadasdfdgdfhfhjmgjipoughkjgjghjghjfhjfhddddddddddddddddddddddddddddddddddddddddddddddDSDDDDDDDDDDDDDdsadscsxcsdvfggggggggggggggggjjjjjjjjjjjjjjjjjjjjnnnnnnnnnnnnnnnnvvvvvvvvvvvvvvccccccccccccccfghjjvvvvvvvvvvvvccccccccccccccfghjj",
						datacenters[0], Retention[1], "comment", "The length of destination_name is incorrect.", "" },
				// For Cloud Direct destinations page
				{ "Valid Scenario to Create Cloud Direct Volume for the enrolled org  at Cloud Direct destinations page",
						"Direct", direct_org_email, common_password, Volume_name[2], datacenters[0], Retention[1],
						"comment", "", "CD" },
				{ "Invalid Scenario - Volume name is empty", "Direct", direct_org_email, common_password, "",
						datacenters[0], Retention[1], "comment", "", "CD" },
				{ "Invalid Scenario - Datacenter is given empty", "Direct", direct_org_email, common_password,
						"volume1", "", Retention[1], "comment", "", "CD" },
				{ "Invalid Scenario - Retention is given empty", "Direct", direct_org_email, common_password, "volume1",
						datacenters[0], "", "comment", "", "CD" },
				{ "Invalid Scenario - Volume name is given more than 256 characters", "Direct", direct_org_email,
						common_password,
						"13232546849dddLKJHGFDAasfhdaaadasdfdgdfhfhjmgjipoughkjgjghjghjfhjfhddddddddddddddddddddddddddddddddddddddddddddddDSDDDDDDDDDDDDDdsadscsxcsdvfggggggggggggggggjjjjjjjjjjjjjjjjjjjjnnnnnnnnnnnnnnnnvvvvvvvvvvvvvvccccccccccccccfghjjvvvvvvvvvvvvccccccccccccccfghjj",
						datacenters[0], Retention[1], "comment", "The length of destination_name is incorrect.",
						"CD" }, };
	}

	@Test(dataProvider = "CloudVolumeInfo1")
	public void addCloudVolumeTest(String testcase, String Org_type, String useremail, String password,
			String Volumename, String datacenter, String retention, String Comments, String exp_msg, String page) {

		test = rep.startTest("AddCloudVolume" + testcase);
		test.assignAuthor("Ramya.Nagepalli");

		if (page.equalsIgnoreCase("cd"))
			destinationHelper.goToCloudDirectDestinationsPage();

		test.log(LogStatus.INFO, "Add Cloud Volume for the Organization" + Org_type + "for Scenario" + testcase);
		destinationHelper.AddCloudVolume(Volumename, datacenter, retention, Comments, exp_msg, test);

		if (testcase.contains("Valid Scenario to Create Cloud Direct Volume for the enrolled org")) {
			spogServer.userLogin(useremail, password);

			test.log(LogStatus.INFO,
					"wait for given seconds to get reflect the added cloud volume in response from UI");
			waitInSec(7);

			test.log(LogStatus.INFO, "get destination from API response");
			Response response = spogDestinationServer.getDestinations(spogServer.getJWTToken(),
					"destination_name=" + Volumename, test);

			test.log(LogStatus.INFO, "validate the Added Cloud Volume for the Organization" + Org_type);
			destinationHelper.verifyAddedCloudVolume(response, Volumename, datacenter, test);
		}

	}

	@DataProvider(name = "manageDestinationColumns")
	public final Object[][] manageDestinationColumns() {
		return new Object[][] { { "Select randomly and validate the destination columns ", "" },
				{ "Select randomly and validate the destination columns ", "" },

				{ "Select randomly and validate the Cloud Direct destination columns ", "CD" },
				{ "Select randomly and validate the Cloud Direct destination columns ", "CD" },

				{ "Select randomly and validate the Cloud Hybrid destination columns ", "CH" },
				{ "Select randomly and validate the Cloud Hybrid destination columns ", "CH" },

		};
	}

	@Test(dataProvider = "manageDestinationColumns")
	public void modifyDestinationColumns_valid(String testcase, String page) {
		test = rep.startTest("manageSavedSearch");
		test.assignAuthor("Ramya.Nagepalli");

		spogUIServer.refresh();
		waitInSec(3);

		if (page.equalsIgnoreCase("cd"))
			destinationHelper.goToCloudDirectDestinationsPage();

		if (page.equalsIgnoreCase("ch"))
			destinationHelper.goToCloudHybridDestinationsPage();

		int exp = gen_random_index(destination_Columns);
		ArrayList<String> exp_columns = new ArrayList<String>();

		for (int i = 0; i < exp; i++) {

			String exp_col = destination_Columns.get(i);
			exp_columns.add(exp_col);
		}

		waitInSec(3);
		test.log(LogStatus.INFO, "modify destination columns by selecting the columns from settings ");
		destinationHelper.enableColumnsWithCheck(exp_columns, test);

		waitInSec(3);
		spogUIServer.refresh();

	}

	@DataProvider(name = "dest_info")
	public final Object[][] dest_info() {
		return new Object[][] { { "create destination for the direct organization ", "direct", direct_organization_id,
				direct_org_email, common_password },

		};
	}

	@Test(dataProvider = "dest_info", priority = 1)
	public void createDestinations(String Testcase, String OrgType, String org_id, String UserEmail, String Password) {

		test = rep.startTest("PostDestinations");
		test.assignAuthor("Ramya.Nagepalli");

		test.log(LogStatus.INFO, "get token");
		spogServer.userLogin(UserEmail, Password);
		spogDestinationServer.setToken(spogServer.getJWTToken());

		test.log(LogStatus.INFO, "create Cloud Hybrid Destinations for the organization");
		for (int i = 0; i < 20; i++) {

			spogDestinationServer.createHybridDestination(spogServer.getJWTToken(), org_id, datacenter_id[0],
					DestinationType.cloud_hybrid_store.toString(), HybridDestinations[0] + (i + 1),
					DestinationStatus.running.toString(), test);
		}

		test.log(LogStatus.INFO, "created Cloud Hybrid Destinations for the organization");
	}

	@DataProvider(name = "bulk_actions_info")
	public final Object[][] bulk_actions_info() {
		return new Object[][] {

				{ "Perform bulk action for the given selected destinations", new String[] { "destination_name3" },
						"No actions supported", "" },
				{ "Perform bulk action for the all destinations",
						new String[] { "destination_name3", "destination_name2" }, "No actions supported", "" },
				{ "Perform bulk action for the given selected Cloud Direct destinations", new String[] { Volume_name[0] },
						"No actions supported", "CD" },
				{ "Perform bulk action for the given selected destinations", new String[] {  Volume_name[0] },
						"No actions supported", "" },
				{ "Perform bulk action for the all Cloud Hybrid destinations", new String[] { "all" }, "", "CH" },
				{ "Perform bulk action for the all Cloud Direct destinations", new String[] { "all" }, "", "CD" },
				{ "Perform bulk action for the all destinations", new String[] { "all" }, null, "" },

		};
	}

	@Test(dataProvider = "bulk_actions_info", dependsOnMethods = "createDestinations", priority = 2)
	public void performBulkActions(String testcase, String[] input, String action, String page) {

		spogUIServer.refresh();
		waitInSec(2);

		test = rep.startTest("performBulkActions");
		test.assignAuthor("Ramya.Nagepalli");

		if (page.equalsIgnoreCase("cd"))
			destinationHelper.goToCloudDirectDestinationsPage();

		if (page.equalsIgnoreCase("ch"))
			destinationHelper.goToCloudHybridDestinationsPage();

		ArrayList<String> input_ele = new ArrayList<>();

		for (int i = 0; i < input.length; i++) {
			input_ele.add(input[i]);
		}

		test.log(LogStatus.INFO, "Testcase : " + testcase);
		destinationHelper.performBulkActions(input_ele, action, test);

		spogUIServer.refresh();

	}

	@DataProvider(name = "contextual_action_info")
	public final Object[][] contextual_action_info() {
		return new Object[][] {
				{ "perform contextual actions for the selected Cloud Hybrid destination", HybridDestinations[2], "CH" },
				{ "perform contextual actions for the selected destination", HybridDestinations[1], "" },
				{ "perform contextual actions for the selected Cloud Direct destination", Volume_name[0], "CD" }, };
	}

	@Test(dataProvider = "contextual_action_info", dependsOnMethods = "createDestinations")
	public void testContextualActions(String testcase, String volume_name, String page) {

		test = rep.startTest("ContextualActions");
		test.assignAuthor("Ramya.Nagepalli");

		waitInSec(2);
		spogUIServer.refresh();

		if (page.equalsIgnoreCase("cd"))
			destinationHelper.goToCloudDirectDestinationsPage();

		if (page.equalsIgnoreCase("ch"))
			destinationHelper.goToCloudHybridDestinationsPage();

		test.log(LogStatus.INFO, "Testcase : " + testcase);
		destinationHelper.checkContextualActions(volume_name, contextual_elements, test);

		waitInSec(2);
		spogUIServer.refresh();
	}

	@DataProvider(name = "pagination_info")
	public final Object[][] pagination_info() {
		return new Object[][] {

				{ "check default pagination for Destinations page", 20, "" },

				{ "modify pagination with given input for destinations page ", 5, "" },
				{ "modify pagination with given input for destinations page ", 10, "" },
				{ "modify pagination with given input for destinations page ", 20, "" },
				{ "modify pagination with given input for destinations page ", 25, "" },
				{ "modify pagination with given input for destinations page ", 50, "" },
				{ "modify pagination with given input for destinations page ", 100, "" },

				{ "check default pagination for cloud direct Destinations page", 20, "" },

				{ "modify pagination with given input for Cloud Direct destinations page ", 5, "CD" },
				{ "modify pagination with given input for Cloud Direct destinations page ", 10, "CD" },
				{ "modify pagination with given input for Cloud Direct destinations page ", 20, "CD" },
				{ "modify pagination with given input for Cloud Direct destinations page ", 25, "CD" },
				{ "modify pagination with given input for Cloud Direct destinations page ", 50, "CD" },
				{ "modify pagination with given input for Cloud Direct destinations page ", 100, "CD" },

				{ "check default pagination for cloud hybrid Destinations page", 20, "" },

				{ "modify pagination with given input for Cloud Hybrid destinations page ", 5, "CH" },
				{ "modify pagination with given input for Cloud Hybrid destinations page ", 10, "CH" },
				{ "modify pagination with given input for Cloud Hybrid destinations page ", 20, "CH" },
				{ "modify pagination with given input for Cloud Hybrid destinations page ", 25, "CH" },
				{ "modify pagination with given input for Cloud Hybrid destinations page ", 50, "CH" },
				{ "modify pagination with given input for Cloud Hybrid destinations page ", 100, "CH" },

		};
	}

	@Test(dataProvider = "pagination_info", dependsOnMethods = "createDestinations")
	public void testPagination(String testcase, int page_size, String page) {

		test.log(LogStatus.INFO, "testcase : " + testcase);
		int exp_pages = 0;

		if (page.equalsIgnoreCase("cd"))
			destinationHelper.goToCloudDirectDestinationsPage();

		if (page.equalsIgnoreCase("ch"))
			destinationHelper.goToCloudHybridDestinationsPage();

		spogServer.userLogin(direct_org_email, common_password);
		Response response = spogDestinationServer.getDestinations(spogServer.getJWTToken(), "", test);
		int rows_count = response.then().extract().path("pagination.total_size");

		if (testcase.contains("modify")) {
			exp_pages = destinationHelper.modifyPagination(page_size, rows_count, test);

			if (exp_pages > 1) {
				Random generator = new Random();
				int input_page = generator.nextInt(exp_pages);
				destinationHelper.navigateToSpecifiedPage(input_page, page_size, test);
			}
		} else
			destinationHelper.checkDefaultPagination(page_size, rows_count, test);

	}

	@DataProvider(name = "filterInfo")
	public Object[][] filterInfo() {
		return new Object[][] {

				{ "Search by destination name", HybridDestinations[0], null,
						"destination_name;=;" + HybridDestinations[0], "" },
				{ "Search by Policy name", null, "Protection Policy;Cloud Direct Full System",
						"policy_name;=;Cloud Direct Full System", "" },
				{ "Search by Policy name in CD page", null, "Protection Policy;Cloud Direct Full System",
						"policy_name;=;Cloud Direct Full System", "CD" },
				{ "Search by destination_name in CD page", Volume_name[1], null, "destination_name;=;" + Volume_name[1],
						"CD" },
				{ "Search by destination_name in CH page", HybridDestinations[0], null,
						"destination_name;=;" + HybridDestinations[0], "CH" },

				{ "Search by destination name in destinations page",  Volume_name[0],
						"Protection Policy;Cloud Direct Full System", "policy_name;=;Cloud Direct Full System", "" },

				{ "Search by destination name in CD page",  Volume_name[0], "Protection Policy;Cloud Direct Full System",
						"policy_name;=;Cloud Direct Full System", "CD" },

		};
	}

	@Test(dataProvider = "filterInfo", dependsOnMethods = "createDestinations")
	public void destinationSearchTest(String caseType, String dest_name, String filter, String apiFilter, String page) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		ArrayList<HashMap<String, Object>> expectedInfo = null;
		ArrayList<String> expectedDestinationNames = new ArrayList<>();
		ArrayList<String> filters = new ArrayList<>();

		expectedDestinationNames.add(dest_name);
		spogServer.userLogin(direct_org_email, common_password);
		Response response = spogDestinationServer.getDestinations(spogServer.getJWTToken(), apiFilter, test);
		expectedInfo = response.then().extract().path("data");

		if (page.equalsIgnoreCase("cd"))
			destinationHelper.goToCloudDirectDestinationsPage();

		if (page.equalsIgnoreCase("ch"))
			destinationHelper.goToCloudHybridDestinationsPage();

		if (filter != null && !filter.isEmpty()) {
			String[] multipleFilters = filter.split("&");
			for (int i = 0; i < multipleFilters.length; i++) {
				filters.add(multipleFilters[i]);
			}
		}

		destinationHelper.searchDestinationWithCheck(dest_name, filters, expectedDestinationNames, expectedInfo, test);

		destinationHelper.clearSearchWithoutSaving();
	}

	@DataProvider(name = "manageSaveSearchInfo")
	public Object[][] manageSaveSearchInfo() {
		return new Object[][] {

				{ "Create saved search with given inputs and modify the created save search", saveSearch[0],
						modSaveSearch[0],  Volume_name[0], false, "Protection Policy;Cloud Direct Full System",
						"Protection Policy;Cloud Direct Full System", "" },
				{ "Create saved search with given inputs and modify the created save search in CD page", saveSearch[0],
						modSaveSearch[0],  Volume_name[0], false, "Protection Policy;Cloud Direct Full System",
						"Protection Policy;Cloud Direct Full System", "CD" },
				{ "Create saved search with given inputs and modify the created save search in CH page", saveSearch[0],
						modSaveSearch[0], HybridDestinations[0], true, "Protection Policy;Cloud Direct Full System",
						"Protection Policy;Cloud Direct Full System", "CH" },

				{ "Create saved search with given inputs and modify the created save search", saveSearch[1],
						modSaveSearch[1], Volume_name[0], false, null, "Protection Policy;Cloud Direct Full System",
						"" },
				{ "Create saved search with given inputs and modify the created save search in CD page", saveSearch[1],
						modSaveSearch[1], Volume_name[1], false, null, "Protection Policy;Cloud Direct Full System",
						"CD" },
				{ "Create saved search with given inputs and modify the created save search in CH page", saveSearch[1],
						modSaveSearch[1], HybridDestinations[1], true, "Protection Policy;Cloud Direct Full System",
						null, "CH" },

		};
	}

	@Test(dataProvider = "manageSaveSearchInfo", dependsOnMethods = "createDestinations")
	public void manageSaveSearchTest(String caseType, String searchName, String newSearchName, String search_string,
			boolean makeDefault, String filter, String updateFilter, String page) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());

		if (page.equalsIgnoreCase("cd"))
			destinationHelper.goToCloudDirectDestinationsPage();

		if (page.equalsIgnoreCase("ch"))
			destinationHelper.goToCloudHybridDestinationsPage();

		ArrayList<String> filters = new ArrayList<>();
		ArrayList<String> updatefilters = new ArrayList<>();

		if (filter != null && !filter.isEmpty()) {
			String[] multipleFilters = filter.split("&");
			for (int i = 0; i < multipleFilters.length; i++) {
				filters.add(multipleFilters[i]);
			}
		}
		destinationHelper.saveSearchWithCheck(searchName, search_string, filters, test);

		if (updateFilter != null && !updateFilter.isEmpty()) {
			String[] multipleFilters = updateFilter.split("&");
			for (int i = 0; i < multipleFilters.length; i++) {
				updatefilters.add(multipleFilters[i]);
			}
		}
		test.log(LogStatus.INFO, "unselect the saved search to modify");
		destinationHelper.selectSavedSearch(searchName, false, test);

		destinationHelper.manageSaveSearchWithCheck(searchName, newSearchName, search_string, makeDefault,
				updatefilters, test);
		if (newSearchName != null && !newSearchName.isEmpty()) {
			destinationHelper.deleteSaveSearchWithCheck(newSearchName, test);
		} else {
			destinationHelper.deleteSaveSearchWithCheck(searchName, test);
		}

	}

	@DataProvider(name = "makeSaveSearchDefaultInfo")
	public Object[][] makeSaveSearchDefaultInfo() {
		return new Object[][] {
				{ "Create saved search with given inputs and check it is default", saveSearch[0],  Volume_name[0], true,
						"Protection Policy;Cloud Direct Full System", "" },
				{ "Create saved search with given inputs and check it is default in CH page", saveSearch[1],
						HybridDestinations[1], true, null, "CH" },
				{ "Create saved search with given inputs and check it is default in CD page", saveSearch[2],  Volume_name[0],
						true, "Protection Policy;Cloud Direct Full System", "CD" }, };
	}

	@Test(dataProvider = "makeSaveSearchDefaultInfo", dependsOnMethods = "createDestinations")
	public void makeSavedSearchDefault(String caseType, String searchName, String search_string, boolean makeDefault,
			String filter, String page) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());

		if (page.equalsIgnoreCase("cd"))
			destinationHelper.goToCloudDirectDestinationsPage();

		if (page.equalsIgnoreCase("ch"))
			destinationHelper.goToCloudHybridDestinationsPage();

		ArrayList<String> filters = new ArrayList<>();
		ArrayList<String> updatefilters = new ArrayList<>();

		if (filter != null && !filter.isEmpty()) {
			String[] multipleFilters = filter.split("&");
			for (int i = 0; i < multipleFilters.length; i++) {
				filters.add(multipleFilters[i]);
			}
		}
		destinationHelper.saveSearchWithCheck(searchName, search_string, filters, test);

		destinationHelper.makeSaveSearchDefaultWithCheck(searchName, makeDefault, test);
		destinationHelper.deleteSaveSearchWithCheck(searchName, test);
	}

	/**
	 * waitInMilliSec - method used to wait for milli seconds
	 * 
	 * @author Ramya.Nagepalli
	 * @param ms
	 */
	public void waitInSec(int ms) {

		ms = ms * 1000;
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * gen_random_index - method used to generate random input
	 * 
	 * @author Ramya.Nagepalli
	 * @param job_status
	 * @return
	 */

	public int gen_random_index(ArrayList<String> job_status) {
		Random generator = new Random();
		int randomindx = generator.nextInt(job_status.size());

		return randomindx;
	}

	@AfterMethod
	public void getResult(ITestResult result) {
		if (result.getStatus() == ITestResult.FAILURE) {
			count1.setfailedcount();
			test.log(LogStatus.FAIL, "Test Case Failed is " + result.getName() + " with parameters as "
					+ Arrays.asList(result.getParameters()));
			test.log(LogStatus.FAIL, result.getThrowable().getMessage());
		} else if (result.getStatus() == ITestResult.SKIP) {
			count1.setskippedcount();
			test.log(LogStatus.SKIP, "Test Case Skipped is " + result.getName());
		} else if (result.getStatus() == ITestResult.SUCCESS) {
			count1.setpassedcount();
		}
		// spogUIServer.destroy();
		rep.endTest(test);
	}

	@AfterClass
	public void destroy_UI_instance() {

		spogUIServer.logout();
		spogUIServer.refresh();

		spogUIServer.destroy();
	}
}
