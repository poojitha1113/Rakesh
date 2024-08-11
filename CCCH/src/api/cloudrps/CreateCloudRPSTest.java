package api.cloudrps;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.xml.bind.util.ValidationEventCollector;

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
import InvokerServer.SPOGCloudRPSServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import InvokerServer.GatewayServer.siteType;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

/** This is to test whether CSR user can register(add/create) the RPS to CC or not
 * 
 * Prerequisites:
 * 1. Csr token which is used to create all organizations, since he is the super user
 * 2. Direct, msp and sub organiztions and users under it
 * 3. UDP RPS installed node 
 * 4. UDP gateway installed and should be registered with site of CC
 * 5. Valid datacenter id and name
 * 
 * @author Rakesh.Chalamala
 * @sprint 13
 */

public class CreateCloudRPSTest extends base.prepare.Is4Org {
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
	
	private String  org_model_prefix=this.getClass().getSimpleName();
	private String datacenter_name=null;
	private String datacenter_id=null;
	private TestOrgInfo ti;
	private String csr_server_type;
	private String csr_user_name;
	
	@BeforeClass
	@Parameters({ "baseURI", "port","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String logFolder,String runningMachine,String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		spogcloudRPSServer = new SPOGCloudRPSServer(baseURI, port);
		rep = ExtentManager.getInstance("CreateCloudRPS", logFolder);
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

		//reading the RPS node info from properties file
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
			datacenter_id=prop.getProperty("datacenter_id");
			datacenter_name=prop.getProperty("datacenter_name");
			csr_server_type=prop.getProperty("csr_server_type");
		}catch (IOException e) {
			e.printStackTrace();
			test.log(LogStatus.INFO, "Failed to load RPS properties file");
		}
		spogServer.setToken(ti.csr_token);
		csr_user_name = spogServer.GetLoggedinUser_UserName();
	}
	
	@DataProvider(name = "postcloudRPS")
	public final Object[][] postcloudRPS() {
		return new Object[][] {
			{"Create Cloud RPS with csr token and valid input",ti.csr_org_id,ti.csr_org_name,ti.csr_admin_user_id,csr_user_name,ti.csr_token,cloud_rps_name,cloud_rps_port,cloud_rps_protocol,cloud_rps_username, cloud_rps_password, csr_site_id,csr_site_name,datacenter_id,datacenter_name},
			
			/*// POST RPS WITH NO DATACENTER ID
			{"csr",ti.csr_org_id,ti.csr_token,cloud_rps_name,cloud_rps_port,cloud_rps_protocol,cloud_rps_username, cloud_rps_password, csr_site_id,null},
			{"csr",ti.csr_org_id,ti.csr_token,cloud_rps_name,cloud_rps_port,cloud_rps_protocol,cloud_rps_username, cloud_rps_password, csr_site_id,""},*/
			
		};
	}
	
	
	@Test(dataProvider = "postcloudRPS")
	public void createcloudRPS(String caseType,
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
			   String datacenter_name
			   ) {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+caseType);
		test.log(LogStatus.INFO, caseType);
		Response response = spogcloudRPSServer.createCloudRPS(server_name, server_port, server_protocol, server_username, server_password, organization_id, site_id, datacenter_id, validToken);
		HashMap<String,Object> compose_cloudRPS_Info = spogcloudRPSServer.composeCloudRPSInfo(server_name, server_port, server_protocol, server_username, server_password, organization_id,organization_name, site_id,site_name, datacenter_id,datacenter_name,user_id,user_name);
		
		test.log(LogStatus.INFO, "Perform the response validation");
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		String cloudRPS_id = spogcloudRPSServer.verifyCloudRPS(response, compose_cloudRPS_Info, test);
		
		// Code to create a duplicate and handle the error
		response = spogcloudRPSServer.createCloudRPS(server_name, server_port, server_protocol, server_username, server_password, organization_id, site_id, datacenter_id, validToken);
		compose_cloudRPS_Info = spogcloudRPSServer.composeCloudRPSInfo(server_name, server_port, server_protocol, server_username, server_password, organization_id,organization_name, site_id,site_name, datacenter_id,datacenter_name,user_id,user_name);
				
		test.log(LogStatus.INFO, "Perform the response validation");
		spogcloudRPSServer.validateCloudRPSResponse(response,SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.RECOVERY_POINT_SERVER_ALREADY_EXISTS,server_name,test);
				
		test.log(LogStatus.INFO, "Delete the cloud rps server");
		spogcloudRPSServer.deleteCloudRPS(cloudRPS_id, validToken, SpogConstants.SUCCESS_GET_PUT_DELETE,null, test);
				
	}
	
	
	@DataProvider(name = "postcloudRPS_invalid")
	public final Object[][] postcloudRPS_invalid() {
		return new Object[][] {
			//400 cases
			{"Create cloud RPS with invalid server_username",
				ti.csr_org_id,ti.csr_token,cloud_rps_name,cloud_rps_port,cloud_rps_protocol,"a", cloud_rps_password, csr_site_id,datacenters[0],"server_username",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.CLOUD_RPS_SERVER_LENGTH_NOT_CORRECT},
			{"Create cloud RPS with invalid server_password",
					ti.csr_org_id,ti.csr_token,cloud_rps_name,cloud_rps_port,cloud_rps_protocol,cloud_rps_username, "", csr_site_id,datacenters[0],"server_password",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.CLOUD_RPS_SERVER_LENGTH_NOT_CORRECT},
			{"Create cloud RPS with invalid server_name",
					ti.csr_org_id,ti.csr_token,"ac",cloud_rps_port,cloud_rps_protocol,cloud_rps_username, cloud_rps_password, csr_site_id,datacenters[0],"server_name",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.CLOUD_RPS_SERVER_LENGTH_NOT_CORRECT},
			{"Create cloud RPS with invalid server_port",
					ti.csr_org_id,ti.csr_token,cloud_rps_name,"",cloud_rps_protocol,cloud_rps_username, cloud_rps_password, csr_site_id,datacenters[0],"server_port",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.CLOUD_RPS_SERVER_LENGTH_NOT_CORRECT},
			{"Create cloud RPS with invalid server_protocol",
					ti.csr_org_id,ti.csr_token,cloud_rps_name,cloud_rps_port,"",cloud_rps_username, cloud_rps_password, csr_site_id,datacenters[0],"server_protocol",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.CLOUD_RPS_SERVER_PROTOCOL_CAN_NOT_BE_BLANK},
			
			{"Create cloud RPS with invalid site_id",
					ti.csr_org_id,ti.csr_token,cloud_rps_name,cloud_rps_port,cloud_rps_protocol,cloud_rps_username, cloud_rps_password, "123",datacenters[0],"site_id",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_ELEMENT_IS_NOT_A_UUID},
			{"Create cloud RPS with invalid organization_id",
					"123",ti.csr_token,cloud_rps_name,cloud_rps_port,cloud_rps_protocol,cloud_rps_username, cloud_rps_password, csr_site_id,datacenters[0],"organization_id",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_ELEMENT_IS_NOT_A_UUID},
			{"Create cloud RPS with invalid site_id",
					ti.csr_org_id,ti.csr_token,cloud_rps_name,cloud_rps_port,cloud_rps_protocol,cloud_rps_username, cloud_rps_password, "12123",datacenters[0],"site_id",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_ELEMENT_IS_NOT_A_UUID},
			
			//401 cases
			{"Create cloud RPS with invaildtoken ",
					ti.csr_org_id,"invalidtoken",cloud_rps_name,cloud_rps_port,cloud_rps_protocol,cloud_rps_username, cloud_rps_password, csr_site_id,datacenters[0],"invaildtoken",SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT},
			{"Create cloud RPS with missing token ",
					ti.csr_org_id,"",cloud_rps_name,cloud_rps_port,cloud_rps_protocol,cloud_rps_username, cloud_rps_password, csr_site_id,datacenters[0],"missing token",SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTHENTICATION_FAILED},
			
			//403 cases
			{"Create cloud RPS with .direct_org1_user1_token ",
					ti.csr_org_id,ti.direct_org1_user1_token,cloud_rps_name,cloud_rps_port,cloud_rps_protocol,cloud_rps_username, cloud_rps_password, csr_site_id,datacenters[0],"ti.direct_org1_user1_token",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Create cloud RPS with root_msp_org1_user1_token ",
					ti.csr_org_id,ti.root_msp_org1_user1_token,cloud_rps_name,cloud_rps_port,cloud_rps_protocol,cloud_rps_username, cloud_rps_password, csr_site_id,datacenters[0],"ti.root_msp_org1_user1_token",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Create cloud RPS with root_msp1_suborg1_user1_token ",
					ti.csr_org_id,ti.root_msp1_suborg1_user1_token,cloud_rps_name,cloud_rps_port,cloud_rps_protocol,cloud_rps_username, cloud_rps_password, csr_site_id,datacenters[0],"ti.root_msp1_suborg1_user1_token",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Create cloud RPS with root_msp_org1_msp_accountadmin1_token ",
					ti.csr_org_id,ti.root_msp_org1_msp_accountadmin1_token,cloud_rps_name,cloud_rps_port,cloud_rps_protocol,cloud_rps_username, cloud_rps_password, csr_site_id,datacenters[0],"ti.root_msp_org1_msp_accountadmin1_token",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Create cloud RPS with sub msp user token",
						ti.csr_org_id,ti.root_msp1_submsp1_user1_token,cloud_rps_name,cloud_rps_port,cloud_rps_protocol,cloud_rps_username, cloud_rps_password, csr_site_id,datacenters[0],"ti.root_msp_org1_user1_token",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Create cloud RPS with sub msp sub org user token",
						ti.csr_org_id,ti.msp1_submsp1_suborg1_user1_token,cloud_rps_name,cloud_rps_port,cloud_rps_protocol,cloud_rps_username, cloud_rps_password, csr_site_id,datacenters[0],"ti.root_msp1_suborg1_user1_token",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Create cloud RPS with sub msp account admin token",
						ti.csr_org_id,ti.root_msp1_submsp1_account_admin_token,cloud_rps_name,cloud_rps_port,cloud_rps_protocol,cloud_rps_username, cloud_rps_password, csr_site_id,datacenters[0],"ti.root_msp_org1_msp_accountadmin1_token",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},			
			{"Create cloud RPS with csr_readonly_token ",
					ti.csr_org_id,ti.csr_readonly_token,cloud_rps_name,cloud_rps_port,cloud_rps_protocol,cloud_rps_username, cloud_rps_password, csr_site_id,datacenters[0],"ti.csr_readonly_token",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
		};
	}
	
	@Test(dataProvider = "postcloudRPS_invalid",enabled=true)
	public void createcloudRPS_invalid(String caseType,
								   String organization_id,
								   String validToken,
								   String server_name,
								   String server_port,
								   String server_protocol,
								   String server_username,
								   String server_password,
								   String site_id,
								   String datacenter_id,
								   String to_replace,
								   int expectedStatusCode,
								   SpogMessageCode expectedErrorMessage
								   ) {
		
		//to_replace is the keyword to identify the invalid parameter of the dataprovider to validate invalid cases.
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());

		String cloudRPS_id = null;

		test.log(LogStatus.INFO, caseType);
		Response response = spogcloudRPSServer.createCloudRPS(server_name, server_port, server_protocol, server_username, server_password, organization_id, site_id, datacenter_id, validToken);
		
		test.log(LogStatus.INFO, "Perform the response validation");
		spogcloudRPSServer.validateCloudRPSResponse(response, expectedStatusCode,expectedErrorMessage,to_replace,test);
		
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
}
