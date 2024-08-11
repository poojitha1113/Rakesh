package api.users.authenticate;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;

import Constants.SpogConstants;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;

import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;

import io.restassured.response.Response;
import java.io.IOException;
import java.text.SimpleDateFormat;
import org.apache.http.client.ClientProtocolException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
public class AuthenticateUserTest {
	  private SPOGServer spogServer;
	  private UserSpogServer userSpogServer;
	  private String csrOrg_id;
	  private String directOrg_id;
	  private String directOrg2_id;
	  private String mspOrg_id;
	  private String mspOrg2_id;
	  private String account_id;
	  private String account2_id;
	  private String msp_email;
	  private String direct_email;
	  private String accountDirect_email;
	  private String sharePassword = "Caworld_2018";
	  private ExtentReports rep;
	  private ExtentTest test;
	  private Response response;
	  private String csrGlobalLoginUser;
	  private String csrGlobalLoginPassword;
	  private String cloudDirectToken_csr;
	  private String cloudDirectToken_direct;
	  private String cloudDirectToken_msp;
	  private String cloudDirectToken_account;
	//this is for update portal, each testng class is taken as BQ set
	  private SQLServerDb bqdb1;
	  public int Nooftest;
	  private long creationTime;
	  private String BQName=null;
	  private String runningMachine;
	  private testcasescount count1;
	  private String buildVersion;
	  //end 
	  
	  @BeforeClass
	  
	  @Parameters({ "baseURI", "port" , "csrAdminUserName", "csrAdminPassword","logFolder","runningMachine", "buildVersion"})
	  public void beforeClass(String baseURI, String port, String userName, String password, String logFolder, String runningMachine, String buildVersion) {
		
		//this is for update portal
		  this.BQName = this.getClass().getSimpleName();
		  String author = "yuefen.liu";
		  this.runningMachine = runningMachine;
		  SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		  java.util.Date date=new java.util.Date();
		  this.buildVersion=buildVersion+"_"+dateFormater.format(date);
		  Nooftest=0;
		  bqdb1 = new SQLServerDb();
		  count1 = new testcasescount();
		  if(count1.isstarttimehit()==0) {
			System.out.println("Into AuthenticateUserTest");
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
		  userSpogServer = new UserSpogServer(baseURI, port);
		  rep = ExtentManager.getInstance("DeleteCloudAccountTest",logFolder);
		  test = rep.startTest("beforeClass");
		  spogServer.userLogin(userName, password);
		  this.csrGlobalLoginUser = userName;
		  this.csrGlobalLoginPassword = password;
		  //create org
		  csrOrg_id = spogServer.GetLoggedinUserOrganizationID();
		  System.out.println("csr org id:"+csrOrg_id);
		  directOrg_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_group_direct1_yuefen"),"direct",spogServer.ReturnRandom("yuefen_direct1_group@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("direct_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  directOrg2_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_group_direct2_yuefen"),"direct",spogServer.ReturnRandom("yuefen_direct2_group@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("direct2_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  mspOrg_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_group_yuefen_msp1"),"msp",spogServer.ReturnRandom("yuefen_msp1_group@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("msp_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  mspOrg2_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_group_yuefen_msp2"),"msp",spogServer.ReturnRandom("yuefen_msp2_group@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("msp2_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  account_id = spogServer.createAccountWithCheck(mspOrg_id,spogServer.ReturnRandom("spogqa_accoun1_msp1_yuefen_group"), mspOrg_id, test);
		  account2_id = spogServer.createAccountWithCheck(mspOrg2_id, spogServer.ReturnRandom("spogqa_accoun2_msp2_yuefen_spogqa"), mspOrg2_id, test);
		  
		  //create cloud direct under csr org
		  String cloudAccountkey = spogServer.ReturnRandom("cloud_direct_key_csr");
		  String cloudAccountSecret = spogServer.ReturnRandom("cloud_direct_secret_csr");
		  spogServer.createCloudAccountWithCheck(cloudAccountkey, cloudAccountSecret, spogServer.ReturnRandom("cloud_direct_name_csr"), "cloud_direct", csrOrg_id, test);
		  cloudDirectToken_csr = spogServer.cloudDirectAccountLoginSuccess(cloudAccountkey, cloudAccountSecret, test);
		  
		  //create cloud direct under direct org
		  cloudAccountkey = spogServer.ReturnRandom("cloud_direct_key_direct");
		  cloudAccountSecret = spogServer.ReturnRandom("cloud_direct_secret_direct");
		  spogServer.createCloudAccountWithCheck(cloudAccountkey, cloudAccountSecret, spogServer.ReturnRandom("cloud_direct_name_direct"), "cloud_direct", directOrg_id, test);
		  cloudDirectToken_direct = spogServer.cloudDirectAccountLoginSuccess(cloudAccountkey, cloudAccountSecret, test);
		  
		  //create cloud direct under msp org
		  cloudAccountkey = spogServer.ReturnRandom("cloud_direct_key_msp");
		  cloudAccountSecret = spogServer.ReturnRandom("cloud_direct_secret_msp");
		  spogServer.createCloudAccountWithCheck(cloudAccountkey, cloudAccountSecret, spogServer.ReturnRandom("cloud_direct_name_msp"), "cloud_direct", mspOrg_id, test);
		  cloudDirectToken_msp = spogServer.cloudDirectAccountLoginSuccess(cloudAccountkey, cloudAccountSecret, test);
		  
		  //create cloud direct under direct org
		  cloudAccountkey = spogServer.ReturnRandom("cloud_direct_key_account");
		  cloudAccountSecret = spogServer.ReturnRandom("cloud_direct_secret_account");
		  spogServer.createCloudAccountWithCheck(cloudAccountkey, cloudAccountSecret, spogServer.ReturnRandom("cloud_direct_name_account"), "cloud_direct", account_id, test);
		  cloudDirectToken_account = spogServer.cloudDirectAccountLoginSuccess(cloudAccountkey, cloudAccountSecret, test);
	  }
	  
	  @DataProvider(name = "userInfo7")
	  public final Object[][] getUserInfo7() {
		  return new Object[][] { {spogServer.ReturnRandom("direct_user7@spogqa.com"), sharePassword,"yuefen","liu", "direct_admin", directOrg_id, cloudDirectToken_direct}
			  };
		}
	  @Test(dataProvider = "userInfo7")
	  public void TokenAuthenticateUser(String email, String password, String firstName, String lastName, String roleID, String orgID, String Token){	 
		  System.out.println("directOrgTokenAuthenticateUser");
		  test = rep.startTest("directOrgTokenAuthenticateUser");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login with csr user
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		 
		  //create user
		  String user_id = spogServer.createUserAndCheck(email, password, firstName, lastName, roleID, orgID, test);
		  
		  //authenticate user with invalid token
		  String invalidToken = spogServer.ReturnRandom(Token);
		  Response response = userSpogServer.AuthenticateUser(email, password, invalidToken);
		  spogServer.checkResponseStatus(response, SpogConstants.NOT_LOGGED_IN);
		  spogServer.checkErrorCode(response, "");
		  
		  //create another
		 
		  //delete user
		  spogServer.CheckDeleteUserByIdStatus(user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  
		  
	  }
	  
	  @DataProvider(name = "userInfo1")
	  public final Object[][] getUserInfo1() {
		  return new Object[][] { {spogServer.ReturnRandom("direct_user1@spogqa.com"), sharePassword,"yuefen","liu", "direct_admin", directOrg_id, cloudDirectToken_direct}
			  };
		}
	  
	  @Test(dataProvider = "userInfo1")
	  public void directOrgTokenAuthenticateUser(String email, String password, String firstName, String lastName, String roleID, String orgID, String Token){	 
		  System.out.println("directOrgTokenAuthenticateUser");
		  test = rep.startTest("directOrgTokenAuthenticateUser");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login with csr user
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		 
		  //create user
		  String user_id = spogServer.createUserAndCheck(email, password, firstName, lastName, roleID, orgID, test);
		  
		  //authenticate user
		  Response response = userSpogServer.AuthenticateUser(email, password, Token);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_LOGIN);
		  
		  //delete user
		  spogServer.CheckDeleteUserByIdStatus(user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	  }
	  
	  @DataProvider(name = "userInfo2")
	  public final Object[][] getUserInfo2() {
		  return new Object[][] { {spogServer.ReturnRandom("direct2_user2@spogqa.com"), sharePassword,"yuefen","liu", "direct_admin", directOrg2_id, cloudDirectToken_direct},
			  {spogServer.ReturnRandom("csr_user2@spogqa.com"), sharePassword,"yuefen","liu", "csr_admin", csrOrg_id, cloudDirectToken_direct},
			  {spogServer.ReturnRandom("msp_user2@spogqa.com"), sharePassword,"yuefen","liu", "msp_admin", mspOrg_id, cloudDirectToken_direct},
			  {spogServer.ReturnRandom("account_user2@spogqa.com"), sharePassword,"yuefen","liu", "direct_admin", account_id, cloudDirectToken_direct}
			  };
		}
	  
	  @Test(dataProvider = "userInfo2")
	  public void directOrgTokenAuthenticateUserFail(String email, String password, String firstName, String lastName, String roleID, String orgID, String Token){	 
		  System.out.println("directOrgTokenAuthenticateUserFail");
		  test = rep.startTest("directOrgTokenAuthenticateUserFail");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login with csr user
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		 
		  //create user
		  String user_id = spogServer.createUserAndCheck(email, password, firstName, lastName, roleID, orgID, test);
		  
		  //authenticate user
		  Response response = userSpogServer.AuthenticateUser(email, password, Token);
		  spogServer.checkResponseStatus(response, SpogConstants.NOT_LOGGED_IN, test);
		  
		  //delete user
		  spogServer.CheckDeleteUserByIdStatus(user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	  }
	  
	  @DataProvider(name = "userInfo3")
	  public final Object[][] getUserInfo3() {
		  return new Object[][] { {spogServer.ReturnRandom("msp_user3@spogqa.com"), sharePassword,"yuefen","liu", "msp_admin", mspOrg_id, cloudDirectToken_msp},
			  {spogServer.ReturnRandom("account_user3@spogqa.com"), sharePassword,"yuefen","liu", "direct_admin", account_id, cloudDirectToken_msp}
			  };
		}
	  
	  @Test(dataProvider = "userInfo3")
	  public void mspOrgTokenAuthenticateUser(String email, String password, String firstName, String lastName, String roleID, String orgID, String Token){	 
		  System.out.println("mspOrgTokenAuthenticateUser");
		  test = rep.startTest("mspOrgTokenAuthenticateUser");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login with csr user
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		 
		  //create user
		  String user_id = spogServer.createUserAndCheck(email, password, firstName, lastName, roleID, orgID, test);
		  
		  //authenticate user
		  Response response = userSpogServer.AuthenticateUser(email, password, Token);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_LOGIN);
		  
		  //delete user
		  spogServer.CheckDeleteUserByIdStatus(user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	  }
	  
	  @DataProvider(name = "userInfo4")
	  public final Object[][] getUserInfo4() {
		  return new Object[][] { {spogServer.ReturnRandom("msp_user4@spogqa.com"), sharePassword,"yuefen","liu", "msp_admin", mspOrg2_id, cloudDirectToken_msp},
			  {spogServer.ReturnRandom("account2_user4@spogqa.com"), sharePassword,"yuefen","liu", "direct_admin", account2_id, cloudDirectToken_msp},
			  {spogServer.ReturnRandom("csr_user4@spogqa.com"), sharePassword,"yuefen","liu", "csr_admin", csrOrg_id, cloudDirectToken_msp},
			  {spogServer.ReturnRandom("direct_user4@spogqa.com"), sharePassword,"yuefen","liu", "direct_admin", directOrg_id, cloudDirectToken_msp}
			  };
		}
	  
	  @Test(dataProvider = "userInfo4")
	  public void mspOrgTokenAuthenticateUserFail(String email, String password, String firstName, String lastName, String roleID, String orgID, String Token){	 
		  System.out.println("mspOrgTokenAuthenticateUserFail");
		  test = rep.startTest("mspOrgTokenAuthenticateUserFail");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login with csr user
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		 
		  //create user
		  String user_id = spogServer.createUserAndCheck(email, password, firstName, lastName, roleID, orgID, test);
		  
		  //authenticate user
		  Response response = userSpogServer.AuthenticateUser(email, password, Token);
		  spogServer.checkResponseStatus(response, SpogConstants.NOT_LOGGED_IN, test);
		  
		  //delete user
		  spogServer.CheckDeleteUserByIdStatus(user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	  }
	  @DataProvider(name = "userInfo5")
	  public final Object[][] getUserInfo5() {
		  return new Object[][] { 
			  {spogServer.ReturnRandom("account_user5@spogqa.com"), sharePassword,"yuefen","liu", "direct_admin", account_id, cloudDirectToken_account}
			  };
		}
	  
	  @Test(dataProvider = "userInfo5")
	  public void accountOrgTokenAuthenticateUser(String email, String password, String firstName, String lastName, String roleID, String orgID, String Token){	 
		  System.out.println("accountOrgTokenAuthenticateUser");
		  test = rep.startTest("accountOrgTokenAuthenticateUser");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login with csr user
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		 
		  //create user
		  String user_id = spogServer.createUserAndCheck(email, password, firstName, lastName, roleID, orgID, test);
		  
		  //authenticate user
		  Response response = userSpogServer.AuthenticateUser(email, password, Token);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_LOGIN);
		  
		  //delete user
		  spogServer.CheckDeleteUserByIdStatus(user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	  }
	  
	  @DataProvider(name = "userInfo6")
	  public final Object[][] getUserInfo6() {
		  return new Object[][] { {spogServer.ReturnRandom("msp_user6@spogqa.com"), sharePassword,"yuefen","liu", "msp_admin", mspOrg2_id, cloudDirectToken_account},
			  {spogServer.ReturnRandom("account2_user6@spogqa.com"), sharePassword,"yuefen","liu", "direct_admin", account2_id, cloudDirectToken_account},
			  {spogServer.ReturnRandom("csr_user6@spogqa.com"), sharePassword,"yuefen","liu", "csr_admin", csrOrg_id, cloudDirectToken_account},
			  {spogServer.ReturnRandom("direct_user6@spogqa.com"), sharePassword,"yuefen","liu", "direct_admin", directOrg_id, cloudDirectToken_account}
			  };
		}
	  
	  @Test(dataProvider = "userInfo6")
	  public void accountOrgTokenAuthenticateUserFail(String email, String password, String firstName, String lastName, String roleID, String orgID, String Token){	 
		  System.out.println("accountOrgTokenAuthenticateUserFail");
		  test = rep.startTest("accountOrgTokenAuthenticateUserFail");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login with csr user
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		 
		  //create user
		  String user_id = spogServer.createUserAndCheck(email, password, firstName, lastName, roleID, orgID, test);
		  
		  //authenticate user
		  Response response = userSpogServer.AuthenticateUser(email, password, Token);
		  spogServer.checkResponseStatus(response, SpogConstants.NOT_LOGGED_IN, test);
		  
		  //delete user
		  spogServer.CheckDeleteUserByIdStatus(user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	  }  
}
