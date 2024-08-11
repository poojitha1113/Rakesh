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
import org.testng.annotations.BeforeSuite;
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

public class EnhanceCreateOrganizationTest extends base.prepare.Is4Org{
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
	  private String csr_readonly="shaji02_csr_readonly@arcserve.com";
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
	  
	 //note: user is not csr admin can't call this api
	  @DataProvider(name = "userType")
	  public final Object[][] getUserType() {
			return new Object[][] { { SpogConstants.MSP_ORG},
				                    { SpogConstants.DIRECT_ORG}, 
				                    { SpogConstants.MSP_SUB_ORG}};
	  }
	  /*
	   * 01. csr admin can create direct organization
	   * 02. csr admin can create a msp organization;
	   * 08. can NOT create duplicate username under direct organization
	   * 09. can NOT create duplicate user name under msp organization.
	   */
	  @Test(dataProvider = "userType" ,priority=0)
	  public void notCsrCannotCallIt(String usertype ){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  if(usertype.equalsIgnoreCase(SpogConstants.MSP_SUB_ORG)){
			  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
			  String accountOrgEmail = spogServer.ReturnRandom("c_spog_qa@arcserve.com");
			  spogServer.createUserAndCheck( accountOrgEmail, OrgPwdForPrepare, "c_spog_qa_firstname",
						"c_spog_qa_lastname", SpogConstants.DIRECT_ADMIN, accountOrgId, test);
			  spogServer.userLogin(accountOrgEmail, this.OrgPwdForPrepare,test);
			  test.log(LogStatus.INFO,"Login as "+SpogConstants.MSP_SUB_ORG+" admin");
		  }else if(usertype.equalsIgnoreCase(SpogConstants.MSP_ORG)){
			  spogServer.userLogin(mspOrgEmailForPrepare, this.OrgPwdForPrepare,test);
			  test.log(LogStatus.INFO,"Login as "+SpogConstants.MSP_ORG+" admin");
		  }else if(usertype.equalsIgnoreCase(SpogConstants.DIRECT_ORG)){
			  spogServer.userLogin(directOrgEmailForPrepare, this.OrgPwdForPrepare,test);
			  test.log(LogStatus.INFO,"Login as "+SpogConstants.DIRECT_ORG+" admin");
		  }
		  spogServer.CreateOrganizationFailedWithExpectedStatusCode(spogServer.ReturnRandom("rerea")+org_prefix, SpogConstants.MSP_ORG,"" , "", "", "",SpogConstants.INSUFFICIENT_PERMISSIONS,ErrorCode.FORBIDDEN_TO_VISIT_RESOURCE,test);
		  test.log(LogStatus.INFO,usertype+" admin can not call this api" );
	  }
	  
	  //note: if user has fistname,lastname and password but without emailname will be ok
	  @DataProvider(name = "withoutEmail")
	  public final Object[][] getWithoutEmail() {
			return new Object[][] { { "c_jing_spogqa_direct_org" , SpogConstants.DIRECT_ORG, "", "Caworld_2017", "ci","direc_org"}, 
									{ "c_jing_spogqa_msp_org", SpogConstants.MSP_ORG, "", "Caworld_2017", "ci","msp_org"},
				                    };
	  }
	  /*
	   * 01. csr admin can create direct organization
	   * 02. csr admin can create a msp organization;
	   * 08. can NOT create duplicate username under direct organization
	   * 09. can NOT create duplicate user name under msp organization.
	   */
	  @Test(dataProvider = "withoutEmail" ,priority=0)
	  public void createOrgWithoutEmail(String organizationName, 
			  String organizationType,
			  String organizationEmail, 
			  String organizationPwd,
			  String organizationFirstName, 
			  String organizationLastName
			  ){	  
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
		  String orgId = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(organizationName)+org_prefix, organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,test);test.log(LogStatus.INFO,organizationType+" admin can not call this api" );
		  test.log(LogStatus.INFO,"can  create org without email settting" );
	  }
	  
	//note: if user has fistname,lastname and password but without emailname will throw exception
	  @DataProvider(name = "emailFormatError")
	  public final Object[][] getEmailFormatError() {
			return new Object[][] { { "c_jing_spogqa_direct_org" , SpogConstants.DIRECT_ORG, "df", "Caworld_2017", "ci","direc_org"},
				                    { "c_jing_spogqa_msp_org", SpogConstants.MSP_ORG, "dde", "Caworld_2017", "ci","msp_org"},
				                    { "c_jing_spogqa_msp_org", SpogConstants.MSP_SUB_ORG, "dde", "Caworld_2017", "ci","msp_org"}};
	  }
	  /*
	   * 01. csr admin can create direct organization
	   * 02. csr admin can create a msp organization;
	   * 08. can NOT create duplicate username under direct organization
	   * 09. can NOT create duplicate user name under msp organization.
	   */
	  @Test(dataProvider = "emailFormatError" ,priority=0)
	  public void createOrgEmailFormatError(String organizationName, 
			  String organizationType,
			  String organizationEmail, 
			  String organizationPwd,
			  String organizationFirstName, 
			  String organizationLastName
			  ){	  
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
		  spogServer.CreateOrganizationFailedWithExpectedStatusCode(spogServer.ReturnRandom(organizationName)+org_prefix, organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,SpogConstants.REQUIRED_INFO_NOT_EXIST,ErrorCode.EMAIL_FORMAT,test);
		  test.log(LogStatus.INFO,"can not create org with email format error" );
	  }
	  
	  //note: org name and email name we append random string to make unique.
	  @DataProvider(name = "mspOrDirectOrganizationInfo")
	  public final Object[][] getMspOrDirectOrganizationInfo() {
			return new Object[][] { { "c_jing_spogqa_direct_org" , SpogConstants.DIRECT_ORG, "c_jing_spogqa_direct_org@arcserve.com", "Caworld_2017", "ci","direc_org"}, 
				                    { "c_jing_spogqa_msp_org", SpogConstants.MSP_ORG, "c_jing_spogqa_msp_org@arcserve.com", "Caworld_2017", "ci","msp_org"},
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
		  String orgId = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(organizationName)+org_prefix, organizationType,organizationEmail , organizationPwd, organizationFirstName, organizationLastName,test);
		  test.log(LogStatus.INFO,"Csr admin can create "+ organizationType + " organization");
		  if (organizationEmail!=null ){
			  spogServer.CreateOrganizationFailedWithExpectedStatusCode(spogServer.ReturnRandom(organizationName)+org_prefix, organizationType,organizationEmail , organizationPwd, organizationFirstName, organizationLastName,SpogConstants.REQUIRED_INFO_NOT_EXIST,ErrorCode.EMAIL_EXISTS,test);
			  test.log(LogStatus.INFO,"Csr admin can not create "+ organizationType + " organization with duplicate username:"+organizationEmail );
		  }
		  if (organizationEmail!=null ){
			  spogServer.userLogin(organizationEmail, organizationPwd,test);
			  test.log(LogStatus.INFO,organizationType + " organization admin can login successfully");
		  }
		  //spogServer.DeleteOrganizationWithCheck(orgId,test);
	  }
	  
	  @DataProvider(name = "mspOrDirectOrganizationInfoWithUser")
	  public final Object[][] getMspOrDirectOrganizationInfoWithUser() {
			return new Object[][] { { "c_jing_spogqa_direct_org" , SpogConstants.DIRECT_ORG, "c_jing_spogqa_direct_org@arcserve.com", "Caworld_2017", "ci","direc_org"}, 
				                    { "c_jing_spogqa_msp_org", SpogConstants.MSP_ORG, "c_jing_spogqa_msp_org@arcserve.com", "Caworld_2017", "ci","msp_org"}};
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
		  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
		  test.log(LogStatus.INFO,"Login as csr admin");
		  organizationEmail= spogServer.ReturnRandom(organizationEmail);
		  String orgId = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(organizationName)+org_prefix, organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,test);
		  spogServer.CreateOrganizationFailedWithExpectedStatusCode(spogServer.ReturnRandom(organizationName)+org_prefix, organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,SpogConstants.REQUIRED_INFO_NOT_EXIST,ErrorCode.EMAIL_EXISTS,test);
		  test.log(LogStatus.INFO,"Csr admin can create "+ organizationType + " organization");
		  spogServer.userLogin(organizationEmail, organizationPwd,test);
		  test.log(LogStatus.INFO,organizationType + " organization admin can login successfully");
	  }
	  
	  @DataProvider(name = "incompleteOrganizationInfo")
	  public final Object[][] getIncompleteSubOrganizationInfo() {
			return new Object[][] { { "c_jing_spogqa_sub_org", "", "c_jing_spogqa_sub_org@arcserve.com", "Caworld_2017", "ci","msp_org"},
									{ "c_jing_spogqa_sub_org", "", "","","",""},
									{ "c_jing_spogqa_sub_org", null, "","","",""}};
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
		  
		  spogServer.CreateOrganizationFailedWithExpectedStatusCode(spogServer.ReturnRandom(organizationName)+org_prefix, organizationType, spogServer.ReturnRandom(organizationEmail), organizationPwd, organizationFirstName, organizationLastName,SpogConstants.REQUIRED_INFO_NOT_EXIST,ErrorCode.ELEMENT_BLANK,test);
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
		  spogServer.CreateOrganizationFailedWithExpectedStatusCode(spogServer.ReturnRandom(organizationName)+org_prefix, organizationType, spogServer.ReturnRandom(organizationEmail), organizationPwd, organizationFirstName, organizationLastName,SpogConstants.NOT_LOGGED_IN,ErrorCode.AUTHORIZATION_HEADER_BLANK,test);
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
	  }
	  
}
