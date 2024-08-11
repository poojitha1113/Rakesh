package api;

import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;
import java.text.SimpleDateFormat;
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
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class GetLoggedInUser extends base.prepare.Is4Org {

	private SPOGServer spogServer;
	private ExtentTest test;
	private TestOrgInfo ti;

	Boolean blocked = false;


	@BeforeClass
	@Parameters({ "baseURI", "port","logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runningMachine,
			String buildVersion) {

		spogServer = new SPOGServer(baseURI, port);
		rep = ExtentManager.getInstance(this.getClass().getSimpleName(), logFolder);
		String author = "Nagepalli, Ramya";
		Nooftest = 0;
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date = new java.util.Date();
		this.buildVersion = buildVersion + "_" + dateFormater.format(date);
		this.BQName = this.getClass().getSimpleName();
		this.runningMachine = runningMachine;
		test = rep.startTest("beforeClass");
		test.assignAuthor("Ramya");
		System.out.println("The value of hit is " + count1.isstarttimehit());

		if (count1.isstarttimehit() == 0) {
			System.out.println("into creation time");
			creationTime = System.currentTimeMillis();
			count1.setcreationtime(creationTime);
			// creationTime = System.currentTimeMillis();
			try {
				bqdb1.updateTable(BQName, runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0",
						String.valueOf(Nooftest), count1.getcreationtime(), "InProgress",
						author + " and Rest server is " + baseURI.split("//")[1]);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		ti = new TestOrgInfo(spogServer, test);
	}

	@Test
	public void getUserTest_401() {

		spogServer.getLoggedInUser(null, SpogConstants.NOT_LOGGED_IN, test);
		spogServer.getLoggedInUser("", SpogConstants.NOT_LOGGED_IN, test);
		spogServer.getLoggedInUser("anc", SpogConstants.NOT_LOGGED_IN, test);

	}

	/****************************
	 * Preference language cases - Sprint 34
	 ********************************************/
	@DataProvider(name = "getUserPreferenceLanguageCases")
	public Object[][] getUserPreferenceLanguageCases() {
		return new Object[][] {
				// 200
				{ "GET user in direct organization having preferred language English ", ti.direct_org1_user1_email,
						ti.common_password, SpogConstants.DIRECT_ADMIN, ti.direct_org1_id, "en",
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "GET user in direct organization having preferred language Japanese", ti.direct_org1_user1_email,
						ti.common_password, SpogConstants.DIRECT_ADMIN, ti.direct_org1_id, "ja",
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "GET msp user in MSP organization having preferred language English",  ti.normal_msp_org1_user1_email,
						ti.common_password, SpogConstants.MSP_ADMIN,  ti.normal_msp_org1_id, "en",
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "GET msp user in MSP organization having preferred language Japanese",  ti.normal_msp_org1_user1_email,
						ti.common_password, SpogConstants.MSP_ADMIN,  ti.normal_msp_org1_id, "ja",
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "GET msp account admin user in MSP organization having preferred language English",  ti.normal_msp_org1_user1_email,
						ti.common_password, SpogConstants.MSP_ACCOUNT_ADMIN,  ti.normal_msp_org1_id, "en",
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "GET msp account admin user in MSP organization having preferred language Japanese",  ti.normal_msp_org1_user1_email,
						ti.common_password, SpogConstants.MSP_ACCOUNT_ADMIN,  ti.normal_msp_org1_id, "ja",
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "GET customer admin user in sub organization using msp token having preferred language English",
						 ti.normal_msp_org1_user1_email, ti.common_password, SpogConstants.DIRECT_ADMIN, ti.normal_msp1_suborg1_id, "en",
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "GET customer user in sub organization using msp token having preferred language Japanese",
						 ti.normal_msp_org1_user1_email, ti.common_password, SpogConstants.DIRECT_ADMIN, ti.normal_msp1_suborg1_id, "ja",
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "GET direct admin user in sub organization using sub org token having preferred language English",
						ti.normal_msp1_suborg1_user1_email, ti.common_password, SpogConstants.DIRECT_ADMIN, ti.normal_msp1_suborg1_id, "en",
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "GET direct admin user in sub organization using sub org token having preferred language Japanese",
						ti.normal_msp1_suborg1_user1_email, ti.common_password, SpogConstants.DIRECT_ADMIN, ti.normal_msp1_suborg1_id, "ja",
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },

				// preferred language value null
				{ "GET user in direct organization having preferred language null", ti.direct_org1_user1_email,
						ti.common_password, SpogConstants.DIRECT_ADMIN, ti.direct_org1_id, null,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "GET msp user in MSP organization having preferred language null",  ti.normal_msp_org1_user1_email, ti.common_password,
						SpogConstants.MSP_ADMIN,  ti.normal_msp_org1_id, null, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "GET msp account admin user in MSP organization having preferred language null",  ti.normal_msp_org1_user1_email,
						ti.common_password, SpogConstants.MSP_ACCOUNT_ADMIN,  ti.normal_msp_org1_id, null,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "GET customer admin user in sub organization using msp token having preferred language null",
						 ti.normal_msp_org1_user1_email, ti.common_password, SpogConstants.DIRECT_ADMIN, ti.normal_msp1_suborg1_id, null,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "GET direct admin user in sub organization using sub org token having preferred language null",
						ti.normal_msp1_suborg1_user1_email, ti.common_password, SpogConstants.DIRECT_ADMIN, ti.normal_msp1_suborg1_id, null,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null }, };
	}

	@Test(dataProvider = "getUserPreferenceLanguageCases")
	public void createUserTest(String caseType, String loginUserName, String loginPassword, String role_id,
			String organization_id, String preference_language, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage) {

		test = rep.startTest(caseType);
		test.assignAuthor("Rakesh.Chalamala");
		spogServer.userLogin(loginUserName, loginPassword, test);

		String first_name = spogServer.ReturnRandom("first");
		String last_name = spogServer.ReturnRandom("last");
		String email = spogServer.ReturnRandom("email") + "@spogqa.com";
		String phone = RandomStringUtils.randomNumeric(10);
		String password = ti.common_password;
		String actions = "";

		if (role_id.equalsIgnoreCase(SpogConstants.MSP_ACCOUNT_ADMIN)) {
			actions += "assignaccount";
		}

		String user_id = spogServer.createUserWithCheck(first_name, last_name, email, phone, role_id, organization_id,
				preference_language, password, SpogConstants.SUCCESS_POST, null, test);

		spogServer.userLogin(email, password);
		Response response = spogServer.getLoggedInUser(spogServer.getJWTToken(), SpogConstants.SUCCESS_GET_PUT_DELETE,
				test);
		if (preference_language == null) {
			preference_language = "en";
		}
		response.then().body("data.preference_language", equalTo(preference_language));

		spogServer.checkLoggedInUserInformation(response, expectedStatusCode, expectedErrorMessage, email, user_id,
				organization_id, false, "verified", actions, test);

	}

	/************************************
	 * end
	 ********************************************/

	@AfterMethod
	public void getResult(ITestResult result) {
		if (result.getStatus() == ITestResult.FAILURE) {
			count1.setfailedcount();
			test.log(LogStatus.FAIL, "Test Case Failed is " + result.getName() + " with parameters as "
					+ Arrays.asList(result.getParameters()));
			test.log(LogStatus.FAIL, result.getThrowable().getMessage());
			test.log(LogStatus.FAIL, result.getThrowable());

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
