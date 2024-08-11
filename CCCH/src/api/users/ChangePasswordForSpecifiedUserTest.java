package api.users;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;

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

public class ChangePasswordForSpecifiedUserTest extends base.prepare.PrepareOrgInfo {
	 @Parameters({ "pmfKey"})
	  public ChangePasswordForSpecifiedUserTest(String pmfKey) {
			super(pmfKey);
			// TODO Auto-generated constructor stub
		}
	  private SPOGServer spogServer;
	  private Org4SPOGServer org4SpogServer;
	  private UserSpogServer userSpogServer;
//	  private ExtentReports rep;
	  private ExtentTest test;
      private String csrLoginUSerName;
      private String csrLoginPassword;
      private String csrOrg_id;
      private String directOrg_id;
      private String directOrg2_id;
      private String mspOrg_id;
      private String mspOrg2_id;
      private String account_id;
      private String account2_id;
      private String sharePassword ="Caworld_2018";
      private String  org_prefix=this.getClass().getSimpleName();
	  //this is for update portal, each testng class is taken as BQ set
//	  private SQLServerDb bqdb1;
//	  public int Nooftest;
//	  private long creationTime;
//	  private String BQName=null;
//	  private String runningMachine;
//	  private testcasescount count1;
//	  private String buildVersion;
	  //end

	  @BeforeClass
	  @Parameters({ "baseURI", "port" , "csrAdminUserName", "csrAdminPassword","logFolder","runningMachine", "buildVersion"})
	  public void beforeClass(String baseURI, String port, String userName, String password, String logFolder, String runningMachine, String buildVersion  ) {
		  rep = ExtentManager.getInstance("ChangePasswordForSpecifiedUserTest",logFolder);
		  spogServer = new SPOGServer(baseURI, port);
		  org4SpogServer =new Org4SPOGServer(baseURI, port);
		  userSpogServer =new UserSpogServer(baseURI, port);
		  spogServer.userLogin(userName, password);  
		  this.csrLoginUSerName = userName;
		  this.csrLoginPassword = password;
		  //prepare org
		  csrOrg_id = spogServer.GetLoggedinUserOrganizationID();
		  directOrg_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_yuefen_direct_changepassword")+org_prefix,"direct",spogServer.ReturnRandom("spogqa_direct_changepassowrd@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("direct_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  directOrg2_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_yuefen_direct2_changepassword")+org_prefix,"direct",spogServer.ReturnRandom("spogqa_direct2_changepassowrd@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("direct_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  mspOrg_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_yuefen_msp1_chagnepassword")+org_prefix,"msp",spogServer.ReturnRandom("spogqa_msp1_changepassword@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("msp_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  mspOrg2_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_yuefen_msp2_changepassword")+org_prefix,"msp",spogServer.ReturnRandom("spogqa_msp2_changepassword@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("msp_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  account_id = spogServer.createAccountWithCheck(mspOrg_id, spogServer.ReturnRandom("account1_yuefen_spogqa")+org_prefix, mspOrg_id, test);
		  account2_id = spogServer.createAccountWithCheck(mspOrg2_id, spogServer.ReturnRandom("account2_yuefen_spogqa")+org_prefix, mspOrg2_id, test);
		  
		  prepare(baseURI, port, logFolder, this.csrLoginUSerName,  this.csrLoginPassword, this.getClass().getSimpleName() );
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
          setEnv(baseURI,  port,  userName, password);
          //end
	     
	  }
	  
	  @DataProvider(name = "userInfo")
	  public final Object[][] getUserInfo() {
		  return new Object[][] { 
			  //user permission
					  {spogServer.ReturnRandom("changepassword_direct@spogqa.com"), sharePassword, "betty", "liu", "direct_admin", directOrg_id,"new_Password2018"}, 
					  {spogServer.ReturnRandom("changepassword_account@spogqa.com"), sharePassword, "betty", "liu", "direct_admin", account_id,"new_Password2018"}, 
					  {spogServer.ReturnRandom("changepassword_msp@spogqa.com"), sharePassword, "betty", "liu", "msp_admin", mspOrg_id,"new_Password2018"}, 
					  {spogServer.ReturnRandom("changepassword_mspaccountAdmin@spogqa.com"), sharePassword, "betty", "liu", "msp_account_admin", mspOrg_id,"new_Password2018"},
					  {spogServer.ReturnRandom("changepassword_csr_read_only@spogqa.com"), sharePassword, "betty", "liu", "csr_read_only", csrOrg_id,"new_Password2018"},
					  {spogServer.ReturnRandom("changepassword_rootmsp1@spogqa.com"), sharePassword, "betty", "liu", "msp_admin", this.root_msp_org_id,"new_Password2018"}, 
					  {spogServer.ReturnRandom("changepassword_rootmsp1@spogqa.com"), sharePassword, "betty", "liu", "msp_account_admin",this.root_msp_org_id,"new_Password2018"},
					  {spogServer.ReturnRandom("changepassword_submsp1_rootmsp1@spogqa.com"), sharePassword, "betty", "liu", "msp_admin", this.sub_msp1_org_id,"new_Password2018"}, 
					  {spogServer.ReturnRandom("changepassword_submsp1_rootmsp1@spogqa.com"), sharePassword, "betty", "liu", "msp_account_admin", this.sub_msp1_org_id,"new_Password2018"},
					  {spogServer.ReturnRandom("changepassword_account_rootmsp1@spogqa.com"), sharePassword, "betty", "liu", "direct_admin", this.root_msp_direct_org_id,"new_Password2018"}, 
					  {spogServer.ReturnRandom("changepassword_account_submsp1_rootmsp1@spogqa.com"), sharePassword, "betty", "liu", "direct_admin", this.sub_msp1_account1_id,"new_Password2018"}, 
		  }; 
		}
	  @Test (dataProvider="userInfo")
	  public void changePasswordForEachRole(String userName, String password, String firstName, String lastName,
			  String role, String org, String newPassword){	 
		  System.out.println("changePasswordForEachRole");
		  test = rep.startTest("changePasswordForEachRole");
		  test.assignAuthor("Liu Yuefen");
		  
		  spogServer.userLogin(csrLoginUSerName, csrLoginPassword, SpogConstants.SUCCESS_LOGIN);
		  String csr_token =spogServer.getJWTToken();
		  //create user
		  String email =userName;
		  String user_id = spogServer.createUserAndCheck(email, password, firstName, lastName, role, org, test);
		  //login
		  spogServer.userLogin(email, password, SpogConstants.SUCCESS_LOGIN);
          //change password
		  spogServer.changePasswordForSpecifiedUserWithExpectedStatusCode(user_id, password, newPassword, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  spogServer.userLogin(email, newPassword, SpogConstants.SUCCESS_LOGIN);
          //delete user
		  spogServer.setToken(csr_token);
          spogServer.CheckDeleteUserByIdStatus(user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	  }
	  
	  @DataProvider(name = "userInfo60")
	  public final Object[][] getUserInfo60() {
		  return new Object[][] { 
			  //user permission
					  {this.final_root_msp_user_name_email, this.common_password,spogServer.ReturnRandom("changepassword_rootmsp1@spogqa.com"), sharePassword, "betty", "liu", "msp_admin",this.sub_msp1_org_id,"new_Password2018"}, 
					  {this.final_root_msp_user_name_email, this.common_password,spogServer.ReturnRandom("changepassword_rootmsp1@spogqa.com"), sharePassword, "betty", "liu", "direct_admin",this.sub_msp1_account1_id,"new_Password2018"}, 
					  {this.final_root_msp_account_admin_user_name_email, this.common_password,spogServer.ReturnRandom("changepassword_rootmsp1@spogqa.com"), sharePassword, "betty", "liu", "msp_admin",this.sub_msp1_org_id,"new_Password2018"}, 
					  {this.final_root_msp_account_admin_user_name_email, this.common_password,spogServer.ReturnRandom("changepassword_rootmsp1@spogqa.com"), sharePassword, "betty", "liu", "direct_admin",this.sub_msp1_account1_id,"new_Password2018"}, 
					  {this.final_root_msp_user_name_email, this.common_password,spogServer.ReturnRandom("changepassword_rootmsp1@spogqa.com"), sharePassword, "betty", "liu", "msp_admin",this.mspOrg_id,"new_Password2018"}, 
					  {this.final_root_msp_user_name_email, this.common_password,spogServer.ReturnRandom("changepassword_rootmsp1@spogqa.com"), sharePassword, "betty", "liu", "direct_admin",this.directOrg_id,"new_Password2018"}, 
					  
					  {this.final_sub_msp1_user_name_email, this.common_password,spogServer.ReturnRandom("changepassword_rootmsp1@spogqa.com"), sharePassword, "betty", "liu", "msp_admin",this.sub_msp2_org_id,"new_Password2018"}, 
					  {this.final_sub_msp1_user_name_email, this.common_password,spogServer.ReturnRandom("changepassword_rootmsp1@spogqa.com"), sharePassword, "betty", "liu", "direct_admin",this.sub_msp2_account1_id,"new_Password2018"}, 
					  {this.final_sub_msp1_user_name_email, this.common_password,spogServer.ReturnRandom("changepassword_rootmsp1@spogqa.com"), sharePassword, "betty", "liu", "direct_admin",this.root_msp_direct_org_id,"new_Password2018"},
					  {this.final_sub_msp1_msp_account_user_name_email, this.common_password,spogServer.ReturnRandom("changepassword_rootmsp1@spogqa.com"), sharePassword, "betty", "liu", "msp_admin",this.sub_msp2_org_id,"new_Password2018"}, 
					  {this.final_sub_msp1_msp_account_user_name_email, this.common_password,spogServer.ReturnRandom("changepassword_rootmsp1@spogqa.com"), sharePassword, "betty", "liu", "direct_admin",this.sub_msp2_account1_id,"new_Password2018"}, 
					  {this.final_sub_msp1_msp_account_user_name_email, this.common_password,spogServer.ReturnRandom("changepassword_rootmsp1@spogqa.com"), sharePassword, "betty", "liu", "direct_admin",this.root_msp_direct_org_id,"new_Password2018"},
					  
					  {this.final_root_msp_direct_org_user_email, this.common_password,spogServer.ReturnRandom("changepassword_rootmsp1@spogqa.com"), sharePassword, "betty", "liu", "msp_admin",this.sub_msp2_org_id,"new_Password2018"}, 
					  {this.final_root_msp_direct_org_user_email, this.common_password,spogServer.ReturnRandom("changepassword_rootmsp1@spogqa.com"), sharePassword, "betty", "liu", "direct_admin",this.sub_msp2_account1_id,"new_Password2018"}, 
					  
					  
					  {this.final_sub_msp1_account1_user_email, this.common_password,spogServer.ReturnRandom("changepassword_rootmsp1@spogqa.com"), sharePassword, "betty", "liu", "direct_admin",this.root_msp_direct_org_id,"new_Password2018"},
					  {this.final_sub_msp1_account1_user_email, this.common_password,spogServer.ReturnRandom("changepassword_rootmsp1@spogqa.com"), sharePassword, "betty", "liu", "direct_admin",this.sub_msp2_account1_id,"new_Password2018"},
					  {this.final_sub_msp1_account1_user_email, this.common_password,spogServer.ReturnRandom("changepassword_rootmsp1@spogqa.com"), sharePassword, "betty", "liu", "msp_admin",this.sub_msp2_org_id,"new_Password2018"},
					  
		  }; 
		}
	  @Test (dataProvider="userInfo60")
	  public void rootMspRelatedChangePasswordFail(String loginUser, String loginPassword,String userName, String password, String firstName, String lastName,
			  String role, String org, String newPassword){	 
		  System.out.println("rootMspRelatedChangePasswordFail");
		  test = rep.startTest("rootMspRelatedChangePasswordFail");
		  test.assignAuthor("Liu Yuefen");
		  
		  spogServer.userLogin(csrLoginUSerName, csrLoginPassword, SpogConstants.SUCCESS_LOGIN);
		  String csr_token =spogServer.getJWTToken();
		  //create user
		  String user_id = spogServer.createUserAndCheck(userName, password, firstName, lastName, role, org, test);
		  //login
		  spogServer.userLogin(loginUser, loginPassword, SpogConstants.SUCCESS_LOGIN);
          //change password
		  Response response =spogServer.changePasswordForSpecifiedUser(user_id, password, newPassword);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS);
		  spogServer.checkErrorMessage(response, "00100101");
		
          //delete user
		  spogServer.setToken(csr_token);
          spogServer.CheckDeleteUserByIdStatus(user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	  }
	  
	  @Test
	  public void csrChagePassword() {
		  test = rep.startTest("csrChagePassword");
		  test.assignAuthor("Liu Yuefen");
		  //login with csr
		  spogServer.userLogin(csrLoginUSerName, csrLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  //create csr admin1
		  String csrAdmin1 = spogServer.ReturnRandom("csr1_specified_yuefen@spogqa.com");
		  String csrPassword1 = spogServer.ReturnRandom("vvVVccDD12");
		  String csr_user1_id = spogServer.createUserAndCheck(csrAdmin1, csrPassword1, spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "csr_admin", "", test);
          //create csr admin2
		  String csrAdmin2 = spogServer.ReturnRandom("csr2_specified_yuefen@spogqa.com");
		  String csrPassword2 = spogServer.ReturnRandom("abAB11");
		  String csr_user2_id = spogServer.createUserAndCheck(csrAdmin2, csrPassword2, spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "csr_admin", "", test);
		  //login with csr admin1
		  spogServer.userLogin(csrAdmin1, csrPassword1, SpogConstants.SUCCESS_LOGIN, test);
		  //change password for admin2
		  String csrNewPassword2 = spogServer.ReturnRandom("vBvCvD12");
		  Response response = spogServer.changePasswordForSpecifiedUser(csr_user2_id, csrPassword2, csrNewPassword2);
		  spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
		  spogServer.checkErrorCode(response, "00200014");
		  
		  //change password for itself - csr admin1
		  String csrNewPassword1 = spogServer.ReturnRandom("newPass@1");
		  spogServer.changePasswordForSpecifiedUserWithExpectedStatusCode(csr_user1_id, csrPassword1, csrNewPassword1, SpogConstants.SUCCESS_LOGIN, test);
		  spogServer.userLogin(csrAdmin1, csrNewPassword1, SpogConstants.SUCCESS_LOGIN, test);
		  
		  //create msp admin
		  spogServer.userLogin(csrAdmin1, csrNewPassword1, SpogConstants.SUCCESS_LOGIN, test);
		  
		  String mspAdmin = spogServer.ReturnRandom("msp1_specified_yuefen@spogqa.com");
		  String mspPassword = spogServer.ReturnRandom("mnMNcc12");
		  String msp_user_id = spogServer.createUserAndCheck(mspAdmin, mspPassword, spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "msp_admin", mspOrg_id, test);
		  //change msp admin password
		  String mspNewPassword = spogServer.ReturnRandom("NewP@ss12");
		  response = spogServer.changePasswordForSpecifiedUser(msp_user_id, mspPassword, mspNewPassword);
		  spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST , test);
		  spogServer.checkErrorCode(response, "00200014");
		  
		  //create direct admin
		  spogServer.userLogin(csrAdmin1, csrNewPassword1, SpogConstants.SUCCESS_LOGIN, test);
		  
		  String directAdmin = spogServer.ReturnRandom("direct_specified_yuefen@spogqa.com");
		  String directPassword = spogServer.ReturnRandom("mnMNcc12");
		  String direct_user_id = spogServer.createUserAndCheck(directAdmin, directPassword, spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "direct_admin", directOrg_id, test);
		  //change direct admin password
		  String directNewPassword = spogServer.ReturnRandom("NewP@ss12");
		  response = spogServer.changePasswordForSpecifiedUser(direct_user_id, directPassword, directNewPassword);
		  spogServer.checkResponseStatus(response,SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
		  spogServer.checkErrorCode(response, "00200014");
		  
		  //create account
		  spogServer.userLogin(csrAdmin1, csrNewPassword1, SpogConstants.SUCCESS_LOGIN, test);
		  
		  String accountDirectAdmin = spogServer.ReturnRandom("acount1_specified_yuefen@spogqa.com");
		  String accountDirectPassword = spogServer.ReturnRandom("mnMNcc12");
		  String account_direct_user_id = spogServer.createUserAndCheck(accountDirectAdmin, accountDirectPassword, spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "direct_admin", account_id, test);
		  //change password for account
		  String accountDirectNewPassword = spogServer.ReturnRandom("NewP@ss12");
		  response = spogServer.changePasswordForSpecifiedUser(account_direct_user_id, accountDirectPassword, accountDirectNewPassword);
		  spogServer.checkResponseStatus(response,SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
		  spogServer.checkErrorCode(response, "00200014");
	  }
	  
	  
	  @Test
	  public void mspChagePassword() {
		  test = rep.startTest("mspChagePassword");
		  test.assignAuthor("Liu Yuefen");
		  
		  spogServer.userLogin(csrLoginUSerName, csrLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  //create msp admin1
		  System.out.println("msp admin1 change password for itself");
		  String mspAdmin1 = spogServer.ReturnRandom("msp1_admin1_specified_yuefen@spogqa.com");
		  String mspPassword1 = spogServer.ReturnRandom("vvVVccDD12");
		  String msp_user1_id = spogServer.createUserAndCheck(mspAdmin1, mspPassword1, spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "msp_admin", mspOrg_id, test);
		  //change password for itself
		  spogServer.userLogin(mspAdmin1, mspPassword1, SpogConstants.SUCCESS_LOGIN, test);
		  String mspNewPassword1 = spogServer.ReturnRandom("NewP@ss12");
		  spogServer.changePasswordForSpecifiedUserWithExpectedStatusCode(msp_user1_id, mspPassword1, mspNewPassword1, SpogConstants.SUCCESS_LOGIN, test);
		  //login with new password
		  spogServer.userLogin(mspAdmin1, mspNewPassword1, SpogConstants.SUCCESS_LOGIN, test);
		  
		  //create msp admin2 in the same org
		  System.out.println("msp admin1 change password for admin2 in the same org");
		  spogServer.userLogin(csrLoginUSerName, csrLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  String mspAdmin2 = spogServer.ReturnRandom("msp1_admin2_specified_yuefen@spogqa.com");
		  String mspPassword2 = spogServer.ReturnRandom("vvVVccDD12");
		  String msp_user2_id = spogServer.createUserAndCheck(mspAdmin2, mspPassword2, spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "msp_admin", mspOrg_id, test);
		  //login with msp admin1
		  spogServer.userLogin(mspAdmin1, mspNewPassword1, SpogConstants.SUCCESS_LOGIN, test);
		  //change password for msp admin2 under the same org
		  String mspNewPassword2 = spogServer.ReturnRandom("NewP@ss12");
		  Response response = spogServer.changePasswordForSpecifiedUser(msp_user2_id, mspPassword2, mspNewPassword2);
		  spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
		  spogServer.checkErrorCode(response, "00200014");
		  
		  //create account direct admin under msp1
		  System.out.println("msp admin1 change password for its suborg user");
		  spogServer.userLogin(csrLoginUSerName, csrLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  String accountDirectAdmin1 = spogServer.ReturnRandom("acount1_msp1_specified_yuefen@spogqa.com");
		  String accountDirectPassword1 = spogServer.ReturnRandom("mnMNcc12");
		  String account1_direct_user_id = spogServer.createUserAndCheck(accountDirectAdmin1, accountDirectPassword1, spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "direct_admin", account_id, test);
		  //change  password for account user under the same org
		  spogServer.userLogin(mspAdmin1, mspNewPassword1, SpogConstants.SUCCESS_LOGIN, test);
		  String accountDirectNewPassword1 = spogServer.ReturnRandom("NewP@ss12");
		  response = spogServer.changePasswordForSpecifiedUser(account1_direct_user_id, accountDirectPassword1, accountDirectNewPassword1);
		  spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
		  spogServer.checkErrorCode(response, "00200014");
		 
		
		  //Create msp admin under msp2 org
		  System.out.println("msp admin1 change password for other org's msp admin");
		  spogServer.userLogin(csrLoginUSerName, csrLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  String msp2Admin = spogServer.ReturnRandom("msp2_admin1_specified_yuefen@spogqa.com");
		  String msp2Password = spogServer.ReturnRandom("vvVVccDD12");
		  String msp2_user_id = spogServer.createUserAndCheck(msp2Admin, msp2Password, spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "msp_admin", mspOrg2_id, test);
		  //login with msp admin1 new password
		  spogServer.userLogin(mspAdmin1, mspNewPassword1, SpogConstants.SUCCESS_LOGIN, test);
		  //change password for msp2 org's  admin 
		  String msp2NewPassword = spogServer.ReturnRandom("NewP@ss12");
		  response = spogServer.changePasswordForSpecifiedUser(msp2_user_id, msp2Password, msp2NewPassword);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
		  spogServer.checkErrorCode(response, "00100101");
		  
		  
		  //create account user under msp2 org
		  System.out.println("msp admin1 change password for other msp org-sub org user");
		  spogServer.userLogin(csrLoginUSerName, csrLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  String msp2AccountAdmin = spogServer.ReturnRandom("msp2_account1_specified_yuefen@spogqa.com");
		  String msp2AccountPassword = spogServer.ReturnRandom("vvVVccDD12");
		  String msp2_account_user_id = spogServer.createUserAndCheck(msp2AccountAdmin, msp2AccountPassword, spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "direct_admin", account2_id, test);
		  //login with msp admin1
		  spogServer.userLogin(mspAdmin1, mspNewPassword1, SpogConstants.SUCCESS_LOGIN, test);
		  //change the password for account user under msp2 org's
		  String msp2AccountNewPassword = spogServer.ReturnRandom("NewP@ss12");
		  response = spogServer.changePasswordForSpecifiedUser(msp2_account_user_id, msp2AccountPassword, msp2AccountNewPassword);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
		  spogServer.checkErrorCode(response, "00100101");

		  //create csr
		  System.out.println("msp admin1 change password for csr user");
		  spogServer.userLogin(csrLoginUSerName, csrLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  String csrAdmin = spogServer.ReturnRandom("csr3_specified_yuefen@spogqa.com");
		  String csrPassword = spogServer.ReturnRandom("vvVVccDD12");
		  String csr_user_id = spogServer.createUserAndCheck(csrAdmin, csrPassword, spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "csr_admin", "", test);
		  //login with msp admin1
		  spogServer.userLogin(mspAdmin1, mspNewPassword1, SpogConstants.SUCCESS_LOGIN, test);
		  //change password for csr  admin 
		  String csrNewPassword = spogServer.ReturnRandom("NewP@ss12");
		  response = spogServer.changePasswordForSpecifiedUser(csr_user_id, csrPassword, csrNewPassword);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
		  spogServer.checkErrorCode(response, "00100101");
		  
		  
		  //create direct admin
		  System.out.println("msp admin1 change password for direct org user");
		  spogServer.userLogin(csrLoginUSerName, csrLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  String directAdmin = spogServer.ReturnRandom("direct2_specified_yuefen@spogqa.com");
		  String directPassword = spogServer.ReturnRandom("vvVVccDD12");
		  String direct_user_id = spogServer.createUserAndCheck(directAdmin, directPassword, spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "direct_admin", directOrg_id, test);
		  //login with msp admin1
		  spogServer.userLogin(mspAdmin1, mspNewPassword1, SpogConstants.SUCCESS_LOGIN, test);
		  //update direct  admin 
		  String directNewPassword = spogServer.ReturnRandom("NewP@ss12");
		  response = spogServer.changePasswordForSpecifiedUser(direct_user_id, directPassword, directNewPassword);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
		  spogServer.checkErrorCode(response, "00100101");
		  
	  }
	  
	  @Test
	  public void directChangePassword() {
		  test = rep.startTest("mspChagePassword");
		  test.assignAuthor("Liu Yuefen");
		  
		  spogServer.userLogin(csrLoginUSerName, csrLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  //create direct admin1
		  String directAdmin1 = spogServer.ReturnRandom("direct1_user1_specified_yuefen@spogqa.com");
		  String directPassword1 = spogServer.ReturnRandom("vvVVccDD12");
		  String direct_user1_id = spogServer.createUserAndCheck(directAdmin1, directPassword1, spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "direct_admin", directOrg_id, test);
		  //change password for itself
		  spogServer.userLogin(directAdmin1, directPassword1, SpogConstants.SUCCESS_LOGIN, test);
		  String directNewPassword1 = spogServer.ReturnRandom("NewP@ss12");
		  spogServer.changePasswordForSpecifiedUserWithExpectedStatusCode(direct_user1_id, directPassword1, directNewPassword1, SpogConstants.SUCCESS_LOGIN, test);
		  //login with new password
		  spogServer.userLogin(directAdmin1, directNewPassword1, SpogConstants.SUCCESS_LOGIN, test);
		  
		  
		  //create another direct admin2 in the same org
		  spogServer.userLogin(csrLoginUSerName, csrLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  String directAdmin2 = spogServer.ReturnRandom("direct1_user2_specified_yuefen@spogqa.com");
		  String directPassword2 = spogServer.ReturnRandom("vvVVccDD12");
		  String direct_user2_id = spogServer.createUserAndCheck(directAdmin2, directPassword2, spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "direct_admin", directOrg_id, test);
		  //change password for another direct admin2 in the same org
		  spogServer.userLogin(directAdmin1, directNewPassword1, SpogConstants.SUCCESS_LOGIN, test);
		  String directNewPassword2 = spogServer.ReturnRandom("NewP@ss12");
		  Response response = spogServer.changePasswordForSpecifiedUser(direct_user2_id, directPassword2, directNewPassword2);
		  spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
		  spogServer.checkErrorCode(response, "00200014");
		  
		  
		  //create another direct admin in directorg2
		  spogServer.userLogin(csrLoginUSerName, csrLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  String direct2Admin = spogServer.ReturnRandom("direct2_user_specified_yuefen@spogqa.com");
		  String direct2Password = spogServer.ReturnRandom("vvVVccDD12");
		  String direct2_user_id = spogServer.createUserAndCheck(direct2Admin, direct2Password, spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "direct_admin", directOrg2_id, test);
		  //change password for another direct admin in directorg2
		  spogServer.userLogin(directAdmin1, directNewPassword1, SpogConstants.SUCCESS_LOGIN, test);
		  String direct2NewPassword = spogServer.ReturnRandom("NewP@ss12");
		  response = spogServer.changePasswordForSpecifiedUser(direct2_user_id, direct2Password, direct2NewPassword);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
		  spogServer.checkErrorCode(response, "00100101");
		 
          
		  //create csr admin
		  spogServer.userLogin(csrLoginUSerName, csrLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		 
		  String csrAdmin = spogServer.ReturnRandom("admin_csr44_specified_yuefen@spogqa.com");
		  String csrPassword = spogServer.ReturnRandom("vvVVccDD12");
		  String csr_user_id = spogServer.createUserAndCheck(csrAdmin, csrPassword, spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "csr_admin", "", test);
		  //change password for csr admin
		  spogServer.userLogin(directAdmin1, directNewPassword1, SpogConstants.SUCCESS_LOGIN, test);
		  String csrNewPassword = spogServer.ReturnRandom("NewP@ss12");
		  response = spogServer.changePasswordForSpecifiedUser(direct2_user_id, direct2Password, direct2NewPassword);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
		  spogServer.checkErrorCode(response, "00100101");
		  
		  
		  //create msp admin
		  spogServer.userLogin(csrLoginUSerName, csrLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  String mspAdmin = spogServer.ReturnRandom("msp5_user_specified_yuefen@spogqa.com");
		  String mspPassword = spogServer.ReturnRandom("vvVVccDD12");
		  String msp_user_id = spogServer.createUserAndCheck(mspAdmin, mspPassword, spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "msp_admin", mspOrg_id, test);
		  //change password for  msp admin
		  spogServer.userLogin(directAdmin1, directNewPassword1, SpogConstants.SUCCESS_LOGIN, test);
		  String mspNewPassword = spogServer.ReturnRandom("NewP@ss12");
		  response = spogServer.changePasswordForSpecifiedUser(msp_user_id, mspPassword, mspNewPassword);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
		  spogServer.checkErrorCode(response, "00100101");
		  
		  
		  //create account admin
		  spogServer.userLogin(csrLoginUSerName, csrLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  String accountAdmin = spogServer.ReturnRandom("msp1_account1_user1_specified_yuefen@spogqa.com");
		  String accountPassword = spogServer.ReturnRandom("vvVVccDD12");
		  String account_user_id = spogServer.createUserAndCheck(accountAdmin, accountPassword, spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "direct_admin", account_id, test);
		  //change password for account admin
		  spogServer.userLogin(directAdmin1, directNewPassword1, SpogConstants.SUCCESS_LOGIN, test);
		  String accountNewPassword = spogServer.ReturnRandom("NewP@ss12");
		  response = spogServer.changePasswordForSpecifiedUser(account_user_id, accountPassword, accountNewPassword);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
		  spogServer.checkErrorCode(response, "00100101");
		  
		  
	  }
	  
	  @Test
	  public void accountChangePassword() {
		  test = rep.startTest("accountChangePassword");
		  test.assignAuthor("Liu Yuefen");
		  
		  //create account admin
          spogServer.userLogin(csrLoginUSerName, csrLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  String account1Admin1 = spogServer.ReturnRandom("msp1_account1_userA_specified_yuefen@spogqa.com");
		  String account1Password1 = spogServer.ReturnRandom("vvVVccDD12");
		  String account1_user1_id = spogServer.createUserAndCheck(account1Admin1, account1Password1, spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "direct_admin", account_id, test);
		  //change password for itself
		  spogServer.userLogin(account1Admin1, account1Password1, SpogConstants.SUCCESS_LOGIN, test);
		  String account1NewPassword1 = spogServer.ReturnRandom("NewP@ss12");
		  spogServer.changePasswordForSpecifiedUserWithExpectedStatusCode(account1_user1_id, account1Password1, account1NewPassword1, SpogConstants.SUCCESS_LOGIN, test); 
		  //login with the new password
		  spogServer.userLogin(account1Admin1, account1NewPassword1, SpogConstants.SUCCESS_LOGIN, test);
		  
		  //create another admin2 under the same account
          spogServer.userLogin(csrLoginUSerName, csrLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  String account1Admin2 = spogServer.ReturnRandom("msp1_account1_userB_specified_yuefen@spogqa.com");
		  String account1Password2 = spogServer.ReturnRandom("vvVVccDD12");
		  String account1_user2_id = spogServer.createUserAndCheck(account1Admin2, account1Password2, spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "direct_admin", account_id, test);
		  //change password for admin2
		  spogServer.userLogin(account1Admin1, account1NewPassword1, SpogConstants.SUCCESS_LOGIN, test);
		  String account1NewPassword2 = spogServer.ReturnRandom("NewP@ss12");
		  Response response = spogServer.changePasswordForSpecifiedUser(account1_user2_id, account1Password2, account1NewPassword2);
		  spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
		  spogServer.checkErrorCode(response, "00200014");
		  
		  
		  //craete another admin under account2
          spogServer.userLogin(csrLoginUSerName, csrLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  String account2Admin1 = spogServer.ReturnRandom("msp2_account2_userA_specified_yuefen@spogqa.com");
		  String account2Password1 = spogServer.ReturnRandom("vvVVccDD12");
		  String account2_user1_id = spogServer.createUserAndCheck(account2Admin1, account2Password1, spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "direct_admin", account2_id, test);
		  //change password for admin under the account2
		  spogServer.userLogin(account1Admin1, account1NewPassword1, SpogConstants.SUCCESS_LOGIN, test);
		  String account2NewPassword1 = spogServer.ReturnRandom("NewP@ss12");
		  response = spogServer.changePasswordForSpecifiedUser(account2_user1_id, account2Password1, account2NewPassword1);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
		  spogServer.checkErrorCode(response, "00100101");
		  
		  
		  //create msp admin
          spogServer.userLogin(csrLoginUSerName, csrLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  String mspAdmin = spogServer.ReturnRandom("msp8_specified_yuefen@spogqa.com");
		  String mspPassword = spogServer.ReturnRandom("vvVVccDD12");
		  String msp_user_id = spogServer.createUserAndCheck(mspAdmin, mspPassword, spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "msp_admin", mspOrg_id, test);
		  //change password for msp admin 
		  spogServer.userLogin(account1Admin1, account1NewPassword1, SpogConstants.SUCCESS_LOGIN, test);
		  String mspNewPassword = spogServer.ReturnRandom("NewP@ss12");
		  response = spogServer.changePasswordForSpecifiedUser(msp_user_id, mspPassword, mspNewPassword);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
		  spogServer.checkErrorCode(response, "00100101");
		  
		  
		  //create direct admin
          spogServer.userLogin(csrLoginUSerName, csrLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  String directAdmin = spogServer.ReturnRandom("direct8_specified_yuefen@spogqa.com");
		  String directPassword = spogServer.ReturnRandom("vvVVccDD12");
		  String direct_user_id = spogServer.createUserAndCheck(directAdmin, directPassword, spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "direct_admin", directOrg_id, test);
		  //change password for direct admin
		  spogServer.userLogin(account1Admin1, account1NewPassword1, SpogConstants.SUCCESS_LOGIN, test);
		  String directNewPassword = spogServer.ReturnRandom("NewP@ss12");
		  response = spogServer.changePasswordForSpecifiedUser(direct_user_id, directPassword, directNewPassword);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
		  spogServer.checkErrorCode(response, "00100101");
		  
		  
		  //create csr admin
          spogServer.userLogin(csrLoginUSerName, csrLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  String csrAdmin = spogServer.ReturnRandom("csr8_specified_yuefen@spogqa.com");
		  String csrPassword = spogServer.ReturnRandom("vvVVccDD12");
		  String csr_user_id = spogServer.createUserAndCheck(csrAdmin, csrPassword, spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "csr_admin", "", test);
		  //change password for csr admin
		  spogServer.userLogin(account1Admin1, account1NewPassword1, SpogConstants.SUCCESS_LOGIN, test);
		  String csrNewPassword = spogServer.ReturnRandom("NewP@ss12");
		  response = spogServer.changePasswordForSpecifiedUser(csr_user_id, csrPassword, csrNewPassword);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
		  spogServer.checkErrorCode(response, "00100101");
		  
	  }
	  
	  @Test
	  public void changePasswordNormalCheck() {
		  test = rep.startTest("changePasswordNormalCheck");
		  test.assignAuthor("Liu Yuefen");
		  spogServer.userLogin(csrLoginUSerName, csrLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  //create csr admin
		  String email = spogServer.ReturnRandom("csrx_specified_yuefen@spogqa.com");
		  String password = spogServer.ReturnRandom("vvVVccDD12");
		  String user_id = spogServer.createUserAndCheck(email, password, spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "csr_admin", "", test);
          
		  //login with created user
		  spogServer.userLogin(email, password, SpogConstants.SUCCESS_LOGIN, test);
		  
		  //update password (oldpassword="")
		  Response response = spogServer.changePasswordForSpecifiedUser(user_id, "", "AAggddDD12");
		  spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
		  spogServer.checkErrorCode(response, "40000001");
		  
		  //update password (oldpassword=null)
		  response = spogServer.changePasswordForSpecifiedUser(user_id, null, "AAggddDD12");
		  spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
		  spogServer.checkErrorCode(response, "40000001");
		  
		  
		  //update password (oldpassword=wrong)
		  response = spogServer.changePasswordForSpecifiedUser(user_id, "123sss", "AAggddDD12");
		  spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
		  spogServer.checkErrorCode(response,   "00200015");
		
		  
		  //update password (new password = "")
		  response = spogServer.changePasswordForSpecifiedUser(user_id, password, "");
		  spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
		  spogServer.checkErrorCode(response,   "40000003");
		  spogServer.checkErrorCode(response,   "40000001");
		  spogServer.checkErrorCode(response,   "40000002");
		 
		  
		  //update password (new password =null)
		  response = spogServer.changePasswordForSpecifiedUser(user_id, password, null);
		  spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
		  spogServer.checkErrorCode(response,   "40000001");
	
		  //update password (new password <8)
		  response = spogServer.changePasswordForSpecifiedUser(user_id, password, "Abc1");
		  spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
		  spogServer.checkErrorCode(response,   "40000002");
		  
		  //update password (new password >20)
		  response = spogServer.changePasswordForSpecifiedUser(user_id, password, "AAggEEddDDAAggEEddDD12");
		  spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
		  spogServer.checkErrorCode(response,   "40000002");
		 
		  //update password (new password=upper/lower case)
		  response = spogServer.changePasswordForSpecifiedUser(user_id, password, "AAAAaaaaB");
		  spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
		  spogServer.checkErrorCode(response,   "40000003");
		
		  
		  //update password (new password=upper case/number)
		  response = spogServer.changePasswordForSpecifiedUser(user_id, password, "AABBCCDD12");
		  spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
		  spogServer.checkErrorCode(response,   "40000003");
		 
		  
		  //update password (new password=lower case/number)
		  response = spogServer.changePasswordForSpecifiedUser(user_id, password, "aabbccdd12");
		  spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
		  spogServer.checkErrorCode(response,   "40000003");
		  
		  
		  //update password (user_id = invalid)
		  response = spogServer.changePasswordForSpecifiedUser("123", password, "AAbbccdd12");
		  spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
		  spogServer.checkErrorCode(response,   "40000005");
		  
		  //change password (contain special character)
		  spogServer.userLogin(email, password, SpogConstants.SUCCESS_LOGIN, test);
		  String newPassword = spogServer.ReturnRandom("AA@aa$12");
		  spogServer.changePasswordForSpecifiedUserWithExpectedStatusCode(user_id, password, newPassword, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  //using the latest password to login and return the token
		  spogServer.userLogin(email, newPassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  String JWTToken = spogServer.getJWTToken();
		  
		  //update password for one deleted user
		  //login with csr admin - xiang's
		  spogServer.userLogin(csrLoginUSerName, csrLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  //delete the current csr user
		  spogServer.CheckDeleteUserByIdStatus(user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test); 
		  spogServer.setToken(JWTToken);
		  response = spogServer.changePasswordForSpecifiedUser(user_id, newPassword, "ABCabc123");
		  spogServer.checkResponseStatus(response, SpogConstants.NOT_LOGGED_IN, test);		   
		  spogServer.checkErrorCode(response, "00200001");
		  
	  }
	  
	  @Test
	  public void changePasswordFail() {
		  test = rep.startTest("changePasswordFail");
		  test.assignAuthor("Liu Yuefen");
		  
		  spogServer.userLogin(csrLoginUSerName, csrLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  String csrToken =spogServer.getJWTToken();
		  
		  //create user
		  String email = spogServer.ReturnRandom("changepasword_fail_yuefen@spogqa.com");
		  String old_password = spogServer.ReturnRandom("aaDDaaDD12");
		  String user_id = spogServer.createUserAndCheck(email, old_password, spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "csr_admin", "", test);
		  
		  spogServer.userLogin(email, old_password, SpogConstants.SUCCESS_LOGIN, test);
		  String token = spogServer.getJWTToken();
		  
		  String new_password = spogServer.ReturnRandom("mnvVCd#$1");
		  Response response = spogServer.changePasswordForSpecifiedUserWithBodyNull(user_id);
		  spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);		   
		  spogServer.checkErrorCode(response, "00100002");
		  
		  //delete user
		  spogServer.setToken(csrToken);
		  spogServer.CheckDeleteUserByIdStatus(user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  
		  //change password
		  spogServer.setToken(token);
		  System.out.println("change password");
		  response = spogServer.changePasswordForSpecifiedUser(user_id, old_password, new_password);
		  spogServer.checkResponseStatus(response, SpogConstants.NOT_LOGGED_IN, test);		   
		  spogServer.checkErrorCode(response, "00200001");
	  }
	  
	  @Test
	  public void changePassword401() {
		  test = rep.startTest("changePassword401");
		  test.assignAuthor("Liu Yuefen");
		  
		  spogServer.userLogin(csrLoginUSerName, csrLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  //create user
		  String old_password = spogServer.ReturnRandom("aaDDaaDD12");
		  String user_id = spogServer.createUserAndCheck(spogServer.ReturnRandom("changepasword_401_yuefen@spogqa.com"), old_password, spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "csr_admin", "", test);
		  
		  //set token as ""
		  spogServer.setToken("");
		  String new_password = spogServer.ReturnRandom("mnvVCd#$1");
		  Response response = spogServer.changePasswordForSpecifiedUser(user_id, old_password, new_password);
		  spogServer.checkResponseStatus(response, SpogConstants.NOT_LOGGED_IN, test);		   
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
