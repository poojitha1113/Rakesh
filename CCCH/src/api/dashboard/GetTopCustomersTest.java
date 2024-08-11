package api.dashboard;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class GetTopCustomersTest extends base.prepare.Is4Org {
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
	Response response;
	String[] datacenters;
	
	String direct_source_group_id_list;
	String msp_source_group_id_list;
	String suborg_source_group_id_list;
	
	String direct_source_id;
	String msp_source_id;
	String suborg_source_id;	
		
	ArrayList<HashMap<String, Object>> msp_exp = new ArrayList<>();
	ArrayList<HashMap<String, Object>> submsp_exp = new ArrayList<>();
	
	LocalDate date = LocalDate.now();  
	LocalDate yesterday = date.minusDays(1);  
	LocalDate tomorrow = yesterday.plusDays(2);

	private String  org_model_prefix=this.getClass().getSimpleName();
	private TestOrgInfo ti;
	private ArrayList<String> accounts = new ArrayList<>();
	
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
		
		spogDestinationServer.setToken(ti.csr_token);
		datacenters = spogDestinationServer.getDestionationDatacenterID();
		
		prepareData("msp", ti.root_msp_org2_user1_token, ti.root_msp_org2_id, ti.root_msp_org2_msp_accountadmin1_id, ti.root_msp_org2_name);
		prepareData("submsp", ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_account_admin_1_id, ti.root_msp1_submsp_org1_name);
	}
	
	public void prepareData(String orgType, String token, String org_id, String accountAdminId, String org_name){
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		String account_id, account_name, destination_id;
		String prefix = spogServer.ReturnRandom(orgType);
		HashMap<String, Object> accountInfo = null;
		
		for (int i = 0; i < 12; i++) {
			accountInfo = new HashMap<>();
			destination_id = null;
			account_name = prefix+"_suborg_"+i;
			spogServer.setToken(token);
			account_id = spogServer.createAccountWithCheck(org_id, account_name,org_id);
			userSpogServer.assignMspAccountAdmins(org_id, account_id, new String[] {accountAdminId}, token);
			
			spogDestinationServer.setToken(token);
			Response response = spogDestinationServer.getDestinations(token, "organization_id="+account_id, test);
			destination_id = response.then().extract().path("data[0].destination_id");
			if(destination_id == null || destination_id.isEmpty())
				destination_id = spogDestinationServer.createDestinationWithCheck(UUID.randomUUID().toString(),account_id, null, datacenters[0], DestinationType.cloud_direct_volume.toString(), DestinationStatus.running.toString(), 
														"20","","normal",RandomStringUtils.randomAlphanumeric(4)+"host-t", "2M", "2M", 
														"0","0", "31", "0", "2", "0", "5", "true", "1", "true",spogServer.ReturnRandom("dest"), test);

			spogDestinationServer.updateDestinationUsage(token, account_id, destination_id, String.valueOf(System.currentTimeMillis()), "1024"+i, "512"+i, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
			
			accountInfo.put("id", account_id);
			accountInfo.put("name", account_name);
			accountInfo.put("value", Integer.parseInt("1024"+i)+Integer.parseInt("512"+i));
			if(orgType.contains("submsp"))
				submsp_exp.add(accountInfo);
			else if(orgType.contains("msp"))
				msp_exp.add(accountInfo);
			
			accounts.add(account_name);
		}
	}	

	@DataProvider(name="testCases")
	public Object[][] testCases(){
		return new Object[][] {
			//200 - MSP
			{"Get top customers in msp organization with msp user token", ti.root_msp_org2_id, ti.root_msp_org2_user1_token, msp_exp, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get top customers in msp organization with msp account admin token", ti.root_msp_org2_id, ti.root_msp_org2_msp_accountadmin1_token, msp_exp, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get top customers in msp organization with csr user token", ti.root_msp_org2_id, ti.csr_token, msp_exp, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get top customers in msp organization with csr readonly user token", ti.root_msp_org2_id, ti.csr_readonly_token, msp_exp, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			
			//200 - SUB MSP
			{"Get top customers in sub msp organization with sub msp user token", ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_user1_token, submsp_exp, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get top customers in sub msp organization with sub msp account admin token", ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp1_account_admin_token, submsp_exp, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get top customers in sub msp organization with csr user token", ti.root_msp1_submsp_org1_id, ti.csr_token, submsp_exp, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get top customers in sub msp organization with csr readonly user token", ti.root_msp1_submsp_org1_id, ti.csr_readonly_token, submsp_exp, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			
			//401
			{"Get top customers in an msp organization with invalid token", ti.root_msp1_submsp_org1_id, "invalid", submsp_exp, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT},
			{"Get top customers in an msp organization with missing token", ti.root_msp1_submsp_org1_id, "", submsp_exp, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED},
			{"Get top customers in an msp organization with null as token", ti.root_msp1_submsp_org1_id, null, submsp_exp, SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED},
			
			//403
			{"Get top customers in msp organization with direct org user token", ti.root_msp_org2_id, ti.direct_org1_user1_token, msp_exp, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Get top customers in msp organization with msp2 org user token", ti.root_msp_org2_id, ti.root_msp_org1_user1_token, msp_exp, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Get top customers in msp organization with sub org user token", ti.root_msp_org2_id, ti.root_msp1_suborg1_user1_token, msp_exp, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Get top customers in msp organization with sub msp user token", ti.root_msp_org2_id, ti.root_msp1_submsp1_user1_token, msp_exp, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Get top customers in msp organization with sub msp sub org user token", ti.root_msp_org2_id, ti.msp1_submsp1_suborg1_user1_token, msp_exp, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Get top customers in msp organization with sub msp account admin token", ti.root_msp_org2_id, ti.root_msp2_submsp1_account_admin_token, msp_exp, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			
			{"Get top customers in sub msp organization with direct org user token", ti.root_msp1_submsp_org1_id, ti.direct_org1_user1_token, submsp_exp, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Get top customers in sub msp organization with msp org user token", ti.root_msp1_submsp_org1_id, ti.root_msp_org1_user1_token, submsp_exp, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Get top customers in sub msp organization with root msp sub org user token", ti.root_msp1_submsp_org1_id, ti.root_msp1_suborg1_user1_token, submsp_exp, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Get top customers in sub msp organization with sub msp2 user token", ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp2_user1_token, submsp_exp, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Get top customers in sub msp organization with sub msp sub org user token", ti.root_msp1_submsp_org1_id, ti.msp1_submsp1_suborg1_user1_token, submsp_exp, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Get top customers in sub msp organization with root msp account admin token", ti.root_msp1_submsp_org1_id, ti.root_msp_org1_msp_accountadmin1_token, submsp_exp, SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
		
		};
	}
	

	@Test(dataProvider="testCases")
	public void getTopCustomers(String caseType, String org_id, String token, ArrayList<HashMap<String, Object>> expectedResponse, int expectedStatusCode, SpogMessageCode expectedErrorMessage) {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		
		test.log(LogStatus.INFO, caseType);
		spogReportServer.getTopCustomersWithCheck(token,org_id, expectedResponse, expectedStatusCode, expectedErrorMessage, test);
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
	public void destroyAccounts() {
		for (String account : accounts) {
			recycleVolumeInCDandDestroyOrg(account);
		}
	}
	/******************************************************************RandomFunction******************************************************************************/
	public int gen_random_index(String[] job_status) {
		Random generator = new Random();
		int randomindx = generator.nextInt(job_status.length);

		return randomindx;
	}
}
