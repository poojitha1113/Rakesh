package api.organizations.alerts;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.http.client.ClientProtocolException;
import org.eclipse.jetty.io.SocketChannelEndPoint;
import org.omg.Messaging.SyncScopeHelper;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGAlertsServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import base.prepare.TestOrgInfo;
import dataPreparation.JsonPreparation;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;
import net.bytebuddy.dynamic.scaffold.subclass.SubclassImplementationTarget.OriginTypeResolver;

public class GetMailForSpecifiedAlertTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private SPOGAlertsServer spogAlertsServer;
	private SPOGDestinationServer spogDestinationServer;
	private Policy4SPOGServer policy4SpogServer;
	private JsonPreparation jp;

	private ExtentTest test;
	private TestOrgInfo ti;

	ArrayList<String> type = new ArrayList<String>();
	private String job_Type = "restore,restore";
	private String job_Method = "full,incremental,resync,full,incremental,resync,full,incremental,resync,full,incremental,resync";
	public String JobSeverity = "warning,information,critical,warning,information,critical,warning,information,critical,warning,information,critical";
	private String test_log_Message_1 = "testLogMessage", test_log_Message_2 = "connect_node_failed_test_message";
	public String JobStatus = "failed,failed,failed,incomplete,idle,waiting,crash,license_failed,backupjob_proc_exit,skipped,stop,active,finished";

	private String Direct_cloud_id;
	private String msp_cloud_account_id;
	private String root_cloud_id;
	private GatewayServer gatewayServer;
	private ArrayList<String> allowed_actions = new ArrayList<>();
	private String direct_group_id = null;
	private String normalSub_group_id = null;
	private String rootSub_group_id = null;
	private String submspSub_group_id = null;

	@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runningMachine, String buildVersion) {

		spogServer = new SPOGServer(baseURI, port);
		spogAlertsServer = new SPOGAlertsServer(baseURI, port);
		policy4SpogServer = new Policy4SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		jp = new JsonPreparation();

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
		// Alert Types
		type.add("backup_failed");
		type.add("backup_missed");
		type.add("backup_errors");
		type.add("backup_warnings");
		type.add("backup_success");

		// available action
		allowed_actions.add("edit");
		allowed_actions.add("delete");
		/*
		 * type.add("policy_errors"); type.add("policy_warnings");
		 * type.add("policy_success"); type.add("recovery_failed");
		 * type.add("recovery_errors"); type.add("recovery_warnings");
		 * type.add("recovery_success"); type.add("clouddirect_trial_start");
		 * type.add("clouddirect_trial_warinig"); type.add("cloudhybrid_trial_start");
		 * type.add("clouddirect_trial_expiration");
		 * type.add("cloudhybrid_trial_warining");
		 * type.add("cloudhybrid_trial_expiration");
		 * type.add("clouddirect_cloud_configuration");
		 * type.add("clouddirect_capacity_warning");
		 * type.add("clouddirect_capacity_reached");
		 * type.add("cloudhybrid_store_configuration");
		 * type.add("cloudhybrid_capacity_warning");
		 * type.add("cloudhybrid_capacity_reached");
		 * type.add("clouddirect_license_expiration");
		 * type.add("cloudhybrid_license_expiration");
		 * type.add("clouddirect_license_warning");
		 * type.add("cloudhybrid_license_warning");
		 * type.add("clouddirect_capacity_disabled");
		 * type.add("cloudhybrid_capacity_disabled");
		 * type.add("clouddirect_baas_threshold_warning");
		 * type.add("cloudhybrid_baas_threshold_warning");
		 */

		// get cloud account for the direct organization
		Response response = spogServer.getCloudAccounts(ti.direct_org1_user1_token, "", test);
		Direct_cloud_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");

		// get cloud account for the msp organization
		response = spogServer.getCloudAccounts(ti.normal_msp_org1_user1_token, "", test);
		msp_cloud_account_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");

		// get cloud account for the root msp organization
		response = spogServer.getCloudAccounts(ti.root_msp_org1_user1_token, "", test);
		root_cloud_id = response.then().extract().path("data[" + 0 + "].cloud_account_id");

	}

	@DataProvider(name = "sourceInfo")
	public final Object[][] sourceInfo() {
		return new Object[][] { { "direct", ti.direct_org1_id, ti.direct_org1_user1_token },
				{ "normalSub", ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token },
				{ "rootSub", ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_token },
				{ "submspSub", ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_token }, };
	}

	@Test(dataProvider = "sourceInfo", dependsOnMethods = "deleteResources")
	public void createSourceGroups(String org_type, String org_id, String validToken) {
		test.log(LogStatus.INFO, "create source group of type : " + org_type);
		String group_name = spogServer.ReturnRandom("group_name");
		spogServer.setToken(validToken);
		String group_id = spogServer.createGroupWithCheck(org_id, group_name, group_name, test);

		if (org_type.equalsIgnoreCase("direct"))
			direct_group_id = group_id;
		else if (org_type.equalsIgnoreCase("normalSub"))
			normalSub_group_id = group_id;
		else if (org_type.equalsIgnoreCase("rootSub"))
			rootSub_group_id = group_id;
		else if (org_type.equalsIgnoreCase("submspSub"))
			submspSub_group_id = group_id;

	}

	@DataProvider(name = "alertsInfo")
	public final Object[][] alertsInfo() {
		return new Object[][] {
				{ "Direct", ti.direct_org1_id, ti.direct_org1_user1_token,
						"test.direct@gmail.com,test.direct111@gmail.com", "DirectBackupAlert", "all_sources", "",
						ti.direct_org1_user1_id, ti.direct_org1_user1_email, SpogConstants.SUCCESS_POST,
						SpogMessageCode.SUCCESS_POST, ti.direct_org1_monitor_user1_token },
				{ "MSP", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token, "test.direct@gmail.com",
						"MSPBackupAlert", "all_organizations", "", ti.normal_msp_org1_user1_id,
						ti.normal_msp_org1_user1_email, SpogConstants.SUCCESS_POST, SpogMessageCode.SUCCESS_POST,
						ti.normal_msp_org1_monitor_user1_token },
				{ "suborg", ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token, "test.direct@gmail.com",
						"suborgBackupAlert", "all_sources", "", ti.normal_msp1_suborg1_user1_id,
						ti.normal_msp1_suborg1_user1_email, SpogConstants.SUCCESS_POST, SpogMessageCode.SUCCESS_POST,
						ti.normal_msp1_suborg1_monitor_user1_token },
				{ "RootMSP", ti.root_msp_org1_id, ti.root_msp_org1_user1_token, "test.direct@gmail.com",
						"RootMSPBackupAlert", "all_organizations", "", ti.root_msp_org1_user1_id,
						ti.root_msp_org1_user1_email, SpogConstants.SUCCESS_POST, SpogMessageCode.SUCCESS_POST,
						ti.root_msp_org1_monitor_user1_token },
				{ "SubMSP", ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_user1_token, "test.direct123@gmail.com",
						"SubMSPBackupAlert", "all_organizations", "", ti.root_msp1_submsp1_user1_id,
						ti.root_msp1_submsp1_user1_email, SpogConstants.SUCCESS_POST, SpogMessageCode.SUCCESS_POST,
						ti.root_msp1_submsp1_monitor_user1_token },
				{ "rootsub", ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_token, "test.direct@gmail.com",
						"RootSubBackupAlert", "all_sources", "", ti.root_msp1_suborg1_user1_id,
						ti.root_msp1_suborg1_user1_email, SpogConstants.SUCCESS_POST, SpogMessageCode.SUCCESS_POST,
						ti.root_msp1_suborg1_monitor_user1_token },
				{ "SubMSP_sub", ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_token,
						"test.direct@gmail.com", "SubmspSubBackupAlert", "all_sources", "",
						ti.msp1_submsp1_suborg1_user1_id, ti.msp1_submsp1_suborg1_user1_email,
						SpogConstants.SUCCESS_POST, SpogMessageCode.SUCCESS_POST,
						ti.msp1_submsp1_suborg1_monitor_user1_token },

				// create alerts reports with group_id/organization_id
				{ "Direct- create alerts reports with group_id", ti.direct_org1_id, ti.direct_org1_user1_token,
						"test.direct@gmail.com,test.direct111@gmail.com", "DirectBackupAlert", "selected_source_groups",
						direct_group_id, ti.direct_org1_user1_id, ti.direct_org1_user1_email,
						SpogConstants.SUCCESS_POST, SpogMessageCode.SUCCESS_POST, ti.direct_org1_monitor_user1_token },
				{ "normalMSP- create alerts reports with organization_id", ti.normal_msp_org1_id,
						ti.normal_msp_org1_user1_token, "test.direct@gmail.com", "normalMSPBackupAlert",
						"selected_organizations", ti.normal_msp1_suborg1_id, ti.normal_msp_org1_user1_id,
						ti.normal_msp_org1_user1_email, SpogConstants.SUCCESS_POST, SpogMessageCode.SUCCESS_POST,
						ti.normal_msp_org1_monitor_user1_token },

				{ "suborg- create alerts reports with group_id", ti.normal_msp1_suborg1_id,
						ti.normal_msp1_suborg1_user1_token, "test.direct@gmail.com", "suborgBackupAlert",
						"selected_source_groups", normalSub_group_id, ti.normal_msp1_suborg1_user1_id,
						ti.normal_msp1_suborg1_user1_email, SpogConstants.SUCCESS_POST, SpogMessageCode.SUCCESS_POST,
						ti.normal_msp1_suborg1_monitor_user1_token },
				{ "RootMSP- create alerts reports with organization_id", ti.root_msp_org1_id,
						ti.root_msp_org1_user1_token, "test.direct@gmail.com", "RootMSPBackupAlert",
						"selected_organizations", ti.root_msp1_suborg1_id, ti.root_msp_org1_user1_id,
						ti.root_msp_org1_user1_email, SpogConstants.SUCCESS_POST, SpogMessageCode.SUCCESS_POST,
						ti.root_msp_org1_monitor_user1_token },
				{ "SubMSP- create alerts reports with organization_id", ti.root_msp1_submsp_org1_id,
						ti.root_msp1_submsp1_user1_token, "test.direct123@gmail.com", "SubMSPBackupAlert",
						"selected_organizations", ti.msp1_submsp1_sub_org1_id, ti.root_msp1_submsp1_user1_id,
						ti.root_msp1_submsp1_user1_email, SpogConstants.SUCCESS_POST, SpogMessageCode.SUCCESS_POST,
						ti.root_msp1_submsp1_monitor_user1_token },
				{ "rootsub- create alerts reports with group_id", ti.root_msp1_suborg1_id,
						ti.root_msp1_suborg1_user1_token, "test.direct@gmail.com", "RootSubBackupAlert",
						"selected_source_groups", rootSub_group_id, ti.root_msp1_suborg1_user1_id,
						ti.root_msp1_suborg1_user1_email, SpogConstants.SUCCESS_POST, SpogMessageCode.SUCCESS_POST,
						ti.root_msp1_suborg1_monitor_user1_token },
				{ "SubMSP_sub- create alerts reports with group_id", ti.msp1_submsp1_sub_org1_id,
						ti.msp1_submsp1_suborg1_user1_token, "test.direct@gmail.com", "SubmspSubBackupAlert",
						"selected_source_groups", submspSub_group_id, ti.msp1_submsp1_suborg1_user1_id,
						ti.msp1_submsp1_suborg1_user1_email, SpogConstants.SUCCESS_POST, SpogMessageCode.SUCCESS_POST,
						ti.msp1_submsp1_suborg1_monitor_user1_token },

				// monitor role cases
				{ "Direct_monitor- create alerts reports with group_id", ti.direct_org1_id, ti.direct_org1_user1_token,
						"test.direct@gmail.com,test.direct111@gmail.com", "DirectBackupAlert", "selected_source_groups",
						direct_group_id, ti.direct_org1_user1_id, ti.direct_org1_user1_email,
						SpogConstants.SUCCESS_POST, SpogMessageCode.SUCCESS_POST, ti.direct_org1_monitor_user1_token },
				{ "normalMSP_monitor- create alerts reports with organization_id", ti.normal_msp_org1_id,
						ti.normal_msp_org1_user1_token, "test.direct@gmail.com", "normalMSPBackupAlert",
						"selected_organizations", ti.normal_msp1_suborg1_id, ti.normal_msp_org1_user1_id,
						ti.normal_msp_org1_user1_email, SpogConstants.SUCCESS_POST, SpogMessageCode.SUCCESS_POST,
						ti.normal_msp_org1_monitor_user1_token },

				{ "suborg_monitor- create alerts reports with group_id", ti.normal_msp1_suborg1_id,
						ti.normal_msp1_suborg1_user1_token, "test.direct@gmail.com", "suborgBackupAlert",
						"selected_source_groups", normalSub_group_id, ti.normal_msp1_suborg1_user1_id,
						ti.normal_msp1_suborg1_user1_email, SpogConstants.SUCCESS_POST, SpogMessageCode.SUCCESS_POST,
						ti.normal_msp1_suborg1_monitor_user1_token },
				{ "RootMSP_monitor- create alerts reports with organization_id", ti.root_msp_org1_id,
						ti.root_msp_org1_user1_token, "test.direct@gmail.com", "RootMSPBackupAlert",
						"selected_organizations", ti.root_msp1_suborg1_id, ti.root_msp_org1_user1_id,
						ti.root_msp_org1_user1_email, SpogConstants.SUCCESS_POST, SpogMessageCode.SUCCESS_POST,
						ti.root_msp_org1_monitor_user1_token },
				{ "SubMSP_monitor- create alerts reports with organization_id", ti.root_msp1_submsp_org1_id,
						ti.root_msp1_submsp1_user1_token, "test.direct123@gmail.com", "SubMSPBackupAlert",
						"selected_organizations", ti.msp1_submsp1_sub_org1_id, ti.root_msp1_submsp1_user1_id,
						ti.root_msp1_submsp1_user1_email, SpogConstants.SUCCESS_POST, SpogMessageCode.SUCCESS_POST,
						ti.root_msp1_submsp1_monitor_user1_token },
				{ "rootsub_monitor- create alerts reports with group_id", ti.root_msp1_suborg1_id,
						ti.root_msp1_suborg1_user1_token, "test.direct@gmail.com", "RootSubBackupAlert",
						"selected_source_groups", rootSub_group_id, ti.root_msp1_suborg1_user1_id,
						ti.root_msp1_suborg1_user1_email, SpogConstants.SUCCESS_POST, SpogMessageCode.SUCCESS_POST,
						ti.root_msp1_suborg1_monitor_user1_token },
				{ "SubMSP_sub_monitor- create alerts reports with group_id", ti.msp1_submsp1_sub_org1_id,
						ti.msp1_submsp1_suborg1_user1_token, "test.direct@gmail.com", "SubmspSubBackupAlert",
						"selected_source_groups", submspSub_group_id, ti.msp1_submsp1_suborg1_user1_id,
						ti.msp1_submsp1_suborg1_user1_email, SpogConstants.SUCCESS_POST, SpogMessageCode.SUCCESS_POST,
						ti.msp1_submsp1_suborg1_monitor_user1_token },

		};
	}

	@Test(dataProvider = "alertsInfo", dependsOnMethods = "createSourceGroups")
	public void getEmailsForAlerts(String org_type, String org_id, String token, String recipient, String alertName,
			String Report_for_type, String extra_id, String user_id, String user_email, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage, String monitor_token) {

		test.log(LogStatus.INFO, "submit Emails For Alerts of org :" + org_type);
		Map<String, Object> expectedData = new HashMap<>();
		String alert_email_id = null;

		for (String item : type) {
			expectedData = jp.composeAlertsInfo(item, alertName, org_id, recipient, Report_for_type, extra_id);
			alert_email_id = spogAlertsServer.submitEmailsForAlertswithcheck(token, expectedData, user_id, user_email,
					allowed_actions, expectedStatusCode, expectedErrorMessage, test);

			if (org_type.contains("monitor"))
				spogAlertsServer.getEmailsForSpecifiedAlertswithcheck(monitor_token, expectedData, alert_email_id,
						user_id, user_email, allowed_actions, SpogConstants.SUCCESS_GET_PUT_DELETE,
						SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
			else
				spogAlertsServer.getEmailsForSpecifiedAlertswithcheck(token, expectedData, alert_email_id, user_id,
						user_email, allowed_actions, SpogConstants.SUCCESS_GET_PUT_DELETE,
						SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			test.log(LogStatus.INFO, "Delete the email for created specific alert");
			spogAlertsServer.deleteEmailsForSpecifiedAlert(token, alert_email_id, SpogConstants.SUCCESS_GET_PUT_DELETE,
					SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		}

	}

	@DataProvider(name = "alertsInfo_invalid")
	public final Object[][] alertsInfo_invalid() {
		return new Object[][] {
				{ "invalid token", ti.direct_org1_id, type.get(0), "asdf", "test.direct@gmail.com",
						ti.direct_org1_user1_id, ti.direct_org1_user1_email, SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTHENTICATION_FAILED },
				{ "missing token", ti.direct_org1_id, type.get(0), "", "test.direct@gmail.com", ti.direct_org1_user1_id,
						ti.direct_org1_user1_email, SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT },
				{ "alert_email_id is random UUID", ti.direct_org1_id, type.get(0), ti.direct_org1_user1_token,
						"test.direct@gmail.com", ti.direct_org1_user1_id, ti.direct_org1_user1_email,
						SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.AUDIT_RESOURCE_NOT_FOUND },

		};
	}

	@Test(dataProvider = "alertsInfo_invalid")
	public void getEmailsForAlerts_invalid(String testcase, String org_id, String alert_type, String token,
			String recipient, String user_id, String user_email, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage) {
		test.log(LogStatus.INFO, "get Emails For Alerts :" + testcase);

		spogAlertsServer.getEmailsForSpecifiedAlertswithcheck(token, null, UUID.randomUUID().toString(), user_id,
				user_email, allowed_actions, expectedStatusCode, expectedErrorMessage, test);
	}

	@DataProvider(name = "org_info")
	public final Object[][] org_info() {
		return new Object[][] {

				{ ti.direct_org1_id, ti.direct_org1_user1_token },
				{ ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token },
				{ ti.root_msp_org1_id, ti.root_msp_org1_user1_token },
				{ ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_token },
				{ ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_user1_token },
				{ ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_token },

		};
	}

	@Test(dataProvider = "org_info")
	public void deleteResources(String org_id, String validToken) {

		policy4SpogServer.setToken(validToken);
		Response response = policy4SpogServer.getPolicies(null);
		ArrayList<String> policies = new ArrayList<>();
		policies = response.then().extract().path("data.policy_id");

		if (!policies.isEmpty()) {
			policies.stream().forEach(policy -> {
				policy4SpogServer.deletePolicybyPolicyId(ti.csr_token, policy);
			});
		}

		spogServer.setToken(validToken);
		response = spogServer.getSources("", "", 1, 20, true, test);

		ArrayList<String> sources = new ArrayList<>();
		sources = response.then().extract().path("data.source_id");
		if (!sources.isEmpty()) {
			sources.stream().forEach(source -> {
				spogServer.deleteSourceByID(source, test);
			});
		}

		spogServer.setToken(validToken);
		response = spogServer.getSourceGroups(null);

		ArrayList<String> groups = new ArrayList<>();
		groups = response.then().extract().path("data.group_id");
		if (!groups.isEmpty()) {
			groups.stream().forEach(group -> {
				spogServer.deleteGroup(group, test);
			});
		}

	}

}
