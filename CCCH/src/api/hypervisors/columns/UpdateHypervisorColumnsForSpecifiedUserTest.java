package api.hypervisors.columns;

import static org.testng.Assert.assertEquals;

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

import Constants.SpogConstants;
import InvokerServer.SPOGServer;
import InvokerServer.UserSpogServer;
import InvokerServer.Source4SPOGServer;
import dataPreparation.JsonPreparation;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;

public class UpdateHypervisorColumnsForSpecifiedUserTest extends base.prepare.PrepareOrgInfo{
	@Parameters({ "pmfKey"})
	  public UpdateHypervisorColumnsForSpecifiedUserTest(String pmfKey) {
			super(pmfKey);
			// TODO Auto-generated constructor stub
		}
	 private static JsonPreparation jp = new JsonPreparation();
	  private SPOGServer spogServer;
	  private UserSpogServer userSpogServer;
	  private Source4SPOGServer source4SpogServer;
	  private String csrOrg_id;
	  private String directOrg_id;
	  private String directOrg2_id;
	  private String mspOrg_id;
	  private String mspOrg2_id;
	  private String account_id;
	  private String account2_id;
	  private String msp_email;
	  private String msp_account_admin_email;
	  private String msp_account_admin_id;
	  private String direct_email;
	  private String accountDirect_email;
	  private String csrReadOnlyUser = "liuyu05@arcserve.com";
	  private String sharePassword = "Caworld_2018";
//	  private ExtentReports rep;
	  private ExtentTest test;
	  private Response response;
	  private String csrGlobalLoginUser;
	  private String csrGlobalLoginPassword;
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
			System.out.println("UpdateHypervisorColumnsForSpecifiedUserTest");
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
		  userSpogServer =new UserSpogServer(baseURI,port);
		  rep = ExtentManager.getInstance("UpdateHypervisorColumnsForSpecifiedUserTest",logFolder);
		  test = rep.startTest("beforeClass");
		  spogServer.userLogin(userName, password);
		  String token=spogServer.getJWTToken();
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
		  //create direct user
		  this.direct_email = spogServer.ReturnRandom("direct_yuefen_group@spogqa.com");
		  spogServer.createUserAndCheck(direct_email, sharePassword, "yuefen", "liu", "direct_admin", directOrg_id, test);
		  //create msp1 user
		  this.msp_email = spogServer.ReturnRandom("msp1_yuefen_group@spogqa.com");
		  spogServer.createUserAndCheck(msp_email, sharePassword, "yuefen", "liu", "msp_admin", mspOrg_id, test);
		  //create msp account admin
		  this.msp_account_admin_email =spogServer.ReturnRandom("msp1_account_admin_yuefen_group@spogqa.com");
		  msp_account_admin_id=spogServer.createUserAndCheck(msp_account_admin_email, sharePassword, "yuefen", "liu", "msp_account_admin", mspOrg_id, test);
		  userSpogServer.assignMspAccountAdmins(mspOrg_id, account_id, msp_account_admin_id, token);
		  //create account user
		  this.accountDirect_email = spogServer.ReturnRandom("account1_msp1_yuefen_group@spogqa.com");
		  spogServer.createUserAndCheck(accountDirect_email, sharePassword, "yuefen", "liu", "direct_admin", account_id, test);
		 
		  prepare(baseURI, port, logFolder, this.csrGlobalLoginUser,  this.csrGlobalLoginPassword, this.getClass().getSimpleName() );
	  }
	  
	  @DataProvider(name = "columnInfo10")
	  public final Object[][] getColumnsInfo10() {
		  return new Object[][] { 
			  {spogServer.ReturnRandom("direct_yuefen_column@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263","true","1","31dfe327-b9fe-432a-a119-24b584a85263","false","2"},
			  {spogServer.ReturnRandom("account_yuefen_column@spogqa.com"), sharePassword,"direct_admin", account_id,"31dfe327-b9fe-432a-a119-24b584a85263","true","1","31dfe327-b9fe-432a-a119-24b584a85263","false","2"},
		  };
		}
	  
	  @Test (dataProvider = "columnInfo10") 
	  public void csrReadOnlyUserUpdateHypervisorColumnsSuccess(String name, String password, String role, String org, String column_id,
			  String visible, String order_id, String newColumn_id, String newVisible, String newOrder_id){	 
		  System.out.println("csrReadOnlyUserUpdateHypervisorColumnsSuccess");
		  test = rep.startTest("csrReadOnlyUserUpdateHypervisorColumnsSuccess");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  String csrToken = spogServer.getJWTToken();
		  String userId =spogServer.createUserAndCheck(name, password, "yuefen", "liu", role, org, test);
		  
		  //create columns
		  System.out.println("create columns");
		  ArrayList<HashMap<String, Object>> columnsList = new ArrayList<HashMap<String, Object>>();
		  HashMap<String, Object> columnInfo = source4SpogServer.getHypervisorColumnInfo(column_id, visible, order_id);
		  columnsList.add(columnInfo); 
		  source4SpogServer.createHypervisorColumnsForSpecifiedUser(userId, columnsList, csrToken);
		  
		  //login
		  spogServer.userLogin(csrReadOnlyUser, sharePassword, SpogConstants.SUCCESS_LOGIN, test);
		  String token = spogServer.getJWTToken();
		  
		  //update
		  ArrayList<HashMap<String, Object>> newColumnsList = new ArrayList<HashMap<String, Object>>();
		  HashMap<String, Object> newColumnInfo = source4SpogServer.getHypervisorColumnInfo(newColumn_id, newVisible, newOrder_id);
		  newColumnsList.add(newColumnInfo);
		  
		  response = source4SpogServer.updateHypervisorColumnsForSpecifiedUser(userId, newColumnsList, token);
          spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS);
          spogServer.checkErrorCode(response, "00100101");

		  //delete user
		  spogServer.setToken(csrToken);
		  spogServer.CheckDeleteUserByIdStatus(userId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  }
	  
	  @DataProvider(name = "columnInfo4")
	  public final Object[][] getColumnsInfo4() {
		  return new Object[][] { 
			  {spogServer.ReturnRandom("direct_yuefen_user4@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263","true","1","31dfe327-b9fe-432a-a119-24b584a85263","false","2"},
			  {spogServer.ReturnRandom("direct_yuefen_user4@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263","false","2","970ae3ec-040a-498b-9171-5ff688a155b5","true","1"},
			  {spogServer.ReturnRandom("direct_yuefen_user4@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"970ae3ec-040a-498b-9171-5ff688a155b5","true","1","970ae3ec-040a-498b-9171-5ff688a155b5","false","1"},
			  {spogServer.ReturnRandom("direct_yuefen_user4@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"970ae3ec-040a-498b-9171-5ff688a155b5","false","2","46520ed0-8204-4b0f-8754-a9d4dfa82ce0","true","1"},
			  {spogServer.ReturnRandom("direct_yuefen_user4@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"46520ed0-8204-4b0f-8754-a9d4dfa82ce0","true","1","46520ed0-8204-4b0f-8754-a9d4dfa82ce0","false","1"},
			  {spogServer.ReturnRandom("direct_yuefen_user4@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"46520ed0-8204-4b0f-8754-a9d4dfa82ce0","false","2","023283e9-0027-4f28-a260-573eed6b1b62","true","1"},
			  {spogServer.ReturnRandom("direct_yuefen_user4@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"023283e9-0027-4f28-a260-573eed6b1b62","true","1","023283e9-0027-4f28-a260-573eed6b1b62","false","2"},
			  {spogServer.ReturnRandom("direct_yuefen_user4@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"023283e9-0027-4f28-a260-573eed6b1b62","false","2","8d101229-d32e-4d7f-bb09-355e96824f32","true","1"},
			  {spogServer.ReturnRandom("direct_yuefen_user4@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"8d101229-d32e-4d7f-bb09-355e96824f32","true","1","8d101229-d32e-4d7f-bb09-355e96824f32","false","2"},
			  {spogServer.ReturnRandom("direct_yuefen_user4@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"8d101229-d32e-4d7f-bb09-355e96824f32","false","2","d065d8a3-699d-4c14-834d-65953942e778","true","1"},
			  {spogServer.ReturnRandom("direct_yuefen_user4@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"d065d8a3-699d-4c14-834d-65953942e778","true","1","d065d8a3-699d-4c14-834d-65953942e778","false","2"},
			  {spogServer.ReturnRandom("direct_yuefen_user4@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"d065d8a3-699d-4c14-834d-65953942e778","false","2","4ca1cc37-bdd4-4427-a23f-9d55d48b3f44","true","1"},
			  {spogServer.ReturnRandom("direct_yuefen_user4@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"4ca1cc37-bdd4-4427-a23f-9d55d48b3f44","true","1","4ca1cc37-bdd4-4427-a23f-9d55d48b3f44","false","2"},
			  {spogServer.ReturnRandom("direct_yuefen_user4@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"4ca1cc37-bdd4-4427-a23f-9d55d48b3f44","false","2","8c8ecbce-322f-4d6b-99bb-db7bdcbfa8dd","true","1"},
			  {spogServer.ReturnRandom("direct_yuefen_user4@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"8c8ecbce-322f-4d6b-99bb-db7bdcbfa8dd","true","1","8c8ecbce-322f-4d6b-99bb-db7bdcbfa8dd","false","2"},
			  {spogServer.ReturnRandom("direct_yuefen_user4@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"8c8ecbce-322f-4d6b-99bb-db7bdcbfa8dd","false","2","31dfe327-b9fe-432a-a119-24b584a85263","true","1"}  
		  };
		}
	  @Test (dataProvider = "columnInfo4") 
	  public void updateHypervisorColumnsCheck(String name, String password, String role, String org, String column_id,
			  String visible, String order_id, String newColumn_id, String newVisible, String newOrder_id){	 
		  System.out.println("updateHypervisorColumnsCheck");
		  test = rep.startTest("updateHypervisorColumnsCheck");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login with csr user
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  String token = spogServer.getJWTToken();
		  System.out.println("token="+token);
		  //create user
		  System.out.println("create user");
		  String userId = spogServer.createUserAndCheck(name,password, "yuefen", "liu", role, org, test);
	
		  test.log(LogStatus.INFO,"compose column array list");
		  ArrayList<HashMap<String, Object>> columnsList = new ArrayList<HashMap<String, Object>>();
		  HashMap<String, Object> columnInfo = source4SpogServer.getHypervisorColumnInfo(column_id, visible, order_id);
		  columnsList.add(columnInfo);
		  
		  //create
		  test.log(LogStatus.INFO,"create logged in user columns");
		  System.out.println("start create columns");
		  Response response = source4SpogServer.createHypervisorColumnsForSpecifiedUser(userId, columnsList, token);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		  
		  //update
		  ArrayList<HashMap<String, Object>> newColumnsList = new ArrayList<HashMap<String, Object>>();
		  HashMap<String, Object> newColumnInfo = source4SpogServer.getHypervisorColumnInfo(newColumn_id, newVisible, newOrder_id);
		  newColumnsList.add(newColumnInfo);
		  
		  response = source4SpogServer.updateHypervisorColumnsForSpecifiedUser(userId, newColumnsList, token);
		  
		  //check
		  ArrayList<HashMap<String, Object>> actualColumnsList = new ArrayList<HashMap<String, Object>>();
		  actualColumnsList = response.then().extract().path("data");
		  HashMap<String, Object> actualColumnInfo = actualColumnsList.get(0);
		  assertEquals(actualColumnInfo.get("column_id").toString(), newColumn_id.toString());
		  assertEquals(actualColumnInfo.get("visible").toString(), newVisible.toString());
		  assertEquals(actualColumnInfo.get("order_id").toString(), newOrder_id.toString());

		  test.log(LogStatus.INFO,"delete logged in user columns");
		  System.out.println("delete columns");
		  response = source4SpogServer.deleteHypervisorColumnsForSpecifiedUser(userId, token);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test); 
		  
		  //delete user  
		  spogServer.CheckDeleteUserByIdStatus(userId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	  }
	  
	  @DataProvider(name = "columnInfo")
	  public final Object[][] getColumnsInfo() {
		  return new Object[][] {
			  {this.csrGlobalLoginUser, this.csrGlobalLoginPassword,spogServer.ReturnRandom("csr_yuefen_aaa@spogqa.com"), sharePassword,"csr_admin", csrOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;2","970ae3ec-040a-498b-9171-5ff688a155b5;true;1"},
		      {this.csrGlobalLoginUser, this.csrGlobalLoginPassword,spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;1","970ae3ec-040a-498b-9171-5ff688a155b5;true;2,46520ed0-8204-4b0f-8754-a9d4dfa82ce0;true;8"},
			  {this.csrGlobalLoginUser, this.csrGlobalLoginPassword,spogServer.ReturnRandom("msp1_yuefen_group@spogqa.com"), sharePassword,"msp_admin", mspOrg_id,"46520ed0-8204-4b0f-8754-a9d4dfa82ce0;true;1,023283e9-0027-4f28-a260-573eed6b1b62;true;2","023283e9-0027-4f28-a260-573eed6b1b62;true;2"},
			  {this.csrGlobalLoginUser, this.csrGlobalLoginPassword,spogServer.ReturnRandom("msp1_account_admin_yuefen_group@spogqa.com"), sharePassword,"msp_account_admin", mspOrg_id,"46520ed0-8204-4b0f-8754-a9d4dfa82ce0;true;1,023283e9-0027-4f28-a260-573eed6b1b62;true;2","023283e9-0027-4f28-a260-573eed6b1b62;true;2"},
			  {this.csrGlobalLoginUser, this.csrGlobalLoginPassword,spogServer.ReturnRandom("account1_msp1_yuefen_group@spogqa.com"), sharePassword,"direct_admin", account_id,"8d101229-d32e-4d7f-bb09-355e96824f32;true;1","8d101229-d32e-4d7f-bb09-355e96824f32;true;1,d065d8a3-699d-4c14-834d-65953942e778;true;2,4ca1cc37-bdd4-4427-a23f-9d55d48b3f44;true;3,8c8ecbce-322f-4d6b-99bb-db7bdcbfa8dd;true;4"},
	          
			  {this.direct_email, sharePassword,spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;1,970ae3ec-040a-498b-9171-5ff688a155b5;true;2,46520ed0-8204-4b0f-8754-a9d4dfa82ce0;true;8,023283e9-0027-4f28-a260-573eed6b1b62;false;7,8d101229-d32e-4d7f-bb09-355e96824f32;true;3,d065d8a3-699d-4c14-834d-65953942e778;false;3,4ca1cc37-bdd4-4427-a23f-9d55d48b3f44;true;3,8c8ecbce-322f-4d6b-99bb-db7bdcbfa8dd;true;3","31dfe327-b9fe-432a-a119-24b584a85263;false;1"},
			  
			  {this.msp_email, sharePassword,spogServer.ReturnRandom("msp1_yuefen_group@spogqa.com"), sharePassword,"msp_admin", mspOrg_id,"46520ed0-8204-4b0f-8754-a9d4dfa82ce0;true;1,023283e9-0027-4f28-a260-573eed6b1b62;true;2","023283e9-0027-4f28-a260-573eed6b1b62;true;2"},
			  {this.msp_email, sharePassword,spogServer.ReturnRandom("msp1_account_admin_yuefen_group@spogqa.com"), sharePassword,"msp_account_admin", mspOrg_id,"46520ed0-8204-4b0f-8754-a9d4dfa82ce0;true;1,023283e9-0027-4f28-a260-573eed6b1b62;true;2","023283e9-0027-4f28-a260-573eed6b1b62;true;2"},
			  
			  {this.msp_account_admin_email, sharePassword,spogServer.ReturnRandom("account1_msp1_yuefen_group@spogqa.com"), sharePassword,"direct_admin", account_id,"8d101229-d32e-4d7f-bb09-355e96824f32;true;1","8d101229-d32e-4d7f-bb09-355e96824f32;true;1,d065d8a3-699d-4c14-834d-65953942e778;true;2,4ca1cc37-bdd4-4427-a23f-9d55d48b3f44;true;3,8c8ecbce-322f-4d6b-99bb-db7bdcbfa8dd;true;4"},
			  
			  {this.accountDirect_email, sharePassword,spogServer.ReturnRandom("account1_msp1_yuefen_group@spogqa.com"), sharePassword,"direct_admin", account_id,"8d101229-d32e-4d7f-bb09-355e96824f32;true;1","8d101229-d32e-4d7f-bb09-355e96824f32;true;1,d065d8a3-699d-4c14-834d-65953942e778;true;2,4ca1cc37-bdd4-4427-a23f-9d55d48b3f44;true;3,8c8ecbce-322f-4d6b-99bb-db7bdcbfa8dd;true;4"},
			  
			  //root msp related
			  {this.final_root_msp_user_name_email, this.common_password,spogServer.ReturnRandom("msp1_yuefen_group@spogqa.com"), sharePassword,"direct_admin", this.root_msp_direct_org_id,"46520ed0-8204-4b0f-8754-a9d4dfa82ce0;true;1,023283e9-0027-4f28-a260-573eed6b1b62;true;2","023283e9-0027-4f28-a260-573eed6b1b62;true;2"},
			  {this.final_root_msp_account_admin_user_name_email, this.common_password,spogServer.ReturnRandom("msp1_account_admin_yuefen_group@spogqa.com"), sharePassword,"direct_admin", this.root_msp_direct_org_id,"46520ed0-8204-4b0f-8754-a9d4dfa82ce0;true;1,023283e9-0027-4f28-a260-573eed6b1b62;true;2","023283e9-0027-4f28-a260-573eed6b1b62;true;2"},
			  {this.final_sub_msp1_user_name_email, this.common_password,spogServer.ReturnRandom("account1_msp1_yuefen_group@spogqa.com"), sharePassword,"direct_admin", this.sub_msp1_account1_id,"8d101229-d32e-4d7f-bb09-355e96824f32;true;1","8d101229-d32e-4d7f-bb09-355e96824f32;true;1,d065d8a3-699d-4c14-834d-65953942e778;true;2,4ca1cc37-bdd4-4427-a23f-9d55d48b3f44;true;3,8c8ecbce-322f-4d6b-99bb-db7bdcbfa8dd;true;4"},
			  {this.final_sub_msp1_msp_account_user_name_email, this.common_password,spogServer.ReturnRandom("account1_msp1_yuefen_group@spogqa.com"), sharePassword,"direct_admin", this.sub_msp1_account1_id,"8d101229-d32e-4d7f-bb09-355e96824f32;true;1","8d101229-d32e-4d7f-bb09-355e96824f32;true;1,d065d8a3-699d-4c14-834d-65953942e778;true;2,4ca1cc37-bdd4-4427-a23f-9d55d48b3f44;true;3,8c8ecbce-322f-4d6b-99bb-db7bdcbfa8dd;true;4"},
			  {this.final_root_msp_direct_org_user_email, this.common_password,spogServer.ReturnRandom("account1_msp1_yuefen_group@spogqa.com"), sharePassword,"direct_admin", this.root_msp_direct_org_id,"8d101229-d32e-4d7f-bb09-355e96824f32;true;1","8d101229-d32e-4d7f-bb09-355e96824f32;true;1,d065d8a3-699d-4c14-834d-65953942e778;true;2,4ca1cc37-bdd4-4427-a23f-9d55d48b3f44;true;3,8c8ecbce-322f-4d6b-99bb-db7bdcbfa8dd;true;4"},
			  {this.final_sub_msp1_account1_user_email, this.common_password,spogServer.ReturnRandom("account1_msp1_yuefen_group@spogqa.com"), sharePassword,"direct_admin", this.sub_msp1_account1_id,"8d101229-d32e-4d7f-bb09-355e96824f32;true;1","8d101229-d32e-4d7f-bb09-355e96824f32;true;1,d065d8a3-699d-4c14-834d-65953942e778;true;2,4ca1cc37-bdd4-4427-a23f-9d55d48b3f44;true;3,8c8ecbce-322f-4d6b-99bb-db7bdcbfa8dd;true;4"},
		  };
		}
	  
	  @Test (dataProvider = "columnInfo") 
	  public void updateHypervisorColumnsSuccess(String loginUser, String loginPassword, String name, String password, String role, String org, String specifiedHypervisorColumns,
			  String newSpecifiedHypervisorColumns){	 
		  System.out.println("updateHypervisorColumnsSuccess");
		  test = rep.startTest("updateHypervisorColumnsSuccess");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login with csr user
		  spogServer.userLogin(loginUser, loginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  String token = spogServer.getJWTToken();
		  System.out.println("token="+token);
		  
		  //create user
		  System.out.println("create user");
		  String userId = spogServer.createUserAndCheck(name,password, "yuefen", "liu", role, org, test);
		  
		  test.log(LogStatus.INFO,"get column array list");
		  ArrayList<HashMap<String, Object>> columnsList = source4SpogServer.getHypervisorColumnArrayList(specifiedHypervisorColumns, token);
		  
		  test.log(LogStatus.INFO,"create logged in user columns");
		  System.out.println("start create columns");
		  Response response = source4SpogServer.createHypervisorColumnsForSpecifiedUser(userId, columnsList, token);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		  //update
		  ArrayList<HashMap<String, Object>> newColumnsList = source4SpogServer.getHypervisorColumnArrayList(newSpecifiedHypervisorColumns, token);
		  System.out.println("start update columns");
		  response = source4SpogServer.updateHypervisorColumnsForSpecifiedUser(userId, newColumnsList, token);
		  source4SpogServer.CompareHypervisorColumns(response, newColumnsList, test);
	      
		  test.log(LogStatus.INFO,"delete logged in user columns");
		  System.out.println("delete columns");
		  response = source4SpogServer.deleteHypervisorColumnsForSpecifiedUser(userId, token);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test); 
		  
		  //delete user  
		  spogServer.CheckDeleteUserByIdStatus(userId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  }
	  
	  @DataProvider(name = "columnInfo5")
	  public final Object[][] getColumnsInfo5() {
		  return new Object[][] { 
			  
			  {spogServer.ReturnRandom("direct_yuefen_user5@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"d065d8a3-699d-4c14-834d-65953942e778","false","2","8c8ecbce-322f-4d6b-99bb-db7bdcbfa8dd","false","none"},
			  {spogServer.ReturnRandom("direct_yuefen_user5@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"023283e9-0027-4f28-a260-573eed6b1b62","true","1","31dfe327-b9fe-432a-a119-24b584a85263","none","8"}
		  };
	  }
	  @Test (dataProvider = "columnInfo5") 
	  public void updateHypervisorColumnsNullCheck(String name, String password, String role, String org, String column_id,
			  String visible, String order_id,String newColumn_id,
			  String newVisible, String newOrder_id){	 
		  System.out.println("updateHypervisorColumnsNullCheck");
		  test = rep.startTest("updateHypervisorColumnsNullCheck");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login with csr user
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  String token = spogServer.getJWTToken();
		  System.out.println("token="+token);
		  
		  //create user
		  System.out.println("create user");
		  String userId = spogServer.createUserAndCheck(name,password, "yuefen", "liu", role, org, test);
		  
		  test.log(LogStatus.INFO,"compose column array list");
		  ArrayList<HashMap<String, Object>> columnsList = new ArrayList<HashMap<String, Object>>();
		  HashMap<String, Object> columnInfo = source4SpogServer.getHypervisorColumnInfo(column_id, visible, order_id);
		  columnsList.add(columnInfo);
		  
		  test.log(LogStatus.INFO,"create logged in user columns");
		  System.out.println("start create columns");
		  Response response = source4SpogServer.createHypervisorColumnsForSpecifiedUser(userId, columnsList, token);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
         //update
		  System.out.println("start update columns");
		  ArrayList<HashMap<String, Object>> newColumnsList = new ArrayList<HashMap<String, Object>>();
		  HashMap<String, Object> newColumnInfo = source4SpogServer.getHypervisorColumnInfo(newColumn_id, newVisible, newOrder_id);
		  newColumnsList.add(newColumnInfo);
		  response = source4SpogServer.updateHypervisorColumnsForSpecifiedUser(userId, newColumnsList, token);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  
		  test.log(LogStatus.INFO,"delete logged in user columns");
		  System.out.println("delete columns");
		  response = source4SpogServer.deleteHypervisorColumnsForSpecifiedUser(userId, token);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test); 
		  
		  //delete user  
		  spogServer.CheckDeleteUserByIdStatus(userId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	  }
	  
	  @DataProvider(name = "columnInfo7")
	  public final Object[][] getColumnsInfo7() {
		  return new Object[][] { 
			  {this.direct_email,sharePassword, spogServer.ReturnRandom("direct_yuefen_aaa6@spogqa.com"), sharePassword,"direct_admin", directOrg2_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;2,970ae3ec-040a-498b-9171-5ff688a155b5;true;1","00100101"},
			  {this.direct_email,sharePassword,spogServer.ReturnRandom("msp1_yuefen_aaa6@spogqa.com"), sharePassword,"msp_admin", mspOrg_id,"46520ed0-8204-4b0f-8754-a9d4dfa82ce0;true;1,023283e9-0027-4f28-a260-573eed6b1b62;true;2","00100101"},
			  {this.direct_email,sharePassword,spogServer.ReturnRandom("msp1_account_adminyuefen_aaa6@spogqa.com"), sharePassword,"msp_account_admin", mspOrg_id,"46520ed0-8204-4b0f-8754-a9d4dfa82ce0;true;1,023283e9-0027-4f28-a260-573eed6b1b62;true;2","00100101"},
			  {this.direct_email,sharePassword,spogServer.ReturnRandom("account1_msp1_yuefen_aaa6@spogqa.com"), sharePassword,"direct_admin", account_id,"8d101229-d32e-4d7f-bb09-355e96824f32;true;1,d065d8a3-699d-4c14-834d-65953942e778;true;2,4ca1cc37-bdd4-4427-a23f-9d55d48b3f44;true;3,8c8ecbce-322f-4d6b-99bb-db7bdcbfa8dd;true;4","00100101"},
			 
			  {this.msp_email,sharePassword,spogServer.ReturnRandom("msp2_yuefen_aaa6@spogqa.com"), sharePassword,"msp_admin", mspOrg2_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;2,970ae3ec-040a-498b-9171-5ff688a155b5;true;1","00100101"},
			  {this.msp_email,sharePassword,spogServer.ReturnRandom("account2_yuefen_aaa6@spogqa.com"), sharePassword,"direct_admin", account2_id,"46520ed0-8204-4b0f-8754-a9d4dfa82ce0;true;1,023283e9-0027-4f28-a260-573eed6b1b62;true;2","00100101"},
			  {this.msp_email,sharePassword,spogServer.ReturnRandom("msp1_account_adminyuefen_aaa6@spogqa.com"), sharePassword,"msp_account_admin", mspOrg2_id,"46520ed0-8204-4b0f-8754-a9d4dfa82ce0;true;1,023283e9-0027-4f28-a260-573eed6b1b62;true;2","00100101"},
			  {this.msp_email,sharePassword,spogServer.ReturnRandom("direct_yuefen_aaa6@spogqa.com"), sharePassword,"direct_admin", directOrg2_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;2,970ae3ec-040a-498b-9171-5ff688a155b5;true;1","00100101"},
			 
			  {this.msp_account_admin_email,sharePassword,spogServer.ReturnRandom("msp2_yuefen_aaa6@spogqa.com"), sharePassword,"msp_admin", mspOrg2_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;2,970ae3ec-040a-498b-9171-5ff688a155b5;true;1","00100101"},
			  {this.msp_account_admin_email,sharePassword,spogServer.ReturnRandom("account2_yuefen_aaa6@spogqa.com"), sharePassword,"direct_admin", account2_id,"46520ed0-8204-4b0f-8754-a9d4dfa82ce0;true;1,023283e9-0027-4f28-a260-573eed6b1b62;true;2","00100101"},
			  {this.msp_account_admin_email,sharePassword,spogServer.ReturnRandom("msp1_account_adminyuefen_aaa6@spogqa.com"), sharePassword,"msp_account_admin", mspOrg2_id,"46520ed0-8204-4b0f-8754-a9d4dfa82ce0;true;1,023283e9-0027-4f28-a260-573eed6b1b62;true;2","00100101"},
			  {this.msp_account_admin_email,sharePassword,spogServer.ReturnRandom("direct_yuefen_aaa6@spogqa.com"), sharePassword,"direct_admin", directOrg2_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;2,970ae3ec-040a-498b-9171-5ff688a155b5;true;1","00100101"},
			  
			  {this.accountDirect_email,sharePassword,spogServer.ReturnRandom("account2_msp2_yuefen_aaa6@spogqa.com"), sharePassword,"direct_admin", account2_id,"8d101229-d32e-4d7f-bb09-355e96824f32;true;1,d065d8a3-699d-4c14-834d-65953942e778;true;2,4ca1cc37-bdd4-4427-a23f-9d55d48b3f44;true;3,8c8ecbce-322f-4d6b-99bb-db7bdcbfa8dd;true;4","00100101"},
			  {this.accountDirect_email,sharePassword,spogServer.ReturnRandom("direct_yuefen_aaa6@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;2,970ae3ec-040a-498b-9171-5ff688a155b5;true;1","00100101"},
			  {this.accountDirect_email,sharePassword,spogServer.ReturnRandom("msp1_yuefen_aaa6@spogqa.com"), sharePassword,"msp_admin", mspOrg_id,"46520ed0-8204-4b0f-8754-a9d4dfa82ce0;true;1,023283e9-0027-4f28-a260-573eed6b1b62;true;2","00100101"},
			  {this.accountDirect_email,sharePassword,spogServer.ReturnRandom("msp1_account_adminyuefen_aaa6@spogqa.com"), sharePassword,"msp_account_admin", mspOrg_id,"46520ed0-8204-4b0f-8754-a9d4dfa82ce0;true;1,023283e9-0027-4f28-a260-573eed6b1b62;true;2","00100101"},
		  //root msp related
			  {this.final_root_msp_user_name_email,this.common_password,spogServer.ReturnRandom("msp2_yuefen_aaa6@spogqa.com"), sharePassword,"msp_admin", this.sub_msp1_org_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;2,970ae3ec-040a-498b-9171-5ff688a155b5;true;1","00100101"},
			  {this.final_root_msp_user_name_email,this.common_password,spogServer.ReturnRandom("account2_yuefen_aaa6@spogqa.com"), sharePassword,"direct_admin", this.sub_msp1_account1_id,"46520ed0-8204-4b0f-8754-a9d4dfa82ce0;true;1,023283e9-0027-4f28-a260-573eed6b1b62;true;2","00100101"},
			  {this.final_root_msp_account_admin_user_name_email,this.common_password,spogServer.ReturnRandom("msp1_account_adminyuefen_aaa6@spogqa.com"), sharePassword,"msp_admin", this.sub_msp1_org_id,"46520ed0-8204-4b0f-8754-a9d4dfa82ce0;true;1,023283e9-0027-4f28-a260-573eed6b1b62;true;2","00100101"},
			  {this.final_root_msp_account_admin_user_name_email,this.common_password,spogServer.ReturnRandom("direct_yuefen_aaa6@spogqa.com"), sharePassword,"direct_admin", this.sub_msp1_account1_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;2,970ae3ec-040a-498b-9171-5ff688a155b5;true;1","00100101"},
			  {this.final_root_msp_user_name_email,this.common_password,spogServer.ReturnRandom("msp2_yuefen_aaa6@spogqa.com"), sharePassword,"msp_admin", this.mspOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;2,970ae3ec-040a-498b-9171-5ff688a155b5;true;1","00100101"},
			  {this.final_root_msp_user_name_email,this.common_password,spogServer.ReturnRandom("msp2_yuefen_aaa6@spogqa.com"), sharePassword,"direct_admin", this.directOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;2,970ae3ec-040a-498b-9171-5ff688a155b5;true;1","00100101"},
			  
			  {this.final_sub_msp1_user_name_email,this.common_password,spogServer.ReturnRandom("msp2_yuefen_aaa6@spogqa.com"), sharePassword,"msp_admin", this.sub_msp2_org_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;2,970ae3ec-040a-498b-9171-5ff688a155b5;true;1","00100101"},
			  {this.final_sub_msp1_user_name_email,this.common_password,spogServer.ReturnRandom("account2_yuefen_aaa6@spogqa.com"), sharePassword,"direct_admin", this.sub_msp2_account1_id,"46520ed0-8204-4b0f-8754-a9d4dfa82ce0;true;1,023283e9-0027-4f28-a260-573eed6b1b62;true;2","00100101"},
			  {this.final_sub_msp1_user_name_email,this.common_password,spogServer.ReturnRandom("account2_yuefen_aaa6@spogqa.com"), sharePassword,"direct_admin", this.root_msp_direct_org_id,"46520ed0-8204-4b0f-8754-a9d4dfa82ce0;true;1,023283e9-0027-4f28-a260-573eed6b1b62;true;2","00100101"},
			  {this.final_sub_msp1_msp_account_user_name_email,this.common_password,spogServer.ReturnRandom("msp1_account_adminyuefen_aaa6@spogqa.com"), sharePassword,"msp_admin", this.sub_msp2_org_id,"46520ed0-8204-4b0f-8754-a9d4dfa82ce0;true;1,023283e9-0027-4f28-a260-573eed6b1b62;true;2","00100101"},
			  {this.final_sub_msp1_msp_account_user_name_email,this.common_password,spogServer.ReturnRandom("direct_yuefen_aaa6@spogqa.com"), sharePassword,"direct_admin", this.sub_msp2_account1_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;2,970ae3ec-040a-498b-9171-5ff688a155b5;true;1","00100101"},
			  {this.final_sub_msp1_msp_account_user_name_email,this.common_password,spogServer.ReturnRandom("direct_yuefen_aaa6@spogqa.com"), sharePassword,"direct_admin", this.root_msp_direct_org_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;2,970ae3ec-040a-498b-9171-5ff688a155b5;true;1","00100101"},
			  {this.final_sub_msp1_user_name_email,this.common_password,spogServer.ReturnRandom("msp2_yuefen_aaa6@spogqa.com"), sharePassword,"msp_admin", this.mspOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;2,970ae3ec-040a-498b-9171-5ff688a155b5;true;1","00100101"},
			  {this.final_sub_msp1_user_name_email,this.common_password,spogServer.ReturnRandom("msp2_yuefen_aaa6@spogqa.com"), sharePassword,"direct_admin", this.directOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;2,970ae3ec-040a-498b-9171-5ff688a155b5;true;1","00100101"},
			  
			  {this.final_root_msp_direct_org_user_email,this.common_password,spogServer.ReturnRandom("msp2_yuefen_aaa6@spogqa.com"), sharePassword,"msp_admin", this.sub_msp1_org_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;2,970ae3ec-040a-498b-9171-5ff688a155b5;true;1","00100101"},
			  {this.final_root_msp_direct_org_user_email,this.common_password,spogServer.ReturnRandom("msp2_yuefen_aaa6@spogqa.com"), sharePassword,"direct_admin", this.sub_msp1_account1_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;2,970ae3ec-040a-498b-9171-5ff688a155b5;true;1","00100101"},
			  {this.final_sub_msp1_account1_user_email,this.common_password,spogServer.ReturnRandom("account2_yuefen_aaa6@spogqa.com"), sharePassword,"msp_admin", this.sub_msp2_org_id,"46520ed0-8204-4b0f-8754-a9d4dfa82ce0;true;1,023283e9-0027-4f28-a260-573eed6b1b62;true;2","00100101"},
			  {this.final_sub_msp1_account1_user_email,this.common_password,spogServer.ReturnRandom("account2_yuefen_aaa6@spogqa.com"), sharePassword,"direct_admin", this.sub_msp2_account1_id,"46520ed0-8204-4b0f-8754-a9d4dfa82ce0;true;1,023283e9-0027-4f28-a260-573eed6b1b62;true;2","00100101"},
			  {this.final_sub_msp1_account1_user_email,this.common_password,spogServer.ReturnRandom("account2_yuefen_aaa6@spogqa.com"), sharePassword,"direct_admin", this.root_msp_direct_org_id,"46520ed0-8204-4b0f-8754-a9d4dfa82ce0;true;1,023283e9-0027-4f28-a260-573eed6b1b62;true;2","00100101"},
		 
			  {this.direct_email,sharePassword,spogServer.ReturnRandom("account1_msp1_yuefen_aaa6@spogqa.com"), sharePassword,"msp_admin", this.root_msp_org_id,"8d101229-d32e-4d7f-bb09-355e96824f32;true;1,d065d8a3-699d-4c14-834d-65953942e778;true;2,4ca1cc37-bdd4-4427-a23f-9d55d48b3f44;true;3,8c8ecbce-322f-4d6b-99bb-db7bdcbfa8dd;true;4","00100101"},
			  {this.msp_email,sharePassword,spogServer.ReturnRandom("msp2_yuefen_aaa6@spogqa.com"), sharePassword,"msp_admin", this.sub_msp1_org_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;2,970ae3ec-040a-498b-9171-5ff688a155b5;true;1","00100101"},
		  };
		}
	  @Test (dataProvider = "columnInfo7") 
	  public void updateHypervisorColumnsForOtherUserfail(String loginUserName, String loginPassword, String name, String password, String role, String org, String specifiedHypervisorColumns,
			  String expectedErrorMessage){	 
		  System.out.println("updateHypervisorColumnsForOtherUserfail");
		  test = rep.startTest("updateHypervisorColumnsForOtherUserfail");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login with csr user
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  String csrToken = spogServer.getJWTToken();
		  System.out.println("csrtoken="+csrToken);
		  
		  //create user
		  System.out.println("create user");
		  String userId = spogServer.createUserAndCheck(name,password, "yuefen", "liu", role, org, test);

		  //login
		  spogServer.userLogin(loginUserName, loginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  String token2 = spogServer.getJWTToken();
		  System.out.println("token="+ token2);
		 
		  test.log(LogStatus.INFO,"get column array list");
		  ArrayList<HashMap<String, Object>> columnsList = source4SpogServer.getHypervisorColumnArrayList(specifiedHypervisorColumns, csrToken);
		  
		  test.log(LogStatus.INFO,"create logged in user columns");
		  System.out.println("start create columns");
		  Response response = source4SpogServer.createHypervisorColumnsForSpecifiedUser(userId, columnsList, csrToken);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test); 
		  //update
		  response = source4SpogServer.updateHypervisorColumnsForSpecifiedUser(userId, columnsList, token2);
		  spogServer.checkResponseStatus(response, SpogConstants.INSUFFICIENT_PERMISSIONS, test); 
		  spogServer.checkErrorCode(response, expectedErrorMessage);
		  
		  //delete user  
		  spogServer.setToken(csrToken);
		  spogServer.CheckDeleteUserByIdStatus(userId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  }
	  
	  @DataProvider(name = "columnInfo8")
	  public final Object[][] getColumnsInfo8() {
		  return new Object[][] { 
			  {spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263","true","1","",SpogConstants.RESOURCE_NOT_EXIST,"00900001"},
			  {spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263","true","1","null",SpogConstants.REQUIRED_INFO_NOT_EXIST,"40000005"},
			  {spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263","true","1","none",SpogConstants.REQUIRED_INFO_NOT_EXIST,"40000005"},
			  {spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263","true","1",UUID.randomUUID().toString(),SpogConstants.RESOURCE_NOT_EXIST,"00200007"},
	          };
		}
	  @Test (dataProvider = "columnInfo8") 
	  public void updateHypervsiorColumnFailWithInvlidUserID(String name, String password, String role, String org, 
			  String column_id, String visible, String order_id,String userId,int expectedStatusCode, 
			  String expectedErrorCode){	 
		  System.out.println("updateHypervsiorColumnFailWithInvlidUserID");
		  test = rep.startTest("updateHypervsiorColumnFailWithInvlidUserID");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  String token = spogServer.getJWTToken();

		  //update columns
		  ArrayList<HashMap<String,Object>> columnsList = new ArrayList<HashMap<String,Object>> ();
		  HashMap<String,Object> columnInfo = source4SpogServer.getSourceColumnInfo(column_id, visible, order_id);
		  columnsList.add(columnInfo);
		  System.out.println("get columns");
		  Response response =source4SpogServer.updateHypervisorColumnsForSpecifiedUser(userId, columnsList, token);
		  spogServer.checkResponseStatus(response, expectedStatusCode, test);
		  spogServer.checkErrorCode(response, expectedErrorCode);
		  }
	  @DataProvider(name = "columnInfo1")
	  public final Object[][] getColumnsInfo1() {
	 	  return new Object[][] { 
	 		  {spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;1,970ae3ec-040a-498b-9171-5ff688a155b5;true;2","31dfe327-b9fe-432a-a119-24b584a85263;true;1,970ae3ec-040a-498b-9171-5ff688a155b5;true;2"},
	 		  {spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;1,970ae3ec-040a-498b-9171-5ff688a155b5;true;2","31dfe327-b9fe-432a-a119-24b584a85263;true;1,8c8ecbce-322f-4d6b-99bb-db7bdcbfa8dd;true;2"}
	 	  };
	 	  }
	  @Test (dataProvider = "columnInfo1") 
	  public void updateHypervisorColumnsDuplicate(String name, String password, String role, String org, String specifiedHypervisorColumns,
	 		  String newSpecifiedHypervisorColumns){	 
	 	  System.out.println("updateHypervisorColumnsDuplicate");
	 	  test = rep.startTest("updateHypervisorColumnsDuplicate");
	 	  test.assignAuthor("Liu Yuefen");
	 	  
	 	  //login with csr user
	 	  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
	 	  String token = spogServer.getJWTToken();
	 	  System.out.println("token="+token);
	 	  //create user
	 	  System.out.println("create user");
	 	  String userId = spogServer.createUserAndCheck(name,password, "yuefen", "liu", role, org, test);
	 	  
	 	  test.log(LogStatus.INFO,"get column array list");
	 	  ArrayList<HashMap<String, Object>> columnsList = source4SpogServer.getHypervisorColumnArrayList(specifiedHypervisorColumns, token);
	 	  
	 	  test.log(LogStatus.INFO,"create logged in user columns");
	 	  System.out.println("start create columns");
	 	  Response response = source4SpogServer.createHypervisorColumnsForSpecifiedUser(userId, columnsList, token);
	 	  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
	 	  
	 	  //update
	 	  ArrayList<HashMap<String, Object>> newColumnsList = source4SpogServer.getHypervisorColumnArrayList(newSpecifiedHypervisorColumns, token);
	 	  response = source4SpogServer.updateHypervisorColumnsForSpecifiedUser(userId, newColumnsList, token);
	 	  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	 	  
	 	  test.log(LogStatus.INFO,"delete logged in user columns");
	 	  System.out.println("delete columns");
	 	  response = source4SpogServer.deleteHypervisorColumnsForSpecifiedUser(userId, token);
	 	  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test); 
	 	  //delete user
	 	  spogServer.CheckDeleteUserByIdStatus(userId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	 	  }
	 	  
	  @DataProvider(name = "columnInfo2")
	  public final Object[][] getColumnsInfo2() {
	 	  return new Object[][] { 
	 	      {spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;1","31dfe327-b9fe-432a-a119-24b584a85263;true;0,970ae3ec-040a-498b-9171-5ff688a155b5;true;2",SpogConstants.REQUIRED_INFO_NOT_EXIST,"40000016"},
	 		  {spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;1","31dfe327-b9fe-432a-a119-24b584a85263;true;1,970ae3ec-040a-498b-9171-5ff688a155b5;true;0",SpogConstants.REQUIRED_INFO_NOT_EXIST,"40000016"},
	 		  {spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;1","31dfe327-b9fe-432a-a119-24b584a85263;true;-2",SpogConstants.REQUIRED_INFO_NOT_EXIST,"40000016"},
	 		  {spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;1","31dfe327-b9fe-432a-a119-24b584a85263;true;1,970ae3ec-040a-498b-9171-5ff688a155b5;true;9",SpogConstants.REQUIRED_INFO_NOT_EXIST,"00D00006"},
	 		  {spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;1","00000000-0000-0000-0000-000000000000;true;1,970ae3ec-040a-498b-9171-5ff688a155b5;true;2",SpogConstants.RESOURCE_NOT_EXIST,"00D00003"},
	 		  {spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;1",";true;1",SpogConstants.REQUIRED_INFO_NOT_EXIST,"40000001"},
	 		  {spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;1","null;true;1,970ae3ec-040a-498b-9171-5ff688a155b5;true;2",SpogConstants.REQUIRED_INFO_NOT_EXIST,"40000005"},
	 		  {spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;1","none;true;1,970ae3ec-040a-498b-9171-5ff688a155b5;true;2",SpogConstants.REQUIRED_INFO_NOT_EXIST,"40000005"}  
	 	  };
	 	  }
	  
	  @Test (dataProvider = "columnInfo2") 
	  public void updateHypervisorColumnsFail(String name, String password, String role, String org, String specifiedHypervisorColumns,
	 		  String newSpecifiedHypervisorColumns, int expectedStatuscode, String expectedErrorCode){	 
	 	  System.out.println("updateHypervisorColumnsFail");
	 	  test = rep.startTest("updateHypervisorColumnsFail");
	 	  test.assignAuthor("Liu Yuefen");
	 	  
	 	  //login with csr user
	 	  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
	 	  String token = spogServer.getJWTToken();
	 	  System.out.println("token="+token);
	 	  //create user
	 	  System.out.println("create user");
	 	  String userId = spogServer.createUserAndCheck(name,password, "yuefen", "liu", role, org, test);
	 	  
	 	  test.log(LogStatus.INFO,"get column array list");
	 	  ArrayList<HashMap<String, Object>> columnsList = source4SpogServer.getHypervisorColumnArrayList(specifiedHypervisorColumns, token);
	 	  
	 	  test.log(LogStatus.INFO,"create logged in user columns");
	 	  System.out.println("start create columns");
	 	  Response response = source4SpogServer.createHypervisorColumnsForSpecifiedUser(userId, columnsList, token);
	 	  //update
	 	  ArrayList<HashMap<String, Object>> newColumnsList = source4SpogServer.getHypervisorColumnArrayList(newSpecifiedHypervisorColumns, token);
	 	  response = source4SpogServer.updateHypervisorColumnsForSpecifiedUser(userId, newColumnsList, token);
	 	  spogServer.checkResponseStatus(response, expectedStatuscode, test);
	 	  spogServer.checkErrorCode(response,  expectedErrorCode);
	 	
	 	  test.log(LogStatus.INFO,"delete logged in user columns");
	 	  System.out.println("delete columns");
	 	  response = source4SpogServer.deleteHypervisorColumnsForSpecifiedUser(userId, token);
	 	  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test); 
	 	  //delete user
	 	  spogServer.CheckDeleteUserByIdStatus(userId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	 	  }
	 	  
	 	  @DataProvider(name = "columnInfo6")
	  public final Object[][] getColumnsInfo6() {
	 	  return new Object[][] { {spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;1,970ae3ec-040a-498b-9171-5ff688a155b5;true;2",SpogConstants.NOT_LOGGED_IN,"00900006"}
	 		  
	 	  };
	 	  }
	  @Test (dataProvider = "columnInfo6") 
	  public void updateHypervisorColumnsWithoutCreate(String name, String password, String role, String org, String specifiedHypervisorColumns,
	 		  int expectedStatuscode, String expectedErrorCode){	 
	 	  System.out.println("updateHypervisorColumnsFail401");
	 	  test = rep.startTest("updateHypervisorColumnsFail401");
	 	  test.assignAuthor("Liu Yuefen");
	 	  
	 	  //login with csr user
	 	  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
	 	  String token = spogServer.getJWTToken();
	 	  System.out.println("token="+token);
	 	  //create user
	 	  System.out.println("create user");
	 	  String userId = spogServer.createUserAndCheck(name,password, "yuefen", "liu", role, org, test);
	 	  
	 	  test.log(LogStatus.INFO,"get column array list");
	 	  ArrayList<HashMap<String, Object>> columnsList = source4SpogServer.getHypervisorColumnArrayList(specifiedHypervisorColumns, token);
	 	  test.log(LogStatus.INFO,"create logged in user columns");
	 	  System.out.println("start create columns");
	 	  Response response = source4SpogServer.updateHypervisorColumnsForSpecifiedUser(userId, columnsList, token);
	 	  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	 	  
	 	  //delete user
	 	  spogServer.CheckDeleteUserByIdStatus(userId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	 	
	 	  }
	  
	  @DataProvider(name = "columnInfo3")
	  public final Object[][] getColumnsInfo3() {
	 	  return new Object[][] { {spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;1,970ae3ec-040a-498b-9171-5ff688a155b5;true;2",SpogConstants.NOT_LOGGED_IN,"00900006"}
	 		  
	 	  };
	 	  }
	  @Test (dataProvider = "columnInfo3") 
	  public void updateHypervisorColumnsFail401(String name, String password, String role, String org, String specifiedHypervisorColumns,
	 		  int expectedStatuscode, String expectedErrorCode){	 
	 	  System.out.println("updateHypervisorColumnsFail401");
	 	  test = rep.startTest("updateHypervisorColumnsFail401");
	 	  test.assignAuthor("Liu Yuefen");
	 	  
	 	  //login with csr user
	 	  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
	 	  String token = spogServer.getJWTToken();
	 	  System.out.println("token="+token);
	 	  //create user
	 	  System.out.println("create user");
	 	  String userId = spogServer.createUserAndCheck(name,password, "yuefen", "liu", role, org, test);

	 	  test.log(LogStatus.INFO,"get column array list");
	 	  ArrayList<HashMap<String, Object>> columnsList = source4SpogServer.getHypervisorColumnArrayList(specifiedHypervisorColumns, token);
	 	  test.log(LogStatus.INFO,"create logged in user columns");
	 	  System.out.println("start create columns");
	 	  Response response = source4SpogServer.createHypervisorColumnsForSpecifiedUser(userId, columnsList, token);
	 	  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
	 	  
	 	  token = "";
	 	  response = source4SpogServer.updateHypervisorColumnsForSpecifiedUser(userId, columnsList, token);
	 	  spogServer.checkResponseStatus(response, expectedStatuscode, test);
	 	  spogServer.checkErrorCode(response,  expectedErrorCode);
	 	  //delete user
	 	  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
	 	  spogServer.CheckDeleteUserByIdStatus(userId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
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
