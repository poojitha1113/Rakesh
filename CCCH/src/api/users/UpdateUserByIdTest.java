package api.users;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import Constants.SpogConstants;
import Constants.SpogMessageCode;
import InvokerServer.SPOGServer;
import InvokerServer.Org4SPOGServer;
import InvokerServer.UserSpogServer;
import bsh.org.objectweb.asm.Constants;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import groovyjarjarantlr.PreservingFileWriter;
import invoker.SPOGInvoker;
import io.restassured.response.Response;

import static org.hamcrest.Matchers.equalTo;
import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
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

public class UpdateUserByIdTest extends base.prepare.Is4Org {
	private SPOGServer spogServer;
	private UserSpogServer userSpogServer;
	private Org4SPOGServer org4SpogServer;
	private String csrOrg_id;
	private String directOrg_id;
	private String directOrg2_id;
	private String mspOrg_id;
	private String mspOrg2_id;
	private String account_id;
	private String account2_id;
	// private ExtentReports rep;
	private ExtentTest test;
	private Response response;
	private String csrGlobalLoginUser;
	private String csrGlobalLoginPassword;
	private String csrReadOnlyUser = "liuyu05@arcserve.com";
	private String sharePassword = "Caworld_2018";
	//private String csr_email;
	private String direct_email;
	private String msp_email;
	private String msp2_email;
	private String accountDirect_email;
	private String org_prefix = this.getClass().getSimpleName();
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
	// this is for update portal, each testng class is taken as BQ set
	// private SQLServerDb bqdb1;
	// public int Nooftest;
	// private long creationTime;
	// private String BQName=null;
	// private String runningMachine;
	// private testcasescount count1;
	// private String buildVersion;
	// end
	private String last_name;

	@BeforeClass
	@Parameters({ "baseURI", "port", "csrAdminUserName", "csrAdminPassword", "logFolder", "runningMachine",
			"buildVersion" })
	public void beforeClass(String baseURI, String port, String userName, String password, String logFolder,
			String runningMachine, String buildVersion) {
		rep = ExtentManager.getInstance("UpdateUserByIdTest", logFolder);
		test = rep.startTest("beforeClass");
		spogServer = new SPOGServer(baseURI, port);
		userSpogServer = new UserSpogServer(baseURI, port);
		org4SpogServer = new Org4SPOGServer(baseURI, port);
		spogServer.userLogin(userName, password);
		this.csrGlobalLoginUser = userName;
		this.csrGlobalLoginPassword = password;
		this.csrToken = spogServer.getJWTToken();
		
		
		csrOrg_id = spogServer.GetLoggedinUserOrganizationID();
		directOrg_id = spogServer.CreateOrganizationWithCheck(
				spogServer.ReturnRandom("spogqa_update_direct1_yuefen") + org_prefix, "direct",
				spogServer.ReturnRandom("spogqa_direct1_update@spogqa.com"), spogServer.ReturnRandom("aaQQdsf11"),
				spogServer.ReturnRandom("direct_spogqa"), spogServer.ReturnRandom("liu_spogqa"));
		directOrg2_id = spogServer.CreateOrganizationWithCheck(
				spogServer.ReturnRandom("spogqa_update_direct2_yuefen") + org_prefix, "direct",
				spogServer.ReturnRandom("spogqa_direct2_update@spogqa.com"), spogServer.ReturnRandom("aaQQdsf11"),
				spogServer.ReturnRandom("direct_spogqa"), spogServer.ReturnRandom("liu_spogqa"));
		mspOrg_id = spogServer.CreateOrganizationWithCheck(
				spogServer.ReturnRandom("spogqa_update_yuefen_msp1") + org_prefix, "msp",
				spogServer.ReturnRandom("spogqa_update_msp1@spogqa.com"), spogServer.ReturnRandom("aaQQdsf11"),
				spogServer.ReturnRandom("direct_spogqa"), spogServer.ReturnRandom("liu_spogqa"));
		mspOrg2_id = spogServer.CreateOrganizationWithCheck(
				spogServer.ReturnRandom("spogqa_update_yuefen_msp2") + org_prefix, "msp",
				spogServer.ReturnRandom("spogqa_update_msp2@spogqa.com"), spogServer.ReturnRandom("aaQQdsf11"),
				spogServer.ReturnRandom("direct_spogqa"), spogServer.ReturnRandom("liu_spogqa"));
		account_id = spogServer.createAccountWithCheck(mspOrg_id,
				spogServer.ReturnRandom("accoun1_msp1_yuefen_spogqa") + org_prefix, mspOrg_id, test);
		account2_id = spogServer.createAccountWithCheck(mspOrg2_id,
				spogServer.ReturnRandom("accoun2_msp2_yuefen_spogqa") + org_prefix, mspOrg2_id, test);
		
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
		  
		// create csr user
		//this.csr_email = spogServer.ReturnRandom("direct_yuefen_update@spogqa.com");
		//spogServer.createUserAndCheck(csr_email, sharePassword, "yuefen", "liu", "csr_admin", csrOrg_id, test);
		// create direct user
		this.direct_email = spogServer.ReturnRandom("direct_yuefen_update@spogqa.com");
		spogServer.createUserAndCheck(direct_email, sharePassword, "yuefen", "liu", "direct_admin", directOrg_id, test);
		// create msp1 user
		this.msp_email = spogServer.ReturnRandom("msp1_yuefen_update@spogqa.com");
		spogServer.createUserAndCheck(msp_email, sharePassword, "yuefen", "liu", "msp_admin", mspOrg_id, test);
		this.msp2_email = spogServer.ReturnRandom("msp2_yuefen_update@spogqa.com");
		spogServer.createUserAndCheck(msp2_email, sharePassword, "yuefen", "liu", "msp_admin", mspOrg2_id, test);
		// create account user
		this.accountDirect_email = spogServer.ReturnRandom("account1_msp1_yuefen_update@spogqa.com");
		spogServer.createUserAndCheck(accountDirect_email, sharePassword, "yuefen", "liu", "direct_admin", account_id,
				test);
		
		
		// this is for update portal
		this.BQName = this.getClass().getSimpleName();
		String author = "Yuefen.liu";
		this.runningMachine = runningMachine;
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy/MM/dd");
		java.util.Date date = new java.util.Date();
		this.buildVersion = buildVersion + "_" + dateFormater.format(date);
		Nooftest = 0;
		bqdb1 = new SQLServerDb();
		count1 = new testcasescount();
		if (count1.isstarttimehit() == 0) {
			System.out.println("Into UpdateUserByIdTest");
			creationTime = System.currentTimeMillis();
			count1.setcreationtime(creationTime);
			// creationTime = System.currentTimeMillis();
			try {
				bqdb1.updateTable(this.BQName, runningMachine, this.buildVersion, String.valueOf(Nooftest), "0", "0",
						String.valueOf(Nooftest), count1.getcreationtime(), "InProgress",
						author + " and Rest server is " + baseURI.split("//")[1]);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		setEnv(baseURI,  port,  userName, password);
		// end
	}

	/*
	 * adding cases for 3 tier msp
	 */
	@DataProvider(name = "userInfo50")
	public final Object[][] getUserInfo50() {
		return new Object[][] {
				{root_msp_email, spogServer.ReturnRandom("account1_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
								"direct_admin",account_rootMSP1_id, "yuefen_new"},
				{root_msp_email, spogServer.ReturnRandom("account2_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
								"direct_admin", account2_rootMSP1_id, "yuefen_new" }, 
				{root_msp_account_admin_email, spogServer.ReturnRandom("account1_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
									"direct_admin", account_rootMSP1_id, "yuefen_new" },
				
				{sub_msp_email, spogServer.ReturnRandom("account1_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
										"direct_admin", account_subMSP1_root1_id, "yuefen_new" },
				{sub_msp_email, spogServer.ReturnRandom("account2_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
										"direct_admin", account2_subMSP1_root1_id, "yuefen_new" }, 
				{sub_msp_account_admin_email, spogServer.ReturnRandom("account1_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
											"direct_admin", account_subMSP1_root1_id, "yuefen_new" },
				};
	}

	@Test(dataProvider = "userInfo50")
	public void rootMspRelatedUpdateSuccessTest(String loginUser, String email, String password, String first_name,
			String last_name, String role_id, String organization_id, String newFirstName) {
		System.out.println("rootMspRelatedUpdateSuccessTest");
		test = rep.startTest("rootMspRelatedUpdateSuccessTest");
		test.assignAuthor("Liu Yuefen");

		spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN);

		// create user
		String user_id = spogServer.createUserAndCheck(email, password, first_name, last_name, role_id, organization_id,
				test);

		// login
		spogServer.userLogin(loginUser, sharePassword, SpogConstants.SUCCESS_LOGIN);

		// update user
		Response response = spogServer.updateUserById(user_id, "", "", newFirstName, "", "", "", test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		response.then().body("data.first_name", equalTo(newFirstName));

		// delete user
		spogServer.CheckDeleteUserByIdStatus(user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	}
	
	@DataProvider(name = "userInfo60")
	public final Object[][] getUserInfo60() {
		return new Object[][] {
				{root_msp_email, spogServer.ReturnRandom("submsp1_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
							"msp_admin", subMSP1_root1_id, "yuefen_new",SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" }, 
				{root_msp_email, spogServer.ReturnRandom("submsp2_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
							"msp_admin", subMSP2_root1_id, "yuefen_new",SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" }, 
				{root_msp_email, spogServer.ReturnRandom("submsp1_msp_account_admin_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
							"msp_account_admin", subMSP1_root1_id, "yuefen_new",SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" }, 
				{root_msp_email, spogServer.ReturnRandom("account_submsp1_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
								"direct_admin", account_subMSP1_root1_id, "yuefen_new",SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" },
				{root_msp_email, spogServer.ReturnRandom("account2_submsp1_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
									"direct_admin", account2_subMSP1_root1_id, "yuefen_new",SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" },
				
				{root_msp_account_admin_email, spogServer.ReturnRandom("submsp1_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
										"msp_admin", subMSP1_root1_id, "yuefen_new",SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" }, 
				{root_msp_account_admin_email, spogServer.ReturnRandom("submsp2_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
										"msp_admin", subMSP2_root1_id, "yuefen_new",SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" }, 
				{root_msp_account_admin_email, spogServer.ReturnRandom("submsp1_msp_account_admin_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
										"msp_account_admin", subMSP1_root1_id, "yuefen_new",SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" }, 
				{root_msp_account_admin_email, spogServer.ReturnRandom("account_submsp1_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
											"direct_admin", account_subMSP1_root1_id, "yuefen_new",SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" },
				{root_msp_account_admin_email, spogServer.ReturnRandom("account2_submsp1_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
												"direct_admin", account2_subMSP1_root1_id, "yuefen_new",SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" },
				
				
				{sub_msp_email, spogServer.ReturnRandom("submsp2_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
										"msp_admin", subMSP2_root1_id, "yuefen_new",SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" },
				{sub_msp_email, spogServer.ReturnRandom("account_submsp2_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
										"direct_admin", account_subMSP2_root1_id, "yuefen_new", SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101"}, 
				{sub_msp_email, spogServer.ReturnRandom("account_submsp2_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
											"direct_admin", account_rootMSP1_id, "yuefen_new", SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101"}, 
				{sub_msp_account_admin_email, spogServer.ReturnRandom("account2_submsp1_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
											"direct_admin", account2_subMSP1_root1_id, "yuefen_new", SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" },
				{sub_msp_account_admin_email, spogServer.ReturnRandom("account_submsp2_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
												"direct_admin", account_subMSP2_root1_id, "yuefen_new", SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" },
				{sub_msp_email, spogServer.ReturnRandom("account_submsp2_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
													"direct_admin", account_rootMSP1_id, "yuefen_new", SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101"}, 
				
				
				{accountDirect_rootMsp_email, spogServer.ReturnRandom("account2_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
													"direct_admin", account2_rootMSP1_id, "yuefen_new", SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" },
				{accountDirect_rootMsp_email, spogServer.ReturnRandom("account_submsp2_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
														"direct_admin", account_subMSP2_root1_id, "yuefen_new", SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" },
				{accountDirect_rootMsp_email, spogServer.ReturnRandom("account_submsp2_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
															"msp_admin", subMSP1_root1_id, "yuefen_new", SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" },
				
				{accountDirect_subMsp_email, spogServer.ReturnRandom("account_submsp2_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
															"direct_admin", account2_subMSP1_root1_id, "yuefen_new", SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" },
				{accountDirect_subMsp_email, spogServer.ReturnRandom("account_submsp2_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
																"direct_admin", account_subMSP2_root1_id, "yuefen_new", SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" },
				{accountDirect_subMsp_email, spogServer.ReturnRandom("account_submsp2_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
																	"msp_admin", subMSP1_root1_id, "yuefen_new", SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" },
				
				{msp_email, spogServer.ReturnRandom("rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
													"msp_admin", root_msp1_id, "yuefen_new",SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" }, 
				{msp_email, spogServer.ReturnRandom("account_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
														"direct_admin", account_rootMSP1_id, "yuefen_new",SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" }, 
				{msp_email, spogServer.ReturnRandom("submsp1_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
															"msp_admin", subMSP1_root1_id, "yuefen_new",SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" }, 
				{msp_email, spogServer.ReturnRandom("account_submsp1_rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
																"direct_admin", account_subMSP1_root1_id, "yuefen_new",SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" }, 
				{direct_email, spogServer.ReturnRandom("rootmsp1@spogqa.com"), sharePassword, "betty", "liu",
																	"msp_admin", root_msp1_id, "yuefen_new",SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" }, 
				{root_msp_email, spogServer.ReturnRandom("msp@spogqa.com"), sharePassword, "betty", "liu",
																		"msp_admin", mspOrg_id, "yuefen_new",SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" }, 
				{root_msp_email, spogServer.ReturnRandom("direct@spogqa.com"), sharePassword, "betty", "liu",
																			"direct_admin", directOrg_id, "yuefen_new",SpogConstants.INSUFFICIENT_PERMISSIONS,"00100101" },
				};
	}

	@Test(dataProvider = "userInfo60")
	public void rootMspRelatedUpdateFailTest(String loginUser, String email, String password, String first_name,
			String last_name, String role_id, String organization_id, String newFirstName, int expectedStatusCode, String expectedErrorCode ) {
		System.out.println("rootMspRelatedUpdateFailTest");
		test = rep.startTest("rootMspRelatedUpdateFailTest");
		test.assignAuthor("Liu Yuefen");

		spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN);

		// create user
		String user_id = spogServer.createUserAndCheck(email, password, first_name, last_name, role_id, organization_id,
				test);

		// login
		spogServer.userLogin(loginUser, sharePassword, SpogConstants.SUCCESS_LOGIN);

		// update user
		Response response = spogServer.updateUserById(user_id, "", "", newFirstName, "", "", "", test);
		spogServer.checkResponseStatus(response, expectedStatusCode);
		spogServer.checkErrorCode(response, expectedErrorCode);

		// delete user
		spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN);
		spogServer.CheckDeleteUserByIdStatus(user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	}
	
	@DataProvider(name = "userInfo7")
	public final Object[][] getUserInfo7() {
		return new Object[][] {
				{ spogServer.ReturnRandom("update_csr_admin_yuefen@spogqa.com"), spogServer.ReturnRandom("aaAA88"),
						spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "csr_admin", csrOrg_id,
						SpogConstants.INSUFFICIENT_PERMISSIONS, "00100101" },
				{ spogServer.ReturnRandom("update_csr_read_only_yuefen@spogqa.com"), spogServer.ReturnRandom("aaAA88"),
						spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "csr_read_only", csrOrg_id,
						SpogConstants.INSUFFICIENT_PERMISSIONS, "00100101" },
				{ spogServer.ReturnRandom("spogServer_update_direct@spogqa.com"), spogServer.ReturnRandom("abyYc3"),
						spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "direct_admin", directOrg_id,
						SpogConstants.INSUFFICIENT_PERMISSIONS, "00100101" },
				{ spogServer.ReturnRandom("spogServer_update_msp_admin@spogqa.com"), spogServer.ReturnRandom("abyYc3"),
						spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "msp_admin", mspOrg_id,
						SpogConstants.INSUFFICIENT_PERMISSIONS, "00100101" },
				{ spogServer.ReturnRandom("spogServer_update_msp_account_admin@spogqa.com"),
						spogServer.ReturnRandom("abyYc3"), spogServer.ReturnRandom("yuefen"),
						spogServer.ReturnRandom("liu"), "msp_account_admin", mspOrg_id,
						SpogConstants.INSUFFICIENT_PERMISSIONS, "00100101" },
				{ spogServer.ReturnRandom("spogServer_update_account_direct_admin@spogqa.com"),
						spogServer.ReturnRandom("abycdYc3"), spogServer.ReturnRandom("yuefen"),
						spogServer.ReturnRandom("liu"), "direct_admin", account_id,
						SpogConstants.INSUFFICIENT_PERMISSIONS, "00100101" }
						};
						
	}

	@Test(dataProvider = "userInfo7")
	public void csrReadOnlyUpdateUser(String email, String password, String first_name, String last_name,
			String role_id, String organization_id, int expectedStatusCode, String expectedErrorCode) {
		System.out.println("csrReadOnlyUpdateUser");
		test = rep.startTest("csrReadOnlyUpdateUser");
		test.assignAuthor("Liu Yuefen");

		// login with csr admin
		spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN);

		// create and update csr/direct/msp/account usre
		String user_id = spogServer.createUserAndCheck(email, password, first_name, last_name, role_id, organization_id,
				test);

		// login with csr read only user
		spogServer.userLogin(csrReadOnlyUser, sharePassword, SpogConstants.SUCCESS_LOGIN);

		// update user
		String newFirstName = spogServer.ReturnRandom("yuefen_update");
		String newLastName = spogServer.ReturnRandom("liu_update");
		Response response = spogServer.updateUserById(user_id, "", "", newFirstName, newLastName, role_id, "", test);
		spogServer.checkResponseStatus(response, expectedStatusCode);
		spogServer.checkErrorCode(response, expectedErrorCode);
        //delete user
		spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN);
		spogServer.CheckDeleteUserByIdStatus(user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		
	}

	@Test
	// after assgin, it can update user under sub org; after unassigned, it can't
	// update user under sub org.
	public void mspAccountAdminUpdateMAccountUser() {
		System.out.println("mspAccountAdminUpdateMAccountUser");
		test = rep.startTest("mspAccountAdminUpdateMAccountUser");
		test.assignAuthor("Liu Yuefen");

		spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN);
		String csr_token = spogServer.getJWTToken();
		// create user
		String email = spogServer.ReturnRandom("msp_account_admin@spogqa.com");
		String admin_id = spogServer.createUserAndCheck(email, sharePassword, "yuefen", "liu", "msp_account_admin",
				mspOrg_id, test);
		// assign
		String[] userIds = new String[1];
		userIds[0] = admin_id;
		userSpogServer.assignMspAccountAdmins(mspOrg_id, account_id, userIds, csr_token);
		// login
		spogServer.userLogin(email, sharePassword, SpogConstants.SUCCESS_LOGIN);
		String admin_token = spogServer.getJWTToken();
		// create account user
		String userId = spogServer.createUserAndCheck(spogServer.ReturnRandom("account_yuefen@spogqa.com"),
				sharePassword, "yuefen", "liu", "direct_admin", account_id, test);
		// update user
		Response response = spogServer.updateUserById(userId, "", "", "yuefen1", "", "", "", test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);

		// unassign
		userSpogServer.unAssignMspAccountAdmins(mspOrg_id, account_id, userIds, csr_token);
		spogServer.setToken(csr_token);
		String userId2 = spogServer.createUserAndCheck(spogServer.ReturnRandom("account_yuefen_user@spogqa.com"),
				sharePassword, "yuefen", "liu", "direct_admin", account_id, test);
		spogServer.setToken(admin_token);
		response = spogServer.updateUserById(userId, "", "", "yuefen2", "", "", "", test);
		spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS);
		spogServer.checkErrorCode(response, "00100101");
		
		//delete user
		spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN);
		spogServer.CheckDeleteUserByIdStatus(userId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		spogServer.CheckDeleteUserByIdStatus(userId2, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		
	}

	@DataProvider(name = "userInfo5")
	public final Object[][] getUserInfo5() {
		return new Object[][] {
//				{ this.csrGlobalLoginUser, spogServer.ReturnRandom("update_msp@spogqa.com"), this.csrGlobalLoginPassword, "betty", "liu",
//						"msp_account_admin", mspOrg_id, "yuefen_new" },
				{ msp_email, spogServer.ReturnRandom("update_msp@spogqa.com"), sharePassword, "betty", "liu",
						"msp_account_admin", mspOrg_id, "yuefen_new" }, 
				{ root_msp_email, spogServer.ReturnRandom("update_msp@spogqa.com"), sharePassword, "betty", "liu",
							"direct_admin", this.account_rootMSP1_id, "yuefen_new" }, 
				};
	}

	@Test(dataProvider = "userInfo5")
	public void updateMSPAdminsSuccess(String loginUser, String email, String password, String first_name,
			String last_name, String role_id, String organization_id, String newFirstName) {
		System.out.println("updateMSPAdminsSuccess");
		test = rep.startTest("updateMSPAdminsSuccess");
		test.assignAuthor("Liu Yuefen");

		spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN);

		// create user
		String user_id = spogServer.createUserAndCheck(email, password, first_name, last_name, role_id, organization_id,
				test);

		// login
		spogServer.userLogin(loginUser, sharePassword, SpogConstants.SUCCESS_LOGIN);

		// update user
		Response response = spogServer.updateUserById(user_id, "", "", newFirstName, "", "", "", test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		response.then().body("data.first_name", equalTo(newFirstName));

		// delete user
		spogServer.CheckDeleteUserByIdStatus(user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	}

	@DataProvider(name = "userInfo6")
	public final Object[][] getUserInfo6() {
		return new Object[][] {
				// user permission
				{ direct_email, spogServer.ReturnRandom("update_msp@spogqa.com"), sharePassword, "betty", "liu",
						"msp_account_admin", mspOrg_id, "yuefen_new", "", SpogConstants.INSUFFICIENT_PERMISSIONS,
						"00100101" },
				{ accountDirect_email, spogServer.ReturnRandom("update_msp@spogqa.com"), sharePassword, "betty", "liu",
						"msp_account_admin", mspOrg_id, "yuefen_new", "", SpogConstants.INSUFFICIENT_PERMISSIONS,
						"00100101" },
				{ msp2_email, spogServer.ReturnRandom("update_msp@spogqa.com"), sharePassword, "betty", "liu",
						"msp_account_admin", mspOrg_id, "yuefen_new", "", SpogConstants.INSUFFICIENT_PERMISSIONS,
						"00100101" },
				// role can't update to other types
//				{this.csrGlobalLoginUser, spogServer.ReturnRandom("update_msp@spogqa.com"), this.csrGlobalLoginPassword, "betty", "liu",
//						"msp_account_admin", mspOrg_id, "yuefen_new", "csr", SpogConstants.NOT_ALLOWED_ON_RESOURCE,
//						"00200012" },
//				{ this.csrGlobalLoginUser, spogServer.ReturnRandom("update_msp@spogqa.com"), this.csrGlobalLoginPassword, "betty", "liu",
//						"msp_account_admin", mspOrg_id, "yuefen_new", "direct", SpogConstants.NOT_ALLOWED_ON_RESOURCE,
//						"00200012" },
//				{ this.csrGlobalLoginUser, spogServer.ReturnRandom("update_msp@spogqa.com"), this.csrGlobalLoginPassword, "betty", "liu",
//						"msp_account_admin", mspOrg_id, "yuefen_new", "msp", SpogConstants.NOT_ALLOWED_ON_RESOURCE,
//						"00200012" }, 
				};
	}

	@Test(dataProvider = "userInfo6")
	public void updateMSPAdminsFail(String loginUser, String email, String password, String first_name,
			String last_name, String role_id, String organization_id, String newFirstName, String newRole,
			int expectedStatusCode, String expectedErrorCode) {
		System.out.println("updateMSPAdminsFail");
		test = rep.startTest("updateMSPAdminsFail");
		test.assignAuthor("Liu Yuefen");

		spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN);
		String csrToken = spogServer.getJWTToken();
		// create user
		String user_id = spogServer.createUserAndCheck(email, password, first_name, last_name, role_id, organization_id,
				test);

		// login
		spogServer.userLogin(loginUser, sharePassword, SpogConstants.SUCCESS_LOGIN);

		// update user
		Response response = spogServer.updateUserById(user_id, "", "", newFirstName, "", newRole, "", test);
		spogServer.checkResponseStatus(response, expectedStatusCode);
		spogServer.checkErrorCode(response, expectedErrorCode);

		// delete user
		spogServer.setToken(csrToken);
		spogServer.CheckDeleteUserByIdStatus(user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	}

	@DataProvider(name = "userInfo3")
	public final Object[][] getUserInfo3() {
		return new Object[][] {
				{ spogServer.ReturnRandom("update_csr_blocked@spogqa.com"), sharePassword, "betty", "liu", "csr_admin",
						csrOrg_id, "true" },
				{ spogServer.ReturnRandom("update_direct_blocked@spogqa.com"), sharePassword, "betty", "liu",
						"direct_admin", directOrg_id, "true" },
				{ spogServer.ReturnRandom("update_msp_blocked@spogqa.com"), sharePassword, "betty", "liu", "msp_admin",
						mspOrg_id, "true" },
				{ spogServer.ReturnRandom("update_account_blocked@spogqa.com"), sharePassword, "betty", "liu",
						"direct_admin", account_id, "true" }

		};
	}

	@Test(dataProvider = "userInfo3")
	public void updateUserBlockStatusSuccess(String email, String password, String first_name, String last_name,
			String role_id, String organization_id, String blockedStatus) {
		System.out.println("updateUserBlockStatusSuccess");
		test = rep.startTest("updateUserBlockStatusSuccess");
		test.assignAuthor("Liu Yuefen");

		spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN);
		String token = spogServer.getJWTToken();

		// create user
		String user_id = spogServer.createUserAndCheck(email, password, first_name, last_name, role_id, organization_id,
				test);

		// update user and check the block status
		Response response = spogServer.updateUserByIdForBlockedStatus(user_id, blockedStatus, token, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		response.then().body("data.blocked", equalTo(Boolean.valueOf(blockedStatus)));
		response = spogServer.login(email, password);
		spogServer.checkResponseStatus(response, SpogConstants.NOT_LOGGED_IN, test);
		spogServer.checkErrorCode(response, "00200026");

		// update user to false again
		response = spogServer.updateUserByIdForBlockedStatus(user_id, "false", token, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		response.then().body("data.blocked", equalTo(Boolean.valueOf("false")));
		response = spogServer.login(email, password);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_LOGIN);

		// delete user
		spogServer.CheckDeleteUserByIdStatus(user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	}

	@DataProvider(name = "userInfo4")
	public final Object[][] getUserInfo4() {
		return new Object[][] {
				{ this.direct_email, spogServer.ReturnRandom("update_csr_blocked@spogqa.com"), sharePassword, "betty",
						"liu", "csr_admin", csrOrg_id, "true", SpogConstants.INSUFFICIENT_PERMISSIONS, "00100101" },
				{ this.direct_email, spogServer.ReturnRandom("update_direct_blocked@spogqa.com"), sharePassword,
						"betty", "liu", "direct_admin", directOrg_id, "true", SpogConstants.INSUFFICIENT_PERMISSIONS,
						"00200028" },
				{ this.direct_email, spogServer.ReturnRandom("update_msp_blocked@spogqa.com"), sharePassword, "betty",
						"liu", "msp_admin", mspOrg_id, "true", SpogConstants.INSUFFICIENT_PERMISSIONS, "00100101" },
				{ this.direct_email, spogServer.ReturnRandom("update_account_blocked@spogqa.com"), sharePassword,
						"betty", "liu", "direct_admin", account_id, "true", SpogConstants.INSUFFICIENT_PERMISSIONS,
						"00100101" },
				{ this.msp_email, spogServer.ReturnRandom("update_csr_blocked@spogqa.com"), sharePassword, "betty",
						"liu", "csr_admin", csrOrg_id, "true", SpogConstants.INSUFFICIENT_PERMISSIONS, "00100101" },
				{ this.msp_email, spogServer.ReturnRandom("update_direct_blocked@spogqa.com"), sharePassword, "betty",
						"liu", "direct_admin", directOrg_id, "true", SpogConstants.INSUFFICIENT_PERMISSIONS,
						"00100101" },
				{ this.msp_email, spogServer.ReturnRandom("update_msp_blocked@spogqa.com"), sharePassword, "betty",
						"liu", "msp_admin", mspOrg_id, "true", SpogConstants.INSUFFICIENT_PERMISSIONS, "00200028" },
				{ this.msp_email, spogServer.ReturnRandom("update_account_blocked@spogqa.com"), sharePassword, "betty",
						"liu", "direct_admin", account_id, "true", SpogConstants.INSUFFICIENT_PERMISSIONS, "00200028" },
				{ this.accountDirect_email, spogServer.ReturnRandom("update_csr_blocked@spogqa.com"), sharePassword,
						"betty", "liu", "csr_admin", csrOrg_id, "true", SpogConstants.INSUFFICIENT_PERMISSIONS,
						"00100101" },
				{ this.accountDirect_email, spogServer.ReturnRandom("update_direct_blocked@spogqa.com"), sharePassword,
						"betty", "liu", "direct_admin", directOrg_id, "true", SpogConstants.INSUFFICIENT_PERMISSIONS,
						"00100101" },
				{ this.accountDirect_email, spogServer.ReturnRandom("update_msp_blocked@spogqa.com"), sharePassword,
						"betty", "liu", "msp_admin", mspOrg_id, "true", SpogConstants.INSUFFICIENT_PERMISSIONS,
						"00100101" },
				{ this.accountDirect_email, spogServer.ReturnRandom("update_account_blocked@spogqa.com"), sharePassword,
						"betty", "liu", "direct_admin", account_id, "true", SpogConstants.INSUFFICIENT_PERMISSIONS,
						"00200028" },
//				{ this.csrGlobalLoginUser, spogServer.ReturnRandom("update_csr_blocked@spogqa.com"), this.csrGlobalLoginPassword, "betty",
//						"liu", "csr_admin", csrOrg_id, "abc", SpogConstants.SUCCESS_GET_PUT_DELETE, "" },
//				{ this.csrGlobalLoginUser, spogServer.ReturnRandom("update_direct_blocked@spogqa.com"), this.csrGlobalLoginPassword, "betty",
//						"liu", "direct_admin", directOrg_id, "", SpogConstants.SUCCESS_GET_PUT_DELETE, "" },
//				{ this.csrGlobalLoginUser, spogServer.ReturnRandom("update_msp_blocked@spogqa.com"), this.csrGlobalLoginPassword, "betty",
//						"liu", "msp_admin", mspOrg_id, "none", SpogConstants.SUCCESS_GET_PUT_DELETE, "" },
//				{ this.csrGlobalLoginUser, spogServer.ReturnRandom("update_account_blocked@spogqa.com"), this.csrGlobalLoginPassword, "betty",
//						"liu", "direct_admin", account_id, null, SpogConstants.SUCCESS_GET_PUT_DELETE, "" }

		};
	}

	@Test(dataProvider = "userInfo4")
	public void updateUserBlockStatusFail(String userName, String email, String password, String first_name,
			String last_name, String role_id, String organization_id, String blockedStatus, int expectedStatusCode,
			String expectedErrorcode) {
		System.out.println("updateUserBlockStatusFail");
		test = rep.startTest("updateUserBlockStatusFail");
		test.assignAuthor("Liu Yuefen");

		spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN);
		String csrToken = spogServer.getJWTToken();

		// create user
		String user_id = spogServer.createUserAndCheck(email, password, first_name, last_name, role_id, organization_id,
				test);
		// login with non-csr user
		spogServer.userLogin(userName, sharePassword, SpogConstants.SUCCESS_LOGIN);
		String token = spogServer.getJWTToken();

		// update user and check the block status
		Response response = spogServer.updateUserByIdForBlockedStatus(user_id, blockedStatus, token, test);
		spogServer.checkResponseStatus(response, expectedStatusCode);

		if (expectedStatusCode != 200) {
			spogServer.checkErrorCode(response, expectedErrorcode);
		}
		// delete user
		spogServer.setToken(csrToken);
		spogServer.CheckDeleteUserByIdStatus(user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	}

	@DataProvider(name = "userInfo1")
	public final Object[][] getUserInfo1() {
		return new Object[][] {
				{ spogServer.ReturnRandom("update_csr_phone@spogqa.com"), sharePassword, "betty", "liu", "csr_admin",
						"", "+86-13812345678" },
				{ spogServer.ReturnRandom("update_direct_phone@spogqa.com"), sharePassword, "betty", "liu",
						"direct_admin", directOrg_id, "+911234567891" },
				{ spogServer.ReturnRandom("update_msp_phone@spogqa.com"), sharePassword, "betty", "liu", "msp_admin",
						mspOrg_id, "+86-01050890816" },
				{ spogServer.ReturnRandom("update_account_phone@spogqa.com"), sharePassword, "betty", "liu",
						"direct_admin", account_id, "+81-345201234" },
				{ spogServer.ReturnRandom("update_account_phone@spogqa.com"), sharePassword, "betty", "liu",
						"direct_admin", account_id, "+1(607) 240-1234" },
				{ spogServer.ReturnRandom("update_account_phone@spogqa.com"), sharePassword, "betty", "liu",
						"direct_admin", account_id, "+1 405 517 1234" } };
	}

	@Test(dataProvider = "userInfo1")
	public void updateUserPhoneNumber(String email, String password, String first_name, String last_name,
			String role_id, String organization_id, String phone_number) {
		System.out.println("updateUserPhoneNumber");
		test = rep.startTest("updateUserPhoneNumber");
		test.assignAuthor("Liu Yuefen");

		spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN);

		// create user
		String user_id = spogServer.createUserAndCheck(email, password, first_name, last_name, role_id, organization_id,
				test);

		// update user
		Response response = spogServer.updateUserById(user_id, "", "", "", "", "", "", phone_number, test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		response.then().body("data.phone_number", equalTo(phone_number));

		// delete user
		spogServer.CheckDeleteUserByIdStatus(user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	}

	@DataProvider(name = "userInfo2")
	public final Object[][] getUserInfo2() {
		return new Object[][] {
//				{ spogServer.ReturnRandom("update_csr_phone@spogqa.com"), sharePassword, "betty", "liu", "csr_admin",
//						"", "abc" },
				{ spogServer.ReturnRandom("update_csr_phone@spogqa.com"), sharePassword, "betty", "liu", "csr_admin",
						"", "+86 405 517 1234" },
				{ spogServer.ReturnRandom("update_csr_phone@spogqa.com"), sharePassword, "betty", "liu", "csr_admin",
						"", "+1 13811011012" },
				{ spogServer.ReturnRandom("update_csr_phone@spogqa.com"), sharePassword, "betty", "liu", "csr_admin",
						"", RandomStringUtils.randomNumeric(8) } };
	}

	// modified by xiang for enroll organization, phone number has no any checking,
	// it can accept anything or null.
	@Test(dataProvider = "userInfo2")
	public void updateUserPhoneNumberFail(String email, String password, String first_name, String last_name,
			String role_id, String organization_id, String phone_number) {
		System.out.println("updateUserPhoneNumber");
		test = rep.startTest("updateUserPhoneNumber");
		test.assignAuthor("Liu Yuefen");

		spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN);

		// create user
		String user_id = spogServer.createUserAndCheck(email, password, first_name, last_name, role_id, organization_id,
				test);

		// update user
		Response response = spogServer.updateUserById(user_id, "", "", "", "", "", "", phone_number, test);
		// spogServer.checkResponseStatus(response,
		// SpogConstants.REQUIRED_INFO_NOT_EXIST);
		// spogServer.checkErrorCode(response, "4000000A");
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);

		// delete user
		spogServer.CheckDeleteUserByIdStatus(user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	}

	@DataProvider(name = "userInfo")
	public final Object[][] getUserInfo() {
		return new Object[][] {
				//{ spogServer.ReturnRandom("update_csr_yuefen@spogqa.com"), spogServer.ReturnRandom("aaAA88"),
						//spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "csr_admin", "" },
				{ spogServer.ReturnRandom("spogServer_update_direct@spogqa.com"), spogServer.ReturnRandom("abyYc3"),
						spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "direct_admin",
						directOrg_id },
				{ spogServer.ReturnRandom("spogServer_update_msp@spogqa.com"), spogServer.ReturnRandom("abyYc3"),
						spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "msp_admin", mspOrg_id },
				{ spogServer.ReturnRandom("spogServer_update_account@spogqa.com"), spogServer.ReturnRandom("abycdYc3"),
						spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "direct_admin",
						account_id }, };
	}

	@Test(dataProvider = "userInfo")
	public void csrUpdateUser(String email, String password, String first_name, String last_name, String role_id,
			String organization_id) {
		System.out.println("csrUpdateUser");
		test = rep.startTest("csrUpdateUser");
		test.assignAuthor("Liu Yuefen");

		spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN);

		// create and update csr/direct/msp/account usre
		String user_id = spogServer.createUserAndCheck(email, password, first_name, last_name, role_id, organization_id,
				test);

		// update user
		String newFirstName = spogServer.ReturnRandom("yuefen_update");
		String newLastName = spogServer.ReturnRandom("liu_update");
		Response response = spogServer.updateUserById(user_id, "", "", newFirstName, newLastName, role_id, "", test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE);
		response.then().body("data.first_name", equalTo(newFirstName));
		response.then().body("data.last_name", equalTo(newLastName));
		//delete user
		spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN);
		spogServer.CheckDeleteUserByIdStatus(user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	}

	@Test
	public void mspUpdateUser() {
		System.out.println("mspUpdateUser");
		test = rep.startTest("mspUpdateUser");
		test.assignAuthor("Liu Yuefen");

		spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		// create csr/direct/msp admin
//		String csr_user_id = spogServer.createUserAndCheck(spogServer.ReturnRandom("update_csr_yuefen@spogqa.com"),
//				spogServer.ReturnRandom("aaAA88"), spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"),
//				"csr_admin", "", test);
		String direct_user_id = spogServer.createUserAndCheck(
				spogServer.ReturnRandom("spogServer_update_direct@spogqa.com"), spogServer.ReturnRandom("abyYc3"),
				spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "direct_admin", directOrg_id, test);
		String msp_user_id = spogServer.createUserAndCheck(spogServer.ReturnRandom("spogServer_update_msp1@spogqa.com"),
				spogServer.ReturnRandom("abyYc3"), spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"),
				"msp_admin", mspOrg_id, test);
		String msp2_user_id = spogServer.createUserAndCheck(
				spogServer.ReturnRandom("spogServer_update_msp2@spogqa.com"), spogServer.ReturnRandom("abyYc3"),
				spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "msp_admin", mspOrg2_id, test);
		String account1_user_id = spogServer.createUserAndCheck(
				spogServer.ReturnRandom("spogServer_update_account1_msp1@spogqa.com"),
				spogServer.ReturnRandom("abyYc3"), spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"),
				"direct_admin", account_id, test);
		String account2_user_id = spogServer.createUserAndCheck(
				spogServer.ReturnRandom("spogServer_update_account2_msp2@spogqa.com"),
				spogServer.ReturnRandom("abyYc3"), spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"),
				"direct_admin", account2_id, test);
		// create msp admin 2
		String logInUserName = spogServer.ReturnRandom("yuefen_msp2222@spogqa.com");
		String logInPassword = spogServer.ReturnRandom("aaAAcc11");
		String msp_user2_id = spogServer.createUserAndCheck(logInUserName, logInPassword,
				spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "msp_admin", mspOrg_id, test);
		// login with msp admin 2
		spogServer.userLogin(logInUserName, logInPassword, SpogConstants.SUCCESS_LOGIN, test);

		// update csr
//		Response response = spogServer.updateUserById(csr_user_id, "", "", spogServer.ReturnRandom("yuefen_update"),
//				spogServer.ReturnRandom("liu_update"), "csr_admin", "", test);
//		spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
//		spogServer.checkErrorCode(response, "00100101");

		// update direct
		response = spogServer.updateUserById(direct_user_id, "", "", spogServer.ReturnRandom("yuefen_update"),
				spogServer.ReturnRandom("liu_update"), "direct_admin", "", test);
		spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
		spogServer.checkErrorCode(response, "00100101");

		// update msp admin under the same org
		response = spogServer.updateUserById(msp_user_id, "", "", spogServer.ReturnRandom("yuefen_update"),
				spogServer.ReturnRandom("liu_update"), "msp_admin", "", test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);

		// update itself
		response = spogServer.updateUserById(msp_user2_id, "", "", spogServer.ReturnRandom("yuefen_update_itself"),
				spogServer.ReturnRandom("liu_update_itself"), "msp_admin", "", test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);

		// update account under its msp org
		response = spogServer.updateUserById(account1_user_id, "", "", spogServer.ReturnRandom("yuefen_update"),
				spogServer.ReturnRandom("liu_update"), "direct_admin", "", test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);

		// update msp2 admin
		response = spogServer.updateUserById(msp2_user_id, "", "", spogServer.ReturnRandom("yuefen_update_msp2"),
				spogServer.ReturnRandom("liu_update"), "direct_admin", "", test);
		spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
		spogServer.checkErrorCode(response, "00100101");

		// update account under another msp org
		response = spogServer.updateUserById(account2_user_id, "", "", spogServer.ReturnRandom("yuefen_update_itself"),
				spogServer.ReturnRandom("liu_update_itself"), "direct_admin", "", test);
		spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
		spogServer.checkErrorCode(response, "00100101");
		
		//delete user
		spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		spogServer.CheckDeleteUserByIdStatus(direct_user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		spogServer.CheckDeleteUserByIdStatus(msp_user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		spogServer.CheckDeleteUserByIdStatus(msp2_user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		spogServer.CheckDeleteUserByIdStatus(account1_user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		spogServer.CheckDeleteUserByIdStatus(account2_user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		spogServer.CheckDeleteUserByIdStatus(msp_user2_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	}

	@Test
	public void directUpdateUser() {
		System.out.println("directUpdateUser");
		test = rep.startTest("directUpdateUser");
		test.assignAuthor("Liu Yuefen");

		spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, test);

		// create csr/direct/msp admin
//		String csr_user_id = spogServer.createUserAndCheck(spogServer.ReturnRandom("update_csr_yuefen_csr@spogqa.com"),
//				spogServer.ReturnRandom("aaAA88"), spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"),
//				"csr_admin", "", test);
		String direct_user_id = spogServer.createUserAndCheck(
				spogServer.ReturnRandom("spogServer_update_direct_yuefen@spogqa.com"),
				spogServer.ReturnRandom("abyYc3"), spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"),
				"direct_admin", directOrg_id, test);
		String direct2_user_id = spogServer.createUserAndCheck(
				spogServer.ReturnRandom("spogServer_update_direct2_yuefen@spogqa.com"),
				spogServer.ReturnRandom("abyYc3"), spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"),
				"direct_admin", directOrg2_id, test);
		String msp_user_id = spogServer.createUserAndCheck(
				spogServer.ReturnRandom("spogServer_update_msp_yuefen@spogqa.com"), spogServer.ReturnRandom("abyYc3"),
				spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "msp_admin", mspOrg_id, test);
		String account_user_id = spogServer.createUserAndCheck(
				spogServer.ReturnRandom("spogServer_update_account_yuefen@spogqa.com"),
				spogServer.ReturnRandom("abyYc3"), spogServer.ReturnRandom("yuefen_account"),
				spogServer.ReturnRandom("liu"), "direct_admin", account_id, test);
		// create direct admin 2
		String logInUserName = spogServer.ReturnRandom("direct2_yuefen_update@spogqa.com");
		String logInPassword = spogServer.ReturnRandom("aaAAcc11");
		String direct_user2_id = spogServer.createUserAndCheck(logInUserName, logInPassword,
				spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "direct_admin", directOrg_id, test);
		// login with direct admin 2
		spogServer.userLogin(logInUserName, logInPassword, SpogConstants.SUCCESS_LOGIN, test);

		// update csr
//		Response response = spogServer.updateUserById(csr_user_id, "", "", spogServer.ReturnRandom("yuefen_csr_update"),
//				spogServer.ReturnRandom("liu_update_csr"), "csr_admin", "", test);
//		spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
//		spogServer.checkErrorCode(response, "00100101");

		// update another direct user in the same org
		response = spogServer.updateUserById(direct_user_id, "", "", spogServer.ReturnRandom("yuefen_direct_update"),
				spogServer.ReturnRandom("liu_update_direct"), "direct_admin", "", test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);

		// update another direct admin in other org
		response = spogServer.updateUserById(direct2_user_id, "", "", spogServer.ReturnRandom("yuefen_update_account"),
				spogServer.ReturnRandom("liu_update_account"), "direct_admin", "", test);
		spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
		spogServer.checkErrorCode(response, "00100101");

		// update msp
		response = spogServer.updateUserById(msp_user_id, "", "", spogServer.ReturnRandom("yuefen_msp_update"),
				spogServer.ReturnRandom("liu_update_msp"), "msp_admin", "", test);
		spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
		spogServer.checkErrorCode(response, "00100101");

		// update itself
		response = spogServer.updateUserById(direct_user2_id, "", "", spogServer.ReturnRandom("yuefen_update_itself"),
				spogServer.ReturnRandom("liu_update_itself"), "direct_admin", "", test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);

		// update account
		response = spogServer.updateUserById(account_user_id, "", "", spogServer.ReturnRandom("yuefen_update_account"),
				spogServer.ReturnRandom("liu_update_account"), "direct_admin", "", test);
		spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
		spogServer.checkErrorCode(response, "00100101");
		
		//delete user
		spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		spogServer.CheckDeleteUserByIdStatus(direct_user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		spogServer.CheckDeleteUserByIdStatus(direct2_user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		spogServer.CheckDeleteUserByIdStatus(msp_user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		spogServer.CheckDeleteUserByIdStatus(account_user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		spogServer.CheckDeleteUserByIdStatus(direct_user2_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	
	}

	@Test
	public void accountUpdateUser() {
		System.out.println("accountUpdateUser");
		test = rep.startTest("accountUpdateUser");
		test.assignAuthor("Liu Yuefen");

		spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);

		// create csr/direct/msp admin
//		String csr_user_id = spogServer.createUserAndCheck(spogServer.ReturnRandom("update_csr_yuefen_csr@spogqa.com"),
//				spogServer.ReturnRandom("aaAA88"), spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"),
//				"csr_admin", "", test);
		String direct_user_id = spogServer.createUserAndCheck(
				spogServer.ReturnRandom("spogServer_update_direct_yuefen@spogqa.com"),
				spogServer.ReturnRandom("abyYc3"), spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"),
				"direct_admin", directOrg_id, test);
		String msp_user_id = spogServer.createUserAndCheck(
				spogServer.ReturnRandom("spogServer_update_msp_yuefen@spogqa.com"), spogServer.ReturnRandom("abyYc3"),
				spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "msp_admin", mspOrg_id, test);
		String account_user_id = spogServer.createUserAndCheck(
				spogServer.ReturnRandom("spogServer_update_account_yuefen@spogqa.com"),
				spogServer.ReturnRandom("abyYc3"), spogServer.ReturnRandom("yuefen_account"),
				spogServer.ReturnRandom("liu"), "direct_admin", account_id, test);
		// create account user 2 under the same msp account
		String logInUserName = spogServer.ReturnRandom("user2_yuefen_account@spogqa.com");
		String logInPassword = spogServer.ReturnRandom("aaAAcc11");
		String account_user2_id = spogServer.createUserAndCheck(logInUserName, logInPassword,
				spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "direct_admin", account_id, test);
		// login with account user2
		spogServer.userLogin(logInUserName, logInPassword, SpogConstants.SUCCESS_LOGIN, test);

//		// update csr
//		response = spogServer.updateUserById(csr_user_id, "", "", spogServer.ReturnRandom("yuefen_csr_update"),
//				spogServer.ReturnRandom("liu_update_csr"), "csr_admin", "", test);
//		spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
//		spogServer.checkErrorCode(response, "00100101");

		// update direct admin
		response = spogServer.updateUserById(direct_user_id, "", "", spogServer.ReturnRandom("yuefen_csr_update"),
				spogServer.ReturnRandom("liu_update_csr"), "csr_admin", "", test);
		spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
		spogServer.checkErrorCode(response, "00100101");

		// update another user under the same account
		response = spogServer.updateUserById(account_user_id, "", "", spogServer.ReturnRandom("yuefen_direct_update"),
				spogServer.ReturnRandom("liu_update_direct"), "direct_admin", "", test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);

		// update msp
		response = spogServer.updateUserById(msp_user_id, "", "", spogServer.ReturnRandom("yuefen_msp_update"),
				spogServer.ReturnRandom("liu_update_msp"), "msp_admin", "", test);
		spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
		spogServer.checkErrorCode(response, "00100101");

		// update itself
		response = spogServer.updateUserById(account_user2_id, "", "", spogServer.ReturnRandom("yuefen_update_itself"),
				spogServer.ReturnRandom("liu_update_itself"), "direct_admin", "", test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);

		// create another user under accout2
		spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		String acccount2_user_id = spogServer.createUserAndCheck(
				spogServer.ReturnRandom("user_account2_yuefen@spogqa.com"), spogServer.ReturnRandom("aaAAbbCC11"),
				spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"), "direct_admin", account2_id, test);
		// login with account
		spogServer.userLogin(logInUserName, logInPassword, SpogConstants.SUCCESS_LOGIN, test);
		// update user under another account
		response = spogServer.updateUserById(acccount2_user_id, "", "",
				spogServer.ReturnRandom("yuefen_update_account2"), spogServer.ReturnRandom("liu_update_account"),
				"direct_admin", "", test);
		spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test);
		spogServer.checkErrorCode(response, "00100101");
		
		//delete user
		spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		spogServer.CheckDeleteUserByIdStatus(direct_user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		spogServer.CheckDeleteUserByIdStatus(acccount2_user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		spogServer.CheckDeleteUserByIdStatus(msp_user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		spogServer.CheckDeleteUserByIdStatus(account_user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		spogServer.CheckDeleteUserByIdStatus(account_user2_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	}

	@Test
	public void canNotChangeUserRole() {
		System.out.println("canNotChangeUserRole");
		test = rep.startTest("canNotChangeUserRole");
		test.assignAuthor("Liu Yuefen");

		spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		// can't change csr to direct or msp role
		// 1. create csr user and change role to direct or msp
//		String csr_user_id = spogServer.createUserAndCheck(spogServer.ReturnRandom("role_csr_yuefen_csr@spogqa.com"),
//				spogServer.ReturnRandom("aaAA88"), spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"),
//				"csr_admin", "", test);
//		Response response = spogServer.updateUserById(csr_user_id, "", "", "", "", "direct_admin", "", test);
//		spogServer.checkResponseStatus(response, SpogConstants.NOT_ALLOWED_ON_RESOURCE, test);
//		spogServer.checkErrorCode(response, "00200012");
//
//		response = spogServer.updateUserById(csr_user_id, "", "", "", "", "msp_admin", "", test);
//		spogServer.checkResponseStatus(response, SpogConstants.NOT_ALLOWED_ON_RESOURCE, test);
//		spogServer.checkErrorCode(response, "00200012");
//
//		// csr can update itself - firstName/lastName
//		String newFirstName = spogServer.ReturnRandom("itself_yuefen");
//		String newLastName = spogServer.ReturnRandom("itself_liu");
//		response = spogServer.updateUserById(csr_user_id, "", "", newFirstName, newLastName, "", "", test);
//		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
//		response.then().body("data.first_name", equalTo(newFirstName));
//		response.then().body("data.last_name", equalTo(newLastName));

		// 2. create msp user and change role to csr or direct
		String msp_user_id = spogServer.createUserAndCheck(spogServer.ReturnRandom("role_msp_yuefen_csr@spogqa.com"),
				spogServer.ReturnRandom("aaAA88"), spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"),
				"msp_admin", mspOrg_id, test);
		response = spogServer.updateUserById(msp_user_id, "", "", "", "", "direct_admin", "", test);
		spogServer.checkResponseStatus(response, SpogConstants.NOT_ALLOWED_ON_RESOURCE, test);
		spogServer.checkErrorCode(response, "00200012");

		response = spogServer.updateUserById(msp_user_id, "", "", "", "", "csr_admin", "", test);
		spogServer.checkResponseStatus(response, SpogConstants.NOT_ALLOWED_ON_RESOURCE, test);
		spogServer.checkErrorCode(response, "00200012");

		// 3. create direct user and change role to msp or csr
		String direct_user_id = spogServer.createUserAndCheck(spogServer.ReturnRandom("role_msp_yuefen_csr@spogqa.com"),
				spogServer.ReturnRandom("aaAA88"), spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"),
				"direct_admin", directOrg_id, test);
		response = spogServer.updateUserById(direct_user_id, "", "", "", "", "csr_admin", "", test);
		spogServer.checkResponseStatus(response, SpogConstants.NOT_ALLOWED_ON_RESOURCE, test);
		spogServer.checkErrorCode(response, "00200012");

		response = spogServer.updateUserById(direct_user_id, "", "", "", "", "msp_admin", "", test);
		spogServer.checkResponseStatus(response, SpogConstants.NOT_ALLOWED_ON_RESOURCE, test);
		spogServer.checkErrorCode(response, "00200012");

		response = spogServer.updateUserById(direct_user_id, "", "", "", "", "msp_account_admin", "", test);
		spogServer.checkResponseStatus(response, SpogConstants.NOT_ALLOWED_ON_RESOURCE, test);
		spogServer.checkErrorCode(response, "00200012");
		
		//delete user
		spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		spogServer.CheckDeleteUserByIdStatus(msp_user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		spogServer.CheckDeleteUserByIdStatus(direct_user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	}

	@Test
	public void updateIdNotExistOrInvalid() {
		System.out.println("updateIdNotExistOrInvalid");
		test = rep.startTest("updateIdNotExistOrInvalid");
		test.assignAuthor("Liu Yuefen");

		spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);

		Response response = spogServer.updateUserById("", "", "", spogServer.ReturnRandom("yuefen_405"), "", "", "",
				test);
		spogServer.checkResponseStatus(response, SpogConstants.NOT_ALLOWED_ON_RESOURCE, test);
		spogServer.checkErrorCode(response, "00900002");

		response = spogServer.updateUserById(null, "", "", spogServer.ReturnRandom("yuefen_400"), "", "", "", test);
		spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
		spogServer.checkErrorCode(response, "40000005");

		response = spogServer.updateUserById("123456789", "", "", spogServer.ReturnRandom("yuefen_400"), "", "", "",
				test);
		spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
		spogServer.checkErrorCode(response, "40000005");

		// create user
		String user_id = spogServer.createUserAndCheck(
				spogServer.ReturnRandom("direct_update_yuefen_idHasProblem@spogqa.com"),
				spogServer.ReturnRandom("aaAA33GG"), spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"),
				"direct_admin", directOrg_id, test);
		System.out.println("update user with all parameters =null");
		response = spogServer.updateUserById(user_id, "", "", "", "", "", "", test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		// call api without body
		response = spogServer.updateUserByIdFWithBodyNull(user_id);
		spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
		spogServer.checkErrorCode(response, "00100002");
		// delete the user
		spogServer.CheckDeleteUserByIdStatus(user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);

		// update the deleted user
		response = spogServer.updateUserById(user_id, "", "", "yuefen", "", "", "", test);
		spogServer.checkResponseStatus(response, SpogConstants.RESOURCE_NOT_EXIST, test);
		spogServer.checkErrorCode(response, "00200007");

	}

	@Test
	public void updatePassword() {
		System.out.println("updatePassword");
		test = rep.startTest("updatePassword");
		test.assignAuthor("Liu Yuefen");

		spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);

		// create direct user
		String email = spogServer.ReturnRandom("passwordcheck_direct_yuefen_csr@spogqa.com");
		String password = spogServer.ReturnRandom("aaAA88");
		String direct_user_id = spogServer.createUserAndCheck(email, password, spogServer.ReturnRandom("yuefen"),
				spogServer.ReturnRandom("liu"), "direct_admin", directOrg_id, test);

		// update password=""
		Response response = spogServer.updateUserById(direct_user_id, "", "", "", "", "", "", test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		spogServer.userLogin(email, password, SpogConstants.SUCCESS_LOGIN, test);

		// update password=null
		response = spogServer.updateUserById(direct_user_id, "", null, "", "", "", "", test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		spogServer.userLogin(email, password, SpogConstants.SUCCESS_LOGIN, test);

		// update password length<8
		response = spogServer.updateUserById(direct_user_id, "", "Aa1", "", "", "", "", test);
		spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
		spogServer.checkErrorCode(response, "40000002");

		// update password length>20
		response = spogServer.updateUserById(direct_user_id, "", "AAaaccddee1234567890ff", "", "", "", "", test);
		spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
		spogServer.checkErrorCode(response, "40000002");

		// update password not contain number
		response = spogServer.updateUserById(direct_user_id, "", "AAaaccDDff", "", "", "", "", test);
		spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
		spogServer.checkErrorCode(response, "40000003");

		// update password not contain upper case
		response = spogServer.updateUserById(direct_user_id, "", "aabbcc123", "", "", "", "", test);
		spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
		spogServer.checkErrorCode(response, "40000003");

		// update password not contain lower case
		response = spogServer.updateUserById(direct_user_id, "", "AABBCC123", "", "", "", "", test);
		spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
		spogServer.checkErrorCode(response, "40000003");

		// update password with special case
		String newPassword = spogServer.ReturnRandom("aaBB@123");
		response = spogServer.updateUserById(direct_user_id, "", newPassword, "", "", "", "", test);
		spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		spogServer.userLogin(email, newPassword, SpogConstants.SUCCESS_LOGIN, test);
		
		//delete user
		spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		spogServer.CheckDeleteUserByIdStatus(direct_user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	}

	@Test
	public void updateUserWithoutLogin() {
		System.out.println("canNotChangeUserRole");
		test = rep.startTest("canNotChangeUserRole");
		test.assignAuthor("Liu Yuefen");

		spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);

		// create direct user
		String direct_user_id = spogServer.createUserAndCheck(spogServer.ReturnRandom("401_direct_yuefen@spogqa.com"),
				spogServer.ReturnRandom("aaAA88"), spogServer.ReturnRandom("yuefen"), spogServer.ReturnRandom("liu"),
				"direct_admin", directOrg_id, test);
		// set token as null

		spogServer.setToken("");
		// call update user by id api
		Response response = spogServer.updateUserById(direct_user_id, "", "", "yuefen_401", "", "", "", test);
		spogServer.checkResponseStatus(response, SpogConstants.NOT_LOGGED_IN, test);
		spogServer.checkErrorCode(response, "00900006");
		
		//delete user
		spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		spogServer.CheckDeleteUserByIdStatus(direct_user_id, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	}

	/****************************
	 * Preference language cases - Sprint 34
	 ********************************************/
	
	@DataProvider(name = "updateUserPreferenceLanguageCases")
	public Object[][] updateUserPreferenceLanguageCases() {
		return new Object[][] {
				// 200
				{ "update user in direct organization with preferred language English", direct_email, sharePassword,
						SpogConstants.DIRECT_ADMIN, directOrg_id, "en", SpogConstants.SUCCESS_GET_PUT_DELETE, null, "ja" },
				{ "update user in direct organization with preferred language Japanese", direct_email, sharePassword,
						SpogConstants.DIRECT_ADMIN, directOrg_id, "ja", SpogConstants.SUCCESS_GET_PUT_DELETE, null, "en" },
				{ "update msp user in MSP organization with preferred language English", msp_email, sharePassword,
						SpogConstants.MSP_ADMIN, mspOrg_id, "en", SpogConstants.SUCCESS_GET_PUT_DELETE, null, "ja" },
				{ "update msp user in MSP organization with preferred language Japanese", msp_email, sharePassword,
						SpogConstants.MSP_ADMIN, mspOrg_id, "ja", SpogConstants.SUCCESS_GET_PUT_DELETE, null, "en" },
				{ "update msp account admin user in MSP organization with preferred language English", msp_email,
						sharePassword, SpogConstants.MSP_ACCOUNT_ADMIN, mspOrg_id, "en", SpogConstants.SUCCESS_GET_PUT_DELETE,
						null, "ja" },
				{ "update msp account admin user in MSP organization with preferred language Japanese", msp_email,
						sharePassword, SpogConstants.MSP_ACCOUNT_ADMIN, mspOrg_id, "ja", SpogConstants.SUCCESS_GET_PUT_DELETE,
						null, "en" },
				{ "update customer admin user in sub organization using msp token with preferred language English",
						msp_email, sharePassword, SpogConstants.DIRECT_ADMIN, account_id, "en",
						SpogConstants.SUCCESS_GET_PUT_DELETE, null, "ja" },
				{ "update customer user in sub organization using msp token with preferred language Japanese",
						msp_email, sharePassword, SpogConstants.DIRECT_ADMIN, account_id, "ja",
						SpogConstants.SUCCESS_GET_PUT_DELETE, null, "en" },
				{ "update direct admin user in sub organization using sub org token with preferred language English",
						accountDirect_email, sharePassword, SpogConstants.DIRECT_ADMIN, account_id, "en",
						SpogConstants.SUCCESS_GET_PUT_DELETE, null, "ja" },
				{ "update direct admin user in sub organization using sub org token with preferred language Japanese",
						accountDirect_email, sharePassword, SpogConstants.DIRECT_ADMIN, account_id, "ja",
						SpogConstants.SUCCESS_GET_PUT_DELETE, null, "en" },

				// preferred language value none - not adding to payload
				{ "update user in direct organization with preferred language none", direct_email, sharePassword,
						SpogConstants.DIRECT_ADMIN, directOrg_id, "none", SpogConstants.SUCCESS_GET_PUT_DELETE, null, "en" },
				{ "update msp user in MSP organization with preferred language none", msp_email, sharePassword,
						SpogConstants.MSP_ADMIN, mspOrg_id, "none", SpogConstants.SUCCESS_GET_PUT_DELETE, null, "ja" },
				{ "update msp account admin user in MSP organization with preferred language none", msp_email,
						sharePassword, SpogConstants.MSP_ACCOUNT_ADMIN, mspOrg_id, "none", SpogConstants.SUCCESS_GET_PUT_DELETE,
						null, "en" },
				{ "update customer admin user in sub organization using msp token with preferred language none",
						msp_email, sharePassword, SpogConstants.DIRECT_ADMIN, account_id, "none",
						SpogConstants.SUCCESS_GET_PUT_DELETE, null, "ja" },
				{ "update direct admin user in sub organization using sub org token with preferred language none",
						accountDirect_email, sharePassword, SpogConstants.DIRECT_ADMIN, account_id, "none",
						SpogConstants.SUCCESS_GET_PUT_DELETE, null, "en" },

				// preferred language value null
				{ "update user in direct organization with preferred language null", direct_email, sharePassword,
						SpogConstants.DIRECT_ADMIN, directOrg_id, null, SpogConstants.SUCCESS_GET_PUT_DELETE, null, "en" },
				{ "update msp user in MSP organization with preferred language null", msp_email, sharePassword,
						SpogConstants.MSP_ADMIN, mspOrg_id, null, SpogConstants.SUCCESS_GET_PUT_DELETE, null, "ja" },
				{ "update msp account admin user in MSP organization with preferred language null", msp_email,
						sharePassword, SpogConstants.MSP_ACCOUNT_ADMIN, mspOrg_id, null, SpogConstants.SUCCESS_GET_PUT_DELETE,
						null, "en" },
				{ "update customer admin user in sub organization using msp token with preferred language null",
						msp_email, sharePassword, SpogConstants.DIRECT_ADMIN, account_id, null,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null, "ja" },
				{ "update direct admin user in sub organization using sub org token with preferred language null",
						accountDirect_email, sharePassword, SpogConstants.DIRECT_ADMIN, account_id, null,
						SpogConstants.SUCCESS_GET_PUT_DELETE, null, "en" },

				// preferred language value empty string
				{ "update user in direct organization with preferred language empty string", direct_email,
						sharePassword, SpogConstants.DIRECT_ADMIN, directOrg_id, "",
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.PREFERENCE_LANGUAGE_FORMAT_DOESNOT_MATCH,
						"ja" },
				{ "update msp user in MSP organization with preferred language empty string", msp_email, sharePassword,
						SpogConstants.MSP_ADMIN, mspOrg_id, "", SpogConstants.REQUIRED_INFO_NOT_EXIST,
						SpogMessageCode.PREFERENCE_LANGUAGE_FORMAT_DOESNOT_MATCH, "en" },
				{ "update msp account admin user in MSP organization with preferred language empty string", msp_email,
						sharePassword, SpogConstants.MSP_ACCOUNT_ADMIN, mspOrg_id, "",
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.PREFERENCE_LANGUAGE_FORMAT_DOESNOT_MATCH,
						"ja" },
				{ "update customer admin user in sub organization using msp token with preferred language empty string",
						msp_email, sharePassword, SpogConstants.DIRECT_ADMIN, account_id, "",
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.PREFERENCE_LANGUAGE_FORMAT_DOESNOT_MATCH,
						"en" },
				{ "update direct admin user in sub organization using sub org token with preferred language empty string",
						accountDirect_email, sharePassword, SpogConstants.DIRECT_ADMIN, account_id, "",
						SpogConstants.REQUIRED_INFO_NOT_EXIST, SpogMessageCode.PREFERENCE_LANGUAGE_FORMAT_DOESNOT_MATCH,
						"ja" }, };
	}

	@Test(dataProvider = "updateUserPreferenceLanguageCases")
	public void updateUserTest(String caseType, String loginUserName, String loginPassword, String role_id,
			String organization_id, String update_language, int expectedStatusCode,
			SpogMessageCode expectedErrorMessage, String preference_language) {

		test = rep.startTest(caseType);
		test.assignAuthor("Rakesh.Chalamala");
		spogServer.userLogin(loginUserName, loginPassword, test);

		String first_name = spogServer.ReturnRandom("first");
		String last_name = spogServer.ReturnRandom("last");
		String email = spogServer.ReturnRandom("email") + "@spogqa.com";
		String phone = "9696969696";
		String password = loginPassword;

		String user_id = spogServer.createUserWithCheck(first_name, last_name, email, phone, role_id, organization_id,
				preference_language, password, SpogConstants.SUCCESS_POST, null, test);

		Response response = spogServer.updateUserById(user_id, email, null, first_name, last_name, role_id,
				organization_id, phone, update_language, expectedStatusCode, expectedErrorMessage,test);
		
		if (expectedStatusCode == SpogConstants.SUCCESS_GET_PUT_DELETE) {
			if (update_language == null || update_language.equalsIgnoreCase("none") || update_language.isEmpty()) {
				response.then().body("data.preference_language", equalTo(preference_language));
			}else {
				response.then().body("data.preference_language", equalTo(update_language));
			}
		}
	}

	/************************************
	 * end
	 ********************************************/

	@AfterMethod
	public void getResult(ITestResult result) {
		if (result.getStatus() == ITestResult.FAILURE) {
			count1.setfailedcount();
			test.log(LogStatus.FAIL, "Test Case Failed is " + result.getName() + " with parameters as "
					+ Arrays.asList(result.getParameters()));
			test.log(LogStatus.FAIL, result.getThrowable().getMessage());
		} else if (result.getStatus() == ITestResult.SKIP) {
			count1.setskippedcount();
			test.log(LogStatus.SKIP, "Test Case Skipped is " + result.getName());
		} else if (result.getStatus() == ITestResult.SUCCESS) {
			count1.setpassedcount();
		}
		// ending test
		// endTest(logger) : It ends the current test and prepares to create HTML report
		rep.endTest(test);
	}
}
