package ui.policy;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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
import Constants.UIConstants;
import InvokerServer.Org4SPOGServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;
import ui.spog.server.PolicyHelper;
import ui.spog.server.SPOGUIServer;

public class CreatePolicy4UITest extends base.prepare.Is4Org {

	private SPOGServer spogServer;
	// private UserSpogServer userSpogServer;
	private PolicyHelper policyHelper;
	private Policy4SPOGServer policy4SpogServer;
	private SPOGDestinationServer spogDestinationServer;

	private String csrAdminUserName;
	private String csrAdminPassword;
	private ExtentTest test;

	private String url;

	private String org_model_prefix = this.getClass().getSimpleName();
	private String direct_user_name = "test.direct10001+sub1s31@gmail.com";
	private String direct_password = "Mclaren@2020";
	private String baas_destination_volume = "vol_ztst-4854.zetta.net";
	private String draas_destination_volume = "Disaster Recovery";
	private String account_user_name = "ss.zz@arcserve.com";
	private String account_password = "Cnbjrdqa1!";
	private String direct_sources = "RamyaRPS2";

	private ArrayList<HashMap<String, String>> multipleThrottleScheduleArray1, multipleMergeScheduleArray;
	private ArrayList<String> direct_source_ids, direct_sources_name;
	private ArrayList<HashMap> direct_sources_data;

	String Sources, direct_cloud_account_id, hybrid_destination_id, hybrid_destination_name;

	private HashMap<String, String> retentionInputs = new HashMap<>();

	String cloud_rps_name = null;
	String cloud_rps_port = null;
	String cloud_rps_protocol = null;
	String cloud_rps_username = null;
	String cloud_rps_password = null;
	String csr_site_id = null;
	String csr_site_name = null;
	String csrToken;

	String server_name = null;
	String server_username = null;
	String server_password = null;
	String server_port = null;
	String server_protocol = null;
	String remote_policy = null;
	String retry_minutes = null;
	String retry_num = null;

	String rps_server_name = null, rps_server_id = null, datastore_id = null;

	String direct_org_email, common_password = "Mclaren@2020", direct_organization_id, direct_user_validToken,
			direct_user_id, direct_site_id;
	private SPOGUIServer spogUIServer;
	int i = 0;
	/* private ArrayList<HashMap<String, String>> multipleThrottleScheduleArray1; */

	@BeforeClass
	@Parameters({ "baseURI", "port", "browserType", "maxWaitTimeSec", "logFolder", "csrAdminUserName",
			"csrAdminPassword", "buildVersion", "uiBaseURI" })
	public void beforeClass(String baseURI, String port, String browserType, String maxWaitTimeSec, String logFolder,
			String adminUserName, String adminPassword, String buildVersion, String uiBaseURI)
			throws UnknownHostException {

		spogUIServer=new SPOGUIServer(browserType,Integer.valueOf(maxWaitTimeSec));
		policyHelper = new PolicyHelper(browserType, Integer.valueOf(maxWaitTimeSec));
		policy4SpogServer = new Policy4SPOGServer(baseURI, port);
		spogServer = new SPOGServer(baseURI, port);
		// userSpogServer = new UserSpogServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		this.url = uiBaseURI;
		this.csrAdminUserName = adminUserName;
		this.csrAdminPassword = adminPassword;
		rep = ExtentManager.getInstance("Test", logFolder);
		test = rep.startTest("beforeClass");

		policyHelper.openUrl(url);

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
		prepareExistingEnv();

		if (!direct_sources_name.isEmpty())
			for (int k = 0; k < direct_sources_name.size(); k++) {

				if (k == 0)
					Sources = direct_sources_name.get(k) + ";";
				else
					Sources = Sources + direct_sources_name.get(k) + ";";
				if (k == (direct_sources_name.size() - 1))
					Sources = Sources + direct_sources_name.get(k);
			}

		// compose retention inputs
		retentionInputs = policyHelper.composeRetentionInputs("3", "4", "5", "6", test);

		// compose throttle schedule
		multipleThrottleScheduleArray1 = new ArrayList<HashMap<String, String>>();
		multipleThrottleScheduleArray1.add(policyHelper.composeThrottleScheduleHashMap("10000",
				UIConstants.THROTTLE_RUN_SCHEDULE_TUE + ";" + UIConstants.THROTTLE_RUN_SCHEDULE_WED, "01:00",
				UIConstants.TIME_AM, "02:32", UIConstants.TIME_PM));
		multipleThrottleScheduleArray1.add(policyHelper.composeThrottleScheduleHashMap("500",
				UIConstants.THROTTLE_RUN_SCHEDULE_FRI, "02:00", UIConstants.TIME_PM, "04:55", UIConstants.TIME_PM));

		// compose merge schedule
		multipleMergeScheduleArray = new ArrayList<HashMap<String, String>>();
		multipleMergeScheduleArray.add(policyHelper.composeMergeScheduleHashMap(
				UIConstants.THROTTLE_RUN_SCHEDULE_TUE + ";" + UIConstants.THROTTLE_RUN_SCHEDULE_WED, "01:00",
				UIConstants.TIME_AM, "12:32", UIConstants.TIME_PM));
		multipleMergeScheduleArray.add(policyHelper.composeMergeScheduleHashMap(
				UIConstants.THROTTLE_RUN_SCHEDULE_THU + ";" + UIConstants.THROTTLE_RUN_SCHEDULE_SAT, "07:00",
				UIConstants.TIME_AM, "10:02", UIConstants.TIME_PM));

		spogServer.userLogin(this.direct_org_email, this.common_password);
		policy4SpogServer.setToken(spogServer.getJWTToken());
		Response policiesResponse = policy4SpogServer.getPolicies(null);
		ArrayList<String> policyIdList = policiesResponse.then().extract().path("data.policy_id");
		for (int i = 0; i < policyIdList.size(); i++) {
			/*
			 * policy4SpogServer.deletePolicybyPolicyId(spogServer.getJWTToken(),
			 * policyIdList.get(i), SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			 */
		}
	}

	private void prepareExistingEnv() {

		direct_org_email = "test.direct10001+sub1s31@gmail.com";
		direct_organization_id = "cb2ccd2d-7227-41b7-90e6-0da1dd09375f";
		hybrid_destination_id = "2a02b295-4d10-477a-82d5-45765c42e30f";
		hybrid_destination_name = "sub1s31CHDest1";

		server_name = "10.60.15.83";
		server_username = "Administrator";
		server_password = "Mclaren@2020";
		server_port = "8015";
		server_protocol = "https";
		remote_policy = "Reverse Replication Policy";
		retry_minutes = "5";
		retry_num = "3";

		spogServer.userLogin(this.direct_org_email, common_password);
		test.log(LogStatus.INFO, "Getting the JWTToken for the Logged in user");
		direct_user_validToken = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Get the logged in user id ");
		direct_user_id = spogServer.GetLoggedinUser_UserID();
		test.log(LogStatus.INFO, "The direct org user id is " + direct_user_id);

		test.log(LogStatus.INFO, "get direct cloud account");
		// get direct cloud account
		Response response = spogServer.getCloudAccounts(direct_user_validToken, "", test);
		direct_cloud_account_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");
		direct_site_id = direct_cloud_account_id;

		response = spogDestinationServer.getDestinations(spogServer.getJWTToken(),
				"destination_id=fde61311-87e8-48ba-9cb9-b6806230f58c", test);
		baas_destination_volume = response.then().extract().path("data[" + 0 + "].destination_name").toString();

		direct_source_ids = new ArrayList<String>();
		direct_sources_name = new ArrayList<String>();
		direct_sources_data = new ArrayList<>();

		policyHelper.login_Spog(direct_org_email, common_password);

	}

	@DataProvider(name = "organizationAndUserInfo1")
	public final Object[][] getOrganizationAndUserInfo1() {
		return new Object[][] {

				{ this.direct_org_email, common_password, "baas image test" + RandomStringUtils.randomAlphanumeric(8),
						null, Sources, UIConstants.ACTIVE_TYPE_IMAGE, UIConstants.ACTIVE_OPTION_SELECTDRIVE, "C;E",
						null, baas_destination_volume, "true", "\\\\zhash05-win8-gd\\dest", "12", "00",
						UIConstants.TIME_PM, null, null, UIConstants.POLICY_STATUS_SUCCESS },
				{ this.direct_org_email, common_password, "baas image test" + RandomStringUtils.randomAlphanumeric(8),
						"hello world", null, UIConstants.ACTIVE_TYPE_IMAGE, UIConstants.ACTIVE_OPTION_FULLSYSTEM, null,
						null, baas_destination_volume, "false", null, "11", "37", UIConstants.TIME_AM,
						multipleThrottleScheduleArray1, null, UIConstants.POLICY_STATUS_SUCCESS },
				{ this.direct_org_email, common_password, "baas file test" + RandomStringUtils.randomAlphanumeric(8),
						null, null, UIConstants.ACTIVE_TYPE_FILE, null, null, "c:\\abc", baas_destination_volume,
						"true", "e:\\dest\\file", "12", "00", UIConstants.TIME_PM, null, null,
						UIConstants.POLICY_STATUS_SUCCESS },
				{ this.direct_org_email, common_password, "baas file test" + RandomStringUtils.randomAlphanumeric(8),
						null, Sources, UIConstants.ACTIVE_TYPE_FILE, null, null, "c:\\abc;D:\\hello world\\mydear",
						baas_destination_volume, "false", null, "10", "59", UIConstants.TIME_AM,
						multipleThrottleScheduleArray1,
						"Path@c:\\abc\\def;File@\\hello world\\mydear\\a.txt;Directory@\\abc\\end",
						UIConstants.POLICY_STATUS_SUCCESS },

		};
	}

	@Test(dataProvider = "organizationAndUserInfo1", enabled = true)
	public void addBaasPolicy(String userName, String password, String policyName, String description,
			String sourceNames, String activeType, String activeOption, String drives, String paths,
			String destVolumeName, String enableLocal, String localDestination, String hour, String minute,
			String clock, ArrayList<HashMap<String, String>> throttleScheduleArray, String excludeRules,
			String expectedStatus) {

		policyHelper.addBaasPolicy(policyName, description, sourceNames, activeType, activeOption, drives, paths,
				destVolumeName, enableLocal, localDestination, hour, minute, clock, throttleScheduleArray, excludeRules,
				expectedStatus);
		test.log(LogStatus.INFO, "get policy_id of the created policy to delete");
		Response response = policy4SpogServer.getPolicies(spogServer.getJWTToken(),"policy_name="+policyName);
		String policy_id = response.then().extract().path("data[0].policy_id");
		policy4SpogServer.deletePolicybyPolicyId(spogServer.getJWTToken(), policy_id);

	}

	@DataProvider(name = "organizationAndUserInfo2")
	public final Object[][] getOrganizationAndUserInfo2() {
		return new Object[][] {
				{ this.direct_user_name, direct_password, "draas image test" + RandomStringUtils.randomAlphanumeric(8),
						"hello world", null, UIConstants.ACTIVE_OPTION_FULLSYSTEM, null, draas_destination_volume,
						"false", null, UIConstants.BACKUP_EVERY_15_MINS, null, "05", UIConstants.TIME_AM,
						multipleThrottleScheduleArray1, UIConstants.POLICY_STATUS_SUCCESS },
				{ this.direct_user_name, direct_password, "draas image test" + RandomStringUtils.randomAlphanumeric(8),
						null, null, UIConstants.ACTIVE_OPTION_SELECTDRIVE, "C;E", draas_destination_volume, "true",
						"\\\\zhash05-win8-gd\\dest", UIConstants.BACKUP_EVERY_1_DAY, "01", "55", UIConstants.TIME_PM,
						null, UIConstants.POLICY_STATUS_SUCCESS },
			/*	{ this.direct_user_name, direct_password, "draas image test" + RandomStringUtils.randomAlphanumeric(8),
						null, direct_sources, UIConstants.ACTIVE_OPTION_FULLSYSTEM, null, draas_destination_volume,
						"false", null, UIConstants.BACKUP_EVERY_1_HOUR, "01", "55", null, null,
						UIConstants.POLICY_STATUS_SUCCESS },
				{ this.direct_user_name, direct_password, "draas image test" + RandomStringUtils.randomAlphanumeric(8),
						null, direct_sources, UIConstants.ACTIVE_OPTION_SELECTDRIVE, "C;E", draas_destination_volume,
						"true", "\\\\zhash05-win8-gd\\dest", UIConstants.BACKUP_EVERY_6_HOURS, "05", "25", null,
						multipleThrottleScheduleArray1, UIConstants.POLICY_STATUS_SUCCESS }, */};
	}

	@Test(dataProvider = "organizationAndUserInfo2", enabled = true)
	public void addDraasPolicy(String userName, String password, String policyName, String description,
			String sourceNames, String activeOption, String drives, String destVolumeName, String enableLocal,
			String localDestination, String backupSchedule, String hour, String minute, String clock,
			ArrayList<HashMap<String, String>> throttleScheduleArray, String expectedStatus) {

		policyHelper.addDraasPolicy(policyName, description, sourceNames, activeOption, drives, destVolumeName,
				enableLocal, localDestination, backupSchedule, hour, minute, clock, throttleScheduleArray,
				expectedStatus);

		test.log(LogStatus.INFO, "get policy_id of the created policy to delete");
		spogServer.userLogin(userName, password);
		policy4SpogServer.setToken(spogServer.getJWTToken());
		
		Response response = policy4SpogServer.getPolicies(spogServer.getJWTToken(),"policy_name="+policyName);
		String policy_id = response.then().extract().path("data[0].policy_id");
		policy4SpogServer.deletePolicybyPolicyId(spogServer.getJWTToken(), policy_id);

		
	}

	@DataProvider(name = "policy_configuration_info")
	public final Object[][] policy_configuration_info() {
		return new Object[][] {

				{ "Cloud Hybrid Replication Policy with Replicate From Remotely Managed RPS task",
						"Cloud Hybrid Replication test" + RandomStringUtils.randomAlphanumeric(8),
						"Cloud Hybrid Replication", multipleMergeScheduleArray, hybrid_destination_name,
						UIConstants.POLICY_STATUS_SUCCESS, retentionInputs, server_name, server_username,
						server_password, server_port, server_protocol, remote_policy, retry_minutes, retry_num,
						multipleThrottleScheduleArray1, multipleMergeScheduleArray },

		};
	}

	@Test(dataProvider = "policy_configuration_info")
	public void addCloudHybridReplicationPolicy(String testcase, String policyName, String policyDescription,
			ArrayList<HashMap<String, String>> expectedScheduleArray, String destVolumeName, String expectedStatus,
			HashMap<String, String> retentionInputs, String severname, String server_username, String server_password,
			String server_port, String server_protocal, String remote_policy, String retry_minutes, String retry_num,
			ArrayList<HashMap<String, String>> throttleScheduleArray,
			ArrayList<HashMap<String, String>> mergeScheduleArray) {

		test.log(LogStatus.INFO, "Testcase : " + testcase);
		policyHelper.addCloudHybridReplicationPolicy(policyName, policyDescription, expectedScheduleArray,
				destVolumeName, retentionInputs, expectedStatus, severname, server_username, server_password,
				server_port, server_protocal, remote_policy, retry_minutes, retry_num, throttleScheduleArray,
				mergeScheduleArray, test);

		test.log(LogStatus.INFO, "get policy_id of the created policy to delete");
		Response response = policy4SpogServer.getPolicies(spogServer.getJWTToken(),"policy_name="+policyName);
		String policy_id = response.then().extract().path("data[0].policy_id");
		policy4SpogServer.deletePolicybyPolicyId(spogServer.getJWTToken(), policy_id);
	}

	@AfterClass
	public void destroyEnv() {

		this.updatebd();
		policyHelper.destroy();
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
		// ending test
		// endTest(logger) : It ends the current test and prepares to create HTML report
		rep.endTest(test);
	}

}
