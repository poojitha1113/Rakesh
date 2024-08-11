package api.cloudrps.datastores;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.lang.RandomStringUtils;
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
import InvokerServer.GatewayServer;
import InvokerServer.SPOGCloudRPSServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

/** This is to test whether the user can get datastore on a RPS machine by its id successful or not
 *  Each datastore will be assigned to a customer
 *  On creation of datastore the hash,data,index directories will be created on datastore 
 *  Directories information will be part of response
 *  We call the api using the datastore_id which we get on creation of datastore
 *  
 * Prerequisites:
 * 1. Csr token which is used to create all organizations, since he is the super user
 * 2. Direct, msp and sub organizations and users under it
 * 3. RPS registered with CC - to create datastore we need an RPS registered
 * 4. Datastore created on the RPS
 * 
 * For more details on how to register RPS visit CreateCloudRPSTest
 * 
 * @author Rakesh.Chalamala
 * @sprint 14
 */

public class GetCloudRPSDatastoreByIdTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private SPOGDestinationServer spogDestinationServer;
	private UserSpogServer userSpogServer;
	private SPOGCloudRPSServer spogcloudRPSServer;
	//public int Nooftest;
	private ExtentTest test;
	//used for test case count like passed,failed,remaining cases
	int passedcases=0;
	int failedcases=0;
	int skippedcases=0;
	String buildnumber=null;
	
    /*private ExtentReports rep;
    private SQLServerDb bqdb1;
    public int Nooftest;
    private long creationTime;
    private String BQName=null;
    private String runningMachine;
    private testcasescount count1;
    private String buildVersion;*/

	String datacenters[] = new String[5];
	String cloudRPS_id = null;
	String cloudRPS_name;
	ArrayList<HashMap<String, Object>> expectedDatastoreInfo = new ArrayList<>();
	ArrayList<String> datastore_ids = new ArrayList<>();
	String datastore_id = UUID.randomUUID().toString();
	
	String cloud_rps_name = null;
	String cloud_rps_port = null;
	String cloud_rps_protocol = null;
	String cloud_rps_username = null;
	String cloud_rps_password = null;
	String csr_site_id = null;
	String csr_site_name = null;
	
	private String  org_model_prefix=this.getClass().getSimpleName();
	private TestOrgInfo ti;
	
	@BeforeClass
	@Parameters({ "baseURI", "port","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port,String logFolder,String runningMachine,String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		spogcloudRPSServer = new SPOGCloudRPSServer(baseURI, port);
		rep = ExtentManager.getInstance("GetCloudRPSDatastoreByIdTest", logFolder);
		test = rep.startTest("Setup for GetCloudRPSDatastoreByIdTest");
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
		}
		ti = new TestOrgInfo(spogServer, test);
		
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
		
		test.log(LogStatus.INFO, "Get the datacenter id");
		spogDestinationServer.setToken(ti.csr_token);
		datacenters = spogDestinationServer.getDestionationDatacenterID();
		
		test.log(LogStatus.INFO, "Create cloud RPS");
		Response response = spogcloudRPSServer.createCloudRPS(cloud_rps_name, cloud_rps_port, cloud_rps_protocol, cloud_rps_username, cloud_rps_password, ti.csr_org_id, csr_site_id, datacenters[0], ti.csr_token);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		cloudRPS_id = response.then().extract().path("data.server_id");
		cloudRPS_name = response.then().extract().path("data.server_name");
	}
	
	
	@DataProvider(name = "getCloudRPSDatastoresById")
	public final Object[][] getCloudRPSDatastoresById() {
		return new Object[][] {
			{"csr",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,1024,true,"C:\\"+spogServer.ReturnRandom("destination\\data"),"administrator",ti.common_password,
				"C:\\"+spogServer.ReturnRandom("index"),"administrator",ti.common_password, "C:\\dest"+spogServer.ReturnRandom("hash"),"administrator",ti.common_password,4,102400,"C:\\"+spogServer.ReturnRandom("dest"),"administrator",ti.common_password,
				"standard",ti.common_password,false,"abc@arcserve.com","encryption_password",SpogConstants.SUCCESS_GET_PUT_DELETE,null},
		};
	}
	
	@Test(dataProvider = "getCloudRPSDatastoresById",enabled=true)
	public void getCloudRPSDatastoresById_200(String organizationType,
										   String organization_id,
										   String validToken,
										   String datastore_name,
										   boolean dedupe_enabled,
										   boolean encryption_enabled,
										   boolean compression_enabled,
										   String rps_server_id,
										   int block_size,
										   int hash_memory,
										   boolean hash_on_ssd,
										   String data_destination_path,
										   String data_destination_username,
										   String data_destination_password,
										   String index_destination_path,
										   String index_destination_username,
										   String index_destination_password,
										   String hash_destination_path,
										   String hash_destination_username,
										   String hash_destination_password,
										   int concurrent_active_nodes,
										   long capacity,
										   String datastore_path, 
										   String datastore_username,
										   String datastore_password,
										   String compression,
										   String encryption_password,
										   boolean send_email,
										   String to_email,
										   String to_replace,
										   int expectedStatusCode,
										   SpogMessageCode expectedErrorMessage
								   			) {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		SpogMessageCode expectederrorMessage = null;
		
		HashMap<String, Object> expectedData = new HashMap<>();
		test.log(LogStatus.INFO, "Start the  cloud RPS in organization "+organizationType);
		
		test.log(LogStatus.INFO, "Compose the hashmap for deduplication info "+organizationType);
		HashMap<String,Object> deduplicationInfo = spogcloudRPSServer.composededuplicationInfo(block_size, hash_memory,hash_on_ssd, data_destination_path, data_destination_username, data_destination_password, 
				index_destination_path, index_destination_username, index_destination_password, hash_destination_path, hash_destination_username, hash_destination_password);
		
		test.log(LogStatus.INFO, "Compose the hashmap for datastoreproperties info "+organizationType);
		HashMap<String,Object> datastorepropertiesInfo = spogcloudRPSServer.composedatastorepropertiesInfo(concurrent_active_nodes,capacity, datastore_path, datastore_username, datastore_password, compression, encryption_password, deduplicationInfo, send_email, to_email);
		
		test.log(LogStatus.INFO, "Create the cloud rps datastore "+organizationType);
		Response response = spogcloudRPSServer.createCloudRPSDataStore(datastore_name, dedupe_enabled, encryption_enabled, compression_enabled, rps_server_id, datastorepropertiesInfo, validToken);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);
		
		test.log(LogStatus.INFO, "Validate the cloud RPS data store reponse");
		datastore_id = spogcloudRPSServer.validateCloudRPSDataStoreResponse(response, datastore_name, dedupe_enabled, encryption_enabled, compression_enabled, rps_server_id,cloudRPS_name, datastorepropertiesInfo, SpogConstants.SUCCESS_POST, null, "", test);
		
		expectedData.put("datastore_name", datastore_name);
		expectedData.put("datastore_id", datastore_id);
		expectedData.put("dedupe_enabled", dedupe_enabled);
		expectedData.put("encryption_enabled", encryption_enabled);
		expectedData.put("compression_enabled", compression_enabled);
		expectedData.put("rps_server_id", rps_server_id);
		expectedData.put("create_user_id", ti.csr_admin_user_id);
		expectedData.put("server_name", cloudRPS_name);
		expectedData.put("datastore_properties", datastorepropertiesInfo);
		
		/*HashMap<String, Object> actualData = new HashMap<>();
		actualData = response.then().extract().path("data");
		
		//Storing the datastored id to a global variable.
		datastore_id = spogcloudRPSServer.checkForDataStoreData(expectedData, actualData, test);*/
		
		test.log(LogStatus.INFO, "Get cloud rps datastore by id with csr token");
		spogcloudRPSServer.getCloudRPSDatastoreByIdWithCheck(validToken, datastore_id, expectedData, expectedStatusCode, expectedErrorMessage, test);
		
		test.log(LogStatus.INFO, "Get cloud rps datstore by id with csr readonly user token");
		spogcloudRPSServer.getCloudRPSDatastoreByIdWithCheck(ti.csr_readonly_token, datastore_id, expectedData, expectedStatusCode, expectedErrorMessage, test);
		
	}	
	
	@DataProvider(name = "getCloudRPSDatastoresbyId_Invalid")
	public Object[][] getCloudRPSDatastoresbyId_Invalid(){
		return new Object[][] {
			//400
			{"Get the cloud RPS data store by id with null datastore_id", ti.csr_token, null, SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID},
			{"Get the cloud RPS data store by id with invalid datastore_id", ti.csr_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID},
			{"Get the cloud RPS data store by id with null datastore_id & csr readonly user token", ti.csr_readonly_token, null, SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID},
			{"Get the cloud RPS data store by id with invalid datastore_id & csr readonly user token", ti.csr_readonly_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID},
			
			//401
			{"Get the cloud RPS data store by id with empty token", "", datastore_id, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED},
			{"Get the cloud RPS data store by id with invalid token", "invalid", datastore_id, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT},
			
			//403
			{"Get the cloud RPS data store by id with Direct organization user token", ti.direct_org1_user1_token,datastore_id, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Get the cloud RPS data store by id with MSP organization user token", ti.root_msp_org1_user1_token, datastore_id, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Get the cloud RPS data store by id with Sub organization user token", ti.root_msp1_suborg1_user1_token, datastore_id, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Get the cloud RPS data store by id with MSP ACCOUNT ADMIN token", ti.root_msp_org1_msp_accountadmin1_token, datastore_id, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Get the cloud RPS data store by id with SUB MSP organization user token", ti.root_msp1_submsp1_user1_token, datastore_id, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Get the cloud RPS data store by id with SUB MSP Sub organization user token", ti.msp1_submsp1_suborg1_user1_token, datastore_id, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Get the cloud RPS data store by id with SUB MSP ACCOUNT ADMIN token", ti.root_msp1_submsp1_account_admin_token, datastore_id, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			
			//404
			{"Get the cloud RPS data store by id with non-existing datastore_id", ti.csr_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.DATASTORE_WITH_ID_DOESNOT_EXIST},
			{"Get the cloud RPS data store by id with non-existing datastore_id & csr readonly user token", ti.csr_readonly_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.DATASTORE_WITH_ID_DOESNOT_EXIST},
		};
	}
	@Test(dataProvider = "getCloudRPSDatastoresbyId_Invalid")
	public void getCloudRPSDatastoresbyId_Invalid(String caseType, String token, String datastore_id, int expectedStatusCode, SpogMessageCode expectedErrorMessage) {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		
		test.log(LogStatus.INFO, caseType);
		spogcloudRPSServer.getCloudRPSDatastoreByIdWithCheck(token, datastore_id, null, expectedStatusCode, expectedErrorMessage, test);
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
		
		/*//Un assign and delete destination if any of case failed or missed.
		if (destination_id != null) {
			spogDestinationServer.unassigndatastore(destination_id, ti.csr_token, test);
			spogDestinationServer.deletedestinationbydestination_Id(destination_id, ti.csr_token, test);
		}*/		
		
		test.log(LogStatus.INFO, "Get the cloud RPS datastores and delete");
		Response response = spogcloudRPSServer.getCloudRPSDataStoresForSpecifiedRPSWithCheck(ti.csr_token, cloudRPS_id, null, null, null, 1, 100, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		ArrayList<HashMap<String, Object>> datastores= response.then().extract().path("data");
		if (!datastores.isEmpty()) {	
			datastores.stream().forEach(datastore->{
				spogcloudRPSServer.destroyCloudRPSDataStore(datastore.get("datastore_id").toString(), ti.csr_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);	
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
			e.printStackTrace();
		  }catch (IOException e) {
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
