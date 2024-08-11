package api.businessintelligencereports;

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

import com.google.common.hash.HashingInputStream;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGHypervisorsServer;
import InvokerServer.SPOGReportServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;
import ui.base.common.SPOGMenuTreePath;

public class BusinessIntelligenceReportsTypesChTrial extends base.prepare.Is4Org {

	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private SPOGDestinationServer spogDestinationServer;
	private UserSpogServer userSpogServer;
	private SPOGReportServer spogReportServer;
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

	private String prefix;
	private Response response;

	LocalDate date = LocalDate.now();  
	LocalDate yesterday = date.minusDays(1);  
	LocalDate tomorrow = yesterday.plusDays(2);

	ArrayList<String> columnIdList = new ArrayList<String>();
	ArrayList<HashMap<String,Object>> columnsHeadContent = new ArrayList<HashMap<String,Object>>();

	private String  org_model_prefix=this.getClass().getSimpleName();
	private TestOrgInfo ti;

	@BeforeClass
	@Parameters({ "baseURI", "port","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String logFolder,String runningMachine,String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		spogReportServer = new SPOGReportServer(baseURI, port);
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
	
	@DataProvider(name="reportsTypesData")
	public Object[][] reportsTypesData(){
		return new Object[][] {
			//200 cases
			{"Get Business intelligence reports types ch_trial with csr token",ti.csr_token,SpogConstants.SUCCESS_GET_PUT_DELETE,null,test},
			{"Get Business intelligence reports types ch_trial with csr readonly user token",ti.csr_readonly_token,SpogConstants.SUCCESS_GET_PUT_DELETE,null,test},
			
			//403 cases
			{"Get Business intelligence reports types ch_trial with direct org user token",ti.direct_org1_user1_token,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.FORBIDDEN_TO_VISIT_THE_RESOURCE,test},
			{"Get Business intelligence reports types ch_trial with msp org user token",ti.root_msp_org1_user1_token,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.FORBIDDEN_TO_VISIT_THE_RESOURCE,test},
			{"Get Business intelligence reports types ch_trial with sub org user token",ti.root_msp1_suborg1_user1_token,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.FORBIDDEN_TO_VISIT_THE_RESOURCE,test},
			{"Get Business intelligence reports types ch_trial with sub msp org user token",ti.root_msp1_submsp1_user1_token,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.FORBIDDEN_TO_VISIT_THE_RESOURCE,test},
			{"Get Business intelligence reports types ch_trial with sub msp sub org user token",ti.msp1_submsp1_suborg1_user1_token,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.FORBIDDEN_TO_VISIT_THE_RESOURCE,test},
			{"Get Business intelligence reports types ch_trial with msp_account_admin token",ti.root_msp_org1_msp_accountadmin1_token,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.FORBIDDEN_TO_VISIT_THE_RESOURCE,test},
			
			//401 cases
			{"Get Business intelligence reports types ch_trial with invalid token",ti.csr_token+"j",SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT,test},
			{"Get Business intelligence reports types ch_trial with missing token","",SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTHENTICATION_FAILED,test},
			
		};
	}
	
	@Test(dataProvider="reportsTypesData")
	public void bIReportsTypesChTrialValid(String caseType, String token, int expectedStatusCode, SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
		
		ArrayList<Object> expectedData = new ArrayList<>();
		
		test.log(LogStatus.INFO, caseType);
		spogReportServer.getBIReportsTypesChTrialWithCheck(token, expectedData, expectedStatusCode, ExpectedErrorMessage, test);
		
	}
	
	@DataProvider(name="reportsTypesDataCSV")
	public Object[][] reportsTypesDataCSV(){
		return new Object[][] {
			//200 cases
			{"Get Business intelligence reports types ch_trial ExportCSV with csr token",ti.csr_token,SpogConstants.SUCCESS_GET_PUT_DELETE,null,test},
			{"Get Business intelligence reports types ch_trial ExportCSV with csr readonly user token",ti.csr_readonly_token,SpogConstants.SUCCESS_GET_PUT_DELETE,null,test},

			//403 cases
			{"Get Business intelligence reports types ch_trial ExportCSV with direct org user token",ti.direct_org1_user1_token,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.FORBIDDEN_TO_VISIT_THE_RESOURCE,test},
			{"Get Business intelligence reports types ch_trial ExportCSV with msp org user token",ti.root_msp_org1_user1_token,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.FORBIDDEN_TO_VISIT_THE_RESOURCE,test},
			{"Get Business intelligence reports types ch_trial ExportCSV with sub org user token",ti.root_msp1_suborg1_user1_token,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.FORBIDDEN_TO_VISIT_THE_RESOURCE,test},
			{"Get Business intelligence reports types ch_trial ExportCSV with sub msp org user token",ti.root_msp1_submsp1_user1_token,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.FORBIDDEN_TO_VISIT_THE_RESOURCE,test},
			{"Get Business intelligence reports types ch_trial ExportCSV with sub msp sub org user token",ti.msp1_submsp1_suborg1_user1_token,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.FORBIDDEN_TO_VISIT_THE_RESOURCE,test},
			{"Get Business intelligence reports types ch_trial ExportCSV with msp_account_admin token",ti.root_msp_org1_msp_accountadmin1_token,SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.FORBIDDEN_TO_VISIT_THE_RESOURCE,test},

			//401 cases
			{"Get Business intelligence reports types ch_trial ExportCSV with invalid token",ti.csr_token+"j",SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT,test},
			{"Get Business intelligence reports types ch_trial ExportCSV with missing token","",SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTHENTICATION_FAILED,test},
			
		};
	}
	
	@Test(dataProvider="reportsTypesDataCSV")
	public void bIReportsTypesChTrialExportCSV(String caseType, String token, int expectedStatusCode, SpogMessageCode ExpectedErrorMessage, ExtentTest test) {
		
		test.log(LogStatus.INFO, caseType);
		spogReportServer.getBIRChTrialExportCSVWithCheck(token, null, expectedStatusCode, ExpectedErrorMessage, test);
		
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
