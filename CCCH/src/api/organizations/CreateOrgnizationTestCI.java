package api.organizations;

import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.SpogConstants;
import InvokerServer.Org4SPOGServer;
import InvokerServer.SPOGServer;
import bsh.org.objectweb.asm.Constants;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import groovyjarjarantlr.PreservingFileWriter;
import io.restassured.response.Response;

import static org.testng.AssertJUnit.assertTrue;

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
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;

public class CreateOrgnizationTestCI {

  private SPOGServer spogServer;
  private String directOrgId;
  private String mspOrgId;
  private String mspSubOrgId;
  private String orgId;
  private String csrAdmin;
  private String csrPwd;
  
  //this is creating msp org or direct org for preparation
  private String mspOrgNameForPrepare="d_jing_spogqa_msp_org_prepare";
  private String mspOrgEmailForPrepare;
  private String directOrgNameForPrepare="d_jing_spogqa_direct_org_prepare";
  private String directOrgEmailForPrepare;
  private String OrgFistNameForPrepare="jing";
  private String OrgLastNameForPrepare="org_prepare";
  private String OrgPwdForPrepare="welcome*02";
  private String non_parent_id = "00000000-0000-0000-0000-000000000000";
  private ExtentReports rep;
  private ExtentTest test;
  //this is for update portal, each testng class is taken as BQ set
  private SQLServerDb bqdb1;
  public int Nooftest;
  private long creationTime;
  private String BQName=null;
  private String runningMachine;
  private testcasescount count1;
  private String buildVersion;
  private Org4SPOGServer org4SPOGServer;
  //end 
  @BeforeSuite
  @Parameters({ "baseURI", "port","csrAdminUserName", "csrAdminPassword"})
  public void beforeClass(String baseURI, String port, String csrAdminUserName, String csrAdminPassword){
	  spogServer = new SPOGServer(baseURI, port);
	  spogServer.checkSwagDocIsActive("http://qaspog2.zetta.net", 8080, SpogConstants.SUCCESS_GET_PUT_DELETE);
	  //spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(mspOrgNameForPrepare), SpogConstants.CSR_ORG, csrAdminUserName, csrAdminPassword, OrgFistNameForPrepare, OrgLastNameForPrepare);
  }
  @BeforeClass
  @Parameters({ "baseURI", "port","csrAdminUserName", "csrAdminPassword","logFolder","runningMachine","buildVersion"})
  public void beforeClass(String baseURI, String port, String csrAdminUserName, String csrAdminPassword, String logFolder, String runningMachine, String buildVersion) {
	  //this is for update portal
	  this.BQName = this.getClass().getSimpleName();
	  org4SPOGServer = new Org4SPOGServer(baseURI, port);
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
	  }
	  //end  
	  spogServer = new SPOGServer(baseURI, port);
	  rep = ExtentManager.getInstance("CreateOrganizationTest",logFolder);
	  this.csrAdmin = csrAdminUserName;
	  this.csrPwd = csrAdminPassword;
	  this.mspOrgEmailForPrepare = spogServer.ReturnRandom("d_jing_spogqa_msp_org_prepare");
	  this.directOrgEmailForPrepare = spogServer.ReturnRandom("d_jing_spogqa_direct_org_prepare@arcserve.com");
	  spogServer.userLogin(this.csrAdmin, this.csrPwd);
	  org4SPOGServer.setToken(spogServer.getJWTToken());
	  this.mspOrgId = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(mspOrgNameForPrepare), SpogConstants.MSP_ORG, mspOrgEmailForPrepare, OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare);
	  this.directOrgId = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(directOrgNameForPrepare), SpogConstants.DIRECT_ORG, directOrgEmailForPrepare, OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare);
  }
  
  //note: org name and email name we append random string to make unique.
  @DataProvider(name = "mspOrDirectOrganizationInfo")
  public final Object[][] getMspOrDirectOrganizationInfo() {
		return new Object[][] { { "c_jing_spogqa_direct_org" , SpogConstants.DIRECT_ORG, "c_jing_spogqa_direct_org@arcserve.com", "welcome*02", "ci","direc_org"}, 
			                    { "c_jing_spogqa_msp_org", SpogConstants.MSP_ORG, "c_jing_spogqa_msp_org@arcserve.com", "welcome*02", "ci","msp_org"},
			                    { "c_jing_spogqa_direct_org", SpogConstants.DIRECT_ORG, null,"","",""}, 
			                    { "c_jing_spogqa_msp_org", SpogConstants.MSP_ORG,  null,"","",""}};
  }
  /*
   * 01. csr admin can create direct organization
   * 02. csr admin can create a msp organization;
   * 08. can NOT create duplicate username under direct organization
   * 09. can NOT create duplicate user name under msp organization.
   */
  @Test(dataProvider = "mspOrDirectOrganizationInfo" ,priority=0)
  public void csrCreateMSPOrDirectOrg(String organizationName, 
		  String organizationType,
		  String organizationEmail, 
		  String organizationPwd,
		  String organizationFirstName, 
		  String organizationLastName
		  ){	 
	  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
	  test.assignAuthor("Shan, Jing");
	  //login as csr admin
	  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
	  test.log(LogStatus.INFO,"Login as csr admin");
	  if (organizationEmail!=null ){
		  organizationEmail = spogServer.ReturnRandom(organizationEmail);
	  }
	  String orgId = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(organizationName), organizationType,organizationEmail , organizationPwd, organizationFirstName, organizationLastName,test);
	  test.log(LogStatus.INFO,"Csr admin can create "+ organizationType + " organization");
	  if (organizationEmail!=null ){
		  spogServer.CreateOrganizationFailedWithExpectedStatusCode(spogServer.ReturnRandom(organizationName), organizationType,organizationEmail , organizationPwd, organizationFirstName, organizationLastName,SpogConstants.REQUIRED_INFO_NOT_EXIST,"00200002",test);
		  test.log(LogStatus.INFO,"Csr admin can not create "+ organizationType + " organization with duplicate username:"+organizationEmail );
	  }
	  if (organizationEmail!=null ){
		  spogServer.userLogin(organizationEmail, organizationPwd,test);
		  test.log(LogStatus.INFO,organizationType + " organization admin can login successfully");
	  }
	  org4SPOGServer.destroyOrganization(orgId, test);
  }
  
  @DataProvider(name = "mspOrDirectOrganizationInfoWithUser")
  public final Object[][] getMspOrDirectOrganizationInfoWithUser() {
		return new Object[][] { { "c_jing_spogqa_direct_org" , SpogConstants.DIRECT_ORG, "c_jing_spogqa_direct_org@arcserve.com", "welcome*02", "ci","direc_org"}, 
			                    { "c_jing_spogqa_msp_org", SpogConstants.MSP_ORG, "c_jing_spogqa_msp_org@arcserve.com", "welcome*02", "ci","msp_org"}};
  }
  
  /*
   * 06. can create msp organization with admin user information and logged as the admin successfully
   * 07. can create direct organization with admin user information and logged as the admin successfully
   */
  @Test(dataProvider = "mspOrDirectOrganizationInfoWithUser" ,priority=0)
  public void CreateDuplicateUserUnderDifferentOrgFail(String organizationName, 
		  String organizationType,
		  String organizationEmail, 
		  String organizationPwd,
		  String organizationFirstName, 
		  String organizationLastName
		  ){	 
	  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
	  test.assignAuthor("Shan, Jing");
	  organizationEmail= spogServer.ReturnRandom(organizationEmail);
	  String orgId = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(organizationName), organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,test);
	  spogServer.CreateOrganizationFailedWithExpectedStatusCode(spogServer.ReturnRandom(organizationName), organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,SpogConstants.REQUIRED_INFO_NOT_EXIST,"00200002",test);
	  test.log(LogStatus.INFO,"Csr admin can create "+ organizationType + " organization");
	  spogServer.userLogin(organizationEmail, organizationPwd,test);
	  test.log(LogStatus.INFO,organizationType + " organization admin can login successfully");
	  org4SPOGServer.destroyOrganization(orgId, test);
  }
  
  @DataProvider(name = "incompleteOrganizationInfo")
  public final Object[][] getIncompleteSubOrganizationInfo() {
		return new Object[][] { { "c_jing_spogqa_sub_org", "", "c_jing_spogqa_sub_org@arcserve.com", "welcome*02", "ci","msp_org"},
								{ "c_jing_spogqa_sub_org", "", "","","",""},
								{ "c_jing_spogqa_sub_org", null, "","","",""},
								{ "", SpogConstants.MSP_ORG, "c_jing_spogqa_sub_org@arcserve.com", "welcome*02", "ci","msp_org"},
			                    { "", SpogConstants.DIRECT_ORG, "","","",""},
			                    { "", SpogConstants.DIRECT_ORG, null,"","",""}};
  }
  
  /*
   * 05. Can not create organization with ogranization name is "" or null
   * 10. Can not create organization with type is "" or null
   */
  @Test(dataProvider = "incompleteOrganizationInfo")
  public void CreateOrgFailWithIncompleteInfo(String organizationName, 
		  String organizationType,
		  String organizationEmail, 
		  String organizationPwd,
		  String organizationFirstName, 
		  String organizationLastName
		  ){	 
	  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
	  test.assignAuthor("Shan, Jing");
	  //login as csr admin
	  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
	  test.log(LogStatus.INFO,"Login as csr admin");
	  spogServer.CreateOrganizationFailedWithExpectedStatusCode(spogServer.ReturnRandom(organizationName), organizationType, spogServer.ReturnRandom(organizationEmail), organizationPwd, organizationFirstName, organizationLastName,SpogConstants.REQUIRED_INFO_NOT_EXIST,"400",test);
	  test.log(LogStatus.INFO,"Can not create organization with incompleted information such as type or name");
  } 
  
  /*
   * 03. NOT login can create a direct organization
   * 04. NOT login can create a msp organization
   */
  
  @Test(dataProvider = "mspOrDirectOrganizationInfo")
  public void notLoggedCreateMspOrDirectOrg(String organizationName, 
		  String organizationType,
		  String organizationEmail, 
		  String organizationPwd,
		  String organizationFirstName, 
		  String organizationLastName
		  ){	 
	  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
	  test.assignAuthor("Shan, Jing");
	  //set token as null
	  spogServer.setToken("");
	  spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(organizationName), organizationType, spogServer.ReturnRandom(organizationEmail), organizationPwd, organizationFirstName, organizationLastName,this.non_parent_id,test);
	  test.log(LogStatus.INFO,"Not login still can create "+ organizationType +" organization will return 401");
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
	  org4SPOGServer.destroyOrganization(this.directOrgId, test);
	  org4SPOGServer.destroyOrganization(this.mspOrgId, test);
  }
    
}
