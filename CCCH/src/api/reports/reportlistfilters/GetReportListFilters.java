package api.reports.reportlistfilters;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
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

public class GetReportListFilters extends base.prepare.Is4Org {
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

	@DataProvider(name = "getreportlist", parallel = false)
	public final Object[][] getreportlist() {

		return new Object[][] { { ti.csr_token }, { ti.direct_org1_user1_token }, { ti.normal_msp_org1_user1_token },
				{ ti.normal_msp1_suborg1_user1_token }, { ti.normal_msp_org1_msp_accountadmin1_token },
				{ ti.root_msp_org1_user1_token }, { ti.root_msp1_suborg1_user1_token },
				{ ti.root_msp_org1_msp_accountadmin1_token }, { ti.msp1_submsp1_suborg1_user1_token },
				{ ti.root_msp1_submsp1_user1_token }, { ti.root_msp1_submsp1_account_admin_token },
				{ ti.csr_readonly_token } };
	}

	// Valid Cases - 200
	@Test(dataProvider = "getreportlist", enabled = false)
	public void getReportListFilters_200_old(String validToken) {

		ArrayList<HashMap<String, Object>> reportlistfilters = new ArrayList<>();
		ArrayList<String> date_range_type = new ArrayList<>();
		ArrayList<String> schedule_frequency = new ArrayList<>();
		HashMap<String, Object> report_for = new HashMap<>();

		HashMap<String, Object> filter1 = new HashMap<>();
		HashMap<String, Object> filter2 = new HashMap<>();
		HashMap<String, Object> filter3 = new HashMap<>();

		date_range_type.add("today");
		date_range_type.add("last_7_days");
		date_range_type.add("last_1_month");
		date_range_type.add("last_3_months");
		date_range_type.add("last_6_months");
		date_range_type.add("last_1_year");
		date_range_type.add("custom");

		schedule_frequency.add("daily");
		schedule_frequency.add("weekly");
		schedule_frequency.add("monthly");
		schedule_frequency.add("quarterly");

		// Composing filter1
		report_for.put("type", "all_sources");

		filter1.put("date_range_type", date_range_type);
		filter1.put("report_for", report_for);
		filter1.put("schedule_frequency", schedule_frequency);
		filter1.put("filter_id", "0000914e-a807-4ec8-b4c8-4e2606ec492c");
		filter1.put("filter_name", "Test_System_Report_List_Filter1");
		filter1.put("user_id", "b8d38069-77c0-4620-9290-4868643d29f6");
		filter1.put("organization_id", "76abacd1-4d9c-4b16-b729-982784055313");
		filter1.put("create_ts", 1525416674);
		filter1.put("modify_ts", 1525416674);
		filter1.put("is_default", true);
		filter1.put("count", 0);

		// Composing filter2
		report_for = new HashMap<>();
		report_for.put("type", "all_organizations");

		filter2.put("date_range_type", date_range_type);
		filter2.put("report_for", report_for);
		filter2.put("schedule_frequency", schedule_frequency);
		filter2.put("filter_id", "000612cf-85ba-497e-9bc0-5e8b5a09a822");
		filter2.put("filter_name", "Test_System_Report_List_Filter2");
		filter2.put("user_id", "b8d38069-77c0-4620-9290-4868643d29f6");
		filter2.put("organization_id", "76abacd1-4d9c-4b16-b729-982784055313");
		filter2.put("create_ts", 1525416674);
		filter2.put("modify_ts", 1525416674);
		filter2.put("is_default", false);
		filter2.put("count", 0);

		// Composing filter3
		report_for = new HashMap<>();
		report_for.put("type", "all_organizations");

		filter3.put("date_range_type", date_range_type);
		filter3.put("report_for", report_for);
		filter3.put("schedule_frequency", schedule_frequency);
		filter3.put("filter_id", "00061b5e-38a7-4bd5-b9da-7d3861d49a4e");
		filter3.put("filter_name", "Test Sys Filter");
		filter3.put("user_id", "0a112d28-6887-4cf3-baf1-aa322372df56");
		filter3.put("organization_id", "1d58e3a5-02b3-4e77-887d-a239504cc72c");
		filter3.put("create_ts", 1525416795);
		filter3.put("modify_ts", 1525416795);
		filter3.put("is_default", true);
		filter3.put("count", 0);

		// Preparing an array with all the default filters
		reportlistfilters.add(filter1);
		reportlistfilters.add(filter2);
		reportlistfilters.add(filter3);

		test.log(LogStatus.INFO, "Get Filter List for Reports");
		spogReportServer.getReportListFiltersWithCheck(validToken, reportlistfilters,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
	}

	// Valid Cases - 200
	@Test(dataProvider = "getreportlist")
	public void getReportListFilters_200(String validToken) {

		ArrayList<HashMap<String, Object>> reportlistfilters = new ArrayList<>();
		HashMap<String, Object> report_for;
		
		HashMap<String, Object> filter1 = new HashMap<>();
		HashMap<String, Object> filter2 = new HashMap<>();
		
		//Composing filter1
		report_for = new HashMap<>();
		report_for.put("type", "all_organizations");
		
		filter1.put("report_for", report_for);
		filter1.put("filter_id", "0001ef96-d9f5-4729-bf73-10b80d5dd079");
		filter1.put("filter_name", /*"Global_All_Sources_Filter1"*/"All Organizations");
		filter1.put("user_id", "854ac38b-1057-4964-8fae-be4e0ef8f0ef");
		filter1.put("organization_id", "95ca66a2-0cc9-43d6-bb9b-aa4d2b8ed1aa");
		filter1.put("create_ts", 1526984142);
		filter1.put("modify_ts", 1526984142);
		filter1.put("is_default", true);
		filter1.put("count", 0);
		filter1.put("view_type", "origin");
		
		//Composing filter2
		report_for = new HashMap<>();
		report_for.put("type", "all_sources");
		
		filter2.put("report_for", report_for);
		filter2.put("filter_id", "0000764c-ede4-4261-8340-2a3373504da4");
		filter2.put("filter_name", /*"Global_All_Sources_Filter1"*/"All Sources");
		filter2.put("user_id", "b8d38069-77c0-4620-9290-4868643d29f6");
		filter2.put("organization_id", "76abacd1-4d9c-4b16-b729-982784055313");
		filter2.put("create_ts", 1526983928);
		filter2.put("modify_ts", 1526983928);
		filter2.put("is_default", true);
		filter2.put("count", 0);
		filter2.put("view_type", "origin");
				
		//Preparing an array with all the default filters
		reportlistfilters.add(filter1);
		reportlistfilters.add(filter2);


		test.log(LogStatus.INFO, "Get Filter List for Reports");
		spogReportServer.getReportListFiltersWithCheck(validToken, reportlistfilters,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
	}

	// Invalid cases - 401

	@Test
	public void getReportListFilters_401() {

		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());

		test.log(LogStatus.INFO, "Get Filter List for Reports with invalid token");
		spogReportServer.getReportListFiltersWithCheck("junk", null, SpogConstants.NOT_LOGGED_IN,
				SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);

		test.log(LogStatus.INFO, "Get Filter List for Reports with missing token");
		spogReportServer.getReportListFiltersWithCheck("", null, SpogConstants.NOT_LOGGED_IN,
				SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);

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
