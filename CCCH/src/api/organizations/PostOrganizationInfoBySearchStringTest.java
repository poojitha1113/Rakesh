//getOrganizationInfoBySearchStringTest
package api.organizations;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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

import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.Org4SPOGServer;
import InvokerServer.SPOGServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class PostOrganizationInfoBySearchStringTest extends base.prepare.Is4Org {

	private SPOGServer spogServer;
	private Org4SPOGServer os;
	private GatewayServer gatewayServer;
	private ExtentTest test;
	private TestOrgInfo ti;
	private Response response;
	private String commonPassword = "Mclaren@2013";

	private String  org_model_prefix=this.getClass().getSimpleName();

	ArrayList<String> available_actions = new ArrayList<String>();
	ArrayList<HashMap<String, Object>> get_selected_orgInfo = new ArrayList<HashMap<String, Object>>();

	@BeforeClass
	@Parameters({ "baseURI", "port","csrAdminUserName", "csrAdminPassword","csrReadOnlyAdminName","csrReadOnlyAdminPassword","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword,String csrReadOnlyAdminName,String csrReadOnlyAdminPassword, String logFolder,String runningMachine,String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		os = new Org4SPOGServer(baseURI, port);
		rep = ExtentManager.getInstance("PostOrganizationInfoBySearchStringTest", logFolder);
		Nooftest = 0;
		bqdb1 = new SQLServerDb();
		String author = "malleswari.sykam";
		count1 = new testcasescount();
		test = rep.startTest("beforeClass");
		test.assignAuthor("Malleswari");
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date = new java.util.Date();
		this.buildVersion = buildVersion + "_" + dateFormater.format(date);
		this.BQName = this.getClass().getSimpleName();
		this.runningMachine = runningMachine;
		System.out.println("The value of hit is " + count1.isstarttimehit());

		ti = new TestOrgInfo(spogServer, test);
		/*if (count1.isstarttimehit() == 0) {
			System.out.println("into creation time");
			creationTime = System.currentTimeMillis();
			count1.setcreationtime(creationTime);
			try {
				bqdb1.updateTable(BQName, runningMachine, buildVersion, String.valueOf(Nooftest), "0", "0",
						String.valueOf(Nooftest), count1.getcreationtime(), "InProgress",
						author + " and Rest server is " + baseURI.split("//")[1]);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 */
	}


	@DataProvider(name = "getOrganizationInfo")
	public final Object[][] getOrganizationInfo() {
		return new Object[][] { 
			{ ti.direct_org1_user1_token, ti.direct_org1_name, "direct", ti.csr_org_id, ti.direct_org1_user1_email,
				commonPassword, "firstname", "lastname", 1, 20, "org_id" },
				/*
				 * { direct_user_validToken, direct_org_name, "direct", csrOrg_id,
				 * direct_user_name_email, commonPassword, direct_user_first_name,
				 * direct_user_last_name, 1, 20, "org_id" }, { direct_user_validToken,
				 * direct_org_name, "direct", csrOrg_id, direct_user_name_email, commonPassword,
				 * direct_user_first_name, direct_user_last_name, 1, 20, "org_name" }, {
				 * direct_user_validToken, direct_org_name, "direct", csrOrg_id,
				 * direct_user_name_email, commonPassword, direct_user_first_name,
				 * direct_user_last_name, 1, 20, "org_name" }, { direct_user_validToken,
				 * direct_org_name, "direct", csrOrg_id, direct_user_name_email, commonPassword,
				 * direct_user_first_name, direct_user_last_name, 1, 100, "org_id" }, {
				 * sub_orga_user_validToken, direct_org_name, "msp_child", csrOrg_id,
				 * initial_sub_email_a, commonPassword, initial_sub_first_name_a,
				 * initial_sub_last_name_a, 1, 20, "org_id" }, { sub_orga_user_validToken,
				 * direct_org_name, "msp_child", csrOrg_id, initial_sub_email_a, commonPassword,
				 * initial_sub_first_name_a, initial_sub_last_name_a, 1, 20, "org_name" }, {
				 * sub_orga_user_validToken, direct_org_name, "msp_child", csrOrg_id,
				 * initial_sub_email_a, commonPassword, initial_sub_first_name_a,
				 * initial_sub_last_name_a, 1, 20, "org_id" }, { sub_orga_user_validToken,
				 * direct_org_name, "msp_child", csrOrg_id, initial_sub_email_a, commonPassword,
				 * initial_sub_first_name_a, initial_sub_last_name_a, 1, 100, "org_name" }
				 */
		};
	}

	// get Source by id For different scenarios
	@Test(dataProvider = "getOrganizationInfo")

	public void GetOrganizationInfoBySearchString(String token, String organizationName, String organizationType,
			String organization_id, String organizationEmail, String organizationPwd, String organizationFirstName,
			String organizationLastName, int currPage, int pageSize, String search) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		test.assignAuthor("sykam.nagamalleswari");
		spogServer.userLogin(ti.csr_admin_email, ti.common_password);
		token=spogServer.getJWTToken();
		spogServer.setToken(ti.csr_token);
		ArrayList<HashMap<String, Object>> all_orgInfo = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> compose_orgInfo_1 = new HashMap<String, Object>();
		HashMap<String, Object> compose_orgInfo_2 = new HashMap<String, Object>();
		HashMap<String, Object> compose_orgInfo_3 = new HashMap<String, Object>();

		HashMap<String, Object> compose_mspInfo_1 = new HashMap<String, Object>();
		String search_string = "1#1";
		String organization_id_msp = null;

		String prefix = spogServer.ReturnRandom("eswari");
		test.log(LogStatus.INFO, "creating an organization ");
		if (organizationType == "direct") {
			spogServer.setToken(ti.csr_token);
			prefix = spogServer.ReturnRandom("1#1");
			Response response = spogServer.CreateOrganization(prefix + organizationName + org_model_prefix,
					organizationType, prefix + organizationEmail, organizationPwd, organizationFirstName,
					organizationLastName);
			spogServer.login(prefix + organizationEmail, organizationPwd);
			token = ti.csr_token;
			organization_id = response.then().extract().path("data.organization_id");
			compose_orgInfo_1.put("organization_name", prefix + organizationName);
			compose_orgInfo_1.put("type", organizationType);
			compose_orgInfo_1.put("organization_id", organization_id);
			compose_orgInfo_1.put("blocked", false);
			compose_orgInfo_1.put("msp", null);
			all_orgInfo.add(compose_orgInfo_1);

			prefix = spogServer.ReturnRandom("1#1");
			response = spogServer.CreateOrganization(prefix + organizationName + org_model_prefix, organizationType,
					prefix + organizationEmail, organizationPwd, organizationFirstName, organizationLastName);
			organization_id = response.then().extract().path("data.organization_id");
			compose_orgInfo_2.put("organization_name", prefix + organizationName);
			compose_orgInfo_2.put("type", organizationType);
			compose_orgInfo_2.put("blocked", false);
			compose_orgInfo_2.put("msp", null);
			compose_orgInfo_2.put("organization_id", organization_id);
			all_orgInfo.add(compose_orgInfo_2);

			prefix = spogServer.ReturnRandom("1#1");
			response = spogServer.CreateOrganization(prefix + organizationName + org_model_prefix, organizationType,
					prefix + organizationEmail, organizationPwd, organizationFirstName, organizationLastName);
			organization_id = response.then().extract().path("data.organization_id");
			compose_orgInfo_3.put("organization_name", prefix + organizationName);
			compose_orgInfo_3.put("type", organizationType);
			compose_orgInfo_3.put("blocked", false);
			compose_orgInfo_3.put("msp", null);
			compose_orgInfo_3.put("organization_id", organization_id);
			all_orgInfo.add(compose_orgInfo_3);
		} else if (organizationType == "msp") {
			prefix = spogServer.ReturnRandom("1#1");
			Response response = spogServer.CreateOrganization(prefix + organizationName + org_model_prefix,
					organizationType, prefix + organizationEmail, organizationPwd, organizationFirstName,
					organizationLastName);
			organization_id = response.then().extract().path("data.organization_id");
			spogServer.login(prefix + organizationEmail, organizationPwd);
			token = ti.csr_token;
			compose_orgInfo_1.put("organization_name", prefix + organizationName);
			compose_orgInfo_1.put("type", organizationType);
			compose_orgInfo_1.put("organization_id", organization_id);
			compose_orgInfo_1.put("blocked", false);
			compose_orgInfo_1.put("msp", null);
			all_orgInfo.add(compose_orgInfo_1);

			prefix = spogServer.ReturnRandom("1#1");
			response = spogServer.CreateOrganization(prefix + organizationName + org_model_prefix, organizationType,
					prefix + organizationEmail, organizationPwd, organizationFirstName, organizationLastName);
			organization_id = response.then().extract().path("data.organization_id");
			compose_orgInfo_2.put("organization_name", prefix + organizationName);
			compose_orgInfo_2.put("type", organizationType);
			compose_orgInfo_2.put("blocked", false);
			compose_orgInfo_2.put("msp", null);
			compose_orgInfo_2.put("organization_id", organization_id);
			all_orgInfo.add(compose_orgInfo_2);

			prefix = spogServer.ReturnRandom("1#1");
			response = spogServer.CreateOrganization(prefix + organizationName + org_model_prefix, organizationType,
					prefix + organizationEmail, organizationPwd, organizationFirstName, organizationLastName);
			organization_id = response.then().extract().path("data.organization_id");
			compose_orgInfo_3.put("organization_name", prefix + organizationName);
			compose_orgInfo_3.put("type", organizationType);
			compose_orgInfo_3.put("blocked", false);
			compose_orgInfo_3.put("msp", null);
			compose_orgInfo_3.put("organization_id", organization_id);
			all_orgInfo.add(compose_orgInfo_3);
		} else {
			// create msp organization
			response = spogServer.CreateOrganization(prefix + organizationName + org_model_prefix, "msp",
					prefix + organizationEmail, organizationPwd, organizationFirstName, organizationLastName);
			organization_id_msp = response.then().extract().path("data.organization_id");
			String organization_name_msp = response.then().extract().path("data.organization_name");
			spogServer.login(prefix + organizationEmail, organizationPwd);
			token = spogServer.getJWTToken();

			prefix = spogServer.ReturnRandom("1#1");
			Response response = spogServer.createAccount(organization_id_msp, prefix, organization_id_msp);
			organization_id = response.then().extract().path("data.organization_id");
			compose_orgInfo_1.put("organization_name", prefix);
			compose_orgInfo_1.put("type", organizationType);
			compose_orgInfo_1.put("organization_id", organization_id);
			compose_orgInfo_1.put("blocked", false);
			// composing msp details
			compose_mspInfo_1.put("organization_id", organization_id_msp);
			compose_mspInfo_1.put("organization_name", organization_name_msp);
			compose_mspInfo_1.put("type", "msp");
			compose_mspInfo_1.put("blocked", false);

			compose_orgInfo_1.put("msp", compose_mspInfo_1);
			all_orgInfo.add(compose_orgInfo_1);

			prefix = spogServer.ReturnRandom("1#1");
			response = spogServer.createAccount(organization_id_msp, prefix, organization_id_msp);
			organization_id = response.then().extract().path("data.organization_id");
			compose_orgInfo_2.put("organization_name", prefix);
			compose_orgInfo_2.put("type", organizationType);
			compose_orgInfo_2.put("blocked", false);
			compose_orgInfo_2.put("msp", compose_mspInfo_1);
			compose_orgInfo_2.put("organization_id", organization_id);
			all_orgInfo.add(compose_orgInfo_2);

			prefix = spogServer.ReturnRandom("1#1");
			response = spogServer.createAccount(organization_id_msp, prefix, organization_id_msp);
			organization_id = response.then().extract().path("data.organization_id");
			compose_orgInfo_3.put("organization_name", prefix);
			compose_orgInfo_3.put("type", organizationType);
			compose_orgInfo_3.put("blocked", false);
			compose_orgInfo_3.put("msp", compose_mspInfo_1);
			compose_orgInfo_3.put("organization_id", organization_id);
			all_orgInfo.add(compose_orgInfo_3);
		}
		if (search == "org_id") {
			search_string = compose_orgInfo_2.get("organization_id").toString();

			get_selected_orgInfo.add(compose_orgInfo_2);

		} else {
			for (int i = 0; i < all_orgInfo.size(); i++) {
				if (all_orgInfo.get(i).get("organization_name").toString().matches("(?i)(" + search_string + ").*")) {
					get_selected_orgInfo.add(all_orgInfo.get(i));
				}
			}
		}

		token = ti.csr_token;
		spogServer.postOrgInfoBySearchString(get_selected_orgInfo, search_string, token, currPage, pageSize,
				SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

		token = ti.csr_readonly_token;
		spogServer.postOrgInfoBySearchString(get_selected_orgInfo, search_string, token, currPage, pageSize,
				SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

		// delete the created organizations
		if (organizationType == "direct" || organizationType == "msp") {
			os.setToken(ti.csr_token);
			os.destroyOrganization(compose_orgInfo_1.get("organization_id").toString(), test);
			os.destroyOrganization(compose_orgInfo_2.get("organization_id").toString(), test);
			os.destroyOrganization(compose_orgInfo_3.get("organization_id").toString(), test);

		} else {
			os.setToken(ti.csr_token);
			os.destroyOrganization(compose_orgInfo_1.get("organization_id").toString(), test);
			os.destroyOrganization(compose_orgInfo_2.get("organization_id").toString(), test);
			os.destroyOrganization(compose_orgInfo_3.get("organization_id").toString(), test);
			// delete msp org
			os.destroyOrganization(organization_id_msp, test);

		}
	}


	@DataProvider(name = "getOrganizationInfo_invalid")
	public final Object[][] getOrganizationInfo1() {
		return new Object[][] { { ti.csr_token, 1, 20, "orgd_id", "csr" },

		};
	}

	// get Source by id For different scenarios
	@Test(dataProvider = "getOrganizationInfo_invalid")

	public void GetOrganizationInfoBySearchString_invalid(String token, int currPage, int pageSize,
			String search_string, String organizationType) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + "_");

		test.assignAuthor("sykam.nagamalleswari");

		// search_stirng is missed

		spogServer.postOrgInfoBySearchString(get_selected_orgInfo, "", token, currPage, pageSize,
				SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.SEARCH_STRING_CANNOT_BLANK, test);

		// seacrh_string_given lessthan 3 charatcers
		spogServer.postOrgInfoBySearchString(get_selected_orgInfo, "12", token, currPage, pageSize,
				SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.AT_LEAST_THREE_CHARACTRES_SEARCH_STRING, test);

		// invalid token
		token = "junk";
		spogServer.postOrgInfoBySearchString(get_selected_orgInfo, search_string, token, currPage, pageSize,
				SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);

		// missed token
		token = "";
		spogServer.postOrgInfoBySearchString(get_selected_orgInfo, search_string, token, currPage, pageSize,
				SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);

	}

	@DataProvider(name = "getOrganizationInfo_invalid_403")
	public final Object[][] getOrganizationInfo2() {
		return new Object[][] {
			{ti.direct_org1_user1_token,1,20,"Org","Direct"},

		};
	}

	// get Source by id For different scenarios
	@Test(dataProvider = "getOrganizationInfo_invalid_403")

	public void GetOrganizationInfoBySearchString_403(String token, 
			int currPage, 
			int pageSize,
			String search_string,
			String organizationType) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + "_");

		test.assignAuthor("sykam.nagamalleswari");

		spogServer.postOrgInfoBySearchString(get_selected_orgInfo, search_string, token, currPage, pageSize,
				SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.FORBIDDEN_TO_VISIT_THE_RESOURCE, test);

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

}
