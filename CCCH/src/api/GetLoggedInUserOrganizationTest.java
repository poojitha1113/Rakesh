package api;

import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.ErrorCode;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.Org4SPOGServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import bsh.org.objectweb.asm.Constants;
import dataPreparation.JsonPreparation;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import groovyjarjarantlr.PreservingFileWriter;
import io.restassured.response.Response;

import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;

public class GetLoggedInUserOrganizationTest extends base.prepare.Is4Org {
	
	private SPOGServer spogServer;
	private JsonPreparation jp;
	private TestOrgInfo ti;
	private ExtentTest test;
	protected ExtentReports rep;

	int passedcases = 0;
	int failedcases = 0;
	int skippedcases = 0;


	String csr_token, csr_org_id;
	String prefix = RandomStringUtils.randomAlphabetic(4);
	String userEmail =prefix+"eswari.sykam100@gmail.com";
	@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runningMachine,
			String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		jp = new JsonPreparation();
		rep = ExtentManager.getInstance(getClass().getName(), logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Sykam, Naga Malleswari";
		ti = new TestOrgInfo(spogServer, test);
		Nooftest = 0;
		// Used for creating a build number with dateformat
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date = new java.util.Date();
		this.buildVersion = buildVersion + "_" + dateFormater.format(date);

		BQName = this.getClass().getSimpleName();
		this.runningMachine = runningMachine;
		System.out.println("The value of hit is " + count1.isstarttimehit());

		ti = new TestOrgInfo(spogServer, test);


		/*if (count1.isstarttimehit() == 0) {
			System.out.println("into creation time");
			creationTime = System.currentTimeMillis();
			count1.setcreationtime(creationTime);

			try {
				bqdb1.updateTable(BQName, this.runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0",
						String.valueOf(Nooftest), count1.getcreationtime(), "InProgress",
						author + " and Rest server is " + baseURI.split("//")[1]);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}*/

	}



	@DataProvider(name = "getOrgInfo")
	public final Object[][]getOrgInfoParams() {

		return new Object[][] {
			//200 Scenarios
			{ "Get organization Info for logged in user",ti.direct_org1_user1_token,ti.direct_org1_id,ti.csr_admin_user_id,SpogConstants.DIRECT_ORG,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{ "Get organization Info for logged in user of normal msp",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.csr_admin_user_id,SpogConstants.MSP_ORG,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{ "Get organization Info for logged in user normal msp account admin",ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_id,ti.normal_msp_org1_user1_id,SpogConstants.MSP_ORG,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{ "Get organization Info for logged in user of customer account of normal msp ",ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_id,ti.csr_admin_user_id,SpogConstants.MSP_SUB_ORG,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{ "Get organization Info for logged in user of root normal msp ",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.csr_admin_user_id,SpogConstants.MSP_ORG,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{ "Get organization Info for logged in user of root normal msp account admin user ",ti.root_msp_org1_msp_accountadmin1_token,ti.root_msp_org1_id,ti.root_msp_org1_user1_id,SpogConstants.MSP_ORG,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{ "Get organization Info for logged in user of customer account of root msp user  ",ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_id,ti.csr_admin_user_id,SpogConstants.MSP_SUB_ORG,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{ "Get organization Info for logged in user of sub normal msp ",ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp_org1_id,ti.csr_admin_user_id,SpogConstants.MSP_ORG,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{ "Get organization Info for logged in user of root normal msp account admin user ",ti.root_msp1_submsp1_account_admin_token,ti.root_msp_org1_id,ti.root_msp1_submsp1_user1_id,SpogConstants.MSP_ORG,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{ "Get organization Info for logged in user of customer account of root msp user  ",ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_sub_org1_id,ti.csr_admin_user_id,SpogConstants.MSP_SUB_ORG,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			
		};
	}

	@Test(dataProvider = "getOrgInfo", enabled = true)
	public void getOrganizationDetailsForLoggedInUser(String testCase,
			String token,
			String ogranization_id,
			String user_id,
			String org_type,
			int expectedstatuccode,
			SpogMessageCode errormessage
			) {

		test = ExtentManager.getNewTest(testCase);

		test.log(LogStatus.INFO, "Compose info for create soucre group");
		HashMap<String, Object> composeOrgInfo = jp.composeGetOrganizationInfo(ogranization_id, user_id, org_type);
			
		spogServer.setToken(token);
		test.log(LogStatus.INFO, "Get OrganizationInfo For LoggedInuser");
		spogServer.getOrganizationInfoWithCheck(token, composeOrgInfo, expectedstatuccode, errormessage, test);

	}

	
	
	@DataProvider(name = "getOrgInfo_Invalid")
	public final Object[][]getOrgInfoInvalidParams() {

		return new Object[][] {
			//200 Scenarios
			{ "Get organization Info for logged in user",ti.direct_org1_user1_token,ti.direct_org1_id,ti.csr_admin_user_id,SpogConstants.DIRECT_ORG,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{ "Get organization Info for logged in user of customer account of normal msp ",ti.normal_msp1_suborg1_user1_token,ti.normal_msp1_suborg1_id,ti.csr_admin_user_id,SpogConstants.MSP_SUB_ORG,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{ "Get organization Info for logged in user of customer account of root msp user  ",ti.root_msp1_suborg1_user1_token,ti.root_msp1_suborg1_id,ti.normal_msp_org1_user1_id,SpogConstants.MSP_SUB_ORG,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{ "Get organization Info for logged in user of customer account of root msp user  ",ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_sub_org1_id,ti.root_msp1_submsp1_user1_id,SpogConstants.MSP_SUB_ORG,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			
		};
	}

	@Test(dataProvider = "getOrgInfo_Invalid", enabled = true)
	public void getOrganizationDetailsForLoggedInUser_Invlaiddetials(String testCase,
			String token,
			String ogranization_id,
			String user_id,
			String org_type,
			int expectedstatuccode,
			SpogMessageCode errormessage
			) {

		test = ExtentManager.getNewTest(testCase);

		test.log(LogStatus.INFO, "Compose info for create soucre group");
		HashMap<String, Object> composeOrgInfo = jp.composeGetOrganizationInfo(ogranization_id, user_id, org_type);
			
	
		test.log(LogStatus.INFO, "Get OrganizationInfo For LoggedInuser with no token");
		spogServer.setToken("");
		spogServer.getOrganizationInfoWithCheck("", composeOrgInfo, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);
		
	
		test.log(LogStatus.INFO, "Get OrganizationInfo For LoggedInuser with invalid token");
		spogServer.setToken("Invlaid");
		spogServer.getOrganizationInfoWithCheck("Invlaid", composeOrgInfo, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);
		
		

	}

	@AfterMethod
	public void getResult(ITestResult result) {
		if (result.getStatus() == ITestResult.FAILURE) {
			count1.setfailedcount();
			// remaincases=Nooftest-passedcases-failedcases;
			test.log(LogStatus.FAIL, "Test Case Failed is " + result.getName() + " with parameters as "
					+ Arrays.asList(result.getParameters()));
			test.log(LogStatus.FAIL, result.getThrowable().getMessage());

		} else if (result.getStatus() == ITestResult.SKIP) {
			test.log(LogStatus.SKIP, "Test Case Skipped is " + result.getName());
			count1.setskippedcount();
		} else if (result.getStatus() == ITestResult.SUCCESS) {
			count1.setpassedcount();
			// remaincases=Nooftest-passedcases-failedcases;

		}
		rep.endTest(test);
		
	}


}