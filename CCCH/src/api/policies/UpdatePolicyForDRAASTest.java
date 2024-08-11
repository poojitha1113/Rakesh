package api.policies;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
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

import Constants.AuditCodeConstants;
import Constants.ConnectionStatus;
import Constants.DestinationStatus;
import Constants.DestinationType;
import Constants.ErrorCode;
import Constants.OSMajor;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import Constants.volume_type;
import InvokerServer.GatewayServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGCloudRPSServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGHypervisorsServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import invoker.SiteTestHelper.siteType;
import invoker.UserSpogInvoker;
import io.restassured.response.Response;

public class UpdatePolicyForDRAASTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private SPOGDestinationServer spogDestinationServer;
	private Policy4SPOGServer policy4SPOGServer;
	private SPOGCloudRPSServer spogcloudRPSServer;
	private SPOGHypervisorsServer spogHypervisorsServer;
	private UserSpogServer userSpogServer;
	private UserSpogInvoker  userSpogInvoker;
	//public int Nooftest;
	//private ExtentReports rep;
	private ExtentTest test;
	
	
	private String direct_org_email = "directprasadtest@gmail.com";
	private String msp_org_email = "rishis.msp@gmail.com";
	private String sub_org_email = "rishis.msp+parasaracustadmin@gmail.com";
	private String direct_user_password = "Test@2020";
	private String msp_user_password = "Spog@2020";
	private String suborg_user_password = "Spog@2020";
	
	private String csrAdminUserName;
	private String csrAdminPassword;
	private String csr_token;
	

	private String direct_user_validToken;
	private String direct_site_token;

	private String msp_user_validToken;
	private String msp_site_token;



	private String sub_org_user_validToken;
	private String suborg_site_token;
	

	
	private String site_version="1.0.0";
	private String gateway_hostname="Prasad";
	//used for test case count like passed,failed,remaining cases
	//private SQLServerDb bqdb1;
	//public int Nooftest;
	int passedcases=0;
	int failedcases=0;
	int skippedcases=0;
	//long creationTime;
	String buildnumber=null;
	String BQame=null;
	//private testcasescount count1;
	
	
	String direct_organization_id="dbcb4ed1-6a1b-4b1b-9b6e-a6c01c94aa9b";
	String msp_organization_id = "65a90488-f2e6-403f-9163-776f755d3038";
	String sub_org_id = "df384eb3-4e73-4b17-b77e-ccc5083d9ae3";
	String direct_source_id = "d8c50884-b4b3-447e-b902-c748ee98df3c";
	String direct_destination_id = "51fe36f0-2156-44d7-9f3a-c6895cd46770";
	String sub_org_source_id = "c921240e-c26d-42a2-bba3-0ab34b231792";
	String sub_org_destination_id = "6a1a8031-7caa-4d10-b200-d0edbfeb3987";
	

	
	String csr_user_id;
	String direct_user_id;
	String msp_user_id;
	String suborg_user_id;
	
	String direct_site_id;
	String msp_site_id;
	String suborg_site_id;
	
	String task_Id = null;
	String schedule_Id = null;
	String throttle_Id = null;
	String validToken;
	String datastore_id = null;
	
	String cloud_account_id;
	String cloud_account_token;
	String msp_cloud_account_id;
	String msp_cloud_account_token;
	
	String postfix = RandomStringUtils.randomNumeric(8);
	
	String orderID = "SKUTESTDATA_10_8_0_1_";
	String fulfillmentID = "SKUTESTDATA_10_8_0_1_";

	
//	private String runningMachine;
//	private String buildVersion;
	
	private String  org_model_prefix=this.getClass().getSimpleName();
		
	@BeforeClass
	@Parameters({ "baseURI", "port","csrAdminUserName", "csrAdminPassword","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword, String logFolder,String runningMachine,String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		policy4SPOGServer= new Policy4SPOGServer(baseURI, port);
		spogHypervisorsServer = new SPOGHypervisorsServer(baseURI,port);
		spogcloudRPSServer = new SPOGCloudRPSServer(baseURI,port);
		userSpogServer = new UserSpogServer(baseURI,port);
		rep = ExtentManager.getInstance("UpdatePolicyForDRAASTest", logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Prasad.Deverakonda";
		
		Nooftest=0;
		this.csrAdminUserName = adminUserName;
		this.csrAdminPassword = adminPassword;
		//Used for creating a build number with dateformat
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date=new java.util.Date();
		this.buildVersion=buildVersion+"_"+dateFormater.format(date);
		
		this.BQName=this.getClass().getSimpleName();
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

		
		
				test.log(LogStatus.INFO, "Logging with csrAdmin");
				spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
				csr_token = spogServer.getJWTToken();
		
				test.log(LogStatus.INFO, "Get the logged in user id ");
				csr_user_id = spogServer.GetLoggedinUser_UserID();
				test.log(LogStatus.INFO, "The csr user id is "+csr_user_id);
		
				String prefix = RandomStringUtils.randomAlphanumeric(8);
		
					
				spogServer.userLogin(this.direct_org_email, direct_user_password);
				test.log(LogStatus.INFO,"Getting the JWTToken for the Logged in user");
				direct_user_validToken = spogServer.getJWTToken();
				//direct_user_validToken=validToken;
				
				Response response=spogServer.getCloudAccounts(direct_user_validToken, "", test);
				cloud_account_id=response.then().extract().path("data["+0+"].cloud_account_id");
				
				
				test.log(LogStatus.INFO,"The token is :"+ direct_user_validToken );
		
				test.log(LogStatus.INFO, "Get the logged in user id ");
				direct_user_id = spogServer.GetLoggedinUser_UserID();
				test.log(LogStatus.INFO, "The direct org user id is "+direct_user_id);
	 
				//Setting csr toekn to MSP
				prefix = RandomStringUtils.randomAlphanumeric(8);
				spogServer.setToken(csr_token);
				
				spogServer.userLogin(this.msp_org_email, msp_user_password);
				test.log(LogStatus.INFO,"Getting the JWTToken for the Logged in user");
				msp_user_validToken = spogServer.getJWTToken();
				//msp_user_validToken=validToken;
				test.log(LogStatus.INFO,"The token is :"+ msp_user_validToken );
		
				test.log(LogStatus.INFO, "Get the logged in user id ");
				msp_user_id = spogServer.GetLoggedinUser_UserID();
				test.log(LogStatus.INFO, "The MSP org user id is "+msp_user_id);
	 
				response=spogServer.getCloudAccounts(msp_user_validToken, "", test);
				msp_cloud_account_id=response.then().extract().path("data["+0+"].cloud_account_id");

		
				
				test.log(LogStatus.INFO, "Login in to sub org A");
				spogServer.userLogin(this.sub_org_email, suborg_user_password);
				test.log(LogStatus.INFO,"Getting the JWTToken for the Logged in user");
				sub_org_user_validToken = spogServer.getJWTToken();
				
				test.log(LogStatus.INFO, "Create a site/register/login to the site");
				//msp_site_id = gatewayServer.createsite_register_login(msp_organization_id, msp_user_validToken, msp_user_id, "ts", "1.0.0", spogServer, test);
				response = spogServer.createSite(spogServer.ReturnRandom("TestSubOrg"), "cloud_direct", sub_org_id, sub_org_user_validToken, test);
				suborg_site_id = response.then().extract().path("data.site_id");

	}
	

	
	@DataProvider(name = "updatepolicybyId")
	public final Object[][] updatepolicybyId() {
		return new Object[][] {

			//DRAAS
			
			{"Policy Update for Direct Organization for DRAAS",direct_organization_id,this.direct_user_validToken, cloud_account_id,direct_source_id, direct_destination_id,"cloud_direct_image_backup","cloud_direct_draas"},
			{"Policy Update for Sub Organization for DRAAS",sub_org_id, this.msp_user_validToken,suborg_site_id, sub_org_source_id, sub_org_destination_id,"cloud_direct_image_backup","cloud_direct_draas"},
			

		};
	}
	
	@Test(dataProvider = "updatepolicybyId")
	public void updatepolicybypolicyid(String organizationType,
									 String organization_id,
									 String validToken,
									 String cloud_account_id,
									 String source_id,
									 String destination_id,
									 String task_type,
									 String policy_type
									 ) {
	
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);
		
		String policy_id = null;
		String schedule_id=spogServer.returnRandomUUID();
		String task_id=spogServer.returnRandomUUID();
		String throttle_id=spogServer.returnRandomUUID();
		String throttle_type="network";
		String policy_name=spogServer.ReturnRandom("test");
		String policy_description=spogServer.ReturnRandom("description");
		//String policy_id = spogServer.returnRandomUUID();
		String destination_name = spogServer.ReturnRandom("Prasad")+"_";
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		HashMap<String, Object> scheduleSettingDTO = new HashMap<>();
		ArrayList<HashMap<String,Object>>  destinations = new ArrayList<>();
		ArrayList<HashMap<String,Object>> schedules = new ArrayList<>();

		spogServer.setToken(validToken);
		
			test.log(LogStatus.INFO, "Logging with csrAdmin");
			spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
			csr_token = spogServer.getJWTToken();

			HashMap<String, Object> CustomScheduleDTO=policy4SPOGServer.createCustomScheduleDTO("1524469596000", "full","1","true","10","minutes",test);
			scheduleSettingDTO=policy4SPOGServer.createScheduleSettingDTO( null,CustomScheduleDTO,test);
			schedules =policy4SPOGServer.createPolicyScheduleDTO(null,spogServer.returnRandomUUID(), 
					  "custom", task_id, destination_id, scheduleSettingDTO, "06:00", "12:00", test);
		
			test.log(LogStatus.INFO, "Create cloud direct schedule");
			
			HashMap<String, Object> cloudDirectScheduleDTO=policy4SPOGServer.createCloudDirectScheduleDTO("0 6 * * *", test);
			
			test.log(LogStatus.INFO, "Create schedule settings");
			scheduleSettingDTO=policy4SPOGServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null,test);
			schedules =policy4SPOGServer.createPolicyScheduleDTO(null,schedule_id, 
					  "1d", task_id, destination_id, scheduleSettingDTO, "06:00", "12:00", task_type ,destination_name,test);
		
		test.log(LogStatus.INFO, "Exclude the folders from cloud direct file backup");  
		
		HashMap<String, Object> cloudDirectLocalBackupDTO=policy4SPOGServer.createCloudDirectLocalBackupDTO("d:\\tmp", "true", test);
		
		ArrayList<String> drivers = new ArrayList<>();
		drivers.add("C");
		
		HashMap<String, Object> cloudDirectimageBackupTaskInfoDTO = policy4SPOGServer.createCloudDirectImageBackupTaskInfoDTO(drivers, cloudDirectLocalBackupDTO, test);

	
		destinations= policy4SPOGServer.createPolicyTaskDTO(null, task_id, task_type, destination_id, "none", cloudDirectimageBackupTaskInfoDTO, null, null, test);

		
	

		test.log(LogStatus.INFO, "Create network throttle ");
		
		ArrayList<HashMap<String,Object>> throttles =policy4SPOGServer.createPolicyThrottleDTO(null, throttle_id, task_id, throttle_type, "1200", "1", "06:00", "18:00", task_type,destination_id,destination_name,test);
		
		test.log(LogStatus.INFO, "Create a policy of type backup_recovery and task of type "+task_type);
		
/*		userSpogServer.setToken(validToken);
			orderID = orderID + postfix;
			fulfillmentID = fulfillmentID + postfix;
	
			userSpogServer.addOrderByOrgId(organization_id,orderID,fulfillmentID, test);*/
		
		policy4SPOGServer.setToken(csr_token);
		
		//source_id = "92fd813c-cb91-4b13-8a45-b0e16de10039";
		
		Response response=policy4SPOGServer.createPolicy(policy_name, policy_description, policy_type, null, "true", source_id, destinations, schedules, throttles, policy_id, organization_id, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		policy_id = response.then().extract().path("data.policy_id");
		//sleep for 20 seconds
		sleepTime(20000);
		response = policy4SPOGServer.getPolicyById(validToken, policy_id, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		
		//response.then().body("data.policy_status", equalTo("failure"));
		response.then().body("data.policy_status", not("deploying"));
		test.log(LogStatus.INFO, "Update the policy by policy name");
		policy_name = spogServer.ReturnRandom("test1");
		
		response = policy4SPOGServer.updatePolicy(policy_name, policy_description, policy_type, null, "true", source_id, destinations, schedules, throttles, policy_id, organization_id, validToken,test);
		policy4SPOGServer.checkPolicyCommon(response, SpogConstants.SUCCESS_GET_PUT_DELETE, policy_name, policy_description, policy_type, null, "true", "success", source_id, policy_id, organization_id, test);
		policy4SPOGServer.checkPolicySchedules(response,SpogConstants.SUCCESS_GET_PUT_DELETE,schedules,test);
		
		//sleep for 20 seconds
		sleepTime(20000);
		
		policy4SPOGServer.deletePolicybyPolicyId(csr_token, policy_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
	//	spogDestinationServer.recycleCloudVolumesAndDelOrg(organization_id, test);

	}	
	
	@AfterMethod
	public void getResult(ITestResult result){
		if(result.getStatus() == ITestResult.FAILURE){
			count1.setfailedcount();
			test.log(LogStatus.FAIL, "Test Case Failed is "+result.getName()+" with parameters as "+Arrays.asList(result.getParameters()) );
			test.log(LogStatus.FAIL, result.getThrowable().getMessage());

		}else if(result.getStatus() == ITestResult.SKIP){
			test.log(LogStatus.SKIP, "Test Case Skipped is "+result.getName());
			count1.setskippedcount();
		}else if(result.getStatus()==ITestResult.SUCCESS){
			count1.setpassedcount();

		}
		rep.endTest(test);
	}

	@AfterClass
	public void updatebd() {
		
		
		test.log(LogStatus.INFO, "Delete the organizations");
		spogDestinationServer.setToken(csr_token);
		
		
		
		try {
			if(count1.getfailedcount()>0) {
				Nooftest=count1.getpassedcount()+count1.getfailedcount()+count1.getskippedcount();
				bqdb1.updateTable(BQName, runningMachine, this.buildVersion, String.valueOf(Nooftest), Integer.toString(count1.getpassedcount()), Integer.toString(count1.getfailedcount()), String.valueOf(count1.getskippedcount()), count1.getcreationtime(), "Failed");
			}else {
				Nooftest=count1.getpassedcount()+count1.getfailedcount()+count1.getskippedcount();
				bqdb1.updateTable(BQName, runningMachine, this.buildVersion, String.valueOf(Nooftest), Integer.toString(count1.getpassedcount()), Integer.toString(count1.getfailedcount()), String.valueOf(count1.getskippedcount()), count1.getcreationtime(), "Passed");
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public void sleepTime(int n) {

		try {
			Thread.sleep(n);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	

}
