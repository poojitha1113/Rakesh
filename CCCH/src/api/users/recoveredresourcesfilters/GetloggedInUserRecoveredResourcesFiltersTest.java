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

public class GetloggedInUserRecoveredResourcesFiltersTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
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

	String csr_org, filterId;

	private String org_model_prefix = this.getClass().getSimpleName();

	@BeforeClass
	@Parameters({ "baseURI", "port", "csrAdminUserName", "csrAdminPassword", "csrReadOnlyAdminName",
			"csrReadOnlyAdminPassword", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword,
			String readOnlyUserName, String readOnlyPassword, String logFolder, String runningMachine,
			String buildVersion) {

		spogServer = new SPOGServer(baseURI, port);
		spogRecoveredResourceServer = new SPOGRecoveredResourceServer(baseURI, port);
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

		if (count1.isstarttimehit() == 0) {
			System.out.println("into creation time");
			creationTime = System.currentTimeMillis();
			count1.setcreationtime(creationTime);

			try {
				bqdb1.updateTable(BQName, this.runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0",
						String.valueOf(Nooftest), count1.getcreationtime(), "InProgress",
						author + " and Rest server is " + baseURI.split("//")[1]);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

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

		recoveredResourceType.add("all");
		recoveredResourceType.add("clouddirect_draas");
		recoveredResourceType.add("ivm");
		recoveredResourceType.add("vsb");

		ti = new TestOrgInfo(spogServer, test);
		spogServer.setToken(ti.csr_token);

	}

	@DataProvider(name = "get_RecoveredResources_filter_valid")
	public final Object[][] getRecoveredResourcesFilterValidParams() {

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
						recoveredResourceType.get(1), spogServer.ReturnRandom("filter"), "false" }, };

	}

	@Test(dataProvider = "get_RecoveredResources_filter_valid")
	public void getRecoveredResourcesFilterValid_200(String organizationType, String validToken, String user_id,
			String org_id, String policy_id, String state, String OSmajor, String RecoveredResourceType,
			String filter_name, String is_default

	) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		spogRecoveredResourceServer.setToken(validToken);
		ArrayList<HashMap<String, Object>> compose_filters = new ArrayList<HashMap<String, Object>>();

		test.log(LogStatus.INFO, "create specified user RecoveredResources Filter in org:  " + organizationType
				+ "and compare with default filters");

		HashMap<String, Object> compose_filter = new HashMap<String, Object>();

		ArrayList<String> policy = new ArrayList<String>();
		policy.add(policy_id);

		ArrayList<String> State_2 = new ArrayList<String>();
		State_2.add(state);

		ArrayList<String> Osmajor = new ArrayList<String>();
		Osmajor.add(OSmajor);

		ArrayList<String> coveredResourceType = new ArrayList<String>();
		coveredResourceType.add(RecoveredResourceType);

		compose_filter.put("policy_id", policy);
		compose_filter.put("state", State_2);
		compose_filter.put("os_major", Osmajor);
		compose_filter.put("recovered_resource_type", coveredResourceType);
		compose_filter.put("filter_name", filter_name);
		compose_filter.put("is_default", is_default);

		compose_filters.add(compose_filter);

		Response response = spogRecoveredResourceServer.createLoggedInUserRecoveredResourcesFilters(validToken,
				policy_id, state, OSmajor, RecoveredResourceType, filter_name, is_default, test);

		String filter_id = response.then().extract().path("data.filter_id").toString();

		spogRecoveredResourceServer.checkRecoveredResourceFilters(response, filter_id, policy_id, state, OSmajor,
				RecoveredResourceType, filter_name, is_default, org_id, user_id, SpogConstants.SUCCESS_POST,
				SpogMessageCode.SUCCESS_POST, test, filtersHeadContent);

		test.log(LogStatus.INFO, " created filters in the organization");

		HashMap<String, Object> compose_filter_1 = new HashMap<String, Object>();

		ArrayList<String> policy_1 = new ArrayList<String>();
		policy_1.add(policy_id);

		ArrayList<String> State_1 = new ArrayList<String>();
		State_1.add(State.get(3));

		ArrayList<String> Osmajor_1 = new ArrayList<String>();
		Osmajor_1.add(OSmajor);

		ArrayList<String> coveredResourceType_1 = new ArrayList<String>();
		coveredResourceType_1.add(recoveredResourceType.get(0));

		compose_filter_1.put("policy_id", policy_1);
		compose_filter_1.put("state", State_1);
		compose_filter_1.put("os_major", Osmajor_1);
		compose_filter_1.put("recovered_resource_type", coveredResourceType_1);
		compose_filter_1.put("filter_name", filter_name + "abc");
		compose_filter_1.put("is_default", is_default);

		compose_filters.add(compose_filter_1);

		response = spogRecoveredResourceServer.createLoggedInUserRecoveredResourcesFilters(validToken, policy_id,
				State.get(3), OSmajor, recoveredResourceType.get(0), filter_name + "abc", is_default, test);
		filter_id = response.then().extract().path("data.filter_id").toString();

		test.log(LogStatus.INFO, "get logged in user RecoveredResources Filter in org: " + organizationType);
		response = spogRecoveredResourceServer.getLoggedInUserRecoveredResourcesFilters(validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, test);

		spogRecoveredResourceServer.checkRecoveredResourceFilters(response, filter_id, policy_id, State.get(3), OSmajor,
				recoveredResourceType.get(0), filter_name + "abc", is_default, org_id, user_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test, compose_filters);

		test.log(LogStatus.INFO, "Delete the created filters in the organization");

		response = spogRecoveredResourceServer.deleteLoggedInUserRecoveredResourcesFilters(validToken, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, test);

	}

	@DataProvider(name = "get_RecoveredResources_filter_invalid")
	public final Object[][] getRecoveredResourcesFilterInValidParams() {

		return new Object[][] {

				{ "DIRECT", ti.direct_org1_user1_token, ti.direct_org1_user1_id, ti.direct_org1_id,
						UUID.randomUUID().toString(), State.get(0), OSMajor.windows.name(),
						recoveredResourceType.get(0), spogServer.ReturnRandom("filter"), "true" },

		};

	}

	// get the user defined job columns using csr/valid token
	@Test(dataProvider = "get_RecoveredResources_filter_invalid")
	public void getRecoveredResourcesFilterValid_401(String organizationType, String validToken, String user_id,
			String org_id, String policy_id, String state, String OSmajor, String RecoveredResourceType,
			String filter_name, String is_default) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		test.assignAuthor("");

		spogRecoveredResourceServer.setToken(validToken);

		test.log(LogStatus.INFO, "create specified user RecoveredResources Filter in org:  " + organizationType);
		test.log(LogStatus.INFO, "created filters in the organization with invalid token");

		Response response = spogRecoveredResourceServer.createLoggedInUserRecoveredResourcesFilters(validToken,
				policy_id, state, OSmajor, RecoveredResourceType, filter_name, is_default, test);

		String filter_id = response.then().extract().path("data.filter_id").toString();

		spogRecoveredResourceServer.checkRecoveredResourceFilters(response, filter_id, policy_id, state, OSmajor,
				RecoveredResourceType, filter_name, is_default, org_id, user_id, SpogConstants.SUCCESS_POST,
				SpogMessageCode.SUCCESS_POST, test, filtersHeadContent);

		test.log(LogStatus.INFO, "get filters in the organization with invalid token");

		String inValidToken = "abc";

		response = spogRecoveredResourceServer.getLoggedInUserRecoveredResourcesFilters(inValidToken,
				SpogConstants.NOT_LOGGED_IN, test);

		spogRecoveredResourceServer.checkRecoveredResourceFilters(response, filter_id, policy_id, state, OSmajor,
				RecoveredResourceType, filter_name, is_default, org_id, user_id, SpogConstants.NOT_LOGGED_IN,
				SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test, filtersHeadContent);

		String MissingToken = "";

		test.log(LogStatus.INFO, "get filters in the organization with missing token");

		response = spogRecoveredResourceServer.getLoggedInUserRecoveredResourcesFilters(MissingToken,
				SpogConstants.NOT_LOGGED_IN, test);

		spogRecoveredResourceServer.checkRecoveredResourceFilters(response, filter_id, policy_id, state, OSmajor,
				RecoveredResourceType, filter_name, is_default, org_id, user_id, SpogConstants.NOT_LOGGED_IN,
				SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test, filtersHeadContent);

		test.log(LogStatus.INFO, "Delete the created filters in the organization");

		spogRecoveredResourceServer.deleteLoggedInUserRecoveredResourcesFilters(validToken, filter_id,
				SpogConstants.SUCCESS_GET_PUT_DELETE, test);

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
