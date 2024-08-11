package api.users.recoveredresourcesfilters;

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

import Constants.OSMajor;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.SPOGJobServer;
import InvokerServer.SPOGRecoveredResourceServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class DeleteSpecifiedUserRecoveredResourcesFiltersTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private SPOGJobServer spogJobServer;
	private SPOGRecoveredResourceServer spogRecoveredResourceServer;

	private ExtentTest test;
	private TestOrgInfo ti;

	ArrayList<String> State = new ArrayList<String>();
	ArrayList<String> recoveredResourceType = new ArrayList<String>();

	LocalDate date = LocalDate.now();
	LocalDate yesterday = date.minusDays(1);
	LocalDate tomorrow = yesterday.plusDays(2);

	ArrayList<String> columnIdList = new ArrayList<String>();
	ArrayList<HashMap<String, Object>> columnsHeadContent = new ArrayList<HashMap<String, Object>>();

	ArrayList<HashMap<String, Object>> filtersHeadContent = new ArrayList<HashMap<String, Object>>();
	ArrayList<String> filter_id = new ArrayList<String>();

	String csr_org_id, filterId;

	private String org_model_prefix = this.getClass().getSimpleName();

	@BeforeClass
	@Parameters({ "baseURI", "port", "csrAdminUserName", "csrAdminPassword", "csrReadOnlyAdminName",
			"csrReadOnlyAdminPassword", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword,
			String readOnlyUserName, String readOnlyPassword, String logFolder, String runningMachine,
			String buildVersion) {

		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogJobServer = new SPOGJobServer(baseURI, port);
		spogRecoveredResourceServer = new SPOGRecoveredResourceServer(baseURI, port);
		rep = ExtentManager.getInstance(this.getClass().getSimpleName(), logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Ramya.Nagepalli";

		Nooftest = 0;
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date = new java.util.Date();
		this.buildVersion = buildVersion + "_" + dateFormater.format(date);

		BQName = this.getClass().getSimpleName();
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

		// place all elements in state
		// all,provisioning,started,stopped,provisioning_failed,deprovisioning,deprovisioned,starting,stopping,restarting
		State.add("all");
		State.add("provisioning");
		State.add("started");
		State.add("stopped");
		State.add("provisioning_failed");
		State.add("deprovisioning");
		State.add("deprovisioned");
		State.add("starting");
		State.add("stopping");
		State.add("restarting");

		// place all elements in recovered resources types
		// all,clouddirect_draas,ivm,vsb

		recoveredResourceType.add("all");
		recoveredResourceType.add("clouddirect_draas");
		recoveredResourceType.add("ivm");
		recoveredResourceType.add("vsb");

		ti = new TestOrgInfo(spogServer, test);
		spogServer.setToken(ti.csr_token);
	}

	@DataProvider(name = "delete_RecoveredResources_filter_valid")
	public final Object[][] deleteRecoveredResourcesFilterValidParams() {

		return new Object[][] {

				{ "CSR-readOnly", ti.csr_readonly_token, ti.csr_readonly_admin_user_id, ti.csr_org_id,
						UUID.randomUUID().toString(), State.get(0), OSMajor.windows.name(),
						recoveredResourceType.get(0), spogServer.ReturnRandom("filter"), "true" },

				{ "DIRECT", ti.direct_org1_user1_token, ti.direct_org1_user1_id, ti.direct_org1_id,
						UUID.randomUUID().toString(), State.get(0), OSMajor.windows.name(),
						recoveredResourceType.get(0), spogServer.ReturnRandom("filter"), "true" },
				{ "MSP", ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id, ti.normal_msp_org1_id,
						UUID.randomUUID().toString(), State.get(1), OSMajor.windows.name(),
						recoveredResourceType.get(1), spogServer.ReturnRandom("filter"), "false" },
				{ "SUBORG", ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id,
						ti.normal_msp1_suborg1_id, UUID.randomUUID().toString(), State.get(3), OSMajor.windows.name(),
						recoveredResourceType.get(2), spogServer.ReturnRandom("filter"), "true" },

				// 3 tier cases
				{ "Root MSP", ti.root_msp_org1_user1_token, ti.root_msp_org1_user1_id, ti.root_msp_org1_id,
						UUID.randomUUID().toString(), State.get(1), OSMajor.windows.name(),
						recoveredResourceType.get(1), spogServer.ReturnRandom("filter"), "false" },
				{ "sub MSP", ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,
						ti.root_msp1_submsp_org1_id, UUID.randomUUID().toString(), State.get(1), OSMajor.windows.name(),
						recoveredResourceType.get(1), spogServer.ReturnRandom("filter"), "false" },
				{ "sub_org-sub_MSP", ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,
						ti.msp1_submsp1_sub_org1_id, UUID.randomUUID().toString(), State.get(1), OSMajor.windows.name(),
						recoveredResourceType.get(1), spogServer.ReturnRandom("filter"), "false" },

		};

	}

	// get the user defined job columns using csr/valid token
	@Test(dataProvider = "delete_RecoveredResources_filter_valid")
	public void deleteRecoveredResourcesFilterValid_200(String organizationType, String validToken, String user_id,
			String org_id, String policy_id, String state, String OSmajor, String recoveredResourceType,
			String filter_name, String is_default

	) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		spogRecoveredResourceServer.setToken(validToken);
		test.log(LogStatus.INFO, "create specified user RecoveredResources Filter in org:  " + organizationType
				+ "and compare with default filters");

		Response response = spogRecoveredResourceServer.createSpecifiedUserRecoveredResourcesFilters(user_id,
				validToken, policy_id, state, OSmajor, recoveredResourceType, filter_name, is_default, test);

		String filter_id = response.then().extract().path("data.filter_id").toString();

		spogRecoveredResourceServer.checkRecoveredResourceFilters(response, filter_id, policy_id, state, OSmajor,
				recoveredResourceType, filter_name, is_default, org_id, user_id, SpogConstants.SUCCESS_POST,
				SpogMessageCode.SUCCESS_POST, test, filtersHeadContent);

		test.log(LogStatus.INFO, "Delete the created filters in the organization");

		response = spogRecoveredResourceServer.deleteSpecifiedUserRecoveredResourcesFiltersByFilterId(user_id,
				filter_id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, test);

	}

	@DataProvider(name = "delete_RecoveredResources_filter_invalid")
	public final Object[][] deleteRecoveredResourcesFilterInValidParams() {

		return new Object[][] {
				{ "CSR-readOnly", ti.csr_readonly_token, ti.csr_readonly_admin_user_id, ti.csr_org_id,
						UUID.randomUUID().toString(), State.get(0), OSMajor.windows.name(),
						recoveredResourceType.get(0), spogServer.ReturnRandom("filter"), "true" },

				{ "DIRECT", ti.direct_org1_user1_token, ti.direct_org1_user1_id, ti.direct_org1_id,
						UUID.randomUUID().toString(), State.get(0), OSMajor.windows.name(),
						recoveredResourceType.get(0), spogServer.ReturnRandom("filter"), "true" },
				{ "MSP", ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id, ti.normal_msp_org1_id,
						UUID.randomUUID().toString(), State.get(1), OSMajor.windows.name(),
						recoveredResourceType.get(1), spogServer.ReturnRandom("filter"), "false" },
				{ "SUBORG", ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id,
						ti.normal_msp1_suborg1_id, UUID.randomUUID().toString(), State.get(3), OSMajor.windows.name(),
						recoveredResourceType.get(2), spogServer.ReturnRandom("filter"), "true" },

				// 3 tier cases
				{ "Root MSP", ti.root_msp_org1_user1_token, ti.root_msp_org1_user1_id, ti.root_msp_org1_id,
						UUID.randomUUID().toString(), State.get(1), OSMajor.windows.name(),
						recoveredResourceType.get(1), spogServer.ReturnRandom("filter"), "false" },
				{ "sub MSP", ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,
						ti.root_msp1_submsp_org1_id, UUID.randomUUID().toString(), State.get(1), OSMajor.windows.name(),
						recoveredResourceType.get(1), spogServer.ReturnRandom("filter"), "false" },
				{ "sub_org-sub_MSP", ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,
						ti.msp1_submsp1_sub_org1_id, UUID.randomUUID().toString(), State.get(1), OSMajor.windows.name(),
						recoveredResourceType.get(1), spogServer.ReturnRandom("filter"), "false" },

		};

	}

	// get the user defined job columns using csr/valid token
	@Test(dataProvider = "delete_RecoveredResources_filter_invalid")
	public void deleteRecoveredResourcesFilterValid_401(String organizationType, String validToken, String user_id,
			String org_id, String policy_id, String state, String OSmajor, String recoveredResourceType,
			String filter_name, String is_default) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		test.assignAuthor("");

		spogRecoveredResourceServer.setToken(validToken);

		test.log(LogStatus.INFO, "create specified user RecoveredResources Filter in org:  " + organizationType);

		test.log(LogStatus.INFO, "created filters in the organization with valid token");

		Response response = spogRecoveredResourceServer.createSpecifiedUserRecoveredResourcesFilters(user_id,
				validToken, policy_id, state, OSmajor, recoveredResourceType, filter_name, is_default, test);

		String filter_id = response.then().extract().path("data.filter_id").toString();

		spogRecoveredResourceServer.checkRecoveredResourceFilters(response, filter_id, policy_id, state, OSmajor,
				recoveredResourceType, filter_name, is_default, org_id, user_id, SpogConstants.SUCCESS_POST,
				SpogMessageCode.SUCCESS_POST, test, filtersHeadContent);

		String MissingToken = "";

		test.log(LogStatus.INFO, "delete filters in the organization with missing token");

		response = spogRecoveredResourceServer.deleteSpecifiedUserRecoveredResourcesFiltersByFilterId(user_id,
				filter_id, MissingToken, SpogConstants.NOT_LOGGED_IN, test);

		spogRecoveredResourceServer.checkRecoveredResourceFilters(response, filter_id, policy_id, state, OSmajor,
				recoveredResourceType, filter_name, is_default, org_id, user_id, SpogConstants.NOT_LOGGED_IN,
				SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test, filtersHeadContent);

		String inValidToken = "abc";

		test.log(LogStatus.INFO, "delete filters in the organization with invalid token");

		response = spogRecoveredResourceServer.deleteSpecifiedUserRecoveredResourcesFiltersByFilterId(user_id,
				filter_id, inValidToken, SpogConstants.NOT_LOGGED_IN, test);

		spogRecoveredResourceServer.checkRecoveredResourceFilters(response, filter_id, policy_id, state, OSmajor,
				recoveredResourceType, filter_name, is_default, org_id, user_id, SpogConstants.NOT_LOGGED_IN,
				SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test, filtersHeadContent);

		test.log(LogStatus.INFO, "Delete the created filters in the organization");

		response = spogRecoveredResourceServer.deleteSpecifiedUserRecoveredResourcesFiltersByFilterId(user_id,
				filter_id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, test);

	}

	@DataProvider(name = "delete_RecoveredResources_filter_invalid_403")
	public final Object[][] deleteRecoveredResourcesFilterInValidParams_403() {

		return new Object[][] { { "direct-msp", ti.direct_org1_user1_token, ti.direct_org1_user1_id, ti.direct_org1_id,
				UUID.randomUUID().toString(), State.get(0), OSMajor.windows.name(), recoveredResourceType.get(0),
				spogServer.ReturnRandom("filter"), "true", ti.normal_msp_org1_user1_token },
				{ "direct-suborg", ti.direct_org1_user1_token, ti.direct_org1_user1_id, ti.direct_org1_id,
						UUID.randomUUID().toString(), State.get(0), OSMajor.windows.name(),
						recoveredResourceType.get(0), spogServer.ReturnRandom("filter"), "true",
						ti.msp1_submsp1_suborg1_user1_token },
				{ "MSP-direct", ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id, ti.normal_msp_org1_id,
						UUID.randomUUID().toString(), State.get(1), OSMajor.windows.name(),
						recoveredResourceType.get(1), spogServer.ReturnRandom("filter"), "false",
						ti.direct_org1_user1_token },
				{ "MSP-mspb", ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id, ti.normal_msp_org1_id,
						UUID.randomUUID().toString(), State.get(1), OSMajor.windows.name(),
						recoveredResourceType.get(1), spogServer.ReturnRandom("filter"), "false",
						ti.normal_msp_org2_user1_token },
				{ "MSP-suborg", ti.normal_msp_org1_user1_token, ti.normal_msp_org1_user1_id, ti.normal_msp_org1_id,
						UUID.randomUUID().toString(), State.get(1), OSMajor.windows.name(),
						recoveredResourceType.get(1), spogServer.ReturnRandom("filter"), "false",
						ti.msp1_submsp1_suborg1_user1_token },
				{ "SUBORG-direct", ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id,
						ti.normal_msp1_suborg1_user1_id, UUID.randomUUID().toString(), State.get(3),
						OSMajor.windows.name(), recoveredResourceType.get(2), spogServer.ReturnRandom("filter"), "true",
						ti.direct_org1_user1_token },
				{ "SUBORG-msp", ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id,
						ti.normal_msp1_suborg1_user1_id, UUID.randomUUID().toString(), State.get(3),
						OSMajor.windows.name(), recoveredResourceType.get(2), spogServer.ReturnRandom("filter"), "true",
						ti.normal_msp_org2_user1_token },
				{ "SUBORG-suborgb", ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id,
						ti.normal_msp1_suborg1_user1_id, UUID.randomUUID().toString(), State.get(3),
						OSMajor.windows.name(), recoveredResourceType.get(2), spogServer.ReturnRandom("filter"), "true",
						ti.root_msp1_suborg1_user1_token },
				{ "SUBORG_MSP", ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id,
						ti.normal_msp1_suborg1_user1_id, UUID.randomUUID().toString(), State.get(3),
						OSMajor.windows.name(), recoveredResourceType.get(2), spogServer.ReturnRandom("filter"), "true",
						ti.root_msp1_suborg1_user1_token },
				{ "csr-direct", ti.csr_token, ti.csr_admin_user_id, ti.csr_org_id, UUID.randomUUID().toString(),
						State.get(0), OSMajor.windows.name(), recoveredResourceType.get(0),
						spogServer.ReturnRandom("filter"), "true", ti.direct_org1_user1_token },
				{ "csr-msp", ti.csr_token, ti.csr_admin_user_id, ti.csr_org_id, UUID.randomUUID().toString(),
						State.get(0), OSMajor.windows.name(), recoveredResourceType.get(0),
						spogServer.ReturnRandom("filter"), "true", ti.root_msp_org1_user1_token },
				{ "csr-suborg", ti.csr_token, ti.csr_admin_user_id, ti.csr_org_id, UUID.randomUUID().toString(),
						State.get(0), OSMajor.windows.name(), recoveredResourceType.get(0),
						spogServer.ReturnRandom("filter"), "true", ti.root_msp1_suborg1_user1_token },
				{ "csr-mspAdminAccount", ti.csr_token, ti.csr_admin_user_id, ti.csr_org_id,
						UUID.randomUUID().toString(), State.get(0), OSMajor.windows.name(),
						recoveredResourceType.get(0), spogServer.ReturnRandom("filter"), "true",
						ti.normal_msp_org1_msp_accountadmin1_token },

				{ "csr-read-only-direct", ti.csr_readonly_token, ti.csr_readonly_admin_user_id, ti.csr_org_id,
						UUID.randomUUID().toString(), State.get(0), OSMajor.windows.name(),
						recoveredResourceType.get(0), spogServer.ReturnRandom("filter"), "true",
						ti.direct_org1_user1_token },
				{ "csr-read-only-msp", ti.csr_readonly_token, ti.csr_readonly_admin_user_id, ti.csr_org_id,
						UUID.randomUUID().toString(), State.get(0), OSMajor.windows.name(),
						recoveredResourceType.get(0), spogServer.ReturnRandom("filter"), "true",
						ti.normal_msp_org1_user1_token },
				{ "csr-read-only-suborg", ti.csr_readonly_token, ti.csr_readonly_admin_user_id, ti.csr_org_id,
						UUID.randomUUID().toString(), State.get(0), OSMajor.windows.name(),
						recoveredResourceType.get(0), spogServer.ReturnRandom("filter"), "true",
						ti.normal_msp1_suborg1_user1_token },
				{ "csr-read-only-mspAdminAccount", ti.csr_readonly_token, ti.csr_readonly_admin_user_id, ti.csr_org_id,
						UUID.randomUUID().toString(), State.get(0), OSMajor.windows.name(),
						recoveredResourceType.get(0), spogServer.ReturnRandom("filter"), "true",
						ti.normal_msp_org1_msp_accountadmin1_token },

				// 3 tier architecture cases
				{ "INSUFFICIENT_PERMISSIONS of root msp-suborgb", ti.root_msp_org1_user1_token,
						ti.root_msp_org1_user1_id, ti.root_msp_org1_id, UUID.randomUUID().toString(), State.get(0),
						OSMajor.windows.name(), recoveredResourceType.get(0), spogServer.ReturnRandom("filter"), "true",
						ti.root_msp1_suborg1_user1_token },

				{ "INSUFFICIENT_PERMISSIONS of submsp1-root_suborgb", ti.root_msp1_submsp1_user1_token,
						ti.root_msp1_submsp1_user1_id, ti.root_msp1_submsp_org1_id, UUID.randomUUID().toString(),
						State.get(0), OSMajor.windows.name(), recoveredResourceType.get(0),
						spogServer.ReturnRandom("filter"), "true", ti.root_msp1_suborg1_user1_token },

				{ "INSUFFICIENT_PERMISSIONS of root_suborgb-submsp1", ti.root_msp1_suborg1_user1_token,
						ti.root_msp1_suborg1_user1_id, ti.root_msp1_suborg1_id, UUID.randomUUID().toString(),
						State.get(0), OSMajor.windows.name(), recoveredResourceType.get(0),
						spogServer.ReturnRandom("filter"), "true", ti.root_msp1_submsp1_user1_token },

				{ "INSUFFICIENT_PERMISSIONS of submsp1-submsp1_suborg", ti.root_msp1_submsp1_user1_token,
						ti.root_msp1_submsp1_user1_id, ti.root_msp1_submsp_org1_id, UUID.randomUUID().toString(),
						State.get(0), OSMajor.windows.name(), recoveredResourceType.get(0),
						spogServer.ReturnRandom("filter"), "true", ti.msp1_submsp1_suborg1_user1_token },

				{ "INSUFFICIENT_PERMISSIONS of root_msp-submsp1", ti.root_msp_org1_user1_token,
						ti.root_msp_org1_user1_id, ti.root_msp_org1_id, UUID.randomUUID().toString(), State.get(0),
						OSMajor.windows.name(), recoveredResourceType.get(0), spogServer.ReturnRandom("filter"), "true",
						ti.root_msp1_submsp1_user1_token },

				{ "INSUFFICIENT_PERMISSIONS of root_msp-submsp1_suborg", ti.root_msp_org1_user1_token,
						ti.root_msp_org1_user1_id, ti.root_msp_org1_id, UUID.randomUUID().toString(), State.get(0),
						OSMajor.windows.name(), recoveredResourceType.get(0), spogServer.ReturnRandom("filter"), "true",
						ti.msp1_submsp1_suborg1_user1_token }, };

	}

	// get the user defined job columns using csr/valid token
	@Test(dataProvider = "delete_RecoveredResources_filter_invalid_403")
	public void deleteRecoveredResourcesFilterValid_403(String organizationType, String validToken, String user_id,
			String org_id, String policy_id, String state, String OSmajor, String recoveredResourceType,
			String filter_name, String is_default, String otherorgToken) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		spogRecoveredResourceServer.setToken(validToken);
		test.log(LogStatus.INFO, "create specified user RecoveredResources Filter in org:  " + organizationType
				+ "and compare with default filters");

		Response response = spogRecoveredResourceServer.createSpecifiedUserRecoveredResourcesFilters(user_id,
				validToken, policy_id, state, OSmajor, recoveredResourceType, filter_name, is_default, test);

		String filter_id = response.then().extract().path("data.filter_id").toString();
		test.log(LogStatus.INFO, "delete filters in the organization with other org token");
		response = spogRecoveredResourceServer.deleteSpecifiedUserRecoveredResourcesFiltersByFilterId(user_id,
				filter_id, otherorgToken, SpogConstants.INSUFFICIENT_PERMISSIONS, test);

		spogRecoveredResourceServer.checkRecoveredResourceFilters(response, filter_id, policy_id, state, OSmajor,
				recoveredResourceType, filter_name, is_default, org_id, user_id, SpogConstants.INSUFFICIENT_PERMISSIONS,
				SpogMessageCode.RESOURCE_PERMISSION_DENY, test, filtersHeadContent);

		test.log(LogStatus.INFO, "Delete the created filters in the organization");
		response = spogRecoveredResourceServer.deleteSpecifiedUserRecoveredResourcesFiltersByFilterId(user_id,
				filter_id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, test);

	}

	// get the user defined job columns using csr/valid token
	@Test(dataProvider = "delete_RecoveredResources_filter_invalid")
	public void deleteRecoveredResourcesFilterValid_404(String organizationType, String validToken, String user_id,
			String org_id, String policy_id, String state, String OSmajor, String recoveredResourceType,
			String filter_name, String is_default) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		String filter_id = UUID.randomUUID().toString();

		test.log(LogStatus.INFO, "delete filters in the organization with random user id");

		Response response = spogRecoveredResourceServer.deleteSpecifiedUserRecoveredResourcesFiltersByFilterId(user_id,
				filter_id, validToken, SpogConstants.RESOURCE_NOT_EXIST, test);

		spogRecoveredResourceServer.checkRecoveredResourceFilters(response, filter_id, policy_id, state, OSmajor,
				recoveredResourceType, filter_name, is_default, org_id, user_id, SpogConstants.RESOURCE_NOT_EXIST,
				SpogMessageCode.FILTER_NOT_FOUND_WITH_USER_ID, test, filtersHeadContent);

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
