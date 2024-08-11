package api.users.destinationfilters;

import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.sun.mail.handlers.message_rfc822;

import Constants.SpogConstants;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import api.preparedata.InitialTestData;
import api.preparedata.InitialTestDataImpl;
import bsh.org.objectweb.asm.Constants;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import groovyjarjarantlr.PreservingFileWriter;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;

import static org.hamcrest.Matchers.equalTo;
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

public class CreateDestinationFilterForLoggedinUserTest extends base.prepare.Is4Org{

	private SPOGServer spogServer;
	private SPOGDestinationServer spogDestinationServer;
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
	private String uuid1 = UUID.randomUUID().toString();
	private String uuid2 = UUID.randomUUID().toString();
	private String  org_model_prefix=this.getClass().getSimpleName();

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
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		this.csrAdmin = csrAdminUserName;
		this.csrPwd = csrAdminPassword;
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		String prefix = RandomStringUtils.randomAlphanumeric(8);

		String prefix_a = RandomStringUtils.randomAlphanumeric(8) + this.getClass().getSimpleName();
		initialTestDataImpl = new InitialTestDataImpl(baseURI, port, csrAdminUserName, csrAdminPassword, prefix_a,
				password);
		itd = initialTestDataImpl.initialize();
	}
	
	@DataProvider(name = "create_destination_filter_for_loggedin_user_valid")
	public final Object[][] createDestinationFilterValidParams() {
		return new Object[][] {
			
			// different policy id format
			{itd.getDirect_org_1_user_1_email(), password, "filterName0", "destinationName", UUID.randomUUID().toString(), "share_folder", "true"},
			{itd.getDirect_org_1_user_1_email(), password, "filterName1", "destinationName", UUID.randomUUID().toString() + "," + UUID.randomUUID().toString(), "share_folder", "true"},
			{itd.getDirect_org_1_user_1_email(), password, "filterName2", "destinationName", "emptyarray", "share_folder", "true"},
			{itd.getDirect_org_1_user_1_email(), password, "filterName3", "destinationName", null, "share_folder", "true"},
			{itd.getDirect_org_1_user_1_email(), password, "filterName4", "destinationName", "none", "share_folder", "true"},
			
			// different destination type
//			{itd.getDirect_org_1_user_1_email(), password, "filterName5", "destinationName", UUID.randomUUID().toString(), "all", "true"},
			{itd.getDirect_org_1_user_1_email(), password, "filterName6", "destinationName", UUID.randomUUID().toString(), "share_folder", "true"},
			{itd.getDirect_org_1_user_1_email(), password, "filterName7", "destinationName", UUID.randomUUID().toString(), "cloud_direct_volume", "true"},
			{itd.getDirect_org_1_user_1_email(), password, "filterName8", "destinationName", UUID.randomUUID().toString(), "cloud_hybrid_store", "true"},
//			{itd.getDirect_org_1_user_1_email(), password, "filterName9", "destinationName", UUID.randomUUID().toString(), "none", "true"},
//			{itd.getDirect_org_1_user_1_email(), password, "filterName10", "destinationName", UUID.randomUUID().toString(), "", "true"},
//			{itd.getDirect_org_1_user_1_email(), password, "filterName11", "destinationName", UUID.randomUUID().toString(), null, "true"},
			
			// different default value
			{itd.getDirect_org_1_user_1_email(), password, "filterName12", "destinationName", UUID.randomUUID().toString(), "cloud_direct_volume", "true"},
			{itd.getDirect_org_1_user_1_email(), password, "filterName13", "destinationName", UUID.randomUUID().toString(), "cloud_direct_volume", "false"},
			{itd.getDirect_org_1_user_1_email(), password, "filterName14", "destinationName", UUID.randomUUID().toString(), "cloud_direct_volume", null},
			{itd.getDirect_org_1_user_1_email(), password, "filterName15", "destinationName", UUID.randomUUID().toString(), "cloud_direct_volume", ""},
			{itd.getDirect_org_1_user_1_email(), password, "filterName16", "destinationName", UUID.randomUUID().toString(), "cloud_direct_volume", "none"},
			
			// different destination name 
			{itd.getMsp_org_1_sub_1_user_1_email(), password, "filterName17", "", UUID.randomUUID().toString(), "cloud_direct_volume", "true"},
			{itd.getMsp_org_1_sub_1_user_1_email(), password, "filterName18", "none", UUID.randomUUID().toString(), "cloud_direct_volume", "true"},
			{itd.getMsp_org_1_sub_1_user_1_email(), password, "filterName19", null, UUID.randomUUID().toString(), "cloud_direct_volume", "true"},
			
			// different type of user
			{itd.getDirect_org_1_user_1_email(), password, "filterName20", "destinationName", UUID.randomUUID().toString(), "share_folder", "true"},
			{itd.getMsp_org_1_user_1_email(), password, "filterName21", "destinationName", UUID.randomUUID().toString(), "share_folder", "true"},
			{itd.getMsp_org_1_sub_1_user_2_email(), password, "filterName22", "destinationName", UUID.randomUUID().toString(), "share_folder", "true"},
			{itd.getMsp_org_1_account_admin_1_email(), password, "filterName23", "destinationName", UUID.randomUUID().toString(), "share_folder", "true"},
			{itd.getRoot_msp_org_1_user_1_email(), password, "filterName24", "destinationName", UUID.randomUUID().toString(), "share_folder", "true"},
			{itd.getRoot_msp_org_1_account_admin_1_email(), password, "filterName25", "destinationName", UUID.randomUUID().toString(), "share_folder", "true"},
			{itd.getRoot_msp_org_1_sub_msp_1_user_1_email(), password, "filterName26", "destinationName", UUID.randomUUID().toString(), "share_folder", "true"},
			{itd.getRoot_msp_org_1_sub_msp_1_account_admin_1_email(), password, "filterName27", "destinationName", UUID.randomUUID().toString(), "share_folder", "true"},
			{itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1_email(), password, "filterName28", "destinationName", UUID.randomUUID().toString(), "share_folder", "true"},
			{itd.getRoot_msp_org_1_account_1_user_1_email(), password, "filterName29", "destinationName", UUID.randomUUID().toString(), "share_folder", "true"},
			
			// monitor user related
			{itd.getRoot_msp_org_1_account_1_monitor_user_1_email(), password, "filterName30", "destinationName", UUID.randomUUID().toString(), "share_folder", "true"},
			{itd.getRoot_msp_org_1_sub_msp_1_monitor_user_1_email(), password, "filterName31", "destinationName", UUID.randomUUID().toString(), "share_folder", "true"},
			{itd.getRoot_msp_org_1_monitor_user_1_email(), password, "filterName32", "destinationName", UUID.randomUUID().toString(), "share_folder", "true"},
			{itd.getMsp_org_1_monitor_user_1_email(), password, "filterName33", "destinationName", UUID.randomUUID().toString(), "share_folder", "true"},
			{itd.getDirect_org_1_monitor_user_1_email(), password, "filterName34", "destinationName", UUID.randomUUID().toString(), "share_folder", "true"},
		};
	}
	
	@Test(dataProvider = "create_destination_filter_for_loggedin_user_valid")
	public void createDestinationFilterValid(String userName, String password, String filterName, 
			String destinationName, String policyID, String destinationType, String isDefault) {
		spogServer.userLogin(userName, password);
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		spogDestinationServer.setToken(spogServer.getJWTToken());
		if (null != policyID) {
			String[] splitted_policyID = policyID.split(",");
			Arrays.sort(splitted_policyID);
			policyID = String.join(",", splitted_policyID);
		}
		spogDestinationServer.createDestinationFilterForLoggedinUserWithCheck(prefix + filterName, prefix + destinationName, policyID, destinationType, isDefault, test);
	}
	
	@DataProvider(name = "create_destination_filter_for_loggedin_user_invalid")
	public final Object[][] createDestinationFilterInvalidParams() {
		return new Object[][] {
			// invalid filter name
			{itd.getDirect_org_1_user_1_email(), password, "", "destinationName", UUID.randomUUID().toString(), "share_folder", "true", 400, "40000001"},
			// invalid destinationType
			{itd.getDirect_org_1_user_1_email(), password, "filterName0", "destinationName", UUID.randomUUID().toString(), "invalidType", "true", 400, "40000006"},
			// there is a bug that when input a string for [], the error code is empty.
			// {itd.getDirect_org_1_user_1_email(), password, initial_direct_user_ID, "filterName0", "destinationName", "invalidString", "share_folder", "true", 400, "40000005"},
			// invalid policy uuid
			{itd.getDirect_org_1_user_1_email(), password, "filterName0", "destinationName", "3bf482af-ba88-4113-952-771c0dddfaa5", "share_folder", "true", 400, "40000005"},
			// invalid isDefault
//			{itd.getDirect_org_1_user_1_email(), password, "filterName0", "destinationName", UUID.randomUUID().toString(), "share_folder", "invalidIsDefault", 400, "00100001"},

			// multiple invalid values (invalid policy uuid and invalid fitler name)
			{itd.getDirect_org_1_user_1_email(), password, "", "destinationName", "3bf482af-ba88-4113-952-771c0dddfaa5", "share_folder", "true", 400, "40000001"},
			
		};}

	@Test(dataProvider = "create_destination_filter_for_loggedin_user_invalid")
	public void createDestinationFilterInvalid(String userName, String password, String filterName, 
			String destinationName, String policyID, String destinationType, String isDefault, int statusCode, String errorCode) {
		spogServer.userLogin(userName, password);
		spogDestinationServer.setToken(spogServer.getJWTToken());
		spogDestinationServer.createDestinationFilterForLoggedinUserAndCheckCode(filterName, destinationName, policyID, destinationType, isDefault, statusCode, errorCode, test);
	}
	
	
	@DataProvider(name = "create_destination_filter_for_loggedin_user_notoken")
	public final Object[][] createDestinationFilterNoTokenParams() {
		return new Object[][] {
			// different policy ID format
			{itd.getDirect_org_1_user_1_email(), password, "filterName0", "destinationName", UUID.randomUUID().toString(), "share_folder", "true", 401, "00900006"},
			{itd.getMsp_org_1_user_1_email(), password, "filterName0", "destinationName", UUID.randomUUID().toString(), "share_folder", "true", 401, "00900006"},
			{itd.getMsp_org_1_sub_1_user_1_email(), password, "filterName0", "destinationName", UUID.randomUUID().toString(), "share_folder", "true", 401, "00900006"},
			};}
	
	@Test(dataProvider = "create_destination_filter_for_loggedin_user_notoken")
	public void createDestinationFilterNoToken(String userName, String password, String filterName, 
			String destinationName, String policyID, String destinationType, String isDefault, int statusCode, String errorCode) {
		spogServer.userLogin(userName, password);
		spogDestinationServer.setToken(spogServer.getJWTToken());
		spogDestinationServer.setToken("");
		spogDestinationServer.createDestinationFilterForLoggedinUserAndCheckCode(filterName, destinationName, policyID, destinationType, isDefault, statusCode, errorCode, test);
	}
	
	@DataProvider(name = "create_destination_filter_for_loggedin_user_duplicate")
	public final Object[][] createDestinationFilterDupParams() {
		return new Object[][] {
			// different policy ID format
			{itd.getDirect_org_1_user_1_email(), password, "filterName0", "destinationName", UUID.randomUUID().toString(), "share_folder", "true", 400, "00A00101"},
			{itd.getMsp_org_1_user_1_email(), password, "filterName0", "destinationName", UUID.randomUUID().toString(), "share_folder", "true", 400, "00A00101"},
			{itd.getMsp_org_1_sub_1_user_1_email(), password, "filterName0", "destinationName", UUID.randomUUID().toString(), "share_folder", "true", 400, "00A00101"},
		};}
	
	@Test(dataProvider = "create_destination_filter_for_loggedin_user_duplicate")
	public void createDestinationFilterDup(String userName, String password, String filterName, 
			String destinationName, String policyID, String destinationType, String isDefault, int statusCode, String errorCode) {
		spogServer.userLogin(userName, password);
		spogDestinationServer.setToken(spogServer.getJWTToken());
		spogDestinationServer.createDestinationFilterForLoggedinUserWithCheck(filterName, destinationName, policyID, destinationType, isDefault, test);
		spogDestinationServer.createDestinationFilterForLoggedinUserAndCheckCode(filterName, destinationName, policyID, destinationType, isDefault, statusCode, errorCode, test);
	}
	
	@Test()
	public void csrReadonlyTest() {
		spogServer.userLogin(csr_readonly_email, csr_readonly_password);
		
		spogDestinationServer.setToken(spogServer.getJWTToken());
		//(filter_name, protection_status, connection_status, protection_policy, backup_status, source_group, operating_system, site_id, source_name, source_type, is_default, test);
		spogDestinationServer.createDestinationFilterForLoggedinUser("filterName" + RandomStringUtils.randomAlphanumeric(8), "destinationName", UUID.randomUUID().toString(), 
				"share_folder", "true", test);
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
