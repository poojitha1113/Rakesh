package api.users.destinationfilters;

import static invoker.SiteTestHelper.composeRandomUserMap;
import static invoker.SiteTestHelper.createSubOrgnaization;
import static invoker.SiteTestHelper.createUser;
import static invoker.SiteTestHelper.getRandomOrganizationName;
import static invoker.SiteTestHelper.getTestPassword;
import static invoker.SiteTestHelper.loginSpogServer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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
import InvokerServer.GatewayServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import InvokerServer.ServerResponseCode;
import InvokerServer.UserSpogServer;
import base.prepare.Is4Org;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;
import ui.base.common.SPOGMenuTreePath;

public class GetDestinationSystemFiltersTest extends Is4Org {
	
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
	
	@DataProvider(name="testCasesForGetDestinationSystemFilters")
	public Object[][] testCasesForGetDestinationSystemFilters(){
		return new Object[][] {
			//200
			{"Get Destination System Filters for direct org user", ti.direct_org1_user1_token, SpogConstants.SUCCESS_GET_PUT_DELETE},
			{"Get Destination System Filters for sub org user", ti.root_msp1_suborg1_user1_token, SpogConstants.SUCCESS_GET_PUT_DELETE},
			{"Get Destination System Filters for msp user", ti.root_msp_org1_user1_token, SpogConstants.SUCCESS_GET_PUT_DELETE},
			{"Get Destination System Filters for msp account admin user", ti.root_msp_org1_msp_accountadmin1_token, SpogConstants.SUCCESS_GET_PUT_DELETE},
			{"Get Destination System Filters for sub msp sub org user", ti.msp1_submsp1_suborg1_user1_token, SpogConstants.SUCCESS_GET_PUT_DELETE},
			{"Get Destination System Filters for sub msp user", ti.root_msp1_submsp1_user1_token, SpogConstants.SUCCESS_GET_PUT_DELETE},
			{"Get Destination System Filters for sub msp account admin user", ti.root_msp1_submsp1_account_admin_token, SpogConstants.SUCCESS_GET_PUT_DELETE},
			{"Get Destination System Filters for csr user", ti.csr_token, SpogConstants.SUCCESS_GET_PUT_DELETE},
			
			//401
			{"Get Destination System Filters with invalid token", "invalid",SpogConstants.NOT_LOGGED_IN},
			{"Get Destination System Filters with missing token", "",SpogConstants.NOT_LOGGED_IN},
			{"Get Destination System Filters with null as token", null,SpogConstants.NOT_LOGGED_IN},
			
		};
	}
	
	@Test(dataProvider="testCasesForGetDestinationSystemFilters")
	public void testGetDestinationSystemFilters(String testCase, String token, int expectedStatusCode) {
		
		test = ExtentManager.getNewTest(testCase);
		test.assignAuthor("Kanamarlapudi, Chandra Kanth");
		
		spogServer.setToken(token);
		Response response = spogServer.getDestinationSystemFilters();
		response.then().statusCode(expectedStatusCode);
	}
	
	
	@AfterMethod
	  public void getResult(ITestResult result){
	        if(result.getStatus() == ITestResult.FAILURE){
	            count1.setfailedcount();            
	            test.log(LogStatus.FAIL, "Test Case Failed is "+result.getName()+" with parameters as "+Arrays.asList(result.getParameters()) );
	            test.log(LogStatus.FAIL, result.getThrowable().getMessage());
	        }else if(result.getStatus() == ITestResult.SKIP){
	            count1.setskippedcount();
	            test.log(LogStatus.SKIP, "Test Case Skipped is "+result.getName());
	        }else if(result.getStatus()==ITestResult.SUCCESS){
	            count1.setpassedcount();
	        }
	        // ending test
	        //endTest(logger) : It ends the current test and prepares to create HTML report
	        rep.endTest(test);    
	  }
	
}
