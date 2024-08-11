package api.sites;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import io.restassured.response.Response;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;

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

import InvokerServer.GatewayServer;
import InvokerServer.ServerResponseCode;
import InvokerServer.GatewayServer.siteType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.SPOGServer;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Base64.Base64Coder;

public class GetsiteconfigurationTest extends base.prepare.Is4Org {
	private GatewayServer gatewayServer;
	private SPOGServer spogServer;
	//public int Nooftest;
	//private ExtentReports rep;
	private Base64Coder base64; 
	private ExtentTest test;
	private String common_password = "Mclaren@2013";
	private String csrAdminUserName;
	private String csrAdminPassword;
	private String csr_readonly_email;
	private String csr_readonly_token;
	private String prefix_direct = "spog_prasad_direct";
	private String postfix_email = "@arcserve.com";
	private String direct_org_name = prefix_direct + "_org";
	private String direct_org_email = direct_org_name + postfix_email;
	private String direct_org_first_name = direct_org_name + "_first_name";
	private String direct_org_last_name = direct_org_name + "_last_name";
	private String direct_user_name = prefix_direct + "_admin";
	private String direct_user_name_email = prefix_direct + "_admin" + postfix_email;
	private String direct_user_first_name = direct_user_name + "_first_name";
	private String direct_user_last_name = direct_user_name + "_last_name";
	private String site_version="1.0.0";
	private String gateway_hostname="prasad";

	//Related Information For MSP
	private String prefix_msp = "spog_prasad_msp";
	private String msp_org_name = prefix_msp + "_org";
	private String msp_org_email = msp_org_name + postfix_email;
	private String msp_org_first_name = msp_org_name + "_first_name";
	private String msp_org_last_name = msp_org_name + "_last_name";
	private String msp_user_name = prefix_msp + "_admin";
	private String msp_user_name_email = prefix_msp + "_admin" + postfix_email;
	private String msp_user_first_name = msp_user_name + "_first_name";
	private String msp_user_last_name = msp_user_name + "_last_name";
	
	private String org_model_prefix = this.getClass().getSimpleName();
	//used for test case count like passed,failed,remaining cases
	//private SQLServerDb bqdb1;
	//public int Nooftest;
	int passedcases=0;
	int failedcases=0;
	int skippedcases=0;
	//long creationTime;
	String buildnumber=null;
	//String BQame=null;
	//private testcasescount count1;
	//private String running_Machine;

	@BeforeClass
	@Parameters({ "baseURI", "port","csrAdminUserName", "csrAdminPassword","csrReadOnlyUser","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword, String csrReadOnlyUser, String logFolder,String runningMachine,String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		rep = ExtentManager.getInstance("GetsiteconfigurationTest", logFolder);
		gatewayServer =new GatewayServer(baseURI,port);
		String author = "Prasad.Deverakonda";
		Nooftest=0;
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		this.csrAdminUserName = adminUserName;
		this.csrAdminPassword = adminPassword;
		this.csr_readonly_email = csrReadOnlyUser;
		
		//running_Machine=runningMachine;
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date=new java.util.Date();
		buildnumber=buildVersion+"_"+dateFormater.format(date);

		this.BQName=this.getClass().getSimpleName();
		if(count1.isstarttimehit()==0) {
			//System.out.println("Into get loggedInUserById");
			creationTime=System.currentTimeMillis();
			count1.setcreationtime(creationTime);
			//creationTime = System.currentTimeMillis();
			try {
				bqdb1.updateTable(BQName, this.runningMachine, this.buildnumber, String.valueOf(Nooftest), "0", "0", String.valueOf(Nooftest), count1.getcreationtime(), "InProgress",author+" and Rest server is "+baseURI.split("//")[1]);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	@DataProvider(name = "organizationAndUserInfo")
	public final Object[][] getOrganizationAndUserInfo() {
		return new Object[][] { { direct_org_name, SpogConstants.DIRECT_ORG, direct_org_email, common_password,direct_org_first_name, direct_org_last_name, 
			direct_user_name_email, common_password,direct_user_first_name, direct_user_last_name, SpogConstants.DIRECT_ADMIN},  
			{msp_org_name, SpogConstants.MSP_ORG, msp_org_email, common_password,msp_org_first_name, msp_org_last_name, 
				msp_user_name_email, common_password,msp_user_first_name, msp_user_last_name, SpogConstants.MSP_ADMIN } };
	}
	@Test (dataProvider="organizationAndUserInfo")	
	public void getsiteconfiguration_direct_msp_Organization(String organizationName,
			String organizationType,
			String organizationEmail,
			String organizationPwd ,
			String organizationFirstName,
			String organizationLastName,
			String userEmail,
			String userPassword,
			String FirstName,
			String LastName,
			String Role_Id){
		String site_registration_key=null;
		test = rep.startTest("getsiteconfiguration_direct_msp_Organization");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_"+organizationName;
		organizationEmail=RandomStringUtils.randomAlphanumeric(8)+"_"+organizationEmail;
		organizationFirstName=RandomStringUtils.randomAlphanumeric(8) +"_"+organizationFirstName;
		organizationLastName=RandomStringUtils.randomAlphanumeric(8) +"_"+organizationLastName;
		FirstName=RandomStringUtils.randomAlphanumeric(8) +"_"+FirstName;
		LastName=RandomStringUtils.randomAlphanumeric(8) +"_"+LastName;
		userEmail=RandomStringUtils.randomAlphanumeric(8)+"_"+userEmail;

		test.log(LogStatus.INFO,"Login with csrAdmin credentials");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword,test);
		test.log(LogStatus.INFO,"Get the JWT Token to create Oganization:");
		String CSR_token= spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The Token is :"+ CSR_token);
		test.log(LogStatus.INFO, "Creating a orgnization of type "+organizationType);
		String organization_id = spogServer.CreateOrganizationWithCheck(organizationName+org_model_prefix, organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName);
		test.log(LogStatus.INFO, "The organization id is "+organization_id);
		test.log(LogStatus.INFO,"Create user for the Organization "+organization_id);
		Response response = spogServer.createUser( userEmail,userPassword,FirstName,LastName,Role_Id,organization_id,test);

		String user_id =spogServer.checkCreateUser(response,SpogConstants.SUCCESS_POST,userEmail,FirstName,LastName,Role_Id,organization_id,"",test);
		test.log(LogStatus.INFO, "The userid is "+user_id);
		test.log(LogStatus.INFO,"Login with the user "+userEmail);
		spogServer.userLogin(userEmail,userPassword);
		test.log(LogStatus.INFO,"Getting the JWTToken for the Logged in user");
		String validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The token is :"+ validToken );
		String siteName= spogServer.getRandomSiteName("TestCreate");
		test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
		String sitetype=siteType.gateway.toString();
		test.log(LogStatus.INFO,"The siteType :"+sitetype);
		test.log(LogStatus.INFO,"Creating a site For Logged in user");
		response = spogServer.createSite(siteName,sitetype, organization_id, validToken,test);
		Map<String,String > sitecreateResMap=new HashMap<>();
		test.log(LogStatus.INFO, "Check the created site");
		sitecreateResMap=spogServer.checkCreateSite(response, SpogConstants.SUCCESS_POST,siteName, sitetype,
				organization_id,user_id,"",test);
		String registration_basecode=sitecreateResMap.get("registration_basecode");
		String site_id=sitecreateResMap.get("site_id");
		try {

			String decoded = URLDecoder.decode( registration_basecode.trim(), "UTF-8" );
			Base64Coder base64 = new Base64Coder();
			String decrypted = base64.decode( decoded );
			String[] parts    = decrypted.split( "\\n", -2 );
			site_registration_key=parts[1];
			//test.log(LogStatus.INFO, "Decoded base64code is : "+parts[1]);   

		} catch(UnsupportedEncodingException e ){
			test.log(LogStatus.FAIL,"The value of the error Message :"+e.getMessage());
		}
		test.log(LogStatus.INFO,"After decoding the site_registration_key is: "+site_registration_key);
		String gateway_id = UUID.randomUUID().toString();
		test.log(LogStatus.INFO,"Randomly generated gateway_id is: "+gateway_id);
		gateway_hostname=RandomStringUtils.randomAlphanumeric(4)+"_"+gateway_hostname;
		test.log(LogStatus.INFO, "Randomly generated gateway_hostname is: "+gateway_hostname);
		boolean is_registered=true;
		test.log(LogStatus.INFO, "Registering the gateway to site");
		response=gatewayServer.RegisterSite(site_registration_key,gateway_id,gateway_hostname,site_version,site_id,test);
		String site_secret=gatewayServer.checkRegisterSite(response,ServerResponseCode.Succes_Login, site_id,siteName,sitetype,organization_id, user_id,is_registered,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
		test.log(LogStatus.INFO,"The secret key is :"+site_secret);
		response = gatewayServer.LoginSite(site_id, site_secret,gateway_id, test);
		test.log(LogStatus.INFO, "validate the login site response ");
		gatewayServer.checksiteLogin(response, ServerResponseCode.Succes_Login, "", test);
		String valid_token=gatewayServer.getJWTToken();
		test.log(LogStatus.INFO, "Get the site token "+valid_token);
		response = gatewayServer.getSiteConfiguration(site_id, valid_token, test);
		test.log(LogStatus.INFO, "csr_readonly user login");
		spogServer.userLogin(csr_readonly_email, common_password);
		csr_readonly_token = spogServer.getJWTToken();
		response = gatewayServer.getSiteConfiguration(site_id, csr_readonly_token, test);
		String site_config = gatewayServer.checkSiteConfiguration(response,site_id, ServerResponseCode.Success_Get,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
		test.log(LogStatus.INFO, "Performing the operations to delete the user and orginzation by loggin in as csr admin");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		spogServer.DeleteUserById(user_id, test);
		spogServer.DeleteOrganizationWithCheck(organization_id, test);

	}

	@Test (dataProvider="organizationAndUserInfo")	
	public void getsiteconfiguration_direct_msp_Organization_csr_admin(String organizationName,
			String organizationType,
			String organizationEmail,
			String organizationPwd ,
			String organizationFirstName,
			String organizationLastName,
			String userEmail,
			String userPassword,
			String FirstName,
			String LastName,
			String Role_Id){
		String site_registration_key=null;
		test = rep.startTest("getsiteconfiguration_direct_msp_Organization_csr_admin");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_"+organizationName;
		organizationEmail=RandomStringUtils.randomAlphanumeric(8)+"_"+organizationEmail;
		organizationFirstName=RandomStringUtils.randomAlphanumeric(8) +"_"+organizationFirstName;
		organizationLastName=RandomStringUtils.randomAlphanumeric(8) +"_"+organizationLastName;
		FirstName=RandomStringUtils.randomAlphanumeric(8) +"_"+FirstName;
		LastName=RandomStringUtils.randomAlphanumeric(8) +"_"+LastName;
		userEmail=RandomStringUtils.randomAlphanumeric(8)+"_"+userEmail;

		test.log(LogStatus.INFO,"Login with csrAdmin credentials");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword,test);
		test.log(LogStatus.INFO,"Get the JWT Token to create Oganization:");
		String CSR_token= spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The Token is :"+ CSR_token);
		test.log(LogStatus.INFO, "Creating a orgnization of type "+organizationType);
		String organization_id = spogServer.CreateOrganizationWithCheck(organizationName+org_model_prefix, organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName);
		test.log(LogStatus.INFO, "The organization id is "+organization_id);
		test.log(LogStatus.INFO,"Create user for the Organization "+organization_id);
		Response response = spogServer.createUser( userEmail,userPassword,FirstName,LastName,Role_Id,organization_id,test);

		String user_id =spogServer.checkCreateUser(response,SpogConstants.SUCCESS_POST,userEmail,FirstName,LastName,Role_Id,organization_id,"",test);
		test.log(LogStatus.INFO, "The userid is "+user_id);
		test.log(LogStatus.INFO,"Login as csr admin user "+csrAdminUserName);
		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		test.log(LogStatus.INFO,"Getting the JWTToken for the Logged in csr admin user");
		String validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The token is :"+ validToken );
		String siteName= spogServer.getRandomSiteName("TestCreate");
		test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
		String sitetype=siteType.gateway.toString();
		test.log(LogStatus.INFO,"The siteType :"+sitetype);
		test.log(LogStatus.INFO,"Creating a site by loggin in as csr admin");

		response = spogServer.createSite(siteName,sitetype, organization_id, validToken,test);
		Map<String,String > sitecreateResMap=new HashMap<>();
		String userid_loggedin=spogServer.GetLoggedinUser_UserID();
		sitecreateResMap=spogServer.checkCreateSite(response, SpogConstants.SUCCESS_POST,siteName, sitetype,
				organization_id,userid_loggedin,"",test);
		String registration_basecode=sitecreateResMap.get("registration_basecode");
		String site_id=sitecreateResMap.get("site_id");
		try {

			String decoded = URLDecoder.decode( registration_basecode.trim(), "UTF-8" );
			Base64Coder base64 = new Base64Coder();
			String decrypted = base64.decode( decoded );
			String[] parts    = decrypted.split( "\\n", -2 );
			site_registration_key=parts[1];
			//test.log(LogStatus.INFO, "Decoded base64code is : "+parts[1]);   

		} catch(UnsupportedEncodingException e ){
			test.log(LogStatus.FAIL,"The value of the error Message :"+e.getMessage());
		}
		test.log(LogStatus.INFO,"After decoding the site_registration_key is: "+site_registration_key);
		String gateway_id = UUID.randomUUID().toString();
		test.log(LogStatus.INFO,"Randomly generated gateway_id is: "+gateway_id);
		gateway_hostname=RandomStringUtils.randomAlphanumeric(4)+"_"+gateway_hostname;
		test.log(LogStatus.INFO, "Randomly generated gateway_hostname is: "+gateway_hostname);
		boolean is_registered=true;
		test.log(LogStatus.INFO, "Registering the gateway to site");
		response=gatewayServer.RegisterSite(site_registration_key,gateway_id,gateway_hostname,site_version,site_id,test);
		String site_secret=gatewayServer.checkRegisterSite(response,ServerResponseCode.Succes_Login, site_id,siteName,sitetype,organization_id, userid_loggedin,is_registered,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
		test.log(LogStatus.INFO,"The secret key is :"+site_secret);
		response = gatewayServer.LoginSite(site_id, site_secret,gateway_id, test);
		test.log(LogStatus.INFO, "validate the login site response ");
		gatewayServer.checksiteLogin(response, ServerResponseCode.Succes_Login, "", test);
		String valid_token=gatewayServer.getJWTToken();
		test.log(LogStatus.INFO, "Get the site token "+valid_token);
		response = gatewayServer.getSiteConfiguration(site_id, valid_token, test);
		String site_config = gatewayServer.checkSiteConfiguration(response, site_id,ServerResponseCode.Success_Get,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
		test.log(LogStatus.INFO, "Performing the operations to delete the user and orginzation by loggin in as csr admin");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		spogServer.DeleteUserById(user_id, test);
		spogServer.DeleteOrganizationWithCheck(organization_id, test);

	}

	@Test (dataProvider="organizationAndUserInfo")	
	public void getsiteconfiguration_direct_msp_Organization_invalidJWT(String organizationName,
			String organizationType,
			String organizationEmail,
			String organizationPwd ,
			String organizationFirstName,
			String organizationLastName,
			String userEmail,
			String userPassword,
			String FirstName,
			String LastName,
			String Role_Id){
		String site_registration_key=null;
		test = rep.startTest("getsiteconfiguration_direct_msp_Organization_invalidJWT");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_"+organizationName;
		organizationEmail=RandomStringUtils.randomAlphanumeric(8)+"_"+organizationEmail;
		organizationFirstName=RandomStringUtils.randomAlphanumeric(8) +"_"+organizationFirstName;
		organizationLastName=RandomStringUtils.randomAlphanumeric(8) +"_"+organizationLastName;
		FirstName=RandomStringUtils.randomAlphanumeric(8) +"_"+FirstName;
		LastName=RandomStringUtils.randomAlphanumeric(8) +"_"+LastName;
		userEmail=RandomStringUtils.randomAlphanumeric(8)+"_"+userEmail;

		test.log(LogStatus.INFO,"Login with csrAdmin credentials");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword,test);
		test.log(LogStatus.INFO,"Get the JWT Token to create Oganization:");
		String CSR_token= spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The Token is :"+ CSR_token);
		test.log(LogStatus.INFO, "Creating a orgnization of type "+organizationType);
		//String organization_id = spogServer.CreateOrganizationWithCheck(organizationName, organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,test);
		String organization_id = spogServer.CreateOrganizationWithCheck(organizationName+org_model_prefix, organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName);
		test.log(LogStatus.INFO, "The organization id is "+organization_id);
		test.log(LogStatus.INFO,"Create user for the Organization "+organization_id);
		Response response = spogServer.createUser( userEmail,userPassword,FirstName,LastName,Role_Id,organization_id,test);

		String user_id =spogServer.checkCreateUser(response,SpogConstants.SUCCESS_POST,userEmail,FirstName,LastName,Role_Id,organization_id,"",test);
		test.log(LogStatus.INFO, "The userid is "+user_id);
		test.log(LogStatus.INFO,"Login with the user "+userEmail);
		spogServer.userLogin(userEmail,userPassword);
		test.log(LogStatus.INFO,"Getting the JWTToken for the Logged in user");
		String validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The token is :"+ validToken );
		String siteName= spogServer.getRandomSiteName("TestCreate");
		test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
		String sitetype=siteType.gateway.toString();
		test.log(LogStatus.INFO,"The siteType :"+sitetype);
		test.log(LogStatus.INFO,"Creating a site For Logged in user");
		response = spogServer.createSite(siteName,sitetype, organization_id, validToken,test);
		Map<String,String > sitecreateResMap=new HashMap<>();
		sitecreateResMap=spogServer.checkCreateSite(response, SpogConstants.SUCCESS_POST,siteName, sitetype,
				organization_id,user_id,"",test);
		String registration_basecode=sitecreateResMap.get("registration_basecode");
		String site_id=sitecreateResMap.get("site_id");
		try {

			String decoded = URLDecoder.decode( registration_basecode.trim(), "UTF-8" );
			Base64Coder base64 = new Base64Coder();
			String decrypted = base64.decode( decoded );
			String[] parts    = decrypted.split( "\\n", -2 );
			site_registration_key=parts[1];
			//test.log(LogStatus.INFO, "Decoded base64code is : "+parts[1]);   

		} catch(UnsupportedEncodingException e ){
			test.log(LogStatus.FAIL,"The value of the error Message :"+e.getMessage());
		}
		test.log(LogStatus.INFO,"After decoding the site_registration_key is: "+site_registration_key);
		String gateway_id = UUID.randomUUID().toString();
		test.log(LogStatus.INFO,"Randomly generated gateway_id is: "+gateway_id);
		gateway_hostname=RandomStringUtils.randomAlphanumeric(4)+"_"+gateway_hostname;
		test.log(LogStatus.INFO, "Randomly generated gateway_hostname is: "+gateway_hostname);
		boolean is_registered=true;
		test.log(LogStatus.INFO, "Registering the gateway to site");
		response=gatewayServer.RegisterSite(site_registration_key,gateway_id,gateway_hostname,site_version,site_id,test);
		String site_secret=gatewayServer.checkRegisterSite(response,ServerResponseCode.Succes_Login, site_id,siteName,sitetype,organization_id, user_id,is_registered,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
		test.log(LogStatus.INFO,"The secret key is :"+site_secret);
		response = gatewayServer.LoginSite(site_id, site_secret,gateway_id, test);
		test.log(LogStatus.INFO, "validate the login site response ");
		gatewayServer.checksiteLogin(response, ServerResponseCode.Succes_Login, "", test);
		String Invalid_token=gatewayServer.getJWTToken()+"JUNK";
		test.log(LogStatus.INFO, "Get the site token "+Invalid_token);
		response = gatewayServer.getSiteConfiguration(site_id, Invalid_token, test);
		String site_config = gatewayServer.checkSiteConfiguration(response, site_id,ServerResponseCode.Not_login,SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT,test);
		test.log(LogStatus.INFO, "Performing the operations to delete the user and orginzation by loggin in as csr admin");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		spogServer.DeleteUserById(user_id, test);
		spogServer.DeleteOrganizationWithCheck(organization_id, test);

	}

	@Test (dataProvider="organizationAndUserInfo")	
	public void getsiteconfiguration_direct_msp_Organization_missingJWT(String organizationName,
			String organizationType,
			String organizationEmail,
			String organizationPwd ,
			String organizationFirstName,
			String organizationLastName,
			String userEmail,
			String userPassword,
			String FirstName,
			String LastName,
			String Role_Id){
		String site_registration_key=null;
		test = rep.startTest("getsiteconfiguration_direct_msp_Organization_missingJWT");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_"+organizationName;
		organizationEmail=RandomStringUtils.randomAlphanumeric(8)+"_"+organizationEmail;
		organizationFirstName=RandomStringUtils.randomAlphanumeric(8) +"_"+organizationFirstName;
		organizationLastName=RandomStringUtils.randomAlphanumeric(8) +"_"+organizationLastName;
		FirstName=RandomStringUtils.randomAlphanumeric(8) +"_"+FirstName;
		LastName=RandomStringUtils.randomAlphanumeric(8) +"_"+LastName;
		userEmail=RandomStringUtils.randomAlphanumeric(8)+"_"+userEmail;

		test.log(LogStatus.INFO,"Login with csrAdmin credentials");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword,test);
		test.log(LogStatus.INFO,"Get the JWT Token to create Oganization:");
		String CSR_token= spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The Token is :"+ CSR_token);
		test.log(LogStatus.INFO, "Creating a orgnization of type "+organizationType);
		//String organization_id = spogServer.CreateOrganizationWithCheck(organizationName, organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,test);
		String organization_id = spogServer.CreateOrganizationWithCheck(organizationName+org_model_prefix, organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName);
		test.log(LogStatus.INFO, "The organization id is "+organization_id);
		test.log(LogStatus.INFO,"Create user for the Organization "+organization_id);
		Response response = spogServer.createUser( userEmail,userPassword,FirstName,LastName,Role_Id,organization_id,test);

		String user_id =spogServer.checkCreateUser(response,SpogConstants.SUCCESS_POST,userEmail,FirstName,LastName,Role_Id,organization_id,"",test);
		test.log(LogStatus.INFO, "The userid is "+user_id);
		test.log(LogStatus.INFO,"Login with the user "+userEmail);
		spogServer.userLogin(userEmail,userPassword);
		test.log(LogStatus.INFO,"Getting the JWTToken for the Logged in user");
		String validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The token is :"+ validToken );
		String siteName= spogServer.getRandomSiteName("TestCreate");
		test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
		String sitetype=siteType.gateway.toString();
		test.log(LogStatus.INFO,"The siteType :"+sitetype);
		test.log(LogStatus.INFO,"Creating a site For Logged in user");
		response = spogServer.createSite(siteName,sitetype, organization_id, validToken,test);
		Map<String,String > sitecreateResMap=new HashMap<>();
		sitecreateResMap=spogServer.checkCreateSite(response, SpogConstants.SUCCESS_POST,siteName, sitetype,
				organization_id,user_id,"",test);
		String registration_basecode=sitecreateResMap.get("registration_basecode");
		String site_id=sitecreateResMap.get("site_id");
		try {

			String decoded = URLDecoder.decode( registration_basecode.trim(), "UTF-8" );
			Base64Coder base64 = new Base64Coder();
			String decrypted = base64.decode( decoded );
			String[] parts    = decrypted.split( "\\n", -2 );
			site_registration_key=parts[1];
			//test.log(LogStatus.INFO, "Decoded base64code is : "+parts[1]);   

		} catch(UnsupportedEncodingException e ){
			test.log(LogStatus.FAIL,"The value of the error Message :"+e.getMessage());
		}
		test.log(LogStatus.INFO,"After decoding the site_registration_key is: "+site_registration_key);
		String gateway_id = UUID.randomUUID().toString();
		test.log(LogStatus.INFO,"Randomly generated gateway_id is: "+gateway_id);
		gateway_hostname=RandomStringUtils.randomAlphanumeric(4)+"_"+gateway_hostname;
		test.log(LogStatus.INFO, "Randomly generated gateway_hostname is: "+gateway_hostname);
		boolean is_registered=true;
		test.log(LogStatus.INFO, "Registering the gateway to site");
		response=gatewayServer.RegisterSite(site_registration_key,gateway_id,gateway_hostname,site_version,site_id,test);
		String site_secret=gatewayServer.checkRegisterSite(response,ServerResponseCode.Succes_Login, site_id,siteName,sitetype,organization_id, user_id,is_registered,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
		test.log(LogStatus.INFO,"The secret key is :"+site_secret);
		response = gatewayServer.LoginSite(site_id, site_secret,gateway_id, test);
		test.log(LogStatus.INFO, "validate the login site response ");
		gatewayServer.checksiteLogin(response, ServerResponseCode.Succes_Login, "", test);
		String Missing_token="";
		test.log(LogStatus.INFO, "Get the site token "+Missing_token);
		response = gatewayServer.getSiteConfiguration(site_id, Missing_token, test);
		String site_config = gatewayServer.checkSiteConfiguration(response, site_id,ServerResponseCode.Not_login,SpogMessageCode.COMMON_AUTHENTICATION_FAILED,test);
		test.log(LogStatus.INFO, "Performing the operations to delete the user and orginzation by loggin in as csr admin");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		spogServer.DeleteUserById(user_id, test);
		spogServer.DeleteOrganizationWithCheck(organization_id, test);
	}


	@Test (dataProvider="organizationAndUserInfo")	
	public void getsiteconfiguration_direct_msp_Organization_invalidsiteid(String organizationName,
			String organizationType,
			String organizationEmail,
			String organizationPwd ,
			String organizationFirstName,
			String organizationLastName,
			String userEmail,
			String userPassword,
			String FirstName,
			String LastName,
			String Role_Id){
		String site_registration_key=null;
		test = rep.startTest("getsiteconfiguration_direct_msp_Organization_invalidsiteid");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_"+organizationName;
		organizationEmail=RandomStringUtils.randomAlphanumeric(8)+"_"+organizationEmail;
		organizationFirstName=RandomStringUtils.randomAlphanumeric(8) +"_"+organizationFirstName;
		organizationLastName=RandomStringUtils.randomAlphanumeric(8) +"_"+organizationLastName;
		FirstName=RandomStringUtils.randomAlphanumeric(8) +"_"+FirstName;
		LastName=RandomStringUtils.randomAlphanumeric(8) +"_"+LastName;
		userEmail=RandomStringUtils.randomAlphanumeric(8)+"_"+userEmail;

		test.log(LogStatus.INFO,"Login with csrAdmin credentials");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword,test);
		test.log(LogStatus.INFO,"Get the JWT Token to create Oganization:");
		String CSR_token= spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The Token is :"+ CSR_token);
		test.log(LogStatus.INFO, "Creating a orgnization of type "+organizationType);
		//String organization_id = spogServer.CreateOrganizationWithCheck(organizationName, organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,test);
		String organization_id = spogServer.CreateOrganizationWithCheck(organizationName+org_model_prefix, organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName);
		test.log(LogStatus.INFO, "The organization id is "+organization_id);
		test.log(LogStatus.INFO,"Create user for the Organization "+organization_id);
		Response response = spogServer.createUser( userEmail,userPassword,FirstName,LastName,Role_Id,organization_id,test);

		String user_id =spogServer.checkCreateUser(response,SpogConstants.SUCCESS_POST,userEmail,FirstName,LastName,Role_Id,organization_id,"",test);
		test.log(LogStatus.INFO, "The userid is "+user_id);
		test.log(LogStatus.INFO,"Login with the user "+userEmail);
		spogServer.userLogin(userEmail,userPassword);
		test.log(LogStatus.INFO,"Getting the JWTToken for the Logged in user");
		String validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The token is :"+ validToken );
		String siteName= spogServer.getRandomSiteName("TestCreate");
		test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
		String sitetype=siteType.gateway.toString();
		test.log(LogStatus.INFO,"The siteType :"+sitetype);
		test.log(LogStatus.INFO,"Creating a site For Logged in user");
		response = spogServer.createSite(siteName,sitetype, organization_id, validToken,test);
		Map<String,String > sitecreateResMap=new HashMap<>();
		sitecreateResMap=spogServer.checkCreateSite(response, SpogConstants.SUCCESS_POST,siteName, sitetype,
				organization_id,user_id,"",test);
		String registration_basecode=sitecreateResMap.get("registration_basecode");
		String site_id=sitecreateResMap.get("site_id");
		try {

			String decoded = URLDecoder.decode( registration_basecode.trim(), "UTF-8" );
			Base64Coder base64 = new Base64Coder();
			String decrypted = base64.decode( decoded );
			String[] parts    = decrypted.split( "\\n", -2 );
			site_registration_key=parts[1];
			//test.log(LogStatus.INFO, "Decoded base64code is : "+parts[1]);   

		} catch(UnsupportedEncodingException e ){
			test.log(LogStatus.FAIL,"The value of the error Message :"+e.getMessage());
		}
		test.log(LogStatus.INFO,"After decoding the site_registration_key is: "+site_registration_key);
		String gateway_id = UUID.randomUUID().toString();
		test.log(LogStatus.INFO,"Randomly generated gateway_id is: "+gateway_id);
		gateway_hostname=RandomStringUtils.randomAlphanumeric(4)+"_"+gateway_hostname;
		test.log(LogStatus.INFO, "Randomly generated gateway_hostname is: "+gateway_hostname);
		boolean is_registered=true;
		test.log(LogStatus.INFO, "Registering the gateway to site");
		response=gatewayServer.RegisterSite(site_registration_key,gateway_id,gateway_hostname,site_version,site_id,test);
		String site_secret=gatewayServer.checkRegisterSite(response,ServerResponseCode.Succes_Login, site_id,siteName,sitetype,organization_id, user_id,is_registered,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
		test.log(LogStatus.INFO,"The secret key is :"+site_secret);
		response = gatewayServer.LoginSite(site_id, site_secret,gateway_id, test);
		test.log(LogStatus.INFO, "validate the login site response ");
		gatewayServer.checksiteLogin(response, ServerResponseCode.Succes_Login, "", test);
		String valid_token=gatewayServer.getJWTToken();
		test.log(LogStatus.INFO, "Get the site token "+valid_token);
		String invalid_site_id=UUID.randomUUID().toString();
		response = gatewayServer.getSiteConfiguration(invalid_site_id, valid_token, test);
		String site_config = gatewayServer.checkSiteConfiguration(response, invalid_site_id,ServerResponseCode.Resource_Does_Not_Exist,SpogMessageCode.SITE_NOT_FOUND,test);
		test.log(LogStatus.INFO, "Performing the operations to delete the user and orginzation by loggin in as csr admin");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		spogServer.DeleteUserById(user_id, test);
		spogServer.DeleteOrganizationWithCheck(organization_id, test);
	}


	@DataProvider(name = "suborganizationAndUserInfo")
	public final Object[][] getsubOrganizationAndUserInfo() {
		return new Object[][] {{msp_org_name, SpogConstants.MSP_ORG, msp_org_email, common_password,msp_org_first_name, msp_org_last_name, 
			msp_user_name_email, common_password,msp_user_first_name, msp_user_last_name, SpogConstants.MSP_ADMIN } };
	}
	@Test (dataProvider="suborganizationAndUserInfo")	
	public void getsuborgsiteconfiguration_validtoken(String organizationName,
			String organizationType,
			String organizationEmail,
			String organizationPwd ,
			String organizationFirstName,
			String organizationLastName,
			String userEmail,
			String userPassword,
			String FirstName,
			String LastName,
			String Role_Id){
		String site_registration_key=null;
		test = rep.startTest("getsuborgsiteconfiguration_validtoken");
		test.assignAuthor("prasaddeverakonda");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_"+organizationName;
		organizationEmail=RandomStringUtils.randomAlphanumeric(8)+"_"+organizationEmail;
		organizationFirstName=RandomStringUtils.randomAlphanumeric(8) +"_"+organizationFirstName;
		organizationLastName=RandomStringUtils.randomAlphanumeric(8) +"_"+organizationLastName;
		FirstName=RandomStringUtils.randomAlphanumeric(8) +"_"+FirstName;
		LastName=RandomStringUtils.randomAlphanumeric(8) +"_"+LastName;
		userEmail=RandomStringUtils.randomAlphanumeric(8)+"_"+userEmail;

		test.log(LogStatus.INFO,"Login with csrAdmin credentials");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword,test);
		test.log(LogStatus.INFO,"Get the JWT Token to create Oganization:");
		String CSR_token= spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The Token is :"+ CSR_token);
		test.log(LogStatus.INFO, "Creating a orgnization of type "+organizationType);
		//String organization_id = spogServer.CreateOrganizationWithCheck(organizationName, organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,test);
		String organization_id = spogServer.CreateOrganizationWithCheck(organizationName+org_model_prefix, organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName);
		test.log(LogStatus.INFO, "The organization id is "+organization_id);

		test.log(LogStatus.INFO, "Create a sub org under organization "+organization_id);
		String sub_org_Id = spogServer.createAccountWithCheck(organization_id, spogServer.ReturnRandom("spogqa_account"), organization_id, test);

		test.log(LogStatus.INFO,"Create user for the sub Organization ");
		Response response = spogServer.createUser( userEmail,userPassword,FirstName,LastName,SpogConstants.DIRECT_ADMIN,sub_org_Id,test);

		String user_id =spogServer.checkCreateUser(response,SpogConstants.SUCCESS_POST,userEmail,FirstName,LastName,SpogConstants.DIRECT_ADMIN,sub_org_Id,"",test);
		test.log(LogStatus.INFO, "The sub org userid is "+user_id);
		test.log(LogStatus.INFO,"Login with the user "+userEmail);
		spogServer.userLogin(userEmail,userPassword);
		test.log(LogStatus.INFO,"Getting the JWTToken for the Logged in user");
		String validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The token is :"+ validToken );
		String siteName= spogServer.getRandomSiteName("TestCreate");
		test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
		String sitetype=siteType.gateway.toString();
		test.log(LogStatus.INFO,"The siteType :"+sitetype);
		test.log(LogStatus.INFO,"Creating a site For Logged in user");
		response = spogServer.createSite(siteName,sitetype, sub_org_Id, validToken,test);
		Map<String,String > sitecreateResMap=new HashMap<>();
		test.log(LogStatus.INFO, "Check the created site");
		sitecreateResMap=spogServer.checkCreateSite(response, SpogConstants.SUCCESS_POST,siteName, sitetype,
				sub_org_Id,user_id,"",test);
		String registration_basecode=sitecreateResMap.get("registration_basecode");
		String site_id=sitecreateResMap.get("site_id");
		try {

			String decoded = URLDecoder.decode( registration_basecode.trim(), "UTF-8" );
			Base64Coder base64 = new Base64Coder();
			String decrypted = base64.decode( decoded );
			String[] parts    = decrypted.split( "\\n", -2 );
			site_registration_key=parts[1];
			//test.log(LogStatus.INFO, "Decoded base64code is : "+parts[1]);   

		} catch(UnsupportedEncodingException e ){
			test.log(LogStatus.FAIL,"The value of the error Message :"+e.getMessage());
		}
		test.log(LogStatus.INFO,"After decoding the site_registration_key is: "+site_registration_key);
		String gateway_id = UUID.randomUUID().toString();
		test.log(LogStatus.INFO,"Randomly generated gateway_id is: "+gateway_id);
		gateway_hostname=RandomStringUtils.randomAlphanumeric(4)+"_"+gateway_hostname;
		test.log(LogStatus.INFO, "Randomly generated gateway_hostname is: "+gateway_hostname);
		boolean is_registered=true;
		test.log(LogStatus.INFO, "Registering the gateway to site");
		response=gatewayServer.RegisterSite(site_registration_key,gateway_id,gateway_hostname,site_version,site_id,test);
		String site_secret=gatewayServer.checkRegisterSite(response,ServerResponseCode.Succes_Login, site_id,siteName,sitetype,sub_org_Id, user_id,is_registered,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
		test.log(LogStatus.INFO,"The secret key is :"+site_secret);
		response = gatewayServer.LoginSite(site_id, site_secret,gateway_id, test);
		test.log(LogStatus.INFO, "validate the login site response ");
		gatewayServer.checksiteLogin(response, ServerResponseCode.Succes_Login, "", test);
		String valid_token=gatewayServer.getJWTToken();
		test.log(LogStatus.INFO, "Get the site token "+valid_token);
		response = gatewayServer.getSiteConfiguration(site_id, valid_token, test);
		String site_config = gatewayServer.checkSiteConfiguration(response, site_id,ServerResponseCode.Success_Get,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
		test.log(LogStatus.INFO, "The site configuration details are "+site_config);
		test.log(LogStatus.INFO, "Performing the operations to delete the user and orginzation by loggin in as csr admin");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		spogServer.DeleteUserById(user_id, test);
		spogServer.DeleteOrganizationWithCheck(sub_org_Id, test);
		spogServer.DeleteOrganizationWithCheck(organization_id, test);

	}
	
	@Test (dataProvider="suborganizationAndUserInfo")	
	public void getsuborgsiteconfiguration_Invalidtoken(String organizationName,
			String organizationType,
			String organizationEmail,
			String organizationPwd ,
			String organizationFirstName,
			String organizationLastName,
			String userEmail,
			String userPassword,
			String FirstName,
			String LastName,
			String Role_Id){
		String site_registration_key=null;
		test = rep.startTest("getsuborgsiteconfiguration_Invalidtoken");
		test.assignAuthor("prasaddeverakonda");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_"+organizationName;
		organizationEmail=RandomStringUtils.randomAlphanumeric(8)+"_"+organizationEmail;
		organizationFirstName=RandomStringUtils.randomAlphanumeric(8) +"_"+organizationFirstName;
		organizationLastName=RandomStringUtils.randomAlphanumeric(8) +"_"+organizationLastName;
		FirstName=RandomStringUtils.randomAlphanumeric(8) +"_"+FirstName;
		LastName=RandomStringUtils.randomAlphanumeric(8) +"_"+LastName;
		userEmail=RandomStringUtils.randomAlphanumeric(8)+"_"+userEmail;

		test.log(LogStatus.INFO,"Login with csrAdmin credentials");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword,test);
		test.log(LogStatus.INFO,"Get the JWT Token to create Oganization:");
		String CSR_token= spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The Token is :"+ CSR_token);
		test.log(LogStatus.INFO, "Creating a orgnization of type "+organizationType);
		//String organization_id = spogServer.CreateOrganizationWithCheck(organizationName, organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,test);
		String organization_id = spogServer.CreateOrganizationWithCheck(organizationName+org_model_prefix, organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName);
		test.log(LogStatus.INFO, "The organization id is "+organization_id);

		test.log(LogStatus.INFO, "Create a sub org under organization "+organization_id);
		String sub_org_Id = spogServer.createAccountWithCheck(organization_id, spogServer.ReturnRandom("spogqa_account"), organization_id, test);

		test.log(LogStatus.INFO,"Create user for the sub Organization ");
		Response response = spogServer.createUser( userEmail,userPassword,FirstName,LastName,SpogConstants.DIRECT_ADMIN,sub_org_Id,test);

		String user_id =spogServer.checkCreateUser(response,SpogConstants.SUCCESS_POST,userEmail,FirstName,LastName,SpogConstants.DIRECT_ADMIN,sub_org_Id,"",test);
		test.log(LogStatus.INFO, "The sub org userid is "+user_id);
		test.log(LogStatus.INFO,"Login with the user "+userEmail);
		spogServer.userLogin(userEmail,userPassword);
		test.log(LogStatus.INFO,"Getting the JWTToken for the Logged in user");
		String validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The token is :"+ validToken );
		String siteName= spogServer.getRandomSiteName("TestCreate");
		test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
		String sitetype=siteType.gateway.toString();
		test.log(LogStatus.INFO,"The siteType :"+sitetype);
		test.log(LogStatus.INFO,"Creating a site For Logged in user");
		response = spogServer.createSite(siteName,sitetype, sub_org_Id, validToken,test);
		Map<String,String > sitecreateResMap=new HashMap<>();
		test.log(LogStatus.INFO, "Check the created site");
		sitecreateResMap=spogServer.checkCreateSite(response, SpogConstants.SUCCESS_POST,siteName, sitetype,
				sub_org_Id,user_id,"",test);
		String registration_basecode=sitecreateResMap.get("registration_basecode");
		String site_id=sitecreateResMap.get("site_id");
		try {

			String decoded = URLDecoder.decode( registration_basecode.trim(), "UTF-8" );
			Base64Coder base64 = new Base64Coder();
			String decrypted = base64.decode( decoded );
			String[] parts    = decrypted.split( "\\n", -2 );
			site_registration_key=parts[1];
			//test.log(LogStatus.INFO, "Decoded base64code is : "+parts[1]);   

		} catch(UnsupportedEncodingException e ){
			test.log(LogStatus.FAIL,"The value of the error Message :"+e.getMessage());
		}
		test.log(LogStatus.INFO,"After decoding the site_registration_key is: "+site_registration_key);
		String gateway_id = UUID.randomUUID().toString();
		test.log(LogStatus.INFO,"Randomly generated gateway_id is: "+gateway_id);
		gateway_hostname=RandomStringUtils.randomAlphanumeric(4)+"_"+gateway_hostname;
		test.log(LogStatus.INFO, "Randomly generated gateway_hostname is: "+gateway_hostname);
		boolean is_registered=true;
		test.log(LogStatus.INFO, "Registering the gateway to site");
		response=gatewayServer.RegisterSite(site_registration_key,gateway_id,gateway_hostname,site_version,site_id,test);
		String site_secret=gatewayServer.checkRegisterSite(response,ServerResponseCode.Succes_Login, site_id,siteName,sitetype,sub_org_Id, user_id,is_registered,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
		test.log(LogStatus.INFO,"The secret key is :"+site_secret);
		response = gatewayServer.LoginSite(site_id, site_secret,gateway_id, test);
		test.log(LogStatus.INFO, "validate the login site response ");
		gatewayServer.checksiteLogin(response, ServerResponseCode.Succes_Login, "", test);
		String valid_token=gatewayServer.getJWTToken()+"Ju";
		test.log(LogStatus.INFO, "Get the site token "+valid_token);
		response = gatewayServer.getSiteConfiguration(site_id, valid_token, test);
		String site_config = gatewayServer.checkSiteConfiguration(response, site_id,ServerResponseCode.Not_login,SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT,test);
		test.log(LogStatus.INFO, "The site configuration details are "+site_config);
		test.log(LogStatus.INFO, "Performing the operations to delete the user and orginzation by loggin in as csr admin");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		spogServer.DeleteUserById(user_id, test);
		spogServer.DeleteOrganizationWithCheck(sub_org_Id, test);
		spogServer.DeleteOrganizationWithCheck(organization_id, test);

	}
	
	@Test (dataProvider="suborganizationAndUserInfo")	
	public void getsuborgsiteconfiguration_Missingtoken(String organizationName,
			String organizationType,
			String organizationEmail,
			String organizationPwd ,
			String organizationFirstName,
			String organizationLastName,
			String userEmail,
			String userPassword,
			String FirstName,
			String LastName,
			String Role_Id){
		String site_registration_key=null;
		test = rep.startTest("getsuborgsiteconfiguration_Missingtoken");
		test.assignAuthor("prasaddeverakonda");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_"+organizationName;
		organizationEmail=RandomStringUtils.randomAlphanumeric(8)+"_"+organizationEmail;
		organizationFirstName=RandomStringUtils.randomAlphanumeric(8) +"_"+organizationFirstName;
		organizationLastName=RandomStringUtils.randomAlphanumeric(8) +"_"+organizationLastName;
		FirstName=RandomStringUtils.randomAlphanumeric(8) +"_"+FirstName;
		LastName=RandomStringUtils.randomAlphanumeric(8) +"_"+LastName;
		userEmail=RandomStringUtils.randomAlphanumeric(8)+"_"+userEmail;

		test.log(LogStatus.INFO,"Login with csrAdmin credentials");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword,test);
		test.log(LogStatus.INFO,"Get the JWT Token to create Oganization:");
		String CSR_token= spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The Token is :"+ CSR_token);
		test.log(LogStatus.INFO, "Creating a orgnization of type "+organizationType);
		//String organization_id = spogServer.CreateOrganizationWithCheck(organizationName, organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,test);
		String organization_id = spogServer.CreateOrganizationWithCheck(organizationName+org_model_prefix, organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName);
		test.log(LogStatus.INFO, "The organization id is "+organization_id);

		test.log(LogStatus.INFO, "Create a sub org under organization "+organization_id);
		String sub_org_Id = spogServer.createAccountWithCheck(organization_id, spogServer.ReturnRandom("spogqa_account"), organization_id, test);

		test.log(LogStatus.INFO,"Create user for the sub Organization ");
		Response response = spogServer.createUser( userEmail,userPassword,FirstName,LastName,SpogConstants.DIRECT_ADMIN,sub_org_Id,test);

		String user_id =spogServer.checkCreateUser(response,SpogConstants.SUCCESS_POST,userEmail,FirstName,LastName,SpogConstants.DIRECT_ADMIN,sub_org_Id,"",test);
		test.log(LogStatus.INFO, "The sub org userid is "+user_id);
		test.log(LogStatus.INFO,"Login with the user "+userEmail);
		spogServer.userLogin(userEmail,userPassword);
		test.log(LogStatus.INFO,"Getting the JWTToken for the Logged in user");
		String validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The token is :"+ validToken );
		String siteName= spogServer.getRandomSiteName("TestCreate");
		test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
		String sitetype=siteType.gateway.toString();
		test.log(LogStatus.INFO,"The siteType :"+sitetype);
		test.log(LogStatus.INFO,"Creating a site For Logged in user");
		response = spogServer.createSite(siteName,sitetype, sub_org_Id, validToken,test);
		Map<String,String > sitecreateResMap=new HashMap<>();
		test.log(LogStatus.INFO, "Check the created site");
		sitecreateResMap=spogServer.checkCreateSite(response, SpogConstants.SUCCESS_POST,siteName, sitetype,
				sub_org_Id,user_id,"",test);
		String registration_basecode=sitecreateResMap.get("registration_basecode");
		String site_id=sitecreateResMap.get("site_id");
		try {

			String decoded = URLDecoder.decode( registration_basecode.trim(), "UTF-8" );
			Base64Coder base64 = new Base64Coder();
			String decrypted = base64.decode( decoded );
			String[] parts    = decrypted.split( "\\n", -2 );
			site_registration_key=parts[1];
			//test.log(LogStatus.INFO, "Decoded base64code is : "+parts[1]);   

		} catch(UnsupportedEncodingException e ){
			test.log(LogStatus.FAIL,"The value of the error Message :"+e.getMessage());
		}
		test.log(LogStatus.INFO,"After decoding the site_registration_key is: "+site_registration_key);
		String gateway_id = UUID.randomUUID().toString();
		test.log(LogStatus.INFO,"Randomly generated gateway_id is: "+gateway_id);
		gateway_hostname=RandomStringUtils.randomAlphanumeric(4)+"_"+gateway_hostname;
		test.log(LogStatus.INFO, "Randomly generated gateway_hostname is: "+gateway_hostname);
		boolean is_registered=true;
		test.log(LogStatus.INFO, "Registering the gateway to site");
		response=gatewayServer.RegisterSite(site_registration_key,gateway_id,gateway_hostname,site_version,site_id,test);
		String site_secret=gatewayServer.checkRegisterSite(response,ServerResponseCode.Succes_Login, site_id,siteName,sitetype,sub_org_Id, user_id,is_registered,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
		test.log(LogStatus.INFO,"The secret key is :"+site_secret);
		response = gatewayServer.LoginSite(site_id, site_secret,gateway_id, test);
		test.log(LogStatus.INFO, "validate the login site response ");
		gatewayServer.checksiteLogin(response, ServerResponseCode.Succes_Login, "", test);
		String valid_token="";
		test.log(LogStatus.INFO, "Get the site token "+valid_token);
		response = gatewayServer.getSiteConfiguration(site_id, valid_token, test);
		String site_config = gatewayServer.checkSiteConfiguration(response, site_id,ServerResponseCode.Not_login,SpogMessageCode.COMMON_AUTHENTICATION_FAILED,test);
		test.log(LogStatus.INFO, "The site configuration details are "+site_config);
		test.log(LogStatus.INFO, "Performing the operations to delete the user and orginzation by loggin in as csr admin");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		spogServer.DeleteUserById(user_id, test);
		spogServer.DeleteOrganizationWithCheck(sub_org_Id, test);
		spogServer.DeleteOrganizationWithCheck(organization_id, test);

	}

	@Test (dataProvider="suborganizationAndUserInfo")	
	public void getsuborgsiteconfiguration_InvalidsiteID(String organizationName,
			String organizationType,
			String organizationEmail,
			String organizationPwd ,
			String organizationFirstName,
			String organizationLastName,
			String userEmail,
			String userPassword,
			String FirstName,
			String LastName,
			String Role_Id){
		String site_registration_key=null;
		test = rep.startTest("getsuborgsiteconfiguration_InvalidSiteId");
		test.assignAuthor("prasaddeverakonda");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_"+organizationName;
		organizationEmail=RandomStringUtils.randomAlphanumeric(8)+"_"+organizationEmail;
		organizationFirstName=RandomStringUtils.randomAlphanumeric(8) +"_"+organizationFirstName;
		organizationLastName=RandomStringUtils.randomAlphanumeric(8) +"_"+organizationLastName;
		FirstName=RandomStringUtils.randomAlphanumeric(8) +"_"+FirstName;
		LastName=RandomStringUtils.randomAlphanumeric(8) +"_"+LastName;
		userEmail=RandomStringUtils.randomAlphanumeric(8)+"_"+userEmail;

		test.log(LogStatus.INFO,"Login with csrAdmin credentials");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword,test);
		test.log(LogStatus.INFO,"Get the JWT Token to create Oganization:");
		String CSR_token= spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The Token is :"+ CSR_token);
		test.log(LogStatus.INFO, "Creating a orgnization of type "+organizationType);
		//String organization_id = spogServer.CreateOrganizationWithCheck(organizationName, organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,test);
		String organization_id = spogServer.CreateOrganizationWithCheck(organizationName+org_model_prefix, organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName);
		test.log(LogStatus.INFO, "The organization id is "+organization_id);

		test.log(LogStatus.INFO, "Create a sub org under organization "+organization_id);
		String sub_org_Id = spogServer.createAccountWithCheck(organization_id, spogServer.ReturnRandom("spogqa_account"), organization_id, test);

		test.log(LogStatus.INFO,"Create user for the sub Organization ");
		Response response = spogServer.createUser( userEmail,userPassword,FirstName,LastName,SpogConstants.DIRECT_ADMIN,sub_org_Id,test);

		String user_id =spogServer.checkCreateUser(response,SpogConstants.SUCCESS_POST,userEmail,FirstName,LastName,SpogConstants.DIRECT_ADMIN,sub_org_Id,"",test);
		test.log(LogStatus.INFO, "The sub org userid is "+user_id);
		test.log(LogStatus.INFO,"Login with the user "+userEmail);
		spogServer.userLogin(userEmail,userPassword);
		test.log(LogStatus.INFO,"Getting the JWTToken for the Logged in user");
		String validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The token is :"+ validToken );
		String siteName= spogServer.getRandomSiteName("TestCreate");
		test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
		String sitetype=siteType.gateway.toString();
		test.log(LogStatus.INFO,"The siteType :"+sitetype);
		test.log(LogStatus.INFO,"Creating a site For Logged in user");
		response = spogServer.createSite(siteName,sitetype, sub_org_Id, validToken,test);
		Map<String,String > sitecreateResMap=new HashMap<>();
		test.log(LogStatus.INFO, "Check the created site");
		sitecreateResMap=spogServer.checkCreateSite(response, SpogConstants.SUCCESS_POST,siteName, sitetype,
				sub_org_Id,user_id,"",test);
		String registration_basecode=sitecreateResMap.get("registration_basecode");
		String site_id=sitecreateResMap.get("site_id");
		try {

			String decoded = URLDecoder.decode( registration_basecode.trim(), "UTF-8" );
			Base64Coder base64 = new Base64Coder();
			String decrypted = base64.decode( decoded );
			String[] parts    = decrypted.split( "\\n", -2 );
			site_registration_key=parts[1];
			//test.log(LogStatus.INFO, "Decoded base64code is : "+parts[1]);   

		} catch(UnsupportedEncodingException e ){
			test.log(LogStatus.FAIL,"The value of the error Message :"+e.getMessage());
		}
		test.log(LogStatus.INFO,"After decoding the site_registration_key is: "+site_registration_key);
		String gateway_id = UUID.randomUUID().toString();
		test.log(LogStatus.INFO,"Randomly generated gateway_id is: "+gateway_id);
		gateway_hostname=RandomStringUtils.randomAlphanumeric(4)+"_"+gateway_hostname;
		test.log(LogStatus.INFO, "Randomly generated gateway_hostname is: "+gateway_hostname);
		boolean is_registered=true;
		test.log(LogStatus.INFO, "Registering the gateway to site");
		response=gatewayServer.RegisterSite(site_registration_key,gateway_id,gateway_hostname,site_version,site_id,test);
		String site_secret=gatewayServer.checkRegisterSite(response,ServerResponseCode.Succes_Login, site_id,siteName,sitetype,sub_org_Id, user_id,is_registered,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
		test.log(LogStatus.INFO,"The secret key is :"+site_secret);
		response = gatewayServer.LoginSite(site_id, site_secret,gateway_id, test);
		test.log(LogStatus.INFO, "validate the login site response ");
		gatewayServer.checksiteLogin(response, ServerResponseCode.Succes_Login, "", test);
		String valid_token=gatewayServer.getJWTToken();
		test.log(LogStatus.INFO, "Get the site token "+valid_token);
		String invalid_site_id=UUID.randomUUID().toString();
		test.log(LogStatus.INFO, "Provide an invalid site id "+invalid_site_id);
		response = gatewayServer.getSiteConfiguration(invalid_site_id, valid_token, test);
		String site_config = gatewayServer.checkSiteConfiguration(response, invalid_site_id,ServerResponseCode.Resource_Does_Not_Exist,SpogMessageCode.SITE_NOT_FOUND,test);
		test.log(LogStatus.INFO, "The site configuration details are "+site_config);
		test.log(LogStatus.INFO, "Performing the operations to delete the user and orginzation by loggin in as csr admin");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		spogServer.DeleteUserById(user_id, test);
		spogServer.DeleteOrganizationWithCheck(sub_org_Id, test);
		spogServer.DeleteOrganizationWithCheck(organization_id, test);

	}
	
	@Test (dataProvider="suborganizationAndUserInfo")	
	public void getsuborgsiteconfiguration_othersiteJWTtoken_differentorg(String organizationName,
			String organizationType,
			String organizationEmail,
			String organizationPwd ,
			String organizationFirstName,
			String organizationLastName,
			String userEmail,
			String userPassword,
			String FirstName,
			String LastName,
			String Role_Id){
		String site_registration_key=null;
		test = rep.startTest("getsuborgsiteconfiguration_othersiteJWTtoken_differentorg");
		test.assignAuthor("prasaddeverakonda");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_"+organizationName;
		organizationEmail=RandomStringUtils.randomAlphanumeric(8)+"_"+organizationEmail;
		organizationFirstName=RandomStringUtils.randomAlphanumeric(8) +"_"+organizationFirstName;
		organizationLastName=RandomStringUtils.randomAlphanumeric(8) +"_"+organizationLastName;
		FirstName=RandomStringUtils.randomAlphanumeric(8) +"_"+FirstName;
		LastName=RandomStringUtils.randomAlphanumeric(8) +"_"+LastName;
		userEmail=RandomStringUtils.randomAlphanumeric(8)+"_"+userEmail;

		
		String msporganizationName = RandomStringUtils.randomAlphanumeric(8) + "_"+organizationName;
		String msporganizationEmail=RandomStringUtils.randomAlphanumeric(8)+"_"+organizationEmail;
		String msporganizationFirstName=RandomStringUtils.randomAlphanumeric(8) +"_"+organizationFirstName;
		String msporganizationLastName=RandomStringUtils.randomAlphanumeric(8) +"_"+organizationLastName;
		String mspFirstName=RandomStringUtils.randomAlphanumeric(8) +"_"+FirstName;
		String mspLastName=RandomStringUtils.randomAlphanumeric(8) +"_"+LastName;
		String mspuserEmail=RandomStringUtils.randomAlphanumeric(8)+"_"+userEmail;
		
		
		test.log(LogStatus.INFO,"Login with csrAdmin credentials");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword,test);
		test.log(LogStatus.INFO,"Get the JWT Token to create Oganization:");
		String CSR_token= spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The Token is :"+ CSR_token);
		
		test.log(LogStatus.INFO, "Creating a orgnization of type "+organizationType);
		//String organization_id = spogServer.CreateOrganizationWithCheck(organizationName, organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,test);
		String organization_id = spogServer.CreateOrganizationWithCheck(organizationName+org_model_prefix, organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName);
		test.log(LogStatus.INFO, "The organization id is "+organization_id);

		test.log(LogStatus.INFO, "Create a sub org under organization "+organization_id);
		String sub_org_Id = spogServer.createAccountWithCheck(organization_id, spogServer.ReturnRandom("spogqa_account"), organization_id, test);

		test.log(LogStatus.INFO,"Create user for the sub Organization ");
		Response response = spogServer.createUser( userEmail,userPassword,FirstName,LastName,SpogConstants.DIRECT_ADMIN,sub_org_Id,test);
		String user_id =spogServer.checkCreateUser(response,SpogConstants.SUCCESS_POST,userEmail,FirstName,LastName,SpogConstants.DIRECT_ADMIN,sub_org_Id,"",test);
		test.log(LogStatus.INFO, "The sub org userid is "+user_id);

		
		test.log(LogStatus.INFO,"Login with the user of sub org A "+userEmail);
		spogServer.userLogin(userEmail,userPassword);
		test.log(LogStatus.INFO,"Getting the JWTToken for the Logged in user");
		String validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The token is :"+ validToken );
		
		String siteName= spogServer.getRandomSiteName("TestCreate");
		test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
		String sitetype=siteType.gateway.toString();
		test.log(LogStatus.INFO,"The siteType :"+sitetype);
		test.log(LogStatus.INFO,"Creating a site For Logged in user");
		String site_id1 = createmultiplesites(sub_org_Id,validToken,user_id,test);
		
		String Site1_valid_token=gatewayServer.getJWTToken();
		test.log(LogStatus.INFO, "The site id "+site_id1 + " token is "+Site1_valid_token);
		
		
		test.log(LogStatus.INFO,"Login with csrAdmin credentials");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword,test);
		test.log(LogStatus.INFO,"Get the JWT Token to create Oganization:");
		CSR_token= spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The Token is :"+ CSR_token);
		
		
		test.log(LogStatus.INFO, "Creating a orgnization of type "+organizationType);
		String organization_id_B = spogServer.CreateOrganizationWithCheck(msporganizationName+org_model_prefix, organizationType, msporganizationEmail, organizationPwd, msporganizationFirstName, msporganizationLastName);
		test.log(LogStatus.INFO, "The organization id is "+organization_id_B);


		test.log(LogStatus.INFO, "Create a sub org under organization "+organization_id);
		String sub_org_Id_B = spogServer.createAccountWithCheck(organization_id_B, spogServer.ReturnRandom("spogqa_account1"), organization_id_B, test);

		test.log(LogStatus.INFO,"Create user for the sub Organization ");
		response = spogServer.createUser( "1"+userEmail,userPassword,FirstName,LastName,SpogConstants.DIRECT_ADMIN,sub_org_Id_B,test);
		String user_id_B =spogServer.checkCreateUser(response,SpogConstants.SUCCESS_POST,"1"+userEmail,FirstName,LastName,SpogConstants.DIRECT_ADMIN,sub_org_Id_B,"",test);
		test.log(LogStatus.INFO, "The sub org userid is "+user_id_B);

		
		test.log(LogStatus.INFO,"Login with the user of sub org B "+ "1"+userEmail);
		spogServer.userLogin("1"+userEmail,userPassword);
		test.log(LogStatus.INFO,"Getting the JWTToken for the Logged in user");
		String validToken_B = spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The token is :"+ validToken_B );
		
		test.log(LogStatus.INFO, "Create another site under the same org"+sub_org_Id);	
		String site_id2 = createmultiplesites(sub_org_Id_B,validToken_B,user_id_B,test);
		String Site2_valid_token=gatewayServer.getJWTToken();
		test.log(LogStatus.INFO, "The site id "+site_id2 + " token is "+Site2_valid_token);
		
		test.log(LogStatus.INFO, "Get the site "+site_id1+ "configuration details "+ " using the site ["+site_id2+"]" +" JWT token");
		response = gatewayServer.getSiteConfiguration(site_id1, Site2_valid_token, test);
		String site_config = gatewayServer.checkSiteConfiguration(response, site_id1,ServerResponseCode.Insufficient_Permissions,SpogMessageCode.RESOURCE_PERMISSION_DENY,test);
		test.log(LogStatus.INFO, "The site configuration details are "+site_config);
		
		test.log(LogStatus.INFO, "Performing the operations to delete the user and orginzation by loggin in as csr admin");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		spogServer.DeleteUserById(user_id, test);
		spogServer.DeleteOrganizationWithCheck(sub_org_Id, test);
		spogServer.DeleteOrganizationWithCheck(sub_org_Id_B, test);
		spogServer.DeleteOrganizationWithCheck(organization_id, test);

	}

	@Test (dataProvider="suborganizationAndUserInfo")	
	public void getsuborgsiteconfiguration_ofmultiplesites(String organizationName,
			String organizationType,
			String organizationEmail,
			String organizationPwd ,
			String organizationFirstName,
			String organizationLastName,
			String userEmail,
			String userPassword,
			String FirstName,
			String LastName,
			String Role_Id){
		String site_registration_key=null;
		test = rep.startTest("getsuborgsiteconfiguration_ofmultiplesites");
		test.assignAuthor("prasad.deverakonda");
		organizationName = RandomStringUtils.randomAlphanumeric(8) + "_"+organizationName;
		organizationEmail=RandomStringUtils.randomAlphanumeric(8)+"_"+organizationEmail;
		organizationFirstName=RandomStringUtils.randomAlphanumeric(8) +"_"+organizationFirstName;
		organizationLastName=RandomStringUtils.randomAlphanumeric(8) +"_"+organizationLastName;
		FirstName=RandomStringUtils.randomAlphanumeric(8) +"_"+FirstName;
		LastName=RandomStringUtils.randomAlphanumeric(8) +"_"+LastName;
		userEmail=RandomStringUtils.randomAlphanumeric(8)+"_"+userEmail;

		test.log(LogStatus.INFO,"Login with csrAdmin credentials");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword,test);
		test.log(LogStatus.INFO,"Get the JWT Token to create Oganization:");
		String CSR_token= spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The Token is :"+ CSR_token);
		test.log(LogStatus.INFO, "Creating a orgnization of type "+organizationType);
		//String organization_id = spogServer.CreateOrganizationWithCheck(organizationName, organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,test);
		String organization_id = spogServer.CreateOrganizationWithCheck(organizationName+org_model_prefix, organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName);
		test.log(LogStatus.INFO, "The organization id is "+organization_id);

		test.log(LogStatus.INFO, "Create a sub org under organization "+organization_id);
		String sub_org_Id = spogServer.createAccountWithCheck(organization_id, spogServer.ReturnRandom("spogqa_account"), organization_id, test);
		
		
		test.log(LogStatus.INFO,"Create user for the sub Organization ");
		Response response = spogServer.createUser( userEmail,userPassword,FirstName,LastName,SpogConstants.DIRECT_ADMIN,sub_org_Id,test);
		String user_id =spogServer.checkCreateUser(response,SpogConstants.SUCCESS_POST,userEmail,FirstName,LastName,SpogConstants.DIRECT_ADMIN,sub_org_Id,"",test);
		test.log(LogStatus.INFO, "The sub org userid is "+user_id);
		
		test.log(LogStatus.INFO,"Login with the user "+userEmail);
		spogServer.userLogin(userEmail,userPassword);
		test.log(LogStatus.INFO,"Getting the JWTToken for the Logged in user");
		String validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The token is :"+ validToken );
		
		String siteName= spogServer.getRandomSiteName("TestCreate");
		test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
		String sitetype=siteType.gateway.toString();
		test.log(LogStatus.INFO,"The siteType :"+sitetype);
		
		test.log(LogStatus.INFO,"Creating a site For Logged in user");
		String site_id1 = createmultiplesites(sub_org_Id,validToken,user_id,test);
		String Site1_valid_token=gatewayServer.getJWTToken();
		test.log(LogStatus.INFO, "The site id "+site_id1 + " token is "+Site1_valid_token);
		
		test.log(LogStatus.INFO, "Create another site under the same org"+sub_org_Id);	
		String site_id2 = createmultiplesites(sub_org_Id,validToken,user_id,test);
		String Site2_valid_token=gatewayServer.getJWTToken();
		test.log(LogStatus.INFO, "The site id "+site_id2 + " token is "+Site2_valid_token);
		
		test.log(LogStatus.INFO, "Get the site configuration details for site "+site_id1);
		response = gatewayServer.getSiteConfiguration(site_id1, Site1_valid_token, test);
		String site_config = gatewayServer.checkSiteConfiguration(response, site_id1,ServerResponseCode.Success_Get,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
		test.log(LogStatus.INFO, "The site configuration details are "+site_config);
		
		test.log(LogStatus.INFO, "Get the site configuration details for site using the site1 token"+site_id2);
		response = gatewayServer.getSiteConfiguration(site_id2, Site1_valid_token, test);
		String site_config1 = gatewayServer.checkSiteConfiguration(response,site_id2, ServerResponseCode.Success_Get,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
		test.log(LogStatus.INFO, "The site configuration details are "+site_config1);
		
		test.log(LogStatus.INFO, "Performing the operations to delete the user and orginzation by loggin in as csr admin");
		spogServer.userLogin(csrAdminUserName, csrAdminPassword);
		spogServer.DeleteUserById(user_id, test);
		spogServer.DeleteOrganizationWithCheck(sub_org_Id, test);
		spogServer.DeleteOrganizationWithCheck(organization_id, test);

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
/*	@AfterTest
	public void aftertest() {
		test.log(LogStatus.INFO, "The total test cases passed are "+count1.getpassedcount());
		test.log(LogStatus.INFO, "the total test cases failed are "+count1.getfailedcount());
		try {
				if(count1.getfailedcount()>0) {
					remaincases=Nooftest-(count1.getpassedcount()+count1.getfailedcount());
					bqdb1.updateTable(BQame, "KIRSRI-LAPW10", buildnumber, String.valueOf(Nooftest), Integer.toString(count1.getpassedcount()), Integer.toString(count1.getfailedcount()), String.valueOf(remaincases), count1.getcreationtime(), "Failed");
				}else {
					remaincases=Nooftest-(count1.getpassedcount()+count1.getfailedcount());
					bqdb1.updateTable(BQame, "KIRSRI-LAPW10", buildnumber, String.valueOf(Nooftest), Integer.toString(count1.getpassedcount()), Integer.toString(count1.getfailedcount()), String.valueOf(remaincases), count1.getcreationtime(), "Passed");
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//rep.endTest(test);
		 		rep.flush();
	}

	@AfterClass
	public void updatebd() {
		try {
			if(count1.getfailedcount()>0) {
				Nooftest=count1.getpassedcount()+count1.getfailedcount()+count1.getskippedcount();
				bqdb1.updateTable(BQame, running_Machine, buildnumber, String.valueOf(Nooftest), Integer.toString(count1.getpassedcount()), Integer.toString(count1.getfailedcount()), String.valueOf(count1.getskippedcount()), count1.getcreationtime(), "Failed");
			}else {
				Nooftest=count1.getpassedcount()+count1.getfailedcount()+count1.getskippedcount();
				bqdb1.updateTable(BQame, running_Machine, buildnumber, String.valueOf(Nooftest), Integer.toString(count1.getpassedcount()), Integer.toString(count1.getfailedcount()), String.valueOf(count1.getskippedcount()), count1.getcreationtime(), "Passed");
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}*/
	
	
	/**********************************Generic Function for create a site *****************************************************/
	public String createmultiplesites(String sub_org_Id,String validToken,String user_id,ExtentTest test) {
		String site_registration_key=null;
		String siteName= spogServer.getRandomSiteName("TestCreate");
		test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
		String sitetype=siteType.gateway.toString();
		test.log(LogStatus.INFO,"The siteType :"+sitetype);
		test.log(LogStatus.INFO,"Creating a site For Logged in user");
		Response response = spogServer.createSite(siteName,sitetype, sub_org_Id, validToken,test);
		Map<String,String > sitecreateResMap=new HashMap<>();
		test.log(LogStatus.INFO, "Check the created site");
		sitecreateResMap=spogServer.checkCreateSite(response, SpogConstants.SUCCESS_POST,siteName, sitetype,
				sub_org_Id,user_id,"",test);
		String registration_basecode=sitecreateResMap.get("registration_basecode");
		String site_id=sitecreateResMap.get("site_id");
		try {

			String decoded = URLDecoder.decode( registration_basecode.trim(), "UTF-8" );
			Base64Coder base64 = new Base64Coder();
			String decrypted = base64.decode( decoded );
			String[] parts    = decrypted.split( "\\n", -2 );
			site_registration_key=parts[1];
			//test.log(LogStatus.INFO, "Decoded base64code is : "+parts[1]);   

		} catch(UnsupportedEncodingException e ){
			test.log(LogStatus.FAIL,"The value of the error Message :"+e.getMessage());
		}
		test.log(LogStatus.INFO,"After decoding the site_registration_key is: "+site_registration_key);
		String gateway_id = UUID.randomUUID().toString();
		test.log(LogStatus.INFO,"Randomly generated gateway_id is: "+gateway_id);
		gateway_hostname=RandomStringUtils.randomAlphanumeric(4)+"_"+gateway_hostname;
		test.log(LogStatus.INFO, "Randomly generated gateway_hostname is: "+gateway_hostname);
		boolean is_registered=true;
		test.log(LogStatus.INFO, "Registering the gateway to site");
		response=gatewayServer.RegisterSite(site_registration_key,gateway_id,gateway_hostname,site_version,site_id,test);
		String site_secret=gatewayServer.checkRegisterSite(response,ServerResponseCode.Succes_Login, site_id,siteName,sitetype,sub_org_Id, user_id,is_registered,SpogMessageCode.SUCCESS_GET_PUT_DEL,test);
		test.log(LogStatus.INFO,"The secret key is :"+site_secret);
		response = gatewayServer.LoginSite(site_id, site_secret,gateway_id, test);
		test.log(LogStatus.INFO, "validate the login site response ");
		gatewayServer.checksiteLogin(response, ServerResponseCode.Succes_Login, "", test);
		return site_id;
	}
}