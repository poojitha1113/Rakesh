package api.organizations;

import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.Org4SPOGServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import api.preparedata.InitialTestData;
import api.preparedata.InitialTestDataImpl;
import bsh.org.objectweb.asm.Constants;
import dataPreparation.JsonPreparation;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import groovyjarjarantlr.PreservingFileWriter;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;

import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Constants.SpogConstants;

import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;

public class GetUsersFromOrganizationTest extends base.prepare.Is4Org{

	private SPOGServer spogServer;
	private UserSpogServer userSpogServer;
	private JsonPreparation jp;
	private String csrAdmin;
	private String csrPwd;	
//	private ExtentReports rep;
	private ExtentTest test;

	private String csr_readonly_email = "csr_readonly_zhaoguo@arcserve.com";
	private String csr_readonly_password = "Caworld_2017";
	
	private InitialTestDataImpl initialTestDataImpl;
	private InitialTestData itd;
	
	private String password = "Pa$$w0rd";

	private String additional_user_email = "spog_qa_add_zhaoguo@arcserve.com";
	private String additional_first_name = "spog_qa_add_ma";
	private String additional_last_name = "spog_qa_add_zhaoguo";
	private String initial_deleted_orgID = "96710d84-732b-4ffa-9554-cc9154cc1469";

//	private SQLServerDb bqdb1;
//	public int Nooftest;
//	private long creationTime;
//	private String BQName = null;
//	private String runningMachine;
//	private testcasescount count1;
//	private String buildVersion;
	private String  org_model_prefix=this.getClass().getSimpleName();

	@BeforeClass
	@Parameters({ "baseURI", "port", "csrAdminUserName", "csrAdminPassword", "logFolder", "runningMachine",
			"buildVersion" })
	public void beforeClass(String baseURI, String port, String csrAdminUserName, String csrAdminPassword,
			String logFolder, String runningMachine, String buildVersion) {
		rep = ExtentManager.getInstance("GetUsersFromOrganizationTest", logFolder);

		test = rep.startTest("initializing data...");

		this.BQName = this.getClass().getSimpleName();
		String author = "Zhaoguo.Ma";
		this.runningMachine = runningMachine;
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
				bqdb1.updateTable(BQName, runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0",
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

		spogServer = new SPOGServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		jp = new JsonPreparation();
		this.csrAdmin = csrAdminUserName;
		this.csrPwd = csrAdminPassword;
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		String prefix = RandomStringUtils.randomAlphanumeric(8);

		String prefix_a = RandomStringUtils.randomAlphanumeric(8) + this.getClass().getSimpleName();		
		initialTestDataImpl = new InitialTestDataImpl(baseURI, port, csrAdminUserName, csrAdminPassword, prefix_a, password);
		itd = initialTestDataImpl.initialize();
		

	}
	
	// msp account admin to get users from account which managed by it;
	@Test
	public void mspAccountAdimnTest() {
		spogServer.userLogin(itd.getMsp_org_1_account_admin_1_email(), password);
		Response response = spogServer.GetUsersByOrganizationID(itd.getMsp_org_1_sub_1());
		//response.then().body("data.site_id", equalTo(site_id));
		List<String> emails = response.then().extract().path("data.email");
		assertEquals(emails.get(0), itd.getMsp_org_1_sub_1_user_1_email().toLowerCase());
	}

	// csr admin: get all users from direct/msp organization
	@DataProvider(name = "csrorganizationInfo")
	public final Object[][] csrgetOrganizationInfo() {
		return new Object[][] { 
			//{ itd.getMsp_org_1(), SpogConstants.MSP_ADMIN },
			{ itd.getDirect_org_2(), SpogConstants.DIRECT_ADMIN } };
	}

	@Test(dataProvider = "csrorganizationInfo")
	public void csrGetUsersFromOrganization(String orgID, String adminType) {
		test.assignAuthor("Zhaoguo.Ma");
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		test = rep.startTest("getUsersFromOrganization");

		Response response = spogServer.GetUsersByOrganizationID(orgID);

		ArrayList<HashMap> defaultUser = new ArrayList<HashMap>();
		defaultUser = response.then().extract().path("data");
		String defaultUserID = defaultUser.get(0).get("user_id").toString();
		String defaultUserEmail = defaultUser.get(0).get("email").toString();
		String defaultUserFirstName = defaultUser.get(0).get("first_name").toString();
		String defaultUserLastName = defaultUser.get(0).get("last_name").toString();

		ArrayList<HashMap> expectedUsers = new ArrayList<HashMap>();
		HashMap<String, String> m = new HashMap();
		m.put("user_id", defaultUserID);
		m.put("role_id", adminType);
		m.put("organization_id", orgID);
		m.put("email", defaultUserEmail.toLowerCase());
		m.put("first_name", defaultUserFirstName);
		m.put("last_name", defaultUserLastName);
		expectedUsers.add(m);

		String prefix = RandomStringUtils.randomAlphanumeric(8);
		for (Integer i = 0; i < 10; i++) {
			Response response_createuser = spogServer.createUser(i.toString() + prefix + additional_user_email,
					password, i.toString() + prefix + additional_first_name,
					i.toString() + prefix + additional_last_name, adminType, orgID, test);
			String added_user_id = spogServer.checkCreateUser(response_createuser, SpogConstants.SUCCESS_POST,
					i.toString() + prefix + additional_user_email, i.toString() + prefix + additional_first_name,
					i.toString() + prefix + additional_last_name, adminType, orgID, "", test);

			HashMap<String, String> user_added = new HashMap();
			user_added.put("user_id", added_user_id);
			user_added.put("role_id", adminType);
			user_added.put("organization_id", orgID);
			user_added.put("email", (i.toString() + prefix + additional_user_email).toLowerCase());
			user_added.put("first_name", i.toString() + prefix + additional_first_name);
			user_added.put("last_name", i.toString() + prefix + additional_last_name);

			expectedUsers.add(user_added);
		}

		spogServer.CompareUsersByOrganizationID(orgID, expectedUsers, test);
		// spogServer.DeleteOrganizationWithCheck(orgID, test);
	}

	// msp,direct admin: get all users from its organization
	@DataProvider(name = "adminorganizationInfo")
	public final Object[][] admingetOrganizationInfo() {
		return new Object[][] {
				{ "msporgName", SpogConstants.MSP_ORG, SpogConstants.MSP_ADMIN, "mspemail@arcserve.com", "mspfirstName",
						"msplastName" },
				{ "directorgName", SpogConstants.DIRECT_ORG, SpogConstants.DIRECT_ADMIN, "directemail@arcserve.com",
						"directfirstName", "directlastName" } };
	}

	@Test(dataProvider = "adminorganizationInfo")
	public void adminGetUsersFromOrganization(String orgName, String orgType, String adminType, String email,
			String firstName, String lastName) {
		test.assignAuthor("Zhaoguo.Ma");
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		test = rep.startTest("getUsersFromOrganization");
		String prefix = RandomStringUtils.randomAlphanumeric(8);

		String orgID = spogServer.CreateOrganizationWithCheck(prefix + orgName + org_model_prefix, orgType, prefix + email, password,
				prefix + firstName, prefix + lastName, test);

		ArrayList<HashMap> defaultUser = new ArrayList<HashMap>();
		Response response = spogServer.GetUsersByOrganizationID(orgID);
		defaultUser = response.then().extract().path("data");
		String defaultUserID = defaultUser.get(0).get("user_id").toString();
		String defaultUserEmail = defaultUser.get(0).get("email").toString();
		String defaultUserFirstName = defaultUser.get(0).get("first_name").toString();
		String defaultUserLastName = defaultUser.get(0).get("last_name").toString();

		ArrayList<HashMap> expectedUsers = new ArrayList<HashMap>();
		HashMap<String, String> m = new HashMap();
		m.put("user_id", defaultUserID);
		m.put("role_id", adminType);
		m.put("organization_id", orgID);
		m.put("email", defaultUserEmail.toLowerCase());
		m.put("first_name", defaultUserFirstName);
		m.put("last_name", defaultUserLastName);
		expectedUsers.add(m);
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		for (Integer i = 0; i < 10; i++) {
			Response response_createuser = spogServer.createUser(i.toString() + prefix + additional_user_email,
					password, i.toString() + prefix + additional_first_name,
					i.toString() + prefix + additional_last_name, adminType, orgID, test);
			String added_user_id = spogServer.checkCreateUser(response_createuser, SpogConstants.SUCCESS_POST,
					i.toString() + prefix + additional_user_email, i.toString() + prefix + additional_first_name,
					i.toString() + prefix + additional_last_name, adminType, orgID, "", test);

			HashMap<String, String> user_added = new HashMap();
			user_added.put("user_id", added_user_id);
			user_added.put("role_id", adminType);
			user_added.put("organization_id", orgID);
			user_added.put("email", (i.toString() + prefix + additional_user_email).toLowerCase());
			user_added.put("first_name", i.toString() + prefix + additional_first_name);
			user_added.put("last_name", i.toString() + prefix + additional_last_name);

			expectedUsers.add(user_added);
		}
		System.out.println("before login, the token is: " + spogServer.getJWTToken());
		spogServer.userLogin(prefix + email, password);
		System.out.println("after login, the token is: " + spogServer.getJWTToken());
		spogServer.CompareUsersByOrganizationID(orgID, expectedUsers, test);
		// spogServer.DeleteOrganizationWithCheck(orgID, test);
	}

	@DataProvider(name = "suborganizationInfo")
	public final Object[][] subgetOrganizationInfo() {
		return new Object[][] { { 1, "orgName", "sub_org_email@arcserve.com", "spog_qa_ma", "spog_qa_zhaoguo" },
				{ 2, "orgName", "sub_org_email@arcserve.com", "spog_qa_ma", "spog_qa_zhaoguo" },
				{ 3, "orgName", "sub_org_email@arcserve.com", "spog_qa_ma", "spog_qa_zhaoguo" } };
	}

	@Test(dataProvider = "suborganizationInfo")
	public void getUsersFromSubOrganization(int type, String orgName, String email, String firstName, String lastName) {

		test.assignAuthor("Zhaoguo.Ma");
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		test = rep.startTest("getUsersFromOrganization");
		String prefix = RandomStringUtils.randomAlphanumeric(8);

		String mspOrgID = spogServer.CreateOrganizationWithCheck(prefix + "msp" + orgName + org_model_prefix, SpogConstants.MSP_ORG,
				prefix + "msp" + email, password, prefix + "msp" + firstName, prefix + "msp" + lastName, test);
		String subOrgID = spogServer.createAccountWithCheck(mspOrgID, "sub" + orgName, mspOrgID);

		ArrayList<HashMap> expectedUsers = new ArrayList<HashMap>();
		spogServer.userLogin(prefix + "msp" + email, password);
		for (Integer i = 0; i < 10; i++) {
			String added_user_id = spogServer.createUserAndCheck(prefix + "sub" + i.toString() + email, password,
					prefix + "sub" + i.toString() + firstName, prefix + "sub" + i.toString() + lastName,
					SpogConstants.DIRECT_ADMIN, subOrgID, test);

			HashMap<String, String> user_added = new HashMap();
			user_added.put("user_id", added_user_id);
			user_added.put("role_id", SpogConstants.DIRECT_ADMIN);
			user_added.put("organization_id", subOrgID);
			user_added.put("email", (prefix + "sub" + i.toString() + email).toLowerCase());
			user_added.put("first_name", prefix + "sub" + i.toString() + firstName);
			user_added.put("last_name", prefix + "sub" + i.toString() + lastName);

			expectedUsers.add(user_added);
		}
		System.out.println("before login, the token is: " + spogServer.getJWTToken());
		if (type == 1) {
			spogServer.userLogin(this.csrAdmin, this.csrPwd);
		} else if (type == 2) {
			spogServer.userLogin(prefix + "msp" + email, password);
		} else if (type == 3) {
			spogServer.userLogin(prefix + "sub0" + email, password);
		}
		System.out.println("after login, the token is: " + spogServer.getJWTToken());
		spogServer.CompareUsersByOrganizationID(subOrgID, expectedUsers, test);
		// spogServer.DeleteOrganizationWithCheck(orgID, test);
	}

	@DataProvider(name = "failedorganizationInfo")
	public final Object[][] failedOrganizationInfo() {
		return new Object[][] {
				// msp account admin cannot get users for other orgs:
				{ itd.getMsp_org_1_sub_2(), itd.getMsp_org_1_account_admin_1_email(), password, 403, "00100101" },
				// 2018/06/29, there is a change on msp_account_admin, it will get empty list of users instead of 403
//				{ itd.getMsp_org_1(), itd.getMsp_org_1_account_admin_1_email(), password, 200, "" },
				{ itd.getDirect_org_1(), itd.getMsp_org_1_account_admin_1_email(), password, 403, "00100101" },
				// direct admin cannot get msp organization users;
				{ itd.getMsp_org_1(), itd.getDirect_org_1_user_1_email(), password, 403, "00100101" },
				{ itd.getMsp_org_1_sub_2(), itd.getMsp_org_1_account_admin_1_email(), password, 403, "00100101" },
				// msp admin cannot get another msp organization users;
				{ itd.getMsp_org_1(), itd.getMsp_org_2_user_1_email(), password, 403, "00100101" },
				// direct admin cannot get another direct organization users;
				{ itd.getDirect_org_1(), itd.getDirect_org_2_user_1_email(), password, 403, "00100101" },
				// msp admin cannot get direct organization users;
				{ itd.getDirect_org_1(), itd.getMsp_org_1_user_1_email(), password, 403, "00100101" },
				// msp admin cannot get sub organization users in another msp;
				{ itd.getMsp_org_2_sub_1(), itd.getMsp_org_1_user_1_email(), password, 403, "00100101" },
				// direct admin cannot get sub organization users;
				{ itd.getMsp_org_1_sub_1(), itd.getDirect_org_1_user_1_email(), password, 403, "00100101" },
				// sub organization admin cannot get its msp users;
				{ itd.getMsp_org_1(), itd.getMsp_org_1_sub_1_user_1_email(), password, 403, "00100101" },
				// sub organization admin cannot get another msp users;
				{ itd.getMsp_org_2(), itd.getMsp_org_1_sub_1_user_1_email(), password, 403, "00100101" },
				// sub organization admin cannot get direct organization users;
				{ itd.getDirect_org_1(), itd.getMsp_org_1_sub_1_user_1_email(), password, 403, "00100101" },
				// sub organization admin cannot get another sub organization users in same msp;
				{ itd.getMsp_org_1_sub_2(), itd.getMsp_org_1_sub_1_user_1_email(), password, 403, "00100101" },
				// sub organization admin cannot get another sub organization users in different
				// msp;
				{ itd.getMsp_org_2_sub_1(), itd.getMsp_org_1_sub_1_user_1_email(), password, 403, "00100101" },
				// csr admin cannot get deleted organization users;
				{ initial_deleted_orgID, csrAdmin, csrPwd, 404, "0030000A" },
				// msp admin cannot get deleted organization users;
				{ initial_deleted_orgID, itd.getMsp_org_1_user_1_email(), password, 404, "0030000A" },
				// direct admin cannot get deleted organization users;
				{ initial_deleted_orgID, itd.getDirect_org_1_user_1_email(), password, 404, "0030000A" },
				// sub org admin cannot get deleted organization users;
				{ initial_deleted_orgID, itd.getMsp_org_1_sub_1_user_1_email(), password, 404, "0030000A" },
				// csr admin cannot get invalid organization users;
				{ "37f336b5-5fc2-4309-84b4-42cc1df5eee1", csrAdmin, csrPwd, 404, "0030000A" },
				// msp admin cannot get invalid organization users;
				{ "37f336b5-5fc2-4309-84b4-42cc1df5eee1", itd.getMsp_org_1_user_1_email(), password, 404, "0030000A" },
				// direct admin cannot get invalid organization users;
				{ "37f336b5-5fc2-4309-84b4-42cc1df5eee1", itd.getDirect_org_1_user_1_email(), password, 404, "0030000A" },
				// sub org admin cannot get invalid organization users;
				{ "37f336b5-5fc2-4309-84b4-42cc1df5eee1", itd.getMsp_org_1_sub_1_user_1_email(), password, 404, "0030000A" }, 
				
				// root msp related
				{itd.getRoot_msp_org_1_sub_msp_1(), itd.getRoot_msp_org_1_user_1_email(), password, 403, "00100101"},
				{itd.getRoot_msp_org_1_sub_msp_1_account_1(), itd.getRoot_msp_org_1_user_1_email(), password, 403, "00100101"},
				{itd.getRoot_msp_org_1_sub_msp_1_account_2(), itd.getRoot_msp_org_1_account_admin_1_email(), password, 403, "00100101"},
				{itd.getRoot_msp_org_1_sub_msp_1(), itd.getRoot_msp_org_1_account_1_user_1_email(), password, 403, "00100101"},
				{itd.getRoot_msp_org_1_account_1(), itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1_email(), password, 403, "00100101"},
				{itd.getRoot_msp_org_1_account_2(), itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1_email(), password, 403, "00100101"},
				{itd.getRoot_msp_org_1_sub_msp_1_account_2(), itd.getRoot_msp_org_1_account_1_user_1_email(), password, 403, "00100101"},
				{itd.getRoot_msp_org_1_sub_msp_1_account_2(), itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1_email(), password, 403, "00100101"},
				{itd.getRoot_msp_org_1_sub_msp_2_account_1(), itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1_email(), password, 403, "00100101"},
		};
	}

	// error test, admin cannot get user information from other organizations
	@Test(dataProvider = "failedorganizationInfo")
	public void GetUsersFromOrganizationFailTest(String orgID, String username, String password, int expectedStatusCode,
			String expectedErrorCode) {
		spogServer.userLogin(username, password);
		spogServer.GetOrganizationUsersFailedWithExpectedStatusCode(orgID, expectedStatusCode, expectedErrorCode, test);
	}

	@DataProvider(name = "organizationInfo")
	public final Object[][] organizationInfo() {
		return new Object[][] {
				// msp account admin cannot get users for other orgs:
				{ itd.getMsp_org_1_sub_1(), itd.getMsp_org_1_user_1_email(), password},
				{ itd.getMsp_org_1_sub_1(), itd.getMsp_org_1_account_admin_1_email(), password},
				{ itd.getMsp_org_1_sub_1(), itd.getMsp_org_1_sub_1_user_1_email(), password},
				{ itd.getRoot_msp_org_1(), itd.getRoot_msp_org_1_user_1_email(), password},
				{ itd.getRoot_msp_org_1_sub_msp_1(), itd.getRoot_msp_org_1_sub_msp_1_user_1_email(), password},
				{ itd.getRoot_msp_org_1_account_1(), itd.getRoot_msp_org_1_account_admin_1_email(), password},
				{ itd.getRoot_msp_org_1_account_1(), itd.getRoot_msp_org_1_user_1(), password},
				{ itd.getRoot_msp_org_1_sub_msp_1_account_1(), itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1_email(), password},
				{ itd.getRoot_msp_org_1_sub_msp_1_account_1(), itd.getRoot_msp_org_1_sub_msp_1_account_admin_1_email(), password},

		};
	}
	
	@Test(dataProvider = "organizationInfo")
	public void GetUsersFromOrganizationTestAuth(String orgID, String username, String password) {
		spogServer.userLogin(username, password);
		
		spogServer.GetUsersByOrganizationID(orgID);
	}

	
	@DataProvider(name = "invalidTokenorganizationInfo")
	public final Object[][] invalidTokenOrganizationInfo() {
		return new Object[][] { { itd.getMsp_org_1(), itd.getMsp_org_1_user_1_email(), password, 401, "00900006" },
				{ itd.getMsp_org_1(), itd.getDirect_org_1_user_1_email(), password, 401, "00900006" },
				{ itd.getMsp_org_1(), itd.getMsp_org_2_user_1_email(), password, 401, "00900006" },
				{ itd.getDirect_org_1(), itd.getDirect_org_1_user_1_email(), password, 401, "00900006" },
				{ itd.getDirect_org_1(), itd.getDirect_org_2_user_1_email(), password, 401, "00900006" },
				{ itd.getDirect_org_1(), itd.getMsp_org_1_user_1_email(), password, 401, "00900006" } };
	}

	@Test(dataProvider = "invalidTokenorganizationInfo")
	public void GetUsersFromOrganizationTokenErrorFailTest(String orgID, String username, String password,
			int expectedStatusCode, String expectedErrorCode) {
		test.assignAuthor("Zhaoguo.Ma");
		spogServer.userLogin(username, password);
		test = rep.startTest("getUsersFromOrganization");
		spogServer.setToken("");
		spogServer.GetOrganizationUsersFailedWithExpectedStatusCode(orgID, expectedStatusCode, expectedErrorCode, test);
		// spogServer.DeleteOrganizationWithCheck(orgID, test);
	}
	
	@DataProvider(name = "csr_readonly_params")
	public final Object[][] getFilters_csr_readonly() {
		return new Object[][] {
			{this.itd.getDirect_org_1()},
			{this.itd.getMsp_org_1()},
			{this.itd.getMsp_org_1_sub_1()},
		};}
	
	@Test(dataProvider = "csr_readonly_params")
	public void csrReadonlyTest(String orgID) {
		spogServer.userLogin(csr_readonly_email, csr_readonly_password);
		spogServer.GetUsersByOrganizationID(orgID);
	}
	
	/**************************** Preference language check - Sprint 34 *************************************/
	@DataProvider(name="getUsersPreferenceLanguageCases")
	   public Object[][] getUsersPreferenceLanguageCases(){
		   return new Object[][] {
			   //200
			   {"Create user in direct organization with preferred language English and get all users", 
				   		itd.getDirect_org_1_user_1_email(), password, SpogConstants.DIRECT_ADMIN, itd.getDirect_org_1(), "en", SpogConstants.SUCCESS_POST, null},
			   {"Create user in direct organization with preferred language Japanese and get all users", 
				   		itd.getDirect_org_1_user_1_email(), password, SpogConstants.DIRECT_ADMIN, itd.getDirect_org_1(), "ja", SpogConstants.SUCCESS_POST, null},
			   {"Create msp user in MSP organization with preferred language English and get all users", 
						itd.getMsp_org_1_user_1_email(), password, SpogConstants.MSP_ADMIN, itd.getMsp_org_1(), "en", SpogConstants.SUCCESS_POST, null},
			   {"Create msp user in MSP organization with preferred language Japanese and get all users", 
						itd.getMsp_org_1_user_1_email(), password, SpogConstants.MSP_ADMIN, itd.getMsp_org_1(), "ja", SpogConstants.SUCCESS_POST, null},
			   {"Create msp account admin user in MSP organization with preferred language English and get all users", 
						itd.getMsp_org_1_user_1_email(), password, SpogConstants.MSP_ACCOUNT_ADMIN, itd.getMsp_org_1(), "en", SpogConstants.SUCCESS_POST, null},
			   {"Create msp account admin user in MSP organization with preferred language Japanese and get all users", 
						itd.getMsp_org_1_user_1_email(), password, SpogConstants.MSP_ACCOUNT_ADMIN, itd.getMsp_org_1(), "ja", SpogConstants.SUCCESS_POST, null},
			   {"Create customer admin user in sub organization using msp token with preferred language English and get all users", 
						itd.getMsp_org_1_user_1_email(), password, SpogConstants.DIRECT_ADMIN, itd.getMsp_org_1_sub_1(), "en", SpogConstants.SUCCESS_POST, null},
			   {"Create customer user in sub organization using msp token with preferred language Japanese and get all users", 
						itd.getMsp_org_1_user_1_email(), password, SpogConstants.DIRECT_ADMIN, itd.getMsp_org_1_sub_1(), "ja", SpogConstants.SUCCESS_POST, null},
			   {"Create customer admin user in sub organization using msp account admin token with preferred language English and get all users", 
						itd.getMsp_org_1_account_admin_1_email(), password, SpogConstants.DIRECT_ADMIN, itd.getMsp_org_1_sub_1(), "en", SpogConstants.SUCCESS_POST, null},
			   {"Create customer user in sub organization using msp account admin token with preferred language Japanese and get all users", 
						itd.getMsp_org_1_account_admin_1_email(), password, SpogConstants.DIRECT_ADMIN, itd.getMsp_org_1_sub_1(), "ja", SpogConstants.SUCCESS_POST, null},
			   {"Create direct admin user in sub organization using sub org token with preferred language English and get all users", 
						itd.getMsp_org_1_sub_1_user_1_email() , password, SpogConstants.DIRECT_ADMIN, itd.getMsp_org_1_sub_1(), "en", SpogConstants.SUCCESS_POST, null},
			   {"Create direct admin user in sub organization using sub org token with preferred language Japanese and get all users", 
						itd.getMsp_org_1_sub_1_user_1_email(), password, SpogConstants.DIRECT_ADMIN, itd.getMsp_org_1_sub_1(), "ja", SpogConstants.SUCCESS_POST, null},
		   
			   //preferred language value null
			   {"Create user in direct organization with preferred language null and get all users", 
						itd.getDirect_org_1_user_1_email(), password, SpogConstants.DIRECT_ADMIN, itd.getDirect_org_1(), null, SpogConstants.SUCCESS_POST, null},
			   {"Create msp user in MSP organization with preferred language null and get all users", 
						itd.getMsp_org_1_user_1_email(), password, SpogConstants.MSP_ADMIN, itd.getMsp_org_1(), null, SpogConstants.SUCCESS_POST, null},
			   {"Create msp account admin user in MSP organization with preferred language null and get all users", 
						itd.getMsp_org_1_user_1_email(), password, SpogConstants.MSP_ACCOUNT_ADMIN, itd.getMsp_org_1(), null, SpogConstants.SUCCESS_POST, null},
			   {"Create customer admin user in sub organization using msp token with preferred language null and get all users", 
						itd.getMsp_org_1_user_1_email(), password, SpogConstants.DIRECT_ADMIN, itd.getMsp_org_1_sub_1(), null, SpogConstants.SUCCESS_POST, null},
			   {"Create customer admin user in sub organization using msp account admin token with preferred language null and get all users", 
						itd.getMsp_org_1_account_admin_1_email(), password, SpogConstants.DIRECT_ADMIN, itd.getMsp_org_1_sub_1(), null, SpogConstants.SUCCESS_POST, null},
			   {"Create direct admin user in sub organization using sub org token with preferred language null and get all users", 
						itd.getMsp_org_1_sub_1_user_1_email() , password, SpogConstants.DIRECT_ADMIN, itd.getMsp_org_1_sub_1(), null, SpogConstants.SUCCESS_POST, null},
			   
		   };
	   }
	   
	   @Test(dataProvider="getUsersPreferenceLanguageCases")
	   public void getUsersTest(String caseType,
			   						String loginUserName,
			   						String loginPassword,
			   						String role_id,
			   						String organization_id,
			   						String preference_language,
			   						int expectedStatusCode,
			   						SpogMessageCode expectedErrorMessage ) {
		
		   	test = rep.startTest(caseType);
		 	test.assignAuthor("Rakesh.Chalamala");
		 	spogServer.userLogin(loginUserName, loginPassword,test);
		 	
		 	String first_name = spogServer.ReturnRandom("first");
		 	String last_name = spogServer.ReturnRandom("last");
		 	String email = spogServer.ReturnRandom("email")+"@spogqa.com";
		 	String phone = "9696969696";
		 		 		 	
		 	Response response = spogServer.GetUsersByOrganizationID(organization_id);
		 	ArrayList<HashMap> expectedUsersInfo = response.then().extract().path("data");
		 			 	
		 	Response response1 = spogServer.createUser(first_name, last_name, email, phone, role_id, organization_id, preference_language, password, expectedStatusCode, test);
		 	HashMap<String, Object> userInfo = response1.then().extract().path("data");
		 	
		 	expectedUsersInfo.add(userInfo);
		 	spogServer.CompareUsersByOrganizationID(organization_id, expectedUsersInfo, test);
	   }
	   
	   /**************************************End********************************************************/

	@AfterMethod
	public void afterMethodTest() {
		rep.endTest(test);
		rep.flush();
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
