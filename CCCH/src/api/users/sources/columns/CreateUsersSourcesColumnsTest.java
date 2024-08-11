package api.users.sources.columns;

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
import InvokerServer.GatewayServer;
import InvokerServer.Org4SPOGServer;
import InvokerServer.SPOGServer;
import InvokerServer.Source4SPOGServer;
import InvokerServer.UserSpogServer;
import InvokerServer.GatewayServer.siteType;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import invoker.SiteTestHelper;
import io.restassured.response.Response;

public class CreateUsersSourcesColumnsTest  extends base.prepare.PrepareOrgInfo{

	@Parameters({ "pmfKey"})
	 public CreateUsersSourcesColumnsTest(String pmfKey) {
		super(pmfKey);
		// TODO Auto-generated constructor stub
	}
	private SPOGServer spogServer;
	 private Source4SPOGServer source4SpogServer;
	  private UserSpogServer userSpogServer;
	
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
		

	  //this is for update portal, each testng class is taken as BQ set
	/*  private SQLServerDb bqdb1;
	  public int Nooftest;
	  private long creationTime;
	  private String BQName=null;
	  private String runningMachine;
	  private testcasescount count1;
	  private String buildVersion;
	  private ExtentReports rep;*/
	  
	private Org4SPOGServer org4SpogServer;
	private String  org_model_prefix=this.getClass().getSimpleName();
	private String csr_read_only_id;
	private String csr_org_id;
	private String new_csr_read_only_mail;
	private String new_csr_read_only_admin_id;



		 
	  @BeforeClass
	  @Parameters({ "baseURI", "port", "logFolder", "csrAdminUserName", "csrAdminPassword", "buildVersion", "csrReadOnlyAdminUserName","csrReadOnlyAdminPassword"})
	  public void beforeClass(String baseURI, String port, String logFolder, String adminUserName, String adminPassword ,  String buildVersion, String csrROAdminUserName, String csrROPwd ) throws UnknownHostException {
			
		  	spogServer = new SPOGServer(baseURI, port);
		  	userSpogServer = new UserSpogServer(baseURI, port);
		  	source4SpogServer =new Source4SPOGServer(baseURI,port);
		  	org4SpogServer = new Org4SPOGServer(baseURI, port);
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
			direct_org_id = spogServer.CreateOrganizationWithCheck(prefix+direct_org_name + org_model_prefix, SpogConstants.DIRECT_ORG, null, null, null, null, test);
			final_direct_user_name_email = prefix + direct_user_name_email;
			
			test.log(LogStatus.INFO,"create a admin under direct org");
			direct_user_id = spogServer.createUserAndCheck(final_direct_user_name_email, common_password, prefix + direct_user_first_name, prefix + direct_user_last_name, SpogConstants.DIRECT_ADMIN, direct_org_id, test);
			spogServer.userLogin(final_direct_user_name_email, common_password);
		  	

			//************************create msp org,user,site*************************************
			spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
			prefix = RandomStringUtils.randomAlphanumeric(8);
			test.log(LogStatus.INFO,"create a msp org");
			msp_org_id = spogServer.CreateOrganizationWithCheck(prefix + msp_org_name+ org_model_prefix , SpogConstants.MSP_ORG, null, null, null, null, test);
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
			
			csr_org_id = spogServer.GetLoggedinUserOrganizationID();
			
		    test.log(LogStatus.INFO,"Creating csr readonly user ");
			new_csr_read_only_mail= prefix+ "_shuo_csr_read_only@arcserve.com";
			
			new_csr_read_only_admin_id = spogServer.createUserAndCheck(new_csr_read_only_mail, common_password, prefix + "_first_name_csr", prefix + "_last_name_csr", SpogConstants.CSR_READ_ONLY_ADMIN, csr_org_id, test);
			
		    
			
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
			prepare(baseURI, port, logFolder, this.csrAdminUserName,  this.csrAdminPassword, this.getClass().getSimpleName() );

	  }
	  
	  @DataProvider(name = "organizationAndUserInfo")
		public final Object[][] getOrganizationAndUserInfo() {
			return new Object[][] {
				 {this.new_csr_read_only_mail, common_password,"Name;true;1,Type;false;3", null, null, this.new_csr_read_only_admin_id,SpogConstants.SUCCESS_POST, null}, 
				 {final_direct_user_name_email, common_password,"Name;true;1,Type;false;3", SpogConstants.DIRECT_ADMIN, direct_org_id, null,SpogConstants.SUCCESS_POST, null}, 
				 {final_msp_user_name_email,  common_password,"Protection;true;3,Connection;true;2,Policy;false;3", SpogConstants.MSP_ADMIN, msp_org_id, null,SpogConstants.SUCCESS_POST, null},
				 {final_msp_user_name_email, common_password, "Last Backup Status;false;1", SpogConstants.DIRECT_ADMIN, msp_org_id, account_user_id,SpogConstants.SUCCESS_POST, null},
				 {account_user_email,  common_password, "Group;true;3,VM Name;true;1", SpogConstants.DIRECT_ADMIN, account_id, null,SpogConstants.SUCCESS_POST, null},
				 {csrAdminUserName, csrAdminPassword,"OS;true;1,Last Recovery Point;true;2", SpogConstants.DIRECT_ADMIN, direct_org_id, null,SpogConstants.SUCCESS_POST, null},
				 {csrAdminUserName, csrAdminPassword,"Agent;true;1", SpogConstants.MSP_ADMIN, msp_org_id, null,SpogConstants.SUCCESS_POST, null},
				 {csrAdminUserName, csrAdminPassword,"Policy;true;1", SpogConstants.DIRECT_ADMIN, account_id, null,SpogConstants.SUCCESS_POST, null},
				 {final_direct_user_name_email, common_password,"Name;true;1", null, null, msp_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 
				 {final_msp_user_name_email,  common_password,"Protection;true;1", null, null, direct_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				 {final_direct_user_name_email, common_password,"Name;true;1", null, null, account_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 
				 {account_user_email,  common_password, "Group;true;3,VM Name;true;1", null,null, msp_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				 {account_user_email,  common_password, "Group;true;3,VM Name;true;1", null, null, direct_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				 {account_user_email,  common_password, "Group;true;3,VM Name;true;1", null,null, another_account_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{final_direct_user_name_email, common_password,"Name;none;none", SpogConstants.DIRECT_ADMIN, direct_org_id, null,SpogConstants.SUCCESS_POST, null},
				{final_direct_user_name_email, common_password,"Name;false;none", SpogConstants.DIRECT_ADMIN, direct_org_id, null,SpogConstants.SUCCESS_POST, null},
				{final_direct_user_name_email, common_password,"Name;none;1", SpogConstants.DIRECT_ADMIN, direct_org_id, null,SpogConstants.SUCCESS_POST, null},
				
				 {final_msp_account_admin_email,  common_password, "Group;true;3,VM Name;true;1", null, null, account_user_id,SpogConstants.SUCCESS_POST, null},
				 {final_msp_user_name_email, common_password, "Last Backup Status;false;1", null, null, msp_account_admin_id,SpogConstants.SUCCESS_POST, null}, 
				 {final_msp_account_admin_email,  common_password, "Group;true;3,VM Name;true;1", null, null, another_account_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				 {final_msp_account_admin_email,  common_password, "Group;true;3,VM Name;true;1", null, null, msp_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				 {final_msp_account_admin_email,  common_password, "Group;true;3,VM Name;true;1", null, null, direct_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				 {final_direct_user_name_email,  common_password, "Group;true;3,VM Name;true;1", null, null, msp_account_admin_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				 {account_user_email,  common_password, "Group;true;3,VM Name;true;1", null, null, msp_account_admin_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				 {csrAdminUserName, csrAdminPassword, "Last Backup Status;false;1", null, null, msp_account_admin_id,SpogConstants.SUCCESS_POST, null},
				 {final_msp_account_admin_email,  common_password, "Group;true;3,VM Name;true;1", null, null, msp_account_admin_id,SpogConstants.SUCCESS_POST, null},
				 {this.csrReadOnlyAdminUserName,  this.csrReadOnlyAdminPassword, "Group;true;1,VM Name;true;2", null, null, direct_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				 {this.csrReadOnlyAdminUserName,  this.csrReadOnlyAdminPassword, "Group;true;1,VM Name;true;2", null, null, account_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				 {this.csrReadOnlyAdminUserName,  this.csrReadOnlyAdminPassword, "Group;true;1,VM Name;true;2", null, null, msp_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				 {this.csrReadOnlyAdminUserName,  this.csrReadOnlyAdminPassword, "Group;true;1,VM Name;true;2", null, null, msp_account_admin_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				
				{this.final_root_msp_user_name_email, common_password,"Name;true;1,Type;false;3", null, null, this.root_msp_user_id,SpogConstants.SUCCESS_POST, null},
				{this.final_root_msp_user_name_email, common_password,"Name;true;1,Type;false;3", null, null, this.root_msp_account_admin_user_id,SpogConstants.SUCCESS_POST, null},
				{this.final_root_msp_user_name_email, common_password,"Name;true;1,Type;false;3", null, null, this.root_msp_direct_org_user_id,SpogConstants.SUCCESS_POST, null},
				{this.final_root_msp_user_name_email, common_password,"Name;true;1,Type;false;3", null, null, this.sub_msp1_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{this.final_root_msp_user_name_email, common_password,"Name;true;1,Type;false;3", null, null, this.sub_msp1_msp_account_admin_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{this.final_root_msp_user_name_email, common_password,"Name;true;1,Type;false;3", null, null, this.sub_msp1_account1_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				
				{this.final_root_msp_account_admin_user_name_email, common_password,"Name;true;1,Type;false;3", null, null, this.root_msp_account_admin_user_id,SpogConstants.SUCCESS_POST, null},
				{this.final_root_msp_account_admin_user_name_email, common_password,"Name;true;1,Type;false;3", null, null, this.root_msp_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{this.final_root_msp_account_admin_user_name_email, common_password,"Name;true;1,Type;false;3", null, null, this.root_msp_direct_org_user_id,SpogConstants.SUCCESS_POST, null},
				{this.final_root_msp_account_admin_user_name_email, common_password,"Name;true;1,Type;false;3", null, null, this.sub_msp1_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{this.final_root_msp_account_admin_user_name_email, common_password,"Name;true;1,Type;false;3", null, null, this.sub_msp1_msp_account_admin_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{this.final_root_msp_account_admin_user_name_email, common_password,"Name;true;1,Type;false;3", null, null, this.sub_msp1_account1_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				
				{this.final_root_msp_direct_org_user_email, common_password,"Name;true;1,Type;false;3", null, null, this.root_msp_direct_org_user_id,SpogConstants.SUCCESS_POST, null},
				{this.final_root_msp_direct_org_user_email, common_password,"Name;true;1,Type;false;3", null, null, this.root_msp_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{this.final_root_msp_direct_org_user_email, common_password,"Name;true;1,Type;false;3", null, null, this.root_msp_account_admin_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{this.final_root_msp_direct_org_user_email, common_password,"Name;true;1,Type;false;3", null, null, this.sub_msp1_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{this.final_root_msp_direct_org_user_email, common_password,"Name;true;1,Type;false;3", null, null, this.sub_msp1_msp_account_admin_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{this.final_root_msp_direct_org_user_email, common_password,"Name;true;1,Type;false;3", null, null, this.sub_msp1_account1_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				
				{this.final_sub_msp1_user_name_email, common_password,"Name;true;1,Type;false;3", null, null, this.sub_msp1_user_id,SpogConstants.SUCCESS_POST, null},
				{this.final_sub_msp1_user_name_email, common_password,"Name;true;1,Type;false;3", null, null, this.sub_msp1_msp_account_admin_id,SpogConstants.SUCCESS_POST, null},
				{this.final_sub_msp1_user_name_email, common_password,"Name;true;1,Type;false;3", null, null, this.sub_msp1_account1_user_id,SpogConstants.SUCCESS_POST, null},
				{this.final_sub_msp1_user_name_email, common_password,"Name;true;1,Type;false;3", null, null, this.root_msp_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{this.final_sub_msp1_user_name_email, common_password,"Name;true;1,Type;false;3", null, null, this.root_msp_account_admin_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{this.final_sub_msp1_user_name_email, common_password,"Name;true;1,Type;false;3", null, null, this.root_msp_direct_org_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{this.final_sub_msp1_user_name_email, common_password,"Name;true;1,Type;false;3", null, null, this.sub_msp2_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{this.final_sub_msp1_user_name_email, common_password,"Name;true;1,Type;false;3", null, null, this.sub_msp2_account1_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				

				{this.final_sub_msp1_msp_account_user_name_email, common_password,"Name;true;1,Type;false;3", null, null, this.sub_msp1_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS,  ErrorCode.RESOURCE_PERMISSION},
				{this.final_sub_msp1_msp_account_user_name_email, common_password,"Name;true;1,Type;false;3", null, null, this.sub_msp1_msp_account_admin_id,SpogConstants.SUCCESS_POST, null},
				{this.final_sub_msp1_msp_account_user_name_email, common_password,"Name;true;1,Type;false;3", null, null, this.sub_msp1_account1_user_id,SpogConstants.SUCCESS_POST, null},
				{this.final_sub_msp1_msp_account_user_name_email, common_password,"Name;true;1,Type;false;3", null, null, this.sub_msp1_account2_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS,  ErrorCode.RESOURCE_PERMISSION},
				{this.final_sub_msp1_msp_account_user_name_email, common_password,"Name;true;1,Type;false;3", null, null, this.root_msp_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{this.final_sub_msp1_msp_account_user_name_email, common_password,"Name;true;1,Type;false;3", null, null, this.root_msp_account_admin_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{this.final_sub_msp1_msp_account_user_name_email, common_password,"Name;true;1,Type;false;3", null, null, this.root_msp_direct_org_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{this.final_sub_msp1_msp_account_user_name_email, common_password,"Name;true;1,Type;false;3", null, null, this.sub_msp2_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{this.final_sub_msp1_msp_account_user_name_email, common_password,"Name;true;1,Type;false;3", null, null, this.sub_msp2_account1_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},

				{this.final_sub_msp1_account1_user_email, common_password,"Name;true;1,Type;false;3", null, null, this.sub_msp1_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS,  ErrorCode.RESOURCE_PERMISSION},
				{this.final_sub_msp1_account1_user_email, common_password,"Name;true;1,Type;false;3", null, null, this.sub_msp1_msp_account_admin_id,SpogConstants.INSUFFICIENT_PERMISSIONS,  ErrorCode.RESOURCE_PERMISSION},
				{this.final_sub_msp1_account1_user_email, common_password,"Name;true;1,Type;false;3", null, null, this.sub_msp1_account1_user_id,SpogConstants.SUCCESS_POST, null},
				{this.final_sub_msp1_account1_user_email, common_password,"Name;true;1,Type;false;3", null, null, this.sub_msp1_account2_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS,  ErrorCode.RESOURCE_PERMISSION},
				{this.final_sub_msp1_account1_user_email, common_password,"Name;true;1,Type;false;3", null, null, this.root_msp_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{this.final_sub_msp1_account1_user_email, common_password,"Name;true;1,Type;false;3", null, null, this.root_msp_account_admin_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{this.final_sub_msp1_account1_user_email, common_password,"Name;true;1,Type;false;3", null, null, this.root_msp_direct_org_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{this.final_sub_msp1_account1_user_email, common_password,"Name;true;1,Type;false;3", null, null, this.sub_msp2_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{this.final_sub_msp1_account1_user_email, common_password,"Name;true;1,Type;false;3", null, null, this.sub_msp2_account1_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},

			};
			
			
		}
	  
	  /**
	   * 1. direct admin could create in the same organization's specific user's source columns
	   * 2. msp admin could create  in the same organization's specific user's source columns
	   * 3. account admin could create  in the same organization's specific user's source columns
	   * 4. msp admin could create in the account's specific user's source columns
	   * 5. csr admin could create direct user's source columns
	   * 34. csr admin could create msp user's source columns
	   * 36. csr admin could create account user's source columns
	   * 13. set visible as false
	   * 14. set visible as true
	   * 6. direct admin could not create msp organization's user's source columns, report 403
	   * 7. msp admin could not create direct organization's user's source columns, report 403
	   * 37. direct admin could not create  account admin's source columns, report 403
	   * 8. account admin could not create msp organization's user's source columns, report 403
	   * 38. account admin could not create direct organization's user's source columns, report 403
	   * 40. account admin could not create other account's user's source columns, report 403
	   * 19. create source column only with column_id
	   * 20. create source column with column_id and visible
	   * 21. create source column with column_id and order_id
	   * 
	   * 27. msp account admin could create mastered account's user's source columns
	   * 31. msp  admin could create msp account admin's source columns
	   * 28. msp account admin could not create not mastered account's user's source columns
	   * 29. msp account admin could not create msp admin's source columns
	   * 30. msp account admin could not create direct admin's source columns in direct org
	   * 32. direct admin could not delete msp account admin's source columns
	   * 33. account admin could not delete msp account admin's source columns
	   * 35. csr admin could create msp account admin user's source columns
	   * 39. MSP account admin could create its own source columns
	   * root msp admin could create its own source columns
	   * root msp admin could create in the same organization's specific user's source columns
	   * root msp admin could create its direct org specific user's source columns
	   *  root msp admin could not create its sub msp1 org specific user's source columns
	   *  root msp admin could not create its sub msp1 account1 specific user's source columns
	   *  
	   *   root msp account admin could create its own source columns
	   * root msp account admin could not create in the same organization's specific user's source columns
	   * root msp account admin could create its direct org specific user's source columns
	   *  root msp account admin could not create its sub msp1 org specific user's source columns
	   *  root msp account admin could not create its sub msp1 account1 specific user's source columns
	   * 
	   * root msp direct org admin could create its own source columns
	   * root msp direct org admin could not create in root msp admin source column
	   *  root msp direct org admin could not create its sub msp1 org specific user's source columns
	   *  root msp direct org admin could not create its sub msp1 account1 specific user's source columns
	   *  
   *   root msp sub msp1 admin could create its own source columns
	   * root msp sub msp1 admin could  create in the same organization's specific user's source columns
	   * root msp sub msp1 admin could  create its sub msp1 account1 specific user's source columns 
	   *  root msp sub msp1 admin could not create root msp admin 's source columns
	   *  root msp sub msp1 admin could not create root msp account admin 's source columns
	   *  root msp sub msp1 admin could not create root msp's direct org admin's source columns
	   *  root msp sub msp1 admin could not create its sub msp2 admin's source columns
	   * root msp sub msp1 admin could not create its sub msp2 account's admin's source columns
	   * @param userName
	   * @param password
	   * @param specifiedSourceColumns
	   * @param role_id
	   * @param organization_id
	   * @param created_user_id
	   */
	  @Test(dataProvider ="organizationAndUserInfo", enabled=true)

	  public void adminCanCreateUsersSourcesColumns(String userName, String password, String specifiedSourceColumns, String role_id, String organization_id, String created_user_id,
			  int statusCode, String errorCode){
		  
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("shuo.zhang");
		  spogServer.errorHandle.printInfoMessageInDebugFile("/****************adminCanCreateUsersSourcesColumns**************/");
		  spogServer.userLogin(userName, password, test);
		  
		  if(created_user_id==null){
			  test.log(LogStatus.INFO,"create user");
			  String finalUserEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + userName;
			  created_user_id = spogServer.createUserAndCheck(finalUserEmail, common_password, RandomStringUtils.randomAlphanumeric(8) , RandomStringUtils.randomAlphanumeric(8) , role_id, organization_id, test);
			  
		  }
		
		  String token = spogServer.getJWTToken();
		  test.log(LogStatus.INFO,"get source column array list");
		  ArrayList<HashMap<String, Object>> columnsList = source4SpogServer.getSourceColumnArrayList(specifiedSourceColumns, token);
		  test.log(LogStatus.INFO,"create user source columns");
		  source4SpogServer.createUsersSourcesColumnsWithCheck(created_user_id, columnsList, token, statusCode, errorCode);
		  
		  if(statusCode== SpogConstants.SUCCESS_POST){
			  test.log(LogStatus.INFO,"delete user source columns");
			  source4SpogServer.deleteUsersSourcesColumnsWithCheck(created_user_id, token, SpogConstants.SUCCESS_GET_PUT_DELETE, null);
		  }

		
	 }
	  
	  @Test(enabled=true)
	  /**
	   * 9. fail to create user's source column without login, report 401
	   * 10. fail to create user's source column with invalid token, report 401

	   */
	  public void createUsersSourcesColumnsWithoutLogin(){
		  
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("shuo.zhang");
		  spogServer.errorHandle.printInfoMessageInDebugFile("/****************createUsersSourcesColumnsWithoutLogin**************/");
		  spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword, test);
		  		
		  String token = spogServer.getJWTToken();
		  test.log(LogStatus.INFO,"createUsersSourcesColumnsWithoutLoginAndCheck");
		  source4SpogServer.createUsersSourcesColumnsWithoutLoginAndCheck(direct_user_id);
		  test.log(LogStatus.INFO,"createUsersSourcesColumns With invalid token");
		  ArrayList<HashMap<String, Object>> columnsList = new ArrayList<HashMap<String, Object>>();
		  source4SpogServer.createUsersSourcesColumnsWithCheck(direct_user_id, columnsList, UUID.randomUUID().toString(), SpogConstants.NOT_LOGGED_IN,  ErrorCode.AUTHORIZATION_HEADER_BLANK);
	 }
	  
	  @DataProvider(name = "organizationAndUserInfo1")
			public final Object[][] getOrganizationAndUserInfo1() {
				return new Object[][] {
					{UUID.randomUUID().toString(),"true", "3",direct_user_id, SpogConstants.RESOURCE_NOT_EXIST, ErrorCode.COLUMN_NOT_EXIST}, 
					{null,"true","3", direct_user_id, SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.ELEMENT_BLANK},
					{"","true", "3", direct_user_id, SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.ELEMENT_BLANK}, 
					{"7aea4c0c-54a1-4dfd-8d52-3b69197acd8f","true", "0", direct_user_id, SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.INVALID_ORDER_ID}, 
					{"7aea4c0c-54a1-4dfd-8d52-3b69197acd8f","true", "-5", direct_user_id, SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.INVALID_ORDER_ID},
					{"7aea4c0c-54a1-4dfd-8d52-3b69197acd8f","true", "1", UUID.randomUUID().toString(), SpogConstants.RESOURCE_NOT_EXIST, ErrorCode.USER_NOT_EXIST_OR_INACTIVE}, 
					{"7aea4c0c-54a1-4dfd-8d52-3b69197acd8f","true", "1",null, SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.ELEMENT_NOT_UUID}, 
					{"7aea4c0c-54a1-4dfd-8d52-3b69197acd8f","true", "1","", SpogConstants.RESOURCE_NOT_EXIST, ErrorCode.RESOURCE_NOT_FOUND},
					{"7aea4c0c-54a1-4dfd-8d52-3b69197acd8f","true", "15",msp_user_id, SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.ORDER_ID_INVALID},
				};
			}
	  
	  
	  /**
	   * 11. set column_id as non-exsiting id, report 404
	   * 12. set column id as null , report 400
	   * 15. set order_id as 0, it should report error 400
	   * 16. set user_id as non-existing id, create source column, report 404
	   * 17. set user_id as null, create source column, report 400
	   * 22. set order_id more than 14, report error 400 
	   * 24. set column id as  "", report 400
	   * 25. set order_id as negative number, it should report error 400
	   * 26. set user_id as  "", create source column, report 404
	   * 
	   * 
	   */
	  @Test(dataProvider ="organizationAndUserInfo1", enabled=true)

	  public void errorHandling( String column_id, String visible, String order_id, String user_id, int statusCode, String errorCode){
		  
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("shuo.zhang");
		  spogServer.errorHandle.printInfoMessageInDebugFile("/****************errorHandling**************/");
		  spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		  
				
		  String token = spogServer.getJWTToken();
		  test.log(LogStatus.INFO,"get source column info");
		  ArrayList<HashMap<String, Object>> columnsList = new ArrayList<HashMap<String, Object>> ();
		  HashMap<String, Object> columnInfo = source4SpogServer.getSourceColumnInfo(column_id, visible, order_id);
		  columnsList.add(columnInfo);
		  test.log(LogStatus.INFO,"createUsersSourcesColumnsWithCheck");
		  source4SpogServer.createUsersSourcesColumnsWithCheck(user_id, columnsList, token, statusCode, errorCode);
		  
	 }
	  
	  @Test( enabled=true)
	  /**
	   * 18. set two columns with same column_id, it should use the first one
	   * 23. the second call for the create user source columns should report 400
	   */
	  public void createColumnsWithSameColumnID( ){
		  
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("shuo.zhang");
		  spogServer.errorHandle.printInfoMessageInDebugFile("/****************errorHandling**************/");
		  spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		  
				
		  String token = spogServer.getJWTToken();
		  test.log(LogStatus.INFO,"get source column info");
		  ArrayList<HashMap<String, Object>> columnsList = source4SpogServer.getSourceColumnArrayList("Name;true;1,Name;false;1", token);
		  test.log(LogStatus.INFO,"createUsersSourcesColumnsWithCheck");
		  source4SpogServer.createUsersSourcesColumnsWithCheck(direct_user_id, columnsList, token, SpogConstants.SUCCESS_POST, null);
		  source4SpogServer.createUsersSourcesColumnsWithCheck(direct_user_id, columnsList, token, SpogConstants.REQUIRED_INFO_NOT_EXIST,  ErrorCode.COLUMN_EXIST);
		  source4SpogServer.deleteUsersSourcesColumnsWithCheck(direct_user_id, token, SpogConstants.SUCCESS_GET_PUT_DELETE, null);
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
		 @AfterClass
		  public void subUpdatebd() {
			  
			  spogServer.DeleteUserById(this.new_csr_read_only_admin_id);
			  this.updatebd();
			 
		  }
	
	  
}

