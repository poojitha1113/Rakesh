package api.users.jobfilters;

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

public class CreateJobFilterTest extends base.prepare.Is4Org{

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
	
	@DataProvider(name = "create_job_filter_valid")
	public final Object[][] createJobFilterValidParams() {
		return new Object[][] {
			// different users
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),
						"sourceName", "filterName", "true" },
				{ itd.getMsp_org_1_user_2_email(), password, itd.getMsp_org_1_user_2(), "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),
						 "sourceName", "filterName", "true" },
				{ itd.getMsp_org_1_sub_1_user_1_email(), password, itd.getMsp_org_1_sub_1_user_1(), "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),
						"sourceName", "filterName", "true" },
				{ itd.getMsp_org_1_account_admin_1_email(), password, itd.getMsp_org_1_sub_1_user_1(), "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),
						"sourceName", "filterName", "true" },
				
				// different users, root msp realted
				{ itd.getRoot_msp_org_1_user_1_email(), password, itd.getRoot_msp_org_1_user_1(), "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),
						"sourceName", "filterName", "true" },
				{ itd.getRoot_msp_org_1_sub_msp_1_user_1_email(), password, itd.getRoot_msp_org_1_sub_msp_1_user_1(), "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),
						"sourceName", "filterName", "true" },
				{ itd.getRoot_msp_org_1_account_admin_1_email(), password, itd.getRoot_msp_org_1_account_admin_1(), "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),
						"sourceName", "filterName", "true" },
				{ itd.getRoot_msp_org_1_sub_msp_1_account_admin_1_email(), password, itd.getRoot_msp_org_1_sub_msp_1_account_admin_1(), "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),
						"sourceName", "filterName", "true" },
				{ itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1_email(), password, itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1(), "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),
						"sourceName", "filterName", "true" },
				{ itd.getRoot_msp_org_1_account_1_user_1_email(), password, itd.getRoot_msp_org_1_account_1_user_1(), "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),
						"sourceName", "filterName", "true" },
				{ itd.getRoot_msp_org_1_user_1_email(), password, itd.getRoot_msp_org_1_account_1_user_1(), "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),
						"sourceName", "filterName", "true" },
				{ itd.getRoot_msp_org_1_account_admin_1_email(), password, itd.getRoot_msp_org_1_account_1_user_1(), "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),
						"sourceName", "filterName", "true" },
				{ itd.getRoot_msp_org_1_sub_msp_1_account_admin_1_email(), password, itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1(), "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),
						"sourceName", "filterName", "true" },
				
				// monitor user related
				{ itd.getRoot_msp_org_1_sub_msp_1_user_1_email(), password, itd.getRoot_msp_org_1_sub_msp_1_monitor_user_1(), "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true" },
				{ itd.getRoot_msp_org_1_account_1_user_1_email(), password, itd.getRoot_msp_org_1_account_1_monitor_user_1(), "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true" },
				{ itd.getRoot_msp_org_1_user_1_email(), password, itd.getRoot_msp_org_1_monitor_user_1(), "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true" },
				{ itd.getMsp_org_1_user_1_email(), password, itd.getMsp_org_1_monitor_user_1(), "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true" },
				{ itd.getDirect_org_1_user_1_email(), password, itd.getDirect_org_1_monitor_user_1(), "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true" },
							
				
				// different timeType
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup", "last_7_days", null, null, "sourceName", "filterName", "true" },
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup", "last_24_hours", null, null, "sourceName", "filterName", "true" },
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup", "last_2_weeks", null, null, "sourceName", "filterName", "true" },
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup", "last_1_month", null, null, "sourceName", "filterName", "true" },
				
				// different job_type
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "restore", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),
						"sourceName", "filterName", "true" },
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup,restore", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),
						"sourceName", "filterName", "true" },
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "emptyarray", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),
						"sourceName", "filterName", "true" },
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), null, "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),
						"sourceName", "filterName", "true" },
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "none", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),
						"sourceName", "filterName", "true" },
			// different job_status
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),
						"sourceName", "filterName", "true" },
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "active", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),
						"sourceName", "filterName", "true" },
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "active,finished", UUID.randomUUID().toString(),
							UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),
							"sourceName", "filterName", "true" },
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "none", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),
						"sourceName",  "filterName", "true" },
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "emptyarray",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()),
						 String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true" },
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), null, UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),
						 "sourceName", "filterName", "true" },
				// different policy_id
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "finished",
						UUID.randomUUID().toString() + "," + UUID.randomUUID().toString(), UUID.randomUUID().toString(),
						"backup", "custom", String.valueOf(System.currentTimeMillis()), 
						String.valueOf(System.currentTimeMillis()),"sourceName",  "filterName", "true" },
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "finished", "emptyarray",
						UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),
						 "sourceName", "filterName", "true" },
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "finished", "none",
						UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),
						"sourceName",  "filterName", "true" },
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "finished", null,
						UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),
						"sourceName", "filterName", "true" },

				// different resource_id
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString() + "," + UUID.randomUUID().toString(), "backup","custom", 
						String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()), 
						"sourceName", "filterName", "true" },
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "finished", UUID.randomUUID().toString(),
						"emptyarray", "backup", "custom", String.valueOf(System.currentTimeMillis()), 
						String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true" },
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "finished", UUID.randomUUID().toString(),
						"none", "backup","custom",  String.valueOf(System.currentTimeMillis()),
						String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true" },
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "finished", UUID.randomUUID().toString(),
						null, "backup", "custom", String.valueOf(System.currentTimeMillis()), 
						String.valueOf(System.currentTimeMillis()),"sourceName",  "filterName", "true" },
				
				// different time range
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup", "custom", "", "",
						"sourceName", "filterName", "true" },
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup", "custom", "none", "none",
						"sourceName",  "filterName", "true" },
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup", "custom", null, null, "sourceName", "filterName", "true" },
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), "none",
						"sourceName", "filterName", "true" },
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), null,
						"sourceName", "filterName", "true" },
				
				// different is_default value;
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),
						"sourceName", "filterName", "false" },
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),
						"sourceName",  "filterName", "" },
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),
						"sourceName", "filterName", "none" },
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),
						"sourceName", "filterName", null },
				
				// create job_filter for other users in same org, or sub org
				{ itd.getDirect_org_1_user_1_email(), password, itd.getDirect_org_1_user_2(), "finished",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup","custom",  String.valueOf(System.currentTimeMillis()),
						String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "false" },
				{ itd.getMsp_org_1_user_1_email(), password, itd.getMsp_org_1_user_2(), "finished", UUID.randomUUID().toString(),
						UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),
						 "sourceName", "filterName", "false" },
				{ itd.getMsp_org_1_user_1_email(), password, itd.getMsp_org_1_sub_1_user_2(), "finished",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()),
						String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "false" },
				{ itd.getMsp_org_1_sub_1_user_1_email(), password, itd.getMsp_org_1_sub_1_user_2(), "finished",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()),
						String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "false" },
		};
	}
	@Test(dataProvider = "create_job_filter_valid")
	public void createJobFilterValid(String userName, String password, String userID, String jobStatus, String policyID, String resourceID, String jobType,
			String startTimeType, String startTimeTSStart, 
			String startTimeTSEnd, String sourceName, String filterName, String isDefault) {
		spogServer.userLogin(userName, password);
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		
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
		spogJobServer.createJobFilterWithCheckEx(userID, jobStatus, policyID, resourceID, jobType, startTimeType, startTimeTSStart, startTimeTSEnd, sourceName, prefix + filterName, isDefault, test);
	}
	
	@DataProvider(name = "create_job_filter_invalid")
	public final Object[][] createJobFilterInvalidParams() {
		return new Object[][] {
				// invalid filter_name
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "finished",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()),
						 String.valueOf(System.currentTimeMillis()), "sourceName", "", "true", 400,
						"40000001" },
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "finished",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom",String.valueOf(System.currentTimeMillis()),
						String.valueOf(System.currentTimeMillis()), "sourceName", "none", "true", 400,
						"40000001" },
//				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "finished",
//						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom",String.valueOf(System.currentTimeMillis()),
//						String.valueOf(System.currentTimeMillis()),  "sourceName", null, "true", 400,
//						"40000001" },
				// invalid job_status
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "jobstatus",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup","custom", String.valueOf(System.currentTimeMillis()),
						String.valueOf(System.currentTimeMillis()),  "sourceName","filterName", "true", 400,
						"40000006" },
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "finished,jobstatus",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup","custom", String.valueOf(System.currentTimeMillis()),
						String.valueOf(System.currentTimeMillis()),  "sourceName","filterName", "true", 400,
						"40000006" },
				// invalid time_type
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "finished,jobstatus",
							UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup","last_365_days", null, null,  "sourceName","filterName", "true", 400,
							"00100001" },
				// invalid job_type
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "finished",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "jobtype","custom", String.valueOf(System.currentTimeMillis()),
						String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true", 400,
						"40000006" },
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "finished",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup,jobtype","custom",
						String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),
						"sourceName","filterName", "true", 400, "40000006" },
				// invalid policy_id
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "finished", "policyID",
						UUID.randomUUID().toString(), "jobtype", "custom",String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),
						"sourceName","filterName", "true", 400, "40000005" },
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "finished",
						UUID.randomUUID().toString() + "," + "policyID", UUID.randomUUID().toString(), "jobtype","custom",
						String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),
						"sourceName","filterName", "true", 400, "40000005" },
				// invalid resource_id
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "finished",
						UUID.randomUUID().toString(), "policyID", "jobtype", "custom",String.valueOf(System.currentTimeMillis()),
						String.valueOf(System.currentTimeMillis()), "sourceName","filterName", "true", 400,
						"40000005" },
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "finished",
						UUID.randomUUID().toString(), UUID.randomUUID().toString() + "," + "policyID", "jobtype","custom",
						String.valueOf(System.currentTimeMillis()), String.valueOf(System.currentTimeMillis()),
						"sourceName","filterName", "true", 400, "40000005" },
				// invalid time_range
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "finished",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom","2017/07/07",
						String.valueOf(System.currentTimeMillis()), "sourceName","filter_name", "true", 400,
						"00100001" },
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "finished",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom",String.valueOf(System.currentTimeMillis()),
						"2017/07/07", "sourceName","filter_name", "true", 400,
						"00100001" },
				// invalid is_default
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "finished",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup","custom", String.valueOf(System.currentTimeMillis()),
						"2018-01-02 12:00:00","sourceName", "filter_name", "isdefault",
						400, "00100001" },
		};
	}
	
	@Test(dataProvider = "create_job_filter_invalid")
	public void createJobFilterInvalid(String userName, String password, String userID, String jobStatus, String policyID, String resourceID, String jobType, String startTimeType, String startTimeTSStart, 
			String startTimeTSEnd,String sourceName, String filterName, String isDefault, int statusCode, String errorCode) {
		spogServer.userLogin(userName, password);
		
		spogJobServer.setToken(spogServer.getJWTToken());
		spogJobServer.createJobFilterWithCodeCheckEx(userID, jobStatus, policyID, resourceID, jobType, startTimeType,  
				startTimeTSStart, startTimeTSEnd, sourceName, filterName, isDefault, statusCode, errorCode, test);
	}
	
	@DataProvider(name = "create_job_filter_auth_error")
	public final Object[][] createJobFilterAuthErrorParams() {
		return new Object[][] {
				{ itd.getDirect_org_1_user_2_email(), password, itd.getMsp_org_1_user_2(), "finished",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()),
						String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true", 403,
						"00100101" },
				{ itd.getDirect_org_1_user_2_email(), password, itd.getMsp_org_1_sub_1_user_2(), "finished",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()),
						String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true", 403,
						"00100101" },
				{ itd.getMsp_org_1_sub_1_user_2_email(), password, itd.getMsp_org_1_user_2(), "finished",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()),
						String.valueOf(System.currentTimeMillis()),  "sourceName", "filterName", "true", 403,
						"00100101" },
				{ itd.getMsp_org_1_sub_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "finished",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()),
						String.valueOf(System.currentTimeMillis()),"sourceName", "filterName", "true", 403,
						"00100101" },
				{ itd.getMsp_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "finished",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup","custom",  String.valueOf(System.currentTimeMillis()),
						String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true", 403,
						"00100101" },
				{ itd.getMsp_org_1_account_admin_1_email(), password, itd.getDirect_org_1_user_2(), "finished",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()),
						String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true", 403,
						"00100101" },
				{ itd.getMsp_org_1_account_admin_1_email(), password, itd.getMsp_org_1_sub_2_user_1(), "finished",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()),
						String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true", 403,
						"00100101" },
				{ itd.getMsp_org_1_account_admin_1_email(), password, itd.getMsp_org_1_user_2(), "finished",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()),
						String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true", 403,
						"00100101" },
				
				//root msp related
				{ itd.getRoot_msp_org_1_user_1_email(), password, itd.getRoot_msp_org_1_sub_msp_1_user_1(), "finished",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()),
						String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true", 403,
						"00100101" },
				{ itd.getRoot_msp_org_1_user_1_email(), password, itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1(), "finished",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()),
						String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true", 403,
						"00100101" },
				{ itd.getRoot_msp_org_1_account_admin_1_email(), password, itd.getRoot_msp_org_1_account_2_user_1(), "finished",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()),
						String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true", 403,
						"00100101" },
				{ itd.getRoot_msp_org_1_sub_msp_1_account_admin_1_email(), password, itd.getRoot_msp_org_1_sub_msp_1_account_2_user_1(), "finished",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()),
						String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true", 403,
						"00100101" },
				{ itd.getRoot_msp_org_1_account_1_user_1_email(), password, itd.getRoot_msp_org_1_sub_msp_1_user_1(), "finished",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()),
						String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true", 403,
						"00100101" },
				{ itd.getRoot_msp_org_2_user_1_email(), password, itd.getRoot_msp_org_1_user_1(), "finished",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()),
						String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true", 403,
						"00100101" },
				{ itd.getRoot_msp_org_1_sub_msp_1_user_1_email(), password, itd.getRoot_msp_org_1_sub_msp_2_user_1(), "finished",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()),
						String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true", 403,
						"00100101" },
				{ itd.getRoot_msp_org_1_account_1_user_1_email(), password, itd.getRoot_msp_org_1_account_2_user_1(), "finished",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()),
						String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true", 403,
						"00100101" },
				{ itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1_email(), password, itd.getRoot_msp_org_1_sub_msp_1_account_2_user_1(), "finished",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()),
						String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true", 403,
						"00100101" },

				{ itd.getRoot_msp_org_1_sub_msp_1_account_1_user_1_email(), password, itd.getRoot_msp_org_1_sub_msp_2_account_1_user_1(), "finished",
							UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()),
							String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true", 403,
							"00100101" },

				{ itd.getMsp_org_1_user_1_email(), password, itd.getRoot_msp_org_1_user_1(), "finished",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()),
						String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true", 403,
						"00100101" },
				
				// monitor user related
				{ itd.getRoot_msp_org_1_monitor_user_1_email(), password, itd.getRoot_msp_org_1_user_1(), "finished",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()),
						String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true", 403, "00100101" },
				{ itd.getRoot_msp_org_1_sub_msp_1_monitor_user_1_email(), password, itd.getRoot_msp_org_1_sub_msp_1_user_1(), "finished",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()),
						String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true", 403, "00100101" },
				{ itd.getRoot_msp_org_1_account_1_monitor_user_1_email(), password, itd.getRoot_msp_org_1_account_1_user_1(), "finished",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()),
						String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true", 403, "00100101" },
				{ itd.getMsp_org_1_monitor_user_1_email(), password, itd.getMsp_org_1_user_1(), "finished",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()),
						String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true", 403, "00100101" },
				{ itd.getDirect_org_1_monitor_user_1_email(), password, itd.getDirect_org_1_user_1(), "finished",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()),
						String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true", 403, "00100101" },
				
		};}

	@Test(dataProvider = "create_job_filter_auth_error")
	public void createJobFilterAuthError(String userName, String password, String userID, String jobStatus, String policyID, String resourceID, String jobType, String startTimeType, String startTimeTSStart, 
			String startTimeTSEnd, String sourceName, String filterName, String isDefault, int statusCode, String errorCode) {
		spogServer.userLogin(userName, password);
		
		spogJobServer.setToken(spogServer.getJWTToken());
		spogJobServer.createJobFilterWithCodeCheckEx(userID, jobStatus, policyID, resourceID, jobType, startTimeType, 
				startTimeTSStart, startTimeTSEnd, sourceName, filterName, isDefault, statusCode, errorCode, test);
	}
	
	@DataProvider(name = "create_job_filter_no_token")
	public final Object[][] createJobFilterNoTokenParams() {
		return new Object[][] {
				{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "finished",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()),
						String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true", 401,
						"00900006" },
				{ itd.getMsp_org_1_user_2_email(), password, itd.getMsp_org_1_user_2(), "finished",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()),
						String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true", 401,
						"00900006" },
				{ itd.getMsp_org_1_sub_1_user_2_email(), password, itd.getMsp_org_1_sub_1_user_2(), "finished",
						UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()),
						String.valueOf(System.currentTimeMillis()), "sourceName", "filterName", "true", 401,
						"00900006" },
		};}
	@Test(dataProvider = "create_job_filter_no_token")
	public void createJobFilterNoToken(String userName, String password, String userID, String jobStatus, String policyID, String resourceID, String jobType, String startTimeType, String startTimeTSStart, 
			String startTimeTSEnd, String sourceName, String filterName, String isDefault, int statusCode, String errorCode) {
		spogServer.userLogin(userName, password);
		
		spogJobServer.setToken(spogServer.getJWTToken());
		spogJobServer.setToken("");
		spogJobServer.createJobFilterWithCodeCheckEx(userID, jobStatus, policyID, resourceID, jobType, startTimeType, 
				startTimeTSStart, startTimeTSEnd, sourceName, filterName, isDefault, statusCode, errorCode, test);
	}
	
	@DataProvider(name = "create_job_filter_is_default")
	public final Object[][] createJobFilterIsDefaultParams() {
		return new Object[][] {
		{ itd.getDirect_org_1_user_2_email(), password, itd.getDirect_org_1_user_2(), "finished",
			UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()),
			String.valueOf(System.currentTimeMillis()),  "sourceName", "filterName"}, 
		{ itd.getMsp_org_1_user_2_email(), password, itd.getMsp_org_1_user_2(), "finished",
			UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup","custom", String.valueOf(System.currentTimeMillis()),
			String.valueOf(System.currentTimeMillis()), "sourceName", "filterName"}, 
		{ itd.getMsp_org_1_sub_1_user_2_email(), password, itd.getMsp_org_1_sub_1_user_2(), "finished",
			UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup","custom", String.valueOf(System.currentTimeMillis()),
			 String.valueOf(System.currentTimeMillis()), "sourceName", "filterName"}, 
		};}
	
	@Test (dataProvider = "create_job_filter_is_default")
	public void checkFilterIsDefault(String userName, String password, String userID, String jobStatus, String policyID, String resourceID, String jobType, String startTimeType, 
			String startTimeTSStart, String startTimeTSEnd, String sourceName, String filterName) {
		spogServer.userLogin(userName, password);
		spogJobServer.setToken(spogServer.getJWTToken());
		String filterID1 = spogJobServer.createJobFilterWithCheckEx(userID, jobStatus, policyID, resourceID, jobType, startTimeType, 
				startTimeTSStart, startTimeTSEnd, sourceName, filterName, "true", test);
		String filterID2 = spogJobServer.createJobFilterWithCheckEx(userID, jobStatus, policyID, resourceID, jobType, startTimeType, 
				startTimeTSStart, startTimeTSEnd, sourceName, filterName + "added", "true", test);
		Response response = spogJobServer.getJobFilterByID(userID, filterID1, test);
		response.then().body("data.is_default", equalTo(false));
		response = spogJobServer.getJobFilterByID(userID, filterID2, test);
		response.then().body("data.is_default", equalTo(true));
		spogJobServer.deleteJobFilterByFilterID(userID, filterID2, spogServer.getJWTToken(), 200, null, test);
		String filterID3 = spogJobServer.createJobFilterWithCheckEx(userID, jobStatus, policyID, resourceID, jobType, startTimeType, 
				startTimeTSStart, startTimeTSEnd, sourceName, filterName + "added", "true", test);
		response = spogJobServer.getJobFilterByID(userID, filterID1, test);
		response.then().body("data.is_default", equalTo(false));
		response = spogJobServer.getJobFilterByID(userID, filterID3, test);
		response.then().body("data.is_default", equalTo(true));
	}
	
	@DataProvider(name = "csr_readonly_param")
	public final Object[][] csr_readonly_param() {
		return new Object[][] {
			// different policy ID format
			{itd.getDirect_org_1_user_2()},
			{itd.getMsp_org_1_user_2()},
			{itd.getMsp_org_1_sub_1_user_1()},
		};}
	
	@Test(dataProvider = "csr_readonly_param")
	public void csrReadonlyTest(String userID) {
		spogServer.userLogin(csr_readonly_email, csr_readonly_password);
		spogJobServer.setToken(spogServer.getJWTToken());
		//(filter_name, protection_status, connection_status, protection_policy, backup_status, source_group, operating_system, site_id, source_name, source_type, is_default, test);
		spogJobServer.createJobFilterWithCodeCheckEx(userID, "finished",
				UUID.randomUUID().toString(), UUID.randomUUID().toString(), "backup", "custom", String.valueOf(System.currentTimeMillis()),
				String.valueOf(System.currentTimeMillis()), "sourceName", "filterName"+RandomStringUtils.randomAlphanumeric(8), "true", 403, "00100101", test);
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
