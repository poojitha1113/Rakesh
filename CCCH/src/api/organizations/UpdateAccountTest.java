package api.organizations;

import java.io.IOException;
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

public class UpdateAccountTest extends base.prepare.PrepareOrgInfo{
	 @Parameters({ "pmfKey"})
	  public UpdateAccountTest(String pmfKey) {
			super(pmfKey);
			// TODO Auto-generated constructor stub
		}
	  private SPOGServer spogServer;
	  private String directOrgId;
	  private String mspOrgId;
	  private String mspOrgId1;
	  private String accountOrgId;
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
	  private String accountOrgId1,accountOrgId2,msp_account_admin_token;
	  
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
		  String accountEamil=spogServer.ReturnRandom("tt3@arcserve.com");
		  spogServer.createUserAndCheck(accountEamil,csrAdminPassword,"dd", "gg", SpogConstants.DIRECT_ADMIN, accountOrgId,test);
		  spogServer.userLogin(accountEamil, this.csrPwd);
		  //account admin can't update its org name
		  //spogServer.updateAccountWithCheck(mspOrgId, accountOrgId, "",test);
		  //spogServer.updateAccountWithCheck(mspOrgId, accountOrgId, null,test);
		  //spogServer.updateAccountWithCheck(mspOrgId, accountOrgId, "fff",test);
		  spogServer.userLogin(mspOrgEmailForPrepare, OrgPwdForPrepare);
		  spogServer.updateAccountWithCheck(mspOrgId, accountOrgId, "",test);
		  spogServer.updateAccountWithCheck(mspOrgId, accountOrgId, null,test);
		  spogServer.updateAccountWithCheck(mspOrgId, accountOrgId, "fff"+org_prefix,test);
		  
		  spogServer.userLogin(this.csrAdmin, this.csrPwd);
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
		  //msp account admin can't update assigned account org name
		  //spogServer.updateAccountWithCheck(mspOrgId, accountOrgId1, "fff",test);
	  }
	  
	  @DataProvider(name = "permissionTest")
	  public final Object[][] permissionTest() {
			return new Object[][] {
									{"msp"},{"direct"},{"account"},
									{"rootmsp"},{"rootmspaccount"},{"rootmspaccountadmin"},
									{"submsp"},{"submspaccount"},{"submspaccountadmin"},
      };
	  }
	  @Test(dataProvider = "permissionTest")
	  public void permissionTest(String usertype){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  if (usertype.equalsIgnoreCase("direct")) {
			  spogServer.userLogin(this.directOrgEmailForPrepare,this.OrgPwdForPrepare,test);
			  spogServer.updateAccountFailedWithExpectedStatusCode(this.root_msp_org_id,this.root_msp_direct_org_id,spogServer.ReturnRandom("spogqa_account")+org_prefix,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
			  spogServer.updateAccountFailedWithExpectedStatusCode(this.sub_msp1_org_id, this.sub_msp1_account1_id,spogServer.ReturnRandom("spogqa_account")+org_prefix,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
			  
          }else if(usertype.equalsIgnoreCase("msp")) {
			  spogServer.userLogin(this.mspOrgEmailForPrepare, OrgPwdForPrepare,test);
			  spogServer.updateAccountFailedWithExpectedStatusCode(this.root_msp_org_id,this.root_msp_direct_org_id,spogServer.ReturnRandom("spogqa_account")+org_prefix,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
			  spogServer.updateAccountFailedWithExpectedStatusCode(this.sub_msp1_org_id, this.sub_msp1_account1_id,spogServer.ReturnRandom("spogqa_account")+org_prefix,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
			  
		  }else if(usertype.equalsIgnoreCase("account")) {
			  spogServer.userLogin(this.accountEmailForPrepare, OrgPwdForPrepare,test);
			  spogServer.updateAccountFailedWithExpectedStatusCode(this.root_msp_org_id,this.root_msp_direct_org_id,spogServer.ReturnRandom("spogqa_account")+org_prefix,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
			  spogServer.updateAccountFailedWithExpectedStatusCode(this.sub_msp1_org_id, this.sub_msp1_account1_id,spogServer.ReturnRandom("spogqa_account")+org_prefix,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
			  
		  }else if(usertype.equalsIgnoreCase("accountadmin")) {
			  spogServer.userLogin(this.mspAccountAdminEmailForPrepare, OrgPwdForPrepare,test);
			  spogServer.updateAccountFailedWithExpectedStatusCode(this.root_msp_org_id,this.root_msp_direct_org_id,spogServer.ReturnRandom("spogqa_account")+org_prefix,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
			  spogServer.updateAccountFailedWithExpectedStatusCode(this.sub_msp1_org_id, this.sub_msp1_account1_id,spogServer.ReturnRandom("spogqa_account")+org_prefix,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
			  
		  }else if(usertype.equalsIgnoreCase("rootmsp")) {
			  spogServer.userLogin(this.final_root_msp_user_name_email, OrgPwdForPrepare,test);
			  spogServer.updateAccountFailedWithExpectedStatusCode(this.mspOrgId1, this.accountOrgId,spogServer.ReturnRandom("spogqa_account")+org_prefix,SpogConstants.RESOURCE_NOT_EXIST,"0030000A",test);
			  spogServer.updateAccountWithCheck(this.root_msp_org_id,this.root_msp_direct_org_id,spogServer.ReturnRandom("spogqa_account")+org_prefix,test);
			  spogServer.updateAccountFailedWithExpectedStatusCode(this.sub_msp1_org_id, this.sub_msp1_account1_id,spogServer.ReturnRandom("spogqa_account")+org_prefix,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
			  
		  }else if(usertype.equalsIgnoreCase("rootmspaccount")) {
			  spogServer.userLogin(this.final_root_msp_direct_org_user_email, OrgPwdForPrepare,test);
			  spogServer.updateAccountFailedWithExpectedStatusCode(this.mspOrgId1, this.accountOrgId,spogServer.ReturnRandom("spogqa_account")+org_prefix,SpogConstants.RESOURCE_NOT_EXIST,"0030000A",test);
			  spogServer.updateAccountWithCheck(this.root_msp_org_id,this.root_msp_direct_org_id,spogServer.ReturnRandom("spogqa_account")+org_prefix,test);
			  spogServer.updateAccountFailedWithExpectedStatusCode(this.sub_msp1_org_id, this.sub_msp1_account1_id,spogServer.ReturnRandom("spogqa_account")+org_prefix,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
			  
		  }else if(usertype.equalsIgnoreCase("rootmspaccountadmin")) {
			  spogServer.userLogin(this.final_root_msp_account_admin_user_name_email, OrgPwdForPrepare,test);
			  spogServer.updateAccountFailedWithExpectedStatusCode(this.mspOrgId1, this.accountOrgId,spogServer.ReturnRandom("spogqa_account")+org_prefix,SpogConstants.RESOURCE_NOT_EXIST,"0030000A",test);
			  spogServer.updateAccountWithCheck(this.root_msp_org_id,this.root_msp_direct_org_id,spogServer.ReturnRandom("spogqa_account")+org_prefix,test);
			  spogServer.updateAccountFailedWithExpectedStatusCode(this.sub_msp1_org_id, this.sub_msp1_account1_id,spogServer.ReturnRandom("spogqa_account")+org_prefix,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
			  
		  }else if(usertype.equalsIgnoreCase("submsp")) {
			  spogServer.userLogin(this.final_sub_msp2_user_name_email, OrgPwdForPrepare,test);
			  spogServer.updateAccountFailedWithExpectedStatusCode(this.mspOrgId1, this.accountOrgId,spogServer.ReturnRandom("spogqa_account")+org_prefix,SpogConstants.RESOURCE_NOT_EXIST,"0030000A",test);
			  spogServer.updateAccountFailedWithExpectedStatusCode(this.root_msp_org_id,this.root_msp_direct_org_id,spogServer.ReturnRandom("spogqa_account")+org_prefix,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
			  spogServer.updateAccountFailedWithExpectedStatusCode(this.sub_msp1_org_id, this.sub_msp1_account1_id,spogServer.ReturnRandom("spogqa_account")+org_prefix,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
			  spogServer.updateAccountWithCheck(this.sub_msp2_org_id,this.sub_msp2_account1_id,spogServer.ReturnRandom("spogqa_account")+org_prefix,test);
			  
		  }else if(usertype.equalsIgnoreCase("submspaccount")) {
			  spogServer.userLogin(this.final_sub_msp2_account1_user_email, OrgPwdForPrepare,test);
			  spogServer.updateAccountFailedWithExpectedStatusCode(this.mspOrgId1, this.accountOrgId,spogServer.ReturnRandom("spogqa_account")+org_prefix,SpogConstants.RESOURCE_NOT_EXIST,"0030000A",test);
			  spogServer.updateAccountFailedWithExpectedStatusCode(this.root_msp_org_id,this.root_msp_direct_org_id,spogServer.ReturnRandom("spogqa_account")+org_prefix,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
			  spogServer.updateAccountFailedWithExpectedStatusCode(this.sub_msp1_org_id, this.sub_msp1_account1_id,spogServer.ReturnRandom("spogqa_account")+org_prefix,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
			  spogServer.updateAccountWithCheck(this.sub_msp2_org_id,this.sub_msp2_account1_id,spogServer.ReturnRandom("spogqa_account")+org_prefix,test);
			  
		  }else if(usertype.equalsIgnoreCase("submspaccountadmin")) {
			  spogServer.userLogin(this.final_sub_msp1_msp_account_user_name_email, OrgPwdForPrepare,test);
			  spogServer.updateAccountFailedWithExpectedStatusCode(this.mspOrgId1, this.accountOrgId,spogServer.ReturnRandom("spogqa_account")+org_prefix,SpogConstants.RESOURCE_NOT_EXIST,"0030000A",test);
			  spogServer.updateAccountFailedWithExpectedStatusCode(this.root_msp_org_id,this.root_msp_direct_org_id,spogServer.ReturnRandom("spogqa_account")+org_prefix,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
			  spogServer.updateAccountFailedWithExpectedStatusCode(this.sub_msp2_org_id, this.sub_msp2_account1_id,spogServer.ReturnRandom("spogqa_account")+org_prefix,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
			  spogServer.updateAccountWithCheck(this.sub_msp1_org_id,this.sub_msp1_account1_id,spogServer.ReturnRandom("spogqa_account")+org_prefix,test);
			  
		  }
		  
	  }
	  
	  @DataProvider(name = "loginUserToUpdateDifferentAccount")
	  public final Object[][] getLoginUserToUpdateDifferentAccount() {
			return new Object[][] { {"csr","csr",""},{"csr","csr",null},{"csr","csr","random"},
									{"csr","direct",""},{"csr","direct",null},{"csr","direct","random"},
									{"csr","msp",""},{"csr","msp",null},{"csr","msp","random"},
									{"csr","account",""},{"csr","account",null},{"csr","account","random"},
									{"msp","csr",""},{"msp","csr",null},{"msp","csr","random"},
									{"msp","direct",""},{"msp","direct",null},{"msp","direct","random"},
									{"msp","msp",""},{"msp","msp",null},{"msp","msp","random"},
									{"msp","account",""},{"msp","account",null},{"msp","account","random"},
									{"direct","csr",""},{"direct","csr",null},{"direct","csr","random"},
									{"direct","direct",""},{"direct","direct",null},{"direct","direct","random"},
									{"direct","msp",""},{"direct","msp",null},{"direct","msp","random"},
									{"direct","account",""},{"direct","account",null},{"direct","account","random"},
									{"account","csr",""},{"account","csr",null},{"account","csr","random"},
									{"account","direct",""},{"account","direct",null},{"account","direct","random"},
									{"account","msp",""},{"account","msp",null},{"account","msp","random"},
									{"account","account",""},{"account","account",null},{"account","account","random"},
									{"msp_account","csr",""},{"msp_account","csr",null},{"msp_account","csr","random"},
									{"msp_account","direct",""},{"msp_account","direct",null},{"msp_account","direct","random"},
									{"msp_account","msp",""},{"msp_account","msp",null},{"msp_account","msp","random"},
									{"msp_account","account",""},{"msp_account","account",null},{"msp_account","account","random"}};
	  }
	  
	  @Test(dataProvider = "loginUserToUpdateDifferentAccount")
	  
	  public void updateAccountWithDifferentLoginUserAndDifferentParentIdAndAccountNameForBlock(String loginUserType,String parentOrgType, String updateAccountName){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  String parentIdInUrl="";
		  String accountOrgId="";
		  String accountOrgId1="";
		  String accountEmailForPrepare = spogServer.ReturnRandom("d_jing_spogqa_account_org_prepare@arcserve.com");
		  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
		  test.log(LogStatus.INFO,"Login as csr admin");	
		  accountOrgId = spogServer.createAccountWithCheck(this.mspOrgId, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",test);
		  Response rrResponse=spogServer.updateOrganizationBlockStatusByID(accountOrgId, true, test);
		  accountOrgId1 = spogServer.createAccountWithCheck(this.mspOrgId1, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",test);
		  test.log(LogStatus.INFO,"Csr admin can create account with parent id is msp organization id.");
		  if (parentOrgType.equalsIgnoreCase("csr")){
			  parentIdInUrl = this.csrOrgId;
		  }else if(parentOrgType.equalsIgnoreCase("direct")){
			  parentIdInUrl = this.directOrgId;
		  }else if(parentOrgType.equalsIgnoreCase("msp")){
			  parentIdInUrl = this.mspOrgId;
		  }else if(parentOrgType.equalsIgnoreCase("account")){
			  parentIdInUrl = this.accountOrgId;
		  }
		  if (!spogServer.CheckIsNullOrEmpty(updateAccountName)){
			  updateAccountName=spogServer.ReturnRandom("spogqa_account")+org_prefix;
		  }
		 if (loginUserType.equalsIgnoreCase("csr")){			  
			  if(parentOrgType.equalsIgnoreCase("msp")){
				  if(!spogServer.CheckIsNullOrEmpty(updateAccountName)){
					  spogServer.updateAccountWithCheck(parentIdInUrl, accountOrgId, updateAccountName, test);
					  test.log(LogStatus.INFO,"Csr admin can update account name under msp org.");
					  spogServer.updateAccountFailedWithExpectedStatusCode(this.mspOrgId, accountOrgId1, updateAccountName, SpogConstants.RESOURCE_NOT_EXIST,ErrorCode.RESOURCE_NOT_FOUND_OR_REMOVED,test);
					  test.log(LogStatus.INFO,"Csr admin can not update account name with parent id is another msp org id.");					  
				  }else{
					  spogServer.updateAccountWithCheck(parentIdInUrl, accountOrgId, updateAccountName,test);
					  test.log(LogStatus.INFO,"Csr admin can update with account name is empty or null under msp org.");
				  }
			  }else{
				  spogServer.updateAccountFailedWithExpectedStatusCode(parentIdInUrl, accountOrgId, updateAccountName, SpogConstants.RESOURCE_NOT_EXIST,ErrorCode.RESOURCE_NOT_FOUND_OR_REMOVED,test);
				  test.log(LogStatus.INFO,"Csr admin can not update account name with parent id is "+parentOrgType+ " org.");					  
			  }				 			  		  
		  }else if(loginUserType.equalsIgnoreCase("msp")){
			  spogServer.userLogin(this.mspOrgEmailForPrepare, OrgPwdForPrepare,test);
			  test.log(LogStatus.INFO,"Login as prepared msp admin");		
			  if(parentOrgType.equalsIgnoreCase("msp")){
				  if(!spogServer.CheckIsNullOrEmpty(updateAccountName)){
					  spogServer.updateAccountWithCheck(parentIdInUrl, accountOrgId, updateAccountName, test);
					  test.log(LogStatus.INFO,"Msp admin can update account name under msp org.");
					  spogServer.updateAccountFailedWithExpectedStatusCode(parentIdInUrl, accountOrgId1, updateAccountName, SpogConstants.RESOURCE_NOT_EXIST,ErrorCode.RESOURCE_NOT_FOUND_OR_REMOVED,test);
					  test.log(LogStatus.INFO,"Msp admin can not update account name with parent id is another msp org id.");
					  spogServer.updateAccountFailedWithExpectedStatusCode(this.mspOrgId1, accountOrgId1, updateAccountName, SpogConstants.INSUFFICIENT_PERMISSIONS,ErrorCode.RESOURCE_PERMISSION,test);
					  test.log(LogStatus.INFO,"Msp admin can not update account name which is under another msp");
				  }else{
					  spogServer.updateAccountWithCheck(parentIdInUrl, accountOrgId, updateAccountName,test);
					  test.log(LogStatus.INFO,"Msp admin can update account name is empty or null under msp org.");
				  }
			  }else{
				  spogServer.updateAccountFailedWithExpectedStatusCode(parentIdInUrl, accountOrgId, updateAccountName, SpogConstants.RESOURCE_NOT_EXIST,ErrorCode.RESOURCE_NOT_FOUND_OR_REMOVED,test);
				  test.log(LogStatus.INFO,"Msp admin can not update account name with parent id is "+parentOrgType+ " org.");					  
			  }
		  }else if(loginUserType.equalsIgnoreCase("direct")){
			  spogServer.userLogin(this.directOrgEmailForPrepare, OrgPwdForPrepare,test);
			  test.log(LogStatus.INFO,"Login as prepared direct admin");
			  if(parentOrgType.equalsIgnoreCase("msp")){
				  if(!spogServer.CheckIsNullOrEmpty(updateAccountName)){
					  spogServer.updateAccountFailedWithExpectedStatusCode(this.mspOrgId, accountOrgId, updateAccountName, SpogConstants.INSUFFICIENT_PERMISSIONS,ErrorCode.RESOURCE_PERMISSION,test);
					  test.log(LogStatus.INFO,"Direct admin can not update account name under msp org.");
					  spogServer.updateAccountFailedWithExpectedStatusCode(this.mspOrgId, accountOrgId1, updateAccountName, SpogConstants.RESOURCE_NOT_EXIST,ErrorCode.RESOURCE_NOT_FOUND_OR_REMOVED,test);
					  test.log(LogStatus.INFO,"Direct admin can not update account name with parent id is another msp org id.");
					  spogServer.updateAccountFailedWithExpectedStatusCode(this.mspOrgId1, accountOrgId1, updateAccountName, SpogConstants.INSUFFICIENT_PERMISSIONS,ErrorCode.RESOURCE_PERMISSION,test);
					  test.log(LogStatus.INFO,"Direct admin can not update account name which is under another msp");					  
				  }else{
					  spogServer.updateAccountFailedWithExpectedStatusCode(this.mspOrgId, accountOrgId, updateAccountName, SpogConstants.INSUFFICIENT_PERMISSIONS,ErrorCode.RESOURCE_PERMISSION,test);
					  test.log(LogStatus.INFO,"Direct admin can not update account name which is under msp org.");
				  }
			  }else{
				  spogServer.updateAccountFailedWithExpectedStatusCode(parentIdInUrl, accountOrgId, updateAccountName, SpogConstants.RESOURCE_NOT_EXIST,ErrorCode.RESOURCE_NOT_FOUND_OR_REMOVED,test);
				  test.log(LogStatus.INFO,"Direct admin can not update account name with parent id is "+parentOrgType+ " org.");					  
			  }
		  }else if(loginUserType.equalsIgnoreCase("account")){
			  spogServer.userLogin(this.mspOrgEmailForPrepare, OrgPwdForPrepare,test);
			  test.log(LogStatus.INFO,"Login as msp admin");
			  spogServer.createUserAndCheck(accountEmailForPrepare, OrgPwdForPrepare, "dd", "gg", SpogConstants.DIRECT_ADMIN, accountOrgId,test);
			  test.log(LogStatus.INFO,"Create account admin successfully");
			  spogServer.userLogin(accountEmailForPrepare, OrgPwdForPrepare,401,test);
			  test.log(LogStatus.INFO,"Can't login as account admin once its org is blocked");
			  if(parentOrgType.equalsIgnoreCase("msp")){
				  spogServer.userLogin(this.mspAccountAdminEmailForPrepare, OrgPwdForPrepare);
				  if(!spogServer.CheckIsNullOrEmpty(updateAccountName)){
					  spogServer.updateAccountWithCheck(this.mspOrgId, this.accountOrgId1, updateAccountName, test);
					  test.log(LogStatus.INFO,"Account admin can update it account name.");
					  spogServer.updateAccountFailedWithExpectedStatusCode(this.mspOrgId, this.accountOrgId, updateAccountName, SpogConstants.INSUFFICIENT_PERMISSIONS,ErrorCode.RESOURCE_PERMISSION,test);
					  test.log(LogStatus.INFO,"Account admin can not update account name with parent id is another msp org id.");
					  spogServer.updateAccountFailedWithExpectedStatusCode(this.mspOrgId1, this.accountOrgId1, updateAccountName, SpogConstants.RESOURCE_NOT_EXIST,ErrorCode.RESOURCE_NOT_FOUND_OR_REMOVED,test);
					  test.log(LogStatus.INFO,"Account admin can not update account name which is under another msp");
				  }else{
					  spogServer.updateAccountWithCheck(this.mspOrgId, this.accountOrgId1, updateAccountName, test);
					  test.log(LogStatus.INFO,"Account admin can not update account name with name is null or empty.");
				  }
				  spogServer.userLogin(this.mspOrgEmailForPrepare, OrgPwdForPrepare,test);
				  if(!spogServer.CheckIsNullOrEmpty(updateAccountName)){
					  spogServer.updateAccountWithCheck(this.mspOrgId, accountOrgId, updateAccountName, test);
					  test.log(LogStatus.INFO,"Account admin can update it account name.");
					  spogServer.updateAccountFailedWithExpectedStatusCode(this.mspOrgId, accountOrgId1, updateAccountName, SpogConstants.RESOURCE_NOT_EXIST,ErrorCode.RESOURCE_NOT_FOUND_OR_REMOVED,test);
					  test.log(LogStatus.INFO,"Account admin can not update account name with parent id is another msp org id.");
					  spogServer.updateAccountFailedWithExpectedStatusCode(this.mspOrgId1, accountOrgId1, updateAccountName, SpogConstants.INSUFFICIENT_PERMISSIONS,ErrorCode.RESOURCE_PERMISSION,test);
					  test.log(LogStatus.INFO,"Account admin can not update account name which is under another msp");
				  }else{
					  spogServer.updateAccountWithCheck(this.mspOrgId, accountOrgId, updateAccountName, test);
					  test.log(LogStatus.INFO,"Account admin can not update account name with name is null or empty.");
				  }
			  }
		  }else if(loginUserType.equalsIgnoreCase("msp_account")){
			  spogServer.userLogin(this.mspAccountAdminEmailForPrepare, OrgPwdForPrepare);
			  test.log(LogStatus.INFO,"Login as msp account admin");
			  if(parentOrgType.equalsIgnoreCase("msp")){
				  if(!spogServer.CheckIsNullOrEmpty(updateAccountName)){
					  spogServer.updateAccountWithCheck(this.mspOrgId, this.accountOrgId1, updateAccountName, test);
					  test.log(LogStatus.INFO,"msp account admin can update assigned account org name");
					  spogServer.updateAccountFailedWithExpectedStatusCode(this.mspOrgId, this.accountOrgId2, updateAccountName, SpogConstants.INSUFFICIENT_PERMISSIONS,ErrorCode.RESOURCE_PERMISSION,test);
					  test.log(LogStatus.INFO,"msp account admin can not update unassigned account org name");
					  spogServer.updateAccountFailedWithExpectedStatusCode(this.mspOrgId1, accountOrgId1, updateAccountName, SpogConstants.INSUFFICIENT_PERMISSIONS,ErrorCode.RESOURCE_PERMISSION,test);
					  test.log(LogStatus.INFO,"msp account admin can not update account under other msp org");				  
				  }else{
					  spogServer.updateAccountFailedWithExpectedStatusCode(this.mspOrgId, accountOrgId, updateAccountName, SpogConstants.INSUFFICIENT_PERMISSIONS,ErrorCode.RESOURCE_PERMISSION,test);
					  test.log(LogStatus.INFO,"msp account admin can not update account name which is under msp org.");
				  }
			  }else{
				  spogServer.updateAccountFailedWithExpectedStatusCode(parentIdInUrl, accountOrgId, updateAccountName, SpogConstants.RESOURCE_NOT_EXIST,ErrorCode.RESOURCE_NOT_FOUND_OR_REMOVED,test);
				  test.log(LogStatus.INFO,"msp account admin can not update account name with parent id is "+parentOrgType+ " org.");					  
			  }
		  }	  
	  }
	  
	  @Test(dataProvider = "loginUserToUpdateDifferentAccount")
	  
	  public void updateAccountWithDifferentLoginUserAndDifferentParentIdAndAccountName(String loginUserType,String parentOrgType, String updateAccountName){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  String parentIdInUrl="";
		  String accountOrgId="";
		  String accountOrgId1="";
		  String accountEmailForPrepare = spogServer.ReturnRandom("d_jing_spogqa_account_org_prepare@arcserve.com");
		  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
		  test.log(LogStatus.INFO,"Login as csr admin");	
		  accountOrgId = spogServer.createAccountWithCheck(this.mspOrgId, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",test);
		  accountOrgId1 = spogServer.createAccountWithCheck(this.mspOrgId1, spogServer.ReturnRandom("spogqa_account"+org_prefix),"",test);
		  test.log(LogStatus.INFO,"Csr admin can create account with parent id is msp organization id.");
		  if (parentOrgType.equalsIgnoreCase("csr")){
			  parentIdInUrl = this.csrOrgId;
		  }else if(parentOrgType.equalsIgnoreCase("direct")){
			  parentIdInUrl = this.directOrgId;
		  }else if(parentOrgType.equalsIgnoreCase("msp")){
			  parentIdInUrl = this.mspOrgId;
		  }else if(parentOrgType.equalsIgnoreCase("account")){
			  parentIdInUrl = this.accountOrgId;
		  }
		  if (!spogServer.CheckIsNullOrEmpty(updateAccountName)){
			  updateAccountName=spogServer.ReturnRandom("spogqa_account")+org_prefix;
		  }
		 if (loginUserType.equalsIgnoreCase("csr")){			  
			  if(parentOrgType.equalsIgnoreCase("msp")){
				  if(!spogServer.CheckIsNullOrEmpty(updateAccountName)){
					  spogServer.updateAccountWithCheck(parentIdInUrl, accountOrgId, updateAccountName, test);
					  test.log(LogStatus.INFO,"Csr admin can update account name under msp org.");
					  spogServer.updateAccountFailedWithExpectedStatusCode(this.mspOrgId, accountOrgId1, updateAccountName, SpogConstants.RESOURCE_NOT_EXIST,ErrorCode.RESOURCE_NOT_FOUND_OR_REMOVED,test);
					  test.log(LogStatus.INFO,"Csr admin can not update account name with parent id is another msp org id.");					  
				  }else{
					  spogServer.updateAccountWithCheck(parentIdInUrl, accountOrgId, updateAccountName,test);
					  test.log(LogStatus.INFO,"Csr admin can update with account name is empty or null under msp org.");
				  }
			  }else{
				  spogServer.updateAccountFailedWithExpectedStatusCode(parentIdInUrl, accountOrgId, updateAccountName, SpogConstants.RESOURCE_NOT_EXIST,ErrorCode.RESOURCE_NOT_FOUND_OR_REMOVED,test);
				  test.log(LogStatus.INFO,"Csr admin can not update account name with parent id is "+parentOrgType+ " org.");					  
			  }				 			  		  
		  }else if(loginUserType.equalsIgnoreCase("msp")){
			  spogServer.userLogin(this.mspOrgEmailForPrepare, OrgPwdForPrepare,test);
			  test.log(LogStatus.INFO,"Login as prepared msp admin");		
			  if(parentOrgType.equalsIgnoreCase("msp")){
				  if(!spogServer.CheckIsNullOrEmpty(updateAccountName)){
					  spogServer.updateAccountWithCheck(parentIdInUrl, accountOrgId, updateAccountName, test);
					  test.log(LogStatus.INFO,"Msp admin can update account name under msp org.");
					  spogServer.updateAccountFailedWithExpectedStatusCode(parentIdInUrl, accountOrgId1, updateAccountName, SpogConstants.RESOURCE_NOT_EXIST,ErrorCode.RESOURCE_NOT_FOUND_OR_REMOVED,test);
					  test.log(LogStatus.INFO,"Msp admin can not update account name with parent id is another msp org id.");
					  spogServer.updateAccountFailedWithExpectedStatusCode(this.mspOrgId1, accountOrgId1, updateAccountName, SpogConstants.INSUFFICIENT_PERMISSIONS,ErrorCode.RESOURCE_PERMISSION,test);
					  test.log(LogStatus.INFO,"Msp admin can not update account name which is under another msp");
				  }else{
					  spogServer.updateAccountWithCheck(parentIdInUrl, accountOrgId, updateAccountName,test);
					  test.log(LogStatus.INFO,"Msp admin can update account name is empty or null under msp org.");
				  }
			  }else{
				  spogServer.updateAccountFailedWithExpectedStatusCode(parentIdInUrl, accountOrgId, updateAccountName, SpogConstants.RESOURCE_NOT_EXIST,ErrorCode.RESOURCE_NOT_FOUND_OR_REMOVED,test);
				  test.log(LogStatus.INFO,"Msp admin can not update account name with parent id is "+parentOrgType+ " org.");					  
			  }
		  }else if(loginUserType.equalsIgnoreCase("direct")){
			  spogServer.userLogin(this.directOrgEmailForPrepare, OrgPwdForPrepare,test);
			  test.log(LogStatus.INFO,"Login as prepared direct admin");
			  if(parentOrgType.equalsIgnoreCase("msp")){
				  if(!spogServer.CheckIsNullOrEmpty(updateAccountName)){
					  spogServer.updateAccountFailedWithExpectedStatusCode(this.mspOrgId, accountOrgId, updateAccountName, SpogConstants.INSUFFICIENT_PERMISSIONS,ErrorCode.RESOURCE_PERMISSION,test);
					  test.log(LogStatus.INFO,"Direct admin can not update account name under msp org.");
					  spogServer.updateAccountFailedWithExpectedStatusCode(this.mspOrgId, accountOrgId1, updateAccountName, SpogConstants.RESOURCE_NOT_EXIST,ErrorCode.RESOURCE_NOT_FOUND_OR_REMOVED,test);
					  test.log(LogStatus.INFO,"Direct admin can not update account name with parent id is another msp org id.");
					  spogServer.updateAccountFailedWithExpectedStatusCode(this.mspOrgId1, accountOrgId1, updateAccountName, SpogConstants.INSUFFICIENT_PERMISSIONS,ErrorCode.RESOURCE_PERMISSION,test);
					  test.log(LogStatus.INFO,"Direct admin can not update account name which is under another msp");					  
				  }else{
					  spogServer.updateAccountFailedWithExpectedStatusCode(this.mspOrgId, accountOrgId, updateAccountName, SpogConstants.INSUFFICIENT_PERMISSIONS,ErrorCode.RESOURCE_PERMISSION,test);
					  test.log(LogStatus.INFO,"Direct admin can not update account name which is under msp org.");
				  }
			  }else{
				  spogServer.updateAccountFailedWithExpectedStatusCode(parentIdInUrl, accountOrgId, updateAccountName, SpogConstants.RESOURCE_NOT_EXIST,ErrorCode.RESOURCE_NOT_FOUND_OR_REMOVED,test);
				  test.log(LogStatus.INFO,"Direct admin can not update account name with parent id is "+parentOrgType+ " org.");					  
			  }
		  }else if(loginUserType.equalsIgnoreCase("account")){
			  spogServer.userLogin(this.mspOrgEmailForPrepare, OrgPwdForPrepare,test);
			  test.log(LogStatus.INFO,"Login as msp admin");
			  spogServer.createUserAndCheck(accountEmailForPrepare, OrgPwdForPrepare, "dd", "gg", SpogConstants.DIRECT_ADMIN, accountOrgId,test);
			  test.log(LogStatus.INFO,"Create account admin successfully");
			  spogServer.userLogin(accountEmailForPrepare, OrgPwdForPrepare,test);
			  test.log(LogStatus.INFO,"Login as account admin");
			  if(parentOrgType.equalsIgnoreCase("msp")){
				  if(!spogServer.CheckIsNullOrEmpty(updateAccountName)){
					  spogServer.updateAccountWithCheck(this.mspOrgId, accountOrgId, updateAccountName, test);
					  test.log(LogStatus.INFO,"Account admin can update it account name.");
					  spogServer.updateAccountFailedWithExpectedStatusCode(this.mspOrgId, accountOrgId1, updateAccountName, SpogConstants.RESOURCE_NOT_EXIST,ErrorCode.RESOURCE_NOT_FOUND_OR_REMOVED,test);
					  test.log(LogStatus.INFO,"Account admin can not update account name with parent id is another msp org id.");
					  spogServer.updateAccountFailedWithExpectedStatusCode(this.mspOrgId1, accountOrgId1, updateAccountName, SpogConstants.INSUFFICIENT_PERMISSIONS,ErrorCode.RESOURCE_PERMISSION,test);
					  test.log(LogStatus.INFO,"Account admin can not update account name which is under another msp");
				  }else{
					  spogServer.updateAccountWithCheck(this.mspOrgId, accountOrgId, updateAccountName, test);
					  test.log(LogStatus.INFO,"Account admin can not update account name with name is null or empty.");
				  }
			  }else{
				  spogServer.updateAccountFailedWithExpectedStatusCode(parentIdInUrl, accountOrgId, updateAccountName, SpogConstants.RESOURCE_NOT_EXIST,ErrorCode.RESOURCE_NOT_FOUND_OR_REMOVED,test);
				  test.log(LogStatus.INFO,"Account admin can not update account name with parent id is "+parentOrgType+ " org.");					  
			  }
		  }else if(loginUserType.equalsIgnoreCase("msp_account")){
			  spogServer.userLogin(this.mspAccountAdminEmailForPrepare, OrgPwdForPrepare);
			  test.log(LogStatus.INFO,"Login as msp account admin");
			  if(parentOrgType.equalsIgnoreCase("msp")){
				  if(!spogServer.CheckIsNullOrEmpty(updateAccountName)){
					  spogServer.updateAccountWithCheck(this.mspOrgId, this.accountOrgId1, updateAccountName, test);
					  test.log(LogStatus.INFO,"msp account admin can update assigned account org name");
					  spogServer.updateAccountFailedWithExpectedStatusCode(this.mspOrgId, this.accountOrgId2, updateAccountName, SpogConstants.INSUFFICIENT_PERMISSIONS,ErrorCode.RESOURCE_PERMISSION,test);
					  test.log(LogStatus.INFO,"msp account admin can not update unassigned account org name");
					  spogServer.updateAccountFailedWithExpectedStatusCode(this.mspOrgId1, accountOrgId1, updateAccountName, SpogConstants.INSUFFICIENT_PERMISSIONS,ErrorCode.RESOURCE_PERMISSION,test);
					  test.log(LogStatus.INFO,"msp account admin can not update account under other msp org");				  
				  }else{
					  spogServer.updateAccountFailedWithExpectedStatusCode(this.mspOrgId, accountOrgId, updateAccountName, SpogConstants.INSUFFICIENT_PERMISSIONS,ErrorCode.RESOURCE_PERMISSION,test);
					  test.log(LogStatus.INFO,"msp account admin can not update account name which is under msp org.");
				  }
			  }else{
				  spogServer.updateAccountFailedWithExpectedStatusCode(parentIdInUrl, accountOrgId, updateAccountName, SpogConstants.RESOURCE_NOT_EXIST,ErrorCode.RESOURCE_NOT_FOUND_OR_REMOVED,test);
				  test.log(LogStatus.INFO,"msp account admin can not update account name with parent id is "+parentOrgType+ " org.");					  
			  }
		  }	  
	  }
	  
	  @DataProvider(name = "nonExistingOrInvalidId")
	  public final Object[][] getNonExistingOrInvalidId() {
			return new Object[][] { {"csr","deleted"}, {"csr","uuid"},{"msp","uuid"} ,
									{"direct", "fadsfad"}, {"direct","uuid"}, {"direct","deleted"},
									{"msp", "fadsfad"}, {"csr", "fadsfad"}, {"msp","deleted"},
									{"account", "fadsfad"}, {"account","uuid"}, {"account","deleted"}};
	  }
	  @Test(dataProvider = "nonExistingOrInvalidId")
	  public void CanNotUpdateAccountWithInvalidParentId(String loginUserType,String parentId){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  String accountEmailForPrepare = spogServer.ReturnRandom("d_jing_spogqa_account_org_prepare@arcserve.com");
		  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
		  test.log(LogStatus.INFO,"Login as csr admin");
		  String accountOrgId = spogServer.createAccountWithCheck(this.mspOrgId1, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",test);
		  int expectStatus = SpogConstants.RESOURCE_NOT_EXIST;
		  String errorCode=ErrorCode.ELEMENT_NOT_UUID;
		  if(parentId.equalsIgnoreCase("uuid")){
			  parentId=spogServer.returnRandomUUID();
			  expectStatus = SpogConstants.RESOURCE_NOT_EXIST;
			  errorCode=ErrorCode.RESOURCE_NOT_FOUND_OR_REMOVED;
		  }else if(!loginUserType.equalsIgnoreCase("csr")||!loginUserType.equalsIgnoreCase("account")){
			  parentId=spogServer.returnRandomUUID();
			  expectStatus = SpogConstants.REQUIRED_INFO_NOT_EXIST;
			  errorCode=ErrorCode.INVALID_CREATING_ACCOUNT;
		  }
		  if (loginUserType.equalsIgnoreCase("csr")){
			  if(parentId.equalsIgnoreCase("uuid")){
				  errorCode=ErrorCode.RESOURCE_NOT_FOUND_OR_REMOVED;				  
			  }else {
				  expectStatus=SpogConstants.RESOURCE_NOT_EXIST;
				  errorCode=ErrorCode.RESOURCE_NOT_FOUND_OR_REMOVED;
			  }
			  spogServer.updateAccountFailedWithExpectedStatusCode(parentId, accountOrgId,spogServer.ReturnRandom("spogqa_account")+org_prefix,expectStatus, errorCode,test);
			  test.log(LogStatus.INFO,"Csr admin can not create account with parent id is deleted or non/invalid csr org id will be failed");
		  }else if(loginUserType.equalsIgnoreCase("direct")){
			  if(parentId.equalsIgnoreCase("deleted")){
				  parentId = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(mspOrgNameForPrepare)+org_prefix, SpogConstants.DIRECT_ORG, "", "", "", "",test);
				  test.log(LogStatus.INFO,"Create another direct org");
				  spogServer.DeleteOrganizationWithCheck(parentId, test);
				  test.log(LogStatus.INFO,"Delete the direct org which org id:"+parentId);
				  errorCode=ErrorCode.RESOURCE_NOT_FOUND_OR_REMOVED;
			  }else if(!parentId.equalsIgnoreCase("uuid")){
				  errorCode=ErrorCode.RESOURCE_NOT_FOUND_OR_REMOVED;
			  }
			  spogServer.userLogin(this.directOrgEmailForPrepare, OrgPwdForPrepare,test);
			  test.log(LogStatus.INFO,"Login as prepared direct admin");
			  spogServer.createAccountFailedWithExpectedStatusCode(parentId, accountOrgId,spogServer.ReturnRandom("spogqa_account")+org_prefix,SpogConstants.RESOURCE_NOT_EXIST,errorCode, test);
			  test.log(LogStatus.INFO,"Direct admin can not create account with parent id is deleted or non/invalid direct org id will be failed");
		  }else if(loginUserType.equalsIgnoreCase("msp")){
			  if(parentId.equalsIgnoreCase("deleted")){
				  parentId = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(mspOrgNameForPrepare)+org_prefix, SpogConstants.MSP_ORG, "", "", "", "",test);
				  test.log(LogStatus.INFO,"Create another msp org");
				  spogServer.DeleteOrganizationWithCheck(parentId, test);
				  test.log(LogStatus.INFO,"Delete the msp org which org id:"+parentId);	
				  errorCode=ErrorCode.INVALID_CREATING_ACCOUNT;
			  }else if(!parentId.equalsIgnoreCase("uuid")){
				  errorCode=ErrorCode.RESOURCE_NOT_FOUND_OR_REMOVED;
			  }
			  spogServer.userLogin(this.mspOrgEmailForPrepare, OrgPwdForPrepare,test);
			  test.log(LogStatus.INFO,"Login as prepared msp admin");
			  spogServer.createAccountFailedWithExpectedStatusCode(parentId, accountOrgId,spogServer.ReturnRandom("spogqa_account")+org_prefix,SpogConstants.RESOURCE_NOT_EXIST,errorCode, test);
			  test.log(LogStatus.INFO,"Msp admin can not create account with parent id is deleted or non/invalid msp org id will be failed");
			  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
			  test.log(LogStatus.INFO,"Login as csr admin");
			  spogServer.createAccountFailedWithExpectedStatusCode(parentId, accountOrgId,spogServer.ReturnRandom("spogqa_account")+org_prefix,SpogConstants.RESOURCE_NOT_EXIST,errorCode ,test);
			  test.log(LogStatus.INFO,"Csr admin can not create account with parent id is deleted or non/invalid msp org id will be failed");
		  }else if(loginUserType.equalsIgnoreCase("account")){
			  expectStatus=SpogConstants.RESOURCE_NOT_EXIST;
			  String parentId_pre= spogServer.createAccountWithCheck(this.mspOrgId,spogServer.ReturnRandom(mspOrgNameForPrepare)+org_prefix ,"",test);
			  test.log(LogStatus.INFO,"Create account successfully which org id:"+parentId_pre);
			  spogServer.createUserAndCheck(accountEmailForPrepare, OrgPwdForPrepare, "dd", "gg", SpogConstants.DIRECT_ADMIN, parentId_pre,test);
			  test.log(LogStatus.INFO,"Create account admin successfully");
			  if(parentId.equalsIgnoreCase("deleted")){
				  parentId= spogServer.createAccountWithCheck(this.mspOrgId,spogServer.ReturnRandom(mspOrgNameForPrepare) +org_prefix,"",test);
				  test.log(LogStatus.INFO,"Create account successfully which org id:"+parentId);
				  spogServer.DeleteOrganizationWithCheck(parentId, test);
				  test.log(LogStatus.INFO,"Delete the direct org which org id:"+parentId);	
				  errorCode=ErrorCode.INVALID_CREATING_ACCOUNT;
			  }else if(!parentId.equalsIgnoreCase("uuid")){
				  errorCode=ErrorCode.RESOURCE_NOT_FOUND_OR_REMOVED;
			  }
			  spogServer.userLogin(accountEmailForPrepare, OrgPwdForPrepare,test);
			  test.log(LogStatus.INFO,"Login as account admin");
			  spogServer.createAccountFailedWithExpectedStatusCode(parentId, accountOrgId,spogServer.ReturnRandom("spogqa_account")+org_prefix, expectStatus, errorCode,test);
			  test.log(LogStatus.INFO,"Account admin can not create account with parent id is deleted or non/invalid Account org id will be failed");
		  }
	  }
	  	  
	  @Test(dataProvider = "nonExistingOrInvalidId")
	  public void CanNotUpdateAccountWithInvalidAccountOrgId(String loginUserType,String accountOrgId){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  String accountEmailForPrepare = spogServer.ReturnRandom("d_jing_spogqa_account_org_prepare@arcserve.com");
		  test.assignAuthor("Shan, Jing");
		  boolean isDel=false;
		  String errorcode=ErrorCode.ELEMENT_NOT_UUID;
		  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
		  test.log(LogStatus.INFO,"Login as csr admin");
		  String accountOrgId1 = spogServer.createAccountWithCheck(this.mspOrgId1, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",test);
		  String accountOrgId2 = spogServer.createAccountWithCheck(this.mspOrgId1, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",test);
		  String parentId = this.mspOrgId1;
		  int expectedStatuscode = SpogConstants.REQUIRED_INFO_NOT_EXIST;
		  
		  spogServer.createUserAndCheck(accountEmailForPrepare, OrgPwdForPrepare, "dd", "gg", SpogConstants.DIRECT_ADMIN, accountOrgId2,test);
		  test.log(LogStatus.INFO,"Create account admin successfully");
		  if(accountOrgId.equalsIgnoreCase("uuid")){
			  errorcode=ErrorCode.RESOURCE_NOT_FOUND_OR_REMOVED;
			  accountOrgId=spogServer.returnRandomUUID();
			  if(loginUserType.equalsIgnoreCase("csr")||(loginUserType.equalsIgnoreCase("msp"))){
				  expectedStatuscode = SpogConstants.RESOURCE_NOT_EXIST;				  
			  }else{
				  expectedStatuscode = SpogConstants.RESOURCE_NOT_EXIST;
				  errorcode=ErrorCode.RESOURCE_NOT_FOUND_OR_REMOVED;
			  }			  
		  }		  
		  if(accountOrgId.equalsIgnoreCase("deleted")){
			  isDel=true;
			  expectedStatuscode = SpogConstants.RESOURCE_NOT_EXIST;
			  spogServer.deleteMSPAccountWithCheck(this.mspOrgId1, accountOrgId1);
			  accountOrgId= accountOrgId1;
			  errorcode=ErrorCode.CAN_NOT_FIND_ORG;
//			  if(loginUserType.equalsIgnoreCase("csr")||(loginUserType.equalsIgnoreCase("msp"))){
//				  expectedStatuscode = SpogConstants.RESOURCE_NOT_EXIST;
//			  }else{
//				  expectedStatuscode = SpogConstants.INSUFFICIENT_PERMISSIONS;
//			  }
			  spogServer.userLogin(this.mspOrgEmailForPrepare, OrgPwdForPrepare,test);
			  test.log(LogStatus.INFO,"Login as another msp admin");
			  spogServer.updateAccountFailedWithExpectedStatusCode(parentId,accountOrgId, spogServer.ReturnRandom("spogqa_account")+org_prefix,SpogConstants.RESOURCE_NOT_EXIST,errorcode, test);
			  test.log(LogStatus.INFO,"Another msp admin can not create account with accoutn org id is deleted or non/invalid org id will be failed");
		  }
		  if (loginUserType.equalsIgnoreCase("direct")){
			  spogServer.userLogin(this.directOrgEmailForPrepare, OrgPwdForPrepare,test);			  
		  }else if(loginUserType.equalsIgnoreCase("msp")){
			  if(isDel){
				  errorcode=ErrorCode.RESOURCE_NOT_FOUND_OR_REMOVED;
			  }
			  spogServer.userLogin(this.mspOrgEmailForPrepare1, OrgPwdForPrepare,test);
		  }else if(loginUserType.equalsIgnoreCase("account")){
			  spogServer.userLogin(accountEmailForPrepare, OrgPwdForPrepare,test);			  
		  }else if(loginUserType.equalsIgnoreCase("csr")){
			  if(isDel){
				  errorcode=ErrorCode.CAN_NOT_FIND_ORG;
			  }
			  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
		  }
		  test.log(LogStatus.INFO,"Login as "+ loginUserType+" admin");
		  spogServer.updateAccountFailedWithExpectedStatusCode(parentId,accountOrgId, spogServer.ReturnRandom("spogqa_account")+org_prefix,expectedStatuscode, errorcode,test);
		  test.log(LogStatus.INFO,loginUserType+" admin can not create account with accoutn org id is deleted or non/invalid org id will be failed");
	  }
	  	  
	  @Test
	  public void notLoggedUpdateAccount(){	
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  //set token as null
		  spogServer.setToken("");
		  spogServer.updateAccountFailedWithExpectedStatusCode(this.mspOrgId,this.accountOrgId, spogServer.ReturnRandom("spogqa_account")+org_prefix,SpogConstants.NOT_LOGGED_IN, ErrorCode.AUTHORIZATION_HEADER_BLANK,test);
		  spogServer.updateAccountFailedWithExpectedStatusCode(this.directOrgId,this.accountOrgId, spogServer.ReturnRandom("spogqa_account")+org_prefix,SpogConstants.NOT_LOGGED_IN, ErrorCode.AUTHORIZATION_HEADER_BLANK,test);
		  spogServer.updateAccountFailedWithExpectedStatusCode(this.csrOrgId,this.accountOrgId, spogServer.ReturnRandom("spogqa_account")+org_prefix,SpogConstants.NOT_LOGGED_IN, ErrorCode.AUTHORIZATION_HEADER_BLANK,test);
		  test.log(LogStatus.INFO,"If not login can not update account");		
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
