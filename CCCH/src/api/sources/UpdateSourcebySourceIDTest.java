package api.sources;

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
import Constants.OSMajor;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.SPOGServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import invoker.SiteTestHelper;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;

public class UpdateSourcebySourceIDTest extends base.prepare.Is4Org{
	
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private SiteTestHelper siteTestHelper;
	private ExtentTest test;

	private String org_model_prefix=this.getClass().getSimpleName();
	private TestOrgInfo ti;
	private Response response;
	private String direct_cloud_id;
	private String msp_cloud_id;
	private String directSourceId;
	private String subOrgSourceId;
	private String subMspSubOrgSourceId;

	@BeforeClass
	@Parameters({ "baseURI", "port","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port,String logFolder,String runMachine,String buildVersion) 
	{
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		rep = ExtentManager.getInstance(this.getClass().getSimpleName(), logFolder);
		test = rep.startTest("Setup for "+this.getClass().getSimpleName());
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Kanamarlapudi, Chandra Kanth";
		
		Nooftest=0;
		//Used for creating a build number with dateformat
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date=new java.util.Date();
		this.buildVersion=buildVersion+"_"+dateFormater.format(date);
		
		BQName=this.getClass().getSimpleName();
		runningMachine=runMachine;
		System.out.println("The value of hit is "+count1.isstarttimehit());
		
		
		if( count1.isstarttimehit( ) == 0 ) 
		{
			System.out.println("into creation time");
			creationTime=System.currentTimeMillis();
			count1.setcreationtime(creationTime);
			
			// creationTime = System.currentTimeMillis();
			    try
			    {
				    bqdb1.updateTable(BQName, runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0", String.valueOf(Nooftest), count1.getcreationtime(), "InProgress",author+" and Rest server is "+baseURI.split("//")[1]);
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

		response = spogServer.getCloudAccounts(ti.root_msp_org1_user1_token, "", test);
		msp_cloud_id = response.then().extract().path("data[0].cloud_account_id");

		spogServer.setToken(ti.direct_org1_user1_token);
		response = spogServer.createSource(spogServer.ReturnRandom("src"), SourceType.machine, SourceProduct.cloud_direct, ti.direct_org1_id, direct_cloud_id, ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(), "",  test);
		directSourceId = response.then().extract().path("data.source_id");
		
		spogServer.setToken(ti.root_msp1_suborg1_user1_token);
		response = spogServer.createSource(spogServer.ReturnRandom("src"), SourceType.machine, SourceProduct.cloud_direct, ti.root_msp1_suborg1_id, msp_cloud_id, ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(), "",  test);
		subOrgSourceId = response.then().extract().path("data.source_id");
		
		spogServer.setToken(ti.msp1_submsp1_suborg1_user1_token);
		response = spogServer.createSource(spogServer.ReturnRandom("src"), SourceType.machine, SourceProduct.cloud_direct, ti.msp1_submsp1_sub_org1_id, msp_cloud_id, ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(), "",  test);
		subMspSubOrgSourceId = response.then().extract().path("data.source_id");
		
	}
	
	@DataProvider(name="updateSourceValidCases")
	public Object[][] updateSourceValidCases(){
		return new Object[][] {
			{"Update source by id in direct organization", ti.direct_org1_user1_token, ti.direct_org1_user1_id, ti.direct_org1_id, direct_cloud_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Update source by id in sub organization using msp token", ti.root_msp_org1_user1_token, ti.root_msp_org1_user1_id, ti.root_msp1_suborg1_id, msp_cloud_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Update source by id in sub organization", ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id, ti.root_msp1_suborg1_id, msp_cloud_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Update source by id in sub organization using msp account admin token", ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_id, ti.root_msp1_suborg1_id, msp_cloud_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Update source by id in sub organization using sub msp token", ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id, ti.msp1_submsp1_sub_org1_id, msp_cloud_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Update source by id in sub msp sub organization", ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id, ti.msp1_submsp1_sub_org1_id, msp_cloud_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Update source by id in sub organization using msp account admin token", ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp1_account_admin_1_id, ti.msp1_submsp1_sub_org1_id, msp_cloud_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			
		};		
	}
	
	@Test(dataProvider="updateSourceValidCases")
	public void updateSourceByIdValid(String testCase, String token, String user_id, String organization_id, String site_id, int expectedStatusCode, SpogMessageCode expectedErrorMessage) {
		
		test = ExtentManager.getNewTest(testCase);
		String source_name = spogServer.ReturnRandom("src");		
		
		test.log(LogStatus.INFO, "Add sources to the site under the org");
		spogServer.setToken(token);
		response = spogServer.createSource("test", SourceType.machine, SourceProduct.cloud_direct, organization_id, site_id, ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(), "",  test);
		String source_id = response.then().extract().path("data.source_id");
		test.log(LogStatus.INFO, "The source id is "+source_id);
				
		test.log(LogStatus.INFO, "update the existing source with check");
		spogServer.updateSourcebysourceIdwithcheck(source_id, source_name, SourceType.machine, SourceProduct.cloud_direct, organization_id, site_id, "",ProtectionStatus.unprotect, 
							ConnectionStatus.online, "windows",token, user_id,expectedStatusCode,expectedErrorMessage,test);
		
		spogServer.setToken(ti.csr_token);
//		spogServer.deleteSourceByID(source_id, test);
	}
	
	@DataProvider(name="invalidCases")
	public Object[][] invalidCases(){
		return new Object[][] {
			//400
			{"Update source with invalid id and direct org user token", "invalid", ti.direct_org1_user1_token, SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID},
			{"Update source with invalid id and msp org user token", "invalid", ti.root_msp_org1_user1_token, SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID},
			{"Update source with invalid id and sub org user token", "invalid", ti.root_msp1_suborg1_user1_token, SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID},
			{"Update source with invalid id and msp org account admin user token", "invalid", ti.root_msp_org1_msp_accountadmin1_token, SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID},
			{"Update source with invalid id and sub msp org user token", "invalid", ti.root_msp1_submsp1_user1_token, SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID},
			{"Update source with invalid id and sub msp sub org user token", "invalid", ti.msp1_submsp1_suborg1_user1_token, SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID},
			{"Update source with invalid id and sub msp org account admin user token", "invalid", ti.root_msp1_submsp1_account_admin_token, SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID},
			{"Update source with invalid id and csr user token", "invalid", ti.csr_token, SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID},
			
			//401
			{"Update source with invalid token", UUID.randomUUID().toString(), "invalid", SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT},
			{"Update source with missing token", UUID.randomUUID().toString(), "", SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED},
			{"Update source with null as token", UUID.randomUUID().toString(), null, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED},
			
			//403
			{"Update source of direct org with direct org2 user token", directSourceId, ti.direct_org2_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Update source of direct org with msp org user token", directSourceId, ti.root_msp_org1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Update source of direct org with sub org user token", directSourceId, ti.root_msp1_suborg1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Update source of direct org with msp org account admin user token", directSourceId, ti.root_msp_org1_msp_accountadmin1_token, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Update source of direct org with sub msp org user token", directSourceId, ti.root_msp1_submsp1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Update source of direct org with sub msp sub org user token", directSourceId, ti.msp1_submsp1_suborg1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Update source of direct org with sub msp org account admin user token", directSourceId, ti.root_msp1_submsp1_account_admin_token, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			
			{"Update source of sub org with direct org2 user token", subOrgSourceId, ti.direct_org2_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Update source of sub org with msp org2 user token", subOrgSourceId, ti.root_msp_org2_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Update source of sub org with sub org2 user token", subOrgSourceId, ti.root_msp1_suborg2_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Update source of sub org with msp org account admin user token", subOrgSourceId, ti.root_msp_org1_msp_accountadmin2_token, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Update source of sub org with sub msp org user token", subOrgSourceId, ti.root_msp1_submsp1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Update source of sub org with sub msp sub org user token", subOrgSourceId, ti.msp1_submsp1_suborg1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Update source of sub org with sub msp org account admin user token", subOrgSourceId, ti.root_msp1_submsp1_account_admin_token, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			
			{"Update source of sub msp sub org with direct org2 user token", subMspSubOrgSourceId, ti.direct_org2_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Update source of sub msp sub org with msp org user token", subMspSubOrgSourceId, ti.root_msp_org1_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Update source of sub msp sub org with sub org2 user token", subMspSubOrgSourceId, ti.root_msp1_suborg2_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Update source of sub msp sub org with msp org account admin user token", subMspSubOrgSourceId, ti.root_msp_org1_msp_accountadmin1_token, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Update source of sub msp sub org with sub msp org2 user token", subMspSubOrgSourceId, ti.root_msp1_submsp2_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Update source of sub msp sub org with sub msp sub org2 user token", subMspSubOrgSourceId, ti.msp1_submsp1_suborg2_user1_token, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Update source of sub msp sub org with sub msp org2 account admin user token", subMspSubOrgSourceId, ti.root_msp1_submsp2_account_admin_token, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
	
		};
	}
	
	@Test(dataProvider="invalidCases")
	public void updateSourceByIdInvalid(String testCase, String source_id, String token, int expectedStatusCode, SpogMessageCode expectedErrorMessage) {
		
		test = ExtentManager.getNewTest(testCase);
		spogServer.updateSourcebysourceIdwithcheck(source_id, "test", SourceType.machine, SourceProduct.cloud_direct, null, null, "",ProtectionStatus.unprotect, 
							ConnectionStatus.offline, "windows",token, "",expectedStatusCode, expectedErrorMessage,test);		
	}
	
	@DataProvider(name="nonExistingSourceCases")
	public Object[][] nonExistingSourceCases(){
		return new Object[][] {
			//200
			{"Update source with id that does not exist and direct org user token", UUID.randomUUID().toString(), ti.direct_org1_user1_token, ti.direct_org1_user1_id,ti.direct_org1_id, direct_cloud_id},
			{"Update source with id that does not exist and msp org user token", UUID.randomUUID().toString(), ti.root_msp_org1_user1_token, ti.root_msp_org1_user1_id, ti.root_msp1_suborg1_id, msp_cloud_id},
			{"Update source with id that does not exist and sub org user token", UUID.randomUUID().toString(), ti.root_msp1_suborg1_user1_token, ti.root_msp1_suborg1_user1_id, ti.root_msp1_suborg1_id, msp_cloud_id},
			{"Update source with id that does not exist and msp org account admin user token", UUID.randomUUID().toString(), ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_msp_accountadmin1_id, ti.root_msp1_suborg1_id, msp_cloud_id},
			{"Update source with id that does not exist and sub msp org user token", UUID.randomUUID().toString(), ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_user1_id, ti.msp1_submsp1_sub_org1_id, msp_cloud_id},
			{"Update source with id that does not exist and sub msp sub org user token", UUID.randomUUID().toString(), ti.msp1_submsp1_suborg1_user1_token, ti.msp1_submsp1_suborg1_user1_id, ti.msp1_submsp1_sub_org1_id, msp_cloud_id},
			{"Update source with id that does not exist and sub msp org account admin user token", UUID.randomUUID().toString(), ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp1_account_admin_1_id, ti.msp1_submsp1_sub_org1_id, msp_cloud_id},
			{"Update source with id that does not exist and csr user token", UUID.randomUUID().toString(), ti.csr_token, ti.csr_admin_user_id, ti.direct_org1_id, direct_cloud_id},

		};
	}
	
	@Test(dataProvider="nonExistingSourceCases")
	public void updateNonExistingSourceShouldAddSourceTest(String testCase, String source_id, String token, String user_id, String org_id, String site_id) {
		
		test = ExtentManager.getNewTest(testCase);
		source_id = spogServer.updateSourcebysourceIdwithcheck(source_id, "test", SourceType.machine, SourceProduct.cloud_direct, org_id, site_id, "",ProtectionStatus.unprotect, 
							ConnectionStatus.offline, "windows",token, user_id,SpogConstants.SUCCESS_GET_PUT_DELETE, null,test);
		
		spogServer.setToken(ti.csr_token);
//		spogServer.deleteSourceByID(source_id, test);
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
