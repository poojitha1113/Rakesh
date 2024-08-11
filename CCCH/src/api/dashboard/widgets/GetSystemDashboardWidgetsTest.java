package api.dashboard.widgets;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

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
import InvokerServer.SPOGJobServer;
import InvokerServer.SPOGReportServer;
import InvokerServer.SPOGServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;

public class GetSystemDashboardWidgetsTest extends base.prepare.Is4Org {

	private SPOGServer spogServer;
	private SPOGReportServer spogReportServer;
	private ExtentTest test;

	private TestOrgInfo ti;

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

	@DataProvider(name = "getSystemDashboardWidgets_valid")
	public final Object[][] getSystemDashboardWidgets_valid() {
		return new Object[][] {
				{ "csrreadonly", ti.csr_readonly_token, SpogConstants.SUCCESS_GET_PUT_DELETE,
						SpogMessageCode.SUCCESS_GET_PUT_DEL },
				{ "CSR", ti.csr_token, SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL },
				{ "Direct", ti.direct_org1_user1_token, SpogConstants.SUCCESS_GET_PUT_DELETE,
						SpogMessageCode.SUCCESS_GET_PUT_DEL },
				{ "MSP", ti.normal_msp_org1_user1_token, SpogConstants.SUCCESS_GET_PUT_DELETE,
						SpogMessageCode.SUCCESS_GET_PUT_DEL },
				{ "suborg", ti.normal_msp1_suborg1_user1_token, SpogConstants.SUCCESS_GET_PUT_DELETE,
						SpogMessageCode.SUCCESS_GET_PUT_DEL },

				// 3 tier cases
				{ "RootMSP", ti.root_msp_org1_user1_token, SpogConstants.SUCCESS_GET_PUT_DELETE,
						SpogMessageCode.SUCCESS_GET_PUT_DEL },
				{ "RootSub", ti.root_msp1_suborg1_user1_token, SpogConstants.SUCCESS_GET_PUT_DELETE,
						SpogMessageCode.SUCCESS_GET_PUT_DEL },
				{ "submsp_sub", ti.msp1_submsp1_suborg1_user1_token, SpogConstants.SUCCESS_GET_PUT_DELETE,
						SpogMessageCode.SUCCESS_GET_PUT_DEL },
				{ "RootMSPAccountAdmin", ti.root_msp1_submsp1_account_admin_token, SpogConstants.SUCCESS_GET_PUT_DELETE,
						SpogMessageCode.SUCCESS_GET_PUT_DEL },
				{ "SubMSP", ti.root_msp1_submsp1_user1_token, SpogConstants.SUCCESS_GET_PUT_DELETE,
						SpogMessageCode.SUCCESS_GET_PUT_DEL },
				// monitor cases

				{ "Direct_monitor", ti.direct_org1_monitor_user1_token, SpogConstants.SUCCESS_GET_PUT_DELETE,
						SpogMessageCode.SUCCESS_GET_PUT_DEL },
				{ "MSP_monitor", ti.normal_msp_org1_monitor_user1_token, SpogConstants.SUCCESS_GET_PUT_DELETE,
						SpogMessageCode.SUCCESS_GET_PUT_DEL },
				{ "suborg_monitor", ti.normal_msp1_suborg1_monitor_user1_token, SpogConstants.SUCCESS_GET_PUT_DELETE,
						SpogMessageCode.SUCCESS_GET_PUT_DEL },
				{ "RootMSP_monitor", ti.root_msp_org1_monitor_user1_token, SpogConstants.SUCCESS_GET_PUT_DELETE,
						SpogMessageCode.SUCCESS_GET_PUT_DEL },
				{ "RootSub_monitor", ti.root_msp1_suborg1_monitor_user1_token, SpogConstants.SUCCESS_GET_PUT_DELETE,
						SpogMessageCode.SUCCESS_GET_PUT_DEL },
				{ "submsp_sub_monitor", ti.msp1_submsp1_suborg1_monitor_user1_token,
						SpogConstants.SUCCESS_GET_PUT_DELETE, SpogMessageCode.SUCCESS_GET_PUT_DEL },
				{ "SubMSP_monitor", ti.root_msp1_submsp1_monitor_user1_token, SpogConstants.SUCCESS_GET_PUT_DELETE,
						SpogMessageCode.SUCCESS_GET_PUT_DEL },

		};
	}

	@Test(dataProvider = "getSystemDashboardWidgets_valid")
	public void getSystemDashboardWidgets(String organization_type, String validToken, int spogErrorCode,
			SpogMessageCode ErrorMessage) {
		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "for Organization" + organization_type);

		test.assignAuthor("Ramya.Nagepalli");

		test.log(LogStatus.INFO,
				"Get system default widgets of Organization of type" + organization_type + "with valid token");
		spogReportServer.getSystemDashboardWidgets(validToken, spogErrorCode, ErrorMessage, test);

	}

	@DataProvider(name = "getSystemDashboardWidgets_invalid")
	public final Object[][] getSystemDashboardWidgets_invalid() {
		return new Object[][] {
				{ "getSystemDashboardWidgets with invalid token", "1234", SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT },
				{ "getSystemDashboardWidgets with missed token", "", SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTHENTICATION_FAILED } };

	}

	@Test(dataProvider = "getSystemDashboardWidgets_invalid")
	public void getSystemDashboardWidgets_401(String testcase, String token, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage) {
		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());

		test.assignAuthor("Ramya.Nagepalli");

		test.log(LogStatus.INFO, "invalid Scenario:" + testcase);

		spogReportServer.getSystemDashboardWidgets(token, expectedStatusCode, expectedErrorMessage, test);

	}

	@AfterMethod
	public void getResult(ITestResult result) {
		if (result.getStatus() == ITestResult.FAILURE) {
			count1.setfailedcount();
			test.log(LogStatus.FAIL, "Test Case Failed is " + result.getName() + " with parameters as "
					+ Arrays.asList(result.getParameters()));
			test.log(LogStatus.FAIL, result.getThrowable().getMessage());

		} else if (result.getStatus() == ITestResult.SKIP) {
			test.log(LogStatus.SKIP, "Test Case Skipped is " + result.getName());
			count1.setskippedcount();
		} else if (result.getStatus() == ITestResult.SUCCESS) {
			count1.setpassedcount();

		}
		rep.endTest(test);
		rep.flush();
	}

}
