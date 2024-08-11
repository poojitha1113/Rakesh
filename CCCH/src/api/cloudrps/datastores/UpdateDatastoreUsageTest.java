package api.cloudrps.datastores;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
import java.util.UUID;

import javax.sql.rowset.CachedRowSet;

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

/** This is to test whether the datastore usage details are updated successfully or not
 *  Each datastore will be assigned to a customer
 *  On creation of datastore the hash,data,index directories will be created on datastore 
 *  Directories information will be part of response
 *  We call the api using datastore ids in the request payload which we get on creation of datstore
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

public class UpdateDatastoreUsageTest extends base.prepare.Is4Org {
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
	String datastore_id = null;
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
	public void beforeClass(String baseURI, String port, String logFolder,String runningMachine,String buildVersion) {
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
			
			{"csr",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,1024,true,"C:\\"+spogServer.ReturnRandom("destination\\data"),"administrator",ti.common_password,
					"C:\\"+spogServer.ReturnRandom("index"),"administrator",ti.common_password, "C:\\dest"+spogServer.ReturnRandom("hash"),"administrator",ti.common_password,4,102400,"C:\\"+spogServer.ReturnRandom("dest"),"administrator",ti.common_password,
					"standard",ti.common_password,false,"abc@arcserve.com","encryption_password",SpogConstants.SUCCESS_GET_PUT_DELETE,null},
		};
	}
	
	@Test(dataProvider = "getCloudRPSDatastoresById",enabled=true)
	public void createCloudRPSDatastoresById_200(String organizationType,
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
		String datastore_id = spogcloudRPSServer.validateCloudRPSDataStoreResponse(response, datastore_name, dedupe_enabled, encryption_enabled, compression_enabled, rps_server_id,cloudRPS_name, datastorepropertiesInfo, SpogConstants.SUCCESS_POST, null, "", test);
		
		datastore_ids.add(datastore_id);
	}	
	
	@DataProvider(name="datastoreUsage")
	public Object[][] datastoreUsage(){
		return new Object[][] {
			{"Update with csr token",ti.csr_token, datastore_ids,String.valueOf(System.currentTimeMillis()), "running",123.0,"4",2048,1024,5.0,3.0,45.0,10.0,20.0,11.0,21.0,12.0,22.0,13.0,23.0,20.0,50.0, SpogConstants.SUCCESS_GET_PUT_DELETE,null}
		};
	}
	
	@Test(dataProvider="datastoreUsage",dependsOnMethods="createCloudRPSDatastoresById_200")
	public void updateDatastoreUsageValid(String caseType, String token,
											ArrayList<String> datastore_ids, String timestamp,
											String status, double source_data_size,
											String dedupe_savings, long storage_space,
											long capacity_usage, double deduplication_percentage,
											double compression_percentage, double overall_data_reduction,
											double backup_dest_usage, double backup_dest_capacity,
											double data_dest_usage, double data_dest_capacity,
											double index_dest_usage, double index_dest_capacity,
											double hash_dest_usage, double hash_dest_capacity,
											double mem_allocation_usage, double mem_allocation_capacity,
											int expectedStatusCode, SpogMessageCode expectedErrorMessage
											) {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		ArrayList<HashMap<String, Object>> expectedResponse = new ArrayList<>();
		
		//Update usage details of only one datastore
		test.log(LogStatus.INFO, "Compose datastore usage details for datastore");
		HashMap<String, Object> datastoreInfo = spogcloudRPSServer.composeDatastoreUsageInfo(datastore_ids.get(0), timestamp, status, source_data_size, dedupe_savings, storage_space, capacity_usage, deduplication_percentage, compression_percentage,
				overall_data_reduction, backup_dest_usage, backup_dest_capacity, data_dest_usage, data_dest_capacity, index_dest_usage, index_dest_capacity, hash_dest_usage, hash_dest_capacity,
				mem_allocation_usage, mem_allocation_capacity);
		expectedResponse.add(datastoreInfo);
		
		test.log(LogStatus.INFO, "Update the datstore usage details of two datastores.");
		spogcloudRPSServer.putDataStoresUsage(token, expectedResponse,expectedStatusCode,expectedErrorMessage, test);
		
		test.log(LogStatus.INFO, "Get datastore to validate the usage details");
		Response response = spogcloudRPSServer.getCloudRPSDatastoreById(token, datastore_ids.get(0), expectedStatusCode, test);
		
		test.log(LogStatus.INFO, "Validate the datastore usage details for datstore");
		spogcloudRPSServer.validateDataStoreUsageResponse(token, response, expectedResponse.get(0), test);
		
		
		//Update usage details of two destinations at a time
		expectedResponse = new ArrayList<>();
		datastoreInfo = new HashMap<>();
		test.log(LogStatus.INFO, "Compose datastore usage details for datastore 1");
		datastoreInfo = spogcloudRPSServer.composeDatastoreUsageInfo(datastore_ids.get(0), timestamp+1, status, source_data_size+1, dedupe_savings+1, storage_space+1, capacity_usage+1, deduplication_percentage+1, compression_percentage+1,
				overall_data_reduction+1, backup_dest_usage+1, backup_dest_capacity+1, data_dest_usage+1, data_dest_capacity+1, index_dest_usage+1, index_dest_capacity+1, hash_dest_usage+1, hash_dest_capacity+1,
				mem_allocation_usage+1, mem_allocation_capacity+1);
		expectedResponse.add(datastoreInfo);
		
		datastoreInfo= new HashMap<>();
		test.log(LogStatus.INFO, "Compose datastore usage details for datastore 2");
		 datastoreInfo = spogcloudRPSServer.composeDatastoreUsageInfo(datastore_ids.get(1), timestamp+2, status, source_data_size+2, dedupe_savings+2, storage_space+2, capacity_usage+2, deduplication_percentage+2, compression_percentage+2,
				overall_data_reduction+2, backup_dest_usage+2, backup_dest_capacity+2, data_dest_usage+2, data_dest_capacity+2, index_dest_usage+2, index_dest_capacity+2, hash_dest_usage+2, hash_dest_capacity+2,
				mem_allocation_usage+2, mem_allocation_capacity+2);
		expectedResponse.add(datastoreInfo);		
		
		
		test.log(LogStatus.INFO, "Update the datstore usage details of two datastores.");
		spogcloudRPSServer.putDataStoresUsage(token, expectedResponse,expectedStatusCode,expectedErrorMessage, test);
		
		test.log(LogStatus.INFO, "Get datastore 1 to validate the usage details");
		response = spogcloudRPSServer.getCloudRPSDatastoreById(token, datastore_ids.get(0), expectedStatusCode, test);
		
		test.log(LogStatus.INFO, "Validate the datastore usage details for datstore 1");
		spogcloudRPSServer.validateDataStoreUsageResponse(token, response, expectedResponse.get(0), test);
		
		test.log(LogStatus.INFO, "Get datastore 2 to validate the usage details");
		response = spogcloudRPSServer.getCloudRPSDatastoreById(token, datastore_ids.get(1), expectedStatusCode, test);
		
		test.log(LogStatus.INFO, "Validate the datastore usage details for datstore 2");
		spogcloudRPSServer.validateDataStoreUsageResponse(token, response, expectedResponse.get(1), test);
		
	}
	
	@DataProvider(name = "updateDatastoreUsage_Invalid")
	public Object[][] updateDatastoreUsage_Invalid(){
		return new Object[][] {
			//400
			{"Update Datastore Usage details with null as datastore id",ti.csr_token,null,String.valueOf(System.currentTimeMillis()),"running",SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.COLUMN_CANNOT_BLANK},
			{"Update Datastore Usage details with invalid as datastore id",ti.csr_token,"invalid",String.valueOf(System.currentTimeMillis()),"running",SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID},
			{"Update Datastore Usage details with null as timestamp",ti.csr_token,UUID.randomUUID().toString(),null,"running",SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.COLUMN_CANNOT_BLANK},
			{"Update Datastore Usage details with invalid as timestamp",ti.csr_token,UUID.randomUUID().toString(),String.valueOf("invalid"),"running",SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_NOT_LONG_VALUE},
			{"Update Datastore Usage details with null as status",ti.csr_token,UUID.randomUUID().toString(),String.valueOf(System.currentTimeMillis()),null,SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.COLUMN_CANNOT_BLANK},
			{"Update Datastore Usage details with invalid as status",ti.csr_token,UUID.randomUUID().toString(),String.valueOf(System.currentTimeMillis()),"invalid",SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_NOT_VALID_ENUM},
						
			//401
			{"Update Datastore Usage details with missing token","",UUID.randomUUID().toString(),String.valueOf(System.currentTimeMillis()),"running",SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED},
			{"Update Datastore Usage details with invalid as token",ti.csr_token+1,UUID.randomUUID().toString(),String.valueOf(System.currentTimeMillis()),"running",SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT},
			
			//403
			{"Update Datastore Usage details with Direct organization user token", ti.direct_org1_user1_token,UUID.randomUUID().toString(),String.valueOf(System.currentTimeMillis()),"running", SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Update Datastore Usage details with MSP organization user token", ti.root_msp_org1_user1_token,UUID.randomUUID().toString(),String.valueOf(System.currentTimeMillis()),"running", SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Update Datastore Usage details with Sub organization user token", ti.root_msp1_suborg1_user1_token,UUID.randomUUID().toString(),String.valueOf(System.currentTimeMillis()),"running", SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Update Datastore Usage details with MSP ACCOUNT ADMIN token", ti.root_msp_org1_msp_accountadmin1_token,UUID.randomUUID().toString(),String.valueOf(System.currentTimeMillis()),"running", SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Update Datastore Usage details with SUB MSP organization user token", ti.root_msp_org1_user1_token,UUID.randomUUID().toString(),String.valueOf(System.currentTimeMillis()),"running", SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Update Datastore Usage details with SUB MSP Sub organization user token", ti.msp1_submsp1_suborg1_user1_token,UUID.randomUUID().toString(),String.valueOf(System.currentTimeMillis()),"running", SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Update Datastore Usage details with SUB MSP ACCOUNT ADMIN token", ti.root_msp1_submsp1_account_admin_token,UUID.randomUUID().toString(),String.valueOf(System.currentTimeMillis()),"running", SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Update Datastore Usage details with csr_readonly_token", ti.csr_readonly_token,UUID.randomUUID().toString(),String.valueOf(System.currentTimeMillis()),"running", SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			
			//404 -> 200 Will not update as no datastore existing and won't throw any error aswell.
			{"Update Datastore Usage details with datastore_id that does not exist",ti.csr_token,UUID.randomUUID().toString(),String.valueOf(System.currentTimeMillis()),"running",SpogConstants.SUCCESS_GET_PUT_DELETE, null},
		};
	}
	@Test(dataProvider = "updateDatastoreUsage_Invalid")
	public void updateDatastoreUsage_Invalid(String caseType, String token,String datastore_id, String timestamp, String status, int expectedStatusCode, SpogMessageCode expectedErrorMessage) {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+caseType);
		
		ArrayList<HashMap<String, Object>> expectedData = new ArrayList<>();
		HashMap<String, Object> datastoreInfo = new HashMap<>();

		datastoreInfo.put("datastore_id", datastore_id);
		datastoreInfo.put("timestamp", timestamp);
		datastoreInfo.put("status", status);
		expectedData.add(datastoreInfo);
		
		test.log(LogStatus.INFO, caseType);
		spogcloudRPSServer.putDataStoresUsage(token, expectedData, expectedStatusCode, expectedErrorMessage, test);
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
