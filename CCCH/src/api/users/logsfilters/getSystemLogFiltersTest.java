package api.users.logsfilters;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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
import InvokerServer.GatewayServer;
import InvokerServer.Log4SPOGServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;

public class getSystemLogFiltersTest extends base.prepare.Is4Org {

	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private Log4SPOGServer spogLogServer;
	private UserSpogServer userSpogServer;
	private TestOrgInfo ti;

	private ExtentTest test;
	

	int passedcases = 0;
	int failedcases = 0;
	int skippedcases = 0;
	private String org_model_prefix = this.getClass().getSimpleName();

	

	LocalDate date = LocalDate.now();
	LocalDate yesterday = date.minusDays(1);
	LocalDate tomorrow = yesterday.plusDays(2);

	@BeforeClass
	@Parameters({ "baseURI", "port","csrAdminUserName", "csrAdminPassword","csrReadOnlyAdminName","csrReadOnlyAdminPassword","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword,String csrReadOnlyAdminName,String csrReadOnlyAdminPassword, String logFolder,String runningMachine,String buildVersion) throws UnknownHostException {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogLogServer = new Log4SPOGServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		rep = ExtentManager.getInstance("GetspecifiedLogFilterForLoggedInUserTest", logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Malleswari.Sykam";

		Nooftest = 0;
		// Used for creating a build number with dateformat
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date = new java.util.Date();
		this.buildVersion = buildVersion + "_" + dateFormater.format(date);

		BQName = this.getClass().getSimpleName();
		this.runningMachine = runningMachine;
		System.out.println("The value of hit is " + count1.isstarttimehit());
		ti = new TestOrgInfo(spogServer, test);	
		if (count1.isstarttimehit() == 0) {
			System.out.println("into creation time");
			creationTime = System.currentTimeMillis();
			count1.setcreationtime(creationTime);

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
		
	}

	@DataProvider(name = "create_log_filter_valid")
	public final Object[][] createLogFilterValidParams() {
		return new Object[][] {

			{ "direct", ti.direct_org1_id, ti.direct_org1_user1_token },
			{ "Normal_Msp", ti.normal_msp_org1_id, ti.normal_msp_org1_user1_token },
			{ "Customer_account_Normal_Msp", ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_user1_token },
			{ "Sub_Msp", ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_user1_token },
			{ "Customer_account_sub_Msp", ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_token },
			{ "Root_Msp", ti.root_msp_org1_id, ti.root_msp_org1_msp_accountadmin1_token },
			{ "Customer_account_Root_Msp", ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_user1_token },
		};

	}

	@Test(dataProvider = "create_log_filter_valid")
	public void getLogFilterValid_200(String organizationType, String organization_id, String validToken) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + "_");

		spogLogServer.setToken(validToken);

		test.log(LogStatus.INFO, "Get log filters for a specified user id  with other valid token");
		spogLogServer.getSystemLogFilters(validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

	}

	@Test(dataProvider = "create_log_filter_valid")
	public void getLogFilterValid_401(String organizationType, String organization_id, String validToken) {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName() + "_");

		spogLogServer.setToken(validToken);

		test.log(LogStatus.INFO, "Get log filters for a specified user id  with other valid token");
		spogLogServer.getSystemLogFilters("", SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED,
				test);

		test.log(LogStatus.INFO, "Get log filters for a specified user id  with other valid token");
		spogLogServer.getSystemLogFilters(validToken + "a", SpogConstants.NOT_LOGGED_IN,
				SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);

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
