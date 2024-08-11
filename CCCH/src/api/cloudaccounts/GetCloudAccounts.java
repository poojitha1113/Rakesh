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
import java.util.UUID;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.codehaus.groovy.ast.tools.ClosureUtils;
import org.testng.ITestResult;
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
import InvokerServer.SPOGServer;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class GetCloudAccounts {
	private int curr_page=1, page_size=20;
	private SPOGServer spogServer;
	private GatewayServer gatewayServer;
	private ExtentReports rep;
	private ExtentTest test;
	private Response response;
	private String commonPassword="Mclaren@2013";

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


	//csr_organization
	private String csrAdminUserName;
	private String csrAdminPassword;
	private String prefix_csr = "spog_csr_bharadwaj";
	private String csr_user_name = prefix_csr + "_admin";
	/*private String csr_user_name_email = prefix_csr + "_admin" + postfix_email;
	private String csr_user_first_name = csr_user_name + "_first_name";
	private String csr_user_last_name = csr_user_name + "_last_name";*/
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
	
	//Related to the cloudAccounts
	ArrayList<HashMap<String,Object>> cloudDatasDirect=new ArrayList<HashMap<String,Object>>();
	HashMap<String,Object> cloudData=new HashMap<String,Object>();
	
	//Related to the MSP organization	
	ArrayList<HashMap<String,Object>> cloudDatasMsp=new ArrayList<HashMap<String,Object>>();
	
	//Related to the MSP and sub organization
	ArrayList<HashMap<String,Object>> cloudDatasMsp_subOrg=new ArrayList<HashMap<String,Object>>();

	//related to the subOrganization
	ArrayList<HashMap<String,Object>> cloudDatasSubOrg=new ArrayList<HashMap<String,Object>>();
	
	//related to the csr_Admin
	ArrayList<HashMap<String,Object>> cloudDataUnderCsr=new ArrayList<HashMap<String,Object>>();
	
	//Related  to the cloud Account names
	ArrayList<String> directClouds=new ArrayList<String>();
	
	//Related to the MspCloudAccounts
	ArrayList<String> mspClouds=new ArrayList<String>();
	
	//Related to the SubCloudAccounts
	ArrayList<String> subClouds=new ArrayList<String>();
	@BeforeClass
	@Parameters({ "baseURI", "port","csrAdminUserName", "csrAdminPassword","logFolder","runningMachine","buildVersion"})
	public void beforeClass(String baseURI, String port, String adminUserName, String adminPassword, String logFolder,String runningMachine,String buildVersion) {
		spogServer = new SPOGServer(baseURI, port);
		gatewayServer =new GatewayServer(baseURI,port);
		rep = ExtentManager.getInstance("GetCloudAccounts",logFolder);
		this.csrAdminUserName = adminUserName;
		this.csrAdminPassword = adminPassword;
		Nooftest=0;
		bqdb1 = new SQLServerDb();
		String author="Malleswari";
		count1 = new testcasescount();
		test = rep.startTest("beforeClass");
		test.assignAuthor("MalleswariSykam");

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

		//get the logged in user
		csrOrg_id = spogServer.GetLoggedinUserOrganizationID();
		test.log(LogStatus.INFO,"The value of the csrOrgId:"+csrOrg_id);

		//create a direct organization
		test.log(LogStatus.INFO,"create  a direct organization");
		String organization_email_direct=RandomStringUtils.randomAlphanumeric(8)+"_spog_direct_bharadwaj@arcserve.com";
		directOrg_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spog_bharadwaj_direct"),SpogConstants.DIRECT_ORG,organization_email_direct,commonPassword,spogServer.ReturnRandom("bharadwaj_first"),spogServer.ReturnRandom("bharadwaj_last"));

		//login direct organization 
		test.log(LogStatus.INFO,"Logging with the csr admin user");
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
        spogServer.setToken(csr_Token);
		test.log(LogStatus.INFO,"create  a direct organization");
		String organization_email_direct_1=RandomStringUtils.randomAlphanumeric(8)+"1_spog_direct_bharadwaj@arcserve.com";
		directOrg_id2 = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spog_bharadwaj_direct"),SpogConstants.DIRECT_ORG,organization_email_direct_1,commonPassword,spogServer.ReturnRandom("bharadwaj_first"),spogServer.ReturnRandom("bharadwaj_last"));

		//login direct organization 
		spogServer.userLogin(organization_email_direct_1, commonPassword);
		test.log(LogStatus.INFO,"Logging with the direct admin user");

		//get the JWT token for direct user 
		direct_Token_1=spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The direct_token:"+direct_Token_1);

		
		
		//create  msp_organization
		 spogServer.setToken(csr_Token);
		test.log(LogStatus.INFO,"create  a  msp organization");
		String organization_email_msp=RandomStringUtils.randomAlphanumeric(8)+"2_spog_msp_bharadwaj@arcserve.com";
		mspOrg_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spog_bharadwaj_msp"),SpogConstants.MSP_ORG,organization_email_msp,commonPassword,spogServer.ReturnRandom("bharadwaj_first"),spogServer.ReturnRandom("bharadwaj_last"));

		//login msp_organization 
		spogServer.userLogin(organization_email_msp, commonPassword);
		test.log(LogStatus.INFO,"Logging with msp admin user");

		//get the JWT token for direct user 
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

		//create another  msp_organization
		 spogServer.setToken(csr_Token);
		test.log(LogStatus.INFO,"create a msp  organization");
		String organization_email_msp_1=RandomStringUtils.randomAlphanumeric(8)+"3_spog_msp_bharadwaj@arcserve.com";
		mspOrg2_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spog_bharadwaj_msp"),SpogConstants.MSP_ORG,organization_email_msp_1,commonPassword,spogServer.ReturnRandom("bharadwaj_first"),spogServer.ReturnRandom("bharadwaj_last"));

		//login msp_organization 
		spogServer.userLogin(organization_email_msp_1, commonPassword);

		//get the JWT token for direct user 
		msp_token_1=spogServer.getJWTToken();
		test.log(LogStatus.INFO,"The msp_token:"+msp_token_1);
	}

	//Data Provider For posting the cloudAccountsData
	@DataProvider(name = "get_cloud_account_valid")
	public final Object[][] getCloudAccountValidParams() {
		return new Object[][] {
				{direct_user_validToken, "cloud_direct,aws_s3","cloudAccountKeyvaluedirect","cloudAccountSecretvaluedirect","cloudAccountNamedirect",directOrg_id,"SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_","cloud_direct_volume",
				DestinationStatus.running.toString(),"direct_destination","destination_direct_volume","6M","6 Months","0","0","0","0","0","0",23.0,23.0,46.0,volume_type.normal.toString(),"BHAGHA-PCW10","d193e09c-efff-45f7-b929-ea138cd3687b","direct",
				System.currentTimeMillis(),System.currentTimeMillis(),"finished","1",30.00, "3", "10","10", "10","12","7"},
				{msp_user_validToken,"cloud_direct,aws_s3","cloudAccountKeyvaluemsp","cloudAccountSecretvaluemsp","cloudAccountNamemsp", mspOrg_id,"SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_","cloud_direct_volume",
				DestinationStatus.running.toString(),"direct_destination","destination_direct_volume","6M","6 Months","0","0","0","0","0","0",23.0,23.0,46.0,volume_type.normal.toString(),"BHAGHA-PCW10","d193e09c-efff-45f7-b929-ea138cd3687b","direct",
			    System.currentTimeMillis(),System.currentTimeMillis(),"finished","1",30.00, "3", "10","10", "10","12","7"},
				{direct_user_validToken_2,"aws_s3","cloudAccountKeyvaluesuborg","cloudAccountSecretvaluesuborg","cloudAccountNamesuborg",account_id,"SKUTESTDATA_1_0_0_0_", "SKUTESTDATA_1_0_0_0_","cloud_direct_volume",
				DestinationStatus.running.toString(),"direct_destination","destination_direct_volume","6M","6 Months","0","0","0","0","0","0",23.0,23.0,46.0,volume_type.normal.toString(),"BHAGHA-PCW10","d193e09c-efff-45f7-b929-ea138cd3687b","direct",
				System.currentTimeMillis(),System.currentTimeMillis(),"finished","1",30.00, "3", "10","10", "10","12","7"}
		};
	}

	//valid cases :@response :200
	
	@Test(dataProvider = "get_cloud_account_valid")
	public void PostCloudAccountValid(String admin_token,String cloudAccountType,  String cloudAccountKey,String cloudAccountSecret,String cloudAccountName, String organization_id,String orderID,String fulfillmentID,
	        String DestinationType,String destination_status,String destination_name,String cloud_direct_volume_name,String retention_id,String retention_name,String age_hours_max ,
			String age_four_hours_max,String age_days_max , String 	age_weeks_max, String age_months_max,String age_years_max , Double primary_usage,Double  snapshot_usage,Double total_usage,String volume_type,String hostname,
			String datacenter_id,String orgType,Long start_time_ts,Long endTimeTS,String status,String job_seq, Double percent_complete, String protected_data_size, String raw_data_size, String sync_read_size,String ntfs_volume_size,
			String virtual_disk_provision_size,String dedupe_savings) {	
		//creating the test object for the log information
		//ArrayList<HashMap to store the details>	
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName()+"_"+orgType);
		test.assignAuthor("MalleswariSykam");	
		spogServer.setToken(admin_token);
		
		String accountsType[]=cloudAccountType.split(",");

				for(int j=0;j<accountsType.length;j++){
					//parameters for the creation of the cloudAccounts
					HashMap<String,Object> expected_volumes=new HashMap<String,Object>();
					expected_volumes.put("usage",0);expected_volumes.put("count",0);expected_volumes.put("capacity",0);
					int count=0;
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
					String cloud_account_status="";
					if (organization_id == null || organization_id == "" || organization_id.equalsIgnoreCase("none")) {
						cloud_account_id=spogServer.createCloudAccountWithCheck(cloudAccountKey, cloudAccountSecret, cloudAccountName,
								accountsType[j], organization_id, orderID, fulfillmentID,datacenter_id, test);
					} else {
						cloud_account_id=spogServer.createCloudAccountWithCheck(cloudAccountKey, cloudAccountSecret, cloudAccountName,
								accountsType[j], organization_id, orderID, fulfillmentID, datacenter_id,test);
					}	if(cloud_account_id!=null){
						cloud_account_status="success";
					}else{
						cloud_account_status="failure";
					}		
					//Cloud Data
					cloudData=spogServer.composeCloudData(cloud_account_id,cloudAccountKey,  organization_id,create_user_id,cloudAccountName,cloud_account_status,expected_volumes,datacenter_id, accountsType[j]);	
					if(orgType.equalsIgnoreCase("direct")){ 
					cloudDatasDirect.add(cloudData);
					cloudDataUnderCsr.add(cloudData);
					directClouds.add(cloudAccountName);
				}else if(orgType.equalsIgnoreCase("msp")){
					mspClouds.add(cloudAccountName);
					cloudDatasMsp.add(cloudData);
					cloudDataUnderCsr.add(cloudData);
					cloudDatasMsp_subOrg.add(cloudData);
				}else {
					subClouds.add(cloudAccountName);
					cloudDatasSubOrg.add(cloudData);
					cloudDataUnderCsr.add(cloudData);
					cloudDatasMsp_subOrg.add(cloudData);
				}
		
		}
	}
	//
	@DataProvider(name = "getcloudInfo1")
	public final Object[][] getCloudInfoForFilters() {
		return new Object[][]   {{direct_user_validToken,"direct",1,20,"organization_id;=;direct","",cloudDatasDirect},
				{direct_user_validToken,"direct",1,20,"","cloud_account_name;asc",cloudDatasDirect},
				{direct_user_validToken,"direct",1,20,"","cloud_account_name;desc",cloudDatasDirect},
				{direct_user_validToken,"direct",1,20,"","cloud_account_type;desc",cloudDatasDirect},
				{direct_user_validToken,"direct",1,20,"","cloud_account_type;asc",cloudDatasDirect},
				{direct_user_validToken,"direct",1,20,"","",cloudDatasDirect},
				{direct_user_validToken,"direct",1,20,"cloud_account_name;=;directClouds(1)","",cloudDatasDirect},
				{direct_user_validToken,"direct",1,20,"","cloud_account_status;desc",cloudDatasDirect},
				{direct_user_validToken,"direct",1,20,"","cloud_account_status;asc",cloudDatasDirect},
				{direct_user_validToken,"direct",1,20,"cloud_account_type;=;cloud_direct","cloud_account_status;asc",cloudDatasDirect},
				{direct_user_validToken,"direct",1,20,"cloud_account_type;=;aws","cloud_account_status;asc",cloudDatasDirect},
				
				//All cases related to the mspOrganization
				{msp_user_validToken,"msp",1,20,"organization_id;=;msp","",cloudDatasMsp},
				{msp_user_validToken,"msp",1,20,"","",cloudDatasMsp_subOrg},	
				{msp_user_validToken,"msp",1,20,"cloud_account_name;=;mspClouds(1)","",cloudDatasMsp_subOrg},
				{msp_user_validToken,"msp",1,20,"","cloud_account_name;asc",cloudDatasMsp_subOrg},
				{msp_user_validToken,"msp",1,20,"","cloud_account_name;desc",cloudDatasMsp_subOrg},
				{msp_user_validToken,"msp",1,20,"","cloud_account_type;desc",cloudDatasMsp_subOrg},
				{msp_user_validToken,"msp",1,20,"","cloud_account_type;asc",cloudDatasMsp_subOrg},
				{msp_user_validToken,"msp",1,20,"","cloud_account_status;desc",cloudDatasMsp_subOrg},
				{msp_user_validToken,"msp",1,20,"","cloud_account_status;asc",cloudDatasMsp_subOrg},
				{msp_user_validToken,"msp",1,20,"cloud_account_type;=;cloud_direct","cloud_account_status;asc",cloudDatasMsp_subOrg},
				{msp_user_validToken,"msp",1,20,"cloud_account_type;=;aws","cloud_account_status;asc",cloudDatasMsp_subOrg},
			
				
				//All the cases related to the csrOrganization
				{csr_Token,"csr",1,20,"organization_id;in;msp|direct|msp_child","",cloudDataUnderCsr},
				{csr_Token,"csr",1,20,"organization_id;=;msp|direct|","",cloudDataUnderCsr},
				{csr_Token,"csr",1,20,"cloud_account_name;=;subClouds(0)","",cloudDataUnderCsr},
				{csr_Token,"csr",1,20,"cloud_account_name;in;subClouds(0)|mspClouds(0)|directClouds(0)","",cloudDataUnderCsr},
				{csr_Token,"csr",1,20,"organization_id;=;msp_child","",cloudDataUnderCsr},
	
				
				//all cases related to the SubOrg organization
				{direct_user_validToken_2,"subOrg",1,20,"organization_id;=;msp_child","",cloudDatasSubOrg},
				{direct_user_validToken_2,"subOrg",1,20,"","",cloudDatasSubOrg},
				{direct_user_validToken_2,"subOrg",1,20,"cloud_account_name;=;subClouds(0)","",cloudDatasSubOrg},
				{direct_user_validToken_2,"subOrg",1,20,"organization_id;=;msp_child","",cloudDatasSubOrg},
				{direct_user_validToken_2,"subOrg",1,20,"","cloud_account_name;asc",cloudDatasSubOrg},
				{direct_user_validToken_2,"subOrg",1,20,"","cloud_account_name;desc",cloudDatasSubOrg},
				{direct_user_validToken_2,"subOrg",1,20,"","cloud_account_type;desc",cloudDatasSubOrg},
				{direct_user_validToken_2,"subOrg",1,20,"","cloud_account_type;asc",cloudDatasSubOrg},
				{direct_user_validToken_2,"subOrg",1,20,"","cloud_account_status;asc",cloudDatasSubOrg},
				{direct_user_validToken_2,"subOrg",1,20,"","cloud_account_status;desc",cloudDatasSubOrg},
				{direct_user_validToken_2,"msp",1,20,"cloud_account_type;=;aws","cloud_account_status;asc",cloudDatasSubOrg},
		};
	}

	//Post the jobs by applying mutiple_Filtering 
	@Test(dependsOnMethods="PostCloudAccountValid",dataProvider="getcloudInfo1")
	public void GetLogsForValidCasesUsingFiltering(String admin_Token,String OrgType,int page_number,int page_size,String filterStr,String Sortstr, ArrayList<HashMap<String,Object>> cloudInfo){
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		test.assignAuthor("MalleswariSykam");		
		String[] filterArray=null;
		String filterName = null,filterOperator = null, filterValue = null;
		if(filterStr!=""&&filterStr!=null){
			filterArray = filterStr.split(";");
			filterName = filterArray[0];filterOperator = filterArray[1];filterValue = filterArray[2];
		}
		//preparing the URL and validating the Response For 	 	
		test.log(LogStatus.INFO,"get filter");	
		String newFilterStr = filterStr;	
	      if(filterStr!=""&&filterStr!=null){
			if(filterName.equalsIgnoreCase("organization_id")){
				if(filterOperator.equals("=")){
					if(filterValue.equals(SpogConstants.DIRECT_ORG)){
						newFilterStr = filterName + ";"+ filterOperator + ";" +directOrg_id;
					}else if(filterValue.equals(SpogConstants.MSP_ORG)){
						newFilterStr = filterName + ";"+ filterOperator + ";" +mspOrg_id;
					}else{
						newFilterStr = filterName + ";"+ filterOperator + ";" +account_id;
					}
				}else{
					//in 
					newFilterStr = filterName + ";"+ filterOperator + ";";
					if(filterValue.contains(SpogConstants.DIRECT_ORG)&& filterValue.contains(SpogConstants.MSP_ORG)&&filterValue.contains(SpogConstants.MSP_SUB_ORG)){
						newFilterStr+=  directOrg_id + "|" + mspOrg_id+"|"+account_id ;
					}else{
						newFilterStr+=  directOrg_id + "|" + mspOrg_id;
					}
				}			
			}else if(filterName.equalsIgnoreCase("cloud_account_name")){
				int index=Integer.parseInt(filterValue.substring((filterValue.length()-2),(filterValue.length()-1)));
				if(filterOperator.equals("=")){
					System.out.println("The value of the index is :"+index);
					if(filterValue.contains("directClouds")){
						newFilterStr = filterName + ";"+ filterOperator + ";"+directClouds.get(index);
					}else if(filterValue.contains("mspClouds")){
						newFilterStr = filterName + ";"+ filterOperator + ";"+mspClouds.get(index);
					}else{
						newFilterStr = filterName + ";"+ filterOperator + ";"+subClouds.get(index);
					}
				}else{
					//in
					newFilterStr = filterName + ";"+ filterOperator + ";";
					if(filterValue.contains("directClouds")&& filterValue.contains("mspClouds")&&filterValue.contains("subClouds")){
						newFilterStr+=  directClouds.get(index)+ "|" +mspClouds.get(index)+"|"+account_id ;
					}else{
						newFilterStr+=  directClouds.get(index)+ "|" + mspClouds.get(index);
					}
				}
			}
	      }	      
		if(OrgType.equalsIgnoreCase("direct")){
			test.log(LogStatus.INFO,"Preapring the URL for the Get cloud accounts:");
			String additionalURL=spogServer.PrepareURL(newFilterStr,Sortstr,page_number, page_size, test);	
			Response response=spogServer.getCloudAccounts(admin_Token,additionalURL,test);	
			spogServer.checkGetCloudAccounts(response, SpogConstants.SUCCESS_GET_PUT_DELETE, cloudInfo, page_number, page_size, newFilterStr, Sortstr, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		}else if(OrgType.equalsIgnoreCase("msp")){	
			test.log(LogStatus.INFO,"Preapring the URL for the Get cloud accounts:");
			String additionalURL=spogServer.PrepareURL(newFilterStr,Sortstr,page_number, page_size, test);	
			Response response=spogServer.getCloudAccounts(admin_Token,additionalURL,test);	
			spogServer.checkGetCloudAccounts(response, SpogConstants.SUCCESS_GET_PUT_DELETE, cloudInfo, page_number, page_size, newFilterStr, Sortstr, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		}else if(OrgType.equalsIgnoreCase("subOrg")) {
			test.log(LogStatus.INFO,"Preapring the URL for the Get cloud accounts:");
			String additionalURL=spogServer.PrepareURL(newFilterStr,Sortstr,page_number, page_size, test);	
			Response response=spogServer.getCloudAccounts(admin_Token,additionalURL,test);	
			spogServer.checkGetCloudAccounts(response, SpogConstants.SUCCESS_GET_PUT_DELETE, cloudInfo, page_number, page_size, newFilterStr, Sortstr, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		}else{
			test.log(LogStatus.INFO,"Preapring the URL for the Get cloud accounts:");
			String additionalURL=spogServer.PrepareURL(newFilterStr,Sortstr,page_number, page_size, test);	
			Response response=spogServer.getCloudAccounts(admin_Token,additionalURL,test);	
			spogServer.checkGetCloudAccounts(response, SpogConstants.SUCCESS_GET_PUT_DELETE, cloudInfo, page_number, page_size, newFilterStr, Sortstr, SpogMessageCode.SUCCESS_GET_PUT_DEL, test);
		}
	}	

	//
	@DataProvider(name = "getcloudInfo2")
	public final Object[][] getCloudInfoInsufficentPermissions() {
		return new Object[][]   { {msp_user_validToken,"direct",1,20,"organization_id;=;direct","",cloudDatasDirect},	
				{direct_user_validToken,"msp",1,20,"organization_id;=;msp","",cloudDatasMsp},
				{direct_user_validToken,"subOrg",1,20,"organization_id;=;msp_child","",cloudDatasSubOrg},
				{direct_user_validToken,"csr",1,20,"organization_id;in;msp|direct","",cloudDataUnderCsr}
		};
	}

	//Post the jobs by applying mutiple_Filtering 
	@Test(dependsOnMethods="PostCloudAccountValid",dataProvider="getcloudInfo2")
	public void GetLogsForInsufficientPermissions(String admin_Token,String OrgType,int page_number,int page_size,String filterStr,String Sortstr, ArrayList<HashMap<String,Object>> cloudInfo){
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		test.assignAuthor("MalleswariSykam");		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		test.assignAuthor("MalleswariSykam");		
		String[] filterArray=null;
		String filterName = null,filterOperator = null, filterValue = null;
		if(filterStr!=""&&filterStr!=null){
			filterArray = filterStr.split(";");
			filterName = filterArray[0];filterOperator = filterArray[1];filterValue = filterArray[2];
		}
		//preparing the URL and validating the Response For 
		
		test.log(LogStatus.INFO,"get filter");	
		String newFilterStr = filterStr;	
		if(filterStr!=""&&filterStr!=null){
		if(filterName.equalsIgnoreCase("organization_id")){
			if(filterOperator.equals("=")){
				if(filterValue.equals(SpogConstants.DIRECT_ORG)){
					newFilterStr = filterName + ";"+ filterOperator + ";" +directOrg_id;
				}else{
					newFilterStr = filterName + ";"+ filterOperator + ";" +mspOrg_id;
				}
			}else{
				//in 
				newFilterStr = filterName + ";"+ filterOperator + ";";
				if(filterValue.contains(SpogConstants.DIRECT_ORG)&& filterValue.contains(SpogConstants.MSP_ORG)&&filterValue.contains(SpogConstants.MSP_SUB_ORG)){
					newFilterStr+=  directOrg_id + "|" + mspOrg_id+"|"+account_id ;
				}else{
					newFilterStr+=  directOrg_id + "|" + mspOrg_id;
				}
			}			
		}
		}
		if(OrgType.equalsIgnoreCase("direct")){
			test.log(LogStatus.INFO,"Preapring the URL for the Get cloud accounts:");
			String additionalURL=spogServer.PrepareURL(newFilterStr,Sortstr,page_number, page_size, test);	
			Response response=spogServer.getCloudAccounts(admin_Token,additionalURL,test);	
			spogServer.checkGetCloudAccounts(response, SpogConstants.INSUFFICIENT_PERMISSIONS, cloudInfo, page_number, page_size, newFilterStr, Sortstr, SpogMessageCode.RESOURCE_PERMISSION_DENY, test);
		}else if(OrgType.equalsIgnoreCase("msp")){	
			test.log(LogStatus.INFO,"Preapring the URL for the Get cloud accounts:");
			String additionalURL=spogServer.PrepareURL(newFilterStr,Sortstr,page_number, page_size, test);	
			Response response=spogServer.getCloudAccounts(admin_Token,additionalURL,test);	
			spogServer.checkGetCloudAccounts(response, SpogConstants.INSUFFICIENT_PERMISSIONS, cloudInfo, page_number, page_size, newFilterStr, Sortstr, SpogMessageCode.RESOURCE_PERMISSION_DENY, test);
		}else if(OrgType.equalsIgnoreCase("subOrg")) {
			test.log(LogStatus.INFO,"Preapring the URL for the Get cloud accounts:");
			String additionalURL=spogServer.PrepareURL(newFilterStr,Sortstr,page_number, page_size, test);	
			Response response=spogServer.getCloudAccounts(admin_Token,additionalURL,test);	
			spogServer.checkGetCloudAccounts(response, SpogConstants.INSUFFICIENT_PERMISSIONS, cloudInfo, page_number, page_size, newFilterStr, Sortstr, SpogMessageCode.RESOURCE_PERMISSION_DENY, test);
		}else{
			test.log(LogStatus.INFO,"Preapring the URL for the Get cloud accounts:");
			String additionalURL=spogServer.PrepareURL(newFilterStr,Sortstr,page_number, page_size, test);	
			Response response=spogServer.getCloudAccounts(admin_Token,additionalURL,test);	
			spogServer.checkGetCloudAccounts(response, SpogConstants.INSUFFICIENT_PERMISSIONS, cloudInfo, page_number, page_size, newFilterStr, Sortstr, SpogMessageCode.RESOURCE_PERMISSION_DENY, test);
		}
	}	
	
	
	//
	@DataProvider(name = "getcloudInfo3")
	public final Object[][] getCloudInfoMissingToken() {
		return new Object[][]   { {"","direct",1,20,"organization_id;=;direct","",cloudDatasDirect},	
				{"","msp",1,20,"organization_id;=;msp","",cloudDatasMsp},
				{"","subOrg",1,20,"organization_id;=;msp_child","",cloudDatasSubOrg},
				{"","csr",1,20,"organization_id;in;msp|direct","",cloudDataUnderCsr}
		};
	}
	//Post the jobs by applying mutiple_Filtering 
		@Test(dependsOnMethods="PostCloudAccountValid",dataProvider="getcloudInfo3")
		public void GetLogsForMissingToken(String admin_Token,String OrgType,int page_number,int page_size,String filterStr,String Sortstr, ArrayList<HashMap<String,Object>> cloudInfo){
			test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
			test.assignAuthor("MalleswariSykam");		
		
			if(OrgType.equalsIgnoreCase("direct")){
				test.log(LogStatus.INFO,"Preapring the URL for the Get cloud accounts:");
				String additionalURL=spogServer.PrepareURL(filterStr,Sortstr,page_number, page_size, test);	
				Response response=spogServer.getCloudAccounts(admin_Token,additionalURL,test);	
				spogServer.checkGetCloudAccounts(response, SpogConstants.NOT_LOGGED_IN, cloudInfo, page_number, page_size, filterStr, Sortstr, SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);
			}else if(OrgType.equalsIgnoreCase("msp")){	
				test.log(LogStatus.INFO,"Preapring the URL for the Get cloud accounts:");
				String additionalURL=spogServer.PrepareURL(filterStr,Sortstr,page_number, page_size, test);	
				Response response=spogServer.getCloudAccounts(admin_Token,additionalURL,test);	
				spogServer.checkGetCloudAccounts(response, SpogConstants.NOT_LOGGED_IN, cloudInfo, page_number, page_size, filterStr, Sortstr, SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);
			}else if(OrgType.equalsIgnoreCase("subOrg")) {
				test.log(LogStatus.INFO,"Preapring the URL for the Get cloud accounts:");
				String additionalURL=spogServer.PrepareURL(filterStr,Sortstr,page_number, page_size, test);	
				Response response=spogServer.getCloudAccounts(admin_Token,additionalURL,test);	
				spogServer.checkGetCloudAccounts(response, SpogConstants.NOT_LOGGED_IN, cloudInfo, page_number, page_size, filterStr, Sortstr, SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);
			}else{
				test.log(LogStatus.INFO,"Preapring the URL for the Get cloud accounts:");
				String additionalURL=spogServer.PrepareURL(filterStr,Sortstr,page_number, page_size, test);	
				Response response=spogServer.getCloudAccounts(admin_Token,additionalURL,test);	
				spogServer.checkGetCloudAccounts(response, SpogConstants.NOT_LOGGED_IN, cloudInfo, page_number, page_size, filterStr, Sortstr, SpogMessageCode.COMMON_AUTHENTICATION_FAILED, test);
			}
		}	

	//
		@DataProvider(name = "getcloudInfo4")
		public final Object[][] getCloudInfoInvalidToken() {
			return new Object[][]   { {msp_user_validToken+"Junk","direct",1,20,"organization_id;=;direct","",cloudDatasDirect},	
					{direct_user_validToken+"Junk","msp",1,20,"organization_id;=;msp","",cloudDatasMsp},
					{direct_user_validToken+"Junk","subOrg",1,20,"organization_id;=;msp_child","",cloudDatasSubOrg},
					{direct_user_validToken+"Junk","csr",1,20,"organization_id;in;msp|direct","",cloudDataUnderCsr}
			};
		}
		//Post the jobs by applying mutiple_Filtering 
		@Test(dependsOnMethods="PostCloudAccountValid",dataProvider="getcloudInfo4")
		public void GetLogsForInvalidToken(String admin_Token,String OrgType,int page_number,int page_size,String filterStr,String Sortstr, ArrayList<HashMap<String,Object>> cloudInfo){
			test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
			test.assignAuthor("MalleswariSykam");		
			
			if(OrgType.equalsIgnoreCase("direct")){
				test.log(LogStatus.INFO,"Preapring the URL for the Get cloud accounts:");
				String additionalURL=spogServer.PrepareURL(filterStr,Sortstr,page_number, page_size, test);	
				Response response=spogServer.getCloudAccounts(admin_Token,additionalURL,test);	
				spogServer.checkGetCloudAccounts(response, SpogConstants.NOT_LOGGED_IN, cloudInfo, page_number, page_size, filterStr, Sortstr, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);
			}else if(OrgType.equalsIgnoreCase("msp")){	
				test.log(LogStatus.INFO,"Preapring the URL for the Get cloud accounts:");
				String additionalURL=spogServer.PrepareURL(filterStr,Sortstr,page_number, page_size, test);	
				Response response=spogServer.getCloudAccounts(admin_Token,additionalURL,test);	
				spogServer.checkGetCloudAccounts(response, SpogConstants.NOT_LOGGED_IN, cloudInfo, page_number, page_size, filterStr, Sortstr, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);
			}else if(OrgType.equalsIgnoreCase("subOrg")) {
				test.log(LogStatus.INFO,"Preapring the URL for the Get cloud accounts:");
				String additionalURL=spogServer.PrepareURL(filterStr,Sortstr,page_number, page_size, test);	
				Response response=spogServer.getCloudAccounts(admin_Token,additionalURL,test);	
				spogServer.checkGetCloudAccounts(response, SpogConstants.NOT_LOGGED_IN, cloudInfo, page_number, page_size, filterStr, Sortstr, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);
			}else{
				test.log(LogStatus.INFO,"Preapring the URL for the Get cloud accounts:");
				String additionalURL=spogServer.PrepareURL(filterStr,Sortstr,page_number, page_size, test);	
				Response response=spogServer.getCloudAccounts(admin_Token,additionalURL,test);	
				spogServer.checkGetCloudAccounts(response, SpogConstants.NOT_LOGGED_IN, cloudInfo, page_number, page_size, filterStr, Sortstr, SpogMessageCode.COMMON_AUTENTICATION_FAILED_FOR_JWT, test);
			}
		}  
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
		@AfterSuite
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