package api.users.recoveredresourcesfilters;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.SPOGJobServer;
import InvokerServer.SPOGRecoveredResourceServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class GetSystemRecoveredResourcesFiltersTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private SPOGJobServer spogJobServer;
	private SPOGRecoveredResourceServer spogRecoveredResourceServer;
	private ExtentTest test;
	private TestOrgInfo ti;

	@BeforeClass
	@Parameters({ "baseURI", "port", "csrAdminUserName", "csrAdminPassword", "csrReadOnlyAdminName",
			"csrReadOnlyAdminPassword", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword,
			String readOnlyUserName, String readOnlyPassword, String logFolder, String runningMachine,
			String buildVersion) {

		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogJobServer = new SPOGJobServer(baseURI, port);
		spogRecoveredResourceServer = new SPOGRecoveredResourceServer(baseURI, port);
		rep = ExtentManager.getInstance(this.getClass().getSimpleName(), logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Ramya.Nagepalli";

		Nooftest = 0;
		// Used for creating a build number with dateformat
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date = new java.util.Date();
		this.buildVersion = buildVersion + "_" + dateFormater.format(date);

		BQName = this.getClass().getSimpleName();
		this.runningMachine = runningMachine;
		System.out.println("The value of hit is " + count1.isstarttimehit());

		if (count1.isstarttimehit() == 0) {
			System.out.println("into creation time");
			creationTime = System.currentTimeMillis();
			count1.setcreationtime(creationTime);

			// creationTime = System.currentTimeMillis();
			try {
				bqdb1.updateTable(BQName, this.runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0",
						String.valueOf(Nooftest), count1.getcreationtime(), "InProgress",
						author + " and Rest server is " + baseURI.split("//")[1]);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		ti = new TestOrgInfo(spogServer, test);
		spogServer.setToken(ti.csr_token);

	}

	@DataProvider(name = "get_system_RecoveredResources_filter_valid")
	public final Object[][] getSystemrecoveredresourcesFilterValidParams() {

		return new Object[][] {

				{ "CSR", ti.csr_readonly_token, ti.csr_readonly_admin_user_id, ti.csr_org_id },
				{ "DIRECT", ti.direct_org2_user1_token, ti.direct_org2_user1_id, ti.direct_org2_id },
				{ "MSP", ti.normal_msp_org2_user1_token, ti.normal_msp_org2_user1_id, ti.normal_msp_org2_id },
				{ "SUBORG", ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_user1_id,
						ti.normal_msp1_suborg1_id },
				{ "Root_MSP", ti.root_msp_org1_user1_token, ti.root_msp_org1_user1_id, ti.root_msp_org1_id },
				{ "SUB_MSP", ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id,
						ti.root_msp1_submsp_org1_id },
				{ "SUB_MSP-sub", ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id,
						ti.msp1_submsp1_sub_org1_id },
				{ "DIRECT_monitor", ti.direct_org2_monitor_user1_token, ti.direct_org2_monitor_user1_id,
						ti.direct_org2_id },
				{ "MSP_monitor", ti.normal_msp_org2_monitor_user1_token, ti.normal_msp_org2_monitor_user1_id,
						ti.normal_msp_org2_id },
				{ "SUBORG_monitor", ti.normal_msp1_suborg1_monitor_user1_token, ti.normal_msp1_suborg1_monitor_user1_id,
						ti.normal_msp1_suborg1_id },
				{ "Root_MSP_monitor", ti.root_msp_org1_monitor_user1_token, ti.root_msp_org1_monitor_user1_id,
						ti.root_msp_org1_id },
				{ "SUB_MSP_monitor", ti.root_msp1_submsp1_monitor_user1_token, ti.root_msp1_submsp1_monitor_user1_id,
						ti.root_msp1_submsp_org1_id },
				{ "SUB_MSP-sub_monitor", ti.msp1_submsp1_suborg1_monitor_user1_token,
						ti.msp1_submsp1_suborg1_monitor_user1_id, ti.msp1_submsp1_sub_org1_id }, };

	}

	// get the user defined job columns using csr/valid token
	@Test(dataProvider = "get_system_RecoveredResources_filter_valid")
	public void getSystemRecoveredResourcesFilterValid_200(String organizationType, String validToken, String user_id,
			String org_id) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		test.assignAuthor("");

		spogRecoveredResourceServer.setToken(validToken);

		test.log(LogStatus.INFO, "get System RecoveredResources Filter in org:  " + organizationType
				+ "and compare with default filters");

		Response response = spogRecoveredResourceServer.getSystemRecoveredResourcesFilters(user_id, org_id, validToken,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

	}

	// get the user defined job columns using csr/valid token
	@Test(dataProvider = "get_system_RecoveredResources_filter_valid")
	public void getSystemRecoveredResourcesFilter_401(String organizationType, String validToken, String user_id,
			String org_id) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		test.assignAuthor("");

		spogRecoveredResourceServer.setToken(validToken);

		test.log(LogStatus.INFO, "get System RecoveredResources Filter in org: " + organizationType);

		String Not_valid_Token = validToken + "Abc";

		test.log(LogStatus.INFO, "get the RecoveredResources Filter in org: with the InValid Token");

		Response response = spogRecoveredResourceServer.getSystemRecoveredResourcesFilters(user_id, org_id,
				Not_valid_Token, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT,
				test);

		String MissingToken = "";

		test.log(LogStatus.INFO, "get the RecoveredResources Filter in org: with the MissingToken ");

		response = spogRecoveredResourceServer.getSystemRecoveredResourcesFilters(user_id, org_id, MissingToken,
				SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);

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
		rep.flush();
	}

	/******************************************************************
	 * RandomFunction
	 ******************************************************************************/
	public int gen_random_index(String[] job_status) {
		Random generator = new Random();
		int randomindx = generator.nextInt(job_status.length);

		return randomindx;
	}

}
