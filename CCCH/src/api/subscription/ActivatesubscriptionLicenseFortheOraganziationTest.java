package api.subscription;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
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
import Constants.volume_type;
import InvokerServer.GatewayServer;
import InvokerServer.LicenseServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGReportServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class ActivatesubscriptionLicenseFortheOraganziationTest  extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private UserSpogServer userSpogServer;
	private Policy4SPOGServer policy4SpogServer; 
	private SPOGDestinationServer spogDestinationServer;
	private SPOGReportServer spogreportServer;
	private LicenseServer licenseServer;
	private ExtentTest test;
	private TestOrgInfo ti;
	//public int Nooftest;
	//private ExtentReports rep;


	@BeforeClass
	@Parameters({ "baseURI", "port","csrAdminUserName", "csrAdminPassword","csrReadOnlyAdminName","csrReadOnlyAdminPassword","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword,String csrReadOnlyAdminName,String csrReadOnlyAdminPassword, String logFolder,String runningMachine,String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		policy4SpogServer=new Policy4SPOGServer(baseURI,port); 
		spogDestinationServer=	new SPOGDestinationServer(baseURI,port); 
		spogreportServer=new  SPOGReportServer(baseURI,port); 
		licenseServer= new LicenseServer(baseURI,port);
		rep = ExtentManager.getInstance("GetBackupJobReportsDetailsTest", logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Sykam.Naga Malleswari";

		Nooftest=0;

		//Used for creating a build number with dateformat
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date=new java.util.Date();
		this.buildVersion=buildVersion+"_"+dateFormater.format(date);

		BQName=this.getClass().getSimpleName();
		this.runningMachine=runningMachine;
		System.out.println("The value of hit is "+count1.isstarttimehit());
		ti = new TestOrgInfo(spogServer, test);

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

	}



	@DataProvider(name = "ActiavteLicenseSubscription")
	public final Object[][] createUserFilterValidParams() {
		return new Object[][] {
			// different role id
			{ "Activate license for the Direct organziation with the direct user token ",ti.direct_org1_id,ti.direct_org1_user1_token,"cloud_direct",1,"1",SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL},
			{ "Activate license for the Normal  Msp organziation with the msp  user token",ti.normal_msp_org1_id,ti.normal_msp_org1_user1_token,"cloud_direct",1,"1",SpogConstants.SUCCESS_GET_PUT_DELETE,SpogMessageCode.SUCCESS_GET_PUT_DEL},


			//organiozation_id is not UUID

			{ "Activate license for the direct organziation where organziation_id is not UUID","123",ti.direct_org1_user1_token,"cloud_direct",1,"1",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_IS_NOT_UUID},
			{ "Activate license for the sub  organziation where organziation_id is not UUID","123",ti.normal_msp_org1_user1_token,"cloud_direct",1,"1",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_IS_NOT_UUID},
			{ "Activate license for the msp organziation where organziation_id is not UUID","123",ti.root_msp_org1_user1_token,"cloud_direct",1,"1",SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_IS_NOT_UUID},

			//In valid organziation_id
			{ "Activate license for the direct organziation where organziation_id is randon uuid",UUID.randomUUID().toString(),ti.direct_org1_user1_token,"cloud_direct",1,"1",SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED},
			{ "Activate license for the sub  organziation where organziation_id is randon uuid",UUID.randomUUID().toString(),ti.normal_msp_org1_user1_token,"cloud_direct",1,"1",SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED},
			{ "Activate license for the msp organziation where organziation_id is randon uuid",UUID.randomUUID().toString(),ti.root_msp_org1_user1_token,"cloud_direct",1,"1",SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED},

			//Insufficient permissions

			//Direct Organization
			{ "Activate license for the Direct organziation with the another Direct user token ",ti.direct_org1_id,ti.direct_org2_user1_token,"cloud_direct",1,"1",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{ "Activate license for the Direct organziation with the Normal MSP  user token ",ti.direct_org1_id,ti.normal_msp_org1_user1_token,"cloud_direct",1,"1",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{ "Activate license for the Direct organziation with the Normal MSP accout admin  user token ",ti.direct_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,"cloud_direct",1,"1",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{ "Activate license for the Direct organziation with the Customer account admin of Normal Msp account admin user ",ti.direct_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,"cloud_direct",1,"1",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{ "Activate license for the Direct organziation with the Root MSP  user token ",ti.direct_org1_id,ti.root_msp_org1_user1_token,"cloud_direct",1,"1",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{ "Activate license for the Direct organziation with the Root MSP accout admin  user token ",ti.direct_org1_id,ti.root_msp_org1_msp_accountadmin1_token,"cloud_direct",1,"1",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{ "Activate license for the Direct organziation with the Customer account admin of Root Msp account admin user ",ti.direct_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,"cloud_direct",1,"1",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},

			{ "Activate license for the Direct organziation with the Sub MSP  user token ",ti.direct_org1_id,ti.root_msp1_submsp1_user1_token,"cloud_direct",1,"1",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{ "Activate license for the Direct organziation with the Sub MSP accout admin  user token ",ti.direct_org1_id,ti.root_msp1_submsp1_account_admin_token,"cloud_direct",1,"1",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{ "Activate license for the Direct organziation with the Customer account admin of Sub Msp account admin user ",ti.direct_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,"cloud_direct",1,"1",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},

			//Normal Msp Organization
			{ "Activate license for the Normal  Msp organziation with the Direct  user token",ti.normal_msp_org1_id,ti.direct_org1_user1_token,"cloud_direct",1,"1",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{ "Activate license for the Normal  Msp organziation with the Another Normal msp  user token",ti.normal_msp_org1_id,ti.normal_msp_org2_user1_token,"cloud_direct",1,"1",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{ "Activate license for the Normal  Msp organziation with the Another Normal msp account admin user token",ti.normal_msp_org1_id,ti.normal_msp_org2_msp_accountadmin1_token,"cloud_direct",1,"1",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{ "Activate license for the Normal  Msp organziation with the Another Customer account of Normal msp  token",ti.normal_msp_org1_id,ti.normal_msp1_suborg1_user1_token,"cloud_direct",1,"1",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{ "Activate license for the Normal  Msp organziation with the Another root msp  user token",ti.normal_msp_org1_id,ti.root_msp_org1_user1_token,"cloud_direct",1,"1",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{ "Activate license for the Normal  Msp organziation with the Another root msp account admin user token",ti.normal_msp_org1_id,ti.root_msp_org1_msp_accountadmin1_token,"cloud_direct",1,"1",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{ "Activate license for the Normal  Msp organziation with the Another Customer account of root msp  token",ti.normal_msp_org1_id,ti.root_msp1_submsp1_user1_token,"cloud_direct",1,"1",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{ "Activate license for the Normal  Msp organziation with the Another sub msp  user token",ti.normal_msp_org1_id,ti.root_msp1_submsp1_user1_token,"cloud_direct",1,"1",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{ "Activate license for the Normal  Msp organziation with the Another sub msp account admin user token",ti.normal_msp_org1_id,ti.root_msp1_submsp1_account_admin_token,"cloud_direct",1,"1",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{ "Activate license for the Normal  Msp organziation with the Another Customer account of sub msp  token",ti.normal_msp_org1_id,ti.msp1_submsp1_suborg1_user1_token,"cloud_direct",1,"1",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},


			//Root Msp Organization
			{ "Activate license for the Root  Msp organziation with the Direct  user token",ti.root_msp_org1_id,ti.direct_org1_user1_token,"cloud_direct",1,"1",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{ "Activate license for the Root  Msp organziation with the Another Normal msp  user token",ti.root_msp_org1_id,ti.normal_msp_org2_user1_token,"cloud_direct",1,"1",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{ "Activate license for the Root  Msp organziation with the Another Normal msp account admin user token",ti.root_msp_org1_id,ti.normal_msp_org2_msp_accountadmin1_token,"cloud_direct",1,"1",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{ "Activate license for the Root  Msp organziation with the Another Customer account of Normal msp  token",ti.root_msp_org1_id,ti.normal_msp1_suborg1_user1_token,"cloud_direct",1,"1",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{ "Activate license for the Root  Msp organziation with the Another root msp  user token",ti.root_msp_org1_id,ti.root_msp_org2_user1_token,"cloud_direct",1,"1",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{ "Activate license for the Root  Msp organziation with the Another root msp account admin user token",ti.root_msp_org1_id,ti.root_msp_org2_msp_accountadmin1_token,"cloud_direct",1,"1",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{ "Activate license for the Root  Msp organziation with the Another Customer account of root msp  token",ti.root_msp_org1_id,ti.root_msp2_submsp1_user1_token,"cloud_direct",1,"1",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{ "Activate license for the Root  Msp organziation with the Another sub msp  user token",ti.root_msp_org1_id,ti.root_msp1_submsp1_user1_token,"cloud_direct",1,"1",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{ "Activate license for the Root  Msp organziation with the Another sub msp account admin user token",ti.root_msp_org1_id,ti.root_msp1_submsp1_account_admin_token,"cloud_direct",1,"1",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{ "Activate license for the Root  Msp organziation with the Another Customer account of sub msp  token",ti.root_msp_org1_id,ti.msp1_submsp1_suborg1_user1_token,"cloud_direct",1,"1",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY},

		};
	}


	@Test(dataProvider = "ActiavteLicenseSubscription")
	public void activateLicenseandSubScription(String testcase,
			String organziation_id,
			String validToken,
			String ProductType,
			int billingdate,
			String capacity,
			int expctedststatuccode,
			SpogMessageCode ExpectedErrorMessage

			) 
	{
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+testcase);

		licenseServer.setToken(validToken);		
		HashMap<String, Object> biling_info = null;

		test.log(LogStatus.INFO, "Compose the log columns as per the user request");
		biling_info = licenseServer.composebiling_info(ProductType,billingdate,capacity);					
		licenseServer.ActivateLicensefororagnziation(validToken,organziation_id,biling_info,expctedststatuccode,ExpectedErrorMessage,test);
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
