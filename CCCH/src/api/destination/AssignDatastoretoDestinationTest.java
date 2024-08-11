package api.destination;

import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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

import com.google.common.util.concurrent.CycleDetectingLockFactory.Policy;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.DestinationStatus;
import Constants.DestinationType;
import Constants.PolicyBackupType;
import Constants.PolicyType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.GatewayServer.siteType;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGCloudRPSServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

/** This is to test whether the csr user can assign the datastore to CH destination successfully or not
 *  Each datastore will be assigned to a customer
 *  On creation of datastore the hash,data,index directories will be created on datastore 
 *  Directories information will be part of response
 *  We call the api using datastore id which we get on creation of datstore and CH destination id
 *  
 * Prerequisites:
 * 1. Csr token which is used to create all organizations, since he is the super user
 * 2. Direct, msp and sub organizations and users under it
 * 3. RPS registered with CC - to create datastore we need an RPS registered
 * 4. Datastores created on the RPS
 * 5. Destination of CH type created for the organizationss
 * 
 * For more details on how to register RPS visit CreateCloudRPSTest
 * 
 * Cloud Hybrid policy auto creation test also part of this class written a separate method
 * CH free trial/license must be activated for policy auto creation  
 * 
 * 
 * @author Rakesh.Chalamala
 * @sprint 14
 */

public class AssignDatastoretoDestinationTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private SPOGDestinationServer spogDestinationServer;
	private Policy4SPOGServer policy4SPOGServer;
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
	String direct_direct_dest_id=null;
	String sub_orga_baas_destionation_ID = null;
	String cloudRPS_id = null;
	ArrayList<String> destination_ids = new ArrayList<>();
	String cloud_rps_name = null;
	String cloud_rps_port = null;
	String cloud_rps_protocol = null;
	String cloud_rps_username = null;
	String cloud_rps_password = null;
	String csr_site_id = null;
	String csr_site_name = null;
	String direct_hybrid_destination_ID = null;
	String sub_orga_hybrid_destination_ID = null;
	
	private String  org_model_prefix=this.getClass().getSimpleName();
	private TestOrgInfo ti;
	private Object rootmsp_cloud_id;
	private Object direct_cloud_id;
	
	@BeforeClass
	@Parameters({ "baseURI", "port","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String logFolder,String runningMachine,String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		policy4SPOGServer = new Policy4SPOGServer(baseURI, port);
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
				//Update the status as inprogress to BQ portal
			    try {
				    bqdb1.updateTable(BQName, this.runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0", String.valueOf(Nooftest), count1.getcreationtime(), "InProgress",author+" and Rest server is "+baseURI.split("//")[1]);
			    } 
			    catch (ClientProtocolException e) {
				       e.printStackTrace();
			    } 
			    catch (IOException e) {
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
		
		test.log(LogStatus.INFO, "Create cloud RPS");
		Response response = spogcloudRPSServer.createCloudRPS(cloud_rps_name, cloud_rps_port, cloud_rps_protocol, cloud_rps_username, cloud_rps_password, ti.csr_org_id, csr_site_id, datacenters[0], ti.csr_token);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		cloudRPS_id = response.then().extract().path("data.server_id");
		cloud_rps_name = response.then().extract().path("data.server_name");

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
		
		response = spogDestinationServer.getDestinations(ti.root_msp1_suborg1_user1_token, "", test);
		sub_orga_baas_destionation_ID = response.then().extract().path("data[0].destination_id");
		if (sub_orga_baas_destionation_ID == null || sub_orga_baas_destionation_ID.isEmpty()) {
			spogDestinationServer.setToken(ti.root_msp_org1_user1_token);
			sub_orga_baas_destionation_ID = spogDestinationServer.createDestinationWithCheck(UUID.randomUUID().toString(),ti.root_msp1_suborg1_id, null, datacenters[0], DestinationType.cloud_direct_volume.toString(), DestinationStatus.running.toString(), 
					"20","","normal",RandomStringUtils.randomAlphanumeric(4)+"host-t", "2M", "2M", 
					"0","0", "31", "0", "2", "0", "5", "true", "1", "true",spogServer.ReturnRandom("dest"), test);
		}
		
		//Create destinations of type cloud_hybrid_store
		direct_hybrid_destination_ID = spogDestinationServer.createHybridDestination(ti.direct_org1_user1_token, ti.direct_org1_id, datacenters[0], DestinationType.cloud_hybrid_store.toString(), spogServer.ReturnRandom("rakesh-hybrid"), DestinationStatus.running.toString(), test);				
		sub_orga_hybrid_destination_ID = spogDestinationServer.createHybridDestination(ti.root_msp_org1_user1_token, ti.root_msp1_suborg1_id, datacenters[0], DestinationType.cloud_hybrid_store.toString(), spogServer.ReturnRandom("rakesh-hybrid"), DestinationStatus.running.toString(), test);

	}
	
	@DataProvider(name = "assigncloudRPSDatastoretoDestination")
	public final Object[][] assigncloudRPSDatastoretoDestination() {
		return new Object[][] {
			
			{"Assign datastore to direct org",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id,cloud_rps_name, 4,2048,false,"C:\\"+spogServer.ReturnRandom("dest")+"\\data","administrator",ti.common_password,
				"C:\\"+spogServer.ReturnRandom("dest")+"\\index","administrator",ti.common_password, "C:\\"+spogServer.ReturnRandom("dest")+"\\hash","administrator",ti.common_password,4,1024,
				"C:\\"+spogServer.ReturnRandom("dest"),"administrator",ti.common_password,"standard","abc",true,"abc@arcserve.com",
				ti.direct_org1_id, ti.direct_org1_user1_token, direct_cloud_id,datacenters[0],"cloud_hybrid_store","4", "true", "4", "true" ,spogServer.ReturnRandom("destname"), DestinationStatus.running.toString()},
			
			/*{"Assign datastore to msp org",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id,cloud_rps_name, 16,2048,false,"C:\\"+spogServer.ReturnRandom("dest")+"\\data","administrator",ti.common_password,
				"C:\\"+spogServer.ReturnRandom("dest")+"\\index","administrator",ti.common_password, "C:\\"+spogServer.ReturnRandom("dest")+"\\hash","administrator",ti.common_password,4,1024,
				"C:\\"+spogServer.ReturnRandom("dest"),"administrator",ti.common_password,"maximum","abc",true,"abc@arcserve.com",
				msp_organization_id, ti.root_msp_org1_user1_token, msp_site_id,datacenters[0],"cloud_hybrid_store","4", "true", "16", "true" ,spogServer.ReturnRandom("destname"), DestinationStatus.running.toString()},
			*/
			{"Assign datastore to sub org",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id,cloud_rps_name, 32,2048,false,"C:\\"+spogServer.ReturnRandom("dest")+"\\data","administrator",ti.common_password,
				"C:\\"+spogServer.ReturnRandom("dest")+"\\index","administrator",ti.common_password, "C:\\"+spogServer.ReturnRandom("dest")+"\\hash","administrator",ti.common_password,4,1024,
				"C:\\"+spogServer.ReturnRandom("dest"),"administrator",ti.common_password,"standard","abc",true,"abc@arcserve.com",
				ti.root_msp1_suborg1_id, ti.root_msp_org1_user1_token, rootmsp_cloud_id,datacenters[0],"cloud_hybrid_store","4", "true", "32", "true" ,spogServer.ReturnRandom("destname"), DestinationStatus.running.toString()},
			
			{"Assign datastore to sub org using msp account admin token",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id,cloud_rps_name, 4,2048,false,"C:\\"+spogServer.ReturnRandom("dest")+"\\data","administrator",ti.common_password,
				"C:\\"+spogServer.ReturnRandom("dest")+"\\index","administrator",ti.common_password, "C:\\"+spogServer.ReturnRandom("dest")+"\\hash","administrator",ti.common_password,4,1024,
				"C:\\"+spogServer.ReturnRandom("dest"),"administrator",ti.common_password,"standard","abc",true,"abc@arcserve.com",
				ti.root_msp1_suborg1_id, ti.root_msp_org1_msp_accountadmin1_token, rootmsp_cloud_id,datacenters[0],"cloud_hybrid_store","4", "true", "4", "true" ,spogServer.ReturnRandom("destname"), DestinationStatus.running.toString()},
			
		};
	}
	
	
	@Test(dataProvider = "assigncloudRPSDatastoretoDestination")
	/*@Test
	@Parameters({ "organizationType", "datastore_name","dedupe_enabled", "encryption_enabled","compression_enabled","block_size","hash_memory","hash_on_ssd",
		"data_destination_path","data_destination_username","data_destination_password",
		"index_destination_path","index_destination_username","index_destination_password",
		"hash_destination_path","hash_destination_username","hash_destination_password","concurrent_active_nodes","datastore_path","datastore_username","datastore_password",
		"compression","encryption_password","send_email","to_email",
		"destination_type","dest_concurrent_active_node","dest_is_deduplicated","dest_block_size","dest_is_compressed","destination_name"})*/
	public void assigncloudRPSDatastoretoDestination(String organizationType,
													   String organization_id,
													   String validToken,
													   String datastore_name,
													   boolean dedupe_enabled,
													   boolean encryption_enabled,
													   boolean compression_enabled,
													   String rps_server_id,
													   String rps_server_name,
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
													   String dest_orgnization_id,
													   String dest_user_token,
													   String dest_site_id,
													   String dest_datacenter,
													   String destination_type,
													   String dest_concurrent_active_node, 
													   String dest_is_deduplicated, 
													   String dest_block_size, 
													   String dest_is_compressed,
													   String destination_name,
													   String destination_status
													   ) {

		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		
				/*************** Create destinations and datstores *******************/
		
		test.log(LogStatus.INFO, "Compose the hashmap for deduplication info "+organizationType);
		HashMap<String,Object> deduplicationInfo = spogcloudRPSServer.composededuplicationInfo(block_size, hash_memory,hash_on_ssd, data_destination_path, data_destination_username, data_destination_password, 
				index_destination_path, index_destination_username, index_destination_password, hash_destination_path, hash_destination_username, hash_destination_password);
		
		test.log(LogStatus.INFO, "Compose the hashmap for datastoreproperties info "+organizationType);
		HashMap<String,Object> datastorepropertiesInfo = spogcloudRPSServer.composedatastorepropertiesInfo(concurrent_active_nodes,capacity, datastore_path, datastore_username, datastore_password, compression, encryption_password, deduplicationInfo, Boolean.valueOf(send_email), to_email);
				
		test.log(LogStatus.INFO, "Create the cloud rps datastore "+organizationType);
		Response response = spogcloudRPSServer.createCloudRPSDataStore(datastore_name, dedupe_enabled, encryption_enabled, compression_enabled, rps_server_id, datastorepropertiesInfo, validToken);
		
		test.log(LogStatus.INFO, "Perform the response validation");
		String datastore_id = spogcloudRPSServer.validateCloudRPSDataStoreResponse(response, datastore_name, dedupe_enabled, encryption_enabled, compression_enabled, rps_server_id,rps_server_name, datastorepropertiesInfo, SpogConstants.SUCCESS_POST, null, "", test);
		
		test.log(LogStatus.INFO,"Create a cloud hybrid datastore for organization");
		spogDestinationServer.setToken(dest_user_token);
		String destination_id = spogDestinationServer.createHybridDestination(dest_user_token, dest_orgnization_id, dest_datacenter, destination_type, destination_name, destination_status, test);
		destination_ids.add(destination_id);
		
		//Create second destination to check error 400 i.e., One datastore can only be assigned to one destination
		String destination_id_1 = spogDestinationServer.createHybridDestination(dest_user_token, dest_orgnization_id, dest_datacenter, destination_type, destination_name+1, destination_status, test);
		destination_ids.add(destination_id_1);
		
		deduplicationInfo = spogcloudRPSServer.composededuplicationInfo(block_size, hash_memory,hash_on_ssd, data_destination_path+1, data_destination_username, data_destination_password, 
				index_destination_path+1, index_destination_username, index_destination_password, hash_destination_path+1, hash_destination_username, hash_destination_password);
		test.log(LogStatus.INFO, "Compose the hashmap for datastoreproperties info "+organizationType);
		datastorepropertiesInfo = spogcloudRPSServer.composedatastorepropertiesInfo(concurrent_active_nodes,capacity, datastore_path+1, datastore_username, datastore_password, compression, encryption_password, deduplicationInfo, Boolean.valueOf(send_email), to_email);
		
		response = spogcloudRPSServer.createCloudRPSDataStore(datastore_name+1, dedupe_enabled, encryption_enabled, compression_enabled, rps_server_id, datastorepropertiesInfo, validToken);
		test.log(LogStatus.INFO, "Perform the response validation");
		String datastore_id_1 = spogcloudRPSServer.validateCloudRPSDataStoreResponse(response, datastore_name+1, dedupe_enabled, encryption_enabled, compression_enabled, rps_server_id,rps_server_name, datastorepropertiesInfo, SpogConstants.SUCCESS_POST, null, "", test);
		
					/************************* Assign datastore and validate ******************/
		
		test.log(LogStatus.INFO, "Assign the rps datastore to the destination");
		response = spogDestinationServer.assigndatastore(destination_id, datastore_id, validToken, test);
		
		//Compose hashmap for cloudhybrid			
		HashMap<String, Object> expectedresponse_cloudhybrid_datastore = composeExpectedResponseCloudHybridDatastore(datastore_id, datastore_name);
		HashMap<String, Object> expectedresponse_cloudhybrid = composeExpectedCH(capacity, dest_concurrent_active_node, expectedresponse_cloudhybrid_datastore, dest_is_deduplicated, dest_block_size, compression, dest_is_compressed);
			
		//compose hasmap for destination
		HashMap<String, Object> expectedresponse = composeExpectedResponse(destination_id, destination_name, destination_type, datastore_id, dest_orgnization_id, dest_site_id, expectedresponse_cloudhybrid);
			
		spogDestinationServer.checkassigneddatastore(response, expectedresponse, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		
				/****************** Un assign and Delete the datstores and destinations *********************/
		
		//Deleting the policy created on assign datastore
		deletePolicyCreatedOnAssignDatastore(destination_id, test);
				
		spogDestinationServer.unassigndatastore(destination_id, validToken, test);
		
		test.log(LogStatus.INFO, "Deleting destinations after un assigning");
		spogDestinationServer.deletedestinationbydestination_Id(destination_id, dest_user_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		spogDestinationServer.deletedestinationbydestination_Id(destination_id_1, dest_user_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		
		test.log(LogStatus.INFO, "Delete the cloud rps datastore");
		spogcloudRPSServer.destroyCloudRPSDataStore(datastore_id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		spogcloudRPSServer.destroyCloudRPSDataStore(datastore_id_1, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
	}
	
	
	@DataProvider(name = "assigncloudRPSDatastoretoDestination_403")
	public final Object[][] assigncloudRPSDatastoretoDestination_403() {
		return new Object[][] {
			{"direct",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id,cloud_rps_name, 4,2048,false,"C:\\"+spogServer.ReturnRandom("dest")+"\\data","administrator",ti.common_password,
				"C:\\"+spogServer.ReturnRandom("dest")+"\\index","administrator",ti.common_password, "C:\\"+spogServer.ReturnRandom("dest")+"\\hash","administrator",ti.common_password,4,1024,
				"C:\\"+spogServer.ReturnRandom("dest"),"administrator",ti.common_password,"standard","abc",true,"abc@arcserve.com",
				ti.direct_org1_id, ti.direct_org1_user1_token, direct_cloud_id,datacenters[0],"cloud_hybrid_store","4", "true", "4", "true" ,spogServer.ReturnRandom("destname"),
				SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			/*{"msp",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id,cloud_rps_name, 4,2048,false,"C:\\"+spogServer.ReturnRandom("dest")+"\\data","administrator",ti.common_password,
				"C:\\"+spogServer.ReturnRandom("dest")+"\\index","administrator",ti.common_password, "C:\\"+spogServer.ReturnRandom("dest")+"\\hash","administrator",ti.common_password,4,1024,
				"C:\\"+spogServer.ReturnRandom("dest"),"administrator",ti.common_password,"standard","abc",true,"abc@arcserve.com",
				msp_organization_id,ti.root_msp_org1_user1_token, msp_site_id,datacenters[0],"cloud_hybrid_store","4", "true", "4", "true" ,spogServer.ReturnRandom("destname"),
				SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			*/{"suborg",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id,cloud_rps_name, 4,2048,false,"C:\\"+spogServer.ReturnRandom("dest")+"\\data","administrator",ti.common_password,
				"C:\\"+spogServer.ReturnRandom("dest")+"\\index","administrator",ti.common_password, "C:\\"+spogServer.ReturnRandom("dest")+"\\hash","administrator",ti.common_password,4,1024,
				"C:\\"+spogServer.ReturnRandom("dest"),"administrator",ti.common_password,"standard","abc",true,"abc@arcserve.com",
				ti.root_msp1_suborg1_id,ti.root_msp_org1_user1_token, rootmsp_cloud_id,datacenters[0],"cloud_hybrid_store","4", "true", "4", "true" ,spogServer.ReturnRandom("destname"),
				SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"mspaccountadmin",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id,cloud_rps_name, 4,2048,false,"C:\\"+spogServer.ReturnRandom("dest")+"\\data","administrator",ti.common_password,
				"C:\\"+spogServer.ReturnRandom("dest")+"\\index","administrator",ti.common_password, "C:\\"+spogServer.ReturnRandom("dest")+"\\hash","administrator",ti.common_password,4,1024,
				"C:\\"+spogServer.ReturnRandom("dest"),"administrator",ti.common_password,"standard","abc",true,"abc@arcserve.com",
				ti.root_msp1_suborg1_id,ti.root_msp_org1_msp_accountadmin1_token, rootmsp_cloud_id,datacenters[0],"cloud_hybrid_store","4", "true", "4", "true" ,spogServer.ReturnRandom("destname"),
				SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			
		};
	}
	
	
	@Test(dataProvider = "assigncloudRPSDatastoretoDestination_403")
	public void assigncloudRPSDatastoretoDestination_403(String organizationType,
													   String organization_id,
													   String validToken,
													   String datastore_name,
													   boolean dedupe_enabled,
													   boolean encryption_enabled,
													   boolean compression_enabled,
													   String rps_server_id,
													   String rps_server_name,
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
													   String dest_orgnization_id,
													   String dest_user_token,
													   String dest_site_id,
													   String dest_datacenter,
													   String destination_type,
													   String dest_concurrent_active_node, 
													   String dest_is_deduplicated, 
													   String dest_block_size, 
													   String dest_is_compressed,
													   String destination_name,
													   int expectedStatusCode,
													   SpogMessageCode expectedErrorMessage
													   ) {
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		
					/*************** Create destinations and datstores *******************/
		
		test.log(LogStatus.INFO, "Compose the hashmap for deduplication info "+organizationType);
		HashMap<String,Object> deduplicationInfo = spogcloudRPSServer.composededuplicationInfo(block_size, hash_memory,hash_on_ssd, data_destination_path, data_destination_username, data_destination_password, 
				index_destination_path, index_destination_username, index_destination_password, hash_destination_path, hash_destination_username, hash_destination_password);
		
		test.log(LogStatus.INFO, "Compose the hashmap for datastoreproperties info "+organizationType);
		HashMap<String,Object> datastorepropertiesInfo = spogcloudRPSServer.composedatastorepropertiesInfo(concurrent_active_nodes,capacity, datastore_path, datastore_username, datastore_password, compression, encryption_password, deduplicationInfo, send_email, to_email);
		
		test.log(LogStatus.INFO, "Create the cloud rps datastore "+organizationType);
		Response response = spogcloudRPSServer.createCloudRPSDataStore(datastore_name, dedupe_enabled, encryption_enabled, compression_enabled, rps_server_id, datastorepropertiesInfo, validToken);
		String datastore_id = spogcloudRPSServer.validateCloudRPSDataStoreResponse(response, datastore_name, dedupe_enabled, encryption_enabled, compression_enabled, rps_server_id,rps_server_name, datastorepropertiesInfo, SpogConstants.SUCCESS_POST, null, "", test);
		
		test.log(LogStatus.INFO,"Create a cloud hybrid datastore for an organization");
		spogDestinationServer.setToken(dest_user_token);
		String destination_id = spogDestinationServer.createDestinationWithCheck("",dest_orgnization_id, dest_site_id, 
				dest_datacenter,destination_type, "running","0",
	    		"", "normal", "iran-win7", "2M", "2M", 
	    		"0","0", "31", "0", "2", "0", dest_concurrent_active_node, dest_is_deduplicated, dest_block_size, dest_is_compressed ,destination_name,test);
		destination_ids.add(destination_id);
		
				/************************* Assign datastore and validate ******************/
		
		test.log(LogStatus.INFO, "Assign the rps datastore to the destination for "+organizationType+" with other org user token");
		response = spogDestinationServer.assigndatastore(destination_id, datastore_id, dest_user_token, test);
		
		spogDestinationServer.checkassigneddatastore(response, null, expectedStatusCode, expectedErrorMessage, test);

		spogDestinationServer.deletedestinationbydestination_Id(destination_id, dest_user_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		
		test.log(LogStatus.INFO, "Delete the cloud rps datastore");
		spogcloudRPSServer.destroyCloudRPSDataStore(datastore_id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
				
	}
	
	@DataProvider(name = "assigncloudRPSDatastoreInvalid")
	public final Object[][] assigncloudRPSDatastoreInvalid() {
		return new Object[][] {
			//400
			{"Assign datastore to invalid destination id","123",UUID.randomUUID().toString(),ti.csr_token,SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_IS_NOT_UUID},
			{"Assign datastore to null destination id",null,UUID.randomUUID().toString(),ti.csr_token,SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_IS_NOT_UUID},
			{"Assign datastore to direct destination id in direct org",direct_direct_dest_id,UUID.randomUUID().toString(),ti.csr_token,SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.DESTINATION_TYPE_MUST_BE_CLOUD_HYBRID},
			{"Assign datastore to direct destination id in sub org",sub_orga_baas_destionation_ID,UUID.randomUUID().toString(),ti.csr_token,SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.DESTINATION_TYPE_MUST_BE_CLOUD_HYBRID},
			
			//401
			{"Assign datastore to destination with invalid token",UUID.randomUUID().toString(),UUID.randomUUID().toString(),ti.csr_token+"J",SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT},
			{"Assign datastore to destination with no token",UUID.randomUUID().toString(),UUID.randomUUID().toString(),"",SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTHENTICATION_FAILED},
			
			//403
			{"Assign datastore to destination with direct token",UUID.randomUUID().toString(),UUID.randomUUID().toString(),ti.direct_org1_user1_token,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Assign datastore to destination with msp token",UUID.randomUUID().toString(),UUID.randomUUID().toString(),ti.root_msp_org1_user1_token,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Assign datastore to destination with suborg token",UUID.randomUUID().toString(),UUID.randomUUID().toString(),ti.root_msp1_suborg1_user1_token,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Assign datastore to destination with mspAccAdmin token",UUID.randomUUID().toString(),UUID.randomUUID().toString(),ti.root_msp_org1_msp_accountadmin1_token,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Assign datastore to destination with sub msp token",UUID.randomUUID().toString(),UUID.randomUUID().toString(),ti.root_msp_org1_user1_token,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Assign datastore to destination with sub msp suborg token",UUID.randomUUID().toString(),UUID.randomUUID().toString(),ti.msp1_submsp1_suborg1_user1_token,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Assign datastore to destination with sub mspAccAdmin token",UUID.randomUUID().toString(),UUID.randomUUID().toString(),ti.root_msp1_submsp1_account_admin_token,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Assign datastore to destination with csr_readonly token",UUID.randomUUID().toString(),UUID.randomUUID().toString(),ti.csr_readonly_token,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
						
			//404
			{"Assign datastore to destination id that does not exist",UUID.randomUUID().toString(),UUID.randomUUID().toString(),ti.csr_token,SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.DESTINATION_NOT_FOUND},
			{"Assign datastore to destination id with datastore id that does not exist in direct org",direct_hybrid_destination_ID,UUID.randomUUID().toString(),ti.csr_token,SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.DATASTORE_WITH_ID_DOESNOT_EXIST},
			{"Assign datastore to destination id with datastore id that does not exist in sub org",sub_orga_hybrid_destination_ID,UUID.randomUUID().toString(),ti.csr_token,SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.DATASTORE_WITH_ID_DOESNOT_EXIST},
		};
	}
	
	@Test(dataProvider = "assigncloudRPSDatastoreInvalid")
	public void assigncloudRPSDatastoreInvalid(String caseType,
										   String destination_id,
										   String datastore_id,
										   String validToken,
										   int expectedStatusCode,
										   SpogMessageCode expectedErrorMessage) {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		
		test.log(LogStatus.INFO, caseType);
		Response response = spogDestinationServer.assigndatastore(destination_id, datastore_id, validToken, test);
		spogDestinationServer.checkassigneddatastore(response, null, expectedStatusCode, expectedErrorMessage, test);
		
	}
	
	
	@Test(dataProvider = "assigncloudRPSDatastoretoDestination")
	public void checkAutoPolicyCreateOnAssign(String organizationType,
													   String organization_id,
													   String validToken,
													   String datastore_name,
													   boolean dedupe_enabled,
													   boolean encryption_enabled,
													   boolean compression_enabled,
													   String rps_server_id,
													   String rps_server_name,
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
													   String dest_orgnization_id,
													   String dest_user_token,
													   String dest_site_id,
													   String dest_datacenter,
													   String destination_type,
													   String dest_concurrent_active_node, 
													   String dest_is_deduplicated, 
													   String dest_block_size, 
													   String dest_is_compressed,
													   String destination_name,
													   String destination_status
													   ) {
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		
					/*************** Create destinations and datstores *******************/
		
		test.log(LogStatus.INFO, "Compose the hashmap for deduplication info "+organizationType);
		HashMap<String,Object> deduplicationInfo = spogcloudRPSServer.composededuplicationInfo(block_size, hash_memory,hash_on_ssd, data_destination_path, data_destination_username, data_destination_password, 
				index_destination_path, index_destination_username, index_destination_password, hash_destination_path, hash_destination_username, hash_destination_password);
	
		test.log(LogStatus.INFO, "Compose the hashmap for datastoreproperties info "+organizationType);
		HashMap<String,Object> datastorepropertiesInfo = spogcloudRPSServer.composedatastorepropertiesInfo(concurrent_active_nodes,capacity, datastore_path, datastore_username, datastore_password, compression, encryption_password, deduplicationInfo, Boolean.valueOf(send_email), to_email);
				
		test.log(LogStatus.INFO, "Create the cloud rps datastore "+organizationType);
		Response response = spogcloudRPSServer.createCloudRPSDataStore(datastore_name, dedupe_enabled, encryption_enabled, compression_enabled, rps_server_id, datastorepropertiesInfo, validToken);
		String datastore_id = response.then().extract().path("data.datastore_id");
		
		test.log(LogStatus.INFO,"Create a cloud hybrid datastore for direct organization");
		spogDestinationServer.setToken(dest_user_token);
		String destination_id = spogDestinationServer.createHybridDestination(dest_user_token, dest_orgnization_id, dest_datacenter, destination_type, destination_name, destination_status, test);
		destination_ids.add(destination_id);
		
				/************************* Assign datastore and validate ******************/
		
		test.log(LogStatus.INFO, "Assign the rps datastore to the destination");
		response = spogDestinationServer.assigndatastore(destination_id, datastore_id, validToken, test);
		
		//Compose hashmap for cloudhybrid			
		HashMap<String, Object> expectedresponse_cloudhybrid_datastore = composeExpectedResponseCloudHybridDatastore(datastore_id, datastore_name);
		HashMap<String, Object> expectedresponse_cloudhybrid = composeExpectedCH(capacity, dest_concurrent_active_node, expectedresponse_cloudhybrid_datastore, dest_is_deduplicated, dest_block_size, compression, dest_is_compressed);
			
		//compose hasmap for destination
		HashMap<String, Object> expectedresponse = composeExpectedResponse(destination_id, destination_name, destination_type, datastore_id, dest_orgnization_id, dest_site_id, expectedresponse_cloudhybrid);
		spogDestinationServer.checkassigneddatastore(response, expectedresponse, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		
		/******************* Check auto policy creation on assign datastore code ************************/
		
		checkAutoPolicyCreationForCHDatastore(validToken, destination_id, destination_name, test);;
		
		
		//Deleting the policy created on assign datastore
		deletePolicyCreatedOnAssignDatastore(destination_id, test);
						
		spogDestinationServer.unassigndatastore(destination_id, validToken, test);
		
		test.log(LogStatus.INFO, "Deleting destinations after un assigning");
		spogDestinationServer.deletedestinationbydestination_Id(destination_id, dest_user_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		
		test.log(LogStatus.INFO, "Delete the cloud rps datastore");
		spogcloudRPSServer.destroyCloudRPSDataStore(datastore_id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);		
	}
	
	public void checkAutoPolicyCreationForCHDatastore(String validToken, String destination_id, String destination_name, ExtentTest test) {
		
		Response response = spogDestinationServer.getDestinationById(validToken, destination_id, test);
		ArrayList<HashMap<String, Object>> policies = response.then().extract().path("data.policies");
		
		/***************** Validate policy information in Get destination api response ********************/
		
		assertEquals(policies.size(), 1);
		assertNotNull(policies.get(0).get("policy_id"));
		assertEquals(policies.get(0).get("policy_name").toString(), destination_name+" policy");
		assertEquals(policies.get(0).get("policy_type").toString(), "cloud_hybrid_replication");
			
		/********************* Validate complete policy information ************************/
		
		response = policy4SPOGServer.getPolicyById(validToken, policies.get(0).get("policy_id").toString(), test);
		response.then()
		.body("data.policy_id", equalTo(policies.get(0).get("policy_id")))
		.body("data.policy_description", equalTo(null))
		.body("data.policy_type", equalTo(policies.get(0).get("policy_type")))
		.body("data.sla", equalTo(null))
		.body("data.policy_name", equalTo(policies.get(0).get("policy_name").toString()))
		.body("data.throttles", equalTo(new ArrayList<String>()))
		.body("data.policy_status", equalTo("success"))
		.body("data.sources", equalTo(new ArrayList<>()))
		.body("data.destinations[0].task_type", equalTo("udp_replication_from_remote"))
		.body("data.destinations[0].destination_id", equalTo(destination_id))
		.body("data.destinations[0].cloud_direct_image_backup", equalTo(null))
		.body("data.destinations[0].cloud_direct_file_backup", equalTo(null))
		.body("data.destinations[0].cloud_direct_sql_server_backup", equalTo(null))
		.body("data.destinations[0].udp_replication_to_remote", equalTo(null))
		.body("data.destinations[0].destination_name", equalTo(destination_name))
		.body("data.destinations[0].destination_type", equalTo("cloud_hybrid_store"))
		.body("data.schedules", equalTo(new ArrayList<>()))
		;
		
		test.log(LogStatus.PASS, "Cloud Hybrid Auto policy creation and policy details verified");
	}
	
	public HashMap<String, Object> composeExpectedResponseCloudHybridDatastore(String datastore_id, String datastore_name) {
		HashMap<String, Object> expectedresponse_cloudhybrid_datastore = new HashMap<>();
		
		expectedresponse_cloudhybrid_datastore.put("datastore_id", datastore_id);
		expectedresponse_cloudhybrid_datastore.put("datastore_name", datastore_name);
		
		return expectedresponse_cloudhybrid_datastore;
	}
	
	public HashMap<String, Object> composeExpectedCH(long capacity, String dest_concurrent_active_node,
														HashMap<String, Object> expectedresponse_cloudhybrid_datastore,
														String dest_is_deduplicated, String dest_block_size, String compression,
														String dest_is_compressed ) {
		HashMap<String, Object> expectedresponse_cloudhybrid = new HashMap<>();
		
		expectedresponse_cloudhybrid.put("storage_capacity", (double) capacity);
		expectedresponse_cloudhybrid.put("concurrent_active_node",Integer.parseInt(dest_concurrent_active_node));
		expectedresponse_cloudhybrid.put("datastore", expectedresponse_cloudhybrid_datastore);
		expectedresponse_cloudhybrid.put("is_deduplicated",Boolean.valueOf(dest_is_deduplicated));
		expectedresponse_cloudhybrid.put("block_size",Integer.parseInt(dest_block_size));
		expectedresponse_cloudhybrid.put("compression", compression);
		expectedresponse_cloudhybrid.put("is_compressed",Boolean.valueOf(dest_is_compressed));
		
		return expectedresponse_cloudhybrid;
	}
	
	public HashMap<String, Object> composeExpectedResponse(String destination_id, String destination_name,
										String destination_type, String datastore_id,
										String dest_orgnization_id, String dest_site_id,
										HashMap<String, Object> expectedresponse_cloudhybrid
										) {
		
		HashMap<String, Object> expectedresponse = new HashMap<>();
		expectedresponse.put("destination_id", destination_id);
		expectedresponse.put("destination_name",destination_name);
		expectedresponse.put("destination_type",destination_type);
		expectedresponse.put("datastore_id",datastore_id);
		expectedresponse.put("organization_id", dest_orgnization_id);
		expectedresponse.put("site_id",dest_site_id);
		expectedresponse.put("cloud_hybrid_store",expectedresponse_cloudhybrid);
		
		return expectedresponse;
	}
	
	/** Used to delete policies created on assign datastore
	 * 
	 * @param destination_id
	 * @param test
	 */
	
	public void deletePolicyCreatedOnAssignDatastore(String destination_id, ExtentTest test) {
		
		Response response = spogDestinationServer.getDestinationById(ti.csr_token, destination_id, test);
		ArrayList<HashMap<String, Object>> policies = response.then().extract().path("data.policies");
				
		for (int i = 0; i < policies.size(); i++) {
			response = policy4SPOGServer.deletePolicybyPolicyId(ti.csr_token, policies.get(i).get("policy_id").toString());
			
		/*	if (Integer.parseInt(response.then().extract().path("status").toString()) == (SpogConstants.SUCCESS_GET_PUT_DELETE)) {
				return true;
			} else {
				return false;
			}*/
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
		
		for (int i = 0; i < destination_ids.size(); i++) {
			spogDestinationServer.unassigndatastore(destination_ids.get(i), ti.csr_token, test);
		}
		
		test.log(LogStatus.INFO, "Get the cloud RPS datastores and delete");
		Response response = spogcloudRPSServer.getCloudRPSDataStoresForSpecifiedRPSWithCheck(ti.csr_token, cloudRPS_id, null, null, null, 1, 100, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		ArrayList<HashMap<String, Object>> datastores= response.then().extract().path("data");
		if (!datastores.isEmpty()) {
			for (int i = 0; i < datastores.size(); i++) {
				spogcloudRPSServer.destroyCloudRPSDataStore(datastores.get(i).get("datastore_id").toString(), ti.csr_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			}
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
