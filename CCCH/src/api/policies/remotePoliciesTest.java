package api.policies;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.ErrorCode;
import Constants.SpogConstants;
import InvokerServer.GatewayServer;
import InvokerServer.Log4SPOGServer;
import InvokerServer.Policy4SPOGServer;
import InvokerServer.SPOGServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class remotePoliciesTest extends base.prepare.PrepareOrgInfo{
	@Parameters({ "pmfKey"})
	public remotePoliciesTest(String pmfKey) {
		super(pmfKey);
		// TODO Auto-generated constructor stub
	}
	private GatewayServer gatewayServer;
	private Policy4SPOGServer policy4SPOGServer;
	  private SPOGServer spogServer;
	  private String directOrgId,directOrgId1;
	  private String mspOrgId;
	  private String mspOrgId1;
	  private String accountOrgId,accountOrgId1;
	  private String csrAdmin;
	  private String csrPwd;
	  private String site_version="1.0.0";
	  private String sub_msp_pwd="Welcome*02";
	  private String gateway_hostname="kiran";
	  
	  //this is creating msp org or direct org for preparation
	  private String mspOrgNameForPrepare="d_jing_spogqa_msp_org_prepare";
	  private String csrOrgNameForPrepare="d_jing_spogqa_csr_org_prepare";
	  private String csrOrgEmailForPrepare,mspOrgEmailForPrepare,mspOrgEmailForPrepare1,accountEmailForPrepare,accountEmailForPrepare1;
	  private String directOrgNameForPrepare="d_jing_spogqa_direct_org_prepare";
	  private String directOrgEmailForPrepare,directOrgEmailForPrepare1;
	  private String OrgFistNameForPrepare="jing";
	  private String OrgLastNameForPrepare="org_prepare";
	  private String OrgPwdForPrepare="welcomeA02";
	  private String csrOrgId;
	  private String csrreadonly;
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
		  policy4SPOGServer= new Policy4SPOGServer(baseURI, port);
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
		  
		  this.csrreadonly=spogServer.ReturnRandom("d_jing_spogqa_csrreadonly_org_prepare@arcserve.com");
		  spogServer.createUserAndCheck(csrreadonly, OrgPwdForPrepare, "dd", "gg", "csr_read_only", spogServer.GetLoggedinUserOrganizationID(),test);
		
		  //this.csrOrgId=spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(csrOrgNameForPrepare), SpogConstants.CSR_ORG, csrOrgEmailForPrepare, OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare);
		  this.mspOrgId = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(mspOrgNameForPrepare)+org_prefix, SpogConstants.MSP_ORG, mspOrgEmailForPrepare, OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare);
		  this.mspOrgId1 = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(mspOrgNameForPrepare)+org_prefix, SpogConstants.MSP_ORG, mspOrgEmailForPrepare1, OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare);
		  this.accountOrgId= spogServer.createAccountWithCheck(this.mspOrgId, spogServer.ReturnRandom("spogqa_account")+org_prefix,"");
		  this.accountOrgId1= spogServer.createAccountWithCheck(this.mspOrgId, spogServer.ReturnRandom("spogqa_account")+org_prefix,"");
		  this.directOrgId = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(directOrgNameForPrepare)+org_prefix, SpogConstants.DIRECT_ORG, directOrgEmailForPrepare, OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare);
		  this.directOrgId1 = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(directOrgNameForPrepare)+org_prefix, SpogConstants.DIRECT_ORG, directOrgEmailForPrepare1, OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare);
		  spogServer.createUserAndCheck(this.accountEmailForPrepare, OrgPwdForPrepare, "dd", "gg", SpogConstants.DIRECT_ADMIN, accountOrgId,test);
		  spogServer.createUserAndCheck(this.accountEmailForPrepare1, OrgPwdForPrepare, "dd", "gg", SpogConstants.DIRECT_ADMIN, accountOrgId1,test);
		  prepare(baseURI, port, logFolder, csrAdminUserName, csrAdminPassword, this.getClass().getSimpleName() );
	  }
	  
	  @DataProvider(name = "loginUserToCreatePolicyFail")
	  public final Object[][] loginUserToCreatePolicyFail() {
			return new Object[][] { 
				{"direct","shaji02-2012r2","administrator","123.com",8015,"http"},
				{"direct","shaji02-2012r2","administrator1","123.com",8015,"http"},
				{"direct","shaji02-2012r2","administrator","123.com",8015,"https"},
				{"direct","shaji02-2012r2","sj","123.com",8015,"http"},
				{"direct","shaji02-2012r2","administrator","123.com",8014,"http"},
				{"direct","shaji02-2012r2ddd","administrator","123.com",8015,"http"},
				{"direct","shaji02-2012r2","administrator","123.com",8015,"https"},
				{"direct","shaji02-2012r2","administrator","123.com",8016,"http"},
				
				{"account","shaji02-2012r2","administrator","123.com",8015,"http"},
				{"account","shaji02-2012r2","administrator1","123.com",8015,"http"},
				{"account","shaji02-2012r2","administrator","123.com",8015,"https"},
				{"account","shaji02-2012r2","sj","123.com",8015,"http"},
				{"account","shaji02-2012r2","administrator","123.com",8014,"http"},
				{"account","shaji02-2012r2ddd","administrator","123.com",8015,"http"},
				
				{"msp","shaji02-2012r2","administrator","123.com",8015,"http"},
				{"msp","shaji02-2012r2","administrator1","123.com",8015,"http"},
				{"msp","shaji02-2012r2","administrator","123.com",8015,"https"},
				{"msp","shaji02-2012r2","sj","123.com",8015,"http"},
				{"msp","shaji02-2012r2","administrator","123.com",8014,"http"},
				{"msp","shaji02-2012r2ddd","administrator","123.com",8015,"http"},
				
				{"submspaccount","shaji02-2012r2","administrator","123.com",8015,"http"},
				{"submspaccount","shaji02-2012r2","administrator1","123.com",8015,"http"},
				{"submspaccount","shaji02-2012r2","administrator","123.com",8015,"https"},
				{"submspaccount","shaji02-2012r2","sj","123.com",8015,"http"},
				{"submspaccount","shaji02-2012r2","administrator","123.com",8014,"http"},
				{"submspaccount","shaji02-2012r2ddd","administrator","123.com",8015,"http"},
				
				{"submspaccountadmin","shaji02-2012r2","administrator","123.com",8015,"http"},
				{"submspaccountadmin","shaji02-2012r2","administrator1","123.com",8015,"http"},
				{"submspaccountadmin","shaji02-2012r2","administrator","123.com",8015,"https"},
				{"submspaccountadmin","shaji02-2012r2","sj","123.com",8015,"http"},
				{"submspaccountadmin","shaji02-2012r2","administrator","123.com",8014,"http"},
				{"submspaccountadmin","shaji02-2012r2ddd","administrator","123.com",8015,"http"},
				
				{"submsp","shaji02-2012r2","administrator","123.com",8015,"http"},
				{"submsp","shaji02-2012r2","administrator1","123.com",8015,"http"},
				{"submsp","shaji02-2012r2","administrator","123.com",8015,"https"},
				{"submsp","shaji02-2012r2","sj","123.com",8015,"http"},
				{"submsp","shaji02-2012r2","administrator","123.com",8014,"http"},
				{"submsp","shaji02-2012r2ddd","administrator","123.com",8015,"http"},
				
				{"rootmspaccount","shaji02-2012r2","administrator","123.com",8015,"http"},
				{"rootmspaccount","shaji02-2012r2","administrator1","123.com",8015,"http"},
				{"rootmspaccount","shaji02-2012r2","administrator","123.com",8015,"https"},
				{"rootmspaccount","shaji02-2012r2","sj","123.com",8015,"http"},
				{"rootmspaccount","shaji02-2012r2","administrator","123.com",8014,"http"},
				{"rootmspaccount","shaji02-2012r2ddd","administrator","123.com",8015,"http"},
				
				{"rootmspaccountadmin","shaji02-2012r2","administrator","123.com",8015,"http"},
				{"rootmspaccountadmin","shaji02-2012r2","administrator1","123.com",8015,"http"},
				{"rootmspaccountadmin","shaji02-2012r2","administrator","123.com",8015,"https"},
				{"rootmspaccountadmin","shaji02-2012r2","sj","123.com",8015,"http"},
				{"rootmspaccountadmin","shaji02-2012r2","administrator","123.com",8014,"http"},
				{"rootmspaccountadmin","shaji02-2012r2ddd","administrator","123.com",8015,"http"},
				
				{"rootmsp","shaji02-2012r2","administrator","123.com",8015,"http"},
				{"rootmsp","shaji02-2012r2","administrator1","123.com",8015,"http"},
				{"rootmsp","shaji02-2012r2","administrator","123.com",8015,"https"},
				{"rootmsp","shaji02-2012r2","sj","123.com",8015,"http"},
				{"rootmsp","shaji02-2012r2","administrator","123.com",8014,"http"},
				{"rootmsp","shaji02-2012r2ddd","administrator","123.com",8015,"http"},

				{"csr","shaji02-2012r2","administrator","123.com",8015,"http"},
				{"csr","shaji02-2012r2","administrator1","123.com",8015,"http"},
				{"csr","shaji02-2012r2","administrator","123.com",8015,"https"},
				{"csr","shaji02-2012r2","sj","123.com",8015,"http"},
				{"csr","shaji02-2012r2","administrator","123.com",8014,"http"},
				{"csr","shaji02-2012r2ddd","administrator","123.com",8015,"http"},
				
				{"csr_read_only","shaji02-2012r2","administrator","123.com",8015,"http"},
				{"csr_read_only","shaji02-2012r2","administrator1","123.com",8015,"http"},
				{"csr_read_only","shaji02-2012r2","administrator","123.com",8015,"https"},
				{"csr_read_only","shaji02-2012r2","sj","123.com",8015,"http"},
				{"csr_read_only","shaji02-2012r2","administrator","123.com",8014,"http"},
				{"csr_read_only","shaji02-2012r2ddd","administrator","123.com",8015,"http"}};
	  }
	  @Test(dataProvider = "loginUserToCreatePolicyFail")
	  public void createPolicyFail(String loginUserType,String consolename,String username,String pwd,int port,String protocol ){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  String policy_id=spogServer.returnRandomUUID();
		  String schedule_id=spogServer.returnRandomUUID();
		  String task_id=spogServer.returnRandomUUID();
		  String throttle_id=spogServer.returnRandomUUID();
		  String destination_store_id=null;
		  String organization_id=null;
		  String task_type="udp_replication_from_remote";
		  String policy_type="cloud_hybrid_replication";
		  String policy_name=spogServer.ReturnRandom("test");
		  String policy_description=spogServer.ReturnRandom("description");
		  String destination_name="shanjing-cloud-hybridDS4";
		  
		  if (loginUserType.equalsIgnoreCase("csr")){
			  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
			 	  
		  }else if (loginUserType.equalsIgnoreCase("csr_read_only")){
			  spogServer.userLogin(this.csrreadonly, OrgPwdForPrepare,test);
			  
		  }else if(loginUserType.equalsIgnoreCase("direct")){
			  spogServer.userLogin(this.directOrgEmailForPrepare, OrgPwdForPrepare,test);
			  
		  }else if(loginUserType.equalsIgnoreCase("msp")){
			  spogServer.userLogin(this.mspOrgEmailForPrepare, OrgPwdForPrepare,test);
			 
		  }else if(loginUserType.equalsIgnoreCase("account")){
			  spogServer.userLogin(this.accountEmailForPrepare, OrgPwdForPrepare,test);
			 
		  }else if(loginUserType.equalsIgnoreCase("submspaccount")) {
			  spogServer.userLogin(this.final_sub_msp2_account1_user_email, sub_msp_pwd,test);
			   
		  }else if(loginUserType.equalsIgnoreCase("submspaccountadmin")) {
			  spogServer.userLogin(this.final_sub_msp1_msp_account_user_name_email, sub_msp_pwd,test);
			  
		  }else if(loginUserType.equalsIgnoreCase("submsp")) {
			  spogServer.userLogin(this.final_sub_msp2_user_name_email, sub_msp_pwd,test);
			  
		  }else if(loginUserType.equalsIgnoreCase("rootmspaccount")) {
			  spogServer.userLogin(this.final_root_msp_direct_org_user_email, sub_msp_pwd,test);
			  
		  }else if(loginUserType.equalsIgnoreCase("rootmspaccountadmin")) {
			  spogServer.userLogin(this.final_root_msp_account_admin_user_name_email, sub_msp_pwd,test);
			  
		  }else if(loginUserType.equalsIgnoreCase("rootmsp")) {
			  spogServer.userLogin(this.final_root_msp_user_name_email, sub_msp_pwd,test);
			  
		  }
		  String user_token=spogServer.getJWTToken();
		  policy4SPOGServer.setToken(user_token);
		  
		  Response res=policy4SPOGServer.getRemotePolicies("none", consolename,username,pwd,port,protocol,test);
		  res.then().statusCode(SpogConstants.REQUIRED_INFO_NOT_EXIST);
		  spogServer.checkErrorCode(res, "00E00054");
		  
	  }
	  
	  @DataProvider(name = "loginUserToCreatePolicyFailForInvalidUserInfo")
	  public final Object[][] loginUserToCreatePolicyFailForInvalidUserInfo() {
			return new Object[][] { 
//				{"direct","ec2-18-234-157-152.compute-1.amazonaws.com","administrator","cnbjrdqa1!1",8015,"https"},
//				{"direct","ec2-18-234-157-152.compute-1.amazonaws.com","administrator1","cnbjrdqa1!",8015,"https"},
				
				{"account","shaji02-2012r2","administrator","cnbjrdqa1!1",8015,"https"},
				{"account","shaji02-2012r2","administrator1","cnbjrdqa1!",8015,"https"},
				
//				{"msp","ec2-18-234-157-152.compute-1.amazonaws.com","administrator","cnbjrdqa1!1",8015,"https"},
//				{"msp","ec2-18-234-157-152.compute-1.amazonaws.com","administrator1","cnbjrdqa1!",8015,"https"},
//				
//				{"csr","ec2-18-234-157-152.compute-1.amazonaws.com","administrator","cnbjrdqa1!1",8015,"https"},
//				{"csr","ec2-18-234-157-152.compute-1.amazonaws.com","administrator1","cnbjrdqa1!",8015,"https"},
//				
//				{"csr_read_only","ec2-18-234-157-152.compute-1.amazonaws.com","administrator","cnbjrdqa1!1",8015,"https"},
//				{"csr_read_only","ec2-18-234-157-152.compute-1.amazonaws.com","administrator1","cnbjrdqa1!",8015,"https"}
				};
	  }
	 // @Test(dataProvider = "loginUserToCreatePolicyFailForInvalidUserInfo")
	  public void createPolicyFailForInvalidUserInfo(String loginUserType,String consolename,String username,String pwd,int port,String protocol ){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  String policy_id=spogServer.returnRandomUUID();
		  String schedule_id=spogServer.returnRandomUUID();
		  String task_id=spogServer.returnRandomUUID();
		  String throttle_id=spogServer.returnRandomUUID();
		  String destination_store_id=null;
		  String organization_id=null;
		  String task_type="udp_replication_from_remote";
		  String policy_type="cloud_hybrid_replication";
		  String policy_name=spogServer.ReturnRandom("test");
		  String policy_description=spogServer.ReturnRandom("description");
		  String destination_name="shanjing-cloud-hybridDS4";
		  
		  if (loginUserType.equalsIgnoreCase("csr")){
			  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
			 	  
		  }else if (loginUserType.equalsIgnoreCase("csr_read_only")){
			  spogServer.userLogin(this.csrreadonly, OrgPwdForPrepare,test);
			  
		  }else if(loginUserType.equalsIgnoreCase("direct")){
			  spogServer.userLogin(this.directOrgEmailForPrepare, OrgPwdForPrepare,test);
			  
		  }else if(loginUserType.equalsIgnoreCase("msp")){
			  spogServer.userLogin(this.mspOrgEmailForPrepare, OrgPwdForPrepare,test);
			 
		  }else if(loginUserType.equalsIgnoreCase("account")){
			  spogServer.userLogin(this.accountEmailForPrepare, OrgPwdForPrepare,test);
			 
		  }
		  String user_token=spogServer.getJWTToken();
		  policy4SPOGServer.setToken(user_token);
		  
		  Response res=policy4SPOGServer.getRemotePolicies("none", consolename,username,pwd,port,protocol,test);
		  res.then().statusCode(SpogConstants.REQUIRED_INFO_NOT_EXIST);
		  spogServer.checkErrorCode(res, "00E00055");
		  
	  }
	  
	  @DataProvider(name = "loginUserToCreatePolicy")
	  public final Object[][] loginUserToCreatePolicy() {
			return new Object[][] { 
				{"direct","10.57.54.190","administrator","123.com",8015,"http"},
				{"account","10.57.54.190","administrator","123.com",8015,"http"},
				{"msp","10.57.54.190","administrator","123.com",8015,"http"},
				{"csr","10.57.54.190","administrator","123.com",8015,"http"},
				{"csr_read_only","10.57.54.190","administrator","123.com",8015,"http"}};
	  }
	  //@Test(dataProvider = "loginUserToCreatePolicy")
	  public void createPolicy(String loginUserType,String consolename,String username,String pwd,int port,String protocol ){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  String policy_id=spogServer.returnRandomUUID();
		  String schedule_id=spogServer.returnRandomUUID();
		  String task_id=spogServer.returnRandomUUID();
		  String throttle_id=spogServer.returnRandomUUID();
		  String destination_store_id=null;
		  String organization_id=null;
		  String task_type="udp_replication_from_remote";
		  String policy_type="cloud_hybrid_replication";
		  String policy_name=spogServer.ReturnRandom("test");
		  String policy_description=spogServer.ReturnRandom("description");
		  String destination_name="shanjing-cloud-hybridDS4";
		  
		  if (loginUserType.equalsIgnoreCase("csr")){
			  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
			 	  
		  }else if (loginUserType.equalsIgnoreCase("csr_read_only")){
			  spogServer.userLogin(this.csrreadonly, OrgPwdForPrepare,test);
			  
		  }else if(loginUserType.equalsIgnoreCase("direct")){
			  spogServer.userLogin(this.directOrgEmailForPrepare, OrgPwdForPrepare,test);
			  
		  }else if(loginUserType.equalsIgnoreCase("msp")){
			  spogServer.userLogin(this.mspOrgEmailForPrepare, OrgPwdForPrepare,test);
			 
		  }else if(loginUserType.equalsIgnoreCase("account")){
			  spogServer.userLogin(this.accountEmailForPrepare, OrgPwdForPrepare,test);
			 
		  }
		  String user_token=spogServer.getJWTToken();
		  policy4SPOGServer.setToken(user_token);
		  
		  Response res=policy4SPOGServer.getRemotePolicies("none", consolename,username,pwd,port,protocol,test);
		  res.then().statusCode(SpogConstants.SUCCESS_POST);
		  
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
