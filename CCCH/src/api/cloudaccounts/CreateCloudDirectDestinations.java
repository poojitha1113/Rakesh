package api.cloudaccounts;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.DestinationStatus;
import Constants.DestinationType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import Constants.volume_type;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import base.prepare.Is4Org;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class CreateCloudDirectDestinations extends Is4Org {

	private TestOrgInfo ti;
	private SPOGServer spogServer;
	private SPOGDestinationServer spogDestinationServer;
	private ExtentTest test;
	private String[] datacenters;

	@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runningMachine, String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
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
		spogDestinationServer.setToken(ti.csr_token);
		datacenters = spogDestinationServer.getDestionationDatacenterID();

		// Composing a HashMap for the destinations Retentions of the cloud direct
		// volumes
		HashMap<String, Object> Retention = new HashMap<String, Object>();
		HashMap<String, String> retention = new HashMap<String, String>();
		retention = spogDestinationServer.composeRetention("0", "0", "7", "0", "0", "0");
		Retention.put("7D", retention);
		retention = spogDestinationServer.composeRetention("0", "0", "14", "0", "0", "0");
		Retention.put("14D", retention);
		retention = spogDestinationServer.composeRetention("0", "0", "31", "0", "0", "0");
		Retention.put("1M", retention);
		retention = spogDestinationServer.composeRetention("0", "0", "31", "0", "2", "0");
		Retention.put("2M", retention);
		retention = spogDestinationServer.composeRetention("0", "0", "31", "0", "3", "0");
		Retention.put("3M", retention);
		retention = spogDestinationServer.composeRetention("0", "0", "31", "0", "6", "0");
		Retention.put("6M", retention);
		retention = spogDestinationServer.composeRetention("0", "0", "31", "0", "12", "0");
		Retention.put("1Y", retention);
		retention = spogDestinationServer.composeRetention("0", "0", "31", "0", "12", "2");
		Retention.put("2Y", retention);
		retention = spogDestinationServer.composeRetention("0", "0", "31", "0", "12", "3");
		Retention.put("3Y", retention);
		retention = spogDestinationServer.composeRetention("0", "0", "31", "0", "12", "7");
		Retention.put("7Y", retention);
		retention = spogDestinationServer.composeRetention("0", "0", "31", "0", "12", "10");
		Retention.put("10Y", retention);
		retention = spogDestinationServer.composeRetention("0", "0", "31", "0", "12", "-1");
		Retention.put("forever", retention);
		retention = spogDestinationServer.composeRetention("0", "42", "7", "0", "0", "0");
		Retention.put("7Dhf", retention);
		retention = spogDestinationServer.composeRetention("0", "42", "14", "0", "0", "0");
		Retention.put("14Dhf", retention);
		retention = spogDestinationServer.composeRetention("0", "42", "31", "0", "0", "0");
		Retention.put("1Mhf", retention);
		spogDestinationServer.setRetention(Retention);
	}

	@DataProvider(name = "testCasesForCreateCdDestination")
	public Object[][] testCasesForCreateCdDestination() {
		return new Object[][] {
				// 200
				{ "Create cd destination in direct org", ti.direct_org1_user1_token, ti.direct_org1_id, datacenters[0],
						spogServer.ReturnRandom("dest"), DestinationStatus.running.toString(), "7D", "7 Days",
						SpogConstants.SUCCESS_POST, null },
				{ "Create cd destination in msp sub org", ti.root_msp_org1_user1_token, ti.root_msp1_suborg1_id,
						datacenters[0], spogServer.ReturnRandom("dest"), DestinationStatus.running.toString(), "7D",
						"7 Days", SpogConstants.SUCCESS_POST, null },
				{ "Create cd destination in sub msp sub org", ti.root_msp1_submsp1_user1_token,
						ti.msp1_submsp1_sub_org1_id, datacenters[0], spogServer.ReturnRandom("dest"),
						DestinationStatus.running.toString(), "7D", "7 Days", SpogConstants.SUCCESS_POST, null },
				{ "Create cd destination in msp sub org", ti.root_msp_org1_msp_accountadmin1_token,
						ti.root_msp1_suborg1_id, datacenters[0], spogServer.ReturnRandom("dest"),
						DestinationStatus.running.toString(), "7D", "7 Days", SpogConstants.SUCCESS_POST, null },
				{ "Create cd destination in sub msp sub org", ti.root_msp1_submsp1_account_admin_token,
						ti.msp1_submsp1_sub_org1_id, datacenters[0], spogServer.ReturnRandom("dest"),
						DestinationStatus.running.toString(), "7D", "7 Days", SpogConstants.SUCCESS_POST, null },

				{ "Create cd destination without organization id", ti.direct_org1_user1_token, null, datacenters[0],
						spogServer.ReturnRandom("dest"), DestinationStatus.running.toString(), "7D", "7 Days",
						SpogConstants.SUCCESS_POST, null },

				// 400
				{ "Create cd destination without organization id should fail with msp token",
						ti.normal_msp_org1_user1_token, null, datacenters[0], spogServer.ReturnRandom("dest"),
						DestinationStatus.running.toString(), "7D", "7 Days", SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.DESTINATION_CANNOT_BE_CREATED_FOR_MSP },
				{ "Create cd destination without datacenter id", ti.direct_org1_user1_token, ti.direct_org1_id, null,
						spogServer.ReturnRandom("dest"), DestinationStatus.running.toString(), "7D", "7 Days",
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID },
				{ "Create cd destination without destination name", ti.direct_org1_user1_token, ti.direct_org1_id,
						datacenters[0], null, DestinationStatus.running.toString(), "7D", "7 Days",
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.DESTINATION_CANNOT_BLANK },
				{ "Create cd destination without retention id", ti.direct_org1_user1_token, ti.direct_org1_id,
						datacenters[0], spogServer.ReturnRandom("dest"), DestinationStatus.running.toString(), "",
						"7 Days", SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_IS_NOT_A_VALID_INT_VALUE },

				// 401
				{ "Create cd destination with invalid token", "invalid", ti.direct_org1_id, datacenters[0],
						spogServer.ReturnRandom("dest"), DestinationStatus.running.toString(), "7D", "7 Days",
						SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT },
				{ "Create cd destination with missing token", "", ti.direct_org1_id, datacenters[0],
						spogServer.ReturnRandom("dest"), DestinationStatus.running.toString(), "7D", "7 Days",
						SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED },
				{ "Create cd destination with null as token", "invalid", ti.direct_org1_id, datacenters[0],
						spogServer.ReturnRandom("dest"), DestinationStatus.running.toString(), "7D", "7 Days",
						SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED },

				// 404
				{ "Create cd destination with organization id that does not exist", ti.direct_org1_user1_token,
						UUID.randomUUID().toString(), datacenters[0], spogServer.ReturnRandom("dest"),
						DestinationStatus.running.toString(), "7D", "7 Days", SpogConstants.RESOURCE_NOT_EXIST,
						SpogMessageCode.INVALID_ORGANIZATION_ID },
				{ "Create cd destination with datacenter id that does not exist", ti.direct_org1_user1_token,
						ti.direct_org1_id, UUID.randomUUID().toString(), spogServer.ReturnRandom("dest"),
						DestinationStatus.running.toString(), "7D", "7 Days", SpogConstants.RESOURCE_NOT_EXIST,
						SpogMessageCode.DATACENTER_ID_NOT_FOUND },
		};
	}

	@Test(dataProvider="testCasesForCreateCdDestination")
	public void testCreateCdDestination(String testCase, String token, String orgId, String datacenterId,
			String destinationName, String destinationStatus, String retentionId, String retentionName,
			int expectedStatusCode, SpogMessageCode expectedErrorMessage) {

		test = ExtentManager.getNewTest(testCase);
		test.assignAuthor("Kanamarlapudi, Chandra Kanth");
		String destinationType = DestinationType.cloud_direct_volume.toString();
		String volumeType = volume_type.normal.toString();
		HashMap<String, String> retention = null;
		if (expectedStatusCode != SpogConstants.SUCCESS_POST)
			retention = (HashMap<String, String>) spogDestinationServer.getRetention().get(retentionId);

		Response response = spogDestinationServer.createCdDestinationWithCheck(token, orgId, datacenterId,
				destinationType, destinationName, destinationStatus, volumeType, retentionId, retentionName, retention,
				expectedStatusCode, expectedErrorMessage, test);

		if (expectedStatusCode == SpogConstants.SUCCESS_POST) {
			String destination_id = response.then().extract().path("data.destination_id");
			spogDestinationServer.recycleCDVolume(destination_id);
			spogDestinationServer.deletedestinationbydestination_Id(destination_id, token, test);
		}
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
