package api.organizations;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

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

import Constants.DestinationStatus;
import Constants.DestinationType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class GetOrganizationIdDestinationsbyTypes extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private UserSpogServer userSpogServer;
	private SPOGDestinationServer spogDestinationServer;
	private ExtentTest test;
	private String org_model_prefix = this.getClass().getSimpleName();
	// used for test case count like passed,failed,remaining cases
	HashMap<String, Object> default_cloudhybrid = new HashMap<>();
	HashMap<String, Object> default_clouddirectvol = new HashMap<>();
	HashMap<String, Object> default_sharedfolder = new HashMap<>();
	String[] datacenters;
	private TestOrgInfo ti;

	@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runningMachine, String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		rep = ExtentManager.getInstance("GetOrganizationIdDestinationsbyTypes", logFolder);
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

	@DataProvider(name = "getdestinationfilter", parallel = false)
	public final Object[][] getdestinationfilter() {
		return new Object[][] {
				{ "Get destination types in direct organization using direct org user token", ti.direct_org1_id,
						ti.direct_org1_user1_token, 0, 1, 0 },
				{ "Get destination types in direct organization using csr token", ti.direct_org1_id, ti.csr_token, 0, 1,
						0 },
				{ "Get destination types in direct organization using csr readonly user token", ti.direct_org1_id,
						ti.csr_readonly_token, 0, 1, 0 },

				{ "Get destination types in sub organization using sub org user token", ti.root_msp1_suborg1_id,
						ti.root_msp1_suborg1_user1_token, 0, 1, 0 },
				{ "Get destination types in sub organization using msp token", ti.root_msp1_suborg1_id,
						ti.root_msp_org1_user1_token, 0, 1, 0 },
				{ "Get destination types in sub organization using msp account admin token", ti.root_msp1_suborg1_id,
						ti.root_msp_org1_msp_accountadmin1_token, 0, 1, 0 },
				{ "Get destination types in sub organization using csr token", ti.root_msp1_suborg1_id, ti.csr_token, 0,
						1, 0 },
				{ "Get destination types in sub organization using csr readonly user token", ti.root_msp1_suborg1_id,
						ti.csr_readonly_token, 0, 1, 0 },

				{ "Get destination types in sub msp's sub organization using sub msp sub org user token",
						ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg1_user1_token, 0, 1, 0 },
				{ "Get destination types in sub msp's sub organization using sub msp token",
						ti.msp1_submsp1_sub_org1_id, ti.root_msp1_submsp1_user1_token, 0, 1, 0 },
				{ "Get destination types in sub msp's sub organization using sub msp account admin token",
						ti.msp1_submsp1_sub_org1_id, ti.root_msp1_submsp1_account_admin_token, 0, 1, 0 },
				{ "Get destination types in sub msp's sub organization using csr token", ti.msp1_submsp1_sub_org1_id,
						ti.csr_token, 0, 1, 0 },
				{ "Get destination types in sub msp's sub organization using csr readonly user token",
						ti.msp1_submsp1_sub_org1_id, ti.csr_readonly_token, 0, 1, 0 }, };

	}

	@Test(dataProvider = "getdestinationfilter")
	public void getdestinationbytypesforspecifiedorg_clouddirectvol(String testCase, String organization_id,
			String validToken, int count_share, int count_cloud, int count_hybrid) {

		test = ExtentManager.getNewTest(testCase);
		ArrayList<HashMap<String, Object>> Expecteddestinationtypes = new ArrayList<>();

		default_sharedfolder.put("destination_type", "share_folder");
		default_sharedfolder.put("name", "share folder");
		default_sharedfolder.put("amount", count_share);

		default_clouddirectvol.put("destination_type", "cloud_direct_volume");
		default_clouddirectvol.put("name", "cloud volumes");
		default_clouddirectvol.put("amount", count_cloud);

		default_cloudhybrid.put("destination_type", "cloud_hybrid_store");
		default_cloudhybrid.put("name", "hybrid stores");
		default_cloudhybrid.put("amount", count_hybrid);

		Expecteddestinationtypes.add(default_sharedfolder);
		Expecteddestinationtypes.add(default_clouddirectvol);
		Expecteddestinationtypes.add(default_cloudhybrid);

		test.log(LogStatus.INFO, "Get destination by types");
		spogDestinationServer.getDestinationTypesForSpecifiedOrgWithCheck(validToken, organization_id,
				Expecteddestinationtypes, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
	}

	@DataProvider(name = "invalidCases", parallel = false)
	public final Object[][] invalidCases() {
		return new Object[][] {
				// 400
				{ "Get destination types for specified organization with invalid organization id and direct user token",
						"invalid", ti.direct_org1_user1_token, SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_ORGANIZATIONID_IS_NOT_UUID },
				{ "Get destination types for specified organization with invalid organization id and msp user token",
						"invalid", ti.root_msp_org1_user1_token, SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_ORGANIZATIONID_IS_NOT_UUID },
				{ "Get destination types for specified organization with invalid organization id and sub org user token",
						"invalid", ti.root_msp1_suborg1_user1_token, SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_ORGANIZATIONID_IS_NOT_UUID },
				{ "Get destination types for specified organization with invalid organization id and msp account admin user token",
						"invalid", ti.root_msp_org1_msp_accountadmin1_token, SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_ORGANIZATIONID_IS_NOT_UUID },
				{ "Get destination types for specified organization with invalid organization id and sub msp user token",
						"invalid", ti.root_msp1_submsp1_user1_token, SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_ORGANIZATIONID_IS_NOT_UUID },
				{ "Get destination types for specified organization with invalid organization id and sub msp sub org user token",
						"invalid", ti.msp1_submsp1_suborg1_user1_token, SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_ORGANIZATIONID_IS_NOT_UUID },
				{ "Get destination types for specified organization with invalid organization id and sub msp account admin user token",
						"invalid", ti.root_msp1_submsp1_account_admin_token, SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.ELEMENT_ORGANIZATIONID_IS_NOT_UUID },

				// 401
				{ "Get destination types for specified organization with invalid token", ti.direct_org1_id, "invalid",
						SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT },
				{ "Get destination types for specified organization with missing token", ti.direct_org1_id, "",
						SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED },
				{ "Get destination types for specified organization with null as token", ti.direct_org1_id, null,
						SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED },

				// 403
				{ "Get destination types for specified organization with direct organization id and direct org2 user token",
						ti.direct_org1_id, ti.direct_org2_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get destination types for specified organization with direct organization id and msp user token",
						ti.direct_org1_id, ti.root_msp_org1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get destination types for specified organization with direct organization id and sub org user token",
						ti.direct_org1_id, ti.root_msp1_suborg1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get destination types for specified organization with direct organization id and msp account admin user token",
						ti.direct_org1_id, ti.root_msp_org1_msp_accountadmin1_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get destination types for specified organization with direct organization id and sub msp user token",
						ti.direct_org1_id, ti.root_msp1_submsp1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get destination types for specified organization with direct organization id and sub msp sub org user token",
						ti.direct_org1_id, ti.msp1_submsp1_suborg1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get destination types for specified organization with direct organization id and sub msp account admin user token",
						ti.direct_org1_id, ti.root_msp1_submsp1_account_admin_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },

				{ "Get destination types for specified organization with msp organization id and direct user token",
						ti.root_msp_org1_id, ti.direct_org1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get destination types for specified organization with msp organization id and msp2 user token",
						ti.root_msp_org1_id, ti.root_msp_org2_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get destination types for specified organization with msp organization id and sub org user token",
						ti.root_msp_org1_id, ti.root_msp1_suborg1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get destination types for specified organization with msp organization id and msp account admin user token",
						ti.root_msp_org1_id, ti.root_msp_org2_msp_accountadmin1_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get destination types for specified organization with msp organization id and sub msp user token",
						ti.root_msp_org1_id, ti.root_msp1_submsp1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get destination types for specified organization with msp organization id and sub msp sub org user token",
						ti.root_msp_org1_id, ti.msp1_submsp1_suborg1_user1_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get destination types for specified organization with msp organization id and sub msp account admin user token",
						ti.root_msp_org1_id, ti.root_msp1_submsp1_account_admin_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },

				{ "Get destination types for specified organization with sub organization id and direct user token",
						ti.root_msp1_suborg1_id, ti.direct_org1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get destination types for specified organization with sub organization id and msp2 user token",
						ti.root_msp1_suborg1_id, ti.root_msp_org2_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get destination types for specified organization with sub organization id and sub org2 user token",
						ti.root_msp1_suborg1_id, ti.root_msp1_suborg2_user1_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get destination types for specified organization with sub organization id and msp account admin user token",
						ti.root_msp1_suborg1_id, ti.root_msp_org1_msp_accountadmin2_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get destination types for specified organization with sub organization id and sub msp user token",
						ti.root_msp1_suborg1_id, ti.root_msp1_submsp1_user1_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get destination types for specified organization with sub organization id and sub msp sub org user token",
						ti.root_msp1_suborg1_id, ti.msp1_submsp1_suborg1_user1_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get destination types for specified organization with sub organization id and sub msp account admin user token",
						ti.root_msp1_suborg1_id, ti.root_msp1_submsp1_account_admin_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },

				{ "Get destination types for specified organization with sub msp organization id and direct user token",
						ti.root_msp1_submsp_org1_id, ti.direct_org1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get destination types for specified organization with sub msp organization id and msp user token",
						ti.root_msp1_submsp_org1_id, ti.root_msp_org1_user1_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get destination types for specified organization with sub msp organization id and sub org user token",
						ti.root_msp1_submsp_org1_id, ti.root_msp1_suborg1_user1_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get destination types for specified organization with sub msp organization id and msp account admin user token",
						ti.root_msp1_submsp_org1_id, ti.root_msp_org1_msp_accountadmin1_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get destination types for specified organization with sub msp organization id and sub msp2 user token",
						ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp2_user1_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get destination types for specified organization with sub msp organization id and sub msp sub org user token",
						ti.root_msp1_submsp_org1_id, ti.msp1_submsp1_suborg1_user1_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get destination types for specified organization with sub msp organization id and sub msp2 account admin user token",
						ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp2_account_admin_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },

				{ "Get destination types for specified organization with sub msp sub organization id and direct user token",
						ti.msp1_submsp1_sub_org1_id, ti.direct_org1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS,
						SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get destination types for specified organization with sub msp sub organization id and msp user token",
						ti.msp1_submsp1_sub_org1_id, ti.root_msp_org1_user1_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get destination types for specified organization with sub msp sub organization id and sub org user token",
						ti.msp1_submsp1_sub_org1_id, ti.root_msp1_suborg1_user1_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get destination types for specified organization with sub msp sub organization id and msp account admin user token",
						ti.msp1_submsp1_sub_org1_id, ti.root_msp_org1_msp_accountadmin1_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get destination types for specified organization with sub msp sub organization id and sub msp2 user token",
						ti.msp1_submsp1_sub_org1_id, ti.root_msp1_submsp2_user1_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get destination types for specified organization with sub msp sub organization id and sub msp sub org2 user token",
						ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_suborg2_user1_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },
				{ "Get destination types for specified organization with sub msp sub organization id and sub msp2 account admin user token",
						ti.msp1_submsp1_sub_org1_id, ti.root_msp1_submsp2_account_admin_token,
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY },

				// 404
				{ "Get destination types for specified organization with organization id that does not exist and direct user token",
						UUID.randomUUID().toString(), ti.direct_org1_user1_token, SpogConstants.RESOURCE_NOT_EXIST,
						SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED },
				{ "Get destination types for specified organization with organization id that does not exist and msp user token",
						UUID.randomUUID().toString(), ti.root_msp_org1_user1_token, SpogConstants.RESOURCE_NOT_EXIST,
						SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED },
				{ "Get destination types for specified organization with organization id that does not exist and sub org user token",
						UUID.randomUUID().toString(), ti.root_msp1_suborg1_user1_token,
						SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED },
				{ "Get destination types for specified organization with organization id that does not exist and msp account admin user token",
						UUID.randomUUID().toString(), ti.root_msp_org1_msp_accountadmin1_token,
						SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED },
				{ "Get destination types for specified organization with organization id that does not exist and sub msp user token",
						UUID.randomUUID().toString(), ti.root_msp1_submsp1_user1_token,
						SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED },
				{ "Get destination types for specified organization with organization id that does not exist and sub msp sub org user token",
						UUID.randomUUID().toString(), ti.msp1_submsp1_suborg1_user1_token,
						SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED },
				{ "Get destination types for specified organization with organization id that does not exist and sub msp account admin user token",
						UUID.randomUUID().toString(), ti.root_msp1_submsp1_account_admin_token,
						SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED }, };
	}

	@Test(dataProvider = "invalidCases")
	public void getDestinationTypesForSpecifiedOrganizationInvalid(String testCase, String org_id, String token,
			int expectedStatusCode, SpogMessageCode expectedErrorMessage) {

		test = ExtentManager.getNewTest(testCase);

		spogDestinationServer.getDestinationTypesForSpecifiedOrgWithCheck(token, org_id, null, expectedStatusCode,
				expectedErrorMessage, test);
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
