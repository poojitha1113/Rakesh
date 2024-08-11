package api.hypervisor;

import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.DestinationStatus;
import Constants.DestinationType;
import Constants.SpogConstants;
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

public class HypervisorPolicyTest extends base.prepare.Is4Org {

	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private SPOGHypervisorsServer spogHypervisorsServer;
	private SPOGDestinationServer spogDestinationServer;
	private UserSpogServer userSpogServer;
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
	
	String direct_cloud_id;
	String msp_cloud_id;
	
	private String direct_hyperv_hypervisor_id;
	private String direct_vmware_hypervisor_id;
	private String sub_hyperv_hypervisor_id;
	private String sub_vmware_hypervisor_id;
	private String submsp_hyperv_hypervisor_id;
	private String submsp_vmware_hypervisor_id;
	
	private String direct_hyperv_policy_id;
	private String direct_vmware_policy_id;
	private String sub_hyperv_policy_id;
	private String sub_vmware_policy_id;
	private String submsp_hyperv_policy_id;
	private String submsp_vmware_policy_id;
	
	private String prefix = null;
	private Response response;
	
	LocalDate date = LocalDate.now();  
	LocalDate yesterday = date.minusDays(1);  
	LocalDate tomorrow = yesterday.plusDays(2);
	
	ArrayList<String> columnIdList = new ArrayList<String>();
	ArrayList<HashMap<String,Object>> columnsHeadContent = new ArrayList<HashMap<String,Object>>();
	
	private String  org_model_prefix=this.getClass().getSimpleName();
	private ArrayList<HashMap<String, Object>> expectedInfo = new ArrayList<>();
	private ArrayList<String> policies = new ArrayList<>();
	private TestOrgInfo ti;
	private Object direct_destination_ID;
	private Object sub_orga_destination_ID;
	private Object submsp_sub_org_destination_ID;
	private Object direct_destination_name;
	private Object sub_orga_destination_name;
	private Object submsp_sub_org_destination_name;
	private String msp_cloud_token;
	
	@BeforeClass
	@Parameters({ "baseURI", "port","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port,String logFolder,String runningMachine,String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		spogHypervisorsServer = new SPOGHypervisorsServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		policy4SPOGServer = new Policy4SPOGServer(baseURI, port);
		rep = ExtentManager.getInstance("GetHypervisorByIdTest", logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Kanamarlapudi, Chandra Kanth";
		this.prefix = RandomStringUtils.randomAlphanumeric(8);
		
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
		direct_destination_name = response.then().extract().path("data[0].destination_name");

		response = spogDestinationServer.getDestinations(ti.root_msp1_suborg1_user1_token, "destination_type="+DestinationType.cloud_direct_volume, test);
		sub_orga_destination_ID = response.then().extract().path("data[0].destination_id");
		sub_orga_destination_name = response.then().extract().path("data[0].destination_name");

		response = spogDestinationServer.getDestinations(ti.msp1_submsp1_suborg1_user1_token, "destination_type="+DestinationType.cloud_direct_volume, test);
		submsp_sub_org_destination_ID = response.then().extract().path("data[0].destination_id");
		submsp_sub_org_destination_name = response.then().extract().path("data[0].destination_name");
		
		//Creating SITE for the organization msp and use the token for posting hypervisors in sub msp's sub org
		spogServer.setToken(ti.root_msp_org1_user1_token);
		msp_cloud_id = gatewayServer.createsite_register_login(ti.root_msp_org1_id, ti.root_msp_org1_user1_token, ti.root_msp_org1_user1_id, "rakesh-host", "1.0.0", spogServer, test);
		msp_cloud_token = gatewayServer.getJWTToken();
	}
	
	@DataProvider(name="postHypervisorInfo")
	public Object[][] postHypervisorInfo(){
		return new Object[][] {
			{"Create hypervisor of type vmware in direct organization and check policy conf",ti.direct_org1_user1_token,
				ti.direct_org1_user1_token, "direct organization", ti.direct_org1_id, direct_cloud_id, direct_destination_ID, 
				direct_destination_name,spogServer.ReturnRandom("dir_vmw"), "vmware", "cloud_direct", "cloud_direct_hypervisor",
				"cloud_direct_vmware_backup", "cloud_direct_hyperv_backup"},
			{"Create hypervisor of type hyperv in direct organization and check policy conf",ti.direct_org1_user1_token,
					ti.direct_org1_user1_token, "direct organization", ti.direct_org1_id, direct_cloud_id, direct_destination_ID, 
					direct_destination_name,spogServer.ReturnRandom("dir_hyp"), "hyperv", "cloud_direct", "cloud_direct_hypervisor", 
					"cloud_direct_hyperv_backup", "cloud_direct_vmware_backup"},
			
			{"Create hypervisor of type vmware in submsp sub organization and check policy conf", ti.msp1_submsp1_suborg1_user1_token,
					msp_cloud_token, "sub msp sub organization", ti.msp1_submsp1_sub_org1_id, msp_cloud_id, submsp_sub_org_destination_ID, 
					submsp_sub_org_destination_name,spogServer.ReturnRandom("dir_vmw"), "vmware", "cloud_direct","cloud_direct_hypervisor", 
					"cloud_direct_vmware_backup", "cloud_direct_hyperv_backup"},
			{"Create hypervisor of type hyperv in submsp sub organization and check policy conf",ti.msp1_submsp1_suborg1_user1_token,
					msp_cloud_token, "sub msp sub organization", ti.msp1_submsp1_sub_org1_id, msp_cloud_id, submsp_sub_org_destination_ID, 
					submsp_sub_org_destination_name,spogServer.ReturnRandom("dir_hyp"), "hyperv", "cloud_direct","cloud_direct_hypervisor", 
					"cloud_direct_hyperv_backup", "cloud_direct_vmware_backup"},
			
			{"Create hypervisor of type vmware in sub organization and check policy conf",ti.root_msp1_suborg1_user1_token,
					msp_cloud_token, "sub organization", ti.root_msp1_suborg1_id, msp_cloud_id, sub_orga_destination_ID, 
					sub_orga_destination_name,spogServer.ReturnRandom("dir_vmw"), "vmware", "cloud_direct","cloud_direct_hypervisor", 
					"cloud_direct_vmware_backup", "cloud_direct_hyperv_backup"},
			{"Create hypervisor of type hyperv in sub organization and check policy conf",ti.root_msp1_suborg1_user1_token,
					msp_cloud_token, "sub organization", ti.root_msp1_suborg1_id, msp_cloud_id, sub_orga_destination_ID, 
					sub_orga_destination_name,spogServer.ReturnRandom("dir_hyp"), "hyperv", "cloud_direct","cloud_direct_hypervisor", 
					"cloud_direct_hyperv_backup", "cloud_direct_vmware_backup"},
			

		};
	}
	@Test(dataProvider="postHypervisorInfo", enabled=true)
	public void postHypervisorAndCheckPolicy(String caseType,
												String user_token,
												String site_token,												
												String orgType,
												String orgId,
												String siteId,
												String destinationId,
												String destination_name, 
												String hypervisorName,
												String hypervisorType,
												String hypervisorProduct,
												String policy_type,
												String task_type,
												String other_task_type
												) {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		test.assignAuthor("Rakesh.Chalamala");
		String hypervisorId = null;
		String policyId = null;
		String policyName = null;	
		String task_id = null;	
		String schedule_id = null;
		
		test.log(LogStatus.INFO, caseType);
		spogHypervisorsServer.setToken(site_token);
		Response response = spogHypervisorsServer.createHypervisor("none", hypervisorName, hypervisorType, hypervisorProduct,"none", siteId, orgId, 
													"false", String.valueOf(System.currentTimeMillis()), destinationId, "none", "0 0 * * *", "1d", 
													"agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true","apiversion", "vcenterHost",
													"pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter",
													"bind_host", test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		hypervisorId = response.then().extract().path("data.hypervisor_id");
		policyName = response.then().extract().path("data.policy.policy_name");
		policyId = checkPolicyOnCreateHypervisor(response, hypervisorName);
		policies.add(policyId);
		
		response = policy4SPOGServer.getPolicyById(user_token, policyId, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		response.then()
				.body("data.policy_name", equalTo(policyName))
				.body("data.policy_type", equalTo(policy_type))
				.body("data.organization_id", equalTo(orgId))
				.body("data.destinations[0].task_type", equalTo(task_type))
//				.body("data.destinations[0].task_id", equalTo(task_id))
				.body("data.destinations[0].destination_id", equalTo(destinationId))
				.body("data.hypervisor_id", equalTo(hypervisorId));
		task_id = response.then().extract().path("data.destinations[0].task_id");
		schedule_id = response.then().extract().path("data.schedules[0].schedule_id");
				
		policy4SPOGServer.setToken(user_token);
		response = policy4SPOGServer.getPolicies(user_token, "policy_name="+policyName);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		response.then()
				.body("data[0].policy_name", equalTo(policyName))
				.body("data[0].policy_type", equalTo(policy_type))
				.body("data[0].hypervisor.hypervisor_id", equalTo(hypervisorId));		
		
		//update policy
		HashMap<String, Object> cloudDirectScheduleDTO=policy4SPOGServer.createCloudDirectScheduleDTO("0 6 * * *", test);
		HashMap<String, Object> scheduleSettingDTO =policy4SPOGServer.createScheduleSettingDTO(cloudDirectScheduleDTO, null,test);
		ArrayList<HashMap<String, Object>> schedules = policy4SPOGServer.createPolicyScheduleDTO(null,schedule_id, 
				  											"1d", task_id, destinationId, scheduleSettingDTO, "06:00",
				  											"12:00", task_type ,destination_name,test);
		
		ArrayList<HashMap<String,Object>>  destinations = policy4SPOGServer.createPolicyTaskDTO(null, task_id, task_type, destinationId, "none", null, null, null, test);
		
		destinations.get(0).put(task_type, new HashMap<>());
		
		response = policy4SPOGServer.updatePolicy(policyName, null, policy_type, null, "false", "none", destinations, schedules, null, policyId, orgId, user_token,hypervisorId, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		response.then()
				.body("data.policy_name", equalTo(hypervisorName))
				.body("data.policy_type", equalTo(policy_type))
				.body("data.organization_id", equalTo(orgId))
				.body("data.destinations[0].task_type", equalTo(task_type))
				.body("data.destinations[0].task_id", equalTo(task_id))
				.body("data.destinations[0].destination_id", equalTo(destinationId))
				.body("data.hypervisor_id", equalTo(hypervisorId));
		
		destinations = policy4SPOGServer.createPolicyTaskDTO(null, task_id, other_task_type, destinationId, "none", null, null, null, test);
		destinations.get(0).put(other_task_type, new HashMap<>());
		response = policy4SPOGServer.updatePolicy(policyName, null, policy_type, null, "false", "none", destinations, schedules, null, policyId, orgId, user_token,hypervisorId, test);
		spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST);
				
		spogHypervisorsServer.deleteHypervisorWithCheck(hypervisorId, test);
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

	public String checkPolicyOnCreateHypervisor(Response response, String hypervisorName){
		
		assertNotNull(response.then().extract().path("data.policy.policy_id"));
//		assertTrue(response.then().extract().path("data.policy.policy_name").toString().equalsIgnoreCase(hypervisorName+" policy")); 	Bug: 889038
		assertTrue(response.then().extract().path("data.policy.policy_name").toString().equalsIgnoreCase(hypervisorName));
			
		return response.then().extract().path("data.policy.policy_id");
	}
}
