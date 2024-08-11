package ui.monitor;

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

import Constants.SpogConstants;
import InvokerServer.Org4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGReportServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;
import ui.base.common.Widgets;
import ui.spog.server.DestinationHelper;
import ui.spog.server.MonitorHelper;
import ui.spog.server.SPOGUIServer;

public class mspOrgMonitorTest extends base.prepare.Is4Org {

	private SPOGServer spogServer;
	private UserSpogServer userSpogServer;
	private SPOGDestinationServer spogDestinationServer;
	private Org4SPOGServer org4SpogServer;
	private SPOGReportServer spogReportServer;
	private MonitorHelper monitorHelper;

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

	private String prefix_msp = "spog_UI_ramya_msp";
	private String msp_org_name = prefix_msp + "_org";
	private String msp_user_name = prefix_msp + "_admin";
	private String msp_user_name_email = prefix_msp + "_admin" + postfix_email;
	private String msp_user_first_name = msp_user_name + "_first_name";
	private String msp_user_last_name = msp_user_name + "_last_name";
	private String msp_org_id = null;
	private String final_msp_user_name_email = null;
	private String msp_user_validToken;

	private String prefix_msp_account = "spogqa_ramya_msp_account";
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
	private String HybridDestinations[] = { "destination_name1", "destination_name2", "destination_name3" };
	private String modSaveSearch[] = { "mod_saveSearch1", "mod_saveSearch2", "mod_saveSearch3" };
	private String destination_Columns[] = { "retention", "type", "protected_data", "source_count", "protection_policy",
			"latest_job", "location", "datacenter_region" };

	private String url;
	private String Volume_name[] = { "dest_1", "dest_2", "dest_3" };
	private String datacenter_id[] = null;

	String[] Retention = { "7 Days ", "14 Days ", "1 Month", "2 Months ", "3 Months ", "6 Months ", "1 Year ",
			"2 Years ", "3 Years ", "7 Years", "10 Years ", "Forever " };

	ArrayList<String> contextual_elements = null;

	private String org_model_prefix = this.getClass().getSimpleName();

	private String direct_org_id;
	private ArrayList<String> total_Widgets_msp, total_Widgets_direct;

	private String UserName, Password;

	/*@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "UserName", "Password", "browserType", "maxWaitTimeSec",
			"buildVersion", "uiBaseURI" })
	public void beforeClass(String baseURI, String port, String logFolder, String adminUserName, String adminPassword,
			String browserType, String maxWaitTimeSec, String buildVersion, String uiBaseURI)
			throws UnknownHostException {*/
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
		spogReportServer = new SPOGReportServer(baseURI, port);
		this.UserName = adminUserName;
		this.Password = adminPassword;

		rep = ExtentManager.getInstance("mspOrgMonitorTest", logFolder);
		test = rep.startTest("beforeClass");

		spogUIServer = new SPOGUIServer(browserType, Integer.valueOf(maxWaitTimeSec));
		spogUIServer.openUrl(url);

		destinationHelper = new DestinationHelper(browserType, Integer.valueOf(maxWaitTimeSec));
		destinationHelper.openUrl(url);

		monitorHelper = new MonitorHelper(browserType, Integer.valueOf(maxWaitTimeSec));
		monitorHelper.openUrl(url);

		rep = ExtentManager.getInstance("mspOrgMonitorTest", logFolder);
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

		test.log(LogStatus.INFO, "login to the created Organization :");
		spogUIServer.login_Spog("test.direct10001+msps27@gmail.com", "Mclaren@2020");

		total_Widgets_direct = new ArrayList<>();
		total_Widgets_direct.add(Widgets.Backup_Jobs_Status_Summary);
		total_Widgets_direct.add(Widgets.Recent_10_Jobs_In_Progress);
		total_Widgets_direct.add(Widgets.Top_10_Sources);
		total_Widgets_direct.add(Widgets.Top_10_Policies);
		total_Widgets_direct.add(Widgets.Data_Transfer_Summary_For_Cloud_Direct_Volumes);
		total_Widgets_direct.add(Widgets.Dedupe_Savings_Trend_For_Cloud_Hybrid_Stores);
		total_Widgets_direct.add(Widgets.Usage_Trend_For_Cloud_Direct_Volumes);
		total_Widgets_direct.add(Widgets.Usage_Trend_For_Cloud_Hybird_Stores);

		total_Widgets_msp = new ArrayList<>();
		total_Widgets_msp.add(Widgets.Backup_Jobs_Status_Summary);
		total_Widgets_msp.add(Widgets.Recent_10_Jobs_In_Progress);
		total_Widgets_msp.add(Widgets.Top_10_Sources);
		total_Widgets_msp.add(Widgets.Top_10_Policies);
		total_Widgets_msp.add(Widgets.Top_10_Customers);
		total_Widgets_msp.add(Widgets.Data_Transfer_Summary_For_Cloud_Direct_Volumes);
		total_Widgets_msp.add(Widgets.Usage_Trend_For_Cloud_Hybird_Stores);
		total_Widgets_msp.add(Widgets.Usage_Trend_For_Cloud_Direct_Volumes);
		total_Widgets_msp.add(Widgets.Dedupe_Savings_Trend_For_Cloud_Hybrid_Stores);

	}

	private void prepareEnv() {

		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
		// *******************create direct org,user,site**********************/
		String prefix = RandomStringUtils.randomAlphanumeric(8) + org_model_prefix;
		test.log(LogStatus.INFO, "create a msp org");

		// Create a msp org with enroll

		this.msp_user_name_email = prefix + this.msp_user_name_email;
		msp_org_name = prefix + msp_org_name;
		msp_org_id = spogServer.CreateOrganizationByEnrollWithCheck(msp_org_name + org_model_prefix,
				SpogConstants.MSP_ORG, this.msp_user_name_email, common_password, prefix + msp_user_first_name,
				prefix + msp_user_last_name, test);
		spogServer.userLogin(this.msp_user_name_email, common_password);
		test.log(LogStatus.INFO, "Getting the JWTToken for the Logged in user");
		msp_user_validToken = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "The token is :" + msp_user_validToken);

		test.log(LogStatus.INFO, "Get the logged in user id ");
		msp_user_id = spogServer.GetLoggedinUser_UserID();
		test.log(LogStatus.INFO, "The msp org user id is " + msp_user_id);

		test.log(LogStatus.INFO, "get destination datacenter_id's using csrtoken ");
		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
		spogDestinationServer.setToken(spogServer.getJWTToken());

		datacenter_id = spogDestinationServer.getDestionationDatacenterID();

		test.log(LogStatus.INFO, "Activate Cloud Hybrid trail License for the MSP organization");
		spogServer.postCloudhybridInFreeTrial(spogServer.getJWTToken(), msp_org_id, "msp", false, false);

	}

	@DataProvider(name = "cust_info")
	public final Object[][] cust_info() {
		return new Object[][] { { " validate customers info on monitor page " }, };
	}

	@Test(dataProvider = "cust_info")
	public void CustomerSummary(String testcase) {

		test.log(LogStatus.INFO, "get the accounts for the org from API");
		spogServer.userLogin(UserName, common_password);
		String org_id = spogServer.GetLoggedinUserOrganizationID();
		Response response = spogServer.getAllaccountsForSpecifiedMsp(spogServer.getJWTToken(), org_id, "", test);

		int count = response.then().extract().path("pagination.total_size");

		test.log(LogStatus.INFO, "testcase :" + testcase);
		monitorHelper.validateCustomerSummary(count, test);
	}

	@DataProvider(name = "source_info")
	public final Object[][] source_info() {
		return new Object[][] { { " validate sources info on source summary across customers on monitor page " }, };
	}

	@Test(dataProvider = "source_info")
	public void SourceSummaryAcrossCustomers(String testcase) {
		test.log(LogStatus.INFO, "get the sources info from api response");
		spogServer.userLogin(UserName, common_password);
		Response response = spogReportServer.getDashboardSourceSummary(spogServer.getJWTToken(), "", test);

		int count = response.then().extract().path("data.total");
		int protected_count = response.then().extract().path("data.protected");
		int not_protected = response.then().extract().path("data.notProtected");
		int offline = response.then().extract().path("data.offline");

		test.log(LogStatus.INFO, "testcase :" + testcase);
		monitorHelper.validateSourceSummaryAcrossCustomers(count, protected_count, not_protected, offline, test);
	}

	@DataProvider(name = "usage_info")
	public final Object[][] usage_info() {
		return new Object[][] { { " validate Usage info on Usage summary across customers on monitor page " }, };
	}

	@Test(dataProvider = "usage_info")
	public void usageSummaryAcrossCustomers(String testcase) {
		test.log(LogStatus.INFO, "get the usage summary info from api response");
		spogServer.userLogin(UserName, common_password);
		Response response = org4SpogServer.getLoggedInUserOrganizationEntitlement(spogServer.getJWTToken());

		HashMap<String, Object> cd_usage = response.then().extract().path("data.clouddirect.baas");
		HashMap<String, Object> ch_usage = response.then().extract().path("data.cloudhybrid.baas");

		Long cd = (Long) cd_usage.get("usage");
		Long ch = (Long) ch_usage.get("usage");

		int cloud_direct_usage = (int) (cd / (1024 * 1024 * 1024));
		int cloud_hybrid_usage = (int) (ch / (1024 * 1024 * 1024));

		Long cd_capacity = (Long) cd_usage.get("capacity");
		Long ch_capacity = (Long) ch_usage.get("capacity");

		int cloud_direct_capacity = 0;
		int cloud_hybrid_capacity = 0;

		if (!(cd_capacity == null)) {
			cloud_direct_capacity = (int) (cd_capacity / (1024 * 1024 * 1024));
			cloud_hybrid_capacity = (int) (ch_capacity / (1024 * 1024 * 1024));
		}

		test.log(LogStatus.INFO, "testcase " + testcase);
		monitorHelper.validateUsageSummaryAcrossCustomers(cloud_direct_usage, cloud_hybrid_usage, cloud_direct_capacity,
				cloud_hybrid_capacity, test);
	}

	@DataProvider(name = "widgets_info")
	public final Object[][] widgets_info() {

		return new Object[][] {
				{ "validate the widgets on monitor page and maximize the given widgets",
						gen_random_index(total_Widgets_msp) },
				{ "validate the widgets on monitor page and maximize the given widgets",
						gen_random_index(total_Widgets_msp) },
				{ "validate the widgets on monitor page and maximize the given widgets",
						gen_random_index(total_Widgets_msp) },
				{ "validate the widgets on monitor page and maximize the given widgets",
						gen_random_index(total_Widgets_msp) },
				{ "validate the widgets on monitor page and maximize the given widgets",
						gen_random_index(total_Widgets_msp) }, };
	}

	@Test(dataProvider = "widgets_info")
	public void testMSPWidgets(String testcase, int input) {

		test.log(LogStatus.INFO, "get the API response of policy summary details for the login org");

		ArrayList<String> expected_widgets = new ArrayList<>();

		for (int i = 0; i < input; i++)
			expected_widgets.add(total_Widgets_msp.get(i));

		test.log(LogStatus.INFO, "testcase : " + testcase);
		monitorHelper.validateWidgets(total_Widgets_msp, test);
		monitorHelper.maximize_validate_widgets(expected_widgets, test);
	}

	@DataProvider(name = "alerts_info")
	public final Object[][] alerts_info() {
		return new Object[][] { { " validate alerts pane and total alerts on monitor page ", "Information" },
				{ " validate alerts pane and total alerts on monitor page ", "Critical" },
				{ " validate alerts pane and total alerts on monitor page ", "all" }, };
	}

	@Test(dataProvider = "alerts_info")
	public void TestAlerts(String testcase, String expected) {

		test.log(LogStatus.INFO, "testcase :" + testcase);
		monitorHelper.validateAlerts(expected, test);
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
	 * @param total_Widgets_msp2
	 * @return
	 */

	public int gen_random_index(ArrayList<String> total_Widgets_msp2) {
		Random generator = new Random();
		int randomindx = generator.nextInt(total_Widgets_msp2.size());
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
