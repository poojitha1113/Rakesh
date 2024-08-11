package api.users.sourcefilters;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.sun.mail.handlers.image_gif;

import Constants.ErrorCode;
import Constants.SourceType;
import Constants.SpogConstants;
import InvokerServer.Org4SPOGServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import bsh.org.objectweb.asm.Constants;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class CreateDefaultSourceFilterTest extends base.prepare.Is4Org{
	  private SPOGServer spogServer;
	  private Org4SPOGServer org4SPOGServer;
	  private String directOrgId,directOrgId1;
	  private String mspOrgId;
	  private String mspOrgId1;
	  private String accountOrgId;
	  private String csrAdmin;
	  private String csrPwd;
	  
	  //this is creating msp org or direct org for preparation
	  private String mspOrgEmailForPrepare,mspOrgEmailForPrepare1;
	  private String directOrgNameForPrepare="d_jing_spogqa_direct_org_prepare";
	  private String directOrgEmailForPrepare,directOrgEmailForPrepare1;
	  private String OrgFistNameForPrepare="jing";
	  private String OrgLastNameForPrepare="org_prepare";
	  private String OrgPwdForPrepare="Welcome*02";
	  private String csr_readonly="csr_readonly@arcserve.com";
	  private String csrOrgId;
	  
	  private ExtentTest test;
	  //this is for update portal, each testng class is taken as BQ set
//	  private ExtentReports rep;
//	  private SQLServerDb bqdb1;
//	  public int Nooftest;
//	  private long creationTime;
//	  private String BQName=null;
//	  private String runningMachine;
//	  private testcasescount count1;
//	  private String buildVersion;
	  
	  private String account_user_id1,account_user_id2,accountOrgId1;
	  private String submspaccount_user_id1,submspaccount_user_id2,submspaccountOrgId1,submspaccountOrgId;
	  private String submsp1account_user_id1,submsp1account_user_id2,submsp1accountOrgId1,submsp1accountOrgId;
	  private String rootmspaccount_user_id1,rootmspaccount_user_id2,rootmspaccountOrgId1,rootmspaccountOrgId;
	  private UserSpogServer userSpogServer;
	  private String[] mspAccountAdminUserIds=new String[1];
	  private String[] submspAccountAdminUserIds=new String[1];
	  private String[] submsp1AccountAdminUserIds=new String[1];
	  private String[] rootmspAccountAdminUserIds=new String[1];
	  private String mspOrgNameForPrepare,rootmspOrgEmailForPrepare,rootmspOrgId,submspOrgEmailForPrepare,submspOrgId,submsp1OrgEmailForPrepare,submsp1OrgId;
	  private String accountEmailForPrepare,mspAccountAdminEmailForPrepare,accountEmailForPrepare1;
	  private String rootmspaccountEmailForPrepare,rootmspaccountEmailForPrepare1,rootmspAccountAdminEmailForPrepare;
	  private String submspaccountEmailForPrepare,submspaccountEmailForPrepare1,submspAccountAdminEmailForPrepare;
	  private String submsp1accountEmailForPrepare,submsp1accountEmailForPrepare1,submsp1AccountAdminEmailForPrepare;
	  private String msp_account_admin_token;
	  private String  org_prefix=this.getClass().getSimpleName();
	  
	  //end 
	  @BeforeClass
	  @Parameters({ "baseURI", "port","csrAdminUserName", "csrAdminPassword","logFolder","runningMachine","buildVersion"})
	  public void beforeClass(String baseURI, String port, String csrAdminUserName, String csrAdminPassword, String logFolder, String runningMachine, String buildVersion) {
		//this is for update portal
		  this.BQName = this.getClass().getSimpleName();
		  String author = "Jing.Shan";
		  this.runningMachine = runningMachine;
		  userSpogServer = new UserSpogServer(baseURI, port);
		  org4SPOGServer = new Org4SPOGServer(baseURI, port);
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
				String aa="http://tccapi.arcserve.com";
				bqdb1.updateTable(this.BQName, runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0", String.valueOf(Nooftest), count1.getcreationtime(), "InProgress",author+" and Rest server is "+aa.split("//")[1]);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		  }
		  //end 
		  spogServer = new SPOGServer(baseURI, port);
		  rep = ExtentManager.getInstance("CreateAccountTest",logFolder);
		  test = rep.startTest("beforeClass");
		  this.csrAdmin = csrAdminUserName;
		  this.csrPwd = csrAdminPassword;
		  this.accountEmailForPrepare = spogServer.ReturnRandom("d_jing_spogqa_account_org_prepare@arcserve.com");
		  this.accountEmailForPrepare1 = spogServer.ReturnRandom("d_jing_spogqa_account_org_prepare@arcserve.com");
		  this.submspaccountEmailForPrepare = spogServer.ReturnRandom("d_jing_spogqa_submspaccount_org_prepare@arcserve.com");
		  this.submspaccountEmailForPrepare1 = spogServer.ReturnRandom("d_jing_spogqa_submspaccount1_org_prepare@arcserve.com");
		  this.submsp1accountEmailForPrepare = spogServer.ReturnRandom("d_jing_spogqa_submsp1account_org_prepare@arcserve.com");
		  this.submsp1accountEmailForPrepare1 = spogServer.ReturnRandom("d_jing_spogqa_submsp1account1_org_prepare@arcserve.com");
		  this.rootmspaccountEmailForPrepare = spogServer.ReturnRandom("d_jing_spogqa_rootmspaccount_org_prepare@arcserve.com");
		  this.rootmspaccountEmailForPrepare1 = spogServer.ReturnRandom("d_jing_spogqa_rootmspaccount1_org_prepare@arcserve.com");
		  this.mspOrgEmailForPrepare = spogServer.ReturnRandom("d_jing_spogqa_msp_org_prepar@arcserve.come");
		  this.mspOrgEmailForPrepare1 = spogServer.ReturnRandom("d_jing_spogqa_msp_org_prepare@arcserve.com");
		  this.directOrgEmailForPrepare = spogServer.ReturnRandom("d_jing_spogqa_direct_org_prepare@arcserve.com");
		  this.directOrgEmailForPrepare1 = spogServer.ReturnRandom("d_jing_spogqa_direct_org_prepare@arcserve.com");
		  this.rootmspOrgEmailForPrepare = spogServer.ReturnRandom("d_jing_spogqa_rootmsp_org_prepar@arcserve.come");
		  this.submspOrgEmailForPrepare= spogServer.ReturnRandom("d_jing_spogqa_submsp_org_prepar@arcserve.come");
		  this.submsp1OrgEmailForPrepare= spogServer.ReturnRandom("d_jing_spogqa_submsp1_org_prepar@arcserve.come");
		  spogServer.userLogin(this.csrAdmin, this.csrPwd);
		  String organization_id=spogServer.GetLoggedinUserOrganizationID();
		  this.csrOrgId=spogServer.GetLoggedinUserOrganizationID();
		  this.mspOrgId = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(mspOrgNameForPrepare)+org_prefix, SpogConstants.MSP_ORG, mspOrgEmailForPrepare, OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare);
		  this.mspOrgId1 = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(mspOrgNameForPrepare)+org_prefix, SpogConstants.MSP_ORG, mspOrgEmailForPrepare1, OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare);
		  this.rootmspOrgId = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(mspOrgNameForPrepare)+org_prefix, SpogConstants.MSP_ORG, rootmspOrgEmailForPrepare, OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare);
		  org4SPOGServer.setToken(spogServer.getJWTToken());
		  org4SPOGServer.convertToRootMSP(rootmspOrgId);
		  this.rootmspaccountOrgId= spogServer.createAccountWithCheck(this.rootmspOrgId, spogServer.ReturnRandom("spogqa_account")+org_prefix,"");
		  this.rootmspaccountOrgId1= spogServer.createAccountWithCheck(this.rootmspOrgId, spogServer.ReturnRandom("spogqa_account")+org_prefix,"");
		  
		  this.submspOrgId=org4SPOGServer.createSubMSPAccountincc(spogServer.ReturnRandom(mspOrgNameForPrepare)+org_prefix, rootmspOrgId, OrgFistNameForPrepare, OrgLastNameForPrepare, submspOrgEmailForPrepare, "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
		  this.submsp1OrgId=org4SPOGServer.createSubMSPAccountincc(spogServer.ReturnRandom(mspOrgNameForPrepare)+org_prefix, rootmspOrgId, OrgFistNameForPrepare, OrgLastNameForPrepare, submsp1OrgEmailForPrepare, "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
		  this.submspOrgEmailForPrepare= spogServer.ReturnRandom("d_jing_spogqa_submsp_org_prepar@arcserve.come");
		  this.submsp1OrgEmailForPrepare= spogServer.ReturnRandom("d_jing_spogqa_submsp1_org_prepar@arcserve.come");
		  spogServer.createUserAndCheck(submspOrgEmailForPrepare, OrgPwdForPrepare,  OrgFistNameForPrepare, OrgLastNameForPrepare, "msp_admin", submspOrgId, test);
		  spogServer.createUserAndCheck(submsp1OrgEmailForPrepare, OrgPwdForPrepare,  OrgFistNameForPrepare, OrgLastNameForPrepare, "msp_admin", submsp1OrgId, test);
		  this.submspaccountOrgId= spogServer.createAccountWithCheck(this.submspOrgId, spogServer.ReturnRandom("spogqa_account")+org_prefix,"");
		  this.submspaccountOrgId1= spogServer.createAccountWithCheck(this.submspOrgId, spogServer.ReturnRandom("spogqa_account")+org_prefix,"");
		  this.submsp1accountOrgId= spogServer.createAccountWithCheck(this.submsp1OrgId, spogServer.ReturnRandom("spogqa_account")+org_prefix,"");
		  this.submsp1accountOrgId1= spogServer.createAccountWithCheck(this.submsp1OrgId, spogServer.ReturnRandom("spogqa_account")+org_prefix,"");
		  
		  this.accountOrgId= spogServer.createAccountWithCheck(this.mspOrgId, spogServer.ReturnRandom("spogqa_account")+org_prefix,"");
		  this.accountOrgId1= spogServer.createAccountWithCheck(this.mspOrgId, spogServer.ReturnRandom("spogqa_account")+org_prefix,"");
		  this.directOrgId = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(directOrgNameForPrepare)+org_prefix, SpogConstants.DIRECT_ORG, directOrgEmailForPrepare, OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare);
		  this.directOrgId1 = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(directOrgNameForPrepare)+org_prefix, SpogConstants.DIRECT_ORG, directOrgEmailForPrepare1, OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare);
		  spogServer.createUserAndCheck(this.accountEmailForPrepare, OrgPwdForPrepare, "dd", "gg", SpogConstants.DIRECT_ADMIN, accountOrgId,test);
		  spogServer.createUserAndCheck(this.accountEmailForPrepare1, OrgPwdForPrepare, "dd", "gg", SpogConstants.DIRECT_ADMIN, accountOrgId1,test);
		  spogServer.createUserAndCheck(this.submspaccountEmailForPrepare, OrgPwdForPrepare, "dd", "gg", SpogConstants.DIRECT_ADMIN, submspaccountOrgId,test);
		  spogServer.createUserAndCheck(this.submspaccountEmailForPrepare1, OrgPwdForPrepare, "dd", "gg", SpogConstants.DIRECT_ADMIN, submspaccountOrgId1,test);
		  spogServer.createUserAndCheck(this.rootmspaccountEmailForPrepare, OrgPwdForPrepare, "dd", "gg", SpogConstants.DIRECT_ADMIN, rootmspaccountOrgId,test);
		  spogServer.createUserAndCheck(this.rootmspaccountEmailForPrepare1, OrgPwdForPrepare, "dd", "gg", SpogConstants.DIRECT_ADMIN, rootmspaccountOrgId1,test);
		  spogServer.createUserAndCheck(this.submsp1accountEmailForPrepare, OrgPwdForPrepare, "dd", "gg", SpogConstants.DIRECT_ADMIN, submsp1accountOrgId,test);
		  spogServer.createUserAndCheck(this.submsp1accountEmailForPrepare1, OrgPwdForPrepare, "dd", "gg", SpogConstants.DIRECT_ADMIN, submsp1accountOrgId1,test);
		  
		  spogServer.userLogin(this.accountEmailForPrepare, OrgPwdForPrepare);
		  this.account_user_id1=spogServer.GetLoggedinUser_UserID();
		  spogServer.userLogin(this.accountEmailForPrepare1, OrgPwdForPrepare);
		  this.account_user_id2=spogServer.GetLoggedinUser_UserID();
		  spogServer.userLogin(this.csrAdmin, this.csrPwd);
		  mspAccountAdminEmailForPrepare = spogServer.ReturnRandom("d_jing_spogqa_msp_org_prepar_jing@arcserve.come");
		  rootmspAccountAdminEmailForPrepare = spogServer.ReturnRandom("d_jing_spogqa_msp_org_prepar_jing@arcserve.come");
		  submspAccountAdminEmailForPrepare = spogServer.ReturnRandom("d_jing_spogqa_msp_org_prepar_jing@arcserve.come");
		  submsp1AccountAdminEmailForPrepare = spogServer.ReturnRandom("d_jing_spogqa_msp_org_prepar_jing@arcserve.come");
		  String csrToken= spogServer.getJWTToken();
		  mspAccountAdminUserIds[0]=spogServer.createUserAndCheck(mspAccountAdminEmailForPrepare, OrgPwdForPrepare, "ff", "ff", "msp_account_admin", mspOrgId, test);
		  rootmspAccountAdminUserIds[0]=spogServer.createUserAndCheck(rootmspAccountAdminEmailForPrepare, OrgPwdForPrepare, "ff", "ff", "msp_account_admin", rootmspOrgId, test);
		  submspAccountAdminUserIds[0]=spogServer.createUserAndCheck(submspAccountAdminEmailForPrepare, OrgPwdForPrepare, "ff", "ff", "msp_account_admin", submspOrgId, test);
		  submsp1AccountAdminUserIds[0]=spogServer.createUserAndCheck(submsp1AccountAdminEmailForPrepare, OrgPwdForPrepare, "ff", "ff", "msp_account_admin", submsp1OrgId, test);
		  spogServer.userLogin(mspAccountAdminEmailForPrepare, OrgPwdForPrepare);
		  this.msp_account_admin_token = spogServer.getJWTToken();
		  
		  Response response = userSpogServer.assignMspAccountAdmins(mspOrgId, accountOrgId1, mspAccountAdminUserIds, csrToken);
		  userSpogServer.assignMspAccountAdminsWithCheck(response, mspAccountAdminUserIds, csrToken);
		  response = userSpogServer.assignMspAccountAdmins(rootmspOrgId, rootmspaccountOrgId1, rootmspAccountAdminUserIds, csrToken);
		  userSpogServer.assignMspAccountAdminsWithCheck(response, rootmspAccountAdminUserIds, csrToken);
		  response = userSpogServer.assignMspAccountAdmins(submspOrgId, submspaccountOrgId1, submspAccountAdminUserIds, csrToken);
		  userSpogServer.assignMspAccountAdminsWithCheck(response, submspAccountAdminUserIds, csrToken);
		  response = userSpogServer.assignMspAccountAdmins(submsp1OrgId, submsp1accountOrgId1, submsp1AccountAdminUserIds, csrToken);
		  userSpogServer.assignMspAccountAdminsWithCheck(response, submsp1AccountAdminUserIds, csrToken);
		  spogServer.userLogin(this.mspAccountAdminEmailForPrepare, OrgPwdForPrepare);
	  }
	  
	  @DataProvider(name = "loginUserToCreateDifferentSourceType")
	  public final Object[][] getLoginUserToCreateDifferentSourceType() {
			return new Object[][] { 
				{"csr",null},{"csr","failed"},{"csr","instant_vm"},{"csr","machine"},{"csr","office_365"},{"csr","shared_folder"},{"csr","virtual_standby"},{"csr","agentless_vm"},
				{"csr_read_only",null},{"csr_read_only","failed"},{"csr_read_only","instant_vm"},{"csr_read_only","machine"},{"csr_read_only","office_365"},{"csr_read_only","shared_folder"},{"csr_read_only","virtual_standby"},{"csr_read_only","agentless_vm"},
				{"msp",null},{"msp","failed"},{"msp","instant_vm"},{"msp","machine"},{"msp","office_365"},{"msp","shared_folder"},{"msp","virtual_standby"},{"msp","agentless_vm"},
				{"direct",null},{"direct","failed"},{"direct","instant_vm"},{"direct","machine"},{"direct","office_365"},{"direct","shared_folder"},{"direct","virtual_standby"},{"direct","agentless_vm"},
				{"account",null},{"account","failed"},{"account","instant_vm"},{"account","machine"},{"account","office_365"},{"account","shared_folder"},{"account","virtual_standby"},{"account","agentless_vm"},
				{"msp_account",null},{"msp_account","failed"},{"msp_account","instant_vm"},{"msp_account","machine"},{"msp_account","office_365"},{"msp_account","shared_folder"},{"msp_account","virtual_standby"},{"msp_account","agentless_vm"},
				
				{"rootmsp",null},{"rootmsp","failed"},{"rootmsp","instant_vm"},{"rootmsp","machine"},{"rootmsp","office_365"},{"rootmsp","shared_folder"},{"rootmsp","virtual_standby"},{"rootmsp","agentless_vm"},
				{"submsp",null},{"submsp","failed"},{"submsp","instant_vm"},{"submsp","machine"},{"submsp","office_365"},{"submsp","shared_folder"},{"submsp","virtual_standby"},{"submsp","agentless_vm"},
				{"rootmspaccount",null},{"rootmspaccount","failed"},{"rootmspaccount","instant_vm"},{"rootmspaccount","machine"},{"rootmspaccount","office_365"},{"rootmspaccount","shared_folder"},{"rootmspaccount","virtual_standby"},{"rootmspaccount","agentless_vm"},
				{"rootmsp_account",null},{"rootmsp_account","failed"},{"rootmsp_account","instant_vm"},{"rootmsp_account","machine"},{"rootmsp_account","office_365"},{"rootmsp_account","shared_folder"},{"rootmsp_account","virtual_standby"},{"rootmsp_account","agentless_vm"},

				{"submspaccount",null},{"submspaccount","failed"},{"submspaccount","instant_vm"},{"submspaccount","machine"},{"submspaccount","office_365"},{"submspaccount","shared_folder"},{"submspaccount","virtual_standby"},{"submspaccount","agentless_vm"},
				{"submsp_account",null},{"submsp_account","failed"},{"submsp_account","instant_vm"},{"submsp_account","machine"},{"submsp_account","office_365"},{"submsp_account","shared_folder"},{"submsp_account","virtual_standby"},{"submsp_account","agentless_vm"}
				
			};
	  }
	  @Test(dataProvider = "loginUserToCreateDifferentSourceType")
	  public void updateFilterWithDifferentSourceType(String loginUserType,String source_type){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  String user_id;
		  String no_permission_id,permission_id,no_permission_errorCode;
		  String[] no_permission_users_id=null;
		  String[] no_permission_users_errorCode=null;
		  String[] permission_users_id=null;
		  test.log(LogStatus.INFO,"Login as csr admin");	
		  if (loginUserType.equalsIgnoreCase("csr")){
			  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
			  no_permission_id=mspOrgEmailForPrepare+","+directOrgEmailForPrepare+","+accountEmailForPrepare;
			  no_permission_errorCode=ErrorCode.RESOURCE_PERMISSION+","+ErrorCode.RESOURCE_PERMISSION+","+ErrorCode.RESOURCE_PERMISSION;
			  no_permission_users_id=no_permission_id.split(",");
			  no_permission_users_errorCode=no_permission_errorCode.split(",");		  
		  }else if (loginUserType.equalsIgnoreCase("csr_read_only")){
			  spogServer.userLogin(this.csr_readonly, "Caworld_2017",test);
			  no_permission_id=mspOrgEmailForPrepare+","+directOrgEmailForPrepare+","+accountEmailForPrepare;
			  no_permission_errorCode=ErrorCode.RESOURCE_PERMISSION+","+ErrorCode.RESOURCE_PERMISSION+","+ErrorCode.RESOURCE_PERMISSION;
			  no_permission_users_id=no_permission_id.split(",");
			  no_permission_users_errorCode=no_permission_errorCode.split(",");		  
		  }else if(loginUserType.equalsIgnoreCase("direct")){
			  spogServer.userLogin(this.directOrgEmailForPrepare, OrgPwdForPrepare,test);
			  no_permission_id=mspOrgEmailForPrepare+","+directOrgEmailForPrepare1+","+accountEmailForPrepare;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.csrAdmin;
			  permission_users_id=permission_id.split(",");	
		  }else if(loginUserType.equalsIgnoreCase("msp")){
			  spogServer.userLogin(this.mspOrgEmailForPrepare, OrgPwdForPrepare,test);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+accountEmailForPrepare;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.csrAdmin;
			  permission_users_id=permission_id.split(",");
		  }else if(loginUserType.equalsIgnoreCase("account")){
			  spogServer.userLogin(this.accountEmailForPrepare, OrgPwdForPrepare,test);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+directOrgEmailForPrepare1+","+accountEmailForPrepare1;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.csrAdmin+","+mspOrgEmailForPrepare;
			  permission_users_id=permission_id.split(",");
		  }else if(loginUserType.equalsIgnoreCase("msp_account")){
			  spogServer.userLogin(this.mspAccountAdminEmailForPrepare, OrgPwdForPrepare);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+directOrgEmailForPrepare1+","+accountEmailForPrepare1;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.csrAdmin+","+mspOrgEmailForPrepare;
			  permission_users_id=permission_id.split(",");
		  }else if(loginUserType.equalsIgnoreCase("rootmsp")){
			  spogServer.userLogin(this.rootmspOrgEmailForPrepare, OrgPwdForPrepare,test);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+accountEmailForPrepare+","+submspOrgEmailForPrepare+","+submspaccountEmailForPrepare+","+rootmspaccountEmailForPrepare;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.csrAdmin;
			  permission_users_id=permission_id.split(",");
		  }else if(loginUserType.equalsIgnoreCase("submsp")){
			  spogServer.userLogin(this.submspOrgEmailForPrepare, OrgPwdForPrepare,test);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+accountEmailForPrepare+","+rootmspOrgEmailForPrepare+","+submspaccountEmailForPrepare+","+rootmspaccountEmailForPrepare;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.csrAdmin;
			  permission_users_id=permission_id.split(",");
		  }else if(loginUserType.equalsIgnoreCase("rootmspaccount")){
			  spogServer.userLogin(this.rootmspaccountEmailForPrepare, OrgPwdForPrepare,test);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+directOrgEmailForPrepare1+","+accountEmailForPrepare1+","+rootmspOrgEmailForPrepare+","+submspaccountEmailForPrepare+","+submspOrgEmailForPrepare;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.csrAdmin+","+rootmspOrgEmailForPrepare;
			  permission_users_id=permission_id.split(",");
		  }else if(loginUserType.equalsIgnoreCase("rootmsp_account")){
			  spogServer.userLogin(this.rootmspAccountAdminEmailForPrepare, OrgPwdForPrepare);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+directOrgEmailForPrepare1+","+accountEmailForPrepare1+","+rootmspOrgEmailForPrepare+","+submspaccountEmailForPrepare+","+submspOrgEmailForPrepare;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.csrAdmin+","+rootmspOrgEmailForPrepare;
			  permission_users_id=permission_id.split(",");
		  }else if(loginUserType.equalsIgnoreCase("submspaccount")){
			  spogServer.userLogin(this.submspaccountEmailForPrepare, OrgPwdForPrepare,test);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+directOrgEmailForPrepare1+","+accountEmailForPrepare1+","+rootmspOrgEmailForPrepare+","+rootmspaccountEmailForPrepare+","+submspOrgEmailForPrepare;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.csrAdmin+","+submspOrgEmailForPrepare;
			  permission_users_id=permission_id.split(",");
		  }else if(loginUserType.equalsIgnoreCase("submsp_account")){
			  spogServer.userLogin(this.submspAccountAdminEmailForPrepare, OrgPwdForPrepare);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+directOrgEmailForPrepare1+","+accountEmailForPrepare1+","+rootmspOrgEmailForPrepare+","+rootmspaccountEmailForPrepare+","+submspOrgEmailForPrepare;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.csrAdmin+","+submspOrgEmailForPrepare;
			  permission_users_id=permission_id.split(",");
		  }
		  user_id=spogServer.GetLoggedinUser_UserID();
		  test.log(LogStatus.INFO,"Login as "+loginUserType+" admin");	
		  String pre_filter_name=spogServer.returnRandomUUID();
		  String tem_filter_name = spogServer.returnRandomUUID();
		  if(loginUserType.equalsIgnoreCase("msp_account")){
			  String true_filter_id1=spogServer.createFilterwithCheck(this.account_user_id2, pre_filter_name, "none", "none","none",  "none", "none", "none", "none","instant_vm","true", test);
		  }
		  String true_filter_id=spogServer.createFilterwithCheck(user_id, pre_filter_name, "none", "none","none",  "none", "none", "none", "none","instant_vm","true", test);
		  String false_filter_id=spogServer.createFilterwithCheck(user_id, tem_filter_name, "none", "none","none",  "none", "none", "none", "none","instant_vm","false", test);
		  if(source_type==null ){
			  spogServer.updateFilterwithCheck(user_id, true_filter_id,pre_filter_name, "none", "none","none",  "none", "none", "none", "none",source_type,"instant_vm","false",test);
			  test.log(LogStatus.INFO,loginUserType+ " admin update filter with source type is null"); 
		  }else if(source_type.equalsIgnoreCase("failed")) {
			  spogServer.updateFilterFailedWithExpectedStatusCode(user_id, true_filter_id,pre_filter_name, "none", "none","none",  "none", "none", "none", "none",source_type,"true",  SpogConstants.REQUIRED_INFO_NOT_EXIST,ErrorCode.NOT_VALID_ENUM,test);
			  test.log(LogStatus.INFO,loginUserType+ " admin update filter with source type is "+source_type.toString()); 
		  }else{
			  spogServer.updateFilterwithCheck(user_id, true_filter_id,pre_filter_name, "none", "none","none",  "none", "none", "none", "none",source_type,source_type,"true",  test);
			  test.log(LogStatus.INFO,loginUserType+ " admin update filter with source type is "+source_type.toString()); 
		  }
		  if(loginUserType.equalsIgnoreCase("csr")){
			  for(int i=0;i<no_permission_users_id.length;i++){
				  spogServer.userLogin(no_permission_users_id[i], OrgPwdForPrepare,test);
				  spogServer.updateFilterFailedWithExpectedStatusCode(user_id, false_filter_id,tem_filter_name, "none", "none", "none", "none", "none", "none", "none", "machine","true",SpogConstants.INSUFFICIENT_PERMISSIONS,no_permission_users_errorCode[i], test);
				  test.log(LogStatus.INFO,no_permission_users_id[i]+ " can't update others' filter");
			  }
		  }		  	
		  if(permission_users_id!=null){
			  for(int i=0;i<permission_users_id.length;i++){
				  spogServer.userLogin(permission_users_id[i], OrgPwdForPrepare,test);
				  spogServer.updateFilterwithCheck(user_id, false_filter_id,tem_filter_name, "none", "none", "none", "none", "none", "none", "none", "machine","machine","true", test);
				  test.log(LogStatus.INFO,permission_users_id[i]+" can update others' filter"); 
			  }	
		  }
	  }
	  
	  @DataProvider(name = "loginUserToCreateDifferentDefaultFlag")
	  public final Object[][] getLoginUserToCreateDifferentDefaultFlag() {
			return new Object[][] { {"csr",""},{"csr",null},{"csr","true"},{"csr","false"},{"csr","none"},
				{"csr_read_only",""},{"csr_read_only",null},{"csr_read_only","true"},{"csr_read_only","false"},{"csr_read_only","none"},
				 {"msp",""},{"msp",null},{"msp","true"},{"msp","false"},{"msp","none"},
				 {"direct",""},{"direct",null},{"direct","true"},{"direct","false"},{"direct","none"},
				 {"account",""},{"account",null},{"account","true"},{"account","false"},{"account","none"}
				};
	  }
	  //@Test(dataProvider = "loginUserToCreateDifferentDefaultFlag")
	  public void updateFilterWithDifferentDefaultFlag(String loginUserType,String default_flag){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  String user_id;
		  String no_permission_id,permission_id;
		  String[] no_permission_users_id=null;
		  String[] permission_users_id=null;
		  test.log(LogStatus.INFO,"Login as csr admin");	
		  if (loginUserType.equalsIgnoreCase("csr")){
			  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
			  no_permission_id=mspOrgEmailForPrepare+","+directOrgEmailForPrepare+","+accountEmailForPrepare;
			  no_permission_users_id=no_permission_id.split(",");			  
		  }else if (loginUserType.equalsIgnoreCase("csr_read_only")){
			  spogServer.userLogin(this.csr_readonly, OrgPwdForPrepare,test);
			  no_permission_id=mspOrgEmailForPrepare+","+directOrgEmailForPrepare+","+accountEmailForPrepare;
			  no_permission_users_id=no_permission_id.split(",");			  
		  }else if(loginUserType.equalsIgnoreCase("direct")){
			  spogServer.userLogin(this.directOrgEmailForPrepare, OrgPwdForPrepare,test);
			  no_permission_id=mspOrgEmailForPrepare+","+directOrgEmailForPrepare1+","+accountEmailForPrepare;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.csrAdmin;
			  permission_users_id=permission_id.split(",");	
		  }else if(loginUserType.equalsIgnoreCase("msp")){
			  spogServer.userLogin(this.mspOrgEmailForPrepare, OrgPwdForPrepare,test);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+accountEmailForPrepare;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.csrAdmin;
			  permission_users_id=permission_id.split(",");
		  }else if(loginUserType.equalsIgnoreCase("account")){
			  spogServer.userLogin(this.accountEmailForPrepare, OrgPwdForPrepare,test);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+directOrgEmailForPrepare1+","+accountEmailForPrepare1;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.csrAdmin+","+mspOrgEmailForPrepare;
			  permission_users_id=permission_id.split(",");
		  }
		  user_id=spogServer.GetLoggedinUser_UserID();
		  test.log(LogStatus.INFO,"Login as "+loginUserType+" admin");	
		  String pre_filter_name=spogServer.returnRandomUUID();
		  String tem_filter_name = spogServer.returnRandomUUID();
		  String true_filter_id=spogServer.createFilterwithCheck(user_id, pre_filter_name, "none", "none","none",  "none", "none", "none", "none","instant_vm","true", test);
		  String false_filter_id=spogServer.createFilterwithCheck(user_id, tem_filter_name, "none", "none","none",  "none", "none", "none", "none","instant_vm","false", test);
		  if(default_flag==null || default_flag=="" || default_flag=="none" || default_flag=="false"){
			  spogServer.updateFilterwithCheck(user_id, true_filter_id,pre_filter_name, "none", "none","none",  "none", "none", "none", "none","instant_vm","instant_vm",default_flag,test);
			  test.log(LogStatus.INFO,loginUserType+ " admin update filter with default_flag is null or emtpy string"); 
		  }else if(default_flag=="true"){
			  String pr_filter_name = spogServer.returnRandomUUID();
			  String pre_filter_id=spogServer.createFilterwithCheck(user_id, pr_filter_name, "none","none","none",  "none", "none", null, "none", "machine","true",test);
			  test.log(LogStatus.INFO,loginUserType+ " admin create filter with default flag is true");
			  String current_filter_name = spogServer.returnRandomUUID();
			  String current_filter_id=spogServer.createFilterwithCheck(user_id, current_filter_name, "none","none","none",  "none", "none", null, "none", "machine","true",test);
			  test.log(LogStatus.INFO,loginUserType+ " admin create filter with default flag is true");
			  Response response = spogServer.getFilterByID(user_id, pre_filter_id);
			  spogServer.compareFilter("none","none","none",  "none", "none", null, "none", "machine", "false", response);
			  test.log(LogStatus.INFO,loginUserType+ " admin create default filter will not replace the previous default filter");
			  response = spogServer.getFilterByID(user_id, current_filter_id);
			  spogServer.compareFilter("none","none","none",  "none", "none", null, "none", "machine", "true", response);
			  test.log(LogStatus.INFO,loginUserType+ " admin create default filter will not replace the previous default filter");			  
		  }		  
		  
		  if(permission_users_id!=null){
			  for(int i=0;i<permission_users_id.length;i++){
				  spogServer.userLogin(permission_users_id[i], OrgPwdForPrepare,test);
				  spogServer.updateFilterwithCheck(user_id, false_filter_id,tem_filter_name, "none", "none", "none", "none", "none", "none", "none", "instant_vm","instant_vm","true",test);
				  test.log(LogStatus.INFO,permission_users_id[i]+" can update others' filter"); 
			  }	
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
		  // ending test
		  //endTest(logger) : It ends the current test and prepares to create HTML report
		  rep.endTest(test);	
	  }
	  
	  
}
