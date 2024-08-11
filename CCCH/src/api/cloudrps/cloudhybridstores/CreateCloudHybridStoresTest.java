package api.cloudrps.cloudhybridstores;

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

/** This is to test the Cloud hybrid destination creation for an organization
 * 
 *  On calling the api POST: /cloudhybridstores
 *  
 *  A destination of type cloud hybrid store will be created for specified organization
 *  A datastore on will be created on specified RPS - a dedicated volume will be created with directories
 *  datastore will be assigned to the destination
 *  A default policy will be created for the organization
 *  
 * Prerequisites:
 * 1. Csr token which is used to create all organizations, since he is the super user
 * 2. Direct, msp and sub organizations and users under it
 * 3. RPS registered with CC - to create datastore we need an RPS registered
 *
 * RPS configuration:
 *  - 2 dynamic disks on the machine
 *  - Free space on disk one so that volumes will be created on calling of datastore api
 *  - 2nd disk should be Dynamic and GPT
 *  - E: volume on 2nd disk where HASH directories will be created
 * 
 * For more details on how to register RPS visit CreateCloudRPSTest
 * 
 * @author Rakesh.Chalamala
 * @sprint 28
 */
public class CreateCloudHybridStoresTest extends base.prepare.Is4Org {
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
				       e.printStackTrace();
			    } 
			    catch (IOException e){
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
	
		
	@DataProvider(name = "cloudHybridDatastoreInfo")
	public final Object[][] cloudHybridDatastoreInfo() {
		return new Object[][] {
			//200
			{"Create cloud hybrid datastore with csr token and valid request payload",
				"direct organization", ti.csr_token,spogServer.ReturnRandom("dest"),cloudRPS_id, ti.direct_org1_id, "F", datacenters[0],  
				"0.001", SpogConstants.SUCCESS_POST, null},
			{"Create cloud hybrid datastore with csr token and valid request payload and no volume letter",
					"direct organization", ti.csr_token,spogServer.ReturnRandom("dest"),cloudRPS_id, ti.direct_org1_id, "", datacenters[0],  
					"0.001", SpogConstants.SUCCESS_POST, null},
			{"Create cloud hybrid datastore with csr token and valid request payload for sub organization",
					"sub organization", ti.csr_token,spogServer.ReturnRandom("dest"),cloudRPS_id, ti.root_msp1_suborg1_id, "F", datacenters[0],  
					"0.001", SpogConstants.SUCCESS_POST, null},
			{"Create cloud hybrid datastore with csr token and valid request payload and no volume letter for sub organization",
					"sub organization", ti.csr_token,spogServer.ReturnRandom("dest"),cloudRPS_id, ti.root_msp1_suborg1_id, "", datacenters[0],  
					"0.001", SpogConstants.SUCCESS_POST, null},
			
			//destination_name
			{"Create cloud hybrid datastore with empty string as destination_name",
						"direct organization", ti.csr_token, "",cloudRPS_id, ti.direct_org1_id, "F", datacenters[0],  
						"0.001", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.CAN_NOT_BE_BLANK},
			{"Create cloud hybrid datastore with null as destination_name",
							"direct organization", ti.csr_token, null,cloudRPS_id, ti.direct_org1_id, "F", datacenters[0],  
							"0.01", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.CAN_NOT_BE_BLANK},
			
			//volume
			{"Create cloud hybrid datastore with lower case driver letter",
						"direct organization", ti.csr_token,spogServer.ReturnRandom("dest"),cloudRPS_id, ti.direct_org1_id, "f", datacenters[0],  
						"0.001", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.INCORRECT_DRIVER_LETTER_FORMAT},
			{"Create cloud hybrid datastore with upper case driver letters",
							"direct organization", ti.csr_token,spogServer.ReturnRandom("dest"),cloudRPS_id, ti.direct_org1_id, "FF", datacenters[0],  
							"0.001", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.INCORRECT_DRIVER_LETTER_FORMAT},
			{"Create cloud hybrid datastore with existing driver letter",
								"direct organization", ti.csr_token,spogServer.ReturnRandom("dest"),cloudRPS_id, ti.direct_org1_id, "C", datacenters[0],  
								"0.001", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.DRIVER_LETTER_IS_IN_USE},
			
			//capacity
			{"Create cloud hybrid datastore with capacity as null",
					"direct organization", ti.csr_token,spogServer.ReturnRandom("dest"),cloudRPS_id, ti.direct_org1_id, "", datacenters[0],  
					null, SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.CAN_NOT_BE_BLANK},
			{"Create cloud hybrid datastore with capacity as empty string",
					"direct organization", ti.csr_token,spogServer.ReturnRandom("dest"),cloudRPS_id, ti.direct_org1_id, "", datacenters[0],  
					"", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.CAN_NOT_BE_BLANK},
			{"Create cloud hybrid datastore with capacity as 0",
					"direct organization", ti.csr_token,spogServer.ReturnRandom("dest"),cloudRPS_id, ti.direct_org1_id, "", datacenters[0],  
					"0", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.NUMBER_SHOULD_BE_POSITIVE},
			{"Create cloud hybrid datastore with capacity as negative value",
					"direct organization", ti.csr_token,spogServer.ReturnRandom("dest"),cloudRPS_id, ti.direct_org1_id, "", datacenters[0],  
					"-1", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.NUMBER_SHOULD_BE_POSITIVE},
			
			//organization_id
			{"Create cloud hybrid datastore with organization_id as empty string",
					"direct organization", ti.csr_token,spogServer.ReturnRandom("dest"),cloudRPS_id, "", "", datacenters[0],  
					"0.01", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.CAN_NOT_BE_BLANK},
			{"Create cloud hybrid datastore with organization_id as null",
					"direct organization", ti.csr_token,spogServer.ReturnRandom("dest"),cloudRPS_id, null, "", datacenters[0],  
					"0.01", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.CAN_NOT_BE_BLANK},
			{"Create cloud hybrid datastore with invalid organization_id",
					"direct organization", ti.csr_token,spogServer.ReturnRandom("dest"),cloudRPS_id, "invalid", "", datacenters[0],  
					"0.01", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID},
			{"Create cloud hybrid datastore with organization_id that does not exist",
					"direct organization", ti.csr_token,spogServer.ReturnRandom("dest"),cloudRPS_id, UUID.randomUUID().toString(), "", datacenters[0],  
					"0.01", SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED},
			
			//datacenter_id
			{"Create cloud hybrid datastore with datacenter_id as empty string",
					"direct organization", ti.csr_token,spogServer.ReturnRandom("dest"),cloudRPS_id, ti.direct_org1_id, "", "",  
					"0.01", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.CAN_NOT_BE_BLANK},
			{"Create cloud hybrid datastore with datacenter_id as null",
					"direct organization", ti.csr_token,spogServer.ReturnRandom("dest"),cloudRPS_id, ti.direct_org1_id, "", null,  
					"0.01", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.CAN_NOT_BE_BLANK},
			{"Create cloud hybrid datastore with invalid datacenter_id",
					"direct organization", ti.csr_token,spogServer.ReturnRandom("dest"),cloudRPS_id, ti.direct_org1_id, "", "invalid",  
					"0.01", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID},
			{"Create cloud hybrid datastore with datacenter_id that does not exist",
					"direct organization", ti.csr_token,spogServer.ReturnRandom("dest"),cloudRPS_id, ti.direct_org1_id, "", UUID.randomUUID().toString(),  
					"0.01", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.DATACENTER_NOT_FOUND},
			
			//rps_server_id
			{"Create cloud hybrid datastore with rps_server_id as empty string",
						"direct organization", ti.csr_token,spogServer.ReturnRandom("dest"), "", ti.direct_org1_id, "", datacenters[0],  
						"0.01", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.CAN_NOT_BE_BLANK},
			{"Create cloud hybrid datastore with rps_server_id as null",
						"direct organization", ti.csr_token,spogServer.ReturnRandom("dest"), null, ti.direct_org1_id, "", datacenters[0],  
						"0.01", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.CAN_NOT_BE_BLANK},
			{"Create cloud hybrid datastore with invalid rps_server_id",
						"direct organization", ti.csr_token,spogServer.ReturnRandom("dest"), "invalid", ti.direct_org1_id, "", datacenters[0],  
						"0.01", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID},
			{"Create cloud hybrid datastore with rps_server_id that does not exist",
						"direct organization", ti.csr_token,spogServer.ReturnRandom("dest"),UUID.randomUUID().toString(), ti.direct_org1_id, "", datacenters[0],  
						"0.01", SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.RECOVERY_POINT_SERVER_WITH_ID_DOESNOT_EXIST},
			
			//401
			{"Create cloud hybrid datastore with empty string as token",
					"direct organization", "",spogServer.ReturnRandom("dest"),cloudRPS_id, ti.direct_org1_id, "F", datacenters[0],  
					"0.01", SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED},
			{"Create cloud hybrid datastore with null as token",
					"direct organization", null,spogServer.ReturnRandom("dest"),cloudRPS_id, ti.direct_org1_id, "F", datacenters[0],  
					"0.01", SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED},
			{"Create cloud hybrid datastore with invalid string as token",
					"direct organization", "invalidTOken",spogServer.ReturnRandom("dest"),cloudRPS_id, ti.direct_org1_id, "F", datacenters[0],  
					"0.01", SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT},
			
			//403
			{"Create cloud hybrid datastore with direct user token",
					"direct organization",ti.direct_org1_user1_token,spogServer.ReturnRandom("dest"),cloudRPS_id, ti.direct_org1_id, "F", datacenters[0],  
					"0.01", SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Create cloud hybrid datastore with msp user token",
					"direct organization", ti.root_msp_org1_user1_token,spogServer.ReturnRandom("dest"),cloudRPS_id, ti.direct_org1_id, "F", datacenters[0],  
					"0.01", SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Create cloud hybrid datastore with msp account admin token",
					"direct organization", ti.root_msp_org1_msp_accountadmin1_token,spogServer.ReturnRandom("dest"),cloudRPS_id, ti.direct_org1_id, "F", datacenters[0],  
					"0.01", SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Create cloud hybrid datastore with sub org user token",
					"direct organization", ti.root_msp1_suborg1_user1_token,spogServer.ReturnRandom("dest"),cloudRPS_id, ti.direct_org1_id, "F", datacenters[0],  
					"0.01", SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Create cloud hybrid datastore with sub msp user token",
						"direct organization", ti.root_msp1_submsp1_user1_token,spogServer.ReturnRandom("dest"),cloudRPS_id, ti.direct_org1_id, "F", datacenters[0],  
						"0.01", SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Create cloud hybrid datastore with sub msp account admin token",
						"direct organization", ti.root_msp1_submsp1_account_admin_token,spogServer.ReturnRandom("dest"),cloudRPS_id, ti.direct_org1_id, "F", datacenters[0],  
						"0.01", SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Create cloud hybrid datastore with sub msp sub org user token",
						"direct organization", ti.msp1_submsp1_suborg1_user1_token,spogServer.ReturnRandom("dest"),cloudRPS_id, ti.direct_org1_id, "F", datacenters[0],  
						"0.01", SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Create cloud hybrid datastore with csr read only user token",
					"direct organization", ti.csr_readonly_token,spogServer.ReturnRandom("dest"),cloudRPS_id, ti.direct_org1_id, "F", datacenters[0],  
					"0.01", SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
		};
	}
	/**
	 *  "destination_name": "sample_datastore1",
  		"rps_server_id": "87c768c7-1a2d-4dbe-8dce-768581e46020",
 		"organization_id":"5d1a5a55-7434-459d-a70e-1d089e472e60",
  		"volume": "H",
  		"datacenter_id": "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c",
    	"capacity": 0.01
	 */
	
	@Test(dataProvider = "cloudHybridDatastoreInfo")
	public void createCloudHybridStores(String caseType,
										String organizationType,
										String validToken,
										String destination_name,
										String rps_server_id,
										String organization_id,
										String volume,
										String datacenter_id,
										String capacity,
										int expectedStatusCode,
										SpogMessageCode expectedErrorMessage
										){
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		String hybridStore_id = null;
		
		test.log(LogStatus.INFO, caseType);
		
		test.log(LogStatus.INFO, "Compose Cloud hybrid store request payload");
		HashMap<String, Object> hybridStoreInfo = spogcloudRPSServer.composeCloudHybridStoreInfo(validToken, destination_name,rps_server_id, 
																									organization_id, volume, datacenter_id, capacity);
		
		hybridStore_id = spogcloudRPSServer.createCloudHybridStoreWithCheck(validToken, hybridStoreInfo, expectedStatusCode, expectedErrorMessage, test);
					
		if (expectedStatusCode == SpogConstants.SUCCESS_POST) {
			spogcloudRPSServer.destroyCloudHybridStoreById(validToken, hybridStore_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);	
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
