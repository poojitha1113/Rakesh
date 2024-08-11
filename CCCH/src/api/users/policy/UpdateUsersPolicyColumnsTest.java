package api.users.policy;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
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

import Constants.ErrorCode;
import Constants.SpogConstants;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGServer;
import InvokerServer.Source4SPOGServer;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;

public class UpdateUsersPolicyColumnsTest extends base.prepare.PrepareOrgInfo{
	
	@Parameters({ "pmfKey"})
	 public UpdateUsersPolicyColumnsTest(String pmfKey) {
		super(pmfKey);
		// TODO Auto-generated constructor stub
	}



	private SPOGServer spogServer;
	 private Policy4SPOGServer policy4SpogServer;
	  private UserSpogServer userSpogServer;
	  private Source4SPOGServer sourceSpogServer;
	
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

	  private String prefix_msp = "spogqa_shuo_msp";
	  private String msp_org_name = prefix_msp + "_org";

	  private String msp_user_name = prefix_msp + "_admin";
	  private String msp_user_name_email = prefix_msp + "_admin" + postfix_email;
	  private String msp_user_first_name = msp_user_name + "_first_name";
	  private String msp_user_last_name = msp_user_name + "_last_name";
	  
	  private String prefix_msp_account = "spogqa_shuo_msp_account";
	  private String msp_account_admin_name = prefix_msp_account + "_admin";
	  private String msp_account_admin_email =msp_account_admin_name + postfix_email;
	  private String msp_account_admin_first_name = msp_account_admin_name + "_first_name";
	  private String msp_account_admin_last_name = msp_account_admin_name + "_last_name";
	  private String msp_account_admin_id;
	  private String final_msp_account_admin_email;

	  private String msp_org_id=null;
	  private String final_msp_user_name_email=null;	    
	  private String account_id;
	  private String account_user_email;
	  private String msp_user_id;
	  private String account_user_id;
	  private String another_account_id;
	  private String another_account_user_email;
	  private String another_account_user_id;
	  private String  org_model_prefix=this.getClass().getSimpleName();
	private String csr_read_only_id;

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

		 
	  @BeforeClass
	  @Parameters({ "baseURI", "port","logFolder","buildVersion" ,"csrAdminUserName", "csrAdminPassword", "csrReadOnlyAdminUserName","csrReadOnlyAdminPassword"})
	  public void beforeClass(String baseURI, String port, String logFolder,  String buildVersion,  String adminUserName, String adminPassword, String csrROAdminUserName, String csrROPwd ) throws UnknownHostException {
		
		  	spogServer = new SPOGServer(baseURI, port);
		  	userSpogServer = new UserSpogServer(baseURI, port);
		  	policy4SpogServer =new Policy4SPOGServer(baseURI,port);
		  	sourceSpogServer =new Source4SPOGServer(baseURI,port);
			this.BQName = this.getClass().getSimpleName();
		  	rep = ExtentManager.getInstance(BQName,logFolder);
		  	this.csrAdminUserName = adminUserName;
		  	this.csrAdminPassword = adminPassword;
		  	 this.csrReadOnlyAdminUserName = csrROAdminUserName;
			  this.csrReadOnlyAdminPassword = csrROPwd;
		    test = rep.startTest("beforeClass");
			spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
			//*******************create direct org,user,site**********************/
			String prefix = RandomStringUtils.randomAlphanumeric(8);
			test.log(LogStatus.INFO,"create a direct org");
			direct_org_id = spogServer.CreateOrganizationWithCheck(prefix+direct_org_name+org_model_prefix , SpogConstants.DIRECT_ORG, null, null, null, null, test);
			final_direct_user_name_email = prefix + direct_user_name_email;
			
			test.log(LogStatus.INFO,"create a admin under direct org");
			direct_user_id = spogServer.createUserAndCheck(final_direct_user_name_email, common_password, prefix + direct_user_first_name, prefix + direct_user_last_name, SpogConstants.DIRECT_ADMIN, direct_org_id, test);
			spogServer.userLogin(final_direct_user_name_email, common_password);
		  	

			//************************create msp org,user,site*************************************
			spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
			prefix = RandomStringUtils.randomAlphanumeric(8);
			test.log(LogStatus.INFO,"create a msp org");
			msp_org_id = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name+org_model_prefix , SpogConstants.MSP_ORG, null, null, null, null, test);
			final_msp_user_name_email = prefix + msp_user_name_email;
			
			test.log(LogStatus.INFO,"create a admin under msp org");
			msp_user_id = spogServer.createUserAndCheck(final_msp_user_name_email, common_password, prefix + msp_user_first_name, prefix + msp_user_last_name, SpogConstants.MSP_ADMIN, msp_org_id, test);
			spogServer.userLogin(final_msp_user_name_email, common_password);
		  	
			test.log(LogStatus.INFO,"create a msp account admin under msp org");
			final_msp_account_admin_email = prefix + this.msp_account_admin_email;
			msp_account_admin_id = spogServer.createUserAndCheck(final_msp_account_admin_email, common_password, prefix + msp_account_admin_first_name, prefix + msp_account_admin_last_name, SpogConstants.MSP_ACCOUNT_ADMIN, msp_org_id, test);
			
			//create account, account user and site
			test.log(LogStatus.INFO,"Creating a account For msp org");
			account_id = spogServer.createAccountWithCheck(msp_org_id, "sub_" + prefix + msp_org_name, "", test);
			prefix = RandomStringUtils.randomAlphanumeric(8);
		
			test.log(LogStatus.INFO,"Creating a account admin For account org");
			account_user_email = prefix + msp_user_name_email;
			account_user_id = spogServer.createUserAndCheck(account_user_email, common_password, prefix + msp_user_first_name, prefix + msp_user_last_name, SpogConstants.DIRECT_ADMIN, account_id, test);
		
			
			//create account, account user 
			test.log(LogStatus.INFO,"Creating another account For msp org");
			prefix = RandomStringUtils.randomAlphanumeric(8);
			another_account_id = spogServer.createAccountWithCheck(msp_org_id, "sub_" + prefix + msp_org_name, "", test);
		  	
			test.log(LogStatus.INFO,"Creating a account admin For another account org");
			another_account_user_email = prefix + msp_user_name_email;
			another_account_user_id = spogServer.createUserAndCheck(another_account_user_email, common_password, prefix + msp_user_first_name, prefix + msp_user_last_name, SpogConstants.DIRECT_ADMIN, another_account_id, test);
			

			spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
			test.log(LogStatus.INFO,"assign account to msp account admin");
			String[] mspAccountAdmins = new String []{msp_account_admin_id};
			userSpogServer.assignMspAccountAdmins(msp_org_id, account_id, mspAccountAdmins , spogServer.getJWTToken()); 
			
			spogServer.userLogin(this.csrReadOnlyAdminUserName, this.csrReadOnlyAdminPassword);
			csr_read_only_id = spogServer.GetLoggedinUser_UserID();
			prepare(baseURI, port, logFolder, this.csrAdminUserName,  this.csrAdminPassword, this.getClass().getSimpleName() );
			
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
	  
	  @DataProvider(name = "organizationAndUserInfo")
		public final Object[][] getOrganizationAndUserInfo() {
			return new Object[][] {
			/*	 {final_direct_user_name_email, common_password,"Name;true;1,Protected Sources;false;2",final_direct_user_name_email, common_password, 
					 "Protected Sources;false;2", SpogConstants.DIRECT_ADMIN, direct_org_id, null,SpogConstants.SUCCESS_GET_PUT_DELETE, null}, 
				 {final_msp_user_name_email,  common_password,"Unprotected Sources;true;3,Source Group;true;2,Description;true;1", final_direct_user_name_email, common_password,
						 "Protected Sources;false;2", 	 SpogConstants.MSP_ADMIN, msp_org_id, null, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				 {final_msp_account_admin_email,  common_password,"Unprotected Sources;true;3,Source Group;true;2,Description;true;1", final_direct_user_name_email, common_password,
							 "Protected Sources;false;2", 		null, null, this.msp_account_admin_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				 {account_user_email,  common_password,"Unprotected Sources;true;3,Source Group;true;2,Description;true;1", final_direct_user_name_email, common_password,
								 "Protected Sources;false;2", 		null, null, this.account_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
								 
				 {final_direct_user_name_email, common_password,"Name;true;1,Protected Sources;false;2",final_msp_user_name_email, common_password, 
									 "Protected Sources;false;2,Latest Job;true;1", 		 SpogConstants.DIRECT_ADMIN, direct_org_id, null,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 
				 {final_msp_user_name_email,  common_password,"Unprotected Sources;true;3,Source Group;true;2,Description;true;1", final_msp_user_name_email, common_password,
									 "Protected Sources;false;2,Latest Job;true;1,Status;true;3,Name;false;4",  SpogConstants.MSP_ADMIN, msp_org_id, null, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				 {final_msp_account_admin_email,  common_password,"Unprotected Sources;true;3,Source Group;true;2,Description;true;1", final_msp_user_name_email, common_password,
									 "Protected Sources;false;2,Latest Job;true;1,Status;true;3,Name;false;4", null, null, msp_account_admin_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				 {account_user_email,  common_password, "Name;true;1,Protected Sources;false;2", final_msp_user_name_email, common_password, 	 "Protected Sources;false;2,Latest Job;true;1,Status;true;3,Name;false;4",
											 SpogConstants.DIRECT_ADMIN, account_id, 	 null,SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				 
				
				 {final_direct_user_name_email, common_password,"Name;true;1,Protected Sources;false;2",final_msp_account_admin_email, common_password, 
												 "Protected Sources;false;2,Latest Job;true;1", SpogConstants.DIRECT_ADMIN, direct_org_id, null,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 
				 {final_msp_user_name_email, common_password,"Name;true;1,Protected Sources;false;2",final_msp_account_admin_email, common_password, 
													 "Protected Sources;false;2,Latest Job;true;1",  SpogConstants.MSP_ADMIN, msp_org_id, null,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 
				 {final_msp_account_admin_email, common_password,"Name;true;1,Protected Sources;false;2",final_msp_account_admin_email, common_password, 
														 "Protected Sources;false;2,Latest Job;true;1",  null, null, msp_account_admin_id,SpogConstants.SUCCESS_GET_PUT_DELETE, null}, 
				 {account_user_email, common_password,"Name;true;1,Protected Sources;false;2",final_msp_account_admin_email, common_password, 
															 "Protected Sources;false;2,Latest Job;true;1",  null, null,this.account_user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null}, 
				 {another_account_user_email, common_password,"Name;true;1,Protected Sources;false;2",final_msp_account_admin_email, common_password, 
																 "Protected Sources;false;2,Latest Job;true;1",  null, null, this.another_account_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 
				 			
				 
			
				 {final_direct_user_name_email, common_password,"Name;true;1,Protected Sources;false;2",account_user_email, common_password, 
						 "Protected Sources;false;2,Latest Job;true;1", 	 SpogConstants.DIRECT_ADMIN, direct_org_id, null,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 
				 {final_msp_user_name_email, common_password,"Name;true;1,Protected Sources;false;2",account_user_email, common_password, 
							 "Protected Sources;false;2,Latest Job;true;1", SpogConstants.MSP_ADMIN, msp_org_id, null,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 
				 {final_msp_account_admin_email, common_password,"Name;true;1,Protected Sources;false;2",account_user_email, common_password, 
								 "Protected Sources;false;2,Latest Job;true;1",  null, null, msp_account_admin_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},			 
			     {account_user_email,  common_password, "Name;true;1,Protected Sources;false;2", account_user_email, common_password, 
									 "Protected Sources;false;2,Latest Job;true;1",  SpogConstants.DIRECT_ADMIN, account_id,  null,SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			     {another_account_user_email, common_password,"Name;true;1,Protected Sources;false;2",account_user_email, common_password, 
										 "Protected Sources;false;2,Latest Job;true;1",  null, null, this.another_account_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 
				   
				  
	
				  {final_direct_user_name_email, common_password,"Name;true;1,Protected Sources;false;2",this.csrAdminUserName, this.csrAdminPassword, 
						 "Protected Sources;none;none", 		 SpogConstants.DIRECT_ADMIN, direct_org_id, null,SpogConstants.SUCCESS_GET_PUT_DELETE, null}, 
				  {final_msp_user_name_email,  common_password,"Unprotected Sources;true;3,Source Group;true;2,Description;true;1", this.csrAdminUserName, this.csrAdminPassword,
							 "Protected Sources;none;2", 			 SpogConstants.MSP_ADMIN, msp_org_id, null, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				  {final_msp_account_admin_email,  common_password, "Name;true;1,Protected Sources;false;2", this.csrAdminUserName, this.csrAdminPassword, 
								 "Protected Sources;false;none,Latest Job;true;1",  null, null ,	 msp_account_admin_id,SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				  {account_user_email,  common_password, "Name;true;1,Protected Sources;false;2", this.csrAdminUserName, this.csrAdminPassword,  
									 "Protected Sources;false;2,Protected Sources;true;1",  SpogConstants.DIRECT_ADMIN, account_id,null,SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				  
				 {final_direct_user_name_email, common_password,"Name;true;1,Protected Sources;false;2",csrReadOnlyAdminUserName, csrReadOnlyAdminPassword, 
									 "Protected Sources;false;2,Latest Job;true;1", null, null, direct_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 
				 {final_msp_user_name_email, common_password,"Name;true;1,Protected Sources;false;2",csrReadOnlyAdminUserName, csrReadOnlyAdminPassword, 
										 "Protected Sources;false;2,Latest Job;true;1", null, null, this.msp_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 
				 {final_msp_account_admin_email, common_password,"Name;true;1,Protected Sources;false;2",csrReadOnlyAdminUserName, csrReadOnlyAdminPassword, 
											 "Protected Sources;false;2,Latest Job;true;1",  null, null, msp_account_admin_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},			 
				 {account_user_email,  common_password, "Name;true;1,Protected Sources;false;2", csrReadOnlyAdminUserName, csrReadOnlyAdminPassword, 
												 "Protected Sources;false;2,Latest Job;true;1",  null, null,  this.account_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				 {csrReadOnlyAdminUserName, csrReadOnlyAdminPassword, "Unprotected Sources;true;3,Source Group;true;2,Description;true;1", csrReadOnlyAdminUserName, csrReadOnlyAdminPassword, 
													 "Protected Sources;false;2", null, null, csr_read_only_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null},	
				 {csrReadOnlyAdminUserName, csrReadOnlyAdminPassword, "Unprotected Sources;true;3,Source Group;true;2,Description;true;1", this.csrAdminUserName, this.csrAdminPassword, 
														 "Protected Sources;false;2", null, null, csr_read_only_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null},		*/
				
				
				 {final_root_msp_user_name_email, common_password,"Name;true;1,Protected Sources;false;2",final_root_msp_user_name_email, common_password, 
						 "Protected Sources;false;2",null, null, root_msp_user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null}, 						 
				{final_root_msp_account_admin_user_name_email, common_password,"Name;true;1,Protected Sources;false;2",final_root_msp_user_name_email, common_password, 
											 "Protected Sources;false;2",	 null, null, root_msp_account_admin_user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null}, 
				{final_root_msp_direct_org_user_email, common_password,"Name;true;1,Protected Sources;false;2",final_root_msp_user_name_email, common_password, 
												 "Protected Sources;false;2",	 null, null, root_msp_direct_org_user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null}, 
				{final_sub_msp1_user_name_email, common_password,"Name;true;1,Protected Sources;false;2",final_root_msp_user_name_email, common_password, 
													 "Protected Sources;false;2",	 null, null, sub_msp1_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 
				{final_sub_msp1_msp_account_user_name_email, common_password,"Name;true;1,Protected Sources;false;2",final_root_msp_user_name_email, common_password, 
														 "Protected Sources;false;2",	 null, null, sub_msp1_msp_account_admin_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 
				{final_sub_msp1_account1_user_email, common_password,"Name;true;1,Protected Sources;false;2",final_root_msp_user_name_email, common_password, 
															 "Protected Sources;false;2",		 null, null, sub_msp1_account1_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 

				{final_root_msp_user_name_email, common_password,"Name;true;1,Protected Sources;false;2",final_root_msp_account_admin_user_name_email, common_password, 
																 "Protected Sources;false;2", null, null, root_msp_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 						 
				{final_root_msp_account_admin_user_name_email, common_password,"Name;true;1,Protected Sources;false;2",final_root_msp_account_admin_user_name_email, common_password, 
																	 "Protected Sources;false;2",	 null, null, root_msp_account_admin_user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null}, 
				{final_root_msp_direct_org_user_email, common_password,"Name;true;1,Protected Sources;false;2",final_root_msp_account_admin_user_name_email, common_password, 
																		 "Protected Sources;false;2",	 null, null, root_msp_direct_org_user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null}, 
				{final_sub_msp1_user_name_email, common_password,"Name;true;1,Protected Sources;false;2",final_root_msp_account_admin_user_name_email, common_password, 
																			 "Protected Sources;false;2",	 null, null, sub_msp1_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 
				{final_sub_msp1_msp_account_user_name_email, common_password,"Name;true;1,Protected Sources;false;2",final_root_msp_account_admin_user_name_email, common_password, 
																				 "Protected Sources;false;2",	 null, null, sub_msp1_msp_account_admin_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 
				{final_sub_msp1_account1_user_email, common_password,"Name;true;1,Protected Sources;false;2",final_root_msp_account_admin_user_name_email, common_password, 
																					 "Protected Sources;false;2",		 null, null, sub_msp1_account1_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 


				 {final_root_msp_user_name_email, common_password,"Name;true;1,Protected Sources;false;2",final_root_msp_direct_org_user_email, common_password, 
																						 "Protected Sources;false;2", null, null, root_msp_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 						 
				{final_root_msp_account_admin_user_name_email, common_password,"Name;true;1,Protected Sources;false;2",final_root_msp_direct_org_user_email, common_password, 
																							 "Protected Sources;false;2",					 null, null, root_msp_account_admin_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 
				{final_root_msp_direct_org_user_email, common_password,"Name;true;1,Protected Sources;false;2",final_root_msp_direct_org_user_email, common_password, 
																								 "Protected Sources;false;2",						 null, null, root_msp_direct_org_user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null}, 
				{final_sub_msp1_user_name_email, common_password,"Name;true;1,Protected Sources;false;2",final_root_msp_direct_org_user_email, common_password, 
																									 "Protected Sources;false;2",						 null, null, sub_msp1_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 
				{final_sub_msp1_msp_account_user_name_email, common_password,"Name;true;1,Protected Sources;false;2",final_root_msp_direct_org_user_email, common_password, 
																										 "Protected Sources;false;2",							 null, null, sub_msp1_msp_account_admin_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 
				{final_sub_msp1_account1_user_email, common_password,"Name;true;1,Protected Sources;false;2",final_root_msp_direct_org_user_email, common_password, 
																											 "Protected Sources;false;2",						 null, null, sub_msp1_account1_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 
				
				{final_root_msp_user_name_email, common_password,"Name;true;1,Protected Sources;false;2",final_sub_msp1_user_name_email, common_password, 
																												 "Protected Sources;false;2",				 null, null, root_msp_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 						 
				{final_root_msp_account_admin_user_name_email, common_password,"Name;true;1,Protected Sources;false;2",final_sub_msp1_user_name_email, common_password, 
																													 "Protected Sources;false;2",				 null, null, root_msp_account_admin_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 
				{final_root_msp_direct_org_user_email, common_password,"Name;true;1,Protected Sources;false;2",final_sub_msp1_user_name_email, common_password, 
																														 "Protected Sources;false;2",					 null, null, root_msp_direct_org_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 
				{final_sub_msp1_user_name_email, common_password,"Name;true;1,Protected Sources;false;2",final_sub_msp1_user_name_email, common_password, 
																															 "Protected Sources;false;2",					 null, null, sub_msp1_user_id,  SpogConstants.SUCCESS_GET_PUT_DELETE, null}, 
				{final_sub_msp1_msp_account_user_name_email, common_password,"Name;true;1,Protected Sources;false;2",final_sub_msp1_user_name_email, common_password, 
																																 "Protected Sources;false;2",						 null, null, sub_msp1_msp_account_admin_id,  SpogConstants.SUCCESS_GET_PUT_DELETE, null}, 
				{final_sub_msp1_account1_user_email, common_password,"Name;true;1,Protected Sources;false;2",final_sub_msp1_user_name_email, common_password, 
																																	 "Protected Sources;false;2",					 null, null, sub_msp1_account1_user_id,  SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				{final_sub_msp2_account1_user_email, common_password,"Name;true;1,Protected Sources;false;2",final_sub_msp1_user_name_email, common_password, 
																																		 "Protected Sources;false;2",				 null, null, sub_msp2_account1_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 
				
				
				
				{final_root_msp_user_name_email, common_password,"Name;true;1,Protected Sources;false;2",final_sub_msp1_msp_account_user_name_email, common_password, 
																																			 "Protected Sources;false;2",	 null, null, root_msp_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 						 
				{final_root_msp_account_admin_user_name_email, common_password,"Name;true;1,Protected Sources;false;2",final_sub_msp1_msp_account_user_name_email, common_password, 
																																				 "Protected Sources;false;2",	 null, null, root_msp_account_admin_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 
				{final_root_msp_direct_org_user_email, common_password,"Name;true;1,Protected Sources;false;2",final_sub_msp1_msp_account_user_name_email, common_password, 
																																					 "Protected Sources;false;2",		 null, null, root_msp_direct_org_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 
				{final_sub_msp1_user_name_email, common_password,"Name;true;1,Protected Sources;false;2",final_sub_msp1_msp_account_user_name_email, common_password, 
																																						 "Protected Sources;false;2",		 null, null, sub_msp1_user_id,  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 
				{final_sub_msp1_msp_account_user_name_email, common_password,"Name;true;1,Protected Sources;false;2",final_sub_msp1_msp_account_user_name_email, common_password, 
																																							 "Protected Sources;false;2",		 null, null, sub_msp1_msp_account_admin_id,  SpogConstants.SUCCESS_GET_PUT_DELETE, null}, 
				{final_sub_msp1_account1_user_email, common_password,"Name;true;1,Protected Sources;false;2",final_sub_msp1_msp_account_user_name_email, common_password, 
																																								 "Protected Sources;false;2",			 null, null, sub_msp1_account1_user_id,  SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				{final_sub_msp1_account2_user_email, common_password,"Name;true;1,Protected Sources;false;2",final_sub_msp1_msp_account_user_name_email, common_password, 
																																									 "Protected Sources;false;2",			 null, null, sub_msp1_account2_user_id,   SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{final_sub_msp2_account1_user_email, common_password,"Name;true;1,Protected Sources;false;2",final_sub_msp1_msp_account_user_name_email, common_password, 
																																										 "Protected Sources;false;2",	 null, null, sub_msp2_account1_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 
				
				
				
				{final_root_msp_user_name_email, common_password,"Name;true;1,Protected Sources;false;2",final_sub_msp1_account1_user_email, common_password, 
																																											 "Protected Sources;false;2",		 null, null, root_msp_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 						 
				{final_root_msp_account_admin_user_name_email, common_password,"Name;true;1,Protected Sources;false;2",final_sub_msp1_account1_user_email, common_password, 
																																												 "Protected Sources;false;2",			 null, null, root_msp_account_admin_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 
				{final_root_msp_direct_org_user_email, common_password,"Name;true;1,Protected Sources;false;2",final_sub_msp1_account1_user_email, common_password, 
																																													 "Protected Sources;false;2",				 null, null, root_msp_direct_org_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 
				{final_sub_msp1_user_name_email, common_password,"Name;true;1,Protected Sources;false;2",final_sub_msp1_account1_user_email, common_password, 
																																														 "Protected Sources;false;2",				 null, null, sub_msp1_user_id,  SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 
				{final_sub_msp1_msp_account_user_name_email, common_password,"Name;true;1,Protected Sources;false;2",final_sub_msp1_account1_user_email, common_password, 
																																															 "Protected Sources;false;2",				 null, null, sub_msp1_msp_account_admin_id,   SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 
				{final_sub_msp1_account1_user_email, common_password,"Name;true;1,Protected Sources;false;2",final_sub_msp1_account1_user_email, common_password, 
																																																 "Protected Sources;false;2",					 null, null, sub_msp1_account1_user_id,  SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				{final_sub_msp1_account2_user_email, common_password,"Name;true;1,Protected Sources;false;2",final_sub_msp1_account1_user_email, common_password, 
																																																	 "Protected Sources;false;2",					 null, null, sub_msp1_account2_user_id,   SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{final_sub_msp2_account1_user_email, common_password,"Name;true;1,Protected Sources;false;2",final_sub_msp1_account1_user_email, common_password, 
																																																		 "Protected Sources;false;2",			 null, null, sub_msp2_account1_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 
				


			};
		}
	  
	  @Test(dataProvider ="organizationAndUserInfo", enabled=true)
	  /**
	   * 1. direct admin could update in the same organization's specific user's policy columns
		2. direct admin could not update msp organization's user's policy columns, report 403
		3. direct admin could not update msp account admin's policy columns
		4. direct admin could not update  account admin's policy columns, report 403
		5. msp admin could not update direct organization's user's policy columns, report 403
		6. msp admin could update  in the same organization's specific user's policy columns
		7. msp  admin could update msp account admin's policy columns
		8. msp admin could update in the account's specific user's policy columns
		9. msp account admin could not update direct admin's policy columns in direct org
		10. msp account admin could not update msp admin's policy columns
		11. MSP account admin could update its own policy columns
		12. msp account admin could update mastered account's user's policy columns
		13. msp account admin could not update not mastered account's user's policy columns
		14. account admin could not update direct organization's user's policy columns, report 403
		15. account admin could not update msp organization's user's policy columns, report 403
		16. account admin could not update msp account admin's policy columns
		17. account admin could not update other account's user's policy columns, report 403
		18. account admin could update  in the same organization's specific user's policy columns
		19. csr admin could update direct user's policy columns
		20. csr admin could update msp user's policy columns
		21. csr admin could update msp account admin user's policy columns
		22. csr admin could update account user's policy columns
		23. csr admin could update its own policy columns
		32. update policy column only with column_id
		33. update policy column with column_id and order_id
		34. update policy column with column_id and visible
		35. set two columns with same column_id, it should use the first one
		36. set visible as true
		37. set visible as false



	   * @param createUserPolicyColumnAdmin
	   * @param createPassword
	   * @param createdPolicyColumns
	   * @param updateUserPolicyColumnAdmin
	   * @param updatePassword
	   * @param updatedPolicyColumns
	   * @param role_id
	   * @param organization_id
	   * @param created_user_id
	   * @param statusCode
	   * @param errorCode
	   */
	  public void adminCanUpdateUsersPolicyColumns(String createUserPolicyColumnAdmin, String createPassword, String createdPolicyColumns, String updateUserPolicyColumnAdmin, 
			  String updatePassword, String updatedPolicyColumns,  String role_id, String organization_id, String created_user_id, int statusCode, String errorCode){
		  
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("shuo.zhang");
		  spogServer.errorHandle.printInfoMessageInDebugFile("/****************adminCanUpdateUsersPolicyColumns**************/");
		  spogServer.userLogin(createUserPolicyColumnAdmin, createPassword, test);
		  
		  if(created_user_id==null){
			  test.log(LogStatus.INFO,"create user");
			  String finalUserEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + createUserPolicyColumnAdmin;
			  created_user_id = spogServer.createUserAndCheck(finalUserEmail, createPassword, RandomStringUtils.randomAlphanumeric(8) , RandomStringUtils.randomAlphanumeric(8) , role_id, organization_id, test);
			  
		  }
		  //create user source column
		  String token = spogServer.getJWTToken();
		  test.log(LogStatus.INFO,"delete user policy columns");
		  policy4SpogServer.deleteUsersPolicyColumnsWithCheck(created_user_id, token, SpogConstants.SUCCESS_GET_PUT_DELETE, null);
		  
		  test.log(LogStatus.INFO,"get policy column array list");
		  ArrayList<HashMap<String, Object>> columnsList =    policy4SpogServer.getPolicyColumnArrayList(createdPolicyColumns, token,test);
		  test.log(LogStatus.INFO,"create user policy columns");
		  policy4SpogServer.createUsersPolicyColumnsWithCheck(created_user_id, columnsList, token, SpogConstants.SUCCESS_POST, null);
		  
		  spogServer.userLogin(updateUserPolicyColumnAdmin, updatePassword, test);
		  token = spogServer.getJWTToken();
		  policy4SpogServer.setToken(token);
		  
		  test.log(LogStatus.INFO,"get policy column info");
		  columnsList =   policy4SpogServer.getPolicyColumnArrayList(updatedPolicyColumns, token,test);
		  test.log(LogStatus.INFO,"updateUsersPolicyColumnsWithCheck");
		  policy4SpogServer.updateUsersPolicyColumnsWithCheck(created_user_id, columnsList, token, statusCode, errorCode);
		  
		 
		  spogServer.userLogin(createUserPolicyColumnAdmin, createPassword, test);
		  token = spogServer.getJWTToken();
		  policy4SpogServer.setToken(token);
		  
		  test.log(LogStatus.INFO,"delete user source columns");
		  policy4SpogServer.deleteUsersPolicyColumnsWithCheck(created_user_id, token, SpogConstants.SUCCESS_GET_PUT_DELETE, null);
		
	 }
	  
	  @Test(enabled=true)
	  /**
		39. fail to update user's policy column with invalid token, report 401
		40. fail to update user's policy column without login, report 401

	   */
	  public void updateUsersPolicyColumnsWithoutLogin(){
		  
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("shuo.zhang");
		  spogServer.errorHandle.printInfoMessageInDebugFile("/****************" + this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName() + "**************/");
		  spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword, test);
		  		
		  String token = spogServer.getJWTToken();
		  test.log(LogStatus.INFO,"updateUsersPolicyColumnsWithoutLogin");
		  policy4SpogServer.updateUsersPolicyColumnsWithoutLoginAndCheck(direct_user_id);
		  test.log(LogStatus.INFO,"updateUsersPolicyColumns With invalide token");	  
		  ArrayList<HashMap<String, Object>> columnsList = new ArrayList<HashMap<String, Object>>();
		  policy4SpogServer.updateUsersPolicyColumnsWithCheck(direct_user_id, columnsList, UUID.randomUUID().toString(), SpogConstants.NOT_LOGGED_IN,  ErrorCode.AUTHORIZATION_HEADER_BLANK);
		  
		  
	 }
	  
	  @DataProvider(name = "organizationAndUserInfo1")
			public final Object[][] getOrganizationAndUserInfo1() {
				return new Object[][] {
					{UUID.randomUUID().toString(),"true", "1",direct_user_id, SpogConstants.RESOURCE_NOT_EXIST, ErrorCode.COLUMN_NOT_EXIST}, 
					{null,"true","1", direct_user_id, SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.ELEMENT_BLANK},
					{"","true", "1", direct_user_id, SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.ELEMENT_BLANK}, 
					{"899fae72-3bc0-493b-8ca8-ca002e766d52","true", "0", direct_user_id, SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.INVALID_ORDER_ID}, 
					{"899fae72-3bc0-493b-8ca8-ca002e766d52","true", "-5", direct_user_id, SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.INVALID_ORDER_ID},
					{"899fae72-3bc0-493b-8ca8-ca002e766d52","true", "1", UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST, ErrorCode.USER_NOT_EXIST_OR_INACTIVE}, 
					{"899fae72-3bc0-493b-8ca8-ca002e766d52","true", "1",null, SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.ELEMENT_NOT_UUID}, 
					{"899fae72-3bc0-493b-8ca8-ca002e766d52","true", "1","", SpogConstants.RESOURCE_NOT_EXIST, ErrorCode.RESOURCE_NOT_FOUND},
					{"899fae72-3bc0-493b-8ca8-ca002e766d52","true", "15",msp_user_id, SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.ORDER_ID_INVALID}, 
					{"899fae72-3bc0-493b-8ca8-ca002e766d52","true", "1",msp_user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				};
			}

		
		/**
		 *24. set user_id as  "", update policy column, report 404
			25. set user_id as null, update policy column, report 400
			26. set user_id as non-existing id, update policy column, report 404
			27. set order_id as negative number, it should report error 400
			28. set order_id more than 14, report error 400
			29. set column id as  "", report 400
			30. set column id as null , report 400
			31. set column_id as non-exsiting id, report 404
			38. if create source column is not called, update source column should be successful

		 * 

		 */
		@Test(dataProvider ="organizationAndUserInfo1", enabled=true)
		
		public void errorHandling( String column_id, String visible, String order_id, String user_id, int statusCode, String errorCode){
			  
			  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
			  test.assignAuthor("shuo.zhang");
			  spogServer.errorHandle.printInfoMessageInDebugFile("/****************errorHandling**************/");
			  spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
			  
					
			  String token = spogServer.getJWTToken();
			  test.log(LogStatus.INFO,"get policy column info");
			  ArrayList<HashMap<String, Object>> columnsList = new ArrayList<HashMap<String, Object>> ();
			  HashMap<String, Object> columnInfo = sourceSpogServer.getSourceColumnInfo(column_id, visible, order_id);
			  columnsList.add(columnInfo);
			  if(statusCode == SpogConstants.SUCCESS_GET_PUT_DELETE){
				  test.log(LogStatus.INFO,"delete user source columns");
				  policy4SpogServer.deleteUsersPolicyColumnsWithCheck(user_id, token, SpogConstants.SUCCESS_GET_PUT_DELETE, null);
				
			  }
			  test.log(LogStatus.INFO,"updateUsersPolicyColumnsWithCheck");
			  policy4SpogServer.updateUsersPolicyColumnsWithCheck(user_id, columnsList, token, statusCode, errorCode);
			  if(statusCode == SpogConstants.SUCCESS_GET_PUT_DELETE){
				  test.log(LogStatus.INFO,"delete user source columns");
				  policy4SpogServer.deleteUsersPolicyColumnsWithCheck(user_id, token, SpogConstants.SUCCESS_GET_PUT_DELETE, null);
				
			  }
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
	    rep.endTest(test);    
	}

	
}
