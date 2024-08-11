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

import Constants.DestinationStatus;
import Constants.DestinationType;
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

/** This is to test whether the udatastore deletion on a RPS machine successful or not
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

public class GetDatastoreForSpecifiedDestinationTest extends base.prepare.Is4Org  {
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
	
   /* private ExtentReports rep;
    private SQLServerDb bqdb1;
    public int Nooftest;
    private long creationTime;
    private String BQName=null;
    private String runningMachine;
    private testcasescount count1;
    private String buildVersion;*/

	String datacenters[] = new String[5];
	String cloudRPS_id = null;
	String cloudRPS_name = null;
	ArrayList<HashMap<String, Object>> expectedDatastoreInfo = new ArrayList<>();
	ArrayList<String> datastore_ids = new ArrayList<>();
	HashMap<String, Object> rps_data = new HashMap<>();
	HashMap<String, Object> expectedResponse = null;
	
	String cloudAccountSecret;
	String direct_cloud_id;
	String msp_cloud_id;
	String direct_cloud_token;
	String msp_cloud_token;
	Response response;
	
	String direct_direct_dest_id;
	String msp_baas_destination_ID;
	String sub_org_direct_dest_id;
	
	String direct_hybrid_destination_ID;
	String msp_hybrid_destination_ID;
	String sub_orga_hybrid_destination_ID;
	
	String cloud_rps_name = null;
	String cloud_rps_port = null;
	String cloud_rps_protocol = null;
	String cloud_rps_username = null;
	String cloud_rps_password = null;
	String csr_site_id = null;
	String csr_site_name = null;
	
	boolean flag = true;
	String prefix = RandomStringUtils.randomAlphanumeric(8);
	
	private String  org_model_prefix=this.getClass().getSimpleName();
	private TestOrgInfo ti;
	private Object rootmsp_cloud_id;
	
	@BeforeClass
	@Parameters({ "baseURI", "port","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port,String logFolder,String runningMachine,String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		spogcloudRPSServer = new SPOGCloudRPSServer(baseURI, port);
		rep = ExtentManager.getInstance("CreateRPSDataStoreTest", logFolder);
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
		
		test.log(LogStatus.INFO, "Get the datacenter id");
		datacenters = spogDestinationServer.getDestionationDatacenterID();
		
		test.log(LogStatus.INFO, "Create cloud RPS");
		Response response = spogcloudRPSServer.createCloudRPS(cloud_rps_name, cloud_rps_port, cloud_rps_protocol, cloud_rps_username, cloud_rps_password, ti.csr_org_id, csr_site_id, datacenters[0], ti.csr_token);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		cloudRPS_id = response.then().extract().path("data.server_id");
		cloudRPS_name = response.then().extract().path("data.server_name");
		rps_data = response.then().extract().path("data");

	}
	
	@Test
	public void createDestinations() {
		this.cloudAccountSecret= "cloudAccountSecret";
		response = spogServer.getCloudAccounts(ti.direct_org1_user1_token, "", test);
        direct_cloud_id = response.then().extract().path("data[0].cloud_account_id");
        
        response = spogServer.getCloudAccounts(ti.root_msp_org1_user1_token, "", test);
        rootmsp_cloud_id = response.then().extract().path("data[0].cloud_account_id");
		
		response = spogDestinationServer.getDestinations(ti.direct_org1_user1_token, "destination_type="+DestinationType.cloud_direct_volume, test);
		direct_direct_dest_id = response.then().extract().path("data[0].destination_id");
		if (direct_direct_dest_id == null || direct_direct_dest_id.isEmpty()) {
			spogDestinationServer.setToken(ti.direct_org1_user1_token);
			direct_direct_dest_id = spogDestinationServer.createDestinationWithCheck(UUID.randomUUID().toString(),ti.direct_org1_id, null, datacenters[0], DestinationType.cloud_direct_volume.toString(), DestinationStatus.running.toString(), 
					"20","","normal",RandomStringUtils.randomAlphanumeric(4)+"host-t", "2M", "2M", 
					"0","0", "31", "0", "2", "0", "5", "true", "1", "true",spogServer.ReturnRandom("dest"), test);
		}
		
		response = spogDestinationServer.getDestinations(ti.root_msp1_suborg1_user1_token, "destination_type="+DestinationType.cloud_direct_volume, test);
		sub_org_direct_dest_id = response.then().extract().path("data[0].destination_id");
		if (sub_org_direct_dest_id == null || sub_org_direct_dest_id.isEmpty()) {
			spogDestinationServer.setToken(ti.root_msp_org1_user1_token);
			sub_org_direct_dest_id = spogDestinationServer.createDestinationWithCheck(UUID.randomUUID().toString(),ti.root_msp1_suborg1_id, null, datacenters[0], DestinationType.cloud_direct_volume.toString(), DestinationStatus.running.toString(), 
					"20","","normal",RandomStringUtils.randomAlphanumeric(4)+"host-t", "2M", "2M", 
					"0","0", "31", "0", "2", "0", "5", "true", "1", "true",spogServer.ReturnRandom("dest"), test);
		}
		
		//Create destinations of type cloud_hybrid_store
		direct_hybrid_destination_ID = spogDestinationServer.createHybridDestination(ti.direct_org1_user1_token, ti.direct_org1_id, datacenters[0], DestinationType.cloud_hybrid_store.toString(), spogServer.ReturnRandom("rakesh-hybrid"), DestinationStatus.running.toString(), test);				
		sub_orga_hybrid_destination_ID = spogDestinationServer.createHybridDestination(ti.root_msp_org1_user1_token, ti.root_msp1_suborg1_id, datacenters[0], DestinationType.cloud_hybrid_store.toString(), spogServer.ReturnRandom("rakesh-hybrid"), DestinationStatus.running.toString(), test);

	}
	
	
	@DataProvider(name = "postcloudRPSDatastores")
	public final Object[][] postcloudRPSDatastores() {
		return new Object[][] {
			{"direct organization",ti.csr_org_id,cloudRPS_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, 4,2048,false,"C:\\"+spogServer.ReturnRandom("destination\\data"),"administrator",ti.common_password,
				"C:\\"+spogServer.ReturnRandom("index"),"administrator",ti.common_password, "C:\\dest"+spogServer.ReturnRandom("hash"),"administrator",ti.common_password,4,102400,"C:\\"+spogServer.ReturnRandom("dest"),"administrator",ti.common_password,
				"standard",ti.common_password,false,"abc@arcserve.com","encryption_password"},
			{"sub organization",ti.csr_org_id,cloudRPS_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, 4,2048,false,"C:\\"+spogServer.ReturnRandom("destination\\data"),"administrator",ti.common_password,
					"C:\\"+spogServer.ReturnRandom("index"),"administrator",ti.common_password, "C:\\dest"+spogServer.ReturnRandom("hash"),"administrator",ti.common_password,4,102400,"C:\\"+spogServer.ReturnRandom("dest"),"administrator",ti.common_password,
				"standard","a",false,"abc@arcserve.com","block_size"},
		};
	}
	
	@Test(dataProvider = "postcloudRPSDatastores",dependsOnMethods= {"createDestinations"})
	public void postcloudRPSDatastores_200(String datastoreForOrg,
										   String organization_id,
										   String server_id,
										   String validToken,
										   String datastore_name,
										   boolean dedupe_enabled,
										   boolean encryption_enabled,
										   boolean compression_enabled,
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
										   String to_replace
								   			) {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+datastoreForOrg);
		SpogMessageCode expectederrorMessage = null;
		String datastore_id = null;
		String destination_id = null;
		HashMap<String, Object> expectedData = new HashMap<>();
		
		test.log(LogStatus.INFO, "Compose the hashmap for deduplication info ");
		HashMap<String,Object> deduplicationInfo = spogcloudRPSServer.composededuplicationInfo(block_size, hash_memory,hash_on_ssd, data_destination_path, data_destination_username, data_destination_password, 
				index_destination_path, index_destination_username, index_destination_password, hash_destination_path, hash_destination_username, hash_destination_password);
		
		test.log(LogStatus.INFO, "Compose the hashmap for datastoreproperties info ");
		HashMap<String,Object> datastorepropertiesInfo = spogcloudRPSServer.composedatastorepropertiesInfo(concurrent_active_nodes,capacity, datastore_path, datastore_username, datastore_password, compression, encryption_password, deduplicationInfo, send_email, to_email);
		
		test.log(LogStatus.INFO, "Create the cloud rps datastore "+datastoreForOrg);
		response = spogcloudRPSServer.createCloudRPSDataStore(datastore_name, dedupe_enabled, encryption_enabled, compression_enabled, server_id, datastorepropertiesInfo, validToken);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		
		test.log(LogStatus.INFO, "Validate the cloud RPS data store reponse");
//		datastore_id = spogcloudRPSServer.validateCloudRPSDataStoreResponse(response, datastore_name, dedupe_enabled, encryption_enabled, compression_enabled, rps_server_id, datastorepropertiesInfo, SpogConstants.SUCCESS_POST, null, "", test);
		
		spogServer.setToken(validToken);
		/*expectedData.put("datastore_name", datastore_name);
		expectedData.put("dedupe_enabled", dedupe_enabled);
		expectedData.put("encryption_enabled", encryption_enabled);
		expectedData.put("compression_enabled", compression_enabled);
		expectedData.put("rps_server_id", server_id);
		expectedData.put("datastore_properties", datastorepropertiesInfo);
		expectedData.put("create_user_id", csr_user_id);*/
		
		expectedData = response.then().extract().path("data");
		
		datastore_id = response.then().extract().path("data.datastore_id");
		
		expectedDatastoreInfo.add(expectedData);
		datastore_ids.add(datastore_id);
		
		//Assinging datastores to destination
		if (datastoreForOrg.contains("direct")) {
			test.log(LogStatus.INFO, "Assign datastore to destination of "+datastoreForOrg);
			destination_id = direct_hybrid_destination_ID;
		}else if (datastoreForOrg.contains("sub")) {
			test.log(LogStatus.INFO, "Assign datastore to destination of "+datastoreForOrg);
			destination_id = sub_orga_hybrid_destination_ID;
		}		
		response = spogDestinationServer.assigndatastore(destination_id, datastore_id, validToken, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		
	}
	
	@DataProvider(name="getDatastore")
	public Object[][] getDatastorevalid(){
		return new Object[][] {
			//csr token
			{"direct",direct_hybrid_destination_ID,datastore_ids.get(0),expectedDatastoreInfo.get(0),ti.csr_token,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"suborg",sub_orga_hybrid_destination_ID,datastore_ids.get(1),expectedDatastoreInfo.get(1),ti.csr_token,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"suborg mspAccAdminToken",sub_orga_hybrid_destination_ID,datastore_ids.get(1),expectedDatastoreInfo.get(1),ti.csr_token,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			
			//csr readonly user token
			{"direct",direct_hybrid_destination_ID,datastore_ids.get(0),expectedDatastoreInfo.get(0),ti.csr_readonly_token,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"suborg",sub_orga_hybrid_destination_ID,datastore_ids.get(1),expectedDatastoreInfo.get(1),ti.csr_readonly_token,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"suborg mspAccAdminToken",sub_orga_hybrid_destination_ID,datastore_ids.get(1),expectedDatastoreInfo.get(1),ti.csr_readonly_token,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			
			//org user token
			{"direct",direct_hybrid_destination_ID,datastore_ids.get(0),expectedDatastoreInfo.get(0),ti.direct_org1_user1_token,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"suborg",sub_orga_hybrid_destination_ID,datastore_ids.get(1),expectedDatastoreInfo.get(1),ti.root_msp1_suborg1_user1_token,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"suborg mspAccAdminToken",sub_orga_hybrid_destination_ID,datastore_ids.get(1),expectedDatastoreInfo.get(1),ti.root_msp_org1_msp_accountadmin1_token,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			
		};
	}
	
	@Test(dataProvider="getDatastore",dependsOnMethods= {"postcloudRPSDatastores_200","createDestinations"})
	public void getDatastoreForSpecifiedDestination(String organizationType, String destination_id, String datastore_id, HashMap<String, Object> expectedDatastoreInfo,
													String validToken, int expectedStatusCode, SpogMessageCode expectedErrorMessage) {
				
		expectedResponse = new HashMap<>();
		
		expectedResponse = rps_data;
		expectedResponse.put("datastore", expectedDatastoreInfo);
		
		test.log(LogStatus.INFO, "Get datastore for specified destination");
		spogcloudRPSServer.getDatastoreForSpecifiedDestination(validToken, destination_id, expectedResponse, expectedStatusCode, expectedErrorMessage, test);
	}
	
	
	@DataProvider(name="getDatastoreInvalid")
	public Object[][] getDatastoreInvalid(){
		return new Object[][] {
			//400 cases
			{"Get datastore with null as destination id & csr token",null,ti.csr_token,SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_DESTINATIONID_NOT_UUID},
			{"Get datastore with invalid destination id & csr token","INVALID",ti.csr_token,SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_DESTINATIONID_NOT_UUID},
			{"Get datastore with null as destination id & csr readonly user token",null,ti.csr_readonly_token,SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_DESTINATIONID_NOT_UUID},
			{"Get datastore with invalid destination id & csr readonly user token","INVALID",ti.csr_readonly_token,SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_DESTINATIONID_NOT_UUID},
			{"Get datastore with invalid destination id","INVALID",ti.direct_org1_user1_token,SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_DESTINATIONID_NOT_UUID},
			{"Get datastore with null as destination id",null,ti.direct_org1_user1_token,SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_DESTINATIONID_NOT_UUID},
			{"Get datastore with invalid destination id","INVALID",ti.root_msp_org1_user1_token,SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_DESTINATIONID_NOT_UUID},
			{"Get datastore with null as destination id",null,ti.root_msp_org1_user1_token,SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_DESTINATIONID_NOT_UUID},
			{"Get datastore with invalid destination id","INVALID",ti.root_msp1_suborg1_user1_token,SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_DESTINATIONID_NOT_UUID},
			{"Get datastore with null as destination id",null,ti.root_msp1_suborg1_user1_token,SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_DESTINATIONID_NOT_UUID},
			{"Get datastore with invalid destination id","INVALID",ti.root_msp_org1_msp_accountadmin1_token,SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_DESTINATIONID_NOT_UUID},
			
			//401 cases
			{"Get datastore with invalid token",UUID.randomUUID().toString(),"INVALID TOKEN",SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT},
			{"Get datastore with empty token", UUID.randomUUID().toString(),"",SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTHENTICATION_FAILED},
			
			//404 cases
			{"Get datastore for a destination that does not exist with csr token", UUID.randomUUID().toString(),ti.csr_token,SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.DESTINATION_NOT_FOUND},
			{"Get datastore for a destination that does not exist with csr readonly user token", UUID.randomUUID().toString(),ti.csr_readonly_token,SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.DESTINATION_NOT_FOUND},
			{"Get datastore for a destination that does not exist for direct org", UUID.randomUUID().toString(),ti.direct_org1_user1_token,SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.DESTINATION_NOT_FOUND},
			{"Get datastore for a destination that does not exist for sub org", UUID.randomUUID().toString(),ti.root_msp1_suborg1_user1_token,SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.DESTINATION_NOT_FOUND},
			{"Get datastore for a destination that does not exist for sub org with msp_account_admin token", UUID.randomUUID().toString(),ti.root_msp_org1_msp_accountadmin1_token,SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.DESTINATION_NOT_FOUND},
			
			//Get datastore for destination of type cloud_direct_volume
			{"Get datastore for destination of type cloud_direct_volume in direct organization",direct_direct_dest_id,ti.direct_org1_user1_token,SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.DESTINATION_TYPE_MUST_BE_CLOUD_HYBRID},
			{"Get datastore for destination of type cloud_direct_volume in sub organization",sub_org_direct_dest_id,ti.root_msp1_suborg1_user1_token,SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.DESTINATION_TYPE_MUST_BE_CLOUD_HYBRID},
			{"Get datastore for destination of type cloud_direct_volume in sub organization with msp_account_admin token",sub_org_direct_dest_id,ti.root_msp_org1_msp_accountadmin1_token,SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.DESTINATION_TYPE_MUST_BE_CLOUD_HYBRID},
			//csr tokens
			{"Get datastore for destination of type cloud_direct_volume in direct organization with csr token",direct_direct_dest_id,ti.csr_token,SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.DESTINATION_TYPE_MUST_BE_CLOUD_HYBRID},
			{"Get datastore for destination of type cloud_direct_volume in sub organization with csr token",sub_org_direct_dest_id,ti.csr_token,SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.DESTINATION_TYPE_MUST_BE_CLOUD_HYBRID},
			{"Get datastore for destination of type cloud_direct_volume in direct organization with csr readonly user token",direct_direct_dest_id,ti.csr_readonly_token,SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.DESTINATION_TYPE_MUST_BE_CLOUD_HYBRID},
			{"Get datastore for destination of type cloud_direct_volume in sub organization with csr readonly user token",sub_org_direct_dest_id,ti.csr_readonly_token,SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.DESTINATION_TYPE_MUST_BE_CLOUD_HYBRID},
			
			
			//Delete and get the datastore
			{"Get datastore for a destination after deleting the datastore for direct org",direct_hybrid_destination_ID,ti.direct_org1_user1_token,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"Get datastore for a destination after deleting the datastore for sub org",sub_orga_hybrid_destination_ID,ti.root_msp1_suborg1_user1_token,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"Get datastore for a destination after deleting the datastore for sub org mspaccountadmin token",sub_orga_hybrid_destination_ID,ti.root_msp_org1_msp_accountadmin1_token,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			//csr tokens
			{"Get datastore for a destination after deleting the datastore for direct org with csr token",direct_hybrid_destination_ID,ti.csr_token,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"Get datastore for a destination after deleting the datastore for sub org with csr token",sub_orga_hybrid_destination_ID,ti.csr_token,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"Get datastore for a destination after deleting the datastore for direct org with csr readonly user token",direct_hybrid_destination_ID,ti.csr_readonly_token,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"Get datastore for a destination after deleting the datastore for sub org with csr readonly user token",sub_orga_hybrid_destination_ID,ti.csr_readonly_token,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			
		};
	}
	
	@Test(dataProvider="getDatastoreInvalid",dependsOnMethods= {"postcloudRPSDatastores_200","getDatastoreForSpecifiedDestination","createDestinations"})
	public void getDatastoresForSpecifiedDestinationInvalid(String caseType,String destination_id, String token, int expectedStatusCode, SpogMessageCode expectedErrorMessage) {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		
		
		if(caseType.contains("deleting") && flag) {
			
			test.log(LogStatus.INFO, "Un assigning datastores");
			spogDestinationServer.unassigndatastore(direct_hybrid_destination_ID, ti.csr_token, test);
			spogDestinationServer.unassigndatastore(sub_orga_hybrid_destination_ID, ti.csr_token, test);
			
			for (int i = 0; i < datastore_ids.size(); i++) {
				test.log(LogStatus.INFO, "Deleting datastore with id: "+datastore_ids.get(i));
				spogcloudRPSServer.destroyCloudRPSDataStore(datastore_ids.get(i), ti.csr_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			}
			flag = false;
		}
		
		test.log(LogStatus.INFO, caseType);
		spogcloudRPSServer.getDatastoreForSpecifiedDestination(token, destination_id, null, expectedStatusCode, expectedErrorMessage, test);
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
		
		//Un assign and delete destination if any of case failed or missed.
		if (direct_hybrid_destination_ID != null) {
			
			spogDestinationServer.unassigndatastore(direct_hybrid_destination_ID, ti.csr_token, test);
			spogDestinationServer.deletedestinationbydestination_Id(direct_hybrid_destination_ID, ti.csr_token, test);
		}
		if(sub_orga_hybrid_destination_ID != null) {
			spogDestinationServer.unassigndatastore(sub_orga_hybrid_destination_ID, ti.csr_token, test);
			spogDestinationServer.deletedestinationbydestination_Id(sub_orga_hybrid_destination_ID, ti.csr_token, test);
		}		
		
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
