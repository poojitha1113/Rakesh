package api.dashboard;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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

public class GetDashboardAccountSummaryTest extends base.prepare.Is4Org {
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

	String direct_cloud_id;
	String msp_cloud_id;

	Response response;
	String[] datacenters;

	String direct_dest_id;
	String sub_dest_id;

	String direct_source_id;
	String msp_source_id;
	String suborg_source_id;	
	HashMap<String, Object> msp_exp = new HashMap<>();
	HashMap<String, Object> submsp_exp = new HashMap<>();
	
	private String  org_model_prefix=this.getClass().getSimpleName();
	private TestOrgInfo ti;
	private List<String> accounts= new ArrayList<>();

	@BeforeClass
	@Parameters({ "baseURI", "port","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port,String logFolder,String runningMachine,String buildVersion) {
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
		test.log(LogStatus.INFO, "Get the datacenter id");
		spogDestinationServer.setToken(ti.csr_token);
		datacenters = spogDestinationServer.getDestionationDatacenterID();
		
		postData("msp",ti.root_msp_org1_id,ti.root_msp_org1_user1_token, ti.root_msp_org1_msp_accountadmin1_id);
		postData("submsp",ti.root_msp1_submsp_org1_id,ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp1_account_admin_1_id);	
	}
	
	

	public void postData(String orgType, String org_id, String token, String accountAdminId) {

		int total_customer_count = 0,
			num_success_total_count=0,
			num_missed_total_count=0,
			num_failed_total_count= 0,
			j; 
		
		long start_time_ts = System.currentTimeMillis(),
			 end_time_ts = start_time_ts + 1000;
		String policy_id = UUID.randomUUID().toString(),
			   rps_id = UUID.randomUUID().toString();
		String source_id,
			   destination_id,
			   jobType = "backup";
		String[] jobStatus = {"finished", "missed", "failed"};
		String account_name, account_id;
		String prefix = spogServer.ReturnRandom("rak");		
		int noOfAccountToCreate = 6;
		total_customer_count = noOfAccountToCreate; // add suborgs created in beforeSuite	
		
		for (int i = 0; i < noOfAccountToCreate; i++) {
				j= gen_random_index(jobStatus);
				account_name = prefix+"_suborg_"+i;
				spogServer.setToken(token);
				spogDestinationServer.setToken(token);
				account_id = spogServer.createAccountWithCheck(org_id, account_name,org_id);
//				userSpogServer.assignMspAccountAdmins(org_id, account_id, new String[] {accountAdminId}, token);

				source_id= spogServer.createSourceWithCheck(spogServer.ReturnRandom("rak"), SourceType.machine, SourceProduct.cloud_direct, account_id, null,ProtectionStatus.unprotect, ConnectionStatus.online, "windows", "SQLSERVER", test);

				Response response = spogDestinationServer.getDestinations(token, "organization_id="+account_id, test);
				destination_id = response.then().extract().path("data[0].destination_id");
				if(destination_id==null || destination_id.isEmpty())
					destination_id = spogDestinationServer.createDestinationWithCheck(UUID.randomUUID().toString(),account_id, null, datacenters[1], DestinationType.cloud_direct_volume.toString(), DestinationStatus.running.toString(), 
															"20","","normal",RandomStringUtils.randomAlphanumeric(4)+"host-t", "2M", "2M", 
															"0","0", "31", "0", "2", "0", "5", "true", "1", "true",spogServer.ReturnRandom("dest"), test);			

				gatewayServer.postJobWithCheck(start_time_ts, end_time_ts,account_id,source_id, source_id, rps_id, destination_id, 
						policy_id,jobType, "full",jobStatus[j] ,token, test);

				switch (jobStatus[j]) {
				case "finished": num_success_total_count++;
				break;
				case "missed": num_missed_total_count++;
				break;
				case "failed": num_failed_total_count++;
				break;
				default:
					break;
				}
				
				accounts.add(account_name);
		}
		if (orgType.contains("submsp")) {
			submsp_exp.put("total_customer_count", total_customer_count+2);
			submsp_exp.put("num_success_total_count", num_success_total_count);
			submsp_exp.put("num_missed_total_count", num_missed_total_count);
			submsp_exp.put("num_failed_total_count", num_failed_total_count);
		}else if(orgType.contains("msp")){
			msp_exp.put("total_customer_count", total_customer_count+4);
			msp_exp.put("num_success_total_count", num_success_total_count);
			msp_exp.put("num_missed_total_count", num_missed_total_count);
			msp_exp.put("num_failed_total_count", num_failed_total_count);
		}
	}

	@DataProvider(name="inputData")
	public Object[][] inputData(){
		return new Object[][] {
			//200
			{"Get account summary of msp organization",ti.root_msp_org1_user1_token, ti.root_msp_org1_id, msp_exp,SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get account summary with msp account admin token",ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_id, msp_exp,SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get account summary of msp organization with csr token",ti.csr_token, ti.root_msp_org1_id, msp_exp,SpogConstants.SUCCESS_GET_PUT_DELETE, null},			
			{"Get account summary of msp organization & csr readonly user token",ti.csr_readonly_token, ti.root_msp_org1_id, msp_exp,SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get account summary of sub msp organization",ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp_org1_id, submsp_exp,SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get account summary with sub msp account admin token",ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp_org1_id, submsp_exp,SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get account summary of sub msp organization with csr token",ti.csr_token, ti.root_msp1_submsp_org1_id, submsp_exp,SpogConstants.SUCCESS_GET_PUT_DELETE, null},			
			{"Get account summary of sub msp organization & csr readonly user token",ti.csr_readonly_token, ti.root_msp1_submsp_org1_id, submsp_exp,SpogConstants.SUCCESS_GET_PUT_DELETE, null},
						
			//401
			{"Get account summary of an organization with invalid token","invalid", ti.root_msp_org1_id, msp_exp,SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT},
			{"Get account summary of an organization with missing token","", ti.root_msp_org1_id, msp_exp,SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED},
			
			//400
			{"Get account summary of direct organization with invalid org id ",ti.direct_org1_user1_token, "invalid", msp_exp,SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID},
			{"Get account summary of msp organization with invalid org id ",ti.root_msp_org1_user1_token, "invalid", msp_exp,SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID},
			{"Get account summary of sub organization with invalid org id ",ti.root_msp1_suborg1_user1_token, "invalid", msp_exp,SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID},
			{"Get account summary of sub msp organization with invalid org id ",ti.root_msp1_submsp1_user1_token, "invalid", submsp_exp,SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID},
			{"Get account summary of sub msp sub organization with invalid org id ",ti.msp1_submsp1_suborg1_user1_token, "invalid", submsp_exp,SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID},
			{"Get account summary of an organization with invalid org id using ti.csr_token",ti.csr_token, "invalid", msp_exp,SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID},
			{"Get account summary of an organization with invalid org id using csr readonly user token",ti.csr_readonly_token, "invalid", msp_exp,SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID},
			
			//404
			{"Get account summary of direct organization with org id that does not exist",ti.direct_org1_user1_token, UUID.randomUUID().toString(), msp_exp,SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED},
			{"Get account summary of msp organization with org id that does not exist",ti.root_msp_org1_user1_token, UUID.randomUUID().toString(), msp_exp,SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED},
			{"Get account summary of sub organization with org id that does not exist",ti.root_msp1_suborg1_user1_token, UUID.randomUUID().toString(), msp_exp,SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED},
			{"Get account summary of sub msp organization with org id that does not exist",ti.root_msp1_submsp1_user1_token, UUID.randomUUID().toString(), submsp_exp,SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED},
			{"Get account summary of sub msp sub organization with org id that does not exist",ti.msp1_submsp1_suborg1_user1_token, UUID.randomUUID().toString(), submsp_exp,SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED},
			{"Get account summary of an organization with org id that does not exist using csr token",ti.csr_token, UUID.randomUUID().toString(), msp_exp,SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED},
			{"Get account summary of an organization with org id that does not exist using csr_readonly user token",ti.csr_readonly_token, UUID.randomUUID().toString(), msp_exp,SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED},
			
			//403
			{"Get account summary of direct organization with msp token",ti.root_msp_org1_user1_token, ti.direct_org1_id, msp_exp,SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Get account summary of direct organization sub org user token",ti.root_msp1_suborg1_user1_token, ti.direct_org1_id, msp_exp,SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Get account summary of msp organization with direct token",ti.direct_org1_user1_token, ti.root_msp_org1_id, msp_exp,SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Get account summary of msp organization with sub org token",ti.root_msp1_suborg1_user1_token, ti.root_msp_org1_id, msp_exp,SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Get account summary of sub organization with direct token",ti.direct_org1_user1_token, ti.root_msp1_suborg1_id, msp_exp,SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Get account summary of sub organization with suborgb token",ti.root_msp1_suborg2_user1_token, ti.root_msp1_suborg1_id, msp_exp,SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
		};
	}

	@Test(dataProvider="inputData")
	public void getAccountSummary(String caseType, String token, String org_id, HashMap<String, Object> expectedResponse, int expectedStatusCode, SpogMessageCode expectedErrorMessage ) {

		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());

		HashMap<String, Object> expRes = new HashMap<>();
		expRes.putAll(expectedResponse);
		
		if (caseType.contains("account admin")) {
			expRes.put("total_customer_count", 1);
			expRes.put("num_success_total_count", 0);
			expRes.put("num_missed_total_count", 0);
			expRes.put("num_failed_total_count", 0);
		}
		
		test.log(LogStatus.INFO, caseType);
		spogReportServer.getOrganizationAccountSummary(token, org_id, expRes, expectedStatusCode, expectedErrorMessage, test);
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
