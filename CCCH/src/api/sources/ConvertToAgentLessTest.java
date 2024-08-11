package api.sources;

import static org.testng.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Properties;
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

import Constants.DestinationStatus;
import Constants.DestinationType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGHypervisorsServer;
import InvokerServer.SPOGServer;
import InvokerServer.Source4SPOGServer;
import InvokerServer.UserSpogServer;
import InvokerServer.GatewayServer.siteType;
import base.prepare.CreateOrgsInfo;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;


/**
 * API - POST: /sources/{id}/convert_to_agentless
 * Converts a specified source to agentless(hypervisor)
 * 
 * For hypervisor_type = hyperv we register the windows server machine as normal source
 * In the response of this source we will get a contextual_action "convert_to_agentless" which will call this API 
 * By calling this API the source machine will be converted to hypervisor so that all VMs on it can be backed up
 * 
 * We support windows server 2012 and 2016 as of 18-03-2019
 * 
 * @author Rakesh.Chalamala
 * Sprint 30       
 */
public class ConvertToAgentLessTest extends base.prepare.Is4Org {
	
	private SPOGServer spogServer;
	private TestOrgInfo ti;
	private GatewayServer gatewayServer;
	private Source4SPOGServer source4SPOGServer;
	private SPOGHypervisorsServer spogHypervisorsServer;
	private SPOGDestinationServer spogDestinationServer;
	private UserSpogServer userSpogServer;
	//public int Nooftest;
	private ExtentTest test;
		
	private String site_version="1.0.0";
	private String gateway_hostname="chandrakanth";
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
	
	String direct_cloud_id;
	String msp_cloud_id;
	
	private String prefix;
	String[] datacenters;
	private Response response;
	
	LocalDate date = LocalDate.now();  
	LocalDate yesterday = date.minusDays(1);  
	LocalDate tomorrow = yesterday.plusDays(2);
	
	ArrayList<String> columnIdList = new ArrayList<String>();
	ArrayList<HashMap<String,Object>> columnsHeadContent = new ArrayList<HashMap<String,Object>>();
	
//	private String  org_model_prefix=this.getClass().getSimpleName();
	
	@BeforeClass
	@Parameters({ "baseURI", "port","csrAdminUserName", "csrAdminPassword","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword, String logFolder,String runningMachine,String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		source4SPOGServer = new Source4SPOGServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		spogHypervisorsServer = new SPOGHypervisorsServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		rep = ExtentManager.getInstance(this.getClass().getSimpleName(), logFolder);
		test = rep.startTest("Setup for "+this.getClass().getSimpleName());
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

	}	
	
	@DataProvider(name="sourceInfo")
	public Object[][] sourceInfo(){
		return new Object[][] {
			//400 - direct
			{"Convert to source to agentless where source_id = invalid with direct org user token",
					ti.direct_org1_user1_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID },
			
			//root msp
			{"Convert to source to agentless where source_id = invalid with root msp token",
					ti.root_msp_org1_user1_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID },
			{"Convert to source to agentless where source_id = invalid with msp account admin token of root msp",
					ti.root_msp_org1_msp_accountadmin1_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID },
			{"Convert to source to agentless where source_id = invalid with sub org user token of root msp",
					ti.root_msp1_suborg1_user1_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID },
			
			//root - sub msp
			{"Convert to source to agentless where source_id = invalid with sub MSP user token under root msp",
					ti.root_msp1_submsp1_user1_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID },
			{"Convert to source to agentless where source_id = invalid with sub MSP account admin token of root msp",
					ti.root_msp1_submsp1_account_admin_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID },
			{"Convert to source to agentless where source_id = invalid with sub org user token of sub msp under root msp",
					ti.msp1_submsp1_suborg1_user1_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID },
			
			//normal msp
			{"Convert to source to agentless where source_id = invalid with normal msp token",
					ti.normal_msp_org1_user1_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID },
			{"Convert to source to agentless where source_id = invalid with msp account admin token of normal msp",
					ti.normal_msp_org1_msp_accountadmin1_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID },
			{"Convert to source to agentless where source_id = invalid with sub org user token of normal msp",
					ti.normal_msp1_suborg1_user1_token, "invalid", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID },
						
			
			//401
			{"Convert to source to agentless where token = emptystring", "", UUID.randomUUID().toString(), SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT },
			{"Convert to source to agentless where token = invalid", "invalid", UUID.randomUUID().toString(), SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED },
			{"Convert to source to agentless where token = null", null, UUID.randomUUID().toString(), SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED },
			
			//404 - direct
			{"Convert to source to agentless where source_id = random_id in direct org",
					ti.direct_org1_user1_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.UNABLE_TO_FIND_THE_RESOURCE },
			
			//root msp
			{"Convert to source to agentless where source_id = random_id with root msp token",
					ti.root_msp_org1_user1_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.UNABLE_TO_FIND_THE_RESOURCE },
			{"Convert to source to agentless where source_id = random_id root msp account admin token",
					ti.root_msp_org1_msp_accountadmin1_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.UNABLE_TO_FIND_THE_RESOURCE },
			{"Convert to source to agentless where source_id = random_id with sub org user token of root msp",
					ti.root_msp1_suborg1_user1_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.UNABLE_TO_FIND_THE_RESOURCE },
			
			//root - sub msp
			{"Convert to source to agentless where source_id = random_id with sub msp token",
					ti.root_msp1_submsp1_user1_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.UNABLE_TO_FIND_THE_RESOURCE },
			{"Convert to source to agentless where source_id = random_id sub msp account admin token",
					ti.root_msp1_submsp1_account_admin_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.UNABLE_TO_FIND_THE_RESOURCE },
			{"Convert to source to agentless where source_id = random_id with sub org user token of sub msp",
					ti.msp1_submsp1_suborg1_user1_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.UNABLE_TO_FIND_THE_RESOURCE },
			
			//normal msp
			{"Convert to source to agentless where source_id = random_id with normal msp token",
					ti.normal_msp_org1_user1_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.UNABLE_TO_FIND_THE_RESOURCE },
			{"Convert to source to agentless where source_id = random_id normal msp account admin token",
					ti.normal_msp_org1_msp_accountadmin1_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.UNABLE_TO_FIND_THE_RESOURCE },
			{"Convert to source to agentless where source_id = random_id with sub org user token of normal msp",
					ti.normal_msp1_suborg1_user1_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.UNABLE_TO_FIND_THE_RESOURCE },
				
		};
	}
	
	@Test(dataProvider="sourceInfo",enabled=true)
	public void converToAgentlessTest(String caseType, 
										String token,
										String source_id, 
										int expectedStatusCode,
										SpogMessageCode expectedErrorMessage
										) {
		
		test = ExtentManager.getNewTest(caseType);
		
		source4SPOGServer.convertToAgentlessBySourceIdWithCheck(token, source_id, expectedStatusCode, expectedErrorMessage, test);		
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
