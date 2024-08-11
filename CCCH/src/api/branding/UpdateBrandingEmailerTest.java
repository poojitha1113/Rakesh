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
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class UpdateBrandingEmailerTest extends base.prepare.Is4Org {

	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private UserSpogServer userSpogServer;
	private BrandingSpogSever brandingSpogSever;
	private TestOrgInfo ti;
	private ExtentTest test;

	int passedcases = 0;
	int failedcases = 0;
	int skippedcases = 0;
	private String org_model_prefix = this.getClass().getSimpleName();


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
		rep = ExtentManager.getInstance("CreateBrandingEmailerTest", logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "malleswari";

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

	@DataProvider(name = "Update_Branding_Emailer")
	public final Object[][] updateBradningEmailer() {
		return new Object[][] {
			{ "UpDate_Branding_Emailer  for the direct organziationwith the direct user valid token",ti.csr_token, ti.direct_org2_id,"eswari","nagamalleswari.sykam@arcserve.com", "nagamaaleswari.sykam", null,
				"http://americanstandard.com/saleschat", "http://americanstandard.com/sales","http://facebook.com", "http://twitter.com", null, "http://facebook.com",
				"http://americanstandard.com/saleschat", "http://facebook.com/americanstandard","http://facebook.com/americanstandard", "string", null, null, null, null, null, null, null,null, null, null, null, null, null, null, null, null,"cloudsupport@arcserve.com" },
			{ "UpDate_Branding_Emailer for the Noraml Msp organziation with the msp org user valid token",ti.csr_token, ti.normal_msp_org2_id,"eswari","nagamalleswari.sykam@arcserve.com", "nagamaaleswari.sykam", null,
					"http://americanstandard.com/saleschat", "http://americanstandard.com/sales","http://facebook.com", "http://twitter.com", null, "http://facebook.com",
					"http://americanstandard.com/saleschat", "http://facebook.com/americanstandard","http://facebook.com/americanstandard", "string", null, null, null, null, null, null, null,null, null, null, null, null, null, null, null, null,"cloudsupport@arcserve.com" },
			{ "UpDate_Branding_Emailerfor the root msp organziation with the msp account admin user valid token",ti.csr_token,ti.root_msp_org2_id, "eswari","nagamalleswari.sykam@arcserve.com", "nagamaaleswari.sykam", null,
						"http://americanstandard.com/saleschat", "http://americanstandard.com/sales","http://facebook.com", "http://twitter.com", null, "http://facebook.com",
						"http://americanstandard.com/saleschat", "http://facebook.com/americanstandard","http://facebook.com/americanstandard", "string", null, null, null, null, null, null, null,null, null, null, null, null, null, null, null, null,"cloudsupport@arcserve.com" },
			{ "UpDate_Branding_Emailerfor the Sub msp organziation with the msp account admin user valid token", ti.csr_token,ti.root_msp2_submsp_org1_id,"eswari","nagamalleswari.sykam@arcserve.com", "nagamaaleswari.sykam", null,
							"http://americanstandard.com/saleschat", "http://americanstandard.com/sales","http://facebook.com", "http://twitter.com", null, "http://facebook.com",
							"http://americanstandard.com/saleschat", "http://facebook.com/americanstandard","http://facebook.com/americanstandard", "string", null, null, null, null, null, null, null,null, null, null, null, null, null, null, null, null,"cloudsupport@arcserve.com" },

		};
	}

	@Test(dataProvider = "Update_Branding_Emailer")
	public void updateBrandingEmailer_401(String organizationType,
			String validToken,
			String organization_id,
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
			String copyrights,
			String branding_from,
			String updateemailer_name, 
			String updateemail,
			String updatesignature,
			String updatesupport_call,
			String updatesupport_chat,
			String updatesupport_sales,
			String updatefacebook_link,
			String updatetwitter_link, 
			String updatelinkdin_link,
			String updatesocial_media_platform, 
			String updatelegal_notice,
			String updatecontact_us,
			String updateprivacy,
			String updatecopyrights,
			String updatebranding_from,
			String updateSupportEmail

			) {
		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		// missed the token
		brandingSpogSever.setToken("");

		test.log(LogStatus.INFO, "Create branding eamiler  in org of type " + organizationType);
		Response response = brandingSpogSever.updateBrandingemailerFororganization(organization_id, emailer_name, email,signature, support_call, support_chat, support_sales, updateSupportEmail,facebook_link, twitter_link, linkdin_link,
				legal_notice, contact_us, privacy, copyrights, branding_from, test);
		brandingSpogSever.verifyBrandingEmailerForOrganization(response, organization_id, emailer_name, email,signature, support_call, support_chat, support_sales,updateSupportEmail, facebook_link, twitter_link, linkdin_link,
				legal_notice, contact_us, privacy, copyrights, branding_from, null,SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);

		// invalid token
		brandingSpogSever.setToken(validToken + "junk");

		test.log(LogStatus.INFO, "Create branding eamiler  in org of type " + organizationType);
		response = brandingSpogSever.updateBrandingemailerFororganization(organization_id, emailer_name, email,signature, support_call, support_chat, support_sales,updateSupportEmail, facebook_link, twitter_link, linkdin_link,
				legal_notice, contact_us, privacy, copyrights, branding_from, test);
		brandingSpogSever.verifyBrandingEmailerForOrganization(response, organization_id, emailer_name, email,signature, support_call, support_chat, support_sales,updateSupportEmail, facebook_link, twitter_link, linkdin_link,
				legal_notice, contact_us, privacy, copyrights, branding_from, null,SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);

	}

	@DataProvider(name = "update_BrandingEmailer_Valid")
	public final Object[][] updateUserFilterValidParams1() {
		return new Object[][] {
			// different role id
			{ "UpDate_Branding_Emailer  for the direct organziationwith the direct user valid token",ti.direct_org1_user1_token,ti.direct_org1_id,"eswari","nagamalleswari.sykam@arcserve.com", "nagamaaleswari.sykam", null,
				"http://americanstandard.com/saleschat", "http://americanstandard.com/sales","http://facebook.com", "http://twitter.com", null, "http://facebook.com",
				"http://americanstandard.com/saleschat", "http://facebook.com/americanstandard","http://facebook.com/americanstandard", "string", "SELF","cloudsupport@arcserve.com" },

			{ "UpDate_Branding_Emailer for the Noraml Msp organziation with the msp org user valid token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id, "eswari","nagamalleswari.sykam@arcserve.com", "nagamaaleswari.sykam", null,
					"http://americanstandard.com/saleschat", "http://americanstandard.com/sales","http://facebook.com", "http://twitter.com", null, "http://facebook.com",
					"http://americanstandard.com/saleschat", "http://facebook.com/americanstandard","http://facebook.com/americanstandard", "string", "SELF","cloudsupport@arcserve.com" },

			{ "UpDate_Branding_Emailerfor the root msp organziation with the msp account admin user valid token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,"eswari","nagamalleswari.sykam@arcserve.com", "nagamaaleswari.sykam", null,
						"http://americanstandard.com/saleschat", "http://americanstandard.com/sales","http://facebook.com", "http://twitter.com", null, "http://facebook.com",
						"http://americanstandard.com/saleschat", "http://facebook.com/americanstandard","http://facebook.com/americanstandard", "string", "SELF","cloudsupport@arcserve.com" },

			{ "UpDate_Branding_Emailerfor the Sub msp organziation with the msp account admin user valid token", ti.root_msp1_submsp1_user1_token,ti.root_msp1_submsp_org1_id,"eswari","nagamalleswari.sykam@arcserve.com", "nagamaaleswari.sykam", null,
							"http://americanstandard.com/saleschat", "http://americanstandard.com/sales","http://facebook.com", "http://twitter.com", null, "http://facebook.com",
							"http://americanstandard.com/saleschat", "http://facebook.com/americanstandard","http://facebook.com/americanstandard", "string", "SELF","cloudsupport@arcserve.com" },


		};
	}

	@Test(dataProvider = "update_BrandingEmailer_Valid")
	public void updateBrandingEmailer_200(String organizationType, 
			String validToken,
			String organization_id,
			String emailer_name,
			String email, 
			String signature, 
			String support_call,
			String support_chat, 
			String support_sales, 
			String facebook_link,
			String twitter_link, String linkdin_link,
			String social_media_platform, 
			String legal_notice,
			String contact_us, 
			String privacy,
			String copyrights,
			String branding_from,
			String support_email) {
		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);

		brandingSpogSever.setToken(validToken);

		
		// given the invalid format for the suport chat
		test.log(LogStatus.INFO, "Create branding eamiler  in org of type " + organizationType);
		Response response = brandingSpogSever.updateBrandingemailerFororganization(organization_id, emailer_name, email,signature, support_call, "www.americanstandard.com/saleschat", support_sales,support_email, facebook_link,
				twitter_link, linkdin_link,  legal_notice, contact_us, privacy, copyrights,branding_from, test);
		brandingSpogSever.verifyBrandingEmailerForOrganization(response, organization_id, emailer_name, email,
				signature, support_call, "www.americanstandard.com/saleschat", support_sales,support_email, facebook_link,twitter_link, linkdin_link,  legal_notice, contact_us, privacy, copyrights,
				branding_from, null, SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.SUPPORT_CHAT_URL_FORMAT_NOT_VALID, test);

		brandingSpogSever.setToken(validToken);
		// given the invalid format for the support_sales
		test.log(LogStatus.INFO, "Create branding eamiler  in org of type " + organizationType);
		response = brandingSpogSever.updateBrandingemailerFororganization(organization_id, emailer_name, email,signature, support_call, support_chat, "www.americanstandard.com/saleschat", support_email,facebook_link,
				twitter_link, linkdin_link,  legal_notice, contact_us, privacy, copyrights,branding_from, test);
		brandingSpogSever.verifyBrandingEmailerForOrganization(response, organization_id, emailer_name, email,
				signature, support_call, support_chat, "www.americanstandard.com/saleschat",support_email, facebook_link,twitter_link, linkdin_link,  legal_notice, contact_us, privacy, copyrights,
				branding_from, null, SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.SUPPORT_SALES_URL_FORMAT_NOT_VALID, test);

		// given the invalid format for the facebook_link
		test.log(LogStatus.INFO, "Create branding eamiler  in org of type " + organizationType);
		response = brandingSpogSever.updateBrandingemailerFororganization(organization_id, emailer_name, email,signature, support_call, support_chat, support_sales,support_email, "www.facebook_link.com", twitter_link,
				linkdin_link,  legal_notice, contact_us, privacy, copyrights, branding_from,test);
		brandingSpogSever.verifyBrandingEmailerForOrganization(response, organization_id, emailer_name, email,
				signature, support_call, support_chat, support_sales,support_email, "www.facebook_link.com", twitter_link,linkdin_link,  legal_notice, contact_us, privacy, copyrights, branding_from, null,SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.FACEBOOK_LINK_URL_FORMAT_NOT_VALID, test);

		// given the invalid format for the twitter_link
		test.log(LogStatus.INFO, "Create branding eamiler  in org of type " + organizationType);
		response = brandingSpogSever.updateBrandingemailerFororganization(organization_id, emailer_name, email,signature, support_call, support_chat, support_sales,support_email, facebook_link, "www.twitter_link.com",
				linkdin_link,  legal_notice, contact_us, privacy, copyrights, branding_from,test);
		brandingSpogSever.verifyBrandingEmailerForOrganization(response, organization_id, emailer_name, email,
				signature, support_call, support_chat, support_sales,support_email, facebook_link, "www.twitter_link.com",linkdin_link,  legal_notice, contact_us, privacy, copyrights, branding_from, null,SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.TWITTER_LINK_URL_FORMAT_NOT_VALID, test);

		// given the invalid format for the linkdin_link
		test.log(LogStatus.INFO, "Create branding eamiler  in org of type " + organizationType);
		response = brandingSpogSever.updateBrandingemailerFororganization(organization_id, emailer_name, email,signature, support_call, support_chat, support_sales,support_email, facebook_link, twitter_link,
				"www.linkdin_link.com",  legal_notice, contact_us, privacy, copyrights,branding_from, test);
		brandingSpogSever.verifyBrandingEmailerForOrganization(response, organization_id, emailer_name, email,
				signature, support_call, support_chat, support_sales,support_email, facebook_link, twitter_link,"www.linkdin_link.com",  legal_notice, contact_us, privacy, copyrights,
				branding_from, null, SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.LINKEDIN_LINK_URL_FORMAT_NOT_VALID, test);



		// given the invalid format for the legal_notice
		test.log(LogStatus.INFO, "Create branding eamiler  in org of type " + organizationType);
		response = brandingSpogSever.updateBrandingemailerFororganization(organization_id, emailer_name, email,signature, support_call, support_chat, support_sales,support_email, facebook_link, twitter_link, linkdin_link,
				"www.linkdin_link.com/legal_notice", contact_us, privacy, copyrights,branding_from, test);
		brandingSpogSever.verifyBrandingEmailerForOrganization(response, organization_id, emailer_name, email,
				signature, support_call, support_chat, support_sales, support_email,facebook_link, twitter_link, linkdin_link,
				"www.linkdin_link.com/legal_notice", contact_us, privacy, copyrights,branding_from, null, SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.LEGAL_NOTICE_URL_FORMAT_NOT_VALID, test);

		// given the invalid format for the contact_us
		test.log(LogStatus.INFO, "Create branding eamiler  in org of type " + organizationType);
		response = brandingSpogSever.updateBrandingemailerFororganization(organization_id, emailer_name, email,
				signature, support_call, support_chat, support_sales,support_email, facebook_link, twitter_link, linkdin_link,legal_notice, "864875894", privacy, copyrights, branding_from, test);
		brandingSpogSever.verifyBrandingEmailerForOrganization(response, organization_id, emailer_name, email,
				signature, support_call, support_chat, support_sales,support_email, facebook_link, twitter_link, linkdin_link,legal_notice, "864875894", privacy, copyrights, branding_from, null,SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.CONTACT_US_URL_FORMAT_NOT_VALID, test);

		// given the invalid format for the privacy
		test.log(LogStatus.INFO, "Create branding eamiler  in org of type " + organizationType);
		response = brandingSpogSever.updateBrandingemailerFororganization(organization_id, emailer_name, email,
				signature, support_call, support_chat, support_sales,support_email, facebook_link, twitter_link, linkdin_link,legal_notice, contact_us, "GDGFD", copyrights, branding_from, test);
		brandingSpogSever.verifyBrandingEmailerForOrganization(response, organization_id, emailer_name, email,
				signature, support_call, support_chat, support_sales,support_email, facebook_link, twitter_link, linkdin_link,legal_notice, contact_us, "GDGFD", copyrights, branding_from, null,
				SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.PRIVACY_URL_FORMAT_NOT_VALID, test);

	}

	@DataProvider(name = "update_Branding_Emialer_403")
	public final Object[][] updateBrandingemailerValidParams_1() {
		return new Object[][] {
			// different role id
			{ "Update_Branding_Emailer  for the direct organziation with the another direct org user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.direct_org2_user2_token,"eswari","nagamalleswari.sykam@arcserve.com", "nagamaaleswari.sykam", null,
				"http://americanstandard.com/saleschat", "http://americanstandard.com/sales","http://facebook.com", "http://twitter.com", null, "http://facebook.com",
				"http://americanstandard.com/saleschat", "http://facebook.com/americanstandard","http://facebook.com/americanstandard", "string", "SELF","cloudsupport@arcserve.com" },
			{ "Update_Branding_Emailer  for the direct organziation with the normal msp  org user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.normal_msp_org1_user1_token,"eswari","nagamalleswari.sykam@arcserve.com", "nagamaaleswari.sykam", null,
					"http://americanstandard.com/saleschat", "http://americanstandard.com/sales","http://facebook.com", "http://twitter.com", null, "http://facebook.com",
					"http://americanstandard.com/saleschat", "http://facebook.com/americanstandard","http://facebook.com/americanstandard", "string", "SELF","cloudsupport@arcserve.com" },
			{ "Update_Branding_Emailer  for the direct organziation with the normal msp account admin user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,"eswari","nagamalleswari.sykam@arcserve.com", "nagamaaleswari.sykam", null,
						"http://americanstandard.com/saleschat", "http://americanstandard.com/sales","http://facebook.com", "http://twitter.com", null, "http://facebook.com",
						"http://americanstandard.com/saleschat", "http://facebook.com/americanstandard","http://facebook.com/americanstandard", "string", "SELF","cloudsupport@arcserve.com" },
			{ "Update_Branding_Emailer  for the direct organziation with the sub org user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.normal_msp1_suborg1_user1_token,"eswari","nagamalleswari.sykam@arcserve.com", "nagamaaleswari.sykam", null,
							"http://americanstandard.com/saleschat", "http://americanstandard.com/sales","http://facebook.com", "http://twitter.com", null, "http://facebook.com",
							"http://americanstandard.com/saleschat", "http://facebook.com/americanstandard","http://facebook.com/americanstandard", "string", "SELF","cloudsupport@arcserve.com" },
			{ "Update_Branding_Emailer  for the direct organziation with the root msp  org user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.root_msp_org1_user1_token,"eswari","nagamalleswari.sykam@arcserve.com", "nagamaaleswari.sykam", null,
								"http://americanstandard.com/saleschat", "http://americanstandard.com/sales","http://facebook.com", "http://twitter.com", null, "http://facebook.com",
								"http://americanstandard.com/saleschat", "http://facebook.com/americanstandard","http://facebook.com/americanstandard", "string", "SELF","cloudsupport@arcserve.com" },
			{ "Update_Branding_Emailer  for the direct organziation with the root msp account admin  user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.root_msp_org1_msp_accountadmin1_token,"eswari","nagamalleswari.sykam@arcserve.com", "nagamaaleswari.sykam", null,
									"http://americanstandard.com/saleschat", "http://americanstandard.com/sales","http://facebook.com", "http://twitter.com", null, "http://facebook.com",
									"http://americanstandard.com/saleschat", "http://facebook.com/americanstandard","http://facebook.com/americanstandard", "string", "SELF","cloudsupport@arcserve.com" },
			{ "Update_Branding_Emailer  for the direct organziation with the root msp sub organization user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.normal_msp1_suborg1_user1_token,"eswari","nagamalleswari.sykam@arcserve.com", "nagamaaleswari.sykam", null,
										"http://americanstandard.com/saleschat", "http://americanstandard.com/sales","http://facebook.com", "http://twitter.com", null, "http://facebook.com",
										"http://americanstandard.com/saleschat", "http://facebook.com/americanstandard","http://facebook.com/americanstandard", "string", "SELF","cloudsupport@arcserve.com" },
			{ "Update_Branding_Emailer  for the direct organziation with the sub msp organization user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.root_msp1_submsp1_user1_token,"eswari","nagamalleswari.sykam@arcserve.com", "nagamaaleswari.sykam", null,
											"http://americanstandard.com/saleschat", "http://americanstandard.com/sales","http://facebook.com", "http://twitter.com", null, "http://facebook.com",
											"http://americanstandard.com/saleschat", "http://facebook.com/americanstandard","http://facebook.com/americanstandard", "string", "SELF","cloudsupport@arcserve.com" },
			{ "Update_Branding_Emailer  for the direct organziation with the sub msp account admin organization user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.root_msp1_submsp1_account_admin_token,"eswari","nagamalleswari.sykam@arcserve.com", "nagamaaleswari.sykam", null,
												"http://americanstandard.com/saleschat", "http://americanstandard.com/sales","http://facebook.com", "http://twitter.com", null, "http://facebook.com",
												"http://americanstandard.com/saleschat", "http://facebook.com/americanstandard","http://facebook.com/americanstandard", "string", "SELF","cloudsupport@arcserve.com" },
			{ "Update_Branding_Emailer  for the direct organziation with the sub msp sub organization user token",ti.direct_org1_user1_token,ti.direct_org1_id,ti.root_msp1_suborg1_user1_token,"eswari","nagamalleswari.sykam@arcserve.com", "nagamaaleswari.sykam", null,
													"http://americanstandard.com/saleschat", "http://americanstandard.com/sales","http://facebook.com", "http://twitter.com", null, "http://facebook.com",
													"http://americanstandard.com/saleschat", "http://facebook.com/americanstandard","http://facebook.com/americanstandard", "string", "SELF","cloudsupport@arcserve.com" },

			//Normal MSP Organizations with other roles users token 
			{ "Update_Branding_Emailer  for the Noraml MSP organziation with the another direct org user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.direct_org1_user2_token,"eswari","nagamalleswari.sykam@arcserve.com", "nagamaaleswari.sykam", null,
														"http://americanstandard.com/saleschat", "http://americanstandard.com/sales","http://facebook.com", "http://twitter.com", null, "http://facebook.com",
														"http://americanstandard.com/saleschat", "http://facebook.com/americanstandard","http://facebook.com/americanstandard", "string", "SELF","cloudsupport@arcserve.com" },
			{ "Update_Branding_Emailer  for the Noraml MSP organziation with the normal msp2 org user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.normal_msp_org2_user1_token,"eswari","nagamalleswari.sykam@arcserve.com", "nagamaaleswari.sykam", null,
															"http://americanstandard.com/saleschat", "http://americanstandard.com/sales","http://facebook.com", "http://twitter.com", null, "http://facebook.com",
															"http://americanstandard.com/saleschat", "http://facebook.com/americanstandard","http://facebook.com/americanstandard", "string", "SELF","cloudsupport@arcserve.com" },
			{ "Update_Branding_Emailer  for the Noraml MSP organziation with the normal msp account admin user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.normal_msp_org2_msp_accountadmin1_token,"eswari","nagamalleswari.sykam@arcserve.com", "nagamaaleswari.sykam", null,
																"http://americanstandard.com/saleschat", "http://americanstandard.com/sales","http://facebook.com", "http://twitter.com", null, "http://facebook.com",
																"http://americanstandard.com/saleschat", "http://facebook.com/americanstandard","http://facebook.com/americanstandard", "string", "SELF","cloudsupport@arcserve.com" },
			{ "Update_Branding_Emailer  for the Noraml MSP organziation with the sub org user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.normal_msp1_suborg1_user1_token,"eswari","nagamalleswari.sykam@arcserve.com", "nagamaaleswari.sykam", null,
																	"http://americanstandard.com/saleschat", "http://americanstandard.com/sales","http://facebook.com", "http://twitter.com", null, "http://facebook.com",
																	"http://americanstandard.com/saleschat", "http://facebook.com/americanstandard","http://facebook.com/americanstandard", "string", "SELF","cloudsupport@arcserve.com" },
			{ "Update_Branding_Emailer  for the Noraml MSP organziation with the root msp  org user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.root_msp_org2_user1_token,"eswari","nagamalleswari.sykam@arcserve.com", "nagamaaleswari.sykam", null,
																		"http://americanstandard.com/saleschat", "http://americanstandard.com/sales","http://facebook.com", "http://twitter.com", null, "http://facebook.com",
																		"http://americanstandard.com/saleschat", "http://facebook.com/americanstandard","http://facebook.com/americanstandard", "string", "SELF","cloudsupport@arcserve.com" },
			{ "Update_Branding_Emailer  for the Noraml MSP organziation with the root msp account admin  user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.root_msp_org1_msp_accountadmin1_token,"eswari","nagamalleswari.sykam@arcserve.com", "nagamaaleswari.sykam", null,
																			"http://americanstandard.com/saleschat", "http://americanstandard.com/sales","http://facebook.com", "http://twitter.com", null, "http://facebook.com",
																			"http://americanstandard.com/saleschat", "http://facebook.com/americanstandard","http://facebook.com/americanstandard", "string", "SELF","cloudsupport@arcserve.com" },
			{ "Update_Branding_Emailer  for the Noraml MSP organziation with the root msp sub organization user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.normal_msp1_suborg1_user1_token,"eswari","nagamalleswari.sykam@arcserve.com", "nagamaaleswari.sykam", null,
																				"http://americanstandard.com/saleschat", "http://americanstandard.com/sales","http://facebook.com", "http://twitter.com", null, "http://facebook.com",
																				"http://americanstandard.com/saleschat", "http://facebook.com/americanstandard","http://facebook.com/americanstandard", "string", "SELF","cloudsupport@arcserve.com" },
			{ "Update_Branding_Emailer  for the Noraml MSP organziation with the sub msp organization user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.root_msp1_submsp1_user1_token,"eswari","nagamalleswari.sykam@arcserve.com", "nagamaaleswari.sykam", null,
																					"http://americanstandard.com/saleschat", "http://americanstandard.com/sales","http://facebook.com", "http://twitter.com", null, "http://facebook.com",
																					"http://americanstandard.com/saleschat", "http://facebook.com/americanstandard","http://facebook.com/americanstandard", "string", "SELF","cloudsupport@arcserve.com" },
			{ "Update_Branding_Emailer  for the Noraml MSP organziation with the sub msp account admin organization user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.root_msp1_submsp1_account_admin_token,"eswari","nagamalleswari.sykam@arcserve.com", "nagamaaleswari.sykam", null,
																						"http://americanstandard.com/saleschat", "http://americanstandard.com/sales","http://facebook.com", "http://twitter.com", null, "http://facebook.com",
																						"http://americanstandard.com/saleschat", "http://facebook.com/americanstandard","http://facebook.com/americanstandard", "string", "SELF","cloudsupport@arcserve.com" },
			{ "Update_Branding_Emailer  for the Noraml MSP organziation with the sub msp sub organization user token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id,ti.root_msp1_suborg1_user1_token,"eswari","nagamalleswari.sykam@arcserve.com", "nagamaaleswari.sykam", null,
																							"http://americanstandard.com/saleschat", "http://americanstandard.com/sales","http://facebook.com", "http://twitter.com", null, "http://facebook.com",
																							"http://americanstandard.com/saleschat", "http://facebook.com/americanstandard","http://facebook.com/americanstandard", "string", "SELF","cloudsupport@arcserve.com" },


			//Normal MSP Organizations with other roles users token 
			{ "Update_Branding_Emailer  for the Root Msp organziation with the direct org user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.direct_org1_user1_token,"eswari","nagamalleswari.sykam@arcserve.com", "nagamaaleswari.sykam", null,
																								"http://americanstandard.com/saleschat", "http://americanstandard.com/sales","http://facebook.com", "http://twitter.com", null, "http://facebook.com",
																								"http://americanstandard.com/saleschat", "http://facebook.com/americanstandard","http://facebook.com/americanstandard", "string", "SELF","cloudsupport@arcserve.com" },
			{ "Update_Branding_Emailer  for the Root Msp organziation with the normal msp  org user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.normal_msp_org1_user1_token,"eswari","nagamalleswari.sykam@arcserve.com", "nagamaaleswari.sykam", null,
																									"http://americanstandard.com/saleschat", "http://americanstandard.com/sales","http://facebook.com", "http://twitter.com", null, "http://facebook.com",
																									"http://americanstandard.com/saleschat", "http://facebook.com/americanstandard","http://facebook.com/americanstandard", "string", "SELF","cloudsupport@arcserve.com" },
			{ "Update_Branding_Emailer  for the Root Msp organziation with the normal msp account admin user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.normal_msp_org1_msp_accountadmin1_token,"eswari","nagamalleswari.sykam@arcserve.com", "nagamaaleswari.sykam", null,
																										"http://americanstandard.com/saleschat", "http://americanstandard.com/sales","http://facebook.com", "http://twitter.com", null, "http://facebook.com",
																										"http://americanstandard.com/saleschat", "http://facebook.com/americanstandard","http://facebook.com/americanstandard", "string", "SELF","cloudsupport@arcserve.com" },
			{ "Update_Branding_Emailer  for the Root Msp organziation with the sub org user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.normal_msp1_suborg1_user1_token,"eswari","nagamalleswari.sykam@arcserve.com", "nagamaaleswari.sykam", null,
																											"http://americanstandard.com/saleschat", "http://americanstandard.com/sales","http://facebook.com", "http://twitter.com", null, "http://facebook.com",
																											"http://americanstandard.com/saleschat", "http://facebook.com/americanstandard","http://facebook.com/americanstandard", "string", "SELF","cloudsupport@arcserve.com" },
			{ "Update_Branding_Emailer  for the Root Msp organziation with the root msp  org user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.root_msp_org2_user1_token,"eswari","nagamalleswari.sykam@arcserve.com", "nagamaaleswari.sykam", null,
																												"http://americanstandard.com/saleschat", "http://americanstandard.com/sales","http://facebook.com", "http://twitter.com", null, "http://facebook.com",
																												"http://americanstandard.com/saleschat", "http://facebook.com/americanstandard","http://facebook.com/americanstandard", "string", "SELF","cloudsupport@arcserve.com" },
			{ "Update_Branding_Emailer  for the Root Msp organziation with the root msp account admin  user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.root_msp_org2_msp_accountadmin1_token,"eswari","nagamalleswari.sykam@arcserve.com", "nagamaaleswari.sykam", null,
																													"http://americanstandard.com/saleschat", "http://americanstandard.com/sales","http://facebook.com", "http://twitter.com", null, "http://facebook.com",
																													"http://americanstandard.com/saleschat", "http://facebook.com/americanstandard","http://facebook.com/americanstandard", "string", "SELF","cloudsupport@arcserve.com" },
			{ "Update_Branding_Emailer  for the Root Msp organziation with the root msp sub organization user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.normal_msp1_suborg1_user1_token,"eswari","nagamalleswari.sykam@arcserve.com", "nagamaaleswari.sykam", null,
																														"http://americanstandard.com/saleschat", "http://americanstandard.com/sales","http://facebook.com", "http://twitter.com", null, "http://facebook.com",
																														"http://americanstandard.com/saleschat", "http://facebook.com/americanstandard","http://facebook.com/americanstandard", "string", "SELF","cloudsupport@arcserve.com" },
			{ "Update_Branding_Emailer  for the Root Msp organziation with the sub msp organization user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.root_msp1_submsp1_user1_token,"eswari","nagamalleswari.sykam@arcserve.com", "nagamaaleswari.sykam", null,
																															"http://americanstandard.com/saleschat", "http://americanstandard.com/sales","http://facebook.com", "http://twitter.com", null, "http://facebook.com",
																															"http://americanstandard.com/saleschat", "http://facebook.com/americanstandard","http://facebook.com/americanstandard", "string", "SELF","cloudsupport@arcserve.com" },
			{ "Update_Branding_Emailer  for the Root Msp organziation with the sub msp account admin organization user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.root_msp1_submsp1_account_admin_token,"eswari","nagamalleswari.sykam@arcserve.com", "nagamaaleswari.sykam", null,
																																"http://americanstandard.com/saleschat", "http://americanstandard.com/sales","http://facebook.com", "http://twitter.com", null, "http://facebook.com",
																																"http://americanstandard.com/saleschat", "http://facebook.com/americanstandard","http://facebook.com/americanstandard", "string", "SELF","cloudsupport@arcserve.com" },
			{ "Update_Branding_Emailer  for the Root Msp organziation with the sub msp sub organization user token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id,ti.root_msp1_suborg1_user1_token,"eswari","nagamalleswari.sykam@arcserve.com", "nagamaaleswari.sykam", null,
																																	"http://americanstandard.com/saleschat", "http://americanstandard.com/sales","http://facebook.com", "http://twitter.com", null, "http://facebook.com",
																																	"http://americanstandard.com/saleschat", "http://facebook.com/americanstandard","http://facebook.com/americanstandard", "string", "SELF","cloudsupport@arcserve.com" },

		};
	}

	@Test(dataProvider = "update_Branding_Emialer_403")
	public void updateBrandingemailerFororganizationValid_403(String Testcase, 
			String validToken,
			String organization_id,
			String another_org_usertoken,
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
			String copyrights,
			String branding_from,
			String support_email) {
		test = ExtentManager.getNewTest(Testcase);

		brandingSpogSever.setToken(another_org_usertoken);

		test.log(LogStatus.INFO, "Create branding eamiler  in org of type " + Testcase);
		Response response = brandingSpogSever.updateBrandingemailerFororganization(organization_id, emailer_name, email,signature, support_call, support_chat, support_sales,support_email, facebook_link, twitter_link, linkdin_link,legal_notice, contact_us, privacy, copyrights, branding_from, test);
		brandingSpogSever.verifyBrandingEmailerForOrganization(response, organization_id, emailer_name, email,
				signature, support_call, support_chat, support_sales,support_email, facebook_link, twitter_link, linkdin_link,legal_notice, contact_us, privacy, copyrights, branding_from, null,
				SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY, test);

	}

	@DataProvider(name = "Update_Brandingemailer_valid_404")
	public final Object[][] updateBrandingemailerValidParams_2() {
		return new Object[][] {
			{ "Create_Branding  for the direct organziationwith the direct user valid token",ti.direct_org1_user1_token,ti.direct_org1_id, "eswari","nagamalleswari.sykam@arcserve.com", "nagamaaleswari.sykam",
				null, "http://americanstandard.com/saleschat", "http://americanstandard.com/sales","http://facebook.com", "http://twitter.com", null, "http://facebook.com",
				"http://americanstandard.com/saleschat", "http://facebook.com/americanstandard","http://facebook.com/americanstandard", "string", "SELF","cloudsupport@arcserve.com" },
			{ "Create_Branding for the Noraml Msp organziation with the msp org user valid token",ti.normal_msp_org1_user1_token,ti.normal_msp_org1_id, "eswari","nagamalleswari.sykam@arcserve.com", "nagamaaleswari.sykam",
					null, "http://americanstandard.com/saleschat", "http://americanstandard.com/sales","http://facebook.com", "http://twitter.com", null, "http://facebook.com",
					"http://americanstandard.com/saleschat", "http://facebook.com/americanstandard","http://facebook.com/americanstandard", "string", "SELF","cloudsupport@arcserve.com" },
			{ "Create_Brandingfor the root msp organziation with the msp account admin user valid token",ti.root_msp_org1_user1_token,ti.root_msp_org1_id, "eswari","nagamalleswari.sykam@arcserve.com", "nagamaaleswari.sykam",
						null, "http://americanstandard.com/saleschat", "http://americanstandard.com/sales","http://facebook.com", "http://twitter.com", null, "http://facebook.com",
						"http://americanstandard.com/saleschat", "http://facebook.com/americanstandard","http://facebook.com/americanstandard", "string", "SELF","cloudsupport@arcserve.com" },

			{ "Create_Branding  for the direct organziationwith the direct user valid token",ti.csr_token, ti.direct_org2_id,"eswari","nagamalleswari.sykam@arcserve.com", "nagamaaleswari.sykam",
							null, "http://americanstandard.com/saleschat", "http://americanstandard.com/sales","http://facebook.com", "http://twitter.com", null, "http://facebook.com",
							"http://americanstandard.com/saleschat", "http://facebook.com/americanstandard","http://facebook.com/americanstandard", "string", "SELF","cloudsupport@arcserve.com" },
			{ "Create_Branding for the Noraml Msp organziation with the msp org user valid token",ti.csr_token, ti.normal_msp_org2_id,"eswari","nagamalleswari.sykam@arcserve.com", "nagamaaleswari.sykam",
								null, "http://americanstandard.com/saleschat", "http://americanstandard.com/sales","http://facebook.com", "http://twitter.com", null, "http://facebook.com",
								"http://americanstandard.com/saleschat", "http://facebook.com/americanstandard","http://facebook.com/americanstandard", "string", "SELF","cloudsupport@arcserve.com" },
			{ "Create_Brandingfor the Sub msp organziation with the msp account admin user valid token", ti.csr_token,ti.root_msp2_submsp_org1_id,"eswari","nagamalleswari.sykam@arcserve.com", "nagamaaleswari.sykam",
									null, "http://americanstandard.com/saleschat", "http://americanstandard.com/sales","http://facebook.com", "http://twitter.com", null, "http://facebook.com",
									"http://americanstandard.com/saleschat", "http://facebook.com/americanstandard","http://facebook.com/americanstandard", "string", "SELF","cloudsupport@arcserve.com" },

		};
	}

	@Test(dataProvider = "Update_Brandingemailer_valid_404")
	public void updateBrandingemailerFororganizationValid_404_400(String organizationType,
			String validToken,
			String organization_id, 
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
			String copyrights,
			String branding_from,String support_email) {

		test = ExtentManager.getNewTest(this.getClass().getName() + "."
				+ Thread.currentThread().getStackTrace()[1].getMethodName() + "_" + organizationType);
		// given the random uuid

		brandingSpogSever.setToken(validToken);

		// generate random user_id
		String random_organization_id = UUID.randomUUID().toString();
		// valid token
		brandingSpogSever.setToken(validToken);

		test.log(LogStatus.INFO, "Create branding eamiler  in org of type " + organizationType);
		Response response = brandingSpogSever.updateBrandingemailerFororganization(random_organization_id, emailer_name,
				email, signature, support_call, support_chat, support_sales,support_email, facebook_link, twitter_link, linkdin_link,legal_notice, contact_us, privacy, copyrights, branding_from, test);
		brandingSpogSever.verifyBrandingEmailerForOrganization(response, random_organization_id, emailer_name, email,signature, support_call, support_chat, support_sales,support_email, facebook_link, twitter_link, linkdin_link,legal_notice, contact_us, privacy, copyrights, branding_from, null,
				SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED, test);

		test.log(LogStatus.INFO, "Create branding eamiler  in org of type " + organizationType);
		response = brandingSpogSever.updateBrandingemailerFororganization("junk", emailer_name, email, signature,support_call, support_chat, support_sales,support_email, facebook_link, twitter_link, linkdin_link,
				legal_notice, contact_us, privacy, copyrights, branding_from, test);
		// brandingSpogSever.verifybrandingemailerfororgnzation(response,"junk",emailer_name,email,signature,support_call,support_chat,support_sales,facebook_link,twitter_link,linkdin_link,legal_notice,contact_us,privacy,copyrights,branding_from,SpogConstants.REQUIRED_INFO_NOT_EXIST,SpogMessageCode.ELEMENT_ORGANIZATION_ID_IS_NOT_UUID,
		// test);

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
