package api.cloudrps.datastores;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

import javax.print.DocFlavor.STRING;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.omg.CORBA.OBJ_ADAPTER;
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


/** This is to test whether the user can get all the datastores successfully or not
 *  Each datastore will be assigned to a customer
 *  On creation of datastore the hash,data,index directories will be created on datastore 
 *  Directories information will be part of response
 *  We call the api to get all the datastores
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

public class GetDatastores extends base.prepare.Is4Org {
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

	String cloud_rps_name = null;
	String cloud_rps_port = null;
	String cloud_rps_protocol = null;
	String cloud_rps_username = null;
	String cloud_rps_password = null;
	String csr_site_id = null;
	String csr_site_name = null;
	String datacenters[] = new String[5];
	String cloudRPS_id = null;
	String cloudRPS_name;
	ArrayList<HashMap<String, Object>> expectedDatastoreInfo = new ArrayList<>();
	ArrayList<String> datastore_ids = new ArrayList<>();
	
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
	
		test.log(LogStatus.INFO, "Create cloud RPS");
		Response response = spogcloudRPSServer.createCloudRPS(cloud_rps_name, cloud_rps_port, cloud_rps_protocol, cloud_rps_username, cloud_rps_password, ti.csr_org_id, csr_site_id, datacenters[0], ti.csr_token);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		cloudRPS_id = response.then().extract().path("data.server_id");
		cloudRPS_name = response.then().extract().path("data.server_name");	
	}
	
	
	@DataProvider(name = "postcloudRPSDatastores")
	public final Object[][] postcloudRPSDatastores() {
		return new Object[][] {
			{"csr",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,2048,false,"C:\\"+spogServer.ReturnRandom("destination\\data"),"administrator",ti.common_password,
				"C:\\"+spogServer.ReturnRandom("index"),"administrator",ti.common_password, "C:\\dest"+spogServer.ReturnRandom("hash"),"administrator",ti.common_password,4,102400,"C:\\"+spogServer.ReturnRandom("dest"),"administrator",ti.common_password,
				"standard",ti.common_password,false,"abc@arcserve.com","encryption_password"},
			{"csr",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,2048,false,"C:\\"+spogServer.ReturnRandom("destination\\data"),"administrator",ti.common_password,
					"C:\\"+spogServer.ReturnRandom("index"),"administrator",ti.common_password, "C:\\dest"+spogServer.ReturnRandom("hash"),"administrator",ti.common_password,4,102400,"C:\\"+spogServer.ReturnRandom("dest"),"administrator",ti.common_password,
				"standard","a",false,"abc@arcserve.com","block_size"},
			{"csr",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,2048,false,"C:\\"+spogServer.ReturnRandom("destination\\data"),"administrator",ti.common_password,
					"C:\\"+spogServer.ReturnRandom("index"),"administrator",ti.common_password, "C:\\dest"+spogServer.ReturnRandom("hash"),"administrator",ti.common_password,4,102400,"C:\\"+spogServer.ReturnRandom("dest"),"administrator",ti.common_password,
				"standard","abc",false,"abc@arcserve.com","compression_type"},
			{"csr",ti.csr_org_id,ti.csr_token,spogServer.ReturnRandom("datastore"),true,true,true, cloudRPS_id, 4,2048,false,"C:\\"+spogServer.ReturnRandom("destination\\data"),"administrator",ti.common_password,
					"C:\\"+spogServer.ReturnRandom("index"),"administrator",ti.common_password, "C:\\dest"+spogServer.ReturnRandom("hash"),"administrator",ti.common_password,4,102400,"C:\\"+spogServer.ReturnRandom("dest"),"administrator",ti.common_password,
				"standard","abc",false,"abc@arcserve.com","concurrent_active_nodes"},
		};
	}
	
	@Test(dataProvider = "postcloudRPSDatastores")
	public void postcloudRPSDatastores_200(String organizationType,
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
		String datastore_id = null;
		HashMap<String, Object> expectedData = new HashMap<>();
		test.log(LogStatus.INFO, "Start the  cloud RPS in organization "+organizationType);
		
		test.log(LogStatus.INFO, "Compose the hashmap for deduplication info "+organizationType);
		HashMap<String,Object> deduplicationInfo = spogcloudRPSServer.composededuplicationInfo(block_size, hash_memory,hash_on_ssd, data_destination_path, data_destination_username, data_destination_password, 
				index_destination_path, index_destination_username, index_destination_password, hash_destination_path, hash_destination_username, hash_destination_password);
		
		test.log(LogStatus.INFO, "Compose the hashmap for datastoreproperties info "+organizationType);
		HashMap<String,Object> datastorepropertiesInfo = spogcloudRPSServer.composedatastorepropertiesInfo(concurrent_active_nodes,capacity, datastore_path, datastore_username, datastore_password, compression, encryption_password, deduplicationInfo, send_email, to_email);
		
		test.log(LogStatus.INFO, "Create the cloud rps datastore "+organizationType);
		Response response = spogcloudRPSServer.createCloudRPSDataStore(datastore_name, dedupe_enabled, encryption_enabled, compression_enabled, rps_server_id, datastorepropertiesInfo, validToken);
		
		test.log(LogStatus.INFO, "Validate the cloud RPS data store reponse");
		datastore_id = spogcloudRPSServer.validateCloudRPSDataStoreResponse(response, datastore_name, dedupe_enabled, encryption_enabled, compression_enabled, rps_server_id,cloudRPS_name, datastorepropertiesInfo, SpogConstants.SUCCESS_POST, null, "", test);
		
		spogServer.setToken(validToken);
		expectedData.put("datastore_name", datastore_name);
		expectedData.put("dedupe_enabled", dedupe_enabled);
		expectedData.put("encryption_enabled", encryption_enabled);
		expectedData.put("compression_enabled", compression_enabled);
		expectedData.put("rps_server_id", rps_server_id);
		expectedData.put("server_name", cloudRPS_name);
		expectedData.put("datastore_id", datastore_id);
		expectedData.put("datastore_properties", datastorepropertiesInfo);
		expectedData.put("create_user_id", spogServer.GetLoggedinUser_UserID());
		
		/*HashMap<String, Object> actualData = new HashMap<>();
		actualData = response.then().extract().path("data");
		
		datastore_id = spogcloudRPSServer.checkForDataStoreData(expectedData, actualData, test);*/
		
		expectedDatastoreInfo.add(expectedData);
		datastore_ids.add(datastore_id);
		
	}	
	
	@DataProvider(name = "getcloudRPSdatastore")
	public Object[][] getcloudRPSdatastore(){
		return new Object[][] {
			{"no filter and no sort", ti.csr_token, null, null, 1, 20},
//			{"csr organization id as filter and sort on create_ts ascending order", ti.csr_token,"organization_id;=;"+ti.csr_org_id,"create_ts;asc",1,5},
			{"filter on rps_server_id and sort on create_ts ascending order", ti.csr_token,"rps_server_id;=;"+cloudRPS_id,"create_ts;asc",2,2},
			{"filter on datastore id and sort on create_ts ascending order", ti.csr_token,"datastore_id;=;"+datastore_ids.get(0),"create_ts;asc",1,5},
			{"filter on datastore_id 1 | 2 and sort on create_ts ascending order", ti.csr_token,"datastore_id;=;"+datastore_ids.get(0)+"|"+datastore_ids.get(2),"create_ts;asc",1,5},
			{"filter on organization_id and sort on create_ts descending order", ti.csr_token,"organization_id;=;"+ti.csr_org_id,"create_ts;desc",1,5},
			
			//csr readonly user token
			{"no filter and no sort & csr readonly user token", ti.csr_readonly_token, null, null, 1, 20},
//			{"csr organization id as filter and sort on create_ts ascending order & csr readonly user token", ti.csr_readonly_token,"organization_id;=;"+ti.csr_org_id,"create_ts;asc",1,5},
			{"filter on rps_server_id and sort on create_ts ascending order & csr readonly user token", ti.csr_readonly_token,"rps_server_id;=;"+cloudRPS_id,"create_ts;asc",2,2},
			{"filter on datastore id and sort on create_ts ascending order & csr readonly user token", ti.csr_readonly_token,"datastore_id;=;"+datastore_ids.get(0),"create_ts;asc",1,5},
			{"filter on datastore_id 1 | 2 and sort on create_ts ascending order & csr readonly user token", ti.csr_readonly_token,"datastore_id;=;"+datastore_ids.get(0)+"|"+datastore_ids.get(2),"create_ts;asc",1,5},
			{"filter on organization_id and sort on create_ts descending order & csr readonly user token", ti.csr_readonly_token,"organization_id;=;"+ti.csr_org_id,"create_ts;desc",1,5},
			
		};
	}
	
	@Test(dependsOnMethods= {"postcloudRPSDatastores_200"},dataProvider="getcloudRPSdatastore",enabled=true)
	public void getDatastores_200(String caseType, String validToken, String filterStr, String sortStr, int page, int page_size) {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		
		test.log(LogStatus.INFO, "Get the cloud RPS data stores with "+caseType);
		spogcloudRPSServer.getCloudRPSDataStoresWithCheck(validToken, expectedDatastoreInfo, filterStr, sortStr, page, page_size, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
	}
	
	@Test
	public void getDatastores_401() {
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		
		test.log(LogStatus.INFO, "Get the cloud RPS data stores with empty token");
		spogcloudRPSServer.getCloudRPSDataStoresWithCheck("", expectedDatastoreInfo, null, null, 1, 20, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);
		
		test.log(LogStatus.INFO, "Get the cloud RPS data stores with invalid token");
		spogcloudRPSServer.getCloudRPSDataStoresWithCheck("Invalid", expectedDatastoreInfo, null, null, 1, 20, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);
	}
	
	@DataProvider(name = "getcloudRPSdatastore_403")
	public Object[][] getcloudRPSdatastore_403(){
		return new Object[][] {
			{"Direct organization user token", ti.direct_org1_user1_token, null, null, 1, 20, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"MSP organization user token", ti.root_msp_org1_user1_token, null, null, 1, 20, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Sub organization user token", ti.root_msp1_suborg1_user1_token, null, null, 1, 20, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"MSP ACCOUNT ADMIN token", ti.root_msp_org1_msp_accountadmin1_token, null, null, 1, 20, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"SUB MSP organization user token", ti.root_msp1_submsp1_user1_token, null, null, 1, 20, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"SUB MSP Sub organization user token", ti.msp1_submsp1_suborg1_user1_token, null, null, 1, 20, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"SUB MSP ACCOUNT ADMIN token", ti.root_msp1_submsp1_account_admin_token, null, null, 1, 20, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
		};
	}
	@Test(dataProvider = "getcloudRPSdatastore_403")
	public void getDatastores_403(String caseType, String token, String filterStr, String sortStr, int page, int page_size,
									int errorCode, SpogMessageCode errorMessage) {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		
		test.log(LogStatus.INFO, "Get the cloud RPS data stores with "+caseType);
		spogcloudRPSServer.getCloudRPSDataStoresWithCheck(token, expectedDatastoreInfo, filterStr, sortStr, page, page_size, errorCode, errorMessage, test);
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
