package api.organizations.freetrial;

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

import Constants.SpogConstants;
import InvokerServer.SPOGServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;

public class GetFreetrialClouddirectTest extends base.prepare.PrepareOrgInfo{
	@Parameters({ "pmfKey"})
	public GetFreetrialClouddirectTest(String pmfKey) {
		super(pmfKey);
		// TODO Auto-generated constructor stub
	}
	  private SPOGServer spogServer;

	  private String directOrgEmailForPrepare,mspOrgId,directOrgId;
	  private String accountOrgId;
	  private String csrAdmin;
	  private String csrPwd;

	  
	  private String mspOrgNameForPrepare="d_jing_spogqa_msp_org_prepare_jing";
	  private String accountEmailForPrepare ="d_jing_spogqa_account_org_prepare_jing_1@arcserve.com";
	  private String mspOrgEmailForPrepare="d_jing_spogqa_msp_org_prepar_jing@arcserve.come";
	  private String OrgFistNameForPrepare="jing";
	  private String OrgLastNameForPrepare="org_prepare";
	  private String OrgPwdForPrepare="welcomeA02";
	  private String sub_msp_pwd="Welcome*02";
	  private String  org_prefix=this.getClass().getSimpleName();
	  private ExtentTest test;

	  
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
				bqdb1.updateTable(this.BQName, runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0", String.valueOf(Nooftest), count1.getcreationtime(), "InProgress",author+" and Rest server is "+baseURI.split("//")[1]);
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
		  String directOrgNameForPrepare="direct_jing";
		  spogServer.userLogin(this.csrAdmin, this.csrPwd);

		  directOrgEmailForPrepare=spogServer.ReturnRandom("d_jing_spogqa_direct_org_prepar_jing@arcserve.come");
		  accountEmailForPrepare=spogServer.ReturnRandom(accountEmailForPrepare);
		  mspOrgNameForPrepare=spogServer.ReturnRandom(mspOrgNameForPrepare);
		  mspOrgEmailForPrepare=spogServer.ReturnRandom(mspOrgEmailForPrepare);
		  this.directOrgId = spogServer.CreateOrganizationByEnrollWithCheck(spogServer.ReturnRandom(directOrgNameForPrepare)+org_prefix, SpogConstants.DIRECT_ORG, directOrgEmailForPrepare, OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare);

		  this.mspOrgId = spogServer.CreateOrganizationByEnrollWithCheck(mspOrgNameForPrepare+org_prefix, SpogConstants.MSP_ORG, mspOrgEmailForPrepare, OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare);
		  this.accountOrgId= spogServer.createAccountWithCheck(this.mspOrgId, spogServer.ReturnRandom("spogqa_account_jing_1")+org_prefix,"");
		  spogServer.createUserAndCheck(this.accountEmailForPrepare, OrgPwdForPrepare, "dd", "gg", SpogConstants.DIRECT_ADMIN, accountOrgId,test);
		  prepare(baseURI, port, logFolder, csrAdminUserName, csrAdminPassword, this.getClass().getSimpleName() );
	  }
	  
	  @DataProvider(name = "checkClouddirect")
	  public final Object[][] checkClouddirect() {
			return new Object[][] {
				{"csr","direct",this.directOrgId},
				{"csr","msp",this.mspOrgId},
				{"csr","account",this.accountOrgId}};	
		}
	  
	  @Test(dataProvider = "checkClouddirect")
	  public void checkClouddirect(
			  String loginUserType,String checkOrg_type,String org_id) {
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  if (loginUserType.equalsIgnoreCase("csr")){
			  spogServer.userLogin(this.csrAdmin, this.csrPwd,test); 
		  }else if (loginUserType.equalsIgnoreCase("direct")){
			  spogServer.userLogin(this.directOrgId, OrgPwdForPrepare,test);
		  }else if(loginUserType.equalsIgnoreCase("msp")){
			  spogServer.userLogin(this.mspOrgId, OrgPwdForPrepare,test);
		  }else if(loginUserType.equalsIgnoreCase("account")){
			  spogServer.userLogin(this.accountOrgId, OrgPwdForPrepare,test);
		  }
		  spogServer.checkClouddirectInFreeTrial(spogServer.getJWTToken(), org_id,checkOrg_type,false,false);
    }
	  
	  @DataProvider(name = "checkClouddirectNoPermission")
	  public final Object[][] checkClouddirectNoPermission() {
			return new Object[][] {
				{"msp",this.directOrgId,403,"00900003","Forbidden to visit the resource."},
				{"msp",this.mspOrgId,403,"00900003","Forbidden to visit the resource."},
				{"msp",this.accountOrgId,403,"00900003","Forbidden to visit the resource."},
				{"direct",this.directOrgId,403,"00900003","Forbidden to visit the resource."},
				{"account",this.accountOrgId,403,"00900003","Forbidden to visit the resource."},
				{"direct",this.mspOrgId,403,"00900003","Forbidden to visit the resource."},
				{"account",this.mspOrgId,403,"00900003","Forbidden to visit the resource."},
				{"account",this.directOrgId,403,"00900003","Forbidden to visit the resource."},
				
				{"submspaccount",this.directOrgId,403,"00900003","Forbidden to visit the resource."},
				{"submspaccount",this.accountOrgId,403,"00900003","Forbidden to visit the resource."},
				{"submspaccountadmin",this.directOrgId,403,"00900003","Forbidden to visit the resource."},
				{"submspaccountadmin",this.accountOrgId,403,"00900003","Forbidden to visit the resource."},
				{"submsp",this.directOrgId,403,"00900003","Forbidden to visit the resource."},
				{"submsp",this.accountOrgId,403,"00900003","Forbidden to visit the resource."},
				{"rootmspaccount",this.directOrgId,403,"00900003","Forbidden to visit the resource."},
				{"rootmspaccount",this.accountOrgId,403,"00900003","Forbidden to visit the resource."},
				{"rootmspaccountadmin",this.directOrgId,403,"00900003","Forbidden to visit the resource."},
				{"rootmspaccountadmin",this.accountOrgId,403,"00900003","Forbidden to visit the resource."},
				{"rootmsp",this.directOrgId,403,"00900003","Forbidden to visit the resource."},
				{"rootmsp",this.accountOrgId,403,"00900003","Forbidden to visit the resource."},
			};		
		}
	  
	  @Test(dataProvider = "checkClouddirectNoPermission")
	  public void checkClouddirectNoPermission(
			  String loginUserType,String org_id,
			  int expect_status, String error_code, String error_message ) {
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  if (loginUserType.equalsIgnoreCase("csr")){
			  spogServer.userLogin(this.csrAdmin, this.csrPwd,test); 
		  }else if (loginUserType.equalsIgnoreCase("direct")){
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
		  spogServer.checkClouddirectInFreeTrialFailed(spogServer.getJWTToken(), org_id,expect_status,error_code,error_message);
    }
	  
	  @DataProvider(name = "checkClouddirectInvalidOrgId")
	  public final Object[][] checkClouddirectInvalidOrgId() {
			return new Object[][] {
				{"msp","",404,"00900001","Unable to find the resource."},
				{"msp",null,400,"40000005","The element organizationId is not a UUID."},
				{"msp",spogServer.returnRandomUUID(),403,"00900003","Forbidden to visit the resource."},
				{"direct","",404,"00900001","Unable to find the resource."},
				{"direct",null,400,"40000005","The element organizationId is not a UUID."},
				{"direct",spogServer.returnRandomUUID(),403,"00900003","Forbidden to visit the resource."},
				{"account","",404,"00900001","Unable to find the resource."},
				{"account",null,400,"40000005","The element organizationId is not a UUID."},
				{"account",spogServer.returnRandomUUID(),403,"00900003","Forbidden to visit the resource."}};	
		}
	  
	  @Test(dataProvider = "checkClouddirectInvalidOrgId")
	  public void checkClouddirectInvalidOrgId(
			  String loginUserType,String org_id,
			  int expect_status, String error_code, String error_message ) {
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  if (loginUserType.equalsIgnoreCase("csr")){
			  spogServer.userLogin(this.csrAdmin, this.csrPwd,test); 
		  }else if (loginUserType.equalsIgnoreCase("direct")){
			  spogServer.userLogin(this.directOrgEmailForPrepare, OrgPwdForPrepare,test);
		  }else if(loginUserType.equalsIgnoreCase("msp")){
			  spogServer.userLogin(this.mspOrgEmailForPrepare, OrgPwdForPrepare,test);
		  }else if(loginUserType.equalsIgnoreCase("account")){
			  spogServer.userLogin(this.accountEmailForPrepare, OrgPwdForPrepare,test);
		  }
		  spogServer.checkClouddirectInFreeTrialFailed(spogServer.getJWTToken(), org_id,expect_status,error_code,error_message);
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
