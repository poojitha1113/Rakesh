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

import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.Org4SPOGServer;
import InvokerServer.SPOGCloudRPSServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

/** This is to test whether the datastore creation on a RPS machine successful or not
 *  Each datastore will be assigned to a customer
 *  On creation of datastore the hash,data,index directories will be created on datastore 
 *  Directories information will be part of response
 *  
 * Prerequisites:
 * 1. Csr token which is used to create all organizations, since he is the super user
 * 2. Direct, msp and sub organizations and users under it
 * 3. RPS registered with CC - to create datastore we need an RPS registered
 * 
 * For more details on how to register RPS visit CreateCloudRPSTest
 * 
 * @author Rakesh.Chalamala
 * @sprint 14
 */
public class CreateRPSDataStoreTest extends base.prepare.Is4Org {
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
	String rps_server_name = null;
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
			
			 creationTime = System.currentTimeMillis();
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
		
		test.log(LogStatus.INFO, "Create cloud RPS");
		Response response = spogcloudRPSServer.createCloudRPS(cloud_rps_name, cloud_rps_port, cloud_rps_protocol, cloud_rps_username, cloud_rps_password, ti.csr_org_id, csr_site_id, datacenters[0], ti.csr_token);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		cloudRPS_id = response.then().extract().path("data.server_id");
		rps_server_name = response.then().extract().path("data.server_name");
	}
	
	@DataProvider(name = "postcloudRPS")
	public final Object[][] postcloudRPS() {
		return new Object[][] {
			{"csr",ti.csr_org_id,"Arcserve CSR",ti.csr_admin_user_id,"Xiang Li",ti.csr_token,"10.60.16.126","8014","https","administrator", "Mclaren@2013", csr_site_id,"RakSite",datacenters[0],"Zetta Test"},
		};
	}
	
	
	@Test(dataProvider = "postcloudRPS",enabled=false)
	public void createcloudRPS(String organizationType,
							   String organization_id,
							   String organization_name,
							   String user_id,
							   String user_name,
							   String validToken,
							   String server_name,
							   String server_port,
							   String server_protocol,
							   String server_username,
							   String server_password,
							   String site_id,
							   String site_name,
							   String datacenter_id,
							   String datacenter_name ) {
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		test.log(LogStatus.INFO, "Create cloud RPS in organization "+organizationType);
		Response response = spogcloudRPSServer.createCloudRPS(server_name, server_port, server_protocol, server_username, server_password, organization_id, site_id, datacenter_id, validToken);
		
		HashMap<String,Object> compose_cloudRPS_Info = spogcloudRPSServer.composeCloudRPSInfo(server_name, server_port, server_protocol, server_username, server_password, organization_id,organization_name, site_id,site_name, datacenter_id,datacenter_name,user_id,user_name);
		rps_server_name = server_name;
	
		test.log(LogStatus.INFO, "Perform the response validation");
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		cloudRPS_id = spogcloudRPSServer.verifyCloudRPS(response, compose_cloudRPS_Info, test);
				
	}
	
	@DataProvider(name = "postcloudRPSDatastore")
	public final Object[][] postcloudRPSDatastore() {
		return new Object[][] {
			{"csr",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("RakDatastore"),true,false,false, cloudRPS_id,rps_server_name, 4,1024,false,spogServer.ReturnRandom("C:\\destination\\data"),"administrator",ti.common_password,
				spogServer.ReturnRandom("C:\\destination\\index"),"administrator",ti.common_password, spogServer.ReturnRandom("C:\\destination\\hash"),"administrator",ti.common_password,4,102400,spogServer.ReturnRandom("C:\\destination1"),"administrator",ti.common_password,
				"standard","abcfdfa",true,"abc@arcserve.com","encryption_password"},
			{"csr",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("RakDatastore"),true,true,false, cloudRPS_id,rps_server_name, 8,2048,true,spogServer.ReturnRandom("C:\\destination\\data"),"administrator",ti.common_password,
					spogServer.ReturnRandom("C:\\destination\\index"),"administrator",ti.common_password, spogServer.ReturnRandom("C:\\destination\\hash"),"administrator",ti.common_password,2,102400,spogServer.ReturnRandom("C:\\destination1"),"administrator",ti.common_password,
					"standard","abcfdfa",false,"abc@arcserve.com","encryption_password"},
			{"csr",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("RakDatastore"),true,true,false, cloudRPS_id,rps_server_name, 16,1024,true,spogServer.ReturnRandom("C:\\destination\\data"),"administrator",ti.common_password,
						spogServer.ReturnRandom("C:\\destination\\index"),"administrator",ti.common_password, spogServer.ReturnRandom("C:\\destination\\hash"),"administrator",ti.common_password,2,102400,spogServer.ReturnRandom("C:\\destination1"),"administrator",ti.common_password,
						"maximum","abcfdfa",false,"abc@arcserve.com","encryption_password"},
			{"csr",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("RakDatastore"),true,true,false, cloudRPS_id,rps_server_name, 32,1024,true,spogServer.ReturnRandom("C:\\destination\\data"),"administrator",ti.common_password,
							spogServer.ReturnRandom("C:\\destination\\index"),"administrator",ti.common_password, spogServer.ReturnRandom("C:\\destination\\hash"),"administrator",ti.common_password,2,102400,spogServer.ReturnRandom("C:\\destination1"),"administrator",ti.common_password,
							"standard","abcfdfa",false,"abc@arcserve.com","encryption_password"},
			{"csr",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("RakDatastore"),true,true,true, cloudRPS_id,rps_server_name, 64,1024,true,spogServer.ReturnRandom("C:\\destination\\data"),"administrator",ti.common_password,
								spogServer.ReturnRandom("C:\\destination\\index"),"administrator",ti.common_password, spogServer.ReturnRandom("C:\\destination\\hash"),"administrator",ti.common_password,2,102400,spogServer.ReturnRandom("C:\\destination1"),"administrator",ti.common_password,
								"maximum","abcfdfa",false,"abc@arcserve.com","encryption_password"},
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
		HashMap<String,Object> deduplicationInfo = spogcloudRPSServer.composededuplicationInfo(block_size, hash_memory,hash_on_ssd, data_destination_path, data_destination_username, data_destination_password, 
				index_destination_path, index_destination_username, index_destination_password, hash_destination_path, hash_destination_username, hash_destination_password);
		
		test.log(LogStatus.INFO, "Compose the hashmap for datastoreproperties info "+organizationType);
		HashMap<String,Object> datastorepropertiesInfo = spogcloudRPSServer.composedatastorepropertiesInfo(concurrent_active_nodes, capacity, datastore_path, datastore_username, datastore_password, compression, encryption_password, deduplicationInfo, send_email, to_email);
		
		test.log(LogStatus.INFO, "Create the cloud rps datastore "+organizationType);
		Response response = spogcloudRPSServer.createCloudRPSDataStore(datastore_name, dedupe_enabled, encryption_enabled, compression_enabled, rps_server_id, datastorepropertiesInfo, validToken);
		
		test.log(LogStatus.INFO, "Perform the response validation");
		String datastore_id = spogcloudRPSServer.validateCloudRPSDataStoreResponse(response, datastore_name, dedupe_enabled, encryption_enabled, compression_enabled, rps_server_id,rps_server_name, datastorepropertiesInfo, SpogConstants.SUCCESS_POST, null, "", test);
		
		test.log(LogStatus.INFO, "Delete the cloud rps datastore");
		spogcloudRPSServer.deleteCloudRPSDataStore(datastore_id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
				
	}
	
	
	@DataProvider(name = "postcloudRPSDatastore_400")
	public final Object[][] postcloudRPSDatastore_400() {
		return new Object[][] {
			{"csr",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,1024,false,spogServer.ReturnRandom("C:\\destination\\data"),"administrator",ti.common_password,
				spogServer.ReturnRandom("C:\\destination\\index"),"administrator",ti.common_password, spogServer.ReturnRandom("C:\\destination\\hash"),"administrator",ti.common_password,4,102400,spogServer.ReturnRandom("C:\\destination"),"administrator",ti.common_password,
				"standard","",false,"abc@arcserve.com","encryption_password",SpogMessageCode.ENCRYPTION_PASSWORD_REQUIRED},
			{"csr",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 5,1024,false,spogServer.ReturnRandom("C:\\destination\\data"),"administrator",ti.common_password,
				spogServer.ReturnRandom("C:\\destination\\index"),"administrator",ti.common_password, spogServer.ReturnRandom("C:\\destination\\hash"),"administrator",ti.common_password,4,102400,spogServer.ReturnRandom("C:\\destination"),"administrator",ti.common_password,
				"standard","a",false,"abc@arcserve.com","block_size",SpogMessageCode.BLOCK_SIZE_NOT_VALID},
			{"csr",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,1024,false,spogServer.ReturnRandom("C:\\destination\\data"),"administrator",ti.common_password,
				spogServer.ReturnRandom("C:\\destination\\index"),"administrator",ti.common_password, spogServer.ReturnRandom("C:\\destination\\hash"),"administrator",ti.common_password,4,102400,spogServer.ReturnRandom("C:\\destination"),"administrator",ti.common_password,
				null,"abc",false,"abc@arcserve.com","compression_type",SpogMessageCode.COMPRESSION_TYPE_REQUIRED},
			{"csr",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,1024,false,spogServer.ReturnRandom("C:\\destination\\data"),"administrator",ti.common_password,
				spogServer.ReturnRandom("C:\\destination\\index"),"administrator",ti.common_password, spogServer.ReturnRandom("C:\\destination\\hash"),"administrator",ti.common_password,-1,102400,spogServer.ReturnRandom("C:\\destination"),"administrator",ti.common_password,
				"standard","abc",false,"abc@arcserve.com","concurrent_active_nodes",SpogMessageCode.MIN_CURRENT_ACTIVE_NODES_SHOULD_BE_1},
			{"csr",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,1024,false,spogServer.ReturnRandom("C:\\destination\\data"),"administrator",ti.common_password,
					spogServer.ReturnRandom("C:\\destination\\index"),"administrator",ti.common_password, spogServer.ReturnRandom("C:\\destination\\hash"),"administrator",ti.common_password,10001,102400,spogServer.ReturnRandom("C:\\destination"),"administrator",ti.common_password,
					"standard","abc",false,"abc@arcserve.com","concurrent_active_nodes",SpogMessageCode.MIN_CURRENT_ACTIVE_NODES_SHOULD_BE_1},// The specified maximum number of agent nodes for a data store must be between 1 and 10000.
			{"csr",ti.csr_org_id,ti.csr_token,"",true,true,true, cloudRPS_id, 4,1024,false,spogServer.ReturnRandom("C:\\destination\\data"),"administrator",ti.common_password,
				spogServer.ReturnRandom("C:\\destination\\index"),"administrator",ti.common_password, spogServer.ReturnRandom("C:\\destination\\hash"),"administrator",ti.common_password,4,102400,spogServer.ReturnRandom("C:\\destination"),"administrator",ti.common_password,
				"standard","abc",false,"abc@arcserve.com","datastore_name",SpogMessageCode.CAN_NOT_BE_BLANK},
			{"csr",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, "", 4,1024,false,spogServer.ReturnRandom("C:\\destination\\data"),"administrator",ti.common_password,
				spogServer.ReturnRandom("C:\\destination\\index"),"administrator",ti.common_password, spogServer.ReturnRandom("C:\\destination\\hash"),"administrator",ti.common_password,4,102400,spogServer.ReturnRandom("C:\\destination"),"administrator",ti.common_password,
				"standard","abc",false,"abc@arcserve.com","rsp_server_id",SpogMessageCode.INPUT_PARAMETER_IS_EMPTY},
			/*{"csr",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,1024,false,spogServer.ReturnRandom("C:\\destination\\data"),"administrator","",
				spogServer.ReturnRandom("C:\\destination\\index"),"administrator",ti.common_password, spogServer.ReturnRandom("C:\\destination\\hash"),"administrator",ti.common_password,4,102400,spogServer.ReturnRandom("C:\\destination"),"administrator",ti.common_password,
				"standard","abc",false,"abc@arcserve.com","Data Destination",SpogMessageCode.PASSWORD_NOT_PROVIDED},
			{"csr",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,1024,false,spogServer.ReturnRandom("C:\\destination\\data"),"",ti.common_password,
				spogServer.ReturnRandom("C:\\destination\\index"),"administrator",ti.common_password, spogServer.ReturnRandom("C:\\destination\\hash"),"administrator",ti.common_password,4,102400,spogServer.ReturnRandom("C:\\destination"),"administrator",ti.common_password,
				"standard","abc",false,"abc@arcserve.com","Data Destination",SpogMessageCode.USERNAME_NOT_PROVIDED},
			*/{"csr",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,1024,false,"","administrator",ti.common_password,
				spogServer.ReturnRandom("C:\\destination\\index"),"administrator",ti.common_password, spogServer.ReturnRandom("C:\\destination\\hash"),"administrator",ti.common_password,4,102400,spogServer.ReturnRandom("C:\\destination"),"administrator",ti.common_password,
				"standard","abc",false,"abc@arcserve.com","Data Destination",SpogMessageCode.PATH_PROVIDED_IS_NOT_VALID},
			{"csr",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,1024,false,spogServer.ReturnRandom("C:\\destination\\data"),"administrator",ti.common_password,
				"","administrator",ti.common_password, spogServer.ReturnRandom("C:\\destination\\hash"),"administrator",ti.common_password,4,102400,spogServer.ReturnRandom("C:\\destination"),"administrator",ti.common_password,
				"standard","abc",false,"abc@arcserve.com","Index Destination",SpogMessageCode.PATH_PROVIDED_IS_NOT_VALID},
			/*{"csr",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,1024,false,spogServer.ReturnRandom("C:\\destination\\data"),"administrator",ti.common_password,
				spogServer.ReturnRandom("C:\\destination\\index"),"",ti.common_password, spogServer.ReturnRandom("C:\\destination\\hash"),"administrator",ti.common_password,4,102400,spogServer.ReturnRandom("C:\\destination"),"administrator",ti.common_password,
				"standard","abc",false,"abc@arcserve.com","Index Destination",SpogMessageCode.USERNAME_NOT_PROVIDED},
			{"csr",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,1024,false,spogServer.ReturnRandom("C:\\destination\\data"),"administrator",ti.common_password,
				spogServer.ReturnRandom("C:\\destination\\index"),"administrator","", spogServer.ReturnRandom("C:\\destination\\hash"),"administrator",ti.common_password,4,102400,spogServer.ReturnRandom("C:\\destination"),"administrator",ti.common_password,
				"standard","abc",false,"abc@arcserve.com","Index Destination",SpogMessageCode.PASSWORD_NOT_PROVIDED},
			*/{"csr",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,1024,false,spogServer.ReturnRandom("C:\\destination\\data"),"administrator",ti.common_password,
				spogServer.ReturnRandom("C:\\destination\\index"),"administrator",ti.common_password, "","administrator",ti.common_password,4,102400,spogServer.ReturnRandom("C:\\destination"),"administrator",ti.common_password,
				"standard","abc",false,"abc@arcserve.com","Hash Destination",SpogMessageCode.PATH_PROVIDED_IS_NOT_VALID},
			/*{"csr",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,1024,false,spogServer.ReturnRandom("C:\\destination\\data"),"administrator",ti.common_password,
				spogServer.ReturnRandom("C:\\destination\\index"),"administrator",ti.common_password, spogServer.ReturnRandom("C:\\destination\\hash"),"",ti.common_password,4,102400,spogServer.ReturnRandom("C:\\destination"),"administrator",ti.common_password,
				"standard","abc",false,"abc@arcserve.com","Hash Destination",SpogMessageCode.USERNAME_NOT_PROVIDED},
			{"csr",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,1024,false,spogServer.ReturnRandom("C:\\destination\\data"),"administrator",ti.common_password,
				spogServer.ReturnRandom("C:\\destination\\index"),"administrator",ti.common_password, spogServer.ReturnRandom("C:\\destination\\hash"),"administrator","",4,102400,spogServer.ReturnRandom("C:\\destination"),"administrator",ti.common_password,
				"standard","abc",false,"abc@arcserve.com","Hash Destination",SpogMessageCode.PASSWORD_NOT_PROVIDED},
			*/{"csr",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,1024,false,spogServer.ReturnRandom("C:\\destination\\data"),"administrator",ti.common_password,
				spogServer.ReturnRandom("C:\\destination\\index"),"administrator",ti.common_password, spogServer.ReturnRandom("C:\\destination\\hash"),"administrator",ti.common_password,4,102400,"\\datastore","administrator",ti.common_password,
				"standard","abc",false,"abc@arcserve.com","datastore_path",SpogMessageCode.PATH_PROVIDED_IS_NOT_VALID},
			/*{"csr",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,1024,false,spogServer.ReturnRandom("C:\\destination\\data"),"administrator",ti.common_password,
				spogServer.ReturnRandom("C:\\destination\\index"),"administrator",ti.common_password, spogServer.ReturnRandom("C:\\destination\\hash"),"administrator",ti.common_password,4,102400,spogServer.ReturnRandom("C:\\destination\\store"),"",ti.common_password,
				"standard","abc",false,"abc@arcserve.com","datastore_username",SpogMessageCode.USERNAME_NOT_PROVIDED},
			{"csr",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,1024,false,spogServer.ReturnRandom("C:\\destination\\data"),"administrator",ti.common_password,
				spogServer.ReturnRandom("C:\\destination\\index"),"administrator",ti.common_password, spogServer.ReturnRandom("C:\\destination\\hash"),"administrator",ti.common_password,4,102400,spogServer.ReturnRandom("C:\\destination\\store"),"administrator","",
				"standard","abc",false,"abc@arcserve.com","datastore_password",SpogMessageCode.PASSWORD_NOT_PROVIDED},
			*/
			
			//When creating a data store, the data store data destination must be empty and cannot contain hidden or system files
			{"csr",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,1024,false,"C:\\Users","administrator",ti.common_password,
				spogServer.ReturnRandom("C:\\destination\\index"),"administrator",ti.common_password, spogServer.ReturnRandom("C:\\destination\\hash"),"administrator",ti.common_password,4,102400,spogServer.ReturnRandom("C:\\destination\\\\data"),"administrator",ti.common_password,
				"standard","abc",false,"abc@arcserve.com","datastore_path",SpogMessageCode.PATH_PROVIDED_IS_NOT_VALID},
			{"csr",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,1024,false,spogServer.ReturnRandom("C:\\destination\\data"),"administrator",ti.common_password,
					"C:\\Users","administrator",ti.common_password, spogServer.ReturnRandom("C:\\destination\\hash"),"administrator",ti.common_password,4,102400,spogServer.ReturnRandom("C:\\destination\\\\data"),"administrator",ti.common_password,
					"standard","abc",false,"abc@arcserve.com","datastore_path",SpogMessageCode.PATH_PROVIDED_IS_NOT_VALID},
			{"csr",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,1024,false,spogServer.ReturnRandom("C:\\destination\\data"),"administrator",ti.common_password,
						spogServer.ReturnRandom("C:\\destination\\index"),"administrator",ti.common_password, "C:\\Users","administrator",ti.common_password,4,102400,spogServer.ReturnRandom("C:\\destinatio\\data"),"administrator",ti.common_password,
						"standard","abc",false,"abc@arcserve.com","datastore_path",SpogMessageCode.PATH_PROVIDED_IS_NOT_VALID},
			{"csr",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,1024,false,spogServer.ReturnRandom("C:\\destination\\data"),"administrator",ti.common_password,
							spogServer.ReturnRandom("C:\\destination\\index"),"administrator",ti.common_password, spogServer.ReturnRandom("C:\\destination\\hash"),"administrator",ti.common_password,4,102400,"C:\\Users","administrator",ti.common_password,
							"standard","abc",false,"abc@arcserve.com","datastore_path",SpogMessageCode.PATH_PROVIDED_IS_NOT_VALID},
			
			//Capacity usage required
			{"csr",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,1024,false,spogServer.ReturnRandom("C:\\destination\\data"),"administrator",ti.common_password,
								spogServer.ReturnRandom("C:\\destination\\index"),"administrator",ti.common_password, spogServer.ReturnRandom("C:\\destination\\hash"),"administrator",ti.common_password,4,-1,spogServer.ReturnRandom("C:\\destinatio\\data"),"administrator",ti.common_password,
								"standard","abc",false,"abc@arcserve.com","datastore_path",SpogMessageCode.CAPACITY_MUST_BE_GREATERTHAN_ZERO},
			{"csr",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,1024,false,spogServer.ReturnRandom("C:\\destination\\data"),"administrator",ti.common_password,
									spogServer.ReturnRandom("C:\\destination\\index"),"administrator",ti.common_password, spogServer.ReturnRandom("C:\\destination\\hash"),"administrator",ti.common_password,4,0,spogServer.ReturnRandom("C:\\destinatio\\data"),"administrator",ti.common_password,
									"standard","abc",false,"abc@arcserve.com","datastore_path",SpogMessageCode.CAPACITY_MUST_BE_GREATERTHAN_ZERO},			
			{"csr",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,1024,false,spogServer.ReturnRandom("C:\\destination\\data"),"administrator",ti.common_password,
										spogServer.ReturnRandom("C:\\destination\\index"),"administrator",ti.common_password, spogServer.ReturnRandom("C:\\destination\\hash"),"administrator",ti.common_password,4,-0,spogServer.ReturnRandom("C:\\destinatio\\data"),"administrator",ti.common_password,
										"standard","abc",false,"abc@arcserve.com","datastore_path",SpogMessageCode.CAN_NOT_BE_BLANK},// if capacity = -0 then adding null or leaving empty while composing
		};
	}
	
	@Test(dataProvider = "postcloudRPSDatastore_400",enabled=true)
	public void postcloudRPSDatastore_400(String organizationType,
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
										   SpogMessageCode expectederrormessage
										   ) {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		String rps_server_name=null;
		
		test.log(LogStatus.INFO, "Start the  cloud RPS in organization "+organizationType);
		
		test.log(LogStatus.INFO, "Compose the hashmap for deduplication info "+organizationType);
		HashMap<String,Object> deduplicationInfo = spogcloudRPSServer.composededuplicationInfo(block_size, hash_memory,hash_on_ssd, data_destination_path, data_destination_username, data_destination_password, 
				index_destination_path, index_destination_username, index_destination_password, hash_destination_path, hash_destination_username, hash_destination_password);
		
		test.log(LogStatus.INFO, "Compose the hashmap for datastoreproperties info "+organizationType);
		HashMap<String,Object> datastorepropertiesInfo = spogcloudRPSServer.composedatastorepropertiesInfo(concurrent_active_nodes, capacity,datastore_path, datastore_username, datastore_password, compression, encryption_password, deduplicationInfo, send_email, to_email);
		
		test.log(LogStatus.INFO, "Create the cloud rps datastore "+organizationType);
		Response response = spogcloudRPSServer.createCloudRPSDataStore(datastore_name, dedupe_enabled, encryption_enabled, compression_enabled, rps_server_id, datastorepropertiesInfo, validToken);
		
		/*test.log(LogStatus.INFO, "Perform the response validation");
		if(to_replace.equalsIgnoreCase("encryption_password")) {
			expectederrorMessage = SpogMessageCode.ENCRYPTION_PASSWORD_REQUIRED;
		}else if (to_replace.equalsIgnoreCase("block_size")) {
			expectederrorMessage = SpogMessageCode.BLOCK_SIZE_NOT_VALID;
		}else if(to_replace.equalsIgnoreCase("compression_type")) {
			expectederrorMessage = SpogMessageCode.COMPRESSION_TYPE_REQUIRED;
		}*/
		spogcloudRPSServer.validateCloudRPSDataStoreResponse(response, datastore_name, dedupe_enabled, encryption_enabled, compression_enabled, rps_server_id, rps_server_name, datastorepropertiesInfo, SpogConstants.REQUIRED_INFO_NOT_EXIST, expectederrormessage, "", test);
		
	}
	
	@DataProvider(name = "postcloudRPSDatastore_401")
	public final Object[][] postcloudRPSDatastore_401() {
		return new Object[][] {
			{"csr",ti.csr_org_id,ti.csr_token+"J",spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,1024,false,spogServer.ReturnRandom("C:\\destination\\data"),"administrator",ti.common_password,
				spogServer.ReturnRandom("C:\\destination\\index"),"administrator",ti.common_password, spogServer.ReturnRandom("C:\\destination\\hash"),"administrator",ti.common_password,4,102400,spogServer.ReturnRandom("C:\\destination"),"administrator",ti.common_password,
				"standard","",false,"abc@arcserve.com",SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT},
			{"csr",ti.csr_org_id,"",spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,1024,false,spogServer.ReturnRandom("C:\\destination\\data"),"administrator",ti.common_password,
					spogServer.ReturnRandom("C:\\destination\\index"),"administrator",ti.common_password, spogServer.ReturnRandom("C:\\destination\\hash"),"administrator",ti.common_password,4,102400,spogServer.ReturnRandom("C:\\destination"),"administrator",ti.common_password,
					"standard","",false,"abc@arcserve.com",SpogMessageCode.COMMON_AUTHENTICATION_FAILED},
			
		};
	}
	
	@Test(dataProvider = "postcloudRPSDatastore_401")
	public void postcloudRPSDatastore_401(String organizationType,
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
		HashMap<String,Object> datastorepropertiesInfo = spogcloudRPSServer.composedatastorepropertiesInfo(concurrent_active_nodes, capacity, datastore_path, datastore_username, datastore_password, compression, encryption_password, deduplicationInfo, send_email, to_email);
		
		test.log(LogStatus.INFO, "Create the cloud rps datastore "+organizationType);
		Response response = spogcloudRPSServer.createCloudRPSDataStore(datastore_name, dedupe_enabled, encryption_enabled, compression_enabled, rps_server_id, datastorepropertiesInfo, validToken);
		
		test.log(LogStatus.INFO, "Perform the response validation");
		spogcloudRPSServer.validateCloudRPSDataStoreResponse(response, datastore_name, dedupe_enabled, encryption_enabled, compression_enabled, rps_server_id,rps_server_name, datastorepropertiesInfo, SpogConstants.NOT_LOGGED_IN, expectederrormessage, "", test);
		
		
		
	}
	
	@DataProvider(name = "postcloudRPSDatastore_403")
	public final Object[][] postcloudRPSDatastore_403() {
		return new Object[][] {
			{"direct",ti.direct_org1_id,ti.direct_org1_user1_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,1024,false,"C:\\destination\\data","administrator",ti.common_password,
				"C:\\destination\\index","administrator",ti.common_password, "C:\\destination\\hash","administrator",ti.common_password,4,102400,"C:\\destination","administrator",ti.common_password,
				"standard","",false,"abc@arcserve.com",SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"msp",ti.root_msp_org1_id,ti.root_msp_org1_user1_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,1024,false,"C:\\destination\\data","administrator",ti.common_password,
				"C:\\destination\\index","administrator",ti.common_password, "C:\\destination\\hash","administrator",ti.common_password,4,102400,"C:\\destination","administrator",ti.common_password,
				"standard","",false,"abc@arcserve.com",SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"suborg",ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,1024,false,"C:\\destination\\data","administrator",ti.common_password,
				"C:\\destination\\index","administrator",ti.common_password, "C:\\destination\\hash","administrator",ti.common_password,4,102400,"C:\\destination","administrator",ti.common_password,
				"standard","",false,"abc@arcserve.com",SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"mspaccountadmin",ti.root_msp1_suborg1_id,ti.root_msp1_submsp1_account_admin_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,1024,false,"C:\\destination\\data","administrator",ti.common_password,
				"C:\\destination\\index","administrator",ti.common_password, "C:\\destination\\hash","administrator",ti.common_password,4,102400,"C:\\destination","administrator",ti.common_password,
				"standard","",false,"abc@arcserve.com",SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"submsp",ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,1024,false,"C:\\destination\\data","administrator",ti.common_password,
					"C:\\destination\\index","administrator",ti.common_password, "C:\\destination\\hash","administrator",ti.common_password,4,102400,"C:\\destination","administrator",ti.common_password,
					"standard","",false,"abc@arcserve.com",SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"submspsuborg",ti.msp1_submsp1_sub_org1_id,ti.msp1_submsp1_suborg1_user1_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,1024,false,"C:\\destination\\data","administrator",ti.common_password,
					"C:\\destination\\index","administrator",ti.common_password, "C:\\destination\\hash","administrator",ti.common_password,4,102400,"C:\\destination","administrator",ti.common_password,
					"standard","",false,"abc@arcserve.com",SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"submspaccountadmin",ti.msp1_submsp1_sub_org1_id,ti.root_msp1_submsp1_account_admin_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,1024,false,"C:\\destination\\data","administrator",ti.common_password,
					"C:\\destination\\index","administrator",ti.common_password, "C:\\destination\\hash","administrator",ti.common_password,4,102400,"C:\\destination","administrator",ti.common_password,
					"standard","",false,"abc@arcserve.com",SpogMessageCode.RESOURCE_PERMISSION_DENY},
			
			{"csr readonly",ti.root_msp1_suborg1_id,ti.csr_readonly_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,1024,false,"C:\\destination\\data","administrator",ti.common_password,
					"C:\\destination\\index","administrator",ti.common_password, "C:\\destination\\hash","administrator",ti.common_password,4,102400,"C:\\destination","administrator",ti.common_password,
					"standard","",false,"abc@arcserve.com",SpogMessageCode.RESOURCE_PERMISSION_DENY},
			
			
		};
	}
	
	@Test(dataProvider = "postcloudRPSDatastore_403")
	public void postcloudRPSDatastore_403(String userType,
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
		
		test.log(LogStatus.INFO, "Compose the hashmap for deduplication info "+userType);
		HashMap<String,Object> deduplicationInfo = spogcloudRPSServer.composededuplicationInfo(block_size, hash_memory,hash_on_ssd, data_destination_path, data_destination_username, data_destination_password, 
				index_destination_path, index_destination_username, index_destination_password, hash_destination_path, hash_destination_username, hash_destination_password);
		
		test.log(LogStatus.INFO, "Compose the hashmap for datastoreproperties info "+userType);
		HashMap<String,Object> datastorepropertiesInfo = spogcloudRPSServer.composedatastorepropertiesInfo(concurrent_active_nodes, capacity, datastore_path, datastore_username, datastore_password, compression, encryption_password, deduplicationInfo, send_email, to_email);
		
		test.log(LogStatus.INFO, "Create the cloud rps datastore with "+userType+" user");
		Response response = spogcloudRPSServer.createCloudRPSDataStore(datastore_name, dedupe_enabled, encryption_enabled, compression_enabled, rps_server_id, datastorepropertiesInfo, validToken);
		
		test.log(LogStatus.INFO, "Perform the response validation");
		spogcloudRPSServer.validateCloudRPSDataStoreResponse(response, datastore_name, dedupe_enabled, encryption_enabled, compression_enabled, rps_server_id,rps_server_name, datastorepropertiesInfo, SpogConstants.INSUFFICIENT_PERMISSIONS, expectederrormessage, "", test);
		
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
