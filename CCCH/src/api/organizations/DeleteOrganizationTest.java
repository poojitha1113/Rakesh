package api.organizations;

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
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;

public class DeleteOrganizationTest extends base.prepare.Is4Org{
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
	  private String default_parent_id = "00000000-0000-0000-0000-000000000000";
	  private String datacenter_id= "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c";
	  private String datacenter_name= "Zetta Test";
	  private String mspOrgname,mspOrgname1,rootmspOrgname,rootmspaccountOrgname,rootmspaccountOrgname1,submspOrgname,submsp1Orgname;
	  private String submspaccountOrgname,submspaccountOrgname1,submsp1accountOrgname,submsp1accountOrgname1,accountOrgname,accountOrgname1;
	  private String directOrgname,directOrgname1;
	  
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
		  this.mspOrgname=spogServer.ReturnRandom(mspOrgNameForPrepare)+org_prefix;
		  this.mspOrgname1=spogServer.ReturnRandom(mspOrgNameForPrepare)+org_prefix;
		  this.rootmspOrgname=spogServer.ReturnRandom(mspOrgNameForPrepare)+org_prefix;
		  this.rootmspaccountOrgname=spogServer.ReturnRandom("spogqa_account")+org_prefix;
		  this.rootmspaccountOrgname1=spogServer.ReturnRandom("spogqa_account")+org_prefix;
		  this.submspOrgname=spogServer.ReturnRandom(mspOrgNameForPrepare)+org_prefix;
		  this.submsp1Orgname=spogServer.ReturnRandom(mspOrgNameForPrepare)+org_prefix;
		  this.submspaccountOrgname=spogServer.ReturnRandom("spogqa_account")+org_prefix;
		  this.submspaccountOrgname1=spogServer.ReturnRandom("spogqa_account")+org_prefix;
		  this.submsp1accountOrgname=spogServer.ReturnRandom("spogqa_account")+org_prefix;
		  this.submsp1accountOrgname1=spogServer.ReturnRandom("spogqa_account")+org_prefix;
		  this.accountOrgname=spogServer.ReturnRandom("spogqa_account")+org_prefix;
		  this.accountOrgname1=spogServer.ReturnRandom("spogqa_account")+org_prefix;
		  this.directOrgname=spogServer.ReturnRandom(directOrgNameForPrepare)+org_prefix;
		  this.directOrgname1=spogServer.ReturnRandom(directOrgNameForPrepare)+org_prefix;
		  spogServer.userLogin(this.csrAdmin, this.csrPwd);
		  String organization_id=spogServer.GetLoggedinUserOrganizationID();
		  this.csrOrgId=spogServer.GetLoggedinUserOrganizationID();
		  this.mspOrgId = spogServer.CreateOrganizationWithCheck(mspOrgname, SpogConstants.MSP_ORG, mspOrgEmailForPrepare, OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare);
		  this.mspOrgId1 = spogServer.CreateOrganizationWithCheck(mspOrgname1, SpogConstants.MSP_ORG, mspOrgEmailForPrepare1, OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare);
		  this.rootmspOrgId = spogServer.CreateOrganizationWithCheck(rootmspOrgname, SpogConstants.MSP_ORG, rootmspOrgEmailForPrepare, OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare);
		  org4SPOGServer.setToken(spogServer.getJWTToken());
		  org4SPOGServer.convertToRootMSP(rootmspOrgId);
		  this.rootmspaccountOrgId= spogServer.createAccountWithCheck(this.rootmspOrgId, rootmspaccountOrgname,"");
		  this.rootmspaccountOrgId1= spogServer.createAccountWithCheck(this.rootmspOrgId, rootmspaccountOrgname,"");
		  this.datacenter_id="91a9b48e-6ac6-4c47-8202-614b5cdcfe0c";
		  this.submspOrgId=org4SPOGServer.createSubMSPAccountincc(this.submspOrgname, rootmspOrgId, OrgFistNameForPrepare, OrgLastNameForPrepare, submspOrgEmailForPrepare, "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
		  this.submsp1OrgId=org4SPOGServer.createSubMSPAccountincc(this.submsp1Orgname, rootmspOrgId, OrgFistNameForPrepare, OrgLastNameForPrepare, submsp1OrgEmailForPrepare, "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
		  this.submspOrgEmailForPrepare= spogServer.ReturnRandom("d_jing_spogqa_submsp_org_prepar@arcserve.come");
		  this.submsp1OrgEmailForPrepare= spogServer.ReturnRandom("d_jing_spogqa_submsp1_org_prepar@arcserve.come");
		  spogServer.createUserAndCheck(submspOrgEmailForPrepare, OrgPwdForPrepare,  OrgFistNameForPrepare, OrgLastNameForPrepare, "msp_admin", submspOrgId, test);
		  spogServer.createUserAndCheck(submsp1OrgEmailForPrepare, OrgPwdForPrepare,  OrgFistNameForPrepare, OrgLastNameForPrepare, "msp_admin", submsp1OrgId, test);
		  this.submspaccountOrgId= spogServer.createAccountWithCheck(this.submspOrgId, this.submspaccountOrgname,"");
		  this.submspaccountOrgId1= spogServer.createAccountWithCheck(this.submspOrgId, this.submspaccountOrgname1,"");
		  this.submsp1accountOrgId= spogServer.createAccountWithCheck(this.submsp1OrgId, this.submsp1accountOrgname,"");
		  this.submsp1accountOrgId1= spogServer.createAccountWithCheck(this.submsp1OrgId, this.submsp1accountOrgname1,"");
		  
		  this.accountOrgId= spogServer.createAccountWithCheck(this.mspOrgId, this.accountOrgname,"");
		  this.accountOrgId1= spogServer.createAccountWithCheck(this.mspOrgId, this.accountOrgname1,"");
		  this.directOrgId = spogServer.CreateOrganizationWithCheck(this.directOrgname, SpogConstants.DIRECT_ORG, directOrgEmailForPrepare, OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare);
		  this.directOrgId1 = spogServer.CreateOrganizationWithCheck(this.directOrgname1, SpogConstants.DIRECT_ORG, directOrgEmailForPrepare1, OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare);
		  spogServer.createUserAndCheck(this.accountEmailForPrepare, OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare, SpogConstants.DIRECT_ADMIN, accountOrgId,test);
		  spogServer.createUserAndCheck(this.accountEmailForPrepare1, OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare, SpogConstants.DIRECT_ADMIN, accountOrgId1,test);
		  spogServer.createUserAndCheck(this.submspaccountEmailForPrepare, OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare, SpogConstants.DIRECT_ADMIN, submspaccountOrgId,test);
		  spogServer.createUserAndCheck(this.submspaccountEmailForPrepare1, OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare, SpogConstants.DIRECT_ADMIN, submspaccountOrgId1,test);
		  spogServer.createUserAndCheck(this.rootmspaccountEmailForPrepare, OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare, SpogConstants.DIRECT_ADMIN, rootmspaccountOrgId,test);
		  spogServer.createUserAndCheck(this.rootmspaccountEmailForPrepare1, OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare, SpogConstants.DIRECT_ADMIN, rootmspaccountOrgId1,test);
		  spogServer.createUserAndCheck(this.submsp1accountEmailForPrepare, OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare, SpogConstants.DIRECT_ADMIN, submsp1accountOrgId,test);
		  spogServer.createUserAndCheck(this.submsp1accountEmailForPrepare1, OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare, SpogConstants.DIRECT_ADMIN, submsp1accountOrgId1,test);
		  
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
		  mspAccountAdminUserIds[0]=spogServer.createUserAndCheck(mspAccountAdminEmailForPrepare, OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare, "msp_account_admin", mspOrgId, test);
		  rootmspAccountAdminUserIds[0]=spogServer.createUserAndCheck(rootmspAccountAdminEmailForPrepare, OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare, "msp_account_admin", rootmspOrgId, test);
		  submspAccountAdminUserIds[0]=spogServer.createUserAndCheck(submspAccountAdminEmailForPrepare, OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare, "msp_account_admin", submspOrgId, test);
		  submsp1AccountAdminUserIds[0]=spogServer.createUserAndCheck(submsp1AccountAdminEmailForPrepare, OrgPwdForPrepare, OrgFistNameForPrepare, OrgLastNameForPrepare, "msp_account_admin", submsp1OrgId, test);
		  spogServer.userLogin(mspAccountAdminEmailForPrepare, OrgPwdForPrepare);
		  this.msp_account_admin_token = spogServer.getJWTToken();
		  
		  Response response = userSpogServer.assignMspAccountAdmins(mspOrgId, accountOrgId, mspAccountAdminUserIds, csrToken);
		  userSpogServer.assignMspAccountAdminsWithCheck(response, mspAccountAdminUserIds, csrToken);
		  response = userSpogServer.assignMspAccountAdmins(rootmspOrgId, rootmspaccountOrgId, rootmspAccountAdminUserIds, csrToken);
		  userSpogServer.assignMspAccountAdminsWithCheck(response, rootmspAccountAdminUserIds, csrToken);
		  response = userSpogServer.assignMspAccountAdmins(submspOrgId, submspaccountOrgId, submspAccountAdminUserIds, csrToken);
		  userSpogServer.assignMspAccountAdminsWithCheck(response, submspAccountAdminUserIds, csrToken);
		  response = userSpogServer.assignMspAccountAdmins(submsp1OrgId, submsp1accountOrgId, submsp1AccountAdminUserIds, csrToken);
		  userSpogServer.assignMspAccountAdminsWithCheck(response, submsp1AccountAdminUserIds, csrToken);
		  spogServer.userLogin(this.mspAccountAdminEmailForPrepare, OrgPwdForPrepare);
	  }

	  @DataProvider(name = "allTypesorganizationInfoForBlocked")
	  public final Object[][] getAllTypesOrganizationInfo() {
			return new Object[][] { {"rootmsp"},{"submsp"},
				{"rootmspaccount"},{"rootmspaccountadmin"},{"submspaccount"},{"submspaccountadmin"},
				                    };
	  }
	  @Test(dataProvider = "allTypesorganizationInfoForBlocked")
	  public void getAllTypesForBlocked(String usertype
			  ){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  String org_prefix="AUTO_";
		  Response response=null;
		  if(usertype.equalsIgnoreCase("rootmsp")) {
			  spogServer.userLogin(this.rootmspOrgEmailForPrepare, OrgPwdForPrepare,test);
			  spogServer.DeleteOrganizationWithExpectedStatusCode(this.directOrgId, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,test);
			  spogServer.DeleteOrganizationWithExpectedStatusCode(this.mspOrgId, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,test);
			  spogServer.DeleteOrganizationWithExpectedStatusCode(this.submsp1accountOrgId, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,test);
		  }else if(usertype.equalsIgnoreCase("rootmspaccount")) {
			  spogServer.userLogin(this.rootmspaccountEmailForPrepare, OrgPwdForPrepare,test);
			  spogServer.DeleteOrganizationWithExpectedStatusCode(this.directOrgId, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,test);
			  spogServer.DeleteOrganizationWithExpectedStatusCode(this.mspOrgId, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,test);
			  spogServer.DeleteOrganizationWithExpectedStatusCode(this.submspaccountOrgId, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,test);
			  spogServer.DeleteOrganizationWithExpectedStatusCode(this.submsp1OrgId, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,test);
			  spogServer.DeleteOrganizationWithExpectedStatusCode(this.rootmspOrgId, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,test);
			  spogServer.DeleteOrganizationWithExpectedStatusCode(this.rootmspaccountOrgId1, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,test);
		  }else if(usertype.equalsIgnoreCase("rootmspaccountadmin")) {
			  spogServer.userLogin(this.rootmspAccountAdminEmailForPrepare, OrgPwdForPrepare,test);
			  spogServer.DeleteOrganizationWithExpectedStatusCode(this.directOrgId, SpogConstants.NOT_ALLOWED_ON_RESOURCE, ErrorCode.MSP_AA_CAN_NOT_DEL_ORG,test);
			  spogServer.DeleteOrganizationWithExpectedStatusCode(this.mspOrgId, SpogConstants.NOT_ALLOWED_ON_RESOURCE, ErrorCode.MSP_AA_CAN_NOT_DEL_ORG,test);
			  spogServer.DeleteOrganizationWithExpectedStatusCode(this.submspaccountOrgId, SpogConstants.NOT_ALLOWED_ON_RESOURCE, ErrorCode.MSP_AA_CAN_NOT_DEL_ORG,test);
			  spogServer.DeleteOrganizationWithExpectedStatusCode(this.submspOrgId, SpogConstants.NOT_ALLOWED_ON_RESOURCE, ErrorCode.MSP_AA_CAN_NOT_DEL_ORG,test);
			  spogServer.DeleteOrganizationWithExpectedStatusCode(this.rootmspOrgId, SpogConstants.NOT_ALLOWED_ON_RESOURCE, ErrorCode.MSP_AA_CAN_NOT_DEL_ORG,test);
			  spogServer.DeleteOrganizationWithExpectedStatusCode(this.rootmspaccountOrgId1, SpogConstants.NOT_ALLOWED_ON_RESOURCE, ErrorCode.MSP_AA_CAN_NOT_DEL_ORG,test);
		  }else if(usertype.equalsIgnoreCase("submsp")) {
			  spogServer.userLogin(this.submspOrgEmailForPrepare, OrgPwdForPrepare,test);
			  spogServer.DeleteOrganizationWithExpectedStatusCode(this.directOrgId, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,test);
			  spogServer.DeleteOrganizationWithExpectedStatusCode(this.mspOrgId, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,test);
			  spogServer.DeleteOrganizationWithExpectedStatusCode(this.submsp1accountOrgId, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,test);
			  spogServer.DeleteOrganizationWithExpectedStatusCode(this.submsp1OrgId, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,test);
			  spogServer.DeleteOrganizationWithExpectedStatusCode(this.rootmspOrgId, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,test);
			  spogServer.DeleteOrganizationWithExpectedStatusCode(this.rootmspaccountOrgId1, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,test);
		  }else if(usertype.equalsIgnoreCase("submspaccount")) {
			  spogServer.userLogin(this.submspaccountEmailForPrepare, OrgPwdForPrepare,test);
			  spogServer.DeleteOrganizationWithExpectedStatusCode(this.directOrgId, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,test);
			  spogServer.DeleteOrganizationWithExpectedStatusCode(this.mspOrgId, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,test);
			  spogServer.DeleteOrganizationWithExpectedStatusCode(this.submsp1accountOrgId, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,test);
			  spogServer.DeleteOrganizationWithExpectedStatusCode(this.submsp1OrgId, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,test);
			  spogServer.DeleteOrganizationWithExpectedStatusCode(this.rootmspOrgId, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,test);
			  spogServer.DeleteOrganizationWithExpectedStatusCode(this.rootmspaccountOrgId1, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,test);
		  }else if(usertype.equalsIgnoreCase("submspaccountadmin")) {
			  spogServer.userLogin(this.submspAccountAdminEmailForPrepare, OrgPwdForPrepare,test);
			  spogServer.DeleteOrganizationWithExpectedStatusCode(this.directOrgId, SpogConstants.NOT_ALLOWED_ON_RESOURCE, ErrorCode.MSP_AA_CAN_NOT_DEL_ORG,test);
			  spogServer.DeleteOrganizationWithExpectedStatusCode(this.mspOrgId, SpogConstants.NOT_ALLOWED_ON_RESOURCE, ErrorCode.MSP_AA_CAN_NOT_DEL_ORG,test);
			  spogServer.DeleteOrganizationWithExpectedStatusCode(this.submsp1accountOrgId, SpogConstants.NOT_ALLOWED_ON_RESOURCE, ErrorCode.MSP_AA_CAN_NOT_DEL_ORG,test);
			  spogServer.DeleteOrganizationWithExpectedStatusCode(this.submsp1OrgId, SpogConstants.NOT_ALLOWED_ON_RESOURCE, ErrorCode.MSP_AA_CAN_NOT_DEL_ORG,test);
			  spogServer.DeleteOrganizationWithExpectedStatusCode(this.rootmspOrgId, SpogConstants.NOT_ALLOWED_ON_RESOURCE, ErrorCode.MSP_AA_CAN_NOT_DEL_ORG,test);
			  spogServer.DeleteOrganizationWithExpectedStatusCode(this.rootmspaccountOrgId1, SpogConstants.NOT_ALLOWED_ON_RESOURCE, ErrorCode.MSP_AA_CAN_NOT_DEL_ORG,test);
		  }
	  }
  /*
   * 01. Csr admin: delete an msp organization, can't get it and its sub organization, users can't login.
   * 13. Csr admin can delete msp sub organization
   * 02. Csr admin: delete a direct organization;
   * 
   */
  //note: org name and email name we append random string to make unique.
  @DataProvider(name = "direct_mspsub_organizationInfo")
  public final Object[][] getDirect_Msp_Sub_OrganizationInfo() {
		return new Object[][] { { "d_jing_spogqa_direct_org" , SpogConstants.DIRECT_ORG, "d_jing_spogqa_direct_org@arcserve.com", "welcomeA02", "ci","direc_org"}, 
			                    { "d_jing_spogqa_msp_sub_org", SpogConstants.MSP_SUB_ORG, "d_jing_spogqa_msp_org@arcserve.com", "welcomeA02", "ci","msp_sub_org"},
			                    { "d_jing_spogqa_msp_org", SpogConstants.MSP_ORG, "d_jing_spogqa_msp_org@arcserve.com", "welcomeA02", "ci","msp_org"}, 
			                    { "d_jing_spogqa_msp_org", SpogConstants.MSP_ORG,  "","","",""},
			                    { "d_jing_spogqa_direct_org", SpogConstants.DIRECT_ORG, "","","",""}, 
			                    { "d_jing_spogqa_msp_sub_org", SpogConstants.MSP_SUB_ORG,  "","","",""}};
  }
  //@Test(dataProvider = "direct_mspsub_organizationInfo")
  public void csrDeleteDirectOrMspSubOrg(String organizationName, 
		  String organizationType,
		  String organizationEmail, 
		  String organizationPwd,
		  String organizationFirstName, 
		  String organizationLastName
		  ){	 
	  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
	  test.assignAuthor("Shan, Jing");
	  if(organizationEmail!=""){
		  organizationEmail=spogServer.ReturnRandom(organizationEmail);
	  }
	  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
	  test.log(LogStatus.INFO,"Login as csr admin");
	  String orgId;
	  if (organizationType.equalsIgnoreCase(SpogConstants.MSP_SUB_ORG)){
		  orgId = spogServer.createAccountWithCheck(this.mspOrgId, spogServer.ReturnRandom(organizationName)+org_prefix, "",test);
		  test.log(LogStatus.INFO,"Create msp sub organization successfully with org id:"+orgId);
		  if(organizationEmail!=""){
			spogServer.createUserAndCheck(organizationEmail, organizationPwd, organizationFirstName, organizationLastName, SpogConstants.DIRECT_ADMIN, orgId, test);
			test.log(LogStatus.INFO,"Can create msp account and return org id:"+orgId);
		  }	
	  }else{
		  orgId = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(organizationName)+org_prefix, organizationType,organizationEmail , organizationPwd, organizationFirstName, organizationLastName,test);
		  test.log(LogStatus.INFO,"Create organization successfully with org id:"+orgId);
	  }
	  spogServer.userLogin(this.mspAccountAdminEmailForPrepare, OrgPwdForPrepare);
	  spogServer.DeleteOrganizationWithExpectedStatusCode(orgId, SpogConstants.NOT_ALLOWED_ON_RESOURCE, ErrorCode.MSP_AA_CAN_NOT_DEL_ORG,test);
	  test.log(LogStatus.INFO,"msp Account admin can not delete org");
	  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
	  spogServer.DeleteOrganizationWithCheck(orgId,test);
	  test.log(LogStatus.INFO,"Csr admin can delete organization with organization type is:"+organizationType);
	  //user can't login
	  if(organizationEmail!=""){
		  spogServer.userLogin(organizationEmail, organizationPwd, SpogConstants.NOT_LOGGED_IN, test);
	  }
  }
  
  /*
   * 05. msp admin: delete its sub organization;
   * 01. Csr admin: delete an msp organization, can't get it and its sub organization, users can't login.
   */
  
  @DataProvider(name = "mspsub_organizationInfo")
  public final Object[][] getMsp_Sub_OrganizationInfo() {
		return new Object[][] { { "d_jing_spogqa_msp_sub_org", SpogConstants.MSP_ORG, "d_jing_spogqa_msp_org@arcserve.com", "welcomeA02", "ci","msp_org"},
			                    { "d_jing_spogqa_msp_sub_org", SpogConstants.MSP_ORG,  "","","",""}};
  }
  //@Test(dataProvider = "mspsub_organizationInfo")
  public void csrDeleteMspOrgWithManyChecks(String organizationName, 
		  String organizationType,
		  String organizationEmail, 
		  String organizationPwd,
		  String organizationFirstName, 
		  String organizationLastName
		  ){	 
	  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
	  test.assignAuthor("Shan, Jing");
	  String mspOrgEmailName="";
	  String mspSubOrgEmailName="";
	  String mspOrgId,mspOrgId1,mspSubOrgId;
	  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
	  test.log(LogStatus.INFO,"Login as csr admin");
	  if(organizationEmail!=""){
		  mspOrgEmailName= spogServer.ReturnRandom(organizationEmail);		  
	  }
	  mspSubOrgEmailName= spogServer.ReturnRandom("spogqa_sub@arcserve.com");
	  String orgname;
	  orgname = spogServer.ReturnRandom(organizationName);
	  mspOrgId = spogServer.CreateOrganizationWithCheck(orgname+org_prefix, organizationType, mspOrgEmailName ,organizationPwd, organizationFirstName, organizationLastName,test);
	  mspOrgId1 = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(organizationName)+org_prefix, organizationType, "" ,"", "", "",test);
	  test.log(LogStatus.INFO,"Create msp organization successfully with org id:"+mspOrgId);
	  //spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
	  mspSubOrgId = spogServer.createAccountWithCheck(mspOrgId, spogServer.ReturnRandom(organizationName)+org_prefix,"", test);
	  test.log(LogStatus.INFO,"Create msp sub organization successfully with org id:"+mspSubOrgId);
	  String mspSubOrgId1 = spogServer.createAccountWithCheck(mspOrgId, spogServer.ReturnRandom(organizationName)+org_prefix,"", test);
	  test.log(LogStatus.INFO,"Create msp sub organization successfully with org id:"+mspSubOrgId1);
	  spogServer.createUserAndCheck(mspSubOrgEmailName, this.OrgPwdForPrepare, this.OrgFistNameForPrepare, this.OrgLastNameForPrepare, SpogConstants.DIRECT_ADMIN, mspSubOrgId, test);
	  spogServer.userLogin(mspSubOrgEmailName, this.OrgPwdForPrepare,test);
	  spogServer.DeleteOrganizationWithExpectedStatusCode(mspSubOrgId, SpogConstants.NOT_ALLOWED_ON_RESOURCE, ErrorCode.NO_PERMISSION_DEL_ITSELF,test);
	  test.log(LogStatus.INFO,"Account admin can not delete itself");
	  spogServer.DeleteOrganizationWithExpectedStatusCode(mspSubOrgId1, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,test);
	  test.log(LogStatus.INFO,"Account admin can not delete other account org");
	  spogServer.userLogin(this.mspAccountAdminEmailForPrepare, OrgPwdForPrepare);
	  spogServer.DeleteOrganizationWithExpectedStatusCode(mspSubOrgId, SpogConstants.NOT_ALLOWED_ON_RESOURCE, ErrorCode.MSP_AA_CAN_NOT_DEL_ORG,test);
	  test.log(LogStatus.INFO,"msp Account admin can not delete org");
	  spogServer.DeleteOrganizationWithExpectedStatusCode(this.accountOrgId, SpogConstants.NOT_ALLOWED_ON_RESOURCE, ErrorCode.MSP_AA_CAN_NOT_DEL_ORG,test);
	  test.log(LogStatus.INFO,"msp Account admin can not delete org");
	  spogServer.DeleteOrganizationWithExpectedStatusCode(this.accountOrgId1, SpogConstants.NOT_ALLOWED_ON_RESOURCE, ErrorCode.MSP_AA_CAN_NOT_DEL_ORG,test);
	  test.log(LogStatus.INFO,"msp Account admin can not delete org");
	  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
	  if(organizationEmail!=""){
		  spogServer.userLogin(mspOrgEmailName, organizationPwd, test);
		  test.log(LogStatus.INFO,"Login as created msp admin successfully");
		  spogServer.DeleteOrganizationWithCheck(mspSubOrgId1,test);
		  test.log(LogStatus.INFO,"Msp admin delete msp sub organization with organization id is:"+mspSubOrgId1);
		  //spogServer.DeleteOrganizationWithExpectedStatusCode(mspOrgId, SpogConstants.NOT_ALLOWED_ON_RESOURCE, test);
		  //test.log(LogStatus.INFO,"Msp admin can not delete itself");
		  //spogServer.DeleteOrganizationWithExpectedStatusCode(mspOrgId1, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
		  //test.log(LogStatus.INFO,"Msp admin can not other msp org");
	  }	  
	  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
	  test.log(LogStatus.INFO,"Login as csr admin");
	  spogServer.DeleteOrganizationWithCheck(mspSubOrgId,test);
	  test.log(LogStatus.INFO,"Csr admin delete msp sub organization with organization id is:"+mspOrgId);
	  spogServer.DeleteOrganizationWithCheck(mspOrgId,test);
	  test.log(LogStatus.INFO,"Csr admin delete msp organization with organization id is:"+mspOrgId);
	  //can't get msp or msp sub organization
	  spogServer.GetDeletedOrganizationInfoWithExpectedStatusCode(mspOrgId, SpogConstants.RESOURCE_NOT_EXIST, ErrorCode.RESOURCE_NOT_FOUND_OR_REMOVED,test);
	  spogServer.GetDeletedOrganizationInfoWithExpectedStatusCode(mspSubOrgId1, SpogConstants.RESOURCE_NOT_EXIST, ErrorCode.RESOURCE_NOT_FOUND_OR_REMOVED,test);
	  spogServer.GetDeletedOrganizationInfoWithExpectedStatusCode(mspSubOrgId, SpogConstants.RESOURCE_NOT_EXIST,ErrorCode.RESOURCE_NOT_FOUND_OR_REMOVED, test);
	  //user can't login
	  if(organizationEmail!=""){
		  spogServer.userLogin(mspOrgEmailName, organizationPwd, SpogConstants.NOT_LOGGED_IN, test);
	  } 
	  spogServer.userLogin(mspSubOrgEmailName, this.OrgPwdForPrepare, SpogConstants.NOT_LOGGED_IN, test);
  }
  
  /*
   * 08. direct admin: cannot delete itself or other organization;
   * 06. msp admin: cannot delete itself or other msp/direct organization;
   */
  @DataProvider(name = "direct_msp_organizationInfo")
  public final Object[][] getDirect_Msp_OrganizationInfo() {
		return new Object[][] { { "d_jing_spogqa_direct_org" , SpogConstants.DIRECT_ORG, "d_jing_spogqa_direct_org@arcserve.com", "welcomeA02", "ci","direc_org"}, 
			                    { "d_jing_spogqa_msp_org", SpogConstants.MSP_ORG, "d_jing_spogqa_msp_org@arcserve.com", "welcomeA02", "ci","msp_org"},
			                    { "d_jing_spogqa_direct_org", SpogConstants.DIRECT_ORG, "","","",""}, 
			                    { "d_jing_spogqa_msp_org", SpogConstants.MSP_ORG,  "","","",""}};
  }
  //@Test(dataProvider = "direct_msp_organizationInfo")
  public void mspAndDirectCannotDeleteItselfAndOtherOrg(String organizationName, 
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
	  if(organizationEmail!=""){
		  organizationEmail = spogServer.ReturnRandom(organizationEmail);	  
	  }
	  String orgId = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(organizationName)+org_prefix, organizationType, organizationEmail, organizationPwd, organizationFirstName, organizationLastName,test); 
	  test.log(LogStatus.INFO,"Create organization successfully with org id:"+orgId);
	  if(organizationEmail!=""){
		  spogServer.userLogin(organizationEmail, organizationPwd,test);
		  test.log(LogStatus.INFO,"Login as the created organization admin");	
		  spogServer.DeleteOrganizationWithExpectedStatusCode(orgId, SpogConstants.NOT_ALLOWED_ON_RESOURCE,ErrorCode.NO_PERMISSION_DEL_ITSELF, test);
		  test.log(LogStatus.INFO,organizationType+" admin can't delete itself");
	  }
	  spogServer.userLogin(mspOrgEmailForPrepare, OrgPwdForPrepare,test);
	  test.log(LogStatus.INFO,"Login as prepared msp admin");
	  //spogServer.DeleteOrganizationWithExpectedStatusCode(orgId, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
	  //test.log(LogStatus.INFO,"Msp admin can't delete other organization such type is:"+organizationType);
	  spogServer.userLogin(directOrgEmailForPrepare, OrgPwdForPrepare,test);
	  test.log(LogStatus.INFO,"Login as prepared direct admin");
	  spogServer.DeleteOrganizationWithExpectedStatusCode(orgId, SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,test);
	  test.log(LogStatus.INFO,"Direct admin can't delete other organization such type is:"+organizationType);
  }
  
  //@Test(dataProvider = "direct_msp_organizationInfo")
  public void directCannotDeleteItselfOrDirectAndMspOrg(String organizationName, 
		  String organizationType,
		  String organizationEmail, 
		  String organizationPwd,
		  String organizationFirstName, 
		  String organizationLastName
		  ){	 
	  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
	  test.assignAuthor("Shan, Jing");
	  //login as msp admin
	  spogServer.userLogin(directOrgEmailForPrepare, OrgPwdForPrepare,test);
	  test.log(LogStatus.INFO,"Login as prepared direct admin");
	  //can not delete itself
	  spogServer.DeleteOrganizationWithExpectedStatusCode(this.directOrgId,SpogConstants.NOT_ALLOWED_ON_RESOURCE, ErrorCode.NO_PERMISSION_DEL_ITSELF, test);
	  test.log(LogStatus.INFO,"Direct admin can't delete itself");
	  //login as csr admin to create other organization
	  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
	  String orgId = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(organizationName)+org_prefix, organizationType, spogServer.ReturnRandom(organizationEmail), organizationPwd, organizationFirstName, organizationLastName,test);
	  //login as msp admin
	  spogServer.userLogin(directOrgEmailForPrepare, OrgPwdForPrepare,test);
	  test.log(LogStatus.INFO,"Login as prepared direct admin");
	  spogServer.DeleteOrganizationWithExpectedStatusCode(orgId, SpogConstants.INSUFFICIENT_PERMISSIONS,ErrorCode.RESOURCE_PERMISSION, test);
	  test.log(LogStatus.INFO,"Msp admin can't delete other organization such type is:"+organizationType);
  }  
  
  /*
   * 10. Can NOT delete an organization provided non-exist id;
   */
  @DataProvider(name = "nonExistingOrganizationId")
  public final Object[][] getNonExistingOrganizationId() {
		return new Object[][] { { "fadsfad", SpogConstants.MSP_ORG}, 
			                    { "", SpogConstants.MSP_ORG},
			                    { "FDSFGDFDSAFDA", SpogConstants.DIRECT_ORG}, 
			                    { "", SpogConstants.DIRECT_ORG},
			                    { "fadsfad", SpogConstants.MSP_SUB_ORG}, 
			                    { "", SpogConstants.MSP_SUB_ORG},
			                    {"uuid",SpogConstants.MSP_SUB_ORG},
			                    {"uuid",SpogConstants.MSP_ORG},
			                    {"uuid",SpogConstants.DIRECT_ORG},
			                    {null,SpogConstants.MSP_SUB_ORG},
			                    {null,SpogConstants.MSP_ORG},
			                    {null,SpogConstants.DIRECT_ORG}};
  }
  @Test(dataProvider = "nonExistingOrganizationId")
  public void deleteOrgByNonExistingOrgId(String organizationId,String organizationType){	 
	  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
	  test.assignAuthor("Shan, Jing");
	  int expectValue = SpogConstants.REQUIRED_INFO_NOT_EXIST;
	  String errorCode=ErrorCode.ELEMENT_NOT_UUID;
	  if (organizationType.equalsIgnoreCase(SpogConstants.MSP_SUB_ORG)){
		//login as msp admin
		spogServer.userLogin(mspOrgEmailForPrepare, OrgPwdForPrepare,test);
	  }else{
		//login as csr admin
		spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
	  }
	  if(organizationId==null){
		  expectValue = SpogConstants.REQUIRED_INFO_NOT_EXIST;
		  
	  }else if(organizationId==""){
		  expectValue = SpogConstants.NOT_ALLOWED_ON_RESOURCE;
		  errorCode=ErrorCode.NO_ALLOWED_GET_RESOURCE;
	  }else if(organizationId.equalsIgnoreCase("uuid")){
		  organizationId =spogServer.returnRandomUUID();
		  expectValue = SpogConstants.RESOURCE_NOT_EXIST;
		  errorCode=ErrorCode.RESOURCE_NOT_FOUND_OR_REMOVED;
	  }
	  spogServer.DeleteOrganizationWithExpectedStatusCode(organizationId, expectValue, errorCode, test);
	  test.log(LogStatus.INFO,"Delete organization by org id is:"+organizationId+" and org type is:"+organizationType);
	  spogServer.GetOrganizationInfobyIDWithExpectedStatusCode(organizationId, expectValue,errorCode, test);
	  test.log(LogStatus.INFO,"Can't delete org by invalid org id will return expected status code");
	  
  }
  
  /*
   * 18.  Can NOT call API if not logged in - 401when JWT is missing
   */
  @Test
  public void notLoggedCreateOrgFail(){	 
	  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
	  test.assignAuthor("Shan, Jing");
	  //set token as null
	  spogServer.setToken("");
	  spogServer.DeleteOrganizationWithExpectedStatusCode(this.mspOrgId, SpogConstants.NOT_LOGGED_IN, ErrorCode.AUTHORIZATION_HEADER_BLANK,test);
	  test.log(LogStatus.INFO,"If not login delete msp organization will return 401");
	  spogServer.DeleteOrganizationWithExpectedStatusCode(this.directOrgId, SpogConstants.NOT_LOGGED_IN, ErrorCode.AUTHORIZATION_HEADER_BLANK,test);
	  test.log(LogStatus.INFO,"If not login delete direct organization will return 401");
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
