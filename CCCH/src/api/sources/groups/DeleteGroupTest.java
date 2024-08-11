package api.sources.groups;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.SpogConstants;
import InvokerServer.SPOGServer;
import InvokerServer.Org4SPOGServer;
import bsh.org.objectweb.asm.Constants;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import groovyjarjarantlr.PreservingFileWriter;
import io.restassured.response.Response;

import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;
import java.util.Arrays;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

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

import Constants.SpogConstants;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;

public class DeleteGroupTest extends base.prepare.Is4Org {
	  private SPOGServer spogServer;
	  private UserSpogServer userSpogServer;
	  private Org4SPOGServer org4SpogServer;
	  private String csrOrg_id;
	  private String directOrg_id;
	  private String directOrg2_id;
	  private String mspOrg_id;
	  private String mspOrg2_id;
	  private String account_id;
	  private String account2_id;
	  private String msp_email;
	  private String msp_account_admin;
	  private String direct_email;
	  private String accountDirect_email;
	  private String csrReadyOnlyUser = "liuyu05@arcserve.com";
	  private String sharePassword = "Caworld_2018";
//	  private ExtentReports rep;
	  private ExtentTest test;
	  private Response response;
	  private String csrGlobalLoginUser;
	  private String csrGlobalLoginPassword;
	  private String csr_token;
	  private String  org_prefix=this.getClass().getSimpleName();
	  
	//add root msp, sub msp, accounts;
	  private String root_msp1_id;
	  private String root_msp2_id;
	  private String subMSP1_root1_id;
	  private String subMSP2_root1_id;
	  private String subMSP1_root2_id;
	  private String subMSP2_root2_id;
	  private String account_rootMSP1_id;
	  private String account2_rootMSP1_id;
	  private String account_rootMSP2_id;
	  private String account_subMSP1_root1_id;
	  private String account2_subMSP1_root1_id;
	  private String account_subMSP2_root1_id;
	  private String account_subMSP1_root2_id;
	  private String account_subMSP2_root2_id;
	  private String root_msp_email;
	  private String root_msp_account_admin_email;
	  private String root_msp_account_admin_userId;
	  private String sub_msp_email;
	  private String sub_msp_account_admin_email;
	  private String sub_msp_account_admin_userId;
	  private String accountDirect_rootMsp_email;
	  private String accountDirect_subMsp_email;
	  //this is for update portal, each testng class is taken as BQ set
//	  private SQLServerDb bqdb1;
//	  public int Nooftest;
//	  private long creationTime;
//	  private String BQName=null;
//	  private String runningMachine;
//	  private testcasescount count1;
//	  private String buildVersion;
	  
	  @BeforeClass
	  @Parameters({ "baseURI", "port" , "csrAdminUserName", "csrAdminPassword","logFolder","runningMachine", "buildVersion"})
	  public void beforeClass(String baseURI, String port, String userName, String password, String logFolder, String runningMachine, String buildVersion) {
		//this is for update portal
		  this.BQName = this.getClass().getSimpleName();
		  String author = "yuefen.liu";
		  this.runningMachine = runningMachine;
		  SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		  java.util.Date date=new java.util.Date();
		  this.buildVersion=buildVersion+"_"+dateFormater.format(date);
		  Nooftest=0;
		  bqdb1 = new SQLServerDb();
		  count1 = new testcasescount();
		  if(count1.isstarttimehit()==0) {
			System.out.println("Into get loggedInUserById");
			creationTime=System.currentTimeMillis();
			count1.setcreationtime(creationTime);
			//creationTime = System.currentTimeMillis();
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
		  setEnv(baseURI,  port,  userName, password);
		  //end 
		  
		  rep = ExtentManager.getInstance("DeleteGroupTest",logFolder);
		  test = rep.startTest("BeforeClass");
		  spogServer = new SPOGServer(baseURI, port);
		  userSpogServer =new UserSpogServer(baseURI, port);
		  org4SpogServer = new Org4SPOGServer(baseURI, port);
		  spogServer.userLogin(userName, password);
		  csr_token=spogServer.getJWTToken();
		  this.csrGlobalLoginUser = userName;
		  this.csrGlobalLoginPassword = password;
		  //create org
		  csrOrg_id = spogServer.GetLoggedinUserOrganizationID();
		  System.out.println("csr org id:"+csrOrg_id);
		  directOrg_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_deleteGroup_direct1_yuefen")+org_prefix,"direct",spogServer.ReturnRandom("yuefen_direct1_group@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("direct_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  directOrg2_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_deleteGroup_direct2_yuefen")+org_prefix,"direct",spogServer.ReturnRandom("yuefen_direct2_group@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("direct2_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  mspOrg_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_deleteGroup_yuefen_msp1")+org_prefix,"msp",spogServer.ReturnRandom("yuefen_msp1_group@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("msp_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  mspOrg2_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_deleteGroup_yuefen_msp2")+org_prefix,"msp",spogServer.ReturnRandom("yuefen_msp2_group@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("msp2_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  account_id = spogServer.createAccountWithCheck(mspOrg_id,spogServer.ReturnRandom("spogqa_accoun1_msp1_yuefen_deleteGroup")+org_prefix, mspOrg_id, test);
		  account2_id =spogServer.createAccountWithCheck(mspOrg2_id,spogServer.ReturnRandom("spogqa_accoun2_msp2_yuefen_deleteGroup")+org_prefix, mspOrg2_id, test);
		  
		//*************************************************************************************************************
			 //create root msp, sub msp, accounts
			  root_msp1_id=spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_update_yuefen_rootmsp1")+org_prefix,"msp",spogServer.ReturnRandom("spogqa_update_rootmsp1@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("direct_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
			  root_msp2_id=spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_update_yuefen_rootmsp2")+org_prefix,"msp",spogServer.ReturnRandom("spogqa_update_rootmsp2@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("direct_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
			  
			  System.out.println("convert to root msp starts");
			  org4SpogServer.setToken(csr_token);
			  org4SpogServer.convertToRootMSP(root_msp1_id);
			  org4SpogServer.convertToRootMSP(root_msp2_id);
			  
			  subMSP1_root1_id=org4SpogServer.createSubMSPAccountincc(spogServer.ReturnRandom("spogqa_update_yuefen_submsp1_rootmsp1")+org_prefix, root_msp1_id,spogServer.ReturnRandom("submsp1_rootmsp1_spogqa"),spogServer.ReturnRandom("liu_spogqa"),spogServer.ReturnRandom("spogqa_update_submsp1_rootmsp1@spogqa.com"), "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
			  subMSP2_root1_id=org4SpogServer.createSubMSPAccountincc(spogServer.ReturnRandom("spogqa_update_yuefen_submsp1_rootmsp1")+org_prefix, root_msp1_id,spogServer.ReturnRandom("submsp2_rootmsp1_spogqa"),spogServer.ReturnRandom("liu_spogqa"),spogServer.ReturnRandom("spogqa_update_submsp2_rootmsp1@spogqa.com"), "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
			  
			  subMSP1_root2_id=org4SpogServer.createSubMSPAccountincc(spogServer.ReturnRandom("spogqa_update_yuefen_submsp1_rootmsp2")+org_prefix, root_msp2_id,spogServer.ReturnRandom("submsp1_rootmsp2_spogqa"),spogServer.ReturnRandom("liu_spogqa"),spogServer.ReturnRandom("spogqa_update_submsp1_rootmsp2@spogqa.com"), "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
			  subMSP2_root2_id=org4SpogServer.createSubMSPAccountincc(spogServer.ReturnRandom("spogqa_update_yuefen_submsp2_rootmsp2")+org_prefix, root_msp2_id,spogServer.ReturnRandom("submsp2_rootmsp2_spogqa"),spogServer.ReturnRandom("liu_spogqa"),spogServer.ReturnRandom("spogqa_update_submsp2_rootmsp2@spogqa.com"), "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test); 
			  
			  account_rootMSP1_id=spogServer.createAccountWithCheck(root_msp1_id,spogServer.ReturnRandom("accoun1_rootmsp1_yuefen_spogqa")+org_prefix, root_msp1_id, test);
			  account2_rootMSP1_id=spogServer.createAccountWithCheck(root_msp1_id,spogServer.ReturnRandom("accoun2_rootmsp1_yuefen_spogqa")+org_prefix, root_msp1_id, test);
			  
			  account_rootMSP2_id=spogServer.createAccountWithCheck(root_msp2_id,spogServer.ReturnRandom("accoun1_rootmsp2_yuefen_spogqa")+org_prefix, root_msp2_id, test);
			  
			  account_subMSP1_root1_id=spogServer.createAccountWithCheck(subMSP1_root1_id,spogServer.ReturnRandom("accoun1_submsp1_rootmsp1_yuefen_spogqa")+org_prefix, subMSP1_root1_id, test);
			  account2_subMSP1_root1_id=spogServer.createAccountWithCheck(subMSP1_root1_id,spogServer.ReturnRandom("accoun2_submsp1_rootmsp1_yuefen_spogqa")+org_prefix, subMSP1_root1_id, test);
			  
			  account_subMSP2_root1_id=spogServer.createAccountWithCheck(subMSP2_root1_id,spogServer.ReturnRandom("accoun1_submsp1_rootmsp1_yuefen_spogqa")+org_prefix, subMSP2_root1_id, test);
			  account_subMSP1_root2_id=spogServer.createAccountWithCheck(subMSP1_root2_id,spogServer.ReturnRandom("accoun1_submsp1_rootmsp1_yuefen_spogqa")+org_prefix, subMSP1_root2_id, test);
			  account_subMSP2_root2_id=spogServer.createAccountWithCheck(subMSP2_root2_id,spogServer.ReturnRandom("accoun1_submsp1_rootmsp1_yuefen_spogqa")+org_prefix, subMSP2_root2_id, test);
			  
			//create user under root msp related
				this.root_msp_email = spogServer.ReturnRandom("rootmsp_yuefen_update@spogqa.com");
				spogServer.createUserAndCheck(root_msp_email, sharePassword, "yuefen", "liu", "msp_admin", root_msp1_id, test);
				this.root_msp_account_admin_email = spogServer.ReturnRandom("rootmsp_account_admin_yuefen_update@spogqa.com");
				this.root_msp_account_admin_userId = spogServer.createUserAndCheck(root_msp_account_admin_email, sharePassword, "yuefen", "liu", "msp_account_admin", root_msp1_id, test);
				userSpogServer.assignMspAccountAdmins(root_msp1_id, account_rootMSP1_id, root_msp_account_admin_userId , csr_token);
				
				this.sub_msp_email = spogServer.ReturnRandom("submsp_yuefen_update@spogqa.com");
				spogServer.createUserAndCheck(sub_msp_email, sharePassword, "yuefen", "liu", "msp_admin", subMSP1_root1_id, test);
				this.sub_msp_account_admin_email = spogServer.ReturnRandom("submsp_yuefen_update@spogqa.com");
				this.sub_msp_account_admin_userId = spogServer.createUserAndCheck(sub_msp_account_admin_email, sharePassword, "yuefen", "liu", "msp_account_admin", subMSP1_root1_id, test);
				userSpogServer.assignMspAccountAdmins(subMSP1_root1_id, account_subMSP1_root1_id, sub_msp_account_admin_userId , csr_token);
				
				this.accountDirect_rootMsp_email = spogServer.ReturnRandom("accountDirect_rootMsp_yuefen_update@spogqa.com");
				spogServer.createUserAndCheck(accountDirect_rootMsp_email, sharePassword, "yuefen", "liu", "direct_admin", account_rootMSP1_id, test);
				
				this.accountDirect_subMsp_email = spogServer.ReturnRandom("accountDirect_subMsp_yuefen_update@spogqa.com");
				spogServer.createUserAndCheck(accountDirect_subMsp_email, sharePassword, "yuefen", "liu", "direct_admin", account_subMSP1_root1_id, test);
			
			//**************************************************************************************************************** 	 
		  
				//create direct user
		  direct_email = spogServer.ReturnRandom("direct_yuefen_deletegroup@spogqa.com");
		  spogServer.createUserAndCheck(direct_email, sharePassword, "yuefen", "liu", "direct_admin", directOrg_id, test);
		  //create msp user
		  //create msp1 user
		  msp_email = spogServer.ReturnRandom("msp1_yuefen_deletegroup@spogqa.com");
		  spogServer.createUserAndCheck(msp_email, sharePassword, "yuefen", "liu", "msp_admin", mspOrg_id, test);
		  //create msp account amdin
		  this.msp_account_admin =spogServer.ReturnRandom("msp_account_admin_group@spogqa.com");
		  String admin_id =spogServer.createUserAndCheck(msp_account_admin, sharePassword, "yuefen", "liu", "msp_account_admin", mspOrg_id, test);
		  String[] userIds = {admin_id};
		  userSpogServer.assignMspAccountAdmins(mspOrg_id, account_id, userIds, csr_token);
		  //create aacount user
		  accountDirect_email = spogServer.ReturnRandom("account1_msp1_yuefen_deletegroup@spogqa.com");
		  spogServer.createUserAndCheck(accountDirect_email, sharePassword, "yuefen", "liu", "direct_admin", account_id, test);
	  }
	  
	  @DataProvider(name = "groupInfo50")
	  public final Object[][] getGroupInfo50() {
		  return new Object[][] { 
			  {root_msp_email, root_msp1_id, spogServer.ReturnRandom("spogqa_direct_account_root_group"), spogServer.ReturnRandom("direct_account_group")}, 
			  {root_msp_email, account_rootMSP1_id, spogServer.ReturnRandom("spogqa_direct_account_root_group"), spogServer.ReturnRandom("direct_account_group")}, 
			  {root_msp_email, account2_rootMSP1_id, spogServer.ReturnRandom("spogqa_direct_account_root_group"), spogServer.ReturnRandom("direct_account_group")}, 
			  {root_msp_account_admin_email,account_rootMSP1_id, spogServer.ReturnRandom("spogqa_direct_account_group"), spogServer.ReturnRandom("direct_account_group")},
			  
			  {sub_msp_email, subMSP1_root1_id, spogServer.ReturnRandom("spogqa_msp_group"), spogServer.ReturnRandom("msp_group")},
			  {sub_msp_email, account_subMSP1_root1_id, spogServer.ReturnRandom("spogqa_msp_group"), spogServer.ReturnRandom("msp_group")},
	          {sub_msp_email, account2_subMSP1_root1_id, spogServer.ReturnRandom("spogqa_account_group"), spogServer.ReturnRandom("account_group")},
	          {sub_msp_account_admin_email, account_subMSP1_root1_id, spogServer.ReturnRandom("spogqa_msp_group"), spogServer.ReturnRandom("msp_group")},
	          };
	  }
	  
	  @Test(dataProvider = "groupInfo50")
	  public void deleteGroupSuccess(String loginUser, String orgId, String groupName, String groupDescription ){	 
		  System.out.println("deleteGroupSuccess");
		  test = rep.startTest("deleteGroupSuccess");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login
		  spogServer.userLogin(loginUser, sharePassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  //create group
		  String group_id = spogServer.createGroupWithCheck(orgId, groupName, groupDescription, test);
		  System.out.println("group_id:"+group_id);
		  //delete group
		  spogServer.deleteGroupWithExpectedStatusCode(group_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	  }
	  
	  @DataProvider(name = "groupInfo60")
	  public final Object[][] getGroupInfo60() {
		  return new Object[][] { 
			  
		{root_msp_email, subMSP1_root1_id, spogServer.ReturnRandom("spogqa_direct_account_root_group"), spogServer.ReturnRandom("direct_account_group") }, 
		{root_msp_email, subMSP2_root1_id, spogServer.ReturnRandom("spogqa_direct_account_root_group"), spogServer.ReturnRandom("direct_account_group") }, 
		{root_msp_email, subMSP1_root1_id, spogServer.ReturnRandom("spogqa_direct_account_root_group"), spogServer.ReturnRandom("direct_account_group") }, 
		{root_msp_email, account_subMSP1_root1_id, spogServer.ReturnRandom("spogqa_direct_account_root_group"), spogServer.ReturnRandom("direct_account_group") },
		{root_msp_email, account2_subMSP1_root1_id, spogServer.ReturnRandom("spogqa_direct_account_root_group"), spogServer.ReturnRandom("direct_account_group") },
		
		//{root_msp_account_admin_email, root_msp1_id, spogServer.ReturnRandom("spogqa_direct_account_root_group"), spogServer.ReturnRandom("direct_account_group") }, 
		{root_msp_account_admin_email, subMSP1_root1_id, spogServer.ReturnRandom("spogqa_direct_account_root_group"), spogServer.ReturnRandom("direct_account_group") }, 
		{root_msp_account_admin_email, subMSP2_root1_id, spogServer.ReturnRandom("spogqa_direct_account_root_group"), spogServer.ReturnRandom("direct_account_group") }, 
		{root_msp_account_admin_email, subMSP1_root1_id, spogServer.ReturnRandom("spogqa_direct_account_root_group"), spogServer.ReturnRandom("direct_account_group") }, 
		{root_msp_account_admin_email, account_subMSP1_root1_id, spogServer.ReturnRandom("spogqa_direct_account_root_group"), spogServer.ReturnRandom("direct_account_group") },
		{root_msp_account_admin_email, account2_subMSP1_root1_id, spogServer.ReturnRandom("spogqa_direct_account_root_group"), spogServer.ReturnRandom("direct_account_group") },
		
		
		{sub_msp_email, subMSP2_root1_id, spogServer.ReturnRandom("spogqa_direct_account_root_group"), spogServer.ReturnRandom("direct_account_group") },
		{sub_msp_email, account_subMSP2_root1_id, spogServer.ReturnRandom("spogqa_direct_account_root_group"), spogServer.ReturnRandom("direct_account_group")}, 
		{sub_msp_account_admin_email, account2_subMSP1_root1_id, spogServer.ReturnRandom("spogqa_direct_account_root_group"), spogServer.ReturnRandom("direct_account_group") },
		{sub_msp_account_admin_email, account_subMSP2_root1_id, spogServer.ReturnRandom("spogqa_direct_account_root_group"), spogServer.ReturnRandom("direct_account_group") },
		//{sub_msp_account_admin_email, subMSP1_root1_id, spogServer.ReturnRandom("spogqa_direct_account_root_group"), spogServer.ReturnRandom("direct_account_group") },
		
		
		{accountDirect_rootMsp_email, account2_rootMSP1_id, spogServer.ReturnRandom("spogqa_direct_account_root_group"), spogServer.ReturnRandom("direct_account_group") },
		{accountDirect_rootMsp_email, account_subMSP2_root1_id, spogServer.ReturnRandom("spogqa_direct_account_root_group"), spogServer.ReturnRandom("direct_account_group") },
		
		{accountDirect_subMsp_email, account2_subMSP1_root1_id, spogServer.ReturnRandom("spogqa_direct_account_root_group"), spogServer.ReturnRandom("direct_account_group")},
		{accountDirect_subMsp_email, account_subMSP2_root1_id, spogServer.ReturnRandom("spogqa_direct_account_root_group"), spogServer.ReturnRandom("direct_account_group") },
		
		{msp_email, root_msp1_id, spogServer.ReturnRandom("spogqa_direct_account_root_group"), spogServer.ReturnRandom("direct_account_group") }, 
		{msp_email, account_rootMSP1_id, spogServer.ReturnRandom("spogqa_direct_account_root_group"), spogServer.ReturnRandom("direct_account_group") }, 
		{msp_email, subMSP1_root1_id, spogServer.ReturnRandom("spogqa_direct_account_root_group"), spogServer.ReturnRandom("direct_account_group") }, 
		{msp_email, account_subMSP1_root1_id, spogServer.ReturnRandom("spogqa_direct_account_root_group"), spogServer.ReturnRandom("direct_account_group")}, 
		{direct_email, root_msp1_id, spogServer.ReturnRandom("spogqa_direct_account_root_group"), spogServer.ReturnRandom("direct_account_group") }, 
		{root_msp_email,  mspOrg_id, spogServer.ReturnRandom("spogqa_direct_account_root_group"), spogServer.ReturnRandom("direct_account_group") }, 
		{root_msp_email, directOrg_id, spogServer.ReturnRandom("spogqa_direct_account_root_group"), spogServer.ReturnRandom("direct_account_group")},
	          };
	  }
	  @Test(dataProvider = "groupInfo60")
	  //direct user can't create group under direct org
	  public void delelteGroupFail(String loginUser, String orgId, String groupName, String groupDescription){	 
		  System.out.println("delelteGroupFail");
		  test = rep.startTest("delelteGroupFail");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login with csr admin
		  spogServer.userLogin(this.csrGlobalLoginUser, this.csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  //create group 
		  String group_id = spogServer.createGroupSuccessWithCheck(orgId, groupName, groupDescription, test);
		  
		  //login 
		  spogServer.userLogin(loginUser, sharePassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  //delete group
		  Response response = spogServer.deleteGroup(group_id, test);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS,test);
		  spogServer.checkErrorCode(response, "00100101");
	  }
	  
	  @DataProvider(name = "groupInfo8")
	  public final Object[][] getGroupInfo8() {
		  return new Object[][] {  
			  {directOrg2_id, spogServer.ReturnRandom("spogqa_direct_group"), spogServer.ReturnRandom("direct_group")},
			  {mspOrg_id, spogServer.ReturnRandom("spogqa_msp_group"), spogServer.ReturnRandom("msp_group")},
	          {account_id, spogServer.ReturnRandom("spogqa_account_group"), spogServer.ReturnRandom("account_group")}
	          };
	  }
	  @Test(dataProvider = "groupInfo8")
	  //csr read-only user can't delete group
	  public void csrReadOnlyUserDeleteGroupFail(String orgId, String groupName, String groupDescription){	 
		  System.out.println("csrReadOnlyUserDeleteGroupFail");
		  test = rep.startTest("csrReadOnlyUserDeleteGroupFail");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login with csr admin
		  spogServer.userLogin(this.csrGlobalLoginUser, this.csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  //create group 
		  String group_id = spogServer.createGroupSuccessWithCheck(orgId, groupName, groupDescription, test);
		  
		  //login with csr read-only user
		  spogServer.userLogin(this.csrReadyOnlyUser, sharePassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  //delete group
		  Response response = spogServer.deleteGroup(group_id, test);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS,test);
		  spogServer.checkErrorCode(response, "00100101");
	  }
	  
	  @DataProvider(name = "groupInfo")
	  public final Object[][] getGroupInfo() {
		  return new Object[][] { {csrOrg_id, spogServer.ReturnRandom("spogqa_csr_Deletegroup"), spogServer.ReturnRandom("csr_group")}, 
			  {directOrg_id, spogServer.ReturnRandom("spogqa_direct_Deletegroup"), spogServer.ReturnRandom("direct_group")},
			  {mspOrg_id, spogServer.ReturnRandom("spogqa_msp_deletegroup"), spogServer.ReturnRandom("msp_group")},
	          {account_id, spogServer.ReturnRandom("spogqa_account_deletegroup"), spogServer.ReturnRandom("account_group")},
	          };
		}  
	  @Test(dataProvider = "groupInfo")
	  //csr user can delete group under any org
	  public void csrUserDeleteGroup(String orgId, String groupName, String groupDescription ){	 
		  System.out.println("csrUserDeleteGroup");
		  test = rep.startTest("csrUserDeleteGroup");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login with csr user
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  //create group
		  String group_id = spogServer.createGroupWithCheck(orgId, groupName, groupDescription, test);
		  System.out.println("group_id:"+group_id);
		  spogServer.deleteGroupWithExpectedStatusCode(group_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	  }
	  
	  @Test
	  //direct user can delete group under direct org
	  public void directUserDeleteGroupSuccess(){	 
		  System.out.println("directUserDeleteGroupSuccess");
		  test = rep.startTest("directUserDeleteGroupSuccess");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login with direct user
		  spogServer.userLogin(direct_email, sharePassword, SpogConstants.SUCCESS_LOGIN, test);
		  //create group under itself direct org
		  String group_id = spogServer.createGroupWithCheck(directOrg_id, spogServer.ReturnRandom("direct_deletegroup_yuefen"), "", test);
		  System.out.println("group_id:"+group_id);
		  //delete group
		  spogServer.deleteGroupWithExpectedStatusCode(group_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	  }
	  
	  
	  @DataProvider(name = "groupInfo2")
	  public final Object[][] getGroupInfo2() {
		  return new Object[][] { {csrOrg_id, spogServer.ReturnRandom("spogqa_csr_deletegroup"), spogServer.ReturnRandom("csr_group")}, 
			  {directOrg2_id, spogServer.ReturnRandom("spogqa_direct_deletegroup"), spogServer.ReturnRandom("direct_group")},
			  {mspOrg_id, spogServer.ReturnRandom("spogqa_msp_deletegroup"), spogServer.ReturnRandom("msp_group")},
	          {account_id, spogServer.ReturnRandom("spogqa_account_deletegroup"), spogServer.ReturnRandom("account_group")},
	          };
	  }
	  @Test(dataProvider = "groupInfo2")
	  //direct user can't create group under other org
	  public void directUserDeleteGroupFail(String orgId, String groupName, String groupDescription){	 
		  System.out.println("directUserDeleteGroupFail");
		  test = rep.startTest("directUserDeleteGroupFail");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login with csr user
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  //create group 
		  String group_id = spogServer.createGroupWithCheck(orgId, groupName, groupDescription, test);
	
          //login with direct user
		  spogServer.userLogin(direct_email, sharePassword, SpogConstants.SUCCESS_LOGIN, test);
		  //delete group
		  Response response = spogServer.deleteGroup(group_id, test);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
		  spogServer.checkErrorCode(response, "00100101");
	  }
	  
	  @DataProvider(name = "groupInfo3")
	  public final Object[][] getGroupInfo3() {
		  return new Object[][] { 
			  {mspOrg_id, spogServer.ReturnRandom("spogqa_msp_deletegroup"), spogServer.ReturnRandom("msp_group")},
	          {account_id, spogServer.ReturnRandom("spogqa_account_deletegroup"), spogServer.ReturnRandom("account_group")},
	          };
	  }
	  @Test(dataProvider = "groupInfo3")
	  //msp user can delete group under its msp org and own account
	  public void mspUserDeleteGroupSuccess(String orgId, String groupName, String groupDescription){	 
		  System.out.println("mspUserDeleteGroupSuccess");
		  test = rep.startTest("mspUserDeleteGroupSuccess");
		  test.assignAuthor("Liu Yuefen");

		  //login with msp1 user
		  spogServer.userLogin(msp_email, sharePassword, SpogConstants.SUCCESS_LOGIN, test);
		  //create group under msp/account org
		  String group_id = spogServer.createGroupWithCheck(orgId, groupName, groupDescription, test);
		  //delete group
		  spogServer.deleteGroupWithExpectedStatusCode(group_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	  }
	  
	  @DataProvider(name = "groupInfo4")
	  public final Object[][] getGroupInfo4() {
		  return new Object[][] { {csrOrg_id, spogServer.ReturnRandom("spogqa_csr_deletegroup"), spogServer.ReturnRandom("csr_group")}, 
			  {directOrg2_id, spogServer.ReturnRandom("spogqa_direct_deletegroup"), spogServer.ReturnRandom("direct_group")},
			  {mspOrg2_id, spogServer.ReturnRandom("spogqa_msp_deletegroup"), spogServer.ReturnRandom("msp_group")},
	          {account2_id, spogServer.ReturnRandom("spogqa_account_deletegroup"), spogServer.ReturnRandom("account_group")},
	          };
	  }
	  @Test(dataProvider = "groupInfo4")
	  //msp user can't delete group under other org
	  public void mspUserDeleteGroupFail(String orgId, String groupName, String groupDescription){	 
		  System.out.println("mspUserDeleteGroupFail");
		  test = rep.startTest("mspUserDeleteGroupFail");
		  test.assignAuthor("Liu Yuefen");
          
		  //login with csr user
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  //create group under itself direct org
		  String group_id = spogServer.createGroupWithCheck(orgId, groupName, groupDescription, test);
		  //login with msp1 user
		  spogServer.userLogin(msp_email, sharePassword, SpogConstants.SUCCESS_LOGIN, test);
		  //delete group under itself direct org
		  Response response = spogServer.deleteGroup(group_id, test);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
		  spogServer.checkErrorCode(response, "00100101");
	  }
	  
	  @Test
	  //account user can delete group
	  public void accountUserDeleteGroupSuccess(){	 
		  System.out.println("accountUserDeleteGroupSuccess");
		  test = rep.startTest("accountUserDeleteGroupSuccess");
		  test.assignAuthor("Liu Yuefen");

		  //login with account user
		  spogServer.userLogin(accountDirect_email, sharePassword, SpogConstants.SUCCESS_LOGIN, test);
		  //create group under itself org
		  String group_id = spogServer.createGroupWithCheck(account_id, spogServer.ReturnRandom("account_group_yuefen"), "", test);
		  //delete group
		  spogServer.deleteGroupWithExpectedStatusCode(group_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	  }
	  
	  @DataProvider(name = "groupInfo5")
	  public final Object[][] getGroupInfo5() {
		  return new Object[][] { {csrOrg_id, spogServer.ReturnRandom("spogqa_csr_deletegroup"), spogServer.ReturnRandom("csr_group")}, 
			  {directOrg2_id, spogServer.ReturnRandom("spogqa_direct_deletegroup"), spogServer.ReturnRandom("direct_group")},
			  {mspOrg2_id, spogServer.ReturnRandom("spogqa_msp_deletegroup"), spogServer.ReturnRandom("msp_group")},
	          {account2_id, spogServer.ReturnRandom("spogqa_account_deletegroup"), spogServer.ReturnRandom("account_group")},
	          };
	  }
	  @Test(dataProvider = "groupInfo5")
	  //account user can't delete group under other org
	  public void accountUserDeleteGroupFail(String orgId, String groupName, String groupDescription){	 
		  System.out.println("accountUserDeleteGroupFail");
		  test = rep.startTest("accountUserDeleteGroupFail");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login with csr user
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  //create group 
		  String group_id = spogServer.createGroupWithCheck(orgId, groupName, groupDescription, test);
		  //login with account user
		  spogServer.userLogin(accountDirect_email, sharePassword, SpogConstants.SUCCESS_LOGIN, test);
		  //delete group
		  Response response = spogServer.deleteGroup(group_id, test);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
		  spogServer.checkErrorCode(response, "00100101");
	  }
	  
	  @DataProvider(name = "groupInfo6")
	  public final Object[][] getGroupInfo6() {
		  return new Object[][] { 
	          {account_id, spogServer.ReturnRandom("spogqa_msp_account_group"), spogServer.ReturnRandom("msp_account_admin_group")},
	          };
	  }
	      
	  @Test(dataProvider = "groupInfo6")
	  //msp account admin can create group under its msp account.
	  public void mspAccountAdminDeleteGroupSuccess(String orgId, String groupName, String groupDescription){	 
		  System.out.println("mspAccountAdminDeleteGroupSuccess");
		  test = rep.startTest("mspAccountAdminDeleteGroupSuccess");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login with msp1 user
		  spogServer.userLogin(msp_account_admin, sharePassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  //create group under msp/account org
		  String group_id =spogServer.createGroupWithCheck(orgId, groupName, groupDescription, test);
		  
		  //delete
		  spogServer.deleteGroupWithExpectedStatusCode(group_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	  }
	  
	  @DataProvider(name = "groupInfo7")
	  public final Object[][] getGroupInfo7() {
		  return new Object[][] { 
			  {csrOrg_id, spogServer.ReturnRandom("spogqa_csr_group"), spogServer.ReturnRandom("csr_group")}, 
			  {directOrg2_id, spogServer.ReturnRandom("spogqa_direct_group"), spogServer.ReturnRandom("direct_group")},
			  //{mspOrg_id, spogServer.ReturnRandom("spogqa_msp_group"), spogServer.ReturnRandom("msp_group")},
	          {account2_id, spogServer.ReturnRandom("spogqa_account_group"), spogServer.ReturnRandom("account_group")},
	          };
	  }
	  @Test(dataProvider = "groupInfo7")
	  //msp account admin can't create group under other org
	  public void mspAccountAdminDeleteGroupFail(String orgId, String groupName, String groupDescription){	 
		  System.out.println("mspAccountAdminDeleteGroupFail");
		  test = rep.startTest("mspAccountAdminDeleteGroupFail");
		  test.assignAuthor("Liu Yuefen");

		  //login with msp1 user
		  spogServer.userLogin(msp_account_admin, sharePassword, SpogConstants.SUCCESS_LOGIN, test);
		  String admin_token = spogServer.getJWTToken();
		  //create group 
		  spogServer.setToken(csr_token);
		  String group_id = spogServer.createGroupSuccessWithCheck(orgId, groupName, groupDescription, test);
		  //delete
		  spogServer.setToken(admin_token);
		  Response response =spogServer.deleteGroup(group_id, test);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS,test);
		  spogServer.checkErrorCode(response, "00100101");
	  }
	  
	  @Test
	  public void DeleteGroupNormalCheck(){	 
		  System.out.println("DeleteGroupNormalCheck");
		  test = rep.startTest("DeleteGroupNormalCheck");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login with csr user
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  //delete group id = null
		  spogServer.deleteGroupWithExpectedStatusCode(null, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
		  
		  //delete group id = ""
		  spogServer.deleteGroupWithExpectedStatusCode("", SpogConstants.NOT_ALLOWED_ON_RESOURCE, test);
		  
		  //delete group id = "00000000-0000-0000-0000-000000000000"
		  Response response = spogServer.deleteGroup("00000000-0000-0000-0000-000000000000", test);
		  spogServer.checkResponseStatus(response, SpogConstants.RESOURCE_NOT_EXIST, test);
		  spogServer.checkErrorCode(response, "00500007");
		                      
		  //create group
		  String groupName = spogServer.ReturnRandom("yuefen_deletegroup_spogqa");
		  String group_id = spogServer.createGroupWithCheck(csrOrg_id, groupName, "", test);
		  //delete group
		  spogServer.deleteGroupWithExpectedStatusCode(group_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  //delete group again
		  response = spogServer.deleteGroup(group_id, test);
		  spogServer.checkErrorCode(response, "00500007");
	  }
	  
	  @Test
	  public void DeleteGroup401(){	 
		  System.out.println("DeleteGroup401");
		  test = rep.startTest("DeleteGroup401");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login with csr user
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  //create group with group name = null
		  String group_id = spogServer.createGroupWithCheck(directOrg2_id, spogServer.ReturnRandom("direct2_deletegroup_yuefen_spogqa"),"direct org2 group", test);
		  
		  //set token =""
		  spogServer.setToken("");
		  //delete group
		  spogServer.deleteGroupWithExpectedStatusCode(group_id, SpogConstants.NOT_LOGGED_IN, test);
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
