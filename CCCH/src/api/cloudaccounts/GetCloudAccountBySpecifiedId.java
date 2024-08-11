package api.cloudaccounts;

import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import Constants.ConnectionStatus;
import Constants.DestinationStatus;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import Constants.SpogMessageCode;
import Constants.volume_type;
import InvokerServer.GatewayServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class GetCloudAccountBySpecifiedId {
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private ExtentReports rep;
	private ExtentTest test;
	private Response response;
	private String commonPassword="Mclaren@2013";
	private SPOGDestinationServer spogDestinationServer;
	//direct_organization
	private String prefix_direct = "spog_direct_bharadwaj";
	private String postfix_email = "@arcserve.com";
	private String direct_org_name = prefix_direct + "_org";
	private String directOrg_id=null;
	private String directOrg_id2=null;
	private String direct_user_name = prefix_direct + "_admin";
	private String direct_user_name_email = direct_user_name + postfix_email;
	private String direct_user_first_name = direct_user_name + "_first_name";
	private String direct_user_last_name = direct_user_name + "_last_name";
	private String direct_user_id =null;
	private String final_direct_user_name_email = null;
	private String direct_user_id_2;
	private String direct_Token="";
	private String direct_Token_1="";
	private String direct_user_validToken_2;
	private String direct_user_validToken_3;
	private String direct_user_validToken="";

	//msp_organization and suborganization
	private String prefix_msp = "spog_msp_bharadwaj";
	private String msp_org_name = prefix_msp + "_org";
	private String msp_org_email = msp_org_name + postfix_email;
	private String msp_org_first_name = msp_org_name + "_first_name";
	private String msp_org_last_name = msp_org_name + "_last_name";
	private String msp_user_name = prefix_msp + "_admin";
	private String msp_user_name_email = prefix_msp + "_admin" + postfix_email;
	private String msp_user_first_name = msp_user_name + "_first_name";
	private String msp_user_last_name = msp_user_name + "_last_name";
	private String mspOrg_id=null;
	private String mspOrg2_id=null;
	private String msp_user_id=null;
	private String final_msp_user_name_email=null;	  
	private String account_user_email=null;
	private String msp_user_validToken="";
	private String msp_token="";
	private String msp_token_1="";
	private String account_id=null;
	private String account2_id=null;
	private String direct_user_id_3;

	private String  cloud_account_id1;


	//csr_organization
	private String csrAdminUserName;
	private String csrAdminPassword;
	private String prefix_csr = "spog_csr_bharadwaj";
	private String csr_user_name = prefix_csr + "_admin";
	private String csr_user_name_email = prefix_csr + "_admin" + postfix_email;
	private String csr_user_first_name = csr_user_name + "_first_name";
	private String csr_user_last_name = csr_user_name + "_last_name";
	private String csrOrg_id=null;
	private String csr_Token="";

	//this is for update portal, each testng class is taken as BQ set\
	private SQLServerDb bqdb1;
	public int Nooftest;
	private long creationTime;
	private String BQName=null;
	private String runningMachine;
	private testcasescount count1;
	private String buildVersion=null;


	HashMap<String,String >retention=new HashMap<String,String>();
	HashMap<String,Object>cloud_direct_volume=new HashMap<String,Object>();

	HashMap<String,Object>cloud_dedupe_volume=new HashMap<String,Object>();
	private List<String > directAccounts=new ArrayList<String>();
	private List<String> mspAccounts=new ArrayList<String>();;

	@BeforeClass
	@Parameters({ "baseURI", "port","csrAdminUserName", "csrAdminPassword","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword, String logFolder,String runningMachine,String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer =new GatewayServer(baseURI,port);
	    spogDestinationServer=new 	SPOGDestinationServer(baseURI,port) ;
	    
		rep = ExtentManager.getInstance("GetCloudAccountBySpecifiedId",logFolder);
		this.csrAdminUserName = adminUserName;
		this.csrAdminPassword = adminPassword;
		Nooftest=0;
		bqdb1 = new SQLServerDb();
		String author="Malleswari";
		count1 = new testcasescount();
		test = rep.startTest("beforeClass");
		test.assignAuthor("sykam.malleswari");

		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date=new java.util.Date();
		this.buildVersion=buildVersion+"_"+dateFormater.format(date);
		this.BQName = this.getClass().getSimpleName();
		this.runningMachine=runningMachine;
		System.out.println("The value of hit is "+count1.isstarttimehit());
		if(count1.isstarttimehit()==0) {
			System.out.println("into creation time");
			creationTime=System.currentTimeMillis();
			count1.setcreationtime(creationTime);
			//creationTime = System.currentTimeMillis();
			try {
				bqdb1.updateTable(BQName, this.runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0", String.valueOf(Nooftest), count1.getcreationtime(), "InProgress",author+" and Rest server is "+baseURI.split("//")[1]);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//login with csr_admin user       
		spogServer.userLogin(csrAdminUserName,csrAdminPassword);

		//getting the JwtToken for csr_admin 
		csr_Token=spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The csr_token:"+csr_Token);

		//create a direct organization
		test.log(LogStatus.INFO,"create  a direct organization");
		String organization_email_direct=RandomStringUtils.randomAlphanumeric(8)+"_spog_direct_bharadwaj@arcserve.com";
		directOrg_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spog_bharadwaj_direct"),SpogConstants.DIRECT_ORG,organization_email_direct,commonPassword,spogServer.ReturnRandom("bharadwaj_first"),spogServer.ReturnRandom("bharadwaj_last"));

		//login direct organization 
		test.log(LogStatus.INFO,"Logging with the direct org admin user");
		spogServer.userLogin(organization_email_direct, commonPassword);

		//get the JWT token for direct user 
		direct_Token=spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The csr_token:"+direct_Token);

		//create a user  under the direct_organization
		test.log(LogStatus.INFO,"create a admin under direct org");
		final_direct_user_name_email=RandomStringUtils.randomAlphanumeric(4)+direct_user_name_email;
		direct_user_id = spogServer.createUserAndCheck(final_direct_user_name_email, commonPassword, direct_user_first_name,direct_user_last_name, SpogConstants.DIRECT_ADMIN, directOrg_id, test);
		test.log(LogStatus.INFO,"The value of the user_id:"+direct_user_id);
		spogServer.userLogin(final_direct_user_name_email, commonPassword);

		//login in with direct user 
		spogServer.userLogin(final_direct_user_name_email, commonPassword);
		test.log(LogStatus.INFO,"Getting the JWTToken for the direct user");
		direct_user_validToken = spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The token is :"+ direct_user_validToken );

		//create another direct organization

	
		//login with csr_admin user
		test.log(LogStatus.INFO,"Logging with the csr admin user");
		spogServer.userLogin(csrAdminUserName,csrAdminPassword);

		//getting the JwtToken for csr_admin 
		csr_Token=spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The csr_token:"+csr_Token);

		//create  msp_organization

		test.log(LogStatus.INFO,"create  a  msp organization");
		String organization_email_msp=RandomStringUtils.randomAlphanumeric(8)+"2_spog_msp_bharadwaj@arcserve.com";
		mspOrg_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spog_bharadwaj_msp"),SpogConstants.MSP_ORG,organization_email_msp,commonPassword,spogServer.ReturnRandom("bharadwaj_first"),spogServer.ReturnRandom("bharadwaj_last"));

		//login msp_organization 
		spogServer.userLogin(organization_email_msp, commonPassword);
		test.log(LogStatus.INFO,"Logging with msp admin user");

		//get the JWT token for msp user 
		test.log(LogStatus.INFO,"Getting the token for the site");
		msp_token=spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The msp_token:"+msp_token);

		//create a user under the msp_organization

		test.log(LogStatus.INFO,"create a msp_admin under msp org");
		final_msp_user_name_email=RandomStringUtils.randomAlphanumeric(4)+msp_user_name_email;
		msp_user_id = spogServer.createUserAndCheck(final_msp_user_name_email, commonPassword, msp_user_first_name,msp_user_last_name, SpogConstants.MSP_ADMIN, mspOrg_id, test);
		test.log(LogStatus.INFO,"The value of the user_id:"+msp_user_id);

		//login with user  msp_organization
		test.log(LogStatus.INFO,"Loggin with msp organization");
		spogServer.userLogin(final_msp_user_name_email, commonPassword);

		//getting the JwtToken for msp_organization
		msp_user_validToken=spogServer.getJWTToken();   
		test.log(LogStatus.INFO,"The token for msp_admin user: "+msp_user_validToken);

		//create sub_org under msp_organization
		test.log(LogStatus.INFO,"creating sub organization under msp_org");
		account_id = spogServer.createAccountWithCheck(mspOrg_id,spogServer.ReturnRandom("spog_bharadwaj_child"), mspOrg_id, test);

		//create a user under the suborganization

		test.log(LogStatus.INFO,"create direct_admin under msp org");
		account_user_email=RandomStringUtils.randomAlphanumeric(4)+direct_user_name_email;
		direct_user_id_2 = spogServer.createUserAndCheck(account_user_email, commonPassword, direct_user_first_name,direct_user_last_name, SpogConstants.DIRECT_ADMIN, account_id, test);
		test.log(LogStatus.INFO,"The value of the direct_user_id:"+direct_user_id_2);

		//login in with direct user 
		test.log(LogStatus.INFO,"Logging with the user under the msp sub_organization");
		spogServer.userLogin(account_user_email, commonPassword);
		test.log(LogStatus.INFO,"Getting the JWTToken for the direct user");
		direct_user_validToken_2 = spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The token is direct_user_1:"+ direct_user_validToken_2 );

		}


	@DataProvider(name = "create_cloud_account_valid")
	public final Object[][] createCloudAccountValidParams() {
		return new Object[][] {
				// different organization_type / cloud_account_type
				    {direct_user_validToken,final_direct_user_name_email,commonPassword, UUID.randomUUID().toString(), "cloudAccountSecret", "cloudAccountName", "cloud_direct", directOrg_id, "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_","cloud_direct_volume",
					DestinationStatus.running.toString(),"direct_destination","destination_direct_volume","6M","6 Months","0","0","0","0","0","0",23.0,23.0,46.0,volume_type.normal.toString(),"BHAGHA-PCW10","d193e09c-efff-45f7-b929-ea138cd3687b","direct",
					System.currentTimeMillis(),System.currentTimeMillis(),"finished","1",30.00, "3", "10","10", "10","12","7"},
		/*	//	{account_user_email, commonPassword, UUID.randomUUID().toString(), "cloudAccountSecret", "cloudAccountName", "aws_s3", account_id, "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_"},
			    {direct_user_validToken,final_direct_user_name_email,commonPassword,UUID.randomUUID().toString(), "cloudAccountSecret", "cloudAccountName", "aws_s3", directOrg_id, "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_"},
				{msp_user_validToken,final_msp_user_name_email, commonPassword,UUID.randomUUID().toString(),"cloudAccountSecret","CloudAccountData", "cloud_direct", mspOrg_id, "SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_"} 		
	*/		};
	}

	@Test(dataProvider = "create_cloud_account_valid")
	public void createCloudAccountValid(String admin_Token,String userName, String password, String cloudAccountKey,String cloudAccountSecret, String cloudAccountName, String cloudAccountType, String organizationID,
			String orderID, String fulfillmentID,String DestinationType,String destination_status,String destination_name,String cloud_direct_volume_name,String retention_id,String retention_name,String age_hours_max ,
			String age_four_hours_max,String age_days_max , String 	age_weeks_max, String age_months_max,String age_years_max , Double primary_usage,Double  snapshot_usage,Double total_usage,String volume_type,String hostname,
			String datacenter_id,String orgType,Long start_time_ts,Long endTimeTS,String status,String job_seq, Double percent_complete, String protected_data_size, String raw_data_size, String sync_read_size,String ntfs_volume_size,
			String virtual_disk_provision_size,String dedupe_savings) {
		HashMap<String,Object> expected_volumes=new HashMap<String,Object>();
		expected_volumes.put("usage",0);expected_volumes.put("count",0);expected_volumes.put("capacity",0);
		int count=0;
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		test.assignAuthor("Bharadwaj.Ghadaim");	 
		spogServer.setToken(admin_Token);
		String prefix = RandomStringUtils.randomAlphanumeric(8);
		String create_user_id=spogServer.GetLoggedinUser_UserID(); 
	
		if (!(null == orderID) && !("" == orderID) && !(orderID.equalsIgnoreCase("none"))) {
			orderID = orderID + prefix;
		}

		if (!(null == fulfillmentID) && !("" == fulfillmentID) && !(fulfillmentID.equalsIgnoreCase("none"))) {
			fulfillmentID = fulfillmentID + prefix;
		}
		if(cloudAccountKey!=""&&cloudAccountSecret!=""){
			cloudAccountKey=RandomStringUtils.randomAlphanumeric(8)+cloudAccountKey;
			cloudAccountSecret=RandomStringUtils.randomAlphanumeric(8)+cloudAccountSecret;
		}
		cloudAccountName=RandomStringUtils.randomAlphanumeric(8)+cloudAccountName;
		String cloud_account_id=null;
		if (organizationID == null || organizationID == "" || organizationID.equalsIgnoreCase("none")) {
			cloud_account_id=spogServer.createCloudAccountWithCheck(cloudAccountKey, cloudAccountSecret, cloudAccountName,
					cloudAccountType, organizationID, orderID, fulfillmentID,datacenter_id, test);
		} else {
			cloud_account_id=spogServer.createCloudAccountWithCheck(cloudAccountKey, cloudAccountSecret, cloudAccountName,
					cloudAccountType, organizationID, orderID, fulfillmentID, datacenter_id,test);
		}
		 if(orgType.equals("direct")) 
			 directAccounts.add(cloud_account_id);
		 else if(orgType.equals("msp"))
			 mspAccounts.add(cloud_account_id);
		 
		test.log(LogStatus.INFO,"Get the cloud accounts for the specified id before creating any cloud_direct_volumes");
	    response=spogServer.getCloudAccountById(admin_Token, cloud_account_id, test);
		spogServer.checkGetCloudAccountById(response, SpogConstants.SUCCESS_GET_PUT_DELETE, cloud_account_id,expected_volumes, organizationID,cloudAccountKey,create_user_id, cloudAccountName,cloudAccountType, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

		//create a destination of type cloud_direct_volume 
		test.log(LogStatus.INFO,"creating a destination of type :"+DestinationType);
		retention= spogDestinationServer.composeRetention(age_hours_max,age_four_hours_max,age_days_max ,age_weeks_max,age_months_max,age_years_max);
		cloud_direct_volume=spogDestinationServer.composeCloudDirectInfo(cloud_account_id,cloud_direct_volume_name,retention_id,retention_name,primary_usage,snapshot_usage,total_usage,volume_type,hostname,retention);	
		test.log(LogStatus.INFO,"Creating a destination of type cloud_direct_volume");
		Response response=spogDestinationServer.createDestination(UUID.randomUUID().toString(),admin_Token,cloud_account_id,organizationID,cloud_account_id,datacenter_id, dedupe_savings,DestinationType,destination_status,destination_name,cloud_direct_volume,test);	
		expected_volumes.put("count",++count);
		String destination_id=response.then().extract().path("data.destination_id");
		test.log(LogStatus.INFO,"The value of the destination_id :"+destination_id);
		
	
		test.log(LogStatus.INFO,"Get the cloud accounts for the specified id");
	    response=spogServer.getCloudAccountById(admin_Token, cloud_account_id, test);

		//validating the response for the get the getCloudAccountForSpecifedId
		test.log(LogStatus.INFO,"validating the response for the getCloudAccountsBySpecifiedId");
		if(cloudAccountType.equals("cloud_direct")){
			spogServer.checkGetCloudAccountById(response, SpogConstants.SUCCESS_GET_PUT_DELETE, cloud_account_id,expected_volumes, organizationID,cloudAccountKey,create_user_id, cloudAccountName,cloudAccountType, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		}else{
			spogServer.checkGetCloudAccountById(response, SpogConstants.SUCCESS_GET_PUT_DELETE, cloud_account_id,null, organizationID,cloudAccountKey,create_user_id, cloudAccountName,cloudAccountType, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		}
		
		
		//Posting another destination and validating the count details
	    response=spogDestinationServer.createDestination(UUID.randomUUID().toString(),admin_Token,cloud_account_id,organizationID,cloud_account_id,datacenter_id, dedupe_savings,DestinationType,destination_status,destination_name+"value",cloud_direct_volume,test);	
		expected_volumes.put("count",++count);
	    destination_id=response.then().extract().path("data.destination_id");		
		test.log(LogStatus.INFO,"The value of the destination_id :"+destination_id);
	
	    test.log(LogStatus.INFO,"Get the cloud accounts for the specified id");
	    response=spogServer.getCloudAccountById(admin_Token, cloud_account_id, test);

		//validating the response for the get the getCloudAccountForSpecifedId
		test.log(LogStatus.INFO,"validating the response for the getCloudAccountsBySpecifiedId");
		if(cloudAccountType.equals("cloud_direct")){
			spogServer.checkGetCloudAccountById(response, SpogConstants.SUCCESS_GET_PUT_DELETE, cloud_account_id,expected_volumes, organizationID,cloudAccountKey,create_user_id, cloudAccountName,cloudAccountType, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		}else{
			spogServer.checkGetCloudAccountById(response, SpogConstants.SUCCESS_GET_PUT_DELETE, cloud_account_id,null, organizationID,cloudAccountKey,create_user_id, cloudAccountName,cloudAccountType, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		}

	}

	/*@DataProvider(name = "get_cloud_account_valid")
	public final Object[][] getCloudAccountValidParams() {
		return new Object[][] {
				{final_direct_user_name_email,commonPassword,"","","CloudAccountData", "cloud_direct", directOrg_id},
				{account_user_email, commonPassword,"cloudAccountKey","cloudAccountSecret","CloudAccountData", "cloud_direct",  account_id},
				{final_msp_user_name_email, commonPassword,"","","CloudAccountData", "cloud_direct", mspOrg_id},
				{final_direct_user_name_email,commonPassword,"cloudAccountKey","cloudAccountSecret","CloudAccountData", "aws_s3", directOrg_id},
				{account_user_email, commonPassword,"cloudAccountKey","cloudAccountSecret","CloudAccountData", "aws_s3",  account_id},
				{final_msp_user_name_email, commonPassword,"cloudAccountKey","cloudAccountSecret","CloudAccountData", "aws_s3", mspOrg_id} 
		};
	}


  //valid jwt token

	@Test(dataProvider = "get_cloud_account_valid")
	public void GetCloudAccountValidtoken(String userName, String password,String cloudAccountKey,String cloudAccountSecret, String cloudAccountName, String cloudAccountType, String organization_id) {

		//creating the test object for the log information
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		test.assignAuthor("Bharadwaj.Ghadaim");	 

		//Login with specific user 
		test.log(LogStatus.INFO,"admin login");
		spogServer.userLogin(userName, password);

		//get the JWTToken for the Logged in user 
		String Token=spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The value of the Token for the direct user:"+Token);

		//Get the Logged in user
		String create_user_id=spogServer.GetLoggedinUser_UserID(); 

	     //parameters for the creation of the cloudAccounts
		test.log(LogStatus.INFO,"creating a cloud account");
		if(cloudAccountKey!=""&&cloudAccountSecret!=""){
		cloudAccountKey=RandomStringUtils.randomAlphanumeric(8)+cloudAccountKey;
		cloudAccountSecret=RandomStringUtils.randomAlphanumeric(8)+cloudAccountSecret;
		}
		cloudAccountName=RandomStringUtils.randomAlphanumeric(8)+cloudAccountName;
		String cloud_account_id=spogServer.createCloudAccountWithCheck( cloudAccountKey, cloudAccountSecret, cloudAccountName, cloudAccountType, organization_id, test);

		//get the cloud account for the specified id
		test.log(LogStatus.INFO,"Get the cloud accounts for the specified id");
		Response response=spogServer.getCloudAccountById(Token, cloud_account_id, test);

		//validating the response for the get the getCloudAccountForSpecifedId
		test.log(LogStatus.INFO,"validating the response for the getCloudAccountsBySpecifiedId");
		spogServer.checkGetCloudAccountById(response, SpogConstants.SUCCESS_GET_PUT_DELETE, cloud_account_id, organization_id,cloudAccountKey, create_user_id, cloudAccountName,cloudAccountType, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
	}


	//missing jwt token

	@Test(dataProvider = "get_cloud_account_valid")
	public void GetCloudAccountMissingToken(String userName, String password,String cloudAccountKey,String cloudAccountSecret, String cloudAccountName, String cloudAccountType, String organization_id) {

		//creating the test object for the log information
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		test.assignAuthor("Bharadwaj.Ghadaim");	 

		//Login with specific user 
 		test.log(LogStatus.INFO,"admin login");
         spogServer.userLogin(userName, password); 

		//get the JWTToken for the Logged in user 
		String Token=spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The value of the Token for the direct user:"+Token);

		//Get the Logged in user
		String create_user_id=spogServer.GetLoggedinUser_UserID(); 

	     //parameters for the creation of the cloudAccounts
		test.log(LogStatus.INFO,"creating a cloud account");
		cloudAccountKey=RandomStringUtils.randomAlphanumeric(8)+cloudAccountKey;
		cloudAccountSecret=RandomStringUtils.randomAlphanumeric(8)+cloudAccountSecret;
		cloudAccountName=RandomStringUtils.randomAlphanumeric(8)+cloudAccountName;
		String cloud_account_id=spogServer.createCloudAccountWithCheck( cloudAccountKey, cloudAccountSecret, cloudAccountName, cloudAccountType, organization_id, test);

		//get the cloud account for the specified id
		test.log(LogStatus.INFO,"Get the cloud accounts for the specified id");
		Response response=spogServer.getCloudAccountById("", cloud_account_id, test);

		//validating the response for the get the getCloudAccountForSpecifedId
		test.log(LogStatus.INFO,"validating the response for the getCloudAccountsBySpecifiedId");
		spogServer.checkGetCloudAccountById(response,SpogConstants.NOT_LOGGED_IN, cloud_account_id, organization_id,cloudAccountKey, create_user_id,cloudAccountName,cloudAccountType,SpogMessageCode.COMMON_AUTHENTICATION_FAILED ,test);
	}



	//invalidtoken


	@Test(dataProvider = "get_cloud_account_valid")
	public void GetCloudAccountinvalidToken(String userName, String password,String cloudAccountKey,String cloudAccountSecret, String cloudAccountName, String cloudAccountType, String organization_id) {

		//creating the test object for the log information
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		test.assignAuthor("SYKAM.NAGAMALLESWARI");	 

		//Login with specific user 
		test.log(LogStatus.INFO,"admin login");
		spogServer.userLogin(userName, password);

		//get the JWTToken for the Logged in user 
		String Token=spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The value of the Token for the direct user:"+Token);

		//Get the Logged in user
		String create_user_id=spogServer.GetLoggedinUser_UserID(); 

	    //parameters for the creation of the cloudAccounts
		test.log(LogStatus.INFO,"creating a cloud account");
		cloudAccountKey=RandomStringUtils.randomAlphanumeric(8)+cloudAccountKey;
		cloudAccountSecret=RandomStringUtils.randomAlphanumeric(8)+cloudAccountSecret;
		cloudAccountName=RandomStringUtils.randomAlphanumeric(8)+cloudAccountName;
		String cloud_account_id=spogServer.createCloudAccountWithCheck( cloudAccountKey, cloudAccountSecret, cloudAccountName, cloudAccountType, organization_id, test);

		//get the cloud account for the specified id
		test.log(LogStatus.INFO,"Get the cloud accounts for the specified id");
		Response response=spogServer.getCloudAccountById(Token+"junk message", cloud_account_id, test);

		//validating the response for the get the getCloudAccountForSpecifedId
		test.log(LogStatus.INFO,"validating the response for the getCloudAccountsBySpecifiedId");
		spogServer.checkGetCloudAccountById(response,SpogConstants.NOT_LOGGED_IN, cloud_account_id, organization_id,cloudAccountKey, create_user_id,cloudAccountName,cloudAccountType,SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);
	}

	//getcloudaccountsbyspecified  by csr token



	@Test(dataProvider = "get_cloud_account_valid")
	public void GetCloudAccountValid_csr_token(String userName, String password,String cloudAccountKey,String cloudAccountSecret, String cloudAccountName, String cloudAccountType, String organization_id) {

		//creating the test object for the log information
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		test.assignAuthor("Bharadwaj.Ghadaim");	 

		//Login with specific user 
		test.log(LogStatus.INFO,"admin login");
		spogServer.userLogin(userName, password);

		//get the JWTToken for the Logged in user 
		String Token=spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The value of the Token for the direct user:"+Token);

		//Get the Logged in user
		String create_user_id=spogServer.GetLoggedinUser_UserID(); 

	    //parameters for the creation of the cloudAccounts
		test.log(LogStatus.INFO,"creating a cloud account");
		cloudAccountKey=RandomStringUtils.randomAlphanumeric(8)+cloudAccountKey;
		cloudAccountSecret=RandomStringUtils.randomAlphanumeric(8)+cloudAccountSecret;
		cloudAccountName=RandomStringUtils.randomAlphanumeric(8)+cloudAccountName;
		String cloud_account_id=spogServer.createCloudAccountWithCheck( cloudAccountKey, cloudAccountSecret, cloudAccountName, cloudAccountType, organization_id, test);


		//login with csr_admin user       
		spogServer.userLogin(csrAdminUserName,csrAdminPassword);

		//getting the JwtToken for csr_admin 
		csr_Token=spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The csr_token:"+csr_Token);

		//get the cloud account for the specified id
		test.log(LogStatus.INFO,"Get the cloud accounts for the specified id");
		Response response=spogServer.getCloudAccountById(csr_Token, cloud_account_id, test);

		//validating the response for the get the getCloudAccountForSpecifedId
		test.log(LogStatus.INFO,"validating the response for the getCloudAccountsBySpecifiedId");
		spogServer.checkGetCloudAccountById(response,SpogConstants.SUCCESS_GET_PUT_DELETE, cloud_account_id, organization_id,cloudAccountKey, create_user_id,cloudAccountName,cloudAccountType,SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
	}


	//Getcloudaccountbyspecifedid mutipletimes with the valid jwt token -200


	@Test(dataProvider = "get_cloud_account_valid")
	public void GetCloudAccountValid_multpletimes(String userName, String password, String cloudAccountKey,String cloudAccountSecret, String cloudAccountName,String cloudAccountType, String organization_id) {

		//creating the test object for the log information
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		test.assignAuthor("Bharadwaj.Ghadaim");	 

		//Login with specific user 
		test.log(LogStatus.INFO,"admin login");
		spogServer.userLogin(userName, password);

		//get the JWTToken for the Logged in user 
		String Token=spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The value of the Token for the direct user:"+Token);

		//Get the Logged in user
		String create_user_id=spogServer.GetLoggedinUser_UserID(); 

	    //parameters for the creation of the cloudAccounts
		test.log(LogStatus.INFO,"creating a cloud account");
		cloudAccountKey=RandomStringUtils.randomAlphanumeric(8)+cloudAccountKey;
		cloudAccountSecret=RandomStringUtils.randomAlphanumeric(8)+cloudAccountSecret;
		cloudAccountName=RandomStringUtils.randomAlphanumeric(8)+cloudAccountName;
		String cloud_account_id=spogServer.createCloudAccountWithCheck( cloudAccountKey, cloudAccountSecret, cloudAccountName, cloudAccountType, organization_id, test);

		//get the cloud account for the specified id
		test.log(LogStatus.INFO,"Get the cloud accounts for the specified id");
		Response response=spogServer.getCloudAccountById(Token, cloud_account_id, test);

		//validating the response for the get the getCloudAccountForSpecifedId
		test.log(LogStatus.INFO,"validating the response for the getCloudAccountsBySpecifiedId");
		spogServer.checkGetCloudAccountById(response,SpogConstants.SUCCESS_GET_PUT_DELETE, cloud_account_id, organization_id,cloudAccountKey, create_user_id,cloudAccountName,cloudAccountType,SpogMessageCode.SUCCESS_GET_PUT_DEL, test);



		//validating the response for the get the getCloudAccountForSpecifedId
		test.log(LogStatus.INFO,"validating the response for the getCloudAccountsBySpecifiedId");
		spogServer.checkGetCloudAccountById(response,SpogConstants.SUCCESS_GET_PUT_DELETE, cloud_account_id, organization_id,cloudAccountKey, create_user_id,cloudAccountName,cloudAccountType,SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

		//validating the response for the get the getCloudAccountForSpecifedId
		test.log(LogStatus.INFO,"validating the response for the getCloudAccountsBySpecifiedId");
		spogServer.checkGetCloudAccountById(response,SpogConstants.SUCCESS_GET_PUT_DELETE, cloud_account_id, organization_id,cloudAccountKey, create_user_id,cloudAccountName,cloudAccountType,SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

		//validating the response for the get the getCloudAccountForSpecifedId
		test.log(LogStatus.INFO,"validating the response for the getCloudAccountsBySpecifiedId");
		spogServer.checkGetCloudAccountById(response,SpogConstants.SUCCESS_GET_PUT_DELETE, cloud_account_id, organization_id,cloudAccountKey, create_user_id,cloudAccountName,cloudAccountType,SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
	}



	//getglouaccountby specifiedid multiple times with the csr token
	@Test(dataProvider = "get_cloud_account_valid")
	public void GetCloudAccountValid_multpletimes_with_csr_token(String userName, String password, String cloudAccountKey,String cloudAccountSecret, String cloudAccountName,String cloudAccountType, String organization_id) {

		//creating the test object for the log information
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		test.assignAuthor("Bharadwaj.Ghadaim");	 

		//Login with specific user 
		test.log(LogStatus.INFO,"admin login");
		spogServer.userLogin(userName, password);

		//get the JWTToken for the Logged in user 
		String Token=spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The value of the Token for the direct user:"+Token);

		//Get the Logged in user
		String create_user_id=spogServer.GetLoggedinUser_UserID(); 

	    //parameters for the creation of the cloudAccounts
		test.log(LogStatus.INFO,"creating a cloud account");
		cloudAccountKey=RandomStringUtils.randomAlphanumeric(8)+cloudAccountKey;
		cloudAccountSecret=RandomStringUtils.randomAlphanumeric(8)+cloudAccountSecret;
		cloudAccountName=RandomStringUtils.randomAlphanumeric(8)+cloudAccountName;
		String cloud_account_id=spogServer.createCloudAccountWithCheck( cloudAccountKey, cloudAccountSecret, cloudAccountName, cloudAccountType, organization_id, test);

		//get the cloud account for the specified id
		test.log(LogStatus.INFO,"Get the cloud accounts for the specified id");
		Response response=spogServer.getCloudAccountById(csr_Token, cloud_account_id, test);

		//validating the response for the get the getCloudAccountForSpecifedId
		test.log(LogStatus.INFO,"validating the response for the getCloudAccountsBySpecifiedId");
		spogServer.checkGetCloudAccountById(response,SpogConstants.SUCCESS_GET_PUT_DELETE, cloud_account_id, organization_id,cloudAccountKey, create_user_id,cloudAccountName,cloudAccountType,SpogMessageCode.SUCCESS_GET_PUT_DEL, test);



	   //validating the response for the get the getCloudAccountForSpecifedId
		test.log(LogStatus.INFO,"validating the response for the getCloudAccountsBySpecifiedId");
		spogServer.checkGetCloudAccountById(response,SpogConstants.SUCCESS_GET_PUT_DELETE, cloud_account_id, organization_id,cloudAccountKey, create_user_id,cloudAccountName,cloudAccountType,SpogMessageCode.SUCCESS_GET_PUT_DEL, test);


	   //validating the response for the get the getCloudAccountForSpecifedId
		test.log(LogStatus.INFO,"validating the response for the getCloudAccountsBySpecifiedId");
		spogServer.checkGetCloudAccountById(response,SpogConstants.SUCCESS_GET_PUT_DELETE, cloud_account_id, organization_id,cloudAccountKey, create_user_id,cloudAccountName,cloudAccountType,SpogMessageCode.SUCCESS_GET_PUT_DEL, test);


	   //validating the response for the get the getCloudAccountForSpecifedId
		test.log(LogStatus.INFO,"validating the response for the getCloudAccountsBySpecifiedId");
		spogServer.checkGetCloudAccountById(response,SpogConstants.SUCCESS_GET_PUT_DELETE, cloud_account_id, organization_id,cloudAccountKey, create_user_id,cloudAccountName,cloudAccountType,SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
	}


	//given invalid cloud_account_id --400



		@Test(dataProvider = "get_cloud_account_valid")
		public void GetCloudAccount_by_invalid_cloud_id(String userName, String password,String cloudAccountKey,String cloudAccountSecret, String cloudAccountName, String cloudAccountType, String organization_id) {

			//creating the test object for the log information
			test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
			test.assignAuthor("Bharadwaj.Ghadaim");	 

			//Login with specific user 
			test.log(LogStatus.INFO,"admin login");
			spogServer.userLogin(userName, password);

			//get the JWTToken for the Logged in user 
			String Token=spogServer.getJWTToken();
			test.log(LogStatus.INFO,"The value of the Token for the direct user:"+Token);

			//Get the Logged in user
			String create_user_id=spogServer.GetLoggedinUser_UserID(); 

			Response response=spogServer.getCloudAccountById(Token, cloud_account_id1, test);

			//validating the response for the get the getCloudAccountForSpecifedId
			test.log(LogStatus.INFO,"validating the response for the getCloudAccountsBySpecifiedId");
			spogServer.checkGetCloudAccountById(response,SpogConstants.REQUIRED_INFO_NOT_EXIST, cloud_account_id1, organization_id,cloudAccountKey, create_user_id,cloudAccountName,cloudAccountType,SpogMessageCode.NON_EXIST_CLOUD_ID, test);
		}




		//given invalid cloud_account_id  given csr user token -400



		@Test(dataProvider = "get_cloud_account_valid")
		public void GetCloudAccount_invalidcoudaccount_csrtoken(String userName, String password,String cloudAccountKey,String cloudAccountSecret, String cloudAccountName, String cloudAccountType, String organization_id) {

			//creating the test object for the log information
			test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
			test.assignAuthor("Bharadwaj.Ghadaim");	 

			//Login with specific user 
			test.log(LogStatus.INFO,"admin login");
			spogServer.userLogin(userName, password);

			//get the JWTToken for the Logged in user 
			String Token=spogServer.getJWTToken();
			test.log(LogStatus.INFO,"The value of the Token for the direct user:"+Token);

			//Get the Logged in user
			String create_user_id=spogServer.GetLoggedinUser_UserID(); 

			//parameters for the creation of the cloudAccounts
			//test.log(LogStatus.INFO,"creating a cloud account");
			//cloudAccountKey=RandomStringUtils.randomAlphanumeric(8)+cloudAccountKey;
			//cloudAccountSecret=RandomStringUtils.randomAlphanumeric(8)+cloudAccountSecret;
			//cloudAccountName=RandomStringUtils.randomAlphanumeric(8)+cloudAccountName;
			// cloud_account_id=spogServer.createCloudAccountWithCheck( cloudAccountKey, cloudAccountSecret, cloudAccountName, cloudAccountType, organization_id, test);


			//login with csr_admin user       
			spogServer.userLogin(csrAdminUserName,csrAdminPassword);

			//getting the JwtToken for csr_admin 
			csr_Token=spogServer.getJWTToken();
			test.log(LogStatus.INFO,"The csr_token:"+csr_Token);

			//get the cloud account for the specified id
			//test.log(LogStatus.INFO,"Get the cloud accounts for the specified id");
			Response response=spogServer.getCloudAccountById(csr_Token, cloud_account_id1, test);

			//validating the response for the get the getCloudAccountForSpecifedId
			test.log(LogStatus.INFO,"validating the response for the getCloudAccountsBySpecifiedId");
			spogServer.checkGetCloudAccountById(response,SpogConstants.REQUIRED_INFO_NOT_EXIST, cloud_account_id1, organization_id,cloudAccountKey, create_user_id,cloudAccountName,cloudAccountType,SpogMessageCode.NON_EXIST_CLOUD_ID, test);
		}





	//get the deleted cloudaccountbyspecifed id given valid token--404 error

		@Test(dataProvider = "get_cloud_account_valid")
		public void GetCloudAccountValid_get_the_deleted_account(String userName, String password,String cloudAccountKey,String cloudAccountSecret, String cloudAccountName, String cloudAccountType, String organization_id) {

			//creating the test object for the log information
			test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
			test.assignAuthor("Bharadwaj.Ghadaim");	 

			//Login with specific user 
			test.log(LogStatus.INFO,"admin login");
			spogServer.userLogin(userName, password);

			//get the JWTToken for the Logged in user 
			String Token=spogServer.getJWTToken();
			test.log(LogStatus.INFO,"The value of the Token for the direct user:"+Token);

			//Get the Logged in user
			String create_user_id=spogServer.GetLoggedinUser_UserID(); 

			//parameters for the creation of the cloudAccounts
			test.log(LogStatus.INFO,"creating a cloud account");
			cloudAccountKey=RandomStringUtils.randomAlphanumeric(8)+cloudAccountKey;
			cloudAccountSecret=RandomStringUtils.randomAlphanumeric(8)+cloudAccountSecret;
			cloudAccountName=RandomStringUtils.randomAlphanumeric(8)+cloudAccountName;
			String cloud_account_id=spogServer.createCloudAccountWithCheck( cloudAccountKey, cloudAccountSecret, cloudAccountName, cloudAccountType, organization_id, test);


			//get the cloud account for the specified id
			test.log(LogStatus.INFO,"Get the cloud accounts for the specified id");
			Response response=spogServer.getCloudAccountById(Token, cloud_account_id, test);

			//validating the response for the get the getCloudAccountForSpecifedId
			test.log(LogStatus.INFO,"validating the response for the getCloudAccountsBySpecifiedId");
			spogServer.checkGetCloudAccountById(response,SpogConstants.SUCCESS_GET_PUT_DELETE, cloud_account_id, organization_id,cloudAccountKey, create_user_id,cloudAccountName,cloudAccountType,SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			//delete the cloud account
			test.log(LogStatus.INFO,"delete the cloudaccount");
			spogServer.deleteCloudAccountWithExpectedStatusCode(cloud_account_id,SpogConstants.SUCCESS_GET_PUT_DELETE,test);


			//get the cloud account for the specified id
			test.log(LogStatus.INFO,"Get the cloud accounts for the specified id");
			Response response2=spogServer.getCloudAccountById(Token, cloud_account_id, test);

			//validating the response for the get the getCloudAccountForSpecifedId
			test.log(LogStatus.INFO,"validating the response for the deleted cloudaccount");
			spogServer.checkGetCloudAccountById(response2,SpogConstants.RESOURCE_NOT_EXIST, cloud_account_id, organization_id,cloudAccountKey, create_user_id,cloudAccountName,cloudAccountType,SpogMessageCode.RESOURCE_NOT_FOUNDED, test);
		}




		//get the deleted cloudaccountbyspecifed id given csr  token--404 error

		@Test(dataProvider = "get_cloud_account_valid")
		public void GetCloudAccountValid_get_the_deleted_account_csr_token(String userName, String password,String cloudAccountKey,String cloudAccountSecret, String cloudAccountName, String cloudAccountType, String organization_id)
		{

			//creating the test object for the log information
			test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
			test.assignAuthor("Bharadwaj.Ghadaim");	 

			//Login with specific user 
			test.log(LogStatus.INFO,"admin login");
			spogServer.userLogin(userName, password);

			//get the JWTToken for the Logged in user 
			String Token=spogServer.getJWTToken();
			test.log(LogStatus.INFO,"The value of the Token for the direct user:"+Token);

			//Get the Logged in user
			String create_user_id=spogServer.GetLoggedinUser_UserID(); 

			//parameters for the creation of the cloudAccounts
			test.log(LogStatus.INFO,"creating a cloud account");
			cloudAccountKey=RandomStringUtils.randomAlphanumeric(8)+cloudAccountKey;
			cloudAccountSecret=RandomStringUtils.randomAlphanumeric(8)+cloudAccountSecret;
			cloudAccountName=RandomStringUtils.randomAlphanumeric(8)+cloudAccountName;
			String cloud_account_id=spogServer.createCloudAccountWithCheck( cloudAccountKey, cloudAccountSecret, cloudAccountName, cloudAccountType, organization_id, test);




			//get the cloud account for the specified id
			test.log(LogStatus.INFO,"Get the cloud accounts for the specified id");
			Response response=spogServer.getCloudAccountById(Token, cloud_account_id, test);

			//validating the response for the get the getCloudAccountForSpecifedId
			test.log(LogStatus.INFO,"validating the response for the getCloudAccountsBySpecifiedId");
			spogServer.checkGetCloudAccountById(response,SpogConstants.SUCCESS_GET_PUT_DELETE, cloud_account_id, organization_id,cloudAccountKey, create_user_id,cloudAccountName,cloudAccountType,SpogMessageCode.SUCCESS_GET_PUT_DEL, test);

			//delete the cloud account
			test.log(LogStatus.INFO,"delete the cloudaccount");
			spogServer.deleteCloudAccountWithExpectedStatusCode(cloud_account_id,SpogConstants.SUCCESS_GET_PUT_DELETE,test);


			//get the cloud account for the specified id
			test.log(LogStatus.INFO,"Get the cloud accounts for the specified id");
			Response response2=spogServer.getCloudAccountById(csr_Token, cloud_account_id, test);

			//validating the response for the get the getCloudAccountForSpecifedId
			test.log(LogStatus.INFO,"validating the response for the deleted cloudaccount");
			spogServer.checkGetCloudAccountById(response2,SpogConstants.RESOURCE_NOT_EXIST, cloud_account_id, organization_id,cloudAccountKey, create_user_id,cloudAccountName,cloudAccountType,SpogMessageCode.RESOURCE_NOT_FOUNDED, test);
		}




  //GetCloudAccountValid_another_direct_token-403


		@Test(dataProvider = "get_cloud_account_valid")
		public void GetCloudAccountValid_another_direct_token(String userName, String password,String cloudAccountKey,String cloudAccountSecret, String cloudAccountName, String cloudAccountType, String organization_id) {

			//creating the test object for the log information
			test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
			test.assignAuthor("Bharadwaj.Ghadaim");	 

			//Login with specific user 
			test.log(LogStatus.INFO,"admin login");
			spogServer.userLogin(userName, password);

			//get the JWTToken for the Logged in user 
			String Token=spogServer.getJWTToken();
			test.log(LogStatus.INFO,"The value of the Token for the direct user:"+Token);

			//Get the Logged in user
			String create_user_id=spogServer.GetLoggedinUser_UserID(); 

			//parameters for the creation of the cloudAccounts
			test.log(LogStatus.INFO,"creating a cloud account");
			cloudAccountKey=RandomStringUtils.randomAlphanumeric(8)+cloudAccountKey;
			cloudAccountSecret=RandomStringUtils.randomAlphanumeric(8)+cloudAccountSecret;
			cloudAccountName=RandomStringUtils.randomAlphanumeric(8)+cloudAccountName;
			String cloud_account_id=spogServer.createCloudAccountWithCheck( cloudAccountKey, cloudAccountSecret, cloudAccountName, cloudAccountType, organization_id, test);

			//get the cloud account for the specified id
			test.log(LogStatus.INFO,"Get the cloud accounts for the specified id");
			Response response=spogServer.getCloudAccountById(direct_Token_1, cloud_account_id, test);

			//validating the response for the get the getCloudAccountForSpecifedId
			test.log(LogStatus.INFO,"validating the response for the getCloudAccountsBySpecifiedId");
			spogServer.checkGetCloudAccountById(response,SpogConstants.INSUFFICIENT_PERMISSIONS, cloud_account_id, organization_id,cloudAccountKey, create_user_id,cloudAccountName,cloudAccountType,SpogMessageCode.RESOURCE_PERMISSION_DENY, test);
		}

	//GetCloudAccountValid_another_msp_token-403

		@Test(dataProvider = "get_cloud_account_valid")
		public void GetCloudAccountValid_another_msp_token(String userName, String password,String cloudAccountKey,String cloudAccountSecret, String cloudAccountName, String cloudAccountType, String organization_id) {

			//creating the test object for the log information
			test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
			test.assignAuthor("Bharadwaj.Ghadaim");	 

			//Login with specific user 
			test.log(LogStatus.INFO,"admin login");
			spogServer.userLogin(userName, password);

			//get the JWTToken for the Logged in user 
			String Token=spogServer.getJWTToken();
			test.log(LogStatus.INFO,"The value of the Token for the direct user:"+Token);

			//Get the Logged in user
			String create_user_id=spogServer.GetLoggedinUser_UserID();

			//parameters for the creation of the cloudAccounts
			test.log(LogStatus.INFO,"creating a cloud account");
			cloudAccountKey=RandomStringUtils.randomAlphanumeric(8)+cloudAccountKey;
			cloudAccountSecret=RandomStringUtils.randomAlphanumeric(8)+cloudAccountSecret;
			cloudAccountName=RandomStringUtils.randomAlphanumeric(8)+cloudAccountName;
			String cloud_account_id=spogServer.createCloudAccountWithCheck( cloudAccountKey, cloudAccountSecret, cloudAccountName, cloudAccountType, organization_id, test);

			//get the cloud account for the specified id
			test.log(LogStatus.INFO,"Get the cloud accounts for the specified id");
			Response response=spogServer.getCloudAccountById(msp_token_1, cloud_account_id, test);

			//validating the response for the get the getCloudAccountForSpecifedId
			test.log(LogStatus.INFO,"validating the response for the getCloudAccountsBySpecifiedId");
			spogServer.checkGetCloudAccountById(response,SpogConstants.INSUFFICIENT_PERMISSIONS, cloud_account_id, organization_id,cloudAccountKey, create_user_id,cloudAccountName,cloudAccountType,SpogMessageCode.RESOURCE_PERMISSION_DENY, test);
		}
	 */



	//passing the information to the BQ
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
	@AfterTest
	public void aftertest() {
		test.log(LogStatus.INFO, "The total test cases passed are "+count1.getpassedcount());
		test.log(LogStatus.INFO, "the total test cases failed are "+count1.getfailedcount());
		rep.flush();
	}

	@AfterClass
	public void updatebd() {
		try {
			if(count1.getfailedcount()>0) {
				Nooftest=count1.getpassedcount()+count1.getfailedcount()+count1.getskippedcount();
				bqdb1.updateTable(BQName, this.runningMachine, this.buildVersion, String.valueOf(Nooftest), Integer.toString(count1.getpassedcount()), Integer.toString(count1.getfailedcount()), String.valueOf(count1.getskippedcount()), count1.getcreationtime(), "Failed");
			}else {
				Nooftest=count1.getpassedcount()+count1.getfailedcount()+count1.getskippedcount();
				bqdb1.updateTable(BQName, this.runningMachine, this.buildVersion, String.valueOf(Nooftest), Integer.toString(count1.getpassedcount()), Integer.toString(count1.getfailedcount()), String.valueOf(count1.getskippedcount()), count1.getcreationtime(), "Passed");
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
