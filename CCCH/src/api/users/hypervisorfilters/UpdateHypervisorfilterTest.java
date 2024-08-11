package api.users.hypervisorfilters;

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

public class UpdateHypervisorfilterTest {


	private SPOGServer spogServer;
	private UserSpogServer userSpogServer;
	private String csrAdmin;
	private String csrPwd;
	private ExtentReports rep;
	private ExtentTest test;

	private String initial_msp_org_name = "spog_qa_msp_zhaoguo";
	private String initial_msp_email = "spog_qa_msp_zhaoguo@arcserve.com";
	private String initial_msp_email_full = "";
	private String initial_msp_first_name = "spog_qa_msp_ma";
	private String initial_msp_last_name = "spog_qa_msp_zhaoguo";
	
	private String initial_msp_email_added = "spog_qa_msp_zhaoguo_added@arcserve.com";
	private String initial_msp_email_full_added = "";
	private String initial_msp_first_name_added = "spog_qa_msp_ma";
	private String initial_msp_last_name_added = "spog_qa_msp_zhaoguo";
	private String initial_msp_user_ID_added = "";

	private String initial_msp_account_email = "spog_qa_msp_account_zhaoguo@arcserve.com";
	private String initial_msp_account_email_full = "";
	private String initial_msp_account_first_name = "spog_qa_msp_ma";
	private String initial_msp_account_last_name = "spog_qa_msp_zhaoguo";
	private String initial_msp_account_userID = "";

	private String initial_direct_org_name = "spog_qa_direct_zhaoguo";
	private String initial_direct_email = "spog_qa_direct_zhaoguo@arcserve.com";
	private String initial_direct_email_full = "";
	private String initial_direct_first_name = "spog_qa_direct_ma";
	private String initial_direct_last_name = "spog_qa_direct_zhaoguo2";
	
	private String initial_direct_email_added = "spog_qa_direct_zhaoguo_added@arcserve.com";
	private String initial_direct_email_full_added = "";
	private String initial_direct_first_name_added = "spog_qa_direct_ma";
	private String initial_direct_last_name_added = "spog_qa_direct_zhaoguo2";
	private String initial_direct_user_ID_added = "";

	private String initial_sub_org_name_a = "spog_qa_sub_zhaoguo_a";
	private String initial_sub_email_a = "spog_qa_sub_zhaoguo_a@arcserve.com";
	private String initial_sub_email_full_a = "";
	private String initial_sub_first_name_a = "spog_qa_sub_ma_a";
	private String initial_sub_last_name_a = "spog_qa_sub_zhaoguo_a";
	private String initial_sub_user_ID_a = "";
	
	private String initial_sub_email_a_added = "spog_qa_sub_zhaoguo_a_added@arcserve.com";
	private String initial_sub_email_full_a_added = "";
	private String initial_sub_first_name_a_added = "spog_qa_sub_ma_a";
	private String initial_sub_last_name_a_added = "spog_qa_sub_zhaoguo_a";
	private String initial_sub_user_ID_a_added = "";
	
	private String initial_sub_org_name_b = "spog_qa_sub_zhaoguo_b";
	private String initial_sub_email_b = "spog_qa_sub_zhaoguo_b@arcserve.com";
	private String initial_sub_email_full_b = "";
	private String initial_sub_first_name_b = "spog_qa_sub_ma_b";
	private String initial_sub_last_name_b = "spog_qa_sub_zhaoguo_b";
	private String initial_sub_user_ID_b = "";
	
	private String initial_sub_email_b_added = "spog_qa_sub_zhaoguo_b_added@arcserve.com";
	private String initial_sub_email_full_b_added = "";
	private String initial_sub_first_name_b_added = "spog_qa_sub_ma_b";
	private String initial_sub_last_name_b_added = "spog_qa_sub_zhaoguo_b";
	private String initial_sub_user_ID_b_added = "";

	private String initial_msp_orgID;
	private String initial_direct_orgID;
	private String initial_sub_orgID_a;
	private String initial_sub_orgID_b;

	private String password = "Pa$$w0rd";

	private SQLServerDb bqdb1;
	public int Nooftest;
	private long creationTime;
	private String BQName = null;
	private String runningMachine;
	private testcasescount count1;
	private String buildVersion;
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

		// create msp organization #1, and then 1 sub-organizations in it, create one
		// admin for the sub organization;
		this.initial_msp_email_full = prefix + this.initial_msp_email;
		this.initial_msp_orgID = spogServer.CreateOrganizationWithCheck(prefix + initial_msp_org_name,
				SpogConstants.MSP_ORG, prefix + initial_msp_email, password, prefix + initial_msp_first_name,
				prefix + initial_msp_last_name);
		
		this.initial_msp_email_full_added = prefix + this.initial_msp_email_added;
		this.initial_msp_user_ID_added = spogServer.createUserAndCheck(initial_msp_email_full_added, password, prefix+ initial_msp_first_name_added, 
				prefix+ initial_msp_last_name_added, "msp_admin", initial_msp_orgID, test);

		spogServer.userLogin(initial_msp_email_full, password);
		this.initial_sub_orgID_a = spogServer.createAccountWithCheck(initial_msp_orgID, initial_sub_org_name_a,	initial_msp_orgID);
		this.initial_sub_orgID_b = spogServer.createAccountWithCheck(initial_msp_orgID, initial_sub_org_name_b,	initial_msp_orgID);
		this.initial_sub_email_full_a = prefix + initial_sub_email_a;
		this.initial_sub_user_ID_a = spogServer.createUserAndCheck(prefix + this.initial_sub_email_a, password,
				prefix + this.initial_sub_first_name_a, prefix + this.initial_sub_last_name_a,
				SpogConstants.DIRECT_ADMIN, initial_sub_orgID_a, test);
		
		this.initial_sub_email_full_a_added = prefix + this.initial_sub_email_a_added;
		this.initial_sub_user_ID_a_added = spogServer.createUserAndCheck(prefix + this.initial_sub_email_a_added, password,
				prefix + this.initial_sub_first_name_a_added, prefix + this.initial_sub_last_name_a_added,
				SpogConstants.DIRECT_ADMIN, initial_sub_orgID_a, test);
		
		this.initial_sub_email_full_b = prefix + initial_sub_email_b;
		this.initial_sub_user_ID_b = spogServer.createUserAndCheck(prefix + this.initial_sub_email_b, password,
				prefix + this.initial_sub_first_name_b, prefix + this.initial_sub_last_name_b,
				SpogConstants.DIRECT_ADMIN, initial_sub_orgID_b, test);
		
		this.initial_sub_email_full_b_added = prefix + this.initial_sub_email_b_added;
		this.initial_sub_user_ID_b_added = spogServer.createUserAndCheck(prefix + this.initial_sub_email_b_added, password,
				prefix + this.initial_sub_first_name_b_added, prefix + this.initial_sub_last_name_b_added,
				SpogConstants.DIRECT_ADMIN, initial_sub_orgID_b, test);

		this.initial_msp_account_email_full = prefix + initial_msp_account_email;
		this.initial_msp_account_userID = spogServer.createUserAndCheck(initial_msp_account_email_full, password, initial_msp_account_first_name, 
				initial_msp_account_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, initial_msp_orgID, test);
		String[] userIds = new String[] {initial_msp_account_userID}; 
		userSpogServer.assignMspAccountAdmins(initial_msp_orgID, initial_sub_orgID_a, userIds, spogServer.getJWTToken());

		// create 1 direct organizations;
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		this.initial_direct_email_full = prefix + this.initial_direct_email;
		this.initial_direct_orgID = spogServer.CreateOrganizationWithCheck(prefix + initial_direct_org_name,
				SpogConstants.DIRECT_ORG, prefix + initial_direct_email, password, prefix + initial_direct_first_name,
				prefix + initial_direct_last_name);
		
		this.initial_direct_email_full_added = prefix + this.initial_direct_email_added;
		this.initial_direct_user_ID_added = spogServer.createUserAndCheck(prefix + this.initial_direct_email_added, password,
				prefix + this.initial_direct_first_name_added, prefix + this.initial_direct_last_name_added,
				SpogConstants.DIRECT_ADMIN, initial_direct_orgID, test);
		
		// create initial hypervisorfilter
		spogServer.userLogin(initial_direct_email_full_added, password);
		userSpogServer.setToken(spogServer.getJWTToken());
		userSpogServer.createHypervisorFilterForLoggedinUser("filterName_initial", "status", "udp", "xen", "hypervisorName", "true", test);
	}
	
	@DataProvider(name = "update_hypervisorfilter_valid")
	public final Object[][] updateHypervisorfilterValidParams() {
		return new Object[][] {
			// update filter with different filter_name
			{initial_direct_email_full_added, initial_direct_user_ID_added, "filterName01", "status", "udp", "xen", "hypervisorName", "true", 
				initial_direct_email_full_added, "filterName01_updated", "status", "udp", "xen", "hypervisorName", "false"},
			{initial_direct_email_full_added, initial_direct_user_ID_added, "filterName01", "status", "udp", "xen", "hypervisorName", "true", 
				initial_direct_email_full_added, "", "status", "udp", "xen", "hypervisorName", "true"},
			{initial_direct_email_full_added, initial_direct_user_ID_added, "filterName01", "status", "udp", "xen", "hypervisorName", "true", 
				initial_direct_email_full_added, null, "status", "udp", "xen", "hypervisorName", "true"},
			{initial_direct_email_full_added, initial_direct_user_ID_added, "filterName01", "status", "udp", "xen", "hypervisorName", "true", 
				initial_direct_email_full_added, "none", "status", "udp", "xen", "hypervisorName", "true"},

			// TODO: update filter with different status
			// TODO: update filter with different hypervisorProduct
			{initial_direct_email_full_added, initial_direct_user_ID_added, "filterName01", "status", "udp", "xen", "hypervisorName", "true", 
				initial_direct_email_full_added, null, "status", "udp,cloud_direct", "vmware,xen", "hypervisorName", "true"},
			{initial_direct_email_full_added, initial_direct_user_ID_added, "filterName01", "status", "udp", "xen", "hypervisorName", "true", 
				initial_direct_email_full_added, null, "status", "none", "vmware,xen", "hypervisorName", "true"},
			{initial_direct_email_full_added, initial_direct_user_ID_added, "filterName01", "status", "udp", "xen", "hypervisorName", "true", 
				initial_direct_email_full_added, null, "status", "emptyarray", "vmware,xen", "hypervisorName", "true"},
			{initial_direct_email_full_added, initial_direct_user_ID_added, "filterName01", "status", "udp", "xen", "hypervisorName", "true", 
				initial_direct_email_full_added, null, "status", null, "vmware,xen", "hypervisorName", "true"},
			
			// update filter with different hypervisorType
			{initial_direct_email_full_added, initial_direct_user_ID_added, "filterName01", "status", "udp", "xen", "hypervisorName", "true", 
				initial_direct_email_full_added, null, "status", "udp", "vmware,xen", "hypervisorName", "true"},
			{initial_direct_email_full_added, initial_direct_user_ID_added, "filterName01", "status", "udp", "vmware,xen", "hypervisorName", "true", 
				initial_direct_email_full_added, null, "status", "udp", "xen", "hypervisorName", "true"},
			{initial_direct_email_full_added, initial_direct_user_ID_added, "filterName01", "status", "udp", "vmware,xen", "hypervisorName", "true", 
				initial_direct_email_full_added, null, "status", "udp", "emptyarray", "hypervisorName", "true"},
			{initial_direct_email_full_added, initial_direct_user_ID_added, "filterName01", "status", "udp", "vmware,xen", "hypervisorName", "true", 
				initial_direct_email_full_added, null, "status", "udp", null, "hypervisorName", "true"},
			{initial_direct_email_full_added, initial_direct_user_ID_added, "filterName01", "status", "udp", "vmware,xen", "hypervisorName", "true", 
				initial_direct_email_full_added, null, "status", "udp", "none", "hypervisorName", "true"},
			{initial_direct_email_full_added, initial_direct_user_ID_added, "filterName01", "status", "udp", "none", "hypervisorName", "true", 
				initial_direct_email_full_added, null, "status", "udp", "vmware,xen", "hypervisorName", "true"},
			
			// update filter with different hypervisorName
			{initial_direct_email_full_added, initial_direct_user_ID_added, "filterName01", "status", "udp", "none", "hypervisorName", "true", 
				initial_direct_email_full_added, null, "status", "udp", "vmware,xen", "", "true"},
			{initial_direct_email_full_added, initial_direct_user_ID_added, "filterName01", "status", "udp", "none", "hypervisorName", "true", 
				initial_direct_email_full_added, null, "status", "udp", "vmware,xen", null, "true"},
			{initial_direct_email_full_added, initial_direct_user_ID_added, "filterName01", "status", "udp", "none", "hypervisorName", "true", 
				initial_direct_email_full_added, null, "status", "udp", "vmware,xen", "none", "true"},
			{initial_direct_email_full_added, initial_direct_user_ID_added, "filterName01", "status", "udp", "none", "", "true", 
				initial_direct_email_full_added, null, "status", "udp", "vmware,xen", "", "true"},
			{initial_direct_email_full_added, initial_direct_user_ID_added, "filterName01", "status", "udp", "none", "", "true", 
				initial_direct_email_full_added, null, "status", "udp", "vmware,xen", "hypervisorName", "true"},
			
			// update filter with different isDefault
			{initial_direct_email_full_added, initial_direct_user_ID_added, "filterName01", "status", "udp", "none", "hypervisorName", "true", 
				initial_direct_email_full_added, null, "status", "udp", "vmware,xen", "hypervisorName", "false"},
			{initial_direct_email_full_added, initial_direct_user_ID_added, "filterName01", "status", "udp", "none", "hypervisorName", "true", 
				initial_direct_email_full_added, null, "status", "udp", "vmware,xen", "hypervisorName", null},
			{initial_direct_email_full_added, initial_direct_user_ID_added, "filterName01", "status", "udp", "none", "hypervisorName", "true", 
				initial_direct_email_full_added, null, "status", "udp", "vmware,xen", "hypervisorName", "none"},
			
			// update filter for other users in same org
			{initial_direct_email_full_added, initial_direct_user_ID_added, "filterName01", "status", "udp", "none", "hypervisorName", "true", 
				initial_direct_email_full, null, "status", "udp", "vmware,xen", "hypervisorName", "false"},
			{initial_msp_email_full_added, initial_msp_user_ID_added, "filterName01", "status", "udp", "none", "hypervisorName", "true", 
				initial_msp_email_full, null, "status", "udp", "vmware,xen", "hypervisorName", "false"},
			{initial_sub_email_full_a_added, initial_sub_user_ID_a_added, "filterName01", "status", "udp", "none", "hypervisorName", "true", 
				initial_sub_email_full_a, null, "status", "udp", "vmware,xen", "hypervisorName", "false"},
			
			// msp admin to update filter for its sub org
			{initial_sub_email_full_a_added, initial_sub_user_ID_a_added, "filterName01", "status", "udp", "none", "hypervisorName", "true", 
				initial_msp_email_full, null, "status", "udp", "vmware,xen", "hypervisorName", "false"},
			
			// msp account admin
			{initial_sub_email_full_a_added, initial_sub_user_ID_a_added, "filterName01", "status", "udp", "none", "hypervisorName", "true", 
					initial_msp_account_email_full, null, "status", "udp", "vmware,xen", "hypervisorName", "false"},
			{initial_msp_account_email_full, initial_msp_account_userID, "filterName01", "status", "udp", "none", "hypervisorName", "true", 
					initial_msp_email_full, null, "status", "udp", "vmware,xen", "hypervisorName", "false"},
		};
	}
	
	@Test(dataProvider = "update_hypervisorfilter_valid")
	public void updateHypervisorfilterValid(String username, String userID, String filterName, String status, String hypervisorProduct, 
			String hypervisorType, String hypervisorName, String isDefault, String userNameUpdate, String filterNameUpdate, String statusUpdate,
			String hypervisorProductUpdate, String hypervisorTypeUpdate, String hypervisorNameUpdate, String isDefaultUpdate) {
		spogServer.userLogin(username, password);
		userSpogServer.setToken(spogServer.getJWTToken());
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		filterName = prefix + filterName;
		String filterID = userSpogServer.createHypervisorFilterForSpecificUser(userID, filterName, status, hypervisorProduct, hypervisorType, hypervisorName, isDefault, test);
		
		spogServer.userLogin(userNameUpdate, password);
		userSpogServer.setToken(spogServer.getJWTToken());
		userSpogServer.updateHypervisorFilterForSpecificUser(userID, filterID, filterNameUpdate, statusUpdate, hypervisorProductUpdate, hypervisorTypeUpdate, hypervisorNameUpdate, isDefaultUpdate, test);
	}
	
	@DataProvider(name = "update_hypervisorfilter_invalid")
	public final Object[][] updateHypervisorfilterInvalidParams() {
		return new Object[][] {
			// update filter and set duplicate name
			{initial_direct_email_full_added, initial_direct_user_ID_added, "filterName01", "status", "udp", "xen", "hypervisorName", "true", 
				initial_direct_email_full_added, "filterName_initial", "status", "udp", "xen", "hypervisorName", "false", 400, "00A00501"},
			// update filter and set invalid hypervisorType
			{initial_direct_email_full_added, initial_direct_user_ID_added, "filterName01", "status", "udp", "xen", "hypervisorName", "true", 
				initial_direct_email_full_added, "filterName_initial", "status", "udp", "hypervisorType", "hypervisorName", "false", 400, "40000006"},
			// update filter and set invalid isDefault
			{initial_direct_email_full_added, initial_direct_user_ID_added, "filterName01", "status", "udp", "xen", "hypervisorName", "true", 
				initial_direct_email_full_added, "filterName_initial", "status", "udp", "xen", "hypervisorName", "isDefault", 400, "00100001"},
			// TODO: update filter and set invalid status
			// TODO: update filter and set invalid hypervisorProduct
			
			// update filter without permission
			{initial_direct_email_full_added, initial_direct_user_ID_added, "filterName01", "status", "udp", "xen", "hypervisorName", "true", 
				initial_msp_email_full_added, "filterName", "status", "udp", "xen", "hypervisorName", "true", 403, "00100101"},
			{initial_direct_email_full_added, initial_direct_user_ID_added, "filterName01", "status", "udp", "xen", "hypervisorName", "true", 
				initial_sub_email_full_a_added, "filterName", "status", "udp", "xen", "hypervisorName", "true", 403, "00100101"},
			{initial_msp_email_full_added, initial_msp_user_ID_added, "filterName01", "status", "udp", "xen", "hypervisorName", "true", 
				initial_direct_email_full_added, "filterName", "status", "udp", "xen", "hypervisorName", "true", 403, "00100101"},
			{initial_msp_email_full_added, initial_msp_user_ID_added, "filterName01", "status", "udp", "xen", "hypervisorName", "true", 
				initial_sub_email_full_a_added, "filterName", "status", "udp", "xen", "hypervisorName", "true", 403, "00100101"},
			{initial_sub_email_full_a_added, initial_sub_user_ID_a_added, "filterName01", "status", "udp", "xen", "hypervisorName", "true", 
				initial_direct_email_full_added, "filterName", "status", "udp", "xen", "hypervisorName", "true", 403, "00100101"},
			
			// msp account admin
			{initial_direct_email_full_added, initial_direct_user_ID_added, "filterName01", "status", "udp", "xen", "hypervisorName", "true", 
				initial_msp_account_email_full, "filterName", "status", "udp", "xen", "hypervisorName", "true", 403, "00100101"},
			{initial_msp_email_full_added, initial_msp_user_ID_added, "filterName01", "status", "udp", "xen", "hypervisorName", "true", 
				initial_msp_account_email_full, "filterName", "status", "udp", "xen", "hypervisorName", "true", 403, "00100101"},
			{initial_sub_email_full_b_added, initial_sub_user_ID_b_added, "filterName01", "status", "udp", "xen", "hypervisorName", "true", 
				initial_msp_account_email_full, "filterName", "status", "udp", "xen", "hypervisorName", "true", 403, "00100101"},
			{initial_msp_account_email_full, initial_msp_account_userID, "filterName01", "status", "udp", "xen", "hypervisorName", "true", 
				initial_direct_email_full_added, "filterName", "status", "udp", "xen", "hypervisorName", "true", 403, "00100101"},
			{initial_msp_account_email_full, initial_msp_account_userID, "filterName01", "status", "udp", "xen", "hypervisorName", "true", 
					initial_sub_email_full_b_added, "filterName", "status", "udp", "xen", "hypervisorName", "true", 403, "00100101"},
		};}
	
	@Test(dataProvider = "update_hypervisorfilter_invalid")
	public void updateHypervisorfilterInvalid(String username, String userID, String filterName, String status, String hypervisorProduct, 
			String hypervisorType, String hypervisorName, String isDefault, String userNameUpdate, String filterNameUpdate, String statusUpdate,
			String hypervisorProductUpdate, String hypervisorTypeUpdate, String hypervisorNameUpdate, String isDefaultUpdate, int statusCode, String expectedErrorCode) {
		spogServer.userLogin(username, password);
		userSpogServer.setToken(spogServer.getJWTToken());
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		filterName = prefix + filterName;
		String filterID = userSpogServer.createHypervisorFilterForSpecificUser(userID, filterName, status, hypervisorProduct, hypervisorType, hypervisorName, isDefault, test);
		
		spogServer.userLogin(userNameUpdate, password);
		userSpogServer.setToken(spogServer.getJWTToken());
		userSpogServer.updateHypervisorFilterForSpecificUserWithCodeCheck(userID, filterID, filterNameUpdate, statusUpdate, 
				hypervisorProductUpdate, hypervisorTypeUpdate, hypervisorNameUpdate, isDefaultUpdate, statusCode, expectedErrorCode, test);
	}

	@DataProvider(name = "update_hypervisorfilter_notoken")
	public final Object[][] updateHypervisorfilterNoTokenParams() {
		return new Object[][] {
			{initial_direct_email_full_added, initial_direct_user_ID_added, "filterName01", "status", "udp", "xen", "hypervisorName", "true", 
				initial_direct_email_full_added, "filterName_initial", "status", "udp", "xen", "hypervisorName", "false", 401, "00900006"},
			{initial_direct_email_full_added, initial_direct_user_ID_added, "filterName01", "status", "udp", "xen", "hypervisorName", "true", 
				initial_msp_email_full_added, "filterName_initial", "status", "udp", "xen", "hypervisorName", "false", 401, "00900006"},
			{initial_direct_email_full_added, initial_direct_user_ID_added, "filterName01", "status", "udp", "xen", "hypervisorName", "true", 
				initial_sub_email_full_a_added, "filterName_initial", "status", "udp", "xen", "hypervisorName", "false", 401, "00900006"},
		};}
	
	@Test(dataProvider = "update_hypervisorfilter_notoken")
	public void updateHypervisorfilterNoToken(String username, String userID, String filterName, String status, String hypervisorProduct, 
			String hypervisorType, String hypervisorName, String isDefault, String userNameUpdate, String filterNameUpdate, String statusUpdate,
			String hypervisorProductUpdate, String hypervisorTypeUpdate, String hypervisorNameUpdate, String isDefaultUpdate, int statusCode, String expectedErrorCode) {
		spogServer.userLogin(username, password);
		userSpogServer.setToken(spogServer.getJWTToken());
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		filterName = prefix + filterName;
		String filterID = userSpogServer.createHypervisorFilterForSpecificUser(userID, filterName, status, hypervisorProduct, hypervisorType, hypervisorName, isDefault, test);
		
		spogServer.userLogin(userNameUpdate, password);
		userSpogServer.setToken("");
		userSpogServer.updateHypervisorFilterForSpecificUserWithCodeCheck(userID, filterID, filterNameUpdate, statusUpdate, 
				hypervisorProductUpdate, hypervisorTypeUpdate, hypervisorNameUpdate, isDefaultUpdate, statusCode, expectedErrorCode, test);
	}
	
	@DataProvider(name = "update_hypervisorfilter_non_existing")
	public final Object[][] updateHypervisorfilterNonExistingParams() {
		return new Object[][] {
			{initial_direct_email_full_added, initial_direct_user_ID_added, "filterID", "filterName01", "status", "udp", "xen", "hypervisorName", "true", 400, "40000005"},
			{initial_direct_email_full_added, initial_direct_user_ID_added, UUID.randomUUID().toString(), "filterName01", "status", "udp", "xen", "hypervisorName", "true", 404, "00A00502"},
			{initial_direct_email_full_added, UUID.randomUUID().toString(), UUID.randomUUID().toString(), "filterName01", "status", "udp", "xen", "hypervisorName", "true", 404, "00200007"},
		};}
	
	@Test(dataProvider = "update_hypervisorfilter_non_existing")
	public void updateHypervisorfilterNonExisting(String username, String userID, String filterID, String filterName, String status, String hypervisorProduct, 
			String hypervisorType, String hypervisorName, String isDefault, int statusCode, String expectedErrorCode) {
		spogServer.userLogin(username, password);
		userSpogServer.setToken(spogServer.getJWTToken());
		userSpogServer.updateHypervisorFilterForSpecificUserWithCodeCheck(userID, filterID, filterName, status, 
				hypervisorProduct, hypervisorType, hypervisorName, isDefault, statusCode, expectedErrorCode, test);
	}
	
	@AfterMethod
	public void afterMethodTest() {
		rep.endTest(test);
		rep.flush();
	}

	@AfterClass
	public void deleteOrgs() {
		
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

	@AfterClass
	public void aftertest() {
		test.log(LogStatus.INFO, "The total test cases passed are " + count1.getpassedcount());
		test.log(LogStatus.INFO, "the total test cases failed are " + count1.getfailedcount());
		rep.flush();
	}

	@AfterClass
	public void updatebd() {
		try {
			if (count1.getfailedcount() > 0) {
				Nooftest = count1.getpassedcount() + count1.getfailedcount() + count1.getskippedcount();
				bqdb1.updateTable(this.BQName, this.runningMachine, this.buildVersion, String.valueOf(Nooftest),
						Integer.toString(count1.getpassedcount()), Integer.toString(count1.getfailedcount()),
						String.valueOf(count1.getskippedcount()), count1.getcreationtime(), "Failed");
			} else {
				Nooftest = count1.getpassedcount() + count1.getfailedcount() + count1.getskippedcount();
				bqdb1.updateTable(this.BQName, this.runningMachine, this.buildVersion, String.valueOf(Nooftest),
						Integer.toString(count1.getpassedcount()), Integer.toString(count1.getfailedcount()),
						String.valueOf(count1.getskippedcount()), count1.getcreationtime(), "Passed");
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
