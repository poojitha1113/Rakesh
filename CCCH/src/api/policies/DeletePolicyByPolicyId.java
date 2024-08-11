package api.policies;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

import Constants.ConnectionStatus;
import Constants.DestinationStatus;
import Constants.DestinationType;
import Constants.OSMajor;
import Constants.PolicyBackupType;
import Constants.PolicyType;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGHypervisorsServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class DeletePolicyByPolicyId extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private SPOGDestinationServer spogDestinationServer;
	private Policy4SPOGServer policy4SPOGServer;
	private SPOGHypervisorsServer spogHypervisorsServer;
	private UserSpogServer userSpogServer;
	//public int Nooftest;
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
	String direct_cloud_id;
	String msp_cloud_id;
	Response response;
	String cloudAccountSecret;
	String direct_destination_ID;
	String msp_destination_ID;
	String sub_orga_destination_ID;
	String submsp_sub_org_destination_ID;

	String direct_source_id;
	String msp_source_id;
	String sub_orga_source_id;
	String submsp_suborg_source_id;
	String prefix = RandomStringUtils.randomAlphanumeric(8);

	private String  org_model_prefix=this.getClass().getSimpleName();
	private ExtentTest test;
	private TestOrgInfo ti;
	private String direct_policy_ID;
	private String sub_orga_policy_ID;
	private String submsp_sub_orga_policy_ID;
		
	@BeforeClass
	@Parameters({ "baseURI", "port","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port,String logFolder,String runningMachine,String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		policy4SPOGServer= new Policy4SPOGServer(baseURI, port);
		spogHypervisorsServer = new SPOGHypervisorsServer(baseURI,port);
		userSpogServer = new UserSpogServer(baseURI, port);
		rep = ExtentManager.getInstance("DeletePolicyByPolicyId", logFolder);
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

		//Creating site for the organization direct
		spogDestinationServer.setToken(ti.csr_token);
		String[] datacenters = spogDestinationServer.getDestionationDatacenterID();

		//get cloud accounts
		response = spogServer.getCloudAccounts(ti.direct_org1_user1_token, "", test);
		direct_cloud_id = response.then().extract().path("data[0].cloud_account_id");

		response = spogServer.getCloudAccounts(ti.root_msp_org1_user1_token, "", test);
		msp_cloud_id = response.then().extract().path("data[0].cloud_account_id");

		//get default destinations
		response = spogDestinationServer.getDestinations(ti.direct_org1_user1_token, "destination_type="+DestinationType.cloud_direct_volume, test);
		direct_destination_ID = response.then().extract().path("data[0].destination_id");

		response = spogDestinationServer.getDestinations(ti.root_msp1_suborg1_user1_token, "destination_type="+DestinationType.cloud_direct_volume, test);
		sub_orga_destination_ID = response.then().extract().path("data[0].destination_id");

		response = spogDestinationServer.getDestinations(ti.msp1_submsp1_suborg1_user1_token, "destination_type="+DestinationType.cloud_direct_volume, test);
		submsp_sub_org_destination_ID = response.then().extract().path("data[0].destination_id");
		
		//get default policies
		response = policy4SPOGServer.getPolicies(ti.direct_org1_user1_token, null);
		direct_policy_ID = response.then().extract().path("data[0].policy_id");

		response = policy4SPOGServer.getPolicies(ti.root_msp1_suborg1_user1_token, null);
		sub_orga_policy_ID = response.then().extract().path("data[0].policy_id");

		response = policy4SPOGServer.getPolicies(ti.msp1_submsp1_suborg1_user1_token, null);
		submsp_sub_orga_policy_ID = response.then().extract().path("data[0].policy_id");

		//Create source in all organization
		spogServer.setToken(ti.direct_org1_user1_token);
		response = spogServer.createSource("sourceName1", SourceType.machine, SourceProduct.cloud_direct, ti.direct_org1_id, direct_cloud_id,ProtectionStatus.unprotect, ConnectionStatus.online,
								OSMajor.windows.name(), "sql;exchange", "Rak_vm", null, "Rak_agent1", "windows 2012",  "64", "1.0.0", "2.0", "http://upgrade", test);
		direct_source_id = response.then().extract().path("data.source_id");

		spogServer.setToken(ti.root_msp1_suborg1_user1_token);
		response = spogServer.createSource("sourceName3", SourceType.machine, SourceProduct.cloud_direct, ti.root_msp1_suborg1_id, msp_cloud_id,ProtectionStatus.unprotect, ConnectionStatus.online,
								OSMajor.windows.name(), "sql;exchange", "Rak_vm", null, "Rak_agent1","windows 2012",  "64", "1.0.0", "2.0", "http://upgrade", test);
		sub_orga_source_id = response.then().extract().path("data.source_id");
		
		spogServer.setToken(ti.msp1_submsp1_suborg1_user1_token);
		response = spogServer.createSource("sourceName3", SourceType.machine, SourceProduct.cloud_direct, ti.msp1_submsp1_sub_org1_id, msp_cloud_id,ProtectionStatus.unprotect, ConnectionStatus.online,
								OSMajor.windows.name(), "sql;exchange", "Rak_vm", null, "Rak_agent1","windows 2012",  "64", "1.0.0", "2.0", "http://upgrade", test);
		submsp_suborg_source_id = response.then().extract().path("data.source_id");

	}

	@DataProvider(name = "deletePolicybyId")
	public final Object[][] deletePolicybyId() {
		return new Object[][] {
			{"direct",ti.direct_org1_id,ti.direct_org1_user1_token,direct_cloud_id,"cloud_direct_file_folder_backup","cloud_direct_baas",
				direct_source_id,direct_destination_ID},
			{"suborg-mspT",ti.root_msp1_suborg1_id,ti.root_msp_org1_user1_token,msp_cloud_id,"cloud_direct_file_folder_backup","cloud_direct_baas",
					sub_orga_source_id,sub_orga_destination_ID},
			{"suborg",ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,msp_cloud_id,"cloud_direct_file_folder_backup","cloud_direct_baas",
						sub_orga_source_id,sub_orga_destination_ID},
			{"suborg-mspAccAdminT",ti.root_msp1_suborg1_id,ti.root_msp_org1_msp_accountadmin1_token,msp_cloud_id,"cloud_direct_file_folder_backup","cloud_direct_baas",
							sub_orga_source_id,sub_orga_destination_ID},
			{"submsp_suborg",ti.msp1_submsp1_sub_org1_id,ti.root_msp1_submsp1_user1_token,msp_cloud_id,"cloud_direct_file_folder_backup","cloud_direct_baas",
								submsp_suborg_source_id,submsp_sub_org_destination_ID},
			{"submsp_suborg",ti.msp1_submsp1_sub_org1_id,ti.msp1_submsp1_suborg1_user1_token,msp_cloud_id,"cloud_direct_file_folder_backup","cloud_direct_baas",
									submsp_suborg_source_id,submsp_sub_org_destination_ID},
			{"submsp_suborg-mspAccAdminT",ti.msp1_submsp1_sub_org1_id,ti.root_msp1_submsp1_account_admin_token,msp_cloud_id,"cloud_direct_file_folder_backup","cloud_direct_baas",
										submsp_suborg_source_id,submsp_sub_org_destination_ID},
		};
	}

	@Test(dataProvider = "deletePolicybyId",enabled=true)
	public void deletepolicybypolicyid(String organizationType,
			String organization_id,
			String token,
			String cloud_account_id,
			String task_type,
			String policy_type,
			String source_id,
			String destination_id
			) {

		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);

		long start_time_ts;
		long end_time_ts;
		String schedule_id=spogServer.returnRandomUUID();
		String task_id=spogServer.returnRandomUUID();
		String throttle_id=spogServer.returnRandomUUID();
		String throttle_type="network";
		String policy_name=spogServer.ReturnRandom("test");
		String policy_description=spogServer.ReturnRandom("description");
		String policy_id = spogServer.returnRandomUUID();
		String resource_name = spogServer.ReturnRandom("rakesh")+"_";
		String prefix = RandomStringUtils.randomAlphanumeric(8);

		ArrayList<HashMap<String,Object>>  destinations = new ArrayList<>();

		test.log(LogStatus.INFO, "Get the datacenter id");
		spogDestinationServer.setToken(token);
		spogServer.setToken(token);
		policy4SPOGServer.setToken(token);

		test.log(LogStatus.INFO, "Create cloud direct schedule");
		HashMap<String, Object> cloudDirectScheduleDTO=policy4SPOGServer.createCloudDirectScheduleDTO("0 6 * * *", test);

		test.log(LogStatus.INFO, "Create schedule settings");
		HashMap<String, Object> scheduleSettingDTO=policy4SPOGServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null,test);

		test.log(LogStatus.INFO, "Create schedules");
		ArrayList<HashMap<String,Object>> schedules =policy4SPOGServer.createPolicyScheduleDTO(null,schedule_id, 
				"1d", task_id, destination_id, scheduleSettingDTO, "06:00", "12:00", test);

		test.log(LogStatus.INFO, "Exclude the folders from cloud direct file backup");  
		ArrayList<HashMap<String,Object>> excludes=policy4SPOGServer.createExcludeInfoDTO(null, "path", "c:\\tmp", test);
		HashMap<String, Object> cloudDirectLocalBackupDTO=policy4SPOGServer.createCloudDirectLocalBackupDTO("d:\\tmp", "true", excludes, test);
		HashMap<String, Object> cloudDirectFileBackupTaskInfoDTO=policy4SPOGServer.createCloudDirectFileBackupTaskInfoDTO("d:\\tmp", cloudDirectLocalBackupDTO, test);

		ArrayList<String> drivers = new ArrayList<>();
		drivers.add("C");

		HashMap<String, Object> cloudDirectimageBackupTaskInfoDTO = policy4SPOGServer.createCloudDirectImageBackupTaskInfoDTO(drivers, cloudDirectLocalBackupDTO, test);


		test.log(LogStatus.INFO, "Create task type and link it to the destination ");
		if(task_type == "cloud_direct_file_folder_backup") {
			destinations= policy4SPOGServer.createPolicyTaskDTO(null, task_id, task_type, destination_id, "none", null, cloudDirectFileBackupTaskInfoDTO, null, test);
		}else if(task_type == "cloud_direct_image_backup") {
			destinations= policy4SPOGServer.createPolicyTaskDTO(null, task_id, task_type, destination_id, "none", cloudDirectimageBackupTaskInfoDTO, null, null, test);
		}

		//ArrayList<HashMap<String,Object>>  destinations= policy4SPOGServer.createPolicyTaskDTO(null, task_id, task_type, datastore_id, cloud_account_id, cloudDirectimageBackupTaskInfoDTO, cloudDirectFileBackupTaskInfoDTO, null, test);

		test.log(LogStatus.INFO, "Create network throttle ");
		ArrayList<HashMap<String,Object>> throttles =policy4SPOGServer.createPolicyThrottleDTO(null, throttle_id, task_id, throttle_type, "1200", "1", "06:00", "18:00", test);

		test.log(LogStatus.INFO, "Create a policy of type backup_recovery and task of type "+task_type);
		Response response=policy4SPOGServer.createPolicy(policy_name, policy_description, policy_type, null, "true", source_id, destinations, schedules, throttles, policy_id, organization_id, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);

		//Check for policy status
		for (int i = 0; i < 15; i++) {
			response = policy4SPOGServer.getPolicyById(token, policy_id, test);	
			String policy_status = response.then().extract().path("data.policy_status");
			if (policy_status.equalsIgnoreCase("deploying")) {
				//Policy_status takes time to get update from state deploying
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				i++;
			}else {
				break;
			}
			if (i==15) {
				System.out.println("Policy is in deploying status only for the past 5 minuites");
				test.log(LogStatus.FAIL, "Policy is in deploying status only for the past 5 minuites");
			}
		}		
		
		/******************** Sprint 32 enhancement - Delete disabled policy should fail ****************************/
		policy4SPOGServer.disablePolicyById(token, policy_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		
		test.log(LogStatus.INFO, "Disable and try to delete should fail - 400");
		policy4SPOGServer.deletePolicybyPolicyId(token, policy_id, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.UNABLE_TO_DELETE_DISABLED_POLICY, test);
		
		policy4SPOGServer.enablePolicyById(token, policy_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		/****************************************** end **********************************************************/
		
		test.log(LogStatus.INFO, "Delete the policy by policy id");
		policy4SPOGServer.deletePolicybyPolicyId(token, policy_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
	}

	@DataProvider(name = "deletePolicyByPolicyIdInvalid")
	public Object[][] deletePolicyByPolicyId_400(){
		return  new Object[][] {
			//400
			{"Delete policy by invalid id in direct organization",ti.direct_org1_user1_token, "invalid",SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ERROR_MESSAGE_ELEMENT_NOT_UUID},
			{"Delete policy by invalid id in sub org with msp user token",ti.root_msp_org1_user1_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ERROR_MESSAGE_ELEMENT_NOT_UUID},
			{"Delete policy by invalid id in sub organization with sub org user token",ti.root_msp1_suborg1_user1_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ERROR_MESSAGE_ELEMENT_NOT_UUID},
			{"Delete policy by invalid id in sub organization account admin token",ti.root_msp_org1_msp_accountadmin1_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ERROR_MESSAGE_ELEMENT_NOT_UUID},
			{"Delete policy by invalid id in sub org with sub msp user token",ti.root_msp1_submsp1_user1_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ERROR_MESSAGE_ELEMENT_NOT_UUID},
			{"Delete policy by invalid id in sub organization with sub org user token",ti.msp1_submsp1_suborg1_user1_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ERROR_MESSAGE_ELEMENT_NOT_UUID},
			{"Delete policy by invalid id in sub organization sub msp account admin token",ti.root_msp1_submsp1_account_admin_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ERROR_MESSAGE_ELEMENT_NOT_UUID},
			
			//401
			{"Delete policy by invalid token","invalid", UUID.randomUUID().toString(), SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT},
			{"Delete policy by invalid null as token",null, UUID.randomUUID().toString(), SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED},
			{"Delete policy by invalid missing token","", UUID.randomUUID().toString(), SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED},			
			
			//403
			{"Delete policy by policy id in direct org with msp user token",ti.root_msp_org1_user1_token, direct_policy_ID, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Delete policy by policy id in direct org with sub org user token",ti.root_msp1_suborg1_user1_token, direct_policy_ID, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Delete policy by policy id in direct org with msp account admin user token",ti.root_msp_org1_msp_accountadmin1_token, direct_policy_ID, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Delete policy by policy id in direct org with sub msp user token",ti.root_msp1_submsp1_user1_token, direct_policy_ID, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Delete policy by policy id in direct org with sub msp sub org user token",ti.msp1_submsp1_suborg1_user1_token, direct_policy_ID, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Delete policy by policy id in direct org with sub msp account admin user token",ti.root_msp1_submsp1_account_admin_token, direct_policy_ID, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Delete policy by policy id in sub org with direct org user token",ti.direct_org1_user1_token, sub_orga_policy_ID, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Delete policy by policy id in sub org with msp2 user token",ti.root_msp_org2_user1_token, sub_orga_policy_ID, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Delete policy by policy id in sub org with sub2 org user token",ti.root_msp1_suborg2_user1_token, sub_orga_policy_ID, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Delete policy by policy id in sub org with msp account admin user token",ti.root_msp_org2_msp_accountadmin1_token, sub_orga_policy_ID, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Delete policy by policy id in sub org with sub msp user token",ti.root_msp1_submsp1_user1_token, sub_orga_policy_ID, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Delete policy by policy id in sub org with sub msp sub org user token",ti.msp1_submsp1_suborg1_user1_token, sub_orga_policy_ID, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Delete policy by policy id in sub org with sub msp account admin user token",ti.root_msp1_submsp1_account_admin_token, sub_orga_policy_ID, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Delete policy by policy id in sub msp sub org with direct org user token",ti.direct_org1_user1_token, submsp_sub_orga_policy_ID, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Delete policy by policy id in sub msp sub org with msp user token",ti.root_msp_org1_user1_token, submsp_sub_orga_policy_ID, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Delete policy by policy id in sub msp sub org with sub2 org user token",ti.root_msp1_suborg2_user1_token, submsp_sub_orga_policy_ID, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Delete policy by policy id in sub msp sub org with msp account admin user token",ti.root_msp_org1_msp_accountadmin1_token, submsp_sub_orga_policy_ID, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Delete policy by policy id in sub msp sub org with sub msp user token",ti.root_msp1_submsp2_user1_token, submsp_sub_orga_policy_ID, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Delete policy by policy id in sub msp sub org with sub msp account admin user token",ti.root_msp1_submsp2_account_admin_token, submsp_sub_orga_policy_ID, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},		
			
			//404
			{"Delete policy by policy id that does not exist in direct organization",ti.direct_org1_user1_token, UUID.randomUUID().toString(),SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.NOT_FOUND_POLICY_WITH_ID},
			{"Delete policy by policy id that does not exist in sub org with msp user token",ti.root_msp_org1_user1_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.NOT_FOUND_POLICY_WITH_ID},
			{"Delete policy by policy id that does not exist in sub organization with sub org user token",ti.root_msp1_suborg1_user1_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.NOT_FOUND_POLICY_WITH_ID},
			{"Delete policy by policy id that does not exist in sub organization account admin token",ti.root_msp_org1_msp_accountadmin1_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.NOT_FOUND_POLICY_WITH_ID},
			{"Delete policy by policy id that does not exist in sub org with sub msp user token",ti.root_msp1_submsp1_user1_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.NOT_FOUND_POLICY_WITH_ID},
			{"Delete policy by policy id that does not exist in sub organization with sub org user token",ti.msp1_submsp1_suborg2_user1_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.NOT_FOUND_POLICY_WITH_ID},
			{"Delete policy by policy id that does not exist in sub organization sub msp account admin token",ti.root_msp1_submsp1_account_admin_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.NOT_FOUND_POLICY_WITH_ID},
			
		};
	}

	@Test(dataProvider = "deletePolicyByPolicyIdInvalid", enabled=true)
	public void deletePolicyByPolicyId_Invalid(String testCase, String token, String policy_id, int expectedStatusCode, SpogMessageCode expectedErrorMessage) {

		test=ExtentManager.getNewTest(testCase);

		
		policy4SPOGServer.deletePolicybyPolicyId(token, policy_id, expectedStatusCode, expectedErrorMessage, test);
	}

	@Test(dataProvider = "deletePolicybyId",enabled=true)
	public void deletepolicybypolicyid_undeployForSource(String organizationType,
			String organization_id,
			String validToken,
			String cloud_account_id,
			String task_type,
			String policy_type,
			String source_id,
			String destination_id
			) {

		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+organizationType);

		long start_time_ts;
		long end_time_ts;
		String schedule_id=spogServer.returnRandomUUID();
		String task_id=spogServer.returnRandomUUID();
		String throttle_id=spogServer.returnRandomUUID();
		String throttle_type="network";
		String policy_name=spogServer.ReturnRandom("test");
		String policy_description=spogServer.ReturnRandom("description");
		String policy_id = spogServer.returnRandomUUID();
		String resource_name = spogServer.ReturnRandom("rakesh")+"_";
		String prefix = RandomStringUtils.randomAlphanumeric(8);

		ArrayList<HashMap<String,Object>>  destinations = new ArrayList<>();

		test.log(LogStatus.INFO, "Get the datacenter id");
		spogDestinationServer.setToken(validToken);
		//String[] datacenters = spogDestinationServer.getDestionationDatacenterID();

		spogServer.setToken(validToken);
		policy4SPOGServer.setToken(validToken);

		test.log(LogStatus.INFO, "Create cloud direct schedule");
		HashMap<String, Object> cloudDirectScheduleDTO=policy4SPOGServer.createCloudDirectScheduleDTO("0 6 * * *", test);

		test.log(LogStatus.INFO, "Create schedule settings");
		HashMap<String, Object> scheduleSettingDTO=policy4SPOGServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null,test);

		test.log(LogStatus.INFO, "Create schedules");
		ArrayList<HashMap<String,Object>> schedules =policy4SPOGServer.createPolicyScheduleDTO(null,schedule_id, 
				"1d", task_id, destination_id, scheduleSettingDTO, "06:00", "12:00", test);

		test.log(LogStatus.INFO, "Exclude the folders from cloud direct file backup");  
		ArrayList<HashMap<String,Object>> excludes=policy4SPOGServer.createExcludeInfoDTO(null, "path", "c:\\tmp", test);
		HashMap<String, Object> cloudDirectLocalBackupDTO=policy4SPOGServer.createCloudDirectLocalBackupDTO("d:\\tmp", "true", excludes, test);
		HashMap<String, Object> cloudDirectFileBackupTaskInfoDTO=policy4SPOGServer.createCloudDirectFileBackupTaskInfoDTO("d:\\tmp", cloudDirectLocalBackupDTO, test);

		ArrayList<String> drivers = new ArrayList<>();
		drivers.add("C");

		HashMap<String, Object> cloudDirectimageBackupTaskInfoDTO = policy4SPOGServer.createCloudDirectImageBackupTaskInfoDTO(drivers, cloudDirectLocalBackupDTO, test);


		test.log(LogStatus.INFO, "Create task type and link it to the destination ");
		if(task_type == "cloud_direct_file_folder_backup") {
			destinations= policy4SPOGServer.createPolicyTaskDTO(null, task_id, task_type, destination_id, "none", null, cloudDirectFileBackupTaskInfoDTO, null, test);
		}else if(task_type == "cloud_direct_image_backup") {
			destinations= policy4SPOGServer.createPolicyTaskDTO(null, task_id, task_type, destination_id, "none", cloudDirectimageBackupTaskInfoDTO, null, null, test);
		}

		//ArrayList<HashMap<String,Object>>  destinations= policy4SPOGServer.createPolicyTaskDTO(null, task_id, task_type, datastore_id, cloud_account_id, cloudDirectimageBackupTaskInfoDTO, cloudDirectFileBackupTaskInfoDTO, null, test);

		test.log(LogStatus.INFO, "Create network throttle ");
		ArrayList<HashMap<String,Object>> throttles =policy4SPOGServer.createPolicyThrottleDTO(null, throttle_id, task_id, throttle_type, "1200", "1", "06:00", "18:00", test);

		test.log(LogStatus.INFO, "Create a policy of type backup_recovery and task of type "+task_type);
		Response response=policy4SPOGServer.createPolicy(policy_name, policy_description, policy_type, null, "true", source_id, destinations, schedules, throttles, policy_id, organization_id, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);

		spogServer.setToken(validToken);
		response = spogServer.getSourceById(validToken, source_id, test);
		ArrayList<Object> policies = response.then().extract().path("data.policy");
		HashMap<String, Object> policy1 = (HashMap<String, Object>) policies.get(0);
		spogServer.assertResponseItem(policy_id, policy1.get("policy_id"), test);
		
		//Check for policy status
				for (int i = 0; i < 15; i++) {
					response = policy4SPOGServer.getPolicyById(validToken, policy_id, test);	
					String policy_status = response.then().extract().path("data.policy_status");
					if (policy_status.equalsIgnoreCase("deploying")) {
						//Policy_status takes time to get update from state deploying
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e1) {
							e1.printStackTrace();
						}
						i++;
					}else {
						break;
					}
					if (i==15) {
						System.out.println("Policy is in deploying status only for the past 5 minuites");
						test.log(LogStatus.FAIL, "Policy is in deploying status only for the past 5 minuites");
					}
				}

		test.log(LogStatus.INFO, "Delete the policy by policy id");
		policy4SPOGServer.deletePolicybyPolicyId(validToken, policy_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		response = spogServer.getSourceById(validToken, source_id, test);
		spogServer.assertResponseItem(null, response.then().extract().path("data.policy"), test);;

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
