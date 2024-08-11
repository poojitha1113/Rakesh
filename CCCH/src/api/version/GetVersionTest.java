package api.version;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

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

public class GetVersionTest extends base.prepare.PrepareOrgInfo{
	@Parameters({ "pmfKey"})
	public GetVersionTest(String pmfKey) {
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
	  private String mspOrgNameForPrepare="d_jing_spogqa_msp_org_prepare@arcserve.com";
	  private String mspOrgEmailForPrepare,mspOrgEmailForPrepare1;
	  private String directOrgNameForPrepare="d_jing_spogqa_direct_org_prepare@arcserve.com";
	  private String directOrgEmailForPrepare;
	  private String OrgFistNameForPrepare="jing";
	  private String OrgLastNameForPrepare="org_prepare";
	  private String OrgPwdForPrepare="welcomeA02";
	  private String sub_msp_password =  "Welcome*02";
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
//	  
	  private UserSpogServer userSpogServer;
	  private String[] mspAccountAdminUserIds=new String[1];
	  private String accountEmailForPrepare ="d_jing_spogqa_account_org_prepare_jing_1@arcserve.com";
	  private String mspAccountAdminEmailForPrepare="d_jing_spogqa_msp_account_org_prepar_jing@arcserve.come";
	  private String accountEmailForPrepare1="d_jing_spogqa_account_org_prepare_jing_2@arcserve.com";
	  private String accountOrgId3,accountOrgId4,msp_account_admin_token;
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
		  this.directOrgId = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(directOrgNameForPrepare)+org_prefix, SpogConstants.DIRECT_ORG, directOrgEmailForPrepare, OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare);
		  //spogServer.createUserAndCheck(accountEmailForPrepare, OrgPwdForPrepare, "dd", "gg", SpogConstants.DIRECT_ADMIN, mspOrgId,test);
		  
		  this.accountOrgId3= spogServer.createAccountWithCheck(this.mspOrgId, "spogqa_account_jing_1"+org_prefix,"");
		  this.accountOrgId4= spogServer.createAccountWithCheck(this.mspOrgId, "spogqa_account_jing_2"+org_prefix,"");	  
		  this.accountEmailForPrepare=spogServer.ReturnRandom(accountEmailForPrepare);
		  this.accountEmailForPrepare1=spogServer.ReturnRandom(accountEmailForPrepare1);
		  spogServer.createUserAndCheck(this.accountEmailForPrepare, OrgPwdForPrepare, "dd", "gg", SpogConstants.DIRECT_ADMIN, accountOrgId3,test);
		  spogServer.createUserAndCheck(this.accountEmailForPrepare1, OrgPwdForPrepare, "dd", "gg", SpogConstants.DIRECT_ADMIN, accountOrgId4,test);
		  mspAccountAdminEmailForPrepare = spogServer.ReturnRandom("d_jing_spogqa_msp_org_prepar_jing@arcserve.come");
		  String csrToken= spogServer.getJWTToken();
		  mspAccountAdminUserIds[0]=spogServer.createUserAndCheck(mspAccountAdminEmailForPrepare, OrgPwdForPrepare, "ff", "ff", "msp_account_admin", mspOrgId, test);
		  spogServer.userLogin(mspAccountAdminEmailForPrepare, OrgPwdForPrepare);
		  this.msp_account_admin_token = spogServer.getJWTToken();
		  
		  Response response = userSpogServer.assignMspAccountAdmins(mspOrgId, accountOrgId3, mspAccountAdminUserIds, csrToken);
		  userSpogServer.assignMspAccountAdminsWithCheck(response, mspAccountAdminUserIds, csrToken);
		  spogServer.userLogin(this.mspAccountAdminEmailForPrepare, OrgPwdForPrepare);
		  prepare(baseURI, port, logFolder, csrAdminUserName, csrAdminPassword, this.getClass().getSimpleName() );
	  }
	  
	  @Test
	  public void getVersionWithoutUserLogin(){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  spogServer.getVersionWithCheckWithoutToken("1.0", "dev");
	  }
	  
	  
	  @DataProvider(name = "getVersionWithUser")
	  public final Object[][] getVersionWithUserLogin() {
			return new Object[][] { {"csr"},{"msp"},{"direct"},{"account"},{"msp_account_admin"},
				{"submspaccountadmin"},{"submspaccount"},{"submsp"},{"rootmspaccount"},{"rootmspaccountadmin"},{"rootmsp"},
			};
	  }
	  @Test(dataProvider = "getVersionWithUser")
	  
	  public void testGetVersionWithUserLogin(String loginUserType){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  if (loginUserType.equalsIgnoreCase("csr")){
			  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);	 
		  }else if(loginUserType.equalsIgnoreCase("direct")){
			  spogServer.userLogin(this.directOrgEmailForPrepare, OrgPwdForPrepare,test);
		  }else if(loginUserType.equalsIgnoreCase("msp")){
			  spogServer.userLogin(this.mspOrgEmailForPrepare, OrgPwdForPrepare,test);
		  }else if(loginUserType.equalsIgnoreCase("account")){
			  spogServer.userLogin(this.accountEmailForPrepare, OrgPwdForPrepare,test);
		  }else if(loginUserType.equalsIgnoreCase("msp_account")){
			  spogServer.userLogin(this.mspAccountAdminEmailForPrepare, OrgPwdForPrepare,test);
		  }else if(loginUserType.equalsIgnoreCase("submspaccount")) {
			  spogServer.userLogin(this.final_sub_msp2_account1_user_email, sub_msp_password,test);
			  
		  }else if(loginUserType.equalsIgnoreCase("submspaccountadmin")) {
			  spogServer.userLogin(this.final_sub_msp1_msp_account_user_name_email, sub_msp_password,test);
			  
		  }else if(loginUserType.equalsIgnoreCase("submsp")) {
			  spogServer.userLogin(this.final_sub_msp2_user_name_email, sub_msp_password,test);
			  			  
		  }else if(loginUserType.equalsIgnoreCase("rootmspaccount")) {
			  spogServer.userLogin(this.final_root_msp_direct_org_user_email, sub_msp_password,test);
			  			  
		  }else if(loginUserType.equalsIgnoreCase("rootmspaccountadmin")) {
			  spogServer.userLogin(this.final_root_msp_account_admin_user_name_email, sub_msp_password,test);
			  			  
		  }else if(loginUserType.equalsIgnoreCase("rootmsp")) {
			  spogServer.userLogin(this.final_root_msp_user_name_email, sub_msp_password,test);
			  
		  }
		  spogServer.getVersionWithCheck("1.0", "dev");
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
