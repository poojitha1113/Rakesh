package api.policies;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
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

import Constants.ConnectionStatus;
import Constants.DestinationStatus;
import Constants.DestinationType;
import Constants.OSMajor;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGCloudRPSServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGHypervisorsServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

/**
 * API - POST: /policies/{id}/enable
 * Used to enable the policy by specific id
 * After trial/license expiry all the policies will be disable so enable a specific policy this api is called  
 * 
 * @author Rakesh.Chalamala
 * @sprint 32
 */
public class EnablePolicyByIdTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private SPOGDestinationServer spogDestinationServer;
	private Policy4SPOGServer policy4SPOGServer;
	private SPOGHypervisorsServer spogHypervisorsServer;
	private UserSpogServer userSpogServer;
	private SPOGCloudRPSServer spogcloudRPSServer;
	//public int Nooftest;
	private ExtentTest test;
	private String common_password = "Mclaren@2013";
	/*private ExtentReports rep;
    private SQLServerDb bqdb1;
    public int Nooftest;
    private long creationTime;
    private String BQName=null;
    private String runningMachine;
    private testcasescount count1;
    private String buildVersion;*/

	String cloudRPS_id = null;
	String rps_server_name = null;
	String cloud_rps_name = null;
	String cloud_rps_port = null;
	String cloud_rps_protocol = null;
	String cloud_rps_username = null;
	String cloud_rps_password = null;
	String csr_organization_id = null;
	String csr_site_id = null;
	String csr_site_name = null;

	String directOrgPolicies = null;
	String subOrgPolicies = null;

	String prefix = RandomStringUtils.randomAlphanumeric(8);
	private String  org_model_prefix=this.getClass().getSimpleName();
	private String[] datacenters;
	private TestOrgInfo ti;
	private String subMSPsubOrgPolicies;

	@BeforeClass
	@Parameters({ "baseURI", "port","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port,String logFolder,String runningMachine,String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		policy4SPOGServer= new Policy4SPOGServer(baseURI, port);
		spogHypervisorsServer = new SPOGHypervisorsServer(baseURI,port);
		userSpogServer = new UserSpogServer(baseURI, port);
		spogcloudRPSServer = new SPOGCloudRPSServer(baseURI, port);
		rep = ExtentManager.getInstance(this.getClass().getSimpleName(), logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Kanamarlapudi, Chandra Kanth";

		Nooftest=0;
		//Used for creating a build number with dateformat
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date=new java.util.Date();
		this.buildVersion=buildVersion+"_"+dateFormater.format(date);

		BQName=this.getClass().getSimpleName();
		this.runningMachine=runningMachine;
		System.out.println("The value of hit is "+count1.isstarttimehit());

		if( count1.isstarttimehit( ) == 0 ) 
		{
			System.out.println("into creation time");
			creationTime=System.currentTimeMillis();
			count1.setcreationtime(creationTime);

			// creationTime = System.currentTimeMillis();
			try
			{
				bqdb1.updateTable(BQName, this.runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0", String.valueOf(Nooftest), count1.getcreationtime(), "InProgress",author+" and Rest server is "+baseURI.split("//")[1]);
			} 
			catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ti = new TestOrgInfo(spogServer, test);
			test.log(LogStatus.INFO, "Get the datacenter id");
			spogDestinationServer.setToken(ti.csr_token);
			datacenters = spogDestinationServer.getDestionationDatacenterID();

			Properties prop = new Properties();
			try {
				prop.load(new FileInputStream("src\\CloudRPS.properties"));
				cloud_rps_name=prop.getProperty("cloud_rps_name");
				cloud_rps_port=prop.getProperty("cloud_rps_port");
				cloud_rps_protocol=prop.getProperty("cloud_rps_protocol");
				cloud_rps_username=prop.getProperty("cloud_rps_username");
				cloud_rps_password=prop.getProperty("cloud_rps_password");
				csr_site_id = prop.getProperty("csr_site_id");
				csr_site_name=prop.getProperty("csr_site_name");
			}catch (IOException e) {
				e.printStackTrace();
				test.log(LogStatus.INFO, "Failed to load RPS properties file");
			}

			test.log(LogStatus.INFO, "Create cloud RPS");
			Response response = spogcloudRPSServer.createCloudRPS(cloud_rps_name, cloud_rps_port, cloud_rps_protocol, cloud_rps_username, cloud_rps_password, ti.csr_org_id, csr_site_id, datacenters[0], ti.csr_token);
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
			cloudRPS_id = response.then().extract().path("data.server_id");

			HashMap<String, Object> hybridStoreInfo = spogcloudRPSServer.composeCloudHybridStoreInfo(ti.csr_token, spogServer.ReturnRandom("dest"),cloudRPS_id, 
					ti.direct_org1_id, "", datacenters[0], "0.001");
			spogcloudRPSServer.createCloudHybridStore(ti.csr_token, hybridStoreInfo, SpogConstants.SUCCESS_POST, test);

			hybridStoreInfo = spogcloudRPSServer.composeCloudHybridStoreInfo(ti.csr_token, spogServer.ReturnRandom("dest"),cloudRPS_id, 
					ti.root_msp1_suborg1_id, "", datacenters[0], "0.001");
			spogcloudRPSServer.createCloudHybridStore(ti.csr_token, hybridStoreInfo, SpogConstants.SUCCESS_POST, test);

			response = policy4SPOGServer.getPolicies(ti.direct_org1_user1_token, "", SpogConstants.SUCCESS_GET_PUT_DELETE, test);
			ArrayList<String> policyIds = response.then().extract().path("data.policy_id");
			directOrgPolicies = String.join(",",policyIds);

			response = policy4SPOGServer.getPolicies(ti.root_msp1_suborg1_user1_token, "", SpogConstants.SUCCESS_GET_PUT_DELETE, test);
			policyIds = response.then().extract().path("data.policy_id");
			subOrgPolicies = String.join(",",policyIds);

			response = policy4SPOGServer.getPolicies(ti.msp1_submsp1_suborg1_user1_token, "", SpogConstants.SUCCESS_GET_PUT_DELETE, test);
			policyIds = response.then().extract().path("data.policy_id");
			subMSPsubOrgPolicies = String.join(",",policyIds);
		}
	}

		@DataProvider(name="testCases")
		public Object[][] testCases(){
			return new Object[][] {
				//200
				{"Enable policies in direct organization with csr token",
						ti.csr_token, directOrgPolicies, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				{"Enable policies in direct organization with org user token",
						ti.direct_org1_user1_token, directOrgPolicies, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				{"Enable policies in sub organization with csr token",
						ti.csr_token, subOrgPolicies, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				{"Enable policies in sub organization with MSP token",
						ti.root_msp_org1_user1_token, subOrgPolicies, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				{"Enable policies in sub organization with MSP account admin token",
						ti.root_msp_org1_msp_accountadmin1_token, subOrgPolicies, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				{"Enable policies in sub organization with org user token",
						ti.root_msp1_suborg1_user1_token, subOrgPolicies, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				{"Enable policies in submsp sub organization with csr token",
						ti.csr_token, subMSPsubOrgPolicies, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				{"Enable policies in submsp sub organization with MSP token",
						ti.root_msp1_submsp1_user1_token, subMSPsubOrgPolicies, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				{"Enable policies in submsp sub organization with MSP account admin token",
						ti.msp1_submsp1_suborg1_user1_token, subMSPsubOrgPolicies, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				{"Enable policies in submsp sub organization with org user token",
						ti.root_msp1_submsp1_account_admin_token, subMSPsubOrgPolicies, SpogConstants.SUCCESS_GET_PUT_DELETE, null},

				//400
				{"Enable policies in direct organization where policy id is invalid",
						ti.direct_org1_user1_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID},
				{"Enable policies in direct organization where policy id = null",
						ti.direct_org1_user1_token, null, SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID},
				{"Enable policies in sub organization where policy id is invalid",
						ti.root_msp1_suborg1_user1_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID},
				{"Enable policies in sub organization where policy id = null",
						ti.root_msp1_suborg1_user1_token, null, SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID},
				{"Enable policies in submsp sub organization where policy id is invalid",
						ti.msp1_submsp1_suborg1_user1_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID},
				{"Enable policies in submsp sub organization where policy id = null",
						ti.msp1_submsp1_suborg1_user1_token, null, SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID},

				//401
				{"Enable policies with invalid token",
						"invalid", directOrgPolicies, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT},
				{"Enable policies with null as token",
						null, directOrgPolicies, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT},
				{"Enable policies with missing token",
						"", subOrgPolicies, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED},

				//403
				{"Enable policies in direct org with msp org user token",
						ti.root_msp_org1_user1_token, directOrgPolicies, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
				{"Enable policies in direct org with sub org user token",
						ti.root_msp1_suborg1_user1_token, directOrgPolicies, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
				{"Enable policies in direct org with msp account admin user token",
						ti.root_msp_org1_msp_accountadmin1_token, directOrgPolicies, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
				{"Enable policies in direct org with sub msp org user token",
						ti.root_msp1_submsp1_user1_token, directOrgPolicies, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
				{"Enable policies in direct org with sub org user token",
						ti.msp1_submsp1_suborg1_user1_token, directOrgPolicies, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
				{"Enable policies in direct org with msp account admin user token",
						ti.root_msp1_submsp1_account_admin_token, directOrgPolicies, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
				{"Enable policies in direct org with csr readonly user token",
						ti.csr_readonly_token, directOrgPolicies, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},

				{"Enable policies in sub org with csr readonly user token",
						ti.csr_readonly_token, subOrgPolicies, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
				{"Enable policies in sub org with other sub org user token",
						ti.root_msp1_suborg2_user1_token, subOrgPolicies, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
				{"Enable policies in sub org with direct user token",
						ti.direct_org1_user1_token, subOrgPolicies, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
				{"Enable policies in sub org with other msp org2 user token",
						ti.root_msp_org2_user1_token, subOrgPolicies, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
				{"Enable policies in sub org with sub msp user token",
						ti.root_msp1_submsp1_user1_token, subOrgPolicies, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
				{"Enable policies in sub org with other msp org2 user token",
						ti.root_msp1_submsp1_account_admin_token, subOrgPolicies, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},

				{"Enable policies in sub msp sub org with csr readonly user token",
						ti.csr_readonly_token, subMSPsubOrgPolicies, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
				{"Enable policies in sub msp sub org with other sub org user token",
						ti.root_msp1_suborg2_user1_token, subMSPsubOrgPolicies, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
				{"Enable policies in sub msp sub org with direct user token",
						ti.direct_org1_user1_token, subMSPsubOrgPolicies, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
				{"Enable policies in sub msp sub org with other msp org2 user token",
						ti.root_msp_org2_user1_token, subMSPsubOrgPolicies, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
				{"Enable policies in sub msp sub org with sub msp user token",
						ti.root_msp1_submsp2_user1_token, subMSPsubOrgPolicies, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
				{"Enable policies in sub msp sub org with other msp org2 user token",
						ti.root_msp1_submsp2_account_admin_token, subMSPsubOrgPolicies, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},

				//404
				{"Enable policies with policy id that does not exist with direct org user token",
						ti.direct_org1_user1_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.NOT_FOUND_POLICY_WITH_ID},
				{"Enable policies with policy id that does not exist with csr user token",
						ti.csr_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.NOT_FOUND_POLICY_WITH_ID},
				{"Enable policies with policy id that does not exist with sub org user token",
						ti.root_msp1_suborg1_user1_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.NOT_FOUND_POLICY_WITH_ID},
				{"Enable policies with policy id that does not exist with msp user token",
						ti.root_msp_org1_user1_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.NOT_FOUND_POLICY_WITH_ID},
				{"Enable policies with policy id that does not exist with msp account admin user token",
						ti.root_msp_org1_msp_accountadmin1_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.NOT_FOUND_POLICY_WITH_ID},
				{"Enable policies with policy id that does not exist with sub msp sub org user token",
						ti.msp1_submsp1_suborg1_user1_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.NOT_FOUND_POLICY_WITH_ID},
				{"Enable policies with policy id that does not exist with sub msp user token",
						ti.root_msp1_submsp1_user1_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.NOT_FOUND_POLICY_WITH_ID},
				{"Enable policies with policy id that does not exist with sub msp account admin user token",
						ti.root_msp1_submsp1_account_admin_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.NOT_FOUND_POLICY_WITH_ID},

			};
		}

	@Test(dataProvider="testCases")
	public void enablePolicyById(String testCase, 
									String token,
									String policyId,
									int expectedStatusCode,
									SpogMessageCode expectedErrorMessage
									) {
	test=ExtentManager.getNewTest(testCase);

	if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
		String[] policies = policyId.split(",");
		for (int i = 0; i < policies.length; i++) {
			String policy = policies[i];
			test.log(LogStatus.INFO, "Disable the policy first to check enable");
			policy4SPOGServer.disablePolicyByIdWithCheck(token, policy, expectedStatusCode, expectedErrorMessage, test);

			policy4SPOGServer.enablePolicyByIdWithCheck(token, policy, expectedStatusCode, expectedErrorMessage, test);

			test.log(LogStatus.INFO, "Enable the already enabled policy");
			policy4SPOGServer.enablePolicyByIdWithCheck(token, policy, expectedStatusCode, expectedErrorMessage, test);
		}
	}else if (expectedStatusCode == SpogConstants.INSUFFICIENT_PERMISSIONS) {

		String[] policies = policyId.split(",");
		for (int i = 0; i < policies.length; i++) {
			String policy = policies[i];
			policy4SPOGServer.enablePolicyByIdWithCheck(token, policy, expectedStatusCode, expectedErrorMessage, test);
		}			
	}else {
		policy4SPOGServer.enablePolicyByIdWithCheck(token, policyId, expectedStatusCode, expectedErrorMessage, test);
	}
	}

		@AfterMethod
		public void getResult(ITestResult result){
			if(result.getStatus() == ITestResult.FAILURE){
				count1.setfailedcount();
				//remaincases=Nooftest-passedcases-failedcases;
				test.log(LogStatus.FAIL, "Test Case Failed is "+result.getName()+" with parameters as "+Arrays.asList(result.getParameters()) );
				test.log(LogStatus.FAIL, result.getThrowable().getMessage());

			}else if(result.getStatus() == ITestResult.SKIP){
				test.log(LogStatus.SKIP, "Test Case Skipped is "+result.getName());
				count1.setskippedcount();
			}else if(result.getStatus()==ITestResult.SUCCESS){
				count1.setpassedcount();
				//remaincases=Nooftest-passedcases-failedcases;

			}
			// ending test
			//endTest(logger) : It ends the current test and prepares to create HTML report
			rep.endTest(test);
			//rep.flush();
		}

		@AfterClass
		public void updatebd() {

			test.log(LogStatus.INFO, "Get the cloud RPS datastores and delete");
			Response response = spogcloudRPSServer.getCloudHybridStoresOnSpecifiedRPS(ti.csr_token, cloudRPS_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
			ArrayList<HashMap<String, Object>> datastores= response.then().extract().path("data");
			if (!datastores.isEmpty()) {	
				datastores.stream().forEach(datastore->{
					spogcloudRPSServer.destroyCloudHybridStoreById(ti.csr_token, datastore.get("destination_id").toString(), SpogConstants.SUCCESS_GET_PUT_DELETE, test);
					//				deleteCloudRPSDataStore(datastore.get("datastore_id").toString(), ti.csr_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);	
					try {
						Thread.sleep(2500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				});	
			}	
			test.log(LogStatus.INFO, "Delete  the RPS");
			spogcloudRPSServer.deleteCloudRPS(cloudRPS_id, ti.csr_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

			try {
				if(count1.getfailedcount()>0) {
					Nooftest=count1.getpassedcount()+count1.getfailedcount()+count1.getskippedcount();
					bqdb1.updateTable(this.BQName, this.runningMachine, this.buildVersion, String.valueOf(Nooftest), Integer.toString(count1.getpassedcount()), Integer.toString(count1.getfailedcount()), String.valueOf(count1.getskippedcount()), count1.getcreationtime(), "Failed");
				}else {
					Nooftest=count1.getpassedcount()+count1.getfailedcount()+count1.getskippedcount();
					bqdb1.updateTable(this.BQName, this.runningMachine, this.buildVersion, String.valueOf(Nooftest), Integer.toString(count1.getpassedcount()), Integer.toString(count1.getfailedcount()), String.valueOf(count1.getskippedcount()), count1.getcreationtime(), "Passed");
				}
			}catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String orgHasString=this.getClass().getSimpleName();
			System.out.println(orgHasString);
			System.out.println("in father afterclass");
			System.out.println("class in father is:"+orgHasString);
			System.out.println("in father after class");
			destroyOrg(orgHasString);
		}
	}
