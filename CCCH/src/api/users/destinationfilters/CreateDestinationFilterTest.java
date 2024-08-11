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

public class CreateDestinationFilterTest extends base.prepare.Is4Org {

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
	
	@DataProvider(name = "create_destination_filter_valid")
	public final Object[][] createDestinationFilterValidParams() {
		return new Object[][] {
			// different policy ID format
			{itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "filterName0", "destinationName", UUID.randomUUID().toString(), "share_folder", "true"},
			{itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "filterName1", "destinationName", UUID.randomUUID().toString() + "," + UUID.randomUUID().toString(), "share_folder", "true"},
			{itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "filterName2", "destinationName", "emptyarray", "share_folder", "true"},
			{itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "filterName3", "destinationName", null, "share_folder", "true"},
			{itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "filterName4", "destinationName", "none", "share_folder", "true"},
			// different destination type
//			{itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "filterName5", "destinationName", UUID.randomUUID().toString(), "all", "true"},
			{itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "filterName6", "destinationName", UUID.randomUUID().toString(), "share_folder", "true"},
			{itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "filterName7", "destinationName", UUID.randomUUID().toString(), "cloud_direct_volume", "true"},
			{itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "filterName8", "destinationName", UUID.randomUUID().toString(), "cloud_hybrid_store", "true"},
//			{itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "filterName9", "destinationName", UUID.randomUUID().toString(), "none", "true"},
//			{itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "filterName10", "destinationName", UUID.randomUUID().toString(), "", "true"},
//			{itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "filterName11", "destinationName", UUID.randomUUID().toString(), null, "true"},
			// different default value
			{itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "filterName12", "destinationName", UUID.randomUUID().toString(), "cloud_direct_volume", "true"},
			{itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "filterName13", "destinationName", UUID.randomUUID().toString(), "cloud_direct_volume", "false"},
			{itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "filterName14", "destinationName", UUID.randomUUID().toString(), "cloud_direct_volume", null},
			{itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "filterName15", "destinationName", UUID.randomUUID().toString(), "cloud_direct_volume", ""},
			{itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "filterName16", "destinationName", UUID.randomUUID().toString(), "cloud_direct_volume", "none"},
			// different users
			{itd.getMsp_org_1_sub_1_user_1_email(), password, itd.getMsp_org_1_sub_1_user_1(), "filterName17", "destinationName", UUID.randomUUID().toString(), "cloud_direct_volume", "true"},
			{itd.getMsp_org_1_user_1(), password, itd.getMsp_org_1_user_1(), "filterName18", "destinationName", UUID.randomUUID().toString(), "cloud_direct_volume", "true"},
			{itd.getMsp_org_1_user_1(), password, itd.getMsp_org_1_sub_1_user_1(), "filterName19", "destinationName", UUID.randomUUID().toString(), "cloud_direct_volume", "true"},
			// different destination name values
			{itd.getMsp_org_1_sub_1_user_1_email(), password, itd.getMsp_org_1_sub_1_user_1(), "filterName20", "", UUID.randomUUID().toString(), "cloud_direct_volume", "true"},
			{itd.getMsp_org_1_sub_1_user_1_email(), password, itd.getMsp_org_1_sub_1_user_1(), "filterName21", "none", UUID.randomUUID().toString(), "cloud_direct_volume", "true"},
			{itd.getMsp_org_1_sub_1_user_1_email(), password, itd.getMsp_org_1_sub_1_user_1(), "filterName22", null, UUID.randomUUID().toString(), "cloud_direct_volume", "true"},
			// admin can create filter for other admin in same org;			
			{itd.getMsp_org_1_user_1_email(), password, itd.getMsp_org_1_user_2(), "filterName23", "destinationName", UUID.randomUUID().toString(), "share_folder", "true"},
			{itd.getDirect_org_1_user_1_email(), password, itd.getDirect_org_1_user_2(), "filterName24", "destinationName", UUID.randomUUID().toString(), "share_folder", "true"},
			{itd.getMsp_org_1_sub_1_user_1_email(), password, itd.getMsp_org_1_sub_1_user_2(), "filterName25", "destinationName", UUID.randomUUID().toString(), "share_folder", "true"},
			{itd.getMsp_org_1_account_admin_1_email(), password, itd.getMsp_org_1_sub_1_user_2(), "filterName26", "destinationName", UUID.randomUUID().toString(), "share_folder", "true"},
			{itd.getMsp_org_1_user_1_email(), password, itd.getMsp_org_1_account_admin_1(), "filterName27", "destinationName", UUID.randomUUID().toString(), "share_folder", "true"},
			
			// root msp related
			{itd.getRoot_msp_org_1_user_1_email(), password, itd.getRoot_msp_org_1_user_1(), "filterName28", "destinationName", UUID.randomUUID().toString(), "share_folder", "true"},
			{itd.getRoot_msp_org_1_user_1_email(), password, itd.getRoot_msp_org_1_account_1_user_1(), "filterName29", "destinationName", UUID.randomUUID().toString(), "share_folder", "true"},
			{itd.getRoot_msp_org_1_account_admin_1_email(), password, itd.getRoot_msp_org_1_account_1_user_1(), "filterName30", "destinationName", UUID.randomUUID().toString(), "share_folder", "true"},
			{itd.getRoot_msp_org_1_sub_msp_1_user_1_email(), password, itd.getRoot_msp_org_1_sub_msp_1_user_1(), "filterName31", "destinationName", UUID.randomUUID().toString(), "share_folder", "true"},
			{itd.getRoot_msp_org_1_account_1_user_1_email(), password, itd.getRoot_msp_org_1_account_1_user_1(), "filterName32", "destinationName", UUID.randomUUID().toString(), "share_folder", "true"},
			{itd.getRoot_msp_org_1_account_admin_1_email(), password, itd.getRoot_msp_org_1_account_admin_1(), "filterName33", "destinationName", UUID.randomUUID().toString(), "share_folder", "true"},
			{itd.getRoot_msp_org_1_sub_msp_1_user_1_email(), password, itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1(), "filterName34", "destinationName", UUID.randomUUID().toString(), "share_folder", "true"},
			{itd.getRoot_msp_org_1_sub_msp_1_account_admin_1_email(), password, itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1(), "filterName35", "destinationName", UUID.randomUUID().toString(), "share_folder", "true"},
			{itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1_email(), password, itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1(), "filterName36", "destinationName", UUID.randomUUID().toString(), "share_folder", "true"},
		
		
			// monitor user related
			{itd.getRoot_msp_org_1_sub_msp_1_monitor_user_1_email(), password, itd.getRoot_msp_org_1_sub_msp_1_monitor_user_1(), "filterName36", "destinationName", UUID.randomUUID().toString(), "share_folder", "true"},
			{itd.getRoot_msp_org_1_account_1_monitor_user_1_email(), password, itd.getRoot_msp_org_1_account_1_monitor_user_1(), "filterName36", "destinationName", UUID.randomUUID().toString(), "share_folder", "true"},
			{itd.getRoot_msp_org_1_monitor_user_1_email(), password, itd.getRoot_msp_org_1_monitor_user_1(), "filterName36", "destinationName", UUID.randomUUID().toString(), "share_folder", "true"},
			{itd.getMsp_org_1_monitor_user_1_email(), password, itd.getMsp_org_1_monitor_user_1(), "filterName36", "destinationName", UUID.randomUUID().toString(), "share_folder", "true"},
			{itd.getDirect_org_1_monitor_user_1_email(), password, itd.getDirect_org_1_monitor_user_1(), "filterName36", "destinationName", UUID.randomUUID().toString(), "share_folder", "true"},
			{itd.getDirect_org_1_user_1_email(), password, itd.getDirect_org_1_monitor_user_1(), "filterName36", "destinationName", UUID.randomUUID().toString(), "share_folder", "true"},
			{itd.getMsp_org_1_user_1_email(), password, itd.getMsp_org_1_monitor_user_1(), "filterName36", "destinationName", UUID.randomUUID().toString(), "share_folder", "true"},
		};
	}
	@Test(dataProvider = "create_destination_filter_valid")
	public void createDestinationFilterValid(String userName, String password, String userID, String filterName, 
			String destinationName, String policyID, String destinationType, String isDefault) {
		spogServer.userLogin(userName, password);
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		spogDestinationServer.setToken(spogServer.getJWTToken());
		if (null != policyID) {
			String[] splitted_policyID = policyID.split(",");
			Arrays.sort(splitted_policyID);
			policyID = String.join(",", splitted_policyID);
		}
		spogDestinationServer.createDestinationFilterWithCheck(userID, prefix + filterName, prefix + destinationName, policyID, destinationType, isDefault, test);
	}
	
	@DataProvider(name = "create_destination_filter_invalid")
	public final Object[][] createDestinationFilterInvalidParams() {
		return new Object[][] {
			// different policy ID format
			{itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "", "destinationName", UUID.randomUUID().toString(), "share_folder", "true", 400, "40000001"},
			{itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "filterName0", "destinationName", UUID.randomUUID().toString(), "invalidType", "true", 400, "40000006"},
			// there is a bug that when input a string for [], the error code is empty.
			// {itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "filterName0", "destinationName", "invalidString", "share_folder", "true", 400, "40000005"},
			{itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "filterName0", "destinationName", "3bf482af-ba88-4113-952-771c0dddfaa5", "share_folder", "true", 400, "40000005"},
//			{itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "filterName0", "destinationName", UUID.randomUUID().toString(), "share_folder", "invalidIsDefault", 400, "00100001"},

			{itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "", "destinationName", "3bf482af-ba88-4113-952-771c0dddfaa5", "share_folder", "true", 400, "40000001"},
			
			{itd.getMsp_org_1_user_1(), password, itd.getDirect_org_1_user_2(), "filterName0", "destinationName", UUID.randomUUID().toString(), "share_folder", "true", 403, "00100101"},
			{itd.getDirect_org_1_user_2_email(), password, itd.getMsp_org_1_sub_1_user_1(), "filterName0", "destinationName", UUID.randomUUID().toString(), "share_folder", "true", 403, "00100101"},
			{itd.getDirect_org_1_user_2_email(), password, itd.getMsp_org_1_user_1(), "filterName0", "destinationName", UUID.randomUUID().toString(), "share_folder", "true", 403, "00100101"},
			{itd.getMsp_org_1_sub_1_user_2_email(), password, itd.getMsp_org_1_user_1(), "filterName0", "destinationName", UUID.randomUUID().toString(), "share_folder", "true", 403, "00100101"},
			{itd.getMsp_org_1_sub_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "filterName0", "destinationName", UUID.randomUUID().toString(), "share_folder", "true", 403, "00100101"},
			{itd.getMsp_org_1_account_admin_1_email(), password, itd.getMsp_org_1_sub_2_user_1(), "filterName0", "destinationName", UUID.randomUUID().toString(), "share_folder", "true", 403, "00100101"},
			{itd.getMsp_org_1_account_admin_1_email(), password, itd.getDirect_org_1_user_2(), "filterName0", "destinationName", UUID.randomUUID().toString(), "share_folder", "true", 403, "00100101"},
			{itd.getMsp_org_1_account_admin_1_email(), password, itd.getMsp_org_1_user_1(), "filterName0", "destinationName", UUID.randomUUID().toString(), "share_folder", "true", 403, "00100101"},
			{itd.getMsp_org_1_sub_1_user_2_email(), password, itd.getMsp_org_1_account_admin_1(), "filterName0", "destinationName", UUID.randomUUID().toString(), "share_folder", "true", 403, "00100101"},
			{itd.getDirect_org_1_user_2_email(), password, itd.getMsp_org_1_account_admin_1(), "filterName0", "destinationName", UUID.randomUUID().toString(), "share_folder", "true", 403, "00100101"},
			
			// root msp related
			{itd.getRoot_msp_org_1_user_1_email(), password, itd.getRoot_msp_org_1_sub_msp_1_user_1(), "filterName0", "destinationName", UUID.randomUUID().toString(), "share_folder", "true", 403, "00100101"},
			{itd.getRoot_msp_org_1_user_1_email(), password, itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1(), "filterName0", "destinationName", UUID.randomUUID().toString(), "share_folder", "true", 403, "00100101"},
			{itd.getRoot_msp_org_1_account_admin_1_email(), password, itd.getRoot_msp_org_1_user_1(), "filterName0", "destinationName", UUID.randomUUID().toString(), "share_folder", "true", 403, "00100101"},
			{itd.getRoot_msp_org_1_account_admin_1_email(), password, itd.getRoot_msp_org_1_account_2_user_1(), "filterName0", "destinationName", UUID.randomUUID().toString(), "share_folder", "true", 403, "00100101"},
			{itd.getRoot_msp_org_1_account_1_user_1_email(), password, itd.getRoot_msp_org_1_sub_msp_1_user_1(), "filterName0", "destinationName", UUID.randomUUID().toString(), "share_folder", "true", 403, "00100101"},
			{itd.getRoot_msp_org_1_sub_msp_1_user_1_email(), password, itd.getRoot_msp_org_1_account_1_user_1(), "filterName0", "destinationName", UUID.randomUUID().toString(), "share_folder", "true", 403, "00100101"},
			{itd.getRoot_msp_org_1_sub_msp_1_account_admin_2_email(), password, itd.getRoot_msp_org_1_sub_msp_1_account_2_user_1(), "filterName0", "destinationName", UUID.randomUUID().toString(), "share_folder", "true", 403, "00100101"},
			{itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1_email(), password, itd.getRoot_msp_org_1_sub_msp_1_account_2_user_1(), "filterName0", "destinationName", UUID.randomUUID().toString(), "share_folder", "true", 403, "00100101"},
			{itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1_email(), password, itd.getRoot_msp_org_1_sub_msp_2_account_1_user_1(), "filterName0", "destinationName", UUID.randomUUID().toString(), "share_folder", "true", 403, "00100101"},
		
		
			// monitor user related
			{itd.getRoot_msp_org_1_sub_msp_1_monitor_user_1_email(), password, itd.getRoot_msp_org_1_sub_msp_1_user_1(), "filterName0", "destinationName", UUID.randomUUID().toString(), "share_folder", "true", 403, "00100101"},
			{itd.getRoot_msp_org_1_account_1_monitor_user_1_email(), password, itd.getRoot_msp_org_1_account_1_user_1(), "filterName0", "destinationName", UUID.randomUUID().toString(), "share_folder", "true", 403, "00100101"},
			{itd.getRoot_msp_org_1_monitor_user_1_email(), password, itd.getRoot_msp_org_1_user_1(), "filterName0", "destinationName", UUID.randomUUID().toString(), "share_folder", "true", 403, "00100101"},
			{itd.getMsp_org_1_monitor_user_1_email(), password, itd.getMsp_org_1_user_1(), "filterName0", "destinationName", UUID.randomUUID().toString(), "share_folder", "true", 403, "00100101"},
			{itd.getDirect_org_1_monitor_user_1_email(), password, itd.getDirect_org_1_user_1(), "filterName0", "destinationName", UUID.randomUUID().toString(), "share_folder", "true", 403, "00100101"},
			{itd.getMsp_org_1_monitor_user_1_email(), password, itd.getMsp_org_1_sub_1_user_1(), "filterName0", "destinationName", UUID.randomUUID().toString(), "share_folder", "true", 403, "00100101"},
			{itd.getRoot_msp_org_1_monitor_user_1_email(), password, itd.getRoot_msp_org_1_account_1_user_1(), "filterName0", "destinationName", UUID.randomUUID().toString(), "share_folder", "true", 403, "00100101"},
		};}

	@Test(dataProvider = "create_destination_filter_invalid")
	public void createDestinationFilterInvalid(String userName, String password, String userID, String filterName, 
			String destinationName, String policyID, String destinationType, String isDefault, int statusCode, String errorCode) {
		spogServer.userLogin(userName, password);
		spogDestinationServer.setToken(spogServer.getJWTToken());
		spogDestinationServer.createDestinationFilterAndCheckCode(userID, filterName, destinationName, policyID, destinationType, isDefault, statusCode, errorCode, test);
	}
	
	@DataProvider(name = "create_destination_filter_notoken")
	public final Object[][] createDestinationFilterNoTokenParams() {
		return new Object[][] {
			// different policy ID format
			{itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "filterName0", "destinationName", UUID.randomUUID().toString(), "share_folder", "true", 401, "00900006"},
			{itd.getMsp_org_1_user_1(), password, itd.getMsp_org_1_user_1(), "filterName0", "destinationName", UUID.randomUUID().toString(), "share_folder", "true", 401, "00900006"},
			{itd.getMsp_org_1_sub_1_user_1_email(), password, itd.getMsp_org_1_sub_1_user_1(), "filterName0", "destinationName", UUID.randomUUID().toString(), "share_folder", "true", 401, "00900006"},
			};}
	
	@Test(dataProvider = "create_destination_filter_notoken")
	public void createDestinationFilterNoToken(String userName, String password, String userID, String filterName, 
			String destinationName, String policyID, String destinationType, String isDefault, int statusCode, String errorCode) {
		spogServer.userLogin(userName, password);
		spogDestinationServer.setToken(spogServer.getJWTToken());
		spogDestinationServer.setToken("");
		spogDestinationServer.createDestinationFilterAndCheckCode(userID, filterName, destinationName, policyID, destinationType, isDefault, statusCode, errorCode, test);
	}
	
	@DataProvider(name = "create_destination_filter_duplicate")
	public final Object[][] createDestinationFilterDupParams() {
		return new Object[][] {
			// different policy ID format
			{itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "filterName0", "destinationName", UUID.randomUUID().toString(), "share_folder", "true", 400, "00A00101"},
			{itd.getMsp_org_1_user_1(), password, itd.getMsp_org_1_user_1(), "filterName0", "destinationName", UUID.randomUUID().toString(), "share_folder", "true", 400, "00A00101"},
			{itd.getMsp_org_1_sub_1_user_1_email(), password, itd.getMsp_org_1_sub_1_user_1(), "filterName0", "destinationName", UUID.randomUUID().toString(), "share_folder", "true", 400, "00A00101"},
		};}
	
	@Test(dataProvider = "create_destination_filter_duplicate")
	public void createDestinationFilterDup(String userName, String password, String userID, String filterName, 
			String destinationName, String policyID, String destinationType, String isDefault, int statusCode, String errorCode) {
		spogServer.userLogin(userName, password);
		spogDestinationServer.setToken(spogServer.getJWTToken());
		spogDestinationServer.createDestinationFilterWithCheck(userID, filterName, destinationName, policyID, destinationType, isDefault, test);
		spogDestinationServer.createDestinationFilterAndCheckCode(userID, filterName, destinationName, policyID, destinationType, isDefault, statusCode, errorCode, test);
	}
	
	@DataProvider(name = "csr_readonly_param")
	public final Object[][] csr_readonly_param() {
		return new Object[][] {
			// different policy ID format
			{itd.getDirect_org_1_user_2()},
			{itd.getMsp_org_1_user_1()},
			{itd.getMsp_org_1_sub_1_user_1()},
		};}
	
	@Test(dataProvider = "csr_readonly_param")
	public void csrReadonlyTest(String userID) {
		spogServer.userLogin(csr_readonly_email, csr_readonly_password);
		spogDestinationServer.setToken(spogServer.getJWTToken());
		//(filter_name, protection_status, connection_status, protection_policy, backup_status, source_group, operating_system, site_id, source_name, source_type, is_default, test);
		spogDestinationServer.createDestinationFilterAndCheckCode(userID, "filterName", "destinationName", UUID.randomUUID().toString(), 
				"share_folder", "true", 403, "00100101", test);
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
