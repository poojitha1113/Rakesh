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

public class RefreshVMSForSpecifiedHypervisor extends base.prepare.Is4Org {

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
	
	private String direct_baas_destionation_ID_hyperv;
	private String direct_baas_destionation_ID_vmware;
	private String msp_baas_destionation_ID;
	private String sub_orga_baas_destionation_ID;
	private String cloudAccountSecret;
	private String direct_hyperv_hypervisor_id;
	private String direct_vmware_hypervisor_id;
	private String msp_hyperv_hypervisor_id;
	private String msp_vmware_hypervisor_id;
	private String sub_hyperv_hypervisor_id;
	private String sub_vmware_hypervisor_id;
	Response response;
	String prefix = RandomStringUtils.randomAlphanumeric(8);
	
	LocalDate date = LocalDate.now();  
	LocalDate yesterday = date.minusDays(1);  
	LocalDate tomorrow = yesterday.plusDays(2);
		
	private String  org_model_prefix=this.getClass().getSimpleName();
	private TestOrgInfo ti;
	private String msp_cloud_id;
	private String direct_cloud_id;
	
	@BeforeClass
	@Parameters({ "baseURI", "port","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port,String logFolder,String runningMachine,String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		spogHypervisorsServer = new SPOGHypervisorsServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		rep = ExtentManager.getInstance("RefreshVMSForSpecifiedHypervisor", logFolder);
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
	}
	
	@Test(enabled=false)
	public void createHypervisorTest() {
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		test.assignAuthor("Rakesh.Chalamala");
		
		Response response = gatewayServer.LoginSite(msp_cloud_id, cloudAccountSecret, test);
		String token = response.then().extract().path("data.token");
		spogHypervisorsServer.setToken(token);
		
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		this.direct_hyperv_hypervisor_id = spogHypervisorsServer.createHypervisorWithCheck("none", "hyperv_hypervisor_name_r", "hyperv", "cloud_direct", "none", direct_cloud_id, ti.direct_org1_id, 
				"false", String.valueOf(System.currentTimeMillis()), direct_baas_destionation_ID_hyperv, "none", "0 0 * * *", "1d", 
				"agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", test);
		this.direct_vmware_hypervisor_id = spogHypervisorsServer.createHypervisorWithCheck("none", "vmware_hypervisor_name_r", "vmware", "cloud_direct", "none", direct_cloud_id, ti.direct_org1_id, 
				"false", String.valueOf(System.currentTimeMillis()), direct_baas_destionation_ID_vmware, "none", "0 0 * * *", "1d", 
				"agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", test);
		
		
		
		/*this.msp_hyperv_hypervisor_id = spogHypervisorsServer.createHypervisorWithCheck("none", "hyperv_hypervisor_name_r", "hyperv", "cloud_direct", "none", msp_cloud_id, msp_organization_id, 
				"false", String.valueOf(System.currentTimeMillis()), msp_baas_destionation_ID, "none", "0 0 * * *", "1d", 
				"agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", test);
		this.msp_vmware_hypervisor_id = spogHypervisorsServer.createHypervisorWithCheck("none", "vmware_hypervisor_name_r", "vmware", "cloud_direct", "none", msp_cloud_id, msp_organization_id, 
				"false", String.valueOf(System.currentTimeMillis()), msp_baas_destionation_ID, "none", "0 0 * * *", "1d", 
				"agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", test);*/
		
		
		this.sub_hyperv_hypervisor_id = spogHypervisorsServer.createHypervisorWithCheck("none", "hyperv_hypervisor_name_r", "hyperv", "cloud_direct", "none", msp_cloud_id, ti.root_msp1_suborg1_id, 
				"false", String.valueOf(System.currentTimeMillis()), sub_orga_baas_destionation_ID, "none", "0 0 * * *", "1d", 
				"agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", test);
		this.sub_vmware_hypervisor_id = spogHypervisorsServer.createHypervisorWithCheck("none", "vmware_hypervisor_name_r", "vmware", "cloud_direct", "none", msp_cloud_id, ti.root_msp1_suborg1_id, 
				"false", String.valueOf(System.currentTimeMillis()), sub_orga_baas_destionation_ID, "none", "0 0 * * *", "1d", 
				"agent_name", "6.2.2036", "6.12.12036", "http://upgradelink", "true",
				"apiversion", "vcenterHost", "pool1,pool2", "store1,store2", "datacenter1,datacenter2", "system1,system2", "bind_datacenter", "bind_host", test);
	}
	
	@DataProvider(name="refreshvmsvalid")
	public Object[][] refreshvmsvalid(){
		return new Object[][] {
			{direct_hyperv_hypervisor_id,ti.direct_org1_user1_token},
			{direct_vmware_hypervisor_id,ti.direct_org1_user1_token},
			
			/*{msp_hyperv_hypervisor_id,ti.root_msp_org1_user1_token},
			{msp_vmware_hypervisor_id,ti.root_msp_org1_user1_token},*/
			
			{sub_hyperv_hypervisor_id,ti.root_msp1_suborg1_user1_token},
			{sub_vmware_hypervisor_id,ti.root_msp1_suborg1_user1_token}
		};
	}
	
	@Test(dataProvider = "refreshvmsvalid",enabled=false)
	public void refreshVMSForSpecifiedHypervisor_200(String hypervisor_id,String site_id) {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		test.assignAuthor("Rakesh.Chalamala");
		
		Response response = gatewayServer.LoginSite(site_id, cloudAccountSecret, test);
		String token = response.then().extract().path("data.token");
		spogHypervisorsServer.setToken(token);
		
		test.log(LogStatus.INFO, "Refresh vms for specified hypervisor valid");
		spogHypervisorsServer.refreshVMSForSpecifiedHypervisorWithCheck(hypervisor_id,SpogConstants.SUCCESS_POST,null, test);
	}
	
	
	
	@DataProvider(name="refreshvmsInvalidT")
	public Object[][] refreshvmsInvalidT(){
		return new Object[][] {
			{"direct-mspT",direct_hyperv_hypervisor_id,direct_cloud_id},
			{"direct-subT",direct_vmware_hypervisor_id,direct_cloud_id},
			
			/*{"msp-directT",msp_hyperv_hypervisor_id,msp_cloud_id},
			{"msp-directT",msp_vmware_hypervisor_id,msp_cloud_id},*/
			
			{"sub-directT",sub_hyperv_hypervisor_id,msp_cloud_id},
			{"sub-directT",sub_vmware_hypervisor_id,msp_cloud_id}
		};
	}
	
	@Test(dataProvider = "refreshvmsInvalidT",enabled=false)
	public void refreshVMSForSpecifiedHypervisor_403(String OrgType,String hypervisor_id,String site_id) {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		test.assignAuthor("Rakesh.Chalamala");
		
		if(OrgType.contains("-mspT")) {
			Response response = gatewayServer.LoginSite(msp_cloud_id, cloudAccountSecret, test);
			String token = response.then().extract().path("data.token");
			spogHypervisorsServer.setToken(token);
		}
		else if (OrgType.contains("-directT")) {
			Response response = gatewayServer.LoginSite(direct_cloud_id, cloudAccountSecret, test);
			String token = response.then().extract().path("data.token");
			spogHypervisorsServer.setToken(token);
		}
		else if (OrgType.contains("-subT")) {
			Response response = gatewayServer.LoginSite(msp_cloud_id, cloudAccountSecret, test);
			String token = response.then().extract().path("data.token");
			spogHypervisorsServer.setToken(token);
		}
		
		test.log(LogStatus.INFO, "Refresh vms for specified hypervisor valid");
		spogHypervisorsServer.refreshVMSForSpecifiedHypervisorWithCheck(hypervisor_id,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY, test);
	}
	
	@DataProvider (name="refreshVMSForSpecifiedHypervisorInvalid")
	public Object[][] refreshVMSForSpecifiedHypervisorInvalid(){
		return new Object[][] {
			//400 cases - invalid / null as hypervisor id
			{"Refresh vms for specified hypervisor with null as hypervisor id & csr token", null, ti.csr_token, SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_HYPERVISORID_IS_NOT_A_UUID},
			{"Refresh vms for specified hypervisor with invalid hypervisor id & csr token", "INVALID", ti.csr_token, SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_HYPERVISORID_IS_NOT_A_UUID},
			{"Refresh vms for specified hypervisor with null as hypervisor id", null, ti.direct_org1_user1_token, SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_HYPERVISORID_IS_NOT_A_UUID},
			{"Refresh vms for specified hypervisor with invalid hypervisor id", "INVALID", ti.direct_org1_user1_token, SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_HYPERVISORID_IS_NOT_A_UUID},
			{"Refresh vms for specified hypervisor with null as hypervisor id", null, ti.root_msp_org1_user1_token, SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_HYPERVISORID_IS_NOT_A_UUID},
			{"Refresh vms for specified hypervisor with invalid hypervisor id", "INVALID", ti.root_msp_org1_user1_token, SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_HYPERVISORID_IS_NOT_A_UUID},
			{"Refresh vms for specified hypervisor with null as hypervisor id", null, ti.root_msp1_suborg1_user1_token, SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_HYPERVISORID_IS_NOT_A_UUID},
			{"Refresh vms for specified hypervisor with invalid hypervisor id", "INVALID", ti.root_msp1_suborg1_user1_token, SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_HYPERVISORID_IS_NOT_A_UUID},
			{"Refresh vms for specified hypervisor with invalid hypervisor id and msp_account_admin token", "INVALID", ti.root_msp_org1_msp_accountadmin1_token, SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_HYPERVISORID_IS_NOT_A_UUID},
			{"Refresh vms for specified hypervisor with null as hypervisor id for sub msp", null, ti.root_msp1_submsp1_user1_token, SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_HYPERVISORID_IS_NOT_A_UUID},
			{"Refresh vms for specified hypervisor with invalid hypervisor id for sub msp", "INVALID", ti.root_msp1_submsp1_user1_token, SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_HYPERVISORID_IS_NOT_A_UUID},
			{"Refresh vms for specified hypervisor with null as hypervisor id for sub msp sub org", null, ti.msp1_submsp1_suborg1_user1_token, SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_HYPERVISORID_IS_NOT_A_UUID},
			{"Refresh vms for specified hypervisor with invalid hypervisor id for sub msp sub org", "INVALID", ti.msp1_submsp1_suborg1_user1_token, SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_HYPERVISORID_IS_NOT_A_UUID},
			{"Refresh vms for specified hypervisor with invalid hypervisor id and sub msp_account_admin token", "INVALID", ti.root_msp1_submsp1_account_admin_token, SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_HYPERVISORID_IS_NOT_A_UUID},
			
			//401 cases - missing / invalid token
			{"Refresh vms for specified hypervisor with missing token", UUID.randomUUID().toString(), "", SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTHENTICATION_FAILED},
			{"Refresh vms for specified hypervisor with invalid token", UUID.randomUUID().toString(), "INVALIDTOKEN", SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT},

			//403
			{"Refresh vms for specified hypervisor with csr_readonly token", UUID.randomUUID().toString(), ti.csr_readonly_token, SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			
			//404 cases - Hypervisor id that does not exist
			{"Refresh vms for specified hypervisor with hypervisor id that doesnot exist & csr token", UUID.randomUUID().toString(), ti.csr_token, SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.THE_HYPERVISOR_WITH_ID_DOES_NOT_EXIST_OR_HAS_BEEN_DELETED},
			{"Refresh vms for specified hypervisor with hypervisor id that doesnot exist", UUID.randomUUID().toString(), ti.direct_org1_user1_token, SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.THE_HYPERVISOR_WITH_ID_DOES_NOT_EXIST_OR_HAS_BEEN_DELETED},
			{"Refresh vms for specified hypervisor with hypervisor id that doesnot exist", UUID.randomUUID().toString(), ti.root_msp_org1_user1_token, SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.THE_HYPERVISOR_WITH_ID_DOES_NOT_EXIST_OR_HAS_BEEN_DELETED},
			{"Refresh vms for specified hypervisor with hypervisor id that doesnot exist", UUID.randomUUID().toString(), ti.root_msp1_suborg1_user1_token, SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.THE_HYPERVISOR_WITH_ID_DOES_NOT_EXIST_OR_HAS_BEEN_DELETED},
			{"Refresh vms for specified hypervisor with hypervisor id that doesnot exist and msp_account_admin token", UUID.randomUUID().toString(), ti.root_msp_org1_msp_accountadmin1_token, SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.THE_HYPERVISOR_WITH_ID_DOES_NOT_EXIST_OR_HAS_BEEN_DELETED},
			{"Refresh vms for specified hypervisor with hypervisor id that doesnot exist sub msp", UUID.randomUUID().toString(), ti.root_msp1_submsp1_user1_token, SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.THE_HYPERVISOR_WITH_ID_DOES_NOT_EXIST_OR_HAS_BEEN_DELETED},
			{"Refresh vms for specified hypervisor with hypervisor id that doesnot exist sub msp sub org", UUID.randomUUID().toString(), ti.msp1_submsp1_suborg1_user1_token, SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.THE_HYPERVISOR_WITH_ID_DOES_NOT_EXIST_OR_HAS_BEEN_DELETED},
			{"Refresh vms for specified hypervisor with hypervisor id that doesnot exist and sub msp_account_admin token", UUID.randomUUID().toString(), ti.root_msp1_submsp1_account_admin_token, SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.THE_HYPERVISOR_WITH_ID_DOES_NOT_EXIST_OR_HAS_BEEN_DELETED},
			
			
		};
	}
	
	@Test(dataProvider="refreshVMSForSpecifiedHypervisorInvalid",enabled=true)
	public void refreshVMSForSpecifiedHypervisor_400_401_404(String caseType,String hypervisor_id,String token, int expectedStatusCode, SpogMessageCode expectedErrorMessage) {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		test.assignAuthor("Rakesh.Chalamala");

		spogHypervisorsServer.setToken(token);		
		test.log(LogStatus.INFO, caseType);
		spogHypervisorsServer.refreshVMSForSpecifiedHypervisorWithCheck(hypervisor_id, expectedStatusCode,expectedErrorMessage, test);
		
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
