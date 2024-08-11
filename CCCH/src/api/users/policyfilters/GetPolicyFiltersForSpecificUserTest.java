package api.users.policyfilters;

import org.testng.annotations.Test;

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
import java.util.Map;
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

public class GetPolicyFiltersForSpecificUserTest extends base.prepare.Is4Org{

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
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName01", "", "emptyarray", "emptyarray", "emptyarray", "true", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName02", "policy_name", UUID.randomUUID().toString(), "finished", "failure", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName03", "policy_name", UUID.randomUUID().toString() + "," + UUID.randomUUID().toString(), "active,finished", "deploying,failure", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName04", "policy_name", UUID.randomUUID().toString(), "active", "success", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName05", "policy_name", UUID.randomUUID().toString(), "canceled", "deploying", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName06", "policy_name", UUID.randomUUID().toString(), "incomplete,canceled,failed", "deploying,failure,success", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName07", "policy_name", UUID.randomUUID().toString(), "all", "success", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName08", "policy_name", UUID.randomUUID().toString(), "missed", "success", "false", test);
		
		// create initial policyFilter
		spogServer.userLogin(itd.getMsp_org_1_user_2_email(), password);
		userSpogServer.setToken(spogServer.getJWTToken());
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName01", "", "emptyarray", "emptyarray", "emptyarray", "true", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName02", "policy_name", UUID.randomUUID().toString(), "finished", "failure", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName03", "policy_name", UUID.randomUUID().toString() + "," + UUID.randomUUID().toString(), "active,finished", "deploying,failure", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName04", "policy_name", UUID.randomUUID().toString(), "active", "success", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName05", "policy_name", UUID.randomUUID().toString(), "canceled", "deploying", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName06", "policy_name", UUID.randomUUID().toString(), "incomplete,canceled,failed", "deploying,failure,success", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName07", "policy_name", UUID.randomUUID().toString(), "all", "success", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName08", "policy_name", UUID.randomUUID().toString(), "missed", "success", "false", test);
				
		// create initial policyFilter
		spogServer.userLogin(itd.getMsp_org_1_sub_1_user_2_email(), password);
		userSpogServer.setToken(spogServer.getJWTToken());
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName01", "", "emptyarray", "emptyarray", "emptyarray", "true", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName02", "policy_name", UUID.randomUUID().toString(), "finished", "failure", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName03", "policy_name", UUID.randomUUID().toString() + "," + UUID.randomUUID().toString(), "active,finished", "deploying,failure", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName04", "policy_name", UUID.randomUUID().toString(), "active", "success", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName05", "policy_name", UUID.randomUUID().toString(), "canceled", "deploying", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName06", "policy_name", UUID.randomUUID().toString(), "incomplete,canceled,failed", "deploying,failure,success", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName07", "policy_name", UUID.randomUUID().toString(), "all", "success", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName08", "policy_name", UUID.randomUUID().toString(), "missed", "success", "false", test);
				
		// create initial policyFilter
		spogServer.userLogin(itd.getMsp_org_1_account_admin_1_email(), password);
		userSpogServer.setToken(spogServer.getJWTToken());
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName01", "", "emptyarray", "emptyarray", "emptyarray", "true", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName02", "policy_name", UUID.randomUUID().toString(), "finished", "failure", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName03", "policy_name", UUID.randomUUID().toString() + "," + UUID.randomUUID().toString(), "active,finished", "deploying,failure", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName04", "policy_name", UUID.randomUUID().toString(), "active", "success", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName05", "policy_name", UUID.randomUUID().toString(), "canceled", "deploying", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName06", "policy_name", UUID.randomUUID().toString(), "incomplete,canceled,failed", "deploying,failure,success", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName07", "policy_name", UUID.randomUUID().toString(), "all", "success", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName08", "policy_name", UUID.randomUUID().toString(), "missed", "success", "false", test);
	
		// root msp related create initial policyFilter
		spogServer.userLogin(itd.getRoot_msp_org_1_account_admin_1_email(), password);
		userSpogServer.setToken(spogServer.getJWTToken());
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName01", "", "emptyarray", "emptyarray", "emptyarray", "true", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName02", "policy_name", UUID.randomUUID().toString(), "finished", "failure", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName03", "policy_name", UUID.randomUUID().toString() + "," + UUID.randomUUID().toString(), "active,finished", "deploying,failure", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName04", "policy_name", UUID.randomUUID().toString(), "active", "success", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName05", "policy_name", UUID.randomUUID().toString(), "canceled", "deploying", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName06", "policy_name", UUID.randomUUID().toString(), "incomplete,canceled,failed", "deploying,failure,success", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName07", "policy_name", UUID.randomUUID().toString(), "all", "success", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName08", "policy_name", UUID.randomUUID().toString(), "missed", "success", "false", test);
	
		spogServer.userLogin(itd.getRoot_msp_org_1_user_1_email(), password);
		userSpogServer.setToken(spogServer.getJWTToken());
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName01", "", "emptyarray", "emptyarray", "emptyarray", "true", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName02", "policy_name", UUID.randomUUID().toString(), "finished", "failure", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName03", "policy_name", UUID.randomUUID().toString() + "," + UUID.randomUUID().toString(), "active,finished", "deploying,failure", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName04", "policy_name", UUID.randomUUID().toString(), "active", "success", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName05", "policy_name", UUID.randomUUID().toString(), "canceled", "deploying", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName06", "policy_name", UUID.randomUUID().toString(), "incomplete,canceled,failed", "deploying,failure,success", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName07", "policy_name", UUID.randomUUID().toString(), "all", "success", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName08", "policy_name", UUID.randomUUID().toString(), "missed", "success", "false", test);
	
		spogServer.userLogin(itd.getRoot_msp_org_1_sub_msp_1_account_admin_1_email(), password);
		userSpogServer.setToken(spogServer.getJWTToken());
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName01", "", "emptyarray", "emptyarray", "emptyarray", "true", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName02", "policy_name", UUID.randomUUID().toString(), "finished", "failure", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName03", "policy_name", UUID.randomUUID().toString() + "," + UUID.randomUUID().toString(), "active,finished", "deploying,failure", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName04", "policy_name", UUID.randomUUID().toString(), "active", "success", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName05", "policy_name", UUID.randomUUID().toString(), "canceled", "deploying", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName06", "policy_name", UUID.randomUUID().toString(), "incomplete,canceled,failed", "deploying,failure,success", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName07", "policy_name", UUID.randomUUID().toString(), "all", "success", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName08", "policy_name", UUID.randomUUID().toString(), "missed", "success", "false", test);
	
		spogServer.userLogin(itd.getRoot_msp_org_1_sub_msp_1_user_1_email(), password);
		userSpogServer.setToken(spogServer.getJWTToken());
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName01", "", "emptyarray", "emptyarray", "emptyarray", "true", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName02", "policy_name", UUID.randomUUID().toString(), "finished", "failure", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName03", "policy_name", UUID.randomUUID().toString() + "," + UUID.randomUUID().toString(), "active,finished", "deploying,failure", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName04", "policy_name", UUID.randomUUID().toString(), "active", "success", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName05", "policy_name", UUID.randomUUID().toString(), "canceled", "deploying", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName06", "policy_name", UUID.randomUUID().toString(), "incomplete,canceled,failed", "deploying,failure,success", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName07", "policy_name", UUID.randomUUID().toString(), "all", "success", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName08", "policy_name", UUID.randomUUID().toString(), "missed", "success", "false", test);
	
		spogServer.userLogin(itd.getRoot_msp_org_1_account_1_user_1_email(), password);
		userSpogServer.setToken(spogServer.getJWTToken());
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName01", "", "emptyarray", "emptyarray", "emptyarray", "true", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName02", "policy_name", UUID.randomUUID().toString(), "finished", "failure", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName03", "policy_name", UUID.randomUUID().toString() + "," + UUID.randomUUID().toString(), "active,finished", "deploying,failure", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName04", "policy_name", UUID.randomUUID().toString(), "active", "success", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName05", "policy_name", UUID.randomUUID().toString(), "canceled", "deploying", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName06", "policy_name", UUID.randomUUID().toString(), "incomplete,canceled,failed", "deploying,failure,success", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName07", "policy_name", UUID.randomUUID().toString(), "all", "success", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName08", "policy_name", UUID.randomUUID().toString(), "missed", "success", "false", test);
	
		spogServer.userLogin(itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1_email(), password);
		userSpogServer.setToken(spogServer.getJWTToken());
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName01", "", "emptyarray", "emptyarray", "emptyarray", "true", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName02", "policy_name", UUID.randomUUID().toString(), "finished", "failure", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName03", "policy_name", UUID.randomUUID().toString() + "," + UUID.randomUUID().toString(), "active,finished", "deploying,failure", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName04", "policy_name", UUID.randomUUID().toString(), "active", "success", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName05", "policy_name", UUID.randomUUID().toString(), "canceled", "deploying", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName06", "policy_name", UUID.randomUUID().toString(), "incomplete,canceled,failed", "deploying,failure,success", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName07", "policy_name", UUID.randomUUID().toString(), "all", "success", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName08", "policy_name", UUID.randomUUID().toString(), "missed", "success", "false", test);
	
		//monitor user realted
				spogServer.userLogin(itd.getRoot_msp_org_1_sub_msp_1_monitor_user_1_email(), password);
				userSpogServer.setToken(spogServer.getJWTToken());
				userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName01", "", "emptyarray", "emptyarray", "emptyarray", "true", test);
				userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName02", "policy_name", UUID.randomUUID().toString(), "finished", "failure", "false", test);
				userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName03", "policy_name", UUID.randomUUID().toString() + "," + UUID.randomUUID().toString(), "active,finished", "deploying,failure", "false", test);
				userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName04", "policy_name", UUID.randomUUID().toString(), "active", "success", "false", test);
				userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName05", "policy_name", UUID.randomUUID().toString(), "canceled", "deploying", "false", test);
				userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName06", "policy_name", UUID.randomUUID().toString(), "incomplete,canceled,failed", "deploying,failure,success", "false", test);
				userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName07", "policy_name", UUID.randomUUID().toString(), "all", "success", "false", test);
				userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName08", "policy_name", UUID.randomUUID().toString(), "missed", "success", "false", test);
			
				spogServer.userLogin(itd.getRoot_msp_org_1_account_1_monitor_user_1_email(), password);
				userSpogServer.setToken(spogServer.getJWTToken());
				userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName01", "", "emptyarray", "emptyarray", "emptyarray", "true", test);
				userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName02", "policy_name", UUID.randomUUID().toString(), "finished", "failure", "false", test);
				userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName03", "policy_name", UUID.randomUUID().toString() + "," + UUID.randomUUID().toString(), "active,finished", "deploying,failure", "false", test);
				userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName04", "policy_name", UUID.randomUUID().toString(), "active", "success", "false", test);
				userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName05", "policy_name", UUID.randomUUID().toString(), "canceled", "deploying", "false", test);
				userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName06", "policy_name", UUID.randomUUID().toString(), "incomplete,canceled,failed", "deploying,failure,success", "false", test);
				userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName07", "policy_name", UUID.randomUUID().toString(), "all", "success", "false", test);
				userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName08", "policy_name", UUID.randomUUID().toString(), "missed", "success", "false", test);
			
				spogServer.userLogin(itd.getRoot_msp_org_1_monitor_user_1_email(), password);
				userSpogServer.setToken(spogServer.getJWTToken());
				userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName01", "", "emptyarray", "emptyarray", "emptyarray", "true", test);
				userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName02", "policy_name", UUID.randomUUID().toString(), "finished", "failure", "false", test);
				userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName03", "policy_name", UUID.randomUUID().toString() + "," + UUID.randomUUID().toString(), "active,finished", "deploying,failure", "false", test);
				userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName04", "policy_name", UUID.randomUUID().toString(), "active", "success", "false", test);
				userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName05", "policy_name", UUID.randomUUID().toString(), "canceled", "deploying", "false", test);
				userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName06", "policy_name", UUID.randomUUID().toString(), "incomplete,canceled,failed", "deploying,failure,success", "false", test);
				userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName07", "policy_name", UUID.randomUUID().toString(), "all", "success", "false", test);
				userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName08", "policy_name", UUID.randomUUID().toString(), "missed", "success", "false", test);
			
				spogServer.userLogin(itd.getMsp_org_1_monitor_user_1_email(), password);
				userSpogServer.setToken(spogServer.getJWTToken());
				userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName01", "", "emptyarray", "emptyarray", "emptyarray", "true", test);
				userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName02", "policy_name", UUID.randomUUID().toString(), "finished", "failure", "false", test);
				userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName03", "policy_name", UUID.randomUUID().toString() + "," + UUID.randomUUID().toString(), "active,finished", "deploying,failure", "false", test);
				userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName04", "policy_name", UUID.randomUUID().toString(), "active", "success", "false", test);
				userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName05", "policy_name", UUID.randomUUID().toString(), "canceled", "deploying", "false", test);
				userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName06", "policy_name", UUID.randomUUID().toString(), "incomplete,canceled,failed", "deploying,failure,success", "false", test);
				userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName07", "policy_name", UUID.randomUUID().toString(), "all", "success", "false", test);
				userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName08", "policy_name", UUID.randomUUID().toString(), "missed", "success", "false", test);
			
				spogServer.userLogin(itd.getDirect_org_1_monitor_user_1_email(), password);
				userSpogServer.setToken(spogServer.getJWTToken());
				userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName01", "", "emptyarray", "emptyarray", "emptyarray", "true", test);
				userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName02", "policy_name", UUID.randomUUID().toString(), "finished", "failure", "false", test);
				userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName03", "policy_name", UUID.randomUUID().toString() + "," + UUID.randomUUID().toString(), "active,finished", "deploying,failure", "false", test);
				userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName04", "policy_name", UUID.randomUUID().toString(), "active", "success", "false", test);
				userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName05", "policy_name", UUID.randomUUID().toString(), "canceled", "deploying", "false", test);
				userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName06", "policy_name", UUID.randomUUID().toString(), "incomplete,canceled,failed", "deploying,failure,success", "false", test);
				userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName07", "policy_name", UUID.randomUUID().toString(), "all", "success", "false", test);
				userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName08", "policy_name", UUID.randomUUID().toString(), "missed", "success", "false", test);
			
	}
	
	@DataProvider(name = "valid_params")
	public final Object[][] getPolicyFilterValidParams() {
		return new Object[][] {
			// get filter with different is_default
			{itd.getDirect_org_1_user_2_email(), itd.getDirect_org_1_user_2(), 8},
			{itd.getMsp_org_1_user_2_email(), itd.getMsp_org_1_user_2(), 8},
			{itd.getMsp_org_1_sub_1_user_2_email(), itd.getMsp_org_1_sub_1_user_2(), 8},
			
			// get filter for other users in same org
			{itd.getDirect_org_1_user_1_email(), itd.getDirect_org_1_user_2(), 8},
			{itd.getMsp_org_1_user_1_email(), itd.getMsp_org_1_user_2(), 8},
			{itd.getMsp_org_1_sub_1_user_1_email(), itd.getMsp_org_1_sub_1_user_2(), 8},
			
			// msp admin get filter in sub org
			{itd.getMsp_org_1_user_1_email(), itd.getMsp_org_1_sub_1_user_2(), 8},
			
			// msp account admin
			{itd.getMsp_org_1_user_1_email(), itd.getMsp_org_1_account_admin_1(), 8},
			{itd.getMsp_org_1_account_admin_1_email(), itd.getMsp_org_1_sub_1_user_2(), 8},
			
			// root msp related
			{itd.getRoot_msp_org_1_user_1_email(), itd.getRoot_msp_org_1_account_1_user_1(), 8},
			{itd.getRoot_msp_org_1_account_admin_1_email(), itd.getRoot_msp_org_1_account_1_user_1(), 8},
			{itd.getRoot_msp_org_1_sub_msp_1_user_1_email(), itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1(), 8},
			{itd.getRoot_msp_org_1_sub_msp_1_account_admin_1_email(), itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1(), 8},
			{itd.getRoot_msp_org_1_user_1_email(), itd.getRoot_msp_org_1_account_admin_1(), 8},
			{itd.getRoot_msp_org_1_sub_msp_1_user_1_email(), itd.getRoot_msp_org_1_sub_msp_1_account_admin_1(), 8},
			
			// monitor user related
			{itd.getRoot_msp_org_1_user_1_email(), itd.getRoot_msp_org_1_monitor_user_1(), 8},
			{itd.getRoot_msp_org_1_sub_msp_1_user_1_email(), itd.getRoot_msp_org_1_sub_msp_1_monitor_user_1(), 8},
			{itd.getRoot_msp_org_1_account_1_user_1_email(), itd.getRoot_msp_org_1_account_1_monitor_user_1(), 8},
			{itd.getMsp_org_1_user_1_email(), itd.getMsp_org_1_monitor_user_1(), 8},
			{itd.getDirect_org_1_user_1_email(), itd.getDirect_org_1_monitor_user_1(), 8},
			
			{itd.getRoot_msp_org_1_monitor_user_1_email(), itd.getRoot_msp_org_1_user_1(), 8},
			{itd.getRoot_msp_org_1_sub_msp_1_monitor_user_1_email(), itd.getRoot_msp_org_1_sub_msp_1_user_1(), 8},
			{itd.getRoot_msp_org_1_account_1_monitor_user_1_email(), itd.getRoot_msp_org_1_account_1_user_1(), 8},
			{itd.getMsp_org_1_monitor_user_1_email(), itd.getMsp_org_1_user_2(), 8},
			{itd.getDirect_org_1_monitor_user_1_email(), itd.getDirect_org_1_user_2(), 8},
			
			{itd.getRoot_msp_org_1_monitor_user_1_email(), itd.getRoot_msp_org_1_account_1_user_1(), 8},
			{itd.getRoot_msp_org_1_sub_msp_1_monitor_user_1_email(), itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1(), 8},
			{itd.getMsp_org_1_monitor_user_1_email(), itd.getMsp_org_1_sub_1_user_2(), 8},
		};
	}
	
	@Test(dataProvider = "valid_params")
	public void getPolicyFilterValid(String username, String userID, int expectedNumber) {
		spogServer.userLogin(username, password);
		userSpogServer.setToken(spogServer.getJWTToken());
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		
		Response response = userSpogServer.getPolicyFiltersForSpecificUser(userID, test);
		
		response.then().body("pagination.total_size", equalTo(expectedNumber));
		ArrayList<Map<String, Object>> policyFilters = response.then().extract().path("data");
		assertEquals(expectedNumber, policyFilters.size());
	}
	
	@DataProvider(name = "invalid_params")
	public final Object[][] getPolicyFilterInvalidParams() {
		return new Object[][] {
			
			{itd.getDirect_org_1_user_2_email(), itd.getMsp_org_1_user_2(), 403, "00100101"},
			{itd.getDirect_org_1_user_2_email(), itd.getMsp_org_1_sub_1_user_2(), 403, "00100101"},
			{itd.getMsp_org_1_user_2_email(), itd.getDirect_org_1_user_2(), 403, "00100101"},
			{itd.getMsp_org_1_sub_1_user_2_email(), itd.getMsp_org_1_user_2(), 403, "00100101"},
			{itd.getMsp_org_1_sub_1_user_2_email(), itd.getDirect_org_1_user_2(), 403, "00100101"},
			
			// msp account admin
			{itd.getMsp_org_1_account_admin_1_email(), itd.getMsp_org_1_user_2(), 403, "00100101"},
			{itd.getMsp_org_1_account_admin_1_email(), itd.getDirect_org_1_user_2(), 403, "00100101"},
			{itd.getMsp_org_1_account_admin_1_email(), itd.getMsp_org_1_sub_2_user_1(), 403, "00100101"},
			{itd.getMsp_org_1_sub_1_user_2_email(), itd.getMsp_org_1_account_admin_1(), 403, "00100101"},
			{itd.getDirect_org_1_user_2_email(), itd.getMsp_org_1_account_admin_1(), 403, "00100101"},
			
			{itd.getDirect_org_1_user_1_email(), UUID.randomUUID().toString(), 404, "00200007"},
			{itd.getMsp_org_1_user_1_email(), UUID.randomUUID().toString(), 404, "00200007"},
			{itd.getMsp_org_1_sub_1_user_1_email(), UUID.randomUUID().toString(), 404, "00200007"},
			
			{itd.getDirect_org_1_user_1_email(), "invaliUUID", 400, "40000005"},
			{itd.getMsp_org_1_user_1_email(), "invaliUUID", 400, "40000005"},
			{itd.getMsp_org_1_sub_1_user_1_email(), "invaliUUID", 400, "40000005"},
			
			// root msp related
			{itd.getRoot_msp_org_1_user_1_email(), itd.getRoot_msp_org_1_sub_msp_1_user_1(), 403, "00100101"},
			{itd.getRoot_msp_org_1_user_1_email(), itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1(), 403, "00100101"},
			{itd.getRoot_msp_org_1_account_admin_2_email(), itd.getRoot_msp_org_1_account_1_user_1(), 403, "00100101"},
			{itd.getRoot_msp_org_1_sub_msp_1_account_admin_2_email(), itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1(), 403, "00100101"},
			{itd.getRoot_msp_org_1_user_1_email(), itd.getRoot_msp_org_2_user_1(), 403, "00100101"},
			{itd.getRoot_msp_org_1_sub_msp_1_user_1_email(), itd.getRoot_msp_org_1_sub_msp_2_user_1(), 403, "00100101"},
			{itd.getRoot_msp_org_1_account_1_user_1_email(), itd.getRoot_msp_org_1_account_2_user_1(), 403, "00100101"},
			{itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1_email(), itd.getRoot_msp_org_1_sub_msp_1_account_2_user_1(), 403, "00100101"},
			{itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1_email(), itd.getRoot_msp_org_1_sub_msp_2_account_1_user_1(), 403, "00100101"},
			{itd.getRoot_msp_org_1_user_1_email(), itd.getMsp_org_1_user_2(), 403, "00100101"},
			
			// monitor user related
			
		};
	}
	
	@Test(dataProvider = "invalid_params")
	public void getPolicyFilterInvalid(String username, String userID, int statusCode, String expectedErrorCode) {
		spogServer.userLogin(username, password);
		userSpogServer.setToken(spogServer.getJWTToken());
	
		userSpogServer.getPolicyFiltersForSpecificUserWithErrorCheck(userID, statusCode, expectedErrorCode,test);
	}
	
	@DataProvider(name = "no_token")
	public final Object[][] getPolicyFilterNoTokenParams() {
		return new Object[][] {
			
			{itd.getDirect_org_1_user_2_email(), itd.getMsp_org_1_user_2(), 401, "00900006"},
			{itd.getDirect_org_1_user_2_email(), itd.getMsp_org_1_sub_1_user_2(), 401, "00900006"},
			{itd.getMsp_org_1_user_2_email(), itd.getDirect_org_1_user_2(), 401, "00900006"},
			
		};
	}
	
	@Test(dataProvider = "no_token")
	public void getPolicyFilterNoToken(String username, String userID, int statusCode, String expectedErrorCode) {
		spogServer.userLogin(username, password);
		userSpogServer.setToken("");
	
		userSpogServer.getPolicyFiltersForSpecificUserWithErrorCheck(userID, statusCode, expectedErrorCode,test);
	}
	
	@DataProvider(name = "pagination")
	public final Object[][] getPolicyFiltersPaginationParams(){
		return new Object[][] {
			{"filter_name;=;filterName", "create_ts;desc", 1, 2, new String[] {"filterName08", "filterName07"}},
			{"filter_name;=;filterName", "create_ts;asc", 2, 2, new String[] {"filterName03", "filterName04"}},
			{"filter_name;=;filterName05", "create_ts;asc", 1, 2, new String[] {"filterName05"}},
			{"is_default;=;true", "create_ts;asc", 1, 2, new String[] {"filterName01"}},
		};
	}
	
	@Test(dataProvider = "pagination")
	public void getpolicyFiltersPagination(String filterStr, String sortStr, int pageNumber, int pageSize, String[] expectedFilterNames) {
		
		
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		String username = RandomStringUtils.randomAlphanumeric(8) + "spogqa@arcserve.com";
		
		
		spogServer.CreateOrganizationWithCheck("organizationName" + org_model_prefix, SpogConstants.DIRECT_ORG, username, password, "organizationFirstName", "organizationLastName");
		spogServer.userLogin(username, password);
		String userID = spogServer.GetLoggedinUser_UserID();
		
		userSpogServer.setToken(spogServer.getJWTToken());
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName01", "", "emptyarray", "emptyarray", "emptyarray", "true", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName02", "policy_name", UUID.randomUUID().toString(), "finished", "failure", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName03", "policy_name", UUID.randomUUID().toString() + "," + UUID.randomUUID().toString(), "active,finished", "deploying,failure", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName04", "policy_name", UUID.randomUUID().toString(), "active", "success", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName05", "policy_name", UUID.randomUUID().toString(), "canceled", "deploying", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName06", "policy_name", UUID.randomUUID().toString(), "incomplete,canceled,failed", "deploying,failure,success", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName07", "policy_name", UUID.randomUUID().toString(), "all", "success", "false", test);
		userSpogServer.createPolicyFilterForLoggedinUserWithCheck("filterName08", "policy_name", UUID.randomUUID().toString(), "missed", "success", "false", test);
		
		Response response = userSpogServer.getPolicyFiltersForSpecificUser(userID, filterStr, sortStr, pageNumber, pageSize, test);
		ArrayList<String> actualFilterNames = response.then().extract().path("data.filter_name");
		int size = expectedFilterNames.length;
		assertEquals(size, actualFilterNames.size());
		
		for (int i = 0; i < size; i++) {
			assertEquals(expectedFilterNames[i], actualFilterNames.get(i));
		}	
	}
	
	@DataProvider(name = "csr_readonly_param")
	public final Object[][] csr_readonly_param() {
		return new Object[][] {
			// different policy ID format
			{itd.getDirect_org_1_user_2_email(), itd.getDirect_org_1_user_2()},
			{itd.getMsp_org_1_user_2_email(), itd.getMsp_org_1_user_2()},
			{itd.getMsp_org_1_sub_1_user_1_email(), itd.getMsp_org_1_sub_1_user_1()},
		};}
	
	@Test(dataProvider = "csr_readonly_param", priority=100)
	public void csrReadonlyTest(String username, String userID) {
		spogServer.userLogin(username, password);
		userSpogServer.setToken(spogServer.getJWTToken());
		String filterID = userSpogServer.createPolicyFilterForSpecificUserWithCheck(userID, "filter_name"+RandomStringUtils.randomAlphanumeric(8), "policy_name", UUID.randomUUID().toString(), 
				"active", "deploying", "true", test);
		spogServer.userLogin(csr_readonly_email, csr_readonly_password);
		userSpogServer.setToken(spogServer.getJWTToken());
		userSpogServer.getPolicyFiltersForSpecificUser(userID, test);
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

