package api.reports.reportfilters;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

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
import InvokerServer.SPOGReportServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;

public class GetReportFiltersTest extends base.prepare.Is4Org {
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

	@DataProvider(name = "getreportfilters", parallel = false)
	public final Object[][] getreportlist() {

		return new Object[][] { { ti.direct_org1_user1_token }, { ti.normal_msp_org1_user1_token },
				{ ti.normal_msp1_suborg1_user1_token }, { ti.normal_msp_org1_msp_accountadmin1_token },
				{ ti.root_msp_org1_user1_token }, { ti.root_msp1_suborg1_user1_token },
				{ ti.root_msp_org1_msp_accountadmin1_token }, { ti.msp1_submsp1_suborg1_user1_token },
				{ ti.root_msp1_submsp1_user1_token }, { ti.root_msp1_submsp1_account_admin_token },
				{ ti.csr_readonly_token }, { ti.direct_org1_monitor_user1_token },
				{ ti.normal_msp_org1_monitor_user1_token }, { ti.normal_msp1_suborg1_monitor_user1_token },
				{ ti.root_msp_org1_monitor_user1_token }, { ti.root_msp1_suborg1_monitor_user1_token },
				{ ti.msp1_submsp1_suborg1_monitor_user1_token }, { ti.root_msp1_submsp1_monitor_user1_token }, };
	}

	// Valid Cases - 200

	@Test(dataProvider = "getreportfilters")
	public void getReportFilters_200(String validToken) {

		ArrayList<HashMap<String, Object>> reportfilters = new ArrayList<>();
		HashMap<String, Object> date_range1 = new HashMap<>();
		HashMap<String, Object> date_range2 = new HashMap<>();
		HashMap<String, Object> date_range3 = new HashMap<>();

		HashMap<String, Object> filter1 = new HashMap<>();
		HashMap<String, Object> filter2 = new HashMap<>();
		HashMap<String, Object> filter3 = new HashMap<>();

		date_range1.put("type", "last_7_days");
		date_range1.put("start_ts", "0");
		date_range1.put("end_ts", "0");

		filter1.put("date_range", date_range1);
		filter1.put("filter_id", "0009433d-d128-4d02-bcd9-263cf4097596");
		filter1.put("filter_name", "Last Week");
		filter1.put("user_id", "b8d38069-77c0-4620-9290-4868643d29f6");
		filter1.put("organization_id", "76abacd1-4d9c-4b16-b729-982784055313");
		filter1.put("create_ts", 1526649225);
		filter1.put("modify_ts", 1526649225);
		filter1.put("filter_type", "report_filter_global");
		filter1.put("is_default", true);
		filter1.put("count", 0);
		filter1.put("view_type", "origin");

		// Composing filter2
		date_range2.put("type", "last_1_month");
		date_range2.put("start_ts", "0");
		date_range2.put("end_ts", "0");

		filter2.put("date_range", date_range2);
		filter2.put("filter_id", "000a9265-b2cb-4473-b1a6-7b95726709f0");
		filter2.put("filter_name", "Last Month");
		filter2.put("user_id", "b8d38069-77c0-4620-9290-4868643d29f6");
		filter2.put("organization_id", "76abacd1-4d9c-4b16-b729-982784055313");
		filter2.put("create_ts", 1526649225);
		filter2.put("modify_ts", 1526649225);
		filter2.put("filter_type", "report_filter_global");
		filter2.put("is_default", true);
		filter2.put("count", 0);
		filter2.put("view_type", "origin");

		// Composing filter3
		date_range3.put("type", "last_24_hours");
		date_range3.put("start_ts", "0");
		date_range3.put("end_ts", "0");

		filter3.put("date_range", date_range3);
		filter3.put("filter_id", "0008accc-0405-440e-bf88-a923271cc2fe");
		filter3.put("filter_name", "Daily Digest");
		filter3.put("user_id", "b8d38069-77c0-4620-9290-4868643d29f6");
		filter3.put("organization_id", "76abacd1-4d9c-4b16-b729-982784055313");
		filter3.put("create_ts", 1526649224);
		filter3.put("modify_ts", 1526649224);
		filter3.put("filter_type", "report_filter_global");
		filter3.put("is_default", true);
		filter3.put("count", 0);
		filter3.put("view_type", "origin");

		// Preparing an array with all the default filters
		reportfilters.add(filter1);
		reportfilters.add(filter2);
		reportfilters.add(filter3);
		test.log(LogStatus.INFO, "Get Filter List for Reports");
		spogReportServer.getSystemReportFiltersWithCheck(validToken, reportfilters,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
	}

	// Invalid cases - 401

	@Test
	public void getReportFilters_401() {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());

		test.log(LogStatus.INFO, "Get Filter for Reports with invalid token");
		spogReportServer.getSystemReportFiltersWithCheck("junk", null, SpogConstants.NOT_LOGGED_IN,
				SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);

		test.log(LogStatus.INFO, "Get Filter for Reports with missing token");
		spogReportServer.getSystemReportFiltersWithCheck("", null, SpogConstants.NOT_LOGGED_IN,
				SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);

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
