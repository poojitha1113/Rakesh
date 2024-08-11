package api.users;
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
import InvokerServer.UserSpogServer;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;

public class DeleteUserByIdTest extends base.prepare.Is4Org {
	  private SPOGServer spogServer;
	  private UserSpogServer userSpogServer;
	  private Org4SPOGServer org4SpogServer;
	  private String csrOrg_id;
	  private String directOrg_id;
	  private String directOrg2_id;
	  private String mspOrg_id;
	  private String mspOrg2_id;
	  private String accountOrg_id;
	  private String accountOrg2_id;
	  private String csrReadOnlyUser = "liuyu05@arcserve.com";
	  private String sharePassword = "Caworld_2018";
	  //private String csr_email;
	  private String direct_email;
	  private String msp_email;
	  private String accountDirect_email;
	  private String msp2_email;
	  //private ExtentReports rep;
	  private ExtentTest test;
	  private Response response;
	  private String csrGlobalLoginUser;
	  private String csrGlobalLoginPassword;	
	  private String  org_prefix=this.getClass().getSimpleName();
	//add root msp, sub msp, accounts;
	  private String root_msp1_id;
	  private String root_msp2_id;
	  private String subMSP1_root1_id;
	  private String subMSP2_root1_id;
	  private String subMSP1_root2_id;
	  private String subMSP2_root2_id;
	  private String account_rootMSP1_id;
	  private String account2_rootMSP1_id;
	  private String account_rootMSP2_id;
	  private String account_subMSP1_root1_id;
	  private String account2_subMSP1_root1_id;
	  private String account_subMSP2_root1_id;
	  private String account_subMSP1_root2_id;
	  private String account_subMSP2_root2_id;
	  private String csrToken;
	  private String root_msp_email;
	  private String root_msp_account_admin_email;
	  private String root_msp_account_admin_userId;
	  private String sub_msp_email;
	  private String sub_msp_account_admin_email;
	  private String sub_msp_account_admin_userId;
	  private String accountDirect_rootMsp_email;
	  private String accountDirect_subMsp_email;
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
		  rep = ExtentManager.getInstance("DeleteUserByIdTest",logFolder);
		  test = rep.startTest("beforeclass");
		  spogServer = new SPOGServer(baseURI, port);
		  userSpogServer = new UserSpogServer(baseURI, port);
		  org4SpogServer = new Org4SPOGServer(baseURI, port);
		  spogServer.userLogin(userName, password); 
		  this.csrGlobalLoginUser = userName;
		  this.csrGlobalLoginPassword = password;
		  this.csrToken = spogServer.getJWTToken();
		  
		  csrOrg_id = spogServer.GetLoggedinUserOrganizationID();
		  directOrg_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_direct1_yuefen")+org_prefix,"direct",spogServer.ReturnRandom("spogqa_yuefen_direct1@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("direct_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  directOrg2_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_direct2_yuefen")+org_prefix,"direct",spogServer.ReturnRandom("spogqa_direct2_yuefen@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("direct_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  mspOrg_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_msp1_yuefen")+org_prefix,"msp",spogServer.ReturnRandom("spogqa_msp2@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("msp_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  mspOrg2_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_yuefen_msp2")+org_prefix,"msp",spogServer.ReturnRandom("spogqa_msp2@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("msp_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  accountOrg_id = spogServer.createAccountWithCheck(mspOrg_id, spogServer.ReturnRandom("account1_yuefen_delete_spogqa")+org_prefix, mspOrg_id, test);
		  accountOrg2_id = spogServer.createAccountWithCheck(mspOrg2_id, spogServer.ReturnRandom("account2_yuefen_delete_spogqa")+org_prefix, mspOrg2_id, test);
	
		  //create csr user
		  //this.csr_email = spogServer.ReturnRandom("direct_yuefen_update@spogqa.com");
		  //spogServer.createUserAndCheck(csr_email, sharePassword, "yuefen", "liu", "csr_admin", csrOrg_id, test);
		  //create direct user
		  this.direct_email = spogServer.ReturnRandom("direct_yuefen_update@spogqa.com");
		  spogServer.createUserAndCheck(direct_email, sharePassword, "yuefen", "liu", "direct_admin", directOrg_id, test);
		  //create msp1 user
		  this.msp_email = spogServer.ReturnRandom("msp1_yuefen_update@spogqa.com");
		  spogServer.createUserAndCheck(msp_email, sharePassword, "yuefen", "liu", "msp_admin", mspOrg_id, test);
		  
		  this.msp2_email = spogServer.ReturnRandom("msp2_yuefen_update@spogqa.com");
		  spogServer.createUserAndCheck(msp2_email, sharePassword, "yuefen", "liu", "msp_admin", mspOrg2_id, test);
		  //create account user
		  this.accountDirect_email = spogServer.ReturnRandom("account1_msp1_yuefen_update@spogqa.com");
		  spogServer.createUserAndCheck(accountDirect_email, sharePassword, "yuefen", "liu", "direct_admin", accountOrg_id, test);
		  
		//*************************************************************************************************************
			 //create root msp, sub msp, accounts
			  root_msp1_id=spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_update_yuefen_rootmsp1")+org_prefix,"msp",spogServer.ReturnRandom("spogqa_update_rootmsp1@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("direct_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
			  root_msp2_id=spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_update_yuefen_rootmsp2")+org_prefix,"msp",spogServer.ReturnRandom("spogqa_update_rootmsp2@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("direct_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
			  
			  System.out.println("convert to root msp starts");
			  org4SpogServer.setToken(csrToken);
			  org4SpogServer.convertToRootMSP(root_msp1_id);
			  org4SpogServer.convertToRootMSP(root_msp2_id);
			  
			  subMSP1_root1_id=org4SpogServer.createSubMSPAccountincc(spogServer.ReturnRandom("spogqa_update_yuefen_submsp1_rootmsp1")+org_prefix, root_msp1_id,spogServer.ReturnRandom("submsp1_rootmsp1_spogqa"),spogServer.ReturnRandom("liu_spogqa"),spogServer.ReturnRandom("spogqa_update_submsp1_rootmsp1@spogqa.com"), "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
			  subMSP2_root1_id=org4SpogServer.createSubMSPAccountincc(spogServer.ReturnRandom("spogqa_update_yuefen_submsp1_rootmsp1")+org_prefix, root_msp1_id,spogServer.ReturnRandom("submsp2_rootmsp1_spogqa"),spogServer.ReturnRandom("liu_spogqa"),spogServer.ReturnRandom("spogqa_update_submsp2_rootmsp1@spogqa.com"), "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
			  
			  subMSP1_root2_id=org4SpogServer.createSubMSPAccountincc(spogServer.ReturnRandom("spogqa_update_yuefen_submsp1_rootmsp2")+org_prefix, root_msp2_id,spogServer.ReturnRandom("submsp1_rootmsp2_spogqa"),spogServer.ReturnRandom("liu_spogqa"),spogServer.ReturnRandom("spogqa_update_submsp1_rootmsp2@spogqa.com"), "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test);
			  subMSP2_root2_id=org4SpogServer.createSubMSPAccountincc(spogServer.ReturnRandom("spogqa_update_yuefen_submsp2_rootmsp2")+org_prefix, root_msp2_id,spogServer.ReturnRandom("submsp2_rootmsp2_spogqa"),spogServer.ReturnRandom("liu_spogqa"),spogServer.ReturnRandom("spogqa_update_submsp2_rootmsp2@spogqa.com"), "91a9b48e-6ac6-4c47-8202-614b5cdcfe0c", test); 
			  
			  account_rootMSP1_id=spogServer.createAccountWithCheck(root_msp1_id,spogServer.ReturnRandom("accoun1_rootmsp1_yuefen_spogqa")+org_prefix, root_msp1_id, test);
			  account2_rootMSP1_id=spogServer.createAccountWithCheck(root_msp1_id,spogServer.ReturnRandom("accoun2_rootmsp1_yuefen_spogqa")+org_prefix, root_msp1_id, test);
			  
			  account_rootMSP2_id=spogServer.createAccountWithCheck(root_msp2_id,spogServer.ReturnRandom("accoun1_rootmsp2_yuefen_spogqa")+org_prefix, root_msp2_id, test);
			  
			  account_subMSP1_root1_id=spogServer.createAccountWithCheck(subMSP1_root1_id,spogServer.ReturnRandom("accoun1_submsp1_rootmsp1_yuefen_spogqa")+org_prefix, subMSP1_root1_id, test);
			  account2_subMSP1_root1_id=spogServer.createAccountWithCheck(subMSP1_root1_id,spogServer.ReturnRandom("accoun2_submsp1_rootmsp1_yuefen_spogqa")+org_prefix, subMSP1_root1_id, test);
			  
			  account_subMSP2_root1_id=spogServer.createAccountWithCheck(subMSP2_root1_id,spogServer.ReturnRandom("accoun1_submsp1_rootmsp1_yuefen_spogqa")+org_prefix, subMSP2_root1_id, test);
			  account_subMSP1_root2_id=spogServer.createAccountWithCheck(subMSP1_root2_id,spogServer.ReturnRandom("accoun1_submsp1_rootmsp1_yuefen_spogqa")+org_prefix, subMSP1_root2_id, test);
			  account_subMSP2_root2_id=spogServer.createAccountWithCheck(subMSP2_root2_id,spogServer.ReturnRandom("accoun1_submsp1_rootmsp1_yuefen_spogqa")+org_prefix, subMSP2_root2_id, test);
			  
			//create user under root msp related
				this.root_msp_email = spogServer.ReturnRandom("rootmsp_yuefen_update@spogqa.com");
				spogServer.createUserAndCheck(root_msp_email, sharePassword, "yuefen", "liu", "msp_admin", root_msp1_id, test);
				this.root_msp_account_admin_email = spogServer.ReturnRandom("rootmsp_account_admin_yuefen_update@spogqa.com");
				this.root_msp_account_admin_userId = spogServer.createUserAndCheck(root_msp_account_admin_email, sharePassword, "yuefen", "liu", "msp_account_admin", root_msp1_id, test);
				userSpogServer.assignMspAccountAdmins(root_msp1_id, account_rootMSP1_id, root_msp_account_admin_userId , csrToken);
				
				this.sub_msp_email = spogServer.ReturnRandom("submsp_yuefen_update@spogqa.com");
				spogServer.createUserAndCheck(sub_msp_email, sharePassword, "yuefen", "liu", "msp_admin", subMSP1_root1_id, test);
				this.sub_msp_account_admin_email = spogServer.ReturnRandom("submsp_yuefen_update@spogqa.com");
				this.sub_msp_account_admin_userId = spogServer.createUserAndCheck(sub_msp_account_admin_email, sharePassword, "yuefen", "liu", "msp_account_admin", subMSP1_root1_id, test);
				userSpogServer.assignMspAccountAdmins(subMSP1_root1_id, account_subMSP1_root1_id, sub_msp_account_admin_userId , csrToken);
				
				this.accountDirect_rootMsp_email = spogServer.ReturnRandom("accountDirect_rootMsp_yuefen_update@spogqa.com");
				spogServer.createUserAndCheck(accountDirect_rootMsp_email, sharePassword, "yuefen", "liu", "direct_admin", account_rootMSP1_id, test);
				
				this.accountDirect_subMsp_email = spogServer.ReturnRandom("accountDirect_subMsp_yuefen_update@spogqa.com");
				spogServer.createUserAndCheck(accountDirect_subMsp_email, sharePassword, "yuefen", "liu", "direct_admin", account_subMSP1_root1_id, test);
			//****************************************************************************************************************  
				
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
	  
	  @DataProvider(name = "userInfo50")
	  public final Object[][] getUserInfo50() {
		  return new Object[][] { 
				{root_msp_email, spogServer.ReturnRandom("account1_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
					"direct_admin",account_rootMSP1_id}, 
		        {root_msp_email, spogServer.ReturnRandom("account2_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
						"direct_admin", account2_rootMSP1_id}, 
		        {root_msp_account_admin_email, spogServer.ReturnRandom("account1_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
							"direct_admin", account_rootMSP1_id},
		
		        {sub_msp_email, spogServer.ReturnRandom("account1_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
								"direct_admin", account_subMSP1_root1_id},
		        {sub_msp_email, spogServer.ReturnRandom("account2_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
								"direct_admin", account2_subMSP1_root1_id}, 
		        {sub_msp_account_admin_email, spogServer.ReturnRandom("account1_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
									"direct_admin", account_subMSP1_root1_id},
			          }; 
		}
	  @Test(dataProvider = "userInfo50")
	  public void deleteUserForRootMspRelatedSuccess(String loginUser, String email, String password, String first_name, String last_name, 
			  String role_id, String organization_id){	 
		  System.out.println("deleteUserForRootMspRelatedSuccess");
		  test = rep.startTest("deleteUserForRootMspRelatedSuccess");
		  test.assignAuthor("Liu Yuefen");
		  
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN);
		
		  //create user
		  String user_id = spogServer.createUserAndCheck(email, password, first_name, last_name, role_id, organization_id, test);
		  
		  //login
		  spogServer.userLogin(loginUser, sharePassword, SpogConstants.SUCCESS_LOGIN);
        
          //delete user
          spogServer.CheckDeleteUserByIdStatus(user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	  }
	  
	  @DataProvider(name = "userInfo60")
	  public final Object[][] getUserInfo60() {
		  return new Object[][] { 
			  //user permission
		{root_msp_email, spogServer.ReturnRandom("submsp1_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
					"msp_admin", subMSP1_root1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" }, 
		{root_msp_email, spogServer.ReturnRandom("submsp2_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
					"msp_admin", subMSP2_root1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" }, 
		{root_msp_email, spogServer.ReturnRandom("submsp1_msp_account_admin_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
					"msp_account_admin", subMSP1_root1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" }, 
		{root_msp_email, spogServer.ReturnRandom("account_submsp1_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
						"direct_admin", account_subMSP1_root1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" },
		{root_msp_email, spogServer.ReturnRandom("account2_submsp1_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
							"direct_admin", account2_subMSP1_root1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" },
		
		{root_msp_account_admin_email, spogServer.ReturnRandom("submsp1_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
								"msp_admin", subMSP1_root1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" }, 
		{root_msp_account_admin_email, spogServer.ReturnRandom("submsp2_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
								"msp_admin", subMSP2_root1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" }, 
		{root_msp_account_admin_email, spogServer.ReturnRandom("submsp1_msp_account_admin_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
								"msp_account_admin", subMSP1_root1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" }, 
		{root_msp_account_admin_email, spogServer.ReturnRandom("account_submsp1_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
									"direct_admin", account_subMSP1_root1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" },
		{root_msp_account_admin_email, spogServer.ReturnRandom("account2_submsp1_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
										"direct_admin", account2_subMSP1_root1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" },
		
		
		{sub_msp_email, spogServer.ReturnRandom("submsp2_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
								"msp_admin", subMSP2_root1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" },
		{sub_msp_email, spogServer.ReturnRandom("account_submsp2_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
								"direct_admin", account_subMSP2_root1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101"}, 
		{sub_msp_account_admin_email, spogServer.ReturnRandom("account2_submsp1_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
									"direct_admin", account2_subMSP1_root1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" },
		{sub_msp_account_admin_email, spogServer.ReturnRandom("account_submsp2_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
										"direct_admin", account_subMSP2_root1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" },
		
		
		{accountDirect_rootMsp_email, spogServer.ReturnRandom("account2_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
											"direct_admin", account2_rootMSP1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" },
		{accountDirect_rootMsp_email, spogServer.ReturnRandom("account_submsp2_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
												"direct_admin", account_subMSP2_root1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" },
		
		{accountDirect_subMsp_email, spogServer.ReturnRandom("account_submsp2_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
													"direct_admin", account2_subMSP1_root1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" },
		{accountDirect_subMsp_email, spogServer.ReturnRandom("account_submsp2_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
														"direct_admin", account_subMSP2_root1_id, SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" },
		
		{msp_email, spogServer.ReturnRandom("rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
											"msp_admin", root_msp1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" }, 
		{msp_email, spogServer.ReturnRandom("account_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
												"direct_admin", account_rootMSP1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" }, 
		{msp_email, spogServer.ReturnRandom("submsp1_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
													"msp_admin", subMSP1_root1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" }, 
		{msp_email, spogServer.ReturnRandom("account_submsp1_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
														"direct_admin", account_subMSP1_root1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" }, 
		{direct_email, spogServer.ReturnRandom("rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
															"msp_admin", root_msp1_id,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" }, 
		{root_msp_email, spogServer.ReturnRandom("msp@spogqa.com"), sharePassword, "betty", "liu",
																"msp_admin", mspOrg_id,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" }, 
		{root_msp_email, spogServer.ReturnRandom("direct@spogqa.com"), sharePassword, "betty", "liu",
																	"direct_admin", directOrg_id,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" },
		};
		}
	  
	  @Test(dataProvider = "userInfo60")
	  public void deleteRootMspRelatedFailTest(String loginUser, String email, String password, String first_name, String last_name, 
			  String role_id, String organization_id, int expectedStatusCode, String expectedErrorCode ){	 
		  System.out.println("deleteRootMspRelatedFailTest");
		  test = rep.startTest("deleteRootMspRelatedFailTest");
		  test.assignAuthor("Liu Yuefen");
		  
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN);
		  String csrToken = spogServer.getJWTToken();
		  
		  //create user
		  String user_id = spogServer.createUserAndCheck(email, password, first_name, last_name, role_id, organization_id, test);
		  
		  //login
		  spogServer.userLogin(loginUser, sharePassword, SpogConstants.SUCCESS_LOGIN);
		  
          //delete user
		  Response response = spogServer.DeleteUserById(user_id, test);
          spogServer.checkResponseStatus(response, expectedStatusCode);
          spogServer.checkErrorCode(response, expectedErrorCode);
          
          //delete user
          spogServer.setToken(csrToken);
          spogServer.CheckDeleteUserByIdStatus(user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	  }
	  
	  
	  @DataProvider(name = "userInfo3")
	  public final Object[][] getUserInfo3() {
		  return new Object[][]{ 
			  //{spogServer.ReturnRandom("yuefen_delete_csr_admin_yuefen@spogqa.com"), spogServer.ReturnRandom("aaAA88"), spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "csr_admin", csrOrg_id,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101"}, 
			  //{spogServer.ReturnRandom("yuefen_delete_csr_read_only_yuefen@spogqa.com"), spogServer.ReturnRandom("aaAA88"), spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "csr_read_only", csrOrg_id,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101"}, 
	          { spogServer.ReturnRandom("yuefen_delete_direct@spogqa.com"), spogServer.ReturnRandom("abyYc3"), spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "direct_admin", directOrg_id,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101"},
	          { spogServer.ReturnRandom("yuefen_delete_msp_admin@spogqa.com"), spogServer.ReturnRandom("abyYc3"), spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "msp_admin", mspOrg_id,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101"},
	          { spogServer.ReturnRandom("yuefen_delete_msp_account_admin@spogqa.com"), spogServer.ReturnRandom("abyYc3"), spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "msp_account_admin", mspOrg_id,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101"},
	          { spogServer.ReturnRandom("yuefen_delete_account_direct_admin@spogqa.com"), spogServer.ReturnRandom("abycdYc3"), spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "direct_admin", accountOrg_id,SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101"}
	          };
		}
		
	  
	  @Test(dataProvider = "userInfo3")
	  public void csrReadOnlyDeleteUser(String email, String password, String first_name, String last_name, String role_id, String organization_id, int expectedStatusCode, String expectedErrorCode){	 
		  System.out.println("csrReadOnlyDeleteUser");
		  test = rep.startTest("csrReadOnlyDeleteUser");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login with csr admin
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN);
		  
		  //create and update direct/msp/account user
		  String user_id = spogServer.createUserAndCheck(email, password, first_name, last_name, role_id, organization_id, test);
		  
		  //login with new user
		  spogServer.userLogin(csrReadOnlyUser, sharePassword, SpogConstants.SUCCESS_LOGIN);
		  
		  //delete user
          Response response = spogServer.DeleteUserById(user_id, test);
          spogServer.checkResponseStatus(response, expectedStatusCode);
          spogServer.checkErrorCode(response, expectedErrorCode);	  
	  }
	  
	  
	  @DataProvider(name = "userInfo1")
	  public final Object[][] getUserInfo1() {
		  return new Object[][] { 
					  {this.csrGlobalLoginUser,spogServer.ReturnRandom("update_msp@spogqa.com"), this.csrGlobalLoginPassword, "betty", "liu", "msp_account_admin", mspOrg_id}, 
					  {msp_email,spogServer.ReturnRandom("update_msp@spogqa.com"), sharePassword, "betty", "liu", "msp_account_admin", mspOrg_id}, 
			          }; 
		}
	  @Test(dataProvider = "userInfo1")
	  public void deleteMSPAdminsSuccess(String loginUser, String email, String password, String first_name, String last_name, 
			  String role_id, String organization_id){	 
		  System.out.println("deleteMSPAdminsSuccess");
		  test = rep.startTest("deleteMSPAdminsSuccess");
		  test.assignAuthor("Liu Yuefen");
		  
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN);
		
		  //create user
		  String user_id = spogServer.createUserAndCheck(email, password, first_name, last_name, role_id, organization_id, test);
		  
		  //login
		  spogServer.userLogin(loginUser, password, SpogConstants.SUCCESS_LOGIN);
        
          //delete user
          spogServer.CheckDeleteUserByIdStatus(user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	  }
	  
	  @DataProvider(name = "userInfo2")
	  public final Object[][] getUserInfo2() {
		  return new Object[][] { 
			  //user permission
					  {direct_email,spogServer.ReturnRandom("update_msp@spogqa.com"), sharePassword, "betty", "liu", "msp_account_admin", mspOrg_id, "yuefen_new", "",SpogConstants.INSUFFICIENT_PERMISSIONS, "00100101"}, 
					  {accountDirect_email,spogServer.ReturnRandom("update_msp@spogqa.com"), sharePassword, "betty", "liu", "msp_account_admin", mspOrg_id,"yuefen_new", "",SpogConstants.INSUFFICIENT_PERMISSIONS, "00100101"}, 
					  {msp2_email,spogServer.ReturnRandom("update_msp@spogqa.com"), sharePassword, "betty", "liu", "msp_account_admin", mspOrg_id,"yuefen_new","",SpogConstants.INSUFFICIENT_PERMISSIONS, "00100101"}, 
			          }; 
		}
	  
	  @Test(dataProvider = "userInfo2")
	  public void deleteMSPAdminsFail(String loginUser, String email, String password, String first_name, String last_name, 
			  String role_id, String organization_id, String newFirstName, String newRole, int expectedStatusCode, String expectedErrorCode ){	 
		  System.out.println("deleteMSPAdminsFail");
		  test = rep.startTest("deleteMSPAdminsFail");
		  test.assignAuthor("Liu Yuefen");
		  
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN);
		  String csrToken = spogServer.getJWTToken();
		  
		  //create user
		  String user_id = spogServer.createUserAndCheck(email, password, first_name, last_name, role_id, organization_id, test);
		  
		  //login
		  spogServer.userLogin(loginUser, sharePassword, SpogConstants.SUCCESS_LOGIN);
		  
          //delete user
		  Response response = spogServer.DeleteUserById(user_id, test);
          spogServer.checkResponseStatus(response, expectedStatusCode);
          spogServer.checkErrorCode(response, expectedErrorCode);
          
          //delete user
          spogServer.setToken(csrToken);
          spogServer.CheckDeleteUserByIdStatus(user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	  }
	  
	  @Test
	  public void mspAccountAdminDeleteMAccountUser(){	 
		  System.out.println("mspAccountAdminDeleteMAccountUser");
		  test = rep.startTest("mspAccountAdminDeleteMAccountUser");
		  test.assignAuthor("Liu Yuefen");
		  
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN);
		  String csr_token =spogServer.getJWTToken();
		  //create user
		  String email =spogServer.ReturnRandom("msp_account_admin@spogqa.com");
		  String admin_id = spogServer.createUserAndCheck(email, sharePassword, "yuefen", "liu", "msp_account_admin", mspOrg_id, test);
		  //assign
		  String[] userIds = new String[1];
		  userIds[0] =admin_id;
		  userSpogServer.assignMspAccountAdmins(mspOrg_id,accountOrg_id, userIds, csr_token);
		  //login
		  spogServer.userLogin(email, sharePassword, SpogConstants.SUCCESS_LOGIN);
		  String admin_token = spogServer.getJWTToken();
          //create account user
		  String userId = spogServer.createUserAndCheck(spogServer.ReturnRandom("account_yuefen@spogqa.com"), sharePassword, "yuefen", "liu", "direct_admin", accountOrg_id, test);
          //delete user
          spogServer.CheckDeleteUserByIdStatus(userId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
          
          //unassign
          userSpogServer.unAssignMspAccountAdmins(mspOrg_id,accountOrg_id, userIds, csr_token);
          spogServer.setToken(csr_token);
          String userId2 = spogServer.createUserAndCheck(spogServer.ReturnRandom("account_yuefen_user@spogqa.com"), sharePassword, "yuefen", "liu", "direct_admin", accountOrg_id, test);
          spogServer.setToken(admin_token);
          Response response = spogServer.DeleteUserById(userId2, test);
          spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS);
          spogServer.checkErrorCode(response, "00100101");
	  }
	  
	  @Test
	  public void csrDeleteUser(){	 
		  System.out.println("csrDeleteUser");
		  test = rep.startTest("csrDeleteUser");
		  test.assignAuthor("Liu Yuefen");
		  
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  //create direct admin
		  String email = spogServer.ReturnRandom("direct1_yuefen@spogqa.com");
		  String password = spogServer.ReturnRandom("aaAA88");
		  String user_id1 = spogServer.createUserAndCheck(email, password, spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "direct_admin", directOrg_id, test);
		  //delete direct admin
		  spogServer.CheckDeleteUserByIdStatus(user_id1, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  Response response = spogServer.login(email, password);
		  spogServer.checkResponseStatus(response, SpogConstants.NOT_LOGGED_IN, test);
		  spogServer.checkErrorCode(response, "00200023");
		  
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  //create msp admin
		  String email2 = spogServer.ReturnRandom("msp1_yuefen@spogqa.com");
		  String password2 = spogServer.ReturnRandom("aaAA88");
		  String user_id2 = spogServer.createUserAndCheck(email2, password2, spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "msp_admin", mspOrg_id, test);
		  //delete msp admin
		  spogServer.CheckDeleteUserByIdStatus(user_id2, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  response = spogServer.login(email2, password2);
		  spogServer.checkResponseStatus(response, SpogConstants.NOT_LOGGED_IN, test);
		  spogServer.checkErrorCode(response, "00200023");
		  
		  
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  //create csr admin
		  String email3 = spogServer.ReturnRandom("csr2_yuefen@spogqa.com");
		  String password3 = spogServer.ReturnRandom("aaAA9988");
		  String user_id3 = spogServer.createUserAndCheck(email3, password3, spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "csr_admin", "", test);
		  //delete csr admin
		  spogServer.CheckDeleteUserByIdStatus(user_id3, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  response = spogServer.login(email3, password3);
		  spogServer.checkResponseStatus(response, SpogConstants.NOT_LOGGED_IN, test);
		  spogServer.checkErrorCode(response, "00200023");
		  
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  //create account
		  String email4 = spogServer.ReturnRandom("account_yuefen@spogqa.com");
		  String password4 = spogServer.ReturnRandom("aaAA88");
		  String user_id4 = spogServer.createUserAndCheck(email4, password4, spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "direct_admin", accountOrg_id, test);
		  //delete account
		  spogServer.CheckDeleteUserByIdStatus(user_id4, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  response = spogServer.login(email4, password4);
		  spogServer.checkResponseStatus(response, SpogConstants.NOT_LOGGED_IN, test);
		  spogServer.checkErrorCode(response, "00200023");
		  
	  }
	  
	  @Test
	  public void mspDeleteUser(){	 
		  System.out.println("mspDeleteUser");
		  test = rep.startTest("mspDeleteUser");
		  test.assignAuthor("Liu Yuefen");
		  
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  //create msp admin1
		  String email1 = spogServer.ReturnRandom("msp1_yuefen@spogqa.com");
		  String password1 = spogServer.ReturnRandom("aaaAAA33");
		  spogServer.createUserAndCheck(email1, password1, "msp1", "liu", "msp_admin", mspOrg_id, test);
		  //create msp admin2 under the same org
		  String email2 = spogServer.ReturnRandom("msp2_yuefen@spogqa.com");
		  String password2 = spogServer.ReturnRandom("aaaAAA33");
		  String user_id = spogServer.createUserAndCheck(email2, password2, spogServer.ReturnRandom("msp2"), spogServer.ReturnRandom("msp2"), "msp_admin", mspOrg_id, test);
		  //login with msp admin1
		  spogServer.userLogin(email1, password1, SpogConstants.SUCCESS_LOGIN, test);
		  //delete msp admin2
		  spogServer.CheckDeleteUserByIdStatus(user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  //login with deleted user fail with msp2
		  Response response = spogServer.login(email2, password2);
		  spogServer.checkResponseStatus(response, SpogConstants.NOT_LOGGED_IN, test);
		  spogServer.checkErrorCode(response, "00200023");
		  
		  //msp admin1 try to delete msp2 admin
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  String msp2_user_id = spogServer.createUserAndCheck(spogServer.ReturnRandom("msp2_yuefen_delete@spogqa.com"), spogServer.ReturnRandom("aaBBccDD12"), spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "msp_admin", mspOrg2_id, test);
		  //login with msp1
		  spogServer.userLogin(email1, password1, SpogConstants.SUCCESS_LOGIN, test);
		  response = spogServer.DeleteUserById(msp2_user_id, test);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS,test);
		  spogServer.checkErrorCode(response, "00100101");
		  
		  
		  //msp admin try to delete csr admin
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  String csr_user_id = spogServer.createUserAndCheck(spogServer.ReturnRandom("csr_yuefen_delete@spogqa.com"), spogServer.ReturnRandom("aaBBccDD12"), spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "csr_admin", "", test);
		  //login with msp1
		  spogServer.userLogin(email1, password1, SpogConstants.SUCCESS_LOGIN, test);
		  response = spogServer.DeleteUserById(csr_user_id, test);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
		  spogServer.checkErrorCode(response, "00100101");
		  
		  //msp admin1 try to delete direct admin
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  String direct_user_id = spogServer.createUserAndCheck(spogServer.ReturnRandom("direct_yuefen_delete@spogqa.com"), spogServer.ReturnRandom("aaBBccDD12"), spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "direct_admin", directOrg_id, test);
		  //login with msp1
		  spogServer.userLogin(email1, password1, SpogConstants.SUCCESS_LOGIN, test);
		  response = spogServer.DeleteUserById(direct_user_id, test);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS,test);
		  spogServer.checkErrorCode(response, "00100101");
		
		  
		  //msp admin1 try to delete msp1's account
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  String account_msp1_user_id = spogServer.createUserAndCheck(spogServer.ReturnRandom("account_msp1_yuefen_delete@spogqa.com"), spogServer.ReturnRandom("aaBBccDD12"), spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "direct_admin", accountOrg_id, test);
		  //login with msp1
		  spogServer.userLogin(email1, password1, SpogConstants.SUCCESS_LOGIN, test);
		  spogServer.CheckDeleteUserByIdStatus(account_msp1_user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  
		  
		  //msp admin1 try to delete msp2's account
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  String account_msp2_user_id = spogServer.createUserAndCheck(spogServer.ReturnRandom("account_msp2_yuefen_delete@spogqa.com"), spogServer.ReturnRandom("aaBBccDD12"), spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "direct_admin", accountOrg2_id, test);
		  //login with msp1
		  spogServer.userLogin(email1, password1, SpogConstants.SUCCESS_LOGIN, test);
		  response = spogServer.DeleteUserById(account_msp2_user_id, test);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS,test);
		  spogServer.checkErrorCode(response, "00100101");
		  
	  }
	  
	  @Test
	  public void direcDeleteUser(){	 
		  System.out.println("directDeleteUser");
		  test = rep.startTest("directDeleteUser");
		  test.assignAuthor("Liu Yuefen");
		  
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  //direct admin1 try to delete direct admin2 under the same org
		  //create direct admin 1
		  String email1 = spogServer.ReturnRandom("direct111_yuefen@spogqa.com");
		  String password1 = spogServer.ReturnRandom("aaAAcc11");
		  spogServer.createUserAndCheck(email1, password1, "direct1", "liu", "direct_admin", directOrg_id, test);
		  //create direct2
		  String email2 = spogServer.ReturnRandom("direct222_yuefen@spogqa.com");
		  String password2 = spogServer.ReturnRandom("sfsFFF22");
	      String user_id = spogServer.createUserAndCheck(email2, password2,spogServer.ReturnRandom("first2_yuefen"),spogServer.ReturnRandom("liu2_direct"),"direct_admin", directOrg_id, test);
          //login with direct1
		  spogServer.userLogin(email1, password1, SpogConstants.SUCCESS_LOGIN);
		  //delete direct2
		  spogServer.CheckDeleteUserByIdStatus(user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  Response response = spogServer.login(email2, password2);
		  spogServer.checkResponseStatus(response, SpogConstants.NOT_LOGGED_IN, test);
		  spogServer.checkErrorCode(response, "00200023");
		  
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  //direct admin1 try to delete direct admin2 under the different org
		  String email3 = spogServer.ReturnRandom("direct333_yuefen@spogqa.com");
		  String password3 = spogServer.ReturnRandom("sfsFFF22");
	      String direct2_user_id = spogServer.createUserAndCheck(email3, password3,spogServer.ReturnRandom("first2_yuefen"),spogServer.ReturnRandom("liu2_direct"),"direct_admin", directOrg2_id, test);
          //login with direct1
		  spogServer.userLogin(email1, password1, SpogConstants.SUCCESS_LOGIN);
		  //delete admin under direct2
		  response = spogServer.DeleteUserById(direct2_user_id, test);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS,test);
		  spogServer.checkErrorCode(response, "00100101");

		  
		  //direct admin1 try to delete csr
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  String csr_user_id = spogServer.createUserAndCheck(spogServer.ReturnRandom("csr2_yuefen_delete@spogqa.com"), spogServer.ReturnRandom("aaBBccDD12"), spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "csr_admin", "", test);
		  //login with direct1
		  spogServer.userLogin(email1, password1, SpogConstants.SUCCESS_LOGIN);
		  //delete csr
		  response = spogServer.DeleteUserById(csr_user_id, test);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
		  spogServer.checkErrorCode(response, "00100101");
		 
		  
		  //direct admin1 try to delete msp
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  String msp_user_id = spogServer.createUserAndCheck(spogServer.ReturnRandom("msp2_yuefen_delete@spogqa.com"), spogServer.ReturnRandom("aaBBccDD12"), spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "msp_admin", mspOrg_id, test);
		  //login with direct1
		  spogServer.userLogin(email1, password1, SpogConstants.SUCCESS_LOGIN);
		  //delete msp
		  response = spogServer.DeleteUserById(msp_user_id, test);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
		  spogServer.checkErrorCode(response, "00100101");

		  
		  //direct admin1 try to delete msp account admin
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  String account_user_id = spogServer.createUserAndCheck(spogServer.ReturnRandom("account_msp1_yuefen_delete@spogqa.com"), spogServer.ReturnRandom("aaBBccDD12"), spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "direct_admin", accountOrg_id, test);
		  //login with direct1
		  spogServer.userLogin(email1, password1, SpogConstants.SUCCESS_LOGIN);
		  //delete account
		  response = spogServer.DeleteUserById(account_user_id, test);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS,test);
		  spogServer.checkErrorCode(response, "00100101");

	  }
	  
	  @Test
	  public void accountDeleteUser(){	 
		  System.out.println("accountDeleteUser");
		  test = rep.startTest("accountDeleteUser");
		  test.assignAuthor("Liu Yuefen");
		  
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  //account1's user try to delete account2's user
		  //create account1's user
		  String email1 = spogServer.ReturnRandom("account1_msp1_yuefen@spogqa.com");
		  String password1 = spogServer.ReturnRandom("aaAAcc11");
		  spogServer.createUserAndCheck(email1, password1, "account1", "liu", "direct_admin", accountOrg_id, test);
		  //create account2's user
		  String email2 = spogServer.ReturnRandom("account222_msp2_yuefen@spogqa.com");
		  String password2 = spogServer.ReturnRandom("sfsFFF22");
	      String user_id = spogServer.createUserAndCheck(email2, password2,spogServer.ReturnRandom("first2_yuefen"),spogServer.ReturnRandom("liu2_direct"),"direct_admin", accountOrg2_id, test);
          //login with account1's user
		  spogServer.userLogin(email1, password1, SpogConstants.SUCCESS_LOGIN);
		  //delete account2's user
		  Response response = spogServer.DeleteUserById(user_id, test);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS,test);
		  spogServer.checkErrorCode(response, "00100101");
		  
		  
		  //account1's user try to delete account1's user
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  String user2_id = spogServer.createUserAndCheck(spogServer.ReturnRandom("account2_msp1_yuefen@spogqa.com"), spogServer.ReturnRandom("aaBBccDD11"),spogServer.ReturnRandom("first2_yuefen"),spogServer.ReturnRandom("liu2_direct"),"direct_admin", accountOrg_id, test);
          //login with account1's user1
		  spogServer.userLogin(email1, password1, SpogConstants.SUCCESS_LOGIN);
		  //delete account1's another user2
		  spogServer.CheckDeleteUserByIdStatus(user2_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);

		  //account's user try to delete csr
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  String csr_user_id = spogServer.createUserAndCheck(spogServer.ReturnRandom("csr22_yuefen_delete@spogqa.com"), spogServer.ReturnRandom("aaBBccDD12"), spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "csr_admin", "", test);
		  //login with account1's user
		  spogServer.userLogin(email1, password1, SpogConstants.SUCCESS_LOGIN);
		  //delete csr
		  response = spogServer.DeleteUserById(csr_user_id, test);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS,test);
		  spogServer.checkErrorCode(response, "00100101");

		  
		  //account's user try to delete msp
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  String msp_user_id = spogServer.createUserAndCheck(spogServer.ReturnRandom("msp2222_yuefen_delete@spogqa.com"), spogServer.ReturnRandom("aaBBccDD12"), spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "msp_admin", mspOrg_id, test);
		  //login with direct1
		  spogServer.userLogin(email1, password1, SpogConstants.SUCCESS_LOGIN);
		  //delete msp
		  response = spogServer.DeleteUserById(msp_user_id, test);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS,test);
		  spogServer.checkErrorCode(response, "00100101");
		  
		  //account's user try to delete direct account
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  String direct_user_id = spogServer.createUserAndCheck(spogServer.ReturnRandom("direct2222_yuefen_delete@spogqa.com"), spogServer.ReturnRandom("aaBBccDD12"), spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "direct_admin", directOrg_id, test);
		  //login with direct1
		  spogServer.userLogin(email1, password1, SpogConstants.SUCCESS_LOGIN);
		  //delete direct
		  response = spogServer.DeleteUserById(direct_user_id, test);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS,test);
		  spogServer.checkErrorCode(response, "00100101");
	  }
	  
	  @Test
	  public void csrDeleteItself() {
		  System.out.println("csrDeleteItself");
		  test = rep.startTest("csrDeleteItself");
		  test.assignAuthor("Liu Yuefen");
	
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);

		  //create csr admin
          String email = spogServer.ReturnRandom("csr_yuefen@spogqa.com");
          String password = spogServer.ReturnRandom("adfDDD222");
          String user_id = spogServer.createUserAndCheck(email, password, spogServer.ReturnRandom("firstname_yuefen"), spogServer.ReturnRandom("lastname_liu"), "csr_admin", "", test);
		  
          spogServer.userLogin(email, password, SpogConstants.SUCCESS_LOGIN);
          Response response = spogServer.DeleteUserById(user_id, test);
		  spogServer.checkResponseStatus(response, SpogConstants.NOT_ALLOWED_ON_RESOURCE, test);
		  spogServer.checkErrorCode(response, "00200008");
		  
	  }
	  
	  @Test
	  public void mspDeleteItself() {
		  System.out.println("mspDeleteItself");
		  test = rep.startTest("mspDeleteItself");
		  test.assignAuthor("Liu Yuefen");
		  
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);

		  //create msp user
		  String email = spogServer.ReturnRandom("deletemsp_yuefen@spogqa.com");
		  String password = spogServer.ReturnRandom("sssSSSS222");
          String user_id = spogServer.createUserAndCheck(email, password, spogServer.ReturnRandom("delete"), spogServer.ReturnRandom("liu"), "msp_admin", mspOrg_id, test);
		  spogServer.userLogin(email, password, SpogConstants.SUCCESS_LOGIN);
          Response response = spogServer.DeleteUserById(user_id, test);
		  spogServer.checkResponseStatus(response, SpogConstants.NOT_ALLOWED_ON_RESOURCE, test);
		  spogServer.checkErrorCode(response, "00200008"); 
	  }
	  
	  @Test
	  public void directDeleteItself() {
		  System.out.println("directDeleteItself");
		  test = rep.startTest("directDeleteItself");
		  test.assignAuthor("Liu Yuefen");
		  
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);

		  //create direct admin
		  String email = spogServer.ReturnRandom("directDeleteItself_yuefen@spogqa.com");
		  String password = spogServer.ReturnRandom("avvVV22");
		  String user_id = spogServer.createUserAndCheck(email, password, spogServer.ReturnRandom("direct"), spogServer.ReturnRandom("yuefen"), "direct_admin", directOrg_id, test);
		  spogServer.userLogin(email, password, SpogConstants.SUCCESS_LOGIN);
          Response response = spogServer.DeleteUserById(user_id, test);
		  spogServer.checkResponseStatus(response, SpogConstants.NOT_ALLOWED_ON_RESOURCE, test);
		  spogServer.checkErrorCode(response, "00200008");
	  }
	  
	  @Test
	  public void accountDeleteItself() {
		  System.out.println("accountDeleteItself");
		  test = rep.startTest("accountDeleteItself");
		  test.assignAuthor("Liu Yuefen");
		  
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);

		  //create account admin
		  String email = spogServer.ReturnRandom("accountDeleteItself_yuefen@spogqa.com");
		  String password = spogServer.ReturnRandom("avvVV22");
		  String user_id = spogServer.createUserAndCheck(email, password, spogServer.ReturnRandom("direct"), spogServer.ReturnRandom("yuefen"), "direct_admin", accountOrg_id, test);
		  spogServer.userLogin(email, password, SpogConstants.SUCCESS_LOGIN);
          Response response = spogServer.DeleteUserById(user_id, test);
		  spogServer.checkResponseStatus(response, SpogConstants.NOT_ALLOWED_ON_RESOURCE, test);
		  spogServer.checkErrorCode(response, "00200008"); 
	  }
	  
	  @Test
	  public void deleteIdNotExistOrInvalid() {
		  System.out.println("deleteIdNotExistOrInvalid");
		  test = rep.startTest("deleteIdNotExistOrInvalid");
		  test.assignAuthor("Liu Yuefen");
		  
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  //user_id = ""
		  Response response = spogServer.DeleteUserById("", test);
		  spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test); 
		  spogServer.checkErrorCode(response, "40000001");
		  
		  //user_id = null
		  response = spogServer.DeleteUserById(null, test);
		  spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test); 
		  spogServer.checkErrorCode(response, "40000005");
		  
		  //user_id = invalid
		  response = spogServer.DeleteUserById("123456789", test);
		  spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test); 
		  spogServer.checkErrorCode(response, "40000005");
		
		  //user_id = 00000000-0000-0000-0000-000000000000
		  response = spogServer.DeleteUserById("00000000-0000-0000-0000-000000000000", test);
		  spogServer.checkResponseStatus(response, SpogConstants.RESOURCE_NOT_EXIST, test); 
		  spogServer.checkErrorCode(response, "00200007");
		  
		  //create user
		  String user_id = spogServer.createUserAndCheck(spogServer.ReturnRandom("direct_yuefen_idHasProblem@spogqa.com"), spogServer.ReturnRandom("aaAA33GG"), spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "direct_admin", directOrg_id, test);
		  //delete it in first time
		  spogServer.CheckDeleteUserByIdStatus(user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  //delete it in second time
		  response = spogServer.DeleteUserById(user_id, test);
		  spogServer.checkResponseStatus(response, SpogConstants.RESOURCE_NOT_EXIST, test); 
		  spogServer.checkErrorCode(response, "00200007");
		  
	  }
	  
	  
	  @Test
	  public void deleteFailReturn401() {
		  System.out.println("deleteFailReturn401");
		  test = rep.startTest("deleteFailReturn401");
		  test.assignAuthor("Liu Yuefen");
		  
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  String user_id = spogServer.createUserAndCheck(spogServer.ReturnRandom("direct_yuefen@spogqa.com"), spogServer.ReturnRandom("aaAA33GG"), spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "direct_admin", directOrg_id, test);
		  
		  spogServer.setToken("");
		  Response response = spogServer.DeleteUserById(user_id, test);
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
