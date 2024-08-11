package api.users.assignedaccounts;
import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.text.SimpleDateFormat;
import static org.hamcrest.Matchers.equalTo;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.client.ClientProtocolException;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.SpogConstants;
import InvokerServer.SPOGServer;
import InvokerServer.Org4SPOGServer;
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import io.restassured.response.Response;

import genericutil.SQLServerDb;
import genericutil.testcasescount;
public class GetAssignedMspAccountsForSpecifiedUserTest extends base.prepare.PrepareOrgInfo{
	@Parameters({ "pmfKey"})
	  public  GetAssignedMspAccountsForSpecifiedUserTest(String pmfKey) {
			super(pmfKey);
			// TODO Auto-generated constructor stub
		}
	private SPOGServer spogServer;
	private UserSpogServer userSpogServer;
	private Org4SPOGServer org4SPOGServer;
	  private String csrOrg_id;
	  private String directOrg_id;
	  private String directOrg2_id;
	  private String mspOrg_id;
	  private String mspOrg2_id;
	  private String account_id;
	  private String account2_id;
//	  private ExtentReports rep;
	  private ExtentTest test;
	  private Response response;
	  private String csrGlobalLoginUser;
	  private String csrGlobalLoginPassword;
	  private String csrReadOnlyUser = "liuyu05@arcserve.com";
	  private String sharePassword = "Caworld_2018";
	  private String csr_email;
	  private String direct_email;
	  private String msp_email;
	  private String msp2_email;
	  private String msp_account_admin;
	  private String msp_account_admin_id;
	  private String accountDirect_email;
	  private String csr_token;
	  private String csr_readonly_token;
	  private String msp_token;
	  private String msp2_token;
	  private String msp_account_admin_token;
	  private String direct_token;
	  private String account_token;
	  private String csr_user_id;
	  private String direct_user_id;
	  private String msp_user_id;
	  private String account_user_id;
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
	  public void beforeClass(String baseURI, String port, String userName, String password, String logFolder, String runningMachine, String buildVersion) {
		  rep = ExtentManager.getInstance("GetAssignedMspAccountsForSpecifiedUserTest",logFolder);
		  test = rep.startTest("beforeClass");
		  spogServer = new SPOGServer(baseURI, port);
		  userSpogServer =new UserSpogServer(baseURI, port);
		  org4SPOGServer = new Org4SPOGServer(baseURI,port);
		  
		  spogServer.userLogin(this.csrReadOnlyUser, sharePassword); 
		  this.csr_readonly_token = spogServer.getJWTToken();
		  
		  spogServer.userLogin(userName, password); 
		  csr_token = spogServer.getJWTToken();
		  
		  this.csrGlobalLoginUser = userName;
		  this.csrGlobalLoginPassword = password;
		  csrOrg_id = spogServer.GetLoggedinUserOrganizationID();
		  directOrg_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_update_direct1_yuefen")+org_prefix,"direct",spogServer.ReturnRandom("spogqa_direct1_update@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("direct_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
//		  directOrg2_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_update_direct2_yuefen"),"direct",spogServer.ReturnRandom("spogqa_direct2_update@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("direct_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  mspOrg_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_update_yuefen_msp1")+org_prefix,"msp",spogServer.ReturnRandom("spogqa_update_msp1@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("direct_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  mspOrg2_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_update_yuefen_msp2")+org_prefix,"msp",spogServer.ReturnRandom("spogqa_update_msp2@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("direct_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  account_id = spogServer.createAccountWithCheck(mspOrg_id,spogServer.ReturnRandom("accoun1_msp1_yuefen_spogqa")+org_prefix, mspOrg_id, test);
//		  account2_id = spogServer.createAccountWithCheck(mspOrg_id, spogServer.ReturnRandom("accoun2_msp1_yuefen_spogqa"), mspOrg_id, test);

		  //create direct user
		  System.out.println("direct user");
		  this.direct_email = spogServer.ReturnRandom("direct_yuefen_update@spogqa.com");
		  direct_user_id = spogServer.createUserAndCheck(direct_email, sharePassword, "yuefen", "liu", "direct_admin", directOrg_id, test);
		  spogServer.userLogin(direct_email, sharePassword);
		  this.direct_token = spogServer.getJWTToken();
		  //create msp1 user
		  System.out.println("msp1 user");
		  spogServer.setToken(csr_token);
		  this.msp_email = spogServer.ReturnRandom("msp1_yuefen_update@spogqa.com");
		  msp_user_id =spogServer.createUserAndCheck(msp_email, sharePassword, "yuefen", "liu", "msp_admin", mspOrg_id, test);
		  spogServer.userLogin(msp_email, sharePassword);
		  this.msp_token = spogServer.getJWTToken();
		  // create msp2 user
		  System.out.println("msp2 user");
		  spogServer.setToken(csr_token);
		  this.msp2_email = spogServer.ReturnRandom("msp2_yuefen_update@spogqa.com");
		  spogServer.createUserAndCheck(msp2_email, sharePassword, "yuefen", "liu", "msp_admin", mspOrg2_id, test);
		  spogServer.userLogin(msp2_email, sharePassword);
		  this.msp2_token = spogServer.getJWTToken();
		  //create msp account admin
		  System.out.println("msp account admin user");
		  spogServer.setToken(csr_token);
		  this.msp_account_admin = spogServer.ReturnRandom("msp_account_admin_yuefen@spogqa.com");
		  msp_account_admin_id=spogServer.createUserAndCheck(msp_account_admin, sharePassword, "yuefen", "liu", "msp_account_admin", mspOrg_id, test);
		  spogServer.userLogin(msp_account_admin, sharePassword);
		  this.msp_account_admin_token = spogServer.getJWTToken();
		  //create account user
		  System.out.println("account user");
		  spogServer.setToken(csr_token);
		  this.accountDirect_email = spogServer.ReturnRandom("account1_msp1_yuefen_update@spogqa.com");
		  account_user_id=spogServer.createUserAndCheck(accountDirect_email, sharePassword, "yuefen", "liu", "direct_admin", account_id, test);
		  spogServer.userLogin(accountDirect_email, sharePassword);
		  this.account_token = spogServer.getJWTToken();
		  
		  prepare(baseURI, port, logFolder, this.csrGlobalLoginUser,  this.csrGlobalLoginPassword, this.getClass().getSimpleName() );
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
        	  System.out.println("Into GetAssignedMspAccountsForSpecifiedUserTest");
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
	  
	  @DataProvider(name = "UserInfo10")
	  public final Object[][] getUserInfo10() {
		  return new Object[][] { 
			          {this.final_root_msp_user_name_email, this.common_password,this.root_msp_org_id},
			          {this.final_sub_msp1_user_name_email, this.common_password,this.sub_msp1_org_id},
			          }; 
		}
	  //in the beginning, msp account admin get fail with 403, later change the behavior to 200
	  @Test (dataProvider = "UserInfo10")
	  public void rootMspRelatedGetSuccess(String loginUser, String loginPassword, String parentOrg) {
		  System.out.println("rootMspRelatedGetSuccess");
		  test = rep.startTest("rootMspRelatedGetSuccess");
		  test.assignAuthor("Liu Yuefen");
          
		  spogServer.setToken(csr_token);
		  //create msp account admin
		  String mspAccountAdmin_id=spogServer.createUserAndCheck(spogServer.ReturnRandom("mspAccountAdmin_msp1_yuefen@spogqa.com"), sharePassword, "yuefen", "liu", "msp_account_admin", parentOrg, test);
		  
		  int num =2;
		  String[] subOrgIds = new String[num];
		  for (int i=0;i<num;i++) {
			  subOrgIds[i] = spogServer.createAccountWithCheck(parentOrg,spogServer.ReturnRandom("accoun1_msp1_yuefen_spogqa")+org_prefix, parentOrg, test);
			  userSpogServer.assignMspAccountAdmins(parentOrg, subOrgIds[i], mspAccountAdmin_id, csr_token);
			  System.out.println("org:"+subOrgIds[i]);
		  }
		  
          //login
		  spogServer.userLogin(loginUser, loginPassword);
		  String token=spogServer.getJWTToken();
		  Response response = userSpogServer.getAllSubOrgsAssignedToMspAccountAdmin(mspAccountAdmin_id, token);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);

	  }
	  
	  @DataProvider(name = "UserInfo20")
	  public final Object[][] getUserInfo20() {
		  return new Object[][] { 
			          {this.final_root_msp_user_name_email, this.common_password,this.sub_msp1_org_id},
	                  {this.msp_email, sharePassword,this.sub_msp1_org_id},
	                  {this.direct_email, sharePassword,this.sub_msp1_org_id},
	          
	                  {this.final_sub_msp1_user_name_email, this.common_password,this.root_msp_org_id},
	                  {this.msp_email, sharePassword,this.root_msp_org_id},
	                  {this.direct_email,sharePassword,this.root_msp_org_id},
	          
	                  {this.final_root_msp_user_name_email, this.common_password,this.mspOrg_id},
	                  {this.final_sub_msp1_user_name_email, this.common_password,this.mspOrg_id},
			          }; 
		}
	  @Test (dataProvider = "UserInfo20")
	  public void rootMspRelatedGetFail(String loginUser, String loginPassword, String parentOrg) {
		  System.out.println("getAccountsAssignWithMspAdminUsersFail");
		  test = rep.startTest("getAccountsAssignWithMspAdminUsersFail");
		  test.assignAuthor("Liu Yuefen");
		  
		  spogServer.setToken(csr_token);
		  System.out.println("token:"+csr_token);
		  //create msp account admin
		  String mspAccountAdmin_id=spogServer.createUserAndCheck(spogServer.ReturnRandom("mspAccountAdmin_msp1_yuefen@spogqa.com"), sharePassword, "yuefen", "liu", "msp_account_admin", parentOrg, test);
          //create sub org
		  String subOrg = spogServer.createAccountWithCheck(parentOrg,spogServer.ReturnRandom("accoun1_msp1_yuefen_spogqa")+org_prefix, parentOrg, test);
		  userSpogServer.assignMspAccountAdmins(parentOrg, subOrg,mspAccountAdmin_id, csr_token);

		  //login
		  spogServer.userLogin(loginUser, loginPassword);
		  String token=spogServer.getJWTToken();
		  Response response = userSpogServer.getAllSubOrgsAssignedToMspAccountAdmin(mspAccountAdmin_id,token );
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS);
		  spogServer.checkErrorCode(response, "00100101");

		  
	  }
	  
	  @Test 
	  public void csrReadOnlyGetAccountsBlockStatusAssignWithMspAdmin() {
		  System.out.println("csrReadOnlyGetAccountsBlockStatusAssignWithMspAdmin");
		  test = rep.startTest("csrReadOnlyGetAccountsBlockStatusAssignWithMspAdmin");
		  test.assignAuthor("Liu Yuefen");
		  
		  spogServer.setToken(csr_token);
		  System.out.println("token:"+csr_token);
		  //create msp org
		  String mspOrg = spogServer.CreateOrganizationWithCheck(RandomStringUtils.randomAlphanumeric(3)+org_prefix, "msp", spogServer.ReturnRandom("msp_yuefen_test@spogqa.com"), sharePassword, "yuefen", "liu");
		  //create msp account admin
		  String admin_id = spogServer.createUserAndCheck(spogServer.ReturnRandom("msp_account_admin_test@spogqa.com"), sharePassword, "yuefen", "liu", "msp_account_admin", mspOrg, test);
          //create sub org
		  String subOrg = spogServer.createAccountWithCheck(mspOrg,spogServer.ReturnRandom("accoun1_msp1_yuefen_spogqa"), mspOrg, test);
		  //assign org
		  userSpogServer.setToken(csr_token);
		  userSpogServer.assignMspAccountAdmins(mspOrg, subOrg, admin_id, csr_token);
		  
		  //get sub orgs
		  userSpogServer.setToken(csr_readonly_token);
		  Response response = userSpogServer.getAllSubOrgsAssignedToMspAccountAdmin(admin_id, csr_readonly_token);
		  spogServer.checkResponseStatus(response,SpogConstants.SUCCESS_GET_PUT_DELETE);
		  
	  }
	  
	  
	  @DataProvider(name = "UserInfo4")
	  public final Object[][] getUserInfo4() {
		  return new Object[][] { 
					  {csr_token,SpogConstants.SUCCESS_GET_PUT_DELETE,""}, 
					  //{msp_token,SpogConstants.SUCCESS_GET_PUT_DELETE,""},
					  {msp2_token,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101"},
					  {direct_token,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101"},
					  //{msp_account_admin_token,SpogConstants.SUCCESS_GET_PUT_DELETE,""},
					  {account_token,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101"}
			          }; 
		}
	  @Test (dataProvider = "UserInfo4")
	  public void getAccountsBlockStatusAssignWithMspAdmin(String token,int expectedStatusCode,String expectedErrorCode) {
		  System.out.println("getAccountsBlockStatusAssignWithMspAdmin");
		  test = rep.startTest("getAccountsBlockStatusAssignWithMspAdmin");
		  test.assignAuthor("Liu Yuefen");
		  
		  spogServer.setToken(csr_token);
		  System.out.println("token:"+csr_token);
		  //create msp org
		  String mspOrg = spogServer.CreateOrganizationWithCheck(RandomStringUtils.randomAlphanumeric(3)+org_prefix, "msp", spogServer.ReturnRandom("msp_yuefen_test@spogqa.com"), sharePassword, "yuefen", "liu");
		  //create msp account admin
		  String admin_id = spogServer.createUserAndCheck(spogServer.ReturnRandom("msp_account_admin_test@spogqa.com"), sharePassword, "yuefen", "liu", "msp_account_admin", mspOrg, test);
          //create sub org
		  String subOrg = spogServer.createAccountWithCheck(mspOrg,spogServer.ReturnRandom("accoun1_msp1_yuefen_spogqa"), mspOrg, test);
		  //assign org
		  userSpogServer.setToken(csr_token);
		  userSpogServer.assignMspAccountAdmins(mspOrg, subOrg, admin_id, csr_token);
		  //change block status
		  spogServer.updateOrganizationInfoByID(subOrg, spogServer.ReturnRandom("accoun1_msp1_yuefen_spogqa_new"), "true", test);
		  //get sub orgs
		  userSpogServer.setToken(token);
		  Response response = userSpogServer.getAllSubOrgsAssignedToMspAccountAdmin(admin_id, token);
		  spogServer.checkResponseStatus(response,expectedStatusCode);
		  if (expectedStatusCode !=200) {
			  spogServer.checkErrorCode(response, expectedErrorCode);
		  }else {
			  ArrayList<HashMap<String, Object>> orgList = new ArrayList<>();
			  orgList = response.then().extract().path("data");
			  HashMap<String, Object> orgInfo = orgList.get(0);
			  assertEquals(orgInfo.get("organization_id").toString(), subOrg.toString());
			  assertEquals(orgInfo.get("organization_type").toString(), "msp_child");
			  assertEquals(orgInfo.get("blocked").toString(), "true");
		  }
		  
		  //delete org
//		  spogServer.setToken(csr_token);
//		  spogServer.DeleteOrganizationWithCheck(subOrg, test);
//		  spogServer.DeleteOrganizationWithCheck(mspOrg, test);  
	  }
	  public void MspTokenGetAccountsBlockStatusAssignWithMspAdmin() {
		  System.out.println("MspTokenGetAccountsBlockStatusAssignWithMspAdmin");
		  test = rep.startTest("MspTokenGetAccountsBlockStatusAssignWithMspAdmin");
		  test.assignAuthor("Liu Yuefen");
		  
		  spogServer.setToken(csr_token);
		  System.out.println("token:"+csr_token);
		  //create msp org
		  String userName = spogServer.ReturnRandom("msp_yuefen_test@spogqa.com");
		  String mspOrg = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("msp_yuefen_org")+org_prefix, "msp", userName, sharePassword, "yuefen", "liu");
		  spogServer.userLogin(userName, sharePassword);
		  String token =spogServer.getJWTToken();
		  //String msp_id = spogServer.GetLoggedinUser_UserID();
		  //create msp account admin
		  String admin_id = spogServer.createUserAndCheck(spogServer.ReturnRandom("msp_account_admin_test@spogqa.com"), sharePassword, "yuefen", "liu", "msp_account_admin", mspOrg, test);
          //create sub org
		  String subOrg = spogServer.createAccountWithCheck(mspOrg,spogServer.ReturnRandom("accoun1_msp1_yuefen_spogqa")+org_prefix, mspOrg, test);
		  //assign org
		  userSpogServer.setToken(csr_token);
		  userSpogServer.assignMspAccountAdmins(mspOrg, subOrg, admin_id, csr_token);
		  //change block status
		  spogServer.setToken(csr_token);
		  spogServer.updateOrganizationInfoByID(subOrg, "", "true", test);
		  //get sub orgs
		  userSpogServer.setToken(token);
		  Response response = userSpogServer.getAllSubOrgsAssignedToMspAccountAdmin(admin_id, token);
		  spogServer.checkResponseStatus(response,SpogConstants.SUCCESS_GET_PUT_DELETE);
		  
			  ArrayList<HashMap<String, Object>> orgList = new ArrayList<>();
			  orgList = response.then().extract().path("data");
			  HashMap<String, Object> orgInfo = orgList.get(0);
			  assertEquals(orgInfo.get("organization_id").toString(), subOrg.toString());
			  assertEquals(orgInfo.get("organization_type").toString(), "msp_child");
			  assertEquals(orgInfo.get("blocked").toString(), "true");
		 
		  
		  //delete org
//		  spogServer.setToken(csr_token);
//		  spogServer.DeleteOrganizationWithCheck(subOrg, test);
//		  spogServer.DeleteOrganizationWithCheck(mspOrg, test);  
	  }
	  public void MspAccountAdminTokenGetAccountsBlockStatusAssignWithMspAdmin() {
		  System.out.println("MspAccountAdminTokenGetAccountsBlockStatusAssignWithMspAdmin");
		  test = rep.startTest("MspAccountAdminTokenGetAccountsBlockStatusAssignWithMspAdmin");
		  test.assignAuthor("Liu Yuefen");
		  
		  spogServer.setToken(csr_token);
		  System.out.println("token:"+csr_token);
		  //create msp org
		  String userName = spogServer.ReturnRandom("msp_yuefen_test@spogqa.com");
		  String mspOrg = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("msp_yuefen_org")+org_prefix, "msp", userName, sharePassword, "yuefen", "liu");

		  //create msp account admin
		  String admin_name = spogServer.ReturnRandom("msp_account_admin_test@spogqa.com");
		  String admin_id = spogServer.createUserAndCheck(admin_name, sharePassword, "yuefen", "liu", "msp_account_admin", mspOrg, test);
		  spogServer.userLogin(admin_name, sharePassword);
		  String token =spogServer.getJWTToken();
          //create sub org
		  spogServer.setToken(csr_token);
		  String subOrg = spogServer.createAccountWithCheck(mspOrg,spogServer.ReturnRandom("accoun1_msp1_yuefen_spogqa")+org_prefix, mspOrg, test);
		  //assign org
		  userSpogServer.setToken(csr_token);
		  userSpogServer.assignMspAccountAdmins(mspOrg, subOrg, admin_id, csr_token);
		  //change block status
		  spogServer.setToken(csr_token);
		  spogServer.updateOrganizationInfoByID(subOrg, "", "true", test);
		  //get sub orgs
		  userSpogServer.setToken(token);
		  Response response = userSpogServer.getAllSubOrgsAssignedToMspAccountAdmin(admin_id, token);
		  spogServer.checkResponseStatus(response,SpogConstants.SUCCESS_GET_PUT_DELETE);
		  
			  ArrayList<HashMap<String, Object>> orgList = new ArrayList<>();
			  orgList = response.then().extract().path("data");
			  HashMap<String, Object> orgInfo = orgList.get(0);
			  assertEquals(orgInfo.get("organization_id").toString(), subOrg.toString());
			  assertEquals(orgInfo.get("organization_type").toString(), "msp_child");
			  assertEquals(orgInfo.get("blocked").toString(), "true");
		 
		  
		  //delete org
//		  spogServer.setToken(csr_token);
//		  spogServer.DeleteOrganizationWithCheck(subOrg, test);
//		  spogServer.DeleteOrganizationWithCheck(mspOrg, test);  
	  }
	  
	  @Test 
	  public void getAccountsWithCheck() {
		  System.out.println("getAccountsWithCheck");
		  test = rep.startTest("getAccountsWithCheck");
		  test.assignAuthor("Liu Yuefen");
          
		  spogServer.setToken(csr_token);
		  System.out.println("token:"+csr_token);
		  //create msp account admin
		  String userId=spogServer.createUserAndCheck(spogServer.ReturnRandom("yuefen_msp_account_admin@spogqa.com"), sharePassword, "yuefen", "liu", "msp_account_admin", mspOrg_id, test);
		  //create sub org and assign
		  int num =2;
		  String[] subOrgIds = new String[num];
		  for (int i=0;i<num;i++) {
			  subOrgIds[i]=spogServer.createAccountWithCheck(mspOrg_id,spogServer.ReturnRandom("accoun1_msp1_yuefen_spogqa")+org_prefix, mspOrg_id, test);
			  userSpogServer.assignMspAccountAdmins(mspOrg_id, subOrgIds[i], userId, csr_token);
		  }
		  //get sub orgs with check
		  Response response = userSpogServer.getAllSubOrgsAssignedToMspAccountAdmin(userId, csr_token);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		  ArrayList<Map<String,Object>> orgList = new ArrayList<>();
		  orgList= response.then().extract().path("data");
		  for(int j=0;j<2;j++)
		  {
			  if (orgList.get(j).containsValue(subOrgIds[j])){
				  assertTrue("It's correct", true);
			  }
		  }
		  //delete msp account admin
		  spogServer.CheckDeleteUserByIdStatus(userId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  //delete org
//		  spogServer.setToken(csr_token);
//		  for (int i=0;i<num;i++) {
//			  spogServer.DeleteOrganizationWithCheck(subOrgIds[i], test);
//		  }   
	  }
	  
	  @DataProvider(name = "UserInfo")
	  public final Object[][] getUserInfo() {
		  return new Object[][] { 
					  {csr_token}, 
					  {msp_token},
					  {msp_account_admin_token}
			          }; 
		}
	  //in the beginning, msp account admin get fail with 403, later change the behavior to 200
	  @Test (dataProvider = "UserInfo")
	  public void getAccountsAssignWithMspAdminUsersSuccessful(String token) {
		  System.out.println("getAccountsAssignWithMspAdminUsersSuccessful");
		  test = rep.startTest("getAccountsAssignWithMspAdminUsersSuccessful");
		  test.assignAuthor("Liu Yuefen");
          
		  spogServer.setToken(csr_token);
		  
		  int num =2;
		  String[] subOrgIds = new String[num];
		  for (int i=0;i<num;i++) {
			  subOrgIds[i] = spogServer.createAccountWithCheck(mspOrg_id,spogServer.ReturnRandom("accoun1_msp1_yuefen_spogqa")+org_prefix, mspOrg_id, test);
			  userSpogServer.assignMspAccountAdmins(mspOrg_id, subOrgIds[i], msp_account_admin_id, csr_token);
			  System.out.println("org:"+subOrgIds[i]);
		  }

		  Response response = userSpogServer.getAllSubOrgsAssignedToMspAccountAdmin(msp_account_admin_id, token);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		  
		  //delete org
//		  spogServer.setToken(csr_token);
//		  for (int i=0;i<num;i++) {
//			  spogServer.DeleteOrganizationWithCheck(subOrgIds[i], test);
//		  }  
	  }
	  
	  @DataProvider(name = "UserInfo2")
	  public final Object[][] getUserInfo2() {
		  return new Object[][] { 
					  {direct_token}, 
					  {msp2_token},
					  {account_token}
			          }; 
		}
	  @Test (dataProvider = "UserInfo2")
	  public void getAccountsAssignWithMspAdminUsersFail(String token) {
		  System.out.println("getAccountsAssignWithMspAdminUsersFail");
		  test = rep.startTest("getAccountsAssignWithMspAdminUsersFail");
		  test.assignAuthor("Liu Yuefen");
		  
		  spogServer.setToken(csr_token);
		  System.out.println("token:"+csr_token);
          //create sub org
		  String subOrg = spogServer.createAccountWithCheck(mspOrg_id,spogServer.ReturnRandom("accoun1_msp1_yuefen_spogqa")+org_prefix, mspOrg_id, test);
		  userSpogServer.assignMspAccountAdmins(mspOrg_id, subOrg, msp_account_admin_id, csr_token);

		  Response response = userSpogServer.getAllSubOrgsAssignedToMspAccountAdmin(msp_account_admin_id, token);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS);
		  spogServer.checkErrorCode(response, "00100101");
		  //delete org
//		  spogServer.setToken(csr_token);
//          spogServer.DeleteOrganizationWithCheck(subOrg, test);
		  
	  }
	  
	  
	  @DataProvider(name = "UserInfo3")
	  public final Object[][] getUserInfo3() {
		  return new Object[][] { 
			          {direct_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS,"00200029"},
			          {msp_user_id, SpogConstants.INSUFFICIENT_PERMISSIONS,"00200029"},
//			          {msp_account_admin_id,SpogConstants.INSUFFICIENT_PERMISSIONS,"00200029"},
			          {account_user_id,SpogConstants.INSUFFICIENT_PERMISSIONS,"00200029"},
					  {UUID.randomUUID().toString(),SpogConstants.RESOURCE_NOT_EXIST,"00200007"}, 
					  {"",SpogConstants.REQUIRED_INFO_NOT_EXIST,"40000005"},
					  {null,SpogConstants.REQUIRED_INFO_NOT_EXIST,"40000005"}
			          }; 
		}
	  @Test (dataProvider = "UserInfo3")
	  public void getAccountsFail(String userID, int expectedStatusCode, String expectedErrorCode) {
		  System.out.println("getAccountsFail");
		  test = rep.startTest("getAccountsFail");
		  test.assignAuthor("Liu Yuefen");

		  Response response = userSpogServer.getAllSubOrgsAssignedToMspAccountAdmin(userID, csr_token);
		  spogServer.checkResponseStatus(response, expectedStatusCode);
		  spogServer.checkErrorCode(response, expectedErrorCode);
		  
	  }
	  
	  @Test
	  //deleted user or deleted org
	  public void getAccountFail2() {
		  System.out.println("getAccountsFail2");
		  test = rep.startTest("getAccountsFail2");
		  test.assignAuthor("Liu Yuefen");
		  
		  spogServer.setToken(csr_token);
		  //create msp account admin
		  String userId=spogServer.createUserAndCheck(spogServer.ReturnRandom("yuefen_msp_account_admin@spogqa.com"), sharePassword, "yuefen", "liu", "msp_account_admin", mspOrg_id, test);
		  //get sub org
		  Response response=userSpogServer.getAllSubOrgsAssignedToMspAccountAdmin(userId, csr_token);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		  //create sub org and assign
		  String subOrg=spogServer.createAccountWithCheck(mspOrg_id,spogServer.ReturnRandom("accoun1_msp1_yuefen_spogqa"), mspOrg_id, test);
		  response = userSpogServer.assignMspAccountAdmins(mspOrg_id, subOrg, userId, csr_token);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		  //get sub org
		  response=userSpogServer.getAllSubOrgsAssignedToMspAccountAdmin(userId, csr_token);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		  //destroy org
		  org4SPOGServer.setToken(csr_token);
		  org4SPOGServer.destroyOrganization(subOrg);
		  //spogServer.deleteMSPAccountWithCheck(mspOrg_id, subOrg);
		  //get sub org
		  response=userSpogServer.getAllSubOrgsAssignedToMspAccountAdmin(userId, csr_token);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		  //delete msp account admin
		  spogServer.CheckDeleteUserByIdStatus(userId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  //get sub org
		  response =userSpogServer.getAllSubOrgsAssignedToMspAccountAdmin(userId, csr_token);
		  spogServer.checkResponseStatus(response, SpogConstants.RESOURCE_NOT_EXIST);
		  spogServer.checkErrorCode(response, "00200007");
	  }
	  
	  @Test
	  public void getSubOrgFail401() {
		  System.out.println("getAccountsFail2");
		  test = rep.startTest("getAccountsFail2");
		  test.assignAuthor("Liu Yuefen");
		  
		  //get sub org
		  Response response =userSpogServer.getAllSubOrgsAssignedToMspAccountAdmin(msp_account_admin_id, "");
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
