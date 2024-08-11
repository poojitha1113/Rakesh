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
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.assertTrue;

public class GetHypervisorColumnsForLoggedInUserTest extends base.prepare.PrepareOrgInfo{
	@Parameters({ "pmfKey"})
	  public GetHypervisorColumnsForLoggedInUserTest(String pmfKey) {
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
	  private String msp_account_admin;
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
			System.out.println("GetHypervisorColumnsForLoggedInUserTest");
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
		  rep = ExtentManager.getInstance("GetHypervisorColumnsForLoggedInUserTest",logFolder);
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
//		  //create direct user
//		  this.direct_email = spogServer.ReturnRandom("direct_yuefen_group@spogqa.com");
//		  spogServer.createUserAndCheck(direct_email, sharePassword, "yuefen", "liu", "direct_admin", directOrg_id, test);
//		  //create msp1 user
//		  this.msp_email = spogServer.ReturnRandom("msp1_yuefen_group@spogqa.com");
//		  spogServer.createUserAndCheck(msp_email, sharePassword, "yuefen", "liu", "msp_admin", mspOrg_id, test);
//		  //create account user
//		  this.accountDirect_email = spogServer.ReturnRandom("account1_msp1_yuefen_group@spogqa.com");
//		  spogServer.createUserAndCheck(accountDirect_email, sharePassword, "yuefen", "liu", "direct_admin", account_id, test);  
		  
		  prepare(baseURI, port, logFolder, this.csrGlobalLoginUser,  this.csrGlobalLoginPassword, this.getClass().getSimpleName() );
	  }

	  @DataProvider(name = "columnsInfo")
	  public final Object[][] getColumnsInfo() {
		  return new Object[][] { 
			  {spogServer.ReturnRandom("csr_yuefen_admin_aa@spogqa.com"), sharePassword,"csr_admin", csrOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263","true","2"},
			  {spogServer.ReturnRandom("csr_yuefen_readonly_aaa@spogqa.com"), sharePassword,"csr_read_only", csrOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263","true","2"},
			  {spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"direct_admin", directOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263","true","1"},
			  {spogServer.ReturnRandom("msp1_yuefen_group@spogqa.com"), sharePassword,"msp_admin", mspOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263","false","1"},
			  {spogServer.ReturnRandom("msp_account_admin_yuefen@spogqa.com"), sharePassword,"msp_account_admin", mspOrg_id,"31dfe327-b9fe-432a-a119-24b584a85263","false","1"},
			  {spogServer.ReturnRandom("account1_msp1_yuefen_group@spogqa.com"), sharePassword,"direct_admin", account_id,"31dfe327-b9fe-432a-a119-24b584a85263","true","1"},
			  
			  
			  {spogServer.ReturnRandom("msp1_yuefen_group@spogqa.com"), sharePassword,"msp_admin", this.root_msp_org_id,"31dfe327-b9fe-432a-a119-24b584a85263","false","1"},
			  {spogServer.ReturnRandom("msp_account_admin_yuefen@spogqa.com"), sharePassword,"msp_account_admin", this.root_msp_org_id,"31dfe327-b9fe-432a-a119-24b584a85263","false","1"},
			  {spogServer.ReturnRandom("msp1_yuefen_group@spogqa.com"), sharePassword,"msp_admin", this.sub_msp1_org_id,"31dfe327-b9fe-432a-a119-24b584a85263","false","1"},
			  {spogServer.ReturnRandom("msp_account_admin_yuefen@spogqa.com"), sharePassword,"msp_account_admin", this.sub_msp1_org_id,"31dfe327-b9fe-432a-a119-24b584a85263","false","1"},
			  {spogServer.ReturnRandom("account1_msp1_yuefen_group@spogqa.com"), sharePassword,"direct_admin", this.root_msp_direct_org_id,"31dfe327-b9fe-432a-a119-24b584a85263","true","1"},
			  {spogServer.ReturnRandom("account1_msp1_yuefen_group@spogqa.com"), sharePassword,"direct_admin", this.sub_msp1_account1_id,"31dfe327-b9fe-432a-a119-24b584a85263","true","1"}
			  
	          };
		}
	  
	  @Test (dataProvider = "columnsInfo") 
	  public void getHypervsiorColumnsCheck(String name, String password, String role, String org, String column_id,
			  String visible, String order_id){	 
		  System.out.println("getHypervsiorColumnsCheck");
		  test = rep.startTest("getHypervsiorColumnsCheck");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  String userId =spogServer.createUserAndCheck(name, password, "yuefen", "liu", role, org, test);
		  spogServer.userLogin(name, password, SpogConstants.SUCCESS_LOGIN, test);
		  
		  String token = spogServer.getJWTToken();
		  //create columns
		  System.out.println("create columns");
		  ArrayList<HashMap<String, Object>> columnsList = new ArrayList<HashMap<String, Object>>();
		  HashMap<String, Object> columnInfo = source4SpogServer.getHypervisorColumnInfo(column_id, visible, order_id);
		  columnsList.add(columnInfo); 
		  source4SpogServer.createHypervisorColumnsForLoggedInUser(columnsList, token);
		  //get logged in user columns
		  System.out.println("get columns");
		  Response response =source4SpogServer.getHypervisorColumnsForLoggedInUser(token);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  //check and compare
		  ArrayList<HashMap<String, Object>> responseColumnsList = response.then().extract().path("data");
		  HashMap<String, Object> responseColumnInfo = responseColumnsList.get(0);
		  assertEquals(responseColumnInfo.get("column_id").toString(), column_id.toString());
		  assertEquals(responseColumnInfo.get("visible").toString(), visible.toString());
		  assertEquals(responseColumnInfo.get("order_id").toString(), order_id.toString());
		  
		  //delete columns
		  response =source4SpogServer.deleteHypervisorColumnsForLoggedInUser(token);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  
		  //delete user
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  spogServer.CheckDeleteUserByIdStatus(userId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  }
	  
	  @DataProvider(name = "columnsInfo2")
	  public final Object[][] getColumnsInfo2() {
		  return new Object[][] { 
			  {spogServer.ReturnRandom("direct_yuefen_group@spogqa.com"), sharePassword,"direct_admin", directOrg_id}
			  
	          };
		}
	  
	  @Test (dataProvider = "columnsInfo2") 
	  public void getHypervsiorColumnWithoutCreate(String name, String password, String role, String org){	 
		  System.out.println("getHypervsiorColumnWithoutCreate");
		  test = rep.startTest("getHypervsiorColumnWithoutCreate");
		  test.assignAuthor("Liu Yuefen");
		  
		  //login
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  String userId =spogServer.createUserAndCheck(name, password, "yuefen", "liu", role, org, test);
		  spogServer.userLogin(name, password, SpogConstants.SUCCESS_LOGIN, test);
		  
		  String token = spogServer.getJWTToken();

		  //get columns
		  System.out.println("get columns");
		  Response response =source4SpogServer.getHypervisorColumnsForLoggedInUser(token);
		  spogServer.checkResponseStatus(response, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  //check and compare
		  ArrayList<HashMap<String, Object>> responseColumnsList = response.then().extract().path("data");
		  
		  response = source4SpogServer.getDefaultHypervisorColumns(token, test);
		  ArrayList<HashMap<String, Object>> expectColumnsList = response.then().extract().path("data");
		  for(int i=0;i<responseColumnsList.size();i++) {
			  HashMap<String, Object> responseColumnInfo = responseColumnsList.get(i);
			  HashMap<String, Object> expectColumnInfo = expectColumnsList.get(i);
			  if (responseColumnInfo.get("column_id").equals(expectColumnInfo.get("column_id"))&&
					  responseColumnInfo.get("visible").equals(expectColumnInfo.get("visible"))&&
					  responseColumnInfo.get("order_id").equals(expectColumnInfo.get("order_id"))) {
				  assertTrue("compare columns pass",true);
				  
			  }else {
				  assertTrue("compare columns fail",false);
			  }
				 
		  }
		  
		  //delete user
		  spogServer.userLogin(csrGlobalLoginUser, csrGlobalLoginPassword, SpogConstants.SUCCESS_LOGIN, test);
		  spogServer.CheckDeleteUserByIdStatus(userId, SpogConstants.SUCCESS_GET_PUT_DELETE, test);
		  }
	  
	  @Test 
	  public void HypervsiorNameCheckWithoutLogin401(){	 
		  System.out.println("HypervsiorNameCheck");
		  test = rep.startTest("HypervsiorNameCheck");
		  test.assignAuthor("Liu Yuefen");
		  
		  //set token
		  String token = "";
		  
		  //get the default columns
		  Response response = source4SpogServer.getHypervisorColumnsForLoggedInUser(token);
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
