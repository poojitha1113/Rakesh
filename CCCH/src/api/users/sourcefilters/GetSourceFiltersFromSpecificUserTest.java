package api.users.sourcefilters;

import org.testng.annotations.Test;

import com.fasterxml.jackson.databind.deser.Deserializers.Base;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.SpogConstants;
import InvokerServer.Org4SPOGServer;
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

import static org.hamcrest.CoreMatchers.nullValue;
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
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;

public class GetSourceFiltersFromSpecificUserTest extends base.prepare.Is4Org {

	private SPOGServer spogServer;
	private UserSpogServer userSpogServer;
	
	private InitialTestDataImpl initialTestDataImpl;
	private InitialTestData itd;
	
	private String csrAdmin;
	private String csrPwd;

	private String csr_readonly_email = "csr_readonly_zhaoguo@arcserve.com";
	private String csr_readonly_password = "Caworld_2017";
	
	
	private String password = "Pa$$w0rd";
	private String  org_model_prefix=this.getClass().getSimpleName();

//	private ExtentReports rep;
	private ExtentTest test;
//	private SQLServerDb bqdb1;
//	public int Nooftest;
//	private long creationTime;
//	private String BQName = null;
//	private String runningMachine;
//	private testcasescount count1;
//	private String buildVersion;
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
		
		spogServer.userLogin(itd.getDirect_org_2_user_1_email(), password);
		
//		spogServer.createFilterwithCheck(user_id,  filter_name, protection_status, connection_status, protection_policy, 
//		backup_status, source_group, operating_system, applications, site_id, source_name, source_type, is_default, test);
		
		for (Integer i = 100; i <= 220; i ++) {
			spogServer.createFilterwithCheck(itd.getDirect_org_2_user_1(), "filter_name" + i.toString(), 
					"protect", "online", null, "finished", null, "linux,windows", null,  uuid1, null, null, "false", test);
		}
	}
	
	@Test
	public void mspAccountAdminTest() {
		spogServer.userLogin(itd.getMsp_org_1_account_admin_1_email(), password);
		// msp account admin get filters for its owned account
		spogServer.getFiltersByUserID(itd.getMsp_org_1_sub_1_user_1());
		// msp account admin cannot get filters for account not owned by it
		spogServer.getFiltersByUserIDWithExpectedcode(itd.getMsp_org_1_sub_2_user_1(), 403);
	}
	
	@DataProvider(name = "basicfiltersInfo")
	public final Object[][] getvalidFiltersInfo() {
		return new Object[][] { 
				{"direct", "filter1,filter2", "protect,unprotect", "online", uuid1, "finished", uuid1, "windows", "SQL_SERVER", uuid1, "sourceName", "", "true"},
				{"msp", "filter1,filter2", "protect,unprotect", "online", uuid1, "finished", uuid1, "windows", "SQL_SERVER", uuid1, "sourceName", "", "true"},
				{"direct", "filter3", "protect,unprotect", "online", uuid1, "finished", uuid1, "windows", "SQL_SERVER", uuid1, "sourceName", "", "true"},
				{"msp", "filter3", "protect,unprotect", "online", uuid1, "finished", uuid1, "windows", "SQL_SERVER", uuid1, "sourceName", "", "true"}, 
				{"direct", "filter4", "protect,unprotect", "online,offline", uuid1+","+uuid2, "active,finished", uuid1+","+uuid2, "linux,windows", "EXCHANGE,SQL_SERVER,All", uuid1, "sourceName", "", "true"},
				{"msp", "filter4", "protect,unprotect", "online,offline", uuid1+","+uuid2, "active,finished", uuid1+","+uuid2, "linux,windows", "EXCHANGE,SQL_SERVER,All", uuid1, "sourceName", "", "true"}};
					}
	//direct & msp admin can get single filter or multiple filters
	@SuppressWarnings({ "unlikely-arg-type", "unchecked" })
	@Test(dataProvider = "basicfiltersInfo")
	public void validfilterTest(String orgType, String filterName, String protection_status, String connection_status, String protection_policy, 
			String backup_status, String source_group, String operating_system, String applications, String site_id, String source_name, String source_type, String is_default) {
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		String orgID = spogServer.CreateOrganizationWithCheck(prefix + "orgName" + org_model_prefix,
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
			
			ArrayList<String> response_backup_status = (ArrayList<String>) response_filters.get(i).get("last_job");
			spogServer.assertFilterItem(backup_status, response_backup_status);
			
			ArrayList<String> response_source_group = (ArrayList<String>) response_filters.get(i).get("group_id");
			spogServer.assertFilterItem(source_group, response_source_group);
			
			ArrayList<String> response_operating_system = (ArrayList<String>) response_filters.get(i).get("operating_system");
			spogServer.assertFilterItem(operating_system, response_operating_system);
			
//			ArrayList<String> response_applications = (ArrayList<String>) response_filters.get(i).get("applications");
//			spogServer.assertFilterItem(applications, response_applications);
			
			ArrayList<String> response_site_id = (ArrayList<String>) response_filters.get(i).get("site_id");
			spogServer.assertFilterItem(site_id, response_site_id);
			
//			String response_source_type = (String)response_filters.get(i).get("source_type");
//			spogServer.assertResponseItem(source_type, response_source_type);
		}
		
//		spogServer.userLogin(csrAdmin, csrPwd);
//		org4spogServer.setToken(spogServer.getJWTToken());
//		org4spogServer.destroyOrganization(orgID);
	}
	
	
	@DataProvider(name = "subbasicfiltersInfo")
	public final Object[][] subgetvalidFiltersInfo() {
		return new Object[][] { 
				{"filter1,filter2", "protect,unprotect", "online", uuid1, "finished", uuid1, "windows", "SQL_SERVER", uuid1, "sourceName", "", "true"},
				{"filter3", "protect,unprotect", "online", uuid1, "finished", uuid1, "windows", "SQL_SERVER", uuid1, "sourceName", "", "true"},
				{"filter4", "protect,unprotect", "online,offline", uuid1+","+uuid2, "active,finished", uuid1+","+uuid2, "linux,windows", "EXCHANGE,SQL_SERVER,All", uuid1, "sourceName", "", "true"},};
					}
	//sub admin can get single filter or multiple filters
	@SuppressWarnings({ "unlikely-arg-type", "unchecked" })
	@Test(dataProvider = "subbasicfiltersInfo")
	public void subvalidfilterTest(String filterName, String protection_status, String connection_status, String protection_policy, 
			String backup_status, String source_group, String operating_system, String applications, String site_id, String source_name, String source_type, String is_default){
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		String orgID = spogServer.CreateOrganizationWithCheck(prefix + "orgName" + org_model_prefix,
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
			
			ArrayList<String> response_backup_status = (ArrayList<String>) response_filters.get(i).get("last_job");
			spogServer.assertFilterItem(backup_status, response_backup_status);
			
			ArrayList<String> response_source_group = (ArrayList<String>) response_filters.get(i).get("group_id");
			spogServer.assertFilterItem(source_group, response_source_group);
			
			ArrayList<String> response_operating_system = (ArrayList<String>) response_filters.get(i).get("operating_system");
			spogServer.assertFilterItem(operating_system, response_operating_system);
			
//			ArrayList<String> response_applications = (ArrayList<String>) response_filters.get(i).get("applications");
//			spogServer.assertFilterItem(applications, response_applications);
			
			ArrayList<String> response_site_id = (ArrayList<String>) response_filters.get(i).get("site_id");
			spogServer.assertFilterItem(site_id, response_site_id);
			
//			String response_source_type = (String)response_filters.get(i).get("source_type");
//			spogServer.assertResponseItem(source_type, response_source_type);
		}
		
//		spogServer.userLogin(csrAdmin, csrPwd);
//		org4spogServer.setToken(spogServer.getJWTToken());
//		org4spogServer.destroyOrganization(orgID);
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
				{itd.getMsp_org_1_user_1(), itd.getMsp_org_2_user_1_email(), 403},
				//msp admin cannot get filter for other admin in same organization;
				{itd.getMsp_org_1_user_1(), itd.getMsp_org_1_user_2_email(), 200},
				//msp admin cannot get filter for direct admin;
				{itd.getDirect_org_1_user_1(), itd.getMsp_org_1_user_1_email(), 403},
				//msp admin cannot get filter for its sub organization admin;
				{itd.getMsp_org_1_sub_1_user_1(), itd.getMsp_org_1_user_1_email(), 200},
				//direct admin cannot get filter for other direct admin;
				{itd.getDirect_org_1_user_1(), itd.getDirect_org_2_user_1_email(), 403},
				//direct admin cannot get filter for other admin in same organization;
				{itd.getDirect_org_1_user_1(), itd.getDirect_org_1_user_2_email(), 200},
				//direct admin cannot get filter for msp admin;
				{itd.getMsp_org_1_user_1(), itd.getDirect_org_1_user_1_email(), 403},
				//direct admin cannot get filter for sub org;
				{itd.getMsp_org_1_sub_1_user_1(), itd.getDirect_org_1_user_1_email(), 403},
				//sub org admin cannot get filter for msp admin;
				{itd.getMsp_org_1_user_1(), itd.getMsp_org_1_sub_1_user_1_email(), 403},
				//sub org admin cannot get filter for direct admin;
				{itd.getDirect_org_1_user_1(), itd.getMsp_org_1_sub_1_user_1_email(), 403},
				//sub org admin cannot get filter for other sub org admin; 
				{itd.getMsp_org_1_sub_1_user_1(), itd.getMsp_org_1_sub_1_user_2_email(), 200},
				//sub org admin cannot get filter for admin in same orgrazation;
				{itd.getMsp_org_1_sub_1_user_1(), itd.getMsp_org_1_sub_2_user_1_email(), 403},
				// root msp related
				{itd.getRoot_msp_org_1_user_1(), itd.getRoot_msp_org_1_user_1_email(), 200},
				{itd.getRoot_msp_org_1_sub_msp_1_user_1(), itd.getRoot_msp_org_1_user_1_email(), 403},
				{itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1(), itd.getRoot_msp_org_1_user_1_email(), 403},
				{itd.getRoot_msp_org_1_account_1_user_1(), itd.getRoot_msp_org_1_user_1_email(), 200}, 
				{itd.getRoot_msp_org_1_account_1_user_1(), itd.getRoot_msp_org_1_account_admin_1_email(), 200},
				{itd.getRoot_msp_org_1_account_1_user_1(), itd.getRoot_msp_org_1_account_admin_2_email(), 403},
				{itd.getRoot_msp_org_1_account_2_user_1(), itd.getRoot_msp_org_1_account_admin_1_email(), 403},
				
				{itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1(), itd.getRoot_msp_org_1_sub_msp_1_account_admin_1_email(), 200},
				{itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1(), itd.getRoot_msp_org_1_sub_msp_1_account_admin_2_email(), 403},
				
				{itd.getRoot_msp_org_1_account_1_user_1(), itd.getRoot_msp_org_1_account_1_user_2_email(), 200},
				{itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1(), itd.getRoot_msp_org_1_sub_msp_1_user_1_email(), 200},
				{itd.getRoot_msp_org_1_sub_msp_2_user_1(), itd.getRoot_msp_org_1_sub_msp_1_user_1_email(), 403},
				{itd.getRoot_msp_org_1_sub_msp_1_account_2_user_1(), itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1_email(), 403},
				{itd.getRoot_msp_org_1_sub_msp_2_account_1_user_1(), itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1_email(), 403},
				
				// monitor user related
				{itd.getRoot_msp_org_1_monitor_user_1(), itd.getRoot_msp_org_1_user_1_email(), 200},
				{itd.getRoot_msp_org_1_sub_msp_1_monitor_user_1(), itd.getRoot_msp_org_1_sub_msp_1_user_1_email(), 200},
				{itd.getRoot_msp_org_1_account_1_monitor_user_1(), itd.getRoot_msp_org_1_account_1_user_1_email(), 200},
				{itd.getMsp_org_1_monitor_user_1(), itd.getMsp_org_1_user_1_email(), 200},
				{itd.getDirect_org_1_monitor_user_1(), itd.getDirect_org_1_user_1_email(), 200},
				
				{itd.getRoot_msp_org_1_user_1(), itd.getRoot_msp_org_1_monitor_user_1_email(), 200},
				{itd.getRoot_msp_org_1_sub_msp_1_user_1(), itd.getRoot_msp_org_1_sub_msp_1_monitor_user_1_email(), 200},
				{itd.getRoot_msp_org_1_account_1_user_1(), itd.getRoot_msp_org_1_account_1_monitor_user_1_email(), 200},
				{itd.getMsp_org_1_user_1(), itd.getMsp_org_1_monitor_user_1_email(), 200},
				{itd.getDirect_org_1_user_1(), itd.getDirect_org_1_monitor_user_1_email(), 200},
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
		String orgID = spogServer.CreateOrganizationWithCheck(prefix + "orgName" +org_model_prefix,
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
		
//		spogServer.userLogin(csrAdmin, csrPwd);
//		org4spogServer.setToken(spogServer.getJWTToken());
//		org4spogServer.destroyOrganization(orgID);
		
	}
	
	//sub admin to get filters without result;
	@SuppressWarnings({ "unlikely-arg-type", "unchecked" })
	@Test
	public void validfilterTest_subnoresult() {
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		String orgID = spogServer.CreateOrganizationWithCheck(prefix + "orgName" +org_model_prefix,
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
		
//		spogServer.userLogin(csrAdmin, csrPwd);
//		org4spogServer.setToken(spogServer.getJWTToken());
//		org4spogServer.destroyOrganization(orgID);
	}
	
	@DataProvider(name = "invalidfiltersInfo_notoken")
	public final Object[][] getFiltersInfo_notoken() {
		return new Object[][] { 
				{itd.getMsp_org_1_user_1(), itd.getMsp_org_1_user_1_email(), 401},
				{itd.getDirect_org_1_user_1(), itd.getDirect_org_1_user_1_email(), 401},
				{itd.getMsp_org_1_sub_1_user_1(), itd.getMsp_org_1_sub_1_user_1_email(), 401}};}
	
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
				{itd.getMsp_org_1(), itd.getMsp_org_1_user_1_email(), 404},
				{itd.getMsp_org_1(), itd.getDirect_org_1_user_1_email(), 404},
				{itd.getMsp_org_1(), itd.getMsp_org_1_sub_1_user_1_email(), 404}};}
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
		
		spogServer.userLogin(itd.getDirect_org_2_user_1_email(), password);
		// this.initial_direct_userID_pagination = spogServer.GetLoggedinUser_UserID();
		
		Response response = spogServer.getFiltersByUserID(itd.getDirect_org_2_user_1(), "", "", curr_page, page_size, test);
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
		spogServer.userLogin(itd.getDirect_org_2_user_1_email(), password);
		// this.initial_direct_userID_pagination = spogServer.GetLoggedinUser_UserID();
		
		Response response = spogServer.getFiltersByUserID(itd.getDirect_org_2_user_1(), filter_str, sort_str, 
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
//			{"msp","source_type;=;shared_folder,is_default;=;1","", 1, 20, 1, "filterName3"},
//			{"msp","source_type;=;office_365","", 1, 20, 1, "filterName8"},
			{"direct","filter_name;=;filterName1","", 1, 20, 5, "filterName1"},
			{"direct","is_default;=;1","", 1, 20, 1, "filterName5"},
//			{"direct","source_type;=;shared_folder,is_default;=;1","", 1, 20, 1, "filterName3"},
//			{"direct","source_type;=;office_365","", 1, 20, 1, "filterName8"},
		};}
	
	
	@Test (dataProvider = "getfiltersInfo_enhancement")
	public void getFilters_enhancement(String orgType, String filter_str, String sort_str, int curr_page, int page_size, int expected_size, String expected_filter_name) {
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		String orgID = spogServer.CreateOrganizationWithCheck(prefix + "orgName" +org_model_prefix,
				orgType, prefix + "spog_qa_filteremail@arcserve.com", password, prefix + "filter_firstname",
				prefix + "filter_lastname");
		spogServer.userLogin(prefix + "spog_qa_filteremail@arcserve.com", password);
		String user_id = spogServer.GetLoggedinUser_UserID();
		String uuid1 = UUID.randomUUID().toString();
		String uuid2 = UUID.randomUUID().toString();
		
		String sourceFilterID_1 = spogServer.createFilterwithCheck(user_id,  "filterName1", "protect", "online", uuid1, "finished", 
				uuid1, "windows", "SQL_SERVER", uuid1, "sourceName1", "", "true", test);
		String sourceFilterID_2 = spogServer.createFilterwithCheck(user_id,  "filterName2", "unprotect", "online", uuid1, "finished", 
				uuid1, "windows", "SQL_SERVER", uuid1, "sourceName2", "", "false", test);
		String sourceFilterID_3 = spogServer.createFilterwithCheck(user_id,  "filterName3", "protect,unprotect", "offline", uuid1, "active", 
				uuid1, "linux", "EXCHANGE", uuid1, "sourceName3", "", "true", test);
		String sourceFilterID_4 = spogServer.createFilterwithCheck(user_id,  "filterName4", "protect", "online", uuid1, "active", 
				uuid1, "linux", "SQL_SERVER", uuid1, "sourceName4", "", "false", test);
		String sourceFilterID_5 = spogServer.createFilterwithCheck(user_id,  "filterName5", "protect", "online", uuid1, "finished", 
				uuid1, "windows", "All", uuid1, "sourceName5", "", "true", test);
		String sourceFilterID_6 = spogServer.createFilterwithCheck(user_id,  "filterName6", "protect,unprotect", "online", uuid1, "active", 
				uuid1, "linux,windows", "All", uuid1, "sourceName6", "", "false", test);
		String sourceFilterID_7 = spogServer.createFilterwithCheck(user_id,  "filterName7", "protect", "online,offline", uuid2, "finished", 
				uuid2, "windows", "SQL_SERVER", uuid1, "sourceName7", "", "false", test);
		String sourceFilterID_8 = spogServer.createFilterwithCheck(user_id,  "filterName8", "protect", "online", uuid1 + "," + uuid2, "finished,canceled", 
				uuid1 + "," + uuid2, "windows", "SQL_SERVER", uuid1, "sourceName8", "", "false", test);
		String sourceFilterID_9 = spogServer.createFilterwithCheck(user_id,  "filterName9", "protect", "online", uuid1 + "," + uuid2, "finished", 
				uuid1 + "," + uuid2, "linux,windows", "EXCHANGE,SQL_SERVER", uuid1 + "," + uuid2, "sourceName9", "", "false", test);
		String sourceFilterID_10 = spogServer.createFilterwithCheck(user_id,  "filterName10", "protect", "online", uuid1 + "," + uuid2, "finished", 
				uuid1 + "," + uuid2, "linux,windows", "EXCHANGE,SQL_SERVER", uuid1 + "," + uuid2, "", "", "false", test);
		String sourceFilterID_11 = spogServer.createFilterwithCheck(user_id,  "filterName11", "protect", "online", uuid1 + "," + uuid2, "finished", 
				uuid1 + "," + uuid2, "linux,windows", "EXCHANGE,SQL_SERVER", uuid1, "none", "", "false", test);
		String sourceFilterID_12 = spogServer.createFilterwithCheck(user_id,  "filterName12", "protect", "online", uuid1 + "," + uuid2, "finished", 
				uuid1 + "," + uuid2, "linux,windows", "EXCHANGE,SQL_SERVER", "none", "sourceName12", "", "false", test);
		String sourceFilterID_13 = spogServer.createFilterwithCheck(user_id,  "filterName13", "protect", "online", uuid1 + "," + uuid2, "finished", 
				uuid1 + "," + uuid2, "linux,windows", "EXCHANGE,SQL_SERVER", "emptyarray", "sourceName13", "", "false", test);
		
		Response response = spogServer.getFiltersByUserID(user_id, filter_str, sort_str, 
				curr_page, page_size, test);
		
		ArrayList<String> filter_names = response.then().extract().path("data.filter_name");
		assertEquals(filter_names.size(), expected_size);
		assertEquals(filter_names.get(0), expected_filter_name);
		
//		spogServer.userLogin(csrAdmin, csrPwd);
//		org4spogServer.setToken(spogServer.getJWTToken());
//		org4spogServer.destroyOrganization(orgID);
		
	}
	
	@DataProvider(name = "csr_readonly_params")
	public final Object[][] getFilters_csr_readonly() {
		return new Object[][] {
			{this.csr_readonly_email, csr_readonly_password}
		};}
	
	@Test(dataProvider = "csr_readonly_params")
	public void csrReadonlyTest(String username, String password) {
		spogServer.userLogin(username, password);
		String user_id = spogServer.GetLoggedinUser_UserID();
		String uuid1 = UUID.randomUUID().toString();
		String uuid2 = UUID.randomUUID().toString();
		
//		String sourceFilterID_1 = spogServer.createFilterwithCheck(user_id,  "filterName" + RandomStringUtils.randomAlphanumeric(8), "protect", "online", uuid1, "finished", 
//				uuid1, "windows", "SQL_SERVER", uuid1, "sourceName1", "machine", "true", test);
		spogServer.userLogin(csr_readonly_email, csr_readonly_password);
		Response response = spogServer.getFiltersByUserID(user_id, "", "", -1, -1, test);
		spogServer.checkResponseStatus(response, 200);
		
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
