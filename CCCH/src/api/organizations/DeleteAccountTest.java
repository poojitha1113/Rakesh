package api.organizations;

import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class DeleteAccountTest extends base.prepare.PrepareOrgInfo{
	@Parameters({ "pmfKey"})
	  public DeleteAccountTest(String pmfKey) {
			super(pmfKey);
			// TODO Auto-generated constructor stub
		}
	 private SPOGServer spogServer;
	  private String directOrgId;
	  private String mspOrgId;
	  private String mspOrgId1;
	  private String accountOrgId,accountOrgIdForNotLogin;
	  private String csrAdmin;
	  private String csrPwd;
	  
	  //this is creating msp org or direct org for preparation
	  private String mspOrgEmailForPrepare,mspOrgEmailForPrepare1;
	  private String directOrgNameForPrepare="d_jing_spogqa_direct_org_prepare@arcserve.com";
	  private String directOrgEmailForPrepare;
	  private String OrgFistNameForPrepare="jing";
	  private String OrgLastNameForPrepare="org_prepare";
	  private String OrgPwdForPrepare="Welcome*02";
	  private String csrOrgId;
	  
	  private String  org_prefix=this.getClass().getSimpleName();
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
	  
	  private UserSpogServer userSpogServer;
	  private String[] mspAccountAdminUserIds=new String[1];
	  private String mspOrgNameForPrepare="d_jing_spogqa_msp_org_prepare_jing";
	  private String accountEmailForPrepare ="d_jing_spogqa_account_org_prepare_jing_1@arcserve.com";
	  private String mspAccountAdminEmailForPrepare="d_jing_spogqa_msp_account_org_prepar_jing@arcserve.come";
	  private String accountEmailForPrepare1="d_jing_spogqa_account_org_prepare_jing_2@arcserve.com";
	  private String accountOrgId1,accountOrgId2,mspOrg1accountOrgId,msp_account_admin_token;
	  //end 
	  @BeforeClass
	  @Parameters({ "baseURI", "port","csrAdminUserName", "csrAdminPassword","logFolder","runningMachine","buildVersion"})
	  public void beforeClass(String baseURI, String port, String csrAdminUserName, String csrAdminPassword, String logFolder, String runningMachine, String buildVersion) {
		  //this is for update portal
		  this.BQName = this.getClass().getSimpleName();
		  String author = "Jing.Shan";
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
		  userSpogServer = new UserSpogServer(baseURI, port);
		  this.csrAdmin = csrAdminUserName;
		  this.csrPwd = csrAdminPassword;
		  this.mspOrgEmailForPrepare = spogServer.ReturnRandom("d_jing_spogqa_msp_org_prepare@arcserve.com");
		  this.mspOrgEmailForPrepare1 = spogServer.ReturnRandom("d_jing_spogqa_msp_org_prepare@arcserve.com");
		  this.directOrgEmailForPrepare = spogServer.ReturnRandom("d_jing_spogqa_direct_org_prepare@arcserve.com");
		  spogServer.userLogin(this.csrAdmin, this.csrPwd);
		  this.csrOrgId=spogServer.GetLoggedinUserOrganizationID();
		  this.mspOrgId = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(mspOrgNameForPrepare)+org_prefix, SpogConstants.MSP_ORG, mspOrgEmailForPrepare, OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare);
		  this.mspOrgId1 = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(mspOrgNameForPrepare)+org_prefix, SpogConstants.MSP_ORG, mspOrgEmailForPrepare1, OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare);
		  this.accountOrgId= spogServer.createAccountWithCheck(this.mspOrgId, spogServer.ReturnRandom("spogqa_account")+org_prefix,"");
		  this.mspOrg1accountOrgId= spogServer.createAccountWithCheck(this.mspOrgId1, spogServer.ReturnRandom("spogqa_account")+org_prefix,"");
		  
		  this.accountOrgIdForNotLogin= spogServer.createAccountWithCheck(this.mspOrgId, spogServer.ReturnRandom("spogqa_account")+org_prefix,"");
		  this.directOrgId = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(directOrgNameForPrepare)+org_prefix, SpogConstants.DIRECT_ORG, directOrgEmailForPrepare, OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare);
		  
		  this.accountOrgId1= spogServer.createAccountWithCheck(this.mspOrgId, "spogqa_account_jing_1"+org_prefix,"");
		  this.accountOrgId2= spogServer.createAccountWithCheck(this.mspOrgId, "spogqa_account_jing_2"+org_prefix,"");	  
		  this.accountEmailForPrepare=spogServer.ReturnRandom(accountEmailForPrepare);
		  this.accountEmailForPrepare1=spogServer.ReturnRandom(accountEmailForPrepare1);
		  spogServer.createUserAndCheck(this.accountEmailForPrepare, OrgPwdForPrepare, "dd", "gg", SpogConstants.DIRECT_ADMIN, accountOrgId1,test);
		  spogServer.createUserAndCheck(this.accountEmailForPrepare1, OrgPwdForPrepare, "dd", "gg", SpogConstants.DIRECT_ADMIN, accountOrgId2,test);
		  mspAccountAdminEmailForPrepare = spogServer.ReturnRandom("d_jing_spogqa_msp_org_prepar_jing@arcserve.come");
		  String csrToken= spogServer.getJWTToken();
		  mspAccountAdminUserIds[0]=spogServer.createUserAndCheck(mspAccountAdminEmailForPrepare, OrgPwdForPrepare, "ff", "ff", "msp_account_admin", mspOrgId, test);
		  spogServer.userLogin(mspAccountAdminEmailForPrepare, OrgPwdForPrepare);
		  this.msp_account_admin_token = spogServer.getJWTToken();
		  
		  Response response = userSpogServer.assignMspAccountAdmins(mspOrgId, accountOrgId1, mspAccountAdminUserIds, csrToken);
		  userSpogServer.assignMspAccountAdminsWithCheck(response, mspAccountAdminUserIds, csrToken);
		  spogServer.userLogin(this.mspAccountAdminEmailForPrepare, OrgPwdForPrepare);
		  prepare(baseURI, port, logFolder, csrAdminUserName, csrAdminPassword, this.getClass().getSimpleName() );
		  //spogServer.createUserAndCheck(accountEmailForPrepare, OrgPwdForPrepare, "dd", "gg", SpogConstants.DIRECT_ADMIN, mspOrgId,test);
	  }
	  
	  @DataProvider(name = "permissionTest")
	  public final Object[][] permissionTest() {
			return new Object[][] {
									{"msp"},{"direct"},{"account"},
									{"submspaccount"},{"submspaccountadmin"},{"submsp"},
									{"rootmspaccount"},{"rootmspaccountadmin"},{"rootmsp"},
									
      };
	  }
	  @Test(dataProvider = "permissionTest")
	  public void permissionTest(String usertype){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  String errorCode="00100101";
		  if (usertype.equalsIgnoreCase("direct")) {
			  spogServer.userLogin(this.directOrgEmailForPrepare,this.OrgPwdForPrepare,test);
			  spogServer.deleteMSPAccountWithExpectedStatusCode(this.mspOrgId, accountOrgId,SpogConstants.INSUFFICIENT_PERMISSIONS, errorCode,test);
			  spogServer.deleteMSPAccountWithExpectedStatusCode(this.root_msp_org_id,this.root_msp_direct_org_id,SpogConstants.INSUFFICIENT_PERMISSIONS, errorCode,test);
			  spogServer.deleteMSPAccountWithExpectedStatusCode(this.sub_msp1_org_id,this.sub_msp1_account1_id,SpogConstants.INSUFFICIENT_PERMISSIONS, errorCode,test);
			  
			  
//			  spogServer.updateAccountFailedWithExpectedStatusCode(this.root_msp_org_id,this.root_msp_direct_org_id,spogServer.ReturnRandom("spogqa_account")+org_prefix,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
//			  spogServer.updateAccountFailedWithExpectedStatusCode(this.sub_msp1_org_id, this.sub_msp1_account1_id,spogServer.ReturnRandom("spogqa_account")+org_prefix,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
//			  
          }else if(usertype.equalsIgnoreCase("msp")) {
			  spogServer.userLogin(this.mspOrgEmailForPrepare, OrgPwdForPrepare,test);
			  String accountOrgIdTmp= spogServer.createAccountWithCheck(this.mspOrgId, spogServer.ReturnRandom("spogqa_account_jing_1")+org_prefix,"");
			  spogServer.deleteMSPAccountWithCheck(this.mspOrgId, accountOrgIdTmp);
			  spogServer.deleteMSPAccountWithExpectedStatusCode(this.mspOrgId1, this.mspOrg1accountOrgId,SpogConstants.INSUFFICIENT_PERMISSIONS, "00100101",test);
			  spogServer.deleteMSPAccountWithExpectedStatusCode(this.root_msp_org_id,this.root_msp_direct_org_id,SpogConstants.INSUFFICIENT_PERMISSIONS, errorCode,test);
			  spogServer.deleteMSPAccountWithExpectedStatusCode(this.sub_msp1_org_id,this.sub_msp1_account1_id,SpogConstants.INSUFFICIENT_PERMISSIONS, errorCode,test);
			  
		  }else if(usertype.equalsIgnoreCase("account")) {
			  spogServer.userLogin(this.accountEmailForPrepare, OrgPwdForPrepare,test);
			  spogServer.deleteMSPAccountWithExpectedStatusCode(this.mspOrgId1, this.mspOrg1accountOrgId,SpogConstants.INSUFFICIENT_PERMISSIONS, errorCode,test);
			  spogServer.deleteMSPAccountWithExpectedStatusCode(this.root_msp_org_id,this.root_msp_direct_org_id,SpogConstants.INSUFFICIENT_PERMISSIONS, errorCode,test);
			  spogServer.deleteMSPAccountWithExpectedStatusCode(this.sub_msp1_org_id,this.sub_msp1_account1_id,SpogConstants.INSUFFICIENT_PERMISSIONS, errorCode,test);
			  
		  }else if(usertype.equalsIgnoreCase("accountadmin")) {
			  spogServer.userLogin(this.mspAccountAdminEmailForPrepare, OrgPwdForPrepare,test);
			  spogServer.deleteMSPAccountWithExpectedStatusCode(this.mspOrgId1, this.mspOrg1accountOrgId,SpogConstants.INSUFFICIENT_PERMISSIONS, errorCode,test);
			  spogServer.deleteMSPAccountWithExpectedStatusCode(this.root_msp_org_id,this.root_msp_direct_org_id,SpogConstants.INSUFFICIENT_PERMISSIONS, errorCode,test);
			  spogServer.deleteMSPAccountWithExpectedStatusCode(this.sub_msp1_org_id,this.sub_msp1_account1_id,SpogConstants.INSUFFICIENT_PERMISSIONS, errorCode,test);
			  
		  }else if(usertype.equalsIgnoreCase("submspaccount")) {
			  spogServer.userLogin(this.final_sub_msp2_account1_user_email, OrgPwdForPrepare,test);
			  spogServer.deleteMSPAccountWithExpectedStatusCode(this.mspOrgId1, this.mspOrg1accountOrgId,SpogConstants.INSUFFICIENT_PERMISSIONS, errorCode,test);
			  spogServer.deleteMSPAccountWithExpectedStatusCode(this.root_msp_org_id,this.root_msp_direct_org_id,SpogConstants.INSUFFICIENT_PERMISSIONS, errorCode,test);
			  spogServer.deleteMSPAccountWithExpectedStatusCode(this.sub_msp1_org_id,this.sub_msp1_account1_id,SpogConstants.INSUFFICIENT_PERMISSIONS, errorCode,test);
			  
		  }else if(usertype.equalsIgnoreCase("submspaccountadmin")) {
			  spogServer.userLogin(this.final_sub_msp1_msp_account_user_name_email, OrgPwdForPrepare,test);
			  spogServer.deleteMSPAccountWithExpectedStatusCode(this.mspOrgId1, this.mspOrg1accountOrgId,SpogConstants.NOT_ALLOWED_ON_RESOURCE, "00300016",test);
			  spogServer.deleteMSPAccountWithExpectedStatusCode(this.root_msp_org_id,this.root_msp_direct_org_id,SpogConstants.NOT_ALLOWED_ON_RESOURCE, "00300016",test);
			  spogServer.deleteMSPAccountWithExpectedStatusCode(this.sub_msp1_org_id,this.sub_msp1_account1_id,SpogConstants.NOT_ALLOWED_ON_RESOURCE, "00300016",test);
			  
		  }else if(usertype.equalsIgnoreCase("submsp")) {
			  spogServer.userLogin(this.final_sub_msp2_user_name_email, OrgPwdForPrepare,test);
			  spogServer.deleteMSPAccountWithExpectedStatusCode(this.mspOrgId1, this.mspOrg1accountOrgId,SpogConstants.INSUFFICIENT_PERMISSIONS, errorCode,test);
			  spogServer.deleteMSPAccountWithExpectedStatusCode(this.root_msp_org_id,this.root_msp_direct_org_id,SpogConstants.INSUFFICIENT_PERMISSIONS, errorCode,test);
			  spogServer.deleteMSPAccountWithExpectedStatusCode(this.sub_msp1_org_id,this.sub_msp1_account1_id,SpogConstants.INSUFFICIENT_PERMISSIONS, errorCode,test);
			  spogServer.deleteMSPAccountWithCheck(this.sub_msp2_org_id, sub_msp2_account1_id);
			  
		  }else if(usertype.equalsIgnoreCase("rootmspaccount")) {
			  spogServer.userLogin(this.final_root_msp_direct_org_user_email, OrgPwdForPrepare,test);
			  spogServer.deleteMSPAccountWithExpectedStatusCode(this.mspOrgId1, this.mspOrg1accountOrgId,SpogConstants.INSUFFICIENT_PERMISSIONS, errorCode,test);
			  spogServer.deleteMSPAccountWithExpectedStatusCode(this.root_msp_org_id,this.root_msp_direct_org_id,SpogConstants.NOT_ALLOWED_ON_RESOURCE, "00300001",test);
			  spogServer.deleteMSPAccountWithExpectedStatusCode(this.sub_msp1_org_id,this.sub_msp1_account1_id,SpogConstants.INSUFFICIENT_PERMISSIONS, errorCode,test);
			  
		  }else if(usertype.equalsIgnoreCase("rootmspaccountadmin")) {
			  spogServer.userLogin(this.final_root_msp_account_admin_user_name_email, OrgPwdForPrepare,test);
			  spogServer.deleteMSPAccountWithExpectedStatusCode(this.mspOrgId1, this.mspOrg1accountOrgId,SpogConstants.NOT_ALLOWED_ON_RESOURCE, "00300016",test);
			  spogServer.deleteMSPAccountWithExpectedStatusCode(this.root_msp_org_id,this.root_msp_direct_org_id,SpogConstants.NOT_ALLOWED_ON_RESOURCE, "00300016",test);
			  spogServer.deleteMSPAccountWithExpectedStatusCode(this.sub_msp1_org_id,this.sub_msp1_account1_id,SpogConstants.NOT_ALLOWED_ON_RESOURCE, "00300016",test);
			  
		  }else if(usertype.equalsIgnoreCase("rootmsp")) {
			  spogServer.userLogin(this.final_root_msp_user_name_email, OrgPwdForPrepare,test);
			  spogServer.deleteMSPAccountWithExpectedStatusCode(this.mspOrgId1, this.mspOrg1accountOrgId,SpogConstants.INSUFFICIENT_PERMISSIONS, errorCode,test);
			  spogServer.deleteMSPAccountWithExpectedStatusCode(this.sub_msp1_org_id,this.sub_msp1_account1_id,SpogConstants.INSUFFICIENT_PERMISSIONS, errorCode,test);
			  spogServer.deleteMSPAccountWithCheck(this.root_msp_org_id, root_msp_direct_org_id);
			  
		  }
		  
	  }
	  
	  
	  @DataProvider(name = "loginUserToDeleteDifferentTypesAccount")
	  public final Object[][] getLoginUserToDeleteDifferentTypesAccount() {
			return new Object[][] { {"csr"},{"msp"},{"direct"},{"account"},{"msp_account_admin"}};
	  }
	  @Test(dataProvider = "loginUserToDeleteDifferentTypesAccount")
	  public void deleteAccountWithDifferentLoginUser(String loginUserType){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  String accountEmailForPrepare = spogServer.ReturnRandom("d_jing_spogqa_account_org_prepare@arcserve.com");
		  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
		  test.log(LogStatus.INFO,"Login as csr admin");
		  String accountOrgId= spogServer.createAccountWithCheck(this.mspOrgId, spogServer.ReturnRandom("spogqa_account")+org_prefix,"");
		  String accountOrgId1= spogServer.createAccountWithCheck(this.mspOrgId, spogServer.ReturnRandom("spogqa_account")+org_prefix,"");
		  test.log(LogStatus.INFO,"Create account successfully with org id:"+accountOrgId);
		  String errorCode=ErrorCode.NOT_FOUND_RESOURCE_ID;
		  if (loginUserType.equalsIgnoreCase("csr")){
			  errorCode=ErrorCode.RESOURCE_NOT_FOUND_OR_REMOVED;
			  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
			  test.log(LogStatus.INFO,"Login as csr admin");
			  spogServer.deleteMSPAccountWithCheck(this.mspOrgId, accountOrgId);
			  test.log(LogStatus.INFO,"Csr admin can delete account"); 
			  spogServer.deleteMSPAccountWithExpectedStatusCode(this.mspOrgId, accountOrgId,SpogConstants.RESOURCE_NOT_EXIST,errorCode, test);
			  test.log(LogStatus.INFO,"Csr admin can not delete the deleted account again"); 
		  }else if(loginUserType.equalsIgnoreCase("msp")){
			  errorCode=ErrorCode.RESOURCE_NOT_FOUND_OR_REMOVED;
			  spogServer.userLogin(this.mspOrgEmailForPrepare, OrgPwdForPrepare,test);
			  test.log(LogStatus.INFO,"Login as prepared msp admin");
			  spogServer.deleteMSPAccountWithCheck(this.mspOrgId, accountOrgId);
			  test.log(LogStatus.INFO,"Msp admin can delete account"); 
			  spogServer.deleteMSPAccountWithExpectedStatusCode(this.mspOrgId, accountOrgId,SpogConstants.RESOURCE_NOT_EXIST, errorCode,test);
			  test.log(LogStatus.INFO,"Csr admin can not delete the deleted account again"); 
			  spogServer.userLogin(this.mspOrgEmailForPrepare1, OrgPwdForPrepare,test);
			  test.log(LogStatus.INFO,"Login as another prepared msp admin");
			  spogServer.deleteMSPAccountWithExpectedStatusCode(this.mspOrgId, accountOrgId, SpogConstants.RESOURCE_NOT_EXIST,ErrorCode.CAN_NOT_FIND_ORG,test);
			  test.log(LogStatus.INFO,"Msp admin can not delete another MSP account"); 
		  }else if(loginUserType.equalsIgnoreCase("direct")){
			  spogServer.userLogin(this.directOrgEmailForPrepare, OrgPwdForPrepare,test);
			  test.log(LogStatus.INFO,"Login as prepared direct admin");
			  spogServer.deleteMSPAccountWithExpectedStatusCode(this.mspOrgId, accountOrgId, SpogConstants.INSUFFICIENT_PERMISSIONS,ErrorCode.RESOURCE_PERMISSION,test);
			  test.log(LogStatus.INFO,"Direct admin can not delete MSP account");
		  }else if(loginUserType.equalsIgnoreCase("account")){
			  spogServer.userLogin(this.mspOrgEmailForPrepare, OrgPwdForPrepare,test);
			  test.log(LogStatus.INFO,"Login as msp admin");
			  spogServer.createUserAndCheck(accountEmailForPrepare, OrgPwdForPrepare, "dd", "gg", SpogConstants.DIRECT_ADMIN, accountOrgId,test);
			  test.log(LogStatus.INFO,"Create account admin successfully");
			  spogServer.userLogin(accountEmailForPrepare, OrgPwdForPrepare,test);
			  test.log(LogStatus.INFO,"Login as account admin");
			  spogServer.deleteMSPAccountWithExpectedStatusCode(this.mspOrgId, accountOrgId1, SpogConstants.INSUFFICIENT_PERMISSIONS,ErrorCode.RESOURCE_PERMISSION,test);
			  test.log(LogStatus.INFO,"Account admin can not delete another account");
			  spogServer.deleteMSPAccountWithExpectedStatusCode(this.mspOrgId, accountOrgId, SpogConstants.NOT_ALLOWED_ON_RESOURCE,ErrorCode.NO_PERMISSION_DEL_ITSELF,test);
			  test.log(LogStatus.INFO,"Account admin can not delete itself");
			  spogServer.userLogin(this.mspOrgEmailForPrepare, OrgPwdForPrepare,test);
			  test.log(LogStatus.INFO,"Login as msp admin");
			  spogServer.deleteMSPAccountWithCheck(this.mspOrgId, accountOrgId);
			  test.log(LogStatus.INFO,"Msp admin can delete account"); 
			  spogServer.userLogin(accountEmailForPrepare,OrgPwdForPrepare,SpogConstants.NOT_LOGGED_IN,test);
			  test.log(LogStatus.INFO,"Account admin can not login after the account is deleted"); 
		  }	else if(loginUserType.equalsIgnoreCase("msp_account_admin")){
			  spogServer.userLogin(this.mspAccountAdminEmailForPrepare, OrgPwdForPrepare);
			  test.log(LogStatus.INFO,"Login as msp account admin");
			  spogServer.deleteMSPAccountWithExpectedStatusCode(this.mspOrgId, this.accountOrgId1, SpogConstants.NOT_ALLOWED_ON_RESOURCE,ErrorCode.MSP_AA_CAN_NOT_DEL_ORG,test);
			  test.log(LogStatus.INFO,"Account admin can not delete assigned account");
			  spogServer.deleteMSPAccountWithExpectedStatusCode(this.mspOrgId, this.accountOrgId2, SpogConstants.NOT_ALLOWED_ON_RESOURCE,ErrorCode.MSP_AA_CAN_NOT_DEL_ORG,test);
			  test.log(LogStatus.INFO,"Account admin can not delete unassigned account");
			  spogServer.deleteMSPAccountWithExpectedStatusCode(this.mspOrgId, accountOrgId, SpogConstants.NOT_ALLOWED_ON_RESOURCE,ErrorCode.MSP_AA_CAN_NOT_DEL_ORG,test);
			  test.log(LogStatus.INFO,"Account admin can not delete other account");
		  }		  
	  }
	  
	  @DataProvider(name = "nonExistingOrInvalidParentId")
	  public final Object[][] getNonExistingOrInvalidParentId() {
			return new Object[][] { {"csr", "fadsfad"}, {"csr","uuid"}, {"csr","deleted"},
									{"direct", "fadsfad"}, {"direct","uuid"}, {"direct","deleted"},
									{"account", "fadsfad"}, {"account","uuid"}, {"account","deleted"}};
	  }
	  @Test(dataProvider = "nonExistingOrInvalidParentId")
	  public void CanNotDeleteAccountWithInvalidParentId(String loginUserType,String parentId){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  boolean uuidValid=false;
		  String accountEmailForPrepare = spogServer.ReturnRandom("d_jing_spogqa_account_org_prepare@arcserve.com");
		  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
		  test.log(LogStatus.INFO,"Login as csr admin");
		  String errorCode=ErrorCode.ELEMENT_NOT_UUID;
		  if(loginUserType.equalsIgnoreCase("csr")&&parentId.equalsIgnoreCase("uuid")){
			  parentId=spogServer.returnRandomUUID();
			  uuidValid= true;
			  errorCode=ErrorCode.ELEMENT_NOT_UUID;
		  }else if(parentId.equalsIgnoreCase("uuid")){
			  parentId=spogServer.returnRandomUUID();
			  uuidValid= true;
		  }
		  if (loginUserType.equalsIgnoreCase("csr")){
			  if(uuidValid){
				  spogServer.deleteMSPAccountWithExpectedStatusCode(parentId, this.accountOrgId, SpogConstants.RESOURCE_NOT_EXIST,ErrorCode.RESOURCE_NOT_FOUND_OR_REMOVED,test);
				  test.log(LogStatus.INFO,"Csr admin can not delete account with non standard uuid as parent id will be failed");
			  }else{
				  spogServer.deleteMSPAccountWithExpectedStatusCode(parentId, this.accountOrgId, SpogConstants.REQUIRED_INFO_NOT_EXIST,errorCode,test);
				  test.log(LogStatus.INFO,"Csr admin can not delete account with parent id is deleted or non existing uuid will be failed");
			  }
			  //spogServer.deleteMSPAccountWithExpectedStatusCode(this.mspOrgId, "", SpogConstants.REQUIRED_INFO_NOT_EXIST,test);
			  test.log(LogStatus.INFO,"Csr admin can not delete account with account org id is empty");
			  spogServer.deleteMSPAccountWithExpectedStatusCode(this.mspOrgId, null, SpogConstants.REQUIRED_INFO_NOT_EXIST,errorCode,test);
			  test.log(LogStatus.INFO,"Csr admin can not delete account with account org id is null");	 
			  spogServer.deleteMSPAccountWithExpectedStatusCode(this.mspOrgId, spogServer.returnRandomUUID(), SpogConstants.RESOURCE_NOT_EXIST,ErrorCode.RESOURCE_NOT_FOUND_OR_REMOVED,test);
			  test.log(LogStatus.INFO,"Csr admin can not delete account with account org id is invalid uuid");	 
		  }else if(loginUserType.equalsIgnoreCase("direct")){
			  if(parentId.equalsIgnoreCase("deleted")){
				  parentId = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(mspOrgNameForPrepare)+org_prefix, SpogConstants.DIRECT_ORG, "", "", "", "",test);
				  test.log(LogStatus.INFO,"Create another direct org");
				  spogServer.DeleteOrganizationWithCheck(parentId, test);
				  test.log(LogStatus.INFO,"Delete the direct org which org id:"+parentId);
				  uuidValid= true;	
				  errorCode=ErrorCode.NOT_FOUND_RESOURCE_ID;
			  }
			  spogServer.userLogin(this.directOrgEmailForPrepare, OrgPwdForPrepare,test);
			  test.log(LogStatus.INFO,"Login as prepared direct admin");
			  if(uuidValid){
				  spogServer.deleteMSPAccountWithExpectedStatusCode(parentId, this.accountOrgId, SpogConstants.INSUFFICIENT_PERMISSIONS,ErrorCode.RESOURCE_PERMISSION,test);
				  test.log(LogStatus.INFO,"Direct admin can not delete account with non standard uuid as parent id will be failed");
			  }else{
				  spogServer.deleteMSPAccountWithExpectedStatusCode(parentId, this.accountOrgId, SpogConstants.REQUIRED_INFO_NOT_EXIST,errorCode,test);
				  test.log(LogStatus.INFO,"Direct admin can not delete account with parent id is deleted or non existing uuid will be failed");
			  }
		  }else if(loginUserType.equalsIgnoreCase("msp")){			  
			  if(parentId.equalsIgnoreCase("deleted")){
				  parentId = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(mspOrgNameForPrepare)+org_prefix, SpogConstants.MSP_ORG, "", "", "", "",test);
				  test.log(LogStatus.INFO,"Create another msp org");
				  spogServer.DeleteOrganizationWithCheck(parentId, test);
				  test.log(LogStatus.INFO,"Delete the msp org which org id:"+parentId);
				  uuidValid= true;	
				  errorCode=ErrorCode.NOT_FOUND_RESOURCE_ID;
			  }
			  spogServer.userLogin(this.mspOrgEmailForPrepare, OrgPwdForPrepare,test);
			  test.log(LogStatus.INFO,"Login as prepared msp admin");
			  if(uuidValid){
				  spogServer.deleteMSPAccountWithExpectedStatusCode(parentId, this.accountOrgId, SpogConstants.RESOURCE_NOT_EXIST,ErrorCode.RESOURCE_PERMISSION,test);
				  test.log(LogStatus.INFO,"Msp admin can not delete account with non standard uuid as parent id will be failed");
			  }else{
				  spogServer.deleteMSPAccountWithExpectedStatusCode(parentId, this.accountOrgId, SpogConstants.REQUIRED_INFO_NOT_EXIST,ErrorCode.RESOURCE_PERMISSION,test);
				  test.log(LogStatus.INFO,"Msp admin can not delete account with parent id is deleted or non existing uuid will be failed");
			  }
			  spogServer.deleteMSPAccountWithExpectedStatusCode(this.mspOrgId, "", SpogConstants.REQUIRED_INFO_NOT_EXIST,errorCode,test);
			  test.log(LogStatus.INFO,"Msp admin can not delete account with account org id is empty");
			  spogServer.deleteMSPAccountWithExpectedStatusCode(this.mspOrgId, null, SpogConstants.REQUIRED_INFO_NOT_EXIST,errorCode,test);
			  test.log(LogStatus.INFO,"Msp admin can not delete account with account org id is null");
			  spogServer.deleteMSPAccountWithExpectedStatusCode(this.mspOrgId, spogServer.returnRandomUUID(), SpogConstants.RESOURCE_NOT_EXIST,errorCode,test);
			  test.log(LogStatus.INFO,"Msp admin can not delete account with account org id is invalid uuid");	 
		  }else if(loginUserType.equalsIgnoreCase("account")){
			  String parent_pre;
			  parent_pre= spogServer.createAccountWithCheck(this.mspOrgId,spogServer.ReturnRandom(mspOrgNameForPrepare)+org_prefix ,"",test);
			  test.log(LogStatus.INFO,"Create prepared account successfully which org id:"+parent_pre);
			  spogServer.createUserAndCheck(accountEmailForPrepare, OrgPwdForPrepare, "dd", "gg", SpogConstants.DIRECT_ADMIN, parent_pre,test);
			  test.log(LogStatus.INFO,"Create account admin successfully");
			  if(parentId.equalsIgnoreCase("deleted")){
				  parentId= spogServer.createAccountWithCheck(this.mspOrgId,spogServer.ReturnRandom(mspOrgNameForPrepare) +org_prefix,"",test);
				  test.log(LogStatus.INFO,"Create account successfully which org id:"+parentId);
				  spogServer.DeleteOrganizationWithCheck(parentId, test);
				  test.log(LogStatus.INFO,"Delete the direct org which org id:"+parentId);
				  uuidValid= true;
				  errorCode=ErrorCode.NOT_FOUND_RESOURCE_ID;
			  }
			  spogServer.userLogin(accountEmailForPrepare, OrgPwdForPrepare,test);
			  test.log(LogStatus.INFO,"Login as account admin");
			  if(uuidValid){
				  spogServer.deleteMSPAccountWithExpectedStatusCode(parentId, this.accountOrgId, SpogConstants.INSUFFICIENT_PERMISSIONS,ErrorCode.RESOURCE_PERMISSION,test);
				  test.log(LogStatus.INFO,"Account admin can not delete account with non standard uuid as parent id will be failed");
			  }else{
				  spogServer.deleteMSPAccountWithExpectedStatusCode(parentId, this.accountOrgId, SpogConstants.REQUIRED_INFO_NOT_EXIST,ErrorCode.ELEMENT_NOT_UUID,test);
				  test.log(LogStatus.INFO,"Account admin can not delete account with parent id is deleted or non existing uuid will be failed");
			  }
		  }
	  }
	  
	  @Test
	  public void notLoggedDeleteAccount(){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  spogServer.setToken("");
		  spogServer.deleteMSPAccountWithExpectedStatusCode(this.mspOrgId, this.accountOrgIdForNotLogin,SpogConstants.NOT_LOGGED_IN,ErrorCode.AUTHORIZATION_HEADER_BLANK, test);
		  spogServer.deleteMSPAccountWithExpectedStatusCode(this.csrOrgId, this.accountOrgIdForNotLogin,SpogConstants.NOT_LOGGED_IN, ErrorCode.AUTHORIZATION_HEADER_BLANK,test);
		  spogServer.deleteMSPAccountWithExpectedStatusCode(this.directOrgId, this.accountOrgIdForNotLogin,SpogConstants.NOT_LOGGED_IN,ErrorCode.AUTHORIZATION_HEADER_BLANK, test);
		  spogServer.deleteMSPAccountWithExpectedStatusCode(this.accountOrgId, this.accountOrgIdForNotLogin,SpogConstants.NOT_LOGGED_IN,ErrorCode.AUTHORIZATION_HEADER_BLANK, test);
		  test.log(LogStatus.INFO,"Delete account without login will be failed");
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
