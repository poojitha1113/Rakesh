package api.organizations;

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
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class CreateAccountTest extends base.prepare.PrepareOrgInfo{
      @Parameters({ "pmfKey"})
	  public CreateAccountTest(String pmfKey) {
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
	  private String OrgPwdForPrepare="Welcome*02";
	  private String csrOrgId;
	  private String csrreadonly="csr_readonly@arcserve.com";
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
			  spogServer.createAccountFailedWithExpectedStatusCode(this.mspOrgId1, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.root_msp_org_id, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.sub_msp1_org_id, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.root_msp_direct_org_id, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.REQUIRED_INFO_NOT_EXIST,"00300002",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.sub_msp1_account1_id, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.REQUIRED_INFO_NOT_EXIST,"00300002",test);

          }else if(usertype.equalsIgnoreCase("msp")) {
			  spogServer.userLogin(this.mspOrgEmailForPrepare, OrgPwdForPrepare,test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.directOrgId, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.REQUIRED_INFO_NOT_EXIST,"00300002",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.root_msp_org_id, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.sub_msp1_org_id, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.root_msp_direct_org_id, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.REQUIRED_INFO_NOT_EXIST,"00300002",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.sub_msp1_account1_id, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.REQUIRED_INFO_NOT_EXIST,"00300002",test);

		  }else if(usertype.equalsIgnoreCase("account")) {
			  spogServer.userLogin(this.accountEmailForPrepare, OrgPwdForPrepare,test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.mspOrgId1, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.accountOrgId3, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.REQUIRED_INFO_NOT_EXIST,"00300002",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.directOrgId, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.REQUIRED_INFO_NOT_EXIST,"00300002",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.root_msp_org_id, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.sub_msp1_org_id, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.root_msp_direct_org_id, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.REQUIRED_INFO_NOT_EXIST,"00300002",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.sub_msp1_account1_id, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.REQUIRED_INFO_NOT_EXIST,"00300002",test);

		  }else if(usertype.equalsIgnoreCase("accountadmin")) {
			  spogServer.userLogin(this.mspAccountAdminEmailForPrepare, OrgPwdForPrepare,test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.mspOrgId1, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.accountOrgId3, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.REQUIRED_INFO_NOT_EXIST,"00300002",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.directOrgId, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.REQUIRED_INFO_NOT_EXIST,"00300002",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.root_msp_org_id, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.sub_msp1_org_id, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.root_msp_direct_org_id, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.REQUIRED_INFO_NOT_EXIST,"00300002",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.sub_msp1_account1_id, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.REQUIRED_INFO_NOT_EXIST,"00300002",test);

		  }else if(usertype.equalsIgnoreCase("rootmsp")) {
			  spogServer.userLogin(this.final_root_msp_user_name_email, OrgPwdForPrepare,test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.mspOrgId1, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.accountOrgId3, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.REQUIRED_INFO_NOT_EXIST,"00300002",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.directOrgId, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.REQUIRED_INFO_NOT_EXIST,"00300002",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.sub_msp1_org_id, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.root_msp_direct_org_id, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.REQUIRED_INFO_NOT_EXIST,"00300002",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.sub_msp1_account1_id, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.REQUIRED_INFO_NOT_EXIST,"00300002",test);

		  }else if(usertype.equalsIgnoreCase("rootmspaccount")) {
			  spogServer.userLogin(this.final_root_msp_direct_org_user_email, OrgPwdForPrepare,test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.mspOrgId1, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.accountOrgId3, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.REQUIRED_INFO_NOT_EXIST,"00300002",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.directOrgId, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.REQUIRED_INFO_NOT_EXIST,"00300002",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.sub_msp1_org_id, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.root_msp_direct_org_id, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.REQUIRED_INFO_NOT_EXIST,"00300002",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.sub_msp1_account1_id, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.REQUIRED_INFO_NOT_EXIST,"00300002",test);

		  }else if(usertype.equalsIgnoreCase("rootmspaccountadmin")) {
			  spogServer.userLogin(this.final_root_msp_account_admin_user_name_email, OrgPwdForPrepare,test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.mspOrgId1, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.accountOrgId3, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.REQUIRED_INFO_NOT_EXIST,"00300002",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.directOrgId, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.REQUIRED_INFO_NOT_EXIST,"00300002",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.sub_msp1_org_id, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.root_msp_direct_org_id, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.REQUIRED_INFO_NOT_EXIST,"00300002",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.sub_msp1_account1_id, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.REQUIRED_INFO_NOT_EXIST,"00300002",test);

		  }else if(usertype.equalsIgnoreCase("submsp")) {
			  spogServer.userLogin(this.final_sub_msp1_user_name_email, OrgPwdForPrepare,test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.mspOrgId1, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.accountOrgId3, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.REQUIRED_INFO_NOT_EXIST,"00300002",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.directOrgId, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.REQUIRED_INFO_NOT_EXIST,"00300002",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.sub_msp2_org_id, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.sub_msp2_account1_id, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.REQUIRED_INFO_NOT_EXIST,"00300002",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.root_msp_direct_org_id, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.REQUIRED_INFO_NOT_EXIST,"00300002",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.root_msp_org_id, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);

		  }else if(usertype.equalsIgnoreCase("submspaccount")) {
			  spogServer.userLogin(this.final_sub_msp1_account1_user_email, OrgPwdForPrepare,test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.mspOrgId1, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.accountOrgId3, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.REQUIRED_INFO_NOT_EXIST,"00300002",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.directOrgId, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.REQUIRED_INFO_NOT_EXIST,"00300002",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.sub_msp2_org_id, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.sub_msp2_account1_id, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.REQUIRED_INFO_NOT_EXIST,"00300002",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.root_msp_direct_org_id, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.REQUIRED_INFO_NOT_EXIST,"00300002",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.root_msp_org_id, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);

		  }else if(usertype.equalsIgnoreCase("submspaccountadmin")) {
			  spogServer.userLogin(this.final_sub_msp1_msp_account_user_name_email, OrgPwdForPrepare,test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.mspOrgId1, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.accountOrgId3, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.REQUIRED_INFO_NOT_EXIST,"00300002",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.directOrgId, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.REQUIRED_INFO_NOT_EXIST,"00300002",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.sub_msp2_org_id, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.sub_msp2_account1_id, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.REQUIRED_INFO_NOT_EXIST,"00300002",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.root_msp_direct_org_id, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.REQUIRED_INFO_NOT_EXIST,"00300002",test);
			  spogServer.createAccountFailedWithExpectedStatusCode(this.root_msp_org_id, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);

		  }
		  
	  }
	  
	  @DataProvider(name = "loginUserToCreateDifferentAccount")
	  public final Object[][] getLoginUserToCreateDifferentAccount() {
			return new Object[][] { {"csr","csr",""},{"csr","csr","same"},{"csr","csr","different"},
									{"csr","direct",""},{"csr","direct","same"},{"csr","direct","different"},
									{"csr","msp",""},{"csr","msp","same"},{"csr","msp","different"},
									{"csr","account",""},{"csr","account","same"},{"csr","account","different"},
									{"msp","csr",""},{"msp","csr","same"},{"msp","csr","different"},
									{"msp","direct",""},{"msp","direct","same"},{"msp","direct","different"},
									{"msp","msp",""},{"msp","msp","same"},{"msp","msp","different"},
									{"msp","account",""},{"msp","account","same"},{"msp","account","different"},
									{"direct","csr",""},{"direct","csr","same"},{"direct","csr","different"},
									{"direct","direct",""},{"direct","direct","same"},{"direct","direct","different"},
									{"direct","msp",""},{"direct","msp","same"},
									{"direct","account",""},{"direct","account","same"},{"direct","account","different"},
									{"account","csr",""},{"account","csr","same"},{"account","csr","different"},
									{"account","direct",""},{"account","direct","same"},{"account","direct","different"},
									{"account","msp",""},{"account","msp","same"},
									{"account","account",""},{"account","account","same"},{"account","account","different"}};
	  }
	  @Test(dataProvider = "loginUserToCreateDifferentAccount")
	  public void createAccountWithDifferentLoginUserAndDifferentParentId(String loginUserType,String parentOrgType, String bodyParentIdConditions){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  String parentIdInBody="";
		  String parentIdInUrl="";
		  String accountOrgName="";
		  String errorcode;
		  String accountEmailForPrepare = spogServer.ReturnRandom("d_jing_spogqa_account_org_prepare@arcserve.com");
		  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
		  test.log(LogStatus.INFO,"Login as csr admin");	
		  errorcode="40000005";
		  if(parentOrgType.equalsIgnoreCase("direct")||parentOrgType.equalsIgnoreCase("account")){
			  errorcode="00300002";
		  }
		  if (parentOrgType.equalsIgnoreCase("csr")){
			  parentIdInUrl = this.csrOrgId;
		  }else if(parentOrgType.equalsIgnoreCase("direct")){
			  parentIdInUrl = this.directOrgId;
		  }else if(parentOrgType.equalsIgnoreCase("msp")){
			  parentIdInUrl = this.mspOrgId;
		  }else if(parentOrgType.equalsIgnoreCase("account")){
			  parentIdInUrl = this.accountOrgId;
		  }
		  if (loginUserType.equalsIgnoreCase("csr")){
			  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
			  test.log(LogStatus.INFO,"Login as csr admin");			  
			  if(parentIdInBody.equalsIgnoreCase("same")){
				  parentIdInBody = this.csrOrgId;
			  }			  
			  if(parentIdInBody.equalsIgnoreCase("different")){
				  parentIdInBody = this.mspOrgId1;
				  spogServer.createAccountFailedWithExpectedStatusCode(parentIdInUrl, spogServer.ReturnRandom("spogqa_account")+org_prefix,parentIdInBody, SpogConstants.REQUIRED_INFO_NOT_EXIST, errorcode,test);
				  test.log(LogStatus.INFO,"Csr admin can't create account with parent id in url is different with parent id in body.");
			  }else{
				  if(parentOrgType.equalsIgnoreCase("msp")){
					  accountOrgName = spogServer.ReturnRandom("spogqa_account")+org_prefix;
					  spogServer.createAccountWithCheck(parentIdInUrl,accountOrgName ,parentIdInBody,test);
					  test.log(LogStatus.INFO,"Csr admin can create account with parent id is msp organization id.");
					  spogServer.createAccountWithCheck(parentIdInUrl,accountOrgName ,parentIdInBody,test);
					  test.log(LogStatus.INFO,"Csr admin can create duplicated account name under the same msp org.");
				  }else{
					  spogServer.createAccountFailedWithExpectedStatusCode(parentIdInUrl, spogServer.ReturnRandom("spogqa_account")+org_prefix,parentIdInBody, SpogConstants.REQUIRED_INFO_NOT_EXIST, errorcode,test);
					  test.log(LogStatus.INFO,"Csr admin can't create account with parent id is "+parentOrgType+" organization id.");
				  }				  
			  }			  
		  }else if(loginUserType.equalsIgnoreCase("msp")){
			  spogServer.userLogin(this.mspOrgEmailForPrepare, OrgPwdForPrepare,test);
			  test.log(LogStatus.INFO,"Login as prepared msp admin");
			  
			  if(parentIdInBody.equalsIgnoreCase("same")){
				  parentIdInBody = this.mspOrgId;
			  }
			  if(parentIdInBody.equalsIgnoreCase("different")){
				  parentIdInBody = this.mspOrgId1;
				  spogServer.createAccountFailedWithExpectedStatusCode(parentIdInUrl, spogServer.ReturnRandom("spogqa_account")+org_prefix,parentIdInBody, SpogConstants.REQUIRED_INFO_NOT_EXIST, errorcode,test);
				  test.log(LogStatus.INFO,"Csr admin can't create account with parent id in url is different with parent id in body.");
			  }else{
				  if(parentOrgType.equalsIgnoreCase("msp")){
					  accountOrgName = spogServer.ReturnRandom("spogqa_account")+org_prefix;
					  spogServer.createAccountWithCheck(parentIdInUrl, accountOrgName,parentIdInBody,test);
					  test.log(LogStatus.INFO,"Msp admin can create account with parent id is msp organization id.");
					  spogServer.createAccountWithCheck(parentIdInUrl,accountOrgName ,parentIdInBody,test);
					  test.log(LogStatus.INFO,"Msp admin can create duplicated account name under the same msp org.");
					  spogServer.createAccountFailedWithExpectedStatusCode(this.mspOrgId1, spogServer.ReturnRandom("spogqa_account")+org_prefix,"",SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
					  test.log(LogStatus.INFO,"Msp admin can not create account with parent id is another msp organization id.");
					  spogServer.createAccountFailedWithExpectedStatusCode(this.mspOrgId1, spogServer.ReturnRandom("spogqa_account")+org_prefix,this.mspOrgId1,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
					  test.log(LogStatus.INFO,"Msp admin can not create account with parent id is another msp organization id.");
//					  spogServer.createAccountFailedWithExpectedStatusCode(this.root_msp_org_id, spogServer.ReturnRandom("spogqa_account")+org_prefix,this.mspOrgId1,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
//					  test.log(LogStatus.INFO,"Msp admin can not create account with parent id is root msp organization id.");
//					  spogServer.createAccountFailedWithExpectedStatusCode(this.sub_msp1_org_id, spogServer.ReturnRandom("spogqa_account")+org_prefix,this.mspOrgId1,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
//					  test.log(LogStatus.INFO,"Msp admin can not create account with parent id is sub msp organization id.");
					  spogServer.createAccountFailedWithExpectedStatusCode(this.mspOrgId1, spogServer.ReturnRandom("spogqa_account")+org_prefix,this.mspOrgId,SpogConstants.REQUIRED_INFO_NOT_EXIST,"00300002",test);
					  test.log(LogStatus.INFO,"Msp admin can not create account with parent id is another msp organization id.");
				  }else{
					  spogServer.createAccountFailedWithExpectedStatusCode(parentIdInUrl, spogServer.ReturnRandom("spogqa_account")+org_prefix,parentIdInBody, SpogConstants.REQUIRED_INFO_NOT_EXIST, errorcode,test);
					  test.log(LogStatus.INFO,"Msp admin can't create account with parent id is "+ parentOrgType+" organization id.");
				  }				  
			  }
		  }else if(loginUserType.equalsIgnoreCase("direct")){
			  spogServer.userLogin(this.directOrgEmailForPrepare, OrgPwdForPrepare,test);
			  test.log(LogStatus.INFO,"Login as prepared direct admin");
			  if(parentIdInBody.equalsIgnoreCase("same")){
				  parentIdInBody = this.directOrgId;
			  }
			  if(parentIdInBody.equalsIgnoreCase("different")){
				  parentIdInBody = this.mspOrgId1;
				  spogServer.createAccountFailedWithExpectedStatusCode(parentIdInUrl, spogServer.ReturnRandom("spogqa_account")+org_prefix,parentIdInBody, SpogConstants.REQUIRED_INFO_NOT_EXIST, errorcode,test);
				  test.log(LogStatus.INFO,"Direct admin can't create account with parent id in url is different with parent id in body.");
			  }else{
				  if(parentOrgType.equalsIgnoreCase("msp")){
					  spogServer.createAccountFailedWithExpectedStatusCode(parentIdInUrl, spogServer.ReturnRandom("spogqa_account")+org_prefix,parentIdInBody, SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
					  test.log(LogStatus.INFO,"Direct admin can't create account with parent id is msp organization id.");
//					  spogServer.createAccountFailedWithExpectedStatusCode(this.root_msp_org_id, spogServer.ReturnRandom("spogqa_account")+org_prefix,this.mspOrgId1,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
//					  test.log(LogStatus.INFO,"Msp admin can not create account with parent id is root msp organization id.");
//					  spogServer.createAccountFailedWithExpectedStatusCode(this.sub_msp1_org_id, spogServer.ReturnRandom("spogqa_account")+org_prefix,this.mspOrgId1,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
//					  test.log(LogStatus.INFO,"Msp admin can not create account with parent id is sub msp organization id.");
				  }else{
					  spogServer.createAccountFailedWithExpectedStatusCode(parentIdInUrl, spogServer.ReturnRandom("spogqa_account")+org_prefix,parentIdInBody, SpogConstants.REQUIRED_INFO_NOT_EXIST, errorcode,test);
					  test.log(LogStatus.INFO,"Direct admin can't create account with parent id is "+ parentOrgType+" organization id.");
				  }	
			  }	
		  }else if(loginUserType.equalsIgnoreCase("account")){
			  spogServer.userLogin(this.mspOrgEmailForPrepare, OrgPwdForPrepare,test);
			  test.log(LogStatus.INFO,"Login as msp admin");
			  spogServer.createUserAndCheck(accountEmailForPrepare, OrgPwdForPrepare, "dd", "gg", SpogConstants.DIRECT_ADMIN, this.accountOrgId,test);
			  test.log(LogStatus.INFO,"Create account admin successfully");
			  spogServer.userLogin(accountEmailForPrepare, OrgPwdForPrepare,test);
			  test.log(LogStatus.INFO,"Login as account admin");
			  if(parentIdInBody.equalsIgnoreCase("same")){
				  parentIdInBody = this.accountOrgId;
			  }
			  if(parentIdInBody.equalsIgnoreCase("different")){
				  parentIdInBody = this.mspOrgId1;
				  spogServer.createAccountFailedWithExpectedStatusCode(parentIdInUrl, spogServer.ReturnRandom("spogqa_account")+org_prefix,parentIdInBody, SpogConstants.REQUIRED_INFO_NOT_EXIST, errorcode,test);
				  test.log(LogStatus.INFO,"Account admin can't create account with parent id in url is different with parent id in body.");
			  }else{
				  if(parentOrgType.equalsIgnoreCase("msp")){
					  spogServer.createAccountFailedWithExpectedStatusCode(parentIdInUrl, spogServer.ReturnRandom("spogqa_account")+org_prefix,parentIdInBody,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101",test);
					  test.log(LogStatus.INFO,"Account admin can not create account with parent id is msp organization id.");
				  }else{
					  spogServer.createAccountFailedWithExpectedStatusCode(parentIdInUrl, spogServer.ReturnRandom("spogqa_account")+org_prefix,parentIdInBody, SpogConstants.REQUIRED_INFO_NOT_EXIST,errorcode, test);
					  test.log(LogStatus.INFO,"Account admin can't create account with parent id is "+ parentOrgType+" organization id.");
				  }
			  }	
		  }		  
	  }
	  
	  @DataProvider(name = "nonExistingOrInvalidParentId")
	  public final Object[][] getNonExistingOrInvalidParentId() {
			return new Object[][] { {"csr", "fadsfad"}, {"csr","uuid"}, 
									{"direct", "fadsfad"}, {"direct","uuid"}, {"direct","deleted"},
									{"account", "fadsfad"}, {"account","uuid"}, {"account","deleted"}};
	  }
	  @Test(dataProvider = "nonExistingOrInvalidParentId")
	  public void CanNotCreateAccountWithInvalidParentId(String loginUserType,String parentId){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  String accountEmailForPrepare = spogServer.ReturnRandom("d_jing_spogqa_account_org_prepare@arcserve.com");
		  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
		  test.log(LogStatus.INFO,"Login as csr admin");
		  String errorcode=ErrorCode.ELEMENT_NOT_UUID;
		  int Generalcode=SpogConstants.REQUIRED_INFO_NOT_EXIST;
		  if(parentId.equalsIgnoreCase("uuid")){
			  parentId=spogServer.returnRandomUUID();
			  errorcode=ErrorCode.CAN_NOT_FIND_ORG;
			  Generalcode=SpogConstants.RESOURCE_NOT_EXIST;
		  }else if(parentId.equalsIgnoreCase("deleted")){
			  errorcode=ErrorCode.INVALID_CREATING_ACCOUNT;
		  }
		  if (loginUserType.equalsIgnoreCase("csr")){
			  spogServer.createAccountFailedWithExpectedStatusCode(parentId, spogServer.ReturnRandom("spogqa_account")+org_prefix, "",Generalcode, errorcode,test);
			  test.log(LogStatus.INFO,"Csr admin can not create account with parent id is deleted or non/invalid csr org id will be failed");
		  }else if(loginUserType.equalsIgnoreCase("direct")){
			  if(parentId.equalsIgnoreCase("deleted")){
				  parentId = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(mspOrgNameForPrepare)+org_prefix, SpogConstants.DIRECT_ORG, "", "", "", "",test);
				  test.log(LogStatus.INFO,"Create another direct org");
				  spogServer.DeleteOrganizationWithCheck(parentId, test);
				  test.log(LogStatus.INFO,"Delete the direct org which org id:"+parentId);				  				  
			  }
			  spogServer.userLogin(this.directOrgEmailForPrepare, OrgPwdForPrepare,test);
			  test.log(LogStatus.INFO,"Login as prepared direct admin");
			  spogServer.createAccountFailedWithExpectedStatusCode(parentId, spogServer.ReturnRandom("spogqa_account")+org_prefix, "",Generalcode, errorcode,test);
			  test.log(LogStatus.INFO,"Direct admin can not create account with parent id is deleted or non/invalid direct org id will be failed");
			  spogServer.userLogin(this.mspAccountAdminEmailForPrepare, OrgPwdForPrepare);
			  test.log(LogStatus.INFO,"Login as msp account admin");
			  spogServer.createAccountFailedWithExpectedStatusCode(this.mspOrgId, spogServer.ReturnRandom("spogqa_account")+org_prefix, this.mspOrgId,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,test);
			  test.log(LogStatus.INFO,"msp admin can not create account under his msp org");
			  spogServer.createAccountFailedWithExpectedStatusCode(this.mspOrgId1, spogServer.ReturnRandom("spogqa_account")+org_prefix, this.mspOrgId1,SpogConstants.INSUFFICIENT_PERMISSIONS, ErrorCode.RESOURCE_PERMISSION,test);
			  test.log(LogStatus.INFO,"msp admin can not create account under other msp org");
		  }else if(loginUserType.equalsIgnoreCase("msp")){
			  if(parentId.equalsIgnoreCase("deleted")){
				  parentId = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom(mspOrgNameForPrepare)+org_prefix, SpogConstants.MSP_ORG, "", "", "", "",test);
				  test.log(LogStatus.INFO,"Create another msp org");
				  spogServer.DeleteOrganizationWithCheck(parentId, test);
				  test.log(LogStatus.INFO,"Delete the msp org which org id:"+parentId);				  
			  }			  
			  spogServer.userLogin(this.mspOrgEmailForPrepare, OrgPwdForPrepare,test);
			  test.log(LogStatus.INFO,"Login as prepared msp admin");
			  spogServer.createAccountFailedWithExpectedStatusCode(parentId, spogServer.ReturnRandom("spogqa_account")+org_prefix, "",Generalcode,errorcode, test);
			  test.log(LogStatus.INFO,"Msp admin can not create account with parent id is deleted or non/invalid msp org id will be failed");
			  spogServer.userLogin(this.csrAdmin, this.csrPwd,test);
			  test.log(LogStatus.INFO,"Login as csr admin");
			  spogServer.createAccountFailedWithExpectedStatusCode(parentId, spogServer.ReturnRandom("spogqa_account")+org_prefix, "",Generalcode, errorcode,test);
			  test.log(LogStatus.INFO,"Csr admin can not create account with parent id is deleted or non/invalid msp org id will be failed");
		  }else if(loginUserType.equalsIgnoreCase("account")){
			  String parentId_pre= spogServer.createAccountWithCheck(this.mspOrgId,spogServer.ReturnRandom(mspOrgNameForPrepare)+org_prefix ,"",test);
			  test.log(LogStatus.INFO,"Create account successfully which org id:"+parentId_pre);
			  spogServer.createUserAndCheck(accountEmailForPrepare, OrgPwdForPrepare, "dd", "gg", SpogConstants.DIRECT_ADMIN, parentId_pre,test);
			  test.log(LogStatus.INFO,"Create account admin successfully");
			  if(parentId.equalsIgnoreCase("deleted")){
				  parentId= spogServer.createAccountWithCheck(this.mspOrgId,spogServer.ReturnRandom(mspOrgNameForPrepare)+org_prefix ,"",test);
				  test.log(LogStatus.INFO,"Create account successfully which org id:"+parentId);
				  spogServer.DeleteOrganizationWithCheck(parentId, test);
				  test.log(LogStatus.INFO,"Delete the direct org which org id:"+parentId);			  
			  }
			  spogServer.userLogin(accountEmailForPrepare, OrgPwdForPrepare,test);
			  test.log(LogStatus.INFO,"Login as account admin");
			  spogServer.createAccountFailedWithExpectedStatusCode(parentId, spogServer.ReturnRandom("spogqa_account")+org_prefix, "",Generalcode, errorcode,test);
			  test.log(LogStatus.INFO,"Account admin can not create account with parent id is deleted or non/invalid Account org id will be failed");
		  }
	  }
	  
	  @DataProvider(name = "InvalidOrganizationName")
	  public final Object[][] getInvalidOrganizationName() {
			return new Object[][] { { ""}, {null}};
	  }
	  @Test(dataProvider = "InvalidOrganizationName")
	  public void CanNotCreateByNoneOrgName(String orgName){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  spogServer.createAccountFailedWithExpectedStatusCode(this.directOrgId, orgName, "",SpogConstants.REQUIRED_INFO_NOT_EXIST,ErrorCode.ELEMENT_BLANK, test);
		  spogServer.createAccountFailedWithExpectedStatusCode(this.csrOrgId, orgName, "",SpogConstants.REQUIRED_INFO_NOT_EXIST,ErrorCode.ELEMENT_BLANK, test);
		  //spogServer.createAccountFailedWithExpectedStatusCode(this.mspOrgId, orgName,"", SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
		  test.log(LogStatus.INFO,"Create account without login will be failed");
	  }
	  
	  @Test
	  public void notLoggedCreateAccount(){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  //set token as null
		  spogServer.setToken("");
		  spogServer.createAccountFailedWithExpectedStatusCode(this.directOrgId, spogServer.ReturnRandom("spogqa_account")+org_prefix,"", SpogConstants.NOT_LOGGED_IN,ErrorCode.AUTHORIZATION_HEADER_BLANK, test);
		  spogServer.createAccountFailedWithExpectedStatusCode(this.csrOrgId, spogServer.ReturnRandom("spogqa_account")+org_prefix, "",SpogConstants.NOT_LOGGED_IN, ErrorCode.AUTHORIZATION_HEADER_BLANK,test);
		  spogServer.createAccountFailedWithExpectedStatusCode(this.mspOrgId, spogServer.ReturnRandom("spogqa_account")+org_prefix,"", SpogConstants.NOT_LOGGED_IN, ErrorCode.AUTHORIZATION_HEADER_BLANK,test);
	  }
	  
	  @Test
	  public void csrReadOnlyNotCreateFilter(){	 
		  test=ExtentManager.getNewTest(this.getClass().getName()+"."+Thread.currentThread() .getStackTrace()[1].getMethodName());
		  test.assignAuthor("Shan, Jing");
		  //set token as null
		  spogServer.userLogin(this.csrreadonly, "Caworld_2017",test);
		  test.log(LogStatus.INFO,"Login as csr read only");	
		  spogServer.createAccountFailedWithExpectedStatusCode(this.mspOrgId, spogServer.ReturnRandom("spogqa_account")+org_prefix,this.csrOrgId, SpogConstants.INSUFFICIENT_PERMISSIONS,ErrorCode.RESOURCE_PERMISSION,test);
		  spogServer.deleteMSPAccountWithExpectedStatusCode(this.mspOrgId, this.accountOrgId, SpogConstants.INSUFFICIENT_PERMISSIONS,ErrorCode.RESOURCE_PERMISSION,test);
		  spogServer.updateAccountFailedWithExpectedStatusCode(this.mspOrgId, accountOrgId, "ddd", SpogConstants.INSUFFICIENT_PERMISSIONS,ErrorCode.RESOURCE_PERMISSION,test);
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
