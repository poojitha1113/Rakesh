package api.users.logsfilters;

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
import InvokerServer.GatewayServer;
import InvokerServer.Log4SPOGServer;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class EnhanceUpdateLogFilterTest extends base.prepare.PrepareOrgInfo{
	@Parameters({ "pmfKey"})
	public EnhanceUpdateLogFilterTest(String pmfKey) {
		super(pmfKey);
		// TODO Auto-generated constructor stub
	}
	  private GatewayServer gatewayServer;
	  private SPOGServer spogServer;
	  private String directOrgId,directOrgId1;
	  private String mspOrgId;
	  private String mspOrgId1;
	  private String accountOrgId,accountOrgId1;
	  private String csrAdmin;
	  private String csrPwd;
	  private String site_version="1.0.0";
	  private String gateway_hostname="kiran";
	  
	  //this is creating msp org or direct org for preparation
	  private String mspOrgNameForPrepare="d_jing_spogqa_msp_org_prepare";
	  private String csrOrgNameForPrepare="d_jing_spogqa_csr_org_prepare";
	  private String csrOrgEmailForPrepare,mspOrgEmailForPrepare,mspOrgEmailForPrepare1,accountEmailForPrepare,accountEmailForPrepare1;
	  private String directOrgNameForPrepare="d_jing_spogqa_direct_org_prepare";
	  private String directOrgEmailForPrepare,directOrgEmailForPrepare1;
	  private String OrgFistNameForPrepare="jing";
	  private String OrgLastNameForPrepare="org_prepare";
	  private String OrgPwdForPrepare="Welcome*02";
	  private String csrOrgId;
	  
	  private String  org_prefix=this.getClass().getSimpleName();
	  private ExtentTest test;
	  private String mspAccountAdminEmailForPrepare="d_jing_spogqa_msp_account_org_prepar_jing@arcserve.come";
	  private String[] mspAccountAdminUserIds=new String[1];
	  private UserSpogServer userSpogServer;
	  //this is for update portal, each testng class is taken as BQ set
//	  private ExtentReports rep;
//	  private SQLServerDb bqdb1;
//	  public int Nooftest;
//	  private long creationTime;
//	  private String BQName=null;
//	  private String runningMachine;
//	  private testcasescount count1;
//	  private String buildVersion;
	  long startTime =System.currentTimeMillis();
	  private Log4SPOGServer log4SPOGServer;
	  String spogToken;
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
				String sbaseURI="http://tccapi.arcserve.com";
				bqdb1.updateTable(this.BQName, runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0", String.valueOf(Nooftest), count1.getcreationtime(), "InProgress",author+" and Rest server is "+sbaseURI.split("//")[1]);
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
		  log4SPOGServer=new Log4SPOGServer(baseURI, port);
		  gatewayServer =new GatewayServer(baseURI,port);
		  rep = ExtentManager.getInstance("CreateAccountTest",logFolder);
		  test = rep.startTest("beforeClass");		  
		  this.csrAdmin = csrAdminUserName;
		  this.csrPwd = csrAdminPassword;
		  this.accountEmailForPrepare = spogServer.ReturnRandom("d_jing_spogqa_account_org_prepare@arcserve.com");
		  this.accountEmailForPrepare1 = spogServer.ReturnRandom("d_jing_spogqa_account_org_prepare@arcserve.com");
		  this.csrOrgEmailForPrepare = spogServer.ReturnRandom("d_jing_spogqa_csr_org_prepar@arcserve.come");
		  this.mspOrgEmailForPrepare = spogServer.ReturnRandom("d_jing_spogqa_msp_org_prepar@arcserve.come");
		  this.mspOrgEmailForPrepare1 = spogServer.ReturnRandom("d_jing_spogqa_msp_org_prepare@arcserve.com");
		  this.directOrgEmailForPrepare = spogServer.ReturnRandom("d_jing_spogqa_direct_org_prepare@arcserve.com");
		  this.directOrgEmailForPrepare1 = spogServer.ReturnRandom("d_jing_spogqa_direct_org_prepare@arcserve.com");
		  spogServer.userLogin(this.csrAdmin, this.csrPwd);
		  //this.csrOrgId=spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(csrOrgNameForPrepare), SpogConstants.CSR_ORG, csrOrgEmailForPrepare, this.OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare);
		  this.mspOrgId = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(mspOrgNameForPrepare)+org_prefix, SpogConstants.MSP_ORG, mspOrgEmailForPrepare, this.OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare);
		  this.mspOrgId1 = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(mspOrgNameForPrepare)+org_prefix, SpogConstants.MSP_ORG, mspOrgEmailForPrepare1, this.OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare);
		  this.accountOrgId= spogServer.createAccountWithCheck(this.mspOrgId, spogServer.ReturnRandom("spogqa_account")+org_prefix,"");
		  this.accountOrgId1= spogServer.createAccountWithCheck(this.mspOrgId, spogServer.ReturnRandom("spogqa_account")+org_prefix,"");
		  this.directOrgId = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(directOrgNameForPrepare)+org_prefix, SpogConstants.DIRECT_ORG, directOrgEmailForPrepare, this.OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare);
		  this.directOrgId1 = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(directOrgNameForPrepare)+org_prefix, SpogConstants.DIRECT_ORG, directOrgEmailForPrepare1, this.OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare);
		  spogServer.createUserAndCheck(this.accountEmailForPrepare, this.OrgPwdForPrepare, "dd", "gg", SpogConstants.DIRECT_ADMIN, accountOrgId,test);
		  spogServer.createUserAndCheck(this.accountEmailForPrepare1, this.OrgPwdForPrepare, "dd", "gg", SpogConstants.DIRECT_ADMIN, accountOrgId1,test);
		  prepare(baseURI, port, logFolder, csrAdminUserName, csrAdminPassword, this.getClass().getSimpleName() );
		  mspAccountAdminEmailForPrepare = spogServer.ReturnRandom("d_jing_spogqa_msp_org_prepar_jing@arcserve.come");
		  String csrToken= spogServer.getJWTToken();
		  mspAccountAdminUserIds[0]=spogServer.createUserAndCheck(mspAccountAdminEmailForPrepare, this.OrgPwdForPrepare, "ff", "ff", "msp_account_admin", mspOrgId, test);
		  spogServer.userLogin(mspAccountAdminEmailForPrepare, this.OrgPwdForPrepare);
		  userSpogServer = new UserSpogServer(baseURI, port);
		  Response response = userSpogServer.assignMspAccountAdmins(mspOrgId, accountOrgId1, mspAccountAdminUserIds, csrToken);
		  response = userSpogServer.assignMspAccountAdmins(mspOrgId, accountOrgId, mspAccountAdminUserIds, csrToken);
		  userSpogServer.assignMspAccountAdminsWithCheck(response, mspAccountAdminUserIds, csrToken);
		  spogServer.userLogin(this.mspAccountAdminEmailForPrepare, this.OrgPwdForPrepare);
		  
	  }
	  
	  @DataProvider(name = "loginUserToCreateDifferentMessageId")
	  public final Object[][] getLoginUserToCreateDifferentMessageId() {
			return new Object[][] { {"csr",""},{"csr",null},{"csr","emptyarray"},{"csr","none"},{"csr","random"},
				{"msp",""},{"msp",null},{"msp","emptyarray"},{"msp","none"},{"msp","random"},
				{"direct",""},{"direct",null},{"direct","emptyarray"},{"direct","none"},{"direct","random"},
				{"account",""},{"account",null},{"account","emptyarray"}};
	  }
	  @Test(dataProvider = "loginUserToCreateDifferentMessageId")
	  public void createFilterWithDifferentMessageId(String loginUserType,String messageId){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  String user_id;
		  String filter_name=spogServer.returnRandomUUID();
		  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
		  test.log(LogStatus.INFO,"Login as csr admin");	
		  String errorCode = ErrorCode.NOT_FOUND_RESOURCE_ID;
		  String serverity="information";
		  String no_permission_id,permission_id;
		  String[] no_permission_users_id=null;
		  String[] permission_users_id=null;
		  test.log(LogStatus.INFO,"Login as csr admin");	
		  if (loginUserType.equalsIgnoreCase("csr")){
			  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
			  no_permission_id=mspOrgEmailForPrepare+","+directOrgEmailForPrepare+","+accountEmailForPrepare;
			  no_permission_users_id=no_permission_id.split(",");			  
		  }else if(loginUserType.equalsIgnoreCase("direct")){
			  spogServer.userLogin(this.directOrgEmailForPrepare, this.OrgPwdForPrepare,test);
			  no_permission_id=mspOrgEmailForPrepare+","+directOrgEmailForPrepare1+","+accountEmailForPrepare;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.directOrgEmailForPrepare;
			  permission_users_id=permission_id.split(",");	
		  }else if(loginUserType.equalsIgnoreCase("msp")){
			  spogServer.userLogin(this.mspOrgEmailForPrepare, this.OrgPwdForPrepare,test);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+accountEmailForPrepare;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.mspOrgEmailForPrepare;
			  permission_users_id=permission_id.split(",");
		  }else if(loginUserType.equalsIgnoreCase("account")){
			  spogServer.userLogin(this.accountEmailForPrepare, this.OrgPwdForPrepare,test);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+directOrgEmailForPrepare1+","+accountEmailForPrepare1;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.accountEmailForPrepare;
			  permission_users_id=permission_id.split(",");
		  }else if(loginUserType.equalsIgnoreCase("rootmsp")){
			  spogServer.userLogin(this.final_root_msp_user_name_email, this.OrgPwdForPrepare,test);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+accountEmailForPrepare+","+this.final_sub_msp1_user_name_email+","+this.final_sub_msp1_msp_account_user_name_email+","+this.final_root_msp_direct_org1_user_email;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.final_root_msp_user_name_email;
			  permission_users_id=permission_id.split(",");
		  }else if(loginUserType.equalsIgnoreCase("submsp")){
			  spogServer.userLogin(this.final_sub_msp1_user_name_email, this.OrgPwdForPrepare,test);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+accountEmailForPrepare+","+this.final_root_msp_user_name_email+","+this.final_sub_msp2_account1_user_email+","+this.final_sub_msp2_user_name_email+","+this.final_root_msp_direct_org1_user_email;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.final_sub_msp1_user_name_email;
			  permission_users_id=permission_id.split(",");
		  }else if(loginUserType.equalsIgnoreCase("rootmspaccount")){
			  spogServer.userLogin(this.final_root_msp_direct_org1_user_email, this.OrgPwdForPrepare,test);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+directOrgEmailForPrepare1+","+accountEmailForPrepare1+","+this.final_root_msp_user_name_email+","+this.final_sub_msp2_account1_user_email+","+this.final_sub_msp2_user_name_email+","+this.final_root_msp_direct_org_user_email;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.final_root_msp_direct_org1_user_email;
			  permission_users_id=permission_id.split(",");
		  }else if(loginUserType.equalsIgnoreCase("rootmsp_account")){
			  spogServer.userLogin(this.final_root_msp_account_admin_user_name_email, this.OrgPwdForPrepare);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+directOrgEmailForPrepare1+","+accountEmailForPrepare1+","+this.final_root_msp_user_name_email+","+this.final_sub_msp2_account1_user_email+","+this.final_sub_msp2_user_name_email+","+this.final_root_msp_direct_org1_user_email;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.final_root_msp_account_admin_user_name_email;
			  permission_users_id=permission_id.split(",");
		  }else if(loginUserType.equalsIgnoreCase("submspaccount")){
			  spogServer.userLogin(this.final_sub_msp2_account1_user_email, this.OrgPwdForPrepare,test);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+directOrgEmailForPrepare1+","+accountEmailForPrepare1+","+this.final_root_msp_user_name_email+","+this.final_sub_msp1_account1_user_email+","+this.final_sub_msp1_user_name_email+","+this.final_root_msp_direct_org1_user_email;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.final_sub_msp2_account1_user_email;
			  permission_users_id=permission_id.split(",");
		  }else if(loginUserType.equalsIgnoreCase("submsp_account")){
			  spogServer.userLogin(this.final_sub_msp1_msp_account_user_name_email, this.OrgPwdForPrepare);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+directOrgEmailForPrepare1+","+accountEmailForPrepare1+","+this.final_root_msp_user_name_email+","+this.final_sub_msp2_account1_user_email+","+this.final_sub_msp2_user_name_email+","+this.final_root_msp_direct_org1_user_email;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.final_sub_msp1_msp_account_user_name_email;
			  permission_users_id=permission_id.split(",");
		  }
		  user_id=spogServer.GetLoggedinUser_UserID();
		  String expected_organization_id=spogServer.GetOrganizationIDforUser(user_id);
		  spogToken=spogServer.getJWTToken();
		  log4SPOGServer.setToken(spogToken);
		  String pre_filter_name=spogServer.returnRandomUUID();
		  String filter_id=log4SPOGServer.createLogFilterwithCheck(user_id, expected_organization_id, pre_filter_name, null, null, "none", "none", "false",test);
		  test.log(LogStatus.INFO,"Login as "+loginUserType+" admin");	
		  if(messageId!=null && messageId.equalsIgnoreCase("invalidString")){
			  //String[] timeRange ={"2018-01-01 00:00:00","2018-01-18 00:00:00"};
			  errorCode=ErrorCode.NOT_VALID_ENUM;
			  log4SPOGServer.updateLogFilterFailedWithExpectedStatusCode(user_id,filter_id ,expected_organization_id, "", null,  null,"none", serverity, "false",messageId,"none","none","none","none",SpogConstants.REQUIRED_INFO_NOT_EXIST, errorCode,test);
			  test.log(LogStatus.INFO,loginUserType+ " admin create log filter with message Id is "+messageId+" failed"); 
		  }else{
			  log4SPOGServer.updateLogFilterwithCheck(user_id, filter_id ,expected_organization_id, pre_filter_name, null, null, "none", serverity, "false",messageId,"none","none","none","none",test);
			  test.log(LogStatus.INFO,loginUserType+ " admin create log filter with message Id is "+messageId); 
		  }
		  if(permission_users_id!=null){
			  for(int i=0;i<permission_users_id.length;i++){
				  spogServer.userLogin(permission_users_id[i], this.OrgPwdForPrepare,test);
				  spogToken=spogServer.getJWTToken();
				  log4SPOGServer.setToken(spogToken);
				  log4SPOGServer.updateLogFilterwithCheck(user_id, filter_id,expected_organization_id, filter_name+"dd", "custom", null, "backup_incremental", "error",  "false",test);
				  //log4SPOGServer.updateLogFilterwithCheck(user_id, filter_id,expected_organization_id, filter_name+"dd", log_tsArray_update, "incremental_backup", "error",  "false",test);
				  test.log(LogStatus.INFO,permission_users_id[i]+" can update others' filter"); 
			  }	
		  }
		  if(no_permission_users_id!=null){
			  for(int i=0;i<no_permission_users_id.length;i++){
				  spogServer.userLogin(no_permission_users_id[i], this.OrgPwdForPrepare,test);
				  spogToken=spogServer.getJWTToken();
				  log4SPOGServer.setToken(spogToken);
				  log4SPOGServer.updateLogFilterFailedWithExpectedStatusCode(user_id,filter_id ,expected_organization_id, "", null, null, "none", serverity, "false","none","none","none","none","none",SpogConstants.INSUFFICIENT_PERMISSIONS, "00100101",test);
				  //log4SPOGServer.updateLogFilterwithCheck(user_id, filter_id,expected_organization_id, filter_name+"dd", log_tsArray_update, "incremental_backup", "error",  "false",test);

			  }	
		  }
		  
	  }
	  
	  @Test(dataProvider = "loginUserToCreateDifferentMessageId")
	  public void createFilterWithDifferentMessage(String loginUserType,String message){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  String user_id;
		  String filter_name=spogServer.returnRandomUUID();
		  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
		  test.log(LogStatus.INFO,"Login as csr admin");	
		  String errorCode = ErrorCode.NOT_FOUND_RESOURCE_ID;
		  String serverity="information";
		  String no_permission_id,permission_id;
		  String[] no_permission_users_id=null;
		  String[] permission_users_id=null;
		  test.log(LogStatus.INFO,"Login as csr admin");	
		  if (loginUserType.equalsIgnoreCase("csr")){
			  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
			  no_permission_id=mspOrgEmailForPrepare+","+directOrgEmailForPrepare+","+accountEmailForPrepare;
			  no_permission_users_id=no_permission_id.split(",");			  
		  }else if(loginUserType.equalsIgnoreCase("direct")){
			  spogServer.userLogin(this.directOrgEmailForPrepare, this.OrgPwdForPrepare,test);
			  no_permission_id=mspOrgEmailForPrepare+","+directOrgEmailForPrepare1+","+accountEmailForPrepare;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.directOrgEmailForPrepare;
			  permission_users_id=permission_id.split(",");	
		  }else if(loginUserType.equalsIgnoreCase("msp")){
			  spogServer.userLogin(this.mspOrgEmailForPrepare, this.OrgPwdForPrepare,test);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+accountEmailForPrepare;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.mspOrgEmailForPrepare;
			  permission_users_id=permission_id.split(",");
		  }else if(loginUserType.equalsIgnoreCase("account")){
			  spogServer.userLogin(this.accountEmailForPrepare, this.OrgPwdForPrepare,test);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+directOrgEmailForPrepare1+","+accountEmailForPrepare1;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.accountEmailForPrepare;
			  permission_users_id=permission_id.split(",");
		  }else if(loginUserType.equalsIgnoreCase("rootmsp")){
			  spogServer.userLogin(this.final_root_msp_user_name_email, this.OrgPwdForPrepare,test);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+accountEmailForPrepare+","+this.final_sub_msp1_user_name_email+","+this.final_sub_msp1_msp_account_user_name_email+","+this.final_root_msp_direct_org1_user_email;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.final_root_msp_user_name_email;
			  permission_users_id=permission_id.split(",");
		  }else if(loginUserType.equalsIgnoreCase("submsp")){
			  spogServer.userLogin(this.final_sub_msp1_user_name_email, this.OrgPwdForPrepare,test);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+accountEmailForPrepare+","+this.final_root_msp_user_name_email+","+this.final_sub_msp2_account1_user_email+","+this.final_sub_msp2_user_name_email+","+this.final_root_msp_direct_org1_user_email;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.final_sub_msp1_user_name_email;
			  permission_users_id=permission_id.split(",");
		  }else if(loginUserType.equalsIgnoreCase("rootmspaccount")){
			  spogServer.userLogin(this.final_root_msp_direct_org1_user_email, this.OrgPwdForPrepare,test);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+directOrgEmailForPrepare1+","+accountEmailForPrepare1+","+this.final_root_msp_user_name_email+","+this.final_sub_msp2_account1_user_email+","+this.final_sub_msp2_user_name_email+","+this.final_root_msp_direct_org_user_email;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.final_root_msp_direct_org1_user_email;
			  permission_users_id=permission_id.split(",");
		  }else if(loginUserType.equalsIgnoreCase("rootmsp_account")){
			  spogServer.userLogin(this.final_root_msp_account_admin_user_name_email, this.OrgPwdForPrepare);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+directOrgEmailForPrepare1+","+accountEmailForPrepare1+","+this.final_root_msp_user_name_email+","+this.final_sub_msp2_account1_user_email+","+this.final_sub_msp2_user_name_email+","+this.final_root_msp_direct_org1_user_email;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.final_root_msp_account_admin_user_name_email;
			  permission_users_id=permission_id.split(",");
		  }else if(loginUserType.equalsIgnoreCase("submspaccount")){
			  spogServer.userLogin(this.final_sub_msp2_account1_user_email, this.OrgPwdForPrepare,test);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+directOrgEmailForPrepare1+","+accountEmailForPrepare1+","+this.final_root_msp_user_name_email+","+this.final_sub_msp1_account1_user_email+","+this.final_sub_msp1_user_name_email+","+this.final_root_msp_direct_org1_user_email;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.final_sub_msp2_account1_user_email;
			  permission_users_id=permission_id.split(",");
		  }else if(loginUserType.equalsIgnoreCase("submsp_account")){
			  spogServer.userLogin(this.final_sub_msp1_msp_account_user_name_email, this.OrgPwdForPrepare);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+directOrgEmailForPrepare1+","+accountEmailForPrepare1+","+this.final_root_msp_user_name_email+","+this.final_sub_msp2_account1_user_email+","+this.final_sub_msp2_user_name_email+","+this.final_root_msp_direct_org1_user_email;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.final_sub_msp1_msp_account_user_name_email;
			  permission_users_id=permission_id.split(",");
		  }
		  user_id=spogServer.GetLoggedinUser_UserID();
		  String expected_organization_id=spogServer.GetOrganizationIDforUser(user_id);
		  spogToken=spogServer.getJWTToken();
		  log4SPOGServer.setToken(spogToken);
		  String pre_filter_name=spogServer.returnRandomUUID();
		  String filter_id=log4SPOGServer.createLogFilterwithCheck(user_id, expected_organization_id, pre_filter_name,  null,null, "none", "none", "false",test);
		  test.log(LogStatus.INFO,"Login as "+loginUserType+" admin");	
		  if(message!=null && message.equalsIgnoreCase("invalidString")){
			  //String[] timeRange ={"2018-01-01 00:00:00","2018-01-18 00:00:00"};
			  errorCode=ErrorCode.NOT_VALID_ENUM;
			  log4SPOGServer.updateLogFilterFailedWithExpectedStatusCode(user_id,filter_id ,expected_organization_id, "", null, null, "none", serverity, "false","none","none",message,"none","none",SpogConstants.REQUIRED_INFO_NOT_EXIST, errorCode,test);
			  test.log(LogStatus.INFO,loginUserType+ " admin create log filter with message is "+message+" failed"); 
		  }else{
			  log4SPOGServer.updateLogFilterwithCheck(user_id, filter_id ,expected_organization_id, pre_filter_name, null, null, "none", serverity, "false","none","none",message,"none","none",test);
			  test.log(LogStatus.INFO,loginUserType+ " admin create log filter with message is "+message); 
		  }
		  if(permission_users_id!=null){
			  for(int i=0;i<permission_users_id.length;i++){
				  spogServer.userLogin(permission_users_id[i], this.OrgPwdForPrepare,test);
				  spogToken=spogServer.getJWTToken();
				  log4SPOGServer.setToken(spogToken);
				  log4SPOGServer.updateLogFilterwithCheck(user_id, filter_id,expected_organization_id, filter_name+"dd", "custom", null, "backup_incremental", "error",  "false",test);
				  //log4SPOGServer.updateLogFilterwithCheck(user_id, filter_id,expected_organization_id, filter_name+"dd", log_tsArray_update, "incremental_backup", "error",  "false",test);
				  test.log(LogStatus.INFO,permission_users_id[i]+" can update others' filter"); 
			  }	
		  }
		  if(no_permission_users_id!=null){
			  for(int i=0;i<no_permission_users_id.length;i++){
				  spogServer.userLogin(no_permission_users_id[i], this.OrgPwdForPrepare,test);
				  spogToken=spogServer.getJWTToken();
				  log4SPOGServer.setToken(spogToken);
				  log4SPOGServer.updateLogFilterFailedWithExpectedStatusCode(user_id,filter_id ,expected_organization_id, "", null, null, "none", serverity, "false","none","none","none","none","none",SpogConstants.INSUFFICIENT_PERMISSIONS, "00100101",test);
				  //log4SPOGServer.updateLogFilterwithCheck(user_id, filter_id,expected_organization_id, filter_name+"dd", log_tsArray_update, "incremental_backup", "error",  "false",test);

			  }	
		  }
	  }
	  
	  //@Test(dataProvider = "loginUserToCreateDifferentMessageId")
	  public void createFilterWithDifferentSourceName(String loginUserType,String sourceName){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  String user_id;
		  String filter_name=spogServer.returnRandomUUID();
		  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
		  test.log(LogStatus.INFO,"Login as csr admin");	
		  String errorCode = ErrorCode.NOT_FOUND_RESOURCE_ID;
		  String serverity="information";
		  String no_permission_id,permission_id;
		  String[] no_permission_users_id=null;
		  String[] permission_users_id=null;
		  test.log(LogStatus.INFO,"Login as csr admin");	
		  if (loginUserType.equalsIgnoreCase("csr")){
			  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
			  no_permission_id=mspOrgEmailForPrepare+","+directOrgEmailForPrepare+","+accountEmailForPrepare;
			  no_permission_users_id=no_permission_id.split(",");			  
		  }else if(loginUserType.equalsIgnoreCase("direct")){
			  spogServer.userLogin(this.directOrgEmailForPrepare, this.OrgPwdForPrepare,test);
			  no_permission_id=mspOrgEmailForPrepare+","+directOrgEmailForPrepare1+","+accountEmailForPrepare;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.directOrgEmailForPrepare;
			  permission_users_id=permission_id.split(",");	
		  }else if(loginUserType.equalsIgnoreCase("msp")){
			  spogServer.userLogin(this.mspOrgEmailForPrepare, this.OrgPwdForPrepare,test);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+accountEmailForPrepare;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.mspOrgEmailForPrepare;
			  permission_users_id=permission_id.split(",");
		  }else if(loginUserType.equalsIgnoreCase("account")){
			  spogServer.userLogin(this.accountEmailForPrepare, this.OrgPwdForPrepare,test);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+directOrgEmailForPrepare1+","+accountEmailForPrepare1;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.accountEmailForPrepare;
			  permission_users_id=permission_id.split(",");
		  }else if(loginUserType.equalsIgnoreCase("rootmsp")){
			  spogServer.userLogin(this.final_root_msp_user_name_email, this.OrgPwdForPrepare,test);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+accountEmailForPrepare+","+this.final_sub_msp1_user_name_email+","+this.final_sub_msp1_msp_account_user_name_email+","+this.final_root_msp_direct_org1_user_email;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.final_root_msp_user_name_email;
			  permission_users_id=permission_id.split(",");
		  }else if(loginUserType.equalsIgnoreCase("submsp")){
			  spogServer.userLogin(this.final_sub_msp1_user_name_email, this.OrgPwdForPrepare,test);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+accountEmailForPrepare+","+this.final_root_msp_user_name_email+","+this.final_sub_msp2_account1_user_email+","+this.final_sub_msp2_user_name_email+","+this.final_root_msp_direct_org1_user_email;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.final_sub_msp1_user_name_email;
			  permission_users_id=permission_id.split(",");
		  }else if(loginUserType.equalsIgnoreCase("rootmspaccount")){
			  spogServer.userLogin(this.final_root_msp_direct_org1_user_email, this.OrgPwdForPrepare,test);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+directOrgEmailForPrepare1+","+accountEmailForPrepare1+","+this.final_root_msp_user_name_email+","+this.final_sub_msp2_account1_user_email+","+this.final_sub_msp2_user_name_email+","+this.final_root_msp_direct_org_user_email;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.final_root_msp_direct_org1_user_email;
			  permission_users_id=permission_id.split(",");
		  }else if(loginUserType.equalsIgnoreCase("rootmsp_account")){
			  spogServer.userLogin(this.final_root_msp_account_admin_user_name_email, this.OrgPwdForPrepare);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+directOrgEmailForPrepare1+","+accountEmailForPrepare1+","+this.final_root_msp_user_name_email+","+this.final_sub_msp2_account1_user_email+","+this.final_sub_msp2_user_name_email+","+this.final_root_msp_direct_org1_user_email;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.final_root_msp_account_admin_user_name_email;
			  permission_users_id=permission_id.split(",");
		  }else if(loginUserType.equalsIgnoreCase("submspaccount")){
			  spogServer.userLogin(this.final_sub_msp2_account1_user_email, this.OrgPwdForPrepare,test);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+directOrgEmailForPrepare1+","+accountEmailForPrepare1+","+this.final_root_msp_user_name_email+","+this.final_sub_msp1_account1_user_email+","+this.final_sub_msp1_user_name_email+","+this.final_root_msp_direct_org1_user_email;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.final_sub_msp2_account1_user_email;
			  permission_users_id=permission_id.split(",");
		  }else if(loginUserType.equalsIgnoreCase("submsp_account")){
			  spogServer.userLogin(this.final_sub_msp1_msp_account_user_name_email, this.OrgPwdForPrepare);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+directOrgEmailForPrepare1+","+accountEmailForPrepare1+","+this.final_root_msp_user_name_email+","+this.final_sub_msp2_account1_user_email+","+this.final_sub_msp2_user_name_email+","+this.final_root_msp_direct_org1_user_email;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.final_sub_msp1_msp_account_user_name_email;
			  permission_users_id=permission_id.split(",");
		  }
		  user_id=spogServer.GetLoggedinUser_UserID();
		  String expected_organization_id=spogServer.GetOrganizationIDforUser(user_id);
		  spogToken=spogServer.getJWTToken();
		  log4SPOGServer.setToken(spogToken);
		  String pre_filter_name=spogServer.returnRandomUUID();
		  String filter_id=log4SPOGServer.createLogFilterwithCheck(user_id, expected_organization_id, pre_filter_name,  null,null, "none", "none", "false",test);
		  test.log(LogStatus.INFO,"Login as "+loginUserType+" admin");	
		  if(sourceName!=null && sourceName.equalsIgnoreCase("invalidString")){
			  //String[] timeRange ={"2018-01-01 00:00:00","2018-01-18 00:00:00"};
			  errorCode=ErrorCode.NOT_VALID_ENUM;
			  log4SPOGServer.updateLogFilterFailedWithExpectedStatusCode(user_id,filter_id ,expected_organization_id, "", null, null, "none", serverity, "false","none","none","none",sourceName,"none",SpogConstants.REQUIRED_INFO_NOT_EXIST, errorCode,test);
			  test.log(LogStatus.INFO,loginUserType+ " admin create log filter with source name is "+sourceName+" failed"); 
		  }else{
			  log4SPOGServer.updateLogFilterwithCheck(user_id, filter_id ,expected_organization_id, pre_filter_name, null, null, "none", serverity, "false","none","none","none",sourceName,"none",test);
			  test.log(LogStatus.INFO,loginUserType+ " admin create log filter with source name is "+sourceName); 
		  }
		  if(permission_users_id!=null){
			  for(int i=0;i<permission_users_id.length;i++){
				  spogServer.userLogin(permission_users_id[i], this.OrgPwdForPrepare,test);
				  spogToken=spogServer.getJWTToken();
				  log4SPOGServer.setToken(spogToken);
				  log4SPOGServer.updateLogFilterwithCheck(user_id, filter_id,expected_organization_id, filter_name+"dd", "custom", null, "backup_incremental", "error",  "false",test);
				  //log4SPOGServer.updateLogFilterwithCheck(user_id, filter_id,expected_organization_id, filter_name+"dd", log_tsArray_update, "incremental_backup", "error",  "false",test);
				  test.log(LogStatus.INFO,permission_users_id[i]+" can update others' filter"); 
			  }	
		  }
		  if(no_permission_users_id!=null){
			  for(int i=0;i<no_permission_users_id.length;i++){
				  spogServer.userLogin(no_permission_users_id[i], this.OrgPwdForPrepare,test);
				  spogToken=spogServer.getJWTToken();
				  log4SPOGServer.setToken(spogToken);
				  log4SPOGServer.updateLogFilterFailedWithExpectedStatusCode(user_id,filter_id ,expected_organization_id, "", null, null, "none", serverity, "false","none","none","none","none","none",SpogConstants.INSUFFICIENT_PERMISSIONS, "00100101",test);
				  //log4SPOGServer.updateLogFilterwithCheck(user_id, filter_id,expected_organization_id, filter_name+"dd", log_tsArray_update, "incremental_backup", "error",  "false",test);

			  }	
		  }
	  }
	  
	  //@Test(dataProvider = "loginUserToCreateDifferentMessageId")
	  public void createFilterWithDifferentSourceId(String loginUserType,String sourceId){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  String user_id;
		  String filter_name=spogServer.returnRandomUUID();
		  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
		  test.log(LogStatus.INFO,"Login as csr admin");	
		  String errorCode = ErrorCode.NOT_FOUND_RESOURCE_ID;
		  String serverity="information";
		  String no_permission_id,permission_id;
		  String[] no_permission_users_id=null;
		  String[] permission_users_id=null;
		  test.log(LogStatus.INFO,"Login as csr admin");	
		  if (loginUserType.equalsIgnoreCase("csr")){
			  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
			  no_permission_id=mspOrgEmailForPrepare+","+directOrgEmailForPrepare+","+accountEmailForPrepare;
			  no_permission_users_id=no_permission_id.split(",");			  
		  }else if(loginUserType.equalsIgnoreCase("direct")){
			  spogServer.userLogin(this.directOrgEmailForPrepare, this.OrgPwdForPrepare,test);
			  no_permission_id=mspOrgEmailForPrepare+","+directOrgEmailForPrepare1+","+accountEmailForPrepare;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.directOrgEmailForPrepare;
			  permission_users_id=permission_id.split(",");	
		  }else if(loginUserType.equalsIgnoreCase("msp")){
			  spogServer.userLogin(this.mspOrgEmailForPrepare, this.OrgPwdForPrepare,test);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+accountEmailForPrepare;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.mspOrgEmailForPrepare;
			  permission_users_id=permission_id.split(",");
		  }else if(loginUserType.equalsIgnoreCase("account")){
			  spogServer.userLogin(this.accountEmailForPrepare, this.OrgPwdForPrepare,test);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+directOrgEmailForPrepare1+","+accountEmailForPrepare1;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.accountEmailForPrepare;
			  permission_users_id=permission_id.split(",");
		  }else if(loginUserType.equalsIgnoreCase("rootmsp")){
			  spogServer.userLogin(this.final_root_msp_user_name_email, this.OrgPwdForPrepare,test);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+accountEmailForPrepare+","+this.final_sub_msp1_user_name_email+","+this.final_sub_msp1_msp_account_user_name_email+","+this.final_root_msp_direct_org1_user_email;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.final_root_msp_user_name_email;
			  permission_users_id=permission_id.split(",");
		  }else if(loginUserType.equalsIgnoreCase("submsp")){
			  spogServer.userLogin(this.final_sub_msp1_user_name_email, this.OrgPwdForPrepare,test);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+accountEmailForPrepare+","+this.final_root_msp_user_name_email+","+this.final_sub_msp2_account1_user_email+","+this.final_sub_msp2_user_name_email+","+this.final_root_msp_direct_org1_user_email;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.final_sub_msp1_user_name_email;
			  permission_users_id=permission_id.split(",");
		  }else if(loginUserType.equalsIgnoreCase("rootmspaccount")){
			  spogServer.userLogin(this.final_root_msp_direct_org1_user_email, this.OrgPwdForPrepare,test);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+directOrgEmailForPrepare1+","+accountEmailForPrepare1+","+this.final_root_msp_user_name_email+","+this.final_sub_msp2_account1_user_email+","+this.final_sub_msp2_user_name_email+","+this.final_root_msp_direct_org_user_email;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.final_root_msp_direct_org1_user_email;
			  permission_users_id=permission_id.split(",");
		  }else if(loginUserType.equalsIgnoreCase("rootmsp_account")){
			  spogServer.userLogin(this.final_root_msp_account_admin_user_name_email, this.OrgPwdForPrepare);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+directOrgEmailForPrepare1+","+accountEmailForPrepare1+","+this.final_root_msp_user_name_email+","+this.final_sub_msp2_account1_user_email+","+this.final_sub_msp2_user_name_email+","+this.final_root_msp_direct_org1_user_email;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.final_root_msp_account_admin_user_name_email;
			  permission_users_id=permission_id.split(",");
		  }else if(loginUserType.equalsIgnoreCase("submspaccount")){
			  spogServer.userLogin(this.final_sub_msp2_account1_user_email, this.OrgPwdForPrepare,test);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+directOrgEmailForPrepare1+","+accountEmailForPrepare1+","+this.final_root_msp_user_name_email+","+this.final_sub_msp1_account1_user_email+","+this.final_sub_msp1_user_name_email+","+this.final_root_msp_direct_org1_user_email;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.final_sub_msp2_account1_user_email;
			  permission_users_id=permission_id.split(",");
		  }else if(loginUserType.equalsIgnoreCase("submsp_account")){
			  spogServer.userLogin(this.final_sub_msp1_msp_account_user_name_email, this.OrgPwdForPrepare);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+directOrgEmailForPrepare1+","+accountEmailForPrepare1+","+this.final_root_msp_user_name_email+","+this.final_sub_msp2_account1_user_email+","+this.final_sub_msp2_user_name_email+","+this.final_root_msp_direct_org1_user_email;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.final_sub_msp1_msp_account_user_name_email;
			  permission_users_id=permission_id.split(",");
		  }
		  user_id=spogServer.GetLoggedinUser_UserID();
		  String expected_organization_id=spogServer.GetOrganizationIDforUser(user_id);
		  spogToken=spogServer.getJWTToken();
		  log4SPOGServer.setToken(spogToken);
		  String pre_filter_name=spogServer.returnRandomUUID();
		  String filter_id=log4SPOGServer.createLogFilterwithCheck(user_id, expected_organization_id, pre_filter_name, null, null, "none", "none", "false",test);
		  test.log(LogStatus.INFO,"Login as "+loginUserType+" admin");	
		  if(sourceId!=null && sourceId.equalsIgnoreCase("invalidString")){
			  //String[] timeRange ={"2018-01-01 00:00:00","2018-01-18 00:00:00"};
			  errorCode=ErrorCode.NOT_VALID_ENUM;
			  log4SPOGServer.updateLogFilterFailedWithExpectedStatusCode(user_id,filter_id ,expected_organization_id, "", null, null, "none", serverity, "false","none",sourceId,"none","none","none",SpogConstants.REQUIRED_INFO_NOT_EXIST, errorCode,test);
			  test.log(LogStatus.INFO,loginUserType+ " admin create log filter with source Id is "+sourceId+" failed"); 
		  }else{
			  log4SPOGServer.updateLogFilterwithCheck(user_id, filter_id ,expected_organization_id, pre_filter_name, null, null, "none", serverity, "false","none",sourceId,"none","none","none",test);
			  test.log(LogStatus.INFO,loginUserType+ " admin create log filter with source Id is "+sourceId); 
		  }
		  if(permission_users_id!=null){
			  for(int i=0;i<permission_users_id.length;i++){
				  spogServer.userLogin(permission_users_id[i], this.OrgPwdForPrepare,test);
				  spogToken=spogServer.getJWTToken();
				  log4SPOGServer.setToken(spogToken);
				  log4SPOGServer.updateLogFilterwithCheck(user_id, filter_id,expected_organization_id, filter_name+"dd", "custom", null, "backup_incremental", "error",  "false",test);
				  //log4SPOGServer.updateLogFilterwithCheck(user_id, filter_id,expected_organization_id, filter_name+"dd", log_tsArray_update, "incremental_backup", "error",  "false",test);
				  test.log(LogStatus.INFO,permission_users_id[i]+" can update others' filter"); 
			  }	
		  }
		  if(no_permission_users_id!=null){
			  for(int i=0;i<no_permission_users_id.length;i++){
				  spogServer.userLogin(no_permission_users_id[i], this.OrgPwdForPrepare,test);
				  spogToken=spogServer.getJWTToken();
				  log4SPOGServer.setToken(spogToken);
				  log4SPOGServer.updateLogFilterFailedWithExpectedStatusCode(user_id,filter_id ,expected_organization_id, "", null, null, "none", serverity, "false","none","none","none","none","none",SpogConstants.RESOURCE_NOT_EXIST, "00A00302",test);
				  //log4SPOGServer.updateLogFilterwithCheck(user_id, filter_id,expected_organization_id, filter_name+"dd", log_tsArray_update, "incremental_backup", "error",  "false",test);

			  }	
		  }
	  }
	  
	  @Test(dataProvider = "loginUserToCreateDifferentMessageId")
	  public void createFilterWithDifferentOrigin(String loginUserType,String origin){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  String user_id;
		  String filter_name=spogServer.returnRandomUUID();
		  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
		  test.log(LogStatus.INFO,"Login as csr admin");	
		  String errorCode = ErrorCode.NOT_FOUND_RESOURCE_ID;
		  String serverity="information";
		  String no_permission_id,permission_id;
		  String[] no_permission_users_id=null;
		  String[] permission_users_id=null;
		  test.log(LogStatus.INFO,"Login as csr admin");	
		  if (loginUserType.equalsIgnoreCase("csr")){
			  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
			  no_permission_id=mspOrgEmailForPrepare+","+directOrgEmailForPrepare+","+accountEmailForPrepare;
			  no_permission_users_id=no_permission_id.split(",");			  
		  }else if(loginUserType.equalsIgnoreCase("direct")){
			  spogServer.userLogin(this.directOrgEmailForPrepare, this.OrgPwdForPrepare,test);
			  no_permission_id=mspOrgEmailForPrepare+","+directOrgEmailForPrepare1+","+accountEmailForPrepare;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.directOrgEmailForPrepare;
			  permission_users_id=permission_id.split(",");	
		  }else if(loginUserType.equalsIgnoreCase("msp")){
			  spogServer.userLogin(this.mspOrgEmailForPrepare, this.OrgPwdForPrepare,test);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+accountEmailForPrepare;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.mspOrgEmailForPrepare;
			  permission_users_id=permission_id.split(",");
		  }else if(loginUserType.equalsIgnoreCase("account")){
			  spogServer.userLogin(this.accountEmailForPrepare, this.OrgPwdForPrepare,test);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+directOrgEmailForPrepare1+","+accountEmailForPrepare1;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.accountEmailForPrepare;
			  permission_users_id=permission_id.split(",");
		  }else if(loginUserType.equalsIgnoreCase("rootmsp")){
			  spogServer.userLogin(this.final_root_msp_user_name_email, this.OrgPwdForPrepare,test);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+accountEmailForPrepare+","+this.final_sub_msp1_user_name_email+","+this.final_sub_msp1_msp_account_user_name_email+","+this.final_root_msp_direct_org1_user_email;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.final_root_msp_user_name_email;
			  permission_users_id=permission_id.split(",");
		  }else if(loginUserType.equalsIgnoreCase("submsp")){
			  spogServer.userLogin(this.final_sub_msp1_user_name_email, this.OrgPwdForPrepare,test);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+accountEmailForPrepare+","+this.final_root_msp_user_name_email+","+this.final_sub_msp2_account1_user_email+","+this.final_sub_msp2_user_name_email+","+this.final_root_msp_direct_org1_user_email;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.final_sub_msp1_user_name_email;
			  permission_users_id=permission_id.split(",");
		  }else if(loginUserType.equalsIgnoreCase("rootmspaccount")){
			  spogServer.userLogin(this.final_root_msp_direct_org1_user_email, this.OrgPwdForPrepare,test);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+directOrgEmailForPrepare1+","+accountEmailForPrepare1+","+this.final_root_msp_user_name_email+","+this.final_sub_msp2_account1_user_email+","+this.final_sub_msp2_user_name_email+","+this.final_root_msp_direct_org_user_email;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.final_root_msp_direct_org1_user_email;
			  permission_users_id=permission_id.split(",");
		  }else if(loginUserType.equalsIgnoreCase("rootmsp_account")){
			  spogServer.userLogin(this.final_root_msp_account_admin_user_name_email, this.OrgPwdForPrepare);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+directOrgEmailForPrepare1+","+accountEmailForPrepare1+","+this.final_root_msp_user_name_email+","+this.final_sub_msp2_account1_user_email+","+this.final_sub_msp2_user_name_email+","+this.final_root_msp_direct_org1_user_email;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.final_root_msp_account_admin_user_name_email;
			  permission_users_id=permission_id.split(",");
		  }else if(loginUserType.equalsIgnoreCase("submspaccount")){
			  spogServer.userLogin(this.final_sub_msp2_account1_user_email, this.OrgPwdForPrepare,test);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+directOrgEmailForPrepare1+","+accountEmailForPrepare1+","+this.final_root_msp_user_name_email+","+this.final_sub_msp1_account1_user_email+","+this.final_sub_msp1_user_name_email+","+this.final_root_msp_direct_org1_user_email;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.final_sub_msp2_account1_user_email;
			  permission_users_id=permission_id.split(",");
		  }else if(loginUserType.equalsIgnoreCase("submsp_account")){
			  spogServer.userLogin(this.final_sub_msp1_msp_account_user_name_email, this.OrgPwdForPrepare);
			  no_permission_id=mspOrgEmailForPrepare1+","+directOrgEmailForPrepare+","+directOrgEmailForPrepare1+","+accountEmailForPrepare1+","+this.final_root_msp_user_name_email+","+this.final_sub_msp2_account1_user_email+","+this.final_sub_msp2_user_name_email+","+this.final_root_msp_direct_org1_user_email;
			  no_permission_users_id=no_permission_id.split(",");	
			  permission_id=this.final_sub_msp1_msp_account_user_name_email;
			  permission_users_id=permission_id.split(",");
		  }
		  user_id=spogServer.GetLoggedinUser_UserID();
		  String expected_organization_id=spogServer.GetOrganizationIDforUser(user_id);
		  spogToken=spogServer.getJWTToken();
		  log4SPOGServer.setToken(spogToken);
		  String pre_filter_name=spogServer.returnRandomUUID();
		  String filter_id=log4SPOGServer.createLogFilterwithCheck(user_id, expected_organization_id, pre_filter_name, null, null, "none", "none", "false",test);
		  test.log(LogStatus.INFO,"Login as "+loginUserType+" admin");	
		  if(origin!=null && origin.equalsIgnoreCase("invalidString")){
			  //String[] timeRange ={"2018-01-01 00:00:00","2018-01-18 00:00:00"};
			  errorCode=ErrorCode.NOT_VALID_ENUM;
			  log4SPOGServer.updateLogFilterFailedWithExpectedStatusCode(user_id,filter_id ,expected_organization_id, "",  null,null, "none", serverity, "false","none","none","none","none",origin,SpogConstants.REQUIRED_INFO_NOT_EXIST, errorCode,test);
			  test.log(LogStatus.INFO,loginUserType+ " admin create log filter with origin is "+origin+" failed"); 
		  }else{
			  log4SPOGServer.updateLogFilterwithCheck(user_id, filter_id ,expected_organization_id, pre_filter_name, null, null, "none", serverity, "false","none","none","none","none",origin,test);
			  test.log(LogStatus.INFO,loginUserType+ " admin create log filter with origin is "+origin); 
		  }
		  if(permission_users_id!=null){
			  for(int i=0;i<permission_users_id.length;i++){
				  spogServer.userLogin(permission_users_id[i], this.OrgPwdForPrepare,test);
				  spogToken=spogServer.getJWTToken();
				  log4SPOGServer.setToken(spogToken);
				  log4SPOGServer.updateLogFilterwithCheck(user_id, filter_id,expected_organization_id, filter_name+"dd", "custom", null, "backup_incremental", "error",  "false",test);
				  //log4SPOGServer.updateLogFilterwithCheck(user_id, filter_id,expected_organization_id, filter_name+"dd", log_tsArray_update, "incremental_backup", "error",  "false",test);
				  test.log(LogStatus.INFO,permission_users_id[i]+" can update others' filter"); 
			  }	
		  }
		  if(no_permission_users_id!=null){
			  for(int i=0;i<no_permission_users_id.length;i++){
				  spogServer.userLogin(no_permission_users_id[i], this.OrgPwdForPrepare,test);
				  spogToken=spogServer.getJWTToken();
				  log4SPOGServer.setToken(spogToken);
				  log4SPOGServer.updateLogFilterFailedWithExpectedStatusCode(user_id,filter_id ,expected_organization_id, "", null, null, "none", serverity, "false","none","none","none","none","none",SpogConstants.INSUFFICIENT_PERMISSIONS, "00100101",test);
				  //log4SPOGServer.updateLogFilterwithCheck(user_id, filter_id,expected_organization_id, filter_name+"dd", log_tsArray_update, "incremental_backup", "error",  "false",test);

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
