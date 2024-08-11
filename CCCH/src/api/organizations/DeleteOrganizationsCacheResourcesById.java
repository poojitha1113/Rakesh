package api.organizations;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.UUID;

import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.Org4SPOGServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;

/** API - DELETE: /organizations/{id}/cache/resources
 * 
 * Test Organizations's cache resources can be deleted or not
 * Accessed only by CSR user
 * 
 * @author Rakesh.Chalamala
 * @sprint 33
 */

public class DeleteOrganizationsCacheResourcesById extends base.prepare.Is4Org {
	
	private SPOGServer spogServer;
	private TestOrgInfo ti;
	private UserSpogServer userSpogServer;
	private Org4SPOGServer org4SPOGServer;
	
	//public int Nooftest;
	private ExtentTest test;
		
	private String site_version="1.0.0";
	private String gateway_hostname="chandra";
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

//	private String  org_model_prefix=this.getClass().getSimpleName();
	
	@BeforeClass
	@Parameters({ "baseURI", "port","csrAdminUserName", "csrAdminPassword","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword, String logFolder,String runningMachine,String buildVersion) {
		
		spogServer = new SPOGServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		org4SPOGServer = new Org4SPOGServer(baseURI, port);
		
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
		System.out.println(ti.csr_token);
		
	}
		
	@DataProvider(name="casesInfo")
	public Object[][] casesInfo(){
		return new Object[][] {
			//200
			{"Delete Cache Resources of Direct organization using csr token", ti.csr_token, ti.direct_org1_id, 
				SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Delete Cache Resources of ROOT MSP organization using csr token", ti.csr_token, ti.root_msp_org1_id, 
				SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Delete Cache Resources of Normal MSP organization using csr token", ti.csr_token, ti.normal_msp_org1_id, 
					SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Delete Cache Resources of SUB MSP organization using csr token", ti.csr_token, ti.root_msp1_submsp_org1_id, 
						SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Delete Cache Resources of root msp's sub organization using csr token", ti.csr_token, ti.root_msp1_suborg1_id, 
				SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Delete Cache Resources of normal msp's sub organization using csr token", ti.csr_token, ti.normal_msp1_suborg1_id, 
					SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Delete Cache Resources of sub msp's sub organization using csr token", ti.csr_token, ti.msp1_submsp1_sub_org1_id, 
					SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			
			//400
			{"Delete Cache Resources in an organization using csr token where organization id is invalid", ti.csr_token, "invalid", 
				SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID},
			{"Delete Cache Resources in an organization using csr token where organization id is null", ti.csr_token, null, 
				SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID},
			
			//401
			{"Delete Cache Resources in an organization using missing token", "", UUID.randomUUID().toString(), 
				SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED},
			{"Delete Cache Resources in an organization using null as token", null, UUID.randomUUID().toString(), 
				SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED},
			{"Delete Cache Resources in an organization using invalid token", "invalid", UUID.randomUUID().toString(), 
				SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT},
			
			//403
			{"Delete Cache Resources in an organization using csr readonly user token", ti.csr_readonly_token, UUID.randomUUID().toString(), 
				SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Delete Cache Resources in an organization using Direct user token token", ti.direct_org1_user1_token, UUID.randomUUID().toString(), 
				SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.FORBIDDEN_TO_VISIT_THE_RESOURCE},
			{"Delete Cache Resources in an organization using ROOT MSP token", ti.root_msp_org1_user1_token, UUID.randomUUID().toString(), 
				SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.FORBIDDEN_TO_VISIT_THE_RESOURCE},
			{"Delete Cache Resources in an organization using Normal MSP token", ti.normal_msp_org1_user1_token, UUID.randomUUID().toString(), 
					SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.FORBIDDEN_TO_VISIT_THE_RESOURCE},
			{"Delete Cache Resources in an organization using SUB MSP token", ti.root_msp1_submsp1_user1_token, UUID.randomUUID().toString(), 
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.FORBIDDEN_TO_VISIT_THE_RESOURCE},
			{"Delete Cache Resources in an organization using ROOT msp account admin token", ti.root_msp_org1_msp_accountadmin1_token, UUID.randomUUID().toString(), 
				SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.FORBIDDEN_TO_VISIT_THE_RESOURCE},
			{"Delete Cache Resources in an organization using Normal msp account admin token", ti.normal_msp_org1_msp_accountadmin1_token, UUID.randomUUID().toString(), 
					SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.FORBIDDEN_TO_VISIT_THE_RESOURCE},
			{"Delete Cache Resources in an organization using SUB msp account admin token", ti.root_msp1_submsp1_account_admin_token, UUID.randomUUID().toString(), 
						SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.FORBIDDEN_TO_VISIT_THE_RESOURCE},
			{"Delete Cache Resources in an organization using root msp sub org token", ti.root_msp1_suborg1_user1_token, UUID.randomUUID().toString(), 
							SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.FORBIDDEN_TO_VISIT_THE_RESOURCE},
			{"Delete Cache Resources in an organization using normal msp sub org token", ti.normal_msp1_suborg1_user1_token, UUID.randomUUID().toString(), 
								SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.FORBIDDEN_TO_VISIT_THE_RESOURCE},
			{"Delete Cache Resources in an organization using sub msp sub org token", ti.msp1_submsp1_suborg1_user1_token, UUID.randomUUID().toString(), 
									SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.FORBIDDEN_TO_VISIT_THE_RESOURCE},
			
			
			//404
			{"Delete Cache Resources in an organization using csr token and deleted organizations id", ti.csr_token, UUID.randomUUID().toString(), 
				SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED},
		};
	}
	
	@Test(dataProvider="casesInfo")
	public void deleteOrgCacheResourcesTest(String caseType,
											String token,
											String organization_id,
											int expectedStatusCode,
											SpogMessageCode expectedErrorMessage
											) {
		
		test=ExtentManager.getNewTest(caseType);
		
		org4SPOGServer.deleteOrgCacheResourcesByOrgIdWithCheck(token, organization_id, expectedStatusCode, expectedErrorMessage, test);
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
