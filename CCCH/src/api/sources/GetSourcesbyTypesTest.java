package api.sources;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import javax.print.DocFlavor.STRING;

import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.ConnectionStatus;
import Constants.OSMajor;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.SPOGServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import invoker.SiteTestHelper;
import io.restassured.response.Response;

public class GetSourcesbyTypesTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private SiteTestHelper siteTestHelper;
	private ExtentTest test;

	HashMap<String, String> default_instantvm = new HashMap<>();
	HashMap<String, String> default_machine = new HashMap<>();
	HashMap<String, String> default_office365 = new HashMap<>();
	HashMap<String, String> default_virtualstandby = new HashMap<>();
	HashMap<String, String> default_sharedfolder = new HashMap<>();
	HashMap<String, String> default_sharepoint = new HashMap<>();
	HashMap<String, String> default_agentlessvm = new HashMap<>();

	private String org_model_prefix = this.getClass().getSimpleName();
	// used for test cases count like passed,failed,remaining cases
	int passedcases = 0;
	int failedcases = 0;
	int skippedcases = 0;
	private TestOrgInfo ti;
	private ArrayList<String> sourceIds = new ArrayList<>();
	private ArrayList<HashMap> dirExpSrcTypes ;
	private ArrayList<HashMap> subOrgExpSrcTypes ;
	private ArrayList<HashMap> subMspSubOrgExpSrcTypes ;
	private String msp_cloud_id;
	private String direct_cloud_id;

	@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runMachine, String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		rep = ExtentManager.getInstance(this.getClass().getSimpleName(), logFolder);
		test = rep.startTest("setup for "+this.getClass().getSimpleName());
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Kanamarlapudi, Chandra Kanth";

		Nooftest = 0;
		// Used for creating a build number with dateformat
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date = new java.util.Date();
		this.buildVersion = buildVersion + "_" + dateFormater.format(date);

		BQName = this.getClass().getSimpleName();
		runningMachine = runMachine;
		System.out.println("The value of hit is " + count1.isstarttimehit());

		if (count1.isstarttimehit() == 0) {
			System.out.println("into creation time");
			creationTime = System.currentTimeMillis();
			count1.setcreationtime(creationTime);

			// creationTime = System.currentTimeMillis();
			try {
				bqdb1.updateTable(BQName, runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0",
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
		
		Response response = spogServer.getCloudAccounts(ti.direct_org1_user1_token, "", test);
		direct_cloud_id = response.then().extract().path("data[0].cloud_account_id");
		
		response = spogServer.getCloudAccounts(ti.direct_org1_user1_token, "", test);
		msp_cloud_id = response.then().extract().path("data[0].cloud_account_id");

		default_instantvm.put("source_type", SourceType.instant_vm.name());
		default_instantvm.put("amount", "0");

		default_machine.put("source_type", SourceType.machine.name());
		default_machine.put("amount", "0");

		default_virtualstandby.put("source_type", SourceType.virtual_standby.name());
		default_virtualstandby.put("amount", "0");

		default_sharedfolder.put("source_type", SourceType.shared_folder.name());
		default_sharedfolder.put("amount", "0");

		default_office365.put("source_type", SourceType.office_365.name());
		default_office365.put("amount", "0");

		default_sharepoint.put("source_type", SourceType.share_point.name());
		default_sharepoint.put("amount", "0");

		default_agentlessvm.put("source_type", SourceType.agentless_vm.name());
		default_agentlessvm.put("amount", "0");
		
		dirExpSrcTypes = postData("direct", ti.direct_org1_user1_token, ti.direct_org1_id, direct_cloud_id);
		subOrgExpSrcTypes = postData("suborg", ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_id, msp_cloud_id);
		subMspSubOrgExpSrcTypes = postData("submsp-suborg", ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_sub_org1_id, msp_cloud_id);
	}
	
	public ArrayList<HashMap> postData(String orgType, String token, String orgId, String site_id) {
		
		SourceType[] sourceTypes = SourceType.values();
		ArrayList<HashMap> expectedsourcetypes = new ArrayList<>();

		for (SourceType sourceType : sourceTypes) {
			
			int numberOfSourcesToCreate = gen_random_index(sourceTypes.length);
			test.log(LogStatus.INFO, "Adding sources of type " + sourceType.name());
			for (int j = 0; j < numberOfSourcesToCreate; j++) {
				String sourceName = spogServer.ReturnRandom("src");
				spogServer.setToken(token);
				Response response = spogServer.createSource(sourceName, sourceType, SourceProduct.cloud_direct, orgId,
						site_id, ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(), "", test);
				spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);
				String source_id = response.then().extract().path("data.source_id");
				
				sourceIds.add(source_id);
			}

			HashMap<String, String> sourceMap = new HashMap<>();
			sourceMap.put("source_type", sourceType.name());
			sourceMap.put("amount", String.valueOf(numberOfSourcesToCreate));

			expectedsourcetypes .add(sourceMap);
		}

		expectedsourcetypes.add(default_sharepoint);
		expectedsourcetypes.add(default_sharedfolder);
		expectedsourcetypes.add(default_virtualstandby);

		return expectedsourcetypes;
	}

	@DataProvider(name="testCases")
	public Object[][] testCases(){
		return new Object[][] {
			//200
			{"Get sources by types in direct organization", ti.direct_org1_user1_token,	dirExpSrcTypes,"",SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get sources by types in direct organization with csr token", ti.csr_token,dirExpSrcTypes,"organization_id="+ti.direct_org1_id,SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get sources by types in direct organization with csr read only token", ti.csr_readonly_token,dirExpSrcTypes,"organization_id="+ti.direct_org1_id,SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get sources by types in sub organization", ti.root_msp1_suborg1_user1_token,	subOrgExpSrcTypes,"",SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get sources by types in sub organization with msp token", ti.root_msp_org1_user1_token,subOrgExpSrcTypes,"organization_id="+ti.root_msp1_suborg1_id,SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get sources by types in sub organization with msp account admin token", ti.root_msp_org1_msp_accountadmin1_token,subOrgExpSrcTypes,"organization_id="+ti.root_msp1_suborg1_id,SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get sources by types in sub organization with csr token", ti.csr_token,subOrgExpSrcTypes,"organization_id="+ti.root_msp1_suborg1_id,SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get sources by types in sub organization with csr read only token", ti.csr_readonly_token,subOrgExpSrcTypes,"organization_id="+ti.root_msp1_suborg1_id,SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get sources by types in sub msp sub organization", ti.msp1_submsp1_suborg1_user1_token,subMspSubOrgExpSrcTypes,"",SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get sources by types in sub msp sub organization with msp token", ti.root_msp1_submsp1_user1_token,subMspSubOrgExpSrcTypes,"organization_id="+ti.msp1_submsp1_sub_org1_id,SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get sources by types in sub msp sub organization with msp account admin token", ti.root_msp1_submsp1_account_admin_token,subMspSubOrgExpSrcTypes,"organization_id="+ti.msp1_submsp1_sub_org1_id,SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get sources by types in sub msp sub organization with csr token", ti.csr_token,subMspSubOrgExpSrcTypes,"organization_id="+ti.msp1_submsp1_sub_org1_id,SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get sources by types in sub msp sub organization with csr read only token", ti.csr_readonly_token,subMspSubOrgExpSrcTypes,"organization_id="+ti.msp1_submsp1_sub_org1_id,SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			
			//401
			{"Get sources by types in an organization using invalid token", "invalid", dirExpSrcTypes, "", SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT},
			{"Get sources by types in an organization using missing token", "", dirExpSrcTypes, "", SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED},
			{"Get sources by types in an organization using null as token", null, dirExpSrcTypes, "", SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED},
		};
	}
	
	@Test(dataProvider = "testCases")
	public void GetSourcesbyTypes(String testCase,
									String token,
									ArrayList<HashMap> expectedsourcetypes,
									String filter,
									int expectedStatusCode,
									SpogMessageCode expectedErrorMessage
									) {

		test = ExtentManager.getNewTest(testCase);

		Response response = spogServer.getsourcesbytypes(filter, token, test);
		spogServer.checksourcesbytypes(response, expectedsourcetypes, filter, expectedStatusCode, expectedErrorMessage, test);
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
	
	@AfterClass
	public void destroySources() {
		test = ExtentManager.getNewTest("After class: destroy sources");
		
		for (String sourceId : sourceIds) {
			spogServer.deleteSourcesById(ti.csr_token, sourceId,test);
		}
	}

	/******************************************************************
	 * RandomFunction
	 ******************************************************************************/
	public int gen_random_index(int num) {
		Random generator = new Random();
		int randomindx = generator.nextInt(num);

		return randomindx;
	}

}
