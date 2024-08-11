package api.users.sourcefilters;

import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.sun.mail.handlers.message_rfc822;

import Constants.SpogConstants;
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

public class GetSpecificSourceFilterFromSpecificUserTest extends base.prepare.Is4Org {

	private SPOGServer spogServer;
	private UserSpogServer userSpogServer;
	
	private InitialTestDataImpl initialTestDataImpl;
	private InitialTestData itd;

	private String csrAdmin;
	private String csrPwd;
//	private ExtentReports rep;
	private ExtentTest test;


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
	private String org_model_prefix=this.getClass().getSimpleName();
	
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
		
	}
	
	@Test
	public void mspAccountAdminTest() {
		spogServer.userLogin(csrAdmin, csrPwd);
		String mspFilterID = spogServer.createFilterwithCheck(itd.getMsp_org_1_user_1(), "filter", "protect,unprotect", 
				"online,offline", uuid1+","+uuid2, "active,finished", uuid1+","+uuid2, "linux,windows", "EXCHANGE,SQL_SERVER,All", uuid1, "sourceName", "machine", "true", test);
		String directFilterID = spogServer.createFilterwithCheck(itd.getDirect_org_1_user_1(), "filter", "protect,unprotect", 
				"online,offline", uuid1+","+uuid2, "active,finished", uuid1+","+uuid2, "linux,windows", "EXCHANGE,SQL_SERVER,All", uuid1, "sourceName", "machine", "true", test);
		String subFilterID_a = spogServer.createFilterwithCheck(itd.getMsp_org_1_sub_1_user_1(), "filter", "protect,unprotect", 
				"online,offline", uuid1+","+uuid2, "active,finished", uuid1+","+uuid2, "linux,windows", "EXCHANGE,SQL_SERVER,All", uuid1, "sourceName", "machine", "true", test);
		String subFilterID_b = spogServer.createFilterwithCheck(itd.getMsp_org_1_sub_2_user_1(), "filter", "protect,unprotect", 
				"online,offline", uuid1+","+uuid2, "active,finished", uuid1+","+uuid2, "linux,windows", "EXCHANGE,SQL_SERVER,All", uuid1, "sourceName", "machine", "true", test);
		String mspAccountFilterID = spogServer.createFilterwithCheck(itd.getMsp_org_1_account_admin_1(), "filter", "protect,unprotect", 
				"online,offline", uuid1+","+uuid2, "active,finished", uuid1+","+uuid2, "linux,windows", "EXCHANGE,SQL_SERVER,All", uuid1, "sourceName", "machine", "true", test);
		
		spogServer.userLogin(itd.getMsp_org_1_account_admin_1_email(), password);
		spogServer.getFilterByID(itd.getMsp_org_1_sub_1_user_1(), subFilterID_a);
		spogServer.getFilterByID(itd.getMsp_org_1_account_admin_1(), mspAccountFilterID);
		spogServer.getFilterByIDWithExpectedCode(itd.getMsp_org_1_sub_2_user_1(), subFilterID_b, 403);
		spogServer.getFilterByIDWithExpectedCode(itd.getMsp_org_1_user_1(), mspFilterID, 403);
		spogServer.getFilterByIDWithExpectedCode(itd.getDirect_org_1_user_1(), directFilterID, 403);
		
		spogServer.userLogin(itd.getMsp_org_1_user_1_email(), password);
		spogServer.getFilterByID(itd.getMsp_org_1_account_admin_1(), mspAccountFilterID);
		
		spogServer.userLogin(itd.getMsp_org_1_sub_1_user_1_email(), password);
		spogServer.getFilterByIDWithExpectedCode(itd.getMsp_org_1_user_1(), mspFilterID, 403);
		
		spogServer.userLogin(itd.getDirect_org_1_user_1_email(), password);
		spogServer.getFilterByIDWithExpectedCode(itd.getMsp_org_1_user_1(), mspFilterID, 403);
	}
	
	public void compareFilter(String filter_id, String filter_name, String user_id, String organization_id, String protection_status,
						String connection_status, String protection_policy, String backup_status, 
						String source_group, String operating_system, String applications, String site_id, String source_name, String source_type, Boolean is_default, Response response) {
		
		String actual_filter_id = response.then().extract().path("data.filter_id");
	    
	    response.then().body("data.filter_name", equalTo(filter_name))
	    .body("data.organization_id",equalTo(organization_id))
	    //.body("data.source_type", equalTo(source_type))
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

	    ArrayList<String> response_backup_status = response.then().extract().path("data.last_job");
	    spogServer.assertFilterItem(backup_status, response_backup_status);

	    ArrayList<String> response_source_group = response.then().extract().path("data.group_id");
	    spogServer.assertFilterItem(source_group, response_source_group);
	    
	    ArrayList<String> response_operating_system =
	        response.then().extract().path("data.operating_system");	    
	    spogServer.assertFilterItem(operating_system, response_operating_system);
	   
//	    ArrayList<String> response_applications = response.then().extract().path("data.applications");
//	    spogServer.assertFilterItem(applications, response_applications);
	    
	    ArrayList<String> response_site_id = response.then().extract().path("data.site_id");
	    spogServer.assertFilterItem(site_id, response_site_id);
	}

	@DataProvider(name = "basicfilterInfo")
	public final Object[][] getbasicfilterInfo() {
		return new Object[][] { 
				{"filter_name1", itd.getDirect_org_1_user_1_email(), itd.getDirect_org_1(), "protect", uuid1, "online", "finished", uuid1, "windows", "SQL_SERVER", uuid1,"sourceName","machine", "true"}, 
				{"filter_name2", itd.getDirect_org_1_user_1_email(), itd.getDirect_org_1(), "protect,unprotect", uuid1, "online,offline", "finished,active", uuid1, "windows,linux", "SQL_SERVER,EXCHANGE,All",uuid1,"sourceName", "machine", "true"},
				{"filter_name3", itd.getMsp_org_1_sub_1_user_1_email(), itd.getMsp_org_1_sub_1(), "protect", uuid1,"online", "finished", uuid1, "windows", "SQL_SERVER",uuid1,"sourceName", "machine", "true"},
				{"filter_name4",  itd.getMsp_org_1_sub_1_user_1_email(), itd.getMsp_org_1_sub_1(), "protect,unprotect", uuid1, "online,offline", "finished,active", uuid1, "windows,linux", "SQL_SERVER,EXCHANGE,All",uuid1,"sourceName", "machine", "true"},
				{"filter_name5", itd.getDirect_org_1_user_1_email(), itd.getDirect_org_1(), "protect", uuid1 + "," + uuid2, "online", "finished", uuid1 + "," + uuid2, "windows", "SQL_SERVER",uuid1,"sourceName", "machine", "true"}, 
				{"filter_name6", itd.getDirect_org_1_user_1_email(), itd.getDirect_org_1(), "protect,unprotect", uuid1 + "," + uuid2, "online,offline", "finished,active", uuid1 + "," + uuid2, "windows,linux", "SQL_SERVER,EXCHANGE,All", uuid1,"sourceName","machine", "true"},
				{"filter_name7",  itd.getMsp_org_1_sub_1_user_1_email(), itd.getMsp_org_1_sub_1(), "protect", uuid1 + "," + uuid2,"online", "finished", uuid1 + "," + uuid2, "windows", "SQL_SERVER", uuid1,"sourceName","machine", "true"},
				{"filter_name8",  itd.getMsp_org_1_sub_1_user_1_email(), itd.getMsp_org_1_sub_1(), "protect,unprotect", uuid1 + "," + uuid2, "online,offline", "finished,active", uuid1 + "," + uuid2, "windows,linux", "SQL_SERVER,EXCHANGE,All",uuid1,"sourceName", "machine", "true"},
				{"filter_name9", itd.getMsp_org_1_user_1_email(), itd.getMsp_org_1(), "protect", uuid1, "online", "finished", uuid1, "windows", "SQL_SERVER", uuid1,"sourceName","machine", "true"}, 
				{"filter_name10", itd.getMsp_org_1_user_1_email(), itd.getMsp_org_1(), "protect,unprotect", uuid1, "online,offline", "finished,active", uuid1, "windows,linux", "SQL_SERVER,EXCHANGE,All", uuid1,"sourceName","machine", "true"},
				{"filter_name11", itd.getMsp_org_1_user_1_email(), itd.getMsp_org_1(), "protect", uuid1 + "," + uuid2, "online", "finished", uuid1 + "," + uuid2, "windows", "SQL_SERVER", uuid1,"sourceName","machine", "true"}, 
				{"filter_name12", itd.getMsp_org_1_user_1_email(), itd.getMsp_org_1(), "protect,unprotect", uuid1 + "," + uuid2, "online,offline", "finished,active", uuid1 + "," + uuid2, "windows,linux", "SQL_SERVER,EXCHANGE,All", uuid1,"sourceName","machine", "true"},
				{"filter_name12_a", itd.getMsp_org_1_user_1_email(), itd.getMsp_org_1(), "protect,unprotect", uuid1 + "," + uuid2, "online,offline", "finished,active,canceled", uuid1 + "," + uuid2, "windows,linux,unknown", "SQL_SERVER,EXCHANGE,All", uuid1,"sourceName","machine", "true"},
				{"filter_name13", itd.getMsp_org_1_user_1_email(), itd.getMsp_org_1(), "protect,unprotect", uuid1 + "," + uuid2, "online,offline", "finished,active", uuid1 + "," + uuid2, "windows,linux", null, uuid1,"sourceName","machine", "true"},
				{"filter_name14", itd.getMsp_org_1_user_1_email(), itd.getMsp_org_1(), null, null, null, null, null, null, null,uuid1,"sourceName", "machine", "true"},
				{"filter_name15", itd.getMsp_org_1_user_1_email(), itd.getMsp_org_1(), "emptyarray", "emptyarray", "emptyarray", "emptyarray", "emptyarray", "emptyarray", "emptyarray", uuid1,"sourceName","machine", "true"},
				{"filter_name16", itd.getDirect_org_1_user_1_email(), itd.getDirect_org_1(), "protect", uuid1 + "," + uuid2, "online", "finished", uuid1 + "," + uuid2, "windows", "SQL_SERVER", uuid1,"sourceName","machine", "true"},
				{"filter_name17", itd.getDirect_org_1_user_1_email(), itd.getDirect_org_1(), "protect", uuid1 + "," + uuid2, "online", "finished", uuid1 + "," + uuid2, "windows", "SQL_SERVER", uuid1,"sourceName","all", "true"},
				{"filter_name18", itd.getDirect_org_1_user_1_email(), itd.getDirect_org_1(), "protect", uuid1 + "," + uuid2, "online", "finished", uuid1 + "," + uuid2, "windows", "SQL_SERVER", uuid1,"sourceName","virtual_standby", "true"},
				{"filter_name19", itd.getDirect_org_1_user_1_email(), itd.getDirect_org_1(), "protect", uuid1 + "," + uuid2, "online", "finished", uuid1 + "," + uuid2, "windows", "SQL_SERVER", uuid1,"sourceName","instant_vm", "true"},
				{"filter_name20", itd.getDirect_org_1_user_1_email(), itd.getDirect_org_1(), "protect", uuid1 + "," + uuid2, "online", "finished", uuid1 + "," + uuid2, "windows", "SQL_SERVER", uuid1,"sourceName","shared_folder", "true"},
				{"filter_name21", itd.getDirect_org_1_user_1_email(), itd.getDirect_org_1(), "protect", uuid1 + "," + uuid2, "online", "finished", uuid1 + "," + uuid2, "windows", "SQL_SERVER", uuid1,"sourceName","office_365", "true"},
				{"filter_name21_a", itd.getDirect_org_1_user_1_email(), itd.getDirect_org_1(), "protect", uuid1 + "," + uuid2, "online", "finished", uuid1 + "," + uuid2, "windows", "SQL_SERVER", uuid1,"sourceName","agentless_vm", "true"},
				{"filter_name22", itd.getDirect_org_1_user_1_email(), itd.getDirect_org_1(), "protect", uuid1 + "," + uuid2, "online", "finished", uuid1 + "," + uuid2, "windows", "SQL_SERVER", uuid1,"sourceName","machine", "false"},

				// different site_id format
				{"filter_name26", itd.getDirect_org_1_user_1_email(), itd.getDirect_org_1(), "protect", uuid1 + "," + uuid2, "online", "finished", uuid1 + "," + uuid2, "windows", "SQL_SERVER", uuid1 + "," + uuid2 ,"sourceName","machine", "false"},
				{"filter_name27", itd.getDirect_org_1_user_1_email(), itd.getDirect_org_1(), "protect", uuid1 + "," + uuid2, "online", "finished", uuid1 + "," + uuid2, "windows", "SQL_SERVER", "emptyarray","sourceName","machine", "false"},
				{"filter_name28", itd.getDirect_org_1_user_1_email(), itd.getDirect_org_1(), "protect", uuid1 + "," + uuid2, "online", "finished", uuid1 + "," + uuid2, "windows", "SQL_SERVER", "none","sourceName","machine", "false"},
				{"filter_name29", itd.getDirect_org_1_user_1_email(), itd.getDirect_org_1(), "protect", uuid1 + "," + uuid2, "online", "finished", uuid1 + "," + uuid2, "windows", "SQL_SERVER", null,"sourceName","machine", "false"},

				// different source_name format
				{"filter_name30", itd.getDirect_org_1_user_1_email(), itd.getDirect_org_1(), "protect", uuid1 + "," + uuid2, "online", "finished", uuid1 + "," + uuid2, "windows", "SQL_SERVER", uuid1,"","machine", "false"},
				{"filter_name31", itd.getDirect_org_1_user_1_email(), itd.getDirect_org_1(), "protect", uuid1 + "," + uuid2, "online", "finished", uuid1 + "," + uuid2, "windows", "SQL_SERVER", uuid1,"none","machine", "false"},
				{"filter_name32", itd.getDirect_org_1_user_1_email(), itd.getDirect_org_1(), "protect", uuid1 + "," + uuid2, "online", "finished", uuid1 + "," + uuid2, "windows", "SQL_SERVER", uuid1,null,"machine", "false"},
				//{"filter_name16", itd.getMsp_org_1_user_1_email(), itd.getMsp_org_1(), "", null, "", "", null, "", ""},
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
				{itd.getDirect_org_1_user_1_email(),  itd.getMsp_org_1_sub_1_user_1_email(), password, 403},
				//direct admin cannot get filter of other user in same org;
				{itd.getDirect_org_1_user_1_email(), itd.getDirect_org_1_user_2_email(), password, 200},
				//direct admin cannot get filter of sub organization;
				{ itd.getMsp_org_1_sub_1_user_1_email(), itd.getDirect_org_1_user_1_email(), password, 403},
				//sub admin cannot get filter of other user in same org;
				{ itd.getMsp_org_1_sub_1_user_1_email(),  itd.getMsp_org_1_sub_1_user_2_email(), password, 200},
				//msp admin cannot get sub admin's filter;
				{ itd.getMsp_org_1_sub_1_user_1_email(), itd.getMsp_org_1_user_1_email(), password, 200},
				//msp admin cannot get direct admin's filter;
				{itd.getDirect_org_1_user_1_email(), itd.getMsp_org_1_user_1_email(), password, 403}, 
				//csr admin cannot get direct admin's filter;
				{itd.getDirect_org_1_user_1_email(), this.csrAdmin, this.csrPwd, 200},
				//csr admin cannot get sub admin's filter;
				{ itd.getMsp_org_1_sub_1_user_1_email(), this.csrAdmin, this.csrPwd, 200},
				//csr admin cannot get msp admin's filter;
				{itd.getMsp_org_1_user_1_email(), this.csrAdmin, this.csrPwd, 200},
				//sub org admin cannot get msp admin's filter;
				{itd.getMsp_org_1_user_1_email(),  itd.getMsp_org_1_sub_1_user_1_email(), password, 403},
				//msp admin cannot get filter of other user in same org;
				{itd.getMsp_org_1_user_1_email(), itd.getMsp_org_1_user_2_email(), password, 200},
				//direct cannot get msp admin's filter;
				{itd.getMsp_org_1_user_1_email(), itd.getDirect_org_1_user_1_email(), password, 403},
				
				//root msp related
				{itd.getRoot_msp_org_1_account_1_user_1_email(), itd.getRoot_msp_org_1_user_1_email(), password, 200},
				{itd.getRoot_msp_org_1_account_1_user_1_email(), itd.getRoot_msp_org_1_account_admin_1_email(), password, 200},
				{itd.getRoot_msp_org_1_account_1_user_1_email(), itd.getRoot_msp_org_1_account_admin_2_email(), password, 403},
				{itd.getRoot_msp_org_1_sub_msp_1_user_1_email(), itd.getRoot_msp_org_1_user_1_email(), password, 403},
				{itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1_email(), itd.getRoot_msp_org_1_user_1_email(), password, 403},
				{itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1_email(), itd.getRoot_msp_org_1_sub_msp_1_account_admin_1_email(), password, 200},
				{itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1_email(), itd.getRoot_msp_org_1_sub_msp_1_account_admin_2_email(), password, 403},
				
				{itd.getRoot_msp_org_1_account_1_user_1_email(), itd.getRoot_msp_org_1_account_1_user_2_email(), password, 200},
				{itd.getRoot_msp_org_1_user_2_email(), itd.getRoot_msp_org_1_user_1_email(), password, 200},
				{itd.getRoot_msp_org_1_sub_msp_1_user_1_email(), itd.getRoot_msp_org_1_sub_msp_1_user_2_email(), password, 200},
				
				{itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1_email(), itd.getRoot_msp_org_1_sub_msp_1_account_2_user_1_email(), password, 403},
				{itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1_email(), itd.getRoot_msp_org_1_sub_msp_2_account_1_user_1_email(), password, 403},
				
				//monitor user related
				{itd.getRoot_msp_org_1_monitor_user_1_email(), itd.getRoot_msp_org_1_user_1_email(), password, 200},
				{itd.getRoot_msp_org_1_sub_msp_1_monitor_user_1_email(), itd.getRoot_msp_org_1_sub_msp_1_user_1_email(), password, 200},
				{itd.getRoot_msp_org_1_account_1_monitor_user_1_email(), itd.getRoot_msp_org_1_account_1_user_1_email(), password, 200},
				{itd.getMsp_org_1_monitor_user_1_email(), itd.getMsp_org_1_user_1_email(), password, 200},
				{itd.getDirect_org_1_monitor_user_1_email(), itd.getDirect_org_1_user_1_email(), password, 200},
				
				{itd.getRoot_msp_org_1_user_1_email(), itd.getRoot_msp_org_1_monitor_user_1_email(), password, 200},
				{itd.getRoot_msp_org_1_sub_msp_1_user_1_email(), itd.getRoot_msp_org_1_sub_msp_1_monitor_user_1_email(), password, 200},
				{itd.getRoot_msp_org_1_account_1_user_1_email(), itd.getRoot_msp_org_1_account_1_monitor_user_1_email(), password, 200},
				{itd.getMsp_org_1_user_1_email(), itd.getMsp_org_1_monitor_user_1_email(), password, 200},
				{itd.getDirect_org_1_user_1_email(), itd.getDirect_org_1_monitor_user_1_email(), password, 200},
				
				};
	}
	
	@Test(dataProvider = "getfilterInfo_autherror")
	public void getfilter_autherror(String create_username, String get_username, String get_password, int status_code) {
		spogServer.userLogin(create_username, password);
		String user_id = spogServer.GetLoggedinUser_UserID();
		
//		spogServer.createFilterwithCheck(itd.getDirect_org_2_user_1(), "filter_name" + i.toString(), 
//		"protect", "online", null, "finished", null, "linux,windows", null,  uuid1, null, null, "false", test);
		String filter_id = spogServer.createFilterwithCheck(user_id, "filter_name"+RandomStringUtils.randomAlphanumeric(8), "protect", 
				"online", uuid1, "finished", uuid1, "windows", null,  uuid1, null, null, "false", test);
		
		spogServer.userLogin(get_username, get_password);
		spogServer.getFilterByIDWithExpectedCode(user_id, filter_id, status_code);
	}
	
	@DataProvider(name = "getfilterInfo_notoken")
	public final Object[][] getfilterInfo_notoken() {
		return new Object[][] { 
				{itd.getDirect_org_1_user_1_email(), 401},
				{itd.getMsp_org_1_user_1_email(), 401},
				{ itd.getMsp_org_1_sub_1_user_1_email(), 401}};
	}
	
	@Test(dataProvider = "getfilterInfo_notoken")
	public void getfilter_notoken(String user_name, int status_code) {
		spogServer.userLogin(user_name, password);
		String user_id = spogServer.GetLoggedinUser_UserID();
//		String filter_id = spogServer.createFilterwithCheck(user_id, "filter_name"+RandomStringUtils.randomAlphanumeric(8), "protect", 
//				"online", uuid1, "finished", uuid1, "windows", test);
		
		spogServer.setToken("");
		spogServer.getFilterByIDWithExpectedCode(user_id, UUID.randomUUID().toString(), status_code);
	}
	
	@DataProvider(name = "getfilterInfo_invalidfilterID")
	public final Object[][] getfilterInfo_invalidfilterID() {
		return new Object[][] { 
				{itd.getDirect_org_1_user_1_email(), 404},
				{itd.getMsp_org_1_user_1_email(), 404},
				{ itd.getMsp_org_1_sub_1_user_1_email(), 404}};
	}
	@Test(dataProvider = "getfilterInfo_invalidfilterID")
	public void getfilter_invalidFilterID(String user_name, int status_code) {
		spogServer.userLogin(user_name, password);
		String user_id = spogServer.GetLoggedinUser_UserID();
//		spogServer.createFilterwithCheck(user_id, "filter_name", "protect", 
//				"online", uuid1, "finished", uuid1, "windows", test);
		
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
		spogServer.CreateOrganizationWithCheck(prefix + "orgName" +org_model_prefix, orgType, prefix + "spog_qa@arcserve.com", 
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
		spogServer.CreateOrganizationWithCheck(prefix + "orgName" +org_model_prefix, orgType, prefix + "spog_qa@arcserve.com", 
				password, prefix + "spog_qa_first_name", prefix + "spog_qa_last_name", test);
		spogServer.userLogin(prefix + "spog_qa@arcserve.com", password);
		
		String user_id = spogServer.GetLoggedinUser_UserID();
		
		String filter_id_1 = spogServer.createFilterwithCheck(user_id, prefix + filter_name + "1", protection_status, connection_status, uuid1, 
				backup_status, uuid2, operating_system, applications, source_type_first, is_default_first, test);
		
		spogServer.createFilterwithCheck(user_id, prefix + filter_name + "2", protection_status, connection_status, uuid1, 
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
		spogServer.CreateOrganizationWithCheck(prefix + "orgName" +org_model_prefix, orgType, prefix + "spog_qa@arcserve.com", 
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
