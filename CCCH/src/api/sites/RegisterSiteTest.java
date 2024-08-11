package api.sites;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Base64.EncodingBase64;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.GatewayServer.siteType;
import base.prepare.TestOrgInfo;
import InvokerServer.SPOGServer;
import InvokerServer.ServerResponseCode;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class RegisterSiteTest extends base.prepare.Is4Org {
	private GatewayServer gatewayServer;
	private SPOGServer spogServer;

	private EncodingBase64 base64;
	private ExtentTest test;
	private TestOrgInfo ti;

	private String common_password = "Mclaren@2020";
	private String csrAdminUserName;
	private String csrAdminPassword;

	private String csrReadOnlyUserName;
	private String csrReadOnlyPassword;
	private String csr_read_only_validToken;
	private String csr_read_only_user_id;

	private String prefix_direct = "SPOG_QA_RAMYA_BQ_direct";
	private String postfix_email = "@arcserve.com";
	private String direct_org_name = prefix_direct + "_org";
	private String direct_org_email = direct_org_name + postfix_email;
	private String direct_org_first_name = direct_org_name + "_first_name";
	private String direct_org_last_name = direct_org_name + "_last_name";
	private String direct_user_name = prefix_direct + "_admin";
	private String direct_user_name_email = prefix_direct + "_admin" + postfix_email;
	private String direct_user_first_name = direct_user_name + "_first_name";
	private String direct_user_last_name = direct_user_name + "_last_name";
	private String site_version = "1.0.0";
	private String gateway_hostname = "Ramya";

	// Related Information For MSP
	private String prefix_msp = "SPOG_QA_RAMYA_BQ_msp";
	private String msp_org_name = prefix_msp + "_org";
	private String msp_org_email = msp_org_name + postfix_email;
	private String msp_org_first_name = msp_org_name + "_first_name";
	private String msp_org_last_name = msp_org_name + "_last_name";
	private String msp_user_name = prefix_msp + "_admin";
	private String msp_user_name_email = prefix_msp + "_admin" + postfix_email;
	private String msp_user_first_name = msp_user_name + "_first_name";
	private String msp_user_last_name = msp_user_name + "_last_name";

	/*
	 * private SQLServerDb bqdb1; public int Nooftest;
	 * 
	 * long creationTime; String buildnumber=null; String BQName=null; private
	 * testcasescount count1; private String runningMachine; private String
	 * buildVersion;
	 */

	int passedcases = 0;
	int failedcases = 0;
	int skippedcases = 0;

	private String prefix_msp_account_admin = "SPOG_QA_RAMYA_BQ_msp_account";
	private String msp_account_admin_email = prefix_msp_account_admin + postfix_email;
	private String msp_account_admin_first_name = prefix_msp_account_admin + "_first_name";
	private String msp_account_admin_last_name = prefix_msp_account_admin + "_last_name";
	private String msp_account_admin_validToken;
	String msp_admin_name;
	String msp_account_admin_user_id;
	String msp_account_admin_id;

	private UserSpogServer userSpogServer;

	private String org_model_prefix = this.getClass().getSimpleName();

	@BeforeClass
	@Parameters({ "baseURI", "port", "logFolder", "runningMachine", "buildVersion" })
	public void beforeClass(String baseURI, String port, String logFolder, String runningMachine, String buildVersion) {

		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		rep = ExtentManager.getInstance(this.getClass().getSimpleName(), logFolder);
		Nooftest = 0;
		base64 = new EncodingBase64();
		bqdb1 = new SQLServerDb();
		String author = "Ramya.Nagepalli";
		count1 = new testcasescount();
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date = new java.util.Date();
		this.buildVersion = buildVersion + "_" + dateFormater.format(date);
		this.BQName = this.getClass().getSimpleName();
		this.runningMachine = runningMachine;
		System.out.println("The value of hit is " + count1.isstarttimehit());
		userSpogServer = new UserSpogServer(baseURI, port);
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
		test = rep.startTest("Setup");
		ti = new TestOrgInfo(spogServer, test);

	}

	@DataProvider(name = "organizationAndUserInfo")
	public final Object[][] getOrganizationAndUserInfo() {
		return new Object[][] { { direct_org_name, SpogConstants.DIRECT_ORG, direct_org_email, common_password,
				direct_org_first_name, direct_org_last_name, direct_user_name_email, common_password,
				direct_user_first_name, direct_user_last_name, SpogConstants.DIRECT_ADMIN }, };
	}

	@Test(dataProvider = "organizationAndUserInfo")
	public void RegisterSite_direct_msp_Organization(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName, String organizationLastName,
			String userEmail, String userPassword, String FirstName, String LastName, String Role_Id) {
		String site_registration_key = null;
		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Ramya  Nagepalli");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;
		FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + FirstName;
		LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + LastName;
		userEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;
		System.out.println("The value of the userEmail:" + userEmail);

		test.log(LogStatus.INFO, "Login to The csrAdmin");
		spogServer.setToken(ti.csr_token);
		String CSR_token = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "The Token is :" + CSR_token);
		String organization_id = spogServer.CreateOrganizationWithCheck(organizationName + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "The organization id is " + organization_id);
		test.log(LogStatus.INFO, "Create user for the Organization");
		Response response = spogServer.createUser(userEmail, userPassword, FirstName, LastName, Role_Id,
				organization_id, test);

		String user_id = spogServer.checkCreateUser(response, SpogConstants.SUCCESS_POST, userEmail, FirstName,
				LastName, Role_Id, organization_id, "", test);
		test.log(LogStatus.INFO, "The userid is " + user_id);
		test.log(LogStatus.INFO, "Login with the user created");
		spogServer.userLogin(userEmail, userPassword);
		test.log(LogStatus.INFO, "Getting the JWTToken for the Logged in user");
		String validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "The actual token is :" + validToken);
		test.log(LogStatus.INFO, "Generating a Random SiteName");
		String siteName = spogServer.getRandomSiteName("TestCreate");
		String sitetype = siteType.gateway.toString();
		test.log(LogStatus.INFO, "The siteType :" + sitetype);
		test.log(LogStatus.INFO, "Creating a site For Logged in user");
		response = spogServer.createSite(siteName, sitetype, organization_id, validToken, test);
		System.out.println("The value of the response:" + response.getBody().asString());
		Map<String, String> sitecreateResMap = new HashMap<>();

		sitecreateResMap = spogServer.checkCreateSite(response, SpogConstants.SUCCESS_POST, siteName, sitetype,
				organization_id, user_id, "", test);
		String registration_basecode = sitecreateResMap.get("registration_basecode");
		test.log(LogStatus.INFO, "The encoded message is:" + registration_basecode);
		String site_id = sitecreateResMap.get("site_id");
		test.log(LogStatus.INFO, "The generated site_id is :" + site_id);
		site_registration_key = base64.DecodeSiteRegistrationKey(registration_basecode);
		test.log(LogStatus.INFO, "After decoding the site_registration_key : " + site_registration_key);

		String gateway_id = UUID.randomUUID().toString();
		test.log(LogStatus.INFO, "After decoding the gateway_id : " + gateway_id);

		gateway_hostname = RandomStringUtils.randomAlphanumeric(4) + "_" + gateway_hostname;
		boolean is_registered = true;
		response = gatewayServer.RegisterSite(site_registration_key, gateway_id, gateway_hostname, site_version,
				site_id, test);
		String site_secret = gatewayServer.checkRegisterSite(response, ServerResponseCode.Succes_Login, site_id,
				siteName, sitetype, organization_id, user_id, is_registered, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		test.log(LogStatus.INFO, "The value of the generated  secret_id :" + site_secret);

	}

	@Test(dataProvider = "organizationAndUserInfo")
	public void RegisterSite_direct_msp_Organization_withInvalidBase64(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName, String organizationLastName,
			String userEmail, String userPassword, String FirstName, String LastName, String Role_Id) {
		String site_registration_key = null;
		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Ramya  Nagepalli");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;
		FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + FirstName;
		LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + LastName;
		userEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;
		test.log(LogStatus.INFO, "Login to The csrAdmin");
		spogServer.setToken(ti.csr_token);
		String CSR_token = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "The Token is :" + CSR_token);
		String organization_id = spogServer.CreateOrganizationWithCheck(organizationName + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "The organization id is " + organization_id);
		test.log(LogStatus.INFO, "Create user for the Organization");
		Response response = spogServer.createUser(userEmail, userPassword, FirstName, LastName, Role_Id,
				organization_id, test);

		String user_id = spogServer.checkCreateUser(response, SpogConstants.SUCCESS_POST, userEmail, FirstName,
				LastName, Role_Id, organization_id, "", test);
		test.log(LogStatus.INFO, "The userid is " + user_id);
		test.log(LogStatus.INFO, "Login with the user created");
		spogServer.userLogin(userEmail, userPassword);
		test.log(LogStatus.INFO, "Getting the JWTToken for the Logged in user");
		String validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "The actual token is :" + validToken);
		test.log(LogStatus.INFO, "Generating a Random SiteName");
		String siteName = spogServer.getRandomSiteName("TestCreate");
		String sitetype = siteType.gateway.toString();
		test.log(LogStatus.INFO, "The siteType :" + sitetype);
		test.log(LogStatus.INFO, "Creating a site For Logged in user");
		response = spogServer.createSite(siteName, sitetype, organization_id, validToken, test);
		Map<String, String> sitecreateResMap = new HashMap<>();
		sitecreateResMap = spogServer.checkCreateSite(response, SpogConstants.SUCCESS_POST, siteName, sitetype,
				organization_id, user_id, "", test);
		String registration_basecode = sitecreateResMap.get("registration_basecode");

		test.log(LogStatus.INFO, "The encoded message is:" + registration_basecode);
		String site_id = sitecreateResMap.get("site_id");
		test.log(LogStatus.INFO, "The generated site_id is :" + site_id);
		site_registration_key = base64.DecodeSiteRegistrationKey(registration_basecode);
		test.log(LogStatus.INFO, "After decoding the site_registration_key : " + site_registration_key);
		site_registration_key = site_registration_key.toLowerCase();
		String gateway_id = UUID.randomUUID().toString();
		test.log(LogStatus.INFO, "After decoding the gateway_id : " + gateway_id);
		test.log(LogStatus.INFO, "Registering the site with invalid site_id :" + site_registration_key);

		gateway_hostname = RandomStringUtils.randomAlphanumeric(4) + "_" + gateway_hostname;
		boolean is_registered = true;
		response = gatewayServer.RegisterSite(site_registration_key, gateway_id, gateway_hostname, site_version,
				site_id, test);
		String site_secret = gatewayServer.checkRegisterSite(response, ServerResponseCode.Bad_Request, site_id,
				siteName, sitetype, organization_id, user_id, is_registered,
				SpogMessageCode.SITE_INVALID_REGISTRATION_KEY, test);
		test.log(LogStatus.INFO, "The value of secret_id :" + site_secret);

	}

	@Test(dataProvider = "organizationAndUserInfo")
	public void RegisterSite_direct_msp_Organization_withInvalidSiteID(String organizationName, String organizationType,
			String organizationEmail, String organizationPwd, String organizationFirstName, String organizationLastName,
			String userEmail, String userPassword, String FirstName, String LastName, String Role_Id) {
		String site_registration_key = null;
		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Ramya  Nagepalli");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationName;
		organizationEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationEmail;
		organizationFirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationFirstName;
		organizationLastName = RandomStringUtils.randomAlphanumeric(8) + "_" + organizationLastName;
		FirstName = RandomStringUtils.randomAlphanumeric(8) + "_" + FirstName;
		LastName = RandomStringUtils.randomAlphanumeric(8) + "_" + LastName;
		userEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userEmail;
		test.log(LogStatus.INFO, "Login to The csrAdmin");
		spogServer.setToken(ti.csr_token);
		String CSR_token = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "The Token is :" + CSR_token);
		String organization_id = spogServer.CreateOrganizationWithCheck(organizationName + org_model_prefix,
				organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,
				test);
		test.log(LogStatus.INFO, "The organization id is " + organization_id);
		test.log(LogStatus.INFO, "Create user for the Organization");
		Response response = spogServer.createUser(userEmail, userPassword, FirstName, LastName, Role_Id,
				organization_id, test);

		String user_id = spogServer.checkCreateUser(response, SpogConstants.SUCCESS_POST, userEmail, FirstName,
				LastName, Role_Id, organization_id, "", test);
		test.log(LogStatus.INFO, "The userid is " + user_id);
		test.log(LogStatus.INFO, "Login with the user created");
		spogServer.userLogin(userEmail, userPassword);
		test.log(LogStatus.INFO, "Getting the JWTToken for the Logged in user");
		String validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO, "The actual token is :" + validToken);
		test.log(LogStatus.INFO, "Generating a Random SiteName");
		String siteName = spogServer.getRandomSiteName("TestCreate");
		String sitetype = siteType.gateway.toString();
		test.log(LogStatus.INFO, "The siteType :" + sitetype);
		test.log(LogStatus.INFO, "Creating a site For Logged in user");
		response = spogServer.createSite(siteName, sitetype, organization_id, validToken, test);
		Map<String, String> sitecreateResMap = new HashMap<>();
		sitecreateResMap = spogServer.checkCreateSite(response, SpogConstants.SUCCESS_POST, siteName, sitetype,
				organization_id, user_id, "", test);
		String registration_basecode = sitecreateResMap.get("registration_basecode");

		test.log(LogStatus.INFO, "The encoded message is:" + registration_basecode);
		String site_id = sitecreateResMap.get("site_id");
		test.log(LogStatus.INFO, "The generated site_id is :" + site_id);

		site_registration_key = base64.DecodeSiteRegistrationKey(registration_basecode);
		test.log(LogStatus.INFO, "After decoding the site_registration_key : " + site_registration_key);

		String gateway_id = UUID.randomUUID().toString();
		test.log(LogStatus.INFO, "After decoding the gateway_id : " + gateway_id);
		test.log(LogStatus.INFO, "Registering the site with invalid site_id :" + site_id);
		site_id = UUID.randomUUID().toString();
		gateway_hostname = RandomStringUtils.randomAlphanumeric(4) + "_" + gateway_hostname;
		boolean is_registered = true;
		response = gatewayServer.RegisterSite(site_registration_key, gateway_id, gateway_hostname, site_version,
				site_id.toLowerCase(), test);
		gatewayServer.setUUID(site_id);
		String site_secret = gatewayServer.checkRegisterSite(response, ServerResponseCode.Resource_Does_Not_Exist,
				site_id, siteName, sitetype, organization_id, user_id, is_registered, SpogMessageCode.SITE_NOT_FOUND,
				test);
		test.log(LogStatus.INFO, "The value of secret_id :" + site_secret);

	}

	/*
	 * @Test public void ResgisterSite_withother_siteID() {
	 * 
	 * String site_registration_key=null; String site_registration_key1=null;
	 * test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.
	 * currentThread() .getStackTrace()[1].getMethodName()); test.assignAuthor(
	 * "Ramya  Nagepalli"); test.log(LogStatus.INFO,"Login to The csrAdmin");
	 * spogServer.userLogin(csrAdminUserName, csrAdminPassword,test); String
	 * CSR_token= spogServer.getJWTToken();
	 * 
	 * test.log(LogStatus.INFO, "Creating an MSP organization"); String
	 * organization_id
	 * =spogServer.CreateOrganizationWithCheck(msp_org_name+RandomStringUtils.
	 * randomAlphanumeric(8)+org_model_prefix, SpogConstants.MSP_ORG,
	 * RandomStringUtils.randomAlphanumeric(8) + "Spog"+postfix_email,
	 * common_password, RandomStringUtils.randomAlphanumeric(4),
	 * RandomStringUtils.randomAlphanumeric(4),test); test.log(LogStatus.INFO,
	 * "msp org id is "+organization_id); String userEmail=
	 * RandomStringUtils.randomAlphanumeric(8)+"_"+"SPOG_QA_RAMYA_BQ_"+
	 * postfix_email; String FirstName= RandomStringUtils.randomAlphanumeric(4);
	 * String LastName= RandomStringUtils.randomAlphanumeric(4); Response response =
	 * spogServer.createUser(userEmail,common_password,FirstName,LastName,
	 * SpogConstants.MSP_ADMIN,organization_id,test);
	 * 
	 * String user_id
	 * =spogServer.checkCreateUser(response,SpogConstants.SUCCESS_POST,userEmail
	 * ,FirstName,LastName,SpogConstants.MSP_ADMIN,organization_id,"",test);
	 * 
	 * //For MSP Organization
	 * 
	 * String organization_id1
	 * =spogServer.CreateOrganizationWithCheck(direct_org_name+RandomStringUtils
	 * .randomAlphanumeric(8)+org_model_prefix, SpogConstants.DIRECT_ORG,
	 * RandomStringUtils.randomAlphanumeric(8) + "spog"+postfix_email,
	 * common_password, RandomStringUtils.randomAlphanumeric(4),
	 * RandomStringUtils.randomAlphanumeric(4),test); test.log(LogStatus.INFO,
	 * "direct org id is "+organization_id1); String userEmail1=
	 * RandomStringUtils.randomAlphanumeric(8) + "SpogRamya"+postfix_email; String
	 * FirstName1= RandomStringUtils.randomAlphanumeric(4); String LastName1=
	 * RandomStringUtils.randomAlphanumeric(4); System.out.println(
	 * "The value of the organization id:"+organization_id1); response =
	 * spogServer.createUser(userEmail1,common_password,FirstName1,LastName1,
	 * SpogConstants.DIRECT_ADMIN,organization_id1,test);
	 * 
	 * String user_id1
	 * =spogServer.checkCreateUser(response,SpogConstants.SUCCESS_POST,
	 * userEmail1,FirstName1,LastName1,SpogConstants.DIRECT_ADMIN,
	 * organization_id1,"",test); String siteName=
	 * spogServer.getRandomSiteName("TestCreate"); test.log(LogStatus.INFO,
	 * "Site name of MSP admin is "+siteName); String
	 * sitetype=siteType.gateway.toString();
	 * 
	 * spogServer.userLogin(userEmail,common_password); test.log(LogStatus.INFO,
	 * "Getting the JWTToken for the Logged in user"); String validToken1 =
	 * spogServer.getJWTToken();
	 * 
	 * response = spogServer.createSite(siteName,sitetype, organization_id,
	 * validToken1,test); Map<String,String > sitecreateResMap=new HashMap<>();
	 * sitecreateResMap=spogServer.checkCreateSite(response,
	 * SpogConstants.SUCCESS_POST,siteName, sitetype,
	 * organization_id,user_id,"",test); String
	 * registration_basecode=sitecreateResMap.get("registration_basecode");
	 * site_registration_key=base64.DecodeSiteRegistrationKey(
	 * registration_basecode); String site_id=sitecreateResMap.get("site_id");
	 * 
	 * // System.out.println("The value of the generated base64key:"
	 * +site_registration_key); test.log(LogStatus.INFO,
	 * "The value of the direct_key is:"+site_registration_key);
	 * spogServer.userLogin(userEmail1,common_password);
	 * test.log(LogStatus.INFO,"Getting the JWTToken for the Logged in user");
	 * String validToken2 = spogServer.getJWTToken(); String siteName1=
	 * spogServer.getRandomSiteName("TestCreate"); test.log(LogStatus.INFO,
	 * "Site name of direct admin is "+siteName1); response =
	 * spogServer.createSite(siteName1,sitetype, organization_id1,
	 * validToken2,test); Map<String,String > sitecreateResMap1=new HashMap<>();
	 * sitecreateResMap1=spogServer.checkCreateSite(response,
	 * SpogConstants.SUCCESS_POST,siteName1, sitetype,
	 * organization_id1,user_id1,"",test); String
	 * registration_basecode1=sitecreateResMap1.get("registration_basecode"); String
	 * site_id1 = sitecreateResMap1.get("site_id");
	 * site_registration_key1=base64.DecodeSiteRegistrationKey(
	 * registration_basecode1); //System.out.println(
	 * "The value of the generated base64key:"+site_registration_key1);
	 * test.log(LogStatus.INFO,"The value of the direct_key is:"
	 * +site_registration_key1);
	 * 
	 * String gateway_id = UUID.randomUUID().toString();
	 * gateway_hostname=RandomStringUtils.randomAlphanumeric(4)+"_"+
	 * gateway_hostname; boolean is_registered=true;
	 * response=gatewayServer.RegisterSite(site_registration_key1,gateway_id,
	 * gateway_hostname,site_version,site_id,test); String
	 * site_secret=gatewayServer.checkRegisterSite(response,ServerResponseCode.
	 * Bad_Request, site_id,siteName1,sitetype,organization_id1,
	 * user_id1,is_registered,SpogMessageCode.SITE_INVALID_REGISTRATION_KEY,test );
	 * test.log(LogStatus.INFO,"The value of the generated  secret_id :"
	 * +site_secret); }
	 */

	@Test
	public void RegisterSite_csrAdmin_With_mspOrgId() {
		String site_registration_key = null;
		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Ramya  Nagepalli");
		test.log(LogStatus.INFO, "Login to The csrAdmin");
		spogServer.setToken(ti.csr_token);
		String CSR_token = spogServer.getJWTToken();

		// get the logged in user
		Response response = spogServer.getLoggedInUser(CSR_token, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		String organization_id1 = response.then().extract().path("data.organization_id");
		String user_id1 = response.then().extract().path("data.user_id");

		String organization_id = spogServer.CreateOrganizationWithCheck(
				msp_org_name + RandomStringUtils.randomAlphanumeric(8) + org_model_prefix, SpogConstants.MSP_ORG,
				RandomStringUtils.randomAlphanumeric(8) + "Spog" + postfix_email, common_password,
				RandomStringUtils.randomAlphanumeric(4), RandomStringUtils.randomAlphanumeric(4), test);
		String siteName = spogServer.getRandomSiteName("TestCreate");
		String sitetype = siteType.gateway.toString();
		response = spogServer.createSite(siteName, sitetype, organization_id, CSR_token, test);
		Map<String, String> sitecreateResMap = new HashMap<>();

		sitecreateResMap = spogServer.checkCreateSite(response, SpogConstants.SUCCESS_POST, siteName, sitetype,
				organization_id, user_id1, "", test);
		String registration_basecode = sitecreateResMap.get("registration_basecode");
		String site_id = sitecreateResMap.get("site_id");
		site_registration_key = base64.DecodeSiteRegistrationKey(registration_basecode);
		test.log(LogStatus.INFO, "The value of the generated base64key:" + site_registration_key);

		String gateway_id = UUID.randomUUID().toString();
		gateway_hostname = RandomStringUtils.randomAlphanumeric(4) + "_" + gateway_hostname;
		boolean is_registered = true;
		response = gatewayServer.RegisterSite(site_registration_key, gateway_id, gateway_hostname, site_version,
				site_id, test);
		String site_secret = gatewayServer.checkRegisterSite(response, ServerResponseCode.Succes_Login, site_id,
				siteName, sitetype, organization_id, user_id1, is_registered, SpogMessageCode.SUCCESS_GET_PUT_DEL,
				test);
		test.log(LogStatus.INFO, "The value of the generated  secret_id :" + site_secret);
	}

	@Test
	public void RegisterSite_csrAdmin_without_OrganizationId_Msp() {
		String site_registration_key = null;
		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Ramya  Nagepalli");
		test.log(LogStatus.INFO, "Login to The csrAdmin");
		spogServer.setToken(ti.csr_token);
		String CSR_token = spogServer.getJWTToken();
		// get the logged in user
		Response response = spogServer.getLoggedInUser(CSR_token, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		String organization_id1 = response.then().extract().path("data.organization_id");
		String user_id1 = response.then().extract().path("data.user_id");

		String organization_id = spogServer.CreateOrganizationWithCheck(
				msp_org_name + RandomStringUtils.randomAlphanumeric(8) + org_model_prefix, SpogConstants.MSP_ORG,
				RandomStringUtils.randomAlphanumeric(8) + "Spog" + postfix_email, common_password,
				RandomStringUtils.randomAlphanumeric(4), RandomStringUtils.randomAlphanumeric(4), test);
		// System.out.println("The value of the Generated organization
		// id:"+organization_id);
		test.log(LogStatus.INFO, "The value of the Organization_id" + organization_id);
		String siteName = spogServer.getRandomSiteName("TestCreate");
		String sitetype = siteType.gateway.toString();
		response = spogServer.createSite(siteName, sitetype, organization_id, CSR_token, test);
		Map<String, String> sitecreateResMap = new HashMap<>();
		sitecreateResMap = spogServer.checkCreateSite(response, SpogConstants.SUCCESS_POST, siteName, sitetype,
				organization_id, user_id1, "", test);
		String registration_basecode = sitecreateResMap.get("registration_basecode");
		String site_id = sitecreateResMap.get("site_id");
		site_registration_key = base64.DecodeSiteRegistrationKey(registration_basecode);
		test.log(LogStatus.INFO, "The value of the registration_key:" + site_registration_key);

		String gateway_id = UUID.randomUUID().toString();
		gateway_hostname = RandomStringUtils.randomAlphanumeric(4) + "_" + gateway_hostname;
		boolean is_registered = true;
		response = gatewayServer.RegisterSite(site_registration_key, gateway_id, gateway_hostname, site_version,
				site_id, test);
		String site_secret = gatewayServer.checkRegisterSite(response, ServerResponseCode.Succes_Login, site_id,
				siteName, sitetype, "", user_id1, is_registered, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		test.log(LogStatus.INFO, "The value of the generated  secret_id :" + site_secret);
	}

	@Test
	public void RegisterSite_csrAdmin() {
		String site_registration_key = null;
		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Ramya  Nagepalli");
		test.log(LogStatus.INFO, "Login to The csrAdmin");
		
		spogServer.setToken(ti.csr_token);
		String CSR_token = spogServer.getJWTToken();

		// get the logged in user
		Response response = spogServer.getLoggedInUser(CSR_token, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		String organization_id = response.then().extract().path("data.organization_id");
		String user_id = response.then().extract().path("data.user_id");

		// creating a site
		String siteName = spogServer.getRandomSiteName("TestCreate");
		String sitetype = siteType.gateway.toString();
		response = spogServer.createSite(siteName, sitetype, organization_id, CSR_token, test);
		Map<String, String> sitecreateResMap = new HashMap<>();
		sitecreateResMap = spogServer.checkCreateSite(response, SpogConstants.SUCCESS_POST, siteName, sitetype,
				organization_id, user_id, "", test);
		String registration_basecode = sitecreateResMap.get("registration_basecode");
		String site_id = sitecreateResMap.get("site_id");
		site_registration_key = base64.DecodeSiteRegistrationKey(registration_basecode);

		test.log(LogStatus.INFO, "The value of the generated base64key:" + site_registration_key);

		String gateway_id = UUID.randomUUID().toString();
		gateway_hostname = RandomStringUtils.randomAlphanumeric(4) + "_" + gateway_hostname;
		boolean is_registered = true;
		response = gatewayServer.RegisterSite(site_registration_key, gateway_id, gateway_hostname, site_version,
				site_id, test);
		String site_secret = gatewayServer.checkRegisterSite(response, ServerResponseCode.Succes_Login, site_id,
				siteName, sitetype, "", user_id, is_registered, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		test.log(LogStatus.INFO, "The value of secret_id :" + site_secret);
	}

	@Test
	public void RegisterSite_csrAdmin_InvalidBase64() {
		String site_registration_key = null;
		test = ExtentManager.getNewTest(
				this.getClass().getName() + "." + Thread.currentThread().getStackTrace()[1].getMethodName());
		test.assignAuthor("Ramya Nagepalli");
		test.log(LogStatus.INFO, "Login to The csrAdmin");
		spogServer.setToken(ti.csr_token);
		String CSR_token = spogServer.getJWTToken();

		// get the logged in user
		Response response = spogServer.getLoggedInUser(CSR_token, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		String organization_id = response.then().extract().path("data.organization_id");
		String user_id = response.then().extract().path("data.user_id");

		String siteName = spogServer.getRandomSiteName("TestCreate");
		String sitetype = siteType.gateway.toString();
		response = spogServer.createSite(siteName, sitetype, organization_id, CSR_token, test);
		Map<String, String> sitecreateResMap = new HashMap<>();
		sitecreateResMap = spogServer.checkCreateSite(response, SpogConstants.SUCCESS_POST, siteName, sitetype,
				organization_id, user_id, "", test);
		String registration_basecode = sitecreateResMap.get("registration_basecode");
		String site_id = sitecreateResMap.get("site_id");
		site_registration_key = base64.DecodeSiteRegistrationKey(registration_basecode);
		site_registration_key = site_registration_key.toLowerCase();
		test.log(LogStatus.INFO, "The invalid site Registation Key :" + site_registration_key);

		String gateway_id = UUID.randomUUID().toString();
		gateway_hostname = RandomStringUtils.randomAlphanumeric(4) + "_" + gateway_hostname;
		boolean is_registered = true;
		response = gatewayServer.RegisterSite(site_registration_key, gateway_id, gateway_hostname, site_version,
				site_id, test);
		String site_secret = gatewayServer.checkRegisterSite(response, ServerResponseCode.Bad_Request, site_id,
				siteName, sitetype, organization_id, user_id, is_registered,
				SpogMessageCode.SITE_INVALID_REGISTRATION_KEY, test);
		test.log(LogStatus.INFO, "The value of the generated  secret_id :" + site_secret);

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

	@AfterClass
	public void delete_Test_created_sites() {
		test.log(LogStatus.INFO, "Login to The csrAdmin");
		spogServer.setToken(ti.csr_token);

		Response response = spogServer.getSites("site_name=TestCreate");
		ArrayList<String> sites = new ArrayList<>();
		sites = response.then().extract().path("data.site_id");
		
		sites.stream().forEach(site->{
			spogServer.deleteSite(site, spogServer.getJWTToken());
		});

		/*for (int i = 0; i < sites.size(); i++) {

			String site_id = sites.get(i);

			spogServer.deleteSite(site_id, spogServer.getJWTToken());
		}*/

	}

}
