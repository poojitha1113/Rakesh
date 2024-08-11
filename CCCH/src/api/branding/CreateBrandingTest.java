package api.branding;

import java.io.IOException;
import java.text.SimpleDateFormat;
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

public class CreateBrandingTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private UserSpogServer userSpogServer;
	private BrandingSpogSever brandingSpogSever;
	private TestOrgInfo ti;
	private static JsonPreparation jp = new JsonPreparation();
	String prefix = RandomStringUtils.randomAlphanumeric(8);

	private ExtentTest test;
	private String common_password = "Mclaren@2013";

	private String site_version = "1.0.0";
	private String gateway_hostname = "Malleswari";

	int passedcases = 0;
	int failedcases = 0;
	int skippedcases = 0;
	private String org_model_prefix = this.getClass().getSimpleName();


	@BeforeClass
	@Parameters({ "baseURI", "port","csrAdminUserName", "csrAdminPassword","csrReadOnlyAdminName","csrReadOnlyAdminPassword","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword,String csrReadOnlyAdminName,String csrReadOnlyAdminPassword, String logFolder,String runningMachine,String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		brandingSpogSever = new BrandingSpogSever(baseURI, port);
		rep = ExtentManager.getInstance(this.getClass().getSimpleName(),logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Malleswari.S";

		Nooftest = 0;
		// Used for creating a build number with dateformat
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date = new java.util.Date();
		this.buildVersion = buildVersion + "_" + dateFormater.format(date);

		BQName = this.getClass().getSimpleName();
		this.runningMachine = runningMachine;
		System.out.println("The value of hit is " + count1.isstarttimehit());
		ti = new TestOrgInfo(spogServer, test);	
		if (count1.isstarttimehit() == 0) {
			System.out.println("into creation time");
			creationTime = System.currentTimeMillis();
			count1.setcreationtime(creationTime);

			// creationTime = System.currentTimeMillis();
			try {
				bqdb1.updateTable(BQName, this.runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0",
						String.valueOf(Nooftest), count1.getcreationtime(), "InProgress",
						author + " and Rest server is " + baseURI.split("//")[1]);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}




	@DataProvider(name = "create_Branding_valid")
	public final Object[][] createBrandingValidParams() {
		return new Object[][] {
			// different role id
			{ "Create_Branding  for the direct organziationwith the direct user valid token",ti.direct_org1_user1_token,ti.direct_org1_id, "eswari","https://tspog.zetta.com"+prefix,"#000455","#000455","test brandinginfo"},
			{ "Create_Branding for the Noraml Msp organziation with the msp org user valid token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id, "eswari","https://tspog.arcserve.net"+prefix,"#000455","#000455","test brandinginfo"},
			{ "Create_Brandingfor the root msp organziation with the msp account admin user valid token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id, "eswari","https://tspog.yahoo.net"+prefix,"#000455","#000455","test brandinginfo"},
			{ "Create_Brandingfor the Sub msp organziation with the msp account admin user valid token", ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp_org1_id,"eswari","https://tspog.yaho11o.net"+prefix,"#000455","#000455","test brandinginfo"},


		};
	}

	@Test(dataProvider = "create_Branding_valid")
	public void createBranding(String testcase, 
			String validToken, 
			String organization_id,
			String organization_name,
			String portal_url,
			String primary_color,
			String secondary_color,
			String branding_msg) {
		test = ExtentManager.getNewTest(testcase);

		brandingSpogSever.setToken(validToken);

		test.log(LogStatus.INFO, "Create branding eamiler  in org of type");
		Response response = brandingSpogSever.createBrandingFororganization(organization_id, organization_name,portal_url, primary_color, secondary_color, branding_msg, test);
		brandingSpogSever.verifybrandingfororganization(response, organization_id, organization_name, portal_url,primary_color, secondary_color, branding_msg, null, SpogConstants.SUCCESS_POST, null, test);

		test.log(LogStatus.INFO, "Create branding eamiler  in org of type ");
		response = brandingSpogSever.createBrandingFororganization(organization_id, organization_name, portal_url,primary_color, secondary_color, branding_msg, test);
		brandingSpogSever.verifybrandingfororganization(response, organization_id, organization_name, portal_url,primary_color, secondary_color, branding_msg, null, SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.BRANDING_ALREADY_EXIST, test);

	}
	@DataProvider(name = "Create_Branding_Deleted_Org")
	public final Object[][] createBrandingDletedOrg() {
		return new Object[][] {
			// different role id
			{ "Create_Branding  for the direct organziationwith the direct user valid token",ti.direct_org1_user1_token,ti.direct_org1_id, "eswari","https://tspog.zetta.com"+prefix,"#000455","#000455","test brandinginfo"},
			{ "Create_Branding for the Noraml Msp organziation with the msp org user valid token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id, "eswari","https://tspog.arcserve.net"+prefix,"#000455","#000455","test brandinginfo"},
			{ "Create_Brandingfor the root msp organziation with the msp account admin user valid token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id, "eswari","https://tspog.yahoo.net"+prefix,"#000455","#000455","test brandinginfo"},

		};
	}


	@Test(dataProvider = "Create_Branding_Deleted_Org")
	public void createBranding_401(String testcase, 
			String validToken,
			String organization_id,
			String organization_name, 
			String portal_url,
			String primary_color, 
			String secondary_color,
			String branding_msg) {
		test = ExtentManager.getNewTest(testcase);

		brandingSpogSever.setToken(validToken + "junk");

		test.log(LogStatus.INFO, "Create branding to Organization " + testcase);
		Response response = brandingSpogSever.createBrandingFororganization(organization_id, organization_name,portal_url, primary_color, secondary_color, branding_msg, test);
		brandingSpogSever.verifybrandingfororganization(response, organization_id, organization_name, portal_url,primary_color, secondary_color, branding_msg, null, SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);

		brandingSpogSever.setToken("");
		test.log(LogStatus.INFO, "Create branding to Organization  " + testcase);
		response = brandingSpogSever.createBrandingFororganization(organization_id, organization_name, portal_url,primary_color, secondary_color, branding_msg, test);
		brandingSpogSever.verifybrandingfororganization(response, organization_id, organization_name, portal_url,primary_color, secondary_color, branding_msg, null, SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);

		brandingSpogSever.setToken(validToken);
		// given organization_is not UUID
		test.log(LogStatus.INFO, "Create branding to Organization  " + testcase);
		response = brandingSpogSever.createBrandingFororganization("123", organization_name, portal_url, primary_color,secondary_color, branding_msg, test);

		// Oganization_name is blank
		test.log(LogStatus.INFO, "Create branding to Organization " + testcase);
		Response response1 = brandingSpogSever.createBrandingFororganization(organization_id, "", portal_url,primary_color, secondary_color, branding_msg, test);
		brandingSpogSever.verifybrandingfororganization(response1, organization_id, "", portal_url, primary_color,secondary_color, branding_msg, null, SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ORGANIZATION_NAME_CANNOT_BLANK, test);

		brandingSpogSever.setToken(validToken);

		// Random Organization_Id
		String random_organization_id = UUID.randomUUID().toString();

		test.log(LogStatus.INFO, "Create branding to Organization  " + testcase);
		Response response3 = brandingSpogSever.createBrandingFororganization(random_organization_id, organization_name,portal_url, primary_color, secondary_color, branding_msg, test);
		brandingSpogSever.verifybrandingfororganization(response3, random_organization_id, organization_name,portal_url, primary_color, secondary_color, branding_msg, null, SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED, test);

	}

	@DataProvider(name = "create_branding_403")
	public final Object[][] createBrandingValidParams1() {
		return new Object[][] {
			{ "Create branding for the direct organziation with the another direct org user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.direct_org2_user2_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Create branding for the direct organziation with the normal msp  org user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.normal_msp_org1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Create branding for the direct organziation with the normal msp account admin user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Create branding for the direct organziation with the sub org user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.normal_msp1_suborg1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Create branding for the direct organziation with the root msp  org user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.root_msp_org1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Create branding for the direct organziation with the root msp account admin  user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.root_msp_org1_msp_accountadmin1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Create branding for the direct organziation with the root msp sub organization user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.normal_msp1_suborg1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Create branding for the direct organziation with the sub msp organization user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.root_msp1_submsp1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Create branding for the direct organziation with the sub msp account admin organization user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.root_msp1_submsp1_account_admin_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Create branding for the direct organziation with the sub msp sub organization user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.root_msp1_suborg1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},

			//Normal MSP Organizations with other roles users token 
			{ "Create branding for the Noraml MSP organziation with the another direct org user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.direct_org1_user2_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Create branding for the Noraml MSP organziation with the normal msp2 org user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.normal_msp_org2_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Create branding for the Noraml MSP organziation with the normal msp account admin user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.normal_msp_org2_msp_accountadmin1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Create branding for the Noraml MSP organziation with the sub org user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.normal_msp1_suborg1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Create branding for the Noraml MSP organziation with the root msp  org user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.root_msp_org2_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Create branding for the Noraml MSP organziation with the root msp account admin  user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.root_msp_org1_msp_accountadmin1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Create branding for the Noraml MSP organziation with the root msp sub organization user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.normal_msp1_suborg1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Create branding for the Noraml MSP organziation with the sub msp organization user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.root_msp1_submsp1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Create branding for the Noraml MSP organziation with the sub msp account admin organization user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.root_msp1_submsp1_account_admin_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Create branding for the Noraml MSP organziation with the sub msp sub organization user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.root_msp1_suborg1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},


			//Normal MSP Organizations with other roles users token 
			{ "Create branding for the Root Msp organziation with the direct org user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.direct_org1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Create branding for the Root Msp organziation with the normal msp  org user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.normal_msp_org1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Create branding for the Root Msp organziation with the normal msp account admin user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Create branding for the Root Msp organziation with the sub org user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.normal_msp1_suborg1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Create branding for the Root Msp organziation with the root msp  org user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.root_msp_org2_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Create branding for the Root Msp organziation with the root msp account admin  user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.root_msp_org2_msp_accountadmin1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Create branding for the Root Msp organziation with the root msp sub organization user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.normal_msp1_suborg1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Create branding for the Root Msp organziation with the sub msp organization user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.root_msp1_submsp1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Create branding for the Root Msp organziation with the sub msp account admin organization user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.root_msp1_submsp1_account_admin_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Create branding for the Root Msp organziation with the sub msp sub organization user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.root_msp1_suborg1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},


		};
	}

	@Test(dataProvider = "create_branding_403")
	public void createBrandingFororganizationValid_403(String testcase,
			String validToken, 
			String organization_id,
			String otherorgtoken,
			String organization_name, 
			String portal_url,
			String primary_color,
			String secondary_color,
			String branding_msg) {
		test = ExtentManager.getNewTest(testcase);
		brandingSpogSever.setToken(otherorgtoken);

		test.log(LogStatus.INFO, "Create branding eamiler  in org of type " + testcase);
		Response response1 = brandingSpogSever.createBrandingFororganization(organization_id, organization_name,portal_url, primary_color, secondary_color, branding_msg, test);
		brandingSpogSever.verifybrandingfororganization(response1, organization_id, organization_name, portal_url,primary_color, secondary_color, branding_msg, null, SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY, test);

	}

	@AfterMethod
	public void getResult(ITestResult result) {
		if (result.getStatus() == ITestResult.FAILURE) {
			count1.setfailedcount();
			test.log(LogStatus.FAIL, "Test Case Failed is " + result.getName() + " with parameters as "
					+ Arrays.asList(result.getParameters()));
			test.log(LogStatus.FAIL, result.getThrowable().getMessage());

		} else if (result.getStatus() == ITestResult.SKIP) {
			test.log(LogStatus.SKIP, "Test Case Skipped is " + result.getName());
			count1.setskippedcount();
		} else if (result.getStatus() == ITestResult.SUCCESS) {
			count1.setpassedcount();

		}
		rep.endTest(test);
		rep.flush();
	}

	/****************************************************************
	 * Generic Function
	 ***************************************************************************/
	public HashMap<String, Object> composeExpectedBrandingInfo(String organization_id, String emailer_name,
			String email, String signature, String support_call, String support_chat, String support_sales,
			String facebook_link, String twitter_link, String linkdin_link, String social_media_platform,
			String legal_notice, String contact_us, String privacy, String copyrights) {
		HashMap<String, Object> expected_response = new HashMap<>();
		expected_response.put("organization_id", organization_id);
		expected_response.put("emailer_name", emailer_name);
		expected_response.put("signature", signature);
		expected_response.put("support_call", support_call);
		expected_response.put("support_chat", support_chat);
		expected_response.put("support_sales", support_sales);
		expected_response.put("facebook_link", facebook_link);
		expected_response.put("twitter_link", twitter_link);
		expected_response.put("linkdin_link", linkdin_link);
		expected_response.put("social_media_platform", social_media_platform);
		expected_response.put("legal_notice", legal_notice);
		expected_response.put("contact_us", contact_us);
		expected_response.put("privacy", privacy);
		expected_response.put("copyrights", copyrights);
		return expected_response;
	}
}
