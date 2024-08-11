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
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGHypervisorsServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class GetHypervisorByIdTest extends base.prepare.Is4Org {

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

	private String direct_baas_destionation_ID;
	private String submsp_suborg_baas_destionation_ID;
	private String sub_orga_baas_destionation_ID;
	private String cloudAccountSecret;
	private String direct_hyperv_hypervisor_id;
	private String direct_vmware_hypervisor_id;
	private String msp_hyperv_hypervisor_id;
	private String msp_vmware_hypervisor_id;
	private String sub_hyperv_hypervisor_id;
	private String submsp_hyperv_hypervisor_id;
	private String sub_vmware_hypervisor_id;
	private String submsp_vmware_hypervisor_id;
	private String prefix = null;
	
	private Response response;
	
	LocalDate date = LocalDate.now();  
	LocalDate yesterday = date.minusDays(1);  
	LocalDate tomorrow = yesterday.plusDays(2);
	
	ArrayList<String> columnIdList = new ArrayList<String>();
	ArrayList<HashMap<String,Object>> columnsHeadContent = new ArrayList<HashMap<String,Object>>();
	
	private String  org_model_prefix=this.getClass().getSimpleName();
	private TestOrgInfo ti;
	private String[] datacenters;
	private String msp_cloud_token;
	
	@BeforeClass
	@Parameters({ "baseURI", "port","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port,String logFolder,String runningMachine,String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		spogHypervisorsServer = new SPOGHypervisorsServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
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
		
		response = spogServer.getCloudAccounts(ti.direct_org1_user1_token, "", test);
		direct_cloud_id = response.then().extract().path("data[0].cloud_account_id");
	
		/*response = spogServer.getCloudAccounts(ti.root_msp_org1_user1_token, "", test);
		msp_cloud_id = response.then().extract().path("data[0].cloud_account_id");*/
		
		//Creating SITE for the organization msp and use the token for posting hypervisors in sub msp's sub org
		spogServer.setToken(ti.root_msp_org1_user1_token);
		msp_cloud_id = gatewayServer.createsite_register_login(ti.root_msp_org1_id, ti.root_msp_org1_user1_token, ti.root_msp_org1_user1_id, "rakesh-host", "1.0.0", spogServer, test);
		msp_cloud_token = gatewayServer.getJWTToken();
	}
	@Test
	public void postCloudAccountsAndCreateDestinations() {
		
		spogDestinationServer.setToken(ti.csr_token);
		datacenters = spogDestinationServer.getDestionationDatacenterID();
		
		response = spogDestinationServer.getDestinations(ti.direct_org1_user1_token, "destination_type="+DestinationType.cloud_direct_volume, test);
		direct_baas_destionation_ID = response.then().extract().path("data[0].destination_id");
				
		response = spogDestinationServer.getDestinations(ti.root_msp1_suborg1_user1_token, "destination_type="+DestinationType.cloud_direct_volume, test);
		sub_orga_baas_destionation_ID = response.then().extract().path("data[0].destination_id");
		
		response = spogDestinationServer.getDestinations(ti.msp1_submsp1_suborg1_user1_token, "destination_type="+DestinationType.cloud_direct_volume, test);
		submsp_suborg_baas_destionation_ID = response.then().extract().path("data[0].destination_id");

		
		spogHypervisorsServer.setToken(ti.direct_org1_user1_token);	
		this.direct_hyperv_hypervisor_id = spogHypervisorsServer.createHypervisorWithCheck("none", "hyperv_hypervisor_name_r"+prefix, "hyperv", "cloud_direct", "none", direct_cloud_id, ti.direct_org1_id, 
				"false", String.valueOf(System.currentTimeMillis()), direct_baas_destionation_ID, "none", "0 0 * * *", "1d", 
				"agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", test);
		this.direct_vmware_hypervisor_id = spogHypervisorsServer.createHypervisorWithCheck("none", "vmware_hypervisor_name_r"+prefix, "vmware", "cloud_direct", "none", direct_cloud_id, ti.direct_org1_id, 
				"false", String.valueOf(System.currentTimeMillis()), direct_baas_destionation_ID, "none", "0 0 * * *", "1d", 
				"agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", test);
		
		spogHypervisorsServer.setToken(msp_cloud_token);
		this.sub_hyperv_hypervisor_id = spogHypervisorsServer.createHypervisorWithCheck("none", "hyperv_hypervisor_name_r"+prefix, "hyperv", "cloud_direct", "none", msp_cloud_id, ti.root_msp1_suborg1_id, 
				"false", String.valueOf(System.currentTimeMillis()), sub_orga_baas_destionation_ID, "none", "0 0 * * *", "1d", 
				"agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", test);
		this.sub_vmware_hypervisor_id = spogHypervisorsServer.createHypervisorWithCheck("none", "vmware_hypervisor_name_r"+prefix, "vmware", "cloud_direct", "none", msp_cloud_id, ti.root_msp1_suborg1_id, 
				"false", String.valueOf(System.currentTimeMillis()), sub_orga_baas_destionation_ID, "none", "0 0 * * *", "1d", 
				"agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", test);
		
		spogHypervisorsServer.setToken(msp_cloud_token);
		this.submsp_hyperv_hypervisor_id = spogHypervisorsServer.createHypervisorWithCheck("none", "hyperv_hypervisor_name_r"+prefix, "hyperv", "cloud_direct", "none", msp_cloud_id, ti.msp1_submsp1_sub_org1_id, 
				"false", String.valueOf(System.currentTimeMillis()), submsp_suborg_baas_destionation_ID, "none", "0 0 * * *", "1d", 
				"agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", test);
		this.submsp_vmware_hypervisor_id = spogHypervisorsServer.createHypervisorWithCheck("none", "vmware_hypervisor_name_r"+prefix, "vmware", "cloud_direct", "none", msp_cloud_id, ti.msp1_submsp1_sub_org1_id, 
				"false", String.valueOf(System.currentTimeMillis()), submsp_suborg_baas_destionation_ID, "none", "0 0 * * *", "1d", 
				"agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", test);
	}
	
	@DataProvider(name = "get_hypervisor_params_valid")
	public final Object[][] get_hypervisor_params_valid() {
		return new Object[][] {
			{ ti.direct_org1_user1_token, direct_vmware_hypervisor_id, "vmware_hypervisor_name_r"+prefix, "vmware", "cloud_direct", "none", direct_cloud_id, ti.direct_org1_id, "false",  String.valueOf(System.currentTimeMillis()), 
					direct_baas_destionation_ID, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
					"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},
			{ ti.direct_org1_user1_token,direct_hyperv_hypervisor_id, "hyperv_hypervisor_name_r"+prefix, "hyperv", "cloud_direct", "none", direct_cloud_id, ti.direct_org1_id, "false", String.valueOf(System.currentTimeMillis()), 
					direct_baas_destionation_ID, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
					"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},
						
			{ ti.root_msp1_suborg1_user1_token, sub_hyperv_hypervisor_id, "hyperv_hypervisor_name_r"+prefix, "hyperv", "cloud_direct", "none", msp_cloud_id, ti.root_msp1_suborg1_id, "false", String.valueOf(System.currentTimeMillis()), 
					sub_orga_baas_destionation_ID, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
					"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},	
			{ ti.root_msp1_suborg1_user1_token, sub_vmware_hypervisor_id, "vmware_hypervisor_name_r"+prefix, "vmware", "cloud_direct", "none", msp_cloud_id, ti.root_msp1_suborg1_id, "false",  String.valueOf(System.currentTimeMillis()), 
					sub_orga_baas_destionation_ID, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
					"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},
			
			{ ti.root_msp_org1_user1_token, sub_hyperv_hypervisor_id, "hyperv_hypervisor_name_r"+prefix, "hyperv", "cloud_direct", "none", msp_cloud_id, ti.root_msp1_suborg1_id, "false", String.valueOf(System.currentTimeMillis()), 
					sub_orga_baas_destionation_ID, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
					"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},	
			{ ti.root_msp_org1_user1_token, sub_vmware_hypervisor_id, "vmware_hypervisor_name_r"+prefix, "vmware", "cloud_direct", "none", msp_cloud_id, ti.root_msp1_suborg1_id, "false",  String.valueOf(System.currentTimeMillis()), 
					sub_orga_baas_destionation_ID, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
					"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},	
			
			{ ti.root_msp_org1_msp_accountadmin1_token, sub_hyperv_hypervisor_id, "hyperv_hypervisor_name_r"+prefix, "hyperv", "cloud_direct", "none", msp_cloud_id, ti.root_msp1_suborg1_id, "false", String.valueOf(System.currentTimeMillis()), 
					sub_orga_baas_destionation_ID, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
					"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},	
			{ ti.root_msp_org1_msp_accountadmin1_token, sub_vmware_hypervisor_id, "vmware_hypervisor_name_r"+prefix, "vmware", "cloud_direct", "none", msp_cloud_id, ti.root_msp1_suborg1_id, "false",  String.valueOf(System.currentTimeMillis()), 
					sub_orga_baas_destionation_ID, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
					"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},	
			
			{ ti.msp1_submsp1_suborg1_user1_token, submsp_hyperv_hypervisor_id, "hyperv_hypervisor_name_r"+prefix, "hyperv", "cloud_direct", "none", msp_cloud_id, ti.msp1_submsp1_sub_org1_id, "false", String.valueOf(System.currentTimeMillis()), 
					submsp_suborg_baas_destionation_ID, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
					"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},		
			{ ti.msp1_submsp1_suborg1_user1_token, submsp_vmware_hypervisor_id, "vmware_hypervisor_name_r"+prefix, "vmware", "cloud_direct", "none", msp_cloud_id, ti.msp1_submsp1_sub_org1_id, "false",  String.valueOf(System.currentTimeMillis()), 
					submsp_suborg_baas_destionation_ID, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
					"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},
			
			{ ti.root_msp1_submsp1_user1_token, submsp_hyperv_hypervisor_id, "hyperv_hypervisor_name_r"+prefix, "hyperv", "cloud_direct", "none", msp_cloud_id, ti.msp1_submsp1_sub_org1_id, "false", String.valueOf(System.currentTimeMillis()), 
					submsp_suborg_baas_destionation_ID, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
					"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},		
			{ ti.root_msp1_submsp1_user1_token, submsp_vmware_hypervisor_id, "vmware_hypervisor_name_r"+prefix, "vmware", "cloud_direct", "none", msp_cloud_id, ti.msp1_submsp1_sub_org1_id, "false",  String.valueOf(System.currentTimeMillis()), 
					submsp_suborg_baas_destionation_ID, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
					"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},
				
			{ ti.root_msp1_submsp1_account_admin_token, submsp_hyperv_hypervisor_id, "hyperv_hypervisor_name_r"+prefix, "hyperv", "cloud_direct", "none", msp_cloud_id, ti.msp1_submsp1_sub_org1_id, "false", String.valueOf(System.currentTimeMillis()), 
					submsp_suborg_baas_destionation_ID, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
					"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},		
			{ ti.root_msp1_submsp1_account_admin_token, submsp_vmware_hypervisor_id, "vmware_hypervisor_name_r"+prefix, "vmware", "cloud_direct", "none", msp_cloud_id, ti.msp1_submsp1_sub_org1_id, "false",  String.valueOf(System.currentTimeMillis()), 
					submsp_suborg_baas_destionation_ID, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
					"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},
		
		};
		
	}
	
	@Test (dataProvider = "get_hypervisor_params_valid",dependsOnMethods= {"postCloudAccountsAndCreateDestinations"},enabled=true) 	
	public void getHypervisorByIdValidTestUserToken_200(String token, String hypervisor_id, String hypervisor_name, String hypervisor_type, String hypervisor_product, String status, 
			String site, String organization_id, String sync_discovered_vms, String vm_refresh_ts, String cloud_direct_baas_destination, String cloud_direct_draas_destination, String schedule, String schedule_type, 
			String agent_name, String agent_current_version, String agent_upgrade_version, String agent_upgrade_link, String agent_autoupgrade,
			String vmware_api_version, String vmware_vcenter_host, String vmware_resource_pools, String vmware_data_stores, String vmware_data_centers, String vmware_host_systems,
			String bind_datacenter, String bind_host) {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		test.assignAuthor("Rakesh.Chalamala");
		
		spogHypervisorsServer.setToken(token);		
		test.log(LogStatus.INFO, "Get hypervisors by id valid");
		spogHypervisorsServer.getHypervisorsByIdWithCheck(hypervisor_id, hypervisor_name, hypervisor_type, hypervisor_product, status, site, organization_id, 
				sync_discovered_vms, vm_refresh_ts, cloud_direct_baas_destination, cloud_direct_draas_destination, schedule, schedule_type, agent_name, 
				agent_current_version, agent_upgrade_version, agent_upgrade_link, agent_autoupgrade, vmware_api_version, vmware_vcenter_host, vmware_resource_pools,
				vmware_data_stores, vmware_data_centers, vmware_host_systems, bind_datacenter, bind_host, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		
		spogHypervisorsServer.setToken(ti.csr_token);
		spogHypervisorsServer.getHypervisorsByIdWithCheck(hypervisor_id, hypervisor_name, hypervisor_type, hypervisor_product, status, site, organization_id, 
				sync_discovered_vms, vm_refresh_ts, cloud_direct_baas_destination, cloud_direct_draas_destination, schedule, schedule_type, agent_name, 
				agent_current_version, agent_upgrade_version, agent_upgrade_link, agent_autoupgrade, vmware_api_version, vmware_vcenter_host, vmware_resource_pools,
				vmware_data_stores, vmware_data_centers, vmware_host_systems, bind_datacenter, bind_host, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		
		spogHypervisorsServer.setToken(ti.csr_readonly_token);
		spogHypervisorsServer.getHypervisorsByIdWithCheck(hypervisor_id, hypervisor_name, hypervisor_type, hypervisor_product, status, site, organization_id,
				sync_discovered_vms, vm_refresh_ts, cloud_direct_baas_destination, cloud_direct_draas_destination, schedule, schedule_type, agent_name,
				agent_current_version, agent_upgrade_version, agent_upgrade_link, agent_autoupgrade, vmware_api_version, vmware_vcenter_host, vmware_resource_pools,
				vmware_data_stores, vmware_data_centers, vmware_host_systems, bind_datacenter, bind_host, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		
	}
	
	
	@Test (dataProvider = "get_hypervisor_params_valid",dependsOnMethods= {"postCloudAccountsAndCreateDestinations"},enabled=true) 
	public void getHypervisorByIdTest_400(String token, String hypervisor_id, String hypervisor_name, String hypervisor_type, String hypervisor_product, String status, 
			String site, String organization_id, String sync_discovered_vms, String vm_refresh_ts, String cloud_direct_baas_destination, String cloud_direct_draas_destination, String schedule, String schedule_type, 
			String agent_name, String agent_current_version, String agent_upgrade_version, String agent_upgrade_link, String agent_autoupgrade,
			String vmware_api_version, String vmware_vcenter_host, String vmware_resource_pools, String vmware_data_stores, String vmware_data_centers, String vmware_host_systems,
			String bind_datacenter, String bind_host) {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		test.assignAuthor("Rakesh.Chalamala");
		spogHypervisorsServer.setToken(token);
		
//		test.log(LogStatus.INFO, "Get hypervisors by id with no hypervisor id" );
//		spogHypervisorsServer.getHypervisorsByIdWithCheck("", hypervisor_name, hypervisor_type, hypervisor_product, status, site, organization_id, sync_discovered_vms, vm_refresh_ts, cloud_direct_baas_destination, cloud_direct_draas_destination, schedule, schedule_type, agent_name, agent_current_version, agent_upgrade_version, agent_upgrade_link, agent_autoupgrade, vmware_api_version, vmware_vcenter_host, vmware_resource_pools, vmware_data_stores, vmware_data_centers, vmware_host_systems, bind_datacenter, bind_host, SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID, test);
		
		test.log(LogStatus.INFO, "Get hypervisors by id using null as hypervisor id" );
		spogHypervisorsServer.getHypervisorsByIdWithCheck(null, hypervisor_name, hypervisor_type, hypervisor_product, status, site, organization_id, sync_discovered_vms, vm_refresh_ts, cloud_direct_baas_destination, cloud_direct_draas_destination, schedule, schedule_type, agent_name, agent_current_version, agent_upgrade_version, agent_upgrade_link, agent_autoupgrade, vmware_api_version, vmware_vcenter_host, vmware_resource_pools, vmware_data_stores, vmware_data_centers, vmware_host_systems, bind_datacenter, bind_host, SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_HYPERVISORID_IS_NOT_A_UUID, test);
		
		test.log(LogStatus.INFO, "Get hypervisors by id using invalid hypervisor id" );
		spogHypervisorsServer.getHypervisorsByIdWithCheck("invalid", hypervisor_name, hypervisor_type, hypervisor_product, status, site, organization_id, sync_discovered_vms, vm_refresh_ts, cloud_direct_baas_destination, cloud_direct_draas_destination, schedule, schedule_type, agent_name, agent_current_version, agent_upgrade_version, agent_upgrade_link, agent_autoupgrade, vmware_api_version, vmware_vcenter_host, vmware_resource_pools, vmware_data_stores, vmware_data_centers, vmware_host_systems, bind_datacenter, bind_host, SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_HYPERVISORID_IS_NOT_A_UUID, test);
	
		spogHypervisorsServer.setToken(ti.csr_token);
		test.log(LogStatus.INFO, "Get hypervisors by id using null as hypervisor id and ti.csr_token" );
		spogHypervisorsServer.getHypervisorsByIdWithCheck(null, hypervisor_name, hypervisor_type, hypervisor_product, status, site, organization_id, sync_discovered_vms, vm_refresh_ts, cloud_direct_baas_destination, cloud_direct_draas_destination, schedule, schedule_type, agent_name, agent_current_version, agent_upgrade_version, agent_upgrade_link, agent_autoupgrade, vmware_api_version, vmware_vcenter_host, vmware_resource_pools, vmware_data_stores, vmware_data_centers, vmware_host_systems, bind_datacenter, bind_host, SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_HYPERVISORID_IS_NOT_A_UUID, test);
		
		test.log(LogStatus.INFO, "Get hypervisors by id using invalid hypervisor id and ti.csr_token" );
		spogHypervisorsServer.getHypervisorsByIdWithCheck("invalid", hypervisor_name, hypervisor_type, hypervisor_product, status, site, organization_id, sync_discovered_vms, vm_refresh_ts, cloud_direct_baas_destination, cloud_direct_draas_destination, schedule, schedule_type, agent_name, agent_current_version, agent_upgrade_version, agent_upgrade_link, agent_autoupgrade, vmware_api_version, vmware_vcenter_host, vmware_resource_pools, vmware_data_stores, vmware_data_centers, vmware_host_systems, bind_datacenter, bind_host, SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_HYPERVISORID_IS_NOT_A_UUID, test);
		
		spogHypervisorsServer.setToken(ti.csr_readonly_token);
		test.log(LogStatus.INFO, "Get hypervisors by id using null as hypervisor id and ti.csr_readonly_token" );
		spogHypervisorsServer.getHypervisorsByIdWithCheck(null, hypervisor_name, hypervisor_type, hypervisor_product, status, site, organization_id, sync_discovered_vms, vm_refresh_ts, cloud_direct_baas_destination, cloud_direct_draas_destination, schedule, schedule_type, agent_name, agent_current_version, agent_upgrade_version, agent_upgrade_link, agent_autoupgrade, vmware_api_version, vmware_vcenter_host, vmware_resource_pools, vmware_data_stores, vmware_data_centers, vmware_host_systems, bind_datacenter, bind_host, SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_HYPERVISORID_IS_NOT_A_UUID, test);
		
		test.log(LogStatus.INFO, "Get hypervisors by id using invalid hypervisor id and ti.csr_readonly_token" );
		spogHypervisorsServer.getHypervisorsByIdWithCheck("invalid", hypervisor_name, hypervisor_type, hypervisor_product, status, site, organization_id, sync_discovered_vms, vm_refresh_ts, cloud_direct_baas_destination, cloud_direct_draas_destination, schedule, schedule_type, agent_name, agent_current_version, agent_upgrade_version, agent_upgrade_link, agent_autoupgrade, vmware_api_version, vmware_vcenter_host, vmware_resource_pools, vmware_data_stores, vmware_data_centers, vmware_host_systems, bind_datacenter, bind_host, SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_HYPERVISORID_IS_NOT_A_UUID, test);
	
	}
	
	@Test (dataProvider = "get_hypervisor_params_valid",dependsOnMethods= {"postCloudAccountsAndCreateDestinations"},enabled=true) 
	public void getHypervisorByIdTest_401(String token, String hypervisor_id, String hypervisor_name, String hypervisor_type, String hypervisor_product, String status, 
			String site, String organization_id, String sync_discovered_vms, String vm_refresh_ts, String cloud_direct_baas_destination, String cloud_direct_draas_destination, String schedule, String schedule_type, 
			String agent_name, String agent_current_version, String agent_upgrade_version, String agent_upgrade_link, String agent_autoupgrade,
			String vmware_api_version, String vmware_vcenter_host, String vmware_resource_pools, String vmware_data_stores, String vmware_data_centers, String vmware_host_systems,
			String bind_datacenter, String bind_host) {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		test.assignAuthor("Rakesh.Chalamala");
		
		spogHypervisorsServer.setToken("");
		test.log(LogStatus.INFO, "Get hypervisor by id with missing token");
		spogHypervisorsServer.getHypervisorsByIdWithCheck(hypervisor_id, hypervisor_name, hypervisor_type, hypervisor_product, status, site, organization_id, sync_discovered_vms, vm_refresh_ts, cloud_direct_baas_destination, cloud_direct_draas_destination, schedule, schedule_type, agent_name, agent_current_version, agent_upgrade_version, agent_upgrade_link, agent_autoupgrade, vmware_api_version, vmware_vcenter_host, vmware_resource_pools, vmware_data_stores, vmware_data_centers, vmware_host_systems, bind_datacenter, bind_host, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);
		
		spogHypervisorsServer.setToken("invalid");
		test.log(LogStatus.INFO, "Get hypervisor by id with invalid token");
		spogHypervisorsServer.getHypervisorsByIdWithCheck(hypervisor_id, hypervisor_name, hypervisor_type, hypervisor_product, status, site, organization_id, sync_discovered_vms, vm_refresh_ts, cloud_direct_baas_destination, cloud_direct_draas_destination, schedule, schedule_type, agent_name, agent_current_version, agent_upgrade_version, agent_upgrade_link, agent_autoupgrade, vmware_api_version, vmware_vcenter_host, vmware_resource_pools, vmware_data_stores, vmware_data_centers, vmware_host_systems, bind_datacenter, bind_host, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);
	}
	
	@DataProvider(name = "get_hypervisor_otherorgT")
	public final Object[][] get_hypervisor_otherorgT(){
		return new Object[][] {
			{ "direct-mspT",direct_vmware_hypervisor_id,ti.root_msp_org1_user1_token, "vmware_hypervisor_name_r"+prefix, "vmware", "cloud_direct", "none", direct_cloud_id, ti.direct_org1_id, "false",  String.valueOf(System.currentTimeMillis()), 
				direct_baas_destionation_ID, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},
			{ "direct-subT",direct_hyperv_hypervisor_id,ti.root_msp1_suborg1_user1_token, "hyperv_hypervisor_name_r"+prefix, "hyperv", "cloud_direct", "none", direct_cloud_id, ti.direct_org1_id, "false", String.valueOf(System.currentTimeMillis()), 
					direct_baas_destionation_ID, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
					"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},
			{ "direct-mspAccAdminT",direct_vmware_hypervisor_id,ti.root_msp_org1_msp_accountadmin1_token, "vmware_hypervisor_name_r"+prefix, "vmware", "cloud_direct", "none", direct_cloud_id, ti.direct_org1_id, "false",  String.valueOf(System.currentTimeMillis()), 
						direct_baas_destionation_ID, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
						"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},
			{ "direct-submspT",direct_hyperv_hypervisor_id,ti.root_msp1_submsp1_user1_token, "hyperv_hypervisor_name_r"+prefix, "hyperv", "cloud_direct", "none", direct_cloud_id, ti.direct_org1_id, "false", String.valueOf(System.currentTimeMillis()), 
							direct_baas_destionation_ID, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
							"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},
			{ "direct-mspT",direct_vmware_hypervisor_id,ti.msp1_submsp1_suborg1_user1_token, "vmware_hypervisor_name_r"+prefix, "vmware", "cloud_direct", "none", direct_cloud_id, ti.direct_org1_id, "false",  String.valueOf(System.currentTimeMillis()), 
								direct_baas_destionation_ID, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
								"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},
			{ "direct-subT",direct_hyperv_hypervisor_id,ti.root_msp1_submsp1_account_admin_token, "hyperv_hypervisor_name_r"+prefix, "hyperv", "cloud_direct", "none", direct_cloud_id, ti.direct_org1_id, "false", String.valueOf(System.currentTimeMillis()), 
									direct_baas_destionation_ID, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
									"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},

			
			{ "sub-directT",sub_vmware_hypervisor_id,ti.direct_org1_user1_token, "vmware_hypervisor_name_r"+prefix, "vmware", "cloud_direct", "none", msp_cloud_id, ti.root_msp1_suborg1_id, "false",  String.valueOf(System.currentTimeMillis()), 
						sub_orga_baas_destionation_ID, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
						"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},
			{ "sub-msp2T",sub_hyperv_hypervisor_id,ti.normal_msp_org2_user1_token, "hyperv_hypervisor_name_r"+prefix, "hyperv", "cloud_direct", "none", msp_cloud_id, ti.root_msp1_suborg1_id, "false", String.valueOf(System.currentTimeMillis()), 
						sub_orga_baas_destionation_ID, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
						"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},
			{ "sub-sub2T",sub_vmware_hypervisor_id,ti.root_msp1_submsp2_user1_token, "vmware_hypervisor_name_r"+prefix, "vmware", "cloud_direct", "none", msp_cloud_id, ti.root_msp1_suborg1_id, "false",  String.valueOf(System.currentTimeMillis()), 
						sub_orga_baas_destionation_ID, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
						"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},
			{ "sub-submspT",sub_hyperv_hypervisor_id,ti.root_msp1_submsp1_user1_token, "hyperv_hypervisor_name_r"+prefix, "hyperv", "cloud_direct", "none", msp_cloud_id, ti.root_msp1_suborg1_id, "false", String.valueOf(System.currentTimeMillis()), 
						sub_orga_baas_destionation_ID, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
						"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},
			{ "sub-submspsuborgT",sub_vmware_hypervisor_id,ti.msp1_submsp1_suborg1_user1_token, "vmware_hypervisor_name_r"+prefix, "vmware", "cloud_direct", "none", msp_cloud_id, ti.root_msp1_suborg1_id, "false",  String.valueOf(System.currentTimeMillis()), 
						sub_orga_baas_destionation_ID, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
						"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},
			{ "sub-submspAAT",sub_hyperv_hypervisor_id,ti.root_msp1_submsp1_account_admin_token, "hyperv_hypervisor_name_r"+prefix, "hyperv", "cloud_direct", "none", msp_cloud_id, ti.root_msp1_suborg1_id, "false", String.valueOf(System.currentTimeMillis()), 
						sub_orga_baas_destionation_ID, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
						"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},
			
			{ "submsp-directT",submsp_vmware_hypervisor_id,ti.direct_org1_user1_token, "vmware_hypervisor_name_r"+prefix, "vmware", "cloud_direct", "none", msp_cloud_id, ti.root_msp1_suborg1_id, "false",  String.valueOf(System.currentTimeMillis()), 
						sub_orga_baas_destionation_ID, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
						"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},
			{ "submsp-msp2T",submsp_hyperv_hypervisor_id,ti.normal_msp_org2_user1_token, "hyperv_hypervisor_name_r"+prefix, "hyperv", "cloud_direct", "none", msp_cloud_id, ti.root_msp1_suborg1_id, "false", String.valueOf(System.currentTimeMillis()), 
						sub_orga_baas_destionation_ID, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
						"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},
			{ "submsp-sub2T",submsp_vmware_hypervisor_id,ti.root_msp1_suborg2_user1_token, "vmware_hypervisor_name_r"+prefix, "vmware", "cloud_direct", "none", msp_cloud_id, ti.root_msp1_suborg1_id, "false",  String.valueOf(System.currentTimeMillis()), 
						sub_orga_baas_destionation_ID, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
						"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},
			{ "submsp-submspT",submsp_hyperv_hypervisor_id,ti.root_msp1_submsp2_user1_token, "hyperv_hypervisor_name_r"+prefix, "hyperv", "cloud_direct", "none", msp_cloud_id, ti.root_msp1_suborg1_id, "false", String.valueOf(System.currentTimeMillis()), 
						sub_orga_baas_destionation_ID, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
						"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},
			{ "submsp-submspsuborgT",submsp_vmware_hypervisor_id,ti.msp1_submsp1_suborg2_user1_token, "vmware_hypervisor_name_r"+prefix, "vmware", "cloud_direct", "none", msp_cloud_id, ti.root_msp1_suborg1_id, "false",  String.valueOf(System.currentTimeMillis()), 
						sub_orga_baas_destionation_ID, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
						"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},
			{ "submsp-submspAAT",submsp_hyperv_hypervisor_id,ti.root_msp1_submsp2_account_admin_token, "hyperv_hypervisor_name_r"+prefix, "hyperv", "cloud_direct", "none", msp_cloud_id, ti.root_msp1_suborg1_id, "false", String.valueOf(System.currentTimeMillis()), 
						sub_orga_baas_destionation_ID, "none", "0 0 * * *", "1d", "agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
						"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host"},
		};
	}

	@Test (dataProvider = "get_hypervisor_otherorgT",dependsOnMethods= {"postCloudAccountsAndCreateDestinations"},enabled=true) 
	public void getHypervisorByIdTest_403(String OrgType,String hypervisor_id,String token, String hypervisor_name, String hypervisor_type, String hypervisor_product, String status, 
			String site, String organization_id, String sync_discovered_vms, String vm_refresh_ts, String cloud_direct_baas_destination, String cloud_direct_draas_destination, String schedule, String schedule_type, 
			String agent_name, String agent_current_version, String agent_upgrade_version, String agent_upgrade_link, String agent_autoupgrade,
			String vmware_api_version, String vmware_vcenter_host, String vmware_resource_pools, String vmware_data_stores, String vmware_data_centers, String vmware_host_systems,
			String bind_datacenter, String bind_host) {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		test.assignAuthor("Rakesh.Chalamala");
		
		spogHypervisorsServer.setToken(token);
		
		test.log(LogStatus.INFO, "Get hypervisor by id with invalid token");
		spogHypervisorsServer.getHypervisorsByIdWithCheck(hypervisor_id, hypervisor_name, hypervisor_type, hypervisor_product, status, site, organization_id, sync_discovered_vms, vm_refresh_ts, cloud_direct_baas_destination, cloud_direct_draas_destination, schedule, schedule_type, agent_name, agent_current_version, agent_upgrade_version, agent_upgrade_link, agent_autoupgrade, vmware_api_version, vmware_vcenter_host, vmware_resource_pools, vmware_data_stores, vmware_data_centers, vmware_host_systems, bind_datacenter, bind_host, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY, test);	
	}
	
	@Test (dataProvider = "get_hypervisor_params_valid",dependsOnMethods= {"postCloudAccountsAndCreateDestinations"},enabled=true) 
	public void getHypervisorByIdTest_404(String token,String hypervisor_id, String hypervisor_name, String hypervisor_type, String hypervisor_product, String status, 
			String site, String organization_id, String sync_discovered_vms, String vm_refresh_ts, String cloud_direct_baas_destination, String cloud_direct_draas_destination, String schedule, String schedule_type, 
			String agent_name, String agent_current_version, String agent_upgrade_version, String agent_upgrade_link, String agent_autoupgrade,
			String vmware_api_version, String vmware_vcenter_host, String vmware_resource_pools, String vmware_data_stores, String vmware_data_centers, String vmware_host_systems,
			String bind_datacenter, String bind_host) {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		test.assignAuthor("Rakesh.Chalamala");
		spogHypervisorsServer.setToken(token);
				
		test.log(LogStatus.INFO, "Get hypervisors by id using random hypervisor id" );
		spogHypervisorsServer.getHypervisorsByIdWithCheck(UUID.randomUUID().toString(), hypervisor_name, hypervisor_type, hypervisor_product, status, site, organization_id, sync_discovered_vms, vm_refresh_ts, cloud_direct_baas_destination, cloud_direct_draas_destination, schedule, schedule_type, agent_name, agent_current_version, agent_upgrade_version, agent_upgrade_link, agent_autoupgrade, vmware_api_version, vmware_vcenter_host, vmware_resource_pools, vmware_data_stores, vmware_data_centers, vmware_host_systems, bind_datacenter, bind_host, SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.THE_HYPERVISOR_WITH_ID_DOES_NOT_EXIST_OR_HAS_BEEN_DELETED, test);
		
		spogHypervisorsServer.setToken(ti.csr_token);
		test.log(LogStatus.INFO, "Get hypervisors by id using random hypervisor id and ti.csr_token" );
		spogHypervisorsServer.getHypervisorsByIdWithCheck(UUID.randomUUID().toString(), hypervisor_name, hypervisor_type, hypervisor_product, status, site, organization_id, sync_discovered_vms, vm_refresh_ts, cloud_direct_baas_destination, cloud_direct_draas_destination, schedule, schedule_type, agent_name, agent_current_version, agent_upgrade_version, agent_upgrade_link, agent_autoupgrade, vmware_api_version, vmware_vcenter_host, vmware_resource_pools, vmware_data_stores, vmware_data_centers, vmware_host_systems, bind_datacenter, bind_host, SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.THE_HYPERVISOR_WITH_ID_DOES_NOT_EXIST_OR_HAS_BEEN_DELETED, test);
		
		spogHypervisorsServer.setToken(ti.csr_readonly_token);
		test.log(LogStatus.INFO, "Get hypervisors by id using random hypervisor id and ti.csr_readonly_token" );
		spogHypervisorsServer.getHypervisorsByIdWithCheck(UUID.randomUUID().toString(), hypervisor_name, hypervisor_type, hypervisor_product, status, site, organization_id, sync_discovered_vms, vm_refresh_ts, cloud_direct_baas_destination, cloud_direct_draas_destination, schedule, schedule_type, agent_name, agent_current_version, agent_upgrade_version, agent_upgrade_link, agent_autoupgrade, vmware_api_version, vmware_vcenter_host, vmware_resource_pools, vmware_data_stores, vmware_data_centers, vmware_host_systems, bind_datacenter, bind_host, SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.THE_HYPERVISOR_WITH_ID_DOES_NOT_EXIST_OR_HAS_BEEN_DELETED, test);
	
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
	public void deleteHyp() {
		spogHypervisorsServer.setToken(ti.direct_org1_user1_token);
		spogHypervisorsServer.deleteAllHypervisors(test);
		
		spogHypervisorsServer.setToken(ti.root_msp1_suborg1_user1_token);
		spogHypervisorsServer.deleteAllHypervisors(test);
		
		spogHypervisorsServer.setToken(ti.msp1_submsp1_suborg1_user1_token);
		spogHypervisorsServer.deleteAllHypervisors(test);
	}
}
