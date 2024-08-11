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

public class UpdateBrandingTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private UserSpogServer userSpogServer;
	private BrandingSpogSever brandingSpogSever;
	private TestOrgInfo ti;
	private static JsonPreparation jp = new JsonPreparation();

	private ExtentTest test;

	private String site_version = "1.0.0";
	private String gateway_hostname = "Malleswari";
	// used for test case count like passed,failed,remaining cases
	// private testcasescount count1;
	int passedcases = 0;
	int failedcases = 0;
	int skippedcases = 0;
	private String org_model_prefix = this.getClass().getSimpleName();

	String prefix=RandomStringUtils.randomAlphanumeric(4);

	/*
	 * private String runningMachine; private String buildVersion;
	 */
	@BeforeClass
	@Parameters({ "baseURI", "port","csrAdminUserName", "csrAdminPassword","csrReadOnlyAdminName","csrReadOnlyAdminPassword","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword,String csrReadOnlyAdminName,String csrReadOnlyAdminPassword, String logFolder,String runningMachine,String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		brandingSpogSever = new BrandingSpogSever(baseURI, port);
		rep = ExtentManager.getInstance("UpdateBrandingTest", logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Malleswari";

		Nooftest = 0;
		// Used for creating a build number with dateformat
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date = new java.util.Date();
		this.buildVersion = buildVersion + "_" + dateFormater.format(date);
		ti = new TestOrgInfo(spogServer, test);	
		BQName = this.getClass().getSimpleName();
		this.runningMachine = runningMachine;
		System.out.println("The value of hit is " + count1.isstarttimehit());

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


	@DataProvider(name = "update_Branding_valid")
	public final Object[][] updateBrandingValidParams() {
		return new Object[][] {
			// different role id

			{ "Update_Branding  for the direct organziationwith the direct user valid token",ti.direct_org1_user1_token,ti.direct_org1_id, "eswari","https://www.globant.com123", "test",
				"#000455", "#000475", "true", "test", "test brandinginfo", "eswari","https://www.globant1"+prefix +".com123", "test",
				"#000458", "#000479", "true", "test", "test brandinginfo1"},
			{ "Update_Branding for the Noraml Msp organziation with the msp org user valid token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id, "eswari","https://www.globant.com123", "test",
					"#000457", "#000459", "true", "test", "test brandinginfo","eswari","https://www.globaqwnt1"+prefix+".com123", "test",
					"#000458", "#000479", "true", "test", "test brandinginfo1" },
			{ "Update_Branding the root msp organziation with the msp account admin user valid token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id, "eswari","https://www.globant.com123", "test",
						"#000459", "#000457", "true", "test", "test brandinginfo","eswari","https://www.glowqwbant1"+prefix+".com123", "test",
						"#000458", "#000479", "true", "test", "test brandinginfo1" },
			{ "Update_Branding the Sub msp organziation with the msp account admin user valid token", ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp_org1_id,"eswari","https://www.globant.com123", "test",
							"#000451", "#000556", "true", "test", "test brandinginfo", "eswari","https://www.glqwqwqwobant1"+prefix+".com123", "test",
							"#000458", "#000479", "true", "test", "test brandinginfo1" },

		};
	}

	@Test(dataProvider = "update_Branding_valid")
	public void updateBrandingFororganizationValid_200(String testcase,
			String validToken,
			String organization_id,
			String organization_name, 
			String portal_url, 
			String logo_img_url,
			String primary_color, 
			String secondary_color,
			String is_branding_logo, 
			String login_img_url,
			String branding_msg,
			String updateorganization_name,
			String updateportal_url,
			String updatelogo_img_url,
			String updateprimary_color,
			String updatesecondary_color,
			String updateis_branding_logo,
			String updatelogin_img_url,
			String updatebranding_msg) {
		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + testcase);

		brandingSpogSever.setToken(validToken);

		/*brandingSpogSever.createBrandingFororganization(organization_id, updateorganization_name, updateportal_url, updateprimary_color, updatesecondary_color, updatebranding_msg, test);
		test.log(LogStatus.INFO, "Update Brnading to the organization");
		 */	Response response = brandingSpogSever.updateBrandingFororganization(organization_id, updateorganization_name,updateportal_url, updateprimary_color, updatesecondary_color, updatebranding_msg, test);
		 brandingSpogSever.verifybrandingfororganization(response, organization_id, updateorganization_name,updateportal_url, updateprimary_color, updatesecondary_color, updatebranding_msg, null,SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
	}

	@Test(dataProvider = "update_Branding_valid")
	public void updateBrandingFororganizationValid_400(String testCase, 
			String validToken,
			String organization_id, 
			String organization_name,
			String portal_url, 
			String logo_img_url,
			String primary_color,
			String secondary_color, 
			String is_branding_logo,
			String login_img_url,
			String branding_msg, 
			String updateorganization_name,
			String updateportal_url,
			String updatelogo_img_url,
			String updateprimary_color, 
			String updatesecondary_color, 
			String updateis_branding_logo,
			String updatelogin_img_url, 
			String updatebranding_msg) {
		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + testCase);

		brandingSpogSever.setToken(validToken + "junk");

		test.log(LogStatus.INFO, "Updtae Bradning to the Organization");
		Response response = brandingSpogSever.updateBrandingFororganization(organization_id, organization_name,portal_url, primary_color, secondary_color, branding_msg, test);
		brandingSpogSever.verifybrandingfororganization(response, organization_id, organization_name, portal_url,primary_color, secondary_color, branding_msg, null, SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);

		brandingSpogSever.setToken("");
		test.log(LogStatus.INFO, "Updtae Bradning to the Organization");
		response = brandingSpogSever.updateBrandingFororganization(organization_id, organization_name, portal_url,primary_color, secondary_color, branding_msg, test);
		brandingSpogSever.verifybrandingfororganization(response, organization_id, organization_name, portal_url,primary_color, secondary_color, branding_msg, null, SpogConstants.NOT_LOGGED_IN,SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);

		brandingSpogSever.setToken(validToken);
		// gievn organization_is not UUID
		test.log(LogStatus.INFO, "Updtae Bradning to the Organization");
		response = brandingSpogSever.updateBrandingFororganization("123", organization_name, portal_url, primary_color,secondary_color, branding_msg, test);
		brandingSpogSever.verifybrandingfororganization(response, "123", organization_name, portal_url, primary_color,secondary_color, branding_msg, null, SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ORGANIZATION_ID_CANNOT_BLANK, test);

		// Oganization_name is blank
		test.log(LogStatus.INFO, "Updtae Bradning to the Organization " );
		Response response1 = brandingSpogSever.updateBrandingFororganization(organization_id, "", portal_url,primary_color, secondary_color, branding_msg, test);
		brandingSpogSever.verifybrandingfororganization(response1, organization_id, "", portal_url, primary_color,secondary_color, branding_msg, null, SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ORGANIZATION_NAME_CANNOT_BLANK, test);

		brandingSpogSever.setToken(validToken);
		// generate random user_id
		String random_organization_id = UUID.randomUUID().toString();

		test.log(LogStatus.INFO, "Updtae Bradning to the Organization");
		Response response3 = brandingSpogSever.updateBrandingFororganization(random_organization_id, organization_name,portal_url, primary_color, secondary_color, branding_msg, test);
		brandingSpogSever.verifybrandingfororganization(response3, random_organization_id, organization_name,portal_url, primary_color, secondary_color, branding_msg, null, SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED, test);

	}

	@DataProvider(name = "Update_branding_valid_403")
	public final Object[][] updateBrandingValidParams1() {
		return new Object[][] {
			{ "Update branding for the direct organziation with the another direct org user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.direct_org2_user2_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Update branding for the direct organziation with the normal msp  org user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.normal_msp_org1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Update branding for the direct organziation with the normal msp account admin user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Update branding for the direct organziation with the sub org user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.normal_msp1_suborg1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Update branding for the direct organziation with the root msp  org user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.root_msp_org1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Update branding for the direct organziation with the root msp account admin  user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.root_msp_org1_msp_accountadmin1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Update branding for the direct organziation with the root msp sub organization user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.normal_msp1_suborg1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Update branding for the direct organziation with the sub msp organization user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.root_msp1_submsp1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Update branding for the direct organziation with the sub msp account admin organization user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.root_msp1_submsp1_account_admin_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Update branding for the direct organziation with the sub msp sub organization user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.root_msp1_suborg1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},

			//Normal MSP Organizations with other roles users token 
			{ "Update branding for the Noraml MSP organziation with the another direct org user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.direct_org1_user2_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Update branding for the Noraml MSP organziation with the normal msp2 org user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.normal_msp_org2_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Update branding for the Noraml MSP organziation with the normal msp account admin user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.normal_msp_org2_msp_accountadmin1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Update branding for the Noraml MSP organziation with the sub org user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.normal_msp1_suborg1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Update branding for the Noraml MSP organziation with the root msp  org user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.root_msp_org2_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Update branding for the Noraml MSP organziation with the root msp account admin  user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.root_msp_org1_msp_accountadmin1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Update branding for the Noraml MSP organziation with the root msp sub organization user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.normal_msp1_suborg1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Update branding for the Noraml MSP organziation with the sub msp organization user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.root_msp1_submsp1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Update branding for the Noraml MSP organziation with the sub msp account admin organization user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.root_msp1_submsp1_account_admin_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Update branding for the Noraml MSP organziation with the sub msp sub organization user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.root_msp1_suborg1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},


			//Normal MSP Organizations with other roles users token 
			{ "Update branding for the Root Msp organziation with the direct org user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.direct_org1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Update branding for the Root Msp organziation with the normal msp  org user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.normal_msp_org1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Update branding for the Root Msp organziation with the normal msp account admin user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Update branding for the Root Msp organziation with the sub org user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.normal_msp1_suborg1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Update branding for the Root Msp organziation with the root msp  org user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.root_msp_org2_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Update branding for the Root Msp organziation with the root msp account admin  user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.root_msp_org2_msp_accountadmin1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Update branding for the Root Msp organziation with the root msp sub organization user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.normal_msp1_suborg1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Update branding for the Root Msp organziation with the sub msp organization user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.root_msp1_submsp1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Update branding for the Root Msp organziation with the sub msp account admin organization user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.root_msp1_submsp1_account_admin_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},
			{ "Update branding for the Root Msp organziation with the sub msp sub organization user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.root_msp1_suborg1_user1_token,"eswari","https://www.globant.com","#000455","#000455","test brandinginfo"},

		};
	}

	@Test(dataProvider = "Update_branding_valid_403")
	public void updateBrandingFororganizationValid_403(String testCase,
			String validToken, 
			String organization_id,
			String otherorgtoken,
			String organization_name, 
			String portal_url, 
			String primary_color,
			String secondary_color, 
			String branding_msg) {
		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + testCase);

		brandingSpogSever.setToken(otherorgtoken);

		test.log(LogStatus.INFO, "Update Branding  in org of type " + testCase);
		Response response1 = brandingSpogSever.updateBrandingFororganization(organization_id, organization_name,portal_url, primary_color, secondary_color, branding_msg, test);
		brandingSpogSever.verifybrandingfororganization(response1, organization_id, organization_name, portal_url,primary_color, secondary_color, branding_msg, null, SpogConstants.INSUFFICIENT_PERMISSIONS,SpogMessageCode.RESOURCE_PERMISSION_DENY, test);

	}

	@DataProvider(name = "Update_branding_valid_404")
	public final Object[][] updateBrandingValidParams2() {
		return new Object[][] {
			// different role id
			{ "Update_Branding to the randomorgid with the direct user valid token",ti.direct_org1_user1_token,UUID.randomUUID().toString(), "eswari","https://tspog.zetta.com"+prefix,"#000455","#000455","test brandinginfo"},
			{ "Update_Branding for the randomorgid with the msp org user valid token",ti.normal_msp_org1_user1_token,UUID.randomUUID().toString(), "eswari","https://tspog.arcserve.net"+prefix,"#000455","#000455","test brandinginfo"},
			{ "Update_Branding the randomorgid with the msp account admin user valid token",ti.root_msp_org1_user1_token,UUID.randomUUID().toString(), "eswari","https://tspog.yahoo.net"+prefix,"#000455","#000455","test brandinginfo"},

			{ "Update_Branding  for the randomorgid with the direct user valid token",ti.csr_token,UUID.randomUUID().toString(),"eswari","https://tspog.zetta.com"+prefix,"#000456","#000455","test brandinginfo"},
			{ "Update_Branding for the randomorgid with the msp org user valid token",ti.csr_token, UUID.randomUUID().toString(),"eswari","https://tspog.arcserve.net"+prefix,"#000457","#000455","test brandinginfo"},
			{ "Update_Branding to  the randomorgidwith the msp account admin user valid token", ti.csr_token,UUID.randomUUID().toString(),"eswari","https://tspog.yahoo.net"+prefix,"#000455","#000495","test brandinginfo"},

		};
	}

	@Test(dataProvider = "Update_branding_valid_404")
	public void updateBrandingFororganizationValid_404(String testCase,
			String validToken,
			String organization_id,
			String organization_name,
			String portal_url,
			String primary_color,
			String secondary_color, 
			String branding_msg) {
		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + testCase);

		brandingSpogSever.setToken(validToken);

		test.log(LogStatus.INFO, "Update branding eamiler  in org of type "  );
		Response response2 = brandingSpogSever.updateBrandingFororganization(organization_id, organization_name,portal_url, primary_color, secondary_color, branding_msg, test);
		brandingSpogSever.verifybrandingfororganization(response2, organization_id, organization_name, portal_url,primary_color, secondary_color, branding_msg, null, SpogConstants.RESOURCE_NOT_EXIST,SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED, test);

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
