package api.users.sourcefilters;

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

import Constants.ErrorCode;
import Constants.SpogConstants;
import InvokerServer.GatewayServer;
import InvokerServer.Org4SPOGServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class EnhanceCreateSourceFilterTest extends base.prepare.Is4Org{
	  private SPOGServer spogServer;
	  private Org4SPOGServer org4SPOGServer;
	  private GatewayServer gatewayServer;
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
		  gatewayServer = new GatewayServer(baseURI, port);
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
		  this.mspOrgId = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(mspOrgNameForPrepare)+org_prefix, SpogConstants.MSP_ORG, mspOrgEmailForPrepare, this.OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare);
		  this.mspOrgId1 = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(mspOrgNameForPrepare)+org_prefix, SpogConstants.MSP_ORG, mspOrgEmailForPrepare1, this.OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare);
		  this.rootmspOrgId = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(mspOrgNameForPrepare)+org_prefix, SpogConstants.MSP_ORG, rootmspOrgEmailForPrepare, this.OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare);
		  org4SPOGServer.setToken(spogServer.getJWTToken());
		  org4SPOGServer.convertToRootMSP(rootmspOrgId);
		  this.rootmspaccountOrgId= spogServer.createAccountWithCheck(this.rootmspOrgId, spogServer.ReturnRandom("spogqa_account")+org_prefix,"");
		  this.rootmspaccountOrgId1= spogServer.createAccountWithCheck(this.rootmspOrgId, spogServer.ReturnRandom("spogqa_account")+org_prefix,"");
		  
		  this.submspOrgId=org4SPOGServer.createSubMSPAccountincc(spogServer.ReturnRandom(mspOrgNameForPrepare)+org_prefix, rootmspOrgId, OrgFistNameForPrepare, OrgLastNameForPrepare, submspOrgEmailForPrepare, "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
		  this.submsp1OrgId=org4SPOGServer.createSubMSPAccountincc(spogServer.ReturnRandom(mspOrgNameForPrepare)+org_prefix, rootmspOrgId, OrgFistNameForPrepare, OrgLastNameForPrepare, submsp1OrgEmailForPrepare, "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
		  this.submspOrgEmailForPrepare= spogServer.ReturnRandom("d_jing_spogqa_submsp_org_prepar@arcserve.come");
		  this.submsp1OrgEmailForPrepare= spogServer.ReturnRandom("d_jing_spogqa_submsp1_org_prepar@arcserve.come");
		  spogServer.createUserAndCheck(submspOrgEmailForPrepare, this.OrgPwdForPrepare,  OrgFistNameForPrepare, OrgLastNameForPrepare, "msp_admin", submspOrgId, test);
		  spogServer.createUserAndCheck(submsp1OrgEmailForPrepare, this.OrgPwdForPrepare,  OrgFistNameForPrepare, OrgLastNameForPrepare, "msp_admin", submsp1OrgId, test);
		  this.submspaccountOrgId= spogServer.createAccountWithCheck(this.submspOrgId, spogServer.ReturnRandom("spogqa_account")+org_prefix,"");
		  this.submspaccountOrgId1= spogServer.createAccountWithCheck(this.submspOrgId, spogServer.ReturnRandom("spogqa_account")+org_prefix,"");
		  this.submsp1accountOrgId= spogServer.createAccountWithCheck(this.submsp1OrgId, spogServer.ReturnRandom("spogqa_account")+org_prefix,"");
		  this.submsp1accountOrgId1= spogServer.createAccountWithCheck(this.submsp1OrgId, spogServer.ReturnRandom("spogqa_account")+org_prefix,"");
		  
		  this.accountOrgId= spogServer.createAccountWithCheck(this.mspOrgId, spogServer.ReturnRandom("spogqa_account")+org_prefix,"");
		  this.accountOrgId1= spogServer.createAccountWithCheck(this.mspOrgId, spogServer.ReturnRandom("spogqa_account")+org_prefix,"");
		  this.directOrgId = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(directOrgNameForPrepare)+org_prefix, SpogConstants.DIRECT_ORG, directOrgEmailForPrepare, this.OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare);
		  this.directOrgId1 = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(directOrgNameForPrepare)+org_prefix, SpogConstants.DIRECT_ORG, directOrgEmailForPrepare1, this.OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare);
		  spogServer.createUserAndCheck(this.accountEmailForPrepare, this.OrgPwdForPrepare, "dd", "gg", SpogConstants.DIRECT_ADMIN, accountOrgId,test);
		  spogServer.createUserAndCheck(this.accountEmailForPrepare1, this.OrgPwdForPrepare, "dd", "gg", SpogConstants.DIRECT_ADMIN, accountOrgId1,test);
		  spogServer.createUserAndCheck(this.submspaccountEmailForPrepare, this.OrgPwdForPrepare, "dd", "gg", SpogConstants.DIRECT_ADMIN, submspaccountOrgId,test);
		  spogServer.createUserAndCheck(this.submspaccountEmailForPrepare1, this.OrgPwdForPrepare, "dd", "gg", SpogConstants.DIRECT_ADMIN, submspaccountOrgId1,test);
		  spogServer.createUserAndCheck(this.rootmspaccountEmailForPrepare, this.OrgPwdForPrepare, "dd", "gg", SpogConstants.DIRECT_ADMIN, rootmspaccountOrgId,test);
		  spogServer.createUserAndCheck(this.rootmspaccountEmailForPrepare1, this.OrgPwdForPrepare, "dd", "gg", SpogConstants.DIRECT_ADMIN, rootmspaccountOrgId1,test);
		  spogServer.createUserAndCheck(this.submsp1accountEmailForPrepare, this.OrgPwdForPrepare, "dd", "gg", SpogConstants.DIRECT_ADMIN, submsp1accountOrgId,test);
		  spogServer.createUserAndCheck(this.submsp1accountEmailForPrepare1, this.OrgPwdForPrepare, "dd", "gg", SpogConstants.DIRECT_ADMIN, submsp1accountOrgId1,test);
		  
		  spogServer.userLogin(this.accountEmailForPrepare, this.OrgPwdForPrepare);
		  this.account_user_id1=spogServer.GetLoggedinUser_UserID();
		  spogServer.userLogin(this.accountEmailForPrepare1, this.OrgPwdForPrepare);
		  this.account_user_id2=spogServer.GetLoggedinUser_UserID();
		  spogServer.userLogin(this.csrAdmin, this.csrPwd);
		  mspAccountAdminEmailForPrepare = spogServer.ReturnRandom("d_jing_spogqa_msp_org_prepar_jing@arcserve.come");
		  rootmspAccountAdminEmailForPrepare = spogServer.ReturnRandom("d_jing_spogqa_msp_org_prepar_jing@arcserve.come");
		  submspAccountAdminEmailForPrepare = spogServer.ReturnRandom("d_jing_spogqa_msp_org_prepar_jing@arcserve.come");
		  submsp1AccountAdminEmailForPrepare = spogServer.ReturnRandom("d_jing_spogqa_msp_org_prepar_jing@arcserve.come");
		  String csrToken= spogServer.getJWTToken();
		  mspAccountAdminUserIds[0]=spogServer.createUserAndCheck(mspAccountAdminEmailForPrepare, this.OrgPwdForPrepare, "ff", "ff", "msp_account_admin", mspOrgId, test);
		  rootmspAccountAdminUserIds[0]=spogServer.createUserAndCheck(rootmspAccountAdminEmailForPrepare, this.OrgPwdForPrepare, "ff", "ff", "msp_account_admin", rootmspOrgId, test);
		  submspAccountAdminUserIds[0]=spogServer.createUserAndCheck(submspAccountAdminEmailForPrepare, this.OrgPwdForPrepare, "ff", "ff", "msp_account_admin", submspOrgId, test);
		  submsp1AccountAdminUserIds[0]=spogServer.createUserAndCheck(submsp1AccountAdminEmailForPrepare, this.OrgPwdForPrepare, "ff", "ff", "msp_account_admin", submsp1OrgId, test);
		  spogServer.userLogin(mspAccountAdminEmailForPrepare, this.OrgPwdForPrepare);
		  this.msp_account_admin_token = spogServer.getJWTToken();
		  
		  Response response = userSpogServer.assignMspAccountAdmins(mspOrgId, accountOrgId1, mspAccountAdminUserIds, csrToken);
		  userSpogServer.assignMspAccountAdminsWithCheck(response, mspAccountAdminUserIds, csrToken);
		  response = userSpogServer.assignMspAccountAdmins(rootmspOrgId, rootmspaccountOrgId1, rootmspAccountAdminUserIds, csrToken);
		  userSpogServer.assignMspAccountAdminsWithCheck(response, rootmspAccountAdminUserIds, csrToken);
		  response = userSpogServer.assignMspAccountAdmins(submspOrgId, submspaccountOrgId1, submspAccountAdminUserIds, csrToken);
		  userSpogServer.assignMspAccountAdminsWithCheck(response, submspAccountAdminUserIds, csrToken);
		  response = userSpogServer.assignMspAccountAdmins(submsp1OrgId, submsp1accountOrgId1, submsp1AccountAdminUserIds, csrToken);
		  userSpogServer.assignMspAccountAdminsWithCheck(response, submsp1AccountAdminUserIds, csrToken);
		  spogServer.userLogin(this.mspAccountAdminEmailForPrepare, this.OrgPwdForPrepare);
	  }
	  
	  
	  
	  @DataProvider(name = "loginUserToCreateDifferentFilterItems")
	  public final Object[][] getLoginUserToCreateDifferentFilterItems() {
			return new Object[][] {  
				{"account"},{"csr"},{"direct"},{"msp"},
				{"submsp"},{"rootmsp"},{"rootmspaccount"},{"submspaccount"},
				
			};
	  }
	  @Test(dataProvider = "loginUserToCreateDifferentFilterItems")
	  public void createFilterWithDifferentFilterItems(String loginUserType){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  String user_id;
		  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
		  test.log(LogStatus.INFO,"Login as csr admin");	
		  String errorCode = ErrorCode.NOT_FOUND_RESOURCE_ID;
		  String tem_filter_name = spogServer.returnRandomUUID();
		  String protection_policy = spogServer.returnRandomUUID();
		  String source_group = spogServer.returnRandomUUID();
		  String[] connect_array= SpogConstants.CONNECTION_STATUS.split(",");
		  String[] protection_array= SpogConstants.PROTETION_STATUS.split(",");
		  String[] backup_array= SpogConstants.BACKUP_STATUS.split(",");
		  String[] operating_array= SpogConstants.OPERATING_SYSTEM.split(",");
		  String[] application_array= SpogConstants.APPLICATIONS.split(",");
		  if (loginUserType.equalsIgnoreCase("csr")){
			  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
		  }else if(loginUserType.equalsIgnoreCase("csr_read_only")){
			  spogServer.userLogin(this.csr_readonly, this.OrgPwdForPrepare,test);
		  }else if(loginUserType.equalsIgnoreCase("direct")){
			  spogServer.userLogin(this.directOrgEmailForPrepare, this.OrgPwdForPrepare,test);
		  }else if(loginUserType.equalsIgnoreCase("msp")){
			  spogServer.userLogin(this.mspOrgEmailForPrepare, this.OrgPwdForPrepare,test);
		  }else if(loginUserType.equalsIgnoreCase("account")){
			  spogServer.userLogin(this.accountEmailForPrepare, this.OrgPwdForPrepare,test);
		  }else if(loginUserType.equalsIgnoreCase("rootmsp")){
			  spogServer.userLogin(this.rootmspOrgEmailForPrepare, this.OrgPwdForPrepare,test);			  
		  }else if(loginUserType.equalsIgnoreCase("submsp")){
			  spogServer.userLogin(this.submspOrgEmailForPrepare, this.OrgPwdForPrepare,test);			  
		  }else if(loginUserType.equalsIgnoreCase("rootmspaccount")){
			  spogServer.userLogin(this.rootmspaccountEmailForPrepare, this.OrgPwdForPrepare,test);			  
		  }else if(loginUserType.equalsIgnoreCase("rootmsp_account")){
			  spogServer.userLogin(this.rootmspAccountAdminEmailForPrepare, this.OrgPwdForPrepare);
		  }else if(loginUserType.equalsIgnoreCase("submspaccount")){
			  spogServer.userLogin(this.submspaccountEmailForPrepare, this.OrgPwdForPrepare,test);
		  }else if(loginUserType.equalsIgnoreCase("submsp_account")){
			  spogServer.userLogin(this.submspAccountAdminEmailForPrepare, this.OrgPwdForPrepare);
		  }
		  user_id=spogServer.GetLoggedinUser_UserID();
		  String userToken = spogServer.getJWTToken();
		  String organizationID=spogServer.GetLoggedinUserOrganizationID();
		  String prefix = RandomStringUtils.randomAlphanumeric(8);
		  String sourceName = "sourceName";
		  String sourceType = "machine";
		  String siteID = gatewayServer.createsite_register_login(organizationID, userToken, user_id, prefix + "enhance",
					"1.0.0", spogServer, test);
		  test.log(LogStatus.INFO,"Login as "+loginUserType+" admin");	
		  if(connect_array.length>0){
			  for(int connect_i=0;connect_i<connect_array.length;connect_i++){
				  if(protection_array.length>0){
					  for(int protection_i=0;protection_i<protection_array.length;protection_i++){
						  if(backup_array.length>0){
							  for(int backup_i=0;backup_i<backup_array.length;backup_i++){
								  if(operating_array.length>0){
									  for(int operating_i=0;operating_i<operating_array.length;operating_i++){
										  if(application_array.length>0){
											  for(int application_i=0;application_i<application_array.length;application_i++){
												  tem_filter_name = spogServer.returnRandomUUID();
												  spogServer.createFilterwithCheck(user_id, tem_filter_name, protection_array[protection_i],connect_array[connect_i],protection_policy,backup_array[backup_i], source_group, operating_array[operating_i], application_array[application_i],siteID,sourceName,sourceType,"false", test);
											  }
										  }
									  }
								  }
							  }
						  }
					  }
				  }
			  }
		  }
	  }

	  @Test
	  public void notLoggedCreateFilter(){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  //set token as null
		  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
		  test.log(LogStatus.INFO,"Login as csr admin");	
		  String user_id=spogServer.GetLoggedinUser_UserID();
		  spogServer.setToken("");
		  spogServer.createFilterFailedWithExpectedStatusCode(user_id, "dd", "none", "none", "none", "none", "none", "none", "none","none", "none",  SpogConstants.NOT_LOGGED_IN, ErrorCode.AUTHORIZATION_HEADER_BLANK,test);
	  }
	  
	  //@Test
	  public void csrReadOnlyNotCreateFilter(){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  //set token as null
		  spogServer.userLogin(this.csr_readonly, "Caworld_2017",test);
		  test.log(LogStatus.INFO,"Login as csr read only");	
		  String user_id=spogServer.GetLoggedinUser_UserID();
		  spogServer.createFilterwithCheck(user_id, "dd", "none", "none", "none", "none", "none", "none", "none","none", "none", test);
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
