package api.users.jobfilters;

import org.testng.annotations.Test;

import com.google.inject.PrivateBinder;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.sun.mail.handlers.message_rfc822;

import Constants.SpogConstants;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGJobServer;
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
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.management.OperationsException;

import Constants.SpogConstants;

import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.IFactoryAnnotation;
import org.testng.annotations.Parameters;
import Constants.SourceType;

public class UpdateJobFilterForLoggedinUserTest extends base.prepare.Is4Org {

	private SPOGServer spogServer;
	private SPOGJobServer spogJobServer;
	private UserSpogServer userSpogServer;
	private String csrAdmin;
	private String csrPwd;
//	private ExtentReports rep;
	private ExtentTest test;
	private String csr_readonly_email = "zhaoguo.ma+csrreadonly@gmail.com";
	private String csr_readonly_password = "Zetta1234";

	private String password = "Pa$$w0rd";
	private InitialTestDataImpl initialTestDataImpl;
	private InitialTestData itd;

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
		spogJobServer = new SPOGJobServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		this.csrAdmin = csrAdminUserName;
		this.csrPwd = csrAdminPassword;
		spogServer.userLogin(this.csrAdmin, this.csrPwd);
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		String prefix_a = RandomStringUtils.randomAlphanumeric(8) + this.getClass().getSimpleName();		
		initialTestDataImpl = new InitialTestDataImpl(baseURI, port, csrAdminUserName, csrAdminPassword, prefix_a, password);
		itd = initialTestDataImpl.initialize();
	}
	
	@DataProvider(name = "update_job_filter_valid")
	public final Object[][] updateJobFilterValidParams() {
		return new Object[][] {
			
			//root msp related
			{ itd.getRoot_msp_org_1_user_1_email(), password,  
				"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true",
				"finished",	UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "", "true"},
			{ itd.getRoot_msp_org_1_account_admin_1_email(), password,  
				"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true",
				"finished",	UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "", "true"},
			{ itd.getRoot_msp_org_1_sub_msp_1_user_1_email(), password,  
				"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true",
				"finished",	UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "", "true"},
			{ itd.getRoot_msp_org_1_sub_msp_1_account_admin_1_email(), password,  
				"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true",
				"finished",	UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "", "true"},
			{ itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1_email(), password,  
				"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true",
				"finished",	UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "", "true"},
			{ itd.getRoot_msp_org_1_account_1_user_1_email(), password,  
				"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true",
				"finished",	UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "", "true"},
			
			// monitor user related
			{ itd.getRoot_msp_org_1_monitor_user_1_email(), password,  
				"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true",
				"finished",	UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "", "true"},
			{ itd.getRoot_msp_org_1_account_1_monitor_user_1_email(), password,  
				"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true",
				"finished",	UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "", "true"},
			{ itd.getRoot_msp_org_1_sub_msp_1_monitor_user_1_email(), password,  
				"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true",
				"finished",	UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "", "true"},
			{ itd.getMsp_org_1_monitor_user_1_email(), password,  
				"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true",
				"finished",	UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "", "true"},
			{ itd.getDirect_org_1_monitor_user_1_email(), password,  
				"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true",
				"finished",	UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "", "true"},
											
			
			// update different values
			{ itd.getDirect_org_1_user_2_email(), password,  
				"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true",
				"finished",	UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "", "true"},
			{ itd.getMsp_org_1_user_2_email(), password,  
				"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true",
				"finished",	UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "", "true"}, 
			{ itd.getMsp_org_1_sub_1_user_2_email(), password,  
				"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true",
				"finished",	UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "", "true"}, 
			{ itd.getMsp_org_1_account_admin_1_email(), password,  
				"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true",
				"finished",	UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "", "true"},
			{ itd.getDirect_org_1_user_2_email(), password,  
				"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true",
				"active,finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName1", "false" },  
			{ itd.getDirect_org_1_user_2_email(), password,  
				"none", "none", "none", "backup", "custom", "", "", "sourceName", "filterName", "",
				"active,finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName1", "false" },
			{ itd.getDirect_org_1_user_2_email(), password,  
				"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true",
				"none", "none", "none", "none", "custom", "none", "none",	"sourceName", "filterName1", "false" },
			{ itd.getDirect_org_1_user_2_email(), password,  
				"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true",
				null, null, null, null, "custom", null, null, "sourceName", "filterName1", null },
			{ itd.getDirect_org_1_user_2_email(), password,  
				null, null, null, null, "custom", null, null, "sourceName",  "filterName1", null, 
				"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true"},
			{ itd.getDirect_org_1_user_2_email(), password,  
				null, null, null, null, "custom", null, null, "sourceName", "filterName", null, 
				null, null, null, null, "custom", null, null, "sourceName", "filterName1", null, },
			{ itd.getDirect_org_1_user_2_email(), password,  
				"active,finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup,restore", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true",
				"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),	"sourceName", "filterName1", "false" },

			{ itd.getDirect_org_1_user_2_email(), password,  
				"active,finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup,restore", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true",
				"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName1", "false" },
			{ itd.getDirect_org_1_user_2_email(), password,  
				"active,finished", UUID.randomUUID().toString() + "," + UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup,restore", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true",
				"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName1", "false" },
			{ itd.getDirect_org_1_user_2_email(), password,  
				"active,finished", UUID.randomUUID().toString(), UUID.randomUUID().toString() + "," + UUID.randomUUID().toString(), "backup,restore", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true",
				"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),	"sourceName", "filterName1", "false" },
			{ itd.getDirect_org_1_user_2_email(), password,  
				"active,finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup,restore", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true",
				"finished", UUID.randomUUID().toString() + "," + UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName1", "false" },
			{ itd.getDirect_org_1_user_2_email(), password,  
				"active,finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup,restore", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true",
				"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString() + "," + UUID.randomUUID().toString(), "backup","custom",  String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName1", "false" },
		};
	}
	

	@Test(dataProvider = "update_job_filter_valid")
	public void updateJobFilterValid(String userName, String password, String jobStatus, String policyID,
			String resourceID, String jobType, String startTimeType, String startTimeTSStart, String startTimeTSEnd, String sourceName, 
			String filterName, String isDefault, String jobStatusUpdate, String policyIDUpdate,
			String resourceIDUpdate, String jobTypeUpdate, String startTimeTypeUpdate, String startTimeTSStartUpdate, String startTimeTSEndUpdate, String sourceNameUpdate,
			String filterNameUpdate, String isDefaultUpdate) 
	{
		spogServer.userLogin(userName, password);
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		filterName = prefix + filterName;
		filterNameUpdate = prefix + filterNameUpdate;
		
		if (null != policyID) {
			String[] splitted_policyID = policyID.split(",");
			Arrays.sort(splitted_policyID);
			policyID = String.join(",", splitted_policyID);
		}

		if (null != resourceID) {
			String[] splitted_resourceID = resourceID.split(",");
			Arrays.sort(splitted_resourceID);
			resourceID = String.join(",", splitted_resourceID);
		}
		
		spogJobServer.setToken(spogServer.getJWTToken());
		String filterID = spogJobServer.createJobFilterForLoggedinUserWithCheckEx(jobStatus, policyID, resourceID, jobType, 
				startTimeType, startTimeTSStart, startTimeTSEnd, sourceName, filterName, isDefault, test);
		
		if (null != policyIDUpdate) {
			String[] splitted_policyIDUpdate = policyIDUpdate.split(",");
			Arrays.sort(splitted_policyIDUpdate);
			policyIDUpdate = String.join(",", splitted_policyIDUpdate);
		}

		if (null != resourceIDUpdate) {
			String[] splitted_resourceIDUpdate = resourceIDUpdate.split(",");
			Arrays.sort(splitted_resourceIDUpdate);
			resourceIDUpdate = String.join(",", splitted_resourceIDUpdate);
		}
		
		spogJobServer.updateJobFilterForLoggedinUserWithCheckEx(filterID, jobStatusUpdate, policyIDUpdate, resourceIDUpdate, jobTypeUpdate, 
				startTimeTypeUpdate, startTimeTSStartUpdate, startTimeTSEndUpdate, sourceNameUpdate, filterNameUpdate, isDefaultUpdate, test);
	}
	
	@DataProvider(name = "update_job_filter_invalid")
	public final Object[][] updateJobFilterInvalidParams() {
		return new Object[][] {
		// invalid job status
		{ itd.getDirect_org_1_user_2_email(), password,  
			"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true",
			"jobStatus", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),"sourceName",  "filterName", "true", 400, "40000006" },
		{ itd.getDirect_org_1_user_2_email(), password,  
			"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup","custom",  String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true",
			"finished,jobStatus", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true", 400, "40000006" },
		// invalid job type
		{ itd.getDirect_org_1_user_2_email(), password, 
			"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true",
			"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "jobtype", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true", 400, "40000006" },
		{ itd.getDirect_org_1_user_2_email(), password, 
			"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true",
			"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup,jobtype", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true", 400, "40000006" },
		// invalid UUID
		{ itd.getDirect_org_1_user_2_email(), password,  
			"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true",
			"finished", "policyID", UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true", 400, "40000005" },
		{ itd.getDirect_org_1_user_2_email(), password,  
			"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true",
			"finished", UUID.randomUUID().toString(), "policyID", "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true", 400, "40000005" },
		// invalid time format
		{ itd.getDirect_org_1_user_2_email(), password, 
			"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup","custom",  String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true",
			"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", "2017/07/07", String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true", 400, "00100001" },
		{ itd.getDirect_org_1_user_2_email(), password,  
			"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup","custom",  String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true",
			"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup","custom",  String.valueOf(System.currentTimeMillis()), "2017/07/07", "sourceName", "filterName", "true", 400, "00100001" },
		// invalid is_default 
//		{ itd.getDirect_org_1_user_2_email(), password, 
//			"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup","custom",  String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true",
//			"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup","custom",  String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "isDefault", 400, "00100001" },
		};}
	
	@Test(dataProvider = "update_job_filter_invalid")
	public void updateJobFilterInvalid(String userName, String password, String jobStatus, String policyID,
			String resourceID, String jobType, String startTimeType, String startTimeTSStart, String startTimeTSEnd, String sourceName, String filterName, String isDefault, 
			String jobStatusUpdate, String policyIDUpdate, String resourceIDUpdate, String jobTypeUpdate, String startTimeTypeUpdate, String startTimeTSStartUpdate, String startTimeTSEndUpdate, String sourceNameUpdate,
			String filterNameUpdate, String isDefaultUpdate, int statusCode, String errorCode) 
	{
		spogServer.userLogin(userName, password);
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		filterName = prefix + filterName;
		filterNameUpdate = prefix + filterNameUpdate;
		
		spogJobServer.setToken(spogServer.getJWTToken());
		String filterID = spogJobServer.createJobFilterForLoggedinUserWithCheckEx(jobStatus, policyID, resourceID, jobType, 
				startTimeType, startTimeTSStart, startTimeTSEnd, sourceName, filterName, isDefault, test);
		
		spogJobServer.updateJobFilterForLoggedinUserWithCodeCheckEx(filterID, jobStatusUpdate, policyIDUpdate, resourceIDUpdate, jobTypeUpdate, 
				startTimeTypeUpdate, startTimeTSStartUpdate, startTimeTSEndUpdate, sourceNameUpdate, filterNameUpdate, isDefaultUpdate, statusCode, errorCode, test);
	}
	
	@DataProvider(name = "update_job_filter_no_token")
	public final Object[][] updateJobFilterNoTokenParams() {
		return new Object[][] {
		// msp admin to update direct admin's filter;
		{ itd.getDirect_org_1_user_2_email(), password, 
			"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true",
			"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true", 401, "00900006" },
		{ itd.getMsp_org_1_user_2_email(), password, 
			"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup","custom",  String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true",
			"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true", 401, "00900006" },
		{ itd.getMsp_org_1_sub_1_user_1_email(), password, 
			"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup","custom",  String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true",
			"finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true", 401, "00900006" },
		};}
	
	@Test(dataProvider = "update_job_filter_no_token")
	public void updateJobFilterNoToken(String userName, String password, String jobStatus, String policyID,
			String resourceID, String jobType, String startTimeType, String startTimeTSStart, String startTimeTSEnd, String sourceName, String filterName, String isDefault, 
			String jobStatusUpdate, String policyIDUpdate, String resourceIDUpdate, String jobTypeUpdate, String startTimeTypeUpdate, String startTimeTSStartUpdate, String startTimeTSEndUpdate, String sourceNameUpdate,
			String filterNameUpdate, String isDefaultUpdate, int statusCode, String errorCode) 
	{
		spogServer.userLogin(userName, password);
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		filterName = prefix + filterName;
		filterNameUpdate = prefix + filterNameUpdate;
		
		spogJobServer.setToken(spogServer.getJWTToken());
		String filterID = spogJobServer.createJobFilterForLoggedinUserWithCheckEx(jobStatus, policyID, resourceID, jobType, startTimeType, 
				startTimeTSStart, startTimeTSEnd, sourceName, filterName, isDefault, test);
		
		spogJobServer.setToken("");
		spogJobServer.updateJobFilterForLoggedinUserWithCodeCheckEx(filterID, jobStatusUpdate, policyIDUpdate, resourceIDUpdate, jobTypeUpdate, 
				startTimeTypeUpdate, startTimeTSStartUpdate, startTimeTSEndUpdate, sourceNameUpdate, filterNameUpdate, isDefaultUpdate, statusCode, errorCode, test);
	}
	
	@Test
	public void updateJobFilterInvalidFilterID() {
		spogServer.userLogin(itd.getDirect_org_1_user_2_email(), password);
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		spogJobServer.setToken(spogServer.getJWTToken());
		
		String jobStatus = "finished";
		String policyID = UUID.randomUUID().toString();
		String resourceID = UUID.randomUUID().toString();
		String jobType = "backup";
		String startTimeTSStart = String.valueOf(System.currentTimeMillis());
		String startTimeTSEnd = String.valueOf(System.currentTimeMillis());
		String filterName = prefix + "filterName";
		String isDefault = String.valueOf(true);
		
		String filterID = spogJobServer.createJobFilterForLoggedinUserWithCheckEx(jobStatus, policyID, resourceID, jobType, 
				"custom", startTimeTSStart, startTimeTSEnd, "sourceName", filterName, isDefault, test);
		
		spogJobServer.updateJobFilterForLoggedinUserWithCodeCheckEx(UUID.randomUUID().toString(), jobStatus, policyID, resourceID, jobType, 
				"custom", startTimeTSStart, startTimeTSEnd, "sourceName", filterName, isDefault, 404, "00A00A02", test);

		spogJobServer.updateJobFilterForLoggedinUserWithCodeCheckEx("filterID", jobStatus, policyID, resourceID, jobType, 
				"custom", startTimeTSStart, startTimeTSEnd, "sourceName", filterName, isDefault, 400, "40000005", test);
	}
	
	@DataProvider(name = "update_job_filter_is_default")
	public final Object[][] updateJobFilterIsDefaultParams() {
		return new Object[][] {
		{ itd.getDirect_org_1_user_2_email(), password, "finished",
			UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()),
			String.valueOf(System.currentTimeMillis()), "sourceName", "filterName"}, 
		{ itd.getMsp_org_1_user_2_email(), password, "finished",
			UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()),
			String.valueOf(System.currentTimeMillis()), "sourceName","filterName"}, 
		{ itd.getMsp_org_1_sub_1_user_2_email(), password, "finished",
			UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()),
			String.valueOf(System.currentTimeMillis()), "sourceName","filterName"}, 
		};}
	
	@Test (dataProvider = "update_job_filter_is_default")
	public void checkFilterIsDefault(String userName, String password, String jobStatus, String policyID, String resourceID, String jobType, String startTimeType, String startTimeTSStart, 
			String startTimeTSEnd, String sourceName, String filterName) {
		spogServer.userLogin(userName, password);
		spogJobServer.setToken(spogServer.getJWTToken());
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		filterName = prefix + filterName;
		String filterID1 = spogJobServer.createJobFilterForLoggedinUserWithCheckEx(jobStatus, policyID, resourceID, jobType, startTimeType,
				startTimeTSStart, startTimeTSEnd, sourceName, filterName, "true", test);
		String filterID2 = spogJobServer.createJobFilterForLoggedinUserWithCheckEx(jobStatus, policyID, resourceID, jobType, startTimeType, 
				startTimeTSStart, startTimeTSEnd, sourceName, filterName + "added", "false", test);
		spogJobServer.updateJobFilterForLoggedinUserWithCheckEx(filterID2, jobStatus, policyID, resourceID, jobType, startTimeType,
				startTimeTSStart, startTimeTSEnd, sourceName, filterName+ "added", "true", test);
		Response response = spogJobServer.getJobFilterForLoggedinUserByID(filterID1, test);
		response.then().body("data.is_default", equalTo(false));
		response = spogJobServer.getJobFilterForLoggedinUserByID(filterID2, test);
		response.then().body("data.is_default", equalTo(true));
		
		spogJobServer.updateJobFilterForLoggedinUserWithCheckEx(filterID2, jobStatus, policyID, resourceID, jobType, startTimeType, 
				startTimeTSStart, startTimeTSEnd, sourceName, filterName + "added", "false", test);
		String filterID3 = spogJobServer.createJobFilterForLoggedinUserWithCheckEx(jobStatus, policyID, resourceID, jobType, startTimeType, 
				startTimeTSStart, startTimeTSEnd, sourceName, filterName + "added2", "true", test);
		response = spogJobServer.getJobFilterForLoggedinUserByID(filterID1, test);
		response.then().body("data.is_default", equalTo(false));
		response = spogJobServer.getJobFilterForLoggedinUserByID(filterID2, test);
		response.then().body("data.is_default", equalTo(false));
		response = spogJobServer.getJobFilterForLoggedinUserByID(filterID3, test);
		response.then().body("data.is_default", equalTo(true));
	}
	
	@Test
	public void csrReadonlyTest() {
		spogServer.userLogin(csr_readonly_email, csr_readonly_password);
		spogJobServer.setToken(spogServer.getJWTToken());
		String filterID1 = spogJobServer.createJobFilterForLoggedinUserWithCheckEx("finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom",
				String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName" + RandomStringUtils.randomAlphanumeric(8), "true", test);
		
		spogJobServer.updateJobFilterForLoggedinUserWithCheckEx(filterID1, "finished", UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom",
				String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName_updated" + RandomStringUtils.randomAlphanumeric(8), "false", test);
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
