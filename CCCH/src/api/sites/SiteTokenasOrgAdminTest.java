package api.sites;

import org.testng.annotations.Test;
import com.google.inject.PrivateBinder;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;
import com.sun.mail.handlers.message_rfc822;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGHypervisorsServer;
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

import static io.restassured.RestAssured.given;
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

import Constants.ConnectionStatus;
import Constants.OSMajor;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;

public class SiteTokenasOrgAdminTest extends base.prepare.Is4Org{

	private SPOGServer spogServer;
	private UserSpogServer userSpogServer;
	private SPOGHypervisorsServer spogHypervisorsServer;
	private GatewayServer gatewayServer;
	private SPOGDestinationServer spogDestinationServer;
	
	private String csrAdmin;
	private String csrPwd;
//	private ExtentReports rep;
	private ExtentTest test;
//	private SQLServerDb bqdb1;
//	public int Nooftest;
//	private long creationTime;
//	private String BQName = null;
//	private String runningMachine;
//	private testcasescount count1;
//	private String buildVersion;
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
		gatewayServer = new GatewayServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		spogHypervisorsServer = new SPOGHypervisorsServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		
	}
	
	@Test
	public void siteTokenTest() {
		
		String username = "zhaoguo.ma@arcserve.com";
		String userpassword = "Zetta1234";

		
		/**
		 * this.siteID = "a47b216f-17c5-422d-bede-a7f309124612";
		this.organizationID = "dee4021e-f854-486e-a9b4-080abd11840d";
		this.cloudAccountSecret = "0c24f474-6d1b-4922-8652-3f05df8afe86";
		this.baas_destionation_ID_normal = "2cd4567f-4319-4737-b4a7-361c2e53962f";
		 */
		
		String site_id = "a47b216f-17c5-422d-bede-a7f309124612";
		String organizationID = "dee4021e-f854-486e-a9b4-080abd11840d";
		String cloudAccountSecret = "0c24f474-6d1b-4922-8652-3f05df8afe86";
		String destinationID = "2cd4567f-4319-4737-b4a7-361c2e53962f";
		
		String prefix = RandomStringUtils.randomAlphanumeric(8);
			
		Response response = gatewayServer.LoginSite(site_id, cloudAccountSecret, test);
		String token = response.then().extract().path("data.token");

		spogServer.setToken(token);
	
		// GET Sites
		spogServer.getSites();
		
		// GET CloudAccount
		spogServer.getCloudAccounts(token, "", test);
//		String cloudaccount_id = spogServer.createCloudAccountWithCheck("cloudAccountKey" + prefix,
//				"cloudAccountSecret", "cloudAccountName" + prefix, "aws_s3", organization_id, "none", "none", "none",
//				"none", test);
		
		// Create User
		spogServer.createUserAndCheck(prefix + "email@arcserve.com", userpassword, "firstname", "lastname",
				SpogConstants.DIRECT_ADMIN, organizationID, test);
		
		// GET organization
		spogServer.GetOrganizationInfobyIDWithCheck(organizationID, "zhaoguo - direct", "direct", test);
		
		// POST Resource
		String resourceID = spogServer.createSourceWithCheck(prefix + "source_name", SourceType.machine, SourceProduct.cloud_direct, organizationID,
				site_id, ProtectionStatus.unprotect, ConnectionStatus.offline, "windows", "SQL_SERVER", test);

		spogDestinationServer.setToken(token);
		
		// POST Destination
//		String destinationID = spogDestinationServer.createDestinationWithCheck(null,
//				organizationID, cloudaccount_id_init, "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", "cloud_direct_volume", "running",
//				"9", cloudaccount_id_init, "normal", "wanle05-win7", "2M", "2M", "0", "0", "31", "0", "2", "0", null,
//				null,null,null, prefix + "destination_name",test);
		
		long startTimeTS = System.currentTimeMillis();
		long endTimeTS = System.currentTimeMillis();
		String serverID = UUID.randomUUID().toString();
		String rpsID = UUID.randomUUID().toString();
		String jobType = "backup";
		String jobMethod = "incremental";
		String jobStatus = "finished";
		String policyID = UUID.randomUUID().toString();
		
		// POST job
		String jobID = UUID.randomUUID().toString();
		gatewayServer.postJobWithCheck(jobID, startTimeTS, endTimeTS, organizationID, serverID, resourceID,
				rpsID, destinationID, policyID, jobType, "none", jobStatus, token, test);
		
		// POST jobdata
//		gatewayServer.postJobDataWithCheck(jobID, "none", "information", "20", "none", "none", "none", "none", "none",
//				"10", "20", "none", "none", "none", "none", "none", "none", "none", "none", token, test);
		
		String message_date = "spog,QA" ;
		String SeverityType = "information";
		String logSourceType = "spog";
		String message_id = "testLogMessage";
		// POST Log
//		gatewayServer.createLogwithCheck(startTimeTS,jobID, organizationID, organizationID, resourceID, SeverityType,
//				logSourceType, message_id, message_date, token, test); 

		spogHypervisorsServer.setToken(token);
		// post Hypervisor
		String hypervisorId = spogHypervisorsServer.createHypervisorWithCheck("none", prefix + "hypervisor_name1", "vmware", "cloud_direct", "none", site_id, organizationID, "false", String.valueOf(System.currentTimeMillis()), 
				destinationID, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", test);
		spogServer.userLogin(username, userpassword);
		spogHypervisorsServer.setToken(spogServer.getJWTToken());
		spogHypervisorsServer.deleteHypervisorWithCheck(hypervisorId, test);
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
