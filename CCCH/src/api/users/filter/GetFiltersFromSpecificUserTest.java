package api.users.filter;

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
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;

import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import Constants.SpogConstants;

import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;

public class GetFiltersFromSpecificUserTest {

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
	private String initial_msp_userID = "";
	
	private String initial_msp_email_added = "spog_qa_msp_zhaoguo_added@arcserve.com";
	private String initial_msp_email_full_added = "";
	private String initial_msp_first_name_added = "spog_qa_msp_ma";
	private String initial_msp_last_name_added = "spog_qa_msp_zhaoguo";
	private String initial_msp_userID_added = "";
	
	private String initial_msp_org_name_2 = "spog_qa_msp_zhaoguo_2";
	private String initial_msp_email_2 = "spog_qa_msp_zhaoguo_2@arcserve.com";
	private String initial_msp_email_full_2 = "";
	private String initial_msp_first_name_2 = "spog_qa_msp_ma_2";
	private String initial_msp_last_name_2 = "spog_qa_msp_zhaoguo_2";
	private String initial_msp_userID_2 = "";
	
	private String initial_msp_email_2_added = "spog_qa_msp_zhaoguo_added_2@arcserve.com";
	private String initial_msp_email_full_2_added = "";
	private String initial_msp_first_name_2_added = "spog_qa_msp_ma_2";
	private String initial_msp_last_name_2_added = "spog_qa_msp_zhaoguo_2";
	private String initial_msp_userID_2_added = "";

	private String initial_direct_org_name = "spog_qa_direct_zhaoguo";
	private String initial_direct_email = "spog_qa_direct_zhaoguo@arcserve.com";
	private String initial_direct_email_full = "";
	private String initial_direct_first_name = "spog_qa_direct_ma";
	private String initial_direct_last_name = "spog_qa_direct_zhaoguo2";
	private String initial_direct_userID = "";
	
	private String initial_direct_email_pagination = "spog_qa_direct_zhaoguo_pag@arcserve.com";
	private String initial_direct_email_full_pagination = "";
	private String initial_direct_first_name_pagination = "spog_qa_direct_ma";
	private String initial_direct_last_name_pagination = "spog_qa_direct_zhaoguo2";
	private String initial_direct_userID_pagination = "";
	
	private String initial_direct_email_added = "spog_qa_direct_zhaoguo_added@arcserve.com";
	private String initial_direct_email_full_added = "";
	private String initial_direct_first_name_added = "spog_qa_direct_ma";
	private String initial_direct_last_name_added = "spog_qa_direct_zhaoguo2";
	private String initial_direct_userID_added = "";
	
	private String initial_direct_org_name_2 = "spog_qa_direct_zhaoguo";
	private String initial_direct_email_2 = "spog_qa_direct_zhaoguo_2@arcserve.com";
	private String initial_direct_email_full_2 = "";
	private String initial_direct_first_name_2 = "spog_qa_direct_ma";
	private String initial_direct_last_name_2 = "spog_qa_direct_zhaoguo2";
	private String initial_direct_userID_2 = "";
	
	private String initial_direct_email_2_added = "spog_qa_direct_zhaoguo_2_added@arcserve.com";
	private String initial_direct_email_full_2_added = "";
	private String initial_direct_first_name_2_added = "spog_qa_direct_ma";
	private String initial_direct_last_name_2_added = "spog_qa_direct_zhaoguo2_added";
	private String initial_direct_userID_2_added = "";
	
	private String initial_sub_org_name_a = "spog_qa_sub_zhaoguo_a";
	private String initial_sub_email_a = "spog_qa_sub_zhaoguo_a@arcserve.com";
	private String initial_sub_email_full_a = "";
	private String initial_sub_first_name_a = "spog_qa_sub_ma_a";
	private String initial_sub_last_name_a = "spog_qa_sub_zhaoguo_a";
	private String initial_sub_userID_a = "";
	
	private String initial_sub_email_a_added = "spog_qa_sub_zhaoguo_a_added@arcserve.com";
	private String initial_sub_email_full_a_added = "";
	private String initial_sub_first_name_a_added = "spog_qa_sub_ma_a";
	private String initial_sub_last_name_a_added = "spog_qa_sub_zhaoguo_a";
	private String initial_sub_userID_a_added = "";
	
	private String initial_sub_org_name_b = "spog_qa_sub_zhaoguo_b";
	private String initial_sub_email_b = "spog_qa_sub_zhaoguo_b@arcserve.com";
	private String initial_sub_email_full_b = "";
	private String initial_sub_first_name_b = "spog_qa_sub_ma_b";
	private String initial_sub_last_name_b = "spog_qa_sub_zhaoguo_b";
	private String initial_sub_userID_b = "";
	
	private String initial_sub_email_b_added = "spog_qa_sub_zhaoguo_b_added@arcserve.com";
	private String initial_sub_email_full_b_added = "";
	private String initial_sub_first_name_b_added = "spog_qa_sub_ma_b";
	private String initial_sub_last_name_b_added = "spog_qa_sub_zhaoguo_b";
	private String initial_sub_userID_b_added = "";

	private String initial_msp_orgID;
	private String initial_msp_orgID_2;
	private String initial_direct_orgID;
	private String initial_direct_orgID_2;
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
		this.csrAdmin = csrAdminUserName;
		this.csrPwd = csrAdminPassword;
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		String prefix = RandomStringUtils.randomAlphanumeric(8);

		//create msp #1
		this.initial_msp_email_full = prefix + this.initial_msp_email;
		this.initial_msp_orgID = spogServer.CreateOrganizationWithCheck(prefix + initial_msp_org_name,
				SpogConstants.MSP_ORG, prefix + initial_msp_email, password, prefix + initial_msp_first_name,
				prefix + initial_msp_last_name);
		spogServer.userLogin(initial_msp_email_full, password);
		this.initial_msp_userID = spogServer.GetLoggedinUser_UserID();
		
		//create msp #2
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		this.initial_msp_email_full_2 = prefix + this.initial_msp_email_2;
		this.initial_msp_orgID_2 = spogServer.CreateOrganizationWithCheck(prefix + initial_msp_org_name_2,
				SpogConstants.MSP_ORG, prefix + initial_msp_email_2, password, prefix + initial_msp_first_name_2,
				prefix + initial_msp_last_name_2);
		spogServer.userLogin(initial_msp_email_full_2, password);
		this.initial_msp_userID_2 = spogServer.GetLoggedinUser_UserID();
		
		//add user to msp #1
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		this.initial_msp_email_full_added = prefix + this.initial_msp_email_added;
		this.initial_msp_userID_added = spogServer.createUserAndCheck(initial_msp_email_full_added, password, prefix+ initial_msp_first_name_added, 
				prefix+ initial_msp_last_name_added, "msp_admin", initial_msp_orgID, test);
		
		//add user to msp #2
		this.initial_msp_email_full_2_added = prefix + this.initial_msp_email_2_added;
		this.initial_msp_userID_2_added = spogServer.createUserAndCheck(initial_msp_email_full_2_added, password, prefix+ initial_msp_first_name_2_added, 
				prefix+ initial_msp_last_name_2_added, "msp_admin", initial_msp_orgID, test);
		
		//create 2 sub organization in msp #1, sub_org_a and sub_org_b
		this.initial_sub_orgID_a = spogServer.createAccountWithCheck(initial_msp_orgID, initial_sub_org_name_a,
				initial_msp_orgID);
		this.initial_sub_orgID_b = spogServer.createAccountWithCheck(initial_msp_orgID, initial_sub_org_name_b,
				initial_msp_orgID);
		
		//create 2 users in sub_org_a
		spogServer.userLogin(initial_msp_email_full, password);
		this.initial_sub_email_full_a = prefix + initial_sub_email_a;
		this.initial_sub_userID_a = spogServer.createUserAndCheck(prefix + this.initial_sub_email_a, password,
				prefix + this.initial_sub_first_name_a, prefix + this.initial_sub_last_name_a,
				SpogConstants.DIRECT_ADMIN, initial_sub_orgID_a, test);
		
		this.initial_sub_email_full_a_added = prefix + this.initial_sub_email_a_added;
		this.initial_sub_userID_a_added = spogServer.createUserAndCheck(prefix + this.initial_sub_email_a_added, password,
				prefix + this.initial_sub_first_name_a_added, prefix + this.initial_sub_last_name_a_added,
				SpogConstants.DIRECT_ADMIN, initial_sub_orgID_a, test);
		
		//create 2 users in sub_org_b
		this.initial_sub_email_full_b = prefix + initial_sub_email_b;
		this.initial_sub_userID_b = spogServer.createUserAndCheck(prefix + this.initial_sub_email_b, password,
				prefix + this.initial_sub_first_name_b, prefix + this.initial_sub_last_name_b,
				SpogConstants.DIRECT_ADMIN, initial_sub_orgID_b, test);
		
		this.initial_sub_email_full_b_added = prefix + this.initial_sub_email_b_added;
		this.initial_sub_userID_b_added = spogServer.createUserAndCheck(prefix + this.initial_sub_email_b_added, password,
				prefix + this.initial_sub_first_name_b_added, prefix + this.initial_sub_last_name_b_added,
				SpogConstants.DIRECT_ADMIN, initial_sub_orgID_b, test);

		// create direct organizations #1, and then create another user in it;
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		this.initial_direct_email_full = prefix + this.initial_direct_email;
		this.initial_direct_orgID = spogServer.CreateOrganizationWithCheck(prefix + initial_direct_org_name,
				SpogConstants.DIRECT_ORG, prefix + initial_direct_email, password, prefix + initial_direct_first_name,
				prefix + initial_direct_last_name);
		spogServer.userLogin(initial_direct_email_full, password);
		this.initial_direct_userID = spogServer.GetLoggedinUser_UserID();
		
		spogServer.userLogin(this.initial_direct_email_full, password);
		this.initial_direct_email_full_added = prefix + this.initial_direct_email_added;
		this.initial_direct_userID_added = spogServer.createUserAndCheck(initial_direct_email_full_added, password, prefix+ initial_direct_first_name_added, 
				prefix+ initial_direct_last_name_added, "direct_admin", initial_direct_orgID, test);
		
		
		// create direct organizations #2, and then create another user in it;
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		this.initial_direct_email_full_2 = prefix + this.initial_direct_email_2;
		this.initial_direct_orgID_2 = spogServer.CreateOrganizationWithCheck(prefix + initial_direct_org_name_2,
				SpogConstants.DIRECT_ORG, prefix + initial_direct_email_2, password, prefix + initial_direct_first_name_2,
				prefix + initial_direct_last_name_2);
		spogServer.userLogin(initial_direct_email_full_2, password);
		this.initial_direct_userID_2 = spogServer.GetLoggedinUser_UserID();

		spogServer.userLogin(this.initial_direct_email_full_2, password);
		this.initial_direct_email_full_2_added = prefix + this.initial_direct_email_2_added;
		this.initial_direct_userID_2_added = spogServer.createUserAndCheck(initial_direct_email_full_2_added, password,
				prefix + initial_direct_first_name_2_added, prefix + initial_direct_last_name_2_added, "direct_admin",
				initial_direct_orgID_2, test);
		
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		this.initial_direct_email_full_pagination = prefix + this.initial_direct_email_pagination;
		this.initial_direct_userID_pagination = spogServer.createUserAndCheck(initial_direct_email_full_pagination, password,
				prefix + initial_direct_first_name_pagination, prefix + initial_direct_last_name_pagination, "direct_admin",
				initial_direct_orgID, test);
		spogServer.userLogin(this.initial_direct_email_full_pagination, password);
		this.initial_direct_userID_pagination = spogServer.GetLoggedinUser_UserID();
		
		for (Integer i = 100; i <= 220; i ++) {
			spogServer.createFilterwithCheck(initial_direct_userID_pagination, "filter_name" + i.toString(), 
					"protect", "online", null, "finished", null, "linux,windows", test);
		}
	}
	
	@DataProvider(name = "basicfiltersInfo")
	public final Object[][] getvalidFiltersInfo() {
		return new Object[][] { 
				{"direct", "filter1,filter2", "protect,unprotect", "online", uuid1, "finished", uuid1, "windows", "SQL_SERVER", uuid1, "sourceName", "machine", "true"},
				{"msp", "filter1,filter2", "protect,unprotect", "online", uuid1, "finished", uuid1, "windows", "SQL_SERVER", uuid1, "sourceName", "machine", "true"},
				{"direct", "filter3", "protect,unprotect", "online", uuid1, "finished", uuid1, "windows", "SQL_SERVER", uuid1, "sourceName", "machine", "true"},
				{"msp", "filter3", "protect,unprotect", "online", uuid1, "finished", uuid1, "windows", "SQL_SERVER", uuid1, "sourceName", "machine", "true"}, 
				{"direct", "filter4", "protect,unprotect", "online,offline", uuid1+","+uuid2, "active,finished", uuid1+","+uuid2, "linux,windows", "EXCHANGE,SQL_SERVER,All", uuid1, "sourceName", "machine", "true"},
				{"msp", "filter4", "protect,unprotect", "online,offline", uuid1+","+uuid2, "active,finished", uuid1+","+uuid2, "linux,windows", "EXCHANGE,SQL_SERVER,All", uuid1, "sourceName", "machine", "true"}};
					}
	//direct & msp admin can get single filter or multiple filters
	@SuppressWarnings({ "unlikely-arg-type", "unchecked" })
	@Test(dataProvider = "basicfiltersInfo")
	public void validfilterTest(String orgType, String filterName, String protection_status, String connection_status, String protection_policy, 
			String backup_status, String source_group, String operating_system, String applications, String site_id, String source_name, String source_type, String is_default) {
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		String orgID = spogServer.CreateOrganizationWithCheck(prefix + "orgName",
				orgType, prefix + "spog_qa_filteremail@arcserve.com", password, prefix + "filter_firstname",
				prefix + "filter_lastname");
		
		spogServer.userLogin(prefix + "spog_qa_filteremail@arcserve.com", password);
		String user_id = spogServer.GetLoggedinUser_UserID();
		String[] filter_names = filterName.split(",");
		for (String filter_name : filter_names) {
			spogServer.createFilterwithCheck(user_id,  filter_name, protection_status, connection_status, protection_policy, 
					backup_status, source_group, operating_system, applications, site_id, source_name, source_type, is_default, test);
		}
		
		Response response = spogServer.getFiltersByUserID(user_id);
		
		ArrayList<HashMap<String, Object>> response_filters = response.then().extract().path("data");
		
		if (filter_names.length == response_filters.size()) {
		      assertTrue("compare filters size passed", true);
		    } else {
		      assertTrue("compare filters size failed", false);
		    }
		
		for (Integer i = 0; i < filter_names.length; i++) {
			System.out.println("comparing filter: " +  i.toString());
			String expected_filtername = filter_names[i];
			if (expected_filtername.equals(response_filters.get(i).get("filter_name"))) {
				assertTrue("compare filter_name passed", true);
			}
			else {
			      assertTrue("compare filter_name failed", false);
			    }
			
			ArrayList<String> response_protection_status = (ArrayList<String>) response_filters.get(i).get("protection_status");
			spogServer.assertFilterItem(protection_status, response_protection_status);
			
			ArrayList<String> response_connection_status = (ArrayList<String>) response_filters.get(i).get("connection_status");
			spogServer.assertFilterItem(connection_status, response_connection_status);
			
			ArrayList<String> response_protection_policy = (ArrayList<String>) response_filters.get(i).get("policy_id");
			spogServer.assertFilterItem(protection_policy, response_protection_policy);
			
			ArrayList<String> response_backup_status = (ArrayList<String>) response_filters.get(i).get("last_backup_status");
			spogServer.assertFilterItem(backup_status, response_backup_status);
			
			ArrayList<String> response_source_group = (ArrayList<String>) response_filters.get(i).get("group_id");
			spogServer.assertFilterItem(source_group, response_source_group);
			
			ArrayList<String> response_operating_system = (ArrayList<String>) response_filters.get(i).get("os_major");
			spogServer.assertFilterItem(operating_system, response_operating_system);
			
//			ArrayList<String> response_applications = (ArrayList<String>) response_filters.get(i).get("applications");
//			spogServer.assertFilterItem(applications, response_applications);
			
			ArrayList<String> response_site_id = (ArrayList<String>) response_filters.get(i).get("site_id");
			spogServer.assertFilterItem(site_id, response_site_id);
			
			String response_source_type = (String)response_filters.get(i).get("source_type");
			spogServer.assertResponseItem(source_type, response_source_type);
		}
	}
	
	
	@DataProvider(name = "subbasicfiltersInfo")
	public final Object[][] subgetvalidFiltersInfo() {
		return new Object[][] { 
				{"filter1,filter2", "protect,unprotect", "online", uuid1, "finished", uuid1, "windows", "SQL_SERVER", uuid1, "sourceName", "machine", "true"},
				{"filter3", "protect,unprotect", "online", uuid1, "finished", uuid1, "windows", "SQL_SERVER", uuid1, "sourceName", "machine", "true"},
				{"filter4", "protect,unprotect", "online,offline", uuid1+","+uuid2, "active,finished", uuid1+","+uuid2, "linux,windows", "EXCHANGE,SQL_SERVER,All", uuid1, "sourceName", "machine", "true"},};
					}
	//sub admin can get single filter or multiple filters
	@SuppressWarnings({ "unlikely-arg-type", "unchecked" })
	@Test(dataProvider = "subbasicfiltersInfo")
	public void subvalidfilterTest(String filterName, String protection_status, String connection_status, String protection_policy, 
			String backup_status, String source_group, String operating_system, String applications, String site_id, String source_name, String source_type, String is_default){
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		String orgID = spogServer.CreateOrganizationWithCheck(prefix + "orgName",
				"msp", prefix + "spog_qa_msp@arcserve.com", password, prefix + "filter_firstname",
				prefix + "filter_lastname");
		
		spogServer.userLogin(prefix + "spog_qa_msp@arcserve.com", password);
		String subOrgID = spogServer.createAccountWithCheck(orgID, prefix + "sub_orgName",orgID);
		spogServer.createUserAndCheck(prefix + "spog_qa_sub@arcserve.com", password,
				prefix + "filter_firstname", prefix + "filter_lastname",
				SpogConstants.DIRECT_ADMIN, subOrgID, test);
		
		spogServer.userLogin(prefix + "spog_qa_sub@arcserve.com", password);
				
		String user_id = spogServer.GetLoggedinUser_UserID();
		String[] filter_names = filterName.split(",");
		for (String filter_name : filter_names) {
			spogServer.createFilterwithCheck(user_id,  filter_name, protection_status, connection_status, protection_policy, 
					backup_status, source_group, operating_system, applications, site_id, source_name, source_type, is_default, test);
		}
		
		Response response = spogServer.getFiltersByUserID(user_id);
		
		ArrayList<HashMap<String, Object>> response_filters = response.then().extract().path("data");
		
		if (filter_names.length == response_filters.size()) {
		      assertTrue("compare filters size passed", true);
		    } else {
		      assertTrue("compare filters size failed", false);
		    }
		
		for (Integer i = 0; i < filter_names.length; i++) {
			System.out.println("comparing filter: " +  i.toString());
			String expected_filtername = filter_names[i];
			if (expected_filtername.equals(response_filters.get(i).get("filter_name"))) {
				assertTrue("compare filter_name passed", true);
			}
			else {
			      assertTrue("compare filter_name failed", false);
			    }
			
			ArrayList<String> response_protection_status = (ArrayList<String>) response_filters.get(i).get("protection_status");
			spogServer.assertFilterItem(protection_status, response_protection_status);
			
			ArrayList<String> response_connection_status = (ArrayList<String>) response_filters.get(i).get("connection_status");
			spogServer.assertFilterItem(connection_status, response_connection_status);
			
			ArrayList<String> response_protection_policy = (ArrayList<String>) response_filters.get(i).get("policy_id");
			spogServer.assertFilterItem(protection_policy, response_protection_policy);
			
			ArrayList<String> response_backup_status = (ArrayList<String>) response_filters.get(i).get("last_backup_status");
			spogServer.assertFilterItem(backup_status, response_backup_status);
			
			ArrayList<String> response_source_group = (ArrayList<String>) response_filters.get(i).get("group_id");
			spogServer.assertFilterItem(source_group, response_source_group);
			
			ArrayList<String> response_operating_system = (ArrayList<String>) response_filters.get(i).get("os_major");
			spogServer.assertFilterItem(operating_system, response_operating_system);
			
//			ArrayList<String> response_applications = (ArrayList<String>) response_filters.get(i).get("applications");
//			spogServer.assertFilterItem(applications, response_applications);
			
			ArrayList<String> response_site_id = (ArrayList<String>) response_filters.get(i).get("site_id");
			spogServer.assertFilterItem(site_id, response_site_id);
			
			String response_source_type = (String)response_filters.get(i).get("source_type");
			spogServer.assertResponseItem(source_type, response_source_type);
		}
	}
	/**
	 * 2018-01-04
	 * there is some change during development, that user can get/update filters for other users in same organizations, so remove the invalid params. 
	 * @return
	 */
	@DataProvider(name = "basicfiltersInfo_403")
	public final Object[][] getvalidFiltersInfo_403() {
		return new Object[][] {
				//msp admin cannot get filter for other msp admin;
				{initial_msp_userID, initial_msp_email_full_2, 403},
				//msp admin cannot get filter for other admin in same organization;
				{initial_msp_userID, initial_msp_email_full_added, 200},
				//msp admin cannot get filter for direct admin;
				{initial_direct_userID, initial_msp_email_full_added, 403},
				//msp admin cannot get filter for its sub organization admin;
				{initial_sub_userID_a, initial_msp_email_full_added, 200},
				//direct admin cannot get filter for other direct admin;
				{initial_direct_userID_2, initial_direct_email_full_added, 403},
				//direct admin cannot get filter for other admin in same organization;
				{initial_direct_userID, initial_direct_email_full_added, 200},
				//direct admin cannot get filter for msp admin;
				{initial_msp_userID, initial_direct_email_full, 403},
				//direct admin cannot get filter for sub org;
				{initial_sub_userID_a, initial_direct_email_full, 403},
				//sub org admin cannot get filter for msp admin;
				{initial_msp_userID, initial_sub_email_full_a, 403},
				//sub org admin cannot get filter for direct admin;
				{initial_direct_userID, initial_sub_email_full_a, 403},
				//sub org admin cannot get filter for other sub org admin; 
				{initial_sub_userID_a_added, initial_sub_email_full_a, 200},
				//sub org admin cannot get filter for admin in same orgrazation;
				{initial_sub_userID_b, initial_sub_email_full_a, 403},
				};}
	
	@SuppressWarnings({ "unlikely-arg-type", "unchecked" })
	@Test(dataProvider = "basicfiltersInfo_403")
	public void subvalidfilterTest_403(String user_id, String userName, int status_code) {
		spogServer.userLogin(userName, password);
		spogServer.getFiltersByUserIDWithExpectedcode(user_id, status_code);
	}
	
	@DataProvider(name = "basicfiltersInfo_noresult")
	public final Object[][] getvalidFiltersInfo_noresult() {
		return new Object[][] { 
				{"direct"},
				{"msp"},
				};}
	//msp, direct admin to get filters without result
	@SuppressWarnings({ "unlikely-arg-type", "unchecked" })
	@Test(dataProvider = "basicfiltersInfo_noresult")
	public void validfilterTest_noresult(String orgType) {
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		String orgID = spogServer.CreateOrganizationWithCheck(prefix + "orgName",
				orgType, prefix + "spog_qa_filter@arcserve.com", password, prefix + "filter_firstname",
				prefix + "filter_lastname");
		spogServer.userLogin(prefix + "spog_qa_filter@arcserve.com", password);
		String user_id = spogServer.GetLoggedinUser_UserID();
		Response response = spogServer.getFiltersByUserID(user_id);
		ArrayList<HashMap<String, Object>> response_filters = response.then().extract().path("data.data");
		
		if (response_filters.size()==0) {
			assertTrue("compare filter length passed", true);
		}else {
			assertTrue("compare filter length passed", false);
		}
	}
	
	//sub admin to get filters without result;
	@SuppressWarnings({ "unlikely-arg-type", "unchecked" })
	@Test
	public void validfilterTest_subnoresult() {
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		String orgID = spogServer.CreateOrganizationWithCheck(prefix + "orgName",
				SpogConstants.MSP_ORG, prefix + "spog_qa_msp@arcserve.com", password, prefix + "filter_firstname",
				prefix + "filter_lastname");
		spogServer.userLogin(prefix + "spog_qa_msp@arcserve.com", password);
		
		String subOrgID = spogServer.createAccountWithCheck(orgID, prefix + "sub_orgName",orgID);
		spogServer.createUserAndCheck(prefix + "spog_qa_sub@arcserve.com", password,
				prefix + "filter_firstname", prefix + "filter_lastname",
				SpogConstants.DIRECT_ADMIN, subOrgID, test);
		
		spogServer.userLogin(prefix + "spog_qa_sub@arcserve.com", password);
		
		String user_id = spogServer.GetLoggedinUser_UserID();
		Response response = spogServer.getFiltersByUserID(user_id);
		ArrayList<HashMap<String, Object>> response_filters = response.then().extract().path("data.data");
		
		if (response_filters.size()==0) {
			assertTrue("compare filter length passed", true);
		}else {
			assertTrue("compare filter length passed", false);
		}
	}
	
	@DataProvider(name = "invalidfiltersInfo_notoken")
	public final Object[][] getFiltersInfo_notoken() {
		return new Object[][] { 
				{initial_msp_userID, initial_msp_email_full, 401},
				{initial_direct_userID, initial_direct_email_full, 401},
				{initial_sub_userID_a, initial_sub_email_full_a, 401}};}
	
	@Test(dataProvider = "invalidfiltersInfo_notoken")
	public void getfilterTest_notoken(String user_id, String userName, int status_code) {
		spogServer.userLogin(userName, password);
		spogServer.setToken("");
		spogServer.getFiltersByUserIDWithExpectedcode(user_id, status_code);
	}
	
	@DataProvider(name = "invalidfiltersInfo_wronguserID")
	public final Object[][] getFiltersInfo_wronguserID() {
		return new Object[][] { 
				//input orgID as userID so that it is invalid;
				{initial_msp_orgID, initial_msp_email_full, 404},
				{initial_msp_orgID, initial_direct_email_full, 404},
				{initial_msp_orgID, initial_sub_email_full_a, 404}};}
	@Test(dataProvider = "invalidfiltersInfo_wronguserID")
	public void getfilterTest_wronguserID(String user_id, String userName, int status_code) {
		spogServer.userLogin(userName, password);
		spogServer.getFiltersByUserIDWithExpectedcode(user_id, status_code);
	}
	
	@DataProvider(name = "getfiltersInfo_pagination")
	public final Object[][] getfiltersInfo_pagination() {
		return new Object[][] { 
				//page=2&page_size=20				
				{2, 20, 20, 2, 7, 20, true, true, 121},
				//page=1&page_size=20
				{1, 20, 20, 1, 7, 20, false, true, 121},
				//page=7&page_size=20
				{7, 20, 1, 7, 7, 20, true, false, 121},
				//page=1&page_size=100
				{1, 100, 100, 1, 2, 100, false, true, 121},
				//page=2&page_size=default
				{2, -1, 20, 2, 7, 20, true, true, 121},
				//page=2&page_size=101 which is invalid, the page_size should be 100
				{1, 101, 100, 1, 2, 100, false, true, 121},
				//page=2&page_size=0 which is invalid, the page_size should be 20
				{2, 0, 20, 2, 7, 20, true, true, 121},
		};}
	
	@Test(dataProvider = "getfiltersInfo_pagination")
	public void getFilters_pagination(int curr_page, int page_size, int expected_length, int expected_curr_page, int expected_total_page, 
			int expected_page_size, boolean expected_has_prev, boolean expected_has_next, int expected_total_size) {
		
		spogServer.userLogin(this.initial_direct_email_full_pagination, password);
		// this.initial_direct_userID_pagination = spogServer.GetLoggedinUser_UserID();
		
		Response response = spogServer.getFiltersByUserID(initial_direct_userID_pagination, "", "", curr_page, page_size, test);
		ArrayList<HashMap<String, Object>> response_filters = response.then().extract().path("data");
		
		if (response_filters.size() == expected_length) {
			assertTrue("compare size passed", true);
		} else {
			assertTrue("compare size failed", false);
		}
		for (Integer i=0; i < expected_length; i++) {
			String actual_name = (String) response_filters.get(i).get("filter_name");
			
			Integer j = i + 100 + (curr_page-1) * 20;
			if (("filter_name"+j.toString()).equals(actual_name)) {
				assertTrue("compare name passed: " + j.toString(), true);
			} else {
				System.out.println(actual_name);
				System.out.println("filter_name"+j.toString());
				assertTrue("compare name failed: " + j.toString(), false);
			}
		}
		
		response.then().body("pagination.curr_page", equalTo(expected_curr_page)).body("pagination.total_page",equalTo(expected_total_page))
		.body("pagination.has_prev",equalTo(expected_has_prev)).body("pagination.page_size",equalTo(expected_page_size))
		.body("pagination.has_next",equalTo(expected_has_next)).body("pagination.total_size",equalTo(expected_total_size));
	}
	
	@DataProvider(name = "getfiltersInfo_sort_filter")
	public final Object[][] getfiltersInfo_sortFilter() {
		return new Object[][] { 
			{"filter_name;=;*filter*2??","filter_name;desc", 1, 20, 20, 1, 2, 20, false, true, 21},
		};}
	
	@Test(dataProvider = "getfiltersInfo_sort_filter")
	public void getFilters_sortFilter(String filter_str, String sort_str, 
			int curr_page, int page_size, int expected_length, int expected_curr_page, int expected_total_page, 
			int expected_page_size, boolean expected_has_prev, boolean expected_has_next, int expected_total_size) {
		spogServer.userLogin(this.initial_direct_email_full_pagination, password);
		// this.initial_direct_userID_pagination = spogServer.GetLoggedinUser_UserID();
		
		Response response = spogServer.getFiltersByUserID(initial_direct_userID_pagination, filter_str, sort_str, 
				curr_page, page_size, test);
		ArrayList<HashMap<String, Object>> response_filters = response.then().extract().path("data");
		
		if (response_filters.size() == expected_length) {
			assertTrue("compare size passed", true);
		} else {
			assertTrue("compare size failed", false);
		}
		for (Integer i=0; i < expected_length; i++) {
			String actual_name = (String) response_filters.get(i).get("filter_name");
			
			//Integer j = i + 200 + (curr_page-1) * 20;
			Integer j = 220 - i;
			if (("filter_name"+j.toString()).equals(actual_name)) {
				assertTrue("compare name passed: " + j.toString(), true);
			} else {
				System.out.println(actual_name);
				System.out.println("filter_name"+j.toString());
				assertTrue("compare name failed: " + j.toString(), false);
			}
		}
		
		response.then().body("pagination.curr_page", equalTo(expected_curr_page)).body("pagination.total_page",equalTo(expected_total_page))
		.body("pagination.has_prev",equalTo(expected_has_prev)).body("pagination.page_size",equalTo(expected_page_size))
		.body("pagination.has_next",equalTo(expected_has_next)).body("pagination.total_size",equalTo(expected_total_size));
	}
	
	@DataProvider(name = "getfiltersInfo_enhancement")
	public final Object[][] getfiltersInfo_enhancement() {
		return new Object[][] { 
			{"msp","filter_name;=;filterName1","", 1, 20, 5, "filterName1"},
			{"msp","is_default;=;1","", 1, 20, 1, "filterName5"},
			{"msp","source_type;=;shared_folder,is_default;=;1","", 1, 20, 1, "filterName3"},
			{"msp","source_type;=;office_365","", 1, 20, 1, "filterName8"},
			{"direct","filter_name;=;filterName1","", 1, 20, 5, "filterName1"},
			{"direct","is_default;=;1","", 1, 20, 1, "filterName5"},
			{"direct","source_type;=;shared_folder,is_default;=;1","", 1, 20, 1, "filterName3"},
			{"direct","source_type;=;office_365","", 1, 20, 1, "filterName8"},
		};}
	
	
	@Test (dataProvider = "getfiltersInfo_enhancement")
	public void getFilters_enhancement(String orgType, String filter_str, String sort_str, int curr_page, int page_size, int expected_size, String expected_filter_name) {
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		String orgID = spogServer.CreateOrganizationWithCheck(prefix + "orgName",
				orgType, prefix + "spog_qa_filteremail@arcserve.com", password, prefix + "filter_firstname",
				prefix + "filter_lastname");
		spogServer.userLogin(prefix + "spog_qa_filteremail@arcserve.com", password);
		String user_id = spogServer.GetLoggedinUser_UserID();
		String uuid1 = UUID.randomUUID().toString();
		String uuid2 = UUID.randomUUID().toString();
		
		String sourceFilterID_1 = spogServer.createFilterwithCheck(user_id,  "filterName1", "protect", "online", uuid1, "finished", 
				uuid1, "windows", "SQL_SERVER", uuid1, "sourceName1", "machine", "true", test);
		String sourceFilterID_2 = spogServer.createFilterwithCheck(user_id,  "filterName2", "unprotect", "online", uuid1, "finished", 
				uuid1, "windows", "SQL_SERVER", uuid1, "sourceName2", "machine", "false", test);
		String sourceFilterID_3 = spogServer.createFilterwithCheck(user_id,  "filterName3", "protect,unprotect", "offline", uuid1, "active", 
				uuid1, "linux", "EXCHANGE", uuid1, "sourceName3", "shared_folder", "true", test);
		String sourceFilterID_4 = spogServer.createFilterwithCheck(user_id,  "filterName4", "protect", "online", uuid1, "active", 
				uuid1, "linux", "SQL_SERVER", uuid1, "sourceName4", "shared_folder", "false", test);
		String sourceFilterID_5 = spogServer.createFilterwithCheck(user_id,  "filterName5", "protect", "online", uuid1, "finished", 
				uuid1, "windows", "All", uuid1, "sourceName5", "all", "true", test);
		String sourceFilterID_6 = spogServer.createFilterwithCheck(user_id,  "filterName6", "protect,unprotect", "online", uuid1, "active", 
				uuid1, "linux,windows", "All", uuid1, "sourceName6", "all", "false", test);
		String sourceFilterID_7 = spogServer.createFilterwithCheck(user_id,  "filterName7", "protect", "online,offline", uuid2, "finished", 
				uuid2, "windows", "SQL_SERVER", uuid1, "sourceName7", "machine", "false", test);
		String sourceFilterID_8 = spogServer.createFilterwithCheck(user_id,  "filterName8", "protect", "online", uuid1 + "," + uuid2, "finished,canceled", 
				uuid1 + "," + uuid2, "windows", "SQL_SERVER", uuid1, "sourceName8", "office_365", "false", test);
		String sourceFilterID_9 = spogServer.createFilterwithCheck(user_id,  "filterName9", "protect", "online", uuid1 + "," + uuid2, "finished", 
				uuid1 + "," + uuid2, "linux,windows", "EXCHANGE,SQL_SERVER", uuid1 + "," + uuid2, "sourceName9", "machine", "false", test);
		String sourceFilterID_10 = spogServer.createFilterwithCheck(user_id,  "filterName10", "protect", "online", uuid1 + "," + uuid2, "finished", 
				uuid1 + "," + uuid2, "linux,windows", "EXCHANGE,SQL_SERVER", uuid1 + "," + uuid2, "", "machine", "false", test);
		String sourceFilterID_11 = spogServer.createFilterwithCheck(user_id,  "filterName11", "protect", "online", uuid1 + "," + uuid2, "finished", 
				uuid1 + "," + uuid2, "linux,windows", "EXCHANGE,SQL_SERVER", uuid1, "none", "machine", "false", test);
		String sourceFilterID_12 = spogServer.createFilterwithCheck(user_id,  "filterName12", "protect", "online", uuid1 + "," + uuid2, "finished", 
				uuid1 + "," + uuid2, "linux,windows", "EXCHANGE,SQL_SERVER", "none", "sourceName12", "machine", "false", test);
		String sourceFilterID_13 = spogServer.createFilterwithCheck(user_id,  "filterName13", "protect", "online", uuid1 + "," + uuid2, "finished", 
				uuid1 + "," + uuid2, "linux,windows", "EXCHANGE,SQL_SERVER", "emptyarray", "sourceName13", "machine", "false", test);
		
		Response response = spogServer.getFiltersByUserID(user_id, filter_str, sort_str, 
				curr_page, page_size, test);
		
		ArrayList<String> filter_names = response.then().extract().path("data.filter_name");
		assertEquals(filter_names.size(), expected_size);
		assertEquals(filter_names.get(0), expected_filter_name);
		
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
