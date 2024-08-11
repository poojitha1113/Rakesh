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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

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

public class GetHypervisorsTest extends base.prepare.Is4Org {
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
    private String buildVersion;	*/
	
	private String direct_cloud_id;
	private String msp_cloud_id;
	private String msp_cloud_token;
	
	private String direct_baas_destionation_ID;
	private String submsp_suborg_baas_destionation_ID;
	private String sub_orga_baas_destionation_ID;
	private String cloudAccountSecret;
	private String[] direct_hyperv_hypervisor_id = new String[10];
	private String[] direct_vmware_hypervisor_id = new String[10];
	private String[] sub_hyperv_hypervisor_id = new String[10];
	private String[] sub_vmware_hypervisor_id = new String[10];
	private String[] submsp_suborg_hyperv_hypervisor_id = new String[10];
	private String[] submsp_suborg_vmware_hypervisor_id = new String[10];
	
	ArrayList<HashMap<String, Object>> expDirectHypervisorsInfo = new ArrayList<>();
	ArrayList<HashMap<String, Object>> expSuborgHypervisorsInfo = new ArrayList<>();
	ArrayList<HashMap<String, Object>> expSubMspSuborgHypervisorsInfo = new ArrayList<>();
	private String prefix;
	String[] datacenters;
	private Response response;
	
	LocalDate date = LocalDate.now();  
	LocalDate yesterday = date.minusDays(1);  
	LocalDate tomorrow = yesterday.plusDays(2);
	
	private String  org_model_prefix=this.getClass().getSimpleName();
	private TestOrgInfo ti;
	
	@BeforeClass
	@Parameters({ "baseURI", "port","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String logFolder,String runningMachine,String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		spogHypervisorsServer = new SPOGHypervisorsServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		rep = ExtentManager.getInstance("GetHypervisorsTest", logFolder);
		test = rep.startTest("Setup for GetHypervisorsTest");
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
		
		expDirectHypervisorsInfo.addAll(spogHypervisorsServer.getHypervisorComposeInfo());
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
		
		expSuborgHypervisorsInfo.addAll(spogHypervisorsServer.getHypervisorComposeInfo());
		//it has both sub and direct hyp info so removing direct hyp info
		expSuborgHypervisorsInfo.removeAll(expDirectHypervisorsInfo);
	}

	public void createHypervisorsInSubMspSuborg() {

		//Create hypervisors with msp cloud account token
//		spogHypervisorsServer.setToken(ti.root_msp1_submsp1_user1_token);
		
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

		expSubMspSuborgHypervisorsInfo.addAll(spogHypervisorsServer.getHypervisorComposeInfo());
		//it has both sub and direct hyp info so removing direct hyp info
		expSubMspSuborgHypervisorsInfo.removeAll(expDirectHypervisorsInfo);
		expSubMspSuborgHypervisorsInfo.removeAll(expSuborgHypervisorsInfo);
	}
	
	@DataProvider(name = "GetHypervisorsTest")
	public Object[][] GetHypervisorsTest() {
		return new Object[][] {
			{"Get hypervisors in direct organization filter by hypervisor_name & sort by create_ts asc",
				ti.direct_org1_user1_token,"hypervisor_name;=;hyperv_hypervisor_name"+prefix,"create_ts;asc",0,0,expDirectHypervisorsInfo},
			{"Get hypervisors in direct organization filter by hypervisor_type=vmware & sort by create_ts asc",
				ti.direct_org1_user1_token,"hypervisor_type;in;hyperv|vmware","create_ts;asc",0,0,expDirectHypervisorsInfo},
			{"Get hypervisors in direct organization filter by hypervisor_type=hyper_v & sort by create_ts asc",
				ti.direct_org1_user1_token,"hypervisor_type;=;hyperv","create_ts;desc",2,0,expDirectHypervisorsInfo},
			{"Get hypervisors in direct organization filter by hypervisor_type=vmware & sort by create_ts asc & pagination",
				ti.direct_org1_user1_token,"hypervisor_type;=;vmware","create_ts;asc",1,0,expDirectHypervisorsInfo},
			{"Get hypervisors in direct organization filter by organization_id & sort by create_ts asc",
				ti.direct_org1_user1_token,"organization_id;=;"+ti.direct_org1_id,"create_ts;asc",0,0,expDirectHypervisorsInfo},
			{"Get hypervisors in direct organization filter by hypervisor_product=cloud_direct & sort by create_ts desc",
				ti.direct_org1_user1_token,"hypervisor_product;=;cloud_direct","create_ts;asc",0,1,expDirectHypervisorsInfo},
			{"Get hypervisors in direct organization filter by site_id & sort by create_ts asc",
				ti.direct_org1_user1_token,"site_id;=;"+direct_cloud_id,"create_ts;desc",0,0,expDirectHypervisorsInfo},
			{"Get hypervisors in direct organization filter by hypervisor_type=hyper_v & hypervisor_name & sort by create_ts asc",
				ti.direct_org1_user1_token,"hypervisor_type;=;hyperv,hypervisor_name;=;hyperv_hypervisor_name"+prefix,"create_ts;asc",0,0,expDirectHypervisorsInfo},
			{"Get hypervisors in direct organization filter by hypervisor_type=hyper_v & hypervisor_name & organization_id & sort by create_ts asc",
				ti.direct_org1_user1_token,"hypervisor_type;=;hyperv,hypervisor_name;=;hyperv_hypervisor_name"+prefix+",organization_id;=;"+ti.direct_org1_id,"create_ts;asc",0,0,expDirectHypervisorsInfo},
			{"Get hypervisors in direct organization filter by hypervisor_type=hyper_v & hypervisor_name & organization_id & hypervisor_product & sort by create_ts asc",
				ti.direct_org1_user1_token,"hypervisor_type;=;hyperv,hypervisor_name;=;hyperv_hypervisor_name"+prefix+",organization_id;=;"+ti.direct_org1_id+",hypervisor_product;=;cloud_direct","create_ts;asc",0,0,expDirectHypervisorsInfo},
			{"Get hypervisors in direct organization filter by hypervisor_type=hyper_v & organization_id & sort by create_ts desc",
				ti.direct_org1_user1_token,"hypervisor_type;=;hyperv,organization_id;=;"+ti.direct_org1_id,"create_ts;desc",0,0,expDirectHypervisorsInfo},
			{"Get hypervisors in direct organization filter by hypervisor_type=hyper_v & organization_id & sort by create_ts asc",
				ti.direct_org1_user1_token,"hypervisor_type;=;vmware,organization_id;=;"+ti.direct_org1_id,"create_ts;asc",0,0,expDirectHypervisorsInfo},
			{"Get hypervisors in direct organization filter by hypervisor_product=cloud_direct & organization_id & sort by create_ts desc",
				ti.direct_org1_user1_token,"hypervisor_product;=;cloud_direct,organization_id;=;"+ti.direct_org1_id,"create_ts;desc",0,0,expDirectHypervisorsInfo},
			{"Get hypervisors in direct organization filter by site_id & organization_id & sort by create_ts desc",
				ti.direct_org1_user1_token,"site_id;=;"+direct_cloud_id+",organization_id;=;"+ti.direct_org1_id,"create_ts;desc",0,0,expDirectHypervisorsInfo},
			
			
			{"Get hypervisors in sub org filter by hypervisor_type & sort by create_ts desc with msp token",
				ti.root_msp_org1_user1_token,"hypervisor_type;=;hyperv","create_ts;desc",2,0, expSuborgHypervisorsInfo},
			{"Get hypervisors in sub org filter by hypervisor_type=vmware & sort by create_ts desc with msp token",
				ti.root_msp_org1_user1_token,"hypervisor_type;=;vmware","create_ts;desc",-1,0, expSuborgHypervisorsInfo},
			{"Get hypervisors in sub org filter by organization_id & sort by create_ts asc with msp token",
				ti.root_msp_org1_user1_token,"organization_id;=;"+ti.root_msp1_suborg1_id,"create_ts;asc",0,0, expSuborgHypervisorsInfo},
			{"Get hypervisors in sub org filter by hypervisor_product=cloud_sub_org & sort by create_ts desc with msp token",
				ti.root_msp_org1_user1_token,"hypervisor_product;=;cloud_sub_org","create_ts;desc",0,-1, expSuborgHypervisorsInfo},
			{"Get hypervisors in sub org filter by site_id & sort by create_ts desc with msp token",
				ti.root_msp_org1_user1_token,"site_id;=;"+msp_cloud_id,"create_ts;desc",0,0, expSuborgHypervisorsInfo},
			{"Get hypervisors in sub org filter by hypervisor_type=hyperv & hypervisor_name & sort by create_ts desc with msp token",
				ti.root_msp_org1_user1_token,"hypervisor_type;=;hyperv,hypervisor_name;=;hyperv_hypervisor_name","create_ts;asc",0,0, expSuborgHypervisorsInfo},
			{"Get hypervisors in sub org filter by hypervisor_type=hyper_v & organization_id & sort by create_ts desc with msp token",
				ti.root_msp_org1_user1_token,"hypervisor_type;=;hyperv,organization_id;=;"+ti.root_msp1_suborg1_id,"create_ts;desc",0,0, expSuborgHypervisorsInfo},
			{"Get hypervisors in sub org filter by hypervisor_type=vmware & organization_id & sort by create_ts desc with msp token",
				ti.root_msp_org1_user1_token,"hypervisor_type;=;vmware,organization_id;=;"+ti.root_msp1_suborg1_id,"create_ts;asc",0,0, expSuborgHypervisorsInfo},
			{"Get hypervisors in sub org filter by hypervisor_product & organization_id & sort by create_ts desc with msp token",
				ti.root_msp_org1_user1_token,"hypervisor_product;=;cloud_sub_org,organization_id;=;"+ti.root_msp1_suborg1_id,"create_ts;desc",0,0, expSuborgHypervisorsInfo},
			{"Get hypervisors in sub org filter by hypervisor_type=vmware & organization_id & sort by create_ts desc with msp token",
				ti.root_msp_org1_user1_token,"hypervisor_type;=;vmware,create_user_id;=;"+ti.root_msp1_suborg1_user1_id,"create_ts;desc",0,0, expSuborgHypervisorsInfo},
			{"Get hypervisors in sub org filter by site_id & organization_id & sort by create_ts desc with msp token",
				ti.root_msp_org1_user1_token,"site_id;=;"+msp_cloud_id+",organization_id;=;"+ti.root_msp1_suborg1_id,"create_ts;desc",0,0, expSuborgHypervisorsInfo},
			
			{"Get hypervisors in sub org filter by hypervisor_type & sort by create_ts desc with sub msp user token",
					ti.root_msp1_submsp1_user1_token,"hypervisor_type;=;hyperv","create_ts;desc",2,0, expSubMspSuborgHypervisorsInfo},
			{"Get hypervisors in sub org filter by hypervisor_type=vmware & sort by create_ts desc with sub msp user token",
					ti.root_msp1_submsp1_user1_token,"hypervisor_type;=;vmware","create_ts;desc",-1,0, expSubMspSuborgHypervisorsInfo},
			{"Get hypervisors in sub org filter by organization_id & sort by create_ts asc with sub msp user token",
					ti.root_msp1_submsp1_user1_token,"organization_id;=;"+ti.msp1_submsp1_sub_org1_id,"create_ts;asc",0,0, expSubMspSuborgHypervisorsInfo},
			{"Get hypervisors in sub org filter by hypervisor_product=cloud_sub_org & sort by create_ts desc with sub msp user token",
					ti.root_msp1_submsp1_user1_token,"hypervisor_product;=;cloud_sub_org","create_ts;desc",0,-1, expSubMspSuborgHypervisorsInfo},
			{"Get hypervisors in sub org filter by site_id & sort by create_ts desc with sub msp user token",
					ti.root_msp1_submsp1_user1_token,"site_id;=;"+msp_cloud_id,"create_ts;desc",0,0, expSubMspSuborgHypervisorsInfo},
			{"Get hypervisors in sub org filter by hypervisor_type=hyperv & hypervisor_name & sort by create_ts desc with sub msp user token",
					ti.root_msp1_submsp1_user1_token,"hypervisor_type;=;hyperv,hypervisor_name;=;hyperv_hypervisor_name","create_ts;asc",0,0, expSubMspSuborgHypervisorsInfo},
			{"Get hypervisors in sub org filter by hypervisor_type=hyper_v & organization_id & sort by create_ts desc with sub msp user token",
					ti.root_msp1_submsp1_user1_token,"hypervisor_type;=;hyperv,organization_id;=;"+ti.msp1_submsp1_sub_org1_id,"create_ts;desc",0,0, expSubMspSuborgHypervisorsInfo},
			{"Get hypervisors in sub org filter by hypervisor_type=vmware & organization_id & sort by create_ts desc with sub msp user token",
					ti.root_msp1_submsp1_user1_token,"hypervisor_type;=;vmware,organization_id;=;"+ti.msp1_submsp1_sub_org1_id,"create_ts;asc",0,0, expSubMspSuborgHypervisorsInfo},
			{"Get hypervisors in sub org filter by hypervisor_product & organization_id & sort by create_ts desc with sub msp user token",
					ti.root_msp1_submsp1_user1_token,"hypervisor_product;=;cloud_sub_org,organization_id;=;"+ti.msp1_submsp1_sub_org1_id,"create_ts;desc",0,0, expSubMspSuborgHypervisorsInfo},
			{"Get hypervisors in sub org filter by hypervisor_type=vmware & organization_id & sort by create_ts desc with sub msp user token",
					ti.root_msp1_submsp1_user1_token,"hypervisor_type;=;vmware,create_user_id;=;"+ti.msp1_submsp1_suborg1_user1_id,"create_ts;desc",0,0, expSubMspSuborgHypervisorsInfo},
			{"Get hypervisors in sub org filter by site_id & organization_id & sort by create_ts desc with sub msp user token",
					ti.root_msp1_submsp1_user1_token,"site_id;=;"+msp_cloud_id+",organization_id;=;"+ti.msp1_submsp1_sub_org1_id,"create_ts;desc",0,0, expSubMspSuborgHypervisorsInfo},
			
			
			{"Get hypervisors in sub org filter by hypervisor_type & sort by create_ts desc",
					ti.root_msp1_suborg1_user1_token,"hypervisor_type;=;hyperv","create_ts;desc",2,0, expSuborgHypervisorsInfo},
			{"Get hypervisors in sub org filter by hypervisor_type=vmware & sort by create_ts desc",
					ti.root_msp1_suborg1_user1_token,"hypervisor_type;=;vmware","create_ts;desc",-1,0, expSuborgHypervisorsInfo},
			{"Get hypervisors in sub org filter by organization_id & sort by create_ts asc",
					ti.root_msp1_suborg1_user1_token,"organization_id;=;"+ti.root_msp1_suborg1_id,"create_ts;asc",0,0, expSuborgHypervisorsInfo},
			{"Get hypervisors in sub org filter by hypervisor_product=cloud_sub_org & sort by create_ts desc",
					ti.root_msp1_suborg1_user1_token,"hypervisor_product;=;cloud_sub_org","create_ts;desc",0,-1, expSuborgHypervisorsInfo},
			{"Get hypervisors in sub org filter by site_id & sort by create_ts desc",
					ti.root_msp1_suborg1_user1_token,"site_id;=;"+msp_cloud_id,"create_ts;desc",0,0, expSuborgHypervisorsInfo},
			{"Get hypervisors in sub org filter by hypervisor_type=hyperv & hypervisor_name & sort by create_ts desc",
					ti.root_msp1_suborg1_user1_token,"hypervisor_type;=;hyperv,hypervisor_name;=;hyperv_hypervisor_name","create_ts;asc",0,0, expSuborgHypervisorsInfo},
			{"Get hypervisors in sub org filter by hypervisor_type=hyper_v & organization_id & sort by create_ts desc",
					ti.root_msp1_suborg1_user1_token,"hypervisor_type;=;hyperv,organization_id;=;"+ti.root_msp1_suborg1_id,"create_ts;desc",0,0, expSuborgHypervisorsInfo},
			{"Get hypervisors in sub org filter by hypervisor_type=vmware & organization_id & sort by create_ts desc",
					ti.root_msp1_suborg1_user1_token,"hypervisor_type;=;vmware,organization_id;=;"+ti.root_msp1_suborg1_id,"create_ts;asc",0,0, expSuborgHypervisorsInfo},
			{"Get hypervisors in sub org filter by hypervisor_product & organization_id & sort by create_ts desc",
					ti.root_msp1_suborg1_user1_token,"hypervisor_product;=;cloud_sub_org,organization_id;=;"+ti.root_msp1_suborg1_id,"create_ts;desc",0,0, expSuborgHypervisorsInfo},
			{"Get hypervisors in sub org filter by hypervisor_type=vmware & organization_id & sort by create_ts desc",
					ti.root_msp1_suborg1_user1_token,"hypervisor_type;=;vmware,create_user_id;=;"+ti.root_msp1_suborg1_user1_id,"create_ts;desc",0,0, expSuborgHypervisorsInfo},
			{"Get hypervisors in sub org filter by site_id & organization_id & sort by create_ts desc",
					ti.root_msp1_suborg1_user1_token,"site_id;=;"+msp_cloud_id+",organization_id;=;"+ti.root_msp1_suborg1_id,"create_ts;desc",0,0, expSuborgHypervisorsInfo},
			
			{"Get hypervisors in sub msp's sub org filter by hypervisor_type & sort by create_ts desc",
						ti.msp1_submsp1_suborg1_user1_token,"hypervisor_type;=;hyperv","create_ts;desc",2,0, expSubMspSuborgHypervisorsInfo},
			{"Get hypervisors in sub msp's sub org filter by hypervisor_type=vmware & sort by create_ts desc",
						ti.msp1_submsp1_suborg1_user1_token,"hypervisor_type;=;vmware","create_ts;desc",-1,0, expSubMspSuborgHypervisorsInfo},
			{"Get hypervisors in sub msp's sub org filter by organization_id & sort by create_ts asc",
						ti.msp1_submsp1_suborg1_user1_token,"organization_id;=;"+ti.msp1_submsp1_sub_org1_id,"create_ts;asc",0,0, expSubMspSuborgHypervisorsInfo},
			{"Get hypervisors in sub msp's sub org filter by hypervisor_product=cloud_sub_org & sort by create_ts desc",
						ti.msp1_submsp1_suborg1_user1_token,"hypervisor_product;=;cloud_sub_org","create_ts;desc",0,-1, expSubMspSuborgHypervisorsInfo},
			{"Get hypervisors in sub msp's sub org filter by site_id & sort by create_ts desc",
						ti.msp1_submsp1_suborg1_user1_token,"site_id;=;"+msp_cloud_id,"create_ts;desc",0,0, expSubMspSuborgHypervisorsInfo},
			{"Get hypervisors in sub msp's sub org filter by hypervisor_type=hyperv & hypervisor_name & sort by create_ts desc",
						ti.msp1_submsp1_suborg1_user1_token,"hypervisor_type;=;hyperv,hypervisor_name;=;hyperv_hypervisor_name","create_ts;asc",0,0, expSubMspSuborgHypervisorsInfo},
			{"Get hypervisors in sub msp's sub org filter by hypervisor_type=hyper_v & organization_id & sort by create_ts desc",
						ti.msp1_submsp1_suborg1_user1_token,"hypervisor_type;=;hyperv,organization_id;=;"+ti.msp1_submsp1_sub_org1_id,"create_ts;desc",0,0, expSubMspSuborgHypervisorsInfo},
			{"Get hypervisors in sub msp's sub org filter by hypervisor_type=vmware & organization_id & sort by create_ts desc",
						ti.msp1_submsp1_suborg1_user1_token,"hypervisor_type;=;vmware,organization_id;=;"+ti.msp1_submsp1_sub_org1_id,"create_ts;asc",0,0, expSubMspSuborgHypervisorsInfo},
			{"Get hypervisors in sub msp's sub org filter by hypervisor_product & organization_id & sort by create_ts desc",
						ti.msp1_submsp1_suborg1_user1_token,"hypervisor_product;=;cloud_sub_org,organization_id;=;"+ti.msp1_submsp1_sub_org1_id,"create_ts;desc",0,0, expSubMspSuborgHypervisorsInfo},
			{"Get hypervisors in sub msp's sub org filter by hypervisor_type=vmware & organization_id & sort by create_ts desc",
						ti.msp1_submsp1_suborg1_user1_token,"hypervisor_type;=;vmware,create_user_id;=;"+ti.msp1_submsp1_suborg1_user1_id,"create_ts;desc",0,0, expSubMspSuborgHypervisorsInfo},
			{"Get hypervisors in sub msp's sub org filter by site_id & organization_id & sort by create_ts desc",
						ti.msp1_submsp1_suborg1_user1_token,"site_id;=;"+msp_cloud_id+",organization_id;=;"+ti.msp1_submsp1_sub_org1_id,"create_ts;desc",0,0, expSubMspSuborgHypervisorsInfo},
				
			//msp account admin token
			{"Get hypervisors in sub org with msp account admin token filter by org_id",
					ti.root_msp_org1_msp_accountadmin1_token,"organization_id;=;"+ti.root_msp1_suborg1_id,"create_ts;asc",0,0, expSuborgHypervisorsInfo},
			{"Get hypervisors in sub org with msp account admin token filter by hypervisor_type=hyper_v & organization_id & sort by create_ts desc",
					ti.root_msp_org1_msp_accountadmin1_token,"hypervisor_type;=;hyperv,organization_id;=;"+ti.root_msp1_suborg1_id,"create_ts;desc",0,0, expSuborgHypervisorsInfo},
			
			//with csr token
			{"Get hypervisors in direct org with csr token filter by org_id",
				ti.csr_token,"organization_id;=;"+ti.direct_org1_id,"create_ts;asc",0,0,expDirectHypervisorsInfo},
			{"Get hypervisors in direct organization with csr token filter by hypervisor_type=hyper_v & organization_id & sort by create_ts desc",
					ti.csr_token,"hypervisor_type;=;hyperv,organization_id;=;"+ti.direct_org1_id,"create_ts;desc",0,0,expDirectHypervisorsInfo},
			
			{"Get hypervisors in sub org with csr token filter by org_id",
					ti.csr_token,"organization_id;=;"+ti.root_msp1_suborg1_id,"create_ts;asc",0,0, expSuborgHypervisorsInfo},
			{"Get hypervisors in sub org with csr token filter by hypervisor_type=hyper_v & organization_id & sort by create_ts desc",
						ti.csr_token,"hypervisor_type;=;hyperv,organization_id;=;"+ti.root_msp1_suborg1_id,"create_ts;desc",0,0, expSuborgHypervisorsInfo},
			
			//with csr readonly token
			{"Get hypervisors in direct org with csr readonly user token filter by org_id",
				ti.csr_readonly_token,"organization_id;=;"+ti.direct_org1_id,"create_ts;asc",0,0,expDirectHypervisorsInfo},
			{"Get hypervisors in direct organization with csr readonly user token filter by hypervisor_type=hyper_v & organization_id & sort by create_ts desc",
					ti.csr_readonly_token,"hypervisor_type;=;hyperv,organization_id;=;"+ti.direct_org1_id,"create_ts;desc",0,0,expDirectHypervisorsInfo},
			
			{"Get hypervisors in sub org with csr read only token filter by org_id",
				ti.csr_readonly_token,"organization_id;=;"+ti.root_msp1_suborg1_id,"create_ts;asc",0,0, expSuborgHypervisorsInfo},
			{"Get hypervisors in sub org with csr readonly user token filter by hypervisor_type=hyper_v & organization_id & sort by create_ts desc",
					ti.csr_readonly_token,"hypervisor_type;=;hyperv,organization_id;=;"+ti.root_msp1_suborg1_id,"create_ts;desc",0,0, expSuborgHypervisorsInfo},
			
		};
	}
	
	@Test(dataProvider = "GetHypervisorsTest",enabled=true)
	public void getHypervisorsTest_200(String caseType,String token, String filterStr,String sortStr, int pageNumber, int pageSize, ArrayList<HashMap<String, Object>> expResponse) {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		
		test.log(LogStatus.INFO, caseType);
		spogHypervisorsServer.setToken(token);
		spogHypervisorsServer.getHypervisorsWithCheck(expResponse,  pageNumber, pageSize, filterStr, sortStr,SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
	}
	
	@Test(enabled=true)
	public void getHypervisorsTest_401() {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		ArrayList<HashMap<String, Object>> expectedHypervisorsInfo = new ArrayList<>();
		
		expectedHypervisorsInfo = spogHypervisorsServer.getHypervisorComposeInfo();
		
		spogHypervisorsServer.setToken("");
		
		test.log(LogStatus.INFO, "Get hypervisors check");
		spogHypervisorsServer.getHypervisorsWithCheck(expectedHypervisorsInfo,  0, 0, null, null,SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);
		
		spogHypervisorsServer.setToken("invalid");
				
		test.log(LogStatus.INFO, "Get hypervisors check");
		spogHypervisorsServer.getHypervisorsWithCheck(expectedHypervisorsInfo,  0, 0, null, null,SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);
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
