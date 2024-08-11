package api;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.SpogConstants;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import InvokerServer.Org4SPOGServer;
import bsh.org.objectweb.asm.Constants;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import groovyjarjarantlr.PreservingFileWriter;
import io.restassured.response.Response;

import static org.hamcrest.Matchers.equalTo;
import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;
import java.util.Arrays;

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

import Constants.SpogConstants;
import InvokerServer.SPOGServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;

import io.restassured.matcher.ResponseAwareMatcher;
import org.hamcrest.Matcher;

public class UpdateLoggedInUserTest extends base.prepare.Is4Org{
	  private SPOGServer spogServer;
	  private UserSpogServer userSpogServer;
	  private Org4SPOGServer org4SpogServer;
	  //private ExtentReports rep;
	  private ExtentTest test;
	  private String csrLogInUserName;
	  private String csrLogInPassword;
	  private String csrToken;
	  private String sharePassword ="Caworld_2018";
	  private String csrOrg_id;
	  private String directOrg_id;
	  private String directOrg2_id;
	  private String mspOrg_id;
	  private String mspOrg2_id;
	  private String account_id;
	  private String account2_id;
	  private String  org_prefix=this.getClass().getSimpleName();
	  //add root msp, sub msp, accounts;
	  private String root_msp1_id;
	  private String root_msp2_id;
	  private String subMSP1_root1_id;
	  private String subMSP2_root1_id;
	  private String subMSP1_root2_id;
	  private String subMSP2_root2_id;
	  private String account_rootMSP1_id;
	  private String account_rootMSP2_id;
	  private String account_subMSP1_root1_id;
	  private String account_subMSP2_root1_id;
	  private String account_subMSP1_root2_id;
	  private String account_subMSP2_root2_id;
	  //this is for update portal, each testng class is taken as BQ set
	  //private SQLServerDb bqdb1;
	  //public int Nooftest;
	  //private long creationTime;
	  //private String BQName=null;
	  //private String runningMachine;
	  //private testcasescount count1;
	  //private String buildVersion;
	  //end 

	  
	  @BeforeClass
	  @Parameters({ "baseURI", "port" , "csrAdminUserName", "csrAdminPassword","logFolder", "runningMachine","buildVersion"})
	  public void beforeClass(String baseURI, String port, String userName, String password, String logFolder, String runningMachine, String buildVersion) {
		  rep = ExtentManager.getInstance("UpdateLoggedInUserTest",logFolder);
		  spogServer = new SPOGServer(baseURI, port);
		  userSpogServer = new UserSpogServer(baseURI, port);
		  org4SpogServer = new Org4SPOGServer(baseURI, port);
		  
		  spogServer.userLogin(userName, password);
		  this.csrLogInUserName = userName;
		  this.csrLogInPassword = password;
		  this.csrToken =spogServer.getJWTToken();
		  
          //create org
		  csrOrg_id = spogServer.GetLoggedinUserOrganizationID();
		  directOrg_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_update_direct1_yuefen")+org_prefix,"direct",spogServer.ReturnRandom("spogqa_direct1_update@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("direct_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  directOrg2_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_update_direct2_yuefen")+org_prefix,"direct",spogServer.ReturnRandom("spogqa_direct2_update@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("direct_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  mspOrg_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_update_yuefen_msp1")+org_prefix,"msp",spogServer.ReturnRandom("spogqa_update_msp1@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("direct_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  mspOrg2_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_update_yuefen_msp2")+org_prefix,"msp",spogServer.ReturnRandom("spogqa_update_msp2@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("direct_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  account_id = spogServer.createAccountWithCheck(mspOrg_id,spogServer.ReturnRandom("accoun1_msp1_yuefen_spogqa")+org_prefix, mspOrg_id, test);
		  account2_id = spogServer.createAccountWithCheck(mspOrg2_id, spogServer.ReturnRandom("accoun2_msp2_yuefen_spogqa")+org_prefix, mspOrg2_id, test);
		  //create root msp, sub msp, accounts
		  root_msp1_id=spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_update_yuefen_rootmsp1")+org_prefix,"msp",spogServer.ReturnRandom("spogqa_update_rootmsp1@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("direct_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  //root_msp2_id=spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_update_yuefen_rootmsp2")+org_prefix,"msp",spogServer.ReturnRandom("spogqa_update_rootmsp2@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("direct_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  
		  System.out.println("convert to root msp starts");
		  org4SpogServer.setToken(csrToken);
		  org4SpogServer.convertToRootMSP(root_msp1_id);
		  //org4SpogServer.convertToRootMSP(root_msp2_id);
		  
		  subMSP1_root1_id=org4SpogServer.createSubMSPAccountincc(spogServer.ReturnRandom("spogqa_update_yuefen_submsp1_rootmsp1")+org_prefix, root_msp1_id,spogServer.ReturnRandom("submsp1_rootmsp1_spogqa"),spogServer.ReturnRandom("liu_spogqa"),spogServer.ReturnRandom("spogqa_update_submsp1_rootmsp1@spogqa.com"), "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
		  //subMSP2_root1_id=org4SpogServer.createSubMSPAccountincc(spogServer.ReturnRandom("spogqa_update_yuefen_submsp1_rootmsp1")+org_prefix, root_msp1_id,spogServer.ReturnRandom("submsp2_rootmsp1_spogqa"),spogServer.ReturnRandom("liu_spogqa"),spogServer.ReturnRandom("spogqa_update_submsp2_rootmsp1@spogqa.com"), "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
		  
		  //subMSP1_root2_id=org4SpogServer.createSubMSPAccountincc(spogServer.ReturnRandom("spogqa_update_yuefen_submsp1_rootmsp2")+org_prefix, root_msp2_id,spogServer.ReturnRandom("submsp1_rootmsp2_spogqa"),spogServer.ReturnRandom("liu_spogqa"),spogServer.ReturnRandom("spogqa_update_submsp1_rootmsp2@spogqa.com"), "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
		  //subMSP2_root2_id=org4SpogServer.createSubMSPAccountincc(spogServer.ReturnRandom("spogqa_update_yuefen_submsp2_rootmsp2")+org_prefix, root_msp2_id,spogServer.ReturnRandom("submsp2_rootmsp2_spogqa"),spogServer.ReturnRandom("liu_spogqa"),spogServer.ReturnRandom("spogqa_update_submsp2_rootmsp2@spogqa.com"), "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test); 
		  
		  account_rootMSP1_id=spogServer.createAccountWithCheck(root_msp1_id,spogServer.ReturnRandom("accoun1_rootmsp1_yuefen_spogqa")+org_prefix, root_msp1_id, test);
		  //account_rootMSP2_id=spogServer.createAccountWithCheck(root_msp2_id,spogServer.ReturnRandom("accoun1_rootmsp2_yuefen_spogqa")+org_prefix, root_msp2_id, test);
		  account_subMSP1_root1_id=spogServer.createAccountWithCheck(subMSP1_root1_id,spogServer.ReturnRandom("accoun1_submsp1_rootmsp1_yuefen_spogqa")+org_prefix, subMSP1_root1_id, test);
		  //account_subMSP2_root1_id=spogServer.createAccountWithCheck(subMSP2_root1_id,spogServer.ReturnRandom("accoun1_submsp1_rootmsp1_yuefen_spogqa")+org_prefix, subMSP2_root1_id, test);
		  //account_subMSP1_root2_id=spogServer.createAccountWithCheck(subMSP1_root2_id,spogServer.ReturnRandom("accoun1_submsp1_rootmsp1_yuefen_spogqa")+org_prefix, subMSP1_root2_id, test);
		  //account_subMSP2_root2_id=spogServer.createAccountWithCheck(subMSP2_root2_id,spogServer.ReturnRandom("accoun1_submsp1_rootmsp1_yuefen_spogqa")+org_prefix, subMSP2_root2_id, test);
		  
		  //this is for update portal
          this.BQName = this.getClass().getSimpleName();
          String author = "Yuefen.liu";
          this.runningMachine = runningMachine;
          SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
          java.util.Date date=new java.util.Date();
          this.buildVersion=buildVersion+"_"+dateFormater.format(date);
          Nooftest=0;
          bqdb1 = new SQLServerDb();
          count1 = new testcasescount();
          if(count1.isstarttimehit()==0) {
              System.out.println("UpdateLoggedInUserTest");
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
          setEnv(baseURI,  port,  userName, password);
          //end


	  }
	  @DataProvider(name="userInfo5")
	  public final Object[][] getUserInfo5() {
		  return new Object[][] { 
					  {spogServer.ReturnRandom("update_csr_admin@spogqa.com"), sharePassword, "betty", "liu", "csr_admin", csrOrg_id, "","","","csr_read_only",SpogConstants.SUCCESS_GET_PUT_DELETE}, 
					  {spogServer.ReturnRandom("update_csr_read_only@spogqa.com"), sharePassword, "betty", "liu", "csr_read_only", csrOrg_id, "","","","direct_admin",SpogConstants.SUCCESS_GET_PUT_DELETE}, 
					  {spogServer.ReturnRandom("update_direct_admin@spogqa.com"), sharePassword, "betty", "liu", "direct_admin", directOrg_id, "","","","msp_admin",SpogConstants.SUCCESS_GET_PUT_DELETE}, 
					  {spogServer.ReturnRandom("update_msp_admin@spogqa.com"), sharePassword, "betty", "liu", "msp_admin", mspOrg_id, "","","","msp_account_admin",SpogConstants.SUCCESS_GET_PUT_DELETE}, 
					  {spogServer.ReturnRandom("update_msp_account_admin@spogqa.com"), sharePassword, "betty", "liu", "msp_account_admin", mspOrg_id, "","","","direct_admin",SpogConstants.SUCCESS_GET_PUT_DELETE},
					  {spogServer.ReturnRandom("update_msp_account_direct_admin@spogqa.com"), sharePassword, "betty", "liu", "direct_admin", account_id, "","","","msp_admin",SpogConstants.SUCCESS_GET_PUT_DELETE},
					  {spogServer.ReturnRandom("updateuser_rootmsp1@spogqa.com"), sharePassword, "betty", "liu", "msp_admin", root_msp1_id,"","","","direct_admin",SpogConstants.SUCCESS_GET_PUT_DELETE}, 
					  {spogServer.ReturnRandom("updateuse_submsp1_rootmsp1@spogqa.com"), sharePassword, "betty", "liu", "msp_admin", subMSP1_root1_id,"","","","direct_admin",SpogConstants.SUCCESS_GET_PUT_DELETE}, 
					  {spogServer.ReturnRandom("updateuse_account_rootmsp1@spogqa.com"), sharePassword, "betty", "liu", "direct_admin", account_rootMSP1_id,"","","","msp_admin",SpogConstants.SUCCESS_GET_PUT_DELETE}, 
					  {spogServer.ReturnRandom("updateuse_account_submsp1_rootmsp1@spogqa.com"), sharePassword, "betty", "liu", "direct_admin", account_subMSP1_root1_id,"","","","msp_admin",SpogConstants.SUCCESS_GET_PUT_DELETE}, 
			          }; 
		}
	  //role id will not be updated actually even status =200
	  @Test(dataProvider="userInfo5")
	  public void updateLogInUserRoleFail(String email, String password, String first_name, String last_name, String role_id, String organization_id,
			  String newPassword, String newFirstName, String newLastName, String newRole, int expectedStatusCode) {
		  System.out.println("updateLogInUserRoleFail");
		  test = rep.startTest("updateLogInUserRoleFail");
		  test.assignAuthor("Liu Yuefen");
		  
		  spogServer.userLogin(csrLogInUserName, csrLogInPassword, SpogConstants.SUCCESS_LOGIN);
		  String csr_token = spogServer.getJWTToken();
		
		  //create user
		  String user_id = spogServer.createUserAndCheck(email, password, first_name, last_name, role_id, organization_id, test);
		  
		  //login
		  spogServer.userLogin(email, sharePassword, SpogConstants.SUCCESS_LOGIN);
		  
		  //update user
		  Response response = spogServer.UpdateLoggedInUser("", newPassword, newFirstName, newLastName, newRole, organization_id);
		  spogServer.checkResponseStatus(response, expectedStatusCode);  
		  response.then().body("data.role_id", equalTo(role_id));
		  //delete user
		  spogServer.userLogin(csrLogInUserName, csrLogInPassword, SpogConstants.SUCCESS_LOGIN);
		  spogServer.CheckDeleteUserByIdStatus(user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	  }
	  
	  @DataProvider(name="userInfo4")
	  public final Object[][] getUserInfo4() {
		  return new Object[][] { 
					  {spogServer.ReturnRandom("update_csr_admin@spogqa.com"), sharePassword, "betty", "liu", "csr_admin", csrOrg_id, "CCddDDee_123","","","",SpogConstants.SUCCESS_GET_PUT_DELETE}, 
					  {spogServer.ReturnRandom("update_csr_read_only@spogqa.com"), sharePassword, "betty", "liu", "csr_read_only", csrOrg_id, "CCddDDee_123","","","",SpogConstants.SUCCESS_GET_PUT_DELETE}, 
					  {spogServer.ReturnRandom("update_direct_admin@spogqa.com"), sharePassword, "betty", "liu", "direct_admin", directOrg_id, "CCddDDee_123","","","",SpogConstants.SUCCESS_GET_PUT_DELETE}, 
					  {spogServer.ReturnRandom("update_msp_admin@spogqa.com"), sharePassword, "betty", "liu", "msp_admin", mspOrg_id, "","new_betty","","",SpogConstants.SUCCESS_GET_PUT_DELETE}, 
					  {spogServer.ReturnRandom("update_msp_account_admin@spogqa.com"), sharePassword, "betty", "liu", "msp_account_admin", mspOrg_id, "","","new_liu","",SpogConstants.SUCCESS_GET_PUT_DELETE},
					  {spogServer.ReturnRandom("update_msp_account_direct_admin@spogqa.com"), sharePassword, "betty", "liu", "direct_admin", account_id, "","","","",SpogConstants.SUCCESS_GET_PUT_DELETE},
					  {spogServer.ReturnRandom("updateuser_rootmsp1@spogqa.com"), sharePassword, "betty", "liu", "msp_admin", root_msp1_id,"CCddDDee_123","","","",SpogConstants.SUCCESS_GET_PUT_DELETE}, 
					  {spogServer.ReturnRandom("updateuse_submsp1_rootmsp1@spogqa.com"), sharePassword, "betty", "liu", "msp_admin", subMSP1_root1_id,"","betty_new","","",SpogConstants.SUCCESS_GET_PUT_DELETE}, 
					  {spogServer.ReturnRandom("updateuse_account_rootmsp1@spogqa.com"), sharePassword, "betty", "liu", "direct_admin", account_rootMSP1_id,"","","liu_new","",SpogConstants.SUCCESS_GET_PUT_DELETE}, 
					  {spogServer.ReturnRandom("updateuse_account_submsp1_rootmsp1@spogqa.com"), sharePassword, "betty", "liu", "direct_admin", account_subMSP1_root1_id,"","","","",SpogConstants.SUCCESS_GET_PUT_DELETE}, 
			          
		  }; 
		}
	  @Test(dataProvider="userInfo4")
	  public void updateLogInUser(String email, String password, String first_name, String last_name, String role_id, String organization_id,
			  String newPassword, String newFirstName, String newLastName, String newRole, int expectedStatusCode) {
		  System.out.println("updateLoginWithMSPAdmin");
		  test = rep.startTest("updateLoginWithMSPAdmin");
		  test.assignAuthor("Liu Yuefen");
		  
		  spogServer.userLogin(csrLogInUserName, csrLogInPassword, SpogConstants.SUCCESS_LOGIN);
		  String csr_token = spogServer.getJWTToken();
		
		  //create user
		  String user_id = spogServer.createUserAndCheck(email, password, first_name, last_name, role_id, organization_id, test);
		  
		  //login
		  spogServer.userLogin(email, sharePassword, SpogConstants.SUCCESS_LOGIN);
		  
		  //update user
		  Response response = spogServer.UpdateLoggedInUser("", newPassword, newFirstName, newLastName, newRole, organization_id);
		  spogServer.checkResponseStatus(response, expectedStatusCode);
		  
		  //delete user
		  spogServer.userLogin(csrLogInUserName, csrLogInPassword, SpogConstants.SUCCESS_LOGIN);
		  spogServer.CheckDeleteUserByIdStatus(user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  
	  }
	  
	  
	  @DataProvider(name = "userInfo3")
	  public final Object[][] getUserInfo3() {
		  return new Object[][] { 
					  {spogServer.ReturnRandom("update_msp@spogqa.com"), sharePassword, "betty", "liu", "msp_account_admin", mspOrg_id, "CCddDDee_123","","","",SpogConstants.SUCCESS_GET_PUT_DELETE}, 
					  {spogServer.ReturnRandom("update_msp@spogqa.com"), sharePassword, "betty", "liu", "msp_account_admin", mspOrg_id, "","new_betty","","",SpogConstants.SUCCESS_GET_PUT_DELETE}, 
					  {spogServer.ReturnRandom("update_msp@spogqa.com"), sharePassword, "betty", "liu", "msp_account_admin", mspOrg_id, "","","new_liu","",SpogConstants.SUCCESS_GET_PUT_DELETE},
					  {spogServer.ReturnRandom("update_msp@spogqa.com"), sharePassword, "betty", "liu", "msp_account_admin", mspOrg_id, "","","","direct",SpogConstants.SUCCESS_GET_PUT_DELETE},
					  {spogServer.ReturnRandom("update_msp@spogqa.com"), sharePassword, "betty", "liu", "msp_account_admin", root_msp1_id, "CCddDDee_123","","","",SpogConstants.SUCCESS_GET_PUT_DELETE}, 
					  {spogServer.ReturnRandom("update_msp@spogqa.com"), sharePassword, "betty", "liu", "msp_account_admin", root_msp1_id, "","new_betty","","",SpogConstants.SUCCESS_GET_PUT_DELETE}, 
					  {spogServer.ReturnRandom("update_msp@spogqa.com"), sharePassword, "betty", "liu", "msp_account_admin", root_msp1_id, "","","new_liu","",SpogConstants.SUCCESS_GET_PUT_DELETE},
					  {spogServer.ReturnRandom("update_msp@spogqa.com"), sharePassword, "betty", "liu", "msp_account_admin", root_msp1_id, "","","","direct",SpogConstants.SUCCESS_GET_PUT_DELETE},
					  {spogServer.ReturnRandom("update_msp@spogqa.com"), sharePassword, "betty", "liu", "msp_account_admin", subMSP1_root1_id, "CCddDDee_123","","","",SpogConstants.SUCCESS_GET_PUT_DELETE}, 
					  {spogServer.ReturnRandom("update_msp@spogqa.com"), sharePassword, "betty", "liu", "msp_account_admin", subMSP1_root1_id, "","new_betty","","",SpogConstants.SUCCESS_GET_PUT_DELETE}, 
					  {spogServer.ReturnRandom("update_msp@spogqa.com"), sharePassword, "betty", "liu", "msp_account_admin", subMSP1_root1_id, "","","new_liu","",SpogConstants.SUCCESS_GET_PUT_DELETE},
					  {spogServer.ReturnRandom("update_msp@spogqa.com"), sharePassword, "betty", "liu", "msp_account_admin", subMSP1_root1_id, "","","","direct",SpogConstants.SUCCESS_GET_PUT_DELETE},
		  }; 
		}
	  @Test(dataProvider = "userInfo3")
	  public void updateLoginWithMSPAdmin(String email, String password, String first_name, String last_name, String role_id, String organization_id,
			  String newPassword, String newFirstName, String newLastName, String newRole, int expectedStatusCode){	 
		  System.out.println("updateLoginWithMSPAdmin");
		  test = rep.startTest("updateLoginWithMSPAdmin");
		  test.assignAuthor("Liu Yuefen");
		  
		  spogServer.userLogin(csrLogInUserName, csrLogInPassword, SpogConstants.SUCCESS_LOGIN);
		  String csr_token = spogServer.getJWTToken();
		
		  //create user
		  String user_id = spogServer.createUserAndCheck(email, password, first_name, last_name, role_id, organization_id, test);
		  
		  //login
		  spogServer.userLogin(email, sharePassword, SpogConstants.SUCCESS_LOGIN);
		  
		  //update user
		  Response response = spogServer.UpdateLoggedInUser("", newPassword, newFirstName, newLastName, newRole, organization_id);
		  spogServer.checkResponseStatus(response, expectedStatusCode);
		  if (newPassword !="") {
			  spogServer.userLogin(email, newPassword, SpogConstants.SUCCESS_LOGIN);
		  }else if (newFirstName !="") {
			  response.then().body("data.first_name", equalTo(newFirstName));
		  }else if (newLastName !="") {
			  response.then().body("data.last_name", equalTo(newLastName));
		  }else {
			  response.then().body("data.role_id", equalTo("msp_account_admin"));
		  }
          
          //delete user
		  spogServer.setToken(csr_token);
          spogServer.CheckDeleteUserByIdStatus(user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	  }
	  
	  @DataProvider(name = "userInfo1")
	  public final Object[][] getUserInfo1() {
		  return new Object[][] { 
					  //{spogServer.ReturnRandom("update_csr_phone@spogqa.com"), sharePassword, "betty", "liu", "csr_admin", "", "+86-13812345678"}, 
			          { spogServer.ReturnRandom("update_direct_phone@spogqa.com"), sharePassword, "betty", "liu", "direct_admin", directOrg_id,"+911234567891" },
			          { spogServer.ReturnRandom("update_msp_phone@spogqa.com"), sharePassword, "betty", "liu", "msp_admin", mspOrg_id,"+86-01050890816"},
			          { spogServer.ReturnRandom("update_account_phone@spogqa.com"), sharePassword, "betty", "liu", "direct_admin", account_id,"+81-345201234"},
			          { spogServer.ReturnRandom("update_account_phone@spogqa.com"), sharePassword, "betty", "liu", "direct_admin", account_id,"+1(607) 240-1234"},
			          { spogServer.ReturnRandom("update_account_phone@spogqa.com"), sharePassword, "betty", "liu", "direct_admin", account_id,"+1 405 517 1234"}
			          }; 
		}
	  @Test(dataProvider = "userInfo1")
	  public void updateUserPhoneNumber(String email, String password, String first_name, String last_name, String role_id, String organization_id, String phone_number ){	 
		  System.out.println("updateUserPhoneNumber");
		  test = rep.startTest("updateUserPhoneNumber");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login with csr
		  spogServer.userLogin(csrLogInUserName, csrLogInPassword, SpogConstants.SUCCESS_LOGIN);
		  
		  //create user
		  String user_id = spogServer.createUserAndCheck(email, password, first_name, last_name, role_id, organization_id, test);
		  
		  //login with new created user
		  spogServer.userLogin(csrLogInUserName, csrLogInPassword, SpogConstants.SUCCESS_LOGIN);
		  
          //update user
		  Response response = spogServer.UpdateLoggedInUser("", "", "", "", "", "", phone_number, test);
          spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
          response.then().body("data.phone_number", equalTo(phone_number));  
          
          //delete user
          spogServer.CheckDeleteUserByIdStatus(user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	  }
	  
	  @DataProvider(name = "userInfo2")
	  public final Object[][] getUserInfo2() {
		  return new Object[][] { 
			  //{spogServer.ReturnRandom("update_direct_phone@spogqa.com"), sharePassword, "betty", "liu", "direct_admin", directOrg_id, "abc"}, 
			  {spogServer.ReturnRandom("update_direct_phone@spogqa.com"), sharePassword, "betty", "liu", "direct_admin", directOrg_id, "+86 405 517 1234"},
			  {spogServer.ReturnRandom("update_direct_phone@spogqa.com"), sharePassword, "betty", "liu", "direct_admin", directOrg_id, "+1 13811011012"},
			  {spogServer.ReturnRandom("update_direct_phone@spogqa.com"), sharePassword, "betty", "liu", "direct_admin", directOrg_id, RandomStringUtils.randomNumeric(8)}
	          };
		}
	  //modified by xiang for enroll organization, phone number has no any checking, it can accept anything or null.
	  @Test(dataProvider = "userInfo2")
	  public void updateUserPhoneNumberFail(String email, String password, String first_name, String last_name, String role_id, String organization_id, String phone_number ){	 
		  System.out.println("updateUserPhoneNumberFail");
		  test = rep.startTest("updateUserPhoneNumberFail");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login with csr
		  spogServer.userLogin(csrLogInUserName, csrLogInPassword, SpogConstants.SUCCESS_LOGIN);
		  
		  //create user
		  String user_id = spogServer.createUserAndCheck(email, password, first_name, last_name, role_id, organization_id, test);
		  
          //update user
		  Response response = spogServer.UpdateLoggedInUser("", "", "", "", "", "", phone_number, test);
//          spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST);
//          spogServer.checkErrorCode(response, "4000000A"); 
          spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
          //delete user
          spogServer.CheckDeleteUserByIdStatus(user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	  }
	  
	  @Test
	  public void updateUserPhoneNumberFail2(){	 
		  System.out.println("updateUserPhoneNumberFail2");
		  test = rep.startTest("updateUserPhoneNumberFail2");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login with csr
		  spogServer.userLogin(csrLogInUserName, csrLogInPassword, SpogConstants.SUCCESS_LOGIN);
		  
		  //create user
		  String user_id = spogServer.createUserAndCheck(spogServer.ReturnRandom("update_direct_phone@spogqa.com"), sharePassword, "betty", "liu", "direct_admin", directOrg_id, test);
		  
          //update user
		  Response response = spogServer.UpdateLoggedInUser("", "", "", "", "", "", "abc", test);
          spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST);
          spogServer.checkErrorCode(response, "40000003"); 
  
          //delete user
          spogServer.CheckDeleteUserByIdStatus(user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	  }
	  @Test
	  public void updateLoggedInUserTest() {
		  System.out.println("updateLoggedInUserTest");
		  test = rep.startTest("updateLoggedInUserTest");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login with csr
		  spogServer.userLogin(csrLogInUserName, csrLogInPassword, SpogConstants.SUCCESS_LOGIN);
		  
		  //create dire org
		  String orgID = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_direct_yuefen")+org_prefix, "direct", spogServer.ReturnRandom("direct_org_yuefen@spogqa.com"), spogServer.ReturnRandom("Aaa1"), spogServer.ReturnRandom("spogqa"),spogServer.ReturnRandom("direc_org"),test);  
		
		  System.out.println("orgid:"+orgID);
		  
		  //prepare data for create user
		  String email = spogServer.ReturnRandom("liu_login@spogqa.com");
		  String password = spogServer.ReturnRandom("spogQA11");
		  String firstName = spogServer.ReturnRandom("spogqa");
		  String lastName = spogServer.ReturnRandom("liu");
		  String roleId = "direct_admin";

		  String user_id=spogServer.createUserAndCheck(email, password, firstName, lastName, roleId, orgID, test);
		  
		  //login with new created direct admin
		  spogServer.userLogin(email, password, SpogConstants.SUCCESS_LOGIN);
		  
		  //update password
		  String newPassword = spogServer.ReturnRandom("spogQA22");
		  Response response = spogServer.UpdateLoggedInUser("", newPassword, "", "", "", "");
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  spogServer.userLogin(email, newPassword, SpogConstants.SUCCESS_LOGIN);
		 
		  
		  //update firstname
		  String newFirstName = spogServer.ReturnRandom("spogqafirst");
		  response = spogServer.UpdateLoggedInUser("", "", newFirstName, "", "", "");
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  response.then().body("data.first_name", equalTo(newFirstName));
		 
		  
		  //update lastname
		  String newLastName = spogServer.ReturnRandom("spogqalast");
		  response = spogServer.UpdateLoggedInUser("", "", "", newLastName, "", "");
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  response.then().body("data.last_name", equalTo(newLastName));
		  
		  //update password, fistname, lastname
		  String newPassword2 = spogServer.ReturnRandom("spogqaYYY111");
		  String newFirstName2 = spogServer.ReturnRandom("spogqa_uuu");
		  String newLastName2 = spogServer.ReturnRandom("spogqaliuyy");
		  response = spogServer.UpdateLoggedInUser("", newPassword2, newFirstName2, newLastName2, "", "");
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  response.then().body("data.first_name", equalTo(newFirstName2));
		  response.then().body("data.last_name", equalTo(newLastName2));
		  spogServer.userLogin(email, newPassword2, SpogConstants.SUCCESS_LOGIN);
		  
		  //update one field that not allow to change
		  String newEmail = spogServer.ReturnRandom("yuefen_liu@spogqa.com");
		  String newRoleId = spogServer.ReturnRandom("msp_admin");
		  response = spogServer.UpdateLoggedInUser(newEmail, "", "", "", newRoleId, "");
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  response.then().body("data.email", equalTo(email.toLowerCase()));
		  response.then().body("data.role_id",equalTo(roleId));
		  
		  //delete user
		  spogServer.userLogin(csrLogInUserName, csrLogInPassword, SpogConstants.SUCCESS_LOGIN);
		  spogServer.CheckDeleteUserByIdStatus(user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  
	  }
	  
	  @Test 
	  public void updatePasswordTest() {
		  System.out.println("updatePasswordTest");
		  test = rep.startTest("updatePasswordTest");
		  test.assignAuthor("Liu Yuefen");
		  //login
		  spogServer.userLogin(csrLogInUserName,csrLogInPassword, SpogConstants.SUCCESS_LOGIN);
		  
          //prepare data to create user
		  String email = spogServer.ReturnRandom("betty_password@spogqa.com");
		  String password = spogServer.ReturnRandom("bettyQQ33");
		  String user_id=spogServer.createUserAndCheck(email, password, spogServer.ReturnRandom("betty"), spogServer.ReturnRandom("liu"), "direct_admin", directOrg_id, test);
		  
		  //login
		  spogServer.userLogin(email, password, SpogConstants.SUCCESS_LOGIN, test);
		  
		  //update password length <8
		  Response response = spogServer.UpdateLoggedInUser("", "abCd123", "", "", "", "");
		  spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST);
		  spogServer.checkErrorCode(response, "40000002");
		  
		  //update password length >20
		  response = spogServer.UpdateLoggedInUser("", "abcdefABCDEF123456789", "", "", "", "");
		  spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST);
		  spogServer.checkErrorCode(response, "40000002");
		  
		  //update password not contain upper case
		  response = spogServer.UpdateLoggedInUser("", "abcabc123", "", "", "", "");
		  spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST);
		  spogServer.checkErrorCode(response, "40000003");
		  //update password not contain lower case
		  response = spogServer.UpdateLoggedInUser("", "ABCABC123", "", "", "", "");
		  spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST);
		  spogServer.checkErrorCode(response, "40000003");
		  
		  //update password not contain number
		  response = spogServer.UpdateLoggedInUser("", "abcabcABC", "", "", "", "");
		  spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST);
		  spogServer.checkErrorCode(response, "40000003");
		  
		  //update password =""
		  response = spogServer.UpdateLoggedInUser("", "", "", "", "", "");
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		  
		  //update password = null
		  response = spogServer.UpdateLoggedInUser("", null, "", "", "", "");
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		  
		  //update password contain upper/lower case/number/special case
		  String newPassword = spogServer.ReturnRandom("abc@ABC123");
		  response = spogServer.UpdateLoggedInUser("", newPassword, "", "", "", "");
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		  spogServer.userLogin(email, newPassword, SpogConstants.SUCCESS_LOGIN, test); 
		  
		  //delete user
		  spogServer.userLogin(csrLogInUserName, csrLogInPassword, SpogConstants.SUCCESS_LOGIN);
		  spogServer.CheckDeleteUserByIdStatus(user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	  }
	  
	  @Test
	  public void updateFail() {
		  System.out.println("updateFail");
		  test = rep.startTest("updateFail");
		  test.assignAuthor("Liu Yuefen");
		  //login
		  spogServer.userLogin(csrLogInUserName,csrLogInPassword, SpogConstants.SUCCESS_LOGIN);
		  String csrToken = spogServer.getJWTToken();
          //prepare data to create user
		  String email = spogServer.ReturnRandom("betty@spogqa.com");
		  String password = spogServer.ReturnRandom("bettyQQ33");
		  String firstName = spogServer.ReturnRandom("betty");
		  String lastName = spogServer.ReturnRandom("liu");
		  String roleId = "csr_admin";
		  String user_id = spogServer.createUserAndCheck(email, password, firstName, lastName, roleId, "", test);
		  spogServer.userLogin(email, password, test);
		  String token = spogServer.getJWTToken();
		  //update user
		  Response response = spogServer.UpdateLoggedInUserWithBodyNull();
		  spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST);
		  spogServer.checkErrorCode(response, "00100002");
		  
		  //delete user
		  spogServer.setToken(csrToken);
		  spogServer.CheckDeleteUserByIdStatus(user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  //update user
		  spogServer.setToken(token);
		  response = spogServer.UpdateLoggedInUser("", "", "yuefen_xx", "", "", "");
		  spogServer.checkResponseStatus(response, SpogConstants.NOT_LOGGED_IN, test);
		  spogServer.checkErrorCode(response, "00200001");
	  }  
	  
	  @Test 
	  public void updateUnloggedInUserTest() {
		  test = rep.startTest("updateUnloggedInUserTest");
		  test.assignAuthor("Liu Yuefen");
		  //login
		  spogServer.userLogin(csrLogInUserName,csrLogInPassword, SpogConstants.SUCCESS_LOGIN);
		  
          //prepare data to create user
		  String email = spogServer.ReturnRandom("betty@spogqa.com");
		  String password = spogServer.ReturnRandom("bettyQQ33");
		  String firstName = spogServer.ReturnRandom("betty");
		  String lastName = spogServer.ReturnRandom("liu");
		  String roleId = "csr_admin";
		  spogServer.createUserAndCheck(email, password, firstName, lastName, roleId, "", test);

		  spogServer.setToken("");
		  
		  //update user
		  Response response = spogServer.UpdateLoggedInUser("", "", spogServer.ReturnRandom("bettyliu"), "", "", "");
		  spogServer.checkResponseStatus(response, SpogConstants.NOT_LOGGED_IN);
		  spogServer.checkErrorCode(response, "00900006");
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
