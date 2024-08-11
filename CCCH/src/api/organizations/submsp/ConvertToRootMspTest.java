package api.organizations.submsp;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.text.StrBuilder;
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
import InvokerServer.SPOGReportServer;
import InvokerServer.SPOGServer;
import InvokerServer.Source4SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.CreateOrgsInfo;
import base.prepare.TestOrgInfo;
import dataPreparation.JsonPreparation;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class ConvertToRootMspTest extends base.prepare.Is4Org {
	
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private UserSpogServer userSpogServer;
	private SPOGReportServer spogReportServer;
	private SPOGDestinationServer spogDestinationServer;
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
    private String buildVersion;	*/
	
	String[] datacenters;
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
		userSpogServer = new UserSpogServer(baseURI, port);
		spogReportServer = new SPOGReportServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		source4SPOGServer = new Source4SPOGServer(baseURI, port);
		
		rep = ExtentManager.getInstance(this.getClass().getSimpleName(), logFolder);
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

			creationTime = System.currentTimeMillis();
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
		
		
		userSpogServer.setToken(ti.csr_token);
		Response response =userSpogServer.addOrderByOrgId(ti.normal_msp_org1_id,"SKUTESTDATA_10_8_0_1_"+RandomStringUtils.randomNumeric(8), "SKUTESTDATA_10_8_0_1_"+RandomStringUtils.randomNumeric(8), test);
        spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST);
	}

	@DataProvider(name="testCases")
	public Object[][] testCases(){
		return new Object[][] {
			//200
			{"Convert MSP organization to ROOT MSP with csr token success with 200", 
				ti.csr_token, ti.root_msp_org1_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			
			//400
			{"Convert MSP organization to ROOT MSP with invalid organization id fails with 400 error", 
				ti.csr_token, "invalidOrgId", SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_ORGANIZATIONID_IS_NOT_UUID},
			
			//401
			{"Convert MSP organization to ROOT MSP with invalid token fails with 401 error", 
				"invalid", ti.root_msp_org1_id, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT},
			{"Convert MSP organization to ROOT MSP with missing token fails with 401 error", 
				"", ti.root_msp_org1_id, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED},
			{"Convert MSP organization to ROOT MSP with null as token fails with 401 error", 
				null, ti.root_msp_org1_id, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED},
			
			//403
			{"Convert MSP organization to ROOT MSP with direct org user token fails with 403 error", 
				ti.direct_org1_user1_token, ti.root_msp_org1_id, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Convert MSP organization to ROOT MSP with Msp2 user token fails with 403 error", 
				ti.root_msp_org2_user1_token, ti.root_msp_org1_id, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Convert MSP organization to ROOT MSP with msp account admin token fails with 403 error", 
				ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_id, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Convert MSP organization to ROOT MSP with sub org user token fails with 403 error", 
				ti.root_msp1_suborg1_user1_token, ti.root_msp_org1_id, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Convert MSP organization to ROOT MSP with csr read only token fails with 403 error", 
				ti.csr_readonly_token, ti.root_msp_org1_id, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			
			//403 - try to convert direct, sub, csr to root msp
			{"Convert Direct organization to ROOT MSP with csr token fails with 403 error", 
				ti.csr_token, ti.direct_org1_id, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.ORGANIZATION_CANT_BE_CHANGED_TO_ROOT_MSP},
			{"Convert Sub organization to ROOT MSP with csr token fails with 403 error", 
				ti.csr_token, ti.root_msp1_suborg1_id, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.ORGANIZATION_CANT_BE_CHANGED_TO_ROOT_MSP},
			{"Convert CSr organization to ROOT MSP with csr token fails with 403 error", 
				ti.csr_token, ti.csr_org_id, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.ORGANIZATION_CANT_BE_CHANGED_TO_ROOT_MSP},
			
			//403 - Licensed org
			{"Convert Licensed MSP organization to ROOT MSP with csr token fails with 403 error", 
				ti.csr_token, ti.normal_msp_org1_id, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.ORGANIZATION_CANT_BE_CHANGED_TO_ROOT_MSP},
			
			//404
			{"Convert MSP organization to ROOT MSP with organization id that does not exist fails with 404 error", 
				ti.csr_token, UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED},
		};
	}
	
	@Test(dataProvider="testCases")
	public void createSubMspTests(String testCase,
									String token,
									String orgId,
									int expectedStatusCode,
									SpogMessageCode expectedErrorMessage
									) {
		test=ExtentManager.getNewTest(testCase);
		
		test.log(LogStatus.INFO, "Convert organization with id:"+orgId+" to root MSP");
		spogServer.convertToRootMspWithCheck(token, orgId, expectedStatusCode, expectedErrorMessage, test);
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
	/******************************************************************RandomFunction******************************************************************************/
	public int gen_random_index(String[] job_status) {
		Random generator = new Random();
		int randomindx = generator.nextInt(job_status.length);

		return randomindx;
	}
}
