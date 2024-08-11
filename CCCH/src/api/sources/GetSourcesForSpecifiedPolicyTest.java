package api.sources;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

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

import Constants.ConnectionStatus;
import Constants.DestinationStatus;
import Constants.DestinationType;
import Constants.OSMajor;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.GatewayServer.siteType;
import base.prepare.TestOrgInfo;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import InvokerServer.Source4SPOGServer;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class GetSourcesForSpecifiedPolicyTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private UserSpogServer userSpogServer;
	private SPOGDestinationServer spogDestinationServer;
	private Policy4SPOGServer policy4SPOGServer;
	private Source4SPOGServer source4SPOGServer;
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

	String direct_cloud_id;
	String msp_cloud_id;
	
	LocalDate date = LocalDate.now();  
	LocalDate yesterday = date.minusDays(1);  
	LocalDate tomorrow = yesterday.plusDays(2);
	
	Response response;
	String direct_sourceIds = null;
	String submsp_sourceIds = null;
	String suborg_sourceIds = null;
	ArrayList<HashMap<String,Object>> direct_SourcesInfo = new ArrayList<HashMap<String,Object>>();
	ArrayList<HashMap<String,Object>> submsp_SourcesInfo = new ArrayList<HashMap<String,Object>>();
	ArrayList<HashMap<String,Object>> suborg_SourcesInfo = new ArrayList<HashMap<String,Object>>();
	
	private String  org_model_prefix=this.getClass().getSimpleName();
	private TestOrgInfo ti;
	private String direct_destination_ID;
	private String sub_orga_destination_ID;
	private String submsp_sub_org_destination_ID;
	
	@BeforeClass
	@Parameters({ "baseURI", "port","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String logFolder,String runningMachine,String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		policy4SPOGServer = new Policy4SPOGServer(baseURI, port);
		source4SPOGServer = new Source4SPOGServer(baseURI, port);
		rep = ExtentManager.getInstance("GetSourcesForSpecifiedPolicyTest", logFolder);
		test = rep.startTest("Setup for GetSourcesForSpecifiedPolicyTest");
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
		
		response = spogServer.getCloudAccounts(ti.root_msp_org1_user1_token, "", test);
		msp_cloud_id = response.then().extract().path("data[0].cloud_account_id");
		
		response = spogServer.getCloudAccounts(ti.direct_org1_user1_token, "", test);
		direct_cloud_id = response.then().extract().path("data[0].cloud_account_id");
		
		//get default destinations
		response = spogDestinationServer.getDestinations(ti.direct_org1_user1_token, null, test);
		direct_destination_ID = response.then().extract().path("data[0].destination_id");

		response = spogDestinationServer.getDestinations(ti.root_msp1_suborg1_user1_token, null, test);
		sub_orga_destination_ID = response.then().extract().path("data[0].destination_id");

		response = spogDestinationServer.getDestinations(ti.msp1_submsp1_suborg1_user1_token, null, test);
		submsp_sub_org_destination_ID = response.then().extract().path("data[0].destination_id");
	}
	
	@DataProvider(name="postSource")
	public Object[][] postSource(){
		return new Object[][] {
			//dataprovider to create the sources in all organizations
			{"direct",ti.direct_org1_user1_token,spogServer.ReturnRandom("src"), SourceType.machine, SourceProduct.cloud_direct, ti.direct_org1_id, direct_cloud_id, ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(), "sql;exchange",  "Rak_vm2",  null, "Rak_agent1", "windows 2012", "64" ,"1.0.0","2.0", "http://upgrade",4},
			{"suborg",ti.root_msp1_suborg1_user1_token,spogServer.ReturnRandom("src"), SourceType.machine, SourceProduct.cloud_direct, ti.root_msp1_suborg1_id, msp_cloud_id, ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(), "sql;exchange",  "Rak_vm2",  null, "Rak_agent1", "windows 2012", "64" ,"1.0.0","2.0", "http://upgrade",4},
			{"submsp_suborg",ti.msp1_submsp1_suborg1_user1_token,spogServer.ReturnRandom("src"), SourceType.machine, SourceProduct.cloud_direct, ti.msp1_submsp1_sub_org1_id, msp_cloud_id, ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(), "sql;exchange",  "Rak_vm2",  null, "Rak_agent1", "windows 2012", "64" ,"1.0.0","2.0", "http://upgrade",4},
		};
	}
	
	@Test(dataProvider= "postSource")
	public void postSources(String orgType, String site_token, String source_name, SourceType source_type, SourceProduct source_product,
			String organization_id, String site_id, ProtectionStatus protection_status,
			ConnectionStatus connection_status, String os_major, String applications, String vm_name,
			String hypervisor_id, String agent_name, String os_name, String os_architecture,
			String agent_current_version, String agent_upgrade_version, String agent_upgrade_link, 
			int noOfSourcesToCreate) {
		
		String sources = null, source = null;
		HashMap<String, Object> SourceInfo = new HashMap<>();
		String site_name = "cloudAccountName";//common for all cloud_accounts	
		String hypervisor_name = null;
		ArrayList<HashMap<String, Object>> SourceList = new ArrayList<>();
		
		spogServer.setToken(site_token);
		test.log(LogStatus.INFO, "Creating source in the organization of type: "+ orgType);
		for (int i = 0; i < noOfSourcesToCreate; i++) {
			Response response = spogServer.createSource(source_name+i, source_type, source_product, organization_id, site_id,
					protection_status, connection_status, os_major, applications, vm_name+i, hypervisor_id, agent_name+i,
					os_name, os_architecture, agent_current_version, agent_upgrade_version, agent_upgrade_link, test);
			source = response.then().extract().path("data.source_id");
			SourceInfo = source4SPOGServer.composeSourceInfo(source, source_name+i, source_type, organization_id, site_id, os_major,os_name,os_architecture, hypervisor_id, site_name, hypervisor_name, null, test);
			
			if (sources == null) {
				sources = source;
			}else{
				sources = sources +","+ source;
			}
			SourceList.add(SourceInfo);
		}
		
		String source_group_id = spogServer.createGroupWithCheck2(site_token, organization_id, spogServer.ReturnRandom("group_name"), spogServer.ReturnRandom("group_description"), test);
		SourceInfo.put("source_group", source_group_id);
		
		//Adding to the global varibles to call them and delete in after class
		if (orgType.contains("direct")) {
			direct_SourcesInfo = SourceList;
			direct_sourceIds = sources;
		}else if(orgType.contains("submsp")) {
			submsp_SourcesInfo = SourceList;
			submsp_sourceIds = sources;
		}else if(orgType.contains("suborg")) {
			suborg_SourcesInfo = SourceList;
			suborg_sourceIds = sources;
		}
		
	}
	
	@DataProvider(name = "getSourcesForSpecifiedpolicy")
	public final Object[][] getSourcesForSpecifiedpolicy() {
		return new Object[][] {
			
			// with csr and csr readonly user token			
			{"direct",ti.direct_org1_id,ti.direct_org1_user1_token,"cloud_direct_file_folder_backup","cloud_direct_baas",
				direct_destination_ID,SpogConstants.SUCCESS_GET_PUT_DELETE,null,direct_sourceIds,direct_SourcesInfo,
				ti.csr_token,ti.csr_readonly_token,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"suborg",ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,"cloud_direct_file_folder_backup","cloud_direct_baas",
					sub_orga_destination_ID,SpogConstants.SUCCESS_GET_PUT_DELETE,null,suborg_sourceIds,suborg_SourcesInfo,
					ti.csr_token,ti.csr_readonly_token,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			{"submsp_suborg",ti.msp1_submsp1_sub_org1_id,ti.msp1_submsp1_suborg1_user1_token,"cloud_direct_file_folder_backup","cloud_direct_baas",
					submsp_sub_org_destination_ID,SpogConstants.SUCCESS_GET_PUT_DELETE,null,submsp_sourceIds,submsp_SourcesInfo,
					ti.csr_token,ti.csr_readonly_token,SpogConstants.SUCCESS_GET_PUT_DELETE,null},
			

			{"direct",ti.direct_org1_id,ti.direct_org1_user1_token,"cloud_direct_file_folder_backup","cloud_direct_baas",
						direct_destination_ID,SpogConstants.SUCCESS_GET_PUT_DELETE,null,direct_sourceIds,direct_SourcesInfo,
				ti.root_msp_org1_user1_token,ti.root_msp1_suborg1_user1_token,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			
			
			{"suborg",ti.root_msp1_suborg1_id,ti.root_msp1_suborg1_user1_token,"cloud_direct_file_folder_backup","cloud_direct_baas",
					sub_orga_destination_ID,SpogConstants.SUCCESS_GET_PUT_DELETE,null,suborg_sourceIds,suborg_SourcesInfo,
					ti.direct_org1_user1_token,ti.root_msp_org2_user1_token,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"suborg",ti.root_msp1_suborg1_id,ti.root_msp_org1_msp_accountadmin1_token,"cloud_direct_file_folder_backup","cloud_direct_baas",
					sub_orga_destination_ID,SpogConstants.SUCCESS_GET_PUT_DELETE,null,suborg_sourceIds,suborg_SourcesInfo,
					ti.direct_org1_user1_token,ti.root_msp_org2_user1_token,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY	},
			
			{"submsp_suborg",ti.msp1_submsp1_sub_org1_id,ti.msp1_submsp1_suborg1_user1_token,"cloud_direct_file_folder_backup","cloud_direct_baas",
						submsp_sub_org_destination_ID,SpogConstants.SUCCESS_GET_PUT_DELETE,null,submsp_sourceIds,submsp_SourcesInfo,
						ti.direct_org1_user1_token,ti.root_msp_org2_user1_token,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"submsp_suborg",ti.msp1_submsp1_sub_org1_id,ti.root_msp1_submsp1_account_admin_token,"cloud_direct_file_folder_backup","cloud_direct_baas",
						submsp_sub_org_destination_ID,SpogConstants.SUCCESS_GET_PUT_DELETE,null,submsp_sourceIds,submsp_SourcesInfo,
						ti.direct_org1_user1_token,ti.root_msp1_submsp2_user1_token,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY	},
		};
	}
	@Test(dependsOnMethods= {"postSources"},dataProvider="getSourcesForSpecifiedpolicy")
	public void getSourcesForSpecifiedPolicy_200_403(String organizationType,
													String organization_id,
													String validToken,
													String task_type,
													String policy_type,
													String datastore_id,
													int expectedStatusCode,
													SpogMessageCode expectedErrorMessage,
													String sources,
													ArrayList<HashMap<String, Object>> sourcesInfo,
													String otherTtoken1,
													String otherToken2,
													int expStatusCode403,
													SpogMessageCode expErrorMessage403
													) {
		String prefix = RandomStringUtils.randomAlphanumeric(8);
				
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
		String resource_name = spogServer.ReturnRandom("kiran")+"_";
		HashMap<String, Object> scheduleSettingDTO = new HashMap<>();
		ArrayList<HashMap<String,Object>>  destinations = new ArrayList<>();
		ArrayList<HashMap<String,Object>> schedules = new ArrayList<>();
		test.log(LogStatus.INFO, "Get the datacenter id");
		spogDestinationServer.setToken(validToken);
		
		Response response = spogDestinationServer.getDestinationById(validToken, datastore_id, test);
		String destination_name = response.then().extract().path("data.destination_name");
		
		spogServer.setToken(validToken);
		test.log(LogStatus.INFO, "post the destination");
		policy4SPOGServer.setToken(validToken);
		if(task_type.equalsIgnoreCase("udp_replication_from_remote")) {
			destination_name = prefix+"-test";
			datastore_id = spogDestinationServer.createDestinationWithCheck("",organization_id, msp_cloud_id, 
		    		"91a9b48e-6ac6-4c47-8202-614b5cdcfe0c","cloud_hybrid_store", "running","9",
		    		"", "normal", "iran-win7", "2M", "2M", 
		    		"0","0", "31", "0", "2", "0", "5", "true", "1", "true" ,destination_name,		
				test);
			HashMap<String, Object> CustomScheduleDTO=policy4SPOGServer.createCustomScheduleDTO("1524469596000", "full","1","true","10","minutes",test);
			scheduleSettingDTO=policy4SPOGServer.createScheduleSettingDTO( null,CustomScheduleDTO,test);
			schedules =policy4SPOGServer.createPolicyScheduleDTO(null,spogServer.returnRandomUUID(), 
					  "custom", task_id, datastore_id, scheduleSettingDTO, "06:00", "12:00", test);
			
		}else {
			test.log(LogStatus.INFO, "Create cloud direct schedule");
			
			HashMap<String, Object> cloudDirectScheduleDTO=policy4SPOGServer.createCloudDirectScheduleDTO("0 6 * * *", test);
			
			test.log(LogStatus.INFO, "Create schedule settings");
			scheduleSettingDTO=policy4SPOGServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null,test);
			schedules =policy4SPOGServer.createPolicyScheduleDTO(null,schedule_id, 
					  "1d", task_id, datastore_id, scheduleSettingDTO, "06:00", "12:00", task_type ,destination_name,test);
		}
		
		test.log(LogStatus.INFO, "Exclude the folders from cloud direct file backup");  
		
		ArrayList<HashMap<String,Object>> excludes=policy4SPOGServer.createExcludeInfoDTO(null, "path", "c:\\tmp", test);
		HashMap<String, Object> cloudDirectLocalBackupDTO=policy4SPOGServer.createCloudDirectLocalBackupDTO("d:\\tmp", "true", test);
		HashMap<String, Object> cloudDirectFileBackupTaskInfoDTO=policy4SPOGServer.createCloudDirectFileBackupTaskInfoDTO("d:\\tmp", cloudDirectLocalBackupDTO, excludes,test);
		
		ArrayList<String> drivers = new ArrayList<>();
		drivers.add("C");
		
		HashMap<String, Object> cloudDirectimageBackupTaskInfoDTO = policy4SPOGServer.createCloudDirectImageBackupTaskInfoDTO(drivers, cloudDirectLocalBackupDTO, test);
		
		HashMap<String, Object> PerformARTestOption=policy4SPOGServer.createPerformARTestOption("true", "true", "true", "true", test);
		HashMap<String, Object> RetentionPolicyOption =policy4SPOGServer.createRetentionPolicyOption ("2", "2", "2", "2", test);
		HashMap<String, Object> udp_replication_from_remote =policy4SPOGServer.createUdpReplicationFromRemoteInfoDTO(PerformARTestOption, RetentionPolicyOption, test);
		  
		test.log(LogStatus.INFO, "Create task type and link it to the destination ");
		if(task_type == "cloud_direct_file_folder_backup") {
			destinations= policy4SPOGServer.createPolicyTaskDTO(null, task_id, task_type, datastore_id, "none", null, cloudDirectFileBackupTaskInfoDTO, null, test);
		}else if(task_type == "cloud_direct_image_backup") {
			destinations= policy4SPOGServer.createPolicyTaskDTO(null, task_id, task_type, datastore_id, "none", cloudDirectimageBackupTaskInfoDTO, null, null, test);
		}else if(task_type.equalsIgnoreCase("udp_replication_from_remote")) {
			 
			 destinations= policy4SPOGServer.createPolicyTaskDTO(null, task_id, task_type, datastore_id, "none", null, null, udp_replication_from_remote, test);
		}
		

		test.log(LogStatus.INFO, "Create network throttle ");
		
		ArrayList<HashMap<String,Object>> throttles =policy4SPOGServer.createPolicyThrottleDTO(null, throttle_id, task_id, throttle_type, "1200", "1", "06:00", "18:00", task_type,datastore_id,destination_name,test);
		
		test.log(LogStatus.INFO, "Create a policy of type backup_recovery and task of type "+task_type);
		policy4SPOGServer.setToken(validToken);
		response = policy4SPOGServer.createPolicy(policy_name, policy_description, policy_type, true, sources, destinations, schedules, throttles, policy_id, organization_id, "", test);
		
//		createPolicy(policy_name, policy_description, policy_type, null, "true", sources, destinations, schedules, throttles, policy_id, organization_id, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		
		//valid case 200
		test.log(LogStatus.INFO, "Get sources for specified policy");
		source4SPOGServer.getSourcesForSpecifiedPolicyWithCheck(validToken, policy_id, sourcesInfo, expectedStatusCode, expectedErrorMessage, test);
		
		//invalid case 403 with other organization token
		test.log(LogStatus.INFO, "Get Sourcse for specified policy with other org token1: "+otherTtoken1);
		source4SPOGServer.getSourcesForSpecifiedPolicyWithCheck(otherTtoken1, policy_id, sourcesInfo, expStatusCode403, expErrorMessage403, test);
		
		test.log(LogStatus.INFO, "Get Sourcse for specified policy with other org token2: "+otherToken2);
		source4SPOGServer.getSourcesForSpecifiedPolicyWithCheck(otherToken2, policy_id, sourcesInfo, expStatusCode403, expErrorMessage403, test);
	}
	
	@DataProvider(name="getSourcesForSpecifiedPolicyInvalid")
	public Object[][] getSourcesForSpecifiedPolicyInvalid(){
		return new Object[][] {
			
//			400 and 404 cases
			{"Get sources for specified policy where policy id is invalid in direct organization",ti.direct_org1_user1_token,"INVALID",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_POLICYID_IS_NOT_UUID},
			{"Get sources for specified policy where policy id is null in direct organization",ti.direct_org1_user1_token,null,SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_POLICYID_IS_NOT_UUID},
			{"Get sources for specified policy where policy id is random_id in direct organization",ti.direct_org1_user1_token,UUID.randomUUID().toString(),SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.NOT_FOUND_POLICY_WITH_ID},
			
			{"Get sources for specified policy where policy id is invalid in sub organization with msp token", ti.root_msp_org1_user1_token,"INVALID",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_POLICYID_IS_NOT_UUID},
			{"Get sources for specified policy where policy id is null in sub organization with msp token", ti.root_msp_org1_user1_token,null,SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_POLICYID_IS_NOT_UUID},
			{"Get sources for specified policy where policy id is random_id in sub organization with msp token", ti.root_msp_org1_user1_token,UUID.randomUUID().toString(),SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.NOT_FOUND_POLICY_WITH_ID},
			
			{"Get sources for specified policy where policy id is invalid in sub organization", ti.root_msp1_suborg1_user1_token,"INVALID",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_POLICYID_IS_NOT_UUID},
			{"Get sources for specified policy where policy id is null in sub organization", ti.root_msp1_suborg1_user1_token,null,SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_POLICYID_IS_NOT_UUID},
			{"Get sources for specified policy where policy id is random_id in sub organization", ti.root_msp1_suborg1_user1_token,UUID.randomUUID().toString(),SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.NOT_FOUND_POLICY_WITH_ID},
			
			{"Get sources for specified policy where policy id is invalid in sub organization with msp account admin token", ti.root_msp_org1_msp_accountadmin1_token,"INVALID",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_POLICYID_IS_NOT_UUID},
			{"Get sources for specified policy where policy id is null in sub organization with msp account admin token", ti.root_msp_org1_msp_accountadmin1_token,null,SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_POLICYID_IS_NOT_UUID},
			{"Get sources for specified policy where policy id is random_id in sub organization with msp account admin token", ti.root_msp_org1_msp_accountadmin1_token,UUID.randomUUID().toString(),SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.NOT_FOUND_POLICY_WITH_ID},
			
			{"Get sources for specified policy where policy id is invalid and using csr token",ti.csr_token,"INVALID",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_POLICYID_IS_NOT_UUID},
			{"Get sources for specified policy where policy id is null and using csr token",ti.csr_token,null,SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_POLICYID_IS_NOT_UUID},
			{"Get sources for specified policy with policy id that doesn't exist and using csr token",ti.csr_token,UUID.randomUUID().toString(),SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.NOT_FOUND_POLICY_WITH_ID},
			
			{"Get sources for specified policy with policy id that doesn't exist and using csr readonly user token",ti.csr_readonly_token,"INVALID",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_POLICYID_IS_NOT_UUID},
			{"Get sources for specified policy with policy id that doesn't exist and using csr readonly user token",ti.csr_readonly_token,null,SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_POLICYID_IS_NOT_UUID},
			{"Get sources for specified policy with policy id that doesn't exist and using csr readonly user token",ti.csr_readonly_token,UUID.randomUUID().toString(),SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.NOT_FOUND_POLICY_WITH_ID},
			
			//401 cases with invalid token
			{"Invalid token","junktoken",UUID.randomUUID().toString(),SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT},
			{"Missing token","",UUID.randomUUID().toString(),SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTHENTICATION_FAILED}
		};
	}
	
	@Test(dataProvider="getSourcesForSpecifiedPolicyInvalid")
	public void getSourcesForSpecifiedPolicy_400_401_404(String caseType, String validToken, String policy_id, int expectedStatusCode, SpogMessageCode expectedErrorMessage) {
		
		ArrayList<HashMap<String, Object>> sourcesInfo = null;
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+caseType);
		
		test.log(LogStatus.INFO, caseType);
		source4SPOGServer.getSourcesForSpecifiedPolicyWithCheck(validToken, policy_id, sourcesInfo, expectedStatusCode, expectedErrorMessage, test);
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
