package api.branding;

import java.io.IOException;
import java.text.SimpleDateFormat;
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
import InvokerServer.BrandingSpogSever;
import InvokerServer.GatewayServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import base.prepare.TestOrgInfo;
import dataPreparation.JsonPreparation;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class TurnOnBrandingEmailer extends base.prepare.Is4Org  {

	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private UserSpogServer userSpogServer;
	private BrandingSpogSever brandingSpogSever;
	private TestOrgInfo ti;
	private static JsonPreparation jp = new JsonPreparation();
	String prefix = RandomStringUtils.randomAlphanumeric(8);
	//public int Nooftest;
	//private ExtentReports rep;
	private ExtentTest test;

	private String site_version="1.0.0";
	private String gateway_hostname="Malleswari";
	//used for test case count like passed,failed,remaining cases
	//private testcasescount count1;
	int passedcases=0;
	int failedcases=0;
	int skippedcases=0;
	private String  org_model_prefix=this.getClass().getSimpleName();

	@BeforeClass
	@Parameters({ "baseURI", "port","csrAdminUserName", "csrAdminPassword","csrReadOnlyAdminName","csrReadOnlyAdminPassword","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword,String csrReadOnlyAdminName,String csrReadOnlyAdminPassword, String logFolder,String runningMachine,String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		brandingSpogSever = new BrandingSpogSever(baseURI, port);
		rep = ExtentManager.getInstance(this.getClass().getSimpleName(), logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Malleswari.S";

		Nooftest=0;
		ti = new TestOrgInfo(spogServer, test);	
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
	}




	@DataProvider(name = "Turn_on_Branding_Emailer")
	public final Object[][] createBrandingValidParams() {
		return new Object[][] {
			// different role id
			{ "Turn_On_Branding Emailer for the direct organziationwith the direct user valid token",ti.direct_org1_user1_token,ti.direct_org1_id, "eswari","https://tspog.zetta.com"+prefix,"#000455","#000455","test brandinginfo"},
			{ "Turn_On_Branding Emailer for the Noraml Msp organziation with the msp org user valid token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id, "eswari","https://tspog.arcserve.net"+prefix,"#000455","#000455","test brandinginfo"},
			{ "Turn_On_Branding Emailerfor the Normal mspaccpunt admin organziation with the msp org  user valid token",ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id,"eswari","https://tspog.arcserve.com"+prefix,"#000455","#000455","test brandinginfo"},
			{ "Turn_On_Branding Emailer for the root msp organziation with the msp account admin user valid token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id, "eswari","https://tspog.yahoo.net"+prefix,"#000455","#000455","test brandinginfo"},
			{ "Turn_On_Branding Emailer for the root msp organziation with the msp account admin user valid token",ti.root_msp1_submsp1_account_admin_token,ti.root_msp_org1_id, "eswari","https://tspog.yahoo.net"+prefix,"#000455","#000455","test brandinginfo"},
			{ "Turn_On_Branding Emailer for the Sub msp organziation with the msp account admin user valid token", ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp_org1_id,"eswari","https://tspog.yahoo.net"+prefix,"#000455","#000455","test brandinginfo"},
			{ "Turn_On_Branding Emailer for the Sub  msp account admin organziation with the msp account admin user valid token", ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_sub_org1_id,"eswari","https://tspog.yahoo.net"+prefix,"#000455","#000455","test brandinginfo"},

			{ "Turn_On_Branding Emailer  for the direct organziationwith the direct user valid token",ti.csr_token, ti.direct_org2_id,"eswari","https://tspog.zetta.com"+prefix,"#000456","#000455","test brandinginfo"},
			{ "Turn_On_Branding Emailer for the Noraml Msp organziation with the msp org user valid token",ti.csr_token, ti.normal_msp_org2_id,"eswari","https://tspog.arcserve.net"+prefix,"#000457","#000455","test brandinginfo"},
			{ "Turn_On_Branding Emailer for the root msp organziation with the msp account admin user valid token",ti.csr_token,ti.root_msp_org2_user1_id, "eswari","https://tspog.yahoo.net"+prefix,"#000455","#000475","test brandinginfo"},
			{ "Turn_On_Branding Emailerfor the Sub msp organziation with the msp account admin user valid token", ti.csr_token,ti.root_msp2_submsp_org1_id,"eswari","https://tspog.yahoo.net"+prefix,"#000455","#000495","test brandinginfo"},

		};
	}



	@Test(dataProvider = "Turn_on_Branding_Emailer")
	public void turnOnBrandingEmailerOrganization_200(String testcase,
			String validToken,
			String organization_id,
			String organization_name, 
			String portal_url, 
			String primary_color, 
			String secondary_color, 
			String branding_msg) {
		test=ExtentManager.getNewTest(testcase);

		ArrayList<HashMap<String,Object>> expectedresponse = new ArrayList<>();
		HashMap<String,Object> expected_response = new HashMap<String,Object>();

		brandingSpogSever.setToken(validToken);

		test.log(LogStatus.INFO, "Turn on Branding with the Invalid user token");
		brandingSpogSever.TurnonBrandingFororganizationwithchcek(organization_id,validToken="123", SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		test.log(LogStatus.INFO, "Turn on Branding with the Invalid user token");
		brandingSpogSever.TurnonBrandingFororganizationwithchcek(organization_id,"", SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);


	}


	@Test(dataProvider = "Turn_on_Branding_Emailer")
	public void turnOnBrandingEmailerOrganization_401(String testcase,
			String validToken,
			String organization_id,
			String organization_name, 
			String portal_url, 
			String primary_color, 
			String secondary_color, 
			String branding_msg) {
		test=ExtentManager.getNewTest(testcase);

		ArrayList<HashMap<String,Object>> expectedresponse = new ArrayList<>();
		HashMap<String,Object> expected_response = new HashMap<String,Object>();

		brandingSpogSever.setToken(validToken);

		test.log(LogStatus.INFO, "Create branding eamiler  in org of type");
		brandingSpogSever.TurnonBrandingEmailerFororganizationwithchcek(organization_id,validToken, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);


	}


	@DataProvider(name = "Turn_On_Branding_Emailer_Deleted_Org")
	public final Object[][] createBrandingDletedOrg() {
		return new Object[][] {
			{ "Turn_On_Branding to the randomuuid of org with the valid direct user token",ti.direct_org1_user1_token, "eswari","https://tspog.zetta.com"+prefix,"#000455","#000455","test brandinginfo"},
			{ "Turn_On_Branding to the randomuuid of org with the msp org user valid token",ti.normal_msp_org1_user1_token,"eswari","https://tspog.arcserve.net"+prefix,"#000455","#000455","test brandinginfo"},
			{ "Turn_On_Branding to the randomuuid of org with the msp account admin user valid token",ti.root_msp_org1_user1_token, "eswari","https://tspog.yahoo.net"+prefix,"#000455","#000455","test brandinginfo"},

			{ "Turn_On_Branding  to the randomuuid of org with the direct user valid token",ti.csr_token, "eswari","https://tspog.zetta.com"+prefix,"#000456","#000455","test brandinginfo"},
			{ "Turn_On_Branding to the randomuuid of org with the msp org user valid token",ti.csr_token,"eswari","https://tspog.arcserve.net"+prefix,"#000457","#000455","test brandinginfo"},
			{ "Turn_On_Brandingto the randomuuid of org with the msp account admin user valid token", ti.csr_token,"eswari","https://tspog.yahoo.net"+prefix,"#000455","#000495","test brandinginfo"},

		};
	}


	@Test(dataProvider = "Turn_On_Branding_Emailer_Deleted_Org")
	public void turnOnBrandingEmailerOrganization_404(String testcase,
			String validToken,
			String organization_name, 
			String portal_url, 
			String primary_color, 
			String secondary_color, 
			String branding_msg) {
		test=ExtentManager.getNewTest(testcase);

		ArrayList<HashMap<String,Object>> expectedresponse = new ArrayList<>();
		HashMap<String,Object> expected_response = new HashMap<String,Object>();

		brandingSpogSever.setToken(validToken);

		String randomUUID= UUID.randomUUID().toString();
		test.log(LogStatus.INFO, "Create branding eamiler  in org of type");
		brandingSpogSever.TurnonBrandingEmailerFororganizationwithchcek(randomUUID,ti.csr_token, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

	}



	@DataProvider(name = "Turn_on_branding_403")
	public final Object[][] TurnOnBrandingValidParams1() {
		return new Object[][] {
			// different role id
			{ "Turn On branding Emailer for the direct organziation with the another direct org user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.direct_org2_user2_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Turn On branding Emailer for the direct organziation with the normal msp  org user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.normal_msp_org1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Turn On branding Emailer for the direct organziation with the normal msp account admin user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Turn On branding Emailer for the direct organziation with the sub org user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.normal_msp1_suborg1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Turn On branding Emailer for the direct organziation with the root msp  org user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.root_msp_org1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Turn On branding Emailerfor the direct organziation with the root msp account admin  user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.root_msp_org1_msp_accountadmin1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Turn On branding Emailer for the direct organziation with the root msp sub organization user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.normal_msp1_suborg1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Turn On branding Emailer for the direct organziation with the sub msp organization user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.root_msp1_submsp1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Turn On branding Emailer for the direct organziation with the sub msp account admin organization user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.root_msp1_submsp1_account_admin_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Turn On branding Emailer for the direct organziation with the sub msp sub organization user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.root_msp1_suborg1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},

			//Normal MSP Organizations with other roles users token 
			{ "Turn On branding Emailer for the Noraml MSP organziation with the another direct org user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.direct_org1_user2_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Turn On branding Emailer for the Noraml MSP organziation with the normal msp2 org user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.normal_msp_org2_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Turn On branding Emailer for the Noraml MSP organziation with the normal msp account admin user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.normal_msp_org2_msp_accountadmin1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Turn On branding Emailer for the Noraml MSP organziation with the sub org user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.normal_msp1_suborg1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Turn On branding Emailerfor the Noraml MSP organziation with the root msp  org user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.root_msp_org2_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Turn On branding Emailer for the Noraml MSP organziation with the root msp account admin  user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.root_msp_org1_msp_accountadmin1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Turn On branding Emailer for the Noraml MSP organziation with the root msp sub organization user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.normal_msp1_suborg1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Turn On branding Emailer for the Noraml MSP organziation with the sub msp organization user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.root_msp1_submsp1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Turn On branding Emailer for the Noraml MSP organziation with the sub msp account admin organization user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.root_msp1_submsp1_account_admin_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Turn On branding Emailerfor the Noraml MSP organziation with the sub msp sub organization user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.root_msp1_suborg1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},


			//Normal MSP Organizations with other roles users token 
			{ "Turn On branding Emailer for the Root Msp organziation with the direct org user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.direct_org1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Turn On branding Emailer for the Root Msp organziation with the normal msp  org user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.normal_msp_org1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Turn On branding Emailer for the Root Msp organziation with the normal msp account admin user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Turn On branding Emailer for the Root Msp organziation with the sub org user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.normal_msp1_suborg1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Turn On branding Emailer for the Root Msp organziation with the root msp  org user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.root_msp_org2_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Turn On branding Emailer for the Root Msp organziation with the root msp account admin  user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.root_msp_org2_msp_accountadmin1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Turn On branding Emailer  for the Root Msp organziation with the root msp sub organization user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.normal_msp1_suborg1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Turn On branding Emailer for the Root Msp organziation with the sub msp organization user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.root_msp1_submsp1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Turn On branding Emailer for the Root Msp organziation with the sub msp account admin organization user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.root_msp1_submsp1_account_admin_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Turn On brandingEmailer for the Root Msp organziation with the sub msp sub organization user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.root_msp1_suborg1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},

		};
	}



	@Test(dataProvider = "Turn_on_branding_403")
	public void turnOnBrandingEmailerOrganization_403(String testcase,
			String validToken,
			String organization_id,
			String otherorgtoken,
			String organization_name, 
			String portal_url, 
			String primary_color, 
			String secondary_color, 
			String branding_msg){
		test=ExtentManager.getNewTest(testcase);
		String filter_Id = null;
		ArrayList<HashMap<String,Object>> expectedresponse = new ArrayList<>();
		HashMap<String,Object> expected_response = new HashMap<String,Object>();

		brandingSpogSever.setToken(otherorgtoken);

		test.log(LogStatus.INFO, "TurnOn branding eamiler  in org of type");
		brandingSpogSever.TurnonBrandingEmailerFororganizationwithchcek(organization_id,validToken, SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY, test);

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