package api.organizations;

import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.SpogConstants;
import InvokerServer.SPOGServer;
import bsh.org.objectweb.asm.Constants;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import groovyjarjarantlr.PreservingFileWriter;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;

import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import Constants.SpogConstants;

import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;

public class GetUsersFromOrganizationAdvTest extends base.prepare.Is4Org {

	private SPOGServer spogServer;
	private String csrAdmin;
	private String csrPwd;
//	private ExtentReports rep;
	private ExtentTest test;

	private String initial_direct_org_name = "0spog_qa_direct_zhaoguo";
	private String initial_direct_email = "0spog_qa_direct_zhaoguo@arcserve.com";
	private String initial_direct_email_full = "";
	private String initial_direct_first_name = "0spog_qa_direct_ma";
	private String initial_direct_last_name = "0spog_qa_direct_zhaoguo";

	private String initial_direct_orgID;

	private String password = "Pa$$w0rd";

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
		rep = ExtentManager.getInstance("GetUsersFromOrganizationAdvTest", logFolder);
		test = rep.startTest("initializing data...");

		this.BQName = this.getClass().getSimpleName();
        String author = "Zhaoguo.Ma";
        this.runningMachine = runningMachine;
        SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
        java.util.Date date=new java.util.Date();
        this.buildVersion=buildVersion+"_"+dateFormater.format(date);
        Nooftest=0;
        bqdb1 = new SQLServerDb();
        count1 = new testcasescount();
        if(count1.isstarttimehit()==0) {
            System.out.println("Into get loggedInUserById");
            creationTime=System.currentTimeMillis();
            count1.setcreationtime(creationTime);
            //creationTime = System.currentTimeMillis();
            try {
                  bqdb1.updateTable(BQName, runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0", String.valueOf(Nooftest), count1.getcreationtime(), "InProgress",author + " and Rest server is "+baseURI.split("//")[1]);
            } catch (ClientProtocolException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
            } catch (IOException e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
            }
        }


		spogServer = new SPOGServer(baseURI, port);
		this.csrAdmin = csrAdminUserName;
		this.csrPwd = csrAdminPassword;
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		String prefix = RandomStringUtils.randomAlphanumeric(8);

		// create an organization;
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		this.initial_direct_email_full = prefix + this.initial_direct_email;
		this.initial_direct_orgID = spogServer.CreateOrganizationWithCheck(prefix + initial_direct_org_name + org_model_prefix,
				SpogConstants.DIRECT_ORG, prefix + initial_direct_email, password, prefix + initial_direct_first_name,
				prefix + initial_direct_last_name);

		spogServer.createUserAndCheck(prefix + "c_spog_qa@arcserve.com", password, prefix + "c_spog_qa_firstname",
				prefix + "c_spog_qa_lastname", SpogConstants.DIRECT_ADMIN, initial_direct_orgID, test);
		spogServer.createUserAndCheck(prefix + "b_spog_qa@arcserve.com", password, prefix + "b_spog_qa_firstname",
				prefix + "b_spog_qa_lastname", SpogConstants.DIRECT_ADMIN, initial_direct_orgID, test);
		spogServer.createUserAndCheck(prefix + "a_spog_qa@arcserve.com", password, prefix + "a_spog_qa_firstname",
				prefix + "a_spog_qa_lastname", SpogConstants.DIRECT_ADMIN, initial_direct_orgID, test);
		spogServer.createUserAndCheck(prefix + "d_spog_qa@arcserve.com", password, prefix + "d_spog_qa_firstname",
				prefix + "d_spog_qa_lastname", SpogConstants.DIRECT_ADMIN, initial_direct_orgID, test);
	}

	@DataProvider(name = "sortByParams")
	public final Object[][] sortParams() {
		return new Object[][] { { "", "first_name;asc", 2, 2, "b_spog_qa@arcserve.com", "c_spog_qa@arcserve.com" },
				{ "", "first_name;desc", 2, 2, "b_spog_qa@arcserve.com", "a_spog_qa@arcserve.com" },
				{ "", "last_name;asc", 2, 2, "b_spog_qa@arcserve.com", "c_spog_qa@arcserve.com" },
				{ "", "last_name;desc", 2, 2, "b_spog_qa@arcserve.com", "a_spog_qa@arcserve.com" },
				{ "", "email;asc", 2, 2, "b_spog_qa@arcserve.com", "c_spog_qa@arcserve.com" },
				{ "", "email;desc", 2, 2, "b_spog_qa@arcserve.com", "a_spog_qa@arcserve.com" },
				{ "", "create_ts;asc", 2, 2, "b_spog_qa@arcserve.com", "a_spog_qa@arcserve.com" },
				{ "", "create_ts;desc", 2, 2, "b_spog_qa@arcserve.com", "c_spog_qa@arcserve.com" }, };
	}

	@Test(dataProvider = "sortByParams")
	public void getUsersSort(String filterOpt, String sortOpt, int pageNumber, int pageSize, String resulta,
			String resultb) {
		test.assignAuthor("Zhaoguo.Ma");
		spogServer.userLogin(initial_direct_email_full, password);
		Response response = spogServer.getAllUsersInOrganization(initial_direct_orgID, filterOpt, sortOpt, pageNumber,
				pageSize, test);

		ArrayList<String> userEmails = response.then().extract().path("data.email");

		if (userEmails.size() == 2 && userEmails.get(0).contains(resulta) && userEmails.get(1).contains(resultb)) {
			test.log(LogStatus.PASS, "the returned value is as expected");
			assertTrue("the returned value is as expected", true);
		} else {
			test.log(LogStatus.FAIL, "the returned value is not as expected");
			assertTrue("the returned value is not as expected", false);
		}
	}

	@Test
	public void filterTest() {
		test.assignAuthor("Zhaoguo.Ma");
		String prefix = RandomStringUtils.randomAlphanumeric(8);

		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		String direct_orgID = spogServer.CreateOrganizationWithCheck(prefix + "orgName_filter" + org_model_prefix,
				SpogConstants.DIRECT_ORG, prefix + "0_spog_qa@arcserve.com", password, prefix + "0_spog_qa_firstname",
				prefix + "0_spog_qa_lastname");

		spogServer.userLogin(prefix + "0_spog_qa@arcserve.com", password);

		spogServer.createUserAndCheck(prefix + "c_spog_qa@arcserve.com", password, prefix + "c_spog_qa_firstname",
				prefix + "c_spog_qa_lastname", SpogConstants.DIRECT_ADMIN, direct_orgID, test);
		spogServer.createUserAndCheck(prefix + "b_spog_qa@arcserve.com", password, prefix + "b_spog_qa_firstname",
				prefix + "b_spog_qa_lastname", SpogConstants.DIRECT_ADMIN, direct_orgID, test);
		spogServer.createUserAndCheck(prefix + "a_spog_qa@arcserve.com", password, prefix + "a_spog_qa_firstname",
				prefix + "a_spog_qa_lastname", SpogConstants.DIRECT_ADMIN, direct_orgID, test);
		spogServer.createUserAndCheck(prefix + "d_spog_qa@arcserve.com", password, prefix + "d_spog_qa_firstname",
				prefix + "d_spog_qa_lastname", SpogConstants.DIRECT_ADMIN, direct_orgID, test);

		// sort by email, filter by email+role_id+first_name, returns 1 result c;
		Response response1 = spogServer.getAllUsersInOrganization(direct_orgID,
				"email;=;*spog_qa@arcserve.com,role_id;=;direct_admin,first_name;=;*c_spog_qa_firstname", "email;asc",
				1, 20, test);
		ArrayList<String> userEmails1 = response1.then().extract().path("data.email");

		System.out.println(userEmails1);
		if (userEmails1.size() == 1 && userEmails1.get(0).contains("c_spog_qa@arcserve.com")) {
			test.log(LogStatus.PASS, "Response1: the returned value is as expected");
			assertTrue("the returned value is as expected", true);
		} else {
			test.log(LogStatus.FAIL, "Response1: the returned value is not as expected");
			assertTrue("the returned value is not as expected", false);
		}

		// sort by email, filter by email+role_id; set page_size to 2, get the 2nd page,
		// it should return b&c;
		Response response2 = spogServer.getAllUsersInOrganization(direct_orgID,
				"email;=;*spog_qa@arcserve.com,role_id;=;direct_admin", "email;asc", 2, 2, test);
		ArrayList<String> userEmails2 = response2.then().extract().path("data.email");

		System.out.println(userEmails2);
		if (userEmails2.size() == 2 && userEmails2.get(0).contains("b_spog_qa@arcserve.com")
				&& userEmails2.get(1).contains("c_spog_qa@arcserve.com")) {
			test.log(LogStatus.PASS, "Response2: the returned value is as expected");
			assertTrue("the returned value is as expected", true);
		} else {
			test.log(LogStatus.FAIL, "Response2: the returned value is not as expected");
			assertTrue("the returned value is not as expected", false);
		}

		// sort by email, filter by email+role_id; set page_size to 2, get the last
		// page, it should return d;
		Response response3 = spogServer.getAllUsersInOrganization(direct_orgID,
				"email;=;*spog_qa@arcserve.com,role_id;=;direct_admin", "email;asc", 3, 2, test);
		ArrayList<String> userEmails3 = response3.then().extract().path("data.email");

		System.out.println(userEmails3);
		if (userEmails3.size() == 1 && userEmails3.get(0).contains("d_spog_qa@arcserve.com")) {
			test.log(LogStatus.PASS, "Response3: the returned value is as expected");
			assertTrue("the returned value is as expected", true);
		} else {
			test.log(LogStatus.FAIL, "Response3: the returned value is not as expected");
			assertTrue("the returned value is not as expected", false);
		}

		// sort by email desc, filter by email+role_id; set page_size to 2, get the
		// first page, it should return d&c;
		Response response4 = spogServer.getAllUsersInOrganization(direct_orgID,
				"email;=;*spog_qa@arcserve.com,role_id;=;direct_admin", "email;desc", 1, 2, test);
		ArrayList<String> userEmails4 = response4.then().extract().path("data.email");

		System.out.println(userEmails4);
		if (userEmails4.size() == 2 && userEmails4.get(0).contains("d_spog_qa@arcserve.com")
				&& userEmails4.get(1).contains("c_spog_qa@arcserve.com")) {
			test.log(LogStatus.PASS, "Response4: the returned value is as expected");
			assertTrue("the returned value is as expected", true);
		} else {
			test.log(LogStatus.FAIL, "Response4: the returned value is not as expected");
			assertTrue("the returned value is not as expected", false);
		}

		// sort by email desc, filter by email+role_id; set page_size to 2, get the 4th
		// page, it should return null;
		Response response5 = spogServer.getAllUsersInOrganization(direct_orgID,
				"email;=;*spog_qa@arcserve.com,role_id;=;direct_admin", "email;desc", 4, 2, test);
		ArrayList<String> userEmails5 = response5.then().extract().path("data.email");

		System.out.println(userEmails5);
		if (userEmails5.size() == 0) {
			test.log(LogStatus.PASS, "Response5: the returned value is as expected");
			assertTrue("the returned value is as expected", true);
		} else {
			test.log(LogStatus.FAIL, "Response5: the returned value is not as expected");
			assertTrue("the returned value is not as expected", false);
		}

		// sort by email desc, filter by email+role_id; set page_size to 200 which is
		// invalid, get the 1st page, it should return all 5 results;
		Response response6 = spogServer.getAllUsersInOrganization(direct_orgID,
				"email;=;*spog_qa@arcserve.com,role_id;=;direct_admin", "email;desc", 1, 200, test);
		ArrayList<String> userEmails6 = response6.then().extract().path("data.email");

		if (userEmails6.size() == 5) {
			test.log(LogStatus.PASS, "Response6: the returned value is as expected");
			assertTrue("the returned value is as expected", true);
		} else {
			test.log(LogStatus.FAIL, "Response6: the returned value is not as expected");
			assertTrue("the returned value is not as expected", false);
		}

		// sort by email desc, filter by email+role_id; set page_size to 2, do not
		// provide page number, it should return the 1st page, d&c;
		Response response7 = spogServer.getAllUsersInOrganization(direct_orgID,
				"email;=;*spog_qa@arcserve.com,role_id;=;direct_admin", "email;desc", -1, 2, test);
		ArrayList<String> userEmails7 = response7.then().extract().path("data.email");

		System.out.println(userEmails7);
		if (userEmails7.size() == 2 && userEmails4.get(0).contains("d_spog_qa@arcserve.com")
				&& userEmails4.get(1).contains("c_spog_qa@arcserve.com")) {
			test.log(LogStatus.PASS, "Response7: the returned value is as expected");
			assertTrue("the returned value is as expected", true);
		} else {
			test.log(LogStatus.FAIL, "Response7: the returned value is not as expected");
			assertTrue("the returned value is not as expected", false);
		}

		// sort by email desc, filter by email+role_id, make sure there is no result,
		// should return null;
		Response response8 = spogServer.getAllUsersInOrganization(direct_orgID,
				"email;=;*dddspog_qa@arcserve.com,role_id;=;direct_admin", "email;desc", 1, -1, test);
		ArrayList<String> userEmails8 = response8.then().extract().path("data.email");

		System.out.println(userEmails8);
		if (userEmails8.size() == 0) {
			test.log(LogStatus.PASS, "Response8: the returned value is as expected");
			assertTrue("the returned value is as expected", true);
		} else {
			test.log(LogStatus.FAIL, "Response8: the returned value is not as expected");
			assertTrue("the returned value is not as expected", false);
		}
	}
	

	@Test
	public void paginationTest() {
		test.assignAuthor("Zhaoguo.Ma");
		String prefix = RandomStringUtils.randomAlphanumeric(8);

		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		String direct_orgID = spogServer.CreateOrganizationWithCheck(prefix + "orgName_filter" + org_model_prefix,
				SpogConstants.DIRECT_ORG, prefix + "0_spog_qa@arcserve.com", password, prefix + "0_spog_qa_firstname",
				prefix + "0_spog_qa_lastname");

		spogServer.userLogin(prefix + "0_spog_qa@arcserve.com", password);

		for (Integer i = 100; i < 220; i++) {
			spogServer.createUserAndCheck(prefix + i.toString() + "spog_qa@arcserve.com", password,
					prefix + i.toString() + "c_spog_qa_firstname", prefix + i.toString() + "c_spog_qa_lastname",
					SpogConstants.DIRECT_ADMIN, direct_orgID, test);
		}

		// filter by email, sort by email desc, make sure there are 100+ results, check
		// the returned users length;
		Response response1 = spogServer.getAllUsersInOrganization(direct_orgID,
				"email;=;*spog_qa@arcserve.com,role_id;=;direct_admin", "email;desc", 1, 100, test);
		ArrayList<String> users1 = response1.then().extract().path("data");
		response1.then().body("pagination.total_size", equalTo(121));
		if (users1.size() == 100) {
			test.log(LogStatus.PASS, "Response8: the returned value is as expected");
			assertTrue("the returned value is as expected", true);
		} else {
			test.log(LogStatus.FAIL, "Response8: the returned value is not as expected");
			assertTrue("the returned value is not as expected", false);
		}

		// filter by email, sort by email desc, make sure there are 100+ results, do not
		// provide page_size, get 2nd page, there should be 20 users returned;
		Response response2 = spogServer.getAllUsersInOrganization(direct_orgID,
				"email;=;*spog_qa@arcserve.com,role_id;=;direct_admin", "email;desc", 2, -1, test);
		ArrayList<String> users2 = response2.then().extract().path("data");
		response2.then().body("pagination.total_size", equalTo(121));
		if (users2.size() == 20) {
			test.log(LogStatus.PASS, "Response8: the returned value is as expected");
			assertTrue("the returned value is as expected", true);
		} else {
			test.log(LogStatus.FAIL, "Response8: the returned value is not as expected");
			assertTrue("the returned value is not as expected", false);
		}
	}
	
	/**
	 * Sprint 12, add cases for filter by UserID (MSP Account Admin);
	 */
	@Test
	public void testFilterByUserID() {
		test.assignAuthor("Zhaoguo.Ma");
		String prefix = RandomStringUtils.randomAlphanumeric(8);

		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		String direct_orgID = spogServer.CreateOrganizationWithCheck(prefix + "orgName_filter" + org_model_prefix,
				SpogConstants.MSP_ORG, prefix + "0_spog_qa@arcserve.com", password, prefix + "0_spog_qa_firstname",
				prefix + "0_spog_qa_lastname");

		spogServer.userLogin(prefix + "0_spog_qa@arcserve.com", password);

		spogServer.createUserAndCheck(prefix + "c_spog_qa@arcserve.com", password, prefix + "c_spog_qa_firstname",
				prefix + "c_spog_qa_lastname", SpogConstants.MSP_ADMIN, direct_orgID, test);
		spogServer.createUserAndCheck(prefix + "b_spog_qa@arcserve.com", password, prefix + "b_spog_qa_firstname",
				prefix + "b_spog_qa_lastname", SpogConstants.MSP_ACCOUNT_ADMIN, direct_orgID, test);
		spogServer.createUserAndCheck(prefix + "a_spog_qa@arcserve.com", password, prefix + "a_spog_qa_firstname",
				prefix + "a_spog_qa_lastname", SpogConstants.MSP_ACCOUNT_ADMIN, direct_orgID, test);
		spogServer.createUserAndCheck(prefix + "d_spog_qa@arcserve.com", password, prefix + "d_spog_qa_firstname",
				prefix + "d_spog_qa_lastname", SpogConstants.MSP_ADMIN, direct_orgID, test);

		// sort by email, filter by email+role_id+first_name, returns 1 result c;
		Response response1 = spogServer.getAllUsersInOrganization(direct_orgID,
				"role_id;=;msp_account_admin", "email;asc",
				1, 20, test);
		ArrayList<String> userEmails1 = response1.then().extract().path("data.email");

		System.out.println(userEmails1);
		if (userEmails1.size() == 2 && userEmails1.get(0).contains("a_spog_qa@arcserve.com") && userEmails1.get(1).contains("b_spog_qa@arcserve.com")) {
			test.log(LogStatus.PASS, "Response1: the returned value is as expected");
			assertTrue("the returned value is as expected", true);
		} else {
			test.log(LogStatus.FAIL, "Response1: the returned value is not as expected");
			assertTrue("the returned value is not as expected", false);
		}
	}

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
