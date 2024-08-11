package api.organizations.submsp;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.text.StrBuilder;
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

import Constants.ConnectionStatus;
import Constants.DestinationStatus;
import Constants.DestinationType;
import Constants.OSMajor;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.GatewayServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGReportServer;
import InvokerServer.SPOGServer;
import InvokerServer.Source4SPOGServer;
import InvokerServer.UserSpogServer;
import dataPreparation.JsonPreparation;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class CreateSubMspTest extends base.prepare.Is4Org {
	
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private UserSpogServer userSpogServer;
	private SPOGReportServer spogReportServer;
	private SPOGDestinationServer spogDestinationServer;
	private Source4SPOGServer source4SPOGServer;
	//public int Nooftest;
	private ExtentTest test;
	private String common_password = "Mclaren@2013";
	private String csrAdminUserName;
	private String csrAdminPassword;
	private String csr_token;
	private String csr_readonly_email;
	private String csr_readonly_token;

	private String prefix_direct = "SPOG_QA_RAKESH_BQ_DIRECT";
	private String postfix_email = "@arcserve.com";
	private String direct_org_name = prefix_direct + "_org";
	private String direct_org_email = direct_org_name + postfix_email;
	private String direct_org_first_name = direct_org_name + "_first_name";
	private String direct_org_last_name = direct_org_name + "_last_name";
	private String direct_user_name = prefix_direct + "_admin";
	private String direct_user_name_email = prefix_direct + "_admin" + postfix_email;
	private String direct_user_first_name = direct_user_name + "_first_name";
	private String direct_user_last_name = direct_user_name + "_last_name";
	private String direct_user_validToken;

	private String prefix_msp = "SPOG_QA_RAKESH_BQ_MSP";
	private String msp_org_name = prefix_msp + "_org";
	private String msp_org_email = msp_org_name + postfix_email;
	private String msp_org_first_name = msp_org_name + "_first_name";
	private String msp_org_last_name = msp_org_name + "_last_name";
	private String msp_user_name = prefix_msp + "_admin";
	private String msp_user_name_email = prefix_msp + "_admin" + postfix_email;
	private String msp_user_first_name = msp_user_name + "_first_name";
	private String msp_user_last_name = msp_user_name + "_last_name";
	private String msp_user_validToken;

	private String prefix_msp_b = "SPOG_QA_RAKESH_BQ_MSP_B";
	private String msp_org_name_b = prefix_msp_b + "_org";
	private String msp_org_email_b = msp_org_name_b + postfix_email;
	private String msp_org_first_name_b = msp_org_name_b + "_first_name";
	private String msp_org_last_name_b = msp_org_name_b + "_last_name";
	private String mspb_user_validToken;

	private String prefix_msp_account_admin = "spog_rakesh_msp_account";
	private String msp_account_admin_email = prefix_msp_account_admin+postfix_email;
	private String msp_account_admin_first_name = prefix_msp_account_admin+"_first_name";
	private String msp_account_admin_last_name = prefix_msp_account_admin+"_last_name";
	private String msp_account_admin_validToken;

	private String initial_sub_org_name_a = "SPOG_QA_RAKESH_BQ_SUB_ORG_A";
	private String initial_sub_email_a = "spog_qa_sub_rakesh_a@arcserve.com";
	private String initial_sub_first_name_a = "spog_qa_sub_rakesh_a";
	private String initial_sub_last_name_a = "spog_qa_sub_chalamala_a";
	private String sub_orga_user_validToken;

	private String initial_sub_org_name_b = "SPOG_QA_RAKESH_BQ_SUB_ORG_B";
	private String initial_sub_email_b = "spog_qa_sub_rakesh_b@arcserve.com";
	private String initial_sub_first_name_b = "spog_qa_sub_rakesh_b";
	private String initial_sub_last_name_b = "spog_qa_sub_chalamala_b";
	private String sub_orgb_user_validToken;

	private String site_version="1.0.0";
	private String gateway_hostname="rakesh";
	//used for test case count like passed,failed,remaining cases
	int passedcases=0;
	int failedcases=0;
	int skippedcases=0;
	String buildnumber=null;

   /* private ExtentReports rep;
    private SQLServerDb bqdb1;
    public int Nooftest;
    private long creationTime;
    private String BQName=null;
    private String runningMachine;
    private testcasescount count1;
    private String buildVersion;	*/
	
	String csr_organization_id;
	String direct_organization_id;
	String msp_organization_id;
	String msp_organization_id_b;
	String sub_org_Id;
	String sub_org_Id_1;

	String csr_user_id;
	String direct_user_id;
	String msp_user_id;
	String mspb_user_id;
	String suborga_user_id;
	String suborgb_user_id;
	String msp_account_admin_id;

	String direct_cloud_id;
	String msp_cloud_id;
	String mspb_cloud_id;
	String suborga_site_id;
	String suborgb_site_id;
	String direct_cloud_token;
	String msp_cloud_token;
	
	String[] datacenters;
	LocalDate date = LocalDate.now();  
	LocalDate yesterday = date.minusDays(1);  
	LocalDate tomorrow = yesterday.plusDays(2);

	private String  org_model_prefix=this.getClass().getSimpleName();
	
	@BeforeClass
	@Parameters({ "baseURI", "port","csrAdminUserName", "csrAdminPassword","csrReadOnlyUser","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword,String csrReadOnlyUser, String logFolder,String runningMachine,String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer = new GatewayServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		spogReportServer = new SPOGReportServer(baseURI, port);
		spogDestinationServer = new SPOGDestinationServer(baseURI, port);
		source4SPOGServer = new Source4SPOGServer(baseURI, port);
		
		rep = ExtentManager.getInstance(this.getClass().getSimpleName(), logFolder);
		test = rep.startTest("Setup");
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		String author = "Kanamarlapudi, Chandra Kanth";

		Nooftest=0;
		this.csrAdminUserName = adminUserName;
		this.csrAdminPassword = adminPassword;
		this.csr_readonly_email = csrReadOnlyUser;

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

			creationTime = System.currentTimeMillis();
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
		//login in as csr admin
		test.log(LogStatus.INFO, "Logging with csrAdmin");
		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
		csr_token = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Get the logged in user id ");
		csr_user_id = spogServer.GetLoggedinUser_UserID();
		test.log(LogStatus.INFO, "The csr user id is "+csr_user_id);
		csr_organization_id = spogServer.GetLoggedinUserOrganizationID();

		String prefix = RandomStringUtils.randomAlphanumeric(8);
		//Create a msp org
		test.log(LogStatus.INFO, "create a msp org");
		this.msp_org_email = prefix+this.msp_org_email;
		this.msp_org_name = prefix+msp_org_name+org_model_prefix;
		msp_organization_id = spogServer.CreateOrganizationByEnrollWithCheck(msp_org_name, SpogConstants.MSP_ORG, this.msp_org_email, common_password, prefix+msp_org_first_name, prefix+msp_org_last_name,test);

		test.log(LogStatus.INFO, "Convert organization with id:"+msp_organization_id+" to root MSP");
		spogServer.convertTo3Tier(msp_organization_id);
		
		test.log(LogStatus.INFO, "create another msp org");
		this.msp_org_email_b = prefix+this.msp_org_email_b;
		this.msp_org_name_b = prefix+msp_org_name_b+org_model_prefix;
		msp_organization_id_b = spogServer.CreateOrganizationWithCheck(msp_org_name_b, SpogConstants.MSP_ORG, this.msp_org_email_b, common_password, prefix+msp_org_first_name_b, prefix+msp_org_last_name_b,test);

		//Create a suborg under MSP org and a user for sub org
		spogServer.userLogin(this.msp_org_email, common_password);
		test.log(LogStatus.INFO,"Getting the JWTToken for the Logged in user");
		msp_user_validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The token is :"+ msp_user_validToken );

		test.log(LogStatus.INFO, "Get the logged in user id ");
		msp_user_id = spogServer.GetLoggedinUser_UserID();
		test.log(LogStatus.INFO, "The msp user id is "+msp_user_id);

		test.log(LogStatus.INFO, "Create a suborg A under msp org");
		this.initial_sub_org_name_a = prefix+initial_sub_org_name_a;
		sub_org_Id = spogServer.createAccountWithCheck(msp_organization_id, initial_sub_org_name_a,
				msp_organization_id);
		test.log(LogStatus.INFO, "Create a suborg B under msp org");
		this.initial_sub_org_name_b = prefix+initial_sub_org_name_b; 
		sub_org_Id_1 = spogServer.createAccountWithCheck(msp_organization_id, initial_sub_org_name_b,
				msp_organization_id);

		test.log(LogStatus.INFO, "Create a direct user for sub org A");
		this.initial_sub_email_a = prefix + initial_sub_email_a;
		suborga_user_id = spogServer.createUserAndCheck(this.initial_sub_email_a, common_password,
				prefix + this.initial_sub_first_name_a, prefix + this.initial_sub_last_name_a,
				SpogConstants.DIRECT_ADMIN, sub_org_Id, test);

		test.log(LogStatus.INFO, "Create a direct user for sub org B");
		this.initial_sub_email_b = prefix + initial_sub_email_b;
		suborgb_user_id = spogServer.createUserAndCheck(this.initial_sub_email_b, common_password,
				prefix + this.initial_sub_first_name_b, prefix + this.initial_sub_last_name_b,
				SpogConstants.DIRECT_ADMIN, sub_org_Id_1, test);

		test.log(LogStatus.INFO, "Login in to sub org A");
		spogServer.userLogin(this.initial_sub_email_a, common_password);
		test.log(LogStatus.INFO,"Getting the JWTToken for the Logged in user");
		sub_orga_user_validToken = spogServer.getJWTToken();

		test.log(LogStatus.INFO, "Login in to sub org B");
		spogServer.userLogin(this.initial_sub_email_b, common_password);
		test.log(LogStatus.INFO,"Getting the JWTToken for the Logged in user");
		sub_orgb_user_validToken = spogServer.getJWTToken();

		//login to mspb and create a site
		spogServer.userLogin(this.msp_org_email_b, common_password);
		test.log(LogStatus.INFO,"Getting the JWTToken for the Logged in user");
		mspb_user_validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The token is :"+ mspb_user_validToken );

		test.log(LogStatus.INFO, "Get the logged in user id ");
		mspb_user_id = spogServer.GetLoggedinUser_UserID();
		test.log(LogStatus.INFO, "The msp user id is "+mspb_user_id);

		//Create msp_account_admin
		test.log(LogStatus.INFO, "Create a msp account admin under msp org");
		msp_account_admin_email = prefix+msp_account_admin_email;
		spogServer.setToken(msp_user_validToken);
		msp_account_admin_id = spogServer.createUserAndCheck(msp_account_admin_email, common_password, msp_account_admin_first_name, msp_account_admin_last_name, "msp_account_admin", msp_organization_id, test);
		spogServer.userLogin(msp_account_admin_email, common_password);
		this.msp_account_admin_validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The token is :"+ msp_account_admin_validToken );
		userSpogServer.assignMspAccountAdmins(msp_organization_id, sub_org_Id, new String[] {msp_account_admin_id}, msp_user_validToken);
		userSpogServer.assignMspAccountAdmins(msp_organization_id, sub_org_Id_1, new String[] {msp_account_admin_id}, msp_user_validToken);
		

		//Create a direct org
		spogServer.setToken(csr_token);
		this.direct_org_email = prefix+this.direct_org_email;
		this.direct_org_name = prefix+direct_org_name+org_model_prefix;
		direct_organization_id = spogServer.CreateOrganizationWithCheck(this.direct_org_name, SpogConstants.DIRECT_ORG, this.direct_org_email, common_password, prefix+direct_org_first_name, prefix+direct_org_last_name,test);
		spogServer.userLogin(this.direct_org_email, common_password);
		test.log(LogStatus.INFO,"Getting the JWTToken for the Logged in user");
		direct_user_validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The token is :"+ direct_user_validToken );

		test.log(LogStatus.INFO, "Get the logged in user id ");
		direct_user_id = spogServer.GetLoggedinUser_UserID();
		test.log(LogStatus.INFO, "The direct org user id is "+direct_user_id);
		
		test.log(LogStatus.INFO, "csr_readonly user login");
		spogServer.userLogin(csr_readonly_email, common_password);
		csr_readonly_token = spogServer.getJWTToken();
		
		spogDestinationServer.setToken(csr_token);
		datacenters = spogDestinationServer.getDestionationDatacenterID();
	}

	@DataProvider(name="testCases")
	public Object[][] testCases(){
		return new Object[][] {
			//200
			{"Create sub msp under an MSP organization with msp user token", 
				msp_user_validToken, msp_organization_id, spogServer.ReturnRandom("submsp"), msp_organization_id, 
				spogServer.ReturnRandom("first"), spogServer.ReturnRandom("last"), spogServer.ReturnRandom("mail")+"@abc.com",
				datacenters[0], SpogConstants.SUCCESS_POST, null},
			{"Create sub msp under an MSP organization with csr token", 
				csr_token, msp_organization_id, spogServer.ReturnRandom("submsp"), msp_organization_id, 
				spogServer.ReturnRandom("first"), spogServer.ReturnRandom("last"), spogServer.ReturnRandom("mail")+"@abc.com",
				datacenters[0], SpogConstants.SUCCESS_POST, null},
			{"Create sub msp under an MSP organization with msp user token and no parent_id in request payload", 
				msp_user_validToken, msp_organization_id, spogServer.ReturnRandom("submsp"), "none", 
				spogServer.ReturnRandom("first"), spogServer.ReturnRandom("last"), spogServer.ReturnRandom("mail")+"@abc.com",
				datacenters[0], SpogConstants.SUCCESS_POST, null},
			{"Create sub msp under an MSP organization with msp user token and no datacenter_id in request payload", 
				msp_user_validToken, msp_organization_id, spogServer.ReturnRandom("submsp"), msp_organization_id, 
				spogServer.ReturnRandom("first"), spogServer.ReturnRandom("last"), spogServer.ReturnRandom("mail")+"@abc.com",
				"none", SpogConstants.SUCCESS_POST, null},
			
			
			//400
			{"Create sub msp organization with invalid organization id ", 
				msp_user_validToken, "invalid", spogServer.ReturnRandom("submsp"), msp_organization_id, 
				spogServer.ReturnRandom("first"), spogServer.ReturnRandom("last"), spogServer.ReturnRandom("mail")+"@abc.com",
				datacenters[0], SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.ELEMENT_IS_NOT_UUID},
			{"Create sub msp organization without organization name in the request payload", 
				msp_user_validToken, msp_organization_id, "none", msp_organization_id, 
				spogServer.ReturnRandom("first"), spogServer.ReturnRandom("last"), spogServer.ReturnRandom("mail")+"@abc.com",
				datacenters[0], SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.CAN_NOT_BE_BLANK},
			{"Create sub msp organization with empty string as organization name in the request payload", 
				msp_user_validToken, msp_organization_id, "", msp_organization_id, 
				spogServer.ReturnRandom("first"), spogServer.ReturnRandom("last"), spogServer.ReturnRandom("mail")+"@abc.com",
				datacenters[0], SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.CAN_NOT_BE_BLANK},
			{"Create sub msp organization with organization name = null in the request payload", 
				msp_user_validToken, msp_organization_id, null, msp_organization_id, 
				spogServer.ReturnRandom("first"), spogServer.ReturnRandom("last"), spogServer.ReturnRandom("mail")+"@abc.com",
				datacenters[0], SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.CAN_NOT_BE_BLANK},
			{"Create sub msp organization with invalid parent id in request payload", 
				msp_user_validToken, msp_organization_id, spogServer.ReturnRandom("submsp"), "invalid", 
				spogServer.ReturnRandom("first"), spogServer.ReturnRandom("last"), spogServer.ReturnRandom("mail")+"@abc.com",
				datacenters[0], SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.INVALID_PARAM_COMBINATION},
			{"Create sub msp organization with  parent id that does not exist in request payload", 
				msp_user_validToken, msp_organization_id, spogServer.ReturnRandom("submsp"), UUID.randomUUID().toString(), 
				spogServer.ReturnRandom("first"), spogServer.ReturnRandom("last"), spogServer.ReturnRandom("mail")+"@abc.com",
				datacenters[0], SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.INVALID_PARAM_COMBINATION},
			{"Create sub msp organization with first name as empty string in the request payload", 
				msp_user_validToken, msp_organization_id, spogServer.ReturnRandom("submsp"), msp_organization_id, 
				"", spogServer.ReturnRandom("last"), spogServer.ReturnRandom("mail")+"@abc.com",
				datacenters[0], SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.CAN_NOT_BE_BLANK},
			{"Create sub msp organization with first name = null in the request payload", 
				msp_user_validToken, msp_organization_id, spogServer.ReturnRandom("submsp"), msp_organization_id, 
				null, spogServer.ReturnRandom("last"), spogServer.ReturnRandom("mail")+"@abc.com",
				datacenters[0], SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.CAN_NOT_BE_BLANK},
			{"Create sub msp organization without first name in the request payload", 
				msp_user_validToken, msp_organization_id, spogServer.ReturnRandom("submsp"), msp_organization_id, 
				"none", spogServer.ReturnRandom("last"), spogServer.ReturnRandom("mail")+"@abc.com",
				datacenters[0], SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.CAN_NOT_BE_BLANK},
			{"Create sub msp organization with last name as empty string in the request payload", 
				msp_user_validToken, msp_organization_id, spogServer.ReturnRandom("submsp"), msp_organization_id, 
				spogServer.ReturnRandom("first"), "", spogServer.ReturnRandom("mail")+"@abc.com",
				datacenters[0], SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.CAN_NOT_BE_BLANK},
			{"Create sub msp organization with last name = null in the request payload", 
				msp_user_validToken, msp_organization_id, spogServer.ReturnRandom("submsp"), msp_organization_id, 
				spogServer.ReturnRandom("first"), null, spogServer.ReturnRandom("mail")+"@abc.com",
				datacenters[0], SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.CAN_NOT_BE_BLANK},
			{"Create sub msp organization without last name in the request payload", 
				msp_user_validToken, msp_organization_id, spogServer.ReturnRandom("submsp"), msp_organization_id, 
				spogServer.ReturnRandom("first"), "none", spogServer.ReturnRandom("mail")+"@abc.com",
				datacenters[0], SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.CAN_NOT_BE_BLANK},
			{"Create sub msp organization with email as empty string in the request payload", 
				msp_user_validToken, msp_organization_id, spogServer.ReturnRandom("submsp"), msp_organization_id, 
				spogServer.ReturnRandom("first"), spogServer.ReturnRandom("last"), "",
				datacenters[0], SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.CAN_NOT_BE_BLANK},
			{"Create sub msp organization with eamil = null in the request payload", 
				msp_user_validToken, msp_organization_id, spogServer.ReturnRandom("submsp"), msp_organization_id, 
				spogServer.ReturnRandom("first"), spogServer.ReturnRandom("last"), null,
				datacenters[0], SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.CAN_NOT_BE_BLANK},
			{"Create sub msp organization without email in the request payload", 
				msp_user_validToken, msp_organization_id, spogServer.ReturnRandom("submsp"), msp_organization_id, 
				spogServer.ReturnRandom("first"), spogServer.ReturnRandom("last"), "none",
				datacenters[0], SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.CAN_NOT_BE_BLANK},
			{"Create sub msp organization with used email in the request payload", 
				msp_user_validToken, msp_organization_id, spogServer.ReturnRandom("submsp"), msp_organization_id, 
				spogServer.ReturnRandom("first"), spogServer.ReturnRandom("last"), msp_org_email,
				datacenters[0], SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.EMAIL_ALREADY_EXISTS},
			{"Create sub msp organization with invalid datacenter id in request payload", 
				msp_user_validToken, msp_organization_id, spogServer.ReturnRandom("submsp"), msp_organization_id, 
				spogServer.ReturnRandom("first"), spogServer.ReturnRandom("last"), spogServer.ReturnRandom("mail")+"@abc.com",
				datacenters[0]+1, SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.DATACENTER_ID_NOT_FOUND},
			
			//401
			{"Create sub msp under an MSP organization with no token", 
				"", msp_organization_id, spogServer.ReturnRandom("submsp"), msp_organization_id, 
				spogServer.ReturnRandom("first"), spogServer.ReturnRandom("last"), spogServer.ReturnRandom("mail")+"@abc.com",
				datacenters[0], SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED},
			{"Create sub msp under an MSP organization with null as token", 
				null, msp_organization_id, spogServer.ReturnRandom("submsp"), msp_organization_id, 
				spogServer.ReturnRandom("first"), spogServer.ReturnRandom("last"), spogServer.ReturnRandom("mail")+"@abc.com",
				datacenters[0], SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTHENTICATION_FAILED},
			{"Create sub msp under an MSP organization with invalid token", 
				"invalid", msp_organization_id, spogServer.ReturnRandom("submsp"), msp_organization_id, 
				spogServer.ReturnRandom("first"), spogServer.ReturnRandom("last"), spogServer.ReturnRandom("mail")+"@abc.com",
				datacenters[0], SpogConstants.NOT_LOGGED_IN, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT},
			
			//403
			{"Create sub msp under an MSP organization with direct org user token", 
				direct_user_validToken, msp_organization_id, spogServer.ReturnRandom("submsp"), msp_organization_id, 
				spogServer.ReturnRandom("first"), spogServer.ReturnRandom("last"), spogServer.ReturnRandom("mail")+"@abc.com",
				datacenters[0], SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Create sub msp under an MSP organization with suborg user token", 
				sub_orga_user_validToken, msp_organization_id, spogServer.ReturnRandom("submsp"), msp_organization_id, 
				spogServer.ReturnRandom("first"), spogServer.ReturnRandom("last"), spogServer.ReturnRandom("mail")+"@abc.com",
				datacenters[0], SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Create sub msp under an MSP organization with msp account admin token", 
				msp_account_admin_validToken, msp_organization_id, spogServer.ReturnRandom("submsp"), msp_organization_id, 
				spogServer.ReturnRandom("first"), spogServer.ReturnRandom("last"), spogServer.ReturnRandom("mail")+"@abc.com",
				datacenters[0], SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Create sub msp under an MSP organization with csr readonly user token", 
				csr_readonly_token, msp_organization_id, spogServer.ReturnRandom("submsp"), msp_organization_id, 
				spogServer.ReturnRandom("first"), spogServer.ReturnRandom("last"), spogServer.ReturnRandom("mail")+"@abc.com",
				datacenters[0], SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			{"Create sub msp under an MSP organization with other msp org user token", 
				mspb_user_validToken, msp_organization_id, spogServer.ReturnRandom("submsp"), msp_organization_id, 
				spogServer.ReturnRandom("first"), spogServer.ReturnRandom("last"), spogServer.ReturnRandom("mail")+"@abc.com",
				datacenters[0], SpogConstants.INSUFFICIENT_PERMISSIONS, SpogMessageCode.RESOURCE_PERMISSION_DENY},
			
			//404
			{"Create sub msp under an MSP organization with msp user token and organization id that does not exist", 
				msp_user_validToken, UUID.randomUUID().toString(), spogServer.ReturnRandom("submsp"), msp_organization_id, 
				spogServer.ReturnRandom("first"), spogServer.ReturnRandom("last"), spogServer.ReturnRandom("mail")+"@abc.com",
				datacenters[0], SpogConstants.RESOURCE_NOT_EXIST, SpogMessageCode.ORGANIZATION_NOT_FOUND_OR_REMOVED},
		};
	}
	
	@Test(dataProvider="testCases")
	public void createSubMspTests(String testCase,
									String token,
									String rootOrgId,
									String organization_name,
									String parent_id,
									String first_name,
									String last_name,
									String email,
									String datacenter_id,
									int expectedStatusCode,
									SpogMessageCode expectedErrorMessage
									) {
		test=ExtentManager.getNewTest(testCase);
	
		test.log(LogStatus.INFO, "Compose the request payload");
		HashMap<String, Object> requestInfo = new JsonPreparation().composeSubMspRqstInfo(organization_name, parent_id, first_name, last_name, email, datacenter_id);
		
		test.log(LogStatus.INFO, "Call api and validate response");
		spogServer.createSubMspWithCheck(token, rootOrgId, requestInfo, expectedStatusCode, expectedErrorMessage, test);
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
	/******************************************************************RandomFunction******************************************************************************/
	public int gen_random_index(String[] job_status) {
		Random generator = new Random();
		int randomindx = generator.nextInt(job_status.length);

		return randomindx;
	}
}
