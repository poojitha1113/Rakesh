package api.destination;

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

import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
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

/** This is to test whether the csr user can unassign the datastore tfrom CH destination successfully or not
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
 * 6. Datastore should be assinged to the destination
 * 
 * For more details on how to register RPS visit CreateCloudRPSTest
 * 
 * @author Rakesh.Chalamala
 * @sprint 14
 */

public class UnassignDatastoretoDestinationTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private SPOGDestinationServer spogDestinationServer;
	private UserSpogServer userSpogServer;
	private SPOGCloudRPSServer spogcloudRPSServer;
	private Policy4SPOGServer policy4SPOGServer;
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
	String cloud_rps_name = null;
	String cloud_rps_port = null;
	String cloud_rps_protocol = null;
	String cloud_rps_username = null;
	String cloud_rps_password = null;
	String csr_site_id = null;
	String csr_site_name = null;
	String cloudRPS_id = null;
	ArrayList<String> destination_ids = new ArrayList<>();
	
	private String  org_model_prefix=this.getClass().getSimpleName();
	private TestOrgInfo ti;
	private Object direct_cloud_id;
	private Object rootmsp_cloud_id;
	
	@BeforeClass
	@Parameters({ "baseURI", "port","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port,String logFolder,String runningMachine,String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		spogcloudRPSServer = new SPOGCloudRPSServer(baseURI, port);
		policy4SPOGServer = new Policy4SPOGServer(baseURI, port);
		rep = ExtentManager.getInstance("UnassignDatastoretoDestinationTest", logFolder);
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
		spogDestinationServer.setToken(ti.csr_token);
		datacenters = spogDestinationServer.getDestionationDatacenterID();
		
		test.log(LogStatus.INFO, "Create cloud RPS");
		Response response = spogcloudRPSServer.createCloudRPS(cloud_rps_name, cloud_rps_port, cloud_rps_protocol, cloud_rps_username, cloud_rps_password, ti.csr_org_id, csr_site_id, datacenters[0], ti.csr_token);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		cloudRPS_id = response.then().extract().path("data.server_id");
		cloud_rps_name = response.then().extract().path("data.server_name");
		
		response = spogServer.getCloudAccounts(ti.direct_org1_user1_token, "", test);
        direct_cloud_id = response.then().extract().path("data[0].cloud_account_id");
        
        response = spogServer.getCloudAccounts(ti.root_msp_org1_user1_token, "", test);
        rootmsp_cloud_id = response.then().extract().path("data[0].cloud_account_id");

	}
	
	@DataProvider(name = "unassigncloudRPSDatastoretoDestination")
	public final Object[][] unassigncloudRPSDatastoretoDestination() {
		return new Object[][] {
			
			{"unssign datastore to direct org",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id,cloud_rps_name, 4,2048,false,"C:\\"+spogServer.ReturnRandom("dest")+"\\data","administrator",ti.common_password,
				"C:\\"+spogServer.ReturnRandom("dest")+"\\index","administrator",ti.common_password, "C:\\"+spogServer.ReturnRandom("dest")+"\\hash","administrator",ti.common_password,4,102400,
				"C:\\"+spogServer.ReturnRandom("dest"),"administrator",ti.common_password,"standard","abc",true,"abc@arcserve.com",
				ti.direct_org1_id, ti.direct_org1_user1_token, direct_cloud_id,datacenters[0],"cloud_hybrid_store","4", "true", "4", "true" ,spogServer.ReturnRandom("destname")},
			
			/*{"unassign datastore to msp org",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id,cloud_rps_name, 16,2048,false,"C:\\"+spogServer.ReturnRandom("dest")+"\\data","administrator",ti.common_password,
				"C:\\"+spogServer.ReturnRandom("dest")+"\\index","administrator",ti.common_password, "C:\\"+spogServer.ReturnRandom("dest")+"\\hash","administrator",ti.common_password,4,102400,
				"C:\\"+spogServer.ReturnRandom("dest"),"administrator",ti.common_password,"maximum","abc",true,"abc@arcserve.com",
				msp_organization_id, ti.root_msp_org1_user1_token, msp_site_id,datacenters[0],"cloud_hybrid_store","4", "true", "16", "true" ,spogServer.ReturnRandom("destname")},
			*/
			{"unassign datastore to sub org",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id,cloud_rps_name, 32,2048,false,"C:\\"+spogServer.ReturnRandom("dest")+"\\data","administrator",ti.common_password,
				"C:\\"+spogServer.ReturnRandom("dest")+"\\index","administrator",ti.common_password, "C:\\"+spogServer.ReturnRandom("dest")+"\\hash","administrator",ti.common_password,4,102400,
				"C:\\"+spogServer.ReturnRandom("dest"),"administrator",ti.common_password,"standard","abc",true,"abc@arcserve.com",
				ti.root_msp1_suborg1_id, ti.root_msp_org1_user1_token, rootmsp_cloud_id,datacenters[0],"cloud_hybrid_store","4", "true", "32", "true" ,spogServer.ReturnRandom("destname")},
			
			{"unassign datastore to sub org using msp account admin token",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id,cloud_rps_name, 4,2048,false,"C:\\"+spogServer.ReturnRandom("dest")+"\\data","administrator",ti.common_password,
				"C:\\"+spogServer.ReturnRandom("dest")+"\\index","administrator",ti.common_password, "C:\\"+spogServer.ReturnRandom("dest")+"\\hash","administrator",ti.common_password,4,102400,
				"C:\\"+spogServer.ReturnRandom("dest"),"administrator",ti.common_password,"standard","abc",true,"abc@arcserve.com",
				ti.root_msp1_suborg1_id, ti.root_msp_org1_msp_accountadmin1_token, rootmsp_cloud_id,datacenters[0],"cloud_hybrid_store","4", "true", "4", "true" ,spogServer.ReturnRandom("destname")},
			
		};
	}
	
	
	@Test(dataProvider = "unassigncloudRPSDatastoretoDestination")
	public void unassigncloudRPSDatastoretoDestination(String organizationType,
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
													   String destination_name
													   ) {
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		
		test.log(LogStatus.INFO, "Compose the hashmap for deduplication info "+organizationType);
		HashMap<String,Object> deduplicationInfo = spogcloudRPSServer.composededuplicationInfo(block_size, hash_memory,hash_on_ssd, data_destination_path, data_destination_username, data_destination_password, 
				index_destination_path, index_destination_username, index_destination_password, hash_destination_path, hash_destination_username, hash_destination_password);
		
		
		test.log(LogStatus.INFO, "Compose the hashmap for datastoreproperties info "+organizationType);
		HashMap<String,Object> datastorepropertiesInfo = spogcloudRPSServer.composedatastorepropertiesInfo(concurrent_active_nodes,capacity, datastore_path, datastore_username, datastore_password, compression, encryption_password, deduplicationInfo, Boolean.valueOf(send_email), to_email);
		
		
		test.log(LogStatus.INFO, "Create the cloud rps datastore "+organizationType);
		Response response = spogcloudRPSServer.createCloudRPSDataStore(datastore_name, dedupe_enabled, encryption_enabled, compression_enabled, rps_server_id, datastorepropertiesInfo, validToken);
		
		test.log(LogStatus.INFO, "Perform the response validation");
		String datastore_id = spogcloudRPSServer.validateCloudRPSDataStoreResponse(response, datastore_name, dedupe_enabled, encryption_enabled, compression_enabled, rps_server_id,rps_server_name, datastorepropertiesInfo, SpogConstants.SUCCESS_POST, null, "", test);
		
		test.log(LogStatus.INFO,"Create a cloud hybrid datastore for direct organization");
		spogDestinationServer.setToken(dest_user_token);
		String destination_id = spogDestinationServer.createDestinationWithCheck("",dest_orgnization_id, dest_site_id, 
				dest_datacenter,destination_type, "running","0",
	    		"", "normal", "iran-win7", "2M", "2M", 
	    		"0","0", "31", "0", "2", "0", dest_concurrent_active_node, dest_is_deduplicated, dest_block_size, dest_is_compressed ,destination_name,test);
		destination_ids.add(destination_id);
		
		test.log(LogStatus.INFO, "Assign the rps datastore to the destination");
		for(int i=0; i<2;i++) {
			response = spogDestinationServer.assigndatastore(destination_id, datastore_id, validToken, test);
			/*//Compose hashmap for cloudhybrid
			HashMap<String, Object> expectedresponse_cloudhybrid = new HashMap<>();
			HashMap<String, Object> expectedresponse_cloudhybrid_datastore = new HashMap<>();
			
			expectedresponse_cloudhybrid_datastore.put("datastore_id", datastore_id);
			expectedresponse_cloudhybrid_datastore.put("datastore_name", datastore_name);
			
			expectedresponse_cloudhybrid.put("concurrent_active_node",Integer.parseInt(dest_concurrent_active_node));
			expectedresponse_cloudhybrid.put("datastore", expectedresponse_cloudhybrid_datastore);
			expectedresponse_cloudhybrid.put("is_deduplicated",Boolean.valueOf(dest_is_deduplicated));
			expectedresponse_cloudhybrid.put("block_size",Integer.parseInt(dest_block_size));
			expectedresponse_cloudhybrid.put("compression", compression);
			expectedresponse_cloudhybrid.put("is_compressed",Boolean.valueOf(dest_is_compressed));
			//compose hasmap for destination
			HashMap<String, Object> expectedresponse = new HashMap<>();
			expectedresponse.put("destination_id", destination_id);
			expectedresponse.put("destination_name",destination_name);
			expectedresponse.put("destination_type",destination_type);
			expectedresponse.put("datastore_id",datastore_id);
			expectedresponse.put("organization_id", dest_orgnization_id);
			expectedresponse.put("site_id",dest_site_id);
			expectedresponse.put("cloud_hybrid_store",expectedresponse_cloudhybrid);
			
			spogDestinationServer.checkassigneddatastore(response, expectedresponse, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);*/
			spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE,test);
			
			response = spogDestinationServer.unassigndatastore(destination_id, validToken, test);
			spogDestinationServer.checkunassigneddatastore(response, destination_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);			
		}
		
		response= spogDestinationServer.getDestinationById(validToken, destination_id, test);
		String policy_id = response.then().extract().path("data.policies[0].policy_id");
		response = policy4SPOGServer.deletePolicybyPolicyId(validToken, policy_id);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		
		spogDestinationServer.deletedestinationbydestination_Id(destination_id, dest_user_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		
		test.log(LogStatus.INFO, "Delete the cloud rps datastore");
		spogcloudRPSServer.destroyCloudRPSDataStore(datastore_id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
				
	}
	
	
	@DataProvider(name = "unassigncloudRPSDatastoretoDestination_403")
	public final Object[][] unassigncloudRPSDatastoretoDestination_403() {
		return new Object[][] {
			{"direct",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id,cloud_rps_name, 4,2048,false,"C:\\"+spogServer.ReturnRandom("dest")+"\\data","administrator",ti.common_password,
				"C:\\"+spogServer.ReturnRandom("dest")+"\\index","administrator",ti.common_password, "C:\\"+spogServer.ReturnRandom("dest")+"\\hash","administrator",ti.common_password,4,102400,
				"C:\\"+spogServer.ReturnRandom("dest"),"administrator",ti.common_password,"standard","abc",true,"abc@arcserve.com",
				ti.direct_org1_id, ti.direct_org1_user1_token, direct_cloud_id,datacenters[0],"cloud_hybrid_store","4", "true", "4", "true" ,spogServer.ReturnRandom("destname"),
				SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			/*{"msp",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id,cloud_rps_name, 4,2048,false,"C:\\"+spogServer.ReturnRandom("dest")+"\\data","administrator",ti.common_password,
				"C:\\"+spogServer.ReturnRandom("dest")+"\\index","administrator",ti.common_password, "C:\\"+spogServer.ReturnRandom("dest")+"\\hash","administrator",ti.common_password,4,102400,
				"C:\\"+spogServer.ReturnRandom("dest"),"administrator",ti.common_password,"standard","abc",true,"abc@arcserve.com",
				msp_organization_id,ti.root_msp_org1_user1_token, msp_site_id,datacenters[0],"cloud_hybrid_store","4", "true", "4", "true" ,spogServer.ReturnRandom("destname"),
				SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			*/{"suborg",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id,cloud_rps_name, 4,2048,false,"C:\\"+spogServer.ReturnRandom("dest")+"\\data","administrator",ti.common_password,
				"C:\\"+spogServer.ReturnRandom("dest")+"\\index","administrator",ti.common_password, "C:\\"+spogServer.ReturnRandom("dest")+"\\hash","administrator",ti.common_password,4,102400,
				"C:\\"+spogServer.ReturnRandom("dest"),"administrator",ti.common_password,"standard","abc",true,"abc@arcserve.com",
				ti.root_msp1_suborg1_id,ti.root_msp_org1_user1_token, rootmsp_cloud_id,datacenters[0],"cloud_hybrid_store","4", "true", "4", "true" ,spogServer.ReturnRandom("destname"),
				SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"mspaccountadmin",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, cloud_rps_name,4,2048,false,"C:\\"+spogServer.ReturnRandom("dest")+"\\data","administrator",ti.common_password,
				"C:\\"+spogServer.ReturnRandom("dest")+"\\index","administrator",ti.common_password, "C:\\"+spogServer.ReturnRandom("dest")+"\\hash","administrator",ti.common_password,4,102400,
				"C:\\"+spogServer.ReturnRandom("dest"),"administrator",ti.common_password,"standard","abc",true,"abc@arcserve.com",
				ti.root_msp1_suborg1_id,ti.root_msp_org1_msp_accountadmin1_token, rootmsp_cloud_id,datacenters[0],"cloud_hybrid_store","4", "true", "4", "true" ,spogServer.ReturnRandom("destname"),
				SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			
		};
	}
	
	
	@Test(dataProvider = "unassigncloudRPSDatastoretoDestination_403")
	public void unassigncloudRPSDatastoretoDestination_403(String organizationType,
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
		
		test.log(LogStatus.INFO, "Compose the hashmap for deduplication info "+organizationType);
		HashMap<String,Object> deduplicationInfo = spogcloudRPSServer.composededuplicationInfo(block_size, hash_memory,hash_on_ssd, data_destination_path, data_destination_username, data_destination_password, 
				index_destination_path, index_destination_username, index_destination_password, hash_destination_path, hash_destination_username, hash_destination_password);
		
		test.log(LogStatus.INFO, "Compose the hashmap for datastoreproperties info "+organizationType);
		HashMap<String,Object> datastorepropertiesInfo = spogcloudRPSServer.composedatastorepropertiesInfo(concurrent_active_nodes,capacity, datastore_path, datastore_username, datastore_password, compression, encryption_password, deduplicationInfo, send_email, to_email);
		
		test.log(LogStatus.INFO, "Create the cloud rps datastore "+organizationType);
		Response response = spogcloudRPSServer.createCloudRPSDataStore(datastore_name, dedupe_enabled, encryption_enabled, compression_enabled, rps_server_id, datastorepropertiesInfo, validToken);
		
		test.log(LogStatus.INFO, "Perform the response validation");
		
		String datastore_id = spogcloudRPSServer.validateCloudRPSDataStoreResponse(response, datastore_name, dedupe_enabled, encryption_enabled, compression_enabled, rps_server_id,rps_server_name, datastorepropertiesInfo, SpogConstants.SUCCESS_POST, null, "", test);
		
		test.log(LogStatus.INFO,"Create a cloud hybrid datastore for direct organization");
		spogDestinationServer.setToken(dest_user_token);
		String destination_id = spogDestinationServer.createDestinationWithCheck("",dest_orgnization_id, dest_site_id, 
				dest_datacenter,destination_type, "running","0",
	    		"", "normal", "iran-win7", "2M", "2M", 
	    		"0","0", "31", "0", "2", "0", dest_concurrent_active_node, dest_is_deduplicated, dest_block_size, dest_is_compressed ,destination_name,test);
		destination_ids.add(destination_id);
		
		test.log(LogStatus.INFO, "Assign the rps datastore to the destination");
		
		response = spogDestinationServer.assigndatastore(destination_id, datastore_id, validToken, test);
		/*HashMap<String, Object> expectedresponse_cloudhybrid = new HashMap<>();
		HashMap<String, Object> expectedresponse_cloudhybrid_datastore = new HashMap<>();
		
		expectedresponse_cloudhybrid_datastore.put("datastore_id", datastore_id);
		expectedresponse_cloudhybrid_datastore.put("datastore_name", datastore_name);
		
		expectedresponse_cloudhybrid.put("concurrent_active_node",Integer.parseInt(dest_concurrent_active_node));
		expectedresponse_cloudhybrid.put("datastore", expectedresponse_cloudhybrid_datastore);
		expectedresponse_cloudhybrid.put("is_deduplicated",Boolean.valueOf(dest_is_deduplicated));
		expectedresponse_cloudhybrid.put("block_size",Integer.parseInt(dest_block_size));
		expectedresponse_cloudhybrid.put("compression", compression);
		expectedresponse_cloudhybrid.put("is_compressed",Boolean.valueOf(dest_is_compressed));
		//compose hasmap for destination
		HashMap<String, Object> expectedresponse = new HashMap<>();
		expectedresponse.put("destination_id", destination_id);
		expectedresponse.put("destination_name",destination_name);
		expectedresponse.put("destination_type",destination_type);
		expectedresponse.put("datastore_id",datastore_id);
		expectedresponse.put("organization_id", dest_orgnization_id);
		expectedresponse.put("site_id",dest_site_id);
		expectedresponse.put("cloud_hybrid_store",expectedresponse_cloudhybrid);
		
		spogDestinationServer.checkassigneddatastore(response, expectedresponse, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		*/
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		
		test.log(LogStatus.INFO, "Unassign the datastore to destination for "+organizationType+" org with other org user token");
		response = spogDestinationServer.unassigndatastore(destination_id, dest_user_token, test);
		spogDestinationServer.checkunassigneddatastore(response, destination_id, expectedStatusCode, expectedErrorMessage, test);
		
		spogDestinationServer.unassigndatastore(destination_id, validToken, test);

		response= spogDestinationServer.getDestinationById(validToken, destination_id, test);
		String policy_id = response.then().extract().path("data.policies[0].policy_id");
		response = policy4SPOGServer.deletePolicybyPolicyId(validToken, policy_id);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		
		spogDestinationServer.deletedestinationbydestination_Id(destination_id, dest_user_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		
		test.log(LogStatus.INFO, "Delete the cloud rps datastore");
		spogcloudRPSServer.destroyCloudRPSDataStore(datastore_id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
				
	}
	
	@DataProvider(name = "unassigncloudRPSDatastoreInvalid")
	public final Object[][] unAasigncloudRPSDatastoreInvalid() {
		return new Object[][] {
			//400
			{"Unassign datastore to invalid destination id","123",UUID.randomUUID().toString(),ti.csr_token,SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_IS_NOT_UUID},
			{"Unassign datastore to null destination id",null,UUID.randomUUID().toString(),ti.csr_token,SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_IS_NOT_UUID},
			
			//401
			{"Unassign datastore to destination with invalid token",UUID.randomUUID().toString(),UUID.randomUUID().toString(),ti.csr_token+"J",SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT},
			{"Unassign datastore to destination with no token",UUID.randomUUID().toString(),UUID.randomUUID().toString(),"",SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTHENTICATION_FAILED},
			
			//403
			{"Unassign datastore to destination with direct token",UUID.randomUUID().toString(),UUID.randomUUID().toString(),ti.direct_org1_user1_token,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Unassign datastore to destination with msp token",UUID.randomUUID().toString(),UUID.randomUUID().toString(),ti.root_msp_org1_user1_token,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Unassign datastore to destination with suborg token",UUID.randomUUID().toString(),UUID.randomUUID().toString(),ti.root_msp1_suborg1_user1_token,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Unassign datastore to destination with msp Account Admin token",UUID.randomUUID().toString(),UUID.randomUUID().toString(),ti.root_msp_org1_msp_accountadmin1_token,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Unassign datastore to destination with sub msp token",UUID.randomUUID().toString(),UUID.randomUUID().toString(),ti.root_msp1_submsp1_user1_token,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Unassign datastore to destination with sub msp suborg token",UUID.randomUUID().toString(),UUID.randomUUID().toString(),ti.msp1_submsp1_suborg1_user1_token,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Unassign datastore to destination with sub msp Account Admin token",UUID.randomUUID().toString(),UUID.randomUUID().toString(),ti.root_msp1_submsp1_account_admin_token,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Unassign datastore to destination with csr_readonly token",UUID.randomUUID().toString(),UUID.randomUUID().toString(),ti.csr_readonly_token,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
						
			//404
			{"Unassign datastore to destination id that does not exist",UUID.randomUUID().toString(),UUID.randomUUID().toString(),ti.csr_token,SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.DESTINATION_NOT_FOUND},
		};
	}
	
	@Test(dataProvider = "unassigncloudRPSDatastoreInvalid")
	public void unassigncloudRPSDatastoreInvalid(String caseType,
										   String destination_id,
										   String datastore_id,
										   String validToken,
										   int expectedStatusCode,
										   SpogMessageCode expectedErrorMessage) {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		
		test.log(LogStatus.INFO, caseType);
		Response response = spogDestinationServer.unassigndatastore(destination_id, validToken, test);
		spogDestinationServer.checkunassigneddatastore(response, destination_id, expectedStatusCode, expectedErrorMessage, test);
		
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
