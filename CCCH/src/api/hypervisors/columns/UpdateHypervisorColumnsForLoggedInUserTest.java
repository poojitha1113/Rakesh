package api.hypervisors.columns;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

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
import InvokerServer.Source4SPOGServer;
import dataPreparation.JsonPreparation;
import genericutil.ExtentManager;
import genericutil.SQLServerDb;
import genericutil.testcasescount;
import io.restassured.response.Response;
import static org.hamcrest.Matchers.equalTo;
import static org.testng.Assert.assertEquals;

public class UpdateHypervisorColumnsForLoggedInUserTest extends base.prepare.PrepareOrgInfo{
	@Parameters({ "pmfKey"})
	  public UpdateHypervisorColumnsForLoggedInUserTest(String pmfKey) {
			super(pmfKey);
			// TODO Auto-generated constructor stub
		}
	  private static JsonPreparation jp = new JsonPreparation();
	  private SPOGServer spogServer;
	  private Source4SPOGServer source4SpogServer;
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
			System.out.println("UpdateHypervisorColumnsForLoggedInUserTest");
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
		  rep = ExtentManager.getInstance("UpdateHypervisorColumnsForLoggedInUserTest",logFolder);
		  test = rep.startTest("beforeClass");
		  spogServer.userLogin(userName, password);
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
//		  this.direct_email = spogServer.ReturnRandom("direct_yuefen_group@spogqa.com");
//		  spogServer.createUserAndCheck(direct_email, sharePassword, "yuefen", "liu", "direct_admin", directOrg_id, test);
//		  //create msp1 user
//		  this.msp_email = spogServer.ReturnRandom("msp1_yuefen_group@spogqa.com");
//		  spogServer.createUserAndCheck(msp_email, sharePassword, "yuefen", "liu", "msp_admin", mspOrg_id, test);
//		  //create account user
//		  this.accountDirect_email = spogServer.ReturnRandom("account1_msp1_yuefen_group@spogqa.com");
//		  spogServer.createUserAndCheck(accountDirect_email, sharePassword, "yuefen", "liu", "direct_admin", account_id, test);
//		  
		  prepare(baseURI, port, logFolder, this.csrGlobalLoginUser,  this.csrGlobalLoginPassword, this.getClass().getSimpleName() );
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
		  
		  //create
		  test.log(LogStatus.INFO,"create logged in user columns");
		  System.out.println("start create columns");
		  Response response = source4SpogServer.createHypervisorColumnsForLoggedInUser(columnsList, token);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_POST, test);
		  
		  //update
		  ArrayList<HashMap<String, Object>> newColumnsList = new ArrayList<HashMap<String, Object>>();
		  HashMap<String, Object> newColumnInfo = source4SpogServer.getHypervisorColumnInfo(newColumn_id, newVisible, newOrder_id);
		  newColumnsList.add(newColumnInfo);
		  
		  response = source4SpogServer.updateHypervisorColumnsForLoggedInUser(newColumnsList, token);
		  
		  //check
		  ArrayList<HashMap<String, Object>> actualColumnsList = new ArrayList<HashMap<String, Object>>();
		  actualColumnsList = response.then().extract().path("data");
		  HashMap<String, Object> actualColumnInfo = actualColumnsList.get(0);
		  assertEquals(actualColumnInfo.get("column_id").toString(), newColumn_id.toString());
		  assertEquals(actualColumnInfo.get("visible").toString(), newVisible.toString());
		  assertEquals(actualColumnInfo.get("order_id").toString(), newOrder_id.toString());

		  test.log(LogStatus.INFO,"delete logged in user columns");
		  System.out.println("delete columns");
		  response = source4SpogServer.deleteHypervisorColumnsForLoggedInUser(token);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test); 
		  
		  //delete user  
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  spogServer.CheckDeleteUserByIdStatus(userId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	  }
	 
	  @DataProvider(name = "columnInfo")
	  public final Object[][] getColumnsInfo() {
		  return new Object[][] { 
			  {spogServer.ReturnRandom("csr_admin_yuefen_aaa@spogqa.com"), sharePassword,"csr_admin", csrOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;2,970ae3ec-040a-498b-9171-5ff688a155b5;true;1","023283e9-0027-4f28-a260-573eed6b1b62;true;2"},
			  {spogServer.ReturnRandom("csr_yuefen_readonly_aa@spogqa.com"), sharePassword,"csr_read_only", csrOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;2,970ae3ec-040a-498b-9171-5ff688a155b5;true;1","023283e9-0027-4f28-a260-573eed6b1b62;true;2"},
			  {spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;1","970ae3ec-040a-498b-9171-5ff688a155b5;true;2,46520ed0-8204-4b0f-8754-a9d4dfa82ce0;true;8"},
		      {spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263;true;1,970ae3ec-040a-498b-9171-5ff688a155b5;true;2,46520ed0-8204-4b0f-8754-a9d4dfa82ce0;true;8,023283e9-0027-4f28-a260-573eed6b1b62;false;7,8d101229-d32e-4d7f-bb09-355e96824f32;true;3,d065d8a3-699d-4c14-834d-65953942e778;false;3,4ca1cc37-bdd4-4427-a23f-9d55d48b3f44;true;3,8c8ecbce-322f-4d6b-99bb-db7bdcbfa8dd;true;3","31dfe327-b9fe-432a-a119-24b584a85263;false;1"},
			  {spogServer.ReturnRandom("msp1_yuefen_group@spogqa.com"), sharePassword,"msp_admin", mspOrg_id,"46520ed0-8204-4b0f-8754-a9d4dfa82ce0;true;1,023283e9-0027-4f28-a260-573eed6b1b62;true;2","023283e9-0027-4f28-a260-573eed6b1b62;true;2"},
			  {spogServer.ReturnRandom("msp1_account_admin_yuefen_group@spogqa.com"), sharePassword,"msp_account_admin", mspOrg_id,"46520ed0-8204-4b0f-8754-a9d4dfa82ce0;true;1,023283e9-0027-4f28-a260-573eed6b1b62;true;2","023283e9-0027-4f28-a260-573eed6b1b62;true;2"},
			  {spogServer.ReturnRandom("account1_msp1_yuefen_group@spogqa.com"), sharePassword,"direct_admin", account_id,"8d101229-d32e-4d7f-bb09-355e96824f32;true;1","8d101229-d32e-4d7f-bb09-355e96824f32;true;1,d065d8a3-699d-4c14-834d-65953942e778;true;2,4ca1cc37-bdd4-4427-a23f-9d55d48b3f44;true;3,8c8ecbce-322f-4d6b-99bb-db7bdcbfa8dd;true;4"},
	          //root msp related
			  {spogServer.ReturnRandom("msp1_yuefen_group@spogqa.com"), sharePassword,"msp_admin", this.root_msp_org_id,"46520ed0-8204-4b0f-8754-a9d4dfa82ce0;true;1,023283e9-0027-4f28-a260-573eed6b1b62;true;2","023283e9-0027-4f28-a260-573eed6b1b62;true;2"},
			  {spogServer.ReturnRandom("msp1_account_admin_yuefen_group@spogqa.com"), sharePassword,"msp_account_admin",this.root_msp_org_id,"46520ed0-8204-4b0f-8754-a9d4dfa82ce0;true;1,023283e9-0027-4f28-a260-573eed6b1b62;true;2","023283e9-0027-4f28-a260-573eed6b1b62;true;2"},
			  {spogServer.ReturnRandom("msp1_yuefen_group@spogqa.com"), sharePassword,"msp_admin", this.sub_msp1_org_id,"46520ed0-8204-4b0f-8754-a9d4dfa82ce0;true;1,023283e9-0027-4f28-a260-573eed6b1b62;true;2","023283e9-0027-4f28-a260-573eed6b1b62;true;2"},
			  {spogServer.ReturnRandom("msp1_account_admin_yuefen_group@spogqa.com"), sharePassword,"msp_account_admin",this.sub_msp1_org_id,"46520ed0-8204-4b0f-8754-a9d4dfa82ce0;true;1,023283e9-0027-4f28-a260-573eed6b1b62;true;2","023283e9-0027-4f28-a260-573eed6b1b62;true;2"},
			  
			  {spogServer.ReturnRandom("account1_msp1_yuefen_group@spogqa.com"), sharePassword,"direct_admin", this.root_msp_direct_org_id,"8d101229-d32e-4d7f-bb09-355e96824f32;true;1","8d101229-d32e-4d7f-bb09-355e96824f32;true;1,d065d8a3-699d-4c14-834d-65953942e778;true;2,4ca1cc37-bdd4-4427-a23f-9d55d48b3f44;true;3,8c8ecbce-322f-4d6b-99bb-db7bdcbfa8dd;true;4"},
			  {spogServer.ReturnRandom("account1_msp1_yuefen_group@spogqa.com"), sharePassword,"direct_admin", this.sub_msp1_account1_id,"8d101229-d32e-4d7f-bb09-355e96824f32;true;1","8d101229-d32e-4d7f-bb09-355e96824f32;true;1,d065d8a3-699d-4c14-834d-65953942e778;true;2,4ca1cc37-bdd4-4427-a23f-9d55d48b3f44;true;3,8c8ecbce-322f-4d6b-99bb-db7bdcbfa8dd;true;4"},
		  };
		}
	  
	  @Test (dataProvider = "columnInfo") 
	  public void updateHypervisorColumnsSuccess(String name, String password, String role, String org, String specifiedHypervisorColumns,
			  String newSpecifiedHypervisorColumns){	 
		  System.out.println("updateHypervisorColumnsSuccess");
		  test = rep.startTest("updateHypervisorColumnsSuccess");
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
		  //update
		  ArrayList<HashMap<String, Object>> newColumnsList = source4SpogServer.getHypervisorColumnArrayList(newSpecifiedHypervisorColumns, token);
		  System.out.println("start update columns");
		  response = source4SpogServer.updateHypervisorColumnsForLoggedInUser(newColumnsList, token);
		  source4SpogServer.CompareHypervisorColumns(response, newColumnsList, test);
	      
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
          //update
		  System.out.println("start update columns");
		  ArrayList<HashMap<String, Object>> newColumnsList = new ArrayList<HashMap<String, Object>>();
		  HashMap<String, Object> newColumnInfo = source4SpogServer.getHypervisorColumnInfo(newColumn_id, newVisible, newOrder_id);
		  newColumnsList.add(newColumnInfo);
		  response = source4SpogServer.updateHypervisorColumnsForLoggedInUser(newColumnsList, token);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  
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
	  
	  //update
	  ArrayList<HashMap<String, Object>> newColumnsList = source4SpogServer.getHypervisorColumnArrayList(newSpecifiedHypervisorColumns, token);
	  response = source4SpogServer.updateHypervisorColumnsForLoggedInUser(newColumnsList, token);
	  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
	  
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
	  //update
	  ArrayList<HashMap<String, Object>> newColumnsList = source4SpogServer.getHypervisorColumnArrayList(newSpecifiedHypervisorColumns, token);
	  response = source4SpogServer.updateHypervisorColumnsForLoggedInUser(newColumnsList, token);
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
	  Response response = source4SpogServer.updateHypervisorColumnsForLoggedInUser(columnsList, token);
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
  public void updateHypervisorColumnsFail401(String name, String password, String role, String org, String specifiedHypervisorColumns,
		  int expectedStatuscode, String expectedErrorCode){	 
	  System.out.println("updateHypervisorColumnsFail401");
	  test = rep.startTest("updateHypervisorColumnsFail401");
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
	  
	  token = "";
	  response = source4SpogServer.updateHypervisorColumnsForLoggedInUser(columnsList, token);
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
