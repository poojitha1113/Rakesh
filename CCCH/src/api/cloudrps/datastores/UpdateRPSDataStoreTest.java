package api.cloudrps.datastores;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

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
import InvokerServer.SPOGCloudRPSServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.Is4Org;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

/** This is to test whether the user can update the datastore successfully or not
 *  Each datastore will be assigned to a customer
 *  On creation of datastore the hash,data,index directories will be created on datastore 
 *  Directories information will be part of response
 *  We call the api using datastore id which we get on creation of datstore
 *  
 * Prerequisites:
 * 1. Csr token which is used to create all organizations, since he is the super user
 * 2. Direct, msp and sub organizations and users under it
 * 3. RPS registered with CC - to create datastore we need an RPS registered
 * 4. Datastores created on the RPS
 * 
 * For more details on how to register RPS visit CreateCloudRPSTest
 * 
 * @author Rakesh.Chalamala
 * @sprint 14
 */

public class UpdateRPSDataStoreTest extends base.prepare.Is4Org {
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
	
	String rps_server_name = null;
	String cloudRPS_id = null;
	String datastore_id = null;
	String data_destination_path = null;
	String index_destination_path = null;
	String hash_destination_path = null;
	String datastore_path = null;
	int block_size = 4;
	int hash_memory = 2048;
	String compression = "standard";
	String encryption_password= "abc";
	String to_email ="rakesh@arcserve.com";
    int	concurrent_active_nodes = 0;
    String datastore_name = null;
    String modify_datastore_name = null;
    String modify_hash_destination_path = null;
    String modify_index_destination_path = null;
    String modify_data_destination_path = null;
    String modify_datastore_path = null;
	String cloudRPS_name = null;
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
		}
		ti = new TestOrgInfo(spogServer, test);
		test.log(LogStatus.INFO, "Get the datacenter id");
		spogDestinationServer.setToken(ti.csr_token);
		datacenters = spogDestinationServer.getDestionationDatacenterID();
		
		data_destination_path = spogServer.ReturnRandom("C:\\destination")+"\\data";
		index_destination_path = spogServer.ReturnRandom("C:\\destination")+"\\index";
		hash_destination_path = spogServer.ReturnRandom("C:\\destination")+"\\hash";
		datastore_path = spogServer.ReturnRandom("C:\\destination2");
		datastore_name = spogServer.ReturnRandom("datastore");
	    modify_datastore_name = spogServer.ReturnRandom("datastore1");
	    modify_hash_destination_path = spogServer.ReturnRandom("C:\\destination1")+"\\hash";
	    modify_index_destination_path = spogServer.ReturnRandom("C:\\destination1")+"\\index";
	    modify_data_destination_path = spogServer.ReturnRandom("C:\\destination1")+"\\data";
	    modify_datastore_path = spogServer.ReturnRandom("C:\\datastore1");
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
		rps_server_name = response.then().extract().path("data.server_name");
	}
	
	@DataProvider(name = "postcloudRPSDatastore")
	public final Object[][] postcloudRPSDatastore() {
		return new Object[][] {
			{"csr",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id,rps_server_name, 4,1024,false,spogServer.ReturnRandom("C:\\destination")+"\\data","administrator",ti.common_password,
				spogServer.ReturnRandom("C:\\destination")+"\\index","administrator",ti.common_password, spogServer.ReturnRandom("C:\\destination")+"\\hash","administrator",ti.common_password,4,10200,spogServer.ReturnRandom("C:\\destination2"),"administrator",ti.common_password,
				"standard","abc",true,"abc@arcserve.com","encryption_password"}
		};
	}
	
	@Test(dataProvider = "postcloudRPSDatastore")
	public void postcloudRPSDatastore(String organizationType,
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
									   String to_replace) {
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		
		test.log(LogStatus.INFO, "Compose the hashmap for deduplication info "+organizationType);
		HashMap<String,Object> deduplicationInfo = spogcloudRPSServer.composededuplicationInfo(block_size, hash_memory,hash_on_ssd, data_destination_path+2, data_destination_username, data_destination_password, 
				index_destination_path+2, index_destination_username, index_destination_password, hash_destination_path+2, hash_destination_username, hash_destination_password);
		
		test.log(LogStatus.INFO, "Compose the hashmap for datastoreproperties info "+organizationType);
		HashMap<String,Object> datastorepropertiesInfo = spogcloudRPSServer.composedatastorepropertiesInfo(concurrent_active_nodes,capacity, datastore_path+2, datastore_username, datastore_password, compression, encryption_password, deduplicationInfo, send_email, to_email);
		
		test.log(LogStatus.INFO, "Create the cloud rps datastore "+organizationType);
		Response response = spogcloudRPSServer.createCloudRPSDataStore(datastore_name+2, dedupe_enabled, encryption_enabled, compression_enabled, rps_server_id, datastorepropertiesInfo, validToken);
		
		test.log(LogStatus.INFO, "Perform the response validation");
		
		String datastore_id = spogcloudRPSServer.validateCloudRPSDataStoreResponse(response, datastore_name+2, dedupe_enabled, encryption_enabled, compression_enabled, rps_server_id,rps_server_name, datastorepropertiesInfo, SpogConstants.SUCCESS_POST, null, "", test);
		this.datastore_id = datastore_id;
		
	}
		
	
	@DataProvider(name = "updatecloudRPSDatastore")
	public final Object[][] updatecloudRPSDatastore() {
		return new Object[][] {
			
			//Change the concurrent active nodes - Done
			//Increase hash memory - Done
			//modify the encryption password - Done
			//Change the hash destination path - Done
			//Enable or disable hash_on_ssd - Done
			//Enable or disable email - Done
			//Change the datastore name - Done
			
			{"update concurrent active nodes",ti.csr_org_id,ti.csr_token,this.datastore_name,true,true,true,this.datastore_id, 4,this.hash_memory,false,this.data_destination_path,"administrator",ti.common_password,
				this.index_destination_path,"administrator",ti.common_password, this.hash_destination_path,"administrator",ti.common_password,10,10240,this.datastore_path,"administrator",ti.common_password,
				this.compression,this.encryption_password,true,to_email,"encryption_password"},
			{"update hash memory",ti.csr_org_id,ti.csr_token,this.datastore_name,true,true,true, this.datastore_id, 4,2048,false,this.data_destination_path,"administrator",ti.common_password,
				this.index_destination_path,"administrator",ti.common_password, this.hash_destination_path,"administrator",ti.common_password,10,10240,this.datastore_path,"administrator",ti.common_password,
				this.compression,this.encryption_password,true,to_email,"encryption_password"},
			{"update encryption password",ti.csr_org_id,ti.csr_token,this.datastore_name,true,true,true, this.datastore_id, 4,1024,false,this.data_destination_path,"administrator",ti.common_password,
				this.index_destination_path,"administrator",ti.common_password, this.hash_destination_path,"administrator",ti.common_password,10,10240,this.datastore_path,"administrator",ti.common_password,
				this.compression,"Mclaren@2010",true,to_email,"encryption_password"},
			{"update is hash on ssd",ti.csr_org_id,ti.csr_token,this.datastore_name,true,true,true, this.datastore_id, 4,1024,true,this.data_destination_path,"administrator",ti.common_password,
				this.index_destination_path,"administrator",ti.common_password, this.hash_destination_path,"administrator",ti.common_password,10,10240,this.datastore_path,"administrator",ti.common_password,
				this.compression,"Mclaren@2010",true,to_email,"encryption_password"},
			{"update email alert to false",ti.csr_org_id,ti.csr_token,this.datastore_name,true,true,true, this.datastore_id, 4,1024,true,this.data_destination_path,"administrator",ti.common_password,
				this.index_destination_path,"administrator",ti.common_password, this.hash_destination_path,"administrator",ti.common_password,10,10240,this.datastore_path,"administrator",ti.common_password,
				this.compression,"Mclaren@2010",false,to_email,"encryption_password"},
			{"update email alert to true and email id",ti.csr_org_id,ti.csr_token,this.datastore_name,true,true,true, this.datastore_id, 4,1024,true,this.data_destination_path,"administrator",ti.common_password,
				this.index_destination_path,"administrator",ti.common_password, this.hash_destination_path,"administrator",ti.common_password,10,10240,this.datastore_path,"administrator",ti.common_password,
				this.compression,"Mclaren@2010",true,"Chalamala@arcserve.com","encryption_password"},
			{"update datastore name",ti.csr_org_id,ti.csr_token,modify_datastore_name,true,true,true, this.datastore_id, this.block_size,this.hash_memory,false,this.data_destination_path,"administrator",ti.common_password,
					this.index_destination_path,"administrator",ti.common_password, this.hash_destination_path,"administrator",ti.common_password,10,10240,this.datastore_path,"administrator",ti.common_password,
					this.compression,"Mclaren@2010",true,"Chalamala@arcserve.com",""},
			{"update All destination paths",ti.csr_org_id,ti.csr_token,this.datastore_name,true,true,true, this.datastore_id, 4,1024,true,this.modify_data_destination_path,"administrator",ti.common_password,
						this.modify_index_destination_path,"administrator",ti.common_password, this.modify_hash_destination_path,"administrator",ti.common_password,10,10240,modify_datastore_path,"administrator",ti.common_password,
						this.compression,"Mclaren@2010",true,"Chalamala@arcserve.com","encryption_password"},
			{"update hash destination paths",ti.csr_org_id,ti.csr_token,this.datastore_name,true,true,true, this.datastore_id, 4,1024,true,this.modify_data_destination_path,"administrator",ti.common_password,
							this.modify_index_destination_path,"administrator",ti.common_password, spogServer.ReturnRandom("C:\\destination1")+"\\hash","administrator",ti.common_password,10,10240,modify_datastore_path,"administrator",ti.common_password,
							this.compression,"Mclaren@2010",true,"Chalamala@arcserve.com","encryption_password"},
			
		};
	}
	
	
	@Test(dataProvider = "updatecloudRPSDatastore",dependsOnMethods="postcloudRPSDatastore")
	public void updatecloudRPSDatastore(String organizationType,
									   String organization_id,
									   String validToken,
									   String datastore_name,
									   boolean dedupe_enabled,
									   boolean encryption_enabled,
									   boolean compression_enabled,
									   String datastore_id,
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
									   String to_replace) {
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		
		test.log(LogStatus.INFO, "Compose the hashmap for deduplication info "+organizationType);
		HashMap<String,Object> deduplicationInfo = spogcloudRPSServer.composededuplicationInfo(block_size, hash_memory,hash_on_ssd, data_destination_path, data_destination_username, data_destination_password, 
				index_destination_path, index_destination_username, index_destination_password, hash_destination_path, hash_destination_username, hash_destination_password);
		
		test.log(LogStatus.INFO, "Compose the hashmap for datastoreproperties info "+organizationType);
		HashMap<String,Object> datastorepropertiesInfo = spogcloudRPSServer.composedatastorepropertiesInfo(concurrent_active_nodes, capacity, datastore_path, datastore_username, datastore_password, compression, encryption_password, deduplicationInfo, send_email, to_email);
		
		test.log(LogStatus.INFO, "update the cloud rps datastore "+organizationType);
		Response response = spogcloudRPSServer.updateCloudRPSDataStore(datastore_name, dedupe_enabled, encryption_enabled, compression_enabled, datastore_id, datastorepropertiesInfo, validToken);
		 
		test.log(LogStatus.INFO, "Perform the response validation");
		
		spogcloudRPSServer.validateCloudRPSDataStoreResponse(response, datastore_name, dedupe_enabled, encryption_enabled, compression_enabled, cloudRPS_id,rps_server_name, datastorepropertiesInfo, SpogConstants.SUCCESS_GET_PUT_DELETE, null, "", test);
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//test.log(LogStatus.INFO, "Delete the cloud rps datastore");
		//spogcloudRPSServer.deleteCloudRPSDataStore(datastore_id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
				
	}
	
	
	@DataProvider(name = "updatecloudRPSDatastore_400")
	public final Object[][] updatecloudRPSDatastore_400() {
		return new Object[][] {
			
			//Cannot Change the datastore folder - Done
			//Cannot change the encryption  - Done
			//Cannot change the compression - Done
			//Cannot change the data/index path - Done
			//Cannot change block size - Done
			{"update datastore path not allowed",ti.csr_org_id,ti.csr_token,modify_datastore_name,true,true,true, this.datastore_id, this.block_size,this.hash_memory,false,this.modify_data_destination_path,"administrator",ti.common_password,
				this.modify_index_destination_path,"administrator",ti.common_password, this.modify_hash_destination_path,"administrator",ti.common_password,this.concurrent_active_nodes,102400,"C:\\Users","administrator",ti.common_password,
				this.compression,"Mclaren@2010",true,"Chalamala@arcserve.com","When modifying folders of a data store, the folder must meet one of the following criteria:<br/>1)    "
						+ "All data store folders are set as empty folders. This is to let you modify the data store as a new data store.<br/>2)    "
						+ "Only the hash destination is set as an empty folder and the remaining folders of the data store are not changed. "
						+ "This is to let you change the hash destination location."},
			
			
			// Commented because these are removed from Request DTO and whatever we give it will take the details which are given while creating so returns success.
			/*{"update encryption not allowed",ti.csr_org_id,ti.csr_token,modify_datastore_name,true,false,true, this.datastore_id, this.block_size,this.hash_memory,false,this.modify_data_destination_path,"administrator",ti.common_password,
				this.modify_index_destination_path,"administrator",ti.common_password, this.modify_hash_destination_path,"administrator",ti.common_password,this.concurrent_active_nodes,102400,this.modify_datastore_path,"administrator",ti.common_password,
				this.compression,"Mclaren@2010",true,"Chalamala@arcserve.com",""},
			{"update compression not allowed",ti.csr_org_id,ti.csr_token,modify_datastore_name,true,true,false, this.datastore_id, this.block_size,this.hash_memory,false,this.modify_data_destination_path,"administrator",ti.common_password,
				this.modify_index_destination_path,"administrator",ti.common_password, this.modify_hash_destination_path,"administrator",ti.common_password,this.concurrent_active_nodes,102400,this.modify_datastore_path,"administrator",ti.common_password,
				this.compression,"Mclaren@2010",true,"Chalamala@arcserve.com",""},
			*/
			
			{"update data destination path",ti.csr_org_id,ti.csr_token,modify_datastore_name,true,true,false, this.datastore_id, this.block_size,this.hash_memory,false,"C:\\"+spogServer.ReturnRandom("dest")+"\\data","administrator",ti.common_password,
				this.index_destination_path,"administrator",ti.common_password, this.modify_hash_destination_path,"administrator",ti.common_password,this.concurrent_active_nodes,102400,this.datastore_path,"administrator",ti.common_password,
				this.compression,"Mclaren@2010",true,"Chalamala@arcserve.com","When modifying folders of a data store, the folder must meet one of the following criteria:<br/>1)    "
						+ "All data store folders are set as empty folders. This is to let you modify the data store as a new data store.<br/>2)    "
						+ "Only the hash destination is set as an empty folder and the remaining folders of the data store are not changed. "
						+ "This is to let you change the hash destination location."},
			{"update index destination path",ti.csr_org_id,ti.csr_token,modify_datastore_name,true,true,false, this.datastore_id, this.block_size,this.hash_memory,false,this.data_destination_path,"administrator",ti.common_password,
				"C:\\"+spogServer.ReturnRandom("dest")+"\\index","administrator",ti.common_password, this.modify_hash_destination_path,"administrator",ti.common_password,this.concurrent_active_nodes,102400,this.datastore_path,"administrator",ti.common_password,
				this.compression,"Mclaren@2010",true,"Chalamala@arcserve.com","When modifying folders of a data store, the folder must meet one of the following criteria:<br/>1)    "
						+ "All data store folders are set as empty folders. This is to let you modify the data store as a new data store.<br/>2)    "
						+ "Only the hash destination is set as an empty folder and the remaining folders of the data store are not changed. "
						+ "This is to let you change the hash destination location."},
			{"update block size",ti.csr_org_id,ti.csr_token,modify_datastore_name,true,true,true, this.datastore_id, 64,this.hash_memory,false,this.data_destination_path,"administrator",ti.common_password,
				this.index_destination_path,"administrator",ti.common_password, this.modify_hash_destination_path,"administrator",ti.common_password,this.concurrent_active_nodes,102400,this.datastore_path,"administrator",ti.common_password,
				this.compression,"Mclaren@2010",true,"Chalamala@arcserve.com","The data store block size cannot be changed."},
			
		};
	}
	
	@Test(dataProvider = "updatecloudRPSDatastore_400",dependsOnMethods="postcloudRPSDatastore")
	public void updatecloudRPSDatastore_400(String organizationType,
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
										   String to_replace
								   			) {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		SpogMessageCode expectederrorMessage = null;  
		test.log(LogStatus.INFO, "Compose the hashmap for deduplication info "+organizationType);
		HashMap<String,Object> deduplicationInfo = spogcloudRPSServer.composededuplicationInfo(block_size, hash_memory,hash_on_ssd, data_destination_path, data_destination_username, data_destination_password, 
				index_destination_path, index_destination_username, index_destination_password, hash_destination_path, hash_destination_username, hash_destination_password);
		
		test.log(LogStatus.INFO, "Compose the hashmap for datastoreproperties info "+organizationType);
		HashMap<String,Object> datastorepropertiesInfo = spogcloudRPSServer.composedatastorepropertiesInfo(concurrent_active_nodes, capacity,datastore_path, datastore_username, datastore_password, compression, encryption_password, deduplicationInfo, send_email, to_email);
		
		test.log(LogStatus.INFO, "update the cloud rps datastore "+organizationType);
		Response response = spogcloudRPSServer.updateCloudRPSDataStore(datastore_name, dedupe_enabled, encryption_enabled, compression_enabled, datastore_id, datastorepropertiesInfo, validToken);
		 
		test.log(LogStatus.INFO, "Perform the response validation");
		
		spogcloudRPSServer.validateCloudRPSDataStoreResponse(response, datastore_name, dedupe_enabled, encryption_enabled, compression_enabled, rps_server_id,cloudRPS_name, datastorepropertiesInfo, SpogConstants.REQUIRED_INFO_NOT_EXIST, expectederrorMessage, "", test);
		
	}
	
	@DataProvider(name = "updatecloudRPSDatastore_401")
	public final Object[][] updatecloudRPSDatastore_401() {
		return new Object[][] {
			{"csr",ti.csr_org_id,ti.csr_token+"J",spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,2048,false,"C:\\destination\\data","administrator",ti.common_password,
				"C:\\destination\\index","administrator",ti.common_password, "C:\\destination\\hash","administrator",ti.common_password,4,10210,"C:\\destination","administrator",ti.common_password,
				"standard","",false,"abc@arcserve.com",SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT},
			{"csr",ti.csr_org_id,"",spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,2048,false,"C:\\destination\\data","administrator",ti.common_password,
					"C:\\destination\\index","administrator",ti.common_password, "C:\\destination\\hash","administrator",ti.common_password,4,102100,"C:\\destination","administrator",ti.common_password,
					"standard","",false,"abc@arcserve.com",SpogMessageCode.COMMON_AUTHENTICATION_FAILED},
			
		};
	}
	
	@Test(dataProvider = "updatecloudRPSDatastore_401",dependsOnMethods="postcloudRPSDatastore")
	public void updatecloudRPSDatastore_401(String organizationType,
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
										   SpogMessageCode expectederrormessage) {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		
		test.log(LogStatus.INFO, "Start the  cloud RPS in organization "+organizationType);
		
		test.log(LogStatus.INFO, "Compose the hashmap for deduplication info "+organizationType);
		HashMap<String,Object> deduplicationInfo = spogcloudRPSServer.composededuplicationInfo(block_size, hash_memory,hash_on_ssd, data_destination_path, data_destination_username, data_destination_password, 
				index_destination_path, index_destination_username, index_destination_password, hash_destination_path, hash_destination_username, hash_destination_password);
		
		test.log(LogStatus.INFO, "Compose the hashmap for datastoreproperties info "+organizationType);
		HashMap<String,Object> datastorepropertiesInfo = spogcloudRPSServer.composedatastorepropertiesInfo(concurrent_active_nodes,capacity, datastore_path, datastore_username, datastore_password, compression, encryption_password, deduplicationInfo, send_email, to_email);
		
		test.log(LogStatus.INFO, "Update the cloud rps datastore "+organizationType);
		Response response = spogcloudRPSServer.updateCloudRPSDataStore(datastore_name, dedupe_enabled, encryption_enabled, compression_enabled, datastore_id, datastorepropertiesInfo, validToken);
		
		test.log(LogStatus.INFO, "Perform the response validation");
		spogcloudRPSServer.validateCloudRPSDataStoreResponse(response, datastore_name, dedupe_enabled, encryption_enabled, compression_enabled, rps_server_id,cloudRPS_name, datastorepropertiesInfo, SpogConstants.NOT_LOGGED_IN, expectederrormessage, "", test);
		
		
		
	}
	
	@DataProvider(name = "updatecloudRPSDatastore_403")
	public final Object[][] updatecloudRPSDatastore_403() {
		return new Object[][] {
			{"direct",ti.direct_org1_id,ti.direct_org1_user1_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,2048,false,"C:\\destination\\data","administrator",ti.common_password,
				"C:\\destination\\index","administrator",ti.common_password, "C:\\destination\\hash","administrator",ti.common_password,4,102400,"C:\\destination","administrator",ti.common_password,
				"standard","",false,"abc@arcserve.com",SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"msp",ti.root_msp_org1_id,ti.root_msp_org1_user1_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,2048,false,"C:\\destination\\data","administrator",ti.common_password,
				"C:\\destination\\index","administrator",ti.common_password, "C:\\destination\\hash","administrator",ti.common_password,4,102400,"C:\\destination","administrator",ti.common_password,
				"standard","",false,"abc@arcserve.com",SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"suborg",ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,2048,false,"C:\\destination\\data","administrator",ti.common_password,
				"C:\\destination\\index","administrator",ti.common_password, "C:\\destination\\hash","administrator",ti.common_password,4,102400,"C:\\destination","administrator",ti.common_password,
				"standard","",false,"abc@arcserve.com",SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"mspaccountadmin",ti.root_msp1_suborg1_id,ti.root_msp_org1_msp_accountadmin1_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,2048,false,"C:\\destination\\data","administrator",ti.common_password,
				"C:\\destination\\index","administrator",ti.common_password, "C:\\destination\\hash","administrator",ti.common_password,4,102400,"C:\\destination","administrator",ti.common_password,
				"standard","",false,"abc@arcserve.com",SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"submsp",ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,2048,false,"C:\\destination\\data","administrator",ti.common_password,
					"C:\\destination\\index","administrator",ti.common_password, "C:\\destination\\hash","administrator",ti.common_password,4,102400,"C:\\destination","administrator",ti.common_password,
					"standard","",false,"abc@arcserve.com",SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"submspsuborg",ti.msp1_submsp1_sub_org1_id,ti.msp1_submsp1_suborg1_user1_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,2048,false,"C:\\destination\\data","administrator",ti.common_password,
					"C:\\destination\\index","administrator",ti.common_password, "C:\\destination\\hash","administrator",ti.common_password,4,102400,"C:\\destination","administrator",ti.common_password,
					"standard","",false,"abc@arcserve.com",SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"submspmspaccountadmin",ti.msp1_submsp1_sub_org1_id,ti.root_msp1_submsp1_account_admin_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,2048,false,"C:\\destination\\data","administrator",ti.common_password,
					"C:\\destination\\index","administrator",ti.common_password, "C:\\destination\\hash","administrator",ti.common_password,4,102400,"C:\\destination","administrator",ti.common_password,
					"standard","",false,"abc@arcserve.com",SpogMessageCode.RESOURCE_PERMISSION_DENY},
			
			{"csr readonly",ti.root_msp1_suborg1_id,ti.csr_readonly_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,2048,false,"C:\\destination\\data","administrator",ti.common_password,
					"C:\\destination\\index","administrator",ti.common_password, "C:\\destination\\hash","administrator",ti.common_password,4,102400,"C:\\destination","administrator",ti.common_password,
					"standard","",false,"abc@arcserve.com",SpogMessageCode.RESOURCE_PERMISSION_DENY},	
		};
	}
	
	@Test(dataProvider = "updatecloudRPSDatastore_403",dependsOnMethods="postcloudRPSDatastore")
	public void updatecloudRPSDatastore_403(String userType,
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
										   SpogMessageCode expectederrormessage) {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
				
		test.log(LogStatus.INFO, "Compose the hashmap for deduplication info ");
		HashMap<String,Object> deduplicationInfo = spogcloudRPSServer.composededuplicationInfo(block_size, hash_memory,hash_on_ssd, data_destination_path, data_destination_username, data_destination_password, 
				index_destination_path, index_destination_username, index_destination_password, hash_destination_path, hash_destination_username, hash_destination_password);
		
		test.log(LogStatus.INFO, "Compose the hashmap for datastoreproperties info ");
		HashMap<String,Object> datastorepropertiesInfo = spogcloudRPSServer.composedatastorepropertiesInfo(concurrent_active_nodes,capacity, datastore_path, datastore_username, datastore_password, compression, encryption_password, deduplicationInfo, send_email, to_email);
		
		test.log(LogStatus.INFO, "update the cloud rps datastore with "+userType+" user");
		Response response = spogcloudRPSServer.updateCloudRPSDataStore(datastore_name, dedupe_enabled, encryption_enabled, compression_enabled, datastore_id, datastorepropertiesInfo, validToken);
		
		test.log(LogStatus.INFO, "Perform the response validation");
		spogcloudRPSServer.validateCloudRPSDataStoreResponse(response, datastore_name, dedupe_enabled, encryption_enabled, compression_enabled, rps_server_id,cloudRPS_name, datastorepropertiesInfo, SpogConstants.INSUFFICIENT_PERMISSIONS, expectederrormessage, "", test);
		
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
