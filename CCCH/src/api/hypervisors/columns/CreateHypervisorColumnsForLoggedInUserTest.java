package api.hypervisors.columns;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

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
import InvokerServer.Source4SPOGServer;
import InvokerServer.Org4SPOGServer;
import InvokerServer.UserSpogServer;
import dataPreparation.JsonPreparation;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;

public class CreateHypervisorColumnsForLoggedInUserTest extends base.prepare.Is4Org  {
	private static JsonPreparation jp = new JsonPreparation();
	  private SPOGServer spogServer;
	  private Source4SPOGServer source4SpogServer;
	  private Org4SPOGServer org4SpogServer;
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
//	  private ExtentReports rep;
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
			System.out.println("CreateHypervisorColumnsForLoggedInUserTest");
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
		  spogServer = new SPOGServer(baseURI, port);
		  source4SpogServer =new Source4SPOGServer(baseURI,port);
		  userSpogServer = new UserSpogServer(baseURI, port);
		  org4SpogServer = new Org4SPOGServer(baseURI, port);
		  rep = ExtentManager.getInstance("CreateHypervisorColumnsForLoggedInUserTest",logFolder);
		  test = rep.startTest("beforeClass");
		  spogServer.userLogin(userName, password);
		  csrToken= spogServer.getJWTToken();
		  this.csrGlobalLoginUser = userName;
		  this.csrGlobalLoginPassword = password;
		  //create org
		  csrOrg_id = spogServer.GetLoggedinUserOrganizationID();
		  System.out.println("csr org id:"+csrOrg_id);
		  directOrg_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_group_direct1_yuefen")+org_prefix,"direct",spogServer.ReturnRandom("yuefen_direct1_group@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("direct_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  directOrg2_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_group_direct2_yuefen")+org_prefix,"direct",spogServer.ReturnRandom("yuefen_direct2_group@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("direct2_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  mspOrg_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_group_yuefen_msp1")+org_prefix,"msp",spogServer.ReturnRandom("yuefen_msp1_group@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("msp_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  mspOrg2_id = spogServer.CreateOrganizationWithCheck(spogServer.ReturnRandom("spogqa_group_yuefen_msp2")+org_prefix,"msp",spogServer.ReturnRandom("yuefen_msp2_group@spogqa.com"),spogServer.ReturnRandom("aaQQdsf11"),spogServer.ReturnRandom("msp2_spogqa"),spogServer.ReturnRandom("liu_spogqa"));
		  account_id = spogServer.createAccountWithCheck(mspOrg_id,spogServer.ReturnRandom("spogqa_accoun1_msp1_yuefen_group")+org_prefix, mspOrg_id, test);
		  account2_id = spogServer.createAccountWithCheck(mspOrg2_id, spogServer.ReturnRandom("spogqa_accoun2_msp2_yuefen_spogqa")+org_prefix, mspOrg2_id, test);

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
		  
		  //create direct user
//		  this.direct_email = spogServer.ReturnRandom("direct_yuefen_group@spogqa.com");
//		  spogServer.createUserAndCheck(direct_email, sharePassword, "yuefen", "liu", "direct_admin", directOrg_id, test);
//		  //create msp1 user
//		  this.msp_email = spogServer.ReturnRandom("msp1_yuefen_group@spogqa.com");
//		  spogServer.createUserAndCheck(msp_email, sharePassword, "yuefen", "liu", "msp_admin", mspOrg_id, test);
//		  //create account user
//		  this.accountDirect_email = spogServer.ReturnRandom("account1_msp1_yuefen_group@spogqa.com");
//		  spogServer.createUserAndCheck(accountDirect_email, sharePassword, "yuefen", "liu", "direct_admin", account_id, test);
//		  
	  }
	
	  @DataProvider(name = "columnInfo")
	  public final Object[][] getColumnsInfo() {
		  return new Object[][] { 
		      {spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;1,970ae3ec-040a-498b-9171-5ff688a155b5;true;2"},
		      {spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263;false;1,970ae3ec-040a-498b-9171-5ff688a155b5;false;1"},
		      {spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263;abc;1,970ae3ec-040a-498b-9171-5ff688a155b5;abc;1"},
		      {spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;1,970ae3ec-040a-498b-9171-5ff688a155b5;true;1"},
		      {spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;1,970ae3ec-040a-498b-9171-5ff688a155b5;true;5"},
			  {spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;2,970ae3ec-040a-498b-9171-5ff688a155b5;true;1"},
			  {spogServer.ReturnRandom("csr_admin_yuefen_group@spogqa.com"), sharePassword,"csr_admin", csrOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263;;1"},
			  {spogServer.ReturnRandom("csr_read_only@spogqa.com"), sharePassword,"csr_read_only", csrOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263;;1"},
			  {spogServer.ReturnRandom("msp1_yuefen_group@spogqa.com"), sharePassword,"msp_admin", mspOrg_id,"46520ed0-8204-4b0f-8754-a9d4dfa82ce0;true;1,023283e9-0027-4f28-a260-573eed6b1b62;true;2"},
			  {spogServer.ReturnRandom("msp_account_admin_column@spogqa.com"), sharePassword,"msp_account_admin", mspOrg_id,"46520ed0-8204-4b0f-8754-a9d4dfa82ce0;true;1,023283e9-0027-4f28-a260-573eed6b1b62;true;2"},
			  {spogServer.ReturnRandom("account1_msp1_yuefen_group@spogqa.com"), sharePassword,"direct_admin", account_id,"8d101229-d32e-4d7f-bb09-355e96824f32;true;1,d065d8a3-699d-4c14-834d-65953942e778;true;2,4ca1cc37-bdd4-4427-a23f-9d55d48b3f44;true;3,8c8ecbce-322f-4d6b-99bb-db7bdcbfa8dd;true;4"},
			  {spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263;;1"},
			  {spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263;false;none"},
			  {spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;1,970ae3ec-040a-498b-9171-5ff688a155b5;true;2,46520ed0-8204-4b0f-8754-a9d4dfa82ce0;true;8,023283e9-0027-4f28-a260-573eed6b1b62;false;7,8d101229-d32e-4d7f-bb09-355e96824f32;true;3,d065d8a3-699d-4c14-834d-65953942e778;false;3,4ca1cc37-bdd4-4427-a23f-9d55d48b3f44;true;3,8c8ecbce-322f-4d6b-99bb-db7bdcbfa8dd;true;3"},
			  
			  {spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"msp_admin", root_msp1_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;1,970ae3ec-040a-498b-9171-5ff688a155b5;true;2"},
			  {spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"msp_admin", subMSP1_root1_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;1,970ae3ec-040a-498b-9171-5ff688a155b5;true;2"},
			  {spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"msp_account_admin", root_msp1_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;1,970ae3ec-040a-498b-9171-5ff688a155b5;true;2"},
			  {spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"msp_account_admin", subMSP1_root1_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;1,970ae3ec-040a-498b-9171-5ff688a155b5;true;2"},
			  {spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"direct_admin", account_subMSP1_root1_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;1,970ae3ec-040a-498b-9171-5ff688a155b5;true;2"},
			  {spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"direct_admin", account_rootMSP1_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;1,970ae3ec-040a-498b-9171-5ff688a155b5;true;2"},
		  };
		}
	  
	  @Test (dataProvider = "columnInfo") 
	  public void createHypervisorColumnsSuccess(String name, String password, String role, String org, String specifiedHypervisorColumns){	 
		  System.out.println("createHypervisorColumnsSuccess");
		  test = rep.startTest("createHypervisorColumnsSuccess");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login with csr user
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  //create user
		  System.out.println("create user");
		  String userId = spogServer.createUserAndCheck(name,password, "yuefen", "liu", role, org, test);
		  System.out.println("login");
		  spogServer.userLogin(name, password, SpogConstants.SUCCESS_LOGIN, test);
		  
		  String token = spogServer.getJWTToken();
		  System.out.println("token="+token);
		  
		  test.log(LogStatus.INFO,"get column array list");
		  ArrayList<HashMap<String, Object>> columnsList = source4SpogServer.getHypervisorColumnArrayList(specifiedHypervisorColumns, token);
		  
		  test.log(LogStatus.INFO,"create logged in user columns");
		  System.out.println("start create columns");
		  Response response = source4SpogServer.createHypervisorColumnsForLoggedInUser(columnsList, token);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		  
		  test.log(LogStatus.INFO,"delete logged in user columns");
		  System.out.println("delete columns");
		  response = source4SpogServer.deleteHypervisorColumnsForLoggedInUser(token);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test); 
		  
		  //delete user  
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  spogServer.CheckDeleteUserByIdStatus(userId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  }
	  
	  @DataProvider(name = "columnInfo4")
	  public final Object[][] getColumnsInfo4() {
		  return new Object[][] { 
			  {spogServer.ReturnRandom("direct_yuefen_user4@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263","true","1"},
			  {spogServer.ReturnRandom("direct_yuefen_user4@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263","false","2"},
			  {spogServer.ReturnRandom("direct_yuefen_user4@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"970ae3ec-040a-498b-9171-5ff688a155b5","true","1"},
			  {spogServer.ReturnRandom("direct_yuefen_user4@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"970ae3ec-040a-498b-9171-5ff688a155b5","false","2"},
			  {spogServer.ReturnRandom("direct_yuefen_user4@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"46520ed0-8204-4b0f-8754-a9d4dfa82ce0","true","1"},
			  {spogServer.ReturnRandom("direct_yuefen_user4@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"46520ed0-8204-4b0f-8754-a9d4dfa82ce0","false","2"},
			  {spogServer.ReturnRandom("direct_yuefen_user4@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"023283e9-0027-4f28-a260-573eed6b1b62","true","1"},
			  {spogServer.ReturnRandom("direct_yuefen_user4@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"023283e9-0027-4f28-a260-573eed6b1b62","false","2"},
			  {spogServer.ReturnRandom("direct_yuefen_user4@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"8d101229-d32e-4d7f-bb09-355e96824f32","true","1"},
			  {spogServer.ReturnRandom("direct_yuefen_user4@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"8d101229-d32e-4d7f-bb09-355e96824f32","false","2"},
			  {spogServer.ReturnRandom("direct_yuefen_user4@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"d065d8a3-699d-4c14-834d-65953942e778","true","1"},
			  {spogServer.ReturnRandom("direct_yuefen_user4@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"d065d8a3-699d-4c14-834d-65953942e778","false","2"},
			  {spogServer.ReturnRandom("direct_yuefen_user4@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"4ca1cc37-bdd4-4427-a23f-9d55d48b3f44","true","1"},
			  {spogServer.ReturnRandom("direct_yuefen_user4@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"4ca1cc37-bdd4-4427-a23f-9d55d48b3f44","false","2"},
			  {spogServer.ReturnRandom("direct_yuefen_user4@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"8c8ecbce-322f-4d6b-99bb-db7bdcbfa8dd","true","1"},
			  {spogServer.ReturnRandom("direct_yuefen_user4@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"8c8ecbce-322f-4d6b-99bb-db7bdcbfa8dd","false","2"}  
		  };
	  }
		  @Test (dataProvider = "columnInfo4") 
		  public void createHypervisorColumnsCheck(String name, String password, String role, String org, String column_id,
				  String visible, String order_id){	 
			  System.out.println("createHypervisorColumnsCheck");
			  test = rep.startTest("createHypervisorColumnsCheck");
			  test.assignAuthor("Liu Yuefen");
			  
			  //login with csr user
			  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
			  //create user
			  System.out.println("create user");
			  String userId = spogServer.createUserAndCheck(name,password, "yuefen", "liu", role, org, test);
			  System.out.println("login");
			  spogServer.userLogin(name, password, SpogConstants.SUCCESS_LOGIN, test);
			  
			  String token = spogServer.getJWTToken();
			  System.out.println("token="+token);
			  
			  test.log(LogStatus.INFO,"compose column array list");
			  ArrayList<HashMap<String, Object>> columnsList = new ArrayList<HashMap<String, Object>>();
			  HashMap<String, Object> columnInfo = source4SpogServer.getHypervisorColumnInfo(column_id, visible, order_id);
			  columnsList.add(columnInfo);
			  
			  test.log(LogStatus.INFO,"create logged in user columns");
			  System.out.println("start create columns");
			  Response response = source4SpogServer.createHypervisorColumnsForLoggedInUser(columnsList, token);
			  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
			  
			  //check
			  ArrayList<HashMap<String, Object>> actualColumnsList = new ArrayList<HashMap<String, Object>>();
			  actualColumnsList = response.then().extract().path("data");
			  HashMap<String, Object> actualColumnInfo = actualColumnsList.get(0);
			  assertEquals(actualColumnInfo.get("column_id").toString(), column_id.toString());
			  assertEquals(actualColumnInfo.get("visible").toString(), visible.toString());
			  assertEquals(actualColumnInfo.get("order_id").toString(), order_id.toString());
	
			  test.log(LogStatus.INFO,"delete logged in user columns");
			  System.out.println("delete columns");
			  response = source4SpogServer.deleteHypervisorColumnsForLoggedInUser(token);
			  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test); 
			  
			  //delete user  
			  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
			  spogServer.CheckDeleteUserByIdStatus(userId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  }
		  
		  @DataProvider(name = "columnInfo5")
		  public final Object[][] getColumnsInfo5() {
			  return new Object[][] { 
				  
				  {spogServer.ReturnRandom("direct_yuefen_user5@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"8c8ecbce-322f-4d6b-99bb-db7bdcbfa8dd","false","none"},
				  {spogServer.ReturnRandom("direct_yuefen_user5@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"8c8ecbce-322f-4d6b-99bb-db7bdcbfa8dd","none","8"}
			  };
		  }
		  @Test (dataProvider = "columnInfo5") 
		  public void createHypervisorColumnsNullCheck(String name, String password, String role, String org, String column_id,
				  String visible, String order_id){	 
			  System.out.println("createHypervisorColumnsNullCheck");
			  test = rep.startTest("createHypervisorColumnsNullCheck");
			  test.assignAuthor("Liu Yuefen");
			  
			  //login with csr user
			  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
			  //create user
			  System.out.println("create user");
			  String userId = spogServer.createUserAndCheck(name,password, "yuefen", "liu", role, org, test);
			  System.out.println("login");
			  spogServer.userLogin(name, password, SpogConstants.SUCCESS_LOGIN, test);
			  
			  String token = spogServer.getJWTToken();
			  System.out.println("token="+token);
			  
			  test.log(LogStatus.INFO,"compose column array list");
			  ArrayList<HashMap<String, Object>> columnsList = new ArrayList<HashMap<String, Object>>();
			  HashMap<String, Object> columnInfo = source4SpogServer.getHypervisorColumnInfo(column_id, visible, order_id);
			  columnsList.add(columnInfo);
			  
			  test.log(LogStatus.INFO,"create logged in user columns");
			  System.out.println("start create columns");
			  Response response = source4SpogServer.createHypervisorColumnsForLoggedInUser(columnsList, token);
			  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
	
			  test.log(LogStatus.INFO,"delete logged in user columns");
			  System.out.println("delete columns");
			  response = source4SpogServer.deleteHypervisorColumnsForLoggedInUser(token);
			  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test); 
			  
			  //delete user  
			  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
			  spogServer.CheckDeleteUserByIdStatus(userId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  }
	  @DataProvider(name = "columnInfo1")
	  public final Object[][] getColumnsInfo1() {
		  return new Object[][] { {spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;1,970ae3ec-040a-498b-9171-5ff688a155b5;true;2"}
			  
		  };
		  }
	  
	  @Test (dataProvider = "columnInfo1") 
	  public void createHypervisorColumnsDuplicate(String name, String password, String role, String org, String specifiedHypervisorColumns){	 
		  System.out.println("createHypervisorColumnsDuplicate");
		  test = rep.startTest("createHypervisorColumnsDuplicate");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login with csr user
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  //create user
		  System.out.println("create user");
		  String userId = spogServer.createUserAndCheck(name,password, "yuefen", "liu", role, org, test);
		  System.out.println("login");
		  spogServer.userLogin(name, password, SpogConstants.SUCCESS_LOGIN, test);
		  
		  String token = spogServer.getJWTToken();
		  System.out.println("token="+token);
		  
		  test.log(LogStatus.INFO,"get column array list");
		  ArrayList<HashMap<String, Object>> columnsList = source4SpogServer.getHypervisorColumnArrayList(specifiedHypervisorColumns, token);
		  
		  test.log(LogStatus.INFO,"create logged in user columns");
		  System.out.println("start create columns");
		  Response response = source4SpogServer.createHypervisorColumnsForLoggedInUser(columnsList, token);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		  
		  //create column duplicate
		  response = source4SpogServer.createHypervisorColumnsForLoggedInUser(columnsList, token);
		  spogServer.checkResponseStatus(response, SpogConstants.REQUIRED_INFO_NOT_EXIST, test);
		  spogServer.checkErrorCode(response, "00D00001");
		  
		  test.log(LogStatus.INFO,"delete logged in user columns");
		  System.out.println("delete columns");
		  response = source4SpogServer.deleteHypervisorColumnsForLoggedInUser(token);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test); 
		  //delete user
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  spogServer.CheckDeleteUserByIdStatus(userId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  }
		  
	  @DataProvider(name = "columnInfo2")
	  public final Object[][] getColumnsInfo2() {
		  return new Object[][] { 
		      {spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;0,970ae3ec-040a-498b-9171-5ff688a155b5;true;2",SpogConstants.REQUIRED_INFO_NOT_EXIST,"40000016"},
			  {spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;1,970ae3ec-040a-498b-9171-5ff688a155b5;true;0",SpogConstants.REQUIRED_INFO_NOT_EXIST,"40000016"},
			  {spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;-2",SpogConstants.REQUIRED_INFO_NOT_EXIST,"40000016"},
			  {spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;1,970ae3ec-040a-498b-9171-5ff688a155b5;true;9",SpogConstants.REQUIRED_INFO_NOT_EXIST,"00D00006"},
			  {spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"00000000-0000-0000-0000-000000000000;true;1,970ae3ec-040a-498b-9171-5ff688a155b5;true;2",SpogConstants.RESOURCE_NOT_EXIST,"00D00003"},
			  {spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"direct_admin", directOrg_id,";true;1",SpogConstants.REQUIRED_INFO_NOT_EXIST,"40000001"},
			  {spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"null;true;1,970ae3ec-040a-498b-9171-5ff688a155b5;true;2",SpogConstants.REQUIRED_INFO_NOT_EXIST,"40000005"},
			  {spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"none;true;1,970ae3ec-040a-498b-9171-5ff688a155b5;true;2",SpogConstants.REQUIRED_INFO_NOT_EXIST,"40000005"}  
		  };
		  }
	  
	  @Test (dataProvider = "columnInfo2") 
	  public void createHypervisorColumnsFail(String name, String password, String role, String org, String specifiedHypervisorColumns,
			  int expectedStatuscode, String expectedErrorCode){	 
		  System.out.println("createHypervisorColumnsFail");
		  test = rep.startTest("createHypervisorColumnsFail");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login with csr user
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  //create user
		  System.out.println("create user");
		  String userId = spogServer.createUserAndCheck(name,password, "yuefen", "liu", role, org, test);
		  System.out.println("login");
		  spogServer.userLogin(name, password, SpogConstants.SUCCESS_LOGIN, test);
		  
		  String token = spogServer.getJWTToken();
		  System.out.println("token="+token);
		  
		  test.log(LogStatus.INFO,"get column array list");
		  ArrayList<HashMap<String, Object>> columnsList = source4SpogServer.getHypervisorColumnArrayList(specifiedHypervisorColumns, token);
		  
		  test.log(LogStatus.INFO,"create logged in user columns");
		  System.out.println("start create columns");
		  Response response = source4SpogServer.createHypervisorColumnsForLoggedInUser(columnsList, token);
		  spogServer.checkResponseStatus(response, expectedStatuscode, test);
		  spogServer.checkErrorCode(response,  expectedErrorCode);
		
		  test.log(LogStatus.INFO,"delete logged in user columns");
		  System.out.println("delete columns");
		  response = source4SpogServer.deleteHypervisorColumnsForLoggedInUser(token);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test); 
		  //delete user
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  spogServer.CheckDeleteUserByIdStatus(userId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  }
	  
	  @DataProvider(name = "columnInfo3")
	  public final Object[][] getColumnsInfo3() {
		  return new Object[][] { {spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;1,970ae3ec-040a-498b-9171-5ff688a155b5;true;2",SpogConstants.NOT_LOGGED_IN,"00900006"}
			  
		  };
		  }
	  @Test (dataProvider = "columnInfo3") 
	  public void createHypervisorColumnsFail401(String name, String password, String role, String org, String specifiedHypervisorColumns,
			  int expectedStatuscode, String expectedErrorCode){	 
		  System.out.println("createHypervisorColumnsFail");
		  test = rep.startTest("createHypervisorColumnsFail");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login with csr user
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  
		  String token = spogServer.getJWTToken();
		  System.out.println("token="+token);
		  
		  test.log(LogStatus.INFO,"get column array list");
		  ArrayList<HashMap<String, Object>> columnsList = source4SpogServer.getHypervisorColumnArrayList(specifiedHypervisorColumns, token);
		  
		  token = "";
		  
		  test.log(LogStatus.INFO,"create logged in user columns");
		  System.out.println("start create columns");
		  Response response = source4SpogServer.createHypervisorColumnsForLoggedInUser(columnsList, token);
		  spogServer.checkResponseStatus(response, expectedStatuscode, test);
		  spogServer.checkErrorCode(response,  expectedErrorCode);
		
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
