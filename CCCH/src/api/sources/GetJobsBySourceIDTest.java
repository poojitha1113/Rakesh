package api.sources;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

import Constants.ConnectionStatus;
import Constants.DestinationStatus;
import Constants.DestinationType;
import Constants.ErrorCode;
import Constants.JobType4LatestJob;
import Constants.OSMajor;
import Constants.ProtectionStatus;
import Constants.SourceProduct;
import Constants.SourceType;
import Constants.SpogConstants;
import InvokerServer.GatewayServer;
import InvokerServer.Org4SPOGServer;
import InvokerServer.SPOGDestinationServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import InvokerServer.GatewayServer.siteType;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import invoker.SiteTestHelper;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;

public class GetJobsBySourceIDTest  extends base.prepare.PrepareOrgInfo{
	
	@Parameters({ "pmfKey"})
	  public GetJobsBySourceIDTest(String pmfKey) {
		super(pmfKey);
		// TODO Auto-generated constructor stub
	}

	private SPOGServer spogServer;
	  private UserSpogServer userSpogServer;
	  private GatewayServer gatewayServer;
	  private SPOGDestinationServer spogDestinationServer;
	  private String csrAdminUserName;
	  private String csrAdminPassword;
	  private String csrReadOnlyAdminUserName;
	  private String csrReadOnlyAdminPassword;
	  private ExtentTest test;
	  
	  private String postfix_email = "@arcserve.com";
	  private String common_password = "Welcome*02";
	  
	  private String prefix_direct = "spogqa_shuo_direct";
	  private String direct_org_name = prefix_direct + "_org";
	  private String direct_org_id=null;
	  private String direct_user_name = prefix_direct + "_admin";
	  private String direct_user_name_email = direct_user_name + postfix_email;
	  private String direct_user_first_name = direct_user_name + "_first_name";
	  private String direct_user_last_name = direct_user_name + "_last_name";
	  private String direct_user_id =null;
	  private String final_direct_user_name_email = null;
	  private String direct_site_id;
	  private String direct_site_token;
	  private String another_direct_site_id;
	  
	  private String prefix_msp = "spogqa_shuo_msp";
	  private String msp_org_name = prefix_msp + "_org";
	  private String msp_org_email = msp_org_name + postfix_email;
	  private String msp_org_first_name = msp_org_name + "_first_name";
	  private String msp_org_last_name = msp_org_name + "_last_name";
	  private String msp_user_name = prefix_msp + "_admin";
	  private String msp_user_name_email = prefix_msp + "_admin" + postfix_email;
	  private String msp_user_first_name = msp_user_name + "_first_name";
	  private String msp_user_last_name = msp_user_name + "_last_name";
	  private String msp_org_id;
	  private String final_msp_user_name_email;
	  private String msp_user_id;
	//  private String msp_site_id;
	//  private String another_msp_site_id;
	//  private String msp_site_token;
	  
	  private String prefix_msp_account = "spogqa_shuo_msp_account";
	  private String msp_account_admin_name = prefix_msp_account + "_admin";
	  private String msp_account_admin_email =msp_account_admin_name + postfix_email;
	  private String msp_account_admin_first_name = msp_account_admin_name + "_first_name";
	  private String msp_account_admin_last_name = msp_account_admin_name + "_last_name";
	  private String msp_account_admin_id;
	  private String final_msp_account_admin_email;
	  
	  
	  private String account_id;
	  private String account_user_email;
	  private String account_user_id;
	  private String account_site_id;
	  private String another_account_site_id;
	  private String account_site_token;
	  private String another_msp_site_token;
	  private String another_account_id;
	  private String site_id_another_account;
	  
	  //this is for update portal, each testng class is taken as BQ set
/*	  private SQLServerDb bqdb1;
	  public int Nooftest;
	  private long creationTime;
	  private String BQName=null;
	  private String runningMachine;
	  private testcasescount count1;
	  private String buildVersion;
	  private ExtentReports rep;
	*/
	//  private String message = "Unable to connect to ";
	  private	String job_Type = "backup";
	  private	String job_Status= "finished";
	  private	String job_Method = "full";
	  private String  org_model_prefix=this.getClass().getSimpleName();

	  private HashMap<String,String> siteInfo = new HashMap<String, String>();
	private Org4SPOGServer org4SpogServer;
	 
	  @BeforeClass
	  @Parameters({ "baseURI", "port", "logFolder", "csrAdminUserName", "csrAdminPassword", "buildVersion", "csrReadOnlyAdminUserName","csrReadOnlyAdminPassword"})
	  public void beforeClass(String baseURI, String port, String logFolder, String adminUserName, String adminPassword ,  String buildVersion, String csrROAdminUserName, String csrROPwd ) throws UnknownHostException {
				
		
		  	spogServer = new SPOGServer(baseURI, port);
			  org4SpogServer = new Org4SPOGServer(baseURI, port);
			userSpogServer = new UserSpogServer(baseURI, port);
		  	gatewayServer =new GatewayServer(baseURI,port);
		  	spogDestinationServer  =new SPOGDestinationServer(baseURI,port);
		  	rep = ExtentManager.getInstance("GetJobsBySourceID",logFolder);
		  	this.csrAdminUserName = adminUserName;
		  	this.csrAdminPassword = adminPassword;
		 	 this.csrReadOnlyAdminUserName = csrROAdminUserName;
			  this.csrReadOnlyAdminPassword = csrROPwd;
		 
		    test = rep.startTest("beforeClass");
			spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
		
			//create a direct org
			String prefix = RandomStringUtils.randomAlphanumeric(8);
			direct_org_id = spogServer.CreateOrganizationWithCheck(prefix+direct_org_name + org_model_prefix , SpogConstants.DIRECT_ORG, null, common_password, null, null, test);
			final_direct_user_name_email = prefix + direct_user_name_email;
			direct_user_id = spogServer.createUserAndCheck(final_direct_user_name_email, common_password, prefix + direct_user_first_name, prefix + direct_user_last_name, SpogConstants.DIRECT_ADMIN, direct_org_id, test);
			spogServer.userLogin(final_direct_user_name_email, common_password);
		  	
			test.log(LogStatus.INFO,"Getting the JWTToken for the Logged in user");
			String validToken = spogServer.getJWTToken();
			String direct_user_validToken = validToken;
			test.log(LogStatus.INFO,"The token is :"+ validToken );
			String siteName= spogServer.getRandomSiteName("TestCreate");
			test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
			String sitetype=siteType.gateway.toString();
			test.log(LogStatus.INFO,"The siteType :"+sitetype);
			test.log(LogStatus.INFO,"Creating a site For Logged in user");
			direct_site_id = gatewayServer.createsite_register_login(direct_org_id, validToken, direct_user_id, "shuo", "1.0.0", spogServer, test);
			direct_site_token=gatewayServer.getJWTToken();
			test.log(LogStatus.INFO, "The site token is "+direct_site_token);
			siteInfo.put( direct_site_id, direct_site_token);

			siteName= spogServer.getRandomSiteName("TestCreate");
			test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
			sitetype=siteType.gateway.toString();
			test.log(LogStatus.INFO,"The siteType :"+sitetype);
			test.log(LogStatus.INFO,"Creating another site For direct org");
			another_direct_site_id = gatewayServer.createsite_register_login(direct_org_id, direct_user_validToken, direct_user_id, "shuo1", "1.0.0", spogServer, test);
			String another_direct_site_token=gatewayServer.getJWTToken();
			test.log(LogStatus.INFO, "The site token is "+another_direct_site_token);
			siteInfo.put( another_direct_site_id, another_direct_site_token);
			
			
		
			
			//************************create msp org,user,site*************************************
			spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
			spogServer.postCloudhybridInFreeTrial(spogServer.getJWTToken(), direct_org_id, SpogConstants.DIRECT_ORG, false, false);
			
			prefix = RandomStringUtils.randomAlphanumeric(8);
			test.log(LogStatus.INFO,"create a msp org");
			msp_org_id = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name + org_model_prefix, SpogConstants.MSP_ORG, null, common_password, null, null, test);
			final_msp_user_name_email = prefix + msp_user_name_email;
			
			test.log(LogStatus.INFO,"create a admin under msp org");
			msp_user_id = spogServer.createUserAndCheck(final_msp_user_name_email, common_password, prefix + msp_user_first_name, prefix + msp_user_last_name, SpogConstants.MSP_ADMIN, msp_org_id, test);
			spogServer.userLogin(final_msp_user_name_email, common_password);
			
		  	
			test.log(LogStatus.INFO,"Getting the JWTToken for the msp user");
			String msp_user_validToken = spogServer.getJWTToken();
			test.log(LogStatus.INFO,"The token is :"+ msp_user_validToken );
			
			test.log(LogStatus.INFO,"create a msp account admin under msp org");
			final_msp_account_admin_email = prefix + this.msp_account_admin_email;
			msp_account_admin_id = spogServer.createUserAndCheck(final_msp_account_admin_email, common_password, prefix + msp_account_admin_first_name, prefix + msp_account_admin_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, msp_org_id, test);
			
			
			siteName= spogServer.getRandomSiteName("TestCreate");
			test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
			sitetype=siteType.gateway.toString();
			test.log(LogStatus.INFO,"The siteType :"+sitetype);
			
		
		/*	test.log(LogStatus.INFO,"Creating a site For msp org");
			msp_site_id = gatewayServer.createsite_register_login(msp_org_id, msp_user_validToken, msp_user_id, "shuo", "1.0.0", spogServer, test);
			msp_site_token=gatewayServer.getJWTToken();
			test.log(LogStatus.INFO, "The site token is "+ msp_site_token);
			siteInfo.put( msp_site_id, msp_site_token); 
			
			
			
			siteName= spogServer.getRandomSiteName("TestCreate");
			test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
			sitetype=siteType.gateway.toString();
			test.log(LogStatus.INFO,"The siteType :"+sitetype);
			
			test.log(LogStatus.INFO,"Creating another site For msp org");
			another_msp_site_id = gatewayServer.createsite_register_login(msp_org_id, msp_user_validToken, msp_user_id, "shuo1", "1.0.0", spogServer, test);
			another_msp_site_token=gatewayServer.getJWTToken();
			test.log(LogStatus.INFO, "The site token is "+ another_msp_site_token);
			siteInfo.put( another_msp_site_id, another_msp_site_token);*/
			
			
			//create account, account user and site
			test.log(LogStatus.INFO,"Creating a account For msp org");
			account_id = spogServer.createAccountWithCheck(msp_org_id, "sub_" + prefix + msp_org_name, "", test);
			prefix = RandomStringUtils.randomAlphanumeric(8);
		
			test.log(LogStatus.INFO,"Creating a account user For account org");
			account_user_email = prefix + msp_user_name_email;
			account_user_id = spogServer.createUserAndCheck(account_user_email, common_password, prefix + msp_user_first_name, prefix + msp_user_last_name, SpogConstants.DIRECT_ADMIN, account_id, test);
			spogServer.userLogin(account_user_email, common_password);
		  	
			
			test.log(LogStatus.INFO,"Getting the JWTToken for the account user");
			String account_user_validToken = spogServer.getJWTToken();
			test.log(LogStatus.INFO,"The token is :"+ account_user_validToken );
			
			siteName= spogServer.getRandomSiteName("TestCreate");
			test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
			sitetype=siteType.gateway.toString();
			test.log(LogStatus.INFO,"The siteType :"+sitetype);
			
			test.log(LogStatus.INFO,"Creating a site For account org");
			account_site_id = gatewayServer.createsite_register_login(account_id, account_user_validToken, account_user_id, "shuo", "1.0.0", spogServer, test);
			account_site_token=gatewayServer.getJWTToken();
			test.log(LogStatus.INFO, "The site token is "+ account_site_token);
			siteInfo.put( account_site_id, account_site_token);
			
			siteName= spogServer.getRandomSiteName("TestCreate");
			test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
			sitetype=siteType.gateway.toString();
			test.log(LogStatus.INFO,"The siteType :"+sitetype);
			
			test.log(LogStatus.INFO,"Creating another site For account org");
			another_account_site_id = gatewayServer.createsite_register_login(account_id, account_user_validToken, account_user_id, "shuo1", "1.0.0", spogServer, test);
			String another_account_site_token=gatewayServer.getJWTToken();
			test.log(LogStatus.INFO, "The site token is "+ another_account_site_token);
			siteInfo.put( another_account_site_id, another_account_site_token);
			
			spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
			spogServer.postCloudhybridInFreeTrial(spogServer.getJWTToken(), msp_org_id, SpogConstants.MSP_ORG, false, false);
			
			test.log(LogStatus.INFO,"assign account to msp account admin");
			String[] mspAccountAdmins = new String []{msp_account_admin_id};
			userSpogServer.assignMspAccountAdmins(msp_org_id, account_id, mspAccountAdmins , spogServer.getJWTToken()); 
			
			//create account, account user 
			test.log(LogStatus.INFO,"Creating another account For msp org");
			prefix = RandomStringUtils.randomAlphanumeric(8);
			another_account_id = spogServer.createAccountWithCheck(msp_org_id, "sub_" + prefix + msp_org_name, "", test);
			
			siteName= spogServer.getRandomSiteName("TestCreate");
			test.log(LogStatus.INFO,"Generated a Random SiteName "+siteName);
			sitetype=siteType.gateway.toString();
			test.log(LogStatus.INFO,"The siteType :"+sitetype);
			
			test.log(LogStatus.INFO,"Creating a site For another account org");
			site_id_another_account = gatewayServer.createsite_register_login(another_account_id, spogServer.getJWTToken(), spogServer.GetLoggedinUser_UserID(), "shuo", "1.0.0", spogServer, test);
	
			siteInfo.put(  site_id_another_account, gatewayServer.getJWTToken());
			
			prepare(baseURI, port, logFolder, this.csrAdminUserName,  this.csrAdminPassword, this.getClass().getSimpleName() );
			siteInfo.put( this.root_msp_direct_org_site1_siteId, this.root_msp_direct_org_site1_token);
			siteInfo.put( this.sub_msp1_account1_site1_siteId, this.sub_msp1_account1_site1_token);
			siteInfo.put( this.sub_msp1_account2_site1_siteId, this.sub_msp1_account2_site1_token);
			siteInfo.put( this.sub_msp2_account1_site1_siteId, this.sub_msp2_account1_site1_token);

			//this is for update portal
		  	this.BQName = this.getClass().getSimpleName();
		    String author = "Shuo.Zhang";
		    this.runningMachine =  InetAddress.getLocalHost().getHostName();
		    SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		    java.util.Date date=new java.util.Date();
		    this.buildVersion=buildVersion+"_"+dateFormater.format(date);
		    Nooftest=0;
		    bqdb1 = new SQLServerDb();
		    count1 = new testcasescount();
		    if(count1.isstarttimehit()==0) {      
		            creationTime=System.currentTimeMillis();
		            count1.setcreationtime(creationTime);
		            try {
                        bqdb1.updateTable(this.BQName, runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0", String.valueOf(Nooftest), count1.getcreationtime(), "InProgress",author+" and Rest server is "+baseURI.split("//")[1]);
		            } catch (ClientProtocolException e) {
		                  // TODO Auto-generated catch block
		                  e.printStackTrace();
		            } catch (IOException e) {
		                  // TODO Auto-generated catch block
		                  e.printStackTrace();
		            }
		        }  
		    

	  }

	  @DataProvider(name = "post_job_data")
		public final Object[][] postJobDataParams() {
			return new Object[][] {
				{ final_direct_user_name_email,  direct_org_id,  direct_site_id,  new String[] { "node", "agent"}, direct_site_token},
			
				{ final_msp_user_name_email,  account_id,  account_site_id,  new String[] { "node", "agent"}, account_site_token},
				{ account_user_email,  account_id,  account_site_id,  new String[] { "node", "agent"}, account_site_token},
			
				{ final_msp_account_admin_email,  account_id,  account_site_id,  new String[] { "node", "agent"}, account_site_token},
				
				
				
				{ this.final_root_msp_user_name_email,  root_msp_direct_org_id,  root_msp_direct_org_site1_siteId,  new String[] { "node", "agent"}, root_msp_direct_org_site1_token},
				{ this.final_root_msp_account_admin_user_name_email,  root_msp_direct_org_id,  root_msp_direct_org_site1_siteId,  new String[] { "node", "agent"}, root_msp_direct_org_site1_token},
				{ this.final_root_msp_direct_org_user_email,  root_msp_direct_org_id,  root_msp_direct_org_site1_siteId,  new String[] { "node", "agent"}, root_msp_direct_org_site1_token},
				
				{ this.final_sub_msp1_user_name_email,  this.sub_msp1_account1_id,  this.sub_msp1_account1_site1_siteId,  new String[] { "node", "agent"}, this.sub_msp1_account1_site1_token},
				{ this.final_sub_msp1_msp_account_user_name_email,  this.sub_msp1_account1_id,  this.sub_msp1_account1_site1_siteId,  new String[] { "node", "agent"}, this.sub_msp1_account1_site1_token},
				{ this.final_sub_msp1_account1_user_email,  this.sub_msp1_account1_id,  this.sub_msp1_account1_site1_siteId,  new String[] { "node", "agent"}, this.sub_msp1_account1_site1_token},
				
			};
	  }
/**
 * 1. direct_admin could get all jobs of its own organization after given source_id
 * 2. msp_admin could get all jobs of its own organization after given source_id
 * 3. msp_admin could get all jobs of its sub organization after given source_id
 * 7. account_admin could get all jobs of its organization after given source_id
 * 21. get jobs by source_id sort by start_time asc
 * 23. get jobs by source_id use default paging
 * 
 * 31. msp account admin could get all jobs of source_id in mastered account
 * 
 * @author shuo.zhang		
 * @param email
 * @param organization_id
 * @param site_id
 * @param messageData
 * @param siteToken
 */
  @Test(dataProvider = "post_job_data", enabled=true)
  public void getJobBySourceId(String email, String organization_id, String site_id, String[] messageData, String siteToken) {
	
	    test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
	    test.assignAuthor("shuo.zhang");
	    spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
	    spogDestinationServer.setToken(spogServer.getJWTToken());	    
		
	
		
		String[] datacenterIDs=spogDestinationServer.getDestionationDatacenterID();
		  
	    String datacenterID=datacenterIDs[0];
	    String destination_name= "mydestination"+RandomStringUtils.randomAlphanumeric(3);
	
		String destination_id =spogDestinationServer.createDestinationWithCheck("", organization_id, site_id, datacenterID, DestinationType.cloud_hybrid_store.toString(), DestinationStatus.creating.toString(), "1", "", "", 
				"", "", "", "3", "5",  "9", "2", "3", "4", "5", "true", "20", "true", destination_name, test);

		
		spogServer.userLogin(email, common_password);
		spogDestinationServer.setToken(spogServer.getJWTToken());
		ArrayList<HashMap<String, Object>> expectedInfoMapArray = new 	ArrayList<HashMap<String, Object>> ();
		ArrayList<String> source_id_List= new ArrayList<String> ();
		
		for(int j=0; j<2; j++){
			test.log(LogStatus.INFO,"create source");
		  	String sourceName = spogServer.ReturnRandom("shuo_source");	  	
		  	String source_id = spogServer.createSourceWithCheck(sourceName, SourceType.machine, SourceProduct.cloud_direct, organization_id, site_id, ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(), "",  test);
		
		//	long start_time_ts = System.currentTimeMillis();
			String server_id = UUID.randomUUID().toString();			
			String rps_id = UUID.randomUUID().toString();
			String policy_id= UUID.randomUUID().toString();
			source_id_List.add(source_id);
			
	
	//		long endTimeTS = System.currentTimeMillis();

			for(int i=0; i<2; i++){
				test.log(LogStatus.INFO,"post job on source "+ sourceName);
				long start_time_ts = System.currentTimeMillis()/1000;
				long endTimeTS = (System.currentTimeMillis()+ 10000)/1000;
				String post_job_id = gatewayServer.postJobWithCheck(start_time_ts,endTimeTS, organization_id, server_id, source_id, rps_id, destination_id, policy_id,
						job_Type, job_Method, job_Status, siteToken, test);
				
				if(j==0){
					test.log(LogStatus.INFO,"compose hashmap for job info ");
							

					HashMap<String, Object> jobInfo = getJobInfo(post_job_id,site_id,start_time_ts,server_id, source_id,sourceName,rps_id,organization_id,destination_id, destination_name, 
							DestinationType.cloud_hybrid_store.toString(), policy_id, null, JobType4LatestJob.backup_full.name(), job_Status, endTimeTS, null );
			
					expectedInfoMapArray.add(jobInfo);
				}
		
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	  	}

		Collections.reverse(expectedInfoMapArray);
		test.log(LogStatus.INFO,"getJobsBySourceIDWithCheck ");
		spogServer.getJobsBySourceIDWithCheck(source_id_List.get(0), null, null, -1, -1, expectedInfoMapArray.size(),expectedInfoMapArray, SpogConstants.SUCCESS_GET_PUT_DELETE, null, true,test);
		
		spogServer.userLogin(this.csrReadOnlyAdminUserName, this.csrReadOnlyAdminPassword);
		test.log(LogStatus.INFO,"csr readonly getJobsBySourceIDWithCheck ");
		spogServer.getJobsBySourceIDWithCheck(source_id_List.get(0), null, null, -1, -1, expectedInfoMapArray.size(),expectedInfoMapArray, SpogConstants.SUCCESS_GET_PUT_DELETE, null, true,test);
		
/*		for(int i=0; i< source_id_List.size();i++){
		 	test.log(LogStatus.INFO,"delete source");
			spogServer.errorHandle.printInfoMessageInDebugFile("delete source");
			Response response = spogServer.deleteSourcesById(spogServer.getJWTToken(), source_id_List.get(i), test);
			spogServer.deleteSourcesWithCheck(response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		}*/
		
		test.log(LogStatus.INFO, "Delete destination by destination id");
	    spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
	    spogDestinationServer.setToken(spogServer.getJWTToken());	    
		
		spogDestinationServer.deletedestinationbydestination_Id(destination_id, spogServer.getJWTToken(), SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);

  }
  
  @DataProvider(name = "filterInfo")
  public final Object[][] getFilterInfo() {
 	 return new Object[][]   {
 		 			{"rps_id;=;"+ UUID.randomUUID().toString()},
 		 			{"rps_id;in;"+ UUID.randomUUID().toString()},
 		 			{"destination_id;=;"+ UUID.randomUUID().toString()},
 		 			{"destination_id;in;"+ UUID.randomUUID().toString()},
 		 			{"policy_id;=;"+ UUID.randomUUID().toString()},
 		 			{"policy_id;in;"+ UUID.randomUUID().toString()},
 		 			{"server_id;=;"+ UUID.randomUUID().toString()},
 		 			{"server_id;in;"+ UUID.randomUUID().toString()},
 		 			
 		 		
 		 			
 		 			{"source_name;=;shuo"}	,
 		 			{"start_time_ts;>=;" + String.valueOf((System.currentTimeMillis()-60000)/1000)},
 		 			{"start_time_ts;<=;" + String.valueOf((System.currentTimeMillis()-60000)/1000)},
 		 			{"start_time_ts;>=;" + String.valueOf((System.currentTimeMillis()-15552100000l)/1000)},
 		 			
 		 		/* no valid	{"organization_id;=;"+ direct_org_id},
 		 			{"organization_id;in;"+ direct_org_id },
 		 			{"site_id;=;"+ another_direct_site_id},
 		 			{"site_id;in;"+ another_direct_site_id},*/
 	 };
 	 	}
  
  @Test(dataProvider = "filterInfo", enabled=true)
  /**
   * 9.  get jobs by source_id filter by rps_id =X
   * 10. get jobs by source_id filter by rps_id  in [X]
   * 11. get jobs by source_id filter by datastore_id
   * 12. get jobs by source_id filter by datastore_id in [x]
   * 13. get jobs by source_id filter by policy_id
   * 14. get jobs by source_id filter by policy_id in [x]
   * 15. get jobs by source_id filter by server_id
   * 16. get jobs by source_id filter by server_id in [x]
   * 17. get jobs by source_id filter by site_id
   * 18. get jobs by source_id filter by site_id in [x]
   * 19. get jobs by source_id filter by organization_id
   * 20. get jobs by source_id filter by organization_id in [x]
   * 1. get jobs by source_id filter by source_name
   * 6. get jobs by source_id filter by source_name, it should be fuzzy search
   * 3. get jobs by source_id filter by start_time_ts <=X, it should get the job which the time range is from X-6 months to X
   * 2. get jobs by source_id filter by start_time_ts >=X,  he time range between x and now is more than 6 months
   * 7. get jobs by source_id filter by start_time_ts >=X, the time range between x and now is less than 6 months, it should use now
  
 

   * @param filterStr
   */
  public void getJobBySourceIdWithFilter(String filterStr) {
  
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		test.assignAuthor("shuo.zhang");
		spogServer.userLogin(this.final_direct_user_name_email, common_password);
			
		String[] filterStrArray = filterStr.split(";");
		ArrayList<String> source_id_List= new ArrayList<String> ();
		ArrayList<HashMap<String, Object>> expectedInfoMapArray = new 	ArrayList<HashMap<String, Object>> ();
	
		
		for(int j=0; j<2; j++){	
		  	String sourceName = spogServer.ReturnRandom("shuo_source");	  	
		  	String source_id = spogServer.createSourceWithCheck(sourceName, SourceType.machine, SourceProduct.cloud_direct, direct_org_id, direct_site_id, ProtectionStatus.unprotect, ConnectionStatus.online,OSMajor.windows.name(), "",  test);
			test.log(LogStatus.INFO,"create source " + sourceName);
			
			
			String server_id = UUID.randomUUID().toString();			
			String rps_id = UUID.randomUUID().toString();
			String datastore_id = UUID.randomUUID().toString();	
			String policy_id= UUID.randomUUID().toString();
			String message_id = "testJobMessage";
			String[] messageData = new String[] { "node", "agent"};
			source_id_List.add(source_id);
		
			for(int i=0; i<4; i++){
				String post_job_id =null;
				long start_time_ts = System.currentTimeMillis()/1000;			
				long endTimeTS = (System.currentTimeMillis()+ 1600000)/1000;
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(filterStrArray[0].equalsIgnoreCase("rps_id")){
					test.log(LogStatus.INFO,"filter is rps_id");
					if(i<2){						
						test.log(LogStatus.INFO,"post job on source "+ sourceName);
				
						post_job_id = gatewayServer.postJobWithCheck(start_time_ts,endTimeTS, direct_org_id, server_id, source_id, filterStrArray[2], datastore_id, policy_id,
								job_Type, job_Method, job_Status, direct_site_token, test);
						if(j==0){
						//	HashMap<String, Object> jobInfo = getJobInfo(post_job_id,msp_site_id,start_time_ts,server_id, source_id,sourceName,filterStrArray[2],msp_org_id,datastore_id, policy_id, null, messageData);
							HashMap<String, Object> jobInfo = getJobInfo(post_job_id,direct_site_id,start_time_ts,server_id, source_id,sourceName,filterStrArray[2],direct_org_id,datastore_id, null, null,
									policy_id, null, JobType4LatestJob.backup_full.name(), job_Status, endTimeTS, null);
							expectedInfoMapArray.add(jobInfo);
						}
					
					}else{						
						test.log(LogStatus.INFO,"post job on source "+ sourceName);
				
						post_job_id = gatewayServer.postJobWithCheck(start_time_ts,endTimeTS, direct_org_id, server_id, source_id, rps_id, datastore_id, policy_id,
								job_Type, job_Method, job_Status, direct_site_token, test);

					}				
				}else if(filterStrArray[0].equalsIgnoreCase("destination_id")){
					test.log(LogStatus.INFO,"filter is destination_id");
					if(i<2){
						test.log(LogStatus.INFO,"post job on source "+ sourceName);
		
						post_job_id = gatewayServer.postJobWithCheck(start_time_ts,endTimeTS, direct_org_id, server_id, source_id, rps_id, filterStrArray[2], policy_id,
								job_Type, job_Method, job_Status, direct_site_token, test);
						if(j==0){
				
							HashMap<String, Object> jobInfo = getJobInfo(post_job_id,direct_site_id,start_time_ts,server_id, source_id,sourceName,rps_id,direct_org_id,filterStrArray[2], null, null,
									policy_id, null, JobType4LatestJob.backup_full.name(), job_Status, endTimeTS, null);
							expectedInfoMapArray.add(jobInfo);
						}
					
					}else{
						
						test.log(LogStatus.INFO,"post job on source "+ sourceName);
					
						post_job_id = gatewayServer.postJobWithCheck(start_time_ts,endTimeTS, direct_org_id, server_id, source_id, rps_id, datastore_id, policy_id,
								job_Type, job_Method, job_Status, direct_site_token, test);

					}			
				}else if(filterStrArray[0].equalsIgnoreCase("policy_id")){
					test.log(LogStatus.INFO,"filter is policy_id");
					if(i<2){
						test.log(LogStatus.INFO,"post job on source "+ sourceName);
					//	post_job_id = gatewayServer.postJobWithCheck(start_time_ts, server_id, source_id, rps_id, datastore_id, msp_site_id, msp_org_id, message_id, filterStrArray[2], messageData, msp_site_token, test);
						post_job_id = gatewayServer.postJobWithCheck(start_time_ts,endTimeTS, direct_org_id, server_id, source_id, rps_id, datastore_id, filterStrArray[2],
								job_Type, job_Method, job_Status, direct_site_token, test);
						if(j==0){
						//	HashMap<String, Object> jobInfo = getJobInfo(post_job_id,msp_site_id,start_time_ts,server_id, source_id,sourceName,rps_id ,msp_org_id,datastore_id, filterStrArray[2], null, messageData);
							HashMap<String, Object> jobInfo = getJobInfo(post_job_id,direct_site_id,start_time_ts,server_id, source_id,sourceName,rps_id,direct_org_id,datastore_id, null, null,
									filterStrArray[2], null, JobType4LatestJob.backup_full.name(), job_Status, endTimeTS, null);
						
							expectedInfoMapArray.add(jobInfo);
						}
					
					}else{
						
						test.log(LogStatus.INFO,"post job on source "+ sourceName);
				
						post_job_id = gatewayServer.postJobWithCheck(start_time_ts,endTimeTS, direct_org_id, server_id, source_id, rps_id, datastore_id, policy_id,
								job_Type, job_Method, job_Status, direct_site_token, test);

					}	
				}else if(filterStrArray[0].equalsIgnoreCase("server_id")){
					test.log(LogStatus.INFO,"filter is server_id");
					if(i<2){
						test.log(LogStatus.INFO,"post job on source "+ sourceName);
			
						post_job_id = gatewayServer.postJobWithCheck(start_time_ts,endTimeTS, direct_org_id, filterStrArray[2], source_id, rps_id, datastore_id, policy_id,
								job_Type, job_Method, job_Status, direct_site_token, test);
						if(j==0){
					
							HashMap<String, Object> jobInfo = getJobInfo(post_job_id,direct_site_id,start_time_ts,filterStrArray[2], source_id,sourceName,rps_id,direct_org_id,datastore_id, null, null,
									policy_id, null, JobType4LatestJob.backup_full.name(), job_Status, endTimeTS, null);
							expectedInfoMapArray.add(jobInfo);
						}
					
					}else{
						
						test.log(LogStatus.INFO,"post job on source "+ sourceName);
				
						post_job_id = gatewayServer.postJobWithCheck(start_time_ts,endTimeTS, direct_org_id, server_id, source_id, rps_id, datastore_id, policy_id,
								job_Type, job_Method, job_Status, direct_site_token, test);

					}	
				}else if(filterStrArray[0].equalsIgnoreCase("site_id")){
					test.log(LogStatus.INFO,"filter is site_id");
					if(i<2){

						String site_token = siteInfo.get(filterStrArray[2]);			
						post_job_id = gatewayServer.postJobWithCheck(start_time_ts,endTimeTS,direct_org_id, server_id, source_id, rps_id, datastore_id, policy_id,
								job_Type, job_Method, job_Status, site_token, test);
						if(j==0){
					//		HashMap<String, Object> jobInfo = getJobInfo(post_job_id, filterStrArray[2],start_time_ts,server_id, source_id,sourceName,rps_id ,msp_org_id,datastore_id, policy_id, null, messageData);
							HashMap<String, Object> jobInfo = getJobInfo(post_job_id,filterStrArray[2],start_time_ts,server_id, source_id,sourceName,rps_id,direct_org_id,datastore_id, null, null,
									policy_id, null, JobType4LatestJob.backup_full.name(), job_Status, endTimeTS, null);
						
							expectedInfoMapArray.add(jobInfo);
						}
					
					}else{
						
						test.log(LogStatus.INFO,"post job on source "+ sourceName);
					//	post_job_id = gatewayServer.postJobWithCheck(start_time_ts, server_id, source_id, rps_id, datastore_id, msp_site_id, msp_org_id, message_id, policy_id, messageData, msp_site_token, test);
						post_job_id = gatewayServer.postJobWithCheck(start_time_ts,endTimeTS, direct_org_id, server_id, source_id, rps_id, datastore_id, policy_id,
								job_Type, job_Method, job_Status, direct_site_token, test);

					}	
				}else if(filterStrArray[0].equalsIgnoreCase("organization_id")){
						test.log(LogStatus.INFO,"filter is organization_id");
						test.log(LogStatus.INFO,"post job on source "+ sourceName);
					//	post_job_id = gatewayServer.postJobWithCheck(start_time_ts, server_id, source_id, rps_id, datastore_id,  msp_site_id, filterStrArray[2], message_id, policy_id, messageData, msp_site_token, test);
						post_job_id = gatewayServer.postJobWithCheck(start_time_ts,endTimeTS, filterStrArray[2], server_id, source_id, rps_id, datastore_id, policy_id,
								job_Type, job_Method, job_Status, direct_site_token, test);
						if(j==0){
						//	HashMap<String, Object> jobInfo = getJobInfo(post_job_id, msp_site_id,start_time_ts,server_id, source_id,sourceName,rps_id ,filterStrArray[2],datastore_id, policy_id, null, messageData);
							HashMap<String, Object> jobInfo = getJobInfo(post_job_id,direct_site_id,start_time_ts,server_id, source_id,sourceName,rps_id,filterStrArray[2],datastore_id, null, null,
									policy_id, null, JobType4LatestJob.backup_full.name(), job_Status, endTimeTS, null);
												
							expectedInfoMapArray.add(jobInfo);
						}
					

				}else if(filterStrArray[0].equalsIgnoreCase("source_name")){
					test.log(LogStatus.INFO,"filter is source_name");
					test.log(LogStatus.INFO,"post job on source "+ sourceName);
				
					post_job_id = gatewayServer.postJobWithCheck(start_time_ts,endTimeTS, direct_org_id, server_id, source_id, rps_id, datastore_id, policy_id,
							job_Type, job_Method, job_Status, direct_site_token, test);
					if(j==0){
						HashMap<String, Object> jobInfo = getJobInfo(post_job_id,direct_site_id,start_time_ts,server_id, source_id,sourceName,rps_id,direct_org_id,datastore_id, 
								null, null, policy_id, null, JobType4LatestJob.backup_full.name(), job_Status, endTimeTS, null);
											
						expectedInfoMapArray.add(jobInfo);
					}
				}else if(filterStrArray[0].equalsIgnoreCase("start_time_ts")){
					long filter_start_time_ts = Long.valueOf(filterStrArray[2]);
					test.log(LogStatus.INFO,"filter is start_time_ts");
					test.log(LogStatus.INFO,"post job on source "+ sourceName);
					long final_start_time_ts = 0;
					long six_month = 15552000l;
					long min_start_time_ts = filter_start_time_ts - six_month+ 24*3600;
					long in_size_month = filter_start_time_ts - 24*3600*31;
					if(i== 0){
						final_start_time_ts = min_start_time_ts;	
						
					}else if( i== 1){
						final_start_time_ts = filter_start_time_ts;
					}else if (i== 2){
						final_start_time_ts = start_time_ts;
					}else if (i== 3){
						final_start_time_ts = filter_start_time_ts - six_month- 100000;
					}
					//endTimeTS = final_start_time_ts + 1600000;
					endTimeTS = final_start_time_ts + 1600;
					post_job_id = gatewayServer.postJobWithCheck(final_start_time_ts,endTimeTS, direct_org_id, server_id, source_id, rps_id, datastore_id, policy_id,
									job_Type, job_Method, job_Status, direct_site_token, test);

					if(j==0){	
						long now = System.currentTimeMillis()/1000;
						if(filterStrArray[1].equalsIgnoreCase(">=")){
							//>= current time
							long boundary_time=0;
							if(filter_start_time_ts + six_month < now){
								boundary_time=filter_start_time_ts + six_month ;
							}else{
								boundary_time= now;
							}
							if((final_start_time_ts >= filter_start_time_ts) && (final_start_time_ts <= boundary_time)){
								HashMap<String, Object> jobInfo = getJobInfo(post_job_id,direct_site_id,final_start_time_ts,server_id, source_id,sourceName,rps_id,direct_org_id,datastore_id, 
										null, null, policy_id, null, JobType4LatestJob.backup_full.name(), job_Status, endTimeTS, null);												
								expectedInfoMapArray.add(jobInfo);
							}
							
						}else if(filterStrArray[1].equalsIgnoreCase("<=")){
							//<= current time
							if((final_start_time_ts <= filter_start_time_ts) && (final_start_time_ts >= min_start_time_ts)){
								HashMap<String, Object> jobInfo = getJobInfo(post_job_id,direct_site_id,final_start_time_ts,server_id, source_id,sourceName,rps_id,direct_org_id,datastore_id, 
										null, null, policy_id, null, JobType4LatestJob.backup_full.name(), job_Status, endTimeTS, null);												
								expectedInfoMapArray.add(jobInfo);
							}
							
						}
						filterStr = filterStrArray[0]+ ";" + filterStrArray[1] + ";" + filter_start_time_ts;
					}
					
					
				}
				
			}
			

	  }
		Collections.reverse(expectedInfoMapArray);
		test.log(LogStatus.INFO,"getJobsBySourceIDWithCheck ");
	
		
		spogServer.getJobsBySourceIDWithCheck(source_id_List.get(0), filterStr, null, -1, -1, expectedInfoMapArray.size(),expectedInfoMapArray, SpogConstants.SUCCESS_GET_PUT_DELETE, null, true, test);
		
/*		for(int i=0; i< source_id_List.size();i++){
		 	test.log(LogStatus.INFO,"delete source");
			spogServer.errorHandle.printInfoMessageInDebugFile("delete source");
			Response response = spogServer.deleteSourcesById(spogServer.getJWTToken(), source_id_List.get(i), test);
			spogServer.deleteSourcesWithCheck(response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		}
*/
	  
	  
  }
  /**22. get jobs by source_id filter by start_time >=X & start_time <=Y
   * 24. get jobs by source_id sort by start_time desc
   * 25. get jobs by source_id use page=X & page_size=Y
   * 26. get jobs by source_id use customed page, sort and filter together
   */
  @Test( enabled=true)
  public void getJobBySourceIdWithSort() {
		
		test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		test.assignAuthor("shuo.zhang");
	  	spogServer.userLogin(final_direct_user_name_email, common_password);
	
		ArrayList<HashMap<String, Object>> expectedInfoMapArray = new 	ArrayList<HashMap<String, Object>> ();
		ArrayList<HashMap<String, Object>> tempInfoMapArray = new 	ArrayList<HashMap<String, Object>> ();
		ArrayList<HashMap<String, Object>> tempInfoMapArray_1 = new 	ArrayList<HashMap<String, Object>> ();
		ArrayList<String> source_id_List= new ArrayList<String> ();
		long start_time_ts = System.currentTimeMillis()/1000;
		long min_start_ts = start_time_ts;
		long max_start_ts = start_time_ts+ 50000;
		for(int j=0; j<2; j++){
			test.log(LogStatus.INFO,"create source");
		  	String sourceName = spogServer.ReturnRandom("shuo_source");	  	
		  	String source_id = spogServer.createSourceWithCheck(sourceName, SourceType.machine, SourceProduct.udp, direct_org_id, direct_site_id, ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(), "",  test);
		
		  
			String server_id = UUID.randomUUID().toString();			
			String rps_id = UUID.randomUUID().toString();
			String datastore_id = UUID.randomUUID().toString();	
			String policy_id= UUID.randomUUID().toString();
			String message_id = "testJobMessage";
			source_id_List.add(source_id);
		
			long endTimeTS = System.currentTimeMillis()/1000;
			String[] messageData = new String[] { "node", "agent"};
			
			for(int i=0; i<6; i++){
				test.log(LogStatus.INFO,"post job on source "+ sourceName);
				start_time_ts+= 10000;
			//	String post_job_id = gatewayServer.postJobWithCheck(start_time_ts, server_id, source_id, rps_id, datastore_id, direct_site_id, direct_org_id, message_id, policy_id, messageData, direct_site_token, test);
				String post_job_id = gatewayServer.postJobWithCheck(start_time_ts,endTimeTS, direct_org_id, server_id, source_id, rps_id, datastore_id, policy_id,
						job_Type, job_Method, job_Status, direct_site_token, test);
				
				if((start_time_ts >= min_start_ts)&& (start_time_ts <= max_start_ts) && (j==0)){

					test.log(LogStatus.INFO,"compose hashmap for job info ");				
			//		HashMap<String, Object> jobInfo = getJobInfo(post_job_id,direct_site_id,start_time_ts, server_id, source_id,sourceName,rps_id,direct_org_id,datastore_id, policy_id, null, messageData);
					HashMap<String, Object> jobInfo = getJobInfo(post_job_id,direct_site_id,start_time_ts,server_id, source_id,sourceName,rps_id,direct_org_id,datastore_id, 
							null, null, policy_id, null, JobType4LatestJob.backup_full.name(), job_Status, endTimeTS, null);
					tempInfoMapArray.add(jobInfo);
				}
				
		
				
			}
	  	}
		test.log(LogStatus.INFO,"sort the jobs");
		for(int k=tempInfoMapArray.size()-1; k>=0 ;k--){
			//get desc order
			tempInfoMapArray_1.add(tempInfoMapArray.get(k));
		}
		int page_size=2;int page_number=2;
		test.log(LogStatus.INFO,"get the jobs according to paging");
		for(int m=0; m<tempInfoMapArray_1.size(); m++){
			//get page item
			if((m/page_size+1)== page_number ){
				expectedInfoMapArray.add(tempInfoMapArray_1.get(m));
			}
			
		}
		

		test.log(LogStatus.INFO,"getJobsBySourceIDWithCheck ");
		
		spogServer.getJobsBySourceIDWithCheck(source_id_List.get(0), "start_time_ts;>=;"+ min_start_ts + ",start_time_ts;<=;"+ max_start_ts, "start_time_ts;desc", 
				page_number, page_size, tempInfoMapArray_1.size(),expectedInfoMapArray, SpogConstants.SUCCESS_GET_PUT_DELETE,null, true, test);
		
	/*	spogServer.getJobsBySourceIDWithCheck(source_id_List.get(0), "site_id;=;"+ another_direct_site_id, null, 
				-1, -1, 0,new ArrayList<HashMap<String, Object>> (), SpogConstants.SUCCESS_GET_PUT_DELETE,null, true, test);*/
		
		for(int i=0; i< source_id_List.size();i++){
		 	test.log(LogStatus.INFO,"delete source");
			spogServer.errorHandle.printInfoMessageInDebugFile("delete source");
			Response response = spogServer.deleteSourcesById(spogServer.getJWTToken(), source_id_List.get(i), test);
			spogServer.deleteSourcesWithCheck(response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		}

}
  
  @Test(enabled=true)
  /**
   * 4. csr admin could get all jobs after given source_id in direct org
   * 38. csr admin could get all jobs after given source_id in msp org
   * 39. csr admin could get all jobs after given source_id in account
   * 5. direct_admin could not get all jobs of msp organization
   * 35. direct_admin could not get all jobs of account organization
   * 
   * 6. msp_admin could not get all jobs of direct organization
   * 8. account_admin could not get all jobs of direct organization
   * 36. account_admin could not get all jobs of msp organization
   * 37. account_admin could not get all jobs of other account
   * 
   * 32. msp account admin could not get all jobs of source_id in not mastered account
   * 33. msp account admin could not get all jobs of source_id in its own org
   * 34. msp account admin could not get all jobs of source_id in direct org
   * @author shuo.zhang
   * 
   */
  public void adminCannotGetJobFromOtherOrg(){
	 
	  	test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
	  	test.assignAuthor("shuo.zhang");
	  	spogServer.userLogin(this.csrAdminUserName, csrAdminPassword);

		ArrayList<String> source_id_List= new ArrayList<String> ();
		//String[] org_id_Array = new String[] {direct_org_id, msp_org_id,  another_account_id, account_id};
	//	String[] site_id_Array = new String[] {direct_site_id, msp_site_id, site_id_another_account, account_site_id};
		String[] org_id_Array = new String[] {direct_org_id,   another_account_id, account_id,  sub_msp1_account1_id, sub_msp1_account2_id, sub_msp2_account1_id, root_msp_direct_org_id};
		String[] site_id_Array = new String[] {direct_site_id,  site_id_another_account, account_site_id, this.sub_msp1_account1_site1_siteId, sub_msp1_account2_site1_siteId,sub_msp2_account1_site1_siteId,root_msp_direct_org_site1_siteId};
		
		test.log(LogStatus.INFO,"create source");
		String sourceName = spogServer.ReturnRandom("shuo_source");	  
		for(int j=0; j<7; j++){
			ArrayList<HashMap<String, Object>> expectedInfoMapArray = new 	ArrayList<HashMap<String, Object>> ();
			String source_id = spogServer.createSourceWithCheck(sourceName, SourceType.machine, SourceProduct.udp, org_id_Array[j], site_id_Array[j], ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(), "",  test);			
			long start_time_ts = System.currentTimeMillis()/1000;
			String server_id = UUID.randomUUID().toString();			
			String rps_id = UUID.randomUUID().toString();
			String datastore_id = UUID.randomUUID().toString();	
			String policy_id= UUID.randomUUID().toString();
			source_id_List.add(source_id);
			String siteToken = siteInfo.get(site_id_Array[j]);
			long endTimeTS = (System.currentTimeMillis()+40000)/1000;
			test.log(LogStatus.INFO,"post job on source "+ sourceName);
		
		
			String post_job_id = gatewayServer.postJobWithCheck(start_time_ts,endTimeTS, org_id_Array[j], server_id, source_id, rps_id, datastore_id, policy_id,
					job_Type, job_Method, job_Status, siteToken, test);
					
		
			HashMap<String, Object> jobInfo = getJobInfo(post_job_id,site_id_Array[j],start_time_ts,server_id, source_id,sourceName,rps_id,org_id_Array[j],datastore_id, null, null,
					policy_id, null, JobType4LatestJob.backup_full.name(), job_Status, endTimeTS, null);
			expectedInfoMapArray.add(jobInfo);	
			
			test.log(LogStatus.INFO,"csr admin getJobsBySourceIDWithCheck ");
			spogServer.getJobsBySourceIDWithCheck(source_id, null, null, -1, -1, expectedInfoMapArray.size(),expectedInfoMapArray, SpogConstants.SUCCESS_GET_PUT_DELETE, null,  true,test);
		}
		
		spogServer.userLogin(this.final_direct_user_name_email, common_password);
		for(int i=1; i<3; i++){
			test.log(LogStatus.INFO,"direct user getJobsBySourceIDWithCheck ");
			spogServer.getJobsBySourceIDWithCheck(source_id_List.get(i), null, null, -1, -1,-1,null, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,  true,test);
			
		}
		
		spogServer.userLogin(this.final_msp_user_name_email, common_password);
		test.log(LogStatus.INFO,"msp user getJobsBySourceIDWithCheck");
		spogServer.getJobsBySourceIDWithCheck(source_id_List.get(0), null, null, -1, -1,-1,null, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION, true, test);
		
		spogServer.userLogin(this.account_user_email, common_password);
		test.log(LogStatus.INFO,"account user getJobsBySourceIDWithCheck");
		for(int i=1; i>=0; i--){
			spogServer.getJobsBySourceIDWithCheck(source_id_List.get(i), null, null, -1, -1,-1,null, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,  true,test);
	
		}
		
		spogServer.userLogin(this.final_msp_account_admin_email, common_password);
		for(int i=1; i>=0; i--){
			spogServer.getJobsBySourceIDWithCheck(source_id_List.get(i), null, null, -1, -1,-1,null, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,  true,test);
	
		}
		
		spogServer.userLogin(this.final_root_msp_user_name_email, common_password);
		for(int i=0; i<6; i++){
			spogServer.getJobsBySourceIDWithCheck(source_id_List.get(i), null, null, -1, -1,-1,null, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,  true,test);
	
		}
		
		spogServer.userLogin(this.final_root_msp_account_admin_user_name_email, common_password);
		for(int i=0; i<6; i++){
			spogServer.getJobsBySourceIDWithCheck(source_id_List.get(i), null, null, -1, -1,-1,null, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,  true,test);
	
		}
		
		spogServer.userLogin(final_root_msp_direct_org_user_email, common_password);
		for(int i=0; i<6; i++){
			spogServer.getJobsBySourceIDWithCheck(source_id_List.get(i), null, null, -1, -1,-1,null, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,  true,test);
	
		}
		
		spogServer.userLogin(this.final_sub_msp1_user_name_email, common_password);
		for(int i=0; i<org_id_Array.length; i++){
			if(i!=3 && i!=4)
				spogServer.getJobsBySourceIDWithCheck(source_id_List.get(i), null, null, -1, -1,-1,null, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,  true,test);
	
		}
		
		spogServer.userLogin(this.final_sub_msp1_msp_account_user_name_email, common_password);
		for(int i=0; i<org_id_Array.length; i++){
			if(i!=3)
				spogServer.getJobsBySourceIDWithCheck(source_id_List.get(i), null, null, -1, -1,-1,null, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,  true,test);
	
		}
		spogServer.userLogin(this.final_sub_msp1_account1_user_email, common_password);
		for(int i=0; i<org_id_Array.length; i++){
			if(i!=3)
				spogServer.getJobsBySourceIDWithCheck(source_id_List.get(i), null, null, -1, -1,-1,null, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,  true,test);
	
		}
		
		
		
		
		
		
		spogServer.userLogin(this.csrAdminUserName, csrAdminPassword);
		
		for(int i=0; i< source_id_List.size();i++){
		 	test.log(LogStatus.INFO,"delete source");
			spogServer.errorHandle.printInfoMessageInDebugFile("delete source");
			Response response = spogServer.deleteSourcesById(spogServer.getJWTToken(), source_id_List.get(i), test);
			spogServer.deleteSourcesWithCheck(response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
		}
	

		
	  
  }
  
  @Test(enabled=true)
  /**
   *28. it will report 404 if the souce_id is not existed. 
   *29. it will report 404 if the souce_id is empty or null
   *5. if the request exceed the time range, reject the request
   */
  
  public void errorHandlingTesting(){
	
	  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
	  test.assignAuthor("shuo.zhang");
	  spogServer.userLogin(this.csrAdminUserName, csrAdminPassword);
	  test.log(LogStatus.INFO,"test source id is invalid");
	  spogServer.getJobsBySourceIDWithCheck(UUID.randomUUID().toString(), null, null, -1, -1, 0, null, SpogConstants.RESOURCE_NOT_EXIST, ErrorCode.NOT_FOUND_RESOURCE_ID, true, test);
	  test.log(LogStatus.INFO,"test source is is null");
	  spogServer.getJobsBySourceIDWithCheck("", null, null, -1, -1, 0, null, SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.ELEMENT_NOT_UUID, true, test);
	
	  test.log(LogStatus.INFO,"test time range is over 6 months is is null");
	  String sourceName = spogServer.ReturnRandom("shuo_source");	  	
	  String source_id = spogServer.createSourceWithCheck(sourceName, SourceType.machine, SourceProduct.udp, direct_org_id, direct_site_id, ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(), "",  test);
	  String filterStr="start_time_ts;>=;" + String.valueOf((System.currentTimeMillis()-15552010010l)/1000)+",start_time_ts;<=;"+ String.valueOf(System.currentTimeMillis()/1000);
	  spogServer.getJobsBySourceIDWithCheck(source_id, filterStr, null, -1, -1,-1,new  ArrayList<HashMap<String, Object>>() , SpogConstants.SUCCESS_GET_PUT_DELETE, null,  true,test);
	 
	  
	  
  }
  
  @Test(enabled=true)
  /**
   * 27. Can NOT call API if not logged in - 401when JWT is missing
   */
  public void callGetJobsBySourceIDWithoutLoggin(){
	  
	  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
	  test.assignAuthor("shuo.zhang");
	  spogServer.userLogin(this.csrAdminUserName, csrAdminPassword);
	  String sourceName = spogServer.ReturnRandom("shuo_source");	  
	  String source_id = spogServer.createSourceWithCheck(sourceName, SourceType.machine, SourceProduct.udp, direct_org_id, direct_site_id, ProtectionStatus.unprotect, ConnectionStatus.online, OSMajor.windows.name(), "",  test);
	  spogServer.getJobsBySourceIDWithCheck(source_id, null, null, -1, -1, 0, null, SpogConstants.NOT_LOGGED_IN, ErrorCode.AUTHORIZATION_HEADER_BLANK, false, test);
	  Response response = spogServer.deleteSourcesById(spogServer.getJWTToken(), source_id, test);
		spogServer.deleteSourcesWithCheck(response, SpogConstants.SUCCESS_GET_PUT_DELETE, null, test);
  }

  
  private HashMap<String, Object> getJobInfo(String post_job_id, String site_id, long start_time_ts, String server_id, String source_id, String sourceName, String rps_id, 
		  String organization_id, String destination_id,String destination_name, String destination_type, String policy_id, String plan_Name,  String job_Type, String job_Status, long end_time_ts, String job_name){

		
	    test.log(LogStatus.INFO,"compose hashmap for job info ");
		HashMap<String, Object> jobInfo = new HashMap<String, Object> ();
		jobInfo.put("job_id", post_job_id);
		if(job_name == null){
			if(destination_name!=null){
				if(job_Type.toLowerCase().contains("backup")){
					job_name = "Backup from " + sourceName + " to " + destination_name;
				}else{
					job_name = job_Type + " from " + sourceName + " to " + destination_name;
				}
		
			}else{
				if(job_Type.toLowerCase().contains("backup")){
					job_name = "Backup from " + sourceName ;
				}else{
					job_name = job_Type + " from " + sourceName ;
				}
		
			
		
			}
			
		}
		jobInfo.put("job_name", job_name);
		jobInfo.put("job_type", job_Type);
		jobInfo.put("job_status", job_Status);
		jobInfo.put("start_time_ts", (int)start_time_ts);
		jobInfo.put("severity",  null);
		jobInfo.put("end_time_ts", (int)end_time_ts);
		long duration = end_time_ts- start_time_ts;
		jobInfo.put("duration", (int)duration );
	//	jobInfo.put("duration", duration );
		jobInfo.put("server_id", server_id);
		jobInfo.put("source_id", source_id);
		jobInfo.put("source_name", sourceName);
		jobInfo.put("rps_id", rps_id);
		jobInfo.put("destination_id", destination_id);
		if(destination_name!=null){
			jobInfo.put("destination_name", destination_name);
			jobInfo.put("destination_type", destination_type);
		}

		jobInfo.put("site_id", site_id);
		HashMap<String, Object> orgInfo = new HashMap<String, Object> ();
		orgInfo.put("organization_id", organization_id);
		spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
		orgInfo.put("organization_name", spogServer.getOrganizationNameByID(organization_id));
		orgInfo.put("blocked", null);
		orgInfo.put("organization_type", spogServer.GetOrganizationTypebyID(organization_id));
		if( spogServer.GetOrganizationSubMSPByID(organization_id))
			orgInfo.put("sub_msp",true);
		else
			orgInfo.put("sub_msp",null);
		
		jobInfo.put("organization", orgInfo);
	//	jobInfo.put("policy_id", policy_id);
		jobInfo.put("available_actions", new ArrayList<String>());
		
	
		//in later will have plan_name
		return jobInfo;

	
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

  
}
