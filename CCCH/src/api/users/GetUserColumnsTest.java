package api.users;


import static org.testng.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Properties;

import org.apache.commons.lang.RandomStringUtils;
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
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.CreateOrgsInfo;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class GetUserColumnsTest extends base.prepare.Is4Org{
	private SPOGServer spogServer;
	private TestOrgInfo ti;
	private SPOGDestinationServer spogDestinationServer;
	private String directOrgId;
	private String mspOrgId;
	private String mspOrgId1;
	private String accountOrgId;

	private String csrOrgId;
	private UserSpogServer userSpogServer;

	private ExtentTest test;
	//this is for update portal, each testng class is taken as BQ set
	//	  private SQLServerDb bqdb1;
	//	  private ExtentReports rep;
	//	  public int Nooftest;
	//	  private long creationTime;
	//	  private String BQName=null;
	//	  private String runningMachine;
	//	  private testcasescount count1;
	//	  private String buildVersion;


	
	private String  org_model_prefix=this.getClass().getSimpleName();
	//end 
	@BeforeClass
	@Parameters({ "baseURI", "port","csrAdminUserName", "csrAdminPassword", "logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String csrAdminUserName, String csrAdminPassword, String logFolder, String runningMachine, String buildVersion) {
		//this is for update portal
		this.BQName = this.getClass().getSimpleName();
		String author = "Rakesh.Chalamala";
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		this.runningMachine = runningMachine;
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date=new java.util.Date();
		this.buildVersion=buildVersion+"_"+dateFormater.format(date);
		userSpogServer = new UserSpogServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		Nooftest=0;
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		if(count1.isstarttimehit()==0) {
			System.out.println("Into get GetUserColumnsTest");
			creationTime=System.currentTimeMillis();
			count1.setcreationtime(creationTime);
			//creationTime = System.currentTimeMillis();
			try {
				bqdb1.updateTable(this.BQName, runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0", String.valueOf(Nooftest), count1.getcreationtime(), "InProgress",author+" and Rest server is "+baseURI.split("//")[1]);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//end

		spogServer = new SPOGServer(baseURI, port);
		rep = ExtentManager.getInstance(this.getClass().getSimpleName(),logFolder);
		test = rep.startTest("beforeClass");
		
		ti = new TestOrgInfo(spogServer, test);
		
	}

	@DataProvider(name = "loginUserToCheckUserColumns")
	public final Object[][] getloginUserToCheckJobColumns() {
		return new Object[][] {
			{"Get user columns with csr user token", ti.csr_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get user columns with csr readonly user token", ti.csr_readonly_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get user columns with direct org user token", ti.direct_org1_user1_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get user columns with root msp org user token", ti.root_msp_org1_user1_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get user columns with normal msp org user token", ti.normal_msp_org1_user1_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get user columns with sub msp org user token", ti.root_msp1_submsp1_user1_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get user columns with root msp account admin user token", ti.root_msp_org1_msp_accountadmin1_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get user columns with normal msp account admin user token", ti.normal_msp_org1_msp_accountadmin1_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get user columns with sub msp account admin user token", ti.root_msp1_submsp1_account_admin_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get user columns with root msp's sub org user token", ti.root_msp1_suborg1_user1_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get user columns with normal msp's sub org user token", ti.normal_msp1_suborg1_user1_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			{"Get user columns with sub msp's sub org user token", ti.msp1_submsp1_suborg1_user1_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
		};
	}
	@Test(dataProvider = "loginUserToCheckUserColumns")
	public void checkUserColumnsWithDifferentUsers(String testCase, String token, int expectedStatusCode, SpogMessageCode expectedErrorMessage){	 
		
		test=ExtentManager.getNewTest(testCase);
		test.assignAuthor("Rakesh ,Chalamala");
		
		userSpogServer.setToken(token);
		Response response= userSpogServer.getUserColumnsWithCheck(expectedStatusCode,expectedErrorMessage,test);
		test.log(LogStatus.PASS,"The response generated is :"+response.getBody().asString());
	}

	@AfterMethod
	public void getResult(ITestResult result){
		if(result.getStatus() == ITestResult.FAILURE){
			count1.setfailedcount();		
			test.log(LogStatus.FAIL, "Test Case Failed is "+result.getName()+" with parameters as "+Arrays.asList(result.getParameters()) );
			test.log(LogStatus.FAIL, result.getThrowable().getMessage());
		}else if(result.getStatus() == ITestResult.SKIP){
			count1.setskippedcount();
			test.log(LogStatus.SKIP, "Test Case Skipped is "+result.getName());
		}else if(result.getStatus()==ITestResult.SUCCESS){
			count1.setpassedcount();
		}
		// ending test
		//endTest(logger) : It ends the current test and prepares to create HTML report
		rep.endTest(test);	
	}

}