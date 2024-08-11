package api.backupjobreports.columns;

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
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGJobServer;
import InvokerServer.SPOGReportServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class GetSystemBackUpJobReportsColumnsTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private SPOGReportServer spogReportServer;

	private ExtentTest test;
	private TestOrgInfo ti;

	LocalDate date = LocalDate.now();
	LocalDate yesterday = date.minusDays(1);
	LocalDate tomorrow = yesterday.plusDays(2);

	ArrayList<String> columnIdList = new ArrayList<String>();
	ArrayList<String> MSPColumnIdList = new ArrayList<String>();

	ArrayList<HashMap<String, Object>> columnsHeadContent = new ArrayList<HashMap<String, Object>>();
	ArrayList<HashMap<String, Object>> MSPColumnsHeadContent = new ArrayList<HashMap<String, Object>>();

	@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runningMachine, String buildVersion) {

		spogServer = new SPOGServer(baseURI, port);
		spogReportServer = new SPOGReportServer(baseURI, port);
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
	}

	// This information is related to the destination information of the
	// cloudDedupe volume
	@DataProvider(name = "getSystemBackUpJobReportsColumnsTest_valid")
	public final Object[][] getSystemBackUpJobReportsColumnsTest_valid() {
		return new Object[][] { { ti.csr_readonly_token, "csrreadonly", ti.csr_org_id },
				{ ti.direct_org1_user1_token, "Direct", ti.direct_org1_id },
				{ ti.normal_msp_org1_user1_token, "MSP", ti.normal_msp_org1_id },
				{ ti.normal_msp1_suborg1_user1_token, "SUB", ti.normal_msp1_suborg1_id },
				{ ti.normal_msp_org1_msp_accountadmin1_token, "SUB_msp", ti.normal_msp1_suborg1_id },

				// 3 tier cases
				{ ti.root_msp_org1_user1_token, "root_msp", ti.root_msp_org1_id },
				{ ti.root_msp1_submsp1_user1_token, "sub_msp", ti.root_msp1_submsp_org1_id },
				{ ti.msp1_submsp1_suborg1_user1_token, "submsp_sub", ti.msp1_submsp1_sub_org1_id },

				// monitor cases
				{ ti.root_msp_org1_monitor_user1_token, "root_msp_monitor", ti.root_msp_org1_id },
				{ ti.root_msp1_submsp1_monitor_user1_token, "sub_msp_monitor", ti.root_msp1_submsp_org1_id },
				{ ti.msp1_submsp1_suborg1_monitor_user1_token, "submsp_sub_monitor", ti.msp1_submsp1_sub_org1_id },
				{ ti.direct_org1_monitor_user1_token, "Direct_monitor", ti.direct_org1_id },
				{ ti.normal_msp_org1_monitor_user1_token, "MSP_monitor", ti.normal_msp_org1_id },
				{ ti.normal_msp1_suborg1_monitor_user1_token, "SUB_monitor", ti.normal_msp1_suborg1_id }, };
	}

	@Test(dataProvider = "getSystemBackUpJobReportsColumnsTest_valid")
	public void getSystemBackUpJobReportsColumnsTest_valid(String adminToken, String organization_type,
			String organization_id) {

		test = ExtentManager.getNewTest(organization_type + "Organization getSystemBackUpJobReportsColumnsTest_valid");

		test.log(LogStatus.INFO, "get the system predefined backup job reports columns with valid token");
		Response response = spogReportServer.getSystemBackUpJobReportsColumns(adminToken, organization_type,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

	}

	@DataProvider(name = "getSystemBackUpJobReportsColumnsTest_invalid")
	public final Object[][] getSystemBackUpJobReportsColumnsTest_invalid() {
		return new Object[][] {
				{ "Invalid Authorization with junk token", "Junk", "Direct", ti.direct_org1_id,
						SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT },
				{ "Invalid Authorization with missing token", "", "MSP", ti.normal_msp_org1_id,
						SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED }, };
	}

	@Test(dataProvider = "getSystemBackUpJobReportsColumnsTest_invalid")
	public void getSystemBackUpJobReportsColumnsTest_invalid(String invalidTestCase, String InValidToken,
			String organization_type, String organization_id, int ExpectedStatusCode, SpogMessageCode Errormessage

	) {

		test = ExtentManager.getNewTest(
				organization_type + "Organization getSystemBackUpJobReportsColumnsTest_invalid" + invalidTestCase);

		test.log(LogStatus.INFO, "get the backup job reports columns with invalid token");
		Response response = spogReportServer.getSystemBackUpJobReportsColumns(InValidToken, organization_type,
				ExpectedStatusCode, Errormessage, test);

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
