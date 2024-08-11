package api.users.sourcefilters;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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
import InvokerServer.SPOGServer;
import base.prepare.Is4Org;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class GetSourceSystemFilterTest extends Is4Org {
	
	private TestOrgInfo ti;
	private SPOGServer spogServer;
	private ExtentTest test;

	@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runningMachine, String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		rep = ExtentManager.getInstance(this.getClass().getSimpleName(), logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Kanamarlapudi, Chandra Kanth";

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
	
	@DataProvider(name="testCasesForGetSourceSystemFilters")
	public Object[][] testCasesForGetSourceSystemFilters(){
		return new Object[][] {
			//200
			{"Get Source System Filters for direct org user", ti.direct_org1_user1_token, SpogConstants.SUCCESS_GET_PUT_DELETE},
			{"Get Source System Filters for sub org user", ti.root_msp1_suborg1_user1_token, SpogConstants.SUCCESS_GET_PUT_DELETE},
			{"Get Source System Filters for msp user", ti.root_msp_org1_user1_token, SpogConstants.SUCCESS_GET_PUT_DELETE},
			{"Get Source System Filters for msp account admin user", ti.root_msp_org1_msp_accountadmin1_token, SpogConstants.SUCCESS_GET_PUT_DELETE},
			{"Get Source System Filters for sub msp sub org user", ti.msp1_submsp1_suborg1_user1_token, SpogConstants.SUCCESS_GET_PUT_DELETE},
			{"Get Source System Filters for sub msp user", ti.root_msp1_submsp1_user1_token, SpogConstants.SUCCESS_GET_PUT_DELETE},
			{"Get Source System Filters for sub msp account admin user", ti.root_msp1_submsp1_account_admin_token, SpogConstants.SUCCESS_GET_PUT_DELETE},
			{"Get Source System Filters for csr user", ti.csr_token, SpogConstants.SUCCESS_GET_PUT_DELETE},
			
			//401
			{"Get Source System Filters with invalid token", "invalid",SpogConstants.NOT_LOGGED_IN},
			{"Get Source System Filters with missing token", "",SpogConstants.NOT_LOGGED_IN},
			{"Get Source System Filters with null as token", null,SpogConstants.NOT_LOGGED_IN},
			
		};
	}
	
	@Test(dataProvider="testCasesForGetSourceSystemFilters")
	public void testGetSourceSystemFilters(String testCase, String token, int expectedStatusCode) {
		
		test = ExtentManager.getNewTest(testCase);
		test.assignAuthor("Kanamarlapudi, Chandra Kanth");
		
		spogServer.setToken(token);
		Response response = spogServer.getSourceSystemFilters();
		response.then().statusCode(expectedStatusCode);
		
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			ArrayList<HashMap<String, Object>> actualResponse = response.then().extract().path("data");
			ArrayList<HashMap<String, Object>> expectedResponse = getComposedSystemFilters();
			
			for (int i = 0; i < actualResponse.size(); i++) {
				HashMap<String, Object> actualFilter = actualResponse.get(i);
				HashMap<String, Object> expectedFilter = expectedResponse.get(i);
				
				validateSystemFilters(actualFilter, expectedFilter, test);
			}
		}
	}
	
	public ArrayList<HashMap<String, Object>> getComposedSystemFilters(){
		
		ArrayList<HashMap<String, Object>> defaultFilters = new ArrayList<>();
		
		HashMap<String, Object> filter1 = new HashMap<>();
		
		ArrayList<String> p_status = new ArrayList<>();
		p_status.add("unprotect");
		
		filter1.put("protection_status", p_status);
		filter1.put("is_default", false);
		filter1.put("filter_id", "846ca92a-a13d-4156-8697-f38bdd9513e1");
		filter1.put("filter_name", "Not Protected");
		filter1.put("user_id", "5c6d9135-f445-4582-a166-87f6075f8f37");
		filter1.put("organization_id", "cce55126-7269-4833-9bbf-432e663820a7");
		filter1.put("create_ts", 1528196317);
		filter1.put("modify_ts", 1528196317);				
		filter1.put("view_type", "origin");
		filter1.put("count", 0);
		filter1.put("filter_type", "source_filter_global");
		defaultFilters.add(filter1);
		
		HashMap<String, Object> filter2 = new HashMap<>();
		
		ArrayList<String> last_job = new ArrayList<>();
		last_job.add("failed");
				
		filter2.put("last_job", last_job);
		filter2.put("is_default", false);
		filter2.put("filter_id", "6e8433dc-63ff-4891-8fc2-ec498fca849d");
		filter2.put("filter_name", "Failed");
		filter2.put("user_id", "5c6d9135-f445-4582-a166-87f6075f8f37");
		filter2.put("organization_id", "cce55126-7269-4833-9bbf-432e663820a7");		
		filter2.put("create_ts", 1528196317);
		filter2.put("modify_ts", 1528196317);		
		filter2.put("view_type", "origin");
		filter2.put("count", 0);
		filter2.put("filter_type", "source_filter_global");
		defaultFilters.add(filter2);
		
		return defaultFilters;
	}
	
	
	public void validateSystemFilters(HashMap<String, Object> actualFilter, HashMap<String, Object> expectedFilter, ExtentTest test) {
		
		spogServer.assertResponseItem(expectedFilter.get("protection_status"), actualFilter.get("protection_status"), test);
		spogServer.assertResponseItem(expectedFilter.get("is_default"), actualFilter.get("is_default"), test);
		spogServer.assertResponseItem(expectedFilter.get("filter_id"), actualFilter.get("filter_id"), test);
		spogServer.assertResponseItem(expectedFilter.get("filter_name"), actualFilter.get("filter_name"), test);
		spogServer.assertResponseItem(expectedFilter.get("user_id"), actualFilter.get("user_id"), test);
		spogServer.assertResponseItem(expectedFilter.get("organization_id"), actualFilter.get("organization_id"), test);
		spogServer.assertResponseItem(expectedFilter.get("create_ts"), actualFilter.get("create_ts"), test);
		spogServer.assertResponseItem(expectedFilter.get("modify_ts"), actualFilter.get("modify_ts"), test);
		spogServer.assertResponseItem(expectedFilter.get("view_type"), actualFilter.get("view_type"), test);
		spogServer.assertResponseItem(expectedFilter.get("count"), actualFilter.get("count"), test);
		spogServer.assertResponseItem(expectedFilter.get("source_filter_type"), actualFilter.get("source_filter_type"), test);
				
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
		// ending test
		// endTest(logger) : It ends the current test and prepares to create HTML report
		rep.endTest(test);
		// rep.flush();
	}
}
