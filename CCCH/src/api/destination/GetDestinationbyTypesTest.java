package api.destination;

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

public class GetDestinationbyTypesTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private SPOGDestinationServer spogDestinationServer;
	private UserSpogServer userSpogServer;
	private ExtentTest test;
	// used for test case count like passed,failed,remaining cases
	int passedcases = 0;
	int failedcases = 0;
	int skippedcases = 0;
	String buildnumber = null;

	String[] datacenters;
	HashMap<String, Object> default_cloudhybrid = new HashMap<>();
	HashMap<String, Object> default_clouddirectvol = new HashMap<>();
	HashMap<String, Object> default_sharedfolder = new HashMap<>();
	private TestOrgInfo ti;
	private String direct_cloud_id;
	private String msp_cloud_id;

	@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runningMachine, String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		rep = ExtentManager.getInstance("GetDestinationbyTypesTest", logFolder);
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

		default_cloudhybrid.put("id", "cloud_hybrid_store");
		default_cloudhybrid.put("name", "dedupe stores");
		default_cloudhybrid.put("count", 0);

		default_clouddirectvol.put("id", "cloud_direct_volume");
		default_clouddirectvol.put("name", "cloud volumes");
		default_clouddirectvol.put("count", 0);

		default_sharedfolder.put("id", "share_folder");
		default_sharedfolder.put("name", "share folder");
		default_sharedfolder.put("count", 0);
	}

	@DataProvider(name = "getDestinationTypeValidCases", parallel = false)
	public final Object[][] getDestinationTypeValidCases() {
		return new Object[][] {
				{ "Get destination types in direct organization", ti.direct_org1_id, ti.direct_org1_user1_token,
						new String[] { ti.direct_org1_user1_token, ti.csr_token, ti.csr_readonly_token }, 0, 2, 1 },
				{ "Get destination types in sub organization and get by root msp token, account admin token, csr token, csr readonly token",
						ti.root_msp1_suborg1_id, ti.root_msp_org1_user1_token,
						new String[] { ti.root_msp1_suborg1_user1_token, ti.root_msp_org1_user1_token,
								ti.root_msp_org1_msp_accountadmin1_token, ti.csr_token, ti.csr_readonly_token },
						0, 1, 3 },
				{ "Get destination types in sub msp sub organization and get by sub msp token, account admin token, csr token, csr readonly token",
						ti.msp1_submsp1_sub_org1_id, ti.root_msp1_submsp1_user1_token,
						new String[] { ti.msp1_submsp1_suborg1_user1_token, ti.root_msp1_submsp1_user1_token,
								ti.root_msp1_submsp1_account_admin_token, ti.csr_token, ti.csr_readonly_token },
						0, 2, 2 }, };
	}

	@Test(dataProvider = "getDestinationTypeValidCases")
	public void getDestinationTypeValidCases(String testCase, String organization_id,
			String token, String[] tokensForGet, int count_share, int count_cd_vol, int count_ch_vol) {

		test = ExtentManager.getNewTest(testCase);
		test.assignAuthor("Kanamarlapudi, Chandra Kanth");
		ArrayList<String> cdVols = new ArrayList<>();
		ArrayList<String> chVols = new ArrayList<>();
		ArrayList<HashMap> Expecteddestinationtypes = new ArrayList<>();

		for (int i = 0; i < count_cd_vol; i++) {
			HashMap<String, String> retention = spogDestinationServer.composeRetention("0", "0", "0", "0", "0", "0");
			String destination_id = spogDestinationServer.createCdDestination(token, organization_id, datacenters[0],
					DestinationType.cloud_direct_volume.toString(), spogServer.ReturnRandom("cd_dest"),
					DestinationStatus.running.toString(), volume_type.normal.toString(), "6M", "6 Months", retention,
					SpogConstants.SUCCESS_POST, test);

			cdVols.add(destination_id);
		}

		for (int i = 0; i < count_ch_vol; i++) {
			String destination_id = spogDestinationServer.createHybridDestination(token, organization_id,
					datacenters[0], DestinationType.cloud_hybrid_store.toString(), spogServer.ReturnRandom("ch_dest"),
					DestinationStatus.running.toString(), test);

			chVols.add(destination_id);
		}

		default_sharedfolder.put("id", "share_folder");
		default_sharedfolder.put("name", "share folder");
		default_sharedfolder.put("count", count_share);

		default_clouddirectvol.put("id", "cloud_direct_volume");
		default_clouddirectvol.put("name", "cloud volumes");
		default_clouddirectvol.put("count", count_cd_vol);

		default_cloudhybrid.put("id", "cloud_hybrid_store");
		default_cloudhybrid.put("name", "dedupe stores");
		default_cloudhybrid.put("count", count_ch_vol);

		Expecteddestinationtypes.add(default_sharedfolder);
		Expecteddestinationtypes.add(default_clouddirectvol);
		Expecteddestinationtypes.add(default_cloudhybrid);

		for(String tokenForGet: tokensForGet) {
			test.log(LogStatus.INFO, "Get destination by types using token");
			String additionalUrl = "organization_id=" + organization_id;
			Response response = spogDestinationServer.getDestinationsbyTypes(additionalUrl, tokenForGet, test);

			test.log(LogStatus.INFO, "Validate the destination types");
			spogDestinationServer.checkdestinationbyTypes(response, Expecteddestinationtypes, "",
					SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		}
		
		/*for (String volume_id: cdVols) { // recycle and delete cd volumes
			spogDestinationServer.recycleCDVolume(volume_id);
			spogDestinationServer.deletedestinationbydestination_Id(volume_id, token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		}*/
		
		for(String destination_id: chVols) { // delete ch destinations
			spogDestinationServer.deletedestinationbydestination_Id(destination_id, token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		}
	}

	@DataProvider(name = "getDestinationTypesInvalidCases", parallel = false)
	public final Object[][] getDestinationTypesInvalidCases() {
		return new Object[][] {
				{ "Get destination by types using invalid token", "invalid", SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT },
				{ "Get destination by types using missing token", "", SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTHENTICATION_FAILED },
				{ "Get destination by types using null as token", null, SpogConstants.NOT_LOGGED_IN,
						SpogMessageCode.COMMON_AUTHENTICATION_FAILED }, };
	}

	@Test(dataProvider = "getDestinationTypesInvalidCases")
	public void getDestinationTypesInvalidCases(String testCase, String token, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage) {
		test = ExtentManager.getNewTest(testCase);
		test.assignAuthor("Kanamarlapudi, Chandra Kanth");
		Response response = spogDestinationServer.getDestinationsbyTypes("", token, test);
		
		test.log(LogStatus.INFO, "Validate the destination types");
		spogDestinationServer.checkdestinationbyTypes(response, null, "", expectedStatusCode, expectedErrorMessage,
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
