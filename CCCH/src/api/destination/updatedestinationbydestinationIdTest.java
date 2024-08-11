package api.destination;

import static org.hamcrest.Matchers.equalTo;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.openqa.selenium.support.ui.Sleeper;
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

import Constants.CloudDirectRetentionValues;
import Constants.ConnectionStatus;
import Constants.DestinationStatus;
import Constants.DestinationType;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import Constants.volume_type;
import InvokerServer.GatewayServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class updatedestinationbydestinationIdTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private UserSpogServer userSpogServer;
	private SPOGDestinationServer spogDestinationServer;
	private ExtentTest test;
	private String org_model_prefix = this.getClass().getSimpleName();
	// used for test case count like passed,failed,remaining cases

	private TestOrgInfo ti;
	private String[] datacenters;
	private String direct_cloud_id;
	private Response response;
	private String msp_cloud_id;
	private String direct_baas_destionation_ID;
	private String sub_orga_baas_destionation_ID;
	private String submsp_suborg_baas_destionation_ID;

	@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runningMachine, String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		rep = ExtentManager.getInstance("updatedestinationbydestinationIdTest", logFolder);
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
		test.log(LogStatus.INFO, "Get the datacenter id");
		spogDestinationServer.setToken(ti.csr_token);
		datacenters = spogDestinationServer.getDestionationDatacenterID();

		response = spogServer.getCloudAccounts(ti.direct_org1_user1_token, "", test);
		direct_cloud_id = response.then().extract().path("data[0].cloud_account_id");

		response = spogServer.getCloudAccounts(ti.root_msp_org1_user1_token, "", test);
		msp_cloud_id = response.then().extract().path("data[0].cloud_account_id");

		response = spogDestinationServer.getDestinations(ti.direct_org1_user1_token,
				"destination_type=cloud_direct_volume", test);
		direct_baas_destionation_ID = response.then().extract().path("data[0].destination_id");

		response = spogDestinationServer.getDestinations(ti.root_msp1_suborg1_user1_token,
				"destination_type=cloud_direct_volume", test);
		sub_orga_baas_destionation_ID = response.then().extract().path("data[0].destination_id");

		response = spogDestinationServer.getDestinations(ti.msp1_submsp1_suborg1_user1_token,
				"destination_type=cloud_direct_volume", test);
		submsp_suborg_baas_destionation_ID = response.then().extract().path("data[0].destination_id");

	}

	@DataProvider(name = "updateDestinationCases")
	public Object[][] updateDestinationCases() {
		return new Object[][] {
				// 200
				{ "Update cloud direct destination in direct organization", ti.direct_org1_user1_token,
						direct_baas_destionation_ID, spogServer.ReturnRandom("newDest"), "1M", "1 Month",
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Update cloud direct destination in direct organization", ti.direct_org1_user1_token,
						direct_baas_destionation_ID, spogServer.ReturnRandom("newDest"), "2M", "2 Months",
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Update cloud direct destination in direct organization", ti.csr_token, direct_baas_destionation_ID,
						spogServer.ReturnRandom("newDest"), "2M", "2 Months", SpogConstants.SUCCESS_GET_PUT_DELETE,
						null },

				{ "Update cloud direct destination in sub organization", ti.root_msp_org1_user1_token,
						sub_orga_baas_destionation_ID, spogServer.ReturnRandom("newDest"), "1M", "1 Month",
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Update cloud direct destination in sub organization", ti.root_msp_org1_msp_accountadmin1_token,
						sub_orga_baas_destionation_ID, spogServer.ReturnRandom("newDest"), "2M", "2 Months",
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Update cloud direct destination in sub organization", ti.csr_token, sub_orga_baas_destionation_ID,
						spogServer.ReturnRandom("newDest"), "2M", "2 Months", SpogConstants.SUCCESS_GET_PUT_DELETE,
						null },

				{ "Update cloud direct destination in sub organization", ti.root_msp1_submsp1_user1_token,
						submsp_suborg_baas_destionation_ID, spogServer.ReturnRandom("newDest"), "1M", "1 Month",
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Update cloud direct destination in sub organization", ti.root_msp1_submsp1_account_admin_token,
						submsp_suborg_baas_destionation_ID, spogServer.ReturnRandom("newDest"), "2M", "2 Months",
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Update cloud direct destination in sub organization", ti.csr_token,
						submsp_suborg_baas_destionation_ID, spogServer.ReturnRandom("newDest"), "2M", "2 Months",
						SpogConstants.SUCCESS_GET_PUT_DELETE, null },

		};
	}

	@Test(dataProvider = "updateDestinationCases",enabled=false)
	public void updateDestiantionValid(String testCase, String token, String destination_id, String new_dest_name,
			String new_retention_id, String new_retention_name, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage) {

		test = ExtentManager.getNewTest(testCase);
		HashMap<String, Object> destinationInfo = new HashMap<>();
		HashMap<String, Object> cloudvolumeInfo = new HashMap<>();

		destinationInfo.put("destination_name", new_dest_name);
		cloudvolumeInfo.put("retention_id", new_retention_id);
		cloudvolumeInfo.put("retention_name", new_retention_name);
		destinationInfo.put("cloud_direct_volume", cloudvolumeInfo);

		Response response = spogDestinationServer.updateDestinationById(token, destination_id, destinationInfo,
				expectedStatusCode);

		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE)
			response.then().body("data.destination_id", equalTo(destination_id))
					.body("destination_name", equalTo(new_dest_name))
					.body("cloud_direct_volume.retention_id", equalTo(new_retention_id))
					.body("cloud_direct_volume.retention_name", equalTo(new_retention_name));
	}

	@DataProvider(name = "updateNonExistingDestination")
	public Object[][] updateNonExistingDestination() {
		return new Object[][] {

				{ "Update destination with id that does not exist and direct org user token, should create destination",
						ti.direct_org1_user1_token, DestinationType.cloud_direct_volume.toString(), ti.direct_org1_id,
						direct_cloud_id, spogServer.ReturnRandom("dest"), "7D", "7 Days",
						CloudDirectRetentionValues.DAYS7, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Update destination with id that does not exist in sub org and msp org user token, should create destination",
						ti.root_msp_org1_user1_token, DestinationType.cloud_direct_volume.toString(),
						ti.root_msp1_suborg1_id, msp_cloud_id, spogServer.ReturnRandom("dest"), "7D", "7 Days",
						CloudDirectRetentionValues.DAYS7, SpogConstants.SUCCESS_GET_PUT_DELETE, null },

				{ "Update destination with id that does not exist in msp org, should fail to create destination",
						ti.root_msp_org1_user1_token, DestinationType.cloud_direct_volume.toString(),
						ti.root_msp_org1_id, msp_cloud_id, spogServer.ReturnRandom("dest"), "7D", "7 Days",
						CloudDirectRetentionValues.DAYS7, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Update destination with id that does not exist in msp org and msp account admin token, should fail to create destination",
						ti.root_msp_org1_msp_accountadmin1_token, DestinationType.cloud_direct_volume.toString(),
						ti.root_msp_org1_id, msp_cloud_id, spogServer.ReturnRandom("dest"), "7D", "7 Days",
						CloudDirectRetentionValues.DAYS7, SpogConstants.SUCCESS_GET_PUT_DELETE, null },

				{ "Update destination with id that does not exist and direct org user token, should create destination",
						ti.root_msp1_submsp1_user1_token, DestinationType.cloud_direct_volume.toString(),
						ti.root_msp1_submsp_org1_id, msp_cloud_id, spogServer.ReturnRandom("dest"), "7D", "7 Days",
						CloudDirectRetentionValues.DAYS7, SpogConstants.SUCCESS_GET_PUT_DELETE, null },
				{ "Update destination with id that does not exist in sub org and msp org user token, should create destination",
						ti.root_msp1_submsp1_account_admin_token, DestinationType.cloud_direct_volume.toString(),
						ti.root_msp1_submsp_org1_id, msp_cloud_id, spogServer.ReturnRandom("dest"), "7D", "7 Days",
						CloudDirectRetentionValues.DAYS7, SpogConstants.SUCCESS_GET_PUT_DELETE, null }, };

	}

	@Test(dataProvider = "updateNonExistingDestination")
	public void updateDestination(String testCase, String token, String destinationType, String org_id, String cloud_id,
			String destination_name, String retention_id, String retention_name,
			CloudDirectRetentionValues expectedRetention, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage) {

		String destination_Id = UUID.randomUUID().toString();
		String datacenter_id = datacenters[0];
		String destination_status = DestinationStatus.running.toString();
		spogServer.setToken(token);
		String user_id = spogServer.GetLoggedinUser_UserID();

		HashMap<String, String> retention = spogDestinationServer.composeRetention("0", "0", "0", "0", "0", "0");
		HashMap<String, Object> cloudvolumeInfo = spogDestinationServer.composeCloudDirectInfo(cloud_id,
				spogServer.ReturnRandom("volumeName"), retention_id, retention_name, 0.0, 0.0, 0.0,
				volume_type.normal.toString(), spogServer.ReturnRandom("hostname"), retention);

		spogDestinationServer.updatedestinationinfobydestinationIdwithCheck(destination_Id, token, org_id, cloud_id,
				datacenter_id, destinationType, destination_status, destination_name, cloudvolumeInfo, null, user_id,
				expectedStatusCode, expectedErrorMessage, expectedRetention, test);

		/*spogDestinationServer.deletedestinationbydestination_Id(destination_Id, token,
				SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		spogDestinationServer.recycleCDVolume(datacenter_id);*/
	}

	@DataProvider(name = "invalidCases")
	public Object[][] invalidCases() {
		return new Object[][] {
				// 400
				{ "Update destination with invalid id and direct org user token", "invalid", ti.direct_org1_user1_token,
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_DESTINATIONID_IS_NOT_UUID },
				{ "Update destination with invalid id and msp org user token", "invalid", ti.root_msp_org1_user1_token,
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_DESTINATIONID_IS_NOT_UUID },
				{ "Update destination with invalid id and sub org user token", "invalid",
						ti.root_msp1_suborg1_user1_token, SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_DESTINATIONID_IS_NOT_UUID },
				{ "Update destination with invalid id and msp org account admin user token", "invalid",
						ti.root_msp_org1_msp_accountadmin1_token, SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_DESTINATIONID_IS_NOT_UUID },
				{ "Update destination with invalid id and sub msp org user token", "invalid",
						ti.root_msp1_submsp1_user1_token, SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_DESTINATIONID_IS_NOT_UUID },
				{ "Update destination with invalid id and sub msp sub org user token", "invalid",
						ti.msp1_submsp1_suborg1_user1_token, SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_DESTINATIONID_IS_NOT_UUID },
				{ "Update destination with invalid id and sub msp org account admin user token", "invalid",
						ti.root_msp1_submsp1_account_admin_token, SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_DESTINATIONID_IS_NOT_UUID },
				{ "Update destination with invalid id and csr user token", "invalid", ti.csr_token,
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_DESTINATIONID_IS_NOT_UUID },

				// 401
				{ "Update destination with invalid token", UUID.randomUUID().toString(), "invalid",
						SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT },
				{ "Update destination with missing token", UUID.randomUUID().toString(), "",
						SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED },
				{ "Update destination with null as token", UUID.randomUUID().toString(), null,
						SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED },

				// 403
				{ "Update destination of direct org with direct org2 user token", direct_baas_destionation_ID,
						ti.direct_org2_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Update destination of direct org with msp org user token", direct_baas_destionation_ID,
						ti.root_msp_org1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Update destination of direct org with sub org user token", direct_baas_destionation_ID,
						ti.root_msp1_suborg1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Update destination of direct org with msp org account admin user token", direct_baas_destionation_ID,
						ti.root_msp_org1_msp_accountadmin1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Update destination of direct org with sub msp org user token", direct_baas_destionation_ID,
						ti.root_msp1_submsp1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Update destination of direct org with sub msp sub org user token", direct_baas_destionation_ID,
						ti.msp1_submsp1_suborg1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Update destination of direct org with sub msp org account admin user token",
						direct_baas_destionation_ID, ti.root_msp1_submsp1_account_admin_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },

				{ "Update destination of direct org with direct org2 user token", sub_orga_baas_destionation_ID,
						ti.direct_org2_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Update destination of direct org with msp org2 user token", sub_orga_baas_destionation_ID,
						ti.root_msp_org2_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Update destination of direct org with sub org2 user token", sub_orga_baas_destionation_ID,
						ti.root_msp1_suborg2_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Update destination of direct org with msp org account admin user token",
						sub_orga_baas_destionation_ID, ti.root_msp_org1_msp_accountadmin2_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Update destination of direct org with sub msp org user token", sub_orga_baas_destionation_ID,
						ti.root_msp1_submsp1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Update destination of direct org with sub msp sub org user token", sub_orga_baas_destionation_ID,
						ti.msp1_submsp1_suborg1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Update destination of direct org with sub msp org account admin user token",
						sub_orga_baas_destionation_ID, ti.root_msp1_submsp1_account_admin_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },

				{ "Update destination of direct org with direct org2 user token", submsp_suborg_baas_destionation_ID,
						ti.direct_org2_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Update destination of direct org with msp org user token", submsp_suborg_baas_destionation_ID,
						ti.root_msp_org1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Update destination of direct org with sub org2 user token", submsp_suborg_baas_destionation_ID,
						ti.root_msp1_suborg2_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Update destination of direct org with msp org account admin user token",
						submsp_suborg_baas_destionation_ID, ti.root_msp_org1_msp_accountadmin1_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Update destination of direct org with sub msp org2 user token", submsp_suborg_baas_destionation_ID,
						ti.root_msp1_submsp2_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Update destination of direct org with sub msp sub org2 user token",
						submsp_suborg_baas_destionation_ID, ti.msp1_submsp1_suborg2_user1_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Update destination of direct org with sub msp org2 account admin user token",
						submsp_suborg_baas_destionation_ID, ti.root_msp1_submsp2_account_admin_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },

				// 404
				/*
				 * { "Update destination with id that does not exist and direct org user token",
				 * UUID.randomUUID().toString(), ti.direct_org1_user1_token,
				 * SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.DESTINATION_NOT_FOUND }, {
				 * "Update destination with id that does not exist and msp org user token",
				 * UUID.randomUUID().toString(), ti.root_msp_org1_user1_token,
				 * SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.DESTINATION_NOT_FOUND }, {
				 * "Update destination with id that does not exist and sub org user token",
				 * UUID.randomUUID().toString(), ti.root_msp1_suborg1_user1_token,
				 * SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.DESTINATION_NOT_FOUND }, {
				 * "Update destination with id that does not exist and msp org account admin user token"
				 * , UUID.randomUUID().toString(), ti.root_msp_org1_msp_accountadmin1_token,
				 * SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.DESTINATION_NOT_FOUND }, {
				 * "Update destination with id that does not exist and sub msp org user token",
				 * UUID.randomUUID().toString(), ti.root_msp1_submsp1_user1_token,
				 * SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.DESTINATION_NOT_FOUND }, {
				 * "Update destination with id that does not exist and sub msp sub org user token"
				 * , UUID.randomUUID().toString(), ti.msp1_submsp1_suborg1_user1_token,
				 * SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.DESTINATION_NOT_FOUND }, {
				 * "Update destination with id that does not exist and sub msp org account admin user token"
				 * , UUID.randomUUID().toString(), ti.root_msp1_submsp1_account_admin_token,
				 * SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.DESTINATION_NOT_FOUND }, {
				 * "Update destination with id that does not exist and csr user token",
				 * UUID.randomUUID().toString(), ti.csr_token, SpogConstants.RESOURCE_NOT_EXIST,
				 * SpogMessageCode.DESTINATION_NOT_FOUND },
				 */

		};
	}

	@Test(dataProvider = "invalidCases")
	public void updateDestinationByIdInvalid(String testCase, String destination_id, String token,
			int expectedStatusCode, SpogMessageCode expectedErrorMessage) {

		test = ExtentManager.getNewTest(testCase);
		spogDestinationServer.updatedestinationinfobydestinationIdwithCheck(destination_id, token, null, null,
				datacenters[0], DestinationType.cloud_direct_volume.toString(), DestinationStatus.running.toString(),
				spogServer.ReturnRandom("dest"), null, null, null, expectedStatusCode, expectedErrorMessage, null,
				test);
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
