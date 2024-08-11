package api.users.filter;

import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.sun.mail.handlers.message_rfc822;

import Constants.SpogConstants;
import InvokerServer.SPOGServer;
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

public class GetSpecificFilterFromSpecificUserTest {

	private SPOGServer spogServer;
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


	private String initial_direct_org_name = "spog_qa_direct_zhaoguo";
	private String initial_direct_email = "spog_qa_direct_zhaoguo@arcserve.com";
	private String initial_direct_email_full = "";
	private String initial_direct_first_name = "spog_qa_direct_ma";
	private String initial_direct_last_name = "spog_qa_direct_zhaoguo2";
	
	private String initial_direct_email_added = "spog_qa_direct_zhaoguo_added@arcserve.com";
	private String initial_direct_email_full_added = "";
	private String initial_direct_first_name_added = "spog_qa_direct_ma";
	private String initial_direct_last_name_added = "spog_qa_direct_zhaoguo2";

	private String initial_sub_org_name_a = "spog_qa_sub_zhaoguo_a";
	private String initial_sub_email_a = "spog_qa_sub_zhaoguo_a@arcserve.com";
	private String initial_sub_email_full_a = "";
	private String initial_sub_first_name_a = "spog_qa_sub_ma_a";
	private String initial_sub_last_name_a = "spog_qa_sub_zhaoguo_a";
	
	private String initial_sub_email_a_added = "spog_qa_sub_zhaoguo_a_added@arcserve.com";
	private String initial_sub_email_full_a_added = "";
	private String initial_sub_first_name_a_added = "spog_qa_sub_ma_a";
	private String initial_sub_last_name_a_added = "spog_qa_sub_zhaoguo_a";

	private String initial_msp_orgID;
	private String initial_direct_orgID;
	private String initial_sub_orgID_a;

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
		spogServer.createUserAndCheck(initial_msp_email_full_added, password, prefix+ initial_msp_first_name_added, 
				prefix+ initial_msp_last_name, "msp_admin", initial_msp_orgID, test);

		this.initial_sub_orgID_a = spogServer.createAccountWithCheck(initial_msp_orgID, initial_sub_org_name_a,
				initial_msp_orgID);

		spogServer.userLogin(initial_msp_email_full, password);
		this.initial_sub_email_full_a = prefix + initial_sub_email_a;
		spogServer.createUserAndCheck(prefix + this.initial_sub_email_a, password,
				prefix + this.initial_sub_first_name_a, prefix + this.initial_sub_last_name_a,
				SpogConstants.DIRECT_ADMIN, initial_sub_orgID_a, test);
		
		this.initial_sub_email_full_a_added = prefix + this.initial_sub_email_a_added;
		spogServer.createUserAndCheck(prefix + this.initial_sub_email_a_added, password,
				prefix + this.initial_sub_first_name_a_added, prefix + this.initial_sub_last_name_a_added,
				SpogConstants.DIRECT_ADMIN, initial_sub_orgID_a, test);

		// create 1 direct organizations;
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		this.initial_direct_email_full = prefix + this.initial_direct_email;
		this.initial_direct_orgID = spogServer.CreateOrganizationWithCheck(prefix + initial_direct_org_name,
				SpogConstants.DIRECT_ORG, prefix + initial_direct_email, password, prefix + initial_direct_first_name,
				prefix + initial_direct_last_name);
		
		this.initial_direct_email_full_added = prefix + this.initial_direct_email_added;
		spogServer.createUserAndCheck(prefix + this.initial_direct_email_added, password,
				prefix + this.initial_direct_first_name_added, prefix + this.initial_direct_last_name_added,
				SpogConstants.DIRECT_ADMIN, initial_direct_orgID, test);
		
	}
	
	public void compareFilter(String filter_id, String filter_name, String user_id, String organization_id, String protection_status,
						String connection_status, String protection_policy, String backup_status, 
						String source_group, String operating_system, String applications, String site_id, String source_name, String source_type, Boolean is_default, Response response) {
		
		String actual_filter_id = response.then().extract().path("data.filter_id");
	    
	    response.then().body("data.filter_name", equalTo(filter_name))
	    .body("data.organization_id",equalTo(organization_id))
	    .body("data.source_type", equalTo(source_type))
	    .body("data.is_default", equalTo(is_default)); 
	    
	    
	    ArrayList<String> response_protection_status =
	        response.then().extract().path("data.protection_status");
	    spogServer.assertFilterItem(protection_status, response_protection_status);

	    ArrayList<String> response_connection_status =
	        response.then().extract().path("data.connection_status");
	    spogServer.assertFilterItem(connection_status, response_connection_status);

	    ArrayList<String> response_protection_policy =
	        response.then().extract().path("data.policy_id");
	    spogServer.assertFilterItem(protection_policy, response_protection_policy);

	    ArrayList<String> response_backup_status = response.then().extract().path("data.last_backup_status");
	    spogServer.assertFilterItem(backup_status, response_backup_status);

	    ArrayList<String> response_source_group = response.then().extract().path("data.group_id");
	    spogServer.assertFilterItem(source_group, response_source_group);
	    
	    ArrayList<String> response_operating_system =
	        response.then().extract().path("data.os_major");	    
	    spogServer.assertFilterItem(operating_system, response_operating_system);
	   
//	    ArrayList<String> response_applications = response.then().extract().path("data.applications");
//	    spogServer.assertFilterItem(applications, response_applications);
	    
	    ArrayList<String> response_site_id = response.then().extract().path("data.site_id");
	    spogServer.assertFilterItem(site_id, response_site_id);
	}

	@DataProvider(name = "basicfilterInfo")
	public final Object[][] getbasicfilterInfo() {
		return new Object[][] { 
				{"filter_name1", initial_direct_email_full, initial_direct_orgID, "protect", uuid1, "online", "finished", uuid1, "windows", "SQL_SERVER", uuid1,"sourceName","machine", "true"}, 
				{"filter_name2", initial_direct_email_full, initial_direct_orgID, "protect,unprotect", uuid1, "online,offline", "finished,active", uuid1, "windows,linux", "SQL_SERVER,EXCHANGE,All",uuid1,"sourceName", "machine", "true"},
				{"filter_name3", initial_sub_email_full_a, initial_sub_orgID_a, "protect", uuid1,"online", "finished", uuid1, "windows", "SQL_SERVER",uuid1,"sourceName", "machine", "true"},
				{"filter_name4", initial_sub_email_full_a, initial_sub_orgID_a, "protect,unprotect", uuid1, "online,offline", "finished,active", uuid1, "windows,linux", "SQL_SERVER,EXCHANGE,All",uuid1,"sourceName", "machine", "true"},
				{"filter_name5", initial_direct_email_full, initial_direct_orgID, "protect", uuid1 + "," + uuid2, "online", "finished", uuid1 + "," + uuid2, "windows", "SQL_SERVER",uuid1,"sourceName", "machine", "true"}, 
				{"filter_name6", initial_direct_email_full, initial_direct_orgID, "protect,unprotect", uuid1 + "," + uuid2, "online,offline", "finished,active", uuid1 + "," + uuid2, "windows,linux", "SQL_SERVER,EXCHANGE,All", uuid1,"sourceName","machine", "true"},
				{"filter_name7", initial_sub_email_full_a, initial_sub_orgID_a, "protect", uuid1 + "," + uuid2,"online", "finished", uuid1 + "," + uuid2, "windows", "SQL_SERVER", uuid1,"sourceName","machine", "true"},
				{"filter_name8", initial_sub_email_full_a, initial_sub_orgID_a, "protect,unprotect", uuid1 + "," + uuid2, "online,offline", "finished,active", uuid1 + "," + uuid2, "windows,linux", "SQL_SERVER,EXCHANGE,All",uuid1,"sourceName", "machine", "true"},
				{"filter_name9", initial_msp_email_full, initial_msp_orgID, "protect", uuid1, "online", "finished", uuid1, "windows", "SQL_SERVER", uuid1,"sourceName","machine", "true"}, 
				{"filter_name10", initial_msp_email_full, initial_msp_orgID, "protect,unprotect", uuid1, "online,offline", "finished,active", uuid1, "windows,linux", "SQL_SERVER,EXCHANGE,All", uuid1,"sourceName","machine", "true"},
				{"filter_name11", initial_msp_email_full, initial_msp_orgID, "protect", uuid1 + "," + uuid2, "online", "finished", uuid1 + "," + uuid2, "windows", "SQL_SERVER", uuid1,"sourceName","machine", "true"}, 
				{"filter_name12", initial_msp_email_full, initial_msp_orgID, "protect,unprotect", uuid1 + "," + uuid2, "online,offline", "finished,active", uuid1 + "," + uuid2, "windows,linux", "SQL_SERVER,EXCHANGE,All", uuid1,"sourceName","machine", "true"},
				{"filter_name12_a", initial_msp_email_full, initial_msp_orgID, "protect,unprotect", uuid1 + "," + uuid2, "online,offline", "finished,active,canceled", uuid1 + "," + uuid2, "windows,linux,unknown", "SQL_SERVER,EXCHANGE,All", uuid1,"sourceName","machine", "true"},
				{"filter_name13", initial_msp_email_full, initial_msp_orgID, "protect,unprotect", uuid1 + "," + uuid2, "online,offline", "finished,active", uuid1 + "," + uuid2, "windows,linux", null, uuid1,"sourceName","machine", "true"},
				{"filter_name14", initial_msp_email_full, initial_msp_orgID, null, null, null, null, null, null, null,uuid1,"sourceName", "machine", "true"},
				{"filter_name15", initial_msp_email_full, initial_msp_orgID, "emptyarray", "emptyarray", "emptyarray", "emptyarray", "emptyarray", "emptyarray", "emptyarray", uuid1,"sourceName","machine", "true"},
				{"filter_name16", initial_direct_email_full, initial_direct_orgID, "protect", uuid1 + "," + uuid2, "online", "finished", uuid1 + "," + uuid2, "windows", "SQL_SERVER", uuid1,"sourceName","machine", "true"},
				{"filter_name17", initial_direct_email_full, initial_direct_orgID, "protect", uuid1 + "," + uuid2, "online", "finished", uuid1 + "," + uuid2, "windows", "SQL_SERVER", uuid1,"sourceName","all", "true"},
				{"filter_name18", initial_direct_email_full, initial_direct_orgID, "protect", uuid1 + "," + uuid2, "online", "finished", uuid1 + "," + uuid2, "windows", "SQL_SERVER", uuid1,"sourceName","virtual_standby", "true"},
				{"filter_name19", initial_direct_email_full, initial_direct_orgID, "protect", uuid1 + "," + uuid2, "online", "finished", uuid1 + "," + uuid2, "windows", "SQL_SERVER", uuid1,"sourceName","instant_vm", "true"},
				{"filter_name20", initial_direct_email_full, initial_direct_orgID, "protect", uuid1 + "," + uuid2, "online", "finished", uuid1 + "," + uuid2, "windows", "SQL_SERVER", uuid1,"sourceName","shared_folder", "true"},
				{"filter_name21", initial_direct_email_full, initial_direct_orgID, "protect", uuid1 + "," + uuid2, "online", "finished", uuid1 + "," + uuid2, "windows", "SQL_SERVER", uuid1,"sourceName","office_365", "true"},
				{"filter_name22", initial_direct_email_full, initial_direct_orgID, "protect", uuid1 + "," + uuid2, "online", "finished", uuid1 + "," + uuid2, "windows", "SQL_SERVER", uuid1,"sourceName","machine", "false"},
				{"filter_name23", initial_direct_email_full, initial_direct_orgID, "protect", uuid1 + "," + uuid2, "online", "finished", uuid1 + "," + uuid2, "windows", "SQL_SERVER", uuid1,"sourceName","machine", ""},
				{"filter_name24", initial_direct_email_full, initial_direct_orgID, "protect", uuid1 + "," + uuid2, "online", "finished", uuid1 + "," + uuid2, "windows", "SQL_SERVER", uuid1,"sourceName","machine", null},
				{"filter_name25", initial_direct_email_full, initial_direct_orgID, "protect", uuid1 + "," + uuid2, "online", "finished", uuid1 + "," + uuid2, "windows", "SQL_SERVER", uuid1,"sourceName","machine", "none"},
				// different site_id format
				{"filter_name26", initial_direct_email_full, initial_direct_orgID, "protect", uuid1 + "," + uuid2, "online", "finished", uuid1 + "," + uuid2, "windows", "SQL_SERVER", uuid1 + "," + uuid2 ,"sourceName","machine", "none"},
				{"filter_name27", initial_direct_email_full, initial_direct_orgID, "protect", uuid1 + "," + uuid2, "online", "finished", uuid1 + "," + uuid2, "windows", "SQL_SERVER", "emptyarray","sourceName","machine", "none"},
				{"filter_name28", initial_direct_email_full, initial_direct_orgID, "protect", uuid1 + "," + uuid2, "online", "finished", uuid1 + "," + uuid2, "windows", "SQL_SERVER", "none","sourceName","machine", "none"},
				{"filter_name29", initial_direct_email_full, initial_direct_orgID, "protect", uuid1 + "," + uuid2, "online", "finished", uuid1 + "," + uuid2, "windows", "SQL_SERVER", null,"sourceName","machine", "none"},

				// different source_name format
				{"filter_name30", initial_direct_email_full, initial_direct_orgID, "protect", uuid1 + "," + uuid2, "online", "finished", uuid1 + "," + uuid2, "windows", "SQL_SERVER", uuid1,"","machine", "none"},
				{"filter_name31", initial_direct_email_full, initial_direct_orgID, "protect", uuid1 + "," + uuid2, "online", "finished", uuid1 + "," + uuid2, "windows", "SQL_SERVER", uuid1,"none","machine", "none"},
				{"filter_name32", initial_direct_email_full, initial_direct_orgID, "protect", uuid1 + "," + uuid2, "online", "finished", uuid1 + "," + uuid2, "windows", "SQL_SERVER", uuid1,null,"machine", "none"},
				//{"filter_name16", initial_msp_email_full, initial_msp_orgID, "", null, "", "", null, "", ""},
		};
	}
	
	@Test(dataProvider = "basicfilterInfo")
	public void validfilterTest(String filter_name, String userName, String organization_id, String protection_status, String protection_policy, 
			String connection_status, String backup_status, String source_group, String operating_system, String applications, String site_id, String source_name, String source_type, String is_default) {
		
		spogServer.userLogin(userName, password);
		String user_id = spogServer.GetLoggedinUser_UserID();
		
		
		String filter_id = spogServer.createFilterwithCheck(user_id, filter_name, protection_status, 
				connection_status, protection_policy, backup_status, source_group, operating_system, applications, site_id, source_name, source_type, is_default, test);
		
		Response response = spogServer.getFilterByID(user_id, filter_id);
		Boolean is_default_value = false;
		if(is_default==null) {
			is_default_value = false;
		} else if(is_default.equalsIgnoreCase("true")) {
			is_default_value = true;
		}
		compareFilter(filter_id, filter_name, user_id, organization_id, protection_status, connection_status,
				protection_policy, backup_status, source_group, operating_system, applications, site_id, source_name, source_type, is_default_value, response);
	}
	
	/**
	 * 2018-01-04
	 * there is some change during development, that user can get/update filters for other users in same organizations, so remove the invalid params. 
	 * @return
	 */
	@DataProvider(name = "getfilterInfo_autherror")
	public final Object[][] getfilterInfo_autherror() {
		return new Object[][] { 
		
				//sub admin cannot get filter of direct admin;
				{initial_direct_email_full, initial_sub_email_full_a, password, 403},
				//direct admin cannot get filter of other user in same org;
				{initial_direct_email_full, initial_direct_email_full_added, password, 200},
				//direct admin cannot get filter of sub organization;
				{initial_sub_email_full_a, initial_direct_email_full, password, 403},
				//sub admin cannot get filter of other user in same org;
				{initial_sub_email_full_a, initial_sub_email_full_a_added, password, 200},
				//msp admin cannot get sub admin's filter;
				{initial_sub_email_full_a, initial_msp_email_full, password, 200},
				//msp admin cannot get direct admin's filter;
				{initial_direct_email_full, initial_msp_email_full, password, 403}, 
				//csr admin cannot get direct admin's filter;
				{initial_direct_email_full, this.csrAdmin, this.csrPwd, 200},
				//csr admin cannot get sub admin's filter;
				{initial_sub_email_full_a, this.csrAdmin, this.csrPwd, 200},
				//csr admin cannot get msp admin's filter;
				{initial_msp_email_full, this.csrAdmin, this.csrPwd, 200},
				//sub org admin cannot get msp admin's filter;
				{initial_msp_email_full, initial_sub_email_full_a, password, 403},
				//msp admin cannot get filter of other user in same org;
				{initial_msp_email_full, initial_msp_email_full_added, password, 200},
				//direct cannot get msp admin's filter;
				{initial_msp_email_full, initial_direct_email_full, password, 403},
				};
	}
	
	@Test(dataProvider = "getfilterInfo_autherror")
	public void getfilter_autherror(String create_username, String get_username, String get_password, int status_code) {
		spogServer.userLogin(create_username, password);
		String user_id = spogServer.GetLoggedinUser_UserID();
		String filter_id = spogServer.createFilterwithCheck(user_id, "filter_name"+RandomStringUtils.randomAlphanumeric(8), "protect", 
				"online", uuid1, "finished", uuid1, "windows", test);
		
		spogServer.userLogin(get_username, get_password);
		spogServer.getFilterByIDWithExpectedCode(user_id, filter_id, status_code);
	}
	
	@DataProvider(name = "getfilterInfo_notoken")
	public final Object[][] getfilterInfo_notoken() {
		return new Object[][] { 
				{initial_direct_email_full, 401},
				{initial_msp_email_full, 401},
				{initial_sub_email_full_a, 401}};
	}
	
	@Test(dataProvider = "getfilterInfo_notoken")
	public void getfilter_notoken(String user_name, int status_code) {
		spogServer.userLogin(user_name, password);
		String user_id = spogServer.GetLoggedinUser_UserID();
		String filter_id = spogServer.createFilterwithCheck(user_id, "filter_name"+RandomStringUtils.randomAlphanumeric(8), "protect", 
				"online", uuid1, "finished", uuid1, "windows", test);
		
		spogServer.setToken("");
		spogServer.getFilterByIDWithExpectedCode(user_id, filter_id, status_code);
	}
	
	@DataProvider(name = "getfilterInfo_invalidfilterID")
	public final Object[][] getfilterInfo_invalidfilterID() {
		return new Object[][] { 
				{initial_direct_email_full, 404},
				{initial_msp_email_full, 404},
				{initial_sub_email_full_a, 404}};
	}
	@Test(dataProvider = "getfilterInfo_invalidfilterID")
	public void getfilter_invalidFilterID(String user_name, int status_code) {
		spogServer.userLogin(user_name, password);
		String user_id = spogServer.GetLoggedinUser_UserID();
		spogServer.createFilterwithCheck(user_id, "filter_name", "protect", 
				"online", uuid1, "finished", uuid1, "windows", test);
		
		spogServer.getFilterByIDWithExpectedCode(user_id, uuid1, status_code);
	}
	
	@DataProvider(name = "getUpdated_filter")
	public final Object[][] getUpdatedFilterParams() {
		return new Object[][] {
				{"msp", "filter_name", "protect","online",uuid1,"finished",uuid2,"windows","SQL_SERVER",uuid1,"sourceName","machine","false","true"},
				{"direct", "filter_name", "protect","online",uuid1,"finished",uuid2,"windows","SQL_SERVER",uuid1,"sourceName","machine","false","true"},
				{"msp", "filter_name", "protect,unprotect","online,offline",uuid1,"finished",uuid2,"windows,linux","SQL_SERVER",uuid1,"sourceName","shared_folder","true","false"},
				{"direct", "filter_name", "protect,unprotect","online,offline",uuid1,"finished",uuid2,"windows,linux","SQL_SERVER",uuid1,"sourceName","instant_vm","true","false"},
				
		};
	}
	@Test(enabled = false, dataProvider = "getUpdated_filter")
	public void getUpdatedFilter(String orgType, String filter_name, String protection_status, String connection_status, 
			String protection_policy, String backup_status, String source_group, String operating_system, String applications, String site_id, String source_name, String source_type, String is_default_old, String is_default_new) {
		spogServer.userLogin(csrAdmin, csrPwd);
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		spogServer.CreateOrganizationWithCheck(prefix + "orgName", orgType, prefix + "spog_qa@arcserve.com", 
				password, prefix + "spog_qa_first_name", prefix + "spog_qa_last_name", test);
		spogServer.userLogin(prefix + "spog_qa@arcserve.com", password);
		
		String user_id = spogServer.GetLoggedinUser_UserID();
		String org_id = spogServer.GetLoggedinUserOrganizationID();
		String filter_id = spogServer.createFilterwithCheck(user_id, filter_name, protection_status, connection_status, uuid1, 
				backup_status, uuid2, operating_system, applications, site_id, source_name, source_type, is_default_old, test);
		
		//spogServer.updateFilterwithCheck(user_id, filter_id, "", protection_status, connection_status, protection_policy, backup_status, source_group, operating_system, applications, source_type, is_default_new, test);
		
		Boolean is_default_value = false;
		if(is_default_new==null) {
			is_default_value = false;
		} else if(is_default_new.equalsIgnoreCase("true")) {
			is_default_value = true;
		}
		
		Response response = spogServer.getFilterByID(user_id, filter_id);
		compareFilter(filter_id, filter_name, user_id, org_id, protection_status, connection_status, protection_policy, backup_status, 
				source_group, operating_system, applications, site_id, source_name, source_type, is_default_value, response);
	}
	
	@DataProvider(name = "get_first_filter_after_add_another")
	public final Object[][] getFirstFilterAfterCreateAnotherParams() {
		return new Object[][] {
				// same source_type, create 2 default filters, the 1st one is changed to non-default
				{"msp", "filter_name", "protect", "online", uuid1, "finished", uuid2, "windows", "SQL_SERVER", "machine", "machine", "true", "true", false},				
				// same source_type, create 1 default filter, 1 non-default filter, the 1st one is not changed;
				{"msp", "filter_name", "protect", "online", uuid1, "finished", uuid2, "windows", "SQL_SERVER", "machine", "machine", "true", "false", true},
				// create default filter in one source_type, and another default filter in another source_type, the 1st one is not changed;
				{"msp", "filter_name", "protect", "online", uuid1, "finished", uuid2, "windows", "SQL_SERVER", "machine", "all", "true", "true", true},
				// create default filter in one source_type, and another non-default filter in another source_type, the 1st one is not changed;
				{"msp", "filter_name", "protect", "online", uuid1, "finished", uuid2, "windows", "SQL_SERVER", "machine", "all", "true", "false", true},
				// create 2 non-default filters in same source_type, the 1st one is not changed;
				{"msp", "filter_name", "protect", "online", uuid1, "finished", uuid2, "windows", "SQL_SERVER", "machine", "machine", "false", "false", false},
				// create 1 non-default filter, and 1 default filter, the 1st one is not changed;
				{"msp", "filter_name", "protect", "online", uuid1, "finished", uuid2, "windows", "SQL_SERVER", "machine", "machine", "false", "true", false},
				// same source_type, create 2 default filters, the 1st one is changed to non-default
				{"direct", "filter_name", "protect", "online", uuid1, "finished", uuid2, "windows", "SQL_SERVER", "machine", "machine", "true", "true", false},	
				// same source_type, create 1 default filter, 1 non-default filter, the 1st one is not changed;
				{"direct", "filter_name", "protect", "online", uuid1, "finished", uuid2, "windows", "SQL_SERVER", "machine", "machine", "true", "false", true},
				// create default filter in one source_type, and another default filter in another source_type, the 1st one is not changed;
				{"direct", "filter_name", "protect", "online", uuid1, "finished", uuid2, "windows", "SQL_SERVER", "machine", "all", "true", "true", true},
				// create default filter in one source_type, and another non-default filter in another source_type, the 1st one is not changed;
				{"direct", "filter_name", "protect", "online", uuid1, "finished", uuid2, "windows", "SQL_SERVER", "machine", "all", "true", "false", true},
				// create 2 non-default filters in same source_type, the 1st one is not changed;
				{"direct", "filter_name", "protect", "online", uuid1, "finished", uuid2, "windows", "SQL_SERVER", "machine", "machine", "false", "false", false},
				// create 1 non-default filter, and 1 default filter, the 1st one is not changed;
				{"direct", "filter_name", "protect", "online", uuid1, "finished", uuid2, "windows", "SQL_SERVER", "machine", "machine", "false", "true", false},
		};
	}
	@Test(dataProvider = "get_first_filter_after_add_another")
	public void checkFilterAfterCreateAnother(String orgType, String filter_name, String protection_status, String connection_status, 
			String protection_policy, String backup_status, String source_group, String operating_system, String applications, 
			String source_type_first, String source_type_second, String is_default_first, String is_default_second, Boolean is_default_expected) {
		spogServer.userLogin(csrAdmin, csrPwd);
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		spogServer.CreateOrganizationWithCheck(prefix + "orgName", orgType, prefix + "spog_qa@arcserve.com", 
				password, prefix + "spog_qa_first_name", prefix + "spog_qa_last_name", test);
		spogServer.userLogin(prefix + "spog_qa@arcserve.com", password);
		
		String user_id = spogServer.GetLoggedinUser_UserID();
		
		String filter_id_1 = spogServer.createFilterwithCheck(user_id, prefix + filter_name + "1", protection_status, connection_status, uuid1, 
				backup_status, uuid2, operating_system, applications, source_type_first, is_default_first, test);
		
		String filter_id_2 = spogServer.createFilterwithCheck(user_id, prefix + filter_name + "2", protection_status, connection_status, uuid1, 
				backup_status, uuid2, operating_system, applications, source_type_second, is_default_second, test);
		
		Response response = spogServer.getFilterByID(user_id, filter_id_1);
		response.then().body("data.is_default", equalTo(is_default_expected));
		
	}
	
	@DataProvider(name = "get_first_filter_after_update_another")
	public final Object[][] getFirstFilterAfterUpdateAnotherParams() {
		return new Object[][] {
				{"msp", "filter_name", "protect", "online", uuid1, "finished", uuid2, "windows", "SQL_SERVER", "machine", "true", "false", "true", false},				
				{"msp", "filter_name", "protect", "online", uuid1, "finished", uuid2, "windows", "SQL_SERVER", "machine", "false", "false", "true", false},
				{"msp", "filter_name", "protect", "online", uuid1, "finished", uuid2, "windows", "SQL_SERVER", "machine", "false", "true", "false", false},
				{"direct", "filter_name", "protect", "online", uuid1, "finished", uuid2, "windows", "SQL_SERVER", "machine", "true", "false", "true", false},				
				{"direct", "filter_name", "protect", "online", uuid1, "finished", uuid2, "windows", "SQL_SERVER", "machine", "false", "false", "true", false},
				{"direct", "filter_name", "protect", "online", uuid1, "finished", uuid2, "windows", "SQL_SERVER", "machine", "false", "true", "false", false},
		};
	}
	@Test(dataProvider = "get_first_filter_after_update_another")
	public void checkFilterAfterUpdateAnother(String orgType, String filter_name, String protection_status, String connection_status, 
			String protection_policy, String backup_status, String source_group, String operating_system, String applications, 
			String source_type, String is_default_first, String is_default_second, String is_default_second_updated, Boolean is_default_expected) {
		spogServer.userLogin(csrAdmin, csrPwd);
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		spogServer.CreateOrganizationWithCheck(prefix + "orgName", orgType, prefix + "spog_qa@arcserve.com", 
				password, prefix + "spog_qa_first_name", prefix + "spog_qa_last_name", test);
		spogServer.userLogin(prefix + "spog_qa@arcserve.com", password);
		
		String user_id = spogServer.GetLoggedinUser_UserID();
		
		String filter_id_1 = spogServer.createFilterwithCheck(user_id, prefix + filter_name + "1", protection_status, connection_status, uuid1, 
				backup_status, uuid2, operating_system, applications, source_type, is_default_first, test);
		
		String filter_id_2 = spogServer.createFilterwithCheck(user_id, prefix + filter_name + "2", protection_status, connection_status, uuid1, 
				backup_status, uuid2, operating_system, applications, source_type, is_default_second, test);
		
		spogServer.updateFilterwithCheck(user_id, filter_id_2, prefix + filter_name + "2", protection_status, connection_status, protection_policy, backup_status, 
				source_group, operating_system, applications, source_type, source_type, is_default_second_updated, test);
		
		Response response = spogServer.getFilterByID(user_id, filter_id_1);
		response.then().body("data.is_default", equalTo(is_default_expected));
		
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
