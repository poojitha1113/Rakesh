package api.hypervisor;

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
import com.steadystate.css.parser.selectors.DirectAdjacentSelectorImpl;

import Constants.DestinationStatus;
import Constants.DestinationType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGHypervisorsServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class GetHypervisorsAmount extends base.prepare.Is4Org {

	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private SPOGHypervisorsServer spogHypervisorsServer;
	private SPOGDestinationServer spogDestinationServer;
	private UserSpogServer userSpogServer;
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
	private String direct_baas_destionation_ID;
	private String msp_baas_destionation_ID;
	private String sub_orga_baas_destionation_ID;
	private String submsp_suborg_baas_destionation_ID;
	private String cloudAccountSecret;
	private String[] direct_hyperv_hypervisor_id = new String[10];
	private String[] direct_vmware_hypervisor_id = new String[10];
	private String[] submsp_suborg_hyperv_hypervisor_id = new String[10];
	private String[] submsp_suborg_vmware_hypervisor_id = new String[10];
	private String[] sub_hyperv_hypervisor_id = new String[10];
	private String[] sub_vmware_hypervisor_id = new String[10];
	private int direct_hyperviosr_flag = 0;
	private int msp_hypervisor_flag = 0;
	private int sub_hypervisor_flag = 0;

	int expDirectHypervisorsAmount = 0;
	int expSuborgHypervisorsAmount = 0;
	int expSubMspSuborgHypervisorsAmount = 0;
	private String prefix;
	private Response response;

	LocalDate date = LocalDate.now();  
	LocalDate yesterday = date.minusDays(1);  
	LocalDate tomorrow = yesterday.plusDays(2);

	private String  org_model_prefix=this.getClass().getSimpleName();
	private String[] datacenters;
	private String direct_cloud_id;
	private String msp_cloud_id;
	private String msp_cloud_token;
	private TestOrgInfo ti;
	

	@BeforeClass
	@Parameters({ "baseURI", "port","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port,String logFolder,String runningMachine,String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		spogHypervisorsServer = new SPOGHypervisorsServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		rep = ExtentManager.getInstance("GetHypervisorsAmount", logFolder);
		test = rep.startTest("Setup for GetHypervisorsAmount");
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
		
		response = spogServer.getCloudAccounts(ti.direct_org1_user1_token, "", test);
		direct_cloud_id = response.then().extract().path("data[0].cloud_account_id");

		/*response = spogServer.getCloudAccounts(ti.root_msp_org1_user1_token, "", test);
		msp_cloud_id = response.then().extract().path("data[0].cloud_account_id");*/
		
		//Creating SITE for the organization msp and use the token for posting hypervisors in sub msp's sub org
		spogServer.setToken(ti.root_msp_org1_user1_token);
		msp_cloud_id = gatewayServer.createsite_register_login(ti.root_msp_org1_id, ti.root_msp_org1_user1_token, ti.root_msp_org1_user1_id, "rakesh-host", "1.0.0", spogServer, test);
		msp_cloud_token = gatewayServer.getJWTToken();
			
		spogDestinationServer.setToken(ti.csr_token);
		datacenters = spogDestinationServer.getDestionationDatacenterID();
		
		response = spogDestinationServer.getDestinations(ti.direct_org1_user1_token, "destination_type="+DestinationType.cloud_direct_volume, test);
		direct_baas_destionation_ID = response.then().extract().path("data[0].destination_id");
				
		response = spogDestinationServer.getDestinations(ti.root_msp1_suborg1_user1_token, "destination_type="+DestinationType.cloud_direct_volume, test);
		sub_orga_baas_destionation_ID = response.then().extract().path("data[0].destination_id");
		
		response = spogDestinationServer.getDestinations(ti.msp1_submsp1_suborg1_user1_token, "destination_type="+DestinationType.cloud_direct_volume, test);
		submsp_suborg_baas_destionation_ID = response.then().extract().path("data[0].destination_id");

		System.out.println("Create hypervisors in direct org");
		createHypervisorsInDirectOrg();
		
		System.out.println("Create hypervisors in suborg");
		createHypervisorsInSuborg();
		
		System.out.println("Create hypervisors in sub msp's suborg");
		createHypervisorsInSubMspSuborg();
	}

	
	public void createHypervisorsInDirectOrg() {
		
		//Create hypervisors with direct cloud account token
		spogHypervisorsServer.setToken(ti.direct_org1_user1_token);
		this.direct_hyperv_hypervisor_id[0] = spogHypervisorsServer.createHypervisorWithCheck(UUID.randomUUID().toString(), "hyperv_hypervisor_name"+prefix, "hyperv", "cloud_direct", "none", direct_cloud_id, ti.direct_org1_id, 
				"false", String.valueOf(System.currentTimeMillis()), direct_baas_destionation_ID, "none", "0 0 * * *", "1d", 
				"agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", test);
		
		this.direct_vmware_hypervisor_id[0] = spogHypervisorsServer.createHypervisorWithCheck(UUID.randomUUID().toString(), "vmware_hypervisor_name"+prefix, "vmware", "cloud_direct", "none", direct_cloud_id, ti.direct_org1_id, 
				"false", String.valueOf(System.currentTimeMillis()), direct_baas_destionation_ID, "none", "0 0 * * *", "1d", 
				"agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", test);
		
		this.direct_hyperv_hypervisor_id[1] = spogHypervisorsServer.createHypervisorWithCheck(UUID.randomUUID().toString(), "hyperv_hypervisor_name1"+prefix, "hyperv", "cloud_direct", "none", direct_cloud_id, ti.direct_org1_id, 
				"false", String.valueOf(System.currentTimeMillis()), direct_baas_destionation_ID, "none", "0 0 * * *", "1d", 
				"agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", test);
		this.direct_vmware_hypervisor_id[1] = spogHypervisorsServer.createHypervisorWithCheck(UUID.randomUUID().toString(), "vmware_hypervisor_name1"+prefix, "vmware", "cloud_direct", "none", direct_cloud_id, ti.direct_org1_id, 
				"false", String.valueOf(System.currentTimeMillis()), direct_baas_destionation_ID, "none", "0 0 * * *", "1d", 
				"agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", test);
		
		expDirectHypervisorsAmount = spogHypervisorsServer.getHypervisorComposeInfo().size();
	}
	
	public void createHypervisorsInSuborg() {
		
		//Create hypervisors with msp cloud account token
		spogHypervisorsServer.setToken(ti.root_msp_org1_user1_token);
		this.sub_hyperv_hypervisor_id[0] = spogHypervisorsServer.createHypervisorWithCheck(UUID.randomUUID().toString(), "hyperv_hypervisor_name"+prefix, "hyperv", "cloud_direct", "none", msp_cloud_id, ti.root_msp1_suborg1_id, 
				"false", String.valueOf(System.currentTimeMillis()), sub_orga_baas_destionation_ID, "none", "0 0 * * *", "1d", 
				"agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", test);
		this.sub_vmware_hypervisor_id[0] = spogHypervisorsServer.createHypervisorWithCheck(UUID.randomUUID().toString(), "vmware_hypervisor_name"+prefix, "vmware", "cloud_direct", "none", msp_cloud_id, ti.root_msp1_suborg1_id, 
				"false", String.valueOf(System.currentTimeMillis()), sub_orga_baas_destionation_ID, "none", "0 0 * * *", "1d", 
				"agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", test);
		
		this.sub_hyperv_hypervisor_id[1] = spogHypervisorsServer.createHypervisorWithCheck(UUID.randomUUID().toString(), "hyperv_hypervisor_name1"+prefix, "hyperv", "cloud_direct", "none", msp_cloud_id, ti.root_msp1_suborg1_id, 
				"false", String.valueOf(System.currentTimeMillis()), sub_orga_baas_destionation_ID, "none", "0 0 * * *", "1d", 
				"agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", test);
		this.sub_vmware_hypervisor_id[1] = spogHypervisorsServer.createHypervisorWithCheck(UUID.randomUUID().toString(), "vmware_hypervisor_name1"+prefix, "vmware", "cloud_direct", "none", msp_cloud_id, ti.root_msp1_suborg1_id, 
				"false", String.valueOf(System.currentTimeMillis()), sub_orga_baas_destionation_ID, "none", "0 0 * * *", "1d", 
				"agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", test);
		
		expSuborgHypervisorsAmount = spogHypervisorsServer.getHypervisorComposeInfo().size() - expDirectHypervisorsAmount; // Excluding the count of direct hypervisor created
	}

	public void createHypervisorsInSubMspSuborg() {

		spogHypervisorsServer.setToken(msp_cloud_token);		
		this.submsp_suborg_hyperv_hypervisor_id[0] = spogHypervisorsServer.createHypervisorWithCheck(UUID.randomUUID().toString(), "hyperv_hypervisor_name"+prefix, "hyperv", "cloud_direct", "none", msp_cloud_id, ti.msp1_submsp1_sub_org1_id, 
				"false", String.valueOf(System.currentTimeMillis()), submsp_suborg_baas_destionation_ID, "none", "0 0 * * *", "1d", 
				"agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", test);
		this.submsp_suborg_vmware_hypervisor_id[0] = spogHypervisorsServer.createHypervisorWithCheck(UUID.randomUUID().toString(), "vmware_hypervisor_name"+prefix, "vmware", "cloud_direct", "none", msp_cloud_id, ti.msp1_submsp1_sub_org1_id, 
				"false", String.valueOf(System.currentTimeMillis()), submsp_suborg_baas_destionation_ID, "none", "0 0 * * *", "1d", 
				"agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", test);

		this.submsp_suborg_hyperv_hypervisor_id[1] = spogHypervisorsServer.createHypervisorWithCheck(UUID.randomUUID().toString(), "hyperv_hypervisor_name1"+prefix, "hyperv", "cloud_direct", "none", msp_cloud_id, ti.msp1_submsp1_sub_org1_id, 
				"false", String.valueOf(System.currentTimeMillis()), submsp_suborg_baas_destionation_ID, "none", "0 0 * * *", "1d", 
				"agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", test);
		this.submsp_suborg_vmware_hypervisor_id[1] = spogHypervisorsServer.createHypervisorWithCheck(UUID.randomUUID().toString(), "vmware_hypervisor_name1"+prefix, "vmware", "cloud_direct", "none", msp_cloud_id, ti.msp1_submsp1_sub_org1_id, 
				"false", String.valueOf(System.currentTimeMillis()), submsp_suborg_baas_destionation_ID, "none", "0 0 * * *", "1d", 
				"agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", test);

		expSubMspSuborgHypervisorsAmount = spogHypervisorsServer.getHypervisorComposeInfo().size() - expDirectHypervisorsAmount-expSuborgHypervisorsAmount; // Excluding the count of direct hypervisor created
	}


	@DataProvider(name = "GetHypervisorsAmountTest")
	public Object[][] GetHypervisorsAmountTest() {
		return new Object[][] {

			{"csr", "Get hypervisors amount with csr token ",ti.csr_token, expDirectHypervisorsAmount + expSuborgHypervisorsAmount+expSubMspSuborgHypervisorsAmount},
			{"csr_readonly","Get hyprvisors amount with csr readonly user token",ti.csr_readonly_token, expDirectHypervisorsAmount + expSuborgHypervisorsAmount+expSubMspSuborgHypervisorsAmount},

			{"direct","Get hypervisors amount with direct user token",ti.direct_org1_user1_token, expDirectHypervisorsAmount},
			{"msp","Get hypervisors amount with msp user token",ti.root_msp_org1_user1_token, expSuborgHypervisorsAmount},
			{"suborg","Get hypervisors amount with sub org token ",ti.root_msp1_suborg1_user1_token, expSuborgHypervisorsAmount},
			{"msp_account_admin","Get hypervisors amount with msp account admin token ",ti.root_msp_org1_msp_accountadmin1_token, expSuborgHypervisorsAmount},
			{"submsp","Get hypervisors amount with sub msp user token",ti.root_msp1_submsp1_user1_token, expSubMspSuborgHypervisorsAmount},
			{"submspsuborg","Get hypervisors amount with sub msp sub org token ",ti.msp1_submsp1_suborg1_user1_token, expSubMspSuborgHypervisorsAmount},
			{"submsp_account_admin","Get hypervisors amount with sub msp account admin token ",ti.root_msp1_submsp1_account_admin_token, expSubMspSuborgHypervisorsAmount},
			
		};
	}

	@Test(dataProvider = "GetHypervisorsAmountTest",enabled=true)
	public void getHypervisorsAmountTest_200(String user, String caseType,String token,int exp_amount) {

		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());

		String expected_type = "Hypervisors";
		HashMap<String, Object> expected_response = new HashMap<>();

		//Composing the expected info
		expected_response.put("amount", exp_amount);
		expected_response.put("amount_type", expected_type);


		test.log(LogStatus.INFO, caseType);
		spogHypervisorsServer.setToken(token);
		spogHypervisorsServer.getHypervisorsAmount(expected_response,user, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

	}

	@Test(enabled=true)
	public void getHypervisorsTest_401() {

		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());

		spogHypervisorsServer.setToken("");

		test.log(LogStatus.INFO, "Get hypervisors Amount check");
		spogHypervisorsServer.getHypervisorsAmount(null,null, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);

		spogHypervisorsServer.setToken("invalid");

		test.log(LogStatus.INFO, "Get hypervisors Amount check");
		spogHypervisorsServer.getHypervisorsAmount(null,null, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);
	}

	@AfterClass
	public void deleteHyp() {
		spogHypervisorsServer.setToken(ti.direct_org1_user1_token);
		spogHypervisorsServer.deleteAllHypervisors(test);
		
		spogHypervisorsServer.setToken(ti.root_msp1_suborg1_user1_token);
		spogHypervisorsServer.deleteAllHypervisors(test);
		
		spogHypervisorsServer.setToken(ti.msp1_submsp1_suborg1_user1_token);
		spogHypervisorsServer.deleteAllHypervisors(test);
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
