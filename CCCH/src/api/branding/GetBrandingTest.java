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

public class GetBrandingTest extends base.prepare.Is4Org {
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

	//private testcasescount count1;
	int passedcases=0;
	int failedcases=0;
	int skippedcases=0;
	private String  org_model_prefix=this.getClass().getSimpleName();

	/*private SQLServerDb bqdb1;
	public int Nooftest;

	long creationTime;
	String buildnumber=null;
	String BQame=null;
	private String running_Machine,buildVersion;*/


	/*private String runningMachine;
	private String buildVersion;*/
	@BeforeClass
	@Parameters({ "baseURI", "port","csrAdminUserName", "csrAdminPassword","csrReadOnlyAdminName","csrReadOnlyAdminPassword","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword,String csrReadOnlyAdminName,String csrReadOnlyAdminPassword, String logFolder,String runningMachine,String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		brandingSpogSever = new BrandingSpogSever(baseURI, port);
		rep = ExtentManager.getInstance("GetBrandingTest", logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "malleswari.sykam";

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




	//get the default branding information 

	@DataProvider(name = "Get_Branding")
	public final Object[][]getBrandingValidParams() {
		return new Object[][] {
			{ "get_default_brandingInfo_fordirectorg_with_directuser_token_200", "directorg",ti.direct_org1_user1_token, ti.direct_org1_id, "spog_udp_qa_" + ti.direct_org1_name, 200, null },
			{ "get_default_brandingInfo_for_mspuser_token_200", "suborg_mspusertoken",ti.normal_msp_org1_user1_token, ti.normal_msp_org1_id, ti.normal_msp_org1_name, 200, null },
			{ "get_default_brandingInfo of sub organization with the valid sub org token_200", "suborg",ti.normal_msp1_suborg1_user1_token, ti.normal_msp1_suborg1_id, ti.normal_msp1_suborg1_name, 200, null },
			{ "get_default_brandingInfo_formsp_org_with_mspaccpunt admin_user_token_200", "mspusertoken",ti.normal_msp_org1_msp_accountadmin1_token, ti.normal_msp_org1_id, "spog_udp_qa_" + ti.normal_msp_org1_name, 200, null },
			{ "get_default_brandingInfo of root msp roganization with the valid root msp user ", "rootmsp",ti.root_msp_org1_user1_token, ti.root_msp_org1_id, ti.root_msp_org1_name, 200, null },
			{ "get_default_brandingInfo of root msp roganization with the valid root msp account admin user ", "rootmsp_mspaccountadminuser",ti.root_msp_org1_msp_accountadmin1_token, ti.root_msp_org1_id, ti.root_msp_org1_name, 200, null },
			{ "get_default_brandingInfo of sub org of root oganization with the valid root msp user ", "Sub org of rootmsp",ti.root_msp_org1_user1_token, ti.root_msp1_suborg1_id, ti.root_msp1_suborg1_name, 200, null },
			{ "get_default_brandingInfo of sub msp of root oganization with the valid sub  msp user ", "Sub msp of rootmsp",ti.root_msp1_submsp1_user1_token, ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp_org1_name, 200, null },
			{ "get_default_brandingInfo of sub msp of root oganization with the valid msp account admin of sub msp user ", "Sub msp of rootmsp",ti.root_msp1_submsp1_account_admin_token, ti.root_msp1_submsp_org1_id, ti.root_msp1_submsp_org1_name, 200, null },
			{ "get_default_brandingInfo of  Customer account,sub msp of root oganization with the valid msp account admin of sub msp user ", "Customer account of Sub msp of rootmsp",ti.root_msp1_submsp1_account_admin_token, ti.msp1_submsp1_sub_org1_id, ti.msp1_submsp1_sub_org1_name, 200, null },


		};
	}



	@Test(dataProvider = "Get_Branding",priority=1)
	public void getDefaultBranding(
			String testcase,
			String organizationType,
			String validToken,
			String organization_id,
			String organization_name,
			int statuscode,
			String expectederroecode

			) {
		test=ExtentManager.getNewTest(testcase);

		ArrayList<HashMap<String,Object>> expectedresponse = new ArrayList<>();
		HashMap<String,Object> expected_response = new HashMap<String,Object>();

		brandingSpogSever.setToken(validToken);


		test.log(LogStatus.INFO, "Get Branding eamiler  in org of type "+organizationType);
		Response response = brandingSpogSever.getbrandingfoorganization("",test);
		//brandingSpogSever.verifybrandingemailerfororganizationwithcheck(organizationType,organization_name,"",statuscode, expectederroecode, test);


	}



	@DataProvider(name = "Get_Branding_info_200")
	public final Object[][] getBrandingValidParams2() {
		return new Object[][] {

			{ "Get_Branding  for the direct organziationwith the direct user valid token",ti.direct_org1_user1_token,ti.direct_org1_id, "eswari","https://tspog.zetta.com"+prefix,"#000455","#000455","test brandinginfo"},
			{ "Get_Branding for the Noraml Msp organziation with the msp org user valid token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id, "eswari","https://tspog.arcserve.net"+prefix,"#000455","#000455","test brandinginfo"},
			{ "Get_Branding for the Normal mspaccpunt admin organziation with the msp org  user valid token",ti.normal_msp_org1_msp_accountadmin1_token,ti.normal_msp_org1_msp_accountadmin1_id,"eswari","https://tspog.arcserve.com"+prefix,"#000455","#000455","test brandinginfo"},
			{ "Get_Branding for the root msp organziation with the msp account admin user valid token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id, "eswari","https://tspog.yahoo.net"+prefix,"#000455","#000455","test brandinginfo"},
			{ "Get_Branding for the root msp organziation with the msp account admin user valid token",ti.root_msp1_submsp1_account_admin_token,ti.root_msp_org1_id, "eswari","https://tspog.yahoo.net"+prefix,"#000455","#000455","test brandinginfo"},
			{ "Get_Branding for the Sub msp organziation with the msp account admin user valid token", ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp_org1_id,"eswari","https://tspog.yahoo.net"+prefix,"#000455","#000455","test brandinginfo"},
			{ "Get_Branding for the Sub  msp account admin organziation with the msp account admin user valid token", ti.msp1_submsp1_suborg1_user1_token,ti.msp1_submsp1_sub_org1_id,"eswari","https://tspog.yahoo.net"+prefix,"#000455","#000455","test brandinginfo"},

			{ "Get_Branding  for the direct organziationwith the direct user valid token",ti.csr_token, ti.direct_org2_id,"eswari","https://tspog.zetta.com"+prefix,"#000456","#000455","test brandinginfo"},
			{ "Get_Branding for the Noraml Msp organziation with the msp org user valid token",ti.csr_token, ti.normal_msp_org2_id,"eswari","https://tspog.arcserve.net"+prefix,"#000457","#000455","test brandinginfo"},
			{ "Get_Branding for the root msp organziation with the msp account admin user valid token",ti.csr_token,ti.root_msp_org2_id, "eswari","https://tspog.yahoo.net"+prefix,"#000455","#000475","test brandinginfo"},
			{ "Get_Branding for the Sub msp organziation with the msp account admin user valid token", ti.csr_token,ti.root_msp2_submsp_org1_id,"eswari","https://tspog.yahoo.net"+prefix,"#000455","#000495","test brandinginfo"},



		};
	}

	@Test(dataProvider = "Get_Branding_info_200",priority=2)
	public void getbrandingorganizationValid_200(
			String testcase,
			String organizationType,
			String validToken,
			String organization_id,
			String organization_name, 
			String portal_url, 
			String primary_color, 
			String secondary_color, 
			String branding_msg
			){
		test=ExtentManager.getNewTest(testcase);

		ArrayList<HashMap<String,Object>> expectedresponse = new ArrayList<>();
		HashMap<String,Object> expected_response = new HashMap<String,Object>();

		brandingSpogSever.setToken(validToken);

		/*	test.log(LogStatus.INFO, "Get Branding eamiler  in org of type "+organizationType);
		Response response = brandingSpogSever.createBrandingFororganization(organization_id,organization_name,portal_url,primary_color,secondary_color,branding_msg,test);
		brandingSpogSever.verifybrandingfororganization(response,organization_id,organization_name,portal_url,primary_color,secondary_color,branding_msg,"false",SpogConstants.SUCCESS_POST, null, test);
		 */
		Response response = brandingSpogSever.getbrandingfoorganization(organization_id,test);
		//brandingSpogSever.verifybrandingfororganization(response,organization_id,organization_name,portal_url,primary_color,secondary_color,branding_msg,SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

		//get organization branding filter with portalurl
		String filter="portal_url="+portal_url;
		String disabled="false";
		brandingSpogSever.setToken(validToken);
		response = brandingSpogSever.getOrganizationBranding(organization_id, filter, test);
		brandingSpogSever.verifybrandingfororganization(response,organization_id,organization_name,portal_url,primary_color,secondary_color,branding_msg,disabled,SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
	}


	@DataProvider(name = "Get_Branding_info_401")
	public final Object[][] GetBrandingValidParams3() {
		return new Object[][] {
			// different role id
			{ "get_brandinginfo_fordirectorg_with_invalid/missed_token_for_directorganization","directorg",ti.direct_org1_user1_token,ti.direct_org1_id, "eswari","direct_org_name","https://www.globant.com"+prefix,"#000455","#000455","test brandinginfo"},
			{ "get_brandinginfo_fordirectorg_with_invalid/missed_token_for_suborganization","NormalMsp",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,"org_name_a","https://www.globant.com"+prefix,"#000455","#000455","test brandinginfo"},
			{ "get_brandinginfo_fordirectorg_with_invalid/missed_token_for_msporganization","RootMsp",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,"spog_udp_qa_","https://www.globant.com"+prefix,"#000455","#000455","test brandinginfo"},

		};
	}

	@Test(dataProvider = "Get_Branding_info_401",priority=3)
	public void getbrandingfoorganizationValid_400_401(
			String testcase,
			String organizationType,
			String validToken,
			String organization_id,
			String organization_name, 
			String portal_url, 
			String primary_color, 
			String secondary_color, 
			String branding_msg){
		test=ExtentManager.getNewTest(testcase);

		ArrayList<HashMap<String,Object>> expectedresponse = new ArrayList<>();
		HashMap<String,Object> expected_response = new HashMap<String,Object>();

		brandingSpogSever.setToken(validToken+"junk");

		test.log(LogStatus.INFO, "Get Branding eamiler  in org of type "+organizationType);
		Response response = brandingSpogSever.getbrandingfoorganization(organization_id,test);
		brandingSpogSever.verifybrandingfororganization(response,organization_id,organization_name,portal_url,primary_color,secondary_color,branding_msg,"false",SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);

		brandingSpogSever.setToken("");
		test.log(LogStatus.INFO, "Get Branding eamiler  in org of type "+organizationType);
		response = brandingSpogSever.getbrandingfoorganization(organization_id,test);
		brandingSpogSever.verifybrandingfororganization(response,organization_id,organization_name,portal_url,primary_color,secondary_color,branding_msg,"false",SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);

		brandingSpogSever.setToken(validToken);
		//generate random user_id
		String random_organization_id=UUID.randomUUID().toString();

		test.log(LogStatus.INFO, "Get Branding eamiler  in org of type "+organizationType);
		response = brandingSpogSever.getbrandingfoorganization(random_organization_id,test);
		brandingSpogSever.verifybrandingfororganization(response,random_organization_id,organization_name,portal_url,primary_color,secondary_color,branding_msg,"false",SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED, test);



	}



	@DataProvider(name = "Get_branding_403")
	public final Object[][] GetBrandingValidParams1() {
		return new Object[][] {

			{ "Get Branding for the direct organziation with the another direct org user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.direct_org2_user2_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Get Branding for the direct organziation with the normal msp  org user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.normal_msp_org1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Get Branding for the direct organziation with the normal msp account admin user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Get Branding for the direct organziation with the sub org user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.normal_msp1_suborg1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Get Branding for the direct organziation with the root msp  org user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.root_msp_org1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Get Branding for the direct organziation with the root msp account admin  user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.root_msp_org1_msp_accountadmin1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Get Branding for the direct organziation with the root msp sub organization user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.normal_msp1_suborg1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Get Branding for the direct organziation with the sub msp organization user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.root_msp1_submsp1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Get Branding for the direct organziation with the sub msp account admin organization user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.root_msp1_submsp1_account_admin_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Get Branding for the direct organziation with the sub msp sub organization user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.root_msp1_suborg1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},

			//Normal MSP Organizations with other roles users token 
			{ "Get Branding for the Noraml MSP organziation with the another direct org user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.direct_org1_user2_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Get Branding for the Noraml MSP organziation with the normal msp2 org user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.normal_msp_org2_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Get Branding for the Noraml MSP organziation with the normal msp account admin user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.normal_msp_org2_msp_accountadmin1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Get Branding for the Noraml MSP organziation with the sub org user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.normal_msp1_suborg1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Get Branding for the Noraml MSP organziation with the root msp  org user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.root_msp_org2_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Get Branding for the Noraml MSP organziation with the root msp account admin  user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.root_msp_org1_msp_accountadmin1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Get Branding for the Noraml MSP organziation with the root msp sub organization user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.normal_msp1_suborg1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Get Branding for the Noraml MSP organziation with the sub msp organization user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.root_msp1_submsp1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Get Branding for the Noraml MSP organziation with the sub msp account admin organization user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.root_msp1_submsp1_account_admin_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Get Branding for the Noraml MSP organziation with the sub msp sub organization user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.root_msp1_suborg1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},


			//Normal MSP Organizations with other roles users token 
			{ "Get Branding for the Root Msp organziation with the direct org user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.direct_org1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Get Branding for the Root Msp organziation with the normal msp  org user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.normal_msp_org1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Get Branding for the Root Msp organziation with the normal msp account admin user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Get Branding for the Root Msp organziation with the sub org user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.normal_msp1_suborg1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Get Branding for the Root Msp organziation with the root msp  org user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.root_msp_org2_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Get Branding for the Root Msp organziation with the root msp account admin  user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.root_msp_org2_msp_accountadmin1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Get Branding for the Root Msp organziation with the root msp sub organization user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.normal_msp1_suborg1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Get Branding for the Root Msp organziation with the sub msp organization user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.root_msp1_submsp1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Get Branding for the Root Msp organziation with the sub msp account admin organization user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.root_msp1_submsp1_account_admin_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Get Branding for the Root Msp organziation with the sub msp sub organization user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.root_msp1_suborg1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},

		};
	}



	@Test(dataProvider = "Get_branding_403",priority=4)
	public void getbrandingfoorganizationValid_403(
			String testcase,
			String organizationType,
			String validToken,
			String otherorgtoken,
			String organization_id,
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


		test.log(LogStatus.INFO, "Get Branding eamiler  in org of type "+organizationType);
		Response response = brandingSpogSever.getbrandingfoorganization(organization_id,test);
		brandingSpogSever.verifybrandingfororganization(response,organization_id,organization_name,portal_url,primary_color,secondary_color,branding_msg,"false",SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY, test);

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


	/****************************************************************Generic Function***************************************************************************/
	public HashMap<String,Object> composeExpectedBrandingInfo(String organization_id,
			String emailer_name, 
			String email, 
			String signature, 
			String support_call, 
			String support_chat, 
			String support_sales, 
			String facebook_link,
			String twitter_link,
			String linkdin_link,
			String social_media_platform,
			String legal_notice,
			String contact_us,
			String privacy,
			String copyrights) {
		HashMap<String,Object> expected_response = new HashMap<>();
		expected_response.put("organization_id", organization_id);
		expected_response.put("emailer_name", emailer_name);
		expected_response.put("signature", signature);
		expected_response.put("support_call", support_call);
		expected_response.put("support_chat",support_chat);
		expected_response.put("support_sales",support_sales);
		expected_response.put("facebook_link",facebook_link);
		expected_response.put("twitter_link",twitter_link);
		expected_response.put("linkdin_link",linkdin_link);
		expected_response.put("social_media_platform",social_media_platform);
		expected_response.put("legal_notice",legal_notice);
		expected_response.put("contact_us",contact_us);
		expected_response.put("privacy",privacy);
		expected_response.put("copyrights",copyrights);
		return expected_response;
	}
}

