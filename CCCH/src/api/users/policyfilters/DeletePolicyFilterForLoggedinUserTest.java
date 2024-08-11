package api.users.policyfilters;

import org.testng.annotations.Test;

import com.fasterxml.jackson.core.filter.FilteringGeneratorDelegate;
import com.google.inject.PrivateBinder;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.sun.mail.handlers.message_rfc822;

import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGJobServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import api.preparedata.InitialTestData;
import api.preparedata.InitialTestDataImpl;
import bsh.org.objectweb.asm.Constants;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.User;
import genericutil.testcasescount;
import groovyjarjarantlr.PreservingFileWriter;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.codehaus.groovy.ast.expr.PrefixExpression;

import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import Constants.SpogConstants;

import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import Constants.SourceType;

public class DeletePolicyFilterForLoggedinUserTest extends base.prepare.Is4Org{

	private SPOGServer spogServer;
	private UserSpogServer userSpogServer;
	private String csrAdmin;
	private String csrPwd;
//	private ExtentReports rep;
	private ExtentTest test;

	private String csr_readonly_email = "zhaoguo.ma+csrreadonly@gmail.com";
	private String csr_readonly_password = "Zetta1234";

	private InitialTestDataImpl initialTestDataImpl;
	private InitialTestData itd;

	private String password = "Pa$$w0rd";
//	private SQLServerDb bqdb1;
//	public int Nooftest;
//	private long creationTime;
//	private String BQName = null;
//	private String runningMachine;
//	private testcasescount count1;
//	private String buildVersion;
	private String  org_model_prefix=this.getClass().getSimpleName();
	private String uuid1 = UUID.randomUUID().toString();
	private String uuid2 = UUID.randomUUID().toString();

	@BeforeClass
	@Parameters({ "baseURI", "port", "csrAdminUserName", "csrAdminPassword", "logFolder", "runningMachine",
			"buildVersion" })
	public void beforeClass(String baseURI, String port, String csrAdminUserName, String csrAdminPassword,
			String logFolder, String runningMachine, String buildVersion) {
		rep = ExtentManager.getInstance("GetSpecificFilterFromSpecificUsersTest", logFolder);
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
						String.valueOf(Nooftest), count1.getcreationtime(), "InProgress", author + " and Rest server is "+baseURI.split("//")[1]);
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
		this.csrAdmin = csrAdminUserName;
		this.csrPwd = csrAdminPassword;
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		
		String prefix_a = RandomStringUtils.randomAlphanumeric(8) + this.getClass().getSimpleName();		
		initialTestDataImpl = new InitialTestDataImpl(baseURI, port, csrAdminUserName, csrAdminPassword, prefix_a, password);
		itd = initialTestDataImpl.initialize();
		// create initial policyFilter
		spogServer.userLogin(itd.getDirect_org_1_user_2_email(), password);
		userSpogServer.setToken(spogServer.getJWTToken());
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName_initial", "policy_name", "emptyarray", "emptyarray", "emptyarray", "true", test);
	}
	
	@DataProvider(name = "valid_params")
	public final Object[][] deletePolicyFilterValidParams() {
		return new Object[][] {
			{itd.getDirect_org_1_user_2_email(), "filter_name", "policy_name", UUID.randomUUID().toString(), "active", "deploying", "true"}, 
			{itd.getMsp_org_1_sub_1_user_2_email(), "filter_name", "policy_name", UUID.randomUUID().toString(), "active", "deploying", "true"},
			{itd.getMsp_org_1_user_1_email(), "filter_name", "policy_name", UUID.randomUUID().toString(), "active", "deploying", "true"},
			{itd.getMsp_org_1_account_admin_1_email(), "filter_name", "policy_name", UUID.randomUUID().toString(), "active", "deploying", "true"},
			// root msp related
			{itd.getRoot_msp_org_1_user_1_email(), "filter_name", "policy_name", UUID.randomUUID().toString(), "active", "deploying", "true"},
			{itd.getRoot_msp_org_1_account_admin_1_email(), "filter_name", "policy_name", UUID.randomUUID().toString(), "active", "deploying", "true"},
			{itd.getRoot_msp_org_1_sub_msp_1_user_1_email(), "filter_name", "policy_name", UUID.randomUUID().toString(), "active", "deploying", "true"},
			{itd.getRoot_msp_org_1_sub_msp_1_account_admin_1_email(), "filter_name", "policy_name", UUID.randomUUID().toString(), "active", "deploying", "true"},
			{itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1_email(), "filter_name", "policy_name", UUID.randomUUID().toString(), "active", "deploying", "true"},
			{itd.getRoot_msp_org_1_account_1_user_2_email(), "filter_name", "policy_name", UUID.randomUUID().toString(), "active", "deploying", "true"},

			// monitor user related
			{itd.getRoot_msp_org_1_monitor_user_1_email(), "filter_name", "policy_name", UUID.randomUUID().toString(), "active", "deploying", "true"},
			{itd.getRoot_msp_org_1_sub_msp_1_monitor_user_1_email(), "filter_name", "policy_name", UUID.randomUUID().toString(), "active", "deploying", "true"},
			{itd.getRoot_msp_org_1_account_1_monitor_user_1_email(), "filter_name", "policy_name", UUID.randomUUID().toString(), "active", "deploying", "true"},
			{itd.getMsp_org_1_monitor_user_1_email(), "filter_name", "policy_name", UUID.randomUUID().toString(), "active", "deploying", "true"},
			{itd.getDirect_org_1_monitor_user_1_email(), "filter_name", "policy_name", UUID.randomUUID().toString(), "active", "deploying", "true"},
		};
	}
	
	@Test (dataProvider = "valid_params")
	public void deletePolicyFilterValidParamsTest(String user_name, String filter_name, String policy_name, String group_id,
			String last_backup_status, String status, String is_default) {
		spogServer.userLogin(user_name, password);
		userSpogServer.setToken(spogServer.getJWTToken());
		
		String prefix = RandomStringUtils.randomAlphanumeric(8);	
		String filter_id = userSpogServer.createPolicyFilterForLoggedinUserWithCheck(prefix + filter_name, policy_name, group_id, last_backup_status, status, is_default, test);
		userSpogServer.deleteSpecificPolicyFilterForLoggedinUser(filter_id, test);
		userSpogServer.getSpecificPolicyFilterForLoggedinUserWithErrorCheck(filter_id, 404, "00A00A02", test);
	}
	
	@DataProvider(name = "no_token")
	public final Object[][] deletePolicyFilterNoToken() {
		Object initial_msp_userID_added;
		return new Object[][] {
			// invalid filter_name
			{itd.getDirect_org_1_user_2_email(), itd.getDirect_org_1_user_1(), "filterName_initial", "policy_name", UUID.randomUUID().toString(), "active", "deploying", "true", 401, "00900006"},
		};}
	
	@Test(dataProvider = "no_token")
	public void deletePolicyFilternoTokenTest(String user_name, String user_id, String filter_name, String policy_name, String group_id,
			String last_backup_status, String status, String is_default, int status_code, String error_code) {
		
		spogServer.userLogin(user_name, password);
		userSpogServer.setToken(spogServer.getJWTToken());
		
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		String filter_id = userSpogServer.createPolicyFilterForSpecificUserWithCheck(user_id, prefix + filter_name, policy_name, group_id, last_backup_status, status, is_default, test);
		userSpogServer.setToken("");
		userSpogServer.deleteSpecificPolicyFilterForLoggedinUserWithErrorCheck(filter_id, status_code, error_code, test);
	}
	
	@Test
	public void deletePolicyFilterInvalidID() {
		spogServer.userLogin(itd.getDirect_org_1_user_2_email(), password);
		userSpogServer.setToken(spogServer.getJWTToken());
		userSpogServer.deleteSpecificPolicyFilterForLoggedinUserWithErrorCheck(UUID.randomUUID().toString(), 404, "00A00A02", test);
		userSpogServer.deleteSpecificPolicyFilterForLoggedinUserWithErrorCheck("invalid_uuid", 400, "40000005", test);
	}
	
	@Test()
	public void csrReadonlyTest() {
		spogServer.userLogin(csr_readonly_email, csr_readonly_password);
	    userSpogServer.setToken(spogServer.getJWTToken());
		String filterID = userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filter_name"+RandomStringUtils.randomAlphanumeric(8), "policy_name", UUID.randomUUID().toString(), 
				"active", "deploying", "true", test);
		userSpogServer.deleteSpecificPolicyFilterForLoggedinUser(filterID, test);
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

