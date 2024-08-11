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
import InvokerServer.Org4SPOGServer;
import InvokerServer.SPOGServer;
import InvokerServer.Source4SPOGServer;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;

public class UpdateUsersSourcesColumnsTest  extends base.prepare.PrepareOrgInfo{
	
	 @Parameters({ "pmfKey"})
	 public UpdateUsersSourcesColumnsTest(String pmfKey) {
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
	 /* private SQLServerDb bqdb1;
	  public int Nooftest;
	  private long creationTime;
	  private String BQName=null;
	  private String runningMachine;
	  private testcasescount count1;
	  private String buildVersion;
	  private ExtentReports rep;
	  
	  */
	private Org4SPOGServer org4SpogServer;
	private String  org_model_prefix=this.getClass().getSimpleName();
	private String csr_org_id;
	private String new_csr_read_only_mail;
	private String new_csr_read_only_admin_id;
	private String csr_user_id;
		 
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
				this.csrReadOnlyAdminUserName = csrROAdminUserName;
				  this.csrReadOnlyAdminPassword = csrROPwd;
				  
		    test = rep.startTest("beforeClass");
			spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
			//*******************create direct org,user,site**********************/
			String prefix = RandomStringUtils.randomAlphanumeric(8);
			test.log(LogStatus.INFO,"create a direct org");
			direct_org_id = spogServer.CreateOrganizationWithCheck(prefix+direct_org_name +org_model_prefix, SpogConstants.DIRECT_ORG, null, null, null, null, test);
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
		
			test.log(LogStatus.INFO,"Creating a account user For account org");
			account_user_email = prefix + msp_user_name_email;
			account_user_id = spogServer.createUserAndCheck(account_user_email, common_password, prefix + msp_user_first_name, prefix + msp_user_last_name, SpogConstants.DIRECT_ADMIN, account_id, test);
			
			
			spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword);
			test.log(LogStatus.INFO,"assign account to msp account admin");
			String[] mspAccountAdmins = new String []{msp_account_admin_id};
			userSpogServer.assignMspAccountAdmins(msp_org_id, account_id, mspAccountAdmins , spogServer.getJWTToken()); 
		  	

			//create account, account user 
			test.log(LogStatus.INFO,"Creating another account For msp org");
			prefix = RandomStringUtils.randomAlphanumeric(8);
			another_account_id = spogServer.createAccountWithCheck(msp_org_id, "sub_" + prefix + msp_org_name, "", test);
		  	
			test.log(LogStatus.INFO,"Creating a account admin For another account org");
			another_account_user_email = prefix + msp_user_name_email;
			another_account_user_id = spogServer.createUserAndCheck(another_account_user_email, common_password, prefix + msp_user_first_name, prefix + msp_user_last_name, SpogConstants.DIRECT_ADMIN, another_account_id, test);
			
			csr_org_id = spogServer.GetLoggedinUserOrganizationID();			
			csr_user_id = spogServer.GetLoggedinUser_UserID();
			
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
					{final_direct_user_name_email, common_password,"Name;true;1,Type;false;3", final_direct_user_name_email,"Name;false;1", SpogConstants.DIRECT_ADMIN, direct_org_id, null,SpogConstants.SUCCESS_GET_PUT_DELETE, null}, 
					{final_msp_user_name_email,  common_password,"Connection;true;2,Policy;true;1", final_msp_user_name_email, "Agent;true;4,Connection;true;3,Policy;true;2,Protection;false;1", SpogConstants.MSP_ADMIN, msp_org_id, null,SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				 {final_msp_user_name_email, common_password, "Last Backup Status;false;1", final_msp_user_name_email,"Last Backup Status;true;1", SpogConstants.DIRECT_ADMIN, msp_org_id, account_user_id,SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				 {account_user_email,  common_password, "Name;true;3,Protection;true;1", account_user_email, "Group;true;3,VM Name;true;1",  SpogConstants.DIRECT_ADMIN, account_id, null,SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				 {csrAdminUserName, csrAdminPassword,"OS;true;1,",csrAdminUserName, "OS;true;1,Policy;true;2", SpogConstants.DIRECT_ADMIN, direct_org_id, null,SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				 {csrAdminUserName, csrAdminPassword,"Name;true;1,Organization;true;2,Type;false;3", csrAdminUserName, "Agent;true;1", SpogConstants.MSP_ADMIN, msp_org_id, null,SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				 {csrAdminUserName, csrAdminPassword,"Name;false;1",csrAdminUserName, "Name;true;1", SpogConstants.DIRECT_ADMIN, account_id, null,SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				 {csrAdminUserName, csrAdminPassword,"Last Recovery Point;false;1",csrAdminUserName, "Organization;true;1", null, null, msp_account_admin_id,SpogConstants.SUCCESS_GET_PUT_DELETE, null},
			
			
				 {final_msp_user_name_email, common_password,"Name;true;1", final_direct_user_name_email, "Name;false;1", SpogConstants.MSP_ADMIN, msp_org_id, null, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION}, 
				 {account_user_email, common_password,"Name;true;1", final_direct_user_name_email, "Name;false;1", SpogConstants.DIRECT_ADMIN, account_id, null, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				
				 {final_direct_user_name_email, common_password,"Name;true;1", final_msp_user_name_email, "Name;false;1", SpogConstants.DIRECT_ADMIN, direct_org_id, null, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				
				 {final_direct_user_name_email, common_password,"Name;true;1", account_user_email, "Name;false;1", SpogConstants.DIRECT_ADMIN, direct_org_id, null, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				 {final_msp_user_name_email, common_password,"Name;true;1", account_user_email, "Name;false;1", SpogConstants.MSP_ADMIN, msp_org_id, null, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				 {another_account_user_email, common_password,"Name;true;1", account_user_email, "Name;false;1", null, null, another_account_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				
				 {final_direct_user_name_email, common_password,"Name;true;1,Type;false;3", final_direct_user_name_email,"Name;none;none,Type;none;none", SpogConstants.DIRECT_ADMIN, direct_org_id, null,SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				{final_direct_user_name_email, common_password,"Name;true;1,Agent;false;2,Type;false;3", final_direct_user_name_email,"Name;false;none,Agent;true;none", SpogConstants.DIRECT_ADMIN, direct_org_id, null,SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				{final_direct_user_name_email, common_password,"Name;true;1,Agent;false;2,Type;false;3", final_direct_user_name_email,"Name;none;2,Agent;none;1", SpogConstants.DIRECT_ADMIN, direct_org_id, null,SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				
				 {account_user_email,  common_password, "Name;true;3,Agent;true;2,Protection;true;1", final_msp_account_admin_email, "Group;true;3,VM Name;true;1",  null, null, account_user_id,SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				 {another_account_user_email,  common_password, "Name;true;3,Agent;true;2,Protection;true;1", final_msp_account_admin_email, "Group;true;3,VM Name;true;1",  null, null, another_account_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				 {final_msp_user_name_email,  common_password, "Name;true;3,Organization;true;2,Protection;true;1", final_msp_account_admin_email, "Group;true;3,VM Name;true;1",  null, null, msp_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				 {final_direct_user_name_email,  common_password, "Name;true;3,Agent;true;2,Protection;true;1", final_msp_account_admin_email, "Group;true;3,VM Name;true;1",  null, null, direct_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				 
				 
				 
			//not decide the behavior	
			  {csrAdminUserName,  csrAdminPassword, "Name;true;3,Organization;true;2,Protection;true;1", final_msp_user_name_email, "Group;true;3,VM Name;true;1",  null, null, msp_account_admin_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				
				  {csrAdminUserName,  csrAdminPassword, "Name;true;3,Organization;true;2,Protection;true;1", final_direct_user_name_email, "Group;true;3,VM Name;true;1",  null, null, msp_account_admin_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				   {csrAdminUserName,  csrAdminPassword, "Name;true;3,Organization;true;2,Protection;true;1", account_user_email, "Group;true;3,VM Name;true;1",  null, null, msp_account_admin_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				   {csrAdminUserName,  csrAdminPassword,  "Name;true;3,Organization;true;2,Protection;true;1", final_msp_account_admin_email, "Group;true;3,VM Name;true;1",  null, null, msp_account_admin_id, SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				
			
					
				{final_direct_user_name_email, common_password,"Name;true;1", this.csrReadOnlyAdminUserName, "Name;false;1", null, null,direct_user_id , SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{final_msp_user_name_email, common_password,"Name;true;1", this.csrReadOnlyAdminUserName, "Name;false;1", null, null,msp_user_id , SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{account_user_email, common_password,"Name;true;1", this.csrReadOnlyAdminUserName, "Name;false;1", null, null,account_user_id , SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{final_msp_account_admin_email, common_password,"Name;true;1", this.csrReadOnlyAdminUserName, "Name;false;1", null, null,msp_account_admin_id , SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
					
				
				
				{this.final_root_msp_user_name_email, common_password,"Name;true;1,Type;false;3", final_root_msp_user_name_email, "Name;false;1",null, null, this.root_msp_user_id,SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				{this.final_root_msp_account_admin_user_name_email, common_password,"Name;true;1,Type;false;3", final_root_msp_user_name_email, "Name;false;1",null, null, this.root_msp_account_admin_user_id,SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				{this.final_root_msp_user_name_email, common_password,"Name;true;1,Type;false;3", final_root_msp_user_name_email, "Name;false;1",null, null,this.root_msp_direct_org_user_id,SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				{this.final_sub_msp1_user_name_email, common_password,"Name;true;1,Type;false;3", final_root_msp_user_name_email, "Name;false;1",null, null, this.sub_msp1_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{this.final_sub_msp1_msp_account_user_name_email, common_password,"Name;true;1,Type;false;3", final_root_msp_user_name_email, "Name;false;1",null, null, this.sub_msp1_msp_account_admin_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{this.final_sub_msp1_account1_user_email, common_password,"Name;true;1,Type;false;3", final_root_msp_user_name_email, "Name;false;1",null, null,this.sub_msp1_account1_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				
				{this.final_root_msp_account_admin_user_name_email, common_password,"Name;true;1,Type;false;3", final_root_msp_account_admin_user_name_email,"Name;false;1", null, null, this.root_msp_account_admin_user_id,SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				{this.final_root_msp_user_name_email, common_password,"Name;true;1,Type;false;3",  final_root_msp_account_admin_user_name_email,"Name;false;1",null, null, this.root_msp_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{this.final_root_msp_direct_org_user_email, common_password,"Name;true;1,Type;false;3", final_root_msp_account_admin_user_name_email,"Name;false;1",null, null, this.root_msp_direct_org_user_id,SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				{this.final_sub_msp1_user_name_email, common_password,"Name;true;1,Type;false;3",  final_root_msp_account_admin_user_name_email,"Name;false;1",null, null, this.sub_msp1_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{this.final_sub_msp1_msp_account_user_name_email, common_password,"Name;true;1,Type;false;3",  final_root_msp_account_admin_user_name_email,"Name;false;1",null, null, this.sub_msp1_msp_account_admin_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{this.final_sub_msp1_account1_user_email, common_password,"Name;true;1,Type;false;3",  final_root_msp_account_admin_user_name_email,"Name;false;1",null, null, this.sub_msp1_account1_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				
				{this.final_root_msp_direct_org_user_email, common_password,"Name;true;1,Type;false;3",final_root_msp_direct_org_user_email, "Name;false;1",null, null, this.root_msp_direct_org_user_id,SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				{this.final_root_msp_user_name_email, common_password,"Name;true;1,Type;false;3", final_root_msp_direct_org_user_email, "Name;false;1",null, null, this.root_msp_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{this.final_root_msp_account_admin_user_name_email, common_password,"Name;true;1,Type;false;3", final_root_msp_direct_org_user_email, "Name;false;1",null, null, this.root_msp_account_admin_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{this.final_sub_msp1_user_name_email, common_password,"Name;true;1,Type;false;3",final_root_msp_direct_org_user_email, "Name;false;1", null, null, this.sub_msp1_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{this.final_sub_msp1_msp_account_user_name_email, common_password,"Name;true;1,Type;false;3", final_root_msp_direct_org_user_email, "Name;false;1",null, null, this.sub_msp1_msp_account_admin_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{this.final_sub_msp1_account1_user_email, common_password,"Name;true;1,Type;false;3",final_root_msp_direct_org_user_email, "Name;false;1", null, null, this.sub_msp1_account1_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				
				{this.final_sub_msp1_user_name_email, common_password,"Name;true;1,Type;false;3", final_sub_msp1_user_name_email, "Name;false;1",null, null, this.sub_msp1_user_id,SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				{this.final_sub_msp1_msp_account_user_name_email, common_password,"Name;true;1,Type;false;3", final_sub_msp1_user_name_email, "Name;false;1", null, null, this.sub_msp1_msp_account_admin_id,SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				{this.final_sub_msp1_account1_user_email, common_password,"Name;true;1,Type;false;3", final_sub_msp1_user_name_email, "Name;false;1", null, null, this.sub_msp1_account1_user_id,SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				{this.final_root_msp_user_name_email, common_password,"Name;true;1,Type;false;3", final_sub_msp1_user_name_email, "Name;false;1", null, null, this.root_msp_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{this.final_root_msp_account_admin_user_name_email, common_password,"Name;true;1,Type;false;3", final_sub_msp1_user_name_email, "Name;false;1", null, null, this.root_msp_account_admin_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{this.final_root_msp_direct_org_user_email, common_password,"Name;true;1,Type;false;3", final_sub_msp1_user_name_email, "Name;false;1", null, null, this.root_msp_direct_org_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{this.final_sub_msp2_user_name_email, common_password,"Name;true;1,Type;false;3", final_sub_msp1_user_name_email, "Name;false;1", null, null, this.sub_msp2_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{this.final_sub_msp2_account1_user_email, common_password,"Name;true;1,Type;false;3", final_sub_msp1_user_name_email, "Name;false;1", null, null, this.sub_msp2_account1_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				

				{this.final_sub_msp1_user_name_email, common_password,"Name;true;1,Type;false;3",final_sub_msp1_msp_account_user_name_email,  "Name;false;1", null, null, this.sub_msp1_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS,  ErrorCode.RESOURCE_PERMISSION},
				{this.final_sub_msp1_msp_account_user_name_email, common_password,"Name;true;1,Type;false;3",final_sub_msp1_msp_account_user_name_email,  "Name;false;1", null, null, this.sub_msp1_msp_account_admin_id,SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				{this.final_sub_msp1_account1_user_email, common_password,"Name;true;1,Type;false;3",final_sub_msp1_msp_account_user_name_email,  "Name;false;1", null, null, this.sub_msp1_account1_user_id,SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				{this.final_sub_msp1_account2_user_email, common_password,"Name;true;1,Type;false;3",final_sub_msp1_msp_account_user_name_email,  "Name;false;1", null, null, this.sub_msp1_account2_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS,  ErrorCode.RESOURCE_PERMISSION},
				{this.final_root_msp_user_name_email, common_password,"Name;true;1,Type;false;3",final_sub_msp1_msp_account_user_name_email,  "Name;false;1", null, null, this.root_msp_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{this.final_root_msp_account_admin_user_name_email, common_password,"Name;true;1,Type;false;3",final_sub_msp1_msp_account_user_name_email,  "Name;false;1", null, null, this.root_msp_account_admin_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{this.final_root_msp_direct_org_user_email, common_password,"Name;true;1,Type;false;3",final_sub_msp1_msp_account_user_name_email,  "Name;false;1", null, null, this.root_msp_direct_org_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{this.final_sub_msp2_user_name_email, common_password,"Name;true;1,Type;false;3",final_sub_msp1_msp_account_user_name_email,  "Name;false;1", null, null, this.sub_msp2_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{this.final_sub_msp2_account1_user_email, common_password,"Name;true;1,Type;false;3",final_sub_msp1_msp_account_user_name_email,  "Name;false;1", null, null, this.sub_msp2_account1_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},

				{this.final_sub_msp1_user_name_email, common_password,"Name;true;1,Type;false;3", final_sub_msp1_account1_user_email, "Name;false;1", null, null, this.sub_msp1_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS,  ErrorCode.RESOURCE_PERMISSION},
				{this.final_sub_msp1_msp_account_user_name_email, common_password,"Name;true;1,Type;false;3", final_sub_msp1_account1_user_email, "Name;false;1", null, null, this.sub_msp1_msp_account_admin_id,SpogConstants.INSUFFICIENT_PERMISSIONS,  ErrorCode.RESOURCE_PERMISSION},
				{this.final_sub_msp1_account1_user_email, common_password,"Name;true;1,Type;false;3", final_sub_msp1_account1_user_email, "Name;false;1", null, null, this.sub_msp1_account1_user_id,SpogConstants.SUCCESS_GET_PUT_DELETE, null},
				{this.final_sub_msp1_account2_user_email, common_password,"Name;true;1,Type;false;3",final_sub_msp1_account1_user_email, "Name;false;1",  null, null, this.sub_msp1_account2_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS,  ErrorCode.RESOURCE_PERMISSION},
				{this.final_root_msp_user_name_email, common_password,"Name;true;1,Type;false;3",final_sub_msp1_account1_user_email, "Name;false;1",  null, null, this.root_msp_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{this.final_root_msp_account_admin_user_name_email, common_password,"Name;true;1,Type;false;3",final_sub_msp1_account1_user_email, "Name;false;1",  null, null, this.root_msp_account_admin_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{this.final_root_msp_direct_org_user_email, common_password,"Name;true;1,Type;false;3", final_sub_msp1_account1_user_email, "Name;false;1", null, null, this.root_msp_direct_org_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{this.final_sub_msp2_user_name_email, common_password,"Name;true;1,Type;false;3", final_sub_msp1_account1_user_email, "Name;false;1", null, null, this.sub_msp2_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},
				{this.final_sub_msp2_account1_user_email, common_password,"Name;true;1,Type;false;3", final_sub_msp1_account1_user_email, "Name;false;1", null, null, this.sub_msp2_account1_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION},

			};
		}
	  
	  @Test(dataProvider ="organizationAndUserInfo", enabled=true)
	  /**
	   * 1. direct admin could update in the same organization's specific user's source columns
	   * 2. msp admin could update  in the same organization's specific user's source columns
	   * 4. msp admin could update in the account's specific user's source columns
	   * 3. account admin could update  in the same organization's specific user's source columns
	   * 5. csr admin could update direct admin's source columns
	   * 32. csr admin could update msp admin's source columns
	   * 33. csr admin could update account admin's source columns
	   * 34. csr admin could update msp account admin's source columns
	   * 
	   * 6. direct admin could not update msp organization's user's source columns, report 403
	   * 35. direct admin could not update account organization's user's source columns, report 403
	   * 
	   * 7. msp admin could not update direct organization's user's source columns, report 403
	   * 
	   * 8. account admin could not update direct organization's user's source columns, report 403
	   * 36. account admin could not update msp organization's user's source columns, report 403
	   * 37. account admin could not update other account's user's source columns, report 403
	   * 
	   * 13. set visible as false
	   * 14. set visible as true
	   * 19. update source column only with column_id
	   * 20. update source column with column_id and visible
	   * 21. update source column with column_id and order_id
	   * 
	   * 24. msp account admin could update mastered account's user's source columns
	   * 25. msp account admin could not update no mastered account's user's source columns
	   * 26. msp account admin could not update msp admin's user's source columns
	   * 27. msp account admin could not update direct org' user's source columns
	   *
	   * 
	   * not decide the behavior
	   * 28. msp admin could update msp account admin's source columns
	   * 29. msp account admin could  update its own source columns
	   * 30. direct admin cannot update msp account admin's source columns
	   * 31. account admin can not update msp account admin's source column
	   *
	   * 
	   * @param userName
	   * @param password
	   * @param createdSourceColumns
	   * @param updatedSourceColumns
	   * @param role_id
	   * @param organization_id
	   * @param created_user_id
	   * @param statusCode
	   * @param errorCode
	   */
	  public void adminCanUpdateUsersSourcesColumns(String createUserSourceColumnAdmin, String password, String createdSourceColumns, String updateUserSourceColumnAdmin,  String updatedSourceColumns,
			  String role_id, String organization_id, String created_user_id, int statusCode, String errorCode){
		  
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("shuo.zhang");
		  spogServer.errorHandle.printInfoMessageInDebugFile("/****************adminCanUpdateUsersSourcesColumns**************/");
		  spogServer.userLogin(createUserSourceColumnAdmin, password, test);
		  
		  if(created_user_id==null){
			  test.log(LogStatus.INFO,"create user");
			  String finalUserEmail = RandomStringUtils.randomAlphanumeric(8) + "_" + createUserSourceColumnAdmin;
			  created_user_id = spogServer.createUserAndCheck(finalUserEmail, common_password, RandomStringUtils.randomAlphanumeric(8) , RandomStringUtils.randomAlphanumeric(8) , role_id, organization_id, test);
			  
		  }
		  //create user source column
		  String token = spogServer.getJWTToken();
		  test.log(LogStatus.INFO,"delete user source columns");
		  source4SpogServer.deleteUsersSourcesColumnsWithCheck(created_user_id, token, SpogConstants.SUCCESS_GET_PUT_DELETE, null);
		  
		  test.log(LogStatus.INFO,"get source column info");
		  ArrayList<HashMap<String, Object>> columnsList = source4SpogServer.getSourceColumnArrayList(createdSourceColumns, token);
		  test.log(LogStatus.INFO,"createUsersSourcesColumnsWithCheck");
		  source4SpogServer.createUsersSourcesColumnsWithCheck(created_user_id, columnsList, token, SpogConstants.SUCCESS_POST, errorCode);
		  //update user source column
		  String updatePassword = password;
		  if(!updateUserSourceColumnAdmin.equalsIgnoreCase(csrAdminUserName)){
			  updatePassword= common_password;
		  }
	
		  spogServer.userLogin(updateUserSourceColumnAdmin, updatePassword, test);
		  token = spogServer.getJWTToken();
		  source4SpogServer.setToken(token);
		  test.log(LogStatus.INFO,"get source column info");
		  columnsList = source4SpogServer.getSourceColumnArrayList(updatedSourceColumns, token);
		  test.log(LogStatus.INFO,"updateUsersSourcesColumnsWithCheck");
		  source4SpogServer.updateUsersSourcesColumnsWithCheck(created_user_id, columnsList, token, statusCode, errorCode);
		 
		  spogServer.userLogin(createUserSourceColumnAdmin, password, test);
		  token = spogServer.getJWTToken();
		  source4SpogServer.setToken(token);
		  
		  test.log(LogStatus.INFO,"delete user source columns");
		  source4SpogServer.deleteUsersSourcesColumnsWithCheck(created_user_id, token, SpogConstants.SUCCESS_GET_PUT_DELETE, null);
		
	 }
	  
	  @Test(enabled=true)
	  /**
	   * 9. fail to update user's source column without login, report 401
	   * 10. fail to update user's source column with invalid token, report 401
	   */
	  public void updateUsersSourcesColumnsWithoutLogin(){
		  
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("shuo.zhang");
		  spogServer.errorHandle.printInfoMessageInDebugFile("/****************" + this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName() + "**************/");
		  spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword, test);
		  		
		  String token = spogServer.getJWTToken();
		  test.log(LogStatus.INFO,"updateUsersSourcesColumnsWithoutLoginAndCheck");
		  source4SpogServer.updateUsersSourcesColumnsWithoutLoginAndCheck(direct_user_id);
		  test.log(LogStatus.INFO,"updateUsersSourcesColumns With invalide token");	  
		  ArrayList<HashMap<String, Object>> columnsList = new ArrayList<HashMap<String, Object>>();
		  source4SpogServer.updateUsersSourcesColumnsWithCheck(direct_user_id, columnsList, UUID.randomUUID().toString(), SpogConstants.NOT_LOGGED_IN,  ErrorCode.AUTHORIZATION_HEADER_BLANK);
	 }
	  
	  @DataProvider(name = "organizationAndUserInfo1")
		public final Object[][] getOrganizationAndUserInfo1() {
			return new Object[][] {
				{UUID.randomUUID().toString(),"true", "1",direct_user_id, SpogConstants.RESOURCE_NOT_EXIST, ErrorCode.COLUMN_NOT_EXIST}, 
				{null,"true","1", direct_user_id, SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.ELEMENT_BLANK},
				{"","true", "1", direct_user_id, SpogConstants.REQUIRED_INFO_NOT_EXIST, ErrorCode.ELEMENT_BLANK}, 
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
	 * 12. set column id as null or "", report 400
	 * 15. set order_id as 0 or negative number, it should report error 400
	 * 16. set user_id as non-existing id, create source column, report 404
	 * 17. set user_id as null or "", create source column, report 400
	 * 22. set order_id more than 14, report error 400
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
		  test.log(LogStatus.INFO,"updateUsersSourcesColumnsWithCheck");
		  source4SpogServer.updateUsersSourcesColumnsWithCheck(user_id, columnsList, token, statusCode, errorCode);
		  
	}

	  
	  @Test( enabled=true)
	  /**
	   *18. set two columns with same column_id, it should use the first one
	   *23. if create source column is not called, update source column should be successful
	   */
	  public void updateColumnsWithSameColumnID( ){
		  
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("shuo.zhang");
		  spogServer.errorHandle.printInfoMessageInDebugFile("/****************updateColumnsWithSameColumnID**************/");
		  spogServer.userLogin(csrAdminUserName, csrAdminPassword, test);
		  
				
		  String token = spogServer.getJWTToken();
		  test.log(LogStatus.INFO,"get source column info");
		  ArrayList<HashMap<String, Object>> columnsList = source4SpogServer.getSourceColumnArrayList("Name;true;1", token);
		  test.log(LogStatus.INFO,"createUsersSourcesColumnsWithCheck");
		  source4SpogServer.createUsersSourcesColumnsWithCheck(direct_user_id, columnsList, token, SpogConstants.SUCCESS_POST, null);
		  test.log(LogStatus.INFO,"get source column info");
		  columnsList = source4SpogServer.getSourceColumnArrayList("Name;true;1,Name;false;3", token);
		  for(int i=0; i<2; i++){
			  test.log(LogStatus.INFO,"updateUsersSourcesColumnsWithCheck");
			  source4SpogServer.updateUsersSourcesColumnsWithCheck(direct_user_id, columnsList, token, SpogConstants.SUCCESS_GET_PUT_DELETE, null);
			  test.log(LogStatus.INFO,"delete user source columns");
			  source4SpogServer.deleteUsersSourcesColumnsWithCheck(direct_user_id, token, SpogConstants.SUCCESS_GET_PUT_DELETE, null);
		  }
		
		  
		  
		  
	 }
	 
	  @Test( enabled=true)
	  public void csrReadOnlyAdminCanUpdateUsersSourcesColumns(){
		  
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("shuo.zhang");
		  spogServer.errorHandle.printInfoMessageInDebugFile("/****************adminCanUpdateUsersSourcesColumns**************/");
		  spogServer.userLogin(this.csrReadOnlyAdminUserName, this.csrReadOnlyAdminPassword, test);

		  //create user source column
		  String token = spogServer.getJWTToken();
		  source4SpogServer.setToken(token);
		  String csr_read_only_id = spogServer.GetLoggedinUser_UserID();
		  test.log(LogStatus.INFO,"delete user source columns");
		  source4SpogServer.deleteUsersSourcesColumnsWithCheck(csr_read_only_id, token, SpogConstants.SUCCESS_GET_PUT_DELETE, null);
		  
		  test.log(LogStatus.INFO,"get source column info");
		  ArrayList<HashMap<String, Object>> columnsList = source4SpogServer.getSourceColumnArrayList("Group;true;3,VM Name;true;1", token);
		  test.log(LogStatus.INFO,"create user source columns");
		  source4SpogServer.createUsersSourcesColumnsWithCheck(csr_read_only_id, columnsList, token, SpogConstants.SUCCESS_POST, null);
		  
		  
		  test.log(LogStatus.INFO,"get source column info");
		  columnsList = source4SpogServer.getSourceColumnArrayList("Name;true;1", token);
		  test.log(LogStatus.INFO,"updateUsersSourcesColumnsWithCheck");	
		  source4SpogServer.updateUsersSourcesColumnsWithCheck(csr_read_only_id, columnsList, token, SpogConstants.SUCCESS_GET_PUT_DELETE, null);
		 
		  spogServer.userLogin(this.csrAdminUserName, this.csrAdminPassword, test);
		  token = spogServer.getJWTToken();
		  source4SpogServer.setToken(token);
		  source4SpogServer.updateUsersSourcesColumnsWithCheck(csr_read_only_id, columnsList, token, SpogConstants.SUCCESS_GET_PUT_DELETE, null);
			 
		  test.log(LogStatus.INFO,"delete user source columns");
		  source4SpogServer.deleteUsersSourcesColumnsWithCheck(csr_read_only_id, token, SpogConstants.SUCCESS_GET_PUT_DELETE, null);
		
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
